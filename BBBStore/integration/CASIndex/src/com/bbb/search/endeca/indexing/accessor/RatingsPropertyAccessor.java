package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;

public class RatingsPropertyAccessor extends PropertyAccessorImpl implements
		IndexConstants {

	//private MutableRepository catalogRepository;
	private BBBCatalogTools catalogTools;

	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {

		float ratings = 0;
		BazaarVoiceProductVO BV = null;

		if (pItem != null && pContext !=null) {

			String productId = pItem.getRepositoryId();
			
			//if(isLoggingDebug()){logDebug("productId****** \t:"+productId);}
			
			String currentSiteId = (String)pContext.getAttribute("atg.siteVariantProdcer.siteValue");
			
			 //if(isLoggingDebug()){logDebug("currentSiteId****** \t:"+currentSiteId);}

			if ((productId != null) && !StringUtils.isEmpty(productId) && (currentSiteId != null) && !StringUtils.isEmpty(currentSiteId)) {
				BV = getCatalogTools().getBazaarVoiceDetails(productId,currentSiteId);
				//if(isLoggingDebug()){logDebug("BV****** \t:"+BV);}
				ratings = BV.getAverageOverallRating();
				//if(isLoggingDebug()){logDebug("numRatings****** \t:"+ratings);}
			}

		}
		return ratings;
	}

	/**
	 * @return the catalogRepository
	 *//*
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	*//**
	 * @param pCatalogRepository
	 *            the catalogRepository to set
	 *//*
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}*/

	/**
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
}
