package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.constants.BBBGiftRegistryConstants;

public class ValidateRegistryDroplet extends BBBPresentationDroplet {
	
	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/** Profile reference variable. */
	private Profile mProfile;

	/** The Site context. */
	private SiteContext mSiteContext;
	
	/**
	 * 
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		
		String registryId = pRequest
				.getParameter(BBBGiftRegistryConstants.REGISTRY_ID);
		
		boolean validCheck = getGiftRegistryManager().isUserOwnRegistry(this.getProfile(), registryId, pRequest);
		if(validCheck){
			pRequest.serviceParameter("valid", pRequest, pResponse);	
		}else{
			pRequest.serviceParameter("inValid", pRequest, pResponse);
		}

	}

	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	public void setGiftRegistryManager(GiftRegistryManager mGiftRegistryManager) {
		this.mGiftRegistryManager = mGiftRegistryManager;
	}

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile mProfile) {
		this.mProfile = mProfile;
	}

	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	public void setSiteContext(SiteContext mSiteContext) {
		this.mSiteContext = mSiteContext;
	}
}
