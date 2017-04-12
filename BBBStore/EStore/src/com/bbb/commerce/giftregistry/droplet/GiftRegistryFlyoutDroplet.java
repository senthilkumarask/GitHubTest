//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Vipul Agarwal
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * The Class GiftRegistryFlyoutDroplet.
 */
public class GiftRegistryFlyoutDroplet extends BBBPresentationDroplet {

	private static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";
	
	/** The Gift reg mgr. */
	private GiftRegistryManager mGiftRegMgr;

	/** The Site context. */
	private SiteContext mSiteContext;
	private Profile profile;
	
	/** The gift reg session bean. */
	private GiftRegSessionBean mGiftRegSessionBean;

	/**
	 * Gets the site context.
	 * 
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * Sets the site context.
	 * 
	 * @param pSiteContext
	 *            the siteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		mSiteContext = pSiteContext;
	}

	/**
	 * Gets the gift registry manager.
	 * 
	 * @return mGiftRegMgr
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegMgr;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegMgr
	 *            the new gift registry manager
	 */
	public void setGiftRegistryManager(final GiftRegistryManager pGiftRegMgr) {
		mGiftRegMgr = pGiftRegMgr;
	}

	/**
	 * The method sets the user status based on the user's login status and the
	 * number of registries user has.
	 * 
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException,
			IOException {
		logDebug("GiftRegistryFlyoutDroplet service method - started");
		String methodName = "service";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.GIFT_REG_FLYOUT_WS_CALL, methodName);
		String pSiteId = SiteContextManager.getCurrentSiteId();

		final Profile pProfile = (Profile) request.getObjectParameter(BBBCoreConstants.USER_PROFILE);
		List<String> userRegList = new ArrayList<String>();
		List<String> userActiveRegList = new ArrayList<String>();
		String pRecentRegistryId = BBBCoreConstants.BLANK;
		RegistrySummaryVO pRegSummaryVO = null;
		request.setParameter(BBBCoreConstants.SITE_ID, pSiteId);

		if (pProfile.isTransient()) {
			request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_NOT_LOGGED_IN);
		} else {
			BBBSessionBean sessionBean = (BBBSessionBean) request.resolveName(SESSION_BEAN);
			final HashMap sessionMap = sessionBean.getValues();
			pRegSummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
			userRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
			userActiveRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST);
			String acceptableStatuses = getGiftRegistryManager().getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
			List<String> acceptableStatusesList = new ArrayList<String>();
			if (!BBBUtility.isEmpty(acceptableStatuses)) {
				String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
				acceptableStatusesList.addAll(Arrays.asList(statusesArray));
			}

			// Get Registry Data from the Database
			if (BBBUtility.isListEmpty(userActiveRegList)) {
				RepositoryItem[] userRegistriesRepItems;
				try {
					logDebug("GiftRegistryFlyoutDroplet service : Fetch user registries from database");
					userRegistriesRepItems = getGiftRegistryManager().fetchUserRegistries(pSiteId, getProfile().getRepositoryId());
					// Set Active Registry Data
					if (userRegistriesRepItems != null) {
						userRegList = new ArrayList<String>(userRegistriesRepItems.length);
						userActiveRegList = new ArrayList<String>(userRegistriesRepItems.length);
						for (RepositoryItem repositoryItem : userRegistriesRepItems) {
							String registryId = repositoryItem.getRepositoryId();
							String registryStatus = getGiftRegistryManager().getRegistryStatusFromRepo(pSiteId,	registryId);
							if (acceptableStatusesList.contains(registryStatus)) {
								userActiveRegList.add(registryId);
							}
							userRegList.add(registryId);
						}
						if(!BBBUtility.isListEmpty(userActiveRegList)){
							if(userActiveRegList.size() == 1){
								if (pRegSummaryVO == null ) {
									pRecentRegistryId = (String) userActiveRegList.get(0);
									pRegSummaryVO = mGiftRegMgr.getRegistryInfo( pRecentRegistryId, pSiteId);
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}else{
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}
								if (BBBGiftRegistryConstants.GR_CREATE.equals(getGiftRegSessionBean().getRegistryOperation())
									|| BBBGiftRegistryConstants.GR_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
									|| BBBGiftRegistryConstants.GR_ITEM_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
									|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(getGiftRegSessionBean().getRegistryOperation())
									|| BBBGiftRegistryConstants.OWNER_VIEW.equals(getGiftRegSessionBean().getRegistryOperation())) {
								sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
								request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY);
								}else{
									Long diffDays = (long) 0;
									if(pRegSummaryVO != null && pRegSummaryVO.getEventDate()!=null)
									{
										diffDays = getDateDiff(pSiteId, pRegSummaryVO);
									}
									if (diffDays >= -90) {
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY);
									} else {
										if(pRegSummaryVO != null){
											sessionBean.setRegDiffDateLess(true);
										}
										request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null);
										request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
									}
								}
							}
							if(userActiveRegList.size() > 1){
								if (pRegSummaryVO == null ) {
									pRecentRegistryId = mGiftRegMgr.fetchUsersSoonestOrRecent(userActiveRegList);
									// cases when the user has more than 1 registries and all not having event date. The recent registry id fetch is null.
									// Registry summary vo in that case is not populated.
									// FIxed as part of ILD-20
									if(pRecentRegistryId==null){
										pRecentRegistryId=userActiveRegList.get(0);
									}
									pRegSummaryVO = mGiftRegMgr.getRegistryInfo( pRecentRegistryId, pSiteId);
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}else{
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}
								if (BBBGiftRegistryConstants.GR_CREATE.equals(getGiftRegSessionBean().getRegistryOperation())
										|| BBBGiftRegistryConstants.GR_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
										|| BBBGiftRegistryConstants.GR_ITEM_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
										|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(getGiftRegSessionBean().getRegistryOperation())
										|| BBBGiftRegistryConstants.OWNER_VIEW.equals(getGiftRegSessionBean().getRegistryOperation())) {
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
								}else{
									Long diffDays =(long) 0;
									if(pRegSummaryVO != null && pRegSummaryVO.getEventDate()!=null)
									{
										diffDays = getDateDiff(pSiteId, pRegSummaryVO);
									}
									if (diffDays >= -90) {
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
									} else {
										if(pRegSummaryVO != null){
											sessionBean.setRegDiffDateLess(true);
										}
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
									}
								}
							}
							
							
						}											
					}
					
					sessionBean.getValues().put(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST,userActiveRegList);
					sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST,userRegList);
					logDebug("GiftRegistryFlyoutDroplet service : Set Active User registries in Sessionbean " + userActiveRegList);
					if(BBBUtility.isListEmpty(userActiveRegList)){
						request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
						logDebug("GiftRegistryFlyoutDroplet service : User Status set to : USER_LOGGED_IN_WITH_NO_REGISTRIES");
					}
				}

				catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(request,	"Repository Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1075), e);
					request.setParameter(BBBCoreConstants.USER_STATUS, "8");
					request.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(request,	"System Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1076), e);
					request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(request,	"BBBBusinessException from SERVICE of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_BUSINESS_EXCEPTION);
					request.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
				} catch (Exception ex) {
					logError(LogMessageFormatter.formatMessage(request,	"Other Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1077), ex);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_SYSTEM_EXCEPTION);
					request.setParameter(OUTPUT_ERROR_MSG, ex.getMessage());
				}
			}
			else if (userActiveRegList != null && userActiveRegList.size() == 1) {
				try {
					logDebug("GiftRegistryFlyoutDroplet service : User_Status set to : USER_LOGGED_IN_WITH_SINGLE_REGISTRY");
					if (pRegSummaryVO == null) {
						pRecentRegistryId = (String) userActiveRegList.get(0);
						pRegSummaryVO = mGiftRegMgr.getRegistryInfo( pRecentRegistryId, pSiteId);
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
					}

					if (pRegSummaryVO != null) {
						String registryStatus = getGiftRegistryManager().getRegistryStatusFromRepo(pSiteId,	pRegSummaryVO.getRegistryId());
						if (acceptableStatusesList.contains(registryStatus)) {
							if (BBBGiftRegistryConstants.GR_CREATE.equals(getGiftRegSessionBean().getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_ITEM_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(getGiftRegSessionBean().getRegistryOperation())
								|| BBBGiftRegistryConstants.OWNER_VIEW.equals(getGiftRegSessionBean().getRegistryOperation())) {
							request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
							request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY);
							} else {
							// check if user registry event is past date and more than 90 days.
							Long diffDays =(long) 0;
							if(pRegSummaryVO.getEventDate()!=null)
							{
								diffDays = getDateDiff(pSiteId, pRegSummaryVO);
							}
							if (diffDays >= -90) {
								request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY);
							} else {
								
									sessionBean.setRegDiffDateLess(true);
								
								request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
							}
						}
					}else{
						pRecentRegistryId = mGiftRegMgr.fetchUsersSoonestOrRecent(userActiveRegList);
						logDebug(" GiftRegistryFlyoutDroplet service() MSG= soonestOrRecent pRecentRegistryId =" + pRecentRegistryId);
						pRegSummaryVO = mGiftRegMgr.getRegistryInfo(pRecentRegistryId, pSiteId);
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
						request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
						request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
						}
					} else {
						request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
					}

				} catch (NumberFormatException e) {
					request.setParameter(BBBCoreConstants.USER_STATUS, "7");
					request.setParameter(OUTPUT_ERROR_MSG, e);
					logError("NumberFormatException " + e);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(request,	"BBBBusinessException from service of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_BUSINESS_EXCEPTION);
					request.setParameter(OUTPUT_ERROR_MSG, e);
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(request, "BBBBusinessException from SERVICE of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
					request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
				} catch (Exception ex) {
					logError("BBBSystemException " + ex);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_SYSTEM_EXCEPTION);
					request.setParameter(OUTPUT_ERROR_MSG, ex.getMessage());
				}
			} else if (userActiveRegList != null && userActiveRegList.size() > 1) {
				try {
					logDebug("GiftRegistryFlyoutDroplet service : User_Status set to : USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES");

					if (pRegSummaryVO == null) {
						pRecentRegistryId = mGiftRegMgr.fetchUsersSoonestOrRecent(userActiveRegList);
						// cases when the user has more than 1 registries and all not having event date. The recent registry id fetch is null.
						// Registry summary vo in that case is not populated.
						// FIxed as part of ILD-20
						if(pRecentRegistryId==null){
							pRecentRegistryId=userActiveRegList.get(0);
						}
						logDebug(" GiftRegistryFlyoutDroplet service() MSG= soonestOrRecent pRecentRegistryId =" + pRecentRegistryId);
						pRegSummaryVO = mGiftRegMgr.getRegistryInfo(pRecentRegistryId, pSiteId);
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
						logDebug(" GiftRegistryFlyoutDroplet service() MSG= soonestOrRecent pRegSummaryVO =" + pRegSummaryVO);
					}

					if (pRegSummaryVO != null) {
						String registryStatus = getGiftRegistryManager().getRegistryStatusFromRepo(pSiteId,	pRegSummaryVO.getRegistryId());
						if (acceptableStatusesList.contains(registryStatus)) {
							if (BBBGiftRegistryConstants.GR_CREATE.equals(getGiftRegSessionBean().getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_ITEM_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(getGiftRegSessionBean().getRegistryOperation())
								|| BBBGiftRegistryConstants.OWNER_VIEW.equals(getGiftRegSessionBean().getRegistryOperation())) {
							request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
							request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);

							} else {

							Long diffDays =(long) 0;
							if(pRegSummaryVO.getEventDate()!=null)
							{
									diffDays = getDateDiff(pSiteId, pRegSummaryVO);
							}
							if (diffDays >= -90) {
								request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
							} else {
								pRecentRegistryId = mGiftRegMgr.fetchUsersSoonestRegistry(userActiveRegList, pSiteId);
								if(pRecentRegistryId == null){
									
										sessionBean.setRegDiffDateLess(true);
									
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
								}else{
									logDebug("GiftRegistryFlyoutDroplet service() MSG= soonestOrRecent pRecentRegistryId =" + pRecentRegistryId);
									pRegSummaryVO = mGiftRegMgr.getRegistryInfo(pRecentRegistryId, pSiteId);
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}
							}
						}
						}
					}else {
						request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
					}

				} catch (NumberFormatException e) {
					request.setParameter(BBBCoreConstants.USER_STATUS, "7");
					request.setParameter(OUTPUT_ERROR_MSG, e);
					logError("NumberFormatException " + e);
				} catch (RepositoryException e) {
					request.setParameter(BBBCoreConstants.USER_STATUS, "8");
					request.setParameter(OUTPUT_ERROR_MSG, e);
					logError("RepositoryException " + e);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(request, "BBBBusinessException from service of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_BUSINESS_EXCEPTION); request.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(request,	"BBBBusinessException from SERVICE of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
					request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
				} catch (Exception ex) {
					logError("BBBSystemException " + ex);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_SYSTEM_EXCEPTION);
					request.setParameter(OUTPUT_ERROR_MSG, ex.getMessage());
				}
			}
			if (userActiveRegList == null || userActiveRegList.isEmpty()) {
				request.setParameter(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_SIZE, 0);
			}
			else{
				int userActiveRegSize = 0;
				if(sessionBean.getValues().get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST) !=null){
					userActiveRegSize = ((ArrayList<String>) sessionBean.getValues().get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST)).size();
				}				
				request.setParameter(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_SIZE, userActiveRegSize);
			}
		}

		if (pRegSummaryVO != null) {
			logDebug(" GiftRegistryFlyoutDroplet service() MSG= soonestOrRecent set pRegSummaryVO info "
					+ " id :"	+ pRegSummaryVO.getRegistryId()
					+ " eventDate :" + pRegSummaryVO.getEventDate()
					+ " eventType :" + pRegSummaryVO.getEventType()
					+ " giftReg purchased :" + pRegSummaryVO.getGiftPurchased()
					+ " giftReg count :" + pRegSummaryVO.getGiftRegistered()
					+ " primaryRName :"	+ pRegSummaryVO.getPrimaryRegistrantFirstName()
					+ " coRegFname :"	+ pRegSummaryVO.getCoRegistrantFirstName());
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.GIFT_REG_FLYOUT_WS_CALL, methodName);
		request.serviceLocalParameter(OPARAM_OUTPUT, request, response);
		logDebug(" GiftRegistryFlyoutDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
	}

	protected long getDateDiff(String pSiteId, RegistrySummaryVO pRegSummaryVO) throws ParseException {
		return BBBUtility.getDateDiff(pRegSummaryVO.getEventDate(), pSiteId);
	}

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * @return the giftRegSessionBean
	 */
	public GiftRegSessionBean getGiftRegSessionBean() {
		return mGiftRegSessionBean;
	}

	/**
	 * @param giftRegSessionBean the giftRegSessionBean to set
	 */
	public void setGiftRegSessionBean(GiftRegSessionBean giftRegSessionBean) {
		mGiftRegSessionBean = giftRegSessionBean;
	}
}
