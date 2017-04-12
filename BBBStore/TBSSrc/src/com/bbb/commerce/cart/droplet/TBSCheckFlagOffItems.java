package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.TBSCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.InventoryTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class TBSCheckFlagOffItems extends CheckFlagOffItems {

	private TBSSearchStoreManager searchStoreManager;
	private InventoryTools inventoryTools;	
	private TBSCatalogToolsImpl bbbCatalogTools;

	public TBSSearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	public void setSearchStoreManager(TBSSearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}
	
	public TBSCatalogToolsImpl getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(TBSCatalogToolsImpl bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	/**
	 * @return the inventoryTools
	 */
	public InventoryTools getInventoryTools() {
		return inventoryTools;
	}
	

	/**
	 * @param inventoryTools the inventoryTools to set
	 */
	public void setInventoryTools(InventoryTools inventoryTools) {
		this.inventoryTools = inventoryTools;
	}
	

	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		boolean checkOOSItems = false;
		if(null != (pRequest.getParameter("checkOOSItem")) && (Boolean)pRequest.getObjectParameter("checkOOSItem")){
			checkOOSItems = true;
		}
		BBBOrder order = getOrder();
		List<CommerceItem> commerceItems = order.getCommerceItems();
		boolean flagOffValidated = true;
		boolean inStockItems = true;
		boolean tbsWebInventory = true;
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		
		for (CommerceItem commerceItem : commerceItems) {
			if(!flagOffValidated && !inStockItems){
				break;
			}
			String skuId = commerceItem.getCatalogRefId();
			logDebug("error occurred while checking sku is flagged off or not : SKU ID  : " +skuId);
			if(BBBUtility.isNotEmpty(skuId)){
				if(!(commerceItem instanceof BBBCommerceItem)){
					continue;
				}
				BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) commerceItem;
				if(sessionBean.isInternationalShippingContext() && BBBUtility.isNotEmpty(bbbCommerceItem.getStoreId()))
				{
					int inStockStatus = 0;
					try {
						inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, 0);
					} catch (BBBBusinessException e) {
						logDebug("error occurred while checking inventory of sku  : SKU ID  : " +skuId);
					} catch (BBBSystemException e) {
						logDebug("error occurred while checking inventory of sku  : SKU ID  : " +skuId);
					}
					if (inStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
						inStockItems = false;
				}
				}
				String registryId = bbbCommerceItem.getRegistryId();
				boolean isNotFlagOff = true;
				if(StringUtils.isEmpty(registryId) && StringUtils.isEmpty(bbbCommerceItem.getStoreId())) {
					try {
						isNotFlagOff = getOrderManager().getCatalogUtil().isSkuActive(skuId);
					} catch (RepositoryException e) {
						logError("error occurred while checking sku is flagged off or not : SKU ID  : " +skuId, e);
					}
				}  
				if(!isNotFlagOff){
					List<ShippingGroupCommerceItemRelationship> cisiRelationships = commerceItem.getShippingGroupRelationships();
					for(ShippingGroupCommerceItemRelationship cisi : cisiRelationships){
						ShippingGroup shippingGroup = cisi.getShippingGroup();
						logDebug("shipping group class ---- "+shippingGroup.getShippingGroupClassType());
						if(null != shippingGroup && !(shippingGroup instanceof BBBStoreShippingGroup)){
							logDebug("shipping group is invalid for flagOff item ---- "+shippingGroup.getShippingGroupClassType());
							flagOffValidated = false;
							break;
						}
					} 

				}
				String currentSiteId = SiteContextManager.getCurrentSiteId();
                if(currentSiteId.equalsIgnoreCase("TBS_BedBathCanada") || currentSiteId.equalsIgnoreCase("TBS_BuyBuyBaby")){
                       isNotFlagOff = true;
                       checkOOSItems= true;
                       inStockItems= true;
                }
				if(isNotFlagOff && checkOOSItems && inStockItems ){

					long pQty = bbbCommerceItem.getQuantity();
					List<ShippingGroupCommerceItemRelationship> cisiRelationships = commerceItem.getShippingGroupRelationships();
					for(ShippingGroupCommerceItemRelationship cisi : cisiRelationships){
						ShippingGroup shippingGroup = cisi.getShippingGroup();
						logDebug("shipping group class ---- "+shippingGroup.getShippingGroupClassType());
						if(null != shippingGroup && (!(shippingGroup instanceof BBBStoreShippingGroup) || SiteContextManager.getCurrentSiteId().contains("TBS_"))){
							int inStockStatus = 0;
							int tbsInStockStatus = 0;
							if(commerceItem instanceof BBBCommerceItem){
								try{
									boolean isStoreSku = false;
									RepositoryItem skuItem = getBbbCatalogTools().getSkuRepositoryItem(skuId);
									if(skuItem != null && skuItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) != null){
							                isStoreSku = ((Boolean) skuItem
							                                .getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME)).booleanValue();
							                
									} 
									
									if(isStoreSku){
					                	flagOffValidated = true;
					                } else {					                	
				                    	if(!BBBUtility.isEmpty(registryId)) {
											inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, pQty);
										} else {
											inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, pQty);
										}					                    
					                }			                	
									if (inStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
										inStockItems = false;
									}
									
									boolean inventoryFound = false;
                                    Integer inventoryStock = 0;
                                   
                                    if(currentSiteId.equalsIgnoreCase("TBS_BedBathCanada")){
                                          	List<String> canadaStoreIdList  = getSearchStoreManager().getCanadaStoreIds();
                                    	
                                           Map<String, Integer> inventories = new HashMap<String, Integer>();
                                           inventories = getBopusInventoryService().getInventoryForBopusItem(skuId, canadaStoreIdList, false);
                                           if (null != inventories && !inventories.isEmpty()) {
                                                 inventoryFound = true;
                                           }
                                           for (int i = 0; i < canadaStoreIdList.size(); i++)
                                                 {
                                                  if(inventoryFound && inventories.containsKey(canadaStoreIdList.get(i))){
                                                        inventoryStock += inventories.get(canadaStoreIdList.get(i));
                                                 }                                                                        
                                              }
                                           //Online inventory check
                                           if(!BBBUtility.isEmpty(registryId)) {
                                        	   tbsInStockStatus = getTBSProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, pQty,isStoreSku);
       										} else {
       											tbsInStockStatus = getTBSProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, pQty,isStoreSku);
       										}
                                            if (tbsInStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
                                            	tbsWebInventory = false;
       										}
                                           //Online inventory check
                                           //Handle Bopus orders
                                           if(!StringUtils.isEmpty(((BBBCommerceItem) commerceItem).getStoreId())){
                                               if(commerceItem.getQuantity()> inventories.get(((BBBCommerceItem) commerceItem).getStoreId())){
                                                inStockItems=false;
                                               }
                                           }else if(commerceItem.getQuantity()> inventoryStock && !tbsWebInventory){
                                        	   inStockItems=false;
                                           }else {
                                        	   inStockItems=true;
                                           }
                                    }
                                    else if(currentSiteId.equalsIgnoreCase("TBS_BuyBuyBaby")){
                                    	List<String> babyStoreIdList  = getSearchStoreManager().getBuyBuyBabyStoreIds();
                                           Map<String, Integer> inventoryByStores = new HashMap<String, Integer>();
                                           inventoryByStores = getBopusInventoryService().getInventoryForBopusItem(skuId, babyStoreIdList, false);
                                           if (null != inventoryByStores && !inventoryByStores.isEmpty()) {
                                                 inventoryFound = true;
                                           }
                                           for (int i = 0; i < babyStoreIdList.size(); i++){
                                                 if(inventoryFound && inventoryByStores.containsKey(babyStoreIdList.get(i))){
                                                        inventoryStock += inventoryByStores.get(babyStoreIdList.get(i));
                                                 }                                                                         
                                           }
                                                
                                           //Online inventory check
                                           if(!BBBUtility.isEmpty(registryId)) {
                                        	   tbsInStockStatus = getTBSProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, pQty,isStoreSku);
       										} else {
       											tbsInStockStatus = getTBSProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, pQty,isStoreSku);
       										}
                                            if (tbsInStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
                                            	tbsWebInventory = false;
       										}
                                           //Online inventory check
                                           //Handle Bopus orders
                                           if(!StringUtils.isEmpty(((BBBCommerceItem) commerceItem).getStoreId())){
                                               if(commerceItem.getQuantity()> inventoryByStores.get(((BBBCommerceItem) commerceItem).getStoreId())){
                                                inStockItems=false;
                                               }
                                           }else if(commerceItem.getQuantity()> inventoryStock && !tbsWebInventory){
                                        	   inStockItems=false;
                                           }else {
                                        	   inStockItems=true;
                                           }                               	
                                   }
                                           else if(((BBBCommerceItem) commerceItem).getStoreId()!=null && inStockStatus == BBBInventoryManager.AVAILABLE){
                                                 List<String> lStoreIds = new ArrayList<String>();
                                                 Map<String, Integer> inventoryByStores = new HashMap<String, Integer>();
                                                 lStoreIds.add(((BBBCommerceItem) commerceItem).getStoreId());
                                                 inventoryByStores = getBopusInventoryService().getInventoryForBopusItem(skuId, lStoreIds, false);
                                                 if(commerceItem.getQuantity()> inventoryByStores.get(((BBBCommerceItem) commerceItem).getStoreId())){
                                                        inStockItems=false;
                                                 }
                                           }
                                   //Needs to be removed 
									if(((BBBCommerceItem) commerceItem).getStoreId()!=null && inStockStatus == BBBInventoryManager.AVAILABLE){
										List<String> lStoreIds = new ArrayList<String>();
										Map<String, Integer> inventoryByStores = new HashMap<String, Integer>();
										lStoreIds.add(((BBBCommerceItem) commerceItem).getStoreId());
										inventoryByStores = getBopusInventoryService().getInventoryForBopusItem(skuId, lStoreIds, false);
										if(commerceItem.getQuantity()> inventoryByStores.get(((BBBCommerceItem) commerceItem).getStoreId())){
											inStockItems=false;
										}
									}
								}catch (Exception e) {
									logError("error occurred while checking inventory of sku  : SKU ID  : " +skuId,e);
								}

							}
						}
					}
				}
			}

		}
		pRequest.setParameter(BBBCoreConstants.FLAG_OFF_CHECKED,flagOffValidated);
		if(checkOOSItems){
			pRequest.setParameter(BBBCoreConstants.ITEM_OUT_OF_STOCK,inStockItems);
		}
		pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);


	}
	
	public int getTBSProductAvailability(String pSiteId, String pSkuId,
			String operation, long qty,boolean isStoreSku) throws BBBBusinessException, BBBSystemException {
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
			skuDetailVO = getBbbCatalogTools().getSKUDetails(pSiteId, pSkuId,
					false,true,isStoreSku);


				logDebug("response skuDetailVO from CatalogTools getSKUDetails() method :"
						+ skuDetailVO);

			String caFlag = skuDetailVO.getEcomFulfillment();

			if (skuDetailVO.isVdcSku()) {
				availabilityStatus = getInventoryManager().getVDCProductAvailability(pSiteId, pSkuId,
						0, BBBCoreConstants.CACHE_ENABLED);
			} else {
				availabilityStatus = getInventoryManager().getNonVDCProductAvailability(pSiteId,
						pSkuId, caFlag, operation, BBBCoreConstants.CACHE_ENABLED, qty);
			}
		} catch (Exception e) {
			logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1021 +
					": BBBInventoryManagerImpl : getProductAvailability() Exception occurred in getting Inventory information  for sku " + pSkuId);
			availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
		}

			logDebug("BBBInventoryManager : getProductAvailability() ends.");
			logDebug("Output - availabilityStatus " + availabilityStatus);

		BBBPerformanceMonitor.end("BBBInventoryManager getProductAvailability");
		return availabilityStatus;
	}


	
}
