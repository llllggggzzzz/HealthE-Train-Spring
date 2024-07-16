package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.domain.UserRepositoryPrivilege;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.RepositoryService;
import com.conv.HealthETrain.service.UserRepositoryPrivilegeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.conv.HealthETrain.enums.ResponseCode.*;
import static com.conv.HealthETrain.enums.VisibilityCode.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/repositoryPrivilege")
public class RepositoryPrivilegeController {

    private final UserRepositoryPrivilegeService userRepositoryPrivilegeService;
    private final RepositoryService repositoryService;
    private final InformationPortalClient informationPortalClient;

    /**
     * @Description: 查找知识库对于用户userId的权限
     * @Param:
     * @return: 返回privilege 的id,-1为无权限，1为可读，2为读写
     * @Author: flora
     * @Date: 2024/7/8
     */
    @GetMapping("/{userId}/{repositoryId}")
    public ApiResponse<Long> getRepositoryPrivilegeByUserIdAndRepositoryId
    (@PathVariable Long userId, @PathVariable Long repositoryId) {
        UserRepositoryPrivilege userRepositoryPrivilege = userRepositoryPrivilegeService.findUserRepositoryPrivilegeByUserIdAndRepositoryid(userId, repositoryId);
        if (userRepositoryPrivilege != null) {
            //找到note的对应用户权限
            log.info("找到repository" + repositoryId + "对于用户" + userId + " 权限为" + userRepositoryPrivilege.getPrivilegeId());
            return ApiResponse.success(userRepositoryPrivilege.getPrivilegeId());
        } else {
            //没找到，设置为无权限
            log.info("找到repository" + repositoryId + "对于用户" + userId + " 权限为无权限");
            return ApiResponse.success(-1L);
        }
    }

    /**
     * @Description: 修改用户userId自己知识库的全局权限, 先验证visibility后验证privilegeId,可以设置全局私有、可读、读写
     * @Param:
     * @return:
     * @Author: flora
     * @Date: 2024/7/8
     */
    @PutMapping("/editAll/{repositoryId}/{visibility}/{privilegeId}")
    public ApiResponse<Boolean> updateNotePrivilegeToAll(
            @PathVariable Long repositoryId,
            @PathVariable int visibility,
            @PathVariable Long privilegeId) {
        // 首先确认是当前的所有者（前端处理）
        // 1.设置visibility
        if (visibility == V_PRIVATE.getCode()) {
            //如果设置笔记权限为私有
            Boolean isSuccess = repositoryService.updateRepositoryVisibility(repositoryId, visibility);
            //删除权限表里面此笔记关联的用户
            if (isSuccess) {
                Boolean isDelete = userRepositoryPrivilegeService.deleteListByRepositoryId(repositoryId);
                if (isDelete) {
                    log.info("设置知识库" + repositoryId + "为私有成功");
                    return ApiResponse.success(true);
                } else {
                    log.info("没有用户");
                    return ApiResponse.success(NO_CONTENT);
                }
            } else {
                log.error("设置知识库权限1失败" + repositoryId);
                return ApiResponse.error(NOT_MODIFIED);
            }
        } else {
            //设置visibility为公开
            Boolean isVisibilitySuccess = repositoryService.updateRepositoryVisibility(repositoryId, V_FULLPUBLIC.getCode());
            if (!isVisibilitySuccess) {
                log.error("设置知识库权限2失败" + repositoryId);
                return ApiResponse.error(NOT_MODIFIED);
            } else {
                // 2.添加全部用户到link表里面
                //修改当前存在在权限表的用户权限
                Boolean isAvailable = userRepositoryPrivilegeService.updatePrivilegeByRepositoryId(repositoryId, privilegeId);
                //添加其他用户进入
                //从信息门户模块获取全部用户
                List<User> userList = informationPortalClient.getAllUsers();
                List<Long> userIdList = userList.stream().map(User::getUserId).toList();
                //修改剩余的权限
                Boolean isEdited = userRepositoryPrivilegeService.addUsersToRepositoryPrivilege(repositoryId, privilegeId, userIdList);
                if (isEdited) {
                    log.info("修改知识库权限为全局" + privilegeId + "成功");
                    return ApiResponse.success(true);
                } else {
                    log.info("修改知识库权限为全局" + privilegeId + "失败");
                    return ApiResponse.error(NOT_MODIFIED);
                }
            }
        }
    }

    /**
     * @Description: 修改知识库权限给部分用户，包含分享可读、读写
     * @Param:
     * @return:
     * @Author: flora
     * @Date: 2024/7/8
     */
    @PutMapping("/editUserList/{repositoryId}/{privilegeId}")
    public ApiResponse<Boolean> editUserListPrivilege(
            @PathVariable Long repositoryId,
            @PathVariable Long privilegeId,
            @RequestBody List<User> userList) {
        //设置visibility为公开
        Boolean isVisibilitySuccess = repositoryService.updateRepositoryVisibility(repositoryId, V_PARTPUBLIC.getCode());
        if (!isVisibilitySuccess) {
            log.error("设置知识库权限失败" + repositoryId);
            return ApiResponse.error(NOT_MODIFIED);
        } else {
            // 2.添加全部用户到link表里面
            List<Long> userIdList = userList.stream().map(User::getUserId).toList();
            //修改存在在existUserIdList的权限，剩余的删除
            Boolean isAvailable = userRepositoryPrivilegeService.
                    updateAndDeletePrivilegeByRepositoryIdAndUserIdList(repositoryId, privilegeId, userIdList);
            //修改剩余的权限
            Boolean isEdited = userRepositoryPrivilegeService.addUsersToRepositoryPrivilege(repositoryId, privilegeId, userIdList);
            if (isEdited) {
                log.info("修改知识库权限为部分用户" + privilegeId + "成功");
                return ApiResponse.success(true);
            } else {
                log.info("修改知识库权限为部分用户" + privilegeId + "失败");
                return ApiResponse.error(NOT_MODIFIED);
            }
        }
    }


}
