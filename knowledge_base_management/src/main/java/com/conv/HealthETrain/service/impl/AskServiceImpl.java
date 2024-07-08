package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Ask;
import com.conv.HealthETrain.service.AskService;
import com.conv.HealthETrain.mapper.AskMapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【ask】的数据库操作Service实现
* @createDate 2024-07-07 11:51:31
*/
@Service
public class AskServiceImpl extends ServiceImpl<AskMapper, Ask>
    implements AskService{
    @Autowired
    private AskMapper askMapper;

    @Override
    public Boolean addOneAsk(Ask ask) {
        Ask newAsk = new Ask();
        newAsk.setNoteId(ask.getNoteId());
        return askMapper.insert(newAsk) > 0;
    }

    @Override
    public List<Ask> getAllAsk() {
        return askMapper.selectList(null);
    }

    @Override
    public Ask getAskById(Long askId) {
        QueryWrapper<Ask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ask_id", askId);
        return askMapper.selectOne(queryWrapper);
    }
}




