package com.bbb.search.endeca.indexing;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.DocumentSafeIdGenerator;
import atg.repository.search.indexing.SafeEscaper;

/**
 * RecordSpecDocumentSafeIdGenerator class would create SafeEscaper of type RecordSpecSafeEscaper 
 * instead of default SafeEscaper  
 * 
 * @author sc0054
 *
 */
public class RecordSpecDocumentSafeIdGenerator extends DocumentSafeIdGenerator {

	@Override
	protected SafeEscaper createSafeEscaper()
	{
		return new RecordSpecSafeEscaper();
	}
	
	@Override
	public String generateDocumentId(Context pContext, RepositoryItem pItem)
	{
		String descriptorName = null;
		try
		{
			descriptorName = pItem.getItemDescriptor().getItemDescriptorName();
		}
		catch (RepositoryException e)
		{
			
		}
		return generateDocumentId(pContext, pItem.getRepositoryId(), descriptorName);
	}
	
	
	private String generateDocumentId(Context pContext, String pItemId, String pDescriptorName)
	{
		StringBuilder strbuf = new StringBuilder(pItemId.length() + 20);
		
		String strTypePrefix = pDescriptorName;
		if (StringUtils.isBlank(strTypePrefix)) {
			strTypePrefix = "unknown";
		}
		
		if (getIdTypeToPrefixMap() != null) {
			String strReplacement = (String)getIdTypeToPrefixMap().get(strTypePrefix);
			
			if (strReplacement != null) {
				strTypePrefix = strReplacement;
			}
		}
		
		SafeEscaper escaper = getSafeEscaper();
		
		escaper.addCurrentVariantParams(pContext, this, strbuf);

		escaper.appendSafeString(strbuf, pItemId);

		return strbuf.toString();
	}
	
}
