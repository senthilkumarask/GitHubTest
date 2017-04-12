package com.bbb.wishlist;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.order.TBSOrder;
import com.bbb.utils.BBBUtility;

/**
 * Extensions to the com.bbb.wishlist.StoreGiftlistFormHandler.java.
 *
 */
public class TBSStoreGiftlistFormHandler extends StoreGiftlistFormHandler {
	@Override
	public boolean handleMoveItemsFromCart(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, CommerceException {
		TBSOrder tbsOrder = (TBSOrder) this.getOrder();
		CommerceItem item=null;
		
		if(this.getUndoComItemId() != null){
		  item = tbsOrder.getCommerceItem(this.getUndoComItemId());
		  }
	  else{
		    item = tbsOrder.getCommerceItem(getCurrentItemId());
		  }
		String siteId = SiteContextManager.getCurrentSiteId();
		if (BBBUtility.siteIsTbs(siteId) && item != null && null!=item.getId()) {
			//Set<String> priceOverrideSet = tbsOrder.getOverridePriceMap().keySet();
			if(tbsOrder.getOverridePriceMap().containsKey(item.getId())){
				tbsOrder.setTBSApprovalRequired(false);
			}
		}
		return super.handleMoveItemsFromCart(pRequest, pResponse);
	}
	
}
