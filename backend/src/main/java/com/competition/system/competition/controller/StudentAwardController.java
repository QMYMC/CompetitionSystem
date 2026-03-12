package com.competition.system.competition.controller;

import com.competition.system.common.model.ApiResponse;
import com.competition.system.competition.dto.StudentAwardCreateRequest;
import com.competition.system.competition.dto.StudentAwardUpdateRequest;
import com.competition.system.competition.service.AwardService;
import com.competition.system.competition.vo.AwardOptionItem;
import com.competition.system.competition.vo.StudentAwardItem;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student/awards")
@PreAuthorize("hasRole('STUDENT')")
public class StudentAwardController {

    private final AwardService awardService;

    public StudentAwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    @GetMapping("/options")
    public ApiResponse<List<AwardOptionItem>> options() {
        return ApiResponse.success(awardService.getStudentAwardOptions());
    }

    @GetMapping
    public ApiResponse<List<StudentAwardItem>> list() {
        return ApiResponse.success(awardService.listMyAwards());
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody StudentAwardCreateRequest request) {
        return ApiResponse.success(awardService.createAward(request));
    }

    @PutMapping("/{awardId}")
    public ApiResponse<Void> update(@PathVariable Long awardId, @Valid @RequestBody StudentAwardUpdateRequest request) {
        awardService.updateAward(awardId, request);
        return ApiResponse.success();
    }
}
