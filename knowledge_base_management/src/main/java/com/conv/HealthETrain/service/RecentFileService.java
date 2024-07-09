package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.RecentFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【recent_file】的数据库操作Service
* @createDate 2024-07-07 11:51:31
*/
public interface RecentFileService extends IService<RecentFile> {
    Boolean insertRecentFile(RecentFile recentFile);
    List<RecentFile> getRecentFile(Long userId);
    Boolean updateRecentFile(RecentFile recentFile);

}
