package com.bbb.kickstarters.droplet;
/**
 * This class retrives consultant details as per given consultant id.
 * 
 * @author dwaghmare
 * 
 */

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.TopSkuDetailVO;
import com.bbb.kickstarters.TopSkuVO;

public class TopSkusDroplet extends BBBDynamoServlet {
	
	public static final String OPARAM_ERROR = "error";

	private BBBCatalogTools catalogTools = null;	
	
	
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * Droplet to retrieve the top consultant details as per consultantId and eventType.
	 *
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws javax.servlet.ServletException, java.io.IOException {
		
		BBBPerformanceMonitor.start( TopSkusDroplet.class.getName() + " : " + "service");
		
		String siteId = (String) pRequest.getLocalParameter("siteId");
		String skuId = ((String) pRequest.getLocalParameter("skuId"));
		TopSkuVO topSku = ((TopSkuVO) pRequest.getLocalParameter("topSku"));
		
		boolean calculateAboveBelowLine = false;
		SKUDetailVO skuDetailVO = null;
		TopSkuDetailVO topSkuDetailVO = null;
		String productId =null;
		String strgSeoURL = null;
		BazaarVoiceProductVO BV = null;
		
		try {
			topSkuDetailVO = topSku.getSkuDetailVO();
			skuDetailVO = (SKUDetailVO) catalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine);
			
			RepositoryItem parentProductItem = catalogTools.getParentProductItemForSku(skuId);			
			if(parentProductItem!=null){
				productId = parentProductItem.getRepositoryId();
				strgSeoURL  = (String)parentProductItem.getPropertyValue("seoUrl");
				BV = catalogTools.getBazaarVoiceDetails(productId,siteId);
			}
			
			if(skuDetailVO !=null){
				//BBBH-3982 DSK Incart price | Mehthod invoked to display free shipping method wrt incart price
				//BBBH-7064 inCartFlag retrieved from topSkuDetailVO to avoid coherence cache call
				catalogTools.updateShippingMessageFlag(skuDetailVO,topSkuDetailVO.isInCartFlag());
				pRequest.setParameter("SKUDetailsVO",skuDetailVO);
				pRequest.setParameter("productId",productId);
				pRequest.setParameter("BV",BV);
				pRequest.setParameter("seoURL",strgSeoURL);	
				pRequest.setParameter("TopSKUDetailVO",topSkuDetailVO);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);
			}else{
				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest,
						pResponse);
			}
		} catch (BBBSystemException e) {
			
				logError(e.getMessage());
			
		} catch (BBBBusinessException e) {
			
				logError(e.getMessage());
			
		}
		
		BBBPerformanceMonitor.end( TopSkusDroplet.class.getName() + " : " + "service");
	}
}
