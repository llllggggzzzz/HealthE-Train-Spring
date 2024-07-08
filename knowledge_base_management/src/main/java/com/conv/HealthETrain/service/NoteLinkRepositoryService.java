package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.domain.NoteLinkRepository;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【note_link_repository】的数据库操作Service
* @createDate 2024-07-07 11:51:31
*/
public interface NoteLinkRepositoryService extends IService<NoteLinkRepository> {
    List<NoteLinkRepository> findNoteLinkRepositoryListByRepositoryId(Long repositoryId);
    Boolean addNoteLinkRepository(NoteLinkRepository noteLinkRepository);

    NoteLinkRepository findNoteLinkRepositoryByNoteId(Long noteId);
}
