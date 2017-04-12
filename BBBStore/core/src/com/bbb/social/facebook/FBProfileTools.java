/**
 * 
 */
package com.bbb.social.facebook;

import java.util.List;
import java.util.Map;

import atg.multisite.Site;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

import com.bbb.exception.BBBSystemException;
import com.bbb.social.facebook.vo.UserVO;

/**
 * @author alakra
 * 
 */
public interface FBProfileTools {
	
	public PropertyManager getPropertyManager();
	
	public void setPropertyManager(PropertyManager pPropertyManager);
	
	public MutableRepository getProfileRepository();

	public void setProfileRepository(MutableRepository pProfileRepository);
	
	/**
	 * fetches the Facebook profile on the basis of Facebook account id provided
	 * @param pFBAccountId Facebook account id whose profile needs to be fetched
	 * @return facebook profile
	 * @throws BBBSystemException throws exception in case of any unexpected error
	 */
	public RepositoryItem getFacebookUserProfile(String pFBAccountId) throws BBBSystemException;
	
	/**
	 * This method checks whether the user profile's exists in the same site or same site group or other
	 * site group
	 * 
	 * @param userSiteAssociationMap user associated site map
	 * @param pCurrentSiteId current site
	 * @return String status of site existance
	 */
	public String findUserSiteAssociation(RepositoryItem bbbUser, String pCurrentSiteId);
	
	/**
	 * this method create Facebook user profile and associate it with corresponding BBB profile
	 * 
	 * @param bbbUserProfile BBB user profile to associate with Facebook
	 * @param pUser	Facebook user details to be stored
	 * @param pCurrentSiteId site for which Facebook profile to be associated
	 * @return newly created facebook profile
	 * @throws BBBSystemException throws exception in case of any unexpected error
	 */
	public RepositoryItem createUser(RepositoryItem bbbUserProfile, UserVO pUser) throws BBBSystemException;
	
	/**
	 * this method update Facebook user profile and its association with BBB profile
	 * 
	 * @param bbbUserProfile BBB user profile to associate with Facebook
	 * @param pUser	Facebook user details to be updated
	 * @param pCurrentSiteId site for which Facebook profile needs to be associated
	 * @param mutableFbUserProfile facebook user to be updated
	 * @return updated facebook profile
	 * @throws BBBSystemException throws exception in case of any unexpected error
	 */
	public RepositoryItem updateUser(MutableRepositoryItem mutableFbUserProfile, RepositoryItem bbbUserProfile, UserVO pUser) throws BBBSystemException;
	
	/**
	 * this method remove Facebook user association with corresponding BBB profile
	 * 
	 * @param bbbUserProfile BBB user profile to be unlinked
	 * @param mutableFbUserProfile	Facebook user details to be removed
	 * @param pCurrentSiteId site for which Facebook profile association needs to be removed
	 * @return status
	 * @throws BBBSystemException throws exception in case of any unexpected error
	 */
	public boolean removeUser(MutableRepositoryItem mutableFbUserProfile, RepositoryItem bbbUserProfile) throws BBBSystemException;
	
	/**
	 * @param bbbUserProfile BBB user profile to be unlinked
	 * @param pCurrentSiteId site for which Facebook association needs to be removed
	 * @return status
	 * @throws BBBSystemException throws exception in case of any unexpected error
	 */
	public boolean unlinkBBBProfileWithFBProfile(RepositoryItem bbbUserProfile) throws BBBSystemException;
	
	/**
	 * @param bbbUserProfile BBB profile to be linked with Facebook
	 * @param fbUserProfile Facebook profile to be linked
	 * @param pCurrentSiteId site for which Facebook and BBB profile needs to be linked
	 * @return newly created/updated Facebook profile 
	 * @throws BBBSystemException throws exception in case of any unexpected error
	 */
	public RepositoryItem linkBBBProfileWithFBProfile(RepositoryItem bbbUserProfile, UserVO fbUserProfile) throws BBBSystemException;
	
	/**
	 * method to fetch BBB profile emial address through FB profile.
	 * @param pFbAccountId Facebook account Id
	 * @param pCurrentSiteId current site id
	 * @return BBB profile email address
	 */
	public String getBBBEmailForFBProfile(String pFbAccountId) throws BBBSystemException;
	
	/**
	 * method to fetch BBB profile email address through FB profile based on sister site profile.
	 * @param pFbAccountId Facebook account Id
	 * @param pCurrentSiteId current site id
	 * @return BBB profile email address
	 */
	public String getSisterSiteBBBEmailForFBProfile(String pFbAccountId) throws BBBSystemException ;
	
	/**
	 * method to fetch the Sister site details for any BBBprofile
	 * @param pFbAccountId Facebook account Id
	 * @param pCurrentSiteId current site id
	 * @return BBB profile email address
	 */
	public List<Site> getSisterSite(Map<String, Object> userSiteAssociationMap, String pCurrentSiteId) ;
}