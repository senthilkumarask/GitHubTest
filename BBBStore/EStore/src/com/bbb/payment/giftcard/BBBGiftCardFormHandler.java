package com.bbb.payment.giftcard;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroup;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteWrapper;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.commerce.util.RepeatingRequestMonitor;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.checkout.formhandler.BBBPaymentGroupFormHandler;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.social.facebook.FBConstants;
import com.bbb.utils.BBBUtility;
import com.bbb.valuelink.ValueLinkGiftCardProcessor;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;


public class BBBGiftCardFormHandler extends BBBPaymentGroupFormHandler {

    private static final String GET_ORDER_GET_PRICE_INFO = "getOrder().getPriceInfo()";
    private static final int CREDIT_CARD_DIGIT = 4;
    private static final String BBB_SYSTEM_EXCEPTION = "BBBSystemException";
    private static final String BBB_BUSINESS_EXCEPTION = "BBBBusinessException";
    private static final String MAXIMUM_INVALID_ATTEMPT_REACHED2 = "maximum Invalid Attempt reached";
    private static final String SERVICE_ERROR_DUE_TO_ANY_CACHED_UNCACHED_EXCEPTION = "Service Error due to any cached/uncached exception";
    private static final String PAYMENT_GROUP_GET_AMOUNT = "paymentGroup.getAmount()";
    private static final String PAYMENT_GROUP_GET_ID = "paymentGroup.getId()";
    private static final String MAXIMUM_INVALID_ATTEMPT_REACHED = "Maximum Invalid Attempt reached";
    private static final String ERROR = "error";
    private static final String CARD_NUMBER = "giftCardNumberResult";
    private static final String CARD_BALANCE = "balance";
    private static final String RESPONSE_TYPE_JSON = "application/json";
    private static final String URL = "url";

    private GiftCardBalanceBean balanceBean;
    private SiteWrapper siteDetails;
    private ValueLinkGiftCardProcessor valueLinkGiftCardProcessor;
    private LblTxtTemplateManager messageHandler;
    private BBBSessionBean sessionBean;

    private String giftCardSuccessURL;
    private String giftCardErrorURL;
    private String siteID;
    private String giftcardBalance;
    private String giftCardNumberRegex;
    private String giftCardPinRegex;
    private String giftCardNumber;
    private String giftCardPin;
    private String giftCardCoversOrderAmountURL;
    private String giftCardInvalidAttemptURL;
    private String fromPage;// Page Name that will be set from JSP
    private Map<String,String> successUrlMap;
    private Map<String,String> errorUrlMap;

	/**
	 * @return the fromPage
	 */
	public String getFromPage() {
		return fromPage;
	}

	/**
	 * @param fromPage the fromPage to set
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	/**
	 * @return the successUrlMap
	 */
	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	/**
	 * @param successUrlMap the successUrlMap to set
	 */
	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}

	/**
	 * @return the errorUrlMap
	 */
	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	/**
	 * @param errorUrlMap the errorUrlMap to set
	 */
	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	/** Default Constructor. */
    public BBBGiftCardFormHandler() {
        super();
    }

    /** @return Gift Card Success URL */
    public final String getGiftCardSuccessURL() {
        return this.giftCardSuccessURL;
    }

    /** @param giftCardSuccessURL Gift Card Success URL */
    public final void setGiftCardSuccessURL(final String giftCardSuccessURL) {
        this.giftCardSuccessURL = giftCardSuccessURL;
    }

    /** @return Gift Card Error URL */
    public final String getGiftCardErrorURL() {
        return this.giftCardErrorURL;
    }

    /** @param giftCardErrorURL Gift Card Error URL */
    public final void setGiftCardErrorURL(final String giftCardErrorURL) {
        this.giftCardErrorURL = giftCardErrorURL;
    }

    /** Gift Card Invalid Attempt success URL after session invalidation. */


    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception
     * @throws JSONException Exception */
    public final boolean handleBalance(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException, JSONException {
        this.logDebug("BBBGiftCardFormHandler.handleBalance() method started");

        final JSONObject responseJson = new JSONObject();
        GiftCardBeanInfo giftCardBeanInfo = null;

        String message = BBBCheckoutConstants.BLANK;
        boolean error = false;

        final String orderID = null;
        final String paymentGroupID = null;

        if (StringUtils.isEmpty(this.siteID)) {
            this.siteID = this.getSiteDetails().getId();
        }

        // get the balance from giftcard processor
        try {
            // raise exception if input is invalid
            if (!this.validateFormInputs()) {
                message = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.INVALID_GC_PIN,
                                pRequest.getLocale().getLanguage(), null, this.siteID);
                error = true;
            }
            //Rest of processing should work only when there are no errors in input validation
            else{

	            giftCardBeanInfo = this.getValueLinkGiftCardProcessor().getBalance(this.getGiftCardNumber(),
	                            this.getGiftCardPin(), orderID, paymentGroupID, this.siteID);
	
	            if (giftCardBeanInfo == null) {
	                // there may be chances of returning null object
	                message = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
	                                pRequest.getLocale().getLanguage(), null, this.siteID);
	                throw new BBBSystemException(BBBCheckoutConstants.GC_BAL_SERV_ERR, message);
	            }
	
	            if (giftCardBeanInfo.getStatus()) {
	                // card is valid balance. print balance
	                responseJson.put(CARD_BALANCE, BBBUtility.FormatCurrency(giftCardBeanInfo.getBalance()));
	                responseJson.put(CARD_NUMBER, this.getGiftCardNumber());
	            } else {
	                message = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
	                                pRequest.getLocale().getLanguage(), null, this.siteID);
	                throw new BBBSystemException(BBBCheckoutConstants.GC_BAL_SERV_ERR, message);
	            }
            }
        } catch (final BBBBusinessException bbe) {
            // BBBBusinessException is always returned from java/ATG code that
            // is from BBBCatalogTools etc..
            if (StringUtils.isEmpty(message)) {
                message = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                pRequest.getLocale().getLanguage(), null, this.siteID);
            }
            error = true;
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBB_BUSINESS_EXCEPTION), bbe);
        } catch (final BBBSystemException bse) {
            // BBBSystemException is always returned from ValueLink.
            error = true;
            if (StringUtils.isEmpty(message)) {
                message = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                pRequest.getLocale().getLanguage(), null, this.siteID);
            }
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBB_SYSTEM_EXCEPTION), bse);
        }

        this.setJSONResponse(pRequest, pResponse, responseJson, error, this.getSessionBean(), message);

        this.logDebug("BBBGiftCardFormHandler.handleBalance() method ended");
        // return false so that JSP is not delivered as part of JSON response
        return false;
    }

    /** This method performs gift card validation by making authorize call to ValueLink API.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return GiftCardBeanInfo Gift Card Bean Information
     * @throws ServletException Exception
     * @throws IOException Exception */
    private GiftCardBeanInfo preCreateGiftCard(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        this.logDebug("BBBGiftCardFormHandler.preCreateGiftCard() method started");

        GiftCardBeanInfo giftCardBeanInfo = null;

        boolean error = false;
        boolean errorMobSPC = false;
        String errmsg = BBBCheckoutConstants.BLANK;
        final String orderID = null;
        final String paymentGroupId = null;

        String siteId = this.getSiteID();
        if (StringUtils.isEmpty(siteId)) {
            siteId = this.getSiteDetails().getId();
        }
        // get the balance from gift card processor
        try {

            if (!this.validateFormInputs()) {
                errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.INVALID_GC_PIN,
                                pRequest.getLocale().getLanguage(), null, siteId);
                error = true;
                errorMobSPC = true;
            }

            if (!error) {
                final boolean isGiftCardExist = ((BBBPaymentGroupManager) this.getPaymentGroupManager())
                                .isGiftCardAlreadyExist(this.getGiftCardNumber(), this.getGiftCardPin(), this
                                                .getShoppingCart().getCurrent());

                if (isGiftCardExist) {
                    errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_ALRDY_EXIST,
                                    pRequest.getLocale().getLanguage(), null, siteId);
                    error = true;
                    errorMobSPC = true;
                }
            }

            if (!error) {
                giftCardBeanInfo = this.getValueLinkGiftCardProcessor().getBalance(this.getGiftCardNumber(),
                                this.getGiftCardPin(), orderID, paymentGroupId, siteId);

                if (giftCardBeanInfo == null) {
                    // there may be chances of returning null object
                    errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                    pRequest.getLocale().getLanguage(), null, siteId);
                    throw new BBBSystemException(BBBCheckoutConstants.GC_BAL_SERV_ERR, errmsg);
                }

                if (!giftCardBeanInfo.getStatus()) {
                    // some issue with card or pin. print generic error
                    errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                    pRequest.getLocale().getLanguage(), null, siteId);
                    errorMobSPC = true;
                    throw new BBBSystemException(BBBCheckoutConstants.GC_BAL_SERV_ERR, errmsg);
                } else if (Double.parseDouble(giftCardBeanInfo.getBalance()) == 0) {
                    errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_NOT_SUFF_BAL,
                                    pRequest.getLocale().getLanguage(), null, siteId);;
                    error = true;
                    errorMobSPC = true;
                }
            }

        } catch (final BBBBusinessException bbe) {
            // BBBBusinessException is always returned from java/ATG code that
            // is from BBBCatalogTools etc..
            error = true;
            if (StringUtils.isEmpty(errmsg)) {
                errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                pRequest.getLocale().getLanguage(), null, siteId);
            }
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBB_BUSINESS_EXCEPTION), bbe);
        } catch (final BBBSystemException bse) {
            // BBBSystemException is always returned from ValueLink.
            error = true;
            if (StringUtils.isEmpty(errmsg)) {
                errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                pRequest.getLocale().getLanguage(), null, siteId);
            }
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBB_SYSTEM_EXCEPTION), bse);
        }

        if (error) {
            if (this.getSessionBean().getGiftCardInvalidAttempt() == Integer.valueOf(
                            this.getSessionBean().getMaxGiftCardInvalidAttempt()).intValue()) {
                this.logDebug("!!!!!!!! maximum Invalid Attempt reached !!!!!!!!!");
                this.setInvalidateSession(true);
                this.invalidateSession(pRequest);
                this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
                this.addFormException(new DropletException(this.getLblTxtTemplateManager()
                                .getErrMsg(BBBCheckoutConstants.GC_MAX_INV_ATTEMPT, pRequest.getLocale().getLanguage(),
                                                null, null), BBBCheckoutConstants.GIFTCARDERROR));
            } else if (this.getSessionBean().getGiftCardInvalidAttempt() < Integer.parseInt(this.getSessionBean()
                            .getMaxGiftCardInvalidAttempt())) {
                this.logDebug("Service Error due to any cached/uncached exception, Adding form exception");
                this.getSessionBean().setGiftCardInvalidAttempt(this.getSessionBean().getGiftCardInvalidAttempt() + 1);
                String channel = BBBUtility.getChannel();
                boolean fromRest = false;
                if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
                	fromRest = true;
        		}
                
               if (Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED)) && fromRest && errorMobSPC) {
            	   this.addFormException(new DropletException(errmsg, BBBCheckoutConstants.GIFTCARD_ERROR_SPC_MOB));
               } else {
				 if (errmsg.equalsIgnoreCase(this.getLblTxtTemplateManager().getErrMsg(
                         BBBCheckoutConstants.GC_NOT_SUFF_BAL, pRequest.getLocale().getLanguage(), null, siteId)) && fromRest) {
					 this.addFormException(new DropletException(errmsg, BBBCheckoutConstants.GIFTCARDERROR));
				 } else {
					 this.addFormException(new DropletException(errmsg, BBBCheckoutConstants.GIFTCARDERROR));
				 }
               }
            }
        }

        this.logDebug("BBBGiftCardFormHandler.preCreateGiftCard() method ended");

        return giftCardBeanInfo;
    }

    /** This method validates a gift card info and creates a Gift card payment group.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleCreateGiftCard(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("Starting method BBBGiftCardFormHandler.handleCreateGiftCard()");
        String giftCardBalance = null;
		if (getFromPage() != null) {
    		setGiftCardSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
    		//setCreateKrischOrderErrorURL(pRequest.getContextPath()
				//	+ getErrorUrlMap().get(getFromPage()));
		}

        final GiftCardBeanInfo giftCardBeanInfo = this.preCreateGiftCard(pRequest, pResponse);

        if (giftCardBeanInfo != null) {
            giftCardBalance = giftCardBeanInfo.getBalance();
        }
        Transaction tr = null;
        try {
            if (!this.getFormError()) {

                synchronized (this.getOrder()) {

                    if (BBBUtility.isNotEmpty(this.getGiftCardNumber())
                                    && (this.getGiftCardNumber().length() > CREDIT_CARD_DIGIT)) {
                        this.logDebug("Gift Card Number Ended With "
                                        + this.getGiftCardNumber().substring(
                                                        this.getGiftCardNumber().length() - CREDIT_CARD_DIGIT,
                                                        this.getGiftCardNumber().length()));
                    }
                    this.logDebug("Gift Card Balance" + giftCardBalance);

                    tr = this.ensureTransaction();
                    final BBBGiftCard gcPaymentGroup = ((BBBPaymentGroupManager) this.getPaymentGroupManager())
                                    .createGiftCardPaymentGroup(this.getOrder(), giftCardBalance,
                                                    this.getGiftCardNumber(), this.getGiftCardPin());
                    this.getPaymentGroupManager().addPaymentGroupToOrder(this.getOrder(), gcPaymentGroup);

                    this.logDebug("gcPaymentGroup.getId()" + gcPaymentGroup.getId());
                    this.logDebug("gcPaymentGroup.getAmount()" + gcPaymentGroup.getAmount());

                    this.getOrderManager().addOrderAmountToPaymentGroup(this.getOrder(), gcPaymentGroup.getId(),
                                    gcPaymentGroup.getAmount());

                    final double gcTotalAmount = ((BBBPaymentGroupManager) this.getPaymentGroupManager())
                                    .getGiftCardTotalAmount(this.getOrder());

                    this.logDebug("getOrder().getPriceInfo().getTotal()" + this.getOrder().getPriceInfo().getTotal());
                    this.logDebug(GET_ORDER_GET_PRICE_INFO + this.getOrder().getPriceInfo());

                    if (gcTotalAmount >= BBBUtility
        					.formatPriceToTwoDecimal(
        					        ((BBBOrderPriceInfo) this.getOrder().getPriceInfo()).getTotal())) {
                        @SuppressWarnings ("unchecked")
                        final List<PaymentGroup> paymentGroups = this.getOrder().getPaymentGroups();
                        final List<String> removalPaymentGrps = new ArrayList<String>();
                        for (final PaymentGroup paymentGroup : paymentGroups) {

                            this.logDebug(PAYMENT_GROUP_GET_ID + paymentGroup.getId());
                            this.logDebug(PAYMENT_GROUP_GET_AMOUNT + paymentGroup.getAmount());

                            if (paymentGroup instanceof BBBCreditCard) {
                                removalPaymentGrps.add(paymentGroup.getId());
                            }
                            
                            if (paymentGroup instanceof Paypal) {
                                removalPaymentGrps.add(paymentGroup.getId());
                            }
                        }

                        if (removalPaymentGrps.size() > 0) {
                            for (final String payGrpIDs : removalPaymentGrps) {
                                this.getPaymentGroupManager().removePaymentGroupFromOrder(this.getOrder(), payGrpIDs);
                            }
                        }
                    }

                    @SuppressWarnings ("unchecked")
                    final List<PaymentGroup> paymentGroupsTemp = this.getOrder().getPaymentGroups();
                    for (final PaymentGroup paymentGroupTemp : paymentGroupsTemp) {
                        this.logDebug(PAYMENT_GROUP_GET_ID + paymentGroupTemp.getId());
                        this.logDebug(PAYMENT_GROUP_GET_AMOUNT + paymentGroupTemp.getAmount());
                    }

                    this.logDebug(GET_ORDER_GET_PRICE_INFO + this.getOrder().getPriceInfo());

                    this.getOrderManager().updateOrder(this.getOrder());
                }
            } else {
                this.logDebug("Authorization of gift card failed during handleCreateGiftCard()");
            }

        } catch (final CommerceException commerceException) {
            this.logError("Error Occured while process the request: ", commerceException);
            this.markTransactionRollback();
        } finally {
            // Complete the transaction
            if (tr != null) {
                this.commitTransaction(tr);
            }
        }

        if (!this.getFormError()) {
            // Cleaning the giftcardNumber text box and pinNo on success.
            this.setGiftCardNumber(BBBCheckoutConstants.BLANK);
            this.setGiftCardPin(BBBCheckoutConstants.BLANK);
        }

        this.logDebug("Exiting method BBBGiftCardFormHandler.handleCreateGiftCard()");
        String redirectURL = null;
        if (!BBBUtility.isEmpty(this.getGiftCardSuccessURL())
                        && !this.getGiftCardSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL)) {

            if ((this.getCheckoutProgressStates().getFailureURL() != null)
                            && !StringUtils.isEmpty(this.getCheckoutProgressStates().getFailureURL())) {

                redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();

            }
        	}
        return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);

        }
    
    /** This method validates a gift card info and creates a Gift card payment group.
    *
    * @param pRequest DynamoHttpServletRequest
    * @param pResponse DynamoHttpServletResponse
    * @return Success / Failure
    * @throws ServletException Exception
    * @throws IOException Exception */
   public final boolean handleSpCreateGiftCard(final DynamoHttpServletRequest pRequest,
                   final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

       this.logDebug("Starting method BBBGiftCardFormHandler.handleCreateGiftCard()");       
       String giftCardBalance = null;
       
       final String myHandleMethod = "BBBPaymentFormHandler.handleSpCreateGiftCard";
       final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();


       final GiftCardBeanInfo giftCardBeanInfo = this.preCreateGiftCard(pRequest, pResponse);
       if (giftCardBeanInfo != null){
           giftCardBalance = giftCardBeanInfo.getBalance();
        }
       Transaction tr = null;
       try {
    	   if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
               BBBPerformanceMonitor.start(BBBPerformanceConstants.SPC_PAYMENT, myHandleMethod);
           
           if (!this.getFormError()) {

               synchronized (this.getOrder()) {

                   if (BBBUtility.isNotEmpty(this.getGiftCardNumber())
                                   && (this.getGiftCardNumber().length() > CREDIT_CARD_DIGIT)) {
                       this.logDebug("Gift Card Number Ended With "
                                       + this.getGiftCardNumber().substring(
                                                       this.getGiftCardNumber().length() - CREDIT_CARD_DIGIT,
                                                       this.getGiftCardNumber().length()));
                   }
                   this.logDebug("Gift Card Balance" + giftCardBalance);

                   tr = this.ensureTransaction();
                   final BBBGiftCard gcPaymentGroup = ((BBBPaymentGroupManager) this.getPaymentGroupManager())
                                   .createGiftCardPaymentGroup(this.getOrder(), giftCardBalance,
                                                   this.getGiftCardNumber(), this.getGiftCardPin());
                   this.getPaymentGroupManager().addPaymentGroupToOrder(this.getOrder(), gcPaymentGroup);

                   this.logDebug("gcPaymentGroup.getId()" + gcPaymentGroup.getId());
                   this.logDebug("gcPaymentGroup.getAmount()" + gcPaymentGroup.getAmount());

                   this.getOrderManager().addOrderAmountToPaymentGroup(this.getOrder(), gcPaymentGroup.getId(),
                                   gcPaymentGroup.getAmount());

                   final double gcTotalAmount = ((BBBPaymentGroupManager) this.getPaymentGroupManager())
                                   .getGiftCardTotalAmount(this.getOrder());

                   this.logDebug("getOrder().getPriceInfo().getTotal()" + this.getOrder().getPriceInfo().getTotal());
                   this.logDebug(GET_ORDER_GET_PRICE_INFO + this.getOrder().getPriceInfo());

                   if (gcTotalAmount >= this.getOrder().getPriceInfo().getTotal()) {
                       @SuppressWarnings ("unchecked")
                       final List<PaymentGroup> paymentGroups = this.getOrder().getPaymentGroups();
                       final List<String> removalPaymentGrps = new ArrayList<String>();
                       for (final PaymentGroup paymentGroup : paymentGroups) {

                           this.logDebug(PAYMENT_GROUP_GET_ID + paymentGroup.getId());
                           this.logDebug(PAYMENT_GROUP_GET_AMOUNT + paymentGroup.getAmount());

                           if (paymentGroup instanceof BBBCreditCard) {
                               removalPaymentGrps.add(paymentGroup.getId());
                           }
                           
                           if (paymentGroup instanceof Paypal) {
                               removalPaymentGrps.add(paymentGroup.getId());
                           }
                       }

                       if (removalPaymentGrps.size() > 0) {
                           for (final String payGrpIDs : removalPaymentGrps) {
                               this.getPaymentGroupManager().removePaymentGroupFromOrder(this.getOrder(), payGrpIDs);
                           }
                       }
                   }

                   @SuppressWarnings ("unchecked")
                   final List<PaymentGroup> paymentGroupsTemp = this.getOrder().getPaymentGroups();
                   for (final PaymentGroup paymentGroupTemp : paymentGroupsTemp) {
                       this.logDebug(PAYMENT_GROUP_GET_ID + paymentGroupTemp.getId());
                       this.logDebug(PAYMENT_GROUP_GET_AMOUNT + paymentGroupTemp.getAmount());
                   }

                   this.logDebug(GET_ORDER_GET_PRICE_INFO + this.getOrder().getPriceInfo());

                   this.getOrderManager().updateOrder(this.getOrder());
               }
           } else {
               this.logDebug("Authorization of gift card failed during handleCreateGiftCard()");
           }
    	   }
       } catch (final CommerceException commerceException) {
           this.logError("Error Occured while process the request: ", commerceException);
           this.markTransactionRollback();
       } finally {
           // Complete the transaction
           if (tr != null) {
               this.commitTransaction(tr);
               if (rrm != null) {
                   rrm.removeRequestEntry(myHandleMethod);
               }
               BBBPerformanceMonitor.end(BBBPerformanceConstants.SPC_PAYMENT, myHandleMethod);          
           }
       }

       if (!this.getFormError()) {
           // Cleaning the giftcardNumber text box and pinNo on success.
           this.setGiftCardNumber(BBBCheckoutConstants.BLANK);
           this.setGiftCardPin(BBBCheckoutConstants.BLANK);
       }

       this.logDebug("Exiting method BBBGiftCardFormHandler.handleCreateGiftCard()");
       String redirectURL = null ;
       if(!BBBUtility.isEmpty(this.getGiftCardSuccessURL())
                       && !this.getGiftCardSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL)) {
           if (!BBBUtility.isEmpty(this.getGiftCardErrorURL())) {
               redirectURL = pRequest.getContextPath() + this.getGiftCardErrorURL();
           }
       }
       
       //BBBP-9866 - Ajax call flow when max invalid attempts reached, redirect to cart page
       boolean spcAjaxCall = false;
       if(BBBUtility.isNotEmpty(pRequest.getHeader(BBBCoreConstants.BBB_AJAX_REQUEST_PARAM))){
    	   
	       String isAjaxCall= pRequest.getHeader(BBBCoreConstants.BBB_AJAX_REQUEST_PARAM);
	       spcAjaxCall = Boolean.parseBoolean(isAjaxCall);
	       if(this.getFormError() && 
	    		   this.getCheckoutProgressStates().getCurrentLevel().equalsIgnoreCase(CheckoutProgressStates.DEFAULT_STATES.CART.toString()) 
	    		   		&& spcAjaxCall){
	    	   pResponse.setHeader(BBBCoreConstants.BBB_AJAX_HEADER_PARAM, pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL());
	    	   return true;
	       }
       }
       
        return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);

    }

    /** This method sets the response for getBalance call.
     *
     * @param pRequest
     * @param pResponse
     * @param responseJson
     * @param serviceError
     * @param dataError
     * @param pSessionBean
     * @param errmsg
     * @param doInvalidateSession
     * @throws IOException
     * @throws JSONException
     * @throws ServletException */
    private void setJSONResponse(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse,
                    final JSONObject responseJson, final Boolean serviceError, final BBBSessionBean pSessionBean,
                    final String errmsg) throws IOException, JSONException, ServletException {

        this.logDebug("Entering method BBBGiftCardFormHandler.setResponse");

        if ((serviceError != null) && serviceError.booleanValue()) {
            this.logDebug(SERVICE_ERROR_DUE_TO_ANY_CACHED_UNCACHED_EXCEPTION);

            if (pSessionBean.getGiftCardInvalidAttempt() == Integer.parseInt(pSessionBean
                            .getMaxGiftCardInvalidAttempt())) {
                this.logDebug(MAXIMUM_INVALID_ATTEMPT_REACHED2);
                this.invalidateSession(pRequest);
                responseJson.put(URL, pRequest.getContextPath() + this.getGiftCardInvalidAttemptURL());
            } else if (pSessionBean.getGiftCardInvalidAttempt() < Integer.parseInt(pSessionBean
                            .getMaxGiftCardInvalidAttempt())) {
                pSessionBean.setGiftCardInvalidAttempt(pSessionBean.getGiftCardInvalidAttempt() + 1);
                responseJson.put(ERROR, errmsg);

            }
        }

        // set content type to JSON
        pResponse.setContentType(RESPONSE_TYPE_JSON);
        final PrintWriter out = pResponse.getWriter();
        this.logDebug("Writing JSON response:" + responseJson.toString());
        out.print(responseJson.toString());
        out.flush();

        this.logDebug("Exiting method BBBGiftCardFormHandler.setResponse");

    }

    /** This method sets the response for getBalance call.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @param responseJson Response JSON
     * @param serviceError Service Error
     * @param dataError Data Error
     * @param pSessionBean BBB Session Bean
     * @param message Error Message
     * @param doInvalidateSession
     * @throws IOException Exception
     * @throws JSONException Exception
     * @throws ServletException Exception */
    private void setJSONResponseGiftCardBalance(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse, final JSONObject responseJson,
                    final Boolean serviceError, final BBBSessionBean pSessionBean, final String message)
                    throws IOException, JSONException, ServletException {

        this.logDebug("Entering method setJSONResponseGiftCardBalance.setResponse");

        if ((serviceError != null) && serviceError.booleanValue()) {
            this.logDebug(SERVICE_ERROR_DUE_TO_ANY_CACHED_UNCACHED_EXCEPTION);

            if (pSessionBean.getGiftCardInvalidAttempt() == Integer.parseInt(pSessionBean
                            .getMaxGiftCardInvalidAttempt())) {
                this.logDebug(MAXIMUM_INVALID_ATTEMPT_REACHED2);
                this.setInvalidateSession(true);
                this.invalidateSession(pRequest);
                responseJson.put(URL, pRequest.getContextPath() + this.getGiftCardInvalidAttemptURL());
            } else if (pSessionBean.getGiftCardInvalidAttempt() < Integer.parseInt(pSessionBean
                            .getMaxGiftCardInvalidAttempt())) {
                pSessionBean.setGiftCardInvalidAttempt(pSessionBean.getGiftCardInvalidAttempt() + 1);
                responseJson.put(ERROR, message);

            }
        }

        this.logDebug("Exiting method setJSONResponseGiftCardBalance.setResponse");
    }

    /** validateFormInputs: This method validates the form input values. */
    private boolean validateFormInputs() {
        this.logDebug("BBBGiftCardFormHandler.validateFormInputs() method started "
                        + "\nPattern for GiftCard number is:" + this.getGiftCardNumberRegex()
                        + "\nPattern for GiftCard PIN is:" + this.getGiftCardPinRegex());
        boolean isValidInput = false;

        if (StringUtils.isEmpty(this.giftCardNumber) && StringUtils.isEmpty(this.giftCardPin)) {

            isValidInput = false;
        } else if (Pattern.matches(this.getGiftCardNumberRegex(), this.giftCardNumber)
                        && Pattern.matches(this.getGiftCardPinRegex(), this.giftCardPin)) {

            isValidInput = true;
            this.logDebug("Card Number and PIN are valid inputs.");
        }

        this.logDebug("BBBGiftCardFormHandler.validateFormInputs() method ended");
        return isValidInput;
    }

    /** @return the GiftCardNumber */
    public final String getGiftCardNumber() {
        return this.giftCardNumber;
    }

    /** @param giftCardNumber Gift Card Number the mGiftCardNumber to set. */
    public final void setGiftCardNumber(final String giftCardNumber) {
        this.giftCardNumber = giftCardNumber;
    }

    /** @return Gift Card Number */
    public final String getGiftCardPin() {
        return this.giftCardPin;
    }

    /** @param giftCardPin the mGiftCardPin to set */
    public final void setGiftCardPin(final String giftCardPin) {
        this.giftCardPin = giftCardPin;
    }

    /** @return Message Handler */
    public final LblTxtTemplateManager getLblTxtTemplateManager() {
        return this.messageHandler;
    }

    /** @param lblTxtTemplateManager the lblTxtTemplateManager to set */
    public final void setLblTxtTemplateManager(final LblTxtTemplateManager messageHandler) {
        this.messageHandler = messageHandler;
    }

    /** @return the mGiftCardNumberRegex */
    public final String getGiftCardNumberRegex() {
        return this.giftCardNumberRegex;
    }

    /** @param giftCardPinRegex the mGiftCardNumberRegex to set */
    public final void setGiftCardNumberRegex(final String giftCardPinRegex) {
        this.giftCardNumberRegex = giftCardPinRegex;
    }

    /** @return the giftCardPinRegex */
    public final String getGiftCardPinRegex() {
        return this.giftCardPinRegex;
    }

    /** @param giftCardPinRegex the mGiftCardPinRegex to set */
    public final void setGiftCardPinRegex(final String giftCardPinRegex) {
        this.giftCardPinRegex = giftCardPinRegex;
    }

    /** @return the mGiftCardInvalidAttemptURL */
    public final String getGiftCardInvalidAttemptURL() {
        return this.giftCardInvalidAttemptURL;
    }

    /** @param pGiftCardInvalidAttemptURL the mGiftCardInvalidAttemptURL to set */
    public final void setGiftCardInvalidAttemptURL(final String pGiftCardInvalidAttemptURL) {
        this.giftCardInvalidAttemptURL = pGiftCardInvalidAttemptURL;
    }

    /** @return the sessionBean */
    public final BBBSessionBean getSessionBean() {
        return this.sessionBean;
    }

    /** @param pSessionBean the sessionBean to set */
    public final void setSessionBean(final BBBSessionBean pSessionBean) {
        this.sessionBean = pSessionBean;
    }

    /** @return the mSiteDetails */
    public final SiteWrapper getSiteDetails() {
        return this.siteDetails;
    }

    /** @param pSiteDetails the mSiteDetails to set */
    public final void setSiteDetails(final SiteWrapper pSiteDetails) {
        this.siteDetails = pSiteDetails;
    }

    /** @return the mGiftcardBalance */
    public final String getGiftcardBalance() {
        return this.giftcardBalance;
    }

    /** @param pGiftcardBalance the mGiftcardBalance to set */
    public final void setGiftcardBalance(final String pGiftcardBalance) {
        this.giftcardBalance = pGiftcardBalance;
    }

    /** @return the mValueLinkGiftCardProcessor */
    public final ValueLinkGiftCardProcessor getValueLinkGiftCardProcessor() {
        return this.valueLinkGiftCardProcessor;
    }

    /** @param pValueLinkGiftCardProcessor the mValueLinkGiftCardProcessor to set */
    public final void setValueLinkGiftCardProcessor(final ValueLinkGiftCardProcessor pValueLinkGiftCardProcessor) {
        this.valueLinkGiftCardProcessor = pValueLinkGiftCardProcessor;
    }

    /** @return the mURLWhenGCCoversOrderAmt */
    public final String getGiftCardCoversOrderAmountURL() {
        return this.giftCardCoversOrderAmountURL;
    }

    /** @param giftCardCoversOrderAmountURL the mURLWhenGCCoversOrderAmt to set */
    public final void setGiftCardCoversOrderAmountURL(final String giftCardCoversOrderAmountURL) {
        this.giftCardCoversOrderAmountURL = giftCardCoversOrderAmountURL;
    }

    /** @return the mSiteID */
    public final String getSiteID() {
        return this.siteID;
    }

    /** @param pSiteID the mSiteID to set */
    public final void setSiteID(final String pSiteID) {
        this.siteID = pSiteID;
    }

    private void markTransactionRollback() {
        if (!this.isTransactionMarkedAsRollBack()) {
            try {
                this.setTransactionToRollbackOnly();
            } catch (final SystemException e) {
                this.logError("Error in marking the transaction rollback", e);
            }
        }
    }

    /** get gift card balance
     *
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws JSONException */
    public final boolean handleCardBalance(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException, JSONException {
        this.logDebug("BBBGiftCardFormHandler.handleCardBalance() method started");

        final JSONObject responseJson = new JSONObject();
        GiftCardBeanInfo giftCardBeanInfo = null;

        String errmsg = BBBCheckoutConstants.BLANK;
        boolean error = false;

        final String orderID = null;
        final String paymentGroupId = null;

        String siteId = this.getSiteID();
        if (StringUtils.isEmpty(siteId)) {
            siteId = this.getSiteDetails().getId();
        }

        // get the balance from giftcard processor
        try {
            // raise exception if input is invalid
            if (!this.validateFormInputs()) {
                errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.INVALID_GC_PIN,
                                pRequest.getLocale().getLanguage(), null, siteId);
                error = true;
            }

            giftCardBeanInfo = this.getValueLinkGiftCardProcessor().getBalance(this.getGiftCardNumber(),
                            this.getGiftCardPin(), orderID, paymentGroupId, siteId);
            if (giftCardBeanInfo == null) {
                // there may be chances of returning null object
                errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                pRequest.getLocale().getLanguage(), null, siteId);
                throw new BBBSystemException(BBBCheckoutConstants.GC_BAL_SERV_ERR, errmsg);
            }

            if (giftCardBeanInfo.getStatus()) {
                // card is valid balance. print balance
                responseJson.put(CARD_BALANCE, BBBUtility.FormatCurrency(giftCardBeanInfo.getBalance()));
                responseJson.put(CARD_NUMBER, this.getGiftCardNumber());
            } else {
                errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                pRequest.getLocale().getLanguage(), null, siteId);
                throw new BBBSystemException(BBBCheckoutConstants.GC_BAL_SERV_ERR, errmsg);
            }
        } catch (final BBBBusinessException bbe) {
            // BBBBusinessException is always returned from java/ATG code that
            // is from BBBCatalogTools etc..
            if (StringUtils.isEmpty(errmsg)) {
                errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                pRequest.getLocale().getLanguage(), null, siteId);
            }
            error = true;
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBB_BUSINESS_EXCEPTION), bbe);
        } catch (final BBBSystemException bse) {
            // BBBSystemException is always returned from ValueLink.
            error = true;
            if (StringUtils.isEmpty(errmsg)) {
                errmsg = this.getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.GC_BAL_SERV_ERR,
                                pRequest.getLocale().getLanguage(), null, siteId);
            }
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBB_SYSTEM_EXCEPTION), bse);
        }

        this.setJSONResponseGiftCardBalance(pRequest, pResponse, responseJson, error, this.getSessionBean(), errmsg);
        this.getBalanceBean().setError(error);
        this.getBalanceBean().setErrorMessage(errmsg);
        if (responseJson.has(CARD_BALANCE)) {
            final String balance = responseJson.getString(CARD_BALANCE);
            final String giftCardNumber = responseJson.getString(CARD_NUMBER);
            this.getBalanceBean().setBalance(balance);
            this.getBalanceBean().setGiftCardId(giftCardNumber);
        }
        if (responseJson.toString().contains("/rest/account/login.jsp")) {
            this.getBalanceBean().setError(Boolean.TRUE);
            this.getBalanceBean().setErrorMessage(MAXIMUM_INVALID_ATTEMPT_REACHED);
        }
        this.logDebug("BBBGiftCardFormHandler.handleCardBalance() method ended");
        // return false so that JSP is not delivered as part of JSON response
        return true;
    }

    /** @return */
    public final GiftCardBalanceBean getBalanceBean() {
        if (this.balanceBean == null) {
            this.balanceBean = new GiftCardBalanceBean();
        }
        return this.balanceBean;

    }

    /** @param giftCardBalanceBean */
    public final void setBalanceBean(final GiftCardBalanceBean balanceBean) {
        this.balanceBean = balanceBean;
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBGiftCardFormHandler [giftCardBalanceBean=").append(this.balanceBean)
                        .append(", siteDetails=").append(this.siteDetails).append(", sessionBean=")
                        .append(this.sessionBean).append(", valueLinkGiftCardProcessor=")
                        .append(this.valueLinkGiftCardProcessor).append(", messageHandler=")
                        .append(this.messageHandler).append(", giftCardSuccessURL=").append(this.giftCardSuccessURL)
                        .append(", giftCardErrorURL=").append(this.giftCardErrorURL).append(", siteID=")
                        .append(this.siteID).append(", giftcardBalance=").append(this.giftcardBalance)
                        .append(", giftCardNumberRegex=").append(this.giftCardNumberRegex)
                        .append(", giftCardPinRegex=").append(this.giftCardPinRegex).append(", giftCardNumber=")
                        .append(this.giftCardNumber).append(", giftCardPin=").append(this.giftCardPin)
                        .append(", giftCardCoversOrderAmountURL=").append(this.giftCardCoversOrderAmountURL)
                        .append(", giftCardInvalidAttemptURL=").append(this.giftCardInvalidAttemptURL).append("]");
        return builder.toString();
    }

}
