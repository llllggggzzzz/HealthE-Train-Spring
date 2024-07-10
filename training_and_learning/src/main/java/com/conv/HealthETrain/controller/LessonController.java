package com.conv.HealthETrain.controller;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.domain.DTO.*;
import com.conv.HealthETrain.domain.POJP.Chapter;
import com.conv.HealthETrain.domain.Checkpoint;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.conv.HealthETrain.domain.Section;
import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.domain.VO.SectionCheckVO;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/study")
@AllArgsConstructor
@Slf4j
public class LessonController {

    private final LessonLinkUserService lessonLinkUserService;

    private final LessonLinkTeacherService lessonLinkTeacherService;

    private final LessonService lessonService;

    private final InformationPortalClient informationPortalClient;

    private final RecentLessonsService recentLessonsService;

    private final ChapterService chapterService;

    private final SectionService sectionService;

    private final CheckpointService checkpointService;

    private final LessonLinkCategoryService lessonLinkCategoryService;


    /**
     * @description 根据课程列表查询DTO并返回，提取为私有方法共多个方法使用
     * @param choosedLessons 课程id列表
     * @return LessonInfoDTO
     */
    private List<LessonInfoDTO> getLessonInfoDTOS(List<Long> choosedLessons) {
        if (choosedLessons.isEmpty()) return ListUtil.empty();
        List<LessonInfoDTO> lessonInfoDTOList  = CollUtil.newArrayList();

        for (Long lessonId : choosedLessons) {
            // 3. 查询课程信息
            Lesson lesson = lessonService.getById(lessonId);
            if (lesson == null) {
                // 查询不到课程信息，直接跳过其他查询
                log.error("未查询到课程信息 - 课程ID: {}", lessonId);
                continue;
            }
            // 2. 查lesson_link_teacher, 得到教师id
            List<Long> teachersByLessonId = lessonLinkTeacherService.getTeachersByLessonId(lessonId);
            // 3. 根据教师id 得到教师姓名, 返回教师姓名 进行openfeign调用
            List<String> teacherNames = CollUtil.newArrayList();
            for (Long teacherId : teachersByLessonId) {
                TeacherDetail teacherDetail = informationPortalClient.getTeacherDetailById(teacherId);
                if (teacherDetail != null) {
                    teacherNames.add(teacherDetail.getRealName());
                } else {
                    log.error("查询到教师ID: {}, 但未查询到教师信息", teacherId);
                }
            }
            // 4. 拼接
            LessonInfoDTO lessonInfoDTO = new LessonInfoDTO(lesson, teacherNames);
            lessonInfoDTOList.add(lessonInfoDTO);
        }
        return lessonInfoDTOList;
    }

    /**
     * @description 查询用户课程，对应前端渲染LessonCard
     * @param id 用户id
     * @return 对应DTO
     */
    @GetMapping("/user/{id}")
    public ApiResponse<List<LessonInfoDTO>> getLessons(@PathVariable("id") Long id) {
        // 1. 查课程表，得到课程信息
        List<Long> choosedLessons = lessonLinkUserService.getChooesdLessons(id);
        if (choosedLessons.isEmpty()) {
            // 返回用户未选课消息
            return ApiResponse.success(ResponseCode.SUCCEED, "用户未选课");
        }

        List<LessonInfoDTO> lessonInfoDTOS = getLessonInfoDTOS(choosedLessons);
        // 5. 返回
        return ApiResponse.success(lessonInfoDTOS);
    }


    /**
     * @description 查询课程信息,在课程界面初始化时使用
     * @param id 课程ID
     * @return 返回课程信息
     */
    @GetMapping("/lesson/{id}/info")
    public ApiResponse<List<LessonInfoDTO>> getLessonInfo(@PathVariable("id") Long id) {
        ArrayList<Long> list = CollUtil.newArrayList();
        list.add(id);
        List<LessonInfoDTO> lessonInfoDTOS = getLessonInfoDTOS(list);
        // 5. 返回
        log.info("查询到课程: {}", lessonInfoDTOS.get(0).toString());
        return ApiResponse.success(lessonInfoDTOS);
    }

    /**
     * @description 查询最近访问
     * @param id 用户id
     * @return 对应DTO
     */
    @GetMapping("/user/{id}/recent")
    public ApiResponse<List<LessonInfoDTO>> getRecentLessons(@PathVariable("id") Long id) {
        // 根据用户id查询最近信息表
        List<Long> lessonIds = recentLessonsService.getLessonIdsByUserId(id);
        if (lessonIds.isEmpty()) return ApiResponse.success(ResponseCode.SUCCEED, "未寻找到最近访问");
        List<LessonInfoDTO> lessonInfoDTOS = getLessonInfoDTOS(lessonIds);
        // 5. 返回
        return ApiResponse.success(lessonInfoDTOS);
    }

    /**
     * @description 查询课程对应的章节信息
     * @param id 课程id
     * @return List<ChapterDTO> 返回对应DTO
     */
    @GetMapping("/lesson/{id}")
    public ApiResponse<List<ChapterDTO>> getLessonDetail(@PathVariable("id") Long id,
                                                         @RequestParam(value = "user", required = false) Long userId) {
        List<Chapter> chapters = chapterService.getChaptersByLessonId(id);
        // 根据章节信息查询课程
        if (chapters.isEmpty()) {
            log.warn("查询课程信息不存在");
            return ApiResponse.error(ResponseCode.NOT_FOUND, "课程信息不存在");
        }

        List<ChapterDTO> chapterDTOS = CollUtil.newArrayList();
        log.info("查询到章节: {}", chapters);
        // 根据章信息查询section信息
        for (Chapter chapter: chapters) {
            ChapterDTO chapterDTO = new ChapterDTO();
            chapterDTO.setChapter(chapter);
            List<Section> sections = sectionService.getSectionsByChapterId(chapter.getChapterId());
            List<SectionCheckVO> sectionCheckVOStream = sections.stream().map(section -> {
                SectionCheckVO vo = new SectionCheckVO();
                vo.setSection(section);
                return vo;
            }).toList();
            chapterDTO.setSectionCheckVOS(sectionCheckVOStream);
            if(!sections.isEmpty()) {
                // 用户未登陆,不查询课程进度
                if (userId == null) {
                    // 不做处理
                } else {
                    // 将拥有检查点的信息加入checkpoint
                    for (SectionCheckVO sectionCheckVO: sectionCheckVOStream) {
                        // 查询checkpoint
                        Checkpoint checkpoint = checkpointService.getCheckpointBySectionId(sectionCheckVO.getSection().getSectionId(), userId);
                        if(checkpoint != null) {
                            sectionCheckVO.setCheckpoint(checkpoint);
                        }
                    }
                }

            } else {
                log.warn("章节: {} 未设置Section", chapter.getChapterId());
            }
            chapterDTOS.add(chapterDTO);
        }

        return ApiResponse.success(chapterDTOS);
    }

    @GetMapping("/section/{id}")
    public Section getSectionInfo(@PathVariable("id") Long id) {
        return sectionService.getById(id);
    }

    @GetMapping("/checkpoint/{sectionId}/user/{userId}")
    public Checkpoint getCheckPoint(@PathVariable("sectionId") Long sectionId,
                                    @PathVariable("userId") Long userId) {
        return checkpointService.getCheckpointBySectionId(sectionId, userId);
    }

    // 查询所有课程的基本信息和课程类别
    @GetMapping("/lesson/statistic/details")
    public ApiResponse<List<LessonCategoryInfoDTO>> getLessonCategoryInfo()
    {
        List<LessonCategoryInfoDTO> lessonCategoryInfoDTOS = new ArrayList<>();
        lessonCategoryInfoDTOS = lessonService.getLessonCategoryInfo();
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",lessonCategoryInfoDTOS);
    }

    // 统计选修和必修的课程总数以及细分为七类课程的情况。
    @GetMapping("/lesson/statistic")
    public ApiResponse<LessonStatistic> getLessonStatistic(){
        LessonStatistic lessonStatistic = new LessonStatistic();
        lessonStatistic.setCompulsory(lessonLinkCategoryService.countStudentsByLessonType(1));
        lessonStatistic.setElective(lessonLinkCategoryService.countStudentsByLessonType(0));
        lessonStatistic.setCompulsoryType(lessonLinkCategoryService.countCategoriesByLessonType(1));
        lessonStatistic.setElectiveType(lessonLinkCategoryService.countCategoriesByLessonType(0));
        return  ApiResponse.success(ResponseCode.SUCCEED,"成功",lessonStatistic);
    }

    // 查询所有课程的基本信息[只有封面和名称]，不复用前面的是为了减少联表查询
    @GetMapping("/lesson/simpleInfo")
    public ApiResponse<List<LessonSimpleInfoDTO>> getAllLessonSimpleInfo(){
        List<LessonSimpleInfoDTO> lessonSimpleInfoDTOS = new ArrayList<>();
        lessonSimpleInfoDTOS = lessonService.getAllSimpleInfo();
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",lessonSimpleInfoDTOS);
    }

    // 根据课程的id查询chapter信息的List列表以及每个chapter在学的人数
    @GetMapping("/lesson/{id}/chapterStatistic")
    public ApiResponse<List<ChapterStatistic>> getChaptersByLessonId(@PathVariable("id") Long lessonId){
        List<Chapter> chapters = chapterService.getChaptersByLessonId(lessonId);
        List<ChapterStatistic> chapterStatistics = new ArrayList<>();

        for (Chapter chapter : chapters) {
            // 获取通过该章节的独立用户数量
            int userCount = checkpointService.getCountUserByChapterId(chapter.getChapterId());
            ChapterStatistic statistic = new ChapterStatistic();
            statistic.setChapter(chapter);
            statistic.setCount(userCount);
            chapterStatistics.add(statistic);
        }
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",chapterStatistics);
    }

    // 修改特定课程的必修选修类型
    @PutMapping("/lesson/{id}/{lessonType}")
    public ApiResponse<String> updateLessonTypeByLessonId(@PathVariable("id") Long lessonId,
                                                          @PathVariable("lessonType") Integer lessonType)
    {
        boolean flag = lessonService.updateLessonType(lessonId,lessonType);
        if(flag)
            return ApiResponse.success(ResponseCode.SUCCEED,"成功");
        else
            return ApiResponse.error(ResponseCode.NOT_FOUND,"未找到");
    }

    // 查询用户的必修课学习总进度
    @GetMapping("/users/lesson/CompulsoryProcess")
    public ApiResponse<List<AllProcessDTO>> getAllUserCompulsoryLessonProcess()
    {
        List<AllProcessDTO> allProcessDTOS = new ArrayList<>();
        // 先获取学生信息
        List<User> userList = informationPortalClient.getAllStudentsInfo();
        System.out.println(userList);
        for (User user : userList)
        {
            AllProcessDTO allProcessDTO = new AllProcessDTO();
            allProcessDTO.setUsername(user.getUsername());
            allProcessDTO.setCover(user.getCover());
            allProcessDTO.setAccount(user.getAccount());
            allProcessDTO.setUserId(user.getUserId());
            int learnedSections = checkpointService.countLearnedSectionsByUserId(user.getUserId());
            int totalSections = lessonLinkUserService.getSectionCountsByUserId(user.getUserId());
            System.out.println(learnedSections+","+totalSections);
            if(totalSections==0)
            {
                allProcessDTO.setProcessLine(0);
                allProcessDTO.setProcessLine(0);
                allProcessDTOS.add(allProcessDTO);
                continue;
            }
            allProcessDTO.setProcessRound(100*learnedSections/totalSections);
            allProcessDTO.setProcessLine(100*learnedSections/totalSections);
            allProcessDTOS.add(allProcessDTO);
        }
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",allProcessDTOS);
    }
}
