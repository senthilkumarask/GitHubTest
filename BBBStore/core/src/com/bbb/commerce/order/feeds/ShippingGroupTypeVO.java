package com.bbb.commerce.order.feeds;

import java.io.Serializable;
import java.util.List;

public class ShippingGroupTypeVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String shipmentGroupId;
	private String shipmentState;
	private String shipmentDescription;
	
	private List<ShipmentTrackingVO> shipmentTrackingVO;
	private List<OrderLineItemVO> orderLineItemVO;
	/**
	 * @return the orderLineItemVO
	 */
	public List<OrderLineItemVO> getOrderLineItemVO() {
		return orderLineItemVO;
	}
	/**
	 * @param pOrderLineItemVO the orderLineItemVO to set
	 */
	public void setOrderLineItemVO(List<OrderLineItemVO> pOrderLineItemVO) {
		orderLineItemVO = pOrderLineItemVO;
	}
	/**
	 * @return the shipmentGroupId
	 */
	public String getShipmentGroupId() {
		return shipmentGroupId;
	}
	/**
	 * @param shipmentGroupId the shipmentGroupId to set
	 */
	public void setShipmentGroupId(String shipmentGroupId) {
		this.shipmentGroupId = shipmentGroupId;
	}
	/**
	 * @return the shipmentState
	 */
	public String getShipmentState() {
		return shipmentState;
	}
	/**
	 * @param shipmentState the shipmentState to set
	 */
	public void setShipmentState(String shipmentState) {
		this.shipmentState = shipmentState;
	}
	/**
	 * @return the shipmentDescription
	 */
	public String getShipmentDescription() {
		return shipmentDescription;
	}
	/**
	 * @param shipmentDescription the shipmentDescription to set
	 */
	public void setShipmentDescription(String shipmentDescription) {
		this.shipmentDescription = shipmentDescription;
	}

	/**
	 * @return the shipmentTrackingVO
	 */
	public List<ShipmentTrackingVO> getShipmentTrackingVO() {
		return shipmentTrackingVO;
	}
	/**
	 * @param shipmentTrackingVO the shipmentTrackingVO to set
	 */
	public void setShipmentTrackingVO(List<ShipmentTrackingVO> shipmentTrackingVO) {
		this.shipmentTrackingVO = shipmentTrackingVO;
	}

	@Override
	public String toString() {
		return "ShippingGroupTypeVO [shipmentGroupId=" + shipmentGroupId
				+ ", shipmentState=" + shipmentState + ", shipmentDescription="
				+ shipmentDescription + ", shipmentTrackingVO="
				+ shipmentTrackingVO + ", orderLineItemVO = " + orderLineItemVO +"]";
	}
	
	
	
}
