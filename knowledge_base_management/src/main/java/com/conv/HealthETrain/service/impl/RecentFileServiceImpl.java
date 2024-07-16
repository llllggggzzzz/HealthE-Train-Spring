package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.RecentFile;
import com.conv.HealthETrain.service.RecentFileService;
import com.conv.HealthETrain.mapper.RecentFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【recent_file】的数据库操作Service实现
* @createDate 2024-07-07 11:51:31
*/
@Service
public class RecentFileServiceImpl extends ServiceImpl<RecentFileMapper, RecentFile>
    implements RecentFileService{

    @Autowired
    private RecentFileMapper recentFileMapper;

    @Override
    public Boolean insertRecentFile(RecentFile recentFile) {
        RecentFile newRecentFile = new RecentFile();
        newRecentFile.setRfId(recentFile.getRfId());
        newRecentFile.setUserId(recentFile.getUserId());
        newRecentFile.setNoteId(recentFile.getNoteId());
        newRecentFile.setTime(recentFile.getTime());
        return recentFileMapper.insert(newRecentFile) > 0;
    }

    @Override
    public List<RecentFile> getRecentFile(Long userId) {
        QueryWrapper<RecentFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return recentFileMapper.selectList(queryWrapper);
    }

    @Override
    public Boolean updateRecentFile(RecentFile recentFile) {
        UpdateWrapper<RecentFile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", recentFile.getUserId())
                .eq("note_id", recentFile.getNoteId())
                .set("time", recentFile.getTime());
        return recentFileMapper.update(updateWrapper) > 0;
    }
}




