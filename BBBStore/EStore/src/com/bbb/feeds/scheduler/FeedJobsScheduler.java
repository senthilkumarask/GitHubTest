package com.bbb.feeds.scheduler;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.TransactionManager;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.feeds.manager.FeedJobsManager;
import com.bbb.utils.BBBJobContextManager;

/**
 * 
 * @author Prashanth K Bhoomula
 * 
 */
public class FeedJobsScheduler extends SingletonSchedulableService {
	/**
	 * Transaction Manager instance for scheduler
	 */
	private TransactionManager transactionManager;
	private String typeOfFeed;
	private boolean fullDataFeed;
	private boolean enabled;
	private boolean mSchedulerEnabled;	
	private FeedJobsManager feedManager;

	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager
	 *            the transactionManager to set
	 */
	public void setTransactionManager(
			final TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return the feedManager
	 */
	public FeedJobsManager getFeedManager() {
		return feedManager;
	}

	/**
	 * @param feedManager
	 *            the feedManager to set
	 */
	public void setFeedManager(final FeedJobsManager feedManager) {
		this.feedManager = feedManager;
	}

	/**
	 * @return the typeOfFeed
	 */
	public String getTypeOfFeed() {
		return typeOfFeed;
	}

	/**
	 * @param typeOfFeed
	 *            the typeOfFeed to set
	 */
	public void setTypeOfFeed(final String typeOfFeed) {
		this.typeOfFeed = typeOfFeed;
	}

	/**
	 * @return the isFullDataFeed
	 */
	public boolean isFullDataFeed() {
		return fullDataFeed;
	}

	/**
	 * @param isFullDataFeed
	 *            the isFullDataFeed to set
	 */
	public void setFullDataFeed(final boolean fullDataFeed) {
		this.fullDataFeed = fullDataFeed;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param isEnabled
	 *            the isEnabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns the whether the scheduler is enable or not
	 * 
	 * @author Prashanth K Bhoomula
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * This variable signifies to enable or disable the scheduler in specific
	 * environment this value is set from the property file
	 * 
	 * @param isShedulerEnabled
	 *            the isShedulerEnabled to set
	 */
	public void setSchedulerEnabled(boolean pSchedulerEnabled) {
		this.mSchedulerEnabled = pSchedulerEnabled;
	}

	
	@Override
	public void doScheduledTask(final Scheduler scheduler,
			final ScheduledJob job) {

		this.doScheduledTask();
		
	}

	public void doScheduledTask() {

		if (isLoggingInfo()) {
			logInfo("Started Scheduler Job [" + getJobId() + ": "
					+ getJobName() + "]" + "at date = " + new Date());
			logInfo("Job Description :" + this.getJobDescription()
					+ " Scheduled at " + this.getSchedule());
		}
		if (isSchedulerEnabled()) {
			if (isLoggingInfo()) {
				logInfo("Scheduler started to perform task with job name=["
						+ getJobName() + "]");
			}
			
			Long threadId = Thread.currentThread().getId();
			Integer jobId = getJobId();
			
//			Populate JobInfo and Job Context			
			populateJobContext(threadId,jobId);
			
			final Calendar currentDate = Calendar.getInstance();
			final Long time = currentDate.getTimeInMillis();
			final Timestamp schedulerStartDate = new Timestamp(time);
			if (isLoggingDebug()) {
				logDebug("Feed Type :" + this.getTypeOfFeed()
						+ " full feed is required: " + this.isFullDataFeed());
			}
			this.getFeedManager().getFeedDetails(this.getTypeOfFeed(),
					this.isFullDataFeed(), schedulerStartDate);
			
//			Remove JobInfo and Job Context
			removeJobContext(threadId, jobId);

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
	
	private void removeJobContext(Long threadId, Integer jobId) {
		BBBJobContextManager.getInstance().getJobInfo().remove(threadId.toString()); 
		BBBJobContextManager.getInstance().getJobContext().remove(jobId.toString());
	}

	private void populateJobContext(Long threadId, Integer jobId) {
		BBBJobContextManager.getInstance().getJobInfo()
				.put(threadId.toString(), jobId.toString());
		Map<String, String> jobContext = populateJobName(getJobName());
		BBBJobContextManager.getInstance().getJobContext()
				.put(jobId.toString(), jobContext);

	}

	private Map<String, String> populateJobName(String jobName) {
		Map<String , String> jobContext = new HashMap<String , String>(); 
		jobContext.put(BBBCoreConstants.JOB_NAME, jobName);
		return jobContext;
	}


	/*public void doStopService() {
		if (isLoggingDebug()) {
			logDebug("Stopping Scheduler Job [" + getJobId() + ": "
					+ getJobName() + "]" + "at date = " + new Date());
			logDebug("Job Description :" + this.getJobDescription()
					+ " Scheduled at " + this.getSchedule()
					+ " is scheduler enabled? " + this.isSchedulerEnabled());
		}

		getScheduler().removeScheduledJob(this.getJobId());
	}*/
}
