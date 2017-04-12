/**
 * 
 */
package com.bbb.search.endeca.indexing.formatter;

import atg.core.util.StringUtils;
import atg.endeca.index.formatter.EndecaPropertyFormatter;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */
public class DimValDisplayNameFormatter extends EndecaPropertyFormatter {

	protected String format(String pPropertyName, String pOutputPropertyName,
			PropertyTypeEnum pType, Object pValue) {
		if(null==pValue){
			return null;
		}
		String dimValDisplayName = "";
		String dispName=pValue.toString(); 
		if(!StringUtils.isBlank(dispName)){
		dimValDisplayName=  dispName.replaceAll("[^A-Za-z0-9.\\-_]", "_");
		}
		
		return dimValDisplayName;
	}
}
