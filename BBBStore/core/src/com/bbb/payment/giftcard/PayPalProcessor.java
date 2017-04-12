package com.bbb.payment.giftcard;

import com.bbb.commerce.order.PaypalBeanInfo;
import com.bbb.commerce.order.PaypalStatus;

/**
 * Paypal processor to perform doAuth/doExp calls.
 * 
 * @author ssh108
 * 
 */
public interface PayPalProcessor {

	/**
	 * 
	 * @param payPalcertificateinfo
	 * @return
	 */
	public abstract PaypalStatus authorize(
			PaypalBeanInfo payPalcertificateinfo);

}