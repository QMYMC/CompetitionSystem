package com.competition.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.competition.system.auth.dto.LoginRequest;
import com.competition.system.auth.vo.LoginResponse;
import com.competition.system.common.enums.ResultCode;
import com.competition.system.common.exception.BusinessException;
import com.competition.system.security.JwtProperties;
import com.competition.system.security.JwtTokenProvider;
import com.competition.system.security.LoginUser;
import com.competition.system.user.entity.SysUser;
import com.competition.system.user.mapper.SysUserMapper;
import com.competition.system.user.service.SysUserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public SysUserServiceImpl(SysUserMapper sysUserMapper,
                              @Lazy AuthenticationManager authenticationManager,
                              JwtTokenProvider jwtTokenProvider,
                              JwtProperties jwtProperties) {
        this.sysUserMapper = sysUserMapper;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public Optional<LoginUser> findLoginUserByUsername(String username) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0)
                .last("LIMIT 1"));
        if (user == null) {
            return Optional.empty();
        }
        List<String> roles = sysUserMapper.selectRoleCodesByUserId(user.getId());
        return Optional.of(new LoginUser(user, roles));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String token = jwtTokenProvider.createToken(
                loginUser.getUser().getId(),
                loginUser.getUsername(),
                loginUser.getRoles()
        );
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expireAt(LocalDateTime.now().plusMinutes(jwtProperties.getExpirationMinutes()))
                .user(buildUserProfile(loginUser))
                .build();
    }

    @Override
    public LoginResponse.UserProfile getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return buildUserProfile(loginUser);
    }

    private LoginResponse.UserProfile buildUserProfile(LoginUser loginUser) {
        SysUser user = loginUser.getUser();
        String collegeName = user.getCollegeId() == null ? null : sysUserMapper.selectCollegeNameByCollegeId(user.getCollegeId());
        return LoginResponse.UserProfile.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .collegeId(user.getCollegeId())
                .collegeName(collegeName)
                .roles(loginUser.getRoles())
                .build();
    }
}
