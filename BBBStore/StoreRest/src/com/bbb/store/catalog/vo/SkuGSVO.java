package com.bbb.store.catalog.vo;

import java.io.Serializable;
import java.util.Map;

import com.bbb.rest.catalog.vo.SkuRestVO;

public class SkuGSVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SkuRestVO skuRestVO;
	private Map<String,String> skuImageMap;
	private Map<String,Integer> skuInventory;
	/**
	 * @return the skuImageMap
	 */
	public Map<String,String> getSkuImageMap() {
		return skuImageMap;
	}
	/**
	 * @param skuImageMap the skuImageMap to set
	 */
	public void setSkuImageMap(Map<String,String> skuImageMap) {
		this.skuImageMap = skuImageMap;
	}
	/**
	 * @return the skuRestVO
	 */
	public SkuRestVO getSkuRestVO() {
		return skuRestVO;
	}
	/**
	 * @param skuRestVO the skuRestVO to set
	 */
	public void setSkuRestVO(SkuRestVO skuRestVO) {
		this.skuRestVO = skuRestVO;
	}
	/**
	 * @return the skuInventory
	 */
	public Map<String,Integer> getSkuInventory() {
		return skuInventory;
	}
	/**
	 * @param skuInventory the skuInventory to set
	 */
	public void setSkuInventory(Map<String,Integer> skuInventory) {
		this.skuInventory = skuInventory;
	}
	
}
