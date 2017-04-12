package com.bbb.commerce.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InventoryStatusVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, Integer> inventoryMap = new HashMap<String, Integer>();
	private Map<String, Integer> inventoryStatusMap = new HashMap<String, Integer>();
	/**
	 * @return the inventoryMap
	 */
	public Map<String, Integer> getInventoryMap() {
		return inventoryMap;
	}
	/**
	 * @return the inventoryStatusMap
	 */
	public Map<String, Integer> getInventoryStatusMap() {
		return inventoryStatusMap;
	}
	/**
	 * @param pInventoryMap the inventoryMap to set
	 */
	public void setInventoryMap(Map<String, Integer> pInventoryMap) {
		inventoryMap = pInventoryMap;
	}
	/**
	 * @param pInventoryStatusMap the inventoryStatusMap to set
	 */
	public void setInventoryStatusMap(Map<String, Integer> pInventoryStatusMap) {
		inventoryStatusMap = pInventoryStatusMap;
	}
	
	

}
