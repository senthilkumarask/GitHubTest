package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class OmnitureReportMetricVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3191199602691023970L;
	private String id;
	private String name;
	private String type;
	private int decimals;
	private String forumula;
	private int latency;
	private boolean current;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getDecimals() {
		return decimals;
	}
	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}
	public String getForumula() {
		return forumula;
	}
	public void setForumula(String forumula) {
		this.forumula = forumula;
	}
	public int getLatency() {
		return latency;
	}
	public void setLatency(int latency) {
		this.latency = latency;
	}
	public boolean isCurrent() {
		return current;
	}
	public void setCurrent(boolean current) {
		this.current = current;
	}
	@Override
	public String toString() {
		return "OmnitureReportMetricVO [id=" + id + ", name=" + name
				+ ", type=" + type + ", decimals=" + decimals + ", forumula="
				+ forumula + ", latency=" + latency + ", current=" + current
				+ "]";
	}

}
