package com.bbb.internationalshipping.vo.orderconfirmation;

import com.bbb.framework.integration.ServiceResponseBase;

/**
 *
 */

public class BBBInternationalShippingOrderConfResponse extends ServiceResponseBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private boolean isConfirmed;

	/**
	 * @return the isConfirmed
	 */
	public final boolean isConfirmed() {
		return isConfirmed;
	}

	/**
	 * @param isConfirmed the isConfirmed to set
	 */
	public final void setConfirmed(boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}
	
	
	
}
