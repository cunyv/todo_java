-- ============================================
-- 任务看板 - 数据库初始化脚本
-- 数据库: MySQL 5.7+ / 8.0+
-- 字符集: utf8mb4
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS todo_app
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE todo_app;

-- ============================================
-- 用户表
-- ============================================
DROP TABLE IF EXISTS `todos`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`   VARCHAR(50)  NOT NULL                  COMMENT '用户名',
    `password`   VARCHAR(255) NOT NULL                  COMMENT '密码',
    `created_at` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 待办事项表
-- ============================================
CREATE TABLE `todos` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT                COMMENT '任务ID',
    `title`       VARCHAR(255) NOT NULL                                COMMENT '任务标题',
    `description` VARCHAR(500) DEFAULT NULL                            COMMENT '任务描述',
    `status`      VARCHAR(20)  NOT NULL DEFAULT 'TODO'                 COMMENT '任务状态: TODO/IN_PROGRESS/DONE',
    `user_id`     BIGINT       NOT NULL                                COMMENT '所属用户ID',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP               COMMENT '创建时间',
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_user_status` (`user_id`, `status`),
    CONSTRAINT `fk_todo_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项表';

-- ============================================
-- 初始化默认用户 (密码: 123456)
-- ============================================
INSERT INTO `users` (`username`, `password`) VALUES
('admin', '123456');

-- ============================================
-- 初始化示例待办事项
-- ============================================
INSERT INTO `todos` (`title`, `description`, `status`, `user_id`) VALUES
('学习数据库设计',    '学习关系型数据库设计原则和SQL优化',                   'TODO',        1),
('准备面试',         '复习Java基础知识、Spring框架和设计模式',              'TODO',        1),
('写技术博客',       '总结学习过程中的心得体会，分享技术经验',                'TODO',        1),
('学习Spring Boot',  '完成Spring Boot基础教程，包括依赖注入、数据访问等',    'IN_PROGRESS', 1),
('开发REST API',     '为待办事项应用添加RESTful接口',                       'IN_PROGRESS', 1),
('搭建项目环境',     '配置Maven、创建项目结构、添加依赖',                   'DONE',        1),
('设计数据库表',     '设计todos表结构，包含id、title、description等字段',   'DONE',        1);
