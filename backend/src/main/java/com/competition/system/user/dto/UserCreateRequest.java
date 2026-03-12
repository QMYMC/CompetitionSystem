package com.competition.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    private String email;
    private String phone;
    private String gender;

    @NotNull(message = "角色不能为空")
    private Long roleId;

    private Long collegeId;
    private Integer status;
    private String remark;
}
