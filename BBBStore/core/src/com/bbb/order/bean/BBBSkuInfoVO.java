package com.bbb.order.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BBBSkuInfoVO  implements Serializable {
/**
	 * 
	 */
private static final long serialVersionUID = 1L;
private String skuId;
private BigInteger quntity;
private BigDecimal totalAmount;
private Long quantity;
private Double amount; 
private String siteId;
private int frequency = 0;



/**
 * @return the quantity
 */
public Long getQuantity() {
	return this.quantity;
}
/**
 * @param quantity the quantity to set
 */
public void setQuantity(Long quantity) {
	this.quantity = quantity;
}
/**
 * @return the amount
 */
public Double getAmount() {
	return this.amount;
}
/**
 * @param amount the amount to set
 */
public void setAmount(Double amount) {
	this.amount = amount;
}
/**
 * @return the siteId
 */
public String getSiteId() {
	return this.siteId;
}
/**
 * @param siteId the siteId to set
 */
public void setSiteId(String siteId) {
	this.siteId = siteId;
}
/**
 * @return the frequency
 */
public int getFrequency() {
	return this.frequency;
}
/**
 * @param frequency the frequency to set
 */
public void setFrequency(int frequency) {
	this.frequency = frequency;
}
public BigDecimal getTotalAmount() {
	return this.totalAmount;
}
public void setTotalAmount(BigDecimal totalAmount) {
	this.totalAmount = totalAmount;
}
public String getSkuId() {
	return this.skuId;
}
public void setSkuId(String skuId) {
	this.skuId = skuId;
}
public BigInteger getQuntity() {
	return this.quntity;
}
public void setQuntity(BigInteger quntity) {
	this.quntity = quntity;
} 
	
}
