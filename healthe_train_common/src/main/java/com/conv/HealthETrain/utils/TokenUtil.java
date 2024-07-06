package com.conv.HealthETrain.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.conv.HealthETrain.enums.ExceptionCode;
import com.conv.HealthETrain.exception.GlobalException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

/**
 * @author liusg
 */
@Component
public class TokenUtil {

    private final JWTSigner jwtSigner;

    public TokenUtil(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    public String getToken(Long userId, Duration ttl) {
        // 1.生成jws
        return JWT.create()
                .setPayload("user", userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }

    public Long parseToken(String token) {
        // 1.校验token是否为空
        if (token == null) {
            throw new GlobalException("未登录", ExceptionCode.UNAUTHORIZED);
        }
        // 2.校验并解析jwt
        JWT jwt = null;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new GlobalException("无效的token", ExceptionCode.UNAUTHORIZED);
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            throw new GlobalException("无效的token", ExceptionCode.UNAUTHORIZED);
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new GlobalException("token已经过期", ExceptionCode.UNAUTHORIZED);
        }
        // 4.数据格式校验
        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            // 数据为空
            throw new GlobalException("无效的token", ExceptionCode.UNAUTHORIZED);
        }

        // 5.数据解析
        try {
        } catch (RuntimeException e) {
            // 数据格式有误
//            throw new UnauthorizedException("无效的token");
        }
        return Long.valueOf(userPayload.toString());
    }

}
