package com.competition.system.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LoginResponse {

    private String token;
    private String tokenType;
    private LocalDateTime expireAt;
    private UserProfile user;

    @Data
    @Builder
    public static class UserProfile {
        private Long id;
        private String username;
        private String realName;
        private String userType;
        private Long collegeId;
        private String collegeName;
        private List<String> roles;
    }
}
