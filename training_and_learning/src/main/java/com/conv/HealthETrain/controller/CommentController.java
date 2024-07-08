package com.conv.HealthETrain.controller;


import com.conv.HealthETrain.domain.DTO.CommentDTO;
import com.conv.HealthETrain.domain.POJP.Comment;
import com.conv.HealthETrain.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;


    /**
     * @description 根据sectionId查询用户评论并返回
     * @param id section id
     * @return 评论列表
     */
    @GetMapping("/section/{id}")
    public List<CommentDTO> getComments(@PathVariable("id") Long id) {
        List<Comment> comments = commentService.getCommentsBySectionId(id);

        for (Comment comment: comments) {
            Long userId = comment.getUserId();
            // 根据userID去查询用户信息

        }
    }

}
