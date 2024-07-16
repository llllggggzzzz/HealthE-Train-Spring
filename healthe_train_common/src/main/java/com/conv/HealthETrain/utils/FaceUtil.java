package com.conv.HealthETrain.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

public class FaceUtil {
    @Getter
    private static final String faceServerHost = "127.0.0.1";
    @Getter
    private static final Integer faceServerPort = 5000;

    private static final String faceStorePath = "";
    private static final String faceStoreType = ".jpg";

    // 人脸比对阙值
    @Getter
    private static final Double faceSimThreshold = 0.85;


    public static String getTargetFacePath(Long userId) {
        String targetFacePath = faceStorePath + "/" + userId.toString() + faceStoreType;
        if(FileUtil.exist(targetFacePath) && FileUtil.isFile(targetFacePath)) {
            return targetFacePath;
        } else  {
            return "";
        }
    }

    public static String getSaveSuffix() {
        return faceStoreType;
    }


    public static Double getFaceSim(String srcImgPath, String targetImgPath) {
        String getURL = UrlBuilder.create().setScheme("http")
                .setHost(faceServerHost)
                .setPort(faceServerPort)
                .addPath("/face")
                .addQuery("src_path", srcImgPath)
                .addQuery("target_path", targetImgPath)
                .build();
        HttpResponse execute = HttpUtil.createGet(getURL).execute();
        String body = execute.body();
        JSONObject bodyJson = JSONUtil.parseObj(body);
        if(bodyJson.containsKey("data")) {
            Double sim = bodyJson.getDouble("data");
            return sim;
        }
        return 0.0;
    }

    public static String faceExtract(String srcImgPath, String saveDirPath) {
        String getURL = UrlBuilder.create().setScheme("http")
                .setHost(faceServerHost)
                .setPort(faceServerPort)
                .addPath("/face").addPath("eq")
                .addQuery("src_path", srcImgPath)
                .addQuery("save_path", saveDirPath)
                .build();
        HttpResponse execute = HttpUtil.createGet(getURL).execute();
        JSONObject object = JSONUtil.parseObj(execute.body());
        if(object.containsKey("data")) {
            String data = object.getStr("data");
            return data;
        }
        return "";
    }

}
