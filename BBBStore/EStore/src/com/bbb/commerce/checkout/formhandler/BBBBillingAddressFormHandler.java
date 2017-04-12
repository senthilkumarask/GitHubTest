package com.bbb.commerce.checkout.formhandler;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.promotion.PromotionTools;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;

import com.bbb.account.BBBGetCouponsManager;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressAPIImpl;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.checkout.util.BBBCouponUtil;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.ManageCheckoutLogging;
import com.bbb.commerce.order.manager.PromotionLookupManager;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.CommonConfiguration;

public class BBBBillingAddressFormHandler extends PurchaseProcessFormHandler {

    private static final String US = "US";
    private static final String CA = "CA";
    private static final String CANADA = "Canada";
    private static final String TRUE = "true";
    private static final int SECURITY_STATUS = 4;
    private static final String ERROR_IN_TRANSACTION_ROLLBACK = ": Error in marking the transaction rollback";
    private static final String COPYING_ORDER_FAILED = "copying order billing Address to handler failed";
    private static final String ERR_CHECKOUT_INVALID_INPUT = "ERR_CHECKOUT_INVALID_INPUT";
    private static final String ERR_CHECKOUT_INVALID_BILLING_ADDRESS = "ERR_CHECKOUT_INVALID_BILLING_ADDRESS";
    private static final String ERR_SETTING_EMAIL_SIGNUP_IN_ORDER = "err_setting_email_signup_in_order";
    private static final String ERR_CHECKOUT_GENERIC_ERROR = "ERR_CHECKOUT_GENERIC_ERROR";
    private static final String ERR_CHECKOUT_INVALID_COUNTRY = "ERR_CHECKOUT_INVALID_COUNTRY";
    private static final String BED_BATH_US_ID = "BedBathUS";
    private static final String BED_BATH_CANADA_ID = "BedBathCanada";
    private static final String BUY_BUY_BABY_ID = "BuyBuyBaby";
    private static final String TBS_BED_BATH_US_ID = "TBS_BedBathUS";
    private static final String TBS_BED_BATH_CANADA_ID = "TBS_BedBathCanada";
    private static final String TBS_BUY_BUY_BABY_ID = "TBS_BuyBuyBaby";

    private static final String CHECKED = "checked";
    private static final String UNCHECKED = "unChecked";
    public static final String COUPONS = "coupons";
    


    private PromotionLookupManager promotionLookupManager;
    private CheckoutProgressStates checkoutState;
    private CommonConfiguration commonConfiguration;
    private PromotionTools promotionTools;
    private LblTxtTemplateManager messageHandler;
    private ManageCheckoutLogging manageLogging;
    private BBBGetCouponsManager bbbGetCouponsManager;

    public BBBGetCouponsManager getBbbGetCouponsManager() {
		return bbbGetCouponsManager;
	}

	public void setBbbGetCouponsManager(BBBGetCouponsManager bbbGetCouponsManager) {
		this.bbbGetCouponsManager = bbbGetCouponsManager;
	}

    /** @return the manageLogging */
    public final ManageCheckoutLogging getManageLogging() {
        return this.manageLogging;
    }

    /** @param manageLogging the manageLogging to set */
    public final void setManageLogging(final ManageCheckoutLogging manageLogging) {
        this.manageLogging = manageLogging;
    }

    private Map<String, RepositoryItem> promotions;

    private BBBCheckoutManager checkoutManager;
    private BBBCatalogTools catalogTools;
    private BBBSessionBean sessionBean;
    private BBBCouponUtil couponUtil;
    private BBBPropertyManager propertyManager;
    private BBBProfileTools profileTools;
    private BBBAddress billingAddress;
    private BBBAddressContainer billingAddressContainer;
    private BBBAddressAPI addressAPI;

    private String userSelectedOption;
    private String confirmedEmail;
    private String siteId;
    private String newAddress;
    private String collegeAddress;

    private boolean internationalCard;
    private boolean billingEmailChanged;
    private boolean billingPhoneChanged;
    private boolean saveToAccount;
    // Added to fix BBBSL-2662 - Start
    private boolean emailSignUp;
    private String emailChecked;
    /** @return New Address */
    public final boolean isEmailSignUp() {
		return emailSignUp;
	}

    /** @param newAddress New Address */
	public final void setEmailSignUp(final boolean emailSignUp) {
		this.emailSignUp = emailSignUp;
	}


	/** @return New Address */
	public final String getEmailChecked() {
			return this.getSessionBean().getEmailChecked();
	}

	/** @param newAddress New Address */
	public final void setEmailChecked(final String emailChecked) {
		this.emailChecked = emailChecked;
	}
    // Added to fix BBBSL-2662 - End
    /** @return New Address */
    public final String getNewAddress() {
        return this.newAddress;
    }

    /** @param newAddress New Address */
    public final void setNewAddress(final String newAddress) {
        this.newAddress = newAddress;
    }

    /** @return College Address */
    public final String getCollegeAddress() {
        return this.collegeAddress;
    }

    /** @param collegeAddress College Address */
    public final void setCollegeAddress(final String collegeAddress) {
        this.collegeAddress = collegeAddress;
    }

    /** @return the mSessionBean */
    public final BBBSessionBean getSessionBean() {
        return this.sessionBean;
    }

    /** @param pSessionBean the mSessionBean to set */
    public final void setSessionBean(final BBBSessionBean pSessionBean) {
        this.sessionBean = pSessionBean;
    }

    /** @param bbbRepoContInfo Report Count */
    public final void setOrderAddress(final BBBRepositoryContactInfo bbbRepoContInfo) {
        final BBBAddressImpl newBillingAddress = new BBBAddressImpl();
        try {
            if (bbbRepoContInfo != null) {
                this.copyContactAddress(bbbRepoContInfo, newBillingAddress);
                this.billingAddress = newBillingAddress;
            } else {
                this.logInfo("bbbRepoContInfo is null due to session time out");
            }
        } catch (final IntrospectionException exception) {
            this.logError(LogMessageFormatter.formatMessage(null, COPYING_ORDER_FAILED,
                            BBBCoreErrorConstants.CHECKOUT_ERROR_1000), exception);
        }
    }

	/**
	 * @param bbbRepoContInfo
	 * @param newBillingAddress
	 * @throws IntrospectionException
	 */
	protected void copyContactAddress(
			final BBBRepositoryContactInfo bbbRepoContInfo,
			final BBBAddressImpl newBillingAddress)
			throws IntrospectionException {
		AddressTools.copyAddress(bbbRepoContInfo.getRepositoryItem(), newBillingAddress);
	}

    /** @return Checkout State */
    public final CheckoutProgressStates getCheckoutState() {
        return this.checkoutState;
    }

    /** @param checkoutState Checkout State */
    public final void setCheckoutState(final CheckoutProgressStates checkoutState) {
        this.checkoutState = checkoutState;
    }

    /** @return Site ID */
    public final String getSiteId() {
        if (StringUtils.isEmpty(this.siteId)) {
            this.siteId = SiteContextManager.getCurrentSiteId();
        }
        return this.siteId;
    }

    /** @param siteId Site ID */
    public final void setSiteId(final String siteId) {
        this.siteId = siteId;
    }

    /** @return Save To Account */
    public final boolean getSaveToAccount() {
        return this.saveToAccount;
    }

    /** @param saveToAccount Save to Account */
    public final void setSaveToAccount(final boolean saveToAccount) {
        this.saveToAccount = saveToAccount;
    }

    /** @return Billing Address Container */
    public final BBBAddressContainer getBillingAddressContainer() {
        return this.billingAddressContainer;
    }

    /** @param pBillingAddressContainer Billing Address Container */
    public final void setBillingAddressContainer(final BBBAddressContainer pBillingAddressContainer) {
        this.billingAddressContainer = pBillingAddressContainer;
    }

    /** @return User Option */
    public final String getUserSelectedOption() {
        return this.userSelectedOption;
    }

    /** @param pUserSelectedOption User Selected Option */
    public final void setUserSelectedOption(final String pUserSelectedOption) {
        this.userSelectedOption = pUserSelectedOption;
    }

    /** @return Billing Address */
    public final BBBAddress getBillingAddress() {
        if (null == this.billingAddress) {
            this.billingAddress = new BBBAddressImpl();
        }

        return this.billingAddress;
    }

    /** @param billingAddress Billing Address */
    public final void setBillingAddress(final BBBAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    /** @return Checkout Manager */
    public final BBBCheckoutManager getCheckoutManager() {
        return this.checkoutManager;
    }

    /** @param checkoutManager Checkout Manager */
    public final void setCheckoutManager(final BBBCheckoutManager checkoutManager) {
        this.checkoutManager = checkoutManager;
    }

    /** @return Confirmed Email */
    public final String getConfirmedEmail() {
        return this.confirmedEmail;
    }

    /** @param confirmedEmail Confirmed Email */
    public final void setConfirmedEmail(final String confirmedEmail) {
        this.confirmedEmail = confirmedEmail;
    }

    /** @return BBB Address API */
    public final BBBAddressAPI getAddressAPI() {
        if (null == this.addressAPI) {
            this.addressAPI = new BBBAddressAPIImpl();

        }
        return this.addressAPI;
    }

    /** @param addressAPI BBB Address API */
    public final void setAddressAPI(final BBBAddressAPI addressAPI) {
        this.addressAPI = addressAPI;
    }

    /** @return Catalog Tools */
    public final BBBCatalogTools getCatalogTools() {
        return this.catalogTools;
    }

    /** @param catalogTools Catalog Tools */
    public final void setCatalogTools(final BBBCatalogTools catalogTools) {
        this.catalogTools = catalogTools;
    }

    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleSaveBillingAddress(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final String contextPath = pRequest.getContextPath();
        if (BBBCheckoutConstants.EDIT.equals(this.getUserSelectedOption())) {
            this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString());
        } else {
            this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
        }
        
        final BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
        this.logInfo("BBBBillingAddressFormHandler.handleSaveBillingAddress() :: profileID : "+this.getProfile().getRepositoryId() + " OrderID : " + order.getId());
        if(null != order.getCouponListVo() && order.getCouponListVo().size() > 0){
    		this.logInfo(" available coupons in order : " + order.getCouponListVo().get(0).getmPromoId());
    	}
        String redirectURL = "";
        if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL())
                        && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            redirectURL = contextPath + this.getCheckoutState().getFailureURL();
        }

        final String myHandleMethod = Thread.currentThread().getStackTrace()[0].getMethodName();
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();

        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.SAVE_BILLING_ADDRESS, myHandleMethod);
            /*
             * Comment Out Below Line PS-17599 Transaction transaction = null; */
            try {
                /*
                 * Comment Out Below Line PS-17599 transaction = this.ensureTransaction(); */
                if (this.getUserLocale() == null) {
                    this.setUserLocale(this.getUserLocale(pRequest, pResponse));
                }
                this.preSaveBillingAddress(pRequest);

                if (this.getFormError()) {
                	//there are form errors, log all form errors and redirect
                	for (Object obj : this.getFormExceptions()){
                		if(obj instanceof DropletException){
                			DropletException de=(DropletException) obj;
                			logDebug("exception message:"+de.getMessage());
                			logDebug("exception errorCode:"+de.getErrorCode());
                		}else{
                			//highly unlikely should not happen
                			logDebug("exception::"+obj.toString());
                		}
                	}
                	if (!checkFormRedirect (null, redirectURL, pRequest, pResponse)){
                		logDebug("redirect to errorURL:"+redirectURL);
                    return false;
                }
                }
                synchronized (this.getOrder()) {

                	//BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
                	if(isEmailSignUp()){
                		this.getSessionBean().setEmailChecked(CHECKED);
                	}
                	else{
                		this.getSessionBean().setEmailChecked(UNCHECKED);
                	}
                	order.setEmailSignUp(isEmailSignUp());
                	//If user selected option is other than paypal billing address then save the same address in order billing address
                	if(this.userSelectedOption != null && !this.userSelectedOption.equalsIgnoreCase(BBBPayPalConstants.PAYPAL_BILLING_ADDRESS)){

	                    this.saveBillingAddress(pRequest);
	                    if (!this.checkFormRedirect(null, redirectURL, pRequest, pResponse)) {
	                        return false;
	                    }
	                    this.postSaveBillingAddress(pRequest, pResponse);
                	}
                	//If user selects continue with paypal, then redirect user to review page directly as email and phone
                	//fields are disabled and coupon check will not be entertained for both logged in and anonymous user
                	//and also user selects paypal as payment method
                	else if(order.isPayPalOrder() && this.userSelectedOption != null && this.userSelectedOption.equalsIgnoreCase(BBBPayPalConstants.PAYPAL_BILLING_ADDRESS)){
                        this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
                	}
                	this.getOrderManager().updateOrder(this.getOrder());
                }
            this.logInfo("BBBBillingAddressFormHandler.handleSaveBillingAddress() end:: profileID : "+this.getProfile().getRepositoryId() + " OrderID : " + order.getId());
            if(null != order.getCouponListVo() && order.getCouponListVo().size() > 0){
        		this.logInfo(" available coupons in order : " + order.getCouponListVo().get(0).getmPromoId());
        	}
            } catch (final NumberFormatException exception) {
                this.addSystemException(ERR_CHECKOUT_INVALID_BILLING_ADDRESS, exception,
                                BBBCoreErrorConstants.CHECKOUT_ERROR_1002);
            } catch (final CommerceException exception) {
                this.addSystemException(ERR_CHECKOUT_INVALID_BILLING_ADDRESS, exception,
                                BBBCoreErrorConstants.CHECKOUT_ERROR_1003);
            } catch (final RepositoryException exception) {
                this.addSystemException(ERR_CHECKOUT_GENERIC_ERROR, exception,
                                BBBCoreErrorConstants.CHECKOUT_ERROR_1004);
            } catch (final IntrospectionException exception) {
                this.addSystemException(ERR_CHECKOUT_GENERIC_ERROR, exception,
                                BBBCoreErrorConstants.CHECKOUT_ERROR_1004);
            } catch (final BBBSystemException exception) {
                this.addSystemException(ERR_CHECKOUT_GENERIC_ERROR, exception,
                                BBBCoreErrorConstants.CHECKOUT_ERROR_1004);
            } catch (final BBBBusinessException exception) {
                this.addSystemException(ERR_CHECKOUT_GENERIC_ERROR, exception,
                                BBBCoreErrorConstants.CHECKOUT_ERROR_1004);
            } catch (final RunProcessException exception) {
                this.addSystemException(ERR_CHECKOUT_GENERIC_ERROR, exception,
                                BBBCoreErrorConstants.CHECKOUT_ERROR_1004);
            } finally {
                /*
                 * Comment Out Below Line PS-17599 if (transaction != null) { this.commitTransaction(transaction); } */
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.SAVE_BILLING_ADDRESS, myHandleMethod);
            }

            // will take to coupons or payment page as per the logic in the post method
            if (!this.isTransactionMarkedAsRollBack()) {
                if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL())
                                && !this.getCheckoutState().getFailureURL()
                                                .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
                    redirectURL = contextPath + this.getCheckoutState().getFailureURL();
                }
            }

            // In case of AJAX based request return true else redirect to page
            if (this.isFromAjaxSubmission()) {
                return true;
            }

            if (StringUtils.isEmpty(redirectURL) || "null".equalsIgnoreCase(redirectURL)) {
                redirectURL = null;
            }
            return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
        }
        return false;
    }

    /**
     * This method is used to set email sign up flag in order.
     * This method is called from mobile.
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleSetEmailSignUpInOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final String myHandleMethod = Thread.currentThread().getStackTrace()[0].getMethodName();
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();

        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.SET_EMAIL_SIGNUP_IN_ORDER, myHandleMethod);
            Transaction tr = null;
            try {
            	tr = this.ensureTransaction();
                synchronized (this.getOrder()) {

                	BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
                	order.setEmailSignUp(isEmailSignUp());
                	this.getOrderManager().updateOrder(this.getOrder());
                }
            } catch (final CommerceException exception) {
                this.addSystemException(ERR_SETTING_EMAIL_SIGNUP_IN_ORDER, exception,
                        BBBCoreErrorConstants.CHECKOUT_ERROR_1003);
            } finally {
            	if (tr != null) {
					this.commitTransaction(tr);
				}

                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.SET_EMAIL_SIGNUP_IN_ORDER, myHandleMethod);
            }

            if (this.isTransactionMarkedAsRollBack()) {
            	return false;
            }
            return true;
        }
        return false;
    }

    private boolean fromAjaxSubmission;

    /** @return From Ajax Submission */
    public final boolean isFromAjaxSubmission() {
        return this.fromAjaxSubmission;
    }

    /** @param fromAjaxSubmission From Ajax Submission */
    public final void setFromAjaxSubmission(final boolean fromAjaxSubmission) {
        this.fromAjaxSubmission = fromAjaxSubmission;
    }

    private void preSaveBillingAddress(DynamoHttpServletRequest pRequest) {
		// Validate state for billing address
		if (Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED))
				&& !StringUtils.isEmpty(this.getBillingAddress().getState()) && (!Boolean.valueOf(pRequest.getParameter("isInternationalBilling")))) {
			this.validateStateForBilling(this.getBillingAddress().getState().trim());
		}

        if (!StringUtils.isEmpty(this.getBillingAddress().getCountry())) {
            try {
                final Map<String, String> ccname = ((BBBCatalogToolsImpl) this.getCatalogTools())
                                .getCountriesInfo(this.getBillingAddress().getCountry());
                boolean isValidCountryForBilling = false;
                if ((ccname != null) && (ccname.size() == 1)) {
                    this.getBillingAddress().setCountryName(ccname.get(this.getBillingAddress().getCountry().trim()));
                    isValidCountryForBilling = true;
                }
				// Validate country for billing address
				if (!isValidCountryForBilling
						&& Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED))) {
					logDebug("invalid country:"+this.getBillingAddress().getCountry());
					logDebug("is_single_page_checkout_enagled:"+pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED));
					this.addPropertyException(ERR_CHECKOUT_INVALID_COUNTRY);
				}
            } catch (final BBBBusinessException e) {
                this.addSystemException(ERR_CHECKOUT_INVALID_COUNTRY, e, BBBCoreErrorConstants.CHECKOUT_ERROR_1004);
            } catch (final BBBSystemException e) {
                this.addSystemException(ERR_CHECKOUT_INVALID_COUNTRY, e, BBBCoreErrorConstants.CHECKOUT_ERROR_1004);
            }
        } else {
            this.getBillingAddress().setCountryName(null);
        }
        if ((null != this.getBillingAddress())
                        && (null != this.getUserSelectedOption())
                        && (this.getUserSelectedOption().equals(BBBCheckoutConstants.NEW) || this
                                        .getUserSelectedOption().equals(BBBCheckoutConstants.EDIT))) {
            final List<String> invalidProperties = this.getBillingAddress().getInvalidProperties();
            if ((null != invalidProperties) && !invalidProperties.isEmpty()) {
                for (final String property : invalidProperties) {
                    if (!((this.getUserSelectedOption().equals(BBBCheckoutConstants.EDIT) && property
                                    .equals(BBBCoreConstants.INVALID_EMAIL)) || (this.getUserSelectedOption().equals(
                                    BBBCheckoutConstants.EDIT) && property.equals(BBBCoreConstants.INVALID_PHONE)))) {
                    	logDebug("invalid property:"+property);
                    	if(property.equalsIgnoreCase(BBBCoreConstants.INVALID_ADDRESS1))
                    	{
                    		logDebug("Address1: "+getBillingAddress().getAddress1());	
                    	}
                    	logDebug("email:"+getBillingAddress().getEmail());
                    	logDebug("phone:"+this.getBillingAddress().getPhoneNumber());
                        this.addPropertyException(property);
                    }
                    if ((null != this.getUserSelectedOption()) && property.equals(BBBCoreConstants.INVALID_PHONE)) {
                    	logDebug("invalid phone:"+property);
                    	logDebug("phone:"+this.getBillingAddress().getPhoneNumber());
                        this.addPropertyException(property);
                    }
                }
            } else if ((null != this.getUserSelectedOption())
                            && !this.getUserSelectedOption().equals(BBBCheckoutConstants.EDIT)
                            && (StringUtils.isEmpty(this.getConfirmedEmail()) || !this.getConfirmedEmail().equalsIgnoreCase(
                                            this.getBillingAddress().getEmail()))) {
            	logDebug("email:"+getBillingAddress().getEmail());
            	logDebug("confirmed_email:"+getConfirmedEmail());
                this.addPropertyException(BBBCoreConstants.EMAIL);
            }
        } else if (null == this.getUserSelectedOption()) {
            this.addSystemException(BBBCheckoutConstants.NULL_BILL_ADDR, null,
                            BBBCoreErrorConstants.CHECKOUT_ERROR_1002);
        } else if (null != this.getBillingAddress()) {
            final List<String> invalidProperties = this.getBillingAddress().getInvalidProperties();
            if ((null != invalidProperties) && !invalidProperties.isEmpty()) {
                for (final String property : invalidProperties) {
                    if (property.equals(BBBCoreConstants.INVALID_PHONE)) {
                    	logDebug("property2:"+property);
                    	logDebug("phone2:"+this.getBillingAddress().getPhoneNumber());
                        this.addPropertyException(property);
                    }
                }
            }
        }
    }

	/**
	 * This method is used to validate state name/code available in billing
	 * address
	 *
	 * @param bbbAddress
	 * @param pRequest
	 * @param pResponse
	 */
	private void validateStateForBilling(final String billingState) {
		this.logDebug("Begin BBBBillingAddressFormHandler.validateStateForBilling");
		try {
			final List<StateVO> stateList = this.getCatalogTools()
					.getStateList();
			boolean isValidStateForBilling = false;
			for (StateVO stateVO : stateList) {
				if ((stateVO.getStateCode().equalsIgnoreCase(billingState) || stateVO
						.getStateName().equalsIgnoreCase(billingState))
						&& stateVO.isShowOnBilling()) {
					isValidStateForBilling = true;
				}
			}
			if (!isValidStateForBilling) {
				this.logInfo("Invalid state for billing in single page checkout - " + billingState);
				this.addPropertyException(BBBCheckoutConstants.INVALID_STATE);
			}
		} catch (BBBBusinessException be) {
			this.logError(LogMessageFormatter.formatMessage(null,
					"ERROR:  getStates call has failed for Billing on site id: "
							+ this.siteId), be);
			this.addSystemException(BBBCheckoutConstants.INVALID_STATE, null,
					BBBCoreErrorConstants.CHECKOUT_ERROR_1002);
		} catch (BBBSystemException se) {
			this.logError(LogMessageFormatter.formatMessage(null,
					"ERROR:  getStates call has failed for Billing on site id: "
							+ this.siteId), se);
			this.addSystemException(BBBCheckoutConstants.INVALID_STATE, null,
					BBBCoreErrorConstants.CHECKOUT_ERROR_1002);
		}

	}

	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @throws RunProcessException
	 * @throws RepositoryException
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@SuppressWarnings("unchecked")
	protected void postSaveBillingAddress(final DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                    throws RunProcessException, RepositoryException, ServletException, IOException {

        final boolean schoolPromoFlag = this.isSchoolPromotion();
        String couponOn = null;
		boolean repriceRequired = false;
        final BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
        this.logInfo("BBBBillingAddressFormHandler.postSaveBillingAddress is called. Order Id is  : "+order.getId() + 
        		":::Profile ID is : "+this.getProfile().getRepositoryId());
        	if(null != order.getCouponListVo() && order.getCouponListVo().size() > 0){
        		this.logInfo(" available coupons in order : " + order.getCouponListVo().get(0).getmPromoId());
        	}
		if (!Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED))) {
			try {
				final Map<String, String> configMap = this.getCatalogTools()
						.getConfigValueByconfigType("ContentCatalogKeys");
				final String orderSiteId = this.getOrder().getSiteId();

				if (orderSiteId.equalsIgnoreCase(configMap
						.get("BedBathUSSiteCode"))) {
					couponOn = this.getCatalogTools()
							.getContentCatalogConfigration("CouponTag_us")
							.get(0);
				} else if (orderSiteId.equalsIgnoreCase(configMap
						.get("BuyBuyBabySiteCode"))) {
					couponOn = this.getCatalogTools()
							.getContentCatalogConfigration("CouponTag_baby")
							.get(0);
				} else if (orderSiteId.equalsIgnoreCase(configMap
						.get("BedBathCanadaSiteCode"))) {
					couponOn = this.getCatalogTools()
							.getContentCatalogConfigration("CouponTag_ca")
							.get(0);
				}
				if (couponOn == null) {
					couponOn = TRUE;
				}
					if (this.billingEmailChanged || this.billingPhoneChanged || ((String)pRequest.getSession().getAttribute("couponMailId")!=null && ((String)pRequest.getSession().getAttribute("couponMailId")).equalsIgnoreCase(BBBCoreConstants.BLANK))) {
					if(getProfile().isTransient()){
						final List<RepositoryItem> availablePromotions = (List<RepositoryItem>) this.getProfile()
								.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);
						final List<RepositoryItem> removeAvailablePromotions = new ArrayList<RepositoryItem>();
						final List<RepositoryItem> activePromotions = (List<RepositoryItem>) getProfile().getPropertyValue(
								BBBCoreConstants.ACTIVE_PROMOTIONS);
						repriceRequired = true;
						if (couponOn.equalsIgnoreCase(TRUE)) {
							/*
							 * remove already granted email coupon promotion, only
							 * for guest user.
							 */
							boolean isStoreOnlyCoupons = false;
							if (activePromotions != null && !activePromotions.isEmpty()) {
								for (RepositoryItem promotionStatus : activePromotions) {
									RepositoryItem activePromotion = (RepositoryItem) promotionStatus
											.getPropertyValue(BBBCoreConstants.PROMOTION);
									List<RepositoryItem> coupons = (List<RepositoryItem>) promotionStatus
											.getPropertyValue(COUPONS);
									isStoreOnlyCoupons = Boolean.parseBoolean(String.valueOf(coupons.get(0)
											.getPropertyValue(TBSConstants.STORE_ONLY)));
									if (isStoreOnlyCoupons) {
										vlogDebug(
												"BBBBillingAddressFormHandler.postSaveBillingAddress: The applied promotion {0} attached to coupon {1} is store only",
												activePromotion, coupons.get(0));
									} else {
										removeAvailablePromotions.add(activePromotion);
										vlogDebug(
												"BBBBillingAddressFormHandler.postSaveBillingAddress: Added promotion: {0} to removeAvailablePromotions list to remove from available promotion list: {1}",
												activePromotion, availablePromotions);
									}
								}
							}
							vlogDebug(
									"BBBBillingAddressFormHandler.postSaveBillingAddress: Removing removeAvailablePromotions list: {0} from available promotion list: {1}",
									removeAvailablePromotions, availablePromotions);
							for (RepositoryItem removePromotion : removeAvailablePromotions) {
								this.getPromotionTools().removePromotion((MutableRepositoryItem) this.getProfile(),
										removePromotion, false);
								this.logInfo("promotion : "+ removePromotion.getRepositoryId() + " is removed from profile : " + this.getProfile().getRepositoryId());
								if (availablePromotions.contains(removePromotion)) {
									availablePromotions.remove(removePromotion);
								}
							}
							vlogDebug(
									"BBBBillingAddressFormHandler.postSaveBillingAddress: Removed removeAvailablePromotions list: {0} from available promotion list: {1}",
									removeAvailablePromotions, availablePromotions);
							((MutableRepositoryItem) this.getProfile()).setPropertyValue(
									BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
							/*
							 * Re-initializing all current active promotion to
							 * pricingModelHolder object.
							 */
							this.getPromotionTools().initializePricingModels(pRequest, pResponse);
						}
					}
					this.promotions = this.getPromotionLookupManager().populateValidPromotions(
							(Profile) this.getProfile(), order, this.getOrder().getSiteId(), true);
					this.promotions = this.getCouponUtil().applySchoolPromotion(this.promotions,
							(Profile) this.getProfile(), order);
					((BBBOrderImpl) this.getOrder()).setCouponMap(this.promotions);

				}
			} catch (final BBBException e) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"Error findind the coupons",
						BBBCoreErrorConstants.CHECKOUT_ERROR_1001), e);
			}
		}


             // Below code has been commented because of R2 requirement.
             // String orderType = order.getOnlineBopusItemsStatusInOrder();
             // if(BBBCheckoutConstants.BOPUS_ONLY.equalsIgnoreCase(orderType)) {
             // //If only BOPUS items are available in order then coupon page will not be displayed.
             // order.getCouponMap().clear();
             // schoolPromoFlag = false;
             // }

        if (this.getUserSelectedOption().equals(BBBCheckoutConstants.EDIT)) {
            this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString());
        } else if (((order.getCouponMap() != null) && (order.getCouponMap().size() > 0) && BBBCoreConstants.TRUE
                .equalsIgnoreCase(couponOn)) || schoolPromoFlag) {
        	if(repriceRequired){
        		this.rePriceOrder(this.getOrder());
        	}
            this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.COUPONS.toString());
        } else {
			// Remove reprice on the basis of FLAG
			if (!Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED))) {
				this.rePriceOrder(this.getOrder());
			}
			this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString());
        } //Re-price Order to get Tax Information
        if (BBBUtility.isNotEmpty(this.getCollegeAddress()) && TRUE.equalsIgnoreCase(this.getCollegeAddress())) {
            order.getBillingAddress().setCompanyName("");
        }

    }

    /** @return BBB Coupon Utility */
    public final BBBCouponUtil getCouponUtil() {
        return this.couponUtil;
    }

    /** @param couponUtil BBB Coupon Utility */
    public final void setCouponUtil(final BBBCouponUtil couponUtil) {
        this.couponUtil = couponUtil;
    }

    /** This method checks if school Promotion is available in Order coupon Map or not.
     *
     * @return boolean, true if school promotion available else false */
    private boolean isSchoolPromotion() {
        this.logDebug("Start: method isSchoolPromotion");

        boolean flag = false;
        if ((((BBBOrderImpl) this.getOrder()).getCouponMap() != null)
                        && (((BBBOrderImpl) this.getOrder()).getCouponMap().size() > 0)) {
            flag = ((BBBOrderImpl) this.getOrder()).getCouponMap().containsKey(BBBCheckoutConstants.SCHOOLPROMO);
        }
        this.logDebug("End: method isSchoolPromotion");

        return flag;
    }

    /** @param order Order
     * @throws RunProcessException Exception
     * @throws RepositoryException Exception */
    protected void rePriceOrder(final Order order) throws RunProcessException, RepositoryException {

        final HashMap<String, Object> paramsReprice = new HashMap<String, Object>();

        paramsReprice.put(PricingConstants.PRICING_OPERATION_PARAM, PricingConstants.OP_REPRICE_ORDER_TOTAL);
        paramsReprice.put(PricingConstants.ORDER_PARAM, order);
        paramsReprice.put(PricingConstants.LOCALE_PARAM, this.getUserLocale());
        //final RepositoryItem profileForOrder = this.getOrderManager().getOrderTools().getProfileTools()
          //              .getProfileForOrder(order);
        paramsReprice.put(PricingConstants.PROFILE_PARAM, getProfile());
        paramsReprice.put(PipelineConstants.ORDERMANAGER, this.getOrderManager());

        this.getOrderManager().getPipelineManager().runProcess(this.getRepriceOrderChainId(), paramsReprice);
    }

    /** @return Promotion Lookup Manager */
    public final PromotionLookupManager getPromotionLookupManager() {
        return this.promotionLookupManager;
    }

    /** @param promotionLookupManager Promotion Lookup Manager */
    public final void setPromotionLookupManager(final PromotionLookupManager promotionLookupManager) {
        this.promotionLookupManager = promotionLookupManager;
    }

    /** @return Promotion Tools */
    public final PromotionTools getPromotionTools() {
        return this.promotionTools;
    }

    /** @param promotionTools Promotion Tools */
    public final void setPromotionTools(final PromotionTools promotionTools) {
        this.promotionTools = promotionTools;
    }

    /** @param pRequest Request
     * @throws RepositoryException Exception
     * @throws IntrospectionException Exception
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception */
    private void saveBillingAddress(final DynamoHttpServletRequest pRequest)
                    throws RepositoryException, IntrospectionException, BBBSystemException, BBBBusinessException {

        final BBBOrder order = (BBBOrder) this.getOrder();
        final Profile profile = (Profile) this.getProfile();
        BBBAddress address = this.getBillingAddress();
        // fetching email and phone no. entered on the form and populating
        // address
        final String emailEntered = address.getEmail();
        final String mobileNumber = address.getMobileNumber();
        final String countryName = address.getCountryName();
        final String country = address.getCountry();
        final String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
        String isFromSPC = (String)pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED);
        if ((channel != null)&& (mobileNumber !=null) && (!getProfile().isTransient())
                && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel
                        .equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)) && !BBBUtility.isEmpty(isFromSPC) && BBBCoreConstants.TRUE.equalsIgnoreCase(isFromSPC)){
        this.getProfileTools().updateProperty(this.getPropertyManager().getMobileNumberPropertyName(),
                mobileNumber, profile);
        }
        // see if email and mobile number has changed or not
        if(null != order){
        	if(null != order.getBillingAddress()){
        		if(!BBBUtility.isEmpty(order.getBillingAddress().getEmail())
                        && !BBBUtility.isEmpty(this.getBillingAddress().getEmail())
                        && !order.getBillingAddress().getEmail().equalsIgnoreCase(this.getBillingAddress().getEmail())){
        			this.billingEmailChanged = true;
        		}
        		else if(StringUtils.isEmpty(order
                        .getBillingAddress().getEmail())){
        			this.billingEmailChanged = true;
        		}
        		if(!BBBUtility.isEmpty(order.getBillingAddress().getMobileNumber())
                        && !BBBUtility.isEmpty(this.getBillingAddress().getMobileNumber())
                        && !order.getBillingAddress().getMobileNumber().equalsIgnoreCase(this.getBillingAddress().getMobileNumber())){
        			this.billingPhoneChanged = true;
        		}
        		else if(StringUtils.isEmpty(order
                        .getBillingAddress().getMobileNumber())){
        			this.billingPhoneChanged = true;
        		}
        	}
        	else{
        		this.billingEmailChanged = true;
        		this.billingPhoneChanged = true;
        	}
        }

        /*// see if email has changed or not
        if ((null != order) && (null != order.getBillingAddress())
                        && !BBBUtility.isEmpty(order.getBillingAddress().getEmail())
                        && !BBBUtility.isEmpty(this.getBillingAddress().getEmail())
                        && !order.getBillingAddress().getEmail().equalsIgnoreCase(this.getBillingAddress().getEmail())) {
            this.billingEmailChanged = true;
        }
        // see if email has changed or not
        if (((null != order) && (null != order.getBillingAddress()) && (null != order.getBillingAddress().getEmail())
                && !StringUtils.isEmpty(this.getBillingAddress().getEmail()) && !order.getBillingAddress().getEmail()
                .equals(this.getBillingAddress().getEmail()))
                || (((null != order) && (null == order.getBillingAddress())) || StringUtils.isEmpty(order
                        .getBillingAddress().getEmail()))) {
            this.billingEmailChanged = true;
        }*/

        // if user did not add new billing address and selected from the
        // existing shipping addresses or profile's billing addresses then add
        // it to duplicate list so that when user comes to the billing page
        // again in the same session duplicate addresses should not be shown
        this.getSessionBean().setPreSelectedAddress(false);
        String sgOriginalEmail = "";
        String sgOriginalMobileNumber = "";
        String sgOriginalPhoneNumber = "";
        boolean sgAddress = false;
        if ((null != this.userSelectedOption) && !this.userSelectedOption.equals(BBBCheckoutConstants.NEW)
                        && !this.userSelectedOption.equals(BBBCheckoutConstants.EDIT)
                        && (null != this.getBillingAddressContainer())) {

            address = this.getBillingAddressContainer().getAddressFromContainer(this.userSelectedOption);
            // List of all shipping group addresses to cross check with billing Address. In few cases billing page
            // uses same address as shipping address(Object reference) and then the following code used to override emaill
            // and phone number so temporary stored these values and re initialized to shipping address
            // LTL RM-27483 Start
            List<String> sgList = new ArrayList<String>();
            for(Object sg : order.getShippingGroups()) {
            	if(sg instanceof HardgoodShippingGroup) {
            		sgList.add(((HardgoodShippingGroup) sg).getId());
            	}
            }

            if(sgList.contains(this.userSelectedOption)) {
            	sgAddress = true;
            	sgOriginalEmail = address.getEmail();
            	sgOriginalMobileNumber = address.getMobileNumber();
            	sgOriginalPhoneNumber = address.getPhoneNumber();
            }
            // LTL RM-27483 End
            address.setEmail(emailEntered);
            address.setPhoneNumber(mobileNumber);
            address.setMobileNumber(mobileNumber);
            if (this.userSelectedOption.equals(BBBCheckoutConstants.EDIT)) {
                address.setCountryName(countryName);
                address.setCountry(country);
            }
            this.getBillingAddressContainer().getDuplicate().clear();
            this.getBillingAddressContainer().getDuplicate().add(address);
            this.getSessionBean().setPreSelectedAddress(true);

        }

        //BBBSL-5304 Start
        if (!BBBUtility.isEmpty(country) && !(country.equalsIgnoreCase(US) || country.equalsIgnoreCase(CA)
				|| country.equalsIgnoreCase(CANADA))) {
			address.setState(country);
        }
        //BBBSL-5304 End
		//Start change for BBBP-3627
        if (getProfile().isTransient()) {
        	//Removing spaces from zipCode because createWallet don't support whitespace for canada.
        	String postalCode = address.getPostalCode();
        	if(!BBBUtility.isEmpty(postalCode)){
        		postalCode = postalCode.replaceAll(BBBCoreConstants.WHITE_SPACE, BBBCoreConstants.BLANK);
        	}
        	try {
        		getBbbGetCouponsManager().createWalletMobile(address.getEmail(), address.getPhoneNumber(),
        				address.getFirstName(), address.getLastName(), address.getAddress1(),
        				address.getCity(), address.getState(), postalCode);
        	} catch (BBBSystemException se) {
        		if (isLoggingError()) {
        			logError("BBBBillingAddressFormHandler.saveBillingAddress exception" , se);
        		}
        	} catch (BBBBusinessException be) {
        		if (isLoggingError()) {
        			logError("BBBBillingAddressFormHandler.saveBillingAddress exception" , be);
        		}
        	}
        }
		//End change for BBBP-3627

        if (null != this.getCheckoutManager()) {
            this.getCheckoutManager().saveBillingAddress(order, address);
        }
        // LTL RM-27483 Start
        if(sgAddress) {
        	address.setEmail(sgOriginalEmail);
            address.setPhoneNumber(sgOriginalPhoneNumber);
            address.setMobileNumber(sgOriginalMobileNumber);
        }
        // LTL RM-27483 End
        if ((null != order) && (null != order.getBillingAddress())
                        && !BBBUtility.isEmpty(order.getBillingAddress().getCountry())) {
            if (order.getBillingAddress().getCountry().equals(US)) {
                this.internationalCard = !((order.getBillingAddress().getCountry().equals(US)) && (this.getSiteId()
                                .equals(BED_BATH_US_ID) || this.getSiteId().equals(BUY_BUY_BABY_ID) || this.getSiteId()
                                .equals(TBS_BED_BATH_US_ID) || this.getSiteId().equals(TBS_BUY_BUY_BABY_ID)));
            } else if (order.getBillingAddress().getCountry().equals(CA)
                            || order.getBillingAddress().getCountry().equalsIgnoreCase(CANADA)) {
                this.internationalCard = !((order.getBillingAddress().getCountry().equals(CA) || order
                                .getBillingAddress().getCountry().equalsIgnoreCase(CANADA)) && (this.getSiteId()
                                .equals(BED_BATH_CANADA_ID) || this.getSiteId().equals(TBS_BED_BATH_CANADA_ID)));
            } else {
                this.internationalCard = true;
            }
        }

        if (this.internationalCard) {
            this.getSessionBean().setPreSelectedAddress(false);
        }

        // if user asks to save newly added billing address to her profile's
        // billing addresses list, then add that and make newly added profile's
        // billing address as duplicate for that session
        if (!this.internationalCard && this.getSaveToAccount()
                        && BBBBillingAddressFormHandler.isUserAuthenticated((Profile) this.getProfile())
                        && (null != this.getAddressAPI())) {
            if ((this.getBillingAddress() != null) && !BBBUtility.isEmpty(this.getBillingAddress().getAddress1())
                            && !BBBUtility.isEmpty(this.getBillingAddress().getPostalCode())
                            && !BBBUtility.isEmpty(this.getBillingAddress().getCity())
                            && !BBBUtility.isEmpty(this.getBillingAddress().getState())) {
                @SuppressWarnings ("rawtypes")
                final List addresses = this.getProfileTools().getAllBillingAddresses(this.getProfile());
                if ((addresses == null) || addresses.isEmpty()) {
                    final BBBAddress profileAddress = this.getAddressAPI().addNewShippingAddress(profile,
                                    this.getBillingAddress(), this.getSiteId(), true, true);
                    this.getBillingAddressContainer().getDuplicate().add(profileAddress);
                } else {
                    final BBBAddress profileAddress = this.getAddressAPI().addNewBillingAddress(profile,
                                    this.getBillingAddress(), this.getSiteId(), false);
                    this.getBillingAddressContainer().getDuplicate().add(profileAddress);
                }
            }
        }

        // If user does not have Mobile number in Profile then saving the mobile number entered by user at billing page
        // in to user profile, this mobile number is used to validate whether coupon is applicable or not for the user.
        if (!this.internationalCard && profile.isTransient() && !BBBUtility.isEmpty(mobileNumber)) {
            this.getProfileTools().updateProperty(this.getPropertyManager().getMobileNumberPropertyName(),
                            mobileNumber, profile);
        } else if (this.internationalCard && profile.isTransient()) {
            this.getProfileTools().updateProperty(this.getPropertyManager().getMobileNumberPropertyName(), null,
                            profile);
        }
        pRequest.getSession().setAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS,
                        String.valueOf(this.internationalCard));

    }

    /** @param profile Profile
     * @return Is Authenticated User */
    private static boolean isUserAuthenticated(final Profile profile) {
        final Integer securityStatus = (Integer) profile.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);

        return !((null != securityStatus) && (securityStatus.intValue() >= SECURITY_STATUS) && profile.isTransient());

    }

    /** Add Droplet Exception.
     *
     * @param property Property String */
    public final void addPropertyException(final String property) {
        this.logDebug("Invalid address property. " + property);

        this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERR_CHECKOUT_INVALID_INPUT,
                        BBBCoreConstants.DEFAULT_LOCALE, null), ERR_CHECKOUT_INVALID_INPUT + " : " + property));
    }

    /** Add System Exception.
     *
     * @param pMsg System Exception message
     * @param exception Exception
     * @param errorKey Error Key */
    public final void addSystemException(final String pMsg, final Exception exception, final String errorKey) {
        this.logError(errorKey + ": Adding Form Error : " + pMsg, exception);
        this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(pMsg, "EN", null),
                        BBBCoreErrorConstants.CHECKOUT_ERROR_1003));

        if (!this.isTransactionMarkedAsRollBack()) {
            try {
                this.setTransactionToRollbackOnly();
            } catch (final SystemException systemException) {
                this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 + ERROR_IN_TRANSACTION_ROLLBACK,
                                systemException);
            }
        }
    }

    /** Returns the CMS message handler to add form exceptions.
     *
     * @return Template Manager */
    public final LblTxtTemplateManager getMessageHandler() {
        return this.messageHandler;
    }

    /** Sets the CMS message handler to add form exceptions.
     *
     * @param messageHandler Message Handler */
    public final void setMessageHandler(final LblTxtTemplateManager messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public final boolean isLoggingDebug() {
        return this.getCommonConfiguration().isLoggingDebugForRequestScopedComponents();
    }

    @Override
    public void logInfo(final String pMessage) {
        if (this.getManageLogging().isBillingAddressHandlerLogging()) {
            this.logInfo(pMessage, null);
        }
    }

    @Override
    public final void logDebug(final String pMessage) {
        if (this.isLoggingDebug()) {
            this.logDebug(pMessage, null);
        }
    }

    /** @return the commonConfiguration */
    public final CommonConfiguration getCommonConfiguration() {
        return this.commonConfiguration;
    }

    /** @param commonConfiguration the commonConfiguration to set */
    public final void setCommonConfiguration(final CommonConfiguration commonConfiguration) {
        this.commonConfiguration = commonConfiguration;
    }

    /** @return mPmgr */
    public final BBBPropertyManager getPropertyManager() {
        return this.propertyManager;
    }

    /** @param propertyManager */
    public final void setPropertyManager(final BBBPropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    /** @return the mProfileTools */
    public final BBBProfileTools getProfileTools() {
        return this.profileTools;
    }

    /** @param pProfileTools the mProfileTools to set */
    public final void setProfileTools(final BBBProfileTools pProfileTools) {
        this.profileTools = pProfileTools;
    }

   /** Save billing Address for Rest API.
    *
    * @param pRequest DynamoHttpServletRequest
    * @param pResponse DynamoHttpServletResponse
    * @return Success / Failure
    * @throws ServletException Exception
    * @throws IOException Exception */
   public final boolean handleAddBillingAddressToOrder(final DynamoHttpServletRequest pRequest,
                   final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

       final Map<String, String> failure = this.getCheckoutState().getCheckoutFailureURLs();
       final Map<String, String> success = this.getCheckoutState().getCheckoutSuccessURLs();
       final HashMap<String, String> map = new HashMap<String, String>();
       map.put("PAYMENT", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       map.put("BILLING", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       map.put("SHIPPING_SINGLE", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       map.put("COUPONS", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       map.put("REVIEW", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       map.put("CART", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       map.put("GIFT", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       map.put("SHIPPING_MULTIPLE", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       map.put("GUEST", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
       this.getCheckoutState().setCheckoutFailureURLs(map);
       this.getCheckoutState().setCheckoutSuccessURLs(map);
       this.setCollegeAddress("false");
//       Call Save Billing Address
       final boolean status = this.handleSaveBillingAddress(pRequest, pResponse);

       this.getCheckoutState().setCheckoutFailureURLs(failure);
       this.getCheckoutState().setCheckoutSuccessURLs(success);
		/* Defect BSL-2661-Start */
       if(((BBBPaymentGroupManager) this.getPaymentGroupManager()).checkGiftCard((BBBOrderImpl) this.getOrder())){
			Transaction tr = null;
			try {
				tr = this.ensureTransaction();
				synchronized (this.getOrder()) {
					((BBBPaymentGroupManager) this.getPaymentGroupManager())
							.processPaymentGroupStatusOnLoad(this.getOrder());
				}
			} catch (CommerceException e) {
				this.logError(
						"handleAddBillingAddressToOrder :: repriceGiftCard-Error updating in Commerce Item in repository",
						e);
				markTransactionRollback();
			} finally {
				// Complete the transaction
					this.commitTransaction(tr);
			}
       }
		/* Defect BSL-2661-END */
       return status;

   }

   /**
    * This method is used to rollback transaction.
    *
    */
   private void markTransactionRollback() {
       if (!this.isTransactionMarkedAsRollBack()) {
           try {
               this.setTransactionToRollbackOnly();
           } catch (final SystemException e) {
               this.logError(BBBCoreErrorConstants.CART_ERROR_1021 + ": Error in marking the transaction rollback", e);
           }
       }
   }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBBillingAddressFormHandler [promotionLookupManager=").append(this.promotionLookupManager)
                        .append(", checkoutState=").append(this.checkoutState).append(", commonConfiguration=")
                        .append(this.commonConfiguration).append(", promotionTools=").append(this.promotionTools)
                        .append(", messageHandler=").append(this.messageHandler).append(", promotions=")
                        .append(this.promotions).append(", checkoutManager=").append(this.checkoutManager)
                        .append(", catalogTools=").append(this.catalogTools).append(", sessionBean=")
                        .append(this.sessionBean).append(", couponUtil=").append(this.couponUtil)
                        .append(", propertyManager=").append(this.propertyManager).append(", profileTools=")
                        .append(this.profileTools).append(", billingAddress=").append(this.billingAddress)
                        .append(", billingAddressContainer=").append(this.billingAddressContainer)
                        .append(", addressAPI=").append(this.addressAPI).append(", userSelectedOption=")
                        .append(this.userSelectedOption).append(", confirmedEmail=").append(this.confirmedEmail)
                        .append(", siteId=").append(this.siteId).append(", newAddress=").append(this.newAddress)
                        .append(", collegeAddress=").append(this.collegeAddress).append(", internationalCard=")
                        .append(this.internationalCard).append(", billingEmailChanged=")
                        .append(this.billingEmailChanged).append(", saveToAccount=").append(this.saveToAccount)
                        .append(", fromAjaxSubmission=").append(this.fromAjaxSubmission).append("]");
        return builder.toString();
    }
}

