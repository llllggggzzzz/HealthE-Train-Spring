package com.conv.HealthETrain.controller;
import cn.hutool.core.lang.Console;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.DTO.NoteDTO;
import com.conv.HealthETrain.domain.DTO.NoteInfoDTO;
import com.conv.HealthETrain.domain.DTO.RecentNoteDTO;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.domain.NoteLinkRepository;
import com.conv.HealthETrain.domain.RecentFile;
import com.conv.HealthETrain.domain.Repository;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.NoteLinkRepositoryService;
import com.conv.HealthETrain.service.NoteService;
import com.conv.HealthETrain.service.RecentFileService;
import com.conv.HealthETrain.utils.ConfigUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;



import java.util.ArrayList;
import java.util.List;

import static com.conv.HealthETrain.enums.ResponseCode.*;
import static com.conv.HealthETrain.utils.ImageUploadHandler.uploadBase64ToGitHub;
import static com.conv.HealthETrain.utils.ImageUploadHandler.uploadFileToGitHub;

@RestController
@AllArgsConstructor
@RequestMapping("/note")
@Slf4j
public class NoteController {

    private final NoteService noteService;
    private final NoteLinkRepositoryService noteLinkRepositoryService;
    private final InformationPortalClient informationPortalClient;
    private final RecentFileService recentFileService;
//    private final NgrokAgentApiClient ngrokAgentApiClient;

    /**
    * @Description: 获取对应知识库的note
    * @Param: repository_id 知识库id
    * @return: List<Note>
    * @Author: flora
    * @Date: 2024/7/8
    */
    @GetMapping("/repository/{repository_id}")
    public ApiResponse<List<NoteInfoDTO>> getNoteListByRepositoryId(@PathVariable Long repository_id) {
        // 获取对应连接表的项
        List<NoteLinkRepository> noteLinkRepositoryList = noteLinkRepositoryService.findNoteLinkRepositoryListByRepositoryId(repository_id);
        List<NoteInfoDTO> noteInfoDTOList = new ArrayList<>();
        if(noteLinkRepositoryList != null){
            for (NoteLinkRepository noteLinkRepository: noteLinkRepositoryList){
                Long noteId = noteLinkRepository.getNoteId();
                Note note = noteService.findNoteByNoteId(noteId);
                Long userId = note.getUserId();
                String userName = informationPortalClient.getUser(userId).getUsername();
                String cover = informationPortalClient.getUser(userId).getCover();
                NoteInfoDTO noteInfoDTO = new NoteInfoDTO(note, userName, cover);
                noteInfoDTOList.add(noteInfoDTO);
            }
            log.info("获取知识库笔记成功，noteInfoDTOList为 "+ noteInfoDTOList);
            return ApiResponse.success(noteInfoDTOList);
        }else{
            log.info("该知识库无笔记");
            return ApiResponse.success();
        }
    }

    /**
    * @Description: 添加一个note的item,同时与知识库关联
    * @Param: note，noteLinkRepository
    * @return: Boolean
    * @Author: flora
    * @Date: 2024/7/8
    */
    @PostMapping("")
    public ApiResponse<Boolean> addNote(@RequestBody NoteDTO noteDTO){
        // 添加note的一个item
        Long addNoteId = noteService.addNoteItem(noteDTO.getNote());
        if(addNoteId != null){
            // 添加note和repository的关联
            noteDTO.getNoteLinkRepository().setNoteId(addNoteId);
            Boolean isAddLinkSuccess = noteLinkRepositoryService.addNoteLinkRepository(noteDTO.getNoteLinkRepository());
            if(isAddLinkSuccess){
                log.info("添加note "+ addNoteId +" 和repository"+ noteDTO.getNoteLinkRepository().getRepositoryId()+"的关联成功");
                return ApiResponse.success(true);
            }else{
                log.info("添加note "+ addNoteId +" 和repository"+ noteDTO.getNoteLinkRepository().getRepositoryId()+"的关联不成功");
                return ApiResponse.error(NOT_IMPLEMENTED);
            }
        }else{
            log.info("添加noteItem不成功");
            return ApiResponse.error(NOT_IMPLEMENTED);
        }
    }
    /**
    * @Description: 更新note的内容
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/9
    */
    @PutMapping("/{noteId}")
    public ApiResponse<Boolean> updateNote(@PathVariable Long noteId, @RequestBody Note note){
        Boolean isSuccess = noteService.updateNote(noteId, note);
        if(isSuccess){
            log.info("更新笔记"+ noteId +"成功");
            return ApiResponse.success(true);
        }else{
            log.info("更新笔记"+ noteId +"失败");
            return ApiResponse.error(NOT_MODIFIED);
        }
    }

    /**
    * @Description: 获取对应noteId的Note
    * @Param:
    * @return: Note
    * @Author: flora
    * @Date: 2024/7/8
    */
    @GetMapping("/noteItem/{noteId}")
    public ApiResponse<Note> getNoteByNoteId(@PathVariable Long noteId){
        Note note = noteService.findNoteByNoteId(noteId);
        if(note != null){
            log.info("获取到noteId为" + noteId + "的note");
            return ApiResponse.success(note);
        }else{
            log.error("没有成功获取到对应id的note");
            return ApiResponse.error(NOT_FOUND);
        }
    }
    /**
    * @Description: 修改笔记的可见性（0私有，1局部公开，2全局公开）
    * @Param:
    * @return: Boolean
    * @Author: flora
    * @Date: 2024/7/8
    */
    @PutMapping("/visibility/{noteId}/{visibility}")
    public ApiResponse<Boolean> updateVisibilityOfNote(@PathVariable Long noteId,@PathVariable int visibility){
        Boolean isSuccess = noteService.updateNoteVisibility(noteId, visibility);
        return ApiResponse.success(isSuccess);
    }
    /**
    * @Description: 获取全局公开的笔记,用于社区
    * @Param: 
    * @return: 
    * @Author: flora
    * @Date: 2024/7/8
    */
    @GetMapping("/open")
    public ApiResponse<List<NoteInfoDTO>> getFullOpenNoteList(){
        List<Note> noteList = noteService.findFullOpenNoteList();
        List<NoteInfoDTO> noteInfoDTOList = new ArrayList<>();
        if(noteList != null){
            for(Note note: noteList){
                Long userId = note.getUserId();
                String userName = informationPortalClient.getUser(userId).getUsername();
                String cover = informationPortalClient.getUser(userId).getCover();
                NoteInfoDTO noteInfoDTO = new NoteInfoDTO(note, userName, cover);
                noteInfoDTOList.add(noteInfoDTO);
            }
            log.info("获取全局公开笔记成功");
            return ApiResponse.success(noteInfoDTOList);
        }else{
            log.error("获取全局公开笔记失败");
            return ApiResponse.error(NOT_FOUND);
        }
    }

    /**
    * @Description: 获取最近访问的笔记
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/10
    */

    @GetMapping("/recent/{userId}")
    public ApiResponse<List<RecentNoteDTO>> getRecentFile(@PathVariable Long userId){
        List<RecentFile> recentFileList = recentFileService.getRecentFile(userId);
        List<Long> recentNoteIdList = recentFileList.stream().map(RecentFile::getNoteId).toList();
        List<RecentNoteDTO> recentNoteDTOList = new ArrayList<>();
        for(Long noteId : recentNoteIdList){
            Note note = noteService.findNoteByNoteId(noteId);
            NoteLinkRepository noteLinkRepository = noteLinkRepositoryService.findNoteLinkRepositoryByNoteId(noteId);
            String username = informationPortalClient.getUser(note.getUserId()).getUsername();
            String cover = informationPortalClient.getUser(note.getUserId()).getCover();
            RecentNoteDTO recentNoteDTO = new RecentNoteDTO(note, noteLinkRepository, username, cover);
            recentNoteDTOList.add(recentNoteDTO);
        }
        log.info("获取用户" + userId + "的最近访问成功");
        return ApiResponse.success(recentNoteDTOList);

    }
    /**
    * @Description: 模糊搜索社区提问的问题
    * @Param:
    * @return: 返回List<NoteInfoDTO>
    * @Author: flora
    * @Date: 2024/7/11
    */
    @GetMapping("/searchQuestion/{query}")
    public ApiResponse<List<NoteInfoDTO>> getSearchQuestionMatch(@PathVariable String query){
        List<Note> noteList = noteService.findNoteListByNoteTitle(query);
        List<NoteInfoDTO> noteInfoDTOList = new ArrayList<>();
        for(Note note: noteList){
            String username = informationPortalClient.getUser(note.getUserId()).getUsername();
            String cover = informationPortalClient.getUser(note.getUserId()).getCover();
            NoteInfoDTO noteInfoDTO = new NoteInfoDTO(note, username, cover);
            noteInfoDTOList.add(noteInfoDTO);
        }
        if(noteInfoDTOList != null){
            log.info("获取模糊搜索问题成功！");
            return ApiResponse.success(noteInfoDTOList);
        }else{
            log.info("无此类型问题");
            return ApiResponse.success();
        }
    }
    /**
    * @Description: 共享笔记链接
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/12
    */
//    @GetMapping("/generate-link")
//    public ApiResponse<String> generateLink() {
//        String httpsTunnelUrl = ngrokAgentApiClient.getHttpsTunnelUrl();
//        String noteUrl = httpsTunnelUrl + "/knowledgeBase";
//        if(noteUrl != null){
//            log.info("创建链接成功！");
//            return ApiResponse.success(noteUrl);
//        }else{
//            log.error("创建链接失败");
//            return ApiResponse.error(NOT_IMPLEMENTED);
//        }
//    }

    /**
    * @Description: 上传base64编码返回图床上的url
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/17
    */
    @PostMapping("/uploadImage")
    public ApiResponse<String> uploadImage(@RequestBody String base64Encode){
        // 设置参数
        String token = ConfigUtil.getImgSaveToken();
        String repo = ConfigUtil.getImgSaveRepo();
        String pathPrefix = ConfigUtil.getImgSaveFolder();
        String commitMessage = "上传图片到图床";
        log.info(base64Encode);
        // 上传图片
        String imageUrl = uploadBase64ToGitHub(token, repo, pathPrefix, base64Encode, commitMessage);
        if(imageUrl != null){
            log.info("上传图片成功,返回url为" + imageUrl);
            return ApiResponse.success(imageUrl);
        }else{
            log.error("上传失败");
            return ApiResponse.error(NOT_IMPLEMENTED);
        }
    }
}
