/**
 * 
 */
package com.bbb.social.facebook.droplet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.Site;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.social.facebook.FBConstants;
import com.bbb.social.facebook.FBProfileTools;
import com.bbb.social.facebook.vo.UserVO;

/**
 * The droplet identifies those sister sites where FBConnect is enabled or any BBBUser profile exists against FBEmail 
 * 
 * @author jsidhu
 *
 */
public class FBUserSiteAssocDroplet extends BBBDynamoServlet {
	
	private static final String SISTERSITE = "sisterSite";
	private static final String PROFILELOOKUP = "profileLookup";
	private static final String SITE_ID = "siteId";
	private static final String ERROR_MSG = "errorMsg";
	private BBBProfileTools mProfileTools;
	/**
	 * Facebook Profile Tool instance
	 */
	private FBProfileTools mFbProfileTool;
	/**
	 * BBB Profile property manager instance
	 */
	private BBBPropertyManager mBBBProfilePropertyManager;
	/**
	 * @return the BBB profile property Manager
	 */
	public final BBBPropertyManager getBbbProfilePropertyManager() {
		return mBBBProfilePropertyManager;
	}

	/**
	 * @param pBBBProfilePropertyManager 
	 *            the BBB profile property Manager to set
	 */
	public final void setBbbProfilePropertyManager(BBBPropertyManager pBBBProfilePropertyManager) {
		mBBBProfilePropertyManager = pBBBProfilePropertyManager;
	}
	
	/**
	 * @return the profileTools
	 */
	public BBBProfileTools getProfileTools() {
		return mProfileTools;
	}

	/**
	 * @param profileTools the profileTools to set
	 */
	public void setProfileTools(BBBProfileTools profileTools) {
		mProfileTools = profileTools;
	}
	/**
	 * @return the mFacebookProfileTool
	 */
	public FBProfileTools getFacebookProfileTool() {
		return mFbProfileTool;
	}

	/**
	 * @param pFacebookProfileTool the mFacebookProfileTool to set
	 */
	public void setFacebookProfileTool(FBProfileTools pFbProfileTool) {
		this.mFbProfileTool = pFbProfileTool;
	}

	/**
	 * The method will return the Sister site name for BBBProfile.
	 * The output parameters are:
	 *  - sisterSite - Sister site name
	 *  
	 */
	public void service (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		String searchBasedOn=pRequest.getParameter(PROFILELOOKUP);
		String siteId=pRequest.getParameter(SITE_ID);
		RepositoryItem bbbUserProfile =null;
		List<Site> sisterSiteList = null;
		UserVO fbUser = (UserVO) pRequest.getSession().getAttribute(FBConstants.FB_BASIC_INFO);
		try{
			if(fbUser != null && searchBasedOn!=null){ 
				if(searchBasedOn.equalsIgnoreCase(FBConstants.PROFILE_LOOKUP_BASED_ON_FBACCOUNTID))
				{
					String bbbUserEmail=getFacebookProfileTool().getSisterSiteBBBEmailForFBProfile(fbUser.getUserName());
					bbbUserProfile = getProfileTools().getItemFromEmail(bbbUserEmail);
				}
				else if(searchBasedOn.equalsIgnoreCase(FBConstants.PROFILE_LOOKUP_BASED_ON_FBEMAILID))
				{
					bbbUserProfile = getProfileTools().getItemFromEmail(fbUser.getEmail());
				}				
				if(bbbUserProfile != null){			
					Map<String, Object> bbbUserCurrSiteMap = (Map<String, Object>) bbbUserProfile.getPropertyValue(getBbbProfilePropertyManager().getUserSiteItemsPropertyName());
					logDebug("bbbUserCurrSiteMap:" + bbbUserCurrSiteMap);
					sisterSiteList = getFacebookProfileTool().getSisterSite(bbbUserCurrSiteMap,  siteId);				
					if(sisterSiteList!=null && sisterSiteList.size()>0)
					{
						for(Site site : sisterSiteList){
							pRequest.setParameter (SISTERSITE, site.getId());								
						}						
					}	
					pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
				}else{
					pRequest.setParameter (ERROR_MSG, "No BBB Profile Found");
					pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
				}			
			}
		}
		catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException in Facebook : Error occured while getting BBB profile details through Facebook profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1235 ), e);
			pRequest.setParameter (ERROR_MSG, e.getMessage());
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		}	
	}
	
}