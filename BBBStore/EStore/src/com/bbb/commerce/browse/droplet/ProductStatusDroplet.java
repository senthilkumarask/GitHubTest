package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

public class ProductStatusDroplet extends BBBDynamoServlet {
		
	

	private BBBCatalogTools mCatalogTools;
	public static final String IS_PRODUCT_ACTIVE = "isProductActive";
	public final static String OPARAM_ERROR="error";
	public final static String OPARAM_OUTPUT="output";
	public final static String PRODUCT_ID="productId";
	
	
	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {		
	    BBBPerformanceMonitor.start("ProductStatusDroplet", "service");
		String pProductId = null;
		String pSiteId = null;
		try {
				/**
				 * Product id from the JSP page.
				 */
				pProductId = pRequest.getParameter(PRODUCT_ID);
				if (null != pProductId) {
					pSiteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);
					if (pSiteId == null) {
						pSiteId = extractSiteId();
					}
					final boolean isProductActive = getCatalogTools().isProductActive(pProductId);
					pRequest.setParameter(IS_PRODUCT_ACTIVE, Boolean.valueOf(isProductActive));
					pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
				} else {
					pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
				}
			} catch (BBBBusinessException bbbbEx) {
				logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of ProductStatusDroplet for productId=" +pProductId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1032),bbbbEx);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
			} catch (BBBSystemException bbbsEx) {
				logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of ProductStatusDroplet for productId=" +pProductId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1033),bbbsEx);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
			}
		BBBPerformanceMonitor.end("ProductStatusDroplet", "service");
	}


	protected String extractSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}
	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
}
