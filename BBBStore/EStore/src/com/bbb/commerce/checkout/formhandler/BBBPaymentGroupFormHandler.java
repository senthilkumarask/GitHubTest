package com.bbb.commerce.checkout.formhandler;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.apache.commons.lang.StringUtils;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.purchase.PaymentGroupFormHandler;
import atg.commerce.pricing.PricingConstants;
import atg.core.util.Address;
import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;
import atg.commerce.util.RepeatingRequestMonitor;

import com.bbb.account.api.BBBCreditCardAPIImpl;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CreditCardTypeVO;
import com.bbb.commerce.checkout.BBBCreditCardContainer;
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants;
import com.bbb.commerce.checkout.droplet.GetCreditCardsForPayment;
import com.bbb.commerce.checkout.manager.BBBCreditCardTools;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.ManageCheckoutLogging;
import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.CommonConfiguration;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;


public class BBBPaymentGroupFormHandler extends PaymentGroupFormHandler {

    private static final String ERROR_OCCURED_WHILE_COPY_BILLING_ADDRESS = "Error Occured while copy billing address to credit card payment group: ";
    private static final String IS_ORDER_AMT_COVERED_BY_GC = "IsOrderAmtCoveredByGC";
    private static final String TRUE = "true";
    private static final String STRING_NEW = "New";
    private static final String ERROR_INVALID_CVV = "err_checkout_invalidCVV";
    private static final String ERROR_INVALID_CARD_NAME = "err_checkout_invalidCardName";
    private static final String ERROR_MISSING_CREDIT_CARD = "err_checkout_missing_creditcard";
    private static final String ERROR_INSUFFICIENT_GIFT_CARD = "err_checkout_insufficient_gift_card";
    private static final String ERROR_CREDIT_CARD_EXPIRED = "err_checkout_expired_credit_card";
    private static final String PAYMENT_GROUP_GET_AMOUNT = "paymentGroup.getAmount()";
    private static final String PAYMENT_GROUP_GET_ID = "paymentGroup.getId()";
    private boolean isDummyOrderOn =false;

    private LblTxtTemplateManager messageHandler;
    private CommonConfiguration commonConfiguration;
    private GetCreditCardsForPayment creditCardsForPayment;
    private Profile profilePath;
    private OrderHolder shoppingCart;
    private CheckoutProgressStates checkoutProgressStates;
    private ExtendableCreditCardTools creditCardTools;
    private ManageCheckoutLogging manageLogging;
    
	private BBBCreditCardAPIImpl creditCardAPI;
    private BBBCreditCardContainer creditCardContainer;
    private BBBCatalogTools catalogTools;
    private BasicBBBCreditCardInfo creditCardInfo;

    private Map<String, String> checkoutFailureURLs = new HashMap<String, String>();

    private String isOrderAmountCoveredByGiftCard;
    private String paymentGroupId;
    private String selectedCreditCardId;
    private String verificationNumber;
    private String redirectURL;

    private int creditCardYearMaxLimit;

    private boolean invalidateSession; // it is for REST consumers to identify that REST session has been expired
    private boolean saveProfileFlag;

	/** @return checkoutFailureURLs */
    public final Map<String, String> getCheckoutFailureURLs() {
        return this.checkoutFailureURLs;
    }

    /** @param checkoutFailureURLs checkoutFailureURLs */
    public final void setCheckoutFailureURLs(final Map<String, String> checkoutFailureURLs) {
        this.checkoutFailureURLs = checkoutFailureURLs;
    }

    /** @return the redirectURL */
    public final String getRedirectURL() {
        return this.redirectURL;
    }

    /** @param redirectURL the redirectURL to set */
    public final void setRedirectURL(final String redirectURL) {
        this.redirectURL = redirectURL;
    }

    /** @return invalidateSession */
    public final boolean isInvalidateSession() {
        return this.invalidateSession;
    }

    /** @param invalidateSession invalidateSession */
    public final void setInvalidateSession(final boolean invalidateSession) {
        this.invalidateSession = invalidateSession;
    }

    /** @return the manageLogging */
    public final ManageCheckoutLogging getManageLogging() {
        return this.manageLogging;
    }

    /** @param manageLogging the manageLogging to set */
    public final void setManageLogging(final ManageCheckoutLogging manageLogging) {
        this.manageLogging = manageLogging;
    }

    /** This method is responsible to call the creditCardTools for expired credit card validation. If return value is
     * failure, then this method adds the corresponding FormException and redirects the user to the error URL. If any
     * exception occurs it adds the error into the form exception.
     *
     * @param request DynamoHttpServletRequest
     * @param response DynamoHttpServletResponse
     * @return boolean Result
     * @throws ServletException Exception
     * @throws IOException Exception */
    public boolean handlePayment(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
                                    throws ServletException, IOException {

        this.logDebug("BBBPaymentGroupFormHandler.handlePayment Entered");
        final String orderAmtCoveredByGC = request.getParameter(IS_ORDER_AMT_COVERED_BY_GC);

        final String internationalCreditCardFlag = (String) request.getSession().getAttribute(
                        BBBCoreConstants.INERNATIONAL_CREDIT_CARDS);
        //Start: 258 - Verified by visa - refresh back button story - resetting the page state
        request.getSession().removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS);
        //End: 258 - Verified by visa - refresh back button story - resetting the page state
        if (!StringUtils.equalsIgnoreCase(orderAmtCoveredByGC, TRUE)) {
            Transaction transaction = null;
            try {
                transaction = this.ensureTransaction();
                this.prePayment(request, response);
                if (this.getFormError()) {
                    // if there were credit card fraud attempts, logout the user
                    if (this.getCreditCardContainer().getCreditCardRetryCount() >= this.getCreditCardContainer()
                                    .getMaxCreditCardRetryCount()) {
                        this.logError(LogMessageFormatter.formatMessage(request,
                                        BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_INVALID_ATTEMPT,
                                        BBBCoreErrorConstants.ACCOUNT_ERROR_1101));
                        this.setInvalidateSession(true);
                        this.invalidateSession(request);
                        this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                        BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_INVALID_ATTEMPT,
                                        request.getLocale().getLanguage(), null, null),
                                        BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_INVALID_ATTEMPT));
					} else if (BBBUtility.siteIsTbs(SiteContextManager
							.getCurrentSiteId())
							&& this.getCreditCardContainer()
									.getCreditCardRetryCount() == this
									.getCreditCardContainer()
									.getMaxCreditCardRetryCount() - 1) {
						 this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                 BBBCheckoutConstants.ERR_CARD_SECOND_LAST_ATTEMPT,
                                 request.getLocale().getLanguage(), null, null),
                                 BBBCheckoutConstants.ERR_CARD_SECOND_LAST_ATTEMPT));
                    } else {
                        return true;
                    }

                } else {

                    synchronized (this.getOrder()) {
                        @SuppressWarnings ("unchecked")
                        final List<PaymentGroup> paymentList = this.getOrder().getPaymentGroups();

                        for (final PaymentGroup paymentGroup : paymentList) {
                            if (paymentGroup instanceof BBBCreditCard) {
                                this.setCurrentPaymentGroup(paymentGroup);
                                break;
                            }
                        }
                        Paypal pp=null;
                        for (final PaymentGroup paymentGroup : paymentList) {
                            if (paymentGroup instanceof Paypal) {
                            	pp=(Paypal) paymentGroup;
                         
                            }
                        }
                        if(pp!=null){
                        	 ((BBBPaymentGroupManager) this.getPaymentGroupManager()).removePaymentGroupFromOrder(this.getShoppingCart().getCurrent(), pp.getId());
                        	 this.getOrderManager().updateOrder(this.getOrder());
                     
                        }
                        
                        this.logInfo("BBBPaymentGroupFormHandler :: handlePayment() start :: orderID : "+ getOrder().getId());
                        if(null != ((BBBOrderImpl) getOrder()).getCouponListVo() && ((BBBOrderImpl) getOrder()).getCouponListVo().size() > 0){
                    		this.logInfo(" available coupons in order : " + ((BBBOrderImpl) getOrder()).getCouponListVo().get(0).getmPromoId());
                    	}

                        if (this.getCurrentPaymentGroup() == null) {
                            final PaymentGroup ccPG = this.getPaymentGroupManager().createPaymentGroup("creditCard");

                            if (((BBBOrder) this.getOrder()).getBillingAddress() != null) {
                                ((CreditCard) ccPG).setBillingAddress(((BBBOrder) this.getOrder()).getBillingAddress());
                            }

                            this.getPaymentGroupManager().addPaymentGroupToOrder(this.getOrder(), ccPG);
                            this.setCurrentPaymentGroup(ccPG);
                        }

                        if (BBBUtility.isNotEmpty(this.getSelectedCreditCardId())
                                        && !this.getSelectedCreditCardId().equalsIgnoreCase(STRING_NEW)) {

                            final BasicBBBCreditCardInfo creditCardInfo = this.getCreditCardContainer()
                                            .getCreditCardMap().get(this.getSelectedCreditCardId());

                            final Address billingAddress = ((CreditCard) this.getCurrentPaymentGroup())
                                            .getBillingAddress();
                            this.copyContactAddress(billingAddress);
                            ((CreditCard) this.getCurrentPaymentGroup()).setBillingAddress(billingAddress);

                            ((CreditCard) this.getCurrentPaymentGroup()).setCardVerficationNumber(this
                                            .getVerificationNumber());
                            ((CreditCard) this.getCurrentPaymentGroup()).setCreditCardNumber(creditCardInfo
                                            .getCreditCardNumber());
                            this.dummyOrder(creditCardInfo.getCreditCardNumber());
                            ((CreditCard) this.getCurrentPaymentGroup()).setCreditCardType(creditCardInfo
                                            .getCreditCardType());
                            ((CreditCard) this.getCurrentPaymentGroup()).setExpirationDayOfMonth(creditCardInfo
                                            .getExpirationDayOfMonth());
                            ((CreditCard) this.getCurrentPaymentGroup()).setExpirationMonth(creditCardInfo
                                            .getExpirationMonth());
                            ((CreditCard) this.getCurrentPaymentGroup()).setExpirationYear(creditCardInfo
                                            .getExpirationYear());

                            ((BBBCreditCard) this.getCurrentPaymentGroup()).setNameOnCard(creditCardInfo
                                            .getNameOnCard());
                            ((BBBCreditCard) this.getCurrentPaymentGroup()).setLastFourDigits(creditCardInfo
                                    .getLastFourDigits());
                        } else if (BBBUtility.isNotEmpty(this.getSelectedCreditCardId())
                                        && STRING_NEW.equalsIgnoreCase(this.getSelectedCreditCardId())) {
                            this.createOrUpdateCreditCardPaymentGroup(request, response);
                            if (this.isSaveProfileFlag() && !this.getProfilePath().isTransient()
                                            && BBBUtility.isNotEmpty(internationalCreditCardFlag)
                                            && !internationalCreditCardFlag.equalsIgnoreCase(TRUE)) {
                                this.getCreditCardAPI().addNewCreditCard(this.getProfilePath(),
                                                this.getCreditCardInfo(), this.getCurrentSiteId());
                            }
                            this.dummyOrder(this.getCreditCardInfo().getCreditCardNumber());
                        }
                        this.getOrderManager().addRemainingOrderAmountToPaymentGroup(this.getOrder(),
                                        this.getCurrentPaymentGroup().getId());
                        ((BBBOrderImpl) this.getOrder()).setExpressCheckOut(false);                       
                        this.getOrderManager().updateOrder(this.getOrder());
                        this.postPayment(request, response);

                    }

                }
            } catch (final CommerceException commerceException) {
                this.logError(LogMessageFormatter.formatMessage(request, "Error Occured while updating an order:"),
                                commerceException);
                this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                BBBCoreConstants.SYSTEM_ERROR, request.getLocale().getLanguage(), null, null),
                                BBBCoreConstants.SYSTEM_ERROR));

            } catch (final IntrospectionException introspectionException) {
                this.logError(LogMessageFormatter.formatMessage(request, ERROR_OCCURED_WHILE_COPY_BILLING_ADDRESS),
                                introspectionException);
                this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                BBBCoreConstants.SYSTEM_ERROR, request.getLocale().getLanguage(), null, null),
                                BBBCoreConstants.SYSTEM_ERROR));

            } catch (final BBBSystemException systemException) {
                this.logError(LogMessageFormatter.formatMessage(request, ERROR_OCCURED_WHILE_COPY_BILLING_ADDRESS),
                                systemException);
                this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                BBBCoreConstants.SYSTEM_ERROR, request.getLocale().getLanguage(), null, null),
                                BBBCoreConstants.SYSTEM_ERROR));

            } finally {
                if (transaction != null) {
                    this.commitTransaction(transaction);
                }
            }
        }

        // no errors set state to next level  | BBBSL-4981 - Check current progress state before moving to Review page.
        String currentCheckoutlevel = this.getCheckoutProgressStates().getCurrentLevel();
		if (BBBPayPalConstants.PAYMENT.equalsIgnoreCase(currentCheckoutlevel) && !this.getFormError()) {
			this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
		}
		else {
			this.getCheckoutProgressStates().setCurrentLevel(currentCheckoutlevel);
        }
        String redirectFailuerURL = request.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
        if ("null".equalsIgnoreCase(redirectFailuerURL) || (redirectFailuerURL.length() < 1)) {
            redirectFailuerURL = null;
        }
        return this.checkFormRedirect(redirectFailuerURL, redirectFailuerURL, request, response);
    }

	/**
	 * @return
	 */
	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * @param billingAddress
	 * @throws IntrospectionException
	 */
	protected void copyContactAddress(final Address billingAddress)
			throws IntrospectionException {
		AddressTools.copyAddress(((BBBOrder) this.getOrder()).getBillingAddress(), billingAddress);
	}
    
    
    /** This method is responsible to call the creditCardTools for expired credit card validation. If return value is
     * failure, then this method adds the corresponding FormException and redirects the user to the error URL. If any
     * exception occurs it adds the error into the form exception.
     *
     * @param request DynamoHttpServletRequest
     * @param response DynamoHttpServletResponse
     * @return boolean Result
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleSpPayment(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
                                    throws ServletException, IOException {

        this.logDebug("BBBPaymentGroupFormHandler.handlePayment Entered");
        final String orderAmtCoveredByGC = request.getParameter(IS_ORDER_AMT_COVERED_BY_GC);

        final String internationalCreditCardFlag = (String) request.getSession().getAttribute(
                        BBBCoreConstants.INERNATIONAL_CREDIT_CARDS);
        //Start: 258 - Verified by visa - refresh back button story - resetting the page state
        request.getSession().removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS);
        final BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
        final String myHandleMethod = "BBBPaymentFormHandler.handleSpPayment";
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();

        //End: 258 - Verified by visa - refresh back button story - resetting the page state
        if (!StringUtils.equalsIgnoreCase(orderAmtCoveredByGC, TRUE)) {
            Transaction transaction = null;
            try {
            	if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
                    BBBPerformanceMonitor.start(BBBPerformanceConstants.SPC_PAYMENT, myHandleMethod);
                
                transaction = this.ensureTransaction();
                this.prePayment(request, response);
                if (this.getFormError()) {
                    // if there were credit card fraud attempts, logout the user
                    if (this.getCreditCardContainer().getCreditCardRetryCount() >= this.getCreditCardContainer()
                                    .getMaxCreditCardRetryCount()) {
                        this.logError(LogMessageFormatter.formatMessage(request,
                                        BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_INVALID_ATTEMPT,
                                        BBBCoreErrorConstants.ACCOUNT_ERROR_1101));
                        this.setInvalidateSession(true);
                        this.invalidateSession(request);
                        this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                        BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_INVALID_ATTEMPT,
                                        request.getLocale().getLanguage(), null, null),
                                        BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_INVALID_ATTEMPT));
                    } else {
                        return true;
                    }

                } else {

                    synchronized (this.getOrder()) {
                        @SuppressWarnings ("unchecked")
                        final List<PaymentGroup> paymentList = this.getOrder().getPaymentGroups();

                        for (final PaymentGroup paymentGroup : paymentList) {
                            if (paymentGroup instanceof BBBCreditCard) {
                                this.setCurrentPaymentGroup(paymentGroup);
                                break;
                            }
                        }
                        Paypal pp=null;
                        for (final PaymentGroup paymentGroup : paymentList) {
                            if (paymentGroup instanceof Paypal) {
                            	pp=(Paypal) paymentGroup;
                         
                            }
                        }
                        if(pp!=null){
                        	 ((BBBPaymentGroupManager) this.getPaymentGroupManager()).removePaymentGroupFromOrder(this.getShoppingCart().getCurrent(), pp.getId());
                        	 this.getOrderManager().updateOrder(this.getOrder());
                     
                        }

                        if (this.getCurrentPaymentGroup() == null) {
                            final PaymentGroup ccPG = this.getPaymentGroupManager().createPaymentGroup("creditCard");

                            if (((BBBOrder) this.getOrder()).getBillingAddress() != null) {
                                ((CreditCard) ccPG).setBillingAddress(((BBBOrder) this.getOrder()).getBillingAddress());
                            }

                            this.getPaymentGroupManager().addPaymentGroupToOrder(this.getOrder(), ccPG);
                            this.setCurrentPaymentGroup(ccPG);
                        }

                        if (BBBUtility.isNotEmpty(this.getSelectedCreditCardId())
                                        && !this.getSelectedCreditCardId().equalsIgnoreCase(STRING_NEW)) {

                            final BasicBBBCreditCardInfo creditCardInfo = this.getCreditCardContainer()
                                            .getCreditCardMap().get(this.getSelectedCreditCardId());

                            final Address billingAddress = ((CreditCard) this.getCurrentPaymentGroup())
                                            .getBillingAddress();
                            this.copyContactAddress(billingAddress);
                            ((CreditCard) this.getCurrentPaymentGroup()).setBillingAddress(billingAddress);

                            ((CreditCard) this.getCurrentPaymentGroup()).setCardVerficationNumber(this
                                            .getVerificationNumber());
                            ((CreditCard) this.getCurrentPaymentGroup()).setCreditCardNumber(creditCardInfo
                                            .getCreditCardNumber());
                            this.dummyOrder(creditCardInfo.getCreditCardNumber());
                            ((CreditCard) this.getCurrentPaymentGroup()).setCreditCardType(creditCardInfo
                                            .getCreditCardType());
                            ((CreditCard) this.getCurrentPaymentGroup()).setExpirationDayOfMonth(creditCardInfo
                                            .getExpirationDayOfMonth());
                            ((CreditCard) this.getCurrentPaymentGroup()).setExpirationMonth(creditCardInfo
                                            .getExpirationMonth());
                            ((CreditCard) this.getCurrentPaymentGroup()).setExpirationYear(creditCardInfo
                                            .getExpirationYear());

                            ((BBBCreditCard) this.getCurrentPaymentGroup()).setNameOnCard(creditCardInfo
                                            .getNameOnCard());

                        } else if (BBBUtility.isNotEmpty(this.getSelectedCreditCardId())
                                        && STRING_NEW.equalsIgnoreCase(this.getSelectedCreditCardId())) {
                            this.createOrUpdateCreditCardPaymentGroup(request, response);
                            if (this.isSaveProfileFlag() && !this.getProfilePath().isTransient()
                                            && BBBUtility.isNotEmpty(internationalCreditCardFlag)
                                            && !internationalCreditCardFlag.equalsIgnoreCase(TRUE)) {
                                this.getCreditCardAPI().addNewCreditCard(this.getProfilePath(),
                                                this.getCreditCardInfo(), this.getCurrentSiteId());
                            }
                            this.dummyOrder(this.getCreditCardInfo().getCreditCardNumber());
                        }
                        this.getOrderManager().addRemainingOrderAmountToPaymentGroup(this.getOrder(),
                                        this.getCurrentPaymentGroup().getId());
                        ((BBBOrderImpl) this.getOrder()).setExpressCheckOut(false);                       
                        this.getOrderManager().updateOrder(this.getOrder());
                        this.postPayment(request, response);

                    }

                }
            	}
            } catch (final CommerceException commerceException) {
                this.logError(LogMessageFormatter.formatMessage(request, "Error Occured while updating an order:"),
                                commerceException);
                this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                BBBCoreConstants.SYSTEM_ERROR, request.getLocale().getLanguage(), null, null),
                                BBBCoreConstants.SYSTEM_ERROR));

            } catch (final IntrospectionException introspectionException) {
                this.logError(LogMessageFormatter.formatMessage(request, ERROR_OCCURED_WHILE_COPY_BILLING_ADDRESS),
                                introspectionException);
                this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                BBBCoreConstants.SYSTEM_ERROR, request.getLocale().getLanguage(), null, null),
                                BBBCoreConstants.SYSTEM_ERROR));

            } catch (final BBBSystemException systemException) {
                this.logError(LogMessageFormatter.formatMessage(request, ERROR_OCCURED_WHILE_COPY_BILLING_ADDRESS),
                                systemException);
                this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                BBBCoreConstants.SYSTEM_ERROR, request.getLocale().getLanguage(), null, null),
                                BBBCoreConstants.SYSTEM_ERROR));

            } finally {
                if (transaction != null) {
                    this.commitTransaction(transaction);
                    if (rrm != null) {
                        rrm.removeRequestEntry(myHandleMethod);
                    }
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.SPC_PAYMENT, myHandleMethod);
                
                }
            }
        }

        // no errors set state to next level
        if (!this.getFormError()) {
            this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_PAYMENT.toString());
        }
        String redirectFailuerURL = request.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
        if ("null".equalsIgnoreCase(redirectFailuerURL) || (redirectFailuerURL.length() < 1)) {
            redirectFailuerURL = null;
        }
        return this.checkFormRedirect(redirectFailuerURL, redirectFailuerURL, request, response);
    }
    
    
    /** This re-price the Order.
    *
    * @param pRequest a <code>DynamoHttpServletRequest</code> value
    * @param pResponse a <code>DynamoHttpServletResponse</code> value
    * @param pOperationType a <code>Operation type</code> value
    * @param object
    * @exception RunProcessException if an error occurs
    * @exception ServletException if an error occurs
    * @exception IOException if an error occurs */
   protected final void runRepricingProcess(final DynamoHttpServletRequest pRequest,
                   final DynamoHttpServletResponse pResponse, final String pOperationType)
                   throws RunProcessException, ServletException, IOException {
       if (this.getRepriceOrderChainId() == null) {
           return;
       }

       final HashMap<String, Object> params = new HashMap<String, Object>();
       params.put(PricingConstants.PRICING_OPERATION_PARAM, pOperationType);
       params.put(PricingConstants.ORDER_PARAM, this.getOrder());
       params.put(PricingConstants.PRICING_MODELS_PARAM, this.getUserPricingModels());
       params.put(PricingConstants.LOCALE_PARAM, this.getUserLocale());
       params.put(PricingConstants.PROFILE_PARAM, this.getProfile());
       params.put(PipelineConstants.ORDERMANAGER, this.getOrderManager());
       final PipelineResult result = this.runProcess(this.getRepriceOrderChainId(), params);
       this.processPipelineErrors(result);
   }

    /** @param request DynamoHttpServletRequest
     * @param response DynamoHttpServletResponse */
    private void postPayment(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response) {
        // TODO Auto-generated method stub

    }

    /** This method is responsible for validating a new credit card.
     *
     * @param request DynamoHttpServletRequest
     * @param response DynamoHttpServletResponse
     * @throws CommerceException ExceptionS */
    public final void prePayment(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
                    throws CommerceException {

        final BBBCreditCardTools creditCardTools = (BBBCreditCardTools) this.getCreditCardTools();

        if ((null == this.getOrder()) || (this.getOrder().getCommerceItemCount() == 0)) {
            this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                            BBBCheckoutConstants.ERROR_SESSION_EXPIRED, BBBCoreConstants.DEFAULT_LOCALE, null),
                            BBBCheckoutConstants.ERROR_SESSION_EXPIRED));
            return;
        }

        if (BBBUtility.isNotEmpty(this.getSelectedCreditCardId())
                        && !this.getSelectedCreditCardId().equalsIgnoreCase(STRING_NEW)) {

            final BasicBBBCreditCardInfo creditCardInfo = this.getCreditCardContainer().getCreditCardMap()
                            .get(this.getSelectedCreditCardId());
            
            if (creditCardInfo != null) {

                // This code is to validate the CVV number is valid or not.
                boolean cvvFlag = false;
                int ccreturn = 0;
                if (BBBUtility.isNotEmpty(this.getVerificationNumber())) {
                    ccreturn = creditCardTools.verifyCreditCard(creditCardInfo);

                    if (ccreturn == 0) {
                        ccreturn = creditCardTools.validateSecurityCode(this.getVerificationNumber(),
                                        creditCardInfo.getCreditCardType());
                        if (ccreturn == 0) {
                            cvvFlag = true;
                        }
                    }
                } else {
                    ccreturn = 15;
                }

                if (!cvvFlag) {
                    this.getCreditCardContainer().increaseCreditCardRetryCount();
                    this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERROR_INVALID_CVV,
                                    BBBCoreConstants.DEFAULT_LOCALE, null), ERROR_INVALID_CVV));
                }

                if (!BBBUtility.isValidCreditCardName(creditCardInfo.getNameOnCard())) {
                    this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                                    ERROR_INVALID_CARD_NAME, BBBCoreConstants.DEFAULT_LOCALE, null),
                                    ERROR_INVALID_CARD_NAME));
                }

            }
        } else if (BBBUtility.isNotEmpty(this.getSelectedCreditCardId())
                        && STRING_NEW.equalsIgnoreCase(this.getSelectedCreditCardId())) {

            final BasicBBBCreditCardInfo creditCardInfo = this.getCreditCardInfo();
            int ccreturn = this.getCreditCardTools().verifyCreditCard(creditCardInfo);

            if (!BBBUtility.isValidCreditCardName(creditCardInfo.getNameOnCard())) {
                this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERROR_INVALID_CARD_NAME,
                                BBBCoreConstants.DEFAULT_LOCALE, null), ERROR_INVALID_CARD_NAME));
            }

            this.getCreditCardTools();
            if (ccreturn != ExtendableCreditCardTools.SUCCESS) {
                this.getCreditCardContainer().increaseCreditCardRetryCount();
                final String msg = this.getCreditCardTools().getStatusCodeMessage(ccreturn);
                this.addFormException(new DropletException(msg, BBBCoreErrorConstants.CHECKOUT_ERROR_1072));
            } else {
                ccreturn = creditCardTools.validateSecurityCode(creditCardInfo.getCardVerificationNumber(),
                                creditCardInfo.getCreditCardType());
                if (ccreturn != 0) {
                    this.getCreditCardContainer().increaseCreditCardRetryCount();
                    this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERROR_INVALID_CVV,
                                    BBBCoreConstants.DEFAULT_LOCALE, null), ERROR_INVALID_CVV));
                }
            }
        } else {
        	
            if (BBBCheckoutConstants.BOPUS_ONLY.equalsIgnoreCase(((BBBOrderImpl) this.getOrder())
                            .getOnlineBopusItemsStatusInOrder())) {
                this.logDebug("BOPUS Order but No credit card added");
                this.logError(LogMessageFormatter.formatMessage(request, ERROR_MISSING_CREDIT_CARD,
                                BBBCoreErrorConstants.ACCOUNT_ERROR_1103));
                this.addFormException(new DropletException(ERROR_MISSING_CREDIT_CARD, ERROR_MISSING_CREDIT_CARD));
            
            } else if (Double.compare(((BBBPaymentGroupManager) this.getPaymentGroupManager()).getGiftCardTotalAmount(this.getOrder()), 0.0) == BBBCoreConstants.ZERO && null==this.getSelectedCreditCardId()) {

            	
		        this.logDebug("Credit card is null or empty");
		        this.logError(LogMessageFormatter.formatMessage(request, ERROR_CREDIT_CARD_EXPIRED,
		                        BBBCoreErrorConstants.ACCOUNT_ERROR_1387));
		        this.addFormException(new DropletException(ERROR_CREDIT_CARD_EXPIRED, ERROR_CREDIT_CARD_EXPIRED));
		        
		    
        	} else if (((BBBPaymentGroupManager) this.getPaymentGroupManager()).getGiftCardTotalAmount(this.getOrder()) < BigDecimal.valueOf(
        			((BBBOrderPriceInfo) this.getOrder().getPriceInfo()).getTotal()).setScale(2,
                            BigDecimal.ROUND_HALF_DOWN).doubleValue()) {
            	    this.logDebug("Gift card does not cover order total");
	                this.logError(LogMessageFormatter.formatMessage(request, ERROR_INSUFFICIENT_GIFT_CARD,
	                                BBBCoreErrorConstants.ACCOUNT_ERROR_1104));
	                this.addFormException(new DropletException(ERROR_INSUFFICIENT_GIFT_CARD, ERROR_INSUFFICIENT_GIFT_CARD));
            }

        }
    }
    
    public final void dummyOrder(String creditCardNumber)
    throws CommerceException {

    	List<String> dummyOrderOn;
			try {
				dummyOrderOn = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG);
				if(dummyOrderOn != null && !dummyOrderOn.isEmpty()){
					isDummyOrderOn = Boolean.parseBoolean(dummyOrderOn.get(0));
				}else{
					isDummyOrderOn=false;
				}
				   String dummyDiscover = this.getCatalogTools().getContentCatalogConfigration("DummyCC_Discover").get(0);
	               String dummyMaster = this.getCatalogTools().getContentCatalogConfigration("DummyCC_Master").get(0);
	               String dummyVisa = this.getCatalogTools().getContentCatalogConfigration("DummyCC_Visa").get(0);
				if(isDummyOrderOn &&(((dummyDiscover!=null && !dummyDiscover.isEmpty())&&dummyDiscover.equalsIgnoreCase(creditCardNumber))||((dummyMaster!=null &&!dummyMaster.isEmpty())&&dummyMaster.equalsIgnoreCase(creditCardNumber))||((dummyVisa!=null && !dummyVisa.isEmpty())&&dummyVisa.equalsIgnoreCase(creditCardNumber)) )){
					((BBBOrderImpl) this.getOrder()).setDummyOrder(true); 
					
				}else{
					((BBBOrderImpl) this.getOrder()).setDummyOrder(false);
				}
			} catch (BBBSystemException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logError(e.getMessage(),e);
			} catch (BBBBusinessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logError(e.getMessage(),e);
			}
			
    }

    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleRemoveGiftCard(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        this.setRedirectURL("atg-rest-ignore-redirect");
        return this.handleRemovePaymentGroup(pRequest, pResponse);

    }

    /** This method removes payment group from existing order.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleRemovePaymentGroup(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("Starting method BBBPaymentGroupFormHandler.handleRemovePaymentGroup");

        Transaction transaction = null;
        @SuppressWarnings("unchecked")
		List<PaymentGroup> paygrp = this.getOrder().getPaymentGroups();
        boolean found = false;
        for(PaymentGroup pg : paygrp){
        	if(pg.getId().equals(this.getPaymentGroupId())){
        		found = true;
        		break;
        	}
        }
        if(found){ 
        try {
            synchronized (this.getOrder()) {
                transaction = this.ensureTransaction();

                ((BBBPaymentGroupManager) this.getPaymentGroupManager()).removePaymentGroupFromOrder(this
                                .getShoppingCart().getCurrent(), this.getPaymentGroupId());

                ((BBBPaymentGroupManager) this.getPaymentGroupManager()).resetAmountCoveredByGiftCard(this.getOrder());

                this.getOrderManager().updateOrder(this.getOrder());
            }

        } catch (final CommerceException commerceException) {
            this.logError("Error Occured while process the requrest: " + commerceException);
            this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                            BBBCoreConstants.SYSTEM_ERROR, pRequest.getLocale().getLanguage(), null, null),
                            BBBCoreConstants.SYSTEM_ERROR));
        } finally {
            if (transaction != null) {
                this.commitTransaction(transaction);
            }
        }
        }
        else{
        	this.logDebug("Payment Group you are trying to remove has beed removed already");
        }
        this.logDebug("Existing method BBBPaymentGroupFormHandler.handleRemovePaymentGroup");

        if (StringUtils.isEmpty(this.getRedirectURL()) && (this.getCheckoutProgressStates().getFailureURL() != null)
                        && !StringUtils.isEmpty(this.getCheckoutProgressStates().getFailureURL())) {
            this.redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
        }
        return this.checkFormRedirect(this.redirectURL, this.redirectURL, pRequest, pResponse);
    }
    
    
    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleSpRemoveGiftCard(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        this.setRedirectURL("atg-rest-ignore-redirect");
        return this.handleSpRemovePaymentGroup(pRequest, pResponse);

    }

    /** This method removes payment group from existing order.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleSpRemovePaymentGroup(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("Starting method BBBPaymentGroupFormHandler.handleRemovePaymentGroup");
        this.getCheckoutProgressStates().setCurrentLevel(
                CheckoutProgressStates.DEFAULT_STATES.SP_PAYMENT.toString());
        Transaction transaction = null;
        @SuppressWarnings("unchecked")
		List<PaymentGroup> paygrp = this.getOrder().getPaymentGroups();
        boolean found = false;
        for(PaymentGroup pg : paygrp){
        	if(pg.getId().equals(this.getPaymentGroupId())){
        		found = true;
        		break;
        	}
        }
        if(found){ 
        try {
            synchronized (this.getOrder()) {
                transaction = this.ensureTransaction();

                ((BBBPaymentGroupManager) this.getPaymentGroupManager()).removePaymentGroupFromOrder(this
                                .getShoppingCart().getCurrent(), this.getPaymentGroupId());

                ((BBBPaymentGroupManager) this.getPaymentGroupManager()).resetAmountCoveredByGiftCard(this.getOrder());

                this.getOrderManager().updateOrder(this.getOrder());
            }

        } catch (final CommerceException commerceException) {
            this.logError("Error Occured while process the requrest: " + commerceException);
            this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                            BBBCoreConstants.SYSTEM_ERROR, pRequest.getLocale().getLanguage(), null, null),
                            BBBCoreConstants.SYSTEM_ERROR));
        } finally {
            if (transaction != null) {
                this.commitTransaction(transaction);
            }
        }
        }
        else{
        	this.logDebug("Payment Group you are trying to remove has beed removed already");
        }
        this.logDebug("Existing method BBBPaymentGroupFormHandler.handleRemovePaymentGroup");

        if (StringUtils.isEmpty(this.getRedirectURL()) && (this.getCheckoutProgressStates().getFailureURL() != null)
                        && !StringUtils.isEmpty(this.getCheckoutProgressStates().getFailureURL())) {
            this.redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
        }
        return this.checkFormRedirect(this.redirectURL, this.redirectURL, pRequest, pResponse);
    }

    /** @param request DynamoHttpServletRequest
     * @param response DynamoHttpServletResponse
     * @throws IntrospectionException Exception */
    public final void createOrUpdateCreditCardPaymentGroup(final DynamoHttpServletRequest request,
                    final DynamoHttpServletResponse response) throws IntrospectionException {

        ((BBBPaymentGroupManager) this.getPaymentGroupManager()).createOrUpdateCreditCardPaymentGroup(this.getOrder(),
                        this.getCreditCardInfo());
    }

    /** This method invalidates current active session.
     *
     * @param pRequest DynamoHttpServletRequest */
    public final void invalidateSession(final DynamoHttpServletRequest pRequest) {
        final HttpSession session = pRequest.getSession(false);
        if (session != null) {
            this.logDebug("session invalidated");
            ServletUtil.invalidateSessionNameContext(pRequest, session);
            ServletUtil.invalidateSession(pRequest, session);
            if (ServletUtil.isWebLogic()) {
                pRequest.setAttribute(DynamoHttpServletRequest.SESSION_INVALIDATED, Boolean.TRUE);
            }
        }
    }

    /** @return the selectedCreditCardId */
    public final String getSelectedCreditCardId() {
        return this.selectedCreditCardId;
    }

    /** @param selectedCreditCardId the selectedCreditCardId to set */
    public final void setSelectedCreditCardId(final String selectedCreditCardId) {
        this.selectedCreditCardId = selectedCreditCardId;
    }

    /** @return ExtendableCreditCardTools */
    public final ExtendableCreditCardTools getCreditCardTools() {
        return this.creditCardTools;
    }

    /** @param creditCardTools new ExtendableCreditCardTools */
    public final void setCreditCardTools(final ExtendableCreditCardTools creditCardTools) {
        this.creditCardTools = creditCardTools;
    }

    /** @return the verificationNumber */
    public final String getVerificationNumber() {
        return this.verificationNumber;
    }

    /** @param verificationNumber the verificationNumber to set */
    public final void setVerificationNumber(final String verificationNumber) {
        this.verificationNumber = verificationNumber;
    }

    /** @return the creditCardInfo */
    public final BasicBBBCreditCardInfo getCreditCardInfo() {
        if (this.creditCardInfo != null) {
            return this.creditCardInfo;
        }

        this.creditCardInfo = new BasicBBBCreditCardInfo();
        return this.creditCardInfo;
    }

    /** @param creditCardInfo the creditCardInfo to set */
    public final void setCreditCardInfo(final BasicBBBCreditCardInfo creditCardInfo) {
        this.creditCardInfo = creditCardInfo;
    }

    /** @return the saveProfileFlag */
    public final boolean isSaveProfileFlag() {
        return this.saveProfileFlag;
    }

    /** @param saveProfileFlag the saveProfileFlag to set */
    public final void setSaveProfileFlag(final boolean saveProfileFlag) {
        this.saveProfileFlag = saveProfileFlag;
    }

    /** @return the mShoppingCart */
    @Override
    public final OrderHolder getShoppingCart() {
        return this.shoppingCart;
    }

    /** @param shoppingCart the mShoppingCart to set */
    @Override
    public final void setShoppingCart(final OrderHolder shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    /** @return the mPaymentGroupId */
    @Override
    public final String getPaymentGroupId() {
        return this.paymentGroupId;
    }

    /** @param pPaymentGroupId the mPaymentGroupId to set */
    @Override
    public final void setPaymentGroupId(final String pPaymentGroupId) {
        this.paymentGroupId = pPaymentGroupId;
    }

    /** @return the profilePath */
    public final Profile getProfilePath() {
        return this.profilePath;
    }

    /** @param profilePath the profilePath to set */
    public final void setProfilePath(final Profile profilePath) {
        this.profilePath = profilePath;
    }

    /** @return the creditCardAPIImpl */
    public final BBBCreditCardAPIImpl getCreditCardAPI() {
        return this.creditCardAPI;
    }

    /** @param creditCardAPI the creditCardAPIImpl to set */
    public final void setCreditCardAPI(final BBBCreditCardAPIImpl creditCardAPI) {
        this.creditCardAPI = creditCardAPI;
    }

    /** Returns the CMS message handler to add form exceptions.
     *
     * @return messageHandler */
    public final LblTxtTemplateManager getMessageHandler() {
        return this.messageHandler;
    }

    /** Sets the CMS message handler to add form exceptions.
     *
     * @param messageHandler messageHandler */
    public final void setMessageHandler(final LblTxtTemplateManager messageHandler) {
        this.messageHandler = messageHandler;
    }

    /** @return CreditCardTypeVO
     * @throws BBBBusinessException Exception
     * @throws BBBSystemException Exception */
    public final List<CreditCardTypeVO> getCreditCardTypes() throws BBBSystemException, BBBBusinessException {
        return this.getCatalogTools().getCreditCardTypes(this.getCurrentSiteId());
    }

    /** @return the catalogTools */
    public final BBBCatalogTools getCatalogTools() {
        return this.catalogTools;
    }

    /** @param pCatalogTools the catalogTools to set */
    public final void setCatalogTools(final BBBCatalogTools pCatalogTools) {
        this.catalogTools = pCatalogTools;
    }

    /** @return the mCreditCardYearMaxLimit */
    public final int getCreditCardYearMaxLimit() {
        return this.creditCardYearMaxLimit;
    }

    /** @param creditCardYearMaxLimit the mCreditCardYearMaxLimit to set */
    public final void setCreditCardYearMaxLimit(final int creditCardYearMaxLimit) {
        this.creditCardYearMaxLimit = creditCardYearMaxLimit;
    }

    /** @return the creditCardContainer */
    public final BBBCreditCardContainer getCreditCardContainer() {
        return this.creditCardContainer;
    }

    /** @param creditCardContainer the creditCardContainer to set */
    public final void setCreditCardContainer(final BBBCreditCardContainer creditCardContainer) {
        this.creditCardContainer = creditCardContainer;
    }

    /** @return the commonConfiguration */
    public final CommonConfiguration getCommonConfiguration() {
        return this.commonConfiguration;
    }

    /** @param commonConfiguration the commonConfiguration to set */
    public final void setCommonConfiguration(final CommonConfiguration commonConfiguration) {
        this.commonConfiguration = commonConfiguration;
    }

    /** This method removes payment group from existing order.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleAddCreditCardToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("Starting method BBBPaymentGroupFormHandler.handleRestAddCreditCard");
        if (this.isOrderAmountCoveredByGiftCard == null) {
            this.isOrderAmountCoveredByGiftCard = "false";
        }
        pRequest.setParameter(IS_ORDER_AMT_COVERED_BY_GC, this.isOrderAmountCoveredByGiftCard);
        pRequest.setContextPath("");

        if (BBBCoreConstants.MOBILEWEB
				.equalsIgnoreCase(BBBUtility.getChannel())
				|| BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility
						.getChannel())) {

			final double gcTotalAmount = ((BBBPaymentGroupManager) this
					.getPaymentGroupManager()).getGiftCardTotalAmount(this
					.getOrder());

			if (gcTotalAmount >= BBBUtility
					.formatPriceToTwoDecimal(
			        ((BBBOrderPriceInfo) this.getOrder().getPriceInfo()).getTotal())) {
				// Order is fully covered by GC's so removing all GC's to proceed with Credit Card
				removeGiftCardsFromOrder();
			}
		}
        final Map<String, String> checkoutProgressStates = new HashMap<String, String>();
        this.getCheckoutProgressStates().setCheckoutFailureURLs(checkoutProgressStates);

        if (BBBUtility.isNotEmpty(this.getSelectedCreditCardId())
                        && !this.getSelectedCreditCardId().equalsIgnoreCase(STRING_NEW)) {
            pRequest.setParameter("Profile", this.getProfile());
            pRequest.setParameter("Order", this.getOrder());
            pRequest.setParameter("CreditCardContainer", this.getCreditCardContainer());
            this.getCreditCardsForPayment().service(pRequest, pResponse);
            final Map<String, BasicBBBCreditCardInfo> creditCardMap = new HashMap<String, BasicBBBCreditCardInfo>();
            @SuppressWarnings ("unchecked")
            final List<BasicBBBCreditCardInfo> basicBBBCreditCardInfoList = (List<BasicBBBCreditCardInfo>) pRequest
                            .getObjectParameter("creditCardInfo");

            for (final BasicBBBCreditCardInfo bbbCreditCardInfo : basicBBBCreditCardInfoList) {

                final String paymentKey = bbbCreditCardInfo.getPaymentId();
                creditCardMap.put(paymentKey, bbbCreditCardInfo);
            }

            this.getCreditCardContainer().getCreditCardMap().putAll(creditCardMap);
            if ((this.getCreditCardInfo() != null) && (this.getCreditCardInfo().getCardVerificationNumber() != null)) {
                this.setVerificationNumber(this.getCreditCardInfo().getCardVerificationNumber());
            }
        }
        
        final boolean success = this.handlePayment(pRequest, pResponse);

        this.getCheckoutProgressStates().setCheckoutFailureURLs(this.getCheckoutFailureURLs());

        return success;
    }
    
    
    /** This method removes credit card payment group from existing order.
    *
    * @param pRequest DynamoHttpServletRequest
    * @param pResponse DynamoHttpServletResponse
    * @return Success / Failure
    * @throws ServletException Exception
    * @throws IOException Exception */
   public final boolean handleRemoveCreditCardFromOrder(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	   //this.setRedirectURL("atg-rest-ignore-redirect");
       
        this.logDebug("Starting method BBBPaymentGroupFormHandler.handleRemovePaymentGroup");

        Transaction transaction = null;
        try {
            synchronized (this.getOrder()) {
                transaction = this.ensureTransaction();

                ((BBBPaymentGroupManager) this.getPaymentGroupManager()).removePaymentGroupFromOrder(this
                                .getShoppingCart().getCurrent(), this.getPaymentGroupId());

                this.getOrderManager().updateOrder(this.getOrder());
            }

        } catch (final CommerceException commerceException) {
            this.logError("Error Occured while process the request: " + commerceException);
            this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(
                            BBBCoreConstants.SYSTEM_ERROR, pRequest.getLocale().getLanguage(), null, null),
                            BBBCoreConstants.SYSTEM_ERROR));
        } finally {
            if (transaction != null) {
                this.commitTransaction(transaction);
            }
        }

        this.logDebug("Existing method BBBPaymentGroupFormHandler.handleRemovePaymentGroup");

		
        return true;
    
      

	}

	/**
	 * Method to remove all GC from order
	 * It iterates over all payment groups in order and 
	 * check if it is of GiftCard type.
	 */
	private void removeGiftCardsFromOrder() {
		@SuppressWarnings ("unchecked")
		 final List<PaymentGroup> paymentGroups = this.getOrder().getPaymentGroups();
		 final List<String> removalPaymentGrps = new ArrayList<String>();
		 for (final PaymentGroup paymentGroup : paymentGroups) {

		     this.logDebug(PAYMENT_GROUP_GET_ID + paymentGroup.getId());
		     this.logDebug(PAYMENT_GROUP_GET_AMOUNT + paymentGroup.getAmount());

		     if (paymentGroup instanceof BBBGiftCard) {
		         removalPaymentGrps.add(paymentGroup.getId());
		     }
		 }

		 if (removalPaymentGrps.size() > 0) {
		     for (final String payGrpID : removalPaymentGrps) {
		         try {
					this.getPaymentGroupManager().removePaymentGroupFromOrder(this.getOrder(), payGrpID);
				} catch (CommerceException commerceException) {
					this.logError("Error Occured while process the request: ", commerceException);
		            this.markTransactionRollback();
				}
		     }
		 }
	}
	
	 private void markTransactionRollback() {
	        if (!this.isTransactionMarkedAsRollBack()) {
	            try {
	                this.setTransactionToRollbackOnly();
	            } catch (final SystemException e) {
	                this.logError(BBBCoreErrorConstants.CART_ERROR_1021 + ": Error in marking the transaction rollback", e);
	            }
	        }
	    }

    /** @return isOrderAmountCoveredByGiftCard */
    public final String getIsOrderAmountCoveredByGiftCard() {
        return this.isOrderAmountCoveredByGiftCard;
    }

    /** @param isOrderAmountCoveredByGiftCard isOrderAmountCoveredByGiftCard */
    public final void setIsOrderAmountCoveredByGiftCard(final String isOrderAmountCoveredByGiftCard) {
        this.isOrderAmountCoveredByGiftCard = isOrderAmountCoveredByGiftCard;
    }

    /** @return creditCardsForPayment */
    public final GetCreditCardsForPayment getCreditCardsForPayment() {
        return this.creditCardsForPayment;
    }

    /** @param creditCardsForPayment creditCardsForPayment */
    public final void setCreditCardsForPayment(final GetCreditCardsForPayment creditCardsForPayment) {
        this.creditCardsForPayment = creditCardsForPayment;
    }

    @Override
    public final boolean isLoggingDebug() {
        return this.getCommonConfiguration().isLoggingDebugForRequestScopedComponents();
    }

    @Override
    public void logInfo(final String pMessage) {
        if (this.getManageLogging().isPaymentFormHandlerLogging()) {
            this.logInfo(pMessage, null);
        }
    }

    @Override
    public final void logDebug(final String message) {
        if (this.isLoggingDebug()) {
            this.logDebug(message, null);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString() */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBPaymentGroupFormHandler [messageHandler=").append(this.messageHandler)
                        .append(", mCommonConfiguration=").append(this.commonConfiguration)
                        .append(", creditCardsForPayment=").append(this.creditCardsForPayment).append(", profile=")
                        .append(this.profilePath).append(", mShoppingCart=").append(this.shoppingCart)
                        .append(", checkoutProgressStates=").append(this.checkoutProgressStates)
                        .append(", mCreditCardTools=").append(this.creditCardTools).append(", creditCardAPI=")
                        .append(this.creditCardAPI).append(", creditCardContainer=").append(this.creditCardContainer)
                        .append(", catalogTools=").append(this.catalogTools).append(", creditCardInfo=")
                        .append(this.creditCardInfo).append(", checkoutFailureURLs=").append(this.checkoutFailureURLs)
                        .append(", isOrderAmtCoveredByGC=").append(this.isOrderAmountCoveredByGiftCard)
                        .append(", mPaymentGroupId=").append(this.paymentGroupId).append(", selectedCreditCardId=")
                        .append(this.selectedCreditCardId).append(", verificationNumber=")
                        .append(this.verificationNumber).append(", redirectURL=").append(this.redirectURL)
                        .append(", creditCardYearMaxLimit=").append(this.creditCardYearMaxLimit)
                        .append(", invalidateSession=").append(this.invalidateSession).append(", saveProfileFlag=")
                        .append(this.saveProfileFlag).append("]");
        return builder.toString();
    }

	/**
	 * @return the checkoutProgressStates
	 */
	public CheckoutProgressStates getCheckoutProgressStates() {
		return checkoutProgressStates;
	}

	/**
	 * @param checkoutProgressStates the checkoutProgressStates to set
	 */
	public void setCheckoutProgressStates(
			CheckoutProgressStates checkoutProgressStates) {
		this.checkoutProgressStates = checkoutProgressStates;
	}

}
