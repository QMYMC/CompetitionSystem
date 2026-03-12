package com.competition.system.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoticeUpdateRequest {

    @NotBlank(message = "公告标题不能为空")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    @NotBlank(message = "发布状态不能为空")
    private String publishStatus;

    @NotNull(message = "置顶状态不能为空")
    private Integer topFlag;
}
