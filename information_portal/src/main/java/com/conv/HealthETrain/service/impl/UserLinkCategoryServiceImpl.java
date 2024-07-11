package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.UserLinkCategory;
import com.conv.HealthETrain.service.UserLinkCategoryService;
import com.conv.HealthETrain.mapper.UserLinkCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author john
* @description 针对表【user_link_category】的数据库操作Service实现
* @createDate 2024-07-05 17:56:52
*/
@Service
@RequiredArgsConstructor
public class UserLinkCategoryServiceImpl extends ServiceImpl<UserLinkCategoryMapper, UserLinkCategory>
    implements UserLinkCategoryService{
    private final UserLinkCategoryMapper userLinkCategoryMapper;

    // 计算七类学生分别的数量
    @Override
    public Map<String, Integer> countStudentsByCategory() {
        List<UserLinkCategory> userLinkCategories = userLinkCategoryMapper.selectList(null);

        Map<String, Integer> categoryCounts = new HashMap<>();

        // 遍历用户关联类别列表，统计category_id为1-7的学生数量
        for (UserLinkCategory ulc : userLinkCategories) {
            int categoryId = ulc.getCategoryId();
            if (categoryId >= 1 && categoryId <= 7) {
                String categoryIdStr = String.valueOf(categoryId);
                int count = categoryCounts.getOrDefault(categoryIdStr, 0);
                categoryCounts.put(categoryIdStr, count + 1);
            }
        }
        return categoryCounts;
    }

    // 计算七类学生总数
    @Override
    public int countStudents() {
        return userLinkCategoryMapper.countStudents();
    }

    @Override
    public void deleteUserLinkCategory(long userId,int categoryId) {
        userLinkCategoryMapper.deleteCategoryForUser(userId,categoryId);
    }
}




