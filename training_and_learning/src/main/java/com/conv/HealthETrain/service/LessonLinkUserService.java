package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.POJP.LessonLinkUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【lesson_link_user】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface LessonLinkUserService extends IService<LessonLinkUser> {
    public List<Long> getChooesdLessons(Long user_id);

    // 实现查询某位学生所修必修课的总section数量
    public int getSectionCountsByUserId(Long userId);

    // 查询某课程的所有学生Id
    public List<Long> getStudentsIdByLessonId (Long userId);

    boolean checkLessonSelected(Long userId, Long lessonId);

    // 删除指定课程内的学生
    boolean deleteLessonStudents(Long lessonId,List<Long> userIdList);
}
