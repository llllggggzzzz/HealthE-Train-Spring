package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.UserNotePrivilege;
import com.conv.HealthETrain.domain.UserRepositoryPrivilege;
import com.conv.HealthETrain.service.UserRepositoryPrivilegeService;
import com.conv.HealthETrain.mapper.UserRepositoryPrivilegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author flora
* @description 针对表【user_repository_privilege】的数据库操作Service实现
* @createDate 2024-07-07 11:51:31
*/
@Service
public class UserRepositoryPrivilegeServiceImpl extends ServiceImpl<UserRepositoryPrivilegeMapper, UserRepositoryPrivilege>
    implements UserRepositoryPrivilegeService{
    @Autowired
    private UserRepositoryPrivilegeMapper userRepositoryPrivilegeMapper;
    @Autowired
    private UserRepositoryPrivilegeTransactionalServiceImpl userRepositoryPrivilegeTransactionalService;

    @Override
    public UserRepositoryPrivilege findUserRepositoryPrivilegeByUserIdAndRepositoryid(Long userId, Long repositoryId) {
        QueryWrapper<UserRepositoryPrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("repository_id", repositoryId);
        return userRepositoryPrivilegeMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean deleteListByRepositoryId(Long repositoryId) {
        QueryWrapper<UserRepositoryPrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", repositoryId);
        return userRepositoryPrivilegeMapper.delete(queryWrapper) > 0;
    }

    @Override
    public Boolean updatePrivilegeByRepositoryId(Long repositoryId, Long privilegeId) {
        UpdateWrapper<UserRepositoryPrivilege> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("repository_id", repositoryId).set("privilege_id", privilegeId);
        return userRepositoryPrivilegeMapper.update(updateWrapper) > 0;
    }

    @Override
    public Boolean addUsersToRepositoryPrivilege(Long repositoryId, Long privilegeId, List<Long> userIdList) {
        // 查询已经存在权限表中的用户ID
        QueryWrapper<UserRepositoryPrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("repository_id", repositoryId);
        List<UserRepositoryPrivilege> existingPrivileges = userRepositoryPrivilegeMapper.selectList(queryWrapper);
        //获取存在的用户id
        List<Long> existingUserIds = existingPrivileges.stream()
                .map(UserRepositoryPrivilege::getUserId)
                .toList();

        // 过滤需要添加的新用户ID
        List<Long> newUserIds = userIdList.stream()
                .filter(userId -> !existingUserIds.contains(userId))
                .toList();

        // 批量插入新用户权限
        if (!newUserIds.isEmpty()) {
            List<UserRepositoryPrivilege> newPrivileges = newUserIds.stream()
                    .map(userId -> {
                        UserRepositoryPrivilege privilege = new UserRepositoryPrivilege();
                        privilege.setUserId(userId);
                        privilege.setRepositoryId(repositoryId);
                        privilege.setPrivilegeId(privilegeId);
                        return privilege;
                    }).collect(Collectors.toList());

            boolean result = userRepositoryPrivilegeTransactionalService.saveUserRepositoryPrivileges(newPrivileges);
            return result;
        }
        // 如果没有新用户需要插入，返回 true 表示操作成功
        return true;
    }

    @Override
    public List<UserRepositoryPrivilege> findUserRepositoryPrivilegeByRepositoryId(Long repositoryId) {
        QueryWrapper<UserRepositoryPrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("repository_id", repositoryId);
        return userRepositoryPrivilegeMapper.selectList(queryWrapper);
    }

    @Override
    public Boolean updateAndDeletePrivilegeByRepositoryIdAndUserIdList(Long repositoryId, Long privilegeId, List<Long> userIdList) {
        if (repositoryId == null || privilegeId == null || userIdList == null || userIdList.isEmpty()) {
            return false;
        }
        //更新操作
        UpdateWrapper<UserRepositoryPrivilege> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("repository_id", repositoryId)
                .in("user_id", userIdList);
        UserRepositoryPrivilege updateEntity = new UserRepositoryPrivilege();
        updateEntity.setPrivilegeId(privilegeId);
        boolean updateSuccess = userRepositoryPrivilegeMapper.update(updateEntity, updateWrapper) > 0;

        // 删除剩余用户的权限项
        QueryWrapper<UserRepositoryPrivilege> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("repository_id", repositoryId)
                .notIn("user_id", userIdList);

        int deleteCount = userRepositoryPrivilegeMapper.delete(deleteWrapper);

        return updateSuccess && deleteCount > 0;
    }

}




