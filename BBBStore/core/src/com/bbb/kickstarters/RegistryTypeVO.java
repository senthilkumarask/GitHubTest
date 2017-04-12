package com.bbb.kickstarters;

import java.io.Serializable;


public class RegistryTypeVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String registeryType;
	private String registryTypeimageUrl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRegistryTypeimageUrl() {
		return registryTypeimageUrl;
	}
	public void setRegistryTypeimageUrl(String registryTypeimageUrl) {
		this.registryTypeimageUrl = registryTypeimageUrl;
	}
	public String getRegisteryType() {
		return registeryType;
	}
	public void setRegisteryType(String registeryType) {
		this.registeryType = registeryType;
	}
	
}