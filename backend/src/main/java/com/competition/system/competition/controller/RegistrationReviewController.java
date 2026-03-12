package com.competition.system.competition.controller;

import com.competition.system.common.model.ApiResponse;
import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.RegistrationReviewQueryRequest;
import com.competition.system.competition.dto.RegistrationReviewRequest;
import com.competition.system.competition.service.CompetitionWorkflowService;
import com.competition.system.competition.vo.RegistrationRecordItem;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews/registrations")
@PreAuthorize("hasAnyRole('ADMIN','COLLEGE_AUDITOR')")
public class RegistrationReviewController {

    private final CompetitionWorkflowService competitionWorkflowService;

    public RegistrationReviewController(CompetitionWorkflowService competitionWorkflowService) {
        this.competitionWorkflowService = competitionWorkflowService;
    }

    @GetMapping
    public ApiResponse<PageResult<RegistrationRecordItem>> page(RegistrationReviewQueryRequest request) {
        return ApiResponse.success(competitionWorkflowService.pageReviewRegistrations(request));
    }

    @PostMapping("/{registrationId}/approve")
    public ApiResponse<Void> approve(@PathVariable Long registrationId,
                                     @Valid @RequestBody RegistrationReviewRequest request) {
        competitionWorkflowService.approveRegistration(registrationId, request);
        return ApiResponse.success();
    }

    @PostMapping("/{registrationId}/reject")
    public ApiResponse<Void> reject(@PathVariable Long registrationId,
                                    @Valid @RequestBody RegistrationReviewRequest request) {
        competitionWorkflowService.rejectRegistration(registrationId, request);
        return ApiResponse.success();
    }
}
