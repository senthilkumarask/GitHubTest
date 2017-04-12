package com.bbb.search.vendor.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.search.bean.query.SortCriteria;

public class VendorRequestVO implements Serializable {

	private static final long serialVersionUID = -447241690626528007L;
	private String keyWord;
	private String narrowDown;
	private SortCriteria sortCriteria;
	private String pageSize;
	private String pageNum;
	private Map<String,String> catalogRef;
	private String siteId;
	private String hostname;
	private String groupId;
	private List<String> stopWrdRemovedString;
	private String partialFlag;
	private String mSearchMode;
	private String alternativePhrasing;
	private String alternativePhrasingResults;
	private String didYouMeanEnabled;
	private String typeOfResultReq;
	private boolean isPartialMatchSearch;
	private String storeId;
    private boolean onlineTab=false;
    private String storeIdFromAjax;
    
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getNarrowDown() {
		return narrowDown;
	}
	public void setNarrowDown(String narrowDown) {
		this.narrowDown = narrowDown;
	}
	public SortCriteria getSortCriteria() {
		return sortCriteria;
	}
	public void setSortCriteria(SortCriteria sortCriteria) {
		this.sortCriteria = sortCriteria;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getPageNum() {
		return pageNum;
	}
	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}
	
	public Map<String, String> getCatalogRef() {
		return catalogRef;
	}
	public void setCatalogRef(Map<String, String> catalogRef) {
		this.catalogRef = catalogRef;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public List<String> getStopWrdRemovedString() {
		return stopWrdRemovedString;
	}
	public void setStopWrdRemovedString(List<String> stopWrdRemovedString) {
		this.stopWrdRemovedString = stopWrdRemovedString;
	}
	public String getPartialFlag() {
		return partialFlag;
	}
	public void setPartialFlag(String partialFlag) {
		this.partialFlag = partialFlag;
	}
	public String getmSearchMode() {
		return mSearchMode;
	}
	public void setmSearchMode(String mSearchMode) {
		this.mSearchMode = mSearchMode;
	}
	public String getAlternativePhrasing() {
		return alternativePhrasing;
	}
	public void setAlternativePhrasing(String alternativePhrasing) {
		this.alternativePhrasing = alternativePhrasing;
	}
	public String getAlternativePhrasingResults() {
		return alternativePhrasingResults;
	}
	public void setAlternativePhrasingResults(String alternativePhrasingResults) {
		this.alternativePhrasingResults = alternativePhrasingResults;
	}
	public String getDidYouMeanEnabled() {
		return didYouMeanEnabled;
	}
	public void setDidYouMeanEnabled(String didYouMeanEnabled) {
		this.didYouMeanEnabled = didYouMeanEnabled;
	}
	public String getTypeOfResultReq() {
		return typeOfResultReq;
	}
	public void setTypeOfResultReq(String typeOfResultReq) {
		this.typeOfResultReq = typeOfResultReq;
	}
	public boolean isPartialMatchSearch() {
		return isPartialMatchSearch;
	}
	public void setPartialMatchSearch(boolean isPartialMatchSearch) {
		this.isPartialMatchSearch = isPartialMatchSearch;
	}
	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return storeId;
	}
	
	/**
	 * @param storeId the storeId to set
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	/**
	 * @return the onlineTab
	 */
	public boolean isOnlineTab() {
		return onlineTab;
	}
	
	/**
	 * @param onlineTab the onlineTab to set
	 */
	public void setOnlineTab(boolean onlineTab) {
		this.onlineTab = onlineTab;
	}
	
	/**
	 * @return the storeIdFromAjax
	 */
	public String getStoreIdFromAjax() {
		return storeIdFromAjax;
	}
	
	/**
	 * @param storeIdFromAjax the storeIdFromAjax to set
	 */
	public void setStoreIdFromAjax(String storeIdFromAjax) {
		this.storeIdFromAjax = storeIdFromAjax;
	}
}
