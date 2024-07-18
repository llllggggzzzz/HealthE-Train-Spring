package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.POJP.LessonAnnouncement;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.LessonAnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liusg
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/announce")
public class AnnouncementController {

    private final LessonAnnouncementService lessonAnnouncementService;

    /**
     * 发布公告
     * @param announcement
     * @return
     */
    @PostMapping("/new")
    public ApiResponse<Object> addAnnouncement(@RequestBody LessonAnnouncement announcement) {
        announcement.setLaId(null);
        boolean save = lessonAnnouncementService.save(announcement);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "发布公告失败");
        }
    }

    /**
     * 获取课程公告
     * @param lessonId
     * @return
     */
    @GetMapping("/lesson/{id}")
    public ApiResponse<Object> getLessonAnnouncements(@PathVariable("id") String lessonId) {
        List<LessonAnnouncement> lessonAnnouncements = lessonAnnouncementService.getLessonAnnouncements(Long.valueOf(lessonId));
        return ApiResponse.success(lessonAnnouncements);
    }

    /**
     * 删除公告
     * @param laId
     * @return
     */
    @DeleteMapping("/lesson/{id}")
    public ApiResponse<Object> deleteAnnouncement(@PathVariable("id") String laId) {
        boolean remove = lessonAnnouncementService.removeById(Long.valueOf(laId));
        if (remove) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "删除公告失败");
        }
    }

    /**
     * 更新公告
     * @param announcement
     * @return
     */
    @PutMapping("/lesson")
    public ApiResponse<Object> updateAnnouncement(@RequestBody LessonAnnouncement announcement) {
        boolean update = lessonAnnouncementService.updateById(announcement);
        if (update) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "更新公告失败");
        }
    }
}
