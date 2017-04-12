package com.bbb.kickstarters;

import java.io.Serializable;

public class TopSkuVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skuId;
	private int recommanded_qty;
	private String id;
	private String comment;
	private TopSkuDetailVO skuDetailVO = null;
	private TopSkuProductVO productVO=null;
	private String salePrice;
	private String listPrice;
	private String inCartPriceVal;
	
	
	public TopSkuDetailVO getSkuDetailVO() {
		return skuDetailVO;
	}
	public void setSkuDetailVO(TopSkuDetailVO skuDetailVO) {
		this.skuDetailVO = skuDetailVO;
	}
	public TopSkuProductVO getProductVO() {
		return productVO;
	}
	public void setProductVO(TopSkuProductVO productVO) {
		this.productVO = productVO;
	}
	public String getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}
	public String getListPrice() {
		return listPrice;
	}
	public void setListPrice(String listPrice) {
		this.listPrice = listPrice;
	}

	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public int getRecommanded_qty() {
		return recommanded_qty;
	}
	public void setRecommanded_qty(int recommanded_qty) {
		this.recommanded_qty = recommanded_qty;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getInCartPriceVal() {
		return inCartPriceVal;
	}
	public void setInCartPriceVal(String inCartPriceVal) {
		this.inCartPriceVal = inCartPriceVal;
	}
	
	
	
}