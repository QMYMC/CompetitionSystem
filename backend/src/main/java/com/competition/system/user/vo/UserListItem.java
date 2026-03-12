package com.competition.system.user.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserListItem {

    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String gender;
    private Integer status;
    private Long collegeId;
    private String collegeName;
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String remark;
    private LocalDateTime createTime;
}
