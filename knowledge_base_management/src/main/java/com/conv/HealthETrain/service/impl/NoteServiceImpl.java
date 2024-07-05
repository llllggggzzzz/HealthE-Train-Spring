package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.service.NoteService;
import com.conv.HealthETrain.mapper.NoteMapper;
import org.springframework.stereotype.Service;

/**
* @author john
* @description 针对表【note】的数据库操作Service实现
* @createDate 2024-07-05 17:57:28
*/
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note>
    implements NoteService{

}




