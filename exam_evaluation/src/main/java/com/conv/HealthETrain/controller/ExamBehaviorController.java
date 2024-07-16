package com.conv.HealthETrain.controller;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.factory.JellyfinFactory;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.utils.FaceUtil;
import com.conv.HealthETrain.utils.JellyfinUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/exam/behavior")
@Slf4j
@AllArgsConstructor
public class ExamBehaviorController {

    private final InformationPortalClient informationPortalClient;

    private static final String behaviorMediaLibraryName = "userBehavior";
    private static final String saveType = ".jpg";
    private static final String jelleyfinAddress = "/home/john/env/server/jellyfin";
    /**
     * @description 根据用户行为图片来进行判断
     * @param userId 用户ID
     * @param examId 考试ID
     * @param multipartFile 用户行为图片
     * @return 返回用户是否出现异常行为: true -> 出现
     */
    @PostMapping
    public ApiResponse<Boolean> detectUserBehavior(@RequestParam("userId") Long userId,
                                                   @RequestParam("examId") Long examId,
                                                   @RequestParam("behaviorImage") MultipartFile multipartFile,
                                                   @RequestParam("time") Date time) throws IOException {

        //  记录原文件, 调用异常检测模型, 记录异常信息, 记录异常图片
        
        // 1. 创建此考试的媒体库
        JellyfinUtil jellyfinUtil = JellyfinFactory.build(jelleyfinAddress);
        List<String> mediaNameLibraries = jellyfinUtil.getAllMediaLibrary("");
        if(!mediaNameLibraries.contains(examId.toString())) {
            jellyfinUtil.createMediaLibrary(behaviorMediaLibraryName+examId.toString(), behaviorMediaLibraryName+examId.toString(), "");
        }
        if(!mediaNameLibraries.contains(examId.toString() + "-behavior")) {
            jellyfinUtil.createMediaLibrary(behaviorMediaLibraryName+examId.toString()+"-behavior", behaviorMediaLibraryName+examId.toString()+"-behavior", "");
        }
        // 2. 上传用户照片
        String fileName = userId.toString() + time + saveType;
        jellyfinUtil.saveFile(multipartFile.getBytes(), fileName, examId.toString(), true);

        // 创建临时文件
        Files.createTempFile(fileName, saveType);
        // 写入文件
        String tempPath = fileName + saveType;
        FileUtil.writeBytes(multipartFile.getBytes(), tempPath);

        // 调用异常检测函数
        String getUrl = UrlBuilder.create()
                .setScheme("http")
                .setHost(FaceUtil.getFaceServerHost())
                .setPort(FaceUtil.getFaceServerPort())
                .addPath("/behavior").addQuery("image_path", tempPath)
                .build();

        HttpResponse response = HttpUtil.createGet(getUrl).execute();
        JSONObject jsonObject = JSONUtil.parseObj(response.body());

        if(jsonObject.containsKey("visualization")) {
            String base64Str = jsonObject.getStr("visualization");
            byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
            // 写入jellyfin库保存
            String behaviorFileName = userId.toString() +
                    time + "-behavior" + saveType;
            jellyfinUtil.saveFile(decodedBytes, behaviorFileName, examId.toString()+"-behavior", true);
        }

        if(jsonObject.containsKey("predictions")) {
            JSONArray predictions = jsonObject.getJSONArray("predictions");
            if(predictions != null) {
                for (int i = 0; i < predictions.size(); i++) {
                    JSONObject prediction = predictions.getJSONObject(i);
                    String aClass = prediction.getStr("class");
                    if(StrUtil.equals(aClass, "cheating")) {
                        return ApiResponse.success(true);
                    }
                }
            }
        }
        return ApiResponse.success(false);
    }

    /**
     * @description 根据人脸进入考试
     * @param account 帐号信息
     * @param multipartFile 人脸信息
     * @return 返回进入考试是否成功
     */
    @PostMapping("enter")
    public ApiResponse<Boolean> enterExamByFace(@RequestParam("account") String account,
                                                @RequestParam("faceInfo")  MultipartFile multipartFile) throws IOException {
        // 进行人脸比对
        User user = informationPortalClient.getUserInfoByAccount(account);
        if(user == null) {
            return ApiResponse.error(ResponseCode.NOT_FOUND, "未找到帐号", false);
        }

        String targetFacePath = FaceUtil.getTargetFacePath(user.getUserId());
        if(StrUtil.isBlank(targetFacePath)) {
            // 用户未存储人脸信息
            return ApiResponse.error(ResponseCode.NOT_FOUND, "用户未存储人脸信息", false);
        }

        // 为multipartFile创建一个临时文件, 读取其路径
        String tempPathPrefix = "uploaded-face-" + user.getUserId() + new Date();
        String tempFilePath = Files.createTempFile(tempPathPrefix, FaceUtil.getSaveSuffix()).toString();
        Double faceSim = FaceUtil.getFaceSim(tempPathPrefix, targetFacePath);
        // 删除临时文件
        Files.deleteIfExists(Path.of(tempFilePath));

        if(faceSim > FaceUtil.getFaceSimThreshold()) {
            // 成功
            return ApiResponse.success(true);
        } else  {
            // 比对失败
            return ApiResponse.error(ResponseCode.METHOD_NOT_ALLOWED, "比对失败", false);
        }

    }


    public static void main(String[] args) {
//        HttpResponse execute = HttpUtil.createGet("localhost:5000/face?image_path=/home/john/Desktop/faceServer/face1.png&save_path=/home/john/Desktop").execute();
        HttpResponse execute = HttpUtil.createGet("localhost:5000/face/eq?src_path=/home/john/Desktop/faceServer/face1.png&target_path=/home/john/Desktop/faceServer/face2.png").execute();
        System.out.println(execute.body());
    }

}
