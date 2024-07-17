package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.DTO.ExamQuestionDTO;
import com.conv.HealthETrain.domain.DTO.CreatPaperQuestionDTO;
import com.conv.HealthETrain.domain.DTO.ExamQuestionStatisticDTO;
import com.conv.HealthETrain.domain.EqOption;
import com.conv.HealthETrain.domain.EqType;
import com.conv.HealthETrain.domain.ExamQuestion;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.EqOptionService;
import com.conv.HealthETrain.service.EqTypeService;
import com.conv.HealthETrain.service.ExamQuestionService;
import com.conv.HealthETrain.service.NoteService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/examQuestion")
public class ExamQuestionController {

    private ExamQuestionService examQuestionService;

    private EqOptionService eqOptionService;

    private EqTypeService eqTypeService;

    private NoteService noteService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    // 根据题型来统计五类题型的数量
    @GetMapping("/statistic")
    public ApiResponse<Map<String, Integer>> getExamQuestionsStatistic() throws JsonProcessingException {
        String cacheKey = "examQuestionsStatistic";
        String cachedData = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            Map<String, Integer> statistic = mapper.readValue(cachedData, Map.class);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", statistic);
        }
        Map<String, Integer> statistic = examQuestionService.countExamQuestionsByType();
        String jsonData = mapper.writeValueAsString(statistic);
        stringRedisTemplate.opsForValue().set(cacheKey, jsonData, 10, TimeUnit.MINUTES);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", statistic);
    }

    // 查询对应题目的试题(简单信息,非完整)
    @GetMapping("/questionBank/{id}/simpleInfo")
    private ApiResponse<List<ExamQuestionStatisticDTO>> getExamQuestionsByQuestionBankId(@PathVariable("id") Long qbId) throws JsonProcessingException {
        String redisKey = "questionBank:" + qbId + ":simpleInfo";
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            List<ExamQuestionStatisticDTO> cachedExamQuestions = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, ExamQuestionStatisticDTO.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", cachedExamQuestions);
        } else {
            List<ExamQuestionStatisticDTO> examQuestionStatisticDTOS = examQuestionService.getExamQuestionsStatisticByQbId(qbId);
            String jsonExamQuestions = mapper.writeValueAsString(examQuestionStatisticDTOS);
            stringRedisTemplate.opsForValue().set(redisKey, jsonExamQuestions);
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", examQuestionStatisticDTOS);
        }
    }

    // 批量删除题库里的试题,body里面携带试题id列表
    @DeleteMapping("/questionBank/{id}")
    private ApiResponse<Object> batchDeleteExamQuestions(@PathVariable("id") Long qbId,
                                                         @RequestBody List<Long> eqIds){
        examQuestionService.batchDeleteExamQuestions(qbId,eqIds);
        return ApiResponse.success(ResponseCode.SUCCEED,"成功");
    }

    /**
     * 根据题库id获取题目的详细信息
     * @param qbId
     * @return
     */
    @GetMapping("/questionBank/{id}")
    public ApiResponse<List<ExamQuestionDTO>> getExamQuestionsDetailByQuestionBankId(@PathVariable("id") Long qbId) {
        // 需要拼接 ExamQuestion表 EqOption表 Note表 EqType表
        List<ExamQuestionDTO> examQuestionDTOS = new ArrayList<>();

        List<ExamQuestion> examQuestions = examQuestionService.getExamQuestionsByQbId(qbId);
        for (ExamQuestion examQuestion : examQuestions) {
            ExamQuestionDTO examQuestionDTO = new ExamQuestionDTO();

            // ExamQuestion表
            examQuestionDTO.setEqId(examQuestion.getEqId());
            examQuestionDTO.setAnswer(examQuestion.getAnswer());

            // EqType表
            String eqTypeName = eqTypeService.getById(examQuestion.getEqTypeId()).getEqTypeName();
            examQuestionDTO.setEqType(eqTypeName);

            // EqOption表 当题目类型为选择题时才有选项
            if (examQuestion.getEqTypeId() == 1 || examQuestion.getEqTypeId() == 2) {
                EqOption eqOption = eqOptionService.getByEqId(examQuestion.getEqId());
                examQuestionDTO.setEqA(eqOption.getEqA());
                examQuestionDTO.setEqB(eqOption.getEqB());
                examQuestionDTO.setEqC(eqOption.getEqC());
                examQuestionDTO.setEqD(eqOption.getEqD());
            } else {
                examQuestionDTO.setEqA(null);
                examQuestionDTO.setEqB(null);
                examQuestionDTO.setEqC(null);
                examQuestionDTO.setEqD(null);
            }

            // Note表
            String noteContent = noteService.getById(examQuestion.getNoteId()).getNoteContent();
            examQuestionDTO.setNoteContent(noteContent);

            examQuestionDTOS.add(examQuestionDTO);
        }

        return ApiResponse.success(ResponseCode.SUCCEED, "成功", examQuestionDTOS);
    }

    /**
     * 新增题目
     * @param examQuestion
     * @return
     */
    @PostMapping("/new")
    public ApiResponse<ExamQuestion> addExamQuestion(@RequestBody ExamQuestion examQuestion) {
        ExamQuestion eq = examQuestionService.insertExamQuestion(examQuestion);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", eq);
    }

    /**
     * 更新题目
     * @param examQuestion
     * @return
     */
    @PutMapping("/update")
    public ApiResponse<ExamQuestion> updateExamQuestion(@RequestBody ExamQuestion examQuestion) {
        ExamQuestion eq = examQuestionService.updateExamQuestion(examQuestion);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", eq);
    }

    /**
     * 删除题目
     * @param eqId
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Object> deleteExamQuestion(@PathVariable("id") Long eqId) {
        examQuestionService.removeById(eqId);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功");
    }

    /**
     * 新增笔记
     * @param note
     * @return
     */
    @PostMapping("/new/note")
    public ApiResponse<Note> addNote(@RequestBody Note note) {
        Note n = noteService.insertNote(note);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", n);
    }

    /**
     * 更新笔记
     * @param note
     * @return
     */
    @PutMapping("/update/note")
    public ApiResponse<Note> updateNote(@RequestBody Note note) {
        Note n = noteService.updateNote(note);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", n);
    }

    /**
     * 删除笔记
     * @param noteId
     * @return
     */
    @DeleteMapping("/delete/note/{id}")
    public ApiResponse<Object> deleteNote(@PathVariable("id") Long noteId) {
        noteService.removeById(noteId);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功");
    }

    /**
     * 新增选项
     * @param eqOption
     * @return
     */
    @PostMapping("/new/option")
    public ApiResponse<EqOption> addOption(@RequestBody EqOption eqOption) {
        EqOption eq = eqOptionService.insertEqOption(eqOption);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", eq);
    }

    /**
     * 更新选项
     * @param eqOption
     * @return
     */
    @PostMapping("/update/option")
    public ApiResponse<EqOption> updateOptionByEqId(@RequestBody EqOption eqOption) {
        EqOption eq = eqOptionService.updateOptionByEqId(eqOption);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", eq);
    }

    /**
     * 删除选项
     * @param eqId
     * @return
     */
    @DeleteMapping("/delete/option/{id}")
    public ApiResponse<Object> deleteOptionByEqId(@PathVariable("id") Long eqId) {
        eqOptionService.deleteOptionByEqId(eqId);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功");
    }


    /**
     * 根据题型名获取题型id
     * @param eqTypeName
     * @return
     */
    @GetMapping("/eqType/name")
    public ApiResponse<Long> getEqTypeIdByEqTypeName(@RequestParam String eqTypeName) {
        EqType eqType = eqTypeService.getEqTypeIdByEqTypeName(eqTypeName);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", eqType.getEqTypeId());
    }

    /**
     * 根据题目id获取题目
     * @param eqId
     * @return
     */
    @GetMapping("{id}")
    public ApiResponse<ExamQuestion> getExamQuestionByEqId(@PathVariable("id") Long eqId) {
        ExamQuestion examQuestion = examQuestionService.getById(eqId);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", examQuestion);
    }


    @GetMapping("/questionBank/{id}/createPaper")
    public ApiResponse<List<CreatPaperQuestionDTO>> getCreatePaperQuestionByQbId(@PathVariable("id") Long qbId) throws JsonProcessingException {
        String redisKey = "questionBank:" + qbId + ":createPaper";
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            List<CreatPaperQuestionDTO> cachedQuestions = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, CreatPaperQuestionDTO.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", cachedQuestions);
        } else {
            List<CreatPaperQuestionDTO> createPaperQuestionDTOS = examQuestionService.getExamQuestionInfoByQBID(qbId);
            String jsonQuestions = mapper.writeValueAsString(createPaperQuestionDTOS);
            stringRedisTemplate.opsForValue().set(redisKey, jsonQuestions);
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", createPaperQuestionDTOS);
        }
    }
}
