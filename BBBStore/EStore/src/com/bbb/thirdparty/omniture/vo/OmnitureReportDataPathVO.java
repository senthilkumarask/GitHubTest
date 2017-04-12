package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class OmnitureReportDataPathVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2475925432162264628L;
	private String name;
	private String url;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "OmnitureReportDataPathVO [name=" + name + ", url=" + url + "]";
	}
	
}
