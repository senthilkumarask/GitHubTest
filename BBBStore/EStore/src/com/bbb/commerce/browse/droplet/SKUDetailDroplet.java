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

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * This class is to populate the all info of product which is the part of
 * product VO.
 * 
 */
public class SKUDetailDroplet extends BBBDynamoServlet {
	
	/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
		private ProductManager productManager;
		private boolean minimal;
		private BBBCatalogTools bbbCatalogTools;

		/* ===================================================== *
 		CONSTANTS
	 * ===================================================== */	
		public static final String SKUDETAILVO = "pSKUDetailVO";
		public final static String OPARAM_OUTPUT="output";
		public final static String OPARAM_ERROR="error";
		public final static String SKUID="skuId";
		public final static String FullDetails="fullDetails";
		private static final String PERSONALIZED_PRICE = "personalizedPrice";
		private static final String PERSONALIZED_FLAG = "personalizedFlag";


	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
	
	public ProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(final ProductManager productManager) {
		this.productManager = productManager;
	}


	/**
	 * @return the minimal
	 */
	public boolean isMinimal() {
		return minimal;
	}

	/**
	 * @param minimal the minimal to set
	 */
	public void setMinimal(boolean minimal) {
		this.minimal = minimal;
	}

	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	/**
	 * This method get the sku id and site id from the jsp and pass these
	 * value to manager class and get the SKUVO from manager class
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	    BBBPerformanceMonitor.start("SKUDetailDroplet", "service");
		/* ===================================================== *
		   MEMBER VARIABLES
	    * ===================================================== */
		SKUDetailVO pSKUDetailVO = null;
		String pSiteId = null;
		String pSKUId = null;
		String fullDetails = "" ;
		try {
			
			/**
			 * SKU id from the JSP page.
			 */
			pSKUId = pRequest.getParameter(SKUID);

			fullDetails = pRequest.getParameter(FullDetails);
			//R2.1 Fix
			boolean xMinimal = minimal;
			String personalizedFlag = pRequest.getParameter(PERSONALIZED_FLAG);

			if(fullDetails != null && fullDetails.equalsIgnoreCase("true")){
				xMinimal = false;
			}

			/**
			 * siteId from the JSP page.if site id is null then get it from the
			 * SiteContextManager
			 */


			pSiteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);
			if (pSiteId == null) {
				pSiteId = extractCurrentSiteId();
			}

			if(null != pSKUId){
				logDebug("pSiteId["+pSiteId+"]");
				logDebug("pSKUId["+pSKUId+"]");
				if (!xMinimal) {
					pSKUDetailVO = getBbbCatalogTools().getSKUDetails(pSiteId, pSKUId, false, false);
				} else {
					pSKUDetailVO = getBbbCatalogTools().getSKUDetails(pSiteId,pSKUId, false, xMinimal , true);
				}
				if(personalizedFlag != null && Boolean.valueOf(personalizedFlag)){
					String personalizedPrice = pRequest.getParameter(PERSONALIZED_PRICE);
					if(personalizedPrice != null){
						getBbbCatalogTools().updateShippingMessageFlag(pSKUDetailVO, Boolean.valueOf(personalizedFlag), Double.valueOf(personalizedPrice));
					}
				}
				pRequest.setParameter(SKUDETAILVO, pSKUDetailVO);
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
			} else {
				pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
			}
			
		} catch (BBBBusinessException bbbbEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of SKUDetailDroplet for skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1032),bbbbEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} catch (BBBSystemException bbbsEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of SKUDetailDroplet for skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1033),bbbsEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		}
		
		BBBPerformanceMonitor.end("ProductDetailDroplet", "service");
	}

	protected String extractCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
		
}



