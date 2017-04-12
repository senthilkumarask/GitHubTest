package com.bbb.rest.account.order;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.account.vo.order.OrderHistory2ResVO;

/**
 * REST API output VO for GetAllOrders
 * 
 * @author akhilesh
 * @version 1.0
 */

public class OrderHistoryDropletResultVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numOrders;
	private List<OrderHistory2ResVO> orderList;
	private Map<String, String> carrierURL;
	
	
	public int getNumOrders() {
		return numOrders;
	}
	public void setNumOrders(int numOrders) {
		this.numOrders = numOrders;
	}
	public List<OrderHistory2ResVO> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<OrderHistory2ResVO> orderList) {
		this.orderList = orderList;
	}
	public Map<String, String> getCarrierURL() {
		return carrierURL;
	}
	public void setCarrierURL(Map<String, String> carrierURL) {
		this.carrierURL = carrierURL;
	}
	
}
