package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.POJP.Chapter;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.conv.HealthETrain.domain.POJP.LessonLinkUser;
import com.conv.HealthETrain.domain.Section;
import com.conv.HealthETrain.mapper.ChapterMapper;
import com.conv.HealthETrain.mapper.LessonMapper;
import com.conv.HealthETrain.mapper.SectionMapper;
import com.conv.HealthETrain.service.LessonLinkUserService;
import com.conv.HealthETrain.mapper.LessonLinkUserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【lesson_link_user】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class LessonLinkUserServiceImpl extends ServiceImpl<LessonLinkUserMapper, LessonLinkUser>
    implements LessonLinkUserService{

    private final LessonLinkUserMapper lessonLinkUserMapper;
    private final LessonMapper lessonMapper;
    private final ChapterMapper chapterMapper;
    private final SectionMapper sectionMapper;

    @Override
    public List<Long> getChooesdLessons(Long user_id) {
        LambdaQueryWrapper<LessonLinkUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LessonLinkUser::getUserId, user_id).select(LessonLinkUser::getLessonId);
        return lessonLinkUserMapper.getLessonIdsByUserId(user_id);
    }

    // 统计一名学生应该修的所有section总量
    @Override
    public int getSectionCountsByUserId(Long userId) {
        QueryWrapper<LessonLinkUser> linkQuery = new QueryWrapper<>();
        linkQuery.eq("user_id", userId);

        // 找到所有课程
        List<LessonLinkUser> lessonLinkUsers = lessonLinkUserMapper.selectList(linkQuery);

        // 总数量
        int totalSections = 0;

        for (LessonLinkUser lessonLinkUser : lessonLinkUsers) {
            Long lessonId = lessonLinkUser.getLessonId();
            // 查一个必修课程的章节List
            Lesson lesson = lessonMapper.selectById(lessonId);
            if (lesson != null && lesson.getLessonType() == 1) {
                QueryWrapper<Chapter> chapterQuery = new QueryWrapper<>();
                chapterQuery.eq("lesson_id", lessonId);
                List<Chapter> chapters = chapterMapper.selectList(chapterQuery);
                // 查询section并累加
                for (Chapter chapter : chapters) {
                    QueryWrapper<Section> sectionQuery = new QueryWrapper<>();
                    sectionQuery.eq("chapter_id", chapter.getChapterId());
                    int sectionCount = Math.toIntExact(sectionMapper.selectCount(sectionQuery));
                    totalSections += sectionCount;
                }
            }
        }
        return totalSections;
    }
}




