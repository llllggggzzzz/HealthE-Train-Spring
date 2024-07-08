package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.POJP.LessonLinkUser;
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


    @Override
    public List<Long> getChooesdLessons(Long user_id) {
        LambdaQueryWrapper<LessonLinkUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LessonLinkUser::getUserId, user_id).select(LessonLinkUser::getLessonId);
        return lessonLinkUserMapper.getLessonIdsByUserId(user_id);
    }
}




