package com.bbb.borderfree.cache;

import java.util.Date;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.framework.cache.BBBLocalCacheContainer;

public class BBBSkuAttributeCacheInvalidateScheduler extends SingletonSchedulableService{

	private boolean schedulerEnabled;
	private BBBLocalCacheContainer skuAttributeLocalCache;
	/**
	 * @param schedulerEnabled
	 *            the schedulerEnabled to set
	 */
	public void setSchedulerEnabled(final boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

	/**
	 * Returns the whether the scheduler is enable or not
	 * 
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return this.schedulerEnabled;
	}


	public BBBLocalCacheContainer getSkuAttributeLocalCache() {
		return skuAttributeLocalCache;
	}

	public void setSkuAttributeLocalCache(
			BBBLocalCacheContainer skuAttributeLocalCache) {
		this.skuAttributeLocalCache = skuAttributeLocalCache;
	}

	@Override
	public void doScheduledTask(Scheduler paramScheduler,
			ScheduledJob paramScheduledJob) {
		this.doScheduledTask();
		
	}

	private void doScheduledTask() {
		this.logInfo("Started Scheduler Job [" + this.getJobId() + ": "
				+ this.getJobName() + "]" + "at date = " + new Date());
		this.logInfo("Job Description :" + this.getJobDescription()
				+ " Scheduled at " + this.getSchedule());
		if (this.isSchedulerEnabled()) {
		
				if (null != getSkuAttributeLocalCache()) {
					if (isLoggingDebug()) {
						this.logDebug("Going to Invalidate Local cache  start : ");
					}
					getSkuAttributeLocalCache().clearCache();
					if (isLoggingDebug()) {
						this.logDebug("Going to Invalidate Local cache  End : ");
					}
				}
			
		this.logInfo("Border Free Invalidate Cache is Successfully Completed !!");
		}
		else{
			this.logInfo("Scheduler Task is Disabled");
		}
	}
}