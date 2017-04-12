package com.bbb.commerce.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Session scope component
 * 
 * @author manohar
 * @story UC_checkout_billing
 * @created 12/2/2011
 */
public class BBBAddressContainer {
	
	private Map<String, String> sourceMap;
	private LinkedHashMap<String, BBBAddress> addressMap;
	private List<BBBAddress> duplicate;
	private Map<String, BBBAddress> newAddressMap;
	private String newAddressKey;
	
	public BBBAddressContainer(){
		super();
		initialize();
	}
	
	
	public List<BBBAddress> getDuplicate() {
		return duplicate;
	}

	public void setDuplicate(List<BBBAddress> duplicate) {
		this.duplicate = duplicate;
	}



	/**
	 * 
	 * @return
	 */
	public Map<String, String> getSourceMap() {
		return sourceMap;
	}

	/**
	 * 
	 * @param sourceMap
	 */
	public void setSourceMap(Map<String, String> sourceMap) {
		this.sourceMap = sourceMap;
	}
	
	/**
	 * 
	 * @return
	 */
	public LinkedHashMap<String, BBBAddress> getAddressMap() {
		return addressMap;
	}
	
	/**
	 * 
	 * @param addressMap
	 */
	public void setAddressMap(LinkedHashMap<String, BBBAddress> addressMap) {
		this.addressMap = addressMap;
	}
	
	/**
	 * 
	 * 
	 * @story UC_checkout_billing
	 */
	public final void initialize(){
		
		sourceMap = new HashMap<String, String>();
		addressMap = new LinkedHashMap<String, BBBAddress>();
		duplicate = new ArrayList<BBBAddress>();
	}
	
	/**
	 * Adds address to the container
	 * 
	 * @param addressKey
	 * @param address
	 */
	public void addAddressToContainer(String addressKey, BBBAddress address){
		if(null == this.addressMap){
			this.addressMap = new LinkedHashMap<String, BBBAddress>();
		}
		addressMap.put(addressKey, address);
	}
	
	/**
	 * 
	 * @param addressKey
	 * @return address
	 */
	public BBBAddress getAddressFromContainer(String addressKey) {
		return (null != addressMap && !addressMap.isEmpty()) ? addressMap
				.get(addressKey) : null;
	}
	
	/**
	 * 
	 * @param addressKey
	 */
	public void removeAddressFromContainer(String addressKey){
		if(null != addressMap && !addressMap.isEmpty()){
			addressMap.remove(addressKey);
		}
	}
	
	/**
	 * 
	 * @param currentAddressKey
	 * @param sourceAddresskey
	 */
	public void addSourceMapping(String currentAddressKey,
			String sourceAddresskey) {
		if (null == sourceMap) {
			sourceMap = new HashMap<String, String>();
		}
		sourceMap.put(currentAddressKey, sourceAddresskey);
	}
	
	public String getSourceKey(String inAddrKey) {
		return (null != sourceMap && !sourceMap.isEmpty()) ? sourceMap
				.get(inAddrKey) : null;
	}
	
	public Map<String, BBBAddress> getNewAddressMap() {
		return newAddressMap;
	}


	public void setNewAddressMap(Map<String, BBBAddress> newAddressMap) {
		this.newAddressMap = newAddressMap;
	}
	
	/**
	 * Adds address to the container
	 * 
	 * @param addressKey
	 * @param address
	 */
	public void addNewAddressToContainer(String addressKey, BBBAddress address){
		if(null == this.newAddressMap){
			this.newAddressMap = new HashMap<String, BBBAddress>();
		}
		newAddressMap.put(addressKey, address);
	}


	public String getNewAddressKey() {
		return newAddressKey;
	}


	public void setNewAddressKey(String newAddressKey) {
		this.newAddressKey = newAddressKey;
	}
	
	
}
