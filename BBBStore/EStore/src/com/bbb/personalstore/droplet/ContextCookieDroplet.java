package com.bbb.personalstore.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.personalstore.manager.PersonalStoreManager;
import com.bbb.utils.BBBUtility;

/**
 * Extends BBBDynamoServlet and will be used for generating Cookie for the
 * context(Last Viewed item) for PersonalStore Page
 * 
 * @author rjai39
 * 
 */
public class ContextCookieDroplet extends BBBDynamoServlet {

	// Personal store Manager
	private PersonalStoreManager mPsManager;

	/**
	 * This method returns <code>PersonalStoreManager</code> contains name of
	 * the manager component to use and get the personal store and strategy
	 * details
	 * 
	 * @return the mPsManager in <code>PersonalStoreManager</code> format
	 */
	public PersonalStoreManager getPsManager() {
		return mPsManager;
	}

	/**
	 * This method sets the PersonalStoreManager to be used from component
	 * properties file and get the personal store and strategy details
	 * 
	 * @param mPsManager
	 *            the personal store manager to set
	 */
	public void setPsManager(final PersonalStoreManager mPsManager) {
		this.mPsManager = mPsManager;
	}

	/**
	 * Droplet to generate the last viewed and Last bought Cookie for personal
	 * store.
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		BBBPerformanceMonitor.start(ContextCookieDroplet.class.getName() + " : " + "service");
		logDebug("ContextCookieDroplet.service method starts");
		Cookie lBCookie = null;
		Cookie lVCookie = null;
		List<String> lVCookieValLst = null;

		// Get the Last Bought and Last Viewed Cookie
		final Cookie[] cookies = pRequest.getCookies();
		if (null != cookies) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(getPsManager().getLbCookieNme())) {
					logDebug("Last Bought Cookie Found in Cookies");
					lBCookie = cookies[i];
				}
				if (cookies[i].getName().equals(getPsManager().getLvCookieNme())) {
					logDebug("Last Viewed Cookie Found in Cookies");
					lVCookie = cookies[i];
				}
			}
		}

		final String productId = pRequest.getParameter(BBBCoreConstants.PRODUCTID);

		if (null != productId) {
			// generate the Last Viewed Cookie
			lVCookie = getPsManager().getLastViewedCookie(productId, lVCookie, lBCookie, pRequest, pResponse);
			if (null != lVCookie) {
				BBBUtility.addCookie(pResponse, lVCookie, true);
			} else {
				pRequest.setParameter(BBBCoreConstants.Is_ERROR, true);
			}
		}else{
			pRequest.setParameter(BBBCoreConstants.Is_ERROR, true);
		}

		logDebug("ContextCookieDroplet.service method ends");
		BBBPerformanceMonitor.end(ContextCookieDroplet.class.getName() + " : " + "service");
	}
}