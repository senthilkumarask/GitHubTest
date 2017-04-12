package com.bbb.search.endeca.indexing.formatter;

import atg.repository.search.indexing.specifier.PropertyTypeEnum;
import atg.endeca.index.formatter.EndecaPropertyFormatter;

public class DatePropertyFormatter extends EndecaPropertyFormatter {
	
	@Override
	protected String format(String pPropertyName, String pOutputPropertyName, PropertyTypeEnum pType, Object pValue)
	  {
		long dateInSec;
		long dateInMilleSec;
		if (pValue != null){
		try {
			java.sql.Date sqlDate = (java.sql.Date) pValue;
			dateInMilleSec = sqlDate.getTime();
			dateInSec = (long) (dateInMilleSec / (1000.0));
			return String.valueOf(dateInSec);	
		} catch (ClassCastException e) {
			//ignore error
		}
		}
		return "";
	  }
}