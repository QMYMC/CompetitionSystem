package com.competition.system.user.controller;

import com.competition.system.common.model.ApiResponse;
import com.competition.system.common.model.PageResult;
import com.competition.system.user.dto.UserCreateRequest;
import com.competition.system.user.dto.UserQueryRequest;
import com.competition.system.user.dto.UserUpdateRequest;
import com.competition.system.user.service.UserAdminService;
import com.competition.system.user.vo.UserListItem;
import com.competition.system.user.vo.UserOptionsResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserAdminService userAdminService;

    public UserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public ApiResponse<PageResult<UserListItem>> page(UserQueryRequest request) {
        return ApiResponse.success(userAdminService.pageUsers(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserListItem> detail(@PathVariable Long id) {
        return ApiResponse.success(userAdminService.getUserDetail(id));
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userAdminService.createUser(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        userAdminService.updateUser(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userAdminService.deleteUser(id);
        return ApiResponse.success();
    }

    @GetMapping("/options")
    public ApiResponse<UserOptionsResponse> options() {
        return ApiResponse.success(userAdminService.getOptions());
    }
}
