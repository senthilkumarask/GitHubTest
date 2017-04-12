package com.bbb.commerce.inventory.vo;

import java.io.Serializable;

/**
 * @author pku104
 *
 */
public class SBInventoryRequestVO implements Serializable {
	private static final long serialVersionUID = -547727319607411957L;
	private InventoryRequestVO availabilityRequest;
	/**
	 * @return the availabilityRequest
	 */
	public InventoryRequestVO getAvailabilityRequest() {
		return availabilityRequest;
	}

	/**
	 * @param availabilityRequest the availabilityRequest to set
	 */
	public void setAvailabilityRequest(
			InventoryRequestVO availabilityRequest) {
		this.availabilityRequest = availabilityRequest;
	}
}
