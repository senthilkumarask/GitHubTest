package com.bbb.commerce.checklist.scheduler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBCacheInvalidatorSource;
import com.bbb.utils.BBBUtility;
  
/**
 * The Class InteractiveRepositoryCacheInvalidateScheduler.
 * Clears the repository cache for admin and interactive checklist repository  across JVMs based on a flag in BBB_PROCESS_STATE_GG table and updates the property in DB.
 */
public class InteractiveRepositoryCacheInvalidateScheduler extends SingletonSchedulableService {

	private boolean schedulerEnabled;
    private BBBCacheInvalidatorSource bbbCacheInvalidatorMessageSource;
    private DataSource miscDataSource;
    private String paramName;
	private String fetchProcessStatusQuery;
	private String updateProcessStatusQuery;
	private int updateProcessStatus;

	
	
	/**
	 * @return the miscDataSource
	 */
	public DataSource getMiscDataSource() {
		return miscDataSource;
	}

	/**
	 * @param miscDataSource the miscDataSource to set
	 */
	public void setMiscDataSource(DataSource miscDataSource) {
		this.miscDataSource = miscDataSource;
	}

	/**
	 * @return the paramName
	 */
	public String getParamName() {
		return paramName;
	}

	/**
	 * @param paramName the paramName to set
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	/**
	 * @return the fetchProcessStatusQuery
	 */
	public String getFetchProcessStatusQuery() {
		return fetchProcessStatusQuery;
	}

	/**
	 * @param fetchProcessStatusQuery the fetchProcessStatusQuery to set
	 */
	public void setFetchProcessStatusQuery(String fetchProcessStatusQuery) {
		this.fetchProcessStatusQuery = fetchProcessStatusQuery;
	}

	/**
	 * @return the updateProcessStatusQuery
	 */
	public String getUpdateProcessStatusQuery() {
		return updateProcessStatusQuery;
	}

	/**
	 * @param updateProcessStatusQuery the updateProcessStatusQuery to set
	 */
	public void setUpdateProcessStatusQuery(String updateProcessStatusQuery) {
		this.updateProcessStatusQuery = updateProcessStatusQuery;
	}

	/**
	 * @return the updateProcessStatus
	 */
	public int getUpdateProcessStatus() {
		return updateProcessStatus;
	}

	/**
	 * @param updateProcessStatus the updateProcessStatus to set
	 */
	public void setUpdateProcessStatus(int updateProcessStatus) {
		this.updateProcessStatus = updateProcessStatus;
	}

	@Override
	public void doScheduledTask(Scheduler paramScheduler,
			ScheduledJob paramScheduledJob) {
		if(isLoggingInfo()){
			logInfo("Start:Scheduler started to perform task with job name=["
					+ getJobName() + "]");
		}
		if(isSchedulerEnabled()){
				this.clearCache();
		}else{
			if(isLoggingInfo()){
				logInfo("Scheduler disabled hence not clearing the cache");
			}
		}
		if(isLoggingInfo()){
			logInfo("End started to perform task with job name=["
					+ getJobName() + "]");
		}
	}

	/** Clears the cache after checking the status flag in DB.
	 * @return
	 */
	public void clearCache() {
		
		if (isLoggingDebug()) {
			logDebug("clearInteractiveChecklistCaches Is enabled");
			long startTime = System.currentTimeMillis();
			logDebug("clearInteractiveChecklistCaches start at  :"
					+ startTime);
		}

		if (isClearCacheRequired() && null != getBbbCacheInvalidatorMessageSource()) {
			if (isLoggingDebug()) {
				this.logDebug("Going to Invalidate interactive checklist cache  start : ");
			}
			
			getBbbCacheInvalidatorMessageSource().fireInteractiveInvalidationMessage();
			getBbbCacheInvalidatorMessageSource().fireRegistryChecklistCacheDropletInvalidationMessage();
						
			if (isLoggingDebug()) {
				this.logDebug("Going to Invalidate interactive checklist cache  End : ");
			}

		}
		if (isLoggingDebug()) {
			long endTime = System.currentTimeMillis();
			logDebug("InvalidateInteractiveCheckListCacheScheduler end at  :" + endTime);
		}


}
	
	/**If the bbbProcessStatus is 1 then clear the cache and update the column BBB_PROCESS_STATUS as 0 based on the DC
	 * @return
	 */
	private boolean isClearCacheRequired() {
		
		if(isLoggingDebug()){
			logDebug("Entering Method InteractiveRepositoryCacheInvalidateScheduler.isClearCacheRequired()");
		}
		Connection connection = null ;
		Integer bbbProcessStatus = 0;
		ResultSet resultSet = null;
		PreparedStatement statement=null;
		PreparedStatement updateStatement=null;
		
		if(isLoggingDebug()){
			logDebug("SQL Query is = "+ getFetchProcessStatusQuery());
		}
		try {
			if(BBBUtility.isEmpty(getParamName()))	{
				if(isLoggingDebug()){				
					logDebug("Param Name for the Data Centre not configured");
				}
				return false;
			}
			connection = openConnection();
			statement = connection.prepareStatement(getFetchProcessStatusQuery());
			statement.setString(1, getParamName());
			resultSet = statement.executeQuery();
			while(resultSet.next()){
				bbbProcessStatus = resultSet.getInt(BBBCoreConstants.BBB_PROCESS_STATUS);
				break;
			}		
			
			if(bbbProcessStatus!=1){
				if(isLoggingDebug()){
					logDebug("BBB_PROCESS_STATUS is false hence not invalidating the caches. ");
				}				
				return false;
			}else{
				// updating the status of column
				updateStatement = connection.prepareStatement(getUpdateProcessStatusQuery());
				updateStatement.setInt(1,getUpdateProcessStatus());
				updateStatement.setString(2, getParamName());
				updateStatement.executeUpdate();
				if(isLoggingInfo()){
					logInfo("BBB_PROCESS_STATUS is 1 hence invalidating the caches ");
				}
			}
			
		} catch (BBBSystemException e) {
			logError("Error in opening connection : " + e.getMessage());
			if(isLoggingDebug()) {
				logError("Error in opening connection : ",e);
			}
		} catch (SQLException e) {
			logError("Error in creating statement : " + e.getMessage());
			if(isLoggingDebug()) {
				logError("Error in creating statement : " , e);
			}
		}finally{
				try{
					if(resultSet!=null){
						resultSet.close();
					}
					if(statement!=null){
						statement.close();
					}
					if(updateStatement!=null){
						updateStatement.close();
					}
				}
				catch (SQLException e){
					logError("Error in closing statement" + e.getMessage());
					if(isLoggingDebug()) {
						logError("Error in closing statement" , e);
					}
				}
				closeConnection(connection);
		}
		if(isLoggingDebug()){
			logDebug("Exiting Method InteractiveRepositoryCacheInvalidateScheduler.isClearCacheRequired()");
		}
		return true;
	}
	
	/**
	 * @param pConnection
	 */
	private void closeConnection(Connection pConnection) {
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

	/** Open connection for misc data source
	 * @return
	 * @throws BBBSystemException
	 */
	private Connection openConnection() throws BBBSystemException {

		Connection connection = null;
		try {
			if (isLoggingDebug()) {
				logDebug("Open Connection....");
			}

			connection = getMiscDataSource().getConnection();
				
				if (isLoggingDebug()) {
					logDebug("Connection is created");
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
    
	private boolean isConnectionOpen(final Connection pConnection)
			throws SQLException {

		return (pConnection != null && !pConnection.isClosed());

	}

	public boolean isSchedulerEnabled() {
		return schedulerEnabled;
	}

	public void setSchedulerEnabled(boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

	public BBBCacheInvalidatorSource getBbbCacheInvalidatorMessageSource() {
		return bbbCacheInvalidatorMessageSource;
	}
	
	public void setBbbCacheInvalidatorMessageSource(
			BBBCacheInvalidatorSource bbbCacheInvalidatorMessageSource) {
		this.bbbCacheInvalidatorMessageSource = bbbCacheInvalidatorMessageSource;
	}
}
