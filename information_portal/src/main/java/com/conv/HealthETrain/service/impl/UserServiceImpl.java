package com.conv.HealthETrain.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.config.JwtProperties;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.domain.UserLinkCategory;
import com.conv.HealthETrain.domain.dto.UserDetailDTO;
import com.conv.HealthETrain.enums.ExceptionCode;
import com.conv.HealthETrain.exception.GlobalException;
import com.conv.HealthETrain.mapper.UserLinkCategoryMapper;
import com.conv.HealthETrain.service.UserLinkCategoryService;
import com.conv.HealthETrain.service.UserService;
import com.conv.HealthETrain.mapper.UserMapper;
import com.conv.HealthETrain.utils.CodeUtil;
import com.conv.HealthETrain.utils.MailUtil;
import com.conv.HealthETrain.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author john
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-07-05 17:56:52
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private final CodeUtil codeUtil;
    private final MailUtil mailUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;
    private final JwtProperties jwtProperties;
    private final int codeLen = 6;

    private final UserLinkCategoryMapper userLinkCategoryMapper;
    private final UserLinkCategoryService userLinkCategoryService;

    @Override
    public String loginByAccount(User loginUser) {
        User user = lambdaQuery().eq(User::getAccount, loginUser.getAccount()).one();
        if (user == null) {
            throw new GlobalException("用户不存在", ExceptionCode.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new GlobalException("密码错误", ExceptionCode.BAD_REQUEST);
        }

        return tokenUtil.createToken(Long.valueOf(user.getUserId()), jwtProperties.getTokenTTL());
    }

    // TODO 用户不存在时的邮箱和手机登录逻辑

    @Override
    public String loginByPhone(User loginUser) {
//        User user = lambdaQuery().eq(User::getPhone, loginUser.getPhone()).one();
//
//        if (user == null) {
//            throw new GlobalException("用户不存在", ExceptionCode.BAD_REQUEST);
//        }
//
//        if (!passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
//            throw new GlobalException("密码错误", ExceptionCode.BAD_REQUEST);
//        }
//
//        return tokenUtil.createToken(Long.valueOf(user.getUserId()), jwtProperties.getTokenTTL());
        return "";
    }

    @Override
    public void sendEmailCode(User loginUser) {
        String code = RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER, codeLen);

        String to = loginUser.getEmail();
        String subject = "【HealthETrain】 登录邮箱验证码";
        String content = "您的验证码是：" + code + "。5分钟内有效，请勿泄露给他人。";

        // Send the email
        mailUtil.sendEmail(to, subject, content);
        // Store the code in the memory
        codeUtil.storeCode(to, code);
    }

    @Override
    public boolean verifyEmail(User loginUser, String code) {
        String email = loginUser.getEmail();
        return codeUtil.verifyCode(email, code);
    }


    @Override
    public boolean register(User registerUser) {
        User user = lambdaQuery().eq(User::getUsername, registerUser.getUsername()).one();
        if (user != null) {
            // The user already exists
            throw new GlobalException("用户已存在", ExceptionCode.BAD_REQUEST);
        }

        // Encode the password before storing it in the database
        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        int result = userMapper.insert(registerUser);

        return result > 0;
    }

    // 查询用户基本情况以及教师类别和权限类别
    @Override
    public List<UserDetailDTO> getAllUsersWithDetails() {
        List<UserDetailDTO> userDetailDTOList = new ArrayList<>();
        List<User> userList = userMapper.selectList(null);
        for (User user : userList) {
            UserDetailDTO userDetailDTO = new UserDetailDTO();
            BeanUtils.copyProperties(user, userDetailDTO);

            // 默认设置为非教师和无权限出卷人
            userDetailDTO.setIsTeacher("0");
            userDetailDTO.setAuthority("0");

            // 根据 userId 在 UserLinkCategory 表中查找对应的信息
            List<UserLinkCategory> linkList = userLinkCategoryMapper.selectLinkCategoriesByUserId(user.getUserId());
            for (UserLinkCategory link : linkList) {
                if (link.getCategoryId() == 9) {
                    userDetailDTO.setIsTeacher("1");
                } else if (link.getCategoryId() == 8) {
                    userDetailDTO.setAuthority("1");
                }
                else {
                    userDetailDTO.setCategoryId(link.getCategoryId());
                }
            }
            userDetailDTOList.add(userDetailDTO);
        }
        return userDetailDTOList;
    }

    // 查询所有学生列表
    @Override
    public List<User> findStudentUserList() {
        return userMapper.findStudentUserList();
    }

}




