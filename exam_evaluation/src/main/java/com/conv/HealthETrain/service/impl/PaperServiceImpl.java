package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Paper;
import com.conv.HealthETrain.service.PaperService;
import com.conv.HealthETrain.mapper.PaperMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【paper】的数据库操作Service实现
* @createDate 2024-07-07 11:47:43
*/
@Service
@AllArgsConstructor
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper>
    implements PaperService{
    private final PaperMapper paperMapper;

    @Override
    public List<Paper> getAllPaper() {
        return paperMapper.selectList(null);
    }
}




