package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.DTO.QuestionBankSelectDTO;
import com.conv.HealthETrain.domain.QuestionBank;
import com.conv.HealthETrain.service.QuestionBankService;
import com.conv.HealthETrain.mapper.QuestionBankMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author flora
* @description 针对表【question_bank】的数据库操作Service实现
* @createDate 2024-07-07 11:52:15
*/
@Service
@AllArgsConstructor
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
    implements QuestionBankService{

    private final QuestionBankMapper questionBankMapper;
    @Override
    public List<QuestionBank> getAllQuestionBanks() {
        return questionBankMapper.selectList(null);
    }

    @Override
    public List<QuestionBankSelectDTO> getQuestionBankSelectDTOByLessonId(Long lessonId) {
        List<QuestionBankSelectDTO> questionBankSelectDTOs = new ArrayList<>();
        QueryWrapper<QuestionBank> wrapper = new QueryWrapper<>();
        wrapper.eq("lesson_id", lessonId);
        List<QuestionBank> questionBanks = questionBankMapper.selectList(wrapper);
        for (QuestionBank questionBank : questionBanks) {
            QuestionBankSelectDTO dto = new QuestionBankSelectDTO();
            dto.setQbId(questionBank.getQbId());
            dto.setQbTitle(questionBank.getQbTitle());
            questionBankSelectDTOs.add(dto);
        }
        return questionBankSelectDTOs;
    }
}




