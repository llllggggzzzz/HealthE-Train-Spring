package com.conv.HealthETrain.controller;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.DTO.ExceptionInfoDTO;
import com.conv.HealthETrain.domain.ExceptionInfo;
import com.conv.HealthETrain.domain.JellyfinImage;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.factory.JellyfinFactory;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ExceptionInfoService;
import com.conv.HealthETrain.utils.FaceUtil;
import com.conv.HealthETrain.utils.JellyfinUtil;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/exam/behavior")
@Slf4j
@AllArgsConstructor
public class ExamBehaviorController {

    private final InformationPortalClient informationPortalClient;

    private final StringRedisTemplate redisTemplate;

    private static final String behaviorMediaLibraryName = "userBehavior";

    private final ExceptionInfoService exceptionInfoService;

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
                                                   @RequestParam("time") Date dateTime) throws IOException {

        //  记录原文件, 调用异常检测模型, 记录异常信息, 记录异常图片
        
        // 1. 创建此考试的媒体库
        JellyfinUtil jellyfinUtil = JellyfinFactory.build(JellyfinFactory.configPath);
        String libName = behaviorMediaLibraryName+"-"+examId.toString()+"-behavior";
        log.info(libName);
        DateFormat outputFormat = new SimpleDateFormat("yyyy年M月d日HH:mm:ss");
        String time = outputFormat.format(dateTime);
        List<String> mediaNameLibraries;
        if(Boolean.TRUE.equals(redisTemplate.hasKey("mediaLibrary:mediaLibraryNameList"))) {
           // 直接读取即可
            mediaNameLibraries = redisTemplate.opsForList().range("mediaLibrary:mediaLibraryNameList", 0, -1);
            log.info("获取到所有媒体库 redis {}", mediaNameLibraries);
        } else {
            mediaNameLibraries = jellyfinUtil.getAllMediaLibrary("");
            redisTemplate.opsForList().rightPushAll("mediaLibrary:mediaLibraryNameList", mediaNameLibraries);
            log.info("获取到所有媒体库 jellyfin {}", mediaNameLibraries);
        }

        if(mediaNameLibraries == null) {
            return ApiResponse.error(ResponseCode.NOT_FOUND);
        }

        if(!mediaNameLibraries.contains(libName)) {
            jellyfinUtil.createMediaLibrary("/video/"+behaviorMediaLibraryName+"/"+examId, libName, "");
            mediaNameLibraries = jellyfinUtil.getAllMediaLibrary("");
            redisTemplate.delete("mediaLibrary:mediaLibraryNameList");
            redisTemplate.opsForList().rightPushAll("mediaLibrary:mediaLibraryNameList", mediaNameLibraries);
        }

        // 2. 上传用户照片
        String fileName = userId.toString() + "-"+ time + "-origin" + FaceUtil.getFaceStoreType();
        log.info("fileName: {}", fileName);
        jellyfinUtil.saveFile(multipartFile.getBytes(), fileName, libName, true);

        // 创建临时文件
        String filePath = Files.createFile(Path.of(fileName)).toAbsolutePath().toString();
        log.info("创建行为检测文件: {}", filePath);
        // 写入文件
        FileUtil.writeBytes(multipartFile.getBytes(), filePath);

        // 调用异常检测函数
        String getUrl = UrlBuilder.create()
                .setScheme("http")
                .setHost(FaceUtil.getFaceServerHost())
                .setPort(FaceUtil.getFaceServerPort())
                .addPath("/behavior").addQuery("image_path", filePath)
                .build();

        HttpResponse response = HttpUtil.createGet(getUrl).execute();
        JSONObject responseBody = JSONUtil.parseObj(response.body());
//        log.info(responseBody.toStringPretty());
        JSONObject jsonObject = responseBody.getJSONObject("data");
        log.info("keys : {}", jsonObject.keySet());
        if(jsonObject.containsKey("visualization")) {
            String base64Str = jsonObject.getStr("visualization");
            byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
            // 写入jellyfin库保存
            String behaviorFileName = userId + "-" +
                    time + "-detect" + FaceUtil.getFaceStoreType();
            jellyfinUtil.saveFile(decodedBytes, behaviorFileName, libName, true);
        }

        FileUtil.del(filePath);

        if(jsonObject.containsKey("predictions")) {
            JSONArray predictions = jsonObject.getJSONArray("predictions");
            log.info("获取到检测信息: {}", predictions);
            ExceptionInfo exceptionInfo = new ExceptionInfo();
            exceptionInfo.setExamId(examId);
            exceptionInfo.setUserId(userId);
            exceptionInfo.setAddTime(time);
            if(predictions != null) {
                exceptionInfo.setInfo(predictions.toString());
            }
            exceptionInfoService.save(exceptionInfo);
            if(predictions != null) {
                for (int i = 0; i < predictions.size(); i++) {
                    JSONObject prediction = predictions.getJSONObject(i);
                    String aClass = prediction.getStr("class");
                    if(StrUtil.equals(aClass, "cheating")) {
                        return ApiResponse.success(ResponseCode.SUCCEED, "检测到作弊", true);
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
        String tempPathPrefix = "uploaded-face-" + user.getUserId() + UniqueIdGenerator.generateUniqueId("", "");
        String tempFilePath = Files.createTempFile(tempPathPrefix, FaceUtil.getSaveSuffix()).toString();
        Path path = Path.of(tempFilePath);
        if(Files.exists(path)) {
            FileUtil.writeBytes(multipartFile.getBytes(), tempFilePath);
        }
        Double faceSim = FaceUtil.getFaceSim(tempFilePath, targetFacePath);
        // 删除临时文件
        FileUtil.del(path);
        if(faceSim > FaceUtil.getFaceSimThreshold()) {
            // 成功
            return ApiResponse.success(true);
        } else  {
            // 比对失败
            return ApiResponse.error(ResponseCode.METHOD_NOT_ALLOWED, "比对失败", false);
        }

    }


    @GetMapping("/{examId}/user/{userId}")
    public ApiResponse<List<ExceptionInfoDTO>> getUserBehaviors(@PathVariable("examId") Long examId,
                                                                @PathVariable("userId") Long userId) {
        // 查询所有用户的所有行为信息, 读取行为库
        if(Boolean.TRUE.equals(redisTemplate.hasKey("behavior-" + examId + userId))) {
            List<String> stringList = redisTemplate.opsForList().range("behavior-" + examId + userId, 0, -1);
            if (stringList != null) {
                List<ExceptionInfoDTO> exceptionInfoDTOS = stringList.stream().map(str -> JSONUtil.toBean(str, ExceptionInfoDTO.class)).toList();
                return ApiResponse.success(exceptionInfoDTOS);
            }
        }
        String mediaLibraryName = "userBehavior-" + examId.toString() + "-behavior";
        JellyfinUtil jellyfinUtil = JellyfinFactory.build(JellyfinFactory.configPath);
        List<JellyfinImage> imagePathList = jellyfinUtil.findImagePathList(userId.toString(), mediaLibraryName, "");
        HashSet<String> timeSet = new HashSet<>();
        List<ExceptionInfoDTO> exceptionInfoDTOArrayList = new ArrayList<>();
        for (JellyfinImage jellyfinImage : imagePathList) {
            // 建立时间set
            String imageName = jellyfinImage.getImageName();
            String[] split = imageName.split("-");
            if(split.length < 3) {
                continue;
            }
            String time = split[1];
            timeSet.add(time);
        }

        for (String time : timeSet) {
            ExceptionInfoDTO exceptionInfoDTO = new ExceptionInfoDTO();
            String originImageName = userId + "-" + time + "-origin";
            String detectImageName = userId + "-" + time + "-detect";
            JellyfinImage originImage = CollUtil.findOne(imagePathList, jellyfinImage -> StrUtil.equals(jellyfinImage.getImageName(), originImageName));
            JellyfinImage detectImage = CollUtil.findOne(imagePathList, jellyfinImage -> StrUtil.equals(jellyfinImage.getImageName(), detectImageName));
            if(originImage != null) {
                exceptionInfoDTO.setOriginPicPath(originImage.getPath());
            }
            if(detectImage != null) {
                exceptionInfoDTO.setDetectPicPath(detectImage.getPath());
            }
            exceptionInfoDTO.setTime(time);

            LambdaQueryWrapper<ExceptionInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper
                    .eq(ExceptionInfo::getExamId, examId)
                    .eq(ExceptionInfo::getUserId, userId)
                    .eq(ExceptionInfo::getAddTime, time);
            ExceptionInfo exceptionInfo = exceptionInfoService.getOne(lambdaQueryWrapper);
            if(exceptionInfo != null) {
                exceptionInfoDTO.setInfo(exceptionInfo.getInfo());
            }
            exceptionInfoDTOArrayList.add(exceptionInfoDTO);
        }
        if(!exceptionInfoDTOArrayList.isEmpty()) {
            redisTemplate.opsForList().rightPushAll("behavior-"+examId+userId,
                    exceptionInfoDTOArrayList.stream().map(JSONUtil::toJsonStr).toList());
        }

        return ApiResponse.success(exceptionInfoDTOArrayList);
    }

}
