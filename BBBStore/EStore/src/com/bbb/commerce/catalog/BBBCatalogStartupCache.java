package com.bbb.commerce.catalog;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class BBBCatalogStartupCache extends BBBGenericService {
	
	private static final String CATALOG_CACHE_QUERIES ="CatalogCacheQueries";
	private static final String RANGE =" RANGE ";
	
	private BBBCatalogTools mCatalogTools;
	private String mSkuThresholdsRql;
	private int mPoolSize;
	private int mRangeCount;
	private ExecutorService threadPool;
	/**
	 * @return the mRangeCount
	 */
	public int getRangeCount() {
		return mRangeCount;
	}

	/**
	 * @param mRangeCount the mRangeCount to set
	 */
	public void setRangeCount(int pRangeCount) {
		this.mRangeCount = pRangeCount;
	}

	/**
	 * @return the mPoolSize
	 */
	public int getPoolSize() {
		return mPoolSize;
	}

	/**
	 * @param mPoolSize the mPoolSize to set
	 */
	public void setPoolSize(int pPoolSize) {
		this.mPoolSize = pPoolSize;
	}

	/**
	 * @return the mSkuThresholdsRql
	 */
	public String getSkuThresholdsRql() {
		return mSkuThresholdsRql;
	}

	/**
	 * @param mSkuThresholdsRql the mSkuThresholdsRql to set
	 */
	public void setSkuThresholdsRql(String pSkuThresholdsRql) {
		this.mSkuThresholdsRql = pSkuThresholdsRql;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		threadPool = Executors.newFixedThreadPool(mPoolSize);
		startCatalogCaching();
		//cacheSkuThresholds();
	}
	
	private void startCatalogCaching(){
		logDebug("Entering BBBCatalogStartupCache.startCatalogCaching()");
		try{
			Map<String,String> cachingQueries = getCatalogTools().getConfigValueByconfigType(CATALOG_CACHE_QUERIES);
			logDebug("cachingQueries is:" + cachingQueries);
			if(!BBBUtility.isMapNullOrEmpty(cachingQueries)){
				for(final String viewName: cachingQueries.keySet()){
					final String rqlQuery = cachingQueries.get(viewName);
					logDebug("view name is:"+ viewName);
					Runnable newQueryThread = new Runnable(){
						public void run(){
							int totalCount = 0;
							String rqlQueryRange=rqlQuery + RANGE+ totalCount + "+" + getRangeCount();
							totalCount=exeuteQuery(rqlQueryRange);
							int count=totalCount;
							while(count==getRangeCount()){
								rqlQueryRange=rqlQuery + RANGE + totalCount + "+" + getRangeCount();
								count=exeuteQuery(rqlQueryRange);
								totalCount = totalCount +count;
							}
						}
						
						private int exeuteQuery(String query){
							int itemCount=0;
							try{
								logDebug("RQL to execute is:"+ query);
								logDebug("Free Memory before query is:"+getMemoryUsed());
								RepositoryItem[] items = getCatalogTools().executeRQLQuery(query.toString(), viewName);
								logDebug("Free Memory after query is:"+getMemoryUsed());
								if(!BBBUtility.isArrayEmpty(items)){
									logDebug("Num of items of type "
												+ viewName + " cached are "
												+ items.length);
									itemCount = items.length;
								}else{
									logDebug("No records fetch for items type "
												+ viewName);
								}
								logDebug("RQL executed successfully:"+ query);
							}catch (RepositoryException re) {
								logError("RepositoryException occured for query:"+ query,re);
							}catch (BBBSystemException bse) {
								logError("BBBSystemException occured for query:" + query,bse);
							}catch (BBBBusinessException bse) {
								logError("BBBBusinessException occured for query:" +query ,bse);
							}
							return itemCount;
						}
					};
					threadPool.execute(newQueryThread);
				}
			}
		} catch(BBBBusinessException bbe){
			logError("BBBBusinessException occured:",bbe);
		} catch (BBBSystemException bse) {
			logError("BBBSystemException occured:",bse);
		}finally{
			logDebug("Exiting BBBCatalogStartupCache.startCatalogCaching()");
			threadPool.shutdown();
		}
	}
	/*
	private void startCatalogCaching(){
		logDebug("Entering BBBCatalogStartupCache.startCatalogCaching()");
		try{
			Map<String,String> cachingQueries = getCatalogTools().getConfigValueByconfigType(CATALOG_CACHE_QUERIES);
			logDebug("cachingQueries is:" + cachingQueries);
			if(cachingQueries!=null & !cachingQueries.isEmpty()){
				for(final String viewName: cachingQueries.keySet()){
					final String rqlQuery=cachingQueries.get(viewName);
					logDebug("view name is:"+ viewName);
					logDebug("RQL to execute is:"+ rqlQuery);
					try{
						logDebug("Free Memory before query is:"+getMemoryUsed());
						RepositoryItem[] items = getCatalogTools().executeRQLQuery(rqlQuery, viewName);
						logDebug("Free Memory after query is:"+getMemoryUsed());
						if(items!=null && items.length>0){
							logInfo("Num of items of type "+ viewName +" cached are " + items.length);
						}else{
							logInfo("No records fetch for items type "+ viewName);
						}
						logDebug("RQL executed successfully:");
					}catch (RepositoryException re) {
						logError("RepositoryException occured:",re);
					}catch (BBBSystemException bse) {
						logError("BBBSystemException occured:",bse);
					}catch (BBBBusinessException bse) {
						logError("BBBBusinessException occured:",bse);
					}
				}
			}
		} catch(BBBBusinessException bbe){
			logError("BBBBusinessException occured:",bbe);
		} catch (BBBSystemException bse) {
			logError("BBBSystemException occured:",bse);
		}finally{
			logDebug("Exiting BBBCatalogStartupCache.startCatalogCaching()");
		}
	}*/
	
	/*private void cacheSkuThresholds(){
		logDebug("Entering BBBCatalogStartupCache.cacheSkuThresholds()");
		try{
			Map<String,String> cachingQueries = getCatalogTools().getConfigValueByconfigType(CATALOG_CACHE_QUERIES);
			
			if(cachingQueries!=null & !cachingQueries.isEmpty()){
				String rqlQuery=cachingQueries.get(BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				logDebug("sku RQL to execute is:"+ rqlQuery);
				try{
					if(rqlQuery!=null){
						RepositoryItem[] skus = getCatalogTools().executeRQLQuery(rqlQuery, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
						logDebug("Free Memory before query is:"+getMemoryUsed());
						if(skus!=null && skus.length>0){
							int countSkuThresholds=0;
							for(RepositoryItem sku:skus){
								RqlStatement statement = RqlStatement.parseRqlStatement(getSkuThresholdsRql());
								RepositoryView view = ((BBBCatalogToolsImpl)getCatalogTools()).getCatalogRepository().getView(BBBCatalogConstants.SKU_THRESHOLD_ITEM_DESCRIPTOR);
								Object[] params = new Object[1];
								params[0]=sku.getRepositoryId();
								logDebug("skuThreholds RQL to execute is:"+ getSkuThresholdsRql());
								logDebug("skuId is:" + sku.getRepositoryId());
								RepositoryItem[] skuThresholds = statement.executeQuery(view, params);
								if(skuThresholds!=null){
									countSkuThresholds = countSkuThresholds + skuThresholds.length;
								}
								logDebug("skuThreholds RQL executed successfully:");
							}
							logDebug("Num of items of type "
										+ BBBCatalogConstants.SKU_THRESHOLD_ITEM_DESCRIPTOR
										+ " cached are " + countSkuThresholds);
						}
						logDebug("Free Memory after query is:"+getMemoryUsed());
						
					}
				}catch(RepositoryException re){
					logError("RepositoryException occured:",re);
				}catch (BBBSystemException bse) {
					logError("BBBBusinessException occured:",bse);
				}
			}
		} catch(BBBBusinessException bbe){
			logError("BBBBusinessException occured:",bbe);
		} catch (BBBSystemException bse) {
			logError("BBBSystemException occured:",bse);
		}finally{
			logDebug("Exiting BBBCatalogStartupCache.cacheSkuThresholds()");
		}
	}*/
	
	 private static long getMemoryUsed(){
		    long totalMemory = Runtime.getRuntime().totalMemory();
		    long freeMemory = Runtime.getRuntime().freeMemory();
		    return (totalMemory - freeMemory);
	 }
}
