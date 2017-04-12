package com.bbb.account.vo.order;

import java.util.Date;
import java.util.List;


//import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.framework.integration.ServiceResponseBase;



//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 23-November-2011
//--------------------------------------------------------------------------------

@SuppressWarnings("rawtypes")
public class OrderHistoryResVO extends ServiceResponseBase implements Comparable<OrderHistoryResVO> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mOrderNumber;
	private String mOnlineOrderNumber;
	private String mBopusOrderNumber;
	private Date mOrderDate;
	private String mOrderStatus;
	private String mShipmentStatus;
	private String mShipmentDesciption;
	private int mOrderType= BBBAccountConstants.ORDER_TYPE_LEGACY;
	private List<ShipmentTrackingInfoVO> mShipment;
	
	
	@Override
	public int compareTo(OrderHistoryResVO pO1) {
		if(pO1.getOrderDate()==null || this.getOrderDate()==null){
				return -1;
		}else{
				return pO1.getOrderDate().compareTo(this.getOrderDate());
		}
	}	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
			OrderHistoryResVO pO1 = (OrderHistoryResVO) obj;
		if(pO1.getOrderDate()==null || this.getOrderDate()==null){
				return false;
		}else{
				return (pO1.getOrderDate().equals(this.getOrderDate()));
		}
	}	
	
	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return mOrderNumber;
	}
	/**
	 * @param pOrderNumber the orderNumber to set
	 */
	public void setOrderNumber(String pOrderNumber) {
		mOrderNumber = pOrderNumber;
	}
	/**
	 * @return the mOnlineOrderNumber
	 */
	public String getOnlineOrderNumber() {
		return mOnlineOrderNumber;
	}
	/**
	 * @param mOnlineOrderNumber the mOnlineOrderNumber to set
	 */
	public void setOnlineOrderNumber(String onlineOrderNumber) {
		mOnlineOrderNumber = onlineOrderNumber;
	}
	/**
	 * @return the mBopusOrderNumber
	 */
	public String getBopusOrderNumber() {
		return mBopusOrderNumber;
	}
	/**
	 * @param mBopusOrderNumber the mBopusOrderNumber to set
	 */
	public void setBopusOrderNumber(String bopusOrderNumber) {
		mBopusOrderNumber = bopusOrderNumber;
	}
	/**
	 * @return the orderedDate
	 */
	public final Date getOrderDate() {
		if(mOrderDate==null){ 
			return null;
		}
		return new Date(mOrderDate.getTime());
	}
	/**
	 * @param pOrderedDate the orderedDate to set
	 */
	public void setOrderDate(final Date pOrderDate) {
		if(pOrderDate!=null){
			mOrderDate = new Date(pOrderDate.getTime());
		}else{
			mOrderDate = null;
		}
	}
	/**
	 * @return the orderStatus
	 */
	public String getOrderStatus() {
		return mOrderStatus;
	}
	/**
	 * @param pOrderStatus the orderStatus to set
	 */
	public void setOrderStatus(String pOrderStatus) {
		mOrderStatus = pOrderStatus;
	}
	/**
	 * @return the shipmentStatus
	 */
	public String getShipmentStatus() {
		return mShipmentStatus;
	}
	/**
	 * @param pShipmentStatus the shipmentStatus to set
	 */
	public void setShipmentStatus(String pShipmentStatus) {
		mShipmentStatus = pShipmentStatus;
	}
	/**
	 * @return the shipmentDesciption
	 */
	public String getShipmentDesciption() {
		return mShipmentDesciption;
	}
	/**
	 * @param pShipmentDesciption the shipmentDesciption to set
	 */
	public void setShipmentDesciption(String pShipmentDesciption) {
		mShipmentDesciption = pShipmentDesciption;
	}
	/**
	 * @return the trackingInfo
	 */
	public List<ShipmentTrackingInfoVO> getShipment() {
		return mShipment;
	}
	/**
	 * @param pTrackingInfo the trackingInfo to set
	 */
	public void setShipment(List<ShipmentTrackingInfoVO> pShipment) {
		mShipment = pShipment;
	}

	/**
	 * @return the orderType
	 */
	public int getOrderType() {
		return mOrderType;
	}

	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(int orderType) {
		mOrderType = orderType;
	}	
	
	

}
