package com.bbb.autowaiveshippingservice;


import java.math.BigInteger;
import java.util.List;


public class AutoWaiveShippingInfoRequestOrderVO extends com.bbb.framework.integration.ServiceRequestBase {


	

	/**
	 * 
	 */
	private static final long serialVersionUID = -362438685252989282L;

	/**
	* property: serviceName to autoWaiveShipping operation for getting Auto waive shipping status
	* 
	*/
	private String serviceName = "autoWaiveShippingInfo";
	
	private AutoWaiveShippingInfoRequestOrderHeaderVO header;
	private BigInteger orderId;
	private String storeNum;
	private List<AutoWaiveShippingInfoRequestOrderLineItemVO> orderLineItems;
	
	
	
	
	
	public AutoWaiveShippingInfoRequestOrderHeaderVO getHeader() {
		return header;
	}


	public void setHeader(AutoWaiveShippingInfoRequestOrderHeaderVO header) {
		this.header = header;
	}


	public BigInteger getOrderId() {
		return orderId;
	}


	public void setOrderId(BigInteger orderId) {
		this.orderId = orderId;
	}


	public String getStoreNum() {
		return storeNum;
	}


	public void setStoreNum(String storeNum) {
		this.storeNum = storeNum;
	}


	public List<AutoWaiveShippingInfoRequestOrderLineItemVO> getOrderLineItems() {
		return orderLineItems;
	}


	public void setOrderLineItems(
			List<AutoWaiveShippingInfoRequestOrderLineItemVO> orderLineItems) {
		this.orderLineItems = orderLineItems;
	}


	@Override
	public String toString() {
	return "AutoWaiveShippingInfoRequestVO [serviceName=" + this.serviceName;
	}
	
	
	/*
	* (non-Javadoc)
	* 
	* @see com.bbb.framework.integration.ServiceRequestBase#getServiceName()
	*/
	@Override
	public String getServiceName() {
	return this.serviceName;
	}
	
	/**
	* @param serviceName
	*/
	public void setServiceName(final String serviceName) {
	this.serviceName = serviceName;
	}

}
