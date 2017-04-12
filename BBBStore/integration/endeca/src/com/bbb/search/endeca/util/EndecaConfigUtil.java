package com.bbb.search.endeca.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBInternationalCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.utils.BBBUtility;

/**
 * Common config referenced in EndecaSearch and also in EndecaContentParser is moved to this new class
 * 
 * @author sc0054
 *
 */
public class EndecaConfigUtil extends BBBGenericService {
	
	private static final String DIM_DISPLAY_VALUE = "dimDisplayValue_";

	//Property to hold Dimension names Map.
	private Map<String, String> dimensionMap;

	//Property to hold siteIds Map.
	private Map<String, String> siteConfig;

	//Property to hold Property name map.
	private Map<String, String> propertyMap;

	//Property to hold JSON property names for "P_Swatch_Info" Endeca Property.
	private Map<String, String> swatchInfoMap;

	//Property to hold Catridge name map.
	private Map<String, String> catridgeNameMap;

	//Property to hold Catridge Property names.
	private Map<String, String> attributePropmap;
	
	// Property to get the PLSR placeholder.
	private String placeHolder;

	private String dimDisplayMapConfig;
	private String dimNonDisplayMapConfig;
	
	private String scene7Path;

	private Map<String, String> priorityFeaturesMap;
	
	private BBBInternationalCatalogTools internationalCatalogTools;
	private BBBLocalCacheContainer skuAttributeCache;
	private BBBCatalogTools mCatalogTools;
	private BBBLocalCacheContainer configCacheContainer;
	
	//Property to hold Record Type names.
	private Map<String, String> recordTypeNames;
	
	//Property to hold Department Dimension Name Map.
	private Map<String, String> departmentConfig;
	
	//content item related constants
	private List<String> validRootTemplateIDs;
	private String redirectDelimiter;
	
	//boostBurySort
	private String boostBurrySort;
	private String defaultKey="boost_bury_sort_order";
	
	
	public BBBLocalCacheContainer getSkuAttributeCache() {
		return skuAttributeCache;
	}

	public void setSkuAttributeCache(BBBLocalCacheContainer skuAttributeCache) {
		this.skuAttributeCache = skuAttributeCache;
	}

	public BBBInternationalCatalogTools getInternationalCatalogTools() {
		return internationalCatalogTools;
	}

	public void setInternationalCatalogTools(
			BBBInternationalCatalogTools internationalCatalogTools) {
		this.internationalCatalogTools = internationalCatalogTools;
	}

	/**
	 * @return dimDisplayMapConfig
	 */
	public String getDimDisplayMapConfig() {
		return this.dimDisplayMapConfig;
	}

	/**
	 * @param dimDisplayMapConfig
	 */
	public void setDimDisplayMapConfig(final String dimDisplayMapConfig) {
		this.dimDisplayMapConfig = dimDisplayMapConfig;
	}

	/**
	 * @return dimNonDisplayMapConfig
	 */
	public String getDimNonDisplayMapConfig() {
		return this.dimNonDisplayMapConfig;
	}

	/**
	 * @param dimNonDisplayMapConfig
	 */
	public void setDimNonDisplayMapConfig(final String dimNonDisplayMapConfig) {
		this.dimNonDisplayMapConfig = dimNonDisplayMapConfig;
	}


	/**
	 * @return scene7Path
	 */
	public String getScene7Path() {
		return this.scene7Path;
	}

	/**
	 * @param dimNonDisplayMapConfig
	 */
	public void setScene7Path(final String scene7Path) {
		this.scene7Path = scene7Path;
	}


	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * object for Coherence cache
	 */
	private BBBObjectCache mObjectCache;

	/**
	 * @return the mObjectCache
	 */
	public BBBObjectCache getObjectCache() {
		return this.mObjectCache;
	}

	/**
	 * @param mObjectCache the mObjectCache to set
	 */
	public void setObjectCache(final BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
	}

	/**
	 * @return the brandDimName
	 */
	public Map<String, String> getDimensionMap() {
		Map<String,String> dimNonDisplayMap = null;
		Map<String,String> dimDisplayMap = null;
		Map<String,String> combinedDimensionMap = new HashMap<String, String>();
		try{
			dimDisplayMap = getCatalogTools().getConfigValueByconfigType(getDimDisplayMapConfig());
			dimNonDisplayMap = getCatalogTools().getConfigValueByconfigType(getDimNonDisplayMapConfig());
			combinedDimensionMap.putAll(dimDisplayMap);
			combinedDimensionMap.putAll(dimNonDisplayMap);
		}catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1040+" BusinessException in default dimensions list  from getDimensionMap from EndecaSearch",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1041+" SystemException in default dimensions list  from getDimensionMap from EndecaSearch",bbbsEx);
		}
		return combinedDimensionMap;
	}

	/**
	 * @param brandDimName the brandDimName to set
	 */
	public void setDimensionMap(final Map<String, String> dimensionMap) {
		this.dimensionMap = dimensionMap;
	}

	/**
	 * @return propertyMap
	 */
	public Map<String, String> getPropertyMap() {
		return this.propertyMap;
	}

	/**
	 * @param propertyMap
	 */
	public void setPropertyMap(final Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

	/**
	 * @return siteConfig
	 */
	public Map<String, String> getSiteConfig() {
		return this.siteConfig;
	}

	/**
	 * @param siteConfig
	 */
	public void setSiteConfig(final Map<String, String> siteConfig) {
		this.siteConfig = siteConfig;
	}

	/**
	 * @return the swatchInfoMap
	 */
	public Map<String, String> getSwatchInfoMap() {
		return this.swatchInfoMap;
	}

	/**
	 * @param swatchInfoMap the swatchInfoMap to set
	 */
	public void setSwatchInfoMap(final Map<String, String> swatchInfoMap) {
		this.swatchInfoMap = swatchInfoMap;
	}

	public Map<String, String> getSortFieldMap() {
		Map<String, String> facetList = null;
		try {
			facetList = getCatalogTools().getSearchSortFieldMap();
		}
		catch (BBBSystemException bbbsEx) {
			logError("System Exception occurred while fetching Type Ahead Dimension Names", bbbsEx);
		}
		return facetList;
	}
	/*
	public void setSortFieldMap(final Map<String, String> sortFieldMap) {
		this.sortFieldMap = sortFieldMap;
	}*/

	public Map<String, String> getCatridgeNameMap() {
		return this.catridgeNameMap;
	}

	public void setCatridgeNameMap(final Map<String, String> catridgeNameMap) {
		this.catridgeNameMap = catridgeNameMap;
	}

	/**
	 * @return the attributePropmap
	 */
	public Map<String, String> getAttributePropmap() {
		return this.attributePropmap;
	}

	/**
	 * @param attributePropmap the attributePropmap to set
	 */
	public void setAttributePropmap(final Map<String, String> attributePropmap) {
		this.attributePropmap = attributePropmap;
	}

	/**
	 * @return the placeHolder
	 */
	public String getPlaceHolder() {
		return this.placeHolder;
	}

	/**
	 * @param placeHolder the placeHolder to set
	 */
	public void setPlaceHolder(final String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public List<String> getCatIdsToBeSuppressed(final String pSiteId) {
		List<String> mFacetList = new ArrayList<String>();
		List<String> facetList = null;
		try {
			facetList = getCatalogTools().getAllValuesForKey(getDimNonDisplayMapConfig(), pSiteId);
		} catch (BBBBusinessException bbbbEx) {
			logError("Business Exception occurred while fetching Type Ahead Dimension Names", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError("System Exception occurred while fetching Type Ahead Dimension Names", bbbsEx);
		}
		if(null != facetList){
			mFacetList = facetList;
		}
		return mFacetList;
	}

	/**
	 * This is to sanitize the Attribute Values and removing HTML mark ups if any..
	 * Return null if nothing is configured in this Property at site level.
	 * @param pSearchQuery
	 * @return List<String>
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<String> sanitizedAttributeValues(final String pSiteId) throws BBBBusinessException, BBBSystemException{

		 

		final List<String> pList =getCatalogTools().siteAttributeValues(pSiteId);
		final List<String> pSanitizedList = new ArrayList<String>();
		if(null != pList){
			for(String pAttribute : pList){
				if(pAttribute.indexOf(">") == -1){
					pSanitizedList.add(pAttribute.toLowerCase());
				}
				else{
					pSanitizedList.add((pAttribute.substring(pAttribute.indexOf(">")+1, pAttribute.indexOf("</"))).toLowerCase());
				}
			}
		}
	 
		return pSanitizedList;
	}

	/**
	 * will return collection of facets configured the current site from 
	 * configure repository
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getFacets(String currentSiteId) {
   		String key = DIM_DISPLAY_VALUE+BBBUtility.getChannel()+BBBCoreConstants.UNDERSCORE+currentSiteId;
		Collection<String> facets=(Collection<String>) getConfigCacheContainer().get(key);
    	   	if(facets==null){
    	   		 try {
    	   			 Map<String, String> dimDisplayMap = getCatalogTools()
						.getConfigValueByconfigType(getDimDisplayMapConfig());
    	   			 	if (!dimDisplayMap.values().isEmpty()){
    	   			 		facets=dimDisplayMap.values();
    	   			 		getConfigCacheContainer().put(key,facets);
    	   			 	}	    	   		
       		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1014+" Business Exception occurred while fetching dimensions display list from  getFacets from SearchDroplet",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1015+" System Exception occurred while fetching dimensions display list from  getFacets from SearchDroplet",bbbsEx);
		}
    }
       if(isLoggingDebug()){
    	   logDebug("facets: "+facets);
       }
		return facets;
	}

	/** The method is returning the  sku attribute list with INTL_FLAG!=Y
	*
	* @return AttributeList
	* @throws BBBSystemException */
	public Map<String,String> getAttributeInfo() throws BBBSystemException{
		logDebug("Inside EndecaSearch METHOD:getAttributeInfo STARTS");
		Map<String,String> attributeMap=new HashMap<String, String>();
		Iterator e=this.getSkuAttributeCache().getAllKeys();
		if(e.hasNext() ){
			while(e.hasNext()){
				String w= (String)e.next();
				this.getSkuAttributeCache().get(w);
				attributeMap.put(w, (String)this.getSkuAttributeCache().get(w));
			}
		}
		else{
			attributeMap=this.getInternationalCatalogTools().getAttributeInfo();
			for (Map.Entry<String,String> entry : attributeMap.entrySet()) {
				this.getSkuAttributeCache().put(entry.getKey(), entry.getValue());
	        }
			
		}
		 
		return attributeMap;
		
	}

	public BBBLocalCacheContainer getConfigCacheContainer() {
		return configCacheContainer;
	}

	public void setConfigCacheContainer(BBBLocalCacheContainer configCacheContainer) {
		this.configCacheContainer = configCacheContainer;
	}

	/**
	 * @return the priorityFeaturesMap
	 */
	public Map<String, String> getPriorityFeaturesMap() {
		return priorityFeaturesMap;
	}


	/**
	 * @param priorityFeaturesMap the priorityFeaturesMap to set
	 */
	public void setPriorityFeaturesMap(Map<String, String> priorityFeaturesMap) {
		this.priorityFeaturesMap = priorityFeaturesMap;
	}

	/**
	 * @return the recordTypeNames
	 */
	public Map<String, String> getRecordTypeNames() {
		return recordTypeNames;
	}

	/**
	 * @param recordTypeNames the recordTypeNames to set
	 */
	public void setRecordTypeNames(Map<String, String> recordTypeNames) {
		this.recordTypeNames = recordTypeNames;
	}

	/**
	 * @return the validRootTemplateIDs
	 */
	public List<String> getValidRootTemplateIDs() {
		return validRootTemplateIDs;
	}

	/**
	 * @param validRootTemplateIDs the validRootTemplateIDs to set
	 */
	public void setValidRootTemplateIDs(List<String> validRootTemplateIDs) {
		this.validRootTemplateIDs = validRootTemplateIDs;
	}
	
	/**
	 * @return departmentConfig
	 */
	public Map<String, String> getDepartmentConfig() {
		return this.departmentConfig;
	}

	/**
	 * @param departmentConfig
	 */
	public void setDepartmentConfig(final Map<String, String> departmentConfig) {
		this.departmentConfig = departmentConfig;
	}

	/**
	 * @return the redirectDelimiter
	 */
	public String getRedirectDelimiter() {
		return redirectDelimiter;
	}

	/**
	 * @param redirectDelimiter the redirectDelimiter to set
	 */
	public void setRedirectDelimiter(String redirectDelimiter) {
		this.redirectDelimiter = redirectDelimiter;
	}
	
	/**
	 * @return the boostBurrySort
	 */
	public String getBoostBurrySort() {
		try {
			boostBurrySort=getCatalogTools().getAllValuesForKey(getDimNonDisplayMapConfig(),defaultKey).get(0);
		} catch (BBBSystemException e) {
			logError("System Exception occurred while fetching boostburry key from dimnondisplayconfigtype", e);

			return null;
		} catch (BBBBusinessException e) {
			logError("Business Exception occurred while fetching boostburry key from dimnondisplayconfigtype", e);
					return null;
		}
		return boostBurrySort;
	}

	/**
	 * @param boostBurrySort the boostBurrySort to set
	 */
	public void setBoostBurrySort(String boostBurrySort) {
		this.boostBurrySort = boostBurrySort;
	}


}
