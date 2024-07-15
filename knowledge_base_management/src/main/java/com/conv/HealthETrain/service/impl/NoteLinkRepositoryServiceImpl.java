package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.domain.NoteLinkRepository;
import com.conv.HealthETrain.service.NoteLinkRepositoryService;
import com.conv.HealthETrain.mapper.NoteLinkRepositoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【note_link_repository】的数据库操作Service实现
* @createDate 2024-07-07 11:51:31
*/
@Service
public class NoteLinkRepositoryServiceImpl extends ServiceImpl<NoteLinkRepositoryMapper, NoteLinkRepository>
    implements NoteLinkRepositoryService{

    @Autowired
    private NoteLinkRepositoryMapper noteLinkRepositoryMapper;

    @Override
    public List<NoteLinkRepository> findNoteLinkRepositoryListByRepositoryId(Long repositoryId) {
        QueryWrapper<NoteLinkRepository> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("repository_id", repositoryId);
        return noteLinkRepositoryMapper.selectList(queryWrapper);
    }

    @Override
    public Boolean addNoteLinkRepository(NoteLinkRepository noteLinkRepository) {
        NoteLinkRepository newNoteLinkRepository = new NoteLinkRepository();
        newNoteLinkRepository.setRepositoryId(noteLinkRepository.getRepositoryId());
        newNoteLinkRepository.setNoteId(noteLinkRepository.getNoteId());
        return noteLinkRepositoryMapper.insert(newNoteLinkRepository) > 0;
    }

    @Override
    public NoteLinkRepository findNoteLinkRepositoryByNoteId(Long noteId) {
        QueryWrapper<NoteLinkRepository> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId);
        return noteLinkRepositoryMapper.selectOne(queryWrapper);
    }
}




