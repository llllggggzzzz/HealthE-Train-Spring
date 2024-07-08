package com.conv.HealthETrain.controller;


import com.conv.HealthETrain.callback.DataCallback;
import com.conv.HealthETrain.domain.DTO.VideoLoadDTO;
import com.conv.HealthETrain.domain.Video;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.exception.GlobalException;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.VideoService;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
@RequestMapping("/video")
@Slf4j
public class VideoController {
    private final VideoService videoService;

    @GetMapping("/{id}/user/{userId}")
    public ApiResponse<VideoLoadDTO> startLoadVideo(@PathVariable("id") Long id, @PathVariable("userId") Long userId) throws IOException, ExecutionException, InterruptedException {
        // 根据视频id找到视频对应的url
//        Video video = videoService.getById(id);
//        if(video == null) {
//            return ApiResponse.error(ResponseCode.NOT_FOUND);
//        }

//        String path = video.getPath();
        String path = "/home/john/Desktop/trailer_hd.mp4";
        // 生成UUID返回给客户
        // 端, 携带路径参数
        String uuid = UniqueIdGenerator.generateUniqueId(id.toString(), userId.toString());
        VideoLoadDTO videoLoadDTO = new VideoLoadDTO(path, uuid);
        log.info("生成UUID: {}", uuid);
        return ApiResponse.success(videoLoadDTO);
    }


}
