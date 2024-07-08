package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Privilege;
import com.conv.HealthETrain.domain.UserNotePrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【user_note_privilege】的数据库操作Service
* @createDate 2024-07-08 13:12:09
*/
public interface UserNotePrivilegeService extends IService<UserNotePrivilege> {
    UserNotePrivilege findUserNotePrivilegeServiceByUserIdAndNoteId(Long userId, Long noteId);
    Boolean deleteListByNoteId(Long noteId);
    Boolean updatePrivilegeByNoteId(Long noteId, Long privilegeId);
    Boolean addUsersToNotePrivilege(Long noteId, Long privilegeId, List<Long> userIdList);
    List<UserNotePrivilege> findUserNotePrivilegeByNoteId(Long noteId);
    Boolean updateAndDeletePrivilegeByNoteIdAndUserIdList(Long noteId, Long privilegeId, List<Long> userIdList);


}
