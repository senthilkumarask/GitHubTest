package com.bbb.personalstore.vo;

import java.io.Serializable;

import atg.repository.RepositoryItem;

/**
 * Response VO for Strategy Details & Cookie 
 * @author ssard1
 *
 */
public class StrategyResponseVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RepositoryItem mStrategyDetails;
	
	private String mStrategyCookieValue;

	/**
	 * This method returns the strategy details
	 * @return the mStrategyDetails
	 */
	public RepositoryItem getStrategyDetails() {
		return mStrategyDetails;
	}

	/**
	 * @param mStrategyDetails the mStrategyDetails to set
	 */
	public void setStrategyDetails(RepositoryItem mStrategyDetails) {
		this.mStrategyDetails = mStrategyDetails;
	}

	/**
	 * @return the mStrategyCookieValue
	 */
	public String getStrategyCookieValue() {
		return mStrategyCookieValue;
	}

	/**
	 * @param mStrategyCookieValue the mStrategyCookieValue to set
	 */
	public void setStrategyCookieValue(String mStrategyCookieValue) {
		this.mStrategyCookieValue = mStrategyCookieValue;
	}


}
