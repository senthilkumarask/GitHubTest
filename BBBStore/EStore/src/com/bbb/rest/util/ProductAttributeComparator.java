/**
 * 
 */
package com.bbb.rest.util;

import java.util.Comparator;

import com.bbb.commerce.catalog.vo.AttributeVO;

/**
 * 
 * This class is used to sort product attribute list on the basis of priority.
 * 
 * @author ssha53
 *
 */
public class ProductAttributeComparator implements Comparator<AttributeVO>{

	/**
	 * This method is used to compare priority and return 0 id priority of first attribute
	 * less then priority of send attribute else return 1.
	 * 
	 */
	@Override
	public int compare(AttributeVO attributeVO1, AttributeVO attributeVO2) {
		final Integer priority1 = Integer.valueOf(attributeVO1.getPriority());
		final Integer priority2 = Integer.valueOf(attributeVO2.getPriority());
		return priority1.compareTo(priority2);
	}

}
