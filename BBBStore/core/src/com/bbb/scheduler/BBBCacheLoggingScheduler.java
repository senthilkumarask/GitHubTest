package com.bbb.scheduler;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.nucleus.ServiceException;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.HeadPipelineServlet;

import com.bbb.framework.cache.BBBCacheDroplet;

public class BBBCacheLoggingScheduler extends SingletonSchedulableService {
	public BBBCacheLoggingScheduler() {
		//default constructor
	}

	private String dynamoHandler = "/atg/dynamo/servlet/dafpipeline/DynamoHandler";
	
	// Scheduler property
	Scheduler scheduler;
	private boolean schedulerEnabled;
	
	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	//Cache Property
	BBBCacheDroplet cacheDroplet;

	public BBBCacheDroplet getCacheDroplet() {
		return cacheDroplet;
	}

	public void setCacheDroplet(BBBCacheDroplet cacheDroplet) {
		this.cacheDroplet = cacheDroplet;
	}

	// Schedule property
	Schedule schedule;

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
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

	// Schedulable method
	public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
		
		if (this.isSchedulerEnabled()) {
			if (isLoggingDebug()) {
				logDebug("BBBCacheLoggingScheduler.performScheduledTask() : Scheduler Enabled ");
			}
			DynamoHttpServletRequest pRequest = (DynamoHttpServletRequest)(((HeadPipelineServlet) Nucleus.getGlobalNucleus().resolveName(dynamoHandler)).getRequest(null));
			ServletUtil.setCurrentRequest(pRequest);		
			pRequest.setRequestURI("/store/_includes/header/header.jsp");
			pRequest.setParameter("loggingSchedulerKey", "TopNav_BedBathCanada");
			char[] cacheData = getCacheDroplet().getCachedContent(pRequest);
			if(cacheData != null  && isLoggingDebug())
			{
				logDebug("BBBCacheLoggingScheduler.performScheduledTask() Reading cache content for TopNav_BedBathCanada: ");
				logDebug((StringUtils.removeCRLF(String.valueOf(cacheData)).trim()).replaceAll("[\\n\\t ]", ""));
			}
			else
			{   if(isLoggingDebug()){
				logDebug("BBBCacheLoggingScheduler.performScheduledTask() Cache content empty for TopNav_BedBathCanada");
			}
			}
		} else {
			if (isLoggingDebug()) {
				logDebug("BBBCacheLoggingScheduler.performScheduledTask() : Scheduler Disabled ");
			}
		}
	}

	// Start method
	int jobId;

	public void doStartService() throws ServiceException {
		ScheduledJob job = new ScheduledJob("TopNav Cache Logging", "Prints cache value of Top Nav for BedBathCanada Site.",
				getAbsoluteName(), getSchedule(), this,
				ScheduledJob.SEPARATE_THREAD);

		jobId = getScheduler().addScheduledJob(job);
	}

	// Stop method
	public void doStopService() throws ServiceException {
		getScheduler().removeScheduledJob(jobId);
	}

	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		ScheduledJob job = new ScheduledJob("TopNav Cache Logging", "Prints cache value of Top Nav for BedBathCanada Site.",
				getAbsoluteName(), getSchedule(), this,
				ScheduledJob.SEPARATE_THREAD);

		//jobId = getScheduler().addScheduledJob(job);
		
			this.performScheduledTask(arg0, job);
		}
}