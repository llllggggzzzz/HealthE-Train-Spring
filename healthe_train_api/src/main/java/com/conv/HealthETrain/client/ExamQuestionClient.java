package com.conv.HealthETrain.client;

import com.conv.HealthETrain.domain.DTO.ExamQuestionDTO;
import com.conv.HealthETrain.domain.EqOption;
import com.conv.HealthETrain.domain.ExamQuestion;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liusg
 */
@FeignClient("exam-evaluation")
public interface ExamQuestionClient {

    @GetMapping("/examQuestion/questionBank/{id}")
    ApiResponse<List<ExamQuestionDTO>> getExamQuestionsDetailByQuestionBankId(@PathVariable("id") Long qbId);

    @PostMapping("/examQuestion/new")
    ApiResponse<ExamQuestion> addExamQuestion(@RequestBody ExamQuestion examQuestion);

    @PutMapping("/examQuestion/update")
    ApiResponse<ExamQuestion> updateExamQuestion(@RequestBody ExamQuestion examQuestion);

    @GetMapping("/examQuestion/eqType/name")
    ApiResponse<Long> getEqTypeIdByEqTypeName(@RequestParam String eqTypeName);

    @PostMapping("/examQuestion/new/note")
    ApiResponse<Note> addNote(@RequestBody Note note);

    @PutMapping("/examQuestion//update/note")
    ApiResponse<Note> updateNote(@RequestBody Note note);

    @PostMapping("/examQuestion/new/option")
    ApiResponse<EqOption> addOption(@RequestBody EqOption eqOption);

    @PostMapping("/examQuestion/update/option")
    ApiResponse<EqOption> updateOptionByEqId(@RequestBody EqOption eqOption);

    @GetMapping("/examQuestion/{id}")
    ApiResponse<ExamQuestion> getExamQuestionByEqId(@PathVariable("id") Long eqId);

    @DeleteMapping("/examQuestion/delete/note/{id}")
    ApiResponse<Object> deleteNote(@PathVariable("id") Long noteId);

    @DeleteMapping("/examQuestion/delete/{id}")
    ApiResponse<Object> deleteExamQuestion(@PathVariable("id") Long eqId);

    @DeleteMapping("/examQuestion/delete/option/{id}")
    ApiResponse<Object> deleteOptionByEqId(@PathVariable("id") Long eqId);
}
