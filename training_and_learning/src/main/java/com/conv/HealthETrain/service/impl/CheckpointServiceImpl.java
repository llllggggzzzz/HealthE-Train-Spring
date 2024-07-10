package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Checkpoint;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.conv.HealthETrain.domain.Section;
import com.conv.HealthETrain.mapper.LessonMapper;
import com.conv.HealthETrain.mapper.SectionMapper;
import com.conv.HealthETrain.service.CheckpointService;
import com.conv.HealthETrain.mapper.CheckpointMapper;
import com.conv.HealthETrain.service.LessonService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【checkpoint】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class CheckpointServiceImpl extends ServiceImpl<CheckpointMapper, Checkpoint>
    implements CheckpointService{

    private final CheckpointMapper checkpointMapper;
    private final LessonMapper lessonMapper;
    private final SectionMapper sectionMapper;

    @Override
    public Checkpoint getCheckpointBySectionId(Long sectionId, Long userId) {
        LambdaQueryWrapper<Checkpoint> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Checkpoint::getSectionId, sectionId).eq(Checkpoint::getUserId, userId);
        return checkpointMapper.selectOne(lambdaQueryWrapper);
    }

    // 根据章节id查找章节的所学总人数
    @Override
    public int getCountUserByChapterId(Long chapterId) {
        return checkpointMapper.getUniqueUserCountByChapterId(chapterId);
    }

    // 根据userId统计该名同学已经学习过的必修课section总数
    @Override
    public int countLearnedSectionsByUserId(Long userId) {
        int totalSections = 0;
        // 查询该学生所有的 Checkpoint 记录
        QueryWrapper<Checkpoint> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        List<Checkpoint> checkpoints = checkpointMapper.selectList(query);

        for (Checkpoint checkpoint : checkpoints) {
            // 获取每个 Checkpoint 记录对应的 sectionId
            Long sectionId = checkpoint.getSectionId();

            // 根据 sectionId 查找对应的 Section 记录
            Section section = sectionMapper.selectById(sectionId);
            if (section != null) {
                // 获取 section 所属的 chapterId
                Long chapterId = section.getChapterId();

                // 根据 chapterId 统计已学习的 section 数量
                QueryWrapper<Section> sectionQuery = new QueryWrapper<>();
                sectionQuery.eq("chapter_id", chapterId);
                sectionQuery.le("section_order", section.getSectionOrder());
                int learnedSections = Math.toIntExact(sectionMapper.selectCount(sectionQuery));

                // 根据 checkpoint 的 lessonId 查询对应的 Lesson 记录
                Long lessonId = checkpoint.getLessonId();
                Lesson lesson = lessonMapper.selectById(lessonId);

                // 如果 lesson 存在且 lessonType 不为 0，则累加到总数
                if (lesson != null && lesson.getLessonType() != 0) {
                    totalSections += learnedSections;
                }
            }
        }

        return totalSections;
    }
}




