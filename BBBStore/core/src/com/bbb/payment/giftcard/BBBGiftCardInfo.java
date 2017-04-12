/**
 * 
 */
package com.bbb.payment.giftcard;

import java.io.Serializable;

/**
 * @author vagra4
 *
 */
public interface BBBGiftCardInfo extends Serializable {

	public abstract String getCardNumber();
	public abstract String getPin();
	public abstract Double getBalance();
}
