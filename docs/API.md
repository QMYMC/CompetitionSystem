# 主要接口文档

本文档整理系统当前版本的主要接口，按模块划分，便于演示、联调和答辩说明。

## 1. 登录认证

### `POST /api/auth/login`

登录接口，返回 JWT Token 和当前用户信息。

### `GET /api/auth/info`

获取当前登录用户信息，用于前端菜单过滤、页面展示和路由守卫。

### `POST /api/auth/logout`

退出登录接口。

## 2. 用户管理

仅管理员可访问。

### `GET /api/users`

分页查询用户列表。

### `GET /api/users/{id}`

查询用户详情。

### `POST /api/users`

新增用户。

### `PUT /api/users/{id}`

更新用户。

### `DELETE /api/users/{id}`

删除用户。

### `GET /api/users/options`

获取角色、学院等下拉选项。

## 3. 竞赛管理

仅管理员可访问。

### `GET /api/competitions`

分页查询竞赛列表。

### `GET /api/competitions/{id}`

查询竞赛详情。

### `POST /api/competitions`

新增竞赛。

### `PUT /api/competitions/{id}`

更新竞赛。

### `DELETE /api/competitions/{id}`

删除竞赛。

### `GET /api/competitions/options`

获取竞赛分类、状态、赛制等下拉选项。

## 4. 学生报名与团队

仅学生角色可访问。

### `GET /api/student/workflow/competitions`

查询学生可报名竞赛列表。

### `GET /api/student/workflow/registrations`

查询当前学生的报名记录。

### `POST /api/student/workflow/registrations/individual`

提交个人赛报名。

### `GET /api/student/workflow/teams`

查询当前学生的团队列表。

### `GET /api/student/workflow/teams/{teamId}`

查询团队详情。

### `POST /api/student/workflow/teams`

创建团队。

### `PUT /api/student/workflow/teams/{teamId}`

更新团队基础信息。

### `POST /api/student/workflow/teams/{teamId}/members`

添加团队成员。

### `DELETE /api/student/workflow/teams/{teamId}/members/{userId}`

移除团队成员。

### `POST /api/student/workflow/teams/{teamId}/submit`

提交团队报名进入院级审核。

### `GET /api/student/workflow/team-options`

获取创建团队时所需的竞赛、教师、学生选项。

## 5. 报名审核

管理员和学院审核员可访问。

### `GET /api/reviews/registrations`

分页查询报名审核列表。

### `POST /api/reviews/registrations/{registrationId}/approve`

院级审核通过。

### `POST /api/reviews/registrations/{registrationId}/reject`

院级审核驳回。

## 6. 获奖填报与审核

### 学生端

#### `GET /api/student/awards/options`

获取可用于获奖填报的已通过报名记录。

#### `GET /api/student/awards`

查询当前学生的获奖填报记录。

#### `POST /api/student/awards`

新增获奖填报。

#### `PUT /api/student/awards/{awardId}`

更新获奖填报信息。

### 审核端

#### `GET /api/awards/reviews`

分页查询获奖审核列表。

#### `POST /api/awards/reviews/{awardId}/approve`

院级审核通过。

#### `POST /api/awards/reviews/{awardId}/reject`

院级审核驳回。

## 7. 公告管理

### `GET /api/notices`

分页查询公告列表。

### `GET /api/notices/{noticeId}`

查询公告详情。

### `POST /api/notices`

新增公告，仅管理员可访问。

### `PUT /api/notices/{noticeId}`

更新公告，仅管理员可访问。

### `DELETE /api/notices/{noticeId}`

删除公告，仅管理员可访问。

## 8. 统计分析

### `GET /api/statistics/overview`

获取统计总览数据，包含：

- 竞赛数量
- 报名数量
- 获奖数量
- 已发布公告数量
- 学院维度统计
- 报名类型分布
- 获奖审核状态分布

## 9. 说明

- 所有业务接口默认使用统一返回结构。
- 受保护接口需要在请求头中携带 `Authorization: Bearer {token}`。
- 前端默认通过 Axios 请求封装自动携带 Token。
