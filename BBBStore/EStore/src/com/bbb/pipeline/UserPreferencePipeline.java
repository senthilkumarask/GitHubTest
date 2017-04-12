/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  UserPreferencePipeline.java
 *
 *  DESCRIPTION: A pipeline handling the user Preference 
 *  HISTORY:
 *  05/10/13 Initial version
 *
 */
package com.bbb.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.checklist.vo.NonRegistryGuideVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * DESCRIPTION: A pipeline handling the user Preference
 * 
 * @author Rajesh Saini
 */
public class UserPreferencePipeline extends InsertableServletImpl {

	private static final String PAGE_SIZE_COOKIE_TIMEOUT = "pageSize_cookie_timeout";
	private static final String PAGE_SIZE_COOKIE_NAME = "pageSizeFilter";
	private static final String PAGE_FILTER_OPT = "pagFilterOpt";
	private static final String MEDAI_PARAM = "media";
	private static final int MAX_TIME_OUT = 100000;
	private List<String> mPagesWithPageFilter;
	
	private Boolean mEnabled;
	private BBBCatalogTools catalogTools;
	private CheckListManager checkListManager;
	private CheckListTools checkListTools;

	public CheckListManager getCheckListManager() {
		return checkListManager;
	}

	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}

	public CheckListTools getCheckListTools() {
		return checkListTools;
	}

	public void setCheckListTools(CheckListTools checkListTools) {
		this.checkListTools = checkListTools;
	}

	public Boolean getEnabled() {
		return mEnabled;
	}

	public void setEnabled(final Boolean pEnabled) {
		this.mEnabled = pEnabled;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.catalogTools = bbbCatalogTools;
	}
	
	public List<String> getPagesWithPageFilter() {
		return mPagesWithPageFilter;
	}

	public void setPagesWithPageFilter(List<String> pPagesWithPageFilter) {
		this.mPagesWithPageFilter = pPagesWithPageFilter;
	}

	/**
	 * OverRided method from ATG OOTB InsertableServletImpl and set the user
	 * preferences in cookie
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with Servlet io
	 * @return void
	 */
	public void service(DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		if (isLoggingDebug()) {
			logDebug("UserPreferencePipeline.service() method started");
		}
		//check for EDW call for dyn user id cookie
		if(!BBBUtility.isEmpty(pRequest.getCookieParameter(BBBCoreConstants.DYN_USER_ID))){
			pRequest.setAttribute(BBBCoreConstants.IS_DYN_USER_COOKIE_EXISTS, true);;
		}else{
			pRequest.setAttribute(BBBCoreConstants.IS_DYN_USER_COOKIE_EXISTS, false);
		}
		if (!getEnabled()) {
			passRequest(pRequest, pResponse);
		} else {
			try {
				final String pageURI = pRequest.getRequestURI();
				if (getPagesWithPageFilter().contains(pageURI)) {
					String pageSize = null;
					String fromMedaiPage = null;
					pageSize = pRequest.getQueryParameter(PAGE_FILTER_OPT);
					fromMedaiPage = pRequest.getQueryParameter(MEDAI_PARAM);
					if(!BBBUtility.isEmpty(pageSize) && BBBUtility.isEmpty(fromMedaiPage)){
						int maxTimeout = MAX_TIME_OUT;
						List<String> cookieTimeOut = null;
						try {
							cookieTimeOut = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, PAGE_SIZE_COOKIE_TIMEOUT);
						} catch (BBBBusinessException e) {
							if (isLoggingDebug()) {
								logDebug("Key Not found in  " + BBBCatalogConstants.CONTENT_CATALOG_KEYS + " for " +  PAGE_SIZE_COOKIE_TIMEOUT);
							}
						}
						if (cookieTimeOut != null) {
							try {
								maxTimeout = Integer.parseInt((String) cookieTimeOut.get(0));
							} catch (NumberFormatException nfe) {
								throw new BBBSystemException("Cannot Cast To Number" + nfe);
							}
						}
					
						final Cookie pageSizeFilterCookie = new Cookie(PAGE_SIZE_COOKIE_NAME, pageSize);
						pageSizeFilterCookie.setMaxAge(maxTimeout);
						pageSizeFilterCookie.setPath(BBBCoreConstants.SLASH);
						if (isLoggingDebug()) {
							logDebug("UserPreferencePipeline.service() Adding cookie : " + pageSizeFilterCookie.getName() + "=" + pageSizeFilterCookie.getValue());
						}
						BBBUtility.addCookie(pResponse, pageSizeFilterCookie, false);
						
					}
				}
				
				setRegistryGuideFromCookie(pRequest);
				
			} catch (BBBSystemException e) {
				if (isLoggingDebug()) {
					logDebug("UserPreferencePipeline : BBBSystemException" + e.getMessage());
				}
				passRequest(pRequest, pResponse);
			} catch (BBBBusinessException e) {
				logError("UserPreferencePipeline : BBBBusinessException", e);
			}
			passRequest(pRequest, pResponse);
		}
		if (isLoggingDebug()) {
			logDebug("UserPreferencePipeline.service() method ends");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setRegistryGuideFromCookie(DynamoHttpServletRequest pRequest) throws BBBSystemException, BBBBusinessException {
		
		final BBBSessionBean sessionBean = (BBBSessionBean)pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
		Map sessionMap = sessionBean.getValues();
		String guideCookieName = null;
		boolean isSelectedGuideSet = false;
		if (sessionBean.getChecklistVO() == null &&  sessionMap.get(BBBGiftRegistryConstants.GUIDE_VO_LIST) == null) {
			guideCookieName = BBBCoreConstants.INTERACTIVE_GUIDE_COOKIE_NAME;			
			String registryGuideCookie = pRequest.getCookieParameter(guideCookieName);
			if (!BBBUtility.isEmpty(registryGuideCookie)) {
				String [] guideTypes = registryGuideCookie.split(BBBCoreConstants.COLON);
				if (!BBBUtility.isArrayEmpty(guideTypes)) {
					pRequest.setParameter("checkToActivateGuide", "true");
					NonRegistryGuideVO	nonRegistryGuideVO = null;
					List<NonRegistryGuideVO> nonRegistryGuideVOs = new ArrayList<NonRegistryGuideVO>();
					for (int index = 0; index < guideTypes.length; index++) {
						nonRegistryGuideVO = getCheckListManager().getNonRegistryGuide (guideTypes[index], BBBCoreConstants.GUIDE);
						// changes for ILD-227 | if guide is disabled then not adding guide into session.
						if (!nonRegistryGuideVO.isGuideDisabled()) {
							if (isSelectedGuideSet == false) {
								sessionMap.put(BBBCoreConstants.SELECTED_GUIDE_TYPE, guideTypes[index]);
								isSelectedGuideSet = true;
							}
							nonRegistryGuideVOs.add(nonRegistryGuideVO);
						}
					}
					sessionMap.put(BBBGiftRegistryConstants.GUIDE_VO_LIST, nonRegistryGuideVOs);
				}
			}
		}
	}
	
}
