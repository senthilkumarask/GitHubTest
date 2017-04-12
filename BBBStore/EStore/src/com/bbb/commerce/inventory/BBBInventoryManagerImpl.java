package com.bbb.commerce.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderHolder;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * This class is used to get inventory status of products
 * 
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class BBBInventoryManagerImpl extends BBBGenericService implements
		BBBInventoryManager {

    private static final String CHECK_INVENTORY = "checkInventory";
    private static final String ATG_SHOPPING_CART = "/atg/commerce/ShoppingCart";
	private final String SITE_HEADER = "X-bbb-site-id";
	private final String TRUE ="true";
	private final String FALSE ="false";
	private final String PRODUCT_DISPLAY = "productDisplay";
    /*
	 * ==================================================== * MEMBER VARIABLE
	 * ====================================================
	 */
	private OnlineInventoryManager mOnlineInventoryManager;
	private BBBStoreInventoryManager mStoreInventoryManager;
	private BBBCatalogTools mCatalogTools;
	private BopusInventoryService mBopusService;
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

	/*
	 * ===================================================== * SETTERS & GETTERS
	 * =====================================================
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	public OnlineInventoryManager getOnlineInventoryManager() {
		return mOnlineInventoryManager;
	}

	public void setOnlineInventoryManager(
			OnlineInventoryManager onlineInventoryManager) {
		this.mOnlineInventoryManager = onlineInventoryManager;
	}

	public BBBStoreInventoryManager getStoreInventoryManager() {
		return mStoreInventoryManager;
	}

	public void setStoreInventoryManager(
			BBBStoreInventoryManager storeInventoryManager) {
		this.mStoreInventoryManager = storeInventoryManager;
	}
	public BopusInventoryService getBopusService() {
		return mBopusService;
	}

	public void setBopusService(
			BopusInventoryService bopusService) {
		this.mBopusService = bopusService;
	}
	
	/**
	 * Adding new method for everliving pdp to check product availability for
	 * skus without checking its active, inactive status.
	 */
	/**
	 * This method return the inventory status for items
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param pReqQty
	 * @return status
	 * @exception InventoryException
	 * 
	 */
	@Override
	public int getEverLivingProductAvailability(String pSiteId, String pSkuId,
			String operation) throws BBBBusinessException, BBBSystemException {
	    BBBPerformanceMonitor.start("BBBInventoryManager getProductAvailability");
			logDebug("BBBInventoryManager : getProductAvailability() starts");
			logDebug("Input parameters : pSiteId " + pSiteId + " ,pSkuId "
					+ pSkuId + " & operation " + operation);

		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		SKUDetailVO skuDetailVO = null;

		try {
	
				logDebug("Calling CatalogTools getSKUDetails() method : Input Parameters are pSiteId - "
						+ pSiteId
						+ " ,pSkuId - "
						+ pSkuId
						+ " & calculateAboveBelowLine - " + false);

			skuDetailVO = getCatalogTools().getEverLivingSKUDetails(pSiteId, pSkuId, true);

			
				logDebug("response skuDetailVO from CatalogTools getSKUDetails() method :"
						+ skuDetailVO);

			String caFlag = skuDetailVO.getEcomFulfillment();

			if (skuDetailVO.isVdcSku()) {
				availabilityStatus = getVDCProductAvailability(pSiteId, pSkuId,
						0, BBBCoreConstants.CACHE_ENABLED);
			} else {
				availabilityStatus = getNonVDCProductAvailability(pSiteId,
						pSkuId, caFlag, operation, BBBCoreConstants.CACHE_ENABLED,0);
			}
		} catch (InventoryException e) {
			if(isLoggingDebug()){
			logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1021 +
					": BBBInventoryManager : getProductAvailability() Inventory Exception occoured",
					e);
			}
			availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
		}


			logDebug("BBBInventoryManager : getProductAvailability() ends.");
			logDebug("Output - availabilityStatus " + availabilityStatus);

		BBBPerformanceMonitor.end("BBBInventoryManager getProductAvailability");
		return availabilityStatus;
	}

	/*
	 * ===================================================== * METHODS
	 * =====================================================
	 */

	// This will be used from Product Display page and Registry Product Display
	// page

	/**
	 * This method return the inventory status for items
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param pReqQty
	 * @return status
	 * @exception InventoryException
	 * 
	 */
	@Override
	public int getProductAvailability(String pSiteId, String pSkuId,
			String operation, long qty) throws BBBBusinessException, BBBSystemException {
	    BBBPerformanceMonitor.start("BBBInventoryManager getProductAvailability");
			logDebug("BBBInventoryManager : getProductAvailability() starts");
			logDebug("Input parameters : pSiteId " + pSiteId + " ,pSkuId "
					+ pSkuId + " & operation " + operation);

		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		SKUDetailVO skuDetailVO = null;

		try {

				logDebug("Calling CatalogTools getSKUDetails() method : Input Parameters are pSiteId - "
						+ pSiteId
						+ " ,pSkuId - "
						+ pSkuId
						+ " & calculateAboveBelowLine - " + false);
			skuDetailVO = getCatalogTools().getSKUDetails(pSiteId, pSkuId,
					false);

			String caFlag = skuDetailVO.getEcomFulfillment();

			if (skuDetailVO.isVdcSku()) {
				availabilityStatus = getVDCProductAvailability(pSiteId, pSkuId,
						qty, BBBCoreConstants.CACHE_ENABLED);
			} else {
				availabilityStatus = getNonVDCProductAvailability(pSiteId,
						pSkuId, caFlag, operation, BBBCoreConstants.CACHE_ENABLED, qty);
			}
		} catch (InventoryException e) {
			if(isLoggingDebug()){
			logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1021 +
					": BBBInventoryManagerImpl : getProductAvailability() Exception occurred in getting Inventory information  for sku " + pSkuId);
			}
			availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
		}

			logDebug("BBBInventoryManager : getProductAvailability() ends.");
			logDebug("Output - availabilityStatus " + availabilityStatus);

		BBBPerformanceMonitor.end("BBBInventoryManager getProductAvailability");
		return availabilityStatus;
	}

	// getProductAvailability() will call this with pReqQty = 0

	/**
	 * This method return the inventory status for VDC item
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param pReqQty
	 * @return status
	 * @exception InventoryException
	 * 
	 */
	@Override
	public int getVDCProductAvailability(String pSiteId, String pSkuId,
			long pReqQty, boolean pUseCache) throws InventoryException {
		BBBPerformanceMonitor.start("BBBInventoryManager getVDCProductAvailability");
			StringBuilder sb = new StringBuilder();
			sb.append("getVDCProductAvailability() : starts Input Parametrs: skuID - ");
			sb.append(pSkuId);
			sb.append(" , siteId - ");
			sb.append(pSiteId);
			sb.append(" & pReqQty - ");
			sb.append(pReqQty);
			sb.append(" & pUseCache - ");
			sb.append(pUseCache);
			logDebug(sb.toString());

		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		InventoryVO inventoryVO = null;

		try {
			if (pUseCache) {
				inventoryVO = getOnlineInventoryManager().getCachedInventory(
						pSkuId, pSiteId);

					logDebug("output parameter from CatalogTools getCachedInventory() method : "
							+ inventoryVO);

			} else {
				inventoryVO = getOnlineInventoryManager().getInventory(pSkuId,
						pSiteId);
					logDebug("output parameter from CatalogTools getInventory() method : "
							+ inventoryVO);
				}

			if (inventoryVO != null) {
				long afs = inventoryVO.getGlobalStockLevel();

				if (pReqQty > 0) {
					// Add Item, updateItem, storeToOnline, retrieveCart,
					// orderSubmission
					if (afs >= pReqQty) {
						availabilityStatus = BBBInventoryManager.AVAILABLE;
					} else {
						availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
					}
				} else {
					// Product Detail Page and Registry Product page
					if (afs == 0 || afs < 0) {
						availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
					} else {
						availabilityStatus = BBBInventoryManager.AVAILABLE;
					}
				}
			} else {
				throw new InventoryException("InventoryVO from catalog is null");
			}

		} catch (BBBBusinessException e) {
			throw new InventoryException(e);
		} catch (BBBSystemException e) {
			throw new InventoryException(e);
		}
		BBBPerformanceMonitor.end("BBBInventoryManager getVDCProductAvailability");
		return availabilityStatus;
	}

	/**
	 * This method return the inventory status for NonVDC item - to be used for
	 * following cases
	 * <ul>
	 * <li>Product Details page</li>
	 * <li>Registry Product Page</li>
	 * <li>Store to Online</li>
	 * <li>Retrieve Cart</li>
	 * </ul>
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param caFlag
	 * @return status
	 * @exception InventoryException
	 * 
	 */
	@Override
	public int getNonVDCProductAvailability(String pSiteId, String pSkuId,
			String caFlag, String operation, boolean pUseCache, long reqQty) throws InventoryException {
		BBBPerformanceMonitor.start("BBBInventoryManager getNonVDCProductAvailability");

			logDebug("getNonVDCProductAvailability() : starts");

			StringBuilder sb = new StringBuilder();
			sb.append("Input Parametrs: skuID - ");
			sb.append(pSkuId);
			sb.append(" , siteId - ");
			sb.append(pSiteId);
			sb.append(" , caFlag - ");
			sb.append(caFlag);
			sb.append(" & operation - ");
			sb.append(operation);
			sb.append(" & UseCache - ");
			sb.append(pUseCache);
			logDebug(sb.toString());


		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		InventoryVO inventoryVO = null;

		try {
			if (pUseCache) {
				inventoryVO = getOnlineInventoryManager().getCachedInventory(pSkuId, pSiteId);
			} else {
				inventoryVO = getOnlineInventoryManager().getInventory(pSkuId, pSiteId);
			}
			
			if (inventoryVO != null) {
				long afs = inventoryVO.getGlobalStockLevel();
				long altAFS = inventoryVO.getSiteStockLevel();
				long igr = inventoryVO.getGiftRegistryStockLevel();

				// BBBSL-5014 defect fixed. The inventory check needs to be done against the requested quantity instead of 0 for all kind 
				// of items on adding the item to cart from anywhere.
				if(reqQty > 0){
				if (RETRIEVE_CART.equals(operation)
						|| ADD_ITEM_FROM_REG.equals(operation)) {
					// Retrieve Cart and Add From Registry

					if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(pSiteId) || "TBS_BedBathCanada".equalsIgnoreCase(pSiteId)) {
						if (caFlag != null && "e".equalsIgnoreCase(caFlag.trim())) {
								if ((afs + altAFS + igr) >= reqQty) {
									availabilityStatus = BBBInventoryManager.AVAILABLE;
								} else {
									availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
								}
							} else {
								if ((altAFS + igr) >= reqQty) {
									availabilityStatus = BBBInventoryManager.AVAILABLE;
								} else {
									availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
								}
							}
						} else {
							if ((afs + altAFS + igr) >= reqQty) {
								availabilityStatus = BBBInventoryManager.AVAILABLE;
							} else {
								availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
							}
						}
					} else {
						// addFromWishList, Product Detail & Store to Online
						if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(pSiteId)) {
							if (caFlag != null && "e".equalsIgnoreCase(caFlag.trim())) {
								if ((afs + altAFS) >= reqQty) {
									availabilityStatus = BBBInventoryManager.AVAILABLE;
								} else {
									availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
								}
							} else {
								if (altAFS >= reqQty) {
									availabilityStatus = BBBInventoryManager.AVAILABLE;
								} else {
									availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
								}
							}
						} else {
							if ((afs + altAFS) >= reqQty) {
								availabilityStatus = BBBInventoryManager.AVAILABLE;
							} else {
								availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
							}
						}
					}
				} else {
					if (RETRIEVE_CART.equals(operation)
							|| ADD_ITEM_FROM_REG.equals(operation)) {
						// Retrieve Cart and Add From Registry

						if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(pSiteId)) {
							if (caFlag != null && "e".equalsIgnoreCase(caFlag.trim())) {
							if ((afs + altAFS + igr) > 0) {
								availabilityStatus = BBBInventoryManager.AVAILABLE;
							} else {
								availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
							}
						} else {
							if ((altAFS + igr) > 0) {
								availabilityStatus = BBBInventoryManager.AVAILABLE;
							} else {
								availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
							}
						}
					} else {
						if ((afs + altAFS + igr) > 0) {
							availabilityStatus = BBBInventoryManager.AVAILABLE;
						} else {
							availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
						}
					}
				} else {
					// addFromWishList, Product Detail & Store to Online
					if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(pSiteId) || "TBS_BedBathCanada".equalsIgnoreCase(pSiteId)) {
						if (caFlag != null && "e".equalsIgnoreCase(caFlag.trim())) {
							if ((afs + altAFS) > 0) {
								availabilityStatus = BBBInventoryManager.AVAILABLE;
							} else {
								availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
							}
						} else {
							if (altAFS > 0) {
								availabilityStatus = BBBInventoryManager.AVAILABLE;
							} else {
								availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
							}
						}
					} else {
						if ((afs + altAFS) > 0) {
							availabilityStatus = BBBInventoryManager.AVAILABLE;
						} else {
							availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
						}
					}
				}
				}
				
			} else {
				throw new InventoryException("InventoryVO from catalog is null");
			}

				logDebug("getNonVDCProductAvailability() : product availability status : "
						+ availabilityStatus);

		} catch (BBBBusinessException e) {
			throw new InventoryException(e);
		} catch (BBBSystemException e) {
			throw new InventoryException(e);
		}
		BBBPerformanceMonitor.end("BBBInventoryManager getNonVDCProductAvailability");
		return availabilityStatus;
	}

	/**
	 * This method return the inventory status for BOPUS item In case of System
	 * Exception if operation is updateCart then return AVAILABLE In case of
	 * System Exception if operation is not updateCart then return NOT AVAILABLE
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param pStoreId
	 * @param pRequestedQuantity
	 * @param operation
	 * 
	 * @return status
	 * @exception InventoryException
	 * 
	 */
	@Override
	public Map<String, Integer> getBOPUSProductAvailability(String pSiteId,
			String pSkuId, List<String> pStoreIds, long pRequestedQuantity,
			String operation, BBBStoreInventoryContainer storeInventoryContainer,
			boolean useCachedInventory, String pRegistryId, boolean pChangeStore ,  boolean isFromLocalStore) throws InventoryException {
	    BBBPerformanceMonitor.start("BBBInventoryManager getBOPUSProductAvailability");
		Map<String, Integer> storeIdInventoryMap = storeInventoryContainer.getStoreIdInventoryMap();
		boolean updateCoherence= false;
		// For left over storeIds if not found in session bean
		List<String> newStoreIdList = new ArrayList<String>();
		
		/*final BopusInventoryService bopusService = (BopusInventoryService) Nucleus
				.getGlobalNucleus().resolveName(
						"/com/bbb/commerce/inventory/BopusInventoryService");*/		

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_SHOPPING_CART);
		BBBOrder order = (BBBOrder) cart.getCurrent();

		Map<String, Integer> inventoryStatusMap = new HashMap<String, Integer>();
		List<CommerceItem> commerceItemList = null;
		try {
			commerceItemList = order.getCommerceItemsByCatalogRefId(pSkuId);
		} catch (CommerceItemNotFoundException exception) {
//			logError("CommerceItemNotFoundException for SKUID : " + pSkuId + exception);
			logDebug("Commerce Item with Catalog Ref Id :: " + pSkuId + " is not already in order ::" + order.getId());
		} catch (InvalidParameterException exception) {
			logError("InvalidParameterException for SKUID :" + pSkuId + exception);
		}
		try {
			ThresholdVO skuThresholdVO = getCatalogTools().getSkuThreshold(pSiteId, pSkuId);

			// use this as the Key to storeInventoryMap
			String inventoryMapKey = null;
			if (useCachedInventory && storeIdInventoryMap != null) {
				for (String storeId : pStoreIds) {
					
					long totalRequestedQuantity = pRequestedQuantity;
					long skuQuantityInOrder = 0L;
					if(BBBInventoryManager.STORE_STORE.equalsIgnoreCase(operation)){
						if (!BBBUtility.isListEmpty(commerceItemList)) {
							for (CommerceItem commerceItem : commerceItemList) {
								if (commerceItem instanceof BBBCommerceItem) {
									final BBBCommerceItem bbbItem = (BBBCommerceItem) commerceItem;
									if (bbbItem.getStoreId() != null && storeId == bbbItem.getStoreId()) {
										skuQuantityInOrder += commerceItem.getQuantity();
									}
								}
							}
						}

						if (!pChangeStore) {
							totalRequestedQuantity += skuQuantityInOrder;
						}
					}

				
					// append store Id and skuid separated by Pipe Symbol
					inventoryMapKey = storeId + BBBCoreConstants.PIPE_SYMBOL + pSkuId;
					if (storeIdInventoryMap.containsKey(inventoryMapKey)) {
						
						Integer invCount = storeIdInventoryMap.get(inventoryMapKey);
						inventoryStatusMap.put(storeId, getInventoryStatus(invCount, totalRequestedQuantity, skuThresholdVO , storeId));
					} else {
						newStoreIdList.add(storeId);
					}
				}
			} else {
				newStoreIdList.addAll(pStoreIds);
			}

			boolean inventoryFound = false;
			Integer inventoryStock = 0;
			if(newStoreIdList.size() > 0){
				Map<String, Integer> inventories = getBopusService().getInventoryForBopusItem(pSkuId,
						newStoreIdList , isFromLocalStore);

				if (!BBBUtility.isMapNullOrEmpty(inventories)) {
					inventoryFound = true;
				}
				
				/*if(inventories.size() != newStoreIdList.size()){
					if(isLoggingError()){
						logError("The number of bopus inventory information is not equal to the supplied store Ids");
					}
					throw new InventoryException("error occourred while getting inventory information for all the stores.");
				}*/
				for (int i = 0; i < newStoreIdList.size(); i++)
				{
					if(inventoryFound && inventories.containsKey(newStoreIdList.get(i))){
						inventoryStock = inventories.get(newStoreIdList.get(i));
					} else {
						inventoryStock = 0;
					}
					long totalReqQuantity = pRequestedQuantity;
					long totalSkuQuantityInOrder = 0;
					// update the storeInventoryMap in session scope
					inventoryMapKey = newStoreIdList.get(i) + BBBCoreConstants.PIPE_SYMBOL + pSkuId;
					storeIdInventoryMap.put(inventoryMapKey, inventoryStock);
					String updateCoherenceCache= BBBConfigRepoUtils.getStringValue(SelfServiceConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.UPDATE_COHERENCE_CACHE);
					updateCoherence =Boolean.parseBoolean(updateCoherenceCache);
					if(updateCoherence){
					getCacheContainer().put(newStoreIdList.get(i) + BBBCoreConstants.HYPHEN + pSkuId, inventoryStock,
		                    BBBCoreConstants.CACHE_STORE_INV);
					}
					
					if(BBBInventoryManager.STORE_STORE.equalsIgnoreCase(operation)){
						if (!BBBUtility.isListEmpty(commerceItemList)) {
							for (CommerceItem commerceItem : commerceItemList) {
								if (commerceItem instanceof BBBCommerceItem) {
									final BBBCommerceItem bbbItem = (BBBCommerceItem) commerceItem;
									if (bbbItem.getStoreId() != null && newStoreIdList.get(i).equalsIgnoreCase(bbbItem.getStoreId())) {
										totalSkuQuantityInOrder += bbbItem.getQuantity();
									}
								}
							}
						}

						if (!pChangeStore) {
							totalReqQuantity += totalSkuQuantityInOrder;
						}

					}

					// Get inventory Status and update the inventoryStatusMap
					inventoryStatusMap.put(newStoreIdList.get(i), getInventoryStatus(inventoryStock, totalReqQuantity, skuThresholdVO , newStoreIdList.get(i)));
				}

				// replace the storeInventoryMap in BBBSessionBean
				storeInventoryContainer.setStoreIdInventoryMap(storeIdInventoryMap);
			}

			return inventoryStatusMap;

		} catch (BBBSystemException e) {

			throw new InventoryException(e);
		} catch (BBBBusinessException e) {

			throw new InventoryException(e);
		} finally {
		    BBBPerformanceMonitor.end("BBBInventoryManager", "getBOPUSProductAvailability");
		}

	}

	public int getInventoryStatus(int inventory, long pReqQty,
			ThresholdVO skuThresholdVO , String storeId) {
		BBBPerformanceMonitor.start("BBBInventoryManager getInventoryStatus");
		int inventoryStatus = BBBInventoryManager.AVAILABLE;
		
		if (skuThresholdVO != null) {
			/*if(inventory>=skuThresholdVO.getThresholdAvailable())
			{
				inventoryStatus = BBBInventoryManager.AVAILABLE;
				if(inventory-pReqQty<skuThresholdVO.getThresholdAvailable())
				{
					inventoryStatus = BBBInventoryManager.LIMITED_STOCK;
				}				
			}
			else if(inventory>=skuThresholdVO.getThresholdLimited())
			{
				inventoryStatus = BBBInventoryManager.LIMITED_STOCK;
			}
			else
			{
				inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
			}*/
			
			if (inventory - pReqQty >= skuThresholdVO.getThresholdAvailable()) {
				inventoryStatus = BBBInventoryManager.AVAILABLE;
				if(inventory-pReqQty<skuThresholdVO.getThresholdAvailable())
				{
					inventoryStatus = BBBInventoryManager.LIMITED_STOCK;
				}
				//setting call store availability in case dummy store is present
				inventoryStatus = getInventorystatusForDummyStore(storeId);
				if(inventoryStatus == BBBInventoryManager.DUMMY_STOCK)
				{
					return inventoryStatus;
				}
			} else if (inventory - pReqQty >= skuThresholdVO
					.getThresholdLimited()) {
				//setting call store availability in case dummy store is present
				inventoryStatus = getInventorystatusForDummyStore(storeId);
				if(inventoryStatus == BBBInventoryManager.DUMMY_STOCK)
				{
					return inventoryStatus;
				}
				inventoryStatus = BBBInventoryManager.LIMITED_STOCK;
			} else {
				inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
			}
		} else {

				logDebug("skuThresholdVO from catalog in null");
			if (inventory - pReqQty > 0) {
				inventoryStatus = BBBInventoryManager.AVAILABLE;
			} else {
				inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
			}

		}

		BBBPerformanceMonitor.end("BBBInventoryManager getInventoryStatus");
		return inventoryStatus;
	}

	/**
	 * @param pSiteId
	 * @param pSkuId
	 * @param operation
	 * @param throwExc
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public int getProductAvailability(String pSiteId, String pSkuId,
			String operation, boolean throwExc) throws BBBBusinessException, BBBSystemException {
		BBBPerformanceMonitor.start("BBBInventoryManager getProductAvailability");

			logDebug("BBBInventoryManager : getProductAvailability() starts");
			logDebug("Input parameters : pSiteId " + pSiteId + " ,pSkuId "
					+ pSkuId + " & operation " + operation);
		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		SKUDetailVO skuDetailVO = null;

		try {
	
				logDebug("Calling CatalogTools getSKUDetails() method : Input Parameters are pSiteId - "
						+ pSiteId
						+ " ,pSkuId - "
						+ pSkuId
						+ " & calculateAboveBelowLine - " + false);

			skuDetailVO = getCatalogTools().getSKUDetails(pSiteId, pSkuId,
					false, throwExc);


				logDebug("response skuDetailVO from CatalogTools getSKUDetails() method :"
						+ skuDetailVO);

			String caFlag = skuDetailVO.getEcomFulfillment();

			if (skuDetailVO.isVdcSku()) {
				availabilityStatus = getVDCProductAvailability(pSiteId, pSkuId,
						0, BBBCoreConstants.CACHE_ENABLED);
			} else {
				availabilityStatus = getNonVDCProductAvailability(pSiteId,
						pSkuId, caFlag, operation, BBBCoreConstants.CACHE_ENABLED, 0);
			}
		} catch (InventoryException e) {
			logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1021 +
					": BBBInventoryManager : getProductAvailability() Inventory Exception occoured",
					e);
			availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
		}

			logDebug("BBBInventoryManager : getProductAvailability() ends.");
			logDebug("Output - availabilityStatus " + availabilityStatus);

		BBBPerformanceMonitor.end("BBBInventoryManager getProductAvailability");
		return availabilityStatus;
	}

	// Start || BBBSL-3018 || PS - Oct
	
	/**
	 * This method is used to invalidate the item cache of cachedInventory and cachedInventoryTranslation for the passed skuId.
	 * The cached is cleared irrespective of site.
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 */
	public void invalidateItemInventoryCache(String pSiteId, String pSkuId) {
		BBBPerformanceMonitor.start("BBBInventoryManager invalidateItemInventoryCache");
	
				logDebug("invalidateItemInventoryCache() : starts");
				StringBuilder sb = new StringBuilder();
				sb.append("Input Parametrs: skuID - ");
				sb.append(pSkuId);
				sb.append(" , siteId - ");
				sb.append(pSiteId);
				logDebug(sb.toString());

			try {
				getOnlineInventoryManager().invalidateItemInventoryCache(pSkuId, pSiteId);
			} catch (BBBBusinessException e) {
				logError("BBBInventoryManager : invalidateItemInventoryCache() Business Exception occoured",e);
			} catch (BBBSystemException e) {
				logError("BBBInventoryManager : invalidateItemInventoryCache() System Exception occoured",e);
			}
						
				logDebug("invalidateItemInventoryCache() : starts");
			
			BBBPerformanceMonitor.end("BBBInventoryManager invalidateItemInventoryCache");
	}
	
	/**
	 * Wrapper method for Rest for Uncached Inventory Check at click of checkout.
	 *     
	 */
	public boolean uncachedInventoryCheck() {
		final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		final OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_SHOPPING_CART);
        final BBBOrder order = (BBBOrderImpl) cart.getCurrent();
        return checkUncachedInventory(order);
	}
	
	public Map<String, String> getWebProductInventoryStatus(List<String> pSkuId)throws BBBSystemException{
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		String siteId = request.getHeader(this.SITE_HEADER);
		Map<String,String> productinventoryStatus = new HashMap<String,String>();
		if(BBBUtility.isListEmpty(pSkuId)){
			logDebug("Input list is empty...");
			return productinventoryStatus;
		}
		for(String skuid: pSkuId){
			if (BBBUtility.isNotEmpty(skuid)) { 
				 logDebug("Getting inventory status for skuid..." + skuid.trim());
				 try{		
					 if(getProductAvailability(siteId, skuid.trim(),PRODUCT_DISPLAY, 0) == 0){
								 productinventoryStatus.put(skuid,TRUE);
							 }
					 else if(getProductAvailability(siteId, skuid.trim(),PRODUCT_DISPLAY, 0) == 1){
						 productinventoryStatus.put(skuid,FALSE);
					 }
				 }
				 catch(BBBBusinessException e){					 
						 if(e.getErrorCode().equals(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY)){
							 productinventoryStatus.put(skuid,BBBCoreConstants.INVALId_SKU);
						 }
						 if(e.getErrorCode().equals(BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY)){
							 productinventoryStatus.put(skuid,FALSE);
						 }
				 }
				 catch(BBBSystemException e){					 
					 	productinventoryStatus.put(skuid,BBBCoreConstants.INVALId_SKU);
				
			 }
				}
			}
		
		return productinventoryStatus;
	}

	/**
	 * This method is used in handleCheckout to check for the inventory of all
	 * items in order, when clicking Checkout button on cart page.
	 * 
	 * @param order
	 */
	public boolean checkUncachedInventory(BBBOrder order) {
		final long startTime = System.currentTimeMillis();
		this.logDebug("START: Verifying inventory stock for All items");
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.PRE_CART_CHECKOUT_PROCESS,
				CHECK_INVENTORY);
		boolean inventoryOOS = false;
		
		@SuppressWarnings("unchecked")
		final List<CommerceItem> commerceItems = order.getCommerceItems();
		BBBCommerceItem bbbItem = null;
		for (final CommerceItem item : commerceItems) {
			if (item instanceof BBBCommerceItem) {
				bbbItem = (BBBCommerceItem) item;
				// Skip BOPUS items.
				int inventoryStatus = BBBInventoryManager.AVAILABLE;
				String ecomFullfillment = null;

				if (bbbItem.getStoreId() == null) {
					if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(order
							.getSiteId()) || TBSConstants.SITE_TBS_BAB_CA.equalsIgnoreCase(order.getSiteId())) {
						try {
							ecomFullfillment = getCatalogTools().getSkuEComFulfillment(
											bbbItem.getCatalogRefId());
						} catch (BBBBusinessException e) {
							logError("BBBInventoryManager : checkUncachedInventory() Business Exception occured",e);
							inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
						} catch (BBBSystemException e) {
							logError("BBBInventoryManager : checkUncachedInventory() System Exception occured",e);
							inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
						}
					}

					try {
						if (bbbItem.isVdcInd()) {
							inventoryStatus = this.getVDCProductAvailability(
											order.getSiteId(),
											bbbItem.getCatalogRefId(),
											bbbItem.getQuantity(),
											BBBCoreConstants.CACHE_DISABLED);
						} else {
							inventoryStatus = this.getNonVDCProductAvailability(
											order.getSiteId(),
											bbbItem.getCatalogRefId(),
											ecomFullfillment,
											BBBInventoryManager.RETRIEVE_CART,
											BBBCoreConstants.CACHE_DISABLED, bbbItem.getQuantity());
						}

					} catch (final InventoryException e) {
						logError("BBBInventoryManager : checkUncachedInventory() Inventory Exception occured",e);
						inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
					}

					if (inventoryStatus == BBBInventoryManager.NOT_AVAILABLE) {
						logDebug("BBBInventoryManager() : checkUncachedInventory ["+bbbItem.getCatalogRefId()+"] is Out of Stock. Clearing Cached Entry.");
						this.invalidateItemInventoryCache(order.getSiteId(),bbbItem.getCatalogRefId());
						inventoryOOS = true;
					}

					if (order.getAvailabilityMap() != null) {
						order.getAvailabilityMap().put(bbbItem.getId(),Integer.valueOf(inventoryStatus));
					}
				}
			}
		}
		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.PRE_CART_CHECKOUT_PROCESS, CHECK_INVENTORY);
		this.logDebug("END: Verifying inventory stock for All items and took time: "
				+ (System.currentTimeMillis() - startTime));
		
		return inventoryOOS;
	}
	
	/**This method checks if any dummy store id is there in map
	 * @param storeId
	 * @return
	 */
	public int getInventorystatusForDummyStore(String storeId)
	{
		int inventoryStatus = 0;
		Map<String,String> dummyStoreIdsMap = (Map<String, String>) ServletUtil.getCurrentRequest().getObjectParameter(SelfServiceConstants.DUMMY_STORE_MAP);
		if(dummyStoreIdsMap != null)
		{
			for (Map.Entry<String,String> entry : dummyStoreIdsMap.entrySet()) {
				if(entry.getKey().equals(storeId))
				{
					inventoryStatus = BBBInventoryManager.DUMMY_STOCK;
				}
			}
		}
		return inventoryStatus;
	}
	// End || BBBSL-3018 || PS - Oct
	
	
	// This Method is Required for TBS and is Unimplemented for BBBInventory
	public int getATGInventoryForTBS(String pSiteId, String pSkuId, String operation, long qty)
			throws BBBBusinessException, BBBSystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.bbb.commerce.inventory.BBBInventoryManager#getProductAvailability(java.lang.String, java.lang.String, java.lang.String, long, com.bbb.commerce.catalog.vo.SKUDetailVO)
	 *Overridden to remove extra sku detail call.
	 */
	@Override
	public int getProductAvailability(String pSiteId, String pSkuId,
			String operation, long qty, SKUDetailVO skuDetailVO)
			throws BBBBusinessException, BBBSystemException {
		BBBPerformanceMonitor
				.start("BBBInventoryManager getProductAvailability");

		logDebug("BBBInventoryManager : getProductAvailability() starts Input parameters : pSiteId " + pSiteId + " ,pSkuId " + pSkuId
				+ " & operation " + operation);
		
		if(skuDetailVO==null){
			return BBBInventoryManager.NOT_AVAILABLE;
		}
		int availabilityStatus = BBBInventoryManager.AVAILABLE;

		try {

			logDebug("Calling CatalogTools getSKUDetails() method : Input Parameters are pSiteId - "
					+ pSiteId
					+ " ,pSkuId - "
					+ pSkuId
					+ " & calculateAboveBelowLine - " + false);

			String caFlag = skuDetailVO.getEcomFulfillment();

			if (skuDetailVO.isVdcSku()) {
				availabilityStatus = getVDCProductAvailability(pSiteId, pSkuId,
						0, BBBCoreConstants.CACHE_ENABLED);
			} else {
				availabilityStatus = getNonVDCProductAvailability(pSiteId,
						pSkuId, caFlag, operation,
						BBBCoreConstants.CACHE_ENABLED, 0);
			}
		} catch (InventoryException e) {
			logError(
					BBBCoreErrorConstants.CHECKOUT_ERROR_1021
							+ ": BBBInventoryManager : getProductAvailability() Inventory Exception occoured",
					e);
			availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
		}

		logDebug("BBBInventoryManager : getProductAvailability() ends.");
		logDebug("Output - availabilityStatus " + availabilityStatus);

		BBBPerformanceMonitor.end("BBBInventoryManager getProductAvailability");
		return availabilityStatus;
	}
	

	

}