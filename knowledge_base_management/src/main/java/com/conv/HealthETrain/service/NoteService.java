package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Note;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【note】的数据库操作Service
* @createDate 2024-07-08 13:26:13
*/
public interface NoteService extends IService<Note> {
   Long addNoteItem(Note note);
   Note findNoteByNoteId(Long noteId);
   Boolean updateNoteVisibility(Long noteId, int visibility);
   List<Note> findFullOpenNoteList();
   Boolean updateNote(Long noteId, Note note);
   List<Note> getMySharedNoteList(Long userId);
   List<Note> findNoteListByNoteIdList(List<Long> noteIdList);
   List<Note> findNoteListByNoteTitle(String title);

}
