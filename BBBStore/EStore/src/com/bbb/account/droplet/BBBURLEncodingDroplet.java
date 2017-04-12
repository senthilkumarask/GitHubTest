/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBEncryptionDroplet.java
 *
 *  DESCRIPTION: BBBURLEncodingDroplet encodes the URL
 *  			 
 *  HISTORY:
 *  02/16/2012 Initial version
 *	02/20/2012 Moved constants to BBBAccountConstants.java file
 *
 */
package com.bbb.account.droplet;

import static com.bbb.constants.BBBAccountConstants.OPARAM_OUTPUT;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class BBBURLEncodingDroplet extends BBBDynamoServlet {

	private String mEncodingType;

	/**
	 * @return the encodingType
	 */
	public String getEncodingType() {
		return mEncodingType;
	}

	/**
	 * @param pEncodingType
	 *            the encodingType to set
	 */
	public void setEncodingType(String pEncodingType) {
		mEncodingType = pEncodingType;
	}

	/**
	 * This droplet encodes the URL and return the output
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	    BBBPerformanceMonitor.start("BBBURLEncodingDroplet", "service");
		
			logDebug("BBBURLEncodingDroplet.getResponseVO() method start");
		
		String encoding_type = pRequest.getParameter(BBBCoreConstants.ENCODING_TYPE);
		if (!StringUtils.isEmpty(encoding_type)) {
			mEncodingType = encoding_type;
		} else {
			mEncodingType = BBBCoreConstants.UTF_8;
		}
		String url = pRequest.getParameter(BBBCoreConstants.URL);
		String queryString =  pRequest.getQueryString();
		
			logDebug("URL = " + url + " encoding_type = " + encoding_type);
		
		if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(queryString) ) {
			String encodedurl = URLEncoder.encode(url, mEncodingType);
			pRequest.setParameter(BBBCoreConstants.ENCODED_URL, encodedurl);
			pRequest.setParameter(BBBCoreConstants.QUERY_STRING, queryString);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		}
		
			logDebug("BBBURLEncodingDroplet.getResponseVO() method ends");
		
		BBBPerformanceMonitor.end("BBBURLEncodingDroplet", "service");
	}
}
