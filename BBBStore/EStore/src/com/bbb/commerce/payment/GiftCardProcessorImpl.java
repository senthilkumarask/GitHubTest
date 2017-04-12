/**
 *
 */
package com.bbb.commerce.payment;


import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.payment.giftcard.GenericGiftCardInfo;
import com.bbb.payment.giftcard.GiftCardBeanInfo;
import com.bbb.payment.giftcard.GiftCardProcessor;
import com.bbb.payment.giftcard.GiftCardStatus;
import com.bbb.payment.giftcard.GiftCardStatusImpl;
import com.bbb.utils.BBBUtility;
import com.bbb.valuelink.ValueLinkGiftCardProcessor;
import com.bbb.valuelink.ValueLinkGiftCardUtil;

/** This GiftCardProcessor redeems user entered gift card during payment authorization call.
 * 
 * @author vagra4 */
public class GiftCardProcessorImpl extends BBBGenericService implements GiftCardProcessor {

    private static final String P_GIFTCERTIFICATE_INFO = ", pGiftcertificateInfo: ";
    private static final String GIFT_CARD = "GiftCard[";
    private static final int CREDIT_CARD_DIGIT = 4;

    private LblTxtTemplateManager messageHandler;
    private ValueLinkGiftCardUtil valueLinkGiftCardUtil;
    private ValueLinkGiftCardProcessor valueLinkGiftCardProcessor;

    /** This method redeems a gift card. Before redeem this method validates gift card balance.
     * 
     * @param pGiftCardInfo
     * @return */
    @Override
    public final GiftCardStatus authorize(final GenericGiftCardInfo pGiftCardInfo) {

        this.logDebug("Starting method GiftCardProcessorImpl.authorize, giftCertificateInfo: " + pGiftCardInfo);
		
        GiftCardStatus giftCardStatus = null;
        GiftCardBeanInfo giftCardBeanInfo = null;
        boolean error = false;
        String errorMsg = null;
        try {
            // Doing redeem call.
            giftCardBeanInfo = this.getValueLinkGiftCardProcessor().redeem(pGiftCardInfo.getGiftCardNumber(),
                    pGiftCardInfo.getPin(),
                    this.getValueLinkGiftCardUtil().getAmountInVLFormat(pGiftCardInfo.getAmount()),
                    pGiftCardInfo.getOrderID(), null, pGiftCardInfo.getSiteId());

            error = this.verifyVLCallResponse(giftCardBeanInfo);
            final String cardNo = pGiftCardInfo.getGiftCardNumber();

            if (error) {
                errorMsg = this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_RED_CALL,
                        BBBCheckoutConstants.US_LOC, null, null);

                if (cardNo != null) {
                    this.logInfo(GIFT_CARD + cardNo.substring(cardNo.length() - CREDIT_CARD_DIGIT, cardNo.length())
                            + "] REDEEM call FAIL during checkout");
                }
            } else {
                // When redeem call is success full checking redemption amount. */
                final double amountRedeemed = BBBUtility
                        .round(Double.parseDouble(giftCardBeanInfo.getPreviousBalance())
                                - Double.parseDouble(giftCardBeanInfo.getBalance()));

                final double redeemAmountRequired = BBBUtility.round(pGiftCardInfo.getAmount());

                if (Double.compare(amountRedeemed, redeemAmountRequired) != BBBCoreConstants.ZERO) {
                    if (cardNo != null) {
                        this.logInfo(GIFT_CARD
                                + cardNo.substring(cardNo.length() - CREDIT_CARD_DIGIT, cardNo.length())
                                + "] REDEEM call FAIL(Amount redemmed by value link is not equal to the amount required"
                                + " for redemption) during checkout \nAmount redemmed by ValueLink " + amountRedeemed
                                + "\nAmount required for redeem " + redeemAmountRequired
                                + "\nCalling RedeemRollback......");
                    }
                    // Doing redeem roll back.
                    giftCardBeanInfo = this.getValueLinkGiftCardProcessor().redeemRollback(
                            pGiftCardInfo.getGiftCardNumber(), pGiftCardInfo.getPin(),
                            this.getValueLinkGiftCardUtil().getAmountInVLFormat(amountRedeemed),
                            pGiftCardInfo.getOrderID(), null, pGiftCardInfo.getSiteId());

                    //Since redeem roll back need to roll back order also.
                    error = true;
                } else {
                    if (cardNo != null) {
                        this.logInfo(GIFT_CARD
                                + cardNo.substring(cardNo.length() - CREDIT_CARD_DIGIT, cardNo.length())
                                + "] REDEEM call SUCCESS during checkout");

                    }
                }
            }

        } catch (final BBBSystemException systemException) {
            this.logError("Error Occured while process the request: ", systemException);
            error = true;
            errorMsg = systemException.getMessage();

        } catch (final BBBBusinessException businessException) {
            this.logError("Error Occured while processing the request: ", businessException);
            error = true;
            errorMsg = businessException.getMessage();
        }

        if (error) {
            giftCardStatus = this.errorStatus(giftCardBeanInfo, pGiftCardInfo, errorMsg);
        } else {
            giftCardStatus = this.successStatus(giftCardBeanInfo, pGiftCardInfo);
        }

        
        this.logDebug("Exiting method GiftCardProcessorImpl.authorize, giftCardStatus: " + giftCardStatus);

        return giftCardStatus;
    }
    
    
    /** This method redeems a gift card. Before redeem this method validates gift card balance.
     * 
     * @param pGiftCardInfo
     * @return */
    @Override
    public final GiftCardStatus authorizeDummyOrderGiftCard(final GenericGiftCardInfo pGiftCardInfo) {

        this.logDebug("Starting method GiftCardProcessorImpl.authorize, giftCertificateInfo: " + pGiftCardInfo);
		
        GiftCardStatus giftCardStatus = null;
        GiftCardBeanInfo giftCardBeanInfo = new GiftCardBeanInfo();
        boolean error = false;
        String errorMsg = null;
        try {
            // setting dummy giftcardInfo bean for dummy order.
            giftCardBeanInfo.setAuthCode("testOrder123");
            giftCardBeanInfo.setAuthRespCode("00");
            giftCardBeanInfo.setBalance(this.getValueLinkGiftCardUtil().getAmountInVLFormat(pGiftCardInfo.getAmount()));
            giftCardBeanInfo.setCardClass("0");
            giftCardBeanInfo.setGiftCardId(pGiftCardInfo.getGiftCardNumber());
            giftCardBeanInfo.setPreviousBalance(this.getValueLinkGiftCardUtil().getAmountInVLFormat(pGiftCardInfo.getAmount()));
            giftCardBeanInfo.setTraceNumber("testOrders123");
            giftCardBeanInfo.setExceptionStatus(false);
            giftCardBeanInfo.setPin(pGiftCardInfo.getPin());
            giftCardBeanInfo.setStatus(true);
            giftCardBeanInfo.setTimeoutReversal(false);
            error = this.verifyVLCallResponse(giftCardBeanInfo);
            final String cardNo = pGiftCardInfo.getGiftCardNumber();

            if (error) {
                errorMsg = this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_RED_CALL,
                        BBBCheckoutConstants.US_LOC, null, null);

                if (cardNo != null) {
                    this.logInfo(GIFT_CARD + cardNo.substring(cardNo.length() - CREDIT_CARD_DIGIT, cardNo.length())
                            + "] REDEEM call FAIL during checkout");
                }
            }
//            } else {
//                // When redeem call is success full checking redemption amount. */
//                final double amountRedeemed = BBBUtility
//                        .round(Double.parseDouble(giftCardBeanInfo.getPreviousBalance())
//                                - Double.parseDouble(giftCardBeanInfo.getBalance()));
//
//                final double redeemAmountRequired = BBBUtility.round(pGiftCardInfo.getAmount());
//
//                if (amountRedeemed != redeemAmountRequired) {
//                    if (cardNo != null) {
//                        this.logInfo(GIFT_CARD
//                                + cardNo.substring(cardNo.length() - CREDIT_CARD_DIGIT, cardNo.length())
//                                + "] REDEEM call FAIL(Amount redemmed by value link is not equal to the amount required"
//                                + " for redemption) during checkout \nAmount redemmed by ValueLink " + amountRedeemed
//                                + "\nAmount required for redeem " + redeemAmountRequired
//                                + "\nCalling RedeemRollback......");
//                    }
//                    // Doing redeem roll back.
//                    giftCardBeanInfo = this.getValueLinkGiftCardProcessor().redeemRollback(
//                            pGiftCardInfo.getGiftCardNumber(), pGiftCardInfo.getPin(),
//                            this.getValueLinkGiftCardUtil().getAmountInVLFormat(amountRedeemed),
//                            pGiftCardInfo.getOrderID(), null, pGiftCardInfo.getSiteId());
//
//                    //Since redeem roll back need to roll back order also.
//                    error = true;
//                } else {
//                    if (cardNo != null) {
//                        this.logInfo(GIFT_CARD
//                                + cardNo.substring(cardNo.length() - CREDIT_CARD_DIGIT, cardNo.length())
//                                + "] REDEEM call SUCCESS during checkout");
//
//                    }
//                }
//            }

        } catch (final BBBSystemException systemException) {
            this.logError("Error Occured while process the request: ", systemException);
            error = true;
            errorMsg = systemException.getMessage();

        }

        if (error) {
            giftCardStatus = this.errorStatus(giftCardBeanInfo, pGiftCardInfo, errorMsg);
        } else {
            giftCardStatus = this.successStatus(giftCardBeanInfo, pGiftCardInfo);
        }

        
        this.logDebug("Exiting method GiftCardProcessorImpl.authorize, giftCardStatus: " + giftCardStatus);

        return giftCardStatus;
    }

    /** This method verifies Value link response error/success status.
     * 
     * @param giftCardBeanInfo
     * @param error
     * @return
     * @throws BBBSystemException */
    private boolean verifyVLCallResponse(final GiftCardBeanInfo giftCardBeanInfo) throws BBBSystemException {

        this.logDebug("Starting method GiftCardProcessorImpl.verifyVLCallResponse, giftCardBeanInfo: "
                + giftCardBeanInfo);

        boolean error = false;

        if ((giftCardBeanInfo == null) || (!giftCardBeanInfo.getStatus()) || (giftCardBeanInfo.getTimeoutReversal())) {
            error = true;
        }

        this.logDebug("Exiting method GiftCardProcessorImpl.verifyVLCallResponse, error: " + error);
        return error;
    }

    /** This method set error status for GiftCardStatus.
     * 
     * @param pGiftCardBeanInfo
     * @param pGiftcertificateInfo
     * @param pErrorMsg
     * @return */
    private GiftCardStatus errorStatus(final GiftCardBeanInfo pGiftCardBeanInfo,
            final GenericGiftCardInfo pGiftcertificateInfo, String pErrorMsg) {

        this.logDebug("Starting method GiftCardProcessorImpl.errorStatus, giftCardBeanInfo: " + pGiftCardBeanInfo
                + P_GIFTCERTIFICATE_INFO + pGiftcertificateInfo + ", pErrorMsg: " + pErrorMsg);

        if (pGiftCardBeanInfo.getTimeoutReversal()) {
            pErrorMsg = this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_TIMEOUT_REVERSAL_CALL,
                    BBBCheckoutConstants.US_LOC, null, null);
        }

        return new GiftCardStatusImpl(this.getNextTransactionId(), false, pErrorMsg, pGiftCardBeanInfo,
                pGiftcertificateInfo);
    }

    /** This method set success status for GiftCardStatus.
     * 
     * @param pGiftCardBeanInfo
     * @param pGiftcertificateInfo
     * @return */
    private GiftCardStatus successStatus(final GiftCardBeanInfo pGiftCardBeanInfo,
            final GenericGiftCardInfo pGiftcertificateInfo) {

        this.logDebug("Starting method GiftCardProcessorImpl.successStatus, giftCardBeanInfo: " + pGiftCardBeanInfo
                + P_GIFTCERTIFICATE_INFO + pGiftcertificateInfo);

        return new GiftCardStatusImpl(this.getNextTransactionId(), true, BBBCheckoutConstants.BLANK,
                pGiftCardBeanInfo, pGiftcertificateInfo);
    }

    /** This method returns timestamp of transaction.
     * 
     * @return */
    @SuppressWarnings ("static-method")
    private String getNextTransactionId() {
        return String.valueOf(System.currentTimeMillis());
    }

    /** @return the mValueLinkGiftCardProcessor */
    public final ValueLinkGiftCardProcessor getValueLinkGiftCardProcessor() {
        return this.valueLinkGiftCardProcessor;
    }

    /** @param pValueLinkGiftCardProcessor the mValueLinkGiftCardProcessor to set */
    public final void setValueLinkGiftCardProcessor(final ValueLinkGiftCardProcessor pValueLinkGiftCardProcessor) {
        this.valueLinkGiftCardProcessor = pValueLinkGiftCardProcessor;
    }

    /** @return the mValueLinkGiftCardUtil */
    public final ValueLinkGiftCardUtil getValueLinkGiftCardUtil() {
        return this.valueLinkGiftCardUtil;
    }

    /** @param pValueLinkGiftCardUtil the mValueLinkGiftCardUtil to set */
    public final void setValueLinkGiftCardUtil(final ValueLinkGiftCardUtil pValueLinkGiftCardUtil) {
        this.valueLinkGiftCardUtil = pValueLinkGiftCardUtil;
    }

    /** @return the lblTxtTemplateManager */
    public final LblTxtTemplateManager getMessageHandler() {
        return this.messageHandler;
    }

    /** @param messageHandler the lblTxtTemplateManager to set */
    public final void setMessageHandler(final LblTxtTemplateManager messageHandler) {
        this.messageHandler = messageHandler;
    }

}
