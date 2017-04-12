package com.bbb.commerce.order;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.PaymentGroupNotFoundException;
import atg.commerce.pricing.PricingConstants;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;

import com.bbb.account.BBBGetCouponsManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.paypal.PayerInfoVO;
import com.bbb.utils.BBBUtility;


public class BBBPaymentGroupManager extends PaymentGroupManager {

    private static final String MAX_GC_FLAG = ", maxGCFlag:";
    private static final String ORDER_AMT_COVERED_FLAG = ", orderAmtCoveredFlag:";
    private static final String GIFT_CARD_FLAG = "giftCardFlag:";
    private static final String USER_HAS_ADDEDD_MAXIMUM = "User has addedd maximum:";
    private static final String P_ORDER = "pOrder:";
    private static final String EXITING_METHOD_AMOUNT_COVERED_BY_GIFT_CARD =
            "Exiting method BBBPaymentGroupManager.getAmountcoveredByGiftCard, Output params:::  ";
    private static final String GIFT_CARDS_MAXIMUM_GIFT_CARD_ADDITION_LIMIT_REACHED =
            " gift cards, !!!!!!! Maximum gift card addition limit reached !!!!!!!!";
    private static final String GIFT_CARD_TOTAL_BALANCE_ORDER_TOTAL_AMOUNT =
            "giftCardTotalBalance > orderTotalAmount";

    private static final int CREDIT_CARD_DIGITS = 4;

    private TransactionManager transactionManager;
    private BBBOrderManager orderManager;
    private String maxGiftCardLimit;
    private ClientLockManager localLockManager;
    private LblTxtTemplateManager lblTxtTemplateManager;
    private BBBGetCouponsManager bbbGetCouponsManager;

    public BBBGetCouponsManager getBbbGetCouponsManager() {
    	return bbbGetCouponsManager;
    }

    public void setBbbGetCouponsManager(BBBGetCouponsManager bbbGetCouponsManager) {
    	this.bbbGetCouponsManager = bbbGetCouponsManager;
    }

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/** Default Constructor.*/
    public BBBPaymentGroupManager() {
        super();
    }
    
    /**
	 * @return the localLockManager
	 */
	public ClientLockManager getLocalLockManager() {
		return this.localLockManager;
	}

	/**
	 * @param localLockManager the localLockManager to set
	 */
	public void setLocalLockManager(ClientLockManager localLockManager) {
		this.localLockManager = localLockManager;
	}
	
    /** This method creates a Payment Group of type GiftCard.
     * 
     * @param pOrder Order
     * @param pGiftCardBalance Gift Card Balance
     * @param pGiftCardNumber Gift Card Number
     * @param pGiftCardPin Gift Card Pin
     * @return Gift Card
     * @throws CommerceException Exception*/
    public final BBBGiftCard createGiftCardPaymentGroup(final Order pOrder, final String pGiftCardBalance,
            final String pGiftCardNumber, final String pGiftCardPin) throws CommerceException {

        this.logDebug("Starting method BBBPaymentGroupManager.createGiftCardPaymentGroup, Input params::: " + P_ORDER
                + pOrder + "pGiftCardBalance:" + pGiftCardBalance + "pGiftCardNumber:" + pGiftCardNumber
                + "pGiftCardPin:" + pGiftCardPin);

        BBBGiftCard giftCard = null;

        final double giftCardBalance = BBBUtility.round(Double.parseDouble(pGiftCardBalance));
        final double orderTotal = BBBUtility.round(((BBBOrderPriceInfo) pOrder.getPriceInfo()).getTotal());
        final double giftCardTotalAmount = this.getGiftCardTotalAmount(pOrder);

        final PaymentGroup paymentGroup = this.createPaymentGroup(BBBCheckoutConstants.GIFTCARD);
        if (paymentGroup instanceof BBBGiftCard) {
            giftCard = (BBBGiftCard) paymentGroup;
        }
        
        if(giftCard != null){
        	giftCard.setBalance(Double.valueOf(giftCardBalance));
            giftCard.setPin(pGiftCardPin);
            giftCard.setCardNumber(pGiftCardNumber);	
        }

        double remainingAmt = orderTotal - giftCardTotalAmount;
        remainingAmt = BBBUtility.round(remainingAmt);

        // Adding amount also for Giftcard this makes calculation of remaining amount on payment page simpler else
        // we've to prepare final complete data in a Droplet. */
        double amount = 0.0;
        if (giftCardBalance >= remainingAmt) {
            amount = remainingAmt;
        } else {
            amount = giftCardBalance;
        }
        if(giftCard != null){
        	giftCard.setAmount(amount);	
        }

        this.logDebug("Exiting method BBBPaymentGroupManager.createGiftCardPaymentGroup returned param:::   giftCard:"
                + giftCard);

        return giftCard;
    }

    /** This method calculates total gift card amount.
     * 
     * @param order order
     * @return */
    public double getGiftCardTotalAmount(final Order order) {

        this.logDebug("Starting method BBBPaymentGroupManager.getGiftCardTotalAmount, Input params::: " + P_ORDER
                + order);

        @SuppressWarnings ("unchecked")
        final List<PaymentGroup> giftCardPayGrps = order.getPaymentGroups();
        double totalGCAmt = 0.0;
        if (giftCardPayGrps != null) {
            for (final PaymentGroup payGroup : giftCardPayGrps) {
                if (payGroup instanceof BBBGiftCard) {
                    final BBBGiftCard giftPayGrp = (BBBGiftCard) payGroup;
                    totalGCAmt += giftPayGrp.getAmount();
                }
            }
        }

        this.logDebug("Exiting method BBBPaymentGroupManager.getGiftCardTotalAmount, Output params::: " + "totalGCAmt:"
                + totalGCAmt);

        return BBBUtility.round(totalGCAmt);
    }

    /** This method create or update credit card paymentGroup.
     * 
     * @param order Order
     * @param creditCardInfo Credit Card Information
     * @return PaymentGroup Payment Group
     * @throws IntrospectionException */
    @SuppressWarnings("unchecked")
	public PaymentGroup createOrUpdateCreditCardPaymentGroup(final Order order,
            final BasicBBBCreditCardInfo creditCardInfo) throws IntrospectionException {

        this.logDebug("Starting method BBBPaymentGroupManager.createOrUpdateCreditCardPaymentGroup, Input params::: "
                + "order:" + order + "creditCardInfo:" + creditCardInfo);

        final List<PaymentGroup> paymentGroups = order.getPaymentGroups();
        CreditCard creditCard = null;

        for (final PaymentGroup paymentGroup : paymentGroups) {

            if (paymentGroup instanceof BBBCreditCard) {
                creditCard = (CreditCard) paymentGroup;
            }
        }
        this.copyCreditCardInfo(creditCard, creditCardInfo, order);
        return creditCard;
    }

    private void copyCreditCardInfo(final CreditCard creditCard, final BasicBBBCreditCardInfo creditCardInfo,
            final Order order) throws IntrospectionException {

        this.logDebug("Starting method BBBPaymentGroupManager.copyCreditCardInfo, Input params::: ");

        if ((creditCard != null) && (creditCardInfo != null)) {
            creditCard.setCardVerficationNumber(creditCardInfo.getCardVerificationNumber());

            final Address billingAddress = creditCard.getBillingAddress();
            AddressTools.copyAddress(((BBBOrder) order).getBillingAddress(), billingAddress);

            creditCard.setBillingAddress(billingAddress);

            creditCard.setCreditCardNumber(creditCardInfo.getCreditCardNumber());
            creditCard.setCreditCardType(creditCardInfo.getCreditCardType());
            creditCard.setExpirationDayOfMonth(creditCardInfo.getExpirationDayOfMonth());
            creditCard.setExpirationMonth(creditCardInfo.getExpirationMonth());
            creditCard.setExpirationYear(creditCardInfo.getExpirationYear());
            ((BBBCreditCard) creditCard).setNameOnCard(creditCardInfo.getNameOnCard());

            final String cardNumber = creditCardInfo.getCreditCardNumber();
            ((BBBCreditCard) creditCard).setLastFourDigits(cardNumber.substring(cardNumber.length()
                    - CREDIT_CARD_DIGITS, cardNumber.length()));
        }

        this.logDebug("Exiting method BBBPaymentGroupManager.createOrUpdateCreditCardPaymentGroup, output::: "
                + "creditCard:" + creditCard);
    }

    /** This method returns Payment group of type Giftcard.
     * 
     * @param order Order
     * @param paymentGroupType Payment Group Type
     * @return Payment Groups */
    @SuppressWarnings("unchecked")
	public List<PaymentGroup> getPaymentGroups(final Order order, final String paymentGroupType) {

        this.logDebug("Starting method BBBPaymentGroupManager.getPaymentGroup, Input params::: " + P_ORDER + order
                + "pPaymentGroupType:" + paymentGroupType);

        final List<PaymentGroup> returnedGroups = new ArrayList<PaymentGroup>();

        List<PaymentGroup> paymentGroup = null;

        if (order != null) {
            paymentGroup = order.getPaymentGroups();
        }

        if (paymentGroupType == null) {
            this.logDebug("Since input param pPaymentGroupType is null Returning All payment groups");
            returnedGroups.addAll(paymentGroup);
        } else if (paymentGroup != null) {
            for (final PaymentGroup paymentGrp : paymentGroup) {

                if ((paymentGrp instanceof BBBGiftCard)
                        && (BBBCheckoutConstants.GIFTCARD.equalsIgnoreCase(paymentGroupType))) {

                    this.logDebug("GiftCard");

                    returnedGroups.add(paymentGrp);
                }
                if ((paymentGrp instanceof CreditCard)
                        && (BBBCheckoutConstants.CREDITCARD.equalsIgnoreCase(paymentGroupType))) {

                    this.logDebug("CreditCard");

                    returnedGroups.add(paymentGrp);
                }
            }
        }

        this.logDebug("Exiting method BBBPaymentGroupManager.getPaymentGroup, Output params::: " + "returnedGroups:"
                + returnedGroups);

        return returnedGroups;
    }

    /** <I>On Payment Page load this method does following</I>
     * <ul>
     * <li>If a Gift card is already present.</li>
     * <li>Checks that order total got covered by available gift cards or not.</li>
     * <li>Total Gift card added by user is within allowed limit</li>
     * </ul>
     * 
     * @param pOrder
     * @return Payment Groups Status
     * @throws CommerceException */
    @SuppressWarnings("unchecked")
	public Boolean[] processPaymentGroupStatusOnLoad(final Order pOrder) throws CommerceException {

        this.logDebug("Starting method BBBPaymentGroupManager.processPaymentGroupStatusOnLoad, Input params::: "
                + P_ORDER + pOrder);

        this.removeExtraGiftCard(pOrder);
        this.resetAmountCoveredByGiftCard(pOrder);

        double orderTotalAmount = 0.0;
        if(pOrder != null){
        	orderTotalAmount = BBBUtility.round(((BBBOrderPriceInfo) pOrder.getPriceInfo()).getTotal()) ;
        }

        double gcTotalAmount = 0.0;
        int gcCount = 0;
        boolean giftCardFlag = false;
        boolean orderAmtCoveredFlag = false;
        boolean maxGCFlag = false;

        List<PaymentGroup> paymentGroup = null;
        if (pOrder != null) {
            paymentGroup = pOrder.getPaymentGroups();
        }

        if ((null != pOrder) && !BBBCheckoutConstants.BOPUS_ONLY.equals(((BBBOrder) pOrder).getOnlineBopusItemsStatusInOrder())) {

            if (paymentGroup != null) {
                for (final PaymentGroup paymentGrp : paymentGroup) {
                    if (paymentGrp instanceof BBBGiftCard) {
                        final BBBGiftCard giftCard = (BBBGiftCard) paymentGrp;
                        this.logDebug("GiftCard Found");
                        giftCardFlag = true;
                        gcTotalAmount = giftCard.getAmount() + gcTotalAmount;
                        gcCount++;
                    }
                }
            }

            gcTotalAmount = BBBUtility.round(gcTotalAmount);

            if (giftCardFlag && (gcTotalAmount >= orderTotalAmount)) {
                this.logDebug(GIFT_CARD_TOTAL_BALANCE_ORDER_TOTAL_AMOUNT);
                orderAmtCoveredFlag = true;

                // Removing all the CreditCard payment groups if available in case of Order total covered by gift cards
                this.removePaymentGroups(pOrder, BBBCheckoutConstants.CREDITCARD);

            }

            if (giftCardFlag && (gcCount >= Integer.parseInt(this.getMaxGiftCardLimit()))) {
                this.logDebug(USER_HAS_ADDEDD_MAXIMUM + this.getMaxGiftCardLimit()
                        + GIFT_CARDS_MAXIMUM_GIFT_CARD_ADDITION_LIMIT_REACHED);
                maxGCFlag = true;
            }

        } else if (null != pOrder && BBBCheckoutConstants.BOPUS_ONLY.equals(((BBBOrder) pOrder).getOnlineBopusItemsStatusInOrder())) {
            // Removing all the GiftCard payment groups if available in case of BOPUS only Order
            this.logDebug("BOPUS ONLY ORDER FOUND");
            this.removePaymentGroups(pOrder, BBBCheckoutConstants.GIFTCARD);
        }

        this.logDebug("Exiting method BBBPaymentGroupManager.processPaymentGroupStatusOnLoad, Output params::: "
                + GIFT_CARD_FLAG + giftCardFlag + ORDER_AMT_COVERED_FLAG + orderAmtCoveredFlag + MAX_GC_FLAG
                + maxGCFlag);

        return new Boolean[] { Boolean.valueOf(giftCardFlag), Boolean.valueOf(orderAmtCoveredFlag),
                Boolean.valueOf(maxGCFlag), };
    }

    /** <ul>
     * <li>Checking If a Gift card is already present.</li>
     * <li>Total Gift card added by user is within allowed limit</li>
     * </ul>
     * 
     * @param pOrder order
     * @return Success / Failure
     * @throws CommerceException Exception*/
    @SuppressWarnings("unchecked")
	public final boolean isGiftCardMaxReachedAPI(final Order pOrder) throws CommerceException {

        this.logDebug("Starting method BBBPaymentGroupManager.isGiftCardMaxReachedAPI, Input params::: " + P_ORDER
                + pOrder);

        List<PaymentGroup> paymentGroup = null;

        boolean giftCardFlag = false;

        boolean maxGCFlag = false;
        int gcCount = 0;

        if (pOrder != null) {
            paymentGroup = pOrder.getPaymentGroups();
        }

        if (pOrder != null && !BBBCheckoutConstants.BOPUS_ONLY.equals(((BBBOrder) pOrder).getOnlineBopusItemsStatusInOrder())) {

            if (paymentGroup != null) {
                for (final PaymentGroup paymentGrp : paymentGroup) {
                    if (paymentGrp instanceof BBBGiftCard) {
                        giftCardFlag = true;
                        gcCount++;
                    }
                }
            }

            if (giftCardFlag && (gcCount >= Integer.parseInt(this.getMaxGiftCardLimit()))) {
                this.logDebug(USER_HAS_ADDEDD_MAXIMUM + this.getMaxGiftCardLimit()
                        + GIFT_CARDS_MAXIMUM_GIFT_CARD_ADDITION_LIMIT_REACHED);
                maxGCFlag = true;
            }

        }
        this.logDebug("Exiting method BBBPaymentGroupManager.isGiftCardMaxReached, Output params::: " + GIFT_CARD_FLAG
                + giftCardFlag + MAX_GC_FLAG + maxGCFlag);
        return maxGCFlag;
    }

    /** This method checks if order total is covered by gift card or not.
     * 
     * @param order Order
     * @return Order Covered by Gift Card*/
    @SuppressWarnings("unchecked")
	public final boolean isOrderAmountCoveredByGC(final Order order) {
        this.logDebug("Starting method BBBPaymentGroupManager.isOrderAmountCoveredByGC, Input params::: " + P_ORDER
                + order);
        double orderTotalAmount = 0.0; 
        boolean orderAmtCoveredFlag = false;
        if(order != null){
        	orderTotalAmount = BBBUtility.round(((BBBOrderPriceInfo) order.getPriceInfo()).getTotal());

        double gcTotalAmount = 0;
       

        List<PaymentGroup> paymentGroup = null;
        paymentGroup = order.getPaymentGroups();

        if (!BBBCheckoutConstants.BOPUS_ONLY.equals(((BBBOrder) order).getOnlineBopusItemsStatusInOrder())) {

            if (paymentGroup != null) {
                for (final PaymentGroup paymentGrp : paymentGroup) {
                    if (paymentGrp instanceof BBBGiftCard) {
                        final BBBGiftCard giftCard = (BBBGiftCard) paymentGrp;
                        gcTotalAmount = giftCard.getAmount() + gcTotalAmount;
                    }
                }
            }

            gcTotalAmount = BBBUtility.round(gcTotalAmount);

            if (gcTotalAmount >= orderTotalAmount) {
                this.logDebug(GIFT_CARD_TOTAL_BALANCE_ORDER_TOTAL_AMOUNT);
                orderAmtCoveredFlag = true;
            }
        }
    }
        this.logDebug("Exiting method BBBPaymentGroupManager.isOrderAmountCoveredByGC, Output params::: "
                + ORDER_AMT_COVERED_FLAG + orderAmtCoveredFlag);

        return orderAmtCoveredFlag;
    }

    /** This methods removes extra gift card and reset gift card amount if user has removed some item from cart after
     * payment group creation.
     * 
     * @param pOrder order
     * @throws CommerceException */
    @SuppressWarnings("unchecked")
	public final void removeExtraGiftCard(final Order pOrder) throws CommerceException {

        this.logDebug("Starting method BBBPaymentGroupManager.removeExtraGiftCard");

        final double orderTotalAmount = BBBUtility.round(((BBBOrderPriceInfo) pOrder.getPriceInfo()).getTotal());

        final double amtCvrdByGC = this.getGiftCardTotalAmount(pOrder);
        this.logDebug("orderTotalAmount: " + orderTotalAmount);
        List<PaymentGroup> validGiftCards = null;
        List<PaymentGroup> extraGiftCards = null;

        if (amtCvrdByGC > orderTotalAmount) {

            this.logDebug("!!!!      Total amount covered by gift card > ordertotal");
            validGiftCards = new ArrayList<PaymentGroup>();
            extraGiftCards = new ArrayList<PaymentGroup>();

            final List<PaymentGroup> giftCardPayGrps = pOrder.getPaymentGroups();

            double totalGCAmt = 0.0;

            int firstSufAmtGC = 0;
            boolean isFirstGCSuf = false;

            if (giftCardPayGrps != null) {

                for (final PaymentGroup payGroup : giftCardPayGrps) {
                    if (payGroup instanceof BBBGiftCard) {
                        final BBBGiftCard giftPayGrp = (BBBGiftCard) payGroup;
                        totalGCAmt += giftPayGrp.getAmount();
                        if (totalGCAmt <= orderTotalAmount) {
                            validGiftCards.add(payGroup);
                        } else {
                            extraGiftCards.add(payGroup);
                            if (firstSufAmtGC == 0) {
                                isFirstGCSuf = true;
                                break;
                            }
                        }
                        firstSufAmtGC++;
                    }
                }

                if (isFirstGCSuf) {
                    //If the first gift card has sufficient balance to cover order total then need to remove other gift
                    // cards
                    this.logDebug("!!!!!!!  First Gift card covers Order Total  !!!!!!!");
                    validGiftCards.add(extraGiftCards.remove(0));
                    for (final PaymentGroup payGroup : giftCardPayGrps) {
                        if (payGroup instanceof BBBGiftCard) {
                            final BBBGiftCard giftPayGrp = (BBBGiftCard) payGroup;
                            if (!giftPayGrp.getId().equals(validGiftCards.get(0).getId())) {
                                extraGiftCards.add(giftPayGrp);
                            }
                        }
                    }

                } else {
                    if (!validGiftCards.isEmpty()) {
                        // This if block is to add one gift card from extraList to validList if one valid gift card got
                        // added in extraList after above block 1 execution. */
                        double totalValidAmt = 0.0;
                        for (final PaymentGroup validGiftCard : validGiftCards) {
                            totalValidAmt += validGiftCard.getAmount();
                        }

                        if ((totalValidAmt < orderTotalAmount) && !extraGiftCards.isEmpty()) {
                            this.logDebug("Taking one valid gift card from extra gift card list");
                            validGiftCards.add(extraGiftCards.remove(0));
                        }
                    }

                }
            }

            if (!extraGiftCards.isEmpty()) {
                this.logDebug("------- Extra gift card found, extra gift cards::: " + extraGiftCards);
            }
            this.logDebug("------- Valid gift cards::::" + validGiftCards);
            if (!extraGiftCards.isEmpty()) {
                for (final PaymentGroup extraGiftCard : extraGiftCards) {
                    this.logDebug("-------    Removing Gift card[" + extraGiftCard.getId() + "]   --------");
                    this.removePaymentGroupFromOrder(pOrder, extraGiftCard.getId());
                    this.getOrderManager().updateOrder(pOrder);
                }
            }
        }
        this.logDebug("--------      Exiting method BBBPaymentGroupManager.removeExtraGiftCard      -----------");
    }

    /** This method removes payment groups based on their type.
     * 
     * @param pOrder order
     * @param pPGType Payment Group Types
     * @throws CommerceException Exception*/
    @SuppressWarnings("unchecked")
	private void removePaymentGroups(final Order pOrder, final String pPGType) throws CommerceException {

        this.logDebug("Starting method BBBPaymentGroupManager.removePaymentGroup, Input params::: " + "pPGType:"
                + pPGType);

        final List<String> gcIds = new ArrayList<String>();
        final List<String> ccIds = new ArrayList<String>();

        final List<PaymentGroup> listOfPG = pOrder.getPaymentGroups();

        if (listOfPG != null) {
            for (final PaymentGroup paymentGroup : listOfPG) {
                if ((paymentGroup instanceof CreditCard) && (BBBCheckoutConstants.CREDITCARD.equals(pPGType))) {
                    ccIds.add(paymentGroup.getId());
                }
                if ((paymentGroup instanceof BBBGiftCard) && (BBBCheckoutConstants.GIFTCARD.equals(pPGType))) {
                	gcIds.add(paymentGroup.getId());
                }
            }
        }

        if (BBBCheckoutConstants.CREDITCARD.equals(pPGType)) {
            for (final String paymentGroupID : ccIds) {
                this.removePaymentGroupFromOrder(pOrder, paymentGroupID);
                this.getOrderManager().updateOrder(pOrder);
            }
        }

        if (BBBCheckoutConstants.GIFTCARD.equals(pPGType)) {
            for (final String paymentGroupID : gcIds) {
                this.removePaymentGroupFromOrder(pOrder, paymentGroupID);
                this.getOrderManager().updateOrder(pOrder);
            }
        }
        this.logDebug("Total GiftCard removed: " + gcIds.size() + "Total CreditCard removed: " + ccIds.size()
                + "Exiting method BBBPaymentGroupManager.removePaymentGroup");
    }

    /** This method returns total amount covered by Gift Cards.
     * 
     * @param pPaymentGroup Payment Group
     * @param order Order
     * @return Amount covered by Gift Card*/
    public double getAmountCoveredByGiftCard(final List<PaymentGroup> pPaymentGroup, final Order order) {

        this.logDebug("Starting method BBBPaymentGroupManager.getAmountcoveredByGiftCard, Input params::: "
                + "pPaymentGroup:" + pPaymentGroup);

		double amount = 0.0;
		if (null != order.getPriceInfo()) {
			final double orderTotalAmount = BBBUtility
					.round(((BBBOrderPriceInfo) order.getPriceInfo())
							.getTotal());
			final double gcTotalAmount = this.getGiftCardTotalAmount(order);

			if (orderTotalAmount >= gcTotalAmount) {
				amount = gcTotalAmount;
			} else {
				this.logDebug("!! giftCardTotalAmount > OrderTotal amount");
				amount = orderTotalAmount;
			}
		}
		else{
			logError("BBBPaymentGroupManager.getAmountCoveredByGiftCard exception order " + order.getId()+" Payment Group "+pPaymentGroup);
		}
		this.logDebug(EXITING_METHOD_AMOUNT_COVERED_BY_GIFT_CARD
				+ "AmountCoveredByGiftCard:" + amount);
		return amount;
    }
    
    /** This method returns total amount covered by Credit Cards.
     * 
     * @param pPaymentGroup Payment Group, order
     * @param order Order
     * @return Amount covered by credit Card*/
    public final double getAmountCoveredByCreditCard(final List<PaymentGroup> pPaymentGroup, final Order order) {
    	
    	this.logDebug("Starting method BBBPaymentGroupManager.getAmountCoveredByCreditCard");
    	double ccAmount = order.getPriceInfo().getTotal() - getAmountCoveredByGiftCard(pPaymentGroup,order);
    	this.logDebug("Amount covered by credit card:" + ccAmount);
    	this.logDebug("End method BBBPaymentGroupManager.getAmountCoveredByCreditCard");
    	return ccAmount;
    }

    /** This method check that a given gift card already exist or not.
     * 
     * @param giftCardNumber Gift Card Number
     * @param giftCardPin Gift Card PIN
     * @param order Order
     * @return Success / Failure*/
    @SuppressWarnings("unchecked")
	public final boolean isGiftCardAlreadyExist(final String giftCardNumber, final String giftCardPin,
            final Order order) {

        this.logDebug("Starting method BBBPaymentGroupManager.isGiftCardalreadyExist, Input params::: "
                + "giftCardNumber:" + giftCardNumber + ", giftCardPin:" + giftCardPin);

        boolean giftcardflag = false;
        final List<PaymentGroup> paymentGroup = order.getPaymentGroups();

        if (paymentGroup != null) {
            for (final PaymentGroup paymentGrp : paymentGroup) {

                if (paymentGrp instanceof BBBGiftCard) {
                    final BBBGiftCard giftCardPayGrp = (BBBGiftCard) paymentGrp;
                    if (giftCardPayGrp.getCardNumber().equals(giftCardNumber)) {
                        giftcardflag = true;
                        break;
                    }
                }
            }
        }

        this.logDebug(EXITING_METHOD_AMOUNT_COVERED_BY_GIFT_CARD
                + "giftcardflag:" + giftcardflag);

        return giftcardflag;
    }

    /**This method does 2 things.
     * Choosing a donor gift card having remainingBal > 0 i.e. when there is a gift card whose balance has not got
     * completely used and we can take some amount from that gift card to complete the order total.
     * Choosing a gift card having extra amount when all gift card balance are completely used but still order total is
     * less than sum of amount covered by gift card. This gift card will be set with required amount.
     * 
     * @param order
     * @throws CommerceException */
    @SuppressWarnings("unchecked")
	public void resetAmountCoveredByGiftCard(final Order pOrder) throws CommerceException {

        this.logDebug("Starting method BBBPaymentGroupManager.resetAmountCoveredByGiftCard");

        final double orderTotal = BBBUtility.round(((BBBOrderPriceInfo) pOrder.getPriceInfo()).getTotal());
        final double totAmtCvrdByGC = this.getGiftCardTotalAmount(pOrder);
        double requiredAmt = orderTotal - totAmtCvrdByGC;
        double donatingAmt = 0.0;

        final List<PaymentGroup> paymentGroup = pOrder.getPaymentGroups();

        BBBGiftCard donorGC = null;
        double sumToFindExtraGC = 0.0;

        // This field will be used to find required amount to be set as amount in extra gift card
        double sumToFindReqAmt = 0.0;

        // This will hold gift card having extra amount. */
        PaymentGroup gcHavingExtraAmount = null;

        if (paymentGroup != null) {
            for (final PaymentGroup paymentGrp : paymentGroup) {
                if (paymentGrp instanceof BBBGiftCard) {
                    final BBBGiftCard giftCardPayGrp = (BBBGiftCard) paymentGrp;
                    if (BBBUtility.round(giftCardPayGrp.getBalance()) > BBBUtility.round(giftCardPayGrp.getAmount())) {
                        // Choosing a donor gift card having remainingBal > 0 i.e. when there is a gift card whose
                        // balance has not final got completely used final and we can final take some amount final from
                        // that gift final card to complete final the order total. */
                        donorGC = giftCardPayGrp;
                        this.logDebug("~~~~~~~  Donor gift card found: paymentGrpID:" + donorGC.getId());
                    } else {
                        // Choosing a gift card having extra amount when all gift card balance are completely used but
                        // still order final total is less final than sum of final amount covered by gift card. This
                        // final gift card will final be set in final below flow final with required amount.
                        sumToFindExtraGC += giftCardPayGrp.getAmount();
                        if (sumToFindExtraGC > orderTotal) {
                            gcHavingExtraAmount = giftCardPayGrp;
                            this.logDebug("~~~~~~~  Gift card with extra amount found: paymentGrpID:"
                                    + gcHavingExtraAmount.getId());
                        } else {
                            sumToFindReqAmt += giftCardPayGrp.getAmount();
                        }
                    }
                }
            }
        }

        if (donorGC != null) {

            donatingAmt = BBBUtility.round(donorGC.getBalance().doubleValue() - donorGC.getAmount());

            this.logDebug("~~~~~~~  required amount from Donor gift card:" + requiredAmt);
            this.logDebug("~~~~~~~  Possible donating amount by donar gift card:" + donatingAmt);
            if (requiredAmt <= donatingAmt) {
                donorGC.getOrderRelationship().setAmount(donorGC.getAmount() + requiredAmt);
                donorGC.setAmount(donorGC.getAmount() + requiredAmt);
                this.logDebug("~~~~~~~  requiredAmt <= donatingAmt");
            } else {
                donorGC.getOrderRelationship().setAmount(donorGC.getAmount() + donatingAmt);
                donorGC.setAmount(donorGC.getAmount() + donatingAmt);
                this.logDebug("~~~~~~~  requiredAmt > donatingAmt");
            }
        }

        if (gcHavingExtraAmount != null) {
            requiredAmt = orderTotal - sumToFindReqAmt;
            this.logDebug("~~~~~~~  Required amount to be set as amount in extra gift card:" + requiredAmt);
            gcHavingExtraAmount.setAmount(requiredAmt);
        }

        if ((donorGC != null) || (gcHavingExtraAmount != null)) {
            this.getOrderManager().updateOrder(pOrder);
        }

        this.logDebug("Exiting method BBBPaymentGroupManager.resetAmountCoveredByGiftCard");
    }

    /** @return the mMaxGiftCardLimit */
    public final String getMaxGiftCardLimit() {
        return this.maxGiftCardLimit;
    }

    /** @param pMaxGiftCardLimit the mMaxGiftCardLimit to set */
    public final void setMaxGiftCardLimit(final String pMaxGiftCardLimit) {
        this.maxGiftCardLimit = pMaxGiftCardLimit;
    }

    /** @return the mOrderManager */
    public final BBBOrderManager getOrderManager() {
        return this.orderManager;
    }

    /** @param pOrderManager the mOrderManager to set */
    public final void setOrderManager(final BBBOrderManager pOrderManager) {
        this.orderManager = pOrderManager;
    }

    /** @return the mTransactionManager */
    public final TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    /** @param pTransactionManager the mTransactionManager to set */
    public final void setTransactionManager(final TransactionManager pTransactionManager) {
        this.transactionManager = pTransactionManager;
    }

    @Override
    public final void logDebug(final String pMessage) {
        if (this.isLoggingDebug()) {
            super.logDebug(pMessage);
        }
    }
    
    
    /**
     * This method sets billing address in order if received from Paypal
     * @param voResp
     * @param pOrder
     * @return
     * @throws BBBSystemException 
     */
	public void setPayPalBillingAddress(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrder pOrder, BBBAddressContainer addressContainer) throws BBBSystemException {
		logDebug("BBBPaymentGroupManager.setPayPalBillingAddress() :: Start");
		String email = null;
		String contactNum = null;
		final TransactionDemarcation td = new TransactionDemarcation();
		boolean rollback = false;
		Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		try {

			td.begin(this.getTransactionManager());

			synchronized (pOrder) {
				// setting email of billing address of order if present else set
				// PayPal email address
				//BBBAddressPPVO addr = voResp.getBillingAddress();
				/*modifying some fields of billing address coming from Paypal before setting them*/
				BBBAddressPPVO modifiedBillingAddress = validatePayPalBillingAddress(voResp.getBillingAddress());
				if(isLoggingDebug())
				{
					logDebug("modifiedBillingAddress : " +modifiedBillingAddress);
				}
				if (pOrder.getBillingAddress() != null) {
					ContactInfo bbbNewAddress = new ContactInfo();
					email = pOrder.getBillingAddress().getEmail();
					contactNum = pOrder.getBillingAddress().getMobileNumber();
					if (BBBUtility.isEmpty(contactNum)) {
						contactNum = populateContactInfo(voResp, profile);
					}
					else{
						logDebug("Setting Contact number from Billing Group" + contactNum);
					}
					if (BBBUtility.isEmpty(email)) {
						email = populateEmailInfo(voResp, profile);
						/*
						 * Below attribute act as an indicator to refresh coupon
						 * in case of user does paypal checkout for the first
						 * time in a session.
						 */
						ServletUtil.getCurrentRequest().getSession()
								.setAttribute(BBBCoreConstants.REFRESH_COUPON, BBBCoreConstants.TRUE);
					}
					else{
						logDebug("Setting Email address from Billing Group" + email);
					}
					bbbNewAddress.setFirstName(modifiedBillingAddress.getFirstName());
					bbbNewAddress.setLastName(modifiedBillingAddress.getLastName());
					bbbNewAddress.setAddress1(modifiedBillingAddress.getAddress1());
					bbbNewAddress.setAddress2(modifiedBillingAddress.getAddress2());
					bbbNewAddress.setCity(modifiedBillingAddress.getCity());
					bbbNewAddress.setEmail(email);
					bbbNewAddress.setPhoneNumber(contactNum);
					bbbNewAddress.setPostalCode(modifiedBillingAddress.getPostalCode());
					bbbNewAddress.setState(modifiedBillingAddress.getState());
					bbbNewAddress.setCountry(modifiedBillingAddress.getCountry());
					
		            AddressTools.copyAddress(bbbNewAddress, pOrder.getBillingAddress());
		            ((MutableRepository) this.getOrderTools().getOrderRepository()).updateItem(pOrder.getBillingAddress()
		                            .getRepositoryItem());
		            if(BBBUtility.isEmpty(pOrder.getBillingAddress().getMobileNumber())){
		            	pOrder.getBillingAddress().setMobileNumber(pOrder.getBillingAddress().getPhoneNumber());
		            }
		            pOrder.getBillingAddress().setCountryName(modifiedBillingAddress.getCountryName());
		            pOrder.getBillingAddress().setFromPaypal(true);
		            if(addressContainer != null && addressContainer.getDuplicate() != null){
		            	addressContainer.getDuplicate().clear();
		            	addressContainer.getDuplicate().add(pOrder.getBillingAddress());
		            }
				}
				
				else{
					/*
					 * Below attribute act as an indicator to refresh coupon in
					 * case of user does paypal checkout for the first time in a
					 * session.
					 */
					ServletUtil.getCurrentRequest().getSession()
							.setAttribute(BBBCoreConstants.REFRESH_COUPON, BBBCoreConstants.TRUE);
					MutableRepository rep = (MutableRepository) getOrderManager().getOrderTools().getOrderRepository();
					MutableRepositoryItem mitem = null;
	
					if (modifiedBillingAddress != null && rep != null) {
						mitem = rep.createItem(BBBPayPalConstants.ADDRESS);
						BBBRepositoryContactInfo mBillingAddress = new BBBRepositoryContactInfo(mitem);
						email = populateEmailInfo(voResp, profile);
						contactNum = populateContactInfo(voResp, profile);
						
						mBillingAddress.setFirstName(modifiedBillingAddress.getFirstName());
						mBillingAddress.setLastName(modifiedBillingAddress.getLastName());
						mBillingAddress.setAddress1(modifiedBillingAddress.getAddress1());
						mBillingAddress.setAddress2(modifiedBillingAddress.getAddress2());
						mBillingAddress.setCity(modifiedBillingAddress.getCity());
						mBillingAddress.setFromPaypal(true);
						mBillingAddress.setEmail(email);
						mBillingAddress.setMobileNumber(contactNum);
						mBillingAddress.setPhoneNumber(contactNum);
						mBillingAddress.setPostalCode(modifiedBillingAddress.getPostalCode());
						mBillingAddress.setState(modifiedBillingAddress.getState());
						mBillingAddress.setCountry(modifiedBillingAddress.getCountry());
						mBillingAddress.setCountryName(modifiedBillingAddress.getCountryName());
						//creating shallow profile for guest user
						if (profile.isTransient()) {
							String postalCode = mBillingAddress.getPostalCode();
				        	if(!BBBUtility.isEmpty(postalCode)){
				        		postalCode = postalCode.replaceAll(BBBCoreConstants.WHITE_SPACE, BBBCoreConstants.BLANK);
				        	}
							try {
								getBbbGetCouponsManager().createWalletMobile(mBillingAddress.getEmail(), mBillingAddress.getPhoneNumber(), 
										mBillingAddress.getFirstName(), mBillingAddress.getLastName(), mBillingAddress.getAddress1(), 
										mBillingAddress.getCity(), mBillingAddress.getState(), postalCode);
							} catch (BBBSystemException se) {
								if (isLoggingError()) {
									logError("BBBPaymentGroupManager.setPayPalBillingAddress exception" + se);
								}
							} catch (BBBBusinessException be) {
								if (isLoggingError()) {
									logError("BBBPaymentGroupManager.setPayPalBillingAddress exception" + be);
								}
							}
						}
						rep.addItem(mitem);
						pOrder.setBillingAddress(mBillingAddress);
				}
					
				}
				getOrderManager().updateOrder(pOrder);
			}
		} catch (RepositoryException e) {
			this.logError(BBBCoreErrorConstants.PAYPAL_BILLINGADDRESS_EXCEPTION + ": RepositoryException", e);
			rollback = true;
			throwPaypalBillingFailError();
		} catch (CommerceException e) {
			if (isLoggingDebug()) {
				logDebug("error in creating payment group");
			}
			this.logError(BBBCoreErrorConstants.PAYPAL_BILLINGADDRESS_EXCEPTION + ": commerceException", e);
			rollback = true;
			throwPaypalBillingFailError();
		} catch (final Exception e) {
			this.logError(BBBCoreErrorConstants.PAYPAL_BILLINGADDRESS_EXCEPTION + ": Exception", e);
			rollback = true;
			throwPaypalBillingFailError();
		} finally {
			try {
				td.end(rollback);
			} catch (final TransactionDemarcationException tde) {
				this.logError("Transaction roll back error", tde);
				throwPaypalBillingFailError();
			}
		}
		logDebug("BBBPaymentGroupManager.setPayPalBillingAddress() :: End");
	}

	/**
	 * @throws BBBSystemException
	 */
	protected void throwPaypalBillingFailError() throws BBBSystemException {
		throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_BILLING_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_BILLING_FAIL, "EN", null));
	}
	
	
	/**
	 * This method validates and modify the billing address coming from PayPal
	 * 
	 * @param billingAddress
	 * @return BBBAddressPPVO
	 */
	
	private BBBAddressPPVO validatePayPalBillingAddress(
			BBBAddressPPVO billingAddress) {

		if (isLoggingDebug()) {
			logDebug("validatePayPalBillingAddress [START] billingAddress "+ billingAddress);
		}
        if(billingAddress != null)
		{
			billingAddress.setFirstName(BBBUtility.modifyFirstName(billingAddress.getFirstName()));
			billingAddress.setLastName(BBBUtility.modifyLastName(billingAddress.getLastName()));
			billingAddress.setAddress1(BBBUtility.modifyAddress1(billingAddress.getAddress1()));
			billingAddress.setAddress2(BBBUtility.modifyAddress2(billingAddress.getAddress2()));
			billingAddress.setCity(BBBUtility.modifyCity(billingAddress.getCity()));
		}
		
		if (isLoggingDebug()) {
			logDebug("validatePayPalBillingAddress [END] billingAddress "+ billingAddress);
		}

		return billingAddress;
	}

	/**
	 * This method store email in order billing address.
	 * fetch from Profile if user is authenticated otherwise
	 * fetch from paypal response
	 * 
	 * @param voResp
	 * @param profile
	 * @return
	 */
	private String populateEmailInfo(BBBGetExpressCheckoutDetailsResVO voResp, Profile profile){
		
		String email = "";
		if(!profile.isTransient()){
			email = (String) profile.getPropertyValue(BBBCoreConstants.EMAIL);
			logDebug("Setting Email address from Profile" + email);
		}
		else{
			email = voResp.getPayerInfo().getPayerEmail();
			logDebug("Setting Email address from Paypal Response" + email);
		}
		return email;
	}
        
	/**
	 * This method store contact number in order billing address. 
	 * If billing address does not have mobile number then fetch from profile 
	 * mobile number and phone number.
	 * If profile does not have any number then fetch from paypal response.
	 * 
	 * @param contactNumber
	 * @param voResp
	 * @param profile
	 * @return
	 */
	private String populateContactInfo(BBBGetExpressCheckoutDetailsResVO voResp, Profile profile){
		
		String contactNumber = "";
		if(!profile.isTransient()){
			contactNumber = (String) profile.getPropertyValue(BBBCoreConstants.MOBILE_NUM);
			if (BBBUtility.isEmpty(contactNumber)) {
				contactNumber = (String) profile.getPropertyValue(BBBCoreConstants.PHONE_NUM);
				if (BBBUtility.isEmpty(contactNumber)) {
					contactNumber = voResp.getContactPhone();
					if (!BBBUtility.isEmpty(contactNumber)) {
						contactNumber = contactNumber.replaceAll("[-+ ]", "");
					}
					logDebug("Contact Number from Paypal as profile contains no contact number" + contactNumber);
				}
			}
			logDebug("Contact Number from Profile" + contactNumber);
		}
		else{
			contactNumber = voResp.getContactPhone();
			if (!BBBUtility.isEmpty(contactNumber)) {
				contactNumber = contactNumber.replaceAll("[-]", "");
			}
			logDebug("Contact Number from Paypal" + contactNumber);
		}
		return contactNumber;
	}
	
	/**
	 * This createPayPalPaymentGroup method creates payPal payment group and returns true if successful
	 * @param voResp
	 * @param pOrder
	 * @param pReq
	 * @param configuration
	 * @param pProfile
	 * @param payPalInitializer
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("unchecked")
	public boolean createPayPalPaymentGroup(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrder pOrder, Profile pProfile) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("BBBPaymentGroupManager.createPayPalPaymentGroup() :: Start");
		}

		Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		boolean acquireLock = false;
		boolean isErrorOccured = false;
		TransactionDemarcation td = new TransactionDemarcation();
		ClientLockManager lockManager = getLocalLockManager();

		try {

			acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
			if (acquireLock) {
				lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
			}

			td.begin(getTransactionManager());

			synchronized (pOrder) {

				final List<PaymentGroup> paymentList = pOrder.getPaymentGroups();
				CreditCard pppGrp = null;

				// Check for CreditCard payment group and remove if any
				for (final PaymentGroup paymentGroup : paymentList) {
					if (paymentGroup instanceof CreditCard) {
						pppGrp = (CreditCard) paymentGroup;
						break;
					}
				}

				if (pppGrp != null) {
					getOrderManager().getPaymentGroupManager().removePaymentGroupFromOrder(pOrder, pppGrp.getId());
					getOrderManager().updateOrder(pOrder);
				}

				// Updating payment group
				PayerInfoVO payer = voResp.getPayerInfo();
				Boolean[] orderAmtCoveredByGC = null;
				boolean isGFAvail = false;

				// Checking if payment group containing GiftCard
				for (final PaymentGroup paymentGroup : paymentList) {
					if (paymentGroup instanceof BBBGiftCard) {
						isGFAvail = true;
						break;
					}
				}

				if (null == pOrder.getPriceInfo()) {
					runPricingEngine(pProfile, pOrder, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL);
				}

				if (isGFAvail) {
					orderAmtCoveredByGC = processPaymentGroupStatusOnLoad(pOrder);
				}
				boolean check = (orderAmtCoveredByGC != null && orderAmtCoveredByGC.length > 1 && Boolean.TRUE == orderAmtCoveredByGC[1] && !checkPaymentGroupExists((BBBOrderImpl) pOrder));
				if (!check) {

					Paypal pg = (Paypal) this.orderManager.getPaymentGroupManager().createPaymentGroup(BBBCoreConstants.PAYPAL);

					if (pg == null) {
						logError("Error while creating paypal payment group & putting in container");
						throw new BBBBusinessException(BBBCoreErrorConstants.PAYPAL_PAYMENT_GROUP_EXCEPTION);
					}
					PaymentGroup pgtemp = null;
					try {
						//
						pgtemp = pOrder.getPaymentGroup(pg.getId());
						this.orderManager.addRemainingOrderAmountToPaymentGroup(pOrder, pgtemp.getId());
					} catch (PaymentGroupNotFoundException e) {
						// Adding PayPal payment group if it is not found
						pg.setPayerId(payer.getPayerID());
						pg.setToken(voResp.getToken());
						pg.setPaymentMethod(BBBCoreConstants.PAYPAL);
						pg.setPayerEmail(voResp.getPayerInfo().getPayerEmail());
						pg.setPayerStatus(voResp.getPayerInfo().getPayerStatus());
						this.orderManager.getPaymentGroupManager().addPaymentGroupToOrder(pOrder, pg);
						this.orderManager.addRemainingOrderAmountToPaymentGroup(pOrder, pg.getId());
					}

				}
				getOrderManager().updateOrder(pOrder);
				pProfile.setPropertyValue(BBBPayPalConstants.PAYERID, payer.getPayerID());
			}

		} catch (RunProcessException e) {
			if (isLoggingError()) {
				logError("Error in creating payment group during repricing");
			}
			this.logError(BBBCoreErrorConstants.PAYPAL_PAYMENT_GROUP_EXCEPTION + ": RunProcessException", e);
			isErrorOccured = true;
			throwPaypalPaymentGroupFailError();
		} catch (CommerceException e) {
			if (isLoggingError()) {
				logError("Error in creating payment group");
			}
			this.logError(BBBCoreErrorConstants.PAYPAL_PAYMENT_GROUP_EXCEPTION + ": commerceException", e);
			isErrorOccured = true;
			throwPaypalPaymentGroupFailError();
		} catch (DeadlockException e) {
			if (isLoggingError()) {
				logError("Deadlock occured while creating paypal payment group");
			}
			this.logError(BBBCoreErrorConstants.PAYPAL_PAYMENT_GROUP_EXCEPTION + ": DeadlockException", e);
			isErrorOccured = true;
			throwPaypalPaymentGroupFailError();
		} catch (TransactionDemarcationException e) {
			if (isLoggingError()) {
				logError("TransactionDemarcationException occured while creating paypal payment group");
			}
			this.logError(BBBCoreErrorConstants.PAYPAL_PAYMENT_GROUP_EXCEPTION + ": TransactionDemarcationException", e);
			isErrorOccured = true;
			throwPaypalPaymentGroupFailError();
		} finally {
			try {
				td.end(isErrorOccured);
			} catch (final TransactionDemarcationException tde) {
				this.logError("Transaction roll back error", tde);
				throwPaypalPaymentGroupFailError();
			}

			if (acquireLock) {
				try {
					lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
				} catch (LockManagerException e) {
					this.logError("TransactionDemarcationException releasing lock on profile", e);
					return false;
				}
			}

		}

		if (isLoggingDebug()) {
			logDebug("BBBPaymentGroupManager.createPayPalPaymentGroup() :: End");
		}
		return !isErrorOccured;
	}

	/**
	 * @throws BBBSystemException
	 */
	protected void throwPaypalPaymentGroupFailError() throws BBBSystemException {
		throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_PAYMENT_GROUP_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_PAYMENT_GROUP_FAIL, "EN", null));
	}
    
	
	 /**
	  * This method runs pricing engine
	 * @param pProfile
	 * @param pOrder
	 * @param payer
	 * @throws RunProcessException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean runPricingEngine(Profile pProfile,BBBOrder pOrder,String pricingConstant) throws RunProcessException{
			final Map map = new HashMap();
			map.put(BBBPayPalConstants.ORDER, pOrder);
			map.put(BBBPayPalConstants.PROFILE, pProfile);
			map.put(PricingConstants.PRICING_OPERATION_PARAM, pricingConstant);
			getOrderManager().getPipelineManager().runProcess("repriceOrder", map);
			return true;
	 }
	
	/**
	 * This method Checks if Paymentgroup Paypal exists
	 * 
	 * @param BBBOrderImpl
	 * @return boolean
	 */
	private boolean checkPaymentGroupExists(BBBOrderImpl order){
		List<PaymentGroup> payPg = order.getPaymentGroups();
		boolean isPayPalPaymentGroupExist = false;
		if (payPg != null) {
			for (PaymentGroup pg : payPg) {
				if (pg instanceof Paypal) {
					isPayPalPaymentGroupExist = true;
					break;
				}
			}
		}
		return isPayPalPaymentGroupExist;
	}

	/**
	 * This method Checks if PaymentGroup GiftCard exists
	 * 
	 * @param BBBOrderImpl
	 * @return boolean
	 */
	public boolean checkGiftCard(BBBOrderImpl order){
		List<PaymentGroup> paymentGroups = order.getPaymentGroups();
		boolean isGiftCardGroupExist = false;
		for (Iterator iterator = paymentGroups.iterator(); iterator
				.hasNext();) {
			PaymentGroup pg = (PaymentGroup) iterator.next();
			if(pg instanceof BBBGiftCard) {
				isGiftCardGroupExist = true;
				break;
			}
		}
		return isGiftCardGroupExist;		
	}
		
}
