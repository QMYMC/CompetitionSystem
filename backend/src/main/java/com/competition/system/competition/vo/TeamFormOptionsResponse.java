package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamFormOptionsResponse {

    private List<WorkflowOptionView> competitions;
    private List<WorkflowOptionView> teachers;
    private List<WorkflowOptionView> students;
}
