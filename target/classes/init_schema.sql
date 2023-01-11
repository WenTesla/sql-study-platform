CREATE TABLE `link_question_group`  (
  `id` bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `question_group_id` bigint unsigned  NOT NULL COMMENT '试题组ID',
  `question_id` bigint unsigned  NOT NULL COMMENT '试题ID',
  PRIMARY KEY (`id`)
) COMMENT '关联试题';

----------------------
CREATE TABLE `question`  (
  `id` bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `question_title` varchar(128)  NOT NULL COMMENT '题目标题',
  `content` varchar(512) NULL COMMENT '题目描述',
  `init_table_sql` text  NOT NULL COMMENT '初始化表结构',
  `init_data_sql` text  NOT NULL COMMENT '初始化数据',
  `answer_sql` text  NOT NULL COMMENT '答案',
  `score` int  NOT NULL COMMENT '得分',
  `info` json NULL COMMENT '其他信息',
  PRIMARY KEY (`id`)
) COMMENT '题目';

----------------------
CREATE TABLE `question_group`  (
  `id` bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `question_group_title` varchar(128)  NOT NULL COMMENT '题目组标题',
  PRIMARY KEY (`id`)
) COMMENT '题目组';

----------------------
CREATE TABLE `setting`  (
  `id` bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `key` varchar(32)  NOT NULL COMMENT 'KEY',
  `value` varchar(512)  NOT NULL COMMENT 'VALUE',
  PRIMARY KEY (`id`)
) COMMENT '基本配置';

----------------------
CREATE TABLE `user`  (
  `id` bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `username` varchar(128)  NOT NULL COMMENT '用户名',
  `password` varchar(128)  NOT NULL COMMENT '密码',
  `role_code` varchar(32) NULL COMMENT '角色',
  `info` json NULL COMMENT '其他信息',
  PRIMARY KEY (`id`)
) COMMENT '用户表';

----------------------
CREATE TABLE `user_result`  (
  `id` bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_id` bigint unsigned  NOT NULL COMMENT '用户ID',
  `question_group_id` bigint unsigned  NOT NULL COMMENT '试题组ID',
  `question_id` bigint unsigned  NOT NULL COMMENT '试题ID',
  `score` int  NOT NULL COMMENT '得分',
  `answer` varchar(512) NULL COMMENT '答案',
  `start_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
  `info` json NULL COMMENT '其他信息',
  PRIMARY KEY (`id`)
) COMMENT '用户结果';
