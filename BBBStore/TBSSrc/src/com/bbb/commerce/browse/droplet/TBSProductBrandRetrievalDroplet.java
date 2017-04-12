package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class TBSProductBrandRetrievalDroplet extends MinimalProductDetailDroplet{
	
	/* This dropet takes in product id, site id and boolean properties to get brand details
	 * (non-Javadoc)
	 * @see com.bbb.commerce.browse.droplet.MinimalProductDetailDroplet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start(TBSProductBrandRetrievalDroplet.class.getName() + " : " + "service");
		logDebug("Enter.TBSProductBrandRetrievalDroplet.service(pRequest,pResponse) start");
		ProductVO productVO = null;
		/**
		 * Product id from the JSP page.
		 */
		final String productId = pRequest.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER);
		final String pageFrom = pRequest.getParameter("pageFrom");
		if(!StringUtils.isEmpty(pageFrom) && pageFrom.equals("pdptbs")){
			try {
				productVO = getCatalogTools().getProductDetails(SiteContextManager.getCurrentSiteId(), productId, true, false, false);
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException"), e);
			}
		}else{
			// TODO Auto-generated method stub
			super.service(pRequest, pResponse);
		}
		if(productVO!=null && productVO.getProductBrand()!=null){
		pRequest.setParameter("productBrand",productVO.getProductBrand());
		}else{
		pRequest.setParameter("productBrand","");
		}
		pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest,pResponse);
		logDebug("End.TBSProductBrandRetrievalDroplet.service(pRequest,pResponse) end");
		BBBPerformanceMonitor.start(TBSProductBrandRetrievalDroplet.class.getName() + " : " + "service");
	}

}
