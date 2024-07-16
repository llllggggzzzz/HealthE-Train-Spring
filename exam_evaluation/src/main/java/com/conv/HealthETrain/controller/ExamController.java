package com.conv.HealthETrain.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.client.LessonClient;
import com.conv.HealthETrain.domain.*;
import com.conv.HealthETrain.domain.DTO.ExamDTO;
import com.conv.HealthETrain.domain.DTO.ExamQuestionDTO;
import com.conv.HealthETrain.domain.DTO.LessonInfoDTO;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exam")
@AllArgsConstructor
@Slf4j
public class ExamController {


    private final LessonClient lessonClient;

    private final ExamService examService ;

    private final InformationPortalClient informationPortalClient;

    private final PaperLinkQuestionService paperLinkQuestionService;

    private final ExamQuestionService examQuestionService;

    private final EqOptionService eqOptionService;

    private final NoteService noteService;

    private final ExamEqtypeScoreService examEqtypeScoreService;

    private final StringRedisTemplate redisTemplate;


    @GetMapping("/list/{userId}")
    public ApiResponse<List<ExamDTO>> getExamInfoList(@PathVariable("userId") Long userId) {
        ApiResponse<List<LessonInfoDTO>> lessonsResponse = lessonClient.getLessons(userId);
        List<ExamDTO> examDTOList = new ArrayList<>();
        if(lessonsResponse != null) {
            List<LessonInfoDTO> lessons = lessonsResponse.getData();
            List<Long> lessonIds = CollUtil.getFieldValues(lessons, "lessonId", Long.class);
            LambdaQueryWrapper<Exam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(Exam::getLessionId, lessonIds);
            List<Exam> examList = examService.list(lambdaQueryWrapper);
            if(examList != null) {
                // 遍历examList, 查询teacherName和teacherCover
                for (Exam exam : examList) {
                    ExamDTO examDTO = new ExamDTO(exam);
                    Long creatorId = exam.getCreatorId();
                    TeacherDetail teacherDetail = informationPortalClient.getTeacherDetailById(creatorId);
                    if(teacherDetail != null) {
                        examDTO.setTeacherName(teacherDetail.getRealName());
                        Long teacherDetailUserId = teacherDetail.getUserId();
                        User userInfo = informationPortalClient.getUserInfo(teacherDetailUserId);
                        if(userInfo != null) {
                            examDTO.setTeacherCover(userInfo.getCover());
                        }
                    }
                    examDTOList.add(examDTO);
                }
            }
        }
        return ApiResponse.success(examDTOList);
    }


    @GetMapping("/info/{examId}")
    public ApiResponse<Exam> getExamInfo(@PathVariable("examId") Long examId) {
        Exam exam = examService.getById(examId);
        return ApiResponse.success(exam);
    }

    @GetMapping("/{examId}")
    public ApiResponse<List<ExamQuestionDTO>> getExamQuestion(@PathVariable("examId") Long examId) {
        // 查询题目类型
        List<ExamQuestionDTO> examQuestionDTOArrayList = new ArrayList<>();
        List<String> strList = redisTemplate.opsForList().range("exam:" + examId, 0, -1);
        if(strList != null && !strList.isEmpty()) {
            examQuestionDTOArrayList = strList.stream()
                    .map(str -> JSONUtil.toBean(str, ExamQuestionDTO.class))
                    .collect(Collectors.toList());
            log.info("检测到redis中存在缓存: {}", examQuestionDTOArrayList);
            return ApiResponse.success(examQuestionDTOArrayList);
        }
        Exam exam = examService.getById(examId);
        if(exam == null) {
            return ApiResponse.error(ResponseCode.NOT_FOUND);
        }
        Long paperId = exam.getPaperId();
        LambdaQueryWrapper<ExamEqtypeScore> examEqtypeScoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examEqtypeScoreLambdaQueryWrapper.eq(ExamEqtypeScore::getExamId, examId);
        List<ExamEqtypeScore> examEqtypeScoreList = examEqtypeScoreService.list(examEqtypeScoreLambdaQueryWrapper);
        Map<Long, Integer> typeScoreMap = examEqtypeScoreList.stream().
                collect(Collectors.toMap(ExamEqtypeScore::getEqTypeId,
                ExamEqtypeScore::getScore));
        log.info("查询到分数映射: {}", typeScoreMap);
        LambdaQueryWrapper<PaperLinkQuestion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PaperLinkQuestion::getPaperId, paperId);
        List<PaperLinkQuestion> paperLinkQuestionList = paperLinkQuestionService.list(lambdaQueryWrapper);
        log.info("查询到题目信息: {}", paperLinkQuestionList);
        List<Long> examQuestionIds = paperLinkQuestionList.stream().map(PaperLinkQuestion::getExamQuestionId).collect(Collectors.toList());
        LambdaQueryWrapper<ExamQuestion> examQuestionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examQuestionLambdaQueryWrapper.in(ExamQuestion::getEqId, examQuestionIds);
        List<ExamQuestion> examQuestionList = examQuestionService.list(examQuestionLambdaQueryWrapper);
        log.info("查询到examQuestionList: {}", examQuestionList);
        if(examQuestionList != null) {
            for (ExamQuestion examQuestion : examQuestionList) {
                ExamQuestionDTO examQuestionDTO = new ExamQuestionDTO();
                Long eqTypeId = examQuestion.getEqTypeId();
                // 读取数据库获取内容
                Long noteId = examQuestion.getNoteId();
                Note note = noteService.getById(noteId);
                examQuestionDTO.setEqId(examQuestion.getEqId());
                examQuestionDTO.setExamQuestionId(examQuestion.getEqId());
                examQuestionDTO.setEqTypeId(eqTypeId);
                examQuestionDTO.setNote(note);
                // 获取分数
                if(typeScoreMap.containsKey(eqTypeId)) {
                    Integer score = typeScoreMap.get(eqTypeId);
                    examQuestionDTO.setScore(score);
                }
                // TODO 读取数据库获取eq_type
                if(eqTypeId == 1L || eqTypeId == 2L) {
                    Long eqId = examQuestion.getEqId();
                    LambdaQueryWrapper<EqOption> eqOptionLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    eqOptionLambdaQueryWrapper.eq(EqOption::getEqId, eqId);
                    EqOption option = eqOptionService.getOne(eqOptionLambdaQueryWrapper);
                    examQuestionDTO.setEqOption(option);
                }
                examQuestionDTOArrayList.add(examQuestionDTO);
            }
            redisTemplate.opsForList().rightPushAll("exam:"+examId,
                    examQuestionDTOArrayList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList()));
        }

        return ApiResponse.success(examQuestionDTOArrayList);
    }


    @PostMapping("/submit")
    public ApiResponse<String> examSubmit(@RequestBody JSONObject submitInfo) {
        Long examId = submitInfo.getLong("examId");
        Long userId = submitInfo.getLong("userId");
        String answerMapStr = submitInfo.getStr("answerMap");
        HashMap<String, String> answerMap = JSONUtil.toBean(answerMapStr, new TypeReference<HashMap<String, String>>() {
        }, true);
        log.info("检测到用户 {} 提交答案: {}", userId, answerMap);
//        String listStr = redisTemplate.opsForValue().get("exam:" + examId);
//        log.info();
        return ApiResponse.success("1");
    }


}
