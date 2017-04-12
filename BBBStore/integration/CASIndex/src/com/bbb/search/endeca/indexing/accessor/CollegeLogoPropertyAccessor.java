package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.commerce.catalog.BBBCatalogConstants;

public class CollegeLogoPropertyAccessor extends PropertyAccessorImpl implements
		IndexConstants {
	
	private MutableRepository schoolVerRepository;
	
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		
		RepositoryItem schoolVerRepositoryItem = null;
		String collegeLogo = null;

		if (pItem != null) {
			String schoolId = (String)pItem.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME);
			
			if (isLoggingDebug()) {logDebug("schoolId ********* \t:" + schoolId);} // test log
		
		 if ((schoolId != null) && !schoolId.isEmpty()) {
	            try {
	                schoolVerRepositoryItem = this.getSchoolVerRepository().getItem(schoolId,
	                                BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR);
	                
	                if (isLoggingDebug()) {logDebug("schoolRepositoryItem ********* \t:" + schoolVerRepositoryItem);} // test log
	                
	                if (schoolVerRepositoryItem != null) {

	                	collegeLogo = (String)schoolVerRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_LOGO_URL_SCHOOL_PROPERTY_NAME);
	                     
	                }
	            }catch (final RepositoryException e) {
	            	e.printStackTrace();
	             }
		 }
		}
		return collegeLogo;
	}
	public MutableRepository getSchoolVerRepository() {
		return schoolVerRepository;
	}
	public void setSchoolVerRepository(MutableRepository schoolVerRepository) {
		this.schoolVerRepository = schoolVerRepository;
	}
}
