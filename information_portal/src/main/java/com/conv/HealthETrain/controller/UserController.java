package com.conv.HealthETrain.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.domain.UserLinkCategory;
import com.conv.HealthETrain.domain.dto.PasswordDTO;
import com.conv.HealthETrain.domain.dto.TeacherDetailDTO;
import com.conv.HealthETrain.domain.dto.UserDetailDTO;
import com.conv.HealthETrain.domain.dto.UserStatistic;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.factory.JellyfinFactory;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.*;
import com.conv.HealthETrain.service.TeacherDetailService;
import com.conv.HealthETrain.service.UserLinkCategoryService;
import com.conv.HealthETrain.service.UserService;
import com.conv.HealthETrain.utils.*;
import com.conv.HealthETrain.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


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
    private final PositionService positionService;
    private final CategoryService categoryService;
    private final QualificationService qualificationService;
    private final UserServiceImpl userServiceImpl;

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
        log.info(registerUser.toString());
        registerUser.setUserId(null);
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
    public ApiResponse<UserStatistic> getUserStatistic() throws JsonProcessingException {
        String cacheKey = "userStatistic"; // 定义缓存的 key

        // 尝试从 Redis 中获取缓存数据
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String cachedUserStatistic = ops.get(cacheKey);

        // 如果缓存中有数据，直接返回缓存数据
        if (cachedUserStatistic != null) {
            // 假设返回的是 JSON 格式的字符串，需要转换为对象
            UserStatistic userStatistic = mapper.readValue(cachedUserStatistic,UserStatistic.class);
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


    @GetMapping("/account/{account}")
    public User getUserInfoByAccount(@PathVariable("account") String account) {
        return userService.getUserByAccount(account);
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

    @PostMapping("/update/password")
    public ApiResponse<Boolean> updateUserPassword(@RequestBody PasswordDTO passwordDTO){
        boolean flag= userService.updatePassword(passwordDTO);
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",flag);
    }
    @PostMapping("/update/userInfo")
    public ApiResponse<String> updateUserInfo(@RequestBody User user){
        boolean flag = userService.isExistAccount(user.getUserId(),user.getAccount());
        if(!flag)
            return ApiResponse.error(ResponseCode.GONE,"失败","error1");
        else{
            boolean flag2 = userService.updateUserInfo(user);
            if(flag2)
                return ApiResponse.success(ResponseCode.SUCCEED,"成功","success");
            else
                return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED,"失败","error2");
        }
    }
    @PostMapping("/update/cover")
    public ApiResponse<String> updateUserCover(@RequestParam("avatar") MultipartFile avatar,
                                               @RequestParam("userId") Long userId) {
        if (avatar.isEmpty()) {
            return ApiResponse.error(ResponseCode.UNPROCESSABLE_ENTITY, "上传头像为空", "上传头像为空");
        }
        try {
            // 读取文件内容并进行 Base64 编码
            byte[] fileContent = avatar.getBytes();
            String contentB64 = Base64.getEncoder().encodeToString(fileContent);

            String token = ConfigUtil.getImgSaveToken();
            String repo = ConfigUtil.getImgSaveRepo();
            String pathPrefix = ConfigUtil.getImgSaveFolder();
            String commitMessage = "上传图片到图床";

            String imageUrl = ImageUploadHandler.uploadBase64ToGitHub(token, repo, pathPrefix, contentB64, commitMessage);
            if(imageUrl!=null) {
                boolean flag = userService.updateUserCover(userId,imageUrl);
                return ApiResponse.success(ResponseCode.SUCCEED, "成功", imageUrl);
            }
            else
                return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED,"失败","失败");
        } catch (IOException e) {
            System.out.println("捕捉错误");
            return  ApiResponse.error(ResponseCode.NOT_IMPLEMENTED,"失败","失败");
        }
    }

    @PostMapping("/login/face")
    public ApiResponse<HashMap<String, Object>> loginByFace(@RequestParam("account") String account,
                                                            @RequestParam("faceInfo") MultipartFile multipartFile) throws IOException {
        // 获取人脸库中用户人脸信息
        User user = userServiceImpl.getUserByAccount(account);
        if(user == null) {
            return  ApiResponse.error(ResponseCode.BAD_REQUEST, "帐号不存在");
        }

        String targetFacePath = FaceUtil.getTargetFacePath(user.getUserId());
        if(StrUtil.isBlank(targetFacePath)) {
            // 用户未存储人脸信息
            return ApiResponse.error(ResponseCode.NOT_FOUND, "用户未存储人脸信息");
        }

        String tempPathPrefix = "uploaded-face-" + user.getUserId() + System.currentTimeMillis();
        String tempFilePath = Files.createFile(Path.of(tempPathPrefix+FaceUtil.getSaveSuffix()))
                .toAbsolutePath().toString();
        log.info("临时文件绝对路径： {}", tempFilePath);
        Path path = Path.of(tempFilePath);
        if(Files.exists(path)) {
            FileUtil.writeBytes(multipartFile.getBytes(), tempFilePath);
        }
        log.info("临时文件目录: {} 人脸库中人脸目录: {}", tempFilePath, targetFacePath);
        Double faceSim = FaceUtil.getFaceSim(tempFilePath, targetFacePath);
        log.info("读取到匹配值： {}", faceSim);
        if(faceSim < 0) {
            // 人脸错误
            return ApiResponse.error(ResponseCode.SERVICE_UNAVAILABLE, "人脸不清晰");
        }
        if (faceSim < FaceUtil.getFaceSimThreshold()) {
            return ApiResponse.error(ResponseCode.UNPROCESSABLE_ENTITY,
                    "人脸不匹配");
        }
        String token = userService.loginByFace(account);
        // 删除临时文件

        FileUtil.del(path);
        if(StrUtil.isBlank(token)) {
            return ApiResponse.error(ResponseCode.BAD_REQUEST);
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return  ApiResponse.success(data);
    }

    @GetMapping("/info/ids/{ids}")
    public List<User> getUserByIds(@PathVariable("ids") String ids) {
        log.info("ids: {}", ids);
        List<Long> userIds = parseStringToList(ids);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(User::getUserId, userIds);
        return userService.list(lambdaQueryWrapper);
    }

    private static List<Long> parseStringToList(String string) {
        // 去除方括号和空格
        String cleanString = string.replaceAll("[\\[\\] ]", "");

        // 将逗号分隔的数字转换为 Long 类型的列表
        List<Long> userIds = Arrays.stream(cleanString.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        return userIds;
    }


    /**
     * @description 将用户照片上传到人脸库, 设定图片名称为userId
     * @param userId 用户id
     * @param multipartFile 图片信息
     * @return 返回是否人脸库成功
     */
    @PostMapping("/face")
    public ApiResponse<Boolean> setFace(@RequestParam("userId") Long userId,
                                        @RequestParam("faceImage") MultipartFile multipartFile) throws IOException {
        // 将人脸上传至人脸库
        String uuid = UniqueIdGenerator.generateUniqueId(new Date().toString(), new Date().toString());
        String tempPathPrefix = "library-face-" + userId.toString() + uuid;
        String tempFilePath = Files.createFile(Path.of(tempPathPrefix+FaceUtil.getSaveSuffix()))
                .toAbsolutePath().toString();
        String tempSaveDirPath = Files.createTempDirectory("library-save").toAbsolutePath().toString();
        log.info("临时文件绝对路径： {}", tempFilePath);
        Path tempPath = Path.of(tempFilePath);
        if(Files.exists(tempPath)) {
            FileUtil.writeBytes(multipartFile.getBytes(), tempFilePath);
        }
        Path tempSaveDir = Path.of(tempSaveDirPath);
        if(!Files.exists(tempSaveDir) || !Files.isDirectory(tempSaveDir)) {
            Files.createDirectory(tempSaveDir);
        }
        // 进行人脸截取识别
        Boolean extracted = FaceUtil.faceExtract(tempFilePath, tempSaveDirPath);
        String tempSaveFilePath = tempSaveDirPath + "/" + tempPathPrefix + FaceUtil.getSaveSuffix();
        log.info("人脸提取结果: {}", extracted);
        log.info("人脸提取路径: {}", tempSaveFilePath);
        byte[] extractFaceBytes;
        Path tempSavePath = Path.of(tempSaveFilePath);
        if(extracted && Files.exists(tempSavePath)) {
            log.info("人脸提取成功，读取byte");
            extractFaceBytes = FileUtil.readBytes(tempSaveFilePath);
        } else {
            return ApiResponse.error(ResponseCode.UNPROCESSABLE_ENTITY, "未检测到人脸, 请重新录入", false);
        }
        JellyfinUtil jellyfinUtil = JellyfinFactory.build(JellyfinFactory.configPath);
        List<String> allMediaLibrary = jellyfinUtil.getAllMediaLibrary("");
        // 创建媒体库
        log.info("查询到所有媒体库: {}", allMediaLibrary);
        if(!allMediaLibrary.contains("face")) {
            jellyfinUtil.createMediaLibrary("/image/face", "face", "");
        }
        // 保存媒体文件
        String fileName = userId + ".jpg";
        Boolean saved = jellyfinUtil.saveFile(extractFaceBytes, fileName, "face", true);
        FileUtil.del(tempPath);
        FileUtil.del(tempSavePath);
        return ApiResponse.success(saved);
    }
}
