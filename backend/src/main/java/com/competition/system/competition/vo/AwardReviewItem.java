package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AwardReviewItem {

    private Long id;
    private Long registrationId;
    private Long competitionId;
    private String competitionTitle;
    private String applicantName;
    private String registrationType;
    private String teamName;
    private String awardName;
    private String awardLevel;
    private String awardRank;
    private LocalDateTime awardTime;
    private String auditStatus;
    private String latestAuditOpinion;
    private String remark;
    private LocalDateTime createTime;
}
