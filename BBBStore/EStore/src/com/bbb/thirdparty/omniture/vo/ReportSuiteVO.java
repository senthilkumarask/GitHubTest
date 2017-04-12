package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class ReportSuiteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3253010151228086123L;
	private String id;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "ReportSuiteVO [id=" + id + ", name=" + name + "]";
	}

}
