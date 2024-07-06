package com.conv.HealthETrain.config;

import cn.hutool.core.util.StrUtil;
import com.conv.HealthETrain.enums.ExceptionCode;
import com.conv.HealthETrain.exception.GlobalException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Config {
    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("global_config.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String getImgSaveToken() {
        String value = properties.getProperty("image_token");
        if(StrUtil.isBlankOrUndefined(value)) {
            // 为空或者不存在
            throw new GlobalException("图片token不存在, 可能无法上传图片", ExceptionCode.IMAGE_TOKEN_EMPTY);
        } else {
            return value;
        }

    }

}
