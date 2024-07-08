package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Answer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.conv.HealthETrain.domain.Ask;

import java.util.List;

/**
* @author flora
* @description 针对表【answer】的数据库操作Service
* @createDate 2024-07-07 11:51:31
*/
public interface AnswerService extends IService<Answer> {
    Boolean addAnswerOfAsk(Answer answer);
    Answer findAnswerByAnswerId(Long answerId);
    Boolean updateLikesOfAnswer(Long answerId);
    Boolean subLikesOfAnswer(Long answerId);
    List<Answer> findAnswerListByAskId(Long askId);

}
