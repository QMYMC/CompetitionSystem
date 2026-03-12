# 高校学科竞赛信息管理系统

当前仓库已完成：

- `阶段 1：初始化项目`
- `阶段 2：数据库与后端基础`

## 项目结构

```text
CompetitionSystem
├─ frontend    # Vue 3 + Vite + Element Plus 前端工程
├─ backend     # Spring Boot 3 + Security + JWT + MyBatis-Plus
└─ database    # schema.sql / init.sql
```

## 环境要求

- Node.js 24+
- npm 11+
- Java 17
- Maven 3.9+

## 默认端口

- 前端开发服务：`5173`
- 后端服务：`8080`

## 默认账号

阶段 1 未实现登录认证，因此当前没有内置业务账号和密码。

## 启动方式

### 1. 启动后端（默认演示模式）

```bash
cd backend
mvn spring-boot:run
```

启动后可访问：

- 健康检查：[http://localhost:8080/api/health](http://localhost:8080/api/health)
- 登录接口：[http://localhost:8080/api/auth/login](http://localhost:8080/api/auth/login)

默认演示模式使用内存数据库自动初始化，无需先安装 MySQL。

### 2. 启动后端（MySQL 8）

先在 MySQL 中创建数据库：

```sql
CREATE DATABASE competition_system DEFAULT CHARACTER SET utf8mb4;
```

再执行：

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

默认 MySQL 配置：

- Host: `localhost:3306`
- Database: `competition_system`
- Username: `root`
- Password: `123456`

SQL 文件位置：

- [schema.sql](E:/CompetitionSystem/database/schema.sql)
- [init.sql](E:/CompetitionSystem/database/init.sql)

MySQL 模式请先手动导入一次：

```bash
mysql -uroot -p123456 competition_system < ../database/schema.sql
mysql -uroot -p123456 competition_system < ../database/init.sql
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

启动后可访问：

- 前端首页：[http://localhost:5173/](http://localhost:5173/)

## 默认账号

- 管理员：`admin` / `Admin@123`
- 教师：`teacher01` / `Teacher@123`
- 学生：`student01` / `Student@123`
- 学院审核员：`auditor01` / `Auditor@123`

## 当前阶段已完成内容

- 创建 `frontend` 和 `backend` 两个子项目
- 初始化 Vue 3 + Vite 前端工程
- 初始化 Spring Boot 后端工程骨架
- 配置基础目录结构
- 补充项目级启动说明
- 设计数据库表并生成 `schema.sql` / `init.sql`
- 接入 MyBatis-Plus、Spring Security、JWT
- 实现基础登录接口和用户初始化数据

## 当前未开始内容

以下内容属于后续阶段，本次没有实现：

- 数据库表设计与 SQL
- 业务模块 CRUD
- 前端登录页、路由守卫、动态菜单
