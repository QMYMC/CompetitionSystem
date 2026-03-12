# 高校学科竞赛信息管理系统（Codex开发任务）

你是本项目的全栈开发助手。

你的任务是在当前目录中开发一个完整的：

**高校学科竞赛信息管理系统**

该系统用于高校管理学生竞赛报名、团队管理、审核和获奖信息。

请严格按照阶段执行，不要一次性生成全部内容。

---

# 一、技术架构

采用前后端分离架构。

前端：

- Vue 3
- Vite
- Element Plus
- Axios
- Pinia
- Vue Router
- ECharts

后端：

- Spring Boot 3.x
- Spring Security
- JWT
- MyBatis Plus
- Lombok
- Maven

数据库：

- MySQL 8
- 字符集 utf8mb4

---

# 二、项目结构

请创建以下目录结构：
CompetitionSystem
│
├─ frontend
│
├─ backend
│
├─ database
│
└─ docs

---

# 三、用户角色

系统需要支持四种角色：

### 1 系统管理员

权限：

- 用户管理
- 竞赛管理
- 公告管理
- 数据统计
- 审核管理

### 2 教师

权限：

- 指导学生团队
- 查看竞赛信息
- 查看团队成果

### 3 学生

权限：

- 注册登录
- 查看竞赛
- 报名竞赛
- 创建团队
- 上传材料
- 查看审核状态

### 4 学院审核员

权限：

- 审核报名
- 审核获奖
- 查看学院统计

---

# 四、核心模块

系统必须实现以下模块：

### 登录认证模块

功能：

- 登录
- JWT认证
- 权限控制
- 退出登录

---

### 用户管理模块

管理员功能：

- 用户列表
- 新建用户
- 编辑用户
- 删除用户
- 分配角色

---

### 竞赛管理模块

功能：

- 新建竞赛
- 编辑竞赛
- 删除竞赛
- 竞赛分类
- 竞赛级别
- 报名时间
- 竞赛说明

---

### 报名模块

学生功能：

- 报名竞赛
- 个人赛
- 团队赛

---

### 团队管理模块

功能：

- 创建团队
- 添加成员
- 删除成员
- 指导教师绑定

---

### 审核模块

功能：

- 院级审核
- 校级审核
- 审核意见
- 状态流转

---

### 获奖管理模块

功能：

- 填报获奖
- 上传证书
- 审核获奖

---

### 公告模块

功能：

- 发布公告
- 编辑公告
- 删除公告
- 查看公告

---

### 统计模块

功能：

- 竞赛数量统计
- 报名人数统计
- 获奖统计
- 学院统计

使用：

ECharts 图表展示

---

# 五、数据库设计

请设计数据库，并生成 SQL 文件。

至少包含以下表：
sys_user
sys_role
sys_user_role
college
competition
competition_registration
team
team_member
teacher
award
notice
audit_record
file_info

要求：

每个表包含：

- id
- create_time
- update_time
- deleted

输出文件：
database/schema.sql
database/init.sql

---

# 六、前端页面

### 公共页面

- 登录页
- 首页仪表盘
- 个人信息页

### 管理端页面

- 用户管理
- 竞赛管理
- 审核管理
- 公告管理
- 数据统计

### 学生端页面

- 竞赛列表
- 竞赛详情
- 我要报名
- 我的团队
- 我的获奖

---

# 七、接口设计

接口遵循 RESTful。

示例：

登录：

POST /api/auth/login

用户：

GET /api/users  
POST /api/users  
PUT /api/users/{id}  
DELETE /api/users/{id}

竞赛：

GET /api/competitions  
POST /api/competitions  
PUT /api/competitions/{id}  
DELETE /api/competitions/{id}

报名：

POST /api/registrations  
GET /api/registrations/my  

团队：

POST /api/teams  
GET /api/teams/{id}

公告：

GET /api/notices  
POST /api/notices  

统计：

GET /api/stats/overview

---

# 八、开发阶段

必须严格按阶段执行。

---

## 阶段1 初始化项目

任务：

创建目录：

frontend  
backend  
database  
docs  

初始化：

Vue3 + Vite 前端项目

Spring Boot 后端项目

输出：

- 项目目录结构
- 如何启动

---

## 阶段2 后端基础

实现：

- MyBatis Plus
- Spring Security
- JWT认证
- 登录接口
- 数据库连接

---

## 阶段3 前端基础

实现：

- 登录页
- 路由
- 权限控制
- Axios封装

---

## 阶段4 用户管理

实现：

用户CRUD

---

## 阶段5 竞赛管理

实现：

竞赛CRUD

---

## 阶段6 报名和团队

实现：

报名系统  
团队系统  

---

## 阶段7 审核和获奖

实现：

审核流程  
获奖管理  

---

## 阶段8 公告和统计

实现：

公告系统  
统计图表  

---

## 阶段9 项目收尾

生成：

README  
启动说明  
接口文档  

---

# 九、开发规则

必须遵守：

1 不要一次生成整个系统  
2 每次只执行当前阶段  
3 完成阶段后必须总结  

总结内容：

- 新增文件
- 修改文件
- 执行命令
- 如何运行
- 如何测试

---

# 十、立即开始

请先检查当前目录。

然后只执行：

**阶段1：初始化项目**

不要进入下一阶段。
