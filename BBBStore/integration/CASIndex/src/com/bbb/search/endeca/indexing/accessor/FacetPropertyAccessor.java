/**
 * 
 */
package com.bbb.search.endeca.indexing.accessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.AdditionalPropertiesMapPropertyAccessor;
import atg.repository.search.indexing.AdditionalPropertiesPropertyAccessor;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.IndexingOutputConfig;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.ReplacePropertyPropertyAccessor;
import atg.repository.search.indexing.specifier.OutputItemSpecifier;
import atg.repository.search.indexing.specifier.OutputProperty;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */
public class FacetPropertyAccessor extends PropertyAccessorImpl implements ReplacePropertyPropertyAccessor {
	
 
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String facetType = null;
		String facetValue = (String) pItem.getPropertyValue("facetValue");
		
		if(!facetValue.equalsIgnoreCase("N/A")){
			RepositoryItem facetTypeItem = (RepositoryItem) pItem.getPropertyValue("facetType");			
			String facetTypeDescription =(String) facetTypeItem.getPropertyValue("description");
			facetType=facetTypeDescription+":"+facetValue;
		}
		return facetType;
		 
	}


	@Override
	public OutputProperty[] getReplacementPropertyDefs(Context pContext,
			RepositoryItem pRepositoryItem, OutputProperty pOutputProperty) {
		
		List<OutputProperty> outputPropertiesList = new ArrayList<OutputProperty>();
		
		if(pOutputProperty.getOutputName().equals("P_FEATURE")) {
			OutputProperty output = pOutputProperty.createCopy();
			
			RepositoryItem facetTypeItem = (RepositoryItem) pRepositoryItem.getPropertyValue("facetType");			
			String facetTypeDescription =(String) facetTypeItem.getPropertyValue("description");
			
			String dimValDisplayName = "";
			if(!StringUtils.isBlank(facetTypeDescription)){
			dimValDisplayName=  facetTypeDescription.replaceAll("[^A-Za-z0-9.\\-_]", "_");
			}
			
			output.setOutputName(dimValDisplayName);
			outputPropertiesList.add(output);
		}
		
		return (OutputProperty[])outputPropertiesList.toArray(new OutputProperty[0]);
	}
		 
}
