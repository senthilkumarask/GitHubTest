package com.bbb.commerce.droplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.TaxPriceInfo;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.order.TBSOrder;
import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.commerce.vo.PriceOverridenVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;

public class TBSOverridenItemsDroplet extends DynamoServlet {
	
	private PricingTools mPricingTools;
	/**
	 * @return the pricingTools
	 */
	public PricingTools getPricingTools() {
		return mPricingTools;
	}
	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public void setPricingTools(PricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}


	@SuppressWarnings({ "unchecked" })
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("TBSOverridenItemsDroplet :: service() method :: START");
		
		TBSOrder order = (TBSOrder) pRequest.getObjectParameter("order");
		
		List<CommerceItem> cItems = null;
		List<ShippingGroup> sGroups = null;
		PriceOverridenVO priceOverrideVO = null;
		TBSItemInfo itemInfo = null;
		TBSShippingInfo shipInfo = null;
		RepositoryItem skuItem = null;
		double savedUnitAmount = 0.0;
		double listPrice = 0.0;
		boolean startFlag = true;
		
		if(order != null){
			cItems = order.getCommerceItems();
			sGroups = order.getShippingGroups();
		}
		if(cItems == null || cItems.isEmpty()){
			vlogDebug("No Price overridden items");
			pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest, pResponse);
			return;
		}
		if( !order.isTBSApprovalRequired() ){
			vlogDebug("No Price overridden items");
			pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest, pResponse);
			return;
		}
		for (CommerceItem commerceItem : cItems) {
			priceOverrideVO = new PriceOverridenVO();
			
			skuItem = (RepositoryItem) commerceItem.getAuxiliaryData().getCatalogRef();
			if(commerceItem instanceof LTLAssemblyFeeCommerceItem){
				itemInfo = ((LTLAssemblyFeeCommerceItem)commerceItem).getTBSItemInfo();
			}
			if(commerceItem instanceof LTLDeliveryChargeCommerceItem){
				itemInfo = ((LTLDeliveryChargeCommerceItem)commerceItem).getTBSItemInfo();
			}
			if(commerceItem instanceof GiftWrapCommerceItem){
				itemInfo = ((GiftWrapCommerceItem)commerceItem).getTBSItemInfo();
			}
			if(commerceItem instanceof TBSCommerceItem){
				itemInfo = ((TBSCommerceItem)commerceItem).getTBSItemInfo();
			}
			if(itemInfo != null && itemInfo.isPriceOveride()){
				if(startFlag){
					pRequest.serviceLocalParameter(BBBCoreConstants.OUTPUT_START, pRequest, pResponse);
					startFlag = false;
				}
				priceOverrideVO.setCommerceId(commerceItem.getId());
				listPrice = commerceItem.getPriceInfo().getListPrice();
				if(listPrice == 0.0){
					listPrice = commerceItem.getPriceInfo().getSalePrice();
				}
				savedUnitAmount = listPrice - itemInfo.getOverridePrice();
				priceOverrideVO.setOverrideAmount(itemInfo.getOverridePrice());
				priceOverrideVO.setOverridePercent(
						getPricingTools().round(BigDecimal.valueOf(savedUnitAmount).multiply(BigDecimal.valueOf(100)).doubleValue() / listPrice, 2));
				priceOverrideVO.setProductName((String)skuItem.getPropertyValue("displayName"));
				pRequest.setParameter("priceOverrideVO", priceOverrideVO);
				pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
			}
			itemInfo = null;
		}
		for (ShippingGroup shippingGroup : sGroups) {
			
			if(shippingGroup instanceof BBBHardGoodShippingGroup){
				shipInfo = ((BBBHardGoodShippingGroup)shippingGroup).getTbsShipInfo();
			} else if(shippingGroup instanceof BBBStoreShippingGroup){
				shipInfo = ((BBBStoreShippingGroup)shippingGroup).getTbsShipInfo();
			}
			if(shipInfo != null && shipInfo.isShipPriceOverride()){
				priceOverrideVO = new PriceOverridenVO();
				vlogDebug("ShippingGroup Override :: "+shippingGroup.getId());
				if(startFlag){
					pRequest.serviceLocalParameter(BBBCoreConstants.OUTPUT_START, pRequest, pResponse);
					startFlag = false;
				}
				priceOverrideVO.setCommerceId(shippingGroup.getId());
				listPrice = shippingGroup.getPriceInfo().getRawShipping();
				savedUnitAmount = listPrice;
				priceOverrideVO.setOverrideAmount(savedUnitAmount);
				priceOverrideVO.setOverridePercent(100);
				priceOverrideVO.setProductName("Shipping");
				pRequest.setParameter("priceOverrideVO", priceOverrideVO);
				pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
			}
			if(shipInfo != null && shipInfo.isTaxOverride()){
				priceOverrideVO = new PriceOverridenVO();
				vlogDebug("Tax Override :: "+shippingGroup.getId());
				if(startFlag){
					pRequest.serviceLocalParameter(BBBCoreConstants.OUTPUT_START, pRequest, pResponse);
					startFlag = false;
				}
				priceOverrideVO.setCommerceId(shippingGroup.getId());
				Object taxObj = order.getTaxPriceInfo().getShippingItemsTaxPriceInfos().get(shippingGroup.getId());
				if(taxObj != null){
					TaxPriceInfo taxInfo = (TaxPriceInfo) taxObj;
					listPrice = taxInfo.getAmount();
				}
				savedUnitAmount = listPrice;
				priceOverrideVO.setOverrideAmount(savedUnitAmount);
				priceOverrideVO.setOverridePercent(100);
				priceOverrideVO.setProductName("Tax");
				pRequest.setParameter("priceOverrideVO", priceOverrideVO);
				pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
			}
			if(shipInfo != null && shipInfo.isSurchargeOverride()){
				priceOverrideVO = new PriceOverridenVO();
				vlogDebug("Surcharge Override :: "+shippingGroup.getId());
				if(startFlag){
					pRequest.serviceLocalParameter(BBBCoreConstants.OUTPUT_START, pRequest, pResponse);
					startFlag = false;
				}
				priceOverrideVO.setCommerceId(shippingGroup.getId());
				listPrice = shipInfo.getSurchargeValue();
				priceOverrideVO.setOverrideAmount(listPrice);
				priceOverrideVO.setProductName("Surcharge");
				pRequest.setParameter("priceOverrideVO", priceOverrideVO);
				pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
			}
			shipInfo = null;
		}
		if(!startFlag){
			pRequest.serviceLocalParameter(BBBCoreConstants.OUTPUT_END, pRequest, pResponse);
		}

		vlogDebug("TBSOverridenItemsDroplet :: service() method :: END");
	}
}
