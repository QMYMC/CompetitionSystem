package com.competition.system.competition.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamUpdateRequest {

    @NotBlank(message = "团队名称不能为空")
    private String teamName;

    private Long teacherId;
    private String remark;
}
