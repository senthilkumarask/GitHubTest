package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BopusInventoryService;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

public class CheckFlagOffItems extends BBBDynamoServlet{

	BopusInventoryService bopusInventoryService;

	/**
	 * @return the bopusInventoryService
	 */
	public BopusInventoryService getBopusInventoryService() {
		return bopusInventoryService;
	}

	/**
	 * @param pBopusInventoryService the bopusInventoryService to set
	 */
	public void setBopusInventoryService(
			BopusInventoryService pBopusInventoryService) {
		bopusInventoryService = pBopusInventoryService;
	}

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		boolean checkOOSItems = false;
		if(null != (pRequest.getParameter("checkOOSItem")) && (Boolean)pRequest.getObjectParameter("checkOOSItem")){
			checkOOSItems = true;
		}
		BBBOrder order = getOrder();
		List<CommerceItem> commerceItems = order.getCommerceItems();
		boolean flagOffValidated = true;
		boolean inStockItems = true;
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
				if(isNotFlagOff && checkOOSItems && inStockItems ){

					long pQty = bbbCommerceItem.getQuantity();
					List<ShippingGroupCommerceItemRelationship> cisiRelationships = commerceItem.getShippingGroupRelationships();
					for(ShippingGroupCommerceItemRelationship cisi : cisiRelationships){
						ShippingGroup shippingGroup = cisi.getShippingGroup();
						logDebug("shipping group class ---- "+shippingGroup.getShippingGroupClassType());
						if(null != shippingGroup && (!(shippingGroup instanceof BBBStoreShippingGroup) || SiteContextManager.getCurrentSiteId().contains("TBS_"))){
							int inStockStatus;
							if(commerceItem instanceof BBBCommerceItem){
								try{
									if (!BBBUtility.isEmpty(registryId)) {
										inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, pQty);
									} else {
										inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, pQty);
									}

									if (inStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
										inStockItems = false;
									}
									if(!BBBUtility.isEmpty(((BBBCommerceItem) commerceItem).getStoreId())
											&& inStockStatus == BBBInventoryManager.AVAILABLE){
										List<String> lStoreIds = new ArrayList<String>();
										Map<String, Integer> inventoryByStores = new HashMap<String, Integer>();
										lStoreIds.add(((BBBCommerceItem) commerceItem).getStoreId());
										inventoryByStores = getBopusInventoryService().getInventoryForBopusItem(skuId, lStoreIds, false);
										Integer actualItemQuantity = inventoryByStores.get(((BBBCommerceItem) commerceItem).getStoreId());
										if(null != actualItemQuantity && commerceItem.getQuantity() > actualItemQuantity){
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

	private BBBOrderManager orderManager;
	private BBBOrder order;
	private BBBInventoryManager inventoryManager;

	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public BBBOrder getOrder() {
		return order;
	}

	public void setOrder(BBBOrder order) {
		this.order = order;
	}

	public BBBOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
