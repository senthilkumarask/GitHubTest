package com.bbb.search.endeca.indexing.accessor;


import atg.commerce.search.IndexConstants;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class PriorityPropertyAccessor extends PropertyAccessorImpl
		implements IndexConstants {
	
	private Repository searchConfig;

	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		if (isLoggingDebug()) {logDebug("MemberNameAccessor********* \t:"+pItem);}
		logDebug("true");
		if (pItem != null){
	            Boolean isSearchable = (Boolean) pItem.getPropertyValue("isSearchable");
    			if((isSearchable != null) && (isSearchable== true)){
    				int priority = (int) pItem.getPropertyValue("priority");
    				return priority;
    			}
			}
		
		return null;
			}

	public Repository getSearchConfig() {
		return searchConfig;
	}

	public void setSearchConfig(Repository searchConfig) {
		this.searchConfig = searchConfig;
	}
		}
  
		  
		  