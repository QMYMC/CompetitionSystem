package com.competition.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateRequest {

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
    private String password;
}
