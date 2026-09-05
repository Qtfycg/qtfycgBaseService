# qtfycgBaseService Agent Guide

## 适用范围与原则

- 本文件适用于整个仓库。更深层目录若存在自己的 Agent 指南，以更具体的指南为准。
- 以实际源码和 `pom.xml` 为事实来源。根目录 `README.md` 仍保留早期“多项目归档”描述，与当前仓库结构不完全一致。
- 修改前先执行 `git status --short`。仓库可能存在用户尚未提交的改动；只修改任务要求涉及的文件，不覆盖、回滚或顺手格式化无关内容。
- 优先完成最小且完整的改动。不要在修复局部问题时擅自重命名模块、移动包、升级依赖或重构公共 API。

## 项目概览

- Java 21、Spring Boot 4.1.0、Maven 多模块工程。
- MyBatis-Plus 3.5.17，使用 `mybatis-plus-spring-boot4-starter` 和 MySQL 方言插件。
- Maven 聚合根目录是 `qtfycg/`，仓库根目录没有 `pom.xml`。从仓库根目录执行 Maven 时必须使用 `-f qtfycg/pom.xml`。
- Maven 坐标为 `com.qtfycg:qtfycg:1.0.0`。
- 默认开发配置启动在 `http://localhost:8080/api`，并依赖本地 MySQL。不要假设数据库或其他外部服务已经运行。

## 模块边界

| 模块 | 当前职责 |
| --- | --- |
| `bootstrap` | Spring Boot 启动入口与运行时配置 |
| `common` | 统一响应、错误码和业务异常等跨模块基础类型 |
| `framework` | 全局异常处理和 MyBatis-Plus 全局配置 |
| `user` | 用户业务模块，当前仍在开发中 |
| `auth` | 认证模块占位，目前只有 POM |
| `file` | 文件模块占位，目前只有 POM |
| `message` | 消息模块占位，目前只有 POM |
| `monitor` | 监控模块占位，目前只有 POM |

新增跨模块引用时，在消费方 POM 中显式声明依赖，避免依赖偶然的传递依赖。业务模块要真正随应用启动，还必须由 `bootstrap` 声明该模块依赖，并保证 Spring 能扫描到 `com.qtfycg` 下对应组件。当前启动类位于 `com.qtfycg.bootstrap`，默认组件扫描范围不会自动覆盖所有兄弟包；不要仅凭聚合 POM 中列出了模块就认为它已经接入运行时。

## 常用命令

以下命令均从仓库根目录执行：

```powershell
# 编译并运行全部测试
mvn -f qtfycg/pom.xml clean test

# 完整打包
mvn -f qtfycg/pom.xml clean package

# 只测试 framework，并同时构建其依赖模块
mvn -f qtfycg/pom.xml -pl framework -am test

# 只运行指定测试；该参数避免上游无匹配测试时提前失败
mvn -f qtfycg/pom.xml -pl framework -am -Dtest=GlobalExceptionHandlerTest -Dsurefire.failIfNoSpecifiedTests=false test

# 启动应用；需要开发配置指定的 MySQL 可用
mvn -f qtfycg/pom.xml -pl bootstrap -am spring-boot:run
```

验证应与改动范围匹配：

- 文档改动：检查内容、路径和 Markdown，可不运行 Maven。
- 单模块 Java 改动：至少运行该模块测试并带 `-am`。
- 公共类型、父 POM、依赖或跨模块改动：运行全量 `clean test`；交付物相关改动再运行 `clean package`。
- 涉及数据库映射或启动配置：除自动化测试外，明确说明是否实际连接数据库完成了运行验证。

## Java 与 Spring 约定

- 新增类型采用标准 Java 命名：类、接口、枚举使用 `UpperCamelCase`，方法和字段使用 `lowerCamelCase`，常量使用 `UPPER_SNAKE_CASE`。
- 仓库已有少量小写开头类型，例如 `bootstrapApplication`、`mybatisPlusConfig`、`user`。局部任务中不要无关重命名；若任务要求规范化命名，必须同步修改 Java 引用、MyBatis XML 的 `namespace` / `type` 以及相关测试。
- 包名保持小写并放在 `com.qtfycg.<module>` 下。
- 注释和面向用户的项目文档优先使用中文；标识符遵循 Java 语义，避免拼音缩写和含义不明的名称。
- 新 Java 源文件沿用仓库版权头：`Copyright (c) 2026 qtfycg All rights reserved`。不要继续复制已有文件中重复或残缺的版权头。
- Controller 只负责 HTTP 协议、参数校验和调用 Service；业务规则放在 Service；数据库操作放在 Mapper。
- Spring Bean 优先使用构造器注入，不新增字段注入。
- 不要在业务模块重复注册 `framework` 已提供的分页、乐观锁和防全表更新/删除拦截器。

## 统一响应与异常

- HTTP 接口统一返回 `com.qtfycg.common.result.Result<T>`。
- 成功使用 `Result.ok(data)`、`Result.ok()` 或 `Result.created(data)`。
- 可预期的业务失败抛出 `BusinessException`，交由 `GlobalExceptionHandler` 转换响应；不要在每个 Controller 中重复拼装失败结果。
- 通用错误码定义在 `GlobalErrorCode`。模块专属错误码应在所属模块定义枚举并实现 `ErrorCode`，避免把所有业务码堆入全局枚举。
- 错误码需要同时表达 HTTP 状态 `status`、稳定的业务码 `code` 和默认消息 `message`。
- 全局异常响应的 `traceId` 来自 MDC 的 `traceId` 键。新增链路能力时保持该键一致。
- 不向客户端返回堆栈、SQL、凭据或内部实现细节；详细异常只写服务端日志。

## MyBatis-Plus 与数据库

- Mapper 接口继承 `BaseMapper<T>`；Service 接口使用 `IService<T>`；实现类使用 `ServiceImpl<Mapper, T>`。
- Mapper XML 放在模块的 `src/main/resources/mapper/` 下，XML `namespace` 必须与 Mapper 接口全限定名完全一致。
- 修改模型字段时，同步检查 Mapper XML 的 `resultMap`、公共列清单、数据库列名和 Java 类型。
- 启用乐观锁的模型使用 `version` 字段并正确标注；逻辑删除字段需要与 MyBatis-Plus 配置及表定义一致，不能只在 Java 类中增加同名字段。
- 分页查询必须有稳定排序；更新和删除操作必须有明确条件。全表操作会被 `BlockAttackInnerInterceptor` 拦截。
- 不在日志中打印完整实体、密码、令牌或包含敏感字段的 SQL 参数。

## 配置与敏感信息

- `application.yml` 只保留通用默认项；环境差异放入 `application-<profile>.yml` 或环境变量。
- 仓库的开发配置目前包含数据库连接信息。不要在文档、测试输出、提交信息或回复中复述具体凭据，也不要新增明文密码、Token、Cookie、私钥或生产地址。
- 涉及配置改造时，优先使用 `${ENV_NAME:local-default}` 形式，并同步记录必需的环境变量；生产配置不得依赖开发环境默认密码。
- 测试不得连接或清理真实数据库。需要集成测试时使用隔离的测试库、容器或专用 profile，并明确数据生命周期。

## 测试要求

- 当前测试使用 JUnit 5 和 AssertJ。
- 纯逻辑和异常映射优先写快速单元测试，不必为每个测试启动完整 Spring 上下文。
- 修复缺陷时至少覆盖一个能在修复前失败、修复后通过的回归场景。
- 对 Controller 关注状态码、参数校验和响应结构；对 Service 关注业务分支和异常码；对 Mapper 关注字段映射、条件和分页边界。
- 测试应可重复执行，不依赖执行顺序、当前时间、开发机已有数据或本地私密配置。
- 如果因外部服务不可用而未执行某项验证，交付时明确列出未验证部分和原因，不能把“编译通过”等同于“运行正常”。

## Git 与交付

- 分支约定：`dev` 用于日常开发，`main` 用于稳定版本；不要直接向 `main` 提交。
- 提交信息沿用仓库历史格式：`类型: :gitmoji: 中文描述`，类型可用 `feat`、`fix`、`docs`、`style`、`refactor`、`test`、`chore`。
- 不提交 `target/`、IDE 配置、日志、测试报告或本地环境文件。
- 完成后再次检查 `git status --short` 和差异，只汇报本次实际修改及真实验证结果，不把用户原有改动算作自己的成果。
