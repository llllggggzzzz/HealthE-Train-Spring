package com.conv.HealthETrain.client;

import com.conv.HealthETrain.domain.Chapter;
import com.conv.HealthETrain.domain.Checkpoint;
import com.conv.HealthETrain.domain.DTO.LessonInfoDTO;
import com.conv.HealthETrain.domain.Section;
import com.conv.HealthETrain.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient("training-and-learning")
public interface LessonClient {
    @GetMapping("/study/section/{id}")
    Section getSectionInfo(@PathVariable("id") Long id);

    @GetMapping("/study/sections")
    List<Section> getAllSection();

   @GetMapping("/study/chapter/{id}")
   public Chapter getChapterInfo(@PathVariable("id") Long id);

    @PostMapping("/study/checkpoint")
    Checkpoint setCheckpoint(@RequestBody Checkpoint checkpoint);

    @GetMapping("/study/chapter/{chapterId}/section/first")
    Section getFirstSectionByChapterId(@PathVariable("chapterId") Long chapterId);

    @GetMapping("/study/chapters")
    List<Chapter> getAllChapter();

    @GetMapping("/study/checkpoint/{sectionId}/user/{userId}")
    Checkpoint getCheckPoint(@PathVariable("sectionId") Long sectionId,
                             @PathVariable("userId") Long userId);

    @GetMapping("/study/user/{id}")
    ApiResponse<List<LessonInfoDTO>> getLessons(@PathVariable("id") Long id);
}

