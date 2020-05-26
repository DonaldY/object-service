-- 文件表
DROP TABLE IF EXISTS `object_file`;
CREATE TABLE object_file (
  file_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id INT UNSIGNED NOT NULL COMMENT '用户id',
  client_id VARCHAR (30) NOT NULL DEFAULT '' COMMENT '应用id',
  file_name VARCHAR(30) NOT NULL DEFAULT '' COMMENT '文件名',
  path VARCHAR(200) NOT NULL DEFAULT '' COMMENT '路径(文件夹+文件名)',
  access_type TINYINT NOT NULL DEFAULT 0 COMMENT '访问类别',
  created_at DATETIME NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '更新时间',
  PRIMARY KEY (file_id)
) ENGINE=InnoDB DEFAULT CHARSET utf8mb4 COMMENT '文件表';

DROP TABLE IF EXISTS `object_file`;
CREATE TABLE `object_file` (
  `file_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户id',
  `client_id` VARCHAR (30) NOT NULL DEFAULT '' COMMENT '应用id',
  `file_name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '文件名',
  `path` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '文件存储路径',
  `type` TINYINT NOT NULL DEFAULT 0 COMMENT '文件类别, 0 私有文件， 1 公有文件， 10 目录',
  `url` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '外链',
  `namespace_id` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '命名空间ID',
  `dir_id` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '目录ID, 0 表示根目录',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `created_at` DATETIME NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '更新时间',
  PRIMARY KEY (file_id)
) ENGINE=InnoDB DEFAULT CHARSET utf8mb4 COMMENT '文件表';

DROP TABLE IF EXISTS `namespace`;
CREATE TABLE `namespace` (
  `namespace_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `namespace_name` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '命名空间名',
  `desc` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '描述',
  `user_id` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户id',
  `client_id` VARCHAR (30) NOT NULL DEFAULT '' COMMENT '应用id',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `created_at` DATETIME NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '更新时间',
  PRIMARY KEY (namespace_id)
) ENGINE=InnoDB DEFAULT CHARSET utf8mb4 COMMENT '命名空间表';