package com.bbb.commerce.order.purchase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.commerce.util.PipelineErrorHandler;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO;
import com.bbb.commerce.catalog.vo.GiftWrapVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.shipping.BBBShippingInfoBean;
import com.bbb.common.vo.CommerceItemShipInfoVO;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.ecommerce.order.BBBShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.utils.BBBUtility;

/**
 * 
 *
 */
public class BBBPurchaseProcessHelper extends PurchaseProcessHelper {

    /** Class version string. */
    public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/order/purchase/StorePurchaseProcessHelper.java#2 $$Change: 633752 $";

    /**
     * Missing required address property.
     */
    public static final String MISS_REQ_ADDR_PROP = "missingRequiredAddressProperty";

    /**
     * Pricing error invalid address message key.
     */
    public static final String PRICING_ERR_ADDR = "pricingErrorInvalidAddress";

    /**
     * Pricing error message key.
     */
    public static final String PRICING_ERROR = "pricingError";

    /**
     * Multiple coupons per order error message key.
     */
    public static final String MSG_MULTI_COUPONS = "multipleCouponsPerOrder";

    /**
     * Uncliamable coupon error message key.
     */
    public static final String UNCLAIMABLE_COUPON = "couponNotClaimable";

    private static final String NULL_SKU_ID = "nullSkuId";
    private static final String REGISTRY_ID = "registryId";
    private static final String STORE_ID = "storeId";
    private static final String BTS = "bts";
    private static final String ERR_INV_CHK = "inventoryCheckFail";
    private static final String ECOFEE_KEY = "EcoFeeEligible";
    private static final String UNDERSCORE = "_";
    private static final String TRUE = "TRUE";
    private static final String BLANK = "";
    private static final String US_COUNTRY_CODE = "US";
	private static final String CA_COUNTRY_CODE = "CA";
    private BBBInventoryManager inventoryManager;
    private BBBCheckoutManager checkoutManager;
    private BBBCatalogTools catalogTools;
    private GiftRegistryManager giftRegistryManager;
    private ClaimableManager claimableManager;

    private Map addressPropertyNameMap;
    private Map<String, String> eximDemoSkuAndRefNum;
    private boolean eximDemo;


    /**
	 * @return the eximDemo
	 */
	public boolean isEximDemo() {
		return this.eximDemo;
	}

	/**
	 * @param eximDemo the eximDemo to set
	 */
	public void setEximDemo(boolean eximDemo) {
		this.eximDemo = eximDemo;
	}

	/**
	 * @return the eximDemoSkuAndRefNum
	 */
	public Map<String, String> getEximDemoSkuAndRefNum() {
		return this.eximDemoSkuAndRefNum;
	}

	/**
	 * @param eximDemoSkuAndRefNum the eximDemoSkuAndRefNum to set
	 */
	public void setEximDemoSkuAndRefNum(Map<String, String> eximDemoSkuAndRefNum) {
		this.eximDemoSkuAndRefNum = eximDemoSkuAndRefNum;
	}

	/**
     * @return Inventory Manager
     */
    public BBBInventoryManager getInventoryManager() {
		return this.inventoryManager;
	}
    
    /**
     * @param inventoryManager
     */
	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}
	/**
     * @return Catalog Tools
     */
    public BBBCatalogTools getCatalogTools() {
        return this.catalogTools;
    }
    /**
     * @param catalogTools
     */
    public void setCatalogTools(final BBBCatalogTools catalogTools) {
        this.catalogTools = catalogTools;
    }

    /**
     * @return GiftRegistryManager
     */
    public GiftRegistryManager getGiftRegistryManager() {
        return this.giftRegistryManager;
    }

    /**
     * @param giftRegistryManager
     */
    public void setGiftRegistryManager(final GiftRegistryManager giftRegistryManager) {
        this.giftRegistryManager = giftRegistryManager;
    }

    /**
     * override the super class method to add additional attributes like -
     *
     * @param pItemInfo
     * @param pCatalogKey
     * @param pOrder
     *
     */
    @Override
    protected CommerceItem createCommerceItem(final AddCommerceItemInfo pItemInfo,
            final String pCatalogKey, final Order pOrder) throws CommerceException {
        this.logDebug(new StringBuilder().append("createCommerceItem() : creating CommerceItem for product id ").append(pItemInfo.getProductId()).toString() );
        final CommerceItemManager cimgr = this.getCommerceItemManager();
        final String siteId = ( StringUtils.isBlank(pItemInfo.getSiteId())) ? getCurrentSiteId() : pItemInfo.getSiteId();
        vlogDebug("BBBPurchaseProcessHelper.createCommerceItem: creating commerceitem with skuId {0}", pItemInfo.getCatalogRefId());
        BBBCommerceItem ci  = (BBBCommerceItem)cimgr.createCommerceItem(
                pItemInfo.getCommerceItemType(), pItemInfo.getCatalogRefId(),
                null, pItemInfo.getProductId(), null, pItemInfo.getQuantity(),
                pCatalogKey, null, siteId, null);

        this.logDebug("BBBPurchaseProcessHelper.createCommerceItem:: BBBCommerceItem before addition of BBBProperties " + ci.toString());
        this.addBBBProperties(pItemInfo, ci, pOrder, siteId);
        List<BBBCommerceItem> existingCIs = null;
        if(ci.getBts() == false && pOrder.getCommerceItemCount() >0){
			existingCIs = fetchExistingCommerceItem(pOrder, ci);
			if ((existingCIs != null) && !existingCIs.isEmpty()) {
                // Loop through the items and add bts property
                for (final BBBCommerceItem commerceItem : existingCIs) {
                    if(commerceItem.getBts() == true){
                    	ci.setBts(true);
                    	pItemInfo.getValue().put(BTS, true);
                    	break;
                    }
                }
            }
        }
        vlogDebug("BBBPurchaseProcessHelper.createCommerceItem: Adding commerceitem: {0} to order: {1} having skuId: {2} and product id: {3}", ci,pOrder, ci.getCatalogRefId(), ci.getAuxiliaryData().getProductId());
        ci = (BBBCommerceItem) cimgr.addItemToOrder(pOrder, ci);
            this.logDebug("BBBPurchaseProcessHelper.createCommerceItem:: BBBCommerceItem after addition of BBBProperties " + ci.toString());
            this.logDebug("createCommerceItem() : Added extra BBB properties into CommerceItem Created " );
        return ci;
    }

	@SuppressWarnings("unchecked")
	protected List<BBBCommerceItem> fetchExistingCommerceItem(final Order pOrder,
			BBBCommerceItem ci) {
		List<BBBCommerceItem> existingCIs = null;
		try {
			existingCIs = pOrder.getCommerceItemsByCatalogRefId(ci.getCatalogRefId());
		} catch (CommerceItemNotFoundException e) {
			logDebug("no commerce item found for the sku");
		} catch (InvalidParameterException e) {
			logDebug("no commerce item found for the sku");
		}
		return existingCIs;
	}

    /**
	 * @param bbbObject
	 * @param info
	 */
	public void eximDemoCode(final BBBCommerceItem bbbObject,
			final AddCommerceItemInfo info) {

		if(this.isEximDemo()){
			if (info.getCatalogRefId().equalsIgnoreCase("18384876") || info.getCatalogRefId().equalsIgnoreCase("18392518")
					|| info.getCatalogRefId().equalsIgnoreCase("17823833")){
				Map<String, String> skuAndRefNumMap = this.getEximDemoSkuAndRefNum();
				String referenceNumber = skuAndRefNumMap.get(info.getCatalogRefId());
				bbbObject.setReferenceNumber(referenceNumber);
			}
		}
		
	}

    /**
     * @param pItemInfo
     * @param pCommerceItem
     * @param pOrder
     * @param pSiteId
     * @return BBBCommerceItem
     * @throws CommerceException
     */
	public BBBCommerceItem addBBBProperties(final AddCommerceItemInfo pItemInfo, final BBBCommerceItem pCommerceItem,
            final Order pOrder, final String pSiteId)
                    throws CommerceException{

        SKUDetailVO skuDetailVO = null;
        @SuppressWarnings ("unchecked")
        final Dictionary<String, String> value = pItemInfo.getValue();
        RegistrySummaryVO registrySummaryVO = null;
        final BBBCommerceItem tempItem = pCommerceItem;
        if (value != null) {
            tempItem.setRegistryId(value.get(REGISTRY_ID));
            final BBBOrderImpl bbbOrder = (BBBOrderImpl) pOrder;
            if( (value.get(REGISTRY_ID) != null) && !StringUtils.isEmpty(value.get(REGISTRY_ID).toString())){
                registrySummaryVO = (RegistrySummaryVO) bbbOrder.getRegistryMap().get(
                        value.get(REGISTRY_ID));
                if(registrySummaryVO == null){
                    try{
                        registrySummaryVO = this.getGiftRegistryManager().getRegistryInfo( value.get(REGISTRY_ID), pSiteId);
                    }catch (final BBBBusinessException e) {
                        throw new CommerceException(e);
                    }catch (final BBBSystemException e) {
                        throw new CommerceException(e);
                    }
                }
                if(null != registrySummaryVO){
                    tempItem.setRegistryInfo( getRegistryInfo(registrySummaryVO));
                }

            }

            // added dummy code for BPSI-1303 demo , we will remove this after demo
            /*if(BBBUtility.isEmpty(value.get("referenceNumber"))){
            	eximDemoCode(tempItem, pItemInfo);
            } else {
            	tempItem.setReferenceNumber(value.get("referenceNumber"));
            }*/
            tempItem.setReferenceNumber(value.get("referenceNumber"));
            tempItem.setEximErrorExists(Boolean.parseBoolean(value.get("eximErrorExists")));

            if(BBBUtility.isNotEmpty(value.get("eximPricingReq"))) {
            	tempItem.setEximPricingReq(Boolean.parseBoolean(value.get("eximPricingReq")));
            } else if(BBBUtility.isNotEmpty(value.get("referenceNumber"))){
            	tempItem.setEximPricingReq(true);
            }
            tempItem.setEximErrorExists(Boolean.parseBoolean(value.get("eximErrorExists")));
            
            // dummy code for BPSI-1303 demo ends.
            tempItem.setStoreId(value.get(STORE_ID));
            final String prevPrice = value.get(BBBCoreConstants.PREVPRICE);
            if(!StringUtils.isEmpty(prevPrice)){
                tempItem.setPrevPrice(Double.parseDouble(prevPrice));
            }
            tempItem.setShipMethodUnsupported(Boolean.parseBoolean(value.get("shipMethodUnsupported")));
            if(BBBUtility.isNotEmpty(value.get("prevLtlShipMethod"))) {
            	tempItem.setLtlShipMethod(value.get("prevLtlShipMethod"));
            	value.remove("ltlShipMethod");
            } else {
            tempItem.setLtlShipMethod(value.get("ltlShipMethod"));
            }
            tempItem.setWhiteGloveAssembly(value.get(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY));
            tempItem.setBts(Boolean.parseBoolean( value.get(BTS)));
            final String check = value.get(BBBCoreConstants.OOS);
            if(!StringUtils.isEmpty(check)) {
                tempItem.setMsgShownOOS(Boolean.parseBoolean(check));
            } else {
                tempItem.setMsgShownOOS(true);
            }
            pItemInfo.getValue().remove(BBBCoreConstants.OOS);
            //set the current Time as lastmodifieddate. which is used in mini cart to sort the items
            tempItem.setLastModifiedDate(new Date());
            try{
                skuDetailVO = this.getCatalogTools().getSKUDetails(pSiteId, pItemInfo.getCatalogRefId(), false, true, true);
            }catch (final BBBSystemException e) {
                this.logError("System Exception Occourred while getting SKUVo from catalog", e);
            }catch (final BBBBusinessException e) {
                this.logError("Business Exception Occourred while getting SKUVo from catalog", e);
            }
            //Set flag indicating CommenceItem is of store SKU
            if(skuDetailVO != null){
                tempItem.setStoreSKU(skuDetailVO.isStoreSKU());
            }
        }



        try {
            skuDetailVO = this.getCatalogTools().getSKUDetails(pSiteId, pItemInfo.getCatalogRefId(), false, true, true);
            if (null != skuDetailVO) {
                tempItem.setVdcInd(skuDetailVO.isVdcSku());
                if (!skuDetailVO.isVdcSku() && (value != null)) {
                    tempItem.setBts(Boolean.parseBoolean(value.get(BTS)));
                }
                //TODO getFreeShipMethods will have logic to append all the free shipping methods
                if(skuDetailVO.getFreeShipMethods() != null) {
                    tempItem.setFreeShippingMethod( getFreeShippingMethodInfo(skuDetailVO.getFreeShipMethods()));
                }
                tempItem.setSkuSurcharge(skuDetailVO.getShippingSurcharge());
                tempItem.setIsEcoFeeEligible(skuDetailVO.getIsEcoFeeEligible());
            }
        } catch (final BBBSystemException e) {

            throw new CommerceException("BBBSystem Exception from createCommerceItem in BBBPurchaseProcessHelper", e);
        } catch (final BBBBusinessException e) {

            throw new CommerceException("BBBSystem Exception from createCommerceItem in BBBPurchaseProcessHelper", e);
        }

        return tempItem;
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

    /**
     * @param shipMethVOList
     * @return String
     */
    public static String getFreeShippingMethodInfo(final List<ShipMethodVO> shipMethVOList){

        String tempShippingMethod = null;
        final StringBuilder freeShipMethodsBuilder = new StringBuilder();
        for(final ShipMethodVO shippingMethodVO : shipMethVOList){
            tempShippingMethod = shippingMethodVO.getShipMethodDescription();
            freeShipMethodsBuilder.append(tempShippingMethod).append(",");
        }
        if(freeShipMethodsBuilder.lastIndexOf(",") != -1 ){
            freeShipMethodsBuilder.deleteCharAt(freeShipMethodsBuilder.lastIndexOf(","));
        }
        return freeShipMethodsBuilder.toString();
    }



    /**
     * Do uncached inventory check by rolling up the quantities of matching items in order
     *
     * @param pSkuId
     * @param pStoreId
     * @param pRegistryId
     * @param pSiteId
     * @param pOrder
     * @param pItemQty
     * @param operation
     * @param storeInventoryContainer
     * @param earlierFetchedStatus
     * @return int
     * @throws CommerceException
     * @throws BBBBusinessException
     * @throws BBBSystemException
     */

    public int checkInventory(final String pSiteId, final String pSkuId, final String pStoreId,
            final Order pOrder, final long pItemQty, final String operation,
            final BBBStoreInventoryContainer storeInventoryContainer, final int earlierFetchedStatus)
                    throws CommerceException {

        int inventoryStatus = 0;
        SKUDetailVO skuDetailVO = null;


        if (!StringUtils.isEmpty(pSkuId)) {
            try{
                skuDetailVO = this.getCatalogTools().getSKUDetails (pSiteId, pSkuId, false, true, true);
            }catch (final Exception e) {
                throw new CommerceException(ERR_INV_CHK, e);
            }

            if (skuDetailVO != null) {


                try{
                    //Check for BPOPUS
                    if(StringUtils.isEmpty(pStoreId)){
                        //online item
                            this.logDebug("checkInventory() : Checking Inventory for Online item");
                        if(skuDetailVO.isVdcSku()){
                            //VDC
                            inventoryStatus =  this.getInventoryManager().getVDCProductAvailability(pSiteId, pSkuId, pItemQty, BBBCoreConstants.CACHE_DISABLED);
                        }else {
                            //Non VDC	 - No Inventory Check Required for updateItem
                            if( ! BBBInventoryManager.UPDATE_CART.equals(operation)){
                                inventoryStatus =  this.getInventoryManager().getNonVDCProductAvailability(pSiteId, pSkuId, skuDetailVO.getEcomFulfillment(), operation, BBBCoreConstants.CACHE_DISABLED, pItemQty);
                            }else{
                                return earlierFetchedStatus;
                            }
                        }
                    }else{
                        //BOPUS item
                        if( ! BBBInventoryManager.STORE_ONLINE.equals(operation)){ // Skip BOPUS check for Store to Online
                                this.logDebug("checkInventory() : Checking Inventory for Store Pickup item");

                            final List<String> storeIds = new ArrayList<String>();
                            storeIds.add(pStoreId);

                            Map<String, Integer> storeInventoryMap;

                            storeInventoryMap = this.getInventoryManager()
                                    .getBOPUSProductAvailability(pSiteId, pSkuId, storeIds,
											pItemQty, BBBInventoryManager.ONLINE_STORE, storeInventoryContainer, true, null, false , false);
                            inventoryStatus = storeInventoryMap.get(pStoreId).intValue ();
                        }
                    }
                }catch (final InventoryException e) {
                	if(isLoggingDebug()){
                        this.logDebug(e);
                	}
                    inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
                }
            }
        } else {
            throw new CommerceException(NULL_SKU_ID);
        }

            if(inventoryStatus == BBBInventoryManager.AVAILABLE){
                this.logDebug("checkInventory() : item is available in inventory");
            }else if(inventoryStatus == BBBInventoryManager.NOT_AVAILABLE){
                this.logDebug("checkInventory() : item is not available in inventory");
            }else{
                this.logDebug("checkInventory() : item is limited vailable in inventory");
            }

        return inventoryStatus;

    }

    /**
     * Do cached inventory check by rolling up the quantities of matching items in order
     *
     * @param pSkuId
     * @param pStoreId
     * @param pRegistryId
     * @param pSiteId
     * @param pOrder
     * @param pItemQty
     * @param operation
     * @param storeInventoryContainer
     * @param earlierFetchedStatus
     * @return int
     * @throws CommerceException
     * @throws BBBBusinessException
     * @throws BBBSystemException
     */
    public int checkCachedInventory(final String pSiteId, final String pSkuId, final String pStoreId,
            final Order pOrder, final long pItemQty, final String operation,
            final BBBStoreInventoryContainer storeInventoryContainer, final int earlierFetchedStatus)
                    throws CommerceException {
        int inventoryStatus = 0;
        SKUDetailVO skuDetailVO = null;
        if (!StringUtils.isEmpty(pSkuId)) {
            try{
                skuDetailVO = this.getCatalogTools().getSKUDetails (pSiteId, pSkuId, false, true, true);
            }catch (final Exception e) {
                throw new CommerceException(ERR_INV_CHK, e);
            }
            if (skuDetailVO != null) {
                try{
                    //Check for BPOPUS
                    if(StringUtils.isEmpty(pStoreId)){
                        //online item
                            this.logDebug("checkCachedInventory() : Checking Cached Inventory for Online item");
                        if(skuDetailVO.isVdcSku()){
                            //VDC
                            inventoryStatus =  this.getInventoryManager().getVDCProductAvailability(pSiteId, pSkuId, pItemQty, BBBCoreConstants.CACHE_ENABLED);
                        }else {
                        	inventoryStatus =  this.getInventoryManager().getNonVDCProductAvailability(pSiteId, pSkuId, skuDetailVO.getEcomFulfillment(), operation, BBBCoreConstants.CACHE_ENABLED, pItemQty);
                        }
                    }else{
                        //BOPUS item
                        if( ! BBBInventoryManager.STORE_ONLINE.equals(operation)){ // Skip BOPUS check for Store to Online
                                this.logDebug("checkCachedInventory() : Checking Cached Inventory for Store Pickup item");

                            final List<String> storeIds = new ArrayList<String>();
                            storeIds.add(pStoreId);

                            Map<String, Integer> storeInventoryMap;

                            storeInventoryMap = this.getInventoryManager()
                                    .getBOPUSProductAvailability(pSiteId, pSkuId, storeIds,
											pItemQty, BBBInventoryManager.ONLINE_STORE, storeInventoryContainer, true, null, false , false);
                            inventoryStatus = storeInventoryMap.get(pStoreId).intValue ();
                        }
                    }
                }catch (final InventoryException e) {
                        if(isLoggingDebug()) {
                        this.logDebug(e);
                        }
                    inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
                }
            }
        } else {
            throw new CommerceException(NULL_SKU_ID);
        }
            if(inventoryStatus == BBBInventoryManager.AVAILABLE){
                this.logDebug("checkCachedInventory() : item is available in inventory");
            }else if(inventoryStatus == BBBInventoryManager.NOT_AVAILABLE){
                this.logDebug("checkCachedInventory() : item is not available in inventory");
            }else{
                this.logDebug("checkCachedInventory() : item is limited vailable in inventory");
            }
        return inventoryStatus;
    }


    /**
     * @param pStoreId
     * @param pSkuId
     * @param pOrder
     * @param pItemQty
     * @return long
     */
    public long getRollupQuantities(final String pStoreId, final String pSkuId, final BBBOrderImpl pOrder, final long pItemQty, String pRegistryId){
        long finalQty = 0;

        final List<CommerceItem> existsItemsForSameSKU = pOrder
                .getExistsItemsForSameSKU(pStoreId, pRegistryId, pSkuId);

        if (!BBBUtility.isListEmpty(existsItemsForSameSKU)) {
            // Loop through the items and add all quantity
            for (final CommerceItem commerceItem : existsItemsForSameSKU) {
                finalQty = finalQty + commerceItem.getQuantity();
            }
            // add new Item Qty
            finalQty = finalQty + pItemQty;
        } else {
            finalQty = pItemQty;
        }

            this.logDebug(new StringBuilder().append("Final quantity for inventory check is : ")
                    .append(finalQty).toString());

        return finalQty;
    }



    /**
     * @param pStoreId
     * @param pRegistryId
     * @param pSkuId
     * @param pOrder
     * @param pItemQty
     * @return long
     */
    public long getRollupQtyForUpdate(final String pStoreId, final String pRegistryId, final String pSkuId, final BBBOrderImpl pOrder, final long pItemQty){
        long finalQty = 0;

        final List<CommerceItem> existsItemsForSameSKU = pOrder
                .getExistsItemsForSameSKU(pStoreId, pRegistryId, pSkuId);

        if (!BBBUtility.isListEmpty(existsItemsForSameSKU)) {
            // Loop through the items and add all quantity
            BBBCommerceItem bbbItem = null;
            for (final CommerceItem commerceItem : existsItemsForSameSKU) {
                bbbItem = (BBBCommerceItem)commerceItem;
                if((pRegistryId == null) && (bbbItem.getRegistryId() != null)){ // updated item is registry product
                    finalQty = finalQty + commerceItem.getQuantity();
                }else if( (pRegistryId != null) && (null != bbbItem) && (bbbItem.getRegistryId() == null)){ // updated item is not registry product
                    finalQty = finalQty + commerceItem.getQuantity();
                }
            }
            // add new Item Qty
            finalQty = finalQty + pItemQty;
        } else {
            finalQty = pItemQty;
        }

            this.logDebug(new StringBuilder().append("Final quantity for inventory check is : ")
                    .append(finalQty).toString());

        return finalQty;
    }

    /*private BBBInventoryInfoVO getInventoryInfo(final String pSiteId, final String pSkuId, final String pStoreId){

		Map<InventoryQueryKey, BBBInventoryInfoVO> inventoryResult = null;

		BBBInventoryInfoVO inventoryInfoVO = null;

		InventoryQueryKey inventoryQueryKey = new InventoryQueryKey();
		inventoryQueryKey.setSiteId(pSiteId);
		inventoryQueryKey.setSkuId(pSkuId);

		InventoryQueryKey[] inventoryQueryKeyArr = new InventoryQueryKey[1];


		if(null != pStoreId){
			inventoryQueryKey.setStoreId(pStoreId);
			inventoryQueryKeyArr[0] = inventoryQueryKey;
			try {
				inventoryResult = getStoreInventoryMgr().getInventory(
						inventoryQueryKeyArr);
			} catch (BBBBusinessException e) {

					logError("BBBBussiness Eception from getInventoryInfo of BBBPurchaseProcessHelper class", e);

			} catch (BBBSystemException e) {

					logError("BBBBussiness Eception from getInventoryInfo of BBBPurchaseProcessHelper class", e);

			}
		}else{
			inventoryQueryKeyArr[0] = inventoryQueryKey;
			try {
				inventoryResult = getOnlineInventoryMgr().getInventory(
						inventoryQueryKeyArr);
			} catch (BBBBusinessException e) {

					logError("BBBBussiness Eception from getInventoryInfo of BBBPurchaseProcessHelper class when pStoreId is null", e);

			} catch (BBBSystemException e) {

					logError("BBBBussiness Eception from getInventoryInfo of BBBPurchaseProcessHelper class when pStoreId is null", e);

			}
		}


		if (inventoryResult != null) {
			inventoryInfoVO = inventoryResult
					.get(inventoryQueryKey);
		}

		return inventoryInfoVO;

	}*/

    /**
     * Overrides super class method to get Store Pickup Shipping group in case of item is to be picked from store
     *
     * @param pOrder
     * @param pItemInfo
     * @param pShippingGroup
     * @param pCatalogKey
     * @throws CommerceException
     */
    @Override
    public ShippingGroup getShippingGroupForItem(final Order pOrder,
            final AddCommerceItemInfo pItemInfo, final ShippingGroup pShippingGroup,
            final String pCatalogKey) throws CommerceException {


        final BBBShippingGroupManager bbbSGManager = (BBBShippingGroupManager) this.getShippingGroupManager();
        ShippingGroup mShippingGroup = null;

        if ((pItemInfo.getValue() != null)
                &&  (pItemInfo.getValue().get("storeId") != null && BBBUtility.isNotEmpty((String) pItemInfo.getValue().get("storeId")))) {
            final String storeId = (String) pItemInfo.getValue().get(STORE_ID);

                mShippingGroup = bbbSGManager.getStorePickupShippingGroup(
                        storeId, pOrder);
                if(mShippingGroup == null){
                    throw new CommerceException("Could not create StorePickupShippingGroup");
                }

        } else {
            try {
            	if(pItemInfo.getValue() != null){
                final String registryId = (String) pItemInfo.getValue().get(BBBGiftRegistryConstants.REGISTRY_ID);
                	//changes for LTL 299                     
                    boolean isSkuLtl=getCatalogTools().isSkuLtl(pOrder.getSiteId(), pItemInfo.getCatalogRefId());                    
                                        
                    if(isSkuLtl) {
                      logInfo("Sku is LTL: " + pItemInfo.getCatalogRefId() + "getting shipping groups");
                      mShippingGroup=getLtlShippingGroups(pItemInfo,mShippingGroup,bbbSGManager,pOrder);
	            } else  if( !StringUtils.isEmpty(registryId)){
	                mShippingGroup = bbbSGManager.getRegistryShippingGroup(registryId, pOrder);
                    }else {
                    	//mShippingGroup = bbbSGManager.getFirstNonGiftHardgoodShippingGroup(pOrder);
                    	mShippingGroup = bbbSGManager.getFirstNonLTLHardgoodShippingGroup(pOrder);
	                    if (null == mShippingGroup) {
	                        mShippingGroup = bbbSGManager.createShippingGroup(
	                                bbbSGManager.getOrderTools().getDefaultShippingGroupType());
	                        bbbSGManager.addShippingGroupToOrder(pOrder, mShippingGroup);
	                    }
                    }
	            }
            } catch (final CommerceException comExp) {
                    this.logError("Error during Shipping group creation: ", comExp);
            }
            catch (final BBBBusinessException comExp) {
                this.logError("Error during Shipping group creation: ", comExp);
        }
            catch (final BBBSystemException comExp) {
                this.logError("Error during Shipping group creation: ", comExp);
            }
        }
        this.logDebug(new StringBuilder().append("getShippingGroupForItem End : ").append(mShippingGroup).toString());
        return mShippingGroup;
    }
    
    /**
     * It is a class method to return the same shipping group/create a new shipping group in case of item is ltl or not
     *  @param pItemInfo
     *  @param mShippingGroup
     *  @param bbbSGManager
     *  @param pOrder
     * @throws CommerceException
     */
    @SuppressWarnings("unchecked")
	private ShippingGroup getLtlShippingGroups(AddCommerceItemInfo pItemInfo,ShippingGroup mShippingGroup,
    		BBBShippingGroupManager bbbSGManager,Order pOrder) throws CommerceException {
    	
		this.logDebug("[Start] BBBPurchaseProcessHelper.getLtlShippingGroups");
		
		List<ShippingGroup> shippingGroups = pOrder.getShippingGroups();
		String ltlShippingMethod = BBBCoreConstants.BLANK; 
		if(null == (String)pItemInfo.getValue().get(BBBCatalogConstants.LTL_SHIP_METHOD)) {
        	pItemInfo.getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD, "");
        } else {
			ltlShippingMethod =  (String)pItemInfo.getValue().get(BBBCatalogConstants.LTL_SHIP_METHOD);
		}
		if (!BBBUtility.isListEmpty(shippingGroups)) {
			for (final ShippingGroup shippingGroup : shippingGroups) {
				if (shippingGroup != null
						&& (shippingGroup instanceof HardgoodShippingGroup)						 
						&& shippingGroup.getShippingMethod() != null
						&& ((shippingGroup.getShippingMethod().equalsIgnoreCase((String) pItemInfo.getValue().get(BBBCatalogConstants.LTL_SHIP_METHOD))))) {
							return ((HardgoodShippingGroup) shippingGroup);
				}
			}
		}
		this.logDebug("Create Shipping group if shipping group is null in order and set shipping method as: " + ltlShippingMethod + " and add that shipping address to order");
		mShippingGroup = bbbSGManager.createShippingGroup(bbbSGManager.getOrderTools().getDefaultShippingGroupType());
		mShippingGroup.setShippingMethod((String) pItemInfo.getValue().get(BBBCatalogConstants.LTL_SHIP_METHOD));
		bbbSGManager.addShippingGroupToOrder(pOrder, mShippingGroup);
		
		this.logDebug("[Exit] BBBPurchaseProcessHelper.getLtlShippingGroups");
		return mShippingGroup;
	}

    /**
     * method override to remove the selected commerce items. The overriden method will check if the commerce item had a relation of type BBBStoreShippingGroup
     * The method will delete the relationship for BBBShippingGroup items
     */
    @Override
    public List <String> deleteItems(final Order pOrder, final String[] pRemItemIds, final PricingModelHolder pPMHolder,
            final Locale pLocale, final RepositoryItem pProfile, final PipelineErrorHandler pErrorHandler,
            final Map pExtraMaps) throws CommerceException {
            this.logDebug("Entry Delete items BBBPurchaseProcessHelper");
        final List<String> removalStoreIds = new ArrayList<String>();
        final List<String> removalRegIds = new ArrayList<String>();
        final List<String> deletedSkus = new ArrayList<String>();

        //Get all the commerce items to be removed with a storeId and registryId and set to a arrayList
        if(pRemItemIds.length > 0 ){
            for (final String pRemItemId : pRemItemIds) {
                    this.logDebug("removal commerce id:"+pRemItemId);
                deletedSkus.add(pRemItemId);
                if (pOrder.getCommerceItem(pRemItemId) instanceof BBBCommerceItem) {
                    final BBBCommerceItem commerceItem = (BBBCommerceItem)pOrder.getCommerceItem(pRemItemId);
                    if(commerceItem.getStoreId() != null){
                        removalStoreIds.add(commerceItem.getStoreId());
                            this.logDebug("Adding removal store id with storeId"+commerceItem.getStoreId());
                    }
                    if(commerceItem.getRegistryId() != null){
                        removalRegIds.add(commerceItem.getRegistryId());
                            this.logDebug("Adding removal registry id with reistryId"+commerceItem.getRegistryId());
                    }
                }
            }
        }
        callSuperDeleteItems(pOrder, pRemItemIds, pPMHolder, pLocale, pProfile, pErrorHandler, pExtraMaps);
        synchronized(pOrder)
        {
            final TransactionDemarcation td = new TransactionDemarcation();
            boolean rollback = true;
            try
            {
                td.begin(this.getTransactionManager(), TransactionDemarcation.REQUIRED);

                final BBBShippingGroupManager shpGrpManager = (BBBShippingGroupManager)this.getShippingGroupManager();

                //removing the store pickup shipping group which dont have relationship count more than 0
                for(int i=0; i<removalStoreIds.size();i++ ){
                        this.logDebug("removing shipping group with store id:"+removalStoreIds.get(i));
                    final BBBStoreShippingGroup shpGrp = (BBBStoreShippingGroup)shpGrpManager.getStorePickupShippingGroup(removalStoreIds.get(i), pOrder);
                    if(!(shpGrp.getCommerceItemRelationshipCount() > 0)){
                        shpGrpManager.removeShippingGroupFromOrder(pOrder, shpGrp.getId());
                            this.logDebug("removing shipping group with id:"+shpGrp.getId());
                    }
                    else{
                            this.logDebug("Commerce item relationship is greater than 0: Not removing the shipping group for the commerce item");
                    }
                }

                //removing the hardgood pickup shipping group which dont have relationship count more than 0
                for(int i=0; i<removalRegIds.size();i++){
                        this.logDebug("removing shipping group with registry id:"+removalRegIds.get(i));
                    final BBBHardGoodShippingGroup shpGrp = (BBBHardGoodShippingGroup)shpGrpManager.getRegistryPickupShippingGroup(removalRegIds.get(i), pOrder);
                    if(shpGrp != null){
                        if(!(shpGrp.getCommerceItemRelationshipCount() > 0)){
                            shpGrpManager.removeShippingGroupFromOrder(pOrder, shpGrp.getId());
                                this.logDebug("removing shipping group with id:"+shpGrp.getId());
                        }
                        else{
                                this.logDebug("Commerce item relationship is greater than 0: Not removing the shipping group for the commerce item");
                        }
                    }
                }
                    this.logDebug("Exit Delete items BBBPurchaseProcessHelper");
                this.getOrderManager().updateOrder(pOrder);
                rollback = false;
            }catch(final Exception e)
            {
                throw new CommerceException("error deleting item",e);
            }
            finally
            {
                try
                {
                    td.end(rollback);
                }
                catch(final TransactionDemarcationException tde)
                {
                        this.logError("Transaction roll back error", tde);
                }
            }
        }
        return deletedSkus;
    }

	protected List callSuperDeleteItems(final Order pOrder, final String[] pRemItemIds,
			final PricingModelHolder pPMHolder, final Locale pLocale, final RepositoryItem pProfile,
			final PipelineErrorHandler pErrorHandler, final Map pExtraMaps) throws CommerceException {
		return super.deleteItems(pOrder, pRemItemIds, pPMHolder, pLocale, pProfile, pErrorHandler, pExtraMaps);
	}


    /**
     * This method either adds or removes gift messaging from existing shipping group
     * based on the pGiftWrapFlag flag.
     *
     * @param pOrder
     * @param pShippingGroup
     * @param pSiteId
     * @param mBBBShippingInfoBean
     * @return boolean
     * @throws BBBSystemException
     * @throws BBBBusinessException
     * @throws CommerceException
     */
    public boolean manageAddOrRemoveGiftWrapToShippingGroup(final Order pOrder,
            final ShippingGroup pShippingGroup, final String pSiteId,
            final BBBShippingInfoBean mBBBShippingInfoBean)
                    throws BBBSystemException, BBBBusinessException, CommerceException {

        final boolean pGiftWrapFlag = mBBBShippingInfoBean.getGiftWrap();
        String pGiftWrapMessage = mBBBShippingInfoBean.getGiftMessage();
        final boolean giftingFlag = mBBBShippingInfoBean.isGiftingFlag();

            this.logDebug("giftingFlag[Order includes gift(s)]: " + giftingFlag
                    + "pGiftWrapFlag[Include gift packaging]: " + pGiftWrapFlag
                    + "pGiftWrapMessage: " + pGiftWrapMessage);

        if(pGiftWrapMessage != null){
            pGiftWrapMessage = pGiftWrapMessage.trim();

            if (BBBUtility.isCrossSiteScripting(pGiftWrapMessage))	{
                throw new BBBBusinessException (BBBCoreErrorConstants.CHECKOUT_ERROR_1070,"Invalid Gift Message");
            }

            if(giftingFlag){
                //Added for the fix of BBBSL-1876 :Special characters in Gift message
                if(!BBBUtility.isGiftMessageValid(pGiftWrapMessage)){
                    this.logError("Input string contains prohibited characters");
                    throw new BBBBusinessException ("Input string contains prohibited characters", BBBCoreErrorConstants.ERROR_ADD_REMOVE_GIFT_MESSAGE_1001);
                }
            }
        }

        boolean changedFlag = false;

        if (pShippingGroup instanceof BBBHardGoodShippingGroup) {
            if (giftingFlag) {

                ((BBBHardGoodShippingGroup) pShippingGroup)
                .setGiftMessage(pGiftWrapMessage);

                if (!StringUtils.isEmpty(pGiftWrapMessage)) {
                    ((BBBHardGoodShippingGroup) pShippingGroup)
                    .setGiftWrapInd(true);
                }

                if (pGiftWrapFlag) {
                    if (!((BBBHardGoodShippingGroup) pShippingGroup)
                            .containsGiftWrap()) {
                        this.addGiftWrap(pOrder, pShippingGroup, pSiteId);
                        mBBBShippingInfoBean.setGiftWrap(true);
                        changedFlag = true;
                        ((BBBHardGoodShippingGroup) pShippingGroup)
                        .setGiftWrapInd(true);
                    }
                } else {
                    this.removeGiftWrap(pOrder, pShippingGroup);
                    mBBBShippingInfoBean.setGiftWrap(false);
                    changedFlag = true;
                }
            }

            if (!giftingFlag) {

                ((BBBHardGoodShippingGroup) pShippingGroup)
                .setGiftWrapInd(false);

                ((BBBHardGoodShippingGroup) pShippingGroup)
                .setGiftMessage(BLANK);
                ((BBBHardGoodShippingGroup) pShippingGroup)
                .getSpecialInstructions().remove(BBBCheckoutConstants.GIFT_MESSAGE_KEY);

                if (((BBBHardGoodShippingGroup) pShippingGroup)
                        .containsGiftWrap()) {
                    this.removeGiftWrap(pOrder, pShippingGroup);
                    mBBBShippingInfoBean.setGiftWrap(false);
                    changedFlag = true;
                }
            }
        }
        //getOrderManager().updateOrder(pOrder);

        return changedFlag;
    }

    public void removeAllGiftWrapFromShippingGroup(final Order pOrder,
            final ShippingGroup pShippingGroup, final String pSiteId)
                    throws BBBSystemException, BBBBusinessException, CommerceException {
	    ((BBBHardGoodShippingGroup) pShippingGroup).setGiftWrapInd(false);
	    ((BBBHardGoodShippingGroup) pShippingGroup).setGiftMessage(BLANK);
	    ((BBBHardGoodShippingGroup) pShippingGroup).getSpecialInstructions().remove(BBBCheckoutConstants.GIFT_MESSAGE_KEY);
	    if (((BBBHardGoodShippingGroup) pShippingGroup).containsGiftWrap()) {
	        this.removeGiftWrap(pOrder, pShippingGroup);
	    }
    }
    
    /**
     * This method either adds or removes gift messaging from existing shipping group
     * based on the pGiftWrapFlag flag.
     *
     * @param pOrder
     * @param pShippingGroup
     * @param pSiteId
     * @param cShipmentInfo
     * @return boolean
     * @throws BBBSystemException
     * @throws BBBBusinessException
     * @throws CommerceException
     */
    public boolean manageAddOrRemoveGiftWrapToShippingGroup(final Order pOrder,
            final ShippingGroup pShippingGroup, final String pSiteId,
            final CommerceItemShipInfoVO cShipmentInfo)
                    throws BBBSystemException, BBBBusinessException, CommerceException {

        final boolean pGiftWrapFlag = cShipmentInfo.getGiftWrap();
        String pGiftWrapMessage = cShipmentInfo.getGiftMessage();
        final boolean giftingFlag = cShipmentInfo.getGiftingFlag();

            this.logDebug("giftingFlag[Order includes gift(s)]: " + giftingFlag);
            this.logDebug("pGiftWrapFlag[Include gift packaging]: " + pGiftWrapFlag);
            this.logDebug("pGiftWrapMessage: " + pGiftWrapMessage);

        if(pGiftWrapMessage != null){
            pGiftWrapMessage = pGiftWrapMessage.trim();

            if (BBBUtility.isCrossSiteScripting(pGiftWrapMessage))	{
                throw new BBBBusinessException (BBBCoreErrorConstants.CHECKOUT_ERROR_1070,"Invalid Gift Message");
            }
        }

        boolean changedFlag = false;

        if (pShippingGroup instanceof BBBHardGoodShippingGroup) {
            if (giftingFlag) {

                ((BBBHardGoodShippingGroup) pShippingGroup)
                .setGiftMessage(pGiftWrapMessage);

                if (!StringUtils.isEmpty(pGiftWrapMessage)) {
                    ((BBBHardGoodShippingGroup) pShippingGroup)
                    .setGiftWrapInd(true);
                }

                if (pGiftWrapFlag) {
                    if (!((BBBHardGoodShippingGroup) pShippingGroup)
                            .containsGiftWrap()) {
                        this.addGiftWrap(pOrder, pShippingGroup, pSiteId);
                        cShipmentInfo.setGiftWrap(true);
                        changedFlag = true;
                        ((BBBHardGoodShippingGroup) pShippingGroup)
                        .setGiftWrapInd(true);
                    }
                } else {
                    this.removeGiftWrap(pOrder, pShippingGroup);
                    cShipmentInfo.setGiftWrap(false);
                    changedFlag = true;
                }
            }

            if (!giftingFlag) {

                ((BBBHardGoodShippingGroup) pShippingGroup)
                .setGiftWrapInd(false);

                ((BBBHardGoodShippingGroup) pShippingGroup)
                .setGiftMessage(BLANK);
                ((BBBHardGoodShippingGroup) pShippingGroup)
                .getSpecialInstructions().remove(BBBCheckoutConstants.GIFT_MESSAGE_KEY);

                if (((BBBHardGoodShippingGroup) pShippingGroup)
                        .containsGiftWrap()) {
                    this.removeGiftWrap(pOrder, pShippingGroup);
                    cShipmentInfo.setGiftWrap(false);
                    changedFlag = true;
                }
            }
        }
        //getOrderManager().updateOrder(pOrder);

        return changedFlag;
    }

    /**
     * This method adds add GiftWrap item into the passed Shipping group.
     * @param order
     * @param shippingGroup
     * @param pSiteId
     * @throws BBBSystemException
     * @throws BBBBusinessException
     * @throws CommerceException
     */
    public void addGiftWrap(final Order order, final ShippingGroup shippingGroup, final String pSiteId)
            throws BBBSystemException, BBBBusinessException, CommerceException {

        final GiftWrapVO giftWrapVO = this.getCatalogTools().getWrapSkuDetails(pSiteId);

        if (giftWrapVO != null) {
                this.logDebug("GiftWrap SKU Found: " + giftWrapVO);
            final CommerceItem item = this.getCommerceItemManager()
                    .createCommerceItem(
                            ((BBBOrderTools) this.getOrderManager().getOrderTools())
                            .getGiftWrapCommerceItemType(),
                            giftWrapVO.getWrapSkuId(), giftWrapVO.getWrapProductId(), 1);

            if (item != null) {
                    this.logDebug("GiftWrap item created: " + item);
                this.getCommerceItemManager().addAsSeparateItemToOrder(order, item);
                this.getCommerceItemManager().addItemQuantityToShippingGroup(order,
                        item.getId(), shippingGroup.getId(), 1);
                //getOrderManager().updateOrder(order);

            }
        }

    }


    /**
     * This method adds add Eco Fee item into the passed Shipping group.
     * @param commerceItem
     * @param order
     * @param ecoFeeSkuVO
     * @param shippingGroup
     * @param quantity
     * @param pSiteId
     * @return EcoFeeCommerceItem
     * @throws BBBSystemException
     * @throws BBBBusinessException
     * @throws CommerceException
     * @throws RepositoryException
     */
    public EcoFeeCommerceItem addEcoFeeItem(final CommerceItem commerceItem, final Order order,
            final ShippingGroup shippingGroup, final long quantity, final EcoFeeSKUVO ecoFeeSkuVO)
                    throws CommerceException, RepositoryException {
            this.logDebug("addEcoFeeItem called with : catalogRefId - "+
                    commerceItem.getCatalogRefId()+" order - " + order + " & ShippingGroup - "+
                    shippingGroup);

        EcoFeeCommerceItem ecoFeeItem = null;

        if((ecoFeeSkuVO.getFeeEcoSKUId() != null) && (ecoFeeSkuVO.getEcoFeeProductId() != null)){
                this.logDebug("EcoFee SKU Found: EcoFee ProductID - "+ ecoFeeSkuVO.getEcoFeeProductId()+" & skuID - " + ecoFeeSkuVO.getFeeEcoSKUId());
            ecoFeeItem = (EcoFeeCommerceItem) this.getCommerceItemManager().createCommerceItem(((BBBOrderTools) this.getOrderManager().
                    getOrderTools()).getEcoFeeCommerceItemType(), ecoFeeSkuVO.getFeeEcoSKUId(), ecoFeeSkuVO.getEcoFeeProductId(), quantity);

            if(shippingGroup instanceof BBBStoreShippingGroup) {
                ecoFeeItem.setStoreId(((BBBStoreShippingGroup) shippingGroup).getStoreId());
            }

            if (ecoFeeItem != null) {

                    this.logDebug("EcoFee item created: " + ecoFeeItem);

                this.getCommerceItemManager().addAsSeparateItemToOrder(order, ecoFeeItem);
                this.getCommerceItemManager().addItemQuantityToShippingGroup(order,
                        ecoFeeItem.getId(), shippingGroup.getId(), quantity);

                ((BBBShippingGroup)shippingGroup).getEcoFeeItemMap().put(commerceItem.getId(), ecoFeeItem.getId());
            }
        }
        return ecoFeeItem;
    }


    /**
     * This method removes GiftWrap item into the passed Shipping group.
     * @param order
     * @param shippingGroup
     * @throws BBBSystemException
     * @throws BBBBusinessException
     * @throws CommerceException
     */
    public void removeGiftWrap(final Order order, final ShippingGroup shippingGroup)
            throws BBBSystemException, BBBBusinessException, CommerceException {

        @SuppressWarnings ("unchecked")
        final List<CommerceItemRelationship> commerceItemRelationshipList = shippingGroup.getCommerceItemRelationships();
        String giftWrapItemId = null;

        for (final CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {
            final CommerceItem commerceItem = commerceItemRelationship.getCommerceItem();
            if (commerceItem instanceof GiftWrapCommerceItem) {
                giftWrapItemId = commerceItem.getId();
            }
        }

        if (giftWrapItemId != null) {
                this.logDebug("Gift Wrap item found:" + giftWrapItemId);
            this.getCommerceItemManager().removeItemFromOrder(order, giftWrapItemId);
        }

    }

    /**
     * @return the address property name map.
     */
    public Map getAddressPropertyNameMap() {
        return this.addressPropertyNameMap;
    }

    /**
     * @param pAddressPropertyNameMap - the address property name map to set.
     */
    public void setAddressPropertyNameMap(final Map pAddressPropertyNameMap) {
        this.addressPropertyNameMap = pAddressPropertyNameMap;
    }

    /**
     * Required address property names.
     */
    private String[] mReqAddrPropNames = new String[0];

    /**
     * @return the address property names.
     */
    public String[] getRequiredAddressPropertyNames() {
        return this.mReqAddrPropNames;
    }

    /**
     * @param pRequiredAddressPropertyNames - the address property name map to set.
     */
    public void setRequiredAddressPropertyNames(final String[] pRequiredAddressPropertyNames) {
        if (pRequiredAddressPropertyNames == null) {
            this.mReqAddrPropNames = new String[0];
        }
        else {
            this.mReqAddrPropNames = pRequiredAddressPropertyNames;
        }
    }
    
//    Start : LTL Item Address Property Map 
    
    /**
     * Required address property names.
     */
    private String[] mLTLReqAddrPropNames = new String[0];

    /**
     * @return the address property names.
     */
    public String[] getRequiredLTLAddressPropertyNames() {
        return this.mLTLReqAddrPropNames;
    }

    /**
     * @param pRequiredAddressPropertyNames - the address property name map to set.
     */
    public void setRequiredLTLAddressPropertyNames(final String[] pRequiredLTLAddressPropertyNames) {
        if (pRequiredLTLAddressPropertyNames == null) {
            this.mReqAddrPropNames = new String[0];
        }
        else {
            this.mLTLReqAddrPropNames = pRequiredLTLAddressPropertyNames;
        }
    }
    
//  End : LTL Item Address Property Map
    

    /**
     * Store order tools.
     */
    private BBBOrderTools mStoreOrderTools;

    /**
     * @return the Store order tools property.
     */
    public BBBOrderTools getStoreOrderTools() {
        return this.mStoreOrderTools;
    }

    /**
     * @param pStoreOrderTools - the Store order tools property.
     */
    public void setStoreOrderTools(final BBBOrderTools pStoreOrderTools) {
        this.mStoreOrderTools = pStoreOrderTools;
    }



    /**
     * property: pricingTools
     */
    private PricingTools mPricingTools;

    /**
     * @param pPricingTools - pricing tools.
     */
    public void setPricingTools(final PricingTools pPricingTools) {
        this.mPricingTools = pPricingTools;
    }

    /**
     * @return mPricingTools - pricing tools.
     */
    public PricingTools getPricingTools() {
        return this.mPricingTools;
    }

    /**
     * @return ClaimableManager
     */
    public ClaimableManager getClaimableManager() {
        return this.claimableManager;
    }

    /**
     * @param claimableManager
     */
    public void setClaimableManager(final ClaimableManager claimableManager) {
        this.claimableManager = claimableManager;
    }

    /**
     * Associate a commerce item with the shipping group specified by
     * the <code>pItemInfo.shippingGroup</code> property or the default
     * shipping group supplied by our caller.
     *
     * @param pItem the CommerceItem
     * @param pItemInfo the object that supplies input for the commerce item
     * @param pOrder the item's order
     * @param pShippingGroup the default shipping group for the order
     * @exception CommerceException if there was an error while executing the code
     */
    @Override
    protected void addItemToShippingGroup(final CommerceItem pItem, final AddCommerceItemInfo pItemInfo,
            final Order pOrder, final ShippingGroup pShippingGroup) throws CommerceException {

        callSuperAddItemToShippingGroup(pItem, pItemInfo, pOrder, pShippingGroup);
        try {
        	logDebug("set LTL flag for commerce item");
			if(pItem instanceof BBBCommerceItem && this.getCatalogTools().isSkuLtl(pOrder.getSiteId(), pItem.getCatalogRefId())){
				logDebug("set flag to true as sku is LTL: " + pItem.getCatalogRefId());
				((BBBCommerceItem)pItem).setLtlItem(true);
			}
        } catch (BBBSystemException e) {
			this.logError("System exception while checking is sku LTL for skuID: "+pItem.getCatalogRefId(), e);
		} catch (BBBBusinessException e) {
			this.logError("System exception while checking is sku LTL for skuID: "+pItem.getCatalogRefId(), e);
		}
    }

    /**
     * call the super add item to shipping group 
     * @param pItem the CommerceItem
     * @param pItemInfo the object that supplies input for the commerce item
     * @param pOrder the item's order
     * @param pShippingGroup the default shipping group for the order
     * @exception CommerceException if there was an error while executing the code
      */
	protected void callSuperAddItemToShippingGroup(final CommerceItem pItem, final AddCommerceItemInfo pItemInfo,
			final Order pOrder, final ShippingGroup pShippingGroup) throws CommerceException {
		super.addItemToShippingGroup(pItem, pItemInfo, pOrder, pShippingGroup);
	}


    /**
     * Validates the required billing properties.
     *
     * @param pContactInfo - contact information
     * @param pRequest
     * @return a list of required properties that are missing from the ContactInfo
     */
    public List <List <String>> checkForRequiredAddressProperties(final ContactInfo pContactInfo, final DynamoHttpServletRequest pRequest) {
        // You won't always have a repository item here, so the usual
        // getPropertyValue(<property_name>) won't work unfortunately.
        // Lots of hardcoded properties instead :(
        final List<String> missReqAddrProps = new ArrayList<String>();
        final List<String> invalidAddrProps = new ArrayList<String>();
        final List<List <String>> errorList = new ArrayList<List <String>>();
        final List<String> reqAddrPropNames = Arrays.asList(this.getRequiredAddressPropertyNames());

        try {
            if (reqAddrPropNames.contains("firstName")){
            	if(StringUtils.isEmpty(pContactInfo.getFirstName())){
                    missReqAddrProps.add("firstName");
                }else if(!BBBUtility.isValidFirstName(pContactInfo.getFirstName())){
                    invalidAddrProps.add("firstName");
                }
            }

            if (reqAddrPropNames.contains("lastName")){
                if(StringUtils.isEmpty(pContactInfo.getLastName())){
                    missReqAddrProps.add("lastName");
                }else if(!BBBUtility.isValidLastName(pContactInfo.getLastName())){
                    invalidAddrProps.add("lastName");
                }
            }

            if(!StringUtils.isEmpty(pContactInfo.getCompanyName()) && !BBBUtility.isValidCompanyName(pContactInfo.getCompanyName())){
                invalidAddrProps.add("company");
            }
            if("undefined".equalsIgnoreCase(pContactInfo.getCompanyName())){
            	pContactInfo.setCompanyName(null);
            }
            if (reqAddrPropNames.contains("address1")){
                if(StringUtils.isEmpty(pContactInfo.getAddress1())){
                    missReqAddrProps.add("address1");
                }else if(!BBBUtility.isValidAddressLine1(pContactInfo.getAddress1())){
                    invalidAddrProps.add("address1");
                }
            }

            if(!StringUtils.isEmpty(pContactInfo.getAddress2()) && !BBBUtility.isValidAddressLine2(pContactInfo.getAddress2())){
                invalidAddrProps.add("address2");
            }
            if(!StringUtils.isEmpty(pContactInfo.getAddress3()) && !BBBUtility.isValidAddressLine3(pContactInfo.getAddress3())){
                invalidAddrProps.add("address3");
            }


            if (reqAddrPropNames.contains("city")){
                if(StringUtils.isEmpty(pContactInfo.getCity())){
                    missReqAddrProps.add("city");
                }else if(!BBBUtility.isValidCity(pContactInfo.getCity())){
                    invalidAddrProps.add("city");
                }
            }


            /*List mandatoryStateCountryList = getMandatoryStateCountryList(pRequest);

	      if ((pContactInfo.getCountry() != null) && (mandatoryStateCountryList != null) &&
	          mandatoryStateCountryList.contains(pContactInfo.getCountry())) {
	        if (StringUtils.isEmpty(pContactInfo.getCountry())) {
	          missingRequiredAddressProperties.add("country");
	        }
	      }*/
            if(StringUtils.isEmpty(pContactInfo.getCountry())){
                missReqAddrProps.add("country");
            }

            if (reqAddrPropNames.contains("state")){
                if(StringUtils.isEmpty(pContactInfo.getState())){
                    missReqAddrProps.add("state");
                }
            }
            
            if(!isValidCountryAndState(pContactInfo.getCountry(), pContactInfo.getState())){
            	invalidAddrProps.add("countryAndState");
            }
            
            if (reqAddrPropNames.contains("postalCode")){
                if(StringUtils.isEmpty(pContactInfo.getPostalCode())){
                    missReqAddrProps.add("postalCode");
                }else if(!BBBUtility.isValidZip(pContactInfo.getPostalCode())){
                    invalidAddrProps.add("postalCode");
                }
            }

            if(!StringUtils.isEmpty(pContactInfo.getPhoneNumber()) && !BBBUtility.isValidPhoneNumber(pContactInfo.getPhoneNumber())){
                invalidAddrProps.add("phoneNumber");
            }
        } catch (final Exception e) {

                this.logError(LogMessageFormatter.formatMessage(pRequest, "Error getting message: " + e.toString()));

        }
        errorList.add(0,missReqAddrProps);
        errorList.add(1,invalidAddrProps);
        return errorList;
    }
    
	//    Start : Added for LTL Item Address Validations
    /**
     *
     * @param pLTLShippingAddr - Shipping Address
     * @param pRequest
     * @return a list of required properties that are missing from the Shipping Address for LTL Item
     */
    public List <List <String>> checkForRequiredLTLAddressProperties(final BBBAddress pLTLShippingAddr, final DynamoHttpServletRequest pRequest) {
        final List<String> missReqAddrProps = new ArrayList<String>();
        final List<String> invalidAddrProps = new ArrayList<String>();
        final List<List <String>> errorList = new ArrayList<List <String>>();
        final List<String> reqAddrPropNames = Arrays.asList(this.getRequiredLTLAddressPropertyNames());

        try {
            if (reqAddrPropNames.contains("firstName")){
                if(StringUtils.isEmpty(pLTLShippingAddr.getFirstName())){
                    missReqAddrProps.add("firstName");
                }else if(!BBBUtility.isValidFirstName(pLTLShippingAddr.getFirstName())){
                    invalidAddrProps.add("firstName");
                }
            }

            if (reqAddrPropNames.contains("lastName")){
                if(StringUtils.isEmpty(pLTLShippingAddr.getLastName())){
                    missReqAddrProps.add("lastName");
                }else if(!BBBUtility.isValidLastName(pLTLShippingAddr.getLastName())){
                    invalidAddrProps.add("lastName");
                }
            }

            if(!StringUtils.isEmpty(pLTLShippingAddr.getCompanyName()) && !BBBUtility.isValidCompanyName(pLTLShippingAddr.getCompanyName())){
                invalidAddrProps.add("company");
            }
            if("undefined".equalsIgnoreCase(pLTLShippingAddr.getCompanyName())){
            	pLTLShippingAddr.setCompanyName(null);
            }

            if (reqAddrPropNames.contains("address1")){
                if(StringUtils.isEmpty(pLTLShippingAddr.getAddress1())){
                    missReqAddrProps.add("address1");
                }else if(!BBBUtility.isValidAddressLine1(pLTLShippingAddr.getAddress1())){
                    invalidAddrProps.add("address1");
                }
            }

            if(!StringUtils.isEmpty(pLTLShippingAddr.getAddress2()) && !BBBUtility.isValidAddressLine2(pLTLShippingAddr.getAddress2())){
                invalidAddrProps.add("address2");
            }
            
            if(!StringUtils.isEmpty(pLTLShippingAddr.getAddress3()) && !BBBUtility.isValidAddressLine3(pLTLShippingAddr.getAddress3())){
                invalidAddrProps.add("address3");
            }


            if (reqAddrPropNames.contains("city")){
                if(StringUtils.isEmpty(pLTLShippingAddr.getCity())){
                    missReqAddrProps.add("city");
                }else if(!BBBUtility.isValidCity(pLTLShippingAddr.getCity())){
                    invalidAddrProps.add("city");
                }
            }


            if(StringUtils.isEmpty(pLTLShippingAddr.getCountry())){
                missReqAddrProps.add("country");
            }

            if (reqAddrPropNames.contains("state")){
                if(StringUtils.isEmpty(pLTLShippingAddr.getState())){
                    missReqAddrProps.add("state");
                }
            }

            if (reqAddrPropNames.contains("postalCode")){
                if(StringUtils.isEmpty(pLTLShippingAddr.getPostalCode())){
                    missReqAddrProps.add("postalCode");
                }else if(!BBBUtility.isValidZip(pLTLShippingAddr.getPostalCode())){
                    invalidAddrProps.add("postalCode");
                }
            }
            
            if (reqAddrPropNames.contains("phoneNumber")){
                if(StringUtils.isEmpty(pLTLShippingAddr.getPhoneNumber())){
                    missReqAddrProps.add("phoneNumber");
                }else if(!BBBUtility.isValidPhoneNumber(pLTLShippingAddr.getPhoneNumber())){
                    invalidAddrProps.add("phoneNumber");
                }
            }
            
            if (reqAddrPropNames.contains("email")){
                if(StringUtils.isEmpty(pLTLShippingAddr.getEmail())){
                    missReqAddrProps.add("email");
                }else if(!BBBUtility.isValidEmail(pLTLShippingAddr.getEmail())){
                    invalidAddrProps.add("email");
                }
            }
            if(!StringUtils.isEmpty(pLTLShippingAddr.getAlternatePhoneNumber()) && !BBBUtility.isValidPhoneNumber(pLTLShippingAddr.getAlternatePhoneNumber())){
                invalidAddrProps.add("alternatePhoneNumber");
            }
        } catch (final Exception e) {

                this.logError(LogMessageFormatter.formatMessage(pRequest, "Error getting message: " + e.toString()));

        }
        errorList.add(0,missReqAddrProps);
        errorList.add(1,invalidAddrProps);
        return errorList;
    }

//  Start : Added for LTL Item Address Validations

    /**
     * @param country
     * @param state
     * @return validState
     */
    public boolean isValidCountryAndState(String country, String state) {
		
    	if (BBBUtility.isEmpty(country) || BBBUtility.isEmpty(state)){
    		return false;
    	}
		boolean validState = false;
		String siteId = getCurrentSiteId();
		// if siteId is null , then for US country code , we pass BedBathUS siteId
		// and for CA country code , we pass BedBathCanada siteId
		if (US_COUNTRY_CODE.equalsIgnoreCase(country)) {
			if(BBBUtility.isEmpty(siteId)){
				siteId=BBBCoreConstants.SITE_BAB_US;
			}
		} else if (CA_COUNTRY_CODE.equalsIgnoreCase(country)) {
			if(BBBUtility.isEmpty(siteId)){
				siteId=BBBCoreConstants.SITE_BAB_CA;
			}
		} else {
			return true;
		}
		if(!BBBUtility.isEmpty(siteId)){
			try {
				List<StateVO> listOfStates= getCatalogTools().getStates(siteId, true, null);
				
					if (!BBBUtility.isListEmpty(listOfStates)) {
						for(StateVO stateDetails : listOfStates){
							if(state.equalsIgnoreCase(stateDetails.getStateCode())){
								validState=true;
								break;
							}
						}
					}
				return validState;
			} catch (BBBSystemException e) {
				validState = false;
				if (isLoggingError()) {
					logError("BBBPurchaseProcessHelper.isValidCountryAndState :: BBBSystemException occured ::"
							+ "country=" + country +" and state=" + state + e.getMessage());
				}
			} catch (BBBBusinessException e) {
				validState = false;
				if (isLoggingError()) {
					logError("BBBPurchaseProcessHelper.isValidCountryAndState :: BBBBusinessException occured ::"
							+ "country=" + country +" and state=" + state + e.getMessage());
				}
			}
		}
		return validState;
	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

    /**
     * Logic to reprice order, and parse any errors.
     *
     * @param pOrder the order to price
     * @param pUserLocale the locale of the user, may be null
     * @param pProfile the user, may be null
     * @param pPricingMods the PricingModelHolder is an object which contains all the
     * pricing models associated with a user (i.e. item, shipping, order and tax).
     * @exception PricingException if there was an error while computing the pricing information
     */
    public void repriceOrder(final Order pOrder,
            final PricingModelHolder pPricingMods,
            final Locale pUserLocale,
            final RepositoryItem pProfile)
                    throws PricingException {

        try {
            final HashMap map = new HashMap();

                this.logDebug("Repricing w/ pricing tools (priceOrderTotal. Profile ID: " + pOrder.getProfileId()
                        + "OrderId: " + pOrder.getId());

            // Need to do priceOrderTotal here to catch errors in the shipping address.
            // CyberSource is the only means of doing city/state/zip validation, so use them here and
            // do tax calc to make sure city/state/zip is valid.
            this.getPricingTools()
            .priceOrderTotal(pOrder, pPricingMods, pUserLocale, pProfile, map);
        }
        catch (final PricingException pe) {

            this.logDebug(LogMessageFormatter.formatMessage(null, "Error w/ PricingTools.priceOrderTotal: " + pe));

            throw pe;
        }
        catch (final Exception e) {

                this.logError(LogMessageFormatter.formatMessage(null, ""), e);

        }
    }

    /**
     * This method get the credit card from the order and sets its properties
     * to the BasicBBBCreditCardInfo bean object.
     *
     * @param order
     * @return List<BasicBBBCreditCardInfo>
     */
    public static List<BasicBBBCreditCardInfo> getCreditCardFromOrder (final Order order) {
        final List<BasicBBBCreditCardInfo> ccInfoList =  new ArrayList<BasicBBBCreditCardInfo>();
        final List<PaymentGroup> pGList = getPaymentGroupOfTypeFromOrder (order);

        for (int index = 0 ; index < pGList.size() ; index++) {
            final CreditCard creditCard = (CreditCard)pGList.get(index);
            final BasicBBBCreditCardInfo creditCardInfo = new BasicBBBCreditCardInfo();

            //creditCardInfo.setAmount(pAmount);
            creditCardInfo.setBillingAddress(creditCard.getBillingAddress());
            creditCardInfo.setCardVerificationNumber(creditCard.getCardVerificationNumber());
            creditCardInfo.setCreditCardId(creditCard.getId());
            creditCardInfo.setCreditCardNumber(creditCard.getCreditCardNumber());
            creditCardInfo.setCreditCardType(creditCard.getCreditCardType());
            creditCardInfo.setCurrencyCode(creditCard.getCurrencyCode());
            //creditCardInfo.setDefault(pDefault);

            creditCardInfo.setExpirationDayOfMonth(creditCard.getExpirationDayOfMonth());
            creditCardInfo.setExpirationMonth(creditCard.getExpirationMonth());
            creditCardInfo.setExpirationYear(creditCard.getExpirationYear());
            //added for setting expired flag
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            if((Integer.parseInt(creditCard.getExpirationYear())<calendar.get(java.util.Calendar.YEAR)) || ((Integer.parseInt(creditCard.getExpirationYear())==calendar.get(java.util.Calendar.YEAR)) && (Integer.parseInt(creditCard.getExpirationMonth()) < calendar.get(java.util.Calendar.MONTH)))){
                creditCardInfo.setExpired(BBBCoreConstants.RETURN_TRUE);
            } else {
                creditCardInfo.setExpired(BBBCoreConstants.RETURN_FALSE);
            }
            creditCardInfo.setNameOnCard(((BBBCreditCard)creditCard).getNameOnCard());

            if (org.apache.commons.lang.StringUtils
                    .isBlank(((BBBCreditCard) creditCard).getLastFourDigits())) {
                creditCardInfo.setLastFourDigits(creditCard
                        .getCreditCardNumber().substring(
                                creditCard.getCreditCardNumber().length() - 4,
                                creditCard.getCreditCardNumber().length()));
            } else {
                creditCardInfo.setLastFourDigits(((BBBCreditCard)creditCard)
                        .getLastFourDigits());
            }

            //creditCardInfo.setOrder(pOrder);
            creditCardInfo.setPaymentId(creditCard.getPaymentId());
            //creditCardInfo.setSecurityCode(pSecurityCode);
            //creditCardInfo.setSource(creditCard.getSource);

            ccInfoList.add(creditCardInfo);
        }

        return ccInfoList;
    }

    /**
     * @param order
     * @return List<PaymentGroup>
     */
    private static List<PaymentGroup> getPaymentGroupOfTypeFromOrder(final Order order) {

        @SuppressWarnings ("unchecked")
        final List<PaymentGroup> paymentList = order.getPaymentGroups();
        final List<PaymentGroup> returnPGList = new ArrayList<PaymentGroup>();

        for (final PaymentGroup paymentGroup : paymentList) {
            if (paymentGroup instanceof CreditCard) {
                final CreditCard creditCard = (CreditCard) paymentGroup;
                /*Filter out empty credit cards which was default in nature*/
                if(!StringUtils.isBlank(creditCard.getCreditCardType()) || !StringUtils.isBlank(creditCard.getCreditCardNumber())){
                    returnPGList.add(paymentGroup);
                }
            }
        }
        return returnPGList;
    }


    /**
     * @param order
     * @param userPModels
     * @param userLocale
     * @param profile
     * @return Success Failure
     * @throws CommerceException
     */
    public boolean removeGiftWrapCommerceItemRelationShip(final BBBOrder order, final PricingModelHolder userPModels,
            final Locale userLocale, final RepositoryItem profile) throws CommerceException{

        final List<CommerceItem> giftWrapItemList = new ArrayList<CommerceItem>();

        for (final Object object : order.getShippingGroups()) {
            HardgoodShippingGroup shipGroup = null;

            if (object instanceof HardgoodShippingGroup) {
                shipGroup = (BBBHardGoodShippingGroup) object;
                @SuppressWarnings ("unchecked")
                final List<CommerceItemRelationship> commerceItemRelationshipList = shipGroup.getCommerceItemRelationships();

                for (final CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {

                    final CommerceItem commerceItem = commerceItemRelationship
                            .getCommerceItem();
                    if ((null != commerceItem)
                            && (commerceItem instanceof GiftWrapCommerceItem)) {
                        giftWrapItemList.add(commerceItem);
                        ((BBBHardGoodShippingGroup) shipGroup).setGiftMessage("");

                    }

                }

            }
        }
        if(!giftWrapItemList.isEmpty()){
            synchronized(order)
            {
                final TransactionDemarcation tranDem = new TransactionDemarcation();
                boolean rollback = true;
                try
                {
                    tranDem.begin(this.getTransactionManager(), TransactionDemarcation.REQUIRED);

                    // Remove pyament group, shipping group relationships and remove gift item from the order
                    for (int index = 0, count = giftWrapItemList.size(); index < count; index++) {

                        final CommerceItem commitem = (giftWrapItemList
                                .get(index));
                        this.getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order,
                                commitem.getId());
                        this.getCommerceItemManager().removeItemFromOrder(order,
                                commitem.getId());
                    }
                    this.repriceOrder(order, userPModels, userLocale, profile);
                    this.getOrderManager().updateOrder(order);
                    rollback = false;
                }catch(final Exception e)
                {

                    throw new CommerceException("Error removing giftwrap item", e);
                }
                finally
                {
                    try
                    {
                        tranDem.end(rollback);
                    }
                    catch(final TransactionDemarcationException tde)
                    {
                            this.logError("Transaction roll back error", tde);
                    }
                }
            }
        }
        return true;
    }

    /**
     * This method checks if ECOFEE is eligible for current site or not.
     *
     * @param order
     * @return Success / Failure
     */
    public boolean isEcoFeeForCurrentSite(final BBBOrderImpl order) {

    	this.logDebug("[START] isEcoFeeForCurrentsite");

        boolean flag = false;

        final String siteSpecificConfigKey = new StringBuffer().append(ECOFEE_KEY)
                .append(UNDERSCORE).append(order.getSiteId()).toString();

            this.logDebug("siteSpecificConfigKey: " + siteSpecificConfigKey);

        if(order.getSiteId() == null){
                this.logDebug("[END] isEcoFeeForCurrentsite, order.siteID is null returning false");
            return false;
        }

        try {
            final List<String> value = this.getCatalogTools()
                    .getContentCatalogConfigration(siteSpecificConfigKey);

                this.logDebug("value: " + value);

            if ((value != null) && (value.size() > 0)) {
                if (TRUE.equalsIgnoreCase(value.get(0))) {
                    flag = true;
                }
            }
        } catch (final BBBSystemException e) {
                this.logError(e);
        } catch (final BBBBusinessException e) {
                this.logError(e);
        }

            this.logDebug("[END] isEcoFeeForCurrentsite, " + flag);

        return flag;
    }

    /**
     * @return Checkout Manager
     */
    public final BBBCheckoutManager getManager() {
        return this.checkoutManager;
    }
    /**
     * @param checkoutManager
     */
    public final void setManager(final BBBCheckoutManager checkoutManager) {
        this.checkoutManager = checkoutManager;
    }

    @Override
    public void logDebug(final String pMessage) {
        if (this.isLoggingDebug()) {
            this.logDebug(pMessage, null);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString () {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBPurchaseProcessHelper [inventoryMgr=").append(this.inventoryManager)
            .append(", checkoutManager=").append(this.checkoutManager)
            .append(", catalogTools=").append(this.catalogTools)
            .append(", giftRegistryManager=").append(this.giftRegistryManager)
            .append(", claimableManager=").append(this.claimableManager)
            .append(", mAddrPropMap=").append(this.addressPropertyNameMap)
            .append(", mReqAddrPropNames=").append(Arrays.toString(this.mReqAddrPropNames))
            .append(", mStoreOrderTools=").append(this.mStoreOrderTools)
            .append(", mPricingTools=").append(this.mPricingTools).append("]");
        return builder.toString();
    }

}
