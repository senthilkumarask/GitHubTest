package com.bbb.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.PricingModelHolder;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.TBSOrderManager;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;

public class TBSProfileTools extends BBBProfileTools {

	@Override
	protected void postLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, RepositoryItem pProfile,
			OrderHolder pShoppingCart, PricingModelHolder pPricingModelHolder) throws ServletException {
		
		//pRequest.setParameter("userCheckingOut", null);
		
		super.postLoginUser(pRequest, pResponse, pProfile, pShoppingCart, pPricingModelHolder);
		boolean isMonitorCanceled = false;
		String methodName = BBBPerformanceConstants.POST_LOGIN_USER;
		CommerceItemManager commerceItemManager = getOrderManager().getCommerceItemManager();
		Order order = pShoppingCart.getCurrent();

		synchronized (order) {
			List<String> removableItems = new ArrayList<String>();
			List<TBSProfileTools.TbsCItemInfo> tbsItemsList = new ArrayList<TBSProfileTools.TbsCItemInfo>();
			SKUDetailVO skuDetailVO = null;
			AuxiliaryData aux = null;
			TBSCommerceItem tbsItem = null;
			ShippingGroup shipGroup = null;
			boolean storeOrderToBeUpdated = false;
			long qty = 0L;
			try {
				List<CommerceItem> items = order.getCommerceItems();
				for (CommerceItem cItem : items) {
					if(!cItem.getAuxiliaryData().getSiteId().equals(SiteContextManager.getCurrentSiteId())){
					if (cItem instanceof LTLDeliveryChargeCommerceItem || cItem instanceof LTLAssemblyFeeCommerceItem){
						storeOrderToBeUpdated = true;
						removableItems.add(cItem.getId());
					}  
					}
					
					if (cItem instanceof GiftWrapCommerceItem
							&& !cItem.getAuxiliaryData().getSiteId().equals(SiteContextManager.getCurrentSiteId())){
						storeOrderToBeUpdated = true;
						removableItems.add(cItem.getId());
					} else	if (!(cItem instanceof TBSCommerceItem)&&(cItem instanceof BBBCommerceItem)) {
						storeOrderToBeUpdated = true;
						BBBCommerceItem bbbItem = (BBBCommerceItem) cItem;

						aux = bbbItem.getAuxiliaryData();

						tbsItem = (TBSCommerceItem) commerceItemManager.createCommerceItem(bbbItem.getCommerceItemClassType(), bbbItem.getCatalogRefId(),
								aux.getCatalogRef(), aux.getProductId(), aux.getProductRef(), bbbItem.getQuantity(),
								bbbItem.getCatalogKey(), bbbItem.getCatalogId(), null, null);

						if (bbbItem.isItemMoved()) {
							tbsItem.setCommerceItemMoved(bbbItem.getAuxiliaryData().getProductId() + "," + bbbItem.getStoreId());
						}
						tbsItem.setStoreId(bbbItem.getStoreId());
						tbsItem.setRegistryId(bbbItem.getRegistryId());
						tbsItem.setItemMoved(bbbItem.isItemMoved());
						tbsItem.setRegistryInfo(bbbItem.getRegistryInfo());
						tbsItem.setBts(bbbItem.getBts());
						tbsItem.setVdcInd(bbbItem.isVdcInd());
						tbsItem.setFreeShippingMethod(bbbItem.getFreeShippingMethod());
						tbsItem.setSkuSurcharge(bbbItem.getSkuSurcharge());
						tbsItem.setLastModifiedDate(bbbItem.getLastModifiedDate());
						
						//BBBH-2396 - Cross channel Cart - To retain personalized details while moving from store to tbs
						tbsItem.setReferenceNumber(bbbItem.getReferenceNumber());
						tbsItem.setPersonalizationDetails(bbbItem.getPersonalizationDetails());
						tbsItem.setPersonalizationOptions(bbbItem.getPersonalizationOptions());
						tbsItem.setPersonalizeCost(bbbItem.getPersonalizeCost());
						tbsItem.setFullImagePath(bbbItem.getFullImagePath());
						tbsItem.setThumbnailImagePath(bbbItem.getThumbnailImagePath());
						tbsItem.setEximErrorExists(bbbItem.isEximErrorExists());
						tbsItem.setEximPricingReq(bbbItem.isEximPricingReq());
						tbsItem.setPersonalizationOptionsDisplay(bbbItem.getPersonalizationOptionsDisplay());
						tbsItem.setPersonalizePrice(bbbItem.getPersonalizePrice());
						tbsItem.setMetaDataFlag(bbbItem.getMetaDataFlag());
						tbsItem.setMetaDataUrl(bbbItem.getMetaDataUrl());
						tbsItem.setModerationFlag(bbbItem.getModerationFlag());
						tbsItem.setModerationUrl(bbbItem.getModerationUrl());
						tbsItem.setMobileFullImagePath(bbbItem.getMobileFullImagePath());
						tbsItem.setMobileThumbnailImagePath(bbbItem.getMobileThumbnailImagePath());
						try {
							skuDetailVO = getCatalogTools().getSKUDetails(order.getSiteId(), tbsItem.getCatalogRefId(), false, true, true);

							List<ShippingGroupCommerceItemRelationship> commItemRels = bbbItem.getShippingGroupRelationships();

							TbsCItemInfo tbsCItemInfo = new TbsCItemInfo();
							tbsCItemInfo.setTbsItem(tbsItem);

						if (skuDetailVO != null) {
							tbsItem.setIsEcoFeeEligible(skuDetailVO.getIsEcoFeeEligible());
						}
						if (skuDetailVO.isLtlItem()) {
							tbsItem.setLtlShipMethod(((BBBShippingGroupCommerceItemRelationship) bbbItem.getShippingGroupRelationships()
									.get(0)).getShippingGroup().getShippingMethod());
							tbsItem.setDeliveryItemId(((BBBCommerceItem) bbbItem).getDeliveryItemId());
							tbsItem.setAssemblyItemId(((BBBCommerceItem) bbbItem).getAssemblyItemId());
							tbsItem.setWhiteGloveAssembly(((BBBCommerceItem) bbbItem).getWhiteGloveAssembly());
							tbsItem.setLtlItem(true);
							
						}
						
						TBSItemInfo tbsItemInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSItemInfo();
						tbsItem.setTBSItemInfo(tbsItemInfo);
							for (ShippingGroupCommerceItemRelationship commItemRel : commItemRels) {

								tbsCItemInfo.getQuantities().add(commItemRel.getQuantity());
								tbsCItemInfo.getShipGroupIds().add(commItemRel.getShippingGroup().getId());
							}

							tbsItemsList.add(tbsCItemInfo);

							removableItems.add(bbbItem.getId());
						} catch (BBBSystemException e) {

							throw new CommerceException("BBBSystem Exception from mergeOrdersCopyCommerceItem in BBBCommerceItemManager", e);
						} catch (BBBBusinessException e) {

							throw new CommerceException("BBBSystem Exception from mergeOrdersCopyCommerceItem in BBBCommerceItemManager", e);
						}
					}
				}
				
				for (String removalId : removableItems) {
					commerceItemManager.removeItemFromOrder(order, removalId);
				}

				for (TbsCItemInfo tbsCItem : tbsItemsList) {

					commerceItemManager.addItemToOrder(order, tbsCItem.getTbsItem());
					List<String> shipGroupIds = tbsCItem.getShipGroupIds();
					List<Long> quantities = tbsCItem.getQuantities();
					Iterator<Long> quantityIterator = quantities.iterator();
					for (Iterator<String> shipIdIterator = shipGroupIds.iterator(); shipIdIterator.hasNext();) {
						commerceItemManager.addItemQuantityToShippingGroup(order, tbsCItem.getTbsItem().getId(), shipIdIterator.next(),
								quantityIterator.next());
					}
				}
				
				if (storeOrderToBeUpdated) {
					logDebug("Removing Assembly and Delivery commerce items and re creating for order :: " + order.getId());
					((BBBOrderManager) getOrderManager()).removeALLDeliveryAssemblyCIFromOrderBySG(order, BBBCoreConstants.DESTINATION_ORDER);
					((BBBOrderManager) getOrderManager()).createDeliveryAssemblyCI(order);
				}
				
				/*
				 * Below code is for changing SDD shipping method to standard
				 * shipping method for non transient user who has selected SDD
				 * shipping method from store application but didn't place
				 * order.
				 */
				for (Object shippingGroup : order.getShippingGroups()) {
					if (shippingGroup instanceof BBBHardGoodShippingGroup
							&& BBBCoreConstants.SDD.equals(((BBBHardGoodShippingGroup) shippingGroup)
									.getShippingMethod())) {
						((BBBHardGoodShippingGroup) shippingGroup)
								.setShippingMethod(BBBCoreConstants.SHIP_METHOD_STANDARD_ID);
					}
				}
				repriceOrder(order, pProfile, pPricingModelHolder, getUserLocale(pRequest, pResponse), getRepriceOrderPricingOp());

			} catch (CommerceException ce) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "CommerceException in TBSProfileFormHandler.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1171 ), ce);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.TBS_PROFILE_TOOLS,
							methodName);
			} catch (IOException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "IOException in TBSProfileFormHandler.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.TBS_PROFILE_TOOLS,
							methodName);
			} catch (BBBBusinessException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException in TBSProfileTools.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.TBS_PROFILE_TOOLS,
							methodName);
			} catch (BBBSystemException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in TBSProfileTools.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.TBS_PROFILE_TOOLS,
							methodName);
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(LogMessageFormatter.formatMessage(null, "RepositoryException in TBSProfileTools.postLoginUser() while Login", BBBCoreErrorConstants.ACCOUNT_ERROR_1172 ), e);
				}
					isMonitorCanceled = true;
					BBBPerformanceMonitor.cancel(
							BBBPerformanceConstants.TBS_PROFILE_TOOLS,
							methodName);
			}
			
			((TBSOrderTools)this.getOrderManager().getOrderTools()).removeAutoWaiveDetailsFromCommerceItem(order);
			((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(order,null);
		}

	}

	private class TbsCItemInfo {

		TBSCommerceItem tbsItem;
		List<String> shipGroupIds;
		List<Long> quantities;

		/**
		 * @return the tbsItem
		 */
		public TBSCommerceItem getTbsItem() {
			return tbsItem;
		}

		/**
		 * @param pTbsItem
		 *            the tbsItem to set
		 */
		public void setTbsItem(TBSCommerceItem pTbsItem) {
			tbsItem = pTbsItem;
		}

		/**
		 * @return the shipGroupId
		 */
		public List<String> getShipGroupIds() {
			if (shipGroupIds == null) {
				shipGroupIds = new ArrayList<String>();
			}
			return shipGroupIds;
		}

		/**
		 * @return the quantity
		 */
		public List<Long> getQuantities() {
			if (quantities == null) {
				quantities = new ArrayList<Long>();
			}
			return quantities;
		}
	}

}
