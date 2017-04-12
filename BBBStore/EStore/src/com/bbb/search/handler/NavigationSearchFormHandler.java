package com.bbb.search.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;


import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBGenericFormHandler;

public class NavigationSearchFormHandler extends BBBGenericFormHandler{

	private String searchTerm;
	private static final String KEYWORD = "Keyword";
	private static final String EQUALS = "=";
	private String searchSuccessURL;
	
	/**
	 * @return the searchTerm
	 */
	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * @param pSearchTerm the searchTerm to set
	 */
	public void setSearchTerm(String pSearchTerm) {
		searchTerm = pSearchTerm;
	}

	/**
	 * @return the searchSuccessURL
	 */
	public String getSearchSuccessURL() {
		return searchSuccessURL;
	}

	/**
	 * @param pSearchSuccessURL the searchSuccessURL to set
	 */
	public void setSearchSuccessURL(String pSearchSuccessURL) {
		searchSuccessURL = pSearchSuccessURL;
	}
	
		
	/**
	 * This is the handle method that is invoked when the Search on Home Page.
	 * @param req The DynamoHttpServletRequest object.
	 * @param res The DynamoHttpServletResponse object.
	 * @return boolean 
	 * @throws IOException
	 */
	public boolean handleSearch(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		
		logDebug("Entering NavigationSearchFormHandler.handleSearch method.");
		
		
		if (getSearchTerm() != null) 
		{
			checkFormRedirect(pRequest.getContextPath()+createSearchSuccessUrl(), pRequest.getContextPath()+createSearchSuccessUrl(), pRequest, pResponse);
		}
		return false;
	}
	
	/**
	 * This method generates success URL to redirect the page to.
	 * @return String 
	 */
	private String createSearchSuccessUrl() {
		StringBuilder successUrl = new StringBuilder();
		successUrl.append(getSearchSuccessURL());
		successUrl.append(KEYWORD);
		successUrl.append(EQUALS);
		try{
			successUrl.append(URLEncoder.encode(getSearchTerm(),"UTF-8"));
		}
		catch(UnsupportedEncodingException e){
			logError(e.getMessage(),e);
			successUrl.append(getSearchTerm());
		}
		return successUrl.toString();
	}
}
