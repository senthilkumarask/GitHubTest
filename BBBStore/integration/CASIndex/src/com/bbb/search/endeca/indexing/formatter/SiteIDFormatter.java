package com.bbb.search.endeca.indexing.formatter;

import atg.endeca.index.formatter.EndecaPropertyFormatter;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class SiteIDFormatter extends EndecaPropertyFormatter {

	protected String format(String pPropertyName, String pOutputPropertyName,
			PropertyTypeEnum pType, Object pValue) {
		String siteId = null;
		try {
			if (pValue != null) {
				siteId = (String) pValue;
				if (siteId.equalsIgnoreCase("BedBathUS")) {
					siteId = "1";
				} else if (siteId.equalsIgnoreCase("BuyBuyBaby")) {
					siteId = "2";
				} else if (siteId.equalsIgnoreCase("BedBathCanada") || siteId.equalsIgnoreCase("BEDBATHCA")) {
					siteId = "3";
				}
			}
			return siteId;
		} catch (ClassCastException e) {
			// ignore error
		}
		return "";
	}
}
