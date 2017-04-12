package com.bbb.cache.listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import atg.adapter.gsa.GSARepository;
import atg.repository.Repository;
import atg.service.email.SMTPEmailSender;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBCacheInvalidatorSource;
import com.bbb.framework.cache.BBBDynamicPriceCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO;
import com.bbb.search.bean.result.BBBDynamicPriceVO;
import com.bbb.utils.BBBUtility;


/**
 * The Class BBBDynamicPriceCacheHelper.
 */
public class BBBDynamicPriceCacheHelper extends BBBGenericService{
	
	/** The dynamic repository. */
	private Repository dynamicRepository;
	
	/** The cache invalidator message source. */
	private BBBCacheInvalidatorSource cacheInvalidatorMessageSource;
	
	/** The product id. */
	private final String PRODUCT_ID = "product_id";
	
	/** The bab list price string. */
	private final String BAB_LIST_PRICE_STRING = "BAB_LIST_PRICE_STRING";
	
	/** The bab pricing label code. */
	private final String BAB_PRICING_LABEL_CODE = "BAB_PRICING_LABEL_CODE";
	
	/** The bab sale price string. */
	private final String BAB_SALE_PRICE_STRING = "BAB_SALE_PRICE_STRING";
	
	/** The bab incart flag. */
	private final String BAB_INCART_FLAG = "BAB_INCART_FLAG";
	
	/** The bbb incart flag. */
	private final String BBB_INCART_FLAG = "BBB_INCART_FLAG";
	
	/** The bbb list price string. */
	private final String BBB_LIST_PRICE_STRING = "BBB_LIST_PRICE_STRING";
	
	/** The bbb pricing label code. */
	private final String BBB_PRICING_LABEL_CODE = "BBB_PRICING_LABEL_CODE";
	
	/** The bbb sale price string. */
	private final String BBB_SALE_PRICE_STRING = "BBB_SALE_PRICE_STRING";
	
	/** The ca incart flag. */
	private final String CA_INCART_FLAG = "CA_INCART_FLAG";
	
	/** The ca list price string. */
	private final String CA_LIST_PRICE_STRING = "CA_LIST_PRICE_STRING";
	
	/** The ca sale price string. */
	private final String CA_SALE_PRICE_STRING = "CA_SALE_PRICE_STRING";
	
	/** The ca pricing label code. */
	private final String CA_PRICING_LABEL_CODE = "CA_PRICING_LABEL_CODE";
	
	/** The mx incart flag. */
	private final String MX_INCART_FLAG = "MX_INCART_FLAG";
	
	/** The mx pricing label code. */
	private final String MX_PRICING_LABEL_CODE = "MX_PRICING_LABEL_CODE";
	
	/** The mx sale price string. */
	private final String MX_SALE_PRICE_STRING = "MX_SALE_PRICE_STRING";
	
	/** The mx list price string. */
	private final String MX_LIST_PRICE_STRING = "MX_LIST_PRICE_STRING";
	
	/** The sku id. */
	private final String SKU_ID = "sku_id";
	
	/** The product cache container. */
	private BBBDynamicPriceCacheContainer productCacheContainer;
	
	/** The sku cache container. */
	private BBBDynamicPriceCacheContainer skuCacheContainer;
	
	/** The query max prod seq. */
	private String queryMaxProdSeq;
	
	/** The query max sku seq. */
	private String queryMaxSkuSeq;
	
	/** The query prod price strings. */
	private String queryProdPriceStrings;
	
	/** The query sku price flags. */
	private String querySkuPriceFlags;
	
	/** The query updated products. */
	private String queryUpdatedProducts;
	
	/** The query updated dynamic products. */
	private String queryUpdatedDynamicProducts;
	
	/** The query updated dynamic skus. */
	private String queryUpdatedDynamicSkus;
	
	/** The query updated skus. */
	private String queryUpdatedSkus;
	
	/** The query updated left over skus. */
	private String queryUpdatedLeftOverSkus;
	
	/** The job target. */
	private String jobTarget;
	
	/** The data center. */
	private String dataCenter;
	
	/** The update cache rebuild time query. */
	private String updateCacheRebuildTimeQuery;
	
	/** The pim connection name. */
	private String pimConnectionName;
	
	/** The coherence batch size. */
	private int coherenceBatchSize;
	
	/** The email sender. */
	private SMTPEmailSender emailSender;
	
	private String cacheUpdateFailEmailIds;
	private String cacheUpdateFailMessage;
	private String cacheUpdateFailMessageSubject;

	/** Property variable to decide to fire DMS messages for local SKU cache rebuild*/
	private boolean fireMessageSkuLocalCacheBuild;
	
	/**
	 * Update dynamic repository cache.
	 *
	 * @return true, if successful
	 */
	public boolean updateDynamicRepositoryCache() {
		boolean cacheUpdateSuccessful=false;
		logInfo("BBBDynamicPriceCacheHelper | Entering updateDynamicRepositoryCache()");
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheHelper","updateDynamicRepositoryCache");
		Connection conn = null;
		PreparedStatement preparedUpdatedProductStmt = null;
		ResultSet rsUpdatedProduct = null;
		PreparedStatement preparedUpdatedSkuStmt = null;
		ResultSet rsUpdatedSku = null;
		PreparedStatement preparedLeftOverUpdatedSkuStmt = null;
		ResultSet rsLeftOverUpdatedSkus = null;
		Map productBuffer = new HashMap();
		Map skuBuffer = new HashMap();
		int countSku = 1;
		try {
			if (conn == null) {
				conn = ((GSARepository) getDynamicRepository()).getDataSource().getConnection();
			}
			logInfo("SQL for querying updated products is: "+ this.getQueryUpdatedDynamicProducts());
			preparedUpdatedProductStmt = conn.prepareStatement(this.getQueryUpdatedDynamicProducts());
			rsUpdatedProduct = preparedUpdatedProductStmt.executeQuery();
			logInfo("SQL for querying updated sku's is: "+ this.getQueryUpdatedDynamicSkus());
			long startTime = System.currentTimeMillis();
			logInfo(" Free Memory before processing updateDynamicRepositoryCache:"	+ getMemoryUsed() + " Start time :" + startTime);
			if (null != rsUpdatedProduct) {
				int countProd = 1;
				while (rsUpdatedProduct.next()) {
					if (BBBUtility.isNotEmpty(rsUpdatedProduct.getString(PRODUCT_ID))) {
						logDebug("Updating Dynamic repository cache for product id: " + rsUpdatedProduct.getString(PRODUCT_ID));
						this.populateProductCache(rsUpdatedProduct, productBuffer);
						if ((countProd++ % getCoherenceBatchSize()) == 0)
		                {
							getProductCacheContainer().bulkLoad(productBuffer,BBBCoreConstants.DYNAMIC_PRODUCT_OBJECT);
							productBuffer.clear();
		                }
						
						preparedUpdatedSkuStmt = conn.prepareStatement(this.getQueryUpdatedSkus());
						preparedUpdatedSkuStmt.setString(1,rsUpdatedProduct.getString(PRODUCT_ID));
						rsUpdatedSku = preparedUpdatedSkuStmt.executeQuery();
						if (null != rsUpdatedSku) {
							while (rsUpdatedSku.next()) {
								if (BBBUtility.isNotEmpty(rsUpdatedSku.getString(SKU_ID))) {
									logDebug("Fetched sku id: " + rsUpdatedSku.getString(SKU_ID));								
									if (BBBUtility.isNotEmpty(rsUpdatedSku.getString(SKU_ID))) {												
										logDebug("Updating Dynamic repository cache for sku id: " + rsUpdatedSku.getString(SKU_ID));
										this.populateSkuCache(rsUpdatedSku,skuBuffer);
										if ((countSku++ % getCoherenceBatchSize()) == 0)
								        {
											getSkuCacheContainer().bulkLoad(skuBuffer,BBBCoreConstants.DYNAMIC_SKU_OBJECT);
											skuBuffer.clear();
								        }
									}
								}
							}
							if (!skuBuffer.isEmpty())
							 {
								getSkuCacheContainer().bulkLoad(skuBuffer,BBBCoreConstants.DYNAMIC_SKU_OBJECT);
								skuBuffer.clear();
					          }
						}
					}
					
					if (rsUpdatedSku != null) {
						rsUpdatedSku.close();
					}
					if(preparedUpdatedSkuStmt!=null){
						preparedUpdatedSkuStmt.close();
					}
					
					
				}
				if (!productBuffer.isEmpty())
				 {
						getProductCacheContainer().bulkLoad(productBuffer,BBBCoreConstants.DYNAMIC_PRODUCT_OBJECT);
						productBuffer.clear();
		          }
				
			}
			logDebug("SQL for querying left over updated skus is: "+ this.getQueryUpdatedLeftOverSkus());
			preparedLeftOverUpdatedSkuStmt = conn.prepareStatement(this.getQueryUpdatedLeftOverSkus());
			rsLeftOverUpdatedSkus = preparedLeftOverUpdatedSkuStmt.executeQuery();
			
			if (null != rsLeftOverUpdatedSkus) {
				while (rsLeftOverUpdatedSkus.next()) {
					if (BBBUtility.isNotEmpty(rsLeftOverUpdatedSkus.getString(SKU_ID))) {												
						logDebug("Updating Dynamic repository cache for left over sku id: " + rsLeftOverUpdatedSkus.getString(SKU_ID));
						this.populateSkuCache(rsLeftOverUpdatedSkus,skuBuffer);
						if ((countSku++ % getCoherenceBatchSize()) == 0)
		                {
							getSkuCacheContainer().bulkLoad(skuBuffer,BBBCoreConstants.DYNAMIC_SKU_OBJECT);
							skuBuffer.clear();
		                }
					}
				}
				
			}
			if (!skuBuffer.isEmpty())
			 {
				getSkuCacheContainer().bulkLoad(skuBuffer,BBBCoreConstants.DYNAMIC_SKU_OBJECT);
				skuBuffer.clear();
	          }
			long endTime = System.currentTimeMillis();
			logInfo(" Free Memory after processing updateDynamicRepositoryCache:"+ getMemoryUsed()+ " Total time by this thread:"+ (endTime - startTime));
			cacheUpdateSuccessful=true;
			
			//fire messages to all JVMS to rebuild local cache
			this.fireDynamcePriceMessage();
		} catch (Exception e) {
			logError("BBBDynamicPriceCacheHelper | updateDynamicRepositoryCache | Exception",e);
			BBBPerformanceMonitor.cancel("BBBDynamicPriceCacheHelper","updateDynamicRepositoryCache");
			cacheUpdateSuccessful= false;
		} finally {
			try {
				if (rsUpdatedProduct != null) {
					rsUpdatedProduct.close();
				}
				if (rsLeftOverUpdatedSkus != null) {
					rsLeftOverUpdatedSkus.close();
				}
				if (preparedLeftOverUpdatedSkuStmt != null) {
					preparedLeftOverUpdatedSkuStmt.close();
				}
				
				
				if(preparedUpdatedProductStmt!=null){
					preparedUpdatedProductStmt.close();
				}
				if(preparedUpdatedSkuStmt!=null && !preparedUpdatedSkuStmt.isClosed()){
					preparedUpdatedSkuStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
				BBBPerformanceMonitor.end("BBBDynamicPriceCacheHelper",	"updateDynamicRepositoryCache");
				logInfo("BBBDynamicPriceCacheHelper: Cache update Successfully");
				
			} catch (SQLException sqlExp) {
				logError("BBBDynamicPriceCacheHelper | updateDynamicRepositoryCache | SQLException from run()",
						sqlExp);
				
			}

		}
		logInfo("Exiting BBBDynamicPriceCacheHelper: updateCache()");
		return cacheUpdateSuccessful;
	}
	
	/**
	 * populateSkuCache() is called by to populate BBBDynamicPriceSkuVO and put
	 * the data to cache. batches.
	 *
	 * @param rs the rs
	 * @param buffer the buffer
	 * @throws Exception the exception
	 */

	public void populateSkuCache(ResultSet rs,Map buffer) throws Exception {
		BBBDynamicPriceSkuVO skuVo = new BBBDynamicPriceSkuVO();
		skuVo.setBabyPricingLabelCode(rs.getString(BAB_PRICING_LABEL_CODE));
		skuVo.setBabyIncartFlag(rs.getBoolean(BAB_INCART_FLAG));
		skuVo.setBbbIncartFlag(rs.getBoolean(BBB_INCART_FLAG));
		skuVo.setBbbPricingLabelCode(rs.getString(BBB_PRICING_LABEL_CODE));
		skuVo.setCaIncartFlag(rs.getBoolean(CA_INCART_FLAG));
		skuVo.setCaPricingLabelCode(rs.getString(CA_PRICING_LABEL_CODE));
		skuVo.setMxIncartFlag(rs.getBoolean(MX_INCART_FLAG));
		skuVo.setMxPricingLabelCode(rs.getString(MX_PRICING_LABEL_CODE));
		buffer.put("sku_" + rs.getString(SKU_ID), skuVo);
		
	}

	/**
	 * This method invokes the source to trigger the DMS messages 
	 * to all JVMs to rebuild local dynamic price SKU cache
	 */
	public void fireDynamcePriceMessage(){
		
		logInfo("BBBDynamicPriceCacheHelper:fireDynamcePriceMessage starts");
		//fire messages to all JVMS to rebuild local cache
		if(isFireMessageSkuLocalCacheBuild()){
			logInfo("BBBDynamicPriceCacheHelper:fireDynamcePriceMessage sending messages");
			getCacheInvalidatorMessageSource().fireDynamicPriceCacheMessage();
		}
		logInfo("BBBDynamicPriceCacheHelper:fireDynamcePriceMessage ends");
	}
	
	/**
	 * populateProductCache() is called by to populate productVo and put the
	 * data to cache. batches.
	 *
	 * @param rs the rs
	 * @param buffer the buffer
	 * @throws Exception the exception
	 */

	public void populateProductCache(ResultSet rs,Map buffer) throws Exception {
		BBBDynamicPriceVO productVo = new BBBDynamicPriceVO();
		productVo.setBabyListPriceString(rs.getString(BAB_LIST_PRICE_STRING));
		productVo.setBabyPricingLabelCode(rs.getString(BAB_PRICING_LABEL_CODE));
		productVo.setBabySalePriceString(rs.getString(BAB_SALE_PRICE_STRING));
		productVo.setBabyIncartFlag(rs.getBoolean(BAB_INCART_FLAG));
		productVo.setBbbIncartFlag(rs.getBoolean(BBB_INCART_FLAG));
		productVo.setBbbListPriceString(rs.getString(BBB_LIST_PRICE_STRING));
		productVo.setBbbPricingLabelCode(rs.getString(BBB_PRICING_LABEL_CODE));
		productVo.setBbbSalePriceString(rs.getString(BBB_SALE_PRICE_STRING));
		productVo.setCaIncartFlag(rs.getBoolean(CA_INCART_FLAG));
		productVo.setCaListPriceString(rs.getString(CA_LIST_PRICE_STRING));
		productVo.setCaSalePriceString(rs.getString(CA_SALE_PRICE_STRING));
		productVo.setCaPricingLabelCode(rs.getString(CA_PRICING_LABEL_CODE));
		productVo.setMxIncartFlag(rs.getBoolean(MX_INCART_FLAG));
		productVo.setMxPricingLabelCode(rs.getString(MX_PRICING_LABEL_CODE));
		productVo.setMxSalePriceString(rs.getString(MX_SALE_PRICE_STRING));
		productVo.setMxListPriceString(rs.getString(MX_LIST_PRICE_STRING));
		buffer.put("product_" + rs.getString(PRODUCT_ID),productVo);
		
	}

	/**
	 * Gets the memory used.
	 *
	 * @return the memory used
	 */
	private static long getMemoryUsed() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		return (totalMemory - freeMemory);
	}
	
	/**
	 * Update cache last execution time.
	 */
	public void updateCacheLastExecutionTime() {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = pimConnection();
			long currentTime = new Date().getTime();
			statement = connection.prepareStatement(getUpdateCacheRebuildTimeQuery());
			statement.setTimestamp(1, new Timestamp(currentTime));
			statement.setString(2, getJobTarget());
			statement.setString(3, getDataCenter());
			statement.executeQuery();
			connection.commit();
		} catch (SQLException | BBBSystemException e) {
			logError(e);
		} finally {
			try {
				if(statement != null){
					statement.close();
				}
				if(connection != null){
					connection.close();	
				}
				
			} catch (SQLException e) {
				logError(e);
			}
		}

	}
	
	/**
	 * Pim connection.
	 *
	 * @return the connection
	 * @throws BBBSystemException the BBB system exception
	 */
	public Connection pimConnection() throws BBBSystemException {

		Connection connection = null;
		try {
			if (isLoggingDebug()) {
				logDebug("Open Connection....");
			}

			DataSource dataSource = null;
			InitialContext initalContext = null;
			try {
				initalContext = new InitialContext();
				dataSource = (DataSource) initalContext.lookup(getPimConnectionName());
			} catch (NamingException e) {
				if (isLoggingError()) {

					logError(e);
				}
			}

			if (dataSource != null) {

				connection = dataSource.getConnection();
				
			}

		} catch (SQLException sqlex) {
			if (isLoggingError()) {

				logError(sqlex);
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,sqlex.getMessage(), sqlex);
		}

		return connection;
	}
	/**
	 * Send notification.
	 *
	 * @param typeOfFeed the type of feed
	 * @param feedId the feed id
	 * @param schemaName the schema name
	 * @param updateMisc the update misc
	 */
	public void sendNotificationCacheUpdateFailed() {
		String[] recipentEmail = null;
		final String emailIds = getCacheUpdateFailEmailIds();
		if(!StringUtils.isNotEmpty(emailIds)) {
			try {
				recipentEmail = emailIds.split(",");
				this.logDebug("sendFailedDynamicStoreProc :Sender :bbbfeeds@bedbath.com recipients: " + Arrays.toString(recipentEmail));
				this.getEmailSender().sendEmailMessage("bbbfeeds@bedbath.com", recipentEmail, getCacheUpdateFailMessageSubject(), getCacheUpdateFailMessage());
			} catch (Exception e) {
				this.logError("Error occured while attempting to send email for Dynamic Price Store procedure."+e.getMessage());
			}
		}
		logDebug("BBBDynamicPriceCacheWarmingListener Method : sendNotification end");

	}
	
	/**
	 * Gets the query max prod seq.
	 *
	 * @return the queryMaxProdSeq
	 */
	public final String getQueryMaxProdSeq() {
		return queryMaxProdSeq;
	}
	
	/**
	 * Sets the query max prod seq.
	 *
	 * @param queryMaxProdSeq the queryMaxProdSeq to set
	 */
	public final void setQueryMaxProdSeq(String queryMaxProdSeq) {
		this.queryMaxProdSeq = queryMaxProdSeq;
	}
	
	/**
	 * Gets the query max sku seq.
	 *
	 * @return the queryMaxSkuSeq
	 */
	public final String getQueryMaxSkuSeq() {
		return queryMaxSkuSeq;
	}
	
	/**
	 * Sets the query max sku seq.
	 *
	 * @param queryMaxSkuSeq the queryMaxSkuSeq to set
	 */
	public final void setQueryMaxSkuSeq(String queryMaxSkuSeq) {
		this.queryMaxSkuSeq = queryMaxSkuSeq;
	}
	
	/**
	 * Gets the query prod price strings.
	 *
	 * @return the queryProdPriceStrings
	 */
	public final String getQueryProdPriceStrings() {
		return queryProdPriceStrings;
	}
	
	/**
	 * Sets the query prod price strings.
	 *
	 * @param queryProdPriceStrings the queryProdPriceStrings to set
	 */
	public final void setQueryProdPriceStrings(String queryProdPriceStrings) {
		this.queryProdPriceStrings = queryProdPriceStrings;
	}
	
	/**
	 * Gets the query sku price flags.
	 *
	 * @return the querySkuPriceFlags
	 */
	public final String getQuerySkuPriceFlags() {
		return querySkuPriceFlags;
	}
	
	/**
	 * Sets the query sku price flags.
	 *
	 * @param querySkuPriceFlags the querySkuPriceFlags to set
	 */
	public final void setQuerySkuPriceFlags(String querySkuPriceFlags) {
		this.querySkuPriceFlags = querySkuPriceFlags;
	}
	
	/**
	 * Gets the query updated products.
	 *
	 * @return the queryUpdatedProducts
	 */
	public final String getQueryUpdatedProducts() {
		return queryUpdatedProducts;
	}
	
	/**
	 * Sets the query updated products.
	 *
	 * @param queryUpdatedProducts the queryUpdatedProducts to set
	 */
	public final void setQueryUpdatedProducts(String queryUpdatedProducts) {
		this.queryUpdatedProducts = queryUpdatedProducts;
	}
	
	/**
	 * Gets the query updated dynamic products.
	 *
	 * @return the queryUpdatedDynamicProducts
	 */
	public final String getQueryUpdatedDynamicProducts() {
		return queryUpdatedDynamicProducts;
	}
	
	/**
	 * Sets the query updated dynamic products.
	 *
	 * @param queryUpdatedDynamicProducts the queryUpdatedDynamicProducts to set
	 */
	public final void setQueryUpdatedDynamicProducts(
			String queryUpdatedDynamicProducts) {
		this.queryUpdatedDynamicProducts = queryUpdatedDynamicProducts;
	}
	
	/**
	 * Gets the query updated dynamic skus.
	 *
	 * @return the queryUpdatedDynamicSkus
	 */
	public final String getQueryUpdatedDynamicSkus() {
		return queryUpdatedDynamicSkus;
	}
	
	/**
	 * Sets the query updated dynamic skus.
	 *
	 * @param queryUpdatedDynamicSkus the queryUpdatedDynamicSkus to set
	 */
	public final void setQueryUpdatedDynamicSkus(String queryUpdatedDynamicSkus) {
		this.queryUpdatedDynamicSkus = queryUpdatedDynamicSkus;
	}
	
	/**
	 * Gets the query updated skus.
	 *
	 * @return the queryUpdatedSkus
	 */
	public final String getQueryUpdatedSkus() {
		return queryUpdatedSkus;
	}
	
	/**
	 * Sets the query updated skus.
	 *
	 * @param queryUpdatedSkus the queryUpdatedSkus to set
	 */
	public final void setQueryUpdatedSkus(String queryUpdatedSkus) {
		this.queryUpdatedSkus = queryUpdatedSkus;
	}
	
	/**
	 * Gets the query updated left over skus.
	 *
	 * @return the queryUpdatedLeftOverSkus
	 */
	public final String getQueryUpdatedLeftOverSkus() {
		return queryUpdatedLeftOverSkus;
	}
	
	/**
	 * Sets the query updated left over skus.
	 *
	 * @param queryUpdatedLeftOverSkus the queryUpdatedLeftOverSkus to set
	 */
	public final void setQueryUpdatedLeftOverSkus(String queryUpdatedLeftOverSkus) {
		this.queryUpdatedLeftOverSkus = queryUpdatedLeftOverSkus;
	}
	
	/**
	 * Gets the dynamic repository.
	 *
	 * @return the dynamicRepository
	 */
	public final Repository getDynamicRepository() {
		return dynamicRepository;
	}
	
	/**
	 * Sets the dynamic repository.
	 *
	 * @param dynamicRepository the dynamicRepository to set
	 */
	public final void setDynamicRepository(Repository dynamicRepository) {
		this.dynamicRepository = dynamicRepository;
	}
	
	/**
	 * Gets the product cache container.
	 *
	 * @return the productCacheContainer
	 */
	public final BBBDynamicPriceCacheContainer getProductCacheContainer() {
		return productCacheContainer;
	}
	
	/**
	 * Sets the product cache container.
	 *
	 * @param productCacheContainer the productCacheContainer to set
	 */
	public final void setProductCacheContainer(
			BBBDynamicPriceCacheContainer productCacheContainer) {
		this.productCacheContainer = productCacheContainer;
	}
	
	/**
	 * Gets the sku cache container.
	 *
	 * @return the skuCacheContainer
	 */
	public final BBBDynamicPriceCacheContainer getSkuCacheContainer() {
		return skuCacheContainer;
	}
	
	/**
	 * Sets the sku cache container.
	 *
	 * @param skuCacheContainer the skuCacheContainer to set
	 */
	public final void setSkuCacheContainer(
			BBBDynamicPriceCacheContainer skuCacheContainer) {
		this.skuCacheContainer = skuCacheContainer;
	}
	
	/**
	 * Gets the job target.
	 *
	 * @return the jobTarget
	 */
	public final String getJobTarget() {
		return jobTarget;
	}
	
	/**
	 * Sets the job target.
	 *
	 * @param jobTarget the jobTarget to set
	 */
	public final void setJobTarget(String jobTarget) {
		this.jobTarget = jobTarget;
	}
	
	/**
	 * Gets the data center.
	 *
	 * @return the dataCenter
	 */
	public final String getDataCenter() {
		return dataCenter;
	}
	
	/**
	 * Sets the data center.
	 *
	 * @param dataCenter the dataCenter to set
	 */
	public final void setDataCenter(String dataCenter) {
		this.dataCenter = dataCenter;
	}
	
	/**
	 * Gets the update cache rebuild time query.
	 *
	 * @return the updateCacheRebuildTimeQuery
	 */
	public final String getUpdateCacheRebuildTimeQuery() {
		return updateCacheRebuildTimeQuery;
	}
	
	/**
	 * Sets the update cache rebuild time query.
	 *
	 * @param updateCacheRebuildTimeQuery the updateCacheRebuildTimeQuery to set
	 */
	public final void setUpdateCacheRebuildTimeQuery(
			String updateCacheRebuildTimeQuery) {
		this.updateCacheRebuildTimeQuery = updateCacheRebuildTimeQuery;
	}

	/**
	 * Gets the pim connection name.
	 *
	 * @return the pimConnectionName
	 */
	public final String getPimConnectionName() {
		return pimConnectionName;
	}

	/**
	 * Sets the pim connection name.
	 *
	 * @param pimConnectionName the pimConnectionName to set
	 */
	public final void setPimConnectionName(String pimConnectionName) {
		this.pimConnectionName = pimConnectionName;
	}

	/**
	 * @return the coherenceBatchSize
	 */
	public final int getCoherenceBatchSize() {
		return coherenceBatchSize;
	}

	/**
	 * @param coherenceBatchSize the coherenceBatchSize to set
	 */
	public final void setCoherenceBatchSize(int coherenceBatchSize) {
		this.coherenceBatchSize = coherenceBatchSize;
	}

	/**
	 * @return the emailSender
	 */
	public final SMTPEmailSender getEmailSender() {
		return emailSender;
	}

	/**
	 * @param emailSender the emailSender to set
	 */
	public final void setEmailSender(SMTPEmailSender emailSender) {
		this.emailSender = emailSender;
	}

	/**
	 * @return the cacheUpdateFailEmailIds
	 */
	public final String getCacheUpdateFailEmailIds() {
		return cacheUpdateFailEmailIds;
	}

	/**
	 * @param cacheUpdateFailEmailIds the cacheUpdateFailEmailIds to set
	 */
	public final void setCacheUpdateFailEmailIds(String cacheUpdateFailEmailIds) {
		this.cacheUpdateFailEmailIds = cacheUpdateFailEmailIds;
	}

	/**
	 * @return the cacheUpdateFailMessage
	 */
	public final String getCacheUpdateFailMessage() {
		return cacheUpdateFailMessage;
	}

	/**
	 * @param cacheUpdateFailMessage the cacheUpdateFailMessage to set
	 */
	public final void setCacheUpdateFailMessage(String cacheUpdateFailMessage) {
		this.cacheUpdateFailMessage = cacheUpdateFailMessage;
	}

	/**
	 * @return the cacheUpdateFailMessageSubject
	 */
	public final String getCacheUpdateFailMessageSubject() {
		return cacheUpdateFailMessageSubject;
	}

	/**
	 * @param cacheUpdateFailMessageSubject the cacheUpdateFailMessageSubject to set
	 */
	public final void setCacheUpdateFailMessageSubject(
			String cacheUpdateFailMessageSubject) {
		this.cacheUpdateFailMessageSubject = cacheUpdateFailMessageSubject;
	}

	/**
	 * Gets the cache invalidator message source.
	 *
	 * @return the cacheInvalidatorMessageSource
	 */
	public  BBBCacheInvalidatorSource getCacheInvalidatorMessageSource() {
		return cacheInvalidatorMessageSource;
	}

	/**
	 * Sets the cache invalidator message source.
	 *
	 * @param cacheInvalidatorMessageSource the cacheInvalidatorMessageSource to set
	 */
	public  void setCacheInvalidatorMessageSource(
			BBBCacheInvalidatorSource cacheInvalidatorMessageSource) {
		this.cacheInvalidatorMessageSource = cacheInvalidatorMessageSource;
	}
	
	/**
	 * 
	 * @return fireMessageSkuLocalCacheBuild
	 */
	public boolean isFireMessageSkuLocalCacheBuild() {
		return fireMessageSkuLocalCacheBuild;
	}

	/**
	 * Set fireMessageSkuLocalCacheBuild
	 * @param fireMessageSkuLocalCacheBuild
	 */
	public void setFireMessageSkuLocalCacheBuild(
			boolean fireMessageSkuLocalCacheBuild) {
		this.fireMessageSkuLocalCacheBuild = fireMessageSkuLocalCacheBuild;
	}
	

	
}
