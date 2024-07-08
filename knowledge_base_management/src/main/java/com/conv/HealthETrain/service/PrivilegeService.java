package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Privilege;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.parameters.P;

/**
* @author flora
* @description 针对表【privilege】的数据库操作Service
* @createDate 2024-07-08 14:30:51
*/
public interface PrivilegeService extends IService<Privilege> {
    Privilege findPrivilegeByPrivilegeId(Long id);

}
