package com.bbb.search.endeca.indexing.formatter;

import java.util.Date;

import atg.repository.search.indexing.specifier.PropertyTypeEnum;
import atg.endeca.index.formatter.EndecaPropertyFormatter;

public class EnabledAgePropertyFormatter extends EndecaPropertyFormatter {
	@Override
	protected String format(String pPropertyName, String pOutputPropertyName, PropertyTypeEnum pType, Object pValue)
	  {
		long dateDiff;
		try {
			if (pValue!=null){
			dateDiff = (new Date().getTime() - ((Date) pValue).getTime());
			if(dateDiff > 0) {
				dateDiff = dateDiff / (1000*60*60*24);
			}
			return String.valueOf(dateDiff);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return "";
		
	  }

}
	