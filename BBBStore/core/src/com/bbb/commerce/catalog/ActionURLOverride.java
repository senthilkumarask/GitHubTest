package com.bbb.commerce.catalog;

import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.utils.BBBUtility;

public class ActionURLOverride extends GSAPropertyDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2134045681150703829L;
	/**
	 * The type name property.
	 */
	private static final String TYPE_NAME = "actionUrlOverride";
	private static final String SITE_ID = "siteId";
	private static final String BLANK = "";
	
	/**
	 * 
	 */

	static {
		RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME,
				ActionURLOverride.class);
	}

	/**
	 * Returns the value of the underlying property.
	 * 
	 * @param pItem
	 *            the RepositoryItem to retrieve the value from
	 * @param pValue
	 *            the value to retrieve
	 * @return The property value requested
	 */
	public Object getPropertyValue(final RepositoryItemImpl pItem,
			final Object pValue) {
		
			String siteId = BLANK;
			DynamoHttpServletRequest currentRequest = ServletUtil.getCurrentRequest();
			if(null != currentRequest) {
				siteId = (String) currentRequest.getAttribute(SITE_ID);
			}
			if (BBBUtility.isEmpty(siteId)){
				siteId = SiteContextManager.getCurrentSiteId();
			}
			
			Object value = callSupergetPropertyValue(pItem, pValue);
			if (value != null && null!= siteId && siteId.contains(TBSConstants.TBS_PREFIX)) {
				return String.valueOf(value).replace(BBBCoreConstants.CONTEXT_STORE, currentRequest.getContextPath());
			}
			
		return value;
	}

	public Object callSupergetPropertyValue(final RepositoryItemImpl pItem, final Object pValue) {
		return super.getPropertyValue(pItem, pValue);
	}
}
