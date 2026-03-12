package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StudentTeamItem {

    private Long id;
    private Long competitionId;
    private String competitionTitle;
    private String teamName;
    private String teamStatus;
    private Integer maxTeamSize;
    private Long teacherId;
    private String teacherName;
    private String teacherTitle;
    private String remark;
    private String latestAuditOpinion;
    private LocalDateTime submitTime;
    private List<TeamMemberView> members;
}
