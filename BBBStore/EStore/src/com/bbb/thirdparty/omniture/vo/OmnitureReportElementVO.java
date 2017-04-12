package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class OmnitureReportElementVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 329985189924110493L;
	private String id;
	private String name;
	private String classification;
	private int top;
	private int startingWith;
	private OmnitureReportSearchVO search;
	
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
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	@Override
	public String toString() {
		return "OmnitureReportElementVO [id=" + id + ", name=" + name
				+ ", classification=" + classification + ", top=" + top +", startingWith= "+ startingWith + ", search=" + search + "]";
	}
	public OmnitureReportSearchVO getSearch() {
		return search;
	}
	public void setSearch(OmnitureReportSearchVO search) {
		this.search = search;
	}
	public int getStartingWith() {
		return startingWith;
	}
	public void setStartingWith(int startingWith) {
		this.startingWith = startingWith;
	}

}
