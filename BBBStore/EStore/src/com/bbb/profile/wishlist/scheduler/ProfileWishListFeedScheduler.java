package com.bbb.profile.wishlist.scheduler;

import java.util.concurrent.TimeUnit;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import com.bbb.profile.wishlist.tools.ProfileWishListFeedTools;

/**
 * @author ikhan2
 *
 */
public class ProfileWishListFeedScheduler extends SingletonSchedulableService {
	
	/**
	 * FeedTools instance
	 */
	private ProfileWishListFeedTools mFeedTools;
	
	/**
	 * enable schedule property
	 */
	private boolean mSchedulerEnabled;
	
	/**
	 * Flag to identify if wishlist feed needs to be process
	 */
	private boolean mProcessWishListFeed;

	
	
	
	/**
	 * This method is invoked repeatedly (by ATG Scheduler Service) at the preset schedule 
	 * to perform the following tasks:
	 * - Retrieve Profile Wish List Feed update
	 * - 
	 * 
	 */
	@Override
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledJob) {
		
		if(isSchedulerEnabled()){
	        logInfo("ProfileWishListFeedScheduler.doScheduledTask : Start");
			
			doProfileWishListMigrationTask();
			
			logInfo("ProfileWishListFeedScheduler.doScheduledTask : End");
		}
	}
	
	/**
	 * This method can be also be called directly fron /dyn/admin 
	 */
	public void doProfileWishListMigrationTask() {
		long startTime = System.currentTimeMillis();	
		if (isProcessWishListFeed()) {
			logInfo("ProfileWishListFeedScheduler.doProfileMigrationTask: START");
			try {
				getFeedTools().processWishListFeed();

			} catch (Exception excp) {
				if (isLoggingError()) {
					logError(
							"Error occured while performing profile migration",
							excp);
				}
			}

			logInfo("ProfileWishListFeedScheduler.doProfileMigrationTask: END");
		}else{
			logInfo("ProfileWishListFeedScheduler.doProfileMigrationTask: Disabled");
		}
		logInfo("Total Time taken by ProfileWishListFeedScheduler.doProfileMigrationTask is:" + TimeUnit.MINUTES.convert((System.currentTimeMillis() - startTime),TimeUnit.MILLISECONDS) +" Minutes ");
	}

	/**
	 * @return the mFeedTools
	 */
	public ProfileWishListFeedTools getFeedTools() {
		return mFeedTools;
	}

	/**
	 * @param mFeedTools the mFeedTools to set
	 */
	public void setFeedTools(ProfileWishListFeedTools mFeedTools) {
		this.mFeedTools = mFeedTools;
	}

	/**
	 * @return the mEnable
	 */
	public boolean isProcessWishListFeed() {
		return mProcessWishListFeed;
	}

	/**
	 * @param mEnable the mEnable to set
	 */
	public void setProcessWishListFeed(boolean pEnable) {
		this.mProcessWishListFeed = pEnable;
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
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("ProfileWishListFeedScheduler", "", "", null, null, false));
		
		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}

	
}