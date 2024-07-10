package com.conv.HealthETrain.controller;


import com.conv.HealthETrain.callback.DataCallback;
import com.conv.HealthETrain.client.LessonClient;
import com.conv.HealthETrain.domain.Checkpoint;
import com.conv.HealthETrain.domain.DTO.VideoLoadDTO;
import com.conv.HealthETrain.domain.Section;
import com.conv.HealthETrain.domain.Video;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.exception.GlobalException;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.VideoService;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
@RequestMapping("/video")
@Slf4j
public class VideoController {
    private final VideoService videoService;

    private final LessonClient lessonClient;

    private final VideoSocketHandler videoSocketHandler;

    @GetMapping("/{id}/user/{userId}")
    public ApiResponse<VideoLoadDTO> startLoadVideo(@PathVariable("id") Long id, @PathVariable("userId") Long userId) throws IOException, ExecutionException, InterruptedException {
//         根据视频id找到视频对应的url
        Video video = videoService.getById(id);
        if(video == null) {
            return ApiResponse.error(ResponseCode.NOT_FOUND);
        }
        // 根据section查询对应的小节名称
        Section section = lessonClient.getSectionInfo(video.getSectionId());
        Long sectionId = section.getSectionId();
        Checkpoint checkPoint = lessonClient.getCheckPoint(sectionId, userId);
//        String path = video.getPath();
        // 生成UUID返回给客户端, 携带路径参数
        String uuid = UniqueIdGenerator.generateUniqueId(id.toString(), userId.toString());
        VideoLoadDTO videoLoadDTO = new VideoLoadDTO(video, uuid, section, checkPoint);
        log.info("生成UUID: {}", uuid);
        return ApiResponse.success(videoLoadDTO);
    }

    /**
     * @description 读取固定长度,返回给前端
     * @param uuid websocket标识
     * @param startByte 起始字节位置
     * @return 返回结束字节
     */
    @SneakyThrows
    @GetMapping("/chunk/{uuid}")
    public ApiResponse<Long> getVideoChunk(@PathVariable("uuid") String uuid,
                                            @RequestParam("startByte") Long startByte,
                                           @RequestParam("readBytes") Integer readBytes) {
        Long endByte = videoSocketHandler.sendChunk(uuid, startByte, readBytes);
        if (endByte == -1) {
            return ApiResponse.error(ResponseCode.NOT_FOUND);
        }
        return ApiResponse.success(endByte);
    }

    @SneakyThrows
    @GetMapping("/jump/{uuid}")
    public ApiResponse<Long> jumpToIndex(@PathVariable("uuid") String uuid,
                                         @RequestParam("jumpIndex") Long jumpIndexByte,
                                         @RequestParam("bufferSize") Integer bufferSize) {
        Long endByte = videoSocketHandler.jumpToIndex(uuid, jumpIndexByte, bufferSize);
        if (endByte == -1) {
            return ApiResponse.error(ResponseCode.NOT_FOUND);
        }
        return ApiResponse.success(endByte);
    }


}
