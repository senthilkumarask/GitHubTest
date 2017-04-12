package com.bbb.internationalshipping.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.internationalshipping.utils.InternationalShipTools;
import com.bbb.profile.session.BBBSessionBean;


/**
 * The Class InternationalShippingContextSetterDroplet.
 *
 * 
 */

public class InternationalShippingContextSetterDroplet extends BBBDynamoServlet {

	/** The international shipping tools. */
	InternationalShipTools internationalShippingTools;
	
	/** The session bean. */
	BBBSessionBean sessionBean;

	/**
	 * Gets the session bean.
	 *
	 * @return the session bean
	 */
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}


	/**
	 * Sets the session bean.
	 *
	 * @param sessionBean the new session bean
	 */
	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}


	/**
	 * Gets the international shipping tools.
	 *
	 * @return the international shipping tools
	 */
	public InternationalShipTools getInternationalShippingTools() {
		return internationalShippingTools;
	}


	/**
	 * Sets the international shipping tools.
	 *
	 * @param internationalShippingTools the new international shipping tools
	 */
	public void setInternationalShippingTools(
			InternationalShipTools internationalShippingTools) {
		this.internationalShippingTools = internationalShippingTools;
	}


	

	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String countryCode=null;
		logDebug("Service method InternationalShippingContextSetterDroplet start ");
		countryCode= (String) getSessionBean().getValues().get("defaultUserCountryCode");
		logDebug(" country code from Session "+countryCode);
		pRequest.setParameter("countryCode", countryCode);
		pRequest.serviceParameter("output", pRequest, pResponse);
		}
}
