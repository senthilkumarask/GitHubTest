package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class OmnitureReportSegmentVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6395677622752866622L;
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
		return "OmnitureReportSegmentVO [id=" + id + ", name=" + name + "]";
	}
	
}
