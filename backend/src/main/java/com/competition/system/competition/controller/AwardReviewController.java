package com.competition.system.competition.controller;

import com.competition.system.common.model.ApiResponse;
import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.AwardReviewQueryRequest;
import com.competition.system.competition.dto.AwardReviewRequest;
import com.competition.system.competition.service.AwardService;
import com.competition.system.competition.vo.AwardReviewItem;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/awards/reviews")
@PreAuthorize("hasAnyRole('ADMIN','COLLEGE_AUDITOR')")
public class AwardReviewController {

    private final AwardService awardService;

    public AwardReviewController(AwardService awardService) {
        this.awardService = awardService;
    }

    @GetMapping
    public ApiResponse<PageResult<AwardReviewItem>> page(AwardReviewQueryRequest request) {
        return ApiResponse.success(awardService.pageAwardsForReview(request));
    }

    @PostMapping("/{awardId}/approve")
    public ApiResponse<Void> approve(@PathVariable Long awardId, @Valid @RequestBody AwardReviewRequest request) {
        awardService.approveAward(awardId, request);
        return ApiResponse.success();
    }

    @PostMapping("/{awardId}/reject")
    public ApiResponse<Void> reject(@PathVariable Long awardId, @Valid @RequestBody AwardReviewRequest request) {
        awardService.rejectAward(awardId, request);
        return ApiResponse.success();
    }
}
