package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * CartItemDetailInfo.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */

 public class CartItemDetailInfo  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean airShipExcluded;

    private boolean isVDS;

    private boolean isProductSKU;

    private boolean isGiftCert;

    private boolean isCoupon;

    private boolean isStockAdequate;

    private boolean isGiftWrapExcluded;

    private boolean isProp65Item;

    private int orderQty;

    private int jdaDeptID;

    private int availForSaleQty;

    private long orderShipItemID;

    private long registryID;

    private long vendorID;

    private double unitCost;

    private double extPrice;

    private double discountAmt;

    private double shipUpchargeAmt;

    private double taxAmt;

    private String sku;

    private String registrantNm;

    private String coRegistrantNm;

    private String giftWrapFlag;

    private String itemTypeCd;

    private String eligibleFlag;

    private String couponAppliedFlag;

    private String freeShipFlag;

    private String itemAttribs;

    private String itemDesc;

    private String color;

    private String webOfferedFlag;

    private String disableFlag;

    private String ovrWtFlag;

    private String ovrSizeFlag;

    private String forceBelowLineFlag;

    private String availForSaleIgrFlag;

    private String skuTypeCd;

    private String prodCD;

    private String reasonCd;

    private String promoCd;

    private String photoURL;

    private boolean isDisplayable;

    private String itemAttribsStr;
    
    private String productId;

    private String productSeoUrl;



	/**
	 * @return the airShipExcluded
	 */
	public boolean isAirShipExcluded() {
		return airShipExcluded;
	}

	/**
	 * @param airShipExcluded the airShipExcluded to set
	 */
	public void setAirShipExcluded(boolean airShipExcluded) {
		this.airShipExcluded = airShipExcluded;
	}

	/**
	 * @return the isVDS
	 */
	public boolean isVDS() {
		return isVDS;
	}

	/**
	 * @param isVDS the isVDS to set
	 */
	public void setVDS(boolean isVDS) {
		this.isVDS = isVDS;
	}

	/**
	 * @return the isProductSKU
	 */
	public boolean isProductSKU() {
		return isProductSKU;
	}

	/**
	 * @param isProductSKU the isProductSKU to set
	 */
	public void setProductSKU(boolean isProductSKU) {
		this.isProductSKU = isProductSKU;
	}

	/**
	 * @return the isGiftCert
	 */
	public boolean isGiftCert() {
		return isGiftCert;
	}

	/**
	 * @param isGiftCert the isGiftCert to set
	 */
	public void setGiftCert(boolean isGiftCert) {
		this.isGiftCert = isGiftCert;
	}

	/**
	 * @return the isCoupon
	 */
	public boolean isCoupon() {
		return isCoupon;
	}

	/**
	 * @param isCoupon the isCoupon to set
	 */
	public void setCoupon(boolean isCoupon) {
		this.isCoupon = isCoupon;
	}

	/**
	 * @return the isStockAdequate
	 */
	public boolean isStockAdequate() {
		return isStockAdequate;
	}

	/**
	 * @param isStockAdequate the isStockAdequate to set
	 */
	public void setStockAdequate(boolean isStockAdequate) {
		this.isStockAdequate = isStockAdequate;
	}

	/**
	 * @return the isGiftWrapExcluded
	 */
	public boolean isGiftWrapExcluded() {
		return isGiftWrapExcluded;
	}

	/**
	 * @param isGiftWrapExcluded the isGiftWrapExcluded to set
	 */
	public void setGiftWrapExcluded(boolean isGiftWrapExcluded) {
		this.isGiftWrapExcluded = isGiftWrapExcluded;
	}

	/**
	 * @return the isProp65Item
	 */
	public boolean isProp65Item() {
		return isProp65Item;
	}

	/**
	 * @param isProp65Item the isProp65Item to set
	 */
	public void setProp65Item(boolean isProp65Item) {
		this.isProp65Item = isProp65Item;
	}

	/**
	 * @return the orderQty
	 */
	public int getOrderQty() {
		return orderQty;
	}

	/**
	 * @param orderQty the orderQty to set
	 */
	public void setOrderQty(int orderQty) {
		this.orderQty = orderQty;
	}

	/**
	 * @return the jDADeptID
	 */
	public int getJDADeptID() {
		return jdaDeptID;
	}

	/**
	 * @param jDADeptID the jDADeptID to set
	 */
	public void setJDADeptID(int jDADeptID) {
		this.jdaDeptID = jDADeptID;
	}

	/**
	 * @return the availForSaleQty
	 */
	public int getAvailForSaleQty() {
		return availForSaleQty;
	}

	/**
	 * @param availForSaleQty the availForSaleQty to set
	 */
	public void setAvailForSaleQty(int availForSaleQty) {
		this.availForSaleQty = availForSaleQty;
	}

	/**
	 * @return the orderShipItemID
	 */
	public long getOrderShipItemID() {
		return orderShipItemID;
	}

	/**
	 * @param orderShipItemID the orderShipItemID to set
	 */
	public void setOrderShipItemID(long orderShipItemID) {
		this.orderShipItemID = orderShipItemID;
	}

	/**
	 * @return the registryID
	 */
	public long getRegistryID() {
		return registryID;
	}

	/**
	 * @param registryID the registryID to set
	 */
	public void setRegistryID(long registryID) {
		this.registryID = registryID;
	}

	/**
	 * @return the vendorID
	 */
	public long getVendorID() {
		return vendorID;
	}

	/**
	 * @param vendorID the vendorID to set
	 */
	public void setVendorID(long vendorID) {
		this.vendorID = vendorID;
	}

	/**
	 * @return the unitCost
	 */
	public double getUnitCost() {
		return unitCost;
	}

	/**
	 * @param unitCost the unitCost to set
	 */
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}

	/**
	 * @return the extPrice
	 */
	public double getExtPrice() {
		return extPrice;
	}

	/**
	 * @param extPrice the extPrice to set
	 */
	public void setExtPrice(double extPrice) {
		this.extPrice = extPrice;
	}

	/**
	 * @return the discountAmt
	 */
	public double getDiscountAmt() {
		return discountAmt;
	}

	/**
	 * @param discountAmt the discountAmt to set
	 */
	public void setDiscountAmt(double discountAmt) {
		this.discountAmt = discountAmt;
	}

	/**
	 * @return the shipUpchargeAmt
	 */
	public double getShipUpchargeAmt() {
		return shipUpchargeAmt;
	}

	/**
	 * @param shipUpchargeAmt the shipUpchargeAmt to set
	 */
	public void setShipUpchargeAmt(double shipUpchargeAmt) {
		this.shipUpchargeAmt = shipUpchargeAmt;
	}

	/**
	 * @return the taxAmt
	 */
	public double getTaxAmt() {
		return taxAmt;
	}

	/**
	 * @param taxAmt the taxAmt to set
	 */
	public void setTaxAmt(double taxAmt) {
		this.taxAmt = taxAmt;
	}

	/**
	 * @return the sKU
	 */
	public String getSKU() {
		return sku;
	}

	/**
	 * @param sKU the sKU to set
	 */
	public void setSKU(String sKU) {
		this.sku = sKU;
	}

	/**
	 * @return the registrantNm
	 */
	public String getRegistrantNm() {
		return registrantNm;
	}

	/**
	 * @param registrantNm the registrantNm to set
	 */
	public void setRegistrantNm(String registrantNm) {
		this.registrantNm = registrantNm;
	}

	/**
	 * @return the coRegistrantNm
	 */
	public String getCoRegistrantNm() {
		return coRegistrantNm;
	}

	/**
	 * @param coRegistrantNm the coRegistrantNm to set
	 */
	public void setCoRegistrantNm(String coRegistrantNm) {
		this.coRegistrantNm = coRegistrantNm;
	}

	/**
	 * @return the giftWrapFlag
	 */
	public String getGiftWrapFlag() {
		return giftWrapFlag;
	}

	/**
	 * @param giftWrapFlag the giftWrapFlag to set
	 */
	public void setGiftWrapFlag(String giftWrapFlag) {
		this.giftWrapFlag = giftWrapFlag;
	}

	/**
	 * @return the itemTypeCd
	 */
	public String getItemTypeCd() {
		return itemTypeCd;
	}

	/**
	 * @param itemTypeCd the itemTypeCd to set
	 */
	public void setItemTypeCd(String itemTypeCd) {
		this.itemTypeCd = itemTypeCd;
	}

	/**
	 * @return the eligibleFlag
	 */
	public String getEligibleFlag() {
		return eligibleFlag;
	}

	/**
	 * @param eligibleFlag the eligibleFlag to set
	 */
	public void setEligibleFlag(String eligibleFlag) {
		this.eligibleFlag = eligibleFlag;
	}

	/**
	 * @return the couponAppliedFlag
	 */
	public String getCouponAppliedFlag() {
		return couponAppliedFlag;
	}

	/**
	 * @param couponAppliedFlag the couponAppliedFlag to set
	 */
	public void setCouponAppliedFlag(String couponAppliedFlag) {
		this.couponAppliedFlag = couponAppliedFlag;
	}

	/**
	 * @return the freeShipFlag
	 */
	public String getFreeShipFlag() {
		return freeShipFlag;
	}

	/**
	 * @param freeShipFlag the freeShipFlag to set
	 */
	public void setFreeShipFlag(String freeShipFlag) {
		this.freeShipFlag = freeShipFlag;
	}

	/**
	 * @return the itemAttribs
	 */
	public String getItemAttribs() {
		return itemAttribs;
	}

	/**
	 * @param itemAttribs the itemAttribs to set
	 */
	public void setItemAttribs(String itemAttribs) {
		this.itemAttribs = itemAttribs;
	}

	/**
	 * @return the itemDesc
	 */
	public String getItemDesc() {
		return itemDesc;
	}

	/**
	 * @param itemDesc the itemDesc to set
	 */
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the webOfferedFlag
	 */
	public String getWebOfferedFlag() {
		return webOfferedFlag;
	}

	/**
	 * @param webOfferedFlag the webOfferedFlag to set
	 */
	public void setWebOfferedFlag(String webOfferedFlag) {
		this.webOfferedFlag = webOfferedFlag;
	}

	/**
	 * @return the disableFlag
	 */
	public String getDisableFlag() {
		return disableFlag;
	}

	/**
	 * @param disableFlag the disableFlag to set
	 */
	public void setDisableFlag(String disableFlag) {
		this.disableFlag = disableFlag;
	}

	/**
	 * @return the ovrWtFlag
	 */
	public String getOvrWtFlag() {
		return ovrWtFlag;
	}

	/**
	 * @param ovrWtFlag the ovrWtFlag to set
	 */
	public void setOvrWtFlag(String ovrWtFlag) {
		this.ovrWtFlag = ovrWtFlag;
	}

	/**
	 * @return the ovrSizeFlag
	 */
	public String getOvrSizeFlag() {
		return ovrSizeFlag;
	}

	/**
	 * @param ovrSizeFlag the ovrSizeFlag to set
	 */
	public void setOvrSizeFlag(String ovrSizeFlag) {
		this.ovrSizeFlag = ovrSizeFlag;
	}

	/**
	 * @return the forceBelowLineFlag
	 */
	public String getForceBelowLineFlag() {
		return forceBelowLineFlag;
	}

	/**
	 * @param forceBelowLineFlag the forceBelowLineFlag to set
	 */
	public void setForceBelowLineFlag(String forceBelowLineFlag) {
		this.forceBelowLineFlag = forceBelowLineFlag;
	}

	/**
	 * @return the availForSaleIgrFlag
	 */
	public String getAvailForSaleIgrFlag() {
		return availForSaleIgrFlag;
	}

	/**
	 * @param availForSaleIgrFlag the availForSaleIgrFlag to set
	 */
	public void setAvailForSaleIgrFlag(String availForSaleIgrFlag) {
		this.availForSaleIgrFlag = availForSaleIgrFlag;
	}

	/**
	 * @return the sKUTypeCd
	 */
	public String getSKUTypeCd() {
		return skuTypeCd;
	}

	/**
	 * @param sKUTypeCd the sKUTypeCd to set
	 */
	public void setSKUTypeCd(String sKUTypeCd) {
		this.skuTypeCd = sKUTypeCd;
	}

	/**
	 * @return the prodCD
	 */
	public String getProdCD() {
		return prodCD;
	}

	/**
	 * @param prodCD the prodCD to set
	 */
	public void setProdCD(String prodCD) {
		this.prodCD = prodCD;
	}

	/**
	 * @return the reasonCd
	 */
	public String getReasonCd() {
		return reasonCd;
	}

	/**
	 * @param reasonCd the reasonCd to set
	 */
	public void setReasonCd(String reasonCd) {
		this.reasonCd = reasonCd;
	}

	/**
	 * @return the promoCd
	 */
	public String getPromoCd() {
		return promoCd;
	}

	/**
	 * @param promoCd the promoCd to set
	 */
	public void setPromoCd(String promoCd) {
		this.promoCd = promoCd;
	}

	/**
	 * @return the photoURL
	 */
	public String getPhotoURL() {
		return photoURL;
	}

	/**
	 * @param photoURL the photoURL to set
	 */
	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	/**
	 * @return the isDisplayable
	 */
	public boolean isDisplayable() {
		return isDisplayable;
	}

	/**
	 * @param isDisplayable the isDisplayable to set
	 */
	public void setDisplayable(boolean isDisplayable) {
		this.isDisplayable = isDisplayable;
	}

	/**
	 * @return the itemAttribsStr
	 */
	public String getItemAttribsStr() {
		return itemAttribsStr;
	}

	/**
	 * @param itemAttribsStr the itemAttribsStr to set
	 */
	public void setItemAttribsStr(String itemAttribsStr) {
		this.itemAttribsStr = itemAttribsStr;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductSeoUrl() {
		return productSeoUrl;
	}

	public void setProductSeoUrl(String productSeoUrl) {
		this.productSeoUrl = productSeoUrl;
	}
	
	

}
