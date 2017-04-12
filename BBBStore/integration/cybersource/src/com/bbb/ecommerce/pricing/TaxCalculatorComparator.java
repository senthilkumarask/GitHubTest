
package com.bbb.ecommerce.pricing;


import java.util.Comparator;

import atg.commerce.pricing.DetailedItemPriceInfo;

/**
 * @author 
 *
 */
public class TaxCalculatorComparator implements Comparator<DetailedItemPriceInfo>{

	/**
	 * This method is used to compare delivery charge in ShipMethodVO
	 */
	@Override
	public int compare(final DetailedItemPriceInfo item1,
			final DetailedItemPriceInfo item2) {

		final Long qty1 = item1.getQuantity();
		final Long qty2 = item2.getQuantity();
		if (qty1.compareTo(qty2) == -1) {
			return -1;
		} else if(qty1.compareTo(qty2) == 1){
			return 1;
		} else{
			return 0;
		}

	}

}