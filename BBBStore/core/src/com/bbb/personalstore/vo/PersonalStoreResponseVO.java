package com.bbb.personalstore.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import atg.repository.RepositoryItem;

/**
 * Response VO for Personal Store Template
 * @author rjain40
 *
 */
public class PersonalStoreResponseVO  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<RepositoryItem> mStrategyDetails;
	
	private Map<String,String> mStrategyJSPMap;
	
	private String mPersonalStoreTitle;
	
	private Map<String,String> mStrategyContextMap;
	
	/**
	 * @return the mStrategyContextMap
	 */
	public Map<String, String> getStrategyContextMap() {
		return mStrategyContextMap;
	}

	/**
	 * @param mStrategyContextMap the mStrategyContextMap to set
	 */
	public void setStrategyContextMap(Map<String, String> mStrategyContextMap) {
		this.mStrategyContextMap = mStrategyContextMap;
	}

	/**
	 * This method returns <code>List</code> contains the strategy details
	 * for Personal Store
	 * 
	 * @return the strategyDetails in <code>List</code> format
	 *
	 */
	public List<RepositoryItem> getStrategyDetails() {
		return mStrategyDetails;
	}
	
	/**
	 * This method sets the strategyDetails
	 * 
	 * @param strategyDetails the strategyDetails to set
	 */
	public void setStrategyDetails(final List<RepositoryItem> strategyDetails) {
		this.mStrategyDetails = strategyDetails;
	}
	
	/**
	 * This method returns <code>Map</code> contains the map of strategy and 
	 * corresponding layout jsp
	 * 
	 * @return the strategyJSPMap in <code>Map</code> format
	 */
	public Map<String, String> getStrategyJSPMap() {
		return mStrategyJSPMap;
	}
	
	/**
	 * This method sets the strategy - JSPMap
	 * 
	 * @param strategyJSPMap the strategyJSPMap to set
	 */
	public void setStrategyJSPMap(final Map<String, String> strategyJSPMap) {
		this.mStrategyJSPMap = strategyJSPMap;
	}
	
	/**
	 * @return the mPersonalStoreTitle
	 */
	public String getPersonalStoreTitle() {
		return mPersonalStoreTitle;
	}

	/**
	 * @param mPersonalStoreTitle the mPersonalStoreTitle to set
	 */
	public void setPersonalStoreTitle(String mPersonalStoreTitle) {
		this.mPersonalStoreTitle = mPersonalStoreTitle;
	}
	
}
