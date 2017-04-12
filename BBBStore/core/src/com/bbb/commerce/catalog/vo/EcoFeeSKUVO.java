/**
 * 
 */
package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

/**
 * @author iteggi
 *
 */
public class EcoFeeSKUVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ecoFeeSKUId;
	private String ecoFeeProductId;
	/**
	 * @return the ecoFeeSKUId
	 */
	public String getFeeEcoSKUId() {
		return ecoFeeSKUId;
	}
	/**
	 * @param pEcoFeeSKUId the ecoFeeSKUId to set
	 */
	public void setEcoFeeSKUId(String pEcoFeeSKUId) {
		ecoFeeSKUId = pEcoFeeSKUId;
	}
	/**
	 * @return the ecoFeeProductId
	 */
	public String getEcoFeeProductId() {
		return ecoFeeProductId;
	}
	/**
	 * @param pEcoFeeProductId the ecoFeeProductId to set
	 */
	public void setEcoFeeProductId(String pEcoFeeProductId) {
		ecoFeeProductId = pEcoFeeProductId;
	}
	public String toString(){
		StringBuffer toString=new StringBuffer("Eco Fee SKUVO Details \n");
		toString.append("ecoFee SKU Id").append(ecoFeeSKUId).append("\n")
		.append("ecoFee Product Id").append(ecoFeeProductId).append("\n");
		return toString.toString();
	}

}
