package com.bbb.commerce.pricing;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.TaxPriceInfo;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.utils.BBBUtility;

public class TBSPricingTools extends BBBPricingTools {

	private final String SHIPPING_SURCHARGE_CAP = "shippingSurchargeCap";
	
	@Override
    public double calculateShippingCost(final String siteId, final String shippingMethod,
			final HardgoodShippingGroup hardgoodShippingGroup, String sddShippingZip)
			throws PricingException {

    	// Force pricing to come from WEB objects, there are no TBS shipping price objects
		String webSiteId = siteId;
		
		if( siteId.equals("TBS_BedBathUS" ) ) {
			webSiteId = "BedBathUS";
		}
		else if( siteId.equals("TBS_BedBathCanada") ) {
			webSiteId = "BedBathCanada";
		}
		else if( siteId.equals("TBS_BuyBuyBaby") ) {
			webSiteId = "BuyBuyBaby";
		}
		
		return super.calculateShippingCost(webSiteId, shippingMethod, hardgoodShippingGroup, sddShippingZip);
    }
    
    
	public double getTotalSavings(double onlineTotalRawAmount,
			double onlineDiscountedAmount, double shippingSavedAmount,
			double deliverySurchargeRawTotal, double deliverySurchargeTotal,
			double storeTotalActualAmount, double storeTotalRawAmount) {
		// TODO Auto-generated method stub
		return ((storeTotalActualAmount+onlineTotalRawAmount) - (onlineDiscountedAmount+storeTotalRawAmount)) + shippingSavedAmount + (deliverySurchargeRawTotal - deliverySurchargeTotal);
		
	}
	

	public double calculateShippingCost(String pSiteId, String pShipMethodId, Double pLineAmt,
			boolean isGiftItem, String state, String catalogRefId) throws PricingException {
		// Force pricing to come from WEB objects, there are no TBS shipping
		// price objects
		String webSiteId = pSiteId;
		double shippingAmount = 0.0;

		if (pSiteId.equals("TBS_BedBathUS")) {
			webSiteId = "BedBathUS";
		} else if (pSiteId.equals("TBS_BedBathCanada")) {
			webSiteId = "BedBathCanada";
		} else if (pSiteId.equals("TBS_BuyBuyBaby")) {
			webSiteId = "BuyBuyBaby";
		}
		
		try {
			if (isGiftItem || this.getCatalogUtil().isFreeShipping(webSiteId,
					catalogRefId, pShipMethodId)) {
				shippingAmount = this.getCatalogUtil().shippingCostForGiftCard(webSiteId,
						pShipMethodId);
			} else {
				shippingAmount = shippingAmount
						+ this.getCatalogUtil().getShippingFee(webSiteId,
								pShipMethodId, state,
								pLineAmt, null);
			}
		} catch (BBBSystemException e) {
			if (this.isLoggingError()) {
				this.logError("Error getting shipping fee", e);
			}
			throw new PricingException(e);
		} catch (BBBBusinessException e) {
			if (this.isLoggingError()) {
				this.logError("Error getting shipping fee", e);
			}
			throw new PricingException(e);
		}
		
		return shippingAmount;
	}
	
	/**
	 * This method is overridden to separate the BOPUS item total in the order total
	 *
	 * @param Order
	 * @param pReq
	 * @return PriceInfoVO
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public PriceInfoVO getOrderPriceInfo(final OrderImpl pOrder) {

		final PriceInfoVO priceInfoVO = new PriceInfoVO();
		if ((pOrder != null) && (pOrder.getPriceInfo() != null)) {
		    final BBBOrderPriceInfo orderPriceInfo = (BBBOrderPriceInfo) pOrder.getPriceInfo();
            priceInfoVO.setOrderPriceInfo(orderPriceInfo);

			int onlinePurchasedItemCount = 0;
			int storePruchasedItemCount =0;
			double onlineTotalRawAmount = 0.0;
			double onlineOrderPreTaxTotal = 0.0;
			double totalSavedAmount  = 0.0;

			double storeTotalRawAmount  =0.0;//A1
			double storeTotalActualAmount  =0.0;//A1
			double onlineOrderTotal = 0.0;//I
			double orderTotal = 0.0;//J
			double onlineDiscountedAmount = 0.0;//C


			double totalShippingAmount = 0.0;//F
			double shippingSavedAmount = 0.0;//F1
			double totalShippingSurcharge = 0.0;//G
            double surchargeSavings = 0.0;
			double shippingSavings = 0.0;

			double onlineEcoFeeTotal = 0.0;//D
			double storeEcoFeeTotal = 0.0;//A2
			double giftWrapTotal = 0.0;//E
			boolean isFreeShipping = false;
			
			double deliverySurchargeTotal = 0.0;
		    double assemblyFeeTotal = 0.0;
		    double deliverySurchargeRawTotal = 0.0;
		    double assemblyFeeRawTotal = 0.0;
		    boolean isLtlOrder = false;
		    
			final List commerceItems = pOrder.getCommerceItems();
			if(commerceItems != null) {
    			for (final Iterator iterator = commerceItems.iterator(); iterator
                        .hasNext();) {
                    final CommerceItem item = (CommerceItem) iterator.next();
                    if (item != null) {
                        if(item instanceof BBBCommerceItem){
                            if(StringUtils.isEmpty(((BBBCommerceItem) item).getStoreId())) {
                                onlinePurchasedItemCount += item.getQuantity();
                                onlineTotalRawAmount += item.getPriceInfo().getRawTotalPrice();
                                onlineDiscountedAmount += item.getPriceInfo().getAmount();
                            } else {
                                storePruchasedItemCount += item.getQuantity();
                                storeTotalActualAmount += item.getPriceInfo().getRawTotalPrice();
                                storeTotalRawAmount += item.getPriceInfo().getAmount();
                            }
                        } else if(item instanceof EcoFeeCommerceItem) {
                            if(StringUtils.isEmpty(((EcoFeeCommerceItem) item).getStoreId())) {
                                onlineEcoFeeTotal += item.getPriceInfo().getAmount();
                            } else {
                                storeEcoFeeTotal += item.getPriceInfo().getAmount();
                            }
                        } else if (item instanceof GiftWrapCommerceItem){
                        	giftWrapTotal = giftWrapTotal + item.getPriceInfo().getAmount();
                        }else if (item instanceof LTLDeliveryChargeCommerceItem) {
                        	isLtlOrder = true;
                        	deliverySurchargeTotal += item.getPriceInfo().getAmount();
                        	if(((LTLDeliveryChargeCommerceItem) item).getTBSItemInfo() != null && ((LTLDeliveryChargeCommerceItem) item).getTBSItemInfo().getOverridePrice() > 0.0){
                        		deliverySurchargeRawTotal += ((LTLDeliveryChargeCommerceItem) item).getTBSItemInfo().getOverridePrice();
                        	} else {
                        		deliverySurchargeRawTotal += item.getPriceInfo().getRawTotalPrice();
                        	}
            			}else if (item instanceof LTLAssemblyFeeCommerceItem) {
            				assemblyFeeTotal += item.getPriceInfo().getAmount();
            				assemblyFeeRawTotal += item.getPriceInfo().getRawTotalPrice();
                        }
                    }
                }
			}
			 int shippingSurchargeCap = 0;
			 List<String> maxSurcahrgeCapList = null;

            try {
                maxSurcahrgeCapList = getCatalogUtil().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,
                                this.SHIPPING_SURCHARGE_CAP);
            } catch (final BBBSystemException e) {
                this.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : "
                                + this.SHIPPING_SURCHARGE_CAP);
            } catch (final BBBBusinessException e) {
                this.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : "
                                + this.SHIPPING_SURCHARGE_CAP);
            }

             if((null != maxSurcahrgeCapList) && (maxSurcahrgeCapList.size() > 0)) {
                 if(BBBUtility.isNumericOnly(maxSurcahrgeCapList.get(0).toString())) {
                     shippingSurchargeCap = Integer.parseInt(maxSurcahrgeCapList.get(0).toString());
                 }
             }
			// This iterates through all the shipping groups and sums totalShippingAmount and totalSurcharge
			for (final ShippingGroupImpl shippingGroup : (List<ShippingGroupImpl>)pOrder.getShippingGroups()){

				final BBBShippingPriceInfo priceInfo = (BBBShippingPriceInfo) shippingGroup.getPriceInfo();

                if(priceInfo != null){
                    totalShippingAmount += priceInfo.getRawShipping();
                    totalShippingSurcharge += priceInfo.getSurcharge();
            		if(shippingGroup instanceof BBBHardGoodShippingGroup){
            			this.fillAdjustments(priceInfoVO, shippingGroup);
                    	shippingSavedAmount += priceInfo.getRawShipping() - priceInfo.getFinalShipping();
                    	shippingSavedAmount += priceInfo.getSurcharge() - priceInfo.getFinalSurcharge();
                    	surchargeSavings += priceInfo.getSurcharge() - priceInfo.getFinalSurcharge();
                    	
                    	shippingSavings += priceInfo.getRawShipping() - priceInfo.getFinalShipping();
            		}
                }
			}
			// If order does not have ltl item, then make isShipMethodAvlForAllLTLItem false for non ltl order
			if((Double.compare(totalShippingAmount, 0) == BBBCoreConstants.ZERO) && (onlinePurchasedItemCount > 0)) {
				isFreeShipping = true;
			}
			totalSavedAmount = getTotalSavings(onlineTotalRawAmount,onlineDiscountedAmount,shippingSavedAmount,deliverySurchargeRawTotal,deliverySurchargeTotal,storeTotalActualAmount,storeTotalRawAmount);
			priceInfoVO.setStoreAmount(storeTotalRawAmount);
			priceInfoVO.setStoreEcoFeeTotal(storeEcoFeeTotal);
			priceInfoVO.setOnlinePurchaseTotal(onlineDiscountedAmount);
			priceInfoVO.setOnlineEcoFeeTotal(onlineEcoFeeTotal);
			priceInfoVO.setGiftWrapTotal(giftWrapTotal);
			priceInfoVO.setRawShippingTotal(totalShippingAmount);
			priceInfoVO.setTotalSurcharge(totalShippingSurcharge);
			final TaxPriceInfo taxPriceInfo = pOrder.getTaxPriceInfo();
            double taxAmount = 0.0;
            if(taxPriceInfo != null) {
                taxAmount = taxPriceInfo.getAmount();//H
                priceInfoVO.setTotalTax(taxAmount);

            }
            onlineOrderPreTaxTotal = (onlineDiscountedAmount + onlineEcoFeeTotal + giftWrapTotal + totalShippingAmount + totalShippingSurcharge + surchargeSavings + assemblyFeeTotal + deliverySurchargeTotal) - shippingSavedAmount;
            onlineOrderTotal = onlineOrderPreTaxTotal + taxAmount;
            orderTotal = onlineOrderTotal;
            if(taxPriceInfo != null)
            {
            	priceInfoVO.setShippingStateLevelTax(taxPriceInfo.getStateTax());
                priceInfoVO.setShippingCountyLevelTax(taxPriceInfo.getCountyTax());
            }
            priceInfoVO.setOnlineTotal(onlineOrderTotal);
            priceInfoVO.setOrderPreTaxAmout(onlineOrderPreTaxTotal);
            priceInfoVO.setTotalAmount(orderTotal);
            priceInfoVO.setFreeShipping(isFreeShipping);
            priceInfoVO.setHardgoodShippingGroupItemCount(onlinePurchasedItemCount);
            priceInfoVO.setStorePickupShippingGroupItemCount(storePruchasedItemCount);
            priceInfoVO.setTotalSavedAmount(totalSavedAmount);
            priceInfoVO.setSurchargeSavings(surchargeSavings);
            priceInfoVO.setShippingSavings(shippingSavings);
            
            priceInfoVO.setFinalShippingCharge(priceInfoVO.getRawShippingTotal() - priceInfoVO.getShippingSavings());
            
            //LTL start
            String thresholdAmountString;
			try {
				thresholdAmountString = this.getCatalogUtil().getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount").get(0);
				double thresholdAmount = Double.parseDouble(thresholdAmountString);
	            if(deliverySurchargeRawTotal>thresholdAmount){
	            	priceInfoVO.setMaxDeliverySurchargeReached(true);
	            	priceInfoVO.setMaxDeliverySurcharge(thresholdAmount);
	            }
	            priceInfoVO.setTotalDeliverySurcharge(deliverySurchargeRawTotal);
				priceInfoVO.setTotalAssemblyFee(assemblyFeeRawTotal);
			} catch (BBBSystemException e) {
				vlogError("Error getting thresholdAmount from config keys", e);
			} catch (BBBBusinessException e) {
				vlogError("Error getting thresholdAmount from config keys", e);
			}
            
            fillAllAdjustments(priceInfoVO, pOrder);

            for(CommerceItem pCommItem: (List<CommerceItem>)pOrder.getCommerceItems()){
            	fillItemAdjustments(priceInfoVO, pCommItem);
            }

		}
		return priceInfoVO;
	}
	
	/**
	 * This method determines whether the site passed as parameter is TBS or not
	 * @param pSite
	 * @return
	 * @throws PricingException
	 */
	public boolean isTBSSite(Site pSite) throws PricingException{
		boolean isTBSConcept = true;
		if (pSite == null) {
			this.logDebug("Site repository item is null: " +  SiteContextManager.getCurrentSiteId());
			throw new PricingException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
		}

		String siteId = pSite.getRepositoryId();
		
		if(isLoggingDebug()) {
			logDebug("TBSPricingTools: isTBSSite : Current : Site ID : " + siteId);
		}

		if(!siteId.contains(TBSConstants.TBS_PREFIX)) {
			isTBSConcept = false;
		}
		return isTBSConcept;
	
	}
	
	/**
	 * Gets the siteid meant for store 
	 * @param pSiteId
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getGiftWrapAutoWaiveFlag(String pSiteId) throws BBBSystemException, BBBBusinessException {
		if (pSiteId != null) {
			List<String> siteIds = this.getCatalogUtil().getAllValuesForKey(TBSConstants.GIFTWRAPAUTOWAIVEFLAG, pSiteId);
			if (siteIds == null || siteIds.get(BBBCoreConstants.ZERO).isEmpty() ){
				logError(LogMessageFormatter.formatMessage(null, "No Value found for Key "+pSiteId+" in Config Type "+TBSConstants.GIFTWRAPAUTOWAIVEFLAG+" passed to getAllValuesForKey() method"));
				throw new BBBSystemException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
			}
			return siteIds.get(BBBCoreConstants.ZERO);
		}
		logError(LogMessageFormatter.formatMessage(null, "No Value found for Key "+pSiteId+" in Config Type "+TBSConstants.GIFTWRAPAUTOWAIVEFLAG+" passed to getAllValuesForKey() method" ));
		throw new BBBSystemException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
	}
}
