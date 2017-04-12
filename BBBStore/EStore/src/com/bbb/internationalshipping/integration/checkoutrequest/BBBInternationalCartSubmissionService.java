package com.bbb.internationalshipping.integration.checkoutrequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.apache.soap.encoding.soapenc.Base64;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 *This is a service for transporting the request xml having cart details to International checkout service.
 *This service makes a synchronous call to International checkout service to submit cart details and fetch the response from it.
 */
public class BBBInternationalCartSubmissionService extends BBBGenericService {

	/**
	 * This variable is used to point to BBBCatalogTools.
	 */
	private BBBCatalogTools catalogTools;
	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	/**
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 * This variable is a reference to the HttpCallInvoker.
	 */
	private HTTPCallInvoker httpCallInvoker;


	/**
	 * @return the httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}


	/**
	 * @param httpCallInvoker the httpCallInvoker to set
	 */
	public void setHttpCallInvoker(final HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}


	/**
	 * This method is used to make the Https call to International Checkout Service
	 * with the requestXml as input and serviceUrl and UserId/pwd configured in BCC.
	 * Output will be the response xml with the required parameters received from the call.
	 * 
	 * @param requestXml
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String submitInternationalCart(final String requestXml) throws BBBSystemException, BBBBusinessException {

		logDebug("Entering class: BBBInternationalCartSubmissionService,  "
				+ "method : submitInternationalCart");
		BBBPerformanceMonitor
				.start(BBBPerformanceConstants.INTERNATIONAL_CHECKOUT_SERVICE
						+ " submitInternationalCart");
		
		final StringBuilder responseXML = new StringBuilder();
		
			final List<String> serviceUrlList = this.catalogTools
					.getAllValuesForKey(
							BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
							BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT);

			final List<String> serviceUserList = this.catalogTools
					.getAllValuesForKey(
							BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
							BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID);

			final List<String> servicePwdList = this.catalogTools
					.getAllValuesForKey(
							BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
							BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD);

			if (BBBUtility.isListEmpty(serviceUrlList)
					&& BBBUtility.isListEmpty(serviceUserList)
					&& BBBUtility.isListEmpty(servicePwdList)) {
				logDebug("Configure Keys for International Checkouts are invalid.");
				throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1008, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1008);

			} else {
					this.submitService(requestXml, responseXML,
							 serviceUrlList,
							serviceUserList, servicePwdList);
			}
			
		BBBPerformanceMonitor
			.end(BBBPerformanceConstants.INTERNATIONAL_CHECKOUT_SERVICE
					+ " submitInternationalCart");
		
		logDebug("Exiting class: BBBInternationalCartSubmissionService,  "
				+ "method : submitInternationalCart");
		return responseXML.toString();
	}


	/**
	 * @param requestXml
	 * @param responseXML
	 * @param serviceUrlList
	 * @param serviceUserList
	 * @param servicePwdList
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void submitService(final String requestXml, 
			final StringBuilder responseXML, final List<String> serviceUrlList,
			final List<String> serviceUserList,
			final List<String> servicePwdList) throws BBBBusinessException, BBBSystemException {
		Map<String,String> headerParams = new HashMap<String, String>();
		try {
			final String envoyServiceUrl = serviceUrlList.get(0);
			final String authStr = serviceUserList.get(0)
					+ BBBCoreConstants.COLON + servicePwdList.get(0);
			final String authEncoded = Base64.encode(authStr.getBytes());

			logDebug("Creating connection to URL: " + envoyServiceUrl);
			logDebug("HttpCallInvoker Envoy Request: " + requestXml);
			headerParams.put(BBBCoreConstants.HTTP_INVKR_AUTH, "Basic " + authEncoded);
			headerParams.put(BBBCoreConstants.HTTP_INVKR_CNT_TYPE, "text/xml");
			final String response = invokeIntlCartSubmission(requestXml, headerParams, envoyServiceUrl);
			if (response != null && !response.isEmpty()) {
				responseXML.append(response);
			} else {
				logError("ERROR: Envoy Response received is NULL/BLANK");
			}
			logDebug("HttpCallInvoker Envoy Response: " + responseXML);
		} catch (final BBBSystemException e) {
			logError(
					"BBBSystemException during execution of HttpCallInvoker in submitService",
					e);
			throw new BBBSystemException(
					"BBBSystemException during execution of HttpCallInvoker in submitService");
		} 
	}


	protected String invokeIntlCartSubmission(final String requestXml, Map<String, String> headerParams,
			final String envoyServiceUrl) throws BBBBusinessException, BBBSystemException {
		return getHttpCallInvoker().invokeIntlCartSubmission(envoyServiceUrl, requestXml, headerParams);
	}
	
}
