package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.constants.BBBCoreConstants;

public class OmnitureRecordIDAccessor extends PropertyAccessorImpl implements
IndexConstants {

/** 
* To create omniture search term record spec property.
* Format : OPB_siteID_SearchTerm
*/
@Override
protected Object getTextOrMetaPropertyValue(Context pContext,RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
	
	String recordID = null;
		if (null != pItem) {
			try {
				
				String siteId = (String) pItem.getPropertyValue(BBBCoreConstants.CONCEPT);				
				String searchTerm = (String) pItem.getPropertyValue(BBBCoreConstants.OPB_KEYWORD);									
				
				if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)) {
					siteId = BBBCoreConstants.SITE_BAB_US_VALUE;
				} else if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)) {
					siteId = BBBCoreConstants.SITE_BBB_VALUE;
				} else if (siteId.equalsIgnoreCase(BBBCoreConstants.CONCEPT_BAB_CA)) {
					siteId = BBBCoreConstants.SITE_BAB_CA_VALUE;
				}
				
				recordID = "OPB_"+siteId+"_" +searchTerm;
				vlogDebug("Site Id = {0} Search term = {1} and record id {2}",siteId,searchTerm,recordID);
			} catch (Exception exception) {
				vlogError("Exception in omniture record ID accessor "+exception.getMessage());
			}
		}
		return recordID;
	}

}