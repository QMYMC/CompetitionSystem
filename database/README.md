# 数据库脚本说明

本目录存放项目数据库初始化所需的两个核心脚本。

## 文件说明

### 1. schema.sql

用途：

- 创建系统所有核心表结构
- 适用于首次初始化数据库
- 适用于 H2 演示模式和 MySQL 模式

包含的主要表：

- 用户与权限：`sys_user`、`sys_role`、`sys_user_role`、`sys_college`
- 竞赛业务：`comp_category`、`competition`
- 报名与团队：`competition_registration`、`comp_team`、`comp_team_member`、`teacher_info`
- 审核与成果：`audit_record`、`award_info`
- 扩展信息：`file_info`、`notice`

### 2. init.sql

用途：

- 初始化默认角色与演示账号
- 初始化竞赛、团队、报名、审核、获奖、公告等演示数据
- 用于毕业设计答辩和联调测试

包含的主要内容：

- 学院、角色、系统用户
- 教师信息
- 竞赛分类与竞赛数据
- 已通过的个人赛报名与团队赛报名
- 已通过的获奖记录
- 已发布与草稿公告

## 执行顺序

无论是 MySQL 还是其他兼容环境，执行顺序都应为：

1. 先执行 `schema.sql`
2. 再执行 `init.sql`

## 推荐使用方式

### 默认演示模式

后端默认使用 H2 内存数据库，启动时会自动加载这两个脚本，最适合快速演示：

```bash
cd E:\CompetitionSystem\backend
mvn spring-boot:run
```

### MySQL 模式

```bash
mysql -uroot -p123456 competition_system < E:\CompetitionSystem\database\schema.sql
mysql -uroot -p123456 competition_system < E:\CompetitionSystem\database\init.sql
```
