package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * This class is to populate the minimal info of product which is the part of
 * product VO.
 * 
 */
public class MinimalProductDetailDroplet extends BBBDynamoServlet {
	
	private BBBCatalogTools mCatalogTools;
	
	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}
	
	/**
	 * @param mCatalogTools
	 *            the CatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	/**
	 * This method get the product id from the jsp and pass these
	 * value to CatalogTools class and get the productVo from CatalogTools class
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		BBBPerformanceMonitor.start(MinimalProductDetailDroplet.class.getName() + " : " + "service");
		logDebug("Enter.MinimalProductDetailDroplet.service(pRequest,pResponse) start");
		/**
		 * Product id from the JSP page.
		 */
		final String productId = pRequest.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER);
		final String isMetaDataRequired = pRequest.getParameter(BBBSearchBrowseConstants.IS_META_DETAILS);
		try {
			ProductVO productVO = null;
			if(isMetaDataRequired !=null && Boolean.valueOf(isMetaDataRequired)){
				logDebug("MinimalProductDetailDroplet getProductVOMetaDetails:productId"+productId);
				productVO = getCatalogTools().getProductVOMetaDetails(SiteContextManager.getCurrentSiteId(), productId);
			} else {
				productVO = getCatalogTools().getProductDetails(SiteContextManager.getCurrentSiteId(), productId, true, true, false);
			}
			pRequest.setParameter(BBBCoreConstants.PRODUCTVO,productVO);
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest,pResponse);

		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException"), e);
		}
		logDebug("End.MinimalProductDetailDroplet.service(pRequest,pResponse) end");
		BBBPerformanceMonitor.start(MinimalProductDetailDroplet.class.getName() + " : " + "service");
	}

}