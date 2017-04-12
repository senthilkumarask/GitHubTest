package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * This droplet is used to get the parent collection product for any product. It gets the parent product from the objectCache.
 * @author atiw14
 *
 */
public class CollectionParentDroplet extends BBBDynamoServlet {
	
	private static final String OPAPRAM_EMPTY = "empty";
	private BBBObjectCache objectCache;
	private BBBCatalogTools catalogTools;
	public final static String OPARAM_ERROR="error";
	public final static String OPARAM_OUTPUT="output";
	public final static String PRODUCT_ID="productId";
	private static final String COLLECTION_PARENT_PRODUCT_ID = "collectionParentProductId";

	
	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {		
	    BBBPerformanceMonitor.start("CollectionParentDroplet", "service");
	    
	    logDebug("Entering in CollectionParentDroplet");
	    //boolean disableCollectionParentCache = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, DISABLE_COLLECTION_PARENT_CACHE));
	    String pProductId = pRequest.getParameter(PRODUCT_ID);
	    String pSiteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);
		if (pSiteId == null) {
			pSiteId = SiteContextManager.getCurrentSiteId();
		}
	    //Product id from the JSP page.
	    	
	    	if (null != pProductId) {
	    		String collectionParentProductId = getCatalogTools().getParentProductId(pProductId, pSiteId);
	    		if(!BBBUtility.isBlank(collectionParentProductId))
	    		{
	    			pRequest.setParameter(COLLECTION_PARENT_PRODUCT_ID, collectionParentProductId);
		    		pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
		    	} else {
			    	pRequest.serviceParameter(OPAPRAM_EMPTY, pRequest, pResponse);
			    }
	    	} else {
	    		pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
	    	}
	    logDebug("Exting from CollectionParentDroplet");
		BBBPerformanceMonitor.end("CollectionParentDroplet", "service");
	}
	
	
	/**
	 * @return the objectCache
	 */
	public BBBObjectCache getObjectCache() {
		return objectCache;
	}


	/**
	 * @param objectCache the objectCache to set
	 */
	public void setObjectCache(BBBObjectCache objectCache) {
		this.objectCache = objectCache;
	}


	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
}
