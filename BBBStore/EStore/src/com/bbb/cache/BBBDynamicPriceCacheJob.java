package com.bbb.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.bbb.cache.listener.BBBDynamicPriceCacheHelper;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;


/**
 * The Class BBBDynamicPriceCacheJob.
 */
public class BBBDynamicPriceCacheJob extends SingletonSchedulableService {

	/** The Constant LAST_DYNAMIC_STORE_PROC_EXECUTION_TIME. */
	private static final String LAST_DYNAMIC_STORE_PROC_EXECUTION_TIME = "Last_modified_date";

	/** The Constant LAST_DYNAMIC_CACHE_REBUILD_TIME. */
	private static final String LAST_DYNAMIC_CACHE_REBUILD_TIME = "schedular_last_clear_time";

	/** The Constant LAST_DYNAMIC_STORE_PROC_DATA_CENTER. */
	private static final String LAST_DYNAMIC_STORE_PROC_DATA_CENTER = "data_center";

	/** The dynamic price cache helper. */
	private BBBDynamicPriceCacheHelper dynamicPriceCacheHelper;

	/** The last store proc execution query. */
	private String lastStoreProcExecutionQuery;

	/** The last cache rebuild query. */
	private String lastCacheRebuildQuery;

	/** The update cache rebuild time query. */
	private String updateCacheRebuildTimeQuery;

	/** The scheduler enabled. */
	private boolean schedulerEnabled;

	/** The update store proc exec time query. */
	private String updateStoreProcExecTimeQuery;
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.service.scheduler.SingletonSchedulableService#doScheduledTask(atg
	 * .service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
	@Override
	public void doScheduledTask(Scheduler paramScheduler,ScheduledJob paramScheduledJob) {
		if (isLoggingDebug()) {
			logDebug("Start : Task cache update scheduled for "	+ getDynamicPriceCacheHelper().getDataCenter() + "," + getDynamicPriceCacheHelper().getJobTarget());
		}
		if (isSchedulerEnabled()) {
			this.dynamicPriceCacheUpdate();
		}
		if (isLoggingDebug()) {
			logDebug("End : Task cache update scheduled for " + getDynamicPriceCacheHelper().getDataCenter()+ "," + getDynamicPriceCacheHelper().getJobTarget());
		}

	}

	/**
	 * Dynamic price cache update.
	 */
	public void dynamicPriceCacheUpdate() {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheJob","dynamicPriceCacheUpdate");
		if (isLoggingDebug()) {
			logDebug("START: BBB BBBDynamicPriceCache Job to cache Dynmaic Repository");
		}
		Connection connection = null;
		try {
			connection = getDynamicPriceCacheHelper().pimConnection();
			if (connection != null) {
				if (!isUpdatedCache(connection)) {
					logInfo("BBB BBBDynamicPriceCache Job is going to update dynamic cache");
					/* updating the Dynamic repository cache */
					boolean cacheUpdatedSuccessfully=getDynamicPriceCacheHelper().updateDynamicRepositoryCache();
					if(cacheUpdatedSuccessfully){
						/* updating the schedular_last_clear_time after updating the cache*/
						getDynamicPriceCacheHelper().updateCacheLastExecutionTime();
					}else{
						getDynamicPriceCacheHelper().sendNotificationCacheUpdateFailed();
					}
				}
			}
		} catch (BBBSystemException e) {
			logError("Error occured while updating Dynmaic Price cache time."+ e.getMessage(),e);
		} finally {
			try {
				if(connection != null){
				 connection.close();	
				}				
			} catch (SQLException e) {
				logError("Sql Exception while closing PIM connections."+ e.getMessage(),e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("End : BBB BBBDynamicPriceCache Job to cache Dynmaic Repository ");
		}
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheJob","dynamicPriceCacheUpdate");
	}

	/**
	 * Checks if is updated cache.
	 * 
	 * @param connection
	 *            the connection
	 * @return true, if is updated cache
	 */
	private boolean isUpdatedCache(Connection connection) {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheJob","isUpdatedCache");
		Date lastStoreProcExecutionDate = null;
		Date lastCacheUpdateDate = null;
		if (isLoggingDebug()) {
			logDebug("SQL Query is = " + getLastStoreProcExecutionQuery()+ " Values passed to query:" + getDynamicPriceCacheHelper().getJobTarget() + ";"+ getDynamicPriceCacheHelper().getDataCenter());
		}
		/*getting the latest Store Proc Exectuion time*/
		lastStoreProcExecutionDate = getQueryResult(getLastStoreProcExecutionQuery(), connection,LAST_DYNAMIC_STORE_PROC_EXECUTION_TIME);
		/*getting the data center for latest Store Proc Exectuion time*/
		String dataCenterForLastStoreProcExec=getQueryResultForDataCenter(getLastStoreProcExecutionQuery(), connection,LAST_DYNAMIC_STORE_PROC_DATA_CENTER);
		/*getting the latest cache rebuild time*/
		lastCacheUpdateDate = getQueryResult(getLastCacheRebuildQuery(),connection, LAST_DYNAMIC_CACHE_REBUILD_TIME);
		BBBPerformanceMonitor.end("BBBDynamicPriceCacheJob","isUpdatedCache");
		if(!dataCenterForLastStoreProcExec.equalsIgnoreCase(getDynamicPriceCacheHelper().getDataCenter())){
			if (lastStoreProcExecutionDate.after(lastCacheUpdateDate)) {
			
				logInfo("Dynamic Price cache to be updated");
			
				return false;
			} else {
			
				logInfo("Dynamic Price cache already updated");
			
				return true;
		}}else{
			logInfo("Dynamic Price cache already updated");
			
			return true;
		}
	}

	/**
	 * Gets the query result for data center.
	 *
	 * @param query the query
	 * @param connection the connection
	 * @param resultType the result type
	 * @return the query result for data center
	 */
	private String getQueryResultForDataCenter(String query,Connection connection, String resultType) {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheJob","getQueryResultForDataCenter");
		logDebug("getting Query result for: " + resultType + "with query:"+ query);
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String dataCenterForStoreProc = null;
		try {
			statement = connection.prepareStatement(query);
			statement.setString(1, getDynamicPriceCacheHelper().getJobTarget());
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				dataCenterForStoreProc = resultSet.getString(resultType);
				break;
			}
		} catch (SQLException e) {
			logError("Error while fetching Query result" + e.getMessage());
		} finally {
			try {
				if (null != resultSet) {
					resultSet.close();
				}
				if (null != statement) {
					statement.close();
				}
			} catch (SQLException e) {
				logError("Sql Exception while closing Statement"+ e.getMessage());
			}
		}
		logDebug("Query result for: " + resultType + "with query:" + query + "is =" + dataCenterForStoreProc);
		BBBPerformanceMonitor.end("BBBDynamicPriceCacheJob","getQueryResultForDataCenter");
		return dataCenterForStoreProc;
	}

	/**
	 * Gets the query result.
	 * 
	 * @param query
	 *            the query
	 * @param connection
	 *            the connection
	 * @param resultType
	 *            the result type
	 * @return the query result
	 */
	private Date getQueryResult(String query, Connection connection,String resultType) {
		BBBPerformanceMonitor.start("BBBDynamicPriceCacheJob","getQueryResult");
		logDebug("getting Query result for: "+resultType+"with query:"+query);
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Date queryResultDate = null;
		try {
			statement = connection.prepareStatement(query);
			if (resultType.equalsIgnoreCase(LAST_DYNAMIC_CACHE_REBUILD_TIME)) {
				statement.setString(1, getDynamicPriceCacheHelper().getJobTarget());
				statement.setString(2, getDynamicPriceCacheHelper().getDataCenter());
			} else {
				statement.setString(1, getDynamicPriceCacheHelper().getJobTarget());
			}
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				queryResultDate = resultSet.getTimestamp(resultType);
				break;
			}
		} catch (SQLException e) {
			logError("Error while fetching Query result"+e.getMessage());
		} finally {
			try {
				if (null != resultSet) {
					resultSet.close();
				}
				if (null != statement) {
					statement.close();
				}
			} catch (SQLException e) {
				logError("Sql Exception while closing Statement"+e.getMessage());
			}
		}
		logDebug("Query result for: "+resultType+"with query:"+query+"is ="+queryResultDate);
		BBBPerformanceMonitor.end("BBBDynamicPriceCacheJob","getQueryResult");
		return queryResultDate;
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
	 * @param dynamicPriceCacheHelper
	 *            the dynamicPriceCacheHelper to set
	 */
	public final void setDynamicPriceCacheHelper(BBBDynamicPriceCacheHelper dynamicPriceCacheHelper) {
		this.dynamicPriceCacheHelper = dynamicPriceCacheHelper;
	}

	

	/**
	 * 
	 * 
	 * /**.
	 * 
	 * @return the updateCacheRebuildTimeQuery
	 */
	public final String getUpdateCacheRebuildTimeQuery() {
		return updateCacheRebuildTimeQuery;
	}

	/**
	 * Sets the update cache rebuild time query.
	 * 
	 * @param updateCacheRebuildTimeQuery
	 *            the updateCacheRebuildTimeQuery to set
	 */
	public final void setUpdateCacheRebuildTimeQuery(String updateCacheRebuildTimeQuery) {
		this.updateCacheRebuildTimeQuery = updateCacheRebuildTimeQuery;
	}

	/**
	 * Checks if is scheduler enabled.
	 * 
	 * @return the schedulerEnabled
	 */
	public final boolean isSchedulerEnabled() {
		return schedulerEnabled;
	}

	/**
	 * Sets the scheduler enabled.
	 * 
	 * @param schedulerEnabled
	 *            the schedulerEnabled to set
	 */
	public final void setSchedulerEnabled(boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

	
	/**
	 * Gets the update store proc exec time query.
	 * 
	 * @return the updateStoreProcExecTimeQuery
	 */
	public final String getUpdateStoreProcExecTimeQuery() {
		return updateStoreProcExecTimeQuery;
	}

	/**
	 * Sets the update store proc exec time query.
	 * 
	 * @param updateStoreProcExecTimeQuery
	 *            the updateStoreProcExecTimeQuery to set
	 */
	public final void setUpdateStoreProcExecTimeQuery(String updateStoreProcExecTimeQuery) {
		this.updateStoreProcExecTimeQuery = updateStoreProcExecTimeQuery;
	}

	/**
	 * Gets the last cache rebuild query.
	 * 
	 * @return the lastCacheRebuildQuery
	 */
	public final String getLastCacheRebuildQuery() {
		return lastCacheRebuildQuery;
	}

	/**
	 * Sets the last cache rebuild query.
	 * 
	 * @param lastCacheRebuildQuery
	 *            the lastCacheRebuildQuery to set
	 */
	public final void setLastCacheRebuildQuery(String lastCacheRebuildQuery) {
		this.lastCacheRebuildQuery = lastCacheRebuildQuery;
	}

	/**
	 * Gets the last store proc execution query.
	 *
	 * @return the lastStoreProcExecutionQuery
	 */
	public final String getLastStoreProcExecutionQuery() {
		return lastStoreProcExecutionQuery;
	}

	/**
	 * Sets the last store proc execution query.
	 *
	 * @param lastStoreProcExecutionQuery the lastStoreProcExecutionQuery to set
	 */
	public final void setLastStoreProcExecutionQuery(
			String lastStoreProcExecutionQuery) {
		this.lastStoreProcExecutionQuery = lastStoreProcExecutionQuery;
	}

}
