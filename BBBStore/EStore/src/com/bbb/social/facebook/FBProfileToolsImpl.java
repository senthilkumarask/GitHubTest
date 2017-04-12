/**
 * 
 */
package com.bbb.social.facebook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import atg.multisite.Site;
import atg.multisite.SiteGroup;
import atg.multisite.SiteGroupManager;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.social.facebook.vo.FriendVO;
import com.bbb.social.facebook.vo.SchoolVO;
import com.bbb.social.facebook.vo.UserVO;
import com.bbb.utils.BBBUtility;

/**
 * Facebook Profile Tools implementation. It uses Profile repository to do
 * repository interaction
 * 
 * @author alakra
 * 
 */
/**
 * @author hbandl
 *
 */
/**
 * @author hbandl
 *
 */
/**
 * @author hbandl
 *
 */
/**
 * @author hbandl
 *
 */
/**
 * @author hbandl
 *
 */
/**
 * @author hbandl
 *
 */
/**
 * @author hbandl
 *
 */
/**
 * @author hbandl
 *
 */
/**
 * @author hbandl
 * 
 */
public class FBProfileToolsImpl extends BBBGenericService implements
		FBProfileTools {

	private PropertyManager mPropertyManager;

	private MutableRepository mProfileRepository;

	private BBBPropertyManager mBBBProfilePropertyManager;

	private SiteGroupManager mSiteGroup;

	public FBProfileToolsImpl() {
		// Default Constructor
	}

	/**
	 * @return the BBB Site group Manager
	 */
	public SiteGroupManager getSiteGroup() {
		return mSiteGroup;
	}

	/**
	 * @param pSiteGroup
	 *            the site group manager to set
	 */
	public final void setSiteGroup(SiteGroupManager pSiteGroup) {
		mSiteGroup = pSiteGroup;
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
	 * @return the propertyManager
	 */
	public final PropertyManager getPropertyManager() {
		return mPropertyManager;
	}

	/**
	 * @param pPropertyManager
	 *            the propertyManager to set
	 */
	public final void setPropertyManager(PropertyManager pPropertyManager) {
		mPropertyManager = pPropertyManager;
	}

	public void setProfileRepository(MutableRepository pProfileRepository) {
		mProfileRepository = pProfileRepository;
	}

	public MutableRepository getProfileRepository() {
		return mProfileRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.social.facebook.FBProfileTools#createUser(atg.repository.
	 * RepositoryItem, com.bbb.social.facebook.vo.UserVO, java.lang.String)
	 */
	public RepositoryItem createUser(RepositoryItem bbbUserProfile,
			UserVO pUser) throws BBBSystemException {

		MutableRepositoryItem fbUserMutableItem = null;
		RepositoryItem newFBUserCreated = null;
		String opName = "FBProfileToolImpl.createUser()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			logDebug(opName + " : Start");
		try {

			fbUserMutableItem = getProfileRepository().createItem(
					getPropertyManager().getFacebookUserPropertyName());
			convertUserToRepositoryItem(pUser, fbUserMutableItem,
					bbbUserProfile);

			/* Create the schools for the given user */
			Set<MutableRepositoryItem> schoolItems = createSchools(pUser);
			if (schoolItems != null && schoolItems.size() > 0) {
				fbUserMutableItem.setPropertyValue(getPropertyManager()
						.getSchoolsPropertyName(), schoolItems);
			}

			/* create Facebook and BBB user profile association */
			convertBBBProfileFBProfileAssoc(fbUserMutableItem, bbbUserProfile,
					FBConstants.FB_OPERATION_CREATE);

			/* creating facebook profile along with BBB profile association */
			getProfileRepository().addItem(fbUserMutableItem);

			newFBUserCreated = fbUserMutableItem;
			/* Invalidate the cache for BBB user */
			ItemDescriptorImpl userItemDesc = (ItemDescriptorImpl) getProfileRepository()
					.getItemDescriptor(
							getPropertyManager().getUserPropertyName());
			userItemDesc.removeItemFromCache(bbbUserProfile.getRepositoryId());

		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(null, "RepositoryException FBProfileToolImpl.createUser() while creating Facebook user profile in FBProfileToolImpl.createUser()", BBBCoreErrorConstants.ACCOUNT_ERROR_1239 ), e);
			
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1239,
					"Error while creating Facebook user profile", e);
		}

			logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return newFBUserCreated;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.social.facebook.FBProfileTools#updateUser(atg.repository.
	 * MutableRepositoryItem, atg.repository.RepositoryItem,
	 * com.bbb.social.facebook.vo.UserVO, java.lang.String)
	 */
	public RepositoryItem updateUser(
			MutableRepositoryItem mutableFbUserProfile,
			RepositoryItem bbbUserProfile, UserVO pUser)
			throws BBBSystemException {

		RepositoryItem updatedFbUser = null;
		String opName = "FBProfileToolImpl.updateUser()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			logDebug(opName + " : Start");

		try {
			convertUserToRepositoryItem(pUser, mutableFbUserProfile,
					bbbUserProfile);

			/* Update the schools for the given user */
			updateSchools(pUser, mutableFbUserProfile);

			/* updating FB and BBB user profile association */
			convertBBBProfileFBProfileAssoc(mutableFbUserProfile,
					bbbUserProfile,	FBConstants.FB_OPERATION_UPDATE);

			/* Finally, update the user and mark the flag */
			getProfileRepository().updateItem(mutableFbUserProfile);

			updatedFbUser = mutableFbUserProfile;

			/* Invalidate the cache for BBB user */
			ItemDescriptorImpl userItemDesc = (ItemDescriptorImpl) getProfileRepository()
					.getItemDescriptor(
							getPropertyManager().getUserPropertyName());
			userItemDesc.removeItemFromCache(bbbUserProfile.getRepositoryId());

		} catch (RepositoryException e) {
			String msg = "Error while updating Facebook user profile["
					+ pUser.getName() + "]";
			
			logError(LogMessageFormatter.formatMessage(null, "RepositoryException in FBProfileToolImpl.updateUser() while updating Facebook user profile FBProfileToolImpl.updateUser() ", BBBCoreErrorConstants.ACCOUNT_ERROR_1240 ), e);
			
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1240,msg, e);
		}

			logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return updatedFbUser;
	}

	/*
	 * Returns Repository items for user's education updates
	 */
	private Set<MutableRepositoryItem> createSchools(UserVO user)
			throws BBBSystemException {
		MutableRepository repository = getProfileRepository();
		Set<MutableRepositoryItem> schoolItems = null;
		String opName = "FBProfileToolImpl.createSchools()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

			logDebug(opName + " : Start");

		try {
			Set<SchoolVO> schools = user.getSchools();
			if (schools != null && schools.size() > 0) {
				schoolItems = new HashSet<MutableRepositoryItem>();
				for (Iterator<SchoolVO> iter = schools.iterator(); iter
						.hasNext();) {
					SchoolVO school = iter.next();

					MutableRepositoryItem schoolItem = repository
							.createItem(getPropertyManager()
									.getFacebookSchoolPropertyName());
					schoolItem.setPropertyValue(getPropertyManager()
							.getSchoolIDPropertyName(), school.getSchoolID());
					schoolItem.setPropertyValue(getPropertyManager()
							.getNamePropertyName(), school.getName());

					schoolItems.add(schoolItem);
				}
			}
		} catch (RepositoryException e) {
			String msg = "Error while creating Facebook schools profile for user["
					+ user.getName() + "]";
			if (isLoggingError()) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException in FBProfileToolImpl.createSchools() while creating Facebook schools profile for user " + user.getName() , BBBCoreErrorConstants.ACCOUNT_ERROR_1241 ), e);
			}
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1241,msg, e);
		}

			logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return schoolItems;
	}

	/*
	 * Given Facebook user instance, returns Repository items for update
	 */
	private void updateSchools(UserVO pUser, MutableRepositoryItem pUserItem)
			throws BBBSystemException {
		MutableRepository repository = getProfileRepository();
		String opName = "FBProfileToolImpl.updateSchools()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

			logDebug(opName + " : Start");

		Set<SchoolVO> schools = pUser.getSchools();
		if (schools != null && schools.size() > 0) {
			Set<MutableRepositoryItem> newSchoolItems = new HashSet<MutableRepositoryItem>();
			@SuppressWarnings("unchecked")
			Set<MutableRepositoryItem> schoolItems = (Set<MutableRepositoryItem>) pUserItem
					.getPropertyValue(getPropertyManager()
							.getSchoolsPropertyName());
				logDebug("Existing school count profile for user["
						+ pUser.getName() + "] :" + schoolItems.size());

			boolean exists = false;
			for (Iterator<SchoolVO> iter = schools.iterator(); iter.hasNext();) {
				SchoolVO school = iter.next();
				MutableRepositoryItem schoolItem = null;

				for (Iterator<MutableRepositoryItem> itemIter = schoolItems
						.iterator(); itemIter.hasNext();) {
					exists = false;
					schoolItem = itemIter.next();
					if (StringUtils.equals((String) schoolItem
							.getPropertyValue(getPropertyManager()
									.getIdPropertyName()), school.getId())) {
						exists = true;
						break;
					}
				}

				if (!exists) {
						logDebug("The user ["
								+ pUser.getName()
								+ "] does not have an existing school by name ["
								+ school.getName() + "]. Adding...");

					try {
						schoolItem = repository.createItem(getPropertyManager()
								.getFacebookSchoolPropertyName());
					} catch (RepositoryException re) {
						String msg = "Error while creating Facebook school for user["
								+ pUser.getName() + "]";
						if (isLoggingError()) {
							logError(LogMessageFormatter.formatMessage(null,"RepositoryException in FBProfileToolImpl.updateSchools() while creating Facebook school for use " + pUser.getName(), BBBCoreErrorConstants.ACCOUNT_ERROR_1242 ), re);
						}
						BBBPerformanceMonitor.cancel(
								BBBPerformanceConstants.FACEBOOK_INTEGRATION,
								opName);
						throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1242,msg, re);
					}
				}
				if(null!=schoolItem){
					schoolItem.setPropertyValue(getPropertyManager()
							.getSchoolIDPropertyName(), school.getSchoolID());
					schoolItem.setPropertyValue(getPropertyManager()
							.getNamePropertyName(), school.getName());
					newSchoolItems.add(schoolItem);
				}
			}

			if (!newSchoolItems.isEmpty()) {
				pUserItem.setPropertyValue(getPropertyManager()
						.getSchoolsPropertyName(), newSchoolItems);
					logDebug("New school count for user[" + pUser.getName()
							+ "] :" + newSchoolItems.size());
			}

		}

			logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
	}

	/*
	 * Transform Facebook user instance to Repository item
	 */
	private void convertUserToRepositoryItem(UserVO userVO,
			MutableRepositoryItem mutableFBUserProfile,
			RepositoryItem bbUserProfile) throws RepositoryException {

		mutableFBUserProfile.setPropertyValue(getPropertyManager()
				.getUserNamePropertyName(), userVO.getUserName());
		mutableFBUserProfile.setPropertyValue(getPropertyManager()
				.getNamePropertyName(), userVO.getName());
		mutableFBUserProfile.setPropertyValue(getPropertyManager()
				.getFirstNamePropertyName(), userVO.getFirstName());
		mutableFBUserProfile.setPropertyValue(getPropertyManager()
				.getMiddleNamePropertyName(), userVO.getMiddleName());
		mutableFBUserProfile.setPropertyValue(getPropertyManager()
				.getLastNamePropertyName(), userVO.getLastName());
		mutableFBUserProfile.setPropertyValue(getPropertyManager()
				.getGenderPropertyName(), userVO.getGender());

	}

	/*
	 * Transform Facebook user instance to Repository item
	 */
	private void convertBBBProfileFBProfileAssoc(
			MutableRepositoryItem mutableFBUserProfile,
			RepositoryItem bbUserProfile, String operation) throws RepositoryException {

		if(operation.equalsIgnoreCase(FBConstants.FB_OPERATION_REMOVE)){
			mutableFBUserProfile.setPropertyValue(getPropertyManager()
					.getBBBProfilePropertyName(), null);
		}else{
			mutableFBUserProfile.setPropertyValue(getPropertyManager()
					.getBBBProfilePropertyName(), bbUserProfile);
		}

	}

	/*
	 * Transform Repository item Facebook user instance
	 */
	private UserVO convertRepositoryItemToUser(RepositoryItem item, UserVO user) {
		user.setID((String) item.getPropertyValue(getPropertyManager()
				.getIdPropertyName()));
		user.setUserName((String) item.getPropertyValue(getPropertyManager()
				.getUserNamePropertyName()));
		user.setName((String) item.getPropertyValue(getPropertyManager()
				.getNamePropertyName()));
		user.setFirstName((String) item.getPropertyValue(getPropertyManager()
				.getFirstNamePropertyName()));
		user.setMiddleName((String) item.getPropertyValue(getPropertyManager()
				.getMiddleNamePropertyName()));
		user.setLastName((String) item.getPropertyValue(getPropertyManager()
				.getLastNamePropertyName()));
		user.setGender((String) item.getPropertyValue(getPropertyManager()
				.getGenderPropertyName()));
		user.setVerified((Boolean) item.getPropertyValue(getPropertyManager()
				.getVerifiedPropertyName()));
		user.setOfflineAccessToken((String) item
				.getPropertyValue(getPropertyManager()
						.getOfflineAccessTokenPropertyName()));
		user.setTokenValid((Boolean) item.getPropertyValue(getPropertyManager()
				.getTokenValidPropertyName()));

		RepositoryItem bbbUser = (RepositoryItem) item
				.getPropertyValue(getPropertyManager()
						.getBBBProfilePropertyName());
		if (bbbUser != null) {
			user.setBBBUserID((String) bbbUser
					.getPropertyValue(getPropertyManager().getIdPropertyName()));
		}

		@SuppressWarnings("unchecked")
		Set<RepositoryItem> friendsItem = (Set<RepositoryItem>) item
				.getPropertyValue(getPropertyManager().getFriendsPropertyName());
		if (friendsItem != null && friendsItem.size() > 0) {
			Set<FriendVO> friends = new HashSet<FriendVO>();
			for (Iterator<RepositoryItem> iter = friendsItem.iterator(); iter
					.hasNext();) {
				RepositoryItem friendItem = iter.next();
				FriendVO friend = new FriendVO();

				friend.setId((String) friendItem
						.getPropertyValue(getPropertyManager()
								.getIdPropertyName()));
				friend.setUserName((String) friendItem
						.getPropertyValue(getPropertyManager()
								.getUserNamePropertyName()));
				friend.setName((String) friendItem
						.getPropertyValue(getPropertyManager()
								.getNamePropertyName()));
				friends.add(friend);
			}

			user.setFriends(friends);
		}

		@SuppressWarnings("unchecked")
		Set<RepositoryItem> schoolsItem = (Set<RepositoryItem>) item
				.getPropertyValue(getPropertyManager().getSchoolsPropertyName());
		if (schoolsItem != null && schoolsItem.size() > 0) {
			Set<SchoolVO> schools = new HashSet<SchoolVO>();
			for (Iterator<RepositoryItem> iter = schoolsItem.iterator(); iter
					.hasNext();) {
				RepositoryItem schoolItem = iter.next();
				SchoolVO school = new SchoolVO();

				school.setId((String) schoolItem
						.getPropertyValue(getPropertyManager()
								.getIdPropertyName()));
				school.setSchoolID((String) schoolItem
						.getPropertyValue(getPropertyManager()
								.getSchoolIDPropertyName()));
				school.setName((String) schoolItem
						.getPropertyValue(getPropertyManager()
								.getNamePropertyName()));
				schools.add(school);
			}

			user.setSchools(schools);
		}
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.social.facebook.FBProfileTools#removeUser(atg.repository.
	 * MutableRepositoryItem, atg.repository.RepositoryItem, java.lang.String)
	 */
	public boolean removeUser(MutableRepositoryItem mutableFbUserProfile,
			RepositoryItem bbbUserProfile)
			throws BBBSystemException {

		boolean success = false;
		String opName = "FBProfileToolImpl.removeUser()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

			logDebug(opName + " : Start");

		try {

			convertBBBProfileFBProfileAssoc(mutableFbUserProfile,
					bbbUserProfile, FBConstants.FB_OPERATION_REMOVE);

			/* Finally, update the user and mark the flag */
			getProfileRepository().updateItem(mutableFbUserProfile);
			success = true;

			/* Invalidate the cache for BBB user */
			ItemDescriptorImpl userItemDesc = (ItemDescriptorImpl) getProfileRepository()
					.getItemDescriptor(
							getPropertyManager().getUserPropertyName());
			userItemDesc.removeItemFromCache(bbbUserProfile.getRepositoryId());

		} catch (RepositoryException e) {
			
			logError(LogMessageFormatter.formatMessage(null, "RepositoryException in FBProfileToolImpl.removeUser() while removing Facebook profile for user["+ mutableFbUserProfile.getPropertyValue(getPropertyManager()
								.getUserNamePropertyName()) + "]", BBBCoreErrorConstants.ACCOUNT_ERROR_1243 ), e);
			
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1243,
					"Error while removing Facebook profile for user["
							+ mutableFbUserProfile.getPropertyValue(getPropertyManager()
									.getUserNamePropertyName()) + "]", e);
		}

			logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return success;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.social.facebook.FBProfileTools#getFacebookUserProfile(java.lang
	 * .String)
	 */
	public RepositoryItem getFacebookUserProfile(String pFBAccountId)
			throws BBBSystemException {

		String opName = "FBProfileToolImpl.getFacebookUserProfile()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			logDebug(opName + "Verifying if facebook user [" + pFBAccountId
					+ "] has existing profiles in database: START");
		RepositoryItem user = null;

		try {

			/* Retrieve potential existing facebook user profile */
			Repository repository = getProfileRepository();
			RepositoryItemDescriptor useritemDescriptor = repository
					.getItemDescriptor(getPropertyManager()
							.getFacebookUserPropertyName());
			RepositoryView userview = useritemDescriptor.getRepositoryView();
			QueryBuilder fbQueryBuilder = userview.getQueryBuilder();
			/* Using query, only pull those profiles whose token are valid */
			QueryExpression queryExpressionProperty = fbQueryBuilder
					.createPropertyQueryExpression(getPropertyManager()
							.getUserNamePropertyName());
			QueryExpression queryValueProperty = fbQueryBuilder
					.createConstantQueryExpression(pFBAccountId);
			Query fbValidUsersQuery = fbQueryBuilder.createComparisonQuery(
					queryExpressionProperty, queryValueProperty,
					QueryBuilder.EQUALS);

				logDebug("Verifying existing facebook profiles in database using query: "
						+ fbValidUsersQuery);

			RepositoryItem[] users = userview.executeQuery(fbValidUsersQuery);

			if (users != null && users.length > 0) {
				user = users[0];
			}

		} catch (RepositoryException e) {

			logError(LogMessageFormatter.formatMessage(null, "RepositoryException in FBProfileToolImpl.getFacebookUserProfile() while fetching Facebook user profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1244 ), e);
			
			BBBPerformanceMonitor.cancel(
					BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			throw new BBBSystemException(
					BBBCoreErrorConstants.ACCOUNT_ERROR_1244,"Error while fetching Facebook user profile", e);

		}

			logDebug(opName + "Verifying if facebook user [" + pFBAccountId
					+ "] has existing profiles in database: END");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return user;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.social.facebook.FBProfileTools#findUserSiteAssociation(java.util
	 * .Map, java.lang.String)
	 */
	public String findUserSiteAssociation(RepositoryItem bbbUser,
			String pCurrentSiteId) {
		String opName = "FBProfileToolImpl.findUserSiteAssociation()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			logDebug(opName + " : Start");

		String siteAssociation = "";

		if (bbbUser != null) {
			if (pCurrentSiteId != null && mSiteGroup != null) {
				Collection<SiteGroup> siteGroupList = mSiteGroup
						.getSiteGroupsBySite(pCurrentSiteId);
				Map<String, Object> userSiteAssociationMap = (Map<String, Object>) bbbUser
						.getPropertyValue(getBbbProfilePropertyManager()
								.getUserSiteItemsPropertyName());

				if (!BBBUtility.isMapNullOrEmpty(userSiteAssociationMap)) {
					if (userSiteAssociationMap.containsKey(pCurrentSiteId)) {
						siteAssociation = FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE;
					} else {
						if (siteGroupList != null && !siteGroupList.isEmpty()) {
							for (SiteGroup group : siteGroupList) {
								Collection<Site> sites = group.getSites();
								for (Site site : sites) {
									String otherGroupSite = site.getId();
									if (userSiteAssociationMap
											.containsKey(otherGroupSite)) {
										siteAssociation = FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP;
									}
								}
							}
						}
					}
				}

				if (siteAssociation.equalsIgnoreCase(FBConstants.EMPTY_STRING)) {
					siteAssociation = FBConstants.EVENT_BBB_PROFILE_EXIST_ON_DIFFERENT_SITE_GROUP;
				}

			}
		}
			logDebug(opName + " : End");

		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return siteAssociation;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.social.facebook.FBProfileTools#linkBBBProfileWithFBProfile(atg
	 * .repository.RepositoryItem, com.bbb.social.facebook.vo.UserVO,
	 * java.lang.String)
	 */
	public RepositoryItem linkBBBProfileWithFBProfile(
			RepositoryItem bbbUserProfile, UserVO fbUserProfile) throws BBBSystemException {
		String opName = "FBProfileToolImpl.linkBBBProfileWithFBProfile()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			logDebug(opName + " : Start");

		RepositoryItem processedProfile = null;

		if (bbbUserProfile != null && fbUserProfile != null) {
			try {
				RepositoryItem fbRepositoryItem = getFacebookUserProfile(fbUserProfile
						.getUserName());
				if (fbRepositoryItem != null) {
					
					RepositoryItem bbbProfileForFB = (RepositoryItem) fbRepositoryItem.getPropertyValue(getPropertyManager().getBBBProfilePropertyName());
					if(bbbProfileForFB == null || bbbProfileForFB.getRepositoryId() == bbbUserProfile.getRepositoryId()){
						MutableRepositoryItem fbUserMutableItem = getProfileRepository()
								.getItemForUpdate(
										(String) fbRepositoryItem.getPropertyValue(getPropertyManager()
												.getIdPropertyName()),
										getPropertyManager()
												.getFacebookUserPropertyName());
						
						processedProfile = updateUser(fbUserMutableItem,
								bbbUserProfile, fbUserProfile);
					}

				} else {
					processedProfile = createUser(bbbUserProfile,
							fbUserProfile);
				}

			} catch (RepositoryException e) {
				
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException in FBProfileToolImpl.linkBBBProfileWithFBProfile() occured while linking a facebook profile with BBB profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1245 ), e);
				
				BBBPerformanceMonitor.cancel(
						BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
				throw new BBBSystemException(
						BBBCoreErrorConstants.ACCOUNT_ERROR_1245,"Error occured while linking a facebook profile with BBB profile",
						e);
			}
		}

			logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return processedProfile;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.social.facebook.FBProfileTools#unlinkBBBProfileWithFBProfile(
	 * atg.repository.RepositoryItem, com.bbb.social.facebook.vo.UserVO,
	 * java.lang.String)
	 */
	public boolean unlinkBBBProfileWithFBProfile(RepositoryItem bbbUserProfile) throws BBBSystemException {
		String opName = "FBProfileToolImpl.unlinkBBBProfileWithFBProfile()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			logDebug(opName + " : Start");

		boolean status = false;
		if (bbbUserProfile != null) {
			try {
				RepositoryItem bbbUserFBAssoc = (RepositoryItem) bbbUserProfile
						.getPropertyValue(getPropertyManager()
								.getFacebookProfilePropertyName());
				if (bbbUserFBAssoc != null) {
					MutableRepositoryItem fbUserMutableItem = getProfileRepository()
								.getItemForUpdate(
					(String) bbbUserFBAssoc.getPropertyValue(getPropertyManager()
								.getIdPropertyName()),
									getPropertyManager()
											.getFacebookUserPropertyName());
					status = removeUser(fbUserMutableItem,
							bbbUserProfile);
				}
					
			} catch (RepositoryException e) {
				
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException in FBProfileToolImpl.unlinkBBBProfileWithFBProfile()  occured while unlinking a facebook profile with BBB profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1246 ), e);
				
				BBBPerformanceMonitor.cancel(
						BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
				throw new BBBSystemException(
						BBBCoreErrorConstants.ACCOUNT_ERROR_1246,"Error occured while unlinking a facebook profile with BBB profile",
						e);
			}
		}

			logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return status;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.social.facebook.FBProfileTools#getBBBEmailForFBProfile(java.lang
	 * .String)
	 */
	public String getBBBEmailForFBProfile(String pFbAccountId) throws BBBSystemException {

		String bbbEmailAddress = null;
		String opName = "FBProfileToolImpl.getBBBEmailForFBProfile()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

		if (!BBBUtility.isEmpty(pFbAccountId)) {
			try {
				RepositoryItem fbProfile = getFacebookUserProfile(pFbAccountId);
				if (fbProfile != null) {
					RepositoryItem bbbProfile = (RepositoryItem) fbProfile
							.getPropertyValue(getPropertyManager()
									.getBBBProfilePropertyName());
					if (bbbProfile != null) {
						bbbEmailAddress = (String) bbbProfile
								.getPropertyValue(getBbbProfilePropertyManager()
									.getEmailAddressPropertyName());
					}
				}

			} catch (BBBSystemException e) {
				
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in FBProfileToolImpl.getBBBEmailForFBProfile() occured while getting BBB profile deteil through Facebook profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1247 ), e);
				
				BBBPerformanceMonitor.cancel(
						BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
				throw new BBBSystemException(
						BBBCoreErrorConstants.ACCOUNT_ERROR_1247,"Error occured while getting BBB profile deteil through Facebook profile",
						e);
			}

		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return bbbEmailAddress;
	}

	/**
	 * this method will return BBB profile email address against FBProfile on
	 * sister site
	 * 
	 * @param pFbAccountId
	 * @param pCurrentSiteId
	 * @return
	 * @throws BBBSystemException
	 */
	public String getSisterSiteBBBEmailForFBProfile(String pFbAccountId) throws BBBSystemException {

		String bbbEmailAddress = null;
		String opName = "FBProfileToolImpl.getSisterSiteBBBEmailForFBProfile()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

		if (!BBBUtility.isEmpty(pFbAccountId)) {
			try {
				RepositoryItem fbProfile = getFacebookUserProfile(pFbAccountId);
				if (fbProfile != null) {
					RepositoryItem bbbProfile = (RepositoryItem) fbProfile
							.getPropertyValue(getPropertyManager()
									.getBBBProfilePropertyName());
					if (bbbProfile != null) {
						bbbEmailAddress = (String) bbbProfile
								.getPropertyValue(getBbbProfilePropertyManager()
										.getEmailAddressPropertyName());
					}
				}
			} catch (BBBSystemException e) {
				
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in FBProfileToolImpl.getSisterSiteBBBEmailForFBProfile() occured while getting BBB profile details through Facebook profile", BBBCoreErrorConstants.ACCOUNT_ERROR_1248 ), e);
				
				BBBPerformanceMonitor.cancel(
						BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
				throw new BBBSystemException(
						BBBCoreErrorConstants.ACCOUNT_ERROR_1248,"Error occured while getting BBB profile deteil through Facebook profile",
						e);
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return bbbEmailAddress;
	}

	public List<Site> getSisterSite(Map<String, Object> userSiteAssociationMap,
			String pCurrentSiteId) {
		String opName = "FBProfileToolImpl.getSisterSite()";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
			logDebug(opName + " : Start");

		List<Site> siteAssociation = null;
		if (userSiteAssociationMap != null && !userSiteAssociationMap.isEmpty()) {

			if (pCurrentSiteId != null && mSiteGroup != null) {

				Collection<SiteGroup> siteGroupList = mSiteGroup
						.getSiteGroupsBySite(pCurrentSiteId);

				if (siteGroupList != null && !siteGroupList.isEmpty()) {
					for (SiteGroup group : siteGroupList) {
						siteAssociation = new ArrayList<Site>();
						Collection<Site> sites = group.getSites();
						for (Site sisterSite : sites) {
							if (userSiteAssociationMap.containsKey(sisterSite
									.getId())) {
								siteAssociation.add(sisterSite);
							}

						}
					}
				}
			}
		}

			logDebug(opName + " : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION,
				opName);
		return siteAssociation;
	}
}