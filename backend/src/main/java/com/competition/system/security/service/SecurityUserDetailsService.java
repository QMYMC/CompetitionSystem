package com.competition.system.security.service;

import com.competition.system.common.enums.ResultCode;
import com.competition.system.common.exception.BusinessException;
import com.competition.system.security.LoginUser;
import com.competition.system.user.service.SysUserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final SysUserService sysUserService;

    public SecurityUserDetailsService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Override
    public LoginUser loadUserByUsername(String username) {
        return sysUserService.findLoginUserByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误"));
    }
}
