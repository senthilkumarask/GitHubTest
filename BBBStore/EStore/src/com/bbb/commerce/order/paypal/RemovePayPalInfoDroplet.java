package com.bbb.commerce.order.paypal;


import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * This class helps remove PayPal Payment Group, paypal billing address and token from Order 
 * 
 * @author akhaju
 *
 */
public class RemovePayPalInfoDroplet extends DynamoServlet {
	private BBBOrderImpl order;
	private BBBPayPalServiceManager paypalServiceManager;
	private Profile profile;
	
	

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		return profile;
	}



	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}



	/**
	 * @return the order
	 */
	public BBBOrderImpl getOrder() {
		return order;
	}



	/**
	 * @param order the order to set
	 */
	public void setOrder(BBBOrderImpl order) {
		this.order = order;
	}



	/**
	 * @return the paypalServiceManager
	 */
	public BBBPayPalServiceManager getPaypalServiceManager() {
		return paypalServiceManager;
	}



	/**
	 * @param paypalServiceManager the paypalServiceManager to set
	 */
	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}



	/**
	 * This method helps remove PayPal Payment Group, paypal billing address and token from Order
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		logDebug("RemovePayPalInfoDroplet.service():: Start");
		try {
			getPaypalServiceManager().removePayPalPaymentGroup(getOrder(), getProfile());
		} catch (BBBSystemException e) {
			logError("RemovePayPalInfoDroplet.service() :: System Exception while the calling getPaypalServiceManager().removePayPalPaymentGroup()" + e);
		}

		logDebug("RemovePayPalInfoDroplet.service():: End");
	}
	
	
	/**
	 * Below method is the wrapper for the service method
	 * @throws ServletException
	 * @throws IOException
	 */
	public void removePayPalInfoDroplet() throws ServletException, IOException  {
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
	    BBBPerformanceMonitor.start("RemovePayPalInfoDroplet.removePayPalInfoDroplet() Start");
		logDebug("RemovePayPalInfoDroplet.removePayPalInfoDroplet()  - start");
		service(pRequest, pResponse);
		BBBPerformanceMonitor.end("RemovePayPalInfoDroplet.removePayPalInfoDroplet() Start");
		logDebug("RemovePayPalInfoDroplet.removePayPalInfoDroplet()  - Ends");
	}
	
}
	
