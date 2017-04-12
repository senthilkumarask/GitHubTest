/**
 * 
 */
package com.bbb.commerce.checkout.tibco;

import atg.repository.RepositoryItem;

import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.framework.integration.ServiceRequestIF;

/**
 * @author alakra
 *
 */
public class SubmitOrderVO implements ServiceRequestIF {
	
	private static final long serialVersionUID = 6574647748708777512L;
	
	private Boolean mCacheEnabled = null;
	
	private String mServiceName = null;
	
	private BBBOrder mOrder = null;
	
	private RepositoryItem mProfile = null;

	/**
	 * @return the order
	 */
	public final BBBOrder getOrder() {
		return mOrder;
	}

	/**
	 * @param pOrder the order to set
	 */
	public final void setOrder(BBBOrder pOrder) {
		mOrder = pOrder;
	}

	/**
	 * @return the profile
	 */
	public final RepositoryItem getProfile() {
		return mProfile;
	}

	/**
	 * @param pProfile the profile to set
	 */
	public final void setProfile(RepositoryItem pProfile) {
		mProfile = pProfile;
	}
	
	/**
	 * @return the cacheEnabled
	 */
	public final Boolean isCacheEnabled() {
		return mCacheEnabled;
	}

	/**
	 * @param pCacheEnabled the cacheEnabled to set
	 */
	public final void setCacheEnabled(Boolean pCacheEnabled) {
		mCacheEnabled = pCacheEnabled;
	}

	/**
	 * @return the serviceName
	 */
	public final String getServiceName() {
		return mServiceName;
	}

	/**
	 * @param pServiceName the serviceName to set
	 */
	public final void setServiceName(String pServiceName) {
		mServiceName = pServiceName;
	}
}