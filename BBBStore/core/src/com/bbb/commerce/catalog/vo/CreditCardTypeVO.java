/**
 * 
 */
package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;

/**
 * @author iteggi
 *
 */
public class CreditCardTypeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;
	private String imageURL;
	private String pattern;


	public CreditCardTypeVO() {
		//default constructor
	}
	public CreditCardTypeVO(RepositoryItem paymentCardRepoItem) {
		if(paymentCardRepoItem!=null){
			this.name=(String) paymentCardRepoItem.getPropertyValue(BBBCatalogConstants.CARD_NAME_CREDIT_CARD_PROPERTY_NAME);
			this.code=(String)paymentCardRepoItem.getPropertyValue(BBBCatalogConstants.CARD_CODE_CREDIT_CARD_PROPERTY_NAME);
			this.imageURL=(String)paymentCardRepoItem.getPropertyValue(BBBCatalogConstants.CARD_IMAGE_CREDIT_CARD_PROPERTY_NAME);
			this.pattern = (String)paymentCardRepoItem.getPropertyValue(BBBCatalogConstants.CARD_PATTERN_CREDIT_CARD_PROPERTY_NAME);
		}
	}
	
	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}
	
	/**
	 * @param pPattern the pattern to set
	 */
	public void setPattern(String pPattern) {
		this.pattern = pPattern;
	}
	
	
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param pCode the code to set
	 */
	public void setCode(String pCode) {
		code = pCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param pName the name to set
	 */
	public void setName(String pName) {
		name = pName;
	}

	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		return imageURL;
	}

	/**
	 * @param pImageURL the imageURL to set
	 */
	public void setImageURL(String pImageURL) {
		imageURL = pImageURL;
	}



}
