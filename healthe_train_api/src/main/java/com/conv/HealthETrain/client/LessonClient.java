package com.conv.HealthETrain.client;

import com.conv.HealthETrain.domain.Checkpoint;
import com.conv.HealthETrain.domain.Section;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient("training-and-learning")
public interface LessonClient {
    @GetMapping("/study/section/{id}")
    Section getSectionInfo(@PathVariable("id") Long id);

    @GetMapping("/study/checkpoint/{sectionId}/user/{userId}")
    Checkpoint getCheckPoint(@PathVariable("sectionId") Long sectionId,
                             @PathVariable("userId") Long userId);
}

