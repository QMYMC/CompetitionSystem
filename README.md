# 高校学科竞赛信息管理系统

当前仓库已完成 `阶段 1：初始化项目`，包含可独立启动的前端和后端基础工程。

## 项目结构

```text
CompetitionSystem
├─ frontend    # Vue 3 + Vite + Element Plus 前端工程
└─ backend     # Spring Boot 3 后端工程
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

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

启动后可访问：

- 健康检查：[http://localhost:8080/api/health](http://localhost:8080/api/health)

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

启动后可访问：

- 前端首页：[http://localhost:5173/](http://localhost:5173/)

## 当前阶段已完成内容

- 创建 `frontend` 和 `backend` 两个子项目
- 初始化 Vue 3 + Vite 前端工程
- 初始化 Spring Boot 后端工程骨架
- 配置基础目录结构
- 补充项目级启动说明

## 当前未开始内容

以下内容属于后续阶段，本次没有实现：

- 数据库表设计与 SQL
- Spring Security + JWT
- MyBatis-Plus 集成
- 业务模块 CRUD
- 登录鉴权与动态菜单
