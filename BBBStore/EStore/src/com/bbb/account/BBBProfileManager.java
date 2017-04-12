/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBProfileManager.java
 *
 *  DESCRIPTION: BBBProfileManager extends ATG OOTB GenericService
 *  			 and perform business logic related to user profile. 	
 *  HISTORY:
 *  10/14/11 Initial version
 *	13/12/11 Reclaim Legacy Account web-service response VO's and services added
 *
 */

package com.bbb.account;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;
import javax.xml.datatype.XMLGregorianCalendar;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.Site;
import atg.multisite.SiteGroup;
import atg.multisite.SiteGroupManager;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryImpl;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.api.BBBAddressAPIImpl;
import com.bbb.account.api.BBBCreditCardAPIImpl;
import com.bbb.account.verifyoldaccount.ValidateOldAccReqVO;
import com.bbb.account.verifyoldaccount.ValidateOldAccResVO;
import com.bbb.account.vo.ProfileSyncRequestVO;
import com.bbb.account.vo.ProfileSyncResponseVO;
import com.bbb.account.vo.profile.RegistrationEmailRequestVO;
import com.bbb.account.vo.reclaim.ForgetPasswordReqVO;
import com.bbb.account.vo.reclaim.ForgetPasswordResponseVO;
import com.bbb.account.vo.reclaim.GetAccountInfoReqVO;
import com.bbb.account.vo.reclaim.GetAccountInfoResponseVO;
import com.bbb.account.vo.reclaim.ReclaimAccountReqVO;
import com.bbb.account.vo.reclaim.ReclaimAccountResponseVO;
import com.bbb.account.vo.reclaim.VerifyAccountReqVO;
import com.bbb.account.vo.reclaim.VerifyAccountResponseVO;
import com.bbb.account.wallet.BBBGetProfileResponse;
import com.bbb.account.wallet.BBBGetProfileRequest;
import com.bbb.browse.BazaarVoiceUtil;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

public class BBBProfileManager extends BBBGenericService {

	private static final String GET_PROFILE = "getProfile";
	private BBBProfileTools mTools;
	private BBBPropertyManager mPmgr;
	private SiteGroupManager mSiteGroup;
	private BBBCatalogTools mCatalogTools;
	private String mPassword;
	private String mVerifyAccountServiceName;
	private String mGetAccountInfoServiceName;
	private String mReclaimAccountServiceName;
	private String mForgetPasswordServiceName;
	private BBBCheckoutManager mCheckoutManager;
	private BBBAddressAPIImpl mAddressAPIImpl;
	private BBBCreditCardAPIImpl mCreditCardAPIImpl;
	private GiftRegistryManager mGiftRegistryManager;
	final static private String UPDATE_REGISTRY_WITH_ATG_INFO = "updateRegistryWithAtgInfo";
	//final static private String ORDER_ITEM_DESCRIPTOR = "order";
    private static final String BED_BATH_US_ID = "BedBathUS";
    private static final String BED_BATH_CANADA_ID = "BedBathCanada";
    private static final String BUY_BUY_BABY_ID = "BuyBuyBaby";
    private static final String US = "US";
    private static final String CA = "CA";
    private static final String CANADA = "Canada";
	private OrderManager orderManager;
	private MutableRepository orderRepository;
	private String emailMD5HashPrefix;

	/**
	 * @return the emailMD5HashPrefix
	 */
	public String getEmailMD5HashPrefix() {
		return this.emailMD5HashPrefix;
	}

	/**
	 * @param emailMD5HashPrefix the emailMD5HashPrefix to set
	 */
	public void setEmailMD5HashPrefix(String emailMD5HashPrefix) {
		this.emailMD5HashPrefix = emailMD5HashPrefix;
	}

	/**
	 * @return
	 */
	public OrderManager getOrderManager() {
		return this.orderManager;
	}
	/**
	 * @param orderManager
	 */
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}    
	
	/**
	 * @return the orderRepository
	 */
	public MutableRepository getOrderRepository() {
		return orderRepository;
	}
	/**
	 * @param orderRepository the orderRepository to set
	 */
	public void setOrderRepository(final MutableRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public BBBAddressAPIImpl getAddressAPIImpl() {
		return mAddressAPIImpl;
	}

	public void setAddressAPIImpl(BBBAddressAPIImpl mAddressAPIImpl) {
		this.mAddressAPIImpl = mAddressAPIImpl;
	}

	public BBBCreditCardAPIImpl getCreditCardAPIImpl() {
		return mCreditCardAPIImpl;
	}

	public void setCreditCardAPIImpl(BBBCreditCardAPIImpl mCreditCardAPIImpl) {
		this.mCreditCardAPIImpl = mCreditCardAPIImpl;
	}

	public BBBCheckoutManager getCheckoutManager() {
		return mCheckoutManager;
	}

	public void setCheckoutManager(BBBCheckoutManager mCheckoutManager) {
		this.mCheckoutManager = mCheckoutManager;
	}

	/**
	 * @return the mVerifyAccountServiceName
	 */
	public String getVerifyAccountServiceName() {
		return mVerifyAccountServiceName;
	}

	/**
	 * @param mVerifyAccountServiceName
	 *            the mVerifyAccountServiceName to set
	 */
	public void setVerifyAccountServiceName(String pVerifyAccountServiceName) {
		this.mVerifyAccountServiceName = pVerifyAccountServiceName;
	}

	/**
	 * @return the mGetAccountInfoServiceName
	 */
	public String getGetAccountInfoServiceName() {
		return mGetAccountInfoServiceName;
	}

	/**
	 * @param mGetAccountInfoServiceName
	 *            the mGetAccountInfoServiceName to set
	 */
	public void setGetAccountInfoServiceName(String pGetAccountInfoServiceName) {
		this.mGetAccountInfoServiceName = pGetAccountInfoServiceName;
	}

	/**
	 * @return the mReclaimAccountServiceName
	 */
	public String getReclaimAccountServiceName() {
		return mReclaimAccountServiceName;
	}

	/**
	 * @param mReclaimAccountServiceName
	 *            the mReclaimAccountServiceName to set
	 */
	public void setReclaimAccountServiceName(String pReclaimAccountServiceName) {
		this.mReclaimAccountServiceName = pReclaimAccountServiceName;
	}

	/**
	 * @return the mForgetPasswordServiceName
	 */
	public String getForgetPasswordServiceName() {
		return mForgetPasswordServiceName;
	}

	/**
	 * @param mForgetPasswordServiceName
	 *            the mForgetPasswordServiceName to set
	 */
	public void setForgetPasswordServiceName(String pForgetPasswordServiceName) {
		this.mForgetPasswordServiceName = pForgetPasswordServiceName;
	}

	/**
	 * @return the mPassword
	 */
	public String getPassword() {
		return mPassword;
	}

	/**
	 * @param mPassword
	 *            the mPassword to set
	 */
	public void setPassword(String pPassword) {
		this.mPassword = pPassword;
	}

	/**
	 * @return the siteGroup
	 */
	public SiteGroupManager getSiteGroup() {
		return mSiteGroup;
	}

	/**
	 * @param pSiteGroup
	 *            the siteGroup to set
	 */
	public void setSiteGroup(SiteGroupManager pSiteGroup) {
		mSiteGroup = pSiteGroup;
	}

	/**
	 * @return the pmgr
	 */
	public BBBPropertyManager getPmgr() {
		return mPmgr;
	}

	/**
	 * @param pPmgr
	 *            the pmgr to set
	 */
	public void setPmgr(BBBPropertyManager pPmgr) {
		mPmgr = pPmgr;
	}

	/**
	 * @return mTools
	 */
	public BBBProfileTools getTools() {
		return mTools;
	}

	/**
	 * @param pTools
	 */
	public void setTools(BBBProfileTools pTools) {
		mTools = pTools;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	/**
	 * @return the mGiftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * @param mGiftRegistryManager the mGiftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager pGiftRegistryManager) {
		this.mGiftRegistryManager = pGiftRegistryManager;
	}

	/**
	 * This method checks whether the profile's site belongs to group which the
	 * current site belong like user register in SiteGroup1 but the current site
	 * belong to SiteGroup2. So it will return true else false
	 * 
	 * @param pLoginEmail
	 * @return boolean
	 */
	public boolean isUserPresentToOtherGroup(RepositoryItem pRepItem,
			String pSiteId) {
		String methodName = BBBCoreConstants.BELONG_TO_OTHER_GROUP;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.SIGNIN_CHECK_SITE_GROUP, methodName);
		logDebug("BBBProfileManager.isUserBelongToOtherGroup() method Ends");
		if (pRepItem != null && pSiteId != null && mSiteGroup != null) {
			Collection<SiteGroup> groupList = mSiteGroup
					.getSiteGroupsBySite(pSiteId);
			Map<String, Object> userCurrSiteMap = (Map<String, Object>) pRepItem
					.getPropertyValue(mPmgr.getUserSiteItemsPropertyName());
			if (groupList != null) {
				Iterator<SiteGroup> itr = groupList.iterator();
				while (itr.hasNext()) {
					if (userCurrSiteMap != null) {
						SiteGroup group = itr.next();
						Collection<Site> sites = group.getSites();
						Iterator<Site> site = sites.iterator();
						while (site.hasNext()) {
							String otherGroupSite = site.next().getId();
							if (userCurrSiteMap.containsKey(otherGroupSite)) {
								BBBPerformanceMonitor
										.end(BBBPerformanceConstants.SIGNIN_CHECK_SITE_GROUP,
												methodName);
								return false;
							}
						}
					}

				}

			}
		}

		logDebug("BBBProfileManager.isUserBelongToOtherGroup() method Ends");
		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.SIGNIN_CHECK_SITE_GROUP, methodName);
		return true;
	}

	/**
	 * This method add all the site into the profile of user, of a particular
	 * group
	 * 
	 * @param pEmail
	 * @param pSiteId
	 */
	public void addSiteToProfile(String pEmail, String pSiteId,String pMemberId, String pFavStore, String emailOptIn) {
		if (pEmail != null && pSiteId != null && mSiteGroup != null) {
			String methodName = BBBCoreConstants.ADD_SITE_TO_PROFILE;
			BBBPerformanceMonitor.start(BBBPerformanceConstants.SIGNIN_CHECK_SITE_GROUP,methodName);
			logDebug("BBBProfileManager.addSiteToProfile() method Starts");
			Collection<SiteGroup> groupList = mSiteGroup.getSiteGroupsBySite(pSiteId);
			if (groupList != null) {
				Iterator<SiteGroup> itrGroup = groupList.iterator();
				while (itrGroup.hasNext()) {
					String groupName = itrGroup.next().getId();

					SiteGroup group = mSiteGroup.getSiteGroupById(groupName);
					Collection<Site> sites = group.getSites();
					if (sites != null) {
						Iterator<Site> itr = sites.iterator();
						while (itr.hasNext()) {
							String siteID = itr.next().getId();
							try {
								mTools.createSiteItem(pEmail, siteID,pMemberId, pFavStore, emailOptIn);
							} catch (BBBSystemException e) {
								logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in BBBProfileManager.addSiteToProfile() while adding item to the User Profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1134 ), e);
							}
						}
					}
				}
				logDebug("BBBProfileManager.addSiteToProfile() method Ends");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SIGNIN_CHECK_SITE_GROUP,methodName);
			}
		}
	}

	/**
	 * This method verify's the Account
	 * 
	 * 
	 * 
	 * @param pEmailId
	 *            , pSiteId
	 * @return verifyAccountResponseVO
	 */

	public VerifyAccountResponseVO verifyAccountResponseVO(String pEmailId,
			String pSiteId) throws BBBSystemException, BBBBusinessException {
		logDebug("BBBProfileManager VerifyAccountResponseVO starts");
		// Setting request VO for webservice
		VerifyAccountReqVO requestVO = new VerifyAccountReqVO();
		requestVO.setEmailAddress(pEmailId);
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId) != null) {
			if (getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId)
					.size() > BBBCoreConstants.ZERO) {
				requestVO.setSiteFlag(getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId)
						.get(BBBCoreConstants.ZERO));
			} else {
				logError(LogMessageFormatter.formatMessage(null, "BBBProfileManager | VerifyAccountResponseVO | value not found for" + BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG , BBBCoreErrorConstants.ACCOUNT_ERROR_1135 ));
			}

		} else {
			BBBSystemException sysExc = new BBBSystemException(
					"BBBProfileManager | VerifyAccountResponseVO | value not found for "
							+ BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG);
			throw sysExc;
		}
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) != null) {
			if (getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size() > BBBCoreConstants.ZERO) {
				requestVO.setUserToken(getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
						BBBCoreConstants.ZERO));
			} else {
				logError(LogMessageFormatter.formatMessage(null, "BBBProfileManager | VerifyAccountResponseVO | value not found for "	+ BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN , BBBCoreErrorConstants.ACCOUNT_ERROR_1136 ));
			}
		} else {
			BBBSystemException sysExc = new BBBSystemException(
					"BBBProfileManager | VerifyAccountResponseVO | value not found for "
							+ BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN);
			throw sysExc;
		}
		requestVO.setServiceName(getVerifyAccountServiceName());

		logDebug("siteFlag: " + requestVO.getSiteFlag());
		logDebug("userToken: " + requestVO.getUserToken());
		logDebug("ServiceName: " + getVerifyAccountServiceName());
		VerifyAccountResponseVO responseVO = null;
		// Invoke the service handler which will call webservice
		responseVO = (VerifyAccountResponseVO) ServiceHandlerUtil
				.invoke(requestVO);
		logDebug("BBBProfileManager VerifyAccountResponseVO responseVO.getErrorStatus().isErrorExists() - "
				+ responseVO.getErrorStatus().isErrorExists());
		logDebug("BBBProfileManager VerifyAccountResponseVO ends responseVO"
				+ responseVO);

		return responseVO;
	}

	/**
	 * This method get's the Account info
	 * 
	 * 
	 * 
	 * @param pEmailId
	 *            , pPassword, pSiteId
	 * @return getAccountInfoResponseVO
	 */

	public GetAccountInfoResponseVO getAccountInfoResponseVO(String pEmailId,
			String pPassword, String pSiteId) throws BBBSystemException,
			BBBBusinessException {

		logDebug("BBBProfileManager getAccountInfoResponseVO starts");

		// Setting request VO for webservice
		GetAccountInfoReqVO getAccountInfoReqVO = new GetAccountInfoReqVO();

		getAccountInfoReqVO.setEmailAddress(pEmailId);
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId) != null) {
			if (getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId)
					.size() > BBBCoreConstants.ZERO) {
				getAccountInfoReqVO.setSiteFlag(getCatalogTools()
						.getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
								pSiteId).get(BBBCoreConstants.ZERO));
			} else {
				logError(LogMessageFormatter.formatMessage(null, "BBBProfileManager| GetAccountInfoResponseVO value not found for " + BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG , BBBCoreErrorConstants.ACCOUNT_ERROR_1137 ));
			}

		} else {
			BBBSystemException sysExc = new BBBSystemException(
					"BBBWebservicesConfig | GetAccountInfoResponseVO | value not found for "
							+ BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG);
			throw sysExc;
		}
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) != null) {
			if (getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size() > BBBCoreConstants.ZERO) {
				getAccountInfoReqVO.setUserToken(getCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
						.get(BBBCoreConstants.ZERO));
			} else {
				logError(LogMessageFormatter.formatMessage(null, "BBBProfileManager| GetAccountInfoResponseVO value not found for " + BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN , BBBCoreErrorConstants.ACCOUNT_ERROR_1138 ));
			}
		} else {

			BBBSystemException sysExc = new BBBSystemException(
					"BBBWebservicesConfig | GetAccountInfoResponseVO | value not found for "
							+ BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN);
			throw sysExc;
		}
		getAccountInfoReqVO.setPassword(pPassword);
		getAccountInfoReqVO.setServiceName(getGetAccountInfoServiceName());
		GetAccountInfoResponseVO getAccountInfoResponseVO = null;

		// Invoke the service handler which will call webservice
		getAccountInfoResponseVO = (GetAccountInfoResponseVO) ServiceHandlerUtil
				.invoke(getAccountInfoReqVO);
		logDebug("BBBProfileManager GetAccountInfoResponseVO getErrorStatus"
				+ getAccountInfoResponseVO.getErrorStatus().isErrorExists());
		logDebug("BBBProfileManager GetAccountInfoResponseVO ends");

		return getAccountInfoResponseVO;
	}

	/**
	 * This method reclaim the Account
	 * 
	 * 
	 * 
	 * @param pEmailId
	 *            , pSiteId, pMemberToken
	 * @return reclaimAccountResponseVO
	 */

	public ReclaimAccountResponseVO reclaimAccountResponseVO(String pProfileId,
			String pSiteId, String pMemberToken) throws BBBSystemException,
			BBBBusinessException {

		logDebug("BBBProfileManager ReclaimAccount starts");

		// Setting request VO for webservice
		ReclaimAccountReqVO requestVO = new ReclaimAccountReqVO();
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId) != null) {
			if (getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId)
					.size() > BBBCoreConstants.ZERO) {
				requestVO.setSiteFlag(getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId)
						.get(BBBCoreConstants.ZERO));
			} else {
				logError(LogMessageFormatter.formatMessage(null, "BBBProfileManager| ReclaimAccountReqVO value not found for" + BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG , BBBCoreErrorConstants.ACCOUNT_ERROR_1139 ));
			}
		} else {
			BBBSystemException sysExc = new BBBSystemException(
					"BBBProfileManager | ReclaimAccountReqVO | value not found for "
							+ BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG);
			throw sysExc;
		}
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) != null) {
			if (getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size() > BBBCoreConstants.ZERO) {
				requestVO.setUserToken(getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
						BBBCoreConstants.ZERO));
			} else {
				logError(LogMessageFormatter.formatMessage(null, "BBBProfileManager|  ReclaimAccountReqVO value not found for" + BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN , BBBCoreErrorConstants.ACCOUNT_ERROR_1140 ));
			}
		} else {
			BBBSystemException sysExc = new BBBSystemException(
					"BBBProfileManager | ReclaimAccountReqVO | value not found for "
							+ BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN);
			throw sysExc;
		}
		requestVO.setServiceName(getReclaimAccountServiceName());
		requestVO.setMemberToken(pMemberToken);
		requestVO.setProfileId(pProfileId);

		ReclaimAccountResponseVO reclaimAccountResponseVO = null;

		// Invoke the service handler which will call webservice
		reclaimAccountResponseVO = (ReclaimAccountResponseVO) ServiceHandlerUtil
				.invoke(requestVO);
		logDebug("BBBProfileManager ReclaimAccount responseVO"
				+ reclaimAccountResponseVO.getErrorStatus().isErrorExists());
		logDebug("BBBProfileManager ReclaimAccount ends");
		return reclaimAccountResponseVO;
	}

	/**
	 * This method send the forgot password info
	 * 
	 * 
	 * 
	 * @param pEmailId
	 *            , pSiteId
	 * @return forgetPasswordResponseVO
	 */

	public ForgetPasswordResponseVO forgetPasswordResponseVO(String pEmailId,
			String pSiteId) throws BBBSystemException, BBBBusinessException {

		logDebug("BBBProfileManager ForgetPassword starts");

		// Setting request VO for webservice
		ForgetPasswordReqVO forgetPasswordReqVO = new ForgetPasswordReqVO();
		forgetPasswordReqVO.setEmailAddress(pEmailId);
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId) != null) {
			if (getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId)
					.size() > BBBCoreConstants.ZERO) {
				forgetPasswordReqVO.setSiteFlag(getCatalogTools()
						.getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
								pSiteId).get(BBBCoreConstants.ZERO));
			} else {
				logError(LogMessageFormatter.formatMessage(null, "BBBProfileManager| ForgetPasswordResponseVO value not found for " + BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG , BBBCoreErrorConstants.ACCOUNT_ERROR_1141 ));
			}
		} else {

			BBBSystemException sysExc = new BBBSystemException(
					"BBBProfileManager | ForgetPasswordResponseVO | value not found for "
							+ BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG);
			throw sysExc;
		}
		if (getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) != null) {
			if (getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size() > 0) {
				forgetPasswordReqVO.setUserToken(getCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
						.get(BBBCoreConstants.ZERO));
			} else {
				logError(LogMessageFormatter.formatMessage(null, "BBBProfileManager| ForgetPasswordResponseVO value not found for " + BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN , BBBCoreErrorConstants.ACCOUNT_ERROR_1142 ));
			}
		} else {

			BBBSystemException sysExc = new BBBSystemException(
					"BBBProfileManager | ForgetPasswordResponseVO | value not found for "
							+ BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN);
			throw sysExc;
		}
		forgetPasswordReqVO.setServiceName(getForgetPasswordServiceName());

		ForgetPasswordResponseVO forgetPasswordResponseVO = null;

		// Invoke the service handler which will call webservice
		forgetPasswordResponseVO = (ForgetPasswordResponseVO) ServiceHandlerUtil
				.invoke(forgetPasswordReqVO);
		logDebug("BBBProfileManager ForgetPassword responseVO"
				+ forgetPasswordResponseVO.getErrorStatus().isErrorExists());
		logDebug("BBBProfileManager ForgetPassword ends");
		return forgetPasswordResponseVO;
	}

	public boolean removeAddressForProfile(Profile pProfile,
			String pAddressNickName, boolean pSetDefaultShippingAddress,
			boolean pSetDefaultBillingAddress, boolean pSetDefaultMailingAddress) throws BBBSystemException,
			TransactionDemarcationException {
		TransactionManager transactionManager = null;

		BBBProfileTools profileTools = (BBBProfileTools) getTools();
		Repository profileRepos = profileTools.getProfileRepository();
		if (profileRepos instanceof RepositoryImpl) {
			transactionManager = ((RepositoryImpl) profileRepos)
					.getTransactionManager();
		}
		TransactionDemarcation transactionDemarcation = new TransactionDemarcation();

		try {
			if (transactionManager != null) {
				transactionDemarcation.begin(transactionManager,
						TransactionDemarcation.REQUIRED);
			}

			if (BBBUtility.isEmpty(pAddressNickName)) {
				logDebug("A null or empty nickname was provided to handleRemoveAddress");

				// if no nickname provided, do nothing.
				return true;
			}

			Profile profile = pProfile;

			// Remove the Address from the Profile
			RepositoryItem purgeAddress = profileTools.getProfileAddress(
					profile, pAddressNickName);
			if (purgeAddress != null) {
				profileTools.removeProfileRepositoryAddress(profile,
						pAddressNickName, true);
			}

			if (pSetDefaultShippingAddress) {
				((BBBProfileTools) getTools()).setDefaultShippingAddress(
						profile, null);
				Map<String, RepositoryItem> profileAddresses = (Map<String, RepositoryItem>) pProfile
						.getPropertyValue(getPmgr()
								.getSecondaryAddressPropertyName());
				if (profileAddresses != null && !profileAddresses.isEmpty()) {
					String firstAddress = profileAddresses.keySet().iterator()
							.next();
					((BBBProfileTools) getTools()).setDefaultShippingAddress(
							profile, firstAddress);
				}
			}

			if (pSetDefaultBillingAddress) {
				((BBBProfileTools) getTools()).setDefaultBillingAddress(
						profile, null);
				Map<String, RepositoryItem> profileAddresses = (Map<String, RepositoryItem>) pProfile
						.getPropertyValue(getPmgr()
								.getSecondaryAddressPropertyName());
				if (profileAddresses != null && !profileAddresses.isEmpty()) {
					String firstAddress = profileAddresses.keySet().iterator()
							.next();
					((BBBProfileTools) getTools()).setDefaultBillingAddress(
							profile, firstAddress);
				}
			}
			
			if (pSetDefaultMailingAddress) {
				((BBBProfileTools) getTools()).setDefaultMailingAddress(
						profile, null);
				Map<String, RepositoryItem> profileAddresses = (Map<String, RepositoryItem>) pProfile
						.getPropertyValue(getPmgr()
								.getSecondaryAddressPropertyName());
				if (profileAddresses != null && !profileAddresses.isEmpty()) {
					String firstAddress = profileAddresses.keySet().iterator()
							.next();
					((BBBProfileTools) getTools()).setDefaultMailingAddress(
							profile, firstAddress);
				}
			}

			logDebug("BBBProfileFormHandler.handleRemoveAddress() method ended");

			return true;
		} catch (TransactionDemarcationException transactionDemarcationException) {
			logError(LogMessageFormatter.formatMessage(null, "TransactionDemarcationException - BBBProfileManager : removeAddressForProfile", BBBCoreErrorConstants.ACCOUNT_ERROR_1143 ), transactionDemarcationException);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1143,
					transactionDemarcationException.getMessage(),
					transactionDemarcationException);

		} catch (RepositoryException repositoryExc) {
			logError(LogMessageFormatter.formatMessage(null, "RepositoryException - BBBProfileManager : removeAddressForProfile", BBBCoreErrorConstants.ACCOUNT_ERROR_1144 ), repositoryExc);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1144,repositoryExc.getMessage(),
					repositoryExc);
		} finally {
			if (transactionManager != null) {
				transactionDemarcation.end();
			}
		}

	}

	public boolean updateAddressForProfile(Profile pProfile, Map pEditValue,
			boolean pSetDefaultShippingAddress,
			boolean pSetDefaultBillingAddress, boolean pSetDefaultMailingAddress, String pNicknameValueMapKey,
			String pNewNicknameValueMapKey) throws BBBSystemException,
			TransactionDemarcationException {
		TransactionManager transactionManager = null;

		logDebug("updateAddressForProfile() method started in BBBProfileManager");
		
		BBBProfileTools profileTools = (BBBProfileTools) getTools();
		Repository profileRepos = profileTools.getProfileRepository();
		if (profileRepos instanceof RepositoryImpl) {
			transactionManager = ((RepositoryImpl) profileRepos)
					.getTransactionManager();
		}

		TransactionDemarcation transactionDemarcation = new TransactionDemarcation();

		try {
			if (transactionManager != null) {
				transactionDemarcation.begin(transactionManager,
						TransactionDemarcation.REQUIRED);
			}

			String nickname = (String) pEditValue.get(pNicknameValueMapKey);
			String newNickname = ((String) pEditValue
					.get(pNewNicknameValueMapKey));

			// Populate Address object data entered by user
			//Address addressObject = AddressTools.createAddressFromMap(
			//		pEditValue, profileTools.getShippingAddressClassName());
				
			final BBBAddressObject addressObject = new BBBAddressObject();
			if(pEditValue.get(BBBCoreConstants.CC_FIRST_NAME)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_FIRST_NAME).toString())){
            addressObject.setFirstName(pEditValue.get(BBBCoreConstants.CC_FIRST_NAME).toString());}
            if(pEditValue.get(BBBCoreConstants.CC_LAST_NAME)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_LAST_NAME).toString())){
            addressObject.setLastName(pEditValue.get(BBBCoreConstants.CC_LAST_NAME).toString());}
            if(pEditValue.get(BBBCoreConstants.CC_COMPANY_NAME)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_COMPANY_NAME).toString())){
            addressObject.setCompanyName(pEditValue.get(BBBCoreConstants.CC_COMPANY_NAME).toString());}
            if(pEditValue.get(BBBCoreConstants.CC_ADDRESS1)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_ADDRESS1).toString())){            
            addressObject.setAddress1(pEditValue.get(BBBCoreConstants.CC_ADDRESS1).toString());}
            if(pEditValue.get(BBBCoreConstants.CC_ADDRESS2)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_ADDRESS2).toString())) {           
            addressObject.setAddress2(pEditValue.get(BBBCoreConstants.CC_ADDRESS2).toString());}
            if(pEditValue.get(BBBCoreConstants.CC_CITY)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_CITY).toString()))  {          
            addressObject.setCity(pEditValue.get(BBBCoreConstants.CC_CITY).toString());}
            if(pEditValue.get(BBBCoreConstants.CC_STATE)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_STATE).toString())){            
            addressObject.setState(pEditValue.get(BBBCoreConstants.CC_STATE).toString());}
            if(pEditValue.get(BBBCoreConstants.CC_COUNTRY)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_COUNTRY).toString())){           
            addressObject.setCountry(pEditValue.get(BBBCoreConstants.CC_COUNTRY).toString());}  
            if(pEditValue.get(BBBCoreConstants.CC_POSTAL_CODE)!=null&& !StringUtils.isEmpty(pEditValue.get(BBBCoreConstants.CC_POSTAL_CODE).toString()))  {          
            addressObject.setPostalCode(pEditValue.get(BBBCoreConstants.CC_POSTAL_CODE).toString());}
            if(pEditValue.get(BBBCoreConstants.CC_POBOXADDRESS)!=null){
            addressObject.setPoBoxAddress(((Boolean)pEditValue.get(BBBCoreConstants.CC_POBOXADDRESS)).booleanValue());}
            if(pEditValue.get(BBBCoreConstants.CC_QASVALIDATED)!=null){
            addressObject.setQasValidated(((Boolean)pEditValue.get(BBBCoreConstants.CC_QASVALIDATED)).booleanValue());   
            }
			
			// Get address repository item to be updated
			RepositoryItem oldAddress = profileTools.getProfileAddress(
					pProfile, nickname);

			// Update address repository item
			profileTools.updateProfileRepositoryAddress(oldAddress,
					addressObject);

			if (!BBBUtility.isBlank(newNickname)) {
				// Change nickname
				if (!newNickname.equalsIgnoreCase(nickname)) {
					profileTools.changeSecondaryAddressName(pProfile, nickname,
							newNickname);
				}
				// Update nickname
				else {
					updateSecondaryAddressName(pProfile, nickname, newNickname);
				}
			}

			if (pSetDefaultShippingAddress) {
				((BBBProfileTools) getTools()).setDefaultShippingAddress(
						pProfile, nickname);
			} else if (nickname.equalsIgnoreCase(profileTools
					.getDefaultShippingAddressNickname(pProfile))) {
				profileTools.setDefaultShippingAddress(pProfile, nickname);
			}

			if (pSetDefaultBillingAddress) {
				((BBBProfileTools) getTools()).setDefaultBillingAddress(
						pProfile, nickname);
			} else if (nickname.equalsIgnoreCase(profileTools
					.getDefaultBillingAddressNickname(pProfile))) {
				profileTools.setDefaultBillingAddress(pProfile, nickname);
			}
			
			if (pSetDefaultMailingAddress) {
				((BBBProfileTools) getTools()).setDefaultMailingAddress(
						pProfile, nickname);
			} else if (nickname.equalsIgnoreCase(profileTools
					.getDefaultMailingAddressNickname(pProfile))) {
				profileTools.setDefaultMailingAddress(pProfile, nickname);
			}

		} catch (RepositoryException repositoryExc) {
			if(isLoggingDebug()){
			logDebug(LogMessageFormatter.formatMessage(null, "RepositoryException - BBBProfileManager : updateAddressForProfile", BBBCoreErrorConstants.ACCOUNT_ERROR_1145 ),repositoryExc);
			}
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1145,repositoryExc.getMessage(),
					repositoryExc);
		} catch (TransactionDemarcationException e) {
			logError(LogMessageFormatter.formatMessage(null, "TransactionDemarcationException - BBBProfileManager : updateAddressForProfile", BBBCoreErrorConstants.ACCOUNT_ERROR_1149 ), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1149,e.getMessage(), e);
		} finally {
			if (transactionManager != null) {
				transactionDemarcation.end();
			}
		}
		logDebug("updateAddressForProfile() method ended in BBBProfileManager");
		return true;
	}

	/**
	 * Updates an address nickname
	 * 
	 * @param pProfile
	 *            The users profile
	 * @param pOldAddressName
	 *            Old address name
	 * @param pNewAddressName
	 *            New address name
	 */
	protected void updateSecondaryAddressName(RepositoryItem pProfile,
			String pOldAddressName, String pNewAddressName) {

		BBBProfileTools profileTools = (BBBProfileTools) getTools();
		CommercePropertyManager cpmgr = (CommercePropertyManager) profileTools
				.getPropertyManager();
		Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr
				.getSecondaryAddressPropertyName());
		RepositoryItem address = profileTools.getProfileAddress(pProfile,
				pOldAddressName);
		if (address != null) {
			try {
				secondaryAddresses.remove(pOldAddressName);
				secondaryAddresses.put(pNewAddressName, address);
				profileTools.updateProperty(
						cpmgr.getSecondaryAddressPropertyName(),
						secondaryAddresses, pProfile);
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException - BBBProfileManager : updateSecondaryAddressName", BBBCoreErrorConstants.ACCOUNT_ERROR_1151 ), e);
			}
		}
	}

	public boolean addAddressForProfile(Profile pProfile, Map mNewAddress,
			boolean pSetDefaultShippingAddress,
			boolean pSetDefaultBillingAddress, boolean pSetDefaultMailingAddress, String pNicknameValueMapKey)
			throws BBBSystemException {
		TransactionManager transactionManager = null;

		BBBProfileTools profileTools = (BBBProfileTools) getTools();
		Repository profileRepos = profileTools.getProfileRepository();
		if (profileRepos instanceof RepositoryImpl) {
			transactionManager = ((RepositoryImpl) profileRepos)
					.getTransactionManager();
		}

		TransactionDemarcation transactionDemarcation = new TransactionDemarcation();

		String nickname = null;
		nickname = (String) mNewAddress.get(pNicknameValueMapKey);
		if (StringUtils.isBlank(nickname)) {
			nickname = profileTools.getUniqueShippingAddressNickname(
					mNewAddress, pProfile, null);
		}

		try {
			if (transactionManager != null) {
				transactionDemarcation.begin(transactionManager,
						TransactionDemarcation.REQUIRED);
			}

			// Populate Address object with new address properties
			//Address addressObject;

				//addressObject = AddressTools.createAddressFromMap(
				//		mNewAddress, profileTools.getShippingAddressClassName());

				final BBBAddressObject addressObject = new BBBAddressObject();
                addressObject.setFirstName(mNewAddress.get(BBBCoreConstants.CC_FIRST_NAME).toString());
                addressObject.setLastName(mNewAddress.get(BBBCoreConstants.CC_LAST_NAME).toString());
                addressObject.setCompanyName(mNewAddress.get(BBBCoreConstants.CC_COMPANY_NAME).toString());
                addressObject.setAddress1(mNewAddress.get(BBBCoreConstants.CC_ADDRESS1).toString());
                if(!BBBUtility.isEmpty((String)mNewAddress.get(BBBCoreConstants.CC_ADDRESS2))) addressObject.setAddress2(mNewAddress.get(BBBCoreConstants.CC_ADDRESS2).toString());
                if(!BBBUtility.isEmpty((String)mNewAddress.get(BBBCoreConstants.CC_ADDRESS3))) addressObject.setAddress3(mNewAddress.get(BBBCoreConstants.CC_ADDRESS3).toString());
                addressObject.setCity(mNewAddress.get(BBBCoreConstants.CC_CITY).toString());
                addressObject.setState(mNewAddress.get(BBBCoreConstants.CC_STATE).toString());
                addressObject.setCountry(mNewAddress.get(BBBCoreConstants.CC_COUNTRY).toString());
                addressObject.setPostalCode(mNewAddress.get(BBBCoreConstants.CC_POSTAL_CODE).toString());
                addressObject.setPoBoxAddress(((Boolean)mNewAddress.get("poBoxAddress")).booleanValue());
                addressObject.setQasValidated(((Boolean)mNewAddress.get("qasValidated")).booleanValue());
			
			// Create secondary address repository item
			profileTools.createProfileRepositorySecondaryAddress(pProfile,
					nickname, addressObject);

			// Check to see Profile.shippingAddress is null, if it is,
			// add the new address as the default shipping address
			if (pSetDefaultShippingAddress) {
				profileTools.setDefaultShippingAddress(pProfile, nickname);
			}

			// Check to see Profile.billingAddress is null, if it is,
			// add the new address as the default billing address

			if (pSetDefaultBillingAddress) {
				profileTools.setDefaultBillingAddress(pProfile, nickname);
			}
			
			// Check to see Profile.mailingAddress is null, if it is,
			// add the new address as the default billing address

			if (pSetDefaultMailingAddress) {
				profileTools.setDefaultMailingAddress(pProfile, nickname);
			}

		} catch (RepositoryException repositoryExc) {

			logError(LogMessageFormatter.formatMessage(null, "RepositoryException - BBBProfileManager : addAddressForProfile", BBBCoreErrorConstants.ACCOUNT_ERROR_1152 ), repositoryExc);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1152,repositoryExc.getMessage(),
					repositoryExc);
		} catch (TransactionDemarcationException demarcationException) {
			logError(LogMessageFormatter.formatMessage(null, "TransactionDemarcationException - BBBProfileManager : addAddressForProfile", BBBCoreErrorConstants.ACCOUNT_ERROR_1157 ), demarcationException);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1157,demarcationException.getMessage(),
					demarcationException);
		} finally {
			try {
				if (transactionManager != null) {
					transactionDemarcation.end();
				}
			} catch (TransactionDemarcationException eTransactionDemarcationException) {
				logError(LogMessageFormatter.formatMessage(null, "TransactionDemarcationException - BBBProfileManager : addAddressForProfile", BBBCoreErrorConstants.ACCOUNT_ERROR_1158 ), eTransactionDemarcationException);
			}
		}
		return true;
	}

	public void checkoutRegistration(BBBOrder bOrder, Profile pProfile,
			String siteId, boolean isPreSelectedAddress)
			throws BBBSystemException, BBBBusinessException {
		BBBAddress billingAddress = mCheckoutManager.getBillingAddress(bOrder); 

		List<BBBAddress> shippingAddressList = mCheckoutManager
				.getNonRegistryShippingAddress(bOrder);
	
		boolean isBillingShippingAddress = false;
		boolean billingAddressAdded = false;
		boolean saveCreditCardToProfile = true;
		
		if (shippingAddressList != null && !shippingAddressList.isEmpty()) {

			for (BBBAddress shippingAddress : shippingAddressList) {
				
				boolean canSaveToShippingAddress = true;				
				if (shippingAddress != null) {
					shippingAddress.getId();
					if (isPreSelectedAddress && BBBAddressTools.compare((Address)shippingAddress, (Address)billingAddress)) {
						isBillingShippingAddress = true;
						billingAddressAdded = true;
					}
					else if(!isPreSelectedAddress){
						DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
						String isIntl = (String)req.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS);
						if(isIntl != null && isIntl.equalsIgnoreCase("true")){
							  billingAddressAdded = true;
						}
					    saveCreditCardToProfile = false;
					}
					//Don't save as shipping address for US Territories AS,GU,FM,MP,PR,PW,MH,VI
			        String bedBathUSSite = "";
			        String buyBuyBabySite = "";
			        List<String> config = getCatalogTools().getContentCatalogConfigration(BBBCatalogConstants.BED_BATH_US_SITE_CODE);
			        if ((config != null) && !config.isEmpty()) {
			            bedBathUSSite = config.get(0);
			        }
			        config = getCatalogTools().getContentCatalogConfigration(BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE);
			        if ((config != null) && !config.isEmpty()) {
			            buyBuyBabySite = config.get(0);
			        }					
					if((siteId.equals(bedBathUSSite)
			    			|| siteId.equals(buyBuyBabySite))			    		
			    			&& !mCheckoutManager.canAddTheAddressToProfile(shippingAddress)) {
						canSaveToShippingAddress = false;
					}
					if(!shippingAddress.getIsBeddingKitOrderAddr() 
							&& !shippingAddress.getIsWebLinkOrderAddr()
							&& canSaveToShippingAddress){
						mAddressAPIImpl.addNewShippingAddress(pProfile,
								shippingAddress, siteId, true, isBillingShippingAddress);
						
					}
					isBillingShippingAddress = false;
				}
			}
		} else if(!isPreSelectedAddress) {
			DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
			String isIntl = (String)req.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS);
			if(isIntl != null && isIntl.equalsIgnoreCase("true")){
				  billingAddressAdded = true;
			}
		    saveCreditCardToProfile = false;
		}
		
		/*if(null != billingAddress && (null == shippingAddressList || shippingAddressList.isEmpty())){
			billingAddressAdded = true;
			mAddressAPIImpl.addNewShippingAddress(pProfile,
					billingAddress, siteId, true,true);
		}*/
		//************************R2.2 Code Starts*******************
		//Defect Id Bed-419
		boolean internationalAddr = false;
		if (null != billingAddress && !BBBUtility.isEmpty(billingAddress.getCountry())) {
			if (billingAddress.getCountry().equals(US)) {
				internationalAddr = !((billingAddress.getCountry().equals(US)) && (siteId.equals(BED_BATH_US_ID) || siteId.equals(BUY_BUY_BABY_ID)));
			} else if (billingAddress.getCountry().equals(CA) || billingAddress.getCountry().equalsIgnoreCase(CANADA)) {
				internationalAddr = !((billingAddress.getCountry().equals(CA) || billingAddress.getCountry().equalsIgnoreCase(CANADA)) && (siteId.equals(BED_BATH_CANADA_ID)));
			} else {
				internationalAddr = true;
			}
		}
		//Billing address of PayPal Not to be save in address book
		Boolean isFromPayPal = ((BBBRepositoryContactInfo)billingAddress).getIsFromPaypal();
		if(!internationalAddr && isFromPayPal != null && isFromPayPal){
			internationalAddr = true;
		}
		//************************R2.2 Code Ends*******************
		if (billingAddress != null && !billingAddressAdded && !internationalAddr) {
			
			if(!billingAddress.getIsBeddingKitOrderAddr() && !billingAddress.getIsWebLinkOrderAddr()){
				mAddressAPIImpl.addNewBillingAddress(pProfile, billingAddress,
						siteId,true);
			}
			
		}
		
		if (pProfile.getPropertyValue(mPmgr.getSaveCreditCardInfoToProfile()) != null && !(Boolean) pProfile.getPropertyValue(mPmgr.getSaveCreditCardInfoToProfile()) && saveCreditCardToProfile) {
			List<BasicBBBCreditCardInfo> cardInfoList = mCheckoutManager.getCreditCardFromOrder(bOrder);
			if (cardInfoList != null) {
				for (BasicBBBCreditCardInfo cardInfo : cardInfoList) {
					if (cardInfo != null) {
						mCreditCardAPIImpl.addNewCreditCard(pProfile, cardInfo,siteId);
					}
				}
			}
		}
		
		//BBBSL-1151 Assign ProfileId with Order Id if user extend account in  Guest checkout flow

		logDebug("Assign ProfileId with Order Id if user extend account in  Guest checkout flow");
		//BBBSL-2735. Updating order object instead of order repository item.
		String orderProfileId = bOrder.getProfileId();
		if(!BBBUtility.isEmpty(orderProfileId)){
			String profileId = pProfile.getRepositoryId();
			logDebug("Order associate with Profile Id " + orderProfileId);
			logDebug("User Profile Id  " + profileId);
			if(!orderProfileId.equalsIgnoreCase(profileId)){
				bOrder.setProfileId(profileId);
				try {
					getOrderManager().updateOrder(bOrder);
				} catch (CommerceException e) {
					String msg = "Commerce Exception while updating the Order profile id for order ["+bOrder.getId()+"] in checkoutRegistration() in BBBProfileManager";
					this.logError(msg, e);
				}
			}
		}
	}

	/**
	 * This method updates the userProfile by adding/removing values of
	 * favStoreId for the current site. If successfull returns true else false
	 * 
	 * @param pProfile
	 * @param pCurrSite
	 * @param pFavStoreId
	 * @return status
	 * @throws RepositoryException 
	 */
	public boolean updateSiteItem(Profile pProfile, String pCurrSite,
			String pId, String pProperty) throws RepositoryException {
		logDebug("BBBProfileManager.updateFavStoreToPofile() method started");
	
		logDebug("Profile = " + pProfile + " pCurrSite =" + pCurrSite + " pId = " + pId + " pProperty =" + pProperty);
		
		boolean status = false;
		if (pProfile != null && pCurrSite != null && pProperty != null) {
			try {
				Map siteMap = (Map) pProfile
						.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);
				RepositoryItem siteItem = (RepositoryItem) siteMap
						.get(pCurrSite);
				if (siteItem != null) {
					MutableRepository repo = (MutableRepository) pProfile
							.getRepository();
					MutableRepositoryItem item = repo.getItemForUpdate(siteItem
							.getRepositoryId(), siteItem.getItemDescriptor()
							.getItemDescriptorName());
					item.setPropertyValue(pProperty, pId);
					repo.updateItem(item);
					status = true;
				}
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException : BBBProfileManager.updateSiteItem while updating the user Profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1159 ), e);
			    throw new RepositoryException("RepositoryException : BBBProfileManager.updateSiteItem while updating the user Profile");
			}
		}
		logDebug("BBBProfileManager.updateFavStoreToPofile() method ends");
		return status;
	}

	/**
	 * This method validated whether the email, siteId and password belongs to
	 * some legacy user or not by calling the bbb web services.
	 * 
	 * @param mEmail
	 * @param mSiteId
	 * @param mPassword
	 * @return boolean
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean isOldAccountValid(String mEmail, String mSiteId,
			String mPassword) throws BBBBusinessException, BBBSystemException {
		
			logDebug("BBBProfileManager.isOldAccountValid() method started");
			
			logDebug("Email = " + mEmail + " mSiteId =" + mSiteId
						+ " mPassword = " + mPassword);
			
		/* Commenting this code... We dont need to call this service anymore because now we are not migrating any account. 
		 * Also the webservice returns "true" for each request so this is just adding a network call
		 * 
		if (mEmail != null && mSiteId != null && mPassword != null) {
			ValidateOldAccReqVO reqVO = new ValidateOldAccReqVO();
			String siteFlag = null;
			String userToken = null;

			try {
				if (getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, mSiteId) != null
						&& getCatalogTools().getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
								mSiteId).size() > BBBCoreConstants.ZERO) {
					siteFlag = getCatalogTools().getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
							mSiteId).get(BBBCoreConstants.ZERO);
				}

				if (getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) != null
						&& getCatalogTools().getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
								.size() > BBBCoreConstants.ZERO) {
					userToken = getCatalogTools().getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY,
							BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
							BBBCoreConstants.ZERO);
				}
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in BBBProfileManager.isOldAccountValid() while fetching the values from catalog", BBBCoreErrorConstants.ACCOUNT_ERROR_1160 ), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException in BBBProfileManager.isOldAccountValid() while fetching the values from catalog", BBBCoreErrorConstants.ACCOUNT_ERROR_1161 ), e);
			}

			if (siteFlag != null && userToken != null) {

				reqVO.setServiceName(BBBCoreConstants.VALIDATE_OLD_ACCOUNT);
				reqVO.setEmail(mEmail);
				reqVO.setUserToken(userToken);
				reqVO.setSiteFlag(siteFlag);
				reqVO.setPassword(mPassword);
				ValidateOldAccResVO resVO = (ValidateOldAccResVO) ServiceHandlerUtil
						.invoke(reqVO);
				
					logDebug("BBBProfileManager.isOldAccountValid() method ends");
				
				return resVO.getIsValid();

			} else {
				logError(LogMessageFormatter.formatMessage(null, "No Value available for mSiteId = " + siteFlag + " or userToken = " + userToken + " in catalog", BBBCoreErrorConstants.ACCOUNT_ERROR_1162 ));
			}
		}
		*/
			logDebug("BBBProfileManager.isOldAccountValid() method ends");
		
		return true;
	}
	
	
	/**
	 * This method will update the new password provided by user in 
	 * profile repository and also set the property generatedPassword to false
	 * @param pEmail
	 * @param pPassword
	 * @return
	 */
	
	public boolean updatePasswordFromToken(String pEmail, String pPassword)
	{

		
		logDebug("BBBProfileManager.updatePasswordFromToken() method starts");
		
		if (pEmail != null && pPassword != null){
			try {
				RepositoryItem bbbUserProfile = mTools.getItemFromEmail(pEmail
						.toLowerCase());
				if (bbbUserProfile != null) {
					MutableRepositoryItem mutableBBBProfile = mTools
							.getProfileRepository().getItemForUpdate(
									bbbUserProfile.getRepositoryId(),
									bbbUserProfile.getItemDescriptor()
											.getItemDescriptorName());
					String encrypted = mTools.getPropertyManager()
							.generatePassword(pEmail.toLowerCase(), pPassword);
					mutableBBBProfile.setPropertyValue(
							mPmgr.getPasswordPropertyName(), encrypted);
					mutableBBBProfile.setPropertyValue(mPmgr.getLoggedIn(),
							true);
					mutableBBBProfile.setPropertyValue(
							BBBCoreConstants.GENERATED_PASSWORD, false);
					mTools.getProfileRepository().updateItem(mutableBBBProfile);
					ItemDescriptorImpl profileItemDescriptor = (ItemDescriptorImpl) bbbUserProfile
							.getItemDescriptor();
					profileItemDescriptor.removeItemFromCache(bbbUserProfile
							.getRepositoryId());
					
						logDebug("BBBProfileManager.updatePassword() method ends");
					
					return true;
				}
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException BBBProfileManager.updatePasswordFromToken()", BBBCoreErrorConstants.ACCOUNT_ERROR_1163 ), e);
			}
		}
			logDebug("BBBProfileManager.updatePasswordFromToken() method ends");
		
		return false;
	
		
	}

	/**
	 * Update the user's password and login property
	 * 
	 * @param mEmail
	 * @param mPassword
	 * @return boolean
	 */
	public boolean updatePassword(String pEmail, String pPassword) {
		
		logDebug("BBBProfileManager.updatePassword() method ends");
		
		if (pEmail != null && pPassword != null){
			try {
				RepositoryItem bbbUserProfile = mTools.getItemFromEmail(pEmail
						.toLowerCase());
				if (bbbUserProfile != null) {
					MutableRepositoryItem mutableBBBProfile = mTools
							.getProfileRepository().getItemForUpdate(
									bbbUserProfile.getRepositoryId(),
									bbbUserProfile.getItemDescriptor()
											.getItemDescriptorName());
					String encrypted = mTools.getPropertyManager()
							.generatePassword(pEmail.toLowerCase(), pPassword);
					mutableBBBProfile.setPropertyValue(
							mPmgr.getPasswordPropertyName(), encrypted);
					mutableBBBProfile.setPropertyValue(mPmgr.getLoggedIn(),
							true);
						mTools.getProfileRepository().updateItem(mutableBBBProfile);
					ItemDescriptorImpl profileItemDescriptor = (ItemDescriptorImpl) bbbUserProfile
							.getItemDescriptor();
					profileItemDescriptor.removeItemFromCache(bbbUserProfile
							.getRepositoryId());
					
						logDebug("BBBProfileManager.updatePassword() method ends");
					
					return true;
				}
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException BBBProfileManager.updatePassword()", BBBCoreErrorConstants.ACCOUNT_ERROR_1163 ), e);
			}
		}
			logDebug("BBBProfileManager.updatePassword() method ends");
		
		return false;
	}

	/**
	 * Update the user's isLoggedIn property
	 * 
	 * @param mEmail
	 * @param mPassword
	 * @return boolean
	 */
	public boolean updateIsLoggedInProp(String pEmail) {
		
			logDebug("BBBProfileManager.updateIsLoggedInProp() method starts");
		
		if (BBBUtility.isNotEmpty(pEmail)) {

			RepositoryItem bbbUserProfile = mTools.getItemFromEmail(pEmail
					.toLowerCase());
			try {
				if (bbbUserProfile != null) {
					MutableRepositoryItem mutableBBBProfile = mTools
							.getProfileRepository().getItemForUpdate(
									bbbUserProfile.getRepositoryId(),
									bbbUserProfile.getItemDescriptor()
											.getItemDescriptorName());
					mutableBBBProfile.setPropertyValue(mPmgr.getLoggedIn(),
							true);
					mTools.getProfileRepository().updateItem(mutableBBBProfile);
					ItemDescriptorImpl profileItemDescriptor = (ItemDescriptorImpl) bbbUserProfile
							.getItemDescriptor();
					profileItemDescriptor.removeItemFromCache(bbbUserProfile
							.getRepositoryId());
					
						logDebug("BBBProfileManager.updateIsLoggedInProp() method ends");
					
					return true;
				}
			} catch (RepositoryException re) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException BBBProfileManager.updateIsLoggedInProp()", BBBCoreErrorConstants.ACCOUNT_ERROR_1164 ), re);
			}
		}
		
			logDebug("BBBProfileManager.updateIsLoggedInProp() method ends");
		

		return false;
	}
	
	/**
	 * This method will set email parameters for Profile Registration Email Notification
	 * in the ProfileEmailRequestVO
	 * 
	 * @param Map
	 * @return void
	 */
	public void sendProfileRegistrationTibcoEmail(final Map<String, Object> emailParams) throws BBBSystemException,
			BBBBusinessException {
		String methodName = "sendProfileRegistrationTibcoEmail";
		logDebug(methodName + " method started");

		final RegistrationEmailRequestVO emailRequestVO = new RegistrationEmailRequestVO();
		if (emailParams.get(BBBCoreConstants.EMAIL_TYPE) != null) {
			emailRequestVO.setEmailType(emailParams.get(BBBCoreConstants.EMAIL_TYPE).toString());
		}
		if (emailParams.get(BBBCoreConstants.FORM_SITE) != null) {
			emailRequestVO.setSiteFlag(emailParams.get(BBBCoreConstants.FORM_SITE).toString());
		}
		if (emailParams.get(BBBCoreConstants.FORM_FNAME) != null) {
			emailRequestVO.setFirstName(emailParams.get(BBBCoreConstants.FORM_FNAME).toString());
		}
		if (emailParams.get(BBBCoreConstants.FORM_LNAME) != null) {
			emailRequestVO.setLastName(emailParams.get(BBBCoreConstants.FORM_LNAME).toString());
		}
		if ((emailParams.get(BBBCoreConstants.FORM_EMAIL) != null)) {
			emailRequestVO.setEmailAddress(emailParams.get(BBBCoreConstants.FORM_EMAIL).toString());
		}
		if ((emailParams.get(BBBCoreConstants.FORM_PHONE_NUMBER) != null)) {
			emailRequestVO.setPhone1(emailParams.get(BBBCoreConstants.FORM_PHONE_NUMBER).toString());
		}
		if ((emailParams.get(BBBCoreConstants.FORM_MOBILE_NUMBER) != null)) {
			emailRequestVO.setPhone2(emailParams.get(BBBCoreConstants.FORM_MOBILE_NUMBER).toString());
		}
		if ((emailParams.get(BBBCoreConstants.FORM_OPTIN) != null)) {
			emailRequestVO.setOptin(emailParams.get(BBBCoreConstants.FORM_OPTIN).toString());
		}
		if ((emailParams.get(BBBCoreConstants.FORM_PROFILEID) != null)) {
			emailRequestVO.setProfileId(emailParams.get(BBBCoreConstants.FORM_PROFILEID).toString());
		}
		if ((emailParams.get(BBBCoreConstants.FORM_SHARE_ACCOUNT) != null)) {
			emailRequestVO.setShareAccount(emailParams.get(BBBCoreConstants.FORM_SHARE_ACCOUNT).toString());
		}
		if (emailParams.get(BBBCoreConstants.REQUESTED_DT_PARAM_NAME) != null) {
			emailRequestVO.setSubmitDate((XMLGregorianCalendar) emailParams.get(BBBCoreConstants.REQUESTED_DT_PARAM_NAME));
		}
		if ((emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME) != null) && (!emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME).toString().equals(BBBCoreConstants.BLANK))) {
			emailRequestVO.setSiteFlag(emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME).toString());
		}
		
		logDebug("Email Type :" + emailRequestVO.getEmailType());			
		logDebug("Site Flag :" + emailRequestVO.getSiteFlag());			
		logDebug("Sender First Name :" + emailRequestVO.getFirstName());
		logDebug("Sender Last Name :" + emailRequestVO.getLastName());
		logDebug("Recipient Email Address :" + emailRequestVO.getEmailAddress());			
		logDebug("Phone Number" + emailRequestVO.getPhone1());
		logDebug("Mobile Number :" + emailRequestVO.getPhone2());			
		logDebug("Opt in :" + emailRequestVO.getOptin());			
		logDebug("Profile Id :" + emailRequestVO.getProfileId());			
		logDebug("Share Account :" + emailRequestVO.getShareAccount());			
		logDebug("Submitted Date :" + emailRequestVO.getSubmitDate());
		
		logDebug("sending Profile Registration Tibco with Email " + emailRequestVO.getEmailAddress() 
				+ " and ID " + emailRequestVO.getProfileId() + " and submitted date " + emailRequestVO.getSubmitDate());

		BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL,	methodName);

		try {
			ServiceHandlerUtil.send(emailRequestVO);
		} catch (BBBSystemException ex) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException BBBProfileManager.sendProfileRegistrationTibcoEmail", BBBCoreErrorConstants.ACCOUNT_ERROR_1165 ), ex);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1165,ex.getMessage(), ex);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);			
		}
		logDebug(methodName + " method ends");
	}	

	
	/**
	 * This method will update the user registry in BBB system using the update registry webservice 
	 * 
	 * @param pProfileItem
	 * @param pSiteId
	 * @param profileSyncRequestVO
	 * @return boolean
	 */
	public boolean updateRegistryWithAtgInfo(RepositoryItem pProfileItem, String pSiteId, ProfileSyncRequestVO profileSyncRequestVO)
			throws BBBSystemException, BBBBusinessException {

		logDebug("BBBProfileManager : updateRegistryWithAtgInfo method Start");

		boolean success = true;

		profileSyncRequestVO.setServiceName(UPDATE_REGISTRY_WITH_ATG_INFO);
		String userToken = BBBCoreConstants.BLANK;
		try {
			List<String> userTokenList = this.getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN);
			if (userTokenList != null && !userTokenList.isEmpty()) {
				userToken = userTokenList.get(0);
				logDebug("userToken is : " + userToken);
			}
		} catch (Exception e) {
			logError(LogMessageFormatter.formatMessage(null, "WebServiceUserToken not found ", BBBCoreErrorConstants.ACCOUNT_ERROR_1166 ), e);
		}
		String siteFlag = BBBCoreConstants.BLANK;
		try {
			List<String> siteFlagList = this.getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId);
			if (siteFlagList != null && !siteFlagList.isEmpty()) {
				siteFlag = siteFlagList.get(0);
				logDebug("siteFlag is : " + siteFlag);
			}
		} catch (Exception e) {
			logError(LogMessageFormatter.formatMessage(null, "WSDLSiteFlags not found ", BBBCoreErrorConstants.ACCOUNT_ERROR_1167 ), e);
		}
		profileSyncRequestVO.setUserToken(userToken);
		profileSyncRequestVO.setSiteFlag(siteFlag);
		if (getGiftRegistryManager() != null) {
			ProfileSyncResponseVO profileSyncResponseVO = getGiftRegistryManager().updateRegistryWithAtgInfo(profileSyncRequestVO);
			if (profileSyncResponseVO.getErrorStatus().isErrorExists()) {
				success = false;
			}
		}

		logDebug("BBBProfileManager : updateRegistryWithAtgInfo method End");
		return success;
	}

	
	/**
	 * R2.2
	 * This method is used to create bazaar voice user token using the email ID or MD5 hash of the email Id
	 * @param emailId
	 * @return
	 * @throws BBBBusinessException
	 */
	public String getBVUserToken(String emailId, boolean isGenerateMD5, String bvEmailId) throws BBBBusinessException {
		String userTokenBVRR = null;
        try {
        	//PS-18177. Updated UAS to generate token for BV
        	BBBPerformanceMonitor.start(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, "getBVUserToken");
        	if(isGenerateMD5){
                userTokenBVRR = BazaarVoiceUtil.createUserTokenBVRR(BazaarVoiceUtil.generateMD5((getEmailMD5HashPrefix()+emailId).toLowerCase()) ,this.getCatalogTools().getBazaarVoiceKey(),bvEmailId);
        	} else {
                userTokenBVRR = BazaarVoiceUtil.createUserTokenBVRR(emailId,this.getCatalogTools().getBazaarVoiceKey(),bvEmailId);
        	}
            if (userTokenBVRR.equalsIgnoreCase(BBBCoreConstants.BAZAAR_VOICE_ERROR)) {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, "getBVUserToken");
                throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1275,
                        BBBCoreConstants.BAZAAR_VOICE_ERROR);
            }
        } catch (final UnsupportedEncodingException exp) {
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, "getBVUserToken");
            throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1275,
                    BBBCoreConstants.BAZAAR_VOICE_ERROR, exp);
        } catch (final BBBSystemException sysExp) {
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, "getBVUserToken");
            throw new BBBBusinessException(sysExp.getErrorCode(), BBBCoreConstants.BAZAAR_VOICE_ERROR, sysExp);
        } catch (BBBBusinessException busExp) {
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, "getBVUserToken");
        	throw new BBBBusinessException(busExp.getErrorCode(), BBBCoreConstants.BAZAAR_VOICE_ERROR, busExp);
		} finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, "getBVUserToken");
        }
        return userTokenBVRR;
	}
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	public MutableRepositoryItem getProfileByEmailId(String pEmail) {
		
			logDebug("BBBProfileTools.getProfileByEmailId() method started");
		
		MutableRepositoryItem userItemFromEmail = (MutableRepositoryItem) getTools().getItemFromEmail(pEmail);

		
			logDebug("BBBProfileTools.getProfileByEmailId() method Ends");
		
		return userItemFromEmail;
	}
	
	/*
	Checks if the email is already associate with ATG Repository
	*/
	
	public boolean isDuplicateEmailAddress(String pEmail) {
		
			logDebug("BBBProfileTools.isDuplicateEmailAddress() method started and input param email\t:"+pEmail);
		
		boolean isDuplicate = false;

		MutableRepositoryItem userItemFromEmail =  getProfileByEmailId(pEmail);

		if (userItemFromEmail != null) {
				isDuplicate = true;
		}
		
			logDebug("BBBProfileTools.isDuplicateEmailAddress() method Ends and isDuplicate?\t:"+isDuplicate);
		
		return isDuplicate;
	}
	
	
	/**
	 * <p>
	 * dkhadka:
	 * This will perform the below logic.
	 * <li>It will retrieve profile repository item for given profileId
	 * <li>If profileId and item  is null no action will be taken,Otherwise walletId would be added with existing profile. 
	 * </p>
	 * @param profileId
	 * @param walletId
	 */
	
	public void addWalletIdToProfile(final String profileId, String walletId){		
		if(isLoggingDebug()){			
			logDebug("BBBProfileManager.addWalletItToProfile() method Starts.. \n"
					+ "ProfileId and WalletId " + profileId+"\t"+walletId);
		}
		MutableRepository profileRepos =  ((BBBProfileTools) getTools()).getProfileRepository();
		if(null==profileId)return;
		try {			
			MutableRepositoryItem profileItem= profileRepos.getItemForUpdate(profileId,
					"user");			
			if(null!=profileItem){					
				profileItem.setPropertyValue(mPmgr.getWalletIdPropertyName(), walletId);				
				profileRepos.updateItem(profileItem);				
				if(isLoggingDebug()) logDebug(profileItem.getPropertyValue(mPmgr.getWalletIdPropertyName()) + ": wallet_id is successfully persisted for this profileItem...");
			}
		}catch(RepositoryException e){
			logError(LogMessageFormatter.formatMessage(null, "RepositoryException BBBProfileManager.addWalletIdToProfile()", 
					BBBCoreErrorConstants.COUPON_WALLET_ERROR_1004), e);	
		}
		if(isLoggingDebug())logDebug("BBBProfileManager.addWalletItToProfile() method end...");			
	}
	
	/**
	 * dkhadka
	 * <p>
	 * Shallow Profile for given email will be created from this createShallowProfile() method...
	 * @param email 
	 * @param walletId
	 * @return shallowProfile:RepositoryItem
	 * </p>
	 */
	public RepositoryItem  createShallowProfile(final String email,final String walletId,String profileSourceType){
		
			logDebug("BBBProfileManager.createShallowProfile() method started...");
			logDebug("For mailID\t:"+email+" and WalletID\t:"+walletId);			
						
		RepositoryItem shallowProfile=null;
		try {
			MutableRepository repository=  getTools().getProfileRepository();
			if(null==repository)
				return null;
			MutableRepositoryItem profileItem=  repository.createItem(getTools().getDefaultProfileType());
			profileItem.setPropertyValue(getPmgr().getLoginPropertyName(), email);
			profileItem.setPropertyValue(getPmgr().getPasswordPropertyName(),getTools().getPasswordGenerator().generatePassword());
			profileItem.setPropertyValue(getPmgr().getEmailAddressPropertyName(), email);
			profileItem.setPropertyValue(getPmgr().getStatusPropertyName(), BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE);
			profileItem.setPropertyValue(mPmgr.getWalletIdPropertyName(), walletId);
			if(null!=profileSourceType){
				profileItem.setPropertyValue(getPmgr().getSource(), profileSourceType);
			}
			shallowProfile=repository.addItem(profileItem);			
			
				if(isLoggingDebug()){
					logDebug("Shallow profile successfully created for\t:"+shallowProfile);
				}
				logDebug("BBBProfileManager.createShallowProfile() method end...");				
			
		} catch (RepositoryException e) {
					
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException:: BBBProfileManager.createShallowProfile() Error while creating a Shallow Profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1159 ), e);
			
		}
		
		return shallowProfile;		
	}
	
	/**
	 * This method checks wallet id to profile association, and calls getProfile service
	 * if association is not found.
	 * 
	 * @param walletId
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public RepositoryItem createShallowProfile(String walletId) throws BBBSystemException, BBBBusinessException {
		logDebug("Entering in BBBProfileManager.createShallowProfile walletId::" + walletId);

		BBBGetProfileRequest getProfileReq = new BBBGetProfileRequest();
		
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size()>BBBCoreConstants.ZERO) {
			getProfileReq.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(BBBCoreConstants.ZERO));
		} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1269,"Webservices Token for webservices is not set");
		}

		getProfileReq.setWalletId(walletId);
		getProfileReq.setServiceName(GET_PROFILE);

		BBBGetProfileResponse getProfileRes = null;
		try {
			getProfileRes = (BBBGetProfileResponse) ServiceHandlerUtil.invoke(getProfileReq);
		} catch (BBBBusinessException | BBBSystemException e) {
			logError("Exception in getting profile info for walletId::" + walletId, e);
		}

		logDebug("Exiting from BBBProfileManager.createShallowProfile with getProfileRes::" + getProfileRes);
		if (null != getProfileRes && StringUtils.isNotBlank(getProfileRes.getEmail())) {
			//create shallow profile if there is no profile exist for this email
			//Update walletId in profile
			MutableRepositoryItem profile = getProfileByEmailId(getProfileRes.getEmail());
			if (profile != null) {
				addWalletIdToProfile(profile.getRepositoryId(), walletId);
				return profile;
			} else {
				//create shallow profile if no associated profile found for the email
				return createShallowProfile(getProfileRes.getEmail(), walletId, null);
			}
		} else {
			return null;
		}
	}

	/**
	 * This method updates profile id in order for shallow profile
	 * @param email
	 * @param order
	 */
	public void updateOrderForCurrentProfile(final String email, Order order) {
		logDebug("BBBProfileManager.updateOrderForCurrentProfile start");
		String orderProfileId = order.getProfileId();
    	RepositoryItem profileItem = this.getTools().getItemFromEmail(email);
    	synchronized (order) {
	    	if(!BBBUtility.isEmpty(orderProfileId) && profileItem != null && profileItem.getRepositoryId() != null){
	    		String profileId = profileItem.getRepositoryId();
	    		logDebug("Order associate with Profile Id " + orderProfileId);
	    		logDebug("User Profile Id  " + profileId);
	    		if(!orderProfileId.equalsIgnoreCase(profileId)){
	    			order.setProfileId(profileId);
	    			try {
	    				getOrderManager().updateOrder(order);
	    			} catch (CommerceException e) {
	    				final String msg = "Commerce Exception while updating the Order profile id for order ["+order.getId()+"] in updateOrderForCurrentProfile() in BBBProfileManager";
	    				this.logError(msg, e);
	    			}
	    		}
	    	}
    	}
    	logDebug("BBBProfileManager.updateOrderForCurrentProfile ends.");
	}
	
	//utlity method to resolve session bean
	public static BBBSessionBean resolveSessionBean(DynamoHttpServletRequest pRequest){
		BBBSessionBean sessionBean = null;
		
		DynamoHttpServletRequest currentRequest = pRequest;
		if (currentRequest ==null){
			currentRequest = ServletUtil.getCurrentRequest();
		}
		
		if(currentRequest!=null){
			sessionBean = (BBBSessionBean) currentRequest.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME);
			if( sessionBean == null){
				sessionBean = (BBBSessionBean) currentRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
				currentRequest.setAttribute(BBBCoreConstants.SESSION_BEAN_NAME, sessionBean);	
			}
		}
		
		return sessionBean;
	}
}

