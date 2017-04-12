package com.bbb.commerce.pricing;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PromotionQualifyingFilter;
import com.bbb.common.BBBGenericService;

import com.bbb.order.bean.NonMerchandiseCommerceItem;

/**
 * To filter Non Merchandise Commerce item for promotions
 * 
 * @author msiddi
 * @story UC_Checkout_Item_Promo
 * @version 1.0
 */

public class BBBNonMerchandiseCommerceItemFilter extends BBBGenericService
		implements PromotionQualifyingFilter {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void filterItems(int filterContext, PricingContext pricingContext,
			Map extraParametersMap, Map detailsPendingActingAsQualifier,
			Map detailsRangesToReceiveDiscount, List filteredItems)
			throws PricingException {

		if (filteredItems == null || filteredItems.isEmpty()) {
			logDebug("Leaving BBBNonMerchandiseCommerceItemFilter.filterItems because filteredItems is a null list");
			return;
		} else {

			for (Iterator<FilteredCommerceItem> itemIterator = filteredItems
					.iterator(); itemIterator.hasNext();) {
				FilteredCommerceItem filteredCommerceItem = (FilteredCommerceItem) itemIterator
						.next();

				// remove the item if it is a Non Merchandise Commerce item
				if (filteredCommerceItem.getWrappedItem() instanceof NonMerchandiseCommerceItem) {
					itemIterator.remove();
				}

			}

		}

	}
}
