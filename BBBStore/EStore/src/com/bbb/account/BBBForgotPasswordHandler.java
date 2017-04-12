/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBForgotPasswordHandler.java
 *
 *  DESCRIPTION: BBBProfileFormHandler extends ATG OOTB ForgotPasswordHandler
 *  			 and perform form handling activities related to forgot password 
 *  			 of the user. 	
 *  HISTORY:
 *  10/14/11 Initial version
 *
 */
package com.bbb.account;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.xml.bind.DatatypeConverter;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.core.util.Base64;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.Site;
import atg.multisite.SiteContext;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepositoryItem;
//import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ForgotPasswordHandler;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.security.PBKDF2PasswordHasher;
import com.bbb.utils.BBBUtility;

public class BBBForgotPasswordHandler extends ForgotPasswordHandler {

	private static final String PAGE_NAME = "pageName";
	private BBBProfileTools mTools;
	private BBBProfileManager mManager;
	private BBBPropertyManager mPmgr;
	private Map<String, String> mErrorMap;
	private SiteContext mSiteContext;
	private LblTxtTemplateManager mLblTxtTemplateManager;
	private String mEmailAddr;
	private Map mPlaceHolderMap;
	private String mLoginURL;
	private boolean mAppendContextPath = true;
	private String mStoreContextPath; // variable to get context path from properties file - REST Specific
	private BBBCatalogTools mCatalogTools;
	private Map<String, String> mTbsEmailSiteMap = new HashMap<String, String>();
	String encodedToken=null;
	private boolean challengeQuestionExist=false;
	private String challengeAnswer1;
	private String challengeAnswer2;
	private String emailProvided;
	private String validateChallengeQuestionSuccessURL;
	private String validateChallengeQuestionFailureURL;
	private String skipChallengeQuestionSuccessURL;
	private String skipChallengeQuestionFailureURL;
	private boolean challengeAnswerValidated;
	private boolean wrongAnswerAttemptCount;
	private String hashValue=null;
	private String urlSalt=null;
	public boolean challengeQuestions; 
	private String resetPasswordToken;
	
	public boolean isChallengeQuestions() {
		return challengeQuestions;
	}

	public void setChallengeQuestions(boolean challengeQuestions) {
		this.challengeQuestions = challengeQuestions;
	}
	
	public String getHashValue() {
		return hashValue;
	}

	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}
	
	public String getUrlSalt() {
		return urlSalt;
	}

	public void setUrlSalt(String urlSalt) {
		this.urlSalt = urlSalt;
	}
	
	/**
	 * @return the wrongAnswerAttemptCount
	 */
	public boolean isWrongAnswerAttemptCount() {
		return wrongAnswerAttemptCount;
	}

	/**
	 * @param wrongAnswerAttemptCount the wrongAnswerAttemptCount to set
	 */
	public void setWrongAnswerAttemptCount(boolean wrongAnswerAttemptCount) {
		this.wrongAnswerAttemptCount = wrongAnswerAttemptCount;
	}

	/**
	 * @return the challengeAnswerValidated
	 */
	public boolean isChallengeAnswerValidated() {
		return challengeAnswerValidated;
	}

	/**
	 * @param challengeAnswerValidated the challengeAnswerValidated to set
	 */
	public void setChallengeAnswerValidated(boolean challengeAnswerValidated) {
		this.challengeAnswerValidated = challengeAnswerValidated;
	}

	/**
	 * @return the skipChallengeQuestionSuccessURL
	 */
	public String getSkipChallengeQuestionSuccessURL() {
		return skipChallengeQuestionSuccessURL;
	}

	/**
	 * @param skipChallengeQuestionSuccessURL the skipChallengeQuestionSuccessURL to set
	 */
	public void setSkipChallengeQuestionSuccessURL(
			String skipChallengeQuestionSuccessURL) {
		this.skipChallengeQuestionSuccessURL = skipChallengeQuestionSuccessURL;
	}

	/**
	 * @return the skipChallengeQuestionFailureURL
	 */
	public String getSkipChallengeQuestionFailureURL() {
		return skipChallengeQuestionFailureURL;
	}

	/**
	 * @param skipChallengeQuestionFailureURL the skipChallengeQuestionFailureURL to set
	 */
	public void setSkipChallengeQuestionFailureURL(
			String skipChallengeQuestionFailureURL) {
		this.skipChallengeQuestionFailureURL = skipChallengeQuestionFailureURL;
	}

	/**
	 * @return the validateChallengeQuestionSuccessURL
	 */
	public String getValidateChallengeQuestionSuccessURL() {
		return validateChallengeQuestionSuccessURL;
	}

	/**
	 * @param validateChallengeQuestionSuccessURL the validateChallengeQuestionSuccessURL to set
	 */
	public void setValidateChallengeQuestionSuccessURL(
			String validateChallengeQuestionSuccessURL) {
		this.validateChallengeQuestionSuccessURL = validateChallengeQuestionSuccessURL;
	}

	/**
	 * @return the validateChallengeQuestionFailureURL
	 */
	public String getValidateChallengeQuestionFailureURL() {
		return validateChallengeQuestionFailureURL;
	}

	/**
	 * @param validateChallengeQuestionFailureURL the validateChallengeQuestionFailureURL to set
	 */
	public void setValidateChallengeQuestionFailureURL(
			String validateChallengeQuestionFailureURL) {
		this.validateChallengeQuestionFailureURL = validateChallengeQuestionFailureURL;
	}

	/**
	 * @return the emailProvided
	 */
	public String getEmailProvided() {
		return emailProvided;
	}

	/**
	 * @param emailProvided the emailProvided to set
	 */
	public void setEmailProvided(String emailProvided) {
		this.emailProvided = emailProvided;
	}

	/**
	 * @return the challengeAnswer1
	 */
	public String getChallengeAnswer1() {
		return challengeAnswer1;
	}

	/**
	 * @param challengeAnswer1 the challengeAnswer1 to set
	 */
	public void setChallengeAnswer1(String challengeAnswer1) {
		this.challengeAnswer1 = challengeAnswer1;
	}

	/**
	 * @return the challengeAnswer2
	 */
	public String getChallengeAnswer2() {
		return challengeAnswer2;
	}

	/**
	 * @param challengeAnswer2 the challengeAnswer2 to set
	 */
	public void setChallengeAnswer2(String challengeAnswer2) {
		this.challengeAnswer2 = challengeAnswer2;
	}

	/**
	 * @return the challengeQuestionExist
	 */
	public boolean isChallengeQuestionExist() {
		return challengeQuestionExist;
	}

	/**
	 * @param challengeQuestionExist the challengeQuestionExist to set
	 */
	public void setChallengeQuestionExist(boolean challengeQuestionExist) {
		this.challengeQuestionExist = challengeQuestionExist;
	}

	public static final PBKDF2PasswordHasher mPBKDF2PasswordHasher = (PBKDF2PasswordHasher)Nucleus.getGlobalNucleus().resolveName("/com/bbb/security/PBKDF2PasswordHasher");
	
	/**
	 * @return the mStoreContextPath
	 */
	public String getStoreContextPath() {
		return mStoreContextPath;
	}

	/**
	 * @param mStoreContextPath the mStoreContextPath to set
	 */
	public void setStoreContextPath(String mStoreContextPath) {
		this.mStoreContextPath = mStoreContextPath;
	}
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}
		
	/**
	 * Introduced flag to identify if profile needs to extend for a particular site before sending forgot password email.
	 * BSL-1429 
	 */
	private boolean forgotPasswordWithProfileExtenstion;
	

	/**
	 * @return forgotPasswordWithProfileExtenstion
	 */
	public boolean isForgotPasswordWithProfileExtenstion() {
		return forgotPasswordWithProfileExtenstion;
	}

	/**
	 * @param forgotPasswordWithProfileExtenstion
	 */
	public void setForgotPasswordWithProfileExtenstion(
			boolean forgotPasswordWithProfileExtenstion) {
		this.forgotPasswordWithProfileExtenstion = forgotPasswordWithProfileExtenstion;
	}
	
	// Added for Email Persistence #69
	private String mUserItemFromEmailId;
	//private TemplateEmailInfoImpl mTemplateEmailInfo;

	
	public String getUserItemFromEmailId() {
	    return mUserItemFromEmailId;
	}

	public void setUserItemFromEmailId(String userItemFromEmailId) {
	    this.mUserItemFromEmailId = userItemFromEmailId;
	}

	public boolean isAppendContextPath() {
		return mAppendContextPath;
	}

	public void setAppendContextPath(boolean pAppendContextPath) {
		this.mAppendContextPath = pAppendContextPath;
	}

	public Map getPlaceHolderMap() {
		return mPlaceHolderMap;
	}

	public void setPlaceHolderMap(Map pPlaceHolderMap) {
		this.mPlaceHolderMap = pPlaceHolderMap;
	}

	/**
	 * @return mEmailAddr
	 */
	public String getEmailAddr() {
		return mEmailAddr;
	}

	/**
	 * @param pEmailAddr
	 */
	public void setEmailAddr(String pEmailAddr) {
		mEmailAddr = pEmailAddr;
	}

	/**
	 * @return the mLoginUrl
	 */
	public String getLoginURL() {
		return mLoginURL;
	}

	/**
	 * @param mLoginUrl the mLoginUrl to set
	 */
	public void setLoginURL(String pLoginURL) {
		this.mLoginURL = pLoginURL;
	}

	/**
	 * @return mLblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return mLblTxtTemplateManager;
	}

	/**
	 * @param pLblTxtTemplateManager
	 */
	public void setLblTxtTemplateManager(
			LblTxtTemplateManager pLblTxtTemplateManager) {
		mLblTxtTemplateManager = pLblTxtTemplateManager;
	}

	/**
	 * Constructor of BBBForgotPasswordHandler
	 */
	public BBBForgotPasswordHandler() {
		mErrorMap = new HashMap<String, String>();
		mTools = (BBBProfileTools) getProfileTools();
	}

	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param pSiteContext
	 *            the siteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		mSiteContext = pSiteContext;
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
	 * @return mPmgr
	 */
	public BBBPropertyManager getPmgr() {
		return mPmgr;
	}

	/**
	 * @param pPmgr
	 */
	public void setPmgr(BBBPropertyManager pPmgr) {
		mPmgr = pPmgr;
	}

	/**
	 * @return mManager
	 */
	public BBBProfileManager getManager() {
		return mManager;
	}

	/**
	 * @param pManager
	 */
	public void setManager(BBBProfileManager pManager) {
		mManager = pManager;
	}
	
	/**
	 * @return the tbsEmailSiteMap
	 */
	public Map<String, String> getTbsEmailSiteMap() {
		return mTbsEmailSiteMap;
	}

	/**
	 * @param pTbsEmailSiteMap the tbsEmailSiteMap to set
	 */
	public void setTbsEmailSiteMap(Map<String, String> pTbsEmailSiteMap) {
		mTbsEmailSiteMap = pTbsEmailSiteMap;
	}

	public boolean handleResetPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		return handleForgotPassword(pRequest, pResponse);
	}

	/**
	 * Handle method to intercept if user has challenge question setup in his profile
	 * then show user with his selected Question in modal
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public boolean handleResetPasswordWithChallengeQuestion(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("handleResetPasswordWithChallengeQuestion  method : Start");
		}
		String pageName = pRequest.getParameter(PAGE_NAME);
		String email = BBBUtility.toLowerCase(getStringValueProperty(BBBCoreConstants.EMAIL));
		mTools = (BBBProfileTools) getProfileTools();
		MutableRepositoryItem challengQuestionItem=null;
		String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		RepositoryItem userItemFromEmail = (RepositoryItem) mTools.getItemFromEmail(email);
		if(null!=userItemFromEmail){		
			this.preForgotPassword(pRequest, pResponse);
			if (this.mErrorMap.isEmpty()) {
				challengQuestionItem = mTools.fetchProfileIdFromChallengeQuestionRepository((String)userItemFromEmail.getPropertyValue(BBBCoreConstants.ID));
			}
		}
		if(null !=challengQuestionItem){			
		if(null !=challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1)){
			logDebug("handleResetPasswordWithChallengeQuestion challengeQuestions is set to :true");
			this.setChallengeQuestionExist(true);
			this.challengeQuestions=true;
			String pageNameUrl="";
			if(!StringUtils.isBlank(pageName))
			{
				pageNameUrl =	BBBCoreConstants.QUESTION_MARK+PAGE_NAME+BBBCoreConstants.EQUAL+pageName;
				pRequest.setParameter(PAGE_NAME, pageName);
			}
			
			return checkFormRedirect(getSkipChallengeQuestionSuccessURL()+pageNameUrl,
					getSkipChallengeQuestionFailureURL(), pRequest, pResponse);
			}}
		if (isLoggingDebug()) {
			logDebug("handleResetPasswordWithChallengeQuestion  method : End");
		}
		
		this.challengeQuestions=false;
			if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
				return true;
			}
			else{
		return handleForgotPassword(pRequest, pResponse);
			}
	}
	
	public final boolean handleResetPasswordWithChallengeQuestionSetUp(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
	{
		this.setSkipChallengeQuestionSuccessURL("");
		this.setSkipChallengeQuestionFailureURL("");
		
		 final boolean status = this.handleResetPasswordWithChallengeQuestion(pRequest,pResponse);
		 return status;
	}
	
	public final boolean handleValidateChallengeQuestions(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
	{
		
		
		this.setValidateChallengeQuestionSuccessURL("");
		this.setValidateChallengeQuestionFailureURL("");
		 final boolean status = this.handleValidateChallengeQuestion(pRequest,pResponse);
		 
		 return status;
	}
	/**
	 * Handle method to intercept whether challenge answer submitted  by user matches
	 * with what user has given while setting up challenge question in reset password flow
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleValidateChallengeQuestion(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("handleValidateChallengeQuestion  method : Start");
		}
		int answerAttempts=0;
		String email = this.getEmailProvided();
		String challengeAnswer1 = this.getChallengeAnswer1();
		String challengeAnswer2 = this.getChallengeAnswer2();
		if(email != null){
			email=BBBUtility.toLowerCase(email);
		} else {
			String emailEmpty = getLblTxtTemplateManager().getErrMsg(
					"ERROR_EMAIL_EMPTY",
					pRequest.getLocale().getLanguage(), null, null);
			addFormException(new DropletException(emailEmpty,"ERROR_EMAIL_EMPTY"));
			return checkFormRedirect(getValidateChallengeQuestionSuccessURL(),
					getValidateChallengeQuestionFailureURL(), pRequest, pResponse);
		}
		if(StringUtils.isEmpty(email) && null !=pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL)){
			email = (String)pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL);
		}
		if(challengeAnswer1 != null){
			challengeAnswer1=challengeAnswer1.toLowerCase();
		}
		if(challengeAnswer2 != null){
			challengeAnswer2=challengeAnswer2.toLowerCase();
		}
		 mTools = (BBBProfileTools) getProfileTools();
		RepositoryItem userItemFromEmail = (RepositoryItem) mTools.getItemFromEmail(email);
		if(userItemFromEmail != null) {
		MutableRepositoryItem challengQuestionItem=null;
		challengQuestionItem=	mTools.fetchProfileIdFromChallengeQuestionRepository((String)userItemFromEmail.getPropertyValue(BBBCoreConstants.ID));
		if(null!=challengeAnswer1 && challengeAnswer1.equals(Base64.decodeToString((String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_1))) && null !=challengeAnswer2
				&& challengeAnswer2.equals(Base64.decodeToString((String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_2))) ){
			if (isLoggingDebug()) {
				logDebug("handleValidateChallengeQuestion  method : End");
			}
			this.setChallengeAnswerValidated(true);
			this.setResetPasswordToken(createRestPasswordToken(email));
			BBBSessionBean sessionBean = ((BBBSessionBean) (pRequest.resolveName(BBBCoreConstants.SESSION_BEAN)));
			if(BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel())){
			sessionBean.setPwdReqByChallengeQ(true);
			}
			return checkFormRedirect(getValidateChallengeQuestionSuccessURL(),
					getValidateChallengeQuestionFailureURL(), pRequest, pResponse);
			
			
		}else{
			if(null ==pRequest.getSession().getAttribute(BBBCoreConstants.CHALLENGE_QUESTION_FAILURE_ATTEMPT)){				
			pRequest.getSession().setAttribute(BBBCoreConstants.CHALLENGE_QUESTION_FAILURE_ATTEMPT, Integer.valueOf(++answerAttempts));
			}else if(null !=pRequest.getSession().getAttribute(BBBCoreConstants.CHALLENGE_QUESTION_FAILURE_ATTEMPT)){
				answerAttempts=(Integer)pRequest.getSession().getAttribute(BBBCoreConstants.CHALLENGE_QUESTION_FAILURE_ATTEMPT);
				pRequest.getSession().setAttribute(BBBCoreConstants.CHALLENGE_QUESTION_FAILURE_ATTEMPT, Integer.valueOf(++answerAttempts));
			}
			if(answerAttempts==3){
				this.setWrongAnswerAttemptCount(true);
				super.handleForgotPassword(pRequest, pResponse);
				String challengeAnswerNotMatched = getLblTxtTemplateManager().getErrMsg(
						"err_challenge_answer_not_matched",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(challengeAnswerNotMatched,"err_challenge_answer_not_matched"));
				mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, challengeAnswerNotMatched);
				return checkFormRedirect(getValidateChallengeQuestionSuccessURL(),
						getValidateChallengeQuestionFailureURL(), pRequest, pResponse);
			}
			
			String challengeAnswerNotMatched = getLblTxtTemplateManager().getErrMsg(
					"err_challenge_answer_not_matched",
					pRequest.getLocale().getLanguage(), null, null);
			addFormException(new DropletException(challengeAnswerNotMatched,"err_challenge_answer_not_matched"));
			mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, challengeAnswerNotMatched);
			if (isLoggingDebug()) {
				logDebug("handleValidateChallengeQuestion  method : End With form exception added");
			}
			return checkFormRedirect(getValidateChallengeQuestionSuccessURL(),
					getValidateChallengeQuestionFailureURL(), pRequest, pResponse);
			}
		
		} else {
			String emailDoesntExist = getLblTxtTemplateManager().getErrMsg(
					"err_profile_not_found_for_email",
					pRequest.getLocale().getLanguage(), null, null);
			addFormException(new DropletException(emailDoesntExist,"err_profile_not_found_for_email"));
			return checkFormRedirect(getValidateChallengeQuestionSuccessURL(),
					getValidateChallengeQuestionFailureURL(), pRequest, pResponse);
		}
		
		}
	
	/**
	 * Overloaded method from ATG OOTB and does the validation for email and
	 * sends the email
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return true if success, false - otherwise
	 */
	public void preForgotPassword(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String methodName = BBBCoreConstants.PRE_FORGOT_PASSWORD;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.FORGOT_PASSWORD,
				methodName);
		
		
		try{
			if (isLoggingDebug()) {
				logDebug("BBBForgotPasswordFormHandler.preForgotPassword() method started");
			}
			String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
			String repositoryId=null;
			if(isAppendContextPath()){
				if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
					//set context path from properties file in case of Mobile Web and mobile App
					getTemplateEmailInfo().setTemplateURL(getStoreContextPath() + getTemplateEmailInfo().getTemplateURL());
				}
				else{
					getTemplateEmailInfo().setTemplateURL(pRequest.getContextPath() + getTemplateEmailInfo().getTemplateURL());
				}
			}
			Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
			mTools = (BBBProfileTools) getProfileTools();
			String email = getStringValueProperty(BBBCoreConstants.EMAIL);
			if (StringUtils.isEmpty(email) && (!StringUtils.isEmpty(this.getEmailProvided()) || null !=pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL))){
				email=this.getEmailProvided();
				if(StringUtils.isEmpty(email)){
				email=(String)pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL);
				}
			}
				
			if(email != null){
				email = email.toLowerCase();
				setValueProperty(BBBCoreConstants.EMAIL, email);
			}
			if (BBBUtility.isEmpty(email)) {
				String emptyEmailError = getLblTxtTemplateManager().getErrMsg(
						"err_profile_email_empty",
						pRequest.getLocale().getLanguage(), errorPlaceHolderMap,
						null);
				addFormException(new DropletException(emptyEmailError,BBBCoreErrorConstants.ACCOUNT_ERROR_1306));
				mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, emptyEmailError);
			} else if (!BBBUtility.isValidEmail(email)) {
				String emailFormatError = getLblTxtTemplateManager().getErrMsg(
						"err_profile_email_invalid",
						pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);
				addFormException(new DropletException(emailFormatError,BBBCoreErrorConstants.ACCOUNT_ERROR_1307));
				mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, emailFormatError);
			} else {
				RepositoryItem userItemFromEmail = (RepositoryItem) mTools.getItemFromEmail(email);
				
				// dkhadka: added to capture forget password scenario for a shallow profile
				String status="";
				if (null != userItemFromEmail) {
					status = (String) userItemFromEmail
							.getPropertyValue(getPmgr().getStatusPropertyName());
					repositoryId = userItemFromEmail.getRepositoryId();
				}
				if(isLoggingDebug())logDebug("Profile Status "+status);
				if (userItemFromEmail==null || BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE.equalsIgnoreCase(status) ) {
					
					String profileNotFound = getLblTxtTemplateManager().getErrMsg("err_profile_email_not_found",
									pRequest.getLocale().getLanguage(),	errorPlaceHolderMap, null);
					addFormException(new DropletException(profileNotFound));
					mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, profileNotFound);
				} else {
					
					/**
					 * Code to extend profile before sending forgot password email
					 * BSL-1429 : START 
					 */
					if (forgotPasswordWithProfileExtenstion) {
						try {
							mTools.createSiteItem(email, getSiteContext().getSite().getId(), null, null, null);
						} catch (BBBSystemException e) {
							String errorInProfileExtenstion = getLblTxtTemplateManager().getErrMsg("err_forgotpassword_with_profile_extenstion",
											pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
							addFormException(new DropletException(errorInProfileExtenstion));
							mErrorMap.put(BBBCoreConstants.EMAIL_ERROR,	errorInProfileExtenstion);
							logError("BBBSystemException is thrown", e);
						}
					}
					/**
					 * BSL-1429 : END 
					 */
					
					if (!mTools.isDuplicateEmailAddress(email, getSiteContext().getSite().getId()) && !forgotPasswordWithProfileExtenstion) {
						errorPlaceHolderMap.put(BBBCoreConstants.EMAIL_PROPERTY_ON_ORDER, email);
						String associateWithSisterSite = getLblTxtTemplateManager()
								.getErrMsg("err_profile_email_associated_sister_site",
										pRequest.getLocale().getLanguage(),	errorPlaceHolderMap, null);
						addFormException(new DropletException(associateWithSisterSite));
						mErrorMap.put(BBBCoreConstants.EMAIL_ERROR,	associateWithSisterSite);
					}else {
					    	if(userItemFromEmail.getRepositoryId() != null){
					    	    mUserItemFromEmailId = userItemFromEmail.getRepositoryId(); 
					    	}
					}
				}
			}
			if (!getFormError()) {
				String loginURL = null;
				if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP) || getSiteId().startsWith("TBS"))){
					try {
						List<String> configValue = getCatalogTools().getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
						if(configValue != null && configValue.size() > 0){
							 //set serverName from config key and context path from properties file
							loginURL = pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND + configValue.get(0) + getStoreContextPath() + getLoginURL();
						}
					} catch (BBBSystemException e) {
						logError("BBBForgotPasswordHandler.preForgotPassword :: System Exception occured while fetching config value for config key " + BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
					} catch (BBBBusinessException e) {
						logError("BBBForgotPasswordHandler.preForgotPassword :: Business Exception occured while fetching config value for config key " + BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
					}
				}
				else{
					String siteId = getSiteContext().getSite().getId();
					if(getTbsEmailSiteMap().get(siteId) != null){
						loginURL = getTbsEmailSiteMap().get(siteId) + getStoreContextPath() + getLoginURL();
					} else {
						loginURL = pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND + pRequest.getServerName() + pRequest.getContextPath() + getLoginURL();
					}
				}
				// generating unique token, that will be sent to user in forgot password link
				/***
				 * Do not use email-id or profile-id in generating token. Create
				 * the token (HH like) using just the timestamp. Encrypt this
				 * generated token using the Password Salt logic.
				 */
				final String timestampToken = String.valueOf(new Date().getTime());
				setPasswordParams(email, loginURL, timestampToken);
			} 
			if (isLoggingDebug()) {
				logDebug("BBBForgotPasswordFormHandler.preForgotPassword() method Ends");
			}
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.FORGOT_PASSWORD,
				methodName);
		}
	}

	/**
	 * Overloaded method from ATG OOTB and adds the error if required
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return true if success, false - otherwise
	 */
	public void postForgotPassword(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()) {
			logDebug("BBBForgotPasswordFormHandler.postForgotPassword() method started");
		}
		 pRequest.getSession().removeAttribute(BBBCoreConstants.FORGET_EMAIL);
		 pRequest.getSession().removeAttribute(BBBCoreConstants.CHALLENGE_QUESTION_FAILURE_ATTEMPT);
		if ((mErrorMap != null && mErrorMap.isEmpty()) && getFormError()) {
			mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, "errorInSendingEmail");
		} else {
			mEmailAddr = getStringValueProperty(BBBCoreConstants.EMAIL);
			RepositoryItem profileItem = mTools.getItemFromEmail(mEmailAddr);
			mTools.updateLoginAttemptCount(profileItem, true);
			// updating the token  value in User Profile 
			mTools.updateToken(profileItem,mEmailAddr,this.getHashValue());
			
			Object tempMigrationFlag =  profileItem.getPropertyValue(mPmgr.getMigratedAccount());
			Object tempUserMigratedLoginProp =  profileItem.getPropertyValue(mPmgr.getLoggedIn());
			if (tempMigrationFlag != null
					&& tempUserMigratedLoginProp != null) {
				boolean migrationFlag = (Boolean) profileItem
						.getPropertyValue(mPmgr.getMigratedAccount());
				boolean mUserMigratedLoginProp = (Boolean) profileItem
						.getPropertyValue(mPmgr.getLoggedIn());
				if (migrationFlag && !mUserMigratedLoginProp) {
					mManager.updateIsLoggedInProp(mEmailAddr);
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBForgotPasswordFormHandler.postForgotPassword() method Ends");
		}
	}
	/**
	 * 
	 * @param email
	 * @param loginURL
	 * @param token
	 * 
	 * set the parameters required in forgot password mail in placeHolderMap
	 * @throws UnsupportedEncodingException 
	 */

	private void setPasswordParams(String email, String loginURL,String timestampToken) throws UnsupportedEncodingException{
		TemplateEmailInfoImpl temp = getTemplateEmailInfo();
		Site site = getSiteContext().getSite();
		String URLencodedToken=null;
		Object siteEmail = site.getPropertyValue(mPmgr
				.getSiteEmailAddressPropertyName());
		if (siteEmail != null) {
			temp.setMessageFrom(siteEmail.toString());
		}
		temp.setMessageTo(email);
		temp.setMailingId(email);
		//enocded the token
		encodedToken = Base64.encodeToString(timestampToken);
		URLencodedToken=URLEncoder.encode(encodedToken,BBBCoreConstants.UTF_8);
		if(!createPasswordSalt(URLencodedToken))
		{
			logDebug("Token password salt creation failed.");
		}
		
		//encodedToken = DatatypeConverter.printHexBinary(token.getBytes());

		Map placeHolderMap = new HashMap();
		placeHolderMap.put(BBBCoreConstants.FORM_SITE, site);
		placeHolderMap.put(BBBCoreConstants.EMAIL_TYPE, BBBCoreConstants.ET_FORGOT_PASSWORD);
		try {
		    
		        final Calendar currentDate = Calendar.getInstance();				
			long uniqueKeyDate = currentDate.getTimeInMillis();
			String emailPersistId = mUserItemFromEmailId + uniqueKeyDate;
			
			placeHolderMap.put(BBBCoreConstants.FORM_FNAME,(String) DynamicBeans.getSubPropertyValue(mTools.getItemFromEmail(email),mPmgr.getFirstNamePropertyName()));
			placeHolderMap.put(BBBCoreConstants.FORM_LNAME,(String) DynamicBeans.getSubPropertyValue(mTools.getItemFromEmail(email),mPmgr.getLastNamePropertyName()));
			placeHolderMap.put(BBBAccountConstants.ACCOUNT_LOGIN_URL, loginURL);
			// setting encoded token in placeHolderMap
			placeHolderMap.put(BBBAccountConstants.ENCODED_TOKEN, URLEncoder.encode(this.getUrlSalt(),BBBCoreConstants.UTF_8));
	//		placeHolderMap.put(BBBAccountConstants.ENCODED_TOKEN, this.getUrlSalt());
			placeHolderMap.put(BBBCoreConstants.EMAIL_PERSIST_ID , emailPersistId);			
			setPlaceHolderMap(placeHolderMap);
		} catch (PropertyNotFoundException e) {
			if (isLoggingError()) {
				logError(LogMessageFormatter.formatMessage(null, "PropertyNotFoundException - Error in setting Password Parameter", BBBCoreErrorConstants.ACCOUNT_ERROR_1086 ), e);
			}
		}
	}

	/**
	 * This method is used to create a salt value
	 * 
	 * @param pEmail
	 * @return
	 */
	private boolean createPasswordSalt(final String token) {
		this.logDebug("BBBForgotPasswordHandler.createPasswordSalt() method started");
		boolean isPasswordCreated = false;
		byte[] saltByte = null;
//		try {
			saltByte = mPBKDF2PasswordHasher.generateSalt().getBytes();
			this.setUrlSalt(DatatypeConverter.printBase64Binary(saltByte)); 
			this.setHashValue(mPBKDF2PasswordHasher.mPwdHashingService.sha256(token));
			isPasswordCreated = mPBKDF2PasswordHasher.mPwdHashingService
					.createPasswordSalt(
							this.getHashValue(), this.getUrlSalt(),
							mPBKDF2PasswordHasher.getNumIterations());
		/*} catch (NoSuchAlgorithmException e) {
			logError(e.getMessage(), e);
		}*/
		this.logDebug("BBBForgotPasswordHandler.createPasswordSalt() method end");
		return isPasswordCreated;
	}

	
	@Override
	protected Map generateNewPasswordTemplateParams(
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse, RepositoryItem pProfile,
			String pNewPassword) {
		Map map= super.generateNewPasswordTemplateParams(pRequest, pResponse, pProfile,
				pNewPassword);
		// commented out code that set the newpassword generated in placeHolderMap
		// as now insetaed of direct password, token will be sent to customerin a link
		//getPlaceHolderMap().put(BBBCoreConstants.FORM_NEW_PASSWORD,pNewPassword);
		map.put(BBBCoreConstants.PLACE_HOLDER, getPlaceHolderMap());
		return map;
	}
	
	/**
	 * This method is used to generate token which will be used at reset password after filling answering the challange Quenstions
	 * @param email
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String createRestPasswordToken(String email) throws UnsupportedEncodingException{
		this.logDebug("createRestPasswordToken() method started");
		String urlEncodedToken=null;
		final String timestampToken = String.valueOf(new Date().getTime());
		String token = null;
		TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
		boolean shouldRollback = false;
		try { 
				transactionDemarcation.begin(getTransactionManager());
				encodedToken = Base64.encodeToString(timestampToken);
				urlEncodedToken=URLEncoder.encode(encodedToken,BBBCoreConstants.UTF_8);
				if(!createPasswordSalt(urlEncodedToken))
				{
					logDebug("creatResetResetPasswordToken password salt creation failed ");
				}
				else{
					RepositoryItem profileItem = mTools.getItemFromEmail(email);
					mTools.updateLoginAttemptCount(profileItem, true);			
					mTools.updateToken(profileItem,email,this.getHashValue());
					mTools.updateProperty(BBBCoreConstants.GENERATED_PASSWORD, true, profileItem);
					token=URLEncoder.encode(this.getUrlSalt(),BBBCoreConstants.UTF_8); 
				
				}
				return  token;
		} catch (Exception e) {
			shouldRollback = true;
			logError("creatResetResetPasswordToken:", e);
		}finally {
			try {
				transactionDemarcation.end(shouldRollback);
			} catch (TransactionDemarcationException e) {
				logError("Error While Creating rest password token." , e);
			}
		}
    this.logDebug("createRestPasswordToken() method end and token value is "+token);
	return token;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}
}
