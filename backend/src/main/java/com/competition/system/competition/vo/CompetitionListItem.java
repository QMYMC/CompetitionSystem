package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CompetitionListItem {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String levelName;
    private String organizer;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime competitionStart;
    private LocalDateTime competitionEnd;
    private String teamMode;
    private Integer maxTeamSize;
    private String description;
    private String status;
    private LocalDateTime createTime;
}
