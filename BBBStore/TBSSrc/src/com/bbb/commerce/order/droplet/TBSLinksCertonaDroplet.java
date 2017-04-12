package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderImpl;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.order.bean.NonMerchandiseCommerceItem;

public class TBSLinksCertonaDroplet extends BBBDynamoServlet {


	private BBBCatalogTools mCatalogTools;
	private BBBPricingTools mPricingTools = null;

	private static final String ITEM_SKU_IDS = "itemSkuIds";
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
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
		return this.mPricingTools;
	}

	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public final void setPricingTools(BBBPricingTools pPricingTools) {
		this.mPricingTools = pPricingTools;
	}
	

	/**
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("CLS=[TBSOrderInfoDroplet] MTHD=[Service starts]");
		final Object order = pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		final StringBuilder itemSkuIds = new StringBuilder();
		
				
		if (order != null) {
 
			final int listSize = ((BBBOrder) order).getCommerceItemCount();
			int count = 1;
			
			for (CommerceItem commerceItem : (List<CommerceItem>) ((BBBOrder) order)
					.getCommerceItems()) {

				//If EcoFee or GiftWrap items then skip
				if( !(commerceItem instanceof NonMerchandiseCommerceItem)){
					itemSkuIds.append(commerceItem.getCatalogRefId());
					if (count < listSize) {
						itemSkuIds.append(BBBCoreConstants.PIPE_SYMBOL);
					}
				}
				
				count++;
			}
			logDebug("TBSOrderInfoDroplet service method :: itemSkuIds :: "+itemSkuIds);
						
			pRequest.setParameter(ITEM_SKU_IDS, itemSkuIds);
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest,
					pResponse);
			
			pRequest.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
					getPricingTools().getOrderPriceInfo((OrderImpl) order));
			logDebug("CLS=[TBSOrderInfoDroplet] MTHD=[Service ends]");
		}

	}


}
