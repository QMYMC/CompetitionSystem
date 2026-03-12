package com.competition.system.competition.controller;

import com.competition.system.common.model.ApiResponse;
import com.competition.system.competition.service.StatisticsService;
import com.competition.system.competition.vo.StatisticsOverviewResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@PreAuthorize("hasAnyRole('ADMIN','COLLEGE_AUDITOR')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public ApiResponse<StatisticsOverviewResponse> overview() {
        return ApiResponse.success(statisticsService.getOverview());
    }
}
