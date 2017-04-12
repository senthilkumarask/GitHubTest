package com.bbb.commerce.catalog;

import java.util.Map;

import com.bbb.exception.BBBSystemException;

public interface BBBInternationalCatalogTools {
	public Map<String,String> getAttributeInfo() throws BBBSystemException ;
}
