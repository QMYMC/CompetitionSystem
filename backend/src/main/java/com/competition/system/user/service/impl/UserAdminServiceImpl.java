package com.competition.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.system.common.exception.BusinessException;
import com.competition.system.common.model.OptionItem;
import com.competition.system.common.model.PageResult;
import com.competition.system.user.dto.UserCreateRequest;
import com.competition.system.user.dto.UserQueryRequest;
import com.competition.system.user.dto.UserUpdateRequest;
import com.competition.system.user.entity.SysCollege;
import com.competition.system.user.entity.SysRole;
import com.competition.system.user.entity.SysUser;
import com.competition.system.user.entity.SysUserRole;
import com.competition.system.user.mapper.SysCollegeMapper;
import com.competition.system.user.mapper.SysRoleMapper;
import com.competition.system.user.mapper.SysUserMapper;
import com.competition.system.user.mapper.SysUserRoleMapper;
import com.competition.system.user.service.UserAdminService;
import com.competition.system.user.vo.UserListItem;
import com.competition.system.user.vo.UserOptionsResponse;
import com.competition.system.user.vo.UserRoleInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserAdminServiceImpl implements UserAdminService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysCollegeMapper sysCollegeMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public UserAdminServiceImpl(SysUserMapper sysUserMapper,
                                SysRoleMapper sysRoleMapper,
                                SysCollegeMapper sysCollegeMapper,
                                SysUserRoleMapper sysUserRoleMapper,
                                PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysCollegeMapper = sysCollegeMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResult<UserListItem> pageUsers(UserQueryRequest request) {
        if (request.getRoleId() != null) {
            List<Long> userIds = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                            .eq(SysUserRole::getRoleId, request.getRoleId()))
                    .stream()
                    .map(SysUserRole::getUserId)
                    .toList();
            if (userIds.isEmpty()) {
                return PageResult.<UserListItem>builder()
                        .current(request.getCurrent())
                        .size(request.getSize())
                        .total(0)
                        .records(List.of())
                        .build();
            }
            return pageUsersByRole(request, userIds);
        }

        return pageUsersByRole(request, null);
    }

    private PageResult<UserListItem> pageUsersByRole(UserQueryRequest request, List<Long> userIds) {
        Page<SysUser> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(request.getUsername()), SysUser::getUsername, request.getUsername())
                .like(StringUtils.hasText(request.getRealName()), SysUser::getRealName, request.getRealName())
                .eq(request.getStatus() != null, SysUser::getStatus, request.getStatus())
                .eq(request.getCollegeId() != null, SysUser::getCollegeId, request.getCollegeId())
                .in(userIds != null, SysUser::getId, userIds)
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserMapper.selectPage(page, wrapper);
        List<UserListItem> records = result.getRecords().stream().map(this::toListItem).toList();
        return PageResult.<UserListItem>builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(records)
                .build();
    }

    @Override
    public UserListItem getUserDetail(Long id) {
        SysUser user = requireUser(id);
        return toListItem(user);
    }

    @Override
    @Transactional
    public Long createUser(UserCreateRequest request) {
        if (sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())
                .last("LIMIT 1")) != null) {
            throw new BusinessException("用户名已存在");
        }

        SysRole role = requireRole(request.getRoleId());
        if (request.getCollegeId() != null) {
            requireCollege(request.getCollegeId());
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setUserType(role.getRoleCode());
        user.setCollegeId(request.getCollegeId());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        user.setRemark(request.getRemark());
        sysUserMapper.insert(user);

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        sysUserRoleMapper.insert(userRole);

        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        SysUser user = requireUser(id);
        SysRole role = requireRole(request.getRoleId());
        if (request.getCollegeId() != null) {
            requireCollege(request.getCollegeId());
        }

        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setUserType(role.getRoleCode());
        user.setCollegeId(request.getCollegeId());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        user.setRemark(request.getRemark());
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        sysUserMapper.updateById(user);

        SysUserRole currentRole = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id)
                .last("LIMIT 1"));
        if (currentRole == null) {
            currentRole = new SysUserRole();
            currentRole.setUserId(id);
            currentRole.setRoleId(role.getId());
            sysUserRoleMapper.insert(currentRole);
        } else if (!role.getId().equals(currentRole.getRoleId())) {
            currentRole.setRoleId(role.getId());
            sysUserRoleMapper.updateById(currentRole);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (Long.valueOf(1L).equals(id)) {
            throw new BusinessException("默认管理员不能删除");
        }
        requireUser(id);
        sysUserMapper.deleteById(id);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
        userRoles.forEach(item -> sysUserRoleMapper.deleteById(item.getId()));
    }

    @Override
    public UserOptionsResponse getOptions() {
        List<OptionItem<Long>> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, 1)
                        .orderByAsc(SysRole::getSort))
                .stream()
                .map(item -> new OptionItem<>(item.getId(), item.getRoleName()))
                .toList();

        List<OptionItem<Long>> colleges = sysCollegeMapper.selectList(new LambdaQueryWrapper<SysCollege>()
                        .eq(SysCollege::getStatus, 1)
                        .orderByAsc(SysCollege::getSort))
                .stream()
                .map(item -> new OptionItem<>(item.getId(), item.getCollegeName()))
                .toList();

        return UserOptionsResponse.builder()
                .roles(roles)
                .colleges(colleges)
                .build();
    }

    private UserListItem toListItem(SysUser user) {
        UserRoleInfo roleInfo = sysUserMapper.selectRoleInfoByUserId(user.getId());
        return UserListItem.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .status(user.getStatus())
                .collegeId(user.getCollegeId())
                .collegeName(user.getCollegeId() == null ? null : sysUserMapper.selectCollegeNameByCollegeId(user.getCollegeId()))
                .roleId(roleInfo == null ? null : roleInfo.getRoleId())
                .roleCode(roleInfo == null ? null : roleInfo.getRoleCode())
                .roleName(roleInfo == null ? null : roleInfo.getRoleName())
                .remark(user.getRemark())
                .createTime(user.getCreateTime())
                .build();
    }

    private SysUser requireUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private SysRole requireRole(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    private void requireCollege(Long id) {
        if (sysCollegeMapper.selectById(id) == null) {
            throw new BusinessException("学院不存在");
        }
    }
}
