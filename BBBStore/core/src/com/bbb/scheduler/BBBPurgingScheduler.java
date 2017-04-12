/**
 * The BBBPurgingScheduler is a ATG scheduler component which purges items
 * The scheduler is configured to run at a specific time daily and this will remove all item from the system
 */

package com.bbb.scheduler;

import javax.transaction.TransactionManager;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepositoryItem;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.constants.BBBCoreConstants;

public class BBBPurgingScheduler extends SingletonSchedulableService{
	
	private Schedule mSchedule;
	private Scheduler mScheduler;	
	private String mJobDescription;
	private BBBPurgeTools mPurgeTools;
	private TransactionManager mTransactionManager;
    private boolean mSchedulerEnabled;
	private int mMaxItemsPerTransaction;

    private Boolean mPurge = false;
    
    
     
	
	/**
	 * @return the mMaxItemsPerTransaction
	 */
	public int getMaxItemsPerTransaction() {
		return mMaxItemsPerTransaction;
	}

	/**
	 * @param mMaxItemsPerTransaction the mMaxItemsPerTransaction to set
	 */
	public void setMaxItemsPerTransaction(int pMaxItemsPerTransaction) {
		this.mMaxItemsPerTransaction = pMaxItemsPerTransaction;
	}

	/**
	 * @return the mPurgeTools
	 */
	public BBBPurgeTools getPurgeTools() {
		return mPurgeTools;
	}

	/**
	 * @param mPurgeTools the mPurgeTools to set
	 */
	public void setPurgeTools(BBBPurgeTools pPurgeTools) {
		this.mPurgeTools = pPurgeTools;
	}

	/**
	 * Get the transaction manager component for the scheduler
	 * @return
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	/**
	 * Sets the transaction manager component for the scheduler
	 * @param pTransactionManager
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}


	/**
	 * Get the job description for the scheduler
	 */
	public String getJobDescription() {
		return mJobDescription;
	}

	/**
	 * Set the job description for the scheduler
	 */
	
	public void setJobDescription(String pJobDescription) {
		this.mJobDescription = pJobDescription;
	}

	/**
	 * method returns the schedule for running the scheduler
	 * @return
	 */
	public Schedule getSchedule() {
		return mSchedule;
	}

	/**
	 * setter method sets the schedule for running the scheduler from the properties file
	 * @param pSchedule
	 */
	public void setSchedule(Schedule pSchedule) {
		mSchedule = pSchedule;
	}	

	/**
	 * method returns the ATG scheduler object
	 * @return
	 */
	public Scheduler getScheduler() {
		return mScheduler;
	}

	/**
	 * setter method sets the ATG scheduler from the properties file 
	 * @param pScheduler
	 */
	public void setScheduler(Scheduler pScheduler) {
		mScheduler = pScheduler;
	}
	
	/**
	 * @return the schedulerEnabled
	 */
	public final boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * @param pSchedulerEnabled the schedulerEnabled to set
	 */
	public final void setSchedulerEnabled(boolean pSchedulerEnabled) {
		mSchedulerEnabled = pSchedulerEnabled;
	}
	
	
	@Override
	/**
	 * Overridden method which is invoked on call of the scheduler.
	 * The method calls the processPurge to start the purging process
	 */
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledjob) {
		this.doScheduledTask();
	}	
	
	public void doScheduledTask() {
		if(isLoggingInfo()){
			logInfo("Entry doScheduledTask");
		}
		Boolean purge = processPurge();
		if(isLoggingInfo()){
			logInfo("Exit doScheduledTask. Purge Successful :"+purge.toString());
		}
	}	
	
	/**
	 * The method purges the items
	 */
	public Boolean processPurge(){
		if(isLoggingDebug()){
			logDebug("Entry Purge Process");
		}
		if(isSchedulerEnabled()){
						
			TransactionDemarcation td = null;
			int purgeItemsCount = 0;
			MutableRepositoryItem[] purgeSchedulerItems= null;
			
			try {
				purgeSchedulerItems = (MutableRepositoryItem[]) getPurgeTools().getPurgeSchedulerItems();
				if (purgeSchedulerItems != null && purgeSchedulerItems.length != 0) {
					for (MutableRepositoryItem item : purgeSchedulerItems) {
		            	mPurge = false;
						MutableRepositoryItem[] purgeItems = (MutableRepositoryItem[]) getPurgeTools().findPurgeItems(item);
		            	
		            	if(purgeItems!= null && purgeItems.length > 0){
							if(isLoggingDebug()){
								logDebug("Item count : " + purgeItems.length);
							}
							td = new TransactionDemarcation();
		            		  
		            		td.begin(getTransactionManager());
		            		if(isLoggingDebug()){
	            				logDebug("Removing items for: "+item.getPropertyValue(BBBCoreConstants.PROPERTY_PURGE_ID));
	            			}
		            		purgeItemsCount = (purgeItems.length < getMaxItemsPerTransaction() ? purgeItems.length : getMaxItemsPerTransaction());
		            		for(int i=0;i<purgeItemsCount ;i++){
		            			getPurgeTools().removeItem(purgeItems[i] , item);
		            		}
		            		td.end();
		            	} else {
	            	  		if(isLoggingDebug()){
	            				logDebug("No Items to Purge for: "+item.getPropertyValue(BBBCoreConstants.PROPERTY_PURGE_ID));
	            			}
	            	  	}
		            	mPurge = true;
					}
		       } else {
					logDebug("Purge repository returned null");
		       }
			}
			catch (TransactionDemarcationException e) {
				logError("Transaction Demarcation error commiting process as a batch from processPurge() in BBBPurgingScheduler", e);
			} finally {
				if (td != null) {
					try {
						td.end();
					} catch (TransactionDemarcationException e) {
						logError("Transaction Demarcation error commiting process as a batch from processPurge() in BBBPurgingScheduler", e);
					}
				}
			}
		}else {
			if(isLoggingDebug()){
				logDebug("Purge Scheduler is DISABLED !!!");
			}
		}
		if(isLoggingDebug()){
			logDebug("Exit processPurge");
		}
		return mPurge;
	}
}
