package com.conv.HealthETrain.client;

import com.conv.HealthETrain.domain.Video;
import com.conv.HealthETrain.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author liusg
 */
@FeignClient("training-learning-video")
public interface VideoClient {

    @GetMapping("/video/section/{id}")
    ApiResponse<Video> getVideoBySectionId(@PathVariable("id") Long sectionId);

}
