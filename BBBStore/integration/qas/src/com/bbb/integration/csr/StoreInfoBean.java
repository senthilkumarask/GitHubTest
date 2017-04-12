package com.bbb.integration.csr;

import java.io.Serializable;

public class StoreInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String storeId;
	private String storeCity;
	private String storeState;
	private String storeZipCode;
	private String storeBopusFlagBaby;
	private String storeBopusFlagUS;

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreCity() {
		return storeCity;
	}

	public void setStoreCity(String storeCity) {
		this.storeCity = storeCity;
	}

	public String getStoreState() {
		return storeState;
	}

	public void setStoreState(String storeState) {
		this.storeState = storeState;
	}

	public String getStoreZipCode() {
		return storeZipCode;
	}

	public void setStoreZipCode(String storeZipCode) {
		this.storeZipCode = storeZipCode;
	}

	public String getStoreBopusFlagBaby() {
		return storeBopusFlagBaby;
	}

	public void setStoreBopusFlagBaby(String storeBopusFlagBaby) {
		this.storeBopusFlagBaby = storeBopusFlagBaby;
	}

	public String getStoreBopusFlagUS() {
		return storeBopusFlagUS;
	}

	public void setStoreBopusFlagUS(String storeBopusFlagUS) {
		this.storeBopusFlagUS = storeBopusFlagUS;
	}

}
