package com.conv.HealthETrain.controller;


import cn.hutool.core.util.ObjectUtil;
import com.conv.HealthETrain.domain.Category;
import com.conv.HealthETrain.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public String getCategoryNameById(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return ObjectUtil.isNull(category) ? category.getCategoryName() : "";
    }
}
