package com.bbb.internationalshipping.fulfillment.oms;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.internationalshipping.vo.orderconfirmation.BBBInternationalShippingOrderConfResponse;
import com.fiftyone.services.ws.merchantapi.v1_0.OrderConfirmationResponse;
import com.fiftyone.services.ws.merchantapi.v1_0.OrderConfirmationResponseDocument;


/**
 * This class contain methods used for unmarshalling 
 * the International Order Confirmation response.
 * 
 * 
 */
public class InternationalOrderConfirmationUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */

	@Override
	public ServiceResponseIF processResponse(final XmlObject pResponseDocument) throws BBBSystemException {

		logDebug("Entry processResponse of InternationalOrderConfirmationUnMarshaller with ServiceRequestIF object:");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.INTERNATIONAL_ORDER_CONFIRMATION_UNMARSHALLER);
		
		final BBBInternationalShippingOrderConfResponse orderConfResponse = new BBBInternationalShippingOrderConfResponse();
		
		final OrderConfirmationResponseDocument bbbDoAuthRes = (OrderConfirmationResponseDocument) pResponseDocument;

		final OrderConfirmationResponse response = bbbDoAuthRes.getOrderConfirmationResponse();

		boolean isConfirmed = false;
		
		if(null != response) {		
			isConfirmed = response.getConfirmed();
		}
		orderConfResponse.setConfirmed(isConfirmed);
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.INTERNATIONAL_ORDER_CONFIRMATION_UNMARSHALLER);
		
		logDebug("Exit processResponse of InternationalOrderConfirmationUnMarshaller with XmlObject object: " +orderConfResponse);
		return orderConfResponse;

	}

}