package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Repository;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【repository】的数据库操作Service
* @createDate 2024-07-07 11:51:31
*/
public interface RepositoryService extends IService<Repository> {
    List<Repository> findRepositoryListByUserId(Long userId);
    Boolean addRepository(Repository repository);
    Boolean updateRepositoryVisibility(Long repositoryId, int visibility);


}
