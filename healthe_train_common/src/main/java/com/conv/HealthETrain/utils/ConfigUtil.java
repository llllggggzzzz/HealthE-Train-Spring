package com.conv.HealthETrain.utils;

import cn.hutool.core.util.StrUtil;
import com.conv.HealthETrain.enums.ExceptionCode;
import com.conv.HealthETrain.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The type Config.
 */
@Slf4j
public abstract class ConfigUtil {
    /**
     * The Properties.
     */
    static Properties properties;
    static {
        try (InputStream in = ConfigUtil.class.getResourceAsStream("/global_config.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Gets img save token.
     *
     * @return the img save token
     */
    public static String getImgSaveToken() {
        String value = properties.getProperty("image_token");
        if(StrUtil.isBlankOrUndefined(value)) {
            // 为空或者不存在
            log.error("图片token不存在, 可能无法上传图片");
            throw new GlobalException("图片token不存在, 可能无法上传图片", ExceptionCode.IMAGE_UPLOAD_ERROR);
        } else {
            return value;
        }
    }


    /**
     * Gets img save token.
     *
     * @return jellyfinKey
     */
    public static String getJellyfinApiKey() {
        String value = properties.getProperty("jellyfin_api_key");
        if(StrUtil.isBlankOrUndefined(value)) {
            // 为空或者不存在
            log.error("无法读取Jellyfin api_key");
            throw new GlobalException("无法读取Jellyfin api_key", ExceptionCode.JELLYFIN_INIT_ERROR);
        } else {
            return value;
        }
    }


    /**
     * Gets img save token.
     *
     * @return jellyfinKey
     */
    public static String getJellyfinCreateJson() {
        String value = properties.getProperty("jellyfin_create_json");
        if(StrUtil.isBlankOrUndefined(value)) {
            // 为空或者不存在
            log.error("无法读取Jellyfin 创建配置");
            throw new GlobalException("无法读取Jellyfin 创建配置", ExceptionCode.JELLYFIN_INIT_ERROR);
        } else {
            return value;
        }
    }

    /**
     * Gets img save repo.
     *
     * @return the img save repo
     */
    public static String getImgSaveRepo() {
        String value = properties.getProperty("image_repo");
        if(StrUtil.isBlankOrUndefined(value)) {
            log.error("未搜索到仓库配置");
            // 为空或者不存在
            throw new GlobalException("未搜索到仓库配置", ExceptionCode.IMAGE_UPLOAD_ERROR);
        } else {
            return value;
        }
    }

    public static String getImgSaveFolder() {
        String value = properties.getProperty("image_folder");
        if(StrUtil.isBlankOrUndefined(value)) {
            log.error("未配置图片存储位置");
            // 为空或者不存在
            throw new GlobalException("未配置图片存储位置", ExceptionCode.IMAGE_UPLOAD_ERROR);
        } else {
            return value;
        }
    }

}
