package com.bbb.account;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.vo.AssignOffersRespVo;
import com.bbb.account.vo.CreateWalletRespVo;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.userprofiling.BBBCookieManager;
import com.bbb.utils.BBBUtility;



public class WalletFormHandler extends BBBGenericFormHandler {
	
	private String couponCode;
	private String addCouponSuccessURL;
	private String addCouponErrorURL;
	private String regSuccessURL;
	private String regErrorURL;
	private String email;
	private  BBBGetCouponsManager  couponsManager;
	private BBBCatalogToolsImpl catalogTools;
	private Map<String, String> mErrorMap;
	private LblTxtTemplateManager mLblTxtTemplateManager;
	private BBBPropertyManager propertyManager;
	private BBBProfileTools mProfileTools;
	private String lookupCouponSuccessURL;
	private String lookupCouponErrorURL;
	private String successCouponid;
	private String walletID;
	private String lookupWalletID;
	private BBBProfileManager profileManager;
	private Profile profile;	 
	private boolean createWalletID = false;
	private Map<String, String> errorMap;
	private BBBCookieManager mCookieManager;
	private String fromPage;// Page Name set from JSP
	private Map<String,String> successUrlMap;
	private String successUrlCreateProfile;
	/**
	 * @return the fromPage
	 */
	public String getFromPage() {
		return fromPage;
	}

	/**
	 * @param fromPage the fromPage to set
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	/**
	 * @return the successUrlMap
	 */
	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	/**
	 * @param successUrlMap the successUrlMap to set
	 */
	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}

	/**
	 * @return the errorUrlMap
	 */
	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	/**
	 * @param errorUrlMap the errorUrlMap to set
	 */
	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}


	private Map<String,String> errorUrlMap;
	
	public BBBCookieManager getCookieManager() {
		return mCookieManager;
	}

	public void setCookieManager(BBBCookieManager pCookieManager) {
		this.mCookieManager = pCookieManager;
	}
	public Map<String, String> getmErrorMap() {
		return mErrorMap;
	}

	public void setmErrorMap(Map<String, String> mErrorMap) {
		this.mErrorMap = mErrorMap;
	}


	public boolean getCreateWalletID() {
		return createWalletID;
	}

	public void setCreateWalletID(boolean createWalletID) {
		this.createWalletID = createWalletID;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public BBBProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(BBBProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public BBBPropertyManager getPropertyManager() {
		
		return propertyManager;
	}

	public void setPropertyManager(BBBPropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	/** Constructor of WalletFormHandler. */
    public WalletFormHandler() {
        this.mErrorMap = new HashMap<String, String>();      
    }
	
	public String getRegSuccessURL() {
		return regSuccessURL;
	}
	public void setRegSuccessURL(String regSuccessURL) {
		this.regSuccessURL = regSuccessURL;
	}
	public String getRegErrorURL() {
		return regErrorURL;
	}
	public void setRegErrorURL(String regErrorURL) {
		this.regErrorURL = regErrorURL;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public BBBGetCouponsManager getCouponsManager() {
		return couponsManager;
	}
	public void setCouponsManager(BBBGetCouponsManager couponsManager) {
		this.couponsManager = couponsManager;
	}
	public BBBCatalogToolsImpl getCatalogTools() {
		return catalogTools;
	}
	public void setCatalogTools(BBBCatalogToolsImpl catalogTools) {
		this.catalogTools = catalogTools;
	}	
	
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public String getAddCouponSuccessURL() {
		return addCouponSuccessURL;
	}
	public void setAddCouponSuccessURL(String addCouponSuccessURL) {
		this.addCouponSuccessURL = addCouponSuccessURL;
	}
	public String getAddCouponErrorURL() {
		return addCouponErrorURL;
	}
	public void setAddCouponErrorURL(String addCouponErrorURL) {
		this.addCouponErrorURL = addCouponErrorURL;
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
	 * @return the lookupCouponSuccessURL
	 */
	public String getLookupCouponSuccessURL() {
		return lookupCouponSuccessURL;
	}
    
	/**
	 * @param lookupCouponSuccessURL the lookupCouponSuccessURL to set
	 */
	public void setLookupCouponSuccessURL(String lookupCouponSuccessURL) {
		this.lookupCouponSuccessURL = lookupCouponSuccessURL;
	}
	
	/**
	 * @return the lookupCouponSuccessURL
	 */
	public String getLookupCouponErrorURL() {
		return lookupCouponErrorURL;
	}
    
	/**
	 * @param lookupCouponErrorURL the lookupCouponErrorURL to set
	 */
	public void setLookupCouponErrorURL(String lookupCouponErrorURL) {
		this.lookupCouponErrorURL = lookupCouponErrorURL;
	}
	
	
	public String getSuccessCouponid() {
		return successCouponid;
	}

	public void setSuccessCouponid(String successCouponid) {
		this.successCouponid = successCouponid;
	}
	
	public String getWalletID() {
		return walletID;
	}

	public void setWalletID(String walletID) {
		this.walletID = walletID;
	}
	
	public String getLookupWalletID() {
		return lookupWalletID;
	}

	public void setLookupWalletID(String lookupWalletID) {
		this.lookupWalletID = lookupWalletID;
	}
	
	
	@SuppressWarnings("unchecked")
public boolean handleCreateWallet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		final String emailAddr = getEmail();
		String mobilePhone = null;
		String firstName =null;
		String lastName= null;
		String address =null;
		String city = null;
		String state = null;
		String postalCode = null;
		String wallet_id = null;
		
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
        sessionBean.setCouponEmail(null);
        this.errorMap = new HashMap<String, String>();
        String siteId = SiteContextManager.getCurrentSiteId();
		if (!BBBUtility.siteIsTbs(siteId) && StringUtils.isNotEmpty(getFromPage())) {
			setRegSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setRegErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));

		}	
		try {
			
			Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
			
			
			// e-mail validation for coupon wallet registration page
			if (BBBUtility.isEmpty(emailAddr)) {
				String emptyEmailError = getLblTxtTemplateManager().getErrMsg(BBBCoreConstants.ERR_COUPON_EMAIL_EMPTY,	pRequest.getLocale().getLanguage(), errorPlaceHolderMap,null);
				emptyEmailError = "Please enter the email address";
				this.errorMap.put(BBBCoreConstants.EMAIL_ERROR, emptyEmailError);
	            this.addFormException(new DropletException(emptyEmailError,BBBCoreErrorConstants.COUPON_WALLET_ERROR_1000));
			} else if (!BBBUtility.isValidEmail(emailAddr)) {
				String emailFormatError = getLblTxtTemplateManager().getErrMsg(BBBCoreConstants.ERR_COUPON_EMAIL_INVALID,pRequest.getLocale().getLanguage(),errorPlaceHolderMap, null);
				this.errorMap.put(BBBCoreConstants.EMAIL_ERROR, emailFormatError);
	            this.addFormException(new DropletException(emailFormatError,BBBCoreErrorConstants.COUPON_WALLET_ERROR_1001));
			} else {				

				RepositoryItem bbbUserProfile = getProfileTools().getItemFromEmail(emailAddr.toLowerCase());				
				if(null!=bbbUserProfile){					
					this.getCookieManager().createCookiesForProfile(bbbUserProfile.getRepositoryId(), pRequest, pResponse);
					wallet_id =(String) bbbUserProfile.getPropertyValue("walletId");	
					if(!BBBUtility.isEmpty(wallet_id)){
						// Setting welcome message flag to false as user and wallet id both exist
						sessionBean.setCouponsWelcomeMsg(false);
						logDebug("Case 1 : Profile exists for the given emailID");
						logDebug("And WalletID also exists:"+ wallet_id);													

					}
					if(BBBUtility.isEmpty(wallet_id)){	

						logDebug("Case 2 : Profile exists for the given emailID");
						logDebug("But WalletID does not exist...");

						CreateWalletRespVo createWalletRespVo = couponsManager.createWallet(emailAddr.toLowerCase(), mobilePhone, firstName, lastName, address, city, state, postalCode);
						wallet_id=getWalletID(createWalletRespVo);
						logDebug("So walletID from webservice call\t:"+wallet_id);
						if(!StringUtils.isEmpty(wallet_id) ){						
							getProfileManager().addWalletIdToProfile(bbbUserProfile.getRepositoryId(), wallet_id);										
							this.setCreateWalletID(true);
						}

					}
				}else{
					logDebug("Case 3: Profile does not exists. Let's create a Shallow Profile...");
					CreateWalletRespVo createWalletRespVo = couponsManager.createWallet(emailAddr.toLowerCase(), mobilePhone, firstName, lastName, address, city, state, postalCode);					
					wallet_id=getWalletID(createWalletRespVo);					
					logDebug("WalletID from webservice call\t:"+wallet_id);				
					if(!StringUtils.isEmpty(wallet_id) ){
						RepositoryItem item= getProfileManager().createShallowProfile(emailAddr.toLowerCase(),wallet_id,BBBCoreConstants.PROFILE_SOURCE_TYPE);								
						this.setCreateWalletID(true);
						if(null==item){
							logDebug("Shallow profile could not be created for this emailID: " + emailAddr + " due to repository errors!!! ");
						} else {
							this.getCookieManager().createCookiesForProfile(item.getRepositoryId(), pRequest, pResponse);
						}					

					}				
				}
			}
			sessionBean.setCouponEmail(emailAddr);		
		
			}catch (BBBSystemException e) {			
				this.logError(LogMessageFormatter.formatMessage(pRequest,"BBBSystemException from handleCreateWallet of WalletFormHandler",BBBCoreErrorConstants.COUPON_WALLET_ERROR_1002),e);			
			} catch (BBBBusinessException e) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,"BBBBusinessException from handleCreateWallet of WalletFormHandler",BBBCoreErrorConstants.COUPON_WALLET_ERROR_1003),e);
		}catch (ServletException | IOException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,"BBBBusinessException from handleCreateWallet of WalletFormHandler",BBBCoreErrorConstants.COUPON_WALLET_ERROR_1003),e);
			}		
		return this.checkFormRedirect(this.getRegSuccessURL(), this.getRegErrorURL(), pRequest, pResponse);
	}
	
	

	@SuppressWarnings("unchecked")
	public boolean handleAddCoupon(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	throws ServletException, IOException {
		
		String validCouponCd = getCouponCode();	
		String walletId = getWalletID();
		String pOSCouponId =null;
		String uniqueCouponCd= null;
		String onlineOfferCd= null;
				
		try {
			
			this.errorMap = new HashMap<String, String>();									
			validCouponCd = validCouponCd.replaceAll("[-\\s]","").trim();
			Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
						
			if (BBBUtility.isEmpty(validCouponCd)) {
				
				String emptyCouponCodeError = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERR_COUPON_CODE_EMPTY,
						pRequest.getLocale().getLanguage(), errorPlaceHolderMap,
						null);
				this.errorMap.put(BBBCoreConstants.COUPON_WALLET_ERROR, emptyCouponCodeError);
	            this.addFormException(new DropletException(emptyCouponCodeError,BBBCoreErrorConstants.COUPON_WALLET_ERROR_1004));
			} if (!BBBUtility.isValidCouponCode(validCouponCd)) {
					
				String emptyCouponCodeError = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERR_COUPON_CODE_INVALID,
						pRequest.getLocale().getLanguage(), errorPlaceHolderMap,
						null);
				this.errorMap.put(BBBCoreConstants.COUPON_WALLET_ERROR, emptyCouponCodeError);
	            this.addFormException(new DropletException(emptyCouponCodeError,BBBCoreErrorConstants.COUPON_WALLET_ERROR_1005));			
			} else{
				
				if(validCouponCd.length()==12){
					onlineOfferCd = validCouponCd;
				}else{
					uniqueCouponCd = validCouponCd;
				}
				
				AssignOffersRespVo assignOffersRespVo = couponsManager.assignOffers(onlineOfferCd, pOSCouponId, uniqueCouponCd, walletId);
				if(assignOffersRespVo.getmErrorStatus().isErrorExists()){
					ErrorStatus errStatus = assignOffersRespVo.getmErrorStatus();
					Map errorMap = new HashMap<String, String>();
					if(errStatus.getErrorId()>0){
						String errorMessage =couponsManager.getWSMappedErrorMessage(errStatus.getErrorId());
						logError(LogMessageFormatter.formatMessage(null, "WalletFormHandler::handleAssignOffers : Technical Error received while fetching createWallet System \n Error returned from service : " + errStatus.getDisplayMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
						this.addFormException(new DropletException(errorMessage,"AssignOffersError"));
		                this.errorMap.put(BBBCoreConstants.COUPON_WALLET_ERROR, errorMessage);			             
					
					}else if(!BBBUtility.isEmpty(errStatus.getDisplayMessage()))//Business error from webservice
					{
						logError(LogMessageFormatter.formatMessage(null, "WalletFormHandler::handleAssignOffers : Technical Error received while fetching createWallet System \n Error returned from service : " + errStatus.getDisplayMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
						this.addFormException(new DropletException(errStatus.getDisplayMessage(),"AssignOffersError"));
		                this.errorMap.put(BBBCoreConstants.COUPON_WALLET_ERROR, errStatus.getDisplayMessage());			             
					}else if(BBBUtility.isEmpty(errStatus.getDisplayMessage()) && !BBBUtility.isEmpty(errStatus.getErrorMessage()))//Technical Error
					{
						logError(LogMessageFormatter.formatMessage(null, "WalletFormHandler::handleAssignOffers : Technical Error received while fetching createWallet System \n Error returned from service : " + errStatus.getErrorMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
						this.addFormException(new DropletException(errStatus.getErrorMessage(),"assignOffersError"));
		                this.errorMap.put(BBBCoreConstants.COUPON_WALLET_ERROR, errStatus.getErrorMessage());
					}
					else{
						logError(LogMessageFormatter.formatMessage(null, "WalletFormHandler::handleAssignOffers : Technical Error received while fetching createWallet System \n Error returned from service : " + errStatus.getDisplayMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
						this.addFormException(new DropletException("Assign Offer webservice failed.", "AssignOffersError"));
		                this.errorMap.put(BBBCoreConstants.COUPON_WALLET_ERROR, "Assign Offer webservice failed.");
					}
				} else{
					this.setSuccessCouponid(assignOffersRespVo.getUniqueCouponCd());
				}
	    }
			
		}catch (BBBSystemException e) {			
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBSystemException from handleAddCoupon of WalletFormHandler",
									BBBCoreErrorConstants.COUPON_WALLET_ERROR_1002),
					e);			
		} catch (BBBBusinessException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleAddCoupon of WalletFormHandler",
									BBBCoreErrorConstants.COUPON_WALLET_ERROR_1003),
					e);
		}
		
		
		return this.checkFormRedirect(this.getAddCouponSuccessURL(), this.getAddCouponErrorURL(), pRequest,
                pResponse);
	}

	@SuppressWarnings("unchecked")
	public boolean handleLookupWallet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	throws ServletException, IOException {
		
        BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		sessionBean.setCouponEmail(null);
		sessionBean.setCouponsWelcomeMsg(true);
		// email address of the user
		final String emailAddr = getEmail();						
		try {
			String siteId = SiteContextManager.getCurrentSiteId();
			if (!BBBUtility.siteIsTbs(siteId) && StringUtils.isNotEmpty(getFromPage())) {
				setLookupCouponSuccessURL(pRequest.getContextPath()
						+ getSuccessUrlMap().get(getFromPage()));
				setLookupCouponErrorURL(pRequest.getContextPath()
						+ getErrorUrlMap().get(getFromPage()));

			}	
			Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
			
			// e-mail validations
			if (BBBUtility.isEmpty(emailAddr)) {
				String emptyEmailError = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERR_COUPON_EMAIL_EMPTY,
						pRequest.getLocale().getLanguage(), errorPlaceHolderMap,
						null);				
				addFormException(new DropletException(emptyEmailError,BBBCoreErrorConstants.COUPON_WALLET_ERROR_1000));
				mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, emptyEmailError);
			} else if (!BBBUtility.isValidEmail(emailAddr)) {
				String emailFormatError = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERR_COUPON_EMAIL_INVALID,
						pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);
				addFormException(new DropletException(emailFormatError,BBBCoreErrorConstants.COUPON_WALLET_ERROR_1001));
				mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, emailFormatError);
			} else {

				// obtain the profile from email
				RepositoryItem bbbUserProfile = getProfileTools()
						.getItemFromEmail(emailAddr.toLowerCase());
				
				if (bbbUserProfile != null) {
					this.getCookieManager().createCookiesForProfile(bbbUserProfile.getRepositoryId(), pRequest, pResponse);
					// obtain the walletid of the profile
					String wallet_id = (String) bbbUserProfile
							.getPropertyValue("walletId");
					
					if (!BBBUtility.isEmpty(wallet_id)) {
						this.setLookupWalletID(wallet_id);
						sessionBean.setCouponsWelcomeMsg(false);
					} else {

						wallet_id=this.getCouponsManager().createWalletIdFromEmail(bbbUserProfile, wallet_id, emailAddr, false);
						if(!StringUtils.isBlank(wallet_id)){
							logDebug("Retrieved wallet Id : " + wallet_id);
							this.setLookupWalletID(wallet_id);
							// Adding session variable to show welcome message for first time.
							sessionBean.setCouponsWelcomeMsg(true);
						}
						
					}
				}
				sessionBean.setCouponEmail(emailAddr);
			}			
		}catch (Exception e) {			
			this.logError(e.getMessage(),e);	
		} 		
		return this.checkFormRedirect(this.getLookupCouponSuccessURL(), this.getLookupCouponErrorURL(), pRequest,
                pResponse);
	}
	

	/**
	 * <p>
	 * dkhadka:
	 * This WalletFormHandler.getWalletId(...) method returns an unique walletId  from the web service call.
	 * </p>
	 * @param createWalletRespVo of type <b>CreateWalletRespVo</b>
	 * @return walletId of type <b>String</b>
	 */
	private String getWalletID(CreateWalletRespVo createWalletRespVo){		
		return isErrorsExistInResponse(createWalletRespVo) ?null :createWalletRespVo.getWalletId();
	}
	
	/**
	 * <p>
	 * dkhadka:
	 * This WalletFormHandler.isErrorsExistsInResponse(...) method returns true if web service call is unsuccessful.
	 * </p>
	 * @param createWalletRespVo:CreateWalletRespVo
	 * @return <b>true</b> or <b>false</b>
	 */
	private boolean isErrorsExistInResponse(CreateWalletRespVo createWalletRespVo){ 	
		logDebug("Processing CreateWalletRespVo and constructing error message in case of failure...");
		boolean foundErrors=false;
		if(null==createWalletRespVo)
			foundErrors= true;		
		if(null!=createWalletRespVo && null!=createWalletRespVo.getmErrorStatus() && createWalletRespVo.getmErrorStatus().isErrorExists()){  			
			ErrorStatus errStatus = createWalletRespVo.getmErrorStatus();		
			if(errStatus.getErrorId()>0){
				String errorMessage =couponsManager.getWSMappedErrorMessage(errStatus.getErrorId());
				logError(LogMessageFormatter.formatMessage(null, "WalletFormHandler::handleCreateWallet : Technical Error received while fetching createWallet System \n Error returned from service : " + errStatus.getDisplayMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
				this.addFormException(new DropletException(errorMessage,"CreateWalletError"));
                this.errorMap.put(BBBCoreConstants.COUPON_WALLET_ERROR, errorMessage);			             			
			}else{
				logError(LogMessageFormatter.formatMessage(null, "WalletFormHandler::handleCreateWallet: Business Error received while fetching createWallet System \n Error returned from service : " + errStatus.getDisplayMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
				this.addFormException(new DropletException("CreateWalletError", "Create Wallet webservice failed."));
				mErrorMap.put("CreateWalletError", "Create Wallet webservice failed.");
			}
			foundErrors= true;
		}	
		logDebug("JAX_WS API for createWallet Failure?:"+foundErrors);
		return foundErrors;
	}
	
	
	public final void handleLoginToManageActiveCouponWallet(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	
		String siteId = SiteContextManager.getCurrentSiteId();
		if (!BBBUtility.siteIsTbs(siteId) && StringUtils.isNotEmpty(getFromPage())) {
			setRegSuccessURL(pRequest.getContextPath()
					+ this.getSuccessUrlCreateProfile());
			setRegErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));

		}
		
		this.logDebug(" handleLoginToManageActiveCouponWallet(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		

		//this.getSessionBean().setMngActRegistry(BBBGiftRegistryConstants.LOGIN_TO_MANAGE_ACTIVE_REGISTRY);
		final MutableRepositoryItem userProfile = (MutableRepositoryItem) ServletUtil
				.getCurrentUserProfile();
		final String loginFrom = BBBWebServiceConstants.TXT_LOGIN_COUPONWALLET;

		userProfile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM,
				loginFrom);

		this.checkFormRedirect(this.regSuccessURL, this.regErrorURL, pRequest,
				pResponse);

		
		this.logDebug("handleLoginToManageActiveCouponWallet(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
		

	}

	public String getSuccessUrlCreateProfile() {
		return successUrlCreateProfile;
	}

	public void setSuccessUrlCreateProfile(String successUrlCreateProfile) {
		this.successUrlCreateProfile = successUrlCreateProfile;
	}
	
}