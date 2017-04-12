package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

public class CategoryMappingVo implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String usUrl;
	private String canadaUrl;
	private String usCategoryId ;
	private String canadaCategoryId;
	/**
	 * @return the usUrl
	 */
	public String getUsUrl() {
		return usUrl;
	}
	/**
	 * @param usUrl the usUrl to set
	 */
	public void setUsUrl(String usUrl) {
		this.usUrl = usUrl;
	}
	/**
	 * @return the canadaUrl
	 */
	public String getCanadaUrl() {
		return canadaUrl;
	}
	/**
	 * @param canadaUrl the canadaUrl to set
	 */
	public void setCanadaUrl(String canadaUrl) {
		this.canadaUrl = canadaUrl;
	}
	/**
	 * @return the usCategoryId
	 */
	public String getUsCategoryId() {
		return usCategoryId;
	}
	/**
	 * @param usCategoryId the usCategoryId to set
	 */
	public void setUsCategoryId(String usCategoryId) {
		this.usCategoryId = usCategoryId;
	}
	/**
	 * @return the canadaCategoryId
	 */
	public String getCanadaCategoryId() {
		return canadaCategoryId;
	}
	/**
	 * @param canadaCategoryId the canadaCategoryId to set
	 */
	public void setCanadaCategoryId(String canadaCategoryId) {
		this.canadaCategoryId = canadaCategoryId;
	}
	
}
