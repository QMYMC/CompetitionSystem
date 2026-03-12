package com.competition.system.competition.dto;

import lombok.Data;

@Data
public class CompetitionQueryRequest {

    private long current = 1;
    private long size = 10;
    private String title;
    private String levelName;
    private String status;
    private Long categoryId;
}
