/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order/BBBOrderManager.java.BBBOrderManager $$
 * @updated $DateTime: Nov 9, 2011 1:43:21 PM
 */
package com.bbb.commerce.order;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import atg.commerce.CommerceException;
import atg.commerce.gifts.InvalidGiftQuantityException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.SimpleOrderManager;
import atg.core.util.Address;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.core.util.ResourceUtils;
import atg.service.pipeline.RunProcessException;
import atg.service.util.CurrentDate;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.utils.BBBUtility;


/**
 * This class extends the default OrderManager for custom functionality.
 *  
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class BBBOrderManager extends SimpleOrderManager {
	
	private static final String BOT = "BOT";

	private static final String USER_AGENT = "User-Agent";

	/** Class name for performance logging*/
	private static final String CLASS_NAME = BBBOrderManager.class.getName();
	
	/* ===================================================== *
 	MEMBER VARIABLES
	 * ===================================================== */
	private boolean mPersistOrderXML = false;
	/**
	 * BBBCatalogTools property.
	 */	
	private BBBCatalogTools mCatalogUtil;
	/**
	 * BBBPurchaseProcessHelper property.
	 */	
	private BBBPurchaseProcessHelper mPurchaseProcessHelper;
	/**
	 * GiftRegistryManager property.
	 */	
	private GiftRegistryManager mRegistryManager;

	private String mBotOrderID;
	
	private CurrentDate mCurrentDate;
	
	/* ===================================================== *
	 	GETTERS and SETTERS
	 * ===================================================== */
	
	public CurrentDate getCurrentDate() {
		return mCurrentDate;
	}

	public void setCurrentDate(final CurrentDate currentDate) {
		this.mCurrentDate = currentDate;
	}
	
	/**
	 * @return the persistOrderXML
	 */
	public final boolean isPersistOrderXML() {
		return this.mPersistOrderXML;
	}

	/**
	 * @param pPersistOrderXML the persistOrderXML to set
	 */
	public final void setPersistOrderXML(final boolean pPersistOrderXML) {
		this.mPersistOrderXML = pPersistOrderXML;
	}
	
	public BBBCatalogTools getCatalogUtil() {
		return this.mCatalogUtil;
	}	

	public void setCatalogUtil(final BBBCatalogTools catalogUtil) {
		this.mCatalogUtil = catalogUtil;
	}
	public BBBPurchaseProcessHelper getPurchaseProcessHelper() {
		return this.mPurchaseProcessHelper;
	}

	public void setPurchaseProcessHelper(
			final BBBPurchaseProcessHelper purchaseProcessHelper) {
		this.mPurchaseProcessHelper = purchaseProcessHelper;
	}
	
	public GiftRegistryManager getRegistryManager() {
		return this.mRegistryManager;
	}

	public void setRegistryManager(final GiftRegistryManager registryManager) {
		this.mRegistryManager = registryManager;
	}

	
	private BBBEximManager eximManager;
	
	/**
	 * @return the eximPricingManager
	 */
	public BBBEximManager getEximManager() {
		return eximManager;
	}

	/**
	 * @param eximPricingManager the eximPricingManager to set
	 */
	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}
	
	/* ===================================================== *
	 	STANDARD METHODS
	 * ===================================================== */
	/**
	 * Override the method for restricting type cast from BBBStoreShippingGroup to HardgoodShippingGroup
	 * 
	 * @param pSrcShippingGroup
	 * @param pDestShippingGroup
	 * @return boolean
	 */
	public boolean compareHardgoodShippingGroups(
			ShippingGroup pSrcShippingGroup, final ShippingGroup pDestShippingGroup) {
		if(pSrcShippingGroup instanceof BBBStoreShippingGroup || pDestShippingGroup instanceof BBBStoreShippingGroup){
			return false;
		}
		boolean compareShippingGroups =  super.compareHardgoodShippingGroups(pSrcShippingGroup, pDestShippingGroup);
		Address srcAddress = ((HardgoodShippingGroup)pSrcShippingGroup).getShippingAddress();
		
		if (((BBBOrderTools)getOrderTools()).isNullAddress(srcAddress) && pSrcShippingGroup.getShippingMethod() != null && pSrcShippingGroup.getShippingMethod().equalsIgnoreCase(pDestShippingGroup.getShippingMethod())) {
			logDebug("Source Shipping Group has empty address and Shipping methods of source and destination shipping groups are same, using destination shipping group :: " + pDestShippingGroup);
			compareShippingGroups = true;
		}
		
		if(pSrcShippingGroup instanceof BBBHardGoodShippingGroup && !pSrcShippingGroup.getShippingMethod().equalsIgnoreCase(BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR))
		{
			logDebug("Merging cart for LTL items only if shipping methods are same");
			if(pSrcShippingGroup.getCommerceItemRelationshipCount() == 0)
				return compareShippingGroups;
			
			List <BBBShippingGroupCommerceItemRelationship> sgciList = pSrcShippingGroup.getCommerceItemRelationships();
			for(BBBShippingGroupCommerceItemRelationship sgci : sgciList){
				if(sgci.getCommerceItem() instanceof BBBCommerceItem && ((BBBCommerceItem)sgci.getCommerceItem()).isLtlItem()){
					if(pSrcShippingGroup.getShippingMethod().equalsIgnoreCase(pDestShippingGroup.getShippingMethod())) {
						return true;
					}
					else {
						return false;
					}
				}
			}
		}
		return compareShippingGroups;
	}
	
	/**
	 * Override the method for restricting adding storeShippingGroup to order which already have CommerceItemRelationship
	 * 
	 * @param pOrder
	 * @param pShippingGroup	 * 
	 */
	public void addShippingGroupToOrder(Order pOrder,
			ShippingGroup pShippingGroup) throws CommerceException {
		if (pShippingGroup.getCommerceItemRelationshipCount() > 0){
			return;
		}
		getShippingGroupManager().addShippingGroupToOrder(pOrder,
				pShippingGroup);
	}
	
	/**
	 * Override the method to remove the item whose SKU is disabled from pDestOrder before calling mergeOrder
	 * 
	 * @param pSrcOrder
	 * @param pDestOrder	  
	 */
	@SuppressWarnings("unchecked")
	public void mergeOrders(Order pSrcOrder, Order pDestOrder)
			throws CommerceException {		
		if(isLoggingDebug()){
			logDebug("mergeOrders() : Starts");
		}
		// LTL start removing delivery surcharge and assembly ci from src order 
		List<ShippingGroup> srcSglist = pSrcOrder.getShippingGroups();
		boolean isLTLInSrcOrder = removeALLDeliveryAssemblyCIFromOrderBySG(pSrcOrder, BBBCoreConstants.SOURCE_ORDER);
		boolean isLTLInDestOrder = removeALLDeliveryAssemblyCIFromOrderBySG(pDestOrder, BBBCoreConstants.DESTINATION_ORDER);
		// LTL End 
		
		//Story Id - BPSI-4433 Start - Exim Changes to call multi-ref API
		if("true".equalsIgnoreCase(getEximManager().getKatoriAvailability())) {
			List<CommerceItem> commerceItems = pDestOrder.getCommerceItems();
			if(!BBBUtility.isListEmpty(commerceItems)){
				boolean isWebServiceFailure = getEximManager().setEximDetailsbyMultiRefNumAPI(commerceItems, (BBBOrder)pDestOrder);
				if(!isWebServiceFailure){
					getOrderManager().updateOrder(pDestOrder);
				}else{
					//In case of webservice failure set the flag of personalized commerce items in Source order as true.
					String refNum;
					List<CommerceItem> srcCommerceItems = pSrcOrder.getCommerceItems();
					for (final CommerceItem commItem : srcCommerceItems) {
						if(commItem instanceof BBBCommerceItem){
							refNum = ((BBBCommerceItem)commItem).getReferenceNumber();
							if(BBBUtility.isNotEmpty(refNum)){
								logDebug("BBBEximMamanger.getEximDetailsbyMultiRefNum Error exists for ref Num : " + ((BBBCommerceItem)commItem).getReferenceNumber());
								((BBBCommerceItem)commItem).setEximErrorExists(true);
								((BBBCommerceItem)commItem).setEximPricingReq(false);
							}
						}
					}
				}
			}
		}
		//Story Id - BPSI-4433 End - Exim Changes to call multi-ref API
		
		removeGiftWrapCommerceItems(pSrcOrder);
		super.mergeOrders(pSrcOrder, pDestOrder);
		
		if(isLoggingDebug()){
			logDebug("mergeOrders() : ends");
		}
		
		if(isLTLInSrcOrder || isLTLInDestOrder){
		// LTL start creating delivery surcharge and assembly ci from src order after merge 
		try {
				createDeliveryAssemblyCI(pDestOrder);
		} catch (BBBBusinessException e) {
				throw new CommerceException("Bussiness Exception from mergeOrders method of BBBOrderManager", e);
		} catch (BBBSystemException e) {
				throw new CommerceException("System Exception from mergeOrders method of BBBOrderManager", e);
		} catch (RepositoryException e) {
				
				throw new CommerceException("Repository Exception from mergeOrders method of BBBOrderManager", e);
		}
	}
	
		
	}
	
	
	
	
	
	
	/**
	 * This method creates the Delivery/Assembly Items from all LTL commerce items and order
	 * 
	 * @param pSrcOrder
	 * @param pDestOrder	  
	 * @throws RepositoryException 
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	@SuppressWarnings("unchecked")
	public void createDeliveryAssemblyCI(Order pDestOrder)
			throws CommerceException, BBBBusinessException, BBBSystemException, RepositoryException {
		if(isLoggingDebug()){
			logDebug("BBBOrderManager:createDeliveryAssemblyCI starts");
		}	
		List<ShippingGroup> destSglist = pDestOrder.getShippingGroups();
		BBBHardGoodShippingGroup destSg = null;
		BBBCommerceItem destci = null;
		boolean updateOrderRequired =false;
		for(ShippingGroup tempdestSg: destSglist) {
			if(tempdestSg instanceof BBBHardGoodShippingGroup){
				destSg = (BBBHardGoodShippingGroup)tempdestSg;
				//Do not create Delivery/Assembly Items if shipping method is empty
				if(BBBUtility.isNotEmpty(destSg.getShippingMethod())) {
							List<CommerceItemRelationship> tempdestcirs = destSg.getCommerceItemRelationships();
							List<BBBCommerceItem> ciEligibleForDeliveryAssembly = new ArrayList<BBBCommerceItem>();
					for(CommerceItemRelationship destcir : tempdestcirs) {
						if(destcir.getCommerceItem() instanceof BBBCommerceItem){
							
								destci = (BBBCommerceItem)destcir.getCommerceItem();
							//Create Delivery/Assembly Items if sku is LTL && shipping method is supported
							if(destci.isLtlItem()){
								if(isLoggingDebug()){
									logDebug("BBBOrderManager:createDeliveryAssemblyCI Adding LTL Commerce ITem : " + destci.getId() + " in list to add Deliver/Assemble Commerce Items");
								}	
									ciEligibleForDeliveryAssembly.add(destci);
									updateOrderRequired =true;
								} 
							}
							}
					
					//create Delivery and Assembly commerce item for LTL Commerce items
					for(BBBCommerceItem tempdestci: ciEligibleForDeliveryAssembly){
						createDeliveryAssemblyCommerceItems(tempdestci,destSg,pDestOrder);
						updateOrderRequired =true;
						}
					}
				}
			}
		if(updateOrderRequired){
			getOrderManager().updateOrder(pDestOrder);
		}
		if(isLoggingDebug()){
			logDebug("BBBOrderManager:createDeliveryAssemblyCI ends");
	}
	}
	
	/**
	 * This method removes giftWrapCommerceItems from the Order
	 * @param pSrcOrder
	 * @throws CommerceException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void removeGiftWrapCommerceItems(Order pSrcOrder) throws CommerceException{
		
		if(isLoggingDebug()){
			logDebug("Mthd=[removeGiftWrapCommerceItems] starts");
		}
		
		if(pSrcOrder !=null ){
			
			final List items = pSrcOrder.getCommerceItems();
			//Check for GiftWrapCommcerItems to remove from order
			if (items != null) {
				
				final List<String> commerceIdsToRemove = new ArrayList<String>();

				if(isLoggingDebug()){
					logDebug("Mthd=[removeGiftWrapCommerceItems] Check for GiftWrapItem");
				}
				
				final Iterator<CommerceItem> itemIterator = items.iterator();
				while(itemIterator.hasNext())
				{
					final CommerceItem commerceItem = itemIterator.next();
					
					if(commerceItem instanceof GiftWrapCommerceItem){
						//item to remove form order
						commerceIdsToRemove.add(commerceItem.getId());
					}
				}
				if(isLoggingDebug()){
					logDebug("Mthd=[removeGiftWrapCommerceItems]  GiftWrapItems to remove== "+commerceIdsToRemove);
				}
				
				//remove unwanted giftCommerceItems
				for(String id :commerceIdsToRemove){
					if(isLoggingDebug()){
						logDebug("Mthd=[removeGiftWrapCommerceItems] Remove giftWrapCommerceItem : id="+id);
					}
					getCommerceItemManager().removeItemFromOrder(pSrcOrder, id);
					
					if(isLoggingDebug()){
						logDebug("Mthd=[removeGiftWrapCommerceItems] Item Removed -giftWrapCommerceItem : id="+id);
					}
				}
			} else{
				if(isLoggingDebug()){
					logDebug("Mthd=[removeGiftWrapCommerceItems] commerceItems are null");
				}
			}
		}
		if(isLoggingDebug()){
			logDebug("Mthd=[removeGiftWrapCommerceItems] ends");
		}
	}
	
	/**
	 * Re populate the availabilityMap of Order with Inventory availability
	 * It loops through all the CommerceItems in the Order and finds out the 
	 * Inventory availability then creates an entry in the mavailabilityMap
	 * 
	 * 
	 * @param pOrder
	 * @throws InventoryException
	 */
	@SuppressWarnings("unchecked")
	public void updateAvailabilityMapInOrder(DynamoHttpServletRequest pRequest, Order pOrder) {
		if (isLoggingDebug()) {
			logDebug("updateAvailabilityMapInOrder() : Starts");
		}
		final long startTime = this.getCurrentDate().getTime();
		String methodName = "updateAvailabilityMapInOrder";
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
		try {
			if (pOrder instanceof BBBOrder) {
				BBBOrder bbbOrder = (BBBOrderImpl) pOrder;
				Map<String, Integer> tempAvailabilityMap = new HashMap<String, Integer>();
				List<CommerceItem> itemList = bbbOrder.getCommerceItems();
				BBBCommerceItem bbbItem = null;
				BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer)pRequest.
						resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
				
				int availabilityStatus = BBBInventoryManager.AVAILABLE;
				// String siteId = SiteContextManager.getCurrentSiteId();
				if (isLoggingDebug()) {
					logDebug("updateAvailabilityMapInOrder() : SiteID  "
							+ pOrder.getSiteId());
				}
				// Iterate over all commerce Items and add to the
				// tempAvailabilityMap
				/* Commented below If() condition to fix P1 of JIRA, BSL-1305
				if(null != bbbOrder.getAvailabilityMap() && !bbbOrder.getAvailabilityMap().isEmpty()){
					tempAvailabilityMap = bbbOrder.getAvailabilityMap();
				}*/
				
				
				long rolledUpQty = 0;
				for (CommerceItem item : itemList) {
					if (item instanceof BBBCommerceItem) {
						bbbItem = (BBBCommerceItem) item;
						//Code removed for R2.1 requirment
						try {
							rolledUpQty = getPurchaseProcessHelper()
									.getRollupQtyForUpdate(
											bbbItem.getStoreId(),
											bbbItem.getRegistryId(),
											bbbItem.getCatalogRefId(),
											(BBBOrderImpl) bbbOrder,
											bbbItem.getQuantity());
							availabilityStatus = getPurchaseProcessHelper()
									.checkCachedInventory(pOrder.getSiteId(),
											bbbItem.getCatalogRefId(),
											bbbItem.getStoreId(), bbbOrder,
											rolledUpQty,
											BBBInventoryManager.RETRIEVE_CART,
											storeInventoryContainer, BBBInventoryManager.AVAILABLE);
						} catch (CommerceException e) {
							if (isLoggingError()) {
								logError(e.getMessage(), e);
							}
							availabilityStatus = BBBInventoryManager.AVAILABLE;
							isMonitorCanceled = true;
							BBBPerformanceMonitor.cancel(
									BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
						}
						tempAvailabilityMap.put(bbbItem.getId(),
								Integer.valueOf(availabilityStatus));
					}

					
				}
				
				// set the tempAvailabilityMap to order
				bbbOrder.setAvailabilityMap(tempAvailabilityMap);
				if (isLoggingDebug()) {
					logDebug("updateAvailabilityMapInOrder() : Ends");
				}
				
			}
		} finally {
			if (isLoggingDebug()) {
				long totalTime = System.currentTimeMillis() - startTime;
				logInfo("***Total time taken by BBBOrderManager.updateAvailabilityMapInOrder() is "
						+ totalTime);
				if(!isMonitorCanceled){
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
				}
			}
		}
	}
	
	/**
	 * Re populate the availabilityMap of Order with Inventory availability
	 * It loops through all the CommerceItems in the Order and finds out the 
	 * Inventory availability then creates an entry in the mavailabilityMap
	 * 
	 * 
	 * @param pOrder
	 * @throws InventoryException
	 */
	@SuppressWarnings("unchecked")
	public void updateRegistryMapInOrder(Order pOrder) throws CommerceException {
		if (isLoggingDebug()) {
			logDebug("updateRegistryMapInOrder() : Starts");
		}
		long startTime = this.getCurrentDate().getTime();;
		String methodName = "updateRegistryMapInOrder";
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
		try {
			if (pOrder instanceof BBBOrder) {
				BBBOrder bbbOrder = (BBBOrderImpl) pOrder;
				Map<String, RegistrySummaryVO> tempRegistryMap = new HashMap<String, RegistrySummaryVO>();
				List<CommerceItem> itemList = bbbOrder.getCommerceItems();
				BBBCommerceItem bbbItem = null;
				String registryId = null;
				RegistrySummaryVO registrySummaryVO = null;

				// Iterate over all commerce Items and add to the
				// tempAvailabilityMap

				for (CommerceItem item : itemList) {
					if (item instanceof BBBCommerceItem) {
						bbbItem = (BBBCommerceItem) item;
						registryId = bbbItem.getRegistryId();
						if (registryId != null) {
							try {
								registrySummaryVO = getRegistryManager()
										.getRegistryInfo(registryId,
												pOrder.getSiteId());
							} catch (BBBBusinessException e) {
								isMonitorCanceled = true;
								BBBPerformanceMonitor.cancel(
										BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
								throw new CommerceException(e);
							} catch (BBBSystemException e) {
								isMonitorCanceled = true;
								BBBPerformanceMonitor.cancel(
										BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
								throw new CommerceException(e);
							}
							tempRegistryMap.put(registryId, registrySummaryVO);
						}
					}

					// set the tempAvailabilityMap to order
					bbbOrder.setRegistryMap(tempRegistryMap);
					if (isLoggingDebug()) {
						logDebug("updateRegistryMapInOrder() : Ends");
					}
				}
			}
		} finally {
			if (isLoggingDebug()) {
				long totalTime = System.currentTimeMillis() - startTime;
				logInfo("***Total time taken by BBBOrderManager.updateRegistryMapInOrder() is "
						+ totalTime);
				if(!isMonitorCanceled){
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
				}
			}
		}
	}
		
	/**
	 * The method removes the item whose SKU is disabled (DISABLED = 'N') or not offered on web (WEB OFFER FLAG = 'N') from pDestOrder before calling mergeOrder
	 * 
	 * @param pSrcOrder
	 * @param pDestOrder	  
	 */
	@SuppressWarnings("unchecked")
	public void removeDisabledSKUs(Order pDestOrder)
			throws CommerceException {
		String methodName = BBBPerformanceConstants.REM_DISABLE_SKUS;
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
		try{
			List<CommerceItem> destCommerceItemsList = pDestOrder.getCommerceItems();
			List<String> removalItemList = new ArrayList<String>();
			String skuID = null;
			//String siteId = SiteContextManager.getCurrentSiteId();
			if (isLoggingDebug()) {
				logDebug("removeDisabledSKUs() : siteId "+pDestOrder.getSiteId());
			}
			if(destCommerceItemsList != null){
				
				for(CommerceItem tempItem : destCommerceItemsList){
					
					if (tempItem instanceof BBBCommerceItem) {
	
					skuID = tempItem.getCatalogRefId();	
					
					boolean isSkuAvailable;
					//logic to check if DISABLED = 'Y' or WEB OFFER FLAG = 'N'
					try{
						isSkuAvailable = getCatalogUtil().isSKUAvailable(pDestOrder.getSiteId(), skuID);
					}catch (BBBBusinessException e) {
						isMonitorCanceled = true;
						BBBPerformanceMonitor.cancel(
								BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
						
						throw new CommerceException("BBBBussiness Exception from removeDisabledSkus method of BBBOrderManager", e);
						
					} catch (BBBSystemException e) {
						isMonitorCanceled = true;
						BBBPerformanceMonitor.cancel(
								BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
						throw new CommerceException("BBBSystem Exception from removeDisabledSkus method of BBBOrderManager", e);
					}
					if(isLoggingDebug()){
						logDebug( new StringBuilder().append("SKU - ").append(skuID).append(" availability check for  from Catalog returned - ").append(isSkuAvailable).toString());
					}
					if( !isSkuAvailable){
						if(isLoggingDebug()){
							logDebug( new StringBuilder().append("Removing the SKU ").append(skuID).append( "as either WEB OFFER FLAG = 'N' or DISABLED = 'Y'").toString());
						}
						removalItemList.add(tempItem.getId());					
					}
				}else {
					if (isLoggingDebug()) {
						logDebug("inside removeDisabledSKUs Catalog , pDestOrder: "
								+ pDestOrder
								+ " is not instanceof BBBCommerceItem");
					}
				}
			}
			}
			
			for( String itemId: removalItemList){
				getCommerceItemManager().removeItemFromOrder(pDestOrder, itemId);
			}
		}finally{
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
			}
		}
	}
	
	
	/**
	 * The method removes the item whose SKU is disabled (DISABLED = 'N') or not offered on web (WEB OFFER FLAG = 'N') from pDestOrder before calling mergeOrder
	 * 
	 * @param pSrcOrder
	 * @param pDestOrder	  
	 */
	@SuppressWarnings("unchecked")
	public void updateSKUPropsFromCatalog(Order pDestOrder)
			throws CommerceException {
		boolean isMonitorCanceled = false;
		String methodName = "updateSKUPropsFromCatalog";
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
		try{
			List<CommerceItem> destCommerceItemsList = pDestOrder.getCommerceItems();
			SKUDetailVO skuDetailVO = null;
			String skuID = null;
			//String siteId = SiteContextManager.getCurrentSiteId();
			if(isLoggingDebug()){
				logDebug("updateSKUPropsFromCatalog() : siteID "+pDestOrder.getSiteId());
			}
			if(destCommerceItemsList != null){
				BBBCommerceItem bbbComItem = null;
				for (CommerceItem comItem : destCommerceItemsList) {
	
					if (comItem instanceof BBBCommerceItem) {
						bbbComItem = (BBBCommerceItem)comItem;
						skuID = bbbComItem.getCatalogRefId();
						try {
							skuDetailVO = getCatalogUtil().getSKUDetails(
									pDestOrder.getSiteId(), skuID, false, true, true);
	
						} catch (BBBBusinessException e) {
							isMonitorCanceled = true;
							BBBPerformanceMonitor.cancel(
									BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
							throw new CommerceException("BBBBussiness Exception from updateSKUPropsFromCatalog method of BBBOrderManager",
                                    e);
						} catch (BBBSystemException e) {
							isMonitorCanceled = true;
							BBBPerformanceMonitor.cancel(
									BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
							throw new CommerceException("BBBSystem Exception from updateSKUPropsFromCatalog method of BBBOrderManager",
                                    e);
						}
	
						if (skuDetailVO != null) {
							String freeShippingMethod = getPurchaseProcessHelper()
									.getFreeShippingMethodInfo(
											skuDetailVO.getFreeShipMethods());
							bbbComItem.setFreeShippingMethod(freeShippingMethod);
							bbbComItem.setVdcInd(skuDetailVO.isVdcSku());
							bbbComItem.setSkuSurcharge(skuDetailVO
									.getShippingSurcharge());
						}
					} else {
						if (isLoggingDebug()) {
							logDebug("inside updateSKUPropsFrommethod Catalog , pDestOrder: "
									+ pDestOrder
									+ " is not instanceof BBBCommerceItem");
						}
					}
					
				}
				// update the order with the changes
				//updateOrder(pDestOrder);
			}
		}finally{
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
			}
		}
	}
	
	/**
	 * Only pull those orders which are submitted, but haven't been sent to TIBCO
	 * 
	 * @param pSubstatus
	 * @param mSubmittedDate
	 * @param startIndex
	 * @param endIndex
	 * @return
	 * @throws BBBSystemException
	 */
	public Order[] getUnsubmittedOrders(String pSubstatus, Timestamp mSubmittedDate, int pStartIndex, int pEndIndex) throws BBBSystemException{
		final String methodName = CLASS_NAME + ".getUnsubmittedOrders()";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ORDER_CONFIRMATION, methodName);
		Order[] orders = null;
		try{
			if(pSubstatus != null && mSubmittedDate != null) {
				BBBOrderTools tools = (BBBOrderTools) getOrderTools();
				RepositoryItem[] orderItems = tools.getUnsubmittedOrders(pSubstatus, mSubmittedDate, pStartIndex, pEndIndex);
				if(orderItems != null && orderItems.length > 0){
					String orderID = null;
					Order order = null;
					List<Order> orderList = new ArrayList<Order>(orderItems.length);
					try {
						for(int index = 0; index < orderItems.length; index++){
							orderID = orderItems[index].getRepositoryId();
							order = loadOrder(orderID);
							orderList.add(order);
						}
						
						orders = orderList.toArray(new Order[orderList.size()]);
					} catch (CommerceException e) {
						String msg = "Error while loading order [" + orderID + "]";
						
						throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1071,msg, e);
					}
				}
				
			}
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ORDER_CONFIRMATION,
					methodName);		
		}		
		
		return orders;
	}
	
	/**
	 * Update the order substatus with the provided value
	 * 
	 * @param pOrder
	 * @param pSubstatus
	 * @return
	 * @throws BBBSystemException
	 */
	public boolean updateOrderSubstatus(Order pOrder, String pSubstatus) throws BBBSystemException {
		final String methodName = CLASS_NAME + ".updateOrderSubstatus()";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ORDER_CONFIRMATION, methodName);
		boolean success = false;
		try{
			if(pOrder != null && pSubstatus != null) {
				BBBOrderTools tools = (BBBOrderTools) getOrderTools();
				success = tools.updateOrderSubstatus(pOrder, pSubstatus);
			}
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ORDER_CONFIRMATION, methodName);
		}
		return success;
	}
	
	/**
	 * Update the order substatus with the provided value
	 * 
	 * @param pOrder
	 * @param pSubstatus
	 * @return
	 * @throws BBBSystemException
	 */
	public boolean updateOrderState(Order pOrder, int state) throws BBBSystemException {
		final String methodName = CLASS_NAME + ".updateOrderState()";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ORDER_CONFIRMATION, methodName);
		boolean success = false;
		try{
			if(pOrder != null) {
				BBBOrderTools tools = (BBBOrderTools) getOrderTools();
				success = tools.updateOrderState(pOrder, state);
			}
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ORDER_CONFIRMATION, methodName);
		}
		return success;
	}
	
	/**
	 * Update the order XML with the provided value
	 * 
	 * @param pOrder
	 * @param pSubstatus
	 * @return
	 * @throws BBBSystemException
	 */
	public boolean updateOrderXML(Order pOrder, String pOrderXML) {
		final String methodName = CLASS_NAME + ".updateOrderXML()";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ORDER_CONFIRMATION, methodName);
		boolean success = false;
		String orderID = pOrder.getId();
		
		if(isLoggingDebug()){
			logDebug("START : Updating Order [" + orderID + "] XML" + "OrderXML: " + pOrderXML);
		}	
		
		BBBOrder bbbOrder = (BBBOrder)pOrder;
		bbbOrder.setOrderXML(pOrderXML);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ORDER_CONFIRMATION, methodName);
		
		if(isLoggingDebug()){
			logDebug("END : Updating Order [" + orderID + "] XML");
		}

		return success;
	}
	
	public CommerceItem addAsSeparateItemToShippingGroup(BBBOrder bbbOrder,
            BBBHardGoodShippingGroup pbbbShippingGroup, String skuId,
            long quantity, String productId, String commerceItemType)
            throws CommerceException {
        
        CommerceItem item = getCommerceItemManager().createCommerceItem(commerceItemType, skuId,
                productId, quantity);

        if(item != null) {
            if (isLoggingDebug()) {
                logDebug("Commcerce item created: " + item);
            }
            getCommerceItemManager().addAsSeparateItemToOrder(bbbOrder, item);
            getCommerceItemManager().addItemQuantityToShippingGroup(bbbOrder, item.getId(), pbbbShippingGroup.getId(), quantity);
        } else {
            if (isLoggingDebug()) {
                logDebug("Commcerce item creation failed for sku " + skuId);
            }
        }
        return item;
    }
	
	/**
	 * This method creates a hardcoded orderID if BOT is visiting site. This is
	 * to avoid exhausting ATG orderIDs
	 */
	public Order createOrder(String pProfileId, String pOrderType)
			throws CommerceException {
		
		if (isLoggingDebug()) {
			logDebug("[START] BBBOrderManager.createOrder");
		}
		
		Order order = null;
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

		if (request != null
				&& BBBUtility.isRobot(request)) {
			order = super.createOrder(pProfileId, getBotOrderID(),
					pOrderType);
			if (isLoggingDebug()) {
				logDebug("BOT is visiting page, Created Order ID: " + order.getId());
				logDebug("Created Order :" + order);
			}
		} else {
			order = super.createOrder(pProfileId, pOrderType);
			order.setSiteId(atg.multisite.SiteContextManager.getCurrentSiteId());
			if (isLoggingDebug()) {
				logDebug("Browser visit , Created Order ID: " + order.getId());
				logDebug("Created Order :" + order);
			}
		}
		
		if (isLoggingDebug()) {
			logDebug("[END] BBBOrderManager.createOrder");
		}
		
		return order;
	}

	/**
	 * @return the mBotOrderID
	 */
	public String getBotOrderID() {
		final StringBuilder orderID = new StringBuilder();
		orderID.append(BOT);
		orderID.append(System.currentTimeMillis());
		this.mBotOrderID = orderID.toString();
		if (isLoggingDebug()) {
			logDebug("BOT OrderID is: " + this.mBotOrderID);
		}
		
		return this.mBotOrderID;
	}

	/**
	 * @param pBotOrderID the mBotOrderID to set
	 */
	public void setBotOrderID(String pBotOrderID) {
		this.mBotOrderID = pBotOrderID;
	}

	/**
	 * @param order
	 * @throws CommerceException 
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public boolean removeALLDeliveryAssemblyCIFromOrderBySG(Order order, String orderType) throws CommerceException
	{
		boolean isLTLItemInOrder = false;
		
		if(isLoggingDebug()){
			logDebug("BBBOrderManager:removeALLDeliveryAssemblyCIFromOrderBySG starts");
		}
		List <BBBCommerceItem> ltlCommerceItemList = new ArrayList<BBBCommerceItem>();
		List<CommerceItem> commerceItems = order.getCommerceItems();
		for(CommerceItem commItem : commerceItems){	
			if(commItem instanceof BBBCommerceItem && ((BBBCommerceItem)commItem).isLtlItem()){
				ltlCommerceItemList.add((BBBCommerceItem)commItem);
				isLTLItemInOrder = true;
			}
		}
		
		for(BBBCommerceItem ltlCommerceItem : ltlCommerceItemList){
			if(isLoggingDebug()){
				logDebug("Removing the Delivery and Assembly Items from the commerce ITem : " + ltlCommerceItem.getId());
				}
			((BBBCommerceItemManager)this.getCommerceItemManager()).removeDeliveryAssemblyCIFromOrderByCISg(ltlCommerceItem.getId(), order);
			ltlCommerceItem.setDeliveryItemId(null);
			ltlCommerceItem.setAssemblyItemId(null);
			
			//[Start] BPSI-1072
			//Do the processing only for Destination order 
			if(orderType.equalsIgnoreCase(BBBCoreConstants.DESTINATION_ORDER)){
				String newShipGroupShippingMethod = BBBCoreConstants.BLANK;
				String ltlShipMethod = ltlCommerceItem.getLtlShipMethod();
				boolean isWhiteGloveAssembly = Boolean.parseBoolean(ltlCommerceItem.getWhiteGloveAssembly());
				String shippingMethod = ((BBBShippingGroupCommerceItemRelationship) ltlCommerceItem
						.getShippingGroupRelationships().get(0))
						.getShippingGroup().getShippingMethod();
				if(null == shippingMethod){
					if(isLoggingDebug()){
						logDebug("Shipping method of shipping group is null, so set the shipping method as blank");
					}
					((BBBShippingGroupCommerceItemRelationship) ltlCommerceItem
							.getShippingGroupRelationships().get(0))
							.getShippingGroup().setShippingMethod(BBBCoreConstants.BLANK);
				}
				//For users which have LTL items in cart before code is live ( ltlShipMethod is empty for those items)
				if(BBBUtility.isEmpty(ltlShipMethod)){
					if(isLoggingDebug()){
						logDebug("LTL ship method of commerce item is empty. Setting the shipping method : " + shippingMethod + " from shipping group");
					}
					ltlShipMethod = shippingMethod;
				}
				// If shipping method of commerce item and shipping method of shipping group is null or empty, then no need to do anything
				if(!BBBUtility.isEmpty(ltlShipMethod)){
					try {
						//Checking the shipping method availability
						boolean isShippingMethodExistsForSku = getCatalogUtil().isShippingMethodExistsForSku(order.getSiteId(), ltlCommerceItem.getCatalogRefId(), ltlShipMethod, isWhiteGloveAssembly);
						
						//If shipping method does not exist and shipping method is not empty , attach this commerce item to empty shipping group
						if(!isShippingMethodExistsForSku && !StringUtils.isEmpty(shippingMethod)){
							if(isLoggingDebug()){
								logDebug("shipping method does not exist, so attach this commerce item " + ltlCommerceItem.getId() + " to empty shipping group");
							}
							ShippingGroup emptySgGrp = getLtlShippingGroup(ltlCommerceItem, order, newShipGroupShippingMethod);
				this.getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, ltlCommerceItem.getId());
							this.getCommerceItemManager().addItemQuantityToShippingGroup(order,ltlCommerceItem.getId(), emptySgGrp.getId(),ltlCommerceItem.getQuantity());
							this.getShippingGroupManager().removeEmptyShippingGroups(order);
							ltlCommerceItem.setShipMethodUnsupported(true);
			}
						//If shipping method does not exists and shipping method is already empty for shipping group, only set the flag to true
						else if(!isShippingMethodExistsForSku && StringUtils.isEmpty(shippingMethod)){
							if(isLoggingDebug()){
								logDebug("shipping method does not exist and shipping method is already empty for Shipping group, " +
										"so only setting the flag to true for this commerce item " + ltlCommerceItem.getId());
							}
							ltlCommerceItem.setShipMethodUnsupported(true);
						}
						
						//If shipping method now exists and shipping method of shipping group does not match shipping method of commerce item,
						//attach this commerce item to LTL shipping method of Commerce Item
						else if(isShippingMethodExistsForSku && !ltlShipMethod.equalsIgnoreCase(shippingMethod)){
							if(isLoggingDebug()){
								logDebug("shipping method now exists and shipping method of shipping group does not match shipping method of commerce item "  + 
										ltlCommerceItem.getId() + "Attaching this to LTL Shipping Group with shipping method: " + ltlShipMethod);
							}
							newShipGroupShippingMethod = ltlShipMethod;
							ShippingGroup sgGrp = getLtlShippingGroup(ltlCommerceItem, order, newShipGroupShippingMethod);
							this.getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, ltlCommerceItem.getId());
							this.getCommerceItemManager().addItemQuantityToShippingGroup(order,ltlCommerceItem.getId(), sgGrp.getId(),ltlCommerceItem.getQuantity());
			this.getShippingGroupManager().removeEmptyShippingGroups(order);
							
		}
		
					} catch (BBBBusinessException e) {
						logError("Bussiness Exception from removeALLDeliveryAssemblyCIFromOrderBySG method of BBBOrderManager" + e.getMessage(), e);
					} catch (BBBSystemException e) {
						logError("System Exception from removeALLDeliveryAssemblyCIFromOrderBySG method of BBBOrderManager" + e.getMessage(), e);
					}
				}
			}
			//[End] BPSI-1072
		}
		
		try {
			if(!ltlCommerceItemList.isEmpty()){
				this.getShippingGroupManager().removeEmptyShippingGroups(order);
				super.updateOrder(order);
			}
		} catch (CommerceException e) {
			logError("Cannot Update order" + e.getMessage(), e);
		}
		if(isLoggingDebug()){
			logDebug("BBBOrderManager:removeALLDeliveryAssemblyCIFromOrderBySG ends");
	}
		return isLTLItemInOrder;
	}
	
	/**
	 * @param currentCommItem
	 * @param shippingGroup
	 * @param order
	 * @throws RepositoryException 
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public void createDeliveryAssemblyCommerceItems(BBBCommerceItem currentCommItem, ShippingGroup shippingGroup, Order order)
			throws BBBBusinessException, BBBSystemException, CommerceException, RepositoryException {
		BBBCommerceItemManager commerceItemManager = (BBBCommerceItemManager)getCommerceItemManager();
		String deliveryCommId = "";
		String assemblyCommId = "";
		if (null != currentCommItem.getWhiteGloveAssembly() && currentCommItem.getWhiteGloveAssembly().equalsIgnoreCase("true")){
			assemblyCommId = commerceItemManager.addLTLAssemblyFeeSku(order, shippingGroup,order.getSiteId(), currentCommItem);
		}
		deliveryCommId = commerceItemManager.addLTLDeliveryChargeSku(order, shippingGroup,order.getSiteId(), currentCommItem);
		currentCommItem.setDeliveryItemId(deliveryCommId);
		currentCommItem.setAssemblyItemId(assemblyCommId);
	}
	
	 /**
     * It is a class method to return the same shipping group/create a new shipping group for LTL item
     *  @param BBBCommerceItem
     *  @param pOrder
     * @throws CommerceException
     */
    @SuppressWarnings("unchecked")
	private ShippingGroup getLtlShippingGroup(BBBCommerceItem ltlCommerceItem, Order pOrder, String ltlShippingMethod) throws CommerceException {
    	
		this.logDebug("[Start] BBBOrderManager.getLtlShippingGroup");
		BBBShippingGroupManager bbbSGManager = (BBBShippingGroupManager)this.getShippingGroupManager(); 
		Collection<ShippingGroup> shippingGroups = pOrder.getShippingGroups();

		if (shippingGroups != null && !shippingGroups.isEmpty()) {

			for (final ShippingGroup shippingGroup : shippingGroups) {

				if (shippingGroup != null
						&& (shippingGroup instanceof HardgoodShippingGroup)						 
						&& shippingGroup.getShippingMethod() != null
						&& ((shippingGroup.getShippingMethod().equalsIgnoreCase(ltlShippingMethod)))) {
							return ((HardgoodShippingGroup) shippingGroup);
				}

			}
		}
		this.logDebug("Create Shipping group if shipping group is null in order and set shipping method as: " + ltlShippingMethod + " and add that shipping address to order");
		ShippingGroup mShippingGroup = bbbSGManager.createShippingGroup(bbbSGManager.getOrderTools().getDefaultShippingGroupType());
		mShippingGroup.setShippingMethod(ltlShippingMethod);
		bbbSGManager.addShippingGroupToOrder(pOrder, mShippingGroup);
		
		this.logDebug("[Exit] BBBOrderManager.getLtlShippingGroup");

		return mShippingGroup;
	}
	
	/**
	 * This method is used to initialize the sequence number of items in the pos list saved in the session.
	 * 
	 * @param pRequest in DynamoHttpServletRequest format
	 * @param order of type BBBOrder
	 */
	public void initializePosListInSession(DynamoHttpServletRequest pRequest, BBBOrder order) {
		 @SuppressWarnings ("unchecked")
         List<String> pos = (List<String>) pRequest.getSession().getAttribute(BBBCoreConstants.ITEM);
         	final List<CommerceItem> comItemObjList = order.getCommerceItems();
         	if (comItemObjList != null) {
         		if (pos == null) {
         			pos = new ArrayList<String>();
         		}
         		for (final CommerceItem comItemObj : comItemObjList) {
         			if (comItemObj instanceof BBBCommerceItem) {
         				if (!StringUtils.isEmpty(((BBBCommerceItem) comItemObj).getRegistryId())) {
         					pos.add(0, comItemObj.getId());
         				} else {
         						pos.add(comItemObj.getId());
         				}
         				((BBBCommerceItem) comItemObj).setMsgShownFlagOff(false);
         			}
         		}
         		pRequest.getSession().setAttribute(BBBCoreConstants.ITEM, pos);
         }
	}
	
	/**
	 * This method return commerce item from order based on commerce item class
	 * @param classType
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CommerceItem> getCommerceItems(Class<? extends CommerceItem> classType, Order order) {
		logDebug("Inside BBBOrderManager.getCommerceItem classType::" + classType + " order:: " + order.getId());
		
		List<CommerceItem> commerceItems = order.getCommerceItems();
		List<CommerceItem> classTypeItems = new ArrayList<>();
		
		for (CommerceItem commerceItem : commerceItems) {
			if (classType.isInstance(commerceItem)) {
				classTypeItems.add(commerceItem);
			}
		}
		
		logDebug("Exting from BBBOrderManager.getCommerceItem classTypeItems::" + classTypeItems);
		return classTypeItems;
	}
	
	/**
	 * Moves gift items from order.
	 * 
	 * @param pRemovableItems
	 *            the removable items
	 * @param pRemovableItemQuantityMap
	 *            the removable item quantity map
	 * @param pOrder
	 *            the order
	 * @throws InvalidGiftQuantityException
	 *             the invalid gift quantity exception
	 * @throws CommerceException
	 *             the commerce exception
	 */
	public void moveGiftItemsFromOrder(List<CommerceItem> pRemovableItems, Map<String, Long> pRemovableItemQuantityMap, Order pOrder)
			throws InvalidGiftQuantityException, CommerceException {
		
		Long removableQty;
		CommerceItem lCommerceItem;
		long itemQty;
		long quantity;
		for (Iterator<CommerceItem> lIterator = pRemovableItems.iterator(); lIterator.hasNext();) {
			lCommerceItem = (CommerceItem) lIterator.next();

			removableQty = pRemovableItemQuantityMap.get(lCommerceItem.getId());
			if (removableQty <= 0)
				throw new InvalidGiftQuantityException();

			itemQty = lCommerceItem.getQuantity();
			quantity = itemQty - removableQty;
			if (quantity > 0) {
				lCommerceItem.setQuantity(quantity);
				vlogDebug("updating the item quantity after moving the " + removableQty + " quantity to the gift list");
			} else {
				getCommerceItemManager().removeItemFromOrder(pOrder, lCommerceItem.getId());
			}
		}
		updateOrder(pOrder);
	}
	
	/**
	 * Copy email from billing address to BBBOrder email property
	 * This method must be called in a transaction and the updateOrder() method must be called after this
	 * 
	 * @param pOrderItem
	 * @throws Exception
	 */
	public void addEmailAddressToBBBOrder(MutableRepositoryItem pOrderItem) throws Exception {
		String orderId = "";
		MutableRepository mutRep = (MutableRepository) getOrderTools().getOrderRepository();
		if (pOrderItem == null) {
			throw new BBBSystemException(BBBCoreConstants.ORDERITEM_NOT_AVAILABLE);
		} else {
			orderId = (String) pOrderItem.getRepositoryId();
			pOrderItem = mutRep.getItemForUpdate(orderId, getOrderItemDescriptorName());
		}
		
		if (pOrderItem == null) {
			mutRep = (MutableRepository) ((BBBOrderTools)getOrderTools()).getArchiveOrderRepository();
			pOrderItem = mutRep.getItemForUpdate(orderId, getOrderItemDescriptorName());
		}
		//If this property is not set then get it from billing_address table
		if(pOrderItem != null && null == pOrderItem.getPropertyValue(BBBCoreConstants.EMAIL_PROPERTY_ON_ORDER)) {
			RepositoryItem billingAddress = (RepositoryItem) pOrderItem.getPropertyValue(BBBCoreConstants.BILLING_ADDRESS);
			if(null != billingAddress) {
				String email = (String) billingAddress.getPropertyValue(BBBCoreConstants.EMAIL);
				if (StringUtils.isNotEmpty(email)) {					
					pOrderItem.setPropertyValue(BBBCoreConstants.EMAIL_PROPERTY_ON_ORDER, email.toLowerCase());
				}
			}
		}
	}

/**
 * Loads order from archive repository 
 * @param pOrderId
 * @return loaded order
 * @throws CommerceException
 */
@SuppressWarnings("unchecked")
public Order loadArchiveOrder (String pOrderId) throws CommerceException {
	
	  vlogDebug("Enter load archive order id {0}", pOrderId);
	    
	    HashMap paramMap = new HashMap(13);
		paramMap.put("OrderManager", this);
		paramMap.put("CatalogTools", getOrderTools().getCatalogTools());
		paramMap.put("OrderId", pOrderId);
		paramMap.put("OrderRepository", ((BBBOrderTools) getOrderTools()).getArchiveOrderRepository());
		paramMap.put("LoadOrderPriceInfo", Boolean.FALSE);
		paramMap.put("LoadTaxPriceInfo", Boolean.FALSE);
		paramMap.put("LoadItemPriceInfo", Boolean.FALSE);
		paramMap.put("LoadShippingPriceInfo", Boolean.FALSE);
		PipelineResult result;
		
		try
		{
			result = getPipelineManager().runProcess("loadOrder",paramMap);
			
		} catch (RunProcessException e) {
			throw new CommerceException(e);
}
		
		Order order = (Order) paramMap.get("Order");
		if (order == null) {
			throw new InvalidParameterException(
					ResourceUtils.getMsgResource("InvalidOrderIdParameter",	"atg.commerce.order.OrderResources",
							sResourceBundle));
			}
		vlogDebug("Exit load archive order");
		return order;
  }
  
/**
 * Method to load archive orders
 * @param pOrderIds
 * @return list of orders
 * @throws CommerceException
 */
@SuppressWarnings("unchecked")
public List<Order> loadArchiveOrders(List<String> pOrderIds) throws CommerceException {
	  
	  vlogDebug("Enter loadArchiveOrders order ids are {0}",pOrderIds);
	  
	  if (BBBUtility.isListEmpty(pOrderIds)) {
			return Collections.EMPTY_LIST;
	   }
		
	    ArrayList orderList = new ArrayList(pOrderIds.size());
	    
		for (String orderId : pOrderIds) {
			
			if(orderExistsInArchive(orderId)){
				
				orderList.add(loadArchiveOrder(orderId));
		}
		}
		
		vlogDebug("Exit loadArchiveOrders");
		return orderList;
  }


@Override
public boolean orderExists(String pOrderId) throws CommerceException {
	vlogDebug("BBBOrderManager.orderExists(String) order id {0}", pOrderId);
	boolean orderExists = super.orderExists(pOrderId);
	
	final String fetchArchivedOrder = getCatalogUtil().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
			BBBCoreConstants.FETCH_ARCHIVED_ORDERS, BBBCoreConstants.FALSE);
	
	vlogDebug("Value of fetchArchivedOrder is {0} ", fetchArchivedOrder);
	
	//In the case order not found in order repository then search in archive repository
	if (!orderExists && Boolean.valueOf(fetchArchivedOrder)) {
		vlogDebug("Order not found in order repository so looking in archive repository");
		orderExists = orderExistsInArchive(pOrderId);
	}
	vlogDebug("BBBOrderManager.orderExists(String) order exists", orderExists);
	return orderExists;
}

@Override
public Order loadOrder(String pOrderId) throws CommerceException {
	vlogDebug("BBBOrderManager.loadOrder(String) Loading order from repository order {0}", pOrderId);
	// load order from core schema
	Order order=null;
	
	if(super.orderExists(pOrderId)){
	order = super.loadOrder(pOrderId);
	}
	final String fetchArchivedOrder = getCatalogUtil().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
			BBBCoreConstants.FETCH_ARCHIVED_ORDERS, BBBCoreConstants.FALSE);
	
	vlogDebug("Value of fetchArchivedOrder is {0} ", fetchArchivedOrder);
	
	//order is null try to load order from archive repository
	if(null == order && Boolean.valueOf(fetchArchivedOrder)) {
		vlogDebug("Order not found in order repository. Loading order from archive repository ");
		if(orderExistsInArchive(pOrderId)){
		order = loadArchiveOrder(pOrderId);
	}
	}
	return order;
}

/**
 * Check whether order exists in archive repository
 * @param pOrderId
 * @return
 * @throws CommerceException
 */
public boolean orderExistsInArchive(String pOrderId) throws CommerceException {
	vlogDebug("BBBOrderManager.orderExistsInArchive(String) : searching for order {0}", pOrderId);
	
	final String fetchArchivedOrder = getCatalogUtil().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
			BBBCoreConstants.FETCH_ARCHIVED_ORDERS, BBBCoreConstants.FALSE);
	
	vlogDebug("Value of fetchArchivedOrder is {0} ", fetchArchivedOrder);
	
	if(!Boolean.valueOf(fetchArchivedOrder)) {
		return Boolean.FALSE;
	}
	
	if (pOrderId == null) {
		throw new InvalidParameterException(
				ResourceUtils.getMsgResource("InvalidOrderIdParameter",
						"atg.commerce.order.OrderResources",
						sResourceBundle));
	}
	
	try {
		return (((BBBOrderTools) getOrderTools()).getArchiveOrderRepository().getItem(pOrderId, getOrderItemDescriptorName())) != null;
	} catch (RepositoryException e) {
		throw new CommerceException(e);
	}
	
}
}
