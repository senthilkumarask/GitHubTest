package com.bbb.social.facebook.formhandler;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.social.facebook.FBProfileTools;

/**
 * logsin the user automatically without password
 *
 * @author manohar
 *
 */
public class FBConnectAutoSignInFormHandler extends BBBProfileFormHandler{

	private String mEmailId;

	private String mSuccessURL;

	private  String mErrorURL;

	/**
	 * Facebook Profile Tool instance
	 */
	private FBProfileTools mFbProfileTool;

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
	 *
	 * @return
	 */
	public String getSuccessURL() {
		return this.mSuccessURL;
	}

	/**
	 *
	 * @param pSuccessURL
	 */
	public void setSuccessURL(final String pSuccessURL) {
		this.mSuccessURL = pSuccessURL;
	}

	/**
	 *
	 * @return
	 */
	public String getErrorURL() {
		return this.mErrorURL;
	}

	/**
	 *
	 * @param pErrorURL
	 */
	public void setErrorURL(final String pErrorURL) {
		this.mErrorURL = pErrorURL;
	}

	/**
	 *
	 * @return
	 */
	public String getEmailId() {
		return this.mEmailId;
	}

	/**
	 *
	 * @param pEmailId
	 */
	public void setEmailId(final String pEmailId) {
		this.mEmailId = pEmailId;
	}

	/**
	 *
	 */
	private void preAutoLoginUser(){
		final String myHandleMethod = "FBConnectAutoSignInFormHandler.preAutoLoginUser";
			this.logDebug("Entry " + myHandleMethod);
	}

	/**
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleAutoLoginUser(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		final String myHandleMethod = "FBConnectAutoSignInFormHandler.handleAutoLoginUser";
			this.logDebug("Entry " + myHandleMethod);
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.AUTO_LOGIN_USER,
				myHandleMethod);

		this.preAutoLoginUser();
		final boolean result = this.autoSignIn(this.getEmailId(), pRequest);
		if (result) {
			pRequest.getSession().setAttribute(BBBCoreConstants.REDIRECT_URL,
					this.getSuccessURL());
		} else {
			pRequest.getSession().setAttribute(BBBCoreConstants.REDIRECT_URL,
					this.getErrorURL());
		}
		// PS-24181 Website Issue Reported - BV | In case of fbconnect or forgot password flow getLoginEmail() is null, which 
    	// inturn made bazaar_voice_token to be null and displaying incorrect value for email on review page so setting in request 
		pRequest.setAttribute("autoLoginEmail", this.getEmailId());
		this.postAutoLoginUser(pRequest, pResponse);

		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.AUTO_LOGIN_USER,
				myHandleMethod);
		return this.checkFormRedirect(this.getCreateSuccessURL(), this.getCreateErrorURL(),
				pRequest, pResponse);
	}

	/**
	 *
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private void postAutoLoginUser(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		final String myHandleMethod = "FBConnectAutoSignInFormHandler.postAutoLoginUser";
			this.logDebug("Entry " + myHandleMethod);
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.AUTO_LOGIN_USER,
				myHandleMethod);
		super.postLoginUser(pRequest, pResponse);
		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.AUTO_LOGIN_USER,
				myHandleMethod);
	}

	/**
	 *
	 * @param emailId
	 * @return
	 */
	private boolean autoSignIn(final String emailId, final DynamoHttpServletRequest pRequest) {

		final String myHandleMethod = "FBConnectAutoSignInFormHandler.autoSignIn";
			this.logDebug("Entry " + myHandleMethod);
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.AUTO_LOGIN_USER,
				myHandleMethod);

		final boolean result = false;
		/*Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		BBBProfileTools profileTools = (BBBProfileTools)getProfileTools();
		if (null == profile || null == profileTools || null == emailId) {
			result = false;
		} else {

			try {

				String bbbProfileEmailAddress = getFacebookProfileTool().getBBBEmailForFBProfile(emailId);


			} catch (BBBSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//(bbbUserProfile, SiteContextManager.getCurrentSiteId());

			profile.setDataSource(profileTools.getItemFromEmail(emailId));
			profile.setPropertyValue(FBConstants.SECURITY_STATUS, 4);
			result = true;
		}*/

		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.AUTO_LOGIN_USER,
				myHandleMethod);
		return result;
	}
}
