package com.competition.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CompetitionSystemApplication.class)
@AutoConfigureMockMvc
class CompetitionSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthShouldReturnUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    void loginShouldReturnTokenAndInfo() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/auth/info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.roles[0]").value("ADMIN"));
    }

    @Test
    void userCrudShouldWork() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token)
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(4))
                .andExpect(jsonPath("$.data.records").isArray());

        String createResponse = mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "stage4_user",
                                  "password": "Stage4@123",
                                  "realName": "阶段四测试用户",
                                  "roleId": 3,
                                  "collegeId": 1,
                                  "status": 1,
                                  "phone": "13800009999",
                                  "email": "stage4_user@test.local",
                                  "gender": "M",
                                  "remark": "stage4 test user"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(createResponse).path("data").asLong();

        mockMvc.perform(put("/users/{id}", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "realName": "阶段四测试用户已更新",
                                  "roleId": 2,
                                  "collegeId": 2,
                                  "status": 1,
                                  "phone": "13800008888",
                                  "email": "stage4_user_updated@test.local",
                                  "gender": "F",
                                  "remark": "updated"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/users/{id}", userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.realName").value("阶段四测试用户已更新"));

        mockMvc.perform(delete("/users/{id}", userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void competitionCrudShouldWork() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/competitions")
                        .header("Authorization", "Bearer " + token)
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records").isArray());

        String createResponse = mockMvc.perform(post("/competitions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "阶段四测试竞赛",
                                  "levelName": "校级",
                                  "organizer": "计算机学院",
                                  "registrationStart": "2026-06-01 09:00:00",
                                  "registrationEnd": "2026-06-15 18:00:00",
                                  "competitionStart": "2026-06-20 09:00:00",
                                  "competitionEnd": "2026-06-20 18:00:00",
                                  "teamMode": "TEAM",
                                  "maxTeamSize": 3,
                                  "description": "stage4 test competition",
                                  "status": "DRAFT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long competitionId = objectMapper.readTree(createResponse).path("data").asLong();

        mockMvc.perform(put("/competitions/{id}", competitionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 2,
                                  "title": "阶段四测试竞赛已更新",
                                  "levelName": "省级",
                                  "organizer": "电子信息学院",
                                  "registrationStart": "2026-06-02 09:00:00",
                                  "registrationEnd": "2026-06-16 18:00:00",
                                  "competitionStart": "2026-06-21 09:00:00",
                                  "competitionEnd": "2026-06-21 18:00:00",
                                  "teamMode": "INDIVIDUAL",
                                  "maxTeamSize": 1,
                                  "description": "updated",
                                  "status": "PUBLISHED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/competitions/{id}", competitionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("阶段四测试竞赛已更新"));

        mockMvc.perform(delete("/competitions/{id}", competitionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private String loginAsAdmin() throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "Admin@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.username").value("admin"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(response);
        return root.path("data").path("token").asText();
    }
}
