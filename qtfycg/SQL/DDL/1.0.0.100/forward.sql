-- Copyright (c) 2026 qtfycg All rights reserved
-- 版本：1.0.0.100  日期：2026-11-10
-- 内容：初始化用户模块用户账号表 user
-- 关联：TODO
-- 前置：数据库 qtfycg 已创建（utf8mb4）；若本地已手工创建该表，先执行 rollback.sql 或手工 DROP 后再运行
SET NAMES utf8mb4;

-- [1] 用户账号表
CREATE TABLE `user`
(
    id                   BIGINT       NOT NULL COMMENT '用户ID，对应Java Long',
    username             VARCHAR(64)  NOT NULL COMMENT '用户名',
    password             VARCHAR(255) NOT NULL COMMENT '密码，加密后',
    status               TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态 0禁用 1正常',
    account_type         VARCHAR(32)  NOT NULL DEFAULT 'USER' COMMENT '账号类型 USER ADMIN SYSTEM',
    last_login_time      DATETIME     NULL COMMENT '最后登录时间',
    last_login_ip        VARCHAR(64)  NULL COMMENT '最后登录IP',
    password_update_time DATETIME     NULL COMMENT '密码更新时间',
    login_fail_count     INT          NOT NULL DEFAULT 0 COMMENT '连续登录失败次数',
    created_by           VARCHAR(64)  NULL COMMENT '创建人',
    created_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by           VARCHAR(64)  NULL COMMENT '修改人',
    updated_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted              TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    version              INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
    PRIMARY KEY (id),
    CONSTRAINT uk_username UNIQUE (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户账号表';
