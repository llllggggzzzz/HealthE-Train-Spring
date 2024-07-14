package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.domain.UserLinkCategory;
import com.conv.HealthETrain.domain.dto.TeacherDetailDTO;
import com.conv.HealthETrain.domain.dto.UserDetailDTO;
import com.conv.HealthETrain.domain.dto.UserStatistic;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liusg
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final TeacherDetailService teacherDetailService;
    private final UserLinkCategoryService userLinkCategoryService;
    private final PositionService positionService;
    private final CategoryService categoryService;
    private final QualificationService qualificationService;
    /**
     * 用户登录接口
     * @param loginUser
     */
    @PostMapping("/login/account")
    public ApiResponse<HashMap<String, Object>> loginByAccount(@RequestBody User loginUser) {
        String token = userService.loginByAccount(loginUser);
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userService.getUserByAccount(loginUser.getAccount()));

        return ApiResponse.success(data);
    }

    /**
     * 邮箱用户登录接口
     * @param loginUser
     */
    @PostMapping("/login/email/code")
    public ApiResponse<Object> sendEmailCode(@RequestBody User loginUser) {
        boolean result = userService.sendEmailCode(loginUser);

        if (!result) {
            return ApiResponse.error(ResponseCode.BAD_REQUEST, "邮箱不存在");
        }

        return ApiResponse.success();
    }

    /**
     * 验证邮箱验证码
     * @param loginUser
     * @param code
     * @return
     */
    @PostMapping("/login/email/verify/{code}")
    public ApiResponse<Object> verifyEmail(@RequestBody User loginUser, @PathVariable("code") String code) {
        String token = userService.verifyEmail(loginUser, code);
        if ("".equals(token)) {
            return ApiResponse.error(ResponseCode.BAD_REQUEST, "验证码错误");
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userService.getUserByEmail(loginUser.getEmail()));

        return ApiResponse.success(data);
    }

    /**
     * 用户注册接口
     * @param registerUser
     */
    @PostMapping("/register")
    public ApiResponse<Object> register(@RequestBody User registerUser) {
        userService.register(registerUser);

        return ApiResponse.success();
    }

    /**
     * 检验账号是否存在
     * @param account
     * @return
     */
    @PostMapping("/check/account")
    public ApiResponse<Object> checkAccount(@RequestBody String account) {
        // 去除掉前后"
        account = account.substring(1, account.length() - 1);

        System.out.println(account);
        User user = userService.getUserByAccount(account);

        System.out.println(user);
        if (user == null) {
            return ApiResponse.success();
        }
        return ApiResponse.error(ResponseCode.BAD_REQUEST, "该账号已被注册");
    }

    // 统计网站用户人数以及类型
    @GetMapping("/statistic")
    public ApiResponse<UserStatistic> getUserStatistic(){
        UserStatistic userStatistic = new UserStatistic();
        Map<String,Integer> studentType = userLinkCategoryService.countStudentsByCategory();
        Map<String,Integer> teacherType = teacherDetailService.countTeachersByQualification();
        userStatistic.setStudentNumber(userLinkCategoryService.countStudents());
        userStatistic.setTeacherNumber(teacherDetailService.countTeachers());
        userStatistic.setStudentType(studentType);
        userStatistic.setTeacherType(teacherType);
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",userStatistic);
    }

    // 获取网站所有用户的较详细信息
    @GetMapping("/statistic/details")
    public ApiResponse<List<UserDetailDTO>> getUserDetailsStatistic(){
        List<UserDetailDTO> userDetailDTOList = new ArrayList<>();
        userDetailDTOList = userService.getAllUsersWithDetails();
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",userDetailDTOList);
    }

    // 获取网站所有学生用户的信息
    @GetMapping("/students")
    public List<User> getAllStudentsInfo(){
        List<User> userList = new ArrayList<>();
        userList = userService.findStudentUserList();
        return userList;
    }

    // 后台管理界面批量增加用户
    @PutMapping("/batchRegister")
    public ApiResponse<Object> batchRegisterUserFromBackground(@RequestBody List<UserDetailDTO> userDetailDTOList){
        try {
            for (UserDetailDTO userDetailDTO : userDetailDTOList) {
                // 插入到 User 表中
                User user = new User();
                // 统一注册默认密码为123456
                user.setPassword("123456");
                user.setUsername(userDetailDTO.getUsername());
                // 头像无数据，不存储
                user.setAccount(userDetailDTO.getAccount());
                user.setPhone(userDetailDTO.getPhone());
                user.setEmail(userDetailDTO.getEmail());
                userService.save(user);
                Long userId = user.getUserId();

//              根据条件插入到 UserLinkCategory 表中
//              9为出卷人
                if ("1".equals(userDetailDTO.getIsTeacher())) {
                    UserLinkCategory ulc = new UserLinkCategory();
                    ulc.setCategoryId(9);
                    ulc.setUserId(userId);
                    userLinkCategoryService.save(ulc);
                }
                // 8为教师
                if ("1".equals(userDetailDTO.getAuthority())) {
                    UserLinkCategory ulc = new UserLinkCategory();
                    ulc.setCategoryId(8);
                    ulc.setUserId(userId);
                    userLinkCategoryService.save(ulc);
                }
                // 如果不是教师并且此类型不是代表出卷人，就表示是专业类型
                if (!"1".equals(userDetailDTO.getIsTeacher()) && userDetailDTO.getCategoryId() < 8) {
                    UserLinkCategory ulc = new UserLinkCategory();
                    ulc.setCategoryId(userDetailDTO.getCategoryId());
                    ulc.setUserId(userId);
                    userLinkCategoryService.save(ulc);
                }
            }
            return ApiResponse.success(ResponseCode.SUCCEED, "成功");
        } catch (Exception e) {
            // 如果有异常，返回相应的错误信息
            return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public User getUserInfo(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    /**
     * 检验邮箱是否存在
     * @param email
     * @return
     */
    @PostMapping("/check/email")
    public ApiResponse<Object> checkEmail(@RequestBody String email) {
        // 去除掉前后"
        email = email.substring(1, email.length() - 1);

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ApiResponse.success();
        }
        return ApiResponse.error(ResponseCode.BAD_REQUEST, "该邮箱已被注册");
    }

    /**
     * 判断用户是否是教师
     * @param userId
     * @return
     */
    @GetMapping("/teacher/exists/{id}")
    public ApiResponse<Object> isTeacher(@PathVariable("id") String userId) {
        TeacherDetail teacherDetail = teacherDetailService.getByUserId(Long.parseLong(userId));

        if (teacherDetail == null) {
            return ApiResponse.success(ResponseCode.BAD_REQUEST, "该用户不是教师");
        }

        return ApiResponse.success("该用户是教师");
    }

    /**
     * 获取教师详细信息
     * @param userId
     * @return
     */
    @GetMapping("/teacher/{id}")
    public ApiResponse<Object> getTeacherDetail(@PathVariable("id") String userId) {
        TeacherDetail teacherDetail = teacherDetailService.getByUserId(Long.parseLong(userId));
        TeacherDetailDTO teacherDetailDTO = new TeacherDetailDTO();

        // 从 TeacherDetail 中获取数据，放入 TeacherDetailDTO 中
        teacherDetailDTO.setTeacherId(teacherDetail.getTdId());
        teacherDetailDTO.setUserId(teacherDetail.getUserId());
        teacherDetailDTO.setRealName(teacherDetail.getRealName());
        teacherDetailDTO.setPosition(positionService.getPositionById(teacherDetail.getPositionId()));
        teacherDetailDTO.setCategory(categoryService.getCategoryById(teacherDetail.getCategoryId()));
        teacherDetailDTO.setQualification(qualificationService.getQualificationById(teacherDetail.getQualificationId()));

        return ApiResponse.success(teacherDetailDTO);
    }
}
