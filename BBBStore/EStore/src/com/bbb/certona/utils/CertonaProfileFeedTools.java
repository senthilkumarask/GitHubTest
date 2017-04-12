/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CertonaProfileFeedScheduler.java
 *
 *  DESCRIPTION: Profile feed helper class. Fetches data from Profile repository based on date.
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  05/02/12 Initial version
 *
 */
package com.bbb.certona.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.multisite.SiteContext;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.certona.vo.CertonaGiftRegistryVO;
import com.bbb.certona.vo.CertonaProfileSiteVO;
import com.bbb.certona.vo.CertonaProfileVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

public class CertonaProfileFeedTools extends BBBGenericService {
	private Repository mUserProfileRepository;
	private Repository mGiftRepository;
	
	/** The Site context. */
	private SiteContext mSiteContext;
	
	/**
	 * @return the userProfileRepository
	 */
	public Repository getUserProfileRepository() {
		return mUserProfileRepository;
	}

	/**
	 * @param userProfileRepository the userProfileRepository to set
	 */
	public void setUserProfileRepository(final Repository pUserProfileRepository) {
		this.mUserProfileRepository = pUserProfileRepository;
	}

	/**
	 * @return the giftRepository
	 */
	public Repository getGiftRepository() {
		return mGiftRepository;
	}

	/**
	 * @param giftRepository the giftRepository to set
	 */
	public void setGiftRepository(final Repository pGiftRepository) {
		this.mGiftRepository = pGiftRepository;
	}
	
	private GiftRegistryManager mGiftRegistryManager;

	/**
	 * Gets the profile details based on the last modified date passed and
	 * populates the profile objects list
	 * 
	 * @param pIsFullDataFeed
	 * @param pLastModifiedDate
	 * @return cProfileVOList
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<CertonaProfileVO> getProfileDetails(final boolean pIsFullDataFeed, final Timestamp pLastModifiedDate,final String pRqlQueryRange)
			throws BBBSystemException, BBBBusinessException {

		logDebug("Entering getProfileDetails of CertonaProfileFeedTools");

		RepositoryItem[] profileItems = null;
		CertonaProfileVO cProfileVO = null;
		final List<CertonaProfileVO> pProfileVOList = new ArrayList<CertonaProfileVO>();
		profileItems = getProfileForFullOrPartialFeed(BBBCertonaConstants.PROFILE_VIEW_NAME,pRqlQueryRange);
		if (profileItems != null && profileItems.length > 0) {
			for (RepositoryItem repositoryItem : profileItems) {
				cProfileVO = this.populateProfileVO(repositoryItem);
				if (cProfileVO != null) {
					pProfileVOList.add(cProfileVO);
				}
			}
		}
		logDebug("Exiting getProfileDetails of CertonaProfileFeedTools");

		return pProfileVOList;
	}
	/**
	 * Gets the profile details based on the last modified date passed and
	 * populates the profile objects list
	 * 
	 * @param pIsFullDataFeed
	 * @param pLastModifiedDate
	 * @return cProfileVOList
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<CertonaProfileVO> getProfileDetails(final boolean pIsFullDataFeed, final Timestamp pLastModifiedDate)
			throws BBBSystemException, BBBBusinessException {

		logDebug("Entering getProfileDetails of CertonaProfileFeedTools");

		RepositoryItem[] profileItems = null;
		CertonaProfileVO cProfileVO = null;
		final List<CertonaProfileVO> cProfileVOList = new ArrayList<CertonaProfileVO>();
		if (pIsFullDataFeed || pLastModifiedDate == null) {
			profileItems = getProfileForFullFeed(BBBCertonaConstants.PROFILE_VIEW_NAME);
		} else {
			profileItems = getProfileForIncrementalFeed(pLastModifiedDate, BBBCertonaConstants.PROFILE_VIEW_NAME);
		}

		if (profileItems != null && profileItems.length > 0) {
			for (RepositoryItem repositoryItem : profileItems) {
				cProfileVO = this.populateProfileVO(repositoryItem);
				if (cProfileVO != null) {
					cProfileVOList.add(cProfileVO);
				}
			}
		}
		logDebug("Exiting getProfileDetails of CertonaProfileFeedTools");

		return cProfileVOList;
	}

	/**
	 * Populates the Profile Object
	 * 
	 * @param profileItem
	 * @return certonaProfileVO
	 */
	private CertonaProfileVO populateProfileVO(final RepositoryItem pProfileItem) {
		logDebug("Entering populateProfileVO of CertonaProfileFeedTools");
		CertonaProfileVO certonaProfileVO = null;
		ArrayList<String> userSiteIds = new ArrayList<String>();
		if (pProfileItem != null) {
			certonaProfileVO = new CertonaProfileVO();
			certonaProfileVO.setUserId(pProfileItem.getRepositoryId());
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.FIRST_NAME) != null) {
				certonaProfileVO.setUserFirstName((String) pProfileItem.getPropertyValue(BBBCertonaConstants.FIRST_NAME));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.LAST_NAME) != null) {
				certonaProfileVO.setUserLastName((String) pProfileItem.getPropertyValue(BBBCertonaConstants.LAST_NAME));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.REGISTRATION_DATE) != null) {
				certonaProfileVO.setRegistrationDate((Date) pProfileItem.getPropertyValue(BBBCertonaConstants.REGISTRATION_DATE));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.USER_TYPE) != null) {
				certonaProfileVO.setUserType((String) pProfileItem.getPropertyValue(BBBCertonaConstants.USER_TYPE));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.LOCALE) != null) {
				certonaProfileVO.setLocale((String) pProfileItem.getPropertyValue(BBBCertonaConstants.LOCALE));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.LAST_ACTIVITY) != null) {
				certonaProfileVO.setLastActivity((Date) pProfileItem.getPropertyValue(BBBCertonaConstants.LAST_ACTIVITY));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.EMAIL) != null) {
				certonaProfileVO.setEmailAddress((String) pProfileItem.getPropertyValue(BBBCertonaConstants.EMAIL));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.MOBILE_NUMBER) != null) {
				certonaProfileVO.setMobileNumber((String) pProfileItem.getPropertyValue(BBBCertonaConstants.MOBILE_NUMBER));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.PHONE_NUMBER) != null) {
				certonaProfileVO.setPhoneNumber((String) pProfileItem.getPropertyValue(BBBCertonaConstants.PHONE_NUMBER));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.DATE_OF_BIRTH) != null) {
				certonaProfileVO.setDateOfBirth((Date) pProfileItem.getPropertyValue(BBBCertonaConstants.DATE_OF_BIRTH));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.GENDER) != null) {
				certonaProfileVO.setGender((String) pProfileItem.getPropertyValue(BBBCertonaConstants.GENDER));
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.FACEBOOK_PROFILE) != null ) {
				certonaProfileVO.setFacebookIntegrated(true);
			}
			if (pProfileItem.getPropertyValue(BBBCertonaConstants.USER_SITE_ITEMS) != null) {
				@SuppressWarnings("unchecked")
				final Map<String, RepositoryItem> userSiteItems = (Map<String, RepositoryItem>) pProfileItem.getPropertyValue(BBBCertonaConstants.USER_SITE_ITEMS);
				if (userSiteItems != null) {
					final List<CertonaProfileSiteVO> certonaProfileSiteVOList = new ArrayList<CertonaProfileSiteVO>();
					final Set<String> keySet = userSiteItems.keySet();
					CertonaProfileSiteVO certonaProfileSiteVO = null;
					for (String key : keySet) {
						certonaProfileSiteVO = new CertonaProfileSiteVO();
						final RepositoryItem userSiteAssoc = userSiteItems.get(key);
						if (userSiteAssoc != null) {
							if (userSiteAssoc.getPropertyValue(BBBCertonaConstants.SITE_ID) != null) {
								certonaProfileSiteVO.setSiteId((String) userSiteAssoc.getPropertyValue(BBBCertonaConstants.SITE_ID));
								userSiteIds.add((String) userSiteAssoc.getPropertyValue(BBBCertonaConstants.SITE_ID));
							}
							if (userSiteAssoc.getPropertyValue(BBBCertonaConstants.FAVOURITE_STORE_ID) != null) {
								certonaProfileSiteVO.setStoreId((String) userSiteAssoc.getPropertyValue(BBBCertonaConstants.FAVOURITE_STORE_ID));
							}
							if (userSiteAssoc.getPropertyValue(BBBCertonaConstants.MEMBER_ID) != null) {
								certonaProfileSiteVO.setMemberId((String) userSiteAssoc.getPropertyValue(BBBCertonaConstants.MEMBER_ID));
							}
							certonaProfileSiteVOList.add(certonaProfileSiteVO);
						}
					}
					certonaProfileVO.setProfileSiteVOList(certonaProfileSiteVOList);
				}

			}
			
			final List<CertonaGiftRegistryVO> certonaGiftRegistryVOList = new ArrayList<CertonaGiftRegistryVO>();
			CertonaGiftRegistryVO certonaGiftRegistryVO = null;
			List<String> userRegIds = null;
			RepositoryItem[] registryIdRepItems;
			for (String siteId : userSiteIds) {
				try {
					registryIdRepItems = getGiftRegistryManager().fetchUserRegistries(siteId, pProfileItem.getRepositoryId());
					if (registryIdRepItems != null) {
						userRegIds = new ArrayList<String>(registryIdRepItems.length);
						for (int index = 0; index < registryIdRepItems.length; index++) {
							userRegIds.add(registryIdRepItems[index].getRepositoryId());
						}
					}
				} catch (RepositoryException e) {
					logError("Not getting data from repository ", e);
				} catch (BBBSystemException e) {
					logError("Not getting data from repository ", e);
				} catch (BBBBusinessException e) {
					logError("Not getting data from repository ", e);
				}
				if (userRegIds != null && !userRegIds.isEmpty()) {
					for (String userRegId : userRegIds) {
						RepositoryItem repositoryItem = null;
						try {
							repositoryItem = getGiftRepository().getItem(userRegId, "giftregistry");
						} catch (RepositoryException e) {
							logError("Not getting data from repository ", e);
						}
						certonaGiftRegistryVO = new CertonaGiftRegistryVO();
						certonaGiftRegistryVO.setRegistryId(userRegId);
						certonaGiftRegistryVO.setSiteId(siteId);
						if(repositoryItem != null){
							certonaGiftRegistryVO.setEventType((String) repositoryItem.getPropertyValue("eventType"));
							certonaGiftRegistryVO.setEventDate((Date) repositoryItem.getPropertyValue("eventDate"));
						}
						
						certonaGiftRegistryVOList.add(certonaGiftRegistryVO);
					}
				}
			}
			certonaProfileVO.setGiftRegistryVOList(certonaGiftRegistryVOList);
		}	
		logDebug("Exiting populateProfileVO of CertonaProfileFeedTools");
		return certonaProfileVO;
	}

	/**
	 * Fetches all the records from the profile
	 * 
	 * @return profileItems -All repository items for profile repository
	 * @throws BBBSystemException
	 */
	private RepositoryItem[] getProfileForFullFeed(final String viewName)
			throws BBBSystemException {
		logDebug("Entering getProfileForFullFeed of CertonaProfileFeedTools");
		RepositoryItem[] profileItems = null;
		try {
			final RepositoryView profileView = this.getUserProfileRepository().getView(viewName);
			final QueryBuilder queryBuilder = profileView.getQueryBuilder();
			final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();
			profileItems = profileView.executeQuery(getAllItemsQuery);
			if (profileItems == null) {
				logError("No profile items found for full feed");
				throw new BBBSystemException(BBBCatalogErrorCodes.INVENTORY_ITEMS_UNAVAILABLE,BBBCatalogErrorCodes.INVENTORY_ITEMS_UNAVAILABLE);
			}
		} catch (RepositoryException re) {
			logError(LogMessageFormatter.formatMessage(null,"CertonaProfileFeedTools.getProfileForFullFeed() | RepositoryException"), re);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,	re);
		}
		logDebug("Exiting getProfileForFullFeed of CertonaProfileFeedTools. "
					+ "Total number of profile items : "
					+  profileItems.length);
		return profileItems;
	}
	/**
	 * Fetches all the records from the profile
	 * 
	 * @return profileItems -All repository items for profile repository
	 * @throws BBBSystemException,BBBBusinessException
	 */
	private RepositoryItem[] getProfileForFullOrPartialFeed(final String viewName,final String pRqlQueryRange)
			throws BBBSystemException, BBBBusinessException{
		logDebug("Entering getProfileForFullFeed of CertonaProfileFeedTools");
		RepositoryItem[] profileItems = null;
		try {
			profileItems=this.executeRQLQuery(pRqlQueryRange, new Object[1], viewName,this.getUserProfileRepository());   
			if (profileItems == null) {
				logError("No profile items found for full feed");
				throw new BBBSystemException(BBBCatalogErrorCodes.INVENTORY_ITEMS_UNAVAILABLE);
			}
		} catch (RepositoryException re) {
			logError(LogMessageFormatter.formatMessage(null,"CertonaProfileFeedTools.getProfileForFullFeed() | RepositoryException"), re);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,	re);
		}
		logDebug("Exiting getProfileForFullFeed of CertonaProfileFeedTools. "
					+ "Total number of profile items : "
					+ profileItems.length);
		return profileItems;
	}

	/**
	 * Fetches records from profile repository whose modification date is less
	 * than the date supplied
	 * 
	 * @param lastModifiedDate
	 * @return profileItems - Profile items matching the criteria
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RepositoryItem[] getProfileForIncrementalFeed(
			final Timestamp lastModifiedDate, final String viewName)
			throws BBBSystemException, BBBBusinessException {
		logDebug("Entering getProfileForIncrementalFeed of CertonaProfileFeedTools");
		RepositoryItem[] profileItems = null;
		try {
			final RepositoryView profileView = this.getUserProfileRepository().getView(viewName);
			final QueryBuilder queryBuilder = profileView.getQueryBuilder();
			final QueryExpression pProperty = queryBuilder.createPropertyQueryExpression(BBBCertonaConstants.LAST_MODIFIED_DATE);
			final QueryExpression pValue = queryBuilder.createConstantQueryExpression(lastModifiedDate);
			final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.GREATER_THAN_OR_EQUALS);
			profileItems = profileView.executeQuery(query);
		} catch (RepositoryException re) {
			logError(LogMessageFormatter.formatMessage(null, "CertonaProfileFeedTools.getProfileForIncrementalFeed() | RepositoryException"), re);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,	re);
		}
		logDebug("Exiting getProfileForIncrementalFeed of CertonaProfileFeedTools. "
					+ "Total number of profile items : "
					+ (profileItems == null ? 0 : profileItems.length));
		return profileItems;
	}

	/**
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * @param giftRegistryManager the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager pGiftRegistryManager) {
		mGiftRegistryManager = pGiftRegistryManager;
	}

	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param siteContext the siteContext to set
	 */
	public void setSiteContext(SiteContext siteContext) {
		mSiteContext = siteContext;
	}
	private RepositoryItem[] executeRQLQuery(String rqlQuery, Object[] params, String viewName, Repository repository) throws RepositoryException, BBBSystemException, BBBBusinessException {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		if (rqlQuery != null) {
			if (repository != null) {
				try {
					long startTime = System.currentTimeMillis();
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					RepositoryView view = repository.getView(viewName);
					if (view == null ) {
						logInfo("View " + viewName + " is null");
					}

					queryResult = statement.executeQuery(view, params);
					if (queryResult == null) {

						logInfo("No results returned for query [" + rqlQuery + "]");

					}
					
						logInfo("Total Time taken by OnlineInventoryManager.executeRQLQuery() is:" + (System.currentTimeMillis() - startTime) + " for rqlQuery:" + rqlQuery);
					
				} catch (RepositoryException e) {

					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				}
			} else {
				
					logInfo("Repository has no data");
				
			}
		} else {
			
			logInfo("Query String is null");
			
		}

		return queryResult;
	}
}
