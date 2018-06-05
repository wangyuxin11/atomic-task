/*
Navicat MySQL Data Transfer

Source Server         : Local
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : apiaccount

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-06-05 15:02:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `auto_task`
-- ----------------------------
DROP TABLE IF EXISTS `auto_task`;
CREATE TABLE `auto_task` (
  `TASK_NO` varchar(64) NOT NULL COMMENT '任务编号',
  `TASK_TYPE` varchar(64) NOT NULL COMMENT '任务分类',
  `TASK_METHOD` varchar(64) NOT NULL COMMENT '调用方法,spring:beanId.method            \r\n参数:TaskExecContext            \r\n返回:TaskResult',
  `NEXT_EXEC_TIME` timestamp NULL DEFAULT NULL COMMENT '下次执行时间',
  `CUR_EXECUTOR` varchar(128) DEFAULT NULL COMMENT '当前执行者,执行完成会清除',
  `LAST_EXEC_TIME` timestamp NULL DEFAULT NULL COMMENT '最近执行时间',
  `LAST_EXECED_TIME` timestamp NULL DEFAULT NULL COMMENT '最近结束时间',
  `LAST_EXECUTOR` varchar(128) DEFAULT NULL COMMENT '最近执行者',
  `EXEC_EXPIRE_MINUTE` decimal(8,0) NOT NULL COMMENT '执行超时时长(分钟)',
  `EXECUTED_TIMES` decimal(8,0) NOT NULL COMMENT '已执行次数',
  `MAX_EXEC_TIMES` decimal(8,0) NOT NULL COMMENT '最大执行次数',
  `CONTEXT_JSON` varchar(4000) NOT NULL COMMENT '任务上下文',
  `EXEC_MSG` varchar(4000) DEFAULT NULL COMMENT '错误消息',
  `TASK_STATE` varchar(64) DEFAULT NULL COMMENT '任务状态',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`TASK_NO`,`TASK_TYPE`),
  KEY `IDX_ATASK_COMPENSATE` (`UPDATE_TIME`,`NEXT_EXEC_TIME`,`TASK_STATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='原子任务';

-- ----------------------------
-- Records of auto_task
-- ----------------------------
