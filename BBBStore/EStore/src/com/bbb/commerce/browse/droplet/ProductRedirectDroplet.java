//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Naveen Kumar
//
//Created on: 29-November-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
//import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;


public class ProductRedirectDroplet extends BBBDynamoServlet {
	
	/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
		//private BBBInventoryManager inventoryManager;
		//private String providerID;

		public static final String PRODUCT = "product";
		public static final String PRODUCT_ID_PARAMETER = "id";
		public final static String OPARAM_OUTPUT="output";
		public final static String OPARAM_ERROR="error";		
		public final static String SKUID="skuId";
		
	
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	private BBBCatalogTools bbbCatalogTools;
	private MutableRepository catalogRepository;
	
	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(final MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
	
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}
	
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		BBBPerformanceMonitor.start("ProductRedirectDroplet", "service");
		String pSiteId = null;
		String pProductId = null;
		String pSKUId = null;
		RepositoryItem productRepositoryItem = null;
		try {

			/**
			 * SKU id from the JSP page.
			 */
			pSKUId = pRequest.getParameter(SKUID);
			boolean activeParentProduct = false;
			if(StringUtils.isEmpty(pSKUId)) {

				logDebug(SKUID+" is NULL");
				throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
			}
			logDebug("pSiteId["+pSiteId+"]");
			logDebug("pProductId["+pProductId+"]");
			
			Set<RepositoryItem> parentProduct  = null;
			if(pSKUId.length()==6) {
				productRepositoryItem = getCatalogRepository().getItem(pSKUId.trim(), BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
				
			}else{
				final RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(pSKUId.trim(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				if(skuRepositoryItem==null){
					logError("Repository Item is null for sku id "+pSKUId);
					pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
					throw new BBBBusinessException (BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
					
				}else{
					boolean isStoreSku = (Boolean)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME);
					parentProduct = (Set<RepositoryItem>)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME);
					if(!isStoreSku){
						if(parentProduct!=null && !parentProduct.isEmpty()){
							for(RepositoryItem productRepItem:parentProduct){
								if(getBbbCatalogTools().isProductActive(productRepItem)){
									productRepositoryItem = productRepItem;
									activeParentProduct =  true;
									break;
								}
							}
							if (!activeParentProduct) {
								Iterator<RepositoryItem> parentProductIterator = parentProduct.iterator();
								productRepositoryItem = (RepositoryItem) parentProductIterator.next();
							}
						}
					  } 
				   }
				}
			
			if(productRepositoryItem == null){
				logError(pSKUId+" have No active products");
				pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
				throw new BBBBusinessException (BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
			}else{
				pRequest.setParameter(PRODUCT, productRepositoryItem);
				if(null == productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) || StringUtils.isBlank((String)productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME))){
					pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
				
			}
		} catch (BBBBusinessException bbbbEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of ProductRedirectDroplet for productId=" +pProductId +" |skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1032),bbbbEx);
			pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} catch (RepositoryException e) {
			logError("RepositoryException from service of ProductRedirectDroplet for productId=" +pProductId +" |skuId="+pSKUId +" |SiteId="+pSiteId,e);
			pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,pResponse);
		}
		BBBPerformanceMonitor.end("ProductRedirectDroplet", "service");
	}
	
}



