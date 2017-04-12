package com.bbb.commerce.common;

import atg.payment.creditcard.CreditCardInfo;

public interface BBBCreditCardInfo extends CreditCardInfo {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/payment/creditcard/StoreCreditCardInfo.java#2 $$Change: 633752 $";

  /**
   * @return the card's verification number.
   */
  public String getCardVerificationNumber();

  /**
   * @param pCardVerificationNumber - card verification number.
   */
  public void setCardVerificationNumber(String pCardVerificationNumber);
}
