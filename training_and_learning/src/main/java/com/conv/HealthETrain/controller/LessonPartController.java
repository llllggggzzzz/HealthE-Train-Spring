package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.client.CoursewareClient;
import com.conv.HealthETrain.client.VideoClient;
import com.conv.HealthETrain.domain.Chapter;
import com.conv.HealthETrain.domain.Courseware;
import com.conv.HealthETrain.domain.DTO.ChapterInfoDTO;
import com.conv.HealthETrain.domain.DTO.SectionInfoDTO;
import com.conv.HealthETrain.domain.Section;
import com.conv.HealthETrain.domain.Video;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ChapterService;
import com.conv.HealthETrain.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liusg
 */
@RestController
@RequestMapping("/part")
@RequiredArgsConstructor
@Slf4j
public class LessonPartController {

    private final SectionService sectionService;
    private final ChapterService chapterService;
    private final VideoClient videoClient;
    private final CoursewareClient coursewareClient;

    /**
     * 修改section标题
     * @param section
     * @return
     */
    @PutMapping("/section/title")
    public ApiResponse<Section> updateSectionTitle(@RequestBody Section section) {
        Section s = sectionService.updateSectionTitleById(section);
        if (s != null) {
            return ApiResponse.success(s);
        } else {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "修改标题失败");
        }
    }

    /**
     * 添加新的section
     * @param chapterId
     * @param section
     * @return
     */
    @PostMapping("/section/{id}")
    public ApiResponse<ChapterInfoDTO> addNewSection(@PathVariable("id") Long chapterId,@RequestBody Section section) {

        // 更新原来的section的order
        List<Section> sections = sectionService.getSectionsByChapterId(chapterId);
        boolean flag = true;
        for (Section s : sections) {
            if (s.getSectionOrder() >= section.getSectionOrder()) {
                s.setSectionOrder(s.getSectionOrder() + 1);
                boolean b = sectionService.updateById(s);
                if (!b) {
                    flag = false;
                }
            }
        }

        // 插入新的section
        section.setVideoId(null);
        Section newSection = sectionService.addSection(section);

        if (newSection == null || !flag) {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "添加section失败");
        }

        ChapterInfoDTO chapterInfoDTO = new ChapterInfoDTO();
        Chapter chapter = chapterService.getById(chapterId);

        chapterInfoDTO.setChapterId(chapter.getChapterId());
        chapterInfoDTO.setChapterTitle(chapter.getChapterTitle());
        chapterInfoDTO.setChapterOrder(chapter.getChapterOrder());

        // sections
        List<Section> newSections = sectionService.getSectionsByChapterId(chapter.getChapterId());
        List<SectionInfoDTO> sectionInfoDTOS = new ArrayList<>();

        newSections.forEach(s -> {
            SectionInfoDTO sectionInfoDTO = new SectionInfoDTO();
            sectionInfoDTO.setSectionId(s.getSectionId());
            sectionInfoDTO.setSectionTitle(s.getSectionTitle());
            sectionInfoDTO.setSectionOrder(s.getSectionOrder());

            // Video 部分
            Video video = videoClient.getVideoBySectionId(s.getSectionId()).getData();
            if (video != null) {
                sectionInfoDTO.setVideoLength(video.getLength());
                sectionInfoDTO.setVideoPath(video.getPath());
                sectionInfoDTO.setVideoSize(video.getSize());
            }

            // Courseware 部分
            Courseware courseware = coursewareClient.getCoursewareBySectionId(s.getSectionId()).getData();
            if (courseware != null) {
                sectionInfoDTO.setCourseware(courseware);
            }

            sectionInfoDTOS.add(sectionInfoDTO);
        });

        chapterInfoDTO.setSections(sectionInfoDTOS);

        return ApiResponse.success(chapterInfoDTO);
    }

    /**
     * 添加新的chapter
     * @param lessonId
     * @param chapter
     * @return
     */
    @PostMapping("/chapter/{id}")
    public ApiResponse<Object> addNewChapter(@PathVariable("id") Long lessonId, @RequestBody Chapter chapter) {
        // 更改原来的chapter的order
        List<Chapter> chapters = chapterService.getChaptersByLessonId(lessonId);
        boolean flag = true;
        for (Chapter c : chapters) {
            if (c.getChapterOrder() >= chapter.getChapterOrder()) {
                c.setChapterOrder(c.getChapterOrder() + 1);
                boolean b = chapterService.updateById(c);
                if (!b) {
                    flag = false;
                }
            }
        }

        // 插入新的chapter
        Chapter newChapter = chapterService.addChapter(chapter);

        if (newChapter == null || !flag) {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "添加chapter失败");
        }
        return ApiResponse.success(newChapter);
    }

}
