package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamMemberView {

    private Long userId;
    private String username;
    private String realName;
    private String collegeName;
    private String memberRole;
}
