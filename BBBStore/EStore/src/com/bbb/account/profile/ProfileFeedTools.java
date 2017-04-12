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
package com.bbb.account.profile;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.account.BBBProfileTools;
import com.bbb.account.profile.vo.ProfileVO;
import com.bbb.account.profile.vo.RegistryVO;
import com.bbb.account.webservices.BBBProfileServices;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryTypes;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.utils.BBBUtility;

/**
 * @author hbandl
 *
 */
public class ProfileFeedTools extends BBBGenericService {
	
	/**
	 * default date format
	 */
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm";
	
	/**
	 * default password
	 */
	private static final String DEFAULT_PASSWORD = "Default1";
	
	/**
	 * feed file location
	 */
	private String mFileLocation;
	
	/**
	 * bbbProfileTools
	 */
	private BBBProfileTools mProfileTools;
	
	/**
	 * transection manager
	 */
	private TransactionManager mTransactionManager;
	
	/**
	 * BBB profile property manager 
	 */
	private BBBPropertyManager mBBBProfilePropertyManager;
	
	/**
	 * BBBProfileService object
	 */
	private BBBProfileServices mBbbProfileService;
	
	/**
	 * GiftRegistry Manager object
	 */
	private GiftRegistryManager mGiftRegistryManager;

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
	 * @return the mBbbProfileService
	 */
	public BBBProfileServices getBbbProfileService() {
		return mBbbProfileService;
	}

	/**
	 * @param pBbbProfileService the pBbbProfileService to set
	 */
	public void setBbbProfileService(BBBProfileServices pBbbProfileService) {
		this.mBbbProfileService = pBbbProfileService;
	}

	/**
	 * Sets property TransactionManager
	 * 
	 * @param pTransactionManager
	 *            a <code>TransactionManager</code> value
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * Returns property TransactionManager
	 * 
	 * @return a <code>TransactionManager</code> value
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
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
	 * @return the mfileLocation
	 */
	public String getFileLocation() {
		return mFileLocation;
	}

	/**
	 * @param pFileLocation the file Location to set
	 */
	public void setFileLocation(String pFileLocation) throws BBBSystemException {
		this.mFileLocation = pFileLocation;
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
	 * method to save profile details 
	 * @param pProfileVOs profile details fetched from the feed
	 * @return
	 */
	public Map<String, ProfileVO> saveProfiles(Map<String, ProfileVO> pProfileVOs) {
		logDebug("ProfileFeedToolsImpl.saveProfiles : Start");
		
		Map<String, ProfileVO> profileFeedResponse  = new HashMap<String, ProfileVO>();
		if(pProfileVOs != null && !pProfileVOs.isEmpty()){
			for (Entry<String, ProfileVO> entry: pProfileVOs.entrySet()) {
				String profileMapkey = entry.getKey();
				logError("Start Migrating User :" + profileMapkey);
				ProfileVO profileVO = pProfileVOs.get(profileMapkey);
				Transaction transaction = null;
				boolean isException= false;
				validateProfileDetails(profileVO);
				
				if(!profileVO.isError()){
					
					RepositoryItem bbbUserProfile = getProfileTools().getItemFromEmail(profileVO.getEmail().toLowerCase());
					if(bbbUserProfile != null){
						//update existing ATG profile if required
						boolean diffSiteGroupUser = getProfileTools().isUserPresentToOtherGroup(bbbUserProfile, profileVO.getSiteId());
						profileVO.setProfileId(bbbUserProfile.getRepositoryId());
						
						if(diffSiteGroupUser){
							logDebug("User already exist for different site group");
							profileVO.setError(true);
							profileVO.setErrorCode("error_profile_already_exist_diff_group");
							profileVO.setResponseMessage("User already exist for different site group");
						}else{
							
							boolean userAlreadyExistSameSite = getProfileTools().isDuplicateEmailAddress(profileVO.getEmail(), profileVO.getSiteId());
							
							if(userAlreadyExistSameSite){
								//update user details
								try{	
									
									Date lastActivityTime = typecastAndformatDate((Timestamp)bbbUserProfile.getPropertyValue(getBbbProfilePropertyManager().getLastActivityTime()));
									if(lastActivityTime.before(profileVO.getLastModifiedDate())){
										transaction = ensureTransaction();	
										try {
											MutableRepositoryItem mutableBBBProfile = getProfileTools().getProfileRepository().getItemForUpdate(bbbUserProfile.getRepositoryId(), 
																							bbbUserProfile.getItemDescriptor().getItemDescriptorName());
											
											copyMigratedProfileDetailToUpdateProfile(mutableBBBProfile, profileVO);
											if(!profileVO.isError()){
												getProfileTools().getProfileRepository().updateItem(mutableBBBProfile);
												ItemDescriptorImpl profileItemDescriptor = (ItemDescriptorImpl) bbbUserProfile.getItemDescriptor();
												profileItemDescriptor.removeItemFromCache(bbbUserProfile.getRepositoryId());
												Map<String, String> propertyMap = new HashMap<String, String>();
												propertyMap.put(getBbbProfilePropertyManager().getFavouriteStoreIdPropertyName(), profileVO.getFavStoreId());
												propertyMap.put(getBbbProfilePropertyManager().getEmailOptInPropertyName(), profileVO.getOptInFlag());
												propertyMap.put(getBbbProfilePropertyManager().getMemberIdPropertyName(), profileVO.getMemberId());
												getProfileTools().updateSiteItem(mutableBBBProfile, profileVO.getSiteId(), propertyMap);
												
												profileVO.setResponseMessage("Profile Successfully Updated");
											}if(profileVO.isError()){
												isException = true;
											}
											
										}catch (RepositoryException e) {
											logDebug("Error occured file fetching profile from repository for :" + profileVO.getEmail(),e);
											isException = true;
											profileVO.setError(true);
											profileVO.setErrorCode("error_update_profile_same_site");
											profileVO.setResponseMessage("Error occured while updating same site profile details");
										}finally{
											endTransaction(isException, transaction);
										}	
										
									}else {
										// set error response for found more resent profile in ATG
										logDebug("Found more recent profile in ATG for email id:" + profileVO.getEmail());
										profileVO.setResponseMessage("Found more recent profile in ATG for email id:" + profileVO.getEmail());
									}
								}catch(ParseException e){
									logDebug("Error occured file parsing profile's last activity date for :" + profileVO.getEmail());
									profileVO.setError(true);
									profileVO.setErrorCode("error_update_profile_same_site");
									profileVO.setResponseMessage("Error occured while updating same site profile details");
								}	
							}else{
								//update profile
								transaction = ensureTransaction();	
								try {
									Date lastActivityTime = typecastAndformatDate((Timestamp)bbbUserProfile.getPropertyValue(getBbbProfilePropertyManager().getLastActivityTime()));
									if(lastActivityTime.before(profileVO.getLastModifiedDate())){
										MutableRepositoryItem mutableBBBProfile = getProfileTools().getProfileRepository().getItemForUpdate(bbbUserProfile.getRepositoryId(), 
												bbbUserProfile.getItemDescriptor().getItemDescriptorName());
	
										copyMigratedProfileDetailToUpdateProfile(mutableBBBProfile, profileVO);
										if(!profileVO.isError()){
											getProfileTools().getProfileRepository().updateItem(mutableBBBProfile);
											ItemDescriptorImpl profileItemDescriptor = (ItemDescriptorImpl) bbbUserProfile.getItemDescriptor();
											profileItemDescriptor.removeItemFromCache(bbbUserProfile.getRepositoryId());
										}
										
									}if(!profileVO.isError()){
										getProfileTools().createSiteItem(profileVO.getEmail().toLowerCase(), profileVO.getSiteId(), 
																				profileVO.getMemberId(), profileVO.getFavStoreId(), profileVO.getOptInFlag());
										
										profileVO.setResponseMessage("Profile Successfully Extended");
									}if(profileVO.isError()){
										isException = true;
									}
									
								}catch(ParseException e){
									logError("Error occured file parsing profile's last activity date for :" + profileVO.getEmail());
									isException = true;
									profileVO.setError(true);
									profileVO.setErrorCode("error_update_profile_sister_site");
									profileVO.setResponseMessage("Error occured while updating sister site profile details");
								}catch (RepositoryException e) {
									logError("Error occured while updating sister site profile details for :" + profileVO.getEmail(),e);
									isException = true;
									profileVO.setError(true);
									profileVO.setErrorCode("error_update_profile_sister_site");
									profileVO.setResponseMessage("Error occured while updating sister site profile details");
									
								}catch (BBBSystemException e) {
									logError("Error occured while updating sister site profile details for:" + profileVO.getEmail(),e);
									isException = true;
									profileVO.setError(true);
									profileVO.setErrorCode("error_update_profile_sister_site");
									profileVO.setResponseMessage("Error occured while updating sister site profile details");
									
								}finally {
									endTransaction(isException,transaction);			
								}
							}
						}
					}else{
						createUser(profileVO);
						profileVO.setResponseMessage("Profile Successfully Created");
					}
				}
				
				if(!profileVO.isError()){
					profileFeedResponse.put(profileVO.getEmail(), profileVO);
				}
				
				logError("End Migrating User :" + profileVO.getEmail());
			}
		}
		logDebug("ProfileFeedToolsImpl.saveProfiles : End");
		return profileFeedResponse;
	}

	/**
	 * method to save registries corresponding to a profile
	 *  
	 * @param pRegistriesVOs list of registries to be created
	 * @param profileResponseVOs list of responses
	 */
	public void saveProfileRegistries(Map<String, RegistryVO> pRegistriesVOs) {
		logDebug("ProfileFeedToolsImpl.saveProfileRegistries : Start");
		
		if(pRegistriesVOs != null && !pRegistriesVOs.isEmpty()){
			for (Entry<String, RegistryVO> entry : pRegistriesVOs.entrySet()) {
				String registryId = entry.getKey();
				//changes done to remove dependency between profile and registry feeds to fix JVM issues.
				RegistryVO registry = pRegistriesVOs.get(registryId);
				validateRegistryDetails(registry);
				RepositoryItem bbbUserProfile = getProfileTools().getItemFromEmail(registry.getEmail().toLowerCase());
				boolean userAlreadyExistSameSite = getProfileTools().isDuplicateEmailAddress(registry.getEmail(), registry.getSiteId());
				if(bbbUserProfile != null && !registry.isError() && userAlreadyExistSameSite){
					registry.setProfileId(bbbUserProfile.getRepositoryId());
					
						if(registry.isRegistrant()){
							com.bbb.commerce.giftregistry.vo.RegistryVO giftRegistryVO = new com.bbb.commerce.giftregistry.vo.RegistryVO();
							RegistryTypes registryType = new RegistryTypes();
							EventVO event = new EventVO();
							RegistrantVO registrantVO = new RegistrantVO();
							registrantVO.setEmail(registry.getEmail());
							
							giftRegistryVO.setRegistryId(registry.getRegistryId());
							giftRegistryVO.setSiteId(registry.getSiteId());
							registryType.setRegistryTypeName(registry.getRegistryTypeCode());
							giftRegistryVO.setRegistryType(registryType);
							event.setEventDate(registry.getEventDate());
							giftRegistryVO.setEvent(event);
							giftRegistryVO.setRegistrantVO(registrantVO);
							
							try {
								getGiftRegistryManager().linkMigratedRegistry(giftRegistryVO, true);
								registry.setResponseMessage("Registry successfully created/updated");
								logError("Registry successfully created/updated for registry id :" + registry.getId());
							} catch (BBBSystemException e) {
								logError("Error occured while creating registry for email id : " + registry.getEmail() + " and id :" + registry.getId(), e);
								registry.setError(true);
								registry.setErrorCode("error_create_registry");
								registry.setResponseMessage("Error occured while creating/updating registry");
								
							} catch (RepositoryException e) {
								logError("Error occured while creating registry for email id : " + registry.getEmail() + " and id :" + registry.getId(), e);
								registry.setError(true);
								registry.setErrorCode("error_create_registry");
								registry.setResponseMessage("Error occured while creating/updating registry");
							}
								
						}else{
							logError("Not a Owner - email id:" + registry.getEmail() + " and id :" + registry.getId());
							registry.setError(true);
							registry.setErrorCode("error_not_registry_owner");
							registry.setResponseMessage("Ignoring since not a registry owner");
						}
					
				}else {
					
					if(bbbUserProfile == null || !userAlreadyExistSameSite){
						logError("Ignoring registry for email id : " + registry.getEmail() + " and id :" + registry.getId());
						registry.setError(true);
						registry.setErrorCode("error_profile_not_found");
						registry.setResponseMessage("Profile not found hence ignoring registry");
					}
					
				}
				
			}
		}
		logDebug("ProfileFeedToolsImpl.saveProfileRegistries : End");
	}
	
	/**
	 * method to link a profile as co-registrant
	 *  
	 * @param pRegistriesVOs list of registries to be created
	 * @param profileResponseVOs list of responses
	 */
	public void linkCoRegistrant(Map<String, RegistryVO> pRegistriesVOs) {
		logDebug("ProfileFeedToolsImpl.linkCoRegistrant : Start");
		if(pRegistriesVOs != null && !pRegistriesVOs.isEmpty()){
			for (Entry<String, RegistryVO> entry : pRegistriesVOs.entrySet()) {
				String registryId = entry.getKey();
				logDebug("ProfileFeedToolsImpl.linkCoRegistrant : Started for registryId="+registryId);
				RegistryVO registry = pRegistriesVOs.get(registryId);
				validateRegistryDetails(registry);
				
				if(!registry.isError()){
					if(!registry.isRegistrant()){
						
						boolean userAlreadyExistSameSite = getProfileTools().isDuplicateEmailAddress(registry.getEmail(), registry.getSiteId());
						if(userAlreadyExistSameSite){
							RepositoryItem bbbCoRegistrantProfile = (MutableRepositoryItem) getProfileTools().getItemFromEmail(registry.getEmail());
							registry.setProfileId(bbbCoRegistrantProfile.getRepositoryId());
							
							com.bbb.commerce.giftregistry.vo.RegistryVO giftRegistryVO = new com.bbb.commerce.giftregistry.vo.RegistryVO();
							RegistryTypes registryType = new RegistryTypes();
							EventVO event = new EventVO();
							RegistrantVO registrantVO = new RegistrantVO();
							registrantVO.setEmail(registry.getEmail());
							
							giftRegistryVO.setRegistryId(registry.getRegistryId());
							giftRegistryVO.setSiteId(registry.getSiteId());
							registryType.setRegistryTypeName(registry.getRegistryTypeCode());
							giftRegistryVO.setRegistryType(registryType);
							event.setEventDate(registry.getEventDate());
							giftRegistryVO.setEvent(event);
							giftRegistryVO.setRegistrantVO(registrantVO);
							
							try {
								getGiftRegistryManager().linkMigratedRegistry(giftRegistryVO, false);
								registry.setResponseMessage("Co-Registrant successfully linked");
								logError("Co-Registrant successfully linked");
							} catch (BBBSystemException e) {
								logError("Error occured while doing co-registrant linking for email id : " + registry.getEmail() + " and id :" + registry.getId(), e);
								registry.setError(true);
								registry.setErrorCode("error_link_coregistrant");
								registry.setResponseMessage("Error occured while linking co-registrant");
								
							} catch (RepositoryException e) {
								logError("Error occured while creating registry for email id : " + registry.getEmail() + " and id :" + registry.getId(), e);
								registry.setError(true);
								registry.setErrorCode("error_link_coregistrant");
								registry.setResponseMessage("Error occured while linking co-registrant");
							}
							
						}else{
							logError("User corresponding to email id : " + registry.getEmail() + " and id :" + registry.getId() + "not belong to this site");
							registry.setError(true);
							registry.setErrorCode("error_profile_not_found_same_site");
							registry.setResponseMessage("Profile not found on same site");
						}
						
					}else{
						logError("Not a co-registrant - email id:" + registry.getEmail() + " and id :" + registry.getId());
						registry.setError(true);
						registry.setErrorCode("error_not_coregistrant");
						registry.setResponseMessage("Ignoring since not a co-registrant");
					}
				}
			}
		}
		logDebug("ProfileFeedToolsImpl.saveProfileRegistries : End");
	}

	/**
	   * This method ensures that a transaction exists before returning.
	   * If there is no transaction, a new one is started and returned.  In
	   * this case, you must call commitTransaction when the transaction
	   * completes.
	   * @return a <code>Transaction</code> value
	   */
	  protected Transaction ensureTransaction(){
		  try {
			  TransactionManager transactionManager = getTransactionManager();	      
			  Transaction transaction = transactionManager.getTransaction();
			  if (transaction == null) {
				  transactionManager.begin();
				  transaction = transactionManager.getTransaction();
				  return transaction;
			  }
			  return null;
		  }catch (NotSupportedException e) {
			  logError(e);
		  }catch (SystemException e) {
			  logError(e);
		  }
		  return null;
	  }
	
	 /**
	  * method to check if error occur during the transaction if yes then execute 
	  * transection rollback otherwise commit the transaction
	  * 
	  * @param isError flag that represent error
	  * @param pTransaction transaction object
	 */
	private void endTransaction(boolean isError,Transaction pTransaction){
		  try {
			  if(isError){
				  if(pTransaction!=null){
					  pTransaction.rollback();
				  }
			  }else{
				  if(pTransaction!=null){				
					  pTransaction.commit();					
				  }
			  }
		  }catch (SecurityException e) {
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
	 * copy the migrated profile detail into the ATG profile
	 * 
	 * @param bbbProfile profile that need to be created/updated with migrated user details
	 * @param migratedProfile migrated profile details
	 *  
	 */
	private void copyMigratedProfileDetailToUpdateProfile(MutableRepositoryItem bbbProfile, ProfileVO migratedProfile){
		logDebug("ProfileFeedToolsImpl.copyMigratedProfileDetailToUpdateProfile : Starts");
		
		bbbProfile.setPropertyValue(getBbbProfilePropertyManager().getLoginPropertyName(), migratedProfile.getEmail().toLowerCase());
		bbbProfile.setPropertyValue(getBbbProfilePropertyManager().getEmailAddressPropertyName(), migratedProfile.getEmail().toLowerCase());
		bbbProfile.setPropertyValue(getBbbProfilePropertyManager().getFirstNamePropertyName(), migratedProfile.getFirstName());
		bbbProfile.setPropertyValue(getBbbProfilePropertyManager().getLastNamePropertyName(), migratedProfile.getLastName());
		bbbProfile.setPropertyValue(getBbbProfilePropertyManager().getMobileNumberPropertyName(), migratedProfile.getMobileNumber());
		bbbProfile.setPropertyValue(getBbbProfilePropertyManager().getPhoneNumberPropertyName(), migratedProfile.getPhoneNumber());
		bbbProfile.setPropertyValue(getBbbProfilePropertyManager().getReceiveEmailPropertyName(), BBBCoreConstants.YES);
		
		logDebug("ProfileFeedToolsImpl.copyMigratedProfileDetailToUpdateProfile : Ends");
	}
	
	/**
	 * method to create user into ATG schema for migrated profile 
	 * @param profileVO migrated profile detail VO
	 * @return profile creation response
	 * 
	 */
	private void createUser(ProfileVO profileVO) {

		logDebug("ProfileFeedToolsImpl.createUser : Starts");
		Transaction transaction = null;
		boolean isException= false;
		RepositoryItem savedProfile = null;
		transaction = ensureTransaction();	
		
		try {
			
			MutableRepositoryItem profileObj = getProfileTools().getProfileRepository().createItem(getBbbProfilePropertyManager().getProfileItemDiscriptorName());
			
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getLoginPropertyName(), profileVO.getEmail().toLowerCase());
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getEmailAddressPropertyName(), profileVO.getEmail().toLowerCase());
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getFirstNamePropertyName(), profileVO.getFirstName());
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getLastNamePropertyName(), profileVO.getLastName());
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getMobileNumberPropertyName(), profileVO.getMobileNumber());
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getPhoneNumberPropertyName(), profileVO.getPhoneNumber());
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getReceiveEmailPropertyName(), BBBCoreConstants.YES);
			
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getLoggedIn(), false);
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getMigratedAccount(), true);
			
			String encrypted = mProfileTools.getPropertyManager().generatePassword(profileVO.getEmail().toLowerCase(), DEFAULT_PASSWORD);
			profileObj.setPropertyValue(getBbbProfilePropertyManager().getPasswordPropertyName(), encrypted);
			
			savedProfile =  getProfileTools().getProfileRepository().addItem(profileObj);
			
			getProfileTools().createSiteItem(profileVO.getEmail(), profileVO.getSiteId(), profileVO.getMemberId(), profileVO.getFavStoreId(), profileVO.getOptInFlag(), savedProfile);
			profileVO.setProfileId(savedProfile.getRepositoryId());
			
		} catch (RepositoryException e) {
			logError("Error occured while creating user", e);
			isException = true;
			profileVO.setError(true);
			profileVO.setErrorCode("error_create_profile");
			profileVO.setResponseMessage("Error occured while creating user");
		}catch (BBBSystemException e) {
			logError("Error occured while creating site association" + e);
			isException = true;
			profileVO.setError(true);
			profileVO.setErrorCode("error_create_profile");
			profileVO.setResponseMessage("Error occured while creating user site association");
		}
		
		finally{
			endTransaction(isException, transaction);
		}
		
		logDebug("ProfileFeedToolsImpl.createUser : End");
			
	}
	
	/**
	 * method to check validation error on feed data
	 * @param profileVO migrated profile details
	 * @param profileResponseVO validation response details
	 */
	private void validateProfileDetails(ProfileVO profileVO) {
		
		logDebug("ProfileFeedToolsImpl.validateProfileDetails : Start");
		StringBuilder errorMessage = new StringBuilder("");
		
		// data validation
		if(BBBUtility.isEmpty(profileVO.getId())) {
			errorMessage.append("Invalid Id,");
		}if(BBBUtility.isEmpty(profileVO.getEmail()) || !BBBUtility.isValidEmail(profileVO.getEmail())) {
			errorMessage.append("Invalid email Id,");
		}if(BBBUtility.isEmpty(profileVO.getSiteId())) {
			errorMessage.append("Invalid site Id,");
		}if(BBBUtility.isEmpty(profileVO.getFirstName())) {
			errorMessage.append("Invalid First Name,");
		}if(BBBUtility.isEmpty(profileVO.getLastName())) {
			errorMessage.append("Invalid last Name,");
		}if(BBBUtility.isEmpty(profileVO.getOptInFlag())) {
			errorMessage.append("Invalid OptIn flag,");
		}else {
			if(profileVO.getOptInFlag().equalsIgnoreCase("TRUE") || profileVO.getOptInFlag().equalsIgnoreCase("Y")){
				profileVO.setOptInFlag(BBBCoreConstants.YES);
			}else{
				profileVO.setOptInFlag(BBBCoreConstants.NO);
			}
		}if(BBBUtility.isEmpty(profileVO.getLastModifiedDateAsString())) {
			errorMessage.append("Invalid Last access date,");
		}if(!BBBUtility.isEmpty(profileVO.getLastModifiedDateAsString())) {
			try {
				profileVO.setLastModifiedDate(formatDate(profileVO.getLastModifiedDateAsString()));
			} catch (ParseException e) {
				profileVO.setError(true);
				profileVO.setErrorCode("error_data_validation_create_profile");
				errorMessage.append("Invalid Date format");
				profileVO.setResponseMessage(errorMessage.toString());
			}
		}if(!BBBUtility.isEmpty(errorMessage.toString())){
			profileVO.setError(true);
			profileVO.setErrorCode("error_data_validation_create_profile");
			profileVO.setResponseMessage(errorMessage.toString());
		}
		
		logDebug("ProfileFeedToolsImpl.validateProfileDetails : End");
	}
	
	/**
	 * method to check validation error on feed data
	 * @param profileVO migrated profile details
	 * @param profileResponseVO validation response details
	 */
	private void validateRegistryDetails(RegistryVO registryVO) {
		
		logDebug("ProfileFeedToolsImpl.validateRegistryDetails : Start");
		StringBuilder errorMessage = new StringBuilder("");
		
		// data validation
		if(BBBUtility.isEmpty(registryVO.getId())) {
			errorMessage.append("Invalid Id,");
		}if(BBBUtility.isEmpty(registryVO.getRegistryId())) {
			errorMessage.append("Invalid registry Id,");
		}if(BBBUtility.isEmpty(registryVO.getEmail()) || !BBBUtility.isValidEmail(registryVO.getEmail())) {
			errorMessage.append("Invalid email Id,");
		}if(BBBUtility.isEmpty(registryVO.getSiteId())) {
			errorMessage.append("Invalid site Id,");
		}if(BBBUtility.isEmpty(registryVO.getRegistryType())) {
			errorMessage.append("Invalid Event type,");
		}else{
			List<RegistryTypeVO> registryTypeList;
			boolean findRegistryType = false;
			try {
				registryTypeList = getGiftRegistryManager().fetchRegistryTypes(registryVO.getSiteId());
				if(registryTypeList != null && !registryTypeList.isEmpty()){
					for(RegistryTypeVO type : registryTypeList){
						if(type.getRegistryName().equalsIgnoreCase(registryVO.getRegistryType())){
							registryVO.setRegistryTypeCode(type.getRegistryCode());
							findRegistryType = true;
						}
					}
				}
			} catch (RepositoryException | BBBSystemException | BBBBusinessException e) {
				errorMessage.append("Invalid Event type,");
				logError("Invalid Event Type exception is thrown", e);
			}
			if(!findRegistryType){
				errorMessage.append("Invalid Event type,");
			}
		}
		
		if(BBBUtility.isEmpty(registryVO.getEventDate())) {
			errorMessage.append("Invalid Event Date,");
		}if(BBBUtility.isEmpty(registryVO.getRegistrantAsString())) {
			errorMessage.append("Invalid is_registrant flag");
		}if(!BBBUtility.isEmpty(registryVO.getRegistrantAsString())) {
			if(registryVO.getRegistrantAsString().equalsIgnoreCase("true")){
				registryVO.setRegistrant(true);
			}else{
				registryVO.setRegistrant(false);
			}
		}if(!BBBUtility.isEmpty(errorMessage.toString())){
			registryVO.setError(true);
			registryVO.setErrorCode("error_data_validation_create_registry");
			registryVO.setResponseMessage(errorMessage.toString());
		}
		
		logDebug("ProfileFeedToolsImpl.validateRegistryDetails : End");
	}
	
	
	/**
	 * method to convert String into Date format 
	 * @param dateAsString
	 * @return
	 * @throws ParseException
	 */
	private Date formatDate(String dateAsString) throws ParseException{

		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		Date date = (Date)formatter.parse(dateAsString);  
		return date;
		
	}
	
	/**
	 * method to convert Timestamp into Date
	 * @param dateAsString
	 * @return
	 * @throws ParseException
	 */
	private Date typecastAndformatDate(Timestamp dateToCast) throws ParseException{
		
		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		if(dateToCast == null){
			dateToCast = new Timestamp(System.currentTimeMillis());
		}
		
		String dateAsString = formatter.format(dateToCast.getTime());
		Date date = (Date)formatter.parse(dateAsString);  
		return date;
		
	}
	
}
