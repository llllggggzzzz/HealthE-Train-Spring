package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.service.NoteService;
import com.conv.HealthETrain.mapper.NoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author flora
* @description 针对表【note】的数据库操作Service实现
* @createDate 2024-07-07 11:47:43
*/
@Service
@RequiredArgsConstructor
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note>
    implements NoteService{

    final private NoteMapper noteMapper;

    @Override
    public Note insertNote(Note note) {
        noteMapper.insert(note);
        return note;
    }

    @Override
    public Note updateNote(Note note) {
        noteMapper.updateById(note);
        return note;
    }
}




