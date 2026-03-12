const ROLE_LABELS = {
  ADMIN: '系统管理员',
  TEACHER: '指导教师',
  STUDENT: '学生',
  COLLEGE_AUDITOR: '学院审核员',
  'System Administrator': '系统管理员',
  Teacher: '指导教师',
  Student: '学生',
  'College Auditor': '学院审核员',
}

const TEXT_REPLACEMENTS = [
  ['National College Programming Contest', '全国大学生程序设计竞赛'],
  ['University Electronic Design Challenge', '全国大学生电子设计竞赛'],
  ['Electronic Design First Prize', '电子设计竞赛一等奖'],
  ['Stage 6 Demo Team', '电子设计参赛队'],
  ['Electronic Information College', '电子信息学院'],
  ['Mechanical Engineering College', '机械工程学院'],
  ['Computer College', '计算机学院'],
  ['Business College', '商学院'],
  ['System Administrator', '系统管理员'],
  ['System Admin', '系统管理员'],
  ['College Auditor', '学院审核员'],
  ['Teacher Zhang', '张老师'],
  ['Student Zhao', '赵同学'],
  ['Student Sun', '孙同学'],
  ['Student Chen', '陈同学'],
  ['Student Li', '李同学'],
  ['Auditor Wang', '王老师'],
  ['Associate Professor', '副教授'],
  ['Software Engineering Department', '软件工程系'],
  ['Competition Guidance', '学科竞赛指导'],
  ['Engineering Association', '工程技术协会'],
  ['Ministry of Education', '教育部'],
  ['Programming', '程序设计类'],
  ['Electronics', '电子信息类'],
  ['Innovation', '创新创业类'],
  ['Algorithm and software competitions', '算法与软件类竞赛'],
  ['Embedded and electronic design competitions', '嵌入式与电子设计类竞赛'],
  ['Innovation and entrepreneurship competitions', '创新创业类竞赛'],
  ['Award Submission Reminder', '获奖填报提醒'],
  ['Statistics Module Preview', '统计分析公告草稿'],
  ['Stage 6 Module Ready', '系统功能已就绪'],
  ['Students with approved registrations can now submit award information for college review.', '已通过报名审核的学生现可提交获奖信息，进入院级审核流程。'],
  ['Award management, notice publishing, and statistics analysis are now available for demonstration.', '获奖管理、公告发布和统计分析功能现已开放使用。'],
  ['This draft notice is prepared for the admin notice editing demonstration.', '本公告草稿用于公告编辑功能预览。'],
  ['Preloaded approved individual registration.', '已预置通过审核的个人报名记录。'],
  ['Preloaded approved team registration.', '已预置通过审核的团队报名记录。'],
  ['Preloaded award approval for statistics demo.', '已预置通过审核的获奖记录。'],
  ['Preloaded approved award for statistics demo.', '已预置通过审核的获奖信息。'],
  ['Preloaded approved team for award demo.', '已预置通过审核的参赛团队。'],
  ['Programming contest demo data for phase 4.', '程序设计竞赛基础数据。'],
  ['Electronic design competition demo data for phase 5 individual registration demo.', '电子设计竞赛个人报名基础数据。'],
  ['Global administrator role', '系统管理角色'],
  ['Competition instructor role', '竞赛指导教师角色'],
  ['Student basic role', '学生基础角色'],
  ['College audit role', '学院审核角色'],
  ['Default administrator account', '默认管理员账号'],
  ['Default teacher account', '默认教师账号'],
  ['Default student account', '默认学生账号'],
  ['Stage 5 team member demo account', '团队成员账号'],
  ['Stage 6 award demo account', '获奖填报账号'],
  ['Stage 6 team demo member account', '团队成员账号'],
  ['PENDING_COLLEGE_REVIEW', '待院级审核'],
  ['COLLEGE_APPROVED', '院级通过'],
  ['COLLEGE_REJECTED', '院级驳回'],
  ['INDIVIDUAL', '个人赛'],
  ['TEAM', '团队赛'],
  ['FORMING', '组队中'],
  ['First Prize', '一等奖'],
  ['Provincial', '省级'],
  ['National', '国家级'],
]

export function formatRoleLabel(value) {
  if (!value) {
    return '--'
  }
  return ROLE_LABELS[value] || value
}

export function formatRoleList(roles = []) {
  if (!roles.length) {
    return '暂无角色'
  }
  return roles.map((role) => formatRoleLabel(role)).join(' / ')
}

export function formatUserTypeLabel(value) {
  return formatRoleLabel(value)
}

export function formatDisplayText(value) {
  if (value === null || value === undefined || value === '') {
    return value
  }

  return TEXT_REPLACEMENTS.reduce((text, [source, target]) => {
    return text.replaceAll(source, target)
  }, String(value))
}

export function formatOptionList(options = []) {
  return options.map((item) => ({
    ...item,
    label: formatDisplayText(item.label),
    extra: formatDisplayText(item.extra),
  }))
}
