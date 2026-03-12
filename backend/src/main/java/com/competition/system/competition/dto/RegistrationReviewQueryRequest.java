package com.competition.system.competition.dto;

import lombok.Data;

@Data
public class RegistrationReviewQueryRequest {

    private long current = 1;
    private long size = 10;
    private String auditStatus;
    private String registrationType;
    private Long competitionId;
}
