package com.bbb.commerce.common.formhandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.droplet.GenericFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class BBBClearCookiesFormHandler extends GenericFormHandler {

	private static final String BLANK_STRING = "";
	private String clearCookiesRequired = "true";
    public String getClearCookiesRequired() {
		return clearCookiesRequired;
	}

	public void setClearCookiesRequired(String clearCookiesRequired) {
		this.clearCookiesRequired = clearCookiesRequired;
	}

	

	/** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleClearCookies(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	
    	if(this.getClearCookiesRequired().equalsIgnoreCase("true")){
    		
    		final Cookie[] cookies = pRequest.getCookies();
//    		final StringBuffer header = new StringBuffer();
//    		int cookies[i] = -1 ;
			if(cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					if ((cookies[i].getName()!= null) && (cookies[i].getName().equals("JSESSIONID"))) {
						cookies[i].setMaxAge(-1);
						cookies[i].setValue("");
						cookies[i].setPath("/");
//						cookieIndex = i;
						pResponse.addCookie(cookies[i]);
						break;
					}
				}
			}
			
    	}
			
			
			return this.checkFormRedirect("", "", pRequest, pResponse);
    }

   
    /**
	 * Performs the following 
	 * 1. Call handle Method to clear Cookies 
	 * 
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */

	public boolean handleClearMobileCookies(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException	{

		return this.handleClearCookies(pRequest, pResponse);
	}
   
}
