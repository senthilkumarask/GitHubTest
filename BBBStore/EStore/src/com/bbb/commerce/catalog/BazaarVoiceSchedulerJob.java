package com.bbb.commerce.catalog;


import java.sql.Timestamp;
import java.util.Calendar;

import javax.xml.bind.JAXBException;

import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * This class schedule a job to parse BazaarVoice provided review rating xml file.
 * After parsing xml file it would interact BazaarVoiceManager for keeping/update review rating of any product.  
 * @author ajosh8
 *
 */

public class BazaarVoiceSchedulerJob extends SingletonSchedulableService{

	private static final String JOB_DESCRIPTION = "BazaarVoice Feed Job";

	private static final String BAZAAR_VOICE_JOB = "BazaarVoiceJob";

	private boolean mSchedulerEnabled;
	private String typeOfFeed;



	//-------------------------------------
	// Properties

	public String getTypeOfFeed() {
		return typeOfFeed;
	}
	public void setTypeOfFeed(String typeOfFeed) {
		this.typeOfFeed = typeOfFeed;
	}
	@Override
	public String getJobDescription() {
		return JOB_DESCRIPTION;
	}
	@Override
	public String getJobName() {
		return BAZAAR_VOICE_JOB;
	}
	/** The Scheduler for this */
	private Scheduler mScheduler;

	/** The Schedule for this */
	private Schedule mSchedule;

	private BazaarVoiceManager mBazaarVoiceManager;
	private BazaarVoiceUnMarshaller mBazaarVoiceUnMarshaller;


	/**
	 * @return the bazaarVoiceManager
	 */
	public BazaarVoiceManager getBazaarVoiceManager() {
		return mBazaarVoiceManager;
	}

	/**
	 * @param pBazaarVoiceManager the bazaarVoiceManager to set
	 */
	public void setBazaarVoiceManager(BazaarVoiceManager pBazaarVoiceManager) {
		mBazaarVoiceManager = pBazaarVoiceManager;
	}



	/**
	 * @return the bazaarVoiceUnMarshaller
	 */
	public BazaarVoiceUnMarshaller getBazaarVoiceUnMarshaller() {
		return mBazaarVoiceUnMarshaller;
	}

	/**
	 * @param pBazaarVoiceUnMarshaller the bazaarVoiceUnMarshaller to set
	 */
	public void setBazaarVoiceUnMarshaller(BazaarVoiceUnMarshaller pBazaarVoiceUnMarshaller) {
		mBazaarVoiceUnMarshaller = pBazaarVoiceUnMarshaller;
	}


	public void setScheduler (Scheduler pScheduler) 
	{
		mScheduler = pScheduler;
	}

	//-------------------------------------
	public Scheduler getScheduler ()
	{
		return mScheduler;
	}

	//-------------------------------------
	public void setSchedule (Schedule pSchedule)
	{
		mSchedule = pSchedule;
	}

	//-------------------------------------
	public Schedule getSchedule ()
	{
		return mSchedule;
	}
	/**
	 * Returns the whether the scheduler is enable or not
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 *  This variable signifies to enable or disable the scheduler in specific
	 * environment
	 * this value is set from the property file
	 * @param isShedulerEnabled the isShedulerEnabled to set
	 */
	public void setSchedulerEnabled(boolean pSchedulerEnabled) {
		this.mSchedulerEnabled = pSchedulerEnabled;
	}








	//-------------------------------------
	// Schedulable methods
	//-------------------------------------
	/**
	 *
	 * This is called when a scheduled job tied to this object occurs.
	 *
	 * @param pScheduler calling the job
	 * @param pJob the ScheduledJob
	 **/
	public void doScheduledTask (Scheduler pScheduler,ScheduledJob pJob)
	{
		if (isSchedulerEnabled()) {
			if (isLoggingInfo()) {
				logInfo("Scheduler started to perform task with job name=[" + pJob.getJobName() + "]");
			}

			updateBaazarVoiceProperties();

		} else {
			if (isLoggingDebug()) {
				logDebug("Scheduler Task is Disabled");
			}
		}
	}

	/**
	 * This method would unmarshal the given bazaar voice xml and call the manager 
	 * class method to update/insert review properties.
	 */
	private void updateBaazarVoiceProperties() {
		if (isLoggingDebug()) {
			logDebug("Entering BazaarVoiceSchedulerJob.updateBaazarVoiceProperties method");

		}
		boolean status=false;
		final Calendar currentDate = Calendar.getInstance();
		final Long time = currentDate.getTimeInMillis();
		final Timestamp schedulerStartDate = new Timestamp(time);
		try {
			//calling method to parse xml placed certain location.
			status = getBazaarVoiceUnMarshaller().unmarshal();

		} catch (BBBSystemException e) {
			if (isLoggingDebug()) {
				logDebug(e);

			}
		} catch (BBBBusinessException e) {
			if (isLoggingDebug()) {
				logDebug(e);
			}
		} catch (JAXBException e) {
			if (isLoggingDebug()) {
				logDebug(e);
			}
		} 
		finally {
			this.getBazaarVoiceManager().updateScheduledRepository(schedulerStartDate, "full", typeOfFeed, status);
		}
		if (isLoggingDebug()) {
			logDebug("Ending BazaarVoiceSchedulerJob.updateBaazarVoiceProperties method");

		}

	}
	
	/**
	 * Helps to trigger schedule task manually
	 */
	public void executeDoScheduledTask() {
		if(isLoggingDebug()){
			logDebug("Entry executeDoScheduledTask");
		}
		doScheduledTask(null,new ScheduledJob("BazaarVoiceSchedulerJob", "", "", null, null, false));
		
		if(isLoggingDebug()){
			logDebug("Exit executeDoScheduledTask");
		}
	}
}




