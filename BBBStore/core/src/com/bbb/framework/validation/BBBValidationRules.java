/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 11-November-2011
 * --------------------------------------------------------------------------------
 */
package com.bbb.framework.validation;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;

public class BBBValidationRules {

	private String emailPattern;
	private String phonePattern;
	private String phoneExtPattern;
	private String orderPattern;
	private String alphaNumericPattern;
	private String onlyNumberPattern;
	private String nonPOBoxAddressPattern;
	private String addressLine1Pattern;
	private String addressLine2Pattern;
	private String companyNamePattern;
	private String cityNamePattern;
	private String numericOnlyPattern;
	private String namePattern;
	private String mxNamePattern;
	private String zipCodePattern;
	private String passwordPattern;
	private String crossSiteScriptingPattern;
	private String nameOnCardPattern;
	private String giftMessagePattern;
	private String alphabetOnlyPattern;
	private String statePattern;
	private String registryNamePattern;
	private String zipCanadaCodePattern;
	
	private String namePatternComlement;
	private String addressLine1PatternComplement;
	private String cityPatternComplement;
	private BBBCatalogTools catalogTools;
	private String collegeNameMaxLength;
	
	public String getCityPatternComplement() {
		return cityPatternComplement;
	}

	public void setCityPatternComplement(String cityPatternComplement) {
		this.cityPatternComplement = cityPatternComplement;
	}

	public String getAddressLine1PatternComplement() {
		return addressLine1PatternComplement;
	}

	public void setAddressLine1PatternComplement(
			String addressLine1PatternComplement) {
		this.addressLine1PatternComplement = addressLine1PatternComplement;
	}

	public String getAddressLine2PatternComplement() {
		return addressLine2PatternComplement;
	}

	public void setAddressLine2PatternComplement(
			String addressLine2PatternComplement) {
		this.addressLine2PatternComplement = addressLine2PatternComplement;
	}

	private String addressLine2PatternComplement;
	
	public String getNamePatternComlement() {
		return namePatternComlement;
	}

	public void setNamePatternComlement(String namePatternComlement) {
		this.namePatternComlement = namePatternComlement;
	}

	
    public String getStatePattern() {
		return statePattern;
	}

	public void setStatePattern(String statePatern) {
		this.statePattern = statePatern;
	}

	
	public String getGiftMessagePattern() {
		return giftMessagePattern;
	}

	public void setGiftMessagePattern(String giftMessagePatern) {
		this.giftMessagePattern = giftMessagePatern;
	}

	/**
	 * @return the crossSiteScriptingPattern
	 */
	public String getCrossSiteScriptingPattern() {
		return crossSiteScriptingPattern;
	}
	
	/**
	 * @param crossSiteScriptingPattern the crossSiteScriptingPattern to set
	 */
	public void setCrossSiteScriptingPattern(String crossSiteScriptingPattern) {
		this.crossSiteScriptingPattern = crossSiteScriptingPattern;
	}

	/**
	 * @return the passwordPattern
	 */
	public String getPasswordPattern() {
		return passwordPattern;
	}

	/**
	 * @param passwordPattern the passwordPattern to set
	 */
	public void setPasswordPattern(String passwordPattern) {
		this.passwordPattern = passwordPattern;
	}

	/**
	 * @return the addressLine1Pattern
	 */
	public String getAddressLine1Pattern() {
		return addressLine1Pattern;
	}

	/**
	 * @return the addressLine2Pattern
	 */
	public String getAddressLine2Pattern() {
		return addressLine2Pattern;
	}

	/**
	 * @return the companyNamePattern
	 */
	public String getCompanyNamePattern() {
		return companyNamePattern;
	}

	/**
	 * @param pAddressLine1Pattern
	 *            the addressLine1Pattern to set
	 */
	public void setAddressLine1Pattern(String pAddressLine1Pattern) {
		addressLine1Pattern = pAddressLine1Pattern;
	}

	/**
	 * @param pAddressLine2Pattern
	 *            the addressLine2Pattern to set
	 */
	public void setAddressLine2Pattern(String pAddressLine2Pattern) {
		addressLine2Pattern = pAddressLine2Pattern;
	}

	/**
	 * @param pCompanyNamePattern
	 *            the companyNamePattern to set
	 */
	public void setCompanyNamePattern(String pCompanyNamePattern) {
		companyNamePattern = pCompanyNamePattern;
	}

	/**
	 * @return the emailPattern
	 */
	public String getEmailPattern() {
		return emailPattern;
	}

	/**
	 * @return the phonePattern
	 */
	public String getPhonePattern() {
		return phonePattern;
	}

	/**
	 * @param pEmailPattern
	 *            the emailPattern to set
	 */
	public void setEmailPattern(String pEmailPattern) {
		emailPattern = pEmailPattern;
	}

	/**
	 * @param pPhonePattern
	 *            the phonePattern to set
	 */
	public void setPhonePattern(String pPhonePattern) {
		phonePattern = pPhonePattern;
	}

	/**
	 * @return the orderPattern
	 */
	public String getOrderPattern() {
		return orderPattern;
	}

	/**
	 * @param pOrderPattern
	 *            the orderPattern to set
	 */
	public void setOrderPattern(String pOrderPattern) {
		orderPattern = pOrderPattern;
	}

	/**
	 * @return the alphaNumericPattern
	 */
	public String getAlphaNumericPattern() {
		return alphaNumericPattern;
	}

	/**
	 * @param alphaNumericPattern
	 *            the alphaNumericPattern to set
	 */
	public void setAlphaNumericPattern(String alphaNumericPattern) {
		this.alphaNumericPattern = alphaNumericPattern;
	}

	/**
	 * @return the phoneExtPattern
	 */
	public String getPhoneExtPattern() {
		return phoneExtPattern;
	}

	/**
	 * @param pPhoneExtPattern
	 *            the phoneExtPattern to set
	 */
	public void setPhoneExtPattern(String pPhoneExtPattern) {
		phoneExtPattern = pPhoneExtPattern;
	}

	/**
	 * @return the onlyNumberPattern
	 */
	public String getOnlyNumberPattern() {
		return onlyNumberPattern;
	}

	/**
	 * @param pOnlyNumberPattern
	 *            the onlyNumberPattern to set
	 */
	public void setOnlyNumberPattern(String pOnlyNumberPattern) {
		onlyNumberPattern = pOnlyNumberPattern;
	}

	/**
	 * @return the cityNamePattern
	 */
	public String getCityNamePattern() {
		return cityNamePattern;
	}

	/**
	 * @param pCityNamePattern
	 *            the cityNamePattern to set
	 */
	public void setCityNamePattern(String pCityNamePattern) {
		cityNamePattern = pCityNamePattern;
	}

	/**
	 * @return the numericOnlyPattern
	 */
	public String getNumericOnlyPattern() {
		return numericOnlyPattern;
	}

	/**
	 * @param numericOnlyPattern
	 *            the numericOnlyPattern to set
	 */
	public void setNumericOnlyPattern(String numericOnlyPattern) {
		this.numericOnlyPattern = numericOnlyPattern;
	}

	/**
	 * @return the namePattern
	 */
	public String getNamePattern() {
		return namePattern;
	}

	/**
	 * @param namePattern
	 *            the namePattern to set
	 */
	public void setNamePattern(String namePattern) {
		this.namePattern = namePattern;
	}

	/**
	 * @return the mxNamePattern
	 */
	public String getMxNamePattern() {
		return mxNamePattern;
	}
	/**
	 * @param namePattern
	 *            the mxNamePattern to set
	 */
	public void setMxNamePattern(String mxNamePattern) {
		this.mxNamePattern = mxNamePattern;
	}
	
	/**
	 * @return the zipCodePattern
	 */
	public String getZipCodePattern() {
		return zipCodePattern;
	}

	/**
	 * @param zipCodePattern
	 *            the zipCodePattern to set
	 */
	public void setZipCodePattern(String zipCodePattern) {
		this.zipCodePattern = zipCodePattern;
	}

	/**
	 * @return the nonPOBoxAddressPattern
	 */
	public final String getNonPOBoxAddressPattern() {
		return nonPOBoxAddressPattern;
	}

	/**
	 * @param pNonPOBoxAddressPattern the nonPOBoxAddressPattern to set
	 */
	public final void setNonPOBoxAddressPattern(String pNonPOBoxAddressPattern) {
		nonPOBoxAddressPattern = pNonPOBoxAddressPattern;
	}
	
	/**
	 * @return the nameOnCardPattern
	 */
	public String getNameOnCardPattern() {
		return nameOnCardPattern;
	}
	
	/**
	 * @param nameOnCardPattern the nameOnCardPattern to sets
	 */
	public void setNameOnCardPattern(String nameOnCardPattern) {
		this.nameOnCardPattern = nameOnCardPattern;
	}

	/**
	 * @return the alphabetOnlyPattern
	 */
	public String getAlphabetOnlyPattern() {
		return alphabetOnlyPattern;
	}

	/**
	 * @param alphabetOnlyPattern the alphabetOnlyPattern to set
	 */
	public void setAlphabetOnlyPattern(String alphabetOnlyPattern) {
		this.alphabetOnlyPattern = alphabetOnlyPattern;
	}

	/**
	 * @return the registryNamePattern
	 */
	public String getRegistryNamePattern() {
		return registryNamePattern;
	}

	/**
	 * @param registryNamePattern the registryNamePattern to set
	 */
	public void setRegistryNamePattern(String registryNamePattern) {
		this.registryNamePattern = registryNamePattern;
	}

	public String getZipCanadaCodePattern() {
		return zipCanadaCodePattern;
	}

	public void setZipCanadaCodePattern(String zipCanadaCodePattern) {
		this.zipCanadaCodePattern = zipCanadaCodePattern;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

   public String getCollegeNameMaxLength() {
		
		collegeNameMaxLength = getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.COLLEGENAME_MAXLENGTH,BBBCoreConstants.TWENTY_NINE);
		return collegeNameMaxLength;
	}
	

	public void setCollegeNameMaxLength(String collegeNameMaxLength) {
		this.collegeNameMaxLength = collegeNameMaxLength;
	}
	
	
    
	
}