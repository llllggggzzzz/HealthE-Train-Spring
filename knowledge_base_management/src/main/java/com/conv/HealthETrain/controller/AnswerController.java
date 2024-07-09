package com.conv.HealthETrain.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.conv.HealthETrain.domain.Answer;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.AnswerService;
import com.conv.HealthETrain.service.NoteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.conv.HealthETrain.enums.ResponseCode.*;

@RestController
@AllArgsConstructor
@RequestMapping("/answer")
@Slf4j
public class AnswerController {
    private final AnswerService answerService;
    private final NoteService noteService;

    /**
    * @Description: 添加对应提问的回答
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/8
    */
    @PostMapping("/{askId}")
    public ApiResponse<Boolean> postAnswerOfAsk(@RequestBody Note note, @PathVariable Long askId){
        Long addNoteId = noteService.addNoteItem(note);
        if(addNoteId != null){
            Answer answer = new Answer();
            answer.setNoteId(note.getNoteId());
            answer.setAskId(askId);
            //初始化为0
            answer.setLikes(0);
            Boolean isAddAnswerSuccess = answerService.addAnswerOfAsk(answer);
            if(isAddAnswerSuccess){
                log.info("对于"+ askId +"设置回答成功");
                return ApiResponse.success(true);
            }else{
                log.info("对于"+ askId +"设置回答失败");
                return ApiResponse.error(NOT_IMPLEMENTED);
            }
        }else{
            log.info("添加回答的子内容笔记失败");
            return ApiResponse.error(NOT_IMPLEMENTED);
        }
    }

    /**
    * @Description: 为对应回答添加点赞+1
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/8
    */
    @PutMapping("/addLikes/{answerId}")
    public ApiResponse<Boolean> addLikesOfAnswer(@PathVariable Long answerId){
        Boolean isSuccess = answerService.updateLikesOfAnswer(answerId);
        if(isSuccess){
            log.info("点赞加一成功");
            return ApiResponse.success(true);
        }else{
            log.info("点赞加一成功");
            return ApiResponse.error(NOT_MODIFIED);
        }
    }
    /**
     * @Description: 为对应回答添加点赞-1
     * @Param:
     * @return:
     * @Author: flora
     * @Date: 2024/7/8
     */
    @PutMapping("/subLikes/{answerId}")
    public ApiResponse<Boolean> subLikesOfAnswer(@PathVariable Long answerId){
        Boolean isSuccess = answerService.subLikesOfAnswer(answerId);
        if(isSuccess){
            log.info("点赞减一成功");
            return ApiResponse.success(true);
        }else{
            log.info("点赞减一成功");
            return ApiResponse.error(NOT_MODIFIED);
        }
    }

    /**
    * @Description: 获取对应提问的回答
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/8
    */
    @GetMapping("/{askId}")
    public ApiResponse<List<Answer>> getAnswerByAskId(@PathVariable Long askId){
        List<Answer> answerList = answerService.findAnswerListByAskId(askId);
        if(answerList != null){
            log.info("获取回答列表成功");
            return ApiResponse.success(answerList);
        }else{
            log.info("回答列表无内容");
            return ApiResponse.success(NO_CONTENT);
        }
    }
}
