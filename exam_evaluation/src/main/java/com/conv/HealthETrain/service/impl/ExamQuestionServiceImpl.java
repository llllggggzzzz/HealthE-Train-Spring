package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.ExamQuestion;
import com.conv.HealthETrain.service.ExamQuestionService;
import com.conv.HealthETrain.mapper.ExamQuestionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author flora
* @description 针对表【exam_question】的数据库操作Service实现
* @createDate 2024-07-07 11:47:43
*/
@Service
@AllArgsConstructor
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion>
    implements ExamQuestionService{

    private final ExamQuestionMapper examQuestionMapper;

    // 根据题型来统计五类题型的数量
    @Override
    public Map<String, Integer> countExamQuestionsByType() {
        // 查询不同eqTypeId的题目数量
        QueryWrapper<ExamQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("eq_type_id", "count(*) as count");
        queryWrapper.groupBy("eq_type_id");
        List<Map<String, Object>> results = examQuestionMapper.selectMaps(queryWrapper);

        // 转换结果为Map<String, Integer>
        Map<String, Integer> questionCountMap = new HashMap<>();
        for (Map<String, Object> result : results) {
            String eqTypeId = result.get("eq_type_id").toString();
            Integer count = Integer.parseInt(result.get("count").toString());
            questionCountMap.put(eqTypeId, count);
        }
        return questionCountMap;
    }
}




