package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Comment;
import com.conv.HealthETrain.service.CommentService;
import com.conv.HealthETrain.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author john
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2024-07-05 17:58:44
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




