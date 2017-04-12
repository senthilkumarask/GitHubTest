package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;



/**
 * 
 * This droplet used to load SkinnyVO in session if it not already loaded after post login.
 *
 */
public class AcceptableGiftRegistryInfoDroplet extends BBBPresentationDroplet
{

	/** The Gift registry manager. */
	private GiftRegistryManager	mGiftRegistryManager;

	
	/**
	 * This droplet's service method is used load registrySkinnyVOList after
	 * post login for check list flow as Interactive Ribbon being loaded before
	 * prime_gift_registry_data.jsp
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
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException
	{
		BBBPerformanceMonitor.start("AcceptableGiftRegistryInfoDroplet", "service");
		logDebug(" AcceptableGiftRegistryInfoDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		
		try
		{
			if (!sessionBean.isPrimeRegistryCompleted())
				{
					Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
					String siteId = SiteContextManager.getCurrentSiteId();
					this.logDebug("AcceptableGiftRegistryInfoDroplet profile:"+profile+",siteId:"+siteId);
					getGiftRegistryManager().getAcceptableGiftRegistries(profile, siteId);
					pRequest.serviceLocalParameter("output", pRequest, pResponse);
					logDebug("AcceptableGiftRegistryInfoDroplet,SkinnyVo successfully loaded In session");
				}
			
		}
		catch (Exception e)
		{
			
			pRequest.setParameter(OUTPUT_ERROR_MSG, "System Exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}
		
		logDebug(" AcceptableGiftRegistryInfoDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
		BBBPerformanceMonitor.end("AcceptableGiftRegistryInfoDroplet", "service");
	}

	

	/**
	 * Gets the gift registry manager.
	 * 
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager()
	{
		return mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager pGiftRegistryManager)
	{
		mGiftRegistryManager = pGiftRegistryManager;
	}

	

}
