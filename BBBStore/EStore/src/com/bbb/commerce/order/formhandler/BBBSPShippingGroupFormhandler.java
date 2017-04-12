package com.bbb.commerce.order.formhandler;

//Java Imports
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.ShippingGroupDroplet;
import atg.commerce.order.purchase.ShippingGroupFormHandler;
import atg.commerce.pricing.PricingException;
import atg.commerce.promotion.PromotionTools;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletConstants;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.multisite.SiteContextManager;
import atg.naming.NameContext;
import atg.nucleus.ServiceMap;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBAddressTools;
import com.bbb.account.BBBProfileManager;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.checkout.util.BBBCouponUtil;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.ManageCheckoutLogging;
import com.bbb.commerce.order.manager.PromotionLookupManager;
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager;
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean;
import com.bbb.commerce.order.purchase.BBBMultiShippingManager;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.order.purchase.BBBShippingGroupContainerService;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.commerce.order.shipping.BBBShippingInfoBean;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.commerce.porch.service.vo.PorchValidateZipCodeResponseVO;
import com.bbb.common.vo.CommerceItemShipInfoVO;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.vo.BeddingShipAddrVO;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.CommonConfiguration;


public class BBBSPShippingGroupFormhandler extends ShippingGroupFormHandler {

    public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/order/purchase/ShippingInfoFormHandler.java#2 $$Change: 633752 $";

    private static final String MSG_MISSING_REQUIRED_ADDRESS_PROPERTY = "MSG_MISSING_REQUIRED_ADDRESS_PROPERTY";
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String DATE_FORMAT_CANADA = "dd/MM/yyyy";
    private static final String IMPROPER_DATE_PACK_HOLD = "ERROR_SHIPPING_IMPROPER_DATE_PACK_HOLD";
    private static final String NOT_ALL_PACK_HOLD = "ERROR_SHIPPING_NOT_ALL_PACK_HOLD";
    private static final String SKU_NOT_PRESENT = "ERROR_NOT_PRESENT_CATALOG";
    private static final String INVALID_ENDDATE_PACK_HOLD = "ERROR_SHIPPING_INVALID_ENDDATE_PACK_HOLD";
    private static final String NICKNAME_SEPARATOR = ";;";
    private static final String REGISTRY_TEXT = "registry";
    private static final String SHIPPING_RESTRICTION_TRUE = "?shippingRestriction=true";
    private static final String MULTI_SHIPPING_RESTRICTION_TRUE = "&shippingRestriction=true";
    private static final String ERR_MULTI_GIFT_INVALID_SHIPPINGGROUPID = "err_multi_gift_invalid_shippinggroupid";
    private static final String GENERIC_ERROR_TRY_LATER = "GENERIC_ERROR_TRY_LATER";
    private static final String ERR_CART_QUANTITY_INCORRECT = "ERR_CART_QUANTITY_INCORRECT";
    private static final String ERR_CART_INVALID_SHIPPING = "ERR_CART_INVALID_SHIPPING";
    private static final String ERR_CART_INVALID_INPUT = "ERR_CART_INVALID_INPUT";
    private static final String ERR_CART_OUT_OF_STOCK = "ERR_CART_OUT_OF_STOCK";
    private static final String LOCALE_EN = BBBCoreConstants.DEFAULT_LOCALE;
    private static final String ERROR_INCORRECT_STATE = "ERROR_INCORRECT_STATE";
    private static final String ERROR_INCORRECT_POBOX = "ERROR_INCORRECT_POBOX";
    private static final String ERROR_SHIPPING_GIFT_CARD = "err_shipping_gift_card_item";
    private static final String ERROR_SHIP_ADD_NEW_ADDRESS = "err_shipping_add_new_address";
    private static final String ERROR_SAVING_TO_PROFILE = "err_shipping_save_to_profile";
    private static final String INVALID_SHIPPING_METHOD = "err_shipping_invalid_shipping_method";
    private static final String ERROR_INCORRECT_ADDRESS = "err_shipping_invalid_shipping_address";
    private static final String ERROR_INCORRECT_SHIP_METHOD = "err_shipping_no_shipping_method_selected";
    private static final String ERROR_SHIP_TO_MULTIPLE = "err_shipping_multiple_people";
    private static final String RESTRICTED_SHIPPING_METHOD = "err_shipping_shipping_method_restriction";
    private static final String ERROR_MULTIPLE_SHIPPING = "err_shipping_multiple_shipping";
    private static final String ERROR_SHIPPING_GROUP_ID_INVALID = "ERROR_SHIPPING_GROUP_ID_INVALID";
    private static final String ERROR_GIFT_MESSAGE_GENERIC_ERROR = "ERROR_GIFT_MESSAGE_GENERIC_ERROR";
    private static final String ERROR_SHIPPING_GENERIC_ERROR = "ERROR_SHIPPING_GENERIC_ERROR";
    private static final String IMPROPER_SHIPPING_METHOD_HOLD = "ERROR_SHIPPING_IMPROPER_SHIPPING_METHOD_HOLD";
    private static final String NO_SHIPPING_ADDRESS_SELECTED = "ERROR_SHIPPING_NO_SHIPPING_ADDRESS_SELECTED";
    private static final String BEDDING_KIT = "beddingKit";
    private static final String GIFTS_ARE_INCLUDED_IN_ORDER = "giftsAreIncludedInOrder";
    private static final String CHECKED = "checked";
    private static final String UNCHECKED = "unChecked";
    private static final String SHIP_MTHD_STD = "standard";
    private static final String TRUE = "true";
    private static final String ERROR_BCK_TO_STANDARD = "displayChangedToStd";
    protected static final String PORCH_RESTRICTION_TRUE = "?porchServiceRestriction=true"; 
    

    private LblTxtTemplateManager messageHandler;
    private CommonConfiguration commonConfiguration;
    private CommerceItemShippingInfo commerceItemShippingInfo;
    private ShippingGroupDroplet shippingGroupDroplet;
    private Properties shippingMethodMap;
    private Address address = new BBBAddressVO();
    private CheckoutProgressStates checkoutProgressStates;
    private ServiceMap shippingGroupInitializers;
    private ManageCheckoutLogging manageLogging;
    private PromotionTools promotionTools;
	private PromotionLookupManager promotionLookupManager;
	private BBBCouponUtil couponUtil;

    private BBBStoreInventoryContainer storeInventoryContainer;
    private BBBProfileTools profileTools;
    private BBBShippingInfoBean shippingInfoBean;
    private BBBAddressContainer shippingAddressContainer;
    private BBBAddressContainer addressContainer;
    private BBBAddressContainer multiShippingAddressContainer;
    private BBBCatalogTools catalogTools;
    private BBBCheckoutManager checkoutManager;
    private BBBPurchaseProcessHelper shippingHelper;
    private BBBShippingGroupManager shippingGroupManager;
    private BBBInventoryManager inventoryManager;
    private BBBShippingGroupContainerService shippingGroupContainerService;
	private BBBPayPalServiceManager paypalServiceManager;
	private Profile userProfile;
	private List commerceItemShippingInfos;
    private List states;
    private List<CommerceItemShipInfoVO> commerceItemShipInfoVOs;
    private List<BBBShippingInfoBean> shippingInfoBeans;
    private Map<String, CommerceItemShipInfoVO> shippingGroupCommerceItemShipInfoVOs;
    private Map<String, Address> shippingAddressCombinations;
    private Map<String, String> checkoutFailureURLsForREST = new HashMap<String, String>();
    private Map<String, String> shippingGroupCombinations;
    private Map<String, RepositoryItem> promotions;
    private PorchServiceManager porchServiceManager;
    
    private BBBSessionBean sessionBean;
	private String commerceItemId;
    private String newQuantity;
    private String orderId;
    private String storeId;
    private String oldShippingId;
    private String successURL;
    private String errorURL;
    private String systemErrorURL;
    private String siteId;
    private String giftMessageInput;
    private String giftMsgAPIDelimiter;
    private String giftMsgAPIPropertiesDelimiter;
    private String multiShipOutOfStock;
    private String addNewAddressURL;
    private String shippingOption;
    private String sendShippingEmail;
    private String packAndHoldDate;
    private String commerceItemShippingInfoIndex;
    private String storeShippingGroupMethodName;
    private String shipToMultiplePeopleErrorURL;
    private String shipToMultiplePeopleSuccessURL;
    private String commerceItemShippingInfoClass;
    private String shipToAddressName;
    private String newShipToAddressName;
    private String defaultShippingMethod = "Ground";
    private String shipToCollegeName;
    private String redirectState;

    private boolean collegeAddress;
    private String beddingKitOrder;
    private boolean saveShippingAddress;
    private boolean singleShippingGroupCheckout = true;
    private boolean changeStoreAfterSplitFlag;
    private boolean sendShippingConfEmail;
    private boolean packAndHold;
    private boolean clearContainer = true;
    private boolean orderIncludesGifts;
    private boolean newAddress;
    private boolean fromPreview;
    private String fromPaypalEdit;
    private BBBPayPalSessionBean payPalSessionBean;
    private boolean shippingGroupChanged;
    private String shippingGroupErrorMsg;
    private String paypalAddressStatus;
    private boolean ltlCommerceItem;
    private String addNewAddressCommItemId;
    private BBBMultiShippingManager multiShippingManager;
   // private String confirmedEmail;
    private boolean emailSignUp;
    private String emailChecked;
    private boolean useAsBilling;
    private boolean updateAddress =false;
    private BBBProfileManager profileManager;
    private String nickname=null;
    private String currentAddressID=null;
    private String poBoxFlag;
    private String poBoxStatus;
    private boolean removePorchService;

	public String getFromPaypalEdit() {
		return fromPaypalEdit;
	}

	public void setFromPaypalEdit(String fromPaypalEdit) {
		this.fromPaypalEdit = fromPaypalEdit;
	}

	private boolean shippingEmailChanged;
    private boolean shippingPhoneChanged;


    public String getPoBoxFlag() {
		return poBoxFlag;
	}

	public void setPoBoxFlag(String poBoxFlag) {
		this.poBoxFlag = poBoxFlag;
	}

	public String getPoBoxStatus() {
		return poBoxStatus;
	}

	public void setPoBoxStatus(String poBoxStatus) {
		this.poBoxStatus = poBoxStatus;
	}

    public PromotionTools getPromotionTools() {
		return promotionTools;
	}

	public void setPromotionTools(PromotionTools promotionTools) {
		this.promotionTools = promotionTools;
	}

	public PromotionLookupManager getPromotionLookupManager() {
		return promotionLookupManager;
	}

	public void setPromotionLookupManager(
			PromotionLookupManager promotionLookupManager) {
		this.promotionLookupManager = promotionLookupManager;
	}

    public String getCurrentAddressID() {
		return currentAddressID;
	}

	public void setCurrentAddressID(String currentAddressID) {
		this.currentAddressID = currentAddressID;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public BBBProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(BBBProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public boolean isUpdateAddress() {
		return updateAddress;
	}

	public void setUpdateAddress(boolean updateAddress) {
		this.updateAddress = updateAddress;
	}

	public boolean isUseAsBilling() {
		return useAsBilling;
	}

	public void setUseAsBilling(boolean useAsBilling) {
		this.useAsBilling = useAsBilling;
	}

	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

    /** @return New Address */
	public final String getEmailChecked() {
			return this.getSessionBean().getEmailChecked();
	}

	/** @param newAddress New Address */
	public final void setEmailChecked(final String emailChecked) {
		this.emailChecked = emailChecked;
	}

    /** @return New Address */
    public boolean isEmailSignUp() {
		return emailSignUp;
	}

    /** @param newAddress New Address */
	public final void setEmailSignUp(final boolean emailSignUp) {
		this.emailSignUp = emailSignUp;
	}


//    /** @return Confirmed Email */
//    public final String getConfirmedEmail() {
//        return this.confirmedEmail;
//    }
//
//    /** @param confirmedEmail Confirmed Email */
//    public final void setConfirmedEmail(final String confirmedEmail) {
//        this.confirmedEmail = confirmedEmail;
//    }
	/**
	 * @return the multiShippingManager
	 */
	public BBBMultiShippingManager getMultiShippingManager() {
		return multiShippingManager;
	}

	/**
	 * @param multiShippingManager the multiShippingManager to set
	 */
	public void setMultiShippingManager(BBBMultiShippingManager multiShippingManager) {
		this.multiShippingManager = multiShippingManager;
	}

    public Profile getUserProfile() {
		return this.userProfile;
	}

	public void setUserProfile(Profile userProfile) {
		this.userProfile = userProfile;
	}

	public BBBPayPalSessionBean getPayPalSessionBean() {
		return this.payPalSessionBean;
	}

	public void setPayPalSessionBean(BBBPayPalSessionBean payPalSessionBean) {
		this.payPalSessionBean = payPalSessionBean;
	}

	public boolean isFromPreview() {
		return this.fromPreview;
	}

	public void setFromPreview(boolean fromPreview) {
		this.fromPreview = fromPreview;
	}

	/** @param shippingInfoBeanList */
    public final void setBBBShippingInfoBeanList(final List<BBBShippingInfoBean> shippingInfoBeanList) {
        this.shippingInfoBeans = shippingInfoBeanList;
    }

    /** @return the addNewAddressURL */
    public final String getAddNewAddressURL() {
        return this.addNewAddressURL;
    }

    /** @param addNewAddressURL the addNewAddressURL to set */
    public final void setAddNewAddressURL(final String addNewAddressURL) {
        this.addNewAddressURL = addNewAddressURL;
    }

    /** @return */
    public final boolean isChangeStoreAfterSplitFlag() {
        return this.changeStoreAfterSplitFlag;
    }

    /** @param changeStoreAfterSplitFlag */
    public final void setChangeStoreAfterSplitFlag(final boolean changeStoreAfterSplitFlag) {
        this.changeStoreAfterSplitFlag = changeStoreAfterSplitFlag;
    }

    /** @return */
    public final String getMultiShipOutOfStock() {
        return this.multiShipOutOfStock;
    }

    /** @param multiShipOutOfStock */
    public final void setMultiShipOutOfStock(final String multiShipOutOfStock) {
        this.multiShipOutOfStock = multiShipOutOfStock;
    }

    /** @return the tools */
    public final BBBProfileTools getTools() {
        return this.profileTools;
    }

    /** @param pTools the tools to set */
    public final void setTools(final BBBProfileTools pTools) {
        this.profileTools = pTools;
    }

    /** @return */
    public final List<BBBShippingInfoBean> getBBBShippingInfoBeanList() {
        if ((this.shippingInfoBeans == null) && (this.getOrder().getShippingGroupCount() > 0)) {
            this.shippingInfoBeans = new ArrayList<BBBShippingInfoBean>();
            for (int index = 0; index < this.getOrder().getShippingGroupCount(); index++) {
                this.shippingInfoBeans.add(new BBBShippingInfoBean());
            }
        }
        return this.shippingInfoBeans;
    }

    /** @return */
    public final String getShipToCollegeName() {
        return this.shipToCollegeName;
    }

    /** @param shipToCollegeName */
    public final void setShipToCollegeName(final String shipToCollegeName) {
        this.shipToCollegeName = shipToCollegeName;
    }

    /** @return */
    public final BBBStoreInventoryContainer getStoreInventoryContainer() {
        return this.storeInventoryContainer;
    }

    /** @param mStoreInventoryContainer */
    public final void setStoreInventoryContainer(final BBBStoreInventoryContainer mStoreInventoryContainer) {
        this.storeInventoryContainer = mStoreInventoryContainer;
    }

    /**
     * @return the manageLogging
     */
    public final ManageCheckoutLogging getManageLogging() {
        return this.manageLogging;
    }

    /**
     * @param manageLogging the manageLogging to set
     */
    public final void setManageLogging(final ManageCheckoutLogging manageLogging) {
        this.manageLogging = manageLogging;
    }

    public boolean isLTLCommerceItem() {
		return this.ltlCommerceItem;
	}

	public void setLTLCommerceItem(boolean ltlCommerceItem) {
		this.ltlCommerceItem = ltlCommerceItem;
	}

    /**
	 * @return the addNewAddressCommItemId
	 */
	public String getAddNewAddressCommItemId() {
		return addNewAddressCommItemId;
	}

	/**
	 * @param addNewAddressCommItemId the addNewAddressCommItemId to set
	 */
	public void setAddNewAddressCommItemId(String addNewAddressCommItemId) {
		this.addNewAddressCommItemId = addNewAddressCommItemId;
	}

	@Override
    public final boolean beforeSet(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                    throws DropletFormException {
        if (this.shippingInfoBean == null) {
            this.shippingInfoBean = new BBBShippingInfoBean();
        }
        return super.beforeSet(pRequest, pResponse);
    }

    /** Add System Exception
     *
     * @param msg System Exception message */
    public final void addSystemException(final String pMsg, final Exception ex) {
        if (this.isLoggingError()) {
            this.logError("Adding System Error." + pMsg, ex);
        }

        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(pMsg, LOCALE_EN, null),
                        BBBCoreErrorConstants.CHECKOUT_ERROR_1051));
        if (!this.isTransactionMarkedAsRollBack()) {
            try {
                this.setTransactionToRollbackOnly();
            } catch (final SystemException e) {

                if (this.isLoggingError()) {
                    this.logError("Error is marking the transaction rollback", e);
                }

            }
        }
    }

    /** This method either adds or removes gift messaging to existing shipping group.
     *
     * @param pRequest
     * @param pResponse
     * @return Boolean
     * @throws ServletException
     * @throws IOException */
    public final boolean handleGiftMessaging(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        String redirectURL = "";
        this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_GIFT.toString());
        if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                        && !this.getCheckoutProgressStates().getFailureURL()
                                        .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
        }
        if (this.isLoggingDebug()) {
            this.logDebug("Entry BBBShippingGroupFormHandler.handleGiftMessaging");
        }

        final String myHandleMethod = "BBBShippingGroupFormHandler.handleGiftMessaging";
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();

        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_MULTIPLE_GIFT_OPTION, myHandleMethod);
        }
        try {

            if (!this.checkFormRedirect(null, redirectURL, pRequest, pResponse)) {
                return false;
            }

            synchronized (this.getOrder()) {

                // add gift options to order
                this.multiGiftMessaging();
                try {
                    // this update order before re-price order is to make the effect of multi gifting.
                    this.getOrderManager().updateOrder(this.getOrder());
                    // reprice order
                    this.repriceOrder(pRequest, pResponse);

                    this.getOrderManager().updateOrder(this.getOrder());
                } catch (final CommerceException e) {
                    if (this.isLoggingError()) {
                        this.logError(LogMessageFormatter.formatMessage(pRequest,
                                        "Update order failed for gift messaging"), e);
                    }
                    this.addFormException(new DropletException(ERROR_GIFT_MESSAGE_GENERIC_ERROR,
                                    ERROR_GIFT_MESSAGE_GENERIC_ERROR));
                }
            } // synchronized


            if (this.isLoggingDebug()) {
                this.logDebug("Exist BBBShippingGroupFormHandler.handleGiftMessaging");
            }

            // If NO form errors are found, redirect to the success URL.
            // If form errors are found, redirect to the error URL.
            // Added as Part of Story 83-AZ: Start
            BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
            if(order.isPayPalOrder() && !this.getFormError()){
            	this.logDebug("Order is of type Paypal");
            	//getPayPalSessionBean().setPayPalShipAddValidated(true);
            	this.getCheckoutProgressStates().setCurrentLevel(
                        CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString());
            	if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                        && !this.getCheckoutProgressStates().getFailureURL()
                                        .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
                	if(getPayPalSessionBean().isFromPayPalPreview()){
                		this.logDebug("Coming from preview page , so redirect to review page");
                		redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
                	}
                	else{
                		this.logDebug("Coming from validation page , so redirect to validation page for further validations");
                		redirectURL = pRequest.getContextPath() + BBBPayPalConstants.PAYPAL_REDIRECT_PATH;
                	}
            	}
            }
            // Added as Part of Story 83-AZ: End
            else{
	            this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString());
	            if (!this.getFormError()) {
	                if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
	                                && !this.getCheckoutProgressStates().getFailureURL()
	                                                .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
	                    redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
	                }
	            }
            }
            return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_MULTIPLE_GIFT_OPTION, myHandleMethod);
            if (rrm != null) {
                rrm.removeRequestEntry(myHandleMethod);
            }
        }

    }

    /** This method either adds or removes gift messaging to existing shipping groups. */
    private void multiGiftMessaging() {

        if (this.isLoggingDebug()) {
            this.logDebug("Entry BBBShippingGroupFormHandler.multiGiftMessaging");
        }

        try {

            final List<BBBShippingInfoBean> shippingBeanList = this.getBBBShippingInfoBeanList();

            // iterate over shipping beans
            for (final BBBShippingInfoBean bbbShippingInfoBean : shippingBeanList) {
                if ((bbbShippingInfoBean != null) && !StringUtils.isBlank(bbbShippingInfoBean.getShippingGroupId())) {
                    final ShippingGroup shippingGroup = this.getOrder().getShippingGroup(
                                    bbbShippingInfoBean.getShippingGroupId());
                    if (shippingGroup instanceof HardgoodShippingGroup) {

                        // add or remove gift wrap
                        ((BBBPurchaseProcessHelper) this.getPurchaseProcessHelper())
                                        .manageAddOrRemoveGiftWrapToShippingGroup(this.getOrder(), shippingGroup,
                                                        this.getSiteId(), bbbShippingInfoBean);
                    }
                }
            }

        } catch (final ShippingGroupNotFoundException e) {

            if (this.isLoggingError()) {
                this.logError("Exception occured while fetching shipping group " + "for multi Ship flow - ", e);
            }
            this.addFormException(new DropletException(ERROR_SHIPPING_GROUP_ID_INVALID, ERROR_SHIPPING_GROUP_ID_INVALID));

        } catch (final InvalidParameterException e) {

            if (this.isLoggingError()) {
                this.logError("Exception occured while fetching shipping group " + "for multi Ship flow - ", e);
            }

        } catch (final CommerceException e) {

            if (this.isLoggingError()) {
                this.logError("Exception occured while fetching shipping group " + "for multi Ship flow - ", e);
            }

        } catch (final BBBSystemException e) {

            if (this.isLoggingError()) {
                this.logError("Exception occured while adding/removing gift item" + "for multi Ship flow - ", e);
            }

        } catch (final BBBBusinessException e) {

            if (this.isLoggingDebug()) {
                this.logError("Exception occured while adding/removing gift item" + "for multi Ship flow - ", e);
            }
            this.addFormException(new DropletException("Invalid Gift Message", ERROR_GIFT_MESSAGE_GENERIC_ERROR));

        }

        if (this.isLoggingDebug()) {
            this.logDebug("Exist BBBShippingGroupFormHandler.multiGiftMessaging");
        }
    }

    /** This method either adds or removes gift messaging to existing shipping group. */
    private void giftMessaging() {
        if (this.isLoggingDebug()) {
            this.logDebug("Entry BBBShippingGroupFormHandler.giftMessaging");
        }

        ShippingGroup shippingGroup;
        try {
            shippingGroup = this.getOrder().getShippingGroup(this.getBBBShippingInfoBean().getShippingGroupId());

            if (shippingGroup instanceof HardgoodShippingGroup) {

                ((BBBPurchaseProcessHelper) this.getPurchaseProcessHelper()).manageAddOrRemoveGiftWrapToShippingGroup(
                                this.getOrder(), shippingGroup, this.getSiteId(), this.getBBBShippingInfoBean());
            }
        } catch (final BBBSystemException e) {
            this.logError("Error occurred processing gift messagin", e);
        } catch (final BBBBusinessException e) {
            this.logError("Error occurred processing gift messagin", e);
            this.addFormException(new DropletException("Invalid Gift Message", ERROR_GIFT_MESSAGE_GENERIC_ERROR));
        } catch (final ShippingGroupNotFoundException e) {
            this.logError("Error occurred processing gift messagin", e);
            this.addFormException(new DropletException(ERROR_SHIPPING_GROUP_ID_INVALID, ERROR_SHIPPING_GROUP_ID_INVALID));
        } catch (final InvalidParameterException e) {
            this.logError("Error occurred processing gift messagin", e);
        } catch (final CommerceException e) {
            this.logError("Error occurred processing gift messagin", e);
        }

        if (this.isLoggingDebug()) {
            this.logDebug("Exist BBBShippingGroupFormHandler.giftMessaging");
        }
    }

    /** This method returns the ShippingMethodDescription for the corresponding shippingMethodId.
     *
     * @param shippingMethodId shipping method id
     * @return ShippingMethodDescription */
    private String getShippingMethodDescription(final String shippingMethodId) {

        if (this.isLoggingDebug()) {
            this.logDebug("Enter BBBShippingGroupFormHandler.getShippingMethodDescription for shippingMethodId - ["
                            + shippingMethodId + "]");
        }

        String shippingMethodDescription = null;
        if (org.apache.commons.lang.StringUtils.isNotBlank(shippingMethodId)) {
            RepositoryItem shippingMethod;
            try {
                shippingMethod = this.getCatalogUtil().getShippingMethod(shippingMethodId);
                shippingMethodDescription = (String) shippingMethod.getPropertyValue("shipMethodDescription");
            } catch (final BBBBusinessException e) {
                this.logError("Business Error while retrieving shipping method for [" + shippingMethodId + "]", e);
            } catch (final BBBSystemException e) {
                this.logError("System Error while retrieving shipping method for [" + shippingMethodId + "]", e);
            }
        }

        if (this.isLoggingDebug()) {
            this.logDebug("Exit BBBShippingGroupFormHandler.getShippingMethodDescription , retrived value of shippingMethodDescription - "
                            + shippingMethodDescription);
        }

        return shippingMethodDescription;

    }



    /** This method place holder for the postAddNewAddress
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public void postAddNewAddress(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                    throws ServletException, IOException {
        // FIXME
    }

    /** Applies the data in the CommerceItemShippingInfoContainer and ShippingGroupMapContainer to the order.
     *
     * @see ShippingGroupFormHandler#applyShippingGroups(DynamoHttpServletRequest, DynamoHttpServletResponse)
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */

    public void addShipping(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                    throws ServletException, IOException {
        final BBBAddress addressFromContainer = this.getAddressContainer().getAddressFromContainer(
                        this.getShipToAddressName());
        // addressFromContainer.setSource("shipping");
        this.getBBBShippingInfoBean().setAddress(addressFromContainer);
        final SimpleDateFormat formatter;
        if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)){
       	 formatter = new SimpleDateFormat(DATE_FORMAT_CANADA, pRequest.getLocale());
       }else{
       	 formatter = new SimpleDateFormat(DATE_FORMAT, pRequest.getLocale());
       }
        

        try {
            if (this.getPackNHold() && !StringUtils.isEmpty(this.getPackNHoldDate())) {
                this.getBBBShippingInfoBean().setPackAndHoldDate(formatter.parse(this.getPackNHoldDate()));
            }
        } catch (final ParseException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, "PACK_DATE_NOT_PROPER"), e);
            }
        }
//        if ((StringUtils.isEmpty(this.getConfirmedEmail()) || !this.getConfirmedEmail().equals(
//        		addressFromContainer.getEmail()))) {
//        	this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIPPING_GENERIC_ERROR,
//                    LOCALE_EN, null), "Email and Confirmed Email are not same."));
//		}

        if (this.getSendShippingConfEmail()
                        && org.apache.commons.lang.StringUtils.isNotBlank(this.getShipToAddressName())
                        && this.getShipToAddressName().contains(REGISTRY_TEXT)) {
            this.getBBBShippingInfoBean().setShippingConfirmationEmail(this.getSendShippingEmail());
            this.getBBBShippingInfoBean().setSendShippingConfirmation(true);
        }
        this.getBBBShippingInfoBean().setShippingMethod(this.getShippingOption());
        final BBBHardGoodShippingGroup firstNonGiftHardgoodShippingGroupWithRels =(BBBHardGoodShippingGroup) this.getShippingGroupManager()
                        .getFirstNonGiftHardgoodShippingGroupWithRels(this.getOrder());
        this.getBBBShippingInfoBean().setShippingGroupId(firstNonGiftHardgoodShippingGroupWithRels.getId());
        final Map registryMap = ((BBBOrderImpl) this.getOrder()).getRegistryMap();
        this.setRegistryInfo(addressFromContainer, registryMap);

        try {
            ((BBBShippingGroupManager) this.getShippingGroupManager()).shipToAddress(this.getBBBShippingInfoBean(),
                            (BBBHardGoodShippingGroup) firstNonGiftHardgoodShippingGroupWithRels);
            // LTL RM-27711 | Remove empty SG | In case LTL item is removed on cart, LTL SG persists in Order with no
            // relationship, hence need to remove all empty SG.
            this.getManager().removeEmptyShippingGroups(this.getOrder());

        } catch (final RepositoryException e) {
            if (this.isLoggingError()) {
                this.logError(" some error occured while updating shipping address", e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIPPING_GENERIC_ERROR,
                            LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
        } catch (final IntrospectionException e) {
            if (this.isLoggingError()) {
                this.logError(" some error occured while updating shipping address", e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIPPING_GENERIC_ERROR,
                            LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
        } catch (CommerceException e) {
            if (this.isLoggingError()) {
            	this.logError(" some error occured while updating shipping address", e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIPPING_GENERIC_ERROR,
                    LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
        }
        if (this.getFormError()) {
            return;
        }

    }

    private void setRegistryInfo(final BBBAddress addressFromContainer, final Map registryMap) {
        if ((this.getCheckoutManager().getItemsRegistryCount(this.getOrder().getCommerceItems()) > 0)
                        && registryMap.containsKey(addressFromContainer.getId())) {// true for registry address
            this.getBBBShippingInfoBean().setRegistryId(addressFromContainer.getId());
            final RegistrySummaryVO registry = (RegistrySummaryVO) registryMap.get(addressFromContainer.getId());

            final StringBuffer registryInfo = new StringBuffer("<strong>");
            registryInfo.append(registry.getPrimaryRegistrantFirstName()).append(" ")
                            .append(registry.getPrimaryRegistrantLastName());
            if (!StringUtils.isEmpty(registry.getCoRegistrantFirstName())) {
                registryInfo.append(" & ").append(registry.getCoRegistrantFirstName()).append(" ")
                                .append(registry.getCoRegistrantLastName());
            }
            registryInfo.append("</strong> (Registry #").append(addressFromContainer.getId()).append(")");
            this.getBBBShippingInfoBean().setRegistryInfo(registryInfo.toString());
        } else {
            this.getBBBShippingInfoBean().setRegistryId(null);
            this.getBBBShippingInfoBean().setRegistryInfo(null);
        }
    }

    /** Applies the address to new HardGoodShippingGroup in order.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public final void addNewAddress(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                    throws ServletException, IOException {
        String name = null;
        try {

            if (this.getSaveShippingAddress()) {
                final BBBAddressImpl newAddress = this.getCheckoutManager().saveAddressToProfile(
                                (Profile) this.getProfile(), this.getAddress(), this.getOrder().getSiteId());
                name = newAddress.getIdentifier();
            } else {
                name = Long.toString(new Date().getTime());
            }

            BBBAddressImpl bbbAddress = new BBBAddressImpl();
            bbbAddress = (BBBAddressImpl) BBBAddressTools.copyBBBAddress((BBBAddress) this.getAddress(),
                            (BBBAddress) bbbAddress);
            this.getShippingAddressContainer().addNewAddressToContainer(name, bbbAddress);
            this.getShippingAddressContainer().setNewAddressKey(name);
            final int index = Integer.parseInt(this.getCisiIndex());
            if (index >= 0) {
                final List commerceItemShippingInfoList = this.getCisiItems();
                final CommerceItemShippingInfo currentCISI = (CommerceItemShippingInfo) commerceItemShippingInfoList
                                .get(index);
                if (this.isLoggingDebug()) {
                    this.logDebug("CISI ShippingGroupName before adding new Address:::::::: "
                                    + currentCISI.getShippingGroupName());
                    this.logDebug("New ShippingGroupName in CISI :::: " + name);
                }
                currentCISI.setShippingGroupName(name);
            }

            pRequest.setParameter("newAddressKey", name);

        } catch (final BBBSystemException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SAVING_TO_PROFILE), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SAVING_TO_PROFILE,
                            LOCALE_EN, null), ERROR_SAVING_TO_PROFILE));
        } catch (final BBBBusinessException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SAVING_TO_PROFILE), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SAVING_TO_PROFILE,
                            LOCALE_EN, null), ERROR_SAVING_TO_PROFILE));
        }

    }

    /** This handler method will validate shipping address and apply the shipping groups to the order.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @return a <code>boolean</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public final boolean handleAddShipping(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        final String contextPath = pRequest.getContextPath();

        this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_SHIPPING_SINGLE.toString());
        String failureURL = null;
        if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                        && !this.getCheckoutProgressStates().getFailureURL()
                                        .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            failureURL = contextPath + this.getCheckoutProgressStates().getFailureURL();
        }

        String successURL = failureURL;

        Transaction tr = null;
        final String myHandleMethod = "BBBSPShippingGroupFormHandler.handleAddShipping";
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();

        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_SINGLE_SHIPPING, myHandleMethod);
            try {
                tr = this.ensureTransaction();

                if (!this.checkFormRedirect(null, failureURL, pRequest, pResponse)) {
                    return false;
                }

                synchronized (this.getOrder()) {
                    try {
                        this.preAddShipping(pRequest, pResponse);

                        if (this.getFormError()) {
                            if (this.isLoggingDebug()) {
                                this.logDebug("Redirecting due to form error in preMoveToBilling.");
                            }
                            this.setNewShipToAddressName(null);
                            return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
                        }

                        this.addShipping(pRequest, pResponse);
                        
                        if(isRemovePorchService()){
                        	List<CommerceItem> commerceItemList=(List<CommerceItem>) getOrder().getCommerceItems();
                        	for(CommerceItem commerceItem:commerceItemList){
                    			 BaseCommerceItemImpl cItemimpl = (BaseCommerceItemImpl) commerceItem;
                    			 if(cItemimpl.isPorchService()){
                    				 ShippingGroup shgroup = ((BBBShippingGroupCommerceItemRelationship)commerceItem.getShippingGroupRelationships().get(0)).getShippingGroup();                        			 
            			    		 if(!(shgroup instanceof BBBStoreShippingGroup)){
            			    			 setRemovePorchService(false);
                            			 removePorchServiceFromCommerceItem(cItemimpl);
            			    		 }
                    				
                    			 }
                    		 }
                        	
                        } else{

                            boolean porchValidation= validatePorchServiceSingleShipping();
                            if(porchValidation){                         	
     	                     if (!BBBUtility.isEmpty(failureURL)) {
     	                         failureURL = failureURL + PORCH_RESTRICTION_TRUE;
     	                     }
     	                    this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                    BBBCoreConstants.PORCH_SERVICE_NOT_AVAILABLE, LOCALE_EN, null),
                                    BBBCoreConstants.PORCH_SERVICE_NOT_AVAILABLE));
     	                     return this.checkFormRedirect(failureURL, failureURL, pRequest, pResponse);
                            }
                            
                        }
                        
                        if (this.getFormError()) {
                            if (this.isLoggingDebug()) {
                                this.logDebug("Redirecting due to form error in moveToBilling");
                            }

                            return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
                        }

                        if (this.isOrderContainRestrictedSKU(this.getOrder(),null)) {
                            // Redirect request to error url
                            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                            BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU, LOCALE_EN, null),
                                            BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU));
                            if (!BBBUtility.isEmpty(failureURL)) {
                                failureURL = failureURL + SHIPPING_RESTRICTION_TRUE;
                            }
                            return this.checkFormRedirect(failureURL, failureURL, pRequest, pResponse);
                        }

                        this.runProcessValidateShippingGroups(this.getOrder(), this.getUserPricingModels(),
                                        this.getUserLocale(pRequest, pResponse), this.getProfile(), null);
                    } catch (final Exception exc) {
                        this.setNewShipToAddressName(null);
                        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                        ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
                        if (this.isLoggingError()) {
                            this.logError("Exception occured", exc);
                        }
                    }

                    if (this.getFormError()) {
                        if (this.isLoggingDebug()) {
                            this.logDebug("Redirecting due to form error in runProcessValidateShippingGroups");
                        }

                        return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
                    }

                    this.giftMessaging();

                    if (this.getFormError()) {
                        if (this.isLoggingDebug()) {
                            this.logDebug("Redirecting due to form error in runProcessValidateShippingGroups");
                        }
                        return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
                    }
                 // Added as Part of Story 83-N: Start
                    BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
                    
                  //calling coupons webservice and adding coupons to order
                	updateCouponDetails(order,pRequest, pResponse);

                    this.postAddShipping(pRequest, pResponse);

                  //Adding new code to set email opt in to order
                	if(isEmailSignUp()){
                		this.getSessionBean().setEmailChecked(CHECKED);
                	}
                	else{
                		this.getSessionBean().setEmailChecked(UNCHECKED);
                	}
                	order.setEmailSignUp(isEmailSignUp());
                	//End

                	// Adding new code to set PackAndHoldFlag to order
                	if(this.packAndHold && !(StringUtils.isEmpty(this.getPackNHoldDate()))){
                		order.setPackAndHoldFlag(this.packAndHold);
                	}
                	else{
                		order.setPackAndHoldFlag(false);
                	}
                    try {
                        this.getOrderManager().updateOrder(this.getOrder());
                        // If NO form errors are found, redirect to the success URL.
                        // If form errors are found, redirect to the error URL.

                        

                        if(order.isPayPalOrder()  && !this.getFormError()){
                        	this.logDebug("Order is of type Paypal");
                        	//getPayPalSessionBean().setPayPalShipAddValidated(true);
                        	this.getCheckoutProgressStates().setCurrentLevel(
                                    CheckoutProgressStates.DEFAULT_STATES.SP_SHIPPING_SINGLE.toString());
                        	if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                                    && !this.getCheckoutProgressStates().getFailureURL()
                                                    .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
	                        	if(getPayPalSessionBean().isFromPayPalPreview()){
	                        		this.logDebug("Coming from preview page , so redirect to review page");
	                        		successURL = contextPath + this.getCheckoutProgressStates().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.SP_SHIPPING_SINGLE.toString());
	                        	}
	                        	else{
	                        		this.logDebug("Coming from validation page , so redirect to validation page for further validations");
				                    successURL = contextPath + BBBPayPalConstants.PAYPAL_REDIRECT_PATH;
	                        	}
                        	}
                        }
                        // Added as Part of Story 83-N: End
                        else{
		                    this.getCheckoutProgressStates().setCurrentLevel(
		                                    CheckoutProgressStates.DEFAULT_STATES.SP_SHIPPING_SINGLE.toString());
		                    if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
		                                    && !this.getCheckoutProgressStates().getFailureURL()
		                                                    .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
		                        successURL = contextPath + this.getCheckoutProgressStates().getFailureURL();
		                    }

		                    if (isCollegeAddress()) {
		                        successURL = successURL + "?colg=true";
		                    }
                        }

                    } catch (final Exception exc) {
                        if (this.isLoggingError()) {
                            this.logError("Exception occured while updateorder during BBBShippingGroupFormhandler.handleAddShipping() ",
                                            exc);
                        }
                        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                        ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
                    }
                } // synchronized

                return this.checkFormRedirect(successURL, failureURL, pRequest, pResponse);
            } finally {
                if (tr != null) {
                    this.commitTransaction(tr);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_SINGLE_SHIPPING, myHandleMethod);
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
            }
        }
        return false;
    }

	private boolean validatePorchServiceSingleShipping() {
		String sourceId =null;  
		final BBBAddress addressFromContainer = this.getAddressContainer().getAddressFromContainer(
                this.getShipToAddressName());
        List<CommerceItem> commerceItemList=(List<CommerceItem>) getOrder().getCommerceItems();
        String commItemZipCode= addressFromContainer.getPostalCode();
        for(CommerceItem commerceItem:commerceItemList){
   		if(((BaseCommerceItemImpl)commerceItem).isPorchService()){	
   			  sourceId=addressFromContainer.getSource();   			  
   				if(!StringUtils.isBlank(sourceId) && sourceId.contains("registry")){
   		    		try {
   		    			BaseCommerceItemImpl cItemimpl = (BaseCommerceItemImpl) commerceItem;
   		    			removePorchServiceFromCommerceItem(cItemimpl);
   		    			this.addFormException(new DropletException(this.getMsgHandler().getErrMsg("err_onlyProductAddedToRegistry",
		    		   				 LOCALE_EN, null), "err_onlyProductAddedToRegistry"));
   					} catch (Exception e) {
   						if(isLoggingError()){ 
   						logDebug(" error while removing proch service ref from commerce item "+e,e);
   						}
   		    		}
   		        }
	    			else {  
		    		    String[] commItemShippingCode=commItemZipCode.split("-");
		    			RepositoryItem productItem = (RepositoryItem) commerceItem.getAuxiliaryData().getProductRef();
		    			List<String> porchServiceFamilycodes=getPorchServiceManager().getPorchServiceFamilyCodes(productItem.getRepositoryId(),null);
		    			Object responseVO = null;
		    			if(!StringUtils.isBlank(commItemShippingCode[0]) && !StringUtils.isBlank(porchServiceFamilycodes.get(0))){
							try {
								responseVO = getPorchServiceManager().invokeValidateZipCodeAPI(commItemShippingCode[0],porchServiceFamilycodes.get(0));
							} catch (BBBSystemException e) {
								if(isLoggingError()){
									logError("king validateZipCode porch api "+e,e);
								}
							} catch (BBBBusinessException e) {
								if(isLoggingError()){
									logError("Error while invoking validateZipCode porch api "+e,e);
								}
							} 
				    		if(null==responseVO){
				    			 return true;
				    	       
				    		}
		    			}
	    		}	   
   		}
       
        }
		return false;
	}

	private void removePorchServiceFromCommerceItem(BaseCommerceItemImpl cItemimpl) {		
		try {
			cItemimpl.setPorchServiceRef(null);
			cItemimpl.setPorchService(false);
		
		} catch (Exception e) {
			if(isLoggingError()){ 
			logDebug(" error while removing proch service ref from commerce item "+e,e);
			}
		
		}
	 
}

	/**
	 * This method will update coupons for guest user.
	 * 
	 * @param order
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
    void updateCouponDetails(BBBOrderImpl order, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException{
    	//final boolean schoolPromoFlag = this.isSchoolPromotion();
        String couponOn = null;
       // final BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
        try {
            final Map<String, String> configMap = this.getCatalogUtil().getConfigValueByconfigType(
                            "ContentCatalogKeys");
            final String orderSiteId = this.getOrder().getSiteId();

            if (orderSiteId.equalsIgnoreCase(configMap.get("BedBathUSSiteCode"))) {
                couponOn = this.getCatalogUtil().getContentCatalogConfigration("CouponTag_us").get(0);
            } else if (orderSiteId.equalsIgnoreCase(configMap.get("BuyBuyBabySiteCode"))) {
                couponOn = this.getCatalogUtil().getContentCatalogConfigration("CouponTag_baby").get(0);
            } else if (orderSiteId.equalsIgnoreCase(configMap.get("BedBathCanadaSiteCode"))) {
                couponOn = this.getCatalogUtil().getContentCatalogConfigration("CouponTag_ca").get(0);
            }

            if (couponOn == null) {
                couponOn = TRUE;
            }


			if (getProfile().isTransient() && (isShippingEmailChanged() || isShippingPhoneChanged())) {
				@SuppressWarnings("unchecked")
				final List<RepositoryItem> availablePromotions = (List<RepositoryItem>) this.getProfile()
						.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);
				if (couponOn.equalsIgnoreCase(TRUE)) {
					// remove already granted promotion only for guest user.
					for (final Object element : availablePromotions) {
						final RepositoryItem promotion = (RepositoryItem) element;
						this.getPromotionTools().removePromotion((MutableRepositoryItem) this.getProfile(), promotion,
								false);
						System.out.println("updateCouponDetails removed coupon.");
					}
					availablePromotions.clear();
					((MutableRepositoryItem) this.getProfile()).setPropertyValue(
							BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
					/*
					 * Re-initializing all current active promotion to
					 * pricingModelHolder object.
					 */
					this.getPromotionTools().initializePricingModels(pRequest, pResponse);
					this.promotions = this.getPromotionLookupManager().populateSPCValidPromotions(
							(Profile) this.getProfile(), order, this.getOrder().getSiteId());
					this.promotions = this.getCouponUtil().applySchoolPromotion(this.promotions,
							(Profile) this.getProfile(), order);
					((BBBOrderImpl) this.getOrder()).setCouponMap(this.promotions);
				}
			}
        } catch (final BBBException e) {
        	this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                    ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
            this.logError(e.getMessage());
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

    /** This method will check that order shipping group has any restricted sku or not.
     *
     * @param order
     * @return */
    private boolean isOrderContainRestrictedSKU(final Order order,final Address bbbAddressImpl) {
        boolean isRestricted = false;
        // For each shipping group in shiipingGroups
        for (final Object shippingGroupObj : order.getShippingGroups()) {
            final ShippingGroup sg = (ShippingGroup) shippingGroupObj;
            // if shippingGroup is of HardGoodShippingGroup
            if (sg instanceof BBBHardGoodShippingGroup) {
                // Retrieve commerce items associated in the shipping group
                for (final Object sgcirObj : sg.getCommerceItemRelationships()) {
                    final ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) sgcirObj;
                    // Retrieve skuId
                    final String skuId = sgcir.getCommerceItem().getCatalogRefId();
                    // Retrieve shipping group's address zip code
                     String zipCode = ((BBBHardGoodShippingGroup) sg).getShippingAddress().getPostalCode();
                     //R2.2 -83 J- Start Added To check Postal code Sent in restmultishipping call
                     if(((BBBOrderImpl)order).isPayPalOrder()){
                    	if(null!=bbbAddressImpl){
                    	zipCode=bbbAddressImpl.getPostalCode();
                    	}
                    //	R2.2 -83 J- END
                    }
                    // Call catalogAPI to check if shipping address's zip code is restricted for the sku
                    try {
                        isRestricted = this.getCatalogUtil().isShippingZipCodeRestrictedForSku(skuId,
                                        order.getSiteId(), zipCode);
                        if (isRestricted) {
                            return true;
                        }
                    } catch (final BBBBusinessException e) {
                        this.logError("Sku not present in the catalog" + skuId, e);
                        this.addFormException((new DropletException(this.getMsgHandler().getErrMsg(SKU_NOT_PRESENT,
                                        LOCALE_EN, null))));
                    } catch (final BBBSystemException e) {
                        this.logError("Error occurred processing sku" + skuId, e);
                    }
                }
            }
        }
        return false;
    }

    /** This method validates the user inputs for the Move To Billing process
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public final void
                    preAddShipping(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {

        if (this.isSingleShippingGroupCheckout()) {

            this.validatePackNHold(pRequest.getLocale(),pRequest);
            if (this.getFormError()) {
                return;
            }

            if ("true".equals(this.getShipToAddressName()) || "college".equals(this.getShipToAddressName() )
                            || "userAddress".equals(this.getShipToAddressName())) {
                if ("college".equals(this.getShipToAddressName())) {
                   setCollegeAddress(true);
                }
                if ("userAddress".equals(this.getShipToAddressName()) || "true".equals(this.getShipToAddressName())) {
                    setCollegeAddress(false);
                }

                this.setNewAddress(true);
                this.setShipToAddressName(this.getNewShipToAddressName());
                final BBBAddress nAddress = (BBBAddress) this.getAddress();
                nAddress.setId(this.getShipToAddressName());
                if(this.getPoBoxStatus()!=null && this.getPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
           	 		((BBBAddress)this.getAddress()).setQasValidated(true);
                if(this.getPoBoxFlag()!=null && this.getPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
                	((BBBAddress)this.getAddress()).setPoBoxAddress(true);
                this.getAddressContainer().addAddressToContainer(this.getShipToAddressName(), nAddress);

            } else {
                this.setNewAddress(false);
                if(this.getShipToAddressName() !=null && !this.getShipToAddressName().contains(REGISTRY_TEXT) && !isCollegeAddress()){
                	BBBAddress address = (BBBAddress) this.getAddressContainer().getAddressFromContainer(this.getShipToAddressName());
                	/*BBBH-2385: The below code check if the selected profile address is not SDD eligible 
                	 * and switched to Standard*/
					String currentPostalCode = BBBCoreConstants.BLANK;
					String currentShippingMethod = BBBCoreConstants.BLANK;
					if (this.getShipToAddressName().contains(
							BBBCoreConstants.PROFILE_CAPS)) {
						final BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
						if (null != order) {
							if (null != order.getShippingAddress()) {
								currentPostalCode = order.getShippingAddress()
										.getPostalCode();
							}
							if (null != order.getShippingGroups()
									&& (ShippingGroup) order
											.getShippingGroups().get(0) instanceof BBBHardGoodShippingGroup) {

								currentShippingMethod = ((ShippingGroup) order
										.getShippingGroups().get(0))
										.getShippingMethod();

							}
							if (null != address
									&& !BBBUtility.isEmpty(currentPostalCode)
									&& !BBBUtility
											.isEmpty(currentShippingMethod)
									&& (!BBBUtility.hyphenExcludedZip(currentPostalCode)
											.equalsIgnoreCase(BBBUtility.hyphenExcludedZip(address
													.getPostalCode())) && currentShippingMethod
											.equalsIgnoreCase(BBBCoreConstants.SDD))
									&& this.shippingOption
											.equalsIgnoreCase(BBBCoreConstants.SDD)) {
									pRequest.getSession().setAttribute(
											ERROR_BCK_TO_STANDARD, true);
									this.setShippingOption((String) this
											.getShippingMethodMap().get(
													SHIP_MTHD_STD));
									this.setShippingGroupChanged(true);
									
									if (this.isLoggingDebug()) {
										this.logDebug("BBBSPShippingGroupFormHandler | preAddShipping | Address Switched to standard and the display error message is set in session");
					                }
							
							}
						}
					}
                	if(this.isUpdateAddress()){
                		final BBBAddress nAddress = (BBBAddress) this.getAddress();
                        nAddress.setId(this.getShipToAddressName());
                        if(this.getPoBoxStatus()!=null && this.getPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
                        	nAddress.setQasValidated(true);
                        if(this.getPoBoxFlag()!=null && this.getPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
                        	nAddress.setPoBoxAddress(true);
                        this.getAddressContainer().addAddressToContainer(this.getShipToAddressName(), nAddress);
                	}else{
                		boolean isPoBoxAdd = false;
	                	boolean isQasValidated = false;
                		if(address != null){
                			isPoBoxAdd =  address.isPoBoxAddress();
    	                	isQasValidated = address.isQasValidated();                			
                		}
	                	final BBBAddress nAddress = (BBBAddress) this.getAddress();
	                	nAddress.setPoBoxAddress(isPoBoxAdd);
	                	nAddress.setQasValidated(isQasValidated);
	                	nAddress.setId(this.getShipToAddressName());
	                	this.getAddressContainer().addAddressToContainer(this.getShipToAddressName(), nAddress);
                	}
                }
            }

            this.preShipToAddress(pRequest, pResponse);

        } else {
            return;//
        }
    }

    private void validatePackNHold(final Locale pLocale, DynamoHttpServletRequest pRequest) {
    	String collegeIdValue = pRequest.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE);
    	BBBHardGoodShippingGroup hardGoodShippingGroup = (BBBHardGoodShippingGroup) this.getShippingGroupManager()
                .getFirstNonGiftHardgoodShippingGroupWithRels(this.getOrder());
        hardGoodShippingGroup.setShipOnDate(null);
        if(collegeIdValue == null){
        	//if collegeId value is null then read the collegeId from Order object
			//as the college id attribute will be set in BBBCommitOrderFormhandler
			collegeIdValue = ((BBBOrderImpl)this.getOrder()).getCollegeId();
		}
        if (this.getPackNHold()) {
            if (BBBUtility.isEmpty(collegeIdValue) && !this.getCheckoutManager().hasEvenSingleCollegeItem(this.getSiteId(), this.getOrder())) {
            	this.addFormException((new DropletException(this.getMsgHandler().getErrMsg(NOT_ALL_PACK_HOLD,
                                LOCALE_EN, null), NOT_ALL_PACK_HOLD)));
                return;
            }
            if (StringUtils.isEmpty(this.getPackNHoldDate())) {
                this.addFormException((new DropletException(this.getMsgHandler().getErrMsg(IMPROPER_DATE_PACK_HOLD,
                                LOCALE_EN, null), IMPROPER_DATE_PACK_HOLD)));
                return;
            } else {
                final SimpleDateFormat formatter;
                String siteId =  getCurrentSiteId();
                if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)){
                	 formatter = new SimpleDateFormat(DATE_FORMAT_CANADA, pLocale);
                }else{
                	 formatter = new SimpleDateFormat(DATE_FORMAT, pLocale);
                }
                try {
                    final Calendar packNHoldCal = Calendar.getInstance(pLocale);
                    packNHoldCal.setTime(formatter.parse(this.getPackNHoldDate()));
                    if (Calendar.getInstance(pLocale).after(packNHoldCal)) {
                        this.addFormException((new DropletException(this.getMsgHandler().getErrMsg(
                                        IMPROPER_DATE_PACK_HOLD, LOCALE_EN, null), IMPROPER_DATE_PACK_HOLD)));
                        return;
                    } else {
                        try {

                            if (!this.getCatalogUtil().isPackNHoldWindow(this.getSiteId(), packNHoldCal.getTime())) {
                                this.addFormException((new DropletException((this.getMsgHandler().getErrMsg(INVALID_ENDDATE_PACK_HOLD, LOCALE_EN, null)  + this.getCatalogUtil().packNHoldEndDate(siteId)), INVALID_ENDDATE_PACK_HOLD)));
                                return;
                            }
                            else{
                            	try {
                            		hardGoodShippingGroup.setShipOnDate(formatter.parse(this.getPackNHoldDate())); 
                                } catch (final ParseException e) {
                                    if (this.isLoggingError()) {
                                        this.logError(LogMessageFormatter.formatMessage(pRequest, "PACK_DATE_NOT_PROPER"), e);
                                    }
                                }
                            }
                        } catch (final Exception e) {
                            this.logError(LogMessageFormatter.formatMessage(null,
                                            "Error validating pack n hold date for the site " + this.getSiteId(),
                                            BBBCoreErrorConstants.CHECKOUT_ERROR_1014), e);

                        }

                    }
                } catch (final ParseException e) {
                    this.addFormException((new DropletException(this.getMsgHandler().getErrMsg(IMPROPER_DATE_PACK_HOLD,
                                    LOCALE_EN, null), IMPROPER_DATE_PACK_HOLD)));
                    return;
                }
            }
            try {
                final ShipMethodVO defaultShippingMethod = this.getShippingHelper().getCatalogTools()
                                .getDefaultShippingMethod(this.getOrder().getSiteId());
                if ((defaultShippingMethod != null) && (defaultShippingMethod.getShipMethodId() != null)
                                && defaultShippingMethod.getShipMethodId().equals(this.getShippingOption())) {
                    return;
                }
                this.addFormException((new DropletException(this.getMsgHandler().getErrMsg(
                                IMPROPER_SHIPPING_METHOD_HOLD, LOCALE_EN, null), IMPROPER_SHIPPING_METHOD_HOLD)));

                return;
            } catch (final Exception e) {
                if (this.isLoggingError()) {
                    this.logError(LogMessageFormatter.formatMessage(null, "error occurred for pack n hold validations"),
                                    e);
                }
                this.addFormException((new DropletException(this.getMsgHandler().getErrMsg(
                                ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR)));
                return;
            }
        }
    }

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

    /** This method will reprice the order to catch address problems through CyberSource.
     * <p>
     * Initializes the billing address from the shipping address if the user selected that option.
     * <p>
     * Saves addresses in the profile, if the user selected that option.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
		public void postAddShipping(final DynamoHttpServletRequest pRequest,
				final DynamoHttpServletResponse pResponse) throws ServletException,
				IOException {
	
			if (this.getFormError()) {
				return;
			}
	
			final String shippingMethod = this.getShippingOption();
	
			// 1. if shipping method is sdd then get store id from session.
			// 2. After getting the store id from session set it at hardgood
			// shipping group level
			if (!StringUtils.isBlank(shippingMethod)
					&& shippingMethod.equals(BBBCoreConstants.SDD)) {
	
				BBBHardGoodShippingGroup sddShippingGroup = (BBBHardGoodShippingGroup) this
						.getOrder().getShippingGroups().get(0);
				final String storeId = this.getSessionBean().getSddStoreId();
				if (!StringUtils.isBlank(storeId)) {
	
					sddShippingGroup.setSddStoreId(storeId);
					
				}
	
			}

        // reprice catches problems reported by CyberSource
       this.repriceOrder(pRequest, pResponse);


        if (this.getFormError()) {
            return;
        }

        this.postShipToNewAddress(pRequest, pResponse);

        /* else { postShipToMultipleAddress(pRequest, pResponse); } */
        // postSetupGiftShippingDetails(pRequest, pResponse);
    }

    /** Performs input data validations for new shipping address specified by shopper
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    protected final void preShipToAddress(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        if (this.isAnyHardgoodShippingGroups()) {
            // if we are in a single shipping group checkout then the user has the option of
            // specifying a new shipping address on the form.

            final String shippingMethod = this.getShippingOption();
            final String addressName = this.getShipToAddressName();
            String contactEmail= null;
            String contactPhone = null;

            if (StringUtils.isEmpty(addressName)) {
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(NO_SHIPPING_ADDRESS_SELECTED,
                                LOCALE_EN, null), NO_SHIPPING_ADDRESS_SELECTED));
                return;
            }
            //Code to keep customer contact details in case of registry addresses.
            if( this.getShipToAddressName().contains(REGISTRY_TEXT)){
            final BBBAddress nAddress = (BBBAddress) this.getAddress();
            		contactEmail= nAddress.getEmail();
            		contactPhone=nAddress.getPhoneNumber();
            		nAddress.setId(this.getShipToAddressName());
            }

            BBBAddress address = (BBBAddress) this.getAddressContainer().getAddressFromContainer(addressName);

          //Code to keep customer contact details in case of registry addresses.
            if( this.getShipToAddressName().contains(REGISTRY_TEXT)){
            	address.setEmail(contactEmail);
            	address.setPhoneNumber(contactPhone);
            }
            if (address == null) {
                if (this.getAddressContainer().getAddressMap().containsKey("PROFILE" + addressName)) {
                    address = (BBBAddress) this.getAddressContainer().getAddressFromContainer("PROFILE" + addressName);
                }
            }
            if (isCollegeAddress()) {

                final BBBAddressVO bbbAddress = (BBBAddressVO) address;
                final String checkoutfirstName = pRequest.getParameter("checkoutfirstName");
                final String checkoutlastName = pRequest.getParameter("checkoutlastName");

                final String collegeIdValue = pRequest.getCookieParameter("SchoolCookie");
                BeddingShipAddrVO beddingShipAddrVO = this.getCatalogUtil().validateBedingKitAtt(
                                this.getOrder().getShippingGroups(), collegeIdValue);
				// added code to fix null pointer issue as in case of Weblink order beddingShipAddrVO is coming null
                if(beddingShipAddrVO == null){

                	beddingShipAddrVO = this.getCatalogUtil().getBeddingShipAddrVO(collegeIdValue);
                }
                if(bbbAddress != null){
                	bbbAddress.setFirstName(checkoutfirstName);
                	bbbAddress.setLastName(checkoutlastName);                	
                }

                if(bbbAddress != null && beddingShipAddrVO !=null){
                bbbAddress.setAddress1(beddingShipAddrVO.getAddrLine1());
                bbbAddress.setAddress2(beddingShipAddrVO.getAddrLine2());
                bbbAddress.setCity(beddingShipAddrVO.getCity());
                bbbAddress.setFirstName(checkoutfirstName);
                bbbAddress.setLastName(checkoutlastName);
                bbbAddress.setPostalCode(beddingShipAddrVO.getZip());
                bbbAddress.setState(beddingShipAddrVO.getState());
                bbbAddress.setCompanyName(beddingShipAddrVO.getCompanyName());
                bbbAddress.setIsWebLinkOrderAddr(true);
                }
            }

            // The shipping group for the selected address should already be in the map.

            this.validateShippingAddress(address, pRequest, pResponse);

            // Make sure user isn't trying to Express ship to AK, etc
            this.validateShippingMethod(address, shippingMethod, pRequest, pResponse);

            this.checkItemShipByMethod(address, shippingMethod, pRequest, pResponse);

          //adding coupon related code
            // see if email has changed or not
			BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
			String tempEmail = null;
			String tempPhoneNumber = null; 
			if(address != null) {
				tempEmail = address.getEmail();
				tempPhoneNumber = address.getPhoneNumber();
			}
			
			if (null != order) {
				if (null != order.getShippingAddress()) {
					if ((!BBBUtility.isEmpty(order.getShippingAddress()
									.getEmail())
							&& !BBBUtility.isEmpty(tempEmail)
							&& !order.getShippingAddress().getEmail()
									.equalsIgnoreCase(tempEmail))||(StringUtils
											.isEmpty(order.getShippingAddress()
													.getEmail()))) {
						setShippingEmailChanged(true);
					}

					// see if email has changed or not
					if ((!BBBUtility.isEmpty(order.getShippingAddress()
									.getPhoneNumber())
							&& !BBBUtility.isEmpty(tempPhoneNumber)
							&& !order.getShippingAddress().getPhoneNumber()
									.equalsIgnoreCase(tempPhoneNumber))||(StringUtils
											.isEmpty(order.getShippingAddress()
													.getPhoneNumber()))) {
						setShippingPhoneChanged(true);
					}
				}
				else {
					setShippingEmailChanged(true);
					setShippingPhoneChanged(true);
				}
			}
			// end coupon code

        } // end if single sg checkout
    }

    /** Logic to reprice order, and parse any errors.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public final void
                    repriceOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {

        try {
        	
        	this.getShippingHelper().getPricingTools().priceOrderTotal(this.getOrder(), this.getUserPricingModels(), 
        			this.getUserLocale(pRequest,pResponse), this.getProfile() , new HashMap());

        } catch (final PricingException pe) {

            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(null, "Error w/ PricingTools.priceOrderTotal: "), pe);
            }

            final String pricingMessage = "createPricingErrorMessage(pe, pRequest, pResponse)";
            this.addFormException(new DropletFormException(pricingMessage, pe, "",
                            BBBCoreErrorConstants.CHECKOUT_ERROR_1052));
        }
    }

    /** This method initializes the billing address from the shipping address if the user selected that option. Saves
     * addresses in the profile, if the user selected that option.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    protected final void postShipToNewAddress(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        try {
            String name;
            boolean defaultShip = false;
            boolean defaultBill = false;
            boolean defaultMailing = false;
            if (this.isSingleShippingGroupCheckout() && this.getSaveShippingAddress()
            		&& !this.getProfile().isTransient() && !this.getShipToAddressName().contains(REGISTRY_TEXT) ) {
            	if(this.isUpdateAddress()){
            		name = Long.toString(new Date().getTime());
            		String defaultShippingId = this.getTools().getDefaultShippingAddress(this.getProfile()).getRepositoryId();
            		String defaultBillingId = this.getTools().getDefaultBillingAddress(this.getProfile()).getRepositoryId();
            		final Map<String, Object> edit = getEditValueMap((BBBAddress) address);
            		String addressId= this.getCurrentAddressID();
            		if(defaultShippingId.equalsIgnoreCase(addressId)){
            			defaultShip=true;
            		}
            		if(defaultBillingId.equalsIgnoreCase(addressId)){
            			defaultBill=true;
            		}
            		this.getProfileManager().updateAddressForProfile((Profile) this.getProfile(), edit, defaultShip, defaultBill, defaultMailing, "nickname", "newNickname");
            	}else{
            		final Map addresses = (Map) this.getProfile().getPropertyValue("secondaryAddresses");
            		if ((addresses == null) || addresses.isEmpty()) {

            			final BBBAddress newAddress = this
            					.getCheckoutManager()
            					.getProfileAddressTool()
            					.addNewShippingAddress(
            							(Profile) this.getProfile(),
            							this.getAddressContainer().getAddressFromContainer(
            									this.getShipToAddressName()),
            									this.getOrder().getSiteId(), true, true);
            			name = newAddress.getIdentifier();
            		} else {
            			final BBBAddressImpl newAddress = (BBBAddressImpl) this
            					.getCheckoutManager()
            					.getProfileAddressTool()
            					.addNewShippingAddress(
            							(Profile) this.getProfile(),
            							(BBBAddressVO) this.getAddressContainer().getAddressFromContainer(
            									this.getShipToAddressName()),
            									this.getOrder().getSiteId(), false, false);
            			name = newAddress.getIdentifier();
            		}
            	}
            }else {
                name = Long.toString(new Date().getTime());
            }

            final BBBHardGoodShippingGroup defaultHGSG = (BBBHardGoodShippingGroup) this.getShippingGroupManager()
                            .getFirstNonGiftHardgoodShippingGroupWithRels(this.getOrder());
            defaultHGSG.setSourceId(name);
        } catch (final Exception e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, "Error while saving address"), e);
            }
        }
    }

    Map<String, Object> getEditValueMap(BBBAddress address){
   	 Map<String, Object> edit= new HashMap<String, Object>();
   	 edit.put(BBBCoreConstants.CC_LAST_NAME, address.getLastName());
   	 edit.put(BBBCoreConstants.CC_FIRST_NAME, address.getFirstName());
   	 edit.put(BBBCoreConstants.CC_POSTAL_CODE, address.getPostalCode());
   	 edit.put(BBBCoreConstants.CC_NICKNAME,this.getNickname());
   	 edit.put(BBBCoreConstants.CC_STATE, address.getState());
   	 edit.put(BBBCoreConstants.CC_COUNTRY, address.getCountry());
   	 edit.put(BBBCoreConstants.CC_ADDRESS1, address.getAddress1());
   	 edit.put(BBBCoreConstants.CC_ADDRESS2, address.getAddress2());
   	 edit.put(BBBCoreConstants.CC_COMPANY_NAME, address.getCompanyName());
   	 edit.put(BBBCoreConstants.CC_CITY, address.getCity());
   	 edit.put(BBBCoreConstants.CC_QASVALIDATED, false);
   	 edit.put(BBBCoreConstants.CC_POBOXADDRESS, false);
   	 if(this.getPoBoxStatus()!=null && ((String)this.getPoBoxStatus()).equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
   		 edit.put(BBBCoreConstants.CC_QASVALIDATED, true);
   	 if(this.getPoBoxFlag() !=null && ((String)this.getPoBoxFlag()).equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
   		 edit.put(BBBCoreConstants.CC_POBOXADDRESS, true);
   	return edit;
   }

    /** Validates the new address. Check for required properties, and make sure street address doesn't include PO Box,
     * AFO/FPO.
     *
     * @param pAddress - address
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    protected final void validateShippingAddress(final BBBAddress pAddress, final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws IOException, ServletException {

        final ContactInfo shippingAddress = (ContactInfo) pAddress;
//			removing PO box check as we are accepting PO box address now.
      //ship to po box changes
		List<String> shiptoPOBoxOn;
		boolean shiptoPOFlag =false;
		try {
			shiptoPOBoxOn = getCatalogUtil().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
			shiptoPOFlag = Boolean.parseBoolean(shiptoPOBoxOn.get(0));

        if (!shiptoPOFlag &&!BBBUtility.isNonPOBoxAddress(shippingAddress.getAddress1(), shippingAddress.getAddress2())) {
           this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                           BBBCheckoutConstants.ERROR_POBOX_ADDRESS, LOCALE_EN, null),
                            BBBCheckoutConstants.ERROR_POBOX_ADDRESS));
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
        List errorList = new ArrayList<String>() ;
        if(this.isLTLCommerceItem()){
        	 final BBBAddress ltlShippingAddress = (BBBAddress)pAddress;
        	errorList = this.getShippingHelper().checkForRequiredLTLAddressProperties(ltlShippingAddress, pRequest);
        }else{
        	errorList = getShippingHelper().checkForRequiredAddressProperties(shippingAddress, pRequest);
        }

        this.addAddressValidationFormError(errorList, pRequest, pResponse);
        if (!this.getFormError()) {
            if (null != this.getCisiIndex() && Integer.parseInt(this.getCisiIndex()) >= 0) {
            	final int index = Integer.parseInt(this.getCisiIndex());
	            if (!this.getCheckoutManager().canItemShipToCiSiIndexAddress(this.getOrder().getSiteId(),
                        (CommerceItemShippingInfo)this.getCisiItems().get(index), shippingAddress)) {
		            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_STATE,
		                            LOCALE_EN, null), ERROR_INCORRECT_STATE));
		            this.setRedirectState(CheckoutProgressStates.DEFAULT_STATES.SP_SHIPPING_SINGLE.toString());
		        }
            }else{
	            if (!this.getCheckoutManager().canItemShipToAddress(this.getOrder().getSiteId(),this.getOrder().getCommerceItems(), shippingAddress)) {
	            	if (!((BBBAddress)pAddress).getIsNonPOBoxAddress())
	            		this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_STATE,
	                                	LOCALE_EN, null), ERROR_INCORRECT_STATE));
	            	else
	            		this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_POBOX,
                            	LOCALE_EN, null), ERROR_INCORRECT_POBOX));
	                this.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
	            }
            }
        }

    }



    /** Validates the new address - Make sure user isn't trying to Express ship to AK, etc.
     *
     * @param pAddress - address
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    protected final void validateShippingMethod(final BBBAddress pAddress, final String pShippingMethod,
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                    throws IOException, ServletException {

        if (!this.getCheckoutManager().canItemShipByMethod(this.getOrder().getSiteId(),
                        this.getOrder().getCommerceItems(), pShippingMethod)) {
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(INVALID_SHIPPING_METHOD,
                            LOCALE_EN, null), INVALID_SHIPPING_METHOD));
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_SHIPPING_METHOD));
            }
        }
    }

    /** Utility method to add form exception.
     *
     * @param pMissingProperties - missing properties list
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value */
    protected final void addAddressValidationFormError(final List pErrorProperties,
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
        final List pMissingProperties = (List) pErrorProperties.get(0);
        if (!BBBUtility.isListEmpty(pMissingProperties)) {
            final Map addressPropertyNameMap = this.getShippingHelper().getAddressPropertyNameMap();

            final Iterator properator = pMissingProperties.iterator();
            while (properator.hasNext()) {
                final String property = (String) properator.next();

                if (this.isLoggingDebug()) {
                    this.logDebug("Address validation error with: " + addressPropertyNameMap.get(property)
                                    + " property.");
                }

                // This is the default message, and will only display if there is
                // an exception getting the message from the resource bundle.
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                MSG_MISSING_REQUIRED_ADDRESS_PROPERTY, LOCALE_EN, null)
                                + (String) addressPropertyNameMap.get(property), MSG_MISSING_REQUIRED_ADDRESS_PROPERTY));

            }
        }
        final List pInvalidProperties = (List) pErrorProperties.get(1);
        if (!BBBUtility.isListEmpty(pInvalidProperties)) {
            final Map addressPropertyNameMap = this.getShippingHelper().getAddressPropertyNameMap();

            final Iterator properator = pInvalidProperties.iterator();
            while (properator.hasNext()) {
                final String property = (String) properator.next();

                if (this.isLoggingDebug()) {
                    this.logDebug("Address validation error with: " + addressPropertyNameMap.get(property)
                                    + " property.");
                }

                // This is the default message, and will only display if there is
                // an exception getting the message from the resource bundle.
                this.addFormException(new DropletException((String) addressPropertyNameMap.get(property)
                                + " is Invalid", BBBCoreErrorConstants.CHECKOUT_ERROR_1053));

            }
        }
    }


    /** Clears exeptionsin case of sessin expire.
     *
     * @param pRequest */
    private void clearExceptionsFromRequest(final DynamoHttpServletRequest pRequest) {
        if (this.isLoggingDebug()) {
            this.logDebug("clearExceptionsFromRequest() : starts ");
        }

        pRequest.setParameter("exceptions", null);
        final NameContext ctx = pRequest.getRequestScope();
        if (ctx != null) {
            final Vector exceptions = (Vector) ctx.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE);
            if (exceptions != null) {
                exceptions.clear();
            }
        }
        this.resetFormExceptions();
        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                        BBBCheckoutConstants.ERROR_SESSION_EXPIRED, LOCALE_EN, null),
                        BBBCheckoutConstants.ERROR_SESSION_EXPIRED));

        if (this.isLoggingDebug()) {
            this.logDebug("clearExceptionsFromRequest() : ends");
        }
    }

    /** this method checks for restricted states for standard Shipping method
     *
     * @param siteId
     * @param skuId
     * @param address
     * @param shippingMethod
     * @return */
    public final boolean checkItemShipByMethod(final BBBAddress address, final String shippingMethod,
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
        if (this.isLoggingDebug()) {
            this.logDebug("checkItemShipByMethod() starts : ");
            this.logDebug("Input paramters : address " + address + " ,shippingMethod " + shippingMethod);
        }
        if ((address == null) || StringUtils.isEmpty(address.getState())) {
            if (this.isLoggingDebug()) {
                this.logDebug("No state founds so this item is restricted");
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(RESTRICTED_SHIPPING_METHOD,
                            LOCALE_EN, null), RESTRICTED_SHIPPING_METHOD));
        } else {
            final String state = address.getState();
            if ((this.getStates() != null) && (this.getShippingMethodMap() != null)) {
                final String standardMethod = this.getShippingMethodMap().getProperty("standard");
                if (this.getStates().contains(state) && !standardMethod.equalsIgnoreCase(shippingMethod)) {

                    final String errMsgFromCMS = this.getMsgHandler().getErrMsg(RESTRICTED_SHIPPING_METHOD, LOCALE_EN,
                                    null);

                    final String shippingMethodDescription = this.getShippingMethodDescription(shippingMethod);
                    final String errShippingMethodMsg = new StringBuilder().append(shippingMethodDescription)
                                    .append(" - ").append(errMsgFromCMS).append(" - ").append(state).toString();
                    if (this.isLoggingError()) {
                        this.logError(errShippingMethodMsg);
                    }
                    this.addFormException(new DropletException(errShippingMethodMsg,
                                    BBBCoreErrorConstants.CHECKOUT_ERROR_1054));
                }
            }
        }
        if (this.isLoggingDebug()) {
            this.logDebug("checkItemShipByMethod() ends : There are no state restrictions for shipping method");
        }
        return false;
    }


	/**
	 * This method creates map of commerce items with same sku in a shipping group.
	 * @param sgAfterApply
	 * @return Map <String, List<String>> skuCommItemListMap
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, List<String>> createSkuToCommItemsMap(
			ShippingGroup sgAfterApply) throws BBBSystemException,
			BBBBusinessException {
		Map <String, List<String>> skuCommItemListMap = new HashMap<String, List<String>>();
		List<BBBShippingGroupCommerceItemRelationship> shippingGroupCIRList = sgAfterApply.getCommerceItemRelationships();
		if(!shippingGroupCIRList.isEmpty()){
			for(final BBBShippingGroupCommerceItemRelationship shippingGroupCIR : shippingGroupCIRList){
				if(shippingGroupCIR.getCommerceItem() instanceof BBBCommerceItem && ((BBBCommerceItem)shippingGroupCIR.getCommerceItem()).isLtlItem()){
					String lwaFlag = ((BBBCommerceItem)shippingGroupCIR.getCommerceItem()).getWhiteGloveAssembly();
					if(lwaFlag !=null && lwaFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE) &&
							skuCommItemListMap.keySet().contains(shippingGroupCIR.getCommerceItem().getCatalogRefId()+"Assembly")){
						skuCommItemListMap.get(shippingGroupCIR.getCommerceItem().getCatalogRefId()+"Assembly").add(shippingGroupCIR.getCommerceItem().getId());
					} else if(lwaFlag !=null &&  !lwaFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE) &&
							skuCommItemListMap.keySet().contains(shippingGroupCIR.getCommerceItem().getCatalogRefId())){
						skuCommItemListMap.get(shippingGroupCIR.getCommerceItem().getCatalogRefId()).add(shippingGroupCIR.getCommerceItem().getId());
					} else {
						List <String> commItemList = new ArrayList<String>();
						if(lwaFlag !=null &&  lwaFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE)) {
							commItemList.add(shippingGroupCIR.getCommerceItem().getId());
							skuCommItemListMap.put(shippingGroupCIR.getCommerceItem().getCatalogRefId()+"Assembly", commItemList);
						} else {
							commItemList.add(shippingGroupCIR.getCommerceItem().getId());
							skuCommItemListMap.put(shippingGroupCIR.getCommerceItem().getCatalogRefId(), commItemList);
						}
					}
				}
			}
		}
		return skuCommItemListMap;
	}


	/**
	 * This method is used to set ShippingGroupName and ShippingMethod for CISI of associated delivery and assembly item if commerce item is ltl
	 * @param cisi
	 * @param sgLtlItemAssocList
	 * @param inputShipGroupMethodKey
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void updateCISIForDelAndAssItems(CommerceItemShippingInfo cisi,
			final String inputShipGroupMethodKey) throws BBBSystemException,
			BBBBusinessException {

		String deliveryId = ((BBBCommerceItem)cisi.getCommerceItem()).getDeliveryItemId();
		String assemblyId = ((BBBCommerceItem)cisi.getCommerceItem()).getAssemblyItemId();
        ((CommerceItemShippingInfo)this.getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(deliveryId).get(0)).setShippingGroupName(inputShipGroupMethodKey);
		((CommerceItemShippingInfo)this.getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(deliveryId).get(0)).setShippingMethod(cisi.getShippingMethod());
		if(BBBUtility.isNotEmpty(assemblyId)){
			((CommerceItemShippingInfo)this.getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(assemblyId).get(0)).setShippingGroupName(inputShipGroupMethodKey);
			((CommerceItemShippingInfo)this.getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(assemblyId).get(0)).setShippingMethod(cisi.getShippingMethod());
		}
		/*f(((BBBCommerceItem)cisi.getCommerceItem()).isLtlItem()){
			for(Map<String, Map<String,String>> sgLtlItemAssoc: sgLtlItemAssocList){
				if(sgLtlItemAssoc.get(cisi.getCommerceItem().getId())!=null){
					RepositoryItem ltlAssocationMap = (RepositoryItem)sgLtlItemAssoc.get(cisi.getCommerceItem().getId());
					String assemblyId = "";
					String deliveryId = "";
					if(ltlAssocationMap != null){
						deliveryId = (String) ltlAssocationMap.getPropertyValue("deliveryItemId");
						assemblyId = (String) ltlAssocationMap.getPropertyValue("assemblyItemId");
					}
					((CommerceItemShippingInfo)this.getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(deliveryId).get(0)).setShippingGroupName(inputShipGroupMethodKey);
					((CommerceItemShippingInfo)this.getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(deliveryId).get(0)).setShippingMethod(cisi.getShippingMethod());
					if(BBBUtility.isNotEmpty(assemblyId)){
						((CommerceItemShippingInfo)this.getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(assemblyId).get(0)).setShippingGroupName(inputShipGroupMethodKey);
						((CommerceItemShippingInfo)this.getCommerceItemShippingInfoContainer().getCommerceItemShippingInfos(assemblyId).get(0)).setShippingMethod(cisi.getShippingMethod());
					}
				}
			}
		}*/
	}


    /**
     * @param pRequest
     * @param pResponse
     * @throws ServletException
     * @throws IOException
     */
    protected final void checkGiftCardRestriction(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        final Set<String> giftCardSet = new HashSet<String>();
        final Set<String> nonGiftCardSet = new HashSet<String>();
        CommerceItemShippingInfo tempCISI = null;
        String tempKey = null;
        String skuId = null;
        String shippingMethod = null;
        CommerceItem tempCommItem = null;

        for (final Object object : this.getCisiItems()) {
            tempCISI = (CommerceItemShippingInfo) object;
            tempCommItem = tempCISI.getCommerceItem();
            if (tempCommItem instanceof BBBCommerceItem) {

                tempKey = (tempCISI.getShippingGroupName() + NICKNAME_SEPARATOR + tempCISI.getShippingMethod());
                skuId = tempCommItem.getCatalogRefId();
                shippingMethod = tempCISI.getShippingMethod();
                boolean isGiftcardItem = false;
                if (BBBCoreConstants.SHIP_METHOD_EXPRESS_ID.equalsIgnoreCase(shippingMethod)) {
                    try {
                        isGiftcardItem = this.getCheckoutManager().isGiftCardItem(this.getOrder().getSiteId(), skuId);
                    } catch (final BBBBusinessException e) {
                        if (this.isLoggingError()) {
                            this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_MULTIPLE_SHIPPING), e);
                        }
                    } catch (final BBBSystemException e) {
                        if (this.isLoggingError()) {
                            this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_MULTIPLE_SHIPPING), e);
                        }
                    }

                    if (isGiftcardItem) {
                        if (nonGiftCardSet.contains(tempKey)) {
                            // already some shippable items are present
                            giftCardSet.remove(tempKey);
                        } else {
                            giftCardSet.add(tempKey);
                        }
                    } else {
                        if (giftCardSet.contains(tempKey)) {
                            giftCardSet.remove(tempKey);
                        } else {
                            nonGiftCardSet.add(tempKey);
                        }
                    }
                } // End If for Express Method

            }

        }// End For Loop for all CISI

        if (!giftCardSet.isEmpty()) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SHIPPING_GIFT_CARD));
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIPPING_GIFT_CARD,
                            LOCALE_EN, null), ERROR_SHIPPING_GIFT_CARD));
        }

        if (this.isLoggingDebug()) {
            this.logDebug("checkGiftCardRestriction() Ends  :: giftCardSet size - " + giftCardSet.size());
        }
    }

    /** @return */
    public final List getCisiItems() {
        final List<CommerceItemShippingInfo> commerceItemShippingInfos = new ArrayList<CommerceItemShippingInfo>();

        for (final Object cisiObj : this.getCommerceItemShippingInfoContainer().getAllCommerceItemShippingInfos()) {
            if (((CommerceItemShippingInfo) cisiObj).getCommerceItem() instanceof BBBCommerceItem) {
                commerceItemShippingInfos.add((CommerceItemShippingInfo) cisiObj);
            }
        }

        this.commerceItemShippingInfos = commerceItemShippingInfos;
        return this.commerceItemShippingInfos;
    }

    protected final void applyRegistrantEmailToSG() {
        String sgName;
        BBBHardGoodShippingGroup bbbHGSG;
        ShippingGroup sg;
        // BBBCommerceItem bbbCommItem;
        for (final Object object : this.getCisiItems()) {
            final CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) object;
            sgName = cisi.getShippingGroupName();
            // bbbCommItem = (BBBCommerceItem)cisi.getCommerceItem();
            sg = this.getShippingGroupContainerService().getShippingGroup(sgName);
            if ((sg instanceof BBBHardGoodShippingGroup) && (cisi.getSplitShippingGroupName() != null)) {
                if (!("false".equalsIgnoreCase(cisi.getSplitShippingGroupName()))) {
                    bbbHGSG = (BBBHardGoodShippingGroup) sg;
                    // bbbHGSG.setRegistryId(bbbCommItem.getRegistryId());
                    bbbHGSG.setShippingConfirmationEmail(cisi.getSplitShippingGroupName());
                    bbbHGSG.setSendShippingConfirmation(true);
                }
            }
        }
    }

    /** @return */
    public final String getRemoveEmptyShippingGroup() {
        try {
            this.getShippingGroupManager().removeEmptyShippingGroups(this.getOrder());
        } catch (final CommerceException e) {
            this.logError("Error removing Shipping group", e);
        }
        return "true";
    }

    /** @return */
    public final String getCommerceItemShippingInfoClass() {
        return this.commerceItemShippingInfoClass;
    }

    /** @param mCommerceItemShippingInfoClass */
    public final void setCommerceItemShippingInfoClass(final String mCommerceItemShippingInfoClass) {
        this.commerceItemShippingInfoClass = mCommerceItemShippingInfoClass;
    }

    /** @return */
    public final BBBAddressContainer getMultiShippingAddrCont() {
        return this.multiShippingAddressContainer;
    }

    /** @param mMultiShippingAddrCont */
    public final void setMultiShippingAddrCont(final BBBAddressContainer mMultiShippingAddrCont) {
        this.multiShippingAddressContainer = mMultiShippingAddrCont;
    }

    /** @return */
    public final String getShipToMultiplePeopleErrorURL() {
        return this.shipToMultiplePeopleErrorURL;
    }

    /** @param mShipToMultiplePeopleErrorURL */
    public final void setShipToMultiplePeopleErrorURL(final String mShipToMultiplePeopleErrorURL) {
        this.shipToMultiplePeopleErrorURL = mShipToMultiplePeopleErrorURL;
    }

    /** @return */
    public final String getShipToMultiplePeopleSuccessURL() {
        return this.shipToMultiplePeopleSuccessURL;
    }

    /** @param mShipToMultiplePeopleSuccessURL */
    public final void setShipToMultiplePeopleSuccessURL(final String mShipToMultiplePeopleSuccessURL) {
        this.shipToMultiplePeopleSuccessURL = mShipToMultiplePeopleSuccessURL;
    }

    /** @return */
    public final boolean isOrderIncludesGifts() {
    	return this.orderIncludesGifts;
    }

    /** @param mOrderIncludesGifts */
    public final void setOrderIncludesGifts(final boolean mOrderIncludesGifts) {
    	 this.orderIncludesGifts = mOrderIncludesGifts;
     	final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
     	request.getSession().setAttribute(GIFTS_ARE_INCLUDED_IN_ORDER,Boolean.valueOf(mOrderIncludesGifts));
    }

    /** @return */
    public final String getStoreSGMethodName() {
        return this.storeShippingGroupMethodName;
    }

    /** @param mStoreSGMethodName */
    public final void setStoreSGMethodName(final String mStoreSGMethodName) {
        this.storeShippingGroupMethodName = mStoreSGMethodName;
    }

    /** @return */
    public final CheckoutProgressStates getCheckoutProgressStates() {
        return this.checkoutProgressStates;
    }

    /** @param pCheckoutProgressStates */
    public final void setCheckoutProgressStates(final CheckoutProgressStates pCheckoutProgressStates) {
        this.checkoutProgressStates = pCheckoutProgressStates;
    }

    /** @return the Shipping Helper component. */
    public BBBPurchaseProcessHelper getShippingHelper() {
        return this.shippingHelper;
    }

    /** @param pShippingHelper the shipping helper component to set. */
    public void setShippingHelper(final BBBPurchaseProcessHelper pShippingHelper) {
        this.shippingHelper = pShippingHelper;
    }

    /** Set the ShippingGroupInitializers property.
     *
     * @param pShippingGroupInitializers a <code>ServiceMap</code> value */
    @Override
    public final void setShippingGroupInitializers(final ServiceMap pShippingGroupInitializers) {
        this.shippingGroupInitializers = pShippingGroupInitializers;
    }

    /** Return the ShippingGroupInitializers property.
     *
     * @return a <code>ServiceMap</code> value */
    @Override
    public final ServiceMap getShippingGroupInitializers() {
        return this.shippingGroupInitializers;
    }

    /** @return the default shipping method name. */
    public final String getDefaultShippingMethod() {
        return this.defaultShippingMethod;
    }

    /** @param pDefaultShippingMethod - the default shipping method to set. */
    public final void setDefaultShippingMethod(final String pDefaultShippingMethod) {
        this.defaultShippingMethod = pDefaultShippingMethod;
    }

    /** @return true if single shipping group should be checkouted, false - otherwise. */
    public final boolean isSingleShippingGroupCheckout() {
        return this.singleShippingGroupCheckout;
    }

    /** @param pSingleShippingGroupCheckout - true if single shipping group should be checkouted, false - otherwise. */
    public final void setSingleShippingGroupCheckout(final boolean pSingleShippingGroupCheckout) {
        this.singleShippingGroupCheckout = pSingleShippingGroupCheckout;
    }

    /** Returns the URL to redirect on successful handling of operations
     *
     * @return */
    public final String getSuccessURL() {
        return this.successURL;
    }

    /** Sets the URL to redirect on successful handling of operations
     *
     * @param pSuccessURL */
    public final void setSuccessURL(final String pSuccessURL) {
        this.successURL = pSuccessURL;
    }

    /** Returns the URL to redirect on error on handling of operations
     *
     * @return */
    public final String getErrorURL() {
        return this.errorURL;
    }

    /** Sets the URL to redirect on error on handling of operations
     *
     * @param pErrorURL */
    public final void setErrorURL(final String pErrorURL) {
        this.errorURL = pErrorURL;
    }

    /** Returns the URL to redirect on System error while handling of operations
     *
     * @return */
    public final String getSystemErrorURL() {
        return this.systemErrorURL;
    }

    /** Sets the URL to redirect on System error while handling of operations
     *
     * @param pSystemErrorURL */
    public final void setSystemErrorURL(final String pSystemErrorURL) {
        this.systemErrorURL = pSystemErrorURL;
    }

    /** gets the old shipping id for the commerce item
     *
     * @return */
    public final String getOldShippingId() {
        return this.oldShippingId;
    }

    /** sets the old shipping id from the commerce item
     *
     * @param oldShippingId */
    public final void setOldShippingId(final String pOldShippingId) {
        this.oldShippingId = pOldShippingId;
    }

    /** returns the CMS message handler to add form exceptions
     *
     * @return */
    public final LblTxtTemplateManager getMsgHandler() {
        return this.messageHandler;
    }

    /** Sets the CMS message handler to add form exceptions
     *
     * @param pMsgHandler */
    public final void setMsgHandler(final LblTxtTemplateManager pMsgHandler) {
        this.messageHandler = pMsgHandler;
    }

    /** gets the store id provided on the shipping page
     *
     * @return */
    public final String getStoreId() {
        return this.storeId;
    }

    /** sets the store id from the shippingpage
     *
     * @param mStoreId */
    public final void setStoreId(final String mStoreId) {
        this.storeId = mStoreId;
    }

    /** getter method for the order id
     *
     * @return */
    public final String getOrderId() {
        return this.orderId;
    }

    /** Setter method sets the orderid to the formhandler
     *
     * @param pOrderId */
    public final void setOrderId(final String pOrderId) {
        this.orderId = pOrderId;
    }

    /** getter method gets the inventory quantity. has to be replaced with bopus inventory quantity
     *
     * @return */
    public final String getNewQuantity() {
        return this.newQuantity;
    }

    /** setter method sets the inventory quantity. has to be replaced with bopus inventory quantity The bopus api should
     * return the quantity, storeID
     *
     * @param pNewQuantity */
    public final void setNewQuantity(final String pNewQuantity) {
        this.newQuantity = pNewQuantity;
    }

    /** getter method gets the commerce item id
     *
     * @return */
    public final String getCommerceItemId() {
        return this.commerceItemId;
    }

    /** setter method sets the commerce item id
     *
     * @param pCommerceItemId */
    public final void setCommerceItemId(final String pCommerceItemId) {
        this.commerceItemId = pCommerceItemId;
    }

    /** getter method gets the bbbInventoryManager
     *
     * @return */
    public final BBBInventoryManager getBbbInventoryManager() {
        return this.inventoryManager;
    }

    /** setter method sets the bbbInventoryManager
     *
     * @param pCommerceItemId */
    public final void setBbbInventoryManager(final BBBInventoryManager bbbInventoryManager) {
        this.inventoryManager = bbbInventoryManager;
    }

    /** @return */
    public final BBBShippingGroupManager getManager() {
        return this.shippingGroupManager;
    }

    /** @param pManager */
    public final void setManager(final BBBShippingGroupManager pManager) {
        this.shippingGroupManager = pManager;
    }

    /** @return the mBBBShippingInfoBean */
    public final BBBShippingInfoBean getBBBShippingInfoBean() {
        if (this.shippingInfoBean == null) {
            this.shippingInfoBean = new BBBShippingInfoBean();
        }
        return this.shippingInfoBean;
    }

    /** @param pBBBShippingInfoBean the mBBBShippingInfoBean to set */
    public final void setBBBShippingInfoBean(final BBBShippingInfoBean pBBBShippingInfoBean) {
        this.shippingInfoBean = pBBBShippingInfoBean;
    }

    /** @return */
    public final boolean getNewAddress() {
        return this.newAddress;
    }

    /** @param value */
    public final void setNewAddress(final boolean value) {
        this.newAddress = value;
    }

    /** @return true if shipping address should be saved, false - otherwise. */
    public final boolean getSaveShippingAddress() {
        return this.saveShippingAddress;
    }

    /** @param pSaveShippingAddress - true if shipping address should be saved, false - otherwise. */
    public final void setSaveShippingAddress(final boolean pSaveShippingAddress) {
        this.saveShippingAddress = pSaveShippingAddress;
    }

    /** @return bedding kit order flag. */
    public final String getBeddingKitOrder() {
        return this.beddingKitOrder;
    }

    /** @param pBeddingKitOrder - bedding kit order. */
    public final void setBeddingKitOrder(final String pBeddingKitOrder) {
        this.beddingKitOrder = pBeddingKitOrder;
    }


    /** @return shipping address. */
    public final String getShipToAddressName() {
        return this.shipToAddressName;
    }

    /** @param pShipToAddressName - shipping address. */
    public final void setShipToAddressName(final String pShipToAddressName) {
        this.shipToAddressName = pShipToAddressName;
    }

    /** @return new shipping address. */
    public final String getNewShipToAddressName() {
        if (this.newShipToAddressName == null) {
            this.newShipToAddressName = this.getProfile().getRepositoryId() + new Date().getTime();
        }
        return this.newShipToAddressName;
    }

    /** @param pNewShipToAddressName - new shipping address. */
    public final void setNewShipToAddressName(final String pNewShipToAddressName) {
        this.newShipToAddressName = pNewShipToAddressName;

        if (this.newShipToAddressName != null) {
            this.newShipToAddressName = this.newShipToAddressName.trim();
        }
    }

    /** @return the address. */
    public final Address getAddress() {
        return this.address;
    }

    /** @param pAddress - the address to set. */
    public final void setAddress(final Address pAddress) {
        this.address = pAddress;
    }

    /** @return */
    public final BBBAddressContainer getAddressContainer() {
        return this.addressContainer;
    }

    /** @param mAddressContainer */
    public final void setAddressContainer(final BBBAddressContainer mAddressContainer) {
        this.addressContainer = mAddressContainer;
    }

    /** @return */
    public final boolean getSendShippingConfEmail() {
        return this.sendShippingConfEmail;
    }

    /** @param pSendShippingConfEmail */
    public final void setSendShippingConfEmail(final boolean pSendShippingConfEmail) {
        this.sendShippingConfEmail = pSendShippingConfEmail;
    }

    /** @return */
    public final String getShippingOption() {
        return this.shippingOption;
    }

    /** @param shippingOption */
    public final void setShippingOption(final String shippingOption) {
        this.shippingOption = shippingOption;
    }

    /** @return */
    public final boolean getPackNHold() {
        return this.packAndHold;
    }

    /** @param packAndHold */
    public final void setPackNHold(final boolean packAndHold) {
        this.packAndHold = packAndHold;
    }

    /** @return */
    public final String getPackNHoldDate() {
        return this.packAndHoldDate;
    }

    /** @param packAndHoldDate */
    public final void setPackNHoldDate(final String packAndHoldDate) {
        this.packAndHoldDate = packAndHoldDate;
    }

    /** @return */
    public final BBBCheckoutManager getCheckoutManager() {
        return this.checkoutManager;
    }

    /** @param checkoutManager */
    public final void setCheckoutManager(final BBBCheckoutManager checkoutManager) {
        this.checkoutManager = checkoutManager;
    }

    /** @return */
    public final String getSiteId() {
        return this.siteId;
    }

    /** @param pSiteId */
    public final void setSiteId(final String pSiteId) {
       this.siteId = pSiteId;
    }

    /** @return */
    public final String getSendShippingEmail() {
        return this.sendShippingEmail;
    }

    /** @param sendShippingEmail */
    public final void setSendShippingEmail(final String sendShippingEmail) {
        this.sendShippingEmail = sendShippingEmail;
    }

    /** @return */
    public final BBBShippingGroupContainerService getShippingGroupContainerService() {
        return this.shippingGroupContainerService;
    }

    /** @param pShippingGroupContainerService */
    public final void setShippingGroupContainerService(
                    final BBBShippingGroupContainerService pShippingGroupContainerService) {
        this.shippingGroupContainerService = pShippingGroupContainerService;
    }

    /** @return */
    public final String getCisiIndex() {
        return this.commerceItemShippingInfoIndex;
    }

    /** @param cisiIndex */
    public final void setCisiIndex(final String cisiIndex) {
        this.commerceItemShippingInfoIndex = cisiIndex;
    }

    /** @return */
    public final boolean isClearContainer() {
        return this.clearContainer;
    }

    /** @param clearContainer */
    public final void setClearContainer(final boolean clearContainer) {
        this.clearContainer = clearContainer;
    }

    /** @return */
    public final Properties getShippingMethodMap() {
        return this.shippingMethodMap;
    }

    /** @param mShippingMethodMap */
    public final void setShippingMethodMap(final Properties mShippingMethodMap) {
        this.shippingMethodMap = mShippingMethodMap;
    }

    /** @return */
    public final List getStates() {
        return this.states;
    }

    /** @param mStates */
    public final void setStates(final List mStates) {
        this.states = mStates;
    }

    /** @return */
    public final BBBCatalogTools getCatalogUtil() {
        return this.catalogTools;
    }

    /** @param catalogUtil */
    public final void setCatalogUtil(final BBBCatalogTools catalogUtil) {
        this.catalogTools = catalogUtil;
    }

    @Override
    public final boolean isLoggingDebug() {
        return this.getCommonConfiguration().isLoggingDebugForRequestScopedComponents();
    }

    /** @return the commonConfiguration */
    public final CommonConfiguration getCommonConfiguration() {
        return this.commonConfiguration;
    }

    /** @param pCommonConfiguration the commonConfiguration to set */
    public final void setCommonConfiguration(final CommonConfiguration pCommonConfiguration) {
        this.commonConfiguration = pCommonConfiguration;
    }

    /** @return */
    public final Map<String, CommerceItemShipInfoVO> getSGCommerceItemShipInfoVO() {
        if (this.shippingGroupCommerceItemShipInfoVOs == null) {
            this.shippingGroupCommerceItemShipInfoVOs = new HashMap<String, CommerceItemShipInfoVO>();
        }
        return this.shippingGroupCommerceItemShipInfoVOs;
    }

    /** @param hardGoodSGShipmentList */
    public final void setSGCommerceItemShipInfoVO(final Map<String, CommerceItemShipInfoVO> hardGoodSGShipmentList) {
        this.shippingGroupCommerceItemShipInfoVOs = hardGoodSGShipmentList;
    }

    /** @param key
     * @param object */
    public final void setSGCommerceItemShipInfoVO(final String key, final Object object) {
        this.shippingGroupCommerceItemShipInfoVOs.put(key, (CommerceItemShipInfoVO) object);
    }

    /** @return */
    public final Map<String, String> getShippingGroupCombinations() {
        if (this.shippingGroupCombinations == null) {
            this.shippingGroupCombinations = new HashMap<String, String>();
        }
        return this.shippingGroupCombinations;
    }

    /** @param mShippingGroupCombinations */
    public final void setShippingGroupCombinations(final Map<String, String> mShippingGroupCombinations) {
        this.shippingGroupCombinations = mShippingGroupCombinations;
    }

    /** @param key
     * @param shippingGroupCombinations */
    public final void setShippingGroupCombinations(final String key, final Object shippingGroupCombinations) {
        this.shippingGroupCombinations.put(key, (String) shippingGroupCombinations);
    }

    /** @return */
    public final Map<String, Address> getShippingAddressCombinations() {

        if (this.shippingAddressCombinations == null) {
            this.shippingAddressCombinations = new HashMap<String, Address>();
        }
        return this.shippingAddressCombinations;
    }

    /** @param mShippingAddressCombinations */
    public final void setShippingAddressCombinations(final Map<String, Address> mShippingAddressCombinations) {

        this.shippingAddressCombinations = mShippingAddressCombinations;
    }

    /** @param key
     * @param shippingAddressCombinations */
    public final void setShippingAddressCombinations(final String key, final Object shippingAddressCombinations) {
        this.shippingAddressCombinations.put(key, (Address) shippingAddressCombinations);
    }

    /** @return */
    public final BBBAddressContainer getShippingAddressContainer() {
        return this.shippingAddressContainer;
    }

    /** @param pShippingAddressContainer */
    public final void setShippingAddressContainer(final BBBAddressContainer pShippingAddressContainer) {
        this.shippingAddressContainer = pShippingAddressContainer;
    }

    /** @param giftMessageInput */
    public final void setGiftMessageInput(final String giftMessageInput) {
        this.giftMessageInput = giftMessageInput;
    }

    /** @return */
    public final String getGiftMessageInput() {
        return this.giftMessageInput;
    }

    /** @param checkoutFailureURLsForREST */
    public final void setCheckoutFailureURLsForREST(final Map<String, String> checkoutFailureURLsForREST) {
        this.checkoutFailureURLsForREST = checkoutFailureURLsForREST;
    }

    /** @return */
    public final Map<String, String> getCheckoutFailureURLsForREST() {
        return this.checkoutFailureURLsForREST;
    }

    /** @param giftMsgAPIDelimiter */
    public final void setGiftMsgAPIDelimiter(final String giftMsgAPIDelimiter) {
        this.giftMsgAPIDelimiter = giftMsgAPIDelimiter;
    }

    /** @return */
    public final String getGiftMsgAPIDelimiter() {
        return this.giftMsgAPIDelimiter;
    }

    /** @param giftMsgAPIPropertiesDelimiter */
    public final void setGiftMsgAPIPropertiesDelimiter(final String giftMsgAPIPropertiesDelimiter) {
        this.giftMsgAPIPropertiesDelimiter = giftMsgAPIPropertiesDelimiter;
    }

    /** @return */
    public final String getGiftMsgAPIPropertiesDelimiter() {
        return this.giftMsgAPIPropertiesDelimiter;
    }

    /** @param shippingGroupDroplet */
    public final void setShippingGroupDroplet(final ShippingGroupDroplet shippingGroupDroplet) {
        this.shippingGroupDroplet = shippingGroupDroplet;
    }

    /** @return */
    public final ShippingGroupDroplet getShippingGroupDroplet() {
        return this.shippingGroupDroplet;
    }

    /** This method either adds or removes gift messages : REST API
     *
     * @param pRequest
     * @param pResponse
     * @return boolean
     * @throws ServletException
     * @throws IOException
     * @throws BBBBusinessException */
    public final boolean handleSaveGiftInfoToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("Start handleGiftMessagingAPI");
        // set data for REST API request
        this.populateShippingInfoBean(pRequest);

        if (this.getFormError()) {
            this.logDebug("Form error occurred- return false");
            return false;
        }
        this.logDebug("End handleGiftMessagingAPI");

        return this.handleGiftMessaging(pRequest, pResponse);
    }

    /** Populates ShippingInfoBean from the input gift message string.
     *
     * @param request
     * @throws BBBBusinessException */
    private void populateShippingInfoBean(final DynamoHttpServletRequest request) {

        this.logDebug("Start - BBBShippingGroupFormhandler.populateShippingInfoBean()");

        final String giftMessageInput = this.getGiftMessageInput();
        if (StringUtils.isBlank(giftMessageInput)) {
            // Input string is empty throw an error
            this.logError("Input string is empty");
            this.addFormException(new DropletException("Input string is empty",
                            BBBCoreErrorConstants.ERROR_ADD_REMOVE_GIFT_MESSAGE_1001));
            return;
        }

        final List<BBBShippingInfoBean> shippingBeanList = new ArrayList<BBBShippingInfoBean>();
        BBBShippingInfoBean shippingInfoBean = null;
        String[] beanPropertiesArr = null;
        final String[] beanInputArray = giftMessageInput.split(this.getGiftMsgAPIDelimiter());

        // iterate over beanInputArray and creates shippingInfoBean per Shipping group
        for (int i = 0; i < beanInputArray.length; i++) {
            shippingInfoBean = new BBBShippingInfoBean();
            beanPropertiesArr = beanInputArray[i].split(this.getGiftMsgAPIPropertiesDelimiter());
            if ((null == beanPropertiesArr)
                            || (beanPropertiesArr.length != 4)
                            || !(org.apache.commons.lang.StringUtils.equalsIgnoreCase("true", beanPropertiesArr[2]) || org.apache.commons.lang.StringUtils
                                            .equalsIgnoreCase("false", beanPropertiesArr[2]))
                            || org.apache.commons.lang.StringUtils.isBlank(beanPropertiesArr[0])) {
                this.logError("Input data for Shipping Group No: " + (i + 1) + " is incorrect throw an error");
                this.addFormException(new DropletException("Input data for Shipping Group No: " + (i + 1)
                                + " is incorrect", BBBCoreErrorConstants.ERROR_ADD_REMOVE_GIFT_MESSAGE_1003));
                return;
            }
            shippingInfoBean.setShippingGroupId(beanPropertiesArr[0]);
            //decode gift message string with URLDecoder [Message string sent as encoded from mobile side - JS] to store in shippingInfoBean.
            try {
				shippingInfoBean.setGiftMessage(URLDecoder.decode(beanPropertiesArr[1], BBBCoreConstants.UTF_ENCODING));
			} catch (UnsupportedEncodingException e) {
				this.logError("Exception while decoding Gift message: " + beanPropertiesArr[1] +  " Shipping Group ID: " + beanPropertiesArr[0] + " Exception: " + e);
			}
            shippingInfoBean.setGiftWrap(Boolean.valueOf(beanPropertiesArr[2]));
            shippingInfoBean.setGiftingFlag(Boolean.valueOf(beanPropertiesArr[3]));
            shippingBeanList.add(shippingInfoBean);
        }
        this.setBBBShippingInfoBeanList(shippingBeanList);

        this.setSiteId(getCurrentSiteId());
        request.setContextPath("");

        this.getCheckoutProgressStates().setCheckoutFailureURLs(this.getCheckoutFailureURLsForREST());

        this.logDebug("End BBBShippingGroupFormhandler.populateShippingInfoBean()");

    }




    /** @param commerceItemShipInfoVOList */
    public final void setCommerceItemShipInfoVOList(final List<CommerceItemShipInfoVO> commerceItemShipInfoVOList) {
        this.commerceItemShipInfoVOs = commerceItemShipInfoVOList;
    }

    /** @return */
    public final List<CommerceItemShipInfoVO> getCommerceItemShipInfoVOList() {
        return this.commerceItemShipInfoVOs;
    }

    /** @param ciInfo */
    public final void setCiInfo(final CommerceItemShippingInfo ciInfo) {
        this.commerceItemShippingInfo = ciInfo;
    }

    /** @return */
    public final CommerceItemShippingInfo getCiInfo() {
        return this.commerceItemShippingInfo;
    }

    @Override
    public void logInfo(final String pMessage) {
        if (this.getManageLogging().isShippingFormHandlerLogging()) {
            this.logInfo(pMessage, null);
        }
    }

    @Override
    public final void logDebug(final String pMessage) {
        if (this.isLoggingDebug()) {
            this.logDebug(pMessage, null);
        }
    }

	/**
	 * @return the redirectState
	 */
	public String getRedirectState() {
		return redirectState;
	}

	/**
	 * @param redirectState the redirectState to set
	 */
	public void setRedirectState(String redirectState) {
		this.redirectState = redirectState;
	}

	/**
	 * @return the paypalServiceManager
	 */
	public BBBPayPalServiceManager getPaypalServiceManager() {
		return paypalServiceManager;
	}

	/**
	 * @param paypalServiceManager the paypalServiceManager to set
	 */
	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}

	/**
	 * @return the paypalAddressStatus
	 */
	public String getPaypalAddressStatus() {
		return paypalAddressStatus;
	}

	/**
	 * @param paypalAddressStatus the paypalAddressStatus to set
	 */
	public void setPaypalAddressStatus(String paypalAddressStatus) {
		this.paypalAddressStatus = paypalAddressStatus;
	}

	/**
	 * @return the shippingGroupChanged
	 */
	public boolean isShippingGroupChanged() {
		return shippingGroupChanged;
	}

	/**
	 * @param shippingGroupChanged the shippingGroupChanged to set
	 */
	public void setShippingGroupChanged(boolean shippingGroupChanged) {
		this.shippingGroupChanged = shippingGroupChanged;
	}
	

	/**
	 * @return the shippingGroupErrorMsg
	 */
	public String getShippingGroupErrorMsg() {
		return shippingGroupErrorMsg;
	}

	/**
	 * @param shippingGroupErrorMsg the shippingGroupErrorMsg to set
	 */
	public void setShippingGroupErrorMsg(String shippingGroupErrorMsg) {
		this.shippingGroupErrorMsg = shippingGroupErrorMsg;
	}

	/**
	 * @return the collegeAddress
	 */
	public boolean isCollegeAddress() {
		return collegeAddress;
	}
	

	/**
	 * @param collegeAddress the collegeAddress to set
	 */
	public void setCollegeAddress(boolean collegeAddress) {
		this.collegeAddress = collegeAddress;
	}
	

	/**
	 * @return the shippingEmailChanged
	 */
	public boolean isShippingEmailChanged() {
		return shippingEmailChanged;
	}
	

	/**
	 * @param shippingEmailChanged the shippingEmailChanged to set
	 */
	public void setShippingEmailChanged(boolean shippingEmailChanged) {
		this.shippingEmailChanged = shippingEmailChanged;
	}
	

	/**
	 * @return the shippingPhoneChanged
	 */
	public boolean isShippingPhoneChanged() {
		return shippingPhoneChanged;
	}
	

	/**
	 * @param shippingPhoneChanged the shippingPhoneChanged to set
	 */
	public void setShippingPhoneChanged(boolean shippingPhoneChanged) {
		this.shippingPhoneChanged = shippingPhoneChanged;
	}

	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}

	/**
	 * @return the removePorchService
	 */
	public boolean isRemovePorchService() {
		return removePorchService;
	}

	/**
	 * @param removePorchService the removePorchService to set
	 */
	public void setRemovePorchService(boolean removePorchService) {
		this.removePorchService = removePorchService;
	}
	

	
}
