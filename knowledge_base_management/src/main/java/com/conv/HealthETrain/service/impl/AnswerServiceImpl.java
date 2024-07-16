package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Answer;
import com.conv.HealthETrain.domain.Ask;
import com.conv.HealthETrain.service.AnswerService;
import com.conv.HealthETrain.mapper.AnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【answer】的数据库操作Service实现
* @createDate 2024-07-07 11:51:31
*/
@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer>
    implements AnswerService{
    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public Boolean addAnswerOfAsk(Answer answer) {
        Answer newAnswer = new Answer();
        newAnswer.setNoteId(answer.getNoteId());
        newAnswer.setAskId(answer.getAskId());
        newAnswer.setLikes(answer.getLikes());
        return answerMapper.insert(newAnswer) > 0;
    }

    @Override
    public Answer findAnswerByAnswerId(Long answerId) {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("answer_id", answerId);
        return answerMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean updateLikesOfAnswer(Long answerId) {
        UpdateWrapper<Answer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("answer_id", answerId)
                .setSql("likes = likes + 1");
        return answerMapper.update(updateWrapper) > 0;
    }

    @Override
    public Boolean subLikesOfAnswer(Long answerId) {
        UpdateWrapper<Answer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("answer_id", answerId)
                .setSql("likes = likes - 1");
        return answerMapper.update(updateWrapper) > 0;
    }

    @Override
    public List<Answer> findAnswerListByAskId(Long askId) {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ask_id", askId);
        return answerMapper.selectList(queryWrapper);
    }
}




