package com.bbb.certona.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import com.bbb.commerce.catalog.BBBCatalogConstants;

import atg.repository.RepositoryItem;

public class RebatesDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rebateId;
	private String rebateDescrip;
	private String rebateURL;
	private Timestamp rebateStartDate;
	private Timestamp rebateEndDate;
	private RepositoryItem rebateRepositoryItem;
	
	public RebatesDetails(RepositoryItem rebateRepositoryItem) {
		this.rebateRepositoryItem=rebateRepositoryItem;
	}
	/**
	 * @return the rebateId
	 */
	public String getRebateId() {
		if(this.rebateRepositoryItem!=null){
		return rebateRepositoryItem.getRepositoryId();
		} else{
			return this.rebateId;
		}
	}
	/**
	 * @param pRebateId the rebateId to set
	 */
	public void setRebateId(String pRebateId) {
		rebateId = pRebateId;
	}
	/**
	 * @return the rebateDescrip
	 */
	public String getRebateDescrip() {
		if(this.rebateRepositoryItem!=null && rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_REBATE_PROPERTY_NAME)!=null){
			return (String) rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_REBATE_PROPERTY_NAME);
		}
		else{
		return this.rebateDescrip;
		}
	}
	/**
	 * @param pRebateDescrip the rebateDescrip to set
	 */
	public void setRebateDescrip(String pRebateDescrip) {
		rebateDescrip = pRebateDescrip;
	}
	/**
	 * @return the rebateURL
	 */
	public String getRebateURL() {
		if(this.rebateRepositoryItem!=null && rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.REBATE_URL_REBATE_PROPERTY_NAME)!=null){
			return (String) rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.REBATE_URL_REBATE_PROPERTY_NAME);
		}
		else{
		return this.rebateURL;
		}
	}
	/**
	 * @param pRebateURL the rebateURL to set
	 */
	public void setRebateURL(String pRebateURL) {
		rebateURL = pRebateURL;
	}
	/**
	 * @return the rebateStartDate
	 */
	public Timestamp getRebateStartDate() {
		if(this.rebateRepositoryItem!=null && rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_REBATE_PROPERTY_NAME)!=null){
			return (Timestamp) rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_REBATE_PROPERTY_NAME);
		}
		else{
		return this.rebateStartDate;
		}
	}
	/**
	 * @param pRebateStartDate the rebateStartDate to set
	 */
	public void setRebateStartDate(Timestamp pRebateStartDate) {
		rebateStartDate = pRebateStartDate;
	}
	/**
	 * @return the rebateEndDate
	 */
	public Timestamp getRebateEndDate() {
		if(this.rebateRepositoryItem!=null && rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_REBATE_PROPERTY_NAME)!=null){
			return (Timestamp) rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_REBATE_PROPERTY_NAME);
		}
		else{
		return this.rebateEndDate;
		}
	}
	/**
	 * @param pRebateEndDate the rebateEndDate to set
	 */
	public void setRebateEndDate(Timestamp pRebateEndDate) {
		rebateEndDate = pRebateEndDate;
	}

}
