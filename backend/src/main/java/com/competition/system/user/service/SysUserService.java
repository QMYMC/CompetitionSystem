package com.competition.system.user.service;

import com.competition.system.auth.dto.LoginRequest;
import com.competition.system.auth.vo.LoginResponse;
import com.competition.system.security.LoginUser;

import java.util.Optional;

public interface SysUserService {

    Optional<LoginUser> findLoginUserByUsername(String username);

    LoginResponse login(LoginRequest request);

    LoginResponse.UserProfile getCurrentUserProfile();
}
