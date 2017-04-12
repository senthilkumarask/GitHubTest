/**
 * 
 */
package com.bbb.commerce.catalog;

 

import java.util.ArrayList;
import java.util.List;



import atg.nucleus.Nucleus;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

/**
 * @author sm0191
 *
 */
public class SKUFacetsTranslation extends RepositoryPropertyDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 839515181821729412L;

 
	
	@SuppressWarnings("unchecked")
	public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
		Repository skuFacetRepository = getSkuFacetRepository();
		List<RepositoryItem> skuFacetList = (List<RepositoryItem>) new ArrayList();
		RepositoryView skuFacetView;
		RepositoryItem[] skuFacetItem = null;
		try {
			skuFacetView = skuFacetRepository.getView("skuFacet");
			RqlStatement skuFacetStatement = RqlStatement.parseRqlStatement("itemId=?0");
			Object skuFacetParams[] = new Object[1]; 
			skuFacetParams[0] = pItem.getRepositoryId();
			 skuFacetItem = invokeSkuFacetRepoQuery(skuFacetView, skuFacetStatement, skuFacetParams);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null!=skuFacetItem){
		for(int i = 0;i<skuFacetItem.length;i++){
			RepositoryItem faceItem = skuFacetItem[i];
			faceItem.getRepositoryId();
			skuFacetList.add(faceItem);
		}
		}
		
	return skuFacetList;	
	
	}



	protected RepositoryItem[] invokeSkuFacetRepoQuery(RepositoryView skuFacetView, RqlStatement skuFacetStatement,
			Object[] skuFacetParams) throws RepositoryException {
		return skuFacetStatement.executeQuery(skuFacetView,skuFacetParams);
	}



	protected Repository getSkuFacetRepository() {
		return (Repository) Nucleus.getGlobalNucleus().resolveName("/com/bbb/ipad/SkuFacetRepository");
	}

}
