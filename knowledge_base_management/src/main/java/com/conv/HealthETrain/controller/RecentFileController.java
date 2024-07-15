package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.RecentFile;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.RecentFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/recentFile")
@Slf4j
public class RecentFileController {
    private final RecentFileService recentFileService;

    /**
    * @Description: 插入访问记录
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/10
    */
    @PostMapping("")
    public ApiResponse<Boolean> postRecentFileRecord(@RequestBody RecentFile recentFile){
        Boolean isSuccess = recentFileService.insertRecentFile(recentFile);
        if(isSuccess){
            log.info("插入访问记录" + recentFile.getRfId() + "成功");
            return ApiResponse.success(true);
        }else{
            log.info("插入失败");
            return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED);
        }
    }
    /**
    * @Description: 更新或者插入访问记录
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/10
    */
    @PostMapping("/updateOrInsert")
    public ApiResponse<Boolean> updateRecentFileRecord(@RequestBody RecentFile recentFile){
        // 检查是否已有记录
        RecentFile existingRecord = recentFileService.lambdaQuery()
                .eq(RecentFile::getUserId, recentFile.getUserId())
                .eq(RecentFile::getNoteId, recentFile.getNoteId())
                .one();
        if(existingRecord != null){
            //更新操作
            Boolean isSuccess = recentFileService.updateRecentFile(recentFile);
            if(isSuccess){
                log.info("更新访问记录" + recentFile.getRfId() + "成功");
                return ApiResponse.success(true);
            }else{
                log.info("更新失败");
                return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED);
            }
        }else{
            Boolean isSuccess = recentFileService.insertRecentFile(recentFile);
            if(isSuccess){
                log.info("插入访问记录" + recentFile.getRfId() + "成功");
                return ApiResponse.success(true);
            }else{
                log.info("插入失败");
                return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED);
            }
        }
    }

}
