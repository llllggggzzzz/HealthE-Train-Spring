package com.conv.HealthETrain.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.conv.HealthETrain.client.LessonClient;
import com.conv.HealthETrain.domain.*;
import com.conv.HealthETrain.domain.DTO.VideoLoadDTO;
import com.conv.HealthETrain.domain.domain.Schedule;
import com.conv.HealthETrain.domain.Video;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.factory.JellyfinFactory;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ScheduleService;
import com.conv.HealthETrain.service.VideoService;
import com.conv.HealthETrain.utils.JellyfinUtil;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.aot.FeignClientBeanFactoryInitializationAotProcessor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
@RequestMapping("/video")
@Slf4j
public class VideoController {
    private final VideoService videoService;

    private final LessonClient lessonClient;

    private final VideoSocketHandler videoSocketHandler;

    private final ScheduleService scheduleService;

    private final FeignClientBeanFactoryInitializationAotProcessor feignClientBeanFactoryInitializationCodeGenerator;

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
        log.info("生成UUID: {}", uuid) ;
        return ApiResponse.success(videoLoadDTO);
    }

    @GetMapping("/{videoId}")
    public Video getVideoById(@PathVariable("videoId") Long videoId) {
        return videoService.getById(videoId);
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

    /**
     * @description 获取视频对应的串流URL
     * @param videoId 视频ID
     * @param libraryName 媒体库名称
     * @param userName jellyfin用户名称
     * @return 返回串流URL
     */
    @GetMapping("/streaming/{videoId}/library/{libraryName}")
    public ApiResponse<String> getVideoStreamingURL(@PathVariable("videoId") Long videoId,
                                                 @PathVariable("libraryName") String libraryName,
                                                 @RequestParam(value = "userName", required = false, defaultValue = "jellyfin") String userName) {
        Video video = videoService.getById(videoId);

        if(video == null) return ApiResponse.error(ResponseCode.NOT_FOUND);

        String path = video.getPath();

        String videoFixName = FileUtil.getName(path);
        String[] split = videoFixName.split("\\.");
        if(split.length <= 1) {
            return ApiResponse.error(ResponseCode.GONE);
        }
        String videoName = split[0];
        String streamingVideoUrl = JellyfinFactory.build(JellyfinFactory.configPath)
                .findStreamingVideoUrl(videoName, libraryName, userName);
        return StrUtil.isBlank(streamingVideoUrl) ?
                ApiResponse.error(ResponseCode.NOT_FOUND)
                : ApiResponse.success(streamingVideoUrl);
    }

    /**
     * @description 更新此视频的检查点,返回下一视频的视频Id给前端进行跳转
     * @param videoId 视频ID
     * @param userId 用户ID
     * @param sectionId 小节ID
     * @return 返回下一视频的视频ID
     */
    @PostMapping("/checkpoint")
    public ApiResponse<Long> updateCheckPoint(@RequestBody JSONObject request) {
        // 根据sectionId 去查询对应的章和课程信息
        Long videoId = request.getLong("video_id");
        Long sectionId = request.getLong("section_id");
        Long userId = request.getLong("user_id");
        Section section = lessonClient.getSectionInfo(sectionId);
        if(ObjectUtil.isNull(section)) {
            return ApiResponse.error(ResponseCode.NOT_FOUND);
        }
        Chapter chapterInfo = lessonClient.getChapterInfo(section.getChapterId());
        if(chapterInfo == null) {
            return ApiResponse.error(ResponseCode.NOT_FOUND);
        }
        Long chapterId = chapterInfo.getChapterId();
        Long lessonId = chapterInfo.getLessonId();
        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setChapterId(chapterId);
        checkpoint.setLessonId(lessonId);
        checkpoint.setSectionId(sectionId);
        checkpoint.setUserId(userId);
        Checkpoint savedCheckpoint = lessonClient.setCheckpoint(checkpoint);
        if(savedCheckpoint == null) {
            return ApiResponse.error(ResponseCode.UNPROCESSABLE_ENTITY);
        } else {
            // 查找下一个视频的视频ID
            log.info("获取下一小节视频ID");
            Long nextVideoId = findNextVideo(section, chapterInfo, lessonId);
            return ApiResponse.success(nextVideoId);
        }
    }

    /**
     * @description 更新学习时间
     * @param requestBody 包含更新消息
     * @return 返回是否更新成功
     */
    @PostMapping("/record")
    public ApiResponse<String> recordStudyTime(@RequestBody JSONObject requestBody) {
        Long userId = requestBody.getLong("user_id");
        Long videoId = requestBody.getLong("video_id");
        Double studyTime = requestBody.getDouble("study_time");
        LambdaQueryWrapper<Schedule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Schedule::getVideoId, videoId).eq(Schedule::getUserId, userId);
        Schedule oldSchedule = scheduleService.getOne(lambdaQueryWrapper);
        Schedule schedule = new Schedule();
        schedule.setVideoId(videoId);
        schedule.setUserId(userId);
        boolean saved = false;
        if(ObjectUtil.isNull(oldSchedule)) {
            schedule.setTime(studyTime);
            saved = scheduleService.save(schedule);
        } else {
            Double sumTime = oldSchedule.getTime() + studyTime;
            schedule.setTime(sumTime);
            saved = scheduleService.update(schedule, lambdaQueryWrapper);
        }
        return saved ? ApiResponse.success():ApiResponse.error(ResponseCode.BAD_REQUEST);
    }

    @GetMapping("/time/user/{userId}")
    public ApiResponse<Double> getLearningMin(@PathVariable("userId") Long userId) {
        LambdaQueryWrapper<Schedule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Schedule::getUserId, userId);
        List<Schedule> scheduleList = scheduleService.list(lambdaQueryWrapper);
        Double sumTime = 0.0;
        for (Schedule schedule : scheduleList) {
            sumTime += schedule.getTime();
        }
        return ApiResponse.success(sumTime);
    }

    /**
     * @description 查询下一个视频的ID
     * @param section 小节信息
     * @param chapterInfo 章节信息
     * @param lessonId 课程信息
     * @return 返回下一个视频的ID
     */
    public Long findNextVideo(Section section, Chapter chapterInfo, Long lessonId) {
        Section nextSection;
        Chapter nextChapter = null;
        Long chapterId = chapterInfo.getChapterId();
        Integer sectionOrder = section.getSectionOrder();
        List<Section> allSection = lessonClient.getAllSection();
        log.info("当前sectionOrder: {}", sectionOrder);
        // 查询是否有下一序号且章节相同
        nextSection = CollUtil.findOne(allSection,
                sectionOne -> ObjectUtil.equals(sectionOne.getChapterId(), chapterId)
                        && ObjectUtil.equals(sectionOne.getSectionOrder(), sectionOrder + 1));
        if(ObjectUtil.isNull(nextSection)) {
            // 此章已经结束, 进入下一章节查询第一个
            Integer chapterOrder = chapterInfo.getChapterOrder();
            log.info("当前chapterOrder: {}", chapterOrder);
            List<Chapter> allChapter = lessonClient.getAllChapter();
            nextChapter = CollUtil.findOne(allChapter,
                    chapterOne -> ObjectUtil.equals(chapterOne.getLessonId(), lessonId)
                            && ObjectUtil.equals(chapterOne.getChapterOrder(), chapterOrder + 1));
        }
        log.info("NextSection: {}", nextSection);
        log.info("NextChapter: {}", nextChapter);
        if(!ObjectUtil.isNull(nextSection)) {
            // 根据小节返回对应的videoId
            log.info("返回查询的videoId: {}", nextSection.getVideoId());
            return nextSection.getVideoId();
        } else if (!ObjectUtil.isNull(nextChapter)) {
            // 查询此章节中第一个小节
            Section firstSection = lessonClient.getFirstSectionByChapterId(nextChapter.getChapterId());
            return firstSection.getVideoId();
        } else {
            return -1L;
        }
    }

    /**
     * 根据章节id获取视频
     * @param sectionId
     * @return
     */
    @GetMapping("/section/{id}")
    public ApiResponse<Video> getVideoBySectionId(@PathVariable("id") Long sectionId) {
        Video video = videoService.getVideoBySectionId(sectionId);
        return ApiResponse.success(video);
    }

    /**
     * 发布视频
     * @param video
     * @return
     */
    @PostMapping("/new")
    public ApiResponse<Object> addVideo(@RequestBody Video video) throws IOException, InterruptedException {
        video.setVideoId(null);
        // 将视频文件放置在媒体库中, 并进行ffmpeg处理
        Long sectionId = video.getSectionId();
        Section section = lessonClient.getSectionInfo(sectionId);
        if(section != null) {
            Long chapterId = section.getChapterId();
            Chapter chapterInfo = lessonClient.getChapterInfo(chapterId);
            if(chapterInfo != null){
                Long lessonId = chapterInfo.getLessonId();
                ApiResponse<Lesson> lesson = lessonClient.getLessonById(lessonId.toString());
                if(lesson.getData() != null) {
                    Lesson lessonData = lesson.getData();
                    String lessonName = lessonData.getLessonName();
                    Integer lessonType = lessonData.getLessonType();
                    String libName = lessonType + "-" + lessonName + "-" + lessonId;
                    JellyfinUtil jellyfinUtil = JellyfinFactory.build(JellyfinFactory.configPath);
                    jellyfinUtil.createMediaLibrary("/video/"+libName, libName, "");
                    // 加入媒体库中
                    jellyfinUtil.saveFile(video.getPath(), libName, true, true);
                    video.setSaveLibrary(libName);
                }
            }
        }

        // 进行视频ffmpeg处理
        String input = video.getPath();
        String output = video.getPath();
        String command = String.format("ffmpeg -i %s -c copy -movflags frag_keyframe+empty_moov+default_base_moof -y %s", input, output);
        runFFmpegCommand(command);

        boolean save = videoService.save(video);

        // 更新Section的videoId
        ApiResponse<Boolean> booleanApiResponse = lessonClient.sectionSetVideo(video.getSectionId(), video.getVideoId());
        boolean result = booleanApiResponse.getData();

        if (save && result) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "发布视频失败");
        }
    }

    /**
     * 运行给定的FFmpeg命令
     * @param command FFmpeg命令
     * @return true 如果命令成功执行，否则 false
     * @throws IOException 如果发生IO异常
     * @throws InterruptedException 如果进程等待中断
     */
    public static boolean runFFmpegCommand(String command) throws IOException, InterruptedException {
        // 创建进程构建器
        ProcessBuilder builder = new ProcessBuilder();
        // 设置命令和参数
        builder.command("bash", "-c", command); // Linux 或 macOS 下的写法
        // builder.command("cmd.exe", "/c", command); // Windows 下的写法
        Console.log("run exec");
        // 启动进程
        Process process = builder.start();

        // 等待进程执行完成
        int exitCode = process.waitFor();
        Console.log("succeed {}", exitCode);
        // 打印命令执行结果
        if (exitCode == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        String input = "/home/john/Desktop/test.mp4";
        String output = "/home/john/Desktop/testtt.mp4";

        String command = String.format("ffmpeg -i %s -c copy -movflags frag_keyframe+empty_moov+default_base_moof -y %s", input, output);

        try {
            boolean success = runFFmpegCommand(command);

            if (success) {
                System.out.println("FFmpeg命令成功执行");
            } else {
                System.err.println("FFmpeg命令执行失败");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

