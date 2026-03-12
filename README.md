# 高校学科竞赛管理系统（毕业设计演示版）

高校学科竞赛管理系统是一个面向毕业设计展示的全栈项目，围绕“竞赛管理、学生报名、团队组建、院级审核、获奖填报、公告发布、统计分析”构建完整业务闭环。项目强调可运行、可演示、结构清晰，适合课程设计、毕业答辩和系统展示场景。

## 项目简介

本系统覆盖以下核心业务：

- 管理员维护用户、竞赛、公告等基础数据
- 学生完成个人赛报名、团队赛组队与成员管理
- 管理端完成院级报名审核与获奖审核
- 学生提交获奖信息并查看审核结果
- 首页和统计页展示关键指标与图表

当前版本已经完成毕业设计演示所需的主要流程，不再继续扩展大型业务模块。

## 技术栈

### 前端

- Vue 3
- Vite
- Vue Router
- Pinia
- Axios
- Element Plus
- ECharts

### 后端

- Spring Boot 3
- Spring Security
- JWT
- MyBatis-Plus
- H2（默认演示模式）
- MySQL 8（可切换部署）

## 目录结构

```text
CompetitionSystem
├─ frontend                        # Vue 3 前端工程
│  ├─ src/api                      # 接口请求封装
│  ├─ src/router                   # 路由与菜单配置
│  ├─ src/stores                   # Pinia 状态管理
│  ├─ src/views                    # 页面视图
│  └─ src/layouts                  # 后台布局
├─ backend                         # Spring Boot 后端工程
│  ├─ src/main/java/com/competition/system
│  │  ├─ auth                      # 登录认证
│  │  ├─ common                    # 统一返回、异常处理、通用模型
│  │  ├─ config                    # 安全、MyBatis-Plus、CORS 等配置
│  │  ├─ competition               # 竞赛、报名、审核、获奖、公告、统计
│  │  └─ user                      # 用户管理
│  └─ src/main/resources           # application 配置
├─ database
│  ├─ schema.sql                   # 建表脚本
│  ├─ init.sql                     # 演示初始化数据
│  └─ README.md                    # 数据库脚本说明
└─ docs
   └─ API.md                       # 主要接口文档
```

## 环境要求

- Node.js 24+
- npm 11+
- Java 17
- Maven 3.9+

## 默认端口

- 前端开发服务：`5173`
- 后端服务：`8080`

## 启动方式

### 1. 启动后端默认演示模式

默认使用 H2 内存数据库，启动时自动执行 `schema.sql` 和 `init.sql`，适合直接演示，无需预先安装 MySQL。

```bash
cd E:\CompetitionSystem\backend
mvn spring-boot:run
```

启动后可访问：

- 健康检查：[http://localhost:8080/api/health](http://localhost:8080/api/health)
- 登录接口：[http://localhost:8080/api/auth/login](http://localhost:8080/api/auth/login)

### 2. 启动后端 MySQL 模式

先创建数据库：

```sql
CREATE DATABASE competition_system DEFAULT CHARACTER SET utf8mb4;
```

然后按顺序导入脚本：

```bash
mysql -uroot -p123456 competition_system < E:\CompetitionSystem\database\schema.sql
mysql -uroot -p123456 competition_system < E:\CompetitionSystem\database\init.sql
```

再启动后端：

```bash
cd E:\CompetitionSystem\backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

默认 MySQL 配置：

- Host：`localhost:3306`
- Database：`competition_system`
- Username：`root`
- Password：`123456`

### 3. 启动前端

```bash
cd E:\CompetitionSystem\frontend
npm install
npm run dev
```

访问地址：

- 前端系统：[http://localhost:5173](http://localhost:5173)

## 默认账号

- 管理员：`admin / Admin@123`
- 教师：`teacher01 / Teacher@123`
- 学生：`student01 / Student@123`
- 学院审核员：`auditor01 / Auditor@123`
- 阶段 6 学生演示账号：`student03 / Student@123`
- 阶段 6 团队成员账号：`student04 / Student@123`

## 数据库脚本说明

- [schema.sql](E:/CompetitionSystem/database/schema.sql)
  - 用于创建系统所有核心表结构
  - 适合首次初始化数据库或重新搭建演示环境
- [init.sql](E:/CompetitionSystem/database/init.sql)
  - 用于写入默认角色、用户、竞赛、团队、报名、获奖、公告等演示数据
  - 适合毕业设计演示和联调测试

更详细说明见：[database/README.md](E:/CompetitionSystem/database/README.md)

## 主要功能模块

### 1. 登录与权限

- 登录认证
- JWT 鉴权
- 路由守卫
- 按角色动态菜单过滤

### 2. 用户管理

- 用户分页查询
- 用户新增、编辑、删除
- 角色与学院下拉选项

### 3. 竞赛管理

- 竞赛分页查询
- 竞赛新增、编辑、删除
- 分类、状态、赛制维护

### 4. 报名与团队

- 学生个人赛报名
- 团队创建、成员管理、指导教师关联
- 院级报名审核

### 5. 获奖与公告

- 学生获奖填报
- 管理端院级获奖审核
- 公告发布、编辑、删除、详情展示

### 6. 统计分析

- 竞赛数量统计
- 报名数量统计
- 获奖数量统计
- 学院维度统计
- ECharts 图表展示

## 接口文档

项目主要接口已整理到：

- [docs/API.md](E:/CompetitionSystem/docs/API.md)

接口文档按模块划分，包括：

- 登录认证
- 用户管理
- 竞赛管理
- 学生报名与团队
- 报名审核
- 获奖填报与审核
- 公告管理
- 统计分析

## 推荐演示流程

### 演示顺序建议

1. 使用 `admin` 登录，展示系统首页、菜单、用户管理、竞赛管理。
2. 切换 `student01`，演示个人赛报名或团队创建、成员管理。
3. 切换 `admin` 或 `auditor01`，演示报名审核通过/驳回。
4. 使用 `student03`，演示获奖填报。
5. 切换 `admin`，演示获奖审核。
6. 继续使用 `admin`，演示公告中心的发布、编辑、删除与详情查看。
7. 最后展示统计分析页，用图表收尾。

### 阶段 6 快速演示

1. `student03 / Student@123` 登录。
2. 进入“获奖填报”，基于已通过的报名记录新增获奖信息。
3. 退出后 `admin / Admin@123` 登录。
4. 进入“获奖审核”，对学生提交的获奖信息执行通过或驳回。
5. 进入“公告中心”，发布或编辑一条公告。
6. 进入“统计分析”，展示关键指标和学院维度图表。

## 测试与验证

### 后端测试

```bash
cd E:\CompetitionSystem\backend
mvn test
```

### 前端构建验证

```bash
cd E:\CompetitionSystem\frontend
npm run build
```

## 最终交付内容

- 可运行的前后端完整项目
- 数据库建表脚本与初始化脚本
- 演示账号与预置演示数据
- 主要接口文档
- README 使用说明
- 适合毕业设计展示的页面与流程
