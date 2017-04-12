//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Sunil Dandriyal
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------

package com.bbb.wishlist;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.gifts.GiftlistManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;

/**
 * This class is to get the item count for wishlist in the current site. 
 */
public class BBBWishlistItemCountDroplet extends BBBDynamoServlet {

	/**
	 * This method takes items as input parameter type and 
	 * sets wishlistItemCount 
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	
private GiftlistManager mGiftlistManager;
    
    
public void setGiftlistManager(GiftlistManager pGiftlistManager)
{
    mGiftlistManager = pGiftlistManager;
}

public GiftlistManager getGiftlistManager()
{
    return mGiftlistManager;
}

	
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	
		long wishlistItemCount = 0;
		String currentSiteId = pRequest
				.getParameter(BBBCoreConstants.SITE_ID);		 
		
		String sisterSiteId = currentSiteId;
		if( sisterSiteId.startsWith("TBS") ) {
			sisterSiteId = sisterSiteId.substring(4);
		}
		else {
			sisterSiteId = "TBS_" + sisterSiteId;
		}
				
		mGiftlistManager = getGiftlistManager();
		String pGiftlistId = (String)pRequest.getObjectParameter("wishlistId");
		List items = mGiftlistManager.getGiftlistItems(pGiftlistId);
		if(items != null && items.size() >0){
			for(int i=0;i<items.size();i++){
				String siteId;
				RepositoryItem item = (RepositoryItem)items.get(i);
				siteId = (String)item.getPropertyValue("siteId");
				if(siteId.equals(currentSiteId) || siteId.equals(sisterSiteId))
				{
					wishlistItemCount++;
				}
			}
		}
			pRequest.setParameter("wishlistItemCount",wishlistItemCount);
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
		}	
}