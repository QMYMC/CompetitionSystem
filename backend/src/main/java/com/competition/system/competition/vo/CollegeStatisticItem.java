package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollegeStatisticItem {

    private String collegeName;
    private long registrationCount;
    private long approvedRegistrationCount;
    private long awardCount;
    private long approvedAwardCount;
}
