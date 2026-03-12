package com.competition.system.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.system.common.exception.BusinessException;
import com.competition.system.common.model.PageResult;
import com.competition.system.competition.constant.WorkflowConstants;
import com.competition.system.competition.dto.IndividualRegistrationSubmitRequest;
import com.competition.system.competition.dto.RegistrationReviewQueryRequest;
import com.competition.system.competition.dto.RegistrationReviewRequest;
import com.competition.system.competition.dto.StudentCompetitionQueryRequest;
import com.competition.system.competition.dto.TeamCreateRequest;
import com.competition.system.competition.dto.TeamMemberAddRequest;
import com.competition.system.competition.dto.TeamSubmitRequest;
import com.competition.system.competition.dto.TeamUpdateRequest;
import com.competition.system.competition.entity.AuditRecord;
import com.competition.system.competition.entity.CompCategory;
import com.competition.system.competition.entity.CompTeam;
import com.competition.system.competition.entity.CompTeamMember;
import com.competition.system.competition.entity.Competition;
import com.competition.system.competition.entity.CompetitionRegistration;
import com.competition.system.competition.entity.TeacherInfo;
import com.competition.system.competition.mapper.AuditRecordMapper;
import com.competition.system.competition.mapper.CompCategoryMapper;
import com.competition.system.competition.mapper.CompTeamMapper;
import com.competition.system.competition.mapper.CompTeamMemberMapper;
import com.competition.system.competition.mapper.CompetitionMapper;
import com.competition.system.competition.mapper.CompetitionRegistrationMapper;
import com.competition.system.competition.mapper.TeacherInfoMapper;
import com.competition.system.competition.service.CompetitionWorkflowService;
import com.competition.system.competition.vo.RegistrationRecordItem;
import com.competition.system.competition.vo.StudentCompetitionItem;
import com.competition.system.competition.vo.StudentTeamItem;
import com.competition.system.competition.vo.TeamFormOptionsResponse;
import com.competition.system.competition.vo.TeamMemberView;
import com.competition.system.competition.vo.WorkflowOptionView;
import com.competition.system.security.LoginUser;
import com.competition.system.user.entity.SysUser;
import com.competition.system.user.mapper.SysUserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class CompetitionWorkflowServiceImpl implements CompetitionWorkflowService {

    private final CompetitionMapper competitionMapper;
    private final CompCategoryMapper compCategoryMapper;
    private final CompTeamMapper compTeamMapper;
    private final CompTeamMemberMapper compTeamMemberMapper;
    private final CompetitionRegistrationMapper competitionRegistrationMapper;
    private final AuditRecordMapper auditRecordMapper;
    private final TeacherInfoMapper teacherInfoMapper;
    private final SysUserMapper sysUserMapper;

    public CompetitionWorkflowServiceImpl(CompetitionMapper competitionMapper,
                                          CompCategoryMapper compCategoryMapper,
                                          CompTeamMapper compTeamMapper,
                                          CompTeamMemberMapper compTeamMemberMapper,
                                          CompetitionRegistrationMapper competitionRegistrationMapper,
                                          AuditRecordMapper auditRecordMapper,
                                          TeacherInfoMapper teacherInfoMapper,
                                          SysUserMapper sysUserMapper) {
        this.competitionMapper = competitionMapper;
        this.compCategoryMapper = compCategoryMapper;
        this.compTeamMapper = compTeamMapper;
        this.compTeamMemberMapper = compTeamMemberMapper;
        this.competitionRegistrationMapper = competitionRegistrationMapper;
        this.auditRecordMapper = auditRecordMapper;
        this.teacherInfoMapper = teacherInfoMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public List<StudentCompetitionItem> listStudentCompetitions(StudentCompetitionQueryRequest request) {
        Long currentUserId = currentUserId();
        List<Competition> competitions = competitionMapper.selectList(new LambdaQueryWrapper<Competition>()
                .eq(Competition::getStatus, "PUBLISHED")
                .like(StringUtils.hasText(request.getTitle()), Competition::getTitle, request.getTitle())
                .eq(StringUtils.hasText(request.getTeamMode()), Competition::getTeamMode, request.getTeamMode())
                .orderByAsc(Competition::getRegistrationEnd)
                .orderByAsc(Competition::getId));

        return competitions.stream().map(item -> {
            CompetitionRegistration registration = null;
            CompTeam team = null;
            if (WorkflowConstants.REGISTRATION_TYPE_TEAM.equals(item.getTeamMode())) {
                team = findOwnedTeamByCompetition(item.getId(), currentUserId);
                if (team != null) {
                    registration = findRegistrationByTeamId(team.getId());
                }
            } else {
                registration = findIndividualRegistration(item.getId(), currentUserId);
            }
            CompCategory category = item.getCategoryId() == null ? null : compCategoryMapper.selectById(item.getCategoryId());
            return StudentCompetitionItem.builder()
                    .id(item.getId())
                    .title(item.getTitle())
                    .categoryName(category == null ? null : category.getCategoryName())
                    .levelName(item.getLevelName())
                    .organizer(item.getOrganizer())
                    .registrationStart(item.getRegistrationStart())
                    .registrationEnd(item.getRegistrationEnd())
                    .teamMode(item.getTeamMode())
                    .maxTeamSize(item.getMaxTeamSize())
                    .description(item.getDescription())
                    .myAuditStatus(registration == null ? null : registration.getAuditStatus())
                    .myTeamId(team == null ? null : team.getId())
                    .myTeamStatus(team == null ? null : team.getTeamStatus())
                    .build();
        }).toList();
    }

    @Override
    public List<RegistrationRecordItem> listMyRegistrations() {
        Long currentUserId = currentUserId();
        List<Long> teamIds = myOwnedTeams().stream().map(CompTeam::getId).toList();
        List<CompetitionRegistration> registrations = competitionRegistrationMapper.selectList(new LambdaQueryWrapper<CompetitionRegistration>()
                .and(wrapper -> wrapper.eq(CompetitionRegistration::getUserId, currentUserId)
                        .or(!teamIds.isEmpty(), inner -> inner.in(CompetitionRegistration::getTeamId, teamIds)))
                .orderByDesc(CompetitionRegistration::getSubmitTime)
                .orderByDesc(CompetitionRegistration::getId));
        return registrations.stream().map(this::toRegistrationRecordItem).toList();
    }

    @Override
    public List<StudentTeamItem> listMyTeams() {
        return myOwnedTeams().stream().map(this::toStudentTeamItem).toList();
    }

    @Override
    public StudentTeamItem getMyTeamDetail(Long teamId) {
        return toStudentTeamItem(requireOwnedTeam(teamId));
    }

    @Override
    public TeamFormOptionsResponse getTeamFormOptions() {
        Long currentUserId = currentUserId();
        List<WorkflowOptionView> competitions = competitionMapper.selectList(new LambdaQueryWrapper<Competition>()
                        .eq(Competition::getStatus, "PUBLISHED")
                        .eq(Competition::getTeamMode, WorkflowConstants.REGISTRATION_TYPE_TEAM)
                        .orderByAsc(Competition::getRegistrationEnd))
                .stream()
                .map(item -> new WorkflowOptionView(item.getId(), item.getTitle(), "最多 " + item.getMaxTeamSize() + " 人"))
                .toList();

        List<WorkflowOptionView> teachers = teacherInfoMapper.selectList(new LambdaQueryWrapper<TeacherInfo>()
                        .orderByAsc(TeacherInfo::getId))
                .stream()
                .map(item -> {
                    SysUser teacherUser = sysUserMapper.selectById(item.getUserId());
                    String label = teacherUser == null ? item.getTeacherNo() : teacherUser.getRealName();
                    String extra = item.getTitleName() == null ? item.getDepartment() : item.getTitleName();
                    return new WorkflowOptionView(item.getId(), label, extra);
                })
                .toList();

        List<WorkflowOptionView> students = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUserType, "STUDENT")
                        .eq(SysUser::getStatus, 1)
                        .ne(SysUser::getId, currentUserId)
                        .orderByAsc(SysUser::getId))
                .stream()
                .map(item -> new WorkflowOptionView(
                        item.getId(),
                        item.getRealName() + "（" + item.getUsername() + "）",
                        sysUserMapper.selectCollegeNameByCollegeId(item.getCollegeId())))
                .toList();

        return TeamFormOptionsResponse.builder()
                .competitions(competitions)
                .teachers(teachers)
                .students(students)
                .build();
    }

    @Override
    @Transactional
    public Long submitIndividualRegistration(IndividualRegistrationSubmitRequest request) {
        Long currentUserId = currentUserId();
        Competition competition = requireCompetition(request.getCompetitionId());
        ensureCompetitionOpenForStudent(competition);
        if (!WorkflowConstants.REGISTRATION_TYPE_INDIVIDUAL.equals(competition.getTeamMode())) {
            throw new BusinessException("该竞赛仅支持团队报名");
        }

        CompetitionRegistration existing = findIndividualRegistration(competition.getId(), currentUserId);
        if (existing != null && WorkflowConstants.REGISTRATION_STATUS_PENDING.equals(existing.getAuditStatus())) {
            throw new BusinessException("当前报名已提交，正在等待院级审核");
        }
        if (existing != null && WorkflowConstants.REGISTRATION_STATUS_APPROVED.equals(existing.getAuditStatus())) {
            throw new BusinessException("当前竞赛已报名通过，无需重复提交");
        }

        if (existing == null) {
            existing = new CompetitionRegistration();
            existing.setCompetitionId(competition.getId());
            existing.setUserId(currentUserId);
            existing.setRegistrationType(WorkflowConstants.REGISTRATION_TYPE_INDIVIDUAL);
        }
        LocalDateTime now = LocalDateTime.now();
        existing.setAuditStatus(WorkflowConstants.REGISTRATION_STATUS_PENDING);
        existing.setSubmitTime(now);
        existing.setFinalSubmitTime(now);
        existing.setRemark(request.getRemark());
        saveOrUpdateRegistration(existing);
        return existing.getId();
    }

    @Override
    @Transactional
    public Long createTeam(TeamCreateRequest request) {
        Long currentUserId = currentUserId();
        Competition competition = requireCompetition(request.getCompetitionId());
        ensureCompetitionOpenForStudent(competition);
        if (!WorkflowConstants.REGISTRATION_TYPE_TEAM.equals(competition.getTeamMode())) {
            throw new BusinessException("该竞赛仅支持个人报名");
        }
        if (findOwnedTeamByCompetition(competition.getId(), currentUserId) != null) {
            throw new BusinessException("你已在该竞赛下创建团队");
        }

        TeacherInfo teacherInfo = request.getTeacherId() == null ? null : requireTeacherInfo(request.getTeacherId());

        CompTeam team = new CompTeam();
        team.setCompetitionId(competition.getId());
        team.setTeamName(request.getTeamName());
        team.setLeaderUserId(currentUserId);
        team.setTeacherId(teacherInfo == null ? null : teacherInfo.getId());
        team.setTeamStatus(WorkflowConstants.TEAM_STATUS_FORMING);
        team.setRemark(request.getRemark());
        compTeamMapper.insert(team);

        insertTeamMember(team.getId(), currentUserId, WorkflowConstants.MEMBER_ROLE_LEADER);
        addMembersInternal(team, request.getMemberUserIds());
        return team.getId();
    }

    @Override
    @Transactional
    public void updateTeam(Long teamId, TeamUpdateRequest request) {
        CompTeam team = requireOwnedTeam(teamId);
        ensureEditableTeam(team);
        TeacherInfo teacherInfo = request.getTeacherId() == null ? null : requireTeacherInfo(request.getTeacherId());
        team.setTeamName(request.getTeamName());
        team.setTeacherId(teacherInfo == null ? null : teacherInfo.getId());
        team.setRemark(request.getRemark());
        compTeamMapper.updateById(team);
    }

    @Override
    @Transactional
    public void addTeamMember(Long teamId, TeamMemberAddRequest request) {
        CompTeam team = requireOwnedTeam(teamId);
        ensureEditableTeam(team);
        ensureStudentMemberAvailable(team, request.getUserId(), null);
        long currentCount = compTeamMemberMapper.selectCount(new LambdaQueryWrapper<CompTeamMember>()
                .eq(CompTeamMember::getTeamId, teamId));
        Competition competition = requireCompetition(team.getCompetitionId());
        if (competition.getMaxTeamSize() != null && currentCount >= competition.getMaxTeamSize()) {
            throw new BusinessException("团队人数已达到上限");
        }
        insertTeamMember(teamId, request.getUserId(), WorkflowConstants.MEMBER_ROLE_MEMBER);
    }

    @Override
    @Transactional
    public void removeTeamMember(Long teamId, Long userId) {
        CompTeam team = requireOwnedTeam(teamId);
        ensureEditableTeam(team);
        if (Objects.equals(team.getLeaderUserId(), userId)) {
            throw new BusinessException("队长不能移出团队");
        }
        CompTeamMember member = compTeamMemberMapper.selectOne(new LambdaQueryWrapper<CompTeamMember>()
                .eq(CompTeamMember::getTeamId, teamId)
                .eq(CompTeamMember::getUserId, userId)
                .last("LIMIT 1"));
        if (member == null) {
            throw new BusinessException("团队成员不存在");
        }
        compTeamMemberMapper.deleteById(member.getId());
    }

    @Override
    @Transactional
    public void submitTeamRegistration(Long teamId, TeamSubmitRequest request) {
        CompTeam team = requireOwnedTeam(teamId);
        ensureEditableTeam(team);
        Competition competition = requireCompetition(team.getCompetitionId());
        ensureCompetitionOpenForStudent(competition);
        if (team.getTeacherId() == null) {
            throw new BusinessException("提交审核前请先关联指导教师");
        }

        long memberCount = compTeamMemberMapper.selectCount(new LambdaQueryWrapper<CompTeamMember>()
                .eq(CompTeamMember::getTeamId, teamId));
        if (memberCount < 1) {
            throw new BusinessException("团队成员不能为空");
        }

        CompetitionRegistration registration = findRegistrationByTeamId(teamId);
        if (registration != null && WorkflowConstants.REGISTRATION_STATUS_PENDING.equals(registration.getAuditStatus())) {
            throw new BusinessException("当前团队已提交审核，请勿重复提交");
        }
        if (registration != null && WorkflowConstants.REGISTRATION_STATUS_APPROVED.equals(registration.getAuditStatus())) {
            throw new BusinessException("当前团队已审核通过");
        }
        if (registration == null) {
            registration = new CompetitionRegistration();
            registration.setCompetitionId(team.getCompetitionId());
            registration.setTeamId(teamId);
            registration.setRegistrationType(WorkflowConstants.REGISTRATION_TYPE_TEAM);
        }

        LocalDateTime now = LocalDateTime.now();
        registration.setAuditStatus(WorkflowConstants.REGISTRATION_STATUS_PENDING);
        registration.setSubmitTime(now);
        registration.setFinalSubmitTime(now);
        registration.setRemark(StringUtils.hasText(request.getRemark()) ? request.getRemark() : team.getRemark());
        saveOrUpdateRegistration(registration);

        team.setTeamStatus(WorkflowConstants.TEAM_STATUS_PENDING);
        compTeamMapper.updateById(team);
    }

    @Override
    public PageResult<RegistrationRecordItem> pageReviewRegistrations(RegistrationReviewQueryRequest request) {
        Page<CompetitionRegistration> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<CompetitionRegistration> wrapper = new LambdaQueryWrapper<CompetitionRegistration>()
                .eq(StringUtils.hasText(request.getAuditStatus()), CompetitionRegistration::getAuditStatus, request.getAuditStatus())
                .eq(StringUtils.hasText(request.getRegistrationType()), CompetitionRegistration::getRegistrationType, request.getRegistrationType())
                .eq(request.getCompetitionId() != null, CompetitionRegistration::getCompetitionId, request.getCompetitionId())
                .orderByDesc(CompetitionRegistration::getSubmitTime)
                .orderByDesc(CompetitionRegistration::getId);
        Page<CompetitionRegistration> result = competitionRegistrationMapper.selectPage(page, wrapper);
        return PageResult.<RegistrationRecordItem>builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(result.getRecords().stream().map(this::toRegistrationRecordItem).toList())
                .build();
    }

    @Override
    @Transactional
    public void approveRegistration(Long registrationId, RegistrationReviewRequest request) {
        reviewRegistration(registrationId, request.getOpinion(), true);
    }

    @Override
    @Transactional
    public void rejectRegistration(Long registrationId, RegistrationReviewRequest request) {
        reviewRegistration(registrationId, request.getOpinion(), false);
    }

    private void reviewRegistration(Long registrationId, String opinion, boolean approved) {
        CompetitionRegistration registration = requireRegistration(registrationId);
        if (!WorkflowConstants.REGISTRATION_STATUS_PENDING.equals(registration.getAuditStatus())) {
            throw new BusinessException("当前报名不在待审核状态");
        }

        registration.setAuditStatus(approved
                ? WorkflowConstants.REGISTRATION_STATUS_APPROVED
                : WorkflowConstants.REGISTRATION_STATUS_REJECTED);
        competitionRegistrationMapper.updateById(registration);

        if (registration.getTeamId() != null) {
            CompTeam team = compTeamMapper.selectById(registration.getTeamId());
            if (team != null) {
                team.setTeamStatus(approved
                        ? WorkflowConstants.TEAM_STATUS_APPROVED
                        : WorkflowConstants.TEAM_STATUS_REJECTED);
                compTeamMapper.updateById(team);
            }
        }

        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setBusinessType(WorkflowConstants.AUDIT_BUSINESS_REGISTRATION);
        auditRecord.setBusinessId(registrationId);
        auditRecord.setAuditStage(WorkflowConstants.AUDIT_STAGE_COLLEGE);
        auditRecord.setAuditStatus(approved
                ? WorkflowConstants.AUDIT_RESULT_APPROVED
                : WorkflowConstants.AUDIT_RESULT_REJECTED);
        auditRecord.setAuditorId(currentUserId());
        auditRecord.setAuditOpinion(opinion);
        auditRecord.setAuditTime(LocalDateTime.now());
        auditRecordMapper.insert(auditRecord);
    }

    private void addMembersInternal(CompTeam team, List<Long> memberUserIds) {
        if (memberUserIds == null || memberUserIds.isEmpty()) {
            return;
        }
        Set<Long> userIds = new LinkedHashSet<>(memberUserIds);
        userIds.remove(team.getLeaderUserId());
        Competition competition = requireCompetition(team.getCompetitionId());
        long maxSize = competition.getMaxTeamSize() == null ? Long.MAX_VALUE : competition.getMaxTeamSize();
        if (userIds.size() + 1 > maxSize) {
            throw new BusinessException("团队人数超过竞赛上限");
        }
        for (Long userId : userIds) {
            ensureStudentMemberAvailable(team, userId, null);
            insertTeamMember(team.getId(), userId, WorkflowConstants.MEMBER_ROLE_MEMBER);
        }
    }

    private void insertTeamMember(Long teamId, Long userId, String role) {
        CompTeamMember member = new CompTeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setMemberRole(role);
        member.setJoinStatus(WorkflowConstants.JOIN_STATUS_JOINED);
        compTeamMemberMapper.insert(member);
    }

    private void ensureStudentMemberAvailable(CompTeam team, Long userId, Long ignoreTeamId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != 1 || !"STUDENT".equals(user.getUserType())) {
            throw new BusinessException("请选择有效的学生成员");
        }
        boolean existsInCurrentTeam = compTeamMemberMapper.selectCount(new LambdaQueryWrapper<CompTeamMember>()
                .eq(CompTeamMember::getTeamId, team.getId())
                .eq(CompTeamMember::getUserId, userId)) > 0;
        if (existsInCurrentTeam) {
            throw new BusinessException("该成员已在当前团队中");
        }

        List<CompTeamMember> memberships = compTeamMemberMapper.selectList(new LambdaQueryWrapper<CompTeamMember>()
                .eq(CompTeamMember::getUserId, userId));
        for (CompTeamMember membership : memberships) {
            if (ignoreTeamId != null && Objects.equals(ignoreTeamId, membership.getTeamId())) {
                continue;
            }
            CompTeam otherTeam = compTeamMapper.selectById(membership.getTeamId());
            if (otherTeam != null && Objects.equals(otherTeam.getCompetitionId(), team.getCompetitionId())) {
                throw new BusinessException("该学生已加入当前竞赛的其他团队");
            }
        }
    }

    private List<CompTeam> myOwnedTeams() {
        Long currentUserId = currentUserId();
        return compTeamMapper.selectList(new LambdaQueryWrapper<CompTeam>()
                .eq(CompTeam::getLeaderUserId, currentUserId)
                .orderByDesc(CompTeam::getCreateTime)
                .orderByDesc(CompTeam::getId));
    }

    private CompTeam requireOwnedTeam(Long teamId) {
        CompTeam team = compTeamMapper.selectById(teamId);
        if (team == null || !Objects.equals(team.getLeaderUserId(), currentUserId())) {
            throw new BusinessException("团队不存在或无权操作");
        }
        return team;
    }

    private void ensureEditableTeam(CompTeam team) {
        if (!(WorkflowConstants.TEAM_STATUS_FORMING.equals(team.getTeamStatus())
                || WorkflowConstants.TEAM_STATUS_REJECTED.equals(team.getTeamStatus()))) {
            throw new BusinessException("当前团队状态下不允许编辑");
        }
    }

    private CompTeam findOwnedTeamByCompetition(Long competitionId, Long userId) {
        return compTeamMapper.selectOne(new LambdaQueryWrapper<CompTeam>()
                .eq(CompTeam::getCompetitionId, competitionId)
                .eq(CompTeam::getLeaderUserId, userId)
                .last("LIMIT 1"));
    }

    private CompetitionRegistration findIndividualRegistration(Long competitionId, Long userId) {
        return competitionRegistrationMapper.selectOne(new LambdaQueryWrapper<CompetitionRegistration>()
                .eq(CompetitionRegistration::getCompetitionId, competitionId)
                .eq(CompetitionRegistration::getUserId, userId)
                .eq(CompetitionRegistration::getRegistrationType, WorkflowConstants.REGISTRATION_TYPE_INDIVIDUAL)
                .last("LIMIT 1"));
    }

    private CompetitionRegistration findRegistrationByTeamId(Long teamId) {
        return competitionRegistrationMapper.selectOne(new LambdaQueryWrapper<CompetitionRegistration>()
                .eq(CompetitionRegistration::getTeamId, teamId)
                .eq(CompetitionRegistration::getRegistrationType, WorkflowConstants.REGISTRATION_TYPE_TEAM)
                .last("LIMIT 1"));
    }

    private Competition requireCompetition(Long competitionId) {
        Competition competition = competitionMapper.selectById(competitionId);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        return competition;
    }

    private void ensureCompetitionOpenForStudent(Competition competition) {
        if (!"PUBLISHED".equals(competition.getStatus())) {
            throw new BusinessException("当前竞赛未开放报名");
        }
    }

    private TeacherInfo requireTeacherInfo(Long teacherId) {
        TeacherInfo teacherInfo = teacherInfoMapper.selectById(teacherId);
        if (teacherInfo == null) {
            throw new BusinessException("指导教师不存在");
        }
        return teacherInfo;
    }

    private CompetitionRegistration requireRegistration(Long registrationId) {
        CompetitionRegistration registration = competitionRegistrationMapper.selectById(registrationId);
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }
        return registration;
    }

    private void saveOrUpdateRegistration(CompetitionRegistration registration) {
        if (registration.getId() == null) {
            competitionRegistrationMapper.insert(registration);
        } else {
            competitionRegistrationMapper.updateById(registration);
        }
    }

    private RegistrationRecordItem toRegistrationRecordItem(CompetitionRegistration registration) {
        Competition competition = competitionMapper.selectById(registration.getCompetitionId());
        CompTeam team = registration.getTeamId() == null ? null : compTeamMapper.selectById(registration.getTeamId());
        SysUser applicantUser = registration.getUserId() != null
                ? sysUserMapper.selectById(registration.getUserId())
                : (team == null ? null : sysUserMapper.selectById(team.getLeaderUserId()));

        List<TeamMemberView> members = team == null ? List.of() : loadTeamMembers(team.getId());
        TeacherInfo teacherInfo = team == null || team.getTeacherId() == null ? null : teacherInfoMapper.selectById(team.getTeacherId());
        SysUser teacherUser = teacherInfo == null ? null : sysUserMapper.selectById(teacherInfo.getUserId());
        AuditRecord latestAudit = latestAuditRecord(registration.getId());

        String memberSummary = members.isEmpty()
                ? null
                : members.stream().map(TeamMemberView::getRealName).filter(Objects::nonNull).reduce((a, b) -> a + "、" + b).orElse(null);

        return RegistrationRecordItem.builder()
                .id(registration.getId())
                .competitionId(registration.getCompetitionId())
                .competitionTitle(competition == null ? null : competition.getTitle())
                .registrationType(registration.getRegistrationType())
                .applicantUserId(applicantUser == null ? null : applicantUser.getId())
                .applicantName(applicantUser == null ? null : applicantUser.getRealName())
                .teamId(team == null ? null : team.getId())
                .teamName(team == null ? null : team.getTeamName())
                .teamStatus(team == null ? null : team.getTeamStatus())
                .teacherName(teacherUser == null ? null : teacherUser.getRealName())
                .memberSummary(memberSummary)
                .auditStatus(registration.getAuditStatus())
                .latestAuditOpinion(latestAudit == null ? null : latestAudit.getAuditOpinion())
                .submitTime(registration.getSubmitTime())
                .finalSubmitTime(registration.getFinalSubmitTime())
                .remark(registration.getRemark())
                .build();
    }

    private StudentTeamItem toStudentTeamItem(CompTeam team) {
        Competition competition = competitionMapper.selectById(team.getCompetitionId());
        TeacherInfo teacherInfo = team.getTeacherId() == null ? null : teacherInfoMapper.selectById(team.getTeacherId());
        SysUser teacherUser = teacherInfo == null ? null : sysUserMapper.selectById(teacherInfo.getUserId());
        CompetitionRegistration registration = findRegistrationByTeamId(team.getId());
        AuditRecord latestAudit = registration == null ? null : latestAuditRecord(registration.getId());
        return StudentTeamItem.builder()
                .id(team.getId())
                .competitionId(team.getCompetitionId())
                .competitionTitle(competition == null ? null : competition.getTitle())
                .teamName(team.getTeamName())
                .teamStatus(team.getTeamStatus())
                .maxTeamSize(competition == null ? null : competition.getMaxTeamSize())
                .teacherId(team.getTeacherId())
                .teacherName(teacherUser == null ? null : teacherUser.getRealName())
                .teacherTitle(teacherInfo == null ? null : teacherInfo.getTitleName())
                .remark(team.getRemark())
                .latestAuditOpinion(latestAudit == null ? null : latestAudit.getAuditOpinion())
                .submitTime(registration == null ? null : registration.getSubmitTime())
                .members(loadTeamMembers(team.getId()))
                .build();
    }

    private List<TeamMemberView> loadTeamMembers(Long teamId) {
        List<CompTeamMember> memberEntities = compTeamMemberMapper.selectList(new LambdaQueryWrapper<CompTeamMember>()
                .eq(CompTeamMember::getTeamId, teamId)
                .orderByAsc(CompTeamMember::getId));
        List<TeamMemberView> members = new ArrayList<>();
        for (CompTeamMember entity : memberEntities) {
            SysUser user = sysUserMapper.selectById(entity.getUserId());
            if (user == null) {
                continue;
            }
            members.add(TeamMemberView.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .realName(user.getRealName())
                    .collegeName(sysUserMapper.selectCollegeNameByCollegeId(user.getCollegeId()))
                    .memberRole(entity.getMemberRole())
                    .build());
        }
        return members;
    }

    private AuditRecord latestAuditRecord(Long registrationId) {
        return auditRecordMapper.selectOne(new LambdaQueryWrapper<AuditRecord>()
                .eq(AuditRecord::getBusinessType, WorkflowConstants.AUDIT_BUSINESS_REGISTRATION)
                .eq(AuditRecord::getBusinessId, registrationId)
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
