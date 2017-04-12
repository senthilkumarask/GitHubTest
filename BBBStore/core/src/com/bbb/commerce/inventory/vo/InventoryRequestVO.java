package com.bbb.commerce.inventory.vo;

import java.io.Serializable;

/**
 * @author pku104
 *
 */
public class InventoryRequestVO implements Serializable {

	private static final long serialVersionUID = 2800852995016766720L;
	private String viewName;
	private SupplyBalanceAvlCriteria availabilityCriteria;
	/**
	 * @return the viewName
	 */
	public String getViewName() {
		return viewName;
	}

	/**
	 * @param viewName the viewName to set
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * @return the availabilityCriteria
	 */
	public SupplyBalanceAvlCriteria getAvailabilityCriteria() {
		return availabilityCriteria;
	}

	/**
	 * @param avc the availabilityCriteria to set
	 */
	public void setAvailabilityCriteria(SupplyBalanceAvlCriteria avc) {
		this.availabilityCriteria = avc;
	}
}
