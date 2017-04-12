package com.bbb.search.endeca.indexing.producer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import atg.endeca.index.EndecaIndexingOutputConfig;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.producer.UniqueSiteVariantProducer;

import com.bbb.commerce.catalog.BBBCatalogConstants;

/**
 * @author sc0054
 *
 */
public class ProdEnabledWebOfferedVariantProducer extends UniqueSiteVariantProducer {
	
	/** The Constant SITE_PROPERTY. */
	private static final String SITE_PROPERTY = "site";
	
	/** The Constant Default Value */
	private static final String PROPERTY_DEFAULT_SUFFIX = "Default";
	
	/** The Constant ATTRIBUTE_PROPERTY. */
	private static final String ATTRIBUTE_PROPERTY = "attributeName";

	/** The Constant BOOLEAN_PROPERTY. */
	private static final String BOOLEAN_PROPERTY = "attributeValueBoolean";

	
	/* (non-Javadoc)
	 * @see atg.repository.search.indexing.producer.UniqueSiteVariantProducer#prepareNextVariant(atg.repository.search.indexing.Context, java.lang.String, atg.repository.RepositoryItem, int, java.util.Map)
	 */
	@Override
	public boolean prepareNextVariant(Context pContext, String pPropertyName, RepositoryItem pItem, int pIndex, Map pUniqueParams) {
		String[] siteIds = null;
		int index;
		/*if(pItem.getPropertyValue(BBBCatalogConstants.INDEX_PRODUCT_COLL_PRODUCT_PROPERTY_NAME) != null
				&& !(Boolean)pItem.getPropertyValue(BBBCatalogConstants.INDEX_PRODUCT_COLL_PRODUCT_PROPERTY_NAME)) {
			logInfo(pItem.getRepositoryId()+" Product's index flag is false");
			return false;
		}*/
		if (pIndex == 0)
		{
			try
			{
				if (!isForceAllSites()) {
					//fetch siteIds based on web offered and prod disable flags instead of contextMemeberships
					//check if product is enabled for each site and populate all sites
					if(null!= pContext.getIndexingOutputConfig() 
							&& pContext.getIndexingOutputConfig() instanceof EndecaIndexingOutputConfig) {
						EndecaIndexingOutputConfig indexingOutputConfig = (EndecaIndexingOutputConfig) pContext.getIndexingOutputConfig();
						
						String[] siteIdsInOutputConfig = indexingOutputConfig.getSiteIDsToIndex();
						Set<String> ids = new HashSet<String>();
						if(null != siteIdsInOutputConfig) {
							for(String siteIdToCheck : siteIdsInOutputConfig) {
								if(checkProductActiveForCurrentSite(pItem, siteIdToCheck)){
				            		ids.add(siteIdToCheck);
								} else {
									if(isLoggingInfo()) {
										logInfo("Product is not active for this site - For item - "+pItem.getRepositoryId()+" siteIdToCheck - "+siteIdToCheck);
									}
								}
							}
							siteIds = (String[])ids.toArray(new String[ids.size()]);
							if(isLoggingInfo()) {
								for(String site : siteIds) {
									logInfo("Active item - "+pItem.getRepositoryId()+" siteID is - "+site);
								}
							}
						}
					}
						
				}
				else {
					//Default code
					siteIds = (String[])pContext.getGlobalAttribute("atg.siteVariantProducer.globalSiteValues");
					
					if (siteIds == null) {
						synchronized (this) {
							siteIds = (String[])pContext.getGlobalAttribute("atg.siteVariantProducer.globalSiteValues");
							
							if (siteIds == null) {
								RepositoryItem[] items = getSiteContextManager().getSiteManager().getAllSites();
								
								ArrayList<String> listIds = new ArrayList<String>(items.length);
								
								for (RepositoryItem itemCur : items) {
									listIds.add(itemCur.getRepositoryId());
								}
								siteIds = (String[])listIds.toArray(new String[0]);
								pContext.setGlobalAttribute("atg.siteVariantProducer.globalSiteValues", siteIds);
							}
						}
					}
				}
			}
			catch (RepositoryException re)
			{
				vlogError(re, "Error getting context memberships from {0.repositoryId}", new Object[] { pItem });
			}
			
			pContext.setAttribute("atg.siteVariantProducer.siteValues", siteIds);
			pContext.setAttribute("atg.siteVariantProducer.siteValuesIndex", Integer.valueOf(0));
			index = 0;
		}
		else
		{
			siteIds = (String[])pContext.getAttribute("atg.siteVariantProducer.siteValues");
			index = ((Integer)pContext.getAttribute("atg.siteVariantProducer.siteValuesIndex")).intValue() + 1;
		}
			
		if (siteIds == null) {
			return false;
		}
			
		while ((index < siteIds.length) && (!allowSiteId(pContext, siteIds[index])))
		{
			index++;
		}
			
		if (index < siteIds.length) {
			pUniqueParams.put(getContextUniqueParamName(), siteIds[index]);
			pContext.setAttribute("atg.siteVariantProdcer.siteValue", siteIds[index]);
			
			pContext.setAttribute("atg.siteVariantProducer.siteValuesIndex", Integer.valueOf(index));
			return true;
		}
		
		pUniqueParams.put(getContextUniqueParamName(), null);
		pContext.setAttribute("atg.siteVariantProducer.siteValues", null);
		pContext.setAttribute("atg.siteVariantProducer.siteValuesIndex", null);
		pContext.setAttribute("atg.siteVariantProdcer.siteValue", null);
		
		return false;
	}


	/**
	 * Verifies whether product is active for current site
	 * using site translated values of prodDisable and webOffered
	 * checks if current date is within start and end dates
	 * 
	 * @param pItem
	 * @param currentSiteVariant
	 * @return
	 */
	public boolean checkProductActiveForCurrentSite(RepositoryItem pItem, String currentSiteVariant) {
		Date currentDate = new Date();
		
		Boolean disableBoolean = (Boolean) pItem.getPropertyValue(new StringBuffer(
									BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME).append(PROPERTY_DEFAULT_SUFFIX)
									.toString());
		Boolean webOfferedBoolean = (Boolean) pItem.getPropertyValue(new StringBuffer(
										BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME).append(PROPERTY_DEFAULT_SUFFIX)
										.toString());
		Date startDate = (Date) pItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
		Date endDate = (Date) pItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
		
		Set<RepositoryItem> translations = (Set<RepositoryItem>) getRepositoryItemProperyValue(pItem, BBBCatalogConstants.PROD_TRANS_SITE_TRANS_PROPERTY_NAME);
				
		if (null != translations && !translations.isEmpty()) {
			
			String repositoryItemId = pItem.getRepositoryId();

			if (isLoggingDebug()) {
				logDebug(new StringBuilder("FIND TRANSLATION FOR ")
						.append("ITEM ID: ").append(repositoryItemId)
						.append("SITE[").append(currentSiteVariant).append("]")
						.toString());
			}
			

			for (RepositoryItem translation : translations) {

				if (isCurrentSiteVariantTranslation(currentSiteVariant,
								translation)
						/*&& isCurrentRepositoryItemTranslation(pPropertyName,
								translation*)*/) {

					if (isLoggingDebug()) {
						logDebug(new StringBuilder("TRANSLATION FOUND FOR ")
								.append("ITEM ID: ").append(repositoryItemId)
								.append("SITE[").append(currentSiteVariant).append("]").toString());
					}

					if(null != translation.getPropertyValue(ATTRIBUTE_PROPERTY)
							&& translation.getPropertyValue(ATTRIBUTE_PROPERTY).equals(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)) {
						disableBoolean = (Boolean) translation.getPropertyValue(BOOLEAN_PROPERTY);
					}
					
					if(null != translation.getPropertyValue(ATTRIBUTE_PROPERTY)
							&& translation.getPropertyValue(ATTRIBUTE_PROPERTY).equals(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME)) {
						webOfferedBoolean = (Boolean) translation.getPropertyValue(BOOLEAN_PROPERTY);
					}
					
				} 
			}
		} 
		
		//return as product disabled if required flags are null
		if(null == webOfferedBoolean || null == disableBoolean) {
			return false;
		}
		
		if ((((endDate != null) && currentDate.after(endDate)) || ((startDate != null) && currentDate.before(startDate)))
                || (disableBoolean) || (!webOfferedBoolean)) {
			return false;
		}
		
		return true;
	}
	
	private Object getRepositoryItemProperyValue(RepositoryItem pItem,
			String pPropertyName) {

		return ((RepositoryItemImpl) pItem).getPropertyValue(pPropertyName);
	}
	
	private boolean isCurrentSiteVariantTranslation(String currentSiteVariant,
			RepositoryItem translation) {

		boolean isCurrentSiteVariantTranslation = false;

		String translationSite = (String) ((RepositoryItem) translation
				.getPropertyValue(SITE_PROPERTY)).getRepositoryId();

		if (null != translationSite
				&& currentSiteVariant.equalsIgnoreCase(translationSite)) {
			isCurrentSiteVariantTranslation = true;
		}
		
		return isCurrentSiteVariantTranslation;
	}

	
}
