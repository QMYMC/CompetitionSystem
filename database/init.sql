-- init.sql
-- 用途：初始化演示账号、竞赛、报名、审核、获奖、公告等默认数据
-- 说明：该脚本应在 schema.sql 执行完成后再导入，适合毕业设计演示和联调测试

INSERT INTO sys_college (id, college_code, college_name, sort, status, create_time, update_time, deleted) VALUES
(1, 'CS', 'Computer College', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'EE', 'Electronic Information College', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 'ME', 'Mechanical Engineering College', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 'BUS', 'Business College', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO sys_role (id, role_name, role_code, sort, status, remark, create_time, update_time, deleted) VALUES
(1, 'System Administrator', 'ADMIN', 1, 1, 'Global administrator role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'Teacher', 'TEACHER', 2, 1, 'Competition instructor role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 'Student', 'STUDENT', 3, 1, 'Student basic role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 'College Auditor', 'COLLEGE_AUDITOR', 4, 1, 'College audit role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO sys_user (id, username, password, real_name, email, phone, gender, user_type, college_id, status, remark, create_time, update_time, deleted) VALUES
(1, 'admin', '$2a$10$DqnDLKyd4kEsqj51OlI7yOu4ybQcFjlZdcK38tsyd9GMwgNprQsZO', 'System Admin', 'admin@competition.local', '13800000001', 'M', 'ADMIN', 1, 1, 'Default administrator account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'teacher01', '$2a$10$ZLLX2whsjlFNMZgL4nOMOOGCW83/ZdgNQKQShv6zucj0vbd.oi4lW', 'Teacher Zhang', 'teacher01@competition.local', '13800000002', 'F', 'TEACHER', 1, 1, 'Default teacher account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 'student01', '$2a$10$oLMn9t2WSh4JPXoJK9dEWeBYnCTgSRC.9Ot21zFAtppFa5umHEYcG', 'Student Li', 'student01@competition.local', '13800000003', 'M', 'STUDENT', 1, 1, 'Default student account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 'auditor01', '$2a$10$.Q6LHhRJsUMeG0vmy54VZeIGXvtjJnfQcWQOyJJ5WpNDHKPrw6LOe', 'Auditor Wang', 'auditor01@competition.local', '13800000004', 'F', 'COLLEGE_AUDITOR', 1, 1, 'Default college auditor account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, 'student02', '$2a$10$oLMn9t2WSh4JPXoJK9dEWeBYnCTgSRC.9Ot21zFAtppFa5umHEYcG', 'Student Zhao', 'student02@competition.local', '13800000005', 'F', 'STUDENT', 1, 1, 'Stage 5 team member demo account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, 'student03', '$2a$10$oLMn9t2WSh4JPXoJK9dEWeBYnCTgSRC.9Ot21zFAtppFa5umHEYcG', 'Student Sun', 'student03@competition.local', '13800000006', 'M', 'STUDENT', 2, 1, 'Stage 6 award demo account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(7, 'student04', '$2a$10$oLMn9t2WSh4JPXoJK9dEWeBYnCTgSRC.9Ot21zFAtppFa5umHEYcG', 'Student Chen', 'student04@competition.local', '13800000007', 'F', 'STUDENT', 2, 1, 'Stage 6 team demo member account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO sys_user_role (id, user_id, role_id, create_time, update_time, deleted) VALUES
(1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 4, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, 5, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, 6, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(7, 7, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO teacher_info (id, user_id, teacher_no, title_name, department, research_direction, phone, create_time, update_time, deleted) VALUES
(1, 2, 'T2026001', 'Associate Professor', 'Software Engineering Department', 'Competition Guidance', '13800000002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO comp_category (id, category_name, category_code, description, sort, status, create_time, update_time, deleted) VALUES
(1, 'Programming', 'PROGRAMMING', 'Algorithm and software competitions', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'Electronics', 'ELECTRONICS', 'Embedded and electronic design competitions', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 'Innovation', 'INNOVATION', 'Innovation and entrepreneurship competitions', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO competition (
    id, category_id, title, level_name, organizer, registration_start, registration_end,
    competition_start, competition_end, team_mode, max_team_size, description, status,
    create_time, update_time, deleted
) VALUES
(1, 1, 'National College Programming Contest', 'National', 'Ministry of Education',
 '2026-04-01 09:00:00', '2026-04-20 18:00:00',
 '2026-05-01 09:00:00', '2026-05-03 18:00:00',
 'TEAM', 3, 'Programming contest demo data for phase 4.', 'PUBLISHED',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 2, 'University Electronic Design Challenge', 'Provincial', 'Engineering Association',
 '2026-04-05 09:00:00', '2026-04-18 18:00:00',
 '2026-04-28 09:00:00', '2026-04-30 18:00:00',
 'INDIVIDUAL', 1, 'Electronic design competition demo data for phase 5 individual registration demo.', 'PUBLISHED',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO comp_team (id, competition_id, team_name, leader_user_id, teacher_id, team_status, remark, create_time, update_time, deleted) VALUES
(1, 1, 'Stage 6 Demo Team', 6, 1, 'COLLEGE_APPROVED', 'Preloaded approved team for award demo.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO comp_team_member (id, team_id, user_id, member_role, join_status, create_time, update_time, deleted) VALUES
(1, 1, 6, 'LEADER', 'JOINED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 1, 7, 'MEMBER', 'JOINED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO competition_registration (id, competition_id, user_id, team_id, registration_type, audit_status, submit_time, final_submit_time, remark, create_time, update_time, deleted) VALUES
(1, 2, 6, NULL, 'INDIVIDUAL', 'COLLEGE_APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Preloaded approved individual registration.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 1, NULL, 1, 'TEAM', 'COLLEGE_APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Preloaded approved team registration.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO audit_record (id, business_type, business_id, audit_stage, audit_status, auditor_id, audit_opinion, audit_time, create_time, update_time, deleted) VALUES
(1, 'REGISTRATION', 1, 'COLLEGE_REVIEW', 'APPROVED', 1, 'Preloaded approved individual registration.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'REGISTRATION', 2, 'COLLEGE_REVIEW', 'APPROVED', 1, 'Preloaded approved team registration.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 'AWARD', 1, 'COLLEGE_REVIEW', 'APPROVED', 1, 'Preloaded award approval for statistics demo.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO award_info (id, competition_id, registration_id, award_name, award_level, award_rank, award_time, certificate_file_id, audit_status, remark, create_time, update_time, deleted) VALUES
(1, 2, 1, 'Electronic Design First Prize', 'Provincial', 'First Prize', '2026-05-08 10:00:00', NULL, 'COLLEGE_APPROVED', 'Preloaded approved award for statistics demo.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO notice (id, title, content, publish_status, publisher_id, publish_time, top_flag, create_time, update_time, deleted) VALUES
(1, 'Stage 6 Module Ready', 'Award management, notice publishing, and statistics analysis are now available for demonstration.', 'PUBLISHED', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'Award Submission Reminder', 'Students with approved registrations can now submit award information for college review.', 'PUBLISHED', 1, CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 'Statistics Module Preview', 'This draft notice is prepared for the admin notice editing demonstration.', 'DRAFT', 1, NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
