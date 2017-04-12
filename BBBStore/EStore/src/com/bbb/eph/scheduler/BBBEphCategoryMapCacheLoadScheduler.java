package com.bbb.eph.scheduler;

import atg.nucleus.ServiceException;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.eph.tools.BBBEphCategoryMapCacheLoadTools;

/**
 * This class contain methods to start/stop scheduler which is used to populate 
 * coherence cache with search keywords and EPH/category mapping. 
 * It will be running once in a day.
 * 
 *  
 */
public class BBBEphCategoryMapCacheLoadScheduler extends SingletonSchedulableService {

	// Scheduler enable/disable flag
	private boolean schedulerEnabled;
	
	private Scheduler scheduler;
	private Schedule schedule;
	private BBBEphCategoryMapCacheLoadTools ephCategoryMapCacheLoadTools;	
	int jobId;
   
    @Override
    public void doStartService() throws ServiceException {
    	super.doStartService();
		if (isSchedulerEnabled()) {
			if(isLoggingDebug()) {
				logDebug("Invoking BBBEphCategoryMapCacheLoadScheduler().loadEPHCategoryMapCache on start up.");
			}
			// call method of tools to load coherence cache
			getEphCategoryMapCacheLoadTools().loadEPHCategoryMapCache();
		}
    }

	@Override
    public void doStopService() throws ServiceException {
    	getEphCategoryMapCacheLoadTools().doStopService();
    	super.doStopService();
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
	
		
	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		this.doScheduledTask();
	}
	
	public void doScheduledTask() {
		if(isLoggingDebug()){
			logDebug("BBBEphCategoryMapCacheLoadScheduler.doScheduledTask() - start");
		}

		if (isSchedulerEnabled()) {
			// call method of tools to load coherence cache
			getEphCategoryMapCacheLoadTools().loadEPHCategoryMapCache();
		}

		if(isLoggingDebug()){
			logDebug("BBBEphCategoryMapCacheLoadScheduler.doScheduledTask() - end");
		}
	}
	
	/**
	 * @return the ephCategoryMapCacheLoadTools
	 */
	public BBBEphCategoryMapCacheLoadTools getEphCategoryMapCacheLoadTools() {
		return ephCategoryMapCacheLoadTools;
	}

	/**
	 * @param ephCategoryMapCacheLoadTools the ephCategoryMapCacheLoadTools to set
	 */
	public void setEphCategoryMapCacheLoadTools(
			BBBEphCategoryMapCacheLoadTools ephCategoryMapCacheLoadTools) {
		this.ephCategoryMapCacheLoadTools = ephCategoryMapCacheLoadTools;
	}

	/**
	 * @return the schedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return schedulerEnabled;
	}

	/**
	 * @param schedulerEnabled the schedulerEnabled to set
	 */
	public void setSchedulerEnabled(boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

}