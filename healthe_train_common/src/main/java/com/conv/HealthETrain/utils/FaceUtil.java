package com.conv.HealthETrain.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.conv.HealthETrain.factory.JellyfinFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FaceUtil {
    @Getter
    private static final String faceServerHost = "127.0.0.1";
    @Getter
    private static final Integer faceServerPort = 5000;

    @Getter
    private static final String faceStoreType = ".jpg";

    // 人脸比对阙值
    @Getter
    private static final Double faceSimThreshold = 0.7;


    public static String getTargetFacePath(Long userId) {
        // 根据用户id去查询jellyfin
        JellyfinUtil jellyfinUtil = JellyfinFactory.build(JellyfinFactory.configPath);
        return jellyfinUtil.findImagePath(userId.toString(), "face", "");
    }

    public static String getSaveSuffix() {
        return faceStoreType;
    }


    public static Double getFaceSim(String srcImgPath, String targetImgPath) {
        String getURL = UrlBuilder.create().setScheme("http")
                .setHost(faceServerHost)
                .setPort(faceServerPort)
                .addPath("/face").addPath("eq")
                .addQuery("src_path", srcImgPath)
                .addQuery("target_path", targetImgPath)
                .build();
        HttpResponse execute = HttpUtil.createGet(getURL).execute();
        String body = execute.body();
        log.info("进行人脸比对返回 {}" , body);
        JSONObject bodyJson = JSONUtil.parseObj(body);
        if(bodyJson.containsKey("data")) {
            Double sim = bodyJson.getDouble("data");
            return sim;
        }
        return 0.0;
    }

    public static Boolean faceExtract(String srcImgPath, String saveDirPath) {
        String getURL = UrlBuilder.create().setScheme("http")
                .setHost(faceServerHost)
                .setPort(faceServerPort)
                .addPath("/face")
                .addQuery("src_path", srcImgPath)
                .addQuery("save_path", saveDirPath)
                .build();
        HttpResponse execute = HttpUtil.createGet(getURL).execute();
        JSONObject object = JSONUtil.parseObj(execute.body());
        log.info("人脸提取返回 {}", object);
        if(object.containsKey("data")) {
            Boolean data = object.getBool("data");
            return data;
        }
        return false;
    }

}
