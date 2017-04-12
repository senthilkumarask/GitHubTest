package com.bbb.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import atg.adapter.gsa.GSARepository;
import atg.nucleus.ServiceException;
import atg.repository.Repository;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.utils.BBBUtility;

/**
 * 
 * @author vnalini BBBLocalStoreCacheLoader loads the inventory details [store
 *         id, sku id and the stock level] to Coherence Cache. Threads are
 *         created as per pool size to load the coherence cache for different
 *         ranges.
 * 
 */

public class BBBLocalStoreCacheLoader extends BBBGenericService {

	private int mRangeCount;
	private ExecutorService threadPool;
	private Repository mRepository;
	private final String STORE_ID = "store_id";
	private final String SKU_ID = "sku_id";
	private final String STOCK_LEVEL = "stock_level";
	private final String CACHE_STORE_INV = "localstore-near-local-store-inv";
	private final String MAX_STORE_ID = "max_store_id";
	private final String INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES = "invLocalStoreRepoSchedulerConfigValues";
	private final String QUERY_MAX_STORE_ID = "SELECT max(to_number(store_id)) as max_store_id FROM BBB_STORE_LOCAL_INVENTORY";
	private final String QUERY_INVENTORY = "SELECT * FROM BBB_STORE_LOCAL_INVENTORY where store_id between ";
	private BBBCatalogTools mCatalogTools;
	private final String POOL_SIZE="PoolSize";
	private int coherenceBatchSize;
	private CoherenceCacheContainer cacheContainer;
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	/**
	 * @return the cacheContainer
	 */
	public CoherenceCacheContainer getCacheContainer() {
		return cacheContainer;
	}

	/**
	 * @param cacheContainer
	 *            the cacheContainer to set
	 */
	public void setCacheContainer(CoherenceCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}

	/**
	 * @return the mRangeCount
	 */
	public int getRangeCount() {
		return mRangeCount;
	}

	/**
	 * @param mRangeCount
	 *            the mRangeCount to set
	 */
	public void setRangeCount(int pRangeCount) {
		this.mRangeCount = pRangeCount;
	}


	public void setRepository(Repository repository) {
		this.mRepository = repository;
	}

	public Repository getRepository() {
		return this.mRepository;
	}

	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		int intPoolSize=2;
		List<String> poolSize;
		// this code to fetch pool size from BCC will be refactored to new method
		try {
			poolSize = getCatalogTools()
					.getAllValuesForKey(
							INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES,
							POOL_SIZE);
			if (null != poolSize && !poolSize.isEmpty()) {
				intPoolSize = Integer.parseInt(getCatalogTools().getAllValuesForKey(
						INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES, POOL_SIZE)
						.get(0).toString());
				logDebug("BBBLocalStoreCacheLoader | doStartService | POOL_SIZE from BCC "+intPoolSize);
			}
		} catch (BBBSystemException e) {
			logError("BBBLocalStoreCacheLoader | doStartService | BBBSystemException", e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException | doStartService | BBBSystemException", e);
		}
		
		threadPool = Executors.newFixedThreadPool(intPoolSize);
		// loadCache();

	}

	class CacheLoaderRunnableThread implements Runnable {

		private int mStartIndex;
		private CountDownLatch mLatchCount;

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

		private int mEndIndex;
		
		
		

		public CountDownLatch getmLatchCount() {
			return mLatchCount;
		}

		public void setmLatchCount(CountDownLatch mLatchCount) {
			this.mLatchCount = mLatchCount;
		}

		public CacheLoaderRunnableThread(int startIndex, int endIndex,CountDownLatch latchCount) {
			mStartIndex = startIndex;
			mEndIndex = endIndex;
			this.mLatchCount=latchCount;
			logDebug("Initializeing thread :"
					+ Thread.currentThread().getName() + " - StartIndex: "
					+ startIndex + " EndIndex: " + endIndex);

		}

		/**
		 * run() is called by each thread to load the coherence cache in
		 * batches.
		 */

		public void run() {
			Connection conn = null;
			PreparedStatement preparedStmt = null;
			ResultSet rs = null;
			try {

				long startTime = System.currentTimeMillis();
				logDebug(Thread.currentThread().getName()
						+ " Free Memory before processing:" + getMemoryUsed()
						+ " Start time :" + startTime);
				int index = BBBCoreConstants.ZERO;
				
					conn = ((GSARepository) getRepository()).getDataSource()
							.getConnection();
				
				String sql = QUERY_INVENTORY + getStartIndex() + " AND "
						+ getEndIndex();

				logDebug("SQL for Thread" + Thread.currentThread().getName()
						+ " is " + sql);
				if (conn != null) {
				preparedStmt = conn.prepareStatement(sql);
				}
				// Check if this fetch size is now required
				if(null!=preparedStmt){
				preparedStmt.setFetchSize(getRangeCount());
				rs = preparedStmt.executeQuery();
				}
				if (null != rs) {
					Map<String, Integer> storyInventoryMap = new HashMap<String, Integer>();
					while (rs.next()) {
						
						if (BBBUtility.isNotEmpty(rs.getString(STORE_ID)) && BBBUtility.isNotEmpty(rs.getString(SKU_ID))) {
							storyInventoryMap.put(rs.getString(STORE_ID) + "-" + rs.getString(SKU_ID), rs.getInt(STOCK_LEVEL));
							index ++;
						}
						
						if (index % getCoherenceBatchSize() == 0) {
							getCacheContainer().putAll(storyInventoryMap, CACHE_STORE_INV);
							storyInventoryMap.clear();
						}

					}
					getCacheContainer().putAll(storyInventoryMap, CACHE_STORE_INV);
					storyInventoryMap.clear();
				}

				long endTime = System.currentTimeMillis();
				logDebug("Ending thread=" + Thread.currentThread().getName()
						+ " Free Memory after processing:" + getMemoryUsed()
						+ " Total time by this thread:" + (endTime - startTime));
				
			} catch (Exception e) {
				logError("BBBLocalStoreCacheLoader | Exception", e);
				return;
			} finally {
				mLatchCount.countDown();
				try {
					if (rs != null) {
						rs.close();
					}
					if (preparedStmt != null) {
						preparedStmt.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (SQLException sqlExp) {
					logError(
							"BBBLocalStoreCacheLoader | SQLException from run()",
							sqlExp);
				}
			
			}

		}

	}

	/**
	 * Loads the cache through the thread based on the Max store id.
	 */
	public void loadCache() {
		logDebug("BBBLocalStoreCacheLoader | Entering loadCache()");

		Connection conn = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		int intPoolSize=2;

		try {

			
				conn = ((GSARepository) getRepository()).getDataSource()
						.getConnection();
			
		if (conn != null) {
			preparedStmt = conn.prepareStatement(QUERY_MAX_STORE_ID);
		}
		
		if(null != preparedStmt){
			preparedStmt.setFetchSize(getRangeCount());
			rs = preparedStmt.executeQuery();
		}
			if (null != rs) {
				rs.next();
				int maxStoreId = rs.getInt(MAX_STORE_ID);
				
				// this code to fetch pool size from BCC will be refactored to new method
				List<String> poolSize = getCatalogTools()
						.getAllValuesForKey(
								INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES,
								POOL_SIZE);
				if (null != poolSize && !poolSize.isEmpty()) {
					intPoolSize = Integer.parseInt(getCatalogTools().getAllValuesForKey(
							INV_LOCAL_STORE_SCHEDULER_CONFIG_VALUES, POOL_SIZE)
							.get(0).toString());
					logDebug("BBBLocalStoreCacheLoader | POOL_SIZE from BCC "+intPoolSize);
				}
				
				if (maxStoreId == 0 || maxStoreId < intPoolSize) {

					logDebug("BBBLocalStoreCacheLoader | Max store id is Zero or less than pool size");
					return;
				}

				int perThreadStoreIdsToFetch = (maxStoreId / intPoolSize) + 1;

				int startIndex = 0;
				int endIndex = perThreadStoreIdsToFetch;
				 
				CountDownLatch latchCount = new CountDownLatch( intPoolSize);
				for (int threadCounter = 0; threadCounter < intPoolSize; threadCounter++) {

					Runnable newQueryThread = new CacheLoaderRunnableThread(
							startIndex, endIndex,latchCount);

					threadPool.execute(newQueryThread);
					startIndex = endIndex;
					endIndex = endIndex + perThreadStoreIdsToFetch;
				}
				//-----------it will wait for 30 minute
				//latchCount.await(30, TimeUnit.MINUTES);
				//it will waith all thread to get complete
				latchCount.await();
				logDebug("BBBLocalStoreCacheLoader | All Pool Threads Completed, ready to send email");
				
				
			}

		} catch (Exception e) {
			logError(
					"BBBLocalStoreCacheLoader | Error occurred while launching threads : ",
					e);
			return;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (preparedStmt != null) {
					preparedStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
				//threadPool.shutdown();

			} catch (SQLException sqlExp) {

				logError(
						"BBBLocalStoreCacheLoader | SQLException from loadCache()",
						sqlExp);
			}

			logDebug("BBBLocalStoreCacheLoader | Exiting loadCache()");
		}

	}

	private static long getMemoryUsed() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		return (totalMemory - freeMemory);
	}

	public static int sizeof(Object obj) throws IOException {

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteOutputStream);

		objectOutputStream.writeObject(obj);
		objectOutputStream.flush();
		objectOutputStream.close();

		return byteOutputStream.toByteArray().length;
	}

	public int getCoherenceBatchSize() {
		return coherenceBatchSize;
	}

	public void setCoherenceBatchSize(int coherenceBatchSize) {
		this.coherenceBatchSize = coherenceBatchSize;
	}

}
