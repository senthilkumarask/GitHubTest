package com.bbb.commerce.inventory.vo;

import java.io.Serializable;

/**
 * @author pku104
 *
 */
public class SBInventoryResponseVO implements Serializable {

	private static final long serialVersionUID = 5951505730861298544L;
	private InventoryResponseVO availability;

	/**
	 * @return
	 */
	public InventoryResponseVO getAvailability() {
		return availability;
	}

	/**
	 * @param availability
	 */
	public void setAvailability(InventoryResponseVO availability) {
		this.availability = availability;
	}

}
