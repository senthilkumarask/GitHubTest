package com.bbb.commerce.order.feeds;

import java.io.Serializable;
import java.util.List;

public class OrderLineItemVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String mItemId;
	private String mItemStatus;
	private String mItemStatusDesc;
	private long mTotalItemQty;
	
	private List<ShipmentTrackingVO> mItemTrackingVO;

	/**
	 * @return the itemId
	 */
	public String getItemId() {
		return mItemId;
	}

	/**
	 * @return the itemStatus
	 */
	public String getItemStatus() {
		return mItemStatus;
	}

	/**
	 * @return the itemStatusDesc
	 */
	public String getItemStatusDesc() {
		return mItemStatusDesc;
	}

	/**
	 * @return the totalItemQty
	 */
	public long getTotalItemQty() {
		return mTotalItemQty;
	}

	/**
	 * @return the itemTrackingVO
	 */
	public List<ShipmentTrackingVO> getItemTrackingVO() {
		return mItemTrackingVO;
	}

	/**
	 * @param pItemId the itemId to set
	 */
	public void setItemId(String pItemId) {
		mItemId = pItemId;
	}

	/**
	 * @param pItemStatus the itemStatus to set
	 */
	public void setItemStatus(String pItemStatus) {
		mItemStatus = pItemStatus;
	}

	/**
	 * @param pItemStatusDesc the itemStatusDesc to set
	 */
	public void setItemStatusDesc(String pItemStatusDesc) {
		mItemStatusDesc = pItemStatusDesc;
	}

	/**
	 * @param pTotalItemQty the totalItemQty to set
	 */
	public void setTotalItemQty(long pTotalItemQty) {
		mTotalItemQty = pTotalItemQty;
	}

	/**
	 * @param pItemTrackingVO the itemTrackingVO to set
	 */
	public void setItemTrackingVO(List<ShipmentTrackingVO> pItemTrackingVO) {
		mItemTrackingVO = pItemTrackingVO;
	}
	
	public String toString() {
		return "OrderLineItemVO [ItemId =" + mItemId + ", itemStatus =" + mItemStatus + ", ItemStatusDesc="
				+ mItemStatusDesc + ", TotalItemQty=" + mTotalItemQty + "]";
	}

}
