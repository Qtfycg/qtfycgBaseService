# SQL 变更脚本

本目录只保存数据库变更的 SQL 语句，不保存数据 dump、备份文件或演示数据。数据库版本与应用版本绑定：目录名即应用版本（`qtfycg/pom.xml` 的 `<version>`，四段版本 `主.次.修订.构建`），并与发布 tag `v<应用版本>` 一致。

## 目录结构

```text
SQL/
├── README.md
├── DDL/
│   └── <应用版本>/
│       ├── forward.sql     # 正向：结构变更
│       └── rollback.sql    # 回滚：撤销本版本结构变更
└── DML/
    └── <应用版本>/
        ├── forward.sql     # 正向：数据变更
        └── rollback.sql    # 回滚：撤销本版本数据变更
```

- `DDL/<版本>/` 每个版本都必须有。
- `DML/<版本>/` 与 DDL 版本目录同步保留；没有数据变更时在 `forward.sql` 中注明「无数据变更」，执行即为空操作。
- 目录顺序不代表执行顺序，执行顺序以本文件的「版本清单」为准。

## 版本清单（执行顺序以此表为准）

| 顺序 | 应用版本 | 日期 | 内容 | 回滚演练 | dev | prod |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 1.0.0.100 | 2026-11-10 | 初始化用户账号表 `user` | ⬜ | ⬜ | ⬜ |

- 新增版本时在表末追加一行，执行序号 = 表格行序。**不要依赖目录名字典序**：`1.0.9` 会排在 `1.0.10` 之后，`1.0.0.90` 会排在 `1.0.0.100` 之后。
- dev / prod 列标记该环境是否已执行（✅ 已执行 / ⬜ 未执行）。
- 回滚演练列表示已在 dev 完成「正向 → 回滚 → 再正向」。

## 前置条件

- 数据库 `qtfycg` 已创建（`utf8mb4`），执行脚本时连接该库。
- 若本地已手工创建 `user` 表，先执行本版本 `rollback.sql` 或手工 `DROP TABLE` 后再运行 `forward.sql`（`forward.sql` 使用普通 `CREATE TABLE`，表已存在会报错，这是预期保护）。

## 执行

按版本清单顺序，先 DDL 后 DML：

```bash
mysql -h 127.0.0.1 -P 13306 -u root -p --default-character-set=utf8mb4 qtfycg < DDL/1.0.0.100/forward.sql
mysql -h 127.0.0.1 -P 13306 -u root -p --default-character-set=utf8mb4 qtfycg < DML/1.0.0.100/forward.sql
```

PowerShell 下 `<` 重定向不生效，使用管道：

```powershell
Get-Content .\DDL\1.0.0.100\forward.sql -Raw | mysql -u root -p qtfycg
```

升级已有环境时，只执行清单中对应环境尚未勾选的版本。执行完成后更新清单。

## 回滚

回滚顺序与正向相反：先 DML、再 DDL。

```bash
mysql -h 127.0.0.1 -P 13306 -u root -p qtfycg < DML/1.0.0.100/rollback.sql
mysql -h 127.0.0.1 -P 13306 -u root -p qtfycg < DDL/1.0.0.100/rollback.sql
```

- 回滚一个版本 = 执行该版本的两个 rollback 脚本，并把应用 jar 退回同一版本；回滚多个版本时从最新版本开始逐版倒序执行，每步执行完更新版本清单。
- MySQL 的 DDL 隐式提交，回滚是补偿脚本而不是事务回滚；执行回滚时**不要加 `-f`**，出错立即停止并人工处理。
- 涉及不可逆操作（`DROP COLUMN`、`DROP TABLE`、无条件 `UPDATE`/`DELETE`）时，必须在 `rollback.sql` 头部注明，并在执行前先备份（备份文件放服务器，不提交仓库）。
- 有数据写入后，优先用下一个版本「前滚修复」，不要用回滚处理线上数据问题；回滚只用于发布失败、旧代码仍兼容旧库的窗口。

## 编写规则

文件头固定为：

```sql
-- Copyright (c) 2026 qtfycg All rights reserved
-- 版本：<应用版本>  日期：yyyy-MM-dd
-- 内容：<本次变更说明>
-- 关联：<commit / PR>
```

- `forward.sql` 与 `rollback.sql` 必须一一对应，用 `-- [1]`、`-- [2]` 编号对齐正向与回滚语句。
- 建表统一 `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`，表和列必须写 `COMMENT`；表名 `user` 等易与关键字混淆的标识统一加反引号。
- 公共字段沿用 `user` 表：`id BIGINT`（应用侧生成，不自增）、`created_by`/`created_time`/`updated_by`/`updated_time`、`deleted`（逻辑删除）、`version`（乐观锁）；逻辑删除与乐观锁字段必须与 MyBatis-Plus 配置一致。
- DML 必须幂等：`INSERT ... ON DUPLICATE KEY UPDATE` 或 `INSERT IGNORE`；禁止无 `WHERE` 的 `UPDATE`/`DELETE`（与 `BlockAttackInnerInterceptor` 一致）。
- 不在脚本里写明文密码、真实手机号等敏感数据；初始化密码使用 BCrypt 占位。
- 数据修正类 DML 的 rollback 依赖备份表：正向先建 `bak_<日期>_<表>` 并存原值，回滚从备份表恢复后删除备份表。
- 版本目录一旦在任一环境执行过即冻结，`forward.sql` 与 `rollback.sql` 都不允许修改，后续变更只能新建版本目录。

## 与发版的关系

1. 升应用版本：修改 `qtfycg/pom.xml` 的 `<version>` 及 common、bootstrap、user 三个子模块的 `<parent><version>`，打 tag `v<应用版本>`；bootstrap 对 common 的依赖已使用 `${project.version}`，无需单独改。
2. dev 环境执行新版本 `forward.sql`，完成「正向 → 回滚 → 再正向」演练并在清单勾选。
3. 合并 `main` 部署后，生产执行同一版本脚本并更新清单。
4. 回滚时 jar 与 SQL 一起回退，保证应用版本与数据库版本配对。
