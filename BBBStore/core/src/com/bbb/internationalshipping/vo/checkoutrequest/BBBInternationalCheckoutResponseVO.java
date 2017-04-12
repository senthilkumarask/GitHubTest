package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;
import java.util.List;



// TODO: Auto-generated Javadoc
/**
 *  This VO holds the required parameters from the FiftyOne service response which includes full Envoy URL , E4X OrderId and error messages. 
 */
public class BBBInternationalCheckoutResponseVO  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The full envoy url extracted from service response. */
	private String fullEnvoyUrl;

	/** The fiftyone orderId extracted from service response. */
	private String internationalOrderId;

	/** The envoy url. */
	private String envoyUrl;

	/** The error message. */
	private List<IntlShippingErrorMessage> errorMessage ;


	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public List<IntlShippingErrorMessage> getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the error message.
	 *
	 * @param errorMessage the new error message
	 */
	public void setErrorMessage(final List<IntlShippingErrorMessage> errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Gets the full envoy url.
	 *
	 * @return the full envoy url
	 */
	public String getFullEnvoyUrl() {
		return fullEnvoyUrl;
	}

	/**
	 * Sets the full envoy url.
	 *
	 * @param fullEnvoyUrl the new full envoy url
	 */
	public void setFullEnvoyUrl(final String fullEnvoyUrl) {
		this.fullEnvoyUrl = fullEnvoyUrl;
	}

	/**
	 * Gets the international order id.
	 *
	 * @return the international order id
	 */
	public String getInternationalOrderId() {
		return internationalOrderId;
	}

	/**
	 * Sets the international order id.
	 *
	 * @param internationalOrderId the new international order id
	 */
	public void setInternationalOrderId(final String internationalOrderId) {
		this.internationalOrderId = internationalOrderId;
	}

	/**
	 * Gets the envoy url.
	 *
	 * @return the envoy url
	 */
	public String getEnvoyUrl() {
		return envoyUrl;
	}

	/**
	 * Sets the envoy url.
	 *
	 * @param envoyUrl the new envoy url
	 */
	public void setEnvoyUrl(final String envoyUrl) {
		this.envoyUrl = envoyUrl;
	}





}
