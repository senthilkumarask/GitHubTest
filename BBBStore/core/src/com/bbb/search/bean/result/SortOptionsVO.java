package com.bbb.search.bean.result;

import java.io.Serializable;

public class SortOptionsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sortCode;
	private String sortValue;
	private String sortUrlParam;
	private String repositoryId;
	private Integer ascending;
	
	
	public String getSortUrlParam() {
		return sortUrlParam;
	}
	public void setSortUrlParam(String sortUrlParam) {
		this.sortUrlParam = sortUrlParam;
	}
	/**
	 * @return the sortCode
	 */
	public String getSortCode() {
		return sortCode;
	}
	/**
	 * @param sortCode the sortCode to set
	 */
	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}
	/**
	 * @return the sortValue
	 */
	public String getSortValue() {
		return sortValue;
	}
	/**
	 * @param sortValue the sortValue to set
	 */
	public void setSortValue(String sortValue) {
		this.sortValue = sortValue;
	}
	/**
	 * @return the repositoryId
	 */
	public String getRepositoryId() {
		return repositoryId;
	}
	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	public Integer getAscending() {
		return ascending;
	}
	public void setAscending(Integer ascending) {
		this.ascending = ascending;
	}
	 
	 
	
}
