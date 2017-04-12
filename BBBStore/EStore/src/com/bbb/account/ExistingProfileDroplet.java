/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: dwaghmare
 *
 * Created on: 2-Jan-2015
 * --------------------------------------------------------------------------------
 */
package com.bbb.account;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;

public class ExistingProfileDroplet extends BBBDynamoServlet {
	

	private static final String EMAIL_FLOW = "emailFlow";
	private BBBProfileTools profileTools;
	private BBBGetCouponsManager couponsManager;

	public BBBGetCouponsManager getCouponsManager() {
		return couponsManager;
	}

	public void setCouponsManager(BBBGetCouponsManager couponsManager) {
		this.couponsManager = couponsManager;
	}

	public BBBProfileTools getProfileTools() {
		return profileTools;
	}

	public void setProfileTools(BBBProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	/**
	 * This method is used to fetch data of my coupons to show on UI.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("GetCouponsDroplet.service() method Started");
	     
		
		String emailAddr = pRequest.getParameter(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME);
		String walletId = pRequest.getParameter(BBBCoreConstants.WALLET_ID);
		boolean isEmailFlow = Boolean.valueOf(pRequest.getParameter(EMAIL_FLOW));
		getProfileTools().createShallowForNonExistingUser(pRequest, emailAddr, walletId, isEmailFlow);
		pRequest.serviceLocalParameter("output", pRequest, pResponse);		
	}
	
}
