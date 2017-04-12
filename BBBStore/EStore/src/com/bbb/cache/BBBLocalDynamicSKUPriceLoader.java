package com.bbb.cache;

import atg.nucleus.GenericService;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

/**
 * This class is invoked to build local Dynamic Price cache for SKUs 
 * which are populated in DynamicRepository's SKU Item descriptor.
 * 
 * The scheduler to load the dynamic cache after every 30 mins as the DB/Repository
 *  can change by the PIM/Price feeds  
 * 
 * @author ikhan2
 *
 */
public class BBBLocalDynamicSKUPriceLoader extends GenericService implements Schedulable {
	
   /**
	 * enable scheduler property
	 */
   private boolean schedulerEnabled;
	
   private BBBLocalDynamicPriceSKUCache localSKUDynamicPriceCache;
   
   private Scheduler scheduler;
   
   private Schedule schedule;
	
	/**
	 * This method is invoked repeatedly (by ATG Scheduler Service) at 
	 * the preset schedule to perform the following tasks:
	 * - Clear Cache and Rebuild Again
	 * - 
	 * @see atg.service.scheduler.Schedulable#doScheduledTask(
	 * 	atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
   @Override
	public void performScheduledTask(Scheduler paramScheduler,
			ScheduledJob paramScheduledJob) {
	    logInfo("Entry BBBLocalDynamicSKUPriceLoader paramScheduler");
		if(isSchedulerEnabled()){
		    logInfo("BBBLocalDynamicSKUPriceLoader scheduler enabled: " + isSchedulerEnabled());
		    getLocalSKUDynamicPriceCache().doRebuildLocalDynamicPriceSKUCache();
		}
		logInfo("Exit BBBLocalDynamicSKUPriceLoader paramScheduler");
	}
   
    /**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
			logDebug("Entry BBBLocalDynamicSKUPriceLoader executeDoScheduledTask");
			
			performScheduledTask(null,new ScheduledJob("BBBLocalDynamicSKUPriceLoader", 
					"", "", null, null, false));
		
			logDebug("Exit BBBLocalDynamicSKUPriceLoader executeDoScheduledTask");
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
	 * @return the localSKUDynamicPriceCache
	 */
	public BBBLocalDynamicPriceSKUCache getLocalSKUDynamicPriceCache() {
		return localSKUDynamicPriceCache;
	}

	/**
	 * @param localSKUDynamicPriceCache the localSKUDynamicPriceCache to set
	 */
	public void setLocalSKUDynamicPriceCache(final BBBLocalDynamicPriceSKUCache pLocalSKUDynamicPriceCache) {
		this.localSKUDynamicPriceCache = pLocalSKUDynamicPriceCache;
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