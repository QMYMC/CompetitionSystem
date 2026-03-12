package com.competition.system.competition.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkflowOptionView {

    private Long value;
    private String label;
    private String extra;
}
