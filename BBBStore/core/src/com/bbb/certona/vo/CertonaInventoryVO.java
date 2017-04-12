package com.bbb.certona.vo;

import java.io.Serializable;

public class CertonaInventoryVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skuId;
	private double stockBBBUS;
	
	private double stockBBBCA;

	private double stockBuyBuyBaby;

	private double registryStockBBBUS;

	private double registryStockBBBCA;

	private double registryStockBuyBuyBaby;

	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		return skuId;
	}

	/**
	 * @param skuId the skuId to set
	 */
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	/**
	 * @return the stockBBBUS
	 */
	public double getStockBBBUS() {
		return stockBBBUS;
	}

	/**
	 * @param stockBBBUS the stockBBBUS to set
	 */
	public void setStockBBBUS(double stockBBBUS) {
		this.stockBBBUS = stockBBBUS;
	}

	/**
	 * @return the stockBBBCA
	 */
	public double getStockBBBCA() {
		return stockBBBCA;
	}

	/**
	 * @param stockBBBCA the stockBBBCA to set
	 */
	public void setStockBBBCA(double stockBBBCA) {
		this.stockBBBCA = stockBBBCA;
	}

	/**
	 * @return the stockBuyBuyBaby
	 */
	public double getStockBuyBuyBaby() {
		return stockBuyBuyBaby;
	}

	/**
	 * @param stockBuyBuyBaby the stockBuyBuyBaby to set
	 */
	public void setStockBuyBuyBaby(double stockBuyBuyBaby) {
		this.stockBuyBuyBaby = stockBuyBuyBaby;
	}

	/**
	 * @return the registryStockBBBUS
	 */
	public double getRegistryStockBBBUS() {
		return registryStockBBBUS;
	}

	/**
	 * @param registryStockBBBUS the registryStockBBBUS to set
	 */
	public void setRegistryStockBBBUS(double registryStockBBBUS) {
		this.registryStockBBBUS = registryStockBBBUS;
	}

	/**
	 * @return the registryStockBBBCA
	 */
	public double getRegistryStockBBBCA() {
		return registryStockBBBCA;
	}

	/**
	 * @param registryStockBBBCA the registryStockBBBCA to set
	 */
	public void setRegistryStockBBBCA(double registryStockBBBCA) {
		this.registryStockBBBCA = registryStockBBBCA;
	}

	/**
	 * @return the registryStockBuyBuyBaby
	 */
	public double getRegistryStockBuyBuyBaby() {
		return registryStockBuyBuyBaby;
	}

	/**
	 * @param registryStockBuyBuyBaby the registryStockBuyBuyBaby to set
	 */
	public void setRegistryStockBuyBuyBaby(double registryStockBuyBuyBaby) {
		this.registryStockBuyBuyBaby = registryStockBuyBuyBaby;
	}

}