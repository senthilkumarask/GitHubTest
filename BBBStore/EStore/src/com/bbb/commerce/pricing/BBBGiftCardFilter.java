package com.bbb.commerce.pricing;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PromotionQualifyingFilter;
import com.bbb.common.BBBGenericService;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * To filter gift card item for promotions
 * 
 * @author msiddi
 * @story UC_Checkout_Item_Promo
 * @version 1.0
 */

public class BBBGiftCardFilter extends BBBGenericService implements
		PromotionQualifyingFilter {

	private BBBCatalogTools mCatalogTools;

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void filterItems(int filterContext, PricingContext pricingContext,
			Map extraParametersMap, Map detailsPendingActingAsQualifier,
			Map detailsRangesToReceiveDiscount, List filteredItems)
			throws PricingException {

		if (null == pricingContext ||
				null == pricingContext.getOrder() || filteredItems == null || filteredItems.isEmpty()) {
                logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +
	                		filteredItems + " pricingContext -" + pricingContext);	      
	            return;
		} else {

			for (Iterator<FilteredCommerceItem> itemIterator = filteredItems
					.iterator(); itemIterator.hasNext();) {
				FilteredCommerceItem filteredCommerceItem = (FilteredCommerceItem) itemIterator
						.next();

				try {
					// remove the item if it is a gift card item
					if (getCatalogTools()
							.isGiftCardItem(pricingContext.getOrder().getSiteId(),
									filteredCommerceItem.getWrappedItem().getCatalogRefId())) {
						itemIterator.remove();
					}

				} catch (BBBSystemException bse) {
					
				    if(isLoggingError()) {
				        logError(
								"BBBSystem Exception occured while identifying a gift card item ", bse);
				    }
					
				} catch (BBBBusinessException bbe) {
					
				    if(isLoggingError()) {
				        logError(
								"BBBBusiness Exception occured while identifying a gift card item ", bbe);
				    }
					

				}

			}

		}

	}
}
