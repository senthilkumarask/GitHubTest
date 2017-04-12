/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBGetProfileInfoWebService.java
 *
 *  DESCRIPTION: BBBGetProfileInfoWebService extends GenericService
 *  			 process the request for harts and hanks i.e it 
 *  			 validate the ip and token if pass it create a responsevo 
 *  			 in which user's profile info. If validation fails it will 
 * 				 return appropriate response 
 *  HISTORY:
 *  2/1/2012 Initial version
 *
 */
package com.bbb.account.service.profile;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import atg.core.util.Address;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SiteVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.utils.BBBUtility;

public class BBBGetProfileInfoWebService extends BBBGenericService {
	
	private static final String CONTENT_CATALOG_CONFIG_KEY = "ContentCatalogKeys";
	private static final String BED_BATH_AND_BEYOND_US_SITE_ID = "BedBathUSSiteCode";
	private static final String BED_BATH_AND_BEYOND_CA_SITE_ID = "BedBathCanadaSiteCode";
	private static final String BUY_BUY_BABY_SITE_ID = "BuyBuyBabySiteCode";
	
	private String mQuery;
	private long mTimeOutMinutes;
	private transient BBBCatalogTools catalogTools;
	private transient BBBPropertyManager profilePropertyManager;
	private transient BBBProfileTools mProfileTool;
	
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
	 * @return the timeOutMinutes
	 */
	public long getTimeOutMinutes() {
		return mTimeOutMinutes;
	}

	/**
	 * @param pTimeOutMinutes
	 *            the timeOutMinutes to set
	 */
	public void setTimeOutMinutes(long pTimeOutMinutes) {
		mTimeOutMinutes = pTimeOutMinutes;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return mQuery;
	}

	/**
	 * @param pQuery
	 *            the query to set
	 */
	public void setQuery(String pQuery) {
		mQuery = pQuery;
	}
	

	/**
	 * This method is called by the ATG OOTB web service and process the request
	 * for harts and hanks i.e it validate the ip and token if pass it create a
	 * responsevo in which user's profile info. If validation fails it will
	 * return appropriate response
	 * 
	 * @param request
	 * @return responseVO
	 */
	public ResponseVO getResponseVO(RequestVO request) {
		BBBPerformanceMonitor
				.start("BBBGetProfileInfoWebService-getResponseVO");
			logDebug("BBBGetProfileInfoWebService.getResponseVO() method start");
		MutableRepository repo = getProfileTool().getProfileRepository();
		
		ResponseVO responseVO = new ResponseVO();
		StringBuffer message = new StringBuffer("");
		validateInput(request, message);
		if(message.toString().equalsIgnoreCase(BBBCoreConstants.BLANK)){
			try {
				RepositoryView view = repo.getView(BBBCoreConstants.USER);
				RqlStatement statement = RqlStatement.parseRqlStatement(mQuery);
				Object params[] = new Object[BBBCoreConstants.THREE];
				params[BBBCoreConstants.ZERO] = request.getToken();
				params[BBBCoreConstants.ONE] = request.getSiteId();
				//params[BBBCoreConstants.TWO] = request.getIpAddress();
				RepositoryItem[] profileItem = statement.executeQuery(view, params);
				
				if (profileItem != null && profileItem.length > 0) {
					
					RepositoryItem bbbProfile = profileItem[0];
					Map map = (Map) bbbProfile.getPropertyValue(getProfilePropertyManager().getUserSiteItemsPropertyName());
					
					RepositoryItem site = (MutableRepositoryItem) map.get(request.getSiteId());
					Date storedDate = (Date) site.getPropertyValue(BBBCoreConstants.TIME_STAMP);
					if (storedDate != null) {
						Calendar currentDate = Calendar.getInstance();
						Date date = currentDate.getTime();
						long diff = date.getTime() - storedDate.getTime();
						if ((diff / (BBBCoreConstants.SIXTY * BBBCoreConstants.THOUSAND)) <= mTimeOutMinutes) {

							ProfileBasicVO profileVO = new ProfileBasicVO();
							profileVO.setProfileId(bbbProfile.getRepositoryId());
							profileVO.setFirstName((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getFirstNamePropertyName()));
							profileVO.setLastName((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLastNamePropertyName()));
							Object obj = site.getPropertyValue(getProfilePropertyManager().getEmailOptInPropertyName());
							int emailOptIn = obj == null ? 0 : (Integer)obj;
							if(emailOptIn == 1){
								profileVO.setEmailOptIn(true);
							}
							
							RepositoryItem defaultShippingAddress = getProfileTool().getDefaultShippingAddress(bbbProfile);
							String country = BBBCoreConstants.BLANK;
							Address address = null;
							if (null != defaultShippingAddress) {
								
								try {
									address = getProfileTool().getAddressFromRepositoryItem(defaultShippingAddress);
									SiteVO siteDetail = getCatalogTools().getSiteDetailFromSiteId(request.getSiteId());
									if(null != siteDetail) {
										address.setCountry(siteDetail.getCountryCode() == null ? country : siteDetail.getCountryCode());
									}
								} catch (RepositoryException e) {
									logError("Error while fetching shipping address details", e);
									responseVO.setMessage("System Exception occured while fetching address details");
								}catch (BBBBusinessException e) {
									logError("Error while fetching shipping address details", e);
									responseVO.setMessage("System Exception occured while fetching address details");
								} catch (BBBSystemException e) {
									logError("Error while fetching shipping address details", e);
									responseVO.setMessage("System Exception occured while fetching address details");
								}
								
								profileVO.setAddressLine1(address.getAddress1());
								profileVO.setAddressLine2(address.getAddress2());
								profileVO.setCity(address.getCity());
								profileVO.setState(address.getState());
								profileVO.setZipcode(address.getPostalCode());
								profileVO.setCountry(address.getCountry());
								
							}
							
							profileVO.setEmail((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getLoginPropertyName()));
							profileVO.setMobile((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getMobileNumberPropertyName()));
							profileVO.setPhoneNum((String)bbbProfile.getPropertyValue(getProfilePropertyManager().getPhoneNumberPropertyName()));
							
							responseVO.setAuthorzied(true);
							responseVO.setResponseType(BBBCoreConstants.ZERO);
							responseVO.setProfileBasicVO(profileVO);
							
						} else {
							responseVO.setAuthorzied(false);
							responseVO.setResponseType(BBBCoreConstants.TWO);
							responseVO.setMessage(BBBCoreConstants.TOKEN_TIMEOUT);
						}
						
					}else{
						responseVO.setAuthorzied(false);
						responseVO.setResponseType(BBBCoreConstants.ONE);
						responseVO.setMessage(BBBCoreConstants.INVALID_TOKEN);
					}
					
				} else {
					responseVO.setAuthorzied(false);
					responseVO.setResponseType(BBBCoreConstants.ONE);
					responseVO.setMessage("Profile not found");
				}
				
			} catch (RepositoryException e) {
				logError("RepositoryException from getResponseVO method of BBBGetProfileInfoWebService", e);
				responseVO.setAuthorzied(false);
				responseVO.setResponseType(BBBCoreConstants.ONE);
				responseVO.setMessage("System Exception occured while fetching profile");
			}

		} else {
			responseVO.setAuthorzied(false);
			responseVO.setResponseType(BBBCoreConstants.ONE);
			responseVO.setMessage(message.toString());
		}
		
		logDebug("BBBGetProfileInfoWebService.getResponseVO() method ends");

		BBBPerformanceMonitor.end("BBBGetProfileInfoWebService-getResponseVO");
		return responseVO;
		
	}
	
	private void validateInput(RequestVO request, StringBuffer message){
		
//		if(BBBUtility.isEmpty(request.getIpAddress())){
//			message.append("Empty IP Address,");
//		}
		if(BBBUtility.isEmpty(request.getToken())){
			message.append("Empty Token,");		
		}if(BBBUtility.isEmpty(request.getSiteId())){
			message.append("Empty Site Id,");
		}else if(!isValidSiteId(request.getSiteId())){
			message.append("Invalid Site Id");
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

}
