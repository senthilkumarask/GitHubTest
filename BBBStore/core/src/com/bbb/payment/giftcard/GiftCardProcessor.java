package com.bbb.payment.giftcard;

/**
 * GiftCard processor to perform auth/redeem calls.
 * 
 * @author vagra4
 * 
 */
public interface GiftCardProcessor {

	/**
	 * This method redeems a gift card. Before redeem this method validates gift
	 * card balance.
	 * 
	 * @param giftCertificateInfo
	 * @return
	 */
	public abstract GiftCardStatus authorize(
			GenericGiftCardInfo giftcertificateinfo);
	
	/**
	 * This method sends gift card status for dummy order. 
	 * 
	 * @param giftCertificateInfo
	 * @return
	 */
	public abstract GiftCardStatus authorizeDummyOrderGiftCard(
			GenericGiftCardInfo giftcertificateinfo);

}