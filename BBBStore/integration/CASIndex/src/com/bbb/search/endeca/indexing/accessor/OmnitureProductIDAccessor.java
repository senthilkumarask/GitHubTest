package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.constants.BBBCoreConstants;

public class OmnitureProductIDAccessor extends PropertyAccessorImpl implements
IndexConstants {
	
/** 
 * To create omniture search term product property with below rules.
 * Comma separated list of products, product with high boost score will come first in list
 */
@Override
protected Object getTextOrMetaPropertyValue(Context pContext,RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
	
		if (null != pItem) {
			try {
				
			RepositoryView repview =pItem.getRepository().getView(BBBCoreConstants.REPORT_DATA);
            QueryBuilder mQueryBuilder = repview.getQueryBuilder();
            QueryExpression keywordProperty = mQueryBuilder.createPropertyQueryExpression(BBBCoreConstants.OPB_KEYWORD);
            QueryExpression keywordValue = mQueryBuilder.createConstantQueryExpression((String) pItem.getPropertyValue(BBBCoreConstants.OPB_KEYWORD));
            final Query keywordQuery = mQueryBuilder.createComparisonQuery(keywordProperty, keywordValue, QueryBuilder.EQUALS);
            
            QueryExpression siteProperty = mQueryBuilder.createPropertyQueryExpression(BBBCoreConstants.CONCEPT);
            QueryExpression siteValue = mQueryBuilder.createConstantQueryExpression((String) pItem.getPropertyValue(BBBCoreConstants.CONCEPT));
            final Query siteQuery = mQueryBuilder.createComparisonQuery(siteProperty, siteValue, QueryBuilder.EQUALS);
            
            final SortDirectives sortDirectives = new SortDirectives();
			sortDirectives.addDirective(new SortDirective(BBBCoreConstants.BOOST_SCORE, SortDirective.DIR_DESCENDING));
            Query[] queries = { keywordQuery, siteQuery };
            Query query = mQueryBuilder.createAndQuery(queries);
            RepositoryItem[] repositoryItems = repview.executeQuery(query,sortDirectives);
            
            if(repositoryItems!=null) {
            	
            	StringBuilder products = new StringBuilder();
            	int i=0;           	
            	int length = repositoryItems.length;
            	for (RepositoryItem repositoryItem : repositoryItems) {
            		products.append( (String) repositoryItem.getPropertyValue(BBBCoreConstants.PRODUCTID));
				    if(i < length-1) {
				    	products.append(BBBCoreConstants.STRING_DEL);
				    }
            		i++;
				} 
            	
            	vlogDebug("Keyword = {0}, Site is = {1} and products are = {2}",keywordValue,siteValue,products );
            	return  products;
            }
		} catch (Exception exception) {
			vlogError("Exception in omniture product ID accessor "+exception.getMessage());			
	    }
		}
		return null;
	}

}