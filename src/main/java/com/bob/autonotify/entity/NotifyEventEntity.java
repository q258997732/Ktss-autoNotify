package com.bob.autonotify.entity;

public class NotifyEventEntity {

	private String username;

	private String xCarNumber;

	private String xAlarm;

	private	String xAlarmStartTime;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getxCarNumber() {
		return xCarNumber;
	}

	public void setxCarNumber(String xCarNumber) {
		this.xCarNumber = xCarNumber;
	}

	public String getxAlarm() {
		return xAlarm;
	}

	public void setxAlarm(String xAlarm) {
		this.xAlarm = xAlarm;
	}

	public String getxAlarmStartTime() {
		return xAlarmStartTime;
	}

	public void setxAlarmStartTime(String xAlarmStartTime) {
		this.xAlarmStartTime = xAlarmStartTime;
	}

	public NotifyEventEntity() {
	}

	public NotifyEventEntity(String username, String xCarNumber, String xAlarm, String xAlarmStartTime) {
		this.username = username;
		this.xCarNumber = xCarNumber;
		this.xAlarm = xAlarm;
		this.xAlarmStartTime = xAlarmStartTime;
	}
}
