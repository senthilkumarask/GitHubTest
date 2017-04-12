package com.bbb.search.vendor.customizer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bbb.common.BBBGenericService;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.DisplayPropertiesVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.utils.BBBUtility;

public class VendorResponseDisplayPropsCustomizer extends BBBGenericService implements VendorResponseCustomizer{

	private static final String IS_COLON = " is :";
	private static final String DEFAULT_MAP_KEY = "DEFAULT";
	private Map<String,String> facetListStyleMap;
	private Map<String,String> fieldsetClassMap;
	private Map<String,String> offScreenMap;
	private Map<String,String> divClassMap;
	private Map<String,String> titleMap;
	private Map<String,String> displayNameMap;
	private static final String CLASS_NAME = "VendorResponseDisplayPropsCustomizer";
	/**
	 * @return the facetListStyleMap
	 */
	public Map<String, String> getFacetListStyleMap() {
		return facetListStyleMap;
	}

	/**
	 * @param facetListStyleMap the facetListStyleMap to set
	 */
	public void setFacetListStyleMap(Map<String, String> facetListStyleMap) {
		this.facetListStyleMap = facetListStyleMap;
	}

	/**
	 * @return the fieldsetClassMap
	 */
	public Map<String, String> getFieldsetClassMap() {
		return fieldsetClassMap;
	}

	/**
	 * @param fieldsetClassMap the fieldsetClassMap to set
	 */
	public void setFieldsetClassMap(Map<String, String> fieldsetClassMap) {
		this.fieldsetClassMap = fieldsetClassMap;
	}

	/**
	 * @return the offScreenMap
	 */
	public Map<String, String> getOffScreenMap() {
		return offScreenMap;
	}

	/**
	 * @param offScreenMap the offScreenMap to set
	 */
	public void setOffScreenMap(Map<String, String> offScreenMap) {
		this.offScreenMap = offScreenMap;
	}

	/**
	 * @return the divClassMap
	 */
	public Map<String, String> getDivClassMap() {
		return divClassMap;
	}

	/**
	 * @param divClassMap the divClassMap to set
	 */
	public void setDivClassMap(Map<String, String> divClassMap) {
		this.divClassMap = divClassMap;
	}

	/**
	 * @return the titleMap
	 */
	public Map<String, String> getTitleMap() {
		return titleMap;
	}

	/**
	 * @param titleMap the titleMap to set
	 */
	public void setTitleMap(Map<String, String> titleMap) {
		this.titleMap = titleMap;
	}

	/**
	 * @return the displayNameMap
	 */
	public Map<String, String> getDisplayNameMap() {
		return displayNameMap;
	}

	/**
	 * @param displayNameMap the displayNameMap to set
	 */
	public void setDisplayNameMap(Map<String, String> displayNameMap) {
		this.displayNameMap = displayNameMap;
	}
	
	/**
	 * This is overridden method to customize the Vendor Response. This method is used to set the Display 
	 * properties like div class, fieldset etc for different facets
	 * 
	 * @param browseSearchVO
	 * @param pSearchQuery
	 * @throws None
	 */
	@Override
	public void customizeResponse(final SearchResults browseSearchVO, final SearchQuery pSearchQuery){
		BBBPerformanceMonitor.start(CLASS_NAME,
				"customizeResponse");
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseDisplayPropsCustomizer.customizeResponse() method Parameters :: Search Keyword -" 
			+ pSearchQuery.getKeyWord());
		}
		
		String facetName;
		List<FacetParentVO> facetParentVOs;
		boolean isEmptyFacets = browseSearchVO.isEmptyFacets();
		if(isEmptyFacets){
			if(isLoggingDebug()){
				logDebug("Empty facet flag is true. So set the Facets to the Empty Facets list");
			}
			facetParentVOs = browseSearchVO.getEmptyFacetsList();
		}else{
			facetParentVOs = browseSearchVO.getFacets();
		}
		
		Iterator<FacetParentVO> facetItr = facetParentVOs.iterator();
		while(facetItr.hasNext()){
			FacetParentVO facetVO = facetItr.next();
			DisplayPropertiesVO displayVO = new DisplayPropertiesVO();
			facetName = facetVO.getName();
			//to -do This pieace code may be not required for vendor , will check once we will have the final response
			setFacetListDisplayProperties(facetName, displayVO);
			setFieldSetDisplayProperties(facetName, displayVO);
			setOffscreenDisplayProperties(facetName, displayVO);
			setDivClassDisplayProperties(facetName, displayVO);
			setTitleDisplayProperties(facetName, displayVO);
			setNameDisplayProperties(facetName, displayVO);
			facetVO.setDisplayProperties(displayVO);
		}
		if(isLoggingDebug()){
			logDebug("[END] VendorResponseDisplayPropsCustomizer.customizeResponse() method");
		}
		BBBPerformanceMonitor.end(CLASS_NAME,
				"customizeResponse");
	}
	
	/**
	 * This method is used to set the FacetListStyle Property for different facets
	 * 
	 * @param facetName
	 * @param displayVO
	 * @throws None
	 */
	private void setFacetListDisplayProperties(final String facetName, final DisplayPropertiesVO displayVO){
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseDisplayPropsCustomizer.setFacetListDisplayProperties() method");
		}
		String facetListValue = "";
		String mapValue = getFacetListStyleMap().get(facetName);
		
		if(BBBUtility.isEmpty(mapValue)){
			String mapDefaultValue = getFacetListStyleMap().get(DEFAULT_MAP_KEY);
			if(BBBUtility.isNotEmpty(mapDefaultValue)){
				facetListValue = mapDefaultValue;
			}
		}else{
			facetListValue = mapValue;
		}
		
		displayVO.setFacetListStyle(facetListValue);
		if(isLoggingDebug()){
			logDebug("The value of facetListStyle for facet : " + facetName + IS_COLON + facetListValue + 
					"[END] VendorResponseDisplayPropsCustomizer.setFacetListDisplayProperties() method");
		}
	}
	
	/**
	 * This method is used to set the FieldSetClass Property for different facets
	 * 
	 * @param facetName
	 * @param displayVO
	 * @throws None
	 */
	private void setFieldSetDisplayProperties(final String facetName, final DisplayPropertiesVO displayVO){
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseDisplayPropsCustomizer.setFieldSetDisplayProperties() method");
		}
		String fieldSetValue = "";
		String mapValue = getFieldsetClassMap().get(facetName);
		
		if(BBBUtility.isEmpty(mapValue)){
			String mapDefaultValue = getFieldsetClassMap().get(DEFAULT_MAP_KEY);
			if(BBBUtility.isNotEmpty(mapDefaultValue)){
				fieldSetValue = mapDefaultValue;
			}
		}else{
			fieldSetValue = mapValue;
		}
		
		displayVO.setFieldsetClass(fieldSetValue);
		if(isLoggingDebug()){
			logDebug("The value of FieldsetClass for facet : " + facetName + IS_COLON + fieldSetValue + 
					".[END] VendorResponseDisplayPropsCustomizer.setFieldSetDisplayProperties() method");
		}
	}
	
	/**
	 * This method is used to set the Div Class for different facets
	 * 
	 * @param facetName
	 * @param displayVO
	 * @throws None
	 */
	private void setDivClassDisplayProperties(final String facetName, final DisplayPropertiesVO displayVO){
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseDisplayPropsCustomizer.setDivClassDisplayProperties() method");
		}
		String divClassValue = "";
		String mapValue = getDivClassMap().get(facetName);
		
		if(BBBUtility.isEmpty(mapValue)){
			String mapDefaultValue = getDivClassMap().get(DEFAULT_MAP_KEY);
			if(BBBUtility.isNotEmpty(mapDefaultValue)){
				divClassValue = mapDefaultValue;
			}
		}else{
			divClassValue = mapValue;
		}
		
		displayVO.setDivClass(divClassValue);
		if(isLoggingDebug()){
			logDebug("The value of DivClass for facet : " + facetName + IS_COLON + divClassValue + 
					".[END] VendorResponseDisplayPropsCustomizer.setDivClassDisplayProperties() method");
		}
	}
	
	/**
	 * This method is used to set the Offscreen property for different facets
	 * 
	 * @param facetName
	 * @param displayVO
	 * @throws None
	 */
	private void setOffscreenDisplayProperties(final String facetName, final DisplayPropertiesVO displayVO){
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseDisplayPropsCustomizer.setOffscreenDisplayProperties() method");
		}
		String offScreenValue;
		String mapValue = getOffScreenMap().get(facetName);
		
		if(BBBUtility.isEmpty(mapValue)){
			offScreenValue = facetName;
		}else{
			offScreenValue = mapValue;
		}
		
		displayVO.setOffScreen(offScreenValue);
		if(isLoggingDebug()){
			logDebug("The value of OffScreen for facet : " + facetName + IS_COLON + offScreenValue + 
					".[END] VendorResponseDisplayPropsCustomizer.setOffscreenDisplayProperties() method");
		}
	}
	
	/**
	 * This method is used to set the Title Value for different facets
	 * 
	 * @param facetName
	 * @param displayVO
	 * @throws None
	 */
	private void setTitleDisplayProperties(final String facetName, final DisplayPropertiesVO displayVO){
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseDisplayPropsCustomizer.setTitleDisplayProperties() method");
		}
		String titleValue;
		String mapValue = getTitleMap().get(facetName);
		
		if(BBBUtility.isEmpty(mapValue)){
			titleValue = facetName;
		}else{
			titleValue = mapValue;
		}
		
		displayVO.setTitle(titleValue);
		if(isLoggingDebug()){
			logDebug("The value of Title for facet : " + facetName + IS_COLON + titleValue + 
					".[END] VendorResponseDisplayPropsCustomizer.setTitleDisplayProperties() method");
		}
	}
	
	/**
	 * This method is used to set the Display name of different facets
	 * 
	 * @param facetName
	 * @param displayVO
	 * @throws None
	 */
	private void setNameDisplayProperties(final String facetName,final DisplayPropertiesVO displayVO){
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseDisplayPropsCustomizer.setNameDisplayProperties() method");
		}
		String displayNameValue;
		String mapValue = getDisplayNameMap().get(facetName);
		
		if(BBBUtility.isEmpty(mapValue)){
			displayNameValue = facetName;
		}else{
			displayNameValue = mapValue;
		}
		
		displayVO.setDisplayName(displayNameValue);
		if(isLoggingDebug()){
			logDebug("The value of displayName for facet : " + facetName + IS_COLON + displayNameValue + 
					".[END] VendorResponseDisplayPropsCustomizer.setNameDisplayProperties() method");
		}
	}

	
}
