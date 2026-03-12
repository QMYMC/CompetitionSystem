package com.competition.system.competition.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AwardReviewRequest {

    @NotBlank(message = "审核意见不能为空")
    private String opinion;
}
