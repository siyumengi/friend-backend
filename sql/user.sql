/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : friend

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 29/05/2023 11:00:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `userPassword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(0) NOT NULL DEFAULT 0 COMMENT '性别：0-未知；1-男；2-女',
  `age` tinyint(0) NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `userStatus` int(0) NOT NULL DEFAULT 0 COMMENT '状态 0 - 正常',
  `avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签 json 列表',
  `userRole` int(0) NOT NULL DEFAULT 0 COMMENT '用户角色 0 - 普通用户 1 - 管理员',
  `profile`     varchar(1024) null comment '个人介绍',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
-- 队伍表
create table team
(
  id          bigint auto_increment comment 'id' primary key,
  name        varchar(256)       not null comment '队伍名称',
  description varchar(1024) null comment '描述',
  maxNum      int      default 1 not null comment '最大人数',
  expireTime  datetime null comment '过期时间',
  userId      bigint comment '用户id（队长 id）',
  status      int      default 0 not null comment '0 - 公开，1 - 私有，2 - 加密',
  password    varchar(512) null comment '密码',
  createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
  updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
  isDelete    tinyint  default 0 not null comment '是否删除'
) comment '队伍';

-- 用户队伍关系
create table user_team
(
  id         bigint auto_increment comment 'id'
    primary key,
  userId     bigint comment '用户id',
  teamId     bigint comment '队伍id',
  joinTime   datetime null comment '加入时间',
  createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
  updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
  isDelete   tinyint  default 0 not null comment '是否删除'
) comment '用户队伍关系';
