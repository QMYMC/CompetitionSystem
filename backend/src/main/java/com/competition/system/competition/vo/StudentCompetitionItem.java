package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudentCompetitionItem {

    private Long id;
    private String title;
    private String categoryName;
    private String levelName;
    private String organizer;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private String teamMode;
    private Integer maxTeamSize;
    private String description;
    private String myAuditStatus;
    private Long myTeamId;
    private String myTeamStatus;
}
