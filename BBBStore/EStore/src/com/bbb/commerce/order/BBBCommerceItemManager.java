package com.bbb.commerce.order;

//Java Imports
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.UnitPriceBean;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.LTLAssemblyFeeVO;
import com.bbb.commerce.catalog.vo.LTLDeliveryChargeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.common.vo.PromotionVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.utils.BBBUtility;

/**
 * This class extends the default CommerceItemManager for custom functionality.
 *  
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class BBBCommerceItemManager extends CommerceItemManager {
	
	/* ===================================================== *
	 	MEMBER VARIABLES
	 * ===================================================== */
	private BBBCatalogTools mCatalogUtil;	
	/**
	 * BBBSessionBean 
	 */
	private  String mCommerceItemMoved;
	
	/**
	 * BBBPurchaseProcessHelper property.
	 */	
	private BBBPurchaseProcessHelper mPurchaseProcessHelper;
	private BBBPricingTools mPricingTools;
	private BBBEximManager eximPricingManager;
	
	/* ===================================================== *
	 	GETTERS and SETTERS
	 * ===================================================== */
	
	public BBBEximManager getEximPricingManager() {
		return eximPricingManager;
	}
	public void setEximPricingManager(BBBEximManager eximPricingManager) {
		this.eximPricingManager = eximPricingManager;
	}
	/**
	 * @return the mPricingTools
	 */
	public BBBPricingTools getPricingTools() {
		return this.mPricingTools;
	}
	/**
	 * @param mPricingTools the mPricingTools to set
	 */
	public void setPricingTools(BBBPricingTools mPricingTools) {
		this.mPricingTools = mPricingTools;
	}
	/**
	 * @return the commerceItemMoved
	 */
	public String getCommerceItemMoved() {
		return mCommerceItemMoved;
	}
	/**
	 * @param pCommerceItemMoved the commerceItemMoved to set
	 */
	public void setCommerceItemMoved(String pCommerceItemMoved) {
		mCommerceItemMoved = pCommerceItemMoved;
	}
	/**
	 * @return the sessionBean
	 */
	
	public BBBCatalogTools getCatalogUtil() {
		return mCatalogUtil;
	}
	public void setCatalogUtil(final BBBCatalogTools catalogUtil) {
		this.mCatalogUtil = catalogUtil;
	}
	
	public BBBPurchaseProcessHelper getPurchaseProcessHelper() {
		return mPurchaseProcessHelper;
	}

	public void setPurchaseProcessHelper(
			BBBPurchaseProcessHelper purchaseProcessHelper) {
		this.mPurchaseProcessHelper = purchaseProcessHelper;
	}
	private MutableRepository orderRepository;
	
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
	
	
	/* ===================================================== *
	 	STANDARD METHODS
	 * ===================================================== */
	
	/**
	 * override the shouldMergeItems method to check if merging is required or not
	 * If existing item and new item has got same store id or registry id combination then retun true
	 * 
	 * @param pExistingItem
	 * @param pNewItem
	 * 
	 */
	@Override
	protected boolean shouldMergeItems(final CommerceItem pExistingItem,
			final CommerceItem pNewItem) {
		boolean doMergeItems = super.shouldMergeItems(pExistingItem, pNewItem);
		
		
		if(pExistingItem instanceof BBBCommerceItem && pNewItem instanceof BBBCommerceItem){
		if(doMergeItems){
				try {
					// Changes for LTL 299
				boolean isSkuLtl = getCatalogUtil().isSkuLtl(pNewItem.getAuxiliaryData().getSiteId(),pNewItem.getCatalogRefId());

					if (isSkuLtl) {
					doMergeItems = getLtlshouldMergeItems(pExistingItem,pNewItem, doMergeItems);
					if(doMergeItems) {
						doMergeItems = compareRegistryId((BBBCommerceItem) pExistingItem,(BBBCommerceItem) pNewItem);
					}
				} else {
			if(compareStoreId((BBBCommerceItem)pExistingItem, (BBBCommerceItem)pNewItem)){
				doMergeItems = true;
					} else {
				return false;
			}
			if(compareRegistryId((BBBCommerceItem)pExistingItem, (BBBCommerceItem)pNewItem)){
				doMergeItems = true;
					} else {
						doMergeItems = false;
					}
					// Added doMergeItems to ensure registry items don't merge with non registry items BPSI-1854
					if(doMergeItems && compareReferenceNumber((BBBCommerceItem)pExistingItem, (BBBCommerceItem)pNewItem)){
						doMergeItems = true;
			}else{
				doMergeItems = false;			
			}
		}
				} catch (BBBSystemException e) {
					this.logError("[BBBCommerceItemManager : shouldMergeItems()] error while fetching sku vo from catalog tools", e);
				} catch (BBBBusinessException e) {
					this.logError("[BBBCommerceItemManager : shouldMergeItems()] error while fetching sku vo from catalog tools", e);
				}
			}
		
		if(isLoggingDebug()){
			logDebug( new StringBuilder().append("shouldMergeItems() returned ").append(doMergeItems).toString());
		}
		
		if(doMergeItems){
			((BBBCommerceItem)pExistingItem).setLastModifiedDate(new Date());
			if(isLoggingDebug()){
				logDebug( new StringBuilder().append("updated the last modified date of item ").append(pExistingItem.getId()).toString());
			}
		}
	}
		return doMergeItems;
	}
	
	/**
	 * override the shouldMergeItems method to check if merging is required or not
	 * If new item is ltl or not
	 * 
	 * @param pExistingItem
	 * @param pNewItem
	 * @param doMergeItems
	 * 
	 */
	
	@SuppressWarnings("rawtypes")
	private boolean getLtlshouldMergeItems(final CommerceItem pExistingItem,final CommerceItem pNewItem,boolean doMergeItems)
 {
		if (this.isLoggingDebug()) {
			this.logDebug("[START] BBBCommerceItemManager.getLtlshouldMergeItems");
		}
		List existingShipGroupRelationships = pExistingItem.getShippingGroupRelationships();
		
		if (existingShipGroupRelationships != null	&& !existingShipGroupRelationships.isEmpty()) {
			for (final Iterator existingShippingGroupRelIterator = existingShipGroupRelationships.iterator(); existingShippingGroupRelIterator.hasNext();) 
			{

				final ShippingGroupRelationship existshipGroupRelationship = (ShippingGroupRelationship) existingShippingGroupRelIterator.next();
				if (existshipGroupRelationship != null) {
					ShippingGroup shippingGroup = existshipGroupRelationship.getShippingGroup();
					String assemblyCommerceId = null;
					if (shippingGroup != null
							&& shippingGroup instanceof BBBHardGoodShippingGroup) {
						assemblyCommerceId = ((BBBCommerceItem)pExistingItem).getAssemblyItemId();

						if (BBBUtility.isNotEmpty(assemblyCommerceId)) {
							((BBBCommerceItem) pExistingItem).setWhiteGloveAssembly(BBBCatalogConstants.TRUE);
						}

						String existShipGroupMethod = shippingGroup.getShippingMethod();
						if(null == ((BBBCommerceItem) pNewItem).getLtlShipMethod()) {
							((BBBCommerceItem) pNewItem).setLtlShipMethod("");
				        }
						
						String existLtLShipMethod = ((BBBCommerceItem) pExistingItem).getLtlShipMethod();
						String newLtLShipMethod = ((BBBCommerceItem) pNewItem).getLtlShipMethod();
						
						if (existShipGroupMethod != null
								&& !existShipGroupMethod.equalsIgnoreCase(((BBBCommerceItem) pNewItem).getLtlShipMethod())){
							doMergeItems = false;
						}
						// logic for not merging items when assembly fee for existing and new item not matches
						else if (existShipGroupMethod != null
								&& existShipGroupMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
								&& ((BBBCommerceItem) pNewItem).getLtlShipMethod()!=null
								&& ((BBBCommerceItem) pNewItem).getLtlShipMethod().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)) {
							if (BBBUtility.isNotEmpty(((BBBCommerceItem) pExistingItem).getWhiteGloveAssembly())
									&& BBBUtility.isNotEmpty(((BBBCommerceItem) pNewItem).getWhiteGloveAssembly())
									&& (!((BBBCommerceItem) pExistingItem).getWhiteGloveAssembly().equalsIgnoreCase(((BBBCommerceItem) pNewItem).getWhiteGloveAssembly()))) {
								doMergeItems = false;
							}

						}
						// [Start] BPSI-1072
						// Check ltlShipMethod for those items for which shipping method of shipping group is empty
						if(BBBUtility.isEmpty(existShipGroupMethod) && BBBUtility.isNotEmpty(existLtLShipMethod) && existLtLShipMethod.equalsIgnoreCase(newLtLShipMethod)){
							doMergeItems = true;
					}
						// logic for not merging items when assembly fee for existing and new item not matches
						if (BBBUtility.isEmpty(existShipGroupMethod)
								&& BBBUtility.isNotEmpty(existLtLShipMethod)
								&& existLtLShipMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
								&& ((BBBCommerceItem) pNewItem).getLtlShipMethod()!=null
								&& ((BBBCommerceItem) pNewItem).getLtlShipMethod().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)) {
							if (BBBUtility.isNotEmpty(((BBBCommerceItem) pExistingItem).getWhiteGloveAssembly())
									&& BBBUtility.isNotEmpty(((BBBCommerceItem) pNewItem).getWhiteGloveAssembly())
									&& (!((BBBCommerceItem) pExistingItem).getWhiteGloveAssembly().equalsIgnoreCase(((BBBCommerceItem) pNewItem).getWhiteGloveAssembly()))) {
								doMergeItems = false;
				}
			}
						// [End] BPSI-1072

		}
			}
		}
		}
		if (this.isLoggingDebug()) {
			this.logDebug("[Exit] BBBCommerceItemManager.getLtlshouldMergeItems");
		}

		return doMergeItems;

	}
	
	/**
	 * compare storeId of existingItem and newItem
	 * 
	 * 
	 * @param pExistingItem
	 * @param pNewItem
	 * @return
	 */
	private boolean compareStoreId(final BBBCommerceItem pExistingItem,
			final BBBCommerceItem pNewItem){
		
		if(!BBBUtility.isEmpty(pExistingItem.getStoreId())){
			return  pExistingItem.getStoreId().equals(pNewItem.getStoreId());
		}else if(BBBUtility.isEmpty(pNewItem.getStoreId())){
			return true;
		}
		
		return false;
	}
	
	/**
	 * compare registryId of existingItem and newItem
	 * 
	 * 
	 * @param pExistingItem
	 * @param pNewItem
	 * @return
	 */
	private boolean compareRegistryId(final BBBCommerceItem pExistingItem,
			final BBBCommerceItem pNewItem){
		if ((BBBUtility.isEmpty(pExistingItem.getRegistryId()) && BBBUtility.isEmpty(pNewItem.getRegistryId())) ||
				(BBBUtility.isNotEmpty(pExistingItem.getRegistryId()) && BBBUtility.isNotEmpty(pNewItem.getRegistryId()) && 
						pExistingItem.getRegistryId().equals(pNewItem.getRegistryId()))) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * compare ReferenceNumber of existingItem and newItem
	 * 
	 * 
	 * @param pExistingItem
	 * @param pNewItem
	 * @return
	 */
	private boolean compareReferenceNumber(final BBBCommerceItem pExistingItem,
			final BBBCommerceItem pNewItem){
	
		if(!BBBUtility.isEmpty(pExistingItem.getReferenceNumber())){
			return pExistingItem.getReferenceNumber().equals(pNewItem.getReferenceNumber());			
		}else if(!BBBUtility.isEmpty(pNewItem.getReferenceNumber())){
			return false;
		}else{
			return true;
		}
		
	}
	
	
	/**
	 * Add storeID and registryID if pItem has storeID and registryID
	 * @param pSrcOrder
	 * @param pDstOrder
	 * @param pItem
	 * @return CommerceItem
	 * 
	 */
	@Override
	protected CommerceItem mergeOrdersCopyCommerceItem(Order pSrcOrder,
			Order pDstOrder, CommerceItem pItem) throws CommerceException {	
		SKUDetailVO skuDetailVO = null;
		BBBCommerceItem bbbDestItem = null;
		mCommerceItemMoved = null;
		if(pItem != null && pItem instanceof BBBCommerceItem){
			bbbDestItem = (BBBCommerceItem)super.mergeOrdersCopyCommerceItem(pSrcOrder, pDstOrder, pItem);
			if(bbbDestItem != null){
				BBBCommerceItem bbbSrcItem = (BBBCommerceItem)pItem;
				if (bbbSrcItem.isItemMoved()){
					bbbDestItem.setCommerceItemMoved(bbbSrcItem.getAuxiliaryData().getProductId() + "," + bbbSrcItem.getStoreId());
				}
				bbbDestItem.setStoreId(bbbSrcItem.getStoreId());
				bbbDestItem.setRegistryId(bbbSrcItem.getRegistryId());
				bbbDestItem.setItemMoved(bbbSrcItem.isItemMoved());
				bbbDestItem.setRegistryInfo( bbbSrcItem.getRegistryInfo());	
				bbbDestItem.setBts( bbbSrcItem.getBts());
				bbbDestItem.setVdcInd(bbbSrcItem.isVdcInd());
				bbbDestItem.setFreeShippingMethod(bbbSrcItem.getFreeShippingMethod());
				bbbDestItem.setSkuSurcharge(bbbSrcItem.getSkuSurcharge());
				bbbDestItem.setLastModifiedDate(bbbSrcItem.getLastModifiedDate());
				bbbDestItem.setReferenceNumber(bbbSrcItem.getReferenceNumber());
				bbbDestItem.setPorchServiceRef(bbbSrcItem.getPorchServiceRef());
				if(BBBUtility.isNotEmpty(bbbSrcItem.getReferenceNumber())){
					bbbDestItem.setPersonalizationDetails(bbbSrcItem.getPersonalizationDetails());
					bbbDestItem.setPersonalizationOptions(bbbSrcItem.getPersonalizationOptions());
					//BPSI-5386 Personalization option single code display			
					bbbDestItem.setPersonalizationOptionsDisplay(getEximPricingManager().getPersonalizedOptionsDisplayCode(bbbDestItem.getPersonalizationOptions()));
					bbbDestItem.setPersonalizePrice(bbbSrcItem.getPersonalizePrice());
					bbbDestItem.setPersonalizeCost(bbbSrcItem.getPersonalizeCost());
					bbbDestItem.setMetaDataFlag(bbbSrcItem.getMetaDataFlag());
					bbbDestItem.setMetaDataUrl(bbbSrcItem.getMetaDataUrl());
					bbbDestItem.setModerationFlag(bbbSrcItem.getModerationFlag());
					bbbDestItem.setModerationUrl(bbbSrcItem.getModerationUrl());
					bbbDestItem.setThumbnailImagePath(bbbSrcItem.getThumbnailImagePath());
					bbbDestItem.setMobileThumbnailImagePath(bbbSrcItem.getMobileThumbnailImagePath());
					bbbDestItem.setMobileFullImagePath(bbbSrcItem.getMobileFullImagePath());
					bbbDestItem.setFullImagePath(bbbSrcItem.getFullImagePath());
					bbbDestItem.setEximPricingReq(bbbSrcItem.isEximPricingReq());
					bbbDestItem.setEximErrorExists(bbbSrcItem.isEximErrorExists());
				}
				
				try {
					skuDetailVO = getCatalogUtil().getSKUDetails(pDstOrder.getSiteId(), bbbDestItem.getCatalogRefId(), false, true, true);					
				} catch (BBBSystemException e) {
					
					throw new CommerceException("BBBSystem Exception from mergeOrdersCopyCommerceItem in BBBCommerceItemManager", e);
				} catch (BBBBusinessException e) {
					
					throw new CommerceException("BBBSystem Exception from mergeOrdersCopyCommerceItem in BBBCommerceItemManager", e);
				}
				
				if(skuDetailVO != null){
					bbbDestItem.setIsEcoFeeEligible(skuDetailVO.getIsEcoFeeEligible());
					if(skuDetailVO.isLtlItem()) {
						// [Start] BPSI-1072
						((BBBCommerceItem) bbbDestItem).setShipMethodUnsupported(((BBBCommerceItem) pItem).isShipMethodUnsupported());
						((BBBCommerceItem) bbbDestItem)
								.setLtlShipMethod(((BBBCommerceItem) pItem).getLtlShipMethod());
						// [End] BPSI-1072
						((BBBCommerceItem) bbbDestItem).setWhiteGloveAssembly(((BBBCommerceItem) pItem).getWhiteGloveAssembly());
						((BBBCommerceItem) bbbDestItem).setLtlItem(true);
				}
				}
				
		}
		}
		return bbbDestItem;		
	}
	
	/**
	 * The method is used when the commerce item is switched from store to online or online to store
	 * The method will take the quantity for the new commerce item and create a new commerce with that quantity and shipping group to which switched
	 * The method will also update the quantity of the existing commerce item
	 * @param pOrder
	 * @param pQuantity
	 * @param pCommerceItem
	 * @param pStoreId
	 * @param pShippingId
	 * @throws CommerceException
	 */
	public void splitCommerceItemByQuantity(Order pOrder,long pQuantity,BBBCommerceItem pCommerceItem, String pStoreId, String pShippingId, boolean changeStoreAfterSplitFlag, BBBStoreInventoryContainer storeInventoryContainer)throws CommerceException{
		if(isLoggingDebug()){
			logDebug("Entry BBBCommerceItemManager: splitCommerceItemByQuantity");
			logDebug("Item details:"+pCommerceItem.getId()+" Quantity:"+pQuantity+" Order:"+pOrder.getId()+" Store ID:"+pStoreId);
		}
		
		SKUDetailVO skuDetailVO = null;
		if(pOrder != null && pCommerceItem != null){
		try {
			skuDetailVO = getCatalogUtil().getSKUDetails(pOrder.getSiteId(), pCommerceItem.getCatalogRefId(), false, true, true);					
		} catch (BBBSystemException e) {
			
			throw new CommerceException("BBBSystem Exception from splitCommerceItemByQuantity in BBBCommerceItemManager", e);
		} catch (BBBBusinessException e) {
			
			throw new CommerceException("BBBBusiness Exception from splitCommerceItemByQuantity in BBBCommerceItemManager", e);
		}
		
		
			BBBShippingGroupManager shpGrpMgr = (BBBShippingGroupManager)getShippingGroupManager();
			BBBCommerceItem ciItem;
			BBBCommerceItem ciFinal;
			if(pStoreId != null && !pStoreId.equals("")){
				if(isLoggingDebug()){
					logDebug("Creating new commerce item with store id");
				}				
				if(shpGrpMgr != null){
					//Creating new commerce item of store pickup shipping  with the new quantity
					BBBStoreShippingGroup bbbShpGrp = (BBBStoreShippingGroup)shpGrpMgr.getStorePickupShippingGroup(pStoreId, pOrder);
					ciItem = (BBBCommerceItem)createCommerceItem(pCommerceItem.getCommerceItemClassType(), pCommerceItem.getCatalogRefId(), null, pCommerceItem.getAuxiliaryData().getProductId(), null, pQuantity, pCommerceItem.getCatalogKey(), null, null);
					ciItem.setStoreId(pStoreId);
					ciItem.setRegistryId(pCommerceItem.getRegistryId());
					ciItem.setRegistryInfo(pCommerceItem.getRegistryInfo());
					ciItem.setBts(pCommerceItem.getBts());
					ciItem.setFreeShippingMethod(pCommerceItem.getFreeShippingMethod());
					ciItem.setRegistryInfo(pCommerceItem.getRegistryInfo());
					ciItem.setLastModifiedDate(new Date());
					if(skuDetailVO != null){
						ciItem.setIsEcoFeeEligible(skuDetailVO.getIsEcoFeeEligible());
					}
					ciFinal = (BBBCommerceItem)addItemToOrder(pOrder, ciItem);
					if(isLoggingDebug()){
						logDebug("created new commerce item with id: "+ ciFinal.getId());
						logDebug("created new shipping group with id:" + bbbShpGrp.getId());
					}	
					addItemQuantityToShippingGroup(pOrder, ciFinal.getId(), bbbShpGrp.getId(), pQuantity);
					
					//modify the earlier commerce item and reduce the quantity
					pCommerceItem.setQuantity(pCommerceItem.getQuantity() - pQuantity);
					if(!changeStoreAfterSplitFlag){
						pCommerceItem.setStoreId(null);
					}
					removeItemQuantityFromShippingGroup(pOrder, pCommerceItem.getId(), pShippingId, pQuantity);	
				}				
			}else{
				if(isLoggingDebug()){
					logDebug("Creating new default commerce item with hardgoodshipping group");
				}
				if(shpGrpMgr != null){
					//Creating new commerce item of hardgood shipping with new quantity 
					HardgoodShippingGroup bbbHrdShpGrp = (HardgoodShippingGroup)shpGrpMgr.getHardGoodShippingGroup(pOrder);
					ciItem = (BBBCommerceItem)createCommerceItem(pCommerceItem.getCommerceItemClassType(), pCommerceItem.getCatalogRefId(), null, pCommerceItem.getAuxiliaryData().getProductId(), null, pQuantity, pCommerceItem.getCatalogKey(), null, null);
					ciItem.setRegistryId(pCommerceItem.getRegistryId());
					ciItem.setRegistryInfo(pCommerceItem.getRegistryInfo());
					ciItem.setBts(pCommerceItem.getBts());
					ciItem.setFreeShippingMethod(pCommerceItem.getFreeShippingMethod());
					ciItem.setRegistryInfo(pCommerceItem.getRegistryInfo());
					ciItem.setLastModifiedDate(new Date());
					//shipping surcharge
					ciItem.setSkuSurcharge(pCommerceItem.getSkuSurcharge());
					if(skuDetailVO != null){
						ciItem.setIsEcoFeeEligible(skuDetailVO.getIsEcoFeeEligible());
					}
					ciFinal = (BBBCommerceItem)addItemToOrder(pOrder, ciItem);
					if(isLoggingDebug()){
						logDebug("created new commerce item with id: "+ ciFinal.getId());
						logDebug("created new shipping group with id:" + bbbHrdShpGrp.getId());
					}
					addItemQuantityToShippingGroup(pOrder, ciFinal.getId(), bbbHrdShpGrp.getId(), pQuantity);
					
					//modify the earlier commerce item and reduce quantity
					pCommerceItem.setQuantity(pCommerceItem.getQuantity() - pQuantity);					
					removeItemQuantityFromShippingGroup(pOrder, pCommerceItem.getId(), pShippingId, pQuantity);	
					
					setInventoryStatus(pOrder, storeInventoryContainer, ciFinal, BBBInventoryManager.STORE_ONLINE);
					
				}				
			}
		}			
		if(isLoggingDebug()){
			logDebug("Exit BBBCommerceItemManager: splitCommerceItemByQuantity");
		}
	}
	/**
	 * @param pOrder
	 * @param storeInventoryContainer
	 * @param ciFinal
	 */
	@SuppressWarnings("unchecked")
	public void setInventoryStatus(Order pOrder, BBBStoreInventoryContainer storeInventoryContainer, BBBCommerceItem ciFinal, String operation) {
		long rolledUpQty = 0;
		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		
		try{
			rolledUpQty = getPurchaseProcessHelper().getRollupQtyForUpdate(ciFinal.getStoreId(), ciFinal.getRegistryId(),
					ciFinal.getCatalogRefId(), (BBBOrderImpl) pOrder, ciFinal.getQuantity());
			availabilityStatus = getPurchaseProcessHelper().checkCachedInventory(pOrder.getSiteId(), ciFinal.getCatalogRefId(),
					ciFinal.getStoreId(), pOrder, rolledUpQty, operation, storeInventoryContainer, BBBInventoryManager.AVAILABLE);
		}catch (CommerceException e) {
			if (isLoggingError()) {
				logError(e.getMessage(), e);
			}
			availabilityStatus = BBBInventoryManager.AVAILABLE;
		}
		
		if(null != ((BBBOrderImpl)pOrder).getAvailabilityMap()){
			((BBBOrderImpl)pOrder).getAvailabilityMap().put(ciFinal.getId(),
						Integer.valueOf(availabilityStatus));
		}
	}
	
	/**
	 * The method is used when the commerce item is switched from store to online or online to store
	 * The method will just modify the commerce items shipping group with the same quantity
	 * @param pOrder
	 * @param pCommerceItem
	 * @param pStoreId
	 * @throws CommerceException
	 */
	public void modifyCommerceItemShippingGroup(Order pOrder,BBBCommerceItem pCommerceItem, String pStoreId)throws CommerceException{
		if(isLoggingDebug()){
			logDebug("Entry BBBCommerceItemManager: modifyCommerceItemShippingGroup");
			logDebug("Item details:"+pCommerceItem.getId()+" Quantity:"+pCommerceItem.getQuantity()+" Order:"+pOrder.getId()+" Store ID:"+pStoreId);
		}
		if(pOrder != null && pCommerceItem != null){
			BBBShippingGroupManager shpGrpMgr = (BBBShippingGroupManager)getShippingGroupManager();
			if(pStoreId != null && !pStoreId.equals("")){				
				if(isLoggingDebug()){
					logDebug("Store Id is not null. Hence associating with a BBBStorePickupShippingGroup");
				}				
				if(shpGrpMgr != null){
					//modify the commerce item and associate it to StorePickup Shipping Group
					BBBStoreShippingGroup bbbShpGrp = (BBBStoreShippingGroup)shpGrpMgr.getStorePickupShippingGroup(pStoreId, pOrder);
					pCommerceItem.setStoreId(pStoreId);
					removeAllRelationshipsFromCommerceItem(pOrder, pCommerceItem.getId());
					addItemQuantityToShippingGroup(pOrder, pCommerceItem.getId(), bbbShpGrp.getId(), pCommerceItem.getQuantity());
				}				
			}else{
				if(isLoggingDebug()){
					logDebug("StoreId is null. Hence associating with a default HardGoodShippingGroup");
				}
				//Write code for handling when no store id and asscoiate for default HArdGoodShiipingGroup
				if(shpGrpMgr != null){
					HardgoodShippingGroup bbbHrdShpGrp = (HardgoodShippingGroup)shpGrpMgr.getHardGoodShippingGroup(pOrder);
					pCommerceItem.setStoreId(null);
					removeAllRelationshipsFromCommerceItem(pOrder, pCommerceItem.getId());
					addItemQuantityToShippingGroup(pOrder, pCommerceItem.getId(), bbbHrdShpGrp.getId(), pCommerceItem.getQuantity());
				}				
			}
		}
		if(isLoggingDebug()){
			logDebug("Exit BBBCommerceItemManager: modifyCommerceItemShippingGroup");			
		}		
	}
	
	
	/**
	 * LTL Changes
	 * This method takes the BBBCommerceItem, Order as input
	 * and populate priceInfo object with addition of delivery surcharge and assembly fee.
	 * 
	 * @param BBBCommerceItem
	 * @return PriceInfoVO
	 * @throws BBBBusinessException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public PriceInfoVO getItemPriceInfo(final BBBCommerceItem item,Order order) {
		
		PriceInfoVO priceInfoVO = this.getItemPriceInfo(item);
		if (item != null && item.isLtlItem()) {
			List<BBBShippingGroupCommerceItemRelationship> sgcirs = item.getShippingGroupRelationships();
			for(BBBShippingGroupCommerceItemRelationship shippingGroupCommItem : sgcirs ){
				ShippingGroup shippingGroup = shippingGroupCommItem.getShippingGroup();
				if(shippingGroup instanceof BBBHardGoodShippingGroup){
					BBBHardGoodShippingGroup bbbShippingGroup = (BBBHardGoodShippingGroup) shippingGroup;
					//Retrieve relationship of LTL commerce item with its assembly and delivery surcharge commerce items. 
					getLTLItemPriceInfo(item.getId(),priceInfoVO,order);
				}
			}
		}
		return priceInfoVO;
	}
	
	
	public boolean isShippingMehthodAvlForCommerceItem(final BBBCommerceItem item){
		if (item != null) {
			boolean isShippingMethodAvl = true;
			SKUDetailVO skuVO = null;
			try {
				skuVO = getCatalogUtil().getMinimalSku(item.getCatalogRefId());
				if (skuVO != null) {
					List<BBBShippingGroupCommerceItemRelationship> sgcirs = item.getShippingGroupRelationships();
					for(BBBShippingGroupCommerceItemRelationship shippingGroupCommItem : sgcirs ){
						ShippingGroup shippingGroup = shippingGroupCommItem.getShippingGroup();
						//flag to identify if shipping method is not available.
						if(shippingGroup!= null && BBBUtility.isEmpty(shippingGroup.getShippingMethod()) ){
							isShippingMethodAvl =false;
							break;
						}
					}
					
				}else{
					isShippingMethodAvl = false;
				}
				
			} catch (BBBSystemException e) {
				logError("BBBSystem Exception from getSKUDetails in BBBCommerceItemManager " ,e);
			} catch (BBBBusinessException e) {
				logError("BBBBusiness Exception from getSKUDetails in BBBCommerceItemManager" ,e);
			}
			return isShippingMethodAvl;
			
		}
		return false;
		
	}
	public String getShippingMethodDesc(final BBBCommerceItem item){
		String shippingMethodDescription = null;
		if (item != null) {
			RepositoryItem shippingMethod;
			try {
			       List<BBBShippingGroupCommerceItemRelationship> sgcirs = item.getShippingGroupRelationships();
					for(BBBShippingGroupCommerceItemRelationship shippingGroupCommItem : sgcirs ){
						ShippingGroup shippingGroup = shippingGroupCommItem.getShippingGroup();
						//flag to identify if shipping method is not available.
						if(!BBBUtility.isEmpty(shippingGroup.getShippingMethod()) && !shippingGroup.getShippingMethod().equalsIgnoreCase("storeShippingGroup")){
							shippingMethod = getCatalogUtil().getShippingMethod(shippingGroup.getShippingMethod());
							shippingMethodDescription = (String) shippingMethod.getPropertyValue("shipMethodDescription");
	
						}
					}
					
			} catch (BBBSystemException e) {
				logError("BBBSystem Exception from getSKUDetails in BBBCommerceItemManager " ,e);
			} catch (BBBBusinessException e) {
				logError("BBBBusiness Exception from getSKUDetails in BBBCommerceItemManager" ,e);
			}
			return shippingMethodDescription;
			
		}
		return shippingMethodDescription;
		
	}
	
	/**
	 * Retrieve relationship of LTL commerce item with its assembly and delivery surcharge commerce items.
	 * @param commerceId
	 * @param priceInfoVO
	 * @param order
	 * @param bbbShippingGroup
	 * @return
	 */
	public PriceInfoVO getLTLItemPriceInfo(String commerceId, PriceInfoVO priceInfoVO, Order order) {
		
		BBBCommerceItem commItem = (BBBCommerceItem)getCommerceItemFromOrder(order,commerceId);
		String deliveryCommerceId;
		String assemblyCommerceId = "";
		if(null != commItem) {
		deliveryCommerceId = commItem.getDeliveryItemId();
		assemblyCommerceId = commItem.getAssemblyItemId();
		if (BBBUtility.isNotEmpty(deliveryCommerceId)) {
			CommerceItem delCommItem = getCommerceItemFromOrder(order,deliveryCommerceId);
				double rawTotalPrice = delCommItem.getPriceInfo().getRawTotalPrice();
				double finalAmount = delCommItem.getPriceInfo().getAmount();
				double surchargeSaving = rawTotalPrice - finalAmount;
				priceInfoVO.setDeliverySurcharge(rawTotalPrice);
				priceInfoVO.setDeliverySurchargeSaving(surchargeSaving);
				priceInfoVO.setDeliverySurchargeProrated(finalAmount);
			}
		}
		if (BBBUtility.isNotEmpty(assemblyCommerceId)) {
			CommerceItem assemblyCommItem =  getCommerceItemFromOrder(order,assemblyCommerceId);
			if(null != commItem) {
				priceInfoVO.setAssemblyFee(assemblyCommItem.getPriceInfo().getAmount());
			}
		}
		return priceInfoVO;
	}

	
	public CommerceItem getCommerceItemFromOrder(Order order,String commerceId) {
		CommerceItem commItem = null;
		try {
			commItem = order.getCommerceItem(commerceId);
		} catch (CommerceItemNotFoundException e) {
			logError("Exception occured while fetching Commerce Item from order" ,e);
		} catch (InvalidParameterException e) {
			logError("Invalid parameter is passed while fecthing Commerce Item from order" ,e);
		}
		return commItem;
	}
	
	/**
	 * This method takes the BBBCommerceItem and get its priceInfo object and
	 * sets into the PriceInfoVO.
	 * 
	 * @param BBBCommerceItem
	 * @return PriceInfoVO
	 */
	@SuppressWarnings("unchecked")
    public PriceInfoVO getItemPriceInfo(final BBBCommerceItem item) {
		
		
		PriceInfoVO priceInfoVO = new PriceInfoVO();
		long undiscountedItemsCount = 0;
		
		List<PromotionVO> itemPromotionVOList = priceInfoVO.getItemPromotionVOList();
		
		if (item != null) {
			
			double savedAmount = 0.0;
			double savedPercentage = 0.0;
			double savedUnitAmount = 0.0;
			double savedUnitPercentage = 0.0;
			
			priceInfoVO.setItemCount((int) item.getQuantity());
			ItemPriceInfo priceInfo = item.getPriceInfo();
			if(priceInfo == null) { 
			    return priceInfoVO;
			}
            priceInfoVO.setRawAmount(priceInfo.getRawTotalPrice());
			priceInfoVO.setTotalAmount(priceInfo.getAmount());
			priceInfoVO.setUnitListPrice(priceInfo.getListPrice());
			
			if (priceInfo.getSalePrice() > 0) {
				
				savedAmount = BigDecimal.valueOf(priceInfo.getListPrice()).multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue() - priceInfo.getAmount();				
				priceInfoVO.setTotalSavedAmount(savedAmount);
				priceInfoVO.setUnitSalePrice(priceInfo.getSalePrice());
				savedUnitAmount = priceInfo.getListPrice() - priceInfo.getSalePrice();
				priceInfoVO.setUnitSavedAmount(savedUnitAmount);
			}
			if (savedAmount > 0) {
				
				savedPercentage  = getPricingTools().round(BigDecimal.valueOf(savedAmount).multiply(BigDecimal.valueOf(100)).doubleValue() / BigDecimal.valueOf(priceInfo.getListPrice()).multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue(), 2);
				priceInfoVO.setTotalSavedPercentage(savedPercentage);
				savedUnitPercentage = getPricingTools().round(BigDecimal.valueOf(savedUnitAmount).multiply(BigDecimal.valueOf(100)).doubleValue() / priceInfo.getListPrice(), 2);
				priceInfoVO.setUnitSavedPercentage(savedUnitPercentage);
			}

			priceInfoVO.setTotalDiscountShare(priceInfo.getOrderDiscountShare());			
		
			
			List<UnitPriceBean> priceBeans= getPricingTools().generatePriceBeans(priceInfo.getCurrentPriceDetails());
			for(UnitPriceBean unitPriceBean : priceBeans) {
				if (unitPriceBean.getPricingModels() == null || unitPriceBean.getPricingModels().isEmpty()) {
					undiscountedItemsCount += unitPriceBean.getQuantity();
				}
			}
			priceInfoVO.setUndiscountedItemsCount(undiscountedItemsCount);
			priceInfoVO.setPriceBeans(priceBeans);		
			priceInfoVO.setItemPromotionVOList(itemPromotionVOList);
			priceInfoVO.setUndiscountedItemsCount(undiscountedItemsCount);
			/* BBBH-5500|My Offer Automation |Cart Page Changes starts */
			List<DetailedItemPriceInfo> dpiList = priceInfo.getCurrentPriceDetails();
			for (DetailedItemPriceInfo dpi : dpiList) {
				List<PricingAdjustment> adjustmentList = dpi.getAdjustments();
				for (PricingAdjustment adjustment : adjustmentList) {
					if (null == adjustment) {
						vlogDebug("BBBCommerceItemManager.getItemPriceInfo: Adjustment is null for item priceinfo {0}",
								dpi.getItemPriceInfo());
						continue;
					}
					if (null != adjustment.getPricingModel()) {
						String promoCouponKey = adjustment.getPricingModel().getRepositoryId();
						if (null != adjustment.getCoupon()) {
							vlogDebug("BBBCommerceItemManager.getItemPriceInfo: Coupon {0} present for promotion {1}",
									adjustment.getCoupon(), adjustment.getPricingModel());
							promoCouponKey += adjustment.getCoupon().getRepositoryId();
						}
						String promotionDisplayName = (String) adjustment.getPricingModel().getPropertyValue(
								BBBCoreConstants.DISPLAY_NAME);
						if (!priceInfoVO.getPromotionDetails().containsKey(promoCouponKey)) {
							priceInfoVO.getPromotionDetails().put(promoCouponKey, promotionDisplayName);
							vlogDebug(
									"BBBCommerceItemManager.getItemPriceInfo: Added new promotion {0} to promotion list for item {1} and promoCouponKey {2}",
									adjustment.getPricingModel(), item, promoCouponKey);
						}
					}
				}
			}
			vlogDebug("BBBCommerceItemManager.getItemPriceInfo: Applied promotions for item {0} are {1}", item,
					priceInfoVO.getPromotionDetails());
			/* BBBH-5500|My Offer Automation |Cart Page Changes Ends */
		}
		return priceInfoVO;
	}
	
	/** @param order Order
     *  @param shippingGroup ShippingGroup
     *  @param pSiteId String
     *  @param currentCommItemId String
     * @return itemId
     * @throws BBBSystemException Exception
     * @throws CommerceException Exception*/
	public String addLTLDeliveryChargeSku(final Order order, final ShippingGroup shippingGroup, final String pSiteId, CommerceItem currentCommItem)
    		throws BBBBusinessException, BBBSystemException, CommerceException, RepositoryException {
		if(isLoggingDebug()){
			this.logDebug("[Start] BBBCommerceItemManager.addLTLDeliveryChargeSku");
		}
		final String currentCommItemId = currentCommItem.getId();
		final long commerceQuantity = currentCommItem.getQuantity();
		final LTLDeliveryChargeVO ltlDeliverChargeVO = this.getCatalogUtil().getLTLDeliveryChargeSkuDetails(pSiteId);
		if (ltlDeliverChargeVO != null) {
			if(isLoggingDebug()){
				this.logDebug("Delivery Charge SKU Found: " + ltlDeliverChargeVO);
			}
			if (shippingGroup != null
	                && shippingGroup instanceof BBBHardGoodShippingGroup) {
				String deliveryCommerItemId = ((BBBCommerceItem)currentCommItem).getDeliveryItemId();
				logInfo("Delivery Surcharge commerce Id: " + deliveryCommerItemId);
				if (deliveryCommerItemId != null) {
					LTLDeliveryChargeCommerceItem deliveryCommItem = (LTLDeliveryChargeCommerceItem) order.getCommerceItem(deliveryCommerItemId);
					this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(order,deliveryCommItem,commerceQuantity);
					deliveryCommItem.setQuantity(commerceQuantity);
					return deliveryCommItem.getId();
				} else {
					logInfo("Delivery commerce Id is empty, so creating new Delivery Surcharge item");
					final CommerceItem item = this
	                              .createCommerceItem(
	                                            ((BBBOrderTools) this.getOrderTools()).getLtlDeliveryChargeCommerceItemType(),ltlDeliverChargeVO.getLtlDeliveryChargeSkuId(),ltlDeliverChargeVO.getLtlDeliveryChargeProductId(),commerceQuantity);
					if (item != null) {
						if(isLoggingDebug()){
							this.logDebug("Delivery Surcharge item created: " + item);
							this.logDebug("Adding item to order and add item quantity to shipping group and relationship with normal commerce item");
						}
			            this.addAsSeparateItemToOrder(order, item);
			            this.addItemQuantityToShippingGroup(order,item.getId(), shippingGroup.getId(),commerceQuantity);
			            ((LTLDeliveryChargeCommerceItem)item).setLtlCommerceItemRelation(currentCommItemId);
			            return item.getId();
					}
				}
	       }
		}
		if(isLoggingDebug()){
			this.logDebug("[Exit] BBBCommerceItemManager.addLTLDeliveryChargeSku");
		}
		return null;
	}
    
    /** @param order Order
     *  @param shippingGroup ShippingGroup
     *  @param pSiteId String
     *  @param currentCommItemId String
     * @return itemId
     * @throws BBBSystemException Exception
     * @throws CommerceException Exception*/
      public String addLTLAssemblyFeeSku(final Order order, final ShippingGroup shippingGroup, final String pSiteId,final CommerceItem currentCommItem) 
      		throws BBBBusinessException, BBBSystemException, CommerceException, RepositoryException {

    	  if(isLoggingDebug()){
    		  this.logDebug("[Entry] BBBOrderManager.addLTLAssemblyChargeSku");
    	  }
          final String currentCommItemId = currentCommItem.getId();
          final long commerceQuantity = currentCommItem.getQuantity();
         final LTLAssemblyFeeVO ltlAssemblyVO = this.getCatalogUtil().getLTLAssemblyFeeSkuDetails(pSiteId);

         if (ltlAssemblyVO != null) {
        	 if(isLoggingDebug()){
                this.logDebug("Assembly Fee SKU Found: " + ltlAssemblyVO);
        	 }

                if (shippingGroup != null
                             && shippingGroup instanceof BBBHardGoodShippingGroup) {
                      String assemblyCommerItemId = ((BBBCommerceItem)currentCommItem).getAssemblyItemId() ;
                      logInfo("Assembly commerce Id: " + assemblyCommerItemId);
                      if (BBBUtility.isNotEmpty(assemblyCommerItemId)) {
                             LTLAssemblyFeeCommerceItem assemblyCommItem = (LTLAssemblyFeeCommerceItem) order.getCommerceItem(assemblyCommerItemId);
                             this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(order,assemblyCommItem,commerceQuantity);
                             assemblyCommItem.setQuantity(commerceQuantity);
                             return assemblyCommItem.getId();
                      } else {
                    	     logInfo("Assembly commerce Id is empty, so creating new Assembly item");
                             final CommerceItem item = this.createCommerceItem(
                                           ((BBBOrderTools) this.getOrderTools()).getLtlAssemblyFeeCommerceItemType(),ltlAssemblyVO.getLtlAssemblySkuId(),ltlAssemblyVO.getLtlAssemblyProductId(),commerceQuantity);
                             String assemblyCommerceItemId = "";
                              if (item != null) {
                            	  if(isLoggingDebug()){
                                    this.logDebug("AssemblyFee Item created: " + item);
                                    this.logDebug("Adding item to order and add item quantity to shipping group and relationship with normal commerce item");
                            	  }
                            	  assemblyCommerceItemId = item.getId();
                                  this.addAsSeparateItemToOrder(order, item);
                                  this.addItemQuantityToShippingGroup(order, assemblyCommerceItemId, shippingGroup.getId(), commerceQuantity);
                                  ((LTLAssemblyFeeCommerceItem)item).setLtlCommerceItemRelation(currentCommItemId);
                              }
                             return assemblyCommerceItemId;
                      }
                }
         }
         if(isLoggingDebug()){
        	 this.logDebug("[Exit] BBBOrderManager.addLTLAssemblyChargeSku");
         }
         return null;
}
    
    /** @param deliveryCommId String
     *  @param assemblyCommId String
     *  @param shippingGroup ShippingGroup
     *  @param currentCommItemId String
     * @throws RepositoryException Exception*//*
    @SuppressWarnings("unchecked")
    public void getltlItemsAssoc(String deliveryCommId,String assemblyCommId,ShippingGroup shippingGroup,String currentCommItemId) 
  		  throws RepositoryException {
  	  
  		if (this.isLoggingDebug()) {
  			this.logDebug("[Start] BBBCartFormHandler.getltlItemsAssoc");
  		}
  		MutableRepositoryItem ltlItemsAssoc;
  		MutableRepository orderRepository = getOrderRepository();
  		if (orderRepository != null) {
  			if(shippingGroup instanceof BBBHardGoodShippingGroup){
  				RepositoryItem existingLtlItemsAssoc = (RepositoryItem) ((BBBHardGoodShippingGroup) shippingGroup).getLTLItemMap().get(currentCommItemId);
  				if (existingLtlItemsAssoc == null) {
  					ltlItemsAssoc = (MutableRepositoryItem) orderRepository
  		  					.createItem("ltlItemAssoc");
  		  			ltlItemsAssoc.setPropertyValue("deliveryItemId", deliveryCommId);
  		  			ltlItemsAssoc.setPropertyValue("assemblyItemId", assemblyCommId);
  		  			orderRepository.addItem(ltlItemsAssoc);
  					((BBBHardGoodShippingGroup) shippingGroup).getLTLItemMap().put(currentCommItemId, ltlItemsAssoc);
  				}
  			}
  		}
  		if (this.isLoggingDebug()) {
  			this.logDebug("[Exit] BBBCartFormHandler.getltlItemsAssoc");
  		}
  	}*/
    
    /**
	 * @param commerceitem
	 * @param shippinggroup
	 * @param order
     * @throws InvalidParameterException 
     * @throws CommerceException 
	 */
	public void removeDeliveryAssemblyCIFromOrderByCISg(String ci, Order order) throws CommerceException {
		
		BBBCommerceItem commerceItem = (BBBCommerceItem)order.getCommerceItem(ci);
		String deliveryItemId = commerceItem.getDeliveryItemId();
		String assemblyItemId = commerceItem.getAssemblyItemId();
		if(BBBUtility.isNotEmpty(deliveryItemId)){
			removeDeliveryAssemblyCIFromOrder(deliveryItemId,order);
			commerceItem.setDeliveryItemId(null);
		}
		if(BBBUtility.isNotEmpty(assemblyItemId)){
			removeDeliveryAssemblyCIFromOrder(assemblyItemId,order);
			commerceItem.setAssemblyItemId(null);
		}
	}
	
	
	/**
	 * @param catalogRefId
	 * @param order
	 * @return
	 */
	public CommerceItem hasPorchCommerceItem(String catalogRefId, Order order){
		
		boolean isAlreadyAdded=false;
		CommerceItem existingCommerceItem=null;		 
        final List<CommerceItem> comItemObj = order.getCommerceItems();
        for(CommerceItem comerceItem: comItemObj){
        	if(comerceItem.getCatalogRefId().equalsIgnoreCase(catalogRefId)){
        	isAlreadyAdded = ((BaseCommerceItemImpl) comerceItem).isPorchService();
        	if(isAlreadyAdded){
        		existingCommerceItem=comerceItem;
        	}
        	}
        	
        	
        }
        
        return existingCommerceItem;
	}
	
	
	/**
	 * @param commerceitem
	 * @param order
	 */
	public void removeDeliveryAssemblyCIFromOrder(String ci,Order order)
	{
		try {
			this.removeItemFromOrder(order, ci);
		} catch (CommerceException e) {
			logError("Cannot delete commerce Item " + ci);
		}
	}
	
	public void removeDeliveryAssemblyCI(String[] cis,Order order) throws CommerceException
	{
        for(String removeCI : cis)
        {
        	removeDeliveryAssemblyCIFromOrderByCISg(removeCI, order);
        }
	}
}