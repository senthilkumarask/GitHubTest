package com.bbb.commerce.inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.commerce.inventory.RepositoryInventoryManager;
import atg.repository.ItemDescriptorImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.vo.InventoryFeedVO;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;


public class OnlineInventoryManagerImpl extends RepositoryInventoryManager implements OnlineInventoryManager {

	/**
	 * Inventory tools instance
	 */
	private InventoryTools inventoryTools;
	private String queryString;
	private BBBCatalogTools mCatalogTools;
	private BBBInventoryManager inventoryManager;
	

	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	/**
	 * Default constructor
	 */
	public OnlineInventoryManagerImpl() {
		// default constructor
	}

	/**
	 * @return the inventoryTools
	 */
	public InventoryTools getInventoryTools() {
		return inventoryTools;
	}

	/**
	 * @param pInventoryTools
	 *            the inventoryTools to set
	 */
	public void setInventoryTools(InventoryTools pInventoryTools) {
		inventoryTools = pInventoryTools;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString
	 *            the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * Given unCached inventory details (sku, site, [store]), returns the inventory
	 * details
	 * 
	 * @param pInventoryVO
	 * @return Inventory details
	 */
	@Override
	public InventoryVO getInventory(String skuId, String siteId) throws BBBSystemException, BBBBusinessException {
		if (isLoggingDebug()) {
			logDebug("Catalog API Method Name [getInventory]");
			logDebug("Parameter skuId[" + skuId + "]");
		}
		if (BBBUtility.isNotBlank(skuId)) {
			try {
				RepositoryItem inventoryrepositoryItem[] = null;
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getInventory_1");
				Object[] params = new Object[1];
				params[0] = skuId;
				inventoryrepositoryItem = this.executeRQLQuery(getQueryString(), params, BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR, getInventoryTools().getInventoryRepository());
				if (inventoryrepositoryItem != null && inventoryrepositoryItem.length > 0) {

					return getInventoryVO(inventoryrepositoryItem[0]);
				} else {
					throw new BBBBusinessException(BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID,BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID);
				}

			} catch (RepositoryException e) {

				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			} finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getInventory_1");
			}

		} else {
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}

	}
	
	
	
	/**
	 * Given cached inventory details (sku, site, [store]), returns the inventory
	 * details
	 * 
	 * @param pInventoryVO
	 * @return Inventory details
	 */
	@Override
	public InventoryVO getCachedInventory(String skuId, String siteId) throws BBBSystemException, BBBBusinessException {
		if (isLoggingDebug()) {
			logDebug("Catalog API Method Name [getCachedInventory]");
			logDebug("Parameter skuId[" + skuId + "]");
		}
		if (BBBUtility.isNotBlank(skuId)) {
			try {
				RepositoryItem inventoryrepositoryItem[] = null;
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getCachedInventory_1");
				Object[] params = new Object[1];
				params[0] = skuId;
				inventoryrepositoryItem = this.executeRQLQuery(getQueryString(), params, BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR, getInventoryTools().getInventoryRepository());
				if (inventoryrepositoryItem != null && inventoryrepositoryItem.length > 0) {

					return getInventoryVO(inventoryrepositoryItem[0]);
				} else {
					throw new BBBBusinessException(BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID,BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID);
				}

			} catch (RepositoryException e) {
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			} finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getCachedInventory_1");
			}
		} else {
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}

	}

	/**
	 * Given a list of inventory details (sku, site, [store]), returns a map of
	 * Inventory VOs with sku id as the key
	 * 
	 * @param pInventoryVO
	 * @return Map of Inventory VOs with sku id as the key
	 */
	@Override
	public Map<String, InventoryVO> getInventory(String[] skuId, String siteId) throws BBBSystemException, BBBBusinessException {
		if (isLoggingDebug()) {
			logDebug("Catalog API Method Name [getInventory]");

		}
		if (skuId != null && skuId.length > 0) {
			Map<String, InventoryVO> skuIdInventoryVOMap = new HashMap<String, InventoryVO>();
			for (int i = 0; i < skuId.length; i++) {
				try {
					RepositoryItem inventoryrepositoryItem[] = null;
					BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getInventory_2");
					Object[] params = new Object[1];
					params[0] = skuId[i];
					inventoryrepositoryItem = this.executeRQLQuery(getQueryString(), params, BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR, getInventoryTools().getInventoryRepository());
					if (inventoryrepositoryItem != null && inventoryrepositoryItem.length > 0) {
						skuIdInventoryVOMap.put(skuId[i], getInventoryVO(inventoryrepositoryItem[0]));
					} else {
						throw new BBBBusinessException(BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID,BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID);
					}

				} catch (RepositoryException e) {

					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				} finally {
					BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getInventory_2");
				}
			}
			return skuIdInventoryVOMap;
		} else {
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

	/**
	 * Returns the feed updates to inventory
	 * 
	 * @return List of InventoryVO inventory feed updates
	 */
	@Override
	public List<InventoryFeedVO> getInventoryFeedUpdates(String status) throws BBBSystemException {
		return getInventoryTools().getInventoryFeedUpdates(status);
	}
	
	/**
	 * Invokes invalidate cache for Inventory Repository.
	 * 
	 */
	@Override
	public void invalidateInventoryCache() throws BBBSystemException {
		getInventoryTools().invalidateInventoryCache();
	}

	/**
	 * Updates the inventory feed with the provided values
	 * 
	 * @param pInventoryFeed
	 */
	@Override
	public void updateInventoryFeed(List<InventoryFeedVO> pInventoryFeeds) throws BBBSystemException {
		getInventoryTools().updateInventoryFeed(pInventoryFeeds);
	}

	/**
	 * Given inventory detail, removes the inventory item from cache
	 * 
	 * @param pInventoryFeed
	 */
	@Override
	public void invalidateInventoryCache(InventoryFeedVO pInventoryFeed) throws BBBSystemException {
		getInventoryTools().removeItemFromCache(pInventoryFeed);
	}

	@Override
	public Long getAltAfs(String skuId, String siteId) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("Catalog API Method Name [getAltAfs]");
			logDebug("Parameter skuId[" + skuId + "]");
		}
		long startTime = System.currentTimeMillis();
		if (skuId != null) {
			try {
				RepositoryItem inventoryrepositoryItem[] = null;
				Long altAfs = Long.valueOf(0);
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getAltAfs");
				Object[] params = new Object[1];
				params[0] = skuId;
				inventoryrepositoryItem = this.executeRQLQuery(getQueryString(), params, BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR, getInventoryTools().getInventoryRepository());
				if (inventoryrepositoryItem != null && inventoryrepositoryItem.length > 0
						&& inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) != null) {

					altAfs = (Long) inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME);

				}
				return altAfs;

			} catch (RepositoryException e) {

				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			} finally {
				if (isLoggingDebug()) {
					logDebug("Total Time taken by OnlineInventoryManager.getAltAfs() is:" + (System.currentTimeMillis() - startTime) + " for skuId:" + skuId);
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getAltAfs");
			}

		} else {
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

	@Override
	public String getMaxStockSku(List<String> skuId, String siteId) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("Catalog API Method Name [getAltAfs]");
			logDebug("Parameter skuId[" + skuId + "]");
		}
		long startTime = System.currentTimeMillis();
		String maxInventorySkuId = "";
		if (skuId != null && skuId.size() > 0) {
			List<InventoryVO> inventoryList = new ArrayList<InventoryVO>();
			try {
				for (int i = 0; i < skuId.size(); i++) {
					RepositoryItem inventoryrepositoryItem[] = null;
					InventoryVO inventoryVO = new InventoryVO();

					BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getAltAfs");
					Object[] params = new Object[1];
					params[0] = skuId.get(i);
					inventoryrepositoryItem = this.executeRQLQuery(getQueryString(), params, BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR, getInventoryTools().getInventoryRepository());
					if (inventoryrepositoryItem != null && inventoryrepositoryItem.length > 0
							&& inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) != null) {
						if (isLoggingDebug()) {
							logDebug("site stock for sku " + skuId.get(i) + " is "
									+ inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME));
						}
						inventoryVO.setSiteStockLevel((Long) inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME));
						inventoryVO.setSkuID(skuId.get(i));
						inventoryList.add(inventoryVO);
					}
				}
				if (!BBBUtility.isListEmpty(inventoryList)) {
					Collections.sort(inventoryList, Collections.reverseOrder());
					maxInventorySkuId = inventoryList.get(0).getSkuID();
				}
				return maxInventorySkuId;
			} catch (RepositoryException e) {

				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			} finally {
				if (isLoggingDebug()) {
					logDebug("Total Time taken by OnlineInventoryManager.getMaxStockSku() is:" + (System.currentTimeMillis() - startTime) + " for skuIds:" + skuId);
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getAltAfs");
			}

		} else {
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

	@Override
	public Long getAfs(String skuId, String siteId) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("Catalog API Method Name [getAfs]");
			logDebug("Parameter skuId[" + skuId + "]");
		}
		long startTime = System.currentTimeMillis();
		if (skuId != null) {
			try {
				RepositoryItem inventoryrepositoryItem[] = null;
				Long afs = Long.valueOf(0);
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getAfs");
				Object[] params = new Object[1];
				params[0] = skuId;
				inventoryrepositoryItem = this.executeRQLQuery(getQueryString(), params, BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR, getInventoryTools().getInventoryRepository());
				if (inventoryrepositoryItem != null && inventoryrepositoryItem.length > 0
						&& inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) != null) {
					afs = (Long) inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME);

				}
				return afs;

			} catch (RepositoryException e) {

				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			} finally {
				if (isLoggingDebug()) {
					logDebug("Total Time taken by OnlineInventoryManager.getAfs() is:" + (System.currentTimeMillis() - startTime) + " for skuId:" + skuId);
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getAfs");
			}

		} else {
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

	@Override
	public Long getIgr(String skuId, String siteId) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("Catalog API Method Name [getIgr]");
			logDebug("Parameter skuId[" + skuId + "]");
		}
		long startTime = System.currentTimeMillis();
		if (skuId != null) {
			try {
				RepositoryItem inventoryrepositoryItem[] = null;
				Long igr = Long.valueOf(0);
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getIgr");
				Object[] params = new Object[1];
				params[0] = skuId;
				inventoryrepositoryItem = this.executeRQLQuery(getQueryString(), params, BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR, getInventoryTools().getInventoryRepository());
				if (inventoryrepositoryItem != null && inventoryrepositoryItem.length > 0
						&& inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) != null) {
					igr = (Long) inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME);
				}
				return igr;

			} catch (RepositoryException e) {

				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			} finally {
				if (isLoggingDebug()) {
					logDebug("Total Time taken by OnlineInventoryManager.getIgr() is:" + (System.currentTimeMillis() - startTime) + " for skuId:" + skuId);
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getIgr");
			}

		} else {
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

	private InventoryVO getInventoryVO(RepositoryItem inventoryrepositoryItem) {
		InventoryVO inventoryVO = new InventoryVO();
		if (inventoryrepositoryItem != null) {
			inventoryVO.setInventoryID(inventoryrepositoryItem.getRepositoryId());
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setSiteStockLevel((Long) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setGiftRegistryStockLevel((Long) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setGlobalStockLevel((Long) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setStartDate((Timestamp) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setEndDate((Timestamp) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.CREATION_DATE_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setCreationDate((Timestamp) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.CREATION_DATE_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.AVAILABILITY_DATE_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setAvailabilityDate((Timestamp) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.AVAILABILITY_DATE_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setDisplayName((String) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setDescription((String) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_INVENTORY_PROPERTY_NAME));
			}
			if (inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME) != null) {
				inventoryVO.setSkuID((String) inventoryrepositoryItem.getPropertyValue(BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME));
			}
		}
		return inventoryVO;
	}

	public RepositoryItem[] executeRQLQuery(String rqlQuery, Object[] params, String viewName, Repository repository) throws RepositoryException, BBBSystemException, BBBBusinessException {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		if (rqlQuery != null) {
			if (repository != null) {
				try {
					long startTime = System.currentTimeMillis();
					statement = getInventoryRqlStatement(rqlQuery);
					RepositoryView view = repository.getView(viewName);
					if (view == null && isLoggingError()) {
						logError("View " + viewName + " is null");
					}

					queryResult = statement.executeQuery(view, params);
					if (isLoggingDebug() && queryResult == null) {

						logDebug("No results returned for query [" + rqlQuery + "]");

					}
					if (isLoggingDebug()) {
						logDebug("Total Time taken by OnlineInventoryManager.executeRQLQuery() is:" + (System.currentTimeMillis() - startTime) + " for rqlQuery:" + rqlQuery);
					}
				} catch (RepositoryException e) {

					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				}
			} else {
				if (isLoggingError()) {
					logError("Repository has no data");
				}
			}
		} else {
			if (isLoggingError()) {
				logError("Query String is null");
			}
		}

		return queryResult;
	}

	protected RqlStatement getInventoryRqlStatement(String rqlQuery) throws RepositoryException {
		return RqlStatement.parseRqlStatement(rqlQuery);
	}

	/*
	 * The method decrements the inventory stock by the requested quantity based
	 * on rules. If the inventory is sufficient, the inventory stock gets
	 * updated
	 * 
	 * @see
	 * com.bbb.commerce.inventory.InventoryManager#decrementInventoryStock(com
	 * .bbb.commerce.inventory.vo.InventoryVO[])
	 */
	@Override
	public void decrementInventoryStock(final InventoryVO[] pInventoryVOs) throws BBBSystemException {
		/* Mark VDC & OOS only inventory decrement requests as FALSE by default */
		String updateAllInventory = BBBCoreConstants.FALSE;
		List<String> config = null;
		try {
			config = getCatalogTools().getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG);
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Error while retrieving configure keys value for [" + BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG + "]", e);
			}
		}
		if (config != null && config.size() > 0) {
			updateAllInventory = config.get(0);
		}
		Long globalStock = Long.valueOf(0);
		Long siteStock = Long.valueOf(0);
		Long registryStock = Long.valueOf(0);
		InventoryVO inventoryVO = null;
		List<InventoryVO> updatedInventoryVOs = new ArrayList<InventoryVO>();
		List<InventoryVO> inventoryVOs;
		if (pInventoryVOs != null && pInventoryVOs.length != 0) {
			try {
				inventoryVOs = inventoryTools.getSKUInventory(pInventoryVOs);
				if (isLoggingDebug()) {
					logDebug("Decrementing the Inventory Levels");
				}
				for (int i = 0; i < inventoryVOs.size(); i++) {
					inventoryVO = inventoryVOs.get(i);

					SKUDetailVO skuDetailVO = (SKUDetailVO) getCatalogTools().getSKUDetails(inventoryVO.getSiteID(), inventoryVO.getSkuID(), true, true, true);
					globalStock = inventoryVO.getGlobalStockLevel();
					siteStock = inventoryVO.getSiteStockLevel();
					registryStock = inventoryVO.getGiftRegistryStockLevel();

					Long ordered_quantity = inventoryVO.getOrderedQuantity();
					if (isLoggingDebug()) {
						logDebug("Stock Levels for Sku :--->" + inventoryVO.getSkuID());
						logDebug("Stock Levels for Site :--->" + inventoryVO.getSiteID());
						logDebug("Global Stock Level(AFS)=" + inventoryVO.getGlobalStockLevel() + "|| Site Specific Stock Level(ALT_AFS)=" + inventoryVO.getSiteStockLevel()
								+ "|| Gift Registry Stock Level(IGR)=" + inventoryVO.getGiftRegistryStockLevel());
						logDebug("Ordered Quantity:--->" + inventoryVO.getOrderedQuantity());
					}
					if (inventoryVO.getGlobalStockLevel() != 0 || inventoryVO.getGiftRegistryStockLevel() != 0 || inventoryVO.getSiteStockLevel() != 0) {
						List<String> listOfSiteCodes = (List<String>) getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathCanadaSiteCode");
						String siteId = null,tbsCanadaSiteId = null;
						if (listOfSiteCodes != null && listOfSiteCodes.size() > 0) {
							siteId = listOfSiteCodes.get(0);
						}
						 listOfSiteCodes = (List<String>) mCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.TBS_BED_BATH_CANADA_SITE_CODE);
						if (listOfSiteCodes != null && listOfSiteCodes.size() > 0) {
							tbsCanadaSiteId = listOfSiteCodes.get(0);
						}
						if (inventoryVO.getSiteID() != null && siteId != null && tbsCanadaSiteId !=null && (inventoryVO.getSiteID().equals(siteId) || inventoryVO.getSiteID().equals(tbsCanadaSiteId)) && isEComFulfillmentIsE(skuDetailVO)) {
							if(inventoryVO.getSiteStockLevel() > 0 || inventoryVO.getGiftRegistryStockLevel() > 0){
								if (siteStock >= ordered_quantity) {
									siteStock = siteStock - ordered_quantity;
								} else {
									registryStock = (registryStock - (ordered_quantity - siteStock));
									siteStock = Long.parseLong("0");
									if (registryStock < 0) {
										registryStock = Long.parseLong("0");
									}
								}
							}
						} else if (globalStock >= ordered_quantity) {
							globalStock = globalStock - ordered_quantity;
						} else if ((globalStock + siteStock) >= ordered_quantity) {
							siteStock = siteStock - (ordered_quantity - globalStock);
							globalStock = Long.parseLong("0");
						} else if ((globalStock + registryStock + siteStock) >= ordered_quantity) {
							registryStock = registryStock - (ordered_quantity - globalStock - siteStock);
							globalStock = siteStock = Long.parseLong("0");
						} else {
							if ((globalStock + registryStock + siteStock) > 0) {
								registryStock = registryStock - (ordered_quantity - globalStock - siteStock);

								if (registryStock < 0) {
									registryStock = Long.parseLong("0");
								}
								globalStock = siteStock = Long.parseLong("0");
							} else {
								globalStock = siteStock = registryStock = Long.parseLong("0");
							}
						}
						/* Check if the SKU was marked out-of-stock */
						boolean outOfStock = (globalStock == 0L || siteStock == 0L || registryStock == 0L) ? true : false;
						/*
						 * If flag is true, then do inventory decrement only in
						 * case it is VDC sku or it is OOS
						 */
						if (BBBCoreConstants.FALSE.equalsIgnoreCase(updateAllInventory) && !(skuDetailVO.isVdcSku() || outOfStock)) {
							logInfo("Stock Levels for Sku :--->" + inventoryVO.getSkuID());
							logInfo("Stock Levels for Site :--->" + inventoryVO.getSiteID());
							logInfo("Global Stock Level(AFS)=" + inventoryVO.getGlobalStockLevel() + "|| Site Specific Stock Level(ALT_AFS)=" + inventoryVO.getSiteStockLevel()
									+ "|| Gift Registry Stock Level(IGR)=" + inventoryVO.getGiftRegistryStockLevel());
							logInfo("Ordered Quantity:--->" + inventoryVO.getOrderedQuantity());
							continue;
						}

						inventoryVO.setGlobalStockLevel(globalStock);
						inventoryVO.setSiteStockLevel(siteStock);
						inventoryVO.setGiftRegistryStockLevel(registryStock);
						updatedInventoryVOs.add(inventoryVO);
					}else{
						logInfo("Stock Levels for Sku :--->" + inventoryVO.getSkuID());
						logInfo("Stock Levels for Site :--->" + inventoryVO.getSiteID());
						logInfo("Global Stock Level(AFS)=" + inventoryVO.getGlobalStockLevel() + "|| Site Specific Stock Level(ALT_AFS)=" + inventoryVO.getSiteStockLevel()
								+ "|| Gift Registry Stock Level(IGR)=" + inventoryVO.getGiftRegistryStockLevel());
						logInfo("Ordered Quantity:--->" + inventoryVO.getOrderedQuantity());
					}
				}
				if (updatedInventoryVOs != null && !updatedInventoryVOs.isEmpty()) {
					if (isLoggingDebug()) {
						logDebug("Before Calling updateSKUInventory(updatedInventoryVOs)");
					}
					inventoryTools.updateSKUInventoryForAllSites(updatedInventoryVOs);
				}
			} catch (BBBSystemException bse) {
				if (isLoggingError()) {
					logError("System Exception ::: " ,bse);
				}
			} catch (BBBBusinessException bbe) {
				if (isLoggingError()) {
					logError("Business Exception ::: " + bbe);
				}
			}
		} else {
			if (isLoggingDebug()) {
				logDebug("List of InventoryVOS are Zero");
			}
		}
	}

	private boolean isEComFulfillmentIsE(final SKUDetailVO skuDetailVO) {
		if (isLoggingDebug()) {
			logDebug("Is EcommerceFullment Flag is E :::::::: " + (skuDetailVO.getEcomFulfillment() == null || !skuDetailVO.getEcomFulfillment().equals("E")));
		}
		return (skuDetailVO.getEcomFulfillment() == null || !skuDetailVO.getEcomFulfillment().equals("E"));
	}

	public void invalidateItemInventoryCache(String pSkuId, String pSiteId) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("Online Inventory Manager [invalidateItemInventoryCache]");
			logDebug("Parameter skuId[" + pSkuId + "]");
		}
		if (pSkuId != null) {
			try {
				RepositoryItem inventoryrepositoryItem[] = null;
				Object[] params = new Object[1];
				params[0] = pSkuId;
				
				inventoryrepositoryItem = this.executeRQLQuery(getQueryString(), params, BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR, getInventoryTools().getInventoryRepository());
				if (inventoryrepositoryItem != null && inventoryrepositoryItem.length > 0) {
					ItemDescriptorImpl cacheInvItemDesc = (ItemDescriptorImpl) getInventoryTools().getInventoryRepository().getItemDescriptor(BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR);
					cacheInvItemDesc.removeItemFromCache(inventoryrepositoryItem[0].getRepositoryId());
					if (inventoryrepositoryItem[0].getPropertyValue("translations") != null) {
						this.logDebug("Site Translation Inventory Items Found in the Inventory");
						@SuppressWarnings("unchecked")
						final Map translationMap =((Map<String, RepositoryItem>) inventoryrepositoryItem[0].getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME));
						final Set transaltionKeys=translationMap.keySet();
						if (!translationMap.isEmpty()) {
							ItemDescriptorImpl cacheInvTransItemDesc  = (ItemDescriptorImpl) getInventoryTools().getInventoryRepository().getItemDescriptor(BBBCatalogConstants.ITEM_CACHED_TRANSLATIONS_INVENTORY_PROPERTY_NAME);
							for (final Object translationItemKey : transaltionKeys) {
								if(translationItemKey!=null){
								final RepositoryItem translationItem=(RepositoryItem) translationMap.get(translationItemKey);
								this.logDebug("Invalidating the Cache For Translated Item:::"+translationItem.getRepositoryId());
								cacheInvTransItemDesc.removeItemFromCache(translationItem.getRepositoryId(), true);
								}
							}
						}
					}
				} else {
					logError("Error while removing inventory ["+pSkuId+"] from cache");
					throw new BBBBusinessException(BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID,BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID);
				}
			} catch (RepositoryException e) {
				logError("Error while removing inventory ["+pSkuId+"] from cache");
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			}
		} else {
			logError("Error while removing inventory ["+pSkuId+"] from cache");
			throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

}