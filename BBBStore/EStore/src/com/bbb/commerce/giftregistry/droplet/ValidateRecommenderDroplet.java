//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit
//written consent is prohibited.
//
//
//
//Created on: 28-December-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.giftregistry.droplet;

import static com.bbb.constants.BBBAccountConstants.PARAM_ERROR_MSG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;


/**
 * The Class MyRegistriesDisplayDroplet.
 */
public class ValidateRecommenderDroplet extends BBBPresentationDroplet {

	/** The Gift registry tools. */
	private GiftRegistryTools mGiftRegistryTools;
	/** Constants for string literal profile. */
	private static final String REGISTRY_ID = "registryId";
	/** Constants for string literal site id. */
	private static final String SITE_ID = "siteId";

	private static final String IS_FROM_FACEBOOK = "isFromFB";

	/** The Constant TOKEN. */
	private static final String TOKEN = "token";

	private static final String ERROR_INVALID_TOKEN = "err_invalid_token";
	private static final String ERROR_TOKEN_EXPIRED = "err_token_expired";
	private static final String ERROR_INVALID_REGISTRY = "err_invalid_registry";
	private static final String ERROR_PRIVATE_REGISTRY = "err_private_registry";

	private static final String RECOMMENDED_REGISTRY = "recommendedRegistry";
	private static final String RECOMMENDED_TOKEN = "recommendedToken";
	private static final String RECOMMENDER_LANDING_PAGE = "RLP";
	private static final String GIFT_GIVER_PAGE = "VRR";
	private static final String RECOMMENDER_IS_FROM_FB ="isRecommenderFromFB";
	private GiftRegistryManager giftRegistryManager;

	/**
	 * The method gets the user's registries and corresponding details from the
	 * web service.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "deprecation", "unchecked"})
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException,
			IOException {
		logDebug("Entering Service Method of ValidateRecommenderDroplet.");
		String siteId = request.getParameter(SITE_ID);
		if (siteId == null) {
			siteId = SiteContextManager.getCurrentSiteId();
		}
		Profile profile = (Profile) request.resolveName(BBBCoreConstants.ATG_PROFILE);
		String profileID = profile.getRepositoryId();
		String registryId = request.getParameter(REGISTRY_ID);
		String token = request.getParameter(TOKEN);
		String isFromFB = request.getParameter(IS_FROM_FACEBOOK);
		if(isFromFB == null)
			isFromFB = BBBCoreConstants.FALSE;
		BBBSessionBean sessionBean = (BBBSessionBean) request.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		String fromURI = request.getParameter("fromURI");
		logDebug("Profile ID:"+profileID+" registryId: "+registryId+" token:"+token + "isFromFacebook:" +  isFromFB);
		try {
			if(fromURI.equals(RECOMMENDER_LANDING_PAGE)) {
				logDebug("From Recommender Landing Page(RLP)");

				// Check if the token and registry are valid - uses recommenderRegistryRepository for these validation
				// If status is 1 i.e. token is valid we save registry and token values in sessionbean which will be used to persist
				// profile and registry relation once user do log in.
				int status = getGiftRegistryTools().validateToken(registryId, token);
				if(status == BBBGiftRegistryConstants.PRIVATE_REGISTRY) {
					logError("Registry is private: "+registryId);
					request.setParameter(PARAM_ERROR_MSG, ERROR_PRIVATE_REGISTRY);
					request.serviceLocalParameter(OPARAM_ERROR, request, response);
					return;
				}else if(status == BBBGiftRegistryConstants.INVALID_REGISTRY) {
					logError("Registry invalid or Expired: "+registryId+" token"+token);
					request.setParameter(PARAM_ERROR_MSG,ERROR_INVALID_REGISTRY);
					request.serviceLocalParameter(OPARAM_ERROR, request, response);
					return;
				} else if(status == BBBGiftRegistryConstants.INVALID_TOKEN) {
					logError("Invalid Token for Profile ID:"+profileID+" registryId:"+registryId+" token"+token);
					request.setParameter(PARAM_ERROR_MSG,ERROR_INVALID_TOKEN);
					request.serviceLocalParameter(OPARAM_ERROR, request, response);
				} else if(status == BBBGiftRegistryConstants.TOKEN_EXPIRED) {
					logError("Token expired for Profile ID:"+profileID+" registryId:"+registryId+" token"+token);
					request.setParameter(PARAM_ERROR_MSG,ERROR_TOKEN_EXPIRED);
					request.serviceLocalParameter(OPARAM_ERROR, request, response);
				} else if(status == BBBGiftRegistryConstants.VALID_TOKEN) {
					logDebug("Valid Token for Profile ID:"+profileID+" registryId:"+registryId+" token:"+token);
					sessionBean.getValues().put(RECOMMENDED_REGISTRY, registryId);
					sessionBean.getValues().put(RECOMMENDED_TOKEN, token);
					sessionBean.getValues().put(RECOMMENDER_IS_FROM_FB, isFromFB);
					request.serviceLocalParameter(OPARAM_OUTPUT, request, response);
				}
			} else if (fromURI.equals(GIFT_GIVER_PAGE)) {
				logDebug("From View Registry Recommender(VRR) Flow");
				if(null != sessionBean.getValues().get(RECOMMENDED_REGISTRY)) {
	        		String recommendedRegistry = sessionBean.getValues().get(RECOMMENDED_REGISTRY).toString();
	        		String recommendedToken = sessionBean.getValues().get(RECOMMENDED_TOKEN).toString();
	        		String recommenderIsFromFB = sessionBean.getValues().get(RECOMMENDER_IS_FROM_FB).toString();
	        		RegistrySummaryVO registrySummaryVO =
	        				getGiftRegistryManager().persistRecommenderReln(recommendedRegistry, recommendedToken,recommenderIsFromFB);

	        		List<RegistrySummaryVO> sessionRegistryVOList = sessionBean.getRegistrySummaryVO();

					//if sessionbean has some recommender registry list then add in list else create new list in session bean
	        		logDebug("From View Registry Recommender(VRR) Flow");
					if(null != sessionBean.getRegistrySummaryVO() && null != registrySummaryVO) {
						// sessionContainsRegistry checks for duplicate registry ID in case its present in sesssion
						boolean sessionContainsRegistry = false;
		        		for(RegistrySummaryVO sessionRegistryVO : sessionRegistryVOList) {
		        			if(sessionRegistryVO.getRegistryId().
		        					equalsIgnoreCase(registrySummaryVO.getRegistryId())) {
		        				sessionContainsRegistry = true;
		        				break;
		        			}
		        		}
		        		logDebug("Is Registry "+registrySummaryVO.getRegistryId()+" duplicate in session:"+sessionContainsRegistry);
						if(!sessionContainsRegistry) {
							sessionBean.getRegistrySummaryVO().add(registrySummaryVO);
						}
					} else if(null != registrySummaryVO) {
						logDebug("Session do not have registry to recommend so creating new List");
						List<RegistrySummaryVO> registrySummaryVOList = new ArrayList<RegistrySummaryVO>();
						registrySummaryVOList.add(registrySummaryVO);
						sessionBean.setRegistrySummaryVO(registrySummaryVOList);
					}
				}
			}
		logDebug("ValidateRecommenderDroplet Method Ends.");
		} catch (BBBSystemException e) {
			logError("Invalid Details in ValidateRecommender() Profile ID:"+profileID+" registryId:"+registryId+" token"+token);
			request.setParameter(PARAM_ERROR_MSG,ERROR_INVALID_REGISTRY);
			request.serviceLocalParameter(OPARAM_ERROR, request, response);
		}
	}

	/**
	 * Sets the gift registry tools.
	 *
	 * @param giftRegistryTools
	 *            the giftRegistryTools to set
	 */
	public void setGiftRegistryTools(GiftRegistryTools giftRegistryTools) {
		mGiftRegistryTools = giftRegistryTools;
	}

	/**
	 * Gets the gift registry tools.
	 *
	 * @return the giftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return mGiftRegistryTools;
	}

	/** @return the giftRegistryManager */
    public final GiftRegistryManager getGiftRegistryManager() {
        return this.giftRegistryManager;
    }

    /** @param pGiftRegistryManager the giftRegistryManager to set */
    public final void setGiftRegistryManager(final GiftRegistryManager pGiftRegistryManager) {
        this.giftRegistryManager = pGiftRegistryManager;
    }

}
