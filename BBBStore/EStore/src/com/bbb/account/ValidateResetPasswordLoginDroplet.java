/**
 * 
 */
package com.bbb.account;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import com.bbb.constants.BBBCoreConstants;

/**
 * @author ssi191 the purpose of this droplet is to fetch user's first name 
 *         and last name based on his email id on clicking Reset Password link
 *         on reset password mail.
 */
public class ValidateResetPasswordLoginDroplet extends BBBPresentationDroplet {
	private BBBProfileTools profileTools;
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
	 * fetch first name and last name of user from email id in reset password flow.
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
		logDebug("ValidateResetPasswordLoginDroplet.service() method Started");
		RepositoryItem userItemFromEmail=null;
		if (!StringUtils.isEmpty(pRequest.getParameter(BBBCoreConstants.FORGET_EMAIL)) || null !=pRequest.getParameter(BBBCoreConstants.FORGET_EMAIL)) {
			MutableRepositoryItem userProfileItem = null;
			userItemFromEmail=	getProfileTools().getItemFromEmail((String)pRequest.getParameter(BBBCoreConstants.FORGET_EMAIL));
			if (null != userItemFromEmail) {
				userProfileItem=(MutableRepositoryItem)userItemFromEmail;
				if (null != userProfileItem
						.getPropertyValue(BBBCoreConstants.FIRST_NAME)) {
					pRequest.setParameter(BBBCoreConstants.FIRST_NAME,(String) userProfileItem
							.getPropertyValue(BBBCoreConstants.FIRST_NAME));
				}
				if (null != userProfileItem
						.getPropertyValue(BBBCoreConstants.LAST_NAME)) {
					pRequest.setParameter(BBBCoreConstants.LAST_NAME,(String) userProfileItem
							.getPropertyValue(BBBCoreConstants.LAST_NAME));
				}
			}				
				
				pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM,
						pRequest, pResponse);

			} else {
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR,
						pRequest, pResponse);
			}
		} 
	
	/** Used in mobile to ensure user not using firstname or last name i  password whole reseting
	 * @return map 
	 */
	public Map<String, String> getUserNameByEmail() {
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		Map<String, String> responseMap = new HashMap<String, String>();
		try {
			service(ServletUtil.getCurrentRequest(),ServletUtil.getCurrentResponse());
			responseMap.put(BBBCoreConstants.FIRST_NAME,request.getParameter(BBBCoreConstants.FIRST_NAME));
			responseMap.put(BBBCoreConstants.LAST_NAME,	request.getParameter(BBBCoreConstants.LAST_NAME));

			} catch (ServletException e) {
			logError("ValidateResetPasswordLoginDroplet:ServletException",e);
			} catch (IOException e) {
			logError("ValidateResetPasswordLoginDroplet:IOException",e);
			}
		return responseMap;
	}
}
