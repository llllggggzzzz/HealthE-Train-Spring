package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.DTO.CreatPaperQuestionDTO;
import com.conv.HealthETrain.domain.DTO.ExamQuestionStatisticDTO;
import com.conv.HealthETrain.domain.ExamQuestion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author flora
* @description 针对表【exam_question】的数据库操作Service
* @createDate 2024-07-07 11:47:43
*/
public interface ExamQuestionService extends IService<ExamQuestion> {

    // 根据题型来统计五类题型的数量
    Map<String,Integer> countExamQuestionsByType();
    // 获取题库中的题目信息
    List<ExamQuestionStatisticDTO> getExamQuestionsStatisticByQbId(Long qbId);
    // 批量删除题库中的一些题目
    void batchDeleteExamQuestions(Long qbId, List<Long> eqIds);

    List<CreatPaperQuestionDTO> getExamQuestionInfoByQBID(Long qbId);

}
