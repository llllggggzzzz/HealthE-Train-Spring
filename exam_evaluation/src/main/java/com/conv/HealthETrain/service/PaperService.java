package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Paper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【paper】的数据库操作Service
* @createDate 2024-07-07 11:47:43
*/
public interface PaperService extends IService<Paper> {
    List<Paper> getAllPaper();
}
