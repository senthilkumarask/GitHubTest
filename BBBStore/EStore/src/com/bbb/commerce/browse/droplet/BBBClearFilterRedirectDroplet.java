package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import atg.droplet.Redirect;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;


/**
 * This droplet extends OOB Redirect droplet and appends Query parameters with base
 * SEO url in case url contains clearFiletrs=true. Also it removes clearFiletrs=true parameter from the url.
 * BPS-2807
 * 
 */
public class BBBClearFilterRedirectDroplet extends Redirect {
	
	
	private static final String URL = "url";

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		String url = pRequest.getParameter(URL);
		String queryparams = (String) pRequest.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING);
	    StringBuilder urlbuilder = new StringBuilder();
	    if (!BBBUtility.isEmpty(queryparams)) {
	    	queryparams = queryparams.replaceAll(BBBCoreConstants.AMPERSAND + BBBCoreConstants.CLEAR_FILTERS_PARAM, BBBCoreConstants.BLANK);
	    	queryparams = queryparams.replaceAll(BBBCoreConstants.CLEAR_FILTERS_PARAM, BBBCoreConstants.BLANK);
	    	url = (urlbuilder.append(pRequest.getContextPath()).append(pResponse.encodeRedirectURL(url)).append(BBBCoreConstants.QUESTION_MARK).append(queryparams)).toString();
		} else {
			url = (urlbuilder.append(pRequest.getContextPath()).append(pResponse.encodeRedirectURL(url))).toString();
		}
	    
	    if (isLoggingDebug())
	    	logDebug("redirect URL after possible encoding: " + url);
	    
	    pResponse.sendRedirect(url);
		pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	}
}