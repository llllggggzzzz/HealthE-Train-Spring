package com.conv.HealthETrain.service.impl;

import com.conv.HealthETrain.domain.UserNotePrivilege;
import com.conv.HealthETrain.domain.UserRepositoryPrivilege;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.baomidou.mybatisplus.extension.toolkit.Db.saveBatch;
@Service
public class UserRepositoryPrivilegeTransactionalServiceImpl {
    @Transactional
    public boolean saveUserRepositoryPrivileges(List<UserRepositoryPrivilege> privileges) {
        return saveBatch(privileges);
    }
}
