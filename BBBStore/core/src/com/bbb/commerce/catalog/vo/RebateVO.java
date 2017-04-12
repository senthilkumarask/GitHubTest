package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

import com.bbb.commerce.catalog.BBBCatalogConstants;

import atg.repository.RepositoryItem;
/**
 * 
 * @author njai13
 *
 */
public class RebateVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rebateId;
	private String rebateDescription;
	private String rebateURL;
	private RepositoryItem rebateRepositoryItem;
	public RebateVO() {
		//default constructor
	}

	public RebateVO(RepositoryItem rebateRepositoryItem) {
		this.rebateRepositoryItem=rebateRepositoryItem;
	}

	/**
	 * @return the rebateId
	 */
	public String getRebateId() {
		if(this.rebateRepositoryItem!=null){
			return rebateRepositoryItem.getRepositoryId();
		}
		else{
			return this.rebateId;
		}
	}

	/**
	 * @param rebateId the rebateId to set
	 */
	public void setRebateId(String rebateId) {
		this.rebateId = rebateId;
	}

	/**
	 * @return the rebateDescription
	 */
	public String getRebateDescription() {
		if(this.rebateRepositoryItem!=null && rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_REBATE_PROPERTY_NAME)!=null){
			return (String) rebateRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_REBATE_PROPERTY_NAME);
		}
		else{
			return this.rebateDescription;
		}
	}

	/**
	 * @param rebateDescription the rebateDescription to set
	 */
	public void setRebateDescription(String rebateDescription) {
		this.rebateDescription = rebateDescription;
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
	 * @param rebateURL the rebateURL to set
	 */
	public void setRebateURL(String rebateURL) {
		this.rebateURL = rebateURL;
	}
	public String toString(){
		StringBuffer toString=new StringBuffer(" Rebate VO Details \n ");
		if(rebateRepositoryItem!=null){
			toString.append("Rebate Id  ").append(rebateId).append("\n")
			.append(" Rebate Description  ").append(rebateDescription).append("\n")
			.append(" Rebate URL ").append(rebateURL).append("\n");
		}
		else{
			toString.append(" Rebate Details not set/available ");
		}
		return toString.toString();
	}
}
