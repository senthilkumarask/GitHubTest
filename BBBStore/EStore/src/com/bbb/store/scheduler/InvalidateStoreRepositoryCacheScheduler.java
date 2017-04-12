package com.bbb.store.scheduler;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;

import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 *
 * @author magga3
 * This scheduler invalidates the store repository cache
 * scheduled at every 8 am EST.
 *
 */
public class InvalidateStoreRepositoryCacheScheduler extends SingletonSchedulableService {

	private static final String BOPUS_FLAG_LAST_MODIFIED_DATE = "bopusFlagLastModifiedDate";
	private static final String ORACLE_DB_DATE_FORMAT = "dd-MMM-yy hh:mm:ss a";
	private boolean mSchedulerEnabled;
	private boolean useOldCacheInvalidationMethod;
	private long lastSchedulerRunTime;
	private boolean schedulerLastRunSuccess;
	
	/**
	 * @return the schedulerLastRunSuccess
	 */
	public boolean isSchedulerLastRunSuccess() {
		return schedulerLastRunSuccess;
	}

	/**
	 * @param schedulerLastRunSuccess the schedulerLastRunSuccess to set
	 */
	public void setSchedulerLastRunSuccess(boolean schedulerLastRunSuccess) {
		this.schedulerLastRunSuccess = schedulerLastRunSuccess;
	}

	/**
	 * @return the lastSchedulerRunTime
	 */
	public long getLastSchedulerRunTime() {
		return lastSchedulerRunTime;
	}

	/**
	 * @param lastSchedulerRunTime the lastSchedulerRunTime to set
	 */
	public void setLastSchedulerRunTime(long lastSchedulerRunTime) {
		this.lastSchedulerRunTime = lastSchedulerRunTime;
	}
	/**
	 * @return the useOldCacheInvalidationMethod
	 */
	public boolean isUseOldCacheInvalidationMethod() {
		return useOldCacheInvalidationMethod;
	}

	/**
	 * @param useOldCacheInvalidationMethod the useOldCacheInvalidationMethod to set
	 */
	public void setUseOldCacheInvalidationMethod(
			boolean useOldCacheInvalidationMethod) {
		this.useOldCacheInvalidationMethod = useOldCacheInvalidationMethod;
	}

	private GSARepository mStoreRepository;

	/**
	 * @return mStoreRepository
	 */
	public GSARepository getStoreRepository() {
		return this.mStoreRepository;
	}

	/**
	 * @param pStoreRepository
	 */
	public void setStoreRepository(GSARepository pStoreRepository) {
		this.mStoreRepository = pStoreRepository;
	}

	/**
	 * Returns whether the scheduler is enable or not
	 * 
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return this.mSchedulerEnabled;
	}

	/**
	 * This variable signifies to enable or disable the scheduler in specific
	 * environment this value is set from the property file
	 * 
	 * @param pSchedulerEnabled
	 *
	 */
	public void setSchedulerEnabled(boolean pSchedulerEnabled) {
		this.mSchedulerEnabled = pSchedulerEnabled;
	}


	@Override
	public void doScheduledTask(final Scheduler scheduler, final ScheduledJob job) {
		if(isSchedulerEnabled()){
			if(isLoggingInfo()){
				logInfo("Started Scheduler Job [" + getJobId() + ": " + getJobName() + "]" + "at date = " + new Date());
				logInfo("Job Description :" + this.getJobDescription() + " Scheduled at " + this.getSchedule());
			}
			if (isUseOldCacheInvalidationMethod()) {
				try {
					if(isLoggingInfo()) {
						logInfo("Clearing Cache using old Cache clear method");
					}
					clearStoreRepositoryCaches();
				} catch (RepositoryException exc) {
					logError("RepositoryException from " + getJobName() + BBBCatalogErrorCodes.UNABLE_TO_CACHE_DATA_EXCEPTION,exc);
				}
			} else {
				//Added isSchedulerLastRunSuccess() check. if there is exception in clearing store repository cache, then again try to clear cache
				if ((this.getLastSchedulerRunTime() == 0) || !this.isSchedulerLastRunSuccess()) {
					try {
						if(isLoggingInfo()) {
							logInfo("Scheduler First Run");
						}
						clearStoreRepositoryCaches();
						this.setSchedulerLastRunSuccess(true);
					} catch (RepositoryException exc) {
						this.setSchedulerLastRunSuccess(false);
						logError("RepositoryException from " + getJobName() + BBBCatalogErrorCodes.UNABLE_TO_CACHE_DATA_EXCEPTION,exc);
					}
				} else  {
					RepositoryItem[] storeItems = null;
					SimpleDateFormat sdf = new SimpleDateFormat(ORACLE_DB_DATE_FORMAT);
					String lastModifiedDate = sdf.format(this.getLastSchedulerRunTime());
					RepositoryView storeView;
					try {
						storeView = getStoreRepository().getView(BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
						final QueryBuilder queryBuilder = storeView.getQueryBuilder();
						final QueryExpression pProperty = queryBuilder.createPropertyQueryExpression(BOPUS_FLAG_LAST_MODIFIED_DATE);
						final QueryExpression pValue = queryBuilder.createConstantQueryExpression(lastModifiedDate);
						final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.GREATER_THAN_OR_EQUALS);
						if(isLoggingDebug()) {
							logDebug("Query to retrieve data : " + query);
						}
						storeItems = storeView.executeQuery(query);
						if (storeItems !=null && storeItems.length > 0) {
							clearStoreRepositoryCaches();
						}
					} catch (RepositoryException exc) {
						this.setSchedulerLastRunSuccess(false);
						logError("RepositoryException from " + getJobName() + BBBCatalogErrorCodes.UNABLE_TO_CACHE_DATA_EXCEPTION,exc);
					}
				}
			}
			
			this.setLastSchedulerRunTime(System.currentTimeMillis());

		} else {
			if (isLoggingInfo()) {
				logInfo(getJobName() + " task is disabled");
			}
		}
	}


	private void clearStoreRepositoryCaches() throws RepositoryException {
		String[] descriptorNames = getStoreRepository().getItemDescriptorNames();
			for (String descriptor : descriptorNames) {
				GSAItemDescriptor desc = (GSAItemDescriptor) getStoreRepository().getItemDescriptor(descriptor);
				desc.invalidateCaches(true);
			}
			if (isLoggingInfo()) {
				logInfo("StoreRepository Cache Cleared");
			}
	}

	/**
	 * Scheduled task to run the scheduler.
	 */
	public void doScheduledTask() {
		this.doScheduledTask(null,null);
	}
}
