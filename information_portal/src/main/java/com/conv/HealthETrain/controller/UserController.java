package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.domain.UserLinkCategory;
import com.conv.HealthETrain.domain.dto.UserDetailDTO;
import com.conv.HealthETrain.domain.dto.UserStatistic;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.mapper.UserMapper;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.TeacherDetailService;
import com.conv.HealthETrain.service.UserLinkCategoryService;
import com.conv.HealthETrain.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liusg
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final UserService userService;
    private final TeacherDetailService teacherDetailService;
    private final UserLinkCategoryService userLinkCategoryService;
    private static final ObjectMapper mapper = new ObjectMapper();
    /**
     * 用户登录接口
     * @param loginUser
     */
    @PostMapping("/login/account")
    public ApiResponse<HashMap<String, Object>> loginByAccount(@RequestBody User loginUser) {
        String token = userService.loginByAccount(loginUser);
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);

        return ApiResponse.success(data);
    }

    /**
     * 用户登录接口
     * @param loginUser
     */
    @PostMapping("/login/phone")
    public void loginByPhone(@RequestBody User loginUser) {
        userService.loginByPhone(loginUser);
    }

    /**
     * 邮箱用户登录接口
     * @param loginUser
     */
    @PostMapping("/login/email/code")
    public ApiResponse<Object> sendEmailCode(@RequestBody User loginUser) {
        userService.sendEmailCode(loginUser);
        return ApiResponse.success();
    }

    @PostMapping("/login/email/verify/{code}")
    public ApiResponse<Boolean> verifyEmail(@RequestBody User loginUser, @PathVariable("code") String code) {
        boolean result = userService.verifyEmail(loginUser, code);
        if (result) {
            return ApiResponse.success();
        }

        return ApiResponse.error(ResponseCode.BAD_REQUEST);
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

    @GetMapping("/test")
    public ApiResponse<Object> test() {
        return ApiResponse.success();
    }

    // 统计网站用户人数以及类型
    @GetMapping("/statistic")
    public ApiResponse<UserStatistic> getUserStatistic() throws JsonProcessingException {
        String cacheKey = "userStatistic"; // 定义缓存的 key

        // 尝试从 Redis 中获取缓存数据
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String cachedUserStatistic = ops.get(cacheKey);

        // 如果缓存中有数据，直接返回缓存数据
        if (cachedUserStatistic != null) {
            // 假设返回的是 JSON 格式的字符串，需要转换为对象
            UserStatistic userStatistic = mapper.readValue(cachedUserStatistic,UserStatistic.class);
            System.out.println(userStatistic);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", userStatistic);
        }
        // 如果缓存中没有数据，则从数据库获取并放入 Redis 缓存
        UserStatistic userStatistic = new UserStatistic();
        Map<String, Integer> studentType = userLinkCategoryService.countStudentsByCategory();
        Map<String, Integer> teacherType = teacherDetailService.countTeachersByQualification();
        userStatistic.setStudentNumber(userLinkCategoryService.countStudents());
        userStatistic.setTeacherNumber(teacherDetailService.countTeachers());
        userStatistic.setStudentType(studentType);
        userStatistic.setTeacherType(teacherType);

        // 将查询到的数据转换为 JSON 字符串并存入 Redis 缓存，设置有效期为 10 分钟
        String jsonUserStatistic = mapper.writeValueAsString(userStatistic);
        ops.set(cacheKey, jsonUserStatistic, 10, TimeUnit.MINUTES);

        return ApiResponse.success(ResponseCode.SUCCEED, "成功", userStatistic);
    }

    // 获取网站所有用户的较详细信息
    @GetMapping("/statistic/details")
    public ApiResponse<List<UserDetailDTO>> getUserDetailsStatistic() throws JsonProcessingException {
        String cacheKey = "UserDetailsStatistic";
        String cachedData = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            List<UserDetailDTO> userDetails = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, UserDetailDTO.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", userDetails);
        }
        List<UserDetailDTO> userDetails = userService.getAllUsersWithDetails();
        String jsonData = mapper.writeValueAsString(userDetails);
        stringRedisTemplate.opsForValue().set(cacheKey, jsonData, 10, TimeUnit.MINUTES);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", userDetails);
    }

    // 获取网站所有学生用户的信息
    @GetMapping("/students")    public List<User> getAllStudentsInfo(){
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
                user.setPassword(userService.encryption("123456"));
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
                if (!"1".equals(userDetailDTO.getIsTeacher()) && userDetailDTO.getCategoryId() < 8 &&userDetailDTO.getCategoryId() >0) {
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
}
