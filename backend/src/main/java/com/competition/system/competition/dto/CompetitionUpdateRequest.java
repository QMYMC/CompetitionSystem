package com.competition.system.competition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompetitionUpdateRequest {

    @NotNull(message = "竞赛分类不能为空")
    private Long categoryId;

    @NotBlank(message = "竞赛名称不能为空")
    private String title;

    @NotBlank(message = "竞赛级别不能为空")
    private String levelName;

    @NotBlank(message = "主办单位不能为空")
    private String organizer;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationEnd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime competitionStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime competitionEnd;

    @NotBlank(message = "参赛方式不能为空")
    private String teamMode;

    private Integer maxTeamSize;
    private String description;

    @NotBlank(message = "状态不能为空")
    private String status;
}
