/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  FBConnectFormHandler.java
 *
 *  DESCRIPTION: FBConnectFormHandler extends ATG OOTB CommerceProfileFormHandler
 *  			 and perform form handling activities related to facebook user profile. 	
 *  HISTORY:
 *  02/02/12 Initial version
 *  	
 */
package com.bbb.social.facebook.formhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import atg.droplet.DropletException;
import atg.multisite.SiteContext;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.account.BBBProfileTools;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.social.facebook.FBConstants;
import com.bbb.social.facebook.FBProfileTools;
import com.bbb.social.facebook.PropertyManager;
import com.bbb.social.facebook.api.FBResponseParser;
import com.bbb.social.facebook.vo.UserVO;
import com.bbb.utils.BBBUtility;

/**
 * @author hbandl
 * 
 */
public class FBConnectFormHandler extends BBBGenericFormHandler {

	private SiteContext mSiteContext;
	/**
	 * FB email id to be register
	 */
	private String mEmailAddress;

	/**
	 * event that needs to triggered against provided facebook id
	 */
	private String mEvent;

	/**
	 * FB basic info
	 */
	private String mFBBasicInfo;

	/**
	 * CMS Label Manager instance
	 */
	private LblTxtTemplateManager mLabelManager;

	/**
	 * Facebook Profile Tool instance
	 */
	private FBProfileTools mFbProfileTool;

	/**
	 * BBBProfile Tool instance
	 */
	private PropertyManager mFbPropertyManager;

	/**
	 * BBB Profile property manager instance
	 */
	private BBBPropertyManager mBBBProfilePropertyManager;

	/**
	 * type of FBResponseParser object
	 */
	private FBResponseParser mFBResponseParser;

	/**
	 * success URL
	 */
	private String mSuccessURL;

	/**
	 * error URL
	 */
	private String mErrorURL;

	/**
	 * page section request parameter
	 */
	private String pageSection;
	
	/**
	 * form error map
	 */
	private Map<String, String> mErrorMap;
	
	/**
	 * bbbProfileTools
	 */
	private BBBProfileTools mProfileTools;
	
	/**
	* FB account id
	*/
	private String mFBAccountId;
	
	/**
	 * FB User Name
	 */
	private String mFbFullName;
	
	/**
	 * @return the mFbUserName
	 */
	public String getFbFullName() {
		return mFbFullName;
	}

	/**
	 * @param pFbFullName the pFbFullName to set
	 */
	public void setFbFullName(String pFbFullName) {
		this.mFbFullName = pFbFullName;
	}

	/**
	* @return the mFBAccountId
	*/
	public String getFbAccountId() {
		return mFBAccountId;
	}

	/**
	* @param mFBAccountId the mFBAccountId to set
	*/
	public void setFbAccountId(String pFBAccountId) {
		this.mFBAccountId = pFBAccountId;
	}

	/**
	 * @return the mProfileTools
	 */
	public BBBProfileTools getProfileTools() {
		return mProfileTools;
	}

	/**
	 * @param mProfileTools the mProfileTools to set
	 */
	public void setProfileTools(BBBProfileTools pProfileTools) {
		this.mProfileTools = pProfileTools;
	}

	/**
	 * @return the errorMap
	 */
	public Map<String, String> getErrorMap() {
		return mErrorMap;
	}

	/**
	 * @param pErrorMap
	 *            the errorMap to set
	 */
	public void setErrorMap(Map<String, String> pErrorMap) {
		mErrorMap = pErrorMap;
	}
	
	/**
	 * @return the pageSection
	 */
	public String getPageSection() {
		return pageSection;
	}

	/**
	 * @param pageSection
	 *            the pageSection to set
	 */
	public void setPageSection(String pageSection) {
		this.pageSection = pageSection;
	}

	/**
	 * @return mSuccessURL
	 */
	public String getSuccessURL() {
		return mSuccessURL;
	}

	/**
	 * 
	 * @param pSuccessURL
	 *            pSuccessURL to set
	 */
	public void setSuccessURL(String pSuccessURL) {
		this.mSuccessURL = pSuccessURL;
	}

	/**
	 * 
	 * @return mErrorURL
	 */
	public String getErrorURL() {
		return mErrorURL;
	}

	/**
	 * 
	 * @param pErrorURL
	 *            pErrorURL to set
	 */
	public void setErrorURL(String pErrorURL) {
		this.mErrorURL = pErrorURL;
	}

	/**
	 * @return the mFBResponseParser
	 */
	public FBResponseParser getFbResponseParser() {
		return mFBResponseParser;
	}

	/**
	 * @param pFBResponseParser
	 *            the pFBResponseParser to set
	 */
	public void setFbResponseParser(FBResponseParser pFBResponseParser) {
		this.mFBResponseParser = pFBResponseParser;
	}

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
	public final void setBbbProfilePropertyManager(
			BBBPropertyManager pBBBProfilePropertyManager) {
		mBBBProfilePropertyManager = pBBBProfilePropertyManager;
	}

	/**
	 * @return mEvent
	 */
	public String getEvent() {
		return mEvent;
	}

	/**
	 * @param pEvent
	 *            the event that represent operation that needs to perform
	 *            aginst provided facebook id.
	 */
	public void setEvent(String pEvent) {
		this.mEvent = pEvent;
	}

	/**
	 * @return mSiteContext
	 */
	public PropertyManager getFbPropertyManager() {
		return mFbPropertyManager;
	}

	/**
	 * @param pFBAccountId
	 *            the mSiteContext to set
	 */
	public void setFbPropertyManager(PropertyManager pFbPropertyManager) {
		this.mFbPropertyManager = pFbPropertyManager;
	}

	/**
	 * @return mFBBasicInfo
	 */
	public String getFbBasicInfo() {
		return mFBBasicInfo;
	}

	/**
	 * @param pFBBasicInfo
	 *            the pFBBasicInfo to set
	 */
	public void setFbBasicInfo(String pFBBasicInfo) {
		this.mFBBasicInfo = pFBBasicInfo;
	}

	/**
	 * @return the mFacebookProfileTool
	 */
	public FBProfileTools getFacebookProfileTool() {
		return mFbProfileTool;
	}

	/**
	 * @param pFacebookProfileTool
	 *            the mFacebookProfileTool to set
	 */
	public void setFacebookProfileTool(FBProfileTools pFbProfileTool) {
		this.mFbProfileTool = pFbProfileTool;
	}

	/**
	 * @return the labelManager
	 */
	public final LblTxtTemplateManager getLabelManager() {
		return mLabelManager;
	}

	/**
	 * @param pLabelManager
	 *            the labelManager to set
	 */
	public final void setLabelManager(LblTxtTemplateManager pLabelManager) {
		mLabelManager = pLabelManager;
	}

	/**
	 * @return mEmailAddress
	 */
	public String getEmailAddress() {
		return mEmailAddress;
	}

	/**
	 * @param pEmailAddress
	 */
	public void setEmailAddress(String pEmailAddress) {
		this.mEmailAddress = pEmailAddress;
	}

	// -------------------------------------
	// property: TransactionManager
	// -------------------------------------
	private TransactionManager mTransactionManager;

	/**
	 * Sets property TransactionManager
	 * 
	 * @param pTransactionManager
	 *            a <code>TransactionManager</code> value
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * Returns property TransactionManager
	 * 
	 * @return a <code>TransactionManager</code> value
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

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
	
	/**
	 * Constructor of BBBForgotPasswordHandler
	 */
	public FBConnectFormHandler() {
		mErrorMap = new HashMap<String, String>();
	}
	
	/**
	 * this method checks if Facebook profile is connected with any of
	 * BBBProfile on the basis of Facebook user information provided.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleCheckFBConnect(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String opName = "FBConnectFormHandler.handleCheckFBConnect()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
		logDebug(opName + " : Start");
		String event = "";
		logDebug("getFbBasicInfo():" + getFbBasicInfo());
		if (!BBBUtility.isEmpty(getFbBasicInfo())) {
			try {
				logDebug("Start Parsing");
				UserVO fbUserVO = getFbResponseParser().parseFacebookBasicInfo(
						getFbBasicInfo());
				logDebug("Parsing End:" + fbUserVO);

				if (fbUserVO != null) {
					pRequest.getSession().setAttribute(
							FBConstants.FB_BASIC_INFO, fbUserVO);
					
					RepositoryItem bbbProfile = getProfileTools().getItemFromEmail(
							fbUserVO.getEmail());
					if(checkMigratedUser(bbbProfile)){
						event = FBConstants.EVENT_MIGRATED_USER;
						pRequest.getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, fbUserVO.getEmail());
					}else{
						RepositoryItem fbUserRepositoryItem = getFacebookProfile(
								fbUserVO, pRequest);
						if (fbUserRepositoryItem != null) {
							RepositoryItem bbbUserProfile = (RepositoryItem) fbUserRepositoryItem
									.getPropertyValue(getFbPropertyManager()
											.getBBBProfilePropertyName());
							logDebug("bbbUserProfile:" + bbbUserProfile);
							event = getFacebookProfileTool()
									.findUserSiteAssociation(bbbUserProfile,
											getSiteContext().getSite().getId());
							if (!event.equalsIgnoreCase(FBConstants.EMPTY_STRING)) {
								event = event
										+ FBConstants.EVENT_SUFFIX_FB_CONNECTED;
							} else {
								event = FBConstants.EVENT_FB_PROFILE_FOUND_NOT_LINKED_WITH_BBB_PROFILE;
							}
							if (getPageSection() != null
									&& getPageSection().equalsIgnoreCase(
											FBConstants.FB_PAGE_ACCOUNT_OVERVIEW)) {
								RepositoryItem profile = ServletUtil.getCurrentUserProfile();
								if (profile != null && bbbUserProfile != null && bbbUserProfile.getRepositoryId() != profile.getRepositoryId()) {
									event = "";
									mErrorMap.put(FBConstants.ERROR_MESSAGE, getLabelManager()
											.getErrMsg(FBConstants.ERROR_FB_ALREADY_LINKED,
													pRequest.getLocale().getLanguage(), null, null));
									addFormException(new DropletException(getLabelManager()
											.getErrMsg(FBConstants.ERROR_FB_ALREADY_LINKED,
													pRequest.getLocale().getLanguage(), null, null),FBConstants.ERROR_FB_ALREADY_LINKED));
								}
							}
						} else {
							RepositoryItem bbbUserProfile = getProfileTools().getItemFromEmail(
											fbUserVO.getEmail());
							if (bbbUserProfile != null) {
								event = getFacebookProfileTool()
										.findUserSiteAssociation(bbbUserProfile,
												getSiteContext().getSite().getId());
								if (BBBUtility.isEmpty(event)) {
									event = FBConstants.EVENT_PROFILE_NOT_FOUND;
								}
							} else {
								event = FBConstants.EVENT_PROFILE_NOT_FOUND;
							}
						}
					}
					setEvent(event);
				} else {
					addFormException(new DropletException(getLabelManager()
							.getErrMsg(FBConstants.ERROR_FB_UNEXPECTED,
									pRequest.getLocale().getLanguage(), null,
									null),FBConstants.ERROR_FB_UNEXPECTED));
				}
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, FBConstants.ERROR_FB_UNEXPECTED , BBBCoreErrorConstants.ACCOUNT_ERROR_1125),e);
					addFormException(new DropletException(e.getMessage(),e.getErrorCode()));
			}
		} else if (getPageSection() != null
				&& getPageSection().equalsIgnoreCase(
						FBConstants.FB_PAGE_ACCOUNT_OVERVIEW)) {
			RepositoryItem profile = ServletUtil.getCurrentUserProfile();
			if (profile != null) {
				RepositoryItem fbUserProfile = (RepositoryItem) profile
						.getPropertyValue(getFbPropertyManager()
								.getFacebookProfilePropertyName());
				if(fbUserProfile != null){
					event = FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT;	
				}
				
				setEvent(event);
			}
		}
		logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return true;
	}

	/**
	 * this method link the logged in BBB user with Facebook.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleLinkingWithoutLogin(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String opName = "FBConnectFormHandler.handleLinkingWithoutLogin()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
		logDebug(opName + " : Start");
		boolean status = false;
		RepositoryItem bbbUserProfile = ServletUtil.getCurrentUserProfile();
		try {
			UserVO fbUser = (UserVO) pRequest.getSession().getAttribute(
					FBConstants.FB_BASIC_INFO);
			RepositoryItem facebookProfile = getFacebookProfileTool()
					.linkBBBProfileWithFBProfile(bbbUserProfile, fbUser);
			if (facebookProfile != null) {
				status = true;
				pResponse.setHeader(FBConstants.FB_HEADER_PARAM,
						getSuccessURL());
			}else {
				addFormException(new DropletException(getLabelManager()
						.getErrMsg(FBConstants.ERROR_FB_ALREADY_LINKED,
								pRequest.getLocale().getLanguage(), null, null),FBConstants.ERROR_FB_ALREADY_LINKED));
			}
			
		} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, FBConstants.ERROR_FB_UNEXPECTED , BBBCoreErrorConstants.ACCOUNT_ERROR_1125),e);
				addFormException(new DropletException(getLabelManager()
						.getErrMsg(FBConstants.ERROR_FB_UNEXPECTED,
								pRequest.getLocale().getLanguage(), null, null),FBConstants.ERROR_FB_UNEXPECTED));
		}
		logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return status;
	}

	/**
	 * this method unlink the logged in BBB user with Facebook.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleUnLinking(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String opName = "FBConnectFormHandler.handleUnLinking()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
		logDebug(opName + " : Start");
		boolean status = false;
		RepositoryItem bbbUserProfile = ServletUtil.getCurrentUserProfile();
		try {
			status = getFacebookProfileTool().unlinkBBBProfileWithFBProfile(
					bbbUserProfile);
			if (status) {
				setEvent(FBConstants.FB_UNLINK_SUCCESS);
			} else {
				setEvent(FBConstants.FB_UNLINK_ERROR);
			}
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, FBConstants.ERROR_FB_UNEXPECTED , BBBCoreErrorConstants.ACCOUNT_ERROR_1125),e);
			addFormException(new DropletException(getLabelManager()
						.getErrMsg(FBConstants.ERROR_FB_UNEXPECTED,
								pRequest.getLocale().getLanguage(), null, null),FBConstants.ERROR_FB_UNEXPECTED));
		}
		logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return status;
	}

	

	/**
	 * method to fetch the facbook profile on the basis of Facebook Account id
	 * provided.
	 * 
	 * @param fbUserVO
	 *            facebook user details
	 * @return facebook user object.
	 * 
	 */
	private RepositoryItem getFacebookProfile(UserVO fbUserVO,
			DynamoHttpServletRequest pRequest) {

		String opName = "FBConnectFormHandler.getFacebookProfile()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
		RepositoryItem user = null;
		try {
			user = getFacebookProfileTool().getFacebookUserProfile(
					fbUserVO.getUserName());
		} catch (BBBSystemException e) {
			addFormException(new DropletException(getLabelManager().getErrMsg(
					FBConstants.ERROR_FB_UNEXPECTED,
					pRequest.getLocale().getLanguage(), null, null),FBConstants.ERROR_FB_UNEXPECTED));
			logError(LogMessageFormatter.formatMessage(pRequest, FBConstants.ERROR_FB_UNEXPECTED , BBBCoreErrorConstants.ACCOUNT_ERROR_1125),e);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return user;
	}

	

	/**
	 * this method checks if BBB profile already exist for Facebook id provided
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleBbbProfileExist(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String opName = "FBConnectFormHandler.handleBbbProfileExist";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
		logDebug(opName + " : Start");
		String event = "";
		UserVO fbUser = (UserVO) pRequest.getSession().getAttribute(
				FBConstants.FB_BASIC_INFO);
		if (fbUser != null) {
			RepositoryItem bbbUserProfile = getProfileTools().getItemFromEmail(fbUser.getEmail());
			if (bbbUserProfile != null) {
				event = getFacebookProfileTool().findUserSiteAssociation(
						bbbUserProfile, getSiteContext().getSite().getId());
				if (event.equalsIgnoreCase(FBConstants.EMPTY_STRING)) {
					event = FBConstants.EVENT_PROFILE_NOT_FOUND;
				}
			} else {
				event = FBConstants.EVENT_PROFILE_NOT_FOUND;
				if (getPageSection() != null
						&& getPageSection().equalsIgnoreCase(
								FBConstants.PAGE_SECTION_LOGIN_REG)) {
					pResponse.setHeader(FBConstants.FB_HEADER_PARAM,
							getSuccessURL());
				}
			}
			setEvent(event);

		} else {
			addFormException(new DropletException(getLabelManager().getErrMsg(
					FBConstants.ERROR_FB_UNEXPECTED,
					pRequest.getLocale().getLanguage(), null, null),FBConstants.ERROR_FB_UNEXPECTED));

		}
		logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return true;
	}

	

	/**
	 * method to fetch Facebook Id against logged in user profile
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleGetFacebookId(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException,
            IOException {
		
	     String opName = "FBConnectFormHandler.handleGetFacebookId()";
	     BBBPerformanceMonitor.start(
	                  BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
	     logDebug(opName + " : Start");
	     
	     RepositoryItem bbbUserProfile = ServletUtil.getCurrentUserProfile();
	     RepositoryItem fbProfile = (RepositoryItem) bbbUserProfile.getPropertyValue(getFbPropertyManager().getFacebookProfilePropertyName());
	     if(fbProfile != null){
	    	 	setFbFullName((String)fbProfile.getPropertyValue(getFbPropertyManager().getNamePropertyName()));
	            setFbAccountId((String)fbProfile.getPropertyValue(getFbPropertyManager().getUserNamePropertyName()));
	     }
	     BBBPerformanceMonitor.end(
                 BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
	     logDebug(opName + " : End");
	     return true;
	     
	}


	/**
	 * set the response header and as success URL
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */

	public boolean handleFbRedirect(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String opName = "FBConnectFormHandler.handleFbRedirect";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
		logDebug(opName + " : Start");

		pResponse.setHeader(FBConstants.FB_HEADER_PARAM, getSuccessURL());

		logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return true;

	}
	
	/**
	 * method to fetch BBB profile email id for a Facebook profile
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleGetBBBProfileEmailId(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException,
            IOException {
		
	     String opName = "FBConnectFormHandler.handleGetBBBProfileEmailId()";
	     BBBPerformanceMonitor.start(
	                  BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
	     logDebug(opName + " : Start");
	     
	     UserVO fbUser = (UserVO) pRequest.getSession().getAttribute(FBConstants.FB_BASIC_INFO);
	     if(fbUser != null){
	    	 try {
				setEmailAddress(getFacebookProfileTool().getSisterSiteBBBEmailForFBProfile(fbUser.getUserName()));
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "Error occured while fetching BBB profile email address for FB User" , BBBCoreErrorConstants.ACCOUNT_ERROR_1236),e);
			} 
	     }
	     BBBPerformanceMonitor.end(
                 BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
	     logDebug(opName + " : End");
	     return true;
	     
	}
	
	/**
	 * This method ensures that a transaction exists before returning. If there
	 * is no transaction, a new one is started and returned. In this case, you
	 * must call commitTransaction when the transaction completes.
	 * 
	 * @return a <code>Transaction</code> value
	 */
	protected Transaction ensureTransaction() {
		String opName = "FBConnectFormHandler.ensureTransaction";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
		try {
			TransactionManager tm = getTransactionManager();
			Transaction t = tm.getTransaction();
			if (t == null) {
				tm.begin();
				t = tm.getTransaction();
				return t;
			}
			return null;
		} catch (NotSupportedException exc) {
			logError(LogMessageFormatter.formatMessage(null, "NotSupportedException in FBConnectFormHandler.ensureTransaction" , BBBCoreErrorConstants.ACCOUNT_ERROR_1237),exc);
		} catch (SystemException exc) {
			logError(LogMessageFormatter.formatMessage(null, "SystemException in in FBConnectFormHandler.ensureTransaction" , BBBCoreErrorConstants.ACCOUNT_ERROR_1238),exc);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return null;
	}

	/**
	 * method to check if the user is migrated user and first time login
	 * @param bbbProfile profile to check against migrated user
	 * @return true/false
	 */
	private boolean checkMigratedUser(RepositoryItem bbbProfile){
		boolean isMigratedUser = false;
		if(bbbProfile != null){
			String status = getFacebookProfileTool().findUserSiteAssociation(bbbProfile, getSiteContext().getSite().getId());
			if(!BBBUtility.isEmpty(status) && !status.equalsIgnoreCase(FBConstants.EVENT_BBB_PROFILE_EXIST_ON_DIFFERENT_SITE_GROUP)){
				Boolean migrationFlag =  (Boolean) bbbProfile.getPropertyValue(getBbbProfilePropertyManager().getMigratedAccount());
				Boolean loginInFlag = (Boolean) bbbProfile.getPropertyValue(getBbbProfilePropertyManager().getLoggedIn());
				if (migrationFlag!=null && loginInFlag!=null && migrationFlag && !loginInFlag) {
					isMigratedUser = true;
				}
			}
		}
		return isMigratedUser;
	}

}