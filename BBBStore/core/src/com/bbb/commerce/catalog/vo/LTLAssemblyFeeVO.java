package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

/**
 * 
 * @author naga13
 *
 */
public class LTLAssemblyFeeVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ltlAssemblySkuId
	 */
	private String ltlAssemblySkuId;
	
	/**
	 * ltlAssemblyProductId
	 */
	private String ltlAssemblyProductId;
	
	/**
	 * ltlAssemblySkuPrice
	 */
	private double ltlAssemblySkuPrice;
	
	public LTLAssemblyFeeVO()
	{
		//default constructor
	}

	public LTLAssemblyFeeVO(String ltlAssemblySkuId, String ltlAssemblyProductId,
			double ltlAssemblySkuPrice) {
		this.ltlAssemblySkuId = ltlAssemblySkuId;
		this.ltlAssemblyProductId = ltlAssemblyProductId;
		this.ltlAssemblySkuPrice = ltlAssemblySkuPrice;
	}

	/**
	 * @return the ltlAssemblySkuId
	 */
	public String getLtlAssemblySkuId() {
		return ltlAssemblySkuId;
	}
	/**
	 * @param ltlAssemblySkuId the ltlAssemblySkuId to set
	 */
	public void setLtlAssemblySkuId(String ltlAssemblySkuId) {
		this.ltlAssemblySkuId = ltlAssemblySkuId;
	}
	/**
	 * @return the ltlAssemblyProductId
	 */
	public String getLtlAssemblyProductId() {
		return ltlAssemblyProductId;
	}
	/**
	 * @param ltlAssemblyProductId the ltlAssemblyProductId to set
	 */
	public void setLtlAssemblyProductId(String ltlAssemblyProductId) {
		this.ltlAssemblyProductId = ltlAssemblyProductId;
	}
	/**
	 * @return the ltlAssemblySkuPrice
	 */
	public double getLtlAssemblySkuPrice() {
		return ltlAssemblySkuPrice;
	}
	/**
	 * @param ltlAssemblySkuPrice the ltlAssemblySkuPrice to set
	 */
	public void setLtlAssemblySkuPrice(double ltlAssemblySkuPrice) {
		this.ltlAssemblySkuPrice = ltlAssemblySkuPrice;
	}
	
	public String toString(){
		StringBuffer toString=new StringBuffer(" Assembly Fee VO Details \n ");
		toString.append("Assembly Fee Sku Id ").append(ltlAssemblySkuId).append("\n")
		.append("Assembly Fee product Id ").append(ltlAssemblyProductId).append("\n")
		.append("Assembly Fee Sku Price ").append(ltlAssemblySkuPrice).append("\n");
		
		return toString.toString();
	}
}
