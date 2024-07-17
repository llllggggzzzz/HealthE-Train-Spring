package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.DTO.CreatPaperQuestionDTO;
import com.conv.HealthETrain.domain.DTO.ExamQuestionStatisticDTO;
import com.conv.HealthETrain.domain.EqOption;
import com.conv.HealthETrain.domain.ExamQuestion;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.mapper.EqOptionMapper;
import com.conv.HealthETrain.mapper.NoteMapper;
import com.conv.HealthETrain.service.ExamQuestionService;
import com.conv.HealthETrain.mapper.ExamQuestionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final NoteMapper noteMapper;
    private final EqOptionMapper eqOptionMapper;

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

    @Override
    public List<ExamQuestionStatisticDTO> getExamQuestionsStatisticByQbId(Long qbId) {
        // 查询所有对应的 ExamQuestion
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(new QueryWrapper<ExamQuestion>().eq("qb_id", qbId));
        // 封装结果到 ExamQuestionStatisticDTO
        List<ExamQuestionStatisticDTO> examQuestionStatisticDTOS = new ArrayList<>();
        for (ExamQuestion examQuestion : examQuestions) {
            ExamQuestionStatisticDTO dto = new ExamQuestionStatisticDTO();
            dto.setEqId(examQuestion.getEqId());
            dto.setEqTypeId(examQuestion.getEqTypeId());
            // 根据 noteId 查询 Note
            Note note = noteMapper.selectOne(new QueryWrapper<Note>().eq("note_id", examQuestion.getNoteId()));
            if (note != null) {
                dto.setNoteContent(note.getNoteContent());
            }
            examQuestionStatisticDTOS.add(dto);
        }
        return examQuestionStatisticDTOS;
    }

    // 批量删除
    @Override
    public void batchDeleteExamQuestions(Long qbId, List<Long> eqIds) {
        // 删除 examQuestion 表中对应的试题记录
        examQuestionMapper.delete(new QueryWrapper<ExamQuestion>()
                .eq("qb_id", qbId)
                .in("eq_id", eqIds));

        // 删除 note 表中对应的笔记记录
        noteMapper.delete(new QueryWrapper<Note>()
                .in("eq_id", eqIds));
    }
    @Override
    public List<CreatPaperQuestionDTO> getExamQuestionInfoByQBID(Long qbId) {
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(
                new QueryWrapper<ExamQuestion>().eq("qb_id", qbId));
        List<CreatPaperQuestionDTO> result = new ArrayList<>();
        for (ExamQuestion examQuestion : examQuestions) {
            CreatPaperQuestionDTO dto = new CreatPaperQuestionDTO();
            dto.setEqId(examQuestion.getEqId());
            dto.setAnswer(examQuestion.getAnswer());
            dto.setEqTypeId(examQuestion.getEqTypeId());

            Note note = noteMapper.selectById(examQuestion.getNoteId());
            if (note != null) {
                dto.setContent(note.getNoteContent());
            }
            if (examQuestion.getEqTypeId() == 1 || examQuestion.getEqTypeId() == 2) {
                List<EqOption> eqOptions = eqOptionMapper.selectList(
                        new QueryWrapper<EqOption>().eq("eq_id", examQuestion.getEqId()));
                List<String> options = new ArrayList<>();
                for (EqOption eqOption : eqOptions) {
                    options.add(eqOption.getEqA());
                    options.add(eqOption.getEqB());
                    options.add(eqOption.getEqC());
                    options.add(eqOption.getEqD());
                }
                dto.setOptions(options);
            }
            result.add(dto);
        }
        return result;
    }
}




