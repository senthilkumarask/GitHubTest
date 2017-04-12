/**
 * 
 */
package com.bbb.account;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;

/**
 * @author asha60 the purpose of this droplet is to validate the token coming in
 *         user request to identify whether user coming from genuine link if yes
 *         then store user profile information in session if not then redirect
 *         to error page
 * 
 */
public class ResetPasswordDroplet extends BBBPresentationDroplet {
	private BBBProfileTools profileTools;
	private MutableRepository profileRepository;
	private String forgotPasswordTokenQuery;
	private GiftRegistryTools giftRegistryTools;

	/**
	 * @return the giftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return giftRegistryTools;
	}

	/**
	 * @param giftRegistryTools
	 *            the giftRegistryTools to set
	 */
	public void setGiftRegistryTools(GiftRegistryTools giftRegistryTools) {
		this.giftRegistryTools = giftRegistryTools;
	}

	/**
	 * @return the forgotPasswordTokenQuery
	 */
	public String getForgotPasswordTokenQuery() {
		return forgotPasswordTokenQuery;
	}

	/**
	 * @param forgotPasswordTokenQuery
	 *            the forgotPasswordTokenQuery to set
	 */
	public void setForgotPasswordTokenQuery(String forgotPasswordTokenQuery) {
		this.forgotPasswordTokenQuery = forgotPasswordTokenQuery;
	}

	/**
	 * @return the mProfileRepository
	 */
	public MutableRepository getProfileRepository() {
		return profileRepository;
	}

	/**
	 * @param mProfileRepository
	 *            the mProfileRepository to set
	 */
	public void setProfileRepository(MutableRepository mProfileRepository) {
		this.profileRepository = mProfileRepository;
	}

	/**
	 * @return the profileTools
	 */
	public BBBProfileTools getProfileTools() {
		return profileTools;
	}

	/**
	 * @param profileTools
	 *            the profileTools to set
	 */
	public void setProfileTools(BBBProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	/**
	 * check if token provided by the user in request is valid or not .if valid
	 * then redirect customer to change password screen.Also redirect to user to change PAssword screen
	 * if user has setup challenge Question and doing Reset password
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws ServletException
	 *             , IOException
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("ResetPasswordDroplet.service() method Started");
		BBBSessionBean sessionBean = ((BBBSessionBean) (pRequest
				.resolveName(BBBCoreConstants.SESSION_BEAN)));

		String receivedToken = pRequest.getParameter(BBBCoreConstants.TOKEN);
		RepositoryItem userItemFromEmail=null;
		if (!StringUtils.isEmpty(receivedToken) || null !=pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL)) {
			RepositoryItem[] userProfileRepositoryItem = null;
			MutableRepositoryItem userProfileItem = null;
			
			final Object[] params = new Object[1];
			params[0] = receivedToken;
			try {
				if(null !=receivedToken){
				userProfileRepositoryItem = getProfileTools().executeRQLQuery(
						this.getForgotPasswordTokenQuery(), params,
						BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME,
						this.getProfileRepository());
				}
				if(null !=pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL))
				{
					 userItemFromEmail=	getProfileTools().getItemFromEmail((String)pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL));
				}

			} catch (BBBSystemException e) {
				logError(
						LogMessageFormatter.formatMessage(
								pRequest,
								"BBBBusinessException from service of ResetPasswordDroplet",
								BBBCoreErrorConstants.ERR_FETCHING_TOKEN), e);
				pRequest.setParameter(OUTPUT_ERROR_MSG, "ERR_FETCHING_TOKEN");
				pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest,
						pResponse);
			} catch (BBBBusinessException e) {
				logError(
						LogMessageFormatter.formatMessage(
								pRequest,
								"BBBBusinessException from service of ResetPasswordDroplet",
								BBBCoreErrorConstants.ERR_FETCHING_TOKEN), e);
				pRequest.setParameter(OUTPUT_ERROR_MSG, "ERR_FETCHING_TOKEN");
				pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
			if (null != userProfileRepositoryItem
					&& userProfileRepositoryItem.length > 0 || null !=userItemFromEmail ) {
			
				if(null !=userItemFromEmail)
				{
				userProfileItem=(MutableRepositoryItem)userItemFromEmail;
				}
				else
				{
					userProfileItem = (MutableRepositoryItem) userProfileRepositoryItem[0];
					
				}
				AccountVo accvo = new AccountVo();
				if (null != userProfileItem
						.getPropertyValue(BBBCoreConstants.EMAIL)) {
					accvo.setEmail((String) userProfileItem
							.getPropertyValue(BBBCoreConstants.EMAIL));
				}
				if (null != userProfileItem
						.getPropertyValue(BBBCoreConstants.PASSWORD)) {
					accvo.setPassword((String) userProfileItem
							.getPropertyValue(BBBCoreConstants.PASSWORD));
				}
				if (null != userProfileItem
						.getPropertyValue(BBBCoreConstants.FIRST_NAME)) {
					accvo.setFirstName((String) userProfileItem
							.getPropertyValue(BBBCoreConstants.FIRST_NAME));
				}
				if (null != userProfileItem
						.getPropertyValue(BBBCoreConstants.LAST_NAME)) {
					accvo.setLastName((String) userProfileItem
							.getPropertyValue(BBBCoreConstants.LAST_NAME));
				}
				sessionBean.setAccountVo(accvo);
				pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM,
						pRequest, pResponse);

			} else {
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR,
						pRequest, pResponse);
			}
		} else if (null !=sessionBean && null != sessionBean.getAccountVo()) {
			pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest,
					pResponse);
		}
		else{
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR,
					pRequest, pResponse);
		}

	}
}
