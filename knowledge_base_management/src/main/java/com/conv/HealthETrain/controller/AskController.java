package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.Ask;
import com.conv.HealthETrain.domain.DTO.NoteDTO;
import com.conv.HealthETrain.domain.DTO.NoteInfoDTO;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.AskService;
import com.conv.HealthETrain.service.NoteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.conv.HealthETrain.enums.ResponseCode.NOT_FOUND;
import static com.conv.HealthETrain.enums.ResponseCode.NOT_IMPLEMENTED;

@RestController
@AllArgsConstructor
@RequestMapping("/ask")
@Slf4j
public class AskController {
    private final NoteService noteService;
    private final AskService askService;
    private final InformationPortalClient informationPortalClient;

    /**
    * @Description: 添加社区提问
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/8
    */
    @PostMapping("")
    public ApiResponse<Boolean> postNewAsk(@RequestBody Note note){
        Long addNoteId = noteService.addNoteItem(note);
        if(addNoteId != null){
            Ask newAsk = new Ask();
            newAsk.setNoteId(addNoteId);
            Boolean isAddAskSuccess = askService.addOneAsk(newAsk);
            if(isAddAskSuccess){
                log.info("添加提问"+ newAsk.getAskId() +"成功");
                return ApiResponse.success(true);
            }else{
                log.info("添加提问"+ newAsk.getAskId() +"失败");
                return ApiResponse.error(NOT_IMPLEMENTED);
            }
        }else{
            log.info("添加提问"+ note.getNoteId() +"失败");
            return ApiResponse.error(NOT_IMPLEMENTED);
        }
    }
    /**
    * @Description: 获取全部社区提问
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/8
    */
    @GetMapping("/allAsk")
    public ApiResponse<List<NoteInfoDTO>> getAllAskList() {
        List<Ask> askList = askService.getAllAsk();
        List<Long> noteIdList = askList.stream().map(Ask::getNoteId).toList();
        List<Note> noteList = noteService.findNoteListByNoteIdList(noteIdList);
        List<NoteInfoDTO> noteInfoDTOList = new ArrayList<>();
        for (Note note : noteList) {
            String username = informationPortalClient.getUser(note.getUserId()).getUsername();
            String cover = informationPortalClient.getUser(note.getUserId()).getCover();
            NoteInfoDTO noteInfoDTO = new NoteInfoDTO(note, username, cover);
            noteInfoDTOList.add(noteInfoDTO);
        }
        if (noteInfoDTOList != null) {
            log.info("获取全部提问成功");
            return ApiResponse.success(noteInfoDTOList);
        } else {
            log.info("无提问");
            return ApiResponse.success();
        }
    }

    /**
    * @Description: 获取对应ask_id的提问
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/8
    */
    @GetMapping("/{askId}")
    public ApiResponse<Ask> getAskItemByAskId(@PathVariable Long askId){
        Ask ask = askService.getAskById(askId);
        if(ask != null){
            log.info("获取ID为" + askId + "的提问成功");
            return ApiResponse.success(ask);
        }else{
            log.info("获取ID为" + askId + "的提问失败");
            return ApiResponse.error(NOT_FOUND);
        }
    }

    /**
    * @Description: 获取对应noteId的提问
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/11
    */
    @GetMapping("/getAsk/{noteId}")
    public ApiResponse<Ask> getAskItemByNoteId(@PathVariable Long noteId){
        Ask ask = askService.getAskByNoteId(noteId);
        if(ask != null){
            log.info("获取noteId为" + noteId + "的提问成功");
            return ApiResponse.success(ask);
        }else{
            log.info("获取noteId为" + noteId + "的提问失败");
            return ApiResponse.error(NOT_FOUND);
        }
    }
}
