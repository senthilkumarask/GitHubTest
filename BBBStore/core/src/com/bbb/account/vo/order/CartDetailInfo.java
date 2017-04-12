package com.bbb.account.vo.order;

import java.io.Serializable;
import java.util.List;

/**
 * CartDetailInfo.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */


public class CartDetailInfo  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean isVDSInCart;

    private boolean isRegistryInCart;

    private boolean isSingleRegistryInCart;

    private boolean isBTSInCart;

    private boolean isGiftCertOnlyInCart;

    private boolean isGiftInCart;

    private boolean isItemStdFreeShipInCart;

    private boolean isItemExpedFreeShipInCart;

    private boolean isItemExprFreeShipInCart;

    private long uniqueRegistryNum;

    private double productSubtotalAmt;

    private double totalAmt;

    private String registrantName;

    private String coRegistrantName;

    private String combinedRegistrantNames;

    private List<CartItemDetailInfo> cartItemDetailList;
    
	/**
	 * @return the isVDSInCart
	 */
	public boolean isVDSInCart() {
		return isVDSInCart;		
	}

	/**
	 * @param isVDSInCart the isVDSInCart to set
	 */
	public void setVDSInCart(boolean isVDSInCart) {
		this.isVDSInCart = isVDSInCart;
	}

	/**
	 * @return the isRegistryInCart
	 */
	public boolean isRegistryInCart() {
		return isRegistryInCart;
	}

	/**
	 * @param isRegistryInCart the isRegistryInCart to set
	 */
	public void setRegistryInCart(boolean isRegistryInCart) {
		this.isRegistryInCart = isRegistryInCart;
	}

	/**
	 * @return the isSingleRegistryInCart
	 */
	public boolean isSingleRegistryInCart() {
		return isSingleRegistryInCart;
	}

	/**
	 * @param isSingleRegistryInCart the isSingleRegistryInCart to set
	 */
	public void setSingleRegistryInCart(boolean isSingleRegistryInCart) {
		this.isSingleRegistryInCart = isSingleRegistryInCart;
	}

	/**
	 * @return the isBTSInCart
	 */
	public boolean isBTSInCart() {
		return isBTSInCart;
	}

	/**
	 * @param isBTSInCart the isBTSInCart to set
	 */
	public void setBTSInCart(boolean isBTSInCart) {
		this.isBTSInCart = isBTSInCart;
	}

	/**
	 * @return the isGiftCertOnlyInCart
	 */
	public boolean isGiftCertOnlyInCart() {
		return isGiftCertOnlyInCart;
	}

	/**
	 * @param isGiftCertOnlyInCart the isGiftCertOnlyInCart to set
	 */
	public void setGiftCertOnlyInCart(boolean isGiftCertOnlyInCart) {
		this.isGiftCertOnlyInCart = isGiftCertOnlyInCart;
	}

	/**
	 * @return the isGiftInCart
	 */
	public boolean isGiftInCart() {
		return isGiftInCart;
	}

	/**
	 * @param isGiftInCart the isGiftInCart to set
	 */
	public void setGiftInCart(boolean isGiftInCart) {
		this.isGiftInCart = isGiftInCart;
	}

	/**
	 * @return the isItemStdFreeShipInCart
	 */
	public boolean isItemStdFreeShipInCart() {
		return isItemStdFreeShipInCart;
	}

	/**
	 * @param isItemStdFreeShipInCart the isItemStdFreeShipInCart to set
	 */
	public void setItemStdFreeShipInCart(boolean isItemStdFreeShipInCart) {
		this.isItemStdFreeShipInCart = isItemStdFreeShipInCart;
	}

	/**
	 * @return the isItemExpedFreeShipInCart
	 */
	public boolean isItemExpedFreeShipInCart() {
		return isItemExpedFreeShipInCart;
	}

	/**
	 * @param isItemExpedFreeShipInCart the isItemExpedFreeShipInCart to set
	 */
	public void setItemExpedFreeShipInCart(boolean isItemExpedFreeShipInCart) {
		this.isItemExpedFreeShipInCart = isItemExpedFreeShipInCart;
	}

	/**
	 * @return the isItemExprFreeShipInCart
	 */
	public boolean isItemExprFreeShipInCart() {
		return isItemExprFreeShipInCart;
	}

	/**
	 * @param isItemExprFreeShipInCart the isItemExprFreeShipInCart to set
	 */
	public void setItemExprFreeShipInCart(boolean isItemExprFreeShipInCart) {
		this.isItemExprFreeShipInCart = isItemExprFreeShipInCart;
	}

	/**
	 * @return the uniqueRegistryNum
	 */
	public long getUniqueRegistryNum() {
		return uniqueRegistryNum;
	}

	/**
	 * @param uniqueRegistryNum the uniqueRegistryNum to set
	 */
	public void setUniqueRegistryNum(long uniqueRegistryNum) {
		this.uniqueRegistryNum = uniqueRegistryNum;
	}

	/**
	 * @return the productSubtotalAmt
	 */
	public double getProductSubtotalAmt() {
		return productSubtotalAmt;
	}

	/**
	 * @param productSubtotalAmt the productSubtotalAmt to set
	 */
	public void setProductSubtotalAmt(double productSubtotalAmt) {
		this.productSubtotalAmt = productSubtotalAmt;
	}

	/**
	 * @return the totalAmt
	 */
	public double getTotalAmt() {
		return totalAmt;
	}

	/**
	 * @param totalAmt the totalAmt to set
	 */
	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}

	/**
	 * @return the registrantName
	 */
	public String getRegistrantName() {
		return registrantName;
	}

	/**
	 * @param registrantName the registrantName to set
	 */
	public void setRegistrantName(String registrantName) {
		this.registrantName = registrantName;
	}

	/**
	 * @return the coRegistrantName
	 */
	public String getCoRegistrantName() {
		return coRegistrantName;
	}

	/**
	 * @param coRegistrantName the coRegistrantName to set
	 */
	public void setCoRegistrantName(String coRegistrantName) {
		this.coRegistrantName = coRegistrantName;
	}

	/**
	 * @return the combinedRegistrantNames
	 */
	public String getCombinedRegistrantNames() {
		return combinedRegistrantNames;
	}

	/**
	 * @param combinedRegistrantNames the combinedRegistrantNames to set
	 */
	public void setCombinedRegistrantNames(String combinedRegistrantNames) {
		this.combinedRegistrantNames = combinedRegistrantNames;
	}

	/**
	 * @return the cartItemDetailList
	 */
	public List<CartItemDetailInfo> getCartItemDetailList() {
		return cartItemDetailList;
	}

	/**
	 * @param cartItemDetailList the cartItemDetailList to set
	 */
	public void setCartItemDetailList(List<CartItemDetailInfo> cartItemDetailList) {
		this.cartItemDetailList = cartItemDetailList;
	}
	
 

}
