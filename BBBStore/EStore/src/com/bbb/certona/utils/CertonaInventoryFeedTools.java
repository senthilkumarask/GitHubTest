package com.bbb.certona.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.certona.vo.CertonaInventoryVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * Inventory feed helper class. Fetches data from inventory repository based 
 * on date.
 * 
 * @author njai13
 *
 */
public class CertonaInventoryFeedTools extends BBBGenericService {
	
	public Repository mInventRepo;
	
	/**
	 * Date from which records needs to be fetched
	 */
	private String mModDateQuery;
	
	
	/**
	 * Gets the inventory details based on the last modified date passed
	 * and populates the inventory objects list
	 * 
	 * @param pIsFullDataFeed
	 * @param pLastModifiedDate
	 * @return inventoryVOList
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public  List<RepositoryItem> getInventoryDetails(final boolean pIsFullDataFeed, 
														final Date pLastModifiedDate) throws BBBSystemException, BBBBusinessException{
		
			logDebug("Entering getInventoryDetails of CertonaInventoryFeedTools");
		
		
		RepositoryItem[] inventoryItems = null;
		if(pIsFullDataFeed || pLastModifiedDate == null){
			inventoryItems = getInventoryForFullFeed();
		}else{
			inventoryItems = getInventoryForIncrementalFeed(pLastModifiedDate);
		}
		if(inventoryItems!=null){
			logDebug("inventory items found ");
			return Arrays.asList(inventoryItems);
		}
		else{
			logDebug("no inventory items found");
			return null;
		}
		
	
		
		
	}
	/**
	 * Gets the inventory details based on the last modified date passed
	 * and populates the inventory objects list
	 * 
	 * @param pIsFullDataFeed
	 * @param pLastModifiedDate
	 * @return inventoryVOList
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public  List<RepositoryItem> getInventoryDetails(final boolean pIsFullDataFeed, 
														final Date pLastModifiedDate,final String pRqlQueryRange) throws BBBSystemException, BBBBusinessException{
		
		logDebug("Entering getInventoryDetails of CertonaInventoryFeedTools");
		RepositoryItem[] inventoryItems = null;	
		inventoryItems = getInventoryForIncrementalFeed(pRqlQueryRange);
		if(inventoryItems!=null){
			logDebug("inventory items found ");
			return Arrays.asList(inventoryItems);
		}
		else{
			logDebug("no inventory items found");
			return null;
		}
	}
	/**
	 * Fetches all the records from the inventory
	 * 
	 * @return inventoryItems -All repository items for inventory repository
	 * @throws BBBSystemException 
	 */
	private RepositoryItem[]  getInventoryForFullFeed() throws BBBSystemException{

			logDebug("Entering getInventoryForFullFeed of CertonaInventoryFeedTools");

		
		RepositoryItem[] inventoryItems = null;

		try{			
			final RepositoryView inventoryView = this.getInventoryRepository().getView(BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR);
			final QueryBuilder queryBuilder = inventoryView.getQueryBuilder();
			final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();
			inventoryItems = inventoryView.executeQuery(getAllItemsQuery);	
			
			if(inventoryItems == null){
				
					logInfo("No inventory items found even for full feed");
				
				throw new BBBSystemException(BBBCatalogErrorCodes.INVENTORY_ITEMS_UNAVAILABLE,BBBCatalogErrorCodes.INVENTORY_ITEMS_UNAVAILABLE);
			}
			
		}catch(RepositoryException re){
			
			logError(LogMessageFormatter.formatMessage(null, "CertonaInventoryFeedTools.getInventoryForFullFeed() | RepositoryException "), re);
			
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, re);
		}
		
	
			logDebug("Exiting getInventoryForFullFeed of CertonaInventoryFeedTools. " +
					"Total number of inventory items : "+ inventoryItems.length);
	
		return inventoryItems;
	}
	
	/**
	 * Fetches records from inventory repository whose modification date is 
	 * less than the date supplied 
	 * 	
	 * @param lastModifiedDate
	 * @return inventoryItems - Inventory items matching the criteria
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RepositoryItem[] getInventoryForIncrementalFeed(final Date pLastModifiedDate) throws BBBSystemException, BBBBusinessException{
		

			logDebug("Entering getInventoryForIncrementalFeed of CertonaInventoryFeedTools");
	
		
		RepositoryItem[] inventoryItems = null;		
        final java.sql.Timestamp time = new java.sql.Timestamp(pLastModifiedDate.getTime());
		
		try {
			
			 final RepositoryView inventoryView = this.getInventoryRepository().getView(BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR);
             final QueryBuilder queryBuilder=inventoryView.getQueryBuilder();
             final QueryExpression pProperty = queryBuilder.createPropertyQueryExpression(BBBCertonaConstants.LAST_MODIFIED_DATE);
             final QueryExpression pValue = queryBuilder.createConstantQueryExpression(time);
             final Query query = queryBuilder.createComparisonQuery(pProperty, pValue,QueryBuilder.GREATER_THAN_OR_EQUALS);
             inventoryItems = inventoryView.executeQuery(query);           
						
		} catch (RepositoryException re) {
			
			logError(LogMessageFormatter.formatMessage(null, "CertonaInventoryFeedTools.getInventoryForFullFeed() | RepositoryException "), re);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, re);
		} 
		
		
			logDebug("Exiting getInventoryForFullFeed of CertonaInventoryFeedTools. " +
					"Total number of inventory items : "+ (inventoryItems == null ? 0 : inventoryItems.length));
	
		
		return inventoryItems;
	}
	/**
	 * Fetches records from inventory repository whose modification date is 
	 * less than the date supplied 
	 * 	
	 * @param lastModifiedDate
	 * @return inventoryItems - Inventory items matching the criteria
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RepositoryItem[] getInventoryForIncrementalFeed(final String pRqlQueryRange) throws BBBSystemException, BBBBusinessException{
		

			logDebug("Entering getInventoryForIncrementalFeed of CertonaInventoryFeedTools");
	
		
		RepositoryItem[] inventoryItems = null;		
		
		try {
			
			 inventoryItems=this.executeRQLQuery(pRqlQueryRange, new Object[1], BBBCatalogConstants.CACHED_INVENTORY_ITEM_DESCRIPTOR, getInventoryRepository());         
						
		} catch (RepositoryException re) {
			
			logError(LogMessageFormatter.formatMessage(null, "CertonaInventoryFeedTools.getInventoryForFullFeed() | RepositoryException "), re);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, re);
		} 
		
		
			logDebug("Exiting getInventoryForFullFeed of CertonaInventoryFeedTools. " +
					"Total number of inventory items : "+ (inventoryItems == null ? 0 : inventoryItems.length));
		
		
		return inventoryItems;
	}
	
	/**
	 * Populates the Inventory Object
	 * 
	 * @param inventoryItems
	 * @return inventoryVO
	 */
	public  CertonaInventoryVO populateInventoryVO(final RepositoryItem inventoryItems){
		

			logDebug("Entering populateInventoryVO of CertonaInventoryFeedTools");
		
		
		CertonaInventoryVO inventoryVO=null;
		if(inventoryItems!=null){
			
			inventoryVO=new CertonaInventoryVO();
			final Object inventTrans = inventoryItems.getPropertyValue(BBBCertonaConstants.TRANSLATIONS_PROPERTY);
						
			@SuppressWarnings("unchecked")
			final Map <String,RepositoryItem> inventTransMap = (Map<String,RepositoryItem>) inventTrans;
			String skuId = (String)inventoryItems.getPropertyValue(BBBCertonaConstants.SKU_ID);
			logDebug("get inventory for sku Id "+skuId);
			inventoryVO.setSkuId(skuId);
			
			double globalStockLevel = getPropertyValue(inventoryItems, BBBCatalogConstants.STOCK_LEVEL_INVENTORY_PROPERTY_NAME);
			double siteStockLevel = getPropertyValue(inventoryItems, BBBCertonaConstants.SITE_STOCK_LEVEL_PROPERTY);
			double registryStockLevel = getPropertyValue(inventoryItems, BBBCertonaConstants.REG_STOCK_LEVEL_PROPERTY);
			
			inventoryVO.setStockBBBUS(globalStockLevel + siteStockLevel);
			inventoryVO.setRegistryStockBBBUS(globalStockLevel + siteStockLevel + registryStockLevel);
			
			
			if(inventTransMap!=null && !inventTransMap.isEmpty()){
				
				
				if(inventTransMap.get(BBBCoreConstants.SITE_BBB) != null){
					final RepositoryItem translationItem=inventTransMap.get(BBBCoreConstants.SITE_BBB);
					//skuId = (String)translationItem.getPropertyValue(BBBCertonaConstants.SKU_ID);
					siteStockLevel = getPropertyValue(translationItem, BBBCertonaConstants.SITE_STOCK_LEVEL_PROPERTY);
					registryStockLevel = getPropertyValue(translationItem, BBBCertonaConstants.REG_STOCK_LEVEL_PROPERTY);
					inventoryVO.setStockBuyBuyBaby(globalStockLevel + siteStockLevel);
					inventoryVO.setRegistryStockBuyBuyBaby(globalStockLevel + siteStockLevel + registryStockLevel);
				}
				
				if(inventTransMap.get(BBBCoreConstants.SITE_BAB_CA) != null){
					final RepositoryItem translationItem=inventTransMap.get(BBBCoreConstants.SITE_BAB_CA);
					//skuId = (String)translationItem.getPropertyValue(BBBCertonaConstants.SKU_ID);
					siteStockLevel = getPropertyValue(translationItem, BBBCertonaConstants.SITE_STOCK_LEVEL_PROPERTY);
					registryStockLevel = getPropertyValue(translationItem, BBBCertonaConstants.REG_STOCK_LEVEL_PROPERTY);
					inventoryVO.setStockBBBCA(globalStockLevel + siteStockLevel);
					inventoryVO.setRegistryStockBBBCA(globalStockLevel + siteStockLevel + registryStockLevel);
				}
				
				
			}
			
		}
		
			logDebug("Exiting populateInventoryVO of CertonaInventoryFeedTools");
	
		return inventoryVO;
	}
	
	private double getPropertyValue(final RepositoryItem translationItem, final String property) {
		Object propertyValue = translationItem.getPropertyValue(property);
		if(propertyValue == null){
			propertyValue = Long.valueOf(0);
		}	
		
		final double propertyDouble = Double.valueOf(((Long)propertyValue).longValue());
		
		return propertyDouble;		
	}

	/**
	 * @return the inventoryRepository
	 */
	public Repository getInventoryRepository() {
		return mInventRepo;
	}

	/**
	 * @param inventoryRepository the inventoryRepository to set
	 */
	public void setInventoryRepository(final Repository pInventRepo) {
		this.mInventRepo = pInventRepo;
	}


	/**
	 * @return the modifiedDateQuery
	 */
	public String getModifiedDateQuery() {
		return mModDateQuery;
	}

	/**
	 * @param modifiedDateQuery the modifiedDateQuery to set
	 */
	public void setModifiedDateQuery(final String pModDateQuery) {
		this.mModDateQuery = pModDateQuery;
	}
	private RepositoryItem[] executeRQLQuery(String rqlQuery, Object[] params, String viewName, Repository repository) throws RepositoryException, BBBSystemException, BBBBusinessException {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		if (rqlQuery != null) {
			if (repository != null) {
				try {
					long startTime = System.currentTimeMillis();
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					RepositoryView view = repository.getView(viewName);
					if (view == null ) {
						logInfo("View " + viewName + " is null");
					}

					queryResult = statement.executeQuery(view, params);
					if ( queryResult == null) {

						logInfo("No results returned for query [" + rqlQuery + "]");

					}
					
					logInfo("Total Time taken by OnlineInventoryManager.executeRQLQuery() is:" + (System.currentTimeMillis() - startTime) + " for rqlQuery:" + rqlQuery);
					
				} catch (RepositoryException e) {

					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				}
			} else {
				
					logInfo("Repository has no data");
				
			}
		} else {
			
			logInfo("Query String is null");
			
		}

		return queryResult;
	}
}
