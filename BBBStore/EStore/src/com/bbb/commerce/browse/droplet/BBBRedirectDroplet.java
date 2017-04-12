package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import atg.droplet.Redirect;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * This droplet extends OOB Redirect droplet and set response status 
 * code from 302 to 301 for redirection from product_redirect.jsp to 
 * product SEO url. Also it removed _requestId parameter from the url.
 * BBBSL-1532
 * 
 * @author magga3
 *
 */
public class BBBRedirectDroplet extends Redirect {

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		String url = pRequest.getParameter("url");
	    StringBuilder urlbuilder = new StringBuilder();
	    url = (urlbuilder.append(pRequest.getContextPath()).append(pResponse.encodeRedirectURL(url))).toString();
	    
	    if (isLoggingDebug())
	    	logDebug("redirect URL after possible encoding: " + url);
	    
	    pResponse.sendRedirect(url);
		pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	}
}