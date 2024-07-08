package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Ask;
import com.baomidou.mybatisplus.extension.service.IService;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

/**
* @author flora
* @description 针对表【ask】的数据库操作Service
* @createDate 2024-07-07 11:51:31
*/
public interface AskService extends IService<Ask> {
    Boolean addOneAsk(Ask ask);
    List<Ask> getAllAsk();
    Ask getAskById(Long askId);

}
