package com.competition.system.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TeamCreateRequest {

    @NotNull(message = "竞赛不能为空")
    private Long competitionId;

    @NotBlank(message = "团队名称不能为空")
    private String teamName;

    private Long teacherId;
    private List<Long> memberUserIds;
    private String remark;
}
