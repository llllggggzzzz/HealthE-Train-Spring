package com.conv.HealthETrain.controller;


import cn.hutool.core.collection.CollUtil;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.client.VideoClient;
import com.conv.HealthETrain.domain.DTO.CommentDTO;
import com.conv.HealthETrain.domain.POJP.Comment;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.domain.Video;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    private final InformationPortalClient informationPortalClient;
    private final VideoClient videoClient;

    /**
     * @description 根据sectionId查询用户评论并返回
     * @param id section id
     * @return 评论列表
     */
    @GetMapping("/section/{id}")
    public ApiResponse<List<CommentDTO>> getComments(@PathVariable("id") Long id) {
        Video video = videoClient.getVideoById(id);
        if(video == null) {
            return  ApiResponse.success(new ArrayList<>());
        }

        Long sectionId = video.getSectionId();
        List<Comment> comments = commentService.getCommentsBySectionId(sectionId);
        log.info("得到评论列表： {}", comments);
        if (comments.isEmpty()) return ApiResponse.success();
        List<CommentDTO> commentDTOS = CollUtil.newArrayList();
        for (Comment comment: comments) {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setComment(comment);
            Long userId = comment.getUserId();
            // 根据userID去查询用户信息
            User userInfo = informationPortalClient.getUserInfo(userId);
            if(userInfo == null) {
                // 查询不到用户信息
                log.warn("查找到用户评论，但是未查找到用户信息");
            } else {
                commentDTO.setUsername(userInfo.getUsername());
                commentDTO.setCover(userInfo.getCover());
            }
            commentDTOS.add(commentDTO);
        }
        return ApiResponse.success(commentDTOS);
    }

    /**
     * @description 插入一条评论
     * @param newComment 前端传入的新的评论
     * @return 返回插入的评论，满足restful要求
     */
    @PostMapping("/new")
    public ApiResponse<Comment> addComment(@RequestBody Comment newComment) {
        newComment.setTime(new Date());
        boolean saved = commentService.save(newComment);
        if (saved) return ApiResponse.success(newComment);
        else return ApiResponse.error(ResponseCode.UNPROCESSABLE_ENTITY);
    }

}
