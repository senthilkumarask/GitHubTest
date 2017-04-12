//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: ssha53
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.checklist.vo.NonRegistryGuideVO;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * This class is used to return the information that the page will show
 * persistent or generic subheader
 * 
 * 
 * @author ssha53
 */
public class SelectHeaderDroplet extends BBBPresentationDroplet {

	private List<String> mPagesWithGenericHeadOnly;

	private List<String> mPagesWithGenOrPersistentHead;

	private List<String> mPagesWithPersistentOrNoHead;

	private List<String> mPagesWithGenericHeadBabyOnly;
	private List<String> refererPagesWithPersistentOrNoHead;
	
	public List<String> getRefererPagesWithPersistentOrNoHead() {
		return refererPagesWithPersistentOrNoHead;
	}
	


	public void setRefererPagesWithPersistentOrNoHead(List<String> refererPagesWithPersistentOrNoHead) {
		this.refererPagesWithPersistentOrNoHead = refererPagesWithPersistentOrNoHead;
	}
	


	private List<String> pagesWithAjaxCall;

	public static final String SHOW_GENERIC_HEADER = "showGenericHeader";

	public static final String SHOW_PERSISTENT_HEADER = "showPersistentHeader";
	
	public static final String SHOW_GUIDE_HEADER = "showGuideHeader";

	public static final String REGISTRY_SUMMARY_VO = "registrySummaryVO";

	public static final String NO_HEADER = "noHeader";
	
	public static final String SUB_HEADER="subheader";
	
	public static final String STATIC_HEADER = "showStaticHeader";
	public static final String REGISTRY_FEATURES = "/store/bbregistry/registry_features.jsp";
	public static final String REGISTRY_OWNER_PAGE = "/store/giftregistry/view_registry_owner.jsp";
	public static final String REGISTRY_GUEST_PAGE = "/store/giftregistry/view_registry_guest.jsp";
	public static final String REGISTRY_LIST_VIEW_URI = "registryListViewUri";
	public static final String CATEGORY_PAGES = "categorypages";
	public static final String ANNOUNCEMENT_CARDS = "/store/printCards/printCardsLanding.jsp";
	public static final String BRIDAL_BOOK = "/store/bbregistry/bridal_book.jsp";
	public static final String PRINT_CARDS = "/store/printCards/printCards.jsp";
	public static final String KICKSTARTERS = "/store/giftregistry/view_kickstarters.jsp";
	public static final String TOP_CONSULTANTS = "/store/giftregistry/kickstarters/top_consultants_picks.jsp";
	public static final String SHOP_THE_LOOK="/store/giftregistry/kickstarters/shop_the_look_details.jsp";
	// To disable dropdown of registry in case of kickstarters ,shop this look and top consultant. | ILD- 20
	public static final String DISABLE_REGISTRY_DROPDOWN="disableRegistryDropdown";
	public static final String REGISTRY_OWNER_VIEW="registryOwnerView";
	/** The gift reg session bean. */
	private GiftRegSessionBean mGiftRegSessionBean;


	/**
	 * Fetch subheader info and display appropriate subheader for a page.
	 * 
	 * @param pRequest
	 *            - http request
	 * @param pResponse
	 *            - http response
	 * @throws IOException
	 *             if an error occurs
	 * @throws ServletException
	 *             if an error occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		logDebug(" SelectHeaderDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String methodName = "service";
		BBBPerformanceMonitor.start("SelectHeaderDroplet", methodName);

		List<String> listUserRegIds = null;
		final String pageURI = pRequest.getRequestURI();
		final String refererURI=pRequest.getHeader(BBBCoreConstants.REFERRER);
		// Retrieve RegistrySummaryVO from BBBSessionBean.
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final HashMap sessionMap = sessionBean.getValues();

		listUserRegIds = (List) sessionMap.get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST);
		
		RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
		if (null != registrySummaryVO && null != registrySummaryVO.getEventDate()) {
			registrySummaryVO.setEventDate(BBBUtility
					.convertDateWSToAppFormat(registrySummaryVO
							.getEventDate()));
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
					registrySummaryVO);
		}
		List<NonRegistryGuideVO> nonRegistryGuideVOs = (List<NonRegistryGuideVO>) sessionMap.get(BBBGiftRegistryConstants.GUIDE_VO_LIST);
		
		boolean checkToActivateGuide = Boolean.parseBoolean(pRequest.getParameter("checkToActivateGuide"));
		if (checkToActivateGuide) {
			if (registrySummaryVO == null && !BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
				sessionBean.setActivateGuideInRegistryRibbon(true);
			}
		}
		if(pRequest.getParameter("checkToActivateGuide") == null){
			if (registrySummaryVO == null && !BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
				sessionBean.setActivateGuideInRegistryRibbon(true);
			}
		}
		

		logDebug(" SelectHeaderDroplet registrySummaryVO fetched from the session");
		
		final Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);

		if (getPagesWithGenericHeadOnly().contains(pageURI)) {
			// Set OPARAM SHOW_GENERIC_HEADER
			if(pageURI.equalsIgnoreCase(REGISTRY_OWNER_PAGE) || REGISTRY_GUEST_PAGE.equalsIgnoreCase(pageURI)){
				if (pageURI.equalsIgnoreCase(REGISTRY_OWNER_PAGE)) {
					sessionBean.setActivateGuideInRegistryRibbon(false);
					pRequest.setParameter(REGISTRY_OWNER_VIEW,BBBCoreConstants.TRUE);
				}
				pRequest.setParameter(REGISTRY_LIST_VIEW_URI,BBBCoreConstants.TRUE);
			}
			
			pRequest.setParameter(SUB_HEADER, SHOW_GENERIC_HEADER);
			pRequest.setParameter(REGISTRY_SUMMARY_VO, registrySummaryVO);
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);

		} else if (getPagesWithGenOrPersistentHead().contains(pageURI)) {
			
			if(refererURI !=null && (refererURI.contains(REGISTRY_OWNER_PAGE) || refererURI.contains(REGISTRY_GUEST_PAGE))){
				if (refererURI.contains(REGISTRY_OWNER_PAGE)) {
					sessionBean.setActivateGuideInRegistryRibbon(false);
					pRequest.setParameter(REGISTRY_OWNER_VIEW,BBBCoreConstants.TRUE);
				}
				pRequest.setParameter(REGISTRY_SUMMARY_VO, registrySummaryVO);
				pRequest.setParameter(SUB_HEADER, SHOW_GENERIC_HEADER);
				pRequest.setParameter(REGISTRY_LIST_VIEW_URI,BBBCoreConstants.TRUE);
			}else if(pageURI.equalsIgnoreCase("/store/_ajax/hideNonRegistryGuideSuccessJson.jsp")){
				if(refererURI !=null && (refererURI.contains("registry/RegistryChecklist") || refererURI.contains("registry/GuidesAndAdviceLandingPage") || refererURI.contains("registry/RegistryIncentives") || refererURI.contains("registry/RegistryFeatures") || refererURI.contains("printCards/printCardsLanding") || refererURI.contains("bbregistry/BridalBook") || refererURI.contains("printCards/printCards") || refererURI.contains("kickstarters") || refererURI.contains("registry/PersonalizedInvitations"))){
					pRequest.setParameter(STATIC_HEADER, BBBCoreConstants.TRUE);
				}
			}else if(refererURI !=null && !profile.isTransient() && (refererURI.contains(BBBCoreConstants.TOP_CONSULTANTS_PREFIX) || refererURI.contains(BBBCoreConstants.SHOP_THIS_LOOK_PREFIX))){
				// Added for cases when on top consultant pages adding all items to gift registry the drop down of registries appear in registry ribbon
				if(registrySummaryVO !=null){
				pRequest.setParameter(DISABLE_REGISTRY_DROPDOWN, BBBCoreConstants.TRUE);
				sessionBean.setActivateGuideInRegistryRibbon(false);
				}
				if(isPersistentHeader(sessionBean,registrySummaryVO, listUserRegIds, profile.isTransient(), nonRegistryGuideVOs )){
					pRequest.setParameter(REGISTRY_SUMMARY_VO, registrySummaryVO);
					pRequest.setParameter(SUB_HEADER, SHOW_PERSISTENT_HEADER);
				}
			}else if(refererURI !=null && referURIInList(refererURI) && isPersistentHeader(sessionBean,registrySummaryVO, listUserRegIds, profile.isTransient(), nonRegistryGuideVOs )){
				
				pRequest.setParameter(REGISTRY_SUMMARY_VO, registrySummaryVO);
				pRequest.setParameter(SUB_HEADER, SHOW_PERSISTENT_HEADER);
				logDebug(" SelectHeaderDroplet loggedin user with getGiftRegSessionBean().getRegistryOperation() value in session ");
			} 
			else if(refererURI !=null && registrySummaryVO == null && (refererURI.contains("registry/RegistryChecklist") || refererURI.contains("registry/GuidesAndAdviceLandingPage") || refererURI.contains("registry/RegistryIncentives") || refererURI.contains("registry/RegistryFeatures") || refererURI.contains("printCards/printCardsLanding") || refererURI.contains("bbregistry/BridalBook") || refererURI.contains("printCards/printCards") || refererURI.contains("kickstarters") || refererURI.contains("registry/PersonalizedInvitations"))){
				pRequest.setParameter(STATIC_HEADER, BBBCoreConstants.TRUE);
				pRequest.setParameter(SUB_HEADER, SHOW_PERSISTENT_HEADER);
			}
			else{
				pRequest.setParameter(SUB_HEADER, NO_HEADER);
			}
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);

		} else if (getPagesWithPersistentOrNoHead().contains(pageURI)) {
			
			if((SHOP_THE_LOOK.equalsIgnoreCase(pageURI) || TOP_CONSULTANTS.equals(pageURI)) && registrySummaryVO!=null){
				pRequest.setParameter(DISABLE_REGISTRY_DROPDOWN, BBBCoreConstants.TRUE);
				sessionBean.setActivateGuideInRegistryRibbon(false);
				
			}

			if(isPersistentHeader(sessionBean,registrySummaryVO, listUserRegIds, profile.isTransient(), nonRegistryGuideVOs )){
				pRequest.setParameter(REGISTRY_SUMMARY_VO, registrySummaryVO);
				pRequest.setParameter(SUB_HEADER, SHOW_PERSISTENT_HEADER);
				if(getPagesWithAjaxCall().contains(pageURI)){
					pRequest.setParameter(CATEGORY_PAGES,BBBCoreConstants.TRUE);
				}
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
			}
			else if(pageURI.equalsIgnoreCase(REGISTRY_FEATURES) || pageURI.equalsIgnoreCase(ANNOUNCEMENT_CARDS) || pageURI.equalsIgnoreCase(BRIDAL_BOOK) || PRINT_CARDS.equalsIgnoreCase(pageURI) || KICKSTARTERS.equalsIgnoreCase(pageURI)){
				pRequest.setParameter(STATIC_HEADER, BBBCoreConstants.TRUE);
				pRequest.setParameter(SUB_HEADER, SHOW_PERSISTENT_HEADER);
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
			}
			else{
				pRequest.setParameter(SUB_HEADER, NO_HEADER);
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
			}

		}else if(getPagesWithGenericHeadBabyOnly().contains(pageURI)){
			String siteId = this.getCurrentSiteId();
			if((BBBCoreConstants.SITE_BBB).equalsIgnoreCase(siteId)){
				pRequest.setParameter(SUB_HEADER, NO_HEADER);
			}else{
				pRequest.setParameter(SUB_HEADER, SHOW_GENERIC_HEADER);
			}
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
		}
		else {
			pRequest.setParameter(SUB_HEADER, NO_HEADER);
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
			logDebug("set noHeader  output to the display page");
		}
		
		BBBPerformanceMonitor.end("SelectHeaderDroplet", methodName);
		logDebug(" SelectHeaderDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}
	
	
	/**
	 * This method is used to check for displayinh persistent header or not.
	 * 
	 * @param registrySummaryVO
	 * @param nonRegistryGuideVOs 
	 * @return
	 */
	private boolean isPersistentHeader(BBBSessionBean sessionBean,RegistrySummaryVO registrySummaryVO, List<String> listUserRegIds, boolean isTransient, List<NonRegistryGuideVO> nonRegistryGuideVOs){
		
		boolean showPersistentHeader = false;
			
		if (null == listUserRegIds && isTransient) {
			if (BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
				showPersistentHeader = false;
			} else {
				showPersistentHeader = true;
			}
			return showPersistentHeader;
		} 
		
		if (BBBGiftRegistryConstants.GR_CREATE.equals(getGiftRegSessionBean().getRegistryOperation())
				|| BBBGiftRegistryConstants.GR_UPDATE.equals(getGiftRegSessionBean().getRegistryOperation())
				|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(getGiftRegSessionBean().getRegistryOperation())
				|| BBBGiftRegistryConstants.OWNER_VIEW.equals(getGiftRegSessionBean().getRegistryOperation())) {
			
			if(null != registrySummaryVO){
				showPersistentHeader = true;
			}
		} else {
			if (null != registrySummaryVO) {
				Long diffDays = null;
				try {
					String siteId = this.getCurrentSiteId();
					if(!BBBUtility.isEmpty(registrySummaryVO.getEventDate())){
						diffDays = BBBUtility.getDateDiff(registrySummaryVO.getEventDate(), siteId);
					}
				} catch (ParseException e) {
					logError("SelectHeaderDroplet: exception in parsing date",
							e);
				}
				if (diffDays ==null || diffDays >= -90) {
					// set registySummaryVO in request
					showPersistentHeader = true;
					logDebug("set showPersistentHeader  output to the display page");

				} else {
					if(sessionBean.isRegDiffDateLess()){
						showPersistentHeader = true;
					}
					else{
						showPersistentHeader = false;
					}
				}
			}else if (!BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
				showPersistentHeader = true;
			} 
			else {
				showPersistentHeader = false;
			}
		}
		return showPersistentHeader;
	}

	/**
	 * Check if the referrerURI is present in the list.
	 * @param refererURI
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean referURIInList(String refererURI) {
		logDebug("Inside Class SelectHeaderDroplet.referURIInList : refererURI : "+refererURI );
		if (refererURI != null) {
			ListIterator listIter = getRefererPagesWithPersistentOrNoHead().listIterator();
			while (listIter.hasNext()) {
				if (refererURI.contains((String) listIter.next())) {
					logDebug("Inside Class SelectHeaderDroplet.referURIInList : Match found" );
					return true;
				}
			}
		}
		logDebug("Exiting Class SelectHeaderDroplet.referURIInList : No Match found" );
		return false;
	}
	/**
	 * @return
	 */
	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}


	/**
	 * @return the pagesWithGenericHeadOnly
	 */
	public List<String> getPagesWithGenericHeadOnly() {
		return mPagesWithGenericHeadOnly;
	}


	/**
	 * @param pagesWithGenericHeadOnly the pagesWithGenericHeadOnly to set
	 */
	public void setPagesWithGenericHeadOnly(final List<String> pagesWithGenericHeadOnly) {
		mPagesWithGenericHeadOnly = pagesWithGenericHeadOnly;
	}


	/**
	 * @return the pagesWithGenOrPersistentHead
	 */
	public List<String> getPagesWithGenOrPersistentHead() {
		return mPagesWithGenOrPersistentHead;
	}


	/**
	 * @param pagesWithGenOrPersistentHead the pagesWithGenOrPersistentHead to set
	 */
	public void setPagesWithGenOrPersistentHead(
			final List<String> pagesWithGenOrPersistentHead) {
		mPagesWithGenOrPersistentHead = pagesWithGenOrPersistentHead;
	}


	/**
	 * @return the pagesWithPersistentOrNoHead
	 */
	public List<String> getPagesWithPersistentOrNoHead() {
		return mPagesWithPersistentOrNoHead;
	}


	/**
	 * @param pagesWithPersistentOrNoHead the pagesWithPersistentOrNoHead to set
	 */
	public void setPagesWithPersistentOrNoHead(
			final List<String> pagesWithPersistentOrNoHead) {
		mPagesWithPersistentOrNoHead = pagesWithPersistentOrNoHead;
	}

	/**
	 * @return the mPagesWithGenericHeadBabyOnly
	 */
	public List<String> getPagesWithGenericHeadBabyOnly() {
		return mPagesWithGenericHeadBabyOnly;
	}


	/**
	 * @param mPagesWithGenericHeadBabyOnly the mPagesWithGenericHeadBabyOnly to set
	 */
	public void setPagesWithGenericHeadBabyOnly(
			final List<String> pagesWithGenericHeadBabyOnly) {
		mPagesWithGenericHeadBabyOnly = pagesWithGenericHeadBabyOnly;
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


	public List<String> getPagesWithAjaxCall() {
		return pagesWithAjaxCall;
	}


	public void setPagesWithAjaxCall(List<String> pagesWithAjaxCall) {
		this.pagesWithAjaxCall = pagesWithAjaxCall;
	}
	
}
