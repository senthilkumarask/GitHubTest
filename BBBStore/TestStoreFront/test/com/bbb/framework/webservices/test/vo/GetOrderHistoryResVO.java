package com.bbb.framework.webservices.test.vo;

import java.util.ArrayList;
import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;

/**
 * 
 * @author manohar
 *
 */
public class GetOrderHistoryResVO extends ServiceResponseBase{
	
	List<OrderVO> orderList;

	public List<OrderVO> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<OrderVO> orderList) {
		this.orderList = orderList;
	}
	
	
}
