package com.bbb.framework.webservices.test.vo;

import com.bbb.framework.integration.ServiceRequestBase;

/**
 * 
 * @author manohar
 *
 */
public class GetOrderHistoryReqVO extends ServiceRequestBase{

	private OrderHistoryVO orderHistory;
	private String serviceName;

	public OrderHistoryVO getOrderHistory() {
		return orderHistory;
	}

	public void setOrderHistory(OrderHistoryVO orderHistory) {
		this.orderHistory = orderHistory;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
}
