package com.bbb.commerce.checkout.formhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.checkout.manager.BBBCreditCardTools;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.commerce.checkout.vbv.vo.BBBVerifiedByVisaVO;
import com.bbb.commerce.common.BBBVBVSessionBean;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.ManageCheckoutLogging;
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager;
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.common.event.PersistedInfoLogEvent;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.payment.giftcard.GiftCardBeanInfo;
import com.bbb.payment.giftcard.GiftCardStatus;
import com.bbb.paypal.BBBSetExpressCheckoutResVO;
import com.bbb.personalstore.manager.PersonalStoreManager;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.generic.BBBGenericSessionComponent;
import com.bbb.userprofiling.BBBCookieManager;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.CommonConfiguration;
import com.bbb.valuelink.ValueLinkGiftCardProcessor;
import com.bbb.valuelink.ValueLinkGiftCardUtil;
import com.cardinalcommerce.client.CentinelRequest;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.CommitOrderFormHandler;
import atg.commerce.states.OrderStates;
import atg.commerce.states.StateDefinitions;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.service.idgen.IdGenerator;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

/**
 * @author alakra
 * 
 */
public class BBBCommitOrderFormHandler extends CommitOrderFormHandler {

	private static final String UPDATE_ORDER_ATTRIBUTES = "updateOrderAttributes";

	private static final String AND_TOOK_TIME = "] and took time: ";

	private static final int MAX_CC_ATTEMPTS = 3;

	private static final String VALIDATE_GIFT_CARD = "validateGiftCard";
	private static final String CHECK_INVENTORY = "checkInventory";
	private static final String COMMIT_ORDER = "commitOrder";
	
	private static final String PRE_COMMIT_ORDER = "preCommitOrder";
	private static final String END = "]";
	private static final String ERR_CREDITCARD_AUTH_MAX_INVALIDATTEMPTS = "err_creditcard_auth_max_invalidattempts";
	private static final String ERROR_WHILE_GET_GIFT_CARD_BALANCE = ": Error while getGiftCardBalance [";
	private static final String FOR_ORDER = "] for order [";
	private static final String ERROR_WHILE_ROLLING_BACK_GIFT_CARD = ": Error while rolling back gift card [";
	private static final String ERROR_COMMITTING_ORDER = "Error Occured while committing an order:";
	private static final String ERROR_COMMITTING_ORDER_INVALID_STATE = "error_committing_order_invalid_state";
	
	private static final String CYBERSOURCE_INVALID_CVV = "cybersourceInvalidCVV";
	private static final String CYBERSOURCE_AVS_FAIL = "cybersourceAVSFail";
	private static final String JVM_PROPERTY = "weblogic.Name";
	private static final String PIPELINE_ERR_CREDIT_CARD_AUTH = "FailedCreditCardAuth";
	private static final String PIPELINE_ERR_PAYMENT_GRP_AUTH = "FailedPaymentGroupAuth";
	private static final String ERR_GIFTCARD_GETBALANCE = "err_giftcard_getbalance";
	private static final String ERR_SDD_PREVIEW = "err_sdd_preview_error";
	private static final String ERROR_INVALID_CVV = "err_checkout_invalidCVV";
	// R2.2 paypal error
	private static final String WSDL_CONFIG_TYPE = "WSDL_PayPal";
	private static final String error_type_one = "paypalErroCodeWindowOne";
	private static final String error_type_two = "paypalErroCodeWindowTwo";
	private static final String PAYPAL_SERVICE_ERROR = "PayPay_Service_Error";
	private static final String ERROR = "ERROR";
	private static final String SP_ERROR = "SP_ERROR";

	private CommonConfiguration commonConfiguration;
	private ValueLinkGiftCardUtil valueLinkGiftCardUtil;
	private LblTxtTemplateManager messageHandler;
	private ValueLinkGiftCardProcessor giftCardProcessor;
	private BBBInventoryManager inventoryManager;
	private BBBCreditCardTools creditCardTools;
	private BBBCatalogTools catalogTools;
	private CheckoutProgressStates checkoutState;
	private BBBSessionBean sessionBean;
	private BBBVBVSessionBean vbvSessionBean;
	private IdGenerator idGenerator;
	private ManageCheckoutLogging manageLogging;
	private BBBCheckoutManager checkoutManager;
	private Profile userProfile;
	private BBBPayPalSessionBean payPalSessionBean;	
	private String dcPrefix;
	private String cardVerNumber;
	private String cartAndCheckOutConfigType;

	private String onlineOrderPrefixKey;

	private String bopusOrderPrefixKey;

	private String paRes;
	private boolean mobileRequest;
	private String vbvAuthErrorUrl;
	private String vbvLookupSuccessUrl;
	private String vbvLookupErrorUrl;
	private static final int maxRandomNumberExclusive = 10;
	private static final String ORDER_DS_SEED_NAME = "OrderDS";
	private BBBPayPalServiceManager paypalServiceManager;
	private String payPalSucessURL;
	private String payPalErrorURL;
	private static final String ERR_CART_PAYPAL_SERVICE = "err_cart_paypal_service";
	private String payPalErrorPage;
	private String vbvONOFFConfigKey;
	private boolean paypalTokenExpired;
	private Map<String, String> errorMap = new HashMap<String, String>();
	private boolean payPalFailure;
	private boolean cybersourceFailure;
	private boolean isFromVBV;
	private boolean preCommitChecked;
	private boolean dummyOrders_inventoryCheckFlag = false;
	private String vbvSPCAuthErrorUrl;
	private String vbvSPCLookupSuccessUrl;
	private String vbvSPCLookupErrorUrl;
	private ProfileTools profileTools;
	
	/**
	 * @return the cybersourceFailure
	 */
	public boolean isCybersourceFailure() {
		return cybersourceFailure;
	}

	/**
	 * @param cybersourceFailure the cybersourceFailure to set
	 */
	public void setCybersourceFailure(boolean cybersourceFailure) {
		this.cybersourceFailure = cybersourceFailure;
	}
	
	/** The same day delivery manager. */
	private BBBSameDayDeliveryManager sameDayDeliveryManager;

	/**
	 * Gets the same day delivery manager.
	 *
	 * @return the same day delivery manager
	 */
	public BBBSameDayDeliveryManager getSameDayDeliveryManager() {
		return sameDayDeliveryManager;
	}

	/**
	 * Sets the same day delivery manager.
	 *
	 * @param sameDayDeliveryManager the new same day delivery manager
	 */
	public void setSameDayDeliveryManager(
			BBBSameDayDeliveryManager sameDayDeliveryManager) {
		this.sameDayDeliveryManager = sameDayDeliveryManager;
	}
	
	
	private BBBCookieManager mCookieManager;

	public BBBCookieManager getCookieManager() {
		return mCookieManager;
	}

	public void setCookieManager(BBBCookieManager pCookieManager) {
		this.mCookieManager = pCookieManager;
	}
	public ProfileTools getProfileTools() {
		return profileTools;
	}

	public void setProfileTools(ProfileTools profileTools) {
		this.profileTools = profileTools;
	}
	
	public String getVbvSPCAuthErrorUrl() {
		return vbvSPCAuthErrorUrl;
	}

	public void setVbvSPCAuthErrorUrl(String vbvSPCAuthErrorUrl) {
		this.vbvSPCAuthErrorUrl = vbvSPCAuthErrorUrl;
	}

	public String getVbvSPCLookupSuccessUrl() {
		return vbvSPCLookupSuccessUrl;
	}

	public void setVbvSPCLookupSuccessUrl(String vbvSPCLookupSuccessUrl) {
		this.vbvSPCLookupSuccessUrl = vbvSPCLookupSuccessUrl;
	}

	public String getVbvSPCLookupErrorUrl() {
		return vbvSPCLookupErrorUrl;
	}

	public void setVbvSPCLookupErrorUrl(String vbvSPCLookupErrorUrl) {
		this.vbvSPCLookupErrorUrl = vbvSPCLookupErrorUrl;
	}
	private BBBGenericSessionComponent bbbGenericSessionComponent;

	private PersonalStoreManager mPsManager;

	/**
	 * This method returns <code>PersonalStoreManager</code> contains name of
	 * the manager component to use and get the personal store and strategy
	 * details
	 * 
	 * @return the mPsManager in <code>PersonalStoreManager</code> format
	 */
	public PersonalStoreManager getPsManager() {
		return mPsManager;
	}

	/**
	 * This method sets the PersonalStoreManager to be used from component
	 * properties file and get the personal store and strategy details
	 * 
	 * @param mPsManager
	 *            the personal store manager to set
	 */
	public void setPsManager(final PersonalStoreManager mPsManager) {
		this.mPsManager = mPsManager;
	}

	public BBBGenericSessionComponent getBbbGenericSessionComponent() {
		return bbbGenericSessionComponent;
	}

	public void setBbbGenericSessionComponent(BBBGenericSessionComponent bbbGenericSessionComponent) {
		this.bbbGenericSessionComponent = bbbGenericSessionComponent;
	}

	public boolean isPreCommitChecked() {
		return preCommitChecked;
	}

	public void setPreCommitChecked(boolean preCommitChecked) {
		this.preCommitChecked = preCommitChecked;
	}

	public boolean isPayPalFailure() {
		return payPalFailure;
	}

	public void setPayPalFailure(boolean payPalFailure) {
		this.payPalFailure = payPalFailure;
	}

	public Map<String, String> getErrorMap() {
		return this.errorMap;
	}

	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	/**
	 * @return the payPalSessionBean
	 */
	public BBBPayPalSessionBean getPayPalSessionBean() {
		return this.payPalSessionBean;
	}

	/**
	 * @param payPalSessionBean
	 *            the payPalSessionBean to set
	 */
	public void setPayPalSessionBean(BBBPayPalSessionBean payPalSessionBean) {
		this.payPalSessionBean = payPalSessionBean;
	}

	public Profile getUserProfile() {
		return this.userProfile;
	}

	public void setUserProfile(Profile userProfile) {
		this.userProfile = userProfile;
	}

	/**
	 * @returns the VBV OnOFF Config Key
	 */
	public String getVbvONOFFConfigKey() {
		return this.vbvONOFFConfigKey;
	}

	/**
	 * Sets the VBV On Off config key
	 * 
	 * @param vbvONOFFConfigKey
	 */
	public void setVbvONOFFConfigKey(String vbvONOFFConfigKey) {
		this.vbvONOFFConfigKey = vbvONOFFConfigKey;
	}

	/**
	 * @return the payPalSucessURL
	 */
	public String getPayPalSucessURL() {
		return this.payPalSucessURL;
	}

	/**
	 * @param payPalSucessURL
	 *            the payPalSucessURL to set
	 */
	public void setPayPalSucessURL(String payPalSucessURL) {
		this.payPalSucessURL = payPalSucessURL;
	}

	/**
	 * @return the payPalErrorURL
	 */
	public String getPayPalErrorURL() {
		return payPalErrorURL;
	}

	/**
	 * @param payPalErrorURL
	 *            the payPalErrorURL to set
	 */
	public void setPayPalErrorURL(String payPalErrorURL) {
		this.payPalErrorURL = payPalErrorURL;
	}

	/**
	 * @return the paypalServiceManager
	 */
	public BBBPayPalServiceManager getPaypalServiceManager() {
		return this.paypalServiceManager;
	}

	/**
	 * @param paypalServiceManager
	 *            the paypalServiceManager to set
	 */
	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}

	/**
	 * Get the id generator component
	 * 
	 * @return IdGenerator
	 */
	public final IdGenerator getIdGenerator() {
		return this.idGenerator;
	}

	/**
	 * Sets the id generator component.
	 * 
	 * @param idGenerator
	 */
	public final void setIdGenerator(final IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	/**
	 * Gets the session bean.
	 * 
	 * @return the sessionBean
	 */
	public final BBBSessionBean getSessionBean() {
		return this.sessionBean;
	}

	/**
	 * Sets the session bean.
	 * 
	 * @param sessionBean
	 *            the sessionBean to set
	 */
	public final void setSessionBean(final BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	/**
	 * @return the onlineOrderPrefixKey
	 */
	public final String getOnlineOrderPrefixKey() {
		return this.onlineOrderPrefixKey;
	}

	/**
	 * @param onlineOrderPrefixKey
	 *            the onlineOrderPrefixKey to set
	 */
	public final void setOnlineOrderPrefixKey(final String onlineOrderPrefixKey) {
		this.onlineOrderPrefixKey = onlineOrderPrefixKey;
	}

	/**
	 * @return the bopusOrderPrefixKey
	 */
	public final String getBopusOrderPrefixKey() {
		return this.bopusOrderPrefixKey;
	}

	/**
	 * @param bopusOrderPrefixKey
	 *            the bopusOrderPrefixKey to set
	 */
	public final void setBopusOrderPrefixKey(final String bopusOrderPrefixKey) {
		this.bopusOrderPrefixKey = bopusOrderPrefixKey;
	}

	/**
	 * @return the cartAndCheckOutConfigType
	 */
	public final String getCartAndCheckOutConfigType() {
		return this.cartAndCheckOutConfigType;
	}

	/**
	 * @param cartAndCheckOutConfigType
	 *            the cartAndCheckOutConfigType to set
	 */
	public final void setCartAndCheckOutConfigType(final String cartAndCheckOutConfigType) {
		this.cartAndCheckOutConfigType = cartAndCheckOutConfigType;
	}

	/** @return Catalog Tools */
	public final BBBCatalogTools getCatalogTools() {

		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 *            Catalog Tools
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {

		this.catalogTools = catalogTools;
	}

	/** @return Credit Card Tools */
	public final BBBCreditCardTools getCreditCardTools() {
		return this.creditCardTools;
	}

	/**
	 * @param creditCardTools
	 *            Credit Card Tools
	 */
	public final void setCreditCardTools(final BBBCreditCardTools creditCardTools) {
		this.creditCardTools = creditCardTools;
	}

	/** @return Card Number */
	public final String getCardVerNumber() {
		return this.cardVerNumber;
	}

	/**
	 * @param cardVerNumber
	 *            Card Version Number
	 */
	public final void setCardVerNumber(final String cardVerNumber) {
		this.cardVerNumber = cardVerNumber;
	}

	/** @return the messageManager */
	public final LblTxtTemplateManager getMessageHandler() {
		return this.messageHandler;
	}

	/**
	 * @param messageHandler
	 *            the messageManager to set
	 */
	public final void setMessageHandler(final LblTxtTemplateManager messageHandler) {
		this.messageHandler = messageHandler;
	}

	/** @return the giftCardManager */
	public final ValueLinkGiftCardProcessor getGiftCardProcessor() {
		return this.giftCardProcessor;
	}

	/**
	 * @param giftCardProcessor
	 *            the giftCardManager to set
	 */
	public final void setGiftCardProcessor(final ValueLinkGiftCardProcessor giftCardProcessor) {
		this.giftCardProcessor = giftCardProcessor;
	}

	/** @return the inventoryManager */
	public final BBBInventoryManager getInventoryManager() {
		return this.inventoryManager;
	}

	/**
	 * @param inventoryManager
	 *            the inventoryManager to set
	 */
	public final void setInventoryManager(final BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	/** @return the mValueLinkGiftCardUtil */
	public final ValueLinkGiftCardUtil getValueLinkGiftCardUtil() {
		return this.valueLinkGiftCardUtil;
	}

	/**
	 * @param valueLinkGiftCardUtil
	 *            the mValueLinkGiftCardUtil to set
	 */
	public final void setValueLinkGiftCardUtil(final ValueLinkGiftCardUtil valueLinkGiftCardUtil) {
		this.valueLinkGiftCardUtil = valueLinkGiftCardUtil;
	}

	/** @return the manageLogging */
	public final ManageCheckoutLogging getManageLogging() {
		return this.manageLogging;
	}

	/**
	 * @param manageLogging
	 *            the manageLogging to set
	 */
	public final void setManageLogging(final ManageCheckoutLogging manageLogging) {
		this.manageLogging = manageLogging;
	}

	@Override
	public void preCommitOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		// return in case PreCommit is already checked
		if (isPreCommitChecked()) {
			return;
		}
		String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		final BBBOrder bbbOrder = (BBBOrder) this.getOrder();
		final long startTime = System.currentTimeMillis();
		this.logInfo("START: PRE-Committing order [" + bbbOrder.getId() + END);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRE_COMMIT_ORDER_PROCESS, PRE_COMMIT_ORDER);

		// Check CreditCard Validation
		if (getVbvSessionBean().getbBBVerifiedByVisaVO() == null) {
			this.validateCreditCard(pRequest);
		}

		// Check the inventory level
		this.checkInventory(pRequest);

		//If Checkout is being done with Same Day Delivery shipping method
		if (bbbOrder.getShippingGroups().size() == 1
				&& (ShippingGroup) bbbOrder.getShippingGroups().get(0) instanceof BBBHardGoodShippingGroup
				&& ((BBBHardGoodShippingGroup) bbbOrder.getShippingGroups().get(0))
				.getShippingMethod().equals(BBBCoreConstants.SDD)
				&& !StringUtils.isEmpty(((BBBHardGoodShippingGroup) bbbOrder
						.getShippingGroups().get(0)).getSddStoreId())) {
			boolean sameDayDeliveryFlag = false;
			String sddEligibleOn = BBBConfigRepoUtils.getStringValue(
					BBBCoreConstants.SAME_DAY_DELIVERY_KEYS,
					BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
			if (null != sddEligibleOn) {
				sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
			}
			// check if the same day delivery flag is true
			if (sameDayDeliveryFlag) {
				HashSet<String> storeIds = new HashSet<String>();
				storeIds.add(((BBBHardGoodShippingGroup) bbbOrder
						.getShippingGroups().get(0)).getSddStoreId());
				String eligibilityStatus = BBBCoreConstants.BLANK;
				try {
					eligibilityStatus = getSameDayDeliveryManager()
							.checkSBCInventoryForSdd(storeIds,
									bbbOrder, true);
					if (StringUtils.isBlank(eligibilityStatus)
							|| (eligibilityStatus
									.equals(BBBCoreConstants.ITEM_INELIGIBLE) || eligibilityStatus
									.equals(BBBCoreConstants.MARKET_INELIGIBLE) || eligibilityStatus
									.equals(BBBCoreConstants.ITEM_UNAVAILABLE))) {

						if (channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
							this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERR_SDD_PREVIEW, null, null), ERR_SDD_PREVIEW));
						}else{
							this.addFormException(new DropletException(ERR_SDD_PREVIEW, ERR_SDD_PREVIEW));
						}

					}
				} catch (BBBSystemException e) {
					if (channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
						this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERR_SDD_PREVIEW, null, null), ERR_SDD_PREVIEW));
					}else{
						this.addFormException(new DropletException(ERR_SDD_PREVIEW, ERR_SDD_PREVIEW));
					}
					this.logError("BBBSystemException in preCommitOrder", e);
				} catch (BBBBusinessException e) {
					if (channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
						this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERR_SDD_PREVIEW, null, null), ERR_SDD_PREVIEW));
					}else{
						this.addFormException(new DropletException(ERR_SDD_PREVIEW, ERR_SDD_PREVIEW));
					}
					this.logError("BBBBusinessException in preCommitOrder", e);
				}
			}
		}
		
		if (!this.validateGiftCard(bbbOrder)) {
			this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERR_GIFTCARD_GETBALANCE, BBBCoreConstants.DEFAULT_LOCALE, null), ERR_GIFTCARD_GETBALANCE));
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRE_COMMIT_ORDER_PROCESS, PRE_COMMIT_ORDER);
		this.logInfo("END: PRE-Committing order [" + bbbOrder.getId() + AND_TOOK_TIME + (System.currentTimeMillis() - startTime));
	}

	/**
	 * This method is used to submit the current order. It calls the
	 * <tt>preCommitOrder</tt> method, allowing the application to do any
	 * required work before the order is submitted. Next it ensures that the
	 * user is not trying to double submit the order by seeing if the shopping
	 * cart last order id is is equal to the current order id.
	 * <p>
	 * Assuming the order is not a duplicate submission, the form handler calls
	 * <tt>OrderManager.processOrder</tt> to actually place the order. If
	 * placing the order succeeds, then the form handler sets the shopping cart
	 * lastOrder to the current (just submitted) order and the currentOrder to
	 * null. Finally, it calls the postCommitOrder method for any
	 * application-specific post-processing.
	 * 
	 * @param pRequest
	 *            the servlet's request
	 * @param pResponse
	 *            the servlet's response
	 * @return a <code>boolean</code> value
	 * @exception ServletException
	 *                if there was an error while executing the code
	 * @exception IOException
	 *                if there was an error with servlet io
	 */

	@Override
	public boolean handleCommitOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.setCommitOrderSuccessURL(pRequest.getContextPath() + this.getCheckoutState().getSuccessURL((Profile) this.getProfile()));
		this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL());
		String currentCheckoutlevel = this.getCheckoutState().getCurrentLevel();
		if (!BBBPayPalConstants.REVIEW.equalsIgnoreCase(currentCheckoutlevel)) {
			this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERROR_COMMITTING_ORDER_INVALID_STATE, BBBCoreConstants.DEFAULT_LOCALE, null), ERROR_COMMITTING_ORDER_INVALID_STATE));
			logError("BBBCommitOrderFormHandler.handleCommitOrder :: user has clicked on" + "place order button but his current Checkout level is " + currentCheckoutlevel + " , so redirecting it to " + getCommitOrderErrorURL());
			return checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse);
		}
		// R2.2 Code | Story 83 Starts
		if (validateOrderForPPCall()) {
			return handleCheckoutWithPaypal(pRequest, pResponse);
		}
		// R2.2 Code | Story 83 Ends
		return this.commitOrderHandler(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected boolean commitOrderHandler(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		return super.handleCommitOrder(pRequest, pResponse);
	}

	//adding new method for single page checkout
	public final boolean handleSPCommitOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString());
		this.setCommitOrderSuccessURL(pRequest.getContextPath() + this.getCheckoutState().getSuccessURL((Profile) this.getProfile()));
		this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL());
//		String currentCheckoutlevel = this.getCheckoutState().getCurrentLevel();
//		if (!BBBPayPalConstants.REVIEW.equalsIgnoreCase(currentCheckoutlevel)) {
//			this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERROR_COMMITTING_ORDER_INVALID_STATE, BBBCoreConstants.DEFAULT_LOCALE, null), ERROR_COMMITTING_ORDER_INVALID_STATE));
//			logError("BBBCommitOrderFormHandler.handleCommitOrder :: user has clicked on" + "place order button but his current Checkout level is " + currentCheckoutlevel + " , so redirecting it to " + getCommitOrderErrorURL());
//			return checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse);
//		}
		// R2.2 Code | Story 83 Starts
		if (validateOrderForPPCall()) {
			return handleCheckoutWithPaypal(pRequest, pResponse);
		}
		// R2.2 Code | Story 83 Ends
		return this.commitOrderHandler(pRequest, pResponse);
	}

	/**
	 * This method is used to make a thin client call to Centinel MAPS. We will
	 * create object of CentineRequest and will add name value pairs of all the
	 * order and card related details. After this weâ€™ll call sendHttp
	 * method of CentinelRequest and fetch the response in the object of
	 * CentinelResponse class. Weâ€™ll check the ErrorNo and ErrorDesc from
	 * response and based on it weâ€™ll redirect.
	 * 
	 * @param pRequest
	 *            the servlet's request
	 * @param pResponse
	 *            the servlet's response
	 * @return a <code>boolean</code> value
	 * @exception ServletException
	 *                if there was an error while executing the code
	 * @exception IOException
	 *                if there was an error with servlet io
	 */

	@SuppressWarnings("finally")
	public final boolean handleVerifiedByVisaLookup(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		// set VBV session bean as null as user is going to place fresh order.
		getVbvSessionBean().setbBBVerifiedByVisaVO(null);
		Transaction transaction = null;
		boolean isExceptionOccur = false;
		boolean isSinglePageCheckout =	getCheckoutState().spcEligible((BBBOrderImpl)getOrder() , false);
		
			sessionBean.setSinglePageCheckout(isSinglePageCheckout);
		
		boolean errorExists = true;
		
			logInfo("is transaction enable : "+isEnsureTransaction() );
			if(!isSinglePageCheckout)
			{
				isSinglePageCheckout = isSpcForRecognizedUser(pRequest);
			}
			
			// validate credit card for express checkout
			this.validateCreditCard(pRequest);
			if (this.getFormExceptions().size() > 0) {
				if (isMobileRequest()) {
					this.setVbvAuthErrorUrl(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
					return checkFormRedirect(null, getVbvAuthErrorUrl(), pRequest, pResponse);
				}else if(isSinglePageCheckout){
					return checkFormRedirect(null, pRequest.getContextPath() + getVbvSPCLookupErrorUrl(), pRequest, pResponse);
				} else {
					return checkFormRedirect(null, pRequest.getContextPath() + getVbvLookupErrorUrl(), pRequest, pResponse);
				}
			}
			// Defect 25028 Start - Create DS Order Number
			/* Update the order attributes before processing the order */
			
			synchronized ((BBBOrder) this.getOrder()) {
				try {
					transaction = ensureTransaction();
					this.updateOrderAttributes(pRequest, pResponse, (BBBOrder) this.getOrder());
				} finally{
					commitTransaction(transaction);
				}
			}
	
			// Defect 25028 -End
			// check if order type is of PayPal or bopus or vbv flag is off then
			// directly call handle commit order.
			if (isOrderNotValidForVBV()) {
				logInfo("For order ID : " + ((BBBOrder) this.getOrder()).getId() + " is paypal order : " + (((BBBOrderImpl) this.getOrder()).isPayPalOrder()) + " is bopus order : " + (((BBBOrder) this.getOrder()).getOnlineBopusItemsStatusInOrder().equalsIgnoreCase(BBBVerifiedByVisaConstants.BOPUS_ONLY)) + " is vbv flag on : " + getVBVOnOffFlag());
				// for bopus paypal and vbv on off flag
				if (isMobileRequest()) {
					this.setCommitOrderErrorURL(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
					return this.commitOrderHandler(pRequest, pResponse);
				}else if(isSinglePageCheckout){
					return handleSPCommitOrder(pRequest, pResponse);
				}
				return handleCommitOrder(pRequest, pResponse);
			}
	
			// this block of code is to perform refresh and back button check.
			if (isPaymentProgressStep(pRequest)) {
				addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBVerifiedByVisaConstants.VBVRefreshBackError, BBBCoreConstants.DEFAULT_LOCALE, null), BBBVerifiedByVisaConstants.VBVRefreshBackError));
				if(isSinglePageCheckout){
					return this.checkFormRedirect(null, pRequest.getContextPath() + getVbvSPCAuthErrorUrl(), pRequest, pResponse);
						
				}
				return this.checkFormRedirect(null, pRequest.getContextPath() + getVbvAuthErrorUrl(), pRequest, pResponse);
			}
	
			BBBOrder bbbOrder = (BBBOrder) this.getOrder();
			CentinelRequest centinelRequest = new CentinelRequest();
			BBBVerifiedByVisaVO bBBVerifiedByVisaVO = new BBBVerifiedByVisaVO();
	
			// lookUpFlag checks whether to redirect user for authentication or
			// complete the order as non-authenticated.
			// Non Authenticated in case of no credit card or credit card other then
			// visa or MasterCard..
			// getCheckoutManager().vbvCentinelAddLookupRequest method is used to
			// add name / value pairs to Centinel request object to construct XML.
			logInfo("Calling Centinal Look up API Start");
	
			// add lookup call
			boolean lookUpFlag = this.getCheckoutManager().vbvCentinelAddLookupRequest(pRequest, bbbOrder, centinelRequest, bBBVerifiedByVisaVO, getVbvSessionBean());
	
		try {	
			
			if (lookUpFlag) {
				// this line of code is to perform refresh and back button
				// check.
				pRequest.getSession().setAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS, Boolean.TRUE);
	
				// getCheckoutManager().vbvCentinelSendLookupRequest method is
				// used to call send api of Centinel request object
				// to get centinel response and update order accordingly with
				// response attributes
				errorExists = getCheckoutManager().vbvCentinelSendLookupRequest(centinelRequest, getCardVerNumber(), bbbOrder, errorExists, this.getOrderManager(), getVbvSessionBean());
	
			}
		} // if error exists in fetching ThirdPartyURLs then skip card
			// authentication and complete order as non-authenticated.
		catch (CommerceException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest, "Error occured while updating order during Centinel MAPS Lookup call:", BBBCoreErrorConstants.CHECKOUT_ERROR_1003), e);
			isExceptionOccur = true;
		} catch (BBBSystemException e) {
			this.logError("Error occured while fetching key from ThirdPartyURLs during Centinel MAPS Lookup call", e);
			isExceptionOccur = true;
		} catch (BBBBusinessException e) {
			this.logError("Error occured while fetching key from ThirdPartyURLs during Centinel MAPS Lookup call", e);
			isExceptionOccur = true;
		} finally {
			logInfo("Calling Centinal Look up API END");
			return processPostCentinalLookupCall(pRequest, pResponse, transaction, isExceptionOccur,
					isSinglePageCheckout, errorExists);
		}
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @param transaction
	 * @param isExceptionOccur
	 * @param isSinglePageCheckout
	 * @param errorExists
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected boolean processPostCentinalLookupCall(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, Transaction transaction, boolean isExceptionOccur,
			boolean isSinglePageCheckout, boolean errorExists) throws ServletException, IOException {
		if (isExceptionOccur) {
			try {
				setTransactionToRollbackOnly();
			} catch (SystemException e) {
				this.logError("Error occured while rollback the transaction during Centinel MAPS Lookup call", e);
			}
		}
		commitTransaction(transaction);
		if (!errorExists) {
			// //if card is enrolled and there are no errors then return
			// true in case of Mobile channel.
			if (isMobileRequest()) {
				return true;
			}
			// if card is enrolled and there are no errors then redirect to
			// cCFrame.jsp to submit form to ACS URL in case of DesktopWeb
			// channel.
			getVbvSessionBean().getbBBVerifiedByVisaVO().setToken(BBBVerifiedByVisaConstants.HANDLE_LOOKUP);
			if(isSinglePageCheckout){
				return this.checkFormRedirect(pRequest.getContextPath() + getVbvSPCLookupSuccessUrl(), null, pRequest, pResponse);					
			}
			return this.checkFormRedirect(pRequest.getContextPath() + getVbvLookupSuccessUrl(), null, pRequest, pResponse);
		}
		
		// if card is not enrolled or error exists then call commit order.
		if (isMobileRequest()) {
			boolean preCommitStatus = preCommitMobileOrder(pRequest, pResponse);
			if (preCommitStatus) {
				return this.commitOrderHandler(pRequest, pResponse);
			} else {
				return false;
			}
		
		}else if(isSinglePageCheckout){
			return handleSPCommitOrder(pRequest, pResponse);
		}
		return handleCommitOrder(pRequest, pResponse);
	}

	private boolean preCommitMobileOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
		boolean preCommitStatus = false;
		try {
			preCommitOrder(pRequest, pResponse);
			if (!this.getFormExceptions().isEmpty()) {
				preCommitStatus = false;
			} else {
				preCommitStatus = true;
			}
			setPreCommitChecked(true);
		} catch (ServletException e) {
			preCommitStatus = false;
			this.logError("Exception occurred while preCommiting mobileOrder" + e);
		} catch (IOException e) {
			preCommitStatus = false;
			this.logError("Exception occurred while preCommiting mobileOrder" + e);
		}
		return preCommitStatus;
	}

	/**
	 * @param isVBVON
	 * @return
	 */
	private boolean isOrderNotValidForVBV() {
		return ((BBBOrderImpl) this.getOrder()).isPayPalOrder() || ((BBBOrder) this.getOrder()).getOnlineBopusItemsStatusInOrder().equalsIgnoreCase(BBBVerifiedByVisaConstants.BOPUS_ONLY) || !getVBVOnOffFlag() || ((BBBOrderImpl) this.getOrder()).isDummyOrder();
	}

	/**
	 * @param pRequest
	 * @return
	 */
	private boolean isPaymentProgressStep(final DynamoHttpServletRequest pRequest) {
		return pRequest.getSession().getAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS) != null && pRequest.getSession().getAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS).toString().equalsIgnoreCase(BBBVerifiedByVisaConstants.TRUE) && !isMobileRequest();
	}

	/**
	 * This method is used to get the vbv on/off flag
	 * 
	 * @return boolean flag isVBVON
	 */
	private boolean getVBVOnOffFlag() {
		boolean isVBVON = false;
		try {
			List<String> vbvTurnedON = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, getVbvONOFFConfigKey());
			if (vbvTurnedON != null && !vbvTurnedON.isEmpty()) {
				isVBVON = Boolean.parseBoolean(vbvTurnedON.get(0));
			}
		} catch (BBBSystemException bse) {
			this.logError("System Exception occured while fetching key(vbvONOFF) from FlagDrivenFunctions", bse);
			return isVBVON;
		} catch (BBBBusinessException bbe) {
			this.logError("Business Exception occured while fetching key(vbvONOFF) from FlagDrivenFunctions", bbe);
			return isVBVON;
		}
		return isVBVON;
	}

	/**
	 * This method is used to make a thin client call to Centinel MAPS. we will
	 * create object of CentineRequest and will add name value pairs of all the
	 * order and card related details. After this weâ€™ll call sendHttp
	 * method of CentinelRequest and fetch the response in the object of
	 * CentinelResponse class. Weâ€™ll check the ErrorNo and ErrorDesc from
	 * response and based on it weâ€™ll redirect user.
	 * 
	 * @param pRequest
	 *            the servlet's request
	 * @param pResponse
	 *            the servlet's response
	 * @return a <code>boolean</code> value
	 * @exception ServletException
	 *                if there was an error while executing the code
	 * @exception IOException
	 *                if there was an error with servlet io
	 */

	@SuppressWarnings("finally")
	public final boolean handleVerifiedByVisaAuth(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		boolean isSinglePageCheckout = sessionBean.isSinglePageCheckout();
		//check is spc is eligible for recognized user(if session expires and user checks out as guest)
		if(!isSinglePageCheckout)
		{
			isSinglePageCheckout = isSpcForRecognizedUser(pRequest);
		}
		
		// pares is response from card issuer's site which is set into
		// authenticateResponse.
		String authenticateResponse = getPaRes();
		// this block of code is to perform refresh and back button check.
		if (!isMobileRequest() && !getVbvSessionBean().getbBBVerifiedByVisaVO().getToken().equalsIgnoreCase(BBBVerifiedByVisaConstants.CC_Authenticate)) {
			addFormException(new DropletException(""));
			if(isSinglePageCheckout){
				return this.checkFormRedirect(null, pRequest.getContextPath() + getVbvSPCAuthErrorUrl(), pRequest, pResponse);
			}
			return this.checkFormRedirect(null, pRequest.getContextPath() + getVbvAuthErrorUrl(), pRequest, pResponse);
		}
		final Map<String, String> failure = this.getCheckoutState().getCheckoutFailureURLs();
		final Map<String, String> success = this.getCheckoutState().getCheckoutSuccessURLs();

		if (isMobileRequest()) {
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
			map.put("ERROR", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
			map.put("SP_ERROR", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
			this.getCheckoutState().setCheckoutFailureURLs(map);
			this.getCheckoutState().setCheckoutSuccessURLs(map);
		}
		BBBOrder bbbOrder = (BBBOrder) this.getOrder();
		BBBVerifiedByVisaVO bBBVerifiedByVisaVO = getVbvSessionBean().getbBBVerifiedByVisaVO();
		boolean errorExists = false;
		Transaction transaction = null;
		boolean isExceptionOccur = false;

		// check whether authenticateResponse is null or not. if null then
		// redirect to payment page with error. if not null then proceed for
		// second thin client call.
		try {

			transaction = ensureTransaction();
			if (authenticateResponse != null && authenticateResponse.length() > 0) {
				// this attribute was set from cCAuthenticate.jsp
				pRequest.getSession().removeAttribute(BBBVerifiedByVisaConstants.AUTH_RESPONSE);
				
				CentinelRequest centinelRequest = new CentinelRequest();
				logInfo("Calling Centinal Authenticate API Start");

				// authenticate thin client call
				getCheckoutManager().vbvCentinelAuthenticateRequest(bbbOrder, bBBVerifiedByVisaVO, authenticateResponse, centinelRequest, this.getMessageHandler(), this.getOrderManager(), getVbvSessionBean());

			}// if pares is null then redirect to payment page with error.
			else {
				bBBVerifiedByVisaVO.setMessage(this.getMessageHandler().getErrMsg(BBBVerifiedByVisaConstants.NoAuthResponse, BBBCoreConstants.DEFAULT_LOCALE, null));
				throw new BBBBusinessException(BBBVerifiedByVisaConstants.NoAuthResponse, this.getMessageHandler().getErrMsg(BBBVerifiedByVisaConstants.NoAuthResponse, BBBCoreConstants.DEFAULT_LOCALE, null));
			}

		} // if error exists in fetching ThirdPartyURLs then redirect to payment
			// page with failure.
		catch (BBBSystemException e) {
			this.logError("Error occured while fetching key from ThirdPartyURLs during Centinel MAPS Authenticate call.", e);
			isExceptionOccur = true;
			errorExists = true;
		}// if exception is thrown from
			// getCheckoutManager().vbvCentinelAuthenticateRequest as per
			// business logic then add form exception
		catch (BBBBusinessException e) {
			if (e.getErrorCode().equalsIgnoreCase(BBBVerifiedByVisaConstants.AuthenticateFailure) || e.getErrorCode().equalsIgnoreCase(BBBVerifiedByVisaConstants.NoAuthResponse)) {
				addFormException(new DropletException(bBBVerifiedByVisaVO.getMessage(), e.getErrorCode()));
			} else {
				this.logError("Error occured while fetching key from ThirdPartyURLs during Centinel MAPS Authenticate call.", e);
				isExceptionOccur = true;
			}
			errorExists = true;
		} catch (CommerceException e) {
			this.logError("Error occured while updating order during auth call for VBV", e);
			isExceptionOccur = true;
			errorExists = true;
		} finally {
			logInfo("Calling Centinal Authenticate API End");
			getVbvSessionBean().setbBBVerifiedByVisaVO(bBBVerifiedByVisaVO);
			// this line of code is to perform refresh and back button check.
			pRequest.getSession().removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS);
			if (isExceptionOccur) {
				try {
					setTransactionToRollbackOnly();
				} catch (SystemException e) {
					this.logError("Error occured while rollback the transaction during VBV Auth call", e);
				}
			}
			commitTransaction(transaction);
			return processPostCentinelAuthenicateCall(pRequest, pResponse, isSinglePageCheckout, failure, success,
					errorExists);
		}
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @param isSinglePageCheckout
	 * @param failure
	 * @param success
	 * @param errorExists
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected boolean processPostCentinelAuthenicateCall(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, boolean isSinglePageCheckout, final Map<String, String> failure,
			final Map<String, String> success, boolean errorExists) throws ServletException, IOException {
		if (errorExists) {
			if (isMobileRequest()) {
				this.getCheckoutState().setCheckoutFailureURLs(failure);
				this.getCheckoutState().setCheckoutSuccessURLs(success);
				return false;
			}
			if(isSinglePageCheckout){
				return this.checkFormRedirect(null, pRequest.getContextPath() + getVbvSPCAuthErrorUrl(), pRequest, pResponse);						
			}
			return this.checkFormRedirect(null, pRequest.getContextPath() + getVbvAuthErrorUrl(), pRequest, pResponse);
		}
		if (isMobileRequest()) {
			pRequest.setParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY,BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY_REQ);
			boolean status = this.commitOrderHandler(pRequest, pResponse);
			this.getCheckoutState().setCheckoutFailureURLs(failure);
			this.getCheckoutState().setCheckoutSuccessURLs(success);
			return status;
		}
		if(isSinglePageCheckout){
			return handleSPCommitOrder(pRequest, pResponse);
		}
		return handleCommitOrder(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @return success/failure
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws RunProcessException
	 *             Exception
	 */
	public final boolean handlePlaceCurrentOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException, RunProcessException {

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
		map.put("ERROR", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
		map.put("SP_ERROR", BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);

		this.getCheckoutState().setCheckoutFailureURLs(map);
		this.getCheckoutState().setCheckoutSuccessURLs(map);
		
		// Story 83 Handle token expired on paypal mobile Start
		if (((BBBOrderImpl) this.getOrder()).isPayPalOrder() && validateOrderForPPCall()) {
			this.setPaypalTokenExpired(true);
			return false;
		}
		// END

		// Start: 258 Verified By Visa
		this.setMobileRequest(true);
		// End: 258 Verified By Visa
		
		final boolean status = handleVerifiedByVisaLookup(pRequest, pResponse);

		// Removed the call
		// this.runRepricingProcess(pRequest, pResponse,
		// PricingConstants.OP_REPRICE_ORDER_TOTAL);

		this.getCheckoutState().setCheckoutFailureURLs(failure);
		this.getCheckoutState().setCheckoutSuccessURLs(success);

		return status;
	}

	/**
	 * @param pRequest
	 * @param bbbOrder
	 */
	private void updateOrderAttributes(final DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, final BBBOrder bbbOrder) {
		final long startTime = System.currentTimeMillis();
		this.logInfo("START: Update order attributes");
		final String mom365Affiliate = pRequest.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY);
		this.logDebug("Affiliate cookie value is:" + mom365Affiliate);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.COMMIT_ORDER_PROCESS, UPDATE_ORDER_ATTRIBUTES);
		try {
			/* Get the right order numbers for the order */
			final Map<String, String> configMap = this.getCatalogTools().getConfigValueByconfigType(this.getCartAndCheckOutConfigType());
			final String refId = pRequest.getCookieParameter("refId");
			// BBBP-5384 story changes start
			String siteId = getCurrentSiteId();
			boolean cookieFlag = true;
			if(!siteId.isEmpty() && (TBSConstants.SITE_TBS_BAB_US.equals(siteId) || TBSConstants.SITE_TBS_BBB.equals(siteId) || TBSConstants.SITE_TBS_BAB_CA.equals(siteId))){
				cookieFlag = false;
			}
			if(this.getUserProfile().isTransient() && cookieFlag){
				RepositoryItem billingEmailProfile = null;
				String billingEmail = bbbOrder.getBillingAddress().getEmail();
				if(StringUtils.isNotEmpty(billingEmail)){
					billingEmailProfile = getProfileTools().getItemFromEmail(billingEmail.toLowerCase());
				}
				
				String userCookieValue = pRequest.getCookieParameter(BBBCoreConstants.DYN_USER_ID);
				//check if existing cookie value is same as billing email profile link order with profile
				if(StringUtils.isNotEmpty(userCookieValue) && billingEmailProfile != null && billingEmailProfile.getRepositoryId().equals(userCookieValue)){
					bbbOrder.setProfileId(userCookieValue);
				} else if(billingEmailProfile != null){
				//if existing cookie value is different from billing email profile, update cookie value
					this.getCookieManager().createCookiesForProfile(billingEmailProfile.getRepositoryId(), pRequest, pResponse);
					bbbOrder.setProfileId(billingEmailProfile.getRepositoryId());
				}
			}
			// BBBP-5384 story changes end
			// checking inventory flag for dummy orders
			List<String> dummyOrder_inventoryCheck;
			dummyOrder_inventoryCheck = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK);
			dummyOrders_inventoryCheckFlag = Boolean.parseBoolean(dummyOrder_inventoryCheck.get(0));

			/* Persist the user IP */
			bbbOrder.setUserIP(pRequest.getRemoteAddr());
			
			//set sales OS from traffic OS variable - get from request header.
			final String trafficOS = BBBUtility.getTrafficOS();
			if(trafficOS != null){
				bbbOrder.setSalesOS(trafficOS);
			}

			// Dummy credit cart.
			if (((BBBOrderImpl) this.getOrder()).isDummyOrder()) {
				if (dummyOrders_inventoryCheckFlag) {
					bbbOrder.setSubStatus(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY);
				} else {
					bbbOrder.setSubStatus(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY);
				}
			}
			// Added for R2.2 : added device finger printing info to Order :
			// START
			// Defect Fix RM 25523
			String deviceFingerPrint = null;
			if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())) {
				if (BBBUtility.isNotEmpty(pRequest.getHeader(BBBCoreConstants.MOBILE_SESSION_ID))) {
					deviceFingerPrint = pRequest.getHeader(BBBCoreConstants.MOBILE_SESSION_ID).split("!")[0];
				}
			} else {
				deviceFingerPrint = pRequest.getSession().getId().split("!")[0];

			}
			((BBBOrderImpl) bbbOrder).setDeviceFingerprint(deviceFingerPrint);
			// Added for R2.2 : added device finger printing info to Order : END
			String ipHeaderName = null;
			if(configMap != null){
				ipHeaderName = configMap.get("TRUE_IP_HEADER");
			}
			if (BBBUtility.isNotEmpty(ipHeaderName)) {
				final String trueIP = pRequest.getHeader(ipHeaderName);
				this.logInfo(ipHeaderName + " value is :" + trueIP);
				if (BBBUtility.isNotEmpty(trueIP)) {
					bbbOrder.setUserIP(trueIP);
				}
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(mom365Affiliate)) {
				bbbOrder.setAffiliate(mom365Affiliate);
			} else if (null != refId) {
				// set the cookie param in request

				/*
				 * ReferralVO referralVO = (ReferralVO)
				 * getSessionBean().getValues
				 * ().get(BBBCoreConstants.REF_URL_VO);
				 */

				if (BBBCoreConstants.WED_CHANNEL_REF.equalsIgnoreCase(refId)) {
					bbbOrder.setAffiliate(this.getReferrer(BBBCoreConstants.WC_REFERRER_PATTERN));
				} else if (BBBCoreConstants.THEBUMP_REF.equalsIgnoreCase(refId)) {
					bbbOrder.setAffiliate(this.getReferrer(BBBCoreConstants.BP_REFERRER_PATTERN));
				}

			}

			// Added for BBBSL-1921: Generate valid Online & Bopus order id for
			// BOT orders || Starts
			final String orderId = bbbOrder.getId();
			String dsOrderId = null;

			if (!StringUtils.isBlank(orderId)) {
				// Start : R2.2 Added For New OrderId Generation for DS Online &
				// Bopus Orders with new IDSPACE
				logDebug("Generate New Order Numbers for DS");
				try {
					final Random randomNo = new Random();

					// Generate a random number

					final int randomNumber = randomNo.nextInt(maxRandomNumberExclusive);
					if (isLoggingDebug()) {
						logDebug("Next int value for Random OrderId generation is : " + randomNumber);
					}
					for (int i = 0; i <= randomNumber; i++) {
						dsOrderId = getIdGenerator().generateStringId(ORDER_DS_SEED_NAME);
					}
					if (BBBUtility.isEmpty(dsOrderId)) {
						logError("DS OrderId is generated Blank");
						// throw new Exception();
					}
					logInfo("New ONLINE/BOPUS/HYBRID Order Id generated For DS : " + dsOrderId + " for OrderID : " + orderId);
				} catch (Exception exc) {
					logError("Inside Exception: Exception occured while creating Ids for DS Order ID" , exc);
				}
			}

			/* Reset the order number values */
			bbbOrder.setOnlineOrderNumber(null);
			bbbOrder.setBopusOrderNumber(null);
			
			
			
			
			final String orderType = bbbOrder.getOnlineBopusItemsStatusInOrder();
			if (orderType.equalsIgnoreCase(BBBCheckoutConstants.HYBRID)) {
				this.addOnlineOrderNumber(bbbOrder, configMap, siteId, dsOrderId);
				this.addBopusOrderNumber(bbbOrder, configMap, siteId, dsOrderId);
			} else if (orderType.equalsIgnoreCase(BBBCheckoutConstants.ONLINE_ONLY) || orderType.equalsIgnoreCase(BBBCheckoutConstants.PAYPAL)) {
				this.addOnlineOrderNumber(bbbOrder, configMap, siteId, dsOrderId);
			} else if (orderType.equalsIgnoreCase(BBBCheckoutConstants.BOPUS_ONLY)) {
				this.addBopusOrderNumber(bbbOrder, configMap, siteId, dsOrderId);
			}
			// End : R2.2 Added For New OrderId Generation for DS Online & Bopus
			// Orders with new IDSPACE

			bbbOrder.setCreatedByOrderId(this.getDcPrefix() + "-" + System.getProperty(JVM_PROPERTY));
			this.getOrderManager().updateOrder(bbbOrder); 

		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_COMMITTING_ORDER, BBBCoreErrorConstants.CHECKOUT_ERROR_1003), e);

			this.addFormException(new DropletException(ERROR_COMMITTING_ORDER, e, BBBCoreErrorConstants.CHECKOUT_ERROR_1055));
		} catch (final BBBBusinessException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_COMMITTING_ORDER, BBBCoreErrorConstants.CHECKOUT_ERROR_1003), e);

			this.addFormException(new DropletException(ERROR_COMMITTING_ORDER, e, BBBCoreErrorConstants.CHECKOUT_ERROR_1056));
		} catch (final CommerceException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest, "Error occured while updating order before commit:", BBBCoreErrorConstants.CHECKOUT_ERROR_1003), e);
		} catch (ServletException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest, "Error occured while updating order before commit:", BBBCoreErrorConstants.CHECKOUT_ERROR_1073), e);
		} catch (IOException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest, "Error occured while updating order before commit:", BBBCoreErrorConstants.CHECKOUT_ERROR_1073), e);
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.COMMIT_ORDER_PROCESS, UPDATE_ORDER_ATTRIBUTES);
		this.logInfo("END: Update order attributes and took time: " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * @param configKey
	 *            Configuration Key
	 * @return Referrer
	 * @throws BBBSystemException
	 *             Exception
	 */
	public final String getReferrer(final String configKey) throws BBBSystemException {

		String referrer = null;
		try {
			referrer = this.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS).get(configKey);
		} catch (final BBBBusinessException e) {
			this.logError("BBBBusinessException occurred while fetching referrer", e);
		}
		return referrer;
	}

	// Start : R2.2 Added For New OrderId Generation for DS Online & Bopus
	// Orders with new IDSPACE
	private void addBopusOrderNumber(final BBBOrder bbbOrder, final Map<String, String> configMap, final String siteId, final String dsOrderID) {
		String bopusOrderPrefix = "OLP";
		if (!StringUtils.isBlank(siteId) && configMap != null && !configMap.isEmpty()) {
			bopusOrderPrefix = configMap.get(getBopusOrderPrefixKey().concat("_").concat(siteId));
		}
		bbbOrder.setBopusOrderNumber(bopusOrderPrefix.concat(dsOrderID));
		logInfo("BOPIS ORDER number generated : " + bbbOrder.getBopusOrderNumber());
	}

	private void addOnlineOrderNumber(final BBBOrder bbbOrder, final Map<String, String> configMap, final String siteId, final String dsOrderID) {
		String onlineOrderPrefix = "XXX";
		if (!StringUtils.isBlank(siteId) && configMap != null && !configMap.isEmpty()) {
			onlineOrderPrefix = configMap.get(getOnlineOrderPrefixKey().concat("_").concat(siteId));
		}
		bbbOrder.setOnlineOrderNumber(onlineOrderPrefix.concat(dsOrderID));
		logInfo("ONLINE ORDER number generated : " + bbbOrder.getOnlineOrderNumber());
	}

	// End : R2.2 Added For New OrderId Generation for DS Online & Bopus Orders
	// with new IDSPACE

	@SuppressWarnings("unchecked")
	@Override
	public void postCommitOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		Cookie lBCookie = null;
		Cookie lVCookie = null;
		List<String> lVCookieValLst = new ArrayList<String>();
		Order pOrder = getOrder();
		if (pOrder != null && this.getFormExceptions() != null && this.getFormExceptions().size() <= 0) {
			// Get the Last Bought and Last Viewed Cookie
			final Cookie[] cookies = pRequest.getCookies();
			if (null != cookies) {
				boolean flagLV = false;
				boolean flagLB = false;
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals(getPsManager().getLbCookieNme())) {
						lBCookie = cookies[i];
						flagLB = true;
					}
					if (cookies[i].getName().equals(getPsManager().getLvCookieNme())) {
						lVCookie = cookies[i];
						flagLV = true;
					}
					if (flagLV && flagLB) {
						logDebug("Last Bought Cookie and Last Viewed Cookie found");
						break;
					}
				}
			}

			logDebug("Generate the Last Bought Cookie");
			Cookie lastBoughtCookie = getPsManager().getLastBoughtCookie(pOrder, lVCookie, lBCookie, pRequest, pResponse);
			BBBUtility.addCookie(pResponse, lastBoughtCookie, true);
		}

		final List<RepositoryItem> availablePromotions = (List<RepositoryItem>) this.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);
		final List<RepositoryItem> selectedPromotions = (List<RepositoryItem>) this.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST);
		final List<RepositoryItem> activePromotions = (List<RepositoryItem>) this.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS);
		// reset invalid attempts
		this.getSessionBean().setCreditCardInvalidAttempts(0);
		availablePromotions.clear();
		selectedPromotions.clear();
		activePromotions.clear();
		this.getUserPricingModels().getItemPricingModels().clear();
		this.getUserPricingModels().getOrderPricingModels().clear();
		this.getUserPricingModels().getShippingPricingModels().clear();
		this.getUserPricingModels().initializeItemPricingModels();
		this.getUserPricingModels().initializeOrderPricingModels();
		this.getUserPricingModels().initializeShippingPricingModels();
		
		/*Resetting REFRESH_COUPON attribute after order submit*/
		pRequest.getSession().setAttribute(BBBCoreConstants.REFRESH_COUPON, null);
		
		// BSL-2562
		Order order = getOrder();
		String orderid = null;
		if (order != null) {
			orderid = order.getId();
		}

		java.util.Date today = new java.util.Date();
		java.sql.Timestamp dateTimeStamp = new java.sql.Timestamp(today.getTime());
		String time = dateTimeStamp.toString();
		String siteID = getCurrentSiteId();
		String channel = BBBUtility.getChannel();

		String longitude = getBbbGenericSessionComponent().getDeviceLocationLongitude();
		String latitude = getBbbGenericSessionComponent().getDeviceLocationLatitude();
		if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel)) {
			if (!StringUtils.isEmpty(longitude) && !StringUtils.isEmpty(latitude)) {
				logPersistedInfo("save_user_location", orderid, null, longitude, latitude, siteID, channel, null, time);
			}
			pRequest.getSession().setAttribute("couponMailId", BBBCoreConstants.BLANK);
		}
	}

	/**
	 * @return
	 */
	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	public void logPersistedInfo(String pMessage, String identifier, String upcCode, String longitude, String latitude, String siteID, String channel, String storeID, String time) {
		this.sendLogEvent(new PersistedInfoLogEvent(pMessage, identifier, upcCode, longitude, latitude, siteID, channel, storeID, time));
	}

	/**
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 */
	private void validateCreditCard(final DynamoHttpServletRequest pRequest) {
		@SuppressWarnings("unchecked")
		final List<PaymentGroup> paymentGroups = this.getOrder().getPaymentGroups();
		for (final PaymentGroup paymentGroup : paymentGroups) {
			if (paymentGroup instanceof BBBCreditCard) {
				final BBBCreditCard creditCard = (BBBCreditCard) paymentGroup;
				if (StringUtils.isEmpty(creditCard.getCardVerificationNumber()) && !StringUtils.isBlank(creditCard.getCreditCardNumber())) {
					this.logDebug("CVV number of CC is not set. User is using express Checkout. " + "\nSetting the CVV number into PG, CVV number entered in Review Page is - " + this.getCardVerNumber());
					final int ccreturn;
					if (this.getVbvSessionBean().getbBBVerifiedByVisaVO() != null) {
						ccreturn = this.getCreditCardTools().validateSecurityCode(this.getVbvSessionBean().getbBBVerifiedByVisaVO().getCardVerNumber(), creditCard.getCreditCardType());
					} else {
						ccreturn = this.getCreditCardTools().validateSecurityCode(this.getCardVerNumber(), creditCard.getCreditCardType());
					}
					if (ccreturn != 0) {
						this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_COMMITTING_ORDER, BBBCoreErrorConstants.CHECKOUT_ERROR_1005));
						this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(ERROR_INVALID_CVV, BBBCoreConstants.DEFAULT_LOCALE, null), "230")));
					} else {
						if (this.getVbvSessionBean().getbBBVerifiedByVisaVO() != null) {
							creditCard.setCardVerificationNumber(this.getVbvSessionBean().getbBBVerifiedByVisaVO().getCardVerNumber());
						} else {
							creditCard.setCardVerificationNumber(this.getCardVerNumber());
						}
					}
				}

			}
		}
	}

	@Override
	public final void commitOrder(final Order pOrder, final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		final long startTime = System.currentTimeMillis();
		this.logInfo("START: Committing order [" + pOrder.getId() + END);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.COMMIT_ORDER_PROCESS, "commitOrder");
		final BBBOrder bbbOrder = (BBBOrder) pOrder;
		
		boolean isSinglePageCheckout = sessionBean.isSinglePageCheckout();
		//check if SPC is valid for recognized user(if session expires and user checks out as guest)
		if(!isSinglePageCheckout)
		{
			isSinglePageCheckout = isSpcForRecognizedUser(pRequest);
		}
		
		// Defect 25028 Start Update the order attributes before processing the
		// order
		/* Update the order attributes before processing the order */
		// if(!isFromVBV()){
		logInfo("ORDER NUMBERS BEFORE: " + bbbOrder.getOnlineOrderNumber() + " - " + bbbOrder.getBopusOrderNumber());
		if (StringUtils.isBlank(bbbOrder.getOnlineOrderNumber()) && StringUtils.isBlank(bbbOrder.getBopusOrderNumber())) {
			synchronized (bbbOrder) {
				this.updateOrderAttributes(pRequest, pResponse, bbbOrder);
			}
		}
		logInfo("ORDER NUMBERS AFTER: " + bbbOrder.getOnlineOrderNumber() + " - " + bbbOrder.getBopusOrderNumber());
		// }
		// Defect 25028 End
		try {
			String collegeId = pRequest.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE);
			((BBBOrderImpl) pOrder).setCollegeId(collegeId);

			// BBBSL-2751. Refactor the code to update the sales channel using
			// OOB component
			this.updateOrderChannel();

			final PipelineResult result = this.getOrderManager().processOrder(getOrder(), this.getProcessOrderMap(this.getUserLocale()));
			if (this.processPipelineErrors(result) && this.isTransactionMarkedAsRollBack()) {
				if(isSinglePageCheckout){
					this.identifySPRedirect(result, pRequest, pResponse);
				}else{
				this.identifyRedirect(result, pRequest, pResponse);
				}

				this.handleErrorScenarios(bbbOrder);

			} else {
				try {
					final BBBOrderTools tools = (BBBOrderTools) this.getOrderManager().getOrderTools();
					if (bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY)) {
						tools.updateOrderSubstatus(bbbOrder, BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY);
					} else if (bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY)) {
						tools.updateOrderSubstatus(bbbOrder, BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY);
					} else {
						tools.updateOrderSubstatus(bbbOrder, BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED);
					}
				} catch (final BBBSystemException bse) {
					this.logError("BBBSystemException while updating the substatus", bse);
				}
				if (this.getShoppingCart() != null) {
					this.getShoppingCart().setLast(bbbOrder);
					this.getShoppingCart().setCurrent(null);
					this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
				}
			}
		} catch (final CommerceException exception) {
			this.handleErrorScenarios(bbbOrder);
			this.processException(exception, MSG_COMMIT_ERROR, pRequest, pResponse);
		} catch (BBBSystemException exception) {
			this.handleErrorScenarios(bbbOrder);
			this.processException(exception, MSG_COMMIT_ERROR, pRequest, pResponse);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.COMMIT_ORDER_PROCESS, COMMIT_ORDER);
		this.logInfo("END: Committing order [" + bbbOrder.getId() + AND_TOOK_TIME + (System.currentTimeMillis() - startTime));
	}

	/**
	 * This method sets the order sales channel as Mobile or MobiApp in the OOB
	 * method setSalesChannel() depending on from where the order has been
	 * placed. Default channel is Desktop. JIRA id BBBSL-2751
	 * 
	 * @param pRequest
	 */
	private void updateOrderChannel() {
		final String originOfTraffic = BBBUtility.getOriginOfTraffic();
		if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(originOfTraffic)) {
			this.setSalesChannel(BBBCoreConstants.CHANNEL_MOBILE_WEB);
		} else if (BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(originOfTraffic)) {
			this.setSalesChannel(BBBCoreConstants.CHANNEL_MOBILE_APP);
		} else if (BBBCoreConstants.THIRD_PARTY_MOBILE_APP.equalsIgnoreCase(originOfTraffic)) {
			this.setSalesChannel(BBBCoreConstants.CHANNEL_THIRD_PARTY_MOBILE_APP);
		}
	}

	/**
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 */
	private void checkInventory(final DynamoHttpServletRequest pRequest) {
		final long startTime = System.currentTimeMillis();
		this.logInfo("START: Verifying inventory stock for VDC items");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRE_COMMIT_ORDER_PROCESS, CHECK_INVENTORY);
		final BBBOrder order = (BBBOrder) this.getOrder();

		@SuppressWarnings("unchecked")
		final List<CommerceItem> commerceItems = order.getCommerceItems();
		int inventoryResult = 0;
		BBBCommerceItem bbbItem = null;
		final Map<String, String> placeholderMap = new HashMap<String, String>();

		for (final CommerceItem item : commerceItems) {
			if (item instanceof BBBCommerceItem) {
				bbbItem = (BBBCommerceItem) item;
				// Skip BOPUS items. Check inventory for VDC items
				if ((bbbItem.getStoreId() == null) && bbbItem.isVdcInd()) {
					try {
						inventoryResult = this.getInventoryManager().getVDCProductAvailability(order.getSiteId(), bbbItem.getCatalogRefId(), bbbItem.getQuantity(), BBBCoreConstants.CACHE_DISABLED);
						// If inventory is not available
						if (inventoryResult != BBBInventoryManager.AVAILABLE && inventoryResult != BBBInventoryManager.LIMITED_STOCK) {
							placeholderMap.put("product", (String) ((RepositoryItem) bbbItem.getAuxiliaryData().getCatalogRef()).getPropertyValue("displayName"));
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_INSUFFICIENT_INVENTORY, BBBCoreConstants.DEFAULT_LOCALE, placeholderMap), BBBCheckoutConstants.ERR_CHECKOUT_INSUFFICIENT_INVENTORY)));

							// redirect to cart page so that user can modify the
							// cart content or reduce item quantity
							this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
							if (!BBBCoreConstants.ATG_REST_IGNORE_REDIRECT.equalsIgnoreCase(this.getCheckoutState().getFailureURL())) {
								this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL());
							}
						}
					} catch (final InventoryException e) {
						if(isLoggingDebug()){
						this.logDebug(LogMessageFormatter.formatMessage(pRequest, ERROR_COMMITTING_ORDER, BBBCoreErrorConstants.CHECKOUT_ERROR_1006), e);
						}
					}
				}
			}
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRE_COMMIT_ORDER_PROCESS, CHECK_INVENTORY);
		this.logInfo("END: Verifying inventory stock for VDC items and took time: " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * @param pOrder
	 *            Order
	 */
	private void handleErrorScenarios(final Order pOrder) {
		// Check Order status. In case of Order status != SUBMITTED, revert Gift
		// Card balance
		final int orderSubmittedState = StateDefinitions.ORDERSTATES.getStateValue(OrderStates.SUBMITTED);
		if ((this.getFormExceptions().size() > 0) || (this.getOrder().getState() != orderSubmittedState)) {
			this.redeemRollbackGiftCardAmount(pOrder);
		}
	}

	/**
	 * @param pOrder
	 *            Order
	 */
	private void redeemRollbackGiftCardAmount(final Order pOrder) {
		this.logInfo("START: Rolling back Gift Card redeemed amount");
		@SuppressWarnings("unchecked")
		final List<PaymentGroup> paymentGroups = pOrder.getPaymentGroups();
		int count = 0;
		for (final PaymentGroup paymentGroup : paymentGroups) {
			if (paymentGroup instanceof BBBGiftCard) {
				count++;
				String gcPin = null;
				String gcNumber = null;
				String gcAmount = null;
				BBBGiftCard giftCard = null;

				giftCard = (BBBGiftCard) paymentGroup;

				@SuppressWarnings("unchecked")
				final List<GiftCardStatus> giftCardstatus = giftCard.getAuthorizationStatus();

				if ((giftCardstatus != null) && !giftCardstatus.isEmpty()) {
					for (final GiftCardStatus cardStatus : giftCardstatus) {
						if (cardStatus.getTransactionSuccess()) {
							this.logDebug("TransactionSuccess of GC is TRUE, RedeemRollback will happen");
							gcNumber = giftCard.getCardNumber();
							gcPin = giftCard.getPin();
							gcAmount = this.getValueLinkGiftCardUtil().getAmountInVLFormat(giftCard.getAmount());

							this.logDebug(new StringBuffer("Rolling back gift card [").append(gcNumber).append(FOR_ORDER).append(pOrder.getId()).append(END).toString());

							try {
								this.getGiftCardProcessor().redeemRollback(gcNumber, gcPin, gcAmount, pOrder.getId(), giftCard.getId(), this.getSiteIdFromManager());
							} catch (final BBBSystemException e) {
								this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1007 + ERROR_WHILE_ROLLING_BACK_GIFT_CARD + gcNumber + FOR_ORDER + pOrder.getId() + END, e);
							} catch (final BBBBusinessException e) {
								this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1007 + ERROR_WHILE_ROLLING_BACK_GIFT_CARD + gcNumber + FOR_ORDER + pOrder.getId() + END, e);
							}
						} else {
							this.logDebug("TransactionSuccess of GC is FALSE, RedeemRollback will not be performed");
						}
					}
				}
			}
		}

		if (0 < count) {
			this.logDebug("GiftCard payment group found during redeemRollbak, total gift card: " + count);
		} else if (1 > count) {
			this.logDebug("NO GIFTCARD Paymentgroup found during method redeemRollbackGiftCardAmount");
		}

		this.logInfo("END: Rolling back Gift Card redeemed amount");
	}

	/**
	 * @param pOrder
	 *            Order
	 * @return Gift Card Validation
	 */
	private boolean validateGiftCard(final Order pOrder) {
		final long startTime = System.currentTimeMillis();
		this.logInfo("START: getGiftCardBalance during preCommitOrder");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRE_COMMIT_ORDER_PROCESS, VALIDATE_GIFT_CARD);
		boolean flag = true;

		String gcPin = null;
		String gcNumber = null;
		String gcAmount = null;
		BBBGiftCard giftCard = null;

		@SuppressWarnings("unchecked")
		final List<PaymentGroup> paymentGroups = pOrder.getPaymentGroups();
		for (final PaymentGroup paymentGroup : paymentGroups) {
			if (paymentGroup instanceof BBBGiftCard) {
				giftCard = (BBBGiftCard) paymentGroup;

				gcNumber = giftCard.getCardNumber();
				gcPin = giftCard.getPin();
				gcAmount = this.getValueLinkGiftCardUtil().getAmountInVLFormat(giftCard.getAmount());

				try {
					final GiftCardBeanInfo giftInfo = this.getGiftCardProcessor().getBalance(gcNumber, gcPin, pOrder.getId(), giftCard.getId(), this.getSiteIdFromManager());

					if (giftInfo.getStatus() && (Double.parseDouble(giftInfo.getBalance()) >= Double.parseDouble(this.getValueLinkGiftCardUtil().getDollorCentAmount(gcAmount)))) {
						flag = true;
					} else {
						flag = false;
					}

				} catch (final BBBSystemException e) {
					this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1008 + ERROR_WHILE_GET_GIFT_CARD_BALANCE + gcNumber + FOR_ORDER + pOrder.getId() + END, e);
				} catch (final BBBBusinessException e) {
					this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1008 + ERROR_WHILE_GET_GIFT_CARD_BALANCE + gcNumber + FOR_ORDER + pOrder.getId() + END, e);
				}
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRE_COMMIT_ORDER_PROCESS, VALIDATE_GIFT_CARD);
		this.logInfo("END: getGiftCardBalance during preCommitOrder and took time: " + (System.currentTimeMillis() - startTime));
		return flag;
	}

	/**
	 * @return
	 */
	protected String getSiteIdFromManager() {
		return SiteContextManager.getCurrentSite().getId();
	}

	/**
	 * @param pResult
	 *            PipelineResult
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws BBBSystemException
	 */
	protected final void identifyRedirect(final PipelineResult pResult, final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws BBBSystemException {
		this.logInfo("START: Processing pipeline errors");
		// If there are pipeline errors, deal with them
		if ((pResult != null) && (pResult.getErrorKeys() != null) && (pResult.getErrorKeys().length > 0)) {
			this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": There are [" + pResult.getErrorKeys().length + "] pipeline error(s)");

			String errorKey = null;
			
		//	String error_codes_types_two = "";
			final List<Object> errorKeys = java.util.Arrays.asList(pResult.getErrorKeys());
			// R2.2 PAYPAL ERROR CODES
			
			for (int index = 0; index < errorKeys.size(); index++) {
				errorKey = (String) errorKeys.get(index);
				this.logDebug("Error Key: " + errorKey);

				this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error #" + index + " : [" + errorKey + "] : Description [" + pResult.getError(errorKey) + END);

				// R2.2 implementation paypal error redirection

				if (errorKey.startsWith(PAYPAL_SERVICE_ERROR)) {
					
					String error_codes_types_one = "";
					try {
						error_codes_types_one = (getCatalogTools().getAllValuesForKey(WSDL_CONFIG_TYPE, error_type_one).get(0));
					//	error_codes_types_two = (getCatalogTools().getAllValuesForKey(WSDL_CONFIG_TYPE, error_type_two).get(0));

					} catch (BBBSystemException e) {
						this.logError("Error occured while fetching key from WSDL_CONFIG_TYPE", e);

					} catch (BBBBusinessException e) {
						this.logError("Error occured while fetching key from WSDL_CONFIG_TYPE", e);

					}
					String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
					String errorCode = (String) pResult.getError(errorKey);
					if (channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
						
						
						// R2.2- Start -Add Form Exception on do auth call
						this.logDebug("Request is from Mobile, adding form exceptions");
						this.logError("Request is from Mobile, adding form exceptions for error: " + error_codes_types_one);
						// R2.2.1 Story - #8780 Redirect User to shipping page
						// if paypal rejects shipping address while placing
						// order. - Start
						if (errorCode.equalsIgnoreCase(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID_STRING)) {
							this.setPayPalErrorPage(BBBPayPalConstants.SHIPPING_PAGE);
							this.getFormExceptions().clear();
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, null, null))));
						}
						// R2.2.1 Story - #8780 Redirect User to shipping page
						// if paypal rejects shipping address while placing
						// order. - End
						else if (error_codes_types_one.contains(errorCode)) {
							this.setPayPalErrorPage(BBBPayPalConstants.BILLING);
							this.setPayPalFailure(true);
							this.getFormExceptions().clear();
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_PAYPAL_ERROR_ONE, null, null))));
						} else {
							this.logError("Request is from Mobile, adding form exceptions for error: " + errorCode);
							this.setPayPalErrorPage(BBBPayPalConstants.BILLING);
							this.setPayPalFailure(true);
							this.getFormExceptions().clear();
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_PAYPAL_ERROR_TWO, null, null))));
						}
						return;
						// R2.2 -End
					}
					this.logDebug("Request is from Desktop, redirecting to Error Page");
					// R2.2.1 Story - #8780 Redirect User to shipping page if
					// paypal rejects shipping address while placing order. -
					// Start
					if (errorCode.equalsIgnoreCase(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID_STRING)) {
						this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString());
						this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(BBBPayPalConstants.SHIPPING_SINGLE) + BBBPayPalConstants.PAYPAL_SHIPPING_ERROR);
					}
					// R2.2.1 Story - #8780 Redirect User to shipping page if
					// paypal rejects shipping address while placing order. -
					// End
					else if (error_codes_types_one.contains(errorCode)) {
						this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(ERROR) + BBBCheckoutConstants.PAYPAL_ERROR_ONE);
						this.logError("Request is from Desktop, Error " + error_codes_types_one + " redirecting to Error Page : " + this.getCommitOrderErrorURL());
					} else {
						this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(ERROR) + BBBPayPalConstants.PAYPAL_ERROR);
						this.logError("Request is from Desktop, Error " + errorCode + "redirecting to Error Page : " + this.getCommitOrderErrorURL());
					}

				}

				// Credit Card Auth failure
				else if (errorKey.startsWith(PIPELINE_ERR_CREDIT_CARD_AUTH)) {
					// remove Cybersource errors if any added by OOB code.

					if (this.getFormExceptions() != null) {
						this.getFormExceptions().clear();
					}
					// Error Scenarios for Rest
					if (BBBCoreConstants.ATG_REST_IGNORE_REDIRECT.equalsIgnoreCase(this.getCheckoutState().getFailureURL())) {

						// Re-factored the code for JIRA ticket BBBSL-11403 removed the transaction for error generation
						if (!BBBUtility.isEmpty(pRequest.getParameter(CYBERSOURCE_AVS_FAIL)) && pRequest.getParameter(CYBERSOURCE_AVS_FAIL).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
							this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, null, null), "200"));
							this.errorMap.put(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, BBBCoreConstants.DEFAULT_LOCALE, null));
						} else if (!BBBUtility.isEmpty(pRequest.getParameter(CYBERSOURCE_INVALID_CVV)) && pRequest.getParameter(CYBERSOURCE_INVALID_CVV).equalsIgnoreCase("true")) {
							this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, null, null), "230"));
							this.errorMap.put(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, BBBCoreConstants.DEFAULT_LOCALE, null));
						} else {
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_ERROR, null, null))));
							this.errorMap.put(BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_ERROR, this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_ERROR, BBBCoreConstants.DEFAULT_LOCALE, null));
						}
						// JIRA ticket BBBSL-11403 end
					}
					// End Error Scenarios

					// redirect to payment page so that user can modify the
					// credit card details
					this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.ERROR.toString());
					if (!BBBCoreConstants.ATG_REST_IGNORE_REDIRECT.equalsIgnoreCase(this.getCheckoutState().getFailureURL())) {
						if (!BBBUtility.isEmpty(pRequest.getParameter(CYBERSOURCE_AVS_FAIL)) && pRequest.getParameter(CYBERSOURCE_AVS_FAIL).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
							this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL() + "?cybersourceAVSFail=true");
						} else if (!BBBUtility.isEmpty(pRequest.getParameter(CYBERSOURCE_INVALID_CVV)) && pRequest.getParameter(CYBERSOURCE_INVALID_CVV).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
							this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL() + "?cybersourceInvalidCVV=true");
						} else {
							this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL());
						}
						final String maxInvalidCCAttemptsConfig = BBBConfigRepoUtils.getStringValue(this.getCartAndCheckOutConfigType(), "CARD_AUTH_MAX_ATTEMPTS");
						int maxInvalidCCAttempts = MAX_CC_ATTEMPTS; // set
																	// default
																	// value
						if (BBBUtility.isNotEmpty(maxInvalidCCAttemptsConfig)) {
							try {
								maxInvalidCCAttempts = Integer.parseInt(maxInvalidCCAttemptsConfig);
							} catch (final NumberFormatException nfe) {
								this.logError("Invalid Number format:" + maxInvalidCCAttemptsConfig, nfe);
							}
						}
						if (this.getSessionBean().getCreditCardInvalidAttempts() >= maxInvalidCCAttempts) {
							this.logInfo("maximum Invalid Auth Attempt reached");
							this.invalidateSession(pRequest);
							this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
							addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERR_CREDITCARD_AUTH_MAX_INVALIDATTEMPTS, null, null)));
							this.setCommitOrderErrorURL(pRequest.getContextPath() + getCheckoutState().getFailureURL());
						} else if (this.getSessionBean().getCreditCardInvalidAttempts() < maxInvalidCCAttempts) {
							this.logInfo("Auth failed. Current Invalid attemps:" + this.getSessionBean().getCreditCardInvalidAttempts());
							this.getSessionBean().setCreditCardInvalidAttempts(this.getSessionBean().getCreditCardInvalidAttempts() + 1);

						}
					}
					return;
				} else if (errorKey.startsWith(PIPELINE_ERR_PAYMENT_GRP_AUTH)) {
					this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_PAYGRP_ERROR, null, null), BBBCheckoutConstants.ERR_CHECKOUT_PAYGRP_ERROR));
				}else if (errorKey.startsWith(BBBCoreErrorConstants.SAME_DAY_DELIV_ERROR)) {
					String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
					if (channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
					this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCoreErrorConstants.SAME_DAY_DELIV_ERROR, null, null), BBBCoreErrorConstants.SAME_DAY_DELIV_ERROR));
					}else{
						this.addFormException(new DropletException(BBBCoreErrorConstants.SAME_DAY_DELIV_ERROR, (String) pResult.getError(errorKey)));
					}
				}else {
					this.addFormException(new DropletException(errorKey, errorKey));
				}
			}
		} else {
			this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.GENERIC_ERROR_TRY_LATER, null, null), BBBCheckoutConstants.GENERIC_ERROR_TRY_LATER));
		}

		this.logInfo("END: Processing pipeline errors");
	}

	
	/**
	 * @param pResult
	 *            PipelineResult
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws BBBSystemException
	 */
	protected final void identifySPRedirect(final PipelineResult pResult, final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws BBBSystemException {
		this.logInfo("START: Processing pipeline errors");
		// If there are pipeline errors, deal with them
		if ((pResult != null) && (pResult.getErrorKeys() != null) && (pResult.getErrorKeys().length > 0)) {
			this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": There are [" + pResult.getErrorKeys().length + "] pipeline error(s)");
			
			String errorKey = null;
			final List<Object> errorKeys = java.util.Arrays.asList(pResult.getErrorKeys());
			
			for (int index = 0; index < errorKeys.size(); index++) {
				errorKey = (String) errorKeys.get(index);
				this.logDebug("Error Key: " + errorKey);

				this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error #" + index + " : [" + errorKey + "] : Description [" + pResult.getError(errorKey) + END);

				// R2.2 implementation paypal error redirection

				if (errorKey.startsWith(PAYPAL_SERVICE_ERROR)) {
					
					// R2.2 PAYPAL ERROR CODES
					String error_codes_types_one = "";
					try {
						error_codes_types_one = (getCatalogTools().getAllValuesForKey(WSDL_CONFIG_TYPE, error_type_one).get(0));
					//	error_codes_types_two = (getCatalogTools().getAllValuesForKey(WSDL_CONFIG_TYPE, error_type_two).get(0));

					} catch (BBBSystemException e) {
						this.logError("Error occured while fetching key from WSDL_CONFIG_TYPE", e);

					} catch (BBBBusinessException e) {
						this.logError("Error occured while fetching key from WSDL_CONFIG_TYPE", e);

					}
					String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
					String errorCode = (String) pResult.getError(errorKey);
					if (channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
						// R2.2- Start -Add Form Exception on do auth call
						this.logDebug("Request is from Mobile, adding form exceptions");
						this.logError("Request is from Mobile, adding form exceptions for error: " + error_codes_types_one);
						// R2.2.1 Story - #8780 Redirect User to shipping page
						// if paypal rejects shipping address while placing
						// order. - Start
						if (errorCode.equalsIgnoreCase(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID_STRING)) {
							this.setPayPalErrorPage(BBBPayPalConstants.SP_CHECKOUT_SINGLE);
							this.getFormExceptions().clear();
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, null, null))));
						}
						// R2.2.1 Story - #8780 Redirect User to shipping page
						// if paypal rejects shipping address while placing
						// order. - End
						else if (error_codes_types_one.contains(errorCode)) {
							this.setPayPalErrorPage(BBBPayPalConstants.SP_CHECKOUT_SINGLE);
							this.setPayPalFailure(true);
							this.getFormExceptions().clear();
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_PAYPAL_ERROR_ONE, null, null))));
						} else {
							this.logError("Request is from Mobile, adding form exceptions for error: " + errorCode);
							this.setPayPalErrorPage(BBBPayPalConstants.SP_CHECKOUT_SINGLE);
							this.setPayPalFailure(true);
							this.getFormExceptions().clear();
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_PAYPAL_ERROR_TWO, null, null))));
						}
						return;
						// R2.2 -End
					}
					this.logDebug("Request is from Desktop, redirecting to Error Page");
					// R2.2.1 Story - #8780 Redirect User to shipping page if
					// paypal rejects shipping address while placing order. -
					// Start
					if (errorCode.equalsIgnoreCase(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID_STRING)) {
						this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_CHECKOUT_SINGLE.toString());
						this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(BBBPayPalConstants.SP_CHECKOUT_SINGLE) + BBBPayPalConstants.PAYPAL_SHIPPING_ERROR);
					}
					// R2.2.1 Story - #8780 Redirect User to shipping page if
					// paypal rejects shipping address while placing order. -
					// End
					else if (error_codes_types_one.contains(errorCode)) {
						this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(SP_ERROR) + BBBCheckoutConstants.PAYPAL_ERROR_ONE);
						this.logError("Request is from Desktop, Error " + error_codes_types_one + " redirecting to Error Page : " + this.getCommitOrderErrorURL());
					} else {
						this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(SP_ERROR) + BBBPayPalConstants.PAYPAL_ERROR);
						this.logError("Request is from Desktop, Error " + errorCode + "redirecting to Error Page : " + this.getCommitOrderErrorURL());
					}

				}

				// Credit Card Auth failure
				else if (errorKey.startsWith(PIPELINE_ERR_CREDIT_CARD_AUTH)) {
					// remove Cybersource errors if any added by OOB code.

					if (this.getFormExceptions() != null) {
						this.getFormExceptions().clear();
					}
					// Error Scenarios for Rest
					if (BBBCoreConstants.ATG_REST_IGNORE_REDIRECT.equalsIgnoreCase(this.getCheckoutState().getFailureURL())) {
						// Re-factored the code for JIRA ticket BBBSL-11403 removed the transaction for error generation
						if (!BBBUtility.isEmpty(pRequest.getParameter(CYBERSOURCE_AVS_FAIL)) && pRequest.getParameter(CYBERSOURCE_AVS_FAIL).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
							this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, null, null), "200"));
							this.errorMap.put(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, BBBCoreConstants.DEFAULT_LOCALE, null));
						} else if (!BBBUtility.isEmpty(pRequest.getParameter(CYBERSOURCE_INVALID_CVV)) && pRequest.getParameter(CYBERSOURCE_INVALID_CVV).equalsIgnoreCase("true")) {
							this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, null, null), "230"));
							this.errorMap.put(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CYBERSOURCE_ERROR, BBBCoreConstants.DEFAULT_LOCALE, null));
						} else {
							this.addFormException((new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_ERROR, null, null))));
							this.errorMap.put(BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_ERROR, this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_CREDITCARD_ERROR, BBBCoreConstants.DEFAULT_LOCALE, null));
						}
						// JIRA ticket BBBSL-11403 end
					}
					// End Error Scenarios

					// redirect to payment page so that user can modify the
					// credit card details
					this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_ERROR.toString());
					if (!BBBCoreConstants.ATG_REST_IGNORE_REDIRECT.equalsIgnoreCase(this.getCheckoutState().getFailureURL())) {
						if (!BBBUtility.isEmpty(pRequest.getParameter(CYBERSOURCE_AVS_FAIL)) && pRequest.getParameter(CYBERSOURCE_AVS_FAIL).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
							this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL() + "?cybersourceAVSFail=true");
						} else if (!BBBUtility.isEmpty(pRequest.getParameter(CYBERSOURCE_INVALID_CVV)) && pRequest.getParameter(CYBERSOURCE_INVALID_CVV).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
							this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL() + "?cybersourceInvalidCVV=true");
						} else {
							this.setCommitOrderErrorURL(pRequest.getContextPath() + this.getCheckoutState().getFailureURL());
						}
						final String maxInvalidCCAttemptsConfig = BBBConfigRepoUtils.getStringValue(this.getCartAndCheckOutConfigType(), "CARD_AUTH_MAX_ATTEMPTS");
						int maxInvalidCCAttempts = MAX_CC_ATTEMPTS; // set
																	// default
																	// value
						if (BBBUtility.isNotEmpty(maxInvalidCCAttemptsConfig)) {
							try {
								maxInvalidCCAttempts = Integer.parseInt(maxInvalidCCAttemptsConfig);
							} catch (final NumberFormatException nfe) {
								this.logError("Invalid Number format:" + maxInvalidCCAttemptsConfig, nfe);
							}
						}
						if (this.getSessionBean().getCreditCardInvalidAttempts() >= maxInvalidCCAttempts) {
							this.logInfo("maximum Invalid Auth Attempt reached");
							this.invalidateSession(pRequest);
							this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
							addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERR_CREDITCARD_AUTH_MAX_INVALIDATTEMPTS, null, null)));
							this.setCommitOrderErrorURL(pRequest.getContextPath() + getCheckoutState().getFailureURL());
						} else if (this.getSessionBean().getCreditCardInvalidAttempts() < maxInvalidCCAttempts) {
							this.logInfo("Auth failed. Current Invalid attemps:" + this.getSessionBean().getCreditCardInvalidAttempts());
							this.getSessionBean().setCreditCardInvalidAttempts(this.getSessionBean().getCreditCardInvalidAttempts() + 1);

						}
					}
					return;
				} else if (errorKey.startsWith(PIPELINE_ERR_PAYMENT_GRP_AUTH)) {
					this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.ERR_CHECKOUT_PAYGRP_ERROR, null, null), BBBCheckoutConstants.ERR_CHECKOUT_PAYGRP_ERROR));
				} else if (errorKey.startsWith(BBBCoreErrorConstants.SAME_DAY_DELIV_ERROR)) {
					this.addFormException(new DropletException(BBBCoreErrorConstants.SAME_DAY_DELIV_ERROR, (String) pResult.getError(errorKey)));
				}else {
					this.addFormException(new DropletException(errorKey, errorKey));
				}
			}
		} else {
			this.addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBCheckoutConstants.GENERIC_ERROR_TRY_LATER, null, null), BBBCheckoutConstants.GENERIC_ERROR_TRY_LATER));
		}

		this.logInfo("END: Processing pipeline errors");
	}

	/** @return Checkout State */
	public final CheckoutProgressStates getCheckoutState() {
		return this.checkoutState;
	}

	/**
	 * @param checkoutState
	 *            Checkout State
	 */
	public final void setCheckoutState(final CheckoutProgressStates checkoutState) {
		this.checkoutState = checkoutState;
	}

	@Override
	public final boolean isLoggingDebug() {
		return this.getCommonConfiguration().isLoggingDebugForRequestScopedComponents();
	}

	/**
	 * @return the commonConfiguration
	 */
	public final CommonConfiguration getCommonConfiguration() {
		return this.commonConfiguration;
	}

	/**
	 * @param commonConfiguration
	 *            the commonConfiguration to set
	 */
	public final void setCommonConfiguration(final CommonConfiguration commonConfiguration) {
		this.commonConfiguration = commonConfiguration;
	}

	/**
	 * This method invalidates current active session
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 */
	private void invalidateSession(final DynamoHttpServletRequest pRequest) {
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

	/**
	 * @return the dcPrefix
	 */
	public final String getDcPrefix() {
		return this.dcPrefix;
	}

	/**
	 * @param dcPrefix
	 *            the dcPrefix to set
	 */
	public final void setDcPrefix(final String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

	@Override
	public final boolean afterSet(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws DropletFormException {
			logDebug("BBBCommitOrderFormHandler-Afterset Start : Override the After set Method");
		boolean retVal = false;
		try {
			retVal = afterSetSuperCall(pRequest, pResponse);
		} catch (final Exception ex) {
			this.logError("Exception Occurred in afterSet:", ex);
			if (this.isLoggingInfo()) {
				this.logInfo("BBBCommitOrderFormHandler-Afterset : Roll Back Gift Card transation");
			}
			this.handleErrorScenarios(this.getOrder());
			throw new DropletFormException("Exception Occurred in afterSet", ex, "", BBBCoreErrorConstants.CHECKOUT_ERROR_1009);
		}
			logDebug("BBBCommitOrderFormHandler-Afterset End : Override the After set Method");
		return retVal;
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws DropletFormException
	 */
	protected boolean afterSetSuperCall(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws DropletFormException {
		return super.afterSet(pRequest, pResponse);
	}

	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BBBCommitOrderFormHandler [commonConfiguration=").append(this.commonConfiguration).append(", valueLinkGiftCardUtil=").append(this.valueLinkGiftCardUtil).append(", messageHandler=").append(this.messageHandler).append(", giftCardProcessor=").append(this.giftCardProcessor).append(", inventoryManager=").append(this.inventoryManager).append(", creditCardTools=").append(this.creditCardTools).append(", catalogTools=").append(this.catalogTools).append(", checkoutState=").append(this.checkoutState).append(", sessionBean=").append(this.sessionBean).append(", idGenerator=").append(this.idGenerator).append(", dcPrefix=").append(this.dcPrefix).append(", cardVerNumber=").append(this.cardVerNumber).append(", cartAndCheckOutConfigType=").append(this.cartAndCheckOutConfigType)
				.append(", onlineOrderPrefixKey=").append(this.onlineOrderPrefixKey).append(", bopusOrderPrefixKey=").append(this.bopusOrderPrefixKey).append("]");
		return builder.toString();
	}

	@Override
	public final void logInfo(final String pMessage) {
		if (this.getManageLogging().isCartFormHandlerLogging()) {
			this.logInfo(pMessage, null);
		}
	}

	@Override
	public final void logDebug(final String message) {
		if (this.isLoggingDebug()) {
			this.logDebug(message, null);
		}
	}

	public final String getPaRes() {
		return this.paRes;
	}

	public final void setPaRes(final String paRes) {
		this.paRes = paRes;
	}

	/**
	 * @return the vbvAuthErrorUrl
	 */
	public String getVbvAuthErrorUrl() {
		return this.vbvAuthErrorUrl;
	}

	/**
	 * @param vbvAuthErrorUrl
	 *            the vbvAuthErrorUrl to set
	 */
	public void setVbvAuthErrorUrl(String vbvAuthErrorUrl) {
		this.vbvAuthErrorUrl = vbvAuthErrorUrl;
	}

	/**
	 * 
	 * @return vbvLookupErrorUrl
	 */
	public String getVbvLookupErrorUrl() {
		return vbvLookupErrorUrl;
	}

	/**
	 * @param vbvLookupErrorUrl
	 *            the vbvLookupErrorUrl to set
	 */
	public void setVbvLookupErrorUrl(String vbvLookupErrorUrl) {
		this.vbvLookupErrorUrl = vbvLookupErrorUrl;
	}

	/**
	 * @return the vbvLookupSuccessUrl
	 */
	public String getVbvLookupSuccessUrl() {
		return this.vbvLookupSuccessUrl;
	}

	/**
	 * @param vbvLookupSuccessUrl
	 *            the vbvLookupSuccessUrl to set
	 */
	public void setVbvLookupSuccessUrl(String vbvLookupSuccessUrl) {
		this.vbvLookupSuccessUrl = vbvLookupSuccessUrl;
	}

	/**
	 * @return the vbvSessionBean
	 */
	public BBBVBVSessionBean getVbvSessionBean() {
		return this.vbvSessionBean;
	}

	/**
	 * @param vbvSessionBean
	 *            the vbvSessionBean to set
	 */
	public void setVbvSessionBean(BBBVBVSessionBean vbvSessionBean) {
		this.vbvSessionBean = vbvSessionBean;
	}

	/**
	 * @return the mobileRequest
	 */
	public boolean isMobileRequest() {
		return this.mobileRequest;
	}

	/**
	 * @param mobileRequest
	 *            the mobileRequest to set
	 */
	public void setMobileRequest(boolean mobileRequest) {
		this.mobileRequest = mobileRequest;
	}

	/**
	 * Validate the token from PayPal and Checks OrderType OrderType| If token
	 * expires Existing Payment Group will get removed
	 * 
	 * @return boolean
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private boolean validateOrderForPPCall() {
		BBBOrderImpl order = (BBBOrderImpl) getOrder();
		try {
			if (order.isPayPalOrder() && getPaypalServiceManager().isTokenExpired(this.getPayPalSessionBean(), order)) {
				logDebug("BBBCartFormHandler.validateOrderForPPCall() | validation failed");
				this.getPayPalSessionBean().setGetExpCheckoutResponse(null);
				this.getPayPalSessionBean().setValidateOrderAddress(false);
				return true;
			}
		} catch (BBBSystemException e) {
			this.logError("BBBSystemException : Error occured while checking token exipiration status", e);
		} catch (BBBBusinessException e) {
			this.logError("BBBBusinessException : Error occured while checking token exipiration status", e);
		}
		return false;
	}

	/**
	 * This method is called when user wants to pay with pay pal and clicks on
	 * paypal button
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return boolean
	 * @throws ServletException
	 * @throws IOException
	 * 
	 */
	private final boolean handleCheckoutWithPaypal(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCommitFormHandler ::handleCheckoutWithPaypal method -  start ");
		}
		final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
		final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		String hostUrl = pRequest.getScheme() + BBBCoreConstants.CONSTANT_SLASH + pRequest.getHeader(BBBCoreConstants.HOST) + pRequest.getContextPath();
		String payPalErrorURL = hostUrl + getCheckoutState().getCheckoutSuccessURLs().get(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
		String returnErrorURL = hostUrl + getCheckoutState().getCheckoutSuccessURLs().get(CheckoutProgressStates.DEFAULT_STATES.VALIDATION.toString());
		if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.PAYPAL_CHECKOUT, myHandleMethod);
			Transaction tr = null;
			try {
				tr = this.ensureTransaction();
				if (this.getUserLocale() == null) {
					this.setUserLocale(this.getUserLocale(pRequest, pResponse));
				}

				synchronized (this.getOrder()) {
					BBBOrder order = (BBBOrder) getOrder();
					BBBSetExpressCheckoutResVO resType = null;
					String sucessURL = BBBCoreConstants.BLANK;
					this.logInfo("CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired");
					String token = null;
					try {

						this.logInfo("CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address");
						resType = getPaypalServiceManager().doSetExpressCheckOut(order, payPalErrorURL, returnErrorURL, this.getPayPalSessionBean(), this.getUserProfile());

						if (resType != null && !StringUtils.isEmpty(resType.getToken())) {
							token = resType.getToken();
							sucessURL = (getPaypalServiceManager().getPayPalRedirectURL()) + token;
							setPayPalSucessURL(sucessURL);
						} else if (resType != null && resType.getErrorStatus().isErrorExists()) {
							logError(" Error Msg Recieved while calling paypal service= " + resType.getErrorStatus().getErrorMessage());
							if (resType.getErrorStatus().getErrorId() == BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID) {
								addFormException(new DropletException(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR));
								// Added as part of R2.2.1 Story - Redirects to
								// shipping page when paypal rejects the
								// shipping address - Start
								setTransactionToRollbackOnly();
								this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString());
								if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL()) && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
									this.payPalErrorURL = pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(BBBPayPalConstants.SHIPPING_SINGLE) + BBBPayPalConstants.PAYPAL_SHIPPING_ERROR;
								} else {
									this.errorMap.put(String.valueOf(resType.getErrorStatus().getErrorId()), this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null));
									logError("Error ID: " + resType.getErrorStatus().getErrorId() + " Error Message: " + this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null));
								}
								return checkFormRedirect(null, this.payPalErrorURL, pRequest, pResponse);
								// Added as part of R2.2.1 Story - Redirects to
								// shipping page when paypal rejects the
								// shipping address - End
							} else {
								addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null), BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
								this.errorMap.put(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
								logError("Error ID: " + resType.getErrorStatus().getErrorId() + " Error Message: " + this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
							}
						} else if (resType != null && resType.getError() != null) {
							logError(" Error Msg Recieved while calling paypal service= " + resType.getError().toString());
							addFormException(new DropletException(this.getMessageHandler().getErrMsg(ERR_CART_PAYPAL_SERVICE, "EN", null)));
						}
						this.logDebug("CommitOrderFormHandler.handleCheckoutWithPaypal() : payPalErrorURL: " + payPalErrorURL + " SucessURL: " + sucessURL);
					} catch (BBBSystemException systemException) {
						try {
							setTransactionToRollbackOnly();
						} catch (SystemException e) {
							this.logError("Error occured while rollback the transaction during CommitOrderFormHandler.handleCheckoutWithPaypal call", e);
						}
						this.logError("BBBSystemException occured in CommitOrderFormHandler.handleCheckoutWithPaypal :", systemException);
						addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null), BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
					} catch (BBBBusinessException businessException) {
						try {
							setTransactionToRollbackOnly();
						} catch (SystemException e) {
							this.logError("Error occured while rollback the transaction during CommitOrderFormHandler.handleCheckoutWithPaypal call", e);
						}
						this.logError("BBBBusinessException occured in CommitOrderFormHandler.handleCheckoutWithPaypal :", businessException);
						addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null), BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
					} catch (SystemException systemException) {
						this.logError("Error occured while rollback the transaction during CommitOrderFormHandler.handleCheckoutWithPaypal call", systemException);
						addFormException(new DropletException(this.getMessageHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null), BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
					}
				}
			} finally {
				// Complete the transaction
				if (tr != null) {
					this.commitTransaction(tr);
				}
				if (rrm != null) {
					rrm.removeRequestEntry(myHandleMethod);
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.PAYPAL_CHECKOUT, myHandleMethod);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBCommitFormHandler ::handleCheckoutWithPaypal method - mthod ends ");
		}
		return checkFormRedirect(getPayPalSucessURL(), payPalErrorURL, pRequest, pResponse);
	}
	
	/**This method checks for spc eligibility when user submit order after becoming recognized user
	 * (spc flag will not be found in session at that time)
	 * @param pRequest
	 * @return boolean
	 */
	public boolean isSpcForRecognizedUser(DynamoHttpServletRequest pRequest)
	{
		 boolean isSPc = false;
		 BBBSessionBean sessionBean = (BBBSessionBean) pRequest.getSession().getAttribute("sessionBean");
         if(sessionBean != null)
         {
        	 isSPc = sessionBean.isSinglePageCheckout();
         }
         return isSPc;
	}
	
	
	
	/**
	 * @return the payPalErrorPage
	 */
	public String getPayPalErrorPage() {
		return this.payPalErrorPage;
	}

	/**
	 * @param payPalErrorPage
	 *            the payPalErrorPage to set
	 */
	public void setPayPalErrorPage(String payPalErrorPage) {
		this.payPalErrorPage = payPalErrorPage;
	}

	/**
	 * @return the paypalTokenExpired
	 */
	public boolean isPaypalTokenExpired() {
		return paypalTokenExpired;
	}

	/**
	 * @param paypalTokenExpired
	 *            the paypalTokenExpired to set
	 */
	public void setPaypalTokenExpired(boolean paypalTokenExpired) {
		this.paypalTokenExpired = paypalTokenExpired;
	}

	/**
	 * @return the checkoutManager
	 */
	public BBBCheckoutManager getCheckoutManager() {
		return checkoutManager;
	}

	/**
	 * @param checkoutManager
	 *            the checkoutManager to set
	 */
	public void setCheckoutManager(BBBCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}

	/**
	 * @return the isFromVBV
	 */
	public boolean isFromVBV() {
		return isFromVBV;
	}

	/**
	 * @param isFromVBV
	 *            the isFromVBV to set
	 */
	public void setFromVBV(boolean isFromVBV) {
		this.isFromVBV = isFromVBV;
	}

	 
}
