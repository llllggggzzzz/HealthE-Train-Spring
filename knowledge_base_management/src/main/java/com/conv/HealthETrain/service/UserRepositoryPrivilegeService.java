package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.UserNotePrivilege;
import com.conv.HealthETrain.domain.UserRepositoryPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【user_repository_privilege】的数据库操作Service
* @createDate 2024-07-07 11:51:31
*/
public interface UserRepositoryPrivilegeService extends IService<UserRepositoryPrivilege> {
    UserRepositoryPrivilege findUserRepositoryPrivilegeByUserIdAndRepositoryid(Long userId, Long repositoryId);
    Boolean deleteListByRepositoryId(Long repositoryId);
    Boolean updatePrivilegeByRepositoryId(Long repositoryId, Long privilegeId);
    Boolean addUsersToRepositoryPrivilege(Long repositoryId, Long privilegeId, List<Long> userIdList);
    List<UserRepositoryPrivilege> findUserRepositoryPrivilegeByRepositoryId(Long repositoryId);
    Boolean updateAndDeletePrivilegeByRepositoryIdAndUserIdList(Long repositoryId, Long privilegeId, List<Long> userIdList);
}
