package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * To populate get sku prop65 flag details
 * 
 * @author ajosh8
 * @version 1.0
 */
public class BBBCheckoutProductDetailDroplet extends BBBDynamoServlet {

	private static final String PRODUCT_ID = "productId";
	private static final String SKU_ID = "skuId";
	private static final String COMM_ITEM_ID = "commerceItemId";
	
	private static final String PRODUCT_VO = "productVO";
	private static final String SKU_DETAIL_VO = "pSKUDetailVO";
	private static final String OPARAM_ERROR = "error";
	

	private BBBCatalogTools catalogTools;

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * This will interact with catalog to get sku prop65 flag details. 
	 */
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		ProductVO productVO = null;
		SKUDetailVO pSKUDetailVO = null;		
		
		String pProductId = null;
		String pSKUId = null;
		String pSiteId = null;
		String pCommItemId = null;
		logDebug("Entry BBBCheckoutProductDetailDroplet.service");
		
		/**
		 * Product id from the JSP page.
		 */
		pProductId = pRequest
				.getParameter(PRODUCT_ID);
		/**
		 * SKU id from the JSP page.
		 */
		pSKUId = pRequest.getParameter(SKU_ID);
		
		pCommItemId = pRequest.getParameter(COMM_ITEM_ID);

		/**
		 * siteId from the JSP page.if site id is null then get it from the
		 * SiteContextManager
		 */
		pSiteId = pRequest.getParameter(BBBCheckoutConstants.SITEID);
		if (pSiteId == null) {
			pSiteId = SiteContextManager.getCurrentSiteId();
		}
		
		logDebug("pSiteId["+pSiteId+"]");
		logDebug("pProductId["+pProductId+"]");
		logDebug("pSKUId["+pSKUId+"]");
		
		productVO = new ProductVO();
		try{
			pSKUDetailVO = getCatalogTools().getSKUDetails(pSiteId, pSKUId, false, true, true);
			
			final BBBOrder bbbOrder = (BBBOrder) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
			
			if(null != pSKUDetailVO && null != bbbOrder.getAvailabilityMap()){
				Map<String, Integer> tempAvailabilityMap = bbbOrder.getAvailabilityMap();
				if(tempAvailabilityMap.containsKey(pCommItemId)){
					Integer status = tempAvailabilityMap.get(pCommItemId);
					if(status == BBBInventoryManager.AVAILABLE || status == BBBInventoryManager.LIMITED_STOCK){
						pSKUDetailVO.setSkuInStock(true);
					}else{
						pSKUDetailVO.setSkuInStock(false);
					}
				}else{
					pSKUDetailVO.setSkuInStock(true);
				}
			}
			
			if(pSKUDetailVO != null){
				boolean isCommItemBelowLine = getCatalogTools().isSKUBelowLine(pSiteId, pSKUId);
				pSKUDetailVO.setSkuBelowLine(isCommItemBelowLine);
				productVO.setProductId(pProductId);
				productVO.setName(pSKUDetailVO.getDisplayName());
				
				pRequest.serviceParameter(BBBCheckoutConstants.OUTPUT, pRequest,
						pResponse);
				pRequest.setParameter(PRODUCT_VO, productVO);
				pRequest.setParameter(SKU_DETAIL_VO, pSKUDetailVO);
			}else{
				logError("BBBCheckoutProductDetailDroplet : SKUDetail not found from catalog API " + pSKUId);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
		} catch (BBBBusinessException bbbbEx) {
			logError("BBBBusinessException :" + bbbbEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} catch (BBBSystemException bbbsEx) {
			logError("BBBSystemException" + bbbsEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		}	
		logDebug("Exit BBBCheckoutProductDetailDroplet.service");
	}

}

