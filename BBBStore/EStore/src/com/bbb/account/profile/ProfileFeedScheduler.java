/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ProfileFeedSchedular.java
 *
 *  DESCRIPTION: Profile feed schedular to migrate registries and their corresponding user into
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

import com.bbb.account.profile.vo.ProfileVO;
import com.bbb.account.profile.vo.RegistryVO;
import com.bbb.exception.BBBSystemException;

/**
 * @author hbandl
 *
 */
public class ProfileFeedScheduler extends SingletonSchedulableService {
	
	/*Name of the job*/
	private static final String JOB_NAME="Profile Feed";
	
	/*Description of the job*/
	private static final String JOB_DESCRIPTION="Scheduled job to migrate registries and their corresponding user";
	
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
	 * Flag to identify if profile feed needs to be process
	 */
	private boolean mProcessProfileFeed;
	
	/**
	 * Flag to identify if registry feed needs to be process
	 */
	private boolean mProcessRegistryFeed;
	
	
	/**
	 * @return the mProcessProfileFeed
	 */
	public boolean isProcessProfileFeed() {
		return mProcessProfileFeed;
	}

	/**
	 * @param pProcessProfileFeed flag to identify if profile feed needs to be process
	 */
	public void setProcessProfileFeed(boolean pProcessProfileFeed) {
		this.mProcessProfileFeed = pProcessProfileFeed;
	}

	/**
	 * @return the mProcessRegistryFeed
	 */
	public boolean isProcessRegistryFeed() {
		return mProcessRegistryFeed;
	}

	/**
	 * @param pProcessRegistryFeed flag to identify if registry feed needs to be process 
	 */
	public void setProcessRegistryFeed(boolean pProcessRegistryFeed) {
		this.mProcessRegistryFeed = pProcessRegistryFeed;
	}
	
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
		    if(isLoggingDebug()){
		        logDebug("ProfileFeedSchedular.doScheduledTask : Start");
		    }
			
			doProfileMigrationTask();
			
			if(isLoggingDebug()){
			    logDebug("ProfileFeedSchedular.doScheduledTask : End");
			}
		}
	}

    public void doProfileMigrationTask() {
    	
    	logError("ProfileFeedScheduler.doProfileMigrationTask: START");
    	if(isProcessProfileFeed()){
    		processProfileFeed();
    	}
    	
    	if(isProcessRegistryFeed()){
    		processRegistryFeed();
    	}
    	
    	logError("ProfileFeedScheduler.doProfileMigrationTask: END");
    }
    
    /**
     * method to process profile feed. This method will read the profile feed and process the records as batch.
     *  
     */
    private void processProfileFeed() {
    	
    	logDebug("ProfileFeedScheduler.processProfileFeed: START");
    	
    	try {
	    	
    		Map<String, ProfileVO> profileToMigrate = getFeedFileParser().readProfileFeed();
	    	getProfileFeedTool().saveProfiles(profileToMigrate);
	    	getFeedFileParser().writeProfileFeedResponse(profileToMigrate);
	    	
    	} catch (BBBSystemException e) {
           
                logError("Error occured while performing profile migration", e);
            
        }
    	
    	logDebug("ProfileFeedScheduler.processProfileFeed: END");	
    }
    
    /**
     * method to process registry feed. This method will read the registry feed and process the records as batch.
     * 
     */
    private void processRegistryFeed() {
    	
    	logDebug("ProfileFeedScheduler.processRegistryFeed: START");
    	
    	try {
    		
    		Map<String, RegistryVO> registryToMigrate = getFeedFileParser().readRegistryFeed();
    		getProfileFeedTool().saveProfileRegistries(registryToMigrate);
        	getFeedFileParser().writeRegistryFeedResponse(registryToMigrate, true);
        	
    	} catch (BBBSystemException e) {
           
                logError("Error occured while performing registry migration", e);
           
        }
    	
    	logDebug("ProfileFeedScheduler.processRegistryFeed: END");
    }
    
    /**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("ProfileFeedScheduler", "", "", null, null, false));
		
		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}
	
}