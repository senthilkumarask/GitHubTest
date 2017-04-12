package com.bbb.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bbb.cache.listener.BBBDynamicPriceCacheHelper;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.cache.BBBDynamicPriceCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSARepository;
import atg.nucleus.ServiceException;
import atg.repository.Repository;

/**
 * 
 *         BBBDynamicPriceDataCacheLoader loads the product id and
 *         productVo from DynamicPriceRepository as key-value pair into
 *         Coherence Cache or BBBProductDynamicRepositoryLocalCacheContainer
 *         as configured in the corresponding properties file.  Similarly, 
 *         sku id and skuVo are loaded in the cache. Threads are created as 
 *         per pool size to load the data in batches.
 * 
 * 
 */

public class BBBDynamicPriceDataCacheLoader extends BBBGenericService {

	private ExecutorService threadPoolProduct;
	private ExecutorService threadPoolSku;
	private Repository dynamicRepository;
	private final String PRODUCT_ID = "product_id";
	private final String SKU_ID = "sku_id";
	private final String MAX_SEQ_NUM_PROD = "max_seq_num_prod";
	private final String MAX_SEQ_NUM_SKU = "max_seq_num_sku";
	private final String DYNAMIC_PRICE_CACHE_LOADER_POOL_SIZE = "dynamicPriceCacheLoaderPoolSize";
	private BBBConfigTools mConfigTools;
	private String updateProdString;
	private String updateSkuString;
	private BBBDynamicPriceCacheContainer productCacheContainer;
	private BBBDynamicPriceCacheContainer skuCacheContainer;
	private String queryMaxProdSeq;
	private String queryMaxSkuSeq;
	private String queryProdPriceStrings;
	private String querySkuPriceFlags;
	private String queryUpdatedProducts;
	private String queryUpdatedDynamicProducts;
	private String queryUpdatedDynamicSkus;
	private String queryUpdatedSkus;
	private String queryUpdatedLeftOverSkus;
	private boolean enabled;
	private int poolSize;
	private BBBDynamicPriceCacheHelper dynamicPriceCacheHelper;

	public BBBConfigTools getConfigTools() {
		return mConfigTools;
	}

	public void setConfigTools(final BBBConfigTools configTools) {
		this.mConfigTools = configTools;
	}	

	@Override
	public void doStartService() throws ServiceException {
		logDebug("BBBDynamicPriceDataCacheLoader | doStartService()");
		if (this.isEnabled()) {
			int poolSize;
			// code to fetch pool size from BCC
			poolSize = getConfigTools().getValueForConfigKey(
						BBBCatalogConstants.CONTENT_CATALOG_KEYS,
						DYNAMIC_PRICE_CACHE_LOADER_POOL_SIZE, this.getPoolSize());
					this.setPoolSize(poolSize);
					logDebug("BBBDynamicPriceDataCacheLoader | doStartService | POOL_SIZE from BCC "
							+ this.getPoolSize());
			threadPoolProduct = Executors
					.newFixedThreadPool(this.getPoolSize());
			threadPoolSku = Executors.newFixedThreadPool(this.getPoolSize());
			loadDynamicPricingCache();
		} else {
			logDebug("BBBDynamicPriceDataCacheLoader | doStartService | enabled flag is false hence caching will not be done.");
		}
	}

	class ProductCacheLoaderRunnableThread implements Runnable {

		private int mStartIndex;

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

		public ProductCacheLoaderRunnableThread(int startIndex, int endIndex) {
			mStartIndex = startIndex;
			mEndIndex = endIndex;
			logDebug("Initializeing thread :"+ Thread.currentThread().getName()+ " ProductCacheLoaderRunnableThread - StartIndex: "
					+ startIndex + " EndIndex: " + endIndex);

		}

		/**
		 * run() is called by each thread to load the coherence cache in
		 * batches.
		 */

		public void run() {
			BBBPerformanceMonitor.start("ProductCacheLoaderRunnableThread",
					"run");
			Connection conn = null;
			PreparedStatement preparedStmt = null;
			ResultSet rs = null;
			Map productBuffer = new HashMap();
			int countProd=1;
			
			try {

				long startTime = System.currentTimeMillis();
				logDebug(Thread.currentThread().getName()
						+ "Free Memory before processing ProductCacheLoaderRunnableThread:"
						+ getMemoryUsed() + " Start time :" + startTime);

				
					conn = ((GSARepository) getDynamicRepository()).getDataSource()
							.getConnection();
				
				logDebug("SQL for product Thread"
						+ Thread.currentThread().getName() + " is "
						+ getQueryProdPriceStrings());
				if (conn != null) {
				preparedStmt = conn.prepareStatement(getQueryProdPriceStrings());
				}
				if(null != preparedStmt){
					preparedStmt.setLong(1, getStartIndex());
					preparedStmt.setLong(2, getEndIndex());
					rs = preparedStmt.executeQuery();	
				}
				
				if (null != rs) {
					while (rs.next()) {
						if (BBBUtility.isNotEmpty(rs.getString(PRODUCT_ID))) {
							getDynamicPriceCacheHelper().populateProductCache(rs,productBuffer);
							if ((countProd++ % getDynamicPriceCacheHelper().getCoherenceBatchSize()) == 0)
			                {
								getProductCacheContainer().bulkLoad(productBuffer,BBBCoreConstants.DYNAMIC_PRODUCT_OBJECT);
								productBuffer.clear();
			                }
							
						}
					}
					if (!productBuffer.isEmpty())
					{
						getProductCacheContainer().bulkLoad(productBuffer,BBBCoreConstants.DYNAMIC_PRODUCT_OBJECT);
						productBuffer.clear();
			        }
				}
				

				long endTime = System.currentTimeMillis();
				logDebug("Ending thread="+ Thread.currentThread().getName()	+ " Free Memory after processing ProductCacheLoaderRunnableThread:"	+ getMemoryUsed() + " Total time by this thread:"
						+ (endTime - startTime));

			} catch (Exception e) {
				logError("ProductCacheLoaderRunnableThread | Exception", e);
				BBBPerformanceMonitor.cancel("ProductCacheLoaderRunnableThread", "run");
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
					BBBPerformanceMonitor.end(
							"ProductCacheLoaderRunnableThread", "run");
				} catch (SQLException sqlExp) {
					logError("ProductCacheLoaderRunnableThread | SQLException from run()",sqlExp);
				}

			}

		}

	}	

	class SkuCacheLoaderRunnableThread implements Runnable {

		private int mStartIndex;

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

		public SkuCacheLoaderRunnableThread(int startIndex, int endIndex) {
			mStartIndex = startIndex;
			mEndIndex = endIndex;
			logDebug("Initializeing thread :"+ Thread.currentThread().getName()+ " SkuCacheLoaderRunnableThread - StartIndex: "
					+ startIndex + " EndIndex: " + endIndex);

		}

		/**
		 * run() is called by each thread to load the coherence cache in
		 * batches.
		 */

		public void run() {
			int countSku=1;
			Map skuBuffer = new HashMap();
			BBBPerformanceMonitor.start("SkuCacheLoaderRunnableThread", "run");
			Connection conn = null;
			PreparedStatement preparedStmt = null;
			ResultSet rs = null;
			try {

				long startTime = System.currentTimeMillis();
				logDebug(Thread.currentThread().getName()
						+ " Free Memory before processing SkuCacheLoaderRunnableThread:"
						+ getMemoryUsed() + " Start time :" + startTime);

				
					conn = ((GSARepository) getDynamicRepository()).getDataSource()
							.getConnection();
				

				logDebug("SQL for sku Thread"
						+ Thread.currentThread().getName() + " is "
						+ getQuerySkuPriceFlags());
				if(null != conn){
					preparedStmt = conn.prepareStatement(getQuerySkuPriceFlags());
				}
				if(null != preparedStmt){
					preparedStmt.setLong(1, getStartIndex());
					preparedStmt.setLong(2, getEndIndex());

					rs = preparedStmt.executeQuery();	
				}
				
				if (null != rs) {
					while (rs.next()) {

						if (BBBUtility.isNotEmpty(rs.getString(SKU_ID))) {
							getDynamicPriceCacheHelper().populateSkuCache(rs,skuBuffer);
							if ((countSku++ % getDynamicPriceCacheHelper().getCoherenceBatchSize()) == 0)
			                {
								getSkuCacheContainer().bulkLoad(skuBuffer,BBBCoreConstants.DYNAMIC_SKU_OBJECT);
								skuBuffer.clear();
			                }
						}

					}
					if (!skuBuffer.isEmpty())
					{
						getSkuCacheContainer().bulkLoad(skuBuffer,BBBCoreConstants.DYNAMIC_SKU_OBJECT);
						skuBuffer.clear();
			        }
				}

				long endTime = System.currentTimeMillis();
				logDebug("Ending thread="+ Thread.currentThread().getName()	+ " Free Memory after processing SkuCacheLoaderRunnableThread:"	+ getMemoryUsed() + " Total time by this thread:"
						+ (endTime - startTime));

			} catch (Exception e) {
				BBBPerformanceMonitor.cancel("SkuCacheLoaderRunnableThread","run");
				logError("SkuCacheLoaderRunnableThread | Exception", e);
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
					BBBPerformanceMonitor.end("SkuCacheLoaderRunnableThread",
							"run");
				} catch (SQLException sqlExp) {
					logError(
							"SkuCacheLoaderRunnableThread | SQLException from run()",
							sqlExp);
				}

			}

		}

	}

	/**
	 * Loads the cache through the thread based on the Max product id and max
	 * sku id.
	 */
	public void loadDynamicPricingCache() {
		logDebug("BBBDynamicPriceDataCacheLoader | Entering loadDynamicPricingCache()");
		BBBPerformanceMonitor.start("BBBDynamicPriceDataCacheLoader",
				"loadDynamicPricingCache");
		Connection conn = null;
		PreparedStatement preparedProductStmt = null;
		ResultSet rsProduct = null;
		PreparedStatement preparedSkuStmt = null;
		ResultSet rsSku = null;
		boolean isSkuloadComplete = false;
		boolean skusNotNull=true;
		try {

			
				conn = ((GSARepository) getDynamicRepository()).getDataSource().getConnection();
			
			if(null != conn){
				preparedProductStmt = conn.prepareStatement(this.getQueryMaxProdSeq());
				preparedSkuStmt = conn.prepareStatement(this.getQueryMaxSkuSeq());	
			}
			if(null != preparedProductStmt){
				rsProduct = preparedProductStmt.executeQuery();
			}
			if(null!=  preparedSkuStmt){
				rsSku = preparedSkuStmt.executeQuery();	
			}
			
			if (null != rsProduct) {
				rsProduct.next();
				int maxProductCount = rsProduct.getInt(MAX_SEQ_NUM_PROD);

				if (maxProductCount == 0|| maxProductCount < this.getPoolSize()) {

					logDebug("BBBDynamicPriceDataCacheLoader | Max product count is Zero or less than pool size");
					return;
				}
				logDebug("Max product count " + maxProductCount);
				int perThreadProductIdsToFetch = (maxProductCount / this
						.getPoolSize()) + 1;
				logDebug("perThreadProductIdsToFetch "
						+ perThreadProductIdsToFetch);
				int startProductIndex = 1;
				int endProductIndex = perThreadProductIdsToFetch;

				if (null != rsSku) {
					rsSku.next();
					int maxSkuCount = rsSku.getInt(MAX_SEQ_NUM_SKU);
					if (maxSkuCount == 0 || maxSkuCount < this.getPoolSize()) {
						logDebug("BBBDynamicPriceDataCacheLoader | Max sku count is Zero or less than pool size");
						skusNotNull=false;
					}
					int perThreadSkuIdsToFetch = (maxSkuCount / this.getPoolSize()) + 1;
					logDebug("Max sku count " + maxSkuCount);
					logDebug("perThreadSkuIdsToFetch " + perThreadSkuIdsToFetch);
					int startSkuIndex = 1;
					int endSkuIndex = perThreadSkuIdsToFetch;
					long startTime = System.currentTimeMillis();
					logDebug(Thread.currentThread().getName()+ "Start time to load Dynmaic Repository Cache:"+ startTime);
					for (int threadCounter = 0; threadCounter < this
							.getPoolSize(); threadCounter++) {
						logDebug("Ready to spawn product and sku threads inside for loop");
						Runnable productThread = new ProductCacheLoaderRunnableThread(
								startProductIndex, endProductIndex);
						threadPoolProduct.execute(productThread);
						if(skusNotNull){
						Runnable skuThread = new SkuCacheLoaderRunnableThread(
								startSkuIndex, endSkuIndex);
						threadPoolSku.execute(skuThread);

						startProductIndex = endProductIndex + 1;
						endProductIndex = endProductIndex
								+ perThreadProductIdsToFetch;

						startSkuIndex = endSkuIndex + 1;
						endSkuIndex = endSkuIndex + perThreadSkuIdsToFetch;
						}
					}
					
				
					logDebug(Thread.currentThread().getName()+ "End time to load Dynmaic Repository Cache:"+ System.currentTimeMillis());
					logDebug("BBBDynamicPriceDataCacheLoader | All Pool Threads Completed");
				}
			}
			
		} catch (Exception e) {
			BBBPerformanceMonitor.cancel("BBBDynamicPriceDataCacheLoader","loadDynamicPricingCache");
			logError("BBBDynamicPriceDataCacheLoader | Error occurred while launching threads : ",e);
			return;
		} finally {
			try {
				if (rsProduct != null) {
					rsProduct.close();
				}
				if (rsSku != null) {
					rsSku.close();
				}
				if (preparedSkuStmt != null) {
					preparedSkuStmt.close();
				}
				if (preparedProductStmt != null) {
					preparedProductStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
				threadPoolProduct.shutdown();
				threadPoolSku.shutdown();
				
				while(!threadPoolSku.isTerminated()){
					isSkuloadComplete = false;
				} 
				
				if(threadPoolSku.isTerminated()){
					isSkuloadComplete = true;
				}
				if(isSkuloadComplete && skusNotNull){
					//if there are SKU items, then rebuild local cache 
					getDynamicPriceCacheHelper().fireDynamcePriceMessage();
				}
				
				BBBPerformanceMonitor.end("BBBDynamicPriceDataCacheLoader","loadDynamicPricingCache");
			} catch (SQLException sqlExp) {

				logError("BBBDynamicPriceDataCacheLoader | SQLException from loadDynamicPricingCache()",sqlExp);
			}

			logDebug("BBBDynamicPriceDataCacheLoader | Exiting loadDynamicPricingCache()");
		}

	}
	private static long getMemoryUsed() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		return (totalMemory - freeMemory);
	}

	
	public BBBDynamicPriceCacheContainer getProductCacheContainer() {
		return productCacheContainer;
	}

	public void setProductCacheContainer(
			BBBDynamicPriceCacheContainer productCacheContainer) {
		this.productCacheContainer = productCacheContainer;
	}

	public BBBDynamicPriceCacheContainer getSkuCacheContainer() {
		return skuCacheContainer;
	}

	public void setSkuCacheContainer(
			BBBDynamicPriceCacheContainer skuCacheContainer) {
		this.skuCacheContainer = skuCacheContainer;
	}

	public ExecutorService getThreadPoolProduct() {
		return threadPoolProduct;
	}

	public void setThreadPoolProduct(ExecutorService threadPoolProduct) {
		this.threadPoolProduct = threadPoolProduct;
	}

	public ExecutorService getThreadPoolSku() {
		return threadPoolSku;
	}

	public void setThreadPoolSku(ExecutorService threadPoolSku) {
		this.threadPoolSku = threadPoolSku;
	}

	public String getUpdateProdString() {
		return updateProdString;
	}

	public void setUpdateProdString(String updateProdString) {
		this.updateProdString = updateProdString;
	}

	public String getUpdateSkuString() {
		return updateSkuString;
	}

	public void setUpdateSkuString(String updateSkuString) {
		this.updateSkuString = updateSkuString;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public String getQueryMaxProdSeq() {
		return queryMaxProdSeq;
	}

	public void setQueryMaxProdSeq(String queryMaxProdSeq) {
		this.queryMaxProdSeq = queryMaxProdSeq;
	}

	public String getQueryMaxSkuSeq() {
		return queryMaxSkuSeq;
	}

	public void setQueryMaxSkuSeq(String queryMaxSkuSeq) {
		this.queryMaxSkuSeq = queryMaxSkuSeq;
	}

	public String getQueryProdPriceStrings() {
		return queryProdPriceStrings;
	}

	public void setQueryProdPriceStrings(String queryProdPriceStrings) {
		this.queryProdPriceStrings = queryProdPriceStrings;
	}

	public String getQuerySkuPriceFlags() {
		return querySkuPriceFlags;
	}

	public void setQuerySkuPriceFlags(String querySkuPriceFlags) {
		this.querySkuPriceFlags = querySkuPriceFlags;
	}

	public String getQueryUpdatedProducts() {
		return queryUpdatedProducts;
	}

	public void setQueryUpdatedProducts(String queryUpdatedProducts) {
		this.queryUpdatedProducts = queryUpdatedProducts;
	}

	public String getQueryUpdatedDynamicProducts() {
		return queryUpdatedDynamicProducts;
	}

	public void setQueryUpdatedDynamicProducts(
			String queryUpdatedDynamicProducts) {
		this.queryUpdatedDynamicProducts = queryUpdatedDynamicProducts;
	}

	public String getQueryUpdatedDynamicSkus() {
		return queryUpdatedDynamicSkus;
	}

	public void setQueryUpdatedDynamicSkus(String queryUpdatedDynamicSkus) {
		this.queryUpdatedDynamicSkus = queryUpdatedDynamicSkus;
	}

	public String getQueryUpdatedSkus() {
		return queryUpdatedSkus;
	}

	public void setQueryUpdatedSkus(String queryUpdatedSkus) {
		this.queryUpdatedSkus = queryUpdatedSkus;
	}

	public String getQueryUpdatedLeftOverSkus() {
		return queryUpdatedLeftOverSkus;
	}

	public void setQueryUpdatedLeftOverSkus(String queryUpdatedLeftOverSkus) {
		this.queryUpdatedLeftOverSkus = queryUpdatedLeftOverSkus;
	}

	public Repository getDynamicRepository() {
		return dynamicRepository;
	}

	public void setDynamicRepository(Repository dynamicRepository) {
		this.dynamicRepository = dynamicRepository;
	}

	/**
	 * @return the dynamicPriceCacheHelper
	 */
	public final BBBDynamicPriceCacheHelper getDynamicPriceCacheHelper() {
		return dynamicPriceCacheHelper;
	}

	/**
	 * @param dynamicPriceCacheHelper the dynamicPriceCacheHelper to set
	 */
	public final void setDynamicPriceCacheHelper(BBBDynamicPriceCacheHelper dynamicPriceCacheHelper) {
		this.dynamicPriceCacheHelper = dynamicPriceCacheHelper;
	}
}
