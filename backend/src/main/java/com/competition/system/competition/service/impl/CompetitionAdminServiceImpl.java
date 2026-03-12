package com.competition.system.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.system.common.exception.BusinessException;
import com.competition.system.common.model.OptionItem;
import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.CompetitionCreateRequest;
import com.competition.system.competition.dto.CompetitionQueryRequest;
import com.competition.system.competition.dto.CompetitionUpdateRequest;
import com.competition.system.competition.entity.CompCategory;
import com.competition.system.competition.entity.Competition;
import com.competition.system.competition.mapper.CompCategoryMapper;
import com.competition.system.competition.mapper.CompetitionMapper;
import com.competition.system.competition.service.CompetitionAdminService;
import com.competition.system.competition.vo.CompetitionListItem;
import com.competition.system.competition.vo.CompetitionOptionsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CompetitionAdminServiceImpl implements CompetitionAdminService {

    private final CompetitionMapper competitionMapper;
    private final CompCategoryMapper compCategoryMapper;

    public CompetitionAdminServiceImpl(CompetitionMapper competitionMapper,
                                       CompCategoryMapper compCategoryMapper) {
        this.competitionMapper = competitionMapper;
        this.compCategoryMapper = compCategoryMapper;
    }

    @Override
    public PageResult<CompetitionListItem> pageCompetitions(CompetitionQueryRequest request) {
        Page<Competition> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<Competition>()
                .like(StringUtils.hasText(request.getTitle()), Competition::getTitle, request.getTitle())
                .like(StringUtils.hasText(request.getLevelName()), Competition::getLevelName, request.getLevelName())
                .eq(StringUtils.hasText(request.getStatus()), Competition::getStatus, request.getStatus())
                .eq(request.getCategoryId() != null, Competition::getCategoryId, request.getCategoryId())
                .orderByDesc(Competition::getCreateTime);
        Page<Competition> result = competitionMapper.selectPage(page, wrapper);
        List<CompetitionListItem> records = result.getRecords().stream().map(this::toListItem).toList();
        return PageResult.<CompetitionListItem>builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(records)
                .build();
    }

    @Override
    public CompetitionListItem getDetail(Long id) {
        return toListItem(requireCompetition(id));
    }

    @Override
    @Transactional
    public Long createCompetition(CompetitionCreateRequest request) {
        CompCategory category = requireCategory(request.getCategoryId());
        Competition competition = new Competition();
        fillCompetition(competition, request, category);
        competitionMapper.insert(competition);
        return competition.getId();
    }

    @Override
    @Transactional
    public void updateCompetition(Long id, CompetitionUpdateRequest request) {
        CompCategory category = requireCategory(request.getCategoryId());
        Competition competition = requireCompetition(id);
        fillCompetition(competition, request, category);
        competitionMapper.updateById(competition);
    }

    @Override
    @Transactional
    public void deleteCompetition(Long id) {
        requireCompetition(id);
        competitionMapper.deleteById(id);
    }

    @Override
    public CompetitionOptionsResponse getOptions() {
        List<OptionItem<Long>> categories = compCategoryMapper.selectList(new LambdaQueryWrapper<CompCategory>()
                        .eq(CompCategory::getStatus, 1)
                        .orderByAsc(CompCategory::getSort))
                .stream()
                .map(item -> new OptionItem<>(item.getId(), item.getCategoryName()))
                .toList();

        return CompetitionOptionsResponse.builder()
                .categories(categories)
                .statuses(List.of(
                        new OptionItem<>("DRAFT", "草稿"),
                        new OptionItem<>("PUBLISHED", "已发布"),
                        new OptionItem<>("CLOSED", "已关闭")
                ))
                .teamModes(List.of(
                        new OptionItem<>("INDIVIDUAL", "个人赛"),
                        new OptionItem<>("TEAM", "团队赛")
                ))
                .build();
    }

    private Competition requireCompetition(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        return competition;
    }

    private CompCategory requireCategory(Long id) {
        CompCategory category = compCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("竞赛分类不存在");
        }
        return category;
    }

    private CompetitionListItem toListItem(Competition competition) {
        CompCategory category = competition.getCategoryId() == null ? null : compCategoryMapper.selectById(competition.getCategoryId());
        return CompetitionListItem.builder()
                .id(competition.getId())
                .categoryId(competition.getCategoryId())
                .categoryName(category == null ? null : category.getCategoryName())
                .title(competition.getTitle())
                .levelName(competition.getLevelName())
                .organizer(competition.getOrganizer())
                .registrationStart(competition.getRegistrationStart())
                .registrationEnd(competition.getRegistrationEnd())
                .competitionStart(competition.getCompetitionStart())
                .competitionEnd(competition.getCompetitionEnd())
                .teamMode(competition.getTeamMode())
                .maxTeamSize(competition.getMaxTeamSize())
                .description(competition.getDescription())
                .status(competition.getStatus())
                .createTime(competition.getCreateTime())
                .build();
    }

    private void fillCompetition(Competition competition, CompetitionCreateRequest request, CompCategory category) {
        competition.setCategoryId(category.getId());
        competition.setTitle(request.getTitle());
        competition.setLevelName(request.getLevelName());
        competition.setOrganizer(request.getOrganizer());
        competition.setRegistrationStart(request.getRegistrationStart());
        competition.setRegistrationEnd(request.getRegistrationEnd());
        competition.setCompetitionStart(request.getCompetitionStart());
        competition.setCompetitionEnd(request.getCompetitionEnd());
        competition.setTeamMode(request.getTeamMode());
        competition.setMaxTeamSize(request.getMaxTeamSize() == null ? 1 : request.getMaxTeamSize());
        competition.setDescription(request.getDescription());
        competition.setStatus(request.getStatus());
    }

    private void fillCompetition(Competition competition, CompetitionUpdateRequest request, CompCategory category) {
        competition.setCategoryId(category.getId());
        competition.setTitle(request.getTitle());
        competition.setLevelName(request.getLevelName());
        competition.setOrganizer(request.getOrganizer());
        competition.setRegistrationStart(request.getRegistrationStart());
        competition.setRegistrationEnd(request.getRegistrationEnd());
        competition.setCompetitionStart(request.getCompetitionStart());
        competition.setCompetitionEnd(request.getCompetitionEnd());
        competition.setTeamMode(request.getTeamMode());
        competition.setMaxTeamSize(request.getMaxTeamSize() == null ? 1 : request.getMaxTeamSize());
        competition.setDescription(request.getDescription());
        competition.setStatus(request.getStatus());
    }
}
