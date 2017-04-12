package com.bbb.autowaiveshippingservice;


import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bedbath.www.autowaiveshipping.sharedresources.schemas.orderoutput_xsd.OrderDocument;
import com.bedbath.www.autowaiveshipping.sharedresources.schemas.orderoutput_xsd.OrderDocument.Order;
import com.bedbath.www.autowaiveshipping.sharedresources.schemas.orderoutput_xsd.OrderDocument.Order.OrderLineItems;

public class AutoWaiveShippingInfoUnMarshaller extends com.bbb.framework.webservices.ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1457768868426134934L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.framework.webservices.ResponseUnMarshaller#processResponse(org
	 * .apache.xmlbeans.XmlObject)
	 */
	public ServiceResponseIF processResponse(XmlObject responseDocument) throws BBBSystemException {
		
		vlogDebug("Inside AutoWaiveShippingInfoUnMarshaller:processResponse. Response Document is ");
		BBBPerformanceMonitor.start("AutoWaiveShippingInfoUnMarshaller-processResponse");

		AutoWaiveShippingInfoResponseOrderVO autoWaiveShippingInfoResponseVo = new AutoWaiveShippingInfoResponseOrderVO();
	    
		if (responseDocument != null) {
			logDebug("Response Document is " + responseDocument.toString());
			OrderDocument orderDocument = (OrderDocument) responseDocument;
			Order orderRes = orderDocument.getOrder();
			autoWaiveShippingInfoResponseVo.setOrderId(orderRes.getOrderID().toString());
			autoWaiveShippingInfoResponseVo.setOrderClassification(orderRes.getOrderClassification());
			autoWaiveShippingInfoResponseVo.setOrderWaiveShipFlag(orderRes.getOrderWaiveShipFlag());
			OrderLineItems[] orderLineItems = (OrderLineItems[]) orderRes.getOrderLineItemsArray();
			List<AutoWaiveShippingInfoResponseOrderLineItemVO> orderLineItemsVO = new ArrayList<AutoWaiveShippingInfoResponseOrderLineItemVO>();
			AutoWaiveShippingInfoResponseOrderLineItemVO orderLineItemVO;
			for (OrderLineItems lOrderLineItem : orderLineItems) {
				orderLineItemVO = new AutoWaiveShippingInfoResponseOrderLineItemVO();
				copyProperties(lOrderLineItem, orderLineItemVO);
				orderLineItemsVO.add(orderLineItemVO);
			}

			autoWaiveShippingInfoResponseVo.setOrderLineItems(orderLineItemsVO);
		}else{
			autoWaiveShippingInfoResponseVo.setWebServiceError(true);
		}
		BBBPerformanceMonitor.end("AutoWaiveShippingInfoUnMarshaller-processResponse");

		return autoWaiveShippingInfoResponseVo;
	}


	private void copyProperties(OrderLineItems orderLineItem,
			AutoWaiveShippingInfoResponseOrderLineItemVO orderLineItemVO) {
		orderLineItemVO.setOrderLineId(orderLineItem.getOrderLineID());
		if(orderLineItem.getOnOrderQty() != null){
			orderLineItemVO.setOnOrderQty(orderLineItem.getOnOrderQty().intValue());
		}
		if(orderLineItem.getOnHandQty() != null){
			orderLineItemVO.setOnHandQty(orderLineItem.getOnHandQty().intValue());
		}
		if(orderLineItem.getSKU() != null){
			orderLineItemVO.setSku(orderLineItem.getSKU().intValue());
		}
		if(orderLineItem.getSKUClassification() != null){
			orderLineItemVO.setSkuClassification(orderLineItem.getSKUClassification());
		}
		orderLineItemVO.setStoreSkuStatusFlag(orderLineItem.getStoreSKUStatusFlag());
		if(orderLineItem.getPastStoreSalesQty() != null){
			orderLineItemVO.setPastStoreSalesQty(orderLineItem.getPastStoreSalesQty().intValue());
		}
		orderLineItemVO.setWaiveShipFlag(orderLineItem.getWaiveShipFlag());
		orderLineItemVO.setOnHandStatusFlag(orderLineItem.getOnHandStatusFlag());
	}
}
