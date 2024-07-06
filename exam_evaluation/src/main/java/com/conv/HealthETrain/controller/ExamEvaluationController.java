package com.conv.HealthETrain.controller;


import com.conv.HealthETrain.service.EqOptionService;
import com.conv.HealthETrain.service.EqTypeService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class ExamEvaluationController {

    private final EqTypeService eqTypeService;
    private final EqOptionService eqOptionService;


    @GetMapping("/hello")
    @GlobalTransactional(name = "default", rollbackFor = Exception.class)
    public String hello() {
        return "123";
    }
}
