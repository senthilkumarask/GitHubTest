package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;

import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;


public class WCRegistryInfoDroplet extends BBBDynamoServlet {

	private BBBCatalogTools mCatalogTools;
	private BBBPricingTools mPricingTools = null;
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	/**
	 * @return the pricingTools
	 */
	public final BBBPricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public final void setPricingTools(BBBPricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}
	

	/**
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException { 
		
		BBBPerformanceMonitor.start("BBBOrderInfoDroplet", "service");
		logDebug("CLS=[BBBOrderInfoDroplet] MTHD=[Service starts]");
		final Object order = pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		final StringBuilder registryIds = new StringBuilder();
		String registryId =null;
		if (order != null) {

			List<String> registryIdsList = new ArrayList<String>();
			int count = 1;
			for (ShippingGroup sg : (List<ShippingGroup>) ((BBBOrder) order)
					.getShippingGroups()) {

				for (ShippingGroupCommerceItemRelationship cisiRel : (List<ShippingGroupCommerceItemRelationship>) sg
						.getCommerceItemRelationships()) {

					CommerceItem commerceItem = cisiRel.getCommerceItem();
					if (!(commerceItem instanceof BBBCommerceItem)) {
						continue;
					}
					BBBCommerceItem bbbComItem = (BBBCommerceItem) commerceItem;
					 registryId = bbbComItem.getRegistryId();
					 if(registryId!=null){
						 registryIdsList.add(registryId);
					 }
				}
			}
			for(String registryIdItem:registryIdsList){
				if(count==1){
					registryIds.append(registryIdItem);
					count++;
				}else{
					registryIds.append(BBBCoreConstants.COMMA);
					registryIds.append(registryIdItem);
					 count++;
				}
			}
			if(registryIds.length()>0){   
			pRequest.setParameter("registryIds", registryIds.toString());  
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
			}else{
				pRequest.serviceParameter(BBBCheckoutConstants.EMPTY, pRequest,
						pResponse);
			}
		}
		BBBPerformanceMonitor.end("BBBOrderInfoDroplet", "service");
	}
}