package com.competition.system.competition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IndividualRegistrationSubmitRequest {

    @NotNull(message = "竞赛不能为空")
    private Long competitionId;

    private String remark;
}
