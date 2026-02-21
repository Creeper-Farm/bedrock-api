# Bedrock API

一个基于 Kotlin + Spring Boot 的多模块后端项目，提供用户注册、登录鉴权（JWT + Redis）、用户资料管理等基础能力。

## 特性

- 多模块架构，按职责拆分 `system / auth / user / common`
- JWT 双令牌机制（`accessToken` + `refreshToken`）
- Redis 会话状态校验与失效控制
- 用户账号软删除、资料更新、最近登录时间同步
- 统一返回结构与全局异常处理
- 支持 Spring Boot Admin（开发环境）

## 技术栈

- Java 21
- Kotlin 2.2.x
- Spring Boot 3.4.x
- PostgreSQL 16
- Redis 7
- Gradle (Kotlin DSL)
- Exposed DSL

## 项目结构

| 模块 | 说明 |
| --- | --- |
| `bedrock-system` | 启动模块、环境配置、监控与系统级配置 |
| `bedrock-auth` | 登录/刷新/登出、JWT 校验、鉴权拦截 |
| `bedrock-user` | 用户注册、查询、资料更新、软删除 |
| `bedrock-common` | 通用模型、异常处理、公共配置 |

## 环境要求

- JDK 21
- Docker（推荐，用于本地 PostgreSQL/Redis）
- 可用端口：`8080`、`5432`、`6379`

## 快速开始

1. 启动依赖服务

```bash
docker compose -f docker.compose.yml up -d
```

2. 启动项目

```bash
./gradlew :bedrock-system:bootRun
```

3. 运行测试

```bash
./gradlew test
```

## 配置说明

主要配置位于：

- `bedrock-system/src/main/resources/application.yaml`
- `bedrock-system/src/main/resources/application-dev.yml`
- `bedrock-system/src/main/resources/application-prod.yml`

默认激活 `dev` 环境，可通过参数切换：

```bash
./gradlew :bedrock-system:bootRun --args='--spring.profiles.active=prod'
```

关键环境变量（生产建议必配）：

- `JWT_SECRET`
- `PROD_DB_PASSWORD`

## 主要接口

### 认证

- `POST /api/auth/login` 登录
- `POST /api/auth/refresh` 刷新令牌
- `POST /api/auth/logout` 登出

### 用户

- `POST /api/user/register` 注册
- `GET /api/user/{userId}` 查询用户信息
- `PUT /api/user/profile` 更新当前用户资料（需登录）
- `DELETE /api/user/account` 软删除当前账号（需登录）

鉴权接口需携带：

```http
Authorization: Bearer <accessToken>
```

## 开发建议

- 新增业务能力优先落在对应模块，避免把业务堆到 `bedrock-system`
- 对外接口统一返回 `Result<T>`，异常交给全局异常处理器
- 提交前至少执行：

```bash
./gradlew :bedrock-system:compileKotlin
```

## 贡献

欢迎提交 Issue 和 PR。建议流程：

1. 从 `dev` 分支创建功能分支
2. 完成功能与自测
3. 提交 PR 到 `main`

## License

本项目采用 `MIT` 许可证，详见 `LICENSE`。
