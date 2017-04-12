package com.bbb.search.endeca.indexing.accessor;


import atg.commerce.search.IndexConstants;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class MemberNameAccessor extends PropertyAccessorImpl
		implements IndexConstants {
	
	private Repository searchConfig;

	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		if (isLoggingDebug()) {logDebug("MemberNameAccessor********* \t:"+pItem);}
		if (pItem != null){
	            Boolean isSearchable = (Boolean) pItem.getPropertyValue("isSearchable");
    			if((isSearchable != null) && (isSearchable== true)){
    				String type = (String) pItem.getPropertyValue("type");
    				if(type.equalsIgnoreCase("propertyItemRelationalObj")){
    				 RepositoryItem propertyItem = (RepositoryItem) pItem.getPropertyValue("property");
    		         String propertyname =  (String) propertyItem.getPropertyValue("propertyName");
    				 return propertyname;
    				}
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
  
		  
		  