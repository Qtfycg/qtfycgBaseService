# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

- 个人后端基础服务：Spring Boot 4.1.0 + Java 21 + MyBatis-Plus 3.5.17（`mybatis-plus-spring-boot4-starter`）+ MySQL + Lombok，多模块 Maven 工程
- Maven 根目录是 `qtfycg/`（仓库根目录没有 pom.xml），Maven 命令需 `-f qtfycg/pom.xml` 或在 `qtfycg/` 目录下执行
- 分支模型（README 规定）：日常开发提交 `dev`，稳定后 PR 合并到 `main`，禁止直接向 `main` 提交
- 提交信息：`类型: :gitmoji: 中文描述`，如 `feat: :art: 全局异常处理初始化`；类型为 feat / fix / docs / style / refactor / test / chore
- 注意：README.md 中「多项目归档目录结构」（order-axis 等）与当前实际代码不符，以 `qtfycg/` 单工程为准；README 的分支规则与提交规范仍有效

## 模块结构

- `bootstrap` — 启动模块：`bootstrapApplication` 入口；`application.yml`（`spring.profiles.active: dev`）、`application-dev.yml`（本地 MySQL `localhost:13306/qtfycg`，端口 8080，context-path `/api`）、`application-prod.yml`（空占位）。业务模块在此聚合依赖（当前仅依赖 `common`，接入新模块时需在 `bootstrap/pom.xml` 追加依赖）
- `common` — 基础库：统一响应 `Result<T>`、`ErrorCode` 接口、`GlobalErrorCode` 枚举、`BusinessException`
- `framework` — 全局配置：MyBatis-Plus 拦截器（MySQL 分页 / 乐观锁 / 防全表 update、delete）、`GlobalExceptionHandler`（@RestControllerAdvice）
- `auth` / `file` / `message` / `monitor` — 空占位模块（仅有 pom）
- `user` — 第一个业务模块（开发中），分层：`dto` / `mapper`（XML 在 `src/main/resources/mapper/`）/ `service` + `service/impl`

## 核心约定

### 统一响应与异常

- 所有接口返回 `com.qtfycg.common.result.Result<T>`：`Result.ok(data)`、`Result.fail(errorCode[, message])`、`Result.created(data)`；字段为 success / status / code / message / data / timestamp / traceId
- 错误码实现 `ErrorCode` 接口（status + code + message）。全局码在 `GlobalErrorCode`；业务模块新增错误码时在模块内定义自己的枚举并实现 `ErrorCode`
- 业务失败通过抛 `BusinessException` 处理（默认 `BUSINESS_ERROR`，可传自定义 ErrorCode 或消息），由全局处理器统一转成 `Result`，不要在 controller 里手工拼失败响应
- `framework` 的 `GlobalExceptionHandler` 覆盖：业务异常、参数校验（MethodArgumentNotValid / Bind / ConstraintViolation / 缺参 / 类型不匹配）、HTTP 层（方法不支持 / Content-Type 不支持 / 404）、Spring Security 认证与授权异常、兜底 `SYSTEM_ERROR`；响应 `traceId` 取自 MDC 的 `traceId` key

### MyBatis-Plus

- Service 继承 `ServiceImpl<Mapper, DTO>`（IService 模式），见 `user` 模块
- 分页 / 乐观锁 / BlockAttack 拦截器已在 `framework` 全局注册，业务模块不要重复配置
- 乐观锁依赖实体 `version` 字段（见 `UserAccountMapper.xml` 的列约定）

### 代码风格

- 新文件加版权头：`Copyright (c) 2026 qtfycg All rights reserved`（全库统一添加过）
- 既有类名小写开头（`bootstrapApplication`、`mybatisPlusConfig`），新增类跟随现状
- 代码注释用中文

## 常用命令

```bash
# 构建（仓库根目录执行）
mvn -f qtfycg/pom.xml clean package

# 运行全部测试
mvn -f qtfycg/pom.xml test

# 单个测试（模块依赖未安装到本地仓库时加 -am）
mvn -f qtfycg/pom.xml -pl framework -Dtest=GlobalExceptionHandlerTest test

# 启动服务（http://localhost:8080/api，需本地 MySQL localhost:13306）
mvn -f qtfycg/pom.xml -pl bootstrap -am spring-boot:run
```

测试为 JUnit 5 + AssertJ 纯单元测试（直接 new 被测类、不启动 Spring 上下文），见 `framework/src/test/java/com/qtfycg/framework/exception/GlobalExceptionHandlerTest.java`。
