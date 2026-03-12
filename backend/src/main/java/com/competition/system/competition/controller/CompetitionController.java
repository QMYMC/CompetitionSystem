package com.competition.system.competition.controller;

import com.competition.system.common.model.ApiResponse;
import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.CompetitionCreateRequest;
import com.competition.system.competition.dto.CompetitionQueryRequest;
import com.competition.system.competition.dto.CompetitionUpdateRequest;
import com.competition.system.competition.service.CompetitionAdminService;
import com.competition.system.competition.vo.CompetitionListItem;
import com.competition.system.competition.vo.CompetitionOptionsResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/competitions")
@PreAuthorize("hasRole('ADMIN')")
public class CompetitionController {

    private final CompetitionAdminService competitionAdminService;

    public CompetitionController(CompetitionAdminService competitionAdminService) {
        this.competitionAdminService = competitionAdminService;
    }

    @GetMapping
    public ApiResponse<PageResult<CompetitionListItem>> page(CompetitionQueryRequest request) {
        return ApiResponse.success(competitionAdminService.pageCompetitions(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<CompetitionListItem> detail(@PathVariable Long id) {
        return ApiResponse.success(competitionAdminService.getDetail(id));
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody CompetitionCreateRequest request) {
        return ApiResponse.success(competitionAdminService.createCompetition(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CompetitionUpdateRequest request) {
        competitionAdminService.updateCompetition(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        competitionAdminService.deleteCompetition(id);
        return ApiResponse.success();
    }

    @GetMapping("/options")
    public ApiResponse<CompetitionOptionsResponse> options() {
        return ApiResponse.success(competitionAdminService.getOptions());
    }
}
