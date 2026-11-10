-- Copyright (c) 2026 qtfycg All rights reserved
-- 版本：1.0.0.100  回滚目标：空库（初始化版本）
-- 警告：不可逆——DROP TABLE 会删除 user 表全部数据，执行前必须备份
-- 顺序：与 forward.sql 相反、逐条对应

-- [1] 删除用户账号表（对应 forward.sql 的 CREATE TABLE）
DROP TABLE IF EXISTS `user`;
