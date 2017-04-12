package com.bbb.commerce.pricing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import atg.core.util.Range;

import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;

public class TBSPricingManager extends BBBPricingManager {


	/**
	 * This method is overridden to add the overridden shipping and Tax amounts to Saved amount
	 */
	@SuppressWarnings("unchecked")
	public PriceInfoVO getShippingPriceInfo(final ShippingGroup shippingGroup, final OrderImpl pOrder) {
		final PriceInfoVO priceInfoVO = new PriceInfoVO();
		if (shippingGroup != null) {
			List<ShippingGroupCommerceItemRelationship> commerceItemRelationshipList = shippingGroup.getCommerceItemRelationships();
			double giftWrapPrice = 0.0;
			double ecoFee = 0.0;
			TaxPriceInfo shippingTaxInfo = null;
			long itemCount = 0;
			double shippingGroupItemsSavedAmount = 0.0;
			double shippingGroupItemsTotalListPrice = 0.0;
			double shippingGroupSavedPercentage = 0.0;
			double totalAmount = 0.0;
			double shippingGroupItemsTotal = 0.0;
			double surchargeSavings = 0.0;
			double shippingSavings = 0.0;
			
			for (ShippingGroupCommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {
				CommerceItem commerceItem = commerceItemRelationship.getCommerceItem();
				if (commerceItem instanceof BBBCommerceItem && commerceItem.getPriceInfo() != null) {
					itemCount += commerceItemRelationship.getQuantity();
					Range relationshipRange = commerceItemRelationship.getRange();
					List<UnitPriceBean> priceBeans= getPricingTools().generatePriceBeans(commerceItemRelationship.
														getCommerceItem().getPriceInfo().getCurrentPriceDetailsForRange(relationshipRange));
					for(UnitPriceBean unitPriceBean : priceBeans) {
						shippingGroupItemsTotal += BigDecimal.valueOf(unitPriceBean.getUnitPrice()).multiply(BigDecimal.valueOf(unitPriceBean.getQuantity())).doubleValue();						
					}
					
					double itemSavedAmount = commerceItemRelationship.getRawtotalByAverage() - commerceItemRelationship.getAmountByAverage();
					shippingGroupItemsSavedAmount += itemSavedAmount;

					shippingGroupItemsTotalListPrice += BigDecimal.valueOf(commerceItem.getPriceInfo().getListPrice()).multiply(BigDecimal.valueOf(itemCount)).doubleValue() ;

				}
				if (commerceItem != null && commerceItem.getPriceInfo() != null && commerceItem instanceof GiftWrapCommerceItem) {
					giftWrapPrice += commerceItem.getPriceInfo().getAmount();
					double itemSavedAmount = commerceItemRelationship.getRawtotalByAverage() - commerceItemRelationship.getAmountByAverage();
					shippingGroupItemsSavedAmount += itemSavedAmount;
				}
				
				if (commerceItem != null && commerceItem.getPriceInfo() != null && commerceItem instanceof EcoFeeCommerceItem) {
					ecoFee = ecoFee + commerceItem.getPriceInfo().getAmount();
				}
			}
			
			if (pOrder != null && pOrder.getTaxPriceInfo() != null && pOrder.getTaxPriceInfo().getShippingItemsTaxPriceInfos() != null) {
				shippingTaxInfo = (TaxPriceInfo) pOrder.getTaxPriceInfo().getShippingItemsTaxPriceInfos().get(shippingGroup.getId());
				
				if (shippingTaxInfo != null) {
					priceInfoVO.setShippingLevelTax(shippingTaxInfo.getAmount());
					priceInfoVO.setShippingCountyLevelTax(shippingTaxInfo.getCountyTax());
					priceInfoVO.setShippingStateLevelTax(shippingTaxInfo.getStateTax());
					totalAmount += shippingTaxInfo.getAmount();
				}
			} else {
				priceInfoVO.setShippingCountyLevelTax(0);
				priceInfoVO.setShippingStateLevelTax(0);
				priceInfoVO.setShippingLevelTax(0);
			}
			
			getPricingTools().fillAdjustments(priceInfoVO, shippingGroup);
			
			if (shippingGroupItemsTotalListPrice > 0) {
				shippingGroupSavedPercentage = getPricingTools()
						.round(BigDecimal.valueOf(shippingGroupItemsSavedAmount).multiply(BigDecimal.valueOf(100)).doubleValue() / shippingGroupItemsTotalListPrice,
								2);
			}
			priceInfoVO.setTotalSavedPercentage(shippingGroupSavedPercentage);
			priceInfoVO.setGiftWrapTotal(giftWrapPrice);
			priceInfoVO.setEcoFeeTotal(ecoFee);
			BBBShippingPriceInfo shippingPriceInfo = (BBBShippingPriceInfo) shippingGroup.getPriceInfo();
			TBSShippingInfo shipInfo = null;
			if (shippingPriceInfo != null) {
				priceInfoVO.setTotalSurcharge(shippingPriceInfo.getSurcharge());
				priceInfoVO.setTotalShippingAmount(shippingPriceInfo.getFinalShipping());
				priceInfoVO.setRawShippingTotal(shippingPriceInfo.getRawShipping());
				totalAmount += shippingPriceInfo.getFinalShipping() + shippingPriceInfo.getSurcharge();
//				surchargeSavings += shippingPriceInfo.getSurcharge() - shippingPriceInfo.getFinalSurcharge();
				shippingSavings+= shippingPriceInfo.getRawShipping() - shippingPriceInfo.getFinalShipping(); 
				if(shippingGroup instanceof BBBHardGoodShippingGroup){
					shipInfo = ((BBBHardGoodShippingGroup)shippingGroup).getTbsShipInfo();
				} else if(shippingGroup instanceof BBBStoreShippingGroup){
					shipInfo = ((BBBStoreShippingGroup)shippingGroup).getTbsShipInfo();
				}
				shippingGroupItemsSavedAmount = getPricingTools().round(shippingGroupItemsSavedAmount);
				if(shipInfo != null && shipInfo.isShipPriceOverride()){
					List<PricingAdjustment> adjustments = shippingGroup.getPriceInfo().getAdjustments();
					for (PricingAdjustment pricingAdjustment : adjustments) {
						if(pricingAdjustment.getPricingModel() == null && pricingAdjustment.getAdjustmentDescription().equals("Shipping Override")){
							shippingGroupItemsSavedAmount += Math.abs(pricingAdjustment.getAdjustment());
						}
					}
				}
				if(shipInfo != null && shipInfo.isSurchargeOverride()){
					List<PricingAdjustment> adjustments = shippingGroup.getPriceInfo().getAdjustments();
					for (PricingAdjustment pricingAdjustment : adjustments) {
						if(pricingAdjustment.getPricingModel() == null && pricingAdjustment.getAdjustmentDescription().equals("Surcharge Override")){
							shippingGroupItemsSavedAmount += Math.abs(pricingAdjustment.getAdjustment());
							surchargeSavings += Math.abs(pricingAdjustment.getAdjustment());
						}
					}
				}
				if(pOrder != null && shipInfo != null && shipInfo.isTaxOverride()){
					Map<String, TaxPriceInfo> taxInfos = pOrder.getTaxPriceInfo().getShippingItemsTaxPriceInfos();
					TaxPriceInfo taxPriceInfo = taxInfos.get(shippingGroup.getId());
					if(taxPriceInfo != null){
						shippingGroupItemsSavedAmount += taxPriceInfo.getCityTax();
						shippingGroupItemsSavedAmount += taxPriceInfo.getCountryTax();
						shippingGroupItemsSavedAmount += taxPriceInfo.getCountyTax();
						shippingGroupItemsSavedAmount += taxPriceInfo.getStateTax();
					}
					//Below three line are used for setting the tax values for canada site.
					priceInfoVO.setShippingCountyLevelTax(0);
					priceInfoVO.setShippingStateLevelTax(0);
					priceInfoVO.setShippingLevelTax(0);
				}
			}
			totalAmount += shippingGroupItemsTotal;
			totalAmount += giftWrapPrice;
			totalAmount += ecoFee;
			priceInfoVO.setItemCount((int) itemCount);
			priceInfoVO.setTotalSavedAmount(shippingGroupItemsSavedAmount);
			priceInfoVO.setShippingGroupItemsTotal(shippingGroupItemsTotal);
			priceInfoVO.setTotalAmount(totalAmount);
			priceInfoVO.setSurchargeSavings(surchargeSavings);
			priceInfoVO.setShippingSavings(shippingSavings);
			
            priceInfoVO.setFinalShippingCharge(priceInfoVO.getRawShippingTotal() - priceInfoVO.getShippingSavings());

			// LTL : Shipping level delivery, assembly and surcharge savings
			Double shippingDeliverySurcharge = 0.0;
			Double shippingDeliverySurchargeSaving = 0.0;
			Double shippingAssemblyFee = 0.0;
			if(shippingGroup instanceof BBBHardGoodShippingGroup){
				//Retrieve relationship of LTL commerce item with its assembly and delivery surcharge commerce items.
				List<BBBShippingGroupCommerceItemRelationship> sgciRelList = shippingGroup.getCommerceItemRelationships();
				for(BBBShippingGroupCommerceItemRelationship sgciRel : sgciRelList){
					if(sgciRel.getCommerceItem() instanceof BBBCommerceItem){
						if(((BBBCommerceItem)sgciRel.getCommerceItem()).isLtlItem()){
							PriceInfoVO ciPriceInfoVo = new PriceInfoVO();
							String commerceItem = sgciRel.getCommerceItem().getId();
							ciPriceInfoVo = getCommerceItemManager().getLTLItemPriceInfo(commerceItem,ciPriceInfoVo, pOrder);
							shippingDeliverySurcharge+=ciPriceInfoVo.getDeliverySurcharge();
							shippingDeliverySurchargeSaving+=ciPriceInfoVo.getDeliverySurchargeSaving();
							shippingAssemblyFee+=ciPriceInfoVo.getAssemblyFee();
						}else{
							break;
						}
					}
				}
				totalAmount = totalAmount + shippingDeliverySurcharge - shippingDeliverySurchargeSaving + shippingAssemblyFee;
			}
			priceInfoVO.setTotalAmount(totalAmount);
			priceInfoVO.setTotalDeliverySurcharge(shippingDeliverySurcharge);
			priceInfoVO.setDeliverySurchargeSaving(shippingDeliverySurchargeSaving);
			priceInfoVO.setAssemblyFee(shippingAssemblyFee);
			
		}
		return priceInfoVO;
	}
}
