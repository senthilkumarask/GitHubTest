package com.bbb.stofu.cache.scheduler;

import java.util.Date;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.stofu.cache.BBBGSCacheInvalidator;

public class BBBGSCacheInvalidatorScheduler extends SingletonSchedulableService {
	
	private BBBGSCacheInvalidator mBBBGSCacheInvalidator;
	private boolean mSchedulerEnabled;	
	
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}
	
	public void setSchedulerEnabled(boolean mSchedulerEnabled) {
		this.mSchedulerEnabled = mSchedulerEnabled;
	}
	
	public BBBGSCacheInvalidator getBBBGSCacheInvalidator() {
		return mBBBGSCacheInvalidator;
	}
	
	public void setBBBGSCacheInvalidator(
			BBBGSCacheInvalidator mBBBGSCacheInvalidator) {
		this.mBBBGSCacheInvalidator = mBBBGSCacheInvalidator;
	}
	
	@Override
	public void doScheduledTask(final Scheduler scheduler, final ScheduledJob job) {
		
		if (isLoggingDebug()) {
			logDebug("Started Scheduler Job [" + getJobId() + ": "
					+ getJobName() + "]" + "at date = " + new Date());
			logDebug("Job Description :" + this.getJobDescription()
					+ " Scheduled at " + this.getSchedule());
		}
		if (isSchedulerEnabled()) {
	      if (isLoggingInfo()) {
	        logInfo("Scheduler started to perform task with job name=[" + job.getJobName() + "]");
	      }
	     
	      getBBBGSCacheInvalidator().invalidateEndecaCache();
	    } else {
	      if (isLoggingDebug()) {
	        logDebug("Scheduler Task is Disabled");
	      }
	    }
		
		
	}
	
	public void manualBBBGSCacheInvalidatorExecution()
	{
		getBBBGSCacheInvalidator().invalidateEndecaCache();
	}
	
	

}
