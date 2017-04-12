package com.bbb.search.endeca.indexing.accessor;


import atg.commerce.search.IndexConstants;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class SearchInterfaceAccessor extends PropertyAccessorImpl
		implements IndexConstants {
	
	private Repository searchConfig;

	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		if (isLoggingDebug()) {logDebug("MemberNameAccessor********* \t:"+pItem);}
		logDebug("true");
		if (pItem != null){
			try {
				RepositoryView repview =pItem.getRepository().getView("searchConfigGroups");
	            QueryBuilder mQueryBuilder = repview.getQueryBuilder();
	            QueryExpression expressionProperty = mQueryBuilder.createPropertyQueryExpression("searchItemId");
	              QueryExpression expressionValue = mQueryBuilder.createConstantQueryExpression(pItem.getRepositoryId().toString());
	              final Query query = mQueryBuilder.createComparisonQuery(expressionProperty, expressionValue,
                          QueryBuilder.EQUALS);
	            RepositoryItem[] searchConfigRepositoryItems = repview.executeQuery(query);
	            RepositoryItem searItem = searchConfigRepositoryItems[0];
	            logDebug("    "+searchConfigRepositoryItems);
	            Boolean isSearchable = (Boolean) pItem.getPropertyValue("isSearchable");
    			if((isSearchable != null) && (isSearchable== true)){
    				String groupName = (String) searItem.getPropertyValue("groupName");
    				return groupName;
    			}
	        }catch (RepositoryException e1) {
				e1.printStackTrace();
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
  
		  
		  