package atg.commerce.pricing;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.definition.CompoundPricingModelExpression;
import atg.commerce.pricing.definition.ConstantElem;
import atg.commerce.pricing.definition.DiscountStructureElem;
import atg.commerce.pricing.definition.MatchingObject;
import atg.commerce.pricing.definition.PricingModelElem;
import atg.core.util.Range;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBDetailedItemPriceInfo;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.utils.BBBUtility;

public class BBBOrderDiscountCalculator extends OrderDiscountCalculator {

    
    public static final String FILTERED_WRAPPED_ITEMS = "FilteredWrappedItems";
    public static final String FILTERED_LIST = "FILTERED_LIST";
    public static final String COMMERCE_ID_INDEX = "COMMERCE_ID_INDEX";
    public static final String DPI_INDEX = "DPI_INDEX";
    public static final String TOTAL_DPI_COUNT = "TOTAL_DPI_COUNT";
    private BBBCatalogToolsImpl mCatalogUtil;


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void priceOrder(OrderPriceInfo pPriceQuote, Order pOrder,
            RepositoryItem pPricingModel, Locale pLocale,
            RepositoryItem pProfile, Map pExtraParameters)
            throws PricingException {
        BBBPerformanceMonitor.start("BBBOrderDiscountCalculator", "priceOrder");
        if(isLoggingDebug()) {
            logDebug(LogMessageFormatter.formatMessage(null, "Price order in discount calculator, current promotion: " + pPricingModel.getItemDisplayName()));
        }
        
        RepositoryItem couponRepositoryItem = null;
        Map<RepositoryItem,String> promoMap = (Map<RepositoryItem,String>) pExtraParameters.get(BBBCoreConstants.PROMO_MAP);
        //check for the order promotion in the promotion coupon map
		if (promoMap != null && pPricingModel != null) {
			for (RepositoryItem key : promoMap.keySet()) {
				String promotionId = promoMap.get(key);
				/*
				 * If the order promotion found then, get the coupon and remove
				 * it from the promo map
				 */
				if (null != promotionId && promotionId.equalsIgnoreCase(pPricingModel.getRepositoryId())) {
					couponRepositoryItem = key;
					vlogDebug("BBBOrderDiscountCalculator.priceOrder: Found matching promotion {0} for coupon id: {1}",
							promotionId, couponRepositoryItem.getRepositoryId());
					break;
				}
			}
		} 
		//Finding Order qualifying conditions based on already applied promotions
        if (shouldNotApply(pPriceQuote, pPricingModel)) {
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "order promotion not qualified promotion: " + pPricingModel.getItemDisplayName()));
            }
			/*
			 * Remove the Coupon promotion map from the promoMap in case of
			 * order is not eligible for promotion
			 */
			if (promoMap != null && couponRepositoryItem != null) {
				vlogDebug(
						"BBBOrderDiscountCalculator.priceOrder: Removed coupon {0} from promotion Map {1} in case of order is not eligible for promotion",
						couponRepositoryItem.getRepositoryId(), promoMap);
				promoMap.remove(couponRepositoryItem);
			}
            return;
        }
       /* List filteredItems;
        List commerceItems;
        if(pExtraParameters.get(FILTERED_LIST) == null) {//this code would execute for multiple $ off promotions
            commerceItems = pOrder.getCommerceItems();
            commerceItems = sortList(commerceItems);
          //Get the filtered items for this promotion
            filteredItems = filterItemsForPromotion(pPriceQuote, pOrder,
                    pPricingModel, pLocale, pProfile, pExtraParameters, commerceItems);
            pExtraParameters.put(FILTERED_WRAPPED_ITEMS, filteredItems);
        } else {
            commerceItems = (List) pExtraParameters.get(FILTERED_LIST);
            filteredItems = (List) pExtraParameters.get(FILTERED_WRAPPED_ITEMS);
        }*/
       
		/*
		 * This changes is done for story BBBH-4673. We have commented the above
		 * line of code because for multiple promotion we were sending same
		 * filtered items, which is wrong according to inclusion/exclusion
		 * rules.
		 */
		Map itemShareMap = new HashMap<String, Double>();
		List commerceItems = pOrder.getCommerceItems();
		commerceItems = sortList(commerceItems);
		// Get the filtered items for this promotion
		List<FilteredCommerceItem> filteredItems = filterItemsForPromotion(pPriceQuote, pOrder, pPricingModel, pLocale,
				pProfile, pExtraParameters, commerceItems);
		/*
		 * The FILTERED_WRAPPED_ITEMS contains all filtered item regard less of
		 * current promotions, means this list contains all the items eligible
		 * for applied order promotions.
		 */
		if (null == pExtraParameters.get(FILTERED_WRAPPED_ITEMS)
				|| ((List) pExtraParameters.get(FILTERED_WRAPPED_ITEMS)).isEmpty()) {
			pExtraParameters.put(FILTERED_WRAPPED_ITEMS, filteredItems);
		} else {
			List<FilteredCommerceItem> filteredItemParam = (List) pExtraParameters.get(FILTERED_WRAPPED_ITEMS);
			List<String> filteredItemParamIds = new ArrayList<String>();
			for (FilteredCommerceItem filteredCommItem : filteredItemParam) {
				filteredItemParamIds.add(filteredCommItem.getWrappedItem().getId());
			}
			/*
			 * If any filtered items already exist in the FILTERED_WRAPPED_ITEMS
			 * list then we should not add the same item again.
			 */
			for (FilteredCommerceItem filteredItem : filteredItems) {
				if (!filteredItemParamIds.contains(filteredItem.getWrappedItem().getId())) {
					filteredItemParam.add(filteredItem);
				}
			}
		}
        
        Qualifier qualifierService = getQualifierService(pPricingModel,
                pExtraParameters);
        double threshold = getThreshold(pPricingModel, qualifierService);
        int dollarOffPromoCount = ((BBBOrderPriceInfo) pPriceQuote).getDollarOffPromoCount();
        double promotionThresholdApplied = 0D;
		Map<String, Map<String, Double>> appliedPromotionThresholdMap = ((BBBOrderPriceInfo) pPriceQuote)
				.getAppliedPromotionThresholdMap();
		String ItemCouponThresholdKey = pPricingModel.getRepositoryId();
		if (null != couponRepositoryItem) {
			ItemCouponThresholdKey += couponRepositoryItem.getRepositoryId();
		}
        if(isLoggingDebug()) {
            logDebug(LogMessageFormatter.formatMessage(null, "promotion threshold adjustments from already applied promotions : " + promotionThresholdApplied));
            logDebug(LogMessageFormatter.formatMessage(null, "Already applied $off promotions : " + dollarOffPromoCount));
            logDebug(LogMessageFormatter.formatMessage(null, "Order threshold rule for current promotion  : " + threshold));
            logDebug(LogMessageFormatter.formatMessage(null, "filtered items for current promotion  : " + filteredItems.size()));
        }
        double initialOrderAmount = pPriceQuote.getAmount();
        double orderAmount = 0.0;
        
        
        long itemCount = 0;
        
        
      //no items qualified so return
        if (filteredItems.size() == 0) {
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "filtered items not found for current promotion"));
            }
            return;
        } else {            
            commerceItems.clear();
            for (Iterator iterator = filteredItems.iterator(); iterator
                    .hasNext();) {
                FilteredCommerceItem object = (FilteredCommerceItem) iterator
                        .next();                    
                long quantity = object.getQuantity();
                double amount = 0;
                ItemPriceInfo itemPriceInfo = object.getPriceInfo();
                
                CommerceItem wrappedItem = object.getWrappedItem();
                commerceItems.add(wrappedItem);
                
                if(itemPriceInfo.isOnSale()) {
                    amount = itemPriceInfo.getSalePrice() * quantity;    
                    //set amount in the item share map
                    itemShareMap.put(wrappedItem.getId(),itemPriceInfo.getSalePrice()); 
                } else {
                    amount = itemPriceInfo.getListPrice() * quantity;
                    itemShareMap.put(wrappedItem.getId(),itemPriceInfo.getListPrice()); 
                }
                orderAmount += amount;
                itemCount += quantity;
                
                if(isLoggingDebug()) {
                    logDebug(LogMessageFormatter.formatMessage(null, "current item price : " + amount));
                    logDebug(LogMessageFormatter.formatMessage(null, "orderAmount : " + orderAmount));
                }
                
            }
            
            pExtraParameters.put(FILTERED_LIST, commerceItems);
            //does not qualify based on coupon quantity ratio
            if(itemCount <= dollarOffPromoCount) {
                if(isLoggingDebug()) {
                    logDebug(LogMessageFormatter.formatMessage(null, "does not qualify based on coupon quantity ratio  : " + itemCount/(dollarOffPromoCount + 1.0)));
                }
                return;
            }
			/*
			 * Calculating threshold value for filtered items as we have
			 * inclusion/exclusion rules.
			 */
			if (!appliedPromotionThresholdMap.isEmpty()) {
				Map<String, Double> finalItemCouponThreshold = new HashMap<String, Double>();
				for (Iterator iterator = filteredItems.iterator(); iterator.hasNext();) {
					FilteredCommerceItem filteredCommItem = (FilteredCommerceItem) iterator.next();
					if (appliedPromotionThresholdMap.keySet().contains(filteredCommItem.getId())) {
						Map<String, Double> itemCouponThreshold = appliedPromotionThresholdMap.get(filteredCommItem
								.getId());
						if (null == itemCouponThreshold) {
							vlogDebug(
									"BBBOrderDiscountCalculator.priceOrder: There is no promotions applied for commerce item {0}",
									filteredCommItem.getId());
							continue;
						}
						vlogDebug(
								"BBBOrderDiscountCalculator.priceOrder: Promotions threshold applied for commerce item {0} are {1}",
								filteredCommItem.getId(), itemCouponThreshold);
						/*
						 * Separating different/unique applied promotion
						 * threshold to calculate final promotion threshold.
						 */
						for (Entry<String, Double> entry : itemCouponThreshold.entrySet()) {
							
							String promotionId = entry.getKey();
							Double promotionValue = entry.getValue();
							if (!finalItemCouponThreshold.containsKey(promotionId)) {
								finalItemCouponThreshold.put(promotionId, promotionValue);
							}
						}
					}
				}
				/* Calculating promotion threshold for filtered items. */
				if (!finalItemCouponThreshold.isEmpty()) {
					for (Double thresholdValue : finalItemCouponThreshold.values()) {
						promotionThresholdApplied += thresholdValue;
					}
					vlogDebug(
							"BBBOrderDiscountCalculator.priceOrder: Applied promotion threshold value for filtered items {0} is {1}",
							filteredItems, promotionThresholdApplied);
				}
			}
			vlogDebug(
					"BBBOrderDiscountCalculator.priceOrder: Promotion threshold value for promotion {0} with coupon {1} is {2}",
					pPricingModel, couponRepositoryItem, promotionThresholdApplied);
			//does not qualify based on order amount
            if(orderAmount - promotionThresholdApplied <= 0) { 
                if(isLoggingDebug()) {
                    logDebug(LogMessageFormatter.formatMessage(null, "does not qualify based on order Amount: " + orderAmount));
                }
                return;
            }
            //setting the qualifying order total to price quote
            pPriceQuote.setAmount(getPricingTools().round(orderAmount - promotionThresholdApplied));
        }

        List priceQuotes = generatePriceQuotesForCI(commerceItems);
        
        MatchingObject ret = null;
        ret = findMatchingObject(pPriceQuote, priceQuotes, commerceItems,
                pPricingModel, pProfile, pLocale, pOrder, pExtraParameters);
		/*
		 * Remove the Coupon promotion map from the promoMap after getting
		 * qualifying items for this promotion.
		 */
		if (promoMap != null && couponRepositoryItem != null) {
			vlogDebug(
					"BBBOrderDiscountCalculator.priceOrder: Removed coupon {0} from promotion Map {1} after getting qualifying items for this promotion.",
					couponRepositoryItem.getRepositoryId(), promoMap);
			promoMap.remove(couponRepositoryItem);
		}
        // if there's no qualifying order, simply return
        if (ret == null) {
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "order did not qualify for current promotion"));
            }
            return;
        }
        
        pPriceQuote.setAmount(initialOrderAmount);//restoring the order amount to original amount
        
        // the qualifier was met. discount the order
        if(isLoggingDebug()) {
            logDebug(LogMessageFormatter.formatMessage(null, "discounting the order : " + pOrder.getId()));
        }
        

        double orderSubTotal = 0.0;
        String discountType = getDiscountType(pPricingModel, pExtraParameters);
        /**
         * If the discount type is percent off, apply the adjuster to each group.
         * If it's amount off, divide the adjuster by the number of groups and
         * apply [adjuster/n] to each group. If it's fixed amount, divide that
         * amount by number of groups, and set each group's price to
         * [adjuster/n].
         */
        double adjuster = getAdjuster(pPricingModel, pExtraParameters);
        if (Double.isNaN(adjuster) || (adjuster > initialOrderAmount && getDiscountType(discountType) != 1)) {
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "adjuster not configure for the promotion adjuster: " + adjuster));
            }
            return;
        }
        //set item share map
        itemShareMap = getItemShare(itemShareMap, orderAmount, adjuster);

        //setting the threshold and promocount for next promotions
		if (getDiscountType(discountType) != 1) {
			for (Iterator iterator = filteredItems.iterator(); iterator.hasNext();) {
				FilteredCommerceItem filteredCommItem = (FilteredCommerceItem) iterator.next();
				/*
				 * As we have inclusion/exclusion rules, so all items are not
				 * eligible all promotions always. That's why we are setting
				 * threshold value for each item. Finally the max threshold will
				 * be picked from filtered item list.
				 */
				if (!appliedPromotionThresholdMap.isEmpty()
						&& appliedPromotionThresholdMap.keySet().contains(filteredCommItem.getId())) {
					Map<String, Double> itemPromotionThreshold = appliedPromotionThresholdMap.get(filteredCommItem
							.getId());
					double appliedThresholdValue = 0D;
					if (null == itemPromotionThreshold || itemPromotionThreshold.isEmpty()) {
						/*
						 * Adding applied promotion threshold value to item in
						 * orderPriceInfo.
						 */
						Map<String, Double> newItemPromotionThreshold = new HashMap<String, Double>();
						newItemPromotionThreshold.put(ItemCouponThresholdKey, threshold);
						appliedPromotionThresholdMap.put(filteredCommItem.getId(), newItemPromotionThreshold);
						vlogDebug(
								"BBBOrderDiscountCalculator.priceOrder: Adding threshold value {0} to item {1} , threshold value for this item is :{0}",
								threshold, filteredCommItem.getCatalogRefId());
					} else if (null == itemPromotionThreshold.get(ItemCouponThresholdKey)) {
						/*
						 * Adding applied promotion threshold value to item in
						 * orderPriceInfo.
						 */
						itemPromotionThreshold.put(ItemCouponThresholdKey, threshold);
						vlogDebug(
								"BBBOrderDiscountCalculator.priceOrder: Adding threshold value {0} to item {1} , threshold value for this item is :{0}",
								threshold, filteredCommItem.getCatalogRefId());
					} else {
						/*
						 * Updating applied promotion threshold value to item in
						 * orderPriceInfo.
						 */
						appliedThresholdValue = itemPromotionThreshold.get(ItemCouponThresholdKey);
						itemPromotionThreshold.put(ItemCouponThresholdKey, appliedThresholdValue + threshold);
						vlogDebug(
								"BBBOrderDiscountCalculator.priceOrder: Promotion already applied to item {0} with promotion threshold value {1}, updated threshold value is {2}",
								filteredCommItem.getCatalogRefId(), appliedThresholdValue, appliedThresholdValue
										+ threshold);
					}
				} else {
					/*
					 * Adding applied promotion threshold value to item in
					 * orderPriceInfo.
					 */
					Map<String, Double> newItemPromotionThreshold = new HashMap<String, Double>();
					newItemPromotionThreshold.put(ItemCouponThresholdKey, threshold);
					appliedPromotionThresholdMap.put(filteredCommItem.getId(), newItemPromotionThreshold);
					vlogDebug(
							"BBBOrderDiscountCalculator.priceOrder: Adding threshold value {0} to item {1} , threshold value for this item is :{0}",
							threshold, filteredCommItem.getCatalogRefId());
				}
			}
			((BBBOrderPriceInfo) pPriceQuote).setDollarOffPromoCount(dollarOffPromoCount + 1);
		}
        adjustItemPricing(filteredItems, adjuster, 
        		discountType, pPricingModel, pExtraParameters, itemShareMap,couponRepositoryItem);
                
        
        orderSubTotal = getAmountToDiscount(pPriceQuote, pOrder, pPricingModel,
                pLocale, pProfile, pExtraParameters);

        for (int i = 0; i < ret.getQuantity(); i++) {
            orderSubTotal = adjustOrderSubTotal(orderSubTotal, adjuster,
                    discountType, pOrder.getId(), pPricingModel,
                    pExtraParameters);
        }

        

        // amountInfo stuff
        orderSubTotal = getPricingTools().round(orderSubTotal);
        if(isLoggingDebug()) {
            logDebug(LogMessageFormatter.formatMessage(null, "order's amount before discounting: " + pPriceQuote.getAmount()));
            logDebug(LogMessageFormatter.formatMessage(null, "rounding discounted order subtotal: " + orderSubTotal));
        }
        

        

        pPriceQuote.setAmount(orderSubTotal);

        // TODO we might not need this as we are updating the item dpi
        /*updateItemsDiscountShare(commerceItems, pOrder, initialOrderAmount,
                getPricingTools().round(initialOrderAmount - orderSubTotal),
                adjuster, discountType, ret.getQuantity());*/
        
        if(isLoggingDebug()) {
            logDebug(LogMessageFormatter.formatMessage(null, "order's amount after discount: " + pPriceQuote.getAmount()));
        }

        pPriceQuote.setDiscounted(true);

        // add this adjustment to the list of adjustments
        double adjustAmount = pPriceQuote.getAmount() - initialOrderAmount;
		if (couponRepositoryItem != null) {
			pPriceQuote.getAdjustments().add(
					new PricingAdjustment(Constants.ORDER_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION, pPricingModel,
							getPricingTools().round(adjustAmount), 1, couponRepositoryItem));
			vlogDebug("BBBOrderDiscountCalculator.priceOrder: Added coupon: {0} to order adjustment for order id: {1}",
					couponRepositoryItem, pOrder.getId());
		} else {
			pPriceQuote.getAdjustments().add(
					new PricingAdjustment(Constants.ORDER_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION, pPricingModel,
							getPricingTools().round(adjustAmount), 1));
		}
        
        BBBPerformanceMonitor.end("BBBOrderDiscountCalculator", "priceOrder");
    }


    /**This method is for adjusting pricing per item quantity.
     * 
     * @param filteredItems
     * @param adjuster
     * @param discountType
     * @param pPricingModel
     * @param pExtraParameters
     * @param itemShareMap
     * @param couponRepositoryItem
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void adjustItemPricing(List filteredItems, double adjuster,
            String discountType, RepositoryItem pPricingModel, Map pExtraParameters, Map itemShareMap, RepositoryItem couponRepositoryItem) {
    	
		if (getDiscountType(discountType) == 1) {// % off promotion
			for (Iterator iterator = filteredItems.iterator(); iterator
					.hasNext();) {
				FilteredCommerceItem item = (FilteredCommerceItem) iterator
						.next();
				
				CommerceItem wrappedItem = item.getWrappedItem();

				Map detailsRangesValidForTarget = item
						.getDetailsRangesValidForQualifier();
				Set keySet = detailsRangesValidForTarget.keySet();

				DetailedItemPriceInfo dpi = null;
				
				double priceInfoAmount = 0.0;
				double unitPriceInfoAmount = 0.0;
				for (Iterator iterator2 = keySet.iterator(); iterator2
						.hasNext();) {
					dpi = (DetailedItemPriceInfo) iterator2.next();
					priceInfoAmount = dpi.getAmount();// * dpi.getQuantity();
					unitPriceInfoAmount = dpi.getDetailedUnitPrice();// * dpi.getQuantity();
					if (isLoggingDebug()) {
						logDebug("unitPriceInfoAmount:" + unitPriceInfoAmount);
						logDebug("Old priceInfoAmount:" + priceInfoAmount);
					}
					double unitItemDiscount = BigDecimal.valueOf(unitPriceInfoAmount).multiply(BigDecimal.valueOf(adjuster / 100D)).doubleValue();
					unitItemDiscount= getPricingTools().round(unitItemDiscount);
					double itemDiscount = unitItemDiscount * dpi.getQuantity();
					priceInfoAmount -= itemDiscount;
					if (isLoggingDebug()) {
						logDebug("itemDiscount per unit:" + unitItemDiscount);
						logDebug("Total itemDiscount per DPI:" + itemDiscount);
						logDebug("New priceInfoAmount:" + priceInfoAmount);
					}
                    dpi.setAmount(getPricingTools().round(priceInfoAmount));
                    dpi.setDiscounted(true);
                    updateDetailedInfo(item, dpi, itemDiscount, pPricingModel, couponRepositoryItem);
                    wrappedItem.getPriceInfo().setAmount(getPricingTools().round(wrappedItem.getPriceInfo().getAmount() - itemDiscount));
				}
				
			}

		} else { 
			// $ off type discount
			if (isLoggingDebug()) {
				logDebug(LogMessageFormatter.formatMessage(null,
						"promotions are being applied for the first time, create dpi list"));
			}
			// create splitted dpi list from the current DPI list
			createDpiList(filteredItems, pExtraParameters);
			// set index parameters into the map
			pExtraParameters.put(DPI_INDEX, 0);
			pExtraParameters.put(COMMERCE_ID_INDEX, 0);
			Integer ciIndex = (Integer) pExtraParameters.get(COMMERCE_ID_INDEX);
			Integer dpiIndex = (Integer) pExtraParameters.get(DPI_INDEX);
			distributeAmount(ciIndex, dpiIndex, adjuster, pExtraParameters, itemShareMap, 0, pPricingModel,
					couponRepositoryItem);
		}
    	
    }
    
    	
	/**
	 * Distribute the remaining adjuster amount
	 * 
	 * @param ciIndex
	 * @param dpiIndex
	 * @param remainingAdjusterAmt
	 * @param pExtraParameters
	 * @param itemShareMap
	 * @param dpiProcessedCount
	 * @param pPricingModel
	 * @param couponRepositoryItem
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void distributeAmount(Integer ciIndex, Integer dpiIndex,
			double remainingAdjusterAmt, Map pExtraParameters,
			Map itemShareMap, Integer dpiProcessedCount, RepositoryItem pPricingModel, RepositoryItem couponRepositoryItem) {

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start-  distributeAmount() , ciIndex =" + ciIndex
							+ ",dpiIndex=" + dpiIndex
							+ ",remainingAdjusterAmt = " + remainingAdjusterAmt
							+ ",dpiProcessedCount = " + dpiProcessedCount));
		}
		
		boolean isAdjusterAmtExhaushted = false;
		
		//get the total DPI count from the map
		Integer totalDpiCount = (Integer) pExtraParameters.get(TOTAL_DPI_COUNT);
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"totalDpiCount" + totalDpiCount));
		}
		

		// get the commerceItemdescendingList from the map
		List<CommerceItem> ciList = (List<CommerceItem>) pExtraParameters
				.get(FILTERED_LIST);

		// get the commerceItem from the list
		CommerceItem commerceItem = ciList.get(ciIndex);

		// get the corresponding DPI list
		List<DetailedItemPriceInfo> dpiList = (List<DetailedItemPriceInfo>) pExtraParameters
				.get(commerceItem.getId());

		// get the item Share for the CommerceItem from itemShareMap
		Double itemShare = (Double) itemShareMap.get(commerceItem.getId());
		Integer count = null;

		// set the discount share in the commerce item dpi list created earlier
		for (count = dpiIndex; count < dpiList.size(); count++) {

			// Check is this is the last DPI, then set all the remaining amount
			// to dpi and exit
			
			DetailedItemPriceInfo detailedItemPriceInfo = dpiList
			.get(count);
			
			if(itemShare > 0.0) {
			
			if (dpiProcessedCount == totalDpiCount - 1 && Double.compare(itemShare ,0.0) == 0) {
				remainingAdjusterAmt = getPricingTools().round(remainingAdjusterAmt);
				detailedItemPriceInfo.setAmount(getPricingTools().round(detailedItemPriceInfo
						.getAmount()
						+ remainingAdjusterAmt));
				
				
				getPricingAdjustments(pPricingModel, remainingAdjusterAmt, detailedItemPriceInfo, couponRepositoryItem);
				
				if (isLoggingDebug()) {
					logDebug(LogMessageFormatter
							.formatMessage(
									null,
									"ItemShare = "
											+ itemShare
											+ "CommerceItem adjusted, dpiProcessedCount ,="
											+ dpiProcessedCount
											+ "totalDpiCount" + totalDpiCount
											+ commerceItem.getId()
											+ "Last DPI "
											+ detailedItemPriceInfo
											+ ",Amount adjusted in this DPI ="
											+ remainingAdjusterAmt));
				}

				remainingAdjusterAmt = 0.0;
				isAdjusterAmtExhaushted = true;
				dpiProcessedCount++;
				count++;
				break;
			}
			
				if (itemShare < remainingAdjusterAmt) {
	
					detailedItemPriceInfo.setAmount(getPricingTools().round(detailedItemPriceInfo
							.getAmount()
							+ itemShare));
					remainingAdjusterAmt -= itemShare;
					dpiProcessedCount++;
					
					getPricingAdjustments(pPricingModel, itemShare, detailedItemPriceInfo, couponRepositoryItem);
					
				} else {
					remainingAdjusterAmt = getPricingTools().round(remainingAdjusterAmt);
					if(remainingAdjusterAmt > 0) {
						detailedItemPriceInfo.setAmount(getPricingTools().round(detailedItemPriceInfo
								.getAmount()
								+ remainingAdjusterAmt));
						
						
						getPricingAdjustments(pPricingModel, remainingAdjusterAmt, detailedItemPriceInfo, couponRepositoryItem);
					}
					remainingAdjusterAmt = 0.0;
					
					
					
					if (isLoggingDebug()) {
						logDebug(LogMessageFormatter.formatMessage(null,
								"Amount Exhaushted, commerceItem adjusted-"
										+ commerceItem.getId() + "ciIndex = "
										+ ciIndex + ",dpiIndex =" + dpiIndex
										+ "remainingAdjusterAmt = "
										+ remainingAdjusterAmt));
					}
					
					dpiProcessedCount++;
					count++;
					isAdjusterAmtExhaushted = true;
					break;
				}
			
		} 
		}

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Current indexes -  ciIndex= " + ciIndex + ", dpiIndex = " + dpiIndex));
		}
		// calculate the index values
		if (count == dpiList.size()) {
			// Set Dpi Index = 0;
			dpiIndex = 0;
			if (ciIndex == ciList.size() - 1) {
				// Set Ci Index to 0
				ciIndex = 0;
			} else {
				ciIndex = ciIndex + 1;
			}

		} else {
			dpiIndex = count;
		}
		
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Updated indexes -  ciIndex = " + ciIndex + ", dpiIndex = " + dpiIndex));
		}

		// set the indexes
		pExtraParameters.put(DPI_INDEX, dpiIndex);
		pExtraParameters.put(COMMERCE_ID_INDEX, ciIndex);

		//if isAdjusterAmtExhaushted = true means amount has been distributed , return to calling method
		if (isAdjusterAmtExhaushted) {
			
			if (isLoggingDebug()) {
				logDebug(LogMessageFormatter.formatMessage(null,
						"Adjuster Amount Exhaushted, total dpi processed= " + dpiProcessedCount + "return"));
			}
			
			return;
		} else {
			
		//remainingAdjusterAmt is not 0, call method again with updated indexes and amount
			distributeAmount(ciIndex, dpiIndex, remainingAdjusterAmt,
					pExtraParameters, itemShareMap,
					dpiProcessedCount, pPricingModel, couponRepositoryItem);
		}

	}
	

	/**
	 * Calculate the item share per commerce item, this will be used when giving
	 * discount share to DPIs
	 * 
	 * @param itemAmountMap
	 * @param orderAmount
	 * @param commerceItems
	 * @param adjuster
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Double> getItemShare(Map<String, Double> itemAmountMap,
			double orderAmount, double adjuster) {

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start- getItemShare itemAmountMap: " + itemAmountMap
							+ ", orderAmount:" + orderAmount + ", adjuster:"
							+ adjuster));
		}

		Map itemShareMap = new HashMap<String, Double>();
		double ratio = 0.0;
		double itemDiscount = 0.0;

		for (Map.Entry<String, Double> entry : itemAmountMap.entrySet()) {

			if (isLoggingDebug()) {
				logDebug(LogMessageFormatter.formatMessage(null, "Key = "
						+ entry.getKey() + ", Value = " + entry.getValue()));
			}

			if (Double.compare(orderAmount,0.0) == 0) {

				if (isLoggingDebug()) {
					logDebug(LogMessageFormatter.formatMessage(null,
							"orderAmount is 0, setting item discount to 0"));
				}

				itemShareMap.put(entry.getKey(), itemDiscount);
			} else {

				ratio = (Double) itemAmountMap.get(entry.getKey())
						/ orderAmount;

				if (isLoggingDebug()) {
					logDebug(LogMessageFormatter.formatMessage(null,
							"itemDiscount ratio is =" + ratio));
				}

				itemDiscount = getPricingTools().round(
						BigDecimal.valueOf(adjuster).multiply(
								BigDecimal.valueOf(ratio)).doubleValue());

				itemShareMap.put(entry.getKey(), itemDiscount);

				if (isLoggingDebug()) {
					logDebug(LogMessageFormatter.formatMessage(null,
							"itemDiscount calculated = " + itemDiscount
									+ "for the commerceId - " + entry.getKey()));
				}
			}

		}

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End- getItemShare ItemShareMap: " + itemShareMap));
		}

		return itemShareMap;

	}

	/**
	 * Create DPI list and set into the map.
	 * 
	 * @param dpi
	 * 
	 * @return list of DPI
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createDpiList(List<FilteredCommerceItem> filteredItems,
			Map pExtraParameters) {
		
	if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start-  createDpiList() , filteredItems -" + filteredItems));
		}

		List<DetailedItemPriceInfo> dpiList = null;

		Integer totalDpiCount = 0;
		

		for (Iterator iterator = filteredItems.iterator(); iterator.hasNext();) {
			FilteredCommerceItem filteredCommerceItem = (FilteredCommerceItem) iterator
					.next();
			CommerceItem wrappedItem = filteredCommerceItem.getWrappedItem();
			List<DetailedItemPriceInfo> extDpiList = (List<DetailedItemPriceInfo>) pExtraParameters.get(wrappedItem
					.getId());
			/*
			 * In case of multiple order promotion we are checking if DPI
			 * already present for any commerce item, then we are not creating
			 * new DPI's, instead we will use same DPI to add adjustments.
			 */
			if (null != extDpiList && !extDpiList.isEmpty()) {
				totalDpiCount = totalDpiCount + extDpiList.size();
				vlogDebug(
						"BBBOrderDiscountCalculator.createDpiList: DPI list already present for commerceItem id - {0}.",
						wrappedItem.getId());
				continue;
			}
			dpiList = new ArrayList<DetailedItemPriceInfo>();
			Map detailsRangesValidForTarget = filteredCommerceItem
					.getDetailsRangesValidForQualifier();
			Set keySet = detailsRangesValidForTarget.keySet();

			for (Iterator iterator2 = keySet.iterator(); iterator2.hasNext();) {

				DetailedItemPriceInfo dpi = (DetailedItemPriceInfo) iterator2
						.next();
				
				dpiList.addAll(splitDpi(dpi));
				
			}
			
			totalDpiCount = totalDpiCount+ dpiList.size();

			// set key - commerceItemId , value - splitted Dpi into the map
			pExtraParameters.put(wrappedItem.getId(), dpiList);
		}

		pExtraParameters.put(TOTAL_DPI_COUNT, totalDpiCount);
		
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End- createDpiList() , totalDpiCount -" + totalDpiCount));
		}

	}

	/**
	 * Split a dpi into multiple DPIs depending upon the no of item range.
	 * 
	 * @param dpi
	 * @return
	 */
	private List<DetailedItemPriceInfo> splitDpi(DetailedItemPriceInfo dpi) {

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start-  splitDpi() , for DPI -" + dpi));
		}

		List<DetailedItemPriceInfo> dpiList = new ArrayList<DetailedItemPriceInfo>();
		Long rangeHighBound = dpi.getRange().getHighBound();
		Long rangeLowBound = dpi.getRange().getLowBound();
		Range range = new Range();

		for (Long count = rangeLowBound; count <= rangeHighBound;count++) {
			
			
			BBBDetailedItemPriceInfo newDpi = new BBBDetailedItemPriceInfo();
			
			range = new Range();
			range.setLowBound(rangeLowBound);
			range.setHighBound(rangeLowBound++);
			newDpi.setRange(range);
			
			newDpi.setAmount(0.0);
			newDpi.setQuantity(1);
			dpiList.add(newDpi);
			

		}
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End -  splitDpi(), DPI list created successfully, size of list - " +dpiList.size()));
		}

		return dpiList;
	}

    /**
     * Update the item level dpi 
     * 
     * @param filteredItem
     * @param filteredDpi
     * @param itemDiscount
     * @param pPricingModel
     * @param couponRepositoryItem 
     */
    @SuppressWarnings("unchecked")
	private void updateDetailedInfo(FilteredCommerceItem filteredItem,DetailedItemPriceInfo filteredDpi, double itemDiscount, RepositoryItem pPricingModel, RepositoryItem couponRepositoryItem){
    	
    	if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start -  updateDetailedInfo, DPI list created successfully"));
		}
    	
    	List<DetailedItemPriceInfo> itemDpiList = filteredItem.getWrappedItem().getPriceInfo().getCurrentPriceDetails();
    	boolean dpiRangeFound = false;
    	
    	if(null != itemDpiList && !itemDpiList.isEmpty()){
    		
    		for(Iterator<DetailedItemPriceInfo> itr = itemDpiList.iterator();itr.hasNext();){
    			DetailedItemPriceInfo commerItemDpi = itr.next();
    			
    			if(commerItemDpi.getRange().getLowBound() == filteredDpi.getRange().getLowBound() &&
    					commerItemDpi.getRange().getHighBound() == filteredDpi.getRange().getHighBound()){
    				
    				commerItemDpi.getAdjustments().add(new PricingAdjustment(
    						Constants.ORDER_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
    						pPricingModel, getPricingTools().round(-itemDiscount), 
    						filteredDpi.getQuantity(), couponRepositoryItem));
    				commerItemDpi.setDiscounted(true);
    				dpiRangeFound = true;
    				
    			}
    		}
    	}
    	
    	
    	if(!dpiRangeFound){
    	
    		PricingAdjustment adjustment = new PricingAdjustment(
                    Constants.ORDER_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
                    pPricingModel, getPricingTools().round(-itemDiscount), 
                    	filteredDpi.getQuantity(), couponRepositoryItem);
    		
    		filteredDpi.getAdjustments().add(adjustment);
    		filteredDpi.setDiscounted(true);
    		filteredItem.getWrappedItem().getPriceInfo().getCurrentPriceDetails().add(filteredDpi);
    		
    		
    	}
    }


    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List generatePriceQuotesForCI(List commerceItems) {
        List priceQuotes = new ArrayList(commerceItems.size());
        Iterator itemIterator = commerceItems.iterator();
        while (itemIterator.hasNext()) {
            CommerceItem item = (CommerceItem) itemIterator.next();
            priceQuotes.add(item.getPriceInfo());
        }
        return priceQuotes;
    }

    
    @SuppressWarnings("rawtypes")
    public List filterItemsForPromotion(OrderPriceInfo pPriceQuote,
            Order pOrder, RepositoryItem pPricingModel, Locale pLocale,
            RepositoryItem pProfile, Map pExtraParameters, List commerceItems)
            throws PricingException {
       
        List priceQuotes = generatePriceQuotesForCI(commerceItems);
        Qualifier qualifierService = getQualifierService(pPricingModel,
                        pExtraParameters);
        List wrappedItems = qualifierService.wrapCommerceItems(
                commerceItems, priceQuotes);
        RepositoryItem siteItem = null;
        try {
        	if (pOrder.getSiteId() != null) {
                siteItem = getCatalogUtil().getSiteRepository()
                        .getItem(pOrder.getSiteId(),BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
        	}
        } catch (RepositoryException e) {
            if(isLoggingError()) {
                logError(LogMessageFormatter.formatMessage(null, "Could not get the site repositoryItem for :" + pOrder.getSiteId()), e);
            }
        }
        PricingContext pricingContext = getPricingTools()
                .getPricingContextFactory().createPricingContext(wrappedItems,
                        pPricingModel, pProfile, pLocale, pOrder, siteItem);

        pricingContext.setOrderPriceQuote(pPriceQuote);

        Map pDetailsMap = new HashMap();
        List filteredItems = new ArrayList();
        Map pDetailsPendingActingAsQualifier = new HashMap();
        
        
        qualifierService.doFilters(1, pricingContext,
        		pExtraParameters, pDetailsMap,
                pDetailsPendingActingAsQualifier, filteredItems);
        return filteredItems;
    }

   

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List sortList(List commerceItems) {
        List results = new ArrayList();
        results.addAll(commerceItems);
        Collections.sort(results);
        return results;
    }

    public double getThreshold(RepositoryItem pPricingModel,
            Qualifier qualifierService) {
    	if(isLoggingDebug()) {
            logDebug(LogMessageFormatter.formatMessage(null, "entering BBBOrderDiscountCalculator"));
        }
		String threshold = null;
		double thresholdValue = 0.0;
		try {
			threshold = ((ConstantElem) ((CompoundPricingModelExpression) ((PricingModelElem) qualifierService
					.getPMDLCache().get(pPricingModel)).getQualifier()
					.getSubElements()[0]).getSubElements()[1])
					.getStringValues()[0];

			if (!BBBUtility.isEmpty(threshold)) {
				thresholdValue = Double.parseDouble(threshold);
			}
		} catch (Exception e) {// no problem, returns 0.0
			if (isLoggingDebug()) {
				logDebug(LogMessageFormatter.formatMessage(null,
						"no thresold found for the promotion ... returning 0.0"), e);
			}
		}
		return thresholdValue;

   }

    
	@SuppressWarnings("rawtypes")
    public boolean shouldNotApply(OrderPriceInfo pPriceQuote, RepositoryItem pPricingModel) {
        if(isLoggingDebug()) {
            logDebug(LogMessageFormatter.formatMessage(null, "entering isOrderAlreadyDiscounted promotion " + pPricingModel.getItemDisplayName()));
        }
        String discountType = null;
        discountType = getDiscountType(pPricingModel);
        if(!pPriceQuote.isDiscounted()) {
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "no applied promotion found"));
            }
            return false;
        } else if(discountType == null) {//current promotion's type should not be null
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "promotion not configured for discountType: " + discountType));
            }
            return true;
        } else if(getDiscountType(discountType) == 1) {//current is %off promotion and atleast one promo is already applied
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "current promotion is %off and there are promotions already applied"));
            }
            return true;
        } else {
            List adjustments = pPriceQuote.getAdjustments();
            for (Iterator iterator = adjustments.iterator(); iterator.hasNext();) {
                PricingAdjustment pAdustment = (PricingAdjustment) iterator.next();
                RepositoryItem usedPromotion = pAdustment.getPricingModel();
                if(usedPromotion != null) {
                    String usedDiscountType = null;
                    usedDiscountType = getDiscountType(usedPromotion);
                    if(usedDiscountType == null || 
                            getDiscountType(usedDiscountType) == 1) {
                        if(isLoggingDebug()) {
                            logDebug(LogMessageFormatter.formatMessage(null, "A % promotion is already applied"));
                        }
                        return true;
                    }
                }
                
            }
        }
        return false;
    }

    private String getDiscountType(RepositoryItem promotion) {
        
        String usedDiscountType = null;
        try {
            usedDiscountType  = ((DiscountStructureElem)((PricingModelElem)getQualifierService(promotion,
                    null).getPMDLCache().get(promotion)).getOffer().getSubElements()[0]).getDiscountType();
        } catch (Exception e) {
            if(isLoggingError()) {
                logError(LogMessageFormatter.formatMessage(null, "no discount type found for the promotion"), e);
            }
        }
        return usedDiscountType;
    }



    /**
     * This method will set the orderDiscountShare property of each
     * CommerceItem's priceInfo. This happens if
     * <code>saveItemsOrderDiscountShare</code> is true. If
     * <code>saveDetailsOrderDiscountShare</code> is true, then the
     * orderDiscountShare property of each DetailedItemPriceInfo is also
     * updated.
     * 
     * @param pOrder
     *            the order that was discounted
     * @param pUnadjustedPrice
     *            The original pre-discount price of the order
     * @param pTotalAdjustment
     *            This is the total change to the order total, for the current
     *            promotion
     * @param pAdjuster
     *            The size of the discount
     * @param pDiscountType
     *            the way in which pAdjuster is applied to pGroupSubTotal. May
     *            be one of: "Fixed Price", "Amount Off", or "Percent Off".
     * @param pTimesToDiscount
     *            The number of times to apply the given discount
     **/
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void updateItemsDiscountShare(List commerceItems, Order pOrder,
            double pUnadjustedPrice, double pTotalAdjustment, double pAdjuster,
            String pDiscountType, long pTimesToDiscount)
            throws PricingException {
        List items = commerceItems;

        if (items == null) {
            items = getItemsToReceiveDiscountShare(pOrder);
        }
        List relationships = new ArrayList();

        for (int i = 0; i < items.size(); i++) {
            CommerceItem item = (CommerceItem) items.get(i);
            relationships.addAll(item.getShippingGroupRelationships());
        }

        if (isSaveItemsOrderDiscountShare()) {
            double shareSoFar = 0.0;
            for (int c = 0; c < items.size(); c++) {
                CommerceItem item = (CommerceItem) items.get(c);
                ItemPriceInfo price = item.getPriceInfo();
                double oldShare = price.getOrderDiscountShare();
                double itemAmount = price.getAmount() - oldShare;
                double ratio = itemAmount / pUnadjustedPrice;

                // if we divided by zero... use zero instead
                if (Double.isNaN(ratio) || Double.isInfinite(ratio)) {
                    if (isLoggingDebug()) {
                        logDebug(MessageFormat.format(
                                Constants.QUOTIENT_IS_NAN,
                                "updateItemDiscountShare",
                                Double.toString(itemAmount),
                                Double.toString(pUnadjustedPrice)));
                    }
                    ratio = 0.0;
                }

                double share = pTotalAdjustment * ratio;
                double roundedShare = getPricingTools().round(share);

                shareSoFar += roundedShare;
                // check leftovers
                if (c == (items.size() - 1)) {
                    double leftovers = pTotalAdjustment - shareSoFar;

                    if (isLoggingDebug()){
                        logDebug("Adding " + leftovers
                                + " leftovers to items order discount share");
                    }   			
                    roundedShare += leftovers;
                    roundedShare = getPricingTools().round(roundedShare);
                }
                if (isLoggingDebug()){
                    logDebug("Setting items order discount share to: "
                            + roundedShare + " + " + oldShare);
                }
                price.setOrderDiscountShare(getPricingTools().round(
                        roundedShare + oldShare));

                // now do the details
                if (isSaveDetailsOrderDiscountShare()) {
                    List details = getDetailsToReceiveDiscountShare(price);
                    double itemDiscShare = roundedShare;
                    double detailShareSoFar = 0.0;
                    for (int d = 0; d < details.size(); d++) {
                        DetailedItemPriceInfo detail = (DetailedItemPriceInfo) details
                                .get(d);
                        double detailOldShare = detail.getOrderDiscountShare();
                        double detailRatio = 0.0;
                        // if the detail amount is zero, then it's possible the
                        // price amount is
                        // zero. If we do the division the answer will be NaN
                        if (detail.getAmount() > 0.0){
                            detailRatio = detail.getAmount()
                                    / price.getAmount();
                        }

                        // if we divided by zero... use zero instead
                        if (Double.isNaN(detailRatio)
                                || Double.isInfinite(detailRatio)) {
                            if (isLoggingDebug()) {
                                logDebug(MessageFormat.format(
                                        Constants.QUOTIENT_IS_NAN,
                                        "updateItemDiscountShare",
                                        Double.toString(detail.getAmount()),
                                        Double.toString(price.getAmount())));
                            }
                            detailRatio = 0.0;
                        }

                        double detailShare = detailRatio * roundedShare;
                        double detailRoundedShare = getPricingTools().round(
                                detailShare);

                        detailShareSoFar += detailRoundedShare;
                        if (d == (details.size() - 1)) {
                            double detailLeftovers = itemDiscShare
                                    - detailShareSoFar;

                            if (isLoggingDebug()){
                                logDebug("Adding "
                                        + detailLeftovers
                                        + " leftovers to details order discount share");
                            }
                            detailRoundedShare += detailLeftovers;
                            detailRoundedShare = getPricingTools().round(
                                    detailRoundedShare);
                        }
                        if (isLoggingDebug()){
                            logDebug("Setting details order discount share to: "
                                    + detailRoundedShare
                                    + " + "
                                    + detailOldShare);
                        }
                    }
                }
            }
        } else {
            // we didn't do it this way in the if block since we want the
            // details share to add up to the
            // item share. If we distribute completely independently, this might
            // not be the case.
            if (isSaveDetailsOrderDiscountShare()){
                getPricingTools().getDetailedItemPriceTools()
                        .distributeAmountAcrossDetails(relationships,
                                pUnadjustedPrice, pTotalAdjustment,
                                "orderDiscountShare", true);
            }
        }
    }
    
	/**
	 * Method to get the Pricing Adjustments and remove the duplicate entries
	 * for pricing model and adjust the total adjustment amount for the same
	 * adjustment.
	 * 
	 * @param pPricingModel
	 * @param itemShare
	 * @param detailedItemPriceInfo
	 * @param couponRepositoryItem
	 */
	@SuppressWarnings("unchecked")
	private void getPricingAdjustments(final RepositoryItem pPricingModel, final Double itemShare,
			final DetailedItemPriceInfo detailedItemPriceInfo, RepositoryItem couponRepositoryItem) {

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null, "Start-  getPricingAdjustments() , itemShare ="
					+ itemShare));
		}

		final PricingAdjustment pricingAdjustment = ((BBBPricingTools) getPricingTools()).getPromotionAdjustment(
				pPricingModel, itemShare, 1);

		boolean isAdjustmentPresent = false;
		PricingAdjustment pricingAdj = null;
		if (!detailedItemPriceInfo.getAdjustments().isEmpty()) {
			final Iterator<PricingAdjustment> it = detailedItemPriceInfo.getAdjustments().iterator();
			while (it.hasNext()) {
				pricingAdj = it.next();
				if (null != pricingAdj.getPricingModel()
						&& null != pricingAdjustment.getPricingModel()
						&& pricingAdj.getPricingModel().getRepositoryId()
								.equals(pricingAdjustment.getPricingModel().getRepositoryId())) {
					if (couponRepositoryItem == null
							|| pricingAdj.getCoupon() == null
							|| pricingAdj.getCoupon().getRepositoryId()
									.equalsIgnoreCase(couponRepositoryItem.getRepositoryId())) {
						vlogDebug(
								"BBBOrderDiscountCalculator.getPricingAdjustments: Adjustment present for coupon: {0}, in detailedItemPriceInfo",
								couponRepositoryItem);
						isAdjustmentPresent = true;
					}
				}
			}

			if (isAdjustmentPresent) {
				if (isLoggingDebug()) {
					logDebug("Updating Total Adjustment to the duplicate pricing model with Id : "
							+ pricingAdjustment.getPricingModel().getRepositoryId()
							+ "present in detailedPriceInfo with " + "Original Adjustment : "
							+ pricingAdj.getTotalAdjustment() + "and Adjusted Amount : "
							+ getPricingTools().round(-itemShare * 1));
				}
				pricingAdj
						.setTotalAdjustment(pricingAdj.getTotalAdjustment() + getPricingTools().round(-itemShare * 1));
			} else {
				if (isLoggingDebug()) {
					logDebug("Adding unique pricingAdjustments to the detailedPriceInfo");
				}
				if (couponRepositoryItem != null) {
					vlogDebug("BBBOrderDiscountCalculator.getPricingAdjustments: Setting coupon: {0}, to adjustment",
							couponRepositoryItem);
					pricingAdjustment.setCoupon(couponRepositoryItem);
				}
				detailedItemPriceInfo.getAdjustments().add(pricingAdjustment);
			}
		} else {
			if (isLoggingDebug()) {
				logDebug("Adding pricingAdjustments to the blank detailedPriceInfo");
			}
			if (couponRepositoryItem != null) {
				vlogDebug("BBBOrderDiscountCalculator.getPricingAdjustments: Setting coupon: {0}, to adjustment",
						couponRepositoryItem);
				pricingAdjustment.setCoupon(couponRepositoryItem);
			}
			detailedItemPriceInfo.getAdjustments().add(pricingAdjustment);
		}

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null, "End-  getPricingAdjustments()"));
		}
	}


    public BBBCatalogToolsImpl getCatalogUtil() {
        return mCatalogUtil;
    }


    public void setCatalogUtil(BBBCatalogToolsImpl mCatalogUtil) {
        this.mCatalogUtil = mCatalogUtil;
    }
}