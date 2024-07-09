package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.*;
import com.conv.HealthETrain.domain.DTO.NoteInfoDTO;
import com.conv.HealthETrain.domain.DTO.UserDTO;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.conv.HealthETrain.enums.ResponseCode.*;
import static com.conv.HealthETrain.enums.VisibilityCode.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/notePrivilege")
public class NotePrivilegeController {
    private final NoteLinkRepositoryService noteLinkRepositoryService;
    private final UserNotePrivilegeService userNotePrivilegeService;
    private final UserRepositoryPrivilegeService userRepositoryPrivilegeService;
    private final PrivilegeService privilegeService;
    private final NoteService noteService;
    private final InformationPortalClient informationPortalClient;

    /**
     * @Description: 查询用户的note权限
     * @Param:
     * @return: 返回privilege 的id,1为可读，2为读写，-1无权限
     * @Author: flora
     * @Date: 2024/7/8
     */
    @GetMapping("/{userId}/{noteId}")
    public ApiResponse<Long> getNotePrivilegeByUserIdAndNoteId(@PathVariable Long userId, @PathVariable Long noteId) {
        //如果是自己的笔记
        Note note = noteService.findNoteByNoteId(noteId);
        Long queryUserId = note.getUserId();
        if (queryUserId.equals(userId)) {
            return ApiResponse.success(2L);
        } else {
            //不是自己的
            UserNotePrivilege userNotePrivilege =
                    userNotePrivilegeService.findUserNotePrivilegeServiceByUserIdAndNoteId(userId, noteId);
            if (userNotePrivilege != null) {
                //找到note的对应用户权限
                log.info("找到note" + noteId + "对于用户" + userId + " 权限为" + userNotePrivilege.getPrivilegeId());
                return ApiResponse.success(userNotePrivilege.getPrivilegeId());
            } else {
                //没找到，查询下一级知识库的权限
                //1.查询笔记对应的知识库
                NoteLinkRepository noteLinkRepository = noteLinkRepositoryService.findNoteLinkRepositoryByNoteId(noteId);
                Long repositoryId = noteLinkRepository.getRepositoryId();
                //2.查询知识库权限
                UserRepositoryPrivilege userRepositoryPrivilege =
                        userRepositoryPrivilegeService.findUserRepositoryPrivilegeByUserIdAndRepositoryid(userId, repositoryId);
                if (userRepositoryPrivilege != null) {
                    log.info("找到note" + noteId + "对于用户" + userId + " 权限为" + userRepositoryPrivilege.getPrivilegeId());
                    return ApiResponse.success(userRepositoryPrivilege.getPrivilegeId());
                } else {
                    log.info("找到note" + noteId + "对于用户" + userId + " 权限为无权限 -1");
                    return ApiResponse.success(-1L);
                }
            }
        }

    }

    /**
     * @Description: 修改用户userId自己笔记的全局权限,
     * 先验证visibility后验证privilegeId,可以设置全局私有、可读、读写
     * visibility：0 私有 1 局部公开 2 全局公开
     * privilege 的id,1为可读，2为读写，-1无权限
     * @Param:
     * @return:
     * @Author: flora
     * @Date: 2024/7/8
     */
    @PutMapping("/editAll/{noteId}/{visibility}/{privilegeId}")
    public ApiResponse<Boolean> updateNotePrivilegeToAll(
            @PathVariable Long noteId,
            @PathVariable int visibility,
            @PathVariable Long privilegeId) {
        // 首先确认是当前的所有者（前端处理）
        // 1.设置visibility
        if (visibility == V_PRIVATE.getCode()) {
            //如果设置笔记权限为私有
            Boolean isSuccess = noteService.updateNoteVisibility(noteId, visibility);
            //删除权限表里面此笔记关联的用户
            if (isSuccess) {
                Boolean isDelete = userNotePrivilegeService.deleteListByNoteId(noteId);
                if (isDelete) {
                    log.info("设置笔记" + noteId + "为私有成功");
                    return ApiResponse.success(true);
                } else {
                    log.info("没有用户");
                    return ApiResponse.success(NO_CONTENT);
                }
            } else {
                log.error("设置笔记权限1失败" + noteId);
                return ApiResponse.error(NOT_MODIFIED);
            }
        } else {
            //设置visibility为公开
            Boolean isVisibilitySuccess = noteService.updateNoteVisibility(noteId, V_FULLPUBLIC.getCode());
            if (!isVisibilitySuccess) {
                log.error("设置笔记权限2失败" + noteId);
                return ApiResponse.error(NOT_MODIFIED);
            } else {
                // 2.添加全部用户到link表里面
                //修改当前存在在权限表的用户权限
                Boolean isAvailable = userNotePrivilegeService.updatePrivilegeByNoteId(noteId, privilegeId);
                //添加其他用户进入
                //从信息门户模块获取全部用户
                List<User> userList = informationPortalClient.getAllUsers();
                List<Long> userIdList = userList.stream().map(User::getUserId).toList();
                //修改剩余的权限
                Boolean isEdited = userNotePrivilegeService.addUsersToNotePrivilege(noteId, privilegeId, userIdList);
                if (isEdited) {
                    log.info("修改文档权限为全局" + privilegeId + "成功");
                    return ApiResponse.success(true);
                } else {
                    log.info("修改文档权限为全局" + privilegeId + "失败");
                    //todo 这里如果影响的行就是空呢？
                    return ApiResponse.error(NOT_MODIFIED);
                }
            }
        }
    }

    /**
     * @Description: 修改权限给部分用户，包含分享可读、读写
     * @Param:
     * @return:
     * @Author: flora
     * @Date: 2024/7/8
     */
    @PutMapping("/editUserList/{noteId}/{privilegeId}")
    public ApiResponse<Boolean> editUserListPrivilege(
            @PathVariable Long noteId,
            @PathVariable Long privilegeId,
            @RequestBody List<UserDTO> partUserList) {
        //todo 后期修改为传userId @RequestBody List<User> userList
        //设置visibility为局部公开
        Boolean isVisibilitySuccess = noteService.updateNoteVisibility(noteId, V_PARTPUBLIC.getCode());
        if (!isVisibilitySuccess) {
            log.error("设置笔记权限失败" + noteId);
            return ApiResponse.error(NOT_MODIFIED);
        } else {
            // 2.添加部分用户到link表里面
            List<Long> userIdList = partUserList.stream().map(UserDTO::getUserId).toList();
            //修改存在在userIdList的权限，剩余的删除
            Boolean isAvailable = userNotePrivilegeService.
                    updateAndDeletePrivilegeByNoteIdAndUserIdList(noteId, privilegeId, userIdList);
            // 添加剩余用户进入
            Boolean isEdited = userNotePrivilegeService.addUsersToNotePrivilege(noteId, privilegeId, userIdList);
            if (isEdited) {
                log.info("修改文档权限为部分用户" + privilegeId + "成功");
                return ApiResponse.success(true);
            } else {
                log.info("修改文档权限为剩余用户" + privilegeId + "失败或者没有剩余用户");
                return ApiResponse.error(NOT_MODIFIED);
            }
        }
    }
    /**
    * @Description: 获取共享空间的笔记
     * 共享空间需要首先获取自己note里面visibility为1 的笔记
     * 其次还要获取user_note_privilege里面userId为自己的noteID
    * @Param:
    * @return: 
    * @Author: flora
    * @Date: 2024/7/10
    */
    @GetMapping("/shareRoom/{userId}")
    public ApiResponse<List<NoteInfoDTO>> getShareRoomNoteList(@PathVariable Long userId){
        List<Note> myPartSharedNoteList = noteService.getMySharedNoteList(userId);
        List<UserNotePrivilege> extraList = userNotePrivilegeService.findUserNotePrivilegeByUserId(userId);
        List<Long> noteIdList = extraList.stream().map(UserNotePrivilege::getNoteId).toList();
        List<Note> extraNoteList = noteService.findNoteListByNoteIdList(noteIdList);
        myPartSharedNoteList.addAll(extraNoteList);
        List<NoteInfoDTO> noteInfoDTOList = new ArrayList<>();
        for(Note note: myPartSharedNoteList){
            String username = informationPortalClient.getUser(note.getUserId()).getUsername();
            String cover = informationPortalClient.getUser(note.getUserId()).getCover();
            NoteInfoDTO noteInfoDTO = new NoteInfoDTO(note, username, cover);
            noteInfoDTOList.add(noteInfoDTO);
        }
        if(noteInfoDTOList != null){
            log.info("获取" + userId + "的共享空间成功！");
            return ApiResponse.success(noteInfoDTOList);
        }else{
            log.info("获取失败");
            return ApiResponse.error(NOT_FOUND);
        }
    }
}
