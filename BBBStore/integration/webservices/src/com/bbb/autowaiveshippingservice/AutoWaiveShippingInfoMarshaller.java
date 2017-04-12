package com.bbb.autowaiveshippingservice;


import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bedbath.www.autowaiveshipping.sharedresources.schemas.orderinput.OrderDocument;
import com.bedbath.www.autowaiveshipping.sharedresources.schemas.orderinput.OrderDocument.Order;
import com.bedbath.www.autowaiveshipping.sharedresources.schemas.orderinput.OrderDocument.Order.Header;
import com.bedbath.www.autowaiveshipping.sharedresources.schemas.orderinput.OrderDocument.Order.OrderLineItems;


public class AutoWaiveShippingInfoMarshaller extends com.bbb.framework.webservices.RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5076260861261543421L;

	
	
	/*
	* (non-Javadoc)
	* 
	* @see
	* com.bbb.framework.webservices.RequestMarshaller#buildRequest(com.bbb.
	* framework.integration.ServiceRequestIF)
	*/
	public XmlObject buildRequest(final ServiceRequestIF pReqVo)
		throws BBBBusinessException, BBBSystemException {
	
	logDebug("Entry buildRequest of AutoWaiveShippingInfoMarshaller with ServiceRequestIF object:"
				+ pReqVo.getServiceName());
	
	
	BBBPerformanceMonitor.start("AutoWaiveShippingInfo-buildRequest");
	
	try {
	
		final AutoWaiveShippingInfoRequestOrderVO requestVo=(AutoWaiveShippingInfoRequestOrderVO)pReqVo;
		final OrderDocument orderDocument = OrderDocument.Factory
				.newInstance();
	
		final Order order = Order.Factory
				.newInstance();
		
		Header header=order.addNewHeader();
		header.setUserToken(requestVo.getHeader().getUserToken());
		if(null!=requestVo.getHeader().getApplicationName()){
			header.setApplicationName(requestVo.getHeader().getApplicationName());
		}
		if(null!=requestVo.getHeader().getHostName()){
			header.setHostname(requestVo.getHeader().getHostName());
		}
		order.setHeader(header);
		
		if(requestVo.getOrderLineItems()!=null&&requestVo.getOrderLineItems().size()!=0){
			OrderLineItems[] orderLineItemsArray=new OrderLineItems[requestVo.getOrderLineItems().size()];
			for(int i=0;i<requestVo.getOrderLineItems().size();i++){
				AutoWaiveShippingInfoRequestOrderLineItemVO orderLineItem=requestVo.getOrderLineItems().get(i);
				OrderLineItems orderLineItems=	order.addNewOrderLineItems();
				if(null!=orderLineItem.getOrderLineId()){
				orderLineItems.setOrderLineID(orderLineItem.getOrderLineId());
				}
				if(null!=orderLineItem.getSku()){
				orderLineItems.setSKU(orderLineItem.getSku());
				}
				if(null!=orderLineItem.getOrderQty()){
				orderLineItems.setOrderQty(orderLineItem.getOrderQty());
				}
				if(null!=orderLineItem.getRegistryNum()){
				orderLineItems.setRegistryNum(orderLineItem.getRegistryNum());
				}
				if(null!=orderLineItem.getStoreOnHandQty()){
				orderLineItems.setStoreOnHandQty(orderLineItem.getStoreOnHandQty());
				}
				if(null!=orderLineItem.getSkuClassification()){
				orderLineItems.setSKUClassification(orderLineItem.getSkuClassification());
				}
				orderLineItemsArray[i]=orderLineItems;
			}
			order.setOrderLineItemsArray(orderLineItemsArray);
		}
		order.setOrderID(requestVo.getOrderId());
		order.setStoreNum(requestVo.getStoreNum());
		
		orderDocument.setOrder(order);
	
		logDebug("Exit buildRequest of AutoWaiveShippingInfoMarshaller ");
		
		return orderDocument;
	} finally {
		BBBPerformanceMonitor.end("AutoWaiveShippingInfoMarshaller-buildRequest");
	}
	
	}
}
