package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.QuestionBank;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【question_bank】的数据库操作Service
* @createDate 2024-07-07 11:52:15
*/
public interface QuestionBankService extends IService<QuestionBank> {
    // 获取题库信息列表
    List<QuestionBank> getAllQuestionBanks();

    List<QuestionBank> getQuestionBankByLessonId(Long lessonId);

    QuestionBank saveQuestionBank(QuestionBank questionBank);
}
