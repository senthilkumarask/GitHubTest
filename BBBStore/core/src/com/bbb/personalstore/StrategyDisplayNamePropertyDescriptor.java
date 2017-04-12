package com.bbb.personalstore;

import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;

import com.bbb.constants.BBBCoreConstants;

public class StrategyDisplayNamePropertyDescriptor extends GSAPropertyDescriptor {
	/**
	 * generated UID.
	 */
	private static final long serialVersionUID = -1342026296442111782L;

	/**
	 * Returns the concatenated value of underlying property of
	 * strategyPageTitle and strategyLayoutName.
	 * 
	 * @param pItem
	 *            the RepositoryItem to retrieve the value from
	 * @param pValue
	 *            the value to retrieve
	 * @return The property value requested
	 */
	@Override
	public Object getPropertyValue(final RepositoryItemImpl pItem, final Object pValue) {
			
		String concatlayoutName = "";
	 
		if (pItem != null) {
			final String strategyPageTitle = (String) pItem.getPropertyValue(BBBCoreConstants.STRATEGY_PAGE_TITLE);
			final RepositoryItem layoutRepoItem = (RepositoryItem) pItem.getPropertyValue(BBBCoreConstants.STRATEGY_LAYOUT);
			if (null != layoutRepoItem ) {
				final String layoutName = (String) layoutRepoItem.getPropertyValue(BBBCoreConstants.LAYOUT_NAME);
				if (null != layoutName && null != strategyPageTitle) {
					concatlayoutName = strategyPageTitle.concat(BBBCoreConstants.CONCAT_DELIMITER).concat(layoutName);
				}
			}
			
		} 
		return concatlayoutName;
	}

}
