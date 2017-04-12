package com.bbb.wishlist.manager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;






import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.GiftlistTools;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.pricing.PricingTools;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.naming.ComponentName;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.dp.RepositoryKeyService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.exim.bean.EximCustomizedAttributesVO;
import com.bbb.commerce.exim.bean.EximImagePreviewVO;
import com.bbb.commerce.exim.bean.EximImagesVO;
import com.bbb.commerce.exim.bean.EximSummaryResponseVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.internationalshipping.vo.BBBInternationalCurrencyDetailsVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.rest.catalog.vo.KatoriPriceRestVO;
import com.bbb.rest.output.BBBCustomTagComponent;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;

// TODO: Auto-generated Javadoc
/**
 * The Class BBBGiftlistManager.
 *
 * @author akhaju
 */
/**
 * @author akhaju
 *
 */
public class BBBGiftlistManager extends GiftlistManager {

	/** The giftlist tools. */
	private GiftlistTools giftlistTools = null;
	
	/** The m pricing tools. */
	private PricingTools mPricingTools;
	private final String TWO="2";
	private final String ZERO="0";
	private BBBCatalogTools bbbCatalogTools;
	public BBBLocalCacheContainer getLocalcache() {
	return localcache;
}

public void setLocalcache(BBBLocalCacheContainer localcache) {
	this.localcache = localcache;
}

	/** The bbb custom tag component. */
	private BBBCustomTagComponent	bbbCustomTagComponent;
	
	/** The exim pricing manager. */
	private BBBEximManager eximPricingManager;
	
	/** The Constant MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY. */
	private static final String MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY = "UNABLE TO RETRIEVE DATA FROM REPOSITORY";
	
	/** The Constant MSG_PARENT_PRODUCT_NOT_FOUND. */
	private static final String MSG_PARENT_PRODUCT_NOT_FOUND = "ATG_ISSUE:PARENT PRODUCT NOT FOUND FOR SKU";
	
	/** The Constant PROFILE_WISH_LIST_MIGRATION. */
	private static final String PROFILE_WISH_LIST_MIGRATION = "Profile-WishListMigration:";
	
	/** The Constant STORE_SKU. */
	private static final String STORE_SKU = "storeSKU";
	
	/** The Constant MSG_ERROR_PROFILE_NOT_MIGRATED. */
	private static final String MSG_ERROR_PROFILE_NOT_MIGRATED = "ATG_ISSUE:PROFILE NOT MIGRATED";
	
	/** The Constant MSG_ERROR_INVALID_SKU_IN_FEED. */
	private static final String MSG_ERROR_INVALID_SKU_IN_FEED = "FEED_DATA_ISSUE:SKU NOT VALID IN FEED";
	
	/** The Constant MSG_ERROR_STORE_SKU_IN_FEED. */
	private static final String MSG_ERROR_STORE_SKU_IN_FEED = "FEED_DATA_ISSUE:STORE_SKU_IN_ATG";
	//private static final String MSG_ERROR_INVALID_PRODUCT_IN_FEED = "FEED_DATA_ISSUE:PRODUCT NOT VALID IN FEED";
	/** The Constant MSG_ERROR_GIFTLIST_ITEM_ALREADY_EXIST. */
	private static final String MSG_ERROR_GIFTLIST_ITEM_ALREADY_EXIST = "ATG_ISSUE:SKIP ITEM MERGE AS GIFT LIST ALREADY EXIST";
	
	/** The Constant MSG_ERROR_NEW_GIFTLIST_EXIST_QTYINVALID. */
	private static final String MSG_ERROR_NEW_GIFTLIST_EXIST_QTYINVALID = "FEED_DATA_ISSUE:WISHLIST MERGE IS ALLOWED BUT QTY IS 0";
	
	/** The Constant MSG_ERROR_INVALID_QTY. */
	private static final String MSG_ERROR_INVALID_QTY = "FEED_DATA_ISSUE:QUANTITY INVALID IN FEED";
	
	/** The Constant MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM. */
	private static final String MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM = "ATG_ISSUE:Error in creating new gift list item";
	
	
	/**
	 * Gets the exim pricing manager.
	 *
	 * @return the eximPricingManager
	 */
	public BBBEximManager getEximPricingManager() {
		return eximPricingManager;
	}

	/**
	 * Sets the exim pricing manager.
	 *
	 * @param eximPricingManager the eximPricingManager to set
	 */
	public void setEximPricingManager(BBBEximManager eximPricingManager) {
		this.eximPricingManager = eximPricingManager;
	}
	
	/** The localcache. */
	private BBBLocalCacheContainer localcache;	
	
	
	/**
	 * Gets the pricing tools.
	 *
	 * @return the pricingTools
	 */
	public PricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * Sets the pricing tools.
	 *
	 * @param pPricingTools the pricingTools to set
	 */
	public void setPricingTools(PricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}

	/* (non-Javadoc)
	 * @see atg.commerce.gifts.GiftlistManager#getGiftlistTools()
	 */
	public GiftlistTools getGiftlistTools() {
		return giftlistTools;
	}

	/* (non-Javadoc)
	 * @see atg.commerce.gifts.GiftlistManager#setGiftlistTools(atg.commerce.gifts.GiftlistTools)
	 */
	public void setGiftlistTools(GiftlistTools giftlistTools) {
		this.giftlistTools = giftlistTools;
	}
	
		/**
		 * Creates the giftlist item.
		 *
		 * @param pGiftListVO the gift list vo
		 * @param pDisplayName the display name
		 * @param pDescription the description
		 * @param pSiteId the site id
		 * @return the string
		 * @throws CommerceException the commerce exception
		 */
		public String createGiftlistItem(GiftListVO pGiftListVO, String pDisplayName, String pDescription, String pSiteId) throws CommerceException {

		GiftlistTools tools = getGiftlistTools();
		String itemId = "";
		

		try {
			MutableRepositoryItem item = tools.createGiftlistItem();
			item.setPropertyValue(tools.getCatalogRefIdProperty(),
					pGiftListVO.getSkuID());
			item.setPropertyValue(tools.getProductIdProperty(), pGiftListVO.getProdID());

			item.setPropertyValue(tools.getQuantityDesiredProperty(),
					Long.valueOf(pGiftListVO.getQuantity()));

			item.setPropertyValue(tools.getQuantityPurchasedProperty(),
					Long.valueOf(0L));

			item.setPropertyValue(tools.getDisplayNameProperty(), pDisplayName);

			item.setPropertyValue(tools.getDescriptionProperty(), pDescription);

			if (pSiteId == null)
				pSiteId = SiteContextManager.getCurrentSiteId();

			item.setPropertyValue(tools.getSiteProperty(), pSiteId);
			
			item.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, pGiftListVO.getPrevPrice());
			item.setPropertyValue(BBBCoreConstants.MSGSHOWNOOS, true);
			
			if(!StringUtils.isEmpty(pGiftListVO.getRegistryID())){
				item.setPropertyValue(BBBCoreConstants.REGISTRY_ID, pGiftListVO.getRegistryID());
			}
			if(!StringUtils.isEmpty(pGiftListVO.getLtlShipMethod())){
				item.setPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD, pGiftListVO.getLtlShipMethod());
			}
			item.setPropertyValue(BBBCoreConstants.MSGSHIPMETHODUNSUPPORTED, pGiftListVO.isShipMethodUnsupported());
			
			//Start: BPSL-1963 | EXIM- Populate personalized item details
			if(BBBUtility.isNotEmpty(pGiftListVO.getReferenceNumber())){
				item.setPropertyValue(BBBCoreConstants.REFERENCE_NUMBER, pGiftListVO.getReferenceNumber());
				item.setPropertyValue(BBBCoreConstants.PERSONALIZE_PRICE, pGiftListVO.getPersonalizePrice());
				item.setPropertyValue(BBBCoreConstants.PERSONALIZATION_DETAILS, pGiftListVO.getPersonalizationDetails());
				item.setPropertyValue(BBBCoreConstants.PERSONALIZATION_OPTIONS, pGiftListVO.getPersonalizationOptions());
				item.setPropertyValue(BBBCoreConstants.FULL_IMAGE_PATH, pGiftListVO.getFullImagePath());
				item.setPropertyValue(BBBCoreConstants.MOBILE_FULL_IMAGE_PATH, pGiftListVO.getMobileFullImagePath());
				item.setPropertyValue(BBBCoreConstants.THUMBNAIL_IMAGE_PATH, pGiftListVO.getThumbnailImagePath());
				item.setPropertyValue(BBBCoreConstants.MOBILE_THUMBNAIL_IMAGE_PATH, pGiftListVO.getMobileThumbnailImagePath());
				item.setPropertyValue(BBBCoreConstants.PERSONALIZATION_STATUS, pGiftListVO.getPersonalizationStatus());
			}
			tools.addItem(item);
			itemId = item.getRepositoryId();
			
		} catch (RepositoryException e) {
			logError(e.getMessage(),e);
		}

		return itemId;

	}

	/**
	 * Merge and update gift list.
	 *
	 * @param pGiftListVOs the gift list v os
	 * @param pGiftlistId the giftlist id
	 * @throws RepositoryException the repository exception
	 */
	public void mergeAndUpdateGiftList(List<GiftListVO> pGiftListVOs, String pGiftlistId) throws RepositoryException {
		for (GiftListVO giftListVO : pGiftListVOs) {
			String siteId = SiteContextManager.getCurrentSiteId();
			//This will update or create new item entry
			if(giftListVO != null){
				try {
					addCatalogItemToGiftlist(giftListVO , pGiftlistId, siteId);
					} catch (CommerceException e) {
						logError(e.getMessage(),e);
				}
			}
		}
		//Retrive all User Items from Gift List and Save it in Session	
		/*List<GiftListVO> storedGiftList = new ArrayList<GiftListVO>();
		List<RepositoryItem> items = null; 
		items = getGiftlistItems(pGiftlistId);
		for (RepositoryItem item : items) {
			GiftListVO giftListVO = new GiftListVO();
			String skuId = (String) item.getPropertyValue("catalogRefId");
			String productId = (String) item.getPropertyValue("productId");
			String siteId = (String) item.getPropertyValue("siteId");
			String displayName = (String) item.getPropertyValue("displayName");
			long quantityDesired = Long.valueOf(item.getPropertyValue(
					"quantityDesired").toString());
			long quantityPurchased = Long.valueOf(item
					.getPropertyValue("quantityPurchased").toString());
			String prevPrice = Double.toString((Double) item.getPropertyValue("previousPrice")) ;
			String registryId = (String) item.getPropertyValue("registryId");

			giftListVO.setSkuID(skuId);
			giftListVO.setProdID(productId);
			giftListVO.setQuantity(quantityDesired);
			giftListVO.setRegistryID(registryId);
			if(null != prevPrice){
				giftListVO.setPrevPrice(Double.parseDouble(prevPrice.substring(1, prevPrice.length())));
			}
			
			// giftListVO.setRegistryId(registryId);

			storedGiftList.add(giftListVO);
		}
		
		DynamoHttpServletRequest pRequest=(DynamoHttpServletRequest ) ServletUtil.getCurrentRequest();
		BBBSavedItemsSessionBean savedItemsSessionBean = (BBBSavedItemsSessionBean)pRequest.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean");
		savedItemsSessionBean.setGiftListVO(storedGiftList);
		return storedGiftList;*/

	}

	/**
	 * Adds the catalog item to giftlist.
	 *
	 * @param giftListVO the gift list vo
	 * @param pGiftlistId the giftlist id
	 * @param siteId the site id
	 * @throws CommerceException the commerce exception
	 * @throws RepositoryException the repository exception
	 */
	public void addCatalogItemToGiftlist(GiftListVO giftListVO, String pGiftlistId, String siteId) throws CommerceException, RepositoryException {
        String displayName;
        String description;
        RepositoryItem sku;
        String skuId;
        String productId;
        RepositoryItem product;
        String giftId = null;
        String registryID = giftListVO.getRegistryID();
        String ltlShipMethod = giftListVO.getLtlShipMethod();
        String referenceNumber = giftListVO.getReferenceNumber();
        long quantity = giftListVO.getQuantity();
        try
        {
            displayName = null;
            description = null;
            sku = getCatalogTools().findSKU(giftListVO.getSkuID());
            if(sku == null)
                return;
        }
        catch(RepositoryException exc)
        {
            throw new CommerceException(exc);
        }
        skuId = sku.getRepositoryId();
        productId = giftListVO.getProdID();
        product = getCatalogTools().findProduct(productId);
        if(product != null)
        {
            productId = product.getRepositoryId();
            displayName = (String)product.getPropertyValue(getGiftlistTools().getDisplayNameProperty());
            description = (String)product.getPropertyValue(getGiftlistTools().getDescriptionProperty());
        }
    
        List items = getGiftlistItems(pGiftlistId);
        
         if (items != null) {
         Iterator iterator = items.iterator();
       
            while (iterator.hasNext()) {
               RepositoryItem item = (RepositoryItem)iterator.next();
               String regId = (String)item.getPropertyValue(BBBCoreConstants.REGISTRY_ID);
               String shipMethod = (String)item.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD);
               String itemReferenceNumber = (String)item.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER);
               if(null !=siteId && item.getPropertyValue(getGiftlistTools().getCatalogRefIdProperty()).equals(skuId) && item.getPropertyValue(getGiftlistTools().getSiteProperty()).equals(siteId)){
            	   if(BBBUtility.compareStringsIgnoreCase(regId, registryID) && BBBUtility.compareStringsIgnoreCase(shipMethod, ltlShipMethod)
            	   		&& BBBUtility.compareStringsIgnoreCase(itemReferenceNumber, referenceNumber)){
                	 giftId = ((String)item.getPropertyValue(BBBCoreConstants.ID));
                  }
            }
         }
         }
        if (giftId != null) {               
               increaseGiftlistItemQuantityDesired(pGiftlistId, giftId, quantity);
        } else if (quantity > 0L) {
        		if(StringUtils.isEmpty(registryID)){
        			registryID = null;
        		}
        		giftListVO.setShipMethodUnsupported(false);
                String itemId = createGiftlistItem(giftListVO, displayName, description, siteId);
               
               addItemToGiftlist(pGiftlistId, itemId);
        }
       
    }
	
	/**
	 * Adds the catalog item to giftlist.
	 *
	 * @param pSkuId the sku id
	 * @param pProductId the product id
	 * @param pGiftlistId the giftlist id
	 * @param pSiteId the site id
	 * @param pQuantity the quantity
	 * @param isMergeWishList the is merge wish list
	 * @return the string
	 */
	public String addCatalogItemToGiftlist(String pSkuId, String pProductId, String pGiftlistId, 
	    	String pSiteId, long pQuantity, boolean isMergeWishList) 
	    {

			String displayName = null;
	        String description = null;
	        String productId = null;
	        String skuId = null;
	        String giftId = null;
	        RepositoryItem sku = null;
	        RepositoryItem product = null;
	    	
			String errorMsg = null;

			if (pGiftlistId == null) {
	        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_PROFILE_NOT_MIGRATED);
				return MSG_ERROR_PROFILE_NOT_MIGRATED;
			}
	        
	        try
	        {
	            sku = getCatalogTools().findSKU(pSkuId);
	            if(sku == null){
	            	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_INVALID_SKU_IN_FEED);
	            	return MSG_ERROR_INVALID_SKU_IN_FEED;
	            }
	        }  catch(RepositoryException exc)    {
	        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_INVALID_SKU_IN_FEED);
	        	return MSG_ERROR_INVALID_SKU_IN_FEED;
	        }
	        boolean isStoreSku=(Boolean)sku.getPropertyValue(STORE_SKU);
	        if(isStoreSku){
	        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_STORE_SKU_IN_FEED);
	        	return MSG_ERROR_STORE_SKU_IN_FEED;
	        }
	        skuId = sku.getRepositoryId();
	        
	        try {
				productId=getBbbCatalogTools().getParentProductForSku(skuId,true);
				if(BBBUtility.isEmpty(productId)){
					logError(PROFILE_WISH_LIST_MIGRATION+MSG_PARENT_PRODUCT_NOT_FOUND);
		        	return MSG_PARENT_PRODUCT_NOT_FOUND;
				}			
			} catch (BBBBusinessException e) {
				logError(PROFILE_WISH_LIST_MIGRATION+MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
	        	return MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY;
			} catch (BBBSystemException e) {
				logError(PROFILE_WISH_LIST_MIGRATION+MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
	        	return MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY;
			}
	        
	        try{
	        		product = getCatalogTools().findProduct(productId);
	        		
	        	}catch (RepositoryException exc){
	        		logError("Profile-WishListMigration:"+MSG_PARENT_PRODUCT_NOT_FOUND);
	        		return MSG_PARENT_PRODUCT_NOT_FOUND;
	        	}

	        if(product != null)
	        {
	            productId = product.getRepositoryId();
	            displayName = (String)product.getPropertyValue(getGiftlistTools().getDisplayNameProperty());
	            description = (String)product.getPropertyValue(getGiftlistTools().getDescriptionProperty());
	        }
	        giftId = getGiftlistItemId(pGiftlistId, skuId, productId, pSiteId);
	        
	        //Merge only when allowed
	        if(giftId != null && isMergeWishList){
	        	//also update only when quantity is greater than 0L
	        	if(pQuantity > 0L) {
	        		increaseGiftlistItemQuantityDesired(pGiftlistId, giftId, pQuantity);
	        	} else{
	        		//log Error that quantity is OL so skipping
	            	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_NEW_GIFTLIST_EXIST_QTYINVALID);
	        		errorMsg =MSG_ERROR_NEW_GIFTLIST_EXIST_QTYINVALID;
	        	}
	        	
	        } else if(giftId != null){
	        	//Merge is not allowed
	        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_GIFTLIST_ITEM_ALREADY_EXIST);
	        	errorMsg = MSG_ERROR_GIFTLIST_ITEM_ALREADY_EXIST;
	        	
	        }
	        else if(pQuantity > 0L)
	        {
	            try{
	            	String itemId = createGiftlistItem(skuId, sku, productId, product, pQuantity, displayName, description, pSiteId);	
	            	addItemToGiftlist(pGiftlistId, itemId);
	            	
	            } catch(CommerceException comExcep){
	            	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM);
	            	errorMsg = MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM;
	            } catch(RepositoryException repExcep) {
	            	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM);
	            	errorMsg = MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM;
	            }
	        } else if( pQuantity <=0L){
	        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_INVALID_QTY);
	        	errorMsg = MSG_ERROR_INVALID_QTY;
	        }            
	        return errorMsg;
	    }
	/* Returns LocaleKeyService service.
	 * 
	 * @return RepositoryKeyService The service for which we will get the locale 
	 * to use proper language.
	 */
	/**
	 * Gets the key service.
	 *
	 * @return the key service
	 */
	public RepositoryKeyService getKeyService() {

		RepositoryKeyService repositoryKeyService = null;
		Nucleus nucleus = Nucleus.getGlobalNucleus();

		if (nucleus != null) {
			Object keyService = nucleus.resolveName("/atg/userprofiling/LocaleService");

			if (keyService instanceof RepositoryKeyService) {
				repositoryKeyService = (RepositoryKeyService)keyService;
			}
		}
		return repositoryKeyService;
	}
	

	/**
	 * Adds the item json object parser.
	 *
	 * @param pJsonResultString the json result string
	 * @param pOmniProdList the omni prod list
	 * @param pParentProduct the parent product
	 * @return the list
	 * @throws BBBBusinessException the bBB business exception
	 */
	public List<GiftListVO> addItemJSONObjectParser(String pJsonResultString, List<String> pOmniProdList, String pParentProduct)
			throws BBBBusinessException {
		final String logMessage = getClass().getName() + "addItemJSONObjectParser";
		if (isLoggingDebug()) {
			logDebug(logMessage + " Starts here");
			logDebug(logMessage + " add item input parameters --> " + pJsonResultString);
		}
		JSONObject jsonObject = null;
		PricingTools pricingTools = getPricingTools();		
		
		//-------------------------------------------
		try{
			jsonObject = (JSONObject) JSONSerializer.toJSON(pJsonResultString);
		}catch(JSONException e){
			logError("JSONException :: Invalid JSON String in addItemJSONObjectParser :: Received string pJsonResultString :: " + pJsonResultString);
			logError(e);
		}
		
		//-------------------------------------------
		
		DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);
		List<GiftListVO> additemList = new ArrayList<GiftListVO>();
		RepositoryKeyService keySvice = getKeyService();
		Object reqLocale = null;
		if(null != keySvice ){
			reqLocale = keySvice.getRepositoryKey();
		}
		String currencySymbol = null;
		DynamoHttpServletRequest pRequest=(DynamoHttpServletRequest ) ServletUtil.getCurrentRequest();
		final Profile profile = (Profile) pRequest.resolveName(ComponentName
					.getComponentName(BBBCoreConstants.ATG_PROFILE));
		Boolean internationalContext=false;
	    internationalContext= (Boolean) profile.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT);
		String countryCode=null;
		if(internationalContext!=null && internationalContext )
		{
			 countryCode=	(String) profile.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
			 currencySymbol=	(String) profile.getPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE);
		}
		else{
		 if(reqLocale instanceof Locale)
         {
             PricingTools _tmp2 = pricingTools;
             currencySymbol = PricingTools.getCurrencySymbol((Locale)(Locale)reqLocale);
         } else
         {
             PricingTools _tmp3 = pricingTools;
             currencySymbol = PricingTools.getCurrencySymbol((String)(String)reqLocale);
         }
		}
			if (dynaBeanProperties.contains(BBBCoreConstants.ADDITEMRESULTS)) {
				 @SuppressWarnings("unchecked")
				List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean.get(BBBCoreConstants.ADDITEMRESULTS);
				 HashMap<String,String> eximMap = getEximPricingManager().getEximValueMap();
				
					for(DynaBean item : itemArray)
					{
						GiftListVO giftListVO=new GiftListVO();
						giftListVO.setSkuID(item.get(BBBCoreConstants.SKUID).toString());
						giftListVO.setProdID(item.get(BBBCoreConstants.PRODID).toString());						
						giftListVO.setPrice(item.get(BBBCoreConstants.PRICE).toString());
						
						if(!BBBUtility.isNumericOnly(item.get(BBBCoreConstants.QTY).toString())){
							logError("BBBGiftListManager.addItemJSONObjectParser() :: Invalid quantity in Item : " + item.get(BBBCoreConstants.QTY).toString());
							throw new NumberFormatException("Invalid quantity in Item");
						}
						giftListVO.setQuantity(Long.parseLong(item.get(BBBCoreConstants.QTY).toString()));
						if(item.getDynaClass().getDynaProperty(BBBCoreConstants.REGISTRY_ID) != null){
							giftListVO.setRegistryID((String)item.get(BBBCoreConstants.REGISTRY_ID));
						}
						if(item.getDynaClass().getDynaProperty(BBBCatalogConstants.LTL_SHIP_METHOD) != null){
							giftListVO.setLtlShipMethod((String)item.get(BBBCatalogConstants.LTL_SHIP_METHOD));
						}
						giftListVO.setWishListItemId("tempItem" + Math.random());
						String price = giftListVO.getPrice();
						if(!StringUtils.isEmpty(price) && price.contains(currencySymbol)){
							price = price.replace(currencySymbol, BBBCoreConstants.BLANK);	
							if(price.contains(BBBCoreConstants.COMMA)){
								price = price.replace(BBBCoreConstants.COMMA, BBBCoreConstants.BLANK);
							}
							double doublePrice = Double.parseDouble(price.trim());
							giftListVO.setPrevPrice(doublePrice);
							String format=TWO;
							Integer scale = BBBCoreConstants.TWO;
							if(BBBUtility.isNotEmpty(currencySymbol) && getLocalcache().get(currencySymbol)!=null){
								BBBInternationalCurrencyDetailsVO currencyDetailsVO = (BBBInternationalCurrencyDetailsVO) getLocalcache().get(currencySymbol);
								scale = currencyDetailsVO.getScale();
								format = scale.toString();
								if(!format.equals(ZERO) && !(format.equals(TWO))){
									format=TWO;
								}
							}
							String strDouble = String.format("%."+format+"f", doublePrice*giftListVO.getQuantity());  
							
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.REFERENCE_NUMBER_PARAM) != null && item.get(BBBCoreConstants.REFERENCE_NUMBER_PARAM) != null && !StringUtils.isEmpty(item.get(BBBCoreConstants.REFERENCE_NUMBER_PARAM).toString())){	
								if(item.get(BBBCoreConstants.PERSIONALIZATION_OPTION) != null){																	
							if(pOmniProdList != null){
											pOmniProdList.add(BBBCoreConstants.SEMICOLON+ giftListVO.getProdID() +BBBCoreConstants.OMNI_SYMBOL_1+ giftListVO.getQuantity()+BBBCoreConstants.OMNI_SYMBOL_2+(strDouble)+ ";eVar30="+giftListVO.getSkuID() + BBBCoreConstants.OMNI_SYMBOL_3 + eximMap.get(item.get(BBBCoreConstants.PERSIONALIZATION_OPTION).toString()));								
							}
						}
							}
							else
								{
									if(pOmniProdList != null){
										pOmniProdList.add(BBBCoreConstants.SEMICOLON+ giftListVO.getProdID() +BBBCoreConstants.OMNI_SYMBOL_1+ giftListVO.getQuantity()+BBBCoreConstants.OMNI_SYMBOL_2+(strDouble)+ ";eVar30="+giftListVO.getSkuID());
									}
								}	
						}

						//Start: BPSL-1963 | EXIM- Modify/Create ATG Components to populate personalized item details
						if(null != item.getDynaClass().getDynaProperty(BBBCoreConstants.REFERENCE_NUMBER_PARAM) && null != item.get(BBBCoreConstants.REFERENCE_NUMBER_PARAM)
								&& ((String)item.get(BBBCoreConstants.REFERENCE_NUMBER_PARAM)).trim().length() != 0){
							
							giftListVO.setReferenceNumber(item.get(BBBCoreConstants.REFERENCE_NUMBER_PARAM).toString());
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.FULL_IMAGE_PATH_PARAM) != null){
								giftListVO.setFullImagePath((String)item.get(BBBCoreConstants.FULL_IMAGE_PATH_PARAM));
							}
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.THUMBNAIL_IMAGE_PATH_PARAM) != null){
								giftListVO.setThumbnailImagePath((String)item.get(BBBCoreConstants.THUMBNAIL_IMAGE_PATH_PARAM));
							}
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.MOBILE_FULL_IMAGE_PATH_PARAM) != null){
								giftListVO.setMobileFullImagePath((String)item.get(BBBCoreConstants.MOBILE_FULL_IMAGE_PATH_PARAM));
							}
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.MOBILE_THUMBNAIL_IMAGE_PATH_PARAM) != null){
								giftListVO.setMobileThumbnailImagePath((String)item.get(BBBCoreConstants.MOBILE_THUMBNAIL_IMAGE_PATH_PARAM));
							}
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.PERSONALIZE_PRICE_PARAM) != null){
								if(BBBUtility.isNotEmpty((String)item.get(BBBCoreConstants.PERSONALIZE_PRICE_PARAM))){
									giftListVO.setPersonalizePrice(Double.valueOf((String)item.get(BBBCoreConstants.PERSONALIZE_PRICE_PARAM)));
								}else{
									giftListVO.setPersonalizePrice(0.0);
								}
							}
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.PERSONALIZATION_OPTIONS_PARAM) != null){
								giftListVO.setPersonalizationOptions((String)item.get(BBBCoreConstants.PERSONALIZATION_OPTIONS_PARAM));
							}
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.PERSONALIZATION_DETAILS_PARAM) != null){
								giftListVO.setPersonalizationDetails((String)item.get(BBBCoreConstants.PERSONALIZATION_DETAILS_PARAM));
							}
							if(item.getDynaClass().getDynaProperty(BBBCoreConstants.PERSONALIZATION_STATUS_PARAM) != null){
									giftListVO.setPersonalizationStatus((String)item.get(BBBCoreConstants.PERSONALIZATION_STATUS_PARAM));
							}
						}
						try {
						double doublePrice = 0.0;
						doublePrice = ((BBBCatalogTools)getBbbCatalogTools()).getEffectivePrice(giftListVO.getProdID(), giftListVO.getSkuID(),giftListVO.getReferenceNumber(),giftListVO.getPersonalizePrice());
						giftListVO.setPrevPrice(doublePrice);
						} catch (final BBBSystemException e) {
							logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
						}
						
						//End: BPSL-1963 | EXIM- Modify/Create ATG Components to populate personalized item details
						
						additemList.add(giftListVO);
					}
			}
			
//			if (dynaBeanProperties.contains(BBBCoreConstants.PARENTPRODID)) {
//				 @SuppressWarnings("unchecked")
//				String parentProdId = JSONResultbean.get(BBBCoreConstants.PARENTPRODID).toString();
//				if(null != parentProdId){
//					pParentProduct = parentProdId;	
//				}				
//			}
			
			
		return additemList;
	}
	
	/**
	 * Parses the saved item list json.
	 *
	 * @param JSONResultbean the jSON resultbean
	 * @param siteId the site id
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<GiftListVO> parseSavedItemListJson(DynaBean JSONResultbean, String siteId) {
		List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);
		List<GiftListVO> itemsList = new ArrayList<GiftListVO>();
		if (dynaBeanProperties.contains("savedItemsList") &&
				JSONResultbean.get("savedItemsList") != null) {
			
			DynaClass dynaClass = JSONResultbean.getDynaClass();
    		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
    		for (int i = 0; i < dynaProp.length; i++) {
    			String name = dynaProp[i].getName();
    			if (name.equals("savedItemsList"))
    			{
					List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean.get("savedItemsList");
					Map<String,EximCustomizedAttributesVO> eximRefNumMap = getEximDetailsMapByMultiRefNum(itemArray);
    				for (DynaBean item : itemArray)
    				{
						GiftListVO giftListVO = new GiftListVO();
						List<String> itemProperties = (ArrayList<String>) getPropertyNames(item);
						giftListVO = new GiftListVO();
						String skuId = item.get("s").toString();
    					giftListVO.setSkuID(item.get("s").toString());
    					giftListVO.setPrevPrice(Double.parseDouble(item.get("pp").toString()));
    					giftListVO.setProdID(item.get("p").toString());
    					giftListVO.setQuantity(Long.parseLong((item.get("q").toString())));
    					giftListVO.setWishListItemId(item.get("w").toString());
    					if (itemProperties.contains("refNum")) {
    						giftListVO.setReferenceNumber(item.get("refNum").toString());
    					}
    					String refNum = giftListVO.getReferenceNumber();
    					if(null != refNum && BBBUtility.isNotEmpty(refNum) && !eximRefNumMap.isEmpty()){
    						setPersonalItemDetailsForSFL(giftListVO,eximRefNumMap.get(refNum));
    					} else if(BBBUtility.isNotEmpty(refNum) && eximRefNumMap.isEmpty()) {
    						giftListVO.setEximErrorExists(Boolean.TRUE);
    					}
    					Object obj = item.get("oos");
    					if(obj != null && obj instanceof String){
    						giftListVO.setMsgShownOOS(Boolean.parseBoolean((String)(item.get("oos").toString())));
    					} else if(obj != null && obj instanceof Boolean){
    						giftListVO.setMsgShownOOS(Boolean.valueOf((Boolean) item.get("oos")));
    					}
						if (itemProperties.contains("r")) {
							giftListVO.setRegistryID(item.get("r").toString());
						}			
						if (itemProperties.contains("sm")) {
							giftListVO.setLtlShipMethod(item.get("sm").toString());
							
							//Get the shippingmethod availablity of sku and set the result in transient property of gift list
							String shippingMethod = item.get("sm").toString();
							try{
								boolean isSkuLTL = getBbbCatalogTools().isSkuLtl(siteId, skuId);
								if(BBBUtility.isNotEmpty(shippingMethod) && isSkuLTL){
									boolean isAssemblyOffered = false;
									if(shippingMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
										shippingMethod = BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD;
										isAssemblyOffered = true;
									}
									//set in transient property of gift list
									boolean isShippingMethodExistsForSku = getBbbCatalogTools().isShippingMethodExistsForSku(siteId, skuId, shippingMethod, isAssemblyOffered);
									giftListVO.setShipMethodUnsupported(!isShippingMethodExistsForSku);
								}
							}catch(BBBSystemException e){
									String msg = "Error while retrieving product details for item [" + skuId + "]";
	                            	this.logError(msg, e);
							}
							catch(BBBBusinessException e){
									String msg = "Error while retrieving product details for item [" + skuId + "]";
	                            	this.logError(msg, e);
							}
						}
    					itemsList.add(giftListVO);
				
    				}
    			}
    		}
    		
		}
		return itemsList;
	}
	
	

	/**
	 * Gets the exim details map by multi ref num.
	 *
	 * @param itemArray the item array
	 * @return the exim details map by multi ref num
	 */
	public Map<String, EximCustomizedAttributesVO> getEximDetailsMapByMultiRefNum(List<DynaBean> itemArray) {
	
		logDebug("BBBGiftListManager.getEximDetailsbyMultiRefNum START");
		Map<String,EximCustomizedAttributesVO> eximResponseMap = new HashMap<String,EximCustomizedAttributesVO>();
	//	List<String> eximRequestList = new ArrayList<String>();
		EximSummaryResponseVO eximResponse = null;
		List<EximCustomizedAttributesVO> eximAtrributeVOList = null;
		
		Map<String, List<String>> vendorToRefNum = new HashMap<String, List<String>>();		
		
		if("true".equalsIgnoreCase(getEximPricingManager().getKatoriAvailability())) {
			
		/**
		 * creating Map of vendor name and list of all the reference numbers associated with the respective vendor.
		 */
		for (DynaBean item : itemArray) {
			List<String> itemProperties = (ArrayList<String>) getPropertyNames(item);
			if (itemProperties.contains(BBBCoreConstants.REFERENCE_NUMBER_PARAM) && itemProperties.contains("p")) {
				String refNum = String.valueOf(item.get(BBBCoreConstants.REFERENCE_NUMBER_PARAM));				
				String productId = String.valueOf(item.get("p"));
				String vendorName = getVendorName(productId);				
				createVendorNameMap(vendorToRefNum, refNum, vendorName);				
			}
		}
		
		/**
		 * Iterating Map by vendor name and fetching the response by multi reference number API.
		 */
		for(Map.Entry<String, List<String>> mp : vendorToRefNum.entrySet()){
			
			try {		
				
				eximResponse = getEximPricingManager().invokeSummaryAPI(null,mp.getKey(),mp.getValue().toArray(new String[mp.getValue().size()]));
				if(null != eximResponse && null != eximResponse.getCustomizations() && !eximResponse.getCustomizations().isEmpty()) {
					eximAtrributeVOList = eximResponse.getCustomizations();
					logDebug("BBBGiftlistManager.getEximDetailsbyMultiRefNum Response is not null or empty from Exim Multi Ref Service");
					     for( final EximCustomizedAttributesVO eximAtrributeVO: eximAtrributeVOList) {
						        eximResponseMap.put(eximAtrributeVO.getRefnum(), eximAtrributeVO);
							}
				}	
			} catch (BBBBusinessException e) {
				logDebug("Exception occured while calling EXIM webservice" + e);
			} catch (BBBSystemException e) {
				logDebug("Exception occured while calling EXIM webservice" + e);
			} 
			
		
			}
		}
		logDebug("BBBGiftlistManager.getEximDetailsbyMultiRefNum ENDS. Exim response map : " + eximResponseMap);
		return eximResponseMap;
	}

	private void createVendorNameMap(Map<String, List<String>> vendorToRefNum,
			String refNum, String vendorName) {
		if(!BBBUtility.isEmpty(vendorName) && !BBBUtility.isEmpty(refNum)){							
			if(vendorToRefNum.get(vendorName) == null){				
				List<String> refNumList = new ArrayList<String>();					
				refNumList.add(refNum);
				vendorToRefNum.put(vendorName, refNumList);
			}else{					
				List<String> refNumList=vendorToRefNum.get(vendorName);						
				refNumList.add(refNum);								
			}
		}
	}

	private String getVendorName(String productId) {
		String vendorName = null;				
		try {
			vendorName = getBbbCatalogTools().getVendorInfo( 																// fetching vendor Name based on product id
					getBbbCatalogTools().getProductVOMetaDetails(SiteContextManager.getCurrentSite().getId(), productId).getVendorId()).getVendorName();
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Exception while fetching vendor Name for the product" + e );
		}
		return vendorName;
	}
	
	/**
	 * This method sets eximResponse for SFL in GiftlistVO.
	 *
	 * @param giftListVO the gift list vo
	 * @param eximAtrributeVO the exim atrributes
	 */
	private void setPersonalItemDetailsForSFL(GiftListVO giftListVO, EximCustomizedAttributesVO eximAtrributeVO) {
		
	    if (null != eximAtrributeVO && (null == eximAtrributeVO.getErrors() || eximAtrributeVO.getErrors().isEmpty())) {
	    	logDebug("Inside setPersonalItemDetailsForSFL: refNum = " + eximAtrributeVO.getRefnum());
			giftListVO.setPersonalizePrice(eximAtrributeVO.getRetailPriceAdder());
			giftListVO.setPersonalizationOptions(eximAtrributeVO.getCustomizationService());
			giftListVO.setPersonalizationDetails(eximAtrributeVO.getNamedrop());
			giftListVO.setPersonalizationStatus(eximAtrributeVO.getCustomizationStatus());
			List<EximImagesVO> images = eximAtrributeVO.getImages();
			for(EximImagesVO imageVO : images) {
				if(imageVO.getId().equalsIgnoreCase(BBBCoreConstants.IMAGE_ID) || imageVO.getId().equalsIgnoreCase("")) {
				List<EximImagePreviewVO> previews = imageVO.getPreviews();
				  for (EximImagePreviewVO preview : previews) { 
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_LARGE)) 
						   giftListVO.setFullImagePath(preview.getUrl());
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_X_SMALL)) {
						   giftListVO.setThumbnailImagePath(preview.getUrl());
						   giftListVO.setMobileThumbnailImagePath(preview.getUrl());
					   }
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_SMALL))
						   giftListVO.setMobileFullImagePath(preview.getUrl());
					}
				  break;
				}
			}
		} else {
			giftListVO.setEximErrorExists(Boolean.TRUE);
		}
	}
	
	
	
	/**
	 * It's an overloaded method with extra parameter registryId which fetch wishlist item
	 * details based on parameter.
	 *
	 * @param pGiftlistId the giftlist id
	 * @param pSkuId the sku id
	 * @param pProductId the product id
	 * @param pSiteId the site id
	 * @param pRegistryId the registry id
	 * @param ltlShipMethod the ltl ship method
	 * @param referenceNumber the reference number
	 * @return giftId
	 */
	public String getGiftlistItemId(String pGiftlistId, String pSkuId, String pProductId, String pSiteId, String pRegistryId, String ltlShipMethod, String referenceNumber) {
		if (isLoggingDebug()) {
			logDebug("Inside getGiftlistItemId: giftlist id = " + pGiftlistId + ", skuId = " + pSkuId + ", productId = " + pProductId + ", siteId = " + pSiteId);
		}

		if (pSiteId == null) {
			pSiteId = SiteContextManager.getCurrentSiteId();
		}
		String giftId = null;
		List items = getGiftlistItems(pGiftlistId);

		if (items != null) {
			Iterator iterator = items.iterator();

			while (iterator.hasNext()) {
				RepositoryItem item = (RepositoryItem) iterator.next();
				String regId = (String) item.getPropertyValue(BBBCoreConstants.REGISTRY_ID);
				String shipMethod = (String)item.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD);
				String itemReferenceNumber = (String)item.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER);

	               if(null !=pSiteId && item.getPropertyValue(getGiftlistTools().getCatalogRefIdProperty()).equals(pSkuId) && item.getPropertyValue(getGiftlistTools().getSiteProperty()).equals(pSiteId)){
	            	   if(BBBUtility.compareStringsIgnoreCase(regId, pRegistryId) && BBBUtility.compareStringsIgnoreCase(shipMethod, ltlShipMethod)
	               	   		&& BBBUtility.compareStringsIgnoreCase(itemReferenceNumber, referenceNumber)){
					giftId = ((String) item.getPropertyValue(BBBCoreConstants.ID));
				}
			}
		}
		}
		return giftId;

	}

	/**
	 * This method gets the gift list Repository Item and calls another method to update the personalized item details to Repository.
	 *
	 * @param giftListVO the gift list vo
	 * @throws CommerceException the commerce exception
	 * @throws RepositoryException the repository exception
	 */
	public void updatePersonlizedItemDetails(GiftListVO giftListVO)
			throws CommerceException, RepositoryException {
		this.logDebug("BBBGiftlistManager ::updatePersonlizedItemDetails method -  start ");
	
		final MutableRepository giftRep = (MutableRepository) getGiftlistTools().getGiftlistRepository();
		final MutableRepositoryItem giftItem = (MutableRepositoryItem) getGiftitem(giftListVO.getWishListItemId());
	
		if(null != giftItem){
		   String itemRefNum = (String)giftItem.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER);
		   this.logDebug("BBBGiftlistManager ::updatePersonlizedItemDetails Reference Number of item is :  " + itemRefNum +
				   ". Reference Number from response is :" + giftListVO.getReferenceNumber());
		   
		   if(null != itemRefNum){
			   this.logDebug("BBBGiftlistManager ::updatePersonlizedItemDetails Check if reference number in response is equal to the reference number stored in repository");
			   if(itemRefNum.equalsIgnoreCase(giftListVO.getReferenceNumber())) {
					String prodId = (String)giftItem.getPropertyValue(BBBCoreConstants.PRODUCTID);
					String skuId = (String)giftItem.getPropertyValue(BBBCoreConstants.CATALOG_REF_ID);
					try {
						KatoriPriceRestVO katoriPriceRestVO=getEximPricingManager().getPriceByRef(prodId, itemRefNum, skuId, (String)giftItem.getPropertyValue(BBBCoreConstants.SITE_ID),false);
						if(katoriPriceRestVO != null &&  !StringUtils.isBlank((katoriPriceRestVO.getKatoriItemPrice())))
						{  
						this.logDebug("BBBGiftlistManager ::updatePersonlizedItemDetails :Updating price from SummaryAPI:["+katoriPriceRestVO.getKatoriItemPrice()+"]");
						double _mPrice= Double.parseDouble(katoriPriceRestVO.getKatoriItemPrice().trim());
						giftListVO.setPersonalizePrice(_mPrice);
						}
						double doublePrice = 0.0;
						doublePrice = getBbbCatalogTools().getEffectivePrice(prodId, skuId,giftListVO.getReferenceNumber(), giftListVO.getPersonalizePrice());
						giftListVO.setPrevPrice(doublePrice);
						
						}catch(BBBSystemException e){
							String msg = "Error while retrieving product details for item [" + skuId + "]";
                        	this.logError(msg + e.getMessage(), e);
						}catch(BBBBusinessException e){
							String msg = "Error while retrieving product details for item [" + skuId + "]";
                        	this.logError(msg + e.getMessage(), e);
					}
				   	//Update the gift list item with personalized details
				    updatePersonalizedDetailsToRepository(giftItem,giftListVO);
				    giftRep.updateItem(giftItem);
			   }else{
				   this.logDebug("BBBGiftlistManager ::updatePersonlizedItemDetails -" + BBBCoreConstants.ERR_EDIT_INVALID_REF_NUM_MSG);
			   }
		   }else{
			   this.logDebug("BBBGiftlistManager ::updatePersonlizedItemDetails - " + BBBCoreConstants.ERR_EDIT_REF_NUM_NOT_EXIST_MSG);
		   }
		}
		 this.logDebug("BBBGiftlistManager ::updatePersonlizedItemDetails method -  End ");
	}
	
	/**
	 * This method update the personalized item details to Repository.
	 *
	 * @param giftItem the gift item
	 * @param giftListVO the gift list vo
	 */
	protected void updatePersonalizedDetailsToRepository(final MutableRepositoryItem giftItem, GiftListVO giftListVO){
		this.logDebug("BBBGiftlistManager ::updatePersonalizedDetailsToRepository method -  start ");

		giftItem.setPropertyValue(BBBCoreConstants.REFERENCE_NUMBER, giftListVO.getReferenceNumber());
		giftItem.setPropertyValue(BBBCoreConstants.PERSONALIZATION_DETAILS, giftListVO.getPersonalizationDetails());
		giftItem.setPropertyValue(BBBCoreConstants.PERSONALIZATION_OPTIONS, giftListVO.getPersonalizationOptions());
		giftItem.setPropertyValue(BBBCoreConstants.PERSONALIZATION_STATUS, giftListVO.getPersonalizationStatus());
		giftItem.setPropertyValue(BBBCoreConstants.PERSONALIZE_PRICE, giftListVO.getPersonalizePrice());
		giftItem.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, giftListVO.getPrevPrice());
		giftItem.setPropertyValue(BBBCoreConstants.MOBILE_FULL_IMAGE_PATH, giftListVO.getMobileFullImagePath());
		giftItem.setPropertyValue(BBBCoreConstants.MOBILE_THUMBNAIL_IMAGE_PATH, giftListVO.getMobileThumbnailImagePath());
		giftItem.setPropertyValue(BBBCoreConstants.THUMBNAIL_IMAGE_PATH, giftListVO.getThumbnailImagePath());
		giftItem.setPropertyValue(BBBCoreConstants.FULL_IMAGE_PATH, giftListVO.getFullImagePath());
		
		this.logDebug("BBBGiftlistManager ::updatePersonalizedDetailsToRepository method -  end ");
	}
	
	/*
	 * To get the properties names from JSON result string
	 */
	/**
	 * Gets the property names.
	 *
	 * @param pDynaBean the dyna bean
	 * @return the property names
	 */
	public List<String> getPropertyNames(DynaBean pDynaBean) {
		List<String> propertyNames = new ArrayList<String>();
		if(pDynaBean!= null ){
		DynaClass dynaClass = pDynaBean.getDynaClass();
		DynaProperty properties[] = dynaClass.getDynaProperties();
			for (int i = 0; i < properties.length; i++) {
				String name = properties[i].getName();
				propertyNames.add(name);
			}
		}
		return propertyNames;
	}

	/**
	 * Gets the bbb custom tag component.
	 *
	 * @return the bbb custom tag component
	 */
	public BBBCustomTagComponent getBbbCustomTagComponent() {
		return bbbCustomTagComponent;
	}

	/**
	 * Sets the bbb custom tag component.
	 *
	 * @param bbbCustomTagComponent the new bbb custom tag component
	 */
	public void setBbbCustomTagComponent(BBBCustomTagComponent bbbCustomTagComponent) {
		this.bbbCustomTagComponent = bbbCustomTagComponent;
	}

	/**
	 * Gets the bbb catalog tools.
	 *
	 * @return the bbb catalog tools
	 */
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	/**
	 * Sets the bbb catalog tools.
	 *
	 * @param bbbCatalogTools the new bbb catalog tools
	 */
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	
	
	
}
