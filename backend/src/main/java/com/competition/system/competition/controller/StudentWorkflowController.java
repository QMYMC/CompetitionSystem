package com.competition.system.competition.controller;

import com.competition.system.common.model.ApiResponse;
import com.competition.system.competition.dto.IndividualRegistrationSubmitRequest;
import com.competition.system.competition.dto.StudentCompetitionQueryRequest;
import com.competition.system.competition.dto.TeamCreateRequest;
import com.competition.system.competition.dto.TeamMemberAddRequest;
import com.competition.system.competition.dto.TeamSubmitRequest;
import com.competition.system.competition.dto.TeamUpdateRequest;
import com.competition.system.competition.service.CompetitionWorkflowService;
import com.competition.system.competition.vo.RegistrationRecordItem;
import com.competition.system.competition.vo.StudentCompetitionItem;
import com.competition.system.competition.vo.StudentTeamItem;
import com.competition.system.competition.vo.TeamFormOptionsResponse;
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

import java.util.List;

@RestController
@RequestMapping("/student/workflow")
@PreAuthorize("hasRole('STUDENT')")
public class StudentWorkflowController {

    private final CompetitionWorkflowService competitionWorkflowService;

    public StudentWorkflowController(CompetitionWorkflowService competitionWorkflowService) {
        this.competitionWorkflowService = competitionWorkflowService;
    }

    @GetMapping("/competitions")
    public ApiResponse<List<StudentCompetitionItem>> competitions(StudentCompetitionQueryRequest request) {
        return ApiResponse.success(competitionWorkflowService.listStudentCompetitions(request));
    }

    @GetMapping("/registrations")
    public ApiResponse<List<RegistrationRecordItem>> registrations() {
        return ApiResponse.success(competitionWorkflowService.listMyRegistrations());
    }

    @GetMapping("/teams")
    public ApiResponse<List<StudentTeamItem>> teams() {
        return ApiResponse.success(competitionWorkflowService.listMyTeams());
    }

    @GetMapping("/teams/{teamId}")
    public ApiResponse<StudentTeamItem> teamDetail(@PathVariable Long teamId) {
        return ApiResponse.success(competitionWorkflowService.getMyTeamDetail(teamId));
    }

    @GetMapping("/team-options")
    public ApiResponse<TeamFormOptionsResponse> teamOptions() {
        return ApiResponse.success(competitionWorkflowService.getTeamFormOptions());
    }

    @PostMapping("/registrations/individual")
    public ApiResponse<Long> submitIndividual(@Valid @RequestBody IndividualRegistrationSubmitRequest request) {
        return ApiResponse.success(competitionWorkflowService.submitIndividualRegistration(request));
    }

    @PostMapping("/teams")
    public ApiResponse<Long> createTeam(@Valid @RequestBody TeamCreateRequest request) {
        return ApiResponse.success(competitionWorkflowService.createTeam(request));
    }

    @PutMapping("/teams/{teamId}")
    public ApiResponse<Void> updateTeam(@PathVariable Long teamId, @Valid @RequestBody TeamUpdateRequest request) {
        competitionWorkflowService.updateTeam(teamId, request);
        return ApiResponse.success();
    }

    @PostMapping("/teams/{teamId}/members")
    public ApiResponse<Void> addMember(@PathVariable Long teamId, @Valid @RequestBody TeamMemberAddRequest request) {
        competitionWorkflowService.addTeamMember(teamId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/teams/{teamId}/members/{userId}")
    public ApiResponse<Void> removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        competitionWorkflowService.removeTeamMember(teamId, userId);
        return ApiResponse.success();
    }

    @PostMapping("/teams/{teamId}/submit")
    public ApiResponse<Void> submitTeam(@PathVariable Long teamId, @RequestBody(required = false) TeamSubmitRequest request) {
        competitionWorkflowService.submitTeamRegistration(teamId, request == null ? new TeamSubmitRequest() : request);
        return ApiResponse.success();
    }
}
