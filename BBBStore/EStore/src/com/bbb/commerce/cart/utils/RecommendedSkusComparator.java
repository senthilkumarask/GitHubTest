package com.bbb.commerce.cart.utils;

import java.util.Comparator;

import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;


/**
 * This class is used to sort the list
 * @author pbhoomul
 *
 */
public class RecommendedSkusComparator implements Comparator<RepositoryItem>{

	/**
	 * This method is used to compare price in RegistryItemVO	
	 */
	@Override
	public int compare(final RepositoryItem arg0, final RepositoryItem arg1) {
		
		final Double price1 = sortedPropertyValue(arg0);
		final Double price2 = sortedPropertyValue(arg1);
		return price1.compareTo(price2);
	}
	
	private Double sortedPropertyValue(RepositoryItem pItem) {
		Double sortedPropertyValue = getValidSortedPropertyValue(pItem, BBBCatalogConstants.PROPERTY_NAME_PRIORITY);
		if (sortedPropertyValue != null) {
			return sortedPropertyValue;
		} 
		return (new Double(0));
	}

	private Double getValidSortedPropertyValue(RepositoryItem pItem, String pClassifiedSortedProperty) {
	
		if (pItem.getPropertyValue(pClassifiedSortedProperty) != null
				&& !StringUtils.isEmpty(pItem.getPropertyValue(pClassifiedSortedProperty).toString())) {
			return Double.parseDouble(pItem.getPropertyValue(pClassifiedSortedProperty).toString());
		}
		return null;
	}

}
