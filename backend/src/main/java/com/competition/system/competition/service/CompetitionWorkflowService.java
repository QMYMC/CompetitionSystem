package com.competition.system.competition.service;

import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.IndividualRegistrationSubmitRequest;
import com.competition.system.competition.dto.RegistrationReviewQueryRequest;
import com.competition.system.competition.dto.RegistrationReviewRequest;
import com.competition.system.competition.dto.StudentCompetitionQueryRequest;
import com.competition.system.competition.dto.TeamCreateRequest;
import com.competition.system.competition.dto.TeamMemberAddRequest;
import com.competition.system.competition.dto.TeamSubmitRequest;
import com.competition.system.competition.dto.TeamUpdateRequest;
import com.competition.system.competition.vo.RegistrationRecordItem;
import com.competition.system.competition.vo.StudentCompetitionItem;
import com.competition.system.competition.vo.StudentTeamItem;
import com.competition.system.competition.vo.TeamFormOptionsResponse;

import java.util.List;

public interface CompetitionWorkflowService {

    List<StudentCompetitionItem> listStudentCompetitions(StudentCompetitionQueryRequest request);

    List<RegistrationRecordItem> listMyRegistrations();

    List<StudentTeamItem> listMyTeams();

    StudentTeamItem getMyTeamDetail(Long teamId);

    TeamFormOptionsResponse getTeamFormOptions();

    Long submitIndividualRegistration(IndividualRegistrationSubmitRequest request);

    Long createTeam(TeamCreateRequest request);

    void updateTeam(Long teamId, TeamUpdateRequest request);

    void addTeamMember(Long teamId, TeamMemberAddRequest request);

    void removeTeamMember(Long teamId, Long userId);

    void submitTeamRegistration(Long teamId, TeamSubmitRequest request);

    PageResult<RegistrationRecordItem> pageReviewRegistrations(RegistrationReviewQueryRequest request);

    void approveRegistration(Long registrationId, RegistrationReviewRequest request);

    void rejectRegistration(Long registrationId, RegistrationReviewRequest request);
}
