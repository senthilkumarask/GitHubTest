/**
 * 
 */
package com.bbb.search.endeca.indexing.accessor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */
public class L2CategoryDimenssionNameAccessor extends PropertyAccessorImpl {
	
	 
	
	private String bedbathUSdimenssionName;
	private String bedbathCAdimenssionName;
	private String bedbathBABYdimenssionName;
	
	
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName,
			PropertyTypeEnum pType) {	
		String[] siteIdsFromRepoItem = null;
		Collection<String> siteContextValues;
		try {
			siteContextValues = pItem.getContextMemberships();
			 if ((siteContextValues != null) && (siteContextValues.size() > 0)) {
		          Set<String> ids = new HashSet<String>(siteContextValues);
		          ids.retainAll(pContext.getIndexInfo().getSiteIndexInfo().getSiteIDs());
		          siteIdsFromRepoItem = (String[])ids.toArray(new String[ids.size()]);
		        }
		        
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("error while fetching siteId from context - for L2Category baseline index "+e,e);
			}
		}
		 
		String siteId = siteIdsFromRepoItem[0];
		if(null==siteId){
			return getBedbathUSdimenssionName();
		}
		if(siteId.equalsIgnoreCase("BedBathUS"))
		{			 
			return getBedbathUSdimenssionName();
		}
		else if(siteId.equalsIgnoreCase("BedBathCanada")){
			return getBedbathCAdimenssionName();
		}
		else if(siteId.equalsIgnoreCase("BuyBuyBaby")){
			return getBedbathBABYdimenssionName();
		}
		
		return null;
	
	}


	/**
	 * @return the bedbathBABYdimenssionName
	 */
	public String getBedbathBABYdimenssionName() {
		return bedbathBABYdimenssionName;
	}


	/**
	 * @param bedbathBABYdimenssionName the bedbathBABYdimenssionName to set
	 */
	public void setBedbathBABYdimenssionName(String bedbathBABYdimenssionName) {
		this.bedbathBABYdimenssionName = bedbathBABYdimenssionName;
	}


	/**
	 * @return the bedbathCAdimenssionName
	 */
	public String getBedbathCAdimenssionName() {
		return bedbathCAdimenssionName;
	}


	/**
	 * @param bedbathCAdimenssionName the bedbathCAdimenssionName to set
	 */
	public void setBedbathCAdimenssionName(String bedbathCAdimenssionName) {
		this.bedbathCAdimenssionName = bedbathCAdimenssionName;
	}


	/**
	 * @return the bedbathUSdimenssionName
	 */
	public String getBedbathUSdimenssionName() {
		return bedbathUSdimenssionName;
	}


	/**
	 * @param bedbathUSdimenssionName the bedbathUSdimenssionName to set
	 */
	public void setBedbathUSdimenssionName(String bedbathUSdimenssionName) {
		this.bedbathUSdimenssionName = bedbathUSdimenssionName;
	}
	
	
	 

 
	
	

}
