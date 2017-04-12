package com.bbb.commerce.vo;

import java.io.Serializable;
import java.util.Map;

import com.bbb.commerce.common.BBBAddressContainer;

public class BBBAddressSelectionVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BBBAddressContainer newAddrContainer;
	private Map<String, String> defaultAddIdMap;
	public BBBAddressContainer getNewAddrContainer() {
		return newAddrContainer;
	}
	public void setNewAddrContainer(BBBAddressContainer newAddrContainer) {
		this.newAddrContainer = newAddrContainer;
	}
	public Map<String, String> getDefaultAddIdMap() {
		return defaultAddIdMap;
	}
	public void setDefaultAddIdMap(Map<String, String> defaultAddIdMap) {
		this.defaultAddIdMap = defaultAddIdMap;
	}
}
