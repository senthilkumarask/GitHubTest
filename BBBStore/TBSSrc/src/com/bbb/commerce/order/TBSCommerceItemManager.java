package com.bbb.commerce.order;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.InvalidTypeException;
import atg.commerce.order.Order;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;

import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.common.vo.PromotionVO;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;
import com.bbb.utils.BBBUtility;

/**
 * This class extends the BBBCommerceItemManager for get the item level saved amount.
 *  
 */
public class TBSCommerceItemManager extends BBBCommerceItemManager {
	
	/**
	 * This method takes the TBSCommerceItem and get its priceInfo object and
	 * sets into the PriceInfoVO.
	 * 
	 * @param BBBCommerceItem
	 * @return PriceInfoVO
	 */
	@SuppressWarnings("unchecked")
    public PriceInfoVO getItemPriceInfo(BBBCommerceItem pItem) {
		vlogDebug("TBSCommerceItemManager :: getItemPriceInfo() method :: START");
		
		
		PriceInfoVO priceInfoVO = new PriceInfoVO();
		long undiscountedItemsCount = 0;
		
		List<PromotionVO> itemPromotionVOList = priceInfoVO.getItemPromotionVOList();
		if (pItem != null) {
			double savedAmount = 0.0;
			double savedPercentage = 0.0;
			double savedUnitAmount = 0.0;
			double savedUnitPercentage = 0.0;
			
			priceInfoVO.setItemCount((int) pItem.getQuantity());
			ItemPriceInfo priceInfo = pItem.getPriceInfo();
			if(priceInfo == null) { 
				vlogDebug("Price info of commerceItem :: "+pItem.getId() +" is null");
			    return priceInfoVO;
			}
            priceInfoVO.setRawAmount(priceInfo.getRawTotalPrice());
            double listPrice = priceInfo.getListPrice();
			if(listPrice == 0.0){
				listPrice = priceInfo.getSalePrice();
			}
			savedAmount = BigDecimal.valueOf(priceInfo.getListPrice()).multiply(BigDecimal.valueOf(pItem.getQuantity())).doubleValue() - priceInfo.getAmount();				
			priceInfoVO.setUnitListPrice(listPrice);
			if (priceInfo.getSalePrice() > 0) {
				
				priceInfoVO.setTotalSavedAmount(savedAmount);
				priceInfoVO.setUnitSalePrice(priceInfo.getSalePrice());
				savedUnitAmount = priceInfo.getListPrice() - priceInfo.getSalePrice();
				priceInfoVO.setUnitSavedAmount(savedUnitAmount);
			} 
			priceInfoVO.setTotalAmount(BigDecimal.valueOf(priceInfo.getListPrice()).multiply(BigDecimal.valueOf(pItem.getQuantity())).doubleValue() - savedAmount);
			
			if (savedAmount > 0) {
				vlogDebug("Saved amount of :: "+pItem.getId() +" is :: "+savedAmount);
				priceInfoVO.setTotalSavedAmount(savedAmount);
				savedPercentage  = getPricingTools().round(BigDecimal.valueOf(savedAmount).multiply(BigDecimal.valueOf(100)).doubleValue() / BigDecimal.valueOf(priceInfo.getListPrice()).multiply(BigDecimal.valueOf(pItem.getQuantity())).doubleValue(), 2);
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
		}
		vlogDebug("TBSCommerceItemManager :: getItemPriceInfo() method :: END");
		return priceInfoVO;
	}
	
	ShippingGroupCommerceItemRelationship addItemQuantityToShippingGroupInternal(
			Order pOrder, String pCommerceItemId, String pShippingGroupId,
			long pQuantity) throws CommerceException {
		if (pOrder == null) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidOrderParameter",
					"atg.commerce.order.OrderResources", sResourceBundle));
		}
		if (pCommerceItemId == null) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidCommerceItemIdParameter",
					"atg.commerce.order.OrderResources", sResourceBundle));
		}
		if (pShippingGroupId == null) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidShippingGroupIdParameter",
					"atg.commerce.order.OrderResources", sResourceBundle));
		}
		if (pQuantity <= 0L) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidQuantityParameter",
					"atg.commerce.order.OrderResources", sResourceBundle));
		}

		ShippingGroupCommerceItemRelationship rel = null;
		CommerceItem item = pOrder.getCommerceItem(pCommerceItemId);
		boolean exists = true;
		try {
			rel = getShippingGroupManager()
					.getShippingGroupCommerceItemRelationship(pOrder,
							pCommerceItemId, pShippingGroupId);
		} catch (RelationshipNotFoundException e) {
			exists = false;
		}

		long unassignedQty = getUnassignedQuantityForCommerceItem(item);
		if (unassignedQty == 0L)
			unassignedQty = getShippingGroupManager()
					.getRemainingQuantityForShippingGroup(item);
		if (pQuantity > unassignedQty) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"QuantityTooBig", "atg.commerce.order.OrderResources",
					sResourceBundle));
		}

		if (exists) {
			if (rel.getRelationshipType() != 100) {
				throw new InvalidTypeException(ResourceUtils.getMsgResource(
						"IncompatibleRelationshipType",
						"atg.commerce.order.OrderResources", sResourceBundle));
			}
			rel.setQuantity(rel.getQuantity() + pQuantity);
		} else {
			rel = (ShippingGroupCommerceItemRelationship) getOrderTools()
					.createRelationship("shippingGroupCommerceItem");
			rel.setRelationshipType(100);

			getOrderTools().initializeRelationship(pOrder, rel,
					pShippingGroupId, pCommerceItemId);
			rel.setQuantity(pQuantity);
		}

		return rel;
	}
	
	public long getUnassignedQuantityForCommerceItem(CommerceItem pItem)
	{
	  long unassignedQty = pItem.getQuantity();

	  Iterator iter = pItem.getShippingGroupRelationships().iterator();
	  while (iter.hasNext()) {
	    ShippingGroupCommerceItemRelationship sgCiRel = (ShippingGroupCommerceItemRelationship)iter.next();
	    if (sgCiRel.getRelationshipType() == 100)
	      unassignedQty -= sgCiRel.getQuantity();
	    if (sgCiRel.getRelationshipType() == 101) {
	      return 0L;
	    }
	  }
	  return unassignedQty;
	}
	
	
	/**
	 * override the shouldMergeItems method to check if merging is required or not
	 * If existing item and new item has got same store id or registry id combination then retun true
	 * 
	 * @param pExistingItem
	 * @param pNewItem
	 * 
	 */
	@Override
	protected boolean shouldMergeItems(CommerceItem pExistingItem, CommerceItem pNewItem) {
		
		boolean doMergeItems = super.shouldMergeItems(pExistingItem, pNewItem);
		
		if(doMergeItems && pExistingItem instanceof TBSCommerceItem && pNewItem instanceof TBSCommerceItem){

			TBSCommerceItem existingTBSItem = (TBSCommerceItem) pExistingItem;
			TBSCommerceItem newTBSItem = (TBSCommerceItem) pNewItem;

			TBSItemInfo existingTBSInfo =  existingTBSItem.getTBSItemInfo();
			TBSItemInfo newTBSInfo =  newTBSItem.getTBSItemInfo();

			if((existingTBSItem.isCMO() || existingTBSItem.isKirsch()) && (newTBSItem.isCMO() || newTBSItem.isKirsch()) && existingTBSInfo != null && newTBSInfo != null){

				if(!StringUtils.isBlank(existingTBSInfo.getConfigId()) && !StringUtils.isBlank(newTBSInfo.getConfigId())){

					if(! existingTBSInfo.getConfigId().equals(newTBSInfo.getConfigId())){
						return false;
					}
				}
			}
		}
		
		
		
		if(pExistingItem instanceof BBBCommerceItem && pNewItem instanceof BBBCommerceItem){
			if(doMergeItems){
				if(compareOverridePrice((BBBCommerceItem)pExistingItem, (BBBCommerceItem)pNewItem)){
					doMergeItems = true;
				}else{
					doMergeItems = false;
				}
				// condition to create the new item for multi ship
				if(doMergeItems){
					if(compareMultiShip((BBBCommerceItem)pExistingItem, (BBBCommerceItem)pNewItem)){
						doMergeItems = true;
					}else{
						doMergeItems = false;
					}
				}
			}
			vlogDebug(new StringBuilder().append("shouldMergeItems() returned ").append(doMergeItems).toString());
		}
		return doMergeItems;
	}
	
	/**
	 * This method will be used while ship to multiple people.
	 * @param pExistingItem
	 * @param pNewItem
	 * @return
	 */
	private boolean compareMultiShip(BBBCommerceItem pExistingItem, BBBCommerceItem pNewItem) {
		boolean returnFlag = true;
		TBSCommerceItem tbsnewItem = null;
		TBSCommerceItem tbsExistingItem = null;
		
		if(pNewItem instanceof TBSCommerceItem ) {
			tbsnewItem = (TBSCommerceItem)pNewItem;
		}
		if(pExistingItem instanceof TBSCommerceItem ) {
    		tbsExistingItem = (TBSCommerceItem)pExistingItem;
		}
		if(tbsExistingItem.getSplitSequence() !=  tbsnewItem.getSplitSequence()){
			returnFlag = false;
		}
		return returnFlag;
	}

	/**
	 * compare overridePrice of existingItem and newItem
	 * 
	 * 
	 * @param pExistingItem
	 * @param pNewItem
	 * @return
	 */
	private boolean compareOverridePrice(BBBCommerceItem pExistingItem, BBBCommerceItem pNewItem){
		TBSItemInfo existingItemInfo = null;
		TBSItemInfo newItemInfo = null;
		boolean returnFlag = false;
		if(pExistingItem instanceof TBSCommerceItem ) {
    		TBSCommerceItem tbsExistingItem = (TBSCommerceItem)pExistingItem;
    		existingItemInfo = tbsExistingItem.getTBSItemInfo();
		}
		if(pNewItem instanceof TBSCommerceItem ) {
			TBSCommerceItem tbsnewItem = (TBSCommerceItem)pNewItem;
			newItemInfo = tbsnewItem.getTBSItemInfo();
		}
		if(existingItemInfo == null && newItemInfo == null ){
			returnFlag = true;
		} 
		if(existingItemInfo == null && (newItemInfo != null && newItemInfo.getOverridePrice() > 0.0)){
			returnFlag = true;
		} else if(existingItemInfo != null && newItemInfo != null){
			if(existingItemInfo.getOverridePrice() == newItemInfo.getOverridePrice()){
				returnFlag = true;
			} else {
				returnFlag = false;
			}
		}
		return returnFlag;
	}

	protected CommerceItem mergeOrdersCopyCommerceItem(Order pSrcOrder,
			Order pDstOrder, CommerceItem pItem) throws CommerceException {	

		CommerceItem destItem = super.mergeOrdersCopyCommerceItem(pSrcOrder, pDstOrder, pItem);
		
		if( pItem instanceof TBSCommerceItem ) {
			TBSItemInfo itemInfo = ((TBSCommerceItem)pItem).getTBSItemInfo();
			if( itemInfo != null ) {
				
				TBSItemInfo newInfo = ((TBSOrderTools)getOrderTools()).createTBSItemInfo();
				newInfo.setConfigId(itemInfo.getConfigId());
				newInfo.setCompetitor(itemInfo.getCompetitor());
				newInfo.setOverideReason(itemInfo.getOverideReason());
				newInfo.setOverridePrice(itemInfo.getOverridePrice());
				newInfo.setOverrideQuantity(itemInfo.getOverrideQuantity());
				newInfo.setPriceOveride(itemInfo.isPriceOveride());
				newInfo.setProductDesc(itemInfo.getProductDesc());
				newInfo.setProductImage(itemInfo.getProductImage());
				newInfo.setCost(itemInfo.getCost());
				newInfo.setErrorCode(itemInfo.getErrorCode());
				newInfo.setErrorName(itemInfo.getErrorName());
				newInfo.setRetailPrice(itemInfo.getRetailPrice());
				((TBSCommerceItem)destItem).setTBSItemInfo(itemInfo);
			}
		}
		return destItem;
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
		String deliveryCommerceId = commItem.getDeliveryItemId();
		String assemblyCommerceId = commItem.getAssemblyItemId();
		if (BBBUtility.isNotEmpty(deliveryCommerceId)) {
			LTLDeliveryChargeCommerceItem delCommItem = (LTLDeliveryChargeCommerceItem) getCommerceItemFromOrder(order,deliveryCommerceId);
			if(null != commItem) {
				double rawTotalPrice = delCommItem.getPriceInfo().getRawTotalPrice();
				double finalAmount = delCommItem.getPriceInfo().getAmount();
				double surchargeSaving = rawTotalPrice - finalAmount;
				priceInfoVO.setDeliverySurchargeSaving(surchargeSaving);
				if(delCommItem.getTBSItemInfo() != null){
					double overridePrice = delCommItem.getTBSItemInfo().getOverridePrice();
					int overrideQty = delCommItem.getTBSItemInfo().getOverrideQuantity();
					rawTotalPrice = overridePrice * overrideQty;
				}
				priceInfoVO.setDeliverySurcharge(rawTotalPrice);
				priceInfoVO.setDeliverySurchargeProrated(finalAmount);
			}
		}
		if (BBBUtility.isNotEmpty(assemblyCommerceId)) {
			LTLAssemblyFeeCommerceItem assemblyCommItem =  (LTLAssemblyFeeCommerceItem) getCommerceItemFromOrder(order,assemblyCommerceId);
			if(null != assemblyCommItem) {
				double rawTotalPrice = assemblyCommItem.getPriceInfo().getRawTotalPrice();
				double finalAmount = assemblyCommItem.getPriceInfo().getAmount();
				if(assemblyCommItem.getTBSItemInfo() != null){
					double overridePrice = assemblyCommItem.getTBSItemInfo().getOverridePrice();
					int overrideQty = assemblyCommItem.getTBSItemInfo().getOverrideQuantity();
					finalAmount = overridePrice * overrideQty;
				}
				priceInfoVO.setAssemblyFee(finalAmount);
				priceInfoVO.setAssemblyFeeSaving(rawTotalPrice - finalAmount);
			}
		}
		return priceInfoVO;
	}

}
