package com.bbb.internationalshipping.fulfillment.oms;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.internationalshipping.vo.orderconfirmation.BBBInternationalShippingOrderConfReq;
import com.fiftyone.services.ws.merchantapi.v1_0.OrderConfirmation;
import com.fiftyone.services.ws.merchantapi.v1_0.OrderConfirmationDocument;
import com.fiftyone.services.ws.merchantapi.v1_0.OrderDetails;


/**
 * The class is the marshaller class which takes the 
 * International Shipping Order Confirmation requestVo and
 * creates a XML request for the Webservice call. The class will require a
 * request VO object which will contain the request parameters for the XML
 * request
 * 
 * 
 */
public class InternationalOrderConfirmationMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The method is extension of the RequestMarshaller service which will take
	 * the request Vo object and create a XML Object for the webservice
	 * 
	 * @param ServiceRequestIF
	 *            the validate International Order Confirmation pRreqVO vo
	 * 
	 */
	@Override
	public XmlObject buildRequest(final ServiceRequestIF pReqVo) throws BBBBusinessException, BBBSystemException {

		logDebug("Entry buildRequest of InternationalOrderConfirmationMarshaller with ServiceRequestIF object:" + pReqVo.getServiceName());
		BBBPerformanceMonitor.start(BBBPerformanceConstants.INTERNATIONAL_ORDER_CONFIRMATION_MARSHALLER);

		OrderConfirmationDocument orderConfDoc = null;

		try {
			orderConfDoc = OrderConfirmationDocument.Factory.newInstance();
			final OrderConfirmation orderConfirmation = orderConfDoc.addNewOrderConfirmation();
			
			final BBBInternationalShippingOrderConfReq requestVo = (BBBInternationalShippingOrderConfReq)pReqVo;
			
			OrderDetails orderDetails = null;
			orderDetails = orderConfirmation.addNewOrder();
			orderDetails.setOrderId(requestVo.getOrderId());
			orderDetails.setMerchantOrderId(requestVo.getMerchantOrderId());
			
			orderConfirmation.setOrder(orderDetails);
			orderConfDoc.setOrderConfirmation(orderConfirmation);
			
		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.INTERNATIONAL_ORDER_CONFIRMATION_MARSHALLER);
		}
		
		logDebug("Exit buildRequest of InternationalOrderConfirmationMarshaller with XmlObject object:");
		return orderConfDoc;

	}
}
