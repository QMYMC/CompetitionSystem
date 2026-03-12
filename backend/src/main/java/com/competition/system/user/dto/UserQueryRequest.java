package com.competition.system.user.dto;

import lombok.Data;

@Data
public class UserQueryRequest {

    private long current = 1;
    private long size = 10;
    private String username;
    private String realName;
    private Integer status;
    private Long roleId;
    private Long collegeId;
}
