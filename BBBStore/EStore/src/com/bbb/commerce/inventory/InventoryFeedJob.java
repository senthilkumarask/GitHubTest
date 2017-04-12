/**
 * 
 */
package com.bbb.commerce.inventory;


import java.util.List;

import javax.transaction.TransactionManager;

import org.apache.commons.lang.StringUtils;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.inventory.vo.InventoryFeedVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;

/**
 * @author alakra
 *
 */
public class InventoryFeedJob extends SingletonSchedulableService {
	

	/*Name of the job*/
	private static final String JOB_NAME="Inventory Feed";
	
	/*Description of the job*/
	private static final String JOB_DESCRIPTION="Scheduled job monitor inventory updates";
	
	/*Source of the job*/
	private static final String JOB_SOURCE_NAME="Bed Bath & Beyond eStore";
	
	/*Thread method of the job*/
	private static final int JOB_THREAD_METHOD = ScheduledJob.REUSED_THREAD;
	
	/*If transaction is enabled for the job*/
	private static final boolean JOB_TRANSACTION_ENABLED=true;
	
	/*Job id*/
	private int mJobID;
	
	private boolean mSchedulerEnabled;
	
	private int delayBeforeFeedUpdate;

	private int delayForFullFeed;

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
	 * Inventory Manager instance for the scheduler to work with Inventory
	 */
	private OnlineInventoryManager mInventoryManager;
	
	/**
	 * Transaction Manager instance for scheduler
	 */
	private TransactionManager mTransactionManager;
	
	/**
	 * Catalog Tools instance for the scheduler to work with Catalog API
	 */
	private BBBCatalogTools catalogTools;

	private String mFeedStatus;

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
	 * Returns the Inventory Manager instance
	 * @return Inventory Manager instance
	 */
	public OnlineInventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	/**
	 * Sets the Inventory Manager instance
	 * @param pInventoryManager
	 */
	public void setInventoryManager(OnlineInventoryManager pInventoryManager) {
		mInventoryManager = pInventoryManager;
	}
	
	/**
	 * @return Returns the transactionManager.
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	/**
	 * @param pTransactionManager
	 *            The transactionManager to set.
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * Returns the Catalog Tools instance
	 * @return Catalog Tools instance
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * Sets the Catalog Tools instance
	 * @param pCatalogTools
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		catalogTools = pCatalogTools;
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
	/**
	 * Default constructor
	 */
	public InventoryFeedJob() {
		//No implementation
	}
	
	
	/**
	 * This method is invoked repeatedly (by ATG Scheduler Service) at the preset schedule to perform the following tasks:
	 * - Retrieve Inventory Feed updates
	 * - 
	 * @see atg.service.scheduler.Schedulable#doScheduledTask(atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
	@Override
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledJob) {
		if (isSchedulerEnabled()) {
			if(isLoggingDebug()){
			logDebug("START: BBB inventory feed Job to process inventory feeds");
		}
		String updateAllInventory = BBBCoreConstants.FALSE;
		List<String> config = null;
		try {
			config = getCatalogTools().getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG);
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1024 + ": Error while retrieving configure keys value for [" + BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG + "]", e);
			}
		}
		if(config != null && config.size() > 0) {
			updateAllInventory = config.get(0);
		}
		boolean rollback = false;
		TransactionDemarcation td = new TransactionDemarcation();
		try {
			/*Start the transaction*/
			td.begin(getTransactionManager());

			
			

			/*Retrieve inventory updates - records should be fetched after Thread Sleep */
			List<InventoryFeedVO> inventoryUpdates = getInventoryManager().getInventoryFeedUpdates(getFeedStatus()); 
			

			
			if(inventoryUpdates!=null && inventoryUpdates.size()>0){
			
			boolean closeAllFeedsFlag = false;
			/*Process each inventory feed update*/
			for(int index = 0; index < inventoryUpdates.size(); index++) {
				InventoryFeedVO inventoryFeed = inventoryUpdates.get(index);
				
				if(!closeAllFeedsFlag){
					
					if(StringUtils.isNotBlank(inventoryFeed.getFeedStatus()) && StringUtils.equalsIgnoreCase(inventoryFeed.getFeedStatus(), BBBCoreConstants.FULL_OPEN)){
						// BBBSL-1948: Introducing Delay of before updating feed status START
						try {
							
							 if(isLoggingDebug()){
				            	  logDebug("InventoryFeedJob.doScheduledTask, Current thread going to sleep for " + getDelayForFullFeed() + "milli sec");
				             }
				             Thread.sleep(getDelayForFullFeed());
							
						} catch (InterruptedException ex) {
							logError("InventoryFeedJob.doScheduledTask, Exception occurred while adding delay in inverntory feed update operation-" + ex);
							Thread.currentThread().interrupt();
						}
						
						// BBBSL-1948: Introducing Delay of before updating feed status END
						getInventoryManager().invalidateInventoryCache();
						closeAllFeedsFlag = true;
						
					}else{
						
//						final long siteStockLevel = inventoryFeed.getSiteStockLevel();
//						final long globalStockLevel = inventoryFeed.getGlobalStockLevel();
//						final long registryStockLevel = inventoryFeed.getGiftRegistryStockLevel();
//						
//						final long baSiteStockLevel = inventoryFeed.getBASiteStockLevel();
//						final long baRegistryStockLevel = inventoryFeed.getBAGiftRegistryStockLevel();
//						final long caSiteStockLevel = inventoryFeed.getCASiteStockLevel();
//						final long caRegistryStockLevel = inventoryFeed.getCAGiftRegistryStockLevel();
//						
//						/*Get SKU details*/
//						SKUDetailVO skuDetail = catalogTools.getSKUDetails(BED_BATH_US, inventoryFeed.getSkuID(), false, true, true);
//						
//						/*Check if the SKU was marked out-of-stock*/
//						boolean outOfStock = (globalStockLevel == 0L || siteStockLevel == 0L || registryStockLevel == 0L || baSiteStockLevel == 0L || baRegistryStockLevel == 0L|| caSiteStockLevel == 0L || caRegistryStockLevel == 0L) ? true : false;
//						
//						/*If the SKU is out-of-stock or is VDU SKU, invalidate the SKU cache so that the stock level is read near-possible real-time*/
//						if(BBBCoreConstants.FALSE.equalsIgnoreCase(updateAllInventory) && !(skuDetail.isVdcSku() || outOfStock)) {
//							inventoryFeed.setFeedStatus(BBBCoreConstants.INVENTORY_FEED_COMPLETE);
//							continue;
//						}
						
						
						if(isLoggingDebug()){
							logDebug("Invalidating inventory cache for SKU [" + inventoryFeed.getSkuID() + "-" + inventoryFeed.getDisplayName() + "]");
						}
						
						/*Invalidate the local cache for the fetch to be latest*/
						getInventoryManager().invalidateInventoryCache(inventoryFeed);
					}
				}
				
				
				if(isLoggingDebug()){
					logDebug("Marking Inventory Feed [" + inventoryFeed.getFeedID() + "] to COMPLETE");
				}
				/*Mark the inventory feed status to "COMPLETE"*/
				inventoryFeed.setFeedStatus(BBBCoreConstants.INVENTORY_FEED_COMPLETE);
			}
			
			/*In order to kick-off the cluster-wide cache invalidation process, update the inventory*/
			/*if(!inventoryList.isEmpty()){
				//getInventoryManager().getInventoryTools().updateSKUInventoryForAllSites(inventoryList);
			}*/
			/*Once done, update the inventory feed status*/
			getInventoryManager().updateInventoryFeed(inventoryUpdates);
		}
		} 
//		catch (BBBBusinessException bbe) {
//			/*Don't rollback the transaction*/
//			if(isLoggingError()){
//				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1025 + ": Error while retrieving SKU details", bbe);
//			}
//		} 
		catch (BBBSystemException bse) {
			/*Rollback the transaction*/
			if(isLoggingError()){
				logError("Business Exception while running Inventory feed " + getFeedStatus(), bse);
			}
			rollback = true;
		}catch (TransactionDemarcationException e) {
			/*Rollback the transaction*/
			rollback = true;
			if(isLoggingError()){
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1026 + ": Transaction failure while processing inventory feed", e);
			}
		} finally {
			try {
				if (rollback && isLoggingError()) {
					logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1026 + ": Transaction failure while processing inventory feed. Rolling back the transaction. Will retry again");
				}
				/*Complete the transaction*/
				td.end(rollback);
			} catch (TransactionDemarcationException tde) {
				if (isLoggingError()) {
					logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1026 + ": Transaction failure while processing inventory feed", tde);
				}
			}
		}
		
		if(isLoggingDebug()){
			logDebug("END: BBB inventory feed Job to process inventory feeds");
		}
		}else {
			if (isLoggingDebug()) {
				logDebug("Scheduler Task is Disabled");
			}
		}
	}

	/**
	 * Getter for delayBeforeFeedUpdate- for introducing delay before starting the feed update
	 * 
	 * @return delayBeforeFeedUpdate
	 */
	public int getDelayBeforeFeedUpdate() {
		return delayBeforeFeedUpdate;
	}

	/**
	 * Setter for delayBeforeFeedUpdate.
	 * 
	 * @param delayBeforeFeedUpdate
	 */
	public void setDelayBeforeFeedUpdate(int delayBeforeFeedUpdate) {
		this.delayBeforeFeedUpdate = delayBeforeFeedUpdate;
	}

	public String getFeedStatus() {
		return mFeedStatus;
	}

	public void setFeedStatus(String mFeedStatus) {
		this.mFeedStatus = mFeedStatus;
	}

	public int getDelayForFullFeed() {
		return delayForFullFeed;
	}

	public void setDelayForFullFeed(int delayForFullFeed) {
		this.delayForFullFeed = delayForFullFeed;
	}

	
}