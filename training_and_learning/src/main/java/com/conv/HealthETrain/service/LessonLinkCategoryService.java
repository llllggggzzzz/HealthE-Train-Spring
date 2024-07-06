package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.LessonLinkCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author john
* @description 针对表【lesson_link_category】的数据库操作Service
* @createDate 2024-07-05 17:58:44
*/
public interface LessonLinkCategoryService extends IService<LessonLinkCategory> {

    List<LessonLinkCategory> getAllLessonsByCategoryId(Long categoryId);
}
