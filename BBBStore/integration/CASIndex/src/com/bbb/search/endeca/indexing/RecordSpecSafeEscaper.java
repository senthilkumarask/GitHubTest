package com.bbb.search.endeca.indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import atg.core.net.URLUtils;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.SafeEscaper;

public class RecordSpecSafeEscaper extends SafeEscaper {
	
	
	@Override
	public void addCurrentVariantParams(Context pContext, VariantParamIgnoring pIgnorer, StringBuilder pBuffer) {
		
		StringBuilder variantId = new StringBuilder();
		
		boolean bAddedIteratorProperties = false;
		String strURL = pContext.getCurrentURL();
		int idxQuestion = strURL.indexOf("?");
		if (idxQuestion != -1)
		{
			@SuppressWarnings("unchecked")
	    	Hashtable<String, String[]> hashParams = URLUtils.parseQueryString(strURL.substring(idxQuestion + 1));
	    	
	    	List<String> keys = new ArrayList<String>(hashParams.keySet());
			Collections.sort(keys);
			
			for (String strKey : keys) {
				String strValue = ((String[])hashParams.get(strKey))[0];

				boolean bShouldIgnore = pIgnorer == null ? false : pIgnorer.shouldIgnoreVariantParam(strKey);
				
				if (!bShouldIgnore)
				{
					if (bAddedIteratorProperties) {
						variantId.append('.');
					}
					else {
						bAddedIteratorProperties = true;
					}
					
					appendSafeString(variantId, strValue);
				}
			}
		}
		
		pBuffer.replace(0,pBuffer.toString().length(),"");
		appendSafeString(pBuffer,variantId.toString());
		pBuffer.append("_");

	}
		
}
