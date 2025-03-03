package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Category;
import com.conv.HealthETrain.service.CategoryService;
import com.conv.HealthETrain.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author john
* @description 针对表【category】的数据库操作Service实现
* @createDate 2024-07-05 17:56:52
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Override
    public String getCategoryById(Long categoryId) {
        return lambdaQuery().eq(Category::getCategoryId, categoryId).one().getCategoryName();
    }
}




