package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.UserNotePrivilege;
import com.conv.HealthETrain.service.UserNotePrivilegeService;
import com.conv.HealthETrain.mapper.UserNotePrivilegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author flora
 * @description 针对表【user_note_privilege】的数据库操作Service实现
 * @createDate 2024-07-08 13:12:09
 */
@Service
public class UserNotePrivilegeServiceImpl extends ServiceImpl<UserNotePrivilegeMapper, UserNotePrivilege>
        implements UserNotePrivilegeService {

    @Autowired
    private UserNotePrivilegeMapper userNotePrivilegeMapper;
    @Autowired
    private UserNotePrivilegeTransactionalServiceImpl userNotePrivilegeTransactionalService;


    @Override
    public UserNotePrivilege findUserNotePrivilegeServiceByUserIdAndNoteId(Long userId, Long noteId) {
        QueryWrapper<UserNotePrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("note_id", noteId);
        return userNotePrivilegeMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean deleteListByNoteId(Long noteId) {
        QueryWrapper<UserNotePrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId);
        return userNotePrivilegeMapper.delete(queryWrapper) > 0;
    }

    @Override
    public Boolean updatePrivilegeByNoteId(Long noteId, Long privilegeId) {
        UpdateWrapper<UserNotePrivilege> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("note_id", noteId).set("privilege_id", privilegeId);
        return userNotePrivilegeMapper.update(updateWrapper) > 0;
    }

    @Override
    public Boolean addUsersToNotePrivilege(Long noteId, Long privilegeId, List<Long> userIdList) {
        // 查询已经存在权限表中的用户ID
        QueryWrapper<UserNotePrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId);
        List<UserNotePrivilege> existingPrivileges = userNotePrivilegeMapper.selectList(queryWrapper);
        //获取存在的用户id
        List<Long> existingUserIds = existingPrivileges.stream()
                .map(UserNotePrivilege::getUserId)
                .toList();

        // 过滤需要添加的新用户ID
        List<Long> newUserIds = userIdList.stream()
                .filter(userId -> !existingUserIds.contains(userId))
                .toList();

        // 批量插入新用户权限
        if (!newUserIds.isEmpty()) {
            List<UserNotePrivilege> newPrivileges = newUserIds.stream()
                    .map(userId -> {
                        UserNotePrivilege privilege = new UserNotePrivilege();
                        privilege.setUserId(userId);
                        privilege.setNoteId(noteId);
                        privilege.setPrivilegeId(privilegeId);
                        return privilege;
                    }).collect(Collectors.toList());

            boolean result = userNotePrivilegeTransactionalService.saveUserNotePrivileges(newPrivileges);
            return result;
        }
        // 如果没有新用户需要插入，返回 true 表示操作成功
        return true;
    }

    @Override
    public List<UserNotePrivilege> findUserNotePrivilegeByNoteId(Long noteId) {
        QueryWrapper<UserNotePrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId);
        return userNotePrivilegeMapper.selectList(queryWrapper);
    }

    /**
    * @Description: 更新指定用户权限，删除剩余原先存在的用户
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/8
    */
    @Override
    public Boolean updateAndDeletePrivilegeByNoteIdAndUserIdList(Long noteId, Long privilegeId, List<Long> userIdList) {
        if (noteId == null || privilegeId == null || userIdList == null || userIdList.isEmpty()) {
            return false;
        }
        //更新操作
        UpdateWrapper<UserNotePrivilege> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("note_id", noteId)
                .in("user_id", userIdList);
        UserNotePrivilege updateEntity = new UserNotePrivilege();
        updateEntity.setPrivilegeId(privilegeId);
        boolean updateSuccess = userNotePrivilegeMapper.update(updateEntity, updateWrapper) > 0;

        // 删除剩余用户的权限项
        QueryWrapper<UserNotePrivilege> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("note_id", noteId)
                .notIn("user_id", userIdList);

        int deleteCount = userNotePrivilegeMapper.delete(deleteWrapper);

        return updateSuccess && deleteCount > 0;
    }

    @Override
    public List<UserNotePrivilege> findUserNotePrivilegeByUserId(Long userId) {
        QueryWrapper<UserNotePrivilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userNotePrivilegeMapper.selectList(queryWrapper);
    }
}




