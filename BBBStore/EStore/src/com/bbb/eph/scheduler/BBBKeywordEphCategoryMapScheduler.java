package com.bbb.eph.scheduler;

import java.util.Map;

import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.eph.tools.BBBKeywordEphCategoryMapTool;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * This Scheduler used to create mapping between keyword and EPH/category. 
 * @author Sapient
 * 
 */
public class BBBKeywordEphCategoryMapScheduler extends SingletonSchedulableService {
	
	/**
	 * Field to enable/disable scheduler
	 * 
	 */
	private boolean schedulerEnabled;
	
	private SiteContextManager siteContextManager;
	
	private Map<String,String> conceptSiteIdMap;
	
	private BBBKeywordEphCategoryMapTool keywordEphCategoryMapTool;
	
	private boolean fullMappingRefresh;
	
	
	private static String CLS_NAME = "BBBKeywordEphCategoryMapScheduler";
	
	@Override
	public void doScheduledTask(final Scheduler scheduler,
			final ScheduledJob job) {

		this.doScheduledTask();
	}

	 
	public void doScheduledTask() {
		this.logInfo(CLS_NAME+".scheduledTask method started");
		 
		BBBPerformanceMonitor.start(CLS_NAME + "doScheduledTask");
		
		if(isSchedulerEnabled())
			{
				
				for(Map.Entry<String, String> conceptSiteId : getConceptSiteIdMap().entrySet())
				{
					 
					String concept=	conceptSiteId.getKey();
					String site=	conceptSiteId.getValue();
					logInfo("Scheduler Task is enable:Starting Mapping for concept:["+concept+"],site:["+site+"] ");
					doMapping(concept,site);
				}
				if(this.isFullMappingRefresh()){
					try {
						getKeywordEphCategoryMapTool().removeOldMappings();
					}
					catch (Exception exception)
					{
						logError(CLS_NAME+":Exception in remove old mappings : "+exception);
					}
				}
			}
		else
			{
				logInfo("Scheduler Task is Disabled");
			}
		
		BBBPerformanceMonitor.end(CLS_NAME + "doScheduledTask");
		this.logInfo(CLS_NAME+".scheduledTask method End..");
	}

	private void doMapping(String concept,String siteId) {
		
		logDebug(CLS_NAME+" Start: concept:"+concept+",siteId:"+siteId);
		
		BBBPerformanceMonitor.start(CLS_NAME + "doMapping:"+concept);
		
		Site site = null;
		try {
			
				site = this.getSiteContextManager().getSite(siteId);
				final SiteContextImpl context = new SiteContextImpl(this.getSiteContextManager(), site);
				this.getSiteContextManager().pushSiteContext(context);
			try {
					getKeywordEphCategoryMapTool().createEPHCategoryMapping(concept,this.isFullMappingRefresh());
				} 
				catch (Exception exception)
				{
					this.logError(CLS_NAME+":Exception in doMapping for concept:["+concept+"],exception"+exception);
				}
			
				getSiteContextManager().popSiteContext(context);
				
			} 
			catch (SiteContextException e)
			{
				logError("SiteContextException occured in OmnitureReportScheduler while pushing siteId :: ");
			}
		
		BBBPerformanceMonitor.end(CLS_NAME + "doMapping:"+concept);
		
		logDebug(CLS_NAME+" End: : concept:"+concept+",siteId:"+siteId+"]");
	}
		
 

	/**
	 * @return the schedulerEnabled
	 */
	public boolean isSchedulerEnabled() {
		return schedulerEnabled;
	}

	/**
	 * @param schedulerEnabled the schedulerEnabled to set
	 */
	public void setSchedulerEnabled(boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

	/**
	 * @return the siteContextManager
	 */
	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}

	/**
	 * @param siteContextManager the siteContextManager to set
	 */
	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}

	/**
	 * @return the conceptSiteIdMap
	 */
	public Map<String, String> getConceptSiteIdMap() {
		return conceptSiteIdMap;
	}

	/**
	 * @param conceptSiteIdMap the conceptSiteIdMap to set
	 */
	public void setConceptSiteIdMap(Map<String, String> conceptSiteIdMap) {
		this.conceptSiteIdMap = conceptSiteIdMap;
	}

	/**
	 * @return the keywordEphCategoryMapTool
	 */
	public BBBKeywordEphCategoryMapTool getKeywordEphCategoryMapTool() {
		return keywordEphCategoryMapTool;
	}

	/**
	 * @param keywordEphCategoryMapTool the keywordEphCategoryMapTool to set
	 */
	public void setKeywordEphCategoryMapTool(
			BBBKeywordEphCategoryMapTool keywordEphCategoryMapTool) {
		this.keywordEphCategoryMapTool = keywordEphCategoryMapTool;
	}


	/**
	 * @return the fullMappingRefresh
	 */
	public final boolean isFullMappingRefresh() {
		return fullMappingRefresh;
	}


	/**
	 * @param fullMappingRefresh the fullMappingRefresh to set
	 */
	public final void setFullMappingRefresh(boolean fullMappingRefresh) {
		this.fullMappingRefresh = fullMappingRefresh;
	}


}
