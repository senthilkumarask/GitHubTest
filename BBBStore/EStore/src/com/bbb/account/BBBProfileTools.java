/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBProfileTools.java
 *
 *  DESCRIPTION: BBBProfileTools extends ATG OOTB CommerceProfileTools
 *  			 and perform repository related validations and operations. 	
 *  HISTORY:
 *  10/14/11 Initial version
 *	13/12/2011 Legacy reclaiming process, isDuplicateEmailAddress(pEmail)- checks the sister sites and createSiteItem
 *             updates the member id in the table for the reclaimed legacy user
 */
package com.bbb.account;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.util.PlaceUtils;
import atg.core.util.Address;
import atg.core.util.Base64;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteGroup;
import atg.multisite.SiteGroupManager;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.lockmanager.DeadlockException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBEximConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.email.BBBTemplateEmailSender;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;
import com.bbb.social.facebook.FBConstants;
import com.bbb.social.facebook.FBProfileTools;
import com.bbb.utils.BBBUtility;

public class BBBProfileTools extends CommerceProfileTools {

	private static final String EMAIL = "email";
	private static final String WALLET_ID_SHALLOW_PROFILE = "walletIdShallowProfile";
	private static final String EXISTING_PROFILE = "existingProfile";
	private static final String COM_BBB_PROFILE_SESSION_SESSION_BEAN = "/com/bbb/profile/session/SessionBean";
	private boolean mSendEmailInSeparateThread;
	private boolean mPersistEmails;
	private BBBTemplateEmailSender mTemplateEmailSender;
	private BBBPropertyManager mPmgr;
	private SiteGroupManager mSiteGroup;
	private BBBCatalogTools mCatalogTools;
	private int maxFailAttemptCount = 3;
	private int unlockTimeInterval = 1;
	private FBProfileTools fbProfileTools;
	private BBBEximManager eximManager;
	private BBBGetCouponsManager couponsManager;
	private MutableRepository challengeQuestionRepository;
	private Repository challengeQuestionCatalogRepository;
	private TransactionManager mTransactionManager;

	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	public void setTransactionManager(TransactionManager pTransactionManager) {
		this.mTransactionManager = pTransactionManager;
	}

	public BBBGetCouponsManager getCouponsManager() {
		return couponsManager;
	}

	public void setCouponsManager(BBBGetCouponsManager couponsManager) {
		this.couponsManager = couponsManager;
	}

	/**
	 * @return the challengeQuestionRepository
	 */
	public MutableRepository getChallengeQuestionRepository() {
		return challengeQuestionRepository;
	}

	/**
	 * @param challengeQuestionRepository the challengeQuestionRepository to set
	 */
	public void setChallengeQuestionRepository(
			MutableRepository challengeQuestionRepository) {
		this.challengeQuestionRepository = challengeQuestionRepository;
	}
	/**
	 * @return the eximPricingManager
	 */
	public BBBEximManager getEximManager() {
		return eximManager;
	}

	/**
	 * @param eximPricingManager the eximPricingManager to set
	 */
	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}
	
	/**
	 * @return the fbProfileTools
	 */
	public FBProfileTools getFbProfileTools() {
		return fbProfileTools;
	}

	/**
	 * @param fbProfileTools the fbProfileTools to set
	 */
	public void setFbProfileTools(FBProfileTools fbProfileTools) {
		this.fbProfileTools = fbProfileTools;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	
	/**
	 * @return the maxFailAttemptCount
	 */
	public int getMaxFailAttemptCount() {
		try {
			List<String> maxFailAttemptCountList = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "maxFailAttemptCount");
			if (maxFailAttemptCountList != null && !maxFailAttemptCountList.isEmpty()) {
				maxFailAttemptCount = Integer.parseInt(maxFailAttemptCountList.get(0));
				logDebug("maxFailAttemptCount  : " + maxFailAttemptCount);
			}
		} catch (Exception e) {
			logError(LogMessageFormatter.formatMessage(null, "Exception - maxFailAttemptCount Key not found", BBBCoreErrorConstants.ACCOUNT_ERROR_1009 ), e);
		}
		return maxFailAttemptCount;
	}

	/**
	 * @return the unLockTimeInterval
	 */
	public int getUnLockTimeInterval() {
		try {
			List<String> unlockTimeIntervalList = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "unlockTimeInterval");
			if (unlockTimeIntervalList != null && !unlockTimeIntervalList.isEmpty()) {
				unlockTimeInterval = Integer.parseInt(unlockTimeIntervalList.get(0));
				logDebug("UnlockTimeInterval  : " + unlockTimeInterval);
			}
		} catch (Exception e) {
			logError(LogMessageFormatter.formatMessage(null, "Exception - pUnlockTimeInterval Key not found", BBBCoreErrorConstants.ACCOUNT_ERROR_1009 ), e);
		}

		return unlockTimeInterval;
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
	 * @return the mSendEmailInSeparateThread
	 */
	public boolean isSendEmailInSeparateThread() {
		return mSendEmailInSeparateThread;
	}

	/**
	 * @param mSendEmailInSeparateThread
	 *            the mSendEmailInSeparateThread to set
	 */
	public void setSendEmailInSeparateThread(boolean mSendEmailInSeparateThread) {
		this.mSendEmailInSeparateThread = mSendEmailInSeparateThread;
	}

	/**
	 * @return the mTemplateEmailSender
	 */
	public BBBTemplateEmailSender getTemplateEmailSender() {
		return mTemplateEmailSender;
	}

	/**
	 * @param mTemplateEmailSender
	 *            the mTemplateEmailSender to set
	 */
	public void setTemplateEmailSender(
			BBBTemplateEmailSender mTemplateEmailSender) {
		this.mTemplateEmailSender = mTemplateEmailSender;
	}

	/**
	 * @return the mPersistEmails
	 */
	public boolean isPersistEmails() {
		return mPersistEmails;
	}

	/**
	 * @param mPersistEmails
	 *            the mPersistEmails to set
	 */
	public void setPersistEmails(boolean mPersistEmails) {
		this.mPersistEmails = mPersistEmails;
	}

	/**
	 * Place utils helper
	 */
	private PlaceUtils mPlaceUtils;

	/**
	 * 
	 * @return place utils
	 */
	public PlaceUtils getPlaceUtils() {
		return mPlaceUtils;
	}

	/**
	 * Sets place utils
	 * 
	 * @param pPlaceUtils
	 */
	public void setPlaceUtils(PlaceUtils pPlaceUtils) {
		mPlaceUtils = pPlaceUtils;
	}

	// --------------------------------------------------
	// property: ShippingAddressClassName
	private String mShippingAddressClassName = "atg.core.util.ContactInfo";

	/**
	 * @return the String
	 */
	public String getShippingAddressClassName() {
		return mShippingAddressClassName;
	}

	/**
	 * @param pShippingAddressClassName 
	 */
	public void setShippingAddressClassName(String pShippingAddressClassName) {
		mShippingAddressClassName = pShippingAddressClassName;
	}

	// --------------------------------------------------
	// property: BillingAddressClassName
	private String mBillingAddressClassName = "atg.core.util.ContactInfo";

	/**
	 * @return the String
	 */
	public String getBillingAddressClassName() {
		return mBillingAddressClassName;
	}

	/**
	 * Method to call appropriate createSiteItem method based on the value of parameter emailOptInBaby
	 * @param pEmailId
	 * @param pSiteId
	 * @param pMemberId
	 * @param pFavStore
	 * @param emailOptIn
	 * @param emailOptInBaby
	 * @param profile
	 * @throws BBBSystemException
	 */
	public void createSiteItemRedirect(String pEmailId, String pSiteId,
			String pMemberId, String pFavStore, String emailOptIn, String emailOptIn_BabyCA, RepositoryItem... profile) throws BBBSystemException
	{
		if((BBBCoreConstants.SITE_BAB_CA).equalsIgnoreCase(pSiteId))
		{
			createSiteItem(pEmailId,  pSiteId,
					 pMemberId,  pFavStore,  emailOptIn, emailOptIn_BabyCA, profile);
		}
		else
		{
			createSiteItem(pEmailId,  pSiteId,
					 pMemberId,  pFavStore,  emailOptIn, profile);
		}
	}
	/**
	 * 1. call mutualrepository.mutualrepository update item method to commit
	 * changes. 2. get userItems map from profile 3. if userItem for a siteId is
	 * present then get it for update else add userItem in hash map withsiteId
	 * as key and userItem created as value. 4. set Property value in profile
	 * for userItems map 5. call mutualrepository.mutualrepository update item
	 * method to commit changes.
	 * 
	 * * UC_Create_Profile
	 * 
	 * @param pEmailId
	 * @param pSiteId
	 * @param pMemberId 
	 * @param pFavStore 
	 * @param emailOptIn 
	 * @param profile 
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createSiteItem(String pEmailId, String pSiteId,
			String pMemberId, String pFavStore, String emailOptIn, String emailOptIn_BabyCA, RepositoryItem... profile) throws BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.createSiteItem() method started");
			logDebug("BBBProfileTools.createSiteItem() Parameters: pEmailId=" + pEmailId + ", pSiteId=" + pSiteId + ", pMemberId=" + pMemberId + ", pFavStore" + pFavStore + ", emailOptIn=" + emailOptIn + ", emailOptIn_BabyCA=" + emailOptIn_BabyCA);
		}
		BBBPerformanceMonitor.start(BBBPerformanceConstants.BBB_PROFILE_TOOLS,
				"createSiteItem");
		String favStore = pFavStore;
		if("null".equalsIgnoreCase(favStore)){
			favStore = "";
		}
		if (pEmailId != null && pSiteId != null) {
			MutableRepository siteItemsRepository = getProfileRepository();

			// create userSite Association Item in Profile repository
			MutableRepositoryItem userSiteAssoc;
			try {
				userSiteAssoc = (MutableRepositoryItem) siteItemsRepository
						.createItem(BBBCoreConstants.USER_SITE_ASSOCIATION_REPOSITORY_ITEM);
				userSiteAssoc.setPropertyValue(getPmgr()
						.getSiteIdPropertyName(), pSiteId);
				userSiteAssoc.setPropertyValue(getPmgr()
						.getMemberIdPropertyName(), pMemberId);
				userSiteAssoc.setPropertyValue(getPmgr()
						.getFavouriteStoreIdPropertyName(), favStore);
				if(BBBUtility.isNotEmpty(emailOptIn)){
					if(emailOptIn != null && emailOptIn.equalsIgnoreCase(BBBCoreConstants.NO)){
						userSiteAssoc.setPropertyValue(getPmgr().getEmailOptInPropertyName(), Integer.valueOf (0));
					}
					if(pSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) && StringUtils.isNotEmpty(emailOptIn) && emailOptIn.equalsIgnoreCase(BBBCoreConstants.YES)){
						userSiteAssoc.setPropertyValue(getPmgr().getEmailOptInPropertyName(), Integer.valueOf (1));
					}
				}
				siteItemsRepository.addItem(userSiteAssoc);
				
				// create userSite Association Item in Profile repository
				MutableRepositoryItem userSiteAssoc_baby = (MutableRepositoryItem) siteItemsRepository
						.createItem(BBBCoreConstants.USER_SITE_ASSOCIATION_REPOSITORY_ITEM);;
				userSiteAssoc_baby.setPropertyValue(getPmgr()
						.getSiteIdPropertyName(), BBBCoreConstants.BABY_CANADA_SITE);
				userSiteAssoc_baby.setPropertyValue(getPmgr()
						.getMemberIdPropertyName(), pMemberId);
				userSiteAssoc_baby.setPropertyValue(getPmgr()
						.getFavouriteStoreIdPropertyName(), favStore);
				if(BBBUtility.isNotEmpty(emailOptIn_BabyCA)){
					if(emailOptIn_BabyCA.equalsIgnoreCase(BBBCoreConstants.NO)){
						userSiteAssoc_baby.setPropertyValue(getPmgr().getEmailOptInPropertyName(), Integer.valueOf (0));
					}
					if(pSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) && emailOptIn_BabyCA.equalsIgnoreCase(BBBCoreConstants.YES)){
						userSiteAssoc_baby.setPropertyValue(getPmgr().getEmailOptInPropertyName(), Integer.valueOf (1));
					}
				}
				siteItemsRepository.addItem(userSiteAssoc_baby);
				
				
				// Add the created Item to the repository.
				
				MutableRepositoryItem userItem = null;
				
				if(profile != null && profile.length > 0){
					userItem = (MutableRepositoryItem)profile[0];
				}else{
					userItem= (MutableRepositoryItem) getItemFromEmail(pEmailId);
				}

				Map userSiteItemsMap = (Map) userItem
						.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);

				if (userSiteItemsMap == null) {
					userSiteItemsMap = new HashMap();
				}

				userSiteItemsMap.put(pSiteId, userSiteAssoc);
				userSiteItemsMap.put(BBBCoreConstants.BABY_CANADA_SITE, userSiteAssoc_baby);
				
				userItem.setPropertyValue(
						BBBCoreConstants.USER_SITE_REPOSITORY_ITEM,
						userSiteItemsMap);

				// call mutablerepository update item method to
				// commit changes.
				siteItemsRepository
						.updateItem(userItem);
				
				if (isLoggingDebug()) {
					logDebug("BBBProfileTools.createSiteItem() method Ends");
				}
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "RepositoryException - BBBProfileTools.createSiteItem()", BBBCoreErrorConstants.ACCOUNT_ERROR_1168 ), e);
				}
				BBBPerformanceMonitor.cancel(BBBPerformanceConstants.BBB_PROFILE_TOOLS,
						"createSiteItem");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1276,"errorCreatingSiteItem", e);
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.BBB_PROFILE_TOOLS,
				"createSiteItem");
	}
	
	/**
	 * 1. call mutualrepository.mutualrepository update item method to commit
	 * changes. 2. get userItems map from profile 3. if userItem for a siteId is
	 * present then get it for update else add userItem in hash map withsiteId
	 * as key and userItem created as value. 4. set Property value in profile
	 * for userItems map 5. call mutualrepository.mutualrepository update item
	 * method to commit changes.
	 * 
	 * * UC_Create_Profile
	 * 
	 * @param pEmailId
	 * @param pSiteId
	 * @param pMemberId 
	 * @param pFavStore 
	 * @param emailOptIn 
	 * @param profile 
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createSiteItem(String pEmailId, String pSiteId,
			String pMemberId, String pFavStore, String emailOptIn, RepositoryItem... profile) throws BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.createSiteItem() method started");
			logDebug("BBBProfileTools.createSiteItem() Parameters: pEmailId=" + pEmailId + ", pSiteId=" + pSiteId + ", pMemberId=" + pMemberId + ", pFavStore" + pFavStore + ", emailOptIn=" + emailOptIn);
		}
		BBBPerformanceMonitor.start(BBBPerformanceConstants.BBB_PROFILE_TOOLS,
				"createSiteItem");
		String favStore = pFavStore;
		if("null".equalsIgnoreCase(favStore)){
			favStore = "";
		}
		if (pEmailId != null && pSiteId != null) {
			MutableRepository siteItemsRepository = getProfileRepository();

			// create userSite Association Item in Profile repository
			MutableRepositoryItem userSiteAssoc;
			try {
				userSiteAssoc = (MutableRepositoryItem) siteItemsRepository
						.createItem(BBBCoreConstants.USER_SITE_ASSOCIATION_REPOSITORY_ITEM);
				userSiteAssoc.setPropertyValue(getPmgr()
						.getSiteIdPropertyName(), pSiteId);
				userSiteAssoc.setPropertyValue(getPmgr()
						.getMemberIdPropertyName(), pMemberId);
				userSiteAssoc.setPropertyValue(getPmgr()
						.getFavouriteStoreIdPropertyName(), favStore);
				if(emailOptIn != null && emailOptIn.equalsIgnoreCase(BBBCoreConstants.YES)){
					userSiteAssoc.setPropertyValue(getPmgr().getEmailOptInPropertyName(), Integer.valueOf (1));
				}
				if(pSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
					userSiteAssoc.setPropertyValue(getPmgr().getEmailOptInPropertyName(), Integer.valueOf (0));
				}
				
				// Add the created Item to the repository.
				siteItemsRepository.addItem(userSiteAssoc);
				
				MutableRepositoryItem userItem = null;
				
				if(profile != null && profile.length > 0){
					userItem = (MutableRepositoryItem)profile[0];
				}else{
					userItem= (MutableRepositoryItem) getItemFromEmail(pEmailId);
				}

				Map userSiteItemsMap = null;
				if( null != userItem){
					userSiteItemsMap = (Map) userItem.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);
				}

				if (userSiteItemsMap == null) {
					userSiteItemsMap = new HashMap();
				}

				userSiteItemsMap.put(pSiteId, userSiteAssoc);

				if( null != userItem){
					userItem.setPropertyValue(
							BBBCoreConstants.USER_SITE_REPOSITORY_ITEM,
							userSiteItemsMap);
				}

				// call mutablerepository update item method to
				// commit changes.
				siteItemsRepository
						.updateItem(userItem);

				if (isLoggingDebug()) {
					logDebug("BBBProfileTools.createSiteItem() method Ends");
				}
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "RepositoryException - BBBProfileTools.createSiteItem()", BBBCoreErrorConstants.ACCOUNT_ERROR_1168 ), e);
				}
				BBBPerformanceMonitor.cancel(BBBPerformanceConstants.BBB_PROFILE_TOOLS,
						"createSiteItem");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1276,"errorCreatingSiteItem", e);
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.BBB_PROFILE_TOOLS,
				"createSiteItem");
	}
	
	/**
	 * Checks if the user is already associated with same site group
	 * or different site group, also check if user is a migrated user or not.
	 * 
	 * @param pEmail
	 * @return Registration Description
	 * @throws BBBBusinessException 
	 */
	public String checkForRegistration(String pEmail) throws BBBBusinessException {
		String status = "";
		String returnStatus = "";
	    boolean legacyUserStatus = false;

	    if(BBBUtility.isEmpty(pEmail)){			
						
					throw new BBBBusinessException("err_email_empty", "Email address is empty or null");				
			}			
		else {		
				RepositoryItem bbbProfile = getItemFromEmail(pEmail.toLowerCase());
				if(null == bbbProfile || isProfileStatusShallow(pEmail)){
					//profile not found
						returnStatus= BBBCoreConstants.PROFILE_NOT_FOUND; 
						
				} else {
					String profileType = (String) bbbProfile.getPropertyValue(BBBCoreConstants.STATUS);
					if((profileType == null )|| (profileType!=null && (profileType ==null || profileType.equalsIgnoreCase(BBBCoreConstants.FULL_PROFILE_STATUS_VALUE)))){
					status = fbProfileTools.findUserSiteAssociation(bbbProfile, SiteContextManager.getCurrentSiteId());
					legacyUserStatus = isLegacyUserLoggedIn(bbbProfile);
					}
				}
				
				if(legacyUserStatus && FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP.equals(status)){
					returnStatus = BBBCoreConstants.PROFILE_AVAILABLE_FOR_MIGRATION;
				}else if(!BBBUtility.isEmpty(status)){
					if(FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE.equals(status)){
						returnStatus = BBBCoreConstants.PROFILE_ALREADY_EXIST;	
					} else if(FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP.equals(status)){
						returnStatus = BBBCoreConstants.PROFILE_AVAILABLE_FOR_EXTENSION;	
					} else {
						returnStatus = BBBCoreConstants.PROFILE_ALREADY_EXIST;
					}
				}
				
			}
		return returnStatus;
	}

	/**
	 * This method return true if profile is of legacy user and user not loggedIn, else return false.
	 * 
	 * @param bbbProfile profile of type RepositoryItem
	 * @return true if user is legacy and not logged in, else return false.
	 * 
	 */
	private boolean isLegacyUserLoggedIn(final RepositoryItem bbbProfile) {
		boolean legacyUserStatus = false;
		boolean migratedUserLoginStatus = false;
		// CHECK for MIGRATED ACCOUNT
		boolean isMigratedAccount = ((Boolean) bbbProfile.getPropertyValue(getPmgr().getMigratedAccount())).booleanValue();
		
		if (isMigratedAccount) {
			migratedUserLoginStatus = ((Boolean) bbbProfile.getPropertyValue(this.getPmgr().getLoggedIn())).booleanValue();
			if (!migratedUserLoginStatus) {
				legacyUserStatus = true;
			}

		}
		return legacyUserStatus;
	}
	
	/**
	 * Checks if the user is already associated with this site. If associated
	 * with any other site then ok
	 * 
	 * UC_Create_Profile
	 * 
	 * @param pEmail
	 * @param pSiteId
	 * @return is the email duplicate
	 */
	@SuppressWarnings("rawtypes")
	public boolean isDuplicateEmailAddress(String pEmail, String pSiteId) {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.isDuplicateEmailAddress() method started");
		}
		boolean isDuplicate = false;

		MutableRepositoryItem userItemFromEmail = (MutableRepositoryItem) getItemFromEmail(pEmail);

		if (userItemFromEmail != null) {
			Map userSiteMap = (Map) userItemFromEmail
					.getPropertyValue("userSiteItems");
			if (userSiteMap != null && userSiteMap.containsKey(pSiteId)) {
				isDuplicate = true;
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.isDuplicateEmailAddress() method Ends");
		}
		return isDuplicate;
	}

	/**
	 * Checks if the user is already associated with this site.
	 * 
	 * UC_Create_Profile
	 * 
	 * @param pEmail
	 * @return is Email Address
	 */
	@SuppressWarnings("rawtypes")
	public boolean isDuplicateEmailAddress(String pEmail) {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.isDuplicateEmailAddress() method started");
		}
		boolean isDuplicate = false;

		MutableRepositoryItem userItemFromEmail = (MutableRepositoryItem) getItemFromEmail(pEmail);

		if (userItemFromEmail != null) {
			Map userSiteMap = (Map) userItemFromEmail
					.getPropertyValue("userSiteItems");
			if (userSiteMap != null) {
				isDuplicate = true;
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.isDuplicateEmailAddress() method Ends");
		}
		// System.out.println("Email Address Already Exists in the new BBB site");
		return isDuplicate;
	}

	/**
	 * extracts users from profile repository based on email and returns them
	 * 
	 * UC_Create_Profile
	 * 
	 * @param pLogin
	 * @param pEmail
	 * @param pProfileType
	 * @return Repository Item List
	 * @throws RepositoryException
	 */
	public MutableRepositoryItem[] lookupUsers(String pLogin, String pEmail,
			String pProfileType) throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.lookupUsers() method started");
		}
		MutableRepositoryItem users[] = null;
		if (!BBBUtility.isEmpty(pLogin)) {
			MutableRepositoryItem user = (MutableRepositoryItem) getItem(
					pLogin, null, pProfileType);
			if (user != null) {
				users = new MutableRepositoryItem[1];
				users[BBBCoreConstants.ZERO] = user;
			}
		}
		if (users == null && !BBBUtility.isEmpty(pLogin)) {
			users = (MutableRepositoryItem[]) (MutableRepositoryItem[]) getItemsFromEmail(pEmail);
		}
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.lookupUsers() method Ends");
		}
		return users;
	}

	/**
	 * Sends email to the users passed as input with the template passed in
	 * pTemplateInfo parameter
	 * 
	 * UC_Create_Profile
	 * 
	 * @param users
	 * @param pTemplateParams
	 * @param pTemplateInfo
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("rawtypes")
	public void sendEmail(MutableRepositoryItem users[], Map pTemplateParams,
			TemplateEmailInfoImpl pTemplateInfo) throws BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.sendEmail() method started");
		}
		try {

			for (int i = BBBCoreConstants.ZERO; i < users.length; i++) {

				sendEmailToUser(users[i], isSendEmailInSeparateThread(),
						isPersistEmails(), getTemplateEmailSender(),
						pTemplateInfo, pTemplateParams);
			}

		} catch (TemplateEmailException exc) {
			if (isLoggingError()) {
				logError(LogMessageFormatter.formatMessage(null, "TemplateEmailException - Method BBBProfileTools.sendEmail() throws error in sending Email", BBBCoreErrorConstants.ACCOUNT_ERROR_1169 ), exc);
			}
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1277,"errorSendingEmail", exc);
		}
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.sendEmail() method Ends");
		}

	}

	/**
	 * @param pProfile
	 * @param pAddressName
	 * @return Operation Result
	 * @throws RepositoryException
	 */
	public boolean setDefaultBillingAddress(RepositoryItem pProfile,
			String pAddressName) throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools |setDefaultBillingAddress| Starts");
		}
		RepositoryItem addressItem = StringUtils.isEmpty(pAddressName) ? null
				: getProfileAddress(pProfile, pAddressName);
		updateProperty(getPmgr().getBillingAddressPropertyName(), addressItem,
				pProfile);
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools |setDefaultBillingAddress| Ends");
		}
		return true;
	}

	/**
	 * @return the mPmgr
	 */
	public BBBPropertyManager getPmgr() {
		return mPmgr;
	}

	/**
	 * @param pPmgr the pPmgr to set
	 */
	public void setPmgr(BBBPropertyManager pPmgr) {
		this.mPmgr = pPmgr;
	}

	/**
	 * Override the OOTB method so that we can determine whether or not to merge
	 * a user's cart after they login. If they login from the checkout flow. the
	 * cart is not merged. Otherwise merge the cart with the persisted cart.
	 * <p>
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void postLoginUser(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse, RepositoryItem pProfile,
			OrderHolder pShoppingCart, PricingModelHolder pPricingModelHolder)
			throws javax.servlet.ServletException {
		 
		//GSARepository gsar =  (GSARepository)getOrderManager().getOrderQueries().getOrderTools().getOrderRepository();
		//gsar.setLoggingDebug(true);
		// Check to see if there is a userCheckingOut param in the request
		if (isLoggingDebug()) {
			logDebug("Checking for userCheckingOut request parameter");
		}
		
		long startTime = System.currentTimeMillis();
		String methodName = BBBPerformanceConstants.POST_LOGIN_USER;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
		TransactionManager tm	=	getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();
		boolean shouldRollback = false;
		try{
			
		String requestParam = pRequest.getParameter("userCheckingOut");
		boolean userCreatingRegistry = false;
		if (pRequest.getParameter("userCreatingRegistry") != null) {
			userCreatingRegistry = (Boolean) pRequest.getObjectParameter("userCreatingRegistry");
		}
		BBBOrderManager om = (BBBOrderManager) getOrderManager();
		BBBOrder order = (BBBOrder) pShoppingCart.getCurrent();	
			 //PS-64362 | Fixing the coupon issue by changing the mode of transaction
			 if(tm !=null){
				 td.begin( tm ,TransactionDemarcation.REQUIRED );
				}
		if (requestParam == null && !userCreatingRegistry) {
			// Since there is no param, assume user is logging in as normal
			// and merge their shopping cart as normal
			if (isLoggingDebug()) {
				logDebug("Request parameter not found. Continuing with nornal login.");
			}
			
			//Storing RegistryMap
			Map<String, RegistrySummaryVO> tempRegistryMap = null;
			
			if(pShoppingCart.getCurrent() != null){
				tempRegistryMap = ((BBBOrder)pShoppingCart.getCurrent()).getRegistryMap();
			}
			
			String tempCurrentOrderId = pShoppingCart.getCurrent().getId();
			boolean isCartEmpty = pShoppingCart.isCurrentEmpty();

			super.postLoginUser(pRequest, pResponse, pProfile, pShoppingCart,
					pPricingModelHolder);

			try {
				 order = (BBBOrder) pShoppingCart.getCurrent();
				if (order.getProfileId() != null && isMergeOrders()
						&& !tempCurrentOrderId.equalsIgnoreCase(order.getId())) {
					
						/*Clear the email in billing address so that coupons may be fetched using email in profile*/
						if(order.getBillingAddress() != null && !StringUtils.isBlank(order.getBillingAddress().getEmail())){
							order.getBillingAddress().setEmail("");
							order.getBillingAddress().setPhoneNumber("");
						}
						
						if(order.getPaymentGroupCount() > 0 ){
							try {
								getOrderManager().getPaymentGroupManager().removeAllPaymentGroupsFromOrder(order);
							} catch (CommerceException e) {
								if(isLoggingError()){
									logError(LogMessageFormatter.formatMessage(null, "Failed while removing payment group", BBBCoreErrorConstants.ACCOUNT_ERROR_1170 ), e);
								}
							}
						}
						// Remove the disabled SKUs from order after calling super
						// postLoginUser
						// R2.1 Removed this are part of scope item #150 : Start
						//om.removeDisabledSKUs(order);
						// R2.1 Removed this are part of scope item #150 : End
						// Update the SKU level attributes by reading from catalog
						// for each commerce items
						om.updateSKUPropsFromCatalog(order);
						
						// BBBJ-615 TBS Next Login - Remove checks that require DOM calls : STARTS | Skip the call in case of TBS
						
						if(!pRequest.getContextPath().equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS)){
							// Added this for updating Availability Map in Order object
							om.updateAvailabilityMapInOrder(pRequest, order);
						
						}
						// BBBJ-615 TBS Next Login - Remove checks that require DOM calls : ENDS
						
						
						//Add RegistryMap of Old order to new Order
						((BBBOrder)order).setRegistryMap(tempRegistryMap);
						
						// Setting schoolID and promo code null so that if user does
						// not get schoolPromotion for unsubmitted order.
						((BBBOrderImpl) order).setSchoolCoupon(null);
						((BBBOrderImpl) order).setSchoolId(null);
						

						//It can not be removed
						repriceShoppingCarts(pProfile, pShoppingCart,
								pPricingModelHolder,
								getUserLocale(pRequest, pResponse),
								getRepriceOrderPricingOp());
						
					
				} else if(isCartEmpty) {
					List<CommerceItem> commItems = order.getCommerceItems();
					//Iterate on commerce items and check if atleast one element is LTL then reprice the order to handle the scenrio logout and re login
					for(CommerceItem commItem : commItems){
						if (commItem instanceof BBBCommerceItem){
							if(((BBBCommerceItem) commItem).isLtlItem()){
								repriceOrder(order, pProfile, pPricingModelHolder, getUserLocale(pRequest, pResponse), getRepriceOrderPricingOp());
								break;
							}
						}
					}
				}
			} catch (CommerceException ce) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "CommerceException in BBBProfileFormHandler.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1171 ), ce);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.BBB_PROFILE_TOOLS,
							methodName);
			} catch (IOException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "IOException in BBBProfileFormHandler.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.BBB_PROFILE_TOOLS,
							methodName);
			}

		} else {
			// User is logging in from the checkout flow. Do everything minus
			// the call to loadUserShoppingCartForLogin()
			if (isLoggingDebug()) {
				logDebug("userCheckingOut: " + requestParam);
			}

			if ((pShoppingCart != null) && (pPricingModelHolder != null)) {
				if (isLoggingDebug()) {
					logDebug("Initializing pricingModelHolder");
				}

				pPricingModelHolder.initializePricingModels();

				// set the new owner of the current order and reprice under the
				// new user context.
				// The order is likely transient at this point if the user was
				// shipping anonymously and then
				// decided to login during checkout. So we add the order to the
				// repository here.
				order = (BBBOrder) pShoppingCart.getCurrent();
				order.setProfileId(pProfile.getRepositoryId());

				try {
					repriceOrder(order, pProfile, pPricingModelHolder,
							getUserLocale(pRequest, pResponse),
							getRepriceOrderPricingOp());

					
				} catch (CommerceException e) {
					if (isLoggingError()) {
						logError(LogMessageFormatter.formatMessage(null, "CommerceException in BBBProfileFormHandler.postLoginUser() repriceOrder", BBBCoreErrorConstants.ACCOUNT_ERROR_1173 ), e);
					}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.BBB_PROFILE_TOOLS,
							methodName);
				} catch (ServletException e) {
					if (isLoggingError()) {
						logError(LogMessageFormatter.formatMessage(null, "ServletException in BBBProfileFormHandler.postLoginUser() repriceOrder", BBBCoreErrorConstants.ACCOUNT_ERROR_1174 ), e);
					}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.BBB_PROFILE_TOOLS,
							methodName);
				} catch (IOException e) {
					if (isLoggingError()) {
						logError(LogMessageFormatter.formatMessage(null, "IOException in BBBProfileFormHandler.postLoginUser() repriceOrder", BBBCoreErrorConstants.ACCOUNT_ERROR_1175 ), e);
					}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.BBB_PROFILE_TOOLS,
							methodName);
				}
			}
		}
		
		if (null != pShoppingCart) {
			vlogDebug("shopping cart is not null");
		     order = (BBBOrder) pShoppingCart.getCurrent();
		    order.setDescription(order.getDescription()); //this is a workaround to make this order the latest.
			synchronized(order) {
				if ((order.isTransient()) && (pShoppingCart.isPersistOrders())) {
					persistOrderIfNeeded(order);
				}else{
		    		    om.updateOrder(order);		      
				}
			}
		   //TBS commerce items conversion starts here::
		    vlogDebug("shopping cart has the order :: "+order);
		   
		    AuxiliaryData aux = null;
			BBBCommerceItem bbbItem = null;	
			List<BBBProfileTools.CItemInfo> bbbItemsList = new ArrayList<BBBProfileTools.CItemInfo>();
			List<String> removableItems = new ArrayList<String>();
			CommerceItemManager commerceItemManager = getOrderManager().getCommerceItemManager();
			SKUDetailVO skuDetailVO = null;
			boolean tbsOrderToBeUpdated = false;
			try {
					List<CommerceItem> items = order.getCommerceItems();
					for (CommerceItem cItem : items) {
						 vlogDebug("cItem for the Items is :: "+cItem);
						 //Check for LTLDeliveryChargeCommerceItem and LTLAssemblyFeeCommerceItem created by load order and remove them
						 if(!cItem.getAuxiliaryData().getSiteId().equals(SiteContextManager.getCurrentSiteId())){
						 if (cItem instanceof LTLDeliveryChargeCommerceItem || cItem instanceof LTLAssemblyFeeCommerceItem){
								removableItems.add(cItem.getId());
								tbsOrderToBeUpdated = true;
							} 
						 }
						 if (cItem instanceof GiftWrapCommerceItem
									&& !cItem.getAuxiliaryData().getSiteId().equals(SiteContextManager.getCurrentSiteId())){
								removableItems.add(cItem.getId());
								tbsOrderToBeUpdated = true;
							} else	if (cItem instanceof TBSCommerceItem && 
								!cItem.getAuxiliaryData().getSiteId().equals(SiteContextManager.getCurrentSiteId())) {
							TBSCommerceItem tbsItem = (TBSCommerceItem) cItem;
							 vlogDebug("tbsItem for the Items is :: "+tbsItem);
							 vlogDebug("CMO items :: "+tbsItem.isCMO());
							 vlogDebug("Kirsch items :: "+tbsItem.isKirsch());
							 
							aux = tbsItem.getAuxiliaryData();
							if(tbsItem != null && !(tbsItem.isCMO() ||tbsItem.isKirsch())){
								tbsOrderToBeUpdated = true;
								//Create new commerce item with 
								bbbItem = (BBBCommerceItem) commerceItemManager.createCommerceItem(tbsItem.getCommerceItemClassType(), tbsItem.getCatalogRefId(),
										aux.getCatalogRef(), aux.getProductId(), aux.getProductRef(), tbsItem.getQuantity(),
										tbsItem.getCatalogKey(), tbsItem.getCatalogId(), null, null);
								
								if (tbsItem.isItemMoved()) {
									bbbItem.setCommerceItemMoved(tbsItem.getAuxiliaryData().getProductId() + "," + tbsItem.getStoreId());
								}
								bbbItem.setStoreId(tbsItem.getStoreId());
								bbbItem.setRegistryId(tbsItem.getRegistryId());
								bbbItem.setItemMoved(tbsItem.isItemMoved());
								bbbItem.setRegistryInfo(tbsItem.getRegistryInfo());
								bbbItem.setBts(tbsItem.getBts());
								bbbItem.setVdcInd(tbsItem.isVdcInd());
								bbbItem.setFreeShippingMethod(tbsItem.getFreeShippingMethod());
								bbbItem.setSkuSurcharge(tbsItem.getSkuSurcharge());
								bbbItem.setLastModifiedDate(tbsItem.getLastModifiedDate());
								
								//BBBH-2396 - Cross channel Cart - To retain personalized details while moving from tbs to Store
								bbbItem.setReferenceNumber(tbsItem.getReferenceNumber());
								bbbItem.setPersonalizationDetails(tbsItem.getPersonalizationDetails());
								bbbItem.setPersonalizationOptions(tbsItem.getPersonalizationOptions());
								bbbItem.setPersonalizeCost(tbsItem.getPersonalizeCost());
								bbbItem.setFullImagePath(tbsItem.getFullImagePath());
								bbbItem.setThumbnailImagePath(tbsItem.getThumbnailImagePath());
								bbbItem.setEximErrorExists(tbsItem.isEximErrorExists());
								bbbItem.setEximPricingReq(tbsItem.isEximPricingReq());
								bbbItem.setPersonalizationOptionsDisplay(tbsItem.getPersonalizationOptionsDisplay());
								bbbItem.setPersonalizePrice(tbsItem.getPersonalizePrice());
								bbbItem.setMetaDataFlag(tbsItem.getMetaDataFlag());
								bbbItem.setMetaDataUrl(tbsItem.getMetaDataUrl());
								bbbItem.setModerationFlag(tbsItem.getModerationFlag());
								bbbItem.setModerationUrl(tbsItem.getModerationUrl());
								bbbItem.setMobileFullImagePath(tbsItem.getMobileFullImagePath());
								bbbItem.setMobileThumbnailImagePath(tbsItem.getMobileThumbnailImagePath());
								try {
									skuDetailVO = getCatalogTools().getSKUDetails(order.getSiteId(), bbbItem.getCatalogRefId(), false, true, true);
								} catch (BBBSystemException e) {
		
									throw new CommerceException("BBBSystem Exception from mergeOrdersCopyCommerceItem in BBBCommerceItemManager", e);
								} catch (BBBBusinessException e) {
		
									throw new CommerceException("BBBSystem Exception from mergeOrdersCopyCommerceItem in BBBCommerceItemManager", e);
								}
		
								if (skuDetailVO != null) {
									bbbItem.setIsEcoFeeEligible(skuDetailVO.getIsEcoFeeEligible());
								}
								//assign ltl attributes to new commerce item
								if (null!=skuDetailVO && skuDetailVO.isLtlItem()) {
									bbbItem.setLtlShipMethod(((BBBShippingGroupCommerceItemRelationship) tbsItem.getShippingGroupRelationships()
											.get(0)).getShippingGroup().getShippingMethod());
									bbbItem.setWhiteGloveAssembly(((BBBCommerceItem) tbsItem).getWhiteGloveAssembly());
									bbbItem.setAssemblyItemId(((BBBCommerceItem) tbsItem).getAssemblyItemId());
									bbbItem.setDeliveryItemId(((BBBCommerceItem) tbsItem).getDeliveryItemId());
									bbbItem.setLtlItem(true);
									
								}
								
								List<ShippingGroupCommerceItemRelationship> commItemRels = tbsItem.getShippingGroupRelationships();
		
								CItemInfo bbbCItemInfo = new CItemInfo();
								bbbCItemInfo.setBbbItem(bbbItem);
								
								for (ShippingGroupCommerceItemRelationship commItemRel : commItemRels) {		
									bbbCItemInfo.getQuantities().add(commItemRel.getQuantity());
									bbbCItemInfo.getShipGroupIds().add(commItemRel.getShippingGroup().getId());
								}		
								bbbItemsList.add(bbbCItemInfo);							
							}	
							removableItems.add(tbsItem.getId());
						}
					}
					vlogDebug("the removable Items are "+removableItems);
					//Remove the tbs commerce items from the order 
					for (String removalId : removableItems) {
						commerceItemManager.removeItemFromOrder(order, removalId);
					}
					//Add the newly created commerce items to the order
					for (CItemInfo bbbCItem : bbbItemsList) {	
						commerceItemManager.addItemToOrder(order, bbbCItem.getBbbItem());
						List<String> shipGroupIds = bbbCItem.getShipGroupIds();
						List<Long> quantities = bbbCItem.getQuantities();
						Iterator<Long> quantityIterator = quantities.iterator();
						for (Iterator<String> shipIdIterator = shipGroupIds.iterator(); shipIdIterator.hasNext();) {
							commerceItemManager.addItemQuantityToShippingGroup(order, bbbCItem.getBbbItem().getId(), shipIdIterator.next(),
									quantityIterator.next());
						}
					}
	
					if(tbsOrderToBeUpdated){
						((BBBOrderManager) getOrderManager()).removeALLDeliveryAssemblyCIFromOrderBySG(order, BBBCoreConstants.DESTINATION_ORDER);
						((BBBOrderManager) getOrderManager()).createDeliveryAssemblyCI(order);
						repriceOrder(order, pProfile, pPricingModelHolder, getUserLocale(pRequest, pResponse), getRepriceOrderPricingOp());
					}
				/*}*/
			} catch (CommerceException ce) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "CommerceException in TBSProfileFormHandler.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1171 ), ce);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.TBS_PROFILE_TOOLS,
							methodName);
			} catch (IOException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "IOException in TBSProfileFormHandler.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.TBS_PROFILE_TOOLS,
							methodName);
			} catch (BBBBusinessException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException in BBBProfileTools.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
			} catch (BBBSystemException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in BBBProfileTools.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "RepositoryException in BBBProfileTools.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
			}
		 

		}else {
		    logError ("Shopping Cart is null");
			}
		// end of synchronized
		} catch(TransactionDemarcationException e){
			shouldRollback = true;
			this.logError("TransactionDemarcationException in fetching error message from postLoginUser() in BBBProfileTool", e);
		}catch (CommerceException e) {
			shouldRollback = true;
			if(isLoggingError()){
				logError(LogMessageFormatter.formatMessage(null, "CommerceException in Error occurred while persisitng/updating order", BBBCoreErrorConstants.ACCOUNT_ERROR_1176 ), e);
			}
		}finally{
				long totalTime = System.currentTimeMillis() - startTime;	
				logDebug("***Total time taken by BBBProfileTools.postLoginUser() is " + totalTime);
				try { 
						if(td!=null){
						td.end(shouldRollback);
						}
				} catch (TransactionDemarcationException e) {
					this.logError("TransactionDemarcationException in ending the transaction from postLoginUser() method in BBBProfileTool class", e);
				}
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
			}
		}
		//gsar.setLoggingDebug(false);
	}
	
	
	@Override
	public void loadUserShoppingCartForLogin(RepositoryItem pProfile,
			OrderHolder pShoppingCart, PricingModelHolder pUserPricingModels,
			Locale pLocale) throws CommerceException {
		
		long startTime = System.currentTimeMillis();
		String methodName = BBBPerformanceConstants.LOAD_USER_CART;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
		
		try {
		      if (isLoggingDebug()){
		        logDebug("loadUserShoppingCartForLogin() : Acquiring transaction lock within loadUserShoppingCartForLogin");
		      }
		      acquireTransactionLock();
		    }
		    catch (DeadlockException de) {

		      // We are going to log the exception here and then ignore it because
		      // the worst that should happen is that the user will get a concurrent
		      // update exception if two threads try to modify the same order, and we
		      // can recover from that.

		      if (isLoggingError())
					logError(LogMessageFormatter.formatMessage(null, "DeadlockException in loadUserShoppingCartForLogin() : Acquiring transaction lock within loadUserShoppingCartForLogin", BBBCoreErrorConstants.ACCOUNT_ERROR_1177 ), de);
		      
		      isMonitorCanceled = true;
				BBBPerformanceMonitor.cancel(
						BBBPerformanceConstants.BBB_PROFILE_TOOLS,
						methodName);
		    }

		    try {
				loadShoppingCarts(pProfile, pShoppingCart);
				//repriceShoppingCarts(pProfile, pShoppingCart, pUserPricingModels, pLocale, getRepriceOrderPricingOp());
				//persistShoppingCarts(pProfile, pShoppingCart);
				Order order = pShoppingCart.getCurrent();
				if (order != null){
				    synchronized(order) {
				        order.setProfileId(pProfile.getRepositoryId());
				    }
				}
		    }

		    // Release the transaction lock in a finally clause in case any of the code
		    // above throws exceptions.  We don't want to end up holding the lock forever
		    // in this case.

		    finally
		    {
		      if (isLoggingDebug()){
		        logDebug("loadUserShoppingCartForLogin() : Releasing transaction lock within loadUserShoppingCartForLogin");
		        long totalTime = System.currentTimeMillis() - startTime;
				logInfo("*** Total time taken by BBBProfileTools.loadUserShoppingCartForLogin() is " + totalTime);
		      }

		      releaseTransactionLock();
		      if (!isMonitorCanceled) {
					BBBPerformanceMonitor.end(
							BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
				}
		    }
	}

	/**
	 * Utility method to copy over the profile email to the login field.
	 * 
	 * @param pProfile
	 *            - Profile Object
	 */
/*	public void copyEmailToLogin(Profile pProfile) {
		BBBPropertyManager propertyManager = (BBBPropertyManager) getPropertyManager();
		String email = (String) pProfile.getPropertyValue(propertyManager
				.getEmailAddressPropertyName());
		pProfile.setPropertyValue(propertyManager.getLoginPropertyName(),
				email.toLowerCase());
	}*/

//	/**
//	 * {@inheritDoc}
//	 * 
//	 * @see atg.commerce.profile.CommerceProfileTools#postCreateUser(atg.servlet.DynamoHttpServletRequest,
//	 *      atg.servlet.DynamoHttpServletResponse,
//	 *      atg.repository.RepositoryItem, atg.commerce.order.OrderHolder)
//	 */
//	protected void postCreateUser(DynamoHttpServletRequest pRequest,
//			DynamoHttpServletResponse pResponse, RepositoryItem pProfile,
//			OrderHolder pShoppingCart) throws ServletException {
//		Profile profile = (Profile) pProfile;
//		copyEmailToLogin(profile);
//		super.postCreateUser(pRequest, pResponse, profile, pShoppingCart);
//
//		try {
//			PricingModelHolder userPricingModels = (PricingModelHolder) pRequest
//					.resolveName(getUserPricingModelsPath());
//			userPricingModels.initializePricingModels();
//			repriceShoppingCarts(pProfile, pShoppingCart, userPricingModels,
//					getUserLocale(pRequest, pResponse));
//		} catch (CommerceException e) {
//			// Do nothing, we'll display old order price
//			if (isLoggingError()) {
//				logError(e);
//			}
//		} catch (IOException e) {
//			// Do nothing, we'll display old order price
//			if (isLoggingError()) {
//				logError(e);
//			}
//		}
//	}

	/**
	 * Obtains all credit cards associated with user profile.
	 * 
	 * @param pProfile
	 *            the user profile
	 * 
	 * @return Map of CreditCard or null if no credit cards are specified on the
	 *         profile
	 */
/*	public Map getCreditCards(RepositoryItem pProfile) {
		BBBPropertyManager spmgr = (BBBPropertyManager) getPropertyManager();
		Map creditCards = (Map) pProfile.getPropertyValue(spmgr
				.getCreditCardPropertyName());

		return creditCards;
	}*/

	
	/**
	   * This method calls super.loadShoppingCarts and removes delivery and
	   * assembly items from logged in cart in case anonymous cart is empty
	   *
	   * @param pProfile the user profile
	   * @param pShoppingCart the OrderHolder component for this user
	   * @exception CommerceException is any errors occur while loading the shopping carts
	   */
	
	public void loadShoppingCarts(RepositoryItem pProfile, OrderHolder pShoppingCart) throws CommerceException
	{
		boolean currentCartEmpty = false;
    	if(pShoppingCart.isCurrentEmpty()){
    		currentCartEmpty = true;
    	}
		super.loadShoppingCarts(pProfile, pShoppingCart);
		if(!pShoppingCart.isCurrentEmpty()){
			Order order = pShoppingCart.getCurrent();
			if (order != null){			 
			        if(currentCartEmpty){
			        	((BBBOrderManager) getOrderManager()).removeALLDeliveryAssemblyCIFromOrderBySG(order, BBBCoreConstants.DESTINATION_ORDER);
			        	try {
			        		//Story Id - BPSI-4433 Start - Exim Changes to call multi-ref API
			        		if("true".equalsIgnoreCase(getEximManager().getKatoriAvailability())) {
			        			List<CommerceItem> commerceItems = order.getCommerceItems();
				        		if(!BBBUtility.isListEmpty(commerceItems)){
				        			boolean isRepricingRequired = getEximManager().setEximDetailsLoadShoppingCart(commerceItems, (BBBOrder)order);
				        			if(isRepricingRequired){
				        				getOrderManager().updateOrder(order);
				        }
				    }
			        		}
			        		
			        		//Story Id - BPSI-4433 End - Exim Changes to call multi-ref API
			        		((BBBOrderManager) getOrderManager()).createDeliveryAssemblyCI(order);
						} catch (BBBBusinessException e) {
							throw new CommerceException("Bussiness Exception from loadShoppingCarts method of BBBProfileTools", e);
						} catch (BBBSystemException e) {
							throw new CommerceException("System Exception from loadShoppingCarts method of BBBProfileTools", e);
						} catch (RepositoryException e) {
							throw new CommerceException("Repository Exception from loadShoppingCarts method of BBBProfileTools", e);
			}
		}	
			}
		}
	}
	
	
	/**
	 * This method gets the credit card with parameter (pNickname) and makes it
	 * the default credit card for this profile. It just returns false if this
	 * fails, exception handling is up to the caller.
	 * 
	 * @param pProfile
	 *            to get the credit card from, and set default to.
	 * @param pNickname
	 *            credit card to copy from Map of credit cards
	 * @return boolean success or failure
	 */
	@SuppressWarnings("rawtypes")
	public boolean setDefaultCreditCard(RepositoryItem pProfile,
			String pNickname) {
		if (isLoggingDebug()) {
			logDebug("Setting default credit card for profile: "
					+ pProfile.getRepositoryId() + " to the credit card: "
					+ pNickname);
		}

		try {
			BBBPropertyManager pm = (BBBPropertyManager) getPropertyManager();
			Map creditCards = (Map) pProfile.getPropertyValue(pm
					.getCreditCardPropertyName());
			RepositoryItem creditCard = (RepositoryItem) creditCards
					.get(pNickname);

			if (creditCard == null) {
				if (isLoggingDebug()) {
					logDebug("No credit card exists for this nickname: "
							+ pNickname + " for this profile: "
							+ pProfile.getRepositoryId());
				}

				return false;
			}

			// pProfile.setPropertyValue(pm.getDefaultCreditCardPropertyName(),
			// creditCard);
			updateProperty(pm.getDefaultCreditCardPropertyName(), creditCard,
					pProfile);
		} catch (RepositoryException re) {
			if (isLoggingError()) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException : Error setting default credit card", BBBCoreErrorConstants.ACCOUNT_ERROR_1178 ), re);
			}

			return false;
		}

		return true;
	}

	/**
	 * Gets the default credit card for a user.
	 * 
	 * @param pProfile
	 *            the user profile
	 * @return CreditCard object. Returns null if no default credit card exists
	 *         for the user.
	 */
	public RepositoryItem getDefaultCreditCard(RepositoryItem pProfile) {
		BBBPropertyManager bpm = (BBBPropertyManager) getPropertyManager();
		String defaultCreditCardPropertyName = bpm
				.getDefaultCreditCardPropertyName();
		RepositoryItem cc = (RepositoryItem) pProfile
				.getPropertyValue(defaultCreditCardPropertyName);
		return cc;
	}

	/**
	 * Finds profile's credit card that corresponds to the given credit card
	 * payment group and returns its nickname.
	 * 
	 * @param pProfile
	 *            profile object
	 * @param pCreditCard
	 *            credit card payment group
	 * @return
	 */
	/*public String getCreditCardNickname(RepositoryItem pProfile,
			CreditCard pCreditCard) {
		// find credit card in Profile with the same credit card number
		String creditCardNumber = pCreditCard.getCreditCardNumber();
		String creditCardExpMonth = pCreditCard.getExpirationMonth();
		String creditCardExpYear = pCreditCard.getExpirationYear();
		Address creditCardAddress = pCreditCard.getBillingAddress();
		BBBPropertyManager spmgr = (BBBPropertyManager) getPropertyManager();
		Map creditCards = (Map) pProfile.getPropertyValue(spmgr
				.getCreditCardPropertyName());
		String nickname = null;

		try {
			for (Iterator it = creditCards.entrySet().iterator(); it.hasNext();) {
				Map.Entry cardsEntry = (Map.Entry) it.next();
				String profileCardNumber = (String) ((RepositoryItem) cardsEntry
						.getValue()).getPropertyValue(spmgr
						.getCreditCardNumberPropertyName());
				String profileCardExpMonth = (String) ((RepositoryItem) cardsEntry
						.getValue()).getPropertyValue(spmgr
						.getCreditCardExpirationMonthPropertyName());
				String profileCardExpYear = (String) ((RepositoryItem) cardsEntry
						.getValue()).getPropertyValue(spmgr
						.getCreditCardExpirationYearPropertyName());

				RepositoryItem profileCardAddressItem = (RepositoryItem) ((RepositoryItem) cardsEntry
						.getValue()).getPropertyValue(spmgr
						.getCreditCardBillingAddressPropertyName());
				Address profileCardAddress = getAddressFromRepositoryItem(profileCardAddressItem);

				if (profileCardNumber.equals(creditCardNumber)
						&& profileCardExpMonth.equals(creditCardExpMonth)
						&& profileCardExpYear.equals(creditCardExpYear)
						&& BBBAddressTools.compare(profileCardAddress,
								creditCardAddress)) {
					nickname = (String) cardsEntry.getKey();

					if (isLoggingDebug()) {
						logDebug("Nickname for credit card " + pCreditCard
								+ " found: " + nickname);
					}
					break;
				}
			}
		} catch (RepositoryException repositoryException) {
			if (isLoggingError()) {
				logError("Error obtaining credit card nickname: ",
						repositoryException);
			}

			nickname = "";
		}
		return nickname;
	}*/
	
	public void updateQASDetails(MutableRepositoryItem pProfile,String nickName, boolean isPOBoxAddress,
			boolean qasValidated) {

		if (isLoggingDebug()) {
			logDebug("BBBProfileTools |setDefaultBillingAddress| Starts");
		}
		//RepositoryItem addressItem = (StringUtils.isEmpty(nickName)) ? null
		//		: getProfileAddress(pProfile, nickName);
		//updateProperty(getPmgr().getShippingAddressPropertyName(), addressItem,
		//		pProfile);

		if (isLoggingDebug()) {
			logDebug("BBBProfileTools |setDefaultBillingAddress| Ends");
		}

	}

	/**
	 * Gets the default credit card nickname for a user.
	 * 
	 * @param pProfile
	 *            the user profile
	 * @return default Credit cart nickname for a user.
	 */
/*	public String getDefaultCreditCardNickname(RepositoryItem pProfile) {
		RepositoryItem defaultCard = getDefaultCreditCard(pProfile);
		return defaultCard != null ? getCreditCardNickname(pProfile,
				defaultCard) : null;
	}

	*//**
	 * Gets the default shipping method for a user.
	 * 
	 * @param pProfile
	 *            the user profile
	 * @return the default shipping method for a user.
	 *//*
	public String getDefaultShippingMethod(RepositoryItem pProfile) {
		BBBPropertyManager spm = (BBBPropertyManager) getPropertyManager();
		String defaultShippingMethodPropertyName = spm
				.getDefaultShippingMethodPropertyName();
		String shippingMethod = (String) pProfile
				.getPropertyValue(defaultShippingMethodPropertyName);

		return shippingMethod;
	}

	*//**
	 * Gets nickname for the given profile's address.
	 * 
	 * @param pProfile
	 *            The profile repository item
	 * @param pAddress
	 *            Address object
	 * @return nickname for secondary address repository item
	 *//*
	public String getProfileAddressName(RepositoryItem pProfile,
			Address pAddress) {
		CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
		Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr
				.getSecondaryAddressPropertyName());
		String nickname = null;

		for (Iterator it = secondaryAddresses.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry addrEntry = (Map.Entry) it.next();
			RepositoryItem address = (RepositoryItem) addrEntry.getValue();
			if (areAddressesEqual(pAddress, address, null)) {
				nickname = (String) addrEntry.getKey();

				if (isLoggingDebug()) {
					logDebug("Nickname for secondary address " + pAddress
							+ " found: " + nickname);
				}
				break;
			}
		}
		return nickname;
	}
    
	*/
	
	/**
	   * Get the default Mailing address repository item by getting property name from the
	   * property manager.
	   *
	   * @param pProfile profile that Mailing address will be extracted from
	   * @return the Billing address
	   */
	  public RepositoryItem getDefaultMailingAddress(RepositoryItem pProfile) {
	    BBBPropertyManager cpmgr = (BBBPropertyManager)getPropertyManager();
	    return (RepositoryItem)pProfile.getPropertyValue(cpmgr.getMailingAddressPropertyName());
	  }
	
	/**
	 * Gets the default shipping address nickname for a user.
	 * 
	 * @param pProfile
	 *            the user profile
	 * @return the default shipping address nickname for a user.
	 */
	public String getDefaultShippingAddressNickname(RepositoryItem pProfile) {
		RepositoryItem defaultAddress = getDefaultShippingAddress(pProfile);
		return defaultAddress != null ? getProfileAddressName(pProfile,
				defaultAddress) : null;
	}

	/**
	 * Gets the default billing address nickname for a user.
	 * 
	 * @param pProfile
	 *            the user profile
	 * @return the default billing address nickname for a user.
	 */
	public String getDefaultBillingAddressNickname(RepositoryItem pProfile) {
		RepositoryItem defaultAddress = getDefaultBillingAddress(pProfile);
		return defaultAddress != null ? getProfileAddressName(pProfile,
				defaultAddress) : null;
	}
	
	/**
	 * Gets the default mailing address nickname for a user.
	 * 
	 * @param pProfile
	 *            the user profile
	 * @return the default mailing address nickname for a user.
	 */
	public String getDefaultMailingAddressNickname(RepositoryItem pProfile) {
		RepositoryItem defaultAddress = getDefaultBillingAddress(pProfile);
		return defaultAddress != null ? getProfileAddressName(pProfile,
				defaultAddress) : null;
	}
	
	
	/**
	 * @return InvalidContextRoot property.
	 *//*
	public String getInvalidContextRoot() {
		return mInvalidContextRoot;
	}

	*//**
	 * @param string
	 *            UrlContextShopWithConsultant property.
	 *//*
	public void setInvalidContextRoot(String string) {
		mInvalidContextRoot = string;
	}*/

	/**
	 * Utility method to copy one address repoitory item to another repository
	 * item.
	 * 
	 * @param pAddress
	 *            - source address
	 * @param pNewAddress
	 *            - target address
	 * @param pAddressIterator
	 *            - address iterator
	 */
	/*public void copyAddress(MutableRepositoryItem pAddress,
			MutableRepositoryItem pNewAddress, Iterator pAddressIterator) {
		String propertyName;
		Object property;

		while (pAddressIterator.hasNext()) {
			propertyName = (String) pAddressIterator.next();
			property = pAddress.getPropertyValue(propertyName);
			pNewAddress.setPropertyValue(propertyName, property);
		}
	}*/

	/**
	 * This method creates an address object and sets the property values to
	 * values in the repository item passed in.
	 * 
	 * @param pItem
	 *            the repository item
	 * @return address the address object with data from repository
	 * @exception RepositoryException
	 *                if there was an error when creating the new repository
	 *                item.
	 */
	public Address getAddressFromRepositoryItem(RepositoryItem pItem)
			throws RepositoryException {
		Address address = new ContactInfo();

		// update item with values in address
		try {
			atg.commerce.order.OrderTools.copyAddress(pItem, address);
		} catch (CommerceException ce) {
			Throwable src = ce.getSourceException();

			if (src instanceof RepositoryException) {
				throw (RepositoryException) src;
			} else {
				throw new RepositoryException(ce);
			}
		}

		return address;
	}

	/**
	 * Validates country-state combination for the given address properties map.
	 * Checks if state is required for the given country.
	 * 
	 * @param pCountry
	 *            country code
	 * @param pState
	 *            state code
	 * @return true if country-state combination is valid
	 */
/*	public boolean isValidCountryStateCombination(String pCountry, String pState) {
		if (StringUtils.isEmpty(pState)
				|| BBBCoreConstants.UNKNOWN_STATE_CODE.equals(pState)) {
			// State code is empty. Make sure that specified country has no
			// states.
			Place[] placesForCountry = getPlaceUtils().getPlaces(pCountry);
			return ((placesForCountry == null || placesForCountry.length == 0));
		}
		return getPlaceUtils().isPlaceInCountry(pCountry, pState);
	}*/

	/**
	 * This method constructs a new credit card and the address object that is
	 * on the credit card. pIsNewAddress indicates new secondary address should
	 * be created or existed one should be used. If new secondary address should
	 * be created then its properties are populated from pShippingAddress map.
	 * Created credit card repository item is added to user's map of credit
	 * cards. If user's default billing address is null than new credit card's
	 * billing address is set as default billing address. If user's default
	 * credit card is null that new credit card is set as default credit card
	 * for this user.
	 * 
	 * @param pProfile
	 *            user's profile object
	 * @param pNewCreditCard
	 *            map of credit cards properties' values
	 * @param pCreditCardNickname
	 *            nickname for credit card
	 * @param pBillingAddress
	 *            map of shipping address properties' values
	 * @param pAddressNickname
	 *            shipping address nickname for shipping address to be created
	 *            or for existed one
	 * @param pIsNewAddress
	 *            indicated if new address should be created or existed one
	 *            should be used
	 * @param isPreferredBilling 
	 * @return created credit card's nickname
	 * 
	 * @throws RepositoryException
	 *             if there was an error accessing the repository
	 * @throws IntrospectionException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws PropertyNotFoundException
	 */

	@SuppressWarnings("rawtypes")
	public String createProfileCreditCard(Profile pProfile, Map pNewCreditCard,
			String pCreditCardNickname, Map pBillingAddress,
			String pAddressNickname, boolean pIsNewAddress,
			boolean isPreferredBilling) throws RepositoryException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IntrospectionException,
			PropertyNotFoundException {

		/*
		 * if (isLoggingDebug()) {
		 * logDebug(LogMessageFormatter.formatMessage(pRequest,
		 * "BBBProfileFormHandler.handleCreateNewCreditCardAndAddress() method Starts"
		 * )); }
		 */
		// create new secondary address item or extract existed one
		boolean isDefShippAddr = false;
		List addresses = getAllAvailableAddresses(pProfile);
		if(addresses == null || addresses.isEmpty()){
			isDefShippAddr = true;
		}
		RepositoryItem secondaryAddress = null;
		String addrNickname = pAddressNickname;
		// <remove this code>
		if (pIsNewAddress) {
			// generate secondary address nickname if it's not provided
			if (StringUtils.isBlank(addrNickname)) {
				addrNickname = getUniqueShippingAddressNickname(
						pBillingAddress, pProfile, null);
			}
			// <remove this code> ends
			// Create Profile's secondary address repository item
			Address addressObject = AddressTools.createAddressFromMap(
					pBillingAddress, getShippingAddressClassName());
			createProfileRepositorySecondaryAddress(pProfile, addrNickname,
					addressObject);
			// Check to see Profile.shippingAddress is null, if it is,
			// add the new address as the default shipping address
			setDefaultShippingAddressIfNull(pProfile, addrNickname);
		}
		secondaryAddress = getProfileAddress(pProfile, addrNickname);
		if (isPreferredBilling) {
			setDefaultBillingAddress(pProfile, addrNickname);
		}
		if (isDefShippAddr) {
			setDefaultShippingAddress(pProfile, addrNickname);
		}
		return createProfileCreditCard(pProfile, pNewCreditCard,
				pCreditCardNickname, secondaryAddress);
	}

	/**
	 * This implementation makes a reference to an existing address (should be
	 * located in a <code>secondaryAddresses</code> set) with the
	 * <code>shippingAddress</code> property.
	 */
	@Override
	public boolean setDefaultShippingAddress(RepositoryItem pProfile,
			String pAddressName) throws RepositoryException {
		
		logDebug("setDefaultShippingAddress() in BBBProfileTools started");
		BBBPropertyManager propertyManager = (BBBPropertyManager) getPropertyManager();
		RepositoryItem addressItem = StringUtils.isEmpty(pAddressName) ? null
				: getProfileAddress(pProfile, pAddressName);
		updateProperty(propertyManager.getShippingAddressPropertyName(),
				addressItem, pProfile);
		logDebug("setDefaultShippingAddress() in BBBProfileTools ended");
		
		return true;
	}
	
	/**
	 * This implementation makes a reference to an existing address (should be
	 * located in a <code>secondaryAddresses</code> set) with the
	 * <code>Mailing</code> property.
	 */
	
	public boolean setDefaultMailingAddress(RepositoryItem pProfile,
			String pAddressName) throws RepositoryException {
		
		logDebug("setDefaultMailingAddress() in BBBProfileTools started");
		BBBPropertyManager propertyManager = (BBBPropertyManager) getPropertyManager();
		RepositoryItem addressItem = StringUtils.isEmpty(pAddressName) ? null
				: getProfileAddress(pProfile, pAddressName);
		updateProperty(propertyManager.getMailingAddressPropertyName(),
				addressItem, pProfile);
		logDebug("setDefaultMailingAddress() in BBBProfileTools ended");
		
		return true;
	}
	
	/**
	 * This method checks whether the profile's site belongs to group which the
	 * current site belong like user register in SiteGroup1 but the current site
	 * belong to SiteGroup2. So it will return true else false
	 * @param pRepItem 
	 * @param pSiteId 
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean isUserPresentToOtherGroup(RepositoryItem pRepItem,
			String pSiteId) {
		logDebug("BBBProfileTools.isUserBelongToOtherGroup() method Ends");
		if (pRepItem != null && pSiteId != null && getSiteGroup() != null) {
			Collection<SiteGroup> groupList = getSiteGroup()
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
								return false;								
							}
						}
					}

				}

			}
		}

		logDebug("BBBProfileTools.isUserBelongToOtherGroup() method Ends");
		return true;
	}
	
	/**
	 * This method updates the userProfile by adding/removing values of
	 * favStoreId for the current site. If successful returns true else false
	 * 
	 * @param pProfile
	 * @param pCurrSite
	 * @param propertyMap 
	 * @param pFavStoreId
	 * @return status
	 */
	@SuppressWarnings("rawtypes")
	public boolean updateSiteItem(MutableRepositoryItem pProfile, String pCurrSite,
			Map<String, String> propertyMap) {
		logDebug("BBBProfileTools.updateFavStoreToPofile() method started");
		
		boolean status = false;
		if (pProfile != null && pCurrSite != null && propertyMap != null && !propertyMap.isEmpty()) {
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
					
					for(Entry<String, String> entry : propertyMap.entrySet()){
						String key = entry.getKey();
						String value = entry.getValue();
						if(key.equalsIgnoreCase(mPmgr.getEmailOptInPropertyName())){
							if(value.equalsIgnoreCase(BBBCoreConstants.NO)){
								item.setPropertyValue(key, Integer.valueOf (0));
							}else{
								item.setPropertyValue(key, Integer.valueOf (1));
							}
						}else{
							item.setPropertyValue(key, value);
						}
					}
					
					repo.updateItem(item);
					status = true;
				}
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException while updating the user Profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1179 ), e);
			}
		}
		logDebug("BBBProfileTools.updateFavStoreToPofile() method ends");
		return status;
	}
	
	
	/**
	 * @param pProfile
	 * @param propertyMap
	 * @return Repository Item
	 */
	@SuppressWarnings("unchecked")
	public Map<String, MutableRepositoryItem> updateSiteItem(Profile pProfile, Map<String, String> propertyMap) {
		logDebug("BBBProfileTools.updateSiteItem() method started");
		
		Map<String, MutableRepositoryItem> updatedSiteItems = null;
		
		if (pProfile != null && propertyMap != null && !propertyMap.isEmpty()) {
			
			try {
				
				Map<String, RepositoryItem> siteMap = (Map<String, RepositoryItem>) pProfile.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);
				MutableRepository mutableBBBProfile = (MutableRepository) pProfile.getRepository();
				
				if (siteMap != null && !siteMap.isEmpty()) {
					
					updatedSiteItems = new HashMap<String, MutableRepositoryItem>();
					for(Entry<String, RepositoryItem> entry : siteMap.entrySet()){
						String siteName = entry.getKey();
						RepositoryItem oldSiteItem = entry.getValue();
						MutableRepositoryItem mutableSite =  mutableBBBProfile.getItemForUpdate(oldSiteItem.getRepositoryId(), 
																	oldSiteItem.getItemDescriptor().getItemDescriptorName());
						
						for(Entry<String, String> innerEntry : propertyMap.entrySet()){
							
							String key = innerEntry.getKey();
							String value = innerEntry.getValue();
							if(key.equalsIgnoreCase(mPmgr.getEmailOptInPropertyName())){
								if(value.equalsIgnoreCase(BBBCoreConstants.NO)){
									mutableSite.setPropertyValue(key, Integer.valueOf (0));
								}else{
									mutableSite.setPropertyValue(key, Integer.valueOf (1));
								}
							}else{
								mutableSite.setPropertyValue(key, value);
							}
						}
						updatedSiteItems.put(siteName, mutableSite);
					}
				}
				
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException while updating the user Profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1179 ), e);
			}
		}
		
		logDebug("BBBProfileTools.updateSiteItem() method ends");
		return updatedSiteItems;
	}
	
	/**
	 * @param userProfile
	 * @return Email Options
	 */
	@SuppressWarnings("unchecked")
	public String fetchEmailOptInflagFromSisterSite(RepositoryItem userProfile){
		
		String emailOptInFlag = null;
		if(userProfile != null){
			Map<String, RepositoryItem> userSites = (Map<String, RepositoryItem>)userProfile.getPropertyValue(mPmgr.getUserSiteItemsPropertyName());
			if(userSites != null && !userSites.isEmpty()){
				for(String sisterSiteName : userSites.keySet()){
					RepositoryItem sisterSite = (RepositoryItem)userSites.get(sisterSiteName);
					if(sisterSite != null){
						if(sisterSite.getPropertyValue(mPmgr.getEmailOptInPropertyName()) != null){
							int emailOptInValue = ((Integer)sisterSite.getPropertyValue(mPmgr.getEmailOptInPropertyName())).intValue ();
							if(emailOptInValue == 1){
								emailOptInFlag = BBBCoreConstants.YES;
							}else{
								emailOptInFlag = BBBCoreConstants.NO;
							}
						}else{
							emailOptInFlag = BBBCoreConstants.NO;
						}
						
						break;
					}
				}
			}
		}
		
		return emailOptInFlag;	
	}

	@Override
	protected void repriceOrder(Order pOrder, RepositoryItem pProfile,
			PricingModelHolder pUserPricingModels, Locale pLocale,
			String pPricingOperation) throws CommerceException {
		long startTime = System.currentTimeMillis();
		String methodName = BBBPerformanceConstants.REPRICE_ORDER;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
		 final TransactionManager tm = this.getTransactionManager();
         final TransactionDemarcation td = new TransactionDemarcation();
         boolean shouldRollback = false;
		try {
		     if (isLoggingDebug())
		        logDebug("reprice order " + pOrder);
		      synchronized(pOrder) {
		    	  if(tm !=null){
						 td.begin( tm ,TransactionDemarcation.REQUIRED );
						}
		    	  HashMap<String, Object> params = new HashMap<String, Object>();
		    	  params.put(BBBCheckoutConstants.REPRICE_SHOPPING_CART_ORDER, pOrder);
		    	  getPricingTools().performPricingOperation(pPricingOperation,
		                                                  pOrder,
		                                                  pUserPricingModels,
		                                                  pLocale,
		                                                  pProfile,
		                                                  params);
		    	  getOrderManager().updateOrder(pOrder);
		      }
		} catch(TransactionDemarcationException e){
			shouldRollback = true;
			isMonitorCanceled = true;
			this.logError("TransactionDemarcationException in synchronising order from repriceOrder() in BBBProfileTool", e);
		}catch(CommerceException ce){
			if (ce != null && ce instanceof PricingException) {
		    	this.logDebug("Some error occured while Repricing of Order, removing all the commerce items from Order : " + pOrder.getId());
				synchronized (pOrder) {
					getOrderManager().getCommerceItemManager().removeAllCommerceItemsFromOrder(pOrder);
					this.getOrderManager().updateOrder(pOrder);
				}
			}else{
				shouldRollback = true;
			}
			isMonitorCanceled = true;
			BBBPerformanceMonitor.cancel(
			BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
			throw ce;
		} finally {
			try {
				td.end(shouldRollback);
			} catch (TransactionDemarcationException e) {
				this.logError("TransactionDemarcationException exception occurred", e);
			}
			if (isLoggingDebug()) {
				long totalTime = System.currentTimeMillis() - startTime;
				logInfo("*** Total time taken by BBBProfileTools.repriceOrder() is "
						+ totalTime);
			}
			if(!isMonitorCanceled){
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
				}
		}

	}
	
	@Override
	public void repriceShoppingCarts(RepositoryItem pProfile,
			OrderHolder pShoppingCart, PricingModelHolder pUserPricingModels,
			Locale pLocale, String pPricingOperation) throws CommerceException {
		long startTime = System.currentTimeMillis();
		String methodName = BBBPerformanceConstants.REPRICE_CART;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
		try {
			
			// BBBJ-739 | Remove repricing of saved orders after login : STARTS
			if ( isRepriceOrderOnLogin() && !pShoppingCart.isCurrentEmpty()) {
			      Order order = pShoppingCart.getCurrent();
			      repriceOrder(order, pProfile, pUserPricingModels, pLocale, pPricingOperation);
			}
			// BBBJ-739 | Remove repricing of saved orders after login : ENDS
		} catch(CommerceException ce){
			isMonitorCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
			throw ce;
		}
		finally {
			if (isLoggingDebug()) {
				long totalTime = System.currentTimeMillis() - startTime;
				logInfo("*** Total time taken by BBBProfileTools.repriceShoppingCarts() is "
						+ totalTime);
			}
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
			}
		}
	}
	
	@Override
	protected void persistOrderIfNeeded(Order pOrder) throws CommerceException {
		long startTime = System.currentTimeMillis();
		String methodName = BBBPerformanceConstants.PERS_ORD_NEED;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
		try {
			super.persistOrderIfNeeded(pOrder);
		}catch(CommerceException ce){
			isMonitorCanceled = true;
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
			throw ce;
		} finally {
			if (isLoggingDebug()) {
				long totalTime = System.currentTimeMillis() - startTime;
				logInfo("*** Total time taken by BBBProfileTools.persistOrderIfNeeded() is "
						+ totalTime);
			}
			if(!isMonitorCanceled){
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.BBB_PROFILE_TOOLS, methodName);
				}
		}
	}
	
	public String generateNewPasswordForProfile(RepositoryItem pProfile)
	        throws RepositoryException
	    {
		
		logDebug("BBBProfileTools.generateNewPasswordForProfile: Start");
		String generatedPassword = null;
		
	    int i = 0;
	    do
	    {
	    	if(i >= super.getPasswordGenerationTriesLimit()){
	    		break;
	    	}
	    		
	    	generatedPassword = getPasswordGenerator().generatePassword();
	    	logDebug("Is Generated Password valid: " + BBBUtility.isValidPassword(generatedPassword));
	    	
	    	if(BBBUtility.isValidPassword(generatedPassword)){
	    		break;	
	    	}
	    		
	    	i++;
	    	
	    } while(true);
	        
        String loginPropertyName = getPmgr().getLoginPropertyName();
        String passwordPropertyName = getPmgr().getPasswordPropertyName();
        String generatedPwdPropertyName = getPmgr().getGeneratedPasswordPropertyName();
        MutableRepository repository = (MutableRepository)pProfile.getRepository();
        MutableRepositoryItem mutItem = repository.getItemForUpdate(pProfile.getRepositoryId(), pProfile.getItemDescriptor().getItemDescriptorName());
        mutItem.setPropertyValue(passwordPropertyName, getPmgr().generatePassword((String)pProfile.getPropertyValue(loginPropertyName), generatedPassword));
        mutItem.setPropertyValue(generatedPwdPropertyName, Boolean.TRUE);
        repository.updateItem(mutItem);
        
        logDebug("BBBProfileTools.generateNewPasswordForProfile: End");
        return generatedPassword;
        
    }
	
	public boolean generateTempPasswordForProfile(RepositoryItem pProfile,String newPassword)
	        throws RepositoryException,BBBBusinessException
	    {
		boolean resetPwd = false;
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.generateTempPasswordForProfile: Start");
		}
		
	    if(!BBBUtility.isValidPassword(newPassword)){
	    	throw new BBBBusinessException("err_invalid_password", "Invalid Password");
	    }
	        
        String loginPropertyName = getPmgr().getLoginPropertyName();
        String passwordPropertyName = getPmgr().getPasswordPropertyName();
        String generatedPwdPropertyName = getPmgr().getGeneratedPasswordPropertyName();
        MutableRepository repository = (MutableRepository)pProfile.getRepository();
        MutableRepositoryItem mutItem = repository.getItemForUpdate(pProfile.getRepositoryId(), pProfile.getItemDescriptor().getItemDescriptorName());
        mutItem.setPropertyValue(passwordPropertyName, getPmgr().generatePassword((String)pProfile.getPropertyValue(loginPropertyName), newPassword));
        mutItem.setPropertyValue(generatedPwdPropertyName, Boolean.FALSE);
        repository.updateItem(mutItem);
        resetPwd = true;
        if (isLoggingDebug()) {
        	logDebug("BBBProfileTools.generateTempPasswordForProfile: End");
        }
        return resetPwd;
        
    }
	
	
	
	
	/**
	   *
	   * This method constructs a consolidated collection of all billing addresses that are associated with
	   * the current customer profile. The collection includes that default billing address and credit card addresses.
	   * @param pProfile
	   * @return Billing Address List
	   * 
	   */
	@SuppressWarnings("rawtypes")
	@Override
  public List getAllBillingAddresses (RepositoryItem pProfile){

    if (isLoggingDebug()) {
      logDebug ("Entering getAllBillingAddresses().");
    }
    
    ArrayList billingAddresses = new ArrayList();
    //add default shipping and billing
    

    if (isLoggingDebug()) {
      logDebug ("Collecting default billing address.");
    }

    RepositoryItem defaultBillingAddress = getDefaultBillingAddress(pProfile);
    if (!isAddressEmpty (defaultBillingAddress)) {
      addUniqueAddressToAddressList(billingAddresses,defaultBillingAddress);
    }

    //This is to add any configurable map properties

    addAddressesFromMapProperties (pProfile, getBillingAddressMapProperties(),billingAddresses);

    return billingAddresses;
  }
	
   /**
 * @param pProfile
 * @param addressId
 * @return Address
 */
public RepositoryItem getAddressesById (RepositoryItem pProfile, String addressId){
	   
	   @SuppressWarnings ("unchecked")
	   List<RepositoryItem> allAddresses = getAllAvailableAddresses(pProfile);

	   if(allAddresses == null || allAddresses.isEmpty()) {
		   return null;
	   }
	   for(RepositoryItem address: allAddresses) {
		   if(address.getRepositoryId().equals(addressId)) {
			   return address;
		   }
	   }
	   return null;
   }
   
	/**
	 * Update user loginFailAttempt and lastLoginAttemptDate property
	 * @param pProfile
	 * @param resetFlag
	 * @return success/failure
	 * @throws BBBSystemException
	 */
	public boolean updateLoginAttemptCount(RepositoryItem pProfile, boolean resetFlag)
			 {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.updateloginAttemptCount: Start");
		}
		
		int loginFailAttemptCount = 0;
		Timestamp systemDate = null;
		RepositoryItem accountLockDetail = null;
		MutableRepositoryItem mutableRepositoryItem = null;
		Timestamp currentSystemTime = new Timestamp(new java.util.Date().getTime());
		long currentSystemTimeinMills = currentSystemTime.getTime();
		long unlockTimeIntervalinMills = getUnLockTimeInterval() * 60 * 60 * 1000;
		long lastLoginFailAttemptTimeinMills = 0;
		Timestamp loginFailAttemptTime = null;
		boolean continueFlag = true;
		
		try {
		
			accountLockDetail = getProfileRepository().getItem(pProfile.getRepositoryId(), mPmgr.getAccountLockItemDescriptorName());
			
			if(resetFlag){
				if (accountLockDetail != null
						&& accountLockDetail.getPropertyValue(mPmgr
								.getInvalidLoginAttemptCount()) != null
						&& ((Integer) accountLockDetail.getPropertyValue(mPmgr
								.getInvalidLoginAttemptCount())).intValue() != 0) {
					loginFailAttemptCount = 0;
					if (isLoggingDebug()) {
						logDebug("Existing flow to set zero");
					}

				} else {
					if (isLoggingDebug()) {
						logDebug("Prev value is already zero so not setting again");
					}
					return resetFlag;
				}
			}else{
				systemDate = new Timestamp(new java.util.Date().getTime());
				if(accountLockDetail != null){
					if(accountLockDetail.getPropertyValue(mPmgr.getLastInvalidLoginAttemptTime()) != null){
						loginFailAttemptTime = (Timestamp) accountLockDetail.getPropertyValue(mPmgr.getLastInvalidLoginAttemptTime());	
						lastLoginFailAttemptTimeinMills = loginFailAttemptTime.getTime();
						if(currentSystemTimeinMills - lastLoginFailAttemptTimeinMills >= unlockTimeIntervalinMills){
							continueFlag = false;
						}
					}
					
					if(continueFlag && accountLockDetail.getPropertyValue(mPmgr.getInvalidLoginAttemptCount()) != null){
						loginFailAttemptCount = ((Integer) accountLockDetail.getPropertyValue(mPmgr.getInvalidLoginAttemptCount())).intValue ();
					}
					
					
				}
				
				loginFailAttemptCount = loginFailAttemptCount + 1;
				
			}	
		
			if(accountLockDetail == null){
				
				mutableRepositoryItem = getProfileRepository().createItem(mPmgr.getAccountLockItemDescriptorName());
				mutableRepositoryItem.setPropertyValue(mPmgr.getAccountLockProfileId(), pProfile.getRepositoryId());
				mutableRepositoryItem.setPropertyValue(mPmgr.getInvalidLoginAttemptCount(), Integer.valueOf (loginFailAttemptCount));
				mutableRepositoryItem.setPropertyValue(mPmgr.getLastInvalidLoginAttemptTime(), systemDate);
				getProfileRepository().addItem(mutableRepositoryItem);
				
			}else{
				
				mutableRepositoryItem = getProfileRepository()
						.getItemForUpdate(pProfile.getRepositoryId(), mPmgr.getAccountLockItemDescriptorName());
				mutableRepositoryItem.setPropertyValue(mPmgr.getInvalidLoginAttemptCount(), Integer.valueOf(loginFailAttemptCount));
				mutableRepositoryItem.setPropertyValue(mPmgr.getLastInvalidLoginAttemptTime(), systemDate);
				getProfileRepository().updateItem(mutableRepositoryItem);
				
			}
			
			/* Invalidate the cache for BBB user */
			ItemDescriptorImpl userItemDesc = (ItemDescriptorImpl) getProfileRepository().getItemDescriptor(mPmgr.getAccountLockItemDescriptorName());
			ItemDescriptorImpl profileItemDesc = (ItemDescriptorImpl) getProfileRepository().getItemDescriptor(mPmgr.getProfileItemDiscriptorName());
			userItemDesc.removeItemFromCache(pProfile.getRepositoryId());
			profileItemDesc.removeItemFromCache(pProfile.getRepositoryId());
			
			
		} catch (RepositoryException re) {
			if(isLoggingError()){
				logError(LogMessageFormatter.formatMessage(
							null, "RepositoryException BBBProfileManager.updateLoginAttemptCount()",	BBBCoreErrorConstants.ACCOUNT_ERROR_1262), re);
			}
		}
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.setloginAttempt: End");
		}
		return resetFlag;
	}
	/**
	 * update token value in profile when user submit reset password form
	 * @param pProfile repository profile item
	 * @param mEmailAddr email Address
	 * @param token encoded token
	 * 
	 */
	
	public void updateToken(RepositoryItem pProfile ,String mEmailAddr,String token)
	{
		
		MutableRepositoryItem mutableRepositoryItem = null;
			
	if(null !=token)
	{
		try {
			mutableRepositoryItem = getProfileRepository()
					.getItemForUpdate(pProfile.getRepositoryId(), mPmgr.getProfileItemDiscriptorName());

			if (mutableRepositoryItem != null) {
				mutableRepositoryItem.setPropertyValue(BBBCatalogConstants.FORGOT_PASSWORD_TOKEN, token);
				if (isLoggingDebug()) {
					logDebug("Updating the ForgotPwdToken property with encoded token value :" +token+" for email Id"+
				mutableRepositoryItem.getPropertyValue(BBBCoreConstants.EMAIL) );
				} 
			}
		} catch (RepositoryException re) {
			if(isLoggingError()){
				logError(LogMessageFormatter.formatMessage(
							null, "RepositoryException BBBProfileTools.updateToken() while updating ForgotPwdToken property",	BBBCoreErrorConstants.ACCOUNT_ERROR_1387), re);
			}
		}
		
	}}
	
	
	/**
	 * This method will store challenge question in Challenge Question Repository
	 * @param profileid
	 * @param questionMap
	 */	
	public void storeChallengeQuestionInProfile(String profileid,Map<String, Object> questionMap) {
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.storeChallengeQuestionInProfile: Start");
		}
		try {
			MutableRepositoryItem challengeQuestionRepositoryItem  = getChallengeQuestionRepository().getItemForUpdate(profileid, BBBCoreConstants.USER_CHALLENGE_QUESTION);
			if (challengeQuestionRepositoryItem == null) {
				final MutableRepositoryItem challengeQuestionRepositoryItemnew =this.getChallengeQuestionRepository().createItem(BBBCoreConstants.USER_CHALLENGE_QUESTION);
				challengeQuestionRepositoryItemnew.setPropertyValue(BBBCoreConstants.PROFILE_ID,profileid);
				challengeQuestionRepositoryItemnew.setPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1,questionMap.get(BBBCoreConstants.CHALLENGE_QUESTION_1).toString());
				challengeQuestionRepositoryItemnew.setPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_1, Base64.encodeToString(questionMap.get(BBBCoreConstants.CHALLENGE_ANSWER_1).toString().toLowerCase()));
				challengeQuestionRepositoryItemnew.setPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2,questionMap.get(BBBCoreConstants.CHALLENGE_QUESTION_2).toString());
				challengeQuestionRepositoryItemnew.setPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_2,  Base64.encodeToString(questionMap.get(BBBCoreConstants.CHALLENGE_ANSWER_2).toString().toLowerCase()));
				this.getChallengeQuestionRepository().addItem(challengeQuestionRepositoryItemnew);
				
			}else
			{
				challengeQuestionRepositoryItem.setPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1,questionMap.get(BBBCoreConstants.CHALLENGE_QUESTION_1).toString());
				challengeQuestionRepositoryItem.setPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_1, Base64.encodeToString(questionMap.get(BBBCoreConstants.CHALLENGE_ANSWER_1).toString().toLowerCase()));
				challengeQuestionRepositoryItem.setPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2,questionMap.get(BBBCoreConstants.CHALLENGE_QUESTION_2).toString());
				challengeQuestionRepositoryItem.setPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_2,  Base64.encodeToString(questionMap.get(BBBCoreConstants.CHALLENGE_ANSWER_2).toString().toLowerCase()));
				getChallengeQuestionRepository().updateItem(challengeQuestionRepositoryItem);
				
				
			}
			if (isLoggingDebug()) {
				logDebug("BBBProfileTools.storeChallengeQuestionInProfile: End");
			}
		}catch(RepositoryException re) {
			if(isLoggingError()){
				logError(LogMessageFormatter.formatMessage(
							null, "RepositoryException BBBProfileTools.storeChallengeQuestionInProfile while inserting challenge Question in repositry",	BBBCoreErrorConstants.ACCOUNT_ERROR_1388), re);
			}
		}
		
	}
	
	/**
	 * This method will return the rowid based on profileid from Challenge Question Repository
	 * @param profileId
	 * @return challengeQuestionRepositoryItem
	 */
	
	public MutableRepositoryItem fetchProfileIdFromChallengeQuestionRepository(String profileId) {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.fetchProfileIdFromChallengeQuestionRepository: Start");
		}
		MutableRepositoryItem repository=null;
		try {
			repository= (MutableRepositoryItem)getChallengeQuestionRepository().getItemForUpdate(profileId, BBBCoreConstants.USER_CHALLENGE_QUESTION);
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.fetchProfileIdFromChallengeQuestionRepository: End");
		}
		return repository;
		
	}
	
	public Map<String,Map<String,String>> fetchChallengeQuestionsFromRepository() {
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.fetchChallengeQuestionsFromRepository: Start");
		}

		
		Object[] params = null;
		String rql= BBBEximConstants.ALL;
		params = new Object[]{};
		
		IRepositoryWrapper iRepositoryWrapper = new RepositoryWrapperImpl(getChallengeQuestionCatalogRepository());		
		RepositoryItem[] items;		
		items = iRepositoryWrapper.queryRepositoryItems(BBBCoreConstants.QUESTIONS, rql, params,true);
		Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>(10);
		Map<String,String> quesType1Map= new HashMap<String,String>(10);
		Map<String,String> quesType2Map= new HashMap<String,String>(10);
		
		if(items!=null)
		{
			for( RepositoryItem repositoryItem :items)
			{
				String quesType=(String) repositoryItem.getPropertyValue(BBBCoreConstants.QUESTIONS_TYPE);
				String question=(String) repositoryItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTIONS_REP);
				if(BBBCoreConstants.QUESTION_1.equals(quesType))
				{
					quesType1Map.put(repositoryItem.getRepositoryId(), question);
				}
				else if(BBBCoreConstants.QUESTION_2.equals(quesType))
				{
					quesType2Map.put(repositoryItem.getRepositoryId(), question);
				}
			}
		}
		else
		{
			if (isLoggingDebug()) {
			logDebug("Challenge Question Repository is null");
			}
		}
		questionMap.put(BBBCoreConstants.STRING_ONE, quesType1Map);
		questionMap.put(BBBCoreConstants.STRING_TWO, quesType2Map);
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.fetchProfileIdFromChallengeQuestionRepository: End");
		}
		return questionMap;
		
	}
	
	
	//Adding a method to get profile using walletId
	public RepositoryItem findProfileByWalletId(String walletId) {
		try {
			MutableRepository repository = getProfileRepository();
			RepositoryView view = (getDefaultProfileType() == null) ? repository
					.getView(repository.getDefaultViewName()) : repository
					.getItemDescriptor(getDefaultProfileType()).getRepositoryView();

					RqlStatement statement = RqlStatement
					.parseRqlStatement("walletId = ?0");

					Object params[] = new Object[1];
					params[0] = walletId;

					
					RepositoryItem[] items = statement.executeQuery(view, params);
			if (isLoggingDebug()) {
				if (items != null) {
					logDebug("findByEmail resultSet=" + Arrays.asList(items)
							+ "; length=" + items.length + "; type="
							+ getDefaultProfileType());
				} else
					logDebug("findByEmail resultSet=null; type=" + getDefaultProfileType());
			}
			if ((items != null) && (items.length > 0)) {
				if (isLoggingDebug()) {
					logDebug("findByEmail found profile from walletId (" + walletId
							+ "): " + items[0] + "; type=" + getDefaultProfileType());
				}
				return items[0];
			}
		} catch (RepositoryException exc) {
			if (isLoggingError()) {
				logError(exc);
			}
		}
		return null;
	}
	
	
	
//  R 2.2 Start : Added for Cookie Auto Login for Mobile App 	
	/**
	 * Update the Auto Login Property Status also update the same in Cache 
	 * @param pProfile
	 * @param autoLoginFlag
	 * @param pRequest
	 * @return
	 */
	public boolean updateAutoLogin(RepositoryItem pProfile, boolean autoLoginFlag, DynamoHttpServletRequest pRequest){
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.updateAutoLogin : Start for auto Login Flag :" + autoLoginFlag);
		}
		
		MutableRepositoryItem mutableRepositoryItem = null;
		boolean autoLoginStatus = false;
		try {
			
				mutableRepositoryItem = getProfileRepository().getItemForUpdate(pProfile.getRepositoryId(), mPmgr.getProfileItemDiscriptorName());
				if(mutableRepositoryItem != null){
					mutableRepositoryItem.setPropertyValue(mPmgr.getAutoLoginPropertyName(), autoLoginFlag);
//					getProfileRepository().updateItem(mutableRepositoryItem);
					pRequest.setAttribute(this.getPropertyManager().getAutoLoginPropertyName(), autoLoginFlag);
					autoLoginStatus = true;
				
				
			/* Invalidate the cache for BBB user */
			ItemDescriptorImpl userItemDesc = (ItemDescriptorImpl) getProfileRepository().getItemDescriptor(mPmgr.getProfileItemDiscriptorName());
			ItemDescriptorImpl profileItemDesc = (ItemDescriptorImpl) getProfileRepository().getItemDescriptor(mPmgr.getProfileItemDiscriptorName());
			userItemDesc.removeItemFromCache(pProfile.getRepositoryId());
			profileItemDesc.removeItemFromCache(pProfile.getRepositoryId());
			logDebug("Updated the user auto login status");
			}
		} catch (RepositoryException re) {
			if(isLoggingError()){
				logError(LogMessageFormatter.formatMessage(
							null, "RepositoryException BBBProfileTools.updateAutoLogin() while updating auto login property",	BBBCoreErrorConstants.ACCOUNT_ERROR_1262), re);
			}
		}
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.updateAutoLogin: End");
		}
		return autoLoginStatus;
	}
	
	/**
	 * get the Auto Login Property Status
	 * @param pProfile
	 * @param autoLoginFlag
	 * @param pRequest
	 * @return
	 */
	public boolean getAutoLogin(RepositoryItem pProfile){
		MutableRepositoryItem mutableRepositoryItem = null;
		
		boolean isAutoLogin = false;
		try{
		mutableRepositoryItem = getProfileRepository().getItemForUpdate(pProfile.getRepositoryId(), mPmgr.getProfileItemDiscriptorName());
		}
		catch (RepositoryException re) {
			if(isLoggingError()){
				logError(LogMessageFormatter.formatMessage(
							null, "RepositoryException BBBProfileTools.updateAutoLogin() while updating auto login property",	BBBCoreErrorConstants.ACCOUNT_ERROR_1262), re);
			}
		}
		if(mutableRepositoryItem != null){
			isAutoLogin = (Boolean) mutableRepositoryItem.getPropertyValue(mPmgr.getAutoLoginPropertyName());
			//value=(Integer) mutableRepositoryItem.getPropertyValue(mPmgr.getAutoLoginPropertyName());
		}
		return isAutoLogin;
	}
	
//  R 2.2 End : Added for Cookie Auto Login for Mobile App
	
	/**
	 * method that checks if account is locked.  
	 * 
	 * @param pProfile 
	 * @return success/failure
	 */
	public boolean isAccountLocked(RepositoryItem pProfile){
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.isAccountLocked: Start");
		}
		
		int loginFailAttemptCount = 0;
		Timestamp loginFailAttemptTime = null;
		boolean isLocked = false;
		
		Timestamp currentSystemTime = new Timestamp(new java.util.Date().getTime());
		long currentSystemTimeinMills = currentSystemTime.getTime();
		long unlockTimeIntervalinMills = getUnLockTimeInterval() * 60 * 60 * 1000;
		long lastLoginFailAttemptTimeinMills = 0;
		RepositoryItem accountLockDetail = null;
				
		try {		
			
			accountLockDetail = pProfile.getRepository().getItem(pProfile.getRepositoryId(), mPmgr.getAccountLockItemDescriptorName());
			
			if(accountLockDetail == null){
				isLocked = false;
			}else {
				
				loginFailAttemptCount = ((Integer) accountLockDetail.getPropertyValue(mPmgr.getInvalidLoginAttemptCount())).intValue ();
	
				if(accountLockDetail.getPropertyValue(mPmgr.getLastInvalidLoginAttemptTime()) != null){
					loginFailAttemptTime = (Timestamp) accountLockDetail.getPropertyValue(mPmgr.getLastInvalidLoginAttemptTime());	
					lastLoginFailAttemptTimeinMills = loginFailAttemptTime.getTime();
				}
				
				if (loginFailAttemptCount >= getMaxFailAttemptCount()
						&& (currentSystemTimeinMills - lastLoginFailAttemptTimeinMills < unlockTimeIntervalinMills)) {
					isLocked = true;
				}
				
			}
			
		}catch (RepositoryException re) {
			if(isLoggingError()){
				logError(LogMessageFormatter.formatMessage(
							null, "RepositoryException BBBProfileManager.isAccountLocked()",	BBBCoreErrorConstants.ACCOUNT_ERROR_1261), re);
			}
		}
		
		if (isLoggingDebug()) {
			logDebug("BBBProfileTools.isAccountLocked: End");
		}
		
		return isLocked;
	}
	
	public void logDebug(String msg, Throwable e){
		if (isLoggingDebug()) {
			super.logDebug(msg, e);
		}
	}
	public void logDebug(String msg){
		if (isLoggingDebug()) {
			super.logDebug(msg);
		}
	}
	public void logDebug(Throwable e) {
		if (isLoggingDebug()) {
			super.logDebug(e);
		}
	}
	
	
	
	/**
	 * This method is used to execute RQL query.
	 *
	 * @param rqlQuery
	 *            the rql query
	 * @param params
	 *            the params
	 * @param viewName
	 *            the view name
	 * @param repository
	 *            the repository
	 * @return the repository item[]
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RepositoryItem[] executeRQLQuery(final String rqlQuery, final Object[] params,
			final String viewName, final MutableRepository repository)
			throws BBBSystemException, BBBBusinessException {

		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		this.logDebug("BBBProfileTools.executeRQLQuery() method start");

		if (rqlQuery != null) {
			if (repository != null) {
				try {
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					final RepositoryView view = repository.getView(viewName);
					if (view == null) {
						this.logError(BBBCoreErrorConstants.VIEW_IS_NULL + viewName+" view is null from executeRQLQuery of BBBProfileTools");
					}
					queryResult = statement.executeQuery(view, params);

					if(queryResult == null ){
						this.logDebug("No results returned for query [" + rqlQuery + "]");
					}

				} catch (final RepositoryException e) {
					this.logError(" Repository Exception [Unable to retrieve data] from executeRQLQuery of BBBProfileTools",e);
					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							e);
				}
			} else {
				this.logError(BBBCoreErrorConstants.REPOSITORY_IS_NULL + " Repository is null from executeRQLQuery of BBBProfileTools");
			}
		} else {
			this.logError(BBBCoreErrorConstants.QUERY_STRING_IS_NULL + " Query String is null from executeRQLQuery of BBBProfileTools");
		}

		this.logDebug("BBBProfileTools.executeRQLQuery() method ends");
		return queryResult;
	}

	/**
	 * 
	 * @param bbbProfileFormHandler TODO
	 * @param loginEmail
	 * @return
	 */
	
	boolean isProfileStatusShallow(String loginEmail){
		logDebug("Inside BBBProfileTools.isProfileStatusShallow loginEmail::" + loginEmail);

		RepositoryItem profileItem=  getItemFromEmail(loginEmail);
		String status="";
		if(profileItem!=null){
			status=(String)profileItem.getPropertyValue(getPmgr().getStatusPropertyName());    				
			if(!StringUtils.isBlank(status)) {
				logDebug("Profile Status: "+status);
			}
			else {
				logDebug("This is not a shallow Profile...");    				
			}

			return (BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE.equalsIgnoreCase(status)?true:false);		
		}
		return false;
	}

	private class CItemInfo {

		BBBCommerceItem bbbItem;
		List<String> shipGroupIds;
		List<Long> quantities;

		
		/**
		 * @return the shipGroupId
		 */
		public List<String> getShipGroupIds() {
			if (shipGroupIds == null) {
				shipGroupIds = new ArrayList<String>();
			}
			return shipGroupIds;
		}

		/**
		 * @return the quantity
		 */
		public List<Long> getQuantities() {
			if (quantities == null) {
				quantities = new ArrayList<Long>();
			}
			return quantities;
		}

		public BBBCommerceItem getBbbItem() {
			return bbbItem;
		}

		public void setBbbItem(BBBCommerceItem bbbItem) {
			this.bbbItem = bbbItem;
		}
	}
	
	/**
	 * Create shallow profile for wallet ID if not exist#BBBP-5042
	 * @param pRequest
	 * @param emailAddr
	 * @param walletId
	 * @param emailFlow
	 * @throws ServletException
	 */
	public void createShallowForNonExistingUser(DynamoHttpServletRequest pRequest, String emailAddr, String walletId, boolean emailFlow) throws ServletException {
		logDebug("Entring BBBProfileTools.createShallowForNonExistingUser emailAddr::" + emailAddr + " walletId::" + walletId + " emailFlow::" + emailFlow);
		boolean existingProfile = false;
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(COM_BBB_PROFILE_SESSION_SESSION_BEAN);
		
		// from wallet registration flow
		if (!BBBUtility.isEmpty(emailAddr) && BBBUtility.isValidEmail(emailAddr) && !emailFlow) {
			RepositoryItem userItemFromEmail = (RepositoryItem) getItemFromEmail(emailAddr.toLowerCase());
			logDebug("userItemFromEmail::" + userItemFromEmail);
			if (null != userItemFromEmail) {
				walletId =(String) userItemFromEmail.getPropertyValue(BBBCoreConstants.WALLETID);
				pRequest.setParameter(WALLET_ID_SHALLOW_PROFILE, walletId);
				String status = (String) userItemFromEmail.getPropertyValue(BBBCoreConstants.STATUS);
				logDebug("status::" + status);
				if((status == null )|| (status!=null && status.equalsIgnoreCase(BBBCoreConstants.FULL_PROFILE_STATUS_VALUE))){
					existingProfile=true;
				}
			}
			
		}else if(!BBBUtility.isEmpty(walletId)){
			RepositoryItem bbbUserProfile = findProfileByWalletId(walletId);
			String profileType = getCouponsManager().getStatusForProfile(bbbUserProfile);
			logDebug("profileType::" + profileType);
			if(profileType.equalsIgnoreCase(BBBCoreConstants.NOT_AVAILABLE) || profileType.equalsIgnoreCase(BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE)){
				//if profile not available get profile details from service and create one shallow profile
				if (BBBCoreConstants.NOT_AVAILABLE.equals(profileType)) {
					RepositoryItem profile = null;
					try {
						profile = getCouponsManager().getProfileManager().createShallowProfile(walletId);
					} catch (BBBSystemException | BBBBusinessException e) {
						logError("Exception in creating shallow prifle with walletID",e);
						throw new ServletException(e.getMessage());
					}
					logDebug("profile ::" + profile);
					if (profile != null) {
						sessionBean.setCouponEmail((String) profile.getPropertyValue(EMAIL));
						sessionBean.setCouponsWelcomeMsg(true);
						existingProfile = true;
						pRequest.setParameter(WALLET_ID_SHALLOW_PROFILE, walletId);
					} else {
						existingProfile = false;
						sessionBean.setCouponEmail(null);
						pRequest.setParameter(WALLET_ID_SHALLOW_PROFILE, walletId);
					}
				} else {
					existingProfile = true;
					sessionBean.setCouponEmail((String) bbbUserProfile.getPropertyValue(EMAIL));
					sessionBean.setCouponsWelcomeMsg(false);
					pRequest.setParameter(WALLET_ID_SHALLOW_PROFILE, walletId);
				}
			}else{
				existingProfile = true;
				sessionBean.setCouponEmail((String) bbbUserProfile.getPropertyValue(EMAIL));
				sessionBean.setCouponsWelcomeMsg(false);
				pRequest.setParameter(WALLET_ID_SHALLOW_PROFILE, walletId);
			}
		}
		
		pRequest.setParameter(EXISTING_PROFILE, existingProfile);
		logDebug("Exting from BBBProfileTools.createShallowForNonExistingUser");
	}	

	public boolean isRecognizedUser(DynamoHttpServletRequest request, RepositoryItem profile) {
		logDebug("Inside BBBProfileTools.isRecognizedUser ::" + profile);
		boolean isRcognized = false;
		int profileStatus = (Integer)profile.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		String channel = BBBUtility.getChannel();
		logDebug("profileStatus::" + profileStatus + " channel::" + channel);
		
		if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
			String cookieLogin = request.getHeader(BBBCoreConstants.COOKIE_AUTO_LOGIN);
			if (StringUtils.isNotBlank(cookieLogin) && Boolean.valueOf(cookieLogin)) {
				isRcognized = true;
			}
		} else if (profileStatus == 2) {
			isRcognized = true;
		}
		logDebug("Exting from BBBProfileTools.isRecognizedUser isRcognized::"+isRcognized);
		return isRcognized;
	}

	public Repository getChallengeQuestionCatalogRepository() {
		return challengeQuestionCatalogRepository;
	}

	public void setChallengeQuestionCatalogRepository(
			Repository challengeQuestionCatalogRepository) {
		this.challengeQuestionCatalogRepository = challengeQuestionCatalogRepository;
	}
	
    /**
     * Remove items from the current order
     * @param pRequest
     * @param order
     * @return success
     * @throws CommerceException
     * @throws BBBSystemException
     */
	
	public boolean removeAllItemsFromOrder(DynamoHttpServletRequest pRequest, Order order) throws CommerceException, BBBSystemException {
		 
		boolean shouldRollback = true;
		TransactionDemarcation td = new TransactionDemarcation();
		try {
			td.begin(this.getOrderManager().getOrderTools().getTransactionManager(), TransactionDemarcation.REQUIRED);
			synchronized (order) {
				getOrderManager().getCommerceItemManager().removeAllCommerceItemsFromOrder(order);
				getOrderManager().getShippingGroupManager().removeEmptyShippingGroups(order);
				shouldRollback = false;
			}
		} catch (CommerceException e) {
			logError("Error occurred while removing empty ship group", e);
			throw e;
		} catch (TransactionDemarcationException e) {
			logError("TransactionDemarcationException exception occurred", e);
			throw new BBBSystemException(e.getMessage());
		} finally {
			endTransactionDemarcation(shouldRollback, td);
		}

		return !shouldRollback;
		 
	}

	/**
	 * @param shouldRollback
	 * @param td
	 * @throws BBBSystemException
	 */
	protected void endTransactionDemarcation(boolean shouldRollback,
			TransactionDemarcation td) throws BBBSystemException {
		try {
			td.end(shouldRollback);
		} catch (TransactionDemarcationException e) {
			this.logError("TransactionDemarcationException exception occurred", e);
			throw new BBBSystemException(e.getMessage());
		}
	}
}
