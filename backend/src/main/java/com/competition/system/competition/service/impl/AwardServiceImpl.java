package com.competition.system.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.system.common.exception.BusinessException;
import com.competition.system.common.model.PageResult;
import com.competition.system.competition.constant.WorkflowConstants;
import com.competition.system.competition.dto.AwardReviewQueryRequest;
import com.competition.system.competition.dto.AwardReviewRequest;
import com.competition.system.competition.dto.StudentAwardCreateRequest;
import com.competition.system.competition.dto.StudentAwardUpdateRequest;
import com.competition.system.competition.entity.AuditRecord;
import com.competition.system.competition.entity.AwardInfo;
import com.competition.system.competition.entity.CompTeam;
import com.competition.system.competition.entity.Competition;
import com.competition.system.competition.entity.CompetitionRegistration;
import com.competition.system.competition.mapper.AuditRecordMapper;
import com.competition.system.competition.mapper.AwardInfoMapper;
import com.competition.system.competition.mapper.CompTeamMapper;
import com.competition.system.competition.mapper.CompetitionMapper;
import com.competition.system.competition.mapper.CompetitionRegistrationMapper;
import com.competition.system.competition.service.AwardService;
import com.competition.system.competition.vo.AwardOptionItem;
import com.competition.system.competition.vo.AwardReviewItem;
import com.competition.system.competition.vo.StudentAwardItem;
import com.competition.system.security.LoginUser;
import com.competition.system.user.entity.SysUser;
import com.competition.system.user.mapper.SysUserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class AwardServiceImpl implements AwardService {

    private final AwardInfoMapper awardInfoMapper;
    private final CompetitionRegistrationMapper competitionRegistrationMapper;
    private final CompetitionMapper competitionMapper;
    private final CompTeamMapper compTeamMapper;
    private final AuditRecordMapper auditRecordMapper;
    private final SysUserMapper sysUserMapper;

    public AwardServiceImpl(AwardInfoMapper awardInfoMapper,
                            CompetitionRegistrationMapper competitionRegistrationMapper,
                            CompetitionMapper competitionMapper,
                            CompTeamMapper compTeamMapper,
                            AuditRecordMapper auditRecordMapper,
                            SysUserMapper sysUserMapper) {
        this.awardInfoMapper = awardInfoMapper;
        this.competitionRegistrationMapper = competitionRegistrationMapper;
        this.competitionMapper = competitionMapper;
        this.compTeamMapper = compTeamMapper;
        this.auditRecordMapper = auditRecordMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public List<AwardOptionItem> getStudentAwardOptions() {
        Long currentUserId = currentUserId();
        List<CompetitionRegistration> registrations = competitionRegistrationMapper.selectList(new LambdaQueryWrapper<CompetitionRegistration>()
                .eq(CompetitionRegistration::getAuditStatus, WorkflowConstants.REGISTRATION_STATUS_APPROVED)
                .orderByDesc(CompetitionRegistration::getFinalSubmitTime)
                .orderByDesc(CompetitionRegistration::getId));
        return registrations.stream()
                .filter(this::isRegistrationOwnedByCurrentStudent)
                .map(item -> {
                    Competition competition = competitionMapper.selectById(item.getCompetitionId());
                    CompTeam team = item.getTeamId() == null ? null : compTeamMapper.selectById(item.getTeamId());
                    return AwardOptionItem.builder()
                            .registrationId(item.getId())
                            .competitionId(item.getCompetitionId())
                            .competitionTitle(competition == null ? null : competition.getTitle())
                            .registrationType(item.getRegistrationType())
                            .teamName(team == null ? null : team.getTeamName())
                            .build();
                })
                .toList();
    }

    @Override
    public List<StudentAwardItem> listMyAwards() {
        return awardInfoMapper.selectList(new LambdaQueryWrapper<AwardInfo>()
                        .orderByDesc(AwardInfo::getCreateTime)
                        .orderByDesc(AwardInfo::getId))
                .stream()
                .filter(this::isAwardOwnedByCurrentStudent)
                .map(this::toStudentAwardItem)
                .toList();
    }

    @Override
    @Transactional
    public Long createAward(StudentAwardCreateRequest request) {
        CompetitionRegistration registration = requireOwnedApprovedRegistration(request.getRegistrationId());
        AwardInfo awardInfo = new AwardInfo();
        awardInfo.setCompetitionId(registration.getCompetitionId());
        awardInfo.setRegistrationId(registration.getId());
        applyAwardFields(awardInfo, request.getAwardName(), request.getAwardLevel(), request.getAwardRank(), request.getAwardTime(), request.getRemark());
        awardInfo.setAuditStatus(WorkflowConstants.AWARD_STATUS_PENDING);
        awardInfoMapper.insert(awardInfo);
        return awardInfo.getId();
    }

    @Override
    @Transactional
    public void updateAward(Long awardId, StudentAwardUpdateRequest request) {
        AwardInfo awardInfo = requireOwnedAward(awardId);
        if (WorkflowConstants.AWARD_STATUS_APPROVED.equals(awardInfo.getAuditStatus())) {
            throw new BusinessException("已通过的获奖信息不允许学生修改");
        }
        applyAwardFields(awardInfo, request.getAwardName(), request.getAwardLevel(), request.getAwardRank(), request.getAwardTime(), request.getRemark());
        awardInfo.setAuditStatus(WorkflowConstants.AWARD_STATUS_PENDING);
        awardInfoMapper.updateById(awardInfo);
    }

    @Override
    public PageResult<AwardReviewItem> pageAwardsForReview(AwardReviewQueryRequest request) {
        Page<AwardInfo> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<AwardInfo> wrapper = new LambdaQueryWrapper<AwardInfo>()
                .eq(StringUtils.hasText(request.getAuditStatus()), AwardInfo::getAuditStatus, request.getAuditStatus())
                .eq(request.getCompetitionId() != null, AwardInfo::getCompetitionId, request.getCompetitionId())
                .like(StringUtils.hasText(request.getKeyword()), AwardInfo::getAwardName, request.getKeyword())
                .orderByDesc(AwardInfo::getCreateTime)
                .orderByDesc(AwardInfo::getId);
        Page<AwardInfo> result = awardInfoMapper.selectPage(page, wrapper);
        return PageResult.<AwardReviewItem>builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(result.getRecords().stream().map(this::toAwardReviewItem).toList())
                .build();
    }

    @Override
    @Transactional
    public void approveAward(Long awardId, AwardReviewRequest request) {
        reviewAward(awardId, request.getOpinion(), true);
    }

    @Override
    @Transactional
    public void rejectAward(Long awardId, AwardReviewRequest request) {
        reviewAward(awardId, request.getOpinion(), false);
    }

    private void reviewAward(Long awardId, String opinion, boolean approved) {
        AwardInfo awardInfo = requireAward(awardId);
        if (!WorkflowConstants.AWARD_STATUS_PENDING.equals(awardInfo.getAuditStatus())) {
            throw new BusinessException("当前获奖信息不在待审核状态");
        }
        awardInfo.setAuditStatus(approved ? WorkflowConstants.AWARD_STATUS_APPROVED : WorkflowConstants.AWARD_STATUS_REJECTED);
        awardInfoMapper.updateById(awardInfo);

        AuditRecord record = new AuditRecord();
        record.setBusinessType(WorkflowConstants.AUDIT_BUSINESS_AWARD);
        record.setBusinessId(awardId);
        record.setAuditStage(WorkflowConstants.AUDIT_STAGE_COLLEGE);
        record.setAuditStatus(approved ? WorkflowConstants.AUDIT_RESULT_APPROVED : WorkflowConstants.AUDIT_RESULT_REJECTED);
        record.setAuditorId(currentUserId());
        record.setAuditOpinion(opinion);
        record.setAuditTime(LocalDateTime.now());
        auditRecordMapper.insert(record);
    }

    private StudentAwardItem toStudentAwardItem(AwardInfo awardInfo) {
        CompetitionRegistration registration = competitionRegistrationMapper.selectById(awardInfo.getRegistrationId());
        Competition competition = competitionMapper.selectById(awardInfo.getCompetitionId());
        CompTeam team = registration == null || registration.getTeamId() == null ? null : compTeamMapper.selectById(registration.getTeamId());
        AuditRecord latestAudit = latestAwardAudit(awardInfo.getId());
        return StudentAwardItem.builder()
                .id(awardInfo.getId())
                .registrationId(awardInfo.getRegistrationId())
                .competitionId(awardInfo.getCompetitionId())
                .competitionTitle(competition == null ? null : competition.getTitle())
                .registrationType(registration == null ? null : registration.getRegistrationType())
                .teamName(team == null ? null : team.getTeamName())
                .awardName(awardInfo.getAwardName())
                .awardLevel(awardInfo.getAwardLevel())
                .awardRank(awardInfo.getAwardRank())
                .awardTime(awardInfo.getAwardTime())
                .auditStatus(awardInfo.getAuditStatus())
                .latestAuditOpinion(latestAudit == null ? null : latestAudit.getAuditOpinion())
                .remark(awardInfo.getRemark())
                .createTime(awardInfo.getCreateTime())
                .build();
    }

    private AwardReviewItem toAwardReviewItem(AwardInfo awardInfo) {
        CompetitionRegistration registration = competitionRegistrationMapper.selectById(awardInfo.getRegistrationId());
        Competition competition = competitionMapper.selectById(awardInfo.getCompetitionId());
        CompTeam team = registration == null || registration.getTeamId() == null ? null : compTeamMapper.selectById(registration.getTeamId());
        SysUser applicant = resolveRegistrationOwner(registration);
        AuditRecord latestAudit = latestAwardAudit(awardInfo.getId());
        return AwardReviewItem.builder()
                .id(awardInfo.getId())
                .registrationId(awardInfo.getRegistrationId())
                .competitionId(awardInfo.getCompetitionId())
                .competitionTitle(competition == null ? null : competition.getTitle())
                .applicantName(applicant == null ? null : applicant.getRealName())
                .registrationType(registration == null ? null : registration.getRegistrationType())
                .teamName(team == null ? null : team.getTeamName())
                .awardName(awardInfo.getAwardName())
                .awardLevel(awardInfo.getAwardLevel())
                .awardRank(awardInfo.getAwardRank())
                .awardTime(awardInfo.getAwardTime())
                .auditStatus(awardInfo.getAuditStatus())
                .latestAuditOpinion(latestAudit == null ? null : latestAudit.getAuditOpinion())
                .remark(awardInfo.getRemark())
                .createTime(awardInfo.getCreateTime())
                .build();
    }

    private void applyAwardFields(AwardInfo awardInfo,
                                  String awardName,
                                  String awardLevel,
                                  String awardRank,
                                  LocalDateTime awardTime,
                                  String remark) {
        awardInfo.setAwardName(awardName);
        awardInfo.setAwardLevel(awardLevel);
        awardInfo.setAwardRank(awardRank);
        awardInfo.setAwardTime(awardTime);
        awardInfo.setRemark(remark);
    }

    private AwardInfo requireAward(Long awardId) {
        AwardInfo awardInfo = awardInfoMapper.selectById(awardId);
        if (awardInfo == null) {
            throw new BusinessException("获奖信息不存在");
        }
        return awardInfo;
    }

    private AwardInfo requireOwnedAward(Long awardId) {
        AwardInfo awardInfo = requireAward(awardId);
        if (!isAwardOwnedByCurrentStudent(awardInfo)) {
            throw new BusinessException("无权操作该获奖信息");
        }
        return awardInfo;
    }

    private CompetitionRegistration requireOwnedApprovedRegistration(Long registrationId) {
        CompetitionRegistration registration = competitionRegistrationMapper.selectById(registrationId);
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (!WorkflowConstants.REGISTRATION_STATUS_APPROVED.equals(registration.getAuditStatus())) {
            throw new BusinessException("仅已通过院级审核的报名可填报获奖信息");
        }
        if (!isRegistrationOwnedByCurrentStudent(registration)) {
            throw new BusinessException("无权使用该报名记录填报获奖信息");
        }
        return registration;
    }

    private boolean isAwardOwnedByCurrentStudent(AwardInfo awardInfo) {
        CompetitionRegistration registration = competitionRegistrationMapper.selectById(awardInfo.getRegistrationId());
        return registration != null && isRegistrationOwnedByCurrentStudent(registration);
    }

    private boolean isRegistrationOwnedByCurrentStudent(CompetitionRegistration registration) {
        Long currentUserId = currentUserId();
        if (Objects.equals(registration.getUserId(), currentUserId)) {
            return true;
        }
        if (registration.getTeamId() != null) {
            CompTeam team = compTeamMapper.selectById(registration.getTeamId());
            return team != null && Objects.equals(team.getLeaderUserId(), currentUserId);
        }
        return false;
    }

    private SysUser resolveRegistrationOwner(CompetitionRegistration registration) {
        if (registration == null) {
            return null;
        }
        if (registration.getUserId() != null) {
            return sysUserMapper.selectById(registration.getUserId());
        }
        if (registration.getTeamId() != null) {
            CompTeam team = compTeamMapper.selectById(registration.getTeamId());
            if (team != null) {
                return sysUserMapper.selectById(team.getLeaderUserId());
            }
        }
        return null;
    }

    private AuditRecord latestAwardAudit(Long awardId) {
        return auditRecordMapper.selectOne(new LambdaQueryWrapper<AuditRecord>()
                .eq(AuditRecord::getBusinessType, WorkflowConstants.AUDIT_BUSINESS_AWARD)
                .eq(AuditRecord::getBusinessId, awardId)
                .orderByDesc(AuditRecord::getAuditTime)
                .last("LIMIT 1"));
    }

    private Long currentUserId() {
        return currentLoginUser().getUser().getId();
    }

    private LoginUser currentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException("当前登录状态无效");
        }
        return loginUser;
    }
}
