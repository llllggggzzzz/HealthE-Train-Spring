package com.conv.HealthETrain.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.client.LessonClient;
import com.conv.HealthETrain.domain.DTO.ExamDTO;
import com.conv.HealthETrain.domain.DTO.LessonExamInfoDTO;
import com.conv.HealthETrain.domain.DTO.LessonInfoDTO;
import com.conv.HealthETrain.domain.*;
import com.conv.HealthETrain.domain.DTO.*;
import com.conv.HealthETrain.domain.Exam;
import com.conv.HealthETrain.domain.Paper;
import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.mapper.ExamMapper;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ExamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.conv.HealthETrain.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private static final ObjectMapper mapper = new ObjectMapper();

    private final PaperLinkQuestionService paperLinkQuestionService;

    private final ExamQuestionService examQuestionService;

    private final EqOptionService eqOptionService;

    private final NoteService noteService;

    private final ExamEqtypeScoreService examEqtypeScoreService;

    private final StringRedisTemplate redisTemplate;

    private final ExamResultService examResultService;
    private final ExamMapper examMapper;


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

    @PostMapping("/add")
    public ApiResponse<String> addExam(@RequestBody Exam exam){
        boolean flag = examService.save(exam);
        if(flag){
            return ApiResponse.success(ResponseCode.SUCCEED,"成功");
        }
        else
        {
            return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED,"失败");
        }
    }


    @GetMapping("/info/{examId}/user/{userId}")
    public ApiResponse<ExamInfoDTO> getExamInfo(@PathVariable("examId") Long examId,
                                         @PathVariable("userId") Long userId) {
        Exam exam = examService.getById(examId);
        ExamInfoDTO examInfoDTO = new ExamInfoDTO(exam);
        // 查询是否交卷
        LambdaQueryWrapper<ExamResult> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExamResult::getExamId, examInfoDTO).eq(ExamResult::getUserAnswer, userId);
        List<ExamResult> results = examResultService.list(lambdaQueryWrapper);
        if(results != null && !results.isEmpty()) {
            // 已经交卷过
            examInfoDTO.setSubmit(true);
        } else {
            examInfoDTO.setSubmit(false);
        }
        return ApiResponse.success(examInfoDTO);
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
                // 读取数据库获取eq_type
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

            //
        }

        return ApiResponse.success(examQuestionDTOArrayList);
    }


    @PostMapping("/submit")
    public ApiResponse<List<ExamResult>> examSubmit(@RequestBody JSONObject submitInfo) {
        Long examId = submitInfo.getLong("examId");
        Long userId = submitInfo.getLong("userId");

        String answerMapStr = submitInfo.getStr("answerMap");
        List<ExamResult> eqResults;
        HashMap<Long, String> answerMap = JSONUtil.toBean(answerMapStr, new TypeReference<>() {
        }, true);
        log.info("检测到用户 {} 提交答案: {}", userId, answerMap);
        List<String> strList = redisTemplate.opsForList().range("exam-answer:" + examId, 0, -1);
        if(strList != null && !strList.isEmpty()) {
            eqResults = strList.stream().map(str -> JSONUtil.toBean(str, ExamResult.class)).collect(Collectors.toList());
            log.info("检测到redis中存在缓存: {}", eqResults);
        } else  {
            // 未在redis查询到题目信息，通过数据库搜索
            List<Long> examQuestionIds = answerMap.keySet().stream().toList();
            List<ExamQuestion> examQuestions = examQuestionService.listByIds(examQuestionIds);
            // 查询题目映射
            LambdaQueryWrapper<ExamEqtypeScore> examEqtypeScoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
            examEqtypeScoreLambdaQueryWrapper.eq(ExamEqtypeScore::getExamId, examId);
            List<ExamEqtypeScore> examEqtypeScoreList = examEqtypeScoreService.list(examEqtypeScoreLambdaQueryWrapper);
            Map<Long, Integer> typeScoreMap = examEqtypeScoreList.stream().
                    collect(Collectors.toMap(ExamEqtypeScore::getEqTypeId,
                            ExamEqtypeScore::getScore));
            log.info("查询到分数映射: {}", typeScoreMap);

            eqResults = examQuestions.stream().map(examQuestion -> {
                ExamResult examResult = new ExamResult();
                examResult.setExamId(examId);
                examResult.setUserId(userId);
                examResult.setEqId(examQuestion.getEqId());
                examResult.setEqTypeId(examQuestion.getEqTypeId());
                examResult.setRealAnswer(examQuestion.getAnswer());
                examResult.setSumScore(typeScoreMap.get(examQuestion.getEqTypeId()));
                return examResult;
            }).collect(Collectors.toList());
            // 设置redis
            redisTemplate.opsForList().rightPushAll("exam-answer:"+examId, eqResults
                    .stream()
                    .map(JSONUtil::toJsonStr)
                    .collect(Collectors.toList()));

            // 加入到数据库

        }
        // 根据题目类型映射处理，进行评分

        // 查询题目类型的分数

        List<Long> examQuestionList = answerMap.keySet().stream().toList();
        for (Long examQuestionId : examQuestionList) {
            // 获取用户填写的答案
            ExamResult examResult = CollUtil.findOneByField(eqResults, "eqId", examQuestionId);
            if(examResult == null) {
                continue;
            }
            Long typeId = examResult.getEqTypeId();
            String realAnswer = examResult.getRealAnswer();
            Integer sumScore = examResult.getSumScore();
            String userAnswer = answerMap.get(examQuestionId);
            examResult.setUserAnswer(userAnswer);
            if(typeId == 1L) {
                // 进行比对即可
                boolean equals = StrUtil.equals(userAnswer, realAnswer);
                if(equals) {
                    // 得到全分
                    examResult.setGetScore(Double.valueOf(sumScore));
                } else {
                    // 不得分
                    examResult.setGetScore(0.0);
                }
            } else if (typeId == 2L) {
                // 进行多项目比对
                List<String> realAnswerChooes = convertStringToIndices(realAnswer);
                List<String> userAnswerChoose = List.of(userAnswer.split(","));
                log.info("进行多选题判断 real {} user {}", realAnswerChooes, userAnswerChoose);
                if (realAnswerChooes.size() == userAnswerChoose.size()) {
                    // 选项完全一致
                    if(realAnswerChooes.containsAll(userAnswerChoose)) {
                        // 得到全分
                        examResult.setGetScore(Double.valueOf(sumScore));
                    } else {
                        // 包含错误选项， 不得分
                        examResult.setGetScore(0.0);
                    }
                } else {
                    // 判断是否多
                    if(realAnswerChooes.size() > userAnswerChoose.size()) {
                        if(realAnswerChooes.containsAll(userAnswerChoose)) {
                            // 得到半分
                            examResult.setGetScore((double) (sumScore - sumScore % 2));
                        } else {
                            // 包含错误选项， 不得分
                            examResult.setGetScore(0.0);
                        }
                    } else {
                        // 包含错误选项， 不得分
                        examResult.setGetScore(0.0);
                    }
                }
            } else if (typeId == 3L) {
                // 判断题
                if(StrUtil.equals(userAnswer, realAnswer)) {
                    // 得到全分
                    examResult.setGetScore(Double.valueOf(sumScore));
                } else {
                    // 错误不得分
                    examResult.setGetScore(0.0);
                }

            } else if (typeId == 4L) {
                // 填空题
                // TODO 调用AI进行问答
                examResult.setGetScore(Double.valueOf(sumScore));
            } else if (typeId == 5L) {
                // 简答题
                // TODO 调用AI进行问答
                examResult.setGetScore(Double.valueOf(sumScore));
            } else {
                // 题目不存在
                log.error("题目类型不存在");
            }
        }
        // 将学生验卷信息保存
        redisTemplate.opsForList()
                .rightPushAll("exam-result:"+examId+"-"+userId,
                eqResults.stream().map(JSONUtil::toJsonStr).toList());
        // 保存到数据库中
        examResultService.saveBatch(eqResults);

        return ApiResponse.success(eqResults);
    }

    public static List<String> convertStringToIndices(String str) {
        // 创建一个存储索引的列表
        List<String> indices = new ArrayList<>();

        // 遍历输入的字符串
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            // 根据字符确定索引，并添加到列表中
            switch (ch) {
                case 'A':
                    indices.add("0");
                    break;
                case 'B':
                    indices.add("1");
                    break;
                case 'C':
                    indices.add("2");
                    break;
                case 'D':
                    indices.add("3");
                    break;
                // 如果输入了不支持的字符，可以选择抛出异常或者跳过
                default:
                    // 这里选择跳过不支持的字符
                    break;
            }
        }

        return indices;
    }


    @GetMapping("/result/exam/{examId}/user/{userId}")
    public ApiResponse<List<ExamResult>> getExamResult(@PathVariable("examId") Long examId,
                                                       @PathVariable("userId") Long userId) {
        // 根据examId 和 userId去查询信息
        List<ExamResult> examResults = null;
        // 1. 首先查询redis
        if(redisTemplate.hasKey("exam-result:"+examId+"-"+userId)) {
            List<String> stringList = redisTemplate.opsForList().range("exam-result:" + examId + "-" + userId, 0, -1);
            if(stringList != null) {
                examResults = stringList.stream().map(str -> JSONUtil.toBean(str, ExamResult.class)).toList();
            }
        } else {
            LambdaQueryWrapper<ExamResult> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ExamResult::getExamId, examId).eq(ExamResult::getUserId, userId);
            examResults = examResultService.list(lambdaQueryWrapper);
        }

        // 添加Note查询
        if(examResults == null) {
            return ApiResponse.success(new ArrayList<>());
        }
        for (ExamResult examResult : examResults) {
            Long eqId = examResult.getEqId();
            ExamQuestion examQuestion = examQuestionService.getById(eqId);
            if(examQuestion != null) {
                Long noteId = examQuestion.getNoteId();
                LambdaQueryWrapper<EqOption> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(EqOption::getEqId, examQuestion.getEqId());
                EqOption option = eqOptionService.getOne(lambdaQueryWrapper);
                examResult.setEqOption(option);
                Note note = noteService.getById(noteId);
                examResult.setNote(note);
            }
        }
        return ApiResponse.success(examResults);
    }


    @GetMapping("/information/{lessonId}/{userId}")
    public ApiResponse<List<LessonExamInfoDTO>> getLessonExamInfos(@PathVariable("lessonId") Long lessonId,
                                                                   @PathVariable("userId")Long userId) throws JsonProcessingException {
        String redisKey = "examLessonInfo:"+lessonId+":"+userId;
        String cachedData = redisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            List<LessonExamInfoDTO> cachedExamLesson = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, LessonExamInfoDTO.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", cachedExamLesson);
        } else {
            List<LessonExamInfoDTO> lessonExamInfoDTOS = examService.getLessonExamInfo(lessonId,userId);
            String jsonExamLesson = mapper.writeValueAsString(lessonExamInfoDTOS);
            redisTemplate.opsForValue().set(redisKey, jsonExamLesson);
            redisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", lessonExamInfoDTOS);
        }
    }


    @GetMapping("/teacher/{teacherId}")
    public ApiResponse<List<Exam>> getExamInfoByTeacherId(@PathVariable("teacherId") Long teacherId) {
        // 根据td_id查询发布的考试
        LambdaQueryWrapper<Exam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Exam::getCreatorId, teacherId);
        // 考试列表
        List<Exam> examList = examService.list(lambdaQueryWrapper);
        return ApiResponse.success(examList);

    }
}
