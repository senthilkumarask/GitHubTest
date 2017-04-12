package com.bbb.eph.vo;

import java.io.Serializable;

public class BBBProductEphCategoryMapVo implements Serializable{
	
	private String productId;
	private String eph;
	private String keyword;
	private String concept;
	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the eph
	 */
	public String getEph() {
		return eph;
	}
	/**
	 * @param eph the eph to set
	 */
	public void setEph(String eph) {
		this.eph = eph;
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
	
	
	
}
