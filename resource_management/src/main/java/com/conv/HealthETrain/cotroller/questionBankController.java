package com.conv.HealthETrain.cotroller;

import com.conv.HealthETrain.client.ExamQuestionClient;
import com.conv.HealthETrain.client.LessonClient;
import com.conv.HealthETrain.domain.*;
import com.conv.HealthETrain.domain.DTO.ll.ExamQuestionDTO;
import com.conv.HealthETrain.domain.DTO.LessonDetailInfoDTO;
import com.conv.HealthETrain.domain.DTO.QuestionBankDTO;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.domain.DTO.QuestionBankSelectDTO;
import com.conv.HealthETrain.domain.QuestionBank;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.QuestionBankService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questionBank")
public class questionBankController {
    private final QuestionBankService questionBankService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    private final LessonClient lessonClient;

    private final ExamQuestionClient examQuestionClient;

    @GetMapping("/all")
    List<QuestionBank> getAllQuestionBank() throws JsonProcessingException {
        String redisKey = "allQuestionBanks";
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            // 如果Redis中有缓存数据，直接返回缓存数据
            return mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, QuestionBank.class));
        } else {
            // 如果Redis中没有缓存数据，从数据库中获取数据
            List<QuestionBank> questionBanks = questionBankService.getAllQuestionBanks();
            // 将数据转换为JSON字符串并存入Redis
            stringRedisTemplate.opsForValue().set(redisKey, mapper.writeValueAsString(questionBanks));
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return questionBanks;
        }
    }

    /**
     * 新建题库
     * @param questionBank
     */
    @PostMapping("/new")
    public ApiResponse<Object> addQuestionBank(@RequestBody QuestionBank questionBank) {
        QuestionBank qb = questionBankService.saveQuestionBank(questionBank);

        if (qb != null) {
            QuestionBankDTO questionBankDTO = new QuestionBankDTO();

            // questionBank部分
            questionBankDTO.setQbId(questionBank.getQbId());
            questionBankDTO.setQbTitle(questionBank.getQbTitle());
            questionBankDTO.setCreateTime(questionBank.getCreateTime());
            questionBankDTO.setLessonId(questionBank.getLessonId());

            // Lesson部分
            ApiResponse<Lesson> lessonById = lessonClient.getLessonById(questionBank.getLessonId().toString());
            Lesson lesson = lessonById.getData();
            questionBankDTO.setLessonName(lesson.getLessonName());
            questionBankDTO.setLessonType(lesson.getLessonType());
            questionBankDTO.setStartTime(lesson.getStartTime());
            questionBankDTO.setEndTime(lesson.getEndTime());
            questionBankDTO.setLessonCover(lesson.getLessonCover());

            // ExamQuestion部分
            questionBankDTO.setExamQuestions(new ArrayList<>());

            return ApiResponse.success(ResponseCode.SUCCEED, "新建题库成功", questionBankDTO);
        }

        return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "新建题库失败");
    }

    /**
     * 根据课程ID获取题库
     * @param lessonId
     * @return
     */
    @GetMapping("/lesson/{id}")
    public ApiResponse<List<QuestionBankDTO>> getQuestionBankByLessonId(@PathVariable("id") String lessonId) {
        List<QuestionBank> questionBanks = questionBankService.getQuestionBankByLessonId(Long.parseLong(lessonId));

        List<QuestionBankDTO> questionBankDTOS = new ArrayList<>();
        for (QuestionBank questionBank : questionBanks) {
            QuestionBankDTO questionBankDTO = new QuestionBankDTO();

            // questionBank部分
            questionBankDTO.setQbId(questionBank.getQbId());
            questionBankDTO.setQbTitle(questionBank.getQbTitle());
            questionBankDTO.setCreateTime(questionBank.getCreateTime());
            questionBankDTO.setLessonId(questionBank.getLessonId());

            // Lesson部分
            ApiResponse<Lesson> lessonById = lessonClient.getLessonById(questionBank.getLessonId().toString());
            Lesson lesson = lessonById.getData();
            questionBankDTO.setLessonName(lesson.getLessonName());
            questionBankDTO.setLessonType(lesson.getLessonType());
            questionBankDTO.setStartTime(lesson.getStartTime());
            questionBankDTO.setEndTime(lesson.getEndTime());
            questionBankDTO.setLessonCover(lesson.getLessonCover());

            // ExamQuestion部分
            ApiResponse<List<ExamQuestionDTO>> response = examQuestionClient.getExamQuestionsDetailByQuestionBankId(questionBank.getQbId());
            List<ExamQuestionDTO> data = response.getData();
            questionBankDTO.setExamQuestions(data);

            questionBankDTOS.add(questionBankDTO);
        }

        return ApiResponse.success(ResponseCode.SUCCEED, "成功", questionBankDTOS);
    }

    /**
     * 根据教师ID获取题库
     * @param tdId
     * @return
     */
    @GetMapping("/teacher/{id}")
    public ApiResponse<List<QuestionBankDTO>> getQuestionBankByTdId(@PathVariable("id") String tdId) {
        ApiResponse<List<LessonDetailInfoDTO>> allTeacherLessons = lessonClient.getAllTeacherLessons(Long.parseLong(tdId));

        List<QuestionBankDTO> questionBankDTOS = new ArrayList<>();
        for (LessonDetailInfoDTO lessonDetailInfoDTO : allTeacherLessons.getData()) {
            Long lessonId = lessonDetailInfoDTO.getLessonId();

            List<QuestionBank> questionBanks = questionBankService.getQuestionBankByLessonId(lessonId);
            for (QuestionBank questionBank : questionBanks) {
                QuestionBankDTO questionBankDTO = new QuestionBankDTO();

                // questionBank部分
                questionBankDTO.setQbId(questionBank.getQbId());
                questionBankDTO.setQbTitle(questionBank.getQbTitle());
                questionBankDTO.setCreateTime(questionBank.getCreateTime());
                questionBankDTO.setLessonId(questionBank.getLessonId());

                // Lesson部分
                ApiResponse<Lesson> lessonById = lessonClient.getLessonById(questionBank.getLessonId().toString());
                Lesson lesson = lessonById.getData();
                questionBankDTO.setLessonName(lesson.getLessonName());
                questionBankDTO.setLessonType(lesson.getLessonType());
                questionBankDTO.setStartTime(lesson.getStartTime());
                questionBankDTO.setEndTime(lesson.getEndTime());
                questionBankDTO.setLessonCover(lesson.getLessonCover());

                // ExamQuestion部分
                ApiResponse<List<ExamQuestionDTO>> response = examQuestionClient.getExamQuestionsDetailByQuestionBankId(questionBank.getQbId());
                List<ExamQuestionDTO> data = response.getData();
                questionBankDTO.setExamQuestions(data);

                questionBankDTOS.add(questionBankDTO);
            }
        }

        return ApiResponse.success(ResponseCode.SUCCEED, "成功", questionBankDTOS);
    }

    // TODO 给题库批量新增题目

    /**
     * 给题库新增题目
     * @param qbId
     * @param examQuestion
     * @return
     */
    @PostMapping("/question/{id}")
    public ApiResponse<ExamQuestionDTO> addQuestionToQuestionBank(@PathVariable("id") Long qbId, @RequestBody ExamQuestionDTO examQuestion) {
        // 要更新的表有 ExamQuestion表 EqOption表 Note表
        // ExamQuestion
        ExamQuestion eq = new ExamQuestion();
        ApiResponse<Long> eqTypeIdResponse = examQuestionClient.getEqTypeIdByEqTypeName(examQuestion.getEqType());
        Long eqTypeId = eqTypeIdResponse.getData();

        eq.setAnswer(examQuestion.getAnswer());
        eq.setQbId(qbId);
        eq.setEqTypeId(eqTypeId);
        // 先设置为null，后续再更新
        eq.setNoteId(null);

        ApiResponse<ExamQuestion> examQuestionResponse = examQuestionClient.addExamQuestion(eq);
        eq = examQuestionResponse.getData();

        Long eqId = eq.getEqId();
        // EqOption
        if (eqTypeId == 1 || eqTypeId == 2) {
            // 是选择题
            EqOption eqOption = new EqOption();
            eqOption.setEqId(eqId);
            eqOption.setEqA(examQuestion.getEqA());
            eqOption.setEqB(examQuestion.getEqB());
            eqOption.setEqC(examQuestion.getEqC());
            eqOption.setEqD(examQuestion.getEqD());

            examQuestionClient.addOption(eqOption);
        }

        // Note
        Note note = new Note();
        note.setEqId(eqId);
        note.setNoteContent(examQuestion.getNoteContent());
        ApiResponse<Note> noteApiResponse = examQuestionClient.addNote(note);

        eq.setNoteId(noteApiResponse.getData().getNoteId());
        examQuestionClient.updateExamQuestion(eq);

        examQuestion.setEqId(eqId);
        return ApiResponse.success(examQuestion);
    }

    /**
     * 从题库删除题目
     * @param qbId
     * @param eqIds
     * @return
     */
    @DeleteMapping("/question/{id}")
    public ApiResponse<Object> deleteQuestionFromQuestionBank(@PathVariable("id") Long qbId, @RequestBody List<Long> eqIds) {
        // 要更新的表有 ExamQuestion表 EqOption表 Note表
        for (Long eqId : eqIds) {
            ExamQuestion eq = examQuestionClient.getExamQuestionByEqId(eqId).getData();
            // EqOption
            examQuestionClient.deleteOptionByEqId(eqId);

            // ExamQuestion
            examQuestionClient.deleteExamQuestion(eqId);

            // Note
            examQuestionClient.deleteNote(eq.getNoteId());
        }

        return ApiResponse.success();
    }

    /**
     * 更新题库题目
     * @param qbId
     * @param examQuestion
     * @return
     */
    @PutMapping("/question/{id}")
    public ApiResponse<ExamQuestionDTO> updateQuestionFromQuestionBank(@PathVariable("id") Long qbId, @RequestBody ExamQuestionDTO examQuestion) {
        // 要更新的表有 ExamQuestion表 EqOption表 Note表
        // ExamQuestion
        ExamQuestion eq = examQuestionClient.getExamQuestionByEqId(examQuestion.getEqId()).getData();

        eq.setAnswer(examQuestion.getAnswer());
        examQuestionClient.updateExamQuestion(eq);

        Long eqId = eq.getEqId();
        // EqOption
        if (eq.getEqTypeId() == 1 || eq.getEqTypeId() == 2) {
            // 是选择题
            EqOption eqOption = new EqOption();

            eqOption.setEqId(eqId);
            eqOption.setEqA(examQuestion.getEqA());
            eqOption.setEqB(examQuestion.getEqB());
            eqOption.setEqC(examQuestion.getEqC());
            eqOption.setEqD(examQuestion.getEqD());

            examQuestionClient.updateOptionByEqId(eqOption);
        }

        // Note
        Note note = new Note();
        note.setNoteId(eq.getNoteId());
        note.setEqId(eqId);
        note.setNoteContent(examQuestion.getNoteContent());
        examQuestionClient.updateNote(note);

        return ApiResponse.success(examQuestion);
    }


    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<QuestionBankSelectDTO>> getQuestionBankInfoByLessonId(@PathVariable("lessonId")Long lessonId) throws JsonProcessingException {
        String redisKey = "lesson:" + lessonId + ":questionBankInfo";
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            List<QuestionBankSelectDTO> cachedQuestionBanks = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, QuestionBankSelectDTO.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", cachedQuestionBanks);
        } else {
            List<QuestionBankSelectDTO> questionBankSelectDTOS = questionBankService.getQuestionBankSelectDTOByLessonId(lessonId);
            String jsonQuestionBanks = mapper.writeValueAsString(questionBankSelectDTOS);
            stringRedisTemplate.opsForValue().set(redisKey, jsonQuestionBanks);
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", questionBankSelectDTOS);
        }
    }
}
