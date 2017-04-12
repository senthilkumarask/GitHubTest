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
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.common.BBBDynamoServlet;
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
public class FacebookImageDroplet extends BBBDynamoServlet {
	
		private ProductManager productManager;
		
		public static final String PRODUCT_IMAGE_LARGE = "productImageLarge";
		public static final String PRODUCT_ID_PARAMETER = "id";
		public final static String OPARAM_OUTPUT="output";
		
	
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
	 * This method get the product id and site id from the PageStart.jsp and return the large image
	 * 
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	    BBBPerformanceMonitor.start("FacebookImageDroplet", "service");
		
		String pProductId = null;
		ImageVO productImages = null;

		try {

			/**
			 * Product id from the JSP page.
			 */
			pProductId = pRequest.getParameter(PRODUCT_ID_PARAMETER);
			
			if(null != pProductId){
				logDebug("pProductId["+pProductId+"]");
				
				productImages = getProductManager().getProductImages(pProductId);
				
			} else {
				logDebug("pProductId is NULL");
			}
			
			
			if(null != productImages){
				
				if(productImages.getLargeImage() != null){
					pRequest.setParameter(PRODUCT_IMAGE_LARGE, productImages.getLargeImage());
				}
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
					pResponse);
			}else{
				logDebug("productImages is NULL");
			}

		} catch (BBBBusinessException bbbbEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of FacebookImageDroplet for productId=" +pProductId,BBBCoreErrorConstants.BROWSE_ERROR_1032),bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of FacebookImageDroplet for productId=" +pProductId,BBBCoreErrorConstants.BROWSE_ERROR_1033),bbbsEx);
		}
		
		BBBPerformanceMonitor.end("FacebookImageDroplet", "service");
	}
	
}



