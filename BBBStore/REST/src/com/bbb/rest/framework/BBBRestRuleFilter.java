package com.bbb.rest.framework;

import com.bbb.common.BBBGenericService;


import atg.multisite.SiteContextRuleFilter;
import atg.multisite.SiteSessionManager;

import atg.servlet.DynamoHttpServletRequest;


public class BBBRestRuleFilter extends BBBGenericService implements
		SiteContextRuleFilter {

	private Boolean enabled = false;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean pEnabled) {
		enabled = pEnabled;
	}

	private String siteIdProperty;
	
	
	public String getSiteIdProperty() {
		return siteIdProperty;
	}

	public void setSiteIdProperty(String siteIdProperty) {
		this.siteIdProperty = siteIdProperty;
	}

	public String filter(DynamoHttpServletRequest pRequest,
			SiteSessionManager pSiteSessionManager) {

		
		logDebug("BBBRestRuleFilter.filter()  method started");
		
	
		String siteId = null;

		if (pRequest == null)
			return null;

		if (!isEnabled())
			return null;
		
		String requestSiteId = pRequest.getHeader(getSiteIdProperty());
		if (requestSiteId != null) {
			siteId = requestSiteId;
		}
		
		
		  logDebug("BBBRestRuleFilter.filter()::Site Id read from request header="+siteId);
			
		
		  logDebug("BBBRestRuleFilter.filter()  method end");
		
		return siteId;
	}

}
