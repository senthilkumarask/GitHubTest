/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.utils.BBBUtility;


// TODO: Auto-generated Javadoc
/**
 * The Class CurrentDescriptorVO.
 *
 * @author agupt8
 */
public class CurrentDescriptorVO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The name. */
	private String name;
	
	/** The removal query. */
	private String removalQuery;
	
	/** The root name. */
	private String rootName;
	
	/** The category id. */
	private String categoryId;
	
	/** The root category id. */
	private String rootCategoryId;
	
	/** The descriptor filter. */
	private String descriptorFilter;
	
	/** The narrow down filter. */
	private Map<String,String> narrowDownFilter;
	
	/** ancestor name of current dimension */
	private String ancestorName;
	
	private String refinedName;
	
	private boolean multiSelect;
	
	/**
	 * @return the multiSelect
	 */
	public boolean isMultiSelect() {
		return multiSelect;
	}

	/**
	 * @param multiSelect the multiSelect to set
	 */
	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	/**
	 * @return the refinedName
	 */
	public String getRefinedName() {
		return refinedName;
	}

	/**
	 * @param refinedName the refinedName to set
	 */
	public void setRefinedName(String refinedName) {
		this.refinedName = refinedName;
	}

	/**
	 * Gets the descriptor filter.
	 *
	 * @return the descriptorFilter
	 */
	public String getDescriptorFilter() {
		return descriptorFilter;
	}
	
	/**
	 * Sets the descriptor filter.
	 *
	 * @param descriptorFilter the descriptorFilter to set
	 */
	public void setDescriptorFilter(String descriptorFilter) {
		this.descriptorFilter = descriptorFilter;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name for US and Mexico countries and localize the price ranges for other countries
	 */
	public String getName() {
		
			String dimName =this.name;
			String country=null;
			//check for price  range facets and for mexico specific facets
	    	 if(!BBBUtility.isEmpty(getRootName())  && getRootName().equalsIgnoreCase(BBBInternationalShippingConstants.PRICE_RANGE) 
	    			 && !dimName.contains(BBBInternationalShippingConstants.CURRENCY_MEXICO)){
	    		 DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
	    		 if(pRequest!=null ){
		 			 Profile profileFromReq = (Profile)pRequest.getAttribute(BBBInternationalShippingConstants.PROFILE);
		 			 if(profileFromReq == null){
		 				profileFromReq = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		 				pRequest.setAttribute(BBBInternationalShippingConstants.PROFILE, profileFromReq);
		 			}
		 			if(profileFromReq!=null){
		 				country=(String) profileFromReq.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
		 			}
	    		 }
	 			//check for country is not US or blank,tehn only localize the prices
	 			if(BBBUtility.isNotEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY)){
		 			Properties prop = new Properties();
		 			prop.setProperty(BBBInternationalShippingConstants.ROUND,BBBInternationalShippingConstants.DOWN);
		 			Pattern pattern = Pattern.compile(BBBCoreConstants.PATTERN_FORMAT);
		 			Matcher matcher = null;
		 			matcher = pattern.matcher(dimName);
		 			int i=0;
		 			String lowPrice=BBBCoreConstants.BLANK,highPrice=BBBCoreConstants.BLANK;
		 			while ( matcher.find()) {
		 			  if(i==0){
		 				 lowPrice = matcher.group();
		 				 lowPrice=lowPrice.substring(1);
		 				  i++;
		 			  }
		 			  else if(i==1){
		 				 highPrice=matcher.group();
		 			  	 highPrice=highPrice.substring(1);
		 			  }
		 			  
		 			}
		 	       dimName=BBBUtility.convertToInternationalPrice(dimName,lowPrice, highPrice, prop);
	 			}
	    	 }
	    	 return dimName;
	}
	
	/**
	 * Gets the root name.
	 *
	 * @return the rootName
	 */
	public String getRootName() {
		return rootName;
	}
	
	/**
	 * Sets the root name.
	 *
	 * @param rootName the rootName to set
	 */
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Gets the removal query.
	 *
	 * @return the removalQuery
	 */
	public String getRemovalQuery() {
		return removalQuery;
	}
	
	/**
	 * Sets the removal query.
	 *
	 * @param removalQuery the removalQuery to set
	 */
	public void setRemovalQuery(final String removalQuery) {
		this.removalQuery = removalQuery;
	}
	
	/**
	 * Gets the category id.
	 *
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}
	
	/**
	 * Sets the category id.
	 *
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	/**
	 * Gets the root category id.
	 *
	 * @return the rootCategoryId
	 */
	public String getRootCategoryId() {
		return rootCategoryId;
	}
	
	/**
	 * Sets the root category id.
	 *
	 * @param rootCategoryId the rootCategoryId to set
	 */
	public void setRootCategoryId(String rootCategoryId) {
		this.rootCategoryId = rootCategoryId;
	}
	
	/**
	 * Gets the narrow down filter.
	 *
	 * @return the narrow down filter
	 */
	public Map<String, String> getNarrowDownFilter() {
		return narrowDownFilter;
	}

	/**
	 * Sets the narrow down filter.
	 *
	 * @param narrowDownFilter the narrow down filter
	 */
	public void setNarrowDownFilter(Map<String, String> narrowDownFilter) {
		this.narrowDownFilter = narrowDownFilter;
	}

	/**
	 * @return the ancestorName
	 */
	public String getAncestorName() {
		return ancestorName;
	}

	/**
	 * @param ancestorName the ancestorName to set
	 */
	public void setAncestorName(String ancestorName) {
		this.ancestorName = ancestorName;
	}
	
}
