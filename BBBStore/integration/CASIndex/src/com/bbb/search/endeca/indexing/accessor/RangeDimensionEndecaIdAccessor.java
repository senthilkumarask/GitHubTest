package com.bbb.search.endeca.indexing.accessor;

import com.bbb.search.endeca.indexing.accessor.helper.AccessorConstants;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class RangeDimensionEndecaIdAccessor extends PropertyAccessorImpl implements IndexConstants {
	
	/**
	 * <p>
	 * This method will construct an EndecaId for price ranges based on dimName & dimSpec values.
	 * </p>
	 */
	
	protected Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {	
		if(isLoggingDebug()){
			logDebug("Start RangeDimensionEndecaIdAccessor.getTextOrMetaPropertyValue()");
		}
		if(isLoggingDebug()){
			logDebug("This line has been added for testing only");
		}
		String dimName=(String)pItem.getPropertyValue("dimName");
		String dimSpec=(String)pItem.getPropertyValue("dimSpec");
		
		String endecaId=dimName+AccessorConstants.COLON_SYMBOL+dimSpec;
		if(isLoggingDebug()){
			logDebug("End RangeDimensionEndecaIdAccessor.getTextOrMetaPropertyValue() and value for endecaId is: "+endecaId);
		}
	return endecaId;
	}
}
