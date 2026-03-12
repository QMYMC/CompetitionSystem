package com.competition.system.user.service;

import com.competition.system.common.model.PageResult;
import com.competition.system.user.dto.UserCreateRequest;
import com.competition.system.user.dto.UserQueryRequest;
import com.competition.system.user.dto.UserUpdateRequest;
import com.competition.system.user.vo.UserListItem;
import com.competition.system.user.vo.UserOptionsResponse;

public interface UserAdminService {

    PageResult<UserListItem> pageUsers(UserQueryRequest request);

    UserListItem getUserDetail(Long id);

    Long createUser(UserCreateRequest request);

    void updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    UserOptionsResponse getOptions();
}
