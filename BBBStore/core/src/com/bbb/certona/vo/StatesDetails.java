package com.bbb.certona.vo;

import java.io.Serializable;

//import com.bbb.commerce.catalog.BBBCatalogConstants;

import atg.repository.RepositoryItem;

public class StatesDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stateCode;
	private String stateName;
	private String countryCd;
	private boolean bopus;
	private RepositoryItem nonShippableStatesRepositoryItem;
	public StatesDetails(RepositoryItem nonShippableStatesRepositoryItem) {
		this.nonShippableStatesRepositoryItem=nonShippableStatesRepositoryItem;
	}
	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		if(this.nonShippableStatesRepositoryItem!=null){
			return nonShippableStatesRepositoryItem.getRepositoryId();
			} else{
				return this.stateCode;
			}
	}
	/**
	 * @param pStateCode the stateCode to set
	 */
	public void setStateCode(String pStateCode) {
		stateCode = pStateCode;
	}
	/**
	 * @return the stateName
	 */
	public String getStateName() {
		if(this.nonShippableStatesRepositoryItem!=null){
			return nonShippableStatesRepositoryItem.getRepositoryId();
			} else{
				return this.stateName;
			}
	}
	/**
	 * @param pStateName the stateName to set
	 */
	public void setStateName(String pStateName) {
		stateName = pStateName;
	}
	/**
	 * @return the countryCd
	 */
	public String getCountryCd() {
		if(this.nonShippableStatesRepositoryItem!=null && nonShippableStatesRepositoryItem.getPropertyValue("countryCd") != null){
			return (String) nonShippableStatesRepositoryItem.getPropertyValue("countryCd");
			} else{
				return this.countryCd;
			}
	}
	/**
	 * @param pCountryCd the countryCd to set
	 */
	public void setCountryCd(String pCountryCd) {
		countryCd = pCountryCd;
	}
	/**
	 * @return the bopus
	 */
	public boolean isBopus() {
		if(this.nonShippableStatesRepositoryItem!=null && nonShippableStatesRepositoryItem.getPropertyValue("bopus") != null){
			return (Boolean) nonShippableStatesRepositoryItem.getPropertyValue("bopus");
			} else{
				return this.bopus;
			}
	}
	/**
	 * @param pBopus the bopus to set
	 */
	public void setBopus(boolean pBopus) {
		bopus = pBopus;
	}

}
