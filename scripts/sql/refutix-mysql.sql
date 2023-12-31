--  Licensed to the Apache Software Foundation (ASF) under one or more
--  contributor license agreements.  See the NOTICE file distributed with
--  this work for additional information regarding copyright ownership.
--  The ASF licenses this file to You under the Apache License, Version 2.0
--  (the "License"); you may not use this file except in compliance with
--  the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
--  Unless required by applicable law or agreed to in writing, software
--  distributed under the License is distributed on an "AS IS" BASIS,
--  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--  See the License for the specific language governing permissions and
--  limitations under the License.

DROP TABLE IF EXISTS `refutix_user`;
CREATE TABLE if not exists `refutix_user`
(
    `id`          int(11)      NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `username`    varchar(50)  NOT NULL COMMENT 'username',
    `password`    varchar(50)  NULL     DEFAULT NULL COMMENT 'password',
    `nickname`    varchar(50)  NULL     DEFAULT NULL COMMENT 'nickname',
    `user_type`   int          NOT NULL DEFAULT 0 COMMENT 'login type (0:LOCAL,1:LDAP)',
    `url`         varchar(100) NULL     DEFAULT NULL COMMENT 'avatar url',
    `mobile`      varchar(20)  NULL     DEFAULT NULL COMMENT 'mobile phone',
    `email`       varchar(100) NULL     DEFAULT NULL COMMENT 'email',
    `enabled`     tinyint(1)   NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `refutix_tenant`;
CREATE TABLE if not exists `refutix_tenant`
(
    `id`          int(11)      NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `name`        varchar(64)  NULL     DEFAULT NULL COMMENT 'tenant name',
    `description` varchar(255) NULL     DEFAULT NULL COMMENT 'tenant description',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `refutix_user_tenant_rel`;
CREATE TABLE if not exists `refutix_user_tenant_rel`
(
    `id`          int(11)     NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `user_id`     int(11)     NOT NULL COMMENT 'user id',
    `tenant_id`   int(11)     NOT NULL COMMENT 'tenant id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `refutix_role`;
CREATE TABLE if not exists `refutix_role`
(
    `id`          int(11)      not null auto_increment primary key comment 'id',
    `role_name`   varchar(30)  not null comment 'role name',
    `role_key`    varchar(100) not null comment 'role key',
    `sort`        int(4)       not null comment 'sort',
    `enabled`     tinyint(1)   NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete`   tinyint(1)   NOT NULL DEFAULT 0 COMMENT 'is delete',
    `remark`      varchar(500)          default null comment 'remark',
    `create_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0)  NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `refutix_menu`;
CREATE TABLE if not exists `refutix_menu`
(
    `id`          int(11)  not null auto_increment primary key comment 'id',
    `menu_name`   varchar(50) not null comment 'menu name',
    `parent_id`   int(11)           default 0 comment 'parent id',
    `sort`        int(4)               default 0 comment 'sort',
    `path`        varchar(200)         default '' comment 'route path',
    `query`       varchar(255)         default null comment 'route params',
    `is_cache`    int(1)               default 0 comment 'is cache（0:cache 1:no_cache）',
    `type`        char(1)              default '' comment 'menu type（M:directory C:menu F:button）',
    `visible`     char(1)              default 0 comment 'is visible（0:display 1:hide）',
    `component`   varchar(255)         default null comment 'component path',
    `is_frame`    int(1)               default 0 comment 'is frame',
    `enabled`     tinyint(1)  NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete`   tinyint(1)  NOT NULL DEFAULT 0 COMMENT 'is delete',
    `perms`       varchar(100)         default null comment 'menu perms',
    `icon`        varchar(100)         default '#' comment 'menu icon',
    `remark`      varchar(500)         default '' comment 'remark',
    `create_time` datetime(0) NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'update time'
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `refutix_user_role_rel`;
CREATE TABLE if not exists `refutix_user_role_rel`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `user_id`     int(11)     not null comment 'user id',
    `role_id`     int(11)     not null comment 'role id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_user_role` (`user_id`, `role_id`)
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `refutix_role_menu_rel`;
CREATE TABLE if not exists `refutix_role_menu_rel`
(
    `id`          int(11)     not null auto_increment primary key comment 'id',
    `role_id`     int(11)     not null comment 'role id',
    `menu_id`     int(11)     not null comment 'menu id',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
    unique key `idx_role_menu` (`role_id`, `menu_id`)
    )  ENGINE = InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `refutix_catalog`;
CREATE TABLE if not exists `refutix_catalog`
(
    id            int                                  not null comment 'id'
    primary key,
    catalog_type  varchar(50)                          not null comment 'catalog type',
    catalog_name  varchar(100)                         not null comment 'catalog name',
    warehouse     varchar(200)                         not null comment 'warehouse',
    hive_uri      varchar(200)                         null comment 'hive uri',
    hive_conf_dir varchar(100)                         null comment 'catalog name',
    is_delete     tinyint(1) default 0                 not null comment 'is delete',
    create_time   datetime   default CURRENT_TIMESTAMP null comment 'create time',
    update_time   datetime   default CURRENT_TIMESTAMP null comment 'update time',
    options       varchar(512)                         null
    ) engine = innodb;


INSERT INTO `refutix_user` ( id, username, password, nickname, mobile
                   , email, enabled, is_delete)
VALUES ( 1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', 0
       , 'admin@refutix.com', 1, 0);
INSERT INTO `refutix_user` (id, username, password, nickname, mobile, email, enabled, is_delete)
VALUES (2, 'common', '21232f297a57a5a743894a0e4a801fc3', 'common', 0, 'common@refutix.com', 1, 0);

INSERT INTO `refutix_tenant` (id, name, description)
VALUES (1, 'DefaultTenant', 'DefaultTenant');

INSERT INTO `refutix_user_tenant` (`id`, `user_id`, `tenant_id`)
VALUES (1, 1, 1);

insert into `refutix_role` (id, role_name, role_key, sort)
values (1, 'admin', 'admin', 1),
       (2, 'common', 'common', 2);

insert into `refutix_menu` (id, menu_name, parent_id, sort, path, component, is_frame, type, perms, icon, remark)
values (1, 'all', 0, 1, 'system', null, 1, 'M', 'system', 'admin', 'system root path'),
       (100, 'user manager', 1, 1, 'user', 'user/index', 1, 'C', 'system:user:list', 'user', 'user manager'),
       (1000, 'user query', 100, 1, '', '', 1, 'F', 'system:user:query', '#', ''),
       (1001, 'user add', 100, 2, '', '', 1, 'F', 'system:user:add', '#', ''),
       (1002, 'user edit', 100, 3, '', '', 1, 'F', 'system:user:edit', '#', ''),
       (1003, 'user del', 100, 4, '', '', 1, 'F', 'system:user:remove', '#', ''),
       (1004, 'user reset', 100, 5, '', '', 1, 'F', 'system:user:resetPwd', '#', ''),
       (200, 'role manager', 1, 1, 'role', 'role/index', 1, 'C', 'system:role:list', 'role', 'role manager'),
       (2000, 'role query', 200, 1, '', '', 1, 'F', 'system:role:query', '#', ''),
       (2001, 'role add', 200, 2, '', '', 1, 'F', 'system:role:add', '#', ''),
       (2002, 'role edit', 200, 3, '', '', 1, 'F', 'system:role:edit', '#', ''),
       (2003, 'role del', 200, 4, '', '', 1, 'F', 'system:role:remove', '#', ''),
       (300, 'menu manager', 1, 1, 'menu', 'menu/index', 1, 'C', 'system:menu:list', 'menu', 'menu manager'),
       (3000, 'menu query', 300, 1, '', '', 1, 'F', 'system:menu:query', '#', ''),
       (3001, 'menu add', 300, 2, '', '', 1, 'F', 'system:menu:add', '#', ''),
       (3002, 'menu edit', 300, 3, '', '', 1, 'F', 'system:menu:edit', '#', ''),
       (3003, 'menu del', 300, 4, '', '', 1, 'F', 'system:menu:remove', '#', '');

insert into refutix_user_role_rel (id, user_id, role_id)
values (1, 1, 1), (2, 2, 2);

insert into refutix_role_menu_rel (role_id, menu_id)
values (1, 1),
       (1, 100),
       (1, 1000),
       (1, 1001),
       (1, 1002),
       (1, 1003),
       (1, 1004);
