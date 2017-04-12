package com.bbb.utils;

import java.util.Comparator;

import com.bbb.commerce.catalog.vo.ShipMethodVO;

/**
 * This class is used to sort the list
 * 
 * @author naga13
 * 
 */
public class DeliveryChargeComparator implements Comparator<ShipMethodVO> {

	/**
	 * This method is used to compare delivery charge in ShipMethodVO
	 */
	@Override
	public int compare(final ShipMethodVO shipMethodVO1,
			final ShipMethodVO shipMethodVO2) {

		final double deliveryCharge1 = shipMethodVO1.getDeliverySurcharge();
		final double deliveryCharge2 = shipMethodVO2.getDeliverySurcharge();
		if (deliveryCharge1 < deliveryCharge2) {
			return -1;
		} else {
			return 1;
		}

	}

}
