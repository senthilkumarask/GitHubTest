package com.bbb.wishlist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import com.bbb.account.BBBProfileTools;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.vo.wishlist.WishListVO;
import com.bbb.wishlist.manager.BBBGiftlistManager;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistFormHandler;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.InvalidGiftQuantityException;
import atg.commerce.gifts.InvalidGiftTypeException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderServices;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingTools;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.dp.RepositoryKeyService;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.PropertyManager;


/**
 * Extensions to the atg.commerce.gifts.GiftlistFormHandler.
 *
 * @see atg.commerce.gifts.GiftlistFormHandler
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#2 $
 * @updated $DateTime: 2011/02/09 11:14:12 $$Author: rbarbier $
 */
public class StoreGiftlistFormHandler extends GiftlistFormHandler {

	private static final String ID = "id";
private Long quantityToUpdate;
	private boolean outOfStockFlag;

	/**
	 * @return the outOfStockFlag
	 */
	public boolean isOutOfStockFlag() {
		return outOfStockFlag;
	}

	/**
	 * @param outOfStockFlag the outOfStockFlag to set
	 */
	public void setOutOfStockFlag(boolean outOfStockFlag) {
		this.outOfStockFlag = outOfStockFlag;
	}
	//-------------------------------------
	/** Class version string */

	public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#2 $$Change: 633752 $";

	/**
	 * Event type with this name will be returned last from the getEventTypes method.
	 */
	protected static final String LAST_EVENT_TYPE_NAME = "Other";

	protected static final String GIFT_LIST_EVENT_NAME_MAP_KEY = "eventName";
	protected static final String GIFT_LIST_MONTH_MAP_KEY = "month";
	protected static final String GIFT_LIST_DATE_MAP_KEY = "date";
	protected static final String GIFT_LIST_YEAR_MAP_KEY = "year";
	protected static final String GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY = "shippingAddressId";
	protected static final String GIFT_LIST_EVENT_TYPE_MAP_KEY = "eventType";
	protected static final String GIFT_LIST_PUBLISHED_MAP_KEY = "isPublished";
	protected static final String GIFT_LIST_DESCRIPTION_MAP_KEY = "description";
	protected static final String GIFT_LIST_INSTRUCTIONS_MAP_KEY = "instructions";
	protected static final String AMPERSAND = "&";
	protected static final String SHOW_POPUP_PARAM="showpopup=";
	protected static final String PROD_LIST_PARAM="prodList=[";
	protected static final String ADD_TO_LIST_PARAM="addToList=";
	protected static final String QTY="qty=";

	protected static final String WISH_LIST = "wishlist";
	protected static final String COMMERCE_ITEM_ID = "commerceItemId";
	private static final String LOGIN_REDIRECT_URL = "/account/login.jsp";
	private static final String SITE_ID = "siteId";
	private static final String SAVED_ITEMS_LIST = "savedItemsList";
	private static final String PARAN_CLOSE = "]";
	protected static final String REST_REDIRECT = "atg-rest-ignore-redirect";


	private LblTxtTemplateManager lblTxtTemplateManager;
	private String mCurrentItemId;
	private boolean mContextAdded;
	private List<String> mOmniProdList;
	private String mOmnitureStatus;
	private String storeId;
	private String shipTime;
	private SiteContext mSiteContext;
	private PricingTools pricingTools;
	private ShippingGroupManager mShipManager;
	private String itemIdToRemove;
	private String itemIdToMove;
	private BBBWishlistManager mwishlistManager;
	private String mSave4LaterCookieName;
	private int mSave4LaterCookieAge;
	private String mSave4LaterCookiePath;
	private String mItemMoveToCartID;
	private String mItemMoveFromCartID;
	private boolean isAddOperation;
	private boolean isUpdateOperation;
	private boolean isRemoveOperation;
	private boolean isMoveOperation;
	private boolean isMoveToCartOperation;
	private boolean fromCartPage;
	private boolean fromProductPage;
	private OrderServices mOrderService;
	private int mCountNo;
	private boolean fromWishlist;
	private long totalQuantityAdded = 0;
	private BBBCatalogTools mCatalogTool;

	private String mUndoComItemId;
	private String mItemIdJustMvBack;
	private boolean isUndoOpt;
	private boolean isWishListUndoOpt;
	private boolean isNewItemAdded;
	private String ltlDsl;
	private boolean lastItemRemoval;

	private BBBCatalogTools catalogTools;
	 private CommerceItemManager commerceItemManager;
	 
	 private MutableRepository orderRepository;
	 
	 private boolean bts;
		
	public boolean isBts() {
		return this.bts;
	}
	private String personalizedEditItemFormsSuccessURL;
	private String personalizedEditItemFormsErrorURL;
	private String referenceNumber;
	private String fullImagePath;
	private String thumbnailImagePath;
	private String mobileFullImagePath;
	private String mobileThumbnailImagePath;
	private double personalizePrice;
	private String personalizationOptions;
	private String personalizationDetails;
	private String personalizationStatus;
	private String personalizationMessage;
	private String wishlistToggle;
	private String sddRequired;
    
	public String getSddRequired() {
		return sddRequired;
	}

	public void setSddRequired(String sddRequired) {
		this.sddRequired = sddRequired;
	}

	public String getPersonalizationStatus() {
		return personalizationStatus;
	}

	public void setPersonalizationStatus(String personalizationStatus) {
		this.personalizationStatus = personalizationStatus;
	}

	/**
	 * @return the personalizePrice
	 */
	public double getPersonalizePrice() {
		return personalizePrice;
	}

	/**
	 * @param personalizePrice the personalizePrice to set
	 */
	public void setPersonalizePrice(double personalizePrice) {
		this.personalizePrice = personalizePrice;
	}

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the fullImagePath
	 */
	public String getFullImagePath() {
		return fullImagePath;
	}

	/**
	 * @param fullImagePath the fullImagePath to set
	 */
	public void setFullImagePath(String fullImagePath) {
		this.fullImagePath = fullImagePath;
	}

	/**
	 * @return the thumbnailImagePath
	 */
	public String getThumbnailImagePath() {
		return thumbnailImagePath;
	}

	/**
	 * @param thumbnailImagePath the thumbnailImagePath to set
	 */
	public void setThumbnailImagePath(String thumbnailImagePath) {
		this.thumbnailImagePath = thumbnailImagePath;
	}

	/**
	 * @return the mobileFullImagePath
	 */
	public String getMobileFullImagePath() {
		return mobileFullImagePath;
	}

	/**
	 * @param mobileFullImagePath the mobileFullImagePath to set
	 */
	public void setMobileFullImagePath(String mobileFullImagePath) {
		this.mobileFullImagePath = mobileFullImagePath;
	}

	/**
	 * @return the mobileThumbnailImagePath
	 */
	public String getMobileThumbnailImagePath() {
		return mobileThumbnailImagePath;
	}

	/**
	 * @param mobileThumbnailImagePath the mobileThumbnailImagePath to set
	 */
	public void setMobileThumbnailImagePath(String mobileThumbnailImagePath) {
		this.mobileThumbnailImagePath = mobileThumbnailImagePath;
	}

	/**
	 * @return the personalizationOptions
	 */
	public String getPersonalizationOptions() {
		return personalizationOptions;
	}

	/**
	 * @param personalizationOptions the personalizationOptions to set
	 */
	public void setPersonalizationOptions(String personalizationOptions) {
		this.personalizationOptions = personalizationOptions;
	}

	/**
	 * @return the personalizationDetails
	 */
	public String getPersonalizationDetails() {
		return personalizationDetails;
	}

	/**
	 * @param personalizationDetails the personalizationDetails to set
	 */
	public void setPersonalizationDetails(String personalizationDetails) {
		this.personalizationDetails = personalizationDetails;
	}
	/**
	 * @return the personalizedEditItemFormsSuccessURL
	 */
	public String getPersonalizedEditItemFormsSuccessURL() {
		return personalizedEditItemFormsSuccessURL;
	}
	/**
	 * @param personalizedEditItemFormsSuccessURL the personalizedEditItemFormsSuccessURL to set
	 */
	public void setPersonalizedEditItemFormsSuccessURL(
			String personalizedEditItemFormsSuccessURL) {
		this.personalizedEditItemFormsSuccessURL = personalizedEditItemFormsSuccessURL;
	}
	/**
	 * @return the personalizedEditItemFormsErrorURL
	 */
	public String getPersonalizedEditItemFormsErrorURL() {
		return personalizedEditItemFormsErrorURL;
	}
	/**
	 * @param personalizedEditItemFormsErrorURL the personalizedEditItemFormsErrorURL to set
	 */
	public void setPersonalizedEditItemFormsErrorURL(
			String personalizedEditItemFormsErrorURL) {
		this.personalizedEditItemFormsErrorURL = personalizedEditItemFormsErrorURL;
	}
	public void setBts(boolean bts) {
		this.bts = bts;
	}
		/**
		 * @return the orderRepository
		 */
		public MutableRepository getOrderRepository() {
			return orderRepository;
		}
		/**
		 * @param orderRepository the orderRepository to set
		 */
		public void setOrderRepository(final MutableRepository orderRepository) {
			this.orderRepository = orderRepository;
		}
	 
	 
	public CommerceItemManager getCommerceItemManager() {
		return commerceItemManager;
	}

	public void setCommerceItemManager(CommerceItemManager commerceItemManager) {
		this.commerceItemManager = commerceItemManager;
	}

	/** @return */
   public final BBBCatalogTools getCatalogUtil() {
       return this.catalogTools;
   }

   /** @param catalogUtil */
   public final void setCatalogUtil(final BBBCatalogTools catalogUtil) {
       this.catalogTools = catalogUtil;
   }

	//added for comparison page redirection
	private String comparisonPageRedirectUrl;
	
	/**
	 * @return the isUndoOpt
	 */
	public boolean isUndoOpt() {
		return this.isUndoOpt;
	}

	/**
	 * @param pIsUndoOpt the isUndoOpt to set
	 */
	public void setUndoOpt(final boolean pIsUndoOpt) {
		this.isUndoOpt = pIsUndoOpt;
	}

	/**
	 * @return the isNewItemAdded
	 */
	public boolean isNewItemAdded() {
		return this.isNewItemAdded;
	}

	/**
	 * @param pIsNewItemAdded the isNewItemAdded to set
	 */
	public void setNewItemAdded(final boolean pIsNewItemAdded) {
		this.isNewItemAdded = pIsNewItemAdded;
	}

	/**
	 * @return the itemIdJustMvBack
	 */
	public String getItemIdJustMvBack() {
		return this.mItemIdJustMvBack;
	}

	/**
	 * @param pItemIdJustMvBack the itemIdJustMvBack to set
	 */
	public void setItemIdJustMvBack(final String pItemIdJustMvBack) {
		this.mItemIdJustMvBack = pItemIdJustMvBack;
	}


	/**
     * @return the wishListItems
     */
    public WishListVO getWishListItems() {
            return this.getWishlistManager().getWishListItems();
    }


	/**
	 * @return the totalQuantityAdded
	 */
	public long getTotalQuantityAdded() {
		return this.totalQuantityAdded;
	}

	/**
	 * @param totalQuantityAdded the totalQuantityAdded to set
	 */
	public void setTotalQuantityAdded(final long totalQuantityAdded) {
		this.totalQuantityAdded = totalQuantityAdded;
	}
	private PurchaseProcessHelper mPurchaseProcessHelper;

	/**
	 * @return the fromProductPage
	 */
	public boolean isFromProductPage() {
		return this.fromProductPage;
	}

	/**
	 * @param fromProductPage the fromProductPage to set
	 */
	public void setFromProductPage(final boolean fromProductPage) {
		this.fromProductPage = fromProductPage;
	}

	/**
	 * @return the purchaseProcessHelper
	 */
	public PurchaseProcessHelper getPurchaseProcessHelper() {
		return this.mPurchaseProcessHelper;
	}

	/**
	 * @param pPurchaseProcessHelper the purchaseProcessHelper to set
	 */
	public void setPurchaseProcessHelper(final PurchaseProcessHelper pPurchaseProcessHelper) {
		this.mPurchaseProcessHelper = pPurchaseProcessHelper;
	}

	/**
	 * @return the undoComItemId
	 */
	public String getUndoComItemId() {
		return this.mUndoComItemId;
	}

	/**
	 * @param pUndoComItemId the undoComItemId to set
	 */
	public void setUndoComItemId(final String pUndoComItemId) {
		this.mUndoComItemId = pUndoComItemId;
	}


	public BBBCatalogTools getCatalogTool() {
		return this.mCatalogTool;
	}

	public void setCatalogTool(final BBBCatalogTools catalogTool) {
		this.mCatalogTool = catalogTool;
	}
	/**
	 * @return the countNo
	 */
	public int getCountNo() {
		return this.mCountNo;
	}

	/**
	 * @param pCountNo the countNo to set
	 */
	public void setCountNo(final int pCountNo) {
		this.mCountNo = pCountNo;
	}

	/**
	 * @return the itemMoveToCartID
	 */
	public String getItemMoveToCartID() {
		return this.mItemMoveToCartID;
	}

	/**
	 * @param pItemMoveToCartID the itemMoveToCartID to set
	 */
	public void setItemMoveToCartID(final String pItemMoveToCartID) {
		this.mItemMoveToCartID = pItemMoveToCartID;
	}

	/**
	 * @return the itemMoveFromCartID
	 */
	public String getItemMoveFromCartID() {
		return this.mItemMoveFromCartID;
	}

	/**
	 * @param pItemMoveFromCartID the itemMoveFromCartID to set
	 */
	public void setItemMoveFromCartID(final String pItemMoveFromCartID) {
		this.mItemMoveFromCartID = pItemMoveFromCartID;
	}
	public boolean isFromWishlist() {
		return this.fromWishlist;
	}

	public void setFromWishlist(final boolean fromWishlist) {
		this.fromWishlist = fromWishlist;
	}


	/**
	 * @return the isFromCartPage
	 */
	public boolean isFromCartPage() {
		return this.fromCartPage;
	}

	/**
	 * @param isFromCartPage the isFromCartPage to set
	 */
	public void setFromCartPage(final boolean fromCartPage) {
		this.fromCartPage = fromCartPage;
	}

	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return this.storeId;
	}

	/**
	 * @param storeId the storeId to set
	 */
	public void setStoreId(final String storeId) {
		this.storeId = storeId;
	}

	/**
	 * @return the shipTime
	 */
	public String getShipTime() {
		return shipTime;
	}
	

	/**
	 * @param shipTime the shipTime to set
	 */
	public void setShipTime(String shipTime) {
		this.shipTime = shipTime;
	}
	

	/**
	 * @return the orderService
	 */
	public OrderServices getOrderService() {
		return this.mOrderService;
	}

	/**
	 * @param pOrderService the orderService to set
	 */
	public void setOrderService(final OrderServices pOrderService) {
		this.mOrderService = pOrderService;
	}

	/**
	 * @return the isMoveToCartOperation
	 */
	public boolean isMoveToCartOperation() {
		return this.isMoveToCartOperation;
	}

	/**
	 * @param pIsMoveToCartOperation the isMoveToCartOperation to set
	 */
	public void setMoveToCartOperation(final boolean pIsMoveToCartOperation) {
		this.isMoveToCartOperation = pIsMoveToCartOperation;
	}

	/**
	 * @return the isMoveOperation
	 */
	public boolean isMoveOperation() {
		return this.isMoveOperation;
	}

	/**
	 * @param pIsMoveOperation the isMoveOperation to set
	 */
	public void setMoveOperation(final boolean pIsMoveOperation) {
		this.isMoveOperation = pIsMoveOperation;
	}

	public boolean isAddOperation() {
		return this.isAddOperation;
	}

	public void setAddOperation(final boolean isAddOperation) {
		this.isAddOperation = isAddOperation;
	}

	public boolean isUpdateOperation() {
		return this.isUpdateOperation;
	}

	public void setUpdateOperation(final boolean isUpdateOperation) {
		this.isUpdateOperation = isUpdateOperation;
	}

	public boolean isRemoveOperation() {
		return this.isRemoveOperation;
	}

	public void setRemoveOperation(final boolean isRemoveOperation) {
		this.isRemoveOperation = isRemoveOperation;
	}

	public String getSave4LaterCookieName() {
		return this.mSave4LaterCookieName;
	}

	public void setSave4LaterCookieName(final String save4LaterCookieName) {
		this.mSave4LaterCookieName = save4LaterCookieName;
	}

	public int getSave4LaterCookieAge() {
		return this.mSave4LaterCookieAge;
	}

	public void setSave4LaterCookieAge(final int save4LaterCookieAge) {
		this.mSave4LaterCookieAge = save4LaterCookieAge;
	}

	public String getSave4LaterCookiePath() {
		return this.mSave4LaterCookiePath;
	}

	public void setSave4LaterCookiePath(final String save4LaterCookiePath) {
		this.mSave4LaterCookiePath = save4LaterCookiePath;
	}
	/**
	 * @return the mwishlistManager
	 */
	public BBBWishlistManager getWishlistManager() {
		return this.mwishlistManager;
	}
	/**
	 * @param mwishlistManager the mwishlistManager to set
	 */
	public void setWishlistManager(final BBBWishlistManager mwishlistManager) {
		this.mwishlistManager = mwishlistManager;
	}
	/**
	 * @return the itemIdToRemove
	 */
	public String getItemIdToRemove() {
		return this.itemIdToRemove;
	}
	/**
	 * @param itemIdToRemove the itemIdToRemove to set
	 */
	public void setItemIdToRemove(final String itemIdToRemove) {
		this.itemIdToRemove = itemIdToRemove;
	}
	/**
	 * @return the quantityToUpdate
	 */
	public Long getQuantityToUpdate() {
		return this.quantityToUpdate;
	}
	/**
	 * @param quantityToUpdate the quantityToUpdate to set
	 */
	public void setQuantityToUpdate(final Long quantityToUpdate) {
		this.quantityToUpdate = quantityToUpdate;
	}
	public ShippingGroupManager getShippingGroupManager() {
		return this.mShipManager;
	}
	public void setShippingGroupManager(final ShippingGroupManager mShipManager) {
		this.mShipManager = mShipManager;
	}

	 public PricingTools getPricingTools()
	    {
	        return this.pricingTools;
	    }

	    public void setPricingTools(final PricingTools pPricingTools)
	    {
	    	this.pricingTools = pPricingTools;
	    }


	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return this.mSiteContext;
	}

	/**
	 * @param pSiteContext the siteContext to set
	 */
	public void setSiteContext(final SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}

	/**
	 * @return the omnitureStatus
	 */
	public String getOmnitureStatus() {
		return this.mOmnitureStatus;
	}

	/**
	 * @param pOmnitureStatus the omnitureStatus to set
	 */
	public void setOmnitureStatus(final String pOmnitureStatus) {
		this.mOmnitureStatus = pOmnitureStatus;
	}

	/**
	 * @return the omniProdList
	 */
	public List<String> getOmniProdList() {
		return this.mOmniProdList;
	}

	/**
	 * @param pOmniProdList the omniProdList to set
	 */
	public void setOmniProdList(final List<String> pOmniProdList) {
		this.mOmniProdList = pOmniProdList;
	}
	
		/**
	 * An extension of standard HashMap Java class.
	 * This implementation overrides the {@link HashMap#put(Object, Object)} method to add
	 * giftlist-specified behaviour.
	 * @see HashMap
	 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#2 $$Change: 633752 $
	 * @updated $DateTime: 2011/02/09 11:14:12 $$Author: rbarbier $
	 */
	/*private class ChangeAwareMap extends HashMap<String, Object>
	{


		private static final long serialVersionUID = 1L;

		*//**
		 * This implementation does the following:
		 * <ol>
		 *  <li>call to super-method to add an item into map;</li>
		 *  <li>set formhandler's <i>giftlistId</i> property to be equal to <i>key</i></li>
		 *  <li>handle <i>addItemToGiftlist</i> process</li>
		 * </ol>
		 * @return old map value stored by the key specified.
		 *//*

		public Object put(String key, Object value)
		{
			Object result = super.put(key, value);
			try
			{
				setGiftlistId(key);
				handleAddItemToGiftlist(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());
			} catch (Exception e)
			{
				if (isLoggingError()) {
					logError(e);
				}

			}
			return result;
		}
	}*/

	//-------------------------------------
	// Constants
	//-------------------------------------
	// Resource message keys
	public static final String MSG_INVALID_DATE = "InvalidDate";

	/**
	 * BBBSessionBean
	 */
	private  BBBSessionBean mSessionBean;

	/**
	 * BBBSavedItemsSessionBean
	 */
	private  BBBSavedItemsSessionBean mSavedItemsSessionBean;

	private List<GiftListVO> giftListVO;
	/**
	 * Gift list id constant.
	 */
	public static final String GIFTLIST_ID = "giftlistId";

	/**
	 * Success URL constant.
	 */
	public static final String SUCCESS_URL = "successURL";

	/**
	 * Sku id constant.
	 */
	public static final String SKU_ID = "skuId";

	/**
	 * Quantity constant.
	 */
	public static final String QUANTITY = "quantity";

	/**
	 * Product id constant.
	 */
	public static final String PRODUCT_ID = "productId";

	/**
	 * Add item to gift list login URL.
	 */
	private String mAddItemToGiftlistLoginURL;

	/**
	 * Move items from gift list login URL.
	 */
	private String mMoveItemsFromCartLoginURL;

	/**
	 * Profile property manager.
	 */
	private PropertyManager mProfilePropertyManager;

	/**
	 * Remove items from gift list successful URL.
	 */
	private String mRemoveItemsFromGiftlistSuccessURL;

	/**
	 * Remove items from gift list error URL.
	 */
	private String mRemoveItemsFromGiftlistErrorURL;

	/* private SessionBean mSessionBean;*/

	/**
	 * jsonResultString for Add to Wish List.
	 */
	private String jsonResultString;

	/**
	 * parentProductId.
	 */
	private String parentProductId;

	/**
	 * addWishlistSuccessFlag.
	 */
	private boolean addWishlistSuccessFlag;

	/**
	 * productList.
	 */
	private List<String> prodList;

	/**
	 * productDetailsRedirectUrl.
	 */
	private String productDetailsRedirectUrl;

	private Map<String, String> mRemoveAllWishListItemsFailureResult;
	
	private String successQueryParam;
	
	private Map<String,String> successUrlMap;
	
    private Map<String,String> errorUrlMap;
    
    private String fromPage;// Page Name that will be set from JSP
	
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
	


	/*private Map<String, Object> giftlistIdToValue = new ChangeAwareMap();

	*//**
	 * Readonly property, contains a Map.
	 * Key is a Giftlist ID; value is some value (no matter what).
	 * <br>
	 * <font color="red"><strong>IMPORTANT!</strong></font>
	 * When adding a key-value pair into this property, the following will occur:
	 * <ol>
	 *  <li>key will be treated as a giftlist ID</li>
	 *  <li>a {@link #handleAddItemToGiftlist(DynamoHttpServletRequest, DynamoHttpServletResponse)} method will be called</li>
	 * </ol>
	 * This property is used by non-JavaScript version of color/size picker only.
	 *//*
	public Map<String, Object> getGiftlistIdToValue()
	{
		return giftlistIdToValue;
	}*/

	/**
	 * @return the mRemoveAllWishListItemsFailureResult
	 */
	public Map<String, String> getRemoveAllWishListItemsFailureResult() {
		return this.mRemoveAllWishListItemsFailureResult;
	}
	/**
	 * @param mRemoveAllWishListItemsFailureResult the mRemoveAllWishListItemsFailureResult to set
	 */
	public void setRemoveAllWishListItemsFailureResult(final Map<String, String> mRemoveAllWishListItemsFailureResult) {
		this.mRemoveAllWishListItemsFailureResult = mRemoveAllWishListItemsFailureResult;
	}
	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}

	/**
	 * @param pLblTxtTemplateManager
	 *            the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(
			final LblTxtTemplateManager pLblTxtTemplateManager) {
		this.lblTxtTemplateManager = pLblTxtTemplateManager;
	}


	/**
	 * Sets property AddItemToGiftlistLoginURL.
	 * @param pAddItemToGiftlistLoginURL  The property to store the URL for where the user should be redirected
	 *        if they attempt to add an item to the giftlist without being logged in.
	 * @beaninfo description:  The property to store the URL for where the user should be redirected
	 *           if they attempt to add an item to the giftlist without being logged in.
	 **/
	public void setAddItemToGiftlistLoginURL(final String pAddItemToGiftlistLoginURL) {
		this.mAddItemToGiftlistLoginURL = pAddItemToGiftlistLoginURL;
	}

	/**
	 * @return add item to gift list login URL.
	 **/
	public String getAddItemToGiftlistLoginURL() {
		return this.mAddItemToGiftlistLoginURL;
	}

	/**
	 * Sets property MoveItemsFromCartLoginURL.
	 * @param pMoveItemsFromCartLoginURL  The property to store the URL for where the user should be redirected
	 *        if they attempt to move items from cart to the giftlist without being logged in.
	 * @beaninfo description:  The property to store the URL for where the user should be redirected
	 *           if they attempt to move items from cart to the giftlist without being logged in.
	 **/
	public void setMoveItemsFromCartLoginURL(final String pMoveItemsFromCartLoginURL) {
		this.mMoveItemsFromCartLoginURL = pMoveItemsFromCartLoginURL;
	}

	/**
	 * @return move items from cart to gift list login URL.
	 **/
	public String getMoveItemsFromCartLoginURL() {
		return this.mMoveItemsFromCartLoginURL;
	}

	/**
	 * @param pProfilePropertyManager - the property manager for profiles, used to see if the user is logged in
	 * @beaninfo description:  The PropertyManager for profiles, used to see if the user is logged in
	 **/
	public void setProfilePropertyManager(final PropertyManager pProfilePropertyManager) {
		this.mProfilePropertyManager = pProfilePropertyManager;
	}

	/**
	 * @return profile property manager.
	 **/
	public PropertyManager getProfilePropertyManager() {
		return this.mProfilePropertyManager;
	}

	/**
	 * @param pRemoveItemsFromGiftlistSuccessURL - remove items from
	 * gift list success URL.
	 * @beaninfo description:  The property to store the success URL for removeItemsFromGiftlist.
	 **/
	public void setRemoveItemsFromGiftlistSuccessURL(final String pRemoveItemsFromGiftlistSuccessURL) {
		this.mRemoveItemsFromGiftlistSuccessURL = pRemoveItemsFromGiftlistSuccessURL;
	}

	/**
	 * @return remove items from gift list success URL.
	 **/
	public String getRemoveItemsFromGiftlistSuccessURL() {
		return this.mRemoveItemsFromGiftlistSuccessURL;
	}

	/**
	 * @param pRemoveItemsFromGiftlistErrorURL - remove items from gift list error URL.
	 * @beaninfo description:  The property to store the error URL for removeItemsFromGiftlist.
	 **/
	public void setRemoveItemsFromGiftlistErrorURL(final String pRemoveItemsFromGiftlistErrorURL) {
		this.mRemoveItemsFromGiftlistErrorURL = pRemoveItemsFromGiftlistErrorURL;
	}

	/**
	 * @return remove items from gift list error URL.
	 **/
	public String getRemoveItemsFromGiftlistErrorURL() {
		return this.mRemoveItemsFromGiftlistErrorURL;
	}

	//-------------------------------------
	// property: UpdateGiftlistAndItemsSuccessURL
	private String mUpdateGiftlistAndItemsSuccessURL;

	/**
	 * Sets property UpdateGiftlistAndItemsSuccessURL
	 * @param pUpdateGiftlistAndItemsSuccessURL  The property to store the Success URL for UpdateGiftlistAndItems.
	 * @beaninfo description:  The property to store the success URL for UpdateGiftlistAndItems.
	 **/
	public void setUpdateGiftlistAndItemsSuccessURL(final String pUpdateGiftlistAndItemsSuccessURL) {
		this.mUpdateGiftlistAndItemsSuccessURL = pUpdateGiftlistAndItemsSuccessURL;
	}

	/**
	 * Returns property UpdateGiftlistAndItemsSuccessURL
	 * @return The value of the property UpdateGiftlistAndItemsSuccessURL.
	 **/
	public String getUpdateGiftlistAndItemsSuccessURL() {
		return this.mUpdateGiftlistAndItemsSuccessURL;
	}

	//-------------------------------------
	// property: UpdateGiftlistAndItemsErrorURL
	private String mUpdateGiftlistAndItemsErrorURL;

	/**
	 * Sets property UpdateGiftlistAndItemsErrorURL
	 * @param pUpdateGiftlistAndItemsErrorURL  The property to store the error URL for UpdateGiftlistAndItems.
	 * @beaninfo description:  The property to store the error URL for UpdateGiftlistAndItems.
	 **/
	public void setUpdateGiftlistAndItemsErrorURL(final String pUpdateGiftlistAndItemsErrorURL) {
		this.mUpdateGiftlistAndItemsErrorURL = pUpdateGiftlistAndItemsErrorURL;
	}

	/**
	 * Returns property UpdateGiftlistAndItemsErrorURL
	 * @return The value of the property UpdateGiftlistAndItemsErrorURL.
	 **/
	public String getUpdateGiftlistAndItemsErrorURL() {
		return this.mUpdateGiftlistAndItemsErrorURL;
	}

	//-------------------------------------
	// property: MoveToNewGiftListAddressSuccessURL
	private String mMoveToNewGiftListAddressSuccessURL;

	/**
	 * Sets property MoveToNewGiftListAddressSuccessURL
	 * @param pMoveToNewGiftListAddressSuccessURL  The property to store the Success URL for MoveToNewGiftListAddress.
	 * @beaninfo description:  The property to store the success URL for MoveToNewGiftListAddress.
	 **/
	public void setMoveToNewGiftListAddressSuccessURL(final String pMoveToNewGiftListAddressSuccessURL) {
		this.mMoveToNewGiftListAddressSuccessURL = pMoveToNewGiftListAddressSuccessURL;
	}

	/**
	 * Returns property MoveToNewGiftListAddressSuccessURL
	 * @return The value of the property MoveToNewGiftListAddressSuccessURL.
	 **/
	public String getMoveToNewGiftListAddressSuccessURL() {
		return this.mMoveToNewGiftListAddressSuccessURL;
	}

	//-------------------------------------
	// property: MoveToNewGiftListAddressErrorURL
	private String mMoveToNewGiftListAddressErrorURL;

	/**
	 * Sets property MoveToNewGiftListAddressErrorURL
	 * @param pMoveToNewGiftListAddressErrorURL  The property to store the error URL for MoveToNewGiftListAddress.
	 * @beaninfo description:  The property to store the error URL for MoveToNewGiftListAddress.
	 **/
	public void setMoveToNewGiftListAddressErrorURL(final String pMoveToNewGiftListAddressErrorURL) {
		this.mMoveToNewGiftListAddressErrorURL = pMoveToNewGiftListAddressErrorURL;
	}

	/**
	 * Returns property MoveToNewGiftListAddressErrorURL
	 * @return The value of the property MoveToNewGiftListAddressErrorURL.
	 **/
	public String getMoveToNewGiftListAddressErrorURL() {
		return this.mMoveToNewGiftListAddressErrorURL;
	}

	//--------------------------------------
	// property: useWishlist
	/**
	 * Identifies whether or not the request comes from adding an item
	 * to a giftlist or wishlist.
	 */
	private boolean mUseWishlist = false;

	public void setUseWishlist(final boolean pUseWishlist){
		this.mUseWishlist = pUseWishlist;
	}

	public boolean isUseWishlist(){
		return this.mUseWishlist;
	}

	//-----------------------------------
	// property: woodfinishPicker
	/**
	 * A boolean indicating whether or not a wood finish picker is being
	 * used to add the item to the gift/wish list.
	 */
	private boolean mWoodFinishPicker = false;

	/**
	 * Sets mWoodFinishPicker
	 * @param pWoodFinishPicker
	 */
	public void setWoodFinishPicker(final boolean pWoodFinishPicker){
		this.mWoodFinishPicker = pWoodFinishPicker;
	}

	/**
	 * Gets mWoodFinishPicker
	 * @return
	 */
	public boolean isWoodFinishPicker(){
		return this.mWoodFinishPicker;
	}

	public BBBSessionBean getSessionBean() {
		return this.mSessionBean;
	}

	public void setSessionBean(final BBBSessionBean pSessionBean) {
		this.mSessionBean = pSessionBean;
	}

	public BBBSavedItemsSessionBean getSavedItemsSessionBean() {
		return this.mSavedItemsSessionBean;
	}
	public void setSavedItemsSessionBean(
			final BBBSavedItemsSessionBean savedItemsSessionBean) {
		this.mSavedItemsSessionBean = savedItemsSessionBean;
	}
	@SuppressWarnings("unchecked")
	@Override
	/*public Collection getEventTypes()
	{
		Collection availableEventTypes = super.getEventTypes();
		if (availableEventTypes == null || availableEventTypes.isEmpty())
		{
			return availableEventTypes;
		} else
		{
			return sortEventTypes(availableEventTypes);
		}
	}*/

	/**
	 * Sort event types in alphabetical ascending order.
	 * The event type with name equal to {@link #LAST_EVENT_TYPE_NAME} will be returned last.
	 * @param pEventTypes - collection of events to be sorted.
	 * @return a sorted list of events.
	 */
	/*protected List<String> sortEventTypes(Collection<String> pEventTypes)
	{
		List<String> eventTypes = new ArrayList<String>(pEventTypes);
		Collections.sort(eventTypes, new Comparator<String>()
				{


			public int compare(String o1, String o2)
			{
				if (LAST_EVENT_TYPE_NAME.equals(o1))
				{
					return Integer.MAX_VALUE;
				} else if (LAST_EVENT_TYPE_NAME.equals(o2))
				{
					return Integer.MIN_VALUE;
				} else
				{
					return o1.compareTo(o2);
				}
			}
				});
		return eventTypes;
	}*/

	/**
	 * Saves sku and product IDs to be added to wishlist into 'skuIdToAdd' and 'productIdToAdd' transient profile properties correspondingly
	 * @param pRedirectURL - the URL to redirect to after login
	 * @throws ServletException if anything goes wrong
	 * @throws InvalidParameterException
	 * @throws CommerceItemNotFoundException
	 */
//	@SuppressWarnings("unchecked") //ok, we know which values (strings) we put on these keys
	//private void saveSkuAndProductIntoSession(String pRedirectURL) throws ServletException, CommerceItemNotFoundException, InvalidParameterException
	//{
		//if (getItemIds()!= null && getItemIds().length > 0){
			//String commerceItemId = getItemIds()[0];
			//CommerceItem item = getOrder().getCommerceItem(commerceItemId);
			/*if (item != null){
				getSessionBean().getValues().put(SessionBean.COMMERCE_ITEM_ID_PROPERTY_NAME, commerceItemId);
        getSessionBean().getValues().put(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME, item.getCatalogRefId());
        getSessionBean().getValues().put(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME,  item.getAuxiliaryData().getProductId());
			}*/
	//	}
		/*else{
			getSessionBean().getValues().put(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME, getCatalogRefIds()[0]);
      getSessionBean().getValues().put(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME, getProductId());
		}*/

		/* getSessionBean().getValues().put(SessionBean.GIFTLIST_ID_PROPERTY_NAME, getProfile().isTransient() ? null : getGiftlistId());
    getSessionBean().getValues().put(SessionBean.QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME, new Long(getQuantity()));
    getSessionBean().getValues().put(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME, pRedirectURL);*/
	//}

	/*protected void preValidateAddItemTogiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	throws ServletException, IOException
	{
		if(isLoggingDebug())
		{
            logDebug(" StoreGiftlistFormHandler :: preValidateAddItemTogiftlist start");
		}
		//add form exception if quantity less or equal zero
		if (getQuantity() <= 0)
		{
			addFormException(new DropletException(
					getLblTxtTemplateManager().getErrMsg(
							"err_giftlist_quantity_lessthan_or_equalto_zero",
							pRequest.getLocale().getLanguage(),
							null, null)));
		}

		if (getCatalogRefIds() == null || getCatalogRefIds().length == 0 //There should be specified SKU IDs to be added
				|| getCatalogRefIds()[0] == null  This condition means that
            catalogRefIds items are explicitly set from http request parameters )
		{
			addFormException(new DropletException(
					getLblTxtTemplateManager().getErrMsg(
							"err_giftlist_noitemstoadd",
							pRequest.getLocale().getLanguage(),
							null, null)));
		}
		if(isLoggingDebug())
		{
            logDebug(" StoreGiftlistFormHandler :: preValidateAddItemTogiftlist end");
		}
	}*/

	/**
	 * Before adding an item to the giftlist check if user is explicitly logged in.
	 * If not store product\sku\giftlist info into the session.
	 *
	 * @param pRequest DynamoHttpServletRequest
	 * @param pResponse DynamoHttpServletResponse
	 *
	 * @throws ServletException if there was an error while executing the code
	 * @throws IOException if there was an error with servlet io
	 */
	/*public void preAddItemToGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	throws ServletException, IOException {
		if(isLoggingDebug())
		{
            logDebug(" StoreGiftlistFormHandler :: preAddItemToGiftlist start");
		}
		if (!isUserLoggedIn()){
			System.out.println("*********************************************");
			System.out.println("saveGiftListItemInSession called");
			System.out.println("*********************************************");
			//addFormException(new DropletException("notLoggedIn", "notLoggedIn"));
			//setAddItemToGiftlistErrorURL(getAddItemToGiftlistLoginURL());
			//return;
			try {
				saveGiftListItemInSession(pRequest);
			} catch (CommerceItemNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		super.preAddItemToGiftlist(pRequest, pResponse);
	}*/

	/**
	 * Checks if user is explicitly logged in.
	 * @return true if user is logged in.
	 * @throws ServletException
	 */
	/*public boolean isUserLoggedIn() throws ServletException{
		// First, check to see if the user is logged in.
		PropertyManager profilePropertyManager = getProfilePropertyManager();
		Integer securityStatus = (Integer) getProfile()
		.getPropertyValue(profilePropertyManager.getSecurityStatusPropertyName());
		int securityStatusCookie = profilePropertyManager.getSecurityStatusCookie();

		return (securityStatus.intValue() > securityStatusCookie);
	}*/


	/**
	 * Before moving item to the giftlist check if user is explicitly logged in.
	 * If not store product\sku\giftlist info into the session.
	 *
	 */
	public void preMoveItemsFromCart(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		super.preMoveItemsFromCart(pRequest, pResponse);
	}

	/**
	 * Initializes all the form values from the provided gift list.
	 * @param pGiftlist - gift list
	 */
	/*public void setGiftlist(RepositoryItem pGiftlist) {
		GiftlistTools tools = getGiftlistManager().getGiftlistTools();
		//populate the form handler with values form the gift list
		setGiftlistId(pGiftlist.getRepositoryId());
		setEventName((String) pGiftlist.getPropertyValue(tools.getEventNameProperty()));
		setEventType((String) pGiftlist.getPropertyValue(tools.getEventTypeProperty()));
		setDescription((String) pGiftlist.getPropertyValue(tools.getDescriptionProperty()));

		Date eventDate = (Date) pGiftlist.getPropertyValue(tools.getEventDateProperty());
		setIsPublished((Boolean) pGiftlist.getPropertyValue(tools.getPublishedProperty()));
		setEventDate(eventDate);

		RepositoryItem shippingaddress = (RepositoryItem) pGiftlist.getPropertyValue(tools.getShippingAddressProperty());

		if (shippingaddress != null) {
			setShippingAddressId(shippingaddress.getRepositoryId());
		}

		setInstructions((String) pGiftlist.getPropertyValue(tools.getInstructionsProperty()));
	}*/

	/**
	 * Clears the form handler property values.
	 * @param pRequest - http request
	 * @param pResponse - http response
	 * @return true if success, false - otherwise
	 * @throws ServletException if servlet error occurs
	 * @throws IOException if IO error occurs
	 * @throws CommerceException if commerce error occurs
	 */
	public boolean handleClearForm(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException {
		this.addContextPath(pRequest.getContextPath());
		//populate the form handler with values form the gift list
		this.setGiftlistId(null);
		this.setEventName(null);
		this.setEventType(null);
		this.setDescription(null);
		this.setIsPublished(Boolean.TRUE);
		this.setEventDate(new Date(System.currentTimeMillis()));
		this.setShippingAddressId(null);
		this.setInstructions(null);
		this.resetFormExceptions();
		return true;
	}

	/**
	 * Clears form errors as part of the cancel operation.
	 * @param pRequest - http request
	 * @param pResponse - http response
	 * @throws ServletException if servlet error occurs
	 * @throws IOException if IO error occurs
	 * @return True if success, False - otherwise
	 */
	@Override
	public boolean handleCancel(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws ServletException, IOException {
		this.addContextPath(pRequest.getContextPath());
		this.resetFormExceptions();

		return super.handleCancel(pRequest, pResponse);
	}

	/**
	 * Stores entered by user data to the sessions-scoped component before moving to
	 * Add New Address URL. The stored data can be retrieved later during form initialization.
	 * @param pRequest - http request
	 * @param pResponse - http response
	 * @throws ServletException if servlet error occurs
	 * @throws IOException if IO error occurs
	 * @return True if success, False - otherwise
	 */
	/*public boolean handleMoveToNewGiftListAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
	{
		// ignore not entered required fields
		addContextPath(pRequest.getContextPath());
		resetFormExceptions();
		storeNewGiftListData();
		return checkFormRedirect (getMoveToNewGiftListAddressSuccessURL(), getMoveToNewGiftListAddressErrorURL(), pRequest, pResponse);
	}*/

	/**
	 * Stores entered by user data to the session-scoped component.
	 */
/*	public void storeNewGiftListData(){
		Map sessionBeanValues = getSessionBean().getValues();

    sessionBeanValues.put(GIFT_LIST_EVENT_NAME_MAP_KEY, getEventName());
    sessionBeanValues.put(GIFT_LIST_MONTH_MAP_KEY, getMonth());
    sessionBeanValues.put(GIFT_LIST_DATE_MAP_KEY, getDate());
    sessionBeanValues.put(GIFT_LIST_YEAR_MAP_KEY, getYear());
    sessionBeanValues.put(GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY, getShippingAddressId());
    sessionBeanValues.put(GIFT_LIST_EVENT_TYPE_MAP_KEY, getEventType());
    sessionBeanValues.put(GIFT_LIST_PUBLISHED_MAP_KEY, getIsPublished());
    sessionBeanValues.put(GIFT_LIST_DESCRIPTION_MAP_KEY, getDescription());
    sessionBeanValues.put(GIFT_LIST_INSTRUCTIONS_MAP_KEY, getInstructions());
	}*/

	/**
	 * Initializes Gift list form with previously entered data stored in the session-scoped
	 * component.
	 *
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @return true
	 */
/*	public boolean handleInitializeGiftListForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse){
		addContextPath(pRequest.getContextPath());
		// Initialize gift list form with values previously entered by user in this session
		Map sessionBeanValues = getSessionBean().getValues();
    setEventName((String)sessionBeanValues.get(GIFT_LIST_EVENT_NAME_MAP_KEY));
    setMonth((Integer)sessionBeanValues.get(GIFT_LIST_MONTH_MAP_KEY));
    setDate((String)sessionBeanValues.get(GIFT_LIST_DATE_MAP_KEY));
    setYear((String)sessionBeanValues.get(GIFT_LIST_YEAR_MAP_KEY));
    setShippingAddressId((String)sessionBeanValues.get(GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY));
    setEventType((String)sessionBeanValues.get(GIFT_LIST_EVENT_TYPE_MAP_KEY));
    setIsPublished((Boolean)sessionBeanValues.get(GIFT_LIST_PUBLISHED_MAP_KEY));
    setDescription((String)sessionBeanValues.get(GIFT_LIST_DESCRIPTION_MAP_KEY));
    setInstructions((String)sessionBeanValues.get(GIFT_LIST_INSTRUCTIONS_MAP_KEY));
		return true;
	}*/

	/**
	 * Overriden method of GiftlistFormHandler class. Called when the customer selects create to create a new
	 * giftlist. Before invoking parent's save gift list logic, this method calls the getEventDate() method to
	 * set the correct value of date in parent class.
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @return true if successful, false otherwise.
	 * @exception ServletException if there was an error while executing the code
	 * @exception IOException if there was an error with servlet io
	 * @exception CommerceException if there was an error with Commerce
	 */
/*	public boolean handleSaveGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException {
		addContextPath(pRequest.getContextPath());
		try {
			getEventDate();
		} catch (InvalidDateException ex) {
			throw new CommerceException(ResourceUtils.getMsgResource(MSG_INVALID_DATE, MY_RESOURCE_NAME, sResourceBundle),ex);
		}

		return super.handleSaveGiftlist(pRequest, pResponse);
	}*/

	/**
	 * Does nothing.
	 * This method is needed for the 'handleAddItemToGiftlist' to be called when accessing Giftlist handler from anchor tag.
	 * @param pValue - value to be set, not used
	 * @throws ServletException never thrown
	 * @throws IOException never thrown
	 */
	/*public void setAddItemToGiftlist(boolean pValue) throws ServletException, IOException
	{

	}*/

	/**
	 * Overridden method of GiftlistFormHandler class. called when the user hits the submit
	 * button on a product page to add an item to the giftlist.  This handler
	 * looks up the product in the catalog and returns a sku.  It then
	 * looks for an existing item in the giftlist with this sku id.  if it
	 * exists, the quantity desired is incremented by the quantity specified.
	 * if it doesn't exist, it creates the item and adds it to the giftlist
	 * specified in giftlist id.
	 *
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @return true if successful, false otherwise.
	 * @exception ServletException if there was an error while executing the code
	 * @exception IOException if there was an error with servlet io
	 * @exception CommerceException if there was an error with Commerce
	 */
	/*public boolean handleAddItemToGiftlist(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException
	{
		if(isLoggingDebug())
		{
            logDebug(" StoreGiftlistFormHandler :: handleAddItemToGiftlist start");
		}
		addContextPath(pRequest.getContextPath());
		if (!isUserLoggedIn()){
			//addFormException(new DropletException("notLoggedIn", "notLoggedIn"));
			//setAddItemToGiftlistErrorURL(getAddItemToGiftlistLoginURL());
			//return;
			try {
				saveGiftListItemInSession(pRequest);
			} catch (CommerceItemNotFoundException e) {
					if (isLoggingError()) {
						logError("handleAddItemToGiftlist "+e);
					}
			} catch (InvalidParameterException e) {
					if (isLoggingError()) {
						logError("handleAddItemToGiftlist "+e);
					}
			}
		}

		preValidateAddItemTogiftlist(pRequest, pResponse);
		return super.handleAddItemToGiftlist(pRequest, pResponse);
	}
*/
	/**
	 * Overriden method of GiftlistFormHandler class. Called when the customer selects update giftlist. Before
	 * invoking parent's gift list update logic, this method calls the getEventDate() method to set the correct
	 * value of date in parent class.
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @return true if successful, false otherwise.
	 * @exception ServletException if there was an error while executing the code
	 * @exception IOException if there was an error with servlet io
	 * @exception CommerceException if there was an error with Commerce
	 */
/*	public boolean handleUpdateGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException {
		addContextPath(pRequest.getContextPath());
		try {
			getEventDate();
		} catch (InvalidDateException ex) {
			throw new CommerceException(ResourceUtils.getMsgResource(MSG_INVALID_DATE, MY_RESOURCE_NAME, sResourceBundle),ex);
		}

		return super.handleUpdateGiftlist(pRequest, pResponse);
	}*/

	/**
	 * The combined handler that allows to update gift list and its items at the same time.
	 *
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @return true if successful, false otherwise.
	 * @exception ServletException if there was an error while executing the code
	 * @exception IOException if there was an error with servlet io
	 * @exception CommerceException if there was an error with Commerce
	 */
	/*public boolean handleUpdateGiftlistAndItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException {
		addContextPath(pRequest.getContextPath());
		String pProfileId = (String) getProfile().getRepositoryId();
		try{
			//If any form errors found, redirect to error URL:
			if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse))
			{
				return false;
			}
			// pre process update
			preUpdateGiftlist(pRequest, pResponse);

			// if new address, create and add to address book.
			if ( getIsNewAddress() )
			{
				setShippingAddressId(createNewShippingAddress());
			}

			// call manager class to update list.
			getGiftlistManager().updateGiftlist(pProfileId, getGiftlistId(), getIsPublished().booleanValue(), getEventName(), getEventDate(), getEventType(), getDescription(), getComments(), getShippingAddressId(), getInstructions(),getSiteId());

			// post process update
			postUpdateGiftlist(pRequest, pResponse);

			//If any form errors found, redirect to error URL:
			if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse))
			{
				return false;
			}

			// update gift list items now
			preUpdateGiftlistItems(pRequest, pResponse);

			if (validateGiftlistId(pRequest, pResponse)) {
				updateGiftlistItems(pRequest, pResponse);

				//If any form errors found, redirect to error URL:
				if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse))
				{
					return false;
				}

				postUpdateGiftlistItems(pRequest, pResponse);
			}
		}

		catch (InvalidDateException ide){
			processException(ide, MSG_INVALID_EVENT_DATE, pRequest, pResponse);
		}
		catch (CommerceException ce){
			processException(ce, MSG_ERROR_SAVING_GIFTLIST, pRequest, pResponse);
		}

		//If NO form errors are found, redirect to the success URL.
		//If form errors are found, redirect to the error URL.
		return checkFormRedirect (getUpdateGiftlistAndItemsSuccessURL(), getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse);
	}*/

	/**
	 * The method is used from REST to remove items from wish list of a logged in user
	 * the wrapper handle method is added to set the input parameters required to
	 * remove wish list item
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException
	 */

	public boolean handleRemoveWishlistItems(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException, CommerceException {
		this.logDebug("StoreGiftlistFormHandler ::handleRemoveWishlistItems getting wish list for profile with email "+this.getProfile().getPropertyValue("email"));
		final String giftListId=((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST)).getRepositoryId();
		this.logDebug("handleRemoveWishlistItems: Wish List id associated with the profile "+giftListId);
		this.setGiftlistId(giftListId);
		final String [] listItemIdToRemove={this.getItemIdToRemove()};
		this.setRemoveGiftitemIds(listItemIdToRemove);
		final GiftlistManager mgr = this.getGiftlistManager();
		final List items = mgr.getGiftlistItems(giftListId);
		boolean isValidItem;

		if(listItemIdToRemove.length > 0){
			if((items != null) && (!items.isEmpty())){
				for(final String idToBeRemoved:listItemIdToRemove){
					isValidItem = false;
					for(int i=0;i<items.size();i++){
						String itemId;
						final RepositoryItem item = (RepositoryItem)items.get(i);
						itemId = (String)item.getPropertyValue(ID);
						if(itemId.equalsIgnoreCase(idToBeRemoved)){
							isValidItem = true;
							break;
						}
					}
					if(!isValidItem){
						this.addFormException((new DropletException("Not a valid item Id",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10162)));
						return false;
					}
				}
			}else{
				this.addFormException((new DropletException("Gift List is empty",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10162)));
				return false;
			}
		}else{
			this.addFormException((new DropletException("List of items to be removed is empty",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10162)));
			return false;
		}

		return this.handleRemoveItemsFromGiftlist(pRequest, pResponse);
	}
		
	/**
	 * handleRemoveAllItemsFromWishlist method is used to remove all items from wish list of a logged in user
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return

	 */
	@SuppressWarnings({"unchecked" })
	public boolean handleRemoveAllItemsFromWishlist(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {

		this.logDebug("StoreGiftlistFormHandler ::handleRemoveAllItemsFromWishlist method starts");
		this.logDebug("StoreGiftlistFormHandler ::handleRemoveAllItemsFromWishlist getting wish list for profile with email "+this.getProfile().getPropertyValue("email"));
		final HashMap<String,String> error = new HashMap<String, String>();
		final String giftListId=((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST)).getRepositoryId();
		this.setGiftlistId(giftListId);
		this.logDebug("handleRemoveAllItemsFromWishlist : wishlist id" + giftListId);
		final WishListVO wishListItems =  this.getWishlistManager().getWishListItems();

		if(wishListItems != null)
		{
			final List<GiftListVO> itemArray =  wishListItems.getWishListItems();
			if(itemArray != null)
			{
				for(final GiftListVO item : itemArray)
				{
					if(item != null)
					{
						String giftListItemId = null;
						try {
							giftListItemId = item.getWishListItemId();
							this.getGiftlistManager().removeItemFromGiftlist(this.getGiftlistId(), giftListItemId );
						} catch (final CommerceException ex) {
							this.logError("Commerce exception while removing items from Wishlist in handleRemoveAllItemsFromWishlist", ex);
							error.put(giftListItemId, BBBCoreErrorConstants.ERR_REMOVE_ITEM + ":Exception Occured while removing item from wishlist");
						} catch (final RepositoryException ex) {
							this.logError("Repository exception while removing items from Wishlist in handleRemoveAllItemsFromWishlist", ex);
							error.put(giftListItemId, BBBCoreErrorConstants.ERR_REMOVE_ITEM + ":Exception Occured while removing item from wishlist");
						}
					}
				}
			}
		}
		this.setRemoveAllWishListItemsFailureResult(error);
		this.logDebug("StoreGiftlistFormHandler ::handleRemoveAllItemsFromWishlist method ends");
		return false;
	}


	/**
	 * HandleRemoveItemsFromGiftlist is called when the user hits the "delete"
	 * button on the wishlist page.  This handler removes the specified
	 * gift Ids from the specified giftlist.
	 *
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @return true if successful, false otherwise.
	 * @exception ServletException if there was an error while executing the code
	 * @exception IOException if there was an error with servlet io
	 * @exception CommerceException if there was an error with Commerce
	 */
	public boolean handleRemoveItemsFromGiftlist(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException {
		this.addContextPath(pRequest.getContextPath());
		//String siteId = SiteContextManager.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			StringBuffer successURL = new StringBuffer("");
			StringBuffer errorURL = new StringBuffer("");
			successURL.append(pRequest.getContextPath()).append(
					getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath()).append(
					getErrorUrlMap().get(getFromPage()));
			setRemoveItemsFromGiftlistSuccessURL(successURL.toString());
			setRemoveItemsFromGiftlistErrorURL(errorURL.toString());
		}

		try {
			//If any form errors found, redirect to error URL:
			if (!this.checkFormRedirect(null, this.getRemoveItemsFromGiftlistErrorURL(), pRequest, pResponse)) {
				return false;
			}
			if(this.getProfile().isTransient()) {
				this.setRemoveOperation(true);
				this.saveGiftListItemInSession(pRequest);
			} else {

				if (this.validateGiftlistId(pRequest, pResponse)) {
					this.removeItemsFromGiftlist(pRequest, pResponse);
				}
			}
		}catch (final CommerceException oce) {
			this.processException(oce, this.getLblTxtTemplateManager().getErrMsg("err_giftlist_remove_items",pRequest.getLocale().getLanguage(),null, null), pRequest,pResponse);
			this.setRollbackTransaction(true);
		}
		if(!this.getFormError()){
			this.mOmnitureStatus = BBBCoreConstants.REMOVED;
		}
		return this.checkFormRedirect(this.getRemoveItemsFromGiftlistSuccessURL(), this.getRemoveItemsFromGiftlistErrorURL(), pRequest,
				pResponse);
	}

	/**
	 * Removes the given items to the selected giftlist.
	 *
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @exception ServletException if there was an error while executing the code
	 * @exception IOException if there was an error with servlet io
	 * @exception CommerceException if there was an error with Commerce
	 */
	protected void removeItemsFromGiftlist(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException {

		if(this.getProfile().isTransient())
		{
			this.saveGiftListItemInSession(pRequest);
		}else
		{
		final GiftlistManager mgr = this.getGiftlistManager();
		final String pGiftlistId = this.getGiftlistId();
		final String[] items = this.getRemoveGiftitemIds();

		if (items == null) {
			return;
		}

		try {
			for (final String id : items) {
				mgr.removeItemFromGiftlist(pGiftlistId, id);
			}
		} catch (final RepositoryException ex) {
			this.processException(ex, MSG_ERROR_UPDATING_GIFTLIST_ITEMS, pRequest, pResponse);
		} catch (final InvalidGiftTypeException ige){
			this.logError(LogMessageFormatter.formatMessage(pRequest, "InvalidGiftTypeException in StoreGiftlistFormHandler while removeItemsFromGiftlist : Item not found", BBBCoreErrorConstants.ACCOUNT_ERROR_1251 ), ige);
		}}
	}
	
	/**
	* This method handles the edit of Personalized gift list item. From the response of katori, 
	* get all the details and save this in Gift List Repository. Edit functionality is only for logged-in user.
    *
    * @param pRequest
    * @param pResponse
    * @return boolean
    * @throws ServletException
    * @throws IOException */
   public final boolean handleEditPersonalisedGiftItem(final DynamoHttpServletRequest pRequest,
                   final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	   this.logDebug("StoreGiftlistFormHandler ::handleEditPersonalisedGiftItem method -  start ");
	  // String siteId = SiteContextManager.getCurrentSiteId();
	   if (StringUtils.isNotEmpty(getFromPage())) {
		   setPersonalizedEditItemFormsSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
		   setPersonalizedEditItemFormsErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
		}
       
	   //validate Giftlist id if this exists in Repository or not
	   if (!this.validateGiftlistId(pRequest, pResponse)) {
			this.addFormException((new DropletException(this.getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.CATALOG_ERROR_1006, pRequest.getLocale().getLanguage(), null, null), BBBCoreErrorConstants.CATALOG_ERROR_1006)));
		}
	   if (!this.checkFormRedirect(null, this.getPersonalizedEditItemFormsErrorURL(), pRequest, pResponse)) {
			return false;
	    }
	   //This flow is only for logged-in user
	   if(!this.getProfile().isTransient()){
		   BBBGiftlistManager giftListMgr = (BBBGiftlistManager)this.getGiftlistManager();
		   GiftListVO giftListVO = new GiftListVO();
		   try{
			   giftListVO.setWishListItemId(this.getCurrentItemId());
			   //set personalized details in giftlist VO
			   setPersonalizedItemProperties(giftListVO);
			   
			   //set personalized details in Repository
			   giftListMgr.updatePersonlizedItemDetails(giftListVO);
			   
		   	}catch (final CommerceException e) {
		   		this.processException(e, this.getLblTxtTemplateManager().getErrMsg("err_giftlist_update_items", pRequest.getLocale().getLanguage() + e.getMessage(), null, null), pRequest, pResponse);
        	
   			}catch (final RepositoryException e) {
   				this.processException(e, this.getLblTxtTemplateManager().getErrMsg("err_giftlist_update_items", pRequest.getLocale().getLanguage(), null, null) + e.getMessage(), pRequest, pResponse);
   			}
	   }
	   return this.checkFormRedirect(this.getPersonalizedEditItemFormsSuccessURL(), this.getPersonalizedEditItemFormsErrorURL(), pRequest,
               pResponse);
   }
   
   /**
	 * The handle method is used for REST framework to update wish list items
	 * The method gets all the wishlist items added by a user
	 * If the wishlist item is other than the one chosen by the user to update then add as a request paarmeter the
	 * wishlist item id as key and the value in the quantity desired property as value
	 * For the wishlist item that the user wants to update teh value from quantityToUpdate
	 * property will be added inthe request
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */

	public boolean handleUpdateAllGiftListItems(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("StoreGiftlistFormHandler ::handleUpdateWishlistItems getting wish list for profile with email "
				+ this.getProfile().getPropertyValue("email"));
		final String giftListId = ((RepositoryItem) this.getProfile()
				.getPropertyValue(WISH_LIST)).getRepositoryId();
		this.logDebug("handleUpdateWishlistItems: Wish List id associated with the profile "
				+ giftListId);
		this.setGiftlistId(giftListId);
		mCurrentItemId = pRequest.getParameter("currentItemId");
		GiftlistManager mgr;
		List items;
		boolean isValidItem = false;
		mgr = this.getGiftlistManager();
		items = mgr.getGiftlistItems(giftListId);
		if ((items != null) && (items.size() > 0)) {
			for (String currentItems : this.getCurrentItemId().split(";")) {
				for (int i = 0; i < items.size(); i++) {
					String itemId;
					final RepositoryItem item = (RepositoryItem) items.get(i);
					itemId = (String) item.getPropertyValue(ID);
					if (currentItems.equalsIgnoreCase(itemId)) {
						isValidItem = true;
					}
				}
				if (!isValidItem) {
					this.addFormException((new DropletException(
							"Not a valid item Id",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10161)));
					return false;
				}
				this.logDebug("current wishlist item to update "
						+ this.getCurrentItemId() + " with quantity "
						+ this.getQuantityToUpdate());

			}
		}
		if ((this.getCurrentItemId() != null)) {
			for (String currentItems : this.getCurrentItemId().split(";")) {
				pRequest.setParameter(currentItems, 0);

			}
		}

		return handleUpdateGiftlistItems(pRequest, pResponse);
	}
	
	
	public boolean handleUpdateWishlistItems(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException{

		this.logDebug("StoreGiftlistFormHandler ::handleUpdateWishlistItems getting wish list for profile with email "+this.getProfile().getPropertyValue("email"));
		String channel = BBBUtility.getChannel();
		// Set below attribute to create SFL cookie in order property customizer
        if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
        	pRequest.setAttribute(BBBCoreConstants.SEND_SFL_COOKIE, Boolean.TRUE);
		}
		
		final String giftListId=((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST)).getRepositoryId();
		final String reqWishlistItemId = this.getCurrentItemId();
		this.logDebug("handleUpdateWishlistItems: Wish List id associated with the profile "+giftListId + " , requested wish list item id: " + reqWishlistItemId + " and quantity to update: " + this.getQuantityToUpdate());
		
		this.setGiftlistId(giftListId);
		GiftlistManager mgr;
		List items;
		boolean isValidItem = false;
		mgr = this.getGiftlistManager();
		items = mgr.getGiftlistItems(giftListId);
		if((items != null) && (items.size() > 0)){
			for(int i=0;i<items.size();i++){
				String itemId;
				long quantityDesired=0;
				final RepositoryItem item = (RepositoryItem)items.get(i);
				itemId = (String)item.getPropertyValue(ID);
				if(((Long)item.getPropertyValue("quantityDesired")).longValue()!=0) {
					quantityDesired = ((Long)item.getPropertyValue("quantityDesired")).longValue();
				}
				this.logDebug("handleUpdateWishlistItems method wishlist item id "+itemId+" quantity Desired for the item id "+quantityDesired);
				if(!this.getCurrentItemId().equalsIgnoreCase(itemId))	{
					pRequest.setParameter(itemId, quantityDesired);
				}
				if(this.getCurrentItemId().equalsIgnoreCase(itemId)){
					isValidItem = true;
				}
			}
			if(!isValidItem){
				this.addFormException((new DropletException("Not a valid item Id",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10161)));
				return false;
			}
			this.logDebug("current wishlist item to update "+this.getCurrentItemId()+" with quantity "+this.getQuantityToUpdate());
		}
		if((this.getCurrentItemId() != null) && (this.getQuantityToUpdate() != null))
		{
            pRequest.setParameter(this.getCurrentItemId(), this.getQuantityToUpdate());
		}
		final boolean status = this.handleUpdateGiftlistItems(pRequest, pResponse);
		/***
		 * BBBP-9708 :On cart page, If user update the quantity of a product in
		 * SFL section, AJAX call is generated which updates the
		 * quantity but does not update the out of stock message & move to cart
		 * button is not disabled.
		 */
		this.logDebug("Check for Out of stock...");
		final BBBSavedItemsSessionBean savedItemsSessionBean = this.getSavedItemsSessionBean();
		final List<GiftListVO> savedList = savedItemsSessionBean.getSaveItems(true);
		if (savedList != null) {
			for (final GiftListVO giftListVO : savedList) {
				if (giftListId != null && reqWishlistItemId != null) {
					if (BBBUtility.isEmpty(giftListVO.getRegistryID()) && giftListVO.getPriceMessageVO() != null
							&& reqWishlistItemId.equalsIgnoreCase(giftListVO.getWishListItemId())
							&& (!giftListVO.getPriceMessageVO().isInStock() || giftListVO.getPriceMessageVO().isFlagOff())) {
						this.setOutOfStockFlag(true);
						pRequest.setParameter(BBBCoreConstants.OUT_OF_STOCK_FLAG, this.isOutOfStockFlag());
						break;
					}
				}
			}
		}

		return status;
	}
	/**
	 * Updates the given items to the selected giftlist.
	 *
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @exception ServletException if there was an error while executing the code
	 * @exception IOException if there was an error with servlet io
	  */
	@Override
	public boolean handleUpdateGiftlistItems(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
 throws ServletException, IOException {
		
			this.logDebug(" StoreGiftlistFormHandler :: handleUpdateGiftlistItems start");
		
		this.addContextPath(pRequest.getContextPath());
		GiftlistManager mgr;
		final String quantity = null;
		final List<GiftListVO> items = this.getWishlistManager().getSavedItemsSessionBean().getItems();
		mgr = this.getGiftlistManager();
		//String siteId = SiteContextManager.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			StringBuffer successURL = new StringBuffer(BBBCoreConstants.BLANK);
			StringBuffer errorURL = new StringBuffer(BBBCoreConstants.BLANK);
			successURL.append(pRequest.getContextPath()).append(
					getSuccessUrlMap().get(getFromPage()));
			errorURL.append(pRequest.getContextPath()).append(
					getErrorUrlMap().get(getFromPage()));
			setUpdateGiftlistItemsSuccessURL(successURL.toString());
			setUpdateGiftlistItemsErrorURL(errorURL.toString());
		}

		if (this.getProfile().isTransient()) {
			try {
				this.setUpdateOperation(true);
				// Rest code change for cart page
				this.setFromCartPage(true);
				this.saveGiftListItemInSession(pRequest);
			} catch (final CommerceItemNotFoundException e) {
				if (this.isLoggingError()) {
					this.logError(LogMessageFormatter.formatMessage(pRequest, "CommerceItemNotFoundException in StoreGiftlistFormHandler while handleAddItemListToGiftlist", BBBCoreErrorConstants.ACCOUNT_ERROR_1259), e);
				}
				this.setRollbackTransaction(true);
			} catch (final InvalidParameterException e) {
				if (this.isLoggingError()) {
					this.logError(LogMessageFormatter.formatMessage(pRequest, "InvalidParameterException in StoreGiftlistFormHandler while handleAddItemListToGiftlist", BBBCoreErrorConstants.ACCOUNT_ERROR_1260), e);
				}
				this.setRollbackTransaction(true);
			}
		} else if ((items != null) && (items.size() > 0)) {
			super.setRemoveGiftitemIds(null);
			this.preUpdateGiftlistItems(pRequest, pResponse);
			if (!this.validateGiftlistId(pRequest, pResponse)) {
				this.addFormException((new DropletException(this.getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.CATALOG_ERROR_1006, pRequest.getLocale().getLanguage(), null, null), BBBCoreErrorConstants.CATALOG_ERROR_1006)));
			}
			if (!this.checkFormRedirect(null, this.getUpdateGiftlistItemsErrorURL(), pRequest, pResponse)) {
				return false;
			}
			try {
				// Parse JSON into giftListID, qty, wishListItems

				this.updateGiftlistItems(pRequest, pResponse);

			} catch (final CommerceException e) {
				this.processException(e, this.getLblTxtTemplateManager().getErrMsg("err_giftlist_update_items", pRequest.getLocale().getLanguage(), null, null), pRequest, pResponse);
			}
			if (!this.checkFormRedirect(null, this.getUpdateGiftlistItemsErrorURL(), pRequest, pResponse)) {
				return false;
			}
			try {
				this.postUpdateGiftlistItems(pRequest, pResponse);
			} catch (final Exception e) {
				if (this.isLoggingError()) {
					this.logError(LogMessageFormatter.formatMessage(pRequest, "InvalidGiftTypeException in StoreGiftlistFormHandler while handleUpdateGiftlistItems", BBBCoreErrorConstants.ACCOUNT_ERROR_1252), e);
				}
				this.addFormException((new DropletException(e.getMessage(), BBBCoreErrorConstants.ACCOUNT_ERROR_1252)));
			}
		}

		if (!this.getFormError()) {
			this.mOmnitureStatus = BBBCoreConstants.UPDATED;
		}

		if (this.isLoggingDebug()) {
			this.logDebug(" StoreGiftlistFormHandler :: handleUpdateGiftlistItems end");
		}
		return this.checkFormRedirect(this.getUpdateGiftlistItemsSuccessURL(), this.getUpdateGiftlistItemsErrorURL(), pRequest, pResponse);
	}

	@Override
	protected void updateGiftlistItems(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException {
	
            this.logDebug(" StoreGiftlistFormHandler :: updateGiftlistItems start");
		
		this.addContextPath(pRequest.getContextPath());
		GiftlistManager mgr;
		String pGiftlistId;
		List items;
		mgr = this.getGiftlistManager();
		pGiftlistId = this.getGiftlistId();
		items = mgr.getGiftlistItems(pGiftlistId);
		if((items != null) && (items.size() >0)){
			for(int i=0;i<items.size();i++){
				String itemId;
				long quantity;
				final RepositoryItem item = (RepositoryItem)items.get(i);
				itemId = (String)item.getPropertyValue(ID);
				final String upquant = pRequest.getParameter(itemId);
				if(!StringUtils.isEmpty(upquant)){
					quantity = Long.parseLong(upquant.trim());
					this.setQuantity(quantity);
					this.setCurrentItemId(itemId);
					final Long qtDes = ((Long)item.getPropertyValue("quantityDesired")).longValue();
					if(qtDes == quantity){
						continue;
					}
				} else {
					continue;
				}
				this.logDebug(i+"th itemId "+itemId+" quantity applicable for the item id "+quantity+" current item id value for update by user "+this.getCurrentItemId());
				if(!this.getCurrentItemId().equalsIgnoreCase(itemId))	{
					quantity = -1;
				}
				if(quantity==0){
					try {
						mgr.removeItemFromGiftlist(pGiftlistId, this.getCurrentItemId());
					} catch (final RepositoryException e) {
						if (this.isLoggingError()) {
							this.logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException in StoreGiftlistFormHandler while updateGiftlistItems", BBBCoreErrorConstants.ACCOUNT_ERROR_1253 ), e);
						}
					}
				} else if(!(quantity > 0L)){
					if(((Long)item.getPropertyValue("quantityDesired")).longValue()!=0) {
						quantity = ((Long)item.getPropertyValue("quantityDesired")).longValue();
					}
					//quantity = Long.valueOf((Long)item.getPropertyValue("quantityDesired")).longValue();
				} else if(quantity > 0L){
					//pRequest.setAttribute(id, new Long(quantity));
					pRequest.setAttribute(itemId, Long.valueOf(quantity));
				}
				this.logDebug(i+ "th quantity to be updated "+quantity+" for item id "+itemId);
				mgr.setGiftlistItemQuantityDesired(pGiftlistId, itemId, quantity);
				
		            this.logDebug(" StoreGiftlistFormHandler :: updateGiftlistItems end");
				
			}
		}
	}

	@Override
	protected void updateOrder(final CommerceItem pItem, final long pQuantity, final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws InvalidGiftQuantityException, IOException, ServletException
	{	this.addContextPath(pRequest.getContextPath());
		super.updateOrder(pItem, pItem.getQuantity(), pRequest, pResponse);
	}

	/**
	 * Before moving item to the giftlist check if user is explicitly logged in.
	 * If not store product\sku\giftlist info into the session.
	 *
	 */

	@Override
	public boolean handleMoveItemsFromCart(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, CommerceException {
		if(!(((this.getMoveItemsFromCartLoginURL() != null) && this.getMoveItemsFromCartLoginURL().equalsIgnoreCase(REST_REDIRECT)) || ((this.getMoveItemsFromCartErrorURL() != null) && this.getMoveItemsFromCartErrorURL().equalsIgnoreCase(REST_REDIRECT)))){
			this.addContextPath(pRequest.getContextPath());
		}
		String siteId = SiteContextManager.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			
			StringBuffer appendData = new StringBuffer("");
			if(StringUtils.isNotEmpty(getSuccessQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getSuccessQueryParam());
			}
			StringBuffer successURL = new StringBuffer("");
			StringBuffer errorURL = new StringBuffer("");
			successURL.append(pRequest.getContextPath())
					.append(getSuccessUrlMap().get(getFromPage()))
					.append(appendData);
			errorURL.append(pRequest.getContextPath())
					.append(getErrorUrlMap().get(getFromPage()));

			setMoveItemsFromCartLoginURL(successURL.toString());
			setMoveItemsFromCartErrorURL(errorURL.toString());
		}

		String channel = BBBUtility.getChannel();
		// Set below attribute to create SFL cookie in order property customizer
        if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
        	pRequest.setAttribute(BBBCoreConstants.SEND_SFL_COOKIE, Boolean.TRUE);
		}
		if(this.getUndoComItemId() != null){
			this.setCurrentItemId(this.getUndoComItemId());
		}
		if (this.getProfile().isTransient()) {

			final List itemsInCart = this.getOrder().getCommerceItems();
			if ((itemsInCart != null) && !itemsInCart.isEmpty()) {
				try {
					this.setAddOperation(true);
					this.setMoveOperation(true);
					removeServiceFromCommItem();
					this.saveGiftListItemInSession(pRequest);
					final MutableRepositoryItem userProfile = (MutableRepositoryItem) ServletUtil.getCurrentUserProfile();
					final String loginFrom = BBBWebServiceConstants.TXT_LOGIN_WISHLIST;

					userProfile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, loginFrom);
					
					synchronized (this.getOrder()) {
						this.removeEmptyShippingGroups(pRequest);
						this.getOrderManager().updateOrder(this.getOrder());
					}

				} catch (final CommerceItemNotFoundException e) {
					if (this.isLoggingError()) {
						this.logError(LogMessageFormatter.formatMessage(pRequest, "CommerceItemNotFoundException in StoreGiftlistFormHandler while handleMoveItemsFromCart", BBBCoreErrorConstants.ACCOUNT_ERROR_1254), e);
					}
					this.addFormException((new DropletException(e.getMessage(), BBBCoreErrorConstants.ACCOUNT_ERROR_1254)));
					this.setRollbackTransaction(true);

				} catch (final InvalidParameterException e) {
					if (this.isLoggingError()) {
						this.logError(LogMessageFormatter.formatMessage(pRequest, "InvalidParameterException in StoreGiftlistFormHandler while handleMoveItemsFromCart", BBBCoreErrorConstants.ACCOUNT_ERROR_1255), e);
					}
					this.addFormException((new DropletException(e.getMessage(), BBBCoreErrorConstants.ACCOUNT_ERROR_1255)));
					this.setRollbackTransaction(true);
				}
			}
		}else {
			//BBBP-5859 changes start
			if (!BBBUtility.siteIsTbs(siteId) && ((BBBProfileTools) getProfileTools()).isRecognizedUser(pRequest, getProfile())) {
				try {
					CommerceItem item = getOrder().getCommerceItem(getCurrentItemId());
					String catalogRefId = null;
					if (item != null) {
						catalogRefId = item.getCatalogRefId();
					}
					checkWishListItemCount(pRequest, catalogRefId);
				} catch (BBBBusinessException be) {
					logError(be);
					addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.ERROR_SFL_MAX_REACHED, pRequest.getLocale().getLanguage(), null, null), 
							BBBCatalogErrorCodes.ERROR_SFL_MAX_REACHED));
					return 	checkFormRedirect(this.getMoveItemsFromCartLoginURL(), this.getMoveItemsFromCartErrorURL(), pRequest, pResponse);
				}
			}
			//BBBP-5859 changes end
			this.setMoveOperation(true);
			String skuId = null;
			if((this.getItemIds() == null) || ((this.getItemIds().length == 0) && (this.getCurrentItemId() != null))){
				final Order holder = this.getOrder();
				final List list = holder.getCommerceItems();
				final Iterator itr = list.iterator();

				while (itr.hasNext()) {
					final CommerceItem item = (CommerceItem) itr.next();
					if(this.getCurrentItemId().equalsIgnoreCase(item.getId())){
						this.setItemIds(new String[]{this.getCurrentItemId()});
						skuId = item.getCatalogRefId();
						if(this.getQuantity() == 0) {
							this.setQuantity(item.getQuantity());
						}
					}
				}
				if(this.getItemIds() == null) {
					this.addFormException((new DropletException("Invalid Commerce Item","invalid_commerceItem_id")));
					return 	this.checkFormRedirect(this.getMoveItemsFromCartLoginURL(), this.getMoveItemsFromCartErrorURL(), pRequest, pResponse);
				}
			}
			synchronized (this.getOrder()) {
				this.setGiftlistId((String) ((RepositoryItem) this.getProfile()
						.getPropertyValue(WISH_LIST)).getPropertyValue(ID));
				removeServiceFromCommItem();				
				super.handleMoveItemsFromCart(pRequest, pResponse);
				//this.removeEmptyStorePickupSG();
				this.removeEmptyShippingGroups(pRequest);
				this.getOrderManager().updateOrder(this.getOrder());
				}

			}
			if(this.isFromCartPage()){
				this.setMoveOperation(false);
			}
			return 	this.checkFormRedirect(this.getMoveItemsFromCartLoginURL(), this.getMoveItemsFromCartErrorURL(), pRequest, pResponse);
	}

	/**
	 * 
	 */
	private void removeServiceFromCommItem() {
		BaseCommerceItemImpl cItem = null;
		 
		try {
			cItem = (BaseCommerceItemImpl) getOrder().getCommerceItem(getCurrentItemId());
		} catch (CommerceItemNotFoundException | InvalidParameterException e1) {
			if(isLoggingError()){
				logError("CommerceItem not found while deleting the service "+e1,e1);
			}
		}
	
		if(cItem.isPorchService()){		
		try {
			cItem.setPorchServiceRef(null);
			cItem.setPorchService(false);
			getSessionBean().setRegistryPorchServiceRemoved(true);
		} catch (Exception e) {
			if(isLoggingError()){ 
			logDebug(" error while removing proch service ref from commerce item "+e,e);
			}
			 
		}
		}
	}

	/**
	 * Wrapper method for rest service used to add wishlist item from cart
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException
	 */
	public boolean handleMoveItemsToWishListFromCart(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, CommerceException,BBBSystemException {
		if (this.getProfile().isTransient())
		{
			throw new BBBSystemException("UNAUTHORIZED ACCESS:Profile not logged in");
		}
		final String moveItemId=this.getItemIdToMove();
		pRequest.setContextPath(null);
		final String arrayItems[]={moveItemId};
		this.setItemIds(arrayItems);
		return this.handleMoveItemsFromCart(pRequest,pResponse);

	}

	@SuppressWarnings("unchecked")
	public void removeEmptyStorePickupSG() throws CommerceException {

		final Order order = this.getOrder();
		final List<ShippingGroup> shippingGroups = order.getShippingGroups();
		if((null != shippingGroups) && !shippingGroups.isEmpty()){
			final ArrayList<String> removalList = new ArrayList(shippingGroups.size());
			for (final Object element : shippingGroups) {
				final ShippingGroup sg = (ShippingGroup) element;
				if(sg instanceof BBBStoreShippingGroup){
					if(sg.getCommerceItemRelationshipCount() == 0){
						removalList.add(sg.getId());
					}
				}
			}
			if(!removalList.isEmpty()){
				for (final String shippingGroupId : removalList) {
					this.getShippingGroupManager().removeShippingGroupFromOrder(order, shippingGroupId);
				}
			}

		}


	}


	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException
	 */
	public boolean handleMoveItemsToCart(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, CommerceException {
		this.addContextPath(pRequest.getContextPath());
		if (this.getProfile().isTransient()) {

			final List itemsInCart = this.getOrder().getCommerceItems();
			if(itemsInCart != null){
				try {
					this.setMoveOperation(true);
					this.saveGiftListItemInSession(pRequest);
					final MutableRepositoryItem userProfile=(MutableRepositoryItem) ServletUtil.getCurrentUserProfile();
					final String loginFrom=BBBWebServiceConstants.TXT_LOGIN_WISHLIST;

					userProfile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, loginFrom);

				} catch (final CommerceItemNotFoundException e) {
					if (this.isLoggingError()) {
						this.logError(LogMessageFormatter.formatMessage(pRequest, "CommerceItemNotFoundException in StoreGiftlistFormHandler while handleMoveItemsFromCart", BBBCoreErrorConstants.ACCOUNT_ERROR_1254 ), e);
					}
					this.addFormException((new DropletException(e.getMessage(),BBBCoreErrorConstants.ACCOUNT_ERROR_1254)));
					this.setRollbackTransaction(true);

				} catch (final InvalidParameterException e) {
					if (this.isLoggingError()) {
						this.logError(LogMessageFormatter.formatMessage(pRequest, "InvalidParameterException in StoreGiftlistFormHandler while handleMoveItemsFromCart", BBBCoreErrorConstants.ACCOUNT_ERROR_1255 ), e);
					}
					this.addFormException((new DropletException(e.getMessage(),BBBCoreErrorConstants.ACCOUNT_ERROR_1255)));
					this.setRollbackTransaction(true);
				}
			}
		}else {
			synchronized (this.getOrder()) {
				this.setGiftlistId((String) ((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST)).getPropertyValue(ID));

				this.removeEmptyStorePickupSG();
				this.getOrderManager().updateOrder(this.getOrder());
				}
			}
			return 	this.checkFormRedirect(this.getMoveItemsFromCartLoginURL(), this.getMoveItemsFromCartErrorURL(), pRequest, pResponse);
	}


	/* (non-Javadoc)
	 * @see atg.commerce.gifts.GiftlistFormHandler#moveItemsFromCart(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void moveItemsFromCart(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException,
 CommerceException {

       if(!(((this.getMoveItemsFromCartLoginURL() != null) && this.getMoveItemsFromCartLoginURL().equalsIgnoreCase(REST_REDIRECT)) || ((this.getMoveItemsFromCartErrorURL() != null) && this.getMoveItemsFromCartErrorURL().equalsIgnoreCase(REST_REDIRECT)))){
    	   this.addContextPath(pRequest.getContextPath());
		}

		final BBBSavedItemsSessionBean savedItemsSessionBean = this.getSavedItemsSessionBean();
		List<GiftListVO> voitem = savedItemsSessionBean.getItems();
		int beforeSize = 0;
		int afterSize = 0;
		if ((voitem != null) && (voitem.size() > 0)) {
			beforeSize = voitem.size();
		}
		try {
			// BBBSL-2735. Added synchronization while updating the order.
			synchronized(this.getOrder()) {
			final List<BBBCommerceItem> items = new CopyOnWriteArrayList<BBBCommerceItem>(this.getShoppingCart().getCurrent().getCommerceItems());
			String item = null;
			if (items != null) {
				final Iterator<BBBCommerceItem> itemIterator = items.iterator();
				while (itemIterator.hasNext()) {
					final CommerceItem comItem = itemIterator.next();
					if (!(comItem instanceof BBBCommerceItem)) {
						continue;
					}
					final BBBCommerceItem bbbcomItem = (BBBCommerceItem) comItem;
					final String productId = bbbcomItem.getAuxiliaryData().getProductId();
					
					String displayName = null;
					String description = null;

					BBBCommerceItem itemToBeRemoved = null;
					final String movedComItemId = ((BBBCommerceItemManager) this.getOrderManager().getCommerceItemManager()).getCommerceItemMoved();
					if (movedComItemId != null) {
						final String temp[] = movedComItemId.split(BBBCoreConstants.COMMA);
						String storeId = null;
						if (bbbcomItem.getStoreId() == null) {
							storeId = BBBCoreConstants.NULL_VALUE;
						} else {
							storeId = bbbcomItem.getStoreId();
						}
						if (bbbcomItem.isItemMoved() || (temp[0].equalsIgnoreCase(bbbcomItem.getAuxiliaryData().getProductId()) && temp[1].equalsIgnoreCase(storeId))) {
							this.setItemIds(new String[] { bbbcomItem.getId() });
							this.setQuantity(bbbcomItem.getQuantity());
							((BBBCommerceItemManager) this.getOrderManager().getCommerceItemManager()).setCommerceItemMoved(null);
						}
					}

					if (this.fromWishlist && this.getCurrentItemId().equalsIgnoreCase(bbbcomItem.getId())) {
						// use catalog tools for getting product and sku info
						try {
							
							ProductVO productVO = null;
							
							String ltlShipMethod = bbbcomItem.getLtlShipMethod();
							productVO = this.getCatalogTool().getProductDetails(this.getSiteContext().getSite().getId(), productId);
							if (productVO != null) {
								displayName = productVO.getName();
								description = productVO.getShortDescription();
							}
							if((BBBCoreConstants.TRUE).equalsIgnoreCase(bbbcomItem.getWhiteGloveAssembly())){
								ltlShipMethod = BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD;
							}
							
							//Create gift list VO
							GiftListVO giftListVO = createGiftListVO(bbbcomItem, productId, ltlShipMethod, bbbcomItem.getQuantity());
							
							item = ((BBBGiftlistManager) this.getGiftlistManager()).createGiftlistItem(giftListVO, displayName, description, this.getSiteContext().getSite().getId());
							
							this.getGiftlistManager().addItemToGiftlist(this.getGiftlistId(), item);
							// update order quantity
							itemToBeRemoved = bbbcomItem;
							super.updateOrder(itemToBeRemoved, bbbcomItem.getQuantity(), pRequest, pResponse);
						} catch (final RepositoryException e1) {
							this.logError("Repository exception while removing moving from Wishlist in moveItemsFromCart", e1);
						} catch (final BBBSystemException e) {
							this.logError("BBBSystemException exception while moving items from Wishlist in moveItemsFromCart", e);
						} catch (final BBBBusinessException e) {
							this.logError("BBBBusinessException exception while moving items from Wishlist in moveItemsFromCart", e);
						}
					}
				}
			}
			if (!this.fromWishlist && (this.getItemIds().length == 1)) {
				final BBBGiftlistManager mgr = (BBBGiftlistManager) this.getGiftlistManager();
				
				String displayName = null;
				String description = null;
				String siteId = null;
				long quantity = 0;
				try {
					BBBCommerceItem comItem = null;
					
					for (int c = 0; c < this.getItemIds().length; c++) {
						String commerceItemId = null;
						if (this.getItemIds()[c] == null) {
							commerceItemId = this.getCurrentItemId();
						} else {
							commerceItemId = this.getItemIds()[c];
						}
						comItem = (BBBCommerceItem) this.getOrder().getCommerceItem(commerceItemId);
						final String skuId = comItem.getCatalogRefId();
						if (skuId == null) {
							return;
						}

						final RepositoryItem sku = this.getCatalogTools().findSKU(skuId, comItem.getCatalogKey());
						quantity = this.getQuantity(commerceItemId, pRequest, pResponse);
						if (quantity == 0) {
							quantity = comItem.getQuantity();
						}
						String productId = comItem.getAuxiliaryData().getProductId();
						if (productId == null) {
							return;
						}

						siteId = comItem.getAuxiliaryData().getSiteId();
						final RepositoryItem product = this.getCatalogTools().findProduct(productId, comItem.getCatalogKey());
						if (product != null) {
							productId = product.getRepositoryId();
							displayName = (String) product.getPropertyValue(mgr.getGiftlistTools().getDisplayNameProperty());
							description = (String) product.getPropertyValue(mgr.getGiftlistTools().getDescriptionProperty());
						}
						// if item is in giftlist, increment quantity otherwise
						// add
						String giftId = null;
						if((BBBCoreConstants.TRUE).equalsIgnoreCase(comItem.getWhiteGloveAssembly())){
							comItem.setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD);
						}
						
						//Create gift list VO
						GiftListVO giftListVO = createGiftListVO(comItem, productId, comItem.getLtlShipMethod(), quantity);
						
						if (StringUtils.isEmpty(comItem.getRegistryId()) && this.isMoveOperation) {
							giftId = mgr.getGiftlistItemId(this.getGiftlistId(), skuId, productId, siteId, comItem.getRegistryId(), comItem.getLtlShipMethod(),comItem.getReferenceNumber());
						}
						if (quantity == this.QUANTITY_NOT_VALID) {
							this.addFormException(MSG_INVALID_GIFTLIST_QUANTITY, pRequest, pResponse);
							return;
						}
						if ((giftId != null) && (this.isMoveOperation()) && StringUtils.isEmpty(comItem.getRegistryId())) {
							mgr.increaseGiftlistItemQuantityDesired(this.getGiftlistId(), giftId, quantity);
							this.setItemMoveFromCartID(giftId);
						} else {
							String itemId = null;
							if ((this.getCountNo() > 0) && this.isUndoOpt) {
								final MutableRepository mutRep = (MutableRepository) this.getGiftlistRepository();
								final MutableRepositoryItem giftlist = mutRep.getItemForUpdate(this.getGiftlistId(), this.getGiftlistManager().getGiftlistTools().getGiftlistItemDescriptor());
								final List<RepositoryItem> value = (List<RepositoryItem>) giftlist.getPropertyValue(this.getGiftlistManager().getGiftlistTools().getGiftlistItemsProperty());
								List<RepositoryItem> giftlistItems = null;
								if ((value != null) && (value.size() > 0)) {
									final Iterator<RepositoryItem> itemList = value.iterator();
									giftlistItems = new ArrayList<RepositoryItem>();
									while (itemList.hasNext()) {
										final RepositoryItem checkItem = itemList.next();
										final String itemsiteId = (String) checkItem.getPropertyValue(BBBCoreConstants.SITE_ID);
										if ((itemsiteId != null) && itemsiteId.equalsIgnoreCase(SiteContextManager.getCurrentSiteId())) {
											giftlistItems.add(checkItem);
										}
									}
								} else {
									giftlistItems = new ArrayList<RepositoryItem>();
								}

								itemId = mgr.createGiftlistItem(giftListVO, displayName, description, siteId);
								int size = giftlistItems.size();
								int finalCount = size + 1;
								int i = 1;
								while (size > 0) {
									if (i == this.getCountNo()) {
										finalCount = size;
										break;
									}
									size--;
									i++;
								}
								if (this.isUndoOpt) {
									this.setItemIdJustMvBack(itemId);
								}
								if (finalCount > giftlistItems.size()) {
									giftlistItems.add(0, mgr.getGiftitem(itemId));
								} else {
									giftlistItems.add(finalCount, mgr.getGiftitem(itemId));
								}
								giftlist.setPropertyValue(this.getGiftlistManager().getGiftlistTools().getGiftlistItemsProperty(), giftlistItems);
								mutRep.updateItem(giftlist);
								final MutableRepositoryItem giftItem = (MutableRepositoryItem) mgr.getGiftitem(itemId);
								final MutableRepository mutrep = (MutableRepository) this.getGiftlistRepository();
								giftItem.setPropertyValue(BBBCoreConstants.MSGSHOWNOOS, true);
								giftItem.setPropertyValue(BBBCoreConstants.REGISTRY_ID, comItem.getRegistryId());
								giftItem.setPropertyValue(BBBCoreConstants.IS_BTS, this.isBts());
								if(comItem.getPriceInfo()!=null){
									if (comItem.getPriceInfo().isOnSale()) {
										giftItem.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, comItem.getPriceInfo().getSalePrice());
									} else {
										giftItem.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, comItem.getPriceInfo().getListPrice());
									}
								}
								mutrep.updateItem(giftItem);
							} else if (!StringUtils.isEmpty(comItem.getRegistryId())) {
								final List itemsList = mgr.getGiftlistItems(this.getGiftlistId());
								if (itemsList != null) {
									final Iterator iterator = itemsList.iterator();

									while (iterator.hasNext()) {
										final RepositoryItem itemGift = (RepositoryItem) iterator.next();
										final String regId = (String) itemGift.getPropertyValue(BBBCoreConstants.REGISTRY_ID);
										if (((regId != null) && regId.equalsIgnoreCase(BBBCoreConstants.BLANK)) || (regId == null)) {
											continue;
										}
										if ((itemGift.getPropertyValue(mgr.getGiftlistTools().getCatalogRefIdProperty()).equals(skuId)) && (itemGift.getPropertyValue(mgr.getGiftlistTools().getSiteProperty()).equals(SiteContextManager.getCurrentSiteId())) && comItem.getRegistryId().equalsIgnoreCase(regId))

										{
											itemId = ((String) itemGift.getPropertyValue(BBBCoreConstants.ID));
										}
									}
								}
								if (itemId == null) {
									itemId = mgr.createGiftlistItem(giftListVO, displayName, description, siteId);
									mgr.addItemToGiftlist(this.getGiftlistId(), itemId);
									final MutableRepositoryItem giftItem = (MutableRepositoryItem) mgr.getGiftitem(itemId);
									final MutableRepository mutrep = (MutableRepository) this.getGiftlistRepository();
									giftItem.setPropertyValue(BBBCoreConstants.MSGSHOWNOOS, true);
									giftItem.setPropertyValue(BBBCoreConstants.REGISTRY_ID, comItem.getRegistryId());
									giftItem.setPropertyValue(BBBCoreConstants.IS_BTS, this.isBts());
									if(comItem.getPriceInfo()!=null){
										if (comItem.getPriceInfo().isOnSale()) {
											giftItem.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, comItem.getPriceInfo().getSalePrice());
										} else {
											giftItem.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, comItem.getPriceInfo().getListPrice());
										}
									}
									mutrep.updateItem(giftItem);
								} else {
									mgr.increaseGiftlistItemQuantityDesired(this.getGiftlistId(), itemId, quantity);
								}
							} else {
								giftListVO.setRegistryID(null);
								itemId = mgr.createGiftlistItem(giftListVO , displayName, description, siteId);
								mgr.addItemToGiftlist(this.getGiftlistId(), itemId);
								final MutableRepositoryItem giftItem = (MutableRepositoryItem) mgr.getGiftitem(itemId);
								final MutableRepository mutrep = (MutableRepository) this.getGiftlistRepository();
								giftItem.setPropertyValue(BBBCoreConstants.MSGSHOWNOOS, true);
								giftItem.setPropertyValue(BBBCoreConstants.IS_BTS, this.isBts());
								giftItem.setPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD, comItem.getLtlShipMethod());
								if (comItem.getPriceInfo()!=null) {
									if (comItem.getPriceInfo().isOnSale()) {
										giftItem.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, comItem.getPriceInfo().getSalePrice());
									} else {
										giftItem.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, comItem.getPriceInfo().getListPrice());
									}
								}
								
								mutrep.updateItem(giftItem);
							}
							this.setItemMoveFromCartID(itemId);
						}
					}
					
					// LTL Changes | remove assembly charges and surcharge before adding LTL item in sfl
					//BBBHardGoodShippingGroup sg = null;
					String deliveryItemId = "";
					String assemblyItemId = "";
					MutableRepositoryItem ltlItemsAssoc;
					//String associd = "";
					if(comItem.isLtlItem()){
						deliveryItemId=comItem.getDeliveryItemId();
						assemblyItemId=comItem.getAssemblyItemId();
						/* sg = (BBBHardGoodShippingGroup) ((BBBShippingGroupCommerceItemRelationship) comItem
									.getShippingGroupRelationships().iterator()
									.next()).getShippingGroup();
						if(((BBBHardGoodShippingGroup)sg).getLTLItemMap() != null && !((BBBHardGoodShippingGroup)sg).getLTLItemMap().isEmpty()) {
							
							ltlItemsAssoc = (MutableRepositoryItem) ((BBBHardGoodShippingGroup)sg).getLTLItemMap().get(comItem.getId());
							associd = ltlItemsAssoc.getRepositoryId();
							if(ltlItemsAssoc.getPropertyValue("deliveryItemId") != null){
								deliveryItemId = (String) ltlItemsAssoc.getPropertyValue("deliveryItemId");
							}
							if(ltlItemsAssoc.getPropertyValue("assemblyItemId") != null){
								assemblyItemId = (String) ltlItemsAssoc.getPropertyValue("assemblyItemId");
							}
						}*/
					}
					
					final long qua = comItem.getQuantity() - quantity;
					if ((qua == 0) || (qua < 0)) {
						/*if(!BBBUtility.isEmpty(associd)){
							((BBBHardGoodShippingGroup)sg).getLTLItemMap().remove(comItem.getId());
							this.getOrderRepository().removeItem(associd, "ltlItemAssoc"); 
						}*/
						
						getCommerceItemManager().removeItemFromOrder(getOrder(), comItem.getId());
						//identify surcharge SKU and assemebly sku associated with comItem and do the following for both.
						if(!BBBUtility.isEmpty(deliveryItemId)){
							comItem.setDeliveryItemId(null);
							getCommerceItemManager().removeItemFromOrder(getOrder(), deliveryItemId);
						}
						if(!BBBUtility.isEmpty(assemblyItemId)){
							comItem.setAssemblyItemId(null);
							getCommerceItemManager().removeItemFromOrder(getOrder(), assemblyItemId);
						}
						getOrderManager().updateOrder(getOrder());
						
					} else if (qua > 0) {
						LTLDeliveryChargeCommerceItem comItemSurcharge = null;
						LTLAssemblyFeeCommerceItem comItemAssembly = null; 
						
						this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(this.getOrder(), comItem, qua);
						
						if(!BBBUtility.isEmpty(deliveryItemId))
						{
							comItemSurcharge = (LTLDeliveryChargeCommerceItem) this.getOrder().getCommerceItem(deliveryItemId);
							this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(this.getOrder(), comItemSurcharge, qua);
							comItemSurcharge.setQuantity(qua);
						}
						if(!BBBUtility.isEmpty(assemblyItemId)) {
							comItemAssembly = (LTLAssemblyFeeCommerceItem) this.getOrder().getCommerceItem(assemblyItemId);
							this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(this.getOrder(), comItemAssembly, qua);
							comItemAssembly.setQuantity(qua);
						}
						
						comItem.setQuantity(qua);
						
					}
					
					this.setQuantity(quantity);
				} catch (final RepositoryException exc) {
					throw new CommerceException(exc);
				}
			}
		}
	}finally {
			voitem = savedItemsSessionBean.getItems();

			if ((voitem != null) && (voitem.size() > 0)) {
				afterSize = voitem.size();
			}
			if ((beforeSize < afterSize) && !this.isUndoOpt()) {
				this.setNewItemAdded(true);
			}
		}
	}

	/**
	 * Populates gift list VO 
	 * 
	 * @param bbbcomItem
	 * @param productId
	 * @param ltlShipMethod
	 * @param quantity 
	 * 
	 * @return GiftListVO
	 */
	private GiftListVO createGiftListVO(final BBBCommerceItem bbbcomItem,
			final String productId, String ltlShipMethod, long quantity) {

		GiftListVO giftListVO = new GiftListVO();
		giftListVO.setLtlShipMethod(ltlShipMethod);
		giftListVO.setSkuID(bbbcomItem.getCatalogRefId());
		giftListVO.setProdID(productId);
		giftListVO.setQuantity(quantity);
		giftListVO.setRegistryID(bbbcomItem.getRegistryId());
		giftListVO.setPrevPrice(bbbcomItem.getPrevPrice());
		giftListVO.setShipMethodUnsupported(Boolean.valueOf(bbbcomItem.isShipMethodUnsupported()));
		//BPSL-1963 | EXIM- Populate personalized item details
		if(BBBUtility.isNotEmpty(bbbcomItem.getReferenceNumber())){
			setPersonalizedItemProperties(giftListVO, bbbcomItem);
		}
		
		return giftListVO;
	}


	@Override
	public void postMoveItemsFromCart(
			final DynamoHttpServletRequest dynamohttpservletrequest,
			final DynamoHttpServletResponse dynamohttpservletresponse)
			throws ServletException, IOException {
		this.addContextPath(dynamohttpservletrequest.getContextPath());
		this.repriceCartMoveItemsFromCart();
		super.postMoveItemsFromCart(dynamohttpservletrequest,
				dynamohttpservletresponse);
	}

	private void repriceCartMoveItemsFromCart() {
		this.logDebug("Entering method StoreGiftListFormHandler.repriceCartMoveItemsFromCart()");
		final Map map = new HashMap();
		map.put("Order", this.getOrder());
		map.put("Profile", this.getProfile());
		map.put(PricingConstants.PRICING_OPERATION_PARAM,  PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING);
		try {
			this.getPipelineManager().runProcess("repriceOrder", map);
		} catch (final RunProcessException e) {
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(null, "RunProcessException in StoreGiftlistFormHandler while repriceCartMoveItemsFromCart", BBBCoreErrorConstants.ACCOUNT_ERROR_1256 ), e);
			}
		}
		this.logDebug("Exiting method StoreGiftListFormHandler.repriceCartMoveItemsFromCart()");
	}

	/**
	 * Saves GiftListItem & quantity In Session
	 *
	 * @param pRedirectURL
	 *            - the URL to redirect to after login
	 * @throws ServletException
	 *             if anything goes wrong
	 * @throws InvalidParameterException
	 * @throws CommerceItemNotFoundException
	 */

	private void saveGiftListItemInSession(final DynamoHttpServletRequest pRequest)
 throws ServletException, CommerceItemNotFoundException, InvalidParameterException {
		
			this.logDebug("CLS=[StoreGiftlistFormHandler] MTHD=[saveGiftListItemInSession starts]");
			this.logDebug("isAddOperation " + this.isAddOperation() +" isMoveOperation "+this.isMoveOperation()+" isRemoveOperation "+this.isRemoveOperation()+" isUpdateOperation "+this.isUpdateOperation);
		
		final Object obj = pRequest.getSession().getAttribute(BBBCoreConstants.ADDED);
		final BBBSavedItemsSessionBean savedItemsSessionBean = this.getSavedItemsSessionBean();
		List<GiftListVO> voitem = savedItemsSessionBean.getItems();
		int beforeSize = 0;
		int afterSize = 0;
		if((voitem != null) && (voitem.size() > 0)) {
			beforeSize = voitem.size();
		}
		try {
			if ((this.getProfile().isTransient() && (pRequest.getSession() != null)) || (this.isAddOperation() && (obj != null))) {

				final BBBSessionBean wishListBean = this.getSessionBean();
				wishListBean.setMoveCartItemToWishSuccessUrl(this.getMoveItemsFromCartSuccessURL());
				if ((this.getItemIds() != null) && (this.getItemIds().length > 0)) {
					final String commerceItemId = this.getItemIds()[0];
					wishListBean.setCommerceItemId(commerceItemId);
					final BBBCommerceItem item = (BBBCommerceItem) this.getOrder().getCommerceItem(commerceItemId);
					item.setItemMoved(true);

				}

				if ((this.getCatalogRefIds() != null) && (this.getCatalogRefIds().length > 0)) {
					final String catalogRefId = this.getCatalogRefIds()[0];
					wishListBean.setCatalogRefId(catalogRefId);
				}
				if (this.getProductId() != null) {

					wishListBean.setProductId(this.getProductId());

				}
				wishListBean.setQuantity(this.getQuantity());

				try {
					GiftListVO tempMoveVO = null;
					if (this.isMoveOperation()) {
						synchronized (this.getOrder()) {
							final Order holder = this.getOrder();
							final List list = holder.getCommerceItems();
							final Iterator itr = list.iterator();
							while (itr.hasNext()) {
								final CommerceItem comitem = (CommerceItem) itr.next();
								if(!(comitem instanceof BBBCommerceItem)){
									continue;
								}
								final BBBCommerceItem item = (BBBCommerceItem) comitem;
								long qua = 0;
								if ((this.getCurrentItemId() != null) && this.getCurrentItemId().equalsIgnoreCase(item.getId())) {
									tempMoveVO = new GiftListVO();
									tempMoveVO.setBts(this.isBts());
									tempMoveVO.setSkuID(item.getCatalogRefId());
									tempMoveVO.setProdID(item.getAuxiliaryData().getProductId());
									tempMoveVO.setShipMethodUnsupported(item.isShipMethodUnsupported());
									//Setting Personalized commerce item values in GiftlistVO
									String refNum = item.getReferenceNumber();
									if(null !=refNum && BBBUtility.isNotEmpty(refNum)){
										setPersonalizedItemProperties(tempMoveVO, item);
									}
									if(item.getPriceInfo().isOnSale()) {
										double salePrice = item.getPriceInfo().getSalePrice();
										tempMoveVO.setPrevPrice(getPricingTools().round(salePrice));

									} else {
										double listPrice = item.getPriceInfo().getListPrice();
										tempMoveVO.setPrevPrice(getPricingTools().round(listPrice));
									}
									tempMoveVO.setWishListItemId(BBBCoreConstants.TEMPITEM + Math.floor((Math.random()*1000)+1));
									tempMoveVO.setMsgShownOOS(true);
									if(null != item.getRegistryId()){
										tempMoveVO.setRegistryID(item.getRegistryId());
									}

									//LTL Change START
									BBBHardGoodShippingGroup sg = null;
									String deliveryItemId = "";
									String assemblyItemId = "";
									String ltlShipMethod ="";
									MutableRepositoryItem ltlItemsAssoc;
									//String associd = "";
									if(item.isLtlItem()) {
										if((BBBCoreConstants.TRUE).equalsIgnoreCase(item.getWhiteGloveAssembly())){
											tempMoveVO.setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD);
											ltlShipMethod = BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD;
										} else {
											tempMoveVO.setLtlShipMethod(item.getLtlShipMethod());
											ltlShipMethod = item.getLtlShipMethod();
										}
										deliveryItemId=item.getDeliveryItemId();
										assemblyItemId=item.getAssemblyItemId();
										/*sg = (BBBHardGoodShippingGroup) ((BBBShippingGroupCommerceItemRelationship) item
													.getShippingGroupRelationships().iterator()
													.next()).getShippingGroup();
									
										if(((BBBHardGoodShippingGroup)sg).getLTLItemMap() != null && !((BBBHardGoodShippingGroup)sg).getLTLItemMap().isEmpty()) {
												
											ltlItemsAssoc = (MutableRepositoryItem) ((BBBHardGoodShippingGroup)sg).getLTLItemMap().get(item.getId());
											associd = ltlItemsAssoc.getRepositoryId();
											if(ltlItemsAssoc.getPropertyValue("deliveryItemId") != null){
												deliveryItemId = (String) ltlItemsAssoc.getPropertyValue("deliveryItemId");
											}
											if(ltlItemsAssoc.getPropertyValue("assemblyItemId") != null){
												assemblyItemId = (String) ltlItemsAssoc.getPropertyValue("assemblyItemId");
											}
										}*/
									}
									//LTL Change END
									
									if((this.getQuantity() != 0) && (this.getQuantity() <= item.getQuantity())){
										tempMoveVO.setQuantity(this.getQuantity());
										qua = item.getQuantity() - this.getQuantity();
										LTLDeliveryChargeCommerceItem comItemSurcharge = null;
										LTLAssemblyFeeCommerceItem comItemAssembly = null; 
										if(!BBBUtility.isEmpty(deliveryItemId)){
											comItemSurcharge = (LTLDeliveryChargeCommerceItem) this.getOrder().getCommerceItem(deliveryItemId);
										}
										if(!BBBUtility.isEmpty(assemblyItemId)){
											comItemAssembly = (LTLAssemblyFeeCommerceItem) this.getOrder().getCommerceItem(assemblyItemId);
										}
										if((qua == 0) || (qua < 0)){
											item.setDeliveryItemId(null);
											item.setAssemblyItemId(null);
											/*if(!BBBUtility.isEmpty(associd)){
												try {
													((BBBHardGoodShippingGroup)sg).getLTLItemMap().remove(item.getId());
													this.getOrderRepository().removeItem(associd, "ltlItemAssoc");
												} catch (RepositoryException e) {
													this.logError("RepositoryException while removing ltlItemAssoc", e);	
												} 
											}*/
											this.getOrderManager().getCommerceItemManager().removeItemFromOrder(this.getOrder(), item.getId());
											
											if(comItemSurcharge != null){
												this.getOrderManager().getCommerceItemManager().removeItemFromOrder(this.getOrder(), comItemSurcharge.getId());
											}
											if(comItemAssembly != null){
												this.getOrderManager().getCommerceItemManager().removeItemFromOrder(this.getOrder(), comItemAssembly.getId());
											}
										} else if(qua > 0) {
											final PurchaseProcessHelper pph = (PurchaseProcessHelper)pRequest.resolveName("/atg/commerce/order/purchase/PurchaseProcessHelper");
											pph.adjustItemRelationshipsForQuantityChange(this.getOrder(),item, qua);
											if(comItemSurcharge != null){
												this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(this.getOrder(), comItemSurcharge, qua);
												comItemSurcharge.setQuantity(qua);
											}
											if(comItemAssembly != null) {
												this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(this.getOrder(), comItemAssembly, qua);
												comItemAssembly.setQuantity(qua);
											}
											item.setQuantity(qua);
										}
										 this.getOrderManager().updateOrder(this.getOrder());

									} else {
										this.getOrderManager().getCommerceItemManager().removeAllRelationshipsFromCommerceItem(this.getOrder(), item.getId());
										this.getOrderManager().getCommerceItemManager().removeItemFromOrder(this.getOrder(), item.getId());
									}
									this.repriceCartMoveItemsFromCart();
									List<GiftListVO> totalStoredQty = savedItemsSessionBean.getItems();
									if ((totalStoredQty == null) || totalStoredQty.isEmpty()) {
										totalStoredQty = new ArrayList<GiftListVO>();
										totalStoredQty.add(tempMoveVO);
										savedItemsSessionBean.setGiftListVO(totalStoredQty);
										if(this.isUndoOpt){
											this.setItemIdJustMvBack(tempMoveVO.getWishListItemId());
										}
									} else {
										final Iterator<GiftListVO> storedGiftListVO = totalStoredQty.iterator();
										boolean isNewItem = true;
										boolean updateItem = false;
										int ix=0;
										while (storedGiftListVO.hasNext()) {
											final GiftListVO existingVO = storedGiftListVO.next();
											if (existingVO.getSkuID().equalsIgnoreCase(item.getCatalogRefId()) && existingVO.getProdID().equalsIgnoreCase(item.getAuxiliaryData().getProductId()) && (existingVO.getLtlShipMethod()==null  || existingVO.getLtlShipMethod().equalsIgnoreCase(ltlShipMethod))) {
												
												//BPSI-2325 - Compare the Registry ID , LTL Shipping Method and Referenece Number while merging
												if(BBBUtility.compareStringsIgnoreCase(existingVO.getRegistryID(), item.getRegistryId()) && BBBUtility.compareStringsIgnoreCase(existingVO.getLtlShipMethod(), ltlShipMethod)
								            	   		&& BBBUtility.compareStringsIgnoreCase(existingVO.getReferenceNumber(), item.getReferenceNumber())){
													tempMoveVO.setQuantity(existingVO.getQuantity() + item.getQuantity());
													if (tempMoveVO.getWishListItemId() == null) {
														tempMoveVO.setWishListItemId(BBBCoreConstants.TEMPITEM + Math.random());
													}
													isNewItem = false;
													updateItem = true;
													break;
												}
											}
											ix++;
										}
										if(updateItem && !isNewItem){
											totalStoredQty.remove(ix);
											totalStoredQty.add(ix,tempMoveVO);
											savedItemsSessionBean.setGiftListVO(totalStoredQty);
										}
										if (isNewItem) {
											if(tempMoveVO.getQuantity() == 0){
												tempMoveVO.setQuantity(item.getQuantity());
											}
											if(this.isUndoOpt){
												this.setItemIdJustMvBack(tempMoveVO.getWishListItemId());
											}
											if(this.isUndoOpt && (this.getCountNo()>0) && (this.getCountNo() <= totalStoredQty.size())) {
												int size = totalStoredQty.size();
											   int finalCount = size + 1;
											   int i = 1;
											   while(size > 0){
												   if(i== this.getCountNo()){
													   finalCount = size;
													   break;
												   }
												   size--;
												   i++;
											   }
											   totalStoredQty.add(finalCount,tempMoveVO);
											} else if(this.isUndoOpt && (this.getCountNo() > totalStoredQty.size())){
												totalStoredQty.add(0,tempMoveVO);
											} else {
												totalStoredQty.add(tempMoveVO);
											}
											savedItemsSessionBean.setGiftListVO(totalStoredQty);
										}
									}
									this.setItemMoveFromCartID(tempMoveVO.getWishListItemId());

									return;
								}
							}
							this.addFormException((new DropletException(BBBCoreErrorConstants.INVALID_COMMERCE, BBBCoreErrorConstants.INVALID_COMMERCEITEM_ID)));
							return;

						}
					}
					if (((null != this.getJsonResultString()) || this.isRemoveOperation()) && !this.isFromCartPage()) {
						//PS-16874 changes
						List<GiftListVO> totalStoredQty = null;
						if(this.getProfile().isTransient()) {
							totalStoredQty = savedItemsSessionBean.getItems();
						} else {
							totalStoredQty = savedItemsSessionBean.getGiftListVO();
						}

						if (totalStoredQty == null) {
							totalStoredQty = new ArrayList<GiftListVO>();
						}
						if (this.isRemoveOperation()) {
							if ((this.getRemoveGiftitemIds() == null) || (this.getRemoveGiftitemIds().length == 0) ) {
								this.addFormException((new DropletException("Not a valid item Id", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10162)));
								return;
							}
							final Iterator<GiftListVO> storedGiftListVO = totalStoredQty.iterator();
							while (storedGiftListVO.hasNext()) {
								final GiftListVO existingVO = storedGiftListVO.next();
								if (existingVO.getWishListItemId().equalsIgnoreCase(this.getRemoveGiftitemIds()[0])) {
									storedGiftListVO.remove();
									return;
								}
							}
							this.addFormException((new DropletException("Not a valid item Id", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10162)));
							return;
						}
						this.prodList = new ArrayList<String>();
						final List<GiftListVO> newGiftList = this.getGiftListVO(pRequest);
						if(this.getOmniProdList().size() >0 ){
							if(this.getOmniProdList().get(0).contains("eVar54")){
								this.setPersonalizationMessage(this.getOmniProdList().get(0).substring(this.getOmniProdList().get(0).lastIndexOf("=") +1));
							}
						}
						// merge with existing list
						if ((totalStoredQty != null) && !totalStoredQty.isEmpty() && ((newGiftList != null) && (newGiftList.size() > 0))) {
							final Iterator<GiftListVO> storedGiftListVO = totalStoredQty.iterator();
							boolean check = true;
							while (storedGiftListVO.hasNext()) {
								long updatedQuantity = 0;
								final GiftListVO newvo = newGiftList.get(0);
								newvo.setMsgShownOOS(true);
								final GiftListVO existingVO = storedGiftListVO.next();
								if (existingVO.getProdID().equalsIgnoreCase(newvo.getProdID()) && this.isAddOperation() && existingVO.getSkuID().equalsIgnoreCase(newvo.getSkuID())) {
									//updatedQuantity = newvo.getQuantity() + existingVO.getQuantity();
									updatedQuantity = newvo.getQuantity();
									existingVO.setQuantity(updatedQuantity);
									savedItemsSessionBean.setGiftListVO(totalStoredQty);
									return;
								} else if (this.isUpdateOperation()) {
									if (this.getCurrentItemId().equalsIgnoreCase(existingVO.getWishListItemId())) {
										updatedQuantity = this.getQuantityToUpdate();
										if (updatedQuantity == 0) {
											storedGiftListVO.remove();
										}
										existingVO.setQuantity(updatedQuantity);
										savedItemsSessionBean.setGiftListVO(totalStoredQty);
										check = false;
										return;
									}

								} else if (existingVO.getWishListItemId().equalsIgnoreCase(this.itemIdToRemove) && this.isRemoveOperation()) {
									storedGiftListVO.remove();
									savedItemsSessionBean.setGiftListVO(totalStoredQty);
									return;
								} else if (this.isAddOperation()) {
									totalStoredQty.addAll(newGiftList);
									savedItemsSessionBean.setGiftListVO(totalStoredQty);
									return;
								}
							}
							if (this.isUpdateOperation && check) {
								this.addFormException((new DropletException("Not a valid item Id", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10161)));
								return;
							}
						} else if (this.isAddOperation()) {
							totalStoredQty.addAll(newGiftList);
							savedItemsSessionBean.setGiftListVO(totalStoredQty);
							return;
						} else {
							totalStoredQty.addAll(newGiftList);
							wishListBean.setGiftListVO(newGiftList);
							savedItemsSessionBean.setGiftListVO(totalStoredQty);

							final Iterator<GiftListVO> itr = newGiftList.iterator();
							while (itr.hasNext()) {
								final GiftListVO vo = itr.next();
								this.prodList.add(vo.getProdID());
							}
							wishListBean.setProductDetailsRedirectUrl(this.getProductDetailsRedirectUrl() + this.getParentProductId() + AMPERSAND + SHOW_POPUP_PARAM + BBBCoreConstants.TRUE + AMPERSAND + PROD_LIST_PARAM + this.prodList + AMPERSAND + ADD_TO_LIST_PARAM);
						}

					}
					if (this.isUpdateOperation() && this.isFromCartPage()) {
						final List<GiftListVO> totalStoredQty = savedItemsSessionBean.getGiftListVO();
						final List<GiftListVO> tempGiftList = new ArrayList<GiftListVO>();

						for (final GiftListVO giftList : totalStoredQty) {
							String upquant = pRequest.getParameter(giftList.getWishListItemId());
							//Below 4 lines for fixing BBBP-1713 and BBBP-1711
							if(null==pRequest.getParameter(giftList.getWishListItemId())){
								this.setCurrentItemId(giftList.getWishListItemId());
								upquant = Long.toString(giftList.getQuantity());
							}
							boolean remove = false;
							if(!StringUtils.isEmpty(upquant)){
								final long quantity = Long.parseLong(upquant);
								this.setQuantity(quantity);
								this.setCurrentItemId(giftList.getWishListItemId());
								final long qtDes = giftList.getQuantity();
								if(qtDes == quantity){
									tempGiftList.add(giftList);
									continue;
								} if(quantity == 0){
									remove = true;
								}else {
									giftList.setQuantity(quantity);
								}
							} else {
								continue;
							}
							if(!remove) {
								tempGiftList.add(giftList);
							}
						}
						savedItemsSessionBean.setGiftListVO(tempGiftList);
					}

					if (this.isRemoveOperation() && this.isFromCartPage()) {
						final List<GiftListVO> totalStoredQty = savedItemsSessionBean.getItems();
						final List<GiftListVO> tempGiftList = new ArrayList<GiftListVO>();
						for (final GiftListVO giftList : totalStoredQty) {
							if (!giftList.getSkuID().equals(wishListBean.getCatalogRefId())) {
								tempGiftList.add(giftList);
							}
						}
						savedItemsSessionBean.setGiftListVO(tempGiftList);
					}

				} catch (final CommerceException e) {
					if (this.isLoggingError()) {
						this.logError(LogMessageFormatter.formatMessage(pRequest, "CommerceException in StoreGiftlistFormHandler while saveGiftListItemInSession", BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM), e);
					}
					 this.setRollbackTransaction(true);
				} catch (final IOException e) {
					if (this.isLoggingError()) {
						this.logError(LogMessageFormatter.formatMessage(pRequest, "IOException in StoreGiftlistFormHandler while saveGiftListItemInSession", BBBCoreErrorConstants.ACCOUNT_ERROR_1258), e);
					}
					this.setRollbackTransaction(true);
				} finally {
					
						this.logDebug("CLS=[StoreGiftlistFormHandler] MTHD=[saveGiftListItemInSession ends]");
					
				}

				} else {
				// Request from cart page

				final GiftListVO newGiftListVO = new GiftListVO();

				if ((this.getCatalogRefIds() != null) && (this.getCatalogRefIds().length > 0)) {
					final String catalogRefId = this.getCatalogRefIds()[0];
					newGiftListVO.setSkuID(catalogRefId);
				}
				newGiftListVO.setProdID(this.getProductId());
				newGiftListVO.setQuantity(this.getQuantity());
				newGiftListVO.setQtyRequested(String.valueOf(this.getQuantity()));
				if ((this.getItemIds() != null) && (this.getItemIds().length > 0)) {
					newGiftListVO.setStoreID(this.getStoreId());
				}

				if ((this.getItemIds() != null) && (this.getItemIds().length > 0)) {
					final String commerceItemId = this.getItemIds()[0];
					newGiftListVO.setCommerceItemId(commerceItemId);

					final BBBCommerceItem item = (BBBCommerceItem) this.getOrder().getCommerceItem(commerceItemId);
					if(item.getPriceInfo().isOnSale()) {
						newGiftListVO.setPrevPrice(item.getPriceInfo().getSalePrice());
					} else {
						newGiftListVO.setPrevPrice(item.getPriceInfo().getListPrice());
					}
					item.setItemMoved(true);
				}
				final List<GiftListVO> storedGiftList = this.getSavedItemsSessionBean().getGiftListVO();
				final List<GiftListVO> updatedStoredGiftList = new ArrayList<GiftListVO>();

				// Updating List<GiftListVO>
				boolean isUpdated = false;

				if ((storedGiftList != null) && !storedGiftList.isEmpty()) {
					for (final GiftListVO storedGiftListVO : storedGiftList) {
						long updatedQuantity = 0;
						if (newGiftListVO.getCommerceItemId().equalsIgnoreCase(storedGiftListVO.getCommerceItemId()) && newGiftListVO.getCommerceItemId().equalsIgnoreCase(storedGiftListVO.getCommerceItemId())) {
							if (newGiftListVO.getQuantity() == 0) {
								this.setRemoveOperation(true);
							}
							if (this.isAddOperation()) {
								updatedQuantity = newGiftListVO.getQuantity() + storedGiftListVO.getQuantity();
							} else if (this.isUpdateOperation()) {
								updatedQuantity = newGiftListVO.getQuantity();
							} else if (this.isRemoveOperation()) {
								updatedQuantity = 0;

							}
							if (updatedQuantity > 0) {
								newGiftListVO.setQuantity(updatedQuantity);
								updatedStoredGiftList.add(newGiftListVO);
								this.getSessionBean().setMovedCommerceItem((BBBCommerceItem) this.getOrder().getCommerceItem(newGiftListVO.getCommerceItemId()));
								this.getOrder().removeCommerceItem(newGiftListVO.getCommerceItemId());
								isUpdated = true;
							} else if (updatedQuantity == 0) {
								isUpdated = true;

							}

						} else {
							updatedStoredGiftList.add(storedGiftListVO);
						}
					}
				}
				if (!isUpdated) {
					synchronized(this.getOrder()){
						updatedStoredGiftList.add(newGiftListVO);
						this.getSessionBean().setMovedCommerceItem((BBBCommerceItem) this.getOrder().getCommerceItem(newGiftListVO.getCommerceItemId()));
						this.getOrder().removeCommerceItem(newGiftListVO.getCommerceItemId());
					}
				}
				this.getSavedItemsSessionBean().setGiftListVO(updatedStoredGiftList);
				// remove item from order/cart which has been moved to
				// saveditems using ordermanager
				// reprice order
			}
		} finally {
			voitem = savedItemsSessionBean.getItems();

			if((voitem != null) && (voitem.size() > 0)) {
				afterSize = voitem.size();
			}
			if((beforeSize < afterSize) && !this.isUndoOpt()){
				this.setNewItemAdded(true);
			}
		}
	}

	/**
	 * This method sets the PersonalizedItem properties in GiftlistVO
	 * @param giftListVO
	 * @param item
	 */
	private void setPersonalizedItemProperties(GiftListVO giftListVO,
			final BBBCommerceItem item) {
		logDebug("StoreGiftlistFormHandler.setPersonalizedItemProperties starts");
		giftListVO.setReferenceNumber(item.getReferenceNumber());
		giftListVO.setFullImagePath(item.getFullImagePath());
		giftListVO.setThumbnailImagePath(item.getThumbnailImagePath());
		giftListVO.setPersonalizePrice(item.getPersonalizePrice());
		giftListVO.setPersonalizationOptions(item.getPersonalizationOptions());		
		//BPSI-5386 Personalization option single code display
		giftListVO.setPersonalizationOptionsDisplay(item.getPersonalizationOptionsDisplay());
		giftListVO.setPersonalizationDetails(item.getPersonalizationDetails());
		giftListVO.setMobileFullImagePath(item.getMobileFullImagePath());
		giftListVO.setMobileThumbnailImagePath(item.getMobileThumbnailImagePath());
		//TO-DO Save the status from commerce item
		giftListVO.setPersonalizationStatus(BBBCoreConstants.PERSONALIZATION_STATUS_COMPLETE);
		logDebug("StoreGiftlistFormHandler.setPersonalizedItemProperties ends");

	}
	
	/**
	 * This method sets the PersonalizedItem properties in GiftlistVO
	 * @param giftListVO
	 * @param item
	 */
	private void setPersonalizedItemProperties(GiftListVO giftListVO) {
		logDebug("StoreGiftlistFormHandler.setPersonalizedItemProperties starts");
		giftListVO.setReferenceNumber(this.getReferenceNumber());
		giftListVO.setFullImagePath(this.getFullImagePath());
		giftListVO.setThumbnailImagePath(this.getThumbnailImagePath());
		//giftListVO.setPersonalizePrice(this.getPersonalizePrice());
		giftListVO.setPersonalizationOptions(this.getPersonalizationOptions());
		giftListVO.setPersonalizationDetails(this.getPersonalizationDetails());
		giftListVO.setMobileFullImagePath(this.getMobileFullImagePath());
		giftListVO.setMobileThumbnailImagePath(this.getMobileThumbnailImagePath());
		giftListVO.setPersonalizationStatus(this.getPersonalizationStatus());
		logDebug("StoreGiftlistFormHandler.setPersonalizedItemProperties ends");
		
	}


	public void addSavedItemTOWishList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, RepositoryException {
		final Object obj = pRequest.getSession().getAttribute(BBBCoreConstants.ADDED);
		if(obj != null){
			this.setJsonResultString(obj.toString());
			this.setAddOperation(true);
			try {
				this.saveGiftListItemInSession(pRequest);
			} catch (final CommerceItemNotFoundException e) {
				//e.printStackTrace();
				logError(e.getMessage(),e);
				this.setRollbackTransaction(true);
			} catch (final InvalidParameterException e) {
				//e.printStackTrace();
				logError(e.getMessage(),e);
				this.setRollbackTransaction(true);
			}
		}
		final String giftListId = ((RepositoryItem) this.getProfile().getPropertyValue(BBBCoreConstants.WISHLIST)).getRepositoryId();
		final BBBSavedItemsSessionBean savedItemsSessionBean = this.getSavedItemsSessionBean();
		final List<GiftListVO> pGiftListVOs = savedItemsSessionBean.getGiftListVO();

		//merge and set new vo list in saveditemssessionbean
		((BBBGiftlistManager)this.getGiftlistManager()).mergeAndUpdateGiftList(pGiftListVOs, giftListId);
		//
		this.removePersonalizedSkuFromSession(pGiftListVOs);
		// remove cookie
		final Cookie oldCookie = new Cookie(this.getSave4LaterCookieName(), "");
		oldCookie.setMaxAge(0);
		oldCookie.setPath(this.getSave4LaterCookiePath());
		pResponse.addCookie(oldCookie);
		}

	private void removePersonalizedSkuFromSession(List<GiftListVO> giftListSessionBean){
		for(GiftListVO giftBean : giftListSessionBean){
	    	if(getSessionBean().getPersonalizedSkus().containsKey(giftBean.getSkuID())){
	    		getSessionBean().getPersonalizedSkus().remove(giftBean.getSkuID());
	    		logDebug("Personalized Sku " + giftBean.getSkuID() + "removed from session.");
	    	}else{
	    		logDebug("Personalized Sku " + giftBean.getSkuID() + "not found in session.");
	    	}
		}
    }

	/**
	 * gets GiftListItem & quantity In Session
	 *
	 * @param pRedirectURL
	 *            - the URL to redirect to after login
	 * @throws ServletException
	 *
	 */

	public void getGiftListItemFromSession(final DynamoHttpServletRequest pRequest)
			throws ServletException {
		 final HttpSession session = pRequest.getSession();
		final String[] itemIds = { this.getSessionBean().getCommerceItemId() };
		this.setItemIds(itemIds);
		this.setQuantity(this.getSessionBean().getQuantity());
		this.setGiftlistId(((RepositoryItem) this.getProfile()
				.getPropertyValue(WISH_LIST)).getRepositoryId());
		final String[] catalogRefIds= {this.getSessionBean().getCatalogRefId()};
		this.setCatalogRefIds(catalogRefIds);
		this.setProductId(this.getSessionBean().getProductId());
		this.setGiftListVO(this.getSessionBean().getGiftListVO());

		// set the gift list VO to session
	}

	/*public void getGiftListItemFromSavedSession(DynamoHttpServletRequest pRequest)
			throws ServletException {
		BBBSavedItemsSessionBean savedItemsSessionBean = getSavedItemsSessionBean();
		List<GiftListVO> storedGiftList  =  savedItemsSessionBean.getGiftListVO();
		int size = storedGiftList.size();
		String items[] = new String[size];
		int i = 0;
		for (GiftListVO storedGiftListVO : storedGiftList) {
			items[i] = storedGiftListVO.getCommerceItemId();
		}

		setItemIds(items);
		setQuantity(getSessionBean().getQuantity());
		setGiftlistId(((RepositoryItem) getProfile()
				.getPropertyValue(WISH_LIST)).getRepositoryId());
		String[] catalogRefIds= {getSessionBean().getCatalogRefId()};
		setCatalogRefIds(catalogRefIds);
		setProductId(getSessionBean().getProductId());
		setGiftListVO(getSessionBean().getGiftListVO());

		// set the gift list VO to session
	}*/

	@Override
	public boolean handleAddItemToGiftlist(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, CommerceException {

		this.saveGiftListItemInSession(pRequest);
		boolean result = super.handleAddItemToGiftlist(pRequest, pResponse);
		if(result){
			removePersonalizedSkuFromSession(this.getGiftListVO().get(0).getSkuID());
		}
		return result;

	}

	private void removePersonalizedSkuFromSession(String skuId){
    	if(getSessionBean().getPersonalizedSkus().containsKey(skuId)){
    		getSessionBean().getPersonalizedSkus().remove(skuId);
    		logDebug("Personalized Sku " + skuId + "removed from session.");
    	}else{
    		logDebug("Personalized Sku " + skuId + "not found in session.");
    	}
    }

	/**
	 * Overridden method of GiftlistFormHandler class. called when the user hits the submit
	 * button on a product page to add an item to the giftlist.  This handler
	 * looks up the product in the catalog and returns a sku.  It then
	 * looks for an existing item in the giftlist with this sku id.  if it
	 * exists, the quantity desired is incremented by the quantity specified.
	 * if it doesn't exist, it creates the item and adds it to the giftlist
	 * specified in giftlist id.
	 *
	 * @param pRequest the servlet's request
	 * @param pResponse the servlet's response
	 * @return true if successful, false otherwise.
	 * @exception ServletException if there was an error while executing the code
	 * @exception IOException if there was an error with servlet io
	 * @exception CommerceException if there was an error with Commerce
	 * @throws JSONException
	 */
	public boolean handleAddItemListToGiftlist(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, CommerceException, JSONException {


			this.logDebug(" StoreGiftlistFormHandler :: handleAddItemToGiftlist start");
		this.addContextPath(pRequest.getContextPath());
		if (this.getProfile().isTransient()){

			try {
				this.setAddOperation(true);

				if(!this.isFromProductPage()){
					this.saveGiftListItemInSession(pRequest);
				} else {
					final BBBSessionBean wishListBean = this.getSessionBean();
					try {
						
						final List<GiftListVO> additemList = ((BBBGiftlistManager) this.getGiftlistManager()).addItemJSONObjectParser(this.getJsonResultString(),this.mOmniProdList,this.getParentProductId());
						if((additemList != null) && (additemList.get(0) != null)) {
							String parentProductId = "";
								final JSONObject urlString = new  JSONObject(this.getJsonResultString());
								parentProductId = (String) urlString.get("parentProdId");
								final JSONArray arrObj =  urlString.getJSONArray("addItemResults");
								String pageView = "";
								if(urlString.has("pageView")){
									pageView = (String) urlString.get("pageView");
								}
								int totalQuan = 0;
								String append = BBBCoreConstants.BLANK;
								for(int i = 0; i<arrObj.length();i++){
									final JSONObject jsonQty = arrObj.getJSONObject(i);
									if(jsonQty.has("skuId") && !StringUtils.isEmpty(jsonQty.getString("skuId"))){
										append = "&skuId=" +jsonQty.getString("skuId") ;
									}
									if(jsonQty.has("registryId") && !StringUtils.isEmpty(jsonQty.getString("registryId"))){
										append = append + "&registryId="+jsonQty.getString("registryId");
									}
									final int quant = jsonQty.getInt("qty");
									totalQuan = totalQuan + quant;
								}
							//added condition to retain the list view from PDP collection and accessories grid for save for later.
							if(!StringUtils.isEmpty(pageView)){
								append += "&view="+pageView;
							}
							final String qty = Integer.toString(totalQuan);
							
							// Added condition to redirect to product comparison page for anonymous user for save for later. R2.2 Story 178-A4.
							if(urlString.has("fromComparisonPage") && !BBBUtility.isEmpty(urlString.getString("fromComparisonPage"))){
								wishListBean.setProductDetailsRedirectUrl(pRequest.getContextPath()+ this.getComparisonPageRedirectUrl() + parentProductId + AMPERSAND + SHOW_POPUP_PARAM + BBBCoreConstants.TRUE + AMPERSAND + PROD_LIST_PARAM + additemList.get(0).getSkuID() + PARAN_CLOSE+ AMPERSAND + ADD_TO_LIST_PARAM + true + AMPERSAND + QTY + qty + append);
							} else {
							wishListBean.setProductDetailsRedirectUrl(pRequest.getContextPath()+this.getProductDetailsRedirectUrl() + parentProductId + AMPERSAND + SHOW_POPUP_PARAM + BBBCoreConstants.TRUE + AMPERSAND + PROD_LIST_PARAM + additemList.get(0).getSkuID() + PARAN_CLOSE+ AMPERSAND + ADD_TO_LIST_PARAM + true + AMPERSAND + QTY + qty + append);
							}
						}
					} catch (final BBBBusinessException e) {
						if (this.isLoggingError()) {
							this.logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in StoreGiftlistFormHandler while handleAddItemListToGiftlist", BBBCoreErrorConstants.ACCOUNT_ERROR_1261 ), e);
							this.logError(e);
						}
					}
				}

				if(this.getProfile().isTransient() && (this.mOmniProdList != null) && !this.mOmniProdList.isEmpty()) {
					pRequest.getSession().setAttribute(BBBCoreConstants.ADDED, this.mOmniProdList);
				} else if(this.getProfile().isTransient() || (pRequest.getParameter("addItemResults") != null)) {
					pRequest.getSession().setAttribute(BBBCoreConstants.ADDED, pRequest.getParameter("addItemResults"));
				}
			} catch (final CommerceItemNotFoundException e) {
				if (this.isLoggingError()) {
					this.logError(LogMessageFormatter.formatMessage(pRequest, "CommerceItemNotFoundException in StoreGiftlistFormHandler while handleAddItemListToGiftlist", BBBCoreErrorConstants.ACCOUNT_ERROR_1259 ), e);
				}
				this.setRollbackTransaction(true);
			} catch (final InvalidParameterException e) {
				if (this.isLoggingError()) {
					this.logError(LogMessageFormatter.formatMessage(pRequest, "InvalidParameterException in StoreGiftlistFormHandler while handleAddItemListToGiftlist", BBBCoreErrorConstants.ACCOUNT_ERROR_1260 ), e);
				}
				this.setRollbackTransaction(true);
			}

			// For Login Page Personalization


			final MutableRepositoryItem userProfile=(MutableRepositoryItem) ServletUtil.getCurrentUserProfile();
			final String loginFrom=BBBWebServiceConstants.TXT_LOGIN_WISHLIST;

			userProfile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, loginFrom);

			pResponse.setHeader("BBB-ajax-redirect-url", pRequest.getContextPath()+LOGIN_REDIRECT_URL);
//			addFormException(new DropletException("notLoggedIn", "notLoggedIn"));
//			setAddItemToGiftlistErrorURL(getAddItemToGiftlistLoginURL());
//			return checkFormRedirect(null, getAddItemToGiftlistErrorURL(), pRequest, pResponse);
		} else {
			this.prodList = new ArrayList<String>();
			this.setGiftlistId(((RepositoryItem) this.getProfile()
					.getPropertyValue(WISH_LIST)).getRepositoryId());
			this.setSiteId(this.getSiteContext().getSite().getId()) ;
			final BBBGiftlistManager mgr = (BBBGiftlistManager)this.getGiftlistManager();
			List<GiftListVO> giftListVO = this.getGiftListVO();
			if(null == giftListVO){
				giftListVO = this.getGiftListVO(pRequest);
				this.setReferenceNumber(giftListVO.get(0).getReferenceNumber());
			}

			if(StringUtils.isEmpty(this.getGiftlistId())){
				this.setGiftlistId(((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST)).getRepositoryId());
			}
			if ( (giftListVO != null) && this.validateGiftlistId(pRequest, pResponse)) {
				final Iterator<GiftListVO> itr = giftListVO.iterator();
				while(itr.hasNext()){
					final GiftListVO vo = itr.next();
					long quantity = vo.getQuantity();
					if (quantity == 0L) {
						quantity = this.getQuantity(vo.getSkuID(), pRequest, pResponse);
						vo.setQuantity(quantity);
					}
					this.prodList.add(vo.getProdID());
					this.setTotalQuantityAdded(this.getTotalQuantityAdded()+quantity);
					try {
						String siteId = SiteContextManager.getCurrentSiteId();
						if (!BBBUtility.siteIsTbs(siteId) && ((BBBProfileTools) getProfileTools()).isRecognizedUser(pRequest, getProfile())) {
							checkWishListItemCount(pRequest, vo.getSkuID());
						}
						mgr.addCatalogItemToGiftlist(vo, this.getGiftlistId(), this.getSiteId());
					} catch (final RepositoryException e) {
						if (this.isLoggingError()) {
							this.logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException in StoreGiftlistFormHandler while getGiftListVO", BBBCoreErrorConstants.ACCOUNT_ERROR_1261 ), e);
							this.logError(e);
						}
					} catch (BBBBusinessException be) {
						logError(be.getMessage());
						addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.ERROR_SFL_MAX_REACHED, pRequest.getLocale().getLanguage(), null, null), 
								BBBCatalogErrorCodes.ERROR_SFL_MAX_REACHED));
						return false;
					}
					this.removePersonalizedSkuFromSession(vo.getSkuID());
				}
				this.setAddWishlistSuccessFlag(true);
				return true;
			}
		}
		this.setAddWishlistSuccessFlag(false);
		return false;
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException
	 */
	public List<GiftListVO> getGiftListVO(final DynamoHttpServletRequest pRequest)throws ServletException,
			IOException, CommerceException {

		List<GiftListVO> additemList = null;
		try {
			this.mOmniProdList = new ArrayList<String>();
			additemList = ((BBBGiftlistManager) this.getGiftlistManager()).addItemJSONObjectParser(this.getJsonResultString(),this.mOmniProdList,this.getParentProductId());
		} catch (final BBBBusinessException e) {
				if (this.isLoggingError()) {
					this.logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in StoreGiftlistFormHandler while getGiftListVO", BBBCoreErrorConstants.ACCOUNT_ERROR_1261 ), e);

					this.logError(e);
				}
		}
		return additemList;

	}

	/** Rest service to add or remove an item from wishlist based on a flag wishlistToggle.
	 * 
	 * @return
	 * @throws CommerceException
	 * @throws ServletException
	 * @throws IOException
	 * @throws JSONException
	 */
	public boolean handleToggleWishListSwitch(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws CommerceException, ServletException, IOException, JSONException{		
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		String toogleValue = this.getWishlistToggle();
		if(null == toogleValue || StringUtils.isEmpty(toogleValue) || toogleValue.equalsIgnoreCase("true")){
			this.logDebug("handleToggleWishListSwitch: adding item to wishlist");
			return this.handleAddItemListToGiftlist(request, response);
		}else{
			if(BBBUtility.getChannel().equalsIgnoreCase(BBBCoreConstants.DEFAULT_CHANNEL_VALUE)){
				if(!this.getProfile().isTransient()){
					this.setGiftlistId(((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST)).getRepositoryId().toString());
					this.getItemIdSaved();
				}
				else{
					List<GiftListVO> vo = (List<GiftListVO>) getSavedItemsSessionBean().getGiftListVO();
					if (vo !=null) {
						for(GiftListVO tempvo:vo){
							if(tempvo.getSkuID().equalsIgnoreCase(this.getCurrentItemId()))
							{
								if(vo.size() == 1){
									this.setLastItemRemoval(true);
								}
								if(null == this.getLtlDsl() || StringUtils.isEmpty(this.getLtlDsl())){
									this.setCurrentItemId(tempvo.getWishListItemId());
								}
								else if(this.getLtlDsl().equalsIgnoreCase(tempvo.getLtlShipMethod())){
									this.setCurrentItemId(tempvo.getWishListItemId());
								}
							
							this.setTotalQuantityAdded(tempvo.getQuantity());
							break;
							}						
						}
					}
				}
				this.setRemoveGiftitemIds(new String[]{this.getCurrentItemId()});
				return this.handleRemoveItemsFromGiftlist(request, response);
			}
			else{
				this.logDebug("handleToggleWishListSwitch: removing item from wishlist");
				if(!this.getProfile().isTransient()){
					this.logDebug("handleToggleWishListSwitch: removing item from wishlist for logged in user with skuid" + this.getCurrentItemId());
					this.getItemIdSaved();
				}				
				else{
					this.logDebug("handleToggleWishListSwitch: removing item from wishlist for logged out user with skuid" + this.getCurrentItemId());
					List<GiftListVO> vo = (List<GiftListVO>) getSavedItemsSessionBean().getGiftListVO();
					if (vo !=null) {
						for(GiftListVO tempvo:vo){
							if(tempvo.getSkuID().equalsIgnoreCase(this.getCurrentItemId()))
							{
							this.setCurrentItemId(tempvo.getWishListItemId());
							break;
							}						
						}
					}
				}
			this.setQuantityToUpdate(0L);
			return this.handleUpdateWishlistItems(request, response);
		}
	}
	}
	
	/**
	 * sets the wishlist item id to be removed in currentItemId.
	 */
	public boolean getItemIdSaved() {
		List items;
		items = this.getGiftlistManager().getGiftlistItems(((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST)).getRepositoryId());
		if ((items != null) && (items.size() > 0)) {
			if(items.size() == 1){
				this.setLastItemRemoval(true);
			}
			for (int i = 0; i < items.size(); i++) {
				String itemId;
				RepositoryItem item = (RepositoryItem) items.get(i);				
				if (this.getCurrentItemId().equalsIgnoreCase(
						(String) (item).getPropertyValue(BBBCoreConstants.SKU_ID))) {
					/*if(BBBUtility.getChannel().equalsIgnoreCase(BBBCoreConstants.DEFAULT_CHANNEL_VALUE)){*/
						if(null == this.getLtlDsl() || StringUtils.isEmpty(this.getLtlDsl())){
							if(null != (String) (item).getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER)){
                                continue;
							}
							this.setCurrentItemId((String) item.getPropertyValue(ID));
							this.logDebug("StoreGiftlistFormHandler ::getItemIdSaved() currentItemId"+ this.getCurrentItemId());
							this.setTotalQuantityAdded(((Long)item.getPropertyValue(BBBCoreConstants.QUANTITY_DESIRED)).longValue());
							this.logDebug("StoreGiftlistFormHandler ::getItemIdSaved() totalQuantityAdded"+ this.getTotalQuantityAdded());
							break;
						}
						else if(this.getLtlDsl().equalsIgnoreCase((String) item.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD))){
							this.setCurrentItemId((String) item.getPropertyValue(ID));
							this.logDebug("StoreGiftlistFormHandler ::getItemIdSaved() currentItemId"+ this.getCurrentItemId());
							this.setTotalQuantityAdded(((Long)item.getPropertyValue(BBBCoreConstants.QUANTITY_DESIRED)).longValue());
							this.logDebug("StoreGiftlistFormHandler ::getItemIdSaved() totalQuantityAdded"+ this.getTotalQuantityAdded());
							break;
						}									
					//}
					/*else{
						this.setCurrentItemId((String) item.getPropertyValue(ID));
						break;
					}*/
				}
			}
		}
		return false;
	}
	
	
	
/*	*//**
	 * parsing JASON Objects
	 * @param pJsonResultString
	 * @return
	 * @throws BBBBusinessException
	 *//*
	public List<GiftListVO> addItemJSONObjectParser(String pJsonResultString, DynamoHttpServletRequest pRequest)
			throws BBBBusinessException {
		final String logMessage = getClass().getName() + "StoreJSONObjectParser";
		if (isLoggingDebug()) {
			logDebug(logMessage + " Starts here");
			logDebug(logMessage + " add item input parameters --> " + pJsonResultString);
		}
		JSONObject jsonObject = null;
		PricingTools pricingTools = getPricingTools();
		jsonObject = (JSONObject) JSONSerializer.toJSON(pJsonResultString);
		DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);
		List<GiftListVO> additemList = new ArrayList<GiftListVO>();
		RepositoryKeyService keySvice = getKeyService();
		Object reqLocale = null;
		if(null != keySvice ){
			reqLocale = keySvice.getRepositoryKey();
		}
		String currencySymbol = null;
		 if(reqLocale instanceof Locale)
         {
             PricingTools _tmp2 = pricingTools;
             currencySymbol = PricingTools.getCurrencySymbol((Locale)(Locale)reqLocale);
         } else
         {
             PricingTools _tmp3 = pricingTools;
             currencySymbol = PricingTools.getCurrencySymbol((String)(String)reqLocale);
         }
			if (dynaBeanProperties.contains(BBBCoreConstants.ADDITEMRESULTS)) {
				 @SuppressWarnings("unchecked")
				List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean.get(BBBCoreConstants.ADDITEMRESULTS);
				 mOmniProdList = new ArrayList<String>();
					for(DynaBean item : itemArray)
					{
						GiftListVO giftListVO=new GiftListVO();
						giftListVO.setSkuID(item.get(BBBCoreConstants.SKUID).toString());
						giftListVO.setProdID(item.get(BBBCoreConstants.PRODID).toString());
						giftListVO.setPrice((item.get(BBBCoreConstants.PRICE).toString()));
						giftListVO.setQuantity(Long.parseLong(item.get(BBBCoreConstants.QTY).toString()));
						String price = giftListVO.getPrice();
						if(!StringUtils.isEmpty(price) && price.contains(currencySymbol)){
							price = price.replace(currencySymbol, BBBCoreConstants.BLANK);
							if(price.contains(BBBCoreConstants.COMMA)){
								price = price.replace(BBBCoreConstants.COMMA, BBBCoreConstants.BLANK);
							}
							double doublePrice = Double.parseDouble(price.trim());
							mOmniProdList.add(BBBCoreConstants.SEMICOLON+ giftListVO.getSkuID() +BBBCoreConstants.OMNI_SYMBOL_1+ giftListVO.getQuantity()+BBBCoreConstants.OMNI_SYMBOL_2+(doublePrice*giftListVO.getQuantity()));
						}
						additemList.add(giftListVO);
					}
			}

			if (dynaBeanProperties.contains(BBBCoreConstants.PARENTPRODID)) {
				 @SuppressWarnings("unchecked")
				String parentProdId = JSONResultbean.get(BBBCoreConstants.PARENTPRODID).toString();
				if(null != parentProdId){
					setParentProductId(parentProdId);
				}
			}


		return additemList;
	}
	*/
	/**
	 * Returns LocaleKeyService service.
	 *
	 * @return RepositoryKeyService The service for which we will get the locale
	 * to use proper language.
	 */
	public RepositoryKeyService getKeyService() {

		RepositoryKeyService repositoryKeyService = null;
		final Nucleus nucleus = Nucleus.getGlobalNucleus();

		if (nucleus != null) {
			final Object keyService = nucleus.resolveName("/atg/userprofiling/LocaleService");

			if (keyService instanceof RepositoryKeyService) {
				repositoryKeyService = (RepositoryKeyService)keyService;
			}
		}
		return repositoryKeyService;
	}

	/*
	 * To get the properties names from JSON result string
	 */
	private List<String> getPropertyNames(final DynaBean pDynaBean) {

		final DynaClass dynaClass = pDynaBean.getDynaClass();
		final DynaProperty properties[] = dynaClass.getDynaProperties();
		final List<String> propertyNames = new ArrayList<String>();
		for (final DynaProperty propertie : properties) {
			final String name = propertie.getName();
			propertyNames.add(name);
		}
		return propertyNames;
	}
	
	/**
	 * This method checks items in wish list for recognized user and throws exception in case it exceeds the limit
	 * @param pRequest
	 * @param currentItemId
	 * @throws BBBBusinessException
	 */
	protected void checkWishListItemCount(DynamoHttpServletRequest pRequest, String currentItemId) throws BBBBusinessException {
		logDebug("Entering StoreGiftlistFormHandler.checkWishListItemCount currentItemId::" + currentItemId);
		RepositoryItem wishList = (RepositoryItem) getProfile().getPropertyValue(WISH_LIST);
		String siteId = getSiteContext().getSite().getId();
		boolean itemExistInWishList = false;
		
		logDebug("wishList::" + wishList);
		if (wishList != null) {
			@SuppressWarnings("unchecked")
			List<RepositoryItem> wishtListItems = (List<RepositoryItem>) (wishList
					.getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS));

			long itemCount = 0;
			for (RepositoryItem item : wishtListItems) {
				if (siteId.equals(item.getPropertyValue(BBBCoreConstants.SITE_ID))) {
					itemCount++;
					if (item.getPropertyValue(BBBCoreConstants.SKU_ID).equals(currentItemId)) {
						itemExistInWishList = true;
						break;
					}
				}
			}
			logDebug("count::" + itemCount);
			if (!itemExistInWishList && itemCount >=  getMaxItemCountForSFL()) {
				throw new BBBBusinessException(getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.ERROR_SFL_MAX_REACHED, pRequest.getLocale().getLanguage(), null, null), 
						BBBCatalogErrorCodes.ERROR_SFL_MAX_REACHED);
			}
		}
		logDebug("Exting from StoreGiftlistFormHandler.checkWishListItemCount");
	}


	/**
	 * @return max item count for SFL
	 */
	private int getMaxItemCountForSFL() {
		int sflMaxItemCount = 0;
		try {
			sflMaxItemCount = Integer.valueOf(getCatalogTool().getAllValuesForKey(BBBCheckoutConstants.WSDL_GIFT_REGISRTY, BBBCheckoutConstants.SFL_MAX_LIMIT).get(0));
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error finding value for key SFL_MAX_LIMIT",e);
		}
		return sflMaxItemCount;
	}

	/**
	 * @return the jsonResultString
	 */
	public String getJsonResultString() {
		return this.jsonResultString;
	}

	/**
	 * @param jsonResultString the jsonResultString to set
	 */
	public void setJsonResultString(final String jsonResultString) {
		this.jsonResultString = jsonResultString;
	}

	/**
	 * @return the giftListVO
	 */
	public List<GiftListVO> getGiftListVO() {
		return this.giftListVO;
	}

	/**
	 * @param giftListVO the giftListVO to set
	 */
	public void setGiftListVO(final List<GiftListVO> giftListVO) {
		this.giftListVO = giftListVO;
	}

	/**
	 * @return the parentProductId
	 */
	public String getParentProductId() {
		return this.parentProductId;
	}

	/**
	 * @param parentProductId the parentProductId to set
	 */
	public void setParentProductId(final String parentProductId) {
		this.parentProductId = parentProductId;
	}

	/**
	 * @return the productDetailsRedirectUrl
	 */
	public String getProductDetailsRedirectUrl() {
		return this.productDetailsRedirectUrl;
	}

	/**
	 * @param productDetailsRedirectUrl the productDetailsRedirectUrl to set
	 */
	public void setProductDetailsRedirectUrl(final String productDetailsRedirectUrl) {
		this.productDetailsRedirectUrl = productDetailsRedirectUrl;
	}

	/**
	 * @return the addWishlistSuccessFlag
	 */
	public boolean isAddWishlistSuccessFlag() {
		return this.addWishlistSuccessFlag;
	}

	/**
	 * @param addWishlistSuccessFlag the addWishlistSuccessFlag to set
	 */
	public void setAddWishlistSuccessFlag(final boolean addWishlistSuccessFlag) {
		this.addWishlistSuccessFlag = addWishlistSuccessFlag;
	}

	/**
	 * @return the mCurrentItemId
	 */
	public String getCurrentItemId() {
		return this.mCurrentItemId;
	}

	/**
	 * @param pCurrentItemId the mCurrentItemId to set
	 */
	public void setCurrentItemId(final String pCurrentItemId) {
		this.mCurrentItemId = pCurrentItemId;
	}

	/**
	 * @return the prodList
	 */
	public List<String> getProdList() {
		return this.prodList;
	}

	/**
	 * @param prodList the prodList to set
	 */
	public void setProdList(final List<String> prodList) {
		this.prodList = prodList;
	}

	/**
	 * Add Context path to the Property bean
	 * @param pContextPath
	 */
	private void addContextPath(final String pContextPath) {
		if ((pContextPath != null) && !this.isContextAdded() && StringUtils.isEmpty(this.getJsonResultString()) && StringUtils.isEmpty(this.getCurrentItemId())) {
			this.setMoveItemsFromCartSuccessURL(pContextPath + this.getMoveItemsFromCartSuccessURL());
			this.setMoveItemsFromCartErrorURL(pContextPath + this.getMoveItemsFromCartErrorURL());
			this.setMoveItemsFromCartLoginURL(pContextPath + this.getMoveItemsFromCartLoginURL());
			this.setProductDetailsRedirectUrl(pContextPath + this.getProductDetailsRedirectUrl());
			this.setContextAdded(true);
		}
	}

	public boolean isContextAdded() {
		return this.mContextAdded;
	}

	public void setContextAdded(final boolean pContextAdded) {
		this.mContextAdded = pContextAdded;
	}

	public String getItemIdToMove() {
		return this.itemIdToMove;
	}
	public void setItemIdToMove(final String itemIdToMove) {
		this.itemIdToMove = itemIdToMove;
	}

	public String getComparisonPageRedirectUrl() {
		return comparisonPageRedirectUrl;
	}

	public void setComparisonPageRedirectUrl(String comparisonPageRedirectUrl) {
		this.comparisonPageRedirectUrl = comparisonPageRedirectUrl;
	}

    @Override
    public final void logDebug(final String message) {
        if (this.isLoggingDebug()) {
            this.logDebug(message, null);
        }
    }

	public String getPersonalizationMessage() {
		return personalizationMessage;
}

	public void setPersonalizationMessage(String personalizationMessage) {
		this.personalizationMessage = personalizationMessage;
	}

	public String getWishlistToggle() {
		return wishlistToggle;
	}

	public void setWishlistToggle(String wishlistToggle) {
		this.wishlistToggle = wishlistToggle;
	}

	public String getLtlDsl() {
		return ltlDsl;
	}
	
	public void setLtlDsl(String ltlDsl) {
		this.ltlDsl = ltlDsl;
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

	public boolean isLastItemRemoval() {
		return lastItemRemoval;
	}

	public void setLastItemRemoval(boolean lastItemRemoval) {
		this.lastItemRemoval = lastItemRemoval;
	}

	public String getSuccessQueryParam() {
		return successQueryParam;
	}

	public void setSuccessQueryParam(String successQueryParam) {
		this.successQueryParam = successQueryParam;
	}

	/**
	 * @return the isWishListUndoOpt
	 */
	public boolean isWishListUndoOpt() {
		return isWishListUndoOpt;
	}

	/**
	 * @param isWishListUndoOpt the isWishListUndoOpt to set
	 */
	public void setWishListUndoOpt(boolean isWishListUndoOpt) {
		this.isWishListUndoOpt = isWishListUndoOpt;
	}
}
