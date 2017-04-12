package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;

import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.MultipleIteminResponseVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.checkout.util.BBBCouponUtil;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.common.couponFilterVO;
import com.bbb.commerce.exim.bean.EximCustomizedAttributesVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBInventoryManagerImpl;
import com.bbb.commerce.inventory.BopusInventoryService;
import com.bbb.commerce.inventory.InventoryTools;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.ManageCheckoutLogging;
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager;
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean;
import com.bbb.commerce.order.purchase.CheckoutProgressStates.DEFAULT_STATES;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.commerce.pricing.BBBPricingManager;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.payment.droplet.BBBPaymentGroupDroplet;
import com.bbb.paypal.BBBSetExpressCheckoutResVO;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.commerce.promotion.BBBClosenessQualifierDropletResultVO;
import com.bbb.rest.commerce.promotion.BBBClosenessQualifierService;
import com.bbb.rest.giftregistry.RestAPIManager;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.CommonConfiguration;
import com.bbb.vo.wishlist.WishListVO;
import com.bbb.wishlist.BBBWishlistManager;
import com.bbb.wishlist.GiftListVO;
import com.bbb.wishlist.manager.BBBGiftlistManager;

import atg.adapter.gsa.GSARepository;
import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.order.purchase.CartModifierFormHandler;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.promotion.PromotionException;
import atg.commerce.promotion.PromotionTools;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.droplet.DropletConstants;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.naming.NameContext;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import net.sf.ezmorph.MorphException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/** Extends the default CartModifierFormHandler for custom functionality. This class holds all the handle methods for the
 * buttons on the cart
 *
 */
public class BBBCartFormhandler extends CartModifierFormHandler {
	
	private static final String SKU_NOT_PRESENT = "ERROR_NOT_PRESENT_CATALOG";
	private static final String LOCALE_EN = BBBCoreConstants.DEFAULT_LOCALE;
	private static final String BTS2 = "bts";
	private static final String ADD_ITEM_RESULTS = "addItemResults";
	private static final String PROD_ID = "prodId";
    private static final String FOR_SKU_ID = " for SKU ID ";
    private static final int MAX_QUANTITY = 99;
    private static final String QUANTITY_DESIRED = "quantityDesired";
    private static final String REPOSITORY_EXCEPTION_WHILE_UPDATE_GIFTLIST_ITEMS = "RepositoryException in StoreGiftlistFormHandler while updateGiftlistItems";
    private static final String INVALID_WISHLIST_ITEM_ID_RECIEVED = "Invalid wishlistItemId recieved.";
    private static final String NO_WISHLIST_ITEM_ID_RECIEVED = "No wishlistItemId recieved.";
    private static final String REPOSITORY_EXCEPTION = ": repositoryException";
    private static final String COMMERCE_EXCEPTION = ": commerceException";
    private static final String EXCEPTION_REMOVING_EMPTY_SHIPPING_GROUPS = "Exception removing empty shipping groups ";
    private static final String DEFAULT_COUNTRY_MISSING_FOR_SITE = "Default country missing for site";
    private static final String PRICE_IS_NOT_SPECIFIED_FOR_THE_SKU = "Price is not specified for the sku";
    private static final String ERROR_UPDATING_ORDER = "errorUpdatingOrder";
    private static final String PRE_SET_ORDER_BY_COMMERCE_ID = "preSetOrderByCommerceId";
    private static final String BILLING_ERROR = "ERR_CART_BILLING_ERROR";
    private static final String SHIPPING_ERROR = "ERR_CART_SHIPPING_ERROR";
    private static final String ITEMS_METHOD_RESTRICTIONS = "ERR_CART_ITEMS_METHOD_RESTRICTIONS";
    private static final String GENERIC_ERROR_TRY_LATER = "ERR_CART_GENERIC_ERROR_TRY_LATER";
    private static final String PROMOTION_ERROR_ON_APPLY = "ERR_PROMOTION_ON_APPLY";
    private static final String ITEMS_STATE_RESTRICTIONS = "ERR_CART_ITEMS_STATE_RESTRICTIONS";
    private static final String ITEMS_POBOX_RESTRICTIONS = "ERR_CART_ITEMS_POBOX_RESTRICTIONS";
    private static final String ITEMS_OUTOF_STOCK = "ERR_CART_ITEMS_OUTOF_STOCK";
    private static final String UPDATE_CART_DELIMETER = ";";
    private static final String UPDATE_CART_EQUAL = "=";
    private static final String REGISTRY_ID = "registryId";
    private static final String STORE_ID = "storeId";
    private static final String PRODUCT_ID = "prodId";
    private static final String SKU_ID = "skuId";
    private static final String WISH_LIST = "wishlist";
    private static final String OUT_OF_STOCK = "err_cart_outOfStock";
    private static final String INVALID_SKU_ID = "err_cart_invalidSkuId";
    private static final String NULL_SKU_ID = "err_cart_nullSkuId";
    private static final String NULL_REGISTRY_ID = "err_cart_nullRegistryId";
    private static final String INVALID_REGISTRY_ID = "err_cart_invalidRegistryId";
    private static final String NULL_STORE_ID = "err_cart_nullStoreId";
    private static final String INVALID_STORE_ID = "err_cart_invalidStoreId";
    private static final String ERR_CART_MISSING_COMMERCE_ITEM = "ERR_CART_MISSING_COMMERCE_ITEM";
    private static final String ERR_CART_INVALID_COMMERCE_ITEM = "ERR_CART_INVALID_COMMERCE_ITEM";
    private static final String INVALID_QUANTITY = "err_cartdetail_invalidQuantity";
    private static final String ERROR_INVENTORY_CHECK = "err_cart_inventoryCheckFail";
    private static final String INVALID_QUANTITY_FORMAT = "err_invalidQuantity_format";
    private static final String COMMERCE_ITEM_NOT_FOUND = "commerce_item_not_found";
    private static final String INVALID_PARAMETER = "invalid_parameter";
    private static final String OUT_OF_STOCK_ITEM = "err_cart_outOfStockItem";
    private static final String COUPON_GENERIC_ERROR = "err_coupon_grant_error";
    private static final String COUPON_BOPUS_ERROR = "err_coupon_bopus_error";
    private static final String COUPON_GC_ERROR = "err_coupon_gc_error";
    private static final String COUPON_NMC_ERROR = "err_coupon_nmc_error";
    private static final String COUPON_SKU_EX_ERROR = "err_coupon_sku_ex_error";
    private static final String ERR_CART_PAYPAL_SERVICE = "err_cart_paypal_service";
    private static final String FROM_MOBILE_PDP = "true";
    private static final String PAYMENT_GROUP_GET_AMOUNT = "paymentGroup.getAmount()";
    private static final String PAYMENT_GROUP_GET_ID = "paymentGroup.getId()";
    private static final String ADD_ITEMS_RESULTS = "addItemResults";
    
    private InventoryTools inventoryTools;
    private SiteManager siteManager;
    private SearchStoreManager searchStoreManager;
    private GiftRegistryManager giftRegistryManager;
    private LblTxtTemplateManager messageHandler;
    private CheckoutProgressStates checkoutState;
    private CommonConfiguration commonConfiguration;
    private PromotionTools promotionTools;
    private ManageCheckoutLogging manageLogging;
    private PorchServiceManager porchServiceManager;
    private BBBStoreInventoryContainer storeInventoryContainer;
    private BBBWishlistManager wishlistManager;
    private BBBInventoryManager inventoryManager;
    private BBBCatalogTools catalogTools;
    private BBBSavedItemsSessionBean savedItemsSessionBean;
    private BBBPricingManager pricingManager;
    private BBBCouponUtil couponUtil;
    private BBBCheckoutManager checkoutManager;
    private BBBPayPalServiceManager paypalServiceManager;
    private BBBPaymentGroupDroplet paymentGroupDroplet;
    private Map<String, String> moveAllItemFailureResult;
    private Map<String, String> coupons;
    private Map<String, String> couponErrors;
    private List<String> wishListItemIdsToRemove;
    private List<String> productList;
	HashMap<String, String > appliedCouponMap = new HashMap<String, String>();
	private String jsonCouponErrors;	
	private String storeId;
    private String itemIdJustMovedBack;
    private String systemErrorPage;
    private String commerceItemId;
    private String removeItemIdParam;
    private String wishListId;
    private String wishlistItemId;
    private String removalStoreId;
    private String globalErrorURL;
    private String updateCartInfoSemiColonSeparated;
    private String couponPage;
    private String jsonResultString;
    private String cartPayLoadResultString;
    private String couponClaimCode;
    private String deletedProdId;
    private String registryId;
    private String removeItemFromOrderFormsSuccessURL;
    private String removeItemFromOrderFormsErrorURL;
    private String addItemToOrderFormsSuccessURL;
    private String addItemToOrderFormsErrorURL;
    private String ajaxCartOrderSuccessURL;
    private String ajaxCartOrderErrorURL;
    private String associateRegistryContextSuccessURL;
    private String associateRegistryContextErrorURL;
    

	private long quantityAdded;
    private int count;

    private boolean checkItemQuantity;
    private boolean fromCart;
    private boolean restService;
    private boolean undoCheck;
    private boolean fromWishlist;
    private boolean expressCheckout;
    private boolean fromPipeline;
    private boolean newItemAdded;
    private String payPalErrorURL;
    private String isFromCart;
    private Profile userProfile;
	private String paypalToken;
    private BBBPayPalSessionBean payPalSessionBean;
    private String mobileCancelURL;
	private String mobileRedirectURL;
	private String payPalSucessURL;
	private boolean payPalTokenNotExpired;
	private Map<String,String> errorMap;
	private boolean recommItemSelected = false;
	private RestAPIManager restAPIManager;
	private List<MultipleIteminResponseVO> listOfMultipleItemInResponseVO;
	private boolean partialAddFlow = false;
	private boolean singlePageCheckout=false;
	private String buyOffAssociationSkuId;
	private boolean buyOffFlag;
	private String errorFlagBuyOffAssociation;
	private boolean buyOffDuplicateItemFlag;
	private String referenceNumber;
	private String editItemInOrderFormsSuccessURL;
    private String editItemInOrderFormsErrorURL;
    private String ltlShipMethod;
    private String shipMethodUnsupported;
 	private String imageURL;
    private BBBEximManager eximManager;
    private BopusInventoryService bopusInventoryService;
    private GSARepository mLocalStoreRepository;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private Map<String,String> successUrlMap;
    private Map<String,String> errorUrlMap;
    private String fromPage;
    private String successQueryParam;
    private String sddRequired;
    private String queryParam;//Query Parameter that will be appended to success/error URL

	/**
	 * @return the inventoryTools
	 */
	public InventoryTools getInventoryTools() {
		return inventoryTools;
	}
	
	/**
	 * @param inventoryTools the inventoryTools to set
	 */
	public void setInventoryTools(InventoryTools inventoryTools) {
		this.inventoryTools = inventoryTools;
	}
	
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

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}
	
	public String getSuccessQueryParam() {
		return successQueryParam;
	}
	public void setSuccessQueryParam(String successQueryParam) {
		this.successQueryParam = successQueryParam;
	}
	
	private CoherenceCacheContainer cacheContainer;

	/**
	 * @return the cacheContainer
	 */
	public CoherenceCacheContainer getCacheContainer() {
		return cacheContainer;
	}
	
	/**
	 * @param cacheContainer
	 *            the cacheContainer to set
	 */
	public void setCacheContainer(CoherenceCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	public BBBEximManager getEximManager() {
		return eximManager;
	}

	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
    private String changeClosenessQualifierMsg;
    private String closenessQualifierName;
    private BBBClosenessQualifierService closenessQualifierService;
    private boolean singlePageCheckoutEnabled;
	
   
	public boolean isSinglePageCheckoutEnabled() {
		return singlePageCheckoutEnabled;
	}

	public void setSinglePageCheckoutEnabled(boolean singlePageCheckoutEnabled) {
		this.singlePageCheckoutEnabled = singlePageCheckoutEnabled;
	}

	private Map<String,EximCustomizedAttributesVO> eximRefNumMap;
    		
	/**
	 * @return the eximRefNumMap
	 */
	public Map<String,EximCustomizedAttributesVO> getEximRefNumMap() {
		return eximRefNumMap;
	}

	/**
	 * @param eximRefNumMap the eximRefNumMap to set
	 */
	public void setEximRefNumMap(Map<String,EximCustomizedAttributesVO> eximRefNumMap) {
		this.eximRefNumMap = eximRefNumMap;
	}

	public String getLtlShipMethod() {
		return ltlShipMethod;
	}

	public void setLtlShipMethod(String ltlShipMethod) {
		this.ltlShipMethod = ltlShipMethod;
	}

	public String getShipMethodUnsupported() {
		return shipMethodUnsupported;
	}

	public void setShipMethodUnsupported(String shipMethodUnsupported) {
		this.shipMethodUnsupported = shipMethodUnsupported;
	}
		
	public String getReferenceNumber() {
			return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
			this.referenceNumber = referenceNumber;
	}
	public String getEditItemInOrderFormsErrorURL() {
			return editItemInOrderFormsErrorURL;
	}

	public String getEditItemInOrderFormsSuccessURL() {
		return editItemInOrderFormsSuccessURL;
	}

	public void setEditItemInOrderFormsSuccessURL(
			String editItemInOrderFormsSuccessURL) {
		this.editItemInOrderFormsSuccessURL = editItemInOrderFormsSuccessURL;
	}

	public void setEditItemInOrderFormsErrorURL(String editItemInOrderFormsErrorURL) {
			this.editItemInOrderFormsErrorURL = editItemInOrderFormsErrorURL;
	}
	public boolean isSinglePageCheckout() {
		return singlePageCheckout;
	}

	public void setSinglePageCheckout(boolean singlePageCheckout) {
		this.singlePageCheckout = singlePageCheckout;
	}
	public boolean isPartialAddFlow() {
		return partialAddFlow;
	}

	public void setPartialAddFlow(boolean partialAddFlow) {
		this.partialAddFlow = partialAddFlow;
	}

	public List<MultipleIteminResponseVO> getListOfMultipleItemInResponseVO() {
		return listOfMultipleItemInResponseVO;
	}

	public void setListOfMultipleItemInResponseVO(
			List<MultipleIteminResponseVO> listOfMultipleItemInResponseVO) {
		this.listOfMultipleItemInResponseVO = listOfMultipleItemInResponseVO;
	}

    public HashMap<String, String> getAppliedCouponMap() {
		return appliedCouponMap;
	}

	public void setAppliedCouponMap(HashMap<String, String> appliedCouponMap) {
		this.appliedCouponMap = appliedCouponMap;
	}
	
	public Map<String, String> getErrorMap() {
		return this.errorMap;
	}

	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	public boolean isPayPalTokenNotExpired() {
		return payPalTokenNotExpired;
	}

	public void setPayPalTokenNotExpired(boolean payPalTokenNotExpired) {
		this.payPalTokenNotExpired = payPalTokenNotExpired;
	}

	/**
	 * @return the payPalSucessURL
	 */
	public String getPayPalSucessURL() {
		return payPalSucessURL;
	}

	/**
	 * @param payPalSucessURL the payPalSucessURL to set
	 */
	public void setPayPalSucessURL(String payPalSucessURL) {
		this.payPalSucessURL = payPalSucessURL;
	}

	public String getMobileCancelURL() {
		return mobileCancelURL;
	}

	public void setMobileCancelURL(String mobileCancelURL) {
		this.mobileCancelURL = mobileCancelURL;
	}

	public String getMobileRedirectURL() {
		return mobileRedirectURL;
	}

	public void setMobileRedirectURL(String mobileRedirectURL) {
		this.mobileRedirectURL = mobileRedirectURL;
	}

    /**
	 * Getter for paypal token.
	 * 
	 * @return the paypalToken
	 */
	 public String getPaypalToken() {
		return paypalToken;
	}

	/**
	 * Setter for paypal token.
	 * 
	 * @param paypalToken the paypalToken to set
	 */
	 public void setPaypalToken(String paypalToken) {
		 this.paypalToken = paypalToken;
	 }
    
    /**
	 * @return the payPalSessionBean
	 */
	public BBBPayPalSessionBean getPayPalSessionBean() {
		return this.payPalSessionBean;
	}

	/**
	 * @param payPalSessionBean the payPalSessionBean to set
	 */
	public void setPayPalSessionBean(BBBPayPalSessionBean payPalSessionBean) {
		this.payPalSessionBean = payPalSessionBean;
	}

	/**
	 * @return the userProfile
	 */
	public Profile getUserProfile() {
		return this.userProfile;
	}

	/**
	 * @param userProfile the userProfile to set
	 */
	public void setUserProfile(Profile userProfile) {
		this.userProfile = userProfile;
	}

	/**
	 * @return the isFromCart
	 */
	public String getIsFromCart() {
		return isFromCart;
	}

	/**
	 * @param isFromCart the isFromCart to set
	 */
	public void setIsFromCart(String isFromCart) {
		this.isFromCart = isFromCart;
	}

	/**
	 * @return the payPalErrorURL
	 */
	public String getPayPalErrorURL() {
		return payPalErrorURL;
	}

	/**
	 * @param payPalErrorURL the payPalErrorURL to set
	 */
	public void setPayPalErrorURL(String payPalErrorURL) {
		this.payPalErrorURL = payPalErrorURL;
	}

	public BBBPayPalServiceManager getPaypalServiceManager() {
		return this.paypalServiceManager;
	}

	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}

	/** Default Constructor. */
    public BBBCartFormhandler() {
        this.coupons = new HashMap<String, String>();
        this.couponErrors = new HashMap<String, String>();
    }

    /** @return the restService */
    public final boolean isRestService() {
        return this.restService;
    }

    /** @param restService the restService to set */
    public final void setRestService(final boolean restService) {
        this.restService = restService;
    }

    /** @return the itemIdJustMvBack */
    public final String getItemIdJustMvBack() {
        return this.itemIdJustMovedBack;
    }

    /** @param itemIdJustMovedBack the itemIdJustMvBack to set */
    public final void setItemIdJustMvBack(final String itemIdJustMovedBack) {
        this.itemIdJustMovedBack = itemIdJustMovedBack;
    }

    /** @return the undoCheck */
    public final boolean isUndoCheck() {
        return this.undoCheck;
    }

    /** @param undoCheck the undoCheck to set */
    public final void setUndoCheck(final boolean undoCheck) {
        this.undoCheck = undoCheck;
    }

    /** @return the storeId */
    public final String getStoreId() {
        return this.storeId;
    }

    /** @param storeId the storeId to set */
    public final void setStoreId(final String storeId) {
        this.storeId = storeId;
    }

    /** @return the isFromCart */
    public final boolean isFromCart() {
        return this.fromCart;
    }

    /** @param fromCart the isFromCart to set */
    public final void setFromCart(final boolean fromCart) {
        this.fromCart = fromCart;
    }

    /** @return the checkItemQuantity */
    public final boolean isCheckItemQuantity() {
        return this.checkItemQuantity;
    }

    /** @param checkItemQuantity the checkItemQuantity to set */
    public final void setCheckItemQuantity(final boolean checkItemQuantity) {
        this.checkItemQuantity = checkItemQuantity;
    }

    /** @return the prodList */
    public final List<String> getProdList() {
        return this.productList;
    }

    /** @param pProdList the prodList to set */
    public final void setProdList(final List<String> pProdList) {
        this.productList = pProdList;
    }

    /** @return the savedItemsSessionBean */
    public final BBBSavedItemsSessionBean getSavedItemsSessionBean() {
        return this.savedItemsSessionBean;
    }

    /** @param savedItemsSessionBean the savedItemsSessionBean to set */
    public final void setSavedItemsSessionBean(final BBBSavedItemsSessionBean savedItemsSessionBean) {
        this.savedItemsSessionBean = savedItemsSessionBean;
    }

    /** @return Coupon Error List */
    public final Map<String, String> getCouponErrorList() {
        return this.couponErrors;
    }

    /** @param couponErrorList Coupon Error List */
    public final void setCouponErrorList(final Map<String, String> couponErrorList) {
        this.couponErrors = couponErrorList;
    }

    /** @return the wishListItemIdsToRemove */
    public final List<String> getWishListItemIdsToRemove() {
        return this.wishListItemIdsToRemove;
    }

    /** @param wishListItemIdsToRemove the wishListItemIdsToRemove to set */
    public final void setWishListItemIdsToRemove(final List<String> wishListItemIdsToRemove) {
        this.wishListItemIdsToRemove = wishListItemIdsToRemove;
    }

    /** @return the siteManager */
    public final SiteManager getSiteManager() {
        return this.siteManager;
    }

    /** @param siteManager the siteManager to set */
    public final void setSiteManager(final SiteManager siteManager) {
        this.siteManager = siteManager;
    }

    /** @return the mwishlistManager */
    public final BBBWishlistManager getWishlistManager() {
        return this.wishlistManager;
    }

    /** @param wishlistManager the mwishlistManager to set */
    public final void setWishlistManager(final BBBWishlistManager wishlistManager) {
        this.wishlistManager = wishlistManager;
    }

    /** @return the inventoryManager */
    public final BBBInventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    /** @param inventoryManager the inventoryManager to set */
    public final void setInventoryManager(final BBBInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }
    
    /** @return the pricingManager */
    public final BBBPricingManager getPricingManager() {
        return this.pricingManager;
    }

    /** @param pricingManager the pricingManager to set */
    public final void setPricingManager(final BBBPricingManager pricingManager) {
        this.pricingManager = pricingManager;
    }

    /** @return the newItemAdded */
    public final boolean isNewItemAdded() {
        return this.newItemAdded;
    }

    /** @param pNewItemAdded the newItemAdded to set */
    public final void setNewItemAdded(final boolean pNewItemAdded) {
        this.newItemAdded = pNewItemAdded;
    }

    /** @return the countNo */
    public final int getCountNo() {
        return this.count;
    }

    /** @param count the countNo to set */
    public final void setCountNo(final int count) {
        this.count = count;
    }

    /** @return */
    public final String getRegistryId() {
        return this.registryId;
    }

    /** @param registryId */
    public final void setRegistryId(final String registryId) {
        this.registryId = registryId;
    }

    /** @return the mMoveAllItemFailureResult */
    public final Map<String, String> getMoveAllItemFailureResult() {
        return this.moveAllItemFailureResult;
    }

    /** @param moveAllItemFailureResult the mMoveAllItemFailureResult to set */
    public final void setMoveAllItemFailureResult(final Map<String, String> moveAllItemFailureResult) {
        this.moveAllItemFailureResult = moveAllItemFailureResult;
    }

    /** @return Updated Cart Information */
    public final String getUpdateCartInfoSemiColonSeparated() {
        return this.updateCartInfoSemiColonSeparated;
    }

    /** @param updateCartInfoSemiColonSeparated Updated Cart Information */
    public final void setUpdateCartInfoSemiColonSeparated(final String updateCartInfoSemiColonSeparated) {
        this.updateCartInfoSemiColonSeparated = updateCartInfoSemiColonSeparated;
    }

    /** @return Deleted Product ID */
    public final String getDeletedProdId() {
        return this.deletedProdId;
    }

    /** @return Deleted Product ID */
    public final BBBCouponUtil getCouponUtil() {
        return this.couponUtil;
    }

    /** @param fromPipeline */
    public void setFromPipelineFlag(final boolean fromPipeline) {
        this.fromPipeline = fromPipeline;
    }

    /** @return */
    public boolean isFromPipelineFlag() {
        return this.fromPipeline;
    }

    /** @param couponUtil */
    public final void setCouponUtil(final BBBCouponUtil couponUtil) {
        this.couponUtil = couponUtil;
    }

    /** @return Coupon Claim Code */
    public final String getCouponClaimCode() {
        return this.couponClaimCode;
    }

    /** @param couponClaimCode Coupon Claim Code */
    public final void setCouponClaimCode(final String couponClaimCode) {
        this.couponClaimCode = couponClaimCode;
    }

    /** @param pDeletedProdId */
    public final void setDeletedProdId(final String pDeletedProdId) {
        this.deletedProdId = pDeletedProdId;
    }

    /** @return Coupon Page */
    public final String getCouponPage() {
        return this.couponPage;
    }

    /** @param couponPage Coupon Page */
    public final void setCouponPage(final String couponPage) {
        this.couponPage = couponPage;
    }

    /** @return the commonConfiguration */
    public final CommonConfiguration getCommonConfiguration() {
        return this.commonConfiguration;
    }

    /** @param pCommonConfiguration the commonConfiguration to set */
    public final void setCommonConfiguration(final CommonConfiguration pCommonConfiguration) {
        this.commonConfiguration = pCommonConfiguration;
    }

    /** @param coupons Coupons */
    public final void setCouponList(final Map<String, String> coupons) {
        this.coupons = coupons;
    }

    /** @return Coupons */
    public final Map<String, String> getCouponList() {
        return this.coupons;
    }

    /** @return */
    public final PromotionTools getPromoTools() {
        return this.promotionTools;
    }

    /** @param promotionTools */
    public final void setPromoTools(final PromotionTools promotionTools) {
        this.promotionTools = promotionTools;
    }

    /** @return Global Error URL */
    public final String getGlobalErrorURL() {
        return this.globalErrorURL;
    }

    /** @param globalErrorURL Global Error URL */
    public final void setGlobalErrorURL(final String globalErrorURL) {
        this.globalErrorURL = globalErrorURL;
    }

    /** @return */
    public final String getRemovalStoreId() {
        return this.removalStoreId;
    }

    /** @param pRemovalStoreId */
    public final void setRemovalStoreId(final String pRemovalStoreId) {
        this.removalStoreId = pRemovalStoreId;
    }

    /** @return */
    public final boolean isFromWishlist() {
        return this.fromWishlist;
    }

    /** @return */
    public final String getWishListId() {
        return this.wishListId;
    }

    /** @param wishListId */
    public final void setWishListId(final String wishListId) {
        this.wishListId = wishListId;
    }

    /** @return */
    public final String getWishlistItemId() {
        return this.wishlistItemId;
    }

    /** @param wishlistItemId */
    public final void setWishlistItemId(final String wishlistItemId) {
        this.wishlistItemId = wishlistItemId;
    }

    /** @param fromWishlist */
    public final void setFromWishlist(final boolean fromWishlist) {
        this.fromWishlist = fromWishlist;
    }

    /** @return */
    public final String getCommerceItemId() {
        return this.commerceItemId;
    }

    /** @param pCommerceItemId */
    public final void setCommerceItemId(final String pCommerceItemId) {
        this.commerceItemId = pCommerceItemId;
    }

    /** @return */
    public final BBBCatalogTools getCatalogUtil() {
        return this.catalogTools;
    }

    /** @param catalogUtil */
    public final void setCatalogUtil(final BBBCatalogTools catalogUtil) {
        this.catalogTools = catalogUtil;
    }

    /** @return Store Manager */
    public final SearchStoreManager getStoreManager() {
        return this.searchStoreManager;
    }

    /** @param storeManager */
    public final void setStoreManager(final SearchStoreManager storeManager) {
        this.searchStoreManager = storeManager;
    }

    /** @return */
    public final GiftRegistryManager getRegistryManager() {
        return this.giftRegistryManager;
    }

    /** @param registryManager */
    public final void setRegistryManager(final GiftRegistryManager registryManager) {
        this.giftRegistryManager = registryManager;
    }

    /** @return System Error Page */
    public final String getSystemErrorPage() {
        return this.systemErrorPage;
    }

    /** @param systemErrorPage */
    public final void setSystemErrorPage(final String systemErrorPage) {
        this.systemErrorPage = systemErrorPage;
    }

    /** @return the removal commerce IDs. */
    public final String[] getRemoveItemFromOrder() {
        return this.getRemovalCommerceIds();
    }

    /** @param pRemoveItemFromOrder - the removal commerce ids. */
    public final void setRemoveItemFromOrderForms(final String[] pRemoveItemFromOrder) {
        this.setRemoveItemFromOrder(pRemoveItemFromOrder);
    }
    
    /** @param pRemoveItemFromOrder - the removal commerce ids. */
    public final void setRemoveItemFromOrder(final String[] pRemoveItemFromOrder) {
        if ((pRemoveItemFromOrder != null) && (pRemoveItemFromOrder.length > 0)) {
            this.setRemovalCommerceIds(pRemoveItemFromOrder);
        } else {
            this.setRemovalCommerceIds(null);
        }
    }

    /** returns the CMS message handler to add form exceptions.
     *
     * @return Message Handler */
    public final LblTxtTemplateManager getMsgHandler() {
        return this.messageHandler;
    }

    /** Sets the CMS message handler to add form exceptions.
     *
     * @param messageHandler */
    public final void setMsgHandler(final LblTxtTemplateManager messageHandler) {
        this.messageHandler = messageHandler;
    }

    /** @return */
    public long getQtyAdded() {
        return this.quantityAdded;
    }

    /** @param qtyAdded */
    public void setQtyAdded(final long qtyAdded) {
        this.quantityAdded = qtyAdded;
    }

    public final BBBCheckoutManager getCheckoutManager() {
        return this.checkoutManager;
    }

    /** @param pCheckoutManager */
    public final void setCheckoutManager(final BBBCheckoutManager pCheckoutManager) {
        this.checkoutManager = pCheckoutManager;
    }

    /** @return */
    public final CheckoutProgressStates getCheckoutState() {
        return this.checkoutState;
    }

    /** @param checkoutState */
    public final void setCheckoutState(final CheckoutProgressStates checkoutState) {
        this.checkoutState = checkoutState;
    }

    /** @return */
    public final boolean getExpressCheckout() {
        return this.expressCheckout;
    }

    /** @param pIsExpressCheckout */
    public final void setExpressCheckout(final boolean pIsExpressCheckout) {
        this.expressCheckout = pIsExpressCheckout;
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

    public boolean isRecommItemSelected() {
		return recommItemSelected;
	}

	public void setRecommItemSelected(boolean recommItemSelected) {
		this.recommItemSelected = recommItemSelected;
	}
	
	  /**
	   * Creates the extra parameter map that is used when the order is repriced after
	   * form handler modifications to the order. Override this to add extra parameters for Exim Details
	   *
	   * @return Map of parameters or null
	   */
	  @Override
	  protected Map createRepriceParameterMap()
	  {
		  Map parameters = super.createRepriceParameterMap();
		  if(parameters == null){
    		  parameters = new HashMap();
    	  }
	      //Set the Exim reference number Map in Extra Params
	      if( null != this.getEximRefNumMap() && !this.getEximRefNumMap().isEmpty()){
	    	  
	    	  parameters.put(BBBCoreConstants.EXIM_DETAILS_MAP,this.getEximRefNumMap());
	    	  
	      }
	      /*BBBH-6242 Story 24 : Feedback : Mini Cart Behavior for Registrant view */
	      if(null!=this.getJsonResultString()){
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(this.getJsonResultString());
		        final DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		        final List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);
		        if (dynaBeanProperties.contains(ADD_ITEMS_RESULTS)) {
		        final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
						.get(ADD_ITEM_RESULTS);
		      
		        for (DynaBean dynaBean : itemArray) {
		        	  List<String> dynProperty = getPropertyNames(dynaBean);
		        	 if(dynProperty.contains(BBBCoreConstants.FROM_REIGISTRY_OWNER))
							{
		        			String fromRegOwner = dynaBean.get(BBBCoreConstants.FROM_REIGISTRY_OWNER).toString();
					        parameters.put(BBBCoreConstants.FROM_REIGISTRY_OWNER,fromRegOwner);
						   }
		        		}
					}
	      }
	      return parameters;
	  }
	  
	  /** handleReserveNow method validates if the the inventory is zero.
	   * if zero updates the loacalstorerepository and invalidates the cache
	   * if non zero, calls the existing addItemToOrder
	   *
	   * @param pRequest - DynamoHttpServletRequest
	   * @param pResponse - DynamoHttpServletResponse
	   * @throws ServletException Exception
	   * @throws IOException Exception 
	   * @throws InventoryException */
	  public final boolean handleReserveNow(final DynamoHttpServletRequest pRequest,
			  final DynamoHttpServletResponse pResponse , String favStoreState) throws ServletException,
			  IOException, InventoryException {
		  String storeIdVal = null;
		  String skuIdVal = null;
		  int quantity = 1;

		  JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(this
				  .getJsonResultString());
		  final DynaBean JSONResultbean = (DynaBean) JSONSerializer
				  .toJava(jsonObject);

		  final List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);
		  if (dynaBeanProperties.contains(ADD_ITEM_RESULTS)) {
			  @SuppressWarnings("unchecked")
			  final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
			  .get(ADD_ITEM_RESULTS);
			  for (DynaBean dynaBean : itemArray) {
				  try {
					  storeIdVal = dynaBean.get(BBBCoreConstants.STOREID).toString();
					  skuIdVal = dynaBean.get(BBBCoreConstants.SKUID).toString();
					  if (dynaBean.get(BBBCoreConstants.QTY) != null) {
						  quantity = Integer.parseInt(dynaBean.get(BBBCoreConstants.QTY)
								  .toString());
					  }
				  } catch (Exception e) {
					  logError(e.getMessage(), e);
				  }

			  }
			  this.setJsonResultString(JSONSerializer.toJSON(JSONResultbean)
					  .toString());
		  }
		  List<String> storeId = new ArrayList<String>();
		  storeId.add(storeIdVal);
		  try {
			  if (favStoreState != null) {
				  Map<String, Integer> inventoryMap = getBopusInventoryService()
						  .getInventoryForBopusItem(skuIdVal, storeId, false);
				  
				  if (BBBUtility.isMapNullOrEmpty(inventoryMap)) {
					  String error = getLblTxtTemplateManager().getErrMsg(
							  BBBCoreErrorConstants.ERROR_FROM_DOM,
							  BBBCoreConstants.DEFAULT_LOCALE, null);								
					 getFormExceptions().addElement(new DropletException(error,BBBCoreErrorConstants.RESERVE_NOW_DOM_1027));
				
				  } else {
					  if (inventoryMap.containsKey(storeIdVal)) {
						  int invCount = inventoryMap.get(storeIdVal);
						  ThresholdVO skuThresholdVO = getCatalogUtil()
								  .getSkuThreshold(
										  SiteContextManager.getCurrentSiteId(),
										  skuIdVal);
						  int inventoryStatus = (((BBBInventoryManagerImpl) getInventoryManager())
								  .getInventoryStatus(invCount, quantity, skuThresholdVO , storeIdVal));
						  
						  if (quantity > 1 && quantity > invCount && invCount > 0) {
							  String error = getLblTxtTemplateManager()
									  .getErrMsg(
											  BBBCoreErrorConstants.ERROR_INSUFFICIENT_MEMORY,
											  BBBCoreConstants.DEFAULT_LOCALE,
											  null);

							  getFormExceptions().addElement(new DropletException(error,BBBCoreErrorConstants.RESERVE_NOW_INSUFF_MEM_1028));
							  return false;
						  }
						  else if(skuThresholdVO == null)
							  				  {
							  						  String error = getLblTxtTemplateManager()
							  								  .getErrMsg(
							  										  BBBCoreErrorConstants.ERROR_EMPTY_SKU_THRESHOLD,
							  										  BBBCoreConstants.DEFAULT_LOCALE,
							  										  null);
							  						  getFormExceptions().addElement(new DropletException(error,BBBCoreErrorConstants.RESERVE_NOW_EMPTY_THRESHOLD_1030));
							  						  return false;
							  					  }
						  else if(skuThresholdVO != null && invCount - skuThresholdVO.getThresholdLimited() <= 0)
						  {
							  String error = getLblTxtTemplateManager()
									  .getErrMsg(
											  BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL,
											  BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL,
											  null);
							  getFormExceptions().addElement(new DropletException(error,BBBCoreErrorConstants.RESERVE_NOW_INVEN_NOT_AVAIL_1029));
							  updateLocalInventory(0, storeIdVal, skuIdVal);
						  }
						  else if (inventoryStatus != BBBInventoryManager.AVAILABLE
								  && inventoryStatus != BBBInventoryManager.LIMITED_STOCK) {
							  // add exception , Inventory not available
							  // getLocalStoreRepository() update to Zero inventory
							  // count
							  int quantityUpdated = quantity;
							  if(quantity == 1 )
							  {
								  quantityUpdated = 0;
								  String error = getLblTxtTemplateManager()
										  .getErrMsg(
												  BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL,
												  BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL,
												  null);
								  getFormExceptions().addElement(new DropletException(error,BBBCoreErrorConstants.RESERVE_NOW_INVEN_NOT_AVAIL_1029));

							  }
							  else
							  {
								  quantityUpdated = invCount;
								  String error = getLblTxtTemplateManager()
										  .getErrMsg(
												  BBBCoreErrorConstants.ERROR_INSUFFICIENT_MEMORY,
												  BBBCoreConstants.DEFAULT_LOCALE,
												  null);

								  getFormExceptions().addElement(new DropletException(error,BBBCoreErrorConstants.RESERVE_NOW_INSUFF_MEM_1028));
							  }

							  updateLocalInventory(quantityUpdated, storeIdVal, skuIdVal);
						  }
						  else
						  {
							  setJsonResultString(getJsonResultString());
							  return true;
						  }
					  }
				  }
			  }
			  
		  }
		  
		  catch (BBBBusinessException e) {
			  this.logError(BBBCoreErrorConstants.ERROR_FETCHING_INVENTORY
					  + e.getMessage());
			  this.addFormException((new DropletException(
					  BBBCoreErrorConstants.ERROR_FETCHING_INVENTORY
					  + e.getMessage())));
		  } catch (BBBSystemException e) {
			  this.logError(BBBCoreErrorConstants.ERROR_WHILE_DOM_CALL);
			  String error = getLblTxtTemplateManager()
					  .getErrMsg(
							  BBBCoreErrorConstants.ERROR_WHILE_DOM_CALL,
							  BBBCoreErrorConstants.ERROR_WHILE_DOM_CALL,
							  null);
			  this.addFormException((new DropletException(
					  error,
					  BBBCoreErrorConstants.RESERVE_NOW_DOM_1027)));
			  this.getFormExceptions().get(0);
		  }

		  return false;
	  }
	  
	  /**This method updates local inventory with updated quantity
	   * @param quantity
	   * @param storeIdVal
	   * @param skuIdVal
	   */
	  private void updateLocalInventory(int quantity , String storeIdVal , String skuIdVal)
	  {
		  final String ITM_DES_LOCAL_STORE_INV = "storeLocalInventory";
		  final String ITM_STOCK_LEVEL = "stockLevel";
		  final String CACHE_STORE_INV = "localstore-near-local-store-inv";
		  try
		  {
			  
			if (null != getCacheContainer().get(storeIdVal + "-" + skuIdVal,
					CACHE_STORE_INV)) {

				logDebug("BBBCartFormhandler | updateLocalInventory | Coherence updating qty for"
						+ storeIdVal + "-" + skuIdVal + "is " + quantity);

				getCacheContainer().remove(storeIdVal + "-" + skuIdVal,
						CACHE_STORE_INV);
				getCacheContainer().put(storeIdVal + "-" + skuIdVal, quantity,
						CACHE_STORE_INV);

			} 
			  
			  logDebug("BBBCartFormhandler | updateLocalInventory | Repo updating qty for"
						+ storeIdVal + "-" + skuIdVal + "is " + quantity);

			  MutableRepository repository = (MutableRepository) getLocalStoreRepository();
			  MutableRepositoryItem mutItem = repository
					  .getItemForUpdate(storeIdVal
							  + BBBCoreConstants.COLON + skuIdVal,
							  ITM_DES_LOCAL_STORE_INV);
			if (null != mutItem) {
				mutItem.setPropertyValue(ITM_STOCK_LEVEL, quantity);
				repository.updateItem(mutItem);
			}  
			  
		  }
		  catch (RepositoryException e) {
			  this.logError(BBBCoreErrorConstants.ERROR_LOCALSTORE_REPO);
			  this.addFormException((new DropletException(
					  BBBCoreErrorConstants.ERROR_LOCALSTORE_REPO,
					  BBBCoreErrorConstants.ERROR_LOCALSTORE_REPO)));
		  } 
	  }

		/**
		 * Product availability and bopus exclusion check for searched Store
		 * 
		 * @param pStoreDetails
		 * @param pSiteId
		 * @param pSkuId
		 * @param pReqQty
		 * @param operation
		 * @return
		 * @throws InventoryException
		 * @throws BBBSystemException
		 * @throws BBBBusinessException
		 */
		@SuppressWarnings("unused")
		private Map<String, Integer> checkProductAvailability(
				List<StoreDetails> pStoreDetails, String pSiteId, String pSkuId,
				String pRegistryId, boolean pChangeStore, long pReqQty,
				String operation, DynamoHttpServletRequest pRequest,
				boolean isFromLocalStore) throws InventoryException,
				BBBSystemException, BBBBusinessException {
			Map<String, Integer> productAvailStatus = new HashMap<String, Integer>();
			List<String> bopusEligibleStates = null;
			List<String> bopusInEligibleStore = null;
			bopusEligibleStates = getCatalogUtil().getBopusEligibleStates(pSiteId);

			String pStoreId = getStoreManager().getStoreType(pSiteId);
			bopusInEligibleStore = getCatalogUtil().getBopusInEligibleStores(
					pStoreId, pSiteId);

			logDebug("Inside  SearchInStoreDroplet.checkProductAvailability---bopusInEligibleStore List "
					+ bopusInEligibleStore);
			logDebug("Inside  SearchInStoreDroplet.checkProductAvailability---BopusEligibleState List "
					+ bopusEligibleStates);

			List<String> storeIds = new ArrayList<String>();
			for (StoreDetails storeDetails : pStoreDetails) {
				// Check for BOPUS excluscion at State Level
				if (bopusEligibleStates == null) {
					// Store pick not available for state
					productAvailStatus.put(storeDetails.getStoreId(),
							SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
				} else if (storeDetails.getState() != null && !bopusEligibleStates.contains(storeDetails.getState().trim())) {
					// Store pick not available for state
					productAvailStatus.put(storeDetails.getStoreId(),
							SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
				} else if (bopusInEligibleStore != null
						&& bopusInEligibleStore.contains(storeDetails.getStoreId())) {
					// Store is not Bopus Eligible
					productAvailStatus.put(storeDetails.getStoreId(),
							SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
				}
				storeIds.add(storeDetails.getStoreId());

			}
			Map<String, Integer> storeInventoryMap;

			storeInventoryMap = getInventoryManager().getBOPUSProductAvailability(
					pSiteId, pSkuId, storeIds, pReqQty,
					BBBInventoryManager.STORE_STORE, getStoreInventoryContainer(),
					false, pRegistryId, pChangeStore, isFromLocalStore);

			for (String storeId : storeInventoryMap.keySet()) {
				/*
				 * if(storeInventoryMap.get(storeId)==BBBInventoryManager.LIMITED_STOCK
				 * ) { storeInventoryMap.put(storeId,
				 * BBBInventoryManager.AVAILABLE); }
				 */
				Integer boupusStatus = productAvailStatus.get(storeId);
				Integer inventoryStatus = storeInventoryMap.get(storeId);
				if (boupusStatus != null) {
					// merge the No Bopus flag with Inventory
					String mergedData = boupusStatus.toString();
					if (inventoryStatus != null) {
						mergedData += inventoryStatus.toString();
					}
					productAvailStatus.put(storeId, Integer.parseInt(mergedData));
				} else {
					productAvailStatus.put(storeId, storeInventoryMap.get(storeId));
				}
			}
			logDebug("productAvailStatus is: " + productAvailStatus);
			return productAvailStatus;
		}
		
    /** Override the handleAddItemToOrder method to check If request is from JSON JSP then don't redirect.
     *
     * @param pRequest - DynamoHttpServletRequest
     * @param pResponse - DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
    
	  @Override
	  public final boolean handleAddItemToOrder(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		  
		String reserveNowFlag = "false";
		String favStoreState = null;
	
		StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer appendData = new StringBuffer("");
		if (StringUtils.isNotEmpty(getFromPage())) {
			
			
			if(StringUtils.isNotEmpty(getSuccessQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getSuccessQueryParam());
				successURL.append(pRequest.getContextPath())
				.append(getSuccessUrlMap().get(getFromPage()))
				.append(appendData);
				errorURL.append(pRequest.getContextPath())
				.append(getErrorUrlMap().get(getFromPage()));
			}
			else if(StringUtils.isNotEmpty(getQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getQueryParam());
				
				successURL
						.append(pRequest.getContextPath())
						.append(getSuccessUrlMap().get(getFromPage()))
						.append(appendData);
				errorURL.append(pRequest.getContextPath())
						.append(getErrorUrlMap().get(getFromPage()))
						.append(appendData);
			}
			else
			{
				successURL
				.append(pRequest.getContextPath())
				.append(getSuccessUrlMap().get(getFromPage()));
				errorURL.append(pRequest.getContextPath())
				.append(getErrorUrlMap().get(getFromPage()));
			}
			setAddItemToOrderSuccessURL(successURL.toString());
			setAddItemToOrderErrorURL(errorURL.toString());
			
		}
		if ((StringUtils.isEmpty(getFromPage())) && (StringUtils.isNotEmpty(getQueryParam())))
		{
			appendData.append(getQueryParam());
			setAddItemToOrderSuccessURL(appendData.toString());
			setAddItemToOrderErrorURL(appendData.toString());
		}
		
		if (null != this.getJsonResultString()
				&& !StringUtils.isEmpty(this.getJsonResultString())) {
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(this
					.getJsonResultString());
			final DynaBean JSONResultbean = (DynaBean) JSONSerializer
					.toJava(jsonObject);

			final List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);
			if (dynaBeanProperties.contains(ADD_ITEM_RESULTS)) {
				@SuppressWarnings("unchecked")
				final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
						.get(ADD_ITEM_RESULTS);
				for (DynaBean dynaBean : itemArray) {
					List<String> dynProperty = getPropertyNames(dynaBean);
					if (dynProperty.contains(BBBCoreConstants.RESERVE_NOW)) {
						try {
							if(dynaBean.get(
									BBBCoreConstants.RESERVE_NOW) != null)
							{
							reserveNowFlag = dynaBean.get(
									BBBCoreConstants.RESERVE_NOW).toString();
							}

						} catch (Exception e) {
							logError(e.getMessage(), e);
						}
						this.setJsonResultString(JSONSerializer.toJSON(
								JSONResultbean).toString());
					
					if (dynProperty.contains(BBBCoreConstants.FAV_STORE_STATE)) {
						if (dynaBean.get(BBBCoreConstants.FAV_STORE_STATE) != null) {
							favStoreState = dynaBean.get(
									BBBCoreConstants.FAV_STORE_STATE)
									.toString();
						}
					}
					}
				}

			}
		}
		if (null != reserveNowFlag && reserveNowFlag.equalsIgnoreCase("true")) {
		 try {
			handleReserveNow(pRequest, pResponse , favStoreState);
		} catch (InventoryException e) {
			logError("Exception occurred while validating reserve now ",e);
		}
			
		}

    	final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
        final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
            Transaction tr = null;
            try {
                tr = this.ensureTransaction();
                if (this.getUserLocale() == null) {
                    this.setUserLocale(this.getUserLocale(pRequest, pResponse));
                }

                synchronized (this.getOrder()) {

                    if(isPartialAddFlow()){
                    	if(!this.preAddItemToOrderPartialAdd(pRequest, pResponse)){
                    		return false;
                    	}
                    	
                    }else{
                    	this.preAddItemToOrder(pRequest, pResponse);
                    }
                   
                    if (this.getFormError()) {
                        if (null == this.getJsonResultString()) {
                            this.checkFormRedirect(null, this.getAddItemToOrderErrorURL(), pRequest, pResponse);
                        }
                        this.markTransactionRollback();
                        return false;
                    }
                }
                @SuppressWarnings ("unchecked")
                final List<CommerceItem> comItemObjList = this.getOrder().getCommerceItems();
                int beforeSize = 0;
                if (comItemObjList != null) {
                    beforeSize = comItemObjList.size();
                }
                this.addItemToOrder(pRequest, pResponse);
                if (comItemObjList != null) {
                    final int afterSize = comItemObjList.size();
                    if ((beforeSize < afterSize) && !this.isUndoCheck()) {
                        this.setNewItemAdded(true);
                    }
                    for (final CommerceItem comItemObj : comItemObjList) {
                        if (comItemObj instanceof BBBCommerceItem) {
                            final BBBCommerceItem bbbObject = (BBBCommerceItem) comItemObj;
                            for (final AddCommerceItemInfo info : this.getItems()) {
                                if (info.getCatalogRefId().equalsIgnoreCase((bbbObject).getCatalogRefId())) {
                                	// For LTL items compare catalog ref id and shipping method both while for non ltl comparing only catalog ref id
                                	boolean compareCI = true;
                                	Boolean isLTL = false;
                                	String skuId = (bbbObject).getCatalogRefId();
                                	try {
										isLTL = getCatalogUtil().isSkuLtl(this.getSiteId(), skuId);
									} catch (BBBSystemException e1) {
						            	this.addFormException((new DropletException(e1.getMessage(), BBBCatalogErrorCodes.NO_SKU_ID_NO_SITE_ID)));
										this.logError("System exception while checking is sku LTL for skuID: "+skuId, e1);
									} catch (BBBBusinessException e1) {
										this.addFormException((new DropletException(e1.getMessage(), BBBCatalogErrorCodes.NO_SKU_ID_NO_SITE_ID)));
										this.logError("System exception while checking is sku LTL for skuID: "+skuId, e1);
									}
                                	if(isLTL) {
                                		String infoShipMethod = (String) info.getValue().get(BBBCatalogConstants.LTL_SHIP_METHOD);
										String existingCIShipMethod = ((BBBShippingGroupCommerceItemRelationship) bbbObject
												.getShippingGroupRelationships()
												.get(0)).getShippingGroup()
												.getShippingMethod();
		                        		if(null != infoShipMethod && null != existingCIShipMethod && infoShipMethod.equalsIgnoreCase(existingCIShipMethod)) {
		                        			compareCI = true;
		                        		} else {
		                        			compareCI = false;
		                        		}
		                        		if(BBBUtility.isNotEmpty(bbbObject.getRegistryId()) && 
		                        				info.getCatalogRefId().equalsIgnoreCase((bbbObject).getCatalogRefId()) && compareCI){
		                        			bbbObject.setRegistrantShipMethod(infoShipMethod);
		                        		}
                                	}
                                	if(info.getCatalogRefId().equalsIgnoreCase((bbbObject).getCatalogRefId()) && compareCI) {
                                    final String regId = (String) info.getValue().get(REGISTRY_ID);
                                    final String objRegId = bbbObject.getRegistryId();
                                    if (!StringUtils.isEmpty(objRegId) && StringUtils.isEmpty(regId)) {
                                        continue;
                                    } else if (!StringUtils.isEmpty(regId) && StringUtils.isEmpty(objRegId)) {
                                        continue;
                                    } else if (!StringUtils.isEmpty(regId) && !StringUtils.isEmpty(objRegId)
                                                    && !regId.equalsIgnoreCase(objRegId)) {
                                        continue;
                                    }
                                    final String storeId = bbbObject.getStoreId();
                                    final String stId = (String) info.getValue().get(BBBCoreConstants.STOREID);
                                    if (!StringUtils.isEmpty(storeId) && StringUtils.isEmpty(stId)) {
                                        continue;
                                    } else if (!StringUtils.isEmpty(stId) && StringUtils.isEmpty(storeId)) {
                                        continue;
                                    } else if (!StringUtils.isEmpty(stId) && !StringUtils.isEmpty(storeId)
                                                    && !stId.equalsIgnoreCase(storeId)) {
                                        continue;
                                    }
                                    if (bbbObject.getPriceInfo().isOnSale()) {
                                        bbbObject.setPrevPrice(bbbObject.getPriceInfo().getSalePrice());
                                    } else {
                                        bbbObject.setPrevPrice(bbbObject.getPriceInfo().getListPrice());
                                    }
                                    // Adding positions
                                    @SuppressWarnings ("unchecked")
                                    List<String> pos = (List<String>) pRequest.getSession().getAttribute(
                                                    BBBCoreConstants.ITEM);
                                    if (this.undoCheck) {
                                        if (beforeSize != afterSize) {
                                            if ((pos != null) && !pos.isEmpty()) {
                                                pos.add(this.getCountNo() - 1, bbbObject.getId());
                                            } else {
                                                pos = new ArrayList<String>();
                                                pos.add(bbbObject.getId());
                                                pRequest.getSession().setAttribute(BBBCoreConstants.ITEM, pos);
                                            }
                                        }
                                    } else {
                                        if (beforeSize != afterSize) {
                                            if ((pos != null) && (pos.size() > 0)) {
                                                if (StringUtils.isEmpty(bbbObject.getRegistryId())) {
                                                    pos.add(bbbObject.getId());
                                                } else {
                                                    pos.add(0, bbbObject.getId());
                                                }
                                            } else {
                                                pos = new ArrayList<String>();
                                                pos.add(bbbObject.getId());
                                                pRequest.getSession().setAttribute(BBBCoreConstants.ITEM, pos);
                                            }
                                        }
                                    }
                                    bbbObject.setMsgShownFlagOff(false);
                                    bbbObject.setMsgShownOOS(false);
                                    this.setCommerceItemId(bbbObject.getId());
                                    if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getOriginOfTraffic())) {
                                    	this.setFreeShippingBannerDetails(pRequest);
                                    }
                                    this.setQuantity(info.getQuantity());
                                    this.setItemIdJustMvBack(bbbObject.getId());
                                    try {
                                        this.getOrderManager().updateOrder(this.getOrder());

                                    } catch (final CommerceException e) {
                                        this.logError(LogMessageFormatter.formatMessage(pRequest,
                                                        BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM,
                                                        BBBCoreErrorConstants.GIFT_ERROR_1000), e);
                                        this.logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
                                    }
                                }
                            }
                        }
                    }
                    }

                }

                if (this.getFormError()) {
                    if (null == this.getJsonResultString()) {
                        this.checkFormRedirect(null, this.getAddItemToOrderErrorURL(), pRequest, pResponse);
                    }

                    this.markTransactionRollback();
                    return false;
                }
                
                //----------------------------------------------------------------------------
                String state = this.getCheckoutState().getCurrentLevel();
                if(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString().equals(state)
                		|| CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString().equals(state)
                		|| CheckoutProgressStates.DEFAULT_STATES.SP_GUEST.toString().equals(state) 
                		|| CheckoutProgressStates.DEFAULT_STATES.SP_PAYMENT.toString().equals(state))
				{
                	repriceTaxAddItemsToCart(this.getOrder());
				}
                //----------------------------------------------------------------------------
                
                
                this.postAddItemToOrder(pRequest, pResponse);
                this.updateOrder(this.getOrder(), ERROR_UPDATING_ORDER, pRequest, pResponse);
                if (this.getFormError()) {
                    if (null == this.getJsonResultString()) {
                        this.checkFormRedirect(this.getAddItemToOrderSuccessURL(), this.getAddItemToOrderErrorURL(),
                                        pRequest, pResponse);
                    }
                    this.markTransactionRollback();
                    return false;
                }
                if (this.fromCart) {
                    this.setFromWishlist(false);
                }
                if (null == this.getJsonResultString()) {
                    this.checkFormRedirect(this.getAddItemToOrderSuccessURL(), this.getAddItemToOrderErrorURL(),
                                    pRequest, pResponse);
                }
             } catch (BBBSystemException sysExcp) {
            	 logError("System exception", sysExcp);
			} catch (BBBBusinessException bsnExcp) {
				logError("Business exception", bsnExcp);
			} finally {
                // Complete the transaction
                if (tr != null) {
                    this.commitTransaction(tr);
                }
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);

            }

        }
        return this.checkFormRedirect(this.getAddItemToOrderSuccessURL(), this.getAddItemToOrderErrorURL(), pRequest,
                        pResponse);
    }
    
    
    private void repriceTaxAddItemsToCart(Order pOrder){
		this.logDebug("Entering method BBBCartFormhandler.repriceTaxAddItemsToCart()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PipelineConstants.ORDER, pOrder);
		map.put(PricingConstants.PRICING_OPERATION_PARAM,  PricingConstants.OP_REPRICE_TAX);
		map.put(PipelineConstants.ORDERMANAGER, this.getOrderManager());
		boolean isException=false;
		try {
			synchronized (pOrder) {
				PipelineResult rs = getOrderManager().getPipelineManager().runProcess("repriceAndUpdateOrder", map);
				if (rs.hasErrors()){
					isException=true;
				}
			}
		} catch (RunProcessException e) {
			logError(LogMessageFormatter.formatMessage(null, "RunProcessException in BBBCartFormhandler while repriceTaxAddItemsToCart", BBBCoreErrorConstants.ACCOUNT_ERROR_1256 ), e);
			isException=true;
			
		} 
		if(isException){
			logError("Errors in Pipeline execution for repriceAndUpdateOrder");
		}
		this.logDebug("Exiting method BBBCartFormhandler.repriceTaxAddItemsToCart()");
	}
    
    
    public final boolean handleAddRegistryItemToOrder(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBCartFormHandler ::handleAddRegistryItemToOrder method starts");
        JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(this.getJsonResultString());
        final DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
        final List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);
        if (dynaBeanProperties.contains(ADD_ITEM_RESULTS)) {
            final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean.get(ADD_ITEM_RESULTS);
            for (DynaBean dynaBean : itemArray) {
            	boolean isBts = false;
            	try {
            		isBts = isBTSProduct(dynaBean);
				} catch (Exception e) {
					logError(e.getMessage(), e);
				}
            	dynaBean.set(BTS2, Boolean.toString(isBts));
            	this.logDebug("BBBCartFormHandler ::handleAddRegistryItemToOrder isBTS for product Id " + dynaBean.get(PROD_ID).toString() + " = " + isBts);
			}
            this.setJsonResultString(JSONSerializer.toJSON(JSONResultbean).toString());
        } 
    	this.logDebug("BBBCartFormHandler ::handleAddRegistryItemToOrder method ends");
    	return this.handleAddItemToOrder(pRequest, pResponse);
    }

	/**
	 * @param dynaBean
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	protected boolean isBTSProduct(DynaBean dynaBean) throws BBBSystemException, BBBBusinessException {
		return getRestAPIManager().isBTSProduct(dynaBean.get(PROD_ID).toString(), true);
	}
    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleAjaxSetOrderByCommerceId(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logDebug("BBBCartFormHandler ::handleAjaxSetOrderByCommerceId method starts");
		this.setSetOrderSuccessURL(this.getAjaxCartOrderSuccessURL());
		this.setSetOrderErrorURL(this.getAjaxCartOrderErrorURL());
		return this.handleSetOrderByCommerceId(pRequest, pResponse);
	}
    
    
    
    
    
    
    /**
     * @param pRequest
     * @param pResponse
     * @return
     * @throws InvalidParameterException 
     * @throws CommerceItemNotFoundException 
     */
    public final boolean handleAddServiceToCommerceItem(final DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws CommerceItemNotFoundException, InvalidParameterException{
    	
    	String commerceItemId = getCommerceItemId();
    	//JSONObject jsonPayLoad=getPorchPayLoadJson();
    	JSONObject jsonPayLoad = (JSONObject) JSONSerializer.toJSON(getCartPayLoadResultString());	 
    	CommerceItem existingCommerceItem = getOrder().getCommerceItem(commerceItemId);    	
    	 ProductVO productVO = new ProductVO();
    	 RepositoryItem repItem = (RepositoryItem) existingCommerceItem.getAuxiliaryData().getProductRef();
    	 getPorchServiceManager().getPorchServiceFamilyCodes(repItem.getRepositoryId(), productVO);
    	String postalCode = (String) jsonPayLoad.get("postalCode");
    	if(!atg.core.util.StringUtils.isBlank(postalCode)){
    		getSessionBean().setPorchZipCode(postalCode);
    	}
     	String sku = existingCommerceItem.getCatalogRefId();
		String productName= ((RepositoryItem)existingCommerceItem.getAuxiliaryData().getProductRef()).getItemDisplayName();
		String productDescription=(String) ((RepositoryItem)existingCommerceItem.getAuxiliaryData().getProductRef()).getPropertyValue("description");
		//String hostURL = getHost(pRequest);
		String productURL = (String) jsonPayLoad.get("productUrl");
		//productURL = hostURL.concat(productURL);
		String quantity=String.valueOf(existingCommerceItem.getQuantity());
		jsonPayLoad.put("partnerSku", sku);
		jsonPayLoad.put("productName", productName);
		jsonPayLoad.put("productDesc", productDescription);
		jsonPayLoad.put("productUrl", productURL);
		jsonPayLoad.put("quantity", quantity);
		boolean freight = (boolean) ((CommerceItemImpl) existingCommerceItem).getPropertyValue("ltlFlag");
		jsonPayLoad.put("freight", freight);				
		RepositoryItem serviceRefItem= (RepositoryItem) getPorchServiceManager().createUpdateServiceRefPorch(jsonPayLoad,null,productVO);
		MutableRepositoryItem commerceItem = ((BaseCommerceItemImpl)existingCommerceItem).getRepositoryItem();
		commerceItem.setPropertyValue("serviceReferal", serviceRefItem);
    	return true;
    }
    
    /**
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws CommerceItemNotFoundException
     * @throws InvalidParameterException
     */
    public final boolean handleRemoveServiceFromCommerceItem(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceItemNotFoundException, InvalidParameterException
    
    {	   
		String commerceItemId = getCommerceItemId();
		CommerceItem cItem = getOrder().getCommerceItem(commerceItemId);		
		BaseCommerceItemImpl cItemimpl = (BaseCommerceItemImpl) cItem;
		if(cItemimpl.isPorchService()){		
		try {
			cItemimpl.setPorchServiceRef(null);
			cItemimpl.setPorchService(false);
		} catch (Exception e) {
			if(isLoggingError()){ 
			logDebug(" error while removing proch service ref from commerce item "+e,e);
			}
			 
		}
		}
    	return true;
    }
    
    
    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception 
     * @throws InvalidParameterException 
     * @throws CommerceItemNotFoundException 
     */
    public final boolean handleRemoveItemFromOrderForms(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceItemNotFoundException, InvalidParameterException{
    	boolean resFlag;
		this.logDebug("BBBCartFormHandler ::handleRemoveItemFromOrderForms method starts");
		StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
	   if (StringUtils.isNotEmpty(getFromPage())) {
		   successURL
			.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			this.setRemoveItemFromOrderSuccessURL(successURL.toString());
			this.setRemoveItemFromOrderErrorURL(errorURL.toString());
		}
		
		logOrderDetails("preRemoveItemFromOrder");
		
		String catRefId = null;
		if(this.getRemovalCommerceIds() != null)
		{
			BBBCommerceItem cItem = (BBBCommerceItem)getShoppingCart().getCurrent().getCommerceItem(this.getRemovalCommerceIds()[0]);
			catRefId = cItem.getCatalogRefId();
		}
		
		resFlag=this.handleRemoveItemFromOrder(pRequest, pResponse);
		logOrderDetails("postRemoveItemFromOrder"); 
		return resFlag;
	}
    
    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleMoveWishListItemToOrderForms(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logDebug("BBBCartFormHandler ::handleMoveWishListItemToOrderForms method starts");
		this.setAddItemToOrderSuccessURL(this.getAddItemToOrderFormsSuccessURL());
		this.setAddItemToOrderErrorURL(this.getAddItemToOrderFormsErrorURL());
		return this.handleMoveWishListItemToOrder(pRequest, pResponse);
	}

    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleMoveWishListItemToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logDebug("BBBCartFormHandler ::handleMoveWishListItemToOrder method starts");
		//As item to be moved to cart is from wishlist, this value is always true
		String channel = BBBUtility.getChannel();
		
		// Set below attribute to create SFL cookie in order property customizer
        if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
        	pRequest.setAttribute(BBBCoreConstants.SEND_SFL_COOKIE, Boolean.TRUE);
		}
		this.setFromWishlist(true);
		return this.handleAddItemToOrder(pRequest, pResponse);
	}

	public boolean handleUndoCartItemMove(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {

		//retrieve from json
		String savedItemIdToRemove = null;
		long qty = 0;

		BBBOrder order = (BBBOrder)getOrder();
		//Retrieve last commerceItem moved
		CommerceItem itemMoved = order.getMovedCommerceItem();

		if(getProfile().isTransient()){
			List<GiftListVO> giftLists = getSavedItemsSessionBean().getGiftListVO();
			GiftListVO giftListVO = null;
			for(GiftListVO item : giftLists){
				if(item.getWishListItemId().equals(savedItemIdToRemove)){
					giftListVO = item;
				}
			}
			if(giftListVO != null){
				if(giftListVO.getQuantity() == qty){
					giftLists.remove(giftListVO);
				} else {
					if(giftListVO.getQuantity() > qty){
						giftListVO.setQuantity(giftListVO.getQuantity() - qty);
					} else {
						giftLists.remove(giftListVO);
					}
				}
			}

		} else {
		     //User is logged-in
		     //get giftListID from user profile
			String giftListId = ((RepositoryItem) getProfile().getPropertyValue(WISH_LIST)).getRepositoryId();

			setWishListId(giftListId);
			RepositoryItem wishListItem;
			try {
				wishListItem = getGiftlistManager().getGiftitem(getWishlistItemId());

				long quantity = getGiftlistManager().getGiftlistItemQuantityDesired(getWishlistItemId());

				if(wishListItem != null)
				{
					if(quantity == qty){
						getGiftlistManager().removeItemFromGiftlist(getWishListId(), getWishlistItemId());
					} else {
						if(quantity > qty){
							getGiftlistManager().setGiftlistItemQuantityDesired(giftListId, getWishlistItemId(), quantity - qty);
						} else {
							getGiftlistManager().removeItemFromGiftlist(getWishListId(), getWishlistItemId());
						}
					}
				}
				else{
					logDebug("Wish list item is invalid or not available in the repository");
					addFormException(new DropletException(BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
				}

				getCommerceItemManager().addItemToOrder(order, itemMoved);
				
			} catch (CommerceException e) {
				this.logError("Error updating in Commerce Item in repository", e);
			} catch (RepositoryException e) {
				this.logError("Error updating in repository", e);
			}
		}

		return true;
	}

    /** handleMoveAllWishListItemsToOrder method to move all wishlist items to cart and remove them from wishlist
     *
     * @param pRequest - DynamoHttpServletRequest
     * @param pResponse - DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception Exception
     * @throws BBBBusinessException Exception */
    public final boolean handleMoveAllWishListItemsToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse)
                    throws ServletException, IOException, BBBBusinessException {

        this.logDebug("BBBCartFormHandler ::handleMoveAllWishListItemsToOrder method starts");
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
        final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
            Transaction tr = null;

            try {
                tr = this.ensureTransaction();
                if (this.getUserLocale() == null) {
                    this.setUserLocale(this.getUserLocale(pRequest, pResponse));
                }

                synchronized (this.getOrder()) {
                    final int successCount = this.preMoveAllWishListItemsToOrder(pRequest, pResponse);
                    if (successCount == 0) {
                        return false;
                    }
                }

                this.addItemToOrder(pRequest, pResponse);
                if (this.getFormError()) {
                    this.markTransactionRollback();
                    return false;
                }

                this.postMoveAllWishListItemsToOrder(pRequest, pResponse);

                this.logDebug("BBBCartFormHandler ::handleMoveAllWishListItemsToOrder method - updating order");
                this.updateOrder(this.getOrder(), ERROR_UPDATING_ORDER, pRequest, pResponse);
                if (this.getFormError()) {
                    this.markTransactionRollback();
                    return false;
                }
                if (null == this.getJsonResultString()) {
                    this.checkFormRedirect(this.getAddItemToOrderSuccessURL(), this.getAddItemToOrderErrorURL(),
                                    pRequest, pResponse);
                }
            } finally {
                // Complete the transaction
                if (tr != null) {
                    this.commitTransaction(tr);
                }
                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);

            }

        }
        this.logDebug("BBBCartFormHandler ::handleMoveAllWishListItemsToOrder method ends");
        return false;

    }

    /** preMoveAllWishListItemsToOrder method populate getItems() to move to cart.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return count of items to move to cart
     * @throws ServletException Exception
     * @throws IOException Exception
     * @throws BBBBusinessException Exception */
    public int preMoveAllWishListItemsToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse)
                    throws ServletException, IOException, BBBBusinessException {

        this.logDebug("BBBCartFormHandler ::preMoveAllWishListItemsToOrder method starts");
        int successCount = 0;

        this.logDebug("BBBCartFormHandler ::preMoveAllWishListItemsToOrder method - get wishlist items");
        final WishListVO wishListItems = this.getWishlistManager().getWishListItems();
        if (wishListItems != null) {
            @SuppressWarnings ("unchecked")
            final List<GiftListVO> itemArray = wishListItems.getWishListItems();
            if (itemArray != null) {
                final AddCommerceItemInfo[] newArray = new AddCommerceItemInfo[itemArray.size()];
                successCount = this.populateMoveAllWishListItemsInfo(itemArray, newArray, pRequest, pResponse);
                this.setAddItemCount(successCount);
                for (int i = 0; i < successCount; i++) {

                    final AddCommerceItemInfo bbbItemInfo = newArray[i];
                    this.quantityAdded = this.quantityAdded + bbbItemInfo.getQuantity();
                    this.getItems()[i] = bbbItemInfo;
                }
            }
        }
        this.logDebug("BBBCartFormHandler ::preMoveAllWishListItemsToOrder method ends");
        return successCount;

    }

    /** populateMoveAllWishListItemsInfo method validates SKU info Inventory List price
     *
     * @param itemArray
     * @param itemInfos
     * @param pRequest
     * @param pResponse
     * @return count of wishlist items which can be move to cart
     * @throws IOException
     * @throws ServletException
     * @throws BBBBusinessException */
    public final int populateMoveAllWishListItemsInfo(final List<GiftListVO> itemArray,
                    final AddCommerceItemInfo[] itemInfos, final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse)
                    throws IOException, ServletException, BBBBusinessException {
        this.logDebug("BBBCartFormHandler ::populateMoveAllWishListItemsInfo method starts");
        int successCount = 0;
        boolean exception;
        final HashMap<String, String> error = new HashMap<String, String>();
        final List<String> idsToRemove = new ArrayList<String>();
        for (final GiftListVO item : itemArray) {
            exception = false;
            final AddCommerceItemInfo wishlistItemInfo = new AddCommerceItemInfo();
            wishlistItemInfo.setCatalogRefId(item.getSkuID());
            wishlistItemInfo.setProductId(item.getProdID());
            wishlistItemInfo.setQuantity(item.getQuantity());
            wishlistItemInfo.setGiftlistItemId(item.getWishListItemId());

            this.logDebug("BBBCartFormHandler ::populateMoveAllWishListItemsInfo method - Validating Sku Info");
            this.validateSkuInfo(wishlistItemInfo.getCatalogRefId(), this.getSiteId(), pRequest, pResponse);
            if (!this.getFormError() && (wishlistItemInfo.getValue() != null)) {
                if (!this.fromPipeline && !this.getFormError()) {
                    this.validateInventory(this.getSiteId(), wishlistItemInfo, this.getOrder(), pRequest, pResponse);
                    if (!this.getFormError()) {
                        String siteName = null;
                        try {
                            siteName = SiteContextManager.getCurrentSiteId();
                            final double price = this.getPricingManager().getListPriceBySite(siteName,
                                            wishlistItemInfo.getProductId(), wishlistItemInfo.getCatalogRefId());
                            if (Double.compare(price, 0.0) == BBBCoreConstants.ZERO) {
                                error.put(wishlistItemInfo.getCatalogRefId(),
                                                BBBCoreErrorConstants.ERR_SKU_PRICE_NOT_FOUND
                                                                + PRICE_IS_NOT_SPECIFIED_FOR_THE_SKU);
                                exception = true;
                            }
                        } catch (final BBBSystemException e) {
                            this.logError("System exception while checking price list for sku", e);
                            error.put(wishlistItemInfo.getCatalogRefId(), BBBCoreErrorConstants.ERR_SKU_PRICE_NOT_FOUND
                                            + PRICE_IS_NOT_SPECIFIED_FOR_THE_SKU);
                            exception = true;
                        }
                    }
                }

            }
            if (!exception) {
                if (this.getFormError()) {
                    error.put(wishlistItemInfo.getCatalogRefId(), this.getFormExceptions().get(0).toString());
                    this.resetFormExceptions();
                } else {
                    idsToRemove.add(item.getWishListItemId());
                    itemInfos[successCount] = wishlistItemInfo;
                    successCount++;
                }
            }

        }
        this.setWishListItemIdsToRemove(idsToRemove);
        this.setMoveAllItemFailureResult(error);
        return successCount;
    }

    /**
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean preAddItemToOrderPartialAdd(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		try {
			if (null != this.getJsonResultString()) {
				this.addItemJSONObjectParser(this.getJsonResultString());
			}
		} catch (final BBBBusinessException e) {

			// Here we will set send error code in responseVO
			this.logError(BBBCoreErrorConstants.CART_ERROR_1013
					+ ": BBBBusinessException", e);
			MultipleIteminResponseVO itemResponseVo = new MultipleIteminResponseVO();
			itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1013
					+ e.getMessage());
			itemResponseVo.setErrorMessage(e.getMessage());

		}

		if ((this.getItems() == null)
				|| (this.getItems().length == 0)
						) {
			return false;
		}else{
			return true;
		}
		
		

	}

    
    /** Override the preAddItemToOrder method to check. If jsEnabled validateSkuInfo validateRegistryInfo
     * validateStoreInfo checkInventory
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
    @Override
    public void preAddItemToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        try {
            if (null != this.getJsonResultString()) {
                this.addItemJSONObjectParser(this.getJsonResultString());
            }
        } catch (final BBBBusinessException e) {
            this.logError(BBBCoreErrorConstants.CART_ERROR_1013 + ": BBBBusinessException", e);
        }
        this.logDebug((new StringBuilder()).append("preAddItemToOrder : Total Number of items : ")
                        .append((this.getItems()!=null)?(this.getItems().length):" item is null ").toString());

        //Story #0.3 DSK/Mob - Allow Recognized users an ability to Add to Cart changes start
        String siteId = SiteContextManager.getCurrentSiteId();
        if (!BBBUtility.siteIsTbs(siteId) && ((BBBProfileTools) getCommerceProfileTools()).isRecognizedUser(pRequest, getProfile())) {
        	try {
        		checkCartItemCount(pRequest);
        	} catch (BBBBusinessException be) {
        		logError(be.getMessage());
        		// form exception has been add in this manner as addformException has been overridden already
        		getFormExceptions().addElement((new DropletException(be.getMessage())));
        		return;
        	}
        }
        //Story #0.3 DSK/Mob - Allow Recognized users an ability to Add to Cart changes end
        
        //start hyd-708 null pointer check fix
        if(this.getItems() == null && ((StringUtils.isEmpty(this.getWishlistItemId()) || !this.isRestService()))){
    		this.getFormExceptions().addElement(this.createDropletException(NULL_SKU_ID, NULL_SKU_ID));
    		return;
    	}else if ((this.getItems() == null)
                        || ((this.getItems().length == 0) && !StringUtils.isEmpty(this.getWishlistItemId()) && (this
                                        .getJsonResultString() == null))) {
            this.setAddItemCount(1);
            this.populateAddItemInfo(null, this.getItems());
        }
        
        //end hyd-708 null pointer check fix
        for (int i = 0; i < this.getItems().length; i++) {
            final AddCommerceItemInfo bbbItemInfo = this.getItems()[i];
            this.quantityAdded = this.quantityAdded + bbbItemInfo.getQuantity();
           /* if (this.isFromWishlist() && bbbItemInfo.getValue().get("shipMethodUnsupported") != null) {
            String shippingMethodUnsupported = (String) bbbItemInfo.getValue().get("shipMethodUnsupported");
            if(shippingMethodUnsupported.equalsIgnoreCase("true")) {
            	this.getItems()[i].getValue().put("prevLtlShipMethod",bbbItemInfo.getValue().get(BBBCatalogConstants.LTL_SHIP_METHOD));
            
            }
            }*/
            this.validateSkuInfo(bbbItemInfo.getCatalogRefId(), this.getSiteId(), pRequest, pResponse);

            if (!this.getFormError() && (bbbItemInfo.getValue() != null)) {
                if (!this.fromPipeline && !StringUtils.isEmpty((String) bbbItemInfo.getValue().get(REGISTRY_ID))) {
                    this.validateRegistryInfo((String) bbbItemInfo.getValue().get(REGISTRY_ID), this.getSiteId(),
                                    pRequest, pResponse);
                }
                if (StringUtils.isNotEmpty((String) bbbItemInfo.getValue().get(STORE_ID))) {
                    this.validateStoreInfo((String) bbbItemInfo.getValue().get(STORE_ID), pRequest, pResponse);
                }
                if (((BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) && this.fromPipeline) || !this.fromPipeline ) && !this.getFormError()) {
                    this.validateInventory(this.getSiteId(), bbbItemInfo, this.getOrder(), pRequest, pResponse);
                }
            }
        }
    }
    
    /**
	 * @param siteId
	 * @param itemInfo
	 * @param order
	 * @param itemResponseVo
	 * @return
	 */
	public final boolean validateInventoryForPartialAdd(final String siteId,
			final String storeId, final String skuId, final String registryId,  final Long qty, final Order order,
			MultipleIteminResponseVO itemResponseVo){

		int inventoryStatus = BBBInventoryManager.AVAILABLE;
		boolean validInventory = true;
		final BBBPurchaseProcessHelper bbbHelper = (BBBPurchaseProcessHelper) this
				.getPurchaseProcessHelper();
		long rolledUpQty = 0;
		final BBBOrderImpl bbOrder = (BBBOrderImpl) this.getOrder();
		try {
			rolledUpQty = bbbHelper.getRollupQuantities(storeId, skuId,
					bbOrder, qty, null);

			if (StringUtils.isNotBlank(storeId)) {
				// Item is added from Gift Registry
				inventoryStatus = bbbHelper.checkCachedInventory(this.getSiteId(), skuId,storeId,this.getOrder(), 0,BBBInventoryManager.ADD_ITEM_FROM_REG,
						this.getStoreInventoryContainer(),
						BBBInventoryManager.AVAILABLE);
			} else {
				inventoryStatus = bbbHelper.checkCachedInventory(
						this.getSiteId(), skuId,
						storeId,
						this.getOrder(), rolledUpQty,
						BBBInventoryManager.ADD_ITEM,
						this.getStoreInventoryContainer(),
						BBBInventoryManager.AVAILABLE);
			}

		} catch (final CommerceException e) {

			logError(BBBCoreErrorConstants.CART_ERROR_1003
					+ ERROR_INVENTORY_CHECK);
			this.logDebug("Error occoured while inventory check, Making inventory Status as available.");
			inventoryStatus = BBBInventoryManager.AVAILABLE;
			itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1003);
			itemResponseVo.setErrorMessage(ERROR_INVENTORY_CHECK);
			validInventory = false;
		}

		if (inventoryStatus != BBBInventoryManager.AVAILABLE) {

			logError(BBBCoreErrorConstants.CART_ERROR_1009 + OUT_OF_STOCK);
			itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1009);
			itemResponseVo.setErrorMessage(OUT_OF_STOCK_ITEM);
			validInventory = false;

		}

		return validInventory;
	}
    

    /** @param siteId Site ID
     * @param itemInfo Item Information
     * @param order Order
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse */
    public void validateInventory(final String siteId, final AddCommerceItemInfo itemInfo, final Order order,
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
        int inventoryStatus = BBBInventoryManager.AVAILABLE;
        final BBBPurchaseProcessHelper bbbHelper = (BBBPurchaseProcessHelper) this.getPurchaseProcessHelper();
        long rolledUpQty = 0;
        final BBBOrderImpl bbOrder = (BBBOrderImpl) this.getOrder();
        try {
            rolledUpQty = bbbHelper.getRollupQuantities((String) itemInfo.getValue().get(STORE_ID),
                            itemInfo.getCatalogRefId(), bbOrder, itemInfo.getQuantity(), (String) itemInfo.getValue().get(REGISTRY_ID));

            if (!BBBUtility.isEmpty((String) itemInfo.getValue().get(REGISTRY_ID))) {
                // Item is added from Gift Registry
                inventoryStatus = bbbHelper.checkCachedInventory(this.getSiteId(), itemInfo.getCatalogRefId(),
                                (String) itemInfo.getValue().get(STORE_ID), this.getOrder(), rolledUpQty,
                                BBBInventoryManager.ADD_ITEM_FROM_REG, this.getStoreInventoryContainer(),
                                BBBInventoryManager.AVAILABLE);
            } else {
                inventoryStatus = bbbHelper.checkCachedInventory(this.getSiteId(), itemInfo.getCatalogRefId(),
                                (String) itemInfo.getValue().get(STORE_ID), this.getOrder(), rolledUpQty,
                                BBBInventoryManager.ADD_ITEM, this.getStoreInventoryContainer(),
                                BBBInventoryManager.AVAILABLE);
            }
            
            if (inventoryStatus == BBBInventoryManager.LIMITED_STOCK && !BBBUtility.isEmpty((String) itemInfo.getValue().get(STORE_ID))) {
            	if (!BBBUtility.isMapNullOrEmpty(this.getStoreInventoryContainer().getStoreIdInventoryMap())) {
            		Integer invCount = this.getStoreInventoryContainer().getStoreIdInventoryMap().get((String) itemInfo.getValue().get(STORE_ID) + "|" + itemInfo.getCatalogRefId()) - Integer.parseInt(Long.toString(rolledUpQty));
  				  getCacheContainer().put((String) itemInfo.getValue().get(STORE_ID) + BBBCoreConstants.HYPHEN + itemInfo.getCatalogRefId(), invCount , BBBCoreConstants.CACHE_STORE_INV);
  				  }
            	}
            	

        } catch (final CommerceException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_INVENTORY_CHECK,
                            BBBCoreErrorConstants.CART_ERROR_1003), e);
            this.logDebug("Error occoured while inventory check, Making inventory Status as available.");
            inventoryStatus = BBBInventoryManager.AVAILABLE;
        }
        
        if (inventoryStatus != BBBInventoryManager.AVAILABLE && inventoryStatus != BBBInventoryManager.LIMITED_STOCK && !(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) && this.fromPipeline)) {

            this.logError(LogMessageFormatter.formatMessage(pRequest, OUT_OF_STOCK,
                            BBBCoreErrorConstants.CART_ERROR_1009));
            this.getFormExceptions().addElement(this.createDropletException(OUT_OF_STOCK_ITEM, OUT_OF_STOCK_ITEM));
        }
    }

    /**
	 * @param pSkuId
	 * @param pSiteId
	 * @param itemResponseVo
	 * @return
	 * @throws SystemException
	 */
	public final boolean validateSkuInfoForPartialAdd(final String pSkuId,
			final String pSiteId, MultipleIteminResponseVO itemResponseVo) {
		SKUDetailVO skuDetailVO = null;
		boolean validSku = true;

		this.logDebug((new StringBuilder())
				.append("validateSkuInfo() pSkuId : ").append(pSkuId)
				.toString());

		if(StringUtils.isBlank(pSkuId)){
			itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1007);
			itemResponseVo.setErrorMessage(NULL_SKU_ID);
			return false;
		}else {

			try {
				skuDetailVO = this.getCatalogUtil().getSKUDetails(pSiteId,
						pSkuId, false, true, true);

			} catch (final BBBSystemException e) {
				logError("Exception in validating sku Info for partial Add"+e.getMessage());
				itemResponseVo.setErrorCode("123");
				itemResponseVo.setErrorMessage(e.getMessage());
				validSku = false;

			} catch (final BBBBusinessException e) {
				logError("Exception in validating sku Info for partial Add"+e.getMessage());
				itemResponseVo.setErrorCode("124");
				itemResponseVo.setErrorMessage(e.getMessage());
				validSku = false;
			}
			
			if (null == skuDetailVO) {
				logError(NULL_SKU_ID);
				itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1007);
				itemResponseVo.setErrorMessage(NULL_SKU_ID);
				validSku = false;
			}
		}
		
		return validSku;
	}

    /** Validates if the input skuId is valid or not.
     *
     * @param pSkuId SKU ID
     * @param pSiteId Site ID
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse */
    public final void validateSkuInfo(final String pSkuId, final String pSiteId,
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
        SKUDetailVO skuDetailVO = null;

        this.logDebug((new StringBuilder()).append("validateSkuInfo() pSkuId : ").append(pSkuId).toString());

        if (null != pSkuId) {

            try {
                skuDetailVO = this.getCatalogUtil().getSKUDetails(pSiteId, pSkuId, false, true, true);

            } catch (final BBBSystemException e) {
                this.addFormException(this.createDropletException(INVALID_SKU_ID, INVALID_SKU_ID));

                this.logError(LogMessageFormatter.formatMessage(pRequest,
                                "BBBSystem Exception from validateSkuInfo of BBBCartFormhandler",
                                BBBCoreErrorConstants.CART_ERROR_1014), e);

            } catch (final BBBBusinessException e) {
                this.addFormException(this.createDropletException(INVALID_SKU_ID, INVALID_SKU_ID));

                this.logError(BBBCoreErrorConstants.CART_ERROR_1015
                                + ": BBBSystem Exception from validateSkuInfo of BBBCartFormhandler "
                                + "SKU is invalid: " + pSkuId, e);

            }

            if (null == skuDetailVO) {

                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_SKU_ID,
                                BBBCoreErrorConstants.CART_ERROR_1001));

                this.addFormException(this.createDropletException(INVALID_SKU_ID, INVALID_SKU_ID));
            }

        } else {

            this.logError(LogMessageFormatter.formatMessage(pRequest, NULL_SKU_ID,
                            BBBCoreErrorConstants.CART_ERROR_1007));

            this.addFormException(this.createDropletException(NULL_SKU_ID, NULL_SKU_ID));
        }
    }
    
    /**
	 * @param pRegistryId
	 * @param pSiteId
	 * @param itemResponseVo
	 * @return
	 */
	public final boolean validateRegistryInfoForPartialAdd(final String pRegistryId,
			final String pSiteId,MultipleIteminResponseVO itemResponseVo) {
		RegistrySummaryVO registrySummaryVO = null;

		boolean validRegistry = true;

		this.logDebug((new StringBuilder())
				.append("validateRegistryInfo: RegistryId: ")
				.append(pRegistryId).toString());
		if (null != pRegistryId) {
			try {
				registrySummaryVO = this.getRegistryManager().getRegistryInfo(
						pRegistryId, pSiteId);
			} catch (final BBBBusinessException e) {

				logError(BBBCoreErrorConstants.CART_ERROR_1016+INVALID_REGISTRY_ID);
				itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1016);
				itemResponseVo.setErrorMessage(INVALID_REGISTRY_ID);
				validRegistry = false;

			} catch (final BBBSystemException e) {

				logError(BBBCoreErrorConstants.CART_ERROR_1016+INVALID_REGISTRY_ID);
				itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1016);
				itemResponseVo.setErrorMessage(INVALID_REGISTRY_ID);
				validRegistry = false;
			}

			if (registrySummaryVO == null) {

				logError(BBBCoreErrorConstants.CART_ERROR_1016+INVALID_REGISTRY_ID);
				itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1016);
				itemResponseVo.setErrorMessage(INVALID_REGISTRY_ID);
				validRegistry = false;

			} else {
				try {
					final String countryName = this.getCatalogUtil()
							.getDefaultCountryForSite(pSiteId);
					registrySummaryVO.getShippingAddress().setCountry(
							countryName);
				} catch (final BBBSystemException e) {

					logError(BBBCoreErrorConstants.CART_ERROR_1025+DEFAULT_COUNTRY_MISSING_FOR_SITE);
					itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1025);
					itemResponseVo.setErrorMessage(DEFAULT_COUNTRY_MISSING_FOR_SITE);
					validRegistry = false;

				} catch (final BBBBusinessException e) {

					logError(BBBCoreErrorConstants.CART_ERROR_1025+DEFAULT_COUNTRY_MISSING_FOR_SITE);
					itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1025);
					itemResponseVo.setErrorMessage(DEFAULT_COUNTRY_MISSING_FOR_SITE);
					validRegistry = false;
				}
				((BBBOrderImpl) this.getOrder()).getRegistryMap().put(
						pRegistryId, registrySummaryVO);
			}

		} else {

			logError(BBBCoreErrorConstants.CART_ERROR_1006+NULL_REGISTRY_ID);
			itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1006);
			itemResponseVo.setErrorMessage(NULL_REGISTRY_ID);
			validRegistry = false;

		}

		return validRegistry;
	}

	/**Method to associate registry context with cart for desktop
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return boolean
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 */
	public boolean handleAssociateRegistryContextWithCart(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBSystemException{
		 String buyOffSkuId=this.getBuyOffAssociationSkuId();
		 this.logDebug("associateRegistryContextWithCart: pSkuId: "+ buyOffSkuId);
		 final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		 RegistrySummaryVO regVO = sessionBean.getBuyoffStartBrowsingSummaryVO();
		 String regId = sessionBean.getBuyoffStartBrowsingSummaryVO().getRegistryId();		 
		 this.logDebug("associateRegistryContextWithCart: RegId: "+ regId);
		String siteId = SiteContextManager.getCurrentSiteId();
		if (!BBBUtility.siteIsTbs(siteId) && StringUtils.isNotEmpty(getFromPage())) {
			setAssociateRegistryContextSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
			setAssociateRegistryContextErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
		}
		 boolean errorFlag = false;
		 final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
			final String myHandleMethod = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
				BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER,
						myHandleMethod);
				Transaction tr = null;
				try {
					tr = this.ensureTransaction();
					if (this.getUserLocale() == null) {
						this.setUserLocale(this.getUserLocale(pRequest, pResponse));
					}

					
					synchronized (this.getOrder()) {
						errorFlag = this.associateRegistryContext(buyOffSkuId,regId);
					}
				} catch (Exception exp) {
					logError("exception", exp);
				} finally {
					// Complete the transaction
					if (tr != null) {
						this.commitTransaction(tr);
					}
					if (rrm != null) {
						rrm.removeRequestEntry(myHandleMethod);
					}
					BBBPerformanceMonitor.end(
							BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
				}
			}
		 if(!errorFlag){
			 return checkFormRedirect (this.getAssociateRegistryContextSuccessURL(), this.getAssociateRegistryContextErrorURL(), pRequest, pResponse);
		 } else {
			 return errorFlag;
		 }
		 
	}
	
	/**Method to associate registry context with cart for mobile
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return boolean
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 */ 
	 public boolean handleAssociateContext(
				final DynamoHttpServletRequest pRequest,
				final DynamoHttpServletResponse pResponse)
				throws BBBSystemException, ServletException, IOException {
			boolean errorFlag=false;
			final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
			final String myHandleMethod = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
				BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER,
						myHandleMethod);
				Transaction tr = null;
				try {
					tr = this.ensureTransaction();
					if (this.getUserLocale() == null) {
						this.setUserLocale(this.getUserLocale(pRequest, pResponse));
					}

					
					synchronized (this.getOrder()) {

						String buyOffSkuId = this.getBuyOffAssociationSkuId();
						this.logDebug("Associate context with cart for mobile: pSkuId: "
								+ buyOffSkuId);
						String regId = this.getRegistryId();
						this.logDebug("Associate context with cart for mobile: RegistryId: "
								+ regId);
						errorFlag = this.associateRegistryContext(
								buyOffSkuId, regId);
						
					}
				} catch (Exception exp) {
					logError("exception", exp);
				} finally {
					// Complete the transaction
					if (tr != null) {
						this.commitTransaction(tr);
					}
					if (rrm != null) {
						rrm.removeRequestEntry(myHandleMethod);
					}
					BBBPerformanceMonitor.end(
							BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
				}
			}
			return errorFlag;
		}
	
	 /**
	  * 
	  * @param skuid
	  * @param regId
	  * @return
	  * @throws BBBSystemException
	 * @throws RunProcessException 
	  */
	 public boolean associateRegistryContext(String skuid,String regId) throws BBBSystemException,ServletException, IOException{
		 this.setBuyOffFlag(true);
		 String siteId =  SiteContextManager.getCurrentSiteId();

		 final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		 final BBBSessionBean sessionBean = (BBBSessionBean) request
				 .resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		 RegistrySummaryVO regVO=sessionBean.getBuyoffStartBrowsingSummaryVO();
		 if(null == regVO){
			 try {
				 regVO = this.getRegistryManager().getRegistryInfo(regId, siteId);
			 } catch (BBBBusinessException e) {
				 logError("Exception occured while fetching registry vo" ,e);
				 addFormException(new DropletException(e.getMessage()));
			 }
		 }
		 final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		 validateRegistryInfo(regId, siteId, request, response);    
		 List<CommerceItem> allCommItems;
		 Object comItemObj;
		 Order order = this.getOrder();
		 try {
			 comItemObj =  this.getOrder().getCommerceItem(skuid);
			 String associateItemSkuId = ((BBBCommerceItem) comItemObj).getCatalogRefId();
			 String commerceItemId = ((BBBCommerceItem) comItemObj).getId();
			 allCommItems = this.getOrder().getCommerceItems();
			// BBBP-909
			 for(CommerceItem commItem : allCommItems){
				 if (commItem instanceof BBBCommerceItem) {
					 BBBCommerceItem bbbCommItem = (BBBCommerceItem) commItem;
					 request.setParameter(bbbCommItem.getId(), bbbCommItem.getQuantity());
				 }
			 }
			 String lTLHighestShipMethodId = "";
			 String commShipMethod = ((BBBCommerceItem) comItemObj).getLtlShipMethod();
			 boolean ltlItemFlag = ((BBBCommerceItem) comItemObj).isLtlItem();
			 if (comItemObj != null && comItemObj instanceof BBBCommerceItem) { 
					 BBBCommerceItem bbbComItemObj = (BBBCommerceItem) comItemObj;
					 String checkShipMethod=null,assemblyCheck="false";
					 if(ltlItemFlag){
						 this.logDebug("LTL item found");
						 List<ShipMethodVO> lTLEligibleShippingMethods = getCatalogUtil().getLTLEligibleShippingMethods(associateItemSkuId, siteId, this.getUserLocale(request, response).getLanguage());
						 if(!BBBUtility.isListEmpty(lTLEligibleShippingMethods)){
							 ShipMethodVO lTLHighestShipMethod = lTLEligibleShippingMethods.get(lTLEligibleShippingMethods.size()-1);
							 lTLHighestShipMethodId = lTLHighestShipMethod.getShipMethodId();
						 }
						 if(lTLHighestShipMethodId.equalsIgnoreCase(BBBCoreConstants.LWA)){
							 checkShipMethod=BBBCoreConstants.LW;
							 assemblyCheck="true";
						 }
						 else{
							 checkShipMethod=lTLHighestShipMethodId;
						 }
						 // BBBP-910, BBBP-911
						 if(!BBBUtility.isEmpty(bbbComItemObj.getWhiteGloveAssembly()) && commShipMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD) && (bbbComItemObj.getWhiteGloveAssembly().equalsIgnoreCase(BBBCoreConstants.TRUE))){
							 commShipMethod = BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD;
						 }
					 }
						 for (CommerceItem comItem : allCommItems) {
							 if (comItem instanceof BBBCommerceItem) {
								 BBBCommerceItem bbbComItem = (BBBCommerceItem) comItem;
								 boolean ltlFlag = bbbComItem.isLtlItem();
								 if(!ltlFlag && bbbComItem.getCatalogRefId().equalsIgnoreCase(associateItemSkuId) && null != bbbComItem.getRegistryId() && null == bbbComItemObj.getStoreId() && null == bbbComItem.getStoreId()){
									 if(bbbComItem.getRegistryId().equalsIgnoreCase(regId)){
										 long updatedQuantity = bbbComItem.getQuantity() + bbbComItemObj.getQuantity();
										 request.setParameter(bbbComItem.getId(), updatedQuantity);
										 request.setParameter(bbbComItemObj.getId(), 0);
										 this.setSetOrderSuccessURL(null);
										 this.setSetOrderErrorURL(null);
										 // BBBP-909
										 this.handleSetOrderByCommerceId(request, response);
										 setRegistryContextInCommerceItem(regId, regVO, bbbComItem);
										 this.setBuyOffDuplicateItemFlag(true);
										 break;
									 }
								 } else if (ltlItemFlag && ltlFlag && BBBUtility.isNotEmpty(bbbComItem.getLtlShipMethod())
										 && bbbComItem.getLtlShipMethod().equalsIgnoreCase(checkShipMethod) 
										 && bbbComItem.getCatalogRefId().equalsIgnoreCase(associateItemSkuId) 
										 && null != bbbComItem.getRegistryId() && bbbComItem.getRegistryId().equalsIgnoreCase(regId) 
										 && assemblyCheck.equalsIgnoreCase(bbbComItem.getWhiteGloveAssembly())) {
									 	if((!BBBCoreConstants.TRUE.equalsIgnoreCase(bbbComItem.getWhiteGloveAssembly()) && assemblyCheck.equalsIgnoreCase(BBBCoreConstants.FALSE)) ||
											 (BBBUtility.isNotEmpty(bbbComItem.getWhiteGloveAssembly()) && bbbComItem.getWhiteGloveAssembly().equalsIgnoreCase(BBBCoreConstants.TRUE) 
											 && assemblyCheck.equalsIgnoreCase(BBBCoreConstants.TRUE))){
											 long updatedQuantity = bbbComItem.getQuantity() + bbbComItemObj.getQuantity();
											 request.setParameter(bbbComItem.getId(), updatedQuantity);
											 request.setParameter(bbbComItemObj.getId(), 0);
											 this.setSetOrderSuccessURL(null);
											 this.setSetOrderErrorURL(null);
											 // BBBP-909
											 this.handleSetOrderByCommerceId(request, response);
											 setRegistryContextInCommerceItem(regId, regVO, bbbComItem);
											 this.setBuyOffDuplicateItemFlag(true);
											 break;
									 }
						} else if (!ltlFlag && bbbComItem.getCatalogRefId().equalsIgnoreCase(associateItemSkuId)
								&& null != bbbComItem.getRegistryId() && null != bbbComItemObj.getStoreId()
								&& null != bbbComItem.getStoreId() && bbbComItem.getStoreId().equals(bbbComItemObj.getStoreId())) {
							// BBBP-4026
							this.logDebug("BBBCartFormHandler.associateRegistryContext BOPUS Scenario observed.");
							if (bbbComItem.getRegistryId().equalsIgnoreCase(
									regId)) {
								long updatedQuantity = bbbComItem.getQuantity() + bbbComItemObj.getQuantity();
								request.setParameter(bbbComItem.getId(), updatedQuantity);
								request.setParameter(bbbComItemObj.getId(), 0);
								this.setSetOrderSuccessURL(null);
								this.setSetOrderErrorURL(null);
								this.handleSetOrderByCommerceId(request, response);
								setRegistryContextInCommerceItem(regId, regVO, bbbComItem);
								this.setBuyOffDuplicateItemFlag(true);
								break;
							}
						}
						// BBBP-4026
							 }
						 }
					 if(isBuyOffDuplicateItemFlag() == false){
			
						 try {
							  order = getOrder();
							 if (order == null) {
								 String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, request, response);
								 throw new ServletException(msg);
							 }
								 if(((BBBCommerceItem) comItemObj).getReferenceNumber()!=null){
									 try {
										getEximManager().setModerateImageURL(((BBBCommerceItem) comItemObj));
										this.setImageURL(((BBBCommerceItem) comItemObj).getFullImagePath());
									} catch (BBBBusinessException e) {
										logError("Unable to set the moderated image in the order",e);
									}
								 }
							
						 setRegistryContextInCommerceItem(regId, regVO,	bbbComItemObj);
						 // added for story BBBP-503
						
							 this.logDebug("Highest shipping method of sku" + lTLHighestShipMethodId);
							 if((null != lTLHighestShipMethodId && !lTLHighestShipMethodId.equalsIgnoreCase(commShipMethod) && !StringUtils.isEmpty(lTLHighestShipMethodId))){
								 List<BBBShippingGroupCommerceItemRelationship> shippingGroupCommerceItemRelationships = bbbComItemObj.getShippingGroupRelationships();
								 for(BBBShippingGroupCommerceItemRelationship shippingGroupCommerceItemRelationship : shippingGroupCommerceItemRelationships){
									 ShippingGroup shipGrp = shippingGroupCommerceItemRelationship.getShippingGroup();
									 shipGrp.setShippingMethod(lTLHighestShipMethodId);
										 ((BBBCommerceItem)comItemObj).setWhiteGloveAssembly(String.valueOf(Boolean.FALSE));
									 ((BBBOrderManager)getOrderManager()).createDeliveryAssemblyCommerceItems((BBBCommerceItem)comItemObj, shipGrp, order);
									 ((BBBCommerceItem)comItemObj).setShipMethodUnsupported(false);
								 }
								 bbbComItemObj.setLtlShipMethod(lTLHighestShipMethodId);
								 if(lTLHighestShipMethodId.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
									 bbbComItemObj.setWhiteGloveAssembly(BBBCoreConstants.TRUE);
									 String assemblyCommItemId = ((BBBCommerceItem)order.getCommerceItem(commerceItemId)).getAssemblyItemId();
									 BBBCommerceItem comitemWithAss = (BBBCommerceItem) order.getCommerceItem(commerceItemId);
									 ShippingGroup shipGrp = ((BBBShippingGroupCommerceItemRelationship) bbbComItemObj.getShippingGroupRelationshipContainer().getShippingGroupRelationships().get(0)).getShippingGroup();
									 if(BBBUtility.isEmpty(assemblyCommItemId)){
										 CommerceItemShippingInfo asscisi = new CommerceItemShippingInfo();
										 assemblyCommItemId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLAssemblyFeeSku(order, shipGrp, order.getSiteId(), comitemWithAss);
										 comitemWithAss.setAssemblyItemId(assemblyCommItemId);
										 asscisi.setCommerceItem(order.getCommerceItem(assemblyCommItemId));
										 asscisi.setRelationshipType(BBBCoreConstants.SHIPPINGQUANTITY);
										 asscisi.setShippingGroupName(this.getShippingGroupMapContainer().getDefaultShippingGroupName());
										 asscisi.setQuantity(comitemWithAss.getQuantity());
										 asscisi.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
										 this.getCommerceItemShippingInfoContainer().addCommerceItemShippingInfo(assemblyCommItemId, asscisi);
										 shipGrp.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
										 bbbComItemObj.setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
									 }
								 }
							 }
							 runProcessRepriceOrder(getAddItemToOrderPricingOp(), this.getOrder(), this.getUserPricingModels(), this.getUserLocale(), this.getProfile(), this.createRepriceParameterMap());
							 getOrderManager().updateOrder(getOrder());
							
			 } catch(CommerceException ex) {
				 processException(ex, MSG_ERROR_UPDATE_ORDER, request, response);
				 return false;  
			 } catch (RunProcessException e) {
				 processException(e, MSG_ERROR_UPDATE_ORDER, request, response);
				 return false;  
			} 
				 }
				BaseCommerceItemImpl cItemimpl = (BaseCommerceItemImpl) comItemObj;
				if(cItemimpl.isPorchService()){		
				 
						cItemimpl.setPorchServiceRef(null);
						cItemimpl.setPorchService(false);
						getSessionBean().setRegistryPorchServiceRemoved(true);			}
						 
			 return true;
		 }  
		 }
		 catch (CommerceItemNotFoundException e) {
			 logError("Exception occured while fetching Commerce Item from order" ,e);
			 addFormException(new DropletException(e.getMessage()));
		 } catch (InvalidParameterException e) {
			 logError("Invalid parameter is passed while fecthing Commerce Item from order" ,e);
			 addFormException(new DropletException(e.getMessage()));
		 }catch (final CommerceException e) {
			 String msg = "Commerce Exception while updating the Order after associating registry context for ["+skuid+"]";
			 addFormException(new DropletException(e.getMessage()));
		 } catch (BBBBusinessException e) {
			 logError("Not able to update LTL items with highest ship charge" ,e);
			 addFormException(new DropletException(e.getMessage()));
		 } catch (RepositoryException e) {
			 addFormException(new DropletException(e.getMessage()));
		} 
		 return false;
	 }

	protected void setRegistryContextInCommerceItem(String regId,
			RegistrySummaryVO regVO, BBBCommerceItem bbbComItem) {
		 this.logDebug("ComItem for associating registry context"+ bbbComItem.getId());
		 bbbComItem.setBuyOffAssociatedItem(true);
		 bbbComItem.setRegistryId(regId);
		 bbbComItem.setRegistryInfo(getRegistryInfo(regVO));  
		 bbbComItem.setBuyOffPrimaryRegFirstName(regVO.getPrimaryRegistrantFirstName());
		 bbbComItem.setBuyOffCoRegFirstName(regVO.getCoRegistrantFirstName());
		 bbbComItem.setBuyOffRegistryEventType(regVO.getRegistryType().getRegistryTypeDesc());
	}
	 
 /**
     * @param comItem
     * @param comItemObj
     * @param regId
     * @return
     */
    protected boolean isItemExistInRegistryContext(CommerceItem comItem, CommerceItem comItemObj, String regId) {
    	logDebug(" Entering in isItemExistInRegistryContext comItem : " + comItem + " comItemObj: " + comItemObj + " regId: " + regId);
		 
    	String refNum = ((BBBCommerceItem) comItem).getReferenceNumber();
		String associateItemSkuId = ((BBBCommerceItem) comItemObj).getCatalogRefId();
		String associatedItemRef = ((BBBCommerceItem) comItemObj).getReferenceNumber();
		 
     	return ((BBBCommerceItem) comItem).isBuyOffAssociatedItem() == true && 
     			((BBBCommerceItem) comItem).getCatalogRefId().equalsIgnoreCase(associateItemSkuId) && 
     			!StringUtils.isBlank(((BBBCommerceItem) comItem).getRegistryId()) &&
     			((BBBCommerceItem) comItem).getRegistryId().equalsIgnoreCase(regId) && 
     			((StringUtils.isEmpty(refNum) && StringUtils.isEmpty(associatedItemRef)) ||
     					(!StringUtils.isBlank(associatedItemRef) && !StringUtils.isBlank(refNum)
             			&& associatedItemRef.equals(refNum)));
	}

	public void associationUpdateSameItem(DynamoHttpServletRequest request,DynamoHttpServletResponse response,String skuid) throws ServletException, IOException{
		 super.handleSetOrderByCommerceId(request, response);	                        			
			this.setRemovalCommerceIds(new String[]{skuid});
			super.handleRemoveItemFromOrder(request, response);
	 }
	/**
     * @param pRegSumVO
     * @return String
     */
    public static String getRegistryInfo(final RegistrySummaryVO pRegSumVO){
        final StringBuilder registryInfo = new StringBuilder();
        registryInfo.append(pRegSumVO.getPrimaryRegistrantFirstName()).append(" ").append(pRegSumVO.getPrimaryRegistrantLastName()).append("  :  ");
        if( !(StringUtils.isEmpty(pRegSumVO.getCoRegistrantFirstName())) && !(StringUtils.isEmpty(pRegSumVO.getCoRegistrantLastName())) ){
            registryInfo.append(pRegSumVO.getCoRegistrantFirstName()).append(" ").append(pRegSumVO.getCoRegistrantLastName()).append("  :  ");
        }
        registryInfo.append(pRegSumVO.getRegistryId());

        return registryInfo.toString();
    }
    /** Validates if the input RegistryId is valid or not.
     *
     * @param pRegistryId Registry ID
     * @param pSiteId Site ID */
    public final void validateRegistryInfo(final String pRegistryId, final String pSiteId,
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
        RegistrySummaryVO registrySummaryVO = null;

        this.logDebug((new StringBuilder()).append("validateRegistryInfo: RegistryId: ").append(pRegistryId).toString());
        if (null != pRegistryId) {
            try {
            	if(this.isBuyOffFlag() == true){
            	final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
    					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
            	registrySummaryVO=sessionBean.getBuyoffStartBrowsingSummaryVO();
            	if(null == registrySummaryVO){
                registrySummaryVO = this.getRegistryManager().getRegistryInfo(pRegistryId, pSiteId);
            	}
            	}
            	else{
            		registrySummaryVO = this.getRegistryManager().getRegistryInfo(pRegistryId, pSiteId);   
            	}
            } catch (final BBBBusinessException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_REGISTRY_ID,
                                BBBCoreErrorConstants.CART_ERROR_1016));
                this.addFormException(this.createDropletException(INVALID_REGISTRY_ID, INVALID_REGISTRY_ID));
            } catch (final BBBSystemException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_REGISTRY_ID,
                                BBBCoreErrorConstants.CART_ERROR_1016));
                this.addFormException(this.createDropletException(INVALID_REGISTRY_ID, INVALID_REGISTRY_ID));
            }

            if (registrySummaryVO == null) {

                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_REGISTRY_ID,
                                BBBCoreErrorConstants.CART_ERROR_1016));

                this.addFormException(this.createDropletException(INVALID_REGISTRY_ID, INVALID_REGISTRY_ID));
            } else {
                try {
                    final String countryName = this.getCatalogUtil().getDefaultCountryForSite(pSiteId);
                    registrySummaryVO.getShippingAddress().setCountry(countryName);
                } catch (final BBBSystemException e) {
                    this.addFormException(this.createDropletException(DEFAULT_COUNTRY_MISSING_FOR_SITE,
                                    BBBCoreErrorConstants.CART_ERROR_1025));
                } catch (final BBBBusinessException e) {
                    this.addFormException(this.createDropletException(DEFAULT_COUNTRY_MISSING_FOR_SITE,
                                    BBBCoreErrorConstants.CART_ERROR_1025));
                }
                ((BBBOrderImpl) this.getOrder()).getRegistryMap().put(pRegistryId, registrySummaryVO);
            }

        } else {

            this.logError(LogMessageFormatter.formatMessage(pRequest, NULL_REGISTRY_ID,
                            BBBCoreErrorConstants.CART_ERROR_1006));

            this.addFormException(this.createDropletException(INVALID_REGISTRY_ID, INVALID_REGISTRY_ID));
        }
    }

    protected DropletException createDropletException(final String key, final String errorCode) {

        return new DropletException(this.getMsgHandler().getErrMsg(key, "EN", null), errorCode);
    }
    
    /**
	 * @param storeId
	 * @param itemResponseVo
	 * @return
	 */
	public final boolean validateStoreInfoForPartialAdd(final String storeId,
			MultipleIteminResponseVO itemResponseVo) {
		StoreDetails storeVO = null;
		boolean validStore = true;
		this.logDebug((new StringBuilder())
				.append("validateStoreInfo: storeId : ").append(storeId)
				.toString());

		if (StringUtils.isNotEmpty(storeId)) {

			try {
				String storeType = getStoreManager().getStoreType(this.getSiteId());
				storeVO = this.getStoreManager().searchStoreById(storeId,
						this.getSiteId(), storeType);
			} catch (final Exception e) {

				logError(BBBCoreErrorConstants.CART_ERROR_1017 + e.getMessage());
				itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1017);
				itemResponseVo.setErrorMessage(INVALID_STORE_ID);
				validStore = false;
			}
			if (storeVO == null) {
				logError(BBBCoreErrorConstants.CART_ERROR_1002
						+ INVALID_STORE_ID);
				itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1002);
				itemResponseVo.setErrorMessage(INVALID_STORE_ID);
				validStore = false;
			}
		} else {
			logError(BBBCoreErrorConstants.CART_ERROR_1008 + NULL_STORE_ID);
			itemResponseVo.setErrorCode(BBBCoreErrorConstants.CART_ERROR_1008);
			itemResponseVo.setErrorMessage(NULL_STORE_ID);
			validStore = false;

		}

		return validStore;

	}
	
    

    /** Validates if the input storeId is valid or not
     * @param storeId Store ID
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse*/
    public final void validateStoreInfo(final String storeId, final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) {
        StoreDetails storeVO = null;
        this.logDebug((new StringBuilder()).append("validateStoreInfo: storeId : ").append(storeId).toString());

        if (StringUtils.isNotEmpty(storeId)) {

            try {
            	String storeType = getStoreManager().getStoreType(this.getSiteId());
                storeVO = this.getStoreManager().searchStoreById(storeId, this.getSiteId(), storeType);
            } catch (final Exception e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, "Store Id error ",
                                BBBCoreErrorConstants.CART_ERROR_1017), e);
                this.addFormException(this.createDropletException(INVALID_STORE_ID, INVALID_STORE_ID));
            }
            if (storeVO == null) {

                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_STORE_ID,
                                BBBCoreErrorConstants.CART_ERROR_1002));

                this.addFormException(this.createDropletException(INVALID_STORE_ID, INVALID_STORE_ID));
            }
        } else {

            this.logError(LogMessageFormatter.formatMessage(pRequest, NULL_STORE_ID,
                            BBBCoreErrorConstants.CART_ERROR_1008));
            this.addFormException(this.createDropletException(NULL_STORE_ID, NULL_STORE_ID));
        }
    }

    /** Method is overridden pre method for removing the commerce items. The method verifies if the commerce item exists
     * in the current order and sets a form exception If the commerce item is not found */
    @Override
    public final void preRemoveItemFromOrder(final DynamoHttpServletRequest dynamohttpservletrequest,
                    final DynamoHttpServletResponse dynamohttpservletresponse) throws ServletException, IOException {
        this.logDebug("Entry preRemoveItemFromOrder");
        try {
            this.logDebug("Removing the commerce item with id:" + this.getRemoveItemFromOrder()[0]);
            this.getShoppingCart().getCurrent().getCommerceItem(this.getRemoveItemFromOrder()[0]);
        } catch (final CommerceItemNotFoundException e) {
            this.addFormException(this.createDropletException(ERR_CART_MISSING_COMMERCE_ITEM,
                            ERR_CART_MISSING_COMMERCE_ITEM));

            this.logError(BBBCoreErrorConstants.CART_ERROR_1018 + ": Missing commerce item", e);

        } catch (final InvalidParameterException e) {
            this.addFormException(this.createDropletException(ERR_CART_INVALID_COMMERCE_ITEM,
                            ERR_CART_INVALID_COMMERCE_ITEM));

            this.logError(BBBCoreErrorConstants.CART_ERROR_1019 + ": Invalid parameter", e);

        }
        super.preRemoveItemFromOrder(dynamohttpservletrequest, dynamohttpservletresponse);
        this.logDebug("Exit preRemoveItemFromOrder");
    }

    /** Method is overridden to removing the empty shipping groups from order when the item is removed from the cart. */
    @Override
    public void postRemoveItemFromOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final BBBShippingGroupManager shpGrpManager = (BBBShippingGroupManager) this.getShippingGroupManager();
        try {
            shpGrpManager.removeEmptyShippingGroups(this.getOrder());
        } catch (final CommerceException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, EXCEPTION_REMOVING_EMPTY_SHIPPING_GROUPS,
                            BBBCoreErrorConstants.CART_ERROR_1020), e);
        }

		//Sap-572 Code fix to solve REmovedItemException Start
        	Map<String, Set<CommerceItem>>  promoExclMap = ((BBBOrderImpl)this.getOrder()).getExcludedPromotionMap();
      		
      		//check if we have commerce items to remove
      		if (getRemovalCommerceIds()!=null && getRemovalCommerceIds().length>0){
      			List<String> itemsToDelete = new ArrayList<String>();
      			//iterate over commerce items to remove
      			for (String cItemStr: getRemovalCommerceIds()){
      				for (Map.Entry<String, Set<CommerceItem>> entry : promoExclMap.entrySet()) 
      				{
      				    Set<CommerceItem> commItem = entry.getValue();
      			    
      				    Iterator<CommerceItem> it = commItem.iterator();
      				    while(it.hasNext())
      				    {
      				    	CommerceItem cItem = it.next();
      				    	//remove the commerceitem from exclusion map if commerceitem id matches
      				    	if(cItem !=null && cItemStr.equals(cItem.getId())){
      				    		it.remove();
      				    		//break as we won't have more commerce items with same id
      				    		break;
      				    	}
      				    }
      				    if (commItem.isEmpty()) {
      				    	// if all commerceitems are removed from exclusion
      				    	// mark the promotion exclusion for removal.
      				    	itemsToDelete.add(entry.getKey());
    }
      				}
      			}
      			//remove all the promotion eclusion marked earlier
      			if(!itemsToDelete.isEmpty()){
      				for(String item: itemsToDelete){
      					promoExclMap.remove(item);
      				}
      			}
      		}
      		//SAP-572 Code fix to solve REmovedItemException End
   
    }

    /** PostMoveAllWishListItemsToOrder method to remove items from wishlist after moving to cart.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception*/
    public final void postMoveAllWishListItemsToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBCartFormHandler ::postMoveAllWishListItemsToOrder method starts");

        if (this.getProfile().isTransient()) {

            this.getSavedItemsSessionBean().setGiftListVO(null);

        } else {
            final String giftListId = ((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST))
                            .getRepositoryId();
            this.setWishListId(giftListId);
            for (int i = 0; i < this.getWishListItemIdsToRemove().size(); i++) {
                try {
                    this.getGiftlistManager().removeItemFromGiftlist(this.getWishListId(),
                                    this.getWishListItemIdsToRemove().get(i));
                } catch (final CommerceException commerceException) {
                    this.logError(BBBCoreErrorConstants.CART_ERROR_1021 + COMMERCE_EXCEPTION, commerceException);
                } catch (final RepositoryException repositoryException) {
                    this.logError(BBBCoreErrorConstants.CART_ERROR_1022 + REPOSITORY_EXCEPTION, repositoryException);
                }
            }
        }
        this.logDebug("BBBCartFormHandler ::postMoveAllWishListItemsToOrder method ends");
    }
    
    BBBSessionBean sessionBean;
    
    public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	private void removePersonalizedSkusFromSession(){
		for(AddCommerceItemInfo commerceItem : this.getItems()){
	    	if(getSessionBean().getPersonalizedSkus().containsKey(commerceItem.getCatalogRefId())){
	    		getSessionBean().getPersonalizedSkus().remove(commerceItem.getCatalogRefId());
	    		logDebug("Personalized Sku " + commerceItem.getCatalogRefId() + "removed from session.");
	    	}else{
	    		logDebug("Personalized Sku " + commerceItem.getCatalogRefId() + "not found in session.");
	    	}
		}
    }

    /** Method is overridden to removing the items from wishlist when the item is moved to cart from wishlist. It checks
     * for Giftlistmanager's isRemoveItemFromGiftlistOnMoveToCart. If true removes the item from wishlist. If the
     * commerce item is not found */
    @Override
    public void postAddItemToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	
		/*BBBH-2388: If currentZipCode's store id is not empty, StoreId is set in session
		  as sddStoreId from currentZipCode storeId and currentZipCode's store
		  id is cleared.*/
    	BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		if (null != sessionBean &&
				null!=sessionBean.getCurrentZipcodeVO()	&& !StringUtils.isEmpty(sessionBean.getCurrentZipcodeVO().getStoreId())) {
			sessionBean.setSddStoreId(sessionBean.getCurrentZipcodeVO()
					.getStoreId());
			if(isLoggingDebug()){
        		this.logDebug("setSddStoreId is set with value"+sessionBean.getCurrentZipcodeVO().getStoreId());
        	}
			sessionBean.getCurrentZipcodeVO().setStoreId(BBBCoreConstants.BLANK);
		}
        final BBBSavedItemsSessionBean savedItemsSessionBean = this.getSavedItemsSessionBean();
		if(isRecommItemSelected()) {
        	pRequest.getSession().setAttribute(BBBCoreConstants.RECOMMANDED_ITEM_SELECTED, recommItemSelected);
        	setRecommItemSelected(false);
        }
		
		removePersonalizedSkusFromSession();
		//final String originOfTraffic = BBBUtility.getOriginOfTraffic();
		final List<CommerceItem> comItemObjList = this.getOrder().getCommerceItems();
		for (final CommerceItem comItemObj : comItemObjList) {
				if (comItemObj instanceof BBBCommerceItem) {
			    	final BBBCommerceItem bbbObject = (BBBCommerceItem) comItemObj;
			    	if(null!=bbbObject.getStoreId() && !bbbObject.getStoreId().isEmpty()) {
			    		try {
			    			//commented the below as it causes issue while adding from Desktop
			    			//if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(originOfTraffic)) {
			    				this.getShippingGroupManager().removeEmptyShippingGroups(this.getOrder());
			    			//}
				    	 } catch (final CommerceException commerceException) {
	
				                this.logError(BBBCoreErrorConstants.CART_ERROR_1021 + COMMERCE_EXCEPTION, commerceException);
	
				         }
			    		
			    	}
				}
		}
		
		boolean fromWishlistForLTL = false;
		// adding this method above so that , if LTL item is moved from wish list to cart , we can maintain its assembly and delivery
		addDeliverySurchargeAssemblyFeeLtlItem(fromWishlistForLTL);

		if (this.isFromWishlist() && this.getProfile().isTransient()) {
        	fromWishlistForLTL = true;
            if (null != this.getJsonResultString()) {

                List<GiftListVO> totalStoredQty = savedItemsSessionBean.getGiftListVO();
                if (totalStoredQty == null) {
                    totalStoredQty = new ArrayList<GiftListVO>();
                }
                this.productList = new ArrayList<String>();
                if (StringUtils.isEmpty(this.getWishlistItemId())) {
                    this.logDebug(NO_WISHLIST_ITEM_ID_RECIEVED);
                    this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
                                    BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
                    return;
                }
                if (!totalStoredQty.isEmpty() && !StringUtils.isEmpty(this.getWishlistItemId())) {
                    final Iterator<GiftListVO> storedGiftListVO = totalStoredQty.iterator();

                    while (storedGiftListVO.hasNext()) {
                        final GiftListVO existingVO = storedGiftListVO.next();
                        if (!this.getWishlistItemId().equalsIgnoreCase(existingVO.getWishListItemId())) {
                            continue;
                        }
                        if (this.getWishlistItemId().equalsIgnoreCase(existingVO.getWishListItemId())
                                        && ((this.getQuantity() == 0) || (this.getQuantity() == existingVO
                                                        .getQuantity()))) {
                            storedGiftListVO.remove();
                            return;
                        }
                        existingVO.setQuantity(existingVO.getQuantity() - this.getQuantity());
                    }
                    this.logDebug(INVALID_WISHLIST_ITEM_ID_RECIEVED);
                    this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
                                    BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
                    return;
                }

            } else {
                final List<GiftListVO> totalStoredQty = this.getSavedItemsSessionBean().getItems();
                if (this.getWishlistItemId() == null) {
                    this.logDebug(NO_WISHLIST_ITEM_ID_RECIEVED);
                    this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
                                    BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
                    return;
                }
                if ((totalStoredQty != null) && !totalStoredQty.isEmpty()) {
                    final Iterator<GiftListVO> storedGiftListVO = totalStoredQty.iterator();

                    while (storedGiftListVO.hasNext()) {
                        final GiftListVO existingVO = storedGiftListVO.next();
                        if (this.getWishlistItemId().equalsIgnoreCase(existingVO.getWishListItemId())) {
                            if (this.getQuantity() == 0) {
                                this.logError("Quantity to move is 0");
                                return;
                            } else if (existingVO.getQuantity() == this.getQuantity()) {
                                storedGiftListVO.remove();
                                return;
                            } else if (existingVO.getQuantity() > this.getQuantity()) {
                                existingVO.setQuantity(existingVO.getQuantity() - this.getQuantity());
                                return;
                            } else if (existingVO.getQuantity() < this.getQuantity()) {
                                storedGiftListVO.remove();
                                return;
                            }
                        }
                    }
                    this.logDebug(INVALID_WISHLIST_ITEM_ID_RECIEVED);
                    this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
                                    BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
                    return;
                }
            }
            if (this.fromCart) {
                this.setFromWishlist(false);
            }
        } else if (this.fromWishlist && !this.getProfile().isTransient()) {
        	fromWishlistForLTL=true;
            try {
                final String giftListId = ((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST))
                                .getRepositoryId();
                this.setWishListId(giftListId);
                final GiftlistManager mgr = this.getGiftlistManager();
                final List items = mgr.getGiftlistItems(giftListId);
                if ((items != null) && !items.isEmpty()) {
                    for (int i = 0; i < items.size(); i++) {
                        String itemId;
                        long quantity;
                        final RepositoryItem item = (RepositoryItem) items.get(i);
                        itemId = (String) item.getPropertyValue(BBBCoreConstants.ID);
                        if (!this.getWishlistItemId().equalsIgnoreCase(itemId)) {
                            continue;
                        }
                        quantity = this.getQuantity();
                        savedItemsSessionBean.setUndoItemQuantity(quantity);
                        this.logDebug(i + "th itemId " + itemId + " quantity applicable for the item id " + quantity
                                        + " current item id value for update by user " + this.getWishlistItemId());

                        if (quantity == 0) {
                            try {
                                mgr.removeItemFromGiftlist(giftListId, this.getWishlistItemId());
                            } catch (final RepositoryException e) {
                                this.logError(LogMessageFormatter
                                                .formatMessage(pRequest,
                                                                REPOSITORY_EXCEPTION_WHILE_UPDATE_GIFTLIST_ITEMS,
                                                                BBBCoreErrorConstants.ACCOUNT_ERROR_1253), e);
                            }
                        } else if (((Long) item.getPropertyValue(QUANTITY_DESIRED)).longValue() != 0) {
                            quantity = ((Long) item.getPropertyValue(QUANTITY_DESIRED)).longValue();
                            // quantity = Long.valueOf((Long)item.getPropertyValue("quantityDesired")).longValue();

                        } else if (quantity > 0L) {
                            // pRequest.setAttribute(id, new Long(quantity));
                            pRequest.setAttribute(itemId, Long.valueOf(quantity));
                        }
                        this.logDebug(i + "the quantity to be updated " + quantity + " for item id " + itemId);
                        quantity = Long.valueOf(quantity) - this.getQuantity();
                        if ((quantity == 0) || (quantity < 0)) {
                            try {
                                mgr.removeItemFromGiftlist(giftListId, this.getWishlistItemId());
                            } catch (final RepositoryException e) {
                                this.logError(LogMessageFormatter
                                                .formatMessage(pRequest,
                                                                REPOSITORY_EXCEPTION_WHILE_UPDATE_GIFTLIST_ITEMS,
                                                                BBBCoreErrorConstants.ACCOUNT_ERROR_1253), e);
                            }
                        } else {
                            mgr.setGiftlistItemQuantityDesired(giftListId, itemId, quantity);
                        }
                        this.logDebug(" StoreGiftlistFormHandler :: updateGiftlistItems end");
                    }

                } else {
                    this.logDebug("Wish list item is invalid or not available in the repository");
                    this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
                                    BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
                }
                if (this.fromCart) {
                    this.setFromWishlist(false);
                }

            } catch (final CommerceException commerceException) {

                this.logError(BBBCoreErrorConstants.CART_ERROR_1021 + COMMERCE_EXCEPTION, commerceException);

            }

        }
    }
    
    
    
	/**
	 * This method will add serviceReferal to commerceItem if user selects the service in pdp page
	 * @param pRequest 
	 * 
	 */
	private void addPorchServiceToCommerceItem(DynamoHttpServletRequest pRequest) {
		if(isLoggingDebug()){
			logDebug("Adding Porch service CartFormHanderl ");
		}
 
		JSONObject jsonPayLoad=getPorchPayLoadJson();
		BaseCommerceItemImpl bbbItem = null;
        if(null!=jsonPayLoad){        	
        	AddCommerceItemInfo[] commerceItemInfo=getItems();
            String catalogRefId = commerceItemInfo[0].getCatalogRefId();
        	CommerceItem existingCommerceItem= ((BBBCommerceItemManager) getCommerceItemManager()).hasPorchCommerceItem(catalogRefId,getOrder());
        	 ProductVO productVO = new ProductVO();
		    
		    if(null==existingCommerceItem){		    	
		        MutableRepositoryItem commerceItem = null;
		        try {
		        	
		        	List<BBBCommerceItem> commerceItemList=(List<BBBCommerceItem>) getOrder().getCommerceItemsByCatalogRefId(catalogRefId);
		        	if(commerceItemList.size()>1){
		        	for(BBBCommerceItem bbbCommerceItem:commerceItemList){
		        		String registryId= bbbCommerceItem.getRegistryId();
		        		if(StringUtils.isBlank(registryId)){
		        			CommerceItemImpl commerceItem1=(CommerceItemImpl) bbbCommerceItem;	
				        	bbbItem=(BaseCommerceItemImpl) commerceItem1;
				        	commerceItem=commerceItem1.getRepositoryItem();
		        		}
		        	}
		        	}
		        	else{
		        	CommerceItemImpl commerceItem1=(CommerceItemImpl) commerceItemList.get(0);	
		        	bbbItem=(BaseCommerceItemImpl) commerceItem1;
		        	commerceItem=commerceItem1.getRepositoryItem();
		        	}
				} catch (CommerceItemNotFoundException e) {
					if(isLoggingError()){
						logError("Error while getting comeerce item "+e,e);
					}
				} catch (InvalidParameterException e) {
					if(isLoggingError()){
						logError("Error while getting comeerce item "+e,e);
					}
				}
		       
		        
		        //updating porch payload
		        if(null!=bbbItem){
		        	RepositoryItem repItem = (RepositoryItem) bbbItem.getAuxiliaryData().getProductRef();
		        	getPorchServiceManager().getPorchServiceFamilyCodes(repItem.getRepositoryId(), productVO);
			        String sku = bbbItem.getCatalogRefId();
					String productName= ((RepositoryItem)bbbItem.getAuxiliaryData().getProductRef()).getItemDisplayName();
					String productDescription=(String) ((RepositoryItem)bbbItem.getAuxiliaryData().getProductRef()).getPropertyValue("description");
					//String hostURL = getHost(pRequest);
					String productURL = (String) jsonPayLoad.get("productUrl");
					//productURL = hostURL.concat(productURL);
					String quantity=String.valueOf(bbbItem.getQuantity());
					jsonPayLoad.put("partnerSku", sku);
					jsonPayLoad.put("productName", productName);
					jsonPayLoad.put("productDesc", productDescription);
					jsonPayLoad.put("productUrl", productURL);
					jsonPayLoad.put("quantity", quantity);
					boolean freight = (boolean) bbbItem.getPropertyValue("ltlFlag");
					jsonPayLoad.put("freight", freight);
		        }
			    RepositoryItem serviceRefItem= (RepositoryItem) getPorchServiceManager().createUpdateServiceRefPorch(jsonPayLoad,null,productVO);
			    commerceItem.setPropertyValue("serviceReferal", serviceRefItem);
				if(isLoggingDebug()){
					logDebug("Adding Porch service to commerceItem "+commerceItem.getRepositoryId());
				}
		      }
		      else{
		    	  bbbItem=(BaseCommerceItemImpl) existingCommerceItem;
		    	  RepositoryItem repItem = (RepositoryItem) bbbItem.getAuxiliaryData().getProductRef();
		    	  getPorchServiceManager().getPorchServiceFamilyCodes(repItem.getRepositoryId(), productVO);
		        	RepositoryItem existingServiceRefItems = bbbItem.getPorchServiceRef();		
		        	 String sku = existingCommerceItem.getCatalogRefId();
						String productName= ((RepositoryItem)existingCommerceItem.getAuxiliaryData().getProductRef()).getItemDisplayName();
						jsonPayLoad.put("partnerSku", sku);
						jsonPayLoad.put("productName", productName);
		      	  	getPorchServiceManager().createUpdateServiceRefPorch(jsonPayLoad,existingServiceRefItems,productVO);
		      	  if(isLoggingDebug()){
						logDebug("Adding Porch service to commerceItem "+existingCommerceItem.getId());
					}
		          }      
        }
	}
        
    /*this method is used to add assembly fee and delivery surcharge to the LTL CommerceItem  */
    private void addDeliverySurchargeAssemblyFeeLtlItem(boolean fromWishlistForLTL) 
    {
        try {
            //changes for ltl 329 
        	if(getItems() != null && getItems().length > 0){
        		for(AddCommerceItemInfo itemToAdd : getItems()){
        			logDebug("Check if sku is Ltl or not: sku id: " + itemToAdd.getCatalogRefId());
        			boolean isSKULtl = getCatalogUtil().isSkuLtl(this.getSiteId(), itemToAdd.getCatalogRefId());
                	if((isSKULtl && !fromWishlistForLTL) || (isSKULtl && !isFromPipelineFlag())){
                		logInfo("Sku is LTL: " + itemToAdd.getCatalogRefId() + " and not from Wish list");
                		getLtlItemsMap(itemToAdd);
                		runProcessRepriceOrder(getAddItemToOrderPricingOp(), this.getOrder(), this.getUserPricingModels(), this.getUserLocale(), this.getProfile(), this.createRepriceParameterMap());
                	}
            	}
        	}
        	
            } catch (BBBBusinessException e) {
                   this.logError("Error occurred processing  getLtlItemsMap()", e);
            } catch (BBBSystemException e) {
                   this.logError("Error occurred processing getLtlItemsMap()", e);
            } catch (CommerceException e) {
                   this.logError("Error occurred processing getLtlItemsMap()", e);
            } catch (RunProcessException e) {
                   Object[] args = { this.getOrder().getId() };
                   this.logError("errorAddingToOrder atg.commerce.order.purchase.PurchaseProcessResources" + args , e);
            } catch (RepositoryException e) {
            	this.logError("Error updating in repository", e);				
			}

    }
    
    
    /** @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception
     * @throws RepositoryException Exception*/
  public void  getLtlItemsMap(AddCommerceItemInfo itemToAdd) 
		  throws BBBSystemException,BBBBusinessException,RepositoryException, CommerceException {
		
	  	this.logDebug("[Start] BBBCartFormHandler.getLtlItemsMap");
	  
		Collection shippingGroupCommerceItemRelationships = getShippingGroupCommerceItemRelationships(this.getOrder());
		if (shippingGroupCommerceItemRelationships != null) {
			ShippingGroup shippingGroup = null;
			CommerceItem currentCommItem = null;
			for (final Iterator shippingGroupCIRIterator = shippingGroupCommerceItemRelationships.iterator(); shippingGroupCIRIterator.hasNext();)
			{

				final BBBShippingGroupCommerceItemRelationship shippingGroupCIR = (BBBShippingGroupCommerceItemRelationship) shippingGroupCIRIterator
						.next();
				logDebug("BBBCartFormHandler.getLtlItemsMap:: check for the shipping group method in shipping commerce relationships i.e. shipping method is already there for same com");
				if (shippingGroupCIR != null 
						&& shippingGroupCIR.getCommerceItem().getCatalogRefId()!=null 
						&& shippingGroupCIR.getCommerceItem().getCatalogRefId().equalsIgnoreCase(itemToAdd.getCatalogRefId())
						&& shippingGroupCIR.getShippingGroup().getShippingMethod().equalsIgnoreCase((String) itemToAdd.getValue().get(BBBCatalogConstants.LTL_SHIP_METHOD))) {

					shippingGroup = shippingGroupCIR.getShippingGroup();
					currentCommItem = shippingGroupCIR.getCommerceItem();
					logDebug("BBBCartFormHandler.getLtlItemsMap:: shippingGroup: " + shippingGroup.getId() + " currentCommItem: " + currentCommItem.getId());
				}
			}
			// call addLTLAssemblyFeeSku only when shipping method is "White glove with assembly"
			
			if(shippingGroup != null && !BBBCoreConstants.BLANK.equalsIgnoreCase(shippingGroup.getShippingMethod())){
				String assemblyCommId = "";
				
				if (null != itemToAdd.getValue().get(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY) && 
						((String) itemToAdd.getValue().get(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY)).equalsIgnoreCase(BBBCatalogConstants.TRUE)) {
					logDebug("Shipping method is white glove assembly, Add Assembly Item as well");
					assemblyCommId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLAssemblyFeeSku(this.getOrder(),	shippingGroup, this.getOrder().getSiteId(), currentCommItem);
					logInfo("assembly commerce Id: " + assemblyCommId);
				}
	
				String deliveryCommId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLDeliveryChargeSku(this.getOrder(), shippingGroup,this.getOrder().getSiteId(), currentCommItem);
				logInfo("delivery commerce Id: " + deliveryCommId);
				((BBBCommerceItem)currentCommItem).setDeliveryItemId(deliveryCommId);
                ((BBBCommerceItem)currentCommItem).setAssemblyItemId(assemblyCommId);
			}
			else{
				logInfo("Empty Shipping Method so not creating Delivery and Assembly items.");
			}
		}
		this.logDebug("[Exit] BBBCartFormHandler.getLtlItemsMap");
	}
    
 
    /** @param pRequest DynamoHttpServletRequest
     * @return Gift List VO
     * @throws ServletException Exception
     * @throws IOException Exception
     * @throws CommerceException Exception*/
    public final List<GiftListVO> getGiftListVO(final DynamoHttpServletRequest pRequest)
                    throws ServletException, IOException, CommerceException {

        List<GiftListVO> additemList = null;
        try {
            additemList = ((BBBGiftlistManager) this.getGiftlistManager()).addItemJSONObjectParser(
                            this.getJsonResultString(), null, null);
        } catch (final BBBBusinessException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest,
                            "BBBBusinessException in StoreGiftlistFormHandler while getGiftListVO",
                            BBBCoreErrorConstants.ACCOUNT_ERROR_1261), e);

            this.logError(e);
        }
        return additemList;

    }

    /** overridden preSetOrderByCommerceId method for validating the commerceitem's quantity & Inaventory Check.
     *
     * @param pRequest - DynamoHttpServletRequest
     * @param pResponse - DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception*/
    @Override
    public void preSetOrderByCommerceId(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        // Checking if all items only belong to BBBCommerceItem
        boolean bbbComItemsOnly = true;
        final List<Object> comItemObj = this.getOrder().getCommerceItems();
        if (comItemObj != null) {
            for (final Object object : comItemObj) {
                if (!(object instanceof BBBCommerceItem)) {
                    bbbComItemsOnly = false;
                } else {
                	// update delivery and assembly ci quantity for eligible ci.
                	updateLTLDeliveryAssemblyCiQuantity(pRequest,pResponse,object);
                }
            }
        }
        this.setCheckItemQuantity(true);
        try {
            if (bbbComItemsOnly) {
                for (final BBBCommerceItem item : (List<BBBCommerceItem>) this.getOrder().getCommerceItems()) {
                    if ((item.getId() != null) && (this.getQuantity(item.getId(), pRequest, pResponse) >= 0)) {
                        BBBPerformanceMonitor.start(PRE_SET_ORDER_BY_COMMERCE_ID);
                        this.validateCommerceItem(pRequest, pResponse, item);

                        BBBPerformanceMonitor.end(PRE_SET_ORDER_BY_COMMERCE_ID);

                    } else {

                        this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_QUANTITY,
                                        BBBCoreErrorConstants.CART_ERROR_1012));

                        this.addFormException(new DropletException(((RepositoryItem) item.getAuxiliaryData()
                                        .getCatalogRef()).getPropertyValue("displayName")
                                        + this.getMsgHandler().getErrMsg(INVALID_QUANTITY, "EN", null),
                                        INVALID_QUANTITY));
                    }
                }
            }

            if (comItemObj != null) {
                for (final Object object : comItemObj) {
                    if ((object instanceof EcoFeeCommerceItem) || (object instanceof GiftWrapCommerceItem)) {
                        pRequest.setParameter(((CommerceItem) object).getId(),
                                        Long.valueOf(((CommerceItem) object).getQuantity()));
                    }
                }
            }
        } catch (final NumberFormatException e) {
            this.addFormException(this.createDropletException(INVALID_QUANTITY, INVALID_QUANTITY));
        }

    }

    /**
     * 
     * @param pRequest
     * @param pResponse
     * @param order
     */
    public void updateLTLDeliveryAssemblyCiQuantity(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse, Object object) {
    	
    	this.logDebug("BBBCartFormHandler.updateLTLDeliveryAssemblyCiQuantity() [Start]");
    	BBBCommerceItem objectCi = ((BBBCommerceItem)object);
    	Object inputQuantity = pRequest.getParameter(objectCi.getId());
    	//BBBP-1031 - Fix for story when updating single commerce item at a time with LTL items also in cart
       	if(inputQuantity == null){
    		inputQuantity = objectCi.getQuantity();
    	}
        if (inputQuantity != null && objectCi.isLtlItem()) {
			
        	String deliveryItemId = objectCi.getDeliveryItemId();
			String assemblyItemId = objectCi.getAssemblyItemId();
			if(BBBUtility.isNotEmpty(deliveryItemId)){
				 pRequest.setParameter(deliveryItemId,Long.parseLong(inputQuantity.toString()));
				 this.logDebug("deliveryItemId: " + deliveryItemId + " quantity: " + inputQuantity.toString());
			}
			if(BBBUtility.isNotEmpty(assemblyItemId)){
				pRequest.setParameter(assemblyItemId,Long.parseLong(inputQuantity.toString()));
				this.logDebug("assemblyItemId: " + assemblyItemId + " quantity: " + inputQuantity.toString());
			}
        }
        this.logDebug("BBBCartFormHandler.updateLTLDeliveryAssemblyCiQuantity() [End]");
    }

    /** Retrieve the quantity that should be used for the given catalog reference id or commerce item id.
     *
     * @param pCatalogRefId a <code>String</code> value
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @return a <code>long</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs
     * @exception NumberFormatException if an error occurs */
    @Override
    public final long getQuantity(final String pCatalogRefId, final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        BBBCommerceItem commerceItem;
        try {
            final Object value = pRequest.getParameter(pCatalogRefId);
            if (value != null) {
                return Long.parseLong(value.toString());
            }
            if (this.isCheckItemQuantity()) {
                if(this.getOrder().getCommerceItem(pCatalogRefId) instanceof BBBCommerceItem){
                commerceItem = (BBBCommerceItem) this.getOrder().getCommerceItem(pCatalogRefId);
                if (commerceItem.getQuantity() >= 0) {
                    return commerceItem.getQuantity();
                }
            }
            }
        } catch (final CommerceItemNotFoundException e) {
            this.addFormException(this.createDropletException(COMMERCE_ITEM_NOT_FOUND, COMMERCE_ITEM_NOT_FOUND));
        } catch (final InvalidParameterException e) {
            this.addFormException(this.createDropletException(INVALID_PARAMETER, INVALID_PARAMETER));
        }

        return this.getQuantity();
    }

    private void validateCommerceItem(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse, final BBBCommerceItem item) {
        try {
            final BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) this.getOrder().getCommerceItem(item.getId());

            int inventoryStatus = BBBInventoryManager.AVAILABLE;
            int earlierFetchedStatus = BBBInventoryManager.AVAILABLE;
            long rolledUpQty = 0;
            
            try {
                rolledUpQty = this.getQuantity(item.getId(), pRequest, pResponse);

                this.checkForMaxQty(pRequest, pResponse, bbbCommerceItem, rolledUpQty);

                @SuppressWarnings ("unchecked")
                final Map<String, Integer> tempAvailabilityMap = ((BBBOrder) this.getOrder()).getAvailabilityMap();
                if (!BBBUtility.isMapNullOrEmpty(tempAvailabilityMap)
                                && tempAvailabilityMap.containsKey(bbbCommerceItem.getId())) {

                    earlierFetchedStatus = tempAvailabilityMap.get(bbbCommerceItem.getId()).intValue();
                }
                               
            	inventoryStatus = ((BBBPurchaseProcessHelper) this.getPurchaseProcessHelper()).checkCachedInventory(this
                        .getOrder().getSiteId(), bbbCommerceItem.getCatalogRefId(), bbbCommerceItem
                        .getStoreId(), this.getOrder(), rolledUpQty, BBBInventoryManager.UPDATE_CART, this
                        .getStoreInventoryContainer(), earlierFetchedStatus);

                // Update Order's AvailabilityMap

                if (((BBBOrder) this.getOrder()).getAvailabilityMap() != null) {
                    ((BBBOrder) this.getOrder()).getAvailabilityMap().put(bbbCommerceItem.getId(),
                                    Integer.valueOf(inventoryStatus));
                }

            } catch (final CommerceException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, "error getting inventory",
                                BBBCoreErrorConstants.CART_ERROR_1003), e);
                inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
            }

            if (inventoryStatus == BBBInventoryManager.NOT_AVAILABLE) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, OUT_OF_STOCK,
                                BBBCoreErrorConstants.CART_ERROR_1009));
            }

        } catch (final CommerceItemNotFoundException e) {

            this.logError(LogMessageFormatter.formatMessage(pRequest, "Commerce item not found",
                            BBBCoreErrorConstants.CART_ERROR_1018), e);

            this.addFormException(this.createDropletException(ERR_CART_INVALID_COMMERCE_ITEM,
                            ERR_CART_INVALID_COMMERCE_ITEM));
            this.setSetOrderErrorURL(this.getGlobalErrorURL());
        } catch (final InvalidParameterException e) {

            this.logError(LogMessageFormatter.formatMessage(pRequest, "Invalid parameter",
                            BBBCoreErrorConstants.CART_ERROR_1019), e);

            this.addFormException(this.createDropletException(ERR_CART_INVALID_COMMERCE_ITEM,
                            ERR_CART_INVALID_COMMERCE_ITEM));
            this.setSetOrderErrorURL(this.getGlobalErrorURL());
        } catch (final Exception e) {

            this.logError(LogMessageFormatter.formatMessage(pRequest, "Invalid commerceitem",
                            BBBCoreErrorConstants.CART_ERROR_1018), e);

            this.addFormException(this.createDropletException(ERR_CART_INVALID_COMMERCE_ITEM,
                            ERR_CART_INVALID_COMMERCE_ITEM));
        }
    }

    /** @param pRequest
     * @param skuID
     * @param reqQty */
    public final void
                    checkForMaxQty(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse,
                                    final BBBCommerceItem bbbCommerceItem, final long reqQty) {
        long maxQtyLong = MAX_QUANTITY;
        final long currentQty = bbbCommerceItem.getQuantity();
        if (currentQty > MAX_QUANTITY) {
            try {
                maxQtyLong = this.getQuantity(bbbCommerceItem.getId(), pRequest, pResponse);
            } catch (final NumberFormatException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_QUANTITY + FOR_SKU_ID
                                + bbbCommerceItem.getCatalogRefId(), BBBCoreErrorConstants.CART_ERROR_1012));

                this.addFormException(this.createDropletException(INVALID_QUANTITY, INVALID_QUANTITY));
            } catch (final ServletException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_QUANTITY + FOR_SKU_ID
                                + bbbCommerceItem.getCatalogRefId(), BBBCoreErrorConstants.CART_ERROR_1012));

                this.addFormException(this.createDropletException(INVALID_QUANTITY, INVALID_QUANTITY));
            } catch (final IOException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_QUANTITY + FOR_SKU_ID
                                + bbbCommerceItem.getCatalogRefId(), BBBCoreErrorConstants.CART_ERROR_1012));

                this.addFormException(this.createDropletException(INVALID_QUANTITY, INVALID_QUANTITY));
            }
        }
        if (reqQty > maxQtyLong) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_QUANTITY + FOR_SKU_ID
                            + bbbCommerceItem.getCatalogRefId(), BBBCoreErrorConstants.CART_ERROR_1012));

            this.addFormException(this.createDropletException(INVALID_QUANTITY, INVALID_QUANTITY));
        }
    }

    /**Overridden postSetOrderByCommerceId method for removing the shipping group entry from bbb_store_ship_grp table.
     *
     * @param pRequest - DynamoHttpServletRequest
     * @param pResponse - DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception*/
    @Override
    public void postSetOrderByCommerceId(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final BBBShippingGroupManager shpGrpManager = (BBBShippingGroupManager) this.getShippingGroupManager();
        try {
            shpGrpManager.removeEmptyShippingGroups(this.getOrder());
        } catch (final CommerceException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, EXCEPTION_REMOVING_EMPTY_SHIPPING_GROUPS,
                            BBBCoreErrorConstants.CART_ERROR_1020), e);
        }

    }

    /* (non-Javadoc)
     * @see atg.commerce.order.purchase.CartModifierFormHandler#getSiteId() */
    @Override
    public final String getSiteId() {
        if (super.getSiteId() != null) {
            return super.getSiteId();
        }
        return SiteContextManager.getCurrentSiteId();
    }

    /** Wrapper method for Rest for Checkout.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception
     * @throws BBBBusinessException 
     * @throws BBBSystemException */
    public final boolean handleCheckoutCart(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBSystemException, BBBBusinessException {

        final Map<String, String> failure = this.getCheckoutState().getCheckoutFailureURLs();
        final Map<String, String> success = this.getCheckoutState().getCheckoutSuccessURLs();
        final HashMap map = setRedirectUrlForRest();
        this.getCheckoutState().setCheckoutFailureURLs(map);
        this.getCheckoutState().setCheckoutSuccessURLs(map);
        final boolean status = this.handleCheckout(pRequest, pResponse);
        this.getCheckoutState().setCheckoutFailureURLs(failure);
        this.getCheckoutState().setCheckoutSuccessURLs(success);

        return status;
    }

    /** Wrapper method for Rest for express Checkout.
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception
     * @throws BBBBusinessException 
     * @throws BBBSystemException */
    public final boolean handleExpressCheckoutForOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBSystemException, BBBBusinessException {

        final Map<String, String> failure = this.getCheckoutState().getCheckoutFailureURLs();
        final Map<String, String> success = this.getCheckoutState().getCheckoutSuccessURLs();
        final HashMap map = setRedirectUrlForRest();
        this.getCheckoutState().setCheckoutFailureURLs(map);
        this.getCheckoutState().setCheckoutSuccessURLs(map);
        this.setExpressCheckout(true);
        final boolean status = this.handleCheckout(pRequest, pResponse);
        this.getCheckoutState().setCheckoutFailureURLs(failure);
        this.getCheckoutState().setCheckoutSuccessURLs(success);
        return status;
    }

    /** handles user checkout event redirection from cart page
     *
     * @param pRequest - DynamoHttpServletRequest
     * @param pResponse - DynamoHttpServletResponse
     * @return Succes / Failure
     * @throws ServletException Exception
     * @throws IOException Exception
     * @throws BBBBusinessException 
     * @throws BBBSystemException */
    public final boolean handleCheckout(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBSystemException, BBBBusinessException {

    	//Cached inventory Implementation BBBSL-3018
    	this.getInventoryManager().checkUncachedInventory((BBBOrder) this.getOrder());
    	BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);

        //R2.2 | PayPal| Starts
		//Treat the checkout button as paypal checkout if order is of type PayPal and token is not expired
        BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
        boolean isTokenExp = getPaypalServiceManager().isTokenExpired(this.getPayPalSessionBean(), order);
    	if(order.isPayPalOrder() && !isTokenExp){
			this.logDebug("BBBCartFormHandler.handleCheckout() :  Order is Paypal and token still exists, so normal checkout button " +
						"will behave same as Checkout with paypal. Calling BBBCartFormHandler.handleCheckoutWithPaypal()");
        	this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
        	if(sessionBean.isSinglePageCheckout()){
        		return this.handleCheckoutSPWithPaypal(pRequest, pResponse);
        	}
        	else{
			return this.handleCheckoutWithPaypal(pRequest, pResponse);
        	}
		} else if(!(StringUtils.isEmpty(order.getToken()) && isTokenExp)) {
			getPaypalServiceManager().removePayPalPaymentGroup(((BBBOrderImpl) this.getOrder()), this.getUserProfile());
		}
        //R2.2 | PayPal| Ends
    	
        //Start: 258 - Verified by visa - refresh back button story - resetting the page state
        pRequest.getSession().removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS);
        //End: 258 - Verified by visa - refresh back button story - resetting the page state
    	
        this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
        
		this.getCheckoutManager();
		// Moved below If() condition from preExpressCheckout() and added here to fix RS defect 2156012
		if (BBBCheckoutManager.getOrderInventoryStatus(this.getOrder()) == BBBInventoryManager.NOT_AVAILABLE) {
			String successURL = pRequest.getContextPath() + this.getCheckoutState().getFailureURL();
			return this.checkFormRedirect(successURL, null, pRequest, pResponse);
		}
		
		// below logic is to delete empty shipping groups from the order for all the scenarions like ltl, express, guest.
		try {
			synchronized (this.getOrder()) {
				this.getShippingGroupManager().removeEmptyShippingGroups(
						this.getOrder());
				// emptyShipGrpRemoved=true;
				this.getOrderManager().updateOrder(this.getOrder());
			}
		} catch (CommerceException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"Error processing order during removal of empty shipping group in case of LTL order while checking out",
									BBBCoreErrorConstants.CART_ERROR_1021), e);
		}
		if (this.getExpressCheckout()) {
		    // review page
		    ((BBBOrderImpl) this.getOrder()).setExpressCheckOut(true);
		    return this.handleExpCheckout(pRequest, pResponse);
		}
		// first checkout page
		((BBBOrderImpl) this.getOrder()).setExpressCheckOut(false);
		//Commentted below code and moved above before express checkout condition. Becuase need clear 
		//empty shipping groups for express checout case also
		
		/*List<CommerceItem> commItemList = this.getOrder().getCommerceItems();
		boolean emptyShipGrpRemoved = false;
		if(!emptyShipGrpRemoved){
			for(CommerceItem commItem : commItemList){
				if(commItem instanceof BBBCommerceItem && ((BBBCommerceItem)commItem).isLtlItem()){
					try {
						synchronized (this.getOrder()) {
							this.getShippingGroupManager().removeEmptyShippingGroups(this.getOrder());
							emptyShipGrpRemoved=true;
							this.getOrderManager().updateOrder(this.getOrder());
						}
					} catch (CommerceException e) {
						this.logError(LogMessageFormatter.formatMessage(pRequest,
	                            "Error processing order during removal of empty shipping group in case of LTL order while checking out",
	                            BBBCoreErrorConstants.CART_ERROR_1021), e);
					}
				}
			}
		}*/
		List<CommerceItem> allCommItems;
		allCommItems = this.getOrder().getCommerceItems();
		if(allCommItems!=null){
 				 for(CommerceItem commItem : allCommItems){
					 if (commItem instanceof BBBCommerceItem) {
						 BBBCommerceItem bbbCommItem = (BBBCommerceItem) commItem;
				
				 String lTLHighestShipMethodId = "";
				 String commShipMethod = bbbCommItem.getLtlShipMethod();
				 boolean ltlItemFlag = bbbCommItem.isLtlItem();
				 if(ltlItemFlag){
					 this.logDebug("LTL item found");
					 this.logDebug("lTLEligibleShippingMethods getCatalogRefId:"+bbbCommItem.getCatalogRefId());
					 this.logDebug("lTLEligibleShippingMethods getCurrentSiteId:"+SiteContextManager.getCurrentSiteId());
					 this.logDebug("lTLEligibleShippingMethods getLanguage:"+this.getUserLocale(pRequest, pResponse).getLanguage());
					 List<ShipMethodVO> lTLEligibleShippingMethods = getCatalogUtil().getLTLEligibleShippingMethods(bbbCommItem.getCatalogRefId(), SiteContextManager.getCurrentSiteId(), this.getUserLocale(pRequest, pResponse).getLanguage());
					 this.logDebug("lTLEligibleShippingMethods size"+lTLEligibleShippingMethods.size());
					 for (int ii=0;ii<lTLEligibleShippingMethods.size();ii++) {
						 this.logDebug("lTLEligibleShippingMethods value"+lTLEligibleShippingMethods.get(ii));
					 }
					 logDebug("lTLEligibleShippingMethods : " + lTLEligibleShippingMethods);
					 if (!BBBUtility.isListEmpty(lTLEligibleShippingMethods)) {
						 ShipMethodVO lTLHighestShipMethod = lTLEligibleShippingMethods.get(lTLEligibleShippingMethods.size()-1);
						 lTLHighestShipMethodId = lTLHighestShipMethod.getShipMethodId();
						 bbbCommItem.setHighestshipMethod(lTLHighestShipMethodId);
					 }
				 }
					 }
 				 
			 }	
		}
		
		sessionBean.setSinglePageCheckout(false);
		boolean isSPCEligible = getCheckoutState().spcEligible(order,this.isSinglePageCheckout());
		if(isSPCEligible){
			sessionBean.setSinglePageCheckout(true);
			this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_CHECKOUT_SINGLE.toString());
		}else if (this.getProfile().isTransient()) { 
			if (BBBCoreConstants.TRUE .equals((String) pRequest.getSession().getAttribute(BBBCoreConstants.RECGONIZED_GUEST_USER_FLOW))){
				this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_MULTIPLE.toString()); 
			}else{		 	
		    this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.GUEST.toString()); 
			}
		} else {
		    this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString());
		}
		String successURL = "";
		if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL())
		                && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
		    successURL = pRequest.getContextPath() + this.getCheckoutState().getFailureURL();
		}
		return this.checkFormRedirect(successURL, null, pRequest, pResponse);
    }


     /** Rest Specific method to set Redirect URL.
     *
     * @return */
    private static HashMap<String, String> setRedirectUrlForRest() {
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

        return map;
    }

    /** @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception*/
    public boolean handleExpCheckout(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        this.logDebug("Entry BBBCartFormHandler.handleExpCheckout");

        final String contextPath = pRequest.getContextPath();
        String failureURL = null;
        if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL())
                        && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            failureURL = contextPath + this.getCheckoutState().getFailureURL();
        }

        final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();

        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.EXPRESS_CHECKOUT, myHandleMethod);
            Transaction tr = null;
            try {
                synchronized (this.getOrder()) {
                    tr = this.ensureTransaction();
                    this.preExpressCheckout(pRequest, pResponse);
                    if (this.getFormError()) {
                        return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
                    }
                    this.expressCheckout(pRequest);
                    if (this.getFormError()) {
                        this.markTransactionRollback();
                        return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
                    }
                    this.runRepricingProcess(pRequest, pResponse, null);
                    this.postExpressCheckout(pRequest, pResponse);

                    this.getOrderManager().updateOrder(this.getOrder());
                }
                if (this.getFormError()) {
                    this.markTransactionRollback();
                    return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
                }
               else{
                	validatePorchServiceWithPreferedAddressZipCode();
                }

            } catch (final RunProcessException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                                "Error processing order repricing during express checkout",
                                BBBCoreErrorConstants.CART_ERROR_1021), e);
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(GENERIC_ERROR_TRY_LATER,
                                "EN", null)));
                this.markTransactionRollback();
                return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
            } catch (final CommerceException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                                "Error processing order during update order while checking out",
                                BBBCoreErrorConstants.CART_ERROR_1021), e);
                this.addFormException(this.createDropletException(GENERIC_ERROR_TRY_LATER, GENERIC_ERROR_TRY_LATER));
                this.markTransactionRollback();
                return this.checkFormRedirect(null, failureURL, pRequest, pResponse);
            } finally {
                // Complete the transaction
                if (tr != null) {
                    this.commitTransaction(tr);
                }

                if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.EXPRESS_CHECKOUT, myHandleMethod);
            }
            this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
            
            //adding code for single page checkout
            
            // Checking for the TBS Site
			if (!this.getSiteId().startsWith(TBSConstants.TBS_PREFIX)) {
				final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
				sessionBean.setSinglePageCheckout(false);
				boolean isSPCEligible = getCheckoutState().spcEligible((BBBOrderImpl) this
						.getOrder() , this.isSinglePageCheckout());
				if (isSPCEligible) {
					this.getCheckoutState().setCurrentLevel(
							CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW
									.toString());
					sessionBean.setSinglePageCheckout(true);
				}
			}
            String pSuccessURL = "";
            if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL())
                            && !this.getCheckoutState().getFailureURL()
                                            .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
                pSuccessURL = contextPath + this.getCheckoutState().getFailureURL();
            }
            return this.checkFormRedirect(pSuccessURL, null, pRequest, pResponse);

        }
        return false;
    }

    /**
     * 
     */
    private void validatePorchServiceWithPreferedAddressZipCode() {

  	  List<CommerceItem> commerceItemList=(List<CommerceItem>) getOrder().getCommerceItems();
         
        for(CommerceItem commerceItem:commerceItemList){
        	BaseCommerceItemImpl cItemimpl = (BaseCommerceItemImpl) commerceItem;
    		if(cItemimpl.isPorchService() ){		
    			BBBHardGoodShippingGroup shipinggroup = (BBBHardGoodShippingGroup) ((BBBShippingGroupCommerceItemRelationship)commerceItem.getShippingGroupRelationships().get(0)).getShippingGroup();
    			String commItemZipCode = shipinggroup.getShippingAddress().getPostalCode();
    			if(atg.core.util.StringUtils.isBlank(commItemZipCode)){
    				return;
    			}
    			String[] commItemShippingCode=commItemZipCode.split("-");
    			RepositoryItem productItem = (RepositoryItem) commerceItem.getAuxiliaryData().getProductRef();    			
    			List<String> porchServiceFamilycodes=getPorchServiceManager().getPorchServiceFamilyCodes(productItem.getRepositoryId(),null);
    			Object responseVO = null;
    			if(!StringUtils.isBlank(commItemShippingCode[0]) && !StringUtils.isBlank(porchServiceFamilycodes.get(0))){
				try {
					responseVO = getPorchServiceManager().invokeValidateZipCodeAPI(commItemShippingCode[0],porchServiceFamilycodes.get(0));
					if(null==responseVO){
						cItemimpl.setPorchServiceRef(null);
						cItemimpl.setPorchService(false);
						this.addFormException(new DropletException(this.getMsgHandler().getErrMsg("err_porchServiceNotAvailable",
				      			  BBBCoreConstants.DEFAULT_LOCALE, null), "err_porchServiceNotAvailable"));
						
					}
				} catch (BBBSystemException e) {
					if(isLoggingError()){
						logError("Error while validiang zipcode with proch avilability "+e,e);
					}
				} catch (BBBBusinessException e) {
					 
					if(isLoggingError()){
						logError("Error while validiang zipcode with proch avilability "+e,e);
					}
				} 
    			 
    			}
    		}
        }
        
	}
    
    
	/**
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     */
    protected void postExpressCheckout(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) {
        // FIXME
    }

    protected void expressCheckout(final DynamoHttpServletRequest pRequest) {
        try {
            this.getCheckoutManager().ensureShippingGroups(this.getOrder(), this.getSiteId(),
                            (Profile) this.getProfile());
        } catch (final Exception e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest,
                            "Error adding Shipping address to shipping group", BBBCoreErrorConstants.CART_ERROR_1021),
                            e);
            this.addFormException(this.createDropletException(SHIPPING_ERROR, SHIPPING_ERROR));
            return;
        }
        try {
            this.getCheckoutManager().ensurePaymentGroups(this.getOrder(), this.getSiteId(),
                            (Profile) this.getProfile());
        } catch (final Exception e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, "Error adding payment group information",
                            BBBCoreErrorConstants.CART_ERROR_1021), e);
            this.addFormException(this.createDropletException(BILLING_ERROR, BILLING_ERROR));
            return;
        }
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
		if (StringUtils.isEmpty(pRequest.getParameter(BBBCoreConstants.CART))) {
        	Map extraParam = new HashMap();
            extraParam.put("couponListParam", this.getCouponList());
            params.put("ExtraParameters",extraParam);
        }
        final PipelineResult result = this.runProcess(this.getRepriceOrderChainId(), params);
        this.processPipelineErrors(result);
    }

    protected final void preExpressCheckout(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) {
        BBBAddressVO shippingAddress = null;
        ShipMethodVO defaultShippingMethod = null;
        try {
            shippingAddress = this.getCheckoutManager().getProfileAddressTool()
                            .getDefaultShippingAddress((Profile) this.getProfile(), this.getSiteId());

        } catch (final Exception e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, "Error getting Shipping address",
                            BBBCoreErrorConstants.CART_ERROR_1021), e);
            this.addFormException(this.createDropletException(GENERIC_ERROR_TRY_LATER, GENERIC_ERROR_TRY_LATER));
            return;
        }
        try {
            defaultShippingMethod = this.getCatalogUtil().getDefaultShippingMethod(this.getSiteId());
        } catch (final Exception e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, "Error getting default Shipping method",
                            BBBCoreErrorConstants.CART_ERROR_1021), e);
            this.addFormException(this.createDropletException(GENERIC_ERROR_TRY_LATER, GENERIC_ERROR_TRY_LATER));
            return;
        }

        this.getCheckoutManager();
        if (BBBCheckoutManager.getOrderInventoryStatus(this.getOrder()) == BBBInventoryManager.NOT_AVAILABLE) {
            this.addFormException(this.createDropletException(ITEMS_OUTOF_STOCK, ITEMS_OUTOF_STOCK));
            return;
        }

        final List<CommerceItem> commerceItems = this.getOrder().getCommerceItems();
        //updating code for ship to PO
        List<String> shiptoPOBoxOn;
		boolean shiptoPOFlag =false;
		try {
			shiptoPOBoxOn = catalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
			shiptoPOFlag = Boolean.parseBoolean(shiptoPOBoxOn.get(0));
        if (!shiptoPOFlag && !BBBUtility.isNonPOBoxAddress(shippingAddress.getAddress1(), shippingAddress.getAddress2())) {
           this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                            BBBCheckoutConstants.ERROR_POBOX_ADDRESS, BBBCoreConstants.DEFAULT_LOCALE, null)));
            return;
        }
		}catch (BBBSystemException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		} catch (BBBBusinessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}
        if (!this.getCheckoutManager().canItemShipToAddress(this.getSiteId(), commerceItems, shippingAddress)) {
        	if (!shippingAddress.getIsNonPOBoxAddress())
            this.addFormException(this.createDropletException(ITEMS_STATE_RESTRICTIONS, ITEMS_STATE_RESTRICTIONS));
        	else
        		this.addFormException(this.createDropletException(ITEMS_POBOX_RESTRICTIONS, ITEMS_POBOX_RESTRICTIONS));
            return;
        } 
        
        
        
        if (this.isOrderContainRestrictedSKU(this.getOrder(),shippingAddress)) {
        	// Redirect request to error url
        	this.addFormException(this.createDropletException(ITEMS_STATE_RESTRICTIONS, ITEMS_STATE_RESTRICTIONS));
        	return;
        }
        
        if (!this.getCheckoutManager().canItemShipByMethod(this.getSiteId(), commerceItems,
                        defaultShippingMethod.getShipMethodId())) {
            this.addFormException(this.createDropletException(ITEMS_METHOD_RESTRICTIONS, ITEMS_METHOD_RESTRICTIONS));
            return;
        }
        if (BBBUtility.isEmpty((String) this.getProfile().getPropertyValue(BBBCoreConstants.PHONENUMBER))
                        && BBBUtility.isEmpty((String) this.getProfile()
                                        .getPropertyValue(BBBCoreConstants.MOBILENUMBER))) {
            /* if no phone/mobile number in profile then no express checkout. */
            this.addFormException(this.createDropletException(GENERIC_ERROR_TRY_LATER, GENERIC_ERROR_TRY_LATER));
            return;
        }

    }
    
    /** This method will check that order shipping group has any restricted sku or not.
    *
    * @param order
    * @return */
   private boolean isOrderContainRestrictedSKU(final Order order,BBBAddressVO shippingAddress) {
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
                    String zipCode = shippingAddress.getPostalCode();
                    //R2.2 -83 J- Start Added To check Postal code Sent in restmultishipping call
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
    
    /** Wrapper method for Rest for ApplyCoupons
     *
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException */
    public final boolean handleApplyCouponsToOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final Map<String, String> failure = this.getCheckoutState().getCheckoutFailureURLs();
        final Map<String, String> success = this.getCheckoutState().getCheckoutSuccessURLs();
        final HashMap map = setRedirectUrlForRest();
        this.getCheckoutState().setCheckoutFailureURLs(map);
        this.getCheckoutState().setCheckoutSuccessURLs(map);
        boolean status = this.handleApplyCoupons(pRequest, pResponse);
        if (status && !this.isSinglePageCheckoutEnabled()) {
            status = this.handleMoveToPayment(pRequest, pResponse);
        }
        if (this.getCouponErrorList().size() > 0) {
            this.removeFormException(pRequest);
            final Map<String, String> couponErrorList = new HashMap<String, String>();
            for (final Map.Entry<String, String> entry : this.getCouponErrorList().entrySet()) {
                couponErrorList.put(
                                entry.getKey(),
                                this.getMsgHandler().getErrMsg(entry.getValue(), pRequest.getLocale().getLanguage(),
                                                null, null));
            }
            this.getCouponErrorList().clear();
            this.setCouponErrorList(couponErrorList);
        }
        this.getCheckoutState().setCheckoutFailureURLs(failure);
        this.getCheckoutState().setCheckoutSuccessURLs(success);

        return status;
    }

    /** This method finds if a particular Commerce Item fall under any one of the Qualifier Service Filters. If it does,
     * it sets the pCouponFiltervo variable true.
     *
     * @param CommerceItem pCommerceItem
     * @param couponFilterVO pCouponFiltervo
     * @param String pCouponCode
     * @return couponFilterVO pCouponFiltervo
     * @throws BBBSystemException
     * @throws BBBBusinessException */
    private couponFilterVO checkCommerceItemQualifierFlags(final CommerceItem pCommerceItem,
                    final couponFilterVO pCouponFiltervo, final String pCouponCode)
                    throws BBBSystemException, BBBBusinessException {
        // Check if Commerce item is instance of BBBCommerce Item for BOPUS check
        BBBCommerceItem bbbCommerceItem = null;
        if (pCommerceItem instanceof BBBCommerceItem) {
            bbbCommerceItem = (BBBCommerceItem) pCommerceItem;
        }

        // Check if Commerce item is BOPUS
        if ((bbbCommerceItem != null) && !(BBBUtility.isEmpty(bbbCommerceItem.getStoreId()))) {
            pCouponFiltervo.setContainsBOPUS(true);
        }
        // Check if Commerce item is Gift Card
        else if (this.getCatalogUtil().isGiftCardItem(this.getOrder().getSiteId(), pCommerceItem.getCatalogRefId())) {
            pCouponFiltervo.setContainsGiftCard(true);
        }
        // Check if Commerce item is Non Merchandise CommerceItem
        else if (pCommerceItem instanceof NonMerchandiseCommerceItem) {
            pCouponFiltervo.setContainsNMC(true);
        }
        // Check if Commerce item is SKU Excluded
        else if (this.getCatalogUtil().isSkuExcluded(pCommerceItem.getCatalogRefId(), pCouponCode, false)) {
            pCouponFiltervo.setContainsSKUExclusion(true);
        }
        // Commerce item is Normal
        else {
            pCouponFiltervo.setContainsNormal(true);
        }

        return pCouponFiltervo;
    }

    /** This method return the corresponding Coupon Error Code based on Priority.
     *
     * @param couponFilterVO pCouponFiltervo
     * @return couponFilterVO pCouponFiltervo */
    private static String getCouponErrorMessage(final couponFilterVO pCouponFiltervo) {
        if (pCouponFiltervo.isContainsNormal()) {
            return COUPON_GENERIC_ERROR;
        } else if (pCouponFiltervo.isContainsBOPUS()) {
            return COUPON_BOPUS_ERROR;
        } else if (pCouponFiltervo.isContainsGiftCard()) {
            return COUPON_GC_ERROR;
        } else if (pCouponFiltervo.isContainsNMC()) {
            return COUPON_NMC_ERROR;
        } else if (pCouponFiltervo.isContainsSKUExclusion()) {
            return COUPON_SKU_EX_ERROR;
        }
        return COUPON_GENERIC_ERROR;
    }

    /** @param req
     * @param res
     * @return
     * @throws ServletException
     * @throws IOException */
    public boolean handleApplyCoupons(final DynamoHttpServletRequest req, final DynamoHttpServletResponse res)
                    throws ServletException, IOException {
          //Set Current Level explicitly
    	
    	if(getCouponPage()!=null && "COUPONS".equalsIgnoreCase(getCouponPage())){
    		getCheckoutState().setCurrentLevel("COUPONS");
    	}else if(getCouponPage()!=null && "SP_COUPONS".equalsIgnoreCase(getCouponPage())){
    		getCheckoutState().setCurrentLevel("SP_COUPONS");
    	}
    	
    	boolean rollback = false;
    	String jsonError="[";
    	
    	this.setAppliedCouponMap(null);
    	HashMap<String, String > appliedCouponMap = new HashMap<String, String>();
    	this.setAppliedCouponMap(appliedCouponMap);
    	
        Transaction tr = null;
        TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();
        try {
            if (!StringUtils.isEmpty(req.getParameter(BBBCoreConstants.CART))) {
                this.getCheckoutState().setCurrentLevel(DEFAULT_STATES.CART.toString());
            }
            this.preApplyCoupons();

            /* Start the transaction */
            if (tm != null) {
    			td.begin(tm, TransactionDemarcation.REQUIRES_NEW);
    		}
            
            if (!this.getFormError()) {
                synchronized (this.getOrder()) {
            	   
                    final Map<String, RepositoryItem> couponMap = ((BBBOrderImpl) this.getOrder()).getCouponMap();
                    final MutableRepositoryItem profile = (MutableRepositoryItem) this.getProfile();

                    final List<RepositoryItem> availablePromotions = (List<RepositoryItem>) profile
                                    .getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);

                    for (final Object element : availablePromotions) {
						// remove already granted promotion
                        final RepositoryItem promotion = (RepositoryItem) element;
                        this.getPromoTools().removePromotion((MutableRepositoryItem) this.getProfile(), promotion,
                                        false);
                    }

                    // Setting order object promo code null on removal of promotions.
                    ((BBBOrderImpl) this.getOrder()).setSchoolCoupon(null);
                    availablePromotions.clear();

                    final Set<String> keySet = couponMap.keySet();
                    String couponCode = null;
                    for (final Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
                        boolean couponError = true;
                        String couponErrorText = COUPON_GENERIC_ERROR;

                        couponCode = iterator.next();
                       if (!BBBUtility.isMapNullOrEmpty(this.getCouponList())) {
                            if (!this.getCouponList().containsValue(couponCode)) {
                                continue;
                            }
                        }
                        if (StringUtils.isEmpty(req.getParameter(BBBCoreConstants.CART)) && getCatalogUtil().isLogCouponDetails()) {
                        	logInfo("Request for Applying Coupon : " + couponCode);
                        }
                        // Start - Coupon Specific Messaging CR by Ayush
                        final Order order = this.getOrder();
                        final List<CommerceItem> commerceItemLists = order.getCommerceItems();
                        couponFilterVO couponFiltervo = new couponFilterVO();
                        try {
							for (final CommerceItem commerceItem : commerceItemLists) {
								if (BBBCheckoutConstants.SCHOOLPROMO.equalsIgnoreCase(couponCode)) {
									couponFiltervo = this.checkCommerceItemQualifierFlags(commerceItem, couponFiltervo,
											getCouponClaimCode());
								} else {
									couponFiltervo = this.checkCommerceItemQualifierFlags(commerceItem, couponFiltervo,
											couponCode);
								}
								if (couponFiltervo.isContainsNormal()) {
									break;
								}
							}
                        } catch (final BBBSystemException bse) {
                            this.logError("BBBSystem Exception occured while identifying a couponFilter ", bse);
                        } catch (final BBBBusinessException bbe) {
                            this.logError("BBBBusiness Exception occured while identifying a couponFilter ", bbe);
                        }
                       // RepositoryItem currentPromo  = null;
                        couponErrorText = getCouponErrorMessage(couponFiltervo);
                        // End - Coupon Specific Messaging CR by Ayush

                        // Start - Regular Code for All Normal or Hybrid Items
                        if (couponErrorText.equalsIgnoreCase(COUPON_GENERIC_ERROR)) {
                            final RepositoryItem currentPromo = couponMap.get(couponCode);
                        	//added for My offers Automation
							RepositoryItem coupon = null;
							if (BBBCheckoutConstants.SCHOOLPROMO.equalsIgnoreCase(couponCode)) {
								coupon = this.getClaimableManager().getClaimableTools()
										.getClaimableItem(getCouponClaimCode());
							} else {
								coupon = this.getClaimableManager().getClaimableTools().getClaimableItem(couponCode);
							}
                        	
                            try {
                                if (currentPromo != null) {
                                	String couponDescription =(String) currentPromo.getPropertyValue("displayName"); 	
                                    // Below Code is for R2 changes
                                    final boolean checkProm = this.getPromoTools().checkPromotionGrant(
                                                    this.getProfile(), currentPromo);
                                    if (this.getCouponList().values().contains(couponCode) && checkProm) {  
                                    	//added for My offers Automation to save coupons in active promotions of profile
    									this.getPromoTools().grantPromotion(profile, currentPromo, null, null, coupon);
    									this.getAppliedCouponMap().put(couponCode, couponDescription);
                                    }
                                    availablePromotions.add(currentPromo);
                                }
                            } catch (final PromotionException e) {
                                this.logError(LogMessageFormatter.formatMessage(req,
                                                "Exception occurred while applying coupons",
                                                BBBCoreErrorConstants.CART_ERROR_1023) + e.getStackTrace());
                                this.addFormException(this.createDropletException(PROMOTION_ERROR_ON_APPLY,
                                                PROMOTION_ERROR_ON_APPLY));
                                this.getCouponErrorList().put(couponCode, couponErrorText);
                                this.getCouponErrorList().put(BBBCoreConstants.ERROR, PROMOTION_ERROR_ON_APPLY);
                            }
                            final List listSelectedPromotion = (List) this.getProfile().getPropertyValue(
                                            BBBCoreConstants.SELECTED_PROMOTIONS_LIST);
                            if (!BBBUtility.isListEmpty(listSelectedPromotion)) {
                                if (listSelectedPromotion.contains(couponCode)) {
                                    couponError = false;
                                }
                            }
                        }
						else if(StringUtils.isEmpty(req.getParameter(BBBCoreConstants.CART)) && getCatalogUtil().isLogCouponDetails()){
                            	logInfo("The coupon : " + couponCode + " cannot be applied to any commerce item. Reason : " + getMsgHandler().getErrMsg(couponErrorText, null, null));
                        }
                        // End - Regular Code

                        if (couponError) {
                            this.logDebug("PromoCode " + couponCode + " is not applicable");
                            this.addFormException(this.createDropletException(GENERIC_ERROR_TRY_LATER,
                                            GENERIC_ERROR_TRY_LATER));
                            this.getCouponErrorList().put(couponCode, couponErrorText);
                            this.getCouponErrorList().put(BBBCoreConstants.ERROR, PROMOTION_ERROR_ON_APPLY);
                            jsonError= jsonError.concat("{\"ccode\":\""+couponCode+"\",\"cerror\":\""+getLblTxtTemplateManager().getErrMsg(couponErrorText,req.getLocale().getLanguage(), null, null)+"\"},");
                        }
                    }
                    profile.setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
                    int index= jsonError.lastIndexOf(",");
                    if(index!=-1){
                    	jsonError=jsonError.substring(0, index)+"]";
                    	this.setJsonCouponErrors(jsonError);
                    }
                    this.getPromoTools().initializePricingModels(req, res);
                    this.runRepricingProcess(req, res, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING);

                    if (this.isSchoolCoupon()) {
                        ((BBBOrderImpl) this.getOrder()).setSchoolCoupon(this.getCouponClaimCode());
                    }

                    this.getOrderManager().updateOrder(this.getOrder());
                }
            }
        } catch (final RunProcessException e) {
            this.logError(LogMessageFormatter.formatMessage(req, "Exception occurred while repricing coupons",
                            BBBCoreErrorConstants.CART_ERROR_1023), e);
            this.addFormException(this.createDropletException(GENERIC_ERROR_TRY_LATER, GENERIC_ERROR_TRY_LATER));
            this.getCouponErrorList().put(BBBCoreConstants.ERROR, GENERIC_ERROR_TRY_LATER);
            this.markTransactionRollback();
            rollback = true;
        } catch (final RepositoryException e) {
            this.logError(LogMessageFormatter.formatMessage(req, "Exception occurred while applying school promotions",
                            BBBCoreErrorConstants.CART_ERROR_1022), e);
            this.addFormException(this.createDropletException(GENERIC_ERROR_TRY_LATER, GENERIC_ERROR_TRY_LATER));
            this.getCouponErrorList().put(BBBCoreConstants.ERROR, GENERIC_ERROR_TRY_LATER);
            this.markTransactionRollback();
            rollback = true;
        } catch (final CommerceException e) {
            this.logError(BBBCoreErrorConstants.CART_ERROR_1021
                            + ": Commerce exception while updating Order for school promotions", e);
            rollback = true;
        }catch (final TransactionDemarcationException e){
        	logError("Error while creating transaction while applying coupons", e);
        }
        finally {
            // Complete the transaction
			if (tm != null) {
				try {
					td.end(rollback);
				} catch (TransactionDemarcationException e) {
					this.logError(
							"TransactionDemarcationException in ending the transaction from hanpleApplyCoupon() method in BBBCartFormHandler class",
							e);
            }
				if (rollback) {
					this.getPromoTools().initializePricingModels(req, res);
        }
			}

		}
        String redirectURL = "";
        if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL())
                        && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            redirectURL = req.getContextPath() + this.getCheckoutState().getFailureURL();
        } else if (BBBCoreConstants.ATG_REST_IGNORE_REDIRECT.equals(this.getCheckoutState().getFailureURL())){
        	redirectURL = this.getCheckoutState().getFailureURL();
	}
        return this.checkFormRedirect(redirectURL, redirectURL, req, res);// success
        // or
        // failure
        // show
        // the
        // same
        // page

    }

    
  
    /** This method does the school promotion related validation. First it checks if school coupon check box is selected
     * then promo code should not be blank. and promo code is matched with school coupon or not.
     *
     * @throws RepositoryException */
    private void preApplyCoupons() throws RepositoryException {
        this.logDebug("Start: method preApplyCoupons");
        if (this.isSchoolCoupon()) {
            if (atg.core.util.StringUtils.isEmpty(this.getCouponClaimCode())) {
                /** Once user has applied promo code successfully promo code will be picked from Order. */
                this.setCouponClaimCode(((BBBOrderImpl) this.getOrder()).getSchoolCoupon());
            }
            if (atg.core.util.StringUtils.isEmpty(this.getCouponClaimCode())) {
                this.addFormException(new DropletException(this.getMsgHandler().getErrMsg("err_schoolPromoCode_BLANK",
                                "EN", null), "schoolPromoCode_BLANK"));
                this.logDebug("Adding error: "
                                + this.getMsgHandler().getErrMsg("err_schoolPromoCode_BLANK", "EN", null));
            } else {
                final boolean comparison = this.getCouponUtil().compareClaimCode(this.getCouponClaimCode(),
                                this.getProfile().getPropertyValue(BBBCoreConstants.SCHOOL_PROMOTIONS));

                if (!Boolean.TRUE.equals(comparison)) {
                    this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                                    "err_schoolPromoCode_UNMATCH", "EN", null), "schoolPromoCode_UNMATCH"));
                    this.logDebug("Adding error: "
                                    + this.getMsgHandler().getErrMsg("err_schoolPromoCode_UNMATCH", "EN", null));
                }
            }
        }
        this.logDebug("End: method preApplyCoupons");
    }

    /** this method checks if user selected school coupon on coupon page or not.
     *
     * @return */
    private boolean isSchoolCoupon() {
        boolean flag = false;

        this.logDebug("Start: method isSchoolCoupon");

        flag = this.getCouponList().values().contains(BBBCheckoutConstants.SCHOOLPROMO);

        this.logDebug("End: method isSchoolCoupon, returned value: isSchoolCoupon checked" + flag);

        return flag;
    }

    /** Wrapper method for Rest for ApplyCoupons
     *
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException */
    public final boolean handleRepriceOrder(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final Map<String, String> failure = this.getCheckoutState().getCheckoutFailureURLs();
        final Map<String, String> success = this.getCheckoutState().getCheckoutSuccessURLs();
        String channel = BBBUtility.getChannel();
        if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
        	pRequest.setParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY, BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY_REQ);
		}
        final HashMap map = setRedirectUrlForRest();
        this.getCheckoutState().setCheckoutFailureURLs(map);
        this.getCheckoutState().setCheckoutSuccessURLs(map);
        final boolean status = this.handleMoveToPayment(pRequest, pResponse);
        this.getCheckoutState().setCheckoutFailureURLs(failure);
        this.getCheckoutState().setCheckoutSuccessURLs(success);
    	/* Defect BSL-2661-Start */
        if (((BBBPaymentGroupManager) this.getPaymentGroupManager()).checkGiftCard((BBBOrderImpl) this.getOrder())) {
			Transaction tr = null;
			try {
				tr = this.ensureTransaction();
				synchronized (this.getOrder()) {
					((BBBPaymentGroupManager) this.getPaymentGroupManager())
							.processPaymentGroupStatusOnLoad(this.getOrder());
				}
			} catch (CommerceException e) {
				this.logError(
						"handleRepriceOrder :: repriceGiftCard - Error updating in Commerce Item in repository",
						e);
					markTransactionRollback();
			} finally {
				// Complete the transaction
				if (tr != null) {
					this.commitTransaction(tr);
				}
			}
		}
		/* Defect BSL-2661-END */
        return status;
    }

    /** Just takes user to the next page in the checkout flow
     *
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException */

    public final boolean handleMoveToPayment(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("Starting method BBBCartFormHandler.handleMoveToPayment");

		 Transaction tr = null;
		 String redirectURL = null;
		 try {
			 synchronized (this.getOrder()) {
				 tr = this.ensureTransaction();
				 this.runRepricingProcess(pRequest, pResponse, null);
				 this.getOrderManager().updateOrder(this.getOrder());
			 }
			 // Added as Part of Story 83-N: Start
			 BBBOrderImpl order = (BBBOrderImpl)this.getOrder();
		 	if(order.isPayPalOrder()  && !this.getFormError()){
		 		if(this.getPayPalSessionBean().isFromPayPalPreview()){
	            	this.logDebug("Flow is from Paypal order Review Page, so setting redirect url to payment page");
	            	this.getCheckoutState().setCurrentLevel(
	                        CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString());
		 		}
			 	else {
	            	this.logDebug("Flow is from Paypal, so setting redirect url to review page, skipping payment page");
	            	this.getCheckoutState().setCurrentLevel(
	                        CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
	            }
		 	}
            // Added as Part of Story 83-N: End
            else{
		        this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString());
            }
            if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL())
                    && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
		        redirectURL = pRequest.getContextPath() + this.getCheckoutState().getFailureURL();
		    }
		 } catch (final RunProcessException exception) {
			 this.logError(LogMessageFormatter.formatMessage(pRequest,
					 "Error processing order repricing during MoveToPayment",
					 BBBCoreErrorConstants.CART_ERROR_1021), exception);
			 this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(GENERIC_ERROR_TRY_LATER, "EN",
					 null)));
		 } catch (final CommerceException commerceException) {
			 this.logError(BBBCoreErrorConstants.CART_ERROR_1021
					 + ": commerceException while updateorder during handleMoveToPayment()", commerceException);

        } finally {
            // Complete the transaction
            if (tr != null) {
                this.commitTransaction(tr);
            }
        }

        this.logDebug("Exiting method BBBCartFormHandler.handleMoveToPayment");

        if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL())
                        && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
            redirectURL = pRequest.getContextPath() + this.getCheckoutState().getFailureURL();
        }

		 return this.checkFormRedirect(redirectURL, redirectURL, pRequest, pResponse);
	 }

    /**
     * @param pJsonResultString
     * @throws BBBBusinessException
     */
    public final void addItemJSONObjectParser(final String pJsonResultString)
			throws BBBBusinessException {
		final String logMessage = this.getClass().getName()
				+ "CartJSONObjectParser";
		this.logDebug(logMessage + " Starts here");
		this.logDebug(logMessage + " add item input parameters --> "
				+ pJsonResultString);
		JSONObject jsonObject = null;
		jsonObject = (JSONObject) JSONSerializer.toJSON(pJsonResultString);
		final DynaBean JSONResultbean = (DynaBean) JSONSerializer
				.toJava(jsonObject);
		final List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);

		if (dynaBeanProperties.contains(ADD_ITEM_RESULTS)) {
			final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
					.get(ADD_ITEM_RESULTS);
			

			if (isPartialAddFlow()) {
				this.populateAddItemInfoForPartialAdd(itemArray);
			} else {
				this.setAddItemCount(itemArray.size());
				this.populateAddItemInfo(itemArray, this.getItems());
			}
		}
	}
    
    /**
	 * @param itemArray
	 * @param itemInfos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final void populateAddItemInfoForPartialAdd
(final List<DynaBean> itemArray) {
        
		//boolean validData = true;
		Long itemQty = 0L;
	    String skuId = "";
	    String prodId="";
	    String storeId="";
		MultipleIteminResponseVO itemResponseVo;
		listOfMultipleItemInResponseVO = new ArrayList<MultipleIteminResponseVO>();
		List<AddCommerceItemInfo> validItemIndexList =  new ArrayList<AddCommerceItemInfo>();
		AddCommerceItemInfo validItemInfo = new AddCommerceItemInfo();
		
			for (final DynaBean item : itemArray) {
			   //initialize for every Item
				    boolean validData = true;
		   //initialize for every Item
 			itemResponseVo = new MultipleIteminResponseVO();
					
					if(null!=item.get(SKU_ID))
					{
						skuId=item.get(SKU_ID).toString();
						itemResponseVo.setSkuId(skuId)	;
					}
					if(null!=item.get(PRODUCT_ID))
					{
					prodId=item.get(PRODUCT_ID).toString();
                    itemResponseVo.setProductId(prodId);
					}
					if(null != item.get(STORE_ID))
					{
						storeId=item.get(STORE_ID).toString();
						itemResponseVo.setStoreId(storeId);
							
					}
					if (validData) {

						validData = this.validateSkuInfoForPartialAdd(skuId,this.getSiteId(), itemResponseVo);

						final List<String> itemPropertiesForValidation = getPropertyNames(item);

						if(itemPropertiesForValidation.size()> 0)
						{

							if (itemPropertiesForValidation.contains(REGISTRY_ID) && (null != item.get(REGISTRY_ID)) && !(item.get(REGISTRY_ID).toString().equals(""))) {
								String registryId = item.get(REGISTRY_ID).toString();
								validData = this.validateRegistryInfoForPartialAdd(registryId,
										this.getSiteId(),
										itemResponseVo);
							}

							if (validData) {
									if (itemPropertiesForValidation.contains(STORE_ID) && (null != item.get(STORE_ID)) && !(storeId.equals(""))) {
										
											validData = this.validateStoreInfoForPartialAdd(storeId, itemResponseVo);
									}
							}
						}				

						try {
							itemQty = Long.parseLong(item.get("qty").toString());
						} catch (final NumberFormatException e) {

							logError(INVALID_QUANTITY_FORMAT+e.getMessage());
							itemResponseVo.setSkuId(skuId);
							itemResponseVo.setErrorCode("");
							itemResponseVo.setErrorMessage(INVALID_QUANTITY_FORMAT);
							itemResponseVo.setStatus(validData);
							validData=false;

						}
						if (validData) {
							validData = this.validateInventoryForPartialAdd(  this.getSiteId(),
									storeId, skuId, registryId, itemQty, getOrder(), itemResponseVo);

						}
					}

					if (!validData) {
						// populate itemResponseVo
						itemResponseVo.setStatus(false);
					    itemResponseVo.setProductId(prodId);
						itemResponseVo.setSkuId(skuId);
						itemResponseVo.setStoreId(storeId);
						
					} else {
						
						itemResponseVo.setStatus(true);
						itemResponseVo.setErrorCode("");
						itemResponseVo.setErrorMessage("");
						validItemInfo = new AddCommerceItemInfo();
						
						// valid input populate data in itemInfos
						boolean isSkuLtl = false;
						try {
							isSkuLtl = getCatalogUtil().isSkuLtl(
							this.getSiteId(), item.get("skuId").toString());
						} catch (BBBSystemException bse) {
							logError("BBBSystemException occured in BBBCartFormHandler.populateAddItemInfoForPartialAdd :"+bse.getMessage());
						} catch (BBBBusinessException bbe) {
							logError("BBBSystemException occured in BBBCartFormHandler.populateAddItemInfoForPartialAdd :"+bbe.getMessage());
						}
						validItemInfo.setCatalogRefId(item.get("skuId")
								.toString());
						validItemInfo.setProductId(item.get("prodId")
								.toString());
						try {
							validItemInfo.setQuantity(Long.parseLong(item
									.get("qty").toString()));
						} catch (final NumberFormatException e) {
							
							logError(INVALID_QUANTITY_FORMAT+e.getMessage());
							
						}
						final List<String> itemProperties = getPropertyNames(item);
						if (!isSkuLtl) {
							if (itemProperties.contains(REGISTRY_ID)) {
								validItemInfo.getValue().put(REGISTRY_ID,
										item.get(REGISTRY_ID).toString());
							}
						}

						if ((itemProperties.contains(STORE_ID))
								&& (null != item.get(STORE_ID))
								&& !(item.get(STORE_ID).toString().equals(""))) {
							validItemInfo.getValue().put(STORE_ID,
									item.get(STORE_ID).toString());
							if (!StringUtils.isEmpty(this.getWishlistItemId())) {
								this.setStoreId(item.get(STORE_ID).toString());
								final String countCart = (String) item
										.get("count");
								if (!StringUtils.isEmpty(countCart)) {
									this.setCountNo(Integer.parseInt(countCart));
								}
							}
						}
						validItemInfo.getValue().put("bts",
								item.get("bts").toString());

						if (isSkuLtl) {
							if (item.get(BBBCatalogConstants.LTL_SHIP_METHOD)
									.toString()
									.equalsIgnoreCase(
											BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)) {
								// checks if shipping method code for LTL item
								// is
								// white glove with assembly if true then
								// shipping
								// method name is white glove
								// and assembly is true.
								validItemInfo
										.getValue()
										.put(BBBCatalogConstants.LTL_SHIP_METHOD,
												BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
								validItemInfo
										.getValue()
										.put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY,
												"true");

							} else {
								// In case not while glove with assembly for LTL
								// item then set whatever is coming as input
								validItemInfo
										.getValue()
										.put(BBBCatalogConstants.LTL_SHIP_METHOD,
												item.get(
														BBBCatalogConstants.LTL_SHIP_METHOD)
														.toString());
								validItemInfo
										.getValue()
										.put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY,
												"false");
							}
						}

						this.logDebug("item added Info - Sku id: "
								+ validItemInfo.getCatalogRefId()
								+ " Product id: "
								+ validItemInfo.getProductId()
								+ " Quantiy: " + validItemInfo.getQuantity()
								+ " GiftList ID: "
								+ validItemInfo.getGiftlistId()
								+ " GiftListItem Id: "
								+ validItemInfo.getGiftlistItemId()
								+ " CommerceItemType: "
								+ validItemInfo.getCommerceItemType()
								+ " ShippingGroupType: "
								+ validItemInfo.getShippingGroupType()
								+ " Registry ID: "
								+ validItemInfo.getValue().get(REGISTRY_ID)
								+ " StoreId: "
								+ validItemInfo.getValue().get(STORE_ID)
								+ " bts: "
								+ validItemInfo.getValue().get("bts"));

 						validItemIndexList.add(validItemInfo);
						//count++;
					}
					validData= true;
					// add item to list
					listOfMultipleItemInResponseVO.add(itemResponseVo);
					
				}// for loop end
//			TODO: Add log message , this is an important step 	
		if(validItemIndexList.size() > 0){
			setAddItemCount(validItemIndexList.size());
			populateItemInfoforPartialAdd(getItems(), validItemIndexList);
		}
				
	}

	private int populateItemInfoforPartialAdd(
			final AddCommerceItemInfo[] itemInfos, 
			List<AddCommerceItemInfo> validItemIndexList) {
		
		int count = 0;
		for(AddCommerceItemInfo itemInfoItr: validItemIndexList){
			itemInfos[count] = itemInfoItr;
			count++;
		}
		return count;
	}

    /** @param itemArray
     * @param count
     * @return */
    public final int populateAddItemInfo(final List<DynaBean> itemArray, final AddCommerceItemInfo[] itemInfos) {
        int count = 0;
        // R2.1 Changes for junit
        if (!StringUtils.isEmpty(this.getWishlistItemId()) && this.isRestService()) {
            final List<GiftListVO> vo = this.getSavedItemsSessionBean().getItems();
            if ((vo != null) && !vo.isEmpty()) {
                for (final GiftListVO tempvo : vo) {
                    if (tempvo.getWishListItemId().equals(this.getWishlistItemId())) {
                        itemInfos[count].setCatalogRefId(tempvo.getSkuID());
                        itemInfos[count].setProductId(tempvo.getProdID());
                        itemInfos[count].setQuantity(tempvo.getQuantity());
                        boolean isSkuLtl= false;
                        try {
                            isSkuLtl=getCatalogUtil().isSkuLtl(this.getSiteId(), tempvo.getSkuID());
                        } catch (final NumberFormatException e) {
                            this.addFormException(this.createDropletException(INVALID_QUANTITY_FORMAT,
                                            INVALID_QUANTITY_FORMAT));
                        } catch (BBBSystemException e) {
                        	this.logError("System exception while checking is sku LTL for skuID: "+tempvo.getSkuID()+e.getMessage(), e);
						} catch (BBBBusinessException e) {
							this.logError("System exception while checking is sku LTL for skuID: "+tempvo.getSkuID()+e.getMessage(), e);
                        }
                       
                        if (tempvo.getRegistryID() != null) {
                            itemInfos[count].getValue().put(BBBCoreConstants.REGISTRY_ID, tempvo.getRegistryID());
                        } else {
                            itemInfos[count].getValue().put(BBBCoreConstants.REGISTRY_ID, BBBCoreConstants.BLANK);
                        }
                        if (tempvo.getStoreID() != null) {
                            itemInfos[count].getValue().put(BBBCoreConstants.STOREID, tempvo.getStoreID());
                        } else {
                            itemInfos[count].getValue().put(BBBCoreConstants.STOREID, BBBCoreConstants.BLANK);
                        }
                        if (itemArray != null) {
                            itemInfos[count].getValue().put(BTS2, itemArray.get(0).get(BTS2).toString());
                        } else {
                            itemInfos[count].getValue().put(BTS2, "false");
                        }
                        
                        if(isSkuLtl)
    	                {                         
    	                		itemInfos[count].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD,"");
    	                }
                        this.setUndoCheck(true);
                        this.setFromCart(true);
                        this.setFromWishlist(true);
                        this.logDebug("item added Info - Sku id: " + itemInfos[count].getCatalogRefId()
                                        + " Product id: " + itemInfos[count].getProductId() + " Quantiy: "
                                        + itemInfos[count].getQuantity() + " GiftList ID: "
                                        + itemInfos[count].getGiftlistId() + " GiftListItem Id: "
                                        + itemInfos[count].getGiftlistItemId() + " CommerceItemType: "
                                        + itemInfos[count].getCommerceItemType() + " ShippingGroupType: "
                                        + itemInfos[count].getShippingGroupType() + " Registry ID: "
                                        + tempvo.getRegistryID() + " StoreId: " + tempvo.getStoreID());
                    }
                }
            }
        } else {
        	 try 
             {
            for (final DynaBean item : itemArray) {
            	if(StringUtils.isBlank((String) item.get(PROD_ID)) || StringUtils.isBlank((String) item.get("skuId")) || StringUtils.isBlank((String) item.get("qty")) || !(Long.parseLong((String) item.get("qty")) > BBBCoreConstants.ZERO)) {
					continue;
				}
            	boolean isSkuLtl=getCatalogUtil().isSkuLtl(this.getSiteId(), item.get("skuId").toString());
            	itemInfos[count].setCatalogRefId(item.get("skuId").toString());
                itemInfos[count].setProductId(item.get(PROD_ID).toString());
                try {
                    itemInfos[count].setQuantity(Long.parseLong(item.get("qty").toString()));
                } catch (final NumberFormatException e) {
                    this.addFormException(this.createDropletException(INVALID_QUANTITY_FORMAT, INVALID_QUANTITY_FORMAT));
                }
                final List<String> itemProperties = getPropertyNames(item);
                if (itemProperties.contains(REGISTRY_ID)) {
                    itemInfos[count].getValue().put(REGISTRY_ID, item.get(REGISTRY_ID).toString());
                }
                // TBS changes start
                if (itemProperties.contains("pagetype")) {
                    itemInfos[count].getValue().put("pagetype", item.get("pagetype").toString());
                }
                //TBS changes end
                
                if ((itemProperties.contains(STORE_ID)) && (null != item.get(STORE_ID))
                                && !(item.get(STORE_ID).toString().equals(""))) {
                    itemInfos[count].getValue().put(STORE_ID, item.get(STORE_ID).toString());
                    if (!StringUtils.isEmpty(this.getWishlistItemId())) {
                        this.setStoreId(item.get(STORE_ID).toString());
                        final String countCart = (String) item.get("count");
                        if (!StringUtils.isEmpty(countCart)) {
                            this.setCountNo(Integer.parseInt(countCart));
                        }
                    }
                }
                 if (itemProperties.contains("refNum") && null != item.get("refNum") && !(item.get("refNum").toString().equals(""))) {
                    itemInfos[count].getValue().put("referenceNumber", item.get("refNum").toString());
                }
                itemInfos[count].getValue().put(BTS2, item.get(BTS2).toString());
                
                itemInfos[count].getValue().put("bts", item.get("bts").toString());
                
	                if(isSkuLtl)
	                {                         
	                	String ltlShippingMethod = "";
	                	try{
	                		ltlShippingMethod = item.get(BBBCatalogConstants.LTL_SHIP_METHOD).toString();
                } catch(MorphException me) {
                	// Do nothing and consider ltlShippingMethod = ""
                	//logError("Cannot not parse "+BBBCatalogConstants.LTL_SHIP_METHOD+" propery from Json",me);
					if(isLoggingDebug()){
                		this.logDebug("Cannot parse "+BBBCatalogConstants.LTL_SHIP_METHOD+" propery from Json",me);
                	}
                }
	                	if(ltlShippingMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD))
	                	{
	                		//checks if shipping method code for LTL item is white glove with assembly if true then shipping method name is white glove 
	                		//and assembly is true.
		                	itemInfos[count].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD,BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
		                	itemInfos[count].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY,"true");
	                	
	                	}
	                	else
	                	{
	                		//In case not while glove with assembly for LTL item then set whatever is coming as input
	                		itemInfos[count].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD,ltlShippingMethod);
	                		itemInfos[count].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY,"false");
		                }
						//BPSI-1192 for Json Call from rest
	                	 if (itemProperties.contains("shipMethodUnsupported") && null != item.get("shipMethodUnsupported")) {
	                         itemInfos[count].getValue().put("shipMethodUnsupported", item.get("shipMethodUnsupported").toString());
	                         if(item.get("shipMethodUnsupported").toString().equalsIgnoreCase(BBBCoreConstants.TRUE)){
	                        	 itemInfos[count].getValue().put("prevLtlShipMethod",itemInfos[count].getValue().get(BBBCatalogConstants.LTL_SHIP_METHOD));
		                	 	 itemInfos[count].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD,"");
	                }
	                	 }
	                }
                
           
                this.logDebug("item added Info - Sku id: " + itemInfos[count].getCatalogRefId() + " Product id: "
                                + itemInfos[count].getProductId() + " Quantiy: " + itemInfos[count].getQuantity()
                                + " GiftList ID: " + itemInfos[count].getGiftlistId() + " GiftListItem Id: "
                                + itemInfos[count].getGiftlistItemId() + " CommerceItemType: "
                                + itemInfos[count].getCommerceItemType() + " ShippingGroupType: "
                                + itemInfos[count].getShippingGroupType() + " Registry ID: "
                                + itemInfos[count].getValue().get(REGISTRY_ID) + " StoreId: " + itemInfos[count].getValue().get(STORE_ID)
                                + " bts: " + itemInfos[count].getValue().get(BTS2));
                                
                count++;
            }
        }
        	 
        catch(BBBSystemException e)
         {
         logError("Error retrieving the isSkuLtl value");
         }
         catch(BBBBusinessException e)
         {
               	logError("Error retrieving the isSkuLtl value");
         }
        }
        return count;
    }

    /* To get the properties names from JSON result string */
    private static List<String> getPropertyNames(final DynaBean pDynaBean) {

        final DynaClass dynaClass = pDynaBean.getDynaClass();
        final DynaProperty[] properties = dynaClass.getDynaProperties();
        final List<String> propertyNames = new ArrayList<String>();
        for (final DynaProperty propertie : properties) {
            final String name = propertie.getName();
            propertyNames.add(name);
        }
        return propertyNames;
    }

    /** @return the jsonResultString */
    public final String getJsonResultString() {
        return this.jsonResultString;
    }

    /** @param jsonResultString the jsonResultString to set */
    public final void setJsonResultString(final String jsonResultString) {
        this.jsonResultString = jsonResultString;
    }

    /** Returns the array of exceptions that occurred for display in the page
     *
     * @beaninfo description: List of exceptions that occurred during processing */
    @Override
    public final Vector getFormExceptions() {
        Vector mExceptions = super.getFormExceptions();
        final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        if (request != null) {
            final NameContext ctx = request.getRequestScope();
            if (ctx != null) {
                final Vector v = (Vector) ctx.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE);
                if ((v != null) && !v.isEmpty()) {
                    if (mExceptions != null) {
                        mExceptions.addAll(v);
                    } else {
                        mExceptions = v;
                    }
                }
            }
        }

        return mExceptions;
    }

    /** Delete all items from the order whose id appears in the RemovalCommerceIds property.
     *
     * @param pRequest servlet request object
     * @param pResponse servlet response object
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
    @Override
    protected final void
                    deleteItems(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {
        // Omniture Changes
    	final Order order = this.getOrder();
        try {
            if ((this.getRemovalCommerceIds() != null)
                            && (this.getOrder().getCommerceItem(this.getRemovalCommerceIds()[0]) != null)) {
                this.setDeletedProdId(this.getOrder().getCommerceItem(this.getRemovalCommerceIds()[0])
                                .getAuxiliaryData().getProductId());
            }
            ((BBBCommerceItemManager)this.getCommerceItemManager()).removeDeliveryAssemblyCI(this.getRemovalCommerceIds(), order);
        } catch (final CommerceItemNotFoundException e) {
            this.processException(e, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
        } catch (final InvalidParameterException e) {
            this.processException(e, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
        } catch (CommerceException e) {
        	this.processException(e, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
		}

        super.deleteItems(pRequest, pResponse);
        final List<String> removedEcoFeeItemIdsList = new ArrayList<String>();
        for (final String removalCommerceId : this.getRemovalCommerceIds()) {
            for (final ShippingGroup sg : (List<ShippingGroup>) order.getShippingGroups()) {
                if ((sg instanceof BBBHardGoodShippingGroup)
                                && ((BBBHardGoodShippingGroup) sg).getEcoFeeItemMap().containsKey(removalCommerceId)) {
                    final String onlineEcoFeeCommerceItemId = ((BBBHardGoodShippingGroup) sg).getEcoFeeItemMap().get(
                                    removalCommerceId);
                    removedEcoFeeItemIdsList.add(onlineEcoFeeCommerceItemId);
                }
                if ((sg instanceof BBBStoreShippingGroup)
                                && ((BBBStoreShippingGroup) sg).getEcoFeeItemMap().containsKey(removalCommerceId)) {
                    final String storeEcoFeeCommerceItemId = ((BBBStoreShippingGroup) sg).getEcoFeeItemMap().get(
                                    removalCommerceId);
                    removedEcoFeeItemIdsList.add(storeEcoFeeCommerceItemId);
                }
            }
        }

        final Map extraParams = this.createRepriceParameterMap();
        try {
            final List deletedEcoSkus = this.getPurchaseProcessHelper().deleteItems(order,
                            removedEcoFeeItemIdsList.toArray(new String[removedEcoFeeItemIdsList.size()]),
                            this.getUserPricingModels(), this.getUserLocale(), this.getProfile(), this, extraParams);

            this.getDeletedSkus().addAll(deletedEcoSkus);

        } catch (final CommerceException ce) {
            this.processException(ce, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
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

    @Override
    public final boolean isLoggingDebug() {
        return this.getCommonConfiguration().isLogDebugEnableOnCartFormHandler();
    }
    
    @Override
    protected void addItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    		throws ServletException, IOException {
		if (this.getOrder().getCommerceItemCount() == BBBCoreConstants.ZERO) {
            final String originOfTraffic = BBBUtility.getOriginOfTraffic();
            if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(originOfTraffic)) {
                this.getOrder().setOriginOfOrder(BBBCoreConstants.CHANNEL_MOBILE_WEB);
            } else if(BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(originOfTraffic)){
                this.getOrder().setOriginOfOrder(BBBCoreConstants.CHANNEL_MOBILE_APP);
            }  else if (BBBCoreConstants.THIRD_PARTY_MOBILE_APP.equalsIgnoreCase(originOfTraffic)) {
            	this.getOrder().setOriginOfOrder(BBBCoreConstants.CHANNEL_THIRD_PARTY_MOBILE_APP);
            }
        }
		adddItemToOrderSuperCall(pRequest, pResponse);
    	//Adding porch service to commerceItem 
		addPorchServiceToCommerceItem(pRequest);
    }

	/**
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void adddItemToOrderSuperCall(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		super.addItemToOrder(pRequest, pResponse);
	}

    /** Method is used to add items retrieved from Cookie into the current order object. If preAddItem gives error for
     * any of the item then that item is not added into cart
     *
     * @param cookieItemArr
     * @param pRequest
     * @param pResponse
     * @throws IOException
     * @throws ServletException */
    public final void addItemsFromCookie(final AddCommerceItemInfo[] cookieItemArr,
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                    throws IOException, ServletException {

        final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
        final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
            Transaction tr = null;

            try {
                tr = this.ensureTransaction();
                if (this.getUserLocale() == null) {
                    this.setUserLocale(this.getUserLocale(pRequest, pResponse));
                }
                boolean orderModified = false;
                synchronized (this.getOrder()) {
                    for (final AddCommerceItemInfo element : cookieItemArr) {
                        this.setAddItemCount(1);
                        this.getItems()[0] = element;
                        this.resetFormExceptions();
                        this.preAddItemToOrder(pRequest, pResponse);
                        
                        if (!this.getFormError()) {
                            this.addItemToOrder(pRequest, pResponse);
                            if (this.getFormError()) {
                                this.logError(LogMessageFormatter.formatMessage(pRequest,
                                                "Exception adding items in cart ",
                                                BBBCoreErrorConstants.CART_ERROR_1000));

                                this.markTransactionRollback();
                                return;
                            }
							addDeliverySurchargeAssemblyFeeLtlItem(false);
                            if(this.getFormError())
                            {
                            	this.logError(LogMessageFormatter.formatMessage(pRequest,
                                        "Exception adding items in cart ",
                                        BBBCoreErrorConstants.CART_ERROR_1000));

                                this.markTransactionRollback();
                                return;	
                            }
                            orderModified = true;
                        }
                    }
                    if (orderModified) {
                        this.resetFormExceptions();
                        this.updateOrder(this.getOrder(), ERROR_UPDATING_ORDER, pRequest, pResponse);
                    }
                    if (this.getFormError()) {
                        this.markTransactionRollback();
                        return;
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
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);

            }

        }
        return;
    }

    /** Handle method to be called from REST to remove an item from the cart.
     *
     * @param pRequest
     * @param pResponse
     * @return Succe
     * @throws ServletException
     * @throws IOException */
    public final boolean handleRemoveItemFromCart(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        if ((this.getRemovalCommerceIds() != null) && (this.getRemovalCommerceIds().length > 0)) {
            this.logDebug("BBBCArtFormHandler.handleRemoveItemFromCart- Calling : superhandleRemoveItemFromOrder");
            this.setRemoveItemFromOrderErrorURL(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
            this.setRemoveItemFromOrderSuccessURL(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
            return super.handleRemoveItemFromOrder(pRequest, pResponse);
        }
        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                        BBBCoreConstants.NO_SKU_ID_TO_REMOVE_FROM_CART, "EN", null),
                        BBBCoreConstants.NO_SKU_ID_TO_REMOVE_FROM_CART));
        return false;
    }

    /** Handle method to be called from REST to update item in the cart.
     *
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException */
    public final boolean handleUpdateCart(final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        if (!StringUtils.isEmpty(this.updateCartInfoSemiColonSeparated)) {
            final String[] items = this.updateCartInfoSemiColonSeparated.split(UPDATE_CART_DELIMETER);

            for (final String item : items) {
                final String[] commerceItem = item.split(UPDATE_CART_EQUAL);
                final String commerceID = commerceItem[0];
                final String quantity = commerceItem[1];
                pRequest.setParameter(commerceID, quantity);
            }
            pRequest.setParameter(BBBCoreConstants.FROM_CART, true);
            return super.handleSetOrderByCommerceId(pRequest, pResponse);
        }
        this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
                        BBBCoreConstants.NO_SKU_ID_TO_UPDATE_IN_CART, "EN", null),
                        BBBCoreConstants.NO_SKU_ID_TO_UPDATE_IN_CART));
        return false;

    }

    /** Setter for removeItemIdParam sets the OOB param removalCommerceIds
     *
     * @param removeItemIdParam */
    public final void setRemoveItemIdParam(final String removeItemIdParam) {

        if (StringUtils.isNotBlank(removeItemIdParam)) {
            final String[] args = { removeItemIdParam };
            if (args.length > 0) {
                this.setRemovalCommerceIds(args);
            } else {
                this.setRemovalCommerceIds(null);
            }
        }

        this.removeItemIdParam = removeItemIdParam;
    }

    /** Getter for removeItemIdParam
     *
     * @return */
    public final String getRemoveItemIdParam() {
        return this.removeItemIdParam;
    }

    /** Added new method to remove form exceptions Sunil 09/11/2013 New exception to the list of FormExceptions.
     *
     * @param request */
    public final void removeFormException(final DynamoHttpServletRequest request) {

        this.logDebug("Removing form exception: ");
        if (request != null) {
            final NameContext namingContext = request.getRequestScope();
            if (namingContext != null) {
                Vector excVector = (Vector) namingContext.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE);
                if (null != excVector) {
                    excVector = new Vector();
                    this.getFormExceptions().clear();
                    request.setAttribute(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, excVector);
                    namingContext.putElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, excVector);
                }

            }
        }
    }

    @Override
    public void logDebug(final String pMessage) {
        if (this.isLoggingDebug()) {
            this.logDebug(pMessage, null);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBCartFormhandler [siteManager=").append(this.siteManager).append(", searchStoreManager=")
                        .append(this.searchStoreManager).append(", giftRegistryManager=")
                        .append(this.giftRegistryManager).append(", messageHandler=").append(this.messageHandler)
                        .append(", checkoutState=").append(this.checkoutState).append(", commonConfiguration=")
                        .append(this.commonConfiguration).append(", promotionTools=").append(this.promotionTools)
                        .append(", storeInventoryContainer=").append(this.storeInventoryContainer)
                        .append(", wishlistManager=").append(this.wishlistManager).append(", catalogTools=")
                        .append(this.catalogTools).append(", savedItemsSessionBean=")
                        .append(this.savedItemsSessionBean).append(", pricingManager=").append(this.pricingManager)
                        .append(", couponUtil=").append(this.couponUtil).append(", checkoutManager=")
                        .append(this.checkoutManager).append(", moveAllItemFailureResult=")
                        .append(this.moveAllItemFailureResult).append(", coupons=").append(this.coupons)
                        .append(", couponErrors=").append(this.couponErrors).append(", wishListItemIdsToRemove=")
                        .append(this.wishListItemIdsToRemove).append(", productList=").append(this.productList)
                        .append(", storeId=").append(this.storeId).append(", itemIdJustMovedBack=")
                        .append(this.itemIdJustMovedBack).append(", systemErrorPage=").append(this.systemErrorPage)
                        .append(", commerceItemId=").append(this.commerceItemId).append(", removeItemIdParam=")
                        .append(this.removeItemIdParam).append(", wishListId=").append(this.wishListId)
                        .append(", wishlistItemId=").append(this.wishlistItemId).append(", removalStoreId=")
                        .append(this.removalStoreId).append(", globalErrorURL=").append(this.globalErrorURL)
                        .append(", updateCartInfoSemiColonSeparated=").append(this.updateCartInfoSemiColonSeparated)
                        .append(", couponPage=").append(this.couponPage).append(", jsonResultString=")
                        .append(this.jsonResultString).append(", couponClaimCode=").append(this.couponClaimCode)
                        .append(", deletedProdId=").append(this.deletedProdId).append(", registryId=")
                        .append(this.registryId).append(", quantityAdded=").append(this.quantityAdded)
                        .append(", count=").append(this.count).append(", checkItemQuantity=")
                        .append(this.checkItemQuantity).append(", fromCart=").append(this.fromCart)
                        .append(", restService=").append(this.restService).append(", undoCheck=")
                        .append(this.undoCheck).append(", fromWishlist=").append(this.fromWishlist)
                        .append(", expressCheckout=").append(this.expressCheckout).append(", fromPipelineFlag=")
                        .append(this.fromPipeline).append(", newItemAdded=").append(this.newItemAdded).append("]");
        return builder.toString();
    }
    
    /**
     * R2.2 Paypal Checkout
     * This method is called when user wants to pay with pay pal and clicks on paypal button
     * @param pRequest
     * @param pResponse
     * @return boolean
     * @throws ServletException
     * @throws IOException
     * 
     */
	public final boolean handleCheckoutWithPaypal(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.logDebug("BBBCartFormHandler ::handleCheckoutWithPaypal method -  start ");
		final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
		final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		this.errorMap = new HashMap<String, String>();
		if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.PAYPAL_CHECKOUT, myHandleMethod);
			
			//Start: 258 - Verified by visa - refresh back button story - resetting the page state
	        pRequest.getSession().removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS);
	        //End: 258 - Verified by visa - refresh back button story - resetting the page state
			
			Transaction tr = null;
			try {
				tr = this.ensureTransaction();
				if (this.getUserLocale() == null) {
					this.setUserLocale(this.getUserLocale(pRequest, pResponse));
				}

				synchronized (this.getOrder()) {
					BBBOrder order = (BBBOrder) getOrder();
					// LTL-1548 | Remove Empty shipping group before PayPal is hit 
					final BBBShippingGroupManager shpGrpManager = (BBBShippingGroupManager) this.getShippingGroupManager();
                    try {
                        shpGrpManager.removeEmptyShippingGroups(this.getOrder());
                        this.getOrderManager().updateOrder(this.getOrder());
                    } catch (final CommerceException e) {
                        this.logError(LogMessageFormatter.formatMessage(pRequest, EXCEPTION_REMOVING_EMPTY_SHIPPING_GROUPS,
                                        BBBCoreErrorConstants.CART_ERROR_1020), e);
                    }
                    // LTL-1548 | Remove Empty shipping group | Ends
					// Validate shipping method on returning to cart
					// Set Checkout state to cart if clicked from cart page
					if (isFromCart()) {
						this.logDebug("CartFormHandler.handleCheckoutWithPaypal() :  Request is from Cart Page");
						this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
						if(this.getInventoryManager().checkUncachedInventory(order)){
				   			String successURL = pRequest.getContextPath() + this.getCheckoutState().getFailureURL();
							return this.checkFormRedirect(successURL, null, pRequest, pResponse);
						}
					}
					// this.preCheckoutWithPaypal(pRequest, pResponse);
				
					BBBSetExpressCheckoutResVO resType = null;
					String sucessURL = BBBCoreConstants.BLANK;
					String token = null;
					validatePorchServiceWithPreferedAddressZipCode();
					try {
						if (!order.isBopusOrder() && validateOrderForPPCall(pRequest.getContextPath())) {
							
							this.logDebug("BBBCartFormHandler.handleCheckoutWithPaypal() :  Order is validated for Paypal and calling setExpCheckout webservice");
							String hostUrl = pRequest.getScheme() + BBBCoreConstants.CONSTANT_SLASH + pRequest.getHeader(BBBCoreConstants.HOST) + pRequest.getContextPath();
							if (!StringUtils.isEmpty(getMobileCancelURL()) && !StringUtils.isEmpty(getMobileRedirectURL())) {
								resType = getPaypalServiceManager().doSetExpressCheckOut(order, getMobileCancelURL(), getMobileRedirectURL(), getPayPalSessionBean(), getUserProfile());
							}else{
								String cancelUrl = "";
								String returnUrl = "";
								if(isFromCart()){
									cancelUrl = hostUrl + getCheckoutState().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
									returnUrl = hostUrl + getCheckoutState().getCheckoutSuccessURLs().get(CheckoutProgressStates.DEFAULT_STATES.VALIDATION.toString()) ;
								}
								else {
									getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
									cancelUrl = hostUrl + getCheckoutState().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString());
									//returnUrl = hostUrl + getCheckoutState().getCheckoutSuccessURLs().get(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString());
									//BBBSL-6713 | Changing return URL in case user comes from payment page flow for Omniture 
									returnUrl = hostUrl + getCheckoutState().getCheckoutSuccessURLs().get(CheckoutProgressStates.DEFAULT_STATES.INTERMEDIATE_PAYPAL.toString()) ;
								}
								resType = getPaypalServiceManager().doSetExpressCheckOut(order, cancelUrl, returnUrl, getPayPalSessionBean(), getUserProfile());
							}	
							
							if (resType != null && !StringUtils.isEmpty(resType.getToken())) {

								token = resType.getToken();
								this.logDebug("BBBCartFormHandler.handleCheckoutWithPaypal() :  token " + token);
								// *********this paypalToken is sent to MOBILE
								// in response******************
								setPaypalToken(token);
								
							} else if (resType != null && resType.getErrorStatus().isErrorExists()) {
								logError(" Error Msg Recieved while calling paypal service= " + resType.getErrorStatus().getErrorMessage());
								if(resType.getErrorStatus().getErrorId() == BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID){
									addFormException(new DropletException(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null),BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR));
									this.errorMap.put(String.valueOf(resType.getErrorStatus().getErrorId()), this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null));
									logError("Error ID: " + resType.getErrorStatus().getErrorId() + " Error Message: " + this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null));
									//Added as part of R2.2.1 Story - Redirects to shipping page when paypal rejects the shipping address - Start
									this.markTransactionRollback();
									this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString());
									if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL()) && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)){
										this.payPalErrorURL = pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(BBBPayPalConstants.SHIPPING_SINGLE);
									}
									List<String> errorList = new ArrayList<String>();
									errorList.add(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null));
									this.getPayPalSessionBean().setErrorList(errorList);
									return checkFormRedirect(null, this.payPalErrorURL, pRequest, pResponse);
									//Added as part of R2.2.1 Story - Redirects to shipping page when paypal rejects the shipping address - End
								}
								else{
									addFormException(new DropletException(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null),BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
									this.errorMap.put(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
									logError("Error ID: " + resType.getErrorStatus().getErrorId() + " Error Message: " + this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
								}
							}
							
							if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL()) && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
								sucessURL = (getPaypalServiceManager().getPayPalRedirectURL()) + token;
								setPayPalSucessURL(sucessURL);
								
							} else if (StringUtils.isEmpty(getMobileCancelURL())) {
								sucessURL = (getPaypalServiceManager().getPayPalRedirectURL()) + token;
								setPayPalSucessURL(sucessURL);
								setMoveToPurchaseInfoSuccessURL(sucessURL);
							}
							this.logDebug("BBBCartFormHandler.handleCheckoutWithPaypal() : payPalErrorURL: " + this.payPalErrorURL + " SucessURL: " + sucessURL);
						}
						// this is for /tbs purpose
						postCheckoutWithPaypal(pRequest, pResponse);
					} catch (BBBSystemException systemException) {
						this.markTransactionRollback();
						this.logError("BBBSystemException occured in BBBCartFormHandler.handleCheckoutWithPaypal :", systemException);
						addFormException(new DropletException(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null),BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
						this.errorMap.put(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
						logError("Error Message: " + this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
					} catch (BBBBusinessException businessException) {
						this.markTransactionRollback();
						this.logError("BBBBusinessException occured in BBBCartFormHandler.handleCheckoutWithPaypal :", businessException);
						addFormException(new DropletException(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null), BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
						this.errorMap.put(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
						logError("Error Message: " + this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
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
		this.logDebug("BBBCartFormHandler ::handleCheckoutWithPaypal method - mthod ends ");
		return checkFormRedirect(getPayPalSucessURL(), pRequest.getContextPath() + getPayPalErrorURL(), pRequest, pResponse);
	}
    
	/**
	 * This dummy method was written for /tbs purpose, so don't delete this.
	 * @param pRequest
	 * @param pResponse
	 */
    public void postCheckoutWithPaypal(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
		//do nothing
	}

	/**
     * R2.2 Paypal Checkout
     * This method is called when user wants to pay with pay pal and clicks on paypal button
     * @param pRequest
     * @param pResponse
     * @return boolean
     * @throws ServletException
     * @throws IOException
     * 
     */
	public final boolean handleCheckoutSPWithPaypal(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.logDebug("BBBCartFormHandler ::handleCheckoutWithPaypal method -  start ");
		final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
		final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		this.errorMap = new HashMap<String, String>();
		if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.PAYPAL_CHECKOUT, myHandleMethod);
			
			//Start: 258 - Verified by visa - refresh back button story - resetting the page state
	        pRequest.getSession().removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS);
	        //End: 258 - Verified by visa - refresh back button story - resetting the page state
			
			Transaction tr = null;
			try {
				tr = this.ensureTransaction();
				if (this.getUserLocale() == null) {
					this.setUserLocale(this.getUserLocale(pRequest, pResponse));
				}

				synchronized (this.getOrder()) {
					BBBOrder order = (BBBOrder) getOrder();
					// LTL-1548 | Remove Empty shipping group before PayPal is hit 
					final BBBShippingGroupManager shpGrpManager = (BBBShippingGroupManager) this.getShippingGroupManager();
                    try {
                        shpGrpManager.removeEmptyShippingGroups(this.getOrder());
                        this.getOrderManager().updateOrder(this.getOrder());
                    } catch (final CommerceException e) {
                        this.logError(LogMessageFormatter.formatMessage(pRequest, EXCEPTION_REMOVING_EMPTY_SHIPPING_GROUPS,
                                        BBBCoreErrorConstants.CART_ERROR_1020), e);
                    }
                    // LTL-1548 | Remove Empty shipping group | Ends
					// Validate shipping method on returning to cart
					// Set Checkout state to cart if clicked from cart page
					if (isFromCart()) {
						this.logDebug("CartFormHandler.handleCheckoutWithPaypal() :  Request is from Cart Page");
						this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
						if(this.getInventoryManager().checkUncachedInventory(order)){
				   			String successURL = pRequest.getContextPath() + this.getCheckoutState().getFailureURL();
							return this.checkFormRedirect(successURL, null, pRequest, pResponse);
						}
					}
					// this.preCheckoutWithPaypal(pRequest, pResponse);
					BBBSetExpressCheckoutResVO resType = null;
					String sucessURL = BBBCoreConstants.BLANK;
					String token = null;
					try {
						if (!order.isBopusOrder() && validateOrderForPPCall(pRequest.getContextPath())) {
							
							this.logDebug("BBBCartFormHandler.handleCheckoutWithPaypal() :  Order is validated for Paypal and calling setExpCheckout webservice");
							String hostUrl = pRequest.getScheme() + BBBCoreConstants.CONSTANT_SLASH + pRequest.getHeader(BBBCoreConstants.HOST) + pRequest.getContextPath();
							if (!StringUtils.isEmpty(getMobileCancelURL()) && !StringUtils.isEmpty(getMobileRedirectURL())) {
								resType = getPaypalServiceManager().doSetExpressCheckOut(order, getMobileCancelURL(), getMobileRedirectURL(), getPayPalSessionBean(), getUserProfile());
							}else{
								String cancelUrl = "";
								String returnUrl = "";
								if(isFromCart()){
									cancelUrl = hostUrl + getCheckoutState().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
									returnUrl = hostUrl + getCheckoutState().getCheckoutSuccessURLs().get(CheckoutProgressStates.DEFAULT_STATES.VALIDATION.toString());
								}
								else {
									getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString());
									cancelUrl = hostUrl + getCheckoutState().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.SP_PAYPAL.toString());
									returnUrl = hostUrl + getCheckoutState().getCheckoutSuccessURLs().get(CheckoutProgressStates.DEFAULT_STATES.SP_PAYPAL.toString());
								}
								resType = getPaypalServiceManager().doSetExpressCheckOut(order, cancelUrl, returnUrl, getPayPalSessionBean(), getUserProfile());
							}	
							
							if (resType != null && !StringUtils.isEmpty(resType.getToken())) {

								token = resType.getToken();
								this.logDebug("BBBCartFormHandler.handleCheckoutWithPaypal() :  token " + token);
								// *********this paypalToken is sent to MOBILE
								// in response******************
								setPaypalToken(token);
								
							} else if (resType != null && resType.getErrorStatus().isErrorExists()) {
								logError(" Error Msg Recieved while calling paypal service= " + resType.getErrorStatus().getErrorMessage());
								if(resType.getErrorStatus().getErrorId() == BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID){
									addFormException(new DropletException(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null),BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR));
									this.errorMap.put(String.valueOf(resType.getErrorStatus().getErrorId()), this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null));
									logError("Error ID: " + resType.getErrorStatus().getErrorId() + " Error Message: " + this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null));
									//Added as part of R2.2.1 Story - Redirects to shipping page when paypal rejects the shipping address - Start
									this.markTransactionRollback();
									this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_CHECKOUT_SINGLE.toString());
									if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL()) && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)){
										this.payPalErrorURL = pRequest.getContextPath() + this.getCheckoutState().getCheckoutFailureURLs().get(BBBPayPalConstants.SP_PAYPAL);
									}
									List<String> errorList = new ArrayList<String>();
									errorList.add(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null));
									this.getPayPalSessionBean().setErrorList(errorList);
									return checkFormRedirect(null, this.payPalErrorURL, pRequest, pResponse);
									//Added as part of R2.2.1 Story - Redirects to shipping page when paypal rejects the shipping address - End
								}
								else{
									addFormException(new DropletException(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null),BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
									this.errorMap.put(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
									logError("Error ID: " + resType.getErrorStatus().getErrorId() + " Error Message: " + this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
								}
							}
							
							if (!StringUtils.isEmpty(this.getCheckoutState().getFailureURL()) && !this.getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
								sucessURL = (getPaypalServiceManager().getPayPalRedirectURL()) + token;
								setPayPalSucessURL(sucessURL);
								
							} else if (StringUtils.isEmpty(getMobileCancelURL())) {
								sucessURL = (getPaypalServiceManager().getPayPalRedirectURL()) + token;
								setPayPalSucessURL(sucessURL);
								setMoveToPurchaseInfoSuccessURL(sucessURL);
							}
							this.logDebug("BBBCartFormHandler.handleCheckoutSPWithPaypal() : payPalErrorURL: " + this.payPalErrorURL + " SucessURL: " + sucessURL);
						}
					} catch (BBBSystemException systemException) {
						this.markTransactionRollback();
						this.logError("BBBSystemException occured in BBBCartFormHandler.handleCheckoutSPWithPaypal :", systemException);
						addFormException(new DropletException(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null),BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
						this.errorMap.put(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
						logError("Error Message: " + this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
					} catch (BBBBusinessException businessException) {
						this.markTransactionRollback();
						this.logError("BBBBusinessException occured in BBBCartFormHandler.handleCheckoutSPWithPaypal :", businessException);
						addFormException(new DropletException(this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null), BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE));
						this.errorMap.put(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
						logError("Error Message: " + this.getMsgHandler().getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null));
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
		this.logDebug("BBBCartFormHandler ::handleCheckoutWithPaypal method - mthod ends ");
		return checkFormRedirect(getPayPalSucessURL(), pRequest.getContextPath() + getPayPalErrorURL(), pRequest, pResponse);
	}
    
    /**
     * This method is prehandled part for handleCheckoutWithPaypal
     * @param pRequest
     * @param pResponse
     */
    /*public void preCheckoutWithPaypal(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse){
    	
    	this.logDebug("BBBCartFormHandler ::preCheckoutWithPaypal method - method start ");
    	BBBOrder order = (BBBOrder) getOrder();
    	BBBPayPalSessionBean payPalSessionBean = getPayPalSessionBean();
    	if(payPalSessionBean !=null && payPalSessionBean.getGetExpCheckoutResponse() !=null){
    		getPaypalServiceManager().validateShippingMethod(order);
    	}
		this.logDebug("BBBCartFormHandler ::preCheckoutWithPaypal method - method ends ");
    	 	
    }*/
	// R 2.2 PayPal implementation
	/**
	 * Wrapper method for Rest for handlecheckoutwithPaypal
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final boolean handleCheckoutWithPaypalRest(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
			// Remove all GC's if user change payment method to Paypal after order is fully covered by GC's BBBP-2865
		if (BBBCoreConstants.MOBILEWEB
				.equalsIgnoreCase(BBBUtility.getChannel())
				|| BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility
						.getChannel())) {

			final double gcTotalAmount = ((BBBPaymentGroupManager) this
					.getPaymentGroupManager()).getGiftCardTotalAmount(this
					.getOrder());

			if (gcTotalAmount >= this.getOrder().getPriceInfo().getTotal()) {
				// Order is fully covered by GC's so removing all GC's to proceed with PayPal
				removeGiftCardsFromOrder();
			}
		}
		 final Map<String, String> failureUrl = this.getCheckoutState().getCheckoutFailureURLs();
		 final Map<String, String> successUrl = this.getCheckoutState().getCheckoutSuccessURLs();
		 final HashMap map = setRedirectUrlForRest();
		 this.getCheckoutState().setCheckoutFailureURLs(map);
		 this.getCheckoutState().setCheckoutSuccessURLs(map);
		 //Defect 25370 -Start Remove Token if Paypal token does not exit
		 if( !((BBBOrderImpl) this.getOrder()).isPayPalOrder() && !(StringUtils.isEmpty(((BBBOrderImpl) this.getOrder()).getToken()) )) {
			getPaypalServiceManager().removePayPalPaymentGroup(((BBBOrderImpl) this.getOrder()), this.getUserProfile());
		 }
		//Defect 25370 -END
		 final boolean status = this.handleCheckoutWithPaypal(pRequest,pResponse);
		 this.getCheckoutState().setCheckoutFailureURLs(failureUrl);
		 this.getCheckoutState().setCheckoutSuccessURLs(successUrl);

		 return status;
	}

	/**
	 * Method to remove all GC from order
	 * It iterates over all payment groups in order and 
	 * check if it is of GiftCard type.
	 */
	private void removeGiftCardsFromOrder() {
		@SuppressWarnings ("unchecked")
		 final List<PaymentGroup> paymentGroups = this.getOrder().getPaymentGroups();
		 final List<String> removeGCPaymentGrpIdList = new ArrayList<String>();
		 for (final PaymentGroup paymentGroup : paymentGroups) {

		     this.logDebug(PAYMENT_GROUP_GET_ID + paymentGroup.getId());
		     this.logDebug(PAYMENT_GROUP_GET_AMOUNT + paymentGroup.getAmount());

		     if (paymentGroup instanceof BBBGiftCard) {
		         removeGCPaymentGrpIdList.add(paymentGroup.getId());
		     }
		 }

		 if (removeGCPaymentGrpIdList.size() > 0) {
		     for (final String payGrpID : removeGCPaymentGrpIdList) {
		         try {
					this.getPaymentGroupManager().removePaymentGroupFromOrder(this.getOrder(), payGrpID);
				} catch (CommerceException commerceException) {
					this.logError("Error Occured while process the request: ", commerceException);
		            this.markTransactionRollback();
				}
		     }
		 }
	}

	/**
	 * Validate the token from paypal and Shipping Group is empty
	 * @return boolean
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private boolean validateOrderForPPCall(String pContextPath) throws BBBSystemException, BBBBusinessException {
		BBBOrderImpl order = (BBBOrderImpl) getOrder();
		if (getPaypalServiceManager().isTokenExpired(this.getPayPalSessionBean(), order)) {
			this.logDebug("BBBCartFormHandler.validateOrderForPPCall() | Order is not paypal or token expired");
			return true;
		}else if(!order.isPayPalOrder()){
			getPayPalSessionBean().setGetExpCheckoutResponse(null);
			getPayPalSessionBean().setValidateOrderAddress(false);
		}
		if (isFromCart()) {
			if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel())
	                || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){				
				setPayPalTokenNotExpired(true);
				this.logDebug("BBBCartFormHandler.validateOrderForPPCall() :  Request is from mobile, token expired: " + !this.isPayPalTokenNotExpired());
			}
			else{
				setPayPalSucessURL(pContextPath + getCheckoutState().getCheckoutSuccessURLs().get(BBBCoreConstants.VALIDATION));
			}
			this.logDebug("BBBCartFormHandler.validateOrderForPPCall() :  Request is from cart, success url: " + this.getPayPalSucessURL());
			this.getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
			getPayPalSessionBean().setValidateOrderAddress(true);
		} else {
			if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel())
	                || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){				
				setPayPalTokenNotExpired(true);
				this.logDebug("BBBCartFormHandler.validateOrderForPPCall() :  Request is from mobile, token expired: " + !this.isPayPalTokenNotExpired());
			}
			else{
				getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
				
				boolean isSinglePageCheckout	=	getCheckoutState().spcEligible((BBBOrderImpl)getOrder(),isSinglePageCheckout());
				if(isSinglePageCheckout){
					setPayPalSucessURL(pContextPath + getCheckoutState().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString()) + "?token=" + order.getToken());	
				}else{
				setPayPalSucessURL(pContextPath + getCheckoutState().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString()) + "?token=" + order.getToken());			
				}
			}
			this.logDebug("BBBCartFormHandler.validateOrderForPPCall() :  Request is from payment page, success url: " + this.getPayPalSucessURL());
		}
		return false;
	}
	/** Handle method to edit Personalized item.
    *
    * @param pRequest
    * @param pResponse
    * @return
    * @throws ServletException
    * @throws IOException */
   public final boolean handleEditPersonalisedItem(final DynamoHttpServletRequest pRequest,
                   final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	   this.logDebug("BBBCartFormHandler ::handleEditPersonalisedItem method -  start ");
	
	   // If not coming from desktop site, remove redirect urls.
	   if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel())
               || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){	
		   this.setEditItemInOrderFormsSuccessURL("");
		   this.setEditItemInOrderFormsErrorURL("");
	   }
	   
	   synchronized (this.getOrder()) {
		   Map pExtraParameters = null;
		   final List<CommerceItem> comItemObjList = this.getOrder().getCommerceItems();
		   if (comItemObjList != null) {
			   for (final CommerceItem comItemObj : comItemObjList) {
                   if (comItemObj instanceof BBBCommerceItem) {
                       final BBBCommerceItem bbbObject = (BBBCommerceItem) comItemObj;
                       if (bbbObject.getId().equalsIgnoreCase(getCommerceItemId())) {
                    	   try {
//                    		   pExtraParameters = new HashMap();
//                    		   pExtraParameters.put(BBBCoreConstants.EXIM_PRICING_REQUIRED, "true");
                    		   if(bbbObject.getReferenceNumber().equalsIgnoreCase(this.getReferenceNumber())) {
                    		   bbbObject.setEximPricingReq(true);
                    	       runProcessRepriceOrder(getModifyOrderPricingOp(), this.getOrder(), this.getUserPricingModels(),
                                                  this.getUserLocale(), this.getProfile(),pExtraParameters);
                    	       this.updateOrder(this.getOrder(), ERROR_UPDATING_ORDER, pRequest, pResponse);
                    		   } else {
                    			   this.errorMap = new HashMap<String, String>();
                    			   logDebug("Wish list item is invalid or not available in the repository");
               					   addFormException(new DropletException(BBBCoreConstants.ERR_EDIT_INVALID_REF_NUM_MSG,BBBCoreConstants.ERR_EDIT_INVALID_REF_NUM));
               					   this.errorMap.put(BBBCoreConstants.ERR_EDIT_INVALID_REF_NUM, BBBCoreConstants.ERR_EDIT_INVALID_REF_NUM_MSG);  
                    		   }
                    	} catch (RunProcessException e) {
							// TODO Auto-generated catch block
                    		bbbObject.setEximErrorExists(true);
                    		 this.updateOrder(this.getOrder(), ERROR_UPDATING_ORDER, pRequest, pResponse);
							logError(LogMessageFormatter.formatMessage(null, "RunProce ss Exception from handleEditPersonalisedItem of BBBCartFormHandler :"), e);
						}
                       }
                       
                   }
			   }
		   }
	   }

	   
	   
	   return this.checkFormRedirect(this.getEditItemInOrderFormsSuccessURL(), this.getEditItemInOrderFormsErrorURL(), pRequest,
               pResponse);

   }
   
   /**
	 * This method is used to set the free shipping banner details in model.If order total is less than the threshold value then 
	 * use the existing message. Else subtract the order total from higher threshold value and display the dynamic message at front end
	 * 
	 * @param DynamoHttpServletRequest request
	 * @return
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	 private void setFreeShippingBannerDetails(final DynamoHttpServletRequest request) throws BBBSystemException, BBBBusinessException {
		 if (((BBBSessionBean) request.
					resolveName(BBBGiftRegistryConstants.SESSION_BEAN)).isInternationalShippingContext()) {
			 this.setChangeClosenessQualifierMsg(BBBCoreConstants.FALSE);
			 return;
		 }
		 	boolean isStaticBanner = false;
		 	boolean isFreeShippingPromo = false;
			double shippingDifference = 0.0;
			double higherShipThreshhold = 0.0;
			this.setChangeClosenessQualifierMsg(BBBCoreConstants.FALSE);
			BBBClosenessQualifierDropletResultVO closenessQualifier =  getClosenessQualifierService().
					getClosenessQualifiers(BBBCoreConstants.SHIPPING, FROM_MOBILE_PDP);
			String higherShippingThreshhold = getMsgHandler().getPageLabel(
					BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, request.getLocale().getLanguage(), null);
			if(null != higherShippingThreshhold){
				String trimedHigherShippingThreshold = higherShippingThreshhold.replaceAll("[^0-9^.]", BBBCoreConstants.BLANK).trim();
				if(!trimedHigherShippingThreshold.equalsIgnoreCase(BBBCoreConstants.BLANK)){		
					higherShipThreshhold = Double.parseDouble(higherShippingThreshhold);
				} 
			}
			
			if( null != closenessQualifier && !BBBUtility.isEmpty(closenessQualifier.getName())){
				isFreeShippingPromo = true;
				isStaticBanner = true;
				String lowerShippingThreshhold = getMsgHandler().getPageLabel(
						BBBCoreConstants.LBL_LOWER_FREE_SHIPPING_THRESHOLD, request.getLocale().getLanguage(), null);
				if(null != lowerShippingThreshhold){
					String trimedLowerShippingThreshold = lowerShippingThreshhold.replaceAll("[^0-9^.]", BBBCoreConstants.BLANK).trim();
					if(!trimedLowerShippingThreshold.equalsIgnoreCase(BBBCoreConstants.BLANK)){		
						double lowerShipThreshhold = Double.parseDouble(lowerShippingThreshhold);
						double cartAmt = closenessQualifier.getOnlinePurchaseTotal();
						if(cartAmt >= lowerShipThreshhold){
							if(higherShipThreshhold > cartAmt){
								isStaticBanner = false;
								shippingDifference = higherShipThreshhold - cartAmt;
								shippingDifference = Math.round(shippingDifference * 100) / 100.00;
							} 
						} 
					}
				}
			} else if (null != closenessQualifier && higherShipThreshhold > 0.0
					&& closenessQualifier.getOnlinePurchaseTotal() >= higherShipThreshhold) {
					String congrats_free_ship_msg = getMsgHandler().getPageTextArea(
						BBBCoreConstants.TXT_CONGRATS_FREE_SHIP_MSG, request.getLocale().getLanguage(), null);
					this.setChangeClosenessQualifierMsg(BBBCoreConstants.TRUE);
					this.setClosenessQualifierName(congrats_free_ship_msg);
			}
			
			if (isFreeShippingPromo) {
				this.setChangeClosenessQualifierMsg(BBBCoreConstants.TRUE);
				if (isStaticBanner) {
					this.setClosenessQualifierName(closenessQualifier.getName());
				} else {
					StringBuilder qualifierMessage = new StringBuilder();
					
					String txt_free_shipping_you_mobile = getMsgHandler().getPageTextArea(
							BBBCoreConstants.TXT_FREE_SHIPPING_YOU_MOBILE, request.getLocale().getLanguage(), null);
					String txt_free_shipping_away_mobile = getMsgHandler().getPageTextArea(
							BBBCoreConstants.TXT_FREE_SHIPPING_AWAY_MOBILE, request.getLocale().getLanguage(), null);
					qualifierMessage.append(txt_free_shipping_you_mobile);
					qualifierMessage.append(Double.toString(shippingDifference));
					qualifierMessage.append(txt_free_shipping_away_mobile);
					this.setClosenessQualifierName(qualifierMessage.toString());
				}
			}
		}
	 
	 /** Check number of items in user cart and throw exception in case of recognized user exceeds the limit
	 * @param pRequest
	 * @throws BBBBusinessException
	 */
	protected void checkCartItemCount(DynamoHttpServletRequest pRequest) throws BBBBusinessException {
		 logDebug("Enter inside BBBCartFormhandler.checkCartItemCount");
		 List<? extends CommerceItem> bbbCommerceItems = ((BBBOrderManager) getOrderManager()).getCommerceItems(BBBCommerceItem.class, getOrder());
		 AddCommerceItemInfo[] itemInfos = getItems();
		 AddCommerceItemInfo itemInfo = null;
		 boolean isItemExistInOrder = false;

		 // assumption :: we receive only one item from front end, in case of multi add to cart another for need be used with below logic
		 if (itemInfos != null && itemInfos.length > 0) {
			 itemInfo = itemInfos[0];
		 } else {
			 return;
		 }
		 for (CommerceItem item : bbbCommerceItems) {
			 BBBCommerceItem commerceItem = (BBBCommerceItem) item;
			 final String storeId = commerceItem.getStoreId();
			 String referceNumber = commerceItem.getReferenceNumber();
			 if (commerceItem.getCatalogRefId().equals(itemInfo.getCatalogRefId())) {
				 if (StringUtils.isNotBlank(storeId) || StringUtils.isNotBlank((String) itemInfo.getValue().get(BBBCoreConstants.STOREID))) {
                     if (StringUtils.isNotBlank(storeId) && storeId.equals(itemInfo.getValue().get(BBBCoreConstants.STOREID))) {
                            isItemExistInOrder = true;
                            break;
                     }
				 } else if (StringUtils.isBlank(referceNumber)) {
                          isItemExistInOrder = true;
                          break;
                   }
             }
		 }
		 if (!isItemExistInOrder && bbbCommerceItems.size() >= getMaxItemCountCart()) {
			 this.errorMap = new HashMap<String, String>();
			 this.errorMap.put(BBBCoreConstants.ERROR_CART_MAX_REACHED, BBBCoreConstants.ERROR_CART_MAX_REACHED);
			 throw new BBBBusinessException(getLblTxtTemplateManager().getErrMsg(BBBCoreConstants.ERROR_CART_MAX_REACHED, pRequest.getLocale().getLanguage(), null, null));
		 }

		 logDebug("Exting from BBBCartFormhandler.checkCartItemCount bbbCommerceItems::" + bbbCommerceItems);

	 }
	
	/**
	 * This method prepares the host string.
	 * 
	 * @param pRequest instance of DynamoHttpServletRequest
	 *//*
	private String getHost(DynamoHttpServletRequest pRequest) {

		
		logDebug("[Start]: getHost()");
		
		String hostStr = null;

		if (pRequest != null) {
			String url = pRequest.getRequestURL().toString();
			String contextPath = pRequest.getContextPath();

			hostStr = url.split(contextPath)[0].concat(contextPath);

			
			logDebug("url: " + url);
			logDebug("contextPath: " + contextPath);
			logDebug("hostpath after context path split: " + hostStr);
			
		} else {
			
				logWarning("Request object is null");
			
		}
		
			logDebug("[End]: getHost()");
		
		return hostStr;
	}*/
	
	 /**
	  * @return max item count for cart
	  */
	 private int getMaxItemCountCart() {
		 int cartMaxItemCount = 0;
		 try {
			 cartMaxItemCount = Integer.valueOf(getCatalogUtil().getAllValuesForKey(BBBCheckoutConstants.WSDL_GIFT_REGISRTY, BBBCheckoutConstants.CART_MAX_LIMIT).get(0));
		 } catch (BBBSystemException | BBBBusinessException e) {
			 logError("Error finding value for key CART_MAX_LIMIT",e);
		 }
		 return cartMaxItemCount;
	 }

	
	/**
	 * @return removeItemFromOrderFormsSuccessURL
	 */
	public String getRemoveItemFromOrderFormsSuccessURL() {
		return this.removeItemFromOrderFormsSuccessURL;
	}

	/**
	 * @param removeItemFromOrderFormsSuccessURL
	 */
	public void setRemoveItemFromOrderFormsSuccessURL(
			final String removeItemFromOrderFormsSuccessURL) {
		this.removeItemFromOrderFormsSuccessURL = removeItemFromOrderFormsSuccessURL;
	}

	/**
	 * @return removeItemFromOrderFormsErrorURL
	 */
	public String getRemoveItemFromOrderFormsErrorURL() {
		return this.removeItemFromOrderFormsErrorURL;
	}

	/**
	 * @param removeItemFromOrderFormsErrorURL
	 */
	public void setRemoveItemFromOrderFormsErrorURL(
			final String removeItemFromOrderFormsErrorURL) {
		this.removeItemFromOrderFormsErrorURL = removeItemFromOrderFormsErrorURL;
	}

	/**
	 * @return addItemToOrderFormsSuccessURL
	 */
	public String getAddItemToOrderFormsSuccessURL() {
		return this.addItemToOrderFormsSuccessURL;
	}

	/**
	 * @param addItemToOrderFormsSuccessURL
	 */
	public void setAddItemToOrderFormsSuccessURL(
			final String addItemToOrderFormsSuccessURL) {
		this.addItemToOrderFormsSuccessURL = addItemToOrderFormsSuccessURL;
	}

	/**
	 * @return addItemToOrderFormsErrorURL
	 */
	public String getAddItemToOrderFormsErrorURL() {
		return this.addItemToOrderFormsErrorURL;
	}

	/**
	 * @param addItemToOrderFormsErrorURL
	 */
	public void setAddItemToOrderFormsErrorURL(final String addItemToOrderFormsErrorURL) {
		this.addItemToOrderFormsErrorURL = addItemToOrderFormsErrorURL;
	}

	/**
	 * @return ajaxCartOrderSuccessURL
	 */
	public String getAjaxCartOrderSuccessURL() {
		return this.ajaxCartOrderSuccessURL;
	}

	/**
	 * @param ajaxCartOrderSuccessURL
	 */
	public void setAjaxCartOrderSuccessURL(String ajaxCartOrderSuccessURL) {
		this.ajaxCartOrderSuccessURL = ajaxCartOrderSuccessURL;
	}

	/**
	 * @return ajaxCartOrderErrorURL
	 */
	public String getAjaxCartOrderErrorURL() {
		return this.ajaxCartOrderErrorURL;
	}

	/**
	 * @param ajaxCartOrderErrorURL
	 */
	public void setAjaxCartOrderErrorURL(String ajaxCartOrderErrorURL) {
		this.ajaxCartOrderErrorURL = ajaxCartOrderErrorURL;
	}


    public String getAssociateRegistryContextSuccessURL() {
		return associateRegistryContextSuccessURL;
	}

	public void setAssociateRegistryContextSuccessURL(
			String associateRegistryContextSuccessURL) {
		this.associateRegistryContextSuccessURL = associateRegistryContextSuccessURL;
	}

	public String getAssociateRegistryContextErrorURL() {
		return associateRegistryContextErrorURL;
	}

	public void setAssociateRegistryContextErrorURL(
			String associateRegistryContextErrorURL) {
		this.associateRegistryContextErrorURL = associateRegistryContextErrorURL;
	}
	
	public BBBPaymentGroupDroplet getPaymentGroupDroplet() {
		return paymentGroupDroplet;
	}

	public void setPaymentGroupDroplet(BBBPaymentGroupDroplet paymentGroupDroplet) {
		this.paymentGroupDroplet = paymentGroupDroplet;
	}
	
	public String getBuyOffAssociationSkuId() {
		return buyOffAssociationSkuId;
	}

	public void setBuyOffAssociationSkuId(String buyOffAssociationSkuId) {
		this.buyOffAssociationSkuId = buyOffAssociationSkuId;
	}

	public boolean isBuyOffFlag() {
		return buyOffFlag;
	}

	public void setBuyOffFlag(boolean buyOffFlag) {
		this.buyOffFlag = buyOffFlag;
	}

	public String getErrorFlagBuyOffAssociation() {
		return errorFlagBuyOffAssociation;
	}

	public void setErrorFlagBuyOffAssociation(String errorFlagBuyOffAssociation) {
		this.errorFlagBuyOffAssociation = errorFlagBuyOffAssociation;
	}

	public boolean isBuyOffDuplicateItemFlag() {
		return buyOffDuplicateItemFlag;
	}

	public void setBuyOffDuplicateItemFlag(boolean buyOffDuplicateItemFlag) {
		this.buyOffDuplicateItemFlag = buyOffDuplicateItemFlag;
	}
	
	/**
	 * @return the restAPIManager
	 */
	public RestAPIManager getRestAPIManager() {
		return restAPIManager;
	}

	/**
	 * @param restAPIManager the restAPIManager to set
	 */
	public void setRestAPIManager(RestAPIManager restAPIManager) {
		this.restAPIManager = restAPIManager;
	}
	
	private void logOrderDetails(String logMsg) {
		try { 
			if(this.getCommonConfiguration().isLogDebugEnableOnCartFormHandlerForOrderDetail()){    
				OrderImpl order = (BBBOrderImpl) getOrder();
				 
				 RepositoryItem orderItem=  getOrderManager().getOrderTools().getOrderRepository().getItem(order.getId(), "order") ;
		         int repVersion= (Integer) orderItem.getPropertyValue("version"); 
		         super.logDebug(logMsg+":[" + order.toString() + " ,orderVersion:" + order.getVersion()+ ",repOrderVersion:"+repVersion+"]" + " , CommerceItemsCount:["+ order.getCommerceItemCount() + "], OrderHexString:["+order.getClass().getName() + "@" + Integer.toHexString(hashCode())+"]");
	            List<CommerceItem> commerceItems =	order.getCommerceItems();	
			     if(order.getCommerceItemCount() > 0){
				    StringBuffer commerceItemslogs = new StringBuffer("CommerceItems:{");
				    for(CommerceItem commerceItem :  commerceItems){
					   commerceItemslogs.append("commerceItemId [" + commerceItem.getId()+",catalogRefId:"+commerceItem.getCatalogRefId()+ ",Qty:"+commerceItem.getQuantity()+"] ");
					   }
			          commerceItemslogs.append("}");
			          super.logDebug(logMsg +": "+commerceItemslogs.toString());
			       } else{
			    	   super.logDebug(logMsg +": No CommerceItems:{}"); 
			       }
			}
	     } catch (Exception e) {
	    	 logError("BBBCartFormHandler:logOrderDetails", e);
		}
	}
	
	public String getClosenessQualifierName() {
		return this.closenessQualifierName;
	}

	public void setClosenessQualifierName(String closenessQualifierName) {
		this.closenessQualifierName = closenessQualifierName;
	}

	
	public String getChangeClosenessQualifierMsg() {
		return this.changeClosenessQualifierMsg;
	}

	public void setChangeClosenessQualifierMsg(String changeClosenessQualifierMsg) {
		this.changeClosenessQualifierMsg = changeClosenessQualifierMsg;
	}

	public BBBClosenessQualifierService getClosenessQualifierService() {
		return this.closenessQualifierService;
	}

	public void setClosenessQualifierService(
			BBBClosenessQualifierService closenessQualifierService) {
		this.closenessQualifierService = closenessQualifierService;
	}
	public BopusInventoryService getBopusInventoryService() {
		return this.bopusInventoryService;
	}

	public void setBopusInventoryService(
			BopusInventoryService bopusInventoryService) {
		this.bopusInventoryService = bopusInventoryService;
	}
	
	/**
	 * @return mStoreRepository
	 */
	public GSARepository getLocalStoreRepository() {
		return this.mLocalStoreRepository;
	}

	/**
	 * @param pStoreRepository
	 */
	public void setLocalStoreRepository(GSARepository pLocalStoreRepository) {
		this.mLocalStoreRepository = pLocalStoreRepository;
	}
/**
	 * @return the porchPayLoadJson
	 */
	public JSONObject getPorchPayLoadJson() {
		
		if(null==this.getJsonResultString()){
			return null;
		}
		 	JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(this.getJsonResultString());
			final DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);			
			 final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean.get(ADD_ITEM_RESULTS);
			 if(null== itemArray || itemArray.isEmpty()){
				 return null;
			 }
			 String  itemPorchJson=null;
			 	try{
				   itemPorchJson=(String) itemArray.get(0).get("porchPayLoadJson");				   
			 	}
			 	catch(MorphException e){
			 		itemPorchJson=null;
			 	}
				if(atg.core.util.StringUtils.isBlank(itemPorchJson)){
					return null;
				}
				JSONObject porchJsonObject = (JSONObject) JSONSerializer.toJSON(itemPorchJson); 
				String porchZipCode = (String) porchJsonObject.get("postalCode");
				if(!atg.core.util.StringUtils.isBlank(porchZipCode)){
					getSessionBean().setPorchZipCode(porchZipCode);
				}
				return porchJsonObject;
			 
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
	 * @return the cartPayLoadResultString
	 */
	public String getCartPayLoadResultString() {
		return cartPayLoadResultString;
	}
	/**
	 * @param cartPayLoadResultString the cartPayLoadResultString to set
	 */
	public void setCartPayLoadResultString(String cartPayLoadResultString) {
		this.cartPayLoadResultString = cartPayLoadResultString;
	}
	
	public String getJsonCouponErrors() {
		return jsonCouponErrors;
	}

	public void setJsonCouponErrors(String jsonCouponErrors) {
		this.jsonCouponErrors = jsonCouponErrors;
	}
}