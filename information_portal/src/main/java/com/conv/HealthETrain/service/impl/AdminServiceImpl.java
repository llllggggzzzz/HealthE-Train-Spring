package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.config.JwtProperties;
import com.conv.HealthETrain.domain.Admin;
import com.conv.HealthETrain.enums.ExceptionCode;
import com.conv.HealthETrain.exception.GlobalException;
import com.conv.HealthETrain.service.AdminService;
import com.conv.HealthETrain.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.conv.HealthETrain.utils.TokenUtil;

/**
* @author john
* @description 针对表【admin】的数据库操作Service实现
* @createDate 2024-07-05 17:56:52
*/
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService{
    private  AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;
    private final JwtProperties jwtProperties;
    @Override
    public String loginByAccountForAdmin(Admin loginAdmin) {
        Admin admin = lambdaQuery().eq(Admin::getAccount, loginAdmin.getAccount()).one();
        if (admin == null) {
            throw new GlobalException("用户不存在", ExceptionCode.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(loginAdmin.getPassword(), loginAdmin.getPassword())) {
            throw new GlobalException("密码错误", ExceptionCode.BAD_REQUEST);
        }

        return tokenUtil.createToken(Long.valueOf(admin.getAdminId()), jwtProperties.getTokenTTL());
    }
}




