package com.bob.autonotify.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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

	public void setCode(String code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setData(List<NotifyEventEntity> data) {
		this.data = data;
	}
}
