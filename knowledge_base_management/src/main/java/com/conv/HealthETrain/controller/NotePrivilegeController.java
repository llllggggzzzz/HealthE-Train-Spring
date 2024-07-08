package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.client.UserClient;
import com.conv.HealthETrain.domain.*;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.conv.HealthETrain.enums.ResponseCode.NOT_FOUND;
import static com.conv.HealthETrain.enums.ResponseCode.NOT_MODIFIED;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/notePrivilege")
public class NotePrivilegeController {
    private final NoteLinkRepositoryService noteLinkRepositoryService;
    private final UserNotePrivilegeService userNotePrivilegeService;
    private final UserRepositoryPrivilegeService userRepositoryPrivilegeService;
    private final PrivilegeService privilegeService;
    private final NoteService noteService;
    private final UserClient userClient;

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
     * @Description: 修改用户userId自己笔记的全局权限, 先验证visibility后验证privilegeId,可以设置全局私有、可读、读写
     * @Param:
     * @return:
     * @Author: flora
     * @Date: 2024/7/8
     */
    @PutMapping("/editAll/{noteId}/{visibility}/{privilegeId}}")
    public ApiResponse<Boolean> updateNotePrivilegeToAll(
            @PathVariable Long noteId,
            @PathVariable int visibility,
            @PathVariable Long privilegeId) {
        // 首先确认是当前的所有者（前端处理）
        // 1.设置visibility
        if (visibility == 0) {
            //如果设置笔记权限为私有
            Boolean isSuccess = noteService.updateNoteVisibility(noteId, visibility);
            //删除权限表里面此笔记关联的用户
            if (isSuccess) {
                Boolean isDelete = userNotePrivilegeService.deleteListByNoteId(noteId);
                if (isDelete) {
                    log.info("设置笔记" + noteId + "为私有成功");
                    return ApiResponse.success(true);
                } else {
                    log.info("删除权限列表用户失败或者没有用户");
                    return ApiResponse.error(NOT_FOUND);
                }
            } else {
                log.error("设置笔记权限失败" + noteId);
                return ApiResponse.error(NOT_MODIFIED);
            }
        } else {
            //设置visibility为公开
            Boolean isVisibilitySuccess = noteService.updateNoteVisibility(noteId, visibility);
            if (isVisibilitySuccess) {
                log.error("设置笔记权限失败" + noteId);
                return ApiResponse.error(NOT_MODIFIED);
            } else {
                // 2.添加全部用户到link表里面
                //修改当前存在在权限表的用户权限
                Boolean isAvailable = userNotePrivilegeService.updatePrivilegeByNoteId(noteId, privilegeId);
                if (isAvailable) {
                    //添加其他用户进入
                    //从信息门户模块获取全部用户
                    List<User> userList = userClient.getAllUsers();
                    List<Long> userIdList = userList.stream().map(User::getUserId).toList();
                    //修改剩余的权限
                    Boolean isEdited = userNotePrivilegeService.addUsersToNotePrivilege(noteId, privilegeId, userIdList);
                    if(isEdited){
                        log.info("修改文档权限为全局" + privilegeId + "成功");
                        return ApiResponse.success(true);
                    }else{
                        log.info("修改文档权限为全局" + privilegeId + "失败");
                        //todo 这里如果影响的行就是空呢？
                        return ApiResponse.error(NOT_MODIFIED);
                    }
                } else {
                    log.info("设置note" + noteId + "全局权限为" + privilegeId + "失败");
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
            @RequestBody List<User> userList){
        //设置visibility为公开
        Boolean isVisibilitySuccess = noteService.updateNoteVisibility(noteId, 1);
        if (isVisibilitySuccess) {
            log.error("设置笔记权限失败" + noteId);
            return ApiResponse.error(NOT_MODIFIED);
        } else {
            // 2.添加全部用户到link表里面
            //修改当前存在在权限表的用户权限
            List<UserNotePrivilege> userNotePrivileges = userNotePrivilegeService.findUserNotePrivilegeByNoteId(noteId);
            List<Long> existUserIdList = userNotePrivileges.stream().map(UserNotePrivilege::getUserId).toList();
            //修改存在在existUserIdList的权限，剩余的删除
            Boolean isAvailable = userNotePrivilegeService.
                    updateAndDeletePrivilegeByNoteIdAndUserIdList(noteId, privilegeId, existUserIdList);
            if (isAvailable) {
                //添加其他用户进入
                List<Long> userIdList = userList.stream().map(User::getUserId).toList();
                //修改剩余的权限
                Boolean isEdited = userNotePrivilegeService.addUsersToNotePrivilege(noteId, privilegeId, userIdList);
                if(isEdited){
                    log.info("修改文档权限为部分用户" + privilegeId + "成功");
                    return ApiResponse.success(true);
                }else{
                    log.info("修改文档权限为部分用户" + privilegeId + "失败");
                    return ApiResponse.error(NOT_MODIFIED);
                }
            } else {
                log.info("修改文档权限为部分用户" + privilegeId + "失败");
                return ApiResponse.error(NOT_MODIFIED);
            }
        }
    }
}
