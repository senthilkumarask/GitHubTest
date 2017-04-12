package com.bbb.rest.stofu.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import atg.adapter.gsa.GSARepository;
import atg.repository.MutableRepository;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class BBBHeartbeatPurgeScheduler extends SingletonSchedulableService {

	private static final String HEARTBEAT_RETENTION_DAYS = "heartbeat_retention_days";
	private static final String GS_HEARTBEAT = "GS_Heartbeat";
	private static final int DEFAULT_RETENTION_DAYS = 30;
	private BBBCatalogTools catalogTools;
	private boolean mSchedulerEnabled = false;
	private String heartbeatDataPurgeQuery;

	public String getHeartbeatDataPurgeQuery() {
		return heartbeatDataPurgeQuery;
	}
	public void setHeartbeatDataPurgeQuery(String heartbeatDataPurgeQuery) {
		this.heartbeatDataPurgeQuery = heartbeatDataPurgeQuery;
	}

	/**
	 * @return the schedulerEnabled
	 */
	public final boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * @param pSchedulerEnabled
	 *            the schedulerEnabled to set
	 */
	public final void setSchedulerEnabled(boolean pSchedulerEnabled) {
		mSchedulerEnabled = pSchedulerEnabled;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	private MutableRepository heartbeatRepository;

	public MutableRepository getHeartbeatRepository() {
		return heartbeatRepository;
	}

	public void setHeartbeatRepository(MutableRepository heartbeatRepository) {
		this.heartbeatRepository = heartbeatRepository;
	}

	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		if(isLoggingDebug()){
			logDebug("BBBHeartbeatPurgeScheduler:doScheduledTask - Start");
		}
		try {
			this.doScheduledTask();
		} catch (BBBSystemException e) {			
			logError("System Exception occurred");		
		}
		if(isLoggingDebug()){
			logDebug("BBBHeartbeatPurgeScheduler:doScheduledTask - End");
		}
	}

	public void doScheduledTask() throws BBBSystemException {

		if (this.isSchedulerEnabled()) {

			int result = 0;
			long startTime = System.currentTimeMillis();
			int retention_days = DEFAULT_RETENTION_DAYS;
			if (isLoggingInfo()) {
				logInfo("Entry doScheduledTask");
			}
			Connection connection = null;
			Statement statement = null;

			try {
				connection = ((GSARepository) this.getHeartbeatRepository())
						.getDataSource().getConnection();
				if (connection != null) {
					retention_days = getRetentionDaysFromConfigKey();
					String heartbeatDataPurgeSql = this.getHeartbeatDataPurgeQuery() + retention_days;
					statement = connection.createStatement();
					result = statement.executeUpdate(heartbeatDataPurgeSql);
				}
			} catch (SQLException e) {
				logError(
						"SQL Exception occurred while deleting data from database",
						e);
				throw new BBBSystemException(
						"SQL Exception occurred while deleting data from database",
						e);
			} finally {
				try {
					if(statement!=null){
						statement.close();
					}
					if(connection!=null){connection.close();}
				} catch (SQLException e) {
					logError("SQL Exception occurred while deleting data from database", e);
				}
			}

			long endTime = System.currentTimeMillis();
			if (isLoggingInfo()) {
				logInfo("Total time took for the Data Purge job::" + (endTime - startTime)
						+ " milliseconds , " + result + " rows deleted");
				logInfo("Exit doScheduledTask");
			}
		}
	}

	private int getRetentionDaysFromConfigKey()
			throws BBBSystemException {
		int retention_days = DEFAULT_RETENTION_DAYS; 
		String strRetentionDaysValue = "" ; 
		try {
			if (!BBBUtility.isListEmpty(this.getCatalogTools()
					.getAllValuesForKey(GS_HEARTBEAT,
							HEARTBEAT_RETENTION_DAYS))) {
				strRetentionDaysValue = this
						.getCatalogTools()
						.getAllValuesForKey(GS_HEARTBEAT,
								HEARTBEAT_RETENTION_DAYS).get(0)
						.toString(); 
				retention_days = Integer.parseInt(strRetentionDaysValue);
			}
		} catch (NumberFormatException e) {
			logError(
					"NumberFormatException Exception occurred while converting retention days ["+strRetentionDaysValue+"] from string to integer",
					e);
			retention_days = DEFAULT_RETENTION_DAYS;
		} catch (BBBBusinessException e) {
			logError(
					"BBBBusinessException Exception occurred while fetching heartbeat_retention_days config key",
					e);
			retention_days = DEFAULT_RETENTION_DAYS;
			}
		return retention_days;
	}

}
