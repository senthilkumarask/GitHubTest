package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import com.bbb.account.BBBGetCouponsManager;
import com.bbb.account.vo.CouponListVo;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.TBSBopusInventoryService;
import com.bbb.commerce.inventory.TBSInventoryManagerImpl;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.PayAtRegister;
import com.bbb.commerce.order.TBSOrder;
import com.bbb.commerce.order.TBSOrderManager;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.common.TBSSessionComponent;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.idm.TBSIDMFormHandler;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.ChangeAwareSet;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

/**
 * The Class TBSCartFormHandler.
 *
 * @author 
 */
public class TBSCartFormHandler extends BBBCartFormhandler {

	private static final String GENERIC_ERROR_TRY_LATER = "ERR_CART_GENERIC_ERROR_TRY_LATER";
	private static final String REASON_LIST = "reasonList";
	private static final String NEW_PRICE = "newPrice";
	private static final String REASON_CODE_EMPTY = "Reason Code cannot be empty";
	private static final String OVERRIDE_PRICE_EMPTY = "Override Price cannot be empty";
	private static final String OVERRIDE_PRICE_HIGH = "Override Price is higher than the list or sale price";
	private static final String OVERRIDE_PRICE_LOW = "Override Price cannot be zero";
	private static final String REGISTRY_ID = "registryId";
	private static final String STORE_ID = "storeId";
	private static final String INVALID_QUANTITY_FORMAT = "err_invalidQuantity_format";
	private static final String INVALID_STORE_ID = "err_cart_invalidStoreId";
	private static final String NULL_STORE_ID = "err_cart_nullStoreId";
	private static final String ERROR_INVENTORY_CHECK = "err_cart_inventoryCheckFail";
	private static final String OUT_OF_STOCK = "err_cart_outOfStock";
	private static final String OUT_OF_STOCK_ITEM = "err_cart_outOfStockItem";
	private static final String NULL_SKU_ID = "err_cart_nullSkuId";
	
	/**
     * mStoreRepository property to hold StoreRepository
     */
    private Repository mStoreRepository;
	private TBSSearchStoreManager mSearchStoreManager;
	
	private boolean multiship = false;
	
	String mStorePromotions[] = new String[10];
	/**
	 * mUserPricingModels to hold PricingModelHolder
	 */
	private PricingModelHolder mUserPricingModels;
	/**
	 * mSucessURL to hold Sucess and failure URL
	 */
	private String mSucessURL;
	private String mSuccessURL;
	/**
	 * Gets the success url.
	 *
	 * @return the successURL
	 */
	public String getSuccessURL() {
		return this.mSuccessURL;
	}

	/**
	 * Sets the success url.
	 *
	 * @param pSuccessURL
	 *            the successURL to set
	 */
	public void setSuccessURL(final String pSuccessURL) {
		this.mSuccessURL = pSuccessURL;
	}
	private String mMobileNumber;
	private String mEmailAddress;
	private String mEmailexists;
	private  BBBGetCouponsManager mCouponsManager;
	private String couponSuccessUrl;
	private String couponErrorUrl;
	private String mErrorURL;
	private BBBCatalogTools catalogTools;
	private String successQueryParam;
    public String getSuccessQueryParam() {
		return successQueryParam;
	}

	public void setSuccessQueryParam(String successQueryParam) {
		this.successQueryParam = successQueryParam;
	}
	private String errorQueryParam;
	List<CouponListVo> couponFinalList = null;
	public String getErrorURL() {
		return this.mErrorURL;
	}

	/**
	 * Sets the error url.
	 *
	 * @param pErrorURL
	 *            the errorURL to set
	 */
	public void setErrorURL(final String pErrorURL) {
		this.mErrorURL = pErrorURL;
	}
	private Map<String,String> successUrlMap;
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
	public String getFromPage() {
		return fromPage;
	}
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}



	private Map<String,String> errorUrlMap;
    private String fromPage;
	/**
	 * mShipTime property to hold shipment time.
	 */
	private String mShipTime;
	
	/**
	 * mIsAllItemsForRegistry property to isAllItemsForRegistry
	 */
	private boolean mAllItemsForRegistry;
	/**
	 * mItemToRegister property to hold ItemToRegister
	 */
	private String mItemToRegister;
	
	/**
	 * mUnassignRegistrySuccessUrl to hold UnassignRegistrySuccessUrl
	 */
	private String mUnassignRegistrySuccessUrl;
	
	/**
	 * mUnassignRegistryErrorUrl to hold UnassignRegistryErrorUrl
	 */
	private String mUnassignRegistryErrorUrl;
	
	/**
	 * mSplitAndAssignRegistrySuccessUrl
	 */
	private String mSplitAndAssignRegistrySuccessUrl;
	
	/**
	 * mSplitAndAssignRegistryErrorUrl
	 */
	private String mSplitAndAssignRegistryErrorUrl;
	
	/**
	 * mMoveNextSuccessUrl
	 */
	private String mMoveNextSuccessUrl;
	
	/**
	 * mMoveNextErrorUrl
	 */
	private String mMoveNextErrorUrl;
	
	/** mTbsSessionComponent property to hold the Tbs session component. */
	private TBSSessionComponent mTbsSessionComponent;
	
	
	/** The Create gs order success url. */
	private String mCreateGSOrderSuccessURL;
	
	/** The Create gs order error url. */
	private String mCreateGSOrderErrorURL;
	
	/** The Guided selling repository. */
	private Repository mGuidedSellingRepository;
	
	/** The Guided Selling order id. */
	private String mGsOrderId;
	
	private long mCollectionQty;

	/** The Associate username. */
	private String mAssoUsername;
	
	/** The Associate password. */
	private String mAssoPassword;
	
	/** The Idm form handler. */
	private TBSIDMFormHandler mIdmFormHandler;
	
	/** The Associate login success url. */
	private String mAssoLoginSuccessURL;
	
	/** The Associate login failure url. */
	private String mAssoLoginFailureURL;
	
	private String overrideSuccessURL="";
	private String overrideErrorURL="";
	private String reasonCode;
	private String competitor;
	private String overridePrice;
	private int overrideQuantity;
	private Site site;
	private BBBCatalogTools bbbCatalogTools;
	private String overrideId;
	private TBSBopusInventoryService mBopusService;
	private TBSInventoryManagerImpl mTbsInventoryManager;
	
	/**
	 * @return the tbsInventoryManager
	 */
	public TBSInventoryManagerImpl getTbsInventoryManager() {
		return mTbsInventoryManager;
	}
	/**
	 * @param pTbsInventoryManager the tbsInventoryManager to set
	 */
	public void setTbsInventoryManager(TBSInventoryManagerImpl pTbsInventoryManager) {
		mTbsInventoryManager = pTbsInventoryManager;
	}
	
	/**
	 * @return the bopusService
	 */
	public TBSBopusInventoryService getBopusService() {
		return mBopusService;
	}
	/**
	 * @param pBopusService the bopusService to set
	 */
	@SuppressWarnings("unused")
	public void setBopusService(TBSBopusInventoryService pBopusService) {
		mBopusService = pBopusService;
	}
	

	/**
	 * @return the overrideId
	 */
	public String getOverrideId() {
		return overrideId;
	}

	/**
	 * @param pOverrideId the overrideId to set
	 */
	public void setOverrideId(String pOverrideId) {
		overrideId = pOverrideId;
	}

	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	/**
	 * @param pSite the site to set
	 */
	public void setSite(Site pSite) {
		site = pSite;
	}

	/**
	 * @param pBbbCatalogTools the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(BBBCatalogTools pBbbCatalogTools) {
		bbbCatalogTools = pBbbCatalogTools;
	}

	/**
	 * @return the reasonCode
	 */
	public String getReasonCode() {
		return reasonCode;
	}

	/**
	 * @return the competitor
	 */
	public String getCompetitor() {
		return competitor;
	}

	/**
	 * @return the overridePrice
	 */
	public String getOverridePrice() {
		return overridePrice;
	}

	/**
	 * @return the overrideQuantity
	 */
	public int getOverrideQuantity() {
		return overrideQuantity;
	}

	/**
	 * @param pReasonCode the reasonCode to set
	 */
	public void setReasonCode(String pReasonCode) {
		reasonCode = pReasonCode;
	}

	/**
	 * @param pCompetitor the competitor to set
	 */
	public void setCompetitor(String pCompetitor) {
		competitor = pCompetitor;
	}

	/**
	 * @param pOverridePrice the overridePrice to set
	 */
	public void setOverridePrice(String pOverridePrice) {
		overridePrice = pOverridePrice;
	}

	/**
	 * @param pOverrideQuantity the overrideQuantity to set
	 */
	public void setOverrideQuantity(int pOverrideQuantity) {
		overrideQuantity = pOverrideQuantity;
	}

	/**
	 * @return the overrideSuccessURL
	 */
	public String getOverrideSuccessURL() {
		return overrideSuccessURL;
	}

	/**
	 * @return the overrideErrorURL
	 */
	public String getOverrideErrorURL() {
		return overrideErrorURL;
	}

	/**
	 * @param pOverrideSuccessURL the overrideSuccessURL to set
	 */
	public void setOverrideSuccessURL(String pOverrideSuccessURL) {
		overrideSuccessURL = pOverrideSuccessURL;
	}

	/**
	 * @param pOverrideErrorURL the overrideErrorURL to set
	 */
	public void setOverrideErrorURL(String pOverrideErrorURL) {
		overrideErrorURL = pOverrideErrorURL;
	}

	/**
	 * @return the couponFinalList
	 */
	public List<CouponListVo> getCouponFinalList() {
		return couponFinalList;
	}

	/**
	 * @param couponFinalList the couponFinalList to set
	 */
	public void setCouponFinalList(List<CouponListVo> couponFinalList) {
		this.couponFinalList = couponFinalList;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the couponSuccessUrl
	 */
	public String getCouponSuccessUrl() {
		return couponSuccessUrl;
	}

	/**
	 * @param couponSuccessUrl the couponSuccessUrl to set
	 */
	public void setCouponSuccessUrl(String couponSuccessUrl) {
		this.couponSuccessUrl = couponSuccessUrl;
	}

	/**
	 * @return the userPricingModels
	 */
	public PricingModelHolder getUserPricingModels() {
		return mUserPricingModels;
	}

	/**
	 * @param pUserPricingModels the userPricingModels to set
	 */
	public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
		mUserPricingModels = pUserPricingModels;
	}

	/**
	 * @return the sucessURL
	 */
	public String getSucessURL() {
		return mSucessURL;
	}

	/**
	 * @param pSucessURL the sucessURL to set
	 */
	public void setSucessURL(String pSucessURL) {
		mSucessURL = pSucessURL;
	}

	/**
	 * @return the storePromotions
	 */
	public String[] getStorePromotions() {
		return mStorePromotions;
	}

	/**
	 * @param pStorePromotions the storePromotions to set
	 */
	public void setStorePromotions(String[] pStorePromotions) {
		mStorePromotions = pStorePromotions;
	}
	
	/**
	 * @return the multiship
	 */
	public boolean isMultiship() {
		return multiship;
	}

	/**
	 * @param pMultiship the multiship to set
	 */
	public void setMultiship(boolean pMultiship) {
		multiship = pMultiship;
	}

	

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mMobileNumber;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return mEmailAddress;
	}

	/**
	 * @return the emailexists
	 */
	public String getEmailexists() {
		return mEmailexists;
	}

	/**
	 * @return the couponsManager
	 */
	public BBBGetCouponsManager getCouponsManager() {
		return mCouponsManager;
	}

	/**
	 * @param pMobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String pMobileNumber) {
		mMobileNumber = pMobileNumber;
	}

	/**
	 * @param pEmailAddress the emailAddress to set
	 */
	public void setEmailAddress(String pEmailAddress) {
		mEmailAddress = pEmailAddress;
	}

	/**
	 * @param pEmailexists the emailexists to set
	 */
	public void setEmailexists(String pEmailexists) {
		mEmailexists = pEmailexists;
	}

	/**
	 * @param pCouponsManager the couponsManager to set
	 */
	public void setCouponsManager(BBBGetCouponsManager pCouponsManager) {
		mCouponsManager = pCouponsManager;
	}

	/**
	 * @return the shipTime
	 */
	public String getShipTime() {
		return mShipTime;
	}

	/**
	 * @param pShipTime the shipTime to set
	 */
	public void setShipTime(String pShipTime) {
		mShipTime = pShipTime;
	}

	/**
	 * @return the allItemsForRegistry
	 */
	public boolean isAllItemsForRegistry() {
		return mAllItemsForRegistry;
	}

	/**
	 * @return the itemToRegister
	 */
	public String getItemToRegister() {
		return mItemToRegister;
	}

	/**
	 * @param pAllItemsForRegistry the allItemsForRegistry to set
	 */
	public void setAllItemsForRegistry(boolean pAllItemsForRegistry) {
		mAllItemsForRegistry = pAllItemsForRegistry;
	}

	/**
	 * @param pItemToRegister the itemToRegister to set
	 */
	public void setItemToRegister(String pItemToRegister) {
		mItemToRegister = pItemToRegister;
	}

	/**
	 * @return the unassignRegistrySuccessUrl
	 */
	public String getUnassignRegistrySuccessUrl() {
		return mUnassignRegistrySuccessUrl;
	}

	/**
	 * @return the unassignRegistryErrorUrl
	 */
	public String getUnassignRegistryErrorUrl() {
		return mUnassignRegistryErrorUrl;
	}

	/**
	 * @return the splitAndAssignRegistrySuccessUrl
	 */
	public String getSplitAndAssignRegistrySuccessUrl() {
		return mSplitAndAssignRegistrySuccessUrl;
	}

	/**
	 * @return the splitAndAssignRegistryErrorUrl
	 */
	public String getSplitAndAssignRegistryErrorUrl() {
		return mSplitAndAssignRegistryErrorUrl;
	}

	/**
	 * @return the moveNextSuccessUrl
	 */
	public String getMoveNextSuccessUrl() {
		return mMoveNextSuccessUrl;
	}

	/**
	 * @return the moveNextErrorUrl
	 */
	public String getMoveNextErrorUrl() {
		return mMoveNextErrorUrl;
	}

	/**
	 * @param pUnassignRegistrySuccessUrl the unassignRegistrySuccessUrl to set
	 */
	public void setUnassignRegistrySuccessUrl(String pUnassignRegistrySuccessUrl) {
		mUnassignRegistrySuccessUrl = pUnassignRegistrySuccessUrl;
	}

	/**
	 * @param pUnassignRegistryErrorUrl the unassignRegistryErrorUrl to set
	 */
	public void setUnassignRegistryErrorUrl(String pUnassignRegistryErrorUrl) {
		mUnassignRegistryErrorUrl = pUnassignRegistryErrorUrl;
	}

	/**
	 * @param pSplitAndAssignRegistrySuccessUrl the splitAndAssignRegistrySuccessUrl to set
	 */
	public void setSplitAndAssignRegistrySuccessUrl(
			String pSplitAndAssignRegistrySuccessUrl) {
		mSplitAndAssignRegistrySuccessUrl = pSplitAndAssignRegistrySuccessUrl;
	}

	/**
	 * @param pSplitAndAssignRegistryErrorUrl the splitAndAssignRegistryErrorUrl to set
	 */
	public void setSplitAndAssignRegistryErrorUrl(
			String pSplitAndAssignRegistryErrorUrl) {
		mSplitAndAssignRegistryErrorUrl = pSplitAndAssignRegistryErrorUrl;
	}

	/**
	 * @param pMoveNextSuccessUrl the moveNextSuccessUrl to set
	 */
	public void setMoveNextSuccessUrl(String pMoveNextSuccessUrl) {
		mMoveNextSuccessUrl = pMoveNextSuccessUrl;
	}

	/**
	 * @param pMoveNextErrorUrl the moveNextErrorUrl to set
	 */
	public void setMoveNextErrorUrl(String pMoveNextErrorUrl) {
		mMoveNextErrorUrl = pMoveNextErrorUrl;
	}
	/**
	 * @return the collectionQty
	 */
	public long getCollectionQty() {
		return mCollectionQty;
	}

	/**
	 * @param pCollectionQty the collectionQty to set
	 */
	public void setCollectionQty(long pCollectionQty) {
		mCollectionQty = pCollectionQty;
	}
	/**
	 * @return the couponErrorUrl
	 */
	public String getCouponErrorUrl() {
		return couponErrorUrl;
	}
	/**
	 * @param pCouponErrorUrl the couponErrorUrl to set
	 */
	public void setCouponErrorUrl(String pCouponErrorUrl) {
		couponErrorUrl = pCouponErrorUrl;
	}
	
	/**
	 * @return the storeRepository
	 */
	public Repository getStoreRepository() {
		return mStoreRepository;
	}

	/**
	 * @param pStoreRepository the storeRepository to set
	 */
	public void setStoreRepository(Repository pStoreRepository) {
		mStoreRepository = pStoreRepository;
	}
	
	@SuppressWarnings("unchecked")
	public void associateLogin(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		TBSIDMFormHandler lIdmFormHandler = getIdmFormHandler();

		lIdmFormHandler.setUsername(getAssoUsername());
		lIdmFormHandler.setPassword(getAssoPassword());
		lIdmFormHandler.setSuccessURL(getCreateGSOrderSuccessURL());
		lIdmFormHandler.setErrorURL(getCreateGSOrderErrorURL());

		lIdmFormHandler.handleAssocAuth(pRequest, pResponse);

		if(lIdmFormHandler.getFormError()){
			getFormExceptions().addAll(lIdmFormHandler.getFormExceptions());
		}
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
   @SuppressWarnings("finally")
public boolean handleTBSCheckout(final DynamoHttpServletRequest pRequest,
		   final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBSystemException, BBBBusinessException {

	   vlogDebug("TBSCartFormHandler :: handleTBSCheckout () :: START");
	   //paypal start
	   BBBOrderImpl order = (BBBOrderImpl) getOrder();
	   Transaction tr = null;
	   String successURL = "";
	   StringBuffer AssosuccessURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer AssoerrorURL = new StringBuffer(BBBCoreConstants.BLANK);
		if (StringUtils.isNotEmpty(getFromPage())) {
			
			AssosuccessURL.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			
			AssoerrorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
		}
	   setAssoLoginSuccessURL(AssosuccessURL.toString());
	   setAssoLoginFailureURL(AssoerrorURL.toString());
	   try {
		   tr = ensureTransaction();
		   synchronized (getOrder()) {
			   try {
				   
				   vlogDebug("TBSCartFormHandler.handleTBSCheckout() : Invoking the associate login before proceeding to checkout ");
				   associateLogin(pRequest, pResponse);
				   
				   if (!checkFormRedirect(null, getAssoLoginFailureURL(), pRequest, pResponse))
						return false;

				   List<CommerceItem> citems = order.getCommerceItems();
				   for (CommerceItem commerceItem : citems) {
					   if((commerceItem instanceof LTLAssemblyFeeCommerceItem || commerceItem instanceof LTLDeliveryChargeCommerceItem) && !StringUtils.isEmpty(order.getToken())){
						   List<String> errorList = new ArrayList<String>();
						   errorList.add("Paypal token "+order.getToken()+ " has expired.");
						   getPayPalSessionBean().setErrorList(errorList);
						   getPaypalServiceManager().removePayPalPaymentGroup(((BBBOrderImpl) this.getOrder()), this.getUserProfile());
					   }
				   }
				   boolean isTokenExp = getPaypalServiceManager().isTokenExpired(this.getPayPalSessionBean(), order);
				   if(order.isBopusOrder() && !isTokenExp){
					   isTokenExp = true;
				   }					   
				   if(order.isPayPalOrder() && !isTokenExp){
					   vlogDebug("TBSCartFormHandler.handleTBSCheckout() :  Order is Paypal and token still exists, so normal checkout button " +
							   "will behave same as Checkout with paypal. Calling BBBCartFormHandler.handleCheckoutWithPaypal()");
					   getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
					   return handleCheckoutWithPaypal(pRequest, pResponse);
				   } else if(!(StringUtils.isEmpty(order.getToken()) && isTokenExp)) {
					   vlogDebug("Proceeding with normal checkout, so if there is any paypal payment group, removing it. ");
					   getPaypalServiceManager().removePayPalPaymentGroup(((BBBOrderImpl) this.getOrder()), this.getUserProfile());
				   }
				   //paypal end
				   pRequest.getSession().removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS);
				   // setting store id in the order
				   String lStoreId = (String) pRequest.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
				   if(!StringUtils.isBlank(lStoreId)){
					   order.setTbsStoreNo(lStoreId);
				   }

				   getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());

				   // Moved below If() condition from preExpressCheckout() and added here to fix RS defect 2156012
				   if (BBBCheckoutManager.getOrderInventoryStatus(order) == BBBInventoryManager.NOT_AVAILABLE) {
					   addFormException(new DropletException(this.getMsgHandler().getErrMsg(TBSConstants.ITEMS_OUTOF_STOCK,"EN", null), TBSConstants.ITEMS_OUTOF_STOCK));
					   return true;
				   }
				   if (!checkFormRedirect(null, getAssoLoginFailureURL(), pRequest, pResponse))
						return false;
				   				   
				   // below logic is to delete empty shipping groups from the order for all the scenarions like ltl, express, guest.
					
					getShippingGroupManager().removeEmptyShippingGroups(order);
					// emptyShipGrpRemoved=true;
				   if (getExpressCheckout()) {
					   vlogDebug("Proceeding with express checkout. ");
					   // review page
					   ((BBBOrderImpl) order).setExpressCheckOut(true);
					   return handleExpCheckout(pRequest, pResponse);
				   }
				   // first checkout page
				   ((BBBOrderImpl) order).setExpressCheckOut(false);

				   if (getProfile().isTransient()) {
					   getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.GUEST.toString());
				   } else {
					   getCheckoutState().setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString());
				   }
				   getOrderManager().updateOrder(order);
			   }
			   catch (CommerceException exc) {
				   processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
			   }
		   }
	   }
	   finally {
		   if (tr != null) commitTransaction(tr);
		   
		   if (!StringUtils.isEmpty(getCheckoutState().getFailureURL())
				   && !getCheckoutState().getFailureURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
			   successURL = pRequest.getContextPath() + getCheckoutState().getFailureURL();
		   }
		   if(isMultiship()){
			   successURL += TBSConstants.MULTISHIP_SELECTION_URL;
		   }
		   
		   setSucessURL(successURL);
		   vlogDebug("redirect URL :: "+successURL);
		   vlogDebug("TBSCartFormHandler :: handleTBSCheckout () :: END");
		   return checkFormRedirect(getAssoLoginSuccessURL(), null, pRequest, pResponse);
	   }
   }
   
	 /**Coupons and Promotion retrieval for cart Page
	    * @param pRequest the request object
	    * @param pResponse the response object
	    * @exception ServletException if an error occurs
	    * @exception IOException if an error occurs
	    */
	   public boolean handleRetrieveCoupons(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			   throws ServletException, IOException {
			
			vlogDebug("TBSCartFormHandler :: handleRetrieveCoupons () :: START");
			StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
			StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
			if (StringUtils.isNotEmpty(getFromPage())) {
				
				successURL.append(pRequest.getContextPath())
				.append(getSuccessUrlMap().get(getFromPage()));
				
				errorURL.append(pRequest.getContextPath())
				.append(getErrorUrlMap().get(getFromPage()));
			}
			setCouponSuccessUrl(successURL.toString());
			setCouponErrorUrl(errorURL.toString());
			if(StringUtils.isBlank(getMobileNumber()) && StringUtils.isBlank(getEmailAddress())){
				addGSFormException(new DropletException("Please enter a valid email or mobile number"));
			}
			if(!StringUtils.isBlank(getMobileNumber()) || !StringUtils.isBlank(getEmailAddress())){
				mTbsSessionComponent.setEmailId(getEmailAddress());
				mTbsSessionComponent.setMobileNumber(getMobileNumber());
				vlogDebug("TBSCartFormHandler :: handleRetrieveCoupons () :: END");
			}
			return checkFormRedirect(getCouponSuccessUrl(), getCouponErrorUrl(), pRequest, pResponse);
		}
	
	/**
	 * This method is used to navigate the quantity selection page, if the qty is more than 1.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public boolean handleMoveNext(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBBusinessException, BBBSystemException {
		StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
		if (StringUtils.isNotEmpty(getFromPage())) {
			
			successURL.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			
			StringBuffer appendData = new StringBuffer("");
			if(StringUtils.isNotEmpty(getSuccessQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getSuccessQueryParam());
				successURL.append(appendData);
				
			}
			if(BBBUtility.isNotEmpty(this.getRegistryId())){
				successURL.append("&registryId=").append(this.getRegistryId());
			}
			if(StringUtils.isNotEmpty(getErrorQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getErrorQueryParam());
				errorURL.append(appendData);
			}
			setMoveNextSuccessUrl(successURL.toString());
			setMoveNextErrorUrl(errorURL.toString());
			
		}
			return checkFormRedirect(getMoveNextSuccessUrl(), getMoveNextErrorUrl(), pRequest, pResponse);
	}
	
	/** 
    * This method is used to assign the registry with the requested qty and if commerceitem qty is remaining
    * it will create the new commerceitem for the remaining qty.
    * 
    * @param pRequest
    * @param pResponse
    * @return
    * @throws ServletException
    * @throws IOException 
	* @throws BBBSystemException 
	* @throws BBBBusinessException
	*/
	@SuppressWarnings({ "static-access" })
	public boolean handleSplitAndAssignRegistry(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws ServletException, IOException, BBBBusinessException, BBBSystemException {
	    BBBOrder order = (BBBOrder) getOrder();
	    
	    vlogDebug("TBSCartFormHandler :: handleSplitAndAssignRegistry () :: START");

	    long commerceItemQty = 0;
	    long remainingQty = 0;
	    String catalogRefId = null;
	    TBSCommerceItem existingRegistryCitem = null;
	    boolean mergeRegItem = true;
	    boolean rollback = true;
	    // currentCommerceItem id
	    String currentId = getCommerceItemId();
	    StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
	   if (StringUtils.isNotEmpty(getFromPage())) {
			successURL
			.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			setSplitAndAssignRegistrySuccessUrl(successURL.toString());
			setSplitAndAssignRegistryErrorUrl(errorURL.toString());
		}
	    preSplitAndAssignRegistry(pRequest, pResponse);
	    
	    TransactionDemarcation td = new TransactionDemarcation();
			    
	    if(!getFormError()){
	    	try {
	    		td.begin(getTransactionManager());
			    	
		    	TBSPurchaseProcessHelper purchaseProcessHelper = (TBSPurchaseProcessHelper)getPurchaseProcessHelper();
		    	RegistrySummaryVO registryInfo = null;
		    	TBSItemInfo itemInfo = null;
		    	List<String> citemIds = new ArrayList<String>();
		    	
		    	registryInfo = purchaseProcessHelper.getGiftRegistryManager().getRegistryInfo(getRegistryId(), getSiteId());
		    	vlogDebug("Registry data for id :: "+getRegistryId() +" is :: "+registryInfo.getPrimaryRegistrantFirstName());
		    	
		    	//get commerceItem based on id
		    	CommerceItem currentItem = ((BBBCommerceItemManager)getCommerceItemManager()).getCommerceItemFromOrder(order, currentId);
			    	
		    	//setting registry details to commerceitem
		    	if (currentItem != null && currentItem instanceof TBSCommerceItem) {
		    		TBSCommerceItem tbsCurrentItem = (TBSCommerceItem) currentItem;
		    		vlogDebug("Current commerceItem found.");
		    		tbsCurrentItem.setRegistryId(getRegistryId());
		    		tbsCurrentItem.setRegistryInfo(purchaseProcessHelper.getRegistryInfo(registryInfo));
		    		ShippingGroup shipObj = ((ShippingGroupRelationship) tbsCurrentItem.getShippingGroupRelationships().get(0)).getShippingGroup();
		    		BBBHardGoodShippingGroup sg = null;
	    			if(shipObj instanceof BBBHardGoodShippingGroup){
	    				sg = (BBBHardGoodShippingGroup)shipObj;
	    				sg.setRegistryId(getRegistryId());
						sg.setRegistryInfo(purchaseProcessHelper.getRegistryInfo(registryInfo));
	    			}
					
		    		commerceItemQty = currentItem.getQuantity();
	    			remainingQty = commerceItemQty - getQuantity();
	    			catalogRefId = currentItem.getCatalogRefId();
	    			vlogDebug("Remaining quantity to create the new commerceitem :: "+remainingQty);
	    			if(tbsCurrentItem.getTBSItemInfo() != null){
	    				itemInfo = tbsCurrentItem.getTBSItemInfo();
	    			}
	    		}
		    	//getting the other commerceitem with same catalogRefId and same registryId
		    	existingRegistryCitem = getExistingItem(pRequest, catalogRefId, existingRegistryCitem,
						currentId, purchaseProcessHelper, registryInfo, citemIds);
		    	synchronized(order) {
			    	//combining the both items with the registry details.
			    	if(existingRegistryCitem != null){
			    		vlogDebug("Existing commerceitem found with sku :: "+catalogRefId +" and registryId :: "+getRegistryId());
			    		//increase the current item qty with existing item qty + assigned qty.
			    		pRequest.setParameter(currentId, getQuantity() + existingRegistryCitem.getQuantity());
			    		// Set the new quantity for the commerce item being updated.
			    		setCheckForChangedQuantity(true);
			    		
			    		//removing the selected commerceitem
			    		String removeids[] = {existingRegistryCitem.getId()};
			    		setRemovalCommerceIds(removeids);
			    		
			    		//modifying the gift registry assigned item with the selected qty. 
			    		modifyOrderByCommerceId(pRequest, pResponse);
						mergeRegItem = false;
			    	}
			    	if(remainingQty > 0){
			    		if(mergeRegItem){
				    		//modifying the gift registry assigned item with the selected qty. 
				    		pRequest.setParameter(currentId, getQuantity());
				    		setCheckForChangedQuantity(true);
				    		modifyOrderByCommerceId(pRequest, pResponse);
			    		}
			    		vlogDebug("Creating new commerceitem with quantity :: "+remainingQty);
			    		//creating the new item based on the remainingQty.
			    		setQuantity(remainingQty);
			    		super.addItemToOrder(pRequest, pResponse);
			    		// assign overridden info to newly created item
			    		assignTbsInfo(order, remainingQty, itemInfo, citemIds);
			    		updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
			    	}
		    	}
		    	rollback = false;
		    	((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(getOrder(),currentId);
		    } catch (TransactionDemarcationException e) {
				vlogError("TransactionDemarcationException occurred :: "+e);
			} catch (RunProcessException e) {
	    		vlogError("RunProcessException occurred :: "+e);
	    	} catch (CommerceException e) {
	    		vlogError("CommerceException occurred :: "+e);
	    	}
	    	finally {
	    		try {
					td.end(rollback);
				} catch (TransactionDemarcationException tde) {
					vlogError("TransactionDemarcationException "+tde);
				}
	    	}
    	}
    	vlogDebug("TBSCartFormHandler :: handleSplitAndAssignRegistry () :: END");
    	return checkFormRedirect(getSplitAndAssignRegistrySuccessUrl(), getSplitAndAssignRegistryErrorUrl(), pRequest, pResponse);
	}

	/**
	 * This method is used to assign the tbsItemInfo to the newly created item
	 * @param order
	 * @param remainingQty
	 * @param itemInfo
	 * @param citemIds
	 * @throws CommerceException 
	 */
	@SuppressWarnings("unchecked")
	private void assignTbsInfo(BBBOrder order, long pQty,
			TBSItemInfo itemInfo, List<String> citemIds) throws CommerceException {
		if(itemInfo != null){
			List<CommerceItem> newCommerceItems = order.getCommerceItems();
			TBSCommerceItem newCommerceItem = null;
			for (CommerceItem commerceItem2 : newCommerceItems) {
				if(!citemIds.contains(commerceItem2.getId()) && commerceItem2 instanceof TBSCommerceItem){
					newCommerceItem = (TBSCommerceItem) commerceItem2;
					break;
				}
			}
			TBSItemInfo newItemInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSItemInfo();
			newItemInfo.setOverideReason(itemInfo.getOverideReason());
			newItemInfo.setOverridePrice(itemInfo.getOverridePrice());
			newItemInfo.setOverrideQuantity((int)pQty);
			newItemInfo.setCompetitor(itemInfo.getCompetitor());
			newItemInfo.setPriceOveride(true);
			newCommerceItem.setTBSItemInfo(newItemInfo);
		}
	}
	

	/**
	 * 
	 * @param pRequest
	 * @param commerceItems
	 * @param catalogRefId
	 * @param existingRegistryCitem
	 * @param currentId
	 * @param purchaseProcessHelper
	 * @param registryInfo
	 * @param citemIds
	 * @return
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	private TBSCommerceItem getExistingItem(DynamoHttpServletRequest pRequest, String catalogRefId, TBSCommerceItem existingRegistryCitem, String currentId,
			TBSPurchaseProcessHelper purchaseProcessHelper, RegistrySummaryVO registryInfo, List<String> citemIds) {
		
		List<CommerceItem> commerceItems = getOrder().getCommerceItems();
		for (CommerceItem commerceItem : commerceItems) {
			citemIds.add(commerceItem.getId());
			//setting the all commerceitems id, qty to request.
			pRequest.setParameter(commerceItem.getId(), commerceItem.getQuantity());
			if(commerceItem instanceof TBSCommerceItem){
				TBSCommerceItem tbsCommerceItem = (TBSCommerceItem) commerceItem;
				if(isAllItemsForRegistry()){
					tbsCommerceItem.setRegistryId(getRegistryId());
					String info = purchaseProcessHelper.getRegistryInfo(registryInfo);
					tbsCommerceItem.setRegistryInfo(info);
					ShippingGroup shipObj = ((ShippingGroupRelationship) tbsCommerceItem.getShippingGroupRelationships().get(0)).getShippingGroup();
		    		BBBHardGoodShippingGroup sg = null;
	    			if(shipObj instanceof BBBHardGoodShippingGroup){
	    				sg = (BBBHardGoodShippingGroup)shipObj;
	    				sg.setRegistryId(getRegistryId());
						sg.setRegistryInfo(purchaseProcessHelper.getRegistryInfo(registryInfo));
	    			}
						
				}
				if (catalogRefId.equals(tbsCommerceItem.getCatalogRefId()) && !tbsCommerceItem.getId().equals(currentId)
						&& getRegistryId().equals(tbsCommerceItem.getRegistryId())) {
					
					existingRegistryCitem = tbsCommerceItem;
				}
			}
		}
		return existingRegistryCitem;
	}
	
	/**
	 * This method is used for validating the registry details and the commerceItem details while setting the
	 * registry details to the commerceitem.
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private void preSplitAndAssignRegistry(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("TBSCartFormHandler :: preSplitAndAssignRegistry () :: START");
		if(StringUtils.isBlank(getRegistryId())){
			addFormException(new DropletException("Please select the Registry to map with the product."));
			vlogError("Registry Id is not available :: "+getRegistryId());
		}
		if(StringUtils.isBlank(getCommerceItemId())){
			addFormException(new DropletException("Please select the item to assign the registry details."));
			vlogError("Item is not available to assign the registry details :: "+getCommerceItemId());
		}
		if(getQuantity() <= 0 ){
			addFormException(new DropletException("Assign Quantity should not be less than or zero."));
			vlogError("Assign quantity is :: "+getQuantity());
		}
		vlogDebug("TBSCartFormHandler :: preSplitAndAssignRegistry () :: END");
	}
	

	/**
	 * This method is used for unassigning the giftregistry and combine with the regular commerce item if any
	 * with same catalogrefId.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public boolean handleUnassignRegistry(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws ServletException, IOException, BBBBusinessException, BBBSystemException {
		
		vlogDebug("TBSCartFormHandler :: handleUnassignRegistry() method :: START");
		String removeRegistryId = null;
		boolean regDelFlag = true;
		String unselectedCatalogrefId = null;
		long unAssignedQty = 0;
		TBSCommerceItem mergeItem = null;
		String unseletedProdId = null;
		boolean rollback = true;
		List<String> citemIds = new ArrayList<String>();
		String unselectItemId = getCommerceItemId();
		BBBOrder order = (BBBOrder) getOrder();
		vlogDebug("Unassigned commerceitem id :: "+unselectItemId);
		StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
	   if (StringUtils.isNotEmpty(getFromPage())) {
			successURL
			.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			setUnassignRegistrySuccessUrl(successURL.toString());
			setUnassignRegistryErrorUrl(errorURL.toString());
		}
		TransactionDemarcation td = new TransactionDemarcation();
		if(order != null){
			try {
	    		td.begin(getTransactionManager());
			
				TBSCommerceItem unAssignededItem = (TBSCommerceItem) ((BBBCommerceItemManager)getCommerceItemManager()).
						getCommerceItemFromOrder(order, unselectItemId);
				
				if(unAssignededItem != null){
					vlogDebug("Unassigned commerceitem found :: ");
					removeRegistryId = unAssignededItem.getRegistryId();
					unAssignedQty = unAssignededItem.getQuantity();
					unAssignededItem.setRegistryId(null);
					unAssignededItem.setRegistryInfo(null);
					unselectedCatalogrefId = unAssignededItem.getCatalogRefId();
					unseletedProdId = unAssignededItem.getAuxiliaryData().getProductId();
				}
				if(!StringUtils.isBlank(removeRegistryId)){
					vlogDebug("Unassigned registry Id :: "+removeRegistryId);
					Map<String, RegistrySummaryVO> registryMap = order.getRegistryMap();
					
					List<CommerceItem> cItems = order.getCommerceItems();
					//removing the registry details from commerceitem 
					for (CommerceItem commerceItem : cItems) {
						citemIds.add(commerceItem.getId());
						if(commerceItem instanceof TBSCommerceItem){
							TBSCommerceItem tbsItem = (TBSCommerceItem) commerceItem;
							if(!unselectItemId.equals(tbsItem.getId())){
								if(!StringUtils.isBlank(unselectedCatalogrefId) &&
										unselectedCatalogrefId.equals(tbsItem.getCatalogRefId()) &&
										StringUtils.isBlank(tbsItem.getRegistryId())){
									
									mergeItem = tbsItem;
								}
							}
							if(!StringUtils.isBlank(tbsItem.getRegistryId()) && removeRegistryId.equals(tbsItem.getRegistryId())){
								vlogDebug("registry Id :: "+removeRegistryId +" is also assigned to :: "+tbsItem.getId() +" commerceitem");
								regDelFlag = false;
							}
						}
					}
				
					if(registryMap != null && !registryMap.isEmpty() && regDelFlag){
						vlogDebug("registryId :: "+ removeRegistryId+" not assigned for any commerceitem, so "
								+ "removing the registry Id from the order registry map ");
						registryMap.remove(removeRegistryId);
						order.setRegistryMap(registryMap);
					}
				}
				synchronized(order) {
					if(mergeItem != null){
						vlogDebug("Unassigned commerceitem is going to merge with the normal commerceItem with Id :: "+mergeItem.getId());
						TBSItemInfo itemInfo = mergeItem.getTBSItemInfo();
						//removing the unassigned item
						getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, unselectItemId);
						order.removeCommerceItem(unselectItemId);
						((BBBPurchaseProcessHelper)getPurchaseProcessHelper()).repriceOrder(order, getUserPricingModels(), getUserLocale(), getProfile());
						
						//createting the commerceitem with the same values of the existing item, so that OOTB will merge these 2 items.
						String catalogref[] = {unselectedCatalogrefId};
						setCatalogRefIds(catalogref);
						setProductId(unseletedProdId);
						setQuantity(unAssignedQty);
						super.addItemToOrder(pRequest, pResponse);
						// assign overridden info to newly created item
			    		assignTbsInfo(order, unAssignedQty, itemInfo, citemIds);
						updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
					}
				}
				rollback = false;
				((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(getOrder(),unselectItemId);
			} catch (TransactionDemarcationException e) {
				vlogError("TransactionDemarcationException occurred :: "+e);
			} catch (CommerceException e) {
	    		vlogError("CommerceException occurred :: "+e);
	    	}
	    	finally {
	    		try {
					td.end(rollback);
				} catch (TransactionDemarcationException tde) {
					vlogError("TransactionDemarcationException "+tde);
				}
	    	}
		}
		vlogDebug("TBSCartFormHandler :: handleUnassignRegistry() method :: END");
		return checkFormRedirect(getUnassignRegistrySuccessUrl(), getUnassignRegistryErrorUrl(), pRequest, pResponse);
	}
	
	
	/**
	 * Handle create gs order.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @return true, if successful
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public boolean handleCreateGSOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
	IOException {
		vlogDebug("TBSCartFormHandler :: handleCreateGSOrder() method :: START");
		TBSIDMFormHandler lIdmFormHandler = getIdmFormHandler();
		Transaction tr = null;
		try {
			tr = ensureTransaction();
			if (getUserLocale() == null)
				setUserLocale(getUserLocale(pRequest, pResponse));

			// If any form errors found, redirect to error URL:
			if (!checkFormRedirect(null, getCreateGSOrderErrorURL(),
					pRequest, pResponse))
				return false;
			BBBOrder order = (BBBOrder) getOrder();
			synchronized (order) {
				preCreateGSOrder(pRequest, pResponse);

				// If any form errors found, redirect to error URL:
				if (!checkFormRedirect(null, getCreateGSOrderErrorURL(),
						pRequest, pResponse))
					return false;

				lIdmFormHandler.setUsername(getAssoUsername());
				lIdmFormHandler.setPassword(getAssoPassword());
				lIdmFormHandler.setSuccessURL(getCreateGSOrderSuccessURL());
				lIdmFormHandler.setErrorURL(getCreateGSOrderErrorURL());

				lIdmFormHandler.handleAssocAuth(pRequest, pResponse);

				if(lIdmFormHandler.getFormError()){
					getFormExceptions().addAll(lIdmFormHandler.getFormExceptions());
				}

				if (!checkFormRedirect(null, getCreateGSOrderErrorURL(),
						pRequest, pResponse))
					return false;

				createGSOrder(pRequest, pResponse);

				// If any form errors found, redirect to error URL:
				if (!checkFormRedirect(null, getCreateGSOrderErrorURL(),
						pRequest, pResponse))
					return false;

				postCreateGSOrder(pRequest, pResponse);

				updateOrder(order, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
			} // synchronized

			// If NO form errors are found, redirect to the success URL.
			// If form errors are found, redirect to the error URL.
			vlogDebug("TBSCartFormHandler :: handleCreateGSOrder() method :: END");
			return checkFormRedirect(getCreateGSOrderSuccessURL(), getCreateGSOrderErrorURL(), pRequest, pResponse);
		} finally {
			if (tr != null)
				commitTransaction(tr);
		}
	}


	/**
	 * Creates the Guidden selling order.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void createGSOrder(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("TBSCartFormHandler :: createGSOrder() method :: START");
		vlogDebug("Guided selling orderId :: "+getGsOrderId());
		try {
			RepositoryItem lItem = getGuidedSellingRepository().getItem(getGsOrderId(), "gsOrderInfo");
			if(lItem != null){
				
				String usedOrder = (String) lItem.getPropertyValue("atgOrderId");
				vlogDebug("ATG orderId of Guided selling order :: "+usedOrder);
				if(!StringUtils.isBlank(usedOrder)){
					this.addGSFormException(new DropletException("The guided selling order is already used and its not allowed to use it again : " + getGsOrderId()));
					return;
				}
				
				String skus = (String) lItem.getPropertyValue("skus");
				String quantities = (String)lItem.getPropertyValue("quantities");
				String[] lSkusArray = skus.split(",");
				String[] lQuantitiesArray = quantities.split(",");
				
				((TBSOrderManager)getOrderManager()).clearOrderForGS(getOrder());
				
				createCommerceItemInfos(lSkusArray, lQuantitiesArray);

				addMultipleItemsToOrder(pRequest, pResponse);
			} else {
				vlogDebug("No Guided selling order found with Id :: "+getGsOrderId());
				addGSFormException(new DropletException("there is no guided selling order with id : " + getGsOrderId()));
			}

		} catch (RepositoryException e) {
			addGSFormException(new DropletException("there is an error in fetching the item"));
			vlogError("there is an error in fetching the item", e);
		}
		vlogDebug("TBSCartFormHandler :: createGSOrder() method :: END");
	}

	
	/**
	 * Creates the commerce item infos.
	 *
	 * @param pSkusArray the skus array
	 * @param pQuantitiesArray the quantities array
	 */
	@SuppressWarnings("unchecked")
	public void createCommerceItemInfos(String[] pSkusArray, String[] pQuantitiesArray) {
		vlogDebug("TBSCartFormHandler :: createCommerceItemInfos() method :: START");
		setAddItemCount(pSkusArray.length);

		for (int i = 0; i < pSkusArray.length; i++) {
			String lSkuId = pSkusArray[i];
			if (pQuantitiesArray[i] != null) {
				long lQuantity = Long.parseLong(pQuantitiesArray[i]);

				if (!StringUtils.isBlank(lSkuId) && lQuantity > 0) {

					RepositoryItem skuItem;
					try {
						skuItem = getOrderManager().getCatalogTools().findSKU(lSkuId);

						if (skuItem != null) {
							Object parentProducts = skuItem.getPropertyValue("parentProducts");
						    if (parentProducts != null)
						    {
						    	ChangeAwareSet parentProductsSet = (ChangeAwareSet)parentProducts;
						    	if (parentProductsSet.size() > 0) {
						    		RepositoryItem prodItem =  (RepositoryItem) parentProductsSet.toArray()[0];
									if (prodItem != null) {
										String productId = prodItem.getRepositoryId();

										AddCommerceItemInfo info = getItems()[i];
										info.setCommerceItemType("tbsCommerceItem");
										info.setCatalogRefId(lSkuId);
										info.setQuantity(lQuantity);
										info.setSiteId(SiteContextManager.getCurrentSiteId());
										info.setProductId(productId);
										info.getValue().put("gsOrderId", getGsOrderId());
									}

						    	}
						    }
						}
					} catch (RepositoryException e) {
						addGSFormException(new DropletException("there is an error while getting sku item"));
						if(isLoggingError()){
							logError("there is an error while getting sku item", e);
						}
					}

				}
			}
		}
		vlogDebug("TBSCartFormHandler :: createCommerceItemInfos() method :: START");
		return;
	}

	/**
	 * Pre create gs order.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void preCreateGSOrder(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		if(StringUtils.isBlank(getAssoUsername())){
			addGSFormException(new DropletException("Please enter the Associate Username"));
		}
		
		if(StringUtils.isBlank(getAssoPassword())){
			addGSFormException(new DropletException("Please enter the Associate Password"));
		}
		
		if(StringUtils.isBlank(getGsOrderId())){
			addGSFormException(new DropletException("Please enter the GS order id"));
		}
		
	}
	
	
	/**
	   * Adds a new exception to the list of FormExceptions.
	   */
	  @SuppressWarnings("unchecked")
	public void addGSFormException(DropletException exc) {
	    // validate inputs
	    if (exc == null) {
	      if (isLoggingError()) {
	        logError(new IllegalArgumentException("The argument to addFormException cannot be null"));
	      }
	      return;
	    }

	    if (isLoggingDebug()) {
	      logDebug("adding form exception: " + exc);
	      logDebug("root cause of exception: " + exc.getRootCause());
	    }

	    getFormExceptions().addElement(exc);

	    /*
	     * Also add this exception to the list of exceptions that occurred
	     * globally for this request
	     */
	    /*DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
	    if (request != null) {
	      NameContext ctx = request.getRequestScope();
	      if (ctx != null) {
	        Vector v = (Vector) ctx.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE);
	        if (v == null) {
	          v = new Vector();
	           This is for compatibility 
	          request.setAttribute(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, v);
	           This is so that exceptions are preserved across request scope 
	          ctx.putElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, v);
	        }
	        if (!v.contains(exc))
	          v.addElement(exc);
	      }
	    }*/
	  }

	
	
	/**
	 * Post create gs order.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void postCreateGSOrder(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		//post create GS order method
	}
	
	/**
	 * Gets the tbs session component.
	 *
	 * @return the tbs session component
	 */
	public TBSSessionComponent getTbsSessionComponent() {
		return mTbsSessionComponent;
	}

	/**
	 * Sets the tbs session component.
	 *
	 * @param pTbsSessionComponent the new tbs session component
	 */
	public void setTbsSessionComponent(TBSSessionComponent pTbsSessionComponent) {
		mTbsSessionComponent = pTbsSessionComponent;
	}
	
	/**
	 * Gets the creates the gs order success url.
	 *
	 * @return the creates the gs order success url
	 */
	public String getCreateGSOrderSuccessURL() {
		return mCreateGSOrderSuccessURL;
	}
	
	/**
	 * Sets the creates the gs order success url.
	 *
	 * @param pCreateGSOrderSuccessURL the new creates the gs order success url
	 */
	public void setCreateGSOrderSuccessURL(String pCreateGSOrderSuccessURL) {
		mCreateGSOrderSuccessURL = pCreateGSOrderSuccessURL;
	}

	/**
	 * Gets the creates the gs order error url.
	 *
	 * @return the creates the gs order error url
	 */
	public String getCreateGSOrderErrorURL() {
		return mCreateGSOrderErrorURL;
	}

	/**
	 * Sets the creates the gs order error url.
	 *
	 * @param pCreateGSOrderErrorURL the new creates the gs order error url
	 */
	public void setCreateGSOrderErrorURL(String pCreateGSOrderErrorURL) {
		mCreateGSOrderErrorURL = pCreateGSOrderErrorURL;
	}


	/**
	 * Gets the guided selling repository.
	 *
	 * @return the guided selling repository
	 */
	public Repository getGuidedSellingRepository() {
		return mGuidedSellingRepository;
	}


	/**
	 * Sets the guided selling repository.
	 *
	 * @param pGuidedSellingRepository the new guided selling repository
	 */
	public void setGuidedSellingRepository(Repository pGuidedSellingRepository) {
		mGuidedSellingRepository = pGuidedSellingRepository;
	}


	/**
	 * Gets the gs order id.
	 *
	 * @return the gs order id
	 */
	public String getGsOrderId() {
		return mGsOrderId;
	}


	/**
	 * Sets the gs order id.
	 *
	 * @param pGsOrderId the new gs order id
	 */
	public void setGsOrderId(String pGsOrderId) {
		mGsOrderId = pGsOrderId;
	}
	
	public String getAssoUsername() {
		return mAssoUsername;
	}

	public void setAssoUsername(String pAssoUsername) {
		mAssoUsername = pAssoUsername;
	}

	public String getAssoPassword() {
		return mAssoPassword;
	}

	public void setAssoPassword(String pAssoPassword) {
		mAssoPassword = pAssoPassword;
	}

	public TBSIDMFormHandler getIdmFormHandler() {
		return mIdmFormHandler;
	}

	public void setIdmFormHandler(TBSIDMFormHandler pIdmFormHandler) {
		mIdmFormHandler = pIdmFormHandler;
	}

	/**
	 * @return the assoLoginSuccessURL
	 */
	public String getAssoLoginSuccessURL() {
		return mAssoLoginSuccessURL;
	}

	/**
	 * @param pAssoLoginSuccessURL the assoLoginSuccessURL to set
	 */
	public void setAssoLoginSuccessURL(String pAssoLoginSuccessURL) {
		mAssoLoginSuccessURL = pAssoLoginSuccessURL;
	}

	/**
	 * @return the assoLoginFailureURL
	 */
	public String getAssoLoginFailureURL() {
		return mAssoLoginFailureURL;
	}

	/**
	 * @param pAssoLoginFailureURL the assoLoginFailureURL to set
	 */
	public void setAssoLoginFailureURL(String pAssoLoginFailureURL) {
		mAssoLoginFailureURL = pAssoLoginFailureURL;
	}
	
	/**
	 * This method is overridden to remove the approver required flag and registry data from the order.
	 * and get the autoWaiveShipping details from the service
	 */
	public void postRemoveItemFromOrder(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		TBSOrder order = (TBSOrder) getOrder();
		for (String remCommerceId : getRemovalCommerceIds()) {
			if (null != order.getOverridePriceMap() && order.getOverridePriceMap().containsKey(remCommerceId)) {
				order.getOverridePriceMap().remove(remCommerceId);
			}
		}
		if (order.getOverridePriceMap().keySet().isEmpty()) {
			order.setTBSApprovalRequired(false);
		}
		
		((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(getOrder(),null);
		super.postRemoveItemFromOrder(pRequest, pResponse);
		
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

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final boolean handleItemPriceOverride(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		pRequest.getSession().removeAttribute("priceOverrideStatusVO");
		boolean rollback = true;
		StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
	   if (StringUtils.isNotEmpty(getFromPage())) {
		   successURL
			.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			setOverrideSuccessURL(successURL.toString());
			setOverrideErrorURL(errorURL.toString());
		}
	
		//Validate Form for Empty Fields
		if (!validateOverrideForm(pRequest)){			
			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
		}
		
    	final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
    	final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    	
    	if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
    		BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
    		TransactionDemarcation td = new TransactionDemarcation();
    		try {
    			td.begin(getTransactionManager());
    			if (this.getUserLocale() == null) {
    				this.setUserLocale(this.getUserLocale(pRequest, pResponse));
    			}
		
    			CommerceItemManager cimgr = getCommerceItemManager();
    			TBSOrder order = (TBSOrder)this.getOrder();    
    			synchronized (order) {
    		    	Map<String, Boolean> pricemap = order.getOverridePriceMap();
    		    	CommerceItem item = order.getCommerceItem(getOverrideId());
    		    	long oldQty = item.getQuantity();
    		    	TBSItemInfo existingItemInfo = null;
    		    	TBSItemInfo newItemInfo = null;
    		    	TBSItemInfo itemInfo;
    		    	if( item instanceof TBSCommerceItem ) {
    		    		TBSCommerceItem tbsItem = (TBSCommerceItem)item;
    		    		existingItemInfo = tbsItem.getTBSItemInfo();
    		    		TBSCommerceItem tbsnewItem = null;
    		    		
    		    		//to reduce the qty of original item
    		    		if(oldQty > getOverrideQuantity() ) {
    		    			boolean isSKULtl = getCatalogUtil().isSkuLtl(getSiteId(), item.getCatalogRefId());
    		    			//modifing the existing item qty
    		    			long remaingQty = oldQty - getOverrideQuantity();
    		    			LTLDeliveryChargeCommerceItem deliveryItem = null;
    		    			List<CommerceItem> cItems = order.getCommerceItems();
    		    			for (CommerceItem commerceItem : cItems) {
								if(commerceItem.getId().equals(getOverrideId())) {
									pRequest.setParameter(item.getId(), remaingQty);
								} else {
									pRequest.setParameter(commerceItem.getId(), commerceItem.getQuantity());
								}
							}
    		    			if(isSKULtl) {
    		    				deliveryItem = (LTLDeliveryChargeCommerceItem) order.getCommerceItem(tbsItem.getDeliveryItemId());
    		    				pRequest.setParameter(deliveryItem.getId(), remaingQty);
    		    			}
    		    			setCheckForChangedQuantity(true);
    		    			modifyOrderByCommerceId(pRequest, pResponse);
    		    			
    		    			LTLDeliveryChargeCommerceItem deliverynewItem = null;

    		                //creating new commerceitem
    		    			tbsnewItem  = (TBSCommerceItem)cimgr.createCommerceItem(item.getCommerceItemClassType(), item.getCatalogRefId(),
    		    					item.getAuxiliaryData().getProductId(), getOverrideQuantity());
    		    			
    		    			newItemInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSItemInfo();
    		    			tbsnewItem.setTBSItemInfo(newItemInfo);
    		    			((TBSPurchaseProcessHelper)getPurchaseProcessHelper()).addBBBProperties(tbsItem, tbsnewItem);
    		    			
    		    			CommerceItem newItem = cimgr.addItemToOrder(order, tbsnewItem);
    		    			
    		    			long lUnassignedQuantity = cimgr.getUnassignedQuantityForCommerceItem(newItem);
    		    			ShippingGroup sg = ((ShippingGroupRelationship) tbsItem.getShippingGroupRelationships().get(0)).getShippingGroup();
    		    			

    		    			//ShippingGroup sg = (ShippingGroup) order.getShippingGroups().get(0);
    		    			cimgr.addItemQuantityToShippingGroup(order, newItem.getId(), sg.getId(), lUnassignedQuantity);
    		    			
    		    			if(isSKULtl){ 
	    		    			//creating new commerceitem
	    		    			deliverynewItem  = (LTLDeliveryChargeCommerceItem) cimgr.createCommerceItem(deliveryItem.getCommerceItemClassType(), deliveryItem.getCatalogRefId(),
	    		    					deliveryItem.getAuxiliaryData().getProductId(), getOverrideQuantity());
	    		    			deliverynewItem.setLtlCommerceItemRelation(tbsnewItem.getId());
	    		    			//CommerceItem newdeliveryItem = cimgr.addItemToOrder(order, deliverynewItem);
	    		    			ShippingGroup ltlsg = ((ShippingGroupRelationship) deliveryItem.getShippingGroupRelationships().get(0)).getShippingGroup();
	    		    			//ShippingGroup sg = (ShippingGroup) order.getShippingGroups().get(0);
	    		    			cimgr.addAsSeparateItemToOrder(order, deliverynewItem);
	    		    			cimgr.addItemQuantityToShippingGroup(order, deliverynewItem.getId(), ltlsg.getId(), getOverrideQuantity());
	    		    			tbsnewItem.setDeliveryItemId(deliverynewItem.getId());
    		    			}
    		    			Map extraParameters = createRepriceParameterMap();
    		    			
    		    			getPurchaseProcessHelper().runProcessRepriceOrder(PricingConstants.OP_REPRICE_ORDER_SUBTOTAL, order,
    		    					getUserPricingModels(), getUserLocale(), getProfile(), extraParameters, null);
    		    			getOrderManager().updateOrder(order);
    		    		} else {
    		    			if(existingItemInfo == null ) {
    		    				vlogDebug("Creating new itemInfo");
    		    				existingItemInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSItemInfo();
    		    				tbsItem.setTBSItemInfo(existingItemInfo);
    		    			}
    		    		}
    		    		
    		    		// Check that override price is not higher than current price		    		
    		    		// If the item is clearance, there will be a non-zero sale price that should be the ceiling for a price override
    		    		double currentListPrice = getCatalogTools().getListPrice(item.getAuxiliaryData().getProductId(), item.getCatalogRefId());	
    		    		double currentSalePrice = getCatalogTools().getSalePrice(item.getAuxiliaryData().getProductId(), item.getCatalogRefId());
    		    		boolean inCartFlag = getCatalogTools().getSkuIncartFlag(item.getCatalogRefId(), true);
    		    		
    		    		String personalizationType = (String) ((RepositoryItem) item.getAuxiliaryData().getCatalogRef()).getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE);
    		    		double priceCeiling;
    		    		
    		    		if( existingItemInfo != null && existingItemInfo.getRetailPrice() > 0.0 ) {
    		    			// Kirsch or CMO item
    		    			priceCeiling = existingItemInfo.getRetailPrice();
    		    			currentListPrice = priceCeiling;
    		    		} else if(inCartFlag) {
    		    			priceCeiling = getCatalogTools().getIncartPrice(item.getAuxiliaryData().getProductId(), item.getCatalogRefId());
    		    		} else {
    		    			priceCeiling = currentListPrice;
    		    			if( currentSalePrice > 0 && currentSalePrice != currentListPrice ) {
    		    				priceCeiling = currentSalePrice ;
    		    			}
    		    		}
    		    		
    		    		if(!BBBUtility.isEmpty(((TBSCommerceItem)item).getReferenceNumber())) {
    		    			 if (BBBCoreConstants.PERSONALIZATION_CODE_CR.equalsIgnoreCase(personalizationType)) {
    		    				 priceCeiling = ((TBSCommerceItem)item).getPersonalizePrice();
    		    			 } else if (BBBCoreConstants.PERSONALIZATION_CODE_PY.equalsIgnoreCase(personalizationType)) {
    		    				 priceCeiling = priceCeiling + ((TBSCommerceItem)item).getPersonalizePrice();
    		    			 } 
    		    		}
    		    		if (isLoggingDebug()){
    		    			logDebug("Price ceiling : " + priceCeiling);
    		    		}
    		    		
    		    		double overridePriceDbl = Double.parseDouble(this.getOverridePrice().replace(",", ""));
    		    		double minVal= 0.0;
                        List<String> minPriceOverrideVal = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.ITEM_PRICEOVERRIDEMIN);
                                     if(minPriceOverrideVal != null && !minPriceOverrideVal.isEmpty()){
                                            minVal = Double.parseDouble(minPriceOverrideVal.get(0));
                                     }
                        if( overridePriceDbl < minVal ) {
    		    			logDebug("Too low : " + getOverridePrice() + "-" + overridePriceDbl);
    		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false",getOverrideId(),NEW_PRICE+"_"+getOverrideId(),OVERRIDE_PRICE_LOW);
    		    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    		    			
    		    			if (isLoggingDebug()){
    		    				vlogDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    			}
    		    			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
    		    		}
    		    		if( overridePriceDbl >= priceCeiling ) {
    		    			vlogDebug("Too high : " + getOverridePrice() + "-" + overridePriceDbl);
    		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false",getOverrideId(),NEW_PRICE+"_"+getOverrideId(),OVERRIDE_PRICE_HIGH);
    		    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    		    			if (isLoggingDebug()){
    		    				logDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    			}
    		    			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
    		    		}

    		    		double thresholdPrice = priceCeiling - getBbbCatalogTools().getOverrideThreshold(getSite().getId(),BBBCatalogConstants.TBS_ITEM_OVERRIDE_THRESHOLD);
    		    		if (isLoggingDebug()){
    		    			logDebug("thresholdPrice="+thresholdPrice);
    		    		} 
    		    		if( overridePriceDbl <= thresholdPrice ) {
    		    			// Set the order to require approval 
    		    			if (isLoggingDebug()){
    		    				logDebug("needs approval");
    		    			}
    		    			order.setTBSApprovalRequired(true);
    		    			pricemap.put(tbsItem.getId(), Boolean.TRUE);
    		    			order.setOverridePriceMap(pricemap);
    		    		}
    		    		else if( pricemap.containsKey(tbsItem.getId()) ) {
   		    				pricemap.remove(tbsItem.getId());
       		    			if (pricemap.keySet().isEmpty()) {
       		    				order.setTBSApprovalRequired(false);
       		    			}
    		    		}
    		    	    		    		
    		    		if(newItemInfo == null){
    		    			itemInfo = existingItemInfo;   
    		    		} else {
    		    			itemInfo = newItemInfo;
    		    		}
    		    		// Set is price override flag
    		    		itemInfo.setPriceOveride(true);
    		    		// set override price
    		    		itemInfo.setOverridePrice(overridePriceDbl);
    		    		itemInfo.setOverrideQuantity(this.getOverrideQuantity());
    		    		// Set override reason 
    		    		itemInfo.setOverideReason(this.getReasonCode());
    		    		// Set competitor if selected
    		    		itemInfo.setCompetitor(this.getCompetitor());
    		    		
    		    		TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("true", getOverrideId());
    		    		priceOverrideStatusVO.setOverrideQty(getOverrideQuantity());
    		    		priceOverrideStatusVO.setOverridePrice(overridePriceDbl);
    		    		pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    		    		if (isLoggingDebug()){
    		    			logDebug("4. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    		}
    		    		rollback = false;
    		    	}else {
    		    		if (isLoggingDebug()){
    		    			logDebug("NOT TBSCommerceItem");
    		    		}
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false",getOverrideId(),"","NOT TBSCommerceItem");
		    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
		    			if (isLoggingDebug()){
		    				logDebug("5. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			}
    		    	}
    		    	
    		    	getOrderManager().updateOrder(order);
    			}
    		}
    		catch( Exception ex ) {
    			logError("Exception occured while handleItemPriceOverride",ex);
    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false",getOverrideId(),"",ex.getMessage());
    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    			if (isLoggingDebug()){
    				logDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    			}  
    		}
    		finally {
	    		try {
					td.end(rollback);
				} catch (TransactionDemarcationException tde) {
					vlogError("TransactionDemarcationException "+tde);
				}
	    	}
    	}    	
    	return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);    				
    }

	@SuppressWarnings({ "unchecked", "unused" })
	private void createLtlDeliveryItem(CommerceItem pItem) {
		TransactionDemarcation td = new TransactionDemarcation();
		boolean rollback = true;
		TBSOrder order = (TBSOrder)getOrder();
		TBSCommerceItem tbsItem = null;
		try {
			synchronized (order) {
				if(pItem instanceof TBSCommerceItem){
					tbsItem = (TBSCommerceItem) pItem;
				}
				td.begin(getTransactionManager());
				setAddItemCount(1);
				getItems()[0].setCatalogRefId(tbsItem.getCatalogRefId());
				getItems()[0].setProductId(tbsItem.getAuxiliaryData().getProductId());
				getItems()[0].setQuantity(getOverrideQuantity());
				if(!StringUtils.isBlank(tbsItem.getRegistryId())){
					getItems()[0].getValue().put("registryId", tbsItem.getRegistryId());
				}
				if(!StringUtils.isBlank(tbsItem.getStoreId())){
					getItems()[0].getValue().put("storeId", tbsItem.getStoreId());
				}
				getItems()[0].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY,"false");
				getItems()[0].getValue().put("bts", tbsItem.getBts());
				getItems()[0].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD, tbsItem.getLtlShipMethod());
				if (tbsItem.getPriceInfo().isOnSale()) {
					tbsItem.setPrevPrice(tbsItem.getPriceInfo().getSalePrice());
				} else {
					tbsItem.setPrevPrice(tbsItem.getPriceInfo().getListPrice());
				}
				//getLtlItemsMap();
				BBBShippingGroupCommerceItemRelationship shipRelationShip = (BBBShippingGroupCommerceItemRelationship) tbsItem.getShippingGroupRelationships().get(0);

				ShippingGroup shippingGroup = shipRelationShip.getShippingGroup();
						
				String deliveryCommId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLDeliveryChargeSku(order, shippingGroup,order.getSiteId(), pItem);
				logInfo("delivery commerce Id: " + deliveryCommId);
				((BBBCommerceItem)pItem).setDeliveryItemId(deliveryCommId);
                
				runProcessRepriceOrder(getAddItemToOrderPricingOp(), order, getUserPricingModels(), getUserLocale(), getProfile(), createRepriceParameterMap());
				getOrderManager().updateOrder(order);
			}
			rollback = false;
			} catch (RunProcessException e) {
				vlogError("RunProcessException occurred :: "+e);
			} catch (BBBSystemException e) {
				vlogError("BBBSystemException occurred :: "+e);
			} catch (BBBBusinessException e) {
				vlogError("BBBBusinessException occurred :: "+e);
			} catch (RepositoryException e) {
				vlogError("RepositoryException occurred :: "+e);
			} catch (CommerceException e) {
				vlogError("CommerceException occurred :: "+e);
			} catch (TransactionDemarcationException e) {
				vlogError("TransactionDemarcationException occurred :: "+e);
			}
			finally {
	    		try {
					td.end(rollback);
				} catch (TransactionDemarcationException tde) {
					vlogError("TransactionDemarcationException "+tde);
				}
	    	}
	}    

	/*
	 * Validate the price override form and return the status vo for creating the json object
	 */
	private boolean validateOverrideForm(DynamoHttpServletRequest pRequest){
		boolean valid = true;
		if (isLoggingDebug()){
			logDebug ("ReasonCode=="+this.getReasonCode());
			logDebug ("overridePrice=="+this.getOverridePrice());
		}
		TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("true", getOverrideId());
		List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = null;
		if (StringUtils.isEmpty(this.getReasonCode())){
			valid = false;
			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO(REASON_LIST+"_"+getOverrideId(),REASON_CODE_EMPTY);
			if (errorMessageVOList == null) errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
			errorMessageVOList.add(errorMessageVO);
			priceOverrideStatusVO.setSuccess("false");
			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
			if (isLoggingDebug()){
				logDebug("1. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
			}
		}
		if (StringUtils.isEmpty(this.getOverridePrice())){
			valid=false;
			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO(NEW_PRICE+"_"+getOverrideId(),OVERRIDE_PRICE_EMPTY);
			if (errorMessageVOList == null) errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
			errorMessageVOList.add(errorMessageVO);
			priceOverrideStatusVO.setSuccess("false");
			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
			if (isLoggingDebug()){
				logDebug("2. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
			}
		}
		pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
		return valid;
	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public final boolean handleDeliveryChargeOverride(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		pRequest.getSession().removeAttribute("priceOverrideStatusVO");
		setOverrideId(pRequest.getParameter("commerceItemId"));
		setOverridePrice(pRequest.getParameter("overridePrice"));
		setOverrideQuantity(Integer.valueOf(pRequest.getParameter("overrideQuantity")));
		setReasonCode(pRequest.getParameter("reasonCode"));
		setCompetitor(pRequest.getParameter("competitor"));
		StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
	   if (StringUtils.isNotEmpty(getFromPage())) {
		   successURL
			.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			setOverrideSuccessURL(successURL.toString());
			setOverrideErrorURL(errorURL.toString());
		}
	
		//Validate Form for Empty Fields
		if (!validateOverrideForm(pRequest)){			
			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
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
		
    			TBSOrder order = (TBSOrder)this.getOrder();
    			synchronized (order) {
    				
    		    	Map<String, Boolean> pricemap = order.getOverridePriceMap();
    		    	
    		    	CommerceItem item = order.getCommerceItem(getOverrideId());

    		    	TBSItemInfo itemInfo;
    		    	if( item instanceof LTLDeliveryChargeCommerceItem ) {
    		    		LTLDeliveryChargeCommerceItem tbsItem = (LTLDeliveryChargeCommerceItem)item;
    		    		itemInfo = tbsItem.getTBSItemInfo();
    		    		
    		    		if( itemInfo == null ) {
    		    			if( isLoggingDebug() ) {
    		    				logDebug("Creating new itemInfo");
    		    			} itemInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSItemInfo();
    		    			tbsItem.setTBSItemInfo(itemInfo);
    		    		}
    		    		
    		    		// Check that override price is not higher than current price
    		    		double currentListPrice = item.getPriceInfo().getListPrice();	
    		    		double overridePriceDbl = Double.parseDouble(this.getOverridePrice());
    		    		double minVal= 0.0;
    		    		List<String> minPriceOverrideVal = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.ITEM_PRICEOVERRIDEMIN);
						if(minPriceOverrideVal != null && !minPriceOverrideVal.isEmpty()){
							minVal = Double.parseDouble(minPriceOverrideVal.get(0));
						}
    		    		if(  overridePriceDbl < minVal) {
    		    			logDebug("Too low : " + getOverridePrice() + "-" + overridePriceDbl);
    		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false",getOverrideId(),NEW_PRICE+"_"+getOverrideId(),OVERRIDE_PRICE_LOW);
    		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
    		    					priceOverrideStatusVO);
    		    			if (isLoggingDebug()){
    		    				vlogDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    			}
    		    			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
    		    		}
    		    		if( overridePriceDbl >= currentListPrice ) {
    		    			if (isLoggingDebug()){
    		    				logDebug("Too high : " + getOverridePrice() + "-" + currentListPrice);
    		    			}
    		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false",getOverrideId(),NEW_PRICE+"_"+getOverrideId(),OVERRIDE_PRICE_HIGH);
    		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
    		    					priceOverrideStatusVO);
    		    			if (isLoggingDebug()){
    		    				logDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    			}
    		    			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
    		    		}

    		    		double thresholdPrice = currentListPrice - getBbbCatalogTools().getOverrideThreshold(getSite().getId(),BBBCatalogConstants.TBS_DELIVERY_OVERRIDE_THRESHOLD);
    		    		if( overridePriceDbl <= thresholdPrice ) {
    		    			// Set the order to require approval 
    		    			if (isLoggingDebug()){
    		    				logDebug("needs approval");
    		    			}
    		    			order.setTBSApprovalRequired(true);
    		    			pricemap.put(tbsItem.getId(), Boolean.TRUE);
    		    			order.setOverridePriceMap(pricemap);
    		    		}
    		    		else if( pricemap.containsKey(tbsItem.getId()) ) {
   		    				pricemap.remove(tbsItem.getId());
       		    			if (pricemap.keySet().isEmpty()) {
       		    				order.setTBSApprovalRequired(false);
       		    			}
    		    		}
   		    	    		    		
    		    		// Set is price override flag
    		    		itemInfo.setPriceOveride(true);
    		    		// set override price
    		    		itemInfo.setOverridePrice(overridePriceDbl);
    		    		itemInfo.setOverrideQuantity(this.getOverrideQuantity());
    		    		// Set override reason 
    		    		itemInfo.setOverideReason(this.getReasonCode());
    		    		// Set competitor if selected
    		    		itemInfo.setCompetitor(this.getCompetitor());
    		    		
    		    		TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("true", getOverrideId());
    		    		priceOverrideStatusVO.setOverrideQty(getOverrideQuantity());
    		    		priceOverrideStatusVO.setOverridePrice(overridePriceDbl);
    		    		pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    		    		if (isLoggingDebug()){
    		    			logDebug("4. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    		}
    		    		
    		    	}else {
    		    		if (isLoggingDebug()){
    		    			logDebug("NOT LTLDeliveryChargeCommerceItem");
    		    		}
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false",getOverrideId(),"","NOT LTLDeliveryChargeCommerceItem");
		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
		    					priceOverrideStatusVO);
		    			if (isLoggingDebug()){
		    				logDebug("5. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			}
    		    	}
    			}
    		}
    		catch( Exception ex ) {
    			logError("An exception occured while handleDeliveryChargeOverride", ex);
    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false",getOverrideId(),"",ex.getMessage());
    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
    					priceOverrideStatusVO);
    			
    			if (isLoggingDebug()){
    				logDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    			}  
    		}
    	}    	
    	return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);   				
    }    
	
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public final boolean handleAssemblyFeeOverride(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		pRequest.getSession().removeAttribute("priceOverrideStatusVO");
		
		setOverrideId(pRequest.getParameter("commerceItemId"));
		setOverridePrice(pRequest.getParameter("overridePrice"));
		setOverrideQuantity(Integer.valueOf(pRequest.getParameter("overrideQuantity")));
		setReasonCode(pRequest.getParameter("reasonCode"));
		setCompetitor(pRequest.getParameter("competitor"));
		StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
	   if (StringUtils.isNotEmpty(getFromPage())) {
		   successURL
			.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			setOverrideSuccessURL(successURL.toString());
			setOverrideErrorURL(errorURL.toString());
		}
		
		//Validate Form for Empty Fields
		if (!validateOverrideForm(pRequest)){			
			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
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
		
    			TBSOrder order = (TBSOrder)this.getOrder();
    			synchronized (order) {
    				
    		    	Map<String, Boolean> pricemap = order.getOverridePriceMap();
    		    	CommerceItem item = order.getCommerceItem(getOverrideId());

    		    	TBSItemInfo itemInfo;
    		    	if( item instanceof LTLAssemblyFeeCommerceItem ) {
    		    		LTLAssemblyFeeCommerceItem tbsItem = (LTLAssemblyFeeCommerceItem)item;
    		    		itemInfo = tbsItem.getTBSItemInfo();
    		    		
    		    		if( itemInfo == null ) {
    		    			if( isLoggingDebug() ) {
    		    				logDebug("Creating new itemInfo");
    		    			} itemInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSItemInfo();
    		    			tbsItem.setTBSItemInfo(itemInfo);
    		    		}
    		    		
    		    		// Check that override price is not higher than current price
    		    		double currentListPrice = item.getPriceInfo().getListPrice();	
    		    		double overridePriceDbl = Double.parseDouble(this.getOverridePrice());
    		    		double minVal= 0.0;
    		    		List<String> minPriceOverrideVal = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.ITEM_PRICEOVERRIDEMIN);
						if(minPriceOverrideVal != null && !minPriceOverrideVal.isEmpty()){
							minVal = Double.parseDouble(minPriceOverrideVal.get(0));
						}
    		    		if(  overridePriceDbl < minVal) {
    		    			logDebug("Too low : " + getOverridePrice() + "-" + overridePriceDbl);
    		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(),NEW_PRICE+"_"+item.getId(),OVERRIDE_PRICE_LOW);
    		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
    		    					priceOverrideStatusVO);
    		    			if (isLoggingDebug()){
    		    				vlogDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    			}
    		    			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
    		    		}
    		    		if( overridePriceDbl >= currentListPrice ) {
    		    			if (isLoggingDebug()){
    		    				logDebug("Too high : " + getOverridePrice() + "-" + currentListPrice);
    		    			}
    		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(),NEW_PRICE+"_"+item.getId(),OVERRIDE_PRICE_HIGH);
    		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
    		    					priceOverrideStatusVO);
    		    			if (isLoggingDebug()){
    		    				logDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    			}
    		    			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
    		    		}

    		    		double thresholdPrice = currentListPrice - getBbbCatalogTools().getOverrideThreshold(getSite().getId(),BBBCatalogConstants.TBS_ASSEMBLY_OVERRIDE_THRESHOLD);
    		    		if( overridePriceDbl <= thresholdPrice ) {
    		    			// Set the order to require approval 
    		    			if (isLoggingDebug()){
    		    				logDebug("needs approval");
    		    			}
    		    			order.setTBSApprovalRequired(true);
    		    			pricemap.put(tbsItem.getId(), Boolean.TRUE);
    		    			order.setOverridePriceMap(pricemap);
    		    		}
     		    		else if( pricemap.containsKey(tbsItem.getId()) ) {
   		    				pricemap.remove(tbsItem.getId());
       		    			if (pricemap.keySet().isEmpty()) {
       		    				order.setTBSApprovalRequired(false);
       		    			}
     		    		}
    		    	    		    		
    		    		// Set is price override flag
    		    		itemInfo.setPriceOveride(true);
    		    		// set override price
    		    		itemInfo.setOverridePrice(overridePriceDbl);
    		    		itemInfo.setOverrideQuantity(this.getOverrideQuantity());
    		    		// Set override reason 
    		    		itemInfo.setOverideReason(this.getReasonCode());
    		    		// Set competitor if selected
    		    		itemInfo.setCompetitor(this.getCompetitor());
    		    		
    		    		TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("true", getOverrideId());
    		    		priceOverrideStatusVO.setOverrideQty(getOverrideQuantity());
    		    		priceOverrideStatusVO.setOverridePrice(overridePriceDbl);
    		    		pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    		    		if (isLoggingDebug()){
    		    			logDebug("4. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		    		}
    		    		
    		    	}else {
    		    		if (isLoggingDebug()){
    		    			logDebug("NOT LTLAssemblyFeeCommerceItem");
    		    		}
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(),"","NOT LTLAssemblyFeeCommerceItem");
		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
		    					priceOverrideStatusVO);
		    			if (isLoggingDebug()){
		    				logDebug("5. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			}
    		    	}
    			}
    		}
    		catch( Exception ex ) {
    			logError("Exception occurred while handleAssemblyFeeOverride", ex);
    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(),"",ex.getMessage());
    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
    					priceOverrideStatusVO);
    			if (isLoggingDebug()){
    				logDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    			}  
    		}
    	}   
    	
    	return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);  
    	
    }
	
	/*   handleGiftWrapOverride --  Start */
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */	
	public final boolean handleGiftWrapOverride(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		// Implementation using Commerce Item id -- Start
		pRequest.getSession().removeAttribute("priceOverrideStatusVO");
				
    	final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
    	final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    	StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
		StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
	   if (StringUtils.isNotEmpty(getFromPage())) {
		   successURL
			.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			setOverrideSuccessURL(successURL.toString());
			setOverrideErrorURL(errorURL.toString());
		}
	
    	if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
    		BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
    		Transaction tr = null;
    		tr = this.ensureTransaction();
			if (this.getUserLocale() == null) {
				this.setUserLocale(this.getUserLocale(pRequest, pResponse));
			}

			TBSOrder order = (TBSOrder)this.getOrder();
			List<ShippingGroup> shipGroups = order.getShippingGroups();
			
			BBBHardGoodShippingGroup hardGoodShip = null;
			for (ShippingGroup shippingGroup : shipGroups) {
				if(shippingGroup instanceof BBBHardGoodShippingGroup){
					hardGoodShip = (BBBHardGoodShippingGroup) shippingGroup;    					
					BBBHardGoodShippingGroup shipGroup;
					try {
						shipGroup = (BBBHardGoodShippingGroup)order.getShippingGroup(getOverrideId());
						if(shipGroup.getId()!=null && (shipGroup.getId()).equalsIgnoreCase(hardGoodShip.getId())){
							List<CommerceItemRelationship> commerceItemRelationships = shipGroup.getCommerceItemRelationships();
							for (CommerceItemRelationship commerceItemRel : commerceItemRelationships) {
								CommerceItem item = commerceItemRel.getCommerceItem();			
								synchronized (order) {
									TBSItemInfo itemInfo;
									if( item instanceof GiftWrapCommerceItem ) {
										GiftWrapCommerceItem tbsItem = (GiftWrapCommerceItem)item;
										itemInfo = tbsItem.getTBSItemInfo();
										
										if( itemInfo == null ) {
											if( isLoggingDebug() ) {
												logDebug("Creating new itemInfo");
											} itemInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSItemInfo();
											tbsItem.setTBSItemInfo(itemInfo);
										}
										
										// Check that override price is not higher than current price
										double currentListPrice = item.getPriceInfo().getListPrice();	
										double overridePriceDbl = Double.parseDouble(this.getOverridePrice());
				    		    		if( overridePriceDbl < 0.0 ) {
				    		    			logDebug("Too low : " + getOverridePrice() + "-" + overridePriceDbl);
				    		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(),NEW_PRICE+"_"+getOverrideId(),OVERRIDE_PRICE_LOW);
				    		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
				    		    					priceOverrideStatusVO);
				    		    			if (isLoggingDebug()){
				    		    				vlogDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
				    		    			}
				    		    			return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
				    		    		}
										if( overridePriceDbl >= currentListPrice ) {
											if (isLoggingDebug()){
												logDebug("Too high : " + getOverridePrice() + "-" + currentListPrice);
											}
											TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(),NEW_PRICE+"_"+item.getId(),OVERRIDE_PRICE_HIGH);
											pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
							    					priceOverrideStatusVO);
											if (isLoggingDebug()){
												logDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
											}
											return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);
										}
										
										double thresholdPrice = getBbbCatalogTools().getOverrideThreshold(getSite().getId(),BBBCatalogConstants.TBS_GIFTWRAP_OVERRIDE_THRESHOLD);
										Map<String, Boolean> pricemap = order.getOverridePriceMap();
                                        if (isLoggingDebug()){
                                               logDebug("gift wrap thresholdPrice="+thresholdPrice);
                                        } 
                                        if( currentListPrice >= thresholdPrice ) {
                                               // Set the order to require approval 
                                               if (isLoggingDebug()){
                                                     logDebug("needs approval");
                                               }
                                               order.setTBSApprovalRequired(true);
                                               pricemap.put(tbsItem.getId(), Boolean.TRUE);
                                               order.setOverridePriceMap(pricemap);
                                        }
                                        else if( pricemap.containsKey(tbsItem.getId()) ) {
                                                      pricemap.remove(tbsItem.getId());
                                               if (pricemap.keySet().isEmpty()) {
                                                      order.setTBSApprovalRequired(false);
                                               }
                                        }

										// Set is price override flag
										itemInfo.setPriceOveride(true);
				    		    		itemInfo.setOverrideQuantity(1);
										// set override price
										itemInfo.setOverridePrice(overridePriceDbl);
										// Set override reason 
										itemInfo.setOverideReason(this.getReasonCode());
										
										TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("true", getOverrideId());
										priceOverrideStatusVO.setOverridePrice(overridePriceDbl);
										pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
										if (isLoggingDebug()){
											logDebug("4. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
										}
										break;
									}
								}
							}
						}
					} catch (ShippingGroupNotFoundException e) {
						logError("Invalid Parammeter Excpetion occured while handleGiftWrapOverride", e);
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(), "",e.getMessage());
		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
		    					priceOverrideStatusVO);
		    			if (isLoggingDebug()){
		    				logDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			}
					} catch (InvalidParameterException e) {
						logError("Invalid Parammeter Excpetion occured while handleGiftWrapOverride", e);
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(), "",e.getMessage());
		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
		    					priceOverrideStatusVO);
		    			if (isLoggingDebug()){
		    				logDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			}
					} catch (CommerceException e) {
						logError("Commerce Excpetion occured while handleGiftWrapOverride", e);
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(), "",e.getMessage());
		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
		    					priceOverrideStatusVO);
		    			if (isLoggingDebug()){
		    				logDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			}
		    		} catch (Exception e) {
						logError("Exception occured while handleGiftWrapOverride", e);
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO=retErrorPriceOverrideStatusVO("false", getOverrideId(), "",e.getMessage());
		    			pRequest.getSession().setAttribute(TBSConstants.PRICE_OVERRIDE_STATUS_VO,
		    					priceOverrideStatusVO);
		    			if (isLoggingDebug()){
		    				logDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			}
		    		}
					
				}
			}
    	}    	
    	return checkFormRedirect(getOverrideSuccessURL(), getOverrideErrorURL(), pRequest, pResponse);  
    	
    }
	/*   handleGiftWrapOverride --  End */

	/** Override the preAddItemToOrder method to check. If jsEnabled validateSkuInfo validateRegistryInfo
     * validateStoreInfo checkInventory
     *
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
    @Override
    public void preAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        try {
            if (null != getJsonResultString()) {
                addItemJSONObjectParser(getJsonResultString());
            }
        } catch (BBBBusinessException e) {
            logError(BBBCoreErrorConstants.CART_ERROR_1013 + ": BBBBusinessException", e);
        }
        //HYD-708 null pointer check fix start
        vlogDebug((new StringBuilder()).append("preAddItemToOrder : Total Number of items : ").append((getItems()!=null)?(getItems().length):" item is null ").toString());

        if(getItems() == null && ((StringUtils.isEmpty(getWishlistItemId()) || !isRestService()))){
    		this.getFormExceptions().addElement(this.createDropletException(NULL_SKU_ID, NULL_SKU_ID));
    		return;
    	}else if ((getItems() == null)
                        || ((getItems().length == 0) && !StringUtils.isEmpty(getWishlistItemId()) && (getJsonResultString() == null))) {
            setAddItemCount(1);
            populateAddItemInfo(null, getItems());
        }
        
       //HYD-708 null pointer check fix end
        for (int i = 0; i < getItems().length; i++) {
            AddCommerceItemInfo bbbItemInfo = getItems()[i];
            setQtyAdded(getQtyAdded() + bbbItemInfo.getQuantity());

            validateSkuInfo(bbbItemInfo.getCatalogRefId(), getSiteId(), pRequest, pResponse);

            if (!getFormError() && (bbbItemInfo.getValue() != null)) {
            	if((pRequest.getRequestURI().contains("add_item_to_cart_pdp.jsp")) && !StringUtils.isEmpty((String) bbbItemInfo.getValue().get(REGISTRY_ID))){
            		bbbItemInfo.getValue().remove(REGISTRY_ID);
            	}
                if (!isFromPipelineFlag() && !StringUtils.isEmpty((String) bbbItemInfo.getValue().get(REGISTRY_ID))) {
                    validateRegistryInfo((String) bbbItemInfo.getValue().get(REGISTRY_ID), getSiteId(), pRequest, pResponse);
                }
                if (!StringUtils.isEmpty((String) bbbItemInfo.getValue().get(STORE_ID))) {
                	validateStoreInfoByStoreId((String) bbbItemInfo.getValue().get(STORE_ID), pRequest, pResponse);
                }
                if (!isFromPipelineFlag() && !getFormError()) {
                    validateInventory(getSiteId(), this.getItems()[i], getOrder(), pRequest, pResponse);
                }
            }
        }
    }
    
    /** Validates if the input storeId is valid or not
     * @param storeId Store ID
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse*/
    public final void validateStoreInfoByStoreId(final String storeId, final DynamoHttpServletRequest pRequest,
                    final DynamoHttpServletResponse pResponse) {
        StoreDetails storeVO = null;
        RepositoryItem lStoreItem = null;
        this.logDebug((new StringBuilder()).append("validateStoreInfoByStoreId: storeId : ").append(storeId).toString());
        if (!StringUtils.isBlank(storeId)) {
			try {
				lStoreItem = getStoreRepository().getItem(storeId, TBSConstants.STORE);
			} catch (RepositoryException e) {
				vlogError("Error Querying the StoreRepository", e.getMessage());
			}
			if(lStoreItem!=null){
        	storeVO = mSearchStoreManager.convertStoreItemToStore(lStoreItem, null, null);
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
    
    /**
     * This method is used to create the new item, while increasing the qty of the override item
     * @param pRequest
     * @param pResponse
     * @return
     */
    protected void modifyOrderByCommerceId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    				throws ServletException, IOException, CommerceException, RunProcessException {
    	
    	boolean updateflag = updateOverrideItem(pRequest, pResponse);
    	updateAutoWaiveDetailsFromCommerceItem(this.getOrder());
        if(updateflag){
        	super.modifyOrderByCommerceId(pRequest, pResponse);
        }
        ((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(getOrder(),null);
        
    }

    /**
     * This method is used to create the new item, while increasing the qty of the override item
     * @param pRequest
     * @param pResponse
     * @return
     * @throws CommerceException 
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean updateOverrideItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws CommerceException {
		boolean updateflag = true;
    	List<CommerceItem> cItems = this.getOrder().getCommerceItems();
        if (cItems != null) {
        	TBSCommerceItem tbsItem = null;
            for (CommerceItem cItem : cItems) {
            	String inputQuantity = pRequest.getParameter(cItem.getId());
                if (cItem instanceof TBSCommerceItem) {
                	tbsItem = (TBSCommerceItem) cItem;
                	if(inputQuantity != null && Long.parseLong(inputQuantity)  > cItem.getQuantity() && tbsItem.getTBSItemInfo() != null
                			&& tbsItem.getTBSItemInfo().getOverridePrice() > 0){
                		updateflag = false;
                		boolean isSkuLtl= false;
                		try {
                			isSkuLtl=getCatalogUtil().isSkuLtl(getSiteId(), cItem.getCatalogRefId());
                		} catch (final NumberFormatException e) {
                            this.addFormException(this.createDropletException(INVALID_QUANTITY_FORMAT, INVALID_QUANTITY_FORMAT));
                        } catch (BBBSystemException e) {
                        	this.logError("System exception while checking is sku LTL for skuID: "+cItem.getCatalogRefId(), e);
						} catch (BBBBusinessException e) {
							this.logError("System exception while checking is sku LTL for skuID: "+cItem.getCatalogRefId(), e);
                        }
        	    		try {
        	    			long newItemQty = Long.parseLong(inputQuantity) - cItem.getQuantity();
        	    			setQuantity(newItemQty);
        	    			setAddItemCount(1);
        	    			AddCommerceItemInfo input = getItems()[0];
        	    			input.setCatalogRefId(cItem.getCatalogRefId());
        	    			input.setProductId(cItem.getAuxiliaryData().getProductId());
        	    			input.setQuantity(newItemQty);
                            
        	    			Dictionary valueDis = input.getValue();
        	    			if(tbsItem.getBts()){
        	    				valueDis.put("bts", "true");
        	    			} else {
        	    				valueDis.put("bts", "false");
        	    			}
        	    			if(!StringUtils.isBlank(tbsItem.getRegistryId())){
        	    				valueDis.put(BBBCoreConstants.REGISTRY_ID, tbsItem.getRegistryId());
        	    			}
        	    			if(!StringUtils.isBlank(tbsItem.getStoreId())){
        	    				valueDis.put(BBBCoreConstants.STOREID, tbsItem.getStoreId());
        	    			}
        	    			if(isSkuLtl){                         
        	    				valueDis.put(BBBCatalogConstants.LTL_SHIP_METHOD,"");
        	                }
        	    			if(!StringUtils.isBlank(tbsItem.getReferenceNumber())) {
        	    			    valueDis.put("referenceNumber", tbsItem.getReferenceNumber());
        	    			}
        					super.addItemToOrder(pRequest, pResponse);
        					getOrderManager().updateOrder(getOrder());
        				} catch (ServletException e) {
        					vlogError("ServletException occurred :: "+e);
        				} catch (IOException e) {
        					vlogError("IOException occurred :: "+e);
        				} 
	                } 
                	if(inputQuantity != null && Long.parseLong(inputQuantity)  < cItem.getQuantity() && tbsItem.getTBSItemInfo() != null){
                		tbsItem.getTBSItemInfo().setOverrideQuantity(Integer.parseInt(inputQuantity));
                	}
	            }
            } 
        }
		return updateflag;
	}
	
	/**
	 * This method is overridden to add the registryId while doing multiple items to order from UPC results page.
	 */
	@SuppressWarnings("unchecked")
	protected boolean mergeItemInputForAdd(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, 
			String pCommerceItemType, boolean pUseProductIds) throws ServletException, IOException {
	 boolean returnType = super.mergeItemInputForAdd(pRequest, pResponse, pCommerceItemType, pUseProductIds);
	 if (returnType && !getFormError()) {
		 String registryId = pRequest.getParameter("upcregistryId");
		if(!StringUtils.isBlank(registryId)){
			validateRegistryInfo(registryId, getSiteId(), pRequest, pResponse);
			if (getItems() != null) {
				for (AddCommerceItemInfo itemInfo : getItems()) {
					itemInfo.getValue().put(BBBCoreConstants.REGISTRY_ID, registryId);
				}
			}
		}
	 }
	 return returnType;
	}
	
    /** @param siteId Site ID
     * @param itemInfo Item Information
     * @param order Order
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse */
	@SuppressWarnings("unchecked")
	@Override
    public void validateInventory(final String siteId, final AddCommerceItemInfo itemInfo, final Order order,
                    final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
        int inventoryStatus = BBBInventoryManager.AVAILABLE;
        String lShiptime = null;
        SKUDetailVO skuVO = null;
        final BBBPurchaseProcessHelper bbbHelper = (BBBPurchaseProcessHelper) this.getPurchaseProcessHelper();
        long rolledUpQty = 0;
        final BBBOrderImpl bbOrder = (BBBOrderImpl) this.getOrder();
        // skip the inventory check for MIE search items while adding to cart
        if(itemInfo.getValue() != null && itemInfo.getValue().get("pagetype") != null && itemInfo.getValue().get("pagetype").equals("MIE")){
        	return;
        }
        try {
            rolledUpQty = bbbHelper.getRollupQuantities((String) itemInfo.getValue().get(STORE_ID),
                            itemInfo.getCatalogRefId(), bbOrder, itemInfo.getQuantity(), null);

            if ((itemInfo.getValue() != null) && !BBBUtility.isEmpty((String) itemInfo.getValue().get(REGISTRY_ID))) {
                // Item is added from Gift Registry
                inventoryStatus = bbbHelper.checkCachedInventory(this.getSiteId(), itemInfo.getCatalogRefId(),
                                (String) itemInfo.getValue().get(STORE_ID), this.getOrder(), 0,
                                BBBInventoryManager.ADD_ITEM_FROM_REG, this.getStoreInventoryContainer(),
                                BBBInventoryManager.AVAILABLE);
            } else {
                inventoryStatus = bbbHelper.checkCachedInventory(this.getSiteId(), itemInfo.getCatalogRefId(),
                                (String) itemInfo.getValue().get(STORE_ID), this.getOrder(), rolledUpQty,
                                BBBInventoryManager.ADD_ITEM, this.getStoreInventoryContainer(),
                                BBBInventoryManager.AVAILABLE);
            }  
            
            skuVO = getCatalogTools().getSKUDetails(siteId, itemInfo.getCatalogRefId());
        } catch (final CommerceException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_INVENTORY_CHECK,
                            BBBCoreErrorConstants.CART_ERROR_1003), e);
            this.logDebug("Error occoured while inventory check, Making inventory Status as available.");
            inventoryStatus = BBBInventoryManager.AVAILABLE;
        } catch (BBBSystemException e) {
			vlogError("SKU details VO not found :: "+e);
		} catch (BBBBusinessException e) {
			vlogError("SKU details VO not found :: "+e);
		} 
        
     // If Inventory not found, check for the availability in Special Departments
		if(skuVO != null && !skuVO.isVdcSku() && !skuVO.isLtlItem()){
			
			if(!BBBUtility.isEmpty(pRequest.getParameter(TBSConstants.TIME_FRAME))) {
				lShiptime = pRequest.getParameter(TBSConstants.TIME_FRAME);
			} else {
			lShiptime = getSearchStoreManager().getShipTime(itemInfo.getCatalogRefId(), itemInfo.getQuantity(), siteId, (String)ServletUtil.getCurrentRequest().getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER));
			}
			if(!BBBUtility.isEmpty(lShiptime)) {
			itemInfo.getValue().put(TBSConstants.SHIP_TIME, lShiptime);
			}
			if(inventoryStatus == BBBInventoryManager.NOT_AVAILABLE){
				inventoryStatus = BBBInventoryManager.AVAILABLE;
				if(!StringUtils.isBlank(lShiptime) && lShiptime == "0004"){
					inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
				}
			}
		}
		
        if (inventoryStatus != BBBInventoryManager.AVAILABLE) {
            vlogError(LogMessageFormatter.formatMessage(pRequest, OUT_OF_STOCK,
                            BBBCoreErrorConstants.CART_ERROR_1009));
            this.getFormExceptions().addElement(this.createDropletException(OUT_OF_STOCK_ITEM, OUT_OF_STOCK_ITEM));
        }
    }
	/**
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}
	/**
	 * @param pSearchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}
	
	/**
	 * This is used to check payAtRegister in the order, if its there then on success of paypal removing the payAtRegister.
	 * @param pRequest
	 * @param pResponse
	 */
    @SuppressWarnings("unchecked")
	public void postCheckoutWithPaypal(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    	vlogDebug("TBSCartFormHandler :: postCheckoutWithPaypal () :: START");
		BBBOrderImpl order = (BBBOrderImpl) getOrder();
		try {
			List<PaymentGroup> payGroups = order.getPaymentGroups();
			for (PaymentGroup payment : payGroups) {
				if(payment instanceof PayAtRegister){
					getOrderManager().getPaymentGroupManager().removePaymentGroupFromOrder(order, payment.getId());
					getOrderManager().updateOrder(order);
					break;
				}
			}
		} catch (CommerceException e) {
			vlogError("CommerceException occurred while removing the PayAtRegister from Order : "+e );
			addFormException(new DropletException("There is an issue occurred while removing the PayAtRegister from Order"));
		}
		vlogDebug("TBSCartFormHandler :: postCheckoutWithPaypal () :: END");
	}
    
    @Override
    /**
     * This method is overridden to get the AutoWaive shipping details
     */
    public void postAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	
    	((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(getOrder(),null);
    	super.postAddItemToOrder(pRequest, pResponse);
    	
    }
    
    /**
     * This method is overridden to get the AutoWaive shipping details
     */
  /*  public void postSetOrderByCommerceId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	// For updated qty, remove saved values from CI
    	try {
    		TBSCommerceItem item = (TBSCommerceItem)getOrder().getCommerceItem(getCommerceItemId());
    		item.setAutoWaiveClassification("");
    		item.setStoreOnhandQty(0);
    		item.setAutoWaiveFlag(false);
    	}
    	catch( Exception e ) {
    		e.printStackTrace();
    	}
    	((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(getOrder(),getCommerceItemId());
    	super.postSetOrderByCommerceId(pRequest, pResponse);
    }*/
    /**
	 * This method is used to remove the AutoWaive details from the commerce items whose quantity is updated
	 * @param pOrder
	 */
    @SuppressWarnings("unchecked")
	public void updateAutoWaiveDetailsFromCommerceItem(Order pOrder) {

    	List<CommerceItem> cItems = this.getOrder().getCommerceItems();
        if (cItems != null) {
        	TBSCommerceItem tbsItem = null;
        	DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
            for (CommerceItem cItem : cItems) {
            	String inputQuantity = request.getParameter(cItem.getId());
                if (cItem instanceof TBSCommerceItem) {
                	tbsItem = (TBSCommerceItem) cItem;
                	if(inputQuantity != null && Long.parseLong(inputQuantity) != cItem.getQuantity()){
                		tbsItem.setAutoWaiveFlag(false);
        				tbsItem.setAutoWaiveClassification(null);
                	}
                }
            }
        }
	}
    
	
	/**
	 * Ret error price override status vo.
	 *
	 * @param pOverrideStatus the override status
	 * @param pOverrideId the override id
	 * @param pOverrideErrorMessage the override error message
	 * @param pErrorMessage the error message
	 * @return the TBS price override status vo
	 */
	private TBSPriceOverrideStatusVO retErrorPriceOverrideStatusVO(String pOverrideStatus,String pOverrideId,String pOverrideErrorMessage,String pErrorMessage) {
		if(isLoggingDebug()){
			logDebug(new StringBuilder("Inside retPriceOverrideStatusVO in TBSCartFormHandler with override id : ").append(pOverrideId).append(" Error Message : ").append(pErrorMessage).toString());
		}
		TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO(
				pOverrideStatus,pOverrideId);
		TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO(
				pOverrideErrorMessage, pErrorMessage);
		List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
		errorMessageVOList.add(errorMessageVO);
		priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
		if(isLoggingDebug()){
			logDebug("Exiting retErrorPriceOverrideStatusVO in TBSCartFormHandler");
		}
	    return priceOverrideStatusVO;	
	}
	  @Override
	  public final boolean handleAddMultipleItemsToOrder(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		  StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
			StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
			if (StringUtils.isNotEmpty(getFromPage())) {
				
				StringBuffer appendData = new StringBuffer("");
				if(StringUtils.isNotEmpty(getErrorQueryParam())){
					appendData.append(getErrorQueryParam());
					successURL.append(pRequest.getContextPath())
					.append(getSuccessUrlMap().get(getFromPage()));
					
					errorURL.append(pRequest.getContextPath())
					.append(getErrorUrlMap().get(getFromPage())).append(appendData);;
				}
				if(StringUtils.isNotEmpty(getQueryParam())){
					appendData.append(BBBCoreConstants.QUESTION_MARK).append(getQueryParam());
					
					successURL
							.append(pRequest.getContextPath())
							.append(getSuccessUrlMap().get(getFromPage()))
							.append(appendData);
					errorURL.append(pRequest.getContextPath())
							.append(getErrorUrlMap().get(getFromPage()))
							.append(appendData);
				}
		
				setAddMultipleItemsToOrderSuccessURL(successURL.toString());
			setAddMultipleItemsToOrderErrorURL(errorURL.toString());
			
		}
    return super.handleAddMultipleItemsToOrder(pRequest, pResponse);
}
	 
    


	public String getErrorQueryParam() {
		return errorQueryParam;
	}

	public void setErrorQueryParam(String errorQueryParam) {
		this.errorQueryParam = errorQueryParam;
	}
}


