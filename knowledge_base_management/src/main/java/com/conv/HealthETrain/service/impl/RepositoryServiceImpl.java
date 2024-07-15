package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.domain.Repository;
import com.conv.HealthETrain.service.RepositoryService;
import com.conv.HealthETrain.mapper.RepositoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flora
* @description 针对表【repository】的数据库操作Service实现
* @createDate 2024-07-07 11:51:31
*/
@Service
public class RepositoryServiceImpl extends ServiceImpl<RepositoryMapper, Repository>
    implements RepositoryService{

    @Autowired
    private RepositoryMapper repositoryMapper;

    @Override
    public List<Repository> findRepositoryListByUserId(Long userId) {
        QueryWrapper<Repository> repositoryQueryWrapper = new QueryWrapper<>();
        repositoryQueryWrapper.eq("user_id", userId);
        return repositoryMapper.selectList(repositoryQueryWrapper);
    }

    @Override
    public Boolean addRepository(Repository repository) {
        Repository newRepository = new Repository();
        newRepository.setRepositoryTitle(repository.getRepositoryTitle());
        newRepository.setUserId(repository.getUserId());
        newRepository.setVisibility(repository.getVisibility());
        return repositoryMapper.insert(newRepository) > 0;
    }

    @Override
    public Boolean updateRepositoryVisibility(Long repositoryId, int visibility) {
        UpdateWrapper<Repository> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("repository_id", repositoryId).set("visibility", visibility);
        return repositoryMapper.update(updateWrapper) > 0;
    }
}




