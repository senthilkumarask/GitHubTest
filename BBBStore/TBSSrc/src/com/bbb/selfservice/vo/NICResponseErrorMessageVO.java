package com.bbb.selfservice.vo;

import java.io.Serializable;

public class NICResponseErrorMessageVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 494552325318169556L;
	private String severity;
	private String code;
	private String description;
	
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
