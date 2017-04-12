package com.bbb.cache.listener;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import atg.adapter.gsa.GSARepository;
import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;
import atg.deployment.server.Target;
import atg.nucleus.ServiceMap;
import atg.repository.Repository;
import atg.service.email.SMTPEmailSender;
import atg.service.jdbc.SwitchingDataSource;

import com.bbb.cache.BBBDynamicPriceCacheJob;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBCacheInvalidatorSource;
import com.bbb.framework.performance.BBBPerformanceMonitor;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving BBBDynamicPriceCacheWarming events.
 * The class that is interested in processing a BBBDynamicPriceCacheWarming
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addBBBDynamicPriceCacheWarmingListener<code> method. When
 * the BBBDynamicPriceCacheWarming event occurs, that object's appropriate
 * method is invoked.
 *
 * @see BBBDynamicPriceCacheWarmingEvent
 */
public class BBBDynamicPriceCacheWarmingListener extends BBBGenericService implements DeploymentEventListener{

	/** The dynamic repository. */
	private Repository dynamicRepository;
	
	/** The catalog repository. */
	private Repository catalogRepository;
	
	/** The udpate price query. */
	private String udpatePriceQuery;
	
	/** The hash map. */
	private ServiceMap hashMap;
	
	/** The item type list. */
	private List<String> itemTypeList;
	
	/** The repository list. */
	private List<String> repositoryList;
	
	/** The enable warming. */
	private boolean enableWarming;
	
	/** The cache invalidator message source. */
	private BBBCacheInvalidatorSource cacheInvalidatorMessageSource;
	
	/** The publishing repository. */
	private Repository publishingRepository;
	
	/** The dynamic price cache helper. */
	private BBBDynamicPriceCacheHelper dynamicPriceCacheHelper;
	
	/** The target for procedure. */
	private String targetForProcedure;
	
	/** The data source map. */
	private Map<String, String> dataSourceMap;
	
	/** The Constant SELECT_PRICING_DEPLOYMENT_DETAILS. */
	private static final String SELECT_PRICING_DEPLOYMENT_DETAILS = " Select FEED_ID from ECP_PRICING_FEED_MONITORING where FEED_STATUS ='PRODUCTION_IN_PROGRESS'";
	
	/** The Constant SELECT_DEPLOYMENT_DETAILS. */
	private static final String SELECT_DEPLOYMENT_DETAILS = " Select FEED_ID from ECP_FEED_MONITORING where FEED_STATUS ='PROD_DEP_IN_PROGRESS'";
	
	/** The Constant PRODUCT_CATALOG_REPOSITORY. */
	private static final String PRODUCT_CATALOG_REPOSITORY="/atg/commerce/catalog/ProductCatalog";
	
	/** The Constant STAGING_DATASOURCE. */
	private static final String STAGING_DATASOURCE="stagingDataSource";
	
	/** The Constant TARGET_STAGING. */
	private static final String TARGET_STAGING="bbb_stg";
	
	/** The Constant TARGET_PRODUCTION. */
	private static final String TARGET_PRODUCTION="bbb_prod";
	
	/** The start time. */
	long startTime;
	
	/** The manual feed id. */
	private String manualFeedId;
	
	/** The manual feed type. */
	private String manualFeedType;
	
	/** The manual update misc. */
	private boolean manualUpdateMisc;
	
	/** The manual data source. */
	private String manualDataSource;
	
	/** The dynamic price cache job. */
	private BBBDynamicPriceCacheJob dynamicPriceCacheJob;
	
	/** The catalog tools. */
	private BBBCatalogTools catalogTools;
	
	/** The update sp exec time query. */
	private String updateSPExecTimeQuery;
	
	/** The update cache rebuild time query. */
	private String updateCacheRebuildTimeQuery;
	
	/** The email ids. */
	private String emailIds;
	
	/** The email sender. */
	private SMTPEmailSender emailSender;
	
	private String priceFeedDeploymentQuery;
	
	private String pimFeedDeploymentQuery;

	
	
	/**
	 * Checks if is enable warming.
	 *
	 * @return true, if is enable warming
	 */
	public boolean isEnableWarming() {
		return enableWarming;
	}

	/**
	 * Sets the enable warming.
	 *
	 * @param enableWarming the new enable warming
	 */
	public void setEnableWarming(boolean enableWarming) {
		this.enableWarming = enableWarming;
	}

	/**
	 * Gets the udpate price query.
	 *
	 * @return the udpate price query
	 */
	public String getUdpatePriceQuery() {
		return udpatePriceQuery;
	}

	/**
	 * Sets the udpate price query.
	 *
	 * @param udpatePriceQuery the new udpate price query
	 */
	public void setUdpatePriceQuery(String udpatePriceQuery) {
		this.udpatePriceQuery = udpatePriceQuery;
	}


	/* (non-Javadoc)
	 * @see atg.deployment.common.event.DeploymentEventListener#deploymentEvent(atg.deployment.common.event.DeploymentEvent)
	 */
	@Override
	public void deploymentEvent(DeploymentEvent paramDeploymentEvent) {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","deploymentEvent");
		logInfo("BBBDynamicPriceCacheWarmingListener.deploymentEvent methods begins");
			if(enableStoreProcedureExecution(paramDeploymentEvent)){
				if (this.isEnableWarming()) {
						logDebug("Target for BBBDynamicPriceCacheWarmingListener execution:"+getTargetForProcedure());
						if (TARGET_PRODUCTION.equalsIgnoreCase(getTargetForProcedure())) {
							logInfo("Starting execution of  BBBDynamicPriceCacheWarmingListener for Production:");
								startTime = System.currentTimeMillis();
								logDebug(" Free Memory before processing BBBDynamicPriceCacheWarmingListener| deploymentEvent():"
										+ getMemoryUsed() + " Start time :" + startTime);
							if (paramDeploymentEvent.getNewState() == DeploymentEvent.ACTIVE_ACTIVATE) {
									BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","deploymentEvent:");
									logDebug("Starting BBBDynamicPriceCacheWarmingListener for deploymentEvent:"+paramDeploymentEvent.getNewState());
									executeStoreProcedure(paramDeploymentEvent,Boolean.parseBoolean(BBBCoreConstants.TRUE),getTargetForProcedure(),Boolean.parseBoolean(BBBCoreConstants.TRUE));
									logDebug("Ending BBBDynamicPriceCacheWarmingListener for deploymentEvent:"+paramDeploymentEvent.getNewState());
									BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","deploymentEvent");
							}
							if (paramDeploymentEvent.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE) {
									BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","deploymentEvent");
									logDebug("Starting BBBDynamicPriceCacheWarmingListener for deploymentEvent:"+paramDeploymentEvent.getNewState());
									executeStoreProcedure(paramDeploymentEvent,Boolean.parseBoolean(BBBCoreConstants.FALSE),getTargetForProcedure(),Boolean.parseBoolean(BBBCoreConstants.FALSE));
									logDebug("Ending BBBDynamicPriceCacheWarmingListener for deploymentEvent:"+paramDeploymentEvent.getNewState());
									BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","deploymentEvent");
							}
								logDebug("Ending execution of  BBBDynamicPriceCacheWarmingListener for Production:");
						} else if (TARGET_STAGING.equalsIgnoreCase(getTargetForProcedure())) {
							logDebug("Starting execution of  BBBDynamicPriceCacheWarmingListener for Staging:");
							startTime = System.currentTimeMillis();
							logDebug(" Free Memory before processing BBBDynamicPriceCacheWarmingListener| deploymentEvent():"
									+ getMemoryUsed() + " Start time :" + startTime);
							if (paramDeploymentEvent.getNewState() == DeploymentEvent.DONE_ACTIVATE) {
									BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","deploymentEvent");
									logDebug("Starting BBBDynamicPriceCacheWarmingListener for deploymentEvent:"+paramDeploymentEvent.getNewState());
									executeStoreProcedure(paramDeploymentEvent,Boolean.parseBoolean(BBBCoreConstants.TRUE),getTargetForProcedure(),Boolean.parseBoolean(BBBCoreConstants.FALSE));
									logDebug("Ending BBBDynamicPriceCacheWarmingListener for deploymentEvent:"+paramDeploymentEvent.getNewState());
									BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","deploymentEvent");
							}
						logDebug("Ending execution of  BBBDynamicPriceCacheWarmingListener for Staging:");
		
				}
			}	
		}
			BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","deploymentEvent");
	}
	
	/**
	 * Enable store procedure execution.
	 *
	 * @param deploymentEvent the deployment event
	 * @return true, if successful
	 */
	private boolean enableStoreProcedureExecution(DeploymentEvent deploymentEvent) {
	
		boolean enableStoreProcedureExecution=false;
		List<String> enableStoreProcedureExecutionList;
		try {
			enableStoreProcedureExecutionList = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.RUN_DYNAMIC_PRICE_SP);
		if(enableStoreProcedureExecutionList!=null && !enableStoreProcedureExecutionList.isEmpty()){
			enableStoreProcedureExecution=Boolean.parseBoolean(enableStoreProcedureExecutionList.get(0));
			}
		} catch (BBBSystemException | BBBBusinessException e) {
			logError(e.getMessage());
		}
		return enableStoreProcedureExecution;
	}

	/**
	 * Execute store procedure.
	 *
	 * @param paramDeploymentEvent the param deployment event
	 * @param updateMisc the update misc
	 * @param target the target
	 * @param syncCall the sync call
	 */
	private void executeStoreProcedure(DeploymentEvent paramDeploymentEvent,
			boolean updateMisc, final String target, boolean syncCall) {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","executeStoreProcedure");
		String[] feedDetails = new String[2];
		String dataSource = null;
		boolean isExecuteProcedure = false;
		Set<String> repositories = (Set<String>) paramDeploymentEvent.getAffectedItemTypes().keySet();
		Iterator<String> iterator = repositories.iterator();
		while (iterator.hasNext()) {
			String affectedRepository = iterator.next();
			String itemType = null;
			logDebug("BBBDynamicPriceCacheWarmingListener| affectedRepositoryName():"+ affectedRepository);
			if (affectedRepository != null&& this.getRepositoryList().contains(affectedRepository)) {
				logDebug("BBBDynamicPriceCacheWarmingListener| Fetching itemTypes now");
				HashSet<String> hashSetItemTypes = (HashSet<String>) paramDeploymentEvent.getAffectedItemTypes().get(affectedRepository);
				Iterator<String> hashItemItr = hashSetItemTypes.iterator();
				while (hashItemItr.hasNext()) { 
					itemType = (String) hashItemItr.next();
					
					logDebug("BBBDynamicPriceCacheWarmingListener| value of itemType:"+ itemType);
					if (this.getItemTypeList().contains(itemType)) {
						logDebug("BBBDynamicPriceCacheWarmingListener| affectedItemType is present in the list of items");
						isExecuteProcedure = true;
						Repository repository = (Repository) getHashMap().get(affectedRepository);
						if (target.equalsIgnoreCase(TARGET_PRODUCTION)&& ((GSARepository) repository).getDataSource() instanceof SwitchingDataSource) {
							dataSource = ((SwitchingDataSource) (((GSARepository) repository).getDataSource())).getNextDataSourceName();
							dataSource = getDataSourceMap().get(dataSource);
						} else {
							dataSource = getDataSourceMap().get(STAGING_DATASOURCE);
						}
						logDebug("Current Data Source: " + dataSource);
						feedDetails = fetchFeedDetails(affectedRepository,target);
						break;
					}
				}
			}
			if (isExecuteProcedure) {
				break;
			}
		}

		if (isExecuteProcedure) {
			final String feedTypeFinal = feedDetails[0];
			final String feedIdListFinal = feedDetails[1];
			final String dataSourceFinal = dataSource;
			final boolean updateMiscFinal = updateMisc;
			logDebug("Current Thread Name before execution: "
					+ Thread.currentThread().getName());
			if (!syncCall) {
				Runnable newThreadExecute = new Runnable() {
					@Override
					public void run() {
						logDebug("Current Thread Name while execution: "+ Thread.currentThread().getName());
						logInfo("Starting Execution of new thread for Dynamic Pricing Store Proc Execution with Asynchronous Call ");
						runStoreProcedure(feedTypeFinal, feedIdListFinal,updateMiscFinal, dataSourceFinal, target);
						logInfo("Ending Execution of new thread for Dynamic Pricing Store Proc Execution with Asynchronous Call");
					}
				};
				new Thread(newThreadExecute).start();
			} else {
				logDebug("Current Thread Name while execution: "+ Thread.currentThread().getName());
				logInfo("Starting Execution of Dynamic Pricing Store Proc Execution in synchronus call with with Synchronous Call");
				runStoreProcedure(feedTypeFinal, feedIdListFinal,updateMiscFinal, dataSourceFinal, target);
				logInfo("Exitting Execution of Dynamic Pricing Store Proc Execution in synchronus call with with Synchronous Call");

			}

		}
		BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","executeStoreProcedure");
	}
	
	/**
	 * Run store procedure.
	 *
	 * @param feedTypeFinal the feed type final
	 * @param feedIdListFinal the feed_ id_ list_ final
	 * @param updateMiscFinal the update misc final
	 * @param dataSourceFinal the data source final
	 * @param target the target
	 */
	private void runStoreProcedure(String feedTypeFinal,String feedIdListFinal, boolean updateMiscFinal,String dataSourceFinal, String target) {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","runStoreProcedure");
		Connection dbConnection = null;
		CallableStatement callableStatement = null;
		int isStoreProcedureExecuted = 1;
		try {
			synchronized (this) {
				dbConnection = ((GSARepository) getDynamicRepository())	.getConnection();
				boolean autoCommit = dbConnection.getAutoCommit();
				dbConnection.setAutoCommit(false);
				callableStatement = (CallableStatement) dbConnection.prepareCall(getUdpatePriceQuery());
				logInfo("Started update for Price");
				callableStatement.setString(1, feedIdListFinal);
				callableStatement.setString(2, feedTypeFinal);
				callableStatement.setString(3, dataSourceFinal);
				callableStatement.setBoolean(4, updateMiscFinal);
				callableStatement.registerOutParameter(5, Types.INTEGER);
				callableStatement.execute();
				isStoreProcedureExecuted = callableStatement.getInt(5);
				long endTime = System.currentTimeMillis();
				logDebug(" Free Memory after processing BBBDynamicPriceCacheWarmingListener| deploymentEvent():"+ getMemoryUsed()
						+ " Total time taken by spc to execute:"+ (endTime - startTime));
				logDebug("BBBDynamicPriceCacheWarmingListener.calling update Dynamic repository code...");
				logInfo("Completed update for price.");
				// callableStatement.close();
				dbConnection.commit();
				dbConnection.setAutoCommit(autoCommit);
			}

		} catch (SQLException e) {
			logError("BBBDynamicPriceCacheWarmingListener | SQL Exception caught while executing stored proc CALL_MAIN_PROC",e);
			BBBPerformanceMonitor.cancel("BBBDynamicPriceCacheWarmingListener","deploymentEvent");
		} finally {
			// close resources
			try {
				if (callableStatement != null) {
					callableStatement.close();
				}
				if (dbConnection != null) {
					((GSARepository) getDynamicRepository())
							.close(dbConnection);
				}
			} catch (SQLException e) {
				logError("BBBDynamicPriceCacheWarmingListener |  SQLException from deploymentEvent()",e);
			}
		}

		if (isStoreProcedureExecuted != 1) {
			updateSToreProcExecutionTime();
			Thread newThreadExecuteCache = new Thread() {
				@Override
				public void run() {
					boolean isCacheUpdated = getDynamicPriceCacheHelper().updateDynamicRepositoryCache();
					logInfo("Cache updated after Store Proc has been triggered: "+isCacheUpdated);
					if (isCacheUpdated) {
						getDynamicPriceCacheHelper().updateCacheLastExecutionTime();
						logInfo("Update Cache last execution time updated");
					}else{
						getDynamicPriceCacheHelper().sendNotificationCacheUpdateFailed();
					}
				}
			};
			newThreadExecuteCache.start();
		} else {
			logDebug("Store Proc exectuion failed. Sending SMTP notification.");
			sendNotification(feedTypeFinal, feedIdListFinal,	dataSourceFinal, updateMiscFinal);
		}
		BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","runStoreProcedure");

	}

	/**
	 * Update s tore proc execution time.
	 */
	private void updateSToreProcExecutionTime() {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","updateSToreProcExecutionTime");
		final long currentTime= new Date().getTime();
		PreparedStatement statement=null;
		Connection pimConnection=null;
		try {
			pimConnection=getDynamicPriceCacheHelper().pimConnection();
			if(pimConnection!=null){
			statement = pimConnection.prepareStatement(getUpdateSPExecTimeQuery());
			statement.setTimestamp(1, new Timestamp(currentTime));
			statement.setString(2, getDynamicPriceCacheHelper().getJobTarget());
			statement.setString(3, getDynamicPriceCacheHelper().getDataCenter());
			statement.executeQuery();
			pimConnection.commit();
			}
		} catch (BBBSystemException | SQLException e) {
			logError("Error while updating store Proc execution time in BBB_Deplyment_POLLING"+e.getMessage());
		}finally{
			try {
				if(statement!=null){
					statement.close();
				}
				if(null!=pimConnection){
					pimConnection.close();
				}
				} catch (SQLException e) {
				logError(e);
			}
		}
		logInfo("Updated Store Proc Execution Time");
		BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","updateSToreProcExecutionTime");
	}

	/**
	 * Fetch feed details.
	 *
	 * @param affectedRepository the affected repository
	 * @param target the target
	 * @return the string[]
	 */
	private String[] fetchFeedDetails(String affectedRepository, String target) {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","fetchFeedDetails");
		String[] feedDetails = new String[2];
		if (affectedRepository.equalsIgnoreCase(PRODUCT_CATALOG_REPOSITORY)) {
			feedDetails = getFeedIdDetails(getPimFeedDeploymentQuery(),BBBCoreConstants.PIM_FEED);
			
		} else {
			feedDetails = getFeedIdDetails(getPriceFeedDeploymentQuery(),BBBCoreConstants.PRICE_FEED);
		}
		logDebug("Current Feed feed_id: " + feedDetails[1] + "Feed type: "+ feedDetails[0]);
		BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","fetchFeedDetails");
		return feedDetails;
	}

	/**
	 * Gets the feed id details.
	 *
	 * @param query the query
	 * @param feedType the feed type
	 * @return the feed id details
	 */
	private String[] getFeedIdDetails(String query, String feedType) {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheWarmingListener","getFeedIdDetails");
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String[] feedDetails = new String[2];
		ArrayList<Long> feedId = new ArrayList<Long>();
		try {
			connection = getDynamicPriceCacheHelper().pimConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				feedId.add(resultSet.getLong(BBBCoreConstants.FEED_ID));
			}
			
		} catch (SQLException | BBBSystemException e) {
			logError("Error while fetching Feed_id from resultset: " + e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logError("Error while closing connections:" + e);

			}

		}
		feedDetails[0] = feedType;
		if(!feedId.isEmpty() && feedId.toString().length()>1){
			feedDetails[1]=feedId.toString().substring(1).replace("]", BBBCoreConstants.BLANK).trim().replace(BBBCoreConstants.SPACE, BBBCoreConstants.BLANK);
		}else{
			feedDetails[1]=BBBCoreConstants.BLANK;
		}
		logInfo("feedIDs===="+feedDetails[1]);	
			
		BBBPerformanceMonitor.end("BBBDynamicPriceCacheWarmingListener","getFeedIdDetails");
		return feedDetails;
	}

	
	/**
	 * Send notification.
	 *
	 * @param typeOfFeed the type of feed
	 * @param feedId the feed id
	 * @param schemaName the schema name
	 * @param updateMisc the update misc
	 */
	public void sendNotification(final String typeOfFeed, final String feedId, final String schemaName, final boolean updateMisc) {
		logDebug("BBBDynamicPriceCacheWarmingListener Method : sendNotification start");
		logDebug("Sending Store proc execution faliure message for typeOfFeed:"+typeOfFeed);
		String[] recipentEmail = null;
		final String emailIds = getEmailIds();
		if(!StringUtils.isNotEmpty(emailIds)) {
			try {
				final String subject = " Dynamic Price Store procedure failure on server "+this.getEmailSender().getSourceHostName();
				recipentEmail = emailIds.split(",");
				this.logDebug("sendFailedDynamicStoreProc :Sender :bbbfeeds@bedbath.com recipients: " + Arrays.toString(recipentEmail));
				String message = "Dynamic Price Store Procedure Exectution for "+typeOfFeed+"failed with following params:";
				message = message+" FeedType="+typeOfFeed+"\nFeed Id = "+feedId+"\n SchemaName = "+schemaName+"\n UpdateMisc = "+updateMisc;
				this.getEmailSender().sendEmailMessage("bbbfeeds@bedbath.com", recipentEmail, subject, message);
			} catch (Exception e) {
				this.logError("Error occured while attempting to send email for Dynamic Price Store procedure."+e.getMessage());
			}
		}
		logDebug("BBBDynamicPriceCacheWarmingListener Method : sendNotification end");

	}
	
	/**
	 * Trigger store procedure manually.
	 */
	public void triggerStoreProcedureManually(){
		
		runStoreProcedure(getManualFeedType(),getManualFeedId(),isManualUpdateMisc(),getManualDataSource(),getTargetForProcedure());
		
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
	 * Gets the item type list.
	 *
	 * @return the item type list
	 */
	public List<String> getItemTypeList() {
		return itemTypeList;
	}

	/**
	 * Sets the item type list.
	 *
	 * @param itemTypeList the new item type list
	 */
	public void setItemTypeList(List<String> itemTypeList) {
		this.itemTypeList = itemTypeList;
	}

	/**
	 * Gets the repository list.
	 *
	 * @return the repository list
	 */
	public List<String> getRepositoryList() {
		return repositoryList;
	}

	/**
	 * Sets the repository list.
	 *
	 * @param repositoryList the new repository list
	 */
	public void setRepositoryList(List<String> repositoryList) {
		this.repositoryList = repositoryList;
	}

	/**
	 * Gets the dynamic repository.
	 *
	 * @return the dynamic repository
	 */
	public Repository getDynamicRepository() {
		return dynamicRepository;
	}

	/**
	 * Sets the dynamic repository.
	 *
	 * @param dynamicRepository the new dynamic repository
	 */
	public void setDynamicRepository(Repository dynamicRepository) {
		this.dynamicRepository = dynamicRepository;
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
	 * Gets the publishing repository.
	 *
	 * @return the publishingRepository
	 */
	public final Repository getPublishingRepository() {
		return publishingRepository;
	}

	/**
	 * Sets the publishing repository.
	 *
	 * @param publishingRepository the publishingRepository to set
	 */
	public final void setPublishingRepository(Repository publishingRepository) {
		this.publishingRepository = publishingRepository;
	}

	/**
	 * Gets the catalog repository.
	 *
	 * @return the catalogRepository
	 */
	public final Repository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * Sets the catalog repository.
	 *
	 * @param catalogRepository the catalogRepository to set
	 */
	public final void setCatalogRepository(Repository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	
	/**
	 * Gets the hash map.
	 *
	 * @return the hashMap
	 */
	public final ServiceMap getHashMap() {
		return hashMap;
	}

	/**
	 * Sets the hash map.
	 *
	 * @param hashMap the hashMap to set
	 */
	public final void setHashMap(ServiceMap hashMap) {
		this.hashMap = hashMap;
	}

	/**
	 * Gets the dynamic price cache helper.
	 *
	 * @return the dynamicPriceCacheHelper
	 */
	public final BBBDynamicPriceCacheHelper getDynamicPriceCacheHelper() {
		return dynamicPriceCacheHelper;
	}

	/**
	 * Sets the dynamic price cache helper.
	 *
	 * @param dynamicPriceCacheHelper the dynamicPriceCacheHelper to set
	 */
	public final void setDynamicPriceCacheHelper(
			BBBDynamicPriceCacheHelper dynamicPriceCacheHelper) {
		this.dynamicPriceCacheHelper = dynamicPriceCacheHelper;
	}

	
	/**
	 * Gets the target for procedure.
	 *
	 * @return the targetForProcedure
	 */
	public final String getTargetForProcedure() {
		return targetForProcedure;
	}

	/**
	 * Sets the target for procedure.
	 *
	 * @param targetForProcedure the targetForProcedure to set
	 */
	public final void setTargetForProcedure(String targetForProcedure) {
		this.targetForProcedure = targetForProcedure;
	}

	/**
	 * Gets the data source map.
	 *
	 * @return the dataSourceMap
	 */
	public final Map<String, String> getDataSourceMap() {
		return dataSourceMap;
	}

	/**
	 * Sets the data source map.
	 *
	 * @param dataSourceMap the dataSourceMap to set
	 */
	public final void setDataSourceMap(Map<String, String> dataSourceMap) {
		this.dataSourceMap = dataSourceMap;
	}

	/**
	 * Gets the manual feed id.
	 *
	 * @return the manualFeedId
	 */
	public final String getManualFeedId() {
		return manualFeedId;
	}

	/**
	 * Sets the manual feed id.
	 *
	 * @param manualFeedId the manualFeedId to set
	 */
	public final void setManualFeedId(String manualFeedId) {
		this.manualFeedId = manualFeedId;
	}

	/**
	 * Gets the manual feed type.
	 *
	 * @return the manualFeedType
	 */
	public final String getManualFeedType() {
		return manualFeedType;
	}

	/**
	 * Sets the manual feed type.
	 *
	 * @param manualFeedType the manualFeedType to set
	 */
	public final void setManualFeedType(String manualFeedType) {
		this.manualFeedType = manualFeedType;
	}

	/**
	 * Checks if is manual update misc.
	 *
	 * @return the manualUpdateMisc
	 */
	public final boolean isManualUpdateMisc() {
		return manualUpdateMisc;
	}

	/**
	 * Sets the manual update misc.
	 *
	 * @param manualUpdateMisc the manualUpdateMisc to set
	 */
	public final void setManualUpdateMisc(boolean manualUpdateMisc) {
		this.manualUpdateMisc = manualUpdateMisc;
	}

	/**
	 * Gets the manual data source.
	 *
	 * @return the manualDataSource
	 */
	public final String getManualDataSource() {
		return manualDataSource;
	}

	/**
	 * Sets the manual data source.
	 *
	 * @param manualDataSource the manualDataSource to set
	 */
	public final void setManualDataSource(String manualDataSource) {
		this.manualDataSource = manualDataSource;
	}

	/**
	 * Gets the dynamic price cache job.
	 *
	 * @return the dynamicPriceCacheJob
	 */
	public final BBBDynamicPriceCacheJob getDynamicPriceCacheJob() {
		return dynamicPriceCacheJob;
	}

	/**
	 * Sets the dynamic price cache job.
	 *
	 * @param dynamicPriceCacheJob the dynamicPriceCacheJob to set
	 */
	public final void setDynamicPriceCacheJob(
			BBBDynamicPriceCacheJob dynamicPriceCacheJob) {
		this.dynamicPriceCacheJob = dynamicPriceCacheJob;
	}

	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * Gets the update sp exec time query.
	 *
	 * @return the updateSPExecTimeQuery
	 */
	public final String getUpdateSPExecTimeQuery() {
		return updateSPExecTimeQuery;
	}

	/**
	 * Sets the update sp exec time query.
	 *
	 * @param updateSPExecTimeQuery the updateSPExecTimeQuery to set
	 */
	public final void setUpdateSPExecTimeQuery(String updateSPExecTimeQuery) {
		this.updateSPExecTimeQuery = updateSPExecTimeQuery;
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
	 * Gets the email ids.
	 *
	 * @return the emailIds
	 */
	public final String getEmailIds() {
		return emailIds;
	}

	/**
	 * Sets the email ids.
	 *
	 * @param emailIds the emailIds to set
	 */
	public final void setEmailIds(String emailIds) {
		this.emailIds = emailIds;
	}

	/**
	 * Gets the email sender.
	 *
	 * @return the emailSender
	 */
	public final SMTPEmailSender getEmailSender() {
		return emailSender;
	}

	/**
	 * Sets the email sender.
	 *
	 * @param emailSender the emailSender to set
	 */
	public final void setEmailSender(SMTPEmailSender emailSender) {
		this.emailSender = emailSender;
	}

	public String getPriceFeedDeploymentQuery() {
		return priceFeedDeploymentQuery;
	}

	public void setPriceFeedDeploymentQuery(String priceFeedDeploymentQuery) {
		this.priceFeedDeploymentQuery = priceFeedDeploymentQuery;
	}

	public String getPimFeedDeploymentQuery() {
		return pimFeedDeploymentQuery;
	}

	public void setPimFeedDeploymentQuery(String pimFeedDeploymentQuery) {
		this.pimFeedDeploymentQuery = pimFeedDeploymentQuery;
	}


}
