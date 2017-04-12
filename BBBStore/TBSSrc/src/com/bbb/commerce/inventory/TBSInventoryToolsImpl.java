package com.bbb.commerce.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.lockmanager.LockReleaser;

public class TBSInventoryToolsImpl extends InventoryToolsImpl {
	
	
	private Map<String, String> mTbsBBBSiteMap;
	
	/**
	 * This method will return the list of InventoryVO's from the Inventory
	 * repository with all the available stock levels of that SKU
	 *
	 * @param inventory
	 *            is list of InventoryVO[]'s
	 * @return List of InventoryVO Objects
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryVO> getSKUInventory(final InventoryVO[] pInventory) throws BBBSystemException {
		vlogDebug("TBSInventoryToolsImpl :: getSKUInventory() method :: START");
		final List<InventoryVO> inventoryVOs = new ArrayList<InventoryVO>();
		RepositoryItem[] inventoryItems = null;
		Long globalStockLevel = Long.valueOf(0);
		Long siteStockLevel =Long.valueOf(0);
		Long registryStockLevel = Long.valueOf(0);
		if(pInventory == null || pInventory.length == TBSConstants.ZERO){
			vlogDebug("pInventory data is empty");
			return null;
		}
		for (InventoryVO inventoryVO : pInventory) {
			try {
				RepositoryView repositoryView = getInventoryRepository().getView(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR);
				if (inventoryVO.getSkuID() == null) {
					continue;
				}
				Object[] inventoryVOParams = new Object[] { inventoryVO.getSkuID() };
				RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_INVENTORY_SKU_QUERY);
				inventoryItems = statement.executeQuery(repositoryView, inventoryVOParams);
				if (inventoryItems != null) {
					InventoryVO newInventoryVO = new InventoryVO();
					globalStockLevel = Long.parseLong(inventoryItems[0].getPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME).toString());
					List<String> listOfSiteCode = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode");
					String siteId=null;
					if((listOfSiteCode!=null) && (listOfSiteCode.size()>0)){
						siteId=listOfSiteCode.get(0);
					}
					String lCurrentSiteId = inventoryVO.getSiteID();
					if(!StringUtils.isBlank(lCurrentSiteId) && !StringUtils.isBlank(getTbsBBBSiteMap().get(lCurrentSiteId))){
						lCurrentSiteId = getTbsBBBSiteMap().get(lCurrentSiteId);
					}
					if(!StringUtils.isBlank(lCurrentSiteId)){
						if ((siteId!=null) && lCurrentSiteId.equals(siteId)) {
							vlogDebug("Getting Inventory For Site US");
							siteStockLevel = Long.parseLong(inventoryItems[0].getPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME)
									.toString());
							registryStockLevel = Long.parseLong(inventoryItems[0]
									.getPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME).toString());
						} else {
							Map<String, RepositoryItem> transitions = (Map<String, RepositoryItem>) inventoryItems[0]
									.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME);
							if ((transitions != null) && !transitions.isEmpty()) {
								RepositoryItem ri = transitions.get(lCurrentSiteId);
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
		vlogDebug("TBSInventoryToolsImpl :: getSKUInventory() method :: END");
		return inventoryVOs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateSKUInventoryForAllSites(final List<InventoryVO> pInventory) throws BBBSystemException {
		if (null == pInventory) {
			vlogError ("pInventory is null");
			return;
		}

		TransactionDemarcation transactionMarker = new TransactionDemarcation();
		MutableRepository inventoryRepository = (MutableRepository) this.getInventoryRepository();
		if (0 == pInventory.size()) {
			if (this.isLoggingDebug()) {
				this.logDebug("List of Inventories received are Zero");
			}
		} else {
			for (int i = 0; i < pInventory.size(); i++) {
				final InventoryVO inventoryVO = pInventory.get(i);
				boolean rollback = false;
				if (this.isLoggingDebug()) {
					this.logDebug("Updating the Inventory for the SKU:" + inventoryVO.getSkuID());
				}
				try {
					List<String> listOfSiteCodes = getCatalogTools()
							.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"BedBathUSSiteCode");
					String siteId=null;
					if((listOfSiteCodes!=null) && (listOfSiteCodes.size()>0)){
						siteId=listOfSiteCodes.get(0);
					}
					String lCurrentSiteId = inventoryVO.getSiteID();
					if(!StringUtils.isBlank(lCurrentSiteId) && !StringUtils.isBlank(getTbsBBBSiteMap().get(lCurrentSiteId))){
						lCurrentSiteId = getTbsBBBSiteMap().get(lCurrentSiteId);
					}
					String pKey = inventoryVO.getInventoryID().toString().concat(lCurrentSiteId);
					if (!((!StringUtils.isBlank(lCurrentSiteId)) && (!StringUtils.isBlank(siteId)) && lCurrentSiteId.equals(siteId))) {
						siteId = lCurrentSiteId;
						pKey = inventoryVO.getInventoryID().toString().concat(siteId);						
					}
	                transactionMarker.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
	                this.getClientLockManager().acquireWriteLock(pKey);//locking inventory ID + siteId
                    final LockReleaser lr = new LockReleaser(this.getClientLockManager(),
                             this.getTransactionManager().getTransaction());
                    lr.addWriteLock(pKey);
					MutableRepositoryItem inventoryItem = inventoryRepository.getItemForUpdate(inventoryVO.getInventoryID().toString(),
							BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR);
					if (inventoryItem != null) {
						inventoryItem.setPropertyValue(BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME, inventoryVO.getGlobalStockLevel());
						if ((!StringUtils.isBlank(lCurrentSiteId)) && (!StringUtils.isBlank(siteId)) && lCurrentSiteId.equals(siteId)) {
							inventoryItem.setPropertyValue(BBBCatalogConstants.SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME, inventoryVO.getSiteStockLevel());
							inventoryItem.setPropertyValue(BBBCatalogConstants.REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME,
									inventoryVO.getGiftRegistryStockLevel());
							
						} else {
							Map<String, RepositoryItem> translations = (Map<String, RepositoryItem>) inventoryItem
									.getPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME);

							if ((translations != null) && !translations.isEmpty()) {
								MutableRepositoryItem mri = (MutableRepositoryItem) translations.get(siteId);
									if (mri != null) {
										mri.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_SITE_STOCK_LEVEL_INVENTORY_PROPERTY_NAME,
												inventoryVO.getSiteStockLevel());
										mri.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_REGISTRY_STOCK_LEVEL_INVENTORY_PROPERTY_NAME,
												inventoryVO.getGiftRegistryStockLevel());
										translations.put(siteId, mri);
									}
								inventoryItem.setPropertyValue(BBBCatalogConstants.TRANSLATIONS_INVENTORY_PROPERTY_NAME, translations);
							}	
						}
	                    inventoryRepository.updateItem(inventoryItem);
						if (this.isLoggingDebug()) {
							this.logDebug("Site ID:--->" + inventoryVO.getSiteID());
							this.logDebug("Global Stock Level(AFS)=" + inventoryVO.getGlobalStockLevel() + "|| Site Specific Stock Level(ALT_AFS)="
									+ inventoryVO.getSiteStockLevel() + "|| Gift Registry Stock Level(IGR)=" + inventoryVO.getGiftRegistryStockLevel());
						}
					}
				} catch (final RepositoryException e) {
					rollback=true;
					final String msg = "Error while updating inventory [" + inventoryVO.getSkuID() + "]";
					if (this.isLoggingError()) {
						this.logError(msg, e);
					}
				}catch (final BBBBusinessException e) {
					rollback=true;
					if (this.isLoggingError()) {
						this.logError("Some Business Exception", e);
					}
				} catch (final TransactionDemarcationException e) {
					rollback=true;
                    this.logError("TransactionDemarcationException occured in the beginning while updating online inventory " + inventoryVO.getSkuID(), e);
                } catch (DeadlockException e) {
                	rollback=true;
                    vlogError("Deadlock Exception add Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
                } catch (LockManagerException e) {
                	rollback=true;
                    vlogError("Lockmanager Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
                } catch (Exception e) {
                	rollback=true;
                    vlogError("System Exception and Mando inventory update failed for sku " + inventoryVO.getSkuID(), e);
                } finally {
                    try {
                        transactionMarker.end(rollback);
                    } catch (final TransactionDemarcationException e) {
                        this.logError("TransactionDemarcationException occured at the end while updating Mando inventory one of the sku", e);
                    }
                }
				
			}
		}
	}

	public Map<String, String> getTbsBBBSiteMap() {
		return mTbsBBBSiteMap;
	}

	public void setTbsBBBSiteMap(Map<String, String> pTbsBBBSiteMap) {
		mTbsBBBSiteMap = pTbsBBBSiteMap;
	}


}
