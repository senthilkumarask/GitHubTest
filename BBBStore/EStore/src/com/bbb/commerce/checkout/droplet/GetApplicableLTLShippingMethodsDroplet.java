package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * To get shipping methods for single ship and multi ship
 * 
 * @author nagarwal9
 * @version 1.0
 */

public class GetApplicableLTLShippingMethodsDroplet extends BBBDynamoServlet {

	private BBBCatalogTools catalogTools;

	/**
	 * This methods adds list of shipping methods in request for sku param
	 * being passed in the request.
	 * 
	 * @param DynamoHttpServletRequest
	 *            Dynamo Http Servlet Request.
	 * @param DynamoHttpServletResponse
	 *            Dynamo Http Servlet Response
	 * @return void
	 * 
	 * @throws ServletException
	 *             , IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start("GetApplicableLTLShippingMethodsDroplet", "service");
		logDebug("starting - service method GetApplicableLTLShippingMethodsDroplet");

		List<ShipMethodVO> shipMethodVOList = null;

		final String skuID =  pRequest.getParameter(BBBCoreConstants.SKUID); 
		final String siteId = pRequest.getParameter(BBBCoreConstants.SITE_ID); 
		final String locale=  pRequest.getLocale().getLanguage();

		logDebug("Input params for GetApplicableLTLShippingMethodsDroplet:"+BBBCoreConstants.SITE_ID+":" + siteId + ","+ BBBCoreConstants.SKUID +":" + skuID);

		if(BBBUtility.isEmpty(skuID) || BBBUtility.isEmpty(siteId)){
			
			pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,
					shipMethodVOList);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
					pResponse);
			
		}else{
		
			try {
					shipMethodVOList = getCatalogTools().getLTLEligibleShippingMethods(skuID, siteId,locale);
	
					if (shipMethodVOList.isEmpty()) {
	
						pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM,
								pRequest, pResponse);
	
					} else {
						pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,
								shipMethodVOList);
						pRequest.serviceParameter(BBBCoreConstants.OPARAM,
								pRequest, pResponse);
					}
				} catch (BBBSystemException e) {
					logError(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1003+" BBBSystemException [Could not find Shipping method for SKU ] "+ skuID, e);
					setErrorParam(pRequest, pResponse);
	
				} catch (BBBBusinessException e) {
					logError(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1003+" BBBBusinessException [Could not find Shipping method for SKU ] "+ skuID, e);
					setErrorParam(pRequest, pResponse);
	
				}
			}
		
		logDebug("Exiting - service method GetApplicableLTLShippingMethodsDroplet");
		BBBPerformanceMonitor.end("GetApplicableLTLShippingMethodsDroplet",
				"service");
	}

	private void setErrorParam(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
				pResponse);
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

}
