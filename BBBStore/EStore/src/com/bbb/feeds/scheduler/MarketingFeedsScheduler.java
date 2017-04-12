package com.bbb.feeds.scheduler;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import com.bbb.feeds.scheduler.FeedJobsScheduler;


import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

public class MarketingFeedsScheduler extends FeedJobsScheduler {

  private boolean mSchedulerEnabled;

  /**
   * Returns the whether the scheduler is enable or not
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
   *          the isShedulerEnabled to set
   */
  public void setSchedulerEnabled(boolean pSchedulerEnabled) {
    this.mSchedulerEnabled = pSchedulerEnabled;
  }

  // -------------------------------------
  // GenericService methods
  // -------------------------------------
  /**
   * This will start the thread running
   **/
  public void doStartService() {
    if (isLoggingDebug()) {
      logDebug("Started Bazaarvoice Scheduler Job [" + getJobId() + ": "+ getJobName()+"]" + "at date = " + new Date());
      logDebug("Job Description :" + this.getJobDescription() + " Scheduled at " + this.getSchedule());
    }

    getScheduler().addScheduledJob(
        new ScheduledJob(this.getJobDescription(), this.getJobName(), getAbsoluteName(), getSchedule(), this, false));

  }

  @Override
  public void performScheduledTask(final Scheduler scheduler, final ScheduledJob job) {
    if (isSchedulerEnabled()) {
      if (isLoggingInfo()) {
        logInfo("Scheduler started to perform task with job name=[" + job.getJobName() + "]");
      }
      final Calendar currentDate = Calendar.getInstance();
      final Long time = currentDate.getTimeInMillis();
      final Timestamp schedulerStartDate = new Timestamp(time);
      if (isLoggingDebug()) {
        logDebug("Feed Type :" + this.getTypeOfFeed() + " full feed is required: " + this.isFullDataFeed());
      }
      this.getFeedManager().getFeedDetails(this.getTypeOfFeed(), this.isFullDataFeed(), schedulerStartDate);
    } else {
      if (isLoggingDebug()) {
        logDebug("Scheduler Task is Disabled");
      }
    }
  }
  
  public void doStopService(){
		if (isLoggingDebug()) {
		    logDebug("Stopping Bazaarvoice Scheduler Job [" + getJobId() + ": "+ getJobName()+"]" + "at date = " + new Date());
			logDebug("Job Description :" + this.getJobDescription() + " Scheduled at " + this.getSchedule()+" is scheduler enabled? "+this.isSchedulerEnabled());
		}

		getScheduler().removeScheduledJob(this.getJobId());
	}

}