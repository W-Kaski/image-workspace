CREATE DATABASE IF NOT EXISTS ek_cloud_gallery;

use ek_cloud_gallery;

create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment 'userAccount',
    userPassword varchar(512)                           not null comment 'userPassword',
    userName     varchar(256)                           null comment 'userName',
    userAvatar   varchar(1024)                          null comment 'userAvatar',
    userProfile  varchar(512)                           null comment 'userProfile',
    userRole     varchar(256) default 'user'            not null comment 'userRole：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment 'editTime',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'createTime',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment 'isDelete',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
    ) comment 'user' collate = utf8mb4_unicode_ci;

-- picture table
create table if not exists picture
(
    id           bigint auto_increment comment 'id' primary key,
    url          varchar(512)                       not null comment 'picture url',
    name         varchar(128)                       not null comment 'name',
    introduction varchar(512)                       null comment 'introduction',
    category     varchar(64)                        null comment 'category',
    tags         varchar(512)                       null comment 'tags（JSON）',
    picSize      bigint                             null comment 'picSize',
    picWidth     int                                null comment 'picWidth',
    picHeight    int                                null comment 'picHeight',
    picScale     double                             null comment 'picScale',
    picFormat    varchar(32)                        null comment 'picFormat',
    userId       bigint                             not null comment 'userId',
    createTime   datetime default CURRENT_TIMESTAMP not null comment 'createTime',
    editTime     datetime default CURRENT_TIMESTAMP not null comment 'editTime',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'updateTime',
    isDelete     tinyint  default 0                 not null comment 'isDelete',
    INDEX idx_name (name),                 -- Improve query performance based on image names
    INDEX idx_introduction (introduction), -- Used for fuzzy search image introduction
    INDEX idx_category (category),         -- Improving classification-based query performance
    INDEX idx_tags (tags),                 -- Improving tag-based query performance
    INDEX idx_userId (userId)              -- Improved performance of user ID-based queries
) comment 'picture' collate = utf8mb4_unicode_ci;

ALTER TABLE picture
    -- add new features
    ADD COLUMN reviewStatus INT DEFAULT 0 NOT NULL COMMENT 'Review：0-Not reviewed; 1-Pass; 2-Reject',
    ADD COLUMN reviewMessage VARCHAR(512) NULL COMMENT 'Review message',
    ADD COLUMN reviewerId BIGINT NULL COMMENT 'Reviewer Id',
    ADD COLUMN reviewTime DATETIME NULL COMMENT 'Review time';

-- Base on reviewStatus index
CREATE INDEX idx_reviewStatus ON picture (reviewStatus);

ALTER TABLE picture
    ADD COLUMN thumbnailUrl varchar(512) NULL COMMENT 'thumbnailUrl';


-- space table
create table if not exists space
(
    id         bigint auto_increment comment 'id' primary key,
    spaceName  varchar(128)                       null comment 'spaceName',
    spaceLevel int      default 0                 null comment 'spaceLevel：0-normal 1-plus 2-pro',
    maxSize    bigint   default 0                 null comment 'maxSize',
    maxCount   bigint   default 0                 null comment 'maxCount',
    totalSize  bigint   default 0                 null comment 'totalSize',
    totalCount bigint   default 0                 null comment 'totalCount',
    userId     bigint                             not null comment 'userId',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'createTime',
    editTime   datetime default CURRENT_TIMESTAMP not null comment 'editTime',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'updateTime',
    isDelete   tinyint  default 0                 not null comment 'isDelete',
    -- 索引设计
    index idx_userId (userId),
    index idx_spaceName (spaceName),
    index idx_spaceLevel (spaceLevel)
) comment 'space' collate = utf8mb4_unicode_ci;

-- 添加新列
ALTER TABLE picture
    ADD COLUMN spaceId bigint  null comment 'spaceId';

-- 创建索引
CREATE INDEX idx_spaceId ON picture (spaceId);


-- 添加新列
ALTER TABLE picture
    ADD COLUMN picColor varchar(16) null comment 'picColor';

-- 支持空间类型，添加新列
ALTER TABLE space
    ADD COLUMN spaceType int default 0 not null comment 'spaceType：0-private 1-team';

CREATE INDEX idx_spaceType ON space (spaceType);

-- 空间成员表
create table if not exists space_user
(
    id         bigint auto_increment comment 'id' primary key,
    spaceId    bigint                                 not null comment 'spaceId',
    userId     bigint                                 not null comment 'userId',
    spaceRole  varchar(128) default 'viewer'          null comment 'spaceRole：viewer/editor/admin',
    createTime datetime     default CURRENT_TIMESTAMP not null comment 'createTime',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'updateTime',
    -- 索引设计
    UNIQUE KEY uk_spaceId_userId (spaceId, userId),
    INDEX idx_spaceId (spaceId),
    INDEX idx_userId (userId)
) comment 'Space user' collate = utf8mb4_unicode_ci;