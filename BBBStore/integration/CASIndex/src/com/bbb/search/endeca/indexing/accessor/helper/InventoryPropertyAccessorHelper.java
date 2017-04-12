package com.bbb.search.endeca.indexing.accessor.helper;

import java.util.List;

import atg.commerce.inventory.InventoryException;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.OnlineInventoryManager;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class InventoryPropertyAccessorHelper extends GenericService {

	BBBCatalogTools catalogTools;
	MutableRepository catalogRepository;
	private OnlineInventoryManager mOnlineInventoryManager;
	private BBBInventoryManager inventoryManager;

	public int getInventoryProperty(final RepositoryItem pItem,
			final String siteId) {

		return getInventory(pItem, siteId);
	}

	@SuppressWarnings("unchecked")
	private int getInventory(RepositoryItem pItem, String siteId) {
		// String siteId =
		// (String)pContext.getAttribute("atg.siteVariantProdcer.siteValue");
		if (pItem != null){
		boolean iscolection = (boolean) pItem
				.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME);
		if (iscolection) {
			try {
				if (getCatalogTools().isProductActive(pItem, siteId)) {
					
					List<RepositoryItem> productChildProducts = (List<RepositoryItem>) pItem
							.getPropertyValue(BBBCatalogConstants.PRODUCT_CHILD_PRODUCTS);
					
					List<RepositoryItem> childSKUS = null;

					// logDebug("childProducts Number******** "+productChildProducts.size());

					if (productChildProducts != null
							&& !(productChildProducts.isEmpty())) {
						for (RepositoryItem productChildProduct : productChildProducts) {

							RepositoryItem product = (RepositoryItem) productChildProduct
									.getPropertyValue(BBBCatalogConstants.PRODUCT_ID);
							if (product == null) {
								continue;
							}

							// logDebug("product ID******** "+product.getRepositoryId());
							childSKUS = (List<RepositoryItem>) product
									.getPropertyValue(BBBCatalogConstants.CHILD_SKUS);
							if (childSKUS == null || childSKUS.isEmpty()) {
								continue;
							}
							for (RepositoryItem sku : childSKUS) {
								String skuId = sku.getRepositoryId();
								int inStockStatus = getAccessorProductAvailability(
										siteId, skuId,
										BBBInventoryManager.PRODUCT_DISPLAY, 0);
								return inStockStatus;
							}

						}
					} else if (!(childSKUS = (List<RepositoryItem>) pItem
								.getPropertyValue(BBBCatalogConstants.CHILD_SKUS)).isEmpty()){
							for (RepositoryItem sku : childSKUS) {
								String skuId = sku.getRepositoryId();
								int inStockStatus = getAccessorProductAvailability(
										siteId, skuId,
										BBBInventoryManager.PRODUCT_DISPLAY, 0);
								return inStockStatus;
						}
					} else {
						return 0;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		return 0;
		
	}

	private int getAccessorProductAvailability(String pSiteId, String pSkuId,
			String operation, long qty) {
		BBBPerformanceMonitor
				.start("BBBInventoryManager getProductAvailability");
		/*
		 * logDebug("BBBInventoryManager : getProductAvailability() starts");
		 * logDebug("Input parameters : pSiteId " + pSiteId + " ,pSkuId " +
		 * pSkuId + " & operation " + operation);
		 */

		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		SKUDetailVO skuDetailVO = null;
		int finalQty = 0;

		try {

			/*
			 * logDebug(
			 * "Calling CatalogTools getSKUDetails() method : Input Parameters are pSiteId - "
			 * + pSiteId + " ,pSkuId - " + pSkuId +
			 * " & calculateAboveBelowLine - " + false);
			 */
			skuDetailVO = getCatalogTools().getSKUDetails(pSiteId, pSkuId,
					false);

			/*
			 * logDebug(
			 * "response skuDetailVO from CatalogTools getSKUDetails() method :"
			 * + skuDetailVO);
			 */

			String caFlag = skuDetailVO.getEcomFulfillment();

			if (skuDetailVO.isVdcSku()) {
				availabilityStatus = getInventoryManager()
						.getVDCProductAvailability(pSiteId, pSkuId, 0,
								BBBCoreConstants.CACHE_ENABLED);
			} else {
				availabilityStatus = getInventoryManager()
						.getNonVDCProductAvailability(pSiteId, pSkuId, caFlag,
								operation, BBBCoreConstants.CACHE_ENABLED, qty);
			}
		} catch (InventoryException e) {
			vlogDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1021
					+ ": BBBInventoryManagerImpl : getProductAvailability() Exception occurred in getting Inventory information  for sku "
					+ pSkuId);
			availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
		} catch (BBBSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BBBBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * logDebug("BBBInventoryManager : getProductAvailability() ends.");
		 * logDebug("Output - availabilityStatus " + availabilityStatus);
		 */

		BBBPerformanceMonitor.end("BBBInventoryManager getProductAvailability");
		if (availabilityStatus == BBBInventoryManager.AVAILABLE) {
			InventoryVO inventoryVO = null;
			try {
				inventoryVO = getOnlineInventoryManager().getInventory(pSkuId,
						pSiteId);
			} catch (BBBSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BBBBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (inventoryVO != null) {
				long afs = inventoryVO.getGlobalStockLevel();
				long altAFS = inventoryVO.getSiteStockLevel();
				long igr = inventoryVO.getGiftRegistryStockLevel();
				finalQty = (int) (afs + altAFS + igr);
			}
		}
		return finalQty;
	}

	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.catalogTools = pCatalogTools;
	}

	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public OnlineInventoryManager getOnlineInventoryManager() {
		return mOnlineInventoryManager;
	}

	public void setOnlineInventoryManager(
			OnlineInventoryManager onlineInventoryManager) {
		this.mOnlineInventoryManager = onlineInventoryManager;
	}

	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}
}
