package com.conv.HealthETrain.cotroller;

import com.conv.HealthETrain.domain.QuestionBank;
import com.conv.HealthETrain.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questionBank")
public class questionBankController {
    private final QuestionBankService questionBankService;
    @GetMapping("/all")
    List<QuestionBank> getAllQuestionBank(){
        return questionBankService.getAllQuestionBanks();
    }
}
