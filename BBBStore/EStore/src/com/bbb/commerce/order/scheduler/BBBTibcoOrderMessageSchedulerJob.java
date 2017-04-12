/**
 * 
 */
package com.bbb.commerce.order.scheduler;

import java.sql.Timestamp;
import java.util.Calendar;

import atg.commerce.order.Order;
import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.commerce.checkout.tibco.BBBSubmitOrderHandler;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBSystemException;

/**
 * ATG Scheduler job to :
 * 1. Retrieve all the order repository items which were not sent for fulfillment.
 * 2. For each order repository item, resend Tibco Message for fulfillment
 * 
 * @author alakra
 *
 */
public class BBBTibcoOrderMessageSchedulerJob extends SingletonSchedulableService {
	
	/**Batch size to pull Orders in batches*/
	private int mOrderBatchSize = -1;
	
	private long mOrderPickTimeDiffSeconds = 120;
	
	/**
	 * The order submitted duration (in hours) to be used to search
	 */
	private int mOrderSubmittedDurationToSearch = 1;
	
	private boolean mSchedulerEnabled = false; 
	
	/*Order to be searched with last submitted date/time*/
	private Timestamp mOrderSubmittedTime = null;
	
	/**
	 * Schedule of the job. It's of the format:
	 * "<months> <dates> <days of week> <occurrences in month> <hours of day> <minutes>"
	 * where each of the above elements, such as <dates>, is specified as one of
	 * the following: 
	 * A comma-separated list of numbers, such as "1,15" 
	 * A comma-separated list of ranges, such as "1-12,14-16" 
	 * A mixture of the above, such as "1,2,3,7-12" 
	 * A "*", indicating all numbers 
	 * A ".", indicating no numbers
	 */
	private String mJobSchedule;
	
	/**
	 * Order Manager instance for the scheduler to work with Orders
	 */
	private BBBOrderManager mOrderManager;
	
	/**
	 * Order Helper instance for the scheduler to work with Orders
	 */
	private BBBSubmitOrderHandler mOrderHelper;
	
	private SiteContextManager siteContextManager;

	/**
	 * @return siteContextManager
	 */
	public final SiteContextManager getSiteContextManager() {
		return this.siteContextManager;
	}

	/**
	 * @param siteContextManager
	 */
	public final void setSiteContextManager(final SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}
	
	/**
	 * @return the orderBatchSize
	 */
	public int getOrderBatchSize() {
		return this.mOrderBatchSize;
	}

	/**
	 * @param pOrderBatchSize the orderBatchSize to set
	 */
	public void setOrderBatchSize(final int pOrderBatchSize) {
		this.mOrderBatchSize = pOrderBatchSize;
	}
	
	/**
	 * @return the orderSubmittedDurationToSearch
	 */
	public final int getOrderSubmittedDurationToSearch() {
		return this.mOrderSubmittedDurationToSearch;
	}

	/**
	 * @param pOrderSubmittedDurationToSearch the orderSubmittedDurationToSearch to set
	 */
	public final void setOrderSubmittedDurationToSearch(final int pOrderSubmittedDurationToSearch) {
		this.mOrderSubmittedDurationToSearch = pOrderSubmittedDurationToSearch;
	}
	
	/**
	 * @return the schedulerEnabled
	 */
	public final boolean isSchedulerEnabled() {
		return this.mSchedulerEnabled;
	}

	/**
	 * @param pSchedulerEnabled the schedulerEnabled to set
	 */
	public final void setSchedulerEnabled(final boolean pSchedulerEnabled) {
		this.mSchedulerEnabled = pSchedulerEnabled;
	}
	
	/**
	 * @return the orderSubmittedTime
	 */
	public final Timestamp getOrderSubmittedTime() {
		return this.mOrderSubmittedTime;
	}
	
	/**
	 * @param orderSubmittedTime
	 */
	public final void setOrderSubmittedTime(final Timestamp orderSubmittedTime) {
		this.mOrderSubmittedTime = orderSubmittedTime;
	}
	
	/**
	 * Schedule of this job
	 * 
	 * @return job schedule
	 */
	public String getJobSchedule() {
		return this.mJobSchedule;
	}

	
	/**
	 * @param pJobSchedule
	 */
	public void setJobSchedule(final String pJobSchedule) {
		this.mJobSchedule = pJobSchedule;
	}
	
	/**
	 * @return the orderHelper
	 */
	public final BBBSubmitOrderHandler getOrderHelper() {
		return this.mOrderHelper;
	}

	/**
	 * @return the orderManager
	 */
	public final BBBOrderManager getOrderManager() {
		return this.mOrderManager;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public final void setOrderManager(final BBBOrderManager pOrderManager) {
		this.mOrderManager = pOrderManager;
	}

	/**
	 * @param pOrderHelper the orderHelper to set
	 */
	public final void setOrderHelper(final BBBSubmitOrderHandler pOrderHelper) {
		this.mOrderHelper = pOrderHelper;
	}

	/**
	 * This method is invoked repeatedly (by ATG Scheduler Service) at the
	 * preset schedule to perform the following tasks:
	 * - Retrieve Orders with substatus UNSUBMITTED
	 * - Send TIBCO message for the given order
	 * - Update the given order with substatus SUBMITTED
	 * 
	 * @see atg.service.scheduler.Schedulable#doScheduledTask(atg.service.scheduler.Scheduler,
	 *      atg.service.scheduler.ScheduledJob)
	 */
	@Override
	public void doScheduledTask(final Scheduler pScheduler,final ScheduledJob pScheduledJob) {
	
		this.doScheduledTask();
	}
	
	private void doScheduledTask() {
		if (isLoggingDebug()) {
			logDebug("START: Resubmitting Order submitted till [" + getOrderSubmittedTime() + "]");
		}
		if(isSchedulerEnabled()){
			long startTime=System.currentTimeMillis();
			/*Get the right batch size for order updates*/
			int batchSize = getOrderBatchSize();
			if(batchSize > -1){
				batchSize = batchSize - 1;
			}
			final int BATCH_SIZE = batchSize;
			
			try {
				int startIndex = 0;
				Order[] orders = null;
				/*Calculate the end index for batch retrieval*/
				final int endIndex = startIndex + BATCH_SIZE;
				/*Perform batch updates till the list of users retrieved is equals the batch size. When it is less than batch size, it indicates we're almost there*/
				do {
					final long orderPickTimeDiffSeconds = getOrderPickTimeDiffSeconds() * 1000;
					final Timestamp ordersPickUpTime = new Timestamp(Calendar.getInstance().getTimeInMillis() - orderPickTimeDiffSeconds);
					if(isLoggingDebug()){
						logDebug("ordersPickUpTime is:" + ordersPickUpTime);
					}
					/*Perform batch retrieval of orders for better performance*/
					orders = getOrderManager().getUnsubmittedOrders(BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED, ordersPickUpTime, startIndex, endIndex);
					if(orders != null && orders.length > 0){
						for(int index = 0; index < orders.length; index++){
							Site site;
							try {
								site = getSiteContextManager().getSite(orders[index].getSiteId());
								final SiteContextImpl context = new SiteContextImpl(getSiteContextManager(),site);
								getSiteContextManager().pushSiteContext(context);
								getOrderHelper().processSubmitOrder((BBBOrder) orders[index], null);
								getSiteContextManager().popSiteContext(context);
							} catch (SiteContextException e) {
								if (isLoggingError()) {
									logError("SiteContextException occured in BBBTibcoOrderMessageSchedulerJob" +
											"while while pushing siteId for order " + orders[index].getId(), e);
								}
							} 
						}
					}
	
					/*Recalculate the start index for batch retrieval*/
					startIndex = endIndex + 1;
				} while (orders != null && orders.length == BATCH_SIZE);
			}catch (BBBSystemException bse) {
				if (isLoggingError()) {
					logError("Exception while retrieving unsubmitted orders", bse);
				}
			}finally{
				long totalTime = System.currentTimeMillis() - startTime;
				logInfo("Total Number of Orders submitted [" + BATCH_SIZE + "]. Time Taken is [" + totalTime + "] ms");
			}
		} else {
			if (isLoggingDebug()) {
				logDebug("Submit Order Scheduler is DISABLED !!!");
			}
		}

		if (isLoggingDebug()) {
			logDebug("END: Resubmitting Order submitted till [" + getOrderSubmittedTime() + "]");
		}
	}

	/**
	 * @return the orderPickTimeDiff
	 */
	public long getOrderPickTimeDiffSeconds() {
		return this.mOrderPickTimeDiffSeconds;
	}

	
	/**
	 * @param pOrderPickTimeDiffSeconds
	 */
	public void setOrderPickTimeDiffSeconds(final long pOrderPickTimeDiffSeconds) {
		this.mOrderPickTimeDiffSeconds = pOrderPickTimeDiffSeconds;
	}
}