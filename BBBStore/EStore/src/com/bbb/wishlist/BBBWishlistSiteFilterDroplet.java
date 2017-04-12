//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Kunjal Shah
//
//Created on: 10-November-2011
//--------------------------------------------------------------------------------

package com.bbb.wishlist;

import java.io.IOException;
import javax.servlet.ServletException;
import com.bbb.constants.BBBCoreConstants;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;

import atg.multisite.SiteContext;


public class BBBWishlistSiteFilterDroplet extends BBBDynamoServlet
{
	public static final ParameterName EMPTY = ParameterName.getParameterName("empty");
    private SiteContext mSiteContext;
    
    
    public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param mSiteContext
	 *            the mSiteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}
    
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		/**
		 * This method is used to compare the giftitem's siteid with the current site id
		 * and return giftItemId if siteid matches the current siteid.
		 * @param pRequest
		 * @param pResponse
		 * @throws ServletException
		 * @throws IOException
		 */
		
			logDebug("BBBWishlistSiteFilterDroplet.service() method started");
			
		/*final String giftItemSiteId =(String) pRequest.getObjectParameter("siteId");
		final String giftItemId = (String)pRequest.getObjectParameter("giftItemId");
		*/String currentSiteId = getSiteContext().getSite().getId();
		
		if (pRequest.getObjectParameter("siteId") != null
				&& pRequest.getObjectParameter("giftItemId") != null) {

			pRequest.setParameter("giftItemId",
					(String) pRequest.getObjectParameter("giftItemId"));
			if (((String) pRequest.getObjectParameter("siteId"))
					.equals(currentSiteId)) {
				pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM,
						pRequest, pResponse);

			}
		} else {
			pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
		}
		
		
			logDebug("BBBWishlistSiteFilterDroplet.service() method end");
		}

}
