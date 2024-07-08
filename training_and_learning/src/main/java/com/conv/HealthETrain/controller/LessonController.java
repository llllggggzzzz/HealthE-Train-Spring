package com.conv.HealthETrain.controller;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.DTO.ChapterDTO;
import com.conv.HealthETrain.domain.DTO.LessonInfoDTO;
import com.conv.HealthETrain.domain.POJP.Chapter;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.conv.HealthETrain.domain.POJP.Section;
import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/study")
@AllArgsConstructor
@Slf4j
public class LessonController {

    private final LessonLinkUserService lessonLinkUserService;

    private final LessonLinkTeacherService lessonLinkTeacherService;

    private final LessonService lessonService;

    private final InformationPortalClient informationPortalClient;

    private final RecentLessonsService recentLessonsService;

    private final ChapterService chapterService;

    private final SectionService sectionService;


    /**
     * @description 根据课程列表查询DTO并返回，提取为私有方法共多个方法使用
     * @param choosedLessons 课程id列表
     * @return LessonInfoDTO
     */
    private List<LessonInfoDTO> getLessonInfoDTOS(List<Long> choosedLessons) {
        if (choosedLessons.isEmpty()) return ListUtil.empty();
        List<LessonInfoDTO> lessonInfoDTOList  = CollUtil.newArrayList();

        for (Long lessonId : choosedLessons) {
            // 3. 查询课程信息
            Lesson lesson = lessonService.getById(lessonId);
            if (lesson == null) {
                // 查询不到课程信息，直接跳过其他查询
                log.error("查询到用户选课,课程ID: {}，但未查询到课程信息", lessonId);
                continue;
            }
            // 2. 查lesson_link_teacher, 得到教师id
            List<Long> teachersByLessonId = lessonLinkTeacherService.getTeachersByLessonId(lessonId);
            // 3. 根据教师id 得到教师姓名, 返回教师姓名 进行openfeign调用
            List<String> teacherNames = ListUtil.empty();

            for (Long teacherId : teachersByLessonId) {
                TeacherDetail teacherDetail = informationPortalClient.getTeacherDetailById(teacherId);
                if (teacherDetail != null) {
                    teacherNames.add(teacherDetail.getRealName());
                } else {
                    log.error("查询到教师ID: {}, 但未查询到教师信息", teacherId);
                }
            }
            // 4. 拼接
            LessonInfoDTO lessonInfoDTO = new LessonInfoDTO(lesson, teacherNames);
            lessonInfoDTOList.add(lessonInfoDTO);
        }
        return lessonInfoDTOList;
    }

    /**
     * @description 查询用户课程，对应前端渲染LessonCard
     * @param id 用户id
     * @return 对应DTO
     */
    @GetMapping("/user/{id}")
    public ApiResponse<List<LessonInfoDTO>> getLessons(@PathVariable("id") Long id) {
        // 1. 查课程表，得到课程信息
        List<Long> choosedLessons = lessonLinkUserService.getChooesdLessons(id);
        if (choosedLessons.isEmpty()) {
            // 返回用户未选课消息
            return ApiResponse.success(ResponseCode.SUCCEED, "用户未选课");
        }

        List<LessonInfoDTO> lessonInfoDTOS = getLessonInfoDTOS(choosedLessons);
        // 5. 返回
        return ApiResponse.success(lessonInfoDTOS);
    }

    /**
     * @description 查询最近访问
     * @param id 用户id
     * @return 对应DTO
     */
    @GetMapping("/user/{id}/recent")
    public ApiResponse<List<LessonInfoDTO>> getRecentLessons(@PathVariable("id") Long id) {
        // 根据用户id查询最近信息表
        List<Long> lessonIds = recentLessonsService.getLessonIdsByUserId(id);
        if (lessonIds.isEmpty()) return ApiResponse.success(ResponseCode.SUCCEED, "未寻找到最近访问");
        List<LessonInfoDTO> lessonInfoDTOS = getLessonInfoDTOS(lessonIds);
        // 5. 返回
        return ApiResponse.success(lessonInfoDTOS);
    }

    /**
     * @description 查询课程对应的章节信息
     * @param id 课程id
     * @return List<ChapterDTO> 返回对应DTO
     */
    @GetMapping("/lesson/{id}")
    public ApiResponse<List<ChapterDTO>> getLessonDetail(@PathVariable("id") Long id) {
        List<Chapter> chapters = chapterService.getChaptersByLessonId(id);
        // 根据章节信息查询课程
        if (chapters.isEmpty()) {
            log.warn("查询课程信息不存在");
            return ApiResponse.error(ResponseCode.NOT_FOUND, "课程信息不存在");
        }

        List<ChapterDTO> chapterDTOS = CollUtil.newArrayList();

        // 根据章信息查询section信息
        for (Chapter chapter: chapters) {
            ChapterDTO chapterDTO = new ChapterDTO();
            chapterDTO.setChapter(chapter);
            List<Section> sections = sectionService.getSectionsByChapterId(chapter.getChapterId());
            if(!sections.isEmpty()) {
                chapterDTO.setSections(sections);
            } else {
                log.warn("章节: {} 未设置Section", chapter.getChapterId());
            }
            chapterDTOS.add(chapterDTO);
        }

        return ApiResponse.success(chapterDTOS);
    }

}
