package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Tag;
import com.conv.HealthETrain.service.TagService;
import com.conv.HealthETrain.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author flora
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2024-07-07 11:52:15
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




