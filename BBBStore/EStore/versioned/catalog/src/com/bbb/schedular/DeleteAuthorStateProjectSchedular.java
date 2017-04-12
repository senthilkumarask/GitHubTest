package com.bbb.schedular;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.utils.BBBStringUtils;
/**
 * 
 * @author njai13
 *
 */
public class DeleteAuthorStateProjectSchedular extends SingletonSchedulableService {

	private  DataSource publishing;
	private boolean enableScheduler;
	/**
	 * @return the publishing
	 */
	public DataSource getPublishing() {
		return publishing;
	}
	/**
	 * @param pPublishing the publishing to set
	 */
	public void setPublishing(DataSource pPublishing) {
		publishing = pPublishing;
	}

	/**
	 * @return the enableScheduler
	 */
	public boolean isEnableScheduler() {
		return enableScheduler;
	}
	/**
	 * @param enableScheduler the enableScheduler to set
	 */
	public void setEnableScheduler(boolean enableScheduler) {
		this.enableScheduler = enableScheduler;
	}
	

	@Override
	public void doScheduledTask(Scheduler scheduler,
			ScheduledJob scheduledjob) {
		if(this.enableScheduler){
			if (isLoggingDebug()) {
				logDebug("Scheduler started to perform task at "+new Date());
			}
			CallableStatement cstmt = null;
			Connection pConnection =null;
			try {
				pConnection = openConnection();
				cstmt = pConnection.prepareCall("{call DELETE_AUTHOR_PROJECTS()}");
				cstmt.execute();
			} catch (SQLException e){
				if(isLoggingError()){
					logError(BBBStringUtils.stack2string(e));
				}
			} 
			catch (Exception e){
				if(isLoggingError()){
					logError(BBBStringUtils.stack2string(e));

				}
			}finally {
				try {
					if(cstmt!=null){
						cstmt.close();
					}
				} catch (SQLException e) {
					if(isLoggingError()){
						logError(BBBStringUtils.stack2string(e));

					}
				}

				try {
					if(null!=pConnection){
						pConnection.close();
					}
				} catch (SQLException e) {
					if(isLoggingError()){
						logError(BBBStringUtils.stack2string(e));

					}
				}
			}
		}
		else if (isLoggingDebug()) {
			logDebug("DeleteAuthorStateProjectSchedular Schedular" +
					" not able to  start as its disabled. change schedulerEnabled property to true!! ");
		}

	}


	public  Connection openConnection() throws Exception {

		Connection conn = null;
		try {
			conn = publishing.getConnection();
		} catch (SQLException e) {

			throw e;
		}
		return conn;
	}
	
	/**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("DeleteAuthorStateProjectSchedular", "", "", null, null, false));
		
		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}
}
