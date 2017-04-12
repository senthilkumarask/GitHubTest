package com.bbb.commerce.catalog.vo;

import java.io.Serializable;


public class StateVO implements Comparable<StateVO>,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stateCode;
	private String stateName;
	private boolean isNexusState;
	private volatile int hashCode = 0;
	private boolean isMilitaryState;
	private boolean showOnReg;
	private boolean showOnShipping;
	private boolean showOnBilling;
	
	public boolean isShowOnReg() {
		return showOnReg;
	}

	public void setShowOnReg(boolean showOnReg) {
		this.showOnReg = showOnReg;
	}

	public boolean isShowOnShipping() {
		return showOnShipping;
	}

	public void setShowOnShipping(boolean showOnShipping) {
		this.showOnShipping = showOnShipping;
	}

	public boolean isShowOnBilling() {
		return showOnBilling;
	}

	public void setShowOnBilling(boolean showOnBilling) {
		this.showOnBilling = showOnBilling;
	}
	

	public boolean isMilitaryState() {
		return isMilitaryState;
	}

	public void setMilitaryState(boolean isMilitaryState) {
		this.isMilitaryState = isMilitaryState;
	}

	public StateVO() {
		// TODO Auto-generated constructor stub
	}

	public StateVO(String stateCode,String stateName,boolean isNexusState,boolean isMilitaryState,boolean isShowOnReg, boolean isShowOnShipping, boolean isShowOnBilling) {
		this.stateCode = stateCode;
		this.stateName = stateName;
		this.isNexusState = isNexusState;
		this.isMilitaryState = isMilitaryState;
		this.showOnReg = isShowOnReg;
		this.showOnShipping  = isShowOnShipping;
		this.showOnBilling = isShowOnBilling;
	}


	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}

	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	/**
	 * @return the isNexusState
	 */
	public boolean isNexusState() {
		return isNexusState;
	}

	/**
	 * @param isNexusState the isNexusState to set
	 */
	public void setNexusState(boolean isNexusState) {
		this.isNexusState = isNexusState;
	}



	public int hashCode() {
		final int multiplier = 23; 
		if (hashCode == 0) { 
			int code = 133; 
			code = multiplier * code + stateCode.hashCode(); 
			code = multiplier * code + stateName.hashCode(); 
			code =  multiplier * code + (isNexusState ? 1231 : 1237); 
			hashCode = code; 
		} 
		return hashCode; 

	}


	public boolean equals(Object pObj) {

		if (pObj instanceof StateVO ) {
			StateVO stateVO = (StateVO) pObj;
			if (this.stateCode.equals(stateVO.stateCode) && this.stateName.equals(stateVO.stateName) && this.isNexusState == stateVO.isNexusState) 
			{
				return true;
			}
		}
		return false;

	}

	@Override
	public int compareTo(StateVO o) {

		int compareResult = stateName.compareTo( o.stateName);

		return compareResult;
	}
	public String toString(){
		StringBuffer toString=new StringBuffer(" State VO Details \n ");
		toString.append("State Code  ").append(stateCode).append("\n")
		.append(" State Name  ").append(stateName).append("\n")
		.append(" is Nexus State flag value ").append(isNexusState).append("\n");
		return toString.toString();
	}
}
