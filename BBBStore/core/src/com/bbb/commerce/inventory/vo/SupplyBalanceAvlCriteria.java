package com.bbb.commerce.inventory.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author pku104
 *
 */
public class SupplyBalanceAvlCriteria implements Serializable {

	private static final long serialVersionUID = 902892082855856422L;
	private Map<String, List<String>> itemNames;
	private Map<String, List<String>> facilityNames;
	/**
	 * @return the itemNames
	 */
	public  Map<String, List<String>> getItemNames() {
		return itemNames;
	}
	/**
	 * @param itemNames the itemNames to set
	 */
	public void setItemNames(Map<String, List<String>> itemNames) {
		this.itemNames = itemNames;
	}
	/**
	 * @return the facilityNames
	 */
	public Map<String, List<String>> getFacilityNames() {
		return facilityNames;
	}
	/**
	 * @param facilityNames the facilityNames to set
	 */
	public void setFacilityNames(Map<String, List<String>> facilityNames) {
		this.facilityNames = facilityNames;
	}
}
