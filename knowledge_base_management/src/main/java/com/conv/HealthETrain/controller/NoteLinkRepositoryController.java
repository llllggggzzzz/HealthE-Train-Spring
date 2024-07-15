package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.NoteLinkRepository;
import com.conv.HealthETrain.domain.Repository;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.NoteLinkRepositoryService;
import com.conv.HealthETrain.service.RepositoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.conv.HealthETrain.enums.ResponseCode.NOT_FOUND;

@RestController
@AllArgsConstructor
@RequestMapping("/noteLinkRepository")
@Slf4j
public class NoteLinkRepositoryController {
    private final NoteLinkRepositoryService noteLinkRepositoryService;
    private final RepositoryService repositoryService;
    /**
     * @Description: 获取笔记所属的知识库
     * @Param:
     * @return:
     * @Author: flora
     * @Date: 2024/7/10
     */
    @GetMapping("/ofRepository/{noteId}")
    public ApiResponse<Repository> getRepositoryOfNote(@PathVariable Long noteId){
        NoteLinkRepository noteLinkRepository = noteLinkRepositoryService.findNoteLinkRepositoryByNoteId(noteId);
        Repository repository = repositoryService.getById(noteLinkRepository.getRepositoryId());
        if(repository != null){
            log.info("获取笔记" + noteId + "所属知识库成功！");
            return ApiResponse.success(repository);
        }else{
            log.info("获取笔记" + noteId + "所属知识库失败！");
            return ApiResponse.error(NOT_FOUND);
        }
    }
}
