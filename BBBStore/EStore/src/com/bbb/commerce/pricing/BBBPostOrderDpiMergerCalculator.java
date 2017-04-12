package com.bbb.commerce.pricing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.BBBOrderDiscountCalculator;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.core.util.Range;
import atg.repository.RepositoryItem;

import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBDetailedItemPriceInfo;

public class BBBPostOrderDpiMergerCalculator extends BBBOrderDiscountCalculator {

	/**
	 * This method call the merge DPI method for all the commerce items in filteredItems.
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public void priceOrder(final OrderPriceInfo pPriceQuote, final Order pOrder,
			final RepositoryItem pPricingModel, final Locale pLocale,
			final RepositoryItem pProfile, final Map pExtraParameters)
			throws PricingException {
	    BBBPerformanceMonitor.start("BBBPostOrderDpiMergerCalculator", "priceOrder");
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start - BBBPostOrderDpiMergerCalculator.priceOrder"));
		}
		final List<FilteredCommerceItem> filteredItems = (List) pExtraParameters
				.get(FILTERED_WRAPPED_ITEMS);

		if (filteredItems != null) {
			
			for (final Iterator iterator = filteredItems.iterator(); iterator
					.hasNext();) {
				final FilteredCommerceItem item = (FilteredCommerceItem) iterator
						.next();
				final CommerceItem wrappedItem = item.getWrappedItem();
				
				mergeDPIListForCommerceItem(wrappedItem, pExtraParameters);

			}
		}
		
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End- BBBPostOrderDpiMergerCalculator.priceOrder"));
		}

		BBBPerformanceMonitor.end("BBBPostOrderDpiMergerCalculator", "priceOrder");
	}

	/**
	 * Merge the DPI list and update the existing commerce item list.
	 * 
	 * @param commerceItem
	 * @param pExtraParameters
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	private void mergeDPIListForCommerceItem(final CommerceItem commerceItem,
			final Map pExtraParameters) {

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter
					.formatMessage(
							null,
							"Start- BBBPostOrderDpiMergerCalculator.mergeDPIListForCommerceItem for CommerceItem - "
									+ commerceItem.getCatalogRefId()));
		}
		// get the corresponding DPI list for the commerce Item
		final List<DetailedItemPriceInfo> dpiList = (List<DetailedItemPriceInfo>) pExtraParameters
				.get(commerceItem.getId());
		
		if(null == dpiList){
			if (isLoggingDebug()) {
				logDebug(LogMessageFormatter.formatMessage(null,
						"Corresponding DPI list for CommerceItem - "
								+ commerceItem.getCatalogRefId() + "is null or empty"));
			}
			return;
		}

		mergeSameDPI(dpiList, commerceItem);

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Merged DPI list created for CommerceItem - "
							+ commerceItem.getCatalogRefId()
							+ ", Merged DPI list - " + dpiList + " , Merged DPI list ended"));
		}

		updateExistingDPI(commerceItem, dpiList);

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter
					.formatMessage(
							null,
							"End- BBBPostOrderDpiMergerCalculator.mergeDPIListForCommerceItem for CommerceItem - "
									+ commerceItem.getCatalogRefId()
									+ ", Final Updated DPI list -"
									+ commerceItem.getPriceInfo()
											.getCurrentPriceDetails()));
		}

	}

	
	@SuppressWarnings( { "unchecked", "rawtypes" })
	private void updateExistingDPI(final CommerceItem commerceItem,
			final List<DetailedItemPriceInfo> dpiList) {
	    
	    
		for (final Iterator iterator = dpiList.iterator(); iterator.hasNext();) {
			final DetailedItemPriceInfo detailedItemPriceInfo = (DetailedItemPriceInfo) iterator
					.next();

			
			final List<DetailedItemPriceInfo> existingcommItemDPI = (List<DetailedItemPriceInfo>) commerceItem
					.getPriceInfo().getCurrentPriceDetailsForRange(
							detailedItemPriceInfo.getRange());

			final double cItemAmount = commerceItem.getPriceInfo().getAmount();
			final double perUnitDiscount = detailedItemPriceInfo.getAmount();
			final double discount = BigDecimal.valueOf(perUnitDiscount).multiply(BigDecimal.valueOf(detailedItemPriceInfo.getQuantity())).doubleValue();
					
			commerceItem.getPriceInfo().setAmount(getPricingTools().round(cItemAmount - discount));
			for (final Iterator iteratorInfo = existingcommItemDPI.iterator(); iteratorInfo
					.hasNext();) {
				final BBBDetailedItemPriceInfo dpi = (BBBDetailedItemPriceInfo) iteratorInfo.next();

				if (isLoggingDebug()) {
					logDebug(LogMessageFormatter.formatMessage(null,
							" ***Inprocess DPI Range = "
									+ detailedItemPriceInfo.getRange()
									+ ", Existing DPI Range = " + dpi.getRange() + "Inprocess DPI =" + detailedItemPriceInfo + ", Inprocess DPI End"));
				}

				
				if (!(detailedItemPriceInfo.getRange().getHighBound() == dpi
						.getRange().getHighBound() && detailedItemPriceInfo
						.getRange().getLowBound() == dpi.getRange()
						.getLowBound())) {
					
					final long startIndex = dpi.getRange().getLowBound();
					final long endIndex = dpi.getRange().getHighBound();
					final  BBBDetailedItemPriceInfo newDPI = new BBBDetailedItemPriceInfo(
							dpi);
					Range range = new Range();
					final double originalAmount = dpi.getAmount();
					final  double amountToAdjust = getPricingTools().round(
                            BigDecimal.valueOf(dpi.getAmount() / dpi.getQuantity()).multiply(BigDecimal.valueOf(detailedItemPriceInfo.getQuantity())).doubleValue());
					dpi.setAmount(amountToAdjust);
                    dpi.setQuantity(detailedItemPriceInfo.getQuantity());
					if (startIndex == detailedItemPriceInfo.getRange()
							.getLowBound()) {
						
						// set dpi per unit amount
						dpi.getRange().setHighBound(
								startIndex
										+ detailedItemPriceInfo.getQuantity()
										- 1);
						
						// Set range
						range.setLowBound(startIndex
								+ detailedItemPriceInfo.getQuantity());
						range.setHighBound(endIndex);
						newDPI.setRange(range);

						newDPI.setQuantity(range.getSize());
						newDPI.setAmount(getPricingTools().round(originalAmount - amountToAdjust));
						if (isLoggingDebug()) {
							logDebug(LogMessageFormatter
									.formatMessage(
											null,
											" ***Lower bound of InProcess DPI and existing DPI matched, New DPI Range="
													+ range
													+ ", Original DPI range updated to = "
													+ dpi.getRange()
													+ ", Original DPI Amount ="
													+ originalAmount
													+ ", Amount Share of existing DPI =  "
													+ dpi.getAmount()
													+ ", Amount Share of new DPI ="
													+ newDPI.getAmount()));
						}

						commerceItem.getPriceInfo().getCurrentPriceDetails()
								.add(newDPI);

					} else if (endIndex == detailedItemPriceInfo.getRange()
							.getHighBound()) {
												
						dpi.getRange().setLowBound(
								endIndex - detailedItemPriceInfo.getQuantity()
										+ 1);
						// set Range
						range.setLowBound(startIndex);
						range.setHighBound(endIndex
								- detailedItemPriceInfo.getQuantity());
						newDPI.setRange(range);
						newDPI.setQuantity(range.getSize());
						newDPI.setAmount(getPricingTools().round(originalAmount - amountToAdjust));
						if (isLoggingDebug()) {
							logDebug(LogMessageFormatter
									.formatMessage(
											null,
											" ***Upper bound of InProcess DPI and existing DPI matched, New DPI Range="
													+ range
													+ ", Original DPI range updated to = "
													+ dpi.getRange()
													+ ", Original DPI Amount -"
													+ originalAmount
													+ ", Amount Share of existing DPI =  "
													+ dpi.getAmount()
													+ ", Amount Share of new DPI ="
													+ newDPI.getAmount()));
						}

						commerceItem.getPriceInfo().getCurrentPriceDetails()
								.add(newDPI);
					} else {
						
						updateExistingDPIForInBetweenRange(commerceItem,
								detailedItemPriceInfo, dpi, startIndex,
								endIndex, newDPI, range, originalAmount,
								amountToAdjust);
					}
				}
				final double currentDpiDiscount = getPricingTools()
                        .round(BigDecimal.valueOf(perUnitDiscount).multiply(BigDecimal.valueOf(dpi.getQuantity())).doubleValue());
				dpi.setAmount(getPricingTools()
                        .round(dpi.getAmount() - currentDpiDiscount));
				dpi.getAdjustments().addAll(
						detailedItemPriceInfo.getAdjustments());
				removeDuplicatePricingAdjustments(dpi);
				dpi.setDiscounted(true);
				
			}
	

		}
	}
	
	/**
	 * Method to remove the duplicate pricing models from detailed price Info
	 * Adjust the Total Adjustment amount in case of duplicate pricing model.
	 * 
	 * @param dpi
	 */
	private void removeDuplicatePricingAdjustments(final DetailedItemPriceInfo dpi) {
		
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start-  removeDuplicatePricingAdjustments()"));
		}
		
		final List<PricingAdjustment> origList = dpi.getAdjustments();
		final List<PricingAdjustment> removeList = new ArrayList<PricingAdjustment>();
		for (int index = 0; index < origList.size(); index++) {
			final PricingAdjustment sourceAdustment = origList.get(index);
			for (int j = index + 1; j < origList.size(); j++) {
				final PricingAdjustment compareAdjustment = origList.get(j);
				if (null != sourceAdustment.getPricingModel()
						&& null != compareAdjustment.getPricingModel()
						&& sourceAdustment.getPricingModel().getRepositoryId()
								.equals(compareAdjustment.getPricingModel().getRepositoryId())) {
					if (isLoggingDebug()) {
						logDebug("Updating Total Adjustment to the duplicate pricing model with Id : "
								+ compareAdjustment.getPricingModel().getRepositoryId()
								+ "present in detailedPriceInfo with " + "Original Adjustment : "
								+ sourceAdustment.getTotalAdjustment() + "and Adjusted Amount : "
								+ compareAdjustment.getTotalAdjustment()
								+ "and removing the duplicate from the detailed price info");
					}
					/*
					 * If the promotions are for same coupons, only then we will
					 * remove the adjustment.
					 */
					if (sourceAdustment.getCoupon() == null
							|| compareAdjustment.getCoupon() == null
							|| compareAdjustment.getCoupon().getRepositoryId()
									.equalsIgnoreCase(sourceAdustment.getCoupon().getRepositoryId())) {
						vlogDebug(
								"BBBPostOrderDpiMergerCalculator.removeDuplicatePricingAdjustments: Coupon id: {0} is same for promotion id: {1} hence removing the adjustment.",
								compareAdjustment.getCoupon(), sourceAdustment.getPricingModel().getRepositoryId());
						(origList.get(index)).setTotalAdjustment(sourceAdustment.getTotalAdjustment()
								+ compareAdjustment.getTotalAdjustment());
						removeList.add(compareAdjustment);
					}
				}
			}
		}
		origList.removeAll(removeList);
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End-  removeDuplicatePricingAdjustments()"));
		}
	}
	
	@SuppressWarnings( { "unchecked" })
	private void updateExistingDPIForInBetweenRange(
			final CommerceItem commerceItem,
			final DetailedItemPriceInfo detailedItemPriceInfo,
			final DetailedItemPriceInfo dpi, final long startIndex,
			final long endIndex, final DetailedItemPriceInfo newDPI,
			Range range, final double originalAmount,
			final double amountToAdjust) {
		dpi.setRange(detailedItemPriceInfo.getRange());
		

		// set Range
		range.setLowBound(startIndex);
		range.setHighBound(detailedItemPriceInfo.getRange()
				.getLowBound() - 1);
		newDPI.setRange(range);
		
		newDPI.setQuantity(range.getSize());
		final double amountToAdjustForNewDPI = getPricingTools()
				.round(BigDecimal.valueOf(dpi.getAmount() / dpi.getQuantity()).multiply(BigDecimal.valueOf(newDPI.getQuantity())).doubleValue());
						
		newDPI.setAmount(amountToAdjustForNewDPI);

		final DetailedItemPriceInfo upperRangeNewDPI = new BBBDetailedItemPriceInfo(
				(BBBDetailedItemPriceInfo)dpi);
		range = new Range();
		range.setLowBound(detailedItemPriceInfo.getRange()
				.getHighBound() + 1);
		range.setHighBound(endIndex);
		upperRangeNewDPI.setRange(range);
		
		upperRangeNewDPI.setQuantity(range.getSize());
		upperRangeNewDPI.setAmount(getPricingTools().round(originalAmount
				- (amountToAdjustForNewDPI + amountToAdjust)));
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter
					.formatMessage(
							null,
							" ***Inprocess DPI range falls in between the existing DPI Range, Range for new DPI created for lower range = "
									+ newDPI.getRange()
									+ ", Range for new DPI created for upper range = "
									+ upperRangeNewDPI
											.getRange()
									+ ", Original DPI Amount ="
									+ originalAmount
									+ ", Amount Share of existing DPI = "
									+ dpi.getAmount()
									+ ", Amount Share of new DPI ="
									+ newDPI.getAmount()
									+ ", Amount Share of upper Range NewDPI ="
									+ upperRangeNewDPI
											.getAmount()));
		}

		// Add new DPIs to the commerceItem
		commerceItem.getPriceInfo().getCurrentPriceDetails()
				.add(newDPI);
		commerceItem.getPriceInfo().getCurrentPriceDetails()
				.add(upperRangeNewDPI);
	}

	/**
	 *  To Merge the same DPIs.
	 *  
	 * @param dpiList
	 * @param commerceItem 
	 */
	@SuppressWarnings( {"rawtypes" })
	private void mergeSameDPI(final List<DetailedItemPriceInfo> dpiList, CommerceItem commerceItem) {
		
	    	    
		final int initialSizeOfList = dpiList.size();
		int currentListSize = initialSizeOfList;
		
		for (Integer count = 0; count < initialSizeOfList; count++) {

			currentListSize = dpiList.size();

			if (count >= currentListSize) {
				break;
			}

			final DetailedItemPriceInfo dpi = dpiList.get(count);
			final Iterator iterator = dpiList.iterator();
			for (int i = 0; i <= count; i++) {//skipping already processed dpis
				iterator.next();
			}
			final DetailedItemPriceInfo existingcommItemDPI = (DetailedItemPriceInfo) commerceItem
                    .getPriceInfo().getCurrentPriceDetailsForRange(
                            dpi.getRange()).get(0);
			
			while(iterator.hasNext()) {
				final DetailedItemPriceInfo compareDPI = (DetailedItemPriceInfo) iterator
						.next();
				DetailedItemPriceInfo nextcommItemDPI = (DetailedItemPriceInfo) commerceItem
	                    .getPriceInfo().getCurrentPriceDetailsForRange(
	                            compareDPI.getRange()).get(0);
				if (dpi.toString().equals(compareDPI.toString()) &&
				        existingcommItemDPI.getRange().equals(nextcommItemDPI.getRange())) {
					iterator.remove();
					// remove items identical from the list
					dpi.getRange().setHighBound(
							dpi.getRange().getHighBound() + 1);
				}
			}
			dpi.setQuantity(dpi.getRange().getSize());
		}
		
		/*DetailedItemPriceInfo currentDpi = null;
        for (final Iterator iterator = dpiList.iterator(); iterator.hasNext();) {
             if(currentDpi == null) {
                 currentDpi = (DetailedItemPriceInfo) iterator
                     .next();
             }
             final DetailedItemPriceInfo existingcommItemDPI = (DetailedItemPriceInfo) commerceItem
                     .getPriceInfo().getCurrentPriceDetailsForRange(
                             currentDpi.getRange()).get(0);
             if(existingcommItemDPI.getRange().getSize() > 1) {
                 for(long count = currentDpi.getRange().getLowBound(); 
                         count < existingcommItemDPI.getRange().getHighBound(); count++) {
                     DetailedItemPriceInfo nextDpi = (DetailedItemPriceInfo) iterator
                             .next();
                     if(nextDpi.getAmount() == currentDpi.getAmount()) {
                         currentDpi.setQuantity(currentDpi.getQuantity() + 1);
                         currentDpi.getRange().setHighBound(currentDpi.getRange().getHighBound() + 1);
                         iterator.remove();
                     } 
                 }
             } 
         
     }*/
	}

}