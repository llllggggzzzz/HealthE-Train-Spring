package com.conv.HealthETrain.utils;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.conv.HealthETrain.enums.ExceptionCode;
import com.conv.HealthETrain.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.internal.http2.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author liusg
 */
@Component
public class TokenUtil {

    private final JWTSigner jwtSigner;

    public TokenUtil(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    public String createToken(Long userId, Duration ttl) {
        // 1.生成jws
        return JWT .create()
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
            System.out.println("12----------");
            throw new GlobalException("无效的token", ExceptionCode.UNAUTHORIZED);
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            System.out.println("22----------");
            throw new GlobalException("无效的token", ExceptionCode.UNAUTHORIZED);
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            System.out.println("33----------");
            throw new GlobalException("token已经过期", ExceptionCode.UNAUTHORIZED);
        }
        // 4.数据格式校验
        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            // 数据为空
            System.out.println("43----------");
            throw new GlobalException("无效的token", ExceptionCode.UNAUTHORIZED);
        }

        // 5.数据解析
        try {
        } catch (RuntimeException e) {
            System.out.println("53----------");
            // 数据格式有误
            throw new GlobalException("无效的token", ExceptionCode.UNAUTHORIZED);
        }
        return Long.valueOf(userPayload.toString());
    }

    @Slf4j
    public static class ImageUploadHandler {
        public static String uploadToGitHub(String token, String repo, String pathPrefix, String filePath, String commitMessage) {// 获取当前时间戳
            long timestamp = System.currentTimeMillis() / 1000;
            // 生成文件路径
            String path = StrUtil.format("{}/{}.png", pathPrefix, timestamp);

            // GitHub API URL
            String url = StrUtil.format("https://api.github.com/repos/{}/contents/{}", repo, path);

            // 读取文件并进行 Base64 编码
            File file = FileUtil.file(filePath);
            byte[] fileContent = FileUtil.readBytes(file);
            String contentB64 = Base64Encoder.encode(fileContent);

            // 准备请求头和数据
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "token " + token);
            headers.put("Accept", "application/vnd.github.v3+json");

            Map<String, Object> data = new HashMap<>();
            data.put("message", commitMessage);
            data.put("content", contentB64);

            // 发送请求
            HttpResponse response = HttpRequest.put(url)
                    .addHeaders(headers)
                    .body(JSONUtil.toJsonStr(data))
                    .execute();

            // 检查响应
            if (response.getStatus() == 201) {
                System.out.println("上传成功！");
                JSONObject jsonResponse = JSONUtil.parseObj(response.body());
                return jsonResponse.getByPath("content.download_url", String.class);
            } else {
                System.out.println("上传失败，状态码：" + response.getStatus());
                System.out.println(response.body());
                return null;
            }
        }

        public static void main(String[] args) {
            // 设置参数
            String token = ConfigUtil.getImgSaveToken();
            String repo = ConfigUtil.getImgSaveRepo();
            String pathPrefix = ConfigUtil.getImgSaveFolder();
            String filePath = "/home/john/Desktop/Snipaste_2024-07-06_09-29-03.png";
            String commitMessage = "上传图片到图床";
            // 上传图片
            String imageUrl = uploadToGitHub(token, repo, pathPrefix, filePath, commitMessage);

            if (imageUrl != null) {
                log.info("图片链接：{}", imageUrl);
            }
        }
    }

    /**
     * The type Config.
     */
    @Slf4j
    public abstract static class ConfigUtil {
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
}
