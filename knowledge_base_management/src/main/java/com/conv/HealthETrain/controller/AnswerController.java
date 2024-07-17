package com.conv.HealthETrain.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.Answer;
import com.conv.HealthETrain.domain.DTO.AnswerDTO;
import com.conv.HealthETrain.domain.DTO.NoteInfoDTO;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.AnswerService;
import com.conv.HealthETrain.service.AskService;
import com.conv.HealthETrain.service.NoteService;
import lombok.AllArgsConstructor;
import lombok.experimental.PackagePrivate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.conv.HealthETrain.enums.ResponseCode.*;

@RestController
@AllArgsConstructor
@RequestMapping("/answer")
@Slf4j
public class AnswerController {
    private final AnswerService answerService;
    private final NoteService noteService;
    private final InformationPortalClient informationPortalClient;
    private final AskService askService;

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
            answer.setNoteId(addNoteId);
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
    public ApiResponse<List<AnswerDTO>> getAnswerByAskId(@PathVariable Long askId){
        List<Answer> answerList = answerService.findAnswerListByAskId(askId);
        List<Long> noteIdList = answerList.stream().map(Answer::getNoteId).toList();
        List<Note> noteList = noteService.findNoteListByNoteIdList(noteIdList);
        List<AnswerDTO> answerDTOList = new ArrayList<>();
        for (Answer answer : answerList) {
            // 查找与当前 answer 关联的 note
            Note note = noteList.stream()
                    .filter(n -> n.getNoteId().equals(answer.getNoteId()))
                    .findFirst()
                    .orElse(null);

            if (note != null) {
                String username = informationPortalClient.getUser(note.getUserId()).getUsername();
                String cover = informationPortalClient.getUser(note.getUserId()).getCover();
                NoteInfoDTO noteInfoDTO = new NoteInfoDTO(note, username, cover);

                // 合并成 AnswerDTO
                AnswerDTO answerDTO = new AnswerDTO(answer, noteInfoDTO);
                answerDTOList.add(answerDTO);
            }
        }
        if(answerDTOList != null){
            log.info("获取回答列表成功");
            return ApiResponse.success(answerDTOList);
        }else{
            log.info("回答列表无内容");
            return ApiResponse.success(NO_CONTENT);
        }
    }
    /**
    * @Description: 获取回答全部的点赞数
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/11
    */
    @GetMapping("/likeNum/{answerId}")
    public ApiResponse<Integer> getLikeNumOfAnswer(@PathVariable Long answerId){
        Answer answer = answerService.findAnswerByAnswerId(answerId);
        if(answer != null){
            log.info("获取到" + answerId + "的点赞数");
            return ApiResponse.success(answer.getLikes());
        }else{
            log.info("未获取到点赞数");
            return ApiResponse.error(NOT_FOUND);
        }
    }

    /**
    * @Description: 根据用户的提问返回最匹配的答案
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/18
    */
    @GetMapping("/getAnswer/{content}")
    public ApiResponse<String> matchAnswerOfAsk(@PathVariable String content){
        List<Note> noteList = noteService.findMatchNoteByNoteContent(content);
        if(noteList.size() != 0){
            Long noteId = noteList.get(0).getNoteId();
            Long askId = askService.getAskByNoteId(noteId).getAskId();
            Answer answer = answerService.getBestAnswer(askId);
            Note note = noteService.findNoteByNoteId(answer.getNoteId());
            if(note != null){
                log.info("获取最佳回答成功");
                return ApiResponse.success(note.getNoteContent());
            }else{
                log.info("无最佳回答");
                return ApiResponse.success();
            }
        }else{
            log.info("无最佳回答");
            return ApiResponse.success();
        }
    }
}
