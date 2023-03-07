########################### 以下为app表结构 ######################################
DROP TABLE IF EXISTS `tb_announcement`;
CREATE TABLE `tb_announcement`
(
    `id`          bigint(20)                             NOT NULL AUTO_INCREMENT,
    `title`       varchar(200) DEFAULT NULL COMMENT '标题',
    `description` text COMMENT '描述',
    `created`     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `updated`     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `created` (`created`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='公告表';

DROP TABLE IF EXISTS `tb_black_list`;
CREATE TABLE `tb_black_list`
(
    `id`            bigint(20)                           NOT NULL AUTO_INCREMENT,
    `user_id`       bigint(20) DEFAULT NULL,
    `black_user_id` bigint(20) DEFAULT NULL,
    `created`       datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    `updated`       datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 32
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='黑名单';

CREATE TABLE `tb_question`
(
    `id`      bigint(20)                             NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20)   DEFAULT NULL COMMENT '用户id',
    `txt`     varchar(200) DEFAULT NULL COMMENT '问题内容',
    `created` datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `updated` datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `tb_settings`;
CREATE TABLE `tb_settings`
(
    `id`                   bigint(20)                           NOT NULL AUTO_INCREMENT,
    `user_id`              bigint(20) DEFAULT NULL,
    `like_notification`    tinyint(4) DEFAULT '1' COMMENT '推送喜欢通知',
    `pinglun_notification` tinyint(4) DEFAULT '1' COMMENT '推送评论通知',
    `gonggao_notification` tinyint(4) DEFAULT '1' COMMENT '推送公告通知',
    `created`              datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    `updated`              datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='设置表';

DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`
(
    `id`          bigint(20)                            NOT NULL AUTO_INCREMENT,
    `mobile`      varchar(11) DEFAULT NULL COMMENT '手机号',
    `password`    varchar(32) DEFAULT NULL COMMENT '密码，需要加密',
    `created`     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    `updated`     datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `hx_user`     varchar(25) DEFAULT NULL,
    `hx_password` varchar(25) DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `mobile` (`mobile`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 111
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='用户表';

DROP TABLE IF EXISTS `tb_user_info`;
CREATE TABLE `tb_user_info`
(
    `id`         bigint(20)                             NOT NULL,
    `nickname`   varchar(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`     varchar(255) DEFAULT NULL COMMENT '用户头像',
    `tags`       varchar(50)  DEFAULT '单身,本科,年龄相仿' COMMENT '用户标签：多个用逗号分隔',
    `gender`     varchar(10)  DEFAULT '3' COMMENT '性别，1-男，2-女，3-未知',
    `age`        int(11)      DEFAULT NULL COMMENT '用户年龄',
    `education`  varchar(20)  DEFAULT NULL COMMENT '学历',
    `city`       varchar(20)  DEFAULT NULL COMMENT '居住城市',
    `birthday`   varchar(20)  DEFAULT NULL COMMENT '生日',
    `cover_pic`  varchar(100) DEFAULT NULL COMMENT '封面图片',
    `profession` varchar(20)  DEFAULT NULL COMMENT '行业',
    `income`     varchar(20)  DEFAULT NULL COMMENT '收入',
    `created`    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `updated`    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `marriage`   int(1)       DEFAULT '0' COMMENT '0：未婚，1：已婚',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='用户信息表';


########################### 以下为app_admin表结构 ######################################
DROP TABLE IF EXISTS `tb_admin`;
CREATE TABLE `tb_admin`
(
    `id`       bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账号',
    `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
    `avatar`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
    `created`  datetime default CURRENT_TIMESTAMP                      not null comment '创建时间',
    `updated`  datetime default CURRENT_TIMESTAMP                      not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '后台管理员表'
  ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `tb_analysis`;
CREATE TABLE `tb_analysis`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT,
    `record_date`     date       NOT NULL COMMENT '日期',
    `num_registered`  int(8)     NOT NULL DEFAULT 0 COMMENT '新注册用户数',
    `num_active`      int(8)     NOT NULL DEFAULT 0 COMMENT '活跃用户数',
    `num_login`       int(8)     NOT NULL DEFAULT 0 COMMENT '登陆次数',
    `num_retention1d` int(8)     NOT NULL DEFAULT 0 COMMENT '次日留存用户数',
    `created`         datetime            default CURRENT_TIMESTAMP not null comment '创建时间',
    `updated`         datetime            default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `record_date` (`record_date`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1012
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `tb_log`;
CREATE TABLE `tb_log`
(
    `id`        bigint(20)                                              NOT NULL AUTO_INCREMENT,
    `user_id`   bigint(20)                                              NOT NULL COMMENT '用户id',
    `type`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作类型,\r\n0101为登录，0201为发动态，0202为浏览动态，0203为动态点赞，0204为动态喜欢，0205为评论，0206为动态取消点赞，0207为动态取消喜欢，0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论',
    `log_time`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '操作日期',
    `place`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作地点',
    `equipment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作设备',
    `created`   datetime                                                     default CURRENT_TIMESTAMP not null comment '创建时间',
    `updated`   datetime                                                     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `time_type_user` (`log_time`, `type`, `user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 152796
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '用户日志表'
  ROW_FORMAT = DYNAMIC;