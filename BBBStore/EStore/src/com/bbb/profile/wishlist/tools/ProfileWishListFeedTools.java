/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ProfileFeedToolsImpl.java
 *
 *  DESCRIPTION: Implementation for migration of user profile. 
 *   	
 *  HISTORY:
 *  26/03/12 Initial version
 *  	
 */
package com.bbb.profile.wishlist.tools;

import java.util.concurrent.TimeUnit;
import atg.commerce.gifts.GiftlistManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.ProfileTools;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.wishlist.manager.BBBGiftlistManager;


/**
 * @author ikhan2
 * 
 */
public class ProfileWishListFeedTools extends BBBGenericService {

	private static final String OPEN = "OPEN";
	private static final String FAILED = "FAILED";
	private static final String PROCESSED = "PROCESSED";
	private static final String EMAIL_ID = "emailId";
	private static final String RQL_QUERY = "feedStatus EQUALS ?0 and feedId  = ? 1";
	private static final String FEED_STATUS = "feedStatus";
	private static final String SKU_ID = "skuId";
	private static final String SITE_ID = "siteId";
	private static final String QUANTITY2 = "quantity";
	private static final String PRODUCT_ID = "productId";
	private static final String WISH_LIST_FEEDS = "wishListFeeds";
	private static final String ERROR = "errorDescription";
	private static final String WISHLIST = "wishlist";
	
	private boolean isMergeAllowed = false;
	private Repository mFeedRepository;
	private int batchSize;
	private int mFeedIsCount=1;
	private GiftlistManager mGiftlistManager;
	private ProfileTools mProfileTools;
	/**
	 * Process wish list feeds
	 * 
	 * @param pSchedulerStartDate
	 * @return
	 */
	public boolean processWishListFeed() {

		/* Batch Processing */
		//int count = 0;
		boolean status = false;
		logDebug("ProfileWishListFeedTools  Method : processWishListFeed()");
		
		try {
			
			
			RepositoryItem[] openWishListItems = getOldProfileWishListItems(RQL_QUERY);

			while (openWishListItems.length!=0){
				populateProfileWishList(openWishListItems);
				openWishListItems = getOldProfileWishListItems(RQL_QUERY);
			}
			
			/*if (openWishListItems == null || openWishListItems.length == 0) {
				return false;
			}
			count = openWishListItems.length;
			status = populateProfileWishList(openWishListItems);
			while (count == this.getBatchSize()) {
				openWishListItems = null;
				openWishListItems = getOldProfileWishListItems(RQL_QUERY);
				if (openWishListItems == null || openWishListItems.length == 0) {
					return false;
				}
				populateProfileWishList(openWishListItems);
				count = openWishListItems.length;
			}*/
		} catch (BBBBusinessException e1) {
				logError(
						LogMessageFormatter
								.formatMessage(null,
										"ProfileWishListFeedTools.processWishListFeed() | BBBBusinessException "),
						e1);
			status = false;
		} catch (BBBSystemException e) {
				logError(
						LogMessageFormatter
								.formatMessage(null,
										"ProfileWishListFeedTools.processWishListFeed() | BBBSystemException "),
						e);
			status = false;
		}
		return status;
	}

	/**
	 * The method returns the wish list items from the WishList feed - DataBase
	 * 
	 * @param rqlQuery
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private RepositoryItem[] getOldProfileWishListItems(String pRqlQueryRange)
			throws BBBSystemException, BBBBusinessException {

		logDebug("Entering getOldProfileWishListItems of ProfileWishListFeedTools");

		RepositoryItem[] oldProfileWishList = null;

		try {
			Object[] params = new Object[2];
			params[0] = OPEN;
			params[1] = mFeedIsCount++;
			oldProfileWishList = this.executeRQLQuery(pRqlQueryRange,
					params, WISH_LIST_FEEDS, getFeedRepository());

		} catch (RepositoryException re) {
			if (isLoggingError()) {
				logError(
						LogMessageFormatter
								.formatMessage(null,
										"ProfileWishListFeedTools.getOldProfileWishListItems() | RepositoryException "),
						re);
			}
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					re);
		}

			logDebug("Exiting getOldProfileWishListItems of ProfileWishListFeedTools. "
					+ "Total number of OldProfileWishList items : "
					+ (oldProfileWishList == null ? 0 : oldProfileWishList.length));

		return oldProfileWishList;

	}

	/**
	 * The method populates the oldProfileWLItems for profile wish list feed with data from
	 * repository
	 * 
	 * @param isFullDataFeed
	 * @param categoryVOList
	 * @param indexOfFile
	 *            file no for which data is populated
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private boolean populateProfileWishList(RepositoryItem[] oldProfileWLItems) throws BBBBusinessException, BBBSystemException {

		// New code
		long startTime = System.currentTimeMillis();
		BBBGiftlistManager mgr = (BBBGiftlistManager) getGiftlistManager();
		if (oldProfileWLItems == null) {
			logError("Error oldProfileWLItems are null/empty");
			return false;
		}
		for (RepositoryItem feedItemtoMigrate : oldProfileWLItems) {

			// skip for null item
			if (feedItemtoMigrate == null) {
				continue;
			}

			String giftListID = getProfileGiftListID((String) feedItemtoMigrate.getPropertyValue(EMAIL_ID));
			String skuID = (String) feedItemtoMigrate.getPropertyValue(SKU_ID);
			String productID = (String) feedItemtoMigrate
					.getPropertyValue(PRODUCT_ID);
			long quantity = (Long) feedItemtoMigrate
					.getPropertyValue(QUANTITY2);
			String siteID = (String) feedItemtoMigrate
					.getPropertyValue(SITE_ID);
			String errorMessage = mgr.addCatalogItemToGiftlist(skuID,
					productID, giftListID, siteID, quantity, isMergeAllowed);
			String successStatus = PROCESSED;
			if (errorMessage != null) {
				logError("Error for item : " + errorMessage);
				successStatus = FAILED;
			}

			// update status in feed
			updateFeedItemStatus(feedItemtoMigrate, successStatus, errorMessage);

		}
			
		logInfo("Total Time taken by ProfileWishListFeedTools.populateProfileWishList() is:" + TimeUnit.SECONDS.convert((System.currentTimeMillis() - startTime),TimeUnit.MILLISECONDS) +" Seconds for batch Items: "+oldProfileWLItems.length + " for batch size :"+this.getBatchSize());

		return true;
	}

	private String getProfileGiftListID(String userEmail) {
		RepositoryItem pProfileGiftListItem = null;
		String pProfileGiftListID=null;
		RepositoryItem profile = (RepositoryItem) getProfileTools()
				.getItemFromEmail(userEmail);
		if (profile != null) {
			pProfileGiftListItem = (RepositoryItem) profile.getPropertyValue(WISHLIST);
			if(pProfileGiftListItem!=null){
				pProfileGiftListID = (String)pProfileGiftListItem.getRepositoryId();
			}else{
				logInfo("Gift List ID not found for user = "+userEmail);
			}
		}else{
			logInfo("Profile not Found with Email ID = "+userEmail);
		}
		
			return pProfileGiftListID;
		
	}

	/**
	 * Update the order substatus with the provided value
	 * 
	 * @param pOrder
	 * @param pSubstatus
	 * @return
	 * @throws BBBSystemException
	 */
	private boolean updateFeedItemStatus(RepositoryItem legacyWishListItem,
			String pStatus, String pErrorDesc) throws BBBSystemException {

		boolean success = false;
		MutableRepository repository = (MutableRepository) getFeedRepository();

		logDebug("START : Updating Wish List Feed Item ["
					+ legacyWishListItem.getRepositoryId() + "] to substatus ["
					+ pStatus + "]");
		try {
			/* Update the wishListItem item status */
			MutableRepositoryItem legacyWlItem = (MutableRepositoryItem) legacyWishListItem;

			// getPropertyManager().getSubstatusName()
			legacyWlItem.setPropertyValue(FEED_STATUS, pStatus);
			legacyWlItem.setPropertyValue(ERROR, pErrorDesc);
			/* Finally, update the order in repository */
			repository.updateItem(legacyWlItem);

			success = true;
		} catch (RepositoryException e) {
			String msg = "Error while updating legacy wish list item status ["
					+ legacyWishListItem.getRepositoryId() + "]";
			// BBBCoreErrorConstants.CART_ERROR_1026
			throw new BBBSystemException(
					"error in update status of feed wish list item", msg, e);
		}

		logDebug("END : Updating Wish List Feed Item ["
					+ legacyWishListItem.getRepositoryId() + "] to substatus ["
					+ pStatus + "]");

		return success;
	}
	private RepositoryItem[] executeRQLQuery(String rqlQuery, Object[] params,
			String viewName, Repository repository) throws RepositoryException,
			BBBSystemException, BBBBusinessException {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		if (rqlQuery != null) {
			if (repository != null) {
				try {
					long startTime = System.currentTimeMillis();
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					RepositoryView view = repository.getView(viewName);
					if (view == null && isLoggingError()) {
						logError("View " + viewName + " is null");
					}

					queryResult = statement.executeQuery(view, params);
					if (queryResult == null) {
						logInfo("No results returned for query [" + rqlQuery
								+ "]");
					}
					
					logInfo("Total Time taken by executeRQLQuery() is:"
								+ (System.currentTimeMillis() - startTime)
								+ " for rqlQuery:" + rqlQuery);
				} catch (RepositoryException e) {
					throw new BBBSystemException(
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							e);
				}
			} else {
				
				logInfo("Repository has no data");
				
			}
		} else {
			logInfo("Query String is null");
			
		}

		return queryResult;
	}

	public Repository getFeedRepository() {
		return mFeedRepository;
	}

	public ProfileTools getProfileTools() {
		return mProfileTools;
	}

	public void setProfileTools(ProfileTools pProfileTools) {
		this.mProfileTools = pProfileTools;
	}

	public void setFeedRepository(Repository pFeedRepository) {
		mFeedRepository = pFeedRepository;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public boolean isMergeAllowed() {
		return isMergeAllowed;
	}

	public void setMergeAllowed(boolean isMergeAllowed) {
		this.isMergeAllowed = isMergeAllowed;
	}

	public void setGiftlistManager(GiftlistManager pGiftlistManager) {
		mGiftlistManager = pGiftlistManager;
	}
	public GiftlistManager getGiftlistManager() {
		return mGiftlistManager;
	}

	/**
	 * @return the mFeedIsCount
	 */
	public int getFeedIsCount() {
		return mFeedIsCount;
	}

	/**
	 * @param mFeedIsCount the mFeedIsCount to set
	 */
	public void setFeedIsCount(int pFeedIsCount) {
		this.mFeedIsCount = pFeedIsCount;
	}	
}
