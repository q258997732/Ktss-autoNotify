CREATE TABLE `tss_event_monstat`
(
    `id`             varchar(50) NOT NULL COMMENT '唯一标识',
    `event_id`       varchar(45) NOT NULL COMMENT '事件ID',
    `user_id`        varchar(45) NOT NULL COMMENT '用户ID',
    `notify_time`    datetime DEFAULT NULL COMMENT '弹窗通知时间',
    `resolve_time`   datetime DEFAULT NULL COMMENT '处理时间',
    `resolve_status` int(11) DEFAULT NULL COMMENT '处理动作\\n0:未处理\\n1:确认已处理\\n2:忽略\\n3:未知',
    PRIMARY KEY (`id`),
    UNIQUE KEY `event_id_UNIQUE` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卫星监控系统报警事件监控员处理情况表格'