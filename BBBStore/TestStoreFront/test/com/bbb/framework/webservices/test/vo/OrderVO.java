package com.bbb.framework.webservices.test.vo;

import java.util.List;

/**
 * 
 * @author manohar
 *
 */
public class OrderVO {
	
	String orderNumber;
	String orderDate;
	String status;
	List<ShipmentVO> shipments;
	
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<ShipmentVO> getShipments() {
		return shipments;
	}
	public void setShipments(List<ShipmentVO> shipments) {
		this.shipments = shipments;
	}
	
	

}
