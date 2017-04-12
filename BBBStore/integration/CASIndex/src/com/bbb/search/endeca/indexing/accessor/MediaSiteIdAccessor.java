package com.bbb.search.endeca.indexing.accessor;

import java.util.Set;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class MediaSiteIdAccessor extends PropertyAccessorImpl
		implements IndexConstants {
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String siteId = null;
		Set<RepositoryItem> pSiteID = null;
		if (pItem != null) {
			try {
				if(pPropertyName.equalsIgnoreCase("sites")){
				pSiteID = (Set<RepositoryItem>) pItem.getPropertyValue("sites");
				} else if(pPropertyName.equalsIgnoreCase("siteId")){
				pSiteID = (Set<RepositoryItem>) pItem.getPropertyValue("siteId");
				}
				if (pSiteID != null) {
					//HashMap siteIds = new HashMap();
					for (RepositoryItem siteIds : pSiteID) {
						siteId = (String) siteIds.getRepositoryId();
						if (siteId.equalsIgnoreCase("BedBathUS")) {
							return"1";
						} else if (siteId.equalsIgnoreCase("BuyBuyBaby")) {
							return"2";
						} else if (siteId.equalsIgnoreCase("BedBathCanada")) {
							return"3";
						}
				}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return null;
	}
}
