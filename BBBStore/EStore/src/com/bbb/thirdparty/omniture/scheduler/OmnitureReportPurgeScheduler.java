package com.bbb.thirdparty.omniture.scheduler;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.thirdparty.omniture.tools.OmnitureBoostedProductTools;

/**
 * 
 * @author apan25
 * This class is used as scheduler to purge omniture boosted data from archive table.
 */
public class OmnitureReportPurgeScheduler extends SingletonSchedulableService {
	/**
	 * Transaction Manager instance for scheduler
	 */
	private boolean mSchedulerEnabled = false;
	
	private OmnitureBoostedProductTools omnitureBoostedProductTools;
	
	public void doScheduledTask() {
		logDebug("OmnitureReportPurgeScheduler:doScheduledTask - Start");
		if (this.isSchedulerEnabled()) {
				
			purgeOmnitureDataForUS();
			purgeOmnitureDataForBaby();
			purgeOmnitureDataForCA();
			
		} else {
			this.logInfo("Scheduler Task is Disabled");
		}
		logDebug("OmnitureReportPurgeScheduler:doScheduledTask - End");
	}
	
	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		doScheduledTask();
	}

	private void purgeOmnitureDataForUS() {
		getOmnitureBoostedProductTools().purgeOmnitureDataFromArchiveTable(BBBCoreConstants.BBB_CONSTANT);
	}

	private void purgeOmnitureDataForCA() {
		getOmnitureBoostedProductTools().purgeOmnitureDataFromArchiveTable(BBBCoreConstants.CA_CONSTANT);
	}
	
	private void purgeOmnitureDataForBaby() {
		getOmnitureBoostedProductTools().purgeOmnitureDataFromArchiveTable(BBBCoreConstants.BABY_CONSTANT);
	}
	
	/**
	 * 
	 * @return the omnitureBoostedProductTools
	 */
	public OmnitureBoostedProductTools getOmnitureBoostedProductTools() {
		return omnitureBoostedProductTools;
	}

	/**
	 * 
	 * @param omnitureBoostedProductTools
	 * 	       the omnitureBoostedProductTools to set
	 */
	public void setOmnitureBoostedProductTools(
			OmnitureBoostedProductTools omnitureBoostedProductTools) {
		this.omnitureBoostedProductTools = omnitureBoostedProductTools;
	}
	
	/**
	 * @return the schedulerEnabled
	 */
	public final boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * @param pSchedulerEnabled
	 *            the schedulerEnabled to set
	 */
	public final void setSchedulerEnabled(boolean pSchedulerEnabled) {
		mSchedulerEnabled = pSchedulerEnabled;
	}

}
