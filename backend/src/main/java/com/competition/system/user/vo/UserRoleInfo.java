package com.competition.system.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleInfo {

    private Long roleId;
    private String roleCode;
    private String roleName;
}
