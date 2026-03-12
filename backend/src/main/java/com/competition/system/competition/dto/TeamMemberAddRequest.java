package com.competition.system.competition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamMemberAddRequest {

    @NotNull(message = "成员不能为空")
    private Long userId;
}
