package com.bbb.rest.commerce.promotion;

import java.io.Serializable;


public class BBBClosenessQualifierDropletResultVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int type;
	private String url;
	private String name;
	private String promoText;
	private double onlinePurchaseTotal;
	
	/**
	 * @return the promoText
	 */
	public String getPromoText() {
		return promoText;
	}
	/**
	 * @param promoText the promoText to set
	 */
	public void setPromoText(String promoText) {
		this.promoText = promoText;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getOnlinePurchaseTotal() {
		return onlinePurchaseTotal;
	}
	public void setOnlinePurchaseTotal(double onlinePurchaseTotal) {
		this.onlinePurchaseTotal = onlinePurchaseTotal;
	}
}
