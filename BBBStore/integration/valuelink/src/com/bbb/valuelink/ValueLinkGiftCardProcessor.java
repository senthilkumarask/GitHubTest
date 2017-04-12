package com.bbb.valuelink;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.payment.giftcard.GiftCardBeanInfo;

/**
 * This interface class GiftCardProcessor exposes methods related to GiftCard to
 * the client.
 * 
 * @author vagra4
 * 
 */
public interface ValueLinkGiftCardProcessor {

	/**
	 * This method authorizes the gift card and returns the balance amount of
	 * Gift Card.
	 * 
	 * 2400 call
	 * 
	 * @param pPinNo
	 * @param pOrderID
	 * @param pPaymentGroupId
	 * @param pSite
	 * @return GiftCardBeanInfo
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public GiftCardBeanInfo getBalance(String pGiftCardNo, String pPinNo,
			String pOrderID, String pPaymentGroupId,
			String pSite)
			throws BBBSystemException, BBBBusinessException;

	/**
	 * This method authorizes the gift card and redeems gift card in ValuLink.
	 * <br>2202 call
	 * @param pPinNo
	 * @param pRedemptionAmount
	 * @param pOrderID
	 * @param pPaymentGroupId
	 * 
	 * @return GiftCardBeanInfo
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public GiftCardBeanInfo redeem(String pGiftCardNo, String pPinNo,
			String pRedemptionAmount, String pOrderID, String pPaymentGroupId,
			String pSite)
			throws BBBSystemException, BBBBusinessException;

	/**
	 * This method roll back the previous redeem call.
	 * @param pPinNo
	 * 
	 * 2800 call
	 * 
	 * @return GiftCardBeanInfo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo redeemRollback(String pGiftCardNo, String pPinNo,
			String pRedemptionAmount, String pOrderID, String pPaymentGroupId,
			String pSite)
			throws BBBSystemException, BBBBusinessException;
	
	/**
	 * This method does a call to Value link in case of any error .
	 * @param pPinNo
	 * 
	 * 0704 call
	 * 
	 * @return GiftCardBeanInfo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo timeoutReversal(String pGiftCardNo, String pPinNo,
			String pRedemptionAmount, String pOrderID, String pPaymentGroupId,
			String pSite)
			throws BBBSystemException, BBBBusinessException;
	
	/**
	 * This method does a request Working Key call to Value link.
	 * @param pPinNo
	 * 
	 * 2010 call
	 * 
	 * @return GiftCardBeanInfo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public GiftCardBeanInfo requestWorkingKey(String pGiftCardNo, String pPinNo,
			String pRedemptionAmount, String pOrderID, String pPaymentGroupId,
			String pSite)
			throws BBBSystemException, BBBBusinessException;
	
	public GiftCardBeanInfo processResponse(String balanceResPayload,
			String pGiftCardNo, String pPinNo) 
			throws BBBSystemException, BBBBusinessException;
	
	public GiftCardBeanInfo processResponse(String balanceResPayload) 
			throws BBBSystemException, BBBBusinessException;

}
