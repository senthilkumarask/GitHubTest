/**
 * 
 */
package com.bbb.search.endeca.indexing.formatter;

import atg.endeca.index.formatter.EndecaPropertyFormatter;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */
public class JDAFormatter extends EndecaPropertyFormatter {

	/**
	 * 
	 */
	protected String format(String pPropertyName, String pOutputPropertyName,
			PropertyTypeEnum pType, Object pValue) {
		if(null==pValue){
			return null;
		}
		RepositoryItem item = (RepositoryItem) pValue;
		
		if(pPropertyName.equalsIgnoreCase("jdaDept")){
		String value = item.getRepositoryId().toString();
		return value;
		}
		else if(pPropertyName.equalsIgnoreCase("jdaSubDept")){
			String value = item.getRepositoryId().toString();
			return value;
		}
		return null;
		 
	}

}
