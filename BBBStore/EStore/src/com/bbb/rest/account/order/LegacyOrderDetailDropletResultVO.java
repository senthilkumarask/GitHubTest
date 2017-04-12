package com.bbb.rest.account.order;

import java.io.Serializable;
import java.util.Map;

import com.bbb.account.vo.order.OrderDetailInfoReturn;

public class LegacyOrderDetailDropletResultVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OrderDetailInfoReturn orderDetailInfoReturn;
	private Map<String, String> carrierURL; 
	
	public OrderDetailInfoReturn getOrderDetailInfoReturn() {
		return orderDetailInfoReturn;
	}
	public void setOrderDetailInfoReturn(OrderDetailInfoReturn orderDetailInfoReturn) {
		this.orderDetailInfoReturn = orderDetailInfoReturn;
	}
	public Map<String, String> getCarrierURL() {
		return carrierURL;
	}
	public void setCarrierURL(Map<String, String> carrierURL) {
		this.carrierURL = carrierURL;
	}
	
}
