-- schema.sql
-- 用途：创建高校学科竞赛管理系统的全部核心表结构
-- 说明：该脚本应先于 init.sql 执行，适用于默认 H2 演示模式和 MySQL 模式

CREATE TABLE IF NOT EXISTS sys_college (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    college_code VARCHAR(50) NOT NULL,
    college_name VARCHAR(100) NOT NULL,
    sort INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE (college_code),
    UNIQUE (college_name)
);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    sort INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    remark VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE (role_code)
);

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    gender VARCHAR(10),
    user_type VARCHAR(30) NOT NULL,
    college_id BIGINT,
    status TINYINT NOT NULL DEFAULT 1,
    remark VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE (username),
    INDEX idx_sys_user_college_id (college_id),
    INDEX idx_sys_user_status (status)
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE (user_id, role_id),
    INDEX idx_user_role_user_id (user_id),
    INDEX idx_user_role_role_id (role_id)
);

CREATE TABLE IF NOT EXISTS comp_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL,
    category_code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    sort INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE (category_code)
);

CREATE TABLE IF NOT EXISTS competition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT,
    title VARCHAR(200) NOT NULL,
    level_name VARCHAR(50) NOT NULL,
    organizer VARCHAR(200) NOT NULL,
    registration_start DATETIME,
    registration_end DATETIME,
    competition_start DATETIME,
    competition_end DATETIME,
    team_mode VARCHAR(20) NOT NULL DEFAULT 'TEAM',
    max_team_size INT DEFAULT 1,
    description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_competition_category_id (category_id),
    INDEX idx_competition_status (status)
);

CREATE TABLE IF NOT EXISTS comp_team (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    competition_id BIGINT NOT NULL,
    team_name VARCHAR(100) NOT NULL,
    leader_user_id BIGINT NOT NULL,
    teacher_id BIGINT,
    team_status VARCHAR(30) NOT NULL DEFAULT 'FORMING',
    remark VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_comp_team_competition_id (competition_id),
    INDEX idx_comp_team_leader_user_id (leader_user_id)
);

CREATE TABLE IF NOT EXISTS comp_team_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    member_role VARCHAR(30) NOT NULL DEFAULT 'MEMBER',
    join_status VARCHAR(30) NOT NULL DEFAULT 'JOINED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE (team_id, user_id),
    INDEX idx_team_member_team_id (team_id),
    INDEX idx_team_member_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS teacher_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    teacher_no VARCHAR(50) NOT NULL,
    title_name VARCHAR(50),
    department VARCHAR(100),
    research_direction VARCHAR(255),
    phone VARCHAR(20),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE (user_id),
    UNIQUE (teacher_no)
);

CREATE TABLE IF NOT EXISTS competition_registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    competition_id BIGINT NOT NULL,
    user_id BIGINT,
    team_id BIGINT,
    registration_type VARCHAR(20) NOT NULL DEFAULT 'TEAM',
    audit_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    submit_time DATETIME,
    final_submit_time DATETIME,
    remark VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_registration_competition_id (competition_id),
    INDEX idx_registration_user_id (user_id),
    INDEX idx_registration_team_id (team_id),
    INDEX idx_registration_audit_status (audit_status)
);

CREATE TABLE IF NOT EXISTS audit_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_type VARCHAR(50) NOT NULL,
    business_id BIGINT NOT NULL,
    audit_stage VARCHAR(50) NOT NULL,
    audit_status VARCHAR(30) NOT NULL,
    auditor_id BIGINT NOT NULL,
    audit_opinion VARCHAR(500),
    audit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_audit_business (business_type, business_id),
    INDEX idx_audit_auditor_id (auditor_id)
);

CREATE TABLE IF NOT EXISTS award_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    competition_id BIGINT NOT NULL,
    registration_id BIGINT,
    award_name VARCHAR(100) NOT NULL,
    award_level VARCHAR(50),
    award_rank VARCHAR(50),
    award_time DATETIME,
    certificate_file_id BIGINT,
    audit_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    remark VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_award_competition_id (competition_id),
    INDEX idx_award_registration_id (registration_id),
    INDEX idx_award_audit_status (audit_status)
);

CREATE TABLE IF NOT EXISTS file_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_type VARCHAR(50) NOT NULL,
    business_id BIGINT NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    storage_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL DEFAULT 0,
    file_type VARCHAR(100),
    uploader_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_file_business (business_type, business_id),
    INDEX idx_file_uploader_id (uploader_id)
);

CREATE TABLE IF NOT EXISTS notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    publish_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    publisher_id BIGINT NOT NULL,
    publish_time DATETIME,
    top_flag TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_notice_publish_status (publish_status),
    INDEX idx_notice_publish_time (publish_time)
);
