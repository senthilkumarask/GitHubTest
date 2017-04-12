package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.constants.BBBCoreConstants;

public class CollegeNamePropertyAccessor extends PropertyAccessorImpl implements
		IndexConstants {
	
	private MutableRepository schoolRepository;
	
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		
		RepositoryItem schoolRepositoryItem = null;
		String schoolName = null;
		
		if (isLoggingDebug()) {logDebug("RepositoryItem ********* \t:" + pItem);}

		if (pItem != null) {
			String schoolId = (String)pItem.getPropertyValue("college");
			
			if (isLoggingDebug()) {logDebug("schoolId ********* \t:" + schoolId);} // test log
		
		 if ((schoolId != null) && !schoolId.isEmpty()) {
	            try {
	                schoolRepositoryItem = this.getSchoolRepository().getItem(schoolId,
	                                BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR);
	                
	                if (isLoggingDebug()) {logDebug("schoolRepositoryItem ********* \t:" + schoolRepositoryItem);} // test log
	                
	                if (schoolRepositoryItem != null) {

	                     schoolName = (String) schoolRepositoryItem.getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME);
	                     
	                     if (isLoggingDebug()) {logDebug("schoolName ********* \t:" + schoolName);} // test log
	                }
	            }catch (final RepositoryException e) {
	            	e.printStackTrace();
	             }
		 }
		}
		return schoolName;
	}
	/** @return the schoolRepository */
    public final MutableRepository getSchoolRepository() {
        return this.schoolRepository;
    }

    /** @param schoolRepository the schoolRepository to set */
    public final void setSchoolRepository(final MutableRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }
}
