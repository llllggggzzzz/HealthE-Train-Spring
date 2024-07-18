package com.conv.HealthETrain.client;

import com.conv.HealthETrain.domain.Courseware;
import com.conv.HealthETrain.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author liusg
 */
@FeignClient(name = "resource-management")
public interface CoursewareClient {
    @GetMapping("/courseware/{id}")
    ApiResponse<Courseware> getCoursewareBySectionId(@PathVariable("id") Long sectionId);
}
