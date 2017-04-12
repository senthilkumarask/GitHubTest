/**
 * 
 */
package com.bbb.search.endeca.indexing.formatter;

import atg.core.util.StringUtils;
import atg.endeca.index.formatter.EndecaPropertyFormatter;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */
public class RollUpFormatter extends EndecaPropertyFormatter {

	/**
	 * 
	 */
	protected String format(String pPropertyName, String pOutputPropertyName,
			PropertyTypeEnum pType, Object pValue) {
		if(null==pValue){
			return null;
		}
		if(!StringUtils.isBlank(pValue.toString()) && pPropertyName.equalsIgnoreCase("prodRollupType")){		
			RepositoryItem item = (RepositoryItem) pValue;	 
			String value = item.getRepositoryId().toString();
		
		return value;
		}
		return null;
		 
	}

}
