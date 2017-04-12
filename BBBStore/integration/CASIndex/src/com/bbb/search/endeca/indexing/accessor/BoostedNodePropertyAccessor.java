package com.bbb.search.endeca.indexing.accessor;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.search.IndexConstants;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class BoostedNodePropertyAccessor extends PropertyAccessorImpl implements
		IndexConstants {

	private MutableRepository catalogRepository;

	@SuppressWarnings("unchecked")
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {

		String productSeqNumber = null;
		String boostedNode = null;
		String categoryId = null;
		
		if (pItem != null && pContext !=null) {

				//MutableRepositoryItem categoryItem = (MutableRepositoryItem) catalogRepository.getItem(categoryID,"category");
				Map<String, String> productCatSeqNumMap = new HashMap<String, String>();
				productCatSeqNumMap =  (Map<String,String>)pItem.getPropertyValue("productSeqNum");
				if (productCatSeqNumMap!=null && !(pContext.getDocumentRepositoryId()).isEmpty()){
				productSeqNumber = productCatSeqNumMap.get(pContext.getDocumentRepositoryId());
				
					try{
						if (!(productSeqNumber).isEmpty() && productSeqNumber.equalsIgnoreCase("10")){
					categoryId = pItem.getRepositoryId();
					boostedNode = categoryId;
					return boostedNode;
				}//System.out.println("productCatSeqNumMap :"+productSeqNumber);
			} catch (Exception e) {
				// Ignore error
			}
		}
		}
		return null;
	}

	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param pCatalogRepository
	 *            the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
}
