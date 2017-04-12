/**
 * 
 */
package com.bbb.account.order.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class TrackingInfoDroplet extends BBBDynamoServlet {
	private static final String ORDEROUTPUT = "orderOutput";
	private static final String CARRIER_URL = "carrierURL";
	private BBBCatalogTools mCatalogTools;
	private String mShippingCarriers;

	/**
	 * Method will get the Shipping carrierUrl
	 * 
	 */
	@Override
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {

		// Code to get Shipping carrier details
		Map<String, String> carrierURL = null;
		try {
			carrierURL = getCatalogTools().getConfigValueByconfigType(getShippingCarriers());
			if (carrierURL != null) {
				pReq.setParameter(CARRIER_URL, carrierURL);
			}
		} catch (BBBSystemException e) {
			
				logError(e);
			
		} catch (BBBBusinessException e) {
			
				logError(e);
			
		}
		pReq.serviceLocalParameter(ORDEROUTPUT, pReq, pRes);
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the shippingCarriers
	 */
	public String getShippingCarriers() {
		return mShippingCarriers;
	}

	/**
	 * @param pShippingCarriers
	 *            the shippingCarriers to set
	 */
	public void setShippingCarriers(String pShippingCarriers) {
		mShippingCarriers = pShippingCarriers;
	}

}
