package com.competition.system.competition.constant;

public final class WorkflowConstants {

    private WorkflowConstants() {
    }

    public static final String REGISTRATION_TYPE_INDIVIDUAL = "INDIVIDUAL";
    public static final String REGISTRATION_TYPE_TEAM = "TEAM";

    public static final String REGISTRATION_STATUS_PENDING = "PENDING_COLLEGE_REVIEW";
    public static final String REGISTRATION_STATUS_APPROVED = "COLLEGE_APPROVED";
    public static final String REGISTRATION_STATUS_REJECTED = "COLLEGE_REJECTED";

    public static final String TEAM_STATUS_FORMING = "FORMING";
    public static final String TEAM_STATUS_PENDING = "PENDING_COLLEGE_REVIEW";
    public static final String TEAM_STATUS_APPROVED = "COLLEGE_APPROVED";
    public static final String TEAM_STATUS_REJECTED = "COLLEGE_REJECTED";

    public static final String MEMBER_ROLE_LEADER = "LEADER";
    public static final String MEMBER_ROLE_MEMBER = "MEMBER";
    public static final String JOIN_STATUS_JOINED = "JOINED";

    public static final String AUDIT_STAGE_COLLEGE = "COLLEGE_REVIEW";
    public static final String AUDIT_RESULT_APPROVED = "APPROVED";
    public static final String AUDIT_RESULT_REJECTED = "REJECTED";
    public static final String AUDIT_BUSINESS_REGISTRATION = "REGISTRATION";
}
