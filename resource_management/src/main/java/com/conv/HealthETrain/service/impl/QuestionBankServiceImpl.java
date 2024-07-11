package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.QuestionBank;
import com.conv.HealthETrain.service.QuestionBankService;
import com.conv.HealthETrain.mapper.QuestionBankMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}




