package com.bbb.internationalshipping.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BBBInternationalCheckoutVO  implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String defaultCountryCode;
	String defaultCurrencyCode;
	String countryCode;
	String currencyCode;
	String countryName;

	public String getCountryName() {
		return countryName;
	}




	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}




	public String getCurrencyName() {
		return currencyName;
	}




	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}


	String currencyName;
	Boolean displayCountryCurrency;
	private List<BBBInternationalContextVO> contextList=new ArrayList<BBBInternationalContextVO>();
	private Map<String,String> currencyMap=new HashMap<String,String>();

	public static final String  DISPLAY_COUNTRY_CURRENCY="displayCountryCurrency";
	public static final String COUNTRY_CODE="countryCode";
	public static final String CURRENCY_CODE="currencyCode";
	public static final String ALL_CONTEXT_LIST="allContextList";
	public static final String ALL_CURRENCY_MAP="allCurrencyMap";




	public String getDefaultCountryCode() {
		return defaultCountryCode;
	}




	public void setDefaultCountryCode(String defaultCountryCode) {
		this.defaultCountryCode = defaultCountryCode;
	}




	public String getDefaultCurrencyCode() {
		return defaultCurrencyCode;
	}




	public void setDefaultCurrencyCode(String defaultCurrencyCode) {
		this.defaultCurrencyCode = defaultCurrencyCode;
	}




	public String getCountryCode() {
		return countryCode;
	}




	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}




	public String getCurrencyCode() {
		return currencyCode;
	}




	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}




	public Boolean getDisplayCountryCurrency() {
		return displayCountryCurrency;
	}




	public void setDisplayCountryCurrency(Boolean displayCountryCurrency) {
		this.displayCountryCurrency = displayCountryCurrency;
	}




	public List<BBBInternationalContextVO> getContextList() {
		return contextList;
	}




	public void setContextList(List<BBBInternationalContextVO> contextList) {
		this.contextList = contextList;
	}




	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}




	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

}
