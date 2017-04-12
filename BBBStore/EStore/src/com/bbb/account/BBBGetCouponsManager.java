/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 04-December-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.account;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.OrderHolder;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.StringUtils;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.account.vo.AssignOffersRequestVo;
import com.bbb.account.vo.AssignOffersRespVo;
import com.bbb.account.vo.CouponListVo;
import com.bbb.account.vo.CouponRequestVo;
import com.bbb.account.vo.CouponResponseVo;
import com.bbb.account.vo.CreateWalletReqVo;
import com.bbb.account.vo.CreateWalletRespVo;
import com.bbb.account.vo.UpdateWalletProfileReqVo;
import com.bbb.account.vo.UpdateWalletProfileRespVo;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.checkout.util.BBBCouponUtil;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.manager.PromotionLookupManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.selfservice.vo.SchoolVO;
import com.bbb.userprofiling.BBBCookieManager;
import com.bbb.utils.BBBUtility;

public class BBBGetCouponsManager extends BBBGenericService {
	
	private String getCouponsServiceName;
	private String createWalletServiceName;
	private String assignOffersServiceName;
	private String updateWalletProfileServiceName;
	private BBBCatalogTools mCatalogTools;
	private String mediaItmKey;
	private BBBCouponUtil mCouponUtil;
	private PromotionTools promotionTools;
	private ProfileTools profileTools;
	private BBBProfileManager profileManager;
	private SiteContext siteContext;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBCookieManager mCookieManager;
	private BBBPromotionTools promTools;
	public BBBCookieManager getCookieManager() {
		return mCookieManager;
	}

	public void setCookieManager(BBBCookieManager pCookieManager) {
		this.mCookieManager = pCookieManager;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	public String getUpdateWalletProfileServiceName() {
		return updateWalletProfileServiceName;
	}

	public void setUpdateWalletProfileServiceName(
			String updateWalletProfileServiceName) {
		this.updateWalletProfileServiceName = updateWalletProfileServiceName;
	}
	
	public BBBProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(BBBProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public ProfileTools getProfileTools() {
		return profileTools;
	}

	public void setProfileTools(ProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	public String getGetCouponsServiceName() {
		return getCouponsServiceName;
	}

	public void setGetCouponsServiceName(String getCouponsServiceName) {
		this.getCouponsServiceName = getCouponsServiceName;
	}

	public String getCreateWalletServiceName() {
		return createWalletServiceName;
	}

	public void setCreateWalletServiceName(String createWalletServiceName) {
		this.createWalletServiceName = createWalletServiceName;
	}

	public String getAssignOffersServiceName() {
		return assignOffersServiceName;
	}

	public void setAssignOffersServiceName(String assignOffersServiceName) {
		this.assignOffersServiceName = assignOffersServiceName;
	}

	
	
	 /** @return Promotion Tools */
    public final PromotionTools getPromotionTools() {
        return this.promotionTools;
    }

    /** @param promotionTools Promotion Tools */
    public final void setPromotionTools(final PromotionTools promotionTools) {
        this.promotionTools = promotionTools;
    }
	public BBBCouponUtil getCouponUtil() {
		return this.mCouponUtil;
	}
	public void setCouponUtil(BBBCouponUtil pCouponUtil) {
		this.mCouponUtil = pCouponUtil;
	}
	/**
	 * @return the mediaItmKey
	 */
	public String getMediaItmKey() {
		return this.mediaItmKey;
	}

	/**
	 * @param mediaItmKey the mediaItmKey to set
	 */
	public void setMediaItmKey(String mediaItmKey) {
		this.mediaItmKey = mediaItmKey;
	}


	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}


	/** @return Site Context */
    public final SiteContext getSiteContext() {
        return this.siteContext;
    }

    /** @param siteContext the mSiteContext to set */
    public final void setSiteContext(final SiteContext siteContext) {
        this.siteContext = siteContext;
    }



    public CouponResponseVo getCouponsList(String pEmailAddr , String pMobilePhone, String walletId, boolean showActiveOnly, boolean isFromBillingPaymentPage) throws BBBSystemException , BBBBusinessException{
		logDebug("BBBGetCouponsManager.getCouponsList() method started");

		CouponRequestVo couponRequestVo = new CouponRequestVo();

		String siteId = SiteContextManager.getCurrentSiteId();

		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).size()>BBBCoreConstants.ZERO) {
			couponRequestVo.setSiteFlag(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1268,"Site Flag for webservices is not set for siteId: " + siteId);
			}
			
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size()>BBBCoreConstants.ZERO) {
			couponRequestVo.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1269,"Webservices Token for webservices is not set");
			}

		couponRequestVo.setEmailAddr(pEmailAddr);
		couponRequestVo.setMobilePhone(pMobilePhone);
		couponRequestVo.setWalletId(walletId);
		if(showActiveOnly)
		couponRequestVo.setShowActiveOnly(String.valueOf(1));
		else
			couponRequestVo.setShowActiveOnly(String.valueOf(0));
		couponRequestVo.setServiceName(getGetCouponsServiceName());


		CouponResponseVo couponResponseVo = null;

		if(getCatalogTools().isLogCouponDetails() && isFromBillingPaymentPage){
			if(null != couponRequestVo){
				CouponRequestVo logCouponRequestVo = couponRequestVo;
				if(!BBBUtility.isEmpty(couponRequestVo.getMobilePhone())) logCouponRequestVo.setMobilePhone(couponRequestVo.getMobilePhone());
				if(!BBBUtility.isEmpty(couponRequestVo.getEmailAddr())) logCouponRequestVo.setEmailAddr(couponRequestVo.getEmailAddr());
				logInfo("BBBGetCouponsManager.getCouponsList() Get Coupons Request :: " + logCouponRequestVo.toString());
			}
		}
		long startTime = System.currentTimeMillis();
		couponResponseVo = (CouponResponseVo) ServiceHandlerUtil.invoke(couponRequestVo);
		long endTime = System.currentTimeMillis();
		logInfo("Total time taken in calling Coupon Service : " + (endTime-startTime) + " milliseconds");
		
		if(null == couponResponseVo){
			ErrorStatus errorStatus = new ErrorStatus();
			errorStatus.setErrorExists(true);
			errorStatus.setErrorId(-1);
			couponResponseVo = new CouponResponseVo();
			couponResponseVo.setErrorStatus(errorStatus);
		}
		
		if(getCatalogTools().isLogCouponDetails() && isFromBillingPaymentPage){
			logInfo("BBBGetCouponsManager.getCouponsList() Get Coupons Response :: " + couponResponseVo.toString());
		}
		
		logDebug("BBBGetCouponsManager.getCouponsList() method end");

		return couponResponseVo;
	}
	
	

	
	/**
	 * Add to a method to call createWallet webservice
	 */
	public CreateWalletRespVo createWallet(String emailAddr,String mobilePhone,String firstName,String lastName,String address,String city,String state,String postalCode) throws BBBSystemException , BBBBusinessException{

		logDebug("BBBGetCouponsManager.getCouponsList() method started");

		CreateWalletReqVo createWalletReqVo = new CreateWalletReqVo();

		String siteId = SiteContextManager.getCurrentSiteId();

		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).size()>BBBCoreConstants.ZERO) {
			createWalletReqVo.setSiteFlag(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1268,"Site Flag for webservices is not set for siteId: " + siteId);
			}
			
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size()>BBBCoreConstants.ZERO) {
			createWalletReqVo.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1269,"Webservices Token for webservices is not set");
			}

		createWalletReqVo.setEmailAddr(emailAddr);
		createWalletReqVo.setMobilePhone(mobilePhone);
		createWalletReqVo.setFirstName(firstName);
		createWalletReqVo.setLastName(lastName);
		createWalletReqVo.setCity(city);
		createWalletReqVo.setState(state);
		createWalletReqVo.setPostalCode(postalCode);
		createWalletReqVo.setServiceName(getCreateWalletServiceName());
		
		CreateWalletRespVo createWalletRespVo = null;
					
		createWalletRespVo = (CreateWalletRespVo) ServiceHandlerUtil.invoke(createWalletReqVo);
		
		if(null == createWalletRespVo){
			ErrorStatus errorStatus = new ErrorStatus();
			errorStatus.setErrorExists(true);
			errorStatus.setErrorId(619);
			createWalletRespVo = new CreateWalletRespVo();
			createWalletRespVo.setmErrorStatus(errorStatus);
		}
		logDebug("BBBGetCouponsManager.getCouponsList() method end");

		return createWalletRespVo;
	}
	
	public CreateWalletRespVo createWalletMobile(String emailAddr,String mobilePhone,String firstName,String lastName,String address,String city,String state,String postalCode) throws BBBSystemException , BBBBusinessException{		
		
		long beforeCreateWallet = System.currentTimeMillis();
		String wallet_id = null;
		CreateWalletRespVo createWalletRespVo=null;
		RepositoryItem bbbUserProfile = getProfileTools().getItemFromEmail(emailAddr.toLowerCase());				
		try {
			if(null!=bbbUserProfile){
				//BBBP-5384 - create user cookie for existing profile 
				this.getCookieManager().createCookiesForProfile(bbbUserProfile.getRepositoryId(), ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());
				
				wallet_id =(String) bbbUserProfile.getPropertyValue(BBBCoreConstants.WALLETID);	
				if(BBBUtility.isEmpty(wallet_id)){	
					logDebug("Case 2 : Profile exists for the given emailID");
					logDebug("But WalletID does not exist...");
					
					createWalletRespVo = createWallet(emailAddr.toLowerCase(), mobilePhone, firstName, lastName, address, city, state, postalCode);
					if(createWalletRespVo != null && null!=createWalletRespVo.getmErrorStatus() && !createWalletRespVo.getmErrorStatus().isErrorExists()){
					wallet_id=createWalletRespVo.getWalletId();
					logDebug("So walletID from webservice call\t:"+wallet_id);
					if(!StringUtils.isEmpty(wallet_id) )						
						getProfileManager().addWalletIdToProfile(bbbUserProfile.getRepositoryId(), wallet_id);	
					}
				}
			}else{
				logDebug("Case 3: Profile does not exists. Let's create a Shallow Profile...");
				createWalletRespVo = createWallet(emailAddr.toLowerCase(), mobilePhone, firstName, lastName, address, city, state, postalCode);	
				logDebug("BBBGetCouponsManager.createWalletMobile createWalletRespVo." + createWalletRespVo);
				//We are going to create shallow profile even if wallet service failed, as per Manpreet comments
				if(null!=createWalletRespVo.getmErrorStatus() && !createWalletRespVo.getmErrorStatus().isErrorExists()){			
					wallet_id=createWalletRespVo.getWalletId();					
					logDebug("WalletID from webservice call\t:"+wallet_id);
				
					//BBB-26 | Wallet Creation Source not being stored for Guest checkout - BEGINS
					RepositoryItem item = null;
					DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
					// When it is from mobile and the service name is "createWallet", the source is set as "couponWallet". createWallet is called only from the walletRegistration page in mobile.
					if((BBBCoreConstants.MOBILEWEB.equals(BBBUtility.getChannel()) || BBBCoreConstants.MOBILEAPP.equals(BBBUtility.getChannel())) && (pRequest != null && pRequest.getRequestURI().contains(BBBCoreConstants.CREATE_WALLET_MOBILE))){
					
						item= getProfileManager().createShallowProfile(emailAddr.toLowerCase(),wallet_id,BBBCoreConstants.PROFILE_SOURCE_TYPE);							
					}
					else{
						
						item= getProfileManager().createShallowProfile(emailAddr.toLowerCase(),wallet_id,BBBCoreConstants.GUEST_CHECKOUT);
					}
					//BBB-26 | Wallet Creation Source not being stored for Guest checkout - ENDS
					
					if(null == item){
						logDebug("Shallow profile could not be created for this emailID: " + emailAddr + " due to repository errors!!! ");
					} else{
						//BBBP-5384 - create user cookie for shallow profile
						this.getCookieManager().createCookiesForProfile(item.getRepositoryId(), ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());
					}	
				}		
			}
		} catch (ServletException | IOException e) {
			throw new BBBSystemException(BBBWebServiceConstants.SYSTEM_EXCEPTION_CODE,"error in Creating cookie for profile",e);
		}
		logDebug("Total time taken to execute createWalletMobile method " + (System.currentTimeMillis() - beforeCreateWallet));
		return createWalletRespVo;
	}
	
	
	
	/**
	 * method to call assignOffers webservice
	 */
	public AssignOffersRespVo assignOffers(String onlineOfferCd, String pOSCouponId, String uniqueCouponCd, String walletId) throws BBBSystemException , BBBBusinessException{

		logDebug("BBBGetCouponsManager.assignOffers() method started");

		AssignOffersRequestVo assignOffersRequestVo = new AssignOffersRequestVo();

		String siteId = SiteContextManager.getCurrentSiteId();

		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).size()>BBBCoreConstants.ZERO) {
			assignOffersRequestVo.setSiteFlag(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1268,"Site Flag for webservices is not set for siteId: " + siteId);
			}
			
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size()>BBBCoreConstants.ZERO) {
			assignOffersRequestVo.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1269,"Webservices Token for webservices is not set");
			}
		
		assignOffersRequestVo.setOnlineOfferCd(onlineOfferCd);
		assignOffersRequestVo.setpOSCouponId(pOSCouponId);
		assignOffersRequestVo.setUniqueCouponCd(uniqueCouponCd);
		assignOffersRequestVo.setWalletId(walletId);
		assignOffersRequestVo.setServiceName(getAssignOffersServiceName());
		
		AssignOffersRespVo assignOffersRespVo = null;
		
		assignOffersRespVo = (AssignOffersRespVo) ServiceHandlerUtil.invoke(assignOffersRequestVo);
		
		if(null == assignOffersRespVo){
			ErrorStatus errorStatus = new ErrorStatus();
			errorStatus.setErrorExists(true);
			errorStatus.setErrorId(619);
			assignOffersRespVo = new AssignOffersRespVo();
			assignOffersRespVo.setmErrorStatus(errorStatus);
		}else{
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			pRequest.getSession().setAttribute("couponMailId", BBBCoreConstants.BLANK);
		}
		
		logDebug("BBBGetCouponsManager.assignOffers() method end");

		return assignOffersRespVo;
	}
	
	/**
	 * method to call updateWalletProfile webservice
	 */
	public UpdateWalletProfileRespVo updateWalletProfile(String email, String firstName, String lastName, String mobilePhone, String walletId) throws BBBSystemException , BBBBusinessException{

		logDebug("BBBGetCouponsManager.updateWalletProfile() method started");

		UpdateWalletProfileReqVo updateWalletProfileReqVo = new UpdateWalletProfileReqVo();

		String siteId = SiteContextManager.getCurrentSiteId();

		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).size()>BBBCoreConstants.ZERO) {
			updateWalletProfileReqVo.setSiteFlag(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1268,"Site Flag for webservices is not set for siteId: " + siteId);
			}
			
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size()>BBBCoreConstants.ZERO) {
			updateWalletProfileReqVo.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1269,"Webservices Token for webservices is not set");
			}
		
		updateWalletProfileReqVo.setWalletId(walletId);
		updateWalletProfileReqVo.setEmailAddr(email);
		updateWalletProfileReqVo.setFirstName(firstName);
		updateWalletProfileReqVo.setLastName(lastName);
		updateWalletProfileReqVo.setMobilePhone(mobilePhone);
		updateWalletProfileReqVo.setServiceName(getUpdateWalletProfileServiceName());
		
		UpdateWalletProfileRespVo updateWalletProfileRespVo = null;

		updateWalletProfileRespVo = (UpdateWalletProfileRespVo) ServiceHandlerUtil.invoke(updateWalletProfileReqVo);

		logDebug("BBBGetCouponsManager.updateWalletProfile() method end");

		return updateWalletProfileRespVo;
	}
	/**
	 * This method calls the Droplet GetCouponsDroplet to get the list of all
	 * coupons associated with user email and phone no 
	 * The code is to be used for Rest service 
	 * @param emailAddr
	 * @param mobilePhone
	 * @return
	 * @throws BBBSystemException
	 */

	@SuppressWarnings("unchecked")
	public Map<String, List<CouponListVo>> getUserCoupons(String emailAddr,	String mobilePhone, String walletId, boolean showOnlyActive ) throws BBBSystemException{
		DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse =ServletUtil.getCurrentResponse();
		pRequest.setParameter(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME, emailAddr);
		pRequest.setParameter(BBBCoreConstants.MOBILE_NUMBER_PARAM_NAME, mobilePhone);
		pRequest.setParameter(BBBCoreConstants.WALLETID,walletId );
		pRequest.setParameter(BBBCoreConstants.SHOWACTIVEONLY,showOnlyActive);
		logDebug("BBBGetCouponsManager.getUserCoupons Input parameter emailAddress  "+emailAddr+" mobilePhone "+mobilePhone);
		final Map<String, List<CouponListVo>> couponsMap = new LinkedHashMap<String, List<CouponListVo>>();
		GetCouponsDroplet couponDroplet=(GetCouponsDroplet)pRequest.resolveName("/com/bbb/account/GetCouponsDroplet");
		try {
			couponDroplet.service(pRequest, pResponse);
		} catch (ServletException e) {
			throw new BBBSystemException(BBBWebServiceConstants.SYSTEM_EXCEPTION_CODE,"error in getting coupon information",e);
		} catch (IOException e) {
			throw new BBBSystemException(BBBWebServiceConstants.SYSTEM_EXCEPTION_CODE,"error in getting coupon information",e);
		}
		if( pRequest.getObjectParameter("couponsMap")!=null){
			couponsMap.putAll((LinkedHashMap<String, List<CouponListVo>>)pRequest.getObjectParameter("couponsMap"));
		}
		if( pRequest.getObjectParameter(BBBCoreConstants.ERROR_MAP_FOR_SERVICE)!=null){
			couponsMap.put(BBBCoreConstants.ERROR_MAP_FOR_SERVICE, null);			
		}		

		return couponsMap;
	}

	/**
	 * The method to get list of coupon promotions for Rest API.
	 * the API first tries to get all valid inputs email address,mobile phone no and school id if they are already not
	 * provided in the input.
	 * email address,mobile phone no is used to get coupon information from the webservice
	 * school Id is used to get promotion coupon if available for the school
	 * If all the 3 values are null and are not retrievable from Order or profile then Exception is thrown
	 * else first promotion coupon is retrieved corresponding to school id
	 * Then web service call is made .If there is already a promotion in the list of CouponListVo
	 * and an error comes in teh web service call then no exception is thrown
	 * but if the list of CouponListVo is empty i.e. has no promotion from school and an error comes in web service call
	 * then exception is thrown
	 * @param emailAddr
	 * @param mobilePhone
	 * @param schoolId
	 * @param basedOnOrder
	 * @param basedOnProfile
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<CouponListVo> getUserCoupons(String emailAddr,	String mobilePhone,String schoolId, Boolean basedOnOrder, Boolean basedOnProfile) throws BBBSystemException, BBBBusinessException {
		logDebug("BBBGerCouponsmanager:getUserCoupons start emailAddr "+emailAddr+
				" mobilePhone "+mobilePhone +" schoolId "+schoolId+" basedOnOrder "+basedOnOrder+" basedOnProfile "+basedOnProfile);
		DynamoHttpServletRequest req=ServletUtil.getCurrentRequest();
		List<CouponListVo> couponList=new ArrayList<CouponListVo>();
		Profile profile = (Profile) req.resolveName("/atg/userprofiling/Profile");

		OrderHolder cart = (OrderHolder) req.resolveName("/atg/commerce/ShoppingCart");
		BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();

		CouponListVo couponListVo=null;
		CouponResponseVo couponResponse=null;
		//if email address ,mobile no or school id is null or empty try fetching it from profile or order as applicable
		if(StringUtils.isEmpty(emailAddr)){
			if(basedOnOrder && order!=null){
				BBBRepositoryContactInfo billingAddress = order.getBillingAddress();
				if(billingAddress != null) {
					emailAddr = billingAddress.getEmail();            
				}
			}
			else if(basedOnProfile && profile!=null){
				emailAddr = (String) profile.getPropertyValue(BBBCoreConstants.EMAIL);
			}
		}
		
		String walletId=null;
		boolean showActiveOnly =true;

		if(StringUtils.isEmpty(mobilePhone)){
			if(basedOnOrder && order!=null){
				BBBRepositoryContactInfo billingAddress = order.getBillingAddress();
				if(billingAddress != null) {
					mobilePhone = billingAddress.getPhoneNumber();
				}
			}

		}
		if(StringUtils.isEmpty(schoolId) && profile!=null){
			schoolId = (String) profile.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS);
		}
		logDebug("BBBGerCouponsmanager:getUserCoupons  input parameters from profile and order emailAddr "+emailAddr+
				" mobilePhone "+mobilePhone +" schoolId "+schoolId);
		//if all three parameters is null then exception is thrown as no valid input is present to retrieve any promotion
		if(StringUtils.isEmpty(emailAddr)&& StringUtils.isEmpty(mobilePhone)&& StringUtils.isEmpty(schoolId)) {
			logDebug( "getUserCoupons: Email not found, No promotions returned");
			throw new BBBBusinessException(BBBWebServiceConstants.ERROR_RETRIEVING_VALID_INPUT,"All inputs email-mobile-schoolid are null cannot retrieve data" );
		}

		//add school promotion if any
		if(!StringUtils.isEmpty(schoolId)){
			couponListVo=addSchoolPromotion( schoolId, profile);
		}

		if(couponListVo!=null){
			couponList.add(couponListVo);
		}
		logDebug( "getUserCoupons: Making call for getCouponsList");

		try {
			if(!StringUtils.isEmpty(emailAddr)){
				couponResponse = getCouponsList(emailAddr, mobilePhone, walletId, showActiveOnly, false);
			}
		} catch (BBBSystemException e) {
			if(couponList!=null && !couponList.isEmpty()){
				logError("error in getting coupon information from Web Service ",e);
			}
			else{
				throw new BBBSystemException(BBBWebServiceConstants.SYSTEM_EXCEPTION_CODE,"error in getting coupon information");
			}

		} catch (BBBBusinessException e) {
			if(couponList!=null && !couponList.isEmpty()){
				logError("error in getting coupon information from Web Service ",e);
			}
			else{
				throw new BBBBusinessException(BBBWebServiceConstants.WEB_SERVICE_CALL_ERROR ,"Error in web service call");
			}
		}
		logDebug("getUserCoupons: returned  from getCouponsList");

		if(couponResponse!=null && couponResponse.getErrorStatus().isErrorExists()) {
			logDebug("populateValidPromotions: couponResponse got ErrorStatus");
			if(couponList!=null && !couponList.isEmpty()){
				logError("error in getting coupon information from Web Service ");
			}
			else{
				throw new BBBSystemException(BBBWebServiceConstants.WEB_SERVICE_RESPONSE_ERROR ,"Error in the response sent by web service");
			}
		} 
		else if(couponResponse!=null && couponResponse.getCouponList()!=null && !couponResponse.getCouponList().isEmpty()){
			couponList.addAll(couponResponse.getCouponList());
		}

		if(couponList!=null && !couponList.isEmpty() ){
			for (CouponListVo couponVo : couponList) {
				String code = couponVo.getEntryCd();
				RepositoryItem[] promId=null;
				try{
					promId = mCatalogTools.getPromotions(code);
				}catch(BBBBusinessException | BBBSystemException e){
					logError(" error in getting promo id "+e.getMessage(),e);
				}
				getPromotionVO(couponVo, promId);
			}
		}
		logDebug("BBBGerCouponsmanager:getUserCoupons end");
		return couponList;
	}

	/**
	 * This method is used to get promotion information and fill in couponListVo for return entry code by web service.
	 * @param couponListVo
	 * @param promId repository item
	 * @throws BBBSystemException 
	 */
	public void getPromotionVO(CouponListVo couponListVo,
			RepositoryItem[] promId) throws BBBSystemException {		

		logDebug("BBBGetCouponsManager.getPromotionVO() method Started");

		if(promId != null && promId.length != 0 && promId[0] != null && PromotionLookupManager.validPromo(promId[0], SiteContextManager.getCurrentSiteId())) {
			fillVOObject(couponListVo, promId[0]);
		} else {
			couponListVo.setDisqualify(true);
			logDebug("Either Promotion validation fail or Promotion doen't exist for this entry code");
		}

		logDebug("BBBGetCouponsManager.getPromotionVO() method Started");

	}

	/**
	 * This method fetches the school promotion from the user profile and adds
	 * to the Map.
	 * 
	 * @param pProfile
	 * @param pPromotions
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public CouponListVo addSchoolPromotion(String schoolId,
			Profile profile) throws BBBSystemException {
		CouponListVo couponListVo=new CouponListVo();
		String logMsg = null;
		if (profile instanceof Profile) {
			logMsg = "valid Profile object(Not displaying on logs due to securioty reasons)";
		}

		logDebug("Start: method addSchoolPromotion, input parameters--> pProfile:"
				+ logMsg );

		RepositoryItem schoolPromoItem = null;
		Object schoolPromoID=null;
		if(profile!=null){

			schoolPromoID = profile.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS);
		}

		SchoolVO schoolVO = null;
		if (schoolPromoID!=null && schoolPromoID instanceof String) {
			// validating schoolID
			try {
				schoolVO = (SchoolVO) getCatalogTools().getSchoolDetailsById(
						(String) schoolId);
			} catch (BBBSystemException e) {
				logDebug("Error retrieving school data: RepositoryException ",e);
			} catch (BBBBusinessException e) {
				logDebug("Exception while retrieving SchoolVO:  "+e.getMessage(),e);
			}

			if (null!=schoolVO && schoolVO.getPromotionRepositoryItem()!=null && schoolVO.getPromotionRepositoryItem() instanceof RepositoryItem) {
				schoolPromoItem = schoolVO.getPromotionRepositoryItem();
				if (schoolPromoItem.getRepositoryId().equals((String) schoolPromoID)) {
					logDebug("schoolPromotion added to Promotion Map");
					fillVOObject( couponListVo, schoolPromoItem);
				}
			}
		}
		logDebug("End: method addSchoolPromotion");
		return couponListVo;
	}

	/**
	 * The method populates the couponListVo with properties
	 * @param couponListVo
	 * @param promo
	 * @throws BBBSystemException 
	 */

	@SuppressWarnings({ "rawtypes", "static-access" })
	private void fillVOObject(CouponListVo couponListVo , RepositoryItem promo) throws BBBSystemException {

		logDebug("BBBGetCouponsManager.fillVOObject() method Started");
		SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
		
		Map mediaMap = (Map) promo.getPropertyValue(BBBCoreConstants.MEDIA);
		if(mediaMap != null){

			RepositoryItem mediaIteam = (RepositoryItem) mediaMap.get(mediaItmKey);

			if(mediaIteam != null){
				couponListVo.setCouponsImageUrl((String) (mediaIteam.getPropertyValue(BBBCoreConstants.URL) != null ? mediaIteam.getPropertyValue(BBBCoreConstants.URL):"") );
			}

		}
		String siteId = null;
		
		if(null != ServletUtil.getCurrentRequest()) {
            siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
		}

		if (BBBUtility.isEmpty(siteId)){
            siteId = SiteContextManager.getCurrentSiteId();
		}
		couponListVo.setCouponsExclusions (this.getPromTools().fetchExclusionText(couponListVo.getEntryCd(), siteId));
		couponListVo.setCouponsDescription((String) (promo.getPropertyValue(BBBCoreConstants.DESCRIPTION) != null ? promo.getPropertyValue(BBBCoreConstants.DESCRIPTION) :""));
		
		//retrieve end usable date for promotion to show up on coupon page for logged-in user in account section 
		Object endUsableDate = promo.getPropertyValue(BBBCoreConstants.END_USABLE);
        if(endUsableDate != null && endUsableDate != BBBCoreConstants.BLANK){
        	Timestamp timestamp = ((Timestamp)endUsableDate);
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			java.util.Calendar sysDate = java.util.Calendar.getInstance();
			sysDate = sysDate.getInstance();
			calendar = calendar.getInstance();
			calendar.setTimeInMillis(timestamp.getTime());
			couponListVo.setExpiryDate(format1.format(calendar.getTime()));
			calendar.add(Calendar.HOUR, -3);
			calendar.add(Calendar.MINUTE, -1);
			couponListVo.setDisplayExpiryDate(format1.format(calendar.getTime()));
			
			int daysDiff = (int)(Math.ceil((calendar.getTimeInMillis()-sysDate.getTimeInMillis())/(float)(24*3600*1000)));
			couponListVo.setExpiryCount(daysDiff);
        }
		couponListVo.setPromoItem(promo.getRepositoryId());
		logDebug("BBBGetCouponsManager.fillVOObject() method Started");
	}
	
	
	//Adding a method for mobile to send profile walletId
	public String getWalletId(String emailAddr, String flowType) throws BBBSystemException {

		// obtain the walletid of the profile
		String walletId = null;
		String walletNew = "false";
		RepositoryItem bbbUserProfile = getProfileTools().getItemFromEmail(emailAddr.toLowerCase());
		logDebug("UserProfile retrieved is ::" + bbbUserProfile);
		try {
		if (bbbUserProfile != null) {

				this.getCookieManager().createCookiesForProfile(bbbUserProfile.getRepositoryId(), ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());

			// Check if wallet id is existing in profile
			walletId = (String) bbbUserProfile
					.getPropertyValue(BBBCoreConstants.WALLETID);
			logDebug("walletId retrieved is :: " + walletId);

			// For logged in flow
			if (flowType != null && flowType.equals("LoginFlow")) {

				return walletId;
			}

			if (StringUtils.isBlank(walletId)) {

				// Sending wallet id to be fetched from service
				walletId=createWalletIdFromEmail(bbbUserProfile, walletId, emailAddr, true);
				logDebug("walletId retrieved is :: " + walletId);
			} else {
				// append the boolean flag signifying , that wallet id is old
				// wallet id.
				walletId = walletId + "," + "false";

			}

		} else {
			logDebug("createWalletIdFromEmail Ends,no profile exists, walletId:: " + walletId);
			return walletId;
		}
		} catch (ServletException | IOException e) {
			throw new BBBSystemException("error in fetching walletId", e);
		}
		logDebug("walletId is existing at profile Level, walletId ::" + walletId);
		return walletId;
	}
	
	/**
	 * This method will create the wallet id after calling the service
	 * 1. For desktop wallet id is returned as is returned from the service
	 * 2. For mobile wallet id is returned after appending a boolean signifying if it is a new wallet 
	 * @param profile
	 * @param walletId
	 * @param emailAddress
	 * @param mobileFlow
	 * @return
	 */
	public String createWalletIdFromEmail(RepositoryItem profile, String walletId, String emailAddress,
			boolean mobileFlow) {

		logDebug("createWalletIdFromEmail Starts, emailAddress :: "
				+ emailAddress);
		String walletNew = "false";

		if (StringUtils.isBlank(walletId)) {
			try {

				logDebug("Calling Create wallet service for emailiId : "
						+ emailAddress);
				// Create the wallet id
				CreateWalletRespVo createWalletResponseVo = createWallet(
						emailAddress.toLowerCase(), null, null, null, null,
						null, null, null);

				// If no error in response, fetch the wallet id
				if (null != createWalletResponseVo.getmErrorStatus()
						&& !createWalletResponseVo.getmErrorStatus()
								.isErrorExists()) {
					walletId = createWalletResponseVo.getWalletId();
					walletNew = "true";
					logDebug("walletID from webservice call\t:" + walletId
							+ "walletNew, which is only used for mobile ::"
							+ walletNew);

				}

				// If wallet id is not empty assign it to the profile
				if (!StringUtils.isEmpty(walletId) && null!=profile) {

					logDebug("walletId is not blank, assigning it to the profile, walletId:: "
							+ walletId);
					getProfileManager().addWalletIdToProfile(
							profile.getRepositoryId(), walletId);

				}

			} catch (BBBSystemException | BBBBusinessException e) {
				logError(" error in creating walletId " + e.getMessage(),e);
			} 

		}
		

		// for mobile flow return
		if (mobileFlow && !StringUtils.isBlank(walletId)) {
			String walleIdEnhanced = walletId + "," + walletNew;
			logDebug("walltId + walletNew String :: " + walleIdEnhanced);
			logDebug("createWalletIdFromEmail Ends, wallet Id ruturn for mobile, walletId ::  "
					+ walleIdEnhanced);
			return walleIdEnhanced;
		}

		logDebug("createWalletIdFromEmail Ends, desktop flow,  walletId:: "
				+ walletId);
		return walletId;
	}
	
	
	//Adding a method for mobile to send profile status full or shallow
	/**Return Profile status for user associated with wallet it#BBBP-5042
	 * @param walletId
	 * @return
	 */
	public String getProfileStatus(String walletId){
		// obtain the walletid of the profile
		RepositoryItem bbbUserProfile = ((BBBProfileTools) profileTools).findProfileByWalletId(walletId);
		return getStatusForProfile(bbbUserProfile);
	}

	/**Return Profile status#BBBP-5042
	 * @param bbbUserProfile
	 * @return
	 */
	public String getStatusForProfile(RepositoryItem bbbUserProfile) {
		String profileType = BBBCoreConstants.NOT_AVAILABLE;	
		if(bbbUserProfile!=null){
		String status = (String) bbbUserProfile.getPropertyValue(BBBCoreConstants.STATUS);
		if((status == null )|| (status!=null && status.equalsIgnoreCase(BBBCoreConstants.FULL_PROFILE_STATUS_VALUE))){
			profileType = BBBCoreConstants.FULL_PROFILE_STATUS_VALUE;
		}else{
			profileType = BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE;
			}
		}
		return profileType;
	}
	
	//Adding new method to get BCC error messages
	
	String getWSMappedErrorMessage(int errorId){		
		String errMessage=this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_MSG", "EN", null);
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_PHONE){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_PHONE_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_EMAIL){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_EMAIL_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_OFFER_1){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_OFFER_1_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_PROFILE_EMAIL){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_PROFILE_EMAIL_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_OFFER_2){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_OFFER_2_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_OFFER_3){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_OFFER_3_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_OFFER_4){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_OFFER_4_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_OFFER_5){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_OFFER_5_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_OFFER_6){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_OFFER_6_MSG", "EN", null);
		}
		if(errorId == BBBWebServiceConstants.ERR_CODE_COUPON_WALLET_OFFER_7){
			errMessage= this.getLblTxtTemplateManager().getErrMsg("ERR_CODE_COUPON_WALLET_OFFER_7_MSG", "EN", null);
		}
		return errMessage;
	}

	public BBBPromotionTools getPromTools() {
		return promTools;
	}

	public void setPromTools(BBBPromotionTools promTools) {
		this.promTools = promTools;
	}
}
