package com.bbb.certona.scheduler;

import java.util.Date;

import atg.core.util.StringUtils;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.bbb.certona.manager.CertonaFeedManager;

/**
 * Inventory specific Certona Feed scheduler. It processes the Certona feed
 * for Inventory
 * 
 * @author njai13
 *
 */
public class CertonaInventoryFeedScheduler extends CertonaScheduler {
	
	private boolean mSchedulerEnabled;
	/**
	 * Returns the whether the scheduler is enable or not
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 *  This variable signifies to enable or disable the scheduler in specific
	 * environment
	 * this value is set from the property file
	 * @param isShedulerEnabled the isShedulerEnabled to set
	 */
	public void setSchedulerEnabled(boolean pSchedulerEnabled) {
		this.mSchedulerEnabled = pSchedulerEnabled;
	}
	  

	@Override
	public void doScheduledTask(final Scheduler scheduler, final ScheduledJob job) {
		this.doScheduledTask();
	}

	
	public void doScheduledTask() {
		if (isSchedulerEnabled()) {
			if (isLoggingInfo()) {
				logInfo("Entering doScheduledTask with job name=["
						+ getJobName() + "]");
			}

			generateFeed();

			if (isLoggingInfo()) {
				logInfo("Exiting doScheduledTask with job name=["
						+ getJobName() + "]");
			}
		} else {
			if (isLoggingInfo()) {
				logInfo("Scheduler Task is Disabled");
			}
		}
	}

	/**
	 * Feed processing for Inventory
	 */
	public void generateFeed() {
		
		java.sql.Timestamp schedulerStartDate = new java.sql.Timestamp(new Date().getTime());
				
		final CertonaFeedManager feedManager = this.getFeedManager();
		
		if(feedManager != null){
			
			if(!StringUtils.isBlank(this.getTypeOfFeed())){
				
				if (isLoggingDebug()) {
					logDebug("FeedType : "+this.getTypeOfFeed() +"\n getIsFullDataFeed : "
							+this.isFullDataFeed()+"\n schedulerStartDate : "+schedulerStartDate);					
				}
				
				feedManager.getFeedDetails(this.getTypeOfFeed(), this.isFullDataFeed(), schedulerStartDate);	
				
			}else{
				if(isLoggingError()){
					logError("FeedType is not specified for "+this.getJobName());
				}
			}
			
		}else{
			if(isLoggingError()){
				logError("CertonaFeedManager not specified for "+this.getJobName());
			}
		}
	} 
}