/**
 * 
 */
package com.bbb.commerce.porch.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.feeds.marketing.utils.BBBMarketingFeedTools;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.repository.RepositoryItem;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * @author sm0191
 *
 */
public class PorchBookAJobScheduler extends SingletonSchedulableService {

	private boolean mSchedulerEnabled;
	
	private Scheduler mScheduler;

	/** The Schedule for this */
	private Schedule mSchedule;
	
	private PorchServiceManager porchServiceManager;
 
	private BBBMarketingFeedTools feedTools;
	
	private String dcPrefix;

	/* (non-Javadoc)
	 * @see atg.service.scheduler.SingletonSchedulableService#doScheduledTask(atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		 
		if (isSchedulerEnabled()) {
			if (isLoggingInfo()) {
				logInfo("Scheduler started to perform task porch book a job");
			}
			
			final Calendar currentDate = Calendar.getInstance();
			final Long time = currentDate.getTimeInMillis();
			final Timestamp schedulerStartDate = new Timestamp(time);
			Timestamp lastModifiedDate = getFeedTools().getLastModifiedDate(dcPrefix+BBBCoreConstants.PORCH_BOOK_A_JOB_API_SCHEDULER);
			if(null==lastModifiedDate){
				lastModifiedDate=schedulerStartDate;
			}
			RepositoryItem[] porchOrders =getPorchServiceManager().getPorchOrders(lastModifiedDate,getDcPrefix());
			if(null!=porchOrders){
				for(RepositoryItem porchOrder:porchOrders){
					try {
						BBBOrder order = (BBBOrder) getPorchServiceManager().getOrderManager().loadOrder(porchOrder.getRepositoryId());
						if(isLoggingDebug()){
							logDebug("Processing  A book job call for porch order "+order.toString());
						}
						processPorchOrder(order);
					} catch (CommerceException e) {
						if(isLoggingError()){
							logError("BBBPorchBookJobScheduler:   while invoking book a job for proch order "+e,e);
						}
					}
				}
			}
			getPorchServiceManager().updateScheduledRepository(schedulerStartDate , dcPrefix+BBBCoreConstants.PORCH_BOOK_A_JOB_API_SCHEDULER, true);
		}
		else {
			if (isLoggingDebug()) {
				logDebug("Scheduler Task is Disabled");
			}
		}
		
 
	} 


	/**
	 * @param order
	 */
	private void processPorchOrder(BBBOrder order) {	
		boolean isPorchOrder =getPorchServiceManager().isPorchServiceOrder(order);
		if(isPorchOrder){
			if(isLoggingDebug()){
				logDebug("Starts Porch book a job call for orderId :"+order.getId());
				}
				
			final List<CommerceItem> comItemObj = order.getCommerceItems();
			int shippingGroups = order.getShippingGroupCount();
		    if(shippingGroups>1){
			  // if order has more than one commerceItem with service with different shipping address - invoke multiple book a job 
			   getPorchServiceManager().multipleBookAJobServices(order, comItemObj,false); 
			   
		   }
		   else {		   
			   getPorchServiceManager().singleBookAJobService(order, comItemObj,false);	       
		   }
		}
	   	    	
		if(isLoggingDebug()){
			logDebug("Ends Porch book a job call for orderId :"+order.getId());
			}
	}


	


	
	   

	/**
		 * Helps to trigger schedule task manually
		 */
		public void executeDoScheduledTask() {
			if(isLoggingDebug()){
				logDebug("Entry executeDoScheduledTask");
			}
			doScheduledTask(null,new ScheduledJob("Porch Book A Job Data Scheduler", "", "", null, null, false));
			
			if(isLoggingDebug()){
				logDebug("Exit executeDoScheduledTask");
			}
		}

	/**
	 * @return the mSchedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}


	/**
	 * @param mSchedulerEnabled the mSchedulerEnabled to set
	 */
	public void setSchedulerEnabled(boolean mSchedulerEnabled) {
		this.mSchedulerEnabled = mSchedulerEnabled;
	}


	/**
	 * @return the mSchedule
	 */
	public Schedule getSchedule() {
		return mSchedule;
	}


	/**
	 * @param mSchedule the mSchedule to set
	 */
	public void setSchedule(Schedule mSchedule) {
		this.mSchedule = mSchedule;
	}


	/**
	 * @return the mScheduler
	 */
	public Scheduler getScheduler() {
		return mScheduler;
	}


	/**
	 * @param mScheduler the mScheduler to set
	 */
	public void setScheduler(Scheduler mScheduler) {
		this.mScheduler = mScheduler;
	}


	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}


	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}


	/**
	 * @return the feedTools
	 */
	public BBBMarketingFeedTools getFeedTools() {
		return feedTools;
	}


	/**
	 * @param feedTools the feedTools to set
	 */
	public void setFeedTools(BBBMarketingFeedTools feedTools) {
		this.feedTools = feedTools;
	}
	
	/**
	 * @return the dcPrefix
	 */
	public String getDcPrefix() {
		return dcPrefix;
	}


	/**
	 * @param dcPrefix the dcPrefix to set
	 */
	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

}
