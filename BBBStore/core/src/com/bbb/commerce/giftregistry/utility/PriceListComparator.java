package com.bbb.commerce.giftregistry.utility;

import java.util.Comparator;

import com.bbb.commerce.giftregistry.vo.RegistryItemVO;


/**
 * This class is used to sort the list
 * @author ssha53
 *
 */
public class PriceListComparator implements Comparator<RegistryItemVO>{

/**
 * This method is used to compare price in RegistryItemVO	
 */
@Override
public int compare(final RegistryItemVO arg0, final RegistryItemVO arg1) {
	
	final Double price1 = Double.parseDouble((String)arg0.getPrice());
	final Double price2 = Double.parseDouble((String)arg1.getPrice());
	return price1.compareTo(price2);
}
	

}
