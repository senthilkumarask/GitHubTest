package com.bbb.pipeline;

/*
 *  Copyright 2016, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBCheckSDDEligibilityServlet.java
 *
 *  DESCRIPTION: A pipeline servlet to check Same Day Delivery conditions. 
 *   
 */

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.commerce.browse.vo.SddZipcodeVO;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

/**
 * DESCRIPTION: A pipeline servlet to check Same Day Delivery conditions and update session attributes.
 * 
 * @author sghosh
 */
public class BBBCheckSDDEligibilityServlet extends InsertableServletImpl {
	
	

	/**
	 * This service is invoked on every request and populates the landing and current zipcode vo in session. 
	 * Landing modal will be displayed only for the first hit of home page
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return void
	 */
	@Override
	
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws IOException, ServletException {if (isLoggingDebug()) {
		logDebug("BBBCheckSDDEligibilityServlet.start" + pRequest.getRequestURI());
	}
	    boolean sameDayDeliveryFlag = false;
		boolean isAkamaiOn = false;
		
	String customerZip = "";
	
	String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
	
	if(null != sddEligibleOn){
		sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
	}
		
		String akamiOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.HOME_PAGE_CACHING_KEY);
		if(null != akamiOn){
			isAkamaiOn = Boolean.valueOf(akamiOn);
		}
		
	// first check the same day delivery turn on flag is true
	if(sameDayDeliveryFlag){
		//resolve session component and retrieve landingZipcodeVo.
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		SddZipcodeVO landingZipcodeVO =  sessionBean.getLandingZipcodeVO();
		
		if(landingZipcodeVO == null){
			
			if(null != pRequest.getParameter(BBBCoreConstants.CUSTOMER_ZIP)){
				customerZip = pRequest.getParameter(BBBCoreConstants.CUSTOMER_ZIP);
			} else {

					String headerValue = pRequest.getHeader(BBBCoreConstants.AKAMAI_HEADER);
				if (!BBBUtility.isEmpty(headerValue)) {
					Map<String, String> akamaiMap = BBBUtility.getAkamaiHeaderValueMap(headerValue);
					if (null != akamaiMap) {
						customerZip = akamaiMap.get(BBBCoreConstants.ZIP);
					}
				}

			}
			
			if(isLoggingDebug()){
				logDebug("BBBCheckSDDEligibilityServlet Customer Zip from request header is " + customerZip);
			}
			
			// Check that we get customer Zip from the request header
			if(!StringUtils.isBlank(customerZip)){
				if(customerZip.contains(BBBCoreConstants.HYPHEN)){
					String[] customerZipArr = customerZip.split(BBBCoreConstants.HYPHEN);
					customerZip = customerZipArr[0];
				}
				getSameDayDeliveryManager().populateDataInVO(sessionBean,
						pRequest, customerZip, BBBCoreConstants.RETURN_TRUE, BBBCoreConstants.RETURN_TRUE, BBBCoreConstants.RETURN_FALSE);
				
			}
		

		}
	}else{
		// if same day delivery flag is false then we set the landing and current zipcode VO to null
		if(isLoggingDebug()){
			logDebug("BBBCheckSDDEligibilityServlet : Same day delivery flag is false , so set the landing and current zipcode VO to null");
		}
	}
	
		String sddAttribCookieValue = BBBUtility.getCookie(pRequest, BBBCoreConstants.SDD_ATTRIBUTE);
		if((!isAkamaiOn || !sameDayDeliveryFlag) && StringUtils.isNotEmpty(sddAttribCookieValue)){
				getSameDayDeliveryManager().deleteSddAttrCookie(pResponse);
				logDebug("Sdd attribute cookie removed in BBBCheckSDDEligibilityServlet");
			
		}
	passRequest(pRequest, pResponse);
	if (isLoggingDebug()) {
		logDebug("BBBCheckSDDEligibilityServlet.end");
		}
	
	}


	private BBBSameDayDeliveryManager sameDayDeliveryManager;
	private BBBCatalogToolsImpl bbbCatalogTools;
	private String sddCookieName;
	private String sddCookiePath;
	

	public String getSddCookieName() {
		return sddCookieName;
	}

	public void setSddCookieName(String sddCookieName) {
		this.sddCookieName = sddCookieName;
	}

	public String getSddCookiePath() {
		return sddCookiePath;
	}

	public void setSddCookiePath(String sddCookiePath) {
		this.sddCookiePath = sddCookiePath;
	}

	public BBBSameDayDeliveryManager getSameDayDeliveryManager() {
		return sameDayDeliveryManager;
	} 

	public void setSameDayDeliveryManager(
			BBBSameDayDeliveryManager sameDayDeliveryManager) {
		this.sameDayDeliveryManager = sameDayDeliveryManager;
	}

	public BBBCatalogToolsImpl getBbbCatalogTools() {
		return bbbCatalogTools;
	}
	public void setBbbCatalogTools(BBBCatalogToolsImpl bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	
	}
	