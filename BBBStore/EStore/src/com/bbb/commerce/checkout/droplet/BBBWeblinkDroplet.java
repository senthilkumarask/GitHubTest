package com.bbb.commerce.checkout.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;

/**
 * @author rsain4
 * 
 */

/*This droplet determines whether it's a weblink order or 
 * not based on the cookie schoolCookie
 */

public class BBBWeblinkDroplet extends BBBDynamoServlet {

	private static String SCHOOL_COOKIE = "SchoolCookie";
	public final static String WEBLINK_ORDER = "weblinkOrder";
	public final static String NOT_WEBLINK_ORDER = "notWeblinkOrder";
	

	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("Entry BBBWeblinkDroplet.service");

		String collegeIdValue = pRequest.getCookieParameter(SCHOOL_COOKIE);

		if (StringUtils.isBlank(collegeIdValue)) {
			logDebug("BBBWeblinkDroplet : College Id is null "
					+ collegeIdValue);
			pRequest.serviceParameter(NOT_WEBLINK_ORDER, pRequest, pResponse);
		}else{
			pRequest.serviceParameter(WEBLINK_ORDER, pRequest, pResponse);
		}

		logDebug("Exit BBBWeblinkDroplet.service");
	}
}