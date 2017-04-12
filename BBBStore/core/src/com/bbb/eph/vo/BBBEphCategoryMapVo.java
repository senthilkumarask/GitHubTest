package com.bbb.eph.vo;

import java.io.Serializable;

public class BBBEphCategoryMapVo implements Serializable{
	private String EPHList;
	private String categoryList;
	private String concept;
	private String keyword;
	private String id;
	/**
	 * @return the ePHList
	 */
	public String getEPHList() {
		return EPHList;
	}
	/**
	 * @param ePHList the ePHList to set
	 */
	public void setEPHList(String ePHList) {
		EPHList = ePHList;
	}
	/**
	 * @return the categoryList
	 */
	public String getCategoryList() {
		return categoryList;
	}
	/**
	 * @param categoryList the categoryList to set
	 */
	public void setCategoryList(String categoryList) {
		this.categoryList = categoryList;
	}
	/**
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}
	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
	}
	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}
	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
		
	
}
