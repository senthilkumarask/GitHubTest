package com.bbb.valuelink;

import com.bbb.payment.giftcard.GiftCardBeanInfo;
import com.sapient.common.tests.BaseTestCase;

public class TestValueLinkGiftCardProcessor extends BaseTestCase {

	public void testGetBalance() throws Exception {
		ValueLinkGiftCardProcessor vlGiftCardProcessor = (ValueLinkGiftCardProcessor) getObject("VLGiftCardProcessor");
		String giftCardNo = (String) getObject("giftCardNo");
		String pinNo = (String) getObject("pinNo");
		String orderID = null;
		String paymentGrpID = null;
		String site = (String) getObject("siteId");

		GiftCardBeanInfo result = null;
		result = vlGiftCardProcessor.getBalance(giftCardNo, pinNo, orderID,
				paymentGrpID, site);

		assertNotNull("Value lilnk response fail", result);
		assertTrue(result.getStatus());
	}

	public void testRedeem() throws Exception {
		ValueLinkGiftCardProcessor vlGiftCardProcessor = (ValueLinkGiftCardProcessor) getObject("VLGiftCardProcessor");
		String giftCardNo = (String) getObject("giftCardNo");
		String pinNo = (String) getObject("pinNo");
		String orderID = null;
		String paymentGrpID = null;
		String site = (String) getObject("siteId");
		/**
		 * Redemption amount need to be keep minimum amount.
		 */
		String redeemAmount = "0500";

		GiftCardBeanInfo result = null;
		result = vlGiftCardProcessor.redeem(giftCardNo, pinNo, redeemAmount,
				orderID, paymentGrpID, site);

		assertNotNull("Value lilnk response fail", result);
	}

	public void testRedeemRollBack() throws Exception {
		ValueLinkGiftCardProcessor vlGiftCardProcessor = (ValueLinkGiftCardProcessor) getObject("VLGiftCardProcessor");
		String giftCardNo = (String) getObject("giftCardNo");
		String pinNo = (String) getObject("pinNo");
		String orderID = null;
		String paymentGrpID = null;
		String site = (String) getObject("siteId");
		/**
		 * Redemption amount need to be keep minimum amount.
		 */
		String redeemAmount = "0500";

		GiftCardBeanInfo result = null;
		result = vlGiftCardProcessor.redeemRollback(giftCardNo, pinNo, redeemAmount,
				orderID, paymentGrpID, site);

		assertNotNull("Value lilnk response fail", result);
	}
	
}
