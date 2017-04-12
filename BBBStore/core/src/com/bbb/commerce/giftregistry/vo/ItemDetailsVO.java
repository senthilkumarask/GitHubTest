package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;

public class ItemDetailsVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int purchasedQuantity;
	private long skuId;
	private String rowId;
	private int regItemOldQty;
	
	
	/**
	 * Getter for purchased Quantity.
	 * 
	 * @return purchasedQuantity
	 */
	public int getPurchasedQuantity() {
		return purchasedQuantity;
	}
	/**
	 * @param purchasedQuantity
	 */
	public void setPurchasedQuantity(int purchasedQuantity) {
		this.purchasedQuantity = purchasedQuantity;
	}
	/**
	 * @return rowId
	 */
	public String getRowId() {
		return rowId;
	}
	/**
	 * @param rowId
	 */
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	/**
	 * @return regItemOldQty
	 */
	public int getRegItemOldQty() {
		return regItemOldQty;
	}
	/**
	 * @param regItemOldQty
	 */
	public void setRegItemOldQty(int regItemOldQty) {
		this.regItemOldQty = regItemOldQty;
	}
	
    /**
     * Getter for skuId.
     * @return
     */
	public long getSkuId() {
		return skuId;
	}
	
	/**
	 * Setter for skuId.
	 * @param skuId
	 */
	public void setSkuId(long skuId) {
		this.skuId = skuId;
	}
	
}
