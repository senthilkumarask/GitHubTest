package com.bbb.cache.scheduler;

import static com.bbb.constants.BBBCoreConstants.COLLECTION_CHILD_RELN_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.Repository;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.repositorywrapper.CustomRepositoryException;
import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;
import com.bbb.utils.BBBConfigRepoUtils;

public class BBBCollectionChildRelnCacheBuilder extends BBBGenericService {

	private static final String RQL_ALL = "ALL";
	private static final String RQL_ORDER_BY_PRODUCT_ID = " ORDER BY id SORT ASC";
	private static final String RQl_COLLECTION_PARAM = "collection=true OR leadPrd=true";
	private static final String PROD_DISABLE_DEFAULT = "prodDisableDefault";
	private static final String WEB_OFFERED_DEFAULT = "webOfferedDefault";
	private static final String UNDER_SCORE = "_";
	private static final ApplicationLogging MLOGGING = ClassLoggingFactory.getFactory().getLoggerForClass(BBBCollectionChildRelnCacheBuilder.class);
	
	private BBBObjectCache objectCache;
	private Repository catalogRepository;
	private BBBCatalogTools catalogTools;
	private Map<String, String> everLivingFlagsMap;
	private List<String> siteIds;
	private boolean runningStatus = false;
	

	
	public void startCacheRefresh() throws BBBBusinessException, BBBSystemException {
		MLOGGING.logDebug("startCacheRefresh start ");
		if (isRunningStatus()) {
			MLOGGING.logInfo("BBBCollectionChildRelnCacheBuilder [invokeCacheBuilder] already running");
		} else {
			this.runningStatus = true;
			try {
				refreshChildPrdToCollectionRelationCache();
			} finally {
				this.runningStatus = false;
			}
		} 
		MLOGGING.logDebug("startCacheRefresh end ");
	}

	/**
	 * This method creates creates and adds the site wise active child product to parent collection product relationship to the object cache.
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 * 
	 */
	private void refreshChildPrdToCollectionRelationCache() throws BBBBusinessException, BBBSystemException {
		
		MLOGGING.logInfo("BBBCollectionChildRelnCacheBuilder [buildChildPrdToCollectionRelationCache] Start");
		long buildCacheStartTime = System.currentTimeMillis();
		String collectionChildRelnCacheName = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, COLLECTION_CHILD_RELN_CACHE_NAME);
		getObjectCache().clearCache(collectionChildRelnCacheName);
		MLOGGING.logInfo("BBBCollectionChildRelnCacheBuilder [buildChildPrdToCollectionRelationCache] cleared cache : " + collectionChildRelnCacheName);
		
		String rql = RQl_COLLECTION_PARAM + RQL_ORDER_BY_PRODUCT_ID;
		RepositoryItem[] collectionProductList = getCatalogItems(BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR, rql);
		
		if (collectionProductList == null || collectionProductList.length == 0) {
			MLOGGING.logInfo("No data available in repository for building child product to collection relation cache");
			throw new BBBBusinessException("No data available in repository for building child product to collection relation cache");
		}
		
		
		for (RepositoryItem collectionProductItem : collectionProductList) {
			for (String siteId : this.getSiteIds()) {
				if (Boolean.TRUE.equals(collectionProductItem.getPropertyValue(everLivingFlagsMap.get(siteId))) || isProductWebOffered(collectionProductItem, siteId) ) {
					final List<RepositoryItem> childProductsRelationList = (List<RepositoryItem>) collectionProductItem
	                        .getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
					 for (RepositoryItem childProdItem : childProductsRelationList) {
						 childProdItem = (RepositoryItem) childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
						 if (Boolean.FALSE.equals(childProdItem.getPropertyValue(everLivingFlagsMap.get(siteId))) || isProductWebOffered(collectionProductItem, siteId)) {
							 String key = new StringBuffer(childProdItem.getRepositoryId()).append(UNDER_SCORE).append(siteId).toString();
							 getObjectCache().put(key, collectionProductItem.getRepositoryId(), collectionChildRelnCacheName);
						}
					 } 
				}
			}
		} 
		long buildCacheEndTime = System.currentTimeMillis();
		MLOGGING.logInfo("BBBCollectionChildRelnCacheBuilder [buildChildPrdToCollectionRelationCache] End. Total time taken (ms) = "+ (buildCacheEndTime-buildCacheStartTime));
	}

	/**
	 * 
	 * @param catalogItemDes
	 * @param rql
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getCatalogItems (final String catalogItemDes, String rql) throws BBBSystemException {

		MLOGGING.logInfo("BBBCollectionChildRelnCacheBuilder [getCatalogItems] start");
		RepositoryItem[] catalogItems = null;
		final IRepositoryWrapper repWrapper = new RepositoryWrapperImpl(this.getCatalogRepository());
		rql = rql==null?RQL_ALL:rql;
		try {
			catalogItems = repWrapper.queryRepositoryItems(catalogItemDes, rql, null, true);
			MLOGGING.logInfo(catalogItemDes + " item(s) count : " + catalogItems.length);
		}
		catch (final CustomRepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		MLOGGING.logInfo("BBBCollectionChildRelnCacheBuilder [getCatalogItems] end");
		return catalogItems;
	}
	
	/**
	 * 
	 * @param productRepoItem
	 * @param siteId
	 * @return
	 */
	public boolean isProductWebOffered(final RepositoryItem productRepoItem, final String siteId) {

		final Object prdTranslations = productRepoItem.getPropertyValue(BBBCatalogConstants.PROD_TRANS_SITE_TRANS_PROPERTY_NAME);
		Date previewDate = new Date();
		Object obj = productRepoItem.getPropertyValue(WEB_OFFERED_DEFAULT);
		boolean webOffered= obj == null ? false: (Boolean) obj;
		obj = productRepoItem.getPropertyValue(PROD_DISABLE_DEFAULT);
		boolean disable=obj == null ? false: (Boolean) obj;
		if(prdTranslations != null) {
			final Set<RepositoryItem> translations = (Set<RepositoryItem>) prdTranslations;
			for (final RepositoryItem transRepo : translations) {
				final RepositoryItem siteItem = (RepositoryItem) transRepo
						.getPropertyValue(BBBCatalogConstants.SITE_PRODUCT_PROPERTY_NAME);
				if ((siteItem == null)
						|| !siteItem.getRepositoryId().equals(siteId)) {
					continue;
				}
				final Object attrName = transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME);
				final Object translationBooleanValue= transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);

				if((attrName != null) && (translationBooleanValue != null) &&((String)attrName).equalsIgnoreCase(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)) {
					webOffered =(Boolean)translationBooleanValue;
					if (!webOffered) {
						return false;
					}
				}
				if((attrName != null) && (translationBooleanValue != null) &&((String) attrName).equalsIgnoreCase(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)){
					disable = (Boolean)translationBooleanValue;
					if (disable) {
						return false;
					}
				}
			}
		}
		if( this.getCatalogTools().isPreviewEnabled()){
			previewDate =  this.getCatalogTools().getPreviewDate();
		}
		final Date startDate = (Date) productRepoItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
		final Date endDate = (Date) productRepoItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
		if((((endDate!=null) && previewDate.after(endDate))|| ((startDate!=null)&& previewDate.before(startDate))) ||(disable) ||(!webOffered)){
			return false;
		}
		return true;
	}

	/**
	 * @return the objectCache
	 */
	public BBBObjectCache getObjectCache() {
		return objectCache;
	}
	/**
	 * @param objectCache the objectCache to set
	 */
	public void setObjectCache(BBBObjectCache objectCache) {
		this.objectCache = objectCache;
	}


	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	/**
	 * @return the everLivingFlagsMap
	 */
	public Map<String, String> getEverLivingFlagsMap() {
		return everLivingFlagsMap;
	}

	/**
	 * @param everLivingFlagsMap the everLivingFlagsMap to set
	 */
	public void setEverLivingFlagsMap(Map<String, String> everLivingFlagsMap) {
		this.everLivingFlagsMap = everLivingFlagsMap;
	}

	/**
	 * @return the catalogRepository
	 */
	public Repository getCatalogRepository() {
		return catalogRepository;
	}


	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(Repository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}


	/**
	 * @return the siteIds
	 */
	public List<String> getSiteIds() {
		return siteIds;
	}

	/**
	 * @param siteIds the siteIds to set
	 */
	public void setSiteIds(List<String> siteIds) {
		this.siteIds = siteIds;
	}

	/**
	 * @return the runningStatus
	 */
	public boolean isRunningStatus() {
		return runningStatus;
	}
}
