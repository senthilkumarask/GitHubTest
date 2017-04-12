package com.bbb.simplifyRegistry.droplet;

import java.util.List;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

public class SimpleRegNearestStoreDroplet extends BBBDynamoServlet {
	
	private SearchStoreManager searchStoreManager;
	private String radius;
	public static final String ORIGIN="origin";
	private BBBCatalogTools catalogTools;

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws javax.servlet.ServletException, java.io.IOException {
		String origin = (String) pRequest.getLocalParameter(ORIGIN);
		logDebug(new StringBuilder("Inside service method of SimpleRegStoreSearchDroplet with origin : ").append(origin).toString());
		String zipcode=null;
		if(BBBUtility.isNumericOnly(origin)){
			zipcode=origin;
		}
		String siteId = getSiteId();
		String storeType=null;
		try {
			storeType = getSearchStoreManager().getStoreType(siteId);
		} catch (BBBSystemException bbbSystemException) {
			if(isLoggingError()){
				logError("BBBSystem Exception Occured while trying to fetch the store type  ",bbbSystemException);
			}
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,	pResponse);
			return;
		} catch (BBBBusinessException bbbBusinessException) {
			if(isLoggingError()){
				logError("bbbBusinessException Exception Occured while trying to fetch the store type  ",bbbBusinessException);
			}
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,	pResponse);
			return;
		}
		StringBuilder strBuild = new StringBuilder();
		String searchType=null;
	    if (!BBBUtility.isEmpty(zipcode)) {
	    	// Search based on Zipcode details
			strBuild.append(SelfServiceConstants.LOCATION_EQUALS);
			strBuild.append(zipcode);
			searchType=SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH;
			logDebug(new StringBuilder("Search based on Zip Code").toString());
		} else {
		    	// Search based on Address details
				strBuild.append(SelfServiceConstants.LOCATION_EQUALS);
				strBuild.append(origin);
				searchType=SelfServiceConstants.ADDRESS_BASED_STORE_SEARCH;
				logDebug(new StringBuilder("Search based on Address").toString());
			}
	    
	    String configValue = BBBCoreConstants.BLANK;
	    List<String> keysList=null;
		if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
			// For Bed bath Canada site
			try {
				keysList = getCatalogTools()
						.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,
								"radius_default_ca");
			} catch (BBBSystemException | BBBBusinessException e) {
				logError("BBBSystemException | BBBBusinessException occured",e);
			}
			if (!BBBUtility.isListEmpty(keysList)) {
				configValue = keysList.get(0);
			}
			if (!(configValue.isEmpty())) {
				setRadius(configValue);
			} 

		

		}
		if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)) {
			// For bed bath US site
			try {
				keysList = getCatalogTools()
						.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,
								"radius_default_us");
			} catch (BBBSystemException | BBBBusinessException e) {
				logError("BBBSystemException | BBBBusinessException occured" , e);
			}
			if (!BBBUtility.isListEmpty(keysList)) {
				configValue = keysList.get(0);
			}
			if (!(configValue.isEmpty())) {
				setRadius(configValue);
			} 
		}
		if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)) {
			// For baby site 
			try {
				keysList = getCatalogTools()
						.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,
								"radius_default_baby");
			} catch (BBBSystemException | BBBBusinessException e) {
				logError("BBBSystemException | BBBBusinessException occured" , e);
			}
			if (!BBBUtility.isListEmpty(keysList)) {
				configValue = keysList.get(0);
			}
			if (!(configValue.isEmpty())) {
				setRadius(configValue);
			} 

		}
		pRequest.getSession().setAttribute(BBBCoreConstants.STORE_TYPE  , storeType);
		pRequest.getSession().setAttribute(BBBCoreConstants.TYPE, searchType);
		pRequest.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, getRadius());
		pRequest.getSession().setAttribute(BBBCoreConstants.STATUS, strBuild.toString());
		pRequest.getSession().setAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME,origin);
		pRequest.getSession().setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
		pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,	pResponse);
	
	}

	protected String getSiteId() {
		return SiteContextManager.getCurrentSite().getId();
	}

	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


}