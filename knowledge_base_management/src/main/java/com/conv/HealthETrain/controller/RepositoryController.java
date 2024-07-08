package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.Repository;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.RepositoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.conv.HealthETrain.enums.ResponseCode.NOT_IMPLEMENTED;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/repository")
@Slf4j
public class RepositoryController {

    private final RepositoryService repositoryService;

    /**
    * @Description: 获取对应用户的知识库
    * @Param: userId 用户ID
    * @return: List<Repository>
    * @Author: flora
    * @Date: 2024/7/8
    */
    @GetMapping("/{userId}")
    public ApiResponse<List<Repository>> getRepositoryListByUserId(@PathVariable Long userId){
        List<Repository> repositoryList = repositoryService.findRepositoryListByUserId(userId);
        if(repositoryList != null){
            log.info("获取用户 " + userId + " 的知识库成功");
            return ApiResponse.success(repositoryList);
        }else{
            log.info("对应用户没有知识库");
            return ApiResponse.success();
        }
    }

    /**
    * @Description: 新建知识库
    * @Param: repository 知识库
    * @return: Boolean 新建是否成功
    * @Author: flora
    * @Date: 2024/7/8
    */
    @PostMapping("")
    public ApiResponse<Boolean> postRepository(@RequestBody Repository repository){
        Boolean isSuccess = repositoryService.addRepository(repository);
        if(isSuccess){
            log.info("新建知识库成功");
            return ApiResponse.success(true);
        }else{
            log.info("新建知识库失败");
            return ApiResponse.error(NOT_IMPLEMENTED);
        }
    }
}
