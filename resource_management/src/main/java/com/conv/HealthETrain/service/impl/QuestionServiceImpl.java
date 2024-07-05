package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Question;
import com.conv.HealthETrain.service.QuestionService;
import com.conv.HealthETrain.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author john
* @description 针对表【question】的数据库操作Service实现
* @createDate 2024-07-05 17:57:57
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




