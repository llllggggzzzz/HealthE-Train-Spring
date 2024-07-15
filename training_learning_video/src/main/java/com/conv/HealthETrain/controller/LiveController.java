package com.conv.HealthETrain.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONParser;
import cn.hutool.json.JSONUtil;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.DTO.ApplyLiveDTO;
import com.conv.HealthETrain.domain.DTO.ApplyLiveUUDTO;
import com.conv.HealthETrain.domain.DTO.StreamPublishInfo;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.utils.ConfigUtil;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/live")
@Slf4j
public class LiveController {
    private static final List<String> uuidBucket = new ArrayList<>();

    private final StringRedisTemplate redisTemplate;

    private final InformationPortalClient informationPortalClient;

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
            redisTemplate.opsForValue().set("live-stream:"+stream, JSONUtil.toJsonStr(streamPublishInfo));
            return ResponseEntity.ok().body(0);
        }
    }

    @PostMapping("/exit")
    public ResponseEntity<Integer> liveEixtHandler(@RequestBody JSONObject jsonObject) throws IOException {
        log.info("接受结束推流消息: {}", jsonObject.toStringPretty());
        // 通知用户, 直播已经结束
        String streamId = jsonObject.getStr("stream");
        if(StrUtil.isBlankOrUndefined(streamId)) {
            return ResponseEntity.ok().body(0);
        }
        String exitMessage = "{header:{from:-1,streamId:-1,userName:-1,},body:{message:exit,date:1,},type:1,}";
        String message = JSONUtil.parseObj(exitMessage).toString();
        log.info("Exit: {}, streamId: {}", message, streamId);
        LiveSocketHandler.sendMessage(streamId, message);
        // 将streamId从令牌桶中删除
        redisTemplate.delete("live:"+streamId);
        return ResponseEntity.ok().body(0);
    }


    /**
     * @description 推流申请逻辑,返回推流地址给用户
     * @param applyLiveDTO 直播申请信息
     * @return 返回加密后的UUID
     */
    @PostMapping("/uuid/user")
    public ApiResponse<String> getLiveUUID(@RequestBody ApplyLiveDTO applyLiveDTO) {
        // TODO 权限校验, 谁可以开直播
        String liveKey = ConfigUtil.getLiveKey();
        if(StrUtil.isBlankOrUndefined(liveKey)) {
            return ApiResponse.error(ResponseCode.SERVICE_UNAVAILABLE,
                    "未鉴权的请求");
        }
        Long categoryId = applyLiveDTO.getCategoryId();
        String categoryName = informationPortalClient.getCategoryNameById(categoryId);
        applyLiveDTO.setCategoryName(categoryName);
        String UUID = UniqueIdGenerator.generateUniqueId(applyLiveDTO.getUserId().toString(), liveKey);
        uuidBucket.add(UUID);
        // 组装拼接推流地址
        log.info("收到推流信息: {}", applyLiveDTO);
        redisTemplate.opsForValue().set("live:"+UUID, JSONUtil.toJsonStr(applyLiveDTO));
        return ApiResponse.success(UUID);
    }

    @GetMapping("/{streamId}")
    public ApiResponse<ApplyLiveDTO> getLiveInfo(@PathVariable("streamId") String streamId) {
        String liveInfo = redisTemplate.opsForValue().get("live:" + streamId);
        if(StrUtil.isNullOrUndefined(liveInfo)) {
            return ApiResponse.error(ResponseCode.NOT_FOUND);
        }
        ApplyLiveDTO applyLiveDTO = JSONUtil.toBean(liveInfo, ApplyLiveDTO.class);
        log.info("获取到推流信息: {}", applyLiveDTO);
        return ApiResponse.success(applyLiveDTO);
    }

    @GetMapping("/allLive")
    public ApiResponse<List<ApplyLiveUUDTO>> getAllLiveInfo(){
        Set<String> keys = redisTemplate.keys("live:*");
        List<String> liveInfos = null;
        if (keys != null) {
            liveInfos = redisTemplate.opsForValue().multiGet(keys);
        }
        List<ApplyLiveUUDTO> applyLiveUUDTOs = null;
        if (liveInfos != null) {
            applyLiveUUDTOs = liveInfos.stream()
                    .map(liveInfo -> {
                        ApplyLiveUUDTO dto = JSONUtil.toBean(liveInfo, ApplyLiveUUDTO.class);
                        dto.setUUID(getUUIDFromKey(liveInfo));
                        return dto;
                    })
                    .toList();
        }
        return ApiResponse.success(applyLiveUUDTOs);
    }

    // 获取UUID参数
    private String getUUIDFromKey(String key) {
        int index = key.indexOf(':');
        if (index != -1 && index + 1 < key.length()) {
            return key.substring(index + 1);
        }
        return "";
    }
}
