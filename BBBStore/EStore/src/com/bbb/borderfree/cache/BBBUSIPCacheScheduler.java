package com.bbb.borderfree.cache;

import atg.nucleus.GenericService;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

/**
 * This class is invoked to build US ip cache.
 * 
 * @author apan25
 *
 */
public class BBBUSIPCacheScheduler extends GenericService implements Schedulable {
	
   /**
	 * enable scheduler property
	 */
   private boolean schedulerEnabled;
	
   private BBBUSIPStartUpCache ipAddressCache;
   
   private Scheduler scheduler;
   
   private Schedule schedule;
	
	/**
	 * This method is invoked repeatedly (by ATG Scheduler Service) at 
	 * the preset schedule to perform the following tasks:
	 * - Clear IP cache and rebuild
	 * - 
	 * @see atg.service.scheduler.Schedulable#doScheduledTask(
	 * 	atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
   @Override
	public void performScheduledTask(Scheduler paramScheduler,
			ScheduledJob paramScheduledJob) {
	    logInfo("Entry BBBUSIPCacheScheduler paramScheduler");
		if(isSchedulerEnabled()){
			    logInfo("Is US Ip cache scheduler enabled: " + isSchedulerEnabled());
		        getIpAddressCache().doRebuildIPAddressCache();
		}
		logInfo("Exit BBBUSIPCacheScheduler paramScheduler");
	}
   
    /**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
			logDebug("Entry BBBUSIPCacheScheduler executeDoScheduledTask");
			
			performScheduledTask(null,new ScheduledJob("BBBUSIPCacheScheduler", 
					"", "", null, null, false));
		
			logDebug("Exit BBBUSIPCacheScheduler executeDoScheduledTask");
	}
	
	/**
	 * @return the schedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return schedulerEnabled;
	}

	/**
	 * @param enable the schedulerEnabled to set
	 */
	public void setSchedulerEnabled(final boolean enable) {
		this.schedulerEnabled = enable;
	}
	
	/**
	 * @return the BBBUSIPStartUpCache
	 */
	public BBBUSIPStartUpCache getIpAddressCache() {
		return ipAddressCache;
	}

	/**
	 * @param ipAddressCache the ipAddressCache to set
	 */
	public void setIpAddressCache(final BBBUSIPStartUpCache ipAddressCache) {
		this.ipAddressCache = ipAddressCache;
	}
	
	/**
	 * @return the scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * @param scheduler the scheduler to set
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	/**
	 * @return the schedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	
}