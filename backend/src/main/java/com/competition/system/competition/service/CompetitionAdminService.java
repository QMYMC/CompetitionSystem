package com.competition.system.competition.service;

import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.CompetitionCreateRequest;
import com.competition.system.competition.dto.CompetitionQueryRequest;
import com.competition.system.competition.dto.CompetitionUpdateRequest;
import com.competition.system.competition.vo.CompetitionListItem;
import com.competition.system.competition.vo.CompetitionOptionsResponse;

public interface CompetitionAdminService {

    PageResult<CompetitionListItem> pageCompetitions(CompetitionQueryRequest request);

    CompetitionListItem getDetail(Long id);

    Long createCompetition(CompetitionCreateRequest request);

    void updateCompetition(Long id, CompetitionUpdateRequest request);

    void deleteCompetition(Long id);

    CompetitionOptionsResponse getOptions();
}
