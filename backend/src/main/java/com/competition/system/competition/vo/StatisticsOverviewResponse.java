package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatisticsOverviewResponse {

    private long competitionCount;
    private long registrationCount;
    private long awardCount;
    private long publishedNoticeCount;
    private List<CollegeStatisticItem> collegeStats;
    private List<NameValueStatisticItem> registrationTypeStats;
    private List<NameValueStatisticItem> awardStatusStats;
}
