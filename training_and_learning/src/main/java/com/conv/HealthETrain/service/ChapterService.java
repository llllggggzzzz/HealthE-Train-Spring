package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【chapter】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface ChapterService extends IService<Chapter> {

    List<Chapter> getChaptersByLessonId(Long id);

    Chapter addChapter(Chapter chapter);
}
