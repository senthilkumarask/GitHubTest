package com.bbb.framework.security;

import java.io.IOException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;


public class SiteIdSecurityServlet extends InsertableServletImpl 
{

	private static String CONTEXT = "/store";
	private static String REST_CONTEXT = "/rest";
	private boolean enableServlet;
	
	public boolean getEnableServlet() {
		return enableServlet;
	}

	public void setEnableServlet(boolean enableServlet) {
		this.enableServlet = enableServlet;
	}

	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		   
		logDebug("SiteIdSecurityServlet Pipeline Servlet invoked for URL= " + pRequest.getRequestURL());

    	logDebug("SiteIdSecurityServlet - Value of enableServlet parameter= " + getEnableServlet());

    	if (!getEnableServlet())
    	{
    		logDebug("SiteIdSecurityServlet is disabled so skipping the process for setting siteId in request");
    		passRequest(pRequest, pResponse);
    		return;
    	}
    	
    	if (!CONTEXT.equals(pRequest.getContextPath()) && !REST_CONTEXT.equals(pRequest.getContextPath()))
    	{
    		logDebug("SiteIdSecurityServlet - Context is not /store or /rest so skipping the setting siteId in request");
    		passRequest(pRequest, pResponse);
    		return;
    	}
    	
    	
    	String siteId = (String) pRequest.getAttribute(BBBCoreConstants.SITE_ID);
    	logDebug("SiteIdSecurityServlet - getting attribute siteId in request = " + siteId);
    	
		if(BBBUtility.isEmpty(siteId)) {
			siteId = SiteContextManager.getCurrentSiteId();
			pRequest.setAttribute(BBBCoreConstants.SITE_ID, siteId);
			logDebug("SiteIdSecurityServlet - setting siteId in request = " + siteId);
		}
		
		passRequest(pRequest, pResponse);
		return;
		
    }

    public void init(FilterConfig config) throws ServletException
    {
    	//init method, do nothing
    }
    @Override
    public void destroy()
    {
    	//destroy method, do nothing
    }
    
    @Override
    public void logDebug(String pMessage) {
    	if (isLoggingDebug())
    	{
    		super.logDebug(pMessage);
    	}
    }
    
}


