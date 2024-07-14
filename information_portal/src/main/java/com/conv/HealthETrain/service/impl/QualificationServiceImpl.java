package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Qualification;
import com.conv.HealthETrain.service.QualificationService;
import com.conv.HealthETrain.mapper.QualificationMapper;
import org.springframework.stereotype.Service;

/**
* @author john
* @description 针对表【qualification】的数据库操作Service实现
* @createDate 2024-07-05 17:56:52
*/
@Service
public class QualificationServiceImpl extends ServiceImpl<QualificationMapper, Qualification>
    implements QualificationService{

    @Override
    public String getQualificationById(Long qualificationId) {
        return lambdaQuery().eq(Qualification::getQualificationId, qualificationId).one().getQualificationName();
    }

}




