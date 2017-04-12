package com.bbb.common.scheduler;

import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import atg.nucleus.GenericService;
import atg.nucleus.Nucleus;
import atg.nucleus.ServiceException;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.HeadPipelineServlet;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.cache.CoherenceCacheContainer;

/**
 * This class contain methods to start scheduler which is used to disable calls
 * to coherence server if coherence server is unresponsive. Scheduler check
 * after a regular interval of time that coherence server enable or not and it
 * will enable calls to coherence after a given threshold time.
 * 
 * @author ssha53
 * 
 */
public class CoherenceCacheScheduler extends GenericService implements Schedulable {

	// Scheduler enable/disable flag
	private boolean enabled;

	// CoherenceCacheContainer instance
	private CoherenceCacheContainer coherenceCacheContainer;
	
	private Scheduler scheduler;

	private Schedule schedule;
    
	int jobId;
   
    /**
     * Perform scheduler task
     * @param Scheduler
     * @param ScheduledJob
     */
	@Override
	public void performScheduledTask(Scheduler scheduler,
			ScheduledJob scheduledjob) {
		if(isLoggingDebug()){
			logDebug("CoherenceCacheScheduler.performScheduledTask() - start");
		}

		if (isEnabled()) {
			doCoherenceEnablecheck();
		}

		if(isLoggingDebug()){
			logDebug("CoherenceCacheScheduler.performScheduledTask() - end");
		}

	}

    @Override
    public void doStartService() throws ServiceException {
        ScheduledJob scheduledJob = new ScheduledJob("Start","Coherence Scheduled Service",getAbsoluteName(),getSchedule(),this,ScheduledJob.SCHEDULER_THREAD);
        jobId = getScheduler().addScheduledJob(scheduledJob);
    }
   
    @Override
    public void doStopService() throws ServiceException {
    	getScheduler().removeScheduledJob(jobId);
    }


	/**
	 * This method is used to set CoherenceCacheContainer component boolean flag
	 * coherenceEnabled to true.If coherence disable timeout is greater then
	 * specific timeout.
	 */
	public void doCoherenceEnablecheck() {

		if (isLoggingDebug()) {
			long startTime = System.currentTimeMillis();
			logDebug("CoherenceCacheScheduler.doCoherenceEnablecheck() start at  :" + startTime);
		}

		long currentTimeStamp = Calendar.getInstance().getTimeInMillis();

		
		DynamoHttpServletRequest request = (DynamoHttpServletRequest) (((HeadPipelineServlet) Nucleus
				.getGlobalNucleus().resolveName(BBBCoreConstants.DYNAMO_HANDLER)).getRequest(null));
		// In schedular there is no request object avaliable that why create a reques
		DynamoHttpServletResponse response = request.getResponse();
		ServletUtil.setCurrentRequest(request);
		ServletUtil.setCurrentResponse(response);
		DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
		req.setParameter(BBBCoreConstants.CHANNEL,BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
		// adding Site id for the scheduler
		req.setParameter(BBBCoreConstants.IS_FROM_SCHEDULER, BBBCoreConstants.TRUE);
		req.setParameter(BBBCoreConstants.SITE_ID, BBBCoreConstants.SITE_BBB);
		
		long coherenceEnableThresholdTime = getCoherenceCacheContainer()
				.getConfiguredValues(
						BBBCoreConstants.COHERENCE_ENABLE_THRESHOLD_TIME);
		
		if(coherenceEnableThresholdTime != 0){
			getCoherenceCacheContainer().setCoherenceEnableThresholdTime(coherenceEnableThresholdTime);
		}
		
		for(Entry<String, Boolean> entry: getCoherenceCacheContainer().getCoherenceEnabledMap().entrySet()){
			if(getCoherenceCacheContainer().getCoherenceDisableTimeStampMap().get(entry.getKey()) != null){
				if ((currentTimeStamp - getCoherenceCacheContainer().getCoherenceDisableTimeStampMap().get(entry.getKey())) > 
				getCoherenceCacheContainer().getCoherenceEnableThresholdTime()) {
					getCoherenceCacheContainer().getCoherenceEnabledMap().put(entry.getKey(), BBBCoreConstants.RETURN_TRUE);
				}
			}				
		}
		

		if (isLoggingDebug()) {
			long endTime = System.currentTimeMillis();
			logDebug("CoherenceCacheScheduler.doCoherenceEnablecheck() end at  :"
					+ endTime);
		}
	}

	/**
	 * this method return <code>CoherenceCacheContainer</code> component.
	 * 
	 * @return the coherenceCacheContainer
	 */
	public CoherenceCacheContainer getCoherenceCacheContainer() {
		return coherenceCacheContainer;
	}

	/**
	 * This method sets coherenceCacheContainer component
	 * 
	 * @param coherenceCacheContainer
	 *            the coherenceCacheContainer to set
	 */
	public void setCoherenceCacheContainer(
			final CoherenceCacheContainer coherenceCacheContainer) {
		this.coherenceCacheContainer = coherenceCacheContainer;
	}

	/**
	 * This method returns <code>boolean</code> contains true or false.
	 * 
	 * @return <code>true</code> if Scheduler is enabled else, return
	 *         <code>false</code>
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * 
	 * This method sets enable value true or false.
	 * 
	 * @param enabled
	 *            in <code>boolean</code> format.
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
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