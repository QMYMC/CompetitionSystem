package com.competition.system.competition.dto;

import lombok.Data;

@Data
public class NoticeQueryRequest {

    private long current = 1;
    private long size = 10;
    private String keyword;
    private String publishStatus;
}
