package com.bbb.borderfree.cache;

import java.rmi.RemoteException;
import java.util.Date;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.framework.cache.BBBLocalCacheContainer;

import atg.adapter.gsa.invalidator.GSAInvalidatorService;
import atg.core.util.StringUtils;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * 
 * @author pgup35
 * 
 */
public class BorderFreeInvalidateScheduler extends SingletonSchedulableService {
	/**
	 * Transaction Manager instance for scheduler
	 */
	private boolean schedulerEnabled;
	private GSAInvalidatorService gsaInvalidatorService;
	private static final String EXCH_RATES = "exchangeRates";
	private static final String COUNTRY_LIST = "countryList";
	private BBBLocalCacheContainer localCacheContainer;
	private BBBCatalogTools catalogTools;

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public BBBLocalCacheContainer getLocalCacheContainer() {
		return localCacheContainer;
	}

	public void setLocalCacheContainer(
			BBBLocalCacheContainer localCacheContainer) {
		this.localCacheContainer = localCacheContainer;
	}

	public GSAInvalidatorService getGsaInvalidatorService() {
		return gsaInvalidatorService;
	}

	public void setGsaInvalidatorService(
			GSAInvalidatorService gsaInvalidatorService) {
		this.gsaInvalidatorService = gsaInvalidatorService;
	}

	private String internationalRepository;

	public String getInternationalRepository() {
		return internationalRepository;
	}

	public void setInternationalRepository(String internationalRepository) {
		this.internationalRepository = internationalRepository;
	}

	/**
	 * @param schedulerEnabled
	 *            the schedulerEnabled to set
	 */
	public void setSchedulerEnabled(final boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

	/**
	 * Returns the whether the scheduler is enable or not
	 * 
	 * @return the isShedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return this.schedulerEnabled;
	}

	/**
	 * This variable signifies to enable or disable the scheduler in specific
	 * environment this value is set from the property file
	 * 
	 * @param isShedulerEnabled
	 *            the isShedulerEnabled to set
	 */

	@Override
	public void doScheduledTask(final Scheduler scheduler,
			final ScheduledJob job) {

		this.doScheduledTask();
	}

	public void doScheduledTask() {

		this.logInfo("Started Scheduler Job [" + this.getJobId() + ": "
				+ this.getJobName() + "]" + "at date = " + new Date());
		this.logInfo("Job Description :" + this.getJobDescription()
				+ " Scheduled at " + this.getSchedule());
		if (this.isSchedulerEnabled()) {

			try {
				if (!StringUtils.isEmpty(getInternationalRepository())) {
					if (isLoggingDebug()) {
						this.logDebug("Going to Invalidate Repository cache with name :"
								+ EXCH_RATES + " Start");
					}
					getGsaInvalidatorService().invalidate(
							getInternationalRepository(), EXCH_RATES, null);
					if (isLoggingDebug()) {
						this.logDebug("Going to Invalidate Repository cache with name :"
								+ EXCH_RATES + " End");
					}
					if (isLoggingDebug()) {
						this.logDebug("Going to Invalidate Repository cache with name :"
								+ COUNTRY_LIST + " Start");
					}
					getGsaInvalidatorService().invalidate(
							getInternationalRepository(), COUNTRY_LIST, null);
					if (isLoggingDebug()) {
						this.logDebug("Going to Invalidate Repository cache with name :"
								+ COUNTRY_LIST + " End");
					}
					if (null != getLocalCacheContainer()) {
						if (isLoggingDebug()) {
							this.logDebug("Going to Invalidate Local cache  start : ");
						}

						getLocalCacheContainer().clearCache();
						if (isLoggingDebug()) {

							this.logDebug("Going to Invalidate Local cache  End : ");
						}

					}
				}
			} catch (RemoteException e) {
				
					this.logError("Border Free Invalidate Cache Scheduler Method [doScheduledTask] : RemoteException  "
							+ e);
				
			}
			
			this.logInfo("Border Free Invalidate Cache is Successfully Completed !!");
			
		} else {
			
			this.logInfo("Scheduler Task is Disabled");
			
		}
	}

}
