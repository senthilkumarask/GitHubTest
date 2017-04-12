/**
 * 
 */
package com.bbb.commerce.porch.service;

import java.sql.Timestamp;
import java.util.Calendar;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.feeds.marketing.utils.BBBMarketingFeedTools;

import atg.repository.RepositoryItem;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * @author sm0191
 *
 */
public class PorchOrdersReportsTibcoScheduler extends SingletonSchedulableService {
	
	
	
	private boolean mSchedulerEnabled;
	
	private Scheduler mScheduler;

	/** The Schedule for this */
	private Schedule mSchedule;
	
	private PorchServiceManager porchServiceManager;
 
	private BBBMarketingFeedTools feedTools;
	
	private String dcPrefix;
	

	/* (non-Javadoc)
	 * @see atg.service.scheduler.SingletonSchedulableService#doScheduledTask(atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		
		
		if (isSchedulerEnabled()) {
				if (isLoggingInfo()) {
					logInfo("Scheduler started to perform task Porch Orders Reports to EDW");
				}
			final Calendar currentDate = Calendar.getInstance();
			final Long time = currentDate.getTimeInMillis();
			final Timestamp schedulerStartDate = new Timestamp(time);
			Timestamp lastModifiedDate = getFeedTools().getLastModifiedDate(dcPrefix+BBBCoreConstants.PORCH_ORDER_EDW_REPORT_SCHEDULER);
			if(null==lastModifiedDate){			
				lastModifiedDate=schedulerStartDate;
			}
			RepositoryItem[] porchOrders =getPorchServiceManager().getPorchOrders(lastModifiedDate,getDcPrefix());
			if(null!=porchOrders){
				getPorchServiceManager().sendPorchOrdersReportTibcco(porchOrders,getDcPrefix());		
			}
			getPorchServiceManager().updateScheduledRepository(schedulerStartDate , dcPrefix+BBBCoreConstants.PORCH_ORDER_EDW_REPORT_SCHEDULER, true);
			
			}
		else {
			if (isLoggingDebug()) {
				logDebug("Scheduler Task is Disabled");
			}
		}
	}

	

	
	
	/**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("Porch Orders Report generation Scheduler", "", "", null, null, false));
		
		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}
	
	
	/**
	 * @return the mSchedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}


	/**
	 * @param mSchedulerEnabled the mSchedulerEnabled to set
	 */
	public void setSchedulerEnabled(boolean mSchedulerEnabled) {
		this.mSchedulerEnabled = mSchedulerEnabled;
	}


	/**
	 * @return the mScheduler
	 */
	public Scheduler getScheduler() {
		return mScheduler;
	}


	/**
	 * @param mScheduler the mScheduler to set
	 */
	public void setScheduler(Scheduler mScheduler) {
		this.mScheduler = mScheduler;
	}


	/**
	 * @return the mSchedule
	 */
	public Schedule getSchedule() {
		return mSchedule;
	}


	/**
	 * @param mSchedule the mSchedule to set
	 */
	public void setSchedule(Schedule mSchedule) {
		this.mSchedule = mSchedule;
	}


	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}


	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}


	/**
	 * @return the feedTools
	 */
	public BBBMarketingFeedTools getFeedTools() {
		return feedTools;
	}


	/**
	 * @param feedTools the feedTools to set
	 */
	public void setFeedTools(BBBMarketingFeedTools feedTools) {
		this.feedTools = feedTools;
	}


	/**
	 * @return the dcPrefix
	 */
	public String getDcPrefix() {
		return dcPrefix;
	}


	/**
	 * @param dcPrefix the dcPrefix to set
	 */
	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

}
