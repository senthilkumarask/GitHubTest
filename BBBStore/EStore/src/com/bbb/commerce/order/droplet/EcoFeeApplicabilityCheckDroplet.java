//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Sunil Dandriyal
//
//Created on: 02-December-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;

/**
 * This Droplet checks if Eco Fee is applicable for particular sku.
 */
public class EcoFeeApplicabilityCheckDroplet extends BBBDynamoServlet {
	
	  public final static ParameterName TRUE  = ParameterName.getParameterName("true");
	  public final static ParameterName FALSE = ParameterName.getParameterName("false");
	  
	  private BBBCatalogToolsImpl catalogTools;	  
	  

	/**
	 * This method checks if Eco Fee is applicable for particular sku.
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@SuppressWarnings("unchecked")
    public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		
		logDebug("START: EcoFeeApplicabilityCheckDroplet.service");		
		
		boolean isEcoFeeApplicable = false;
		
		// multi shipping groups - Gift Wrap
		final String skuId = (String) pRequest.getObjectParameter(BBBCheckoutConstants.SKU_ID);
		
			if(!StringUtils.isBlank(skuId)){
				try {
					final RepositoryItem skuRepositoryItem = getCatalogTools().getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					if(null != skuRepositoryItem
							&& getCatalogTools().isSkuActive(skuRepositoryItem)
							&& ((Set<RepositoryItem>)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME)) != null
							&& ! ((Set<RepositoryItem>)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME)).isEmpty() ){						
						
						isEcoFeeApplicable = true;					
						
					}
					
				} catch (RepositoryException e) {
				
					logError("Eco Fee Not applicable for Sku : "+skuId,e);
					
				}		
			}
			pRequest.setParameter("isEcoFee", isEcoFeeApplicable);
			if(isEcoFeeApplicable) {
				pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
			} else {
				pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
			}

		logDebug("END: EcoFeeApplicabilityCheckDroplet.service");		
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogToolsImpl catalogTools) {
		this.catalogTools = catalogTools;
	}	
}