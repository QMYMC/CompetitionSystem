package com.competition.system.competition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentAwardCreateRequest {

    @NotNull(message = "报名记录不能为空")
    private Long registrationId;

    @NotBlank(message = "获奖名称不能为空")
    private String awardName;

    @NotBlank(message = "获奖级别不能为空")
    private String awardLevel;

    @NotBlank(message = "获奖等次不能为空")
    private String awardRank;

    @NotNull(message = "获奖时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime awardTime;

    private String remark;
}
