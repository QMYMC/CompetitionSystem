package com.competition.system.competition.dto;

import lombok.Data;

@Data
public class AwardReviewQueryRequest {

    private long current = 1;
    private long size = 10;
    private String auditStatus;
    private Long competitionId;
    private String keyword;
}
