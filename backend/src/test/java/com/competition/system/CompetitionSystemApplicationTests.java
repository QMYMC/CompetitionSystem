package com.competition.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
        String token = login("admin", "Admin@123");

        mockMvc.perform(get("/auth/info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.roles[0]").value("ADMIN"));
    }

    @Test
    void userCrudShouldWork() throws Exception {
        String token = login("admin", "Admin@123");

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token)
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(7))
                .andExpect(jsonPath("$.data.records").isArray());

        String createResponse = mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "stage6_user",
                                  "password": "Stage6@123",
                                  "realName": "阶段六测试用户",
                                  "roleId": 3,
                                  "collegeId": 1,
                                  "status": 1,
                                  "phone": "13800009999",
                                  "email": "stage6_user@test.local",
                                  "gender": "M",
                                  "remark": "stage6 test user"
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
                                  "realName": "阶段六测试用户已更新",
                                  "roleId": 2,
                                  "collegeId": 2,
                                  "status": 1,
                                  "phone": "13800008888",
                                  "email": "stage6_user_updated@test.local",
                                  "gender": "F",
                                  "remark": "updated"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/users/{id}", userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.realName").value("阶段六测试用户已更新"));

        mockMvc.perform(delete("/users/{id}", userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void competitionCrudShouldWork() throws Exception {
        String token = login("admin", "Admin@123");

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
                                  "title": "阶段六测试竞赛",
                                  "levelName": "校级",
                                  "organizer": "计算机学院",
                                  "registrationStart": "2026-06-01 09:00:00",
                                  "registrationEnd": "2026-06-15 18:00:00",
                                  "competitionStart": "2026-06-20 09:00:00",
                                  "competitionEnd": "2026-06-20 18:00:00",
                                  "teamMode": "TEAM",
                                  "maxTeamSize": 3,
                                  "description": "stage6 test competition",
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
                                  "title": "阶段六测试竞赛已更新",
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
                .andExpect(jsonPath("$.data.title").value("阶段六测试竞赛已更新"));

        mockMvc.perform(delete("/competitions/{id}", competitionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void studentWorkflowAndCollegeReviewShouldWork() throws Exception {
        String studentToken = login("student01", "Student@123");
        String adminToken = login("admin", "Admin@123");

        String competitionResponse = mockMvc.perform(get("/student/workflow/competitions")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode competitionNodes = objectMapper.readTree(competitionResponse).path("data");
        boolean hasTeamCompetition = false;
        boolean hasIndividualCompetition = false;
        for (JsonNode competitionNode : competitionNodes) {
            String teamMode = competitionNode.path("teamMode").asText();
            hasTeamCompetition = hasTeamCompetition || "TEAM".equals(teamMode);
            hasIndividualCompetition = hasIndividualCompetition || "INDIVIDUAL".equals(teamMode);
        }
        Assertions.assertTrue(hasTeamCompetition);
        Assertions.assertTrue(hasIndividualCompetition);

        String individualResponse = mockMvc.perform(post("/student/workflow/registrations/individual")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "competitionId": 2,
                                  "remark": "个人赛报名测试"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long individualRegistrationId = objectMapper.readTree(individualResponse).path("data").asLong();

        String teamResponse = mockMvc.perform(post("/student/workflow/teams")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "competitionId": 1,
                                  "teamName": "阶段六演示团队",
                                  "teacherId": 1,
                                  "memberUserIds": [5],
                                  "remark": "团队赛报名测试"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long teamId = objectMapper.readTree(teamResponse).path("data").asLong();

        mockMvc.perform(post("/student/workflow/teams/{teamId}/submit", teamId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "remark": "提交院级审核"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        String reviewPage = mockMvc.perform(get("/reviews/registrations")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("current", "1")
                        .param("size", "10")
                        .param("auditStatus", "PENDING_COLLEGE_REVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode reviewRecords = objectMapper.readTree(reviewPage).path("data").path("records");
        long teamRegistrationId = 0L;
        for (JsonNode record : reviewRecords) {
            if (record.path("teamId").asLong() == teamId) {
                teamRegistrationId = record.path("id").asLong();
                break;
            }
        }

        mockMvc.perform(post("/reviews/registrations/{id}/approve", individualRegistrationId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "opinion": "材料完整，同意通过"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/reviews/registrations/{id}/reject", teamRegistrationId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "opinion": "团队说明需要补充后再次提交"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/student/workflow/teams/{teamId}", teamId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teamStatus").value("COLLEGE_REJECTED"));
    }

    @Test
    void awardNoticeAndStatisticsShouldWork() throws Exception {
        String studentToken = login("student03", "Student@123");
        String adminToken = login("admin", "Admin@123");

        String optionsResponse = mockMvc.perform(get("/student/awards/options")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode optionNodes = objectMapper.readTree(optionsResponse).path("data");
        long registrationId = optionNodes.get(0).path("registrationId").asLong();

        String awardCreateResponse = mockMvc.perform(post("/student/awards")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "registrationId": %d,
                                  "awardName": "阶段六测试获奖",
                                  "awardLevel": "省级",
                                  "awardRank": "一等奖",
                                  "awardTime": "2026-06-30 10:00:00",
                                  "remark": "阶段六获奖填报测试"
                                }
                                """.formatted(registrationId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long awardId = objectMapper.readTree(awardCreateResponse).path("data").asLong();

        mockMvc.perform(get("/student/awards")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists());

        mockMvc.perform(post("/awards/reviews/{awardId}/approve", awardId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "opinion": "获奖材料真实完整，同意通过"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        String noticeCreateResponse = mockMvc.perform(post("/notices")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "阶段六公告测试",
                                  "content": "用于验证公告发布、详情查看和编辑删除流程。",
                                  "publishStatus": "PUBLISHED",
                                  "topFlag": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long noticeId = objectMapper.readTree(noticeCreateResponse).path("data").asLong();

        mockMvc.perform(put("/notices/{noticeId}", noticeId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "阶段六公告测试已更新",
                                  "content": "公告内容已更新。",
                                  "publishStatus": "PUBLISHED",
                                  "topFlag": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/notices/{noticeId}", noticeId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("阶段六公告测试已更新"));

        mockMvc.perform(get("/statistics/overview")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.competitionCount").value(2))
                .andExpect(jsonPath("$.data.registrationCount").isNumber())
                .andExpect(jsonPath("$.data.awardCount").isNumber())
                .andExpect(jsonPath("$.data.collegeStats").isArray());

        mockMvc.perform(delete("/notices/{noticeId}", noticeId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private String login(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(response);
        return root.path("data").path("token").asText();
    }
}
