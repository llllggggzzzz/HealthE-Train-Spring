package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Chapter;
import com.conv.HealthETrain.service.ChapterService;
import com.conv.HealthETrain.mapper.ChapterMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【chapter】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter>
    implements ChapterService{

    private ChapterMapper chapterMapper;

    @Override
    public List<Chapter> getChaptersByLessonId(Long id) {
        LambdaQueryWrapper<Chapter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Chapter::getLessonId, id);
        List<Chapter> chapters = chapterMapper.selectList(lambdaQueryWrapper);
        return chapters;
    }
}




