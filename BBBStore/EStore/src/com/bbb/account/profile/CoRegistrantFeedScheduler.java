/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CoRegistrantFeedScheduler.java
 *
 *  DESCRIPTION: Profile feed scheduler to perform Co-registrant linking for migrate registries and their corresponding user into
 *  the ATG schema 
 *   	
 *  HISTORY:
 *  26/03/12 Initial version
 *  	
 */
package com.bbb.account.profile;

import java.util.Map;

import atg.nucleus.ServiceException;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.account.profile.vo.RegistryVO;
import com.bbb.exception.BBBSystemException;

/**
 * @author hbandl
 *
 */
public class CoRegistrantFeedScheduler extends SingletonSchedulableService {
	
	/*Name of the job*/
	private static final String JOB_NAME="CoRegistrant Linking";
	
	/*Description of the job*/
	private static final String JOB_DESCRIPTION="Scheduled job to perform Co-registrant linking for migrate registries and their corresponding user";
	
	/*Source of the job*/
	private static final String JOB_SOURCE_NAME="Bed Bath & Beyond eStore";
	
	/*Thread method of the job*/
	private static final int JOB_THREAD_METHOD = ScheduledJob.SEPARATE_THREAD;
	
	/*If transaction is enabled for the job*/
	private static final boolean JOB_TRANSACTION_ENABLED = false;
	
	/*ProfileFeedTools instance*/
	private ProfileFeedTools mProfileFeedTool;
	
	/*Job id*/
	private int mJobID;
	
	/**
	 * Schedule of the job. It's of the format:
	 * 	"<months> <dates> <days of week> <occurrences in month> <hours of day> <minutes>"
	 * where each of the above elements, such as <dates>, is specified as one of the following: 
	 *	A comma-separated list of numbers, such as "1,15" 
	 *	A comma-separated list of ranges, such as "1-12,14-16" 
	 *	A mixture of the above, such as "1,2,3,7-12" 
	 *	A "*", indicating all numbers 
	 *	A ".", indicating no numbers 
	 * 
	 */
	private String mJobSchedule;

	/**
	 * Scheduler instance to the job
	 */
	private Scheduler mScheduler;
	
	/**
	 * FeedFileParser instance
	 */
	private FeedFileParser mFeedFileParser;
	
	/**
	 * enable property
	 */
	private boolean mSchedulerEnabled;
	
	/**
	 * @return the mEnable
	 */
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * @param mEnable the mEnable to set
	 */
	public void setSchedulerEnabled(boolean pEnable) {
		this.mSchedulerEnabled = pEnable;
	}
	
	/**
	 * @return the mFeedFileParser
	 */
	public FeedFileParser getFeedFileParser() {
		return mFeedFileParser;
	}

	/**
	 * @param pFeedFileParser the pFeedFileParser to set
	 */
	public void setFeedFileParser(FeedFileParser pFeedFileParser) {
		this.mFeedFileParser = pFeedFileParser;
	}

	/**
	 * @return the mProfileFeedTool
	 */
	public ProfileFeedTools getProfileFeedTool() {
		return mProfileFeedTool;
	}

	/**
	 * @param pProfileFeedTool the pProfileFeedTool to set
	 */
	public void setProfileFeedTool(ProfileFeedTools pProfileFeedTool) {
		this.mProfileFeedTool = pProfileFeedTool;
	}
	
	/**
	 * Job ID of this job
	 * @return Job ID
	 */
	public int getJobID() {
		return mJobID;
	}
	
	/**
	 * Name of this job
	 * @return Job Name
	 */
	public String getJobName(){
		return JOB_NAME;
	}
	
	/**
	 * Description of this job
	 * @return Job Description
	 */
	public String getJobDescription(){
		return JOB_DESCRIPTION;
	}
	
	/**
	 * Source of this job
	 * @return Job Source
	 */
	public String getJobSourceName(){
		return JOB_SOURCE_NAME;
	}
	
	/**
	 * Thread method of this job
	 * @return Job Thread Method
	 */
	public int getJobThreadMethod(){
		return JOB_THREAD_METHOD;
	}
	
	/**
	 * Transaction enabled flag of this job
	 * @return Transaction enabled flag
	 */
	public boolean isJobTransactionEnabled(){
		return JOB_TRANSACTION_ENABLED;
	}

	

	/**
	 * Schedule of this job
	 * @return job schedule
	 */
	public String getJobSchedule() {
		return mJobSchedule;
	}

	/**
	 * Sets the schedule of this job
	 * @param pSchedule
	 */
	public void setJobSchedule(String pJobSchedule) {
		mJobSchedule = pJobSchedule;
	}

	/**
	 * The ATG scheduler service instance
	 * @return Scheduler instance
	 */
	public Scheduler getScheduler() {
		return mScheduler;
	}

	/**
	 * Sets the ATG scheduler service instance
	 * @param pScheduler
	 */
	public void setScheduler(Scheduler pScheduler) {
		mScheduler = pScheduler;
	}

	
	/**
	 * This method is invoked repeatedly (by ATG Scheduler Service) at the preset schedule to perform the following tasks:
	 * - Retrieve Inventory Feed updates
	 * - 
	 * @see atg.service.scheduler.Schedulable#doScheduledTask(atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
	@Override
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledJob) {
		
		if(isSchedulerEnabled()){
		    if(isLoggingDebug()) {
		        logDebug("CoRegistrantFeedScheduler.doScheduledTask : Start");
		    }
			
			doCoRegistrantTask();
			
			if(isLoggingDebug()) {
			    logDebug("CoRegistrantFeedScheduler.doScheduledTask : End");
			}
		}
	}
	
	@Override
    public void doStartService() throws ServiceException {
     //do nothing
    }
    
    @Override
    public void doStopService() throws ServiceException {
        //do nothing
    }

    public void doCoRegistrantTask() {
        try {
        	
        	Map<String, RegistryVO> coRegistrantToMigrate = getFeedFileParser().readRegistryFeed();
        	getProfileFeedTool().linkCoRegistrant(coRegistrantToMigrate);
        	
        	getFeedFileParser().writeRegistryFeedResponse(coRegistrantToMigrate, false);
        	
        } catch (BBBSystemException e) {
            if(isLoggingError()) {
                logError("Error occured while performing CoRegistrant linking for migrated profile/registry", e);
            
            }
        }
    }
    
    /**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("CoRegistrantFeedScheduler", "", "", null, null, false));
		
		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}	
	
}