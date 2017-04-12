package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;
 
/*
 * This droplet is used to fetch related categories for a product 
 * using the parentCategories property of product item descriptor.
 */
/**
 * The Class ProductRelatedCategoriesDroplet.
 */
public class ProductRelatedCategoriesDroplet extends BBBPresentationDroplet {
	
	/** The product manager. */
	private ProductManager productManager;
	
	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start("ProductRelatedCategoriesDroplet", "service");
		String pProductId = null;
		String pSiteId = null;
		List<CategoryVO> relatedCategories = new ArrayList<>();
		
		try {
			pProductId = pRequest
					.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER);
			pSiteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);
			if (pSiteId == null) {
				pSiteId = extractCurrentSiteId();
			}
			
			if(!BBBUtility.isEmpty(pProductId)) {
				relatedCategories = getProductManager().getProductRelatedCategories(pProductId, pSiteId);
				if(relatedCategories!=null) {
					logDebug("ProductRelatedCategoriesDroplet :: Related Categories for product " + pProductId + ", siteId "
							+ pSiteId + " : " + relatedCategories.toString());
				}
				
			} else {
				logError("ProductRelatedCategoriesDroplet :: Product id is null ");
			}
			
			pRequest.setParameter(BBBCoreConstants.RELATED_CATEGORIES, relatedCategories);
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
					pResponse);
		} catch (BBBBusinessException bbbbEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of ProductRelatedCategoriesDroplet for productId=" +pProductId +" |SiteId="+pSiteId),bbbbEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
				pResponse);
			
		} catch (BBBSystemException bbbsEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of ProductRelatedCategoriesDroplet for productId=" +pProductId +" |SiteId="+pSiteId),bbbsEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
				pResponse);
		} 
		finally {
			BBBPerformanceMonitor.end("ProductRelatedCategoriesDroplet", "service");
		}
		
	}

	protected String extractCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * Gets the product manager.
	 *
	 * @return the product manager
	 */
	public ProductManager getProductManager() {
		return productManager;
	}

	/**
	 * Sets the product manager.
	 *
	 * @param productManager the new product manager
	 */
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}
	
	
}
