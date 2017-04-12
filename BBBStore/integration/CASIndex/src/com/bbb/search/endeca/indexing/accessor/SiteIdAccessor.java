package com.bbb.search.endeca.indexing.accessor;

import java.util.Set;

import atg.commerce.search.IndexConstants;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class SiteIdAccessor extends PropertyAccessorImpl
		implements IndexConstants {

	private MutableRepository guidesTemplate;

	@SuppressWarnings("unchecked")
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String siteId = null;
		if (pItem != null) {
			try {
				Set<RepositoryItem> guideID = (Set<RepositoryItem>) pItem.getPropertyValue("site");
				if (guideID != null) {
					//HashMap siteIds = new HashMap();
					for (RepositoryItem siteIds : guideID) {
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

	public MutableRepository getGuidesTemplate() {
		return guidesTemplate;
	}

	public void setGuidesTemplate(MutableRepository guidesTemplate) {
		this.guidesTemplate = guidesTemplate;
	}
	
}
