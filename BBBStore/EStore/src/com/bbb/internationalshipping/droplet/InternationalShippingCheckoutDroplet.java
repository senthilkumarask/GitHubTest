package com.bbb.internationalshipping.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.utils.InternationaShippingCheckoutHelper;
import com.bbb.logging.LogMessageFormatter;


/**
 * The Class InternationalShippingCheckoutDroplet.
 *
 * 
 */

public class InternationalShippingCheckoutDroplet extends BBBDynamoServlet {
	
	/** The helper. */
	InternationaShippingCheckoutHelper helper;
	
	

	/**
	 * Gets the helper.
	 *
	 * @return the helper
	 */
	public InternationaShippingCheckoutHelper getHelper() {
		return helper;
	}


	/**
	 * Sets the helper.
	 *
	 * @param helper the new helper
	 */
	public void setHelper(InternationaShippingCheckoutHelper helper) {
		this.helper = helper;
	}




	
	/** The Constant DISPLAY_COUNTRY_CURRENCY. */
	public static final String  DISPLAY_COUNTRY_CURRENCY="displayCountryCurrency";
	
	/** The Constant COUNTRY_CODE. */
	public static final String COUNTRY_CODE="countryCode";
	
	/** The Constant CURRENCY_CODE. */
	public static final String CURRENCY_CODE="currencyCode";
	
	/** The Constant ALL_CONTEXT_LIST. */
	public static final String ALL_CONTEXT_LIST="allContextList";
	
	/** The Constant ALL_CURRENCY_MAP. */
	public static final String ALL_CURRENCY_MAP="allCurrencyMap";

	




	/** The Constant OPARAM_OUTPUT. */
	public static final String OPARAM_OUTPUT = "output";
	
	/** The Constant OPARAM_COUNTRY. */
	public static final String OPARAM_COUNTRY = "country";
	
	/** The Constant OPARAM_ERROR. */
	public static final String OPARAM_ERROR = "error";
	
	/** The Constant OPARAM_EMPTY. */
	public static final String OPARAM_EMPTY = "empty";
	
	/** The Constant OUTPUT_ERROR_MSG. */
	public static final String OUTPUT_ERROR_MSG = "errorMsg";
	
	/** The Constant COUNTRY_NAME. */
	private static final String COUNTRY_NAME = "countryName";
	
	/** The Constant CURRENCY_NAME. */
	private static final String CURRENCY_NAME = "currencyName";





	/**
	 * This method displays country currency drop down.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		try {
			
			this.getHelper().setCheckoutParam(pRequest);
			
			
			pRequest.setParameter(COUNTRY_NAME, this.getHelper().getCountryName());
			pRequest.setParameter(CURRENCY_NAME, this.getHelper().getCurrencyName());
			pRequest.setParameter(COUNTRY_CODE, this.getHelper().getCountryCode());
			pRequest.setParameter(CURRENCY_CODE, this.getHelper().getCurrencyCode());
			if(this.getHelper().getCountryCode()==null || this.getHelper().getCountryCode().isEmpty())
			{
				pRequest.setParameter(DISPLAY_COUNTRY_CURRENCY, false);
			}
			else{
				pRequest.setParameter(DISPLAY_COUNTRY_CURRENCY, this.getHelper().getDisplayCountryCurrency());
			}
			pRequest.setParameter(ALL_CONTEXT_LIST, this.getHelper().getContextList());
			pRequest.setParameter(ALL_CURRENCY_MAP, this.getHelper().getCurrencyMap());
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
		} catch (BBBSystemException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			logError(LogMessageFormatter.formatMessage(pRequest,
					"Business Exception from service of InternationalShippingCheckoutDroplet "), e);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			logError(LogMessageFormatter.formatMessage(pRequest,
					"Business Exception from service of InternationalShippingCheckoutDroplet "), e);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}

	}
	

}
