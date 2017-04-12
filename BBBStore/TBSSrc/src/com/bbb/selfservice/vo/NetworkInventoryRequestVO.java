package com.bbb.selfservice.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class NetworkInventoryRequestVO  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6501255763498237998L;

	private NICAvailabilityRequestVO availabilityRequest;

	public NICAvailabilityRequestVO getAvailabilityRequest() {
		return availabilityRequest;
	}

	public void setAvailabilityRequest(NICAvailabilityRequestVO availabilityRequest) {
		this.availabilityRequest = availabilityRequest;
	}
	
	
}
