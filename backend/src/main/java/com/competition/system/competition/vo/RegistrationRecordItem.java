package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegistrationRecordItem {

    private Long id;
    private Long competitionId;
    private String competitionTitle;
    private String registrationType;
    private Long applicantUserId;
    private String applicantName;
    private Long teamId;
    private String teamName;
    private String teamStatus;
    private String teacherName;
    private String memberSummary;
    private String auditStatus;
    private String latestAuditOpinion;
    private LocalDateTime submitTime;
    private LocalDateTime finalSubmitTime;
    private String remark;
}
