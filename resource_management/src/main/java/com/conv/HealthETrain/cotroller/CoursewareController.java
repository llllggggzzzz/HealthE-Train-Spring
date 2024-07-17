package com.conv.HealthETrain.cotroller;

import com.conv.HealthETrain.domain.Courseware;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.CoursewareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author liusg
 */
@RestController
@RequestMapping("/courseware")
@RequiredArgsConstructor
public class CoursewareController {
    private final CoursewareService coursewareService;

    /**
     * 根据章节id获取课件
     * @param sectionId
     * @return
     */
    @GetMapping("/{id}")
    public ApiResponse<Courseware> getCoursewareBySectionId(@PathVariable("id") Long sectionId) {
        Courseware courseware = coursewareService.getCoursewareBySectionId(sectionId);
        return ApiResponse.success(courseware);
    }

    /**
     * 添加课件
     * @param courseware
     * @return
     */
    @PostMapping("/new")
    public ApiResponse<Object> addCourseware(@RequestBody Courseware courseware) {
        courseware.setCoursewareId(null);
        boolean save = coursewareService.save(courseware);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "添加课件失败");
        }
    }
}
