/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CertonaProfileFeedScheduler.java
 *
 *  DESCRIPTION: Profile specific Certona Feed scheduler. It processes the Certona feed for Profile
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  05/02/12 Initial version
 *
 */
package com.bbb.certona.scheduler;

import java.sql.Timestamp;
import java.util.Calendar;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

public class CertonaProfileFeedScheduler extends CertonaScheduler {

	
	@Override
	public void doScheduledTask(final Scheduler scheduler,
			final ScheduledJob job) {
		
		this.doScheduledTask();
	}
	
	public void doScheduledTask() {
		
		if(isEnabled()){

			if (isLoggingInfo()) {
				logInfo("Scheduler started to perform task with job name=["	+ getJobName() + "]");
			}
			
			final Calendar currentDate = Calendar.getInstance();
			final Long time = currentDate.getTimeInMillis();
			final Timestamp schedulerStartDate = new Timestamp(time);
			if (isLoggingDebug()) {
				logDebug("Feed Type :" + this.getTypeOfFeed() + " full feed is required: " + this.isFullDataFeed());
			}
			this.getFeedManager().getFeedDetails(this.getTypeOfFeed(), this.isFullDataFeed(), schedulerStartDate);
			
			if (isLoggingInfo()) {
				logInfo("Exiting doScheduledTask with job name=["+getJobName ()+"]");
			}
		}else{
			if (isLoggingInfo()) {
				logInfo("Scheduler is disabled");
			}
		}
	}
	
}
