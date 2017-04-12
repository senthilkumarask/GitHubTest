package com.bbb.commerce.browse.vo;

import java.io.Serializable;

import com.bbb.commerce.catalog.vo.SKUDetailVO;


public class RecommendedSkuVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SKUDetailVO recommSKUVO = null;
	private String comment = null;
	private int priority = 0;
	private String cartSkuId = null;
	private double listPrice = 0.0;
	private double salePrice = 0.0;	
	public SKUDetailVO getRecommSKUVO() {
		return recommSKUVO;
	}
	public void setRecommSKUVO(SKUDetailVO recommSKUVO) {
		this.recommSKUVO = recommSKUVO;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getCartSkuId() {
		return cartSkuId;
	}
	public void setCartSkuId(String cartSkuId) {
		this.cartSkuId = cartSkuId;
	}
	public double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	public double getListPrice() {
		return listPrice;
	}
	public void setListPrice(double listPrice) {
		this.listPrice = listPrice;
	}
	
	
}
