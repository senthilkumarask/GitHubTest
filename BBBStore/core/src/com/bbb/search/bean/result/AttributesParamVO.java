package com.bbb.search.bean.result;

import java.io.Serializable;

import com.bbb.search.bean.query.SearchQuery;

public class AttributesParamVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private SearchQuery searchQuery;
	private String cacheName;
	int cacheTimeout;
	private SearchResults searchResults;
	boolean isPartialMatchSearch;
	private String boostCode;
	boolean boostFlag;
	
	/**
	 * @return the searchQuery
	 */
	public SearchQuery getSearchQuery() {
		return searchQuery;
	}
	/**
	 * @param searchQuery the searchQuery to set
	 */
	public void setSearchQuery(SearchQuery searchQuery) {
		this.searchQuery = searchQuery;
	}
	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return cacheName;
	}
	/**
	 * @param cacheName the cacheName to set
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	/**
	 * @return the cacheTimeout
	 */
	public int getCacheTimeout() {
		return cacheTimeout;
	}
	/**
	 * @param cacheTimeout the cacheTimeout to set
	 */
	public void setCacheTimeout(int cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}
	/**
	 * @return the searchResults
	 */
	public SearchResults getSearchResults() {
		return searchResults;
	}
	/**
	 * @param searchResults the searchResults to set
	 */
	public void setSearchResults(SearchResults searchResults) {
		this.searchResults = searchResults;
	}
	/**
	 * @return the isPartialMatchSearch
	 */
	public boolean isPartialMatchSearch() {
		return isPartialMatchSearch;
	}
	/**
	 * @param isPartialMatchSearch the isPartialMatchSearch to set
	 */
	public void setPartialMatchSearch(boolean isPartialMatchSearch) {
		this.isPartialMatchSearch = isPartialMatchSearch;
	}
	/**
	 * @return the boostCode
	 */
	public String getBoostCode() {
		return boostCode;
	}
	/**
	 * @param boostCode the boostCode to set
	 */
	public void setBoostCode(String boostCode) {
		this.boostCode = boostCode;
	}
	/**
	 * @return the boostFlag
	 */
	public boolean isBoostFlag() {
		return boostFlag;
	}
	/**
	 * @param boostFlag the boostFlag to set
	 */
	public void setBoostFlag(boolean boostFlag) {
		this.boostFlag = boostFlag;
	}
}
