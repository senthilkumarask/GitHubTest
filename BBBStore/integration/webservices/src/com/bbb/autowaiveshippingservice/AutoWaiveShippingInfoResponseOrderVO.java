package com.bbb.autowaiveshippingservice;


import java.util.List;

public class AutoWaiveShippingInfoResponseOrderVO extends com.bbb.framework.integration.ServiceResponseBase {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2536833215001661446L;
	private String orderId;
	private String orderClassification;
	private String orderWaiveShipFlag;
	private List<AutoWaiveShippingInfoResponseOrderLineItemVO> orderLineItems;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderClassification() {
		return orderClassification;
	}
	public void setOrderClassification(String orderClassification) {
		this.orderClassification = orderClassification;
	}
	public String getOrderWaiveShipFlag() {
		return orderWaiveShipFlag;
	}
	public void setOrderWaiveShipFlag(String orderWaiveShipFlag) {
		this.orderWaiveShipFlag = orderWaiveShipFlag;
	}
	public List<AutoWaiveShippingInfoResponseOrderLineItemVO> getOrderLineItems() {
		return orderLineItems;
	}
	public void setOrderLineItems(
			List<AutoWaiveShippingInfoResponseOrderLineItemVO> orderLineItems) {
		this.orderLineItems = orderLineItems;
	}
	
	
	

}
