package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.UserNotePrivilege;
import com.conv.HealthETrain.mapper.UserNotePrivilegeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserNotePrivilegeTransactionalServiceImpl extends ServiceImpl<UserNotePrivilegeMapper, UserNotePrivilege> {

    @Transactional
    public boolean saveUserNotePrivileges(List<UserNotePrivilege> privileges) {
        return saveBatch(privileges);
    }
}
