package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.DTO.AddPaperDTO;
import com.conv.HealthETrain.domain.DTO.CreatPaperQuestionDTO;
import com.conv.HealthETrain.domain.Exam;
import com.conv.HealthETrain.domain.ExamEqtypeScore;
import com.conv.HealthETrain.domain.Paper;
import com.conv.HealthETrain.domain.PaperLinkQuestion;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ExamEqtypeScoreService;
import com.conv.HealthETrain.service.ExamService;
import com.conv.HealthETrain.service.PaperLinkQuestionService;
import com.conv.HealthETrain.service.PaperService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/paper")
@AllArgsConstructor
@Slf4j
public class PaperController {
    private final PaperService paperService;
    private final PaperLinkQuestionService paperLinkQuestionService;
    private final ExamService examService;
    private final ExamEqtypeScoreService examEqtypeScoreService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/add")
    public ApiResponse<String> addPaperAndPublishExam(@RequestBody AddPaperDTO addPaperDTO){
        try {
            Paper paper = new Paper();
            paper.setPaperTitle(addPaperDTO.getPaperTitle());
            paper.setSumScore(addPaperDTO.getSumScore());
            paperService.save(paper);
            Long paperId = paper.getPaperId();

            List<Long> questions = addPaperDTO.getQuestions();
            for (Long questionId : questions) {
                PaperLinkQuestion link = new PaperLinkQuestion();
                link.setPaperId(paperId);
                link.setExamQuestionId(questionId);
                paperLinkQuestionService.save(link);
            }

            Exam exam = new Exam();
            exam.setPaperId(paperId);
            exam.setCreatorId(addPaperDTO.getCreatorId());
            exam.setLessionId(addPaperDTO.getLessonId());
            exam.setDuration(addPaperDTO.getDuration());
            exam.setLevel(addPaperDTO.getLevel());
            exam.setPassScore(addPaperDTO.getPassScore());
            exam.setRetryTimes(addPaperDTO.getRetryTimes());
            exam.setStartTime(addPaperDTO.getStartTime());
            exam.setEndTime(addPaperDTO.getEndTime());
            exam.setExamName(addPaperDTO.getExamName());
            examService.save(exam);
            Long examId = exam.getExamId();

            List<Integer> scores = addPaperDTO.getScores();
            for (int i = 0; i < scores.size(); i++) {
                Integer score = scores.get(i);
                if (score != 0) {
                    ExamEqtypeScore ees = new ExamEqtypeScore();
                    ees.setExamId(examId);
                    ees.setEqTypeId((long) (i + 1));
                    ees.setScore(score);
                    examEqtypeScoreService.save(ees);
                }
            }
            return ApiResponse.success(ResponseCode.SUCCEED,"成功","成功");
        } catch (Exception e) {
            log.info(String.valueOf(e));
            return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED,"失败","失败");
        }
    }

    @GetMapping("/all")
    public ApiResponse<List<Paper>> getAllListPaper() throws JsonProcessingException {
        String redisKey = "allPaper";
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            List<Paper> cachedPapers = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, Paper.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", cachedPapers);
        } else {
            List<Paper> createPapers = paperService.getAllPaper();
            String jsonPapers = mapper.writeValueAsString(createPapers);
            stringRedisTemplate.opsForValue().set(redisKey, jsonPapers);
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", createPapers);
        }
    }

}
