/**
 * 
 */
package com.bbb.search.endeca.vo;

import java.io.Serializable;
import java.util.List;


public class BBBDimension implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String rootDimName;
	private List<BBBDimVal> dimValList;
	
	private String rootDimId;
	
	public List<BBBDimVal> getDimValList() {
		return this.dimValList;
	}
	public void setDimValList(List<BBBDimVal> dimValList) {
		this.dimValList = dimValList;
	}
	public String getRootDimName() {
		return this.rootDimName;
	}
	public void setRootDimName(String rootDimName) {
		this.rootDimName = rootDimName;
	}
	
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the rootDimId
	 */
	public String getRootDimId() {
		return rootDimId;
	}
	/**
	 * @param rootDimId the rootDimId to set
	 */
	public void setRootDimId(String rootDimId) {
		this.rootDimId = rootDimId;
	}

	
	
	
	
}