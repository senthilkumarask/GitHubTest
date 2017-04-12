package com.bbb.commerce.inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.TransactionManager;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.inventory.vo.InventoryFeedVO;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.lockmanager.LockReleaser;

public class InventoryToolsImpl extends BBBGenericService implements InventoryTools {

	/**
	 * The inventory repository instance
	 */
	private Repository mInventoryRepository;
	private BBBCatalogTools mCatalogTools;
	/**
     * Client lock manager to get locks on inventory update
     */
    private ClientLockManager clientLockManager;
    private TransactionManager mTransactionManager;


	public TransactionManager getTransactionManager() {
		return this.mTransactionManager;
	}

	public void setTransactionManager(final TransactionManager transactionManager) {
		this.mTransactionManager = transactionManager;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	/**
	 * RQL query for available inventory feed items.
	 */
	public static final String RQL_QUERY_AVAILABLE_FEED = "feedStatus=?0 OR feedStatus=?1 ORDER BY creationDate SORT ASC";

	public static final String RQL_QUERY_INVENTORY_SKU_QUERY = "catalogRefId=?0";

	/**
	 * Returns the inventory repository instance
	 *
	 * @return Inventory Repository
	 */
	@Override
	public Repository getInventoryRepository() {
		return this.mInventoryRepository;
	}

	/**
	 * Sets the inventory repository instance
	 *
	 * @param pInventoryRepository
	 */
	@Override
	public void setInventoryRepository(final Repository pInventoryRepository) {
		this.mInventoryRepository = pInventoryRepository;
	}

	public ClientLockManager getClientLockManager() {
		return this.clientLockManager;
	}

	public void setClientLockManager(final ClientLockManager clientLockManager) {
		this.clientLockManager = clientLockManager;
	}



	/**
	 * Returns the feed updates to inventory
	 *
	 * @return List of InventoryVO inventory feed updates
	 */
	@Override
	public List<InventoryFeedVO> getInventoryFeedUpdates(final String status) throws BBBSystemException {

		RepositoryItem[] inventoryFeedItems = null;
		try {
			final RepositoryView repositoryView = this.getInventoryRepository().getView("inventoryFeed");
			/* Get inventory feed updates for items with status CREATED & ERROR */
			final Object[] feedStatusParams = new Object[] { status, BBBCoreConstants.INVENTORY_FEED_ERROR };
			final RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_AVAILABLE_FEED);
			inventoryFeedItems = executeQuery(repositoryView, feedStatusParams, statement);
		} catch (final RepositoryException e) {
			final String msg = "Error while retrieving inventory feed updates";

			throw new BBBSystemException(e.getId(),msg, e);
		}
		/* Return inventory feed updates */
		final List<InventoryFeedVO> inventoryFeeds = this.getRepositoryItemToInventoryFeedVO(inventoryFeedItems);
	
		this.logDebug("Found [" + inventoryFeeds.size() + "] inventory feed items");
		
		return inventoryFeeds;
	}

	/**
	 * Invokes invalidate cache for Inventory Repository.
	 *
	 */
	@Override
	public void invalidateInventoryCache() throws BBBSystemException {
		try {
			final ItemDescriptorImpl inventoryDescriptor = (ItemDescriptorImpl) this.getInventoryRepository()
				.getItemDescriptor(
						BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR);
			inventoryDescriptor.invalidateCaches(true);

			final ItemDescriptorImpl inventoryDescriptorTranslation = (ItemDescriptorImpl) this.getInventoryRepository().getItemDescriptor(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME);
			inventoryDescriptorTranslation.invalidateCaches(true);

		} catch (final RepositoryException ex) {
			final String msg = "Error occurred while removing inventory from cache";
			throw new BBBSystemException(ex.getId(), msg, ex);
		}

	}

	/**
	 * Given inventory detail, removes the inventory item from cache
	 *
	 * @param pInventoryFeed
	 */
	@Override
	public void removeItemFromCache(final InventoryFeedVO pInventoryFeed) throws BBBSystemException {
		try {
			ItemDescriptorImpl inventoryDescriptor = (ItemDescriptorImpl) this.getInventoryRepository().getItemDescriptor(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR);
			inventoryDescriptor.removeItemFromCache(pInventoryFeed.getInventoryID(), true);
			final RepositoryItem inventoryItem = inventoryDescriptor.getRepositoryImpl().getItem(pInventoryFeed.getInventoryID());
			if (inventoryItem.getPropertyValue("translations") != null) {
				this.logDebug("Site Translation Inventory Items Found in the Inventory");
				@SuppressWarnings("unchecked")
				final
				Map translationMap =((Map<String, RepositoryItem>) inventoryItem.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME));
				final Set transaltionKeys=translationMap.keySet();
				//List<RepositoryItem> repoItems = (List<RepositoryItem>) ((Map<String, RepositoryItem>) inventoryItem.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME)).values();
				if (!translationMap.isEmpty()) {
 					inventoryDescriptor = (ItemDescriptorImpl) this.getInventoryRepository().getItemDescriptor(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME);
					for (final Object translationItemKey : transaltionKeys) {
						if(translationItemKey!=null){
						final RepositoryItem	translationItem=(RepositoryItem)translationMap.get(translationItemKey);
						this.logDebug("Invalidating the Cache For Translated Item:::"+translationItem.getRepositoryId());
						inventoryDescriptor.removeItemFromCache(translationItem.getRepositoryId(), true);
						}
					}
				}
			}

			this.logDebug("Removed inventory [" + pInventoryFeed.getInventoryID() + " | " + pInventoryFeed.getDisplayName() + "] from cache");

		} catch (final RepositoryException e) {
			final String msg = "Error while removing inventory [" + pInventoryFeed.getInventoryID() + " | " + pInventoryFeed.getDisplayName() + "] from cache";

			throw new BBBSystemException(e.getId(),msg, e);
		}

	}

	/**
	 * Updates the inventory feed with the provided values
	 *
	 * @param pInventoryFeed
	 */
	/**
	 * Updates the inventory feed with the provided values
	 *
	 * @param pInventoryFeed
	 */
	@Override
	public void updateInventoryFeed(final List<InventoryFeedVO> pInventoryFeed) throws BBBSystemException {
		final MutableRepository mutableRepository = (MutableRepository) this.getInventoryRepository();
		InventoryFeedVO inventoryFeed = null;
		for (int index = 0; index < pInventoryFeed.size(); index++) {
			inventoryFeed = pInventoryFeed.get(index);
			try {

				final MutableRepositoryItem mutableInventoryFeed = mutableRepository.getItemForUpdate(inventoryFeed.getID(), "inventoryFeed");
				if (mutableInventoryFeed != null) {
					mutableInventoryFeed.setPropertyValue("feedStatus", inventoryFeed.getFeedStatus());
					mutableInventoryFeed.setPropertyValue("lastUpdated", new Timestamp(Calendar.getInstance().getTimeInMillis()));
					mutableRepository.updateItem(mutableInventoryFeed);
					break;
				}
			} catch (final RepositoryException e) {
				final String msg = "Error while updating inventory [" + inventoryFeed.getFeedID() + "] with status [" + inventoryFeed.getFeedStatus() + "]";

				throw new BBBSystemException(e.getId(),msg, e);
			}
		}

	}

	/*
	 * Given an array of inventory feed repository items, transform them into a
	 * list of inventory feed VOs
	 */
	private List<InventoryFeedVO> getRepositoryItemToInventoryFeedVO(final RepositoryItem[] repositoryItems) {
		final List<InventoryFeedVO> inventoryFeeds = new ArrayList<InventoryFeedVO>();
		if (repositoryItems != null) {
			this.logDebug("Inside InventoryToolsImpl. repositoryItems.length = " + repositoryItems.length);
			for (final RepositoryItem repositoryItem : repositoryItems) {
				InventoryFeedVO inventoryFeed = null;
				final String feed_id = (String) repositoryItem.getPropertyValue("feedID");
				if((repositoryItem.getPropertyValue("feedStatus") != null) && repositoryItem.getPropertyValue("feedStatus").toString().equalsIgnoreCase(BBBCoreConstants.FULL_OPEN))
				{
					this.logDebug("Inside InventoryToolsImpl. Got one full feed with status " + repositoryItem.getPropertyValue("feedStatus").toString());
					inventoryFeed = new InventoryFeedVO();
					inventoryFeed.setID(repositoryItem.getRepositoryId());
					inventoryFeed.setFeedID((String) repositoryItem.getPropertyValue("feedID"));
					inventoryFeed.setFeedStatus((String) repositoryItem.getPropertyValue("feedStatus"));
					inventoryFeeds.add(inventoryFeed);
					continue;
				}
				try {					
		            this.logDebug("InventoryFeedJob.doScheduledTask, Current thread going to sleep for 10000 milli sec");
		            threadSleep(10000);
				} catch (final InterruptedException e1) {
					this.logError("InventoryFeedJob.doScheduledTask, Exception occurred while adding delay in inverntory feed update operation-" + e1);
					Thread.currentThread().interrupt();
				}

				RepositoryView repositoryView;
				try {
					repositoryView = this.getInventoryRepository().getView("inventory");
				/* Get inventory feed updates for items with status CREATED & ERROR */
				final Object[] feedStatusParams = new Object[] { feed_id };
				final RqlStatement statement = RqlStatement.parseRqlStatement("feedID=?0");
				final RepositoryItem[] inventoryItems = executeQuery(repositoryView, feedStatusParams, statement);
				if(inventoryItems!=null){
				for (final RepositoryItem inventoryItem : inventoryItems) {
					inventoryFeed = new InventoryFeedVO();

					inventoryFeed.setID(repositoryItem.getRepositoryId());
					inventoryFeed.setFeedID((String) repositoryItem.getPropertyValue("feedID"));
					inventoryFeed.setFeedStatus((String)repositoryItem.getPropertyValue("feedStatus"));
					final RepositoryItem inventory = inventoryItem;
					if (inventory != null) {
						inventoryFeed.setInventoryID(inventory.getRepositoryId());
						inventoryFeed.setSkuID((String) inventory.getPropertyValue("catalogRefId"));
						inventoryFeed.setCreationDate((Timestamp) inventory.getPropertyValue("creationDate"));
						inventoryFeed.setStartDate((Timestamp) inventory.getPropertyValue("startDate"));
						inventoryFeed.setEndDate((Timestamp) inventory.getPropertyValue("endDate"));
						inventoryFeed.setDisplayName((String) inventory.getPropertyValue("displayName"));
						inventoryFeed.setDescription((String) inventory.getPropertyValue("description"));
						inventoryFeed.setGlobalStockLevel(((Long) inventory.getPropertyValue("stockLevel")).longValue());
						inventoryFeed.setSiteStockLevel(((Long) inventory.getPropertyValue("siteStockLevel")).longValue());
						inventoryFeed.setGiftRegistryStockLevel(((Long) inventory.getPropertyValue("registryStockLevel")).longValue());
						@SuppressWarnings("unchecked")
						final
						Map<String,RepositoryItem> translations=(Map<String, RepositoryItem>)inventory.getPropertyValue("translations");
						if (translations != null) {
						String siteId = null;
						List<String> listOfSiteCodes = null;
						listOfSiteCodes = this.mCatalogTools
										.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BuyBuyBabySiteCode");
						if (!BBBUtility.isListEmpty(listOfSiteCodes)) {
								siteId = listOfSiteCodes.get(0);
						}
						if(translations.get(siteId)!=null){
						inventoryFeed.setBASiteStockLevel(((Long)translations.get(siteId).getPropertyValue("siteStockLevel")).longValue());
						inventoryFeed.setBAGiftRegistryStockLevel(((Long) translations.get(siteId).getPropertyValue("registryStockLevel")).longValue());
						}
						listOfSiteCodes = this.mCatalogTools
								.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathCanadaSiteCode");
						if (!BBBUtility.isListEmpty(listOfSiteCodes)) {
								siteId = listOfSiteCodes.get(0);
						}
						if(translations.get(siteId)!=null){
						inventoryFeed.setCASiteStockLevel(((Long) translations.get(siteId).getPropertyValue("siteStockLevel")).longValue());
						inventoryFeed.setCAGiftRegistryStockLevel(((Long) translations.get(siteId).getPropertyValue("registryStockLevel")).longValue());
						}
						}
					}
					inventoryFeeds.add(inventoryFeed);
					}
				}
				} catch (final RepositoryException e) {
					final String msg = "Error while Fetching inventory";
					if (this.isLoggingError()) {
						this.logError(msg, e);
					}
				}catch (final BBBSystemException bbbse) {
					final String msg = "BBBSystemException::: System Exception while Fetching inventory";
					if (this.isLoggingError()) {
						this.logError(msg, bbbse);
					}
				} catch (final BBBBusinessException bbbbe) {
					final String msg = "BBBBusinessException::: Busness Exception while Fetching inventory";
					if (this.isLoggingError()) {
						this.logError(msg, bbbbe);
					}
				}
			}
			this.logDebug("Inside InventoryToolsImpl. inventoryFeeds.size() = " + inventoryFeeds.size());
		}
		return inventoryFeeds;
	}

	/**
	 * @throws InterruptedException
	 */
	protected void threadSleep(long val) throws InterruptedException {
		Thread.sleep(val);
	}

	/**
	 * This method will return the list of InventoryVO's from the Inventory
	 * repository with all the available stock levels of that SKU
	 *
	 * @param inventory
	 *            is list of InventoryVO[]'s
	 * @return List of InventoryVO Objects
	 * @throws BBBSystemException
	 */
	@Override
	public List<InventoryVO> getSKUInventory(final InventoryVO[] pInventory) throws BBBSystemException {
		final List<InventoryVO> inventoryVOs = new ArrayList<InventoryVO>();
		RepositoryItem[] inventoryItems = null;
		Long globalStockLevel = Long.valueOf(0);
		Long siteStockLevel =Long.valueOf(0);
		Long registryStockLevel = Long.valueOf(0);
		for (final InventoryVO inventoryVO : pInventory) {
			try {
				final RepositoryView repositoryView = this.getInventoryRepository().getView(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR);
				if (inventoryVO.getSkuID() == null) {
					continue;
				}
				final Object[] inventoryVOParams = new Object[] { inventoryVO.getSkuID() };
				final RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_INVENTORY_SKU_QUERY);
				inventoryItems = executeQuery(repositoryView, inventoryVOParams, statement);
				if (inventoryItems != null) {
					final InventoryVO newInventoryVO = new InventoryVO();
					globalStockLevel = Long.parseLong(inventoryItems[0].getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME).toString());
					final List<String> listOfSiteCode = this.mCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode");
					String siteId=null;
					if((listOfSiteCode!=null) && (listOfSiteCode.size()>0)){
					siteId=listOfSiteCode.get(0);
					}
					if ((inventoryVO.getSiteID()!=null) && (siteId!=null) && inventoryVO.getSiteID().equals(siteId)) {
						this.logDebug("Getting Inventory For Site US");
						siteStockLevel = Long.parseLong(inventoryItems[0].getPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME)
								.toString());
						registryStockLevel = Long.parseLong(inventoryItems[0]
								.getPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME).toString());
					} else {
						@SuppressWarnings("unchecked")
						final
						Map<String, RepositoryItem> transitions = (Map<String, RepositoryItem>) inventoryItems[0]
								.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME);
						if ((transitions != null) && !transitions.isEmpty()) {
							final RepositoryItem ri = transitions.get(inventoryVO.getSiteID());
							Long stockLevel = (Long) ri.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME);
							if (stockLevel != null) {
								siteStockLevel = stockLevel.longValue();
							}
							stockLevel = (Long) ri.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME);
							if (stockLevel != null) {
								registryStockLevel = stockLevel.longValue();
							}
						}
					}
					newInventoryVO.setInventoryID(inventoryItems[0].getRepositoryId());
					newInventoryVO.setGlobalStockLevel(globalStockLevel);
					newInventoryVO.setSiteStockLevel(siteStockLevel);
					newInventoryVO.setGiftRegistryStockLevel(registryStockLevel);
					newInventoryVO.setSiteID(inventoryVO.getSiteID());
					newInventoryVO.setSkuID(inventoryVO.getSkuID());
					newInventoryVO.setOrderedQuantity(inventoryVO.getOrderedQuantity());
					inventoryVOs.add(newInventoryVO);
				}

			} catch (final RepositoryException re) {
				final String msg = "Error while fetcing the Invenotry Stock details for the SKU" + inventoryVO.getSiteID();
				throw new BBBSystemException(re.getId(),msg, re);
			} catch (final BBBBusinessException e) {
				if (this.isLoggingError()) {
					this.logError("Some Business Exception", e);
				}
			}
		}
		return inventoryVOs;
	}


	/**
	 * Updates the SKU's Stock Level in the inventory for the sites provided in
	 * the InventoryVO objects present in the List
	 *
	 * @param pInventory
	 * @throws BBBSystemException
	 */

	@Override
	public void updateSKUInventory(final List<InventoryVO> pInventory) throws BBBSystemException {
		final MutableRepository mutableRepository = (MutableRepository) this.getInventoryRepository();
		if ((pInventory != null) && (pInventory.size() == 0)) {
			this.logDebug("List of Inventories received are Zero");
		} else {
			for (int i = 0; i < pInventory.size(); i++) {
				final InventoryVO inventoryVO = pInventory.get(i);
				this.logDebug("Updating the Inventory for the SKU:" + inventoryVO.getSkuID());
				try {
					final MutableRepositoryItem mutableInventoryVO = mutableRepository.getItemForUpdate(inventoryVO.getInventoryID().toString(),
							BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR);
					if (mutableInventoryVO != null) {
						mutableInventoryVO.setPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME, inventoryVO.getGlobalStockLevel());
						final List<String> listOfSiteCodes = this.mCatalogTools
								.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode");
						String siteId=null;
						if(!BBBUtility.isListEmpty(listOfSiteCodes)){
						siteId=listOfSiteCodes.get(0);
						}
						String pKey = mutableInventoryVO.getRepositoryId().concat(siteId);
						if ((inventoryVO.getSiteID() != null) && (siteId!=null) && !inventoryVO.getSiteID().equals(siteId)) {
							pKey = mutableInventoryVO.getRepositoryId().concat(inventoryVO.getSiteID());
							@SuppressWarnings("unchecked")
							final
							Map<String, RepositoryItem> translations = (Map<String, RepositoryItem>) mutableInventoryVO
									.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME);

							if (!BBBUtility.isMapNullOrEmpty(translations)) {
								final MutableRepositoryItem mri = (MutableRepositoryItem) translations.get(inventoryVO.getSiteID());
								mri.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME, inventoryVO.getSiteStockLevel());
								mri.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME,
										inventoryVO.getGiftRegistryStockLevel());
								translations.put(inventoryVO.getSiteID(), mri);
								mutableInventoryVO.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME, translations);

							}
						} else {
							mutableInventoryVO.setPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME, inventoryVO.getSiteStockLevel());
							mutableInventoryVO.setPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME,
									inventoryVO.getGiftRegistryStockLevel());
						}
						if(pKey != null){

							 final TransactionDemarcation transactionMarker = new TransactionDemarcation();
				                try {
				                    transactionMarker.begin(this.getTransactionManager(),
				                            TransactionDemarcation.REQUIRES_NEW);

				                    this.getClientLockManager().acquireWriteLock(pKey);//locking inventory ID + siteId
				                    final LockReleaser lr = new LockReleaser(this.getClientLockManager(),
				           		         this.getTransactionManager().getTransaction());
				                    lr.addWriteLock(pKey);

				                    mutableRepository.updateItem(mutableInventoryVO);

				                } catch (final TransactionDemarcationException e) {
				                    this.logError("TransactionDemarcationException occured in the beginning while updating online inventory " + inventoryVO.getSkuID(), e);
				                } catch (final DeadlockException e) {
				                    this.logError("Deadlock Exception add Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
				                } catch (final LockManagerException e) {
				                    this.logError("Lockmanager Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
				                } catch (final Exception e) {
				                    this.logError("System Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
				                } finally {
				                    try {
				                        transactionMarker.end();
				                    } catch (final TransactionDemarcationException e) {
				                        this.logError("TransactionDemarcationException occured at the end while updating Mando inventory one of the sku", e);
				                    }
				                }




						}
						this.logDebug("Site ID:--->" + inventoryVO.getSiteID());
						this.logDebug("Global Stock Level(AFS)=" + inventoryVO.getGlobalStockLevel() + "|| Site Specific Stock Level(ALT_AFS)="
									+ inventoryVO.getSiteStockLevel() + "|| Gift Registry Stock Level(IGR)=" + inventoryVO.getGiftRegistryStockLevel());
					
					}
				} catch (final RepositoryException e) {
					final String msg = "Error while updating inventory [" + inventoryVO.getSkuID() + "]";
					this.logError(msg, e);
					
				}catch (final BBBBusinessException e) {
					this.logError("Some Business Exception", e);
				}
			}
		}
	}
	/**
	 * Updates the SKU's Stock Level in the inventory for the sites provided in
	 * the InventoryVO objects present in the List
	 *
	 * @param pInventory
	 * @throws BBBSystemException
	 */

	@Override
	public void updateSKUInventoryForAllSites(final List<InventoryVO> pInventory) throws BBBSystemException {
		if (null == pInventory) {
			this.logError ("pInventory is null");
			return;
		}
		final TransactionDemarcation transactionMarker = new TransactionDemarcation();
		final MutableRepository mutableRepository = (MutableRepository) this.getInventoryRepository();
 
		if (0 == pInventory.size()) {
		this.logDebug("List of Inventories received are Zero");
		} else {
			for (int i = 0; i < pInventory.size(); i++) { 
				boolean rollback = false;
				final InventoryVO inventoryVO = pInventory.get(i);
				this.logDebug("Updating the Inventory for the SKU:" + inventoryVO.getSkuID());
				try {
					List<String> listOfSiteCodes = this.mCatalogTools
							.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode");
					String siteId=null;
					if((listOfSiteCodes!=null) && (listOfSiteCodes.size()>0)) {
						siteId=listOfSiteCodes.get(0);
					}
					
					if (!((inventoryVO.getSiteID()!=null) && (siteId!=null) && inventoryVO.getSiteID().equals(siteId))) {
						siteId = inventoryVO.getSiteID();
					}
					String pKey = inventoryVO.getInventoryID().concat(siteId);
                    transactionMarker.begin(this.getTransactionManager(),
                            TransactionDemarcation.REQUIRES_NEW);
                    this.getClientLockManager().acquireWriteLock(pKey);//locking inventory ID + siteId
                    final LockReleaser lr = new LockReleaser(this.getClientLockManager(),
           		         this.getTransactionManager().getTransaction());
                    lr.addWriteLock(pKey);
					final MutableRepositoryItem mutableInventoryVO = mutableRepository.getItemForUpdate(inventoryVO.getInventoryID().toString(),
							BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR);

					if (mutableInventoryVO != null) {
						mutableInventoryVO.setPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME, inventoryVO.getGlobalStockLevel());
						if ((inventoryVO.getSiteID()!=null) && (siteId!=null) && inventoryVO.getSiteID().equals(siteId)) {
							mutableInventoryVO.setPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME, inventoryVO.getSiteStockLevel());
							mutableInventoryVO.setPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME,
									inventoryVO.getGiftRegistryStockLevel());
						} else {
							@SuppressWarnings("unchecked")
							final
							Map<String, RepositoryItem> translations = (Map<String, RepositoryItem>) mutableInventoryVO
									.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME);

							if (!BBBUtility.isMapNullOrEmpty(translations)) {
								final MutableRepositoryItem mri = (MutableRepositoryItem) translations.get(siteId);
									if (mri != null) {
										mri.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME,
												inventoryVO.getSiteStockLevel());
										mri.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME,
												inventoryVO.getGiftRegistryStockLevel());
										translations.put(siteId, mri);
									}
								mutableInventoryVO.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME, translations);
							}	
						}
						logInfo("Decrementing repository for "+ mutableInventoryVO.getRepositoryId() +" with version number: ["+mutableInventoryVO.getPropertyValue("version")+"]");
				        mutableRepository.updateItem(mutableInventoryVO);
						this.logDebug("Site ID:--->" + inventoryVO.getSiteID());
						this.logDebug("Global Stock Level(AFS)=" + inventoryVO.getGlobalStockLevel() + "|| Site Specific Stock Level(ALT_AFS)="
									+ inventoryVO.getSiteStockLevel() + "|| Gift Registry Stock Level(IGR)=" + inventoryVO.getGiftRegistryStockLevel());
					}
				} catch (final TransactionDemarcationException e) {
                    this.logError("TransactionDemarcationException occured in the beginning while updating online inventory " + inventoryVO.getSkuID(), e);
                    rollback = true;
				} catch (final RepositoryException e) {
					final String msg = "Error while updating inventory [" + inventoryVO.getSkuID() + "]";
					this.logError(msg, e);	
					rollback = true;
				}catch (final BBBBusinessException e) {
					this.logError("Some Business Exception", e);		
					rollback = true;
	            } catch (final DeadlockException e) {
	            	rollback = true;
                    this.logError("Deadlock Exception add Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
                } catch (final LockManagerException e) {
                	rollback = true;
                    this.logError("Lockmanager Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
                } catch (final Exception e) {
                	rollback = true;
                    this.logError("System Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
                }
				finally {
                    try {
                        transactionMarker.end(rollback);
                    } catch (final TransactionDemarcationException e) {
                        this.logError("TransactionDemarcationException occured at the end while updating Mando inventory one of the sku", e);
                    }
                }
			}
		}
	}
	/**This method is used to fetch repository data
	 * @param repositoryView
	 * @param inventoryVOParams
	 * @param statement
	 * @return
	 * @throws RepositoryException
	 */
	protected RepositoryItem[] executeQuery(final RepositoryView repositoryView, final Object[] inventoryVOParams,
			final RqlStatement statement) throws RepositoryException {
		return statement.executeQuery(repositoryView, inventoryVOParams);
	}
}
