package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Section;
import com.conv.HealthETrain.service.SectionService;
import com.conv.HealthETrain.mapper.SectionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【section】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class SectionServiceImpl extends ServiceImpl<SectionMapper, Section>
    implements SectionService{

    private SectionMapper sectionMapper;

    @Override
    public List<Section> getSectionsByChapterId(Long chapterId) {
        LambdaQueryWrapper<Section> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Section::getChapterId, chapterId);
        return sectionMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public Section updateSectionTitleById(Section section) {
        UpdateWrapper<Section> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("section_id", section.getSectionId()).set("section_title", section.getSectionTitle());
        int update = sectionMapper.update(section, updateWrapper);
        if (update > 0) {
            return section;
        } else {
            return null;
        }
    }

    @Override
    public Section addSection(Section section) {
        sectionMapper.insert(section);
        return section;
    }
}




