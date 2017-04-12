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
import java.util.Collection;
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
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.ShippingGroupDroplet;
import atg.commerce.order.purchase.ShippingGroupFormHandler;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingException;
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
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;

import com.bbb.account.BBBAddressTools;
import com.bbb.account.BBBGetCouponsManager;
import com.bbb.account.BBBProfileManager;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressAPIConstants;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.ManageCheckoutLogging;
import com.bbb.commerce.order.Paypal;
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
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BaseCommerceItemImpl;
 
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.CommonConfiguration;
import com.bbb.wishlist.GiftListVO;


public class BBBShippingGroupFormhandler extends ShippingGroupFormHandler {

    public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/order/purchase/ShippingInfoFormHandler.java#2 $$Change: 633752 $";

    private static final String MSG_MISSING_REQUIRED_ADDRESS_PROPERTY = "MSG_MISSING_REQUIRED_ADDRESS_PROPERTY";
    protected static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String DATE_FORMAT_CANADA = "dd/MM/yyyy";
    private static final String IMPROPER_DATE_PACK_HOLD = "ERROR_SHIPPING_IMPROPER_DATE_PACK_HOLD";
    private static final String INVALID_ENDDATE_PACK_HOLD = "ERROR_SHIPPING_INVALID_ENDDATE_PACK_HOLD";
    private static final String NOT_ALL_PACK_HOLD = "ERROR_SHIPPING_NOT_ALL_PACK_HOLD";
    private static final String SKU_NOT_PRESENT = "ERROR_NOT_PRESENT_CATALOG";
    private static final String NICKNAME_SEPARATOR = ";;";
    protected static final String REGISTRY_TEXT = "registry";
    private static final String PROFILE_TEXT = "PROFILE";
    protected static final String SHIPPING_RESTRICTION_TRUE = "?shippingRestriction=true";
    protected static final String PORCH_SHIPPING_RESTRICTION_TRUE = "?porchServiceRestriction=true";  
    protected static final String PORCH_MULTI_SHIPPING_RESTRICTION_TRUE = "&porchServiceRestriction=true"; 
    private static final String RELOAD_MULTI_SHIPPING_PAGE = "?reloadPage=";
    private static final String MULTI_SHIPPING_RESTRICTION_TRUE = "&shippingRestriction=true";
    private static final String ERR_MULTI_GIFT_INVALID_SHIPPINGGROUPID = "err_multi_gift_invalid_shippinggroupid";
    private static final String GENERIC_ERROR_TRY_LATER = "GENERIC_ERROR_TRY_LATER";
    private static final String ERR_CART_QUANTITY_INCORRECT = "ERR_CART_QUANTITY_INCORRECT";
    private static final String ERR_CART_INVALID_SHIPPING = "ERR_CART_INVALID_SHIPPING";
    private static final String ERR_CART_INVALID_INPUT = "ERR_CART_INVALID_INPUT";
    private static final String ERR_CART_OUT_OF_STOCK = "ERR_CART_OUT_OF_STOCK";
    protected static final String LOCALE_EN = BBBCoreConstants.DEFAULT_LOCALE;
    private static final String ERROR_INCORRECT_STATE = "ERROR_INCORRECT_STATE";
    private static final String ERROR_INCORRECT_COUNTRY = "ERROR_INCORRECT_COUNTRY";
    private static final String ERROR_INCORRECT_POBOX = "ERROR_INCORRECT_POBOX";
    private static final String ERROR_ENTER_ALTERNATE_ADDRESS = "ERROR_ENTER_ALTERNATE_ADDRESS";
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
    protected static final String ERROR_SHIPPING_GENERIC_ERROR = "ERROR_SHIPPING_GENERIC_ERROR";
    private static final String IMPROPER_SHIPPING_METHOD_HOLD = "ERROR_SHIPPING_IMPROPER_SHIPPING_METHOD_HOLD";
    private static final String NO_SHIPPING_ADDRESS_SELECTED = "ERROR_SHIPPING_NO_SHIPPING_ADDRESS_SELECTED";
    private static final String BEDDING_KIT = "beddingKit";
    private static final String GIFTS_ARE_INCLUDED_IN_ORDER = "giftsAreIncludedInOrder";

    private LblTxtTemplateManager messageHandler;
    private CommonConfiguration commonConfiguration;
    private CommerceItemShippingInfo commerceItemShippingInfo;
    private ShippingGroupDroplet shippingGroupDroplet;
    private Properties shippingMethodMap;
    private Address address = new BBBAddressVO();
    private CheckoutProgressStates checkoutProgressStates;
    private ServiceMap shippingGroupInitializers;
    private ManageCheckoutLogging manageLogging;

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
	private PorchServiceManager porchServiceManager;
	private List commerceItemShippingInfos;
    private List states;
    private List<CommerceItemShipInfoVO> commerceItemShipInfoVOs;
    private List<BBBShippingInfoBean> shippingInfoBeans;
    private Map<String, CommerceItemShipInfoVO> shippingGroupCommerceItemShipInfoVOs;
    private Map<String, Address> shippingAddressCombinations;
    private Map<String, String> checkoutFailureURLsForREST = new HashMap<String, String>();
    private Map<String, String> shippingGroupCombinations;

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

    protected boolean collegeAddress;
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
    private BBBPayPalSessionBean payPalSessionBean;
    private boolean shippingGroupChanged;
    private String shippingGroupErrorMsg;
    private String paypalAddressStatus;
    private boolean ltlCommerceItem;
    private String addNewAddressCommItemId;
    private BBBMultiShippingManager multiShippingManager;
    private String poBoxFlag;
    private String poBoxStatus;
    private boolean qasOnProfileAddress;
    private String savePhone;
    private String saveEmail;
    private BBBGetCouponsManager bbbGetCouponsManager;
    private String fromPage;// Page Name that will be set from JSP
    private Map<String,String> successUrlMap;
    private Map<String,String> errorUrlMap;
    private String sddRequired;
    private BBBSessionBean sessionBean;
    private String queryParam;//Query Parameter that will be appended to success/error URL
    private boolean ajaxAddressFilled;
	private boolean updateOrderSummaryAjax;
    private boolean removePorchService;

 	/**
 	 * @return queryParam
 	 */
 	public String getQueryParam() {
 		return queryParam;
 	}
 	/**
 	 * @param queryParam
 	 */
 	public void setQueryParam(String queryParam) {
 		this.queryParam = queryParam;
 	}
	public String getSddRequired() {
		return sddRequired;
	}

	public void setSddRequired(String sddRequired) {
		this.sddRequired = sddRequired;
	}
    public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}
	

	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	
	/**
     * @return fromPage
     */
	public String getFromPage() {
		return fromPage;
	}

	/**
	 * @param fromPage
	 */

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

    
    public BBBGetCouponsManager getBbbGetCouponsManager() {
		return bbbGetCouponsManager;
	}

	public void setBbbGetCouponsManager(BBBGetCouponsManager bbbGetCouponsManager) {
		this.bbbGetCouponsManager = bbbGetCouponsManager;
	}
    
    /**
	 * @return the savePhone
	 */
	public String getSavePhone() {
		return savePhone;
	}

	/**
	 * @param savePhone the savePhone to set
	 */
	public void setSavePhone(String savePhone) {
		this.savePhone = savePhone;
	}

	/**
	 * @return the saveEmail
	 */
	public String getSaveEmail() {
		return saveEmail;
	}

	/**
	 * @param saveEmail the saveEmail to set
	 */
	public void setSaveEmail(String saveEmail) {
		this.saveEmail = saveEmail;
	}

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

    /** The pre method to change the commerce item to store pickup shipping group The method performs the initial
     * validations to verify the commerce item id passed, verify the storeId passed to change to storepick shipping
     * group and also validates the new quantity to which the commerce item has to be updated The method also validates
     * if the old shipping group id is already present in the order.shippingGroups and returns error validations
     *
     * @param pRequest
     * @param pResponse
     * @throws ServletException
     * @throws IOException */
    public void preChangeToStorePickup(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws CommerceException, NumberFormatException {
        if (this.isLoggingDebug()) {
            this.logDebug("Entry preChangeToStorePickup");
        }

        if ((this.getCommerceItemId() != null) && !StringUtils.isEmpty(this.getStoreId())
                        && (this.getNewQuantity() != null)) {
            if (this.isLoggingDebug()) {
                this.logDebug("getCommerceItemId is :" + this.getCommerceItemId());
                this.logDebug("getStoreId is :" + this.getStoreId());
                this.logDebug("getNewQuantity is :" + this.getNewQuantity());
                this.logDebug("Old Shipping group is:" + this.getOldShippingId());
            }
            final Object bbbItemObj = this.getOrder().getCommerceItem(this.getCommerceItemId());
            if (bbbItemObj instanceof BBBCommerceItem) {
                final BBBCommerceItem bbbItem = (BBBCommerceItem) bbbItemObj;

                final long currentQuantity = Long.parseLong(this.getNewQuantity());
                if ((currentQuantity < bbbItem.getQuantity())
                                && (this.getShippingGroupContainerService().getShippingGroup(this.getOldShippingId()) instanceof BBBStoreShippingGroup)) {
                	setChangeStoreAfterSplitFlag(true);
                }
                this.updateOrderAvailabiltyMap(bbbItem);
                this.checkStoreInventory(pRequest, bbbItem, currentQuantity);
            }
        } else {
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_INVALID_INPUT,
                            LOCALE_EN, null), ERR_CART_INVALID_INPUT));
        }

        if (this.isLoggingDebug()) {
            this.logDebug("Exit preChangeToStorePickup");
        }
    }

    /** @param bbbItem */
    private void updateOrderAvailabiltyMap(final BBBCommerceItem bbbItem) {

        final BBBOrder bbbOrder = (BBBOrderImpl) this.getOrder();
        if (bbbOrder != null) {
            if (!BBBUtility.isMapNullOrEmpty(bbbOrder.getAvailabilityMap())) {
                final Map<String, Integer> tempAvailabilityMap = ((BBBOrderImpl) this.getOrder()).getAvailabilityMap();
                tempAvailabilityMap.remove(bbbItem.getId());
            }
        }

    }

    private void checkStoreInventory(final DynamoHttpServletRequest pRequest, final BBBCommerceItem bbbItem,
                    final long currentQuantity)
                    throws ShippingGroupNotFoundException, InvalidParameterException, InventoryException {
        boolean foundItem = false;

        if (bbbItem.getQuantity() < currentQuantity) {
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_QUANTITY_INCORRECT,
                            LOCALE_EN, null), ERR_CART_QUANTITY_INCORRECT));
        } else if ((bbbItem.getQuantity() == currentQuantity) || this.changeStoreAfterSplitFlag) {
            foundItem = true;
        } else if (this.getOldShippingId() != null) {
            // ShippingGroup shpGrp = getOrder().getShippingGroup(getOldShippingId());
            final ShippingGroup shpGrp = this.getShippingGroupContainerService().getShippingGroup(
                            this.getOldShippingId());
            if (shpGrp instanceof HardgoodShippingGroup) {
                final List<CommerceItemRelationship> comItemRel = shpGrp.getCommerceItemRelationships();
                for (final CommerceItemRelationship comRel : comItemRel) {
                    if (this.isLoggingDebug()) {
                        this.logDebug("commerce item relation -- commerce item id:" + comRel.getCommerceItem().getId());
                        this.logDebug("commerce item id:" + this.getCommerceItemId());
                    }
                    if (comRel.getCommerceItem().getId().equals(this.getCommerceItemId())) {
                        foundItem = true;
                        if (comRel.getQuantity() < currentQuantity) {
                            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                            ERR_CART_QUANTITY_INCORRECT, LOCALE_EN, null), ERR_CART_QUANTITY_INCORRECT));
                        }
                        break;
                    }
                }
            }
        } else {
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_INVALID_SHIPPING,
                            LOCALE_EN, null), ERR_CART_INVALID_SHIPPING));
        }
        if (!foundItem) {
            if (this.isLoggingDebug()) {
                this.logDebug("Shipping group not found in the commerce relation");
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_INVALID_INPUT,
                            LOCALE_EN, null), ERR_CART_INVALID_INPUT));
        } else {
            final List<String> storeIds = new ArrayList<String>();
            storeIds.add(this.getStoreId());

            Map<String, Integer> storeInventoryMap;

            storeInventoryMap = this.getBbbInventoryManager().getBOPUSProductAvailability(this.getOrder().getSiteId(),
                            bbbItem.getCatalogRefId(), storeIds, currentQuantity, BBBInventoryManager.ONLINE_STORE,
                            this.getStoreInventoryContainer(), true,null, false , false);

            /* int inventoryStatus = getBbbInventoryManager().getBOPUSProductAvailability(getOrder().getSiteId(),
             * bbbItem.getCatalogRefId(), getStoreId(), currentQuantity, BBBInventoryManager.ONLINE_STORE); */
            if (BBBInventoryManager.NOT_AVAILABLE == storeInventoryMap.get(this.getStoreId())) {
                if (this.isLoggingError()) {
                    this.logError(LogMessageFormatter.formatMessage(pRequest, ERR_CART_OUT_OF_STOCK));
                }
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_OUT_OF_STOCK,
                                LOCALE_EN, null), ERR_CART_OUT_OF_STOCK));
            }
        }
    }

    /** The method modifies the commerce item relation to storepickup shipping group If the new quantity is less than the
     * commerceItem.quantity the method creates a new commerce item with the new quantity If the new quantity is same as
     * the commerceItem.quantity then the method modifies the relationship of the commerceItem to the storepickup
     * shipping group
     *
     * @param pRequest
     * @param pResponse */
    private void changeToStorePickup(final DynamoHttpServletRequest pRequest,
                                    final DynamoHttpServletResponse pResponse) throws CommerceException {
        if (this.isLoggingDebug()) {
            this.logDebug("Entry changeToStorePickup");
        }
        BBBCommerceItem bbbItem = null;
        final Object bbbItemObj = this.getOrder().getCommerceItem(this.getCommerceItemId());
        if (bbbItemObj instanceof BBBCommerceItem) {
            bbbItem = (BBBCommerceItem) bbbItemObj;
        }

        if (bbbItem != null) {
            final long currentQuantity = Long.parseLong(this.getNewQuantity());
            final long itemQuantity = bbbItem.getQuantity();
            final BBBCommerceItemManager ciManager = (BBBCommerceItemManager) this.getCommerceItemManager();
            if (currentQuantity < itemQuantity) {
                if (this.isLoggingDebug()) {
                    this.logDebug("currentQuantity == bbbItem.getQuantity()");
                }
                if (this.getShippingGroupContainerService().getShippingGroup(this.getOldShippingId()) != null) {
                    ciManager.splitCommerceItemByQuantity(this.getOrder(), currentQuantity, bbbItem, this.getStoreId(),
                                    this.getShippingGroupContainerService().getShippingGroup(this.getOldShippingId())
                                                    .getId(), this.changeStoreAfterSplitFlag,
                                    this.getStoreInventoryContainer());
                } else {
                    throw new CommerceException(ERR_MULTI_GIFT_INVALID_SHIPPINGGROUPID);
                }
            }
            if (currentQuantity == itemQuantity) {
                if (this.isLoggingDebug()) {
                    this.logDebug("currentQuantity == bbbItem.getQuantity()");
                }
                ciManager.modifyCommerceItemShippingGroup(this.getOrder(), bbbItem, this.getStoreId());
            }

        }
        if (this.isLoggingDebug()) {
            this.logDebug("Exit changeToStorePickup");
        }
    }

    /** The post method for change to store pickup commerce items. Method kept for post validations for change to store
     * pickup
     *
     * @param pRequest
     * @param pResponse
     * @return */
    public final boolean postChangeToStorePickup(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) {
        this.removeEmptyShippingGroups(pRequest);
        ((BBBOrderManager) this.getOrderManager()).updateAvailabilityMapInOrder(pRequest, this.getOrder());
        return true;
    }

    /** handle method to change the shipping group to store pickup shipping group. The method is invoked from the modify
     * shipping group page and performs calling the pre method for validations If no formErrors are found it calls the
     * changeToStorePickup for modifying the relationship for the commerce item
     *
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException */
    public final boolean handleChangeToStorePickup(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        if (this.isLoggingDebug()) {
            this.logDebug("Entry handleChangeToStorePickup");
        }
       
		StringBuffer appendData = new StringBuffer("");
        final String myHandleMethod = "BBBShippingGroupFormhandler.handleChangeToStorePickup";
        if ((StringUtils.isEmpty(getFromPage())) && (StringUtils.isNotEmpty(getQueryParam())))
		{
			appendData.append(getQueryParam());
			setSuccessURL(appendData.toString());
			setErrorURL(appendData.toString());
			setSystemErrorURL(appendData.toString());
		}
		
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.SHIPPING_STORE_PICKUP, myHandleMethod);
            Transaction tr = null;
            try {
                tr = this.ensureTransaction();
                if (this.getUserLocale() == null) {
                    this.setUserLocale(this.getUserLocale(pRequest, pResponse));
                }
                if (!this.checkFormRedirect(null, this.getSystemErrorURL(), pRequest, pResponse)) {
                    return false;
                }

                // checking for session expire
                if ((null == this.getOrder()) || (this.getOrder().getCommerceItemCount() == 0)) {
                    this.clearExceptionsFromRequest(pRequest);
                    this.checkFormRedirect(this.successURL, this.errorURL, pRequest, pResponse);
                    return false;
                }

                synchronized (this.getOrder()) {
                    this.preChangeToStorePickup(pRequest, pResponse);
                    if (!this.checkFormRedirect(null, this.getErrorURL(), pRequest, pResponse)) {
                        return false;
                    }
                    //Start: Release 2.2 Code-Jira BED-415
                    final List<PaymentGroup> paymentList = this.getOrder().getPaymentGroups();
					Paypal pp = null;
					for (final PaymentGroup paymentGroup : paymentList) {
						if (paymentGroup instanceof Paypal) {
							pp = (Paypal) paymentGroup;
						}
					}
					if (pp != null) {
						((BBBPaymentGroupManager) getOrderManager().getPaymentGroupManager()).removePaymentGroupFromOrder(this.getShoppingCart().getCurrent(), pp.getId());
						if(((BBBOrder) this.getOrder()).getBillingAddress().isFromPaypal()){
							((BBBOrderImpl)this.getOrder()).setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null);
						}
						this.getOrderManager().updateOrder(this.getOrder());
					}
					//End: Release 2.2 Code-Jira BED-415
                    this.changeToStorePickup(pRequest, pResponse);
                    if (!this.checkFormRedirect(null, this.getErrorURL(), pRequest, pResponse)) {
                        return false;
                    }
                    setChangeStoreAfterSplitFlag(false);
                    this.postChangeToStorePickup(pRequest, pResponse);
                    this.getOrderManager().updateOrder(this.getOrder());
                    this.runProcessRepriceOrder(PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING, this.getOrder(),
                                    this.getUserPricingModels(), this.getUserLocale(), this.getProfile(), null);
                }
            } catch (final NumberFormatException e) {
                this.addSystemException(ERR_CART_INVALID_INPUT, e);
            } catch (final CommerceException e) {
                this.addSystemException(GENERIC_ERROR_TRY_LATER, e);
                this.getShoppingCart().deleteOrder(this.getOrderId());
                pRequest.getSession().removeAttribute(BBBCoreConstants.COOKIE_USED_ONCE);

            } catch (final RunProcessException e) {
                this.addSystemException(GENERIC_ERROR_TRY_LATER, e);
            } finally {
                if (tr != null) {
                    this.commitTransaction(tr);
                }
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.SHIPPING_STORE_PICKUP, myHandleMethod);
            }

            return this.checkFormRedirect(this.successURL, this.errorURL, pRequest, pResponse);
        }
        return false;
    }

    /** The pre method to change the commerce item to online shipping group The method performs the initial validations
     * to verify the commerce item id passed, verify the storeId passed from the commerce item and also validates the
     * new quantity to which the commerce item has to be updated The method also validates if the old shipping group id
     * is already present in the order.shippingGroups and returns error validations
     *
     * @param pRequest
     * @param pResponse */
    public void preChangeToShipOnline(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws CommerceException, NumberFormatException {
        if (this.isLoggingDebug()) {
            this.logDebug("Entry preChangeToOnlinePickup");
        }

        if ((this.getCommerceItemId() != null) && (this.getNewQuantity() != null)) {
            if (this.isLoggingDebug()) {
                this.logDebug("Commerce item id:" + this.getCommerceItemId());
                this.logDebug("Current quantity:" + this.getNewQuantity());
                this.logDebug("Old shipping group:" + this.getOldShippingId());
            }
            BBBCommerceItem bbbItem = null;
            final Object bbbItemObj = this.getOrder().getCommerceItem(this.getCommerceItemId());
            if (bbbItemObj instanceof BBBCommerceItem) {
                bbbItem = (BBBCommerceItem) bbbItemObj;
            }

            if ((bbbItem != null) && (bbbItem.getStoreId() != null)) {
                final long currentQuantity = Long.parseLong(this.getNewQuantity());

                this.updateOrderAvailabiltyMap(bbbItem);
                this.checkOnlineInventory(pRequest, bbbItem, currentQuantity);

            }
        } else {
            if (this.isLoggingDebug()) {
                this.logDebug("Invalaid input to the formhandler: Either commerceItemId is null or quantity is null");
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_INVALID_INPUT,
                            LOCALE_EN, null), ERR_CART_INVALID_INPUT));
        }
        if (this.isLoggingDebug()) {
            this.logDebug("Exit preChangeToOnlinePickup");
        }
    }

    private void checkOnlineInventory(final DynamoHttpServletRequest pRequest, final BBBCommerceItem bbbItem,
                    final long currentQuantity)
                    throws ShippingGroupNotFoundException, InvalidParameterException, CommerceException {
        boolean foundItem = false;
        if (bbbItem.getQuantity() < currentQuantity) {
            if (this.isLoggingDebug()) {
                this.logDebug("The new quantity is greater than the actual commerce item quantity");
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_QUANTITY_INCORRECT,
                            LOCALE_EN, null), ERR_CART_QUANTITY_INCORRECT));
        } else if (bbbItem.getQuantity() == currentQuantity) {
            foundItem = true;
        } else if (this.getOldShippingId() != null) {
            final ShippingGroup shpGrp = this.getOrder().getShippingGroup(this.getOldShippingId());
            if (shpGrp instanceof BBBStoreShippingGroup) {
                final List<CommerceItemRelationship> comItemRel = shpGrp.getCommerceItemRelationships();
                for (final CommerceItemRelationship comRel : comItemRel) {
                    if (this.isLoggingDebug()) {
                        this.logDebug("commerce item relation -- commerce item id:" + comRel.getCommerceItem().getId());
                        this.logDebug("commerce item id:" + this.getCommerceItemId());
                    }
                    if (comRel.getCommerceItem().getId().equals(this.getCommerceItemId())) {
                        foundItem = true;
                        if (comRel.getQuantity() < currentQuantity) {
                            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                            ERR_CART_QUANTITY_INCORRECT, LOCALE_EN, null), ERR_CART_QUANTITY_INCORRECT));
                        }
                        break;
                    }
                }
            }
        } else {
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_INVALID_SHIPPING,
                            LOCALE_EN, null), ERR_CART_INVALID_SHIPPING));
        }
        if (!foundItem) {
            if (this.isLoggingDebug()) {
                this.logDebug("Shipping group not found in the commerce relation");
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERR_CART_INVALID_INPUT,
                            LOCALE_EN, null), ERR_CART_INVALID_INPUT));
        } else {
            // Inventory Check
            final BBBPurchaseProcessHelper purchaseProcessHelper = (BBBPurchaseProcessHelper) this
                            .getPurchaseProcessHelper();
            final int inventoryStatus = purchaseProcessHelper.checkInventory(this.getOrder().getSiteId(),
                            bbbItem.getCatalogRefId(), null, this.getOrder(), currentQuantity,
                            BBBInventoryManager.STORE_ONLINE, this.getStoreInventoryContainer(),
                            BBBInventoryManager.AVAILABLE);

            if (BBBInventoryManager.NOT_AVAILABLE == inventoryStatus) {
                if (this.isLoggingError()) {
                    this.logError(LogMessageFormatter.formatMessage(pRequest, ERR_CART_OUT_OF_STOCK));
                }
            }

        }
    }

    /** The method modifies the commerce item relation to hardgood shipping group If the new quantity is less than the
     * commerceItem.quantity the method creates a new commerce item with the new quantity If the new quantity is same as
     * the commerceItem.quantity then the method modifies the relationship of the commerceItem to the online shipping
     * group
     *
     * @param pRequest
     * @param pResponse
     * @throws CommerceException */
    public final void changeToShipOnline(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws CommerceException {
        if (this.isLoggingDebug()) {
            this.logDebug("Entry changeToOnlinePickup");
        }

        BBBCommerceItem bbbItem = null;
        final Object bbbItemObj = this.getOrder().getCommerceItem(this.getCommerceItemId());
        if (bbbItemObj instanceof BBBCommerceItem) {
            bbbItem = (BBBCommerceItem) bbbItemObj;
        }

        if (bbbItem != null) {
            final long currentQuantity = Long.parseLong(this.getNewQuantity());
            final long itemQuantity = bbbItem.getQuantity();
            final BBBCommerceItemManager ciManager = (BBBCommerceItemManager) this.getCommerceItemManager();
            if (currentQuantity < itemQuantity) {
                if (this.isLoggingDebug()) {
                    this.logDebug("currentQuantity < bbbItem.getQuantity()");
                }
                ciManager.splitCommerceItemByQuantity(this.getOrder(), currentQuantity, bbbItem, this.getStoreId(),
                                this.getOldShippingId(), false, this.getStoreInventoryContainer());
            }
            if (currentQuantity == itemQuantity) {
                if (this.isLoggingDebug()) {
                    this.logDebug("currentQuantity == bbbItem.getQuantity()");
                }
                ciManager.modifyCommerceItemShippingGroup(this.getOrder(), bbbItem, null);
                ciManager.setInventoryStatus(this.getOrder(), this.getStoreInventoryContainer(), bbbItem,
                                BBBInventoryManager.STORE_ONLINE);
            }
        }
        if (this.isLoggingDebug()) {
            this.logDebug("Exit changeToOnlinePickup");
        }
    }

    /** The post method for change to online ship commerce items. Method kept for post validations for change to online
     * pickup
     *
     * @param pRequest
     * @param pResponse
     * @return Success / Failure
     * @throws CommerceException */
    public boolean postChangeToShipOnline(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws CommerceException {
        this.removeEmptyShippingGroups(pRequest);
        // ((BBBOrderManager)getOrderManager()).updateAvailabilityMapInOrder(pRequest, getOrder(),
        // BBBInventoryManager.STORE_ONLINE);
        // R2.1 Defect fixing for OOS messaging in Saved item and cart page.
        BBBCommerceItem bbbItem = null;
        final Object bbbItemObj = this.getOrder().getCommerceItem(this.getCommerceItemId());
        if (bbbItemObj instanceof BBBCommerceItem) {
            bbbItem = (BBBCommerceItem) bbbItemObj;
        }
        boolean isChange = false;
        if ((bbbItem != null) && !bbbItem.isMsgShownOOS()) {
            @SuppressWarnings ("unchecked")
            final List<CommerceItem> commerceItemLists = this.getOrder().getCommerceItems();

            for (final CommerceItem commerceItem : commerceItemLists) {
                if ((commerceItem instanceof BBBCommerceItem)
                                && !bbbItem.getId().equalsIgnoreCase(commerceItem.getId())
                                && commerceItem.getCatalogRefId().equalsIgnoreCase(bbbItem.getCatalogRefId())) {
                    final BBBCommerceItem bbbcommerceItem = (BBBCommerceItem) commerceItem;
                    if (bbbcommerceItem.isMsgShownOOS()) {
                        isChange = true;
                        break;
                    }
                }
            }
            if (!isChange) {
                final BBBSavedItemsSessionBean bean = (BBBSavedItemsSessionBean) ServletUtil.getCurrentRequest()
                                .resolveName(BBBCoreConstants.SAVEDCOMP);
                final List<GiftListVO> giftListVO = bean.getItems();
                if (!BBBUtility.isListEmpty(giftListVO)) {
                    for (final GiftListVO savedItem : giftListVO) {
                        if (savedItem.isMsgShownOOS()) {
                            isChange = true;
                            break;
                        }
                    }
                }
            }
            if (isChange) {
                bbbItem.setMsgShownOOS(true);
                this.getOrderManager().updateOrder(this.getOrder());
            }
        }
        return true;
    }

    /** handle method to change the shipping group to online shipping group. The method is invoked from the modify
     * shipping group page and performs calling the pre method for validations If no formErrors are found it calls the
     * changeToShipOnline for modifying the relationship for the commerce item
     *
     * @param pRequest
     * @param pResponse
     * @return Success / Failure
     * @throws ServletException
     * @throws IOException */
    public final boolean handleChangeToShipOnline(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        if (this.isLoggingDebug()) {
            this.logDebug("Entry handleChangeToOnlinePickup");
        }
        final String myHandleMethod = "BBBShippingGroupFormhandler.handleChangeToShipOnline";
        // BBBH-391 | Client DOM XSRF changes
        String siteId = getCurrentSiteID();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
			setSystemErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
		}
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.SHIPPING_ONLINE_SHIP, myHandleMethod);
            Transaction tr = null;
            try {
                tr = this.ensureTransaction();
                if (this.getUserLocale() == null) {
                    this.setUserLocale(this.getUserLocale(pRequest, pResponse));
                }
                if (!this.checkFormRedirect(null, this.getSystemErrorURL(), pRequest, pResponse)) {
                    return false;
                }
                synchronized (this.getOrder()) {
                    this.preChangeToShipOnline(pRequest, pResponse);
                    if (!this.checkFormRedirect(null, this.getErrorURL(), pRequest, pResponse)) {
                        return false;
                    }
                    this.changeToShipOnline(pRequest, pResponse);
                    if (!this.checkFormRedirect(null, this.getErrorURL(), pRequest, pResponse)) {
                        return false;
                    }
                    this.postChangeToShipOnline(pRequest, pResponse);
                    this.getOrderManager().updateOrder(this.getOrder());
                    this.runProcessRepriceOrder(PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING, this.getOrder(),
                                    this.getUserPricingModels(), this.getUserLocale(), this.getProfile(), null);
                }
            } catch (final NumberFormatException e) {
                this.addSystemException(ERR_CART_INVALID_INPUT, e);
            } catch (final CommerceException e) {
                this.addSystemException(GENERIC_ERROR_TRY_LATER, e);
            } catch (final RunProcessException e) {
                this.addSystemException(GENERIC_ERROR_TRY_LATER, e);
            } finally {
                if (tr != null) {
                    this.commitTransaction(tr);
                }
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.SHIPPING_ONLINE_SHIP, myHandleMethod);
            }

            return this.checkFormRedirect(this.successURL, this.errorURL, pRequest, pResponse);
        }
        return false;
    }

    /** Removes Empty shipping groups from the order
     *
     * @param pRequest */
    private void removeEmptyShippingGroups(final DynamoHttpServletRequest pRequest) {
        try {
            this.getShippingGroupManager().removeEmptyShippingGroups(this.getOrder());
        } catch (final CommerceException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, "Exception removing empty shipping groups "),
                                e);
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
        this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.GIFT.toString());
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
//                    this.getOrderManager().updateOrder(this.getOrder());
					// reprice order
//					if (!Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED))) {
						this.repriceOrder(pRequest, pResponse);
						this.getOrderManager().updateOrder(this.getOrder());
//					}
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
                        CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
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
	            this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
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
    protected void giftMessaging() {
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

    /** This handler method will validate shipping address and apply to the input shipping group.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @return a <code>boolean</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */

    public final boolean handleAddNewAddress(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	
    	this.setClearContainer(false);
        this.getCheckoutProgressStates().setCurrentLevel(
                        CheckoutProgressStates.DEFAULT_STATES.SHIPPING_MULTIPLE.toString());

        final String myHandleMethod = "BBBShippingGroupFormHandler.handleAddNewAddress";
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
        // String redirectURL = "/store/checkout/shipping/frag/newAdd_MultiShip.jsp";

        final String redirectURL = this.getAddNewAddressURL();

        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_MULTIPLE_SHIPPING, myHandleMethod);
            Transaction tr = null;

            try {
                tr = this.ensureTransaction();

                if (!this.checkFormRedirect(null, redirectURL, pRequest, pResponse)) {
                    return false;
                }

                synchronized (this.getOrder()) {
                    this.preAddNewAddress(pRequest, pResponse);

                    if (this.getFormError()) {
                        if (this.isLoggingDebug()) {
                            this.logDebug("Redirecting due to form error in preAddNewAddress.");
                        }
                        return this.checkFormRedirect(null, redirectURL, pRequest, pResponse);
                    }

                    this.addNewAddress(pRequest, pResponse);

                    if (this.getFormError()) {
                        if (this.isLoggingDebug()) {
                            this.logDebug("Redirecting due to form error in addNewAddress");
                        }
                        this.setTransactionToRollbackOnly();

                        return this.checkFormRedirect(null, redirectURL, pRequest, pResponse);
                    }

                    this.postAddNewAddress(pRequest, pResponse);

                    if (this.getFormError()) {
                        if (this.isLoggingDebug()) {
                            this.logDebug("Redirecting due to form error in postAddNewAddress");
                        }

                        return this.checkFormRedirect(null, redirectURL, pRequest, pResponse);
                    }

                }

            } catch (final SystemException e) {
                if (this.isLoggingError()) {

                    this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SHIP_ADD_NEW_ADDRESS), e);
                }
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIP_ADD_NEW_ADDRESS,
                                LOCALE_EN, null), ERROR_SHIP_ADD_NEW_ADDRESS));
            } finally {
                /* Complete the transaction */
                if (tr != null) {
                    this.commitTransaction(tr);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_MULTIPLE_SHIPPING, myHandleMethod);
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
            }
        }
        // return true;
        return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
    }

    /** This method validates the user inputs for the AddNewAddress
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public void preAddNewAddress(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	
    	if(this.getPoBoxStatus()!=null && this.getPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
   	 		((BBBAddress)this.getAddress()).setQasValidated(true);
        if(this.getPoBoxFlag()!=null && this.getPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
        	((BBBAddress)this.getAddress()).setPoBoxAddress(true);
        
        // profile address sent to QAS during multi ship checkout
        if (this.isQasOnProfileAddress())
        	updateProfileAddress(pRequest, pResponse);
        
        this.validateShippingAddress(this.getAddress(), pRequest, pResponse);
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
        if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(getSiteId())){
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

        if (this.getSendShippingConfEmail()
                        && org.apache.commons.lang.StringUtils.isNotBlank(this.getShipToAddressName())
                        && this.getShipToAddressName().contains(REGISTRY_TEXT)) {
            this.getBBBShippingInfoBean().setShippingConfirmationEmail(this.getSendShippingEmail());
            this.getBBBShippingInfoBean().setSendShippingConfirmation(true);
        }
        this.getBBBShippingInfoBean().setShippingMethod(this.getShippingOption());
        final HardgoodShippingGroup firstNonGiftHardgoodShippingGroupWithRels = this.getShippingGroupManager()
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

    protected void setRegistryInfo(final BBBAddress addressFromContainer, final Map registryMap) {
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
    public void addNewAddress(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                    throws ServletException, IOException {
        String name = null;
        
        // profile address sent to QAS during multi ship checkout
        if (this.isQasOnProfileAddress())
        	return;
        
        try {

           BBBAddressImpl bbbAddress = new BBBAddressImpl();
           bbbAddress = (BBBAddressImpl) BBBAddressTools.copyBBBAddress((BBBAddress) this.getAddress(),
                           (BBBAddress) bbbAddress);

            if (this.getSaveShippingAddress()) {
                final BBBAddressImpl newAddress = this.getCheckoutManager().saveAddressToProfile(
                                (Profile) this.getProfile(), this.getAddress(), this.getOrder().getSiteId());
                name = newAddress.getIdentifier();
            } else {
                name = Long.toString(new Date().getTime());
            }

           // BBBAddressImpl bbbAddress = new BBBAddressImpl();
           // bbbAddress = (BBBAddressImpl) BBBAddressTools.copyBBBAddress((BBBAddress) this.getAddress(),
          //                  (BBBAddress) bbbAddress);
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
    /** Update Hold and Pack Info method for REST API
    *
    * @param pRequest
    * @param pResponse
    * @return boolean status flag
    * @throws ServletException
    * @throws IOException */
    public final boolean handleUpdateHoldNPackInfo(final DynamoHttpServletRequest pRequest,
                   final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
       
       logDebug("BBBShippingGroupFormHandler.handleUpdateHoldNPackInfo(): START");
       Transaction tr = null;
       final String myHandleMethod = "BBBShippingGroupFormHandler.handleUpdateHoldNPackInfo";
       final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
       final String siteId = getCurrentSiteID();
       this.setSiteId(siteId);
	   final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, pRequest.getLocale());
       if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
           BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_SINGLE_SHIPPING, myHandleMethod);
           
           try {
               tr = this.ensureTransaction();
               synchronized (this.getOrder()) {
                   try {
                       
                	   this.preUpdateHoldNPackInfo(pRequest);
                       if (this.getFormError()) {
                           if (this.isLoggingDebug()) {
                               this.logDebug("Returning due to form error in preUpdateHoldNPackInfo.");
                           }
                           return false; 
                       }
                       // Duplicate code - Already written in "validatePackNHold"
                       /*try {
                    	   final HardgoodShippingGroup hardGoodShippingGroup = this.getShippingGroupManager()
                                   .getFirstNonGiftHardgoodShippingGroupWithRels(this.getOrder());
                    	   if (this.getPackNHold() && !StringUtils.isEmpty(this.getPackNHoldDate())) {
                    		   hardGoodShippingGroup.setShipOnDate(formatter.parse(this.getPackNHoldDate()));
                           }else {
                        	   hardGoodShippingGroup.setShipOnDate(null);
                           }
                    	   
                       } catch (final ParseException e) {
                           if (this.isLoggingError()) {
                               this.logError(LogMessageFormatter.formatMessage(pRequest, "PACK_DATE_NOT_PROPER"), e);
                           }
                       }*/                     
                   } catch (final Exception exc) {
                       this.setNewShipToAddressName(null);
                       this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                       ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
                       if (this.isLoggingError()) {
                           this.logError("Exception occured", exc);
                       }
                   }

                   this.postUpdateHoldNPackInfo(pRequest);

                   try {
                       this.getOrderManager().updateOrder(this.getOrder());
                   } catch (final Exception exc) {
                       if (this.isLoggingError()) {
                           this.logError("Exception occured while updateorder during BBBShippingGroupFormhandler.handleUpdateHoldNPackInfo() ",
                                           exc);
                       }
                       this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                       ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
                   }
               }
               return true;
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
    
    public void preUpdateHoldNPackInfo(final DynamoHttpServletRequest pRequest)
            throws ServletException, IOException {
    	logDebug("BBBShippingGroupFormHandler.preUpdateHoldNPackInfo(): START");
    	this.validatePackNHold(pRequest.getLocale(),pRequest);
    }
    
    public final void postUpdateHoldNPackInfo(final DynamoHttpServletRequest pRequest)
            throws ServletException, IOException {
    	
    	logDebug("BBBShippingGroupFormHandler.postUpdateHoldNPackInfo() : START");
    	
    }
    /** This handler method will validate shipping address and apply the shipping groups to the order.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @return a <code>boolean</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public boolean handleAddShipping(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	
    	final String contextPath = pRequest.getContextPath();
        String failureURL = null;
        if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                        && !this.getCheckoutProgressStates().getFailureURL()
                                        .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            failureURL = contextPath + this.getCheckoutProgressStates().getFailureURL();
        }

        String successURL = failureURL;

        Transaction tr = null;
        final String myHandleMethod = "BBBShippingGroupFormHandler.handleAddShipping";
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
                        //RMT-41747
                        String channel = BBBUtility.getChannel();
                        String isFromSPC = pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED);
                        if(null == isFromSPC) isFromSPC= BBBCoreConstants.BLANK;
                        if(BBBCoreConstants.TRUE.equalsIgnoreCase(isFromSPC) && (BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel) || BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel))){
                        	if(!BBBUtility.isListEmpty(getOrder().getShippingGroups())){
                        		if(null != this.getOrder().getShippingGroups().get(0) && this.getOrder().getShippingGroups().get(0) instanceof BBBHardGoodShippingGroup){
                        				BBBHardGoodShippingGroup shippingGroup = (BBBHardGoodShippingGroup)this.getOrder().getShippingGroups().get(0);
                        				this.getBBBShippingInfoBean().setGiftMessage(shippingGroup.getGiftMessage());
                            			this.getBBBShippingInfoBean().setGiftWrap(shippingGroup.isContainsGiftWrap());
                            			this.getBBBShippingInfoBean().setGiftingFlag(shippingGroup.getGiftWrapInd());
                        		}
                        	}
                        	if (this.isOrderContainRestrictedSKU(this.getOrder(),null,isFromSPC)) {
                                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                                BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU, LOCALE_EN, null),
                                                BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU));
                                if (!BBBUtility.isEmpty(failureURL)) {
                                    failureURL = failureURL + SHIPPING_RESTRICTION_TRUE;
                                }
                                return this.checkFormRedirect(failureURL, failureURL, pRequest, pResponse);
                            }
                        }
                        this.addShipping(pRequest, pResponse);

                        if (this.getFormError()) {
                            if (this.isLoggingDebug()) {
                                this.logDebug("Redirecting due to form error in moveToBilling");
                            }

                            return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
                        }

                        if (this.isOrderContainRestrictedSKU(this.getOrder(),null,isFromSPC)) {
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

                    this.postAddShipping(pRequest, pResponse);
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
                    	
                    }
                    else {
                    boolean porchValidation= validatePorchServiceSingleShipping();
                    if(porchValidation){
	                     if (!BBBUtility.isEmpty(failureURL)) {
	                         failureURL = failureURL + PORCH_SHIPPING_RESTRICTION_TRUE;
	                     }
	                     String channel = BBBUtility.getChannel();
		     				if (BBBCoreConstants.MOBILEWEB.equals(channel) ){
		     					pRequest.setParameter("porchMobileShippingAddressError", "true");
		     				}
		     				else {	                     
	                     this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                 BBBCoreConstants.PORCH_SERVICE_NOT_AVAILABLE, LOCALE_EN, null),
                                 BBBCoreConstants.PORCH_SERVICE_NOT_AVAILABLE));
		     				}
	                     return this.checkFormRedirect(failureURL, failureURL, pRequest, pResponse);
                    }
                    }
                    
                    // Adding new code to set PackAndHoldFlag to order
                	if(this.packAndHold && !(StringUtils.isEmpty(this.getPackNHoldDate()))){
                		((BBBOrderImpl)this.getOrder()).setPackAndHoldFlag(this.packAndHold);
                	}
                	else{
                		((BBBOrderImpl)this.getOrder()).setPackAndHoldFlag(false);
                	}
                    try {
                        this.getOrderManager().updateOrder(this.getOrder());
                        // If NO form errors are found, redirect to the success URL.
                        // If form errors are found, redirect to the error URL.
                        
                        // Added as Part of Story 83-N: Start
                        BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
                        if(order.isPayPalOrder()  && !this.getFormError()){
                        	this.logDebug("Order is of type Paypal");
                        	//getPayPalSessionBean().setPayPalShipAddValidated(true);
                        	this.getCheckoutProgressStates().setCurrentLevel(
                                    CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
                        	if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                                    && !this.getCheckoutProgressStates().getFailureURL()
                                                    .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
	                        	if(getPayPalSessionBean().isFromPayPalPreview()){
	                        		this.logDebug("Coming from preview page , so redirect to review page");
	                        		successURL = contextPath + this.getCheckoutProgressStates().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
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
		                                    CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
		                    if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
		                                    && !this.getCheckoutProgressStates().getFailureURL()
		                                                    .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
		                        successURL = contextPath + this.getCheckoutProgressStates().getFailureURL();
		                    }
		
		                    if (this.isCollegeAddress()) {
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

    
    
    
    /**
     * @param pResponse 
     * @param pRequest 
     * @return 
     * @throws IOException 
     * @throws ServletException 
	 * 
	 */
		private boolean validatePorchServiceSingleShipping() throws ServletException, IOException {
		List<CommerceItem> commerceItemList=(List<CommerceItem>) getOrder().getCommerceItems(); 
		final BBBAddress addressFromContainer = this.getAddressContainer().getAddressFromContainer(
                 this.getShipToAddressName());     
		 for(CommerceItem commerceItem:commerceItemList){
			 
				BaseCommerceItemImpl cItemimpl = (BaseCommerceItemImpl) commerceItem;
	    		if(cItemimpl.isPorchService() ){
	    			 	String sourceId =addressFromContainer.getSource();
	    			 	if(!StringUtils.isBlank(sourceId) && sourceId.contains("registry")){
	    				removePorchServiceFromCommerceItem(cItemimpl);
	    				this.addFormException(new DropletException(this.getMsgHandler().getErrMsg("err_onlyProductAddedToRegistry",
	   		   				 LOCALE_EN, null), "err_onlyProductAddedToRegistry"));
	    			 	}
	    			 	else{
	    			 		String commItemZipCode= addressFromContainer.getPostalCode();
	    			 		 String[] commItemShippingCode=commItemZipCode.split("-");
	 		    			RepositoryItem productItem = (RepositoryItem) commerceItem.getAuxiliaryData().getProductRef();
	 		    			List<String> porchServiceFamilycodes=getPorchServiceManager().getPorchServiceFamilyCodes(productItem.getRepositoryId(),null);
	 		    			Object responseVO = null;
	 		    			if(!StringUtils.isBlank(commItemShippingCode[0]) && !StringUtils.isBlank(porchServiceFamilycodes.get(0))){
	 							try {
	 								responseVO = getPorchServiceManager().invokeValidateZipCodeAPI(commItemShippingCode[0],porchServiceFamilycodes.get(0));
	 							} catch (BBBSystemException e) {
	 								if(isLoggingError()){
	 									logError("Error while invoking validateZipCode porch api "+e,e);
	 								}
	 							} catch (BBBBusinessException e) {
	 								if(isLoggingError()){
	 									logError("Error while invoking validateZipCode porch api "+e,e);
	 								}
	 							} 
	 		    			}
	 		    			 if(null==responseVO){
	 		    				 return true;
	 		    			 }
	 		    			
	    			 	}
	    			 
	    		}
		 }
		
		 
	 
		return false;
	}
	/**
	 * @param cItemimpl
	 * @param sourceId
	 */
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
	
	
    /** This method will check that order shipping group has any restricted sku or not.
     *
     * @param order
     * @return */
    protected boolean isOrderContainRestrictedSKU(final Order order,final Address bbbAddressImpl, String isFromSPC) {
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
                     String zipCode = BBBCoreConstants.BLANK;
                     if(BBBCoreConstants.TRUE.equalsIgnoreCase(isFromSPC)){
                     	final BBBAddress addressFromContainer = this.getAddressContainer().getAddressFromContainer(
                                 this.getShipToAddressName());
                     	zipCode = addressFromContainer.getPostalCode();
                     }else{
                    	 zipCode = ((BBBHardGoodShippingGroup) sg).getShippingAddress().getPostalCode();
                     }
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
    public void preAddShipping(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {
    	
        if (this.isSingleShippingGroupCheckout()) {
        	 
            this.validatePackNHold(pRequest.getLocale(),pRequest);
            if (this.getFormError()) {
                return;
            }
            
            if(this.getPoBoxStatus()!=null && this.getPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
            	((BBBAddress)this.getAddress()).setQasValidated(true);
            if(this.getPoBoxFlag()!=null && this.getPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
            	((BBBAddress)this.getAddress()).setPoBoxAddress(true);


        	BBBAddress address = (BBBAddress) this.getAddress();
        	// BBBH-3532 Story Changes - Same Day Delivery SPC
        	if (Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED))) {
        		switchSDDToStandard(address);
        	}
            if ("true".equals(this.getShipToAddressName()) || "college".equals(this.getShipToAddressName())
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
                this.getAddressContainer().addAddressToContainer(this.getShipToAddressName(), nAddress);

            } else {
                this.setNewAddress(false);
                
                // profile address sent to QAS during single ship checkout
                if (this.isQasOnProfileAddress())
                	updateProfileAddress(pRequest, pResponse);
                
            }

            this.preShipToAddress(pRequest, pResponse);

        } else {
            return;//
            // preShipToMultipleAddress(pRequest, pResponse);
        }

        // preSetupGiftShippingDetails(pRequest, pResponse);
    }

    /**
     * Switch sdd to standard.
     *
     * @param address the address
     */
    private void switchSDDToStandard(BBBAddress address) {
    	String currentPostalCode = BBBCoreConstants.BLANK;
    	String currentShippingMethod = BBBCoreConstants.BLANK;
    	if (this.isLoggingDebug()) {
			this.logDebug("BBBShippingGroupFormHandler | switchSDDToStandard starts");
		}
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
    					&& (!currentPostalCode
    							.equalsIgnoreCase(address
    									.getPostalCode()) && currentShippingMethod
    									.equalsIgnoreCase(BBBCoreConstants.SDD))
    									&& getShippingOption()
    									.equalsIgnoreCase(BBBCoreConstants.SDD)) {
    				this.setShippingOption((String) this
    						.getShippingMethodMap().getProperty("standard"));
    				this.setShippingGroupChanged(true);
    				if (this.isLoggingDebug()) {
    					this.logDebug("BBBShippingGroupFormHandler | switchSDDToStandard | "
    							+ "Address Switched to standard in case SDD was preselected and address is selected");
    				}

    			}
    		}
    	}
    }

    
    /** This method updates profile address with QAS results
    *
    * @param pRequest a <code>DynamoHttpServletRequest</code> value
    * @param pResponse a <code>DynamoHttpServletResponse</code> value
    * **/
    public void updateProfileAddress(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
        	
        	//get profile address from container
        	Address profileAddress = (Address) this.getAddressContainer().getAddressFromContainer(getShipToAddressName());
        	//get new address returned from QAS
        	final Address newAddress = this.getAddress();
        	
        	//if user clicked on "use address as entered"... no need to update anything
        	if (BBBAddressTools.compare(profileAddress, newAddress)) {
               	// force the flag to avoid further QAS on this address in the same session
        		((BBBAddress)profileAddress).setQasValidated(true);
            	return;
        	}
        	                       
            //replace profile address with new address in container
            this.getAddressContainer().addAddressToContainer(getShipToAddressName(), (BBBAddress)newAddress);
            
            String addressId = getShipToAddressName();
            if (this.getShipToAddressName().startsWith(PROFILE_TEXT)) {
            	// extract address id for update
            	addressId=this.getShipToAddressName().substring(7);
            }
            
        	// update address in profile
        	try {	
        		this.getCheckoutManager()
        			.getProfileAddressTool()
        			.updateAddressToProfile((Profile)this.getProfile(), 
        									(BBBAddressVO)this.getAddress(), 
        									addressId, this.getOrder().getSiteId());               	
        	}catch (final BBBSystemException e) {
        		if (this.isLoggingError()) {
        			this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SAVING_TO_PROFILE), e);
        		}
        	}
        	
        	// force the flag to avoid further QAS on this address in the same session
        	((BBBAddress)newAddress).setQasValidated(true);
    	
    }

    @SuppressWarnings("unchecked")
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
                String siteId =  getCurrentSiteID();
    			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) {
    				String channel = BBBUtility.getChannel();
    				if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
    					formatter = new SimpleDateFormat(DATE_FORMAT, Locale.CANADA);
    				} else {
    					formatter = new SimpleDateFormat(DATE_FORMAT_CANADA, pLocale);
					}
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
                                this.addFormException((new DropletException(this.getMsgHandler().getErrMsg(
                                		INVALID_ENDDATE_PACK_HOLD, LOCALE_EN, null) + this.getCatalogUtil().packNHoldEndDate(siteId), INVALID_ENDDATE_PACK_HOLD)));
                                return;
                            }
                            else{
                             		   hardGoodShippingGroup.setShipOnDate(formatter.parse(this.getPackNHoldDate()));  
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
	/**
	 * @return
	 */
	protected String getCurrentSiteID() {
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
    public void
                    postAddShipping(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {

        if (this.getFormError()) {
            return;
        }

        String shippingMethod = this.getShippingOption();
        
        //1. if shipping method is sdd then get store id from session.
        //2. After getting the store id from session set it at hardgood shipping group level
		if (!StringUtils.isBlank(shippingMethod) && shippingMethod.equals("SDD")) {

			BBBHardGoodShippingGroup sddShippingGroup = (BBBHardGoodShippingGroup) this.getOrder().getShippingGroups()
					.get(0);
			String storeId = this.getSessionBean().getSddStoreId();
			if (!StringUtils.isBlank(storeId)) {

				sddShippingGroup.setSddStoreId(storeId);
			}

		}
        
		if (!Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED))) {
			this.repriceOrder(pRequest, pResponse);
		}
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
    protected void preShipToAddress(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        if (this.isAnyHardgoodShippingGroups() && this.isSingleShippingGroupCheckout()) {
            // if we are in a single shipping group checkout then the user has the option of
            // specifying a new shipping address on the form.

            final String shippingMethod = this.getShippingOption();
            final String addressName = this.getShipToAddressName();

            if (StringUtils.isEmpty(addressName)) {
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(NO_SHIPPING_ADDRESS_SELECTED,
                                LOCALE_EN, null), NO_SHIPPING_ADDRESS_SELECTED));
                return;
            }
            Address address = (Address) this.getAddressContainer().getAddressFromContainer(addressName);
            if (address == null) {
                if (this.getAddressContainer().getAddressMap().containsKey(PROFILE_TEXT + addressName)) {
                    address = (Address) this.getAddressContainer().getAddressFromContainer(PROFILE_TEXT + addressName);
                }
            }
            // BBBH-3308
            updatePersonalInfoInAddress(address);
            // BBBH-3308

            if (this.isCollegeAddress()) {

                final BBBAddressVO bbbAddress = (BBBAddressVO) address;
                final String checkoutfirstName = pRequest.getParameter("checkoutfirstName");
                final String checkoutlastName = pRequest.getParameter("checkoutlastName");
                bbbAddress.setAddress1(pRequest.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS1));
                bbbAddress.setAddress2(pRequest.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS2));
                bbbAddress.setAddress3(pRequest.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS3));
                bbbAddress.setCity(pRequest.getParameter("cityName"));
                bbbAddress.setFirstName(checkoutfirstName);
            	bbbAddress.setLastName(checkoutlastName);
                String siteId = getCurrentSiteID();
                if(siteId.equalsIgnoreCase("BedBathCanada")){
                	bbbAddress.setPostalCode(pRequest.getParameter("zipCA"));
                }
                else{
                	bbbAddress.setPostalCode(pRequest.getParameter("zipUS"));
            }
                bbbAddress.setState(pRequest.getParameter("stateName"));
                bbbAddress.setCompanyName(pRequest.getParameter("company"));
                bbbAddress.setIsWebLinkOrderAddr(true);               
            }

			if (Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED))) {
				// Validate state for shipping address
				this.validateStateForShipping(address, pRequest, pResponse);
			}
            // The shipping group for the selected address should already be in the map.

            this.validateShippingAddress(address, pRequest, pResponse);

            if (!StringUtils.isBlank(shippingMethod) && !shippingMethod.equals("SDD")) {
    			 // Make sure user isn't trying to Express ship to AK, etc
                this.validateShippingMethod(address, shippingMethod, pRequest, pResponse);
    		}
           

            this.checkItemShipByMethod(address, shippingMethod, pRequest, pResponse);

        } // end if single sg checkout
    }

	/**
	 * This method used to update First Name & Last Name in address object when
	 * primary registrant address selected to ship the product and it is not available in address.
	 * 
	 * @param address
	 */
	private void updatePersonalInfoInAddress(Address address) {
		logDebug("BBBShippingGroupFormHandler.updatePersonalInfoInAddress start...");
		String addressInfo = this.getShipToAddressName();
		BBBAddressImpl bbbAddress = null;
		if (address != null && addressInfo != null && addressInfo.contains(REGISTRY_TEXT) && address instanceof BBBAddressImpl && (address.getFirstName() == null || address.getLastName() == null)) {
			bbbAddress = (BBBAddressImpl) address;
			final String[] regInfo = bbbAddress.getRegistryInfo() != null ? bbbAddress.getRegistryInfo().split(" ") : null;
			if (regInfo != null && regInfo.length >= 2) {
				address.setFirstName(regInfo[0]);
				address.setLastName(regInfo[1]);
			} else {
				logError("ERROR: First Name and Last Name are missing in Address object and can not be updated.");
			}
		}
		logDebug("BBBShippingGroupFormHandler.updatePersonalInfoInAddress end.");
	}

	/**
	 * This method is used to validate state name/code available in shipping
	 * address
	 * 
	 * @param address
	 * @param pRequest
	 * @param pResponse
	 */
	private void validateStateForShipping(Address address,
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {
		this.logDebug("Begin BBBShippingGroupFormHandler.validateStateForShipping - Validate state for shipping address");
		try {
			final List<StateVO> stateList = this.getCatalogUtil()
					.getStateList();
			final String shippingState = address.getState();
			boolean isValidStateForShipping = false;
			if (!StringUtils.isEmpty(shippingState)) {
				for (StateVO stateVO : stateList) {
					if (stateVO.getStateCode().equalsIgnoreCase(shippingState)
							|| stateVO.getStateName().equalsIgnoreCase(
									shippingState)) {
						isValidStateForShipping = true;
					}
				}
			}
			if (!isValidStateForShipping) {
				this.logDebug("Invalid state for shipping - " + shippingState);
				this.addFormException(new DropletException(this.getMsgHandler()
						.getErrMsg(ERROR_INCORRECT_STATE, LOCALE_EN, null),
						ERROR_INCORRECT_STATE));
			}
		} catch (BBBBusinessException be) {
			this.logError(LogMessageFormatter.formatMessage(null,
					"ERROR:  getStates call has failed for site id: "
							+ this.siteId), be);
			this.addFormException(new DropletException(this.getMsgHandler()
					.getErrMsg(ERROR_INCORRECT_STATE, LOCALE_EN, null),
					ERROR_INCORRECT_STATE));
		} catch (BBBSystemException se) {
			this.logError(LogMessageFormatter.formatMessage(null,
					"ERROR:  getStates call has failed for site id: "
							+ this.siteId), se);
			this.addFormException(new DropletException(this.getMsgHandler()
					.getErrMsg(ERROR_INCORRECT_STATE, LOCALE_EN, null),
					ERROR_INCORRECT_STATE));
		}

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

            this.getShippingHelper()
                            .getPricingTools()
                            .priceOrderSubtotalShipping(this.getOrder(), this.getUserPricingModels(),
                                            this.getUserLocale(pRequest, pResponse), this.getProfile(), new HashMap());

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
    protected void postShipToNewAddress(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        try {
            String name;
            if (this.isSingleShippingGroupCheckout() && this.getSaveShippingAddress()
                            && !this.getProfile().isTransient() 
                            && !this.getShipToAddressName().contains(REGISTRY_TEXT)) {
            	
            	//profile address sent to QAS during single ship checkout
            	if (this.isQasOnProfileAddress())
            		return;

            	final Map addresses = (Map) this.getProfile().getPropertyValue("secondaryAddresses");
	            if (BBBUtility.isMapNullOrEmpty(addresses)) {
	
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
	            
            	
            } else {
                name = Long.toString(new Date().getTime());
            }
            	updateEmailAndPhoneNumberInBillingAddress(pRequest, pResponse);

            final BBBHardGoodShippingGroup defaultHGSG = (BBBHardGoodShippingGroup) this.getShippingGroupManager()
                            .getFirstNonGiftHardgoodShippingGroupWithRels(this.getOrder());
            defaultHGSG.setSourceId(name);
        } catch (final Exception e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, "Error while saving address"), e);
            }
        }
    }
    
	/**
	 * If email & phone number present in request parameter then save email &
	 * phone number in billing address.
	 * 
	 * @param pRequest
	 * @param pResponse
	 */
	private void updateEmailAndPhoneNumberInBillingAddress(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		this.logDebug("Begin BBBShippingGroupFormHandler.updateEmailAndPhoneNumberInBillingAddress");
		BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
		if (!StringUtils.isEmpty(this.getSaveEmail())) {
			//Start change for BBBP-3627 : creating shallow profile for transient user
			 if (getProfile().isTransient()) {
				 try {
		        		BBBRepositoryContactInfo billingAddress = order.getBillingAddress();
		        		String postalCode = billingAddress.getPostalCode();
				         if(!BBBUtility.isEmpty(postalCode)){
				        	 postalCode = postalCode.replaceAll(BBBCoreConstants.WHITE_SPACE, BBBCoreConstants.BLANK);
				         }	
		        		getBbbGetCouponsManager().createWalletMobile(getSaveEmail(), getSavePhone(), 
		        				billingAddress.getFirstName(), billingAddress.getLastName(), billingAddress.getAddress1(), 
		        				billingAddress.getCity(), billingAddress.getState(), postalCode);
		        	} catch (BBBSystemException se) {
		        		if (isLoggingError()) {
		        			logError("BBBBillingAddressFormHandler.saveBillingAddress exception" + se);
		        		}
		        	} catch (BBBBusinessException be) {
		        		if (isLoggingError()) {
		        			logError("BBBBillingAddressFormHandler.saveBillingAddress exception" + be);
		        		}
		        	}
		        }
			 //end change for BBBP-3627
			order.getBillingAddress()
					.setEmail(this.getSaveEmail());
		} 
		if (!StringUtils.isEmpty(this.getSavePhone())) {
			order.getBillingAddress()
					.setPhoneNumber(this.getSavePhone());
		}
		this.logDebug("End BBBShippingGroupFormHandler.updateEmailAndPhoneNumberInBillingAddress");
	}

    /** Validates the new address. Check for required properties, and make sure street address doesn't include PO Box,
     * AFO/FPO.
     *
     * @param pAddress - address
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    protected void validateShippingAddress(final Address pAddress, final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws IOException, ServletException {

        final ContactInfo shippingAddress = (ContactInfo) pAddress;
//			removing PO box check as we are accepting PO box address now.
      //ship to po box changes
		List<String> shiptoPOBoxOn;
		boolean shiptoPOFlag =false;
		try {
			shiptoPOBoxOn = catalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
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
        	errorList = this.getShippingHelper().checkForRequiredAddressProperties(shippingAddress, pRequest);
        }
        
        this.addAddressValidationFormError(errorList, pRequest, pResponse);  
        if (!this.getFormError()) {
            if (null != this.getCisiIndex() && Integer.parseInt(this.getCisiIndex()) >= 0 && Integer.parseInt(this.getCisiIndex()) < this.getCisiItems().size()) {
            	final int index = Integer.parseInt(this.getCisiIndex());
	            if (!this.getCheckoutManager().canItemShipToCiSiIndexAddress(this.getOrder().getSiteId(),
                        (CommerceItemShippingInfo)this.getCisiItems().get(index), shippingAddress)) {
	            	if (!((BBBAddress)pAddress).getIsNonPOBoxAddress()) 
		            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_STATE,
		                            LOCALE_EN, null), ERROR_INCORRECT_STATE));
	            	else
	            		this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_POBOX,
                            	LOCALE_EN, null), ERROR_INCORRECT_POBOX));
		            this.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
		        }
            }else{
	            if (!this.getCheckoutManager().canItemShipToAddress(this.getOrder().getSiteId(),
	                            this.getOrder().getCommerceItems(), shippingAddress)) {
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

    protected void validateShippingAddressForMultipleShipping(final Address pAddress, final String skuId, final boolean ltlFlag, 
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                    throws IOException, ServletException {

        if (pAddress == null) {
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(GENERIC_ERROR_TRY_LATER,
                            LOCALE_EN, null), GENERIC_ERROR_TRY_LATER));
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_INCORRECT_ADDRESS)
                                + " : Input Address is NULL");
            }
        }

        final ContactInfo shippingAddress = (ContactInfo) pAddress;

      //ship to po box changes
		List<String> shiptoPOBoxOn;
		boolean shiptoPOFlag =false;
		try {
			shiptoPOBoxOn = catalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
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
		
        List errorList = new ArrayList<String>();
        if(ltlFlag)
        {
        	final BBBAddress ltlShippingAddress = (BBBAddress)pAddress;
        	errorList= this.getShippingHelper().checkForRequiredLTLAddressProperties(ltlShippingAddress, pRequest);
        }else{
        	errorList= this.getShippingHelper().checkForRequiredAddressProperties(shippingAddress, pRequest);	
        }
        
        this.addAddressValidationFormError(errorList, pRequest, pResponse);
	        if (!this.getFormError()) {
	        	final StringBuilder errorMsg = new StringBuilder();
	            if (this.getCheckoutManager().checkItemShipToAddress(this.getOrder().getSiteId(), skuId, shippingAddress)) {
	            	
	            	if (!((BBBAddress)pAddress).getIsNonPOBoxAddress()) {
	
	            		errorMsg.append(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_STATE, LOCALE_EN, null))
	                    .append(" : ").append(shippingAddress.getAddress1()).append(", ")
	                                .append(shippingAddress.getCity()).append(", ").append(shippingAddress.getState())
	                                .append(", ").append(shippingAddress.getCountry());
	              
	                    this.addFormException(new DropletException(errorMsg.toString(), ERROR_INCORRECT_STATE));
	                    if (this.isLoggingError()) 
	                        this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_INCORRECT_STATE));
	            		
	                }
	            	else {
	            		
	            		errorMsg.append(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_POBOX, LOCALE_EN, null))
	                    .append(this.getMsgHandler().getErrMsg(ERROR_ENTER_ALTERNATE_ADDRESS, LOCALE_EN, null));
	            		
	            		this.addFormException(new DropletException(errorMsg.toString(), ERROR_INCORRECT_POBOX));
	                    if (this.isLoggingError()) 
	                        this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_INCORRECT_POBOX));
	            	
	            }
	            		
	        }else if((((BBBAddress)shippingAddress).isPoBoxAddress() || this.getCheckoutManager().isPostOfficeBoxAddress(shippingAddress))&& ltlFlag){
	    		errorMsg.append(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_POBOX, LOCALE_EN, null))
	            .append(this.getMsgHandler().getErrMsg(ERROR_ENTER_ALTERNATE_ADDRESS, LOCALE_EN, null));
	    		
	    		this.addFormException(new DropletException(errorMsg.toString(), ERROR_INCORRECT_POBOX));
	            if (this.isLoggingError()) 
	                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_INCORRECT_POBOX));
	    		
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
    protected void validateShippingMethod(final Address pAddress, final String pShippingMethod,
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

    /** This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple
     * CommerceItemShippingInfo objects. The number of new CommerceItemShippingInfo objects is equal to the quantity in
     * the existing CommerceItemShippingInfo object. .
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @return a <code>boolean</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */

    public final boolean handleShipToMultiplePeople(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        String redirectURL = "";
        StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
	   if (StringUtils.isNotEmpty(getFromPage())) {
		   successURL
			.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			setShipToMultiplePeopleSuccessURL(successURL.toString());
			setShipToMultiplePeopleErrorURL(errorURL.toString());
		}
        if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                        && !this.getCheckoutProgressStates().getFailureURL()
                                        .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
        }
        Transaction tr = null;
        this.setClearContainer(false);

        final String myHandleMethod = "BBBShippingGroupFormHandler.handleShipToMultiplePeople";
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();

        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_MULTIPLE_SHIPPING, myHandleMethod);
            try {
                tr = this.ensureTransaction();

                if (!this.checkFormRedirect(null, this.getShipToMultiplePeopleErrorURL(), pRequest, pResponse)) {
                    return false;
                }

                synchronized (this.getOrder()) {
                    try {
                        this.preShipToMultiplePeople(pRequest, pResponse);

                        if (this.getFormError()) {
                            if (this.isLoggingDebug()) {
                                this.logDebug("Redirecting due to form error in preShipToMultiplePeople.");
                            }
                            return this.checkFormRedirect(null, redirectURL, pRequest, pResponse);
                        }

                        this.shipToMultiplePeople(pRequest, pResponse);

                        if (this.getFormError()) {
                            if (this.isLoggingDebug()) {
                                this.logDebug("Redirecting due to form error in addNewAddress");
                            }

                            return this.checkFormRedirect(null, this.getShipToMultiplePeopleErrorURL(), pRequest,
                                            pResponse);
                        }

                        this.postShipToMultiplePeople(pRequest, pResponse);

                        if (this.getFormError()) {
                            if (this.isLoggingDebug()) {
                                this.logDebug("Redirecting due to form error in postShipToMultiplePeople");
                            }

                            return this.checkFormRedirect(null, this.getShipToMultiplePeopleErrorURL(), pRequest,
                                            pResponse);
                        }
                    } catch (final Exception exc) {
                        if (this.isLoggingError()) {
                            this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SHIP_TO_MULTIPLE));
                        }
                        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                        ERROR_SHIP_TO_MULTIPLE, LOCALE_EN, null), ERROR_SHIP_TO_MULTIPLE));

                    }

                    try {
                        this.getOrderManager().updateOrder(this.getOrder());
                    } catch (final Exception exc) {
                        if (this.isLoggingError()) {
                            this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SHIP_TO_MULTIPLE));
                        }
                        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                        ERROR_SHIP_TO_MULTIPLE, LOCALE_EN, null), ERROR_SHIP_TO_MULTIPLE));
                    }
                } // synchronized

                this.getCheckoutProgressStates().setCurrentLevel(
                                CheckoutProgressStates.DEFAULT_STATES.SHIPPING_MULTIPLE.toString());
                if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                                && !this.getCheckoutProgressStates().getFailureURL()
                                                .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
                    redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
                }
                // If NO form errors are found, redirect to the success URL.
                // If form errors are found, redirect to the error URL.
                return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
            } finally {
                if (tr != null) {
                    this.commitTransaction(tr);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_MULTIPLE_SHIPPING, myHandleMethod);
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
            }
        }
        return false;
    }

    /** This method place holder for the preShipToMultiplePeople
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public void preShipToMultiplePeople(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	//do nothing
    }

    /** This method place holder for the postShipToMultiplePeople
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public void postShipToMultiplePeople(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	//do nothing
    }

    /** Splits the CommerceItemShippingInfo into multiple items
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public void shipToMultiplePeople(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        CommerceItemShippingInfo currentCISI = null;
        try {
            final int index = Integer.parseInt(this.getCisiIndex());
            if (index >= 0) {
                final List commerceItemShippingInfoList = this.getCisiItems();
                currentCISI = (CommerceItemShippingInfo) commerceItemShippingInfoList.get(index);

                if (StringUtils.isEmpty(currentCISI.getShippingGroupName())) {
                    currentCISI.setShippingGroupName(pRequest.getParameter("cisiShipGroupName"));
                }

                if ((currentCISI.getQuantity() > 1)
                                && (this.getMultiShippingAddrCont().getAddressFromContainer(
                                                currentCISI.getShippingGroupName()) != null)) {
                    final ShippingGroup shippingGroup = this.getShippingGroupContainerService().getShippingGroup(
                                    pRequest.getParameter("cisiShipGroupName"));
                    this.getShippingGroupContainerService().addShippingGroup(currentCISI.getShippingGroupName(),
                                    shippingGroup);

                }

                this.getManager().splitCommerceItemShippingInfo(currentCISI, this.getOrder(),
                                this.getShippingGroupContainerService());
				if(((BBBCommerceItem)currentCISI.getCommerceItem()).isLtlItem()){
					this.repriceOrder(pRequest, pResponse);
				}

            }

        } catch (final NumberFormatException e) {

            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SHIP_TO_MULTIPLE), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIP_TO_MULTIPLE,
                            LOCALE_EN, null), ERROR_SHIP_TO_MULTIPLE));

        } catch (final CommerceException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SHIP_TO_MULTIPLE), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIP_TO_MULTIPLE,
                            LOCALE_EN, null), ERROR_SHIP_TO_MULTIPLE));

        } catch (BBBSystemException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SHIP_TO_MULTIPLE), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIP_TO_MULTIPLE,
                            LOCALE_EN, null), ERROR_SHIP_TO_MULTIPLE));

        } catch (BBBBusinessException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SHIP_TO_MULTIPLE), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIP_TO_MULTIPLE,
                            LOCALE_EN, null), ERROR_SHIP_TO_MULTIPLE));

        }

    }

    public final boolean handleSaveNewAddress(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final List<CommerceItemShippingInfo> commerceItemShippingInfos = this.getCisiItems();
        String redirectURL = "";
        for (final CommerceItemShippingInfo cisiObj : commerceItemShippingInfos) {
            if (StringUtils.isEmpty(cisiObj.getShippingGroupName())) {
                cisiObj.setShippingGroupName(this.getShippingGroupManager()
                                .getFirstNonGiftHardgoodShippingGroupWithRels(this.getOrder()).getId());
            }
        }
        if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                        && !this.getCheckoutProgressStates().getFailureURL()
                                        .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
        }
        
        if(isAjaxAddressFilled()){
        	
        	String newAddressKey = this.getShippingAddressContainer().getNewAddressKey();
        	BBBAddress newAddress = this.getShippingAddressContainer().getNewAddressMap().get(newAddressKey);
        	
        	if(!BBBAddressTools.duplicateFound(this.getShippingAddressContainer().getAddressMap(), newAddress)) {
        		
        		this.getShippingAddressContainer().addAddressToContainer(newAddressKey, newAddress);
            	this.handleMultipleShipping(pRequest, pResponse);
        	}
        }
        this.getRepeatingRequestMonitor();
        this.setClearContainer(false);

        return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
    }
    
    /** This handler method will This method
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @return a <code>boolean</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */

    public boolean handleMultipleShipping(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final String myHandleMethod = "BBBShippingGroupFormHandler.handleMultipleShipping";
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
        boolean ajaxCall = false;
        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_MULTIPLE_SHIPPING, myHandleMethod);

            Transaction tr = null;
            String redirectURL = "";
            if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                            && !this.getCheckoutProgressStates().getFailureURL()
                                            .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
                redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
            }
            final int cisiItemsSizeInitial = this.getCisiItems().size();
            if(!isUpdateOrderSummaryAjax() || isAjaxAddressFilled()) {
	            try {
	                tr = this.ensureTransaction();
	
	                synchronized (this.getOrder()) {
	                    this.preMultipleShipping(pRequest, pResponse);
	
	                    if (this.getFormError()) {
	                        if (this.isLoggingDebug()) {
	                            this.logDebug("Redirecting due to form error in preMultipleShipping.");
	                        }
	                        return this.checkFormRedirect(null, redirectURL, pRequest, pResponse);
	                    }
	
	                    this.multipleShipping(pRequest, pResponse);
	                    this.postMultipleShipping(pRequest, pResponse);
	                    
	                    this.getOrderManager().updateOrder(this.getOrder());
	                    this.repriceOrder(pRequest, pResponse);
						if(isRemovePorchService()){
                    	 setRemovePorchService(false);
                         removePorchServiceFromMultiShipping(getOrder());	
	                    } 
	                    else {
		                    boolean isPorchRestrictedArea= validatePorchServiceMultiShipping();
		                    if(isPorchRestrictedArea){	                    	
		 	                     if (!BBBUtility.isEmpty(redirectURL)) {
		 	                    	 redirectURL = redirectURL + PORCH_MULTI_SHIPPING_RESTRICTION_TRUE;
		 	                     }
		 	                    String channel = BBBUtility.getChannel();
			     				if (BBBCoreConstants.MOBILEWEB.equals(channel) ){
			     					pRequest.setParameter("fromMultiShippingPage", "true");
			     				}
			     				else {/*
		 	                    this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
	                                    BBBCoreConstants.PORCH_SERVICE_NOT_AVAILABLE, LOCALE_EN, null),
	                                    BBBCoreConstants.PORCH_SERVICE_NOT_AVAILABLE));
			     				*/}
		 	                     return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
		                       }
	                    }
	                    if (this.isOrderContainRestrictedSKU(this.getOrder(),null, BBBCoreConstants.BLANK)) {
	                        // Redirect request to error url
	                        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
	                                        BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU, LOCALE_EN, null),
	                                        BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU));
	                        if (!BBBUtility.isEmpty(redirectURL)) {
	                            redirectURL = redirectURL + MULTI_SHIPPING_RESTRICTION_TRUE;
	                        }
	                        return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
	                    }
	
	                }
	            } catch (final Exception e) {
	                if (this.isLoggingError()) {
	                    this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_MULTIPLE_SHIPPING), e);
	                }
	                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
	                                LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
	            } finally {
	                if (tr != null) {
	                    this.commitTransaction(tr);
	                }
	                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_MULTIPLE_SHIPPING, myHandleMethod);
	                if (rrm != null) {
	                    rrm.removeRequestEntry(myHandleMethod);
	                }
	            }
            }
            if (this.getFormError()) {
                if (this.isLoggingDebug()) {
                    this.logDebug("Redirecting due to form error in postMultipleShipping." + this.getFormExceptions());
                }

                return this.checkFormRedirect(null, redirectURL, pRequest, pResponse);
            }

            // If NO form errors are found, redirect to the success URL.
            // If form errors are found, redirect to the error URL.
            // Send to Gifting page if Order Includes Gifts
            if (this.isOrderIncludesGifts() && !BBBCheckoutConstants.BOPUS_ONLY.equalsIgnoreCase(((BBBOrderImpl) this.getOrder())
                    .getOnlineBopusItemsStatusInOrder())) {
                this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.GIFT.toString());
                if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
                                && !this.getCheckoutProgressStates().getFailureURL()
                                                .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
                    redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
                }
                return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);

            }
            
            // Added as Part of Story 83-AZ: Start
            BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
            if(order.isPayPalOrder()  && !this.getFormError()){
            	this.logDebug("Order is of type Paypal");
            	//getPayPalSessionBean().setPayPalShipAddValidated(true);
            	this.getCheckoutProgressStates().setCurrentLevel(
                        CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
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
	            this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
	            if (!BBBUtility.isEmpty(this.getCheckoutProgressStates().getFailureURL())
	                            && !this.getCheckoutProgressStates().getFailureURL()
	                                            .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
	                redirectURL = pRequest.getContextPath() + this.getCheckoutProgressStates().getFailureURL();
	            }
            }
            
            if(BBBUtility.isNotEmpty(pRequest.getHeader(BBBCoreConstants.BBB_AJAX_REQUEST_PARAM))){
    			String isAjaxCall= pRequest.getHeader(BBBCoreConstants.BBB_AJAX_REQUEST_PARAM);
    			ajaxCall = Boolean.parseBoolean(isAjaxCall);
    			/*Sending this paramter to reload the page in case of difference found in no. of 
    			cisiItems before and after the form submit.*/
    			boolean reloadPage = false;
    			if(ajaxCall) {
    				int cisiItemsSizeFinal = this.getCisiItems().size();
    				if(cisiItemsSizeInitial != cisiItemsSizeFinal) {
    					reloadPage = true;
    				}
    				this.getCheckoutProgressStates().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_MULTIPLE.toString());
    				redirectURL = pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()) + RELOAD_MULTI_SHIPPING_PAGE + reloadPage;
    			}
            }
            
            
            return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
        }
		return false;
    }

    
    
    
    /**
     * @param order
     */
    private void removePorchServiceFromMultiShipping(Order order) {
		
    	List<ShippingGroup> shippingGroupsList =  order.getShippingGroups();
		for(ShippingGroup porchShipingGroup:shippingGroupsList){
			if(!(porchShipingGroup instanceof BBBStoreShippingGroup)){
				 BBBHardGoodShippingGroup shipinggroup = (BBBHardGoodShippingGroup) porchShipingGroup;
				  String zipCode= shipinggroup.getShippingAddress().getPostalCode();
				  List<BBBShippingGroupCommerceItemRelationship> porchCommerceItems = porchShipingGroup.getCommerceItemRelationships();
					for(BBBShippingGroupCommerceItemRelationship porchCommerceItem:porchCommerceItems){
						CommerceItem bbbItem = (CommerceItem) porchCommerceItem.getCommerceItem();
						if(((BaseCommerceItemImpl)bbbItem).isPorchService()){
						 String[] commItemShippingCode=zipCode.split("-");
			    		  RepositoryItem productItem = (RepositoryItem) bbbItem.getAuxiliaryData().getProductRef();
			    		  List<String> porchServiceFamilycodes=getPorchServiceManager().getPorchServiceFamilyCodes(productItem.getRepositoryId(),null);
			    		  Object responseVO = null;
			    		  if(!StringUtils.isBlank(commItemShippingCode[0]) && !StringUtils.isBlank(porchServiceFamilycodes.get(0))){
								try {
									responseVO = getPorchServiceManager().invokeValidateZipCodeAPI(commItemShippingCode[0],porchServiceFamilycodes.get(0));
									 if(null==responseVO){
										 removePorchServiceFromCommerceItem((BaseCommerceItemImpl) bbbItem);									
									 }
								} catch (BBBSystemException e) {
									if(isLoggingError()){
										logError("invoking validateZipCode porch api "+e,e);
									}
								} catch (BBBBusinessException e) {
									if(isLoggingError()){
										logError("invoking validateZipCode porch api "+e,e);
									}
								}
							}
					}
					}
				  
			}
		}
    	
		
	}
	/** This method place holder for the preMultipleShipping
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public void preMultipleShipping(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        final List cisiItemsList = this.getCisiItems();
        CommerceItemShippingInfo cisi = null;
        String shippingMethod = null;
        String sourceId = null;
        Address address = null;

        this.setClearContainer(false);

        // checking for session expire
        if ((null == this.getOrder()) || (this.getOrder().getCommerceItemCount() == 0) || this.getCisiItems().isEmpty()) {
            this.clearExceptionsFromRequest(pRequest);
            return;
        }

        for (int index = 0; index < cisiItemsList.size(); index++) {
            cisi = (CommerceItemShippingInfo) cisiItemsList.get(index);
            shippingMethod = cisi.getShippingMethod();
            sourceId = cisi.getShippingGroupName();

            final BBBCommerceItem commerceItem = (BBBCommerceItem) cisi.getCommerceItem();
            if(commerceItem.isLtlItem()) {
            commerceItem.setShipMethodUnsupported(false);
            commerceItem.setLtlShipMethod(shippingMethod);
            }
            if (this.getStoreSGMethodName().equalsIgnoreCase(shippingMethod)) {
                if (!StringUtils.isEmpty(commerceItem.getStoreId()) && StringUtils.isEmpty(cisi.getShippingGroupName())) {
                    try {
                        final ShippingGroup storePickupShippingGroup = this.getManager().getStorePickupShippingGroup(
                                        commerceItem.getStoreId(), this.getOrder());
                        cisi.setShippingGroupName(storePickupShippingGroup.getId());
                    } catch (final CommerceException e) {
                        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                        ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
                        return;
                    }
                }
                continue;
            }
            if (StringUtils.isEmpty(shippingMethod)) {
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_SHIP_METHOD,
                                LOCALE_EN, null), ERROR_INCORRECT_SHIP_METHOD));
                return;
            }
            if (shippingMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)){
            	((BBBCommerceItem)cisi.getCommerceItem()).setWhiteGloveAssembly(BBBCatalogConstants.FALSE);
            }
            else if (shippingMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
            	cisi.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
            	((BBBCommerceItem)cisi.getCommerceItem()).setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
            	((BBBCommerceItem)cisi.getCommerceItem()).setWhiteGloveAssembly(BBBCatalogConstants.TRUE);
            }
            else{
            	((BBBCommerceItem)cisi.getCommerceItem()).setWhiteGloveAssembly(BBBCatalogConstants.FALSE);
            }
            if (StringUtils.isEmpty(sourceId)) {
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_ADDRESS,
                                LOCALE_EN, null), ERROR_INCORRECT_ADDRESS));
                return;
            }
            address = (Address) this.getMultiShippingAddrCont().getAddressFromContainer(sourceId);
            
            // PSI7 LTL Registry : For LTL item order.xml needs alternate phone number, so 
            // populating alternate phone number with registry's additional mobile number, in 
            // case its empty then using registry's primary phone number.
            if(commerceItem.isLtlItem() && BBBUtility.isNotEmpty(commerceItem.getRegistryId())) {
            	BBBAddress bbbAddress = (BBBAddress)address;
            	if(BBBUtility.isNotEmpty(bbbAddress.getMobileNumber())) {
            		logDebug("LTL and Registry Item: Primary Phone number:"+bbbAddress.getPhoneNumber()+" Mobile Number:"+bbbAddress.getMobileNumber());
            		bbbAddress.setAlternatePhoneNumber(bbbAddress.getMobileNumber());
            	}
            }

            // The shipping group for the selected address should already be in
            // the map.
            this.validateShippingAddressForMultipleShipping(address, commerceItem.getCatalogRefId(), commerceItem.isLtlItem(), pRequest,pResponse);

            // Make sure user isn't trying to Express ship to AK, etc
            this.checkItemShipByMethod(address, cisi.getShippingMethod(), pRequest, pResponse);
        }

        // Only Gift card Items can not be shipped in Express Shipping
        this.checkGiftCardRestriction(pRequest, pResponse);
    }

    /** Clears exeptionsin case of sessin expire.
     *
     * @param pRequest */
    public void clearExceptionsFromRequest(final DynamoHttpServletRequest pRequest) {
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
    public final boolean checkItemShipByMethod(final Address address, final String shippingMethod,
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

    /** This method place holder for the postMultipleShipping
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public void postMultipleShipping(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        if (!this.getFormError()) {

            final List<BBBShippingGroup> shippingGroups = this.getOrder().getShippingGroups();

            for (final BBBShippingGroup shippingGroup : shippingGroups) {
                if ((shippingGroup != null) && (shippingGroup instanceof BBBHardGoodShippingGroup)) {
                    ((BBBHardGoodShippingGroup) shippingGroup).setShipOnDate(null);
                }
            }
        }

    }
    
    
	/**
	 * 
	 */
	private boolean validatePorchServiceMultiShipping() {
		 
		List<ShippingGroup> shippingGroupsList =  getOrder().getShippingGroups();
		for(ShippingGroup porchShipingGroup:shippingGroupsList){
			if(!(porchShipingGroup instanceof BBBStoreShippingGroup)){
				BBBHardGoodShippingGroup shipinggroup = (BBBHardGoodShippingGroup) porchShipingGroup ;
				List<BBBShippingGroupCommerceItemRelationship> porchCommerceItems = porchShipingGroup.getCommerceItemRelationships();
				for(BBBShippingGroupCommerceItemRelationship porchCommerceItem:porchCommerceItems){
					BaseCommerceItemImpl commerceItem = (BaseCommerceItemImpl) porchCommerceItem.getCommerceItem();
					 if(commerceItem.isPorchService()){
							 
						 String registryId = shipinggroup.getRegistryId();
						 if(!StringUtils.isBlank(registryId)){
							 removePorchServiceFromCommerceItem(commerceItem);
								/*this.addFormException(new DropletException(this.getMsgHandler().getErrMsg("err_onlyProductAddedToRegistry",
						   				 LOCALE_EN, null), "err_onlyProductAddedToRegistry"));*/
						 }
						 else{
							 String commItemZipCode= shipinggroup.getShippingAddress().getPostalCode();
							 String[] commItemShippingCode=commItemZipCode.split("-");
								RepositoryItem productItem = (RepositoryItem) commerceItem.getAuxiliaryData().getProductRef();
								List<String> porchServiceFamilycodes=getPorchServiceManager().getPorchServiceFamilyCodes(productItem.getRepositoryId(),null);
								Object responseVO = null;
								if(!StringUtils.isBlank(commItemShippingCode[0]) && !StringUtils.isBlank(porchServiceFamilycodes.get(0))){
									try {
										responseVO = getPorchServiceManager().invokeValidateZipCodeAPI(commItemShippingCode[0],porchServiceFamilycodes.get(0));
									} catch (BBBSystemException e) {
										if(isLoggingError()){
											logError("invoking validateZipCode porch api "+e,e);
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
			}
		}
	 	 
		return false;
	}

    /** @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    public void multipleShipping(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
        final List cisiItemsList = this.getCisiItems();
        CommerceItemShippingInfo cisi = null;
        BBBHardGoodShippingGroup hgsg = null;
        // Setting it to false. If there is exception in multipleShipping() then page will again initialize the
        // CISIContainer
        this.setClearContainer(true);
        try {

        	//Start: ltl specific code start
        	
        	List<ShippingGroup> sglist = this.getOrder().getShippingGroups();
        	
        	//create map of commerce items in a shipping group for which shipping method is empty.
            Map<String, List<String>> sgciMap = getSgCiMapForEmptyShipMethod(sglist);
            
            //create assembly and delivery items and CISI for commerce items present in sgciMap.
            if(!sgciMap.isEmpty()){
            	this.getManager().createDelAssItemForSGCIMap(sgciMap, this.getOrder(), this.getShippingGroupMapContainer(), this.getCommerceItemShippingInfoContainer());
            }
            
            //create list of commerce items for which assembly is false and for which assembly is true in all shipping group
            Map<String, List<String>> sgciMapForWgWithAss = new HashMap<String, List<String>>();
            Map<String, List<String>> sgciMapForWgWithoutAss = new HashMap<String, List<String>>();
            createSGCIMapForWithAndWithoutAss(sglist, sgciMapForWgWithAss, sgciMapForWgWithoutAss);
            
            //add remove assembly for commerceIdListWithAssembly and commerceIdListWithoutAssembly 
            this.getManager().addRemoveAssForCommerceItems(sgciMapForWgWithAss, sgciMapForWgWithoutAss,
            		this.getOrder(), this.getShippingGroupMapContainer(), this.getCommerceItemShippingInfoContainer());

            //create a list of ltl item map.
            /*List <Map<String, Map<String,String>>> sgLtlItemAssocList = new ArrayList<Map<String, Map<String,String>>>();
            createLTLItemAssocMap(sgLtlItemAssocList, sglist);*/
            
            //End: ltl specific code end
            
            for (int index = 0; index < cisiItemsList.size(); index++) {
                cisi = (CommerceItemShippingInfo) cisiItemsList.get(index);
                if (this.getStoreSGMethodName().equalsIgnoreCase(cisi.getShippingMethod())) {
                    continue;
                }
                hgsg = this.getManager().getMatchingSGForIdAndMethod(this.getOrder(), this.getMultiShippingAddrCont(),
                                cisi.getShippingGroupName(), cisi.getShippingMethod());
                final String inputShipGroupMethodKey = cisi.getShippingGroupName() + NICKNAME_SEPARATOR
                                + cisi.getShippingMethod();
                this.getShippingGroupContainerService().addShippingGroup(inputShipGroupMethodKey, hgsg);
                cisi.setShippingGroupName(inputShipGroupMethodKey);
                
                //Start: LTL code to set ShippingGroupName and ShippingMethod for CISI of associated delivery and assembly item if commerce item is ltl.
                if(((BBBCommerceItem)cisi.getCommerceItem()).isLtlItem()){
                	updateCISIForDelAndAssItems(cisi, inputShipGroupMethodKey);
                }
                //End: LTL code to set ShippingGroupName and ShippingMethod for CISI of associated delivery and assembly item if commerce item is ltl.
            }

            // splitShippingGroupsByMethod(pRequest, pResponse);

            // add registrant Email Here
            this.applyRegistrantEmailToSG();

            this.applyShippingGroups(pRequest, pResponse);
            
            //apply LTLItemAssocMap to newly created shipping groups for ltl items.
            
            this.getManager().applyLTLItemAssocMap(sglist, this.getOrder());
            
            // merge cisi when shipping method is same for ltl items
            sglist = this.getOrder().getShippingGroups();
            for(ShippingGroup sgAfterApply : sglist){
            	//create map of commerce items with same sku in a shipping group.
            	Map<String, List<String>> skuCommItemListMap = createSkuToCommItemsMap(sgAfterApply);
            	this.getManager().mergeCISIforSameLTLShipMethod(sgAfterApply, skuCommItemListMap, this.getOrder(),
            			this.getPurchaseProcessHelper(), this.getCommerceItemShippingInfoContainer());
            }
            
            this.getManager().removeEmptyShippingGroups(this.getOrder());

        } catch (final CommerceException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_MULTIPLE_SHIPPING), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                            LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
            throw new CommerceException(e);
        }  catch (BBBSystemException e) {
        	if (this.isLoggingError()) {
        		this.logError("System Exception occurred while checking if sku is ltl in multipleShipping method::BBBShippingGroupFormHandler",e);
        	}
        	this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                    LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
		} catch (BBBBusinessException e) {
			if (this.isLoggingError()) {
				this.logError("Business Exception occurred while checking if sku is ltl in multipleShipping method::BBBShippingGroupFormHandler",e);
			}
			this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                    LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
		} catch (RepositoryException e) {
			if (this.isLoggingError()) {
				logError("Error while creating Delivery Commerce Item",e);
			}
			this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                    LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
		}

    }


	/**
	 * This method creates map of commerce items with same sku in a shipping group.
	 * @param sgAfterApply
	 * @return Map <String, List<String>> skuCommItemListMap
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> createSkuToCommItemsMap(
			ShippingGroup sgAfterApply) throws BBBSystemException,
			BBBBusinessException {
		Map <String, List<String>> skuCommItemListMap = new HashMap<String, List<String>>();
		List<BBBShippingGroupCommerceItemRelationship> shippingGroupCIRList = sgAfterApply.getCommerceItemRelationships();
		
		if(!shippingGroupCIRList.isEmpty()){
			for(final BBBShippingGroupCommerceItemRelationship shippingGroupCIR : shippingGroupCIRList){
				if(shippingGroupCIR.getCommerceItem() instanceof BBBCommerceItem && ((BBBCommerceItem)shippingGroupCIR.getCommerceItem()).isLtlItem()){
					String registryId = ((BBBCommerceItem)shippingGroupCIR.getCommerceItem()).getRegistryId();
					String lwaFlag = ((BBBCommerceItem)shippingGroupCIR.getCommerceItem()).getWhiteGloveAssembly();
					String skuId = shippingGroupCIR.getCommerceItem().getCatalogRefId();
					if(lwaFlag !=null && lwaFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE) &&BBBUtility.isNotEmpty(registryId)&&
							skuCommItemListMap.keySet().contains(registryId+skuId+"Assembly")){
						skuCommItemListMap.get(registryId+skuId+"Assembly").add(shippingGroupCIR.getCommerceItem().getId());
					} else if(lwaFlag !=null &&  !lwaFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE)
							&& BBBUtility.isNotEmpty(registryId) &&
							skuCommItemListMap.keySet().contains(registryId+skuId)){
						skuCommItemListMap.get(registryId+skuId).add(shippingGroupCIR.getCommerceItem().getId());
					} else if(lwaFlag !=null && lwaFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE) 
							&& BBBUtility.isEmpty(registryId)&&
							skuCommItemListMap.keySet().contains(skuId+"Assembly")){
						skuCommItemListMap.get(skuId+"Assembly").add(shippingGroupCIR.getCommerceItem().getId());
					} else if(lwaFlag !=null &&  !lwaFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE) &&
							skuCommItemListMap.keySet().contains(skuId)
							&& BBBUtility.isEmpty(registryId)){
						skuCommItemListMap.get(skuId).add(shippingGroupCIR.getCommerceItem().getId());
					} else {
						List <String> commItemList = new ArrayList<String>();
						BBBCommerceItem bbbCommerceItem = (BBBCommerceItem)shippingGroupCIR.getCommerceItem();
						if(lwaFlag !=null &&  lwaFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE)) {
							// PSI7 LTL Registry | To maintain registry context Block merging of 
							// Sku's from different registries or non registry LTL sku's
							if(BBBUtility.isNotEmpty(bbbCommerceItem.getRegistryId())) {
								logDebug("LTL Commerce Item:"+bbbCommerceItem.getId()+", "
										+ "Registry Id:"+bbbCommerceItem.getRegistryId()
										+", Sku:"+bbbCommerceItem.getCatalogRefId());
								commItemList.add(bbbCommerceItem.getId());
								skuCommItemListMap.put(registryId+bbbCommerceItem.getCatalogRefId()+"Assembly", commItemList);
						} else {
							// PSI7 LTL Registry 
								commItemList.add(bbbCommerceItem.getId());
								skuCommItemListMap.put(bbbCommerceItem.getCatalogRefId()+"Assembly", commItemList);
						}
						} else {
							// PSI7 LTL Registry | To maintain registry context Block merging of 
							// Sku's from different registries or non registry LTL sku's
							if(BBBUtility.isNotEmpty(bbbCommerceItem.getRegistryId())) {
								logDebug("LTL Commerce Item:"+bbbCommerceItem.getId()+", "
										+ "Registry Id:"+bbbCommerceItem.getRegistryId()
										+", Sku:"+bbbCommerceItem.getCatalogRefId());
								commItemList.add(bbbCommerceItem.getId());
								skuCommItemListMap.put(registryId+bbbCommerceItem.getCatalogRefId(), commItemList);
							} else {
							// PSI7 LTL Registry 
								commItemList.add(bbbCommerceItem.getId());
								skuCommItemListMap.put(bbbCommerceItem.getCatalogRefId(), commItemList);
					}
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
	public void updateCISIForDelAndAssItems(CommerceItemShippingInfo cisi,
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
	 * This method create a list of ltl item map.
	 * @param sgLtlItemAssocList
	 * @param sglist
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	/*@SuppressWarnings("unchecked")
	private void createLTLItemAssocMap(
			List<Map<String, Map<String, String>>> sgLtlItemAssocList,
			List<ShippingGroup> sglist) throws BBBSystemException,
			BBBBusinessException {
		for(ShippingGroup sgrp : sglist){
			List<BBBShippingGroupCommerceItemRelationship> shippingGroupCIRList = sgrp.getCommerceItemRelationships();
			if(!shippingGroupCIRList.isEmpty()){
		    	for(final BBBShippingGroupCommerceItemRelationship shippingGroupCIR : shippingGroupCIRList){
		    		if(shippingGroupCIR.getCommerceItem() instanceof BBBCommerceItem){
						if(((BBBCommerceItem)shippingGroupCIR.getCommerceItem()).isLtlItem()){
		            		sgLtlItemAssocList.add(((BBBHardGoodShippingGroup)sgrp).getLTLItemMap());
		            	}
						break;
					}
		    	}
			}
		}
	}*/



	/**
	 * This method creates list of commerce items for which assembly is false and for which assembly is true in all shipping group
	 * @param sglist
	 * @param sgciMapForWgWithAss
	 * @param sgciMapForWgWithoutAss
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	public void createSGCIMapForWithAndWithoutAss(List<ShippingGroup> sglist,
			Map<String, List<String>> sgciMapForWgWithAss,
			Map<String, List<String>> sgciMapForWgWithoutAss)
			throws BBBSystemException, BBBBusinessException {
		for(ShippingGroup sgrp : sglist){
			List<BBBShippingGroupCommerceItemRelationship> shippingGroupCIRList = sgrp.getCommerceItemRelationships();
			List<String> commerceIdListWithAss = new ArrayList<String>();
			List<String> commerceIdListWithoutAss = new ArrayList<String>();
			for(final BBBShippingGroupCommerceItemRelationship shippingGroupCIR : shippingGroupCIRList){
				if(shippingGroupCIR.getCommerceItem() instanceof BBBCommerceItem){
					BBBCommerceItem bbbCommerceItem = (BBBCommerceItem)shippingGroupCIR.getCommerceItem();
					if(bbbCommerceItem.isLtlItem()){
						if(bbbCommerceItem.getWhiteGloveAssembly() != null && bbbCommerceItem.getWhiteGloveAssembly().equalsIgnoreCase(BBBCatalogConstants.TRUE)){
							commerceIdListWithAss.add(shippingGroupCIR.getCommerceItem().getId());
						}
						else {
							commerceIdListWithoutAss.add(shippingGroupCIR.getCommerceItem().getId());
						}
					}
					else{
						break;
					}
				}
			}
			if(!commerceIdListWithAss.isEmpty())
				sgciMapForWgWithAss.put(sgrp.getId(), commerceIdListWithAss);
			if(!commerceIdListWithoutAss.isEmpty())
			sgciMapForWgWithoutAss.put(sgrp.getId(), commerceIdListWithoutAss);
		}
	}


	/**
	 * This method creates map of commerce items in a shipping group for which shipping method is empty.
	 * @param sglist
	 * @return Map<String, List<String>> sgciMap
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> getSgCiMapForEmptyShipMethod(
			List<ShippingGroup> sglist) {
		Map<String, List<String>> sgciMap = new HashMap<String, List<String>>();
		for(ShippingGroup sgrp : sglist){
			if(BBBUtility.isEmpty(sgrp.getShippingMethod())){
				List<BBBShippingGroupCommerceItemRelationship> shippingGroupCIRList = sgrp.getCommerceItemRelationships();
				List<String> commerceIdList = new ArrayList<String>();
				for(final BBBShippingGroupCommerceItemRelationship shippingGroupCIR : shippingGroupCIRList){
					commerceIdList.add(shippingGroupCIR.getCommerceItem().getId());
				}
				sgciMap.put(sgrp.getId(), commerceIdList);
				break;
			}
		}
		return sgciMap;
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
    public final BBBPurchaseProcessHelper getShippingHelper() {
        return this.shippingHelper;
    }

    /** @param pShippingHelper the shipping helper component to set. */
    public final void setShippingHelper(final BBBPurchaseProcessHelper pShippingHelper) {
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

    /** Add Shipping Address method for REST API
     *
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException */
    public final boolean handleAddShippingAddressToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final Map<String, String> failure = this.getCheckoutProgressStates().getCheckoutFailureURLs();
        final Map<String, String> success = this.getCheckoutProgressStates().getCheckoutSuccessURLs();
        final HashMap<String, String> map = getRestIgnoreRedirectUrls();
        this.getCheckoutProgressStates().setCheckoutFailureURLs(map);
        this.getCheckoutProgressStates().setCheckoutSuccessURLs(map);

        final String siteId = getCurrentSiteID();
        this.setSiteId(siteId);

        final boolean status = this.handleAddShipping(pRequest, pResponse);
        this.getCheckoutProgressStates().setCheckoutFailureURLs(failure);
        this.getCheckoutProgressStates().setCheckoutSuccessURLs(success);
        return status;
    }
	
	/**
	 * @return
	 */
	private HashMap<String, String> getRestIgnoreRedirectUrls() {
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
        map.put("ERROR",BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
		return map;
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
        String isFromSPC = pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED);
        if(!BBBUtility.isEmpty(isFromSPC) && BBBCoreConstants.TRUE.equalsIgnoreCase(isFromSPC)){
        	pRequest.setParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED, BBBCoreConstants.TRUE);
        }
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

        this.setSiteId(getCurrentSiteID());
        request.setContextPath("");

        this.getCheckoutProgressStates().setCheckoutFailureURLs(this.getCheckoutFailureURLsForREST());

        this.logDebug("End BBBShippingGroupFormhandler.populateShippingInfoBean()");

    }

    /** @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws CommerceException */
    public final boolean handleRestMultipleShipping(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
    	
    	final String myHandleMethod = "BBBShippingGroupFormHandler.handleRestMultipleShipping";
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();

        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_MULTIPLE_SHIPPING, myHandleMethod);

            Transaction tr = null;
            final String redirectURL = "";
            try {

                tr = this.ensureTransaction();
                synchronized (this.getOrder()) {
                	
                	getMultiShippingManager().reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, 
                										true, false, false, true, false);
                    this.getCisiItems();
                    this.setClearContainer(true);

                    if ((this.shippingGroupCommerceItemShipInfoVOs == null) || (this.shippingGroupCommerceItemShipInfoVOs.size() == 0)) {
                    	return true;
                    }
                    final Collection<CommerceItemShipInfoVO> commerceItemShipInfoVOList = this.shippingGroupCommerceItemShipInfoVOs
                                    .values();
                    for (final CommerceItemShipInfoVO cShipmentInfo : commerceItemShipInfoVOList) {
                    
                    	if (StringUtils.isEmpty(cShipmentInfo.getShippingGroupType())) {
                            continue;
                        }
                        
                        if (this.getManager().getStoreShippingGroupType().equals(cShipmentInfo.getShippingGroupType())) {
                            this.createStoreShippingGroups(pRequest, pResponse, cShipmentInfo);
                        } else if (this.getManager().getHardGoodShippingGroupType()
                                        .equals(cShipmentInfo.getShippingGroupType())) {
                            this.createHardGoodShippingGroups(pRequest, pResponse, cShipmentInfo);
                        }
                    }
                    this.getManager().removeEmptyShippingGroups(this.getOrder());
                    // commented to set the pack and hold date
                    /* postMultipleShipping(pRequest, pResponse); */
                    this.getOrderManager().updateOrder(this.getOrder());
                    this.repriceOrder(pRequest, pResponse);
                }
            } catch (final CommerceException e) {
                if (this.isLoggingError()) {
                    this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_MULTIPLE_SHIPPING), e);
                }
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                                LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
                throw new CommerceException(e);
            } catch (final Exception e) {
                if (this.isLoggingError()) {
                    this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_MULTIPLE_SHIPPING), e);
                }
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                                LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
            } finally {
                if (tr != null) {
                    this.commitTransaction(tr);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_MULTIPLE_SHIPPING, myHandleMethod);
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
            }
            if (this.getFormError()) {
                if (this.isLoggingDebug()) {
                    this.logDebug("Redirecting due to form error in postMultipleShipping." + this.getFormExceptions());
                }
                return this.checkFormRedirect(null, redirectURL, pRequest, pResponse);
            }
            
            return true;
        }
        return false;
    }


    /**
     * LTL 
     * THis method is used to expose multi shipping functionality of desktop site as a rest service. It takes Map<String,CommerceItemShipInfoVO> as an input
     * and calls the handleMultipleShipping method to create shipping groups accordingly.
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws CommerceException
     */
    public final boolean handleAddMultipleShippingToOrder(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
    	
	        final Map<String, String> failure = this.getCheckoutProgressStates().getCheckoutFailureURLs();
	        final Map<String, String> success = this.getCheckoutProgressStates().getCheckoutSuccessURLs();
    		final HashMap<String, String> map = getRestIgnoreRedirectUrls();
	        this.getCheckoutProgressStates().setCheckoutFailureURLs(map);
	        this.getCheckoutProgressStates().setCheckoutSuccessURLs(map);

	        //List<CommerceItemShippingInfo> cisiItems = this.getCisiItems();
           /* Map<String, CommerceItemShipInfoVO> commerceItemShipInfoVOMap =  this.getSGCommerceItemShipInfoVO();
            for (Iterator<CommerceItemShippingInfo> iterator = cisiItems.iterator(); iterator.hasNext();) {
				CommerceItemShippingInfo commerceItemShippingInfo = iterator.next();
				CommerceItemShipInfoVO commerceItemShipInfoVO = commerceItemShipInfoVOMap.get(commerceItemShippingInfo.getCommerceItem().getId());
				if (commerceItemShipInfoVO != null) {
					commerceItemShippingInfo.setShippingGroupName(commerceItemShipInfoVO.getShippingGroupName());
					commerceItemShippingInfo.setShippingMethod(commerceItemShipInfoVO.getShippingMethod()); 
				}
			}*/
            
	        
            for (int index = 0; index < this.getCisiItems().size(); index++) {
				CommerceItemShipInfoVO commerceItemShipInfoVO = this.getSGCommerceItemShipInfoVO().get(String.valueOf(index));
				// In case of Pick up in Store item, we don't receive this VO so it can be null
				if (commerceItemShipInfoVO != null) {
					CommerceItemShippingInfo commerceItemShippingInfo = (CommerceItemShippingInfo) this.getCisiItems().get(index);
					commerceItemShippingInfo.setShippingGroupName(commerceItemShipInfoVO.getShippingGroupName());
					commerceItemShippingInfo.setShippingMethod(commerceItemShipInfoVO.getShippingMethod());
				}
			}
            final boolean status = this.handleMultipleShipping(pRequest, pResponse);
            this.getCheckoutProgressStates().setCheckoutFailureURLs(failure);
            this.getCheckoutProgressStates().setCheckoutSuccessURLs(success);
            return status;
    }
    
    /**
     * LTL 
     * This method exposes the "Add new address" functionality of shipping page from desktop site as a rest service.
     * It takes care of add new address logic for both guest and logged in user.It takes Address object as an input and then calls the handleAddNewAddress method.
     * The method will also trigger form submit for multi shipping when all but one addresses are filled and order summary has to be updated.
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws CommerceException
     */
    public final boolean handleRestAddNewAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
    	
	        final Map<String, String> failure = this.getCheckoutProgressStates().getCheckoutFailureURLs();
	        final Map<String, String> success = this.getCheckoutProgressStates().getCheckoutSuccessURLs();
    		final HashMap<String, String> map = getRestIgnoreRedirectUrls();	
	        this.getCheckoutProgressStates().setCheckoutFailureURLs(map);
	        this.getCheckoutProgressStates().setCheckoutSuccessURLs(map);
	        
	        // Finds the index of commerce item based on the shippingGroupName received in rest request
            /*List<CommerceItemShippingInfo> cisiItems = this.getCisiItems(); 
            for (int index=0;index<cisiItems.size();index++) {
				CommerceItemShippingInfo commerceItemShippingInfo = cisiItems.get(index);
				if (commerceItemShippingInfo.getShippingGroupName().equalsIgnoreCase(this.getCisiIdentifier())) {
					this.setCisiIndex(String.valueOf(index));
					break;
				}
			}
            */
	        
	        /*BBB-95 - Mobile | Advanced Shipping - missing shipping costs changes start*/
            final boolean status = this.handleAddNewAddress(pRequest, pResponse);
            if(status && isAjaxAddressFilled()){
            	
            	String newAddressKey = this.getShippingAddressContainer().getNewAddressKey();
            	BBBAddress newAddress = this.getShippingAddressContainer().getNewAddressMap().get(newAddressKey);
            	
            	if(!BBBAddressTools.duplicateFound(this.getShippingAddressContainer().getAddressMap(), newAddress)) {
            	
            		this.getShippingAddressContainer().addAddressToContainer(newAddressKey, newAddress);
            	
            		this.handleMultipleShipping(pRequest, pResponse);
            	}
            }
            /*BBB-95 - Mobile | Advanced Shipping - missing shipping costs changes end*/
            
            this.getCheckoutProgressStates().setCheckoutFailureURLs(failure);
            this.getCheckoutProgressStates().setCheckoutSuccessURLs(success);
            return status;
    }
    
    /**
     * 
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws CommerceException
     */
    public final boolean handleSplitCurrentItem(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
    	
	        final Map<String, String> failure = this.getCheckoutProgressStates().getCheckoutFailureURLs();
	        final Map<String, String> success = this.getCheckoutProgressStates().getCheckoutSuccessURLs();
    		final HashMap<String, String> map = getRestIgnoreRedirectUrls();	
	        this.getCheckoutProgressStates().setCheckoutFailureURLs(map);
	        this.getCheckoutProgressStates().setCheckoutSuccessURLs(map);
            final boolean status = this.handleShipToMultiplePeople(pRequest, pResponse);
            this.getCheckoutProgressStates().setCheckoutFailureURLs(failure);
            this.getCheckoutProgressStates().setCheckoutSuccessURLs(success);
            return status;
    }

    private void createHardGoodShippingGroups(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse, final CommerceItemShipInfoVO cShipmentInfo)
                    throws ServletException, IOException, CommerceException {
    	
    	CommerceItem cItem = null;
        String addressKey = null;
        final String commerceItemStr = cShipmentInfo.getCommerceItemId();
        if (StringUtils.isEmpty(commerceItemStr)) {
            return;
        }

        final String[] commerceItemArray = commerceItemStr.split(",");
        BBBAddressImpl bbbAddress = new BBBAddressImpl();
        
        try {
            if (!StringUtils.isEmpty(cShipmentInfo.getAddressId())) {
                RepositoryItem address = null;
                if (cShipmentInfo.getAddressId().startsWith("registry")) {
                    final List<BBBAddress> addressList = this.getCheckoutManager().getRegistryShippingAddress(
                                    this.getOrder().getSiteId(), this.getOrder());
                    if (BBBUtility.isListEmpty(addressList)) {
                        throw new BBBSystemException(ERROR_INCORRECT_ADDRESS);
                    }
                    for (final BBBAddress bbbAdd : addressList) {
                        if (bbbAdd.getIdentifier().equals(cShipmentInfo.getAddressId())) {
                            bbbAddress = (BBBAddressImpl) BBBAddressTools.copyBBBAddress(bbbAdd, bbbAddress);
                            bbbAddress.setRegistryId(bbbAdd.getRegistryId());
                            bbbAddress.setRegistryInfo(bbbAdd.getRegistryInfo());
                            bbbAddress.setIdentifier(bbbAdd.getIdentifier());
                            bbbAddress.setId(bbbAdd.getId());
                            bbbAddress.setSource(BBBCheckoutConstants.REGISTRY_SOURCE);
                            break;
                        }
                    }
                } else {
                    address = this.getCheckoutManager().getProfileTools()
                                    .getAddressesById(this.getProfile(), cShipmentInfo.getAddressId());
                    try {
                        AddressTools.copyAddress(address, bbbAddress);
                        bbbAddress.setSource(BBBAddressAPIConstants.SOURCE);
                        if (address.getPropertyValue("qasValidated")!=null)
                        	bbbAddress.setQasValidated(((Boolean)address.getPropertyValue("qasValidated")).booleanValue());
                        if (address.getPropertyValue("poBoxAddress")!=null)
                        	bbbAddress.setPoBoxAddress(((Boolean)address.getPropertyValue("poBoxAddress")).booleanValue());                      
                    } catch (final IntrospectionException e) {
                        // TODO Auto-generated catch block
                        //e.printStackTrace();
                    	logError(e.getMessage(),e);                  	
                    }
                }

                bbbAddress.setSourceIdentifier(bbbAddress.getSource() + bbbAddress.getId());
                if (BBBUtility.isEmpty(bbbAddress.getIdentifier())) {
                    addressKey = bbbAddress.getId();
                } else {
                    addressKey = bbbAddress.getIdentifier();
                }

            } else {
                final Address shipGroupAddress = cShipmentInfo.getBbbAddress();
                if (this.getSaveShippingAddress()) {
                    final BBBAddressImpl newAddress = this.getCheckoutManager().saveAddressToProfile(
                                    (Profile) this.getProfile(), shipGroupAddress, this.getOrder().getSiteId()); 
                    addressKey = newAddress.getIdentifier();
                } else {
                    addressKey = Long.toString(new Date().getTime());
                }
                bbbAddress = (BBBAddressImpl) BBBAddressTools.copyBBBAddress((BBBAddress) shipGroupAddress,
                                (BBBAddress) bbbAddress);
                
            }
            //R2.2 Start Story 83AI ,AK START- Modified Validation mobile- Validate Shipping method & Coupons for PAYPAL
            this.setRedirectState(BBBPayPalConstants.PREVIEW);
            this.validateShippingAddress(bbbAddress, pRequest, pResponse);
            BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
            if(order.isPayPalOrder())
            {
            	if(BBBPayPalConstants.SHIPPING_SINGLE != getRedirectState())
	            {
                    if (this.isOrderContainRestrictedSKU(this.getOrder(),cShipmentInfo.getBbbAddress(),BBBCoreConstants.BLANK)) {
	                        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
	                                        BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS, LOCALE_EN, null),
	                                        BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS));
	                        this.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
                    }else{
	            		//Checks for Coupons for guest user
		            	 if(getPaypalServiceManager().validateCoupons(order, this.getUserProfile()) && getProfile().isTransient()){
		            		 this.setRedirectState(BBBPayPalConstants.COUPONS);
		            	 }
                    }
	            }
             
	           //R2.2 - Start Story AK
		        //This checks if we are redirecting to preview page and Shipping Method been changed
		        if( BBBPayPalConstants.COUPONS.equals(this.getRedirectState()) || BBBPayPalConstants.PREVIEW.equals(this.getRedirectState())){
		        	//shipping method check
		        	//Defect 24701 - Start send error message to display in preview
					String ShippingMethodType = getPaypalServiceManager().validateShippingMethod(order, getUserProfile());
					if(!StringUtils.isEmpty(ShippingMethodType) && ShippingMethodType.equals(BBBCoreConstants.SHIP_METHOD_EXPEDIATED_ID)){
						this.setShippingGroupChanged(true);
						this.setShippingGroupErrorMsg(new DropletException(this.getMsgHandler().getErrMsg(
								   BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPEDIT, LOCALE_EN, null),
								   BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPEDIT).getMessage());
		
					}
					else if(!StringUtils.isEmpty(ShippingMethodType) && ShippingMethodType.equals(BBBCoreConstants.SHIP_METHOD_EXPRESS_ID)){
						this.setShippingGroupErrorMsg(new DropletException(this.getMsgHandler().getErrMsg(
								BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPRESS, LOCALE_EN, null),
								BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPRESS).getMessage());
						this.setShippingGroupChanged(true);
					}
					//Defect 24701 - END
		        }
            }
	        //R2.2 - END
             
           //R2.2 Start Story 83AI-END
            if (this.getFormError()) {
                return;
            }

        } catch (final BBBSystemException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_INCORRECT_ADDRESS), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_ADDRESS,
                            LOCALE_EN, null), ERROR_INCORRECT_ADDRESS));
        } catch (final BBBBusinessException e) {
            if (this.isLoggingError()) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_INCORRECT_ADDRESS), e);
            }
            this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_INCORRECT_ADDRESS,
                            LOCALE_EN, null), ERROR_INCORRECT_ADDRESS));
        }

        final BBBHardGoodShippingGroup hgsg = this.getManager().createHardGoodShippingGroup(this.getOrder(),
                        bbbAddress, cShipmentInfo.getShippingMethod());
        hgsg.setSourceId(addressKey);
        //R2.2 Start -Story AP- Order.xml changes
        BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
        if(order.isPayPalOrder()){
	        hgsg.setAddressStatus(getPaypalAddressStatus());
	        hgsg.setIsFromPaypal(true);
        }
        //R2.2 End
        this.getShippingAddressContainer().addNewAddressToContainer(addressKey, bbbAddress);
        this.getShippingAddressContainer().setNewAddressKey(addressKey);
        final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, pRequest.getLocale());
        if (BBBUtility.isNotEmpty(cShipmentInfo.getPackAndHoldDate())) {
            try {
                hgsg.setShipOnDate(formatter.parse(cShipmentInfo.getPackAndHoldDate()));
            } catch (final ParseException e) {
                if (this.isLoggingError()) {
                    this.logError(LogMessageFormatter.formatMessage(pRequest, "PACK_DATE_NOT_PROPER"), e);
                }
            }
        } else {
            hgsg.setShipOnDate(null);
        }
        for (final String commerceItemId : commerceItemArray) {
            cItem = this.getOrder().getCommerceItem(commerceItemId.trim());
            this.getCommerceItemManager().removeAllRelationshipsFromCommerceItem(this.getOrder(), cItem.getId());
            this.getCommerceItemManager().addItemQuantityToShippingGroup(this.getOrder(), cItem.getId(), hgsg.getId(),
                            cItem.getQuantity());
        }

        // add registrant Email Here
        this.applyRegistrantEmailToSG();
        try {
            ((BBBPurchaseProcessHelper) this.getPurchaseProcessHelper()).manageAddOrRemoveGiftWrapToShippingGroup(
                            this.getOrder(), hgsg, getCurrentSiteID(), cShipmentInfo);
        } catch (final BBBSystemException e) {

            if (this.isLoggingError()) {
                this.logError("Exception occured while adding/removing gift item" + "for multi Ship flow - ", e);
            }
            this.addFormException(new DropletException(ERROR_GIFT_MESSAGE_GENERIC_ERROR,
                            ERROR_GIFT_MESSAGE_GENERIC_ERROR));
        } catch (final BBBBusinessException e) {

            if (this.isLoggingDebug()) {
                this.logError("Exception occured while adding/removing gift item" + "for multi Ship flow - ", e);
            }
            this.addFormException(new DropletException(ERROR_GIFT_MESSAGE_GENERIC_ERROR,
                            ERROR_GIFT_MESSAGE_GENERIC_ERROR));
        }

    }

    private void createStoreShippingGroups(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse, final CommerceItemShipInfoVO cShipmentInfo)
                    throws ServletException, IOException, CommerceException {

        CommerceItem cItem = null;
        final String commerceItemStr = cShipmentInfo.getCommerceItemId();
        if (StringUtils.isEmpty(commerceItemStr)) {
            return;
        }

        final String[] commerceItemArray = commerceItemStr.split(",");
        for (final String commerceItemId : commerceItemArray) {
            cItem = this.getOrder().getCommerceItem(commerceItemId);
            this.setNewQuantity(String.valueOf((cItem.getQuantity())));
            this.setCommerceItemId(cItem.getId());
            this.setStoreId(cShipmentInfo.getStoreId());
            this.preChangeToStorePickup(pRequest, pResponse);
            if (!this.checkFormRedirect(null, this.getErrorURL(), pRequest, pResponse)) {
                throw new CommerceException();
            }
            this.changeToStorePickup(pRequest, pResponse);
            ((BBBOrderManager) this.getOrderManager()).updateAvailabilityMapInOrder(pRequest, this.getOrder());
        }
    }

    /** This method either adds or removes gift messages : REST API
     *
     * @param pRequest
     * @param pResponse
     * @return boolean
     * @throws ServletException
     * @throws IOException
     * @throws BBBBusinessException */
    public final boolean handleMultiShippingDisplayAPI(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	
 /*       pRequest.setParameter("clear", new Boolean("true"));
        pRequest.setParameter("createOneInfoPerUnit", new Boolean("false"));
        pRequest.setParameter("shippingGroupTypes", new Boolean("hardgoodShippingGroup,storeShippingGroup"));
        pRequest.setParameter("initBasedOnOrder", new Boolean("true"));
        this.getShippingGroupDroplet().service(pRequest, pResponse);
*/
    	getMultiShippingManager().reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, 
				true, false, false, true, false);
    	
        final List<CommerceItemShippingInfo> cisiItemsList = this.getCisiItems();
        final List<CommerceItemShipInfoVO> commerceItemShipInfoVOList = new ArrayList<CommerceItemShipInfoVO>();
        CommerceItemShipInfoVO vo = new CommerceItemShipInfoVO();

        for (final CommerceItemShippingInfo iterator : cisiItemsList) {
            vo = new CommerceItemShipInfoVO();
            vo.setShippingMethod(iterator.getShippingMethod());
            vo.setShippingGroupName(iterator.getShippingGroupName());
            commerceItemShipInfoVOList.add(vo);
            this.setCiInfo(iterator);
        }

        this.setCommerceItemShipInfoVOList(commerceItemShipInfoVOList);

        return false;

    }
    
    /**
     * 
     * @param pRequest
     * @param pResponse
     * @return 
     * @throws ServletException
     * @throws IOException
     * Validate if order object contains sku which is state restricted for shipping address selected
     */
    public final boolean handleValidateOrderForShippingRestriction(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	final HashMap<String, String> map = getRestIgnoreRedirectUrls();
        this.getCheckoutProgressStates().setCheckoutFailureURLs(map);
        this.getCheckoutProgressStates().setCheckoutSuccessURLs(map);
    	Order order = (BBBOrder)this.getShoppingCart().getCurrent();
    	BBBHardGoodShippingGroup hg = (BBBHardGoodShippingGroup)order.getShippingGroups().get(0);
    	BBBAddressImpl bbbAddress = new BBBAddressImpl();
          bbbAddress = (BBBAddressImpl) BBBAddressTools.copyBBBAddress((BBBAddress) hg.getShippingAddress(),
                          (BBBAddress) bbbAddress);
          this.validateShippingAddress(bbbAddress, pRequest, pResponse);
          String channel = BBBUtility.getChannel();
          if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
        	  String isFromSPC = pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED);
        	  if (!BBBUtility.isEmpty(isFromSPC) && BBBCoreConstants.TRUE.equalsIgnoreCase(isFromSPC)) {
            	  if (this.isOrderContainRestrictedSKU(this.getOrder(),null,null)) {
                      this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                      BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU_MSSG, LOCALE_EN, null),
                                      BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU_MSSG));
                      return false;
                  }
              }
  		}
          
          // Make sure user isn't trying to Express ship to AK, etc
          this.validateShippingMethod(bbbAddress, hg.getShippingMethod(), pRequest, pResponse);
	    return true;
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
	
	/** @return */
    public final boolean isQasOnProfileAddress() {
        return this.qasOnProfileAddress;
    }

    /** @param QasOnProfileAddress */
    public final void setQasOnProfileAddress(final boolean qasOnProfileAddress) {
        this.qasOnProfileAddress = qasOnProfileAddress;
    }

	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
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
	 * Checks if is ajax address filled.
	 *
	 * @return true, if is ajax address filled
	 */
	public boolean isAjaxAddressFilled() {
		return ajaxAddressFilled;
	}
	
	/**
	 * Sets the ajax address filled.
	 *
	 * @param ajaxAddressFilled the new ajax address filled
	 */
	public void setAjaxAddressFilled(boolean ajaxAddressFilled) {
		this.ajaxAddressFilled = ajaxAddressFilled;
	}
	public boolean isUpdateOrderSummaryAjax() {
		return updateOrderSummaryAjax;
	}
	
	public void setUpdateOrderSummaryAjax(boolean updateOrderSummaryAjax) {
		this.updateOrderSummaryAjax = updateOrderSummaryAjax;
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
	
	
    
    
}
