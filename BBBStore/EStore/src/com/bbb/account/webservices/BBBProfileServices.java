/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBProfileServices.java
 *
 *  DESCRIPTION: BBBProfileServices extends ATG OOTB ProfileServices
 *  			 and gives the response to the web-services for the Levelor 	
 *  HISTORY:
 *  9/02/12 Initial version
 *  10/02/12 Login / Forgot Password
 *	14/02/12 Registration Service  
 */

package com.bbb.account.webservices;

import static com.bbb.constants.BBBCoreConstants.EMAIL;
import static com.bbb.constants.BBBCoreConstants.FIRST_NAME;
import static com.bbb.constants.BBBCoreConstants.FORM_EMAIL;
import static com.bbb.constants.BBBCoreConstants.FORM_EVENT_TYPE;
import static com.bbb.constants.BBBCoreConstants.FORM_FNAME;
import static com.bbb.constants.BBBCoreConstants.FORM_LNAME;
import static com.bbb.constants.BBBCoreConstants.FORM_REGISTRANT_NAME;
import static com.bbb.constants.BBBCoreConstants.FORM_REG_ID;
import static com.bbb.constants.BBBCoreConstants.LAST_NAME;
import static com.bbb.constants.BBBCoreConstants.LOGIN;
import static com.bbb.constants.BBBCoreConstants.MOBILE_NUM;
import static com.bbb.constants.BBBCoreConstants.PHONE_NUM;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;

import com.bbb.account.BBBProfileManager;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.webservices.vo.BillingAddressRequestVO;
import com.bbb.account.webservices.vo.BillingAddressResponseVO;
import com.bbb.account.webservices.vo.ForgotPasswordRequestVO;
import com.bbb.account.webservices.vo.ForgotPasswordResponseVO;
import com.bbb.account.webservices.vo.LinkCoRegistrantRequestVO;
import com.bbb.account.webservices.vo.LinkCoRegistrantResponseVO;
import com.bbb.account.webservices.vo.LinkProfileRequestVO;
import com.bbb.account.webservices.vo.LinkProfileResponseVO;
import com.bbb.account.webservices.vo.LoginRequestVO;
import com.bbb.account.webservices.vo.LoginResponseVO;
import com.bbb.account.webservices.vo.ProfileInfoRequestVO;
import com.bbb.account.webservices.vo.ProfileInfoResponseVO;
import com.bbb.account.webservices.vo.ProfileRequestVO;
import com.bbb.account.webservices.vo.ProfileResponseVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SiteVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryTypes;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.security.PBKDF2PasswordHasher;
import com.bbb.utils.BBBUtility;

import atg.core.util.Address;
import atg.core.util.Base64;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.security.IdentityManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.ProfileServices;
import atg.userprofiling.email.HtmlContentProcessor;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

public class BBBProfileServices extends ProfileServices {

	private static final String DEFAULT_LOCALE = "EN";
	
	// Email Template Constants
	private static final String PLACE_HOLDER = "placeHolderValues";
	private static final String EMAIL_TYPE = "emailType";
	private static final String ET_CREATE_ACCOUNT = "ProfileCreation";
	private static final String ET_FORGOT_PASSWORD = "ForgotPassword";
	private static final String ET_COREG_EMAIL = "CoRegistrantEmail";
	private static final String FORM_SITE = "frmData_siteId";
	private static final String FORM_NEW_PASSWORD = "newPassword";
	
	private static final String CONTENT_CATALOG_CONFIG_KEY = "ContentCatalogKeys";
	private static final String BED_BATH_AND_BEYOND_US_SITE_ID = "BedBathUSSiteCode";
	private static final String BED_BATH_AND_BEYOND_CA_SITE_ID = "BedBathCanadaSiteCode";
	private static final String BUY_BUY_BABY_SITE_ID = "BuyBuyBabySiteCode";
	//private static final String GIFT_REGISTRY_MANAGER = "/com/bbb/commerce/giftregistry/manager/GiftRegistryManager";

	private transient ProfileServices mProfileServices;
	private transient BBBProfileManager profileManager;
	private transient LblTxtTemplateManager mLblTxtTemplateManager;
	private transient TemplateEmailSender mTemplateEmailSender;
	
	private transient String mMessageFrom;
	private transient DynamoHttpServletRequest mDynRequest;
	private transient DynamoHttpServletResponse mDynResponse;
	private transient BBBProfileTools mProfileTool;
	private transient GiftRegistryTools mGiftRegistryTools;
	private transient BBBCatalogTools catalogTools;
	private transient BBBPropertyManager profilePropertyManager;
	private TransactionManager mTransactionManager;
	private MutableRepository mGiftRepository;
	private String mTemplateURL;
	private HtmlContentProcessor mEmailContentProcessor;
	
	private transient  boolean mSendEmailInSeparateThread;
	private transient  boolean mPersistEmails;
	private GiftRegistryManager mGiftRegistryManager;
	private SiteContextManager mSiteContextManager;
	private String loginURL;
	private String accountLoginURL;
	private String storeContextPath;
	
	public final SiteContextManager getSiteContextManager() {
		return mSiteContextManager;
	}

	public final void setSiteContextManager(
			SiteContextManager pSiteContextManager) {
		mSiteContextManager = pSiteContextManager;
	}
	
	/**
	 * @return the mGiftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * @param pGiftRegistryManager the pGiftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager pGiftRegistryManager) {
		this.mGiftRegistryManager = pGiftRegistryManager;
	}
	
	/**
	 * @return the mEmailContentProcessor
	 */
	public HtmlContentProcessor getEmailContentProcessor() {
		return mEmailContentProcessor;
	}

	/**
	 * @param pEmailContentProcessor the pEmailContentProcessor to set
	 */
	public void setEmailContentProcessor(HtmlContentProcessor pEmailContentProcessor) {
		this.mEmailContentProcessor = pEmailContentProcessor;
	}

	/**
	 * @return the mTemplateURL
	 */
	public String getTemplateURL() {
		return mTemplateURL;
	}

	/**
	 * @param pTemplateURL the pRegistrationTemplateURL to set
	 */
	public void setTemplateURL(String pTemplateURL) {
		this.mTemplateURL = pTemplateURL;
	}

	/**
	 * @return the mGiftRepository
	 */
	public MutableRepository getGiftRepository() {
		return mGiftRepository;
	}

	/**
	 * @param mGiftRepository the mGiftRepository to set
	 */
	public void setGiftRepository(MutableRepository pGiftRepository) {
		this.mGiftRepository = pGiftRepository;
	}

	
	/**
	 * @return the mLblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return mLblTxtTemplateManager;
	}

	/**
	 * @param mLblTxtTemplateManager
	 *            the mLblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(final LblTxtTemplateManager pLblTxtTemplateManager) {
		this.mLblTxtTemplateManager = pLblTxtTemplateManager;
	}

	/**
	 * @return the mProfileServices
	 */
	public ProfileServices getProfileServices() {
		return mProfileServices;
	}

	/**
	 * @param mProfileServices
	 *            the mProfileServices to set
	 */
	public void setProfileServices(final ProfileServices pProfileServices) {
		this.mProfileServices = pProfileServices;
	}

	/**
	 * @return the mTemplateEmailSender
	 */
	public TemplateEmailSender getTemplateEmailSender() {
		return mTemplateEmailSender;
	}

	/**
	 * @param mTemplateEmailSender
	 *            the mTemplateEmailSender to set
	 */
	public void setTemplateEmailSender(final TemplateEmailSender mTemplateEmailSender) {
		this.mTemplateEmailSender = mTemplateEmailSender;
	}

	/**
	 * @return the mMessageFrom
	 */
	public String getMessageFrom() {
		return mMessageFrom;
	}

	/**
	 * @param mMessageFrom
	 *            the mMessageFrom to set
	 */
	public void setMessageFrom(final String pMessageFrom) {
		this.mMessageFrom = pMessageFrom;
	}

	public LoginResponseVO login(final LoginRequestVO loginRequest) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | login | Starts");
		}
		if(!loginRequest.isLoginProfileId()){
			loginRequest.setLoginId( !BBBUtility.isEmpty(loginRequest.getLoginId()) ? loginRequest.getLoginId().toLowerCase() : loginRequest.getLoginId());
		}
		
		final LoginResponseVO loginRespVO = new LoginResponseVO();
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		boolean status = false;
		boolean isAccountLocked = false;
		
		validateLogin(loginRequest, loginRespVO, errorPlaceHolderMap);
		//trim the password
		if(BBBUtility.isNotEmpty(loginRequest.getPassword())){
			loginRequest.setPassword(loginRequest.getPassword().trim());
		}
		if (errorPlaceHolderMap.isEmpty()) {
			try {
				Site site = getSiteContextManager().getSite(loginRequest.getSiteId());
				SiteContextImpl context = new SiteContextImpl(getSiteContextManager(), site);
				getSiteContextManager().pushSiteContext(context);
			} catch (SiteContextException siteContextException) {
				try {
					throw new BBBSystemException("BBBSystemException in BBBProfileServices.login " + siteContextException);
				} catch (BBBSystemException e) {
					logError("Error in login ", e);
				}
			}
			RepositoryItem bbbProfile = null;
			try {
				bbbProfile = getProfileForString(loginRequest.getLoginId(), loginRequest.isLoginProfileId());
			} catch (RepositoryException e) {
				final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_profile", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_profile", error);
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1107),e);
			}
			
			if(bbbProfile != null){
				
				boolean diffSiteGroup = getProfileTool().isUserPresentToOtherGroup(bbbProfile, loginRequest.getSiteId());
				if(!diffSiteGroup){
					
					RepositoryItem validProfile = null;
					IdentityManager identityManager = (IdentityManager) ServletUtil.getCurrentRequest().resolveName("/atg/dynamo/security/IdentityManager");
					try {
						if (identityManager.checkAuthenticationByPassword(
								(String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()), loginRequest.getPassword())) {
									validProfile = getProfileTools().getItem((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()), 
											null, getLoginProfileType());
						}
						logDebug("Valid profile is: " + validProfile);
					} catch (atg.security.SecurityException e1) {
						logError("Error in validating profile ", e1);
					}
					
					if(validProfile == null){
						Map<String, Object> siteAssociation = (Map<String, Object>) bbbProfile.getPropertyValue(getProfilePropertyManager().getUserSiteItemsPropertyName());
						if (siteAssociation != null	&& !siteAssociation.isEmpty()) {
							if (siteAssociation.containsKey(loginRequest.getSiteId())) {
								isAccountLocked = getProfileTool().isAccountLocked(bbbProfile);
								if (isAccountLocked) {
									final String error = getLblTxtTemplateManager().getErrMsg("err_account_locked",	DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_account_locked", error);
								}else{
									final String error = getLblTxtTemplateManager().getErrMsg("err_invalid_password", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_invalid_password", error);
									getProfileTool().updateLoginAttemptCount(bbbProfile, false);
								}
							}else{
								if(loginRequest.isAutoExtendProfile()){
									isAccountLocked = getProfileTool().isAccountLocked(bbbProfile);
									if (isAccountLocked) {
										final String error = getLblTxtTemplateManager().getErrMsg("err_account_locked",	DEFAULT_LOCALE, null, null);
										errorPlaceHolderMap.put("err_account_locked", error);
									}else{
										final String error = getLblTxtTemplateManager().getErrMsg("err_invalid_password", DEFAULT_LOCALE, null, null);
										errorPlaceHolderMap.put("err_invalid_password", error);
										getProfileTool().updateLoginAttemptCount(bbbProfile, false);
									}
								}else{
									final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_sister_site", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_profile_exist_sister_site", error);
								}
							}
						}
					}else{
						Map<String, Object> siteAssociation = (Map<String, Object>)bbbProfile.getPropertyValue(getProfilePropertyManager().getUserSiteItemsPropertyName());
						if(siteAssociation != null && !siteAssociation.isEmpty()){
							if(siteAssociation.containsKey(loginRequest.getSiteId())){
								isAccountLocked = getProfileTool().isAccountLocked(bbbProfile);
								if (isAccountLocked) {
									final String error = getLblTxtTemplateManager().getErrMsg("err_account_locked",	DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_account_locked", error);
								}else{
									getProfileTool().updateLoginAttemptCount(bbbProfile, true);
									status = true;
									loginRespVO.setProfileId(bbbProfile.getRepositoryId());
									loginRespVO.setProfileAutoExtended(false);
								}
							}else {
								if(loginRequest.isAutoExtendProfile()){
									isAccountLocked = getProfileTool().isAccountLocked(bbbProfile);
									if (isAccountLocked) {
										final String error = getLblTxtTemplateManager().getErrMsg("err_account_locked",	DEFAULT_LOCALE, null, null);
										errorPlaceHolderMap.put("err_account_locked", error);
									}else{
										//String emailOptIn = getProfileTool().fetchEmailOptInflagFromSisterSite(bbbProfile);
										try {
											getProfileTool().updateLoginAttemptCount(bbbProfile, true);
											getProfileTool().createSiteItem((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()), 
													loginRequest.getSiteId(), null, null, null);
											status = true;
											loginRespVO.setProfileId(bbbProfile.getRepositoryId());
											loginRespVO.setProfileAutoExtended(true);
										} catch (BBBSystemException e) {
											final String error = getLblTxtTemplateManager().getErrMsg("err_extend_profile", DEFAULT_LOCALE, null, null);
											errorPlaceHolderMap.put("err_extend_profile", error);
											logError(LogMessageFormatter.formatMessage(null, "err_extend_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1108),e);
										} 
									}
								}else{
									final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_sister_site", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_profile_exist_sister_site", error);
								}
							}
						}
					}
					
				}else{
					final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_diff_site_group", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_profile_exist_diff_site_group", error);
				}
				
			}else{
				final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_profile_not_found", error);
			}
			
		}
		
		if (status){
			loginRespVO.setError(false);
		}else{
			loginRespVO.setError(true);
			loginRespVO.setErrorMap(errorPlaceHolderMap);
		}
			
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | login | ends");
		}
		
		return loginRespVO;
	}

	/**
	 * @param profileInfoRequestVO
	 * @return
	 */
	public ProfileInfoResponseVO getProfileInfo(final ProfileInfoRequestVO profileInfoRequestVO) {
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | getProfileInfo | starts");
		}
		profileInfoRequestVO.setEmail(!BBBUtility.isEmpty(profileInfoRequestVO.getEmail()) ? profileInfoRequestVO.getEmail().toLowerCase() : profileInfoRequestVO.getEmail());
		final ProfileInfoResponseVO profileInfoResponseVO = new ProfileInfoResponseVO();
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		boolean status = false;
		
		validateProfileInfo(profileInfoRequestVO, errorPlaceHolderMap);
		
		if (errorPlaceHolderMap.isEmpty()) {
			try {
				Site site = getSiteContextManager().getSite(profileInfoRequestVO.getSiteId());
				SiteContextImpl context = new SiteContextImpl(getSiteContextManager(), site);
				getSiteContextManager().pushSiteContext(context);
			} catch (SiteContextException siteContextException) {
				try {
					throw new BBBSystemException("BBBSystemException in BBBProfileServices.getProfileInfo " + siteContextException);
				} catch (BBBSystemException e) {
					logError("Error in getProfileInfo ", e);
				}
			}

			boolean checkForProfileId = false;
			RepositoryItem bbbProfile = null;
			
			if(BBBUtility.isNotEmpty(profileInfoRequestVO.getEmail())){
				if(BBBUtility.isValidEmail(profileInfoRequestVO.getEmail())){
					try {
						bbbProfile = getProfileForString(profileInfoRequestVO.getEmail(), false);
						if(bbbProfile == null){
							final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found_for_email", DEFAULT_LOCALE, null, null);
							errorPlaceHolderMap.put("err_profile_not_found_for_email", error);
						}
					} catch (RepositoryException e) {
						final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_profile", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_fetch_profile", error);
						logError(LogMessageFormatter.formatMessage(null, "err_fetch_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1107),e);
					}
				}else{
					checkForProfileId = true;
				}
			}else{
				checkForProfileId = true;
			}
			
			if(checkForProfileId){
				if(BBBUtility.isNotEmpty(profileInfoRequestVO.getProfileId())){
					try {
						bbbProfile = getProfileForString(profileInfoRequestVO.getProfileId(), true);
						if(bbbProfile == null){
							final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found_for_profileId", DEFAULT_LOCALE, null, null);
							errorPlaceHolderMap.put("err_profile_not_found_for_profileId", error);
						}
					} catch (RepositoryException e) {
						final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_profile", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_fetch_profile", error);
						logError(LogMessageFormatter.formatMessage(null, "err_fetch_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1107),e);
					}
				}
			}
			
			if(bbbProfile != null){
				
				boolean diffSiteGroup = getProfileTool().isUserPresentToOtherGroup(bbbProfile, profileInfoRequestVO.getSiteId());
				if(!diffSiteGroup){
					RepositoryItem validProfile = null;
					IdentityManager identityManager = (IdentityManager) ServletUtil.getCurrentRequest().resolveName("/atg/dynamo/security/IdentityManager");
					try {
						if (identityManager.checkAuthenticationByPassword(
								(String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()), profileInfoRequestVO.getPassword())) {
									validProfile = getProfileTools().getItem((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()), 
											null, getLoginProfileType());
						}
						logDebug("Valid profile is: " + validProfile);
					} catch (atg.security.SecurityException e1) {
						logError("Error in validating profile ", e1);
					}
					if(validProfile == null){
						final String error = getLblTxtTemplateManager().getErrMsg("err_invalid_password", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_invalid_password", error);
					}else{
						Map<String, Object> siteAssociation = (Map<String, Object>)bbbProfile.getPropertyValue(getProfilePropertyManager().getUserSiteItemsPropertyName());
						if(siteAssociation != null && !siteAssociation.isEmpty()){
							if(siteAssociation.containsKey(profileInfoRequestVO.getSiteId())){
								fetchProfileDetails(bbbProfile, profileInfoRequestVO, profileInfoResponseVO, errorPlaceHolderMap);
								profileInfoResponseVO.setProfileId(bbbProfile.getRepositoryId());
								profileInfoResponseVO.setAutoExtend(false);
								if(errorPlaceHolderMap.isEmpty()){
									status = true;
								}
							}else {
								if(profileInfoRequestVO.isAutoExtendProfile()){
									//String emailOptIn = getProfileTool().fetchEmailOptInflagFromSisterSite(bbbProfile);
									try {
										getProfileTool().createSiteItem((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()), 
												profileInfoRequestVO.getSiteId(), null, null, null);
										fetchProfileDetails(bbbProfile, profileInfoRequestVO, profileInfoResponseVO, errorPlaceHolderMap);
										profileInfoResponseVO.setProfileId(bbbProfile.getRepositoryId());
										profileInfoResponseVO.setAutoExtend(true);
										if(errorPlaceHolderMap.isEmpty()){
											status = true;
										}
									} catch (BBBSystemException e) {
										final String error = getLblTxtTemplateManager().getErrMsg("err_extend_profile", DEFAULT_LOCALE, null, null);
										errorPlaceHolderMap.put("err_extend_profile", error);
										logError(LogMessageFormatter.formatMessage(null, "err_extend_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1108),e);
									} 
								}else{
									final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_sister_site", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_profile_exist_sister_site", error);
								}
							}
						}
					}
					
				}else{
					final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_diff_site_group", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_profile_exist_diff_site_group", error);
				}
				
			}
			
		}

		if(status){
			profileInfoResponseVO.setError(false);
		}else{
			profileInfoResponseVO.setError(true);
			profileInfoResponseVO.setErrorMap(errorPlaceHolderMap);
		}
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | getProfileInfo | ends");
		}
		
		return profileInfoResponseVO;
	}

	public LinkProfileResponseVO linkProfileAndRegistry(final LinkProfileRequestVO linkProfileRequestVO) {
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | getProfileInfo | starts |" + linkProfileRequestVO.getAppId());
		}
		linkProfileRequestVO.setCoRegEmail(!BBBUtility.isEmpty(linkProfileRequestVO.getCoRegEmail()) ? linkProfileRequestVO.getCoRegEmail().toLowerCase() : linkProfileRequestVO.getCoRegEmail());		
		final LinkProfileResponseVO linkProfileResponseVO = new LinkProfileResponseVO();
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		boolean status = false;
		validateLinkProfile(linkProfileRequestVO, linkProfileResponseVO, errorPlaceHolderMap);
		
		if (errorPlaceHolderMap.isEmpty()) {
			try {
				Site site = getSiteContextManager().getSite(linkProfileRequestVO.getSiteId());
				SiteContextImpl context = new SiteContextImpl(getSiteContextManager(), site);
				getSiteContextManager().pushSiteContext(context);
			} catch (SiteContextException siteContextException) {
				try {
					throw new BBBSystemException("BBBSystemException in BBBProfileServices.linkProfileAndRegistry " + siteContextException);
				} catch (BBBSystemException e) {
					logError("Error in linkProfileAndRegistry ", e);
				}
			}
			MutableRepositoryItem bbbRegistrantProfile = null;
			MutableRepositoryItem bbbCoRegistrantProfile = null;
			
			try{
				bbbRegistrantProfile = (MutableRepositoryItem)getProfileForString(linkProfileRequestVO.getProfileId(), true);
				if(bbbRegistrantProfile == null){
					final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found_for_profileId", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_profile_not_found_for_profileId", error);
				}
				if(BBBUtility.isNotEmpty(linkProfileRequestVO.getCoRegEmail())){
					if(BBBUtility.isValidEmail(linkProfileRequestVO.getCoRegEmail())){
						bbbCoRegistrantProfile = (MutableRepositoryItem)getProfileForString(linkProfileRequestVO.getCoRegEmail(), false);
						if(bbbCoRegistrantProfile == null){
							final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found_for_email", DEFAULT_LOCALE, null, null);
							errorPlaceHolderMap.put("err_profile_not_found_for_email", error);
						}
					}
				}
			}catch(RepositoryException e){
				errorPlaceHolderMap.put("err_fetch_profile", e.getMessage());
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1107),e);
			}
				
			if(errorPlaceHolderMap.isEmpty()){
				
				boolean diffSiteGroup = getProfileTool().isUserPresentToOtherGroup(bbbRegistrantProfile, linkProfileRequestVO.getSiteId());
				if(!diffSiteGroup){
					
					RegistryVO registryVO = new RegistryVO();
					RegistrantVO registrantVO = new RegistrantVO();
					EventVO eventVO = new EventVO();
					RegistryTypes registryTypes = new RegistryTypes();
					
					eventVO.setEventDate(linkProfileRequestVO.getEventDate());
					registryTypes.setRegistryTypeName(linkProfileRequestVO.getEventType());
					if(null!=bbbRegistrantProfile){
						registrantVO.setEmail((String)bbbRegistrantProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()));
					}
					registryVO.setRegistrantVO(registrantVO);
					registryVO.setRegistryId(linkProfileRequestVO.getRegId());
					registryVO.setSiteId(linkProfileRequestVO.getSiteId());
					registryVO.setRegistryType(registryTypes);
					registryVO.setEvent(eventVO);
					
					try {
						
						getGiftRegistryTools().addORUpdateRegistry(registryVO, bbbRegistrantProfile, bbbCoRegistrantProfile);
						
					}catch (BBBSystemException e) {
						errorPlaceHolderMap.put("err_system_exp", e.getMessage());
						logError(LogMessageFormatter.formatMessage(null, "err_system_exp" , BBBCoreErrorConstants.ACCOUNT_ERROR_1109),e);
					} 
					
					if(errorPlaceHolderMap.isEmpty()){
						if(null!=bbbRegistrantProfile){
							linkProfileResponseVO.setProfileId(bbbRegistrantProfile.getRepositoryId());
						}
						status = true;
						if(bbbCoRegistrantProfile != null){
							linkProfileResponseVO.setCoRegProfileId(bbbCoRegistrantProfile.getRepositoryId());
							TemplateEmailInfoImpl templateInfo = new TemplateEmailInfoImpl();
							templateInfo.setContentProcessor(getEmailContentProcessor());
							
							try {
								Map placeHolderMap = new HashMap();
								try {
									if(getCatalogTools().getAllValuesForKey("BBBWebServices", "profileWSContextPath") != null) {
										templateInfo.setTemplateURL(getCatalogTools().getAllValuesForKey("BBBWebServices", "profileWSContextPath").get(0) + getTemplateURL());
									}
								} catch (BBBSystemException | BBBBusinessException e1) {
									logError(e1);
								} 
								placeHolderMap.put(EMAIL_TYPE, ET_COREG_EMAIL);
								placeHolderMap.put(FORM_SITE, linkProfileRequestVO.getSiteId());
								placeHolderMap.put(FORM_FNAME, (String)bbbCoRegistrantProfile.getPropertyValue(getProfilePropertyManager().getFirstNamePropertyName()));
								placeHolderMap.put(FORM_LNAME, (String)bbbCoRegistrantProfile.getPropertyValue(getProfilePropertyManager().getLastNamePropertyName()));
								placeHolderMap.put(FORM_EMAIL, linkProfileRequestVO.getCoRegEmail());
								placeHolderMap.put(FORM_REGISTRANT_NAME, (String)bbbRegistrantProfile.getPropertyValue(getProfilePropertyManager().getFirstNamePropertyName())
										+" "+(String)bbbRegistrantProfile.getPropertyValue(getProfilePropertyManager().getLastNamePropertyName()));
								
								placeHolderMap.put(FORM_EVENT_TYPE, linkProfileRequestVO.getEventType());
								placeHolderMap.put(FORM_REG_ID, linkProfileRequestVO.getRegId());
								
								templateInfo.setMessageTo(linkProfileRequestVO.getCoRegEmail());
								templateInfo.setMailingId(linkProfileRequestVO.getCoRegEmail());
								Map pTemplateParams = new HashMap();
								pTemplateParams.put(PLACE_HOLDER, placeHolderMap);
								MutableRepositoryItem[] users = lookupUsers(linkProfileRequestVO.getCoRegEmail(), linkProfileRequestVO.getCoRegEmail());
								((BBBProfileTools) getProfileTool()).sendEmail(users, pTemplateParams, templateInfo);
								
							}catch(BBBSystemException e){
								logError("Error while doing profile linking", e);
							}
						}
					}
					
				}else{
					String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_diff_site_group", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_profile_exist_diff_site_group", error);
				}
				
			}
			
		}
		
		if(status){
			linkProfileResponseVO.setError(false);
		}else{
			linkProfileResponseVO.setError(true);
			linkProfileResponseVO.setErrorMap(errorPlaceHolderMap);
		}
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | getProfileInfo | ends");
		}
		
		return linkProfileResponseVO;

	}

	public LinkCoRegistrantResponseVO linkCoRegistrant(final LinkCoRegistrantRequestVO linkCoRegistrantRequestVO) {
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | getProfileInfo | starts");
		}
		linkCoRegistrantRequestVO.setCoRegEmail(!BBBUtility.isEmpty(linkCoRegistrantRequestVO.getCoRegEmail()) ? linkCoRegistrantRequestVO.getCoRegEmail().toLowerCase() : linkCoRegistrantRequestVO.getCoRegEmail());
		boolean status = false;
		final LinkCoRegistrantResponseVO linkCoRegistrantResponseVO = new LinkCoRegistrantResponseVO();
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		validateCoRegistrant(linkCoRegistrantRequestVO, linkCoRegistrantResponseVO, errorPlaceHolderMap);
		
		if (errorPlaceHolderMap.isEmpty()) {
			try {
				Site site = getSiteContextManager().getSite(linkCoRegistrantRequestVO.getSiteId());
				SiteContextImpl context = new SiteContextImpl(getSiteContextManager(), site);
				getSiteContextManager().pushSiteContext(context);
			} catch (SiteContextException siteContextException) {
				try {
					throw new BBBSystemException("BBBSystemException in BBBProfileServices.linkCoRegistrant " + siteContextException);
				} catch (BBBSystemException e) {
					logError("Error in linkCoRegistrant ", e);
				}
			}
			RepositoryItem[] registryItem = null;
			RepositoryItem bbbCoregistrantProfile = null;
			
			try {
				registryItem = getGiftRegistryTools().fetchGiftRepositoryItem(linkCoRegistrantRequestVO.getSiteId(), linkCoRegistrantRequestVO.getRegId());
			} catch (BBBSystemException e) {
				String invalidRegistry = getLblTxtTemplateManager().getErrMsg("err_fetch_registry", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_registry", invalidRegistry);
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_registry" , BBBCoreErrorConstants.ACCOUNT_ERROR_1115),e);
			} catch (BBBBusinessException e) {
				String invalidRegistry = getLblTxtTemplateManager().getErrMsg("err_fetch_registry", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_registry", invalidRegistry);
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_registry" , BBBCoreErrorConstants.ACCOUNT_ERROR_1115),e);
			}
			
			if (registryItem == null || registryItem.length <= 0) {
				String error = getLblTxtTemplateManager().getErrMsg("err_invalid_registry", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_invalid_registry", error);
			}else{
				
				bbbCoregistrantProfile = getProfileTool().getItemFromEmail(linkCoRegistrantRequestVO.getCoRegEmail());
				if(bbbCoregistrantProfile != null){
					boolean diffSiteGroup = getProfileTool().isUserPresentToOtherGroup(bbbCoregistrantProfile, linkCoRegistrantRequestVO.getSiteId());
					if(!diffSiteGroup){
						
						RegistryVO registryVO = new RegistryVO();
						RegistrantVO registrantVO = new RegistrantVO();
						EventVO eventVO = new EventVO();
						RegistryTypes registryTypes = new RegistryTypes();
						
						registryVO.setRegistryId(linkCoRegistrantRequestVO.getRegId());
						registryVO.setSiteId(linkCoRegistrantRequestVO.getSiteId());
						
						registrantVO.setEmail(linkCoRegistrantRequestVO.getCoRegEmail());
						registryVO.setRegistrantVO(registrantVO);
						
						eventVO.setEventDate(linkCoRegistrantRequestVO.getEventDate());
						registryVO.setEvent(eventVO);
						
						registryTypes.setRegistryTypeName(linkCoRegistrantRequestVO.getEventType());
						registryVO.setRegistryType(registryTypes);
						
						try {
							
							getGiftRegistryManager().linkRegistry(registryVO, false);
							
						}catch (BBBSystemException e) {
							errorPlaceHolderMap.put("err_system_exp", e.getMessage());
							logError("Error while doing coregistrant linking", e);
						} catch (RepositoryException e) {
							errorPlaceHolderMap.put("err_system_exp", e.getMessage());
							logError(LogMessageFormatter.formatMessage(null, "err_system_exp" , BBBCoreErrorConstants.ACCOUNT_ERROR_1109),e);
						}
						
						if(errorPlaceHolderMap.isEmpty()){
							linkCoRegistrantResponseVO.setCoRegProfileId(bbbCoregistrantProfile.getRepositoryId());
							status = true;
							try{
								
								RepositoryItem bbbRegistrantProfile = (RepositoryItem)registryItem[0].getPropertyValue("registryOwner");
								
								TemplateEmailInfoImpl templateInfo = new TemplateEmailInfoImpl();
								templateInfo.setContentProcessor(getEmailContentProcessor());
								
								Map placeHolderMap = new HashMap();
								try {
									if(getCatalogTools().getAllValuesForKey("BBBWebServices", "profileWSContextPath") != null) {
										templateInfo.setTemplateURL(getCatalogTools().getAllValuesForKey("BBBWebServices", "profileWSContextPath").get(0) + getTemplateURL());
									}
								} catch (BBBSystemException e1) {
									logError("Error while sending coregistrant email", e1);
								} catch (BBBBusinessException e1) {
									logError("Error while sending coregistrant email", e1);
								}
								
								placeHolderMap.put(EMAIL_TYPE, ET_COREG_EMAIL);
								placeHolderMap.put(FORM_SITE, linkCoRegistrantRequestVO.getSiteId());
								placeHolderMap.put(FORM_FNAME, (String)bbbCoregistrantProfile.getPropertyValue(getProfilePropertyManager().getFirstNamePropertyName()));
								placeHolderMap.put(FORM_LNAME, (String)bbbCoregistrantProfile.getPropertyValue(getProfilePropertyManager().getLastNamePropertyName()));
								placeHolderMap.put(FORM_EMAIL, linkCoRegistrantRequestVO.getCoRegEmail());
								placeHolderMap.put(FORM_REGISTRANT_NAME, (String)bbbRegistrantProfile.getPropertyValue(getProfilePropertyManager().getFirstNamePropertyName())
												+" "+(String)bbbRegistrantProfile.getPropertyValue(getProfilePropertyManager().getLastNamePropertyName()));
								placeHolderMap.put(FORM_EVENT_TYPE, linkCoRegistrantRequestVO.getEventType());
								placeHolderMap.put(FORM_REG_ID, linkCoRegistrantRequestVO.getRegId());
								
								templateInfo.setMessageTo(linkCoRegistrantRequestVO.getCoRegEmail());
								templateInfo.setMailingId(linkCoRegistrantRequestVO.getCoRegEmail());
								Map pTemplateParams = new HashMap();
								pTemplateParams.put(PLACE_HOLDER, placeHolderMap);
								MutableRepositoryItem[] users = lookupUsers(linkCoRegistrantRequestVO.getCoRegEmail(), linkCoRegistrantRequestVO.getCoRegEmail());
								((BBBProfileTools) getProfileTool()).sendEmail(users, pTemplateParams, templateInfo);
								
							}catch(BBBSystemException e){
								logError("Error while sending coregistrant email", e);
							}
						}
							
					}else{
						String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_diff_site_group", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_profile_exist_diff_site_group", error);
					}
				}else{
					String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_profile_not_found", error);
				}
				
			}
			
		}
		
		if(status){
			linkCoRegistrantResponseVO.setError(false);
		}else{
			linkCoRegistrantResponseVO.setError(true);
			linkCoRegistrantResponseVO.setErrorMap(errorPlaceHolderMap);
		}

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | getProfileInfo | ends");
		}
		
		return linkCoRegistrantResponseVO;
	}

	public BillingAddressResponseVO getBillingAddress(final BillingAddressRequestVO billingAddressRequestVO) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | getBillingAddress | Starts");
		}
		billingAddressRequestVO.setEmail(!BBBUtility.isEmpty(billingAddressRequestVO.getEmail()) ? billingAddressRequestVO.getEmail().toLowerCase() : billingAddressRequestVO.getEmail());
		boolean status = false;
		final BillingAddressResponseVO billingAddressResponseVO = new BillingAddressResponseVO();
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		validateBillingAddress(billingAddressRequestVO, billingAddressResponseVO, errorPlaceHolderMap);
		
		if (errorPlaceHolderMap.isEmpty()) {
			try {
				Site site = getSiteContextManager().getSite(billingAddressRequestVO.getSiteId());
				SiteContextImpl context = new SiteContextImpl(getSiteContextManager(), site);
				getSiteContextManager().pushSiteContext(context);
			} catch (SiteContextException siteContextException) {
				try {
					throw new BBBSystemException("BBBSystemException in BBBProfileServices.getBillingAddress " + siteContextException);
				} catch (BBBSystemException e) {
					logError("Error in getBillingAddress ", e);
				}
			}

			
			boolean checkForProfileId = false;
			RepositoryItem bbbProfile = null;
			
			if(BBBUtility.isNotEmpty(billingAddressRequestVO.getEmail())){
				if(BBBUtility.isValidEmail(billingAddressRequestVO.getEmail())){
					try {
						bbbProfile = getProfileForString(billingAddressRequestVO.getEmail(), false);
						if(bbbProfile == null){
							//checkForProfileId = true;
							final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found_for_email", DEFAULT_LOCALE, null, null);
							errorPlaceHolderMap.put("err_profile_not_found_for_email", error);
						}
					} catch (RepositoryException e) {
						final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_profile", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_fetch_profile", error);
						logError(LogMessageFormatter.formatMessage(null, "err_fetch_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1107),e);
					}
				}else{
					checkForProfileId = true;
				}
			}else{
				checkForProfileId = true;
			}
			
			if(checkForProfileId){
				if(BBBUtility.isNotEmpty(billingAddressRequestVO.getProfileId())){
					try {
						bbbProfile = getProfileForString(billingAddressRequestVO.getProfileId(), true);
						if(bbbProfile == null){
							final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found_for_profileId", DEFAULT_LOCALE, null, null);
							errorPlaceHolderMap.put("err_profile_not_found_for_profileId", error);
						}
					} catch (RepositoryException e) {
						final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_profile", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_fetch_profile", error);
						logError(LogMessageFormatter.formatMessage(null, "err_fetch_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1107),e);
					}
				}
			}
			
			if(bbbProfile != null){
				
				boolean diffSiteGroup = getProfileTool().isUserPresentToOtherGroup(bbbProfile, billingAddressRequestVO.getSiteId());
				if(!diffSiteGroup){
					
					Map<String, Object> siteAssociation = (Map<String, Object>)bbbProfile.getPropertyValue(getProfilePropertyManager().getUserSiteItemsPropertyName());
					if(siteAssociation != null && !siteAssociation.isEmpty()){
						if(siteAssociation.containsKey(billingAddressRequestVO.getSiteId())){
							
							billingAddressResponseVO.setEmail((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()));
							billingAddressResponseVO.setProfileId(bbbProfile.getRepositoryId());
							String country = BBBCoreConstants.BLANK;
							Address address = null;
							RepositoryItem defaultBillingAddress = getProfileTool().getDefaultBillingAddress(bbbProfile);
							
							if (null != defaultBillingAddress) {
								try {
									address = getProfileTool().getAddressFromRepositoryItem(defaultBillingAddress);
									SiteVO siteDetail = getCatalogTools().getSiteDetailFromSiteId(billingAddressRequestVO.getSiteId());
									if(null != siteDetail) {
										address.setCountry(siteDetail.getCountryCode() == null ? country : siteDetail.getCountryCode());
									}
								} catch (RepositoryException e) {
									logError(LogMessageFormatter.formatMessage(null, "err_fetch_billing_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1110),e);
									final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_billing_address", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_fetch_billing_address", error);
									
								}catch (BBBBusinessException e) {
									logError(LogMessageFormatter.formatMessage(null, "err_fetch_billing_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1110),e);
									final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_billing_address", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_fetch_billing_address", error);
									
								} catch (BBBSystemException e) {
									logError(LogMessageFormatter.formatMessage(null, "err_fetch_billing_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1110),e);
									final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_billing_address", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_fetch_billing_address", error);
									
								}
								
								billingAddressResponseVO.setDefaultBillingAddress(address);
							}	
							
							if(errorPlaceHolderMap.isEmpty()){
								status = true;
							}
							
						}
					}else{
						final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_profile_not_found", error);
					}
					
				}else{
					final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_diff_site_group", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_profile_exist_diff_site_group", error);
				}
			}
		}
		
		if (status) {
			billingAddressResponseVO.setError(false);
		}else{
			billingAddressResponseVO.setError(true);
			billingAddressResponseVO.setErrorMap(errorPlaceHolderMap);
		}
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | getBillingAddress | Ends");
		}
		
		return billingAddressResponseVO;
		
	}

	private void validateLogin(final LoginRequestVO pReqVO, final LoginResponseVO pRespVO, final Map<String, String> errorPlaceHolderMap) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateLogin | start");
		}
		
		// data validation
		if (BBBUtility.isEmpty(pReqVO.getSiteId())) {
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_empty_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_siteId", invalidSiteId);
		}else if(!isValidSiteId(pReqVO.getSiteId())){
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_invalid_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_siteId", invalidSiteId);
		}
		
		if(BBBUtility.isEmpty(pReqVO.getLoginId())){
			final String invalidLoginId = getLblTxtTemplateManager().getErrMsg("err_empty_loginId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_loginId", invalidLoginId);
		}else if(!pReqVO.isLoginProfileId()){			
			if(!BBBUtility.isValidEmail(pReqVO.getLoginId())){
				final String invalidEmailId = getLblTxtTemplateManager().getErrMsg("err_invalid_emailId", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_invalid_emailId", invalidEmailId);
			}
		}
		
		if(BBBUtility.isEmpty(pReqVO.getPassword())){
			final String invalidPwd = getLblTxtTemplateManager().getErrMsg("err_empty_password", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_password", invalidPwd);
		}
			
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateLogin | end");
		}
		
	}

	protected Map generateNewPasswordTemplateParams(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, RepositoryItem pProfile, String pNewPassword) {
		HashMap params = new HashMap(1);
		params.put("newpassword", pNewPassword);
		return params;
	}

	private MutableRepositoryItem[] lookupUsers(String login, final String email) {
		MutableRepositoryItem[] users = null;
		if (BBBUtility.isNotEmpty(login)) {
			MutableRepositoryItem user = (MutableRepositoryItem) getProfileTool().getItem(login, null, getLoginProfileType());
			if (user != null) {
				users = new MutableRepositoryItem[1];
				users[0] = user;
			}
		}
		if ((users == null) && BBBUtility.isNotEmpty(email)) {
			users = (MutableRepositoryItem[]) (MutableRepositoryItem[]) getProfileTool().getItemsFromEmail(email);
		}
		return users;
	}

	private void validateForgotPassword(final ForgotPasswordRequestVO pReqVO, final Map<String, String> errorPlaceHolderMap) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateForgotPassword | Starts");
		}
		
		if (BBBUtility.isEmpty(pReqVO.getEmail())) {
			String invalidEmail = getLblTxtTemplateManager().getErrMsg("err_empty_email", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_email", invalidEmail);
		} else {
			if (!BBBUtility.isValidEmail(pReqVO.getEmail())) {
				String invalidEmail = getLblTxtTemplateManager().getErrMsg("err_invalid_email", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_invalid_email", invalidEmail);
			}
		}
		
		if (BBBUtility.isEmpty(pReqVO.getSiteId())) {
			String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_empty_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_siteId", invalidSiteId);
		}else if(!isValidSiteId(pReqVO.getSiteId())){
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_invalid_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_siteId", invalidSiteId);
		}

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateForgotPassword | Ends");
		}
		
	}

	public ForgotPasswordResponseVO forgotPassword(ForgotPasswordRequestVO forgotPassReqVO) throws UnsupportedEncodingException {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | ForgotPasswordResponseVO | Starts");
		}
		forgotPassReqVO.setEmail(!BBBUtility.isEmpty(forgotPassReqVO.getEmail()) ? forgotPassReqVO.getEmail().toLowerCase() : forgotPassReqVO.getEmail());
		boolean success = false;
		ForgotPasswordResponseVO forgotPasswordResponseVO = new ForgotPasswordResponseVO();
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		validateForgotPassword(forgotPassReqVO, errorPlaceHolderMap);
		RepositoryItem bbbProfile = null;
		String newPassword = null;
		
		if (errorPlaceHolderMap.isEmpty()) {
			try {
				Site site = getSiteContextManager().getSite(forgotPassReqVO.getSiteId());
				SiteContextImpl context = new SiteContextImpl(getSiteContextManager(), site);
				getSiteContextManager().pushSiteContext(context);
			} catch (SiteContextException siteContextException) {
				try {
					throw new BBBSystemException("BBBSystemException in BBBProfileServices.forgotPassword " + siteContextException);
				} catch (BBBSystemException e) {
					logError("Error in forgotPassword ", e);
				}
			}
			try {
				bbbProfile = getProfileForString(forgotPassReqVO.getEmail(), false);
				if(bbbProfile != null){
					boolean diffSiteGroup = getProfileTool().isUserPresentToOtherGroup(bbbProfile, forgotPassReqVO.getSiteId());
					if(!diffSiteGroup){
						Map<String, Object> siteAssociation = (Map<String, Object>)bbbProfile.getPropertyValue(getProfilePropertyManager().getUserSiteItemsPropertyName());
						if(siteAssociation != null && !siteAssociation.isEmpty()){
							if(siteAssociation.containsKey(forgotPassReqVO.getSiteId())){
								try {
									newPassword = getProfileTool().generateNewPasswordForProfile(bbbProfile);
									forgotPasswordResponseVO.setResetSuccess(true);
									getProfileTool().updateLoginAttemptCount(bbbProfile, true);

									TemplateEmailInfoImpl templateInfo = new TemplateEmailInfoImpl();
									templateInfo.setContentProcessor(getEmailContentProcessor());
									templateInfo.setFillFromTemplate(true);
									
									try {
										if(getCatalogTools().getAllValuesForKey("BBBWebServices", "profileWSContextPath") != null) {
											templateInfo.setTemplateURL(getCatalogTools().getAllValuesForKey("BBBWebServices", "profileWSContextPath").get(0) + getTemplateURL());
										}
									} catch (BBBSystemException e1) {
										errorPlaceHolderMap.put("err_send_email", e1.getMessage());
										logError(LogMessageFormatter.formatMessage(null, "err_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1111),e1);
									} catch (BBBBusinessException e1) {
										errorPlaceHolderMap.put("err_send_email", e1.getMessage());
										logError(LogMessageFormatter.formatMessage(null, "err_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1111),e1);
									}
									
									templateInfo.setMessageTo(forgotPassReqVO.getEmail());
									templateInfo.setMailingId(forgotPassReqVO.getEmail());
									Map pTemplateParams = new HashMap();
									Map placeHolderMap = new HashMap();
									
									placeHolderMap.put(EMAIL_TYPE, ET_FORGOT_PASSWORD);
									placeHolderMap.put(FORM_SITE, forgotPassReqVO.getSiteId());
									placeHolderMap.put(FORM_NEW_PASSWORD, newPassword);
									pTemplateParams.put(PLACE_HOLDER, placeHolderMap);
									placeHolderMap.put(BBBCoreConstants.FORM_FNAME, (String)bbbProfile.getPropertyValue(getProfilePropertyManager().getFirstNamePropertyName()));
									placeHolderMap.put(BBBCoreConstants.FORM_LNAME, (String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLastNamePropertyName()));
									DynamoHttpServletRequest pRequest= ServletUtil.getCurrentRequest();
									String accountUrl = null;
									if(pRequest != null){
										 accountUrl = pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND + pRequest.getServerName() + "/store" + getLoginURL();
										
									}
									String urlSalt= createPasswordSalt(forgotPassReqVO.getEmail());
									if(BBBUtility.isNotEmpty(urlSalt))
									{
										placeHolderMap.put(BBBAccountConstants.ENCODED_TOKEN, URLEncoder.encode(urlSalt,BBBCoreConstants.UTF_8));
										logDebug("Token password salt creation successfully.");
									}
									placeHolderMap.put(BBBAccountConstants.ACCOUNT_LOGIN_URL, accountUrl);
									getProfileTool().sendEmailToUser((MutableRepositoryItem)bbbProfile, isSendEmailInSeparateThread(), isPersistEmails(), getTemplateEmailSender(), templateInfo, pTemplateParams);
									
									forgotPasswordResponseVO.setProfileId(bbbProfile.getRepositoryId());
									if(errorPlaceHolderMap.isEmpty()){
										success = true;
									}
									
								} catch (RepositoryException e1) {
									errorPlaceHolderMap.put("err_new_password_generation", e1.getMessage());
									logError(LogMessageFormatter.formatMessage(null, "err_new_password_generation" , BBBCoreErrorConstants.ACCOUNT_ERROR_1112),e1);
								}catch(TemplateEmailException e){
									errorPlaceHolderMap.put("err_send_email", e.getMessage());
									logError(LogMessageFormatter.formatMessage(null, "err_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1111),e);
								}
							}
							else
							{
								final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_sister_site", DEFAULT_LOCALE, null, null);
								errorPlaceHolderMap.put("err_profile_exist_sister_site", error);
							}
						}
							
						}else{
							final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_diff_site_group", DEFAULT_LOCALE, null, null);
							errorPlaceHolderMap.put("err_profile_exist_diff_site_group", error);
						}
					}
					else{
					final String error = getLblTxtTemplateManager().getErrMsg("err_profile_not_found", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_profile_not_found", error);
				}
				
			} catch (RepositoryException e1) {
				errorPlaceHolderMap.put("err_fetch_profile", e1.getMessage());
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1107),e1);
			}
		}
		
		if(success){
			forgotPasswordResponseVO.setError(false);
		}else{
			forgotPasswordResponseVO.setError(true);
			forgotPasswordResponseVO.setErrorMap(errorPlaceHolderMap);
		}

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | ForgotPasswordResponseVO | Ends");
		}
			
		return forgotPasswordResponseVO;
			
	}
	/**
	 * This method is used to create a salt value
	 * 
	 * @param pEmail
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws RepositoryException 
	 */
	private String createPasswordSalt(String emailId) throws UnsupportedEncodingException, RepositoryException {
		this.logDebug("BBBProfileServices.createPasswordSalt() method started");
		//enocded the token
		final String timestampToken = String.valueOf(new Date().getTime());
		TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
		boolean shouldRollback = false;
		try { 
			transactionDemarcation.begin(getTransactionManager());
			String encodedToken = Base64.encodeToString(timestampToken);
			String URLencodedToken = URLEncoder.encode(encodedToken,BBBCoreConstants.UTF_8);
			PBKDF2PasswordHasher mPBKDF2PasswordHasher = (PBKDF2PasswordHasher)Nucleus.getGlobalNucleus().resolveName("/com/bbb/security/PBKDF2PasswordHasher");
			boolean isPasswordCreated = false;
			byte[] saltByte = null;
			saltByte = mPBKDF2PasswordHasher.generateSalt().getBytes();
			String hashvalue= mPBKDF2PasswordHasher.mPwdHashingService.sha256(URLencodedToken);
			RepositoryItem profileItem = this.getProfileTool().getItemFromEmail(emailId);
			isPasswordCreated = mPBKDF2PasswordHasher.mPwdHashingService
						.createPasswordSalt(
								mPBKDF2PasswordHasher.mPwdHashingService.sha256(URLencodedToken), DatatypeConverter.printBase64Binary(saltByte),
								mPBKDF2PasswordHasher.getNumIterations());
			this.logDebug("BBBProfileServices.createPasswordSalt() method end");
			if(isPasswordCreated){
				this.getProfileTool().updateProperty(BBBCoreConstants.GENERATED_PASSWORD, true, profileItem);
				this.getProfileTool().updateToken(profileItem,emailId,hashvalue);
				return DatatypeConverter.printBase64Binary(saltByte);
			}
		}
		catch (Exception e) {
			shouldRollback = true;
			logError("creatResetResetPasswordToken:", e);
		}finally {
			try {
				transactionDemarcation.end(shouldRollback);
			} catch (TransactionDemarcationException e) {
				logError("Error While Creating rest password token." , e);
			}
		}
		return null;
	}
	private void validateRegistration(final ProfileRequestVO pReqVO, final ProfileResponseVO pRespVO, final Map<String, String> errorPlaceHolderMap) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateRegistration | Starts");
		}

		if (BBBUtility.isEmpty(pReqVO.getEmail())) {
			String invalidEmail = getLblTxtTemplateManager().getErrMsg("err_empty_email", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_email", invalidEmail);
		} else if (!BBBUtility.isValidEmail(pReqVO.getEmail())) {
			String invalidEmail = getLblTxtTemplateManager().getErrMsg("err_invalid_email", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_email", invalidEmail);
		}

		if (BBBUtility.isEmpty(pReqVO.getSiteId())) {
			String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_empty_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_siteId", invalidSiteId);
		}else if(!isValidSiteId(pReqVO.getSiteId())){
			String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_invalid_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_siteId", invalidSiteId);
		}

		if (BBBUtility.isEmpty(pReqVO.getFirstName())) {
			String invalidEmail = getLblTxtTemplateManager().getErrMsg("err_empty_first_name", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_first_name", invalidEmail);
		} else if(!BBBUtility.isValidFirstName(pReqVO.getFirstName())) {
			String invalidFirstName = getLblTxtTemplateManager().getErrMsg("err_profile_firstname_invalid", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_profile_firstname_invalid", invalidFirstName);
		}

		if (BBBUtility.isEmpty(pReqVO.getLastName())) {
			String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_empty_last_name", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_last_name", invalidSiteId);
		} else if(!BBBUtility.isValidLastName(pReqVO.getLastName())) {
				String invalidLastName = getLblTxtTemplateManager().getErrMsg("err_profile_lastname_invalid", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_profile_lastname_invalid", invalidLastName);
		}

		if (BBBUtility.isEmpty(pReqVO.getPassword())) {
			String invalidPassword = getLblTxtTemplateManager().getErrMsg("err_empty_password", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_password", invalidPassword);
		}else if(pReqVO.getPassword().toLowerCase().contains(pReqVO.getFirstName().toLowerCase()) 
								|| pReqVO.getPassword().toLowerCase().contains(pReqVO.getLastName().toLowerCase())){
			String invalidPassword = getLblTxtTemplateManager().getErrMsg("err_profile_password_contains_name", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_profile_password_contains_name", invalidPassword);
		}else if(!isValidPassword(pReqVO.getEmail(), pReqVO.getPassword())){
			errorPlaceHolderMap.put("err_password_rule", getProfileTool().getPasswordRuleChecker().getLastRuleCheckedDescription());
		}else{
			if (BBBUtility.isEmpty(pReqVO.getConfirmPassword())) {
				String invalidPassword = getLblTxtTemplateManager().getErrMsg("err_empty_confirm_password", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_empty_confirm_password", invalidPassword);
			}else if(!pReqVO.getPassword().equals(pReqVO.getConfirmPassword())){
				String invalidPassword = getLblTxtTemplateManager().getErrMsg("err_password_match", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_password_match", invalidPassword);
			}
		}
		
		if (!BBBUtility.isEmpty(pReqVO.getPhoneNumber()) && !BBBUtility.isValidPhoneNumber(pReqVO.getPhoneNumber())) {
			String invalidPhone = getLblTxtTemplateManager().getErrMsg("err_invalid_phone_number", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_phone_number", invalidPhone);
		}

		if (!BBBUtility.isEmpty(pReqVO.getMobileNumber()) && !BBBUtility.isValidPhoneNumber(pReqVO.getMobileNumber())) {
			String invalidMobile = getLblTxtTemplateManager().getErrMsg("err_invalid_mobile_number", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_mobile_number", invalidMobile);
		}
		
		if(errorPlaceHolderMap.isEmpty()){
			try {
				RepositoryItem bbbProfile = getProfileForString(pReqVO.getEmail(), false);
				
				if(bbbProfile != null && !BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE.equalsIgnoreCase((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getStatusPropertyName()))){
					boolean diffSiteGroup =  getProfileTool().isUserPresentToOtherGroup(bbbProfile, pReqVO.getSiteId());
					if(!diffSiteGroup){
						
						
						RepositoryItem validProfile = null;
						IdentityManager identityManager = (IdentityManager) ServletUtil.getCurrentRequest().resolveName("/atg/dynamo/security/IdentityManager");
						try {
							if (identityManager.checkAuthenticationByPassword(
									(String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()), pReqVO.getPassword())) {
										validProfile = getProfileTools().getItem((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()), 
												null, getLoginProfileType());
							}
							logDebug("Valid profile is: " + validProfile);
						} catch (atg.security.SecurityException e1) {
							logError("Error in validating profile ", e1);
						}
						if(validProfile == null)
						{
							boolean isAccountLocked = getProfileTool().isAccountLocked(bbbProfile);
							if (isAccountLocked) 
							{
								final String error = getLblTxtTemplateManager().getErrMsg("err_account_locked",	DEFAULT_LOCALE, null, null);
								errorPlaceHolderMap.put("err_account_locked", error);
							}
							else
							{
								final String error = getLblTxtTemplateManager().getErrMsg("err_invalid_password", DEFAULT_LOCALE, null, null);
								errorPlaceHolderMap.put("err_invalid_password", error);
								getProfileTool().updateLoginAttemptCount(bbbProfile, false);
							}
						}
						else
						{
							boolean isAccountLocked = getProfileTool().isAccountLocked(bbbProfile);
							if (isAccountLocked) 
							{
								final String error = getLblTxtTemplateManager().getErrMsg("err_account_locked",	DEFAULT_LOCALE, null, null);
								errorPlaceHolderMap.put("err_account_locked", error);
							}
							else
							{
								Map<String, Object> siteAssociation = (Map<String, Object>)bbbProfile.getPropertyValue(getProfilePropertyManager().getUserSiteItemsPropertyName());
								if(siteAssociation != null && !siteAssociation.isEmpty() && siteAssociation.containsKey(pReqVO.getSiteId()))
								{
									final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_same_site", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_profile_exist_same_site", error);
									//If autoextend is true, then send profile also in the response
									if(pReqVO.isAutoExtendProfile())
									{
										pRespVO.setProfileId(bbbProfile.getRepositoryId());
									}
								}
								else
								{
									final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_sister_site", DEFAULT_LOCALE, null, null);
									errorPlaceHolderMap.put("err_profile_exist_sister_site", error);
									//If autoextend is true, then send profile also in the response
									if(pReqVO.isAutoExtendProfile())
									{
										pRespVO.setProfileId(bbbProfile.getRepositoryId());
									}
								}
							}
						}
					}
					else
					{
							final String error = getLblTxtTemplateManager().getErrMsg("err_profile_exist_diff_site_group", DEFAULT_LOCALE, null, null);
							errorPlaceHolderMap.put("err_profile_exist_diff_site_group", error);
					}
				}
			} catch (RepositoryException e) {
				errorPlaceHolderMap.put("err_fetch_profile", e.getMessage());
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_profile" , BBBCoreErrorConstants.ACCOUNT_ERROR_1107),e);
			}
		}
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateRegistration | Ends");
		}
		
	}

	private void validateProfileInfo(final ProfileInfoRequestVO pReqVO, final Map<String, String> errorPlaceHolderMap) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateProfileInfo | Starts");
		}
		
		boolean profileIdCheck = false;
		
		if(BBBUtility.isNotEmpty(pReqVO.getEmail())){
			if(!BBBUtility.isValidEmail(pReqVO.getEmail())){
				profileIdCheck = true;
			}
		}else{
			profileIdCheck = true;
		}
		
		if(profileIdCheck){
			if(BBBUtility.isEmpty(pReqVO.getProfileId())){
				if(BBBUtility.isEmpty(pReqVO.getEmail())){
					String error = getLblTxtTemplateManager().getErrMsg("err_empty_email", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_empty_email", error);
				}else{
					String error = getLblTxtTemplateManager().getErrMsg("err_invalid_email", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_invalid_email", error);
				}
				String error = getLblTxtTemplateManager().getErrMsg("err_empty_profile_id", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_empty_profile_id", error);
			}
		}
		
		if (BBBUtility.isEmpty(pReqVO.getPassword())) {
			String invalidPassword = getLblTxtTemplateManager().getErrMsg("err_empty_password", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_password", invalidPassword);
		}
		
		if (BBBUtility.isEmpty(pReqVO.getSiteId())) {
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_empty_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_siteId", invalidSiteId);
		}else if(!isValidSiteId(pReqVO.getSiteId())){
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_invalid_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_siteId", invalidSiteId);
		}
		
	}

	private void validateLinkProfile(final LinkProfileRequestVO pReqVO, final LinkProfileResponseVO pRespVO, final Map<String, String> errorPlaceHolderMap) {

		if(isLoggingDebug()) {
			logDebug("BBBProfileServices | validateLinkProfile | Starts");
		}

		if(BBBUtility.isEmpty(pReqVO.getProfileId())) {
			String invalidProfileId = getLblTxtTemplateManager().getErrMsg("err_empty_profile_id", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_profile_id", invalidProfileId);
		} 
		
		if(BBBUtility.isEmpty(pReqVO.getSiteId())) {
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_empty_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_siteId", invalidSiteId);
		}else if(!isValidSiteId(pReqVO.getSiteId())){
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_invalid_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_siteId", invalidSiteId);
		}
		
		if(BBBUtility.isEmpty(pReqVO.getRegId())) {
			String invalidRegId = getLblTxtTemplateManager().getErrMsg("err_empty_reg_id", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_reg_id", invalidRegId);
		}
		
		if(BBBUtility.isEmpty(pReqVO.getEventType())) {
			String invalidEventType = getLblTxtTemplateManager().getErrMsg("err_empty_event_type", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_event_type", invalidEventType);
		}
		
		if(BBBUtility.isEmpty(pReqVO.getEventDate())) {
			String invalidEventDate = getLblTxtTemplateManager().getErrMsg("err_empty_event_date", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_event_date", invalidEventDate);
		}else{
			try{
				formatDate(pReqVO.getEventDate());
			}catch(ParseException e){
				String invalidEventDate = getLblTxtTemplateManager().getErrMsg("err_invalid_event_date", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_invalid_event_date", invalidEventDate);
			}
		}
		
		if(BBBUtility.isNotEmpty(pReqVO.getCoRegEmail())) {
			if(!BBBUtility.isValidEmail(pReqVO.getCoRegEmail())) {
				String error = getLblTxtTemplateManager().getErrMsg("err_invalid_email", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_invalid_email", error);	
			}
		} 
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateLinkProfile | Ends");
		}
		
	}

	private void validateCoRegistrant(final LinkCoRegistrantRequestVO pReqVO, final LinkCoRegistrantResponseVO pRespVO, final Map<String, String> errorPlaceHolderMap) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateCoRegistrant | Starts");
		}

		if(BBBUtility.isEmpty(pReqVO.getCoRegEmail())) {
			String invalidCoRegEmail = getLblTxtTemplateManager().getErrMsg("err_empty_email", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_co_reg_email", invalidCoRegEmail);
		} else {
			if (!BBBUtility.isValidEmail(pReqVO.getCoRegEmail())) {
				String invalidEmail = getLblTxtTemplateManager().getErrMsg("err_invalid_email", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_invalid_email", invalidEmail);
			} 
		}	

		if (BBBUtility.isEmpty(pReqVO.getRegId())) {
			String invalidRegId = getLblTxtTemplateManager().getErrMsg("err_empty_reg_id", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_reg_id", invalidRegId);
		}
		
		if (BBBUtility.isEmpty(pReqVO.getSiteId())) {
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_empty_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_siteId", invalidSiteId);
		}else if(!isValidSiteId(pReqVO.getSiteId())){
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_invalid_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_siteId", invalidSiteId);
		}
		
		if (BBBUtility.isEmpty(pReqVO.getEventType())) {
			String invalidEventType = getLblTxtTemplateManager().getErrMsg("err_empty_event_type", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_event_type", invalidEventType);
		}
		
		if (BBBUtility.isEmpty(pReqVO.getEventDate())) {
			String invalidEventType = getLblTxtTemplateManager().getErrMsg("err_empty_event_date", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_event_date", invalidEventType);
		}else {
			try {
				formatDate(pReqVO.getEventDate());
			} catch (ParseException e) {
				String invalidEventDate = getLblTxtTemplateManager().getErrMsg("err_invalid_event_date", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_invalid_event_date", invalidEventDate);
			}
		
		}

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateCoRegistrant | Ends");
		}

	}

	private void validateBillingAddress(final BillingAddressRequestVO pReqVO, final BillingAddressResponseVO pRespVO, final Map<String, String> errorPlaceHolderMap) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateBillingAddress | Starts");
		}
		
		boolean profileIdCheck = false;
		
		if(BBBUtility.isNotEmpty(pReqVO.getEmail())){
			if(!BBBUtility.isValidEmail(pReqVO.getEmail())){
				profileIdCheck = true;
			}
		}else{
			profileIdCheck = true;
		}
		
		if(profileIdCheck){
			if(BBBUtility.isEmpty(pReqVO.getProfileId())){
				if(BBBUtility.isEmpty(pReqVO.getEmail())){
					String error = getLblTxtTemplateManager().getErrMsg("err_empty_email", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_empty_email", error);
				}else{
					String error = getLblTxtTemplateManager().getErrMsg("err_invalid_email", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_invalid_email", error);
				}
				String error = getLblTxtTemplateManager().getErrMsg("err_empty_profile_id", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_empty_profile_id", error);
			}
		}
		
		if (BBBUtility.isEmpty(pReqVO.getSecurityToken())) {
			String error = getLblTxtTemplateManager().getErrMsg("err_empty_security_token", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_security_token", error);
		}else{
			try {
				String securityToken = getCatalogTools().getAllValuesForKey("BBBWebServices", "BBBWSSecurityToken").get(0);
				if (securityToken != null) {
					if (!securityToken.equals(pReqVO.getSecurityToken())) {
						String invalidToken = getLblTxtTemplateManager().getErrMsg("err_invalid_token", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_invalid_token", invalidToken);
					}
				}else{
					String invalidToken = getLblTxtTemplateManager().getErrMsg("err_invalid_token", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_invalid_token", invalidToken);
				}
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_fetching_ws_token" , BBBCoreErrorConstants.ACCOUNT_ERROR_1116),e);
				String invalidToken = getLblTxtTemplateManager().getErrMsg("err_fetching_ws_token", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetching_ws_token", invalidToken);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_fetching_ws_token" , BBBCoreErrorConstants.ACCOUNT_ERROR_1116),e);
				String invalidToken = getLblTxtTemplateManager().getErrMsg("err_fetching_ws_token", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetching_ws_token", invalidToken);
			}
		}
		
		if (BBBUtility.isEmpty(pReqVO.getSiteId())) {
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_empty_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_empty_siteId", invalidSiteId);
		}else if(!isValidSiteId(pReqVO.getSiteId())){
			final String invalidSiteId = getLblTxtTemplateManager().getErrMsg("err_invalid_siteId", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_invalid_siteId", invalidSiteId);
		}
				
		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | validateBillingAddress | Ends");
		}
		
	}

	/**
	 * This method ensures that a transaction exists before returning. If there
	 * is no transaction, a new one is started and returned. In this case, you
	 * must call commitTransaction when the transaction completes.
	 * 
	 * @return a <code>Transaction</code> value
	 */
	protected Transaction ensureTransaction() {
		try {
			TransactionManager transactionManager = getTransactionManager();
			Transaction transaction = transactionManager.getTransaction();
			if (transaction == null) {
				transactionManager.begin();
				transaction = transactionManager.getTransaction();
				return transaction;
			}
			return null;
		} catch (NotSupportedException e) {
			logError(e);
		} catch (SystemException e) {
			logError(e);
		}
		return null;
	}

	public ProfileResponseVO createUser(ProfileRequestVO profileRequestVO) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | createAccount | Starts |" + profileRequestVO.getAppId());
		}
		profileRequestVO.setEmail(!BBBUtility.isEmpty(profileRequestVO.getEmail()) ? profileRequestVO.getEmail().toLowerCase().trim() : profileRequestVO.getEmail());
		ProfileResponseVO profileResponseVO = new ProfileResponseVO();
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		
		// validate the user profile information before creating account
		validateRegistration(profileRequestVO, profileResponseVO, errorPlaceHolderMap);
		String emailOptInFlag = null;
		if (profileRequestVO.isEmailOptIn()) {
			emailOptInFlag = BBBCoreConstants.YES;
		}else{
			emailOptInFlag = BBBCoreConstants.NO;
		}
		
		if (errorPlaceHolderMap.isEmpty()) {
			try {
				Site site = getSiteContextManager().getSite(profileRequestVO.getSiteId());
				SiteContextImpl context = new SiteContextImpl(getSiteContextManager(), site);
				getSiteContextManager().pushSiteContext(context);
			} catch (SiteContextException siteContextException) {
				
					logError("BBBSystemException in BBBProfileServices.createUser " + siteContextException);
				
			}
			
			// enters if validation passes
			
			Transaction transaction = null;
			boolean isException = false;
			transaction = ensureTransaction();

			try {
				RepositoryItem savedProfile = getProfileForString(profileRequestVO.getEmail(), false);
				if (savedProfile != null) {
					// if profile is already exist then update the existing profile properties - case of shallow profile
					MutableRepositoryItem profileObj = (MutableRepositoryItem) getProfileTool().getItemFromEmail(profileRequestVO.getEmail());
					updateProfileProperties(profileObj, profileRequestVO);
					getProfileTool().getProfileRepository().updateItem(profileObj);
				} else {
					// if profile not exists then create new profile
					MutableRepositoryItem profileObj = getProfileTool().getProfileRepository().createItem(getProfilePropertyManager().getProfileItemDiscriptorName());
					profileObj.setPropertyValue(getProfilePropertyManager().getLoginPropertyName(), profileRequestVO.getEmail().toLowerCase()); 
					profileObj.setPropertyValue(getProfilePropertyManager().getEmailAddressPropertyName(), profileRequestVO.getEmail().toLowerCase());
					updateProfileProperties(profileObj, profileRequestVO);
					savedProfile = getProfileTool().getProfileRepository().addItem(profileObj);
				}
				try {
					((BBBProfileTools) getProfileTool()).createSiteItem(profileRequestVO.getEmail(), profileRequestVO.getSiteId(), null, null, emailOptInFlag, savedProfile);
					if(profileRequestVO.isAutoExtendProfile())//If Auto Extend flag is true add to other sites in the same group
					{
						if (isLoggingDebug())
						{
							logDebug("Handling case of auto extend during profile creation");
						}
						profileManager.addSiteToProfile(profileRequestVO.getEmail(), profileRequestVO.getSiteId(), null, null, emailOptInFlag);
					}
						
				} catch (BBBSystemException e1) {
					logError("Error creating site item for email id : " + profileRequestVO.getEmail(), e1);
				}
				
				// Sends the Email on successful account creation
			
				TemplateEmailInfoImpl templateInfo = new TemplateEmailInfoImpl();
				templateInfo.setContentProcessor(getEmailContentProcessor());
				Map placeHolderMap = new HashMap();
				try {
					if(getCatalogTools().getAllValuesForKey("BBBWebServices", "profileWSContextPath") != null) {
						templateInfo.setTemplateURL(getCatalogTools().getAllValuesForKey("BBBWebServices", "profileWSContextPath").get(0) + getTemplateURL());
					}
				} catch (BBBSystemException e1) {
					logError("Error sending email for " + profileRequestVO.getEmail(), e1);
				} catch (BBBBusinessException e1) {
					logError("Error sending email for " + profileRequestVO.getEmail(), e1);
				}
				placeHolderMap.put(EMAIL_TYPE, ET_CREATE_ACCOUNT);
				placeHolderMap.put(FORM_SITE, profileRequestVO.getSiteId());
				placeHolderMap.put(FORM_FNAME, profileRequestVO.getFirstName());
				placeHolderMap.put(FORM_LNAME, profileRequestVO.getLastName());
				placeHolderMap.put(FORM_EMAIL, profileRequestVO.getEmail());
				String accountLoginURL=null;
		    	List<String> configValue = null;
				try {
					configValue = this.getCatalogTools().getAllValuesForKey(
					        BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
				} catch (BBBSystemException e1) {
					logError("Error while send profile registration email - getting value for config key. " + profileRequestVO.getEmail(), e1);
				} catch (BBBBusinessException e1) {
					logError("Error while send profile registration email - getting value for config key. " + profileRequestVO.getEmail(), e1);
				}
		        if ((configValue != null) && (configValue.size() > 0)) {
		            // set serverName from config key and context path from properties
		        	accountLoginURL = ServletUtil.getCurrentRequest().getScheme() + BBBAccountConstants.SCHEME_APPEND + configValue.get(0) + this.getStoreContextPath() + this.getAccountLoginURL();
		        }
		        placeHolderMap.put(BBBAccountConstants.ACCOUNT_LOGIN_URL, accountLoginURL);
				templateInfo.setMessageTo(profileRequestVO.getEmail());
				templateInfo.setMailingId(profileRequestVO.getEmail());
				Map pTemplateParams = new HashMap();
				pTemplateParams.put("placeHolderValues", placeHolderMap);
				MutableRepositoryItem recipients[] = null;
				try {
					
					recipients = ((BBBProfileTools) getProfileTool()).lookupUsers(profileRequestVO.getEmail(), profileRequestVO.getEmail(), BBBProfileServicesConstant.USER);
				} catch (RepositoryException e) {
					if (isLoggingError()) {
						logError("BBBProfileServices | createAccount | RepositoryException | " + e);
					}
				}
				if (recipients == null) {

					String noProfile = getLblTxtTemplateManager().getErrMsg("err_profile_no_such_profile", DEFAULT_LOCALE, null, null);
					errorPlaceHolderMap.put("err_profile_no_such_profile", noProfile);
				} else {

					try {
						((BBBProfileTools) getProfileTool()).sendEmail(recipients, pTemplateParams, templateInfo);
						sendRegistrationTibcoEmail(errorPlaceHolderMap,profileRequestVO.getSiteId(),profileRequestVO,savedProfile.getRepositoryId());
					} catch (BBBSystemException e) {
						if (isLoggingError()) {
							logError(LogMessageFormatter.formatMessage(null, "err_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1111),e);
						}
						String sendEmail = getLblTxtTemplateManager().getErrMsg("err_send_email", DEFAULT_LOCALE, null, null);
						errorPlaceHolderMap.put("err_send_email", sendEmail);
					}
				}
				
				
				
				profileResponseVO.setProfileId(savedProfile.getRepositoryId());
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_create_user" , BBBCoreErrorConstants.ACCOUNT_ERROR_1113),e);
				isException = true;
				String invalidEmail = getLblTxtTemplateManager().getErrMsg("err_create_user", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_create_user", invalidEmail);
			}

			finally {
				endTransaction(isException, transaction);
			}

		} 
		
		if (errorPlaceHolderMap.containsKey("err_profile_exist_same_site") && profileRequestVO.isAutoExtendProfile()) {
			if (isLoggingDebug()){
				logDebug("Handling case of auto extend when err_profile_exist_same_site Start");
			}
			errorPlaceHolderMap.clear();
			profileManager.addSiteToProfile(profileRequestVO.getEmail(), profileRequestVO.getSiteId(), null, null, emailOptInFlag);
			if (isLoggingDebug()){
				logDebug("Handling case of auto extend when err_profile_exist_same_site End");
			}
		}else if (errorPlaceHolderMap.containsKey("err_profile_exist_sister_site") && profileRequestVO.isAutoExtendProfile()){
			if (isLoggingDebug()){
				logDebug("Handling case of auto extend when err_profile_exist_sister_site Start");
			}
			errorPlaceHolderMap.clear();
			try {
				mProfileTool.createSiteItem(profileRequestVO.getEmail(), profileRequestVO.getSiteId(), null, null, emailOptInFlag);
			} catch (BBBSystemException e) {
				logError("Error creating site item for email id : " + profileRequestVO.getEmail(), e);
			}
			if (isLoggingDebug()){
				logDebug("Handling case of auto extend when err_profile_exist_sister_site End");
			}
		}
			
		if (!errorPlaceHolderMap.isEmpty()) {
			profileResponseVO.setError(true);
			profileResponseVO.setErrorMap(errorPlaceHolderMap);
		}

		if (isLoggingDebug()) {
			logDebug("BBBProfileServices | createAccount | Ends | " + profileRequestVO.getAppId());
		}

		return profileResponseVO;
	}

	/**
	 * @return the mProfileTools
	 */
	public BBBProfileTools getProfileTool() {
		return mProfileTool;
	}

	/**
	 * @param mProfileTools
	 *            the mProfileTools to set
	 */
	public void setProfileTool(BBBProfileTools pProfileTools) {
		this.mProfileTool = pProfileTools;
	}

	/**
	 * @return the mDynRequest
	 */
	public DynamoHttpServletRequest getDynRequest() {
		return mDynRequest;
	}

	/**
	 * @param mDynRequest
	 *            the mDynRequest to set
	 */
	public void setDynRequest(final DynamoHttpServletRequest pDynRequest) {
		this.mDynRequest = pDynRequest;
	}

	/**
	 * @return the mDynResponse
	 */
	public DynamoHttpServletResponse getDynResponse() {
		return mDynResponse;
	}

	/**
	 * @param mDynResponse
	 *            the mDynResponse to set
	 */
	public void setDynResponse(final DynamoHttpServletResponse pDynResponse) {
		this.mDynResponse = pDynResponse;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the profileManager
	 */
	public BBBProfileManager getProfileManager() {
		return profileManager;
	}

	/**
	 * @param profileManager
	 *            the profileManager to set
	 */
	public void setProfileManager(BBBProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	/**
	 * @return the mGiftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return mGiftRegistryTools;
	}

	/**
	 * @param mGiftRegistryTools
	 *            the mGiftRegistryTools to set
	 */
	public void setGiftRegistryTools(GiftRegistryTools pGiftRegistryTools) {
		this.mGiftRegistryTools = pGiftRegistryTools;
	}

	/**
	 * method to check if error occur during the transaction if yes then execute
	 * transection rollback otherwise commit the transaction
	 * 
	 * @param isError
	 *            flag that represent error
	 * @param pTransaction
	 *            transaction object
	 */
	private void endTransaction(boolean isError, Transaction pTransaction) {
		try {
			if (isError) {
				if (pTransaction != null) {
					pTransaction.rollback();
				}
			} else {
				if (pTransaction != null) {
					pTransaction.commit();
				}
			}
		} catch (SecurityException e) {
			logError(e);
		} catch (IllegalStateException e) {
			logError(e);
		} catch (RollbackException e) {
			logError(e);
		} catch (HeuristicMixedException e) {
			logError(e);
		} catch (HeuristicRollbackException e) {
			logError(e);
		} catch (SystemException e) {
			logError(e);
		}
	}

	/**
	 * @return the profilePropertyManager
	 */
	public BBBPropertyManager getProfilePropertyManager() {
		return profilePropertyManager;
	}

	/**
	 * @param profilePropertyManager
	 *            the profilePropertyManager to set
	 */
	public void setProfilePropertyManager(BBBPropertyManager profilePropertyManager) {
		this.profilePropertyManager = profilePropertyManager;
	}

	/**
	 * @return the mTransactionManager
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	/**
	 * @param mTransactionManager
	 *            the mTransactionManager to set
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		this.mTransactionManager = pTransactionManager;
	}

	/**
	 * @return the mSendEmailInSeparateThread
	 */
	public boolean isSendEmailInSeparateThread() {
		return mSendEmailInSeparateThread;
	}

	/**
	 * @param mSendEmailInSeparateThread the mSendEmailInSeparateThread to set
	 */
	public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
		this.mSendEmailInSeparateThread = pSendEmailInSeparateThread;
	}

	/**
	 * @return the mPersistEmails
	 */
	public boolean isPersistEmails() {
		return mPersistEmails;
	}

	/**
	 * @param mPersistEmails the mPersistEmails to set
	 */
	public void setPersistEmails(boolean pPersistEmails) {
		this.mPersistEmails = pPersistEmails;
	}

	/**
	 * method to send Registration Email using Tibco 
	 * 
	 * @param pRequest
	 * @param siteId
	 * @return
	 */
	private void sendRegistrationTibcoEmail(Map<String, String> errorPlaceHolderMap, String siteId, ProfileRequestVO profileRequestVO, String profileId) {

		try {
			Map<String, Object> emailParams = createEmailParameters(siteId,profileRequestVO, profileId);
			getProfileManager().sendProfileRegistrationTibcoEmail(emailParams);
		} catch (BBBSystemException bbbSystemException) {
			logError(LogMessageFormatter.formatMessage(null, "err_profile_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1123),bbbSystemException);
			String errProfile = getLblTxtTemplateManager().getErrMsg("err_profile_send_email", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_profile_send_email", errProfile);
		} catch (BBBBusinessException bbbBusinessException) {
			logError(LogMessageFormatter.formatMessage(null, "err_profile_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1123),bbbBusinessException);
			String errProfile = getLblTxtTemplateManager().getErrMsg("err_profile_send_email", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_profile_send_email", errProfile);
		} catch (DatatypeConfigurationException configurationException) {
			logError(LogMessageFormatter.formatMessage(null, "err_profile_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1123),configurationException);
			String errProfile = getLblTxtTemplateManager().getErrMsg("err_profile_send_email", DEFAULT_LOCALE, null, null);
			errorPlaceHolderMap.put("err_profile_send_email", errProfile);
		}
	}
	
		
	/**
	 * This method will set email parameter for Profile Creation Email Notification
	 * 
	 * @param sProfileType
	 * @param sEmail
	 * @param siteId
	 * @return Map
	 */
	private Map<String, Object> createEmailParameters(String siteId, ProfileRequestVO profileRequestVO, String profileId) throws BBBSystemException,
			BBBBusinessException, DatatypeConfigurationException {
		
		
		Map<String, Object> emailParams = new HashMap<String, Object>();
		emailParams.put(BBBCoreConstants.EMAIL_TYPE, BBBCoreConstants.ET_CREATE_ACCOUNT);
		emailParams.put(BBBCoreConstants.FORM_SITE, siteId);
		emailParams.put(BBBCoreConstants.FORM_FNAME, profileRequestVO.getFirstName());
		emailParams.put(BBBCoreConstants.FORM_LNAME, profileRequestVO.getLastName());
		emailParams.put(BBBCoreConstants.FORM_EMAIL, profileRequestVO.getEmail());
		emailParams.put(BBBCoreConstants.FORM_PHONE_NUMBER, profileRequestVO.getPhoneNumber());
		emailParams.put(BBBCoreConstants.FORM_MOBILE_NUMBER, profileRequestVO.getMobileNumber());
		if(profileRequestVO.isEmailOptIn()){
			emailParams.put(BBBCoreConstants.FORM_OPTIN, BBBCoreConstants.TRUE);	
		}else{
			emailParams.put(BBBCoreConstants.FORM_OPTIN, BBBCoreConstants.FALSE);
		}
		emailParams.put(BBBCoreConstants.FORM_PROFILEID, profileId);
		if(profileRequestVO.isAutoExtendProfile())
		{
			emailParams.put(BBBCoreConstants.FORM_SHARE_ACCOUNT,  BBBCoreConstants.YES);
		}
		else
		{
			emailParams.put(BBBCoreConstants.FORM_SHARE_ACCOUNT,  BBBCoreConstants.NO);
		}
		List<String> siteIds = getCatalogTools().getAllValuesForKey(BBBCoreConstants.TIBCO_KEYS, siteId);
		String siteFlag = BBBCoreConstants.BLANK;
		if (null != siteIds  && !siteIds.isEmpty()) {
			siteFlag = siteIds.get(0).toString();
		}
		emailParams.put(BBBCoreConstants.SITE_FLAG_PARAM_NAME, siteFlag);
		emailParams.put(BBBCoreConstants.REQUESTED_DT_PARAM_NAME, BBBUtility.getXMLCalendar(new java.util.Date()));
		return emailParams;
		
		
	}
	/**
	 * method to convert String into Date format 
	 * @param dateAsString
	 * @return
	 * @throws ParseException
	 */
	private Date formatDate(String dateAsString) throws ParseException{

		DateFormat formatter = new SimpleDateFormat(BBBCoreConstants.DATE_FORMAT);
		Date date = (Date)formatter.parse(dateAsString);  
		return date;
		
	}
	
	private void fetchProfileDetails(RepositoryItem bbbProfile, ProfileInfoRequestVO profileInfoRequestVO, 
			ProfileInfoResponseVO profileInfoResponseVO, Map<String, String> errorPlaceHolderMap){
		
		profileInfoResponseVO.setFirstName(bbbProfile.getPropertyValue(FIRST_NAME) == null ? "" : (String)bbbProfile.getPropertyValue(FIRST_NAME));
		profileInfoResponseVO.setLastName(bbbProfile.getPropertyValue(LAST_NAME) == null ? "" : (String)bbbProfile.getPropertyValue(LAST_NAME));
		profileInfoResponseVO.setPhone(bbbProfile.getPropertyValue(PHONE_NUM) == null ? "" : (String)bbbProfile.getPropertyValue(PHONE_NUM));
		profileInfoResponseVO.setMobile(bbbProfile.getPropertyValue(MOBILE_NUM) == null ? "" : (String)bbbProfile.getPropertyValue(MOBILE_NUM));
		profileInfoResponseVO.setEmail(bbbProfile.getPropertyValue(EMAIL) == null ? "" : (String)bbbProfile.getPropertyValue(EMAIL));
		
		RepositoryItem defaultBillingAddress = getProfileTool().getDefaultBillingAddress(bbbProfile);
		String country = BBBCoreConstants.BLANK;
		Address address = null;
		if (null != defaultBillingAddress) {
			
			try {
				address = getProfileTool().getAddressFromRepositoryItem(defaultBillingAddress);
				SiteVO siteDetail = getCatalogTools().getSiteDetailFromSiteId(profileInfoRequestVO.getSiteId());
				if(null != siteDetail) {
					address.setCountry(siteDetail.getCountryCode() == null ? country : siteDetail.getCountryCode());
				}
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_billing_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1110),e);
				final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_billing_address", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_billing_address", error);
			}catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_billing_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1110),e);
				final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_billing_address", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_billing_address", error);
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_billing_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1110),e);
				final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_billing_address", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_billing_address", error);
			}
			
			profileInfoResponseVO.setDefaultBillingAddress(address);
			
		}
		
		country = BBBCoreConstants.BLANK;
		address = null;
		RepositoryItem defaultShippingAddress = getProfileTool().getDefaultShippingAddress(bbbProfile);
		
		if (null != defaultShippingAddress) {
			try {
				address = getProfileTool().getAddressFromRepositoryItem(defaultShippingAddress);
				SiteVO siteDetail = getCatalogTools().getSiteDetailFromSiteId(profileInfoRequestVO.getSiteId());
				if(null != siteDetail) {
					address.setCountry(siteDetail.getCountryCode() == null ? country : siteDetail.getCountryCode());
				}
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_shipping_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1114),e);
				final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_shipping_address", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_shipping_address", error);
			}catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_shipping_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1114),e);
				final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_shipping_address", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_shipping_address", error);
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null, "err_fetch_shipping_address" , BBBCoreErrorConstants.ACCOUNT_ERROR_1114),e);
				final String error = getLblTxtTemplateManager().getErrMsg("err_fetch_shipping_address", DEFAULT_LOCALE, null, null);
				errorPlaceHolderMap.put("err_fetch_shipping_address", error);
			}
			
			profileInfoResponseVO.setDefaultShippingAddress(address);
		}	
		
	}
	
	private boolean isValidSiteId(String siteId) {
		
		boolean isValid = false;
		try {
			
			String bedBathandUSSiteId = getCatalogTools().getAllValuesForKey(CONTENT_CATALOG_CONFIG_KEY , BED_BATH_AND_BEYOND_US_SITE_ID).get(0);
			String buybuybabySiteId = getCatalogTools().getAllValuesForKey(CONTENT_CATALOG_CONFIG_KEY , BUY_BUY_BABY_SITE_ID).get(0);
			String bedBathandCASiteId = getCatalogTools().getAllValuesForKey(CONTENT_CATALOG_CONFIG_KEY , BED_BATH_AND_BEYOND_CA_SITE_ID).get(0);
			
			if(bedBathandUSSiteId.equals(siteId) || buybuybabySiteId.equals(siteId) || bedBathandCASiteId.equals(siteId)){
				isValid = true;
			}
			
		} catch (BBBSystemException e) {
			logError("Error occured while fetching site id from content catalog", e);
		} catch (BBBBusinessException e) {
			logError("Error occured while fetching site id from content catalog", e);
		}
		
		return isValid;
	}

	private RepositoryItem getProfileForString(String profileIdOrEmail, boolean isProfileId) throws RepositoryException{
		
		RepositoryItem bbbProfile = null;
		
		if(isProfileId){
			bbbProfile = getProfileTool().getProfileRepository().getItem(profileIdOrEmail, getProfilePropertyManager().getProfileItemDiscriptorName());
		}else{
			bbbProfile = getProfileTool().getItemFromEmail(profileIdOrEmail);
		}
		
		return bbbProfile;
	}
	
	private boolean isValidPassword(String email, String password){
		boolean valid = false;
		Map map = new HashMap();
		map.put(LOGIN, email);
		valid = getProfileTool().getPasswordRuleChecker().checkRules(password, map);
		return valid;
	}
	  /**
	   * Populate Profile Properties from RequestVO
	   * 
	   * @param profileObj
	   * @param profileRequestVO 
	   */
	
	private void updateProfileProperties(final MutableRepositoryItem profileObj,ProfileRequestVO profileRequestVO){
		profileObj.setPropertyValue(getProfilePropertyManager().getFirstNamePropertyName(), profileRequestVO.getFirstName());
		profileObj.setPropertyValue(getProfilePropertyManager().getLastNamePropertyName(), profileRequestVO.getLastName());
		profileObj.setPropertyValue(getProfilePropertyManager().getMobileNumberPropertyName(), profileRequestVO.getMobileNumber());
		profileObj.setPropertyValue(getProfilePropertyManager().getPhoneNumberPropertyName(), profileRequestVO.getPhoneNumber());
		profileObj.setPropertyValue(getProfilePropertyManager().getReceiveEmailPropertyName(), BBBCoreConstants.YES);
		profileObj.setPropertyValue(getProfilePropertyManager().getStatusPropertyName(), "FULL");
		
		profileObj.setPropertyValue(getProfilePropertyManager().getLoggedIn(), false);
		// password encrypted before storage
		String encrypted = getProfileTool().getPropertyManager().generatePassword(profileRequestVO.getEmail().toLowerCase(), profileRequestVO.getPassword());
		profileObj.setPropertyValue(getProfilePropertyManager().getPasswordPropertyName(), encrypted);
	}

	/**
	 * @return the loginURL
	 */
	public String getLoginURL() {
		return loginURL;
	}

	/**
	 * @param loginURL the loginURL to set
	 */
	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}

	/**
	 * @return the accountLoginURL
	 */
	public String getAccountLoginURL() {
		return accountLoginURL;
	}

	/**
	 * @param accountLoginURL the accountLoginURL to set
	 */
	public void setAccountLoginURL(String accountLoginURL) {
		this.accountLoginURL = accountLoginURL;
	}

	/**
	 * @return the contextPath
	 */
	public String getStoreContextPath() {
		return storeContextPath;
	}

	/**
	 * @param contextPath the contextPath to set
	 */
	public void setStoreContextPath(String storeContextPath) {
		this.storeContextPath = storeContextPath;
	}

	

}
