package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoticeDetailView {

    private Long id;
    private String title;
    private String content;
    private String publishStatus;
    private String publisherName;
    private LocalDateTime publishTime;
    private Integer topFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
