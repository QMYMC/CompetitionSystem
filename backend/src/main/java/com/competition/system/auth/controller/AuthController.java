package com.competition.system.auth.controller;

import com.competition.system.auth.dto.LoginRequest;
import com.competition.system.auth.vo.LoginResponse;
import com.competition.system.common.model.ApiResponse;
import com.competition.system.user.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysUserService sysUserService;

    public AuthController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(sysUserService.login(request));
    }

    @GetMapping("/info")
    public ApiResponse<LoginResponse.UserProfile> info() {
        return ApiResponse.success(sysUserService.getCurrentUserProfile());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }
}
