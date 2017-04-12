package com.bbb.commerce.common;

import java.io.Serializable;

/**
 * This is a custom class for express checkout details.
 * 
 * @author pbhar4
 * @version $Revision: #1 $
 */

public class ExpressCheckoutVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6854932752014179453L;
	private boolean expressCheckoutEnabled;
	private boolean checkRestrictedZip;

	public boolean isExpressCheckoutEnabled() {
		return expressCheckoutEnabled;
	}

	public void setExpressCheckoutEnabled(boolean expressCheckoutEnabled) {
		this.expressCheckoutEnabled = expressCheckoutEnabled;
	}

	public boolean isCheckRestrictedZip() {
		return checkRestrictedZip;
	}

	public void setCheckRestrictedZip(boolean checkRestrictedZip) {
		this.checkRestrictedZip = checkRestrictedZip;
	}
}
