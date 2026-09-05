# qtfycgBaseService

集中存放个人开发过程中编写过的后端项目代码，主要包括 Java、Spring Boot、MySQL、Redis、权限认证、接口服务等后端相关项目。

## 仓库定位

主要用途：

* 统一归档后端项目代码
* 记录后端项目开发过程
* 沉淀后端工程能力
* 管理多个后端项目的代码结构
* 展示个人后端项目经验和工程实践

## 项目列表

| 项目名称 | 项目说明 | 技术栈 | 状态 |
| ------------------------ | -------------- | ---------------------------------- | --- |
| order-axis | 在线点餐系统 | Java / Spring Boot / MySQL / Redis | 开发中 |
| knowledge-system | AI 客服与工单系统后端服务 | Java / Spring Boot / MySQL / Redis | 规划中 |
| personal-finance | 个人财务管理后端服务 | Java / Spring Boot / MySQL | 规划中 |

## 目录结构

```text
backend-projects/
├── README.md
├── order-axis/
├── knowledge-system/
├── personal-finance/
└── demos/
```

## 目录说明

| 目录 | 说明 |
| --------------------------- | ---------------- |
| `order-axis-backend/` | 在线点餐系统 |
| `knowledge-system-backend/` | AI 客服与工单系统 |
| `personal-finance-backend/` | 个人财务管理 |
| `demos/` | 后端练习、技术验证或临时实验代码 |

## 分支规则

本仓库采用以下分支模型：

```text
main：稳定归档分支
dev：日常开发分支
```

规则说明：

* `main` 分支用于保存相对稳定的项目代码
* `dev` 分支用于日常开发、修改和整理
* 日常开发优先提交到 `dev`
* 稳定后通过 Pull Request 合并到 `main`
* 禁止直接向 `main` 分支提交代码

## 开发流程

```text
dev 开发
→ 提交代码
→ 创建 Pull Request
→ 合并到 main
```

## 提交规范

提交信息建议使用以下格式：

```text
feat: 新增功能
fix: 修复问题
docs: 修改文档
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 配置、依赖或工程调整
```

示例：

```text
feat: 新增用户登录接口
fix: 修复数据库连接配置问题
docs: 补充接口说明
refactor: 重构统一响应结构
chore: 初始化 Maven 项目结构
```

## 维护说明

本仓库会随着个人后端项目开发过程持续更新。每个项目应保持独立目录、独立配置、独立依赖和独立说明，避免不同项目之间产生不必要的耦合。
