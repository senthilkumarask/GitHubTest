package com.bbb.scheduler;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * This schedular clears the caches described in the properties files
 * in cachesToBeCleared property
 * by comparing the time of the LAST_MODIFIED_DATE and SCHEDULAR_LAST_CLEAR_TIME
 * in BBB_DEPLOYMENT_POLLLING table in PIM schema.
 * if the SCHEDULAR_LAST_CLEAR_TIME < LAST_MODIFIED_DATE,
 * then the caches will be cleared,else not
 *
 * @author asi162
 *
 */

public class BBBCacheClearScheduler extends SingletonSchedulableService {

	private static final String JOB_NAME = "BBBCacheClearScheduler";
	private static final String JOB_DESCRIPTION="Scheduled job to clear the back caches after endeca baseling has been completed";
	private boolean schedulerEnabled;
	private String dataCenter;
	private String deploymentId;
	private List<String> cachesToBeCleared;
	private BBBObjectCache objectCache;
	private String datasourcePim;
	private boolean checkEndecaPollingTable;

	/**
	 * 
	 * @return status whether to compare the time stamp to clear the caches or not 
	 */
	public boolean isCheckEndecaPollingTable() {
		return checkEndecaPollingTable;
	}

	public void setCheckEndecaPollingTable(boolean checkEndecaPollingTable) {
		this.checkEndecaPollingTable = checkEndecaPollingTable;
	}
	
	/**
	 * this returns the name of the schema from properties file
	 * @return datasourcePim
	 */
	public String getDatasourcePim() {
		return datasourcePim;
	}
	
	public void setDatasourcePim(String datasourcePim) {
		this.datasourcePim = datasourcePim;
	}

	public List<String> getCachesToBeCleared() {
		return cachesToBeCleared;
	}

	public void setCachesToBeCleared(List<String> cachesToBeCleared) {
		this.cachesToBeCleared = cachesToBeCleared;
	}

	public BBBObjectCache getObjectCache() {
		return objectCache;
	}

	public void setObjectCache(BBBObjectCache objectCache) {
		this.objectCache = objectCache;
	}

	public boolean isSchedulerEnabled() {
		return schedulerEnabled;
	}

	public void setSchedulerEnabled(boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

	public String getDataCenter() {
		return dataCenter;
	}

	public void setDataCenter(String dataCenter) {
		this.dataCenter = dataCenter;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}



	@Override
	public void doScheduledTask(Scheduler paramScheduler,
			ScheduledJob paramScheduledJob) {
		if(isLoggingDebug()){
			logDebug("Start : Task cacheClear scheduled for " + getDeploymentId() + getDataCenter());
		}
		boolean cacheClear = clearCache();
		if (isLoggingDebug()) {
			logDebug("CacheClear is cleared - " + cacheClear);
			logDebug("End : Task cacheClear scheduled for " + getDeploymentId()
					+ getDataCenter());
		}
	}

	/**
	 * This method clears the cache by comparison of the dates and then updates
	 * the column with the current date and time
	 * 
	 * @return
	 */
	private boolean clearCache() {

		
		
		if(isSchedulerEnabled()){
			
			if(!isCheckEndecaPollingTable()){
				if(isLoggingDebug()){
					logDebug("Invalidating the cache without comparing the times in BBB_DEPLOYMENT_POLLING table.");
				}				
				invalidateCache();
				return true;
			}else{		
				
				if( getDeploymentId() ==null || getDataCenter() ==null || getDeploymentId().isEmpty() || getDataCenter().isEmpty())	{
					if(isLoggingDebug()){				
						logDebug("Deployment id or datacenter is not setup");
					}
					return false;
				}
				Connection connection = null ;
				Statement statement = null;
				Timestamp lastModifiedTime = null;
				Timestamp schedulerLastCacheClearTime = null;
				ResultSet resultSet = null;
				
				
				String sql = "select LAST_MODIFIED_DATE, SCHEDULAR_LAST_CLEAR_TIME from BBB_DEPLOYMENT_POLLING where id='"+ getDeploymentId() + "' and data_center ='"+ getDataCenter() + "'and polling_status='EndecaEnd'" ;
				
				if(isLoggingDebug()){
					logDebug("SQL Query is = "+ sql);
				}
				try {
					connection = openConnection();
					statement = connection.createStatement();
					resultSet = statement.executeQuery(sql);
					while(resultSet.next()){
						lastModifiedTime = resultSet.getTimestamp(BBBCoreConstants.LAST_MODIFIED_DATE_COLUMN);
						schedulerLastCacheClearTime =  resultSet.getTimestamp(BBBCoreConstants.SCHEDULAR_LAST_CLEAR_TIME);	
						break;
					}		
					
					if(isCacheClearanceRequired(lastModifiedTime,schedulerLastCacheClearTime)){
						invalidateCache();
						updateTimeOfSchedular(connection);
						return true;
					}
					
				} catch (BBBSystemException e) {
					logError("Error in opening connection",e);
				} catch (SQLException e) {
					logError("error in creating statement",e);
				}finally{
						try{
							if(resultSet!=null){
								resultSet.close();
							}
							if(statement!=null){
								statement.close();
							}
						}
						catch (SQLException e){
							logError("error in closing statement", e);
						}
						closeConnection(connection);
				}
			}
		}else{
			logDebug("Scheduler is disabaled");		
		}
		return false;

	}

	/**
	 * This method updates the column with the current date
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 * @throws SQLException 
	 */
	private void updateTimeOfSchedular(Connection connection) throws SQLException {

		String sql = "update BBB_DEPLOYMENT_POLLING set SCHEDULAR_LAST_CLEAR_TIME = ? where ID=? and DATA_CENTER = ? ";		
		PreparedStatement preparedStatement;
		Date date = new Date();		
		
		preparedStatement = connection.prepareStatement(sql);			
		preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
		preparedStatement.setString(2, getDeploymentId());
		preparedStatement.setString(3, getDataCenter());
		preparedStatement.executeUpdate();
					
		if(isLoggingDebug()){
			logDebug("Updated the column");
		}
		preparedStatement.close();
	}

	/**
	 * This method invalidated the caches mentioned in the property file
	 */
	private void invalidateCache() {
		if(isLoggingDebug()){
			logDebug("Invalidating Cache ....");
		}
		if(getCachesToBeCleared()!=null && !getCachesToBeCleared().isEmpty()){
			for(String cache: getCachesToBeCleared()){
				getObjectCache().clearCache(cache);
				if(isLoggingDebug()){
					logDebug(" --- " + cache + " has been cleared ");
				}
			}
			if(isLoggingDebug()){
				logDebug("Caches has been cleared");
			}
		}

	}

	/**
	 * This methods compares the lastModifiedTime and schedulerLastCacheClearTime 
	 * and returns true when schedulers last runtime is before Endeca's base lining last runtime. 
	 * @param lastModifiedTime
	 * @param schedulerLastCacheClearTime
	 * @return
	 */
	private boolean isCacheClearanceRequired(Timestamp lastModifiedTime,
			Timestamp schedulerLastCacheClearTime) {
		
		boolean clearCache = false;

		if(lastModifiedTime !=null && schedulerLastCacheClearTime!=null){
			if(lastModifiedTime.after(schedulerLastCacheClearTime)){
				clearCache = true;
			}
		}
		if(isLoggingDebug()){
			logDebug("lastModifiedTime is = " + lastModifiedTime + " & schedulerLastCacheClearTime = " + schedulerLastCacheClearTime +" clearCache="+clearCache);
		}	
		
		return clearCache;
	}

	/**
	 * 
	 * @return
	 * @throws BBBSystemException
	 */
	public Connection openConnection() throws BBBSystemException {

		Connection connection = null;
		try {
			if (isLoggingDebug()) {
				logDebug("Open Connection....");
			}

			DataSource dataSource = null;
			InitialContext initialContext = null;
			try {
				initialContext = new InitialContext();
				
				if(isLoggingDebug()){
					NamingEnumeration<NameClassPair> list = initialContext.list("");
					while (list.hasMore()) {
						  logDebug(list.next().getName());
					}
				}
				
				dataSource = (DataSource) initialContext.lookup(getDatasourcePim());
			} catch (NamingException e) {
				if (isLoggingError()) {
					logError("Error in getting PIM dataSource ",e);
				}
			}
			if (dataSource != null) {
				connection = dataSource.getConnection();
				
				if (isLoggingDebug()) {
					logDebug("Connection is created");
				}
			}
		} catch (SQLException sqlex) {
			if (isLoggingError()) {

				logError("SQL Exception" ,sqlex);
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.VER_TOOLS_SQL_EXCEPTION,
					sqlex.getMessage(), sqlex);
		}

		return connection;
	}
	
	/**
	 * 
	 * @param pConnection
	 */
	public void closeConnection(Connection pConnection) {
		boolean isClose = false;
		if (pConnection != null) {

			try {
				if (isConnectionOpen(pConnection)) {
					pConnection.close();
					isClose = true;
					if (isLoggingDebug()) {
						logDebug("Connection Closed Sucess....");
					}
				} else {

					if (isLoggingDebug()) {
						logDebug("Connection is already closed or not opened....");
					}
				}
			} catch (SQLException sqlex) {
				if (isLoggingError()) {
					logError(sqlex);
				}
			} finally {

				try {
					if (!isClose && isConnectionOpen(pConnection)) {
						pConnection.close();
					}
				} catch (SQLException e) {
					if (isLoggingError()) {
						logError(e);
					}
				}
			}
		}

	}

	private boolean isConnectionOpen(final Connection pConnection)
			throws SQLException {

		return (pConnection != null && !pConnection.isClosed());

	}

	public String getJobName(){
		return JOB_NAME;
	}

	/**
	 * Description of this job
	 * @return Job Description
	 */
	public String getJobDescription(){
		return JOB_DESCRIPTION;
	}

	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("BBBCacheClearScheduler", "", "", null, null, false));

		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}


}
