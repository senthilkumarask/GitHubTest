package com.bbb.internationalshipping.vo;

import java.io.Serializable;


/**
 * This class gives you the information
 * about currency lcpFactor, fxRate and scale for  a particular currency.
 * @version 1.0
 */
public class BBBInternationalCurrencyDetailsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to currencyCode.
	 */
	private double lcpFactor;
	/**
	 * This variable is used to point to currencyName.
	 */
	private double fxRate;
	/**
	 * This variable is used to point to scale.
	 */
	private Integer scale;
	/**
	 * This variable is used to point to scale.
	 */
	private String currencySymbol;
	
	/**
	 * @return the currencySymbol
	 */
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	/**
	 * @param currencySymbol the currencySymbol to set
	 */
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	/**
	 * @return the lcpFactor
	 */
	public double getLcpFactor() {
		return lcpFactor;
	}
	/**
	 * @param lcpFactor the lcpFactor to set
	 */
	public void setLcpFactor(double lcpFactor) {
		this.lcpFactor = lcpFactor;
	}
	/**
	 * @return the fxRate
	 */
	public double getFxRate() {
		return fxRate;
	}
	/**
	 * @param fxRate the fxRate to set
	 */
	public void setFxRate(double fxRate) {
		this.fxRate = fxRate;
	}
	/**
	 * @return the scale
	 */
	public Integer getScale() {
		return scale;
	}
	/**
	 * @param scale the scale to set
	 */
	public void setScale(Integer scale) {
		this.scale = scale;
	}
}