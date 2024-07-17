package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.DTO.LessonExamInfoDTO;
import com.conv.HealthETrain.domain.Exam;
import com.conv.HealthETrain.domain.ExamLinkUser;
import com.conv.HealthETrain.mapper.ExamLinkUserMapper;
import com.conv.HealthETrain.service.ExamService;
import com.conv.HealthETrain.mapper.ExamMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author flora
* @description 针对表【exam】的数据库操作Service实现
* @createDate 2024-07-07 11:47:43
*/
@Service
@AllArgsConstructor
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam>
    implements ExamService{

    private  final ExamMapper examMapper;
    private final ExamLinkUserMapper examLinkUserMapper;

    // 查询用户的考试信息
    @Override
    public List<LessonExamInfoDTO> getLessonExamInfo(Long lessonId, Long userId) {
        List<Exam> exams = examMapper.selectList(new QueryWrapper<Exam>().eq("lession_id", lessonId));
        List<LessonExamInfoDTO> lessonExamInfoDTOS = new ArrayList<>();
        for (Exam exam : exams) {
            LessonExamInfoDTO dto = new LessonExamInfoDTO();
            BeanUtils.copyProperties(exam, dto);

            List<ExamLinkUser> linkUsers = examLinkUserMapper.selectList(new QueryWrapper<ExamLinkUser>()
                    .eq("exam_id", exam.getExamId())
                    .eq("user_id", userId));

            int maxScore = Integer.MIN_VALUE;
            ExamLinkUser maxScoreAttempt = null;

            for (ExamLinkUser linkUser : linkUsers) {
                if (linkUser.getScore() > maxScore) {
                    maxScore = linkUser.getScore();
                    maxScoreAttempt = linkUser;
                }
            }

            if (maxScoreAttempt != null) {
                dto.setScore(maxScoreAttempt.getScore());
                dto.setTimeCost(maxScoreAttempt.getTimeCost());
            } else {
                dto.setScore(null); // No attempts found
                dto.setTimeCost(null);
            }

            int remainTimes = exam.getRetryTimes() - linkUsers.size();
            dto.setRemainTimes(remainTimes);

            lessonExamInfoDTOS.add(dto);
        }
        return lessonExamInfoDTOS;
    }
}




