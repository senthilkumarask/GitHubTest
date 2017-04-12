package com.bbb.userprofiling;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.crypto.EncryptionPropertyDescriptor;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

public class AutoLoginPropertyDescriptor extends GSAPropertyDescriptor {

	/**
	 * The type name property.
	 */
	private static final String TYPE_NAME = "autoLogin";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static {
		RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME,
				EncryptionPropertyDescriptor.class);
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
		 	DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
			String contextPath = pRequest.getContextPath();
			final String originOfTraffic = BBBUtility.getOriginOfTraffic();
			if(isLoggingDebug()) {
				logDebug("Inside AutoLoginPropertyDescriptor.getPropertyValue : Origin Of Traffic is : " +originOfTraffic);
			}
			if (!contextPath.equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS)) {
				return super.getPropertyValue(pItem, pValue);
			}
		return false;
	}
}
