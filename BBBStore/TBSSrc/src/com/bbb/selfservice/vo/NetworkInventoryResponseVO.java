package com.bbb.selfservice.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

public class NetworkInventoryResponseVO  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4464998860394637573L;
	private NICAvailabiltyResponseVO availability;
	public NICAvailabiltyResponseVO getAvailability() {
		return availability;
	}
	public void setAvailability(NICAvailabiltyResponseVO availability) {
		this.availability = availability;
	}
}
