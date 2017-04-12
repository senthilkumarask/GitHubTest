/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
/*import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import atg.core.util.StringUtils;
*/


// TODO: Auto-generated Javadoc
/**
 * This class provides the Registry summary information properties.
 *
 * @author dhanashree
 * 
 */
public class RegSkuDetailsVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int registryNum;
	int skuId;
	int qtyRequested;
	int qtyFulfilled;
	int qtyPurchReserv;
	String actionCD;
	public int getRegistryNum() {
		return registryNum;
	}
	public void setRegistryNum(int registryNum) {
		this.registryNum = registryNum;
	}
	public int getSkuId() {
		return skuId;
	}
	public void setSkuId(int skuId) {
		this.skuId = skuId;
	}
	public int getQtyRequested() {
		return qtyRequested;
	}
	public void setQtyRequested(int qtyRequested) {
		this.qtyRequested = qtyRequested;
	}
	public int getQtyFulfilled() {
		return qtyFulfilled;
	}
	public void setQtyFulfilled(int qtyFulfilled) {
		this.qtyFulfilled = qtyFulfilled;
	}
	public int getQtyPurchReserv() {
		return qtyPurchReserv;
	}
	public void setQtyPurchReserv(int qtyPurchReserv) {
		this.qtyPurchReserv = qtyPurchReserv;
	}
	public String getActionCD() {
		return actionCD;
	}
	public void setActionCD(String actionCD) {
		this.actionCD = actionCD;
	}
	
	
	
	
}