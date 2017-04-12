package com.bbb.search.endeca.indexing.accessor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import atg.commerce.endeca.index.dimension.CategoryNodePropertyAccessor;
import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class CategorySiteSpecificSpecPropertyAccessor extends CategoryNodePropertyAccessor implements IndexConstants {
	
	
	private String categorySite;
	
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType)
	{
		Object superValue = super.getTextOrMetaPropertyValue(pContext, pItem, pPropertyName, pType);
		
		try {
			String[] siteIdsFromRepoItem = null;
			Collection<String> siteContextValues = pItem.getContextMemberships();
	        if ((siteContextValues != null) && (siteContextValues.size() > 0)) {
	          Set<String> ids = new HashSet<String>(siteContextValues);
	          ids.retainAll(pContext.getIndexInfo().getSiteIndexInfo().getSiteIDs());
	          siteIdsFromRepoItem = (String[])ids.toArray(new String[ids.size()]);
	        }
	        
	        String currentSiteId = (String)pContext.getAttribute("atg.siteVariantProdcer.siteValue");;
		    for(int i=0;siteIdsFromRepoItem!= null && i<siteIdsFromRepoItem.length;i++) {
		    	if(null!=currentSiteId && currentSiteId.equalsIgnoreCase(siteIdsFromRepoItem[i])) {
		    		//logDebug("siteIdsFromRepoItem[i] - "+siteIdsFromRepoItem[i]+" matches currentSiteId - "+currentSiteId);
		    		if(currentSiteId.equalsIgnoreCase(getCategorySite()))
		    			return superValue;
		    	}
		    }
		    
		} catch(RepositoryException re) {
			logError(re.getMessage());
		}
		
		return null;
	}

	/**
	 * @return the categorySite
	 */
	public String getCategorySite() {
		return categorySite;
	}

	/**
	 * @param categorySite the categorySite to set
	 */
	public void setCategorySite(String categorySite) {
		this.categorySite = categorySite;
	}

}
