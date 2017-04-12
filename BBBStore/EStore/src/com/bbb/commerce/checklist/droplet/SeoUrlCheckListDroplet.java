package com.bbb.commerce.checklist.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.utility.BBBGiftRegistryUtils;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;

public class SeoUrlCheckListDroplet extends BBBDynamoServlet {
	
	private BBBGiftRegistryUtils giftRegistryUtils; 
	
	/**
	 * @return the giftRegistryUtils
	 */
	public BBBGiftRegistryUtils getGiftRegistryUtils() {
		return giftRegistryUtils;
	}

	/**
	 * @param giftRegistryUtils the giftRegistryUtils to set
	 */
	public void setGiftRegistryUtils(BBBGiftRegistryUtils giftRegistryUtils) {
		this.giftRegistryUtils = giftRegistryUtils;
	}

	/**
	 * The method retrieves the SEOURL for the checklist category invoked
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		String seoURL= this.getGiftRegistryUtils().populateChecklistSEOUrl(pRequest,null);
		pRequest.setParameter(BBBCoreConstants.SEO_URL, seoURL);
		pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
	}
}
