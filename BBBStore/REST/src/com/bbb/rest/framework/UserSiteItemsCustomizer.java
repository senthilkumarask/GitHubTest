package com.bbb.rest.framework;

import java.util.Map;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.rest.filtering.RestPropertyCustomizer;

import com.bbb.common.BBBGenericService;
//import com.bbb.utils.BBBUtility;


public class UserSiteItemsCustomizer  extends BBBGenericService  implements RestPropertyCustomizer{

	@SuppressWarnings("rawtypes")
	public Object getPropertyValue(String pPropertyName, Object pResource) {
	
				
		logDebug("Entering UserSiteItemsCustomizer.getPropertyValue");
		
		
		
		Map userSiteItems = null;
		//Map siteItems = null;
		RepositoryItem item = null;
		
		try {
			
			userSiteItems = (Map) DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
			
			item = (RepositoryItem) userSiteItems.get(SiteContextManager.getCurrentSiteId());
			
		
			logDebug("UserSiteItemsCustomizer::: value="+item);
			
			
		} catch (PropertyNotFoundException e) {
			logDebug(e.getMessage(),e);
		}

		return item;

	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
