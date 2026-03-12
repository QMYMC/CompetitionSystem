package com.competition.system.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.competition.system.competition.constant.WorkflowConstants;
import com.competition.system.competition.entity.AwardInfo;
import com.competition.system.competition.entity.CompTeam;
import com.competition.system.competition.entity.Competition;
import com.competition.system.competition.entity.CompetitionRegistration;
import com.competition.system.competition.entity.NoticeInfo;
import com.competition.system.competition.mapper.AwardInfoMapper;
import com.competition.system.competition.mapper.CompTeamMapper;
import com.competition.system.competition.mapper.CompetitionMapper;
import com.competition.system.competition.mapper.CompetitionRegistrationMapper;
import com.competition.system.competition.mapper.NoticeInfoMapper;
import com.competition.system.competition.service.StatisticsService;
import com.competition.system.competition.vo.CollegeStatisticItem;
import com.competition.system.competition.vo.NameValueStatisticItem;
import com.competition.system.competition.vo.StatisticsOverviewResponse;
import com.competition.system.user.entity.SysCollege;
import com.competition.system.user.entity.SysUser;
import com.competition.system.user.mapper.SysCollegeMapper;
import com.competition.system.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final CompetitionMapper competitionMapper;
    private final CompetitionRegistrationMapper competitionRegistrationMapper;
    private final AwardInfoMapper awardInfoMapper;
    private final NoticeInfoMapper noticeInfoMapper;
    private final CompTeamMapper compTeamMapper;
    private final SysCollegeMapper sysCollegeMapper;
    private final SysUserMapper sysUserMapper;

    public StatisticsServiceImpl(CompetitionMapper competitionMapper,
                                 CompetitionRegistrationMapper competitionRegistrationMapper,
                                 AwardInfoMapper awardInfoMapper,
                                 NoticeInfoMapper noticeInfoMapper,
                                 CompTeamMapper compTeamMapper,
                                 SysCollegeMapper sysCollegeMapper,
                                 SysUserMapper sysUserMapper) {
        this.competitionMapper = competitionMapper;
        this.competitionRegistrationMapper = competitionRegistrationMapper;
        this.awardInfoMapper = awardInfoMapper;
        this.noticeInfoMapper = noticeInfoMapper;
        this.compTeamMapper = compTeamMapper;
        this.sysCollegeMapper = sysCollegeMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public StatisticsOverviewResponse getOverview() {
        List<CompetitionRegistration> registrations = competitionRegistrationMapper.selectList(new LambdaQueryWrapper<>());
        List<AwardInfo> awards = awardInfoMapper.selectList(new LambdaQueryWrapper<>());
        List<SysCollege> colleges = sysCollegeMapper.selectList(new LambdaQueryWrapper<SysCollege>().orderByAsc(SysCollege::getSort));

        Map<Long, CollegeAccumulator> collegeMap = new LinkedHashMap<>();
        for (SysCollege college : colleges) {
            collegeMap.put(college.getId(), new CollegeAccumulator(college.getCollegeName()));
        }

        Map<String, Long> registrationTypeMap = new LinkedHashMap<>();
        for (CompetitionRegistration registration : registrations) {
            registrationTypeMap.merge(registration.getRegistrationType(), 1L, Long::sum);
            Long collegeId = resolveRegistrationCollegeId(registration);
            if (collegeId != null && collegeMap.containsKey(collegeId)) {
                collegeMap.get(collegeId).registrationCount++;
                if (WorkflowConstants.REGISTRATION_STATUS_APPROVED.equals(registration.getAuditStatus())) {
                    collegeMap.get(collegeId).approvedRegistrationCount++;
                }
            }
        }

        Map<String, Long> awardStatusMap = new LinkedHashMap<>();
        for (AwardInfo award : awards) {
            awardStatusMap.merge(award.getAuditStatus(), 1L, Long::sum);
            CompetitionRegistration registration = competitionRegistrationMapper.selectById(award.getRegistrationId());
            Long collegeId = registration == null ? null : resolveRegistrationCollegeId(registration);
            if (collegeId != null && collegeMap.containsKey(collegeId)) {
                collegeMap.get(collegeId).awardCount++;
                if (WorkflowConstants.AWARD_STATUS_APPROVED.equals(award.getAuditStatus())) {
                    collegeMap.get(collegeId).approvedAwardCount++;
                }
            }
        }

        return StatisticsOverviewResponse.builder()
                .competitionCount(competitionMapper.selectCount(new LambdaQueryWrapper<Competition>()))
                .registrationCount(registrations.size())
                .awardCount(awards.size())
                .publishedNoticeCount(noticeInfoMapper.selectCount(new LambdaQueryWrapper<NoticeInfo>()
                        .eq(NoticeInfo::getPublishStatus, WorkflowConstants.NOTICE_STATUS_PUBLISHED)))
                .collegeStats(collegeMap.values().stream().map(item -> CollegeStatisticItem.builder()
                        .collegeName(item.collegeName)
                        .registrationCount(item.registrationCount)
                        .approvedRegistrationCount(item.approvedRegistrationCount)
                        .awardCount(item.awardCount)
                        .approvedAwardCount(item.approvedAwardCount)
                        .build()).toList())
                .registrationTypeStats(toStats(registrationTypeMap))
                .awardStatusStats(toStats(awardStatusMap))
                .build();
    }

    private List<NameValueStatisticItem> toStats(Map<String, Long> source) {
        List<NameValueStatisticItem> stats = new ArrayList<>();
        source.forEach((key, value) -> stats.add(new NameValueStatisticItem(key, value)));
        return stats;
    }

    private Long resolveRegistrationCollegeId(CompetitionRegistration registration) {
        if (registration.getUserId() != null) {
            SysUser user = sysUserMapper.selectById(registration.getUserId());
            return user == null ? null : user.getCollegeId();
        }
        if (registration.getTeamId() != null) {
            CompTeam team = compTeamMapper.selectById(registration.getTeamId());
            if (team != null) {
                SysUser leader = sysUserMapper.selectById(team.getLeaderUserId());
                return leader == null ? null : leader.getCollegeId();
            }
        }
        return null;
    }

    private static class CollegeAccumulator {
        private final String collegeName;
        private long registrationCount;
        private long approvedRegistrationCount;
        private long awardCount;
        private long approvedAwardCount;

        private CollegeAccumulator(String collegeName) {
            this.collegeName = collegeName;
        }
    }
}
