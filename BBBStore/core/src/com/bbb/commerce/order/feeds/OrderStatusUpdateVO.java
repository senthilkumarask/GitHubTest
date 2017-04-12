package com.bbb.commerce.order.feeds;

import java.io.Serializable;
import java.util.List;


public class OrderStatusUpdateVO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String orderNumber;
	private String orderState;
	private String stateDescription;
	private Object modificationDate;
	private List<ShippingGroupTypeVO> shippingGroupTypeVOList;
	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the orderState
	 */
	public String getOrderState() {
		return orderState;
	}
	/**
	 * @param orderState the orderState to set
	 */
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	/**
	 * @return the stateDescription
	 */
	public String getStateDescription() {
		return stateDescription;
	}
	/**
	 * @param stateDescription the stateDescription to set
	 */
	public void setStateDescription(String stateDescription) {
		this.stateDescription = stateDescription;
	}
	/**
	 * @return the modificationDate
	 */
	public Object getModificationDate() {
		return modificationDate;
	}
	/**
	 * @param modificationDate the modificationDate to set
	 */
	public void setModificationDate(Object modificationDate) {
		this.modificationDate = modificationDate;
	}
	/**
	 * @return the shippingGroupTypeVOList
	 */
	public List<ShippingGroupTypeVO> getShippingGroupTypeVOList() {
		return shippingGroupTypeVOList;
	}
	/**
	 * @param shippingGroupTypeVOList the shippingGroupTypeVOList to set
	 */
	public void setShippingGroupTypeVOList(
			List<ShippingGroupTypeVO> shippingGroupTypeVOList) {
		this.shippingGroupTypeVOList = shippingGroupTypeVOList;
	}
	
	

	
	
	
}
