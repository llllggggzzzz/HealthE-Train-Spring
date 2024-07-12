package com.conv.HealthETrain.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.conv.HealthETrain.domain.DTO.ApplyLiveDTO;
import com.conv.HealthETrain.domain.DTO.StreamPublishInfo;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.utils.ConfigUtil;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/live")
@Slf4j
public class LiveController {

    private static final String streamingBaseAddress = "rtmp://localhost/live";

    private static final Map<String, StreamPublishInfo> sessionMap = new ConcurrentHashMap<>();
    private static final List<String> uuidBucket = new ArrayList<>();
    private static final Map<String, ApplyLiveDTO> liveDTOMap = new ConcurrentHashMap<>();

    /**
     * @description 推流过滤, 放行申请过的流, 拒绝未申请的流
     * @param streamPublishInfo 连接请求信息, 由SRS转发而来
     * @return 固定返回值: ResponseEntity.ok().body(0);表示成功, 其余表示失败
     */
    @PostMapping("/streams")
    public ResponseEntity<Integer> livePublishHandler(@RequestBody StreamPublishInfo streamPublishInfo) {
        log.info("接受消息: {}", streamPublishInfo.toString());
        // 在UUID桶中检查是否存在UUID, 如果存在则取出使用,通过推流, 否则拒绝
        String stream = streamPublishInfo.getStream();
        int index = uuidBucket.indexOf(stream);
        if(index == -1) {
            // 未鉴权的请求
            log.warn("服务器发生未鉴权请求: {}", new JSONObject(streamPublishInfo).toStringPretty());
            return ResponseEntity.status(401).body(401);
        } else {
            log.info("推流成功: {}", new JSONObject(streamPublishInfo).toStringPretty());
            // 将流信息保存到服务器session中
            sessionMap.put(stream, streamPublishInfo);
            return ResponseEntity.ok().body(0);
        }
    }

    /**
     * @description 推流申请逻辑,返回推流地址给用户
     * @param userId 申请UUID批准
     * @return 返回加密后的UUID
     */
    @PostMapping("/uuid/user/{userId}")
    public ApiResponse<String> getLiveUUID(@RequestBody ApplyLiveDTO applyLiveDTO) {
        // TODO 权限校验, 谁可以开直播
        String liveKey = ConfigUtil.getLiveKey();
        if(StrUtil.isBlankOrUndefined(liveKey)) {
            return ApiResponse.error(ResponseCode.SERVICE_UNAVAILABLE,
                    "未鉴权的请求");
        }
        String UUID = UniqueIdGenerator.generateUniqueId(applyLiveDTO.getUserId().toString(), liveKey);
        uuidBucket.add(UUID);
        // 组装拼接推流地址
        String liveStreamingUrl = streamingBaseAddress + "/" + UUID;
        // TODO 保存直播串流信息到redis
        liveDTOMap.put(UUID, applyLiveDTO);
        return ApiResponse.success(liveStreamingUrl);
    }


}
