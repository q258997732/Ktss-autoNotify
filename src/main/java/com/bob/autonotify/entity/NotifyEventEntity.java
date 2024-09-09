package com.bob.autonotify.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotifyEventEntity {

	@JsonProperty("username")
	private String username;

	@JsonProperty("xCarNumber")
	private String xCarNumber;

	@JsonProperty("xAlarm")
	private String xAlarm;

	@JsonProperty("xAlarmStartTime")
	private	String xAlarmStartTime;

	@JsonProperty("id")
	private String id;

	@JsonProperty("xStartSpeed")
	private String xStartSpeed;

	public NotifyEventEntity() {
	}

	public NotifyEventEntity(String xCarNumber, String username, String xAlarm, String xAlarmStartTime, String id, String xStartSpeed) {
		this.xCarNumber = xCarNumber;
		this.username = username;
		this.xAlarm = xAlarm;
		this.xAlarmStartTime = xAlarmStartTime;
		this.id = id;
		this.xStartSpeed = xStartSpeed;
	}

	public String toString() {
		if(getXStartSpeed()==null||getXStartSpeed().isEmpty())
			setXStartSpeed("未知");
		return String.format("%s 车牌号:%s、报警信息:%s、报警开始时间:%s、开始速度:%s",getUsername(), getXCarNumber(), getXAlarm(), getXAlarmStartTime(), getXStartSpeed());
	}

}
