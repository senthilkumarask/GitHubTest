/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBChangeAnchorTagUrlDroplet.java
 *
 *  DESCRIPTION: BBBChangeAnchorTagUrlDroplet changes the anchor tags in given HTML string
 *  			 with the intercept page. This was designed specifically for Levolor intercept page.
 *  			 
 *  HISTORY:
 *  02/16/2012 Initial version
 *	02/20/2012 Moved constants to BBBAccountConstants.java file
 *
 */

package com.bbb.account.droplet;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.servlet.ServletException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import static com.bbb.constants.BBBAccountConstants.*;

public class BBBChangeAnchorTagUrlDroplet extends BBBDynamoServlet {
	

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		final String changedUrl= pRequest.getParameter(PARAM_CHANGED_URL);
		final String requestParamName= pRequest.getParameter(PARAM_REQUEST_NAME);
		final String htmlString = pRequest.getParameter(PARAM_HTML_STRING);
		
			logDebug("changedUrl is:"+ changedUrl);
			logDebug("requestParamName is:"+ requestParamName);
			logDebug("htmlString is:"+ htmlString);
		try {
			final Document document=Jsoup.parse(htmlString);
			final Iterator<Element> links = document.select(HTML_ANCHOR).iterator();
			//iterate all anchor links
			while(links.hasNext()){
				final Element link = links.next();
				final String linkHref = link.attr(HTML_HREF);
				//modify the link if and only if it begins with http
				if(linkHref!=null && linkHref.toLowerCase().startsWith(HTTP)){
					link.attr(HTML_HREF, changedUrl+URL_QUESTION+requestParamName+URL_EQUAL+URLEncoder.encode(linkHref,UTF8));
				}
			}
			//save the modified string. we will use only body part
			final String changedHtmlString = document.body().html();
				logDebug("changedAnchorTagHtml is:"+ changedHtmlString);
			pRequest.setParameter(PARAM_CHANGED_HTML_STRING,changedHtmlString);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		} catch (UnsupportedEncodingException e) {
				logError("UnsupportedEncodingException:"+ e);
			pRequest.setParameter(PARAM_ERROR_MSG,e.toString());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}

	}
	
}
