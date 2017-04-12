package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.commerce.catalog.BBBCatalogConstants;

public class CollegeStatePropertyAccessor extends PropertyAccessorImpl implements
		IndexConstants {
	
	private MutableRepository schoolRepository;
	private MutableRepository shippingRepository;
	
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		
		RepositoryItem schoolRepositoryItem = null;
		String stateName = null;

		if (pItem != null) {
			String schoolId = (String)pItem.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME);
			
			if (isLoggingDebug()) {logDebug("schoolId ********* \t:" + schoolId);} // test log
		
		 if ((schoolId != null) && !schoolId.isEmpty()) {
	            try {
	                schoolRepositoryItem = this.getSchoolRepository().getItem(schoolId,
	                                BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR);
	                
	                if (isLoggingDebug()) {logDebug("schoolRepositoryItem ********* \t:" + schoolRepositoryItem);} // test log
	                
	                if (schoolRepositoryItem != null) {

	                	String schoolState = (String)schoolRepositoryItem.getPropertyValue(BBBCatalogConstants.STATE_SCHOOL_PROPERTY_NAME);
	                     
	                     if (isLoggingDebug()) {logDebug("schoolState ********* \t:" + schoolState);} // test log
	                     
	                     RepositoryItem statesRepositoryItem = this.getShippingRepository().getItem(schoolState.trim(),
	                             BBBCatalogConstants.STATES_ITEM_DESCRIPTOR);
	                     
	                     if (isLoggingDebug()) {logDebug("statesRepositoryItem ********* \t:" + statesRepositoryItem);} // test log
	                     
	             if ((statesRepositoryItem != null)
	                             && (statesRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) != null)) {
	                 stateName = (String) statesRepositoryItem
	                                 .getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME);
	                 
	                 if (isLoggingDebug()) {logDebug("stateName ********* \t:" + stateName);} // test log
	             }
	                }
	            }catch (final RepositoryException e) {
	            	e.printStackTrace();
	             }
		 }
		}
		return stateName;
	}
	/** @return the schoolRepository */
    public final MutableRepository getSchoolRepository() {
        return this.schoolRepository;
    }

    /** @param schoolRepository the schoolRepository to set */
    public final void setSchoolRepository(final MutableRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }
    /** @return the shippingRepository */
    public final MutableRepository getShippingRepository() {
        return this.shippingRepository;
    }

    /** @param shippingRepository the shippingRepository to set */
    public final void setShippingRepository(final MutableRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }
}
