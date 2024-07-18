package com.conv.HealthETrain.cotroller;

import cn.hutool.core.io.FileUtil;
import com.conv.HealthETrain.domain.Courseware;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.factory.JellyfinFactory;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.CoursewareService;
import com.conv.HealthETrain.utils.JellyfinUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping("/pdf/{pdfAddress}")
    public ApiResponse<String> getPDFUrl(@PathVariable("pdfAddress") String pdfAddress) {
        // 创建媒体库
        JellyfinUtil jellyfinUtil = JellyfinFactory.build(JellyfinFactory.configPath);

        List<String> allMediaLibrary = jellyfinUtil.getAllMediaLibrary("");
        if(!allMediaLibrary.contains("pdf")) {
            jellyfinUtil.createMediaLibrary("/video/pdf", "pdf", "");
        }

        // 上传到media
        String name = FileUtil.getPrefix(pdfAddress);
        jellyfinUtil.saveFile(pdfAddress, "pdf", true, true);
        String streamingVideoUrl = jellyfinUtil.findStreamingVideoUrl(name, "pdf", "");
        return ApiResponse.success(streamingVideoUrl);

    }

}
