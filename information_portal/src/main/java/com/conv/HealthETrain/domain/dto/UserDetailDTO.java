package com.conv.HealthETrain.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDetailDTO {
    // 负责用户的详细信息，比User多“是否为教师” “是否为出卷人"属性,但是此处不保存密码
    @JsonProperty("key")
    private Long userId;
    private String account;
    private String username;
    private String email;
    private String phone;
    private String cover;
    @JsonProperty("category_id")
    private int categoryId;
    @JsonProperty("is_teacher")
    private String isTeacher;// "0"代表不是，“1”代表是
    private String authority; // "0"代表不是出卷人，即无权限，“1”代表是出卷人
}
