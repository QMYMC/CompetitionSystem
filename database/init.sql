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
(4, 'auditor01', '$2a$10$.Q6LHhRJsUMeG0vmy54VZeIGXvtjJnfQcWQOyJJ5WpNDHKPrw6LOe', 'Auditor Wang', 'auditor01@competition.local', '13800000004', 'F', 'COLLEGE_AUDITOR', 1, 1, 'Default college auditor account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO sys_user_role (id, user_id, role_id, create_time, update_time, deleted) VALUES
(1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 4, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO teacher_info (id, user_id, teacher_no, title_name, department, research_direction, phone, create_time, update_time, deleted) VALUES
(1, 2, 'T2026001', 'Associate Professor', 'Software Engineering Department', 'Competition Guidance', '13800000002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO comp_category (id, category_name, category_code, description, sort, status, create_time, update_time, deleted) VALUES
(1, 'Programming', 'PROGRAMMING', 'Algorithm and software competitions', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'Electronics', 'ELECTRONICS', 'Embedded and electronic design competitions', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 'Innovation', 'INNOVATION', 'Innovation and entrepreneurship competitions', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO notice (id, title, content, publish_status, publisher_id, publish_time, top_flag, create_time, update_time, deleted) VALUES
(1, 'Stage 2 Initialization Finished', 'Database schema, default users, and authentication foundation are ready for demo.', 'PUBLISHED', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
