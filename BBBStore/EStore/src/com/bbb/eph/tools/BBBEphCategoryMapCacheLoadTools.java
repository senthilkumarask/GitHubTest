package com.bbb.eph.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import atg.adapter.gsa.GSARepository;
import atg.repository.Repository;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.eph.vo.BBBEphCategoryMapVo;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

public class BBBEphCategoryMapCacheLoadTools extends BBBGenericService{
	
	private final String EPH_CATEGORY_MAP_CACHE_LOAD_POOL_SIZE = "KeywordEPHCategoryMapPoolSize";
	private final String EPH_CATEGORY_MAP_CACHE_LOAD_CACHE_NAME = "KEYWORD_EPH_CAT_MAP_CACHE_NAME";
	private final String CONFIG_TYPE_OBJECT_CACHE_KEYS = "ObjectCacheKeys";
	private final String CLS_NAME = "BBBEphCategoryMapCacheLoadTools";
	private final String PARAM_SEARCH_KEYWORD = "SEARCH_KEYWORD";
	private final String PARAM_EPH_IDS = "EPH_IDS";
	private final String PARAM_CATEGORY_IDS = "CATEGORY_IDS";
	private final String PARAM_CONCEPT = "CONCEPT";
		
	private final String MAX_SEQ_NUM = "max_seq_num";
	private int poolSize;
	private BBBConfigTools configTools;
	private ExecutorService threadPool;
	private Repository ephCatRepository;
	private String queryAllMaxSearchKeySeq;
	private String queryAllSearchKeyData;
	private CoherenceCacheContainer coherenceCacheContainer;
	private String cacheName;
	private String queryLastDayMaxSearchKeySeq;
	private String queryLastDaySearchKeyData;
	private boolean fullCacheLoadRequired;
		
	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return cacheName;
	}

	/**
	 * @param cacheName the cacheName to set
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @return the coherenceCacheContainer
	 */
	public CoherenceCacheContainer getCoherenceCacheContainer() {
		return coherenceCacheContainer;
	}

	/**
	 * @param coherenceCacheContainer the coherenceCacheContainer to set
	 */
	public void setCoherenceCacheContainer(
			CoherenceCacheContainer coherenceCacheContainer) {
		this.coherenceCacheContainer = coherenceCacheContainer;
	}

	/**
	 * @return the ephCatRepository
	 */
	public Repository getEphCatRepository() {
		return ephCatRepository;
	}

	/**
	 * @param ephCatRepository the ephCatRepository to set
	 */
	public void setEphCatRepository(Repository ephCatRepository) {
		this.ephCatRepository = ephCatRepository;
	}

	/**
	 * @return the threadPool
	 */
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	/**
	 * @param threadPool the threadPool to set
	 */
	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

	/**
	 * @return the poolSize
	 */
	public int getPoolSize() {
		return poolSize;
	}

	/**
	 * @param poolSize the poolSize to set
	 */
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	

	/**
	 * @return the configTools
	 */
	public BBBConfigTools getConfigTools() {
		return configTools;
	}

	/**
	 * @param configTools the configTools to set
	 */
	public void setConfigTools(BBBConfigTools configTools) {
		this.configTools = configTools;
	}
	
	private static long getMemoryUsed() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		return (totalMemory - freeMemory);
	}
	
	/*
	 * This method is used to populate all configuration values defined in BCC
	 * and set them in component properties.
	 */
	public void populateConfigurations() {
		logDebug(CLS_NAME + " | populateConfigurations() START..");
			
		// code to fetch pool size, cache name and cache timeout from BCC
		this.setPoolSize(getConfigTools().getValueForConfigKey(CONFIG_TYPE_OBJECT_CACHE_KEYS,EPH_CATEGORY_MAP_CACHE_LOAD_POOL_SIZE,this.getPoolSize()));
		this.setCacheName(getConfigTools().getConfigKeyValue(CONFIG_TYPE_OBJECT_CACHE_KEYS,EPH_CATEGORY_MAP_CACHE_LOAD_CACHE_NAME,this.getCacheName()));
					
		logDebug(CLS_NAME + " | populateConfigurations | POOL_SIZE from BCC : "+ this.getPoolSize()
						+" | cacheName from BCC : "+ this.getCacheName());
		logDebug(CLS_NAME + " | populateConfigurations() END");		
	}
	
	/*
	 * This class is created to execute populate coherence using thread.
	 * run() method of this class contains actual logic for coherence update. 
	 * 
	 */
	class KeywordCacheLoaderRunnableThread implements Runnable {
		private final String EPH_CATEGORY_MAP_CACHE_BATCH_SIZE = "KeywordEPHCategoryMapBatchSize";
		private final int TEN_THOUSAND = 10000;
		private int mStartIndex;
		private int mEndIndex;
		private CountDownLatch mLatchCount;
		
		// getters and setters for mStartIndex and mEndIndex
		public int getStartIndex() {
			return mStartIndex;
		}

		public void setStartIndex(int mStartIndex) {
			this.mStartIndex = mStartIndex;
		}

		public int getEndIndex() {
			return mEndIndex;
		}

		public void setEndIndex(int mEndIndex) {
			this.mEndIndex = mEndIndex;
		}
		
		public CountDownLatch getmLatchCount() {
			return mLatchCount;
		}

		public void setmLatchCount(CountDownLatch mLatchCount) {
			this.mLatchCount = mLatchCount;
		}
		
		public KeywordCacheLoaderRunnableThread(int startIndex, int endIndex, CountDownLatch latchCount) {
			mStartIndex = startIndex;
			mEndIndex = endIndex;
			this.mLatchCount=latchCount;
			logDebug("Initializeing thread :"
					+ Thread.currentThread().getName()
					+ " KeywordCacheLoaderRunnableThread - StartIndex: "
					+ startIndex + " EndIndex: " + endIndex);
		}

		/**
		 * run() is called by each thread to load the coherence cache in
		 * batches.
		 */

		public void run() {
			BBBPerformanceMonitor.start(CLS_NAME, "run");
			logDebug(CLS_NAME+"run method START..");
			
			int batchSize = getConfigTools().getValueForConfigKey(CONFIG_TYPE_OBJECT_CACHE_KEYS,EPH_CATEGORY_MAP_CACHE_BATCH_SIZE,TEN_THOUSAND);
			logDebug("batch size for entering mappings in coherence is : "+batchSize);
			Connection conn = null;
			PreparedStatement preparedStmt = null;
			ResultSet rs = null;
			try {

				long startTime = System.currentTimeMillis();
				logDebug(Thread.currentThread().getName()
						+ "Free Memory before processing KeywordCacheLoaderRunnableThread:"
						+ getMemoryUsed() + " Start time :" + startTime);

				if (conn == null) {
					conn = ((GSARepository) getEphCatRepository()).getDataSource().getConnection();
				}
								
				if(BBBEphCategoryMapCacheLoadTools.this.isFullCacheLoadRequired()){
					preparedStmt = conn
							.prepareStatement(getQueryAllSearchKeyData());
					logDebug("SQL for searchKey Thread"
							+ Thread.currentThread().getName() + " is "
							+ getQueryAllSearchKeyData());
				} else {
					preparedStmt = conn
							.prepareStatement(getQueryLastDaySearchKeyData());
					logDebug("SQL for searchKey Thread"
							+ Thread.currentThread().getName() + " is "
							+ getQueryLastDaySearchKeyData());
				}
				
				preparedStmt.setLong(1, getStartIndex());
				preparedStmt.setLong(2, getEndIndex());
				
				rs = preparedStmt.executeQuery();
				Map<String, Object> ephCategoryMap = new HashMap<>();
				if (null != rs) {
					int dataSize = getEndIndex() - getStartIndex() + 1;
					for(int counter=1; counter<=dataSize; counter++){
						if(rs.next()){
							if (BBBUtility.isNotEmpty(rs.getString(PARAM_SEARCH_KEYWORD))) {
								ephCategoryMap = BBBEphCategoryMapCacheLoadTools.this
										.populateSearchkeywordMap(rs, ephCategoryMap);
							}						
							
							if(counter % batchSize == 0){
								logDebug("updating partial coherence for a batch");
								BBBEphCategoryMapCacheLoadTools.this.getCoherenceCacheContainer().putAll(ephCategoryMap, BBBEphCategoryMapCacheLoadTools.this.getCacheName());
								ephCategoryMap.clear();
							}
						}						
					}		
					logDebug("updating coherence for final batch");
					BBBEphCategoryMapCacheLoadTools.this.getCoherenceCacheContainer().putAll(ephCategoryMap, BBBEphCategoryMapCacheLoadTools.this.getCacheName());					
				}

				long endTime = System.currentTimeMillis();
				logDebug("Ending thread="
						+ Thread.currentThread().getName()
						+ " Free Memory after processing KeywordCacheLoaderRunnableThread:"
						+ getMemoryUsed() + " Total time by this thread:"
						+ (endTime - startTime));

			} catch (Exception e) {
				logError(CLS_NAME+" | Exception", e);
				BBBPerformanceMonitor.cancel(
						CLS_NAME, "run");
			} finally {
				mLatchCount.countDown();
				try {
					//close resultSet
					if (rs != null) {
						rs.close();
					}
					//close PreparedStatement
					if (preparedStmt != null) {
						preparedStmt.close();
					}
					//close connection
					if (conn != null) {
						conn.close();
					}
					BBBPerformanceMonitor.end(
							CLS_NAME, "run");
					logDebug(CLS_NAME+"run method END.");
				} catch (SQLException sqlExp) {
					logError(
							CLS_NAME + " | SQLException from run()",
							sqlExp);
				}
			}
		}
	}	
	
	/*
	 * This method populates ephCategoryMap and returns same.
	 * @param ResultSet
	 * 				: resulrSet as input parameter
	 * @param Map<String, Object>
	 * 				map for storing searchKey-eph/category mappings 				
	 * @throws SQLException
	 */
	
	public Map<String, Object> populateSearchkeywordMap(ResultSet rs, Map<String, Object> ephCategoryMap) throws SQLException{
		logDebug(CLS_NAME + " | populateSearchkeywordCache START..");
		BBBPerformanceMonitor.start(CLS_NAME, "populateSearchkeywordCache");
		
		BBBEphCategoryMapVo ephCategoryMapVo = new BBBEphCategoryMapVo();
		if(null != rs.getString(PARAM_SEARCH_KEYWORD)){
			ephCategoryMapVo.setKeyword((rs.getString(PARAM_SEARCH_KEYWORD).toLowerCase() + BBBCoreConstants.UNDERSCORE + rs.getString(PARAM_CONCEPT)));
			ephCategoryMapVo.setEPHList(rs.getString(PARAM_EPH_IDS));
			ephCategoryMapVo.setCategoryList(rs.getString(PARAM_CATEGORY_IDS));
			
			ephCategoryMap.put(ephCategoryMapVo.getKeyword(), ephCategoryMapVo);
		}		
				
		logDebug(CLS_NAME + " | populateSearchkeywordCache END.");
		BBBPerformanceMonitor.end(CLS_NAME, "populateSearchkeywordCache");
		
		return ephCategoryMap;
	}
	
	/*
	 * This method checks for pool size defined in 
	 * BCC to create thread and items per thread.
	 * Then it calls thread to populate coherence cache.
	 * 
	 */
	
	public void loadEPHCategoryMapCache(){
		logDebug(CLS_NAME + " | Entering loadEPHCategoryMapCache()");
		BBBPerformanceMonitor.start(CLS_NAME, "loadEPHCategoryMapCache");
		
		Iterator itr = this.getCoherenceCacheContainer().getAllCacheKeys(this.getCacheName());
		if(null == itr || !itr.hasNext()){
			this.setFullCacheLoadRequired(true);
			this.logDebug("Coherence cache is empty for region keyword-eph-cat-near-cache, so executing full cache load.");
		} else {
			this.setFullCacheLoadRequired(false);
			this.logDebug("Items are already there in coherence region keyword-eph-cat-near-cache, so executing cache load for last-day-modified items only.");
		}
		
		// populate all configuration values defined in BCC
		this.populateConfigurations();
				
		Connection conn = null;
		PreparedStatement preparedtmt = null;
		ResultSet resultSet = null;
		
		try {

			if (conn == null) {
				// create connection with SearchKeyEphCatMappingRepository repository
				conn = ((GSARepository) getEphCatRepository()).getDataSource().getConnection();
			}
			
			if(this.isFullCacheLoadRequired()){
				preparedtmt = conn.prepareStatement(this.getQueryAllMaxSearchKeySeq());
			} else {
				preparedtmt = conn.prepareStatement(this.getQueryLastDayMaxSearchKeySeq());
			}
						
			resultSet = preparedtmt.executeQuery();
			if (null != resultSet) {
				resultSet.next();
				int maxRecordCount = resultSet.getInt(MAX_SEQ_NUM);

				if (maxRecordCount == 0) {
					logDebug(CLS_NAME + " | Max search key records is Zero");
					return;
				} else if(maxRecordCount < this.getPoolSize()){
					this.setPoolSize(maxRecordCount);
				}
				threadPool = Executors.newFixedThreadPool(this.getPoolSize());
				logDebug("Max search key count " + maxRecordCount);
				
				// calculate no. of items per thread
				int searchKeyToFetchPerThread = (maxRecordCount / this.getPoolSize()) + 1;
				logDebug("per thread searchKey to fetch items = "+ searchKeyToFetchPerThread);
				
				int startIndex = 1;
				int endIndex = searchKeyToFetchPerThread;
				
				CountDownLatch latchCount = new CountDownLatch(this.getPoolSize());
				for (int threadCounter = 0; threadCounter < this
						.getPoolSize(); threadCounter++) {
					logDebug("Ready to spawn search key threads inside for loop");
					
					Runnable searchKeyThread = new KeywordCacheLoaderRunnableThread(
							startIndex, endIndex, latchCount);
					// execute thread
					threadPool.execute(searchKeyThread);
					
					startIndex = endIndex + 1;
					endIndex = endIndex
							+ searchKeyToFetchPerThread;						
				}
				//it will waith all thread to get complete
				latchCount.await();
				
				logDebug(CLS_NAME + " | All Pool Threads Completed");
				}
			
		} catch (Exception e) {
			BBBPerformanceMonitor.cancel(CLS_NAME, "loadEPHCategoryMapCache");
			logError(CLS_NAME + " | Error occurred while launching threads : ", e);
		} finally {
			try {
				// close resultSet
				if (resultSet != null) {
					resultSet.close();
				}				
				//close PreparedStatement
				if (preparedtmt != null) {
					preparedtmt.close();
				}
				// close connection
				if (conn != null) {
					conn.close();
				}
			  if( threadPool != null){	
				   threadPool.shutdown();
				}
				 
				
				BBBPerformanceMonitor.end(CLS_NAME, "loadEPHCategoryMapCache");
			} catch (SQLException sqlExp) {
				logError(
						CLS_NAME+" | SQLException from loadEPHCategoryMapCache()",
						sqlExp);
			}
			catch (SecurityException securityExc) {
				logError(CLS_NAME+" | SecurityException from loadEPHCategoryMapCache()",	securityExc);
			}
			
			logDebug(CLS_NAME + " | Exiting loadEPHCategoryMapCache()");
		}
	}
	
	@Override
	public void doStopService(){
		logDebug(CLS_NAME+".doStopService called");
    	if(null != threadPool && !threadPool.isShutdown()){
    		threadPool.shutdownNow();
    		logDebug(CLS_NAME+".doStopService | shutting down the thread pool forcefully.");
    	}    
	}

	/**
	 * @return the queryAllMaxSearchKeySeq
	 */
	public String getQueryAllMaxSearchKeySeq() {
		return queryAllMaxSearchKeySeq;
	}

	/**
	 * @param queryAllMaxSearchKeySeq the queryAllMaxSearchKeySeq to set
	 */
	public void setQueryAllMaxSearchKeySeq(String queryAllMaxSearchKeySeq) {
		this.queryAllMaxSearchKeySeq = queryAllMaxSearchKeySeq;
	}

	/**
	 * @return the queryAllSearchKeyData
	 */
	public String getQueryAllSearchKeyData() {
		return queryAllSearchKeyData;
	}

	/**
	 * @param queryAllSearchKeyData the queryAllSearchKeyData to set
	 */
	public void setQueryAllSearchKeyData(String queryAllSearchKeyData) {
		this.queryAllSearchKeyData = queryAllSearchKeyData;
	}

	/**
	 * @return the queryLastDayMaxSearchKeySeq
	 */
	public String getQueryLastDayMaxSearchKeySeq() {
		return queryLastDayMaxSearchKeySeq;
	}

	/**
	 * @param queryLastDayMaxSearchKeySeq the queryLastDayMaxSearchKeySeq to set
	 */
	public void setQueryLastDayMaxSearchKeySeq(String queryLastDayMaxSearchKeySeq) {
		this.queryLastDayMaxSearchKeySeq = queryLastDayMaxSearchKeySeq;
	}

	/**
	 * @return the queryLastDaySearchKeyData
	 */
	public String getQueryLastDaySearchKeyData() {
		return queryLastDaySearchKeyData;
	}

	/**
	 * @param queryLastDaySearchKeyData the queryLastDaySearchKeyData to set
	 */
	public void setQueryLastDaySearchKeyData(String queryLastDaySearchKeyData) {
		this.queryLastDaySearchKeyData = queryLastDaySearchKeyData;
	}

	/**
	 * @return the fullCacheLoadRequired
	 */
	public boolean isFullCacheLoadRequired() {
		return fullCacheLoadRequired;
	}

	/**
	 * @param fullCacheLoadRequired the fullCacheLoadRequired to set
	 */
	public void setFullCacheLoadRequired(boolean fullCacheLoadRequired) {
		this.fullCacheLoadRequired = fullCacheLoadRequired;
	}		
}
