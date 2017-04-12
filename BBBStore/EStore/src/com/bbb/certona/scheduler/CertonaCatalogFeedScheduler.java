package com.bbb.certona.scheduler;

import java.sql.Timestamp;
import java.util.Calendar;
//import java.util.Date;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

public class CertonaCatalogFeedScheduler extends CertonaScheduler {

  private boolean mSchedulerEnabled;

  /**
   * Returns the whether the scheduler is enable or not
   * 
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

 
  @Override
  public void doScheduledTask(final Scheduler scheduler, final ScheduledJob job) {
	  this.doScheduledTask();
  }
  
  public void doScheduledTask() {
		if (isSchedulerEnabled()) {
			if (isLoggingInfo()) {
				logInfo("Scheduler started to perform task with job name=["
						+ getJobName() + "]");
			}
			final Calendar currentDate = Calendar.getInstance();
			final Long time = currentDate.getTimeInMillis();
			final Timestamp schedulerStartDate = new Timestamp(time);
			if (isLoggingDebug()) {
				logDebug("Feed Type :" + this.getTypeOfFeed()
						+ " full feed is required: " + this.isFullDataFeed());
			}
			this.getFeedManager().getFeedDetails(this.getTypeOfFeed(),
					this.isFullDataFeed(), schedulerStartDate);
			
			if (isLoggingInfo()) {
				logInfo("Scheduler finished with job name=["
						+ getJobName() + "]");
			}
		} else {
			if (isLoggingDebug()) {
				logDebug("Scheduler Task is Disabled");
			}
		}
  }	
  
}
