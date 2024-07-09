package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.service.NoteService;
import com.conv.HealthETrain.mapper.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.conv.HealthETrain.enums.NoteTypeCode.NOTE_OF_REPOSITORY;
import static com.conv.HealthETrain.enums.VisibilityCode.V_FULLPUBLIC;

/**
* @author flora
* @description 针对表【note】的数据库操作Service实现
* @createDate 2024-07-08 13:26:13
*/
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note>
    implements NoteService{

    @Autowired
    private NoteMapper noteMapper;

    /**
    * @Description: 新增noteItem
    * @Param: note
    * @return: Boolean
    * @Author: flora
    * @Date: 2024/7/8
    */
    @Override
    public Long addNoteItem(Note note) {
        Note newNote = new Note();
        newNote.setNoteTitle(note.getNoteTitle());
        newNote.setNoteContent(note.getNoteContent());
        newNote.setType(note.getType());
        newNote.setVisibility(note.getVisibility());
        newNote.setTime(note.getTime());
        newNote.setUserId(note.getUserId());
        noteMapper.insert(newNote);
        return newNote.getNoteId();
    }

    @Override
    public Note findNoteByNoteId(Long noteId) {
        QueryWrapper<Note> wrapper = new QueryWrapper<>();
        wrapper.eq("note_id", noteId);
        return noteMapper.selectOne(wrapper);
    }

    @Override
    public Boolean updateNoteVisibility(Long noteId, int visibility) {
        UpdateWrapper<Note> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("note_id", noteId).set("visibility", visibility);
        return noteMapper.update(updateWrapper) > 0;
    }

    @Override
    public List<Note> findFullOpenNoteList() {
        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("visibility", V_FULLPUBLIC.getCode()).eq("type", NOTE_OF_REPOSITORY.getCode());
        return noteMapper.selectList(queryWrapper);
    }

    @Override
    public Boolean updateNote(Long noteId, Note newNote) {
        // 查找现有笔记
        Note existingNote = noteMapper.selectById(noteId);
        if (existingNote == null) {
            return false; // 如果找不到笔记，返回 false
        }
        // 更新笔记信息
        existingNote.setNoteContent(newNote.getNoteContent());
        existingNote.setNoteTitle(newNote.getNoteTitle());
        existingNote.setUserId(newNote.getUserId());
        existingNote.setTime(newNote.getTime());
        existingNote.setType(newNote.getType());
        existingNote.setVisibility(newNote.getVisibility());

        // 保存更新后的笔记
        int rows = noteMapper.updateById(existingNote);
        return rows > 0;
    }

    @Override
    public List<Note> getMySharedNoteList(Long userId) {
        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("visibility", 1);
        return noteMapper.selectList(queryWrapper);
    }

    @Override
    public List<Note> findNoteListByNoteIdList(List<Long> noteIdList) {
        if (noteIdList == null || noteIdList.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("note_id", noteIdList);
        return noteMapper.selectList(queryWrapper);
    }
}




