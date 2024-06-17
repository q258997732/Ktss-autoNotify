package com.bob.autonotify.entity;

import java.util.List;

public class NotifyResponseEntity  {

	private String code;

	private List<NotifyEventEntity> data;

	private String message;

	public NotifyResponseEntity() {
	}

	public NotifyResponseEntity(String code, List<NotifyEventEntity> data,String message) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<NotifyEventEntity> getData() {
		return data;
	}

	public void setData(List<NotifyEventEntity> data) {
		this.data = data;
	}
}
