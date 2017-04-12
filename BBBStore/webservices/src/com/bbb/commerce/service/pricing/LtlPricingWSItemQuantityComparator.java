/**
 * 
 */
package com.bbb.commerce.service.pricing;

import java.math.BigInteger;
import java.util.Comparator;

import com.bedbathandbeyond.atg.Item;

/**
 * This Calculator is used in LTL for Pricing WS to sort the items in descending order of quantity.
 *
 */
public class LtlPricingWSItemQuantityComparator implements Comparator<Item>{

	/**
	 * This method is used to compare delivery charge in ShipMethodVO
	 */
	@Override
	public int compare(final Item item1,
			final Item item2) {

		final BigInteger qty1 = item1.getQuantity();
		final BigInteger qty2 = item2.getQuantity();
		if (qty1.compareTo(qty2) == -1) {
			return 1;
		} else if(qty1.compareTo(qty2) == 1){
			return -1;
		} else{
			return 0;
		}

	}

}
