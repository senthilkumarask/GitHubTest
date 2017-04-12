package com.bbb.store.catalog.vo;

import java.io.Serializable;

public class MultiSkuDataVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FilteredSKUDetailVO  skuGsVo;
	private boolean isskuError;
	private String errorMessage;
	
	
	public boolean isSkuError() {
		return isskuError;
	}
	public void setSkuError(boolean isskuError) {
		this.isskuError = isskuError;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public FilteredSKUDetailVO getSkuGsVo() {
		return skuGsVo;
	}
	public void setSkuGsVo(FilteredSKUDetailVO skuGsVo) {
		this.skuGsVo = skuGsVo;
	}
	
	
}
