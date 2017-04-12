package com.bbb.thirdparty.omniture.scheduler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.thirdparty.omniture.manager.OmnitureReportAPIManager;
import com.bbb.thirdparty.omniture.vo.OmnitureReportEmailContentVO;
import com.bbb.thirdparty.omniture.vo.OmnitureReportStatus;
import com.bbb.utils.BBBUtility;

/**
 * Scheduler to fetch and queue Omniture reports
 * @author Sapient
 * 
 */
public class OmnitureReportScheduler extends SingletonSchedulableService {
	/**
	 * Field to enable/disable scheduler
	 * 
	 */
	private boolean schedulerEnabled;

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
	 * OmnitureReportAPIManager 
	*/
	private OmnitureReportAPIManager omnitureReportAPIManager;
	 
	/**
	 *  @return omnitureReportAPIManager
	 */
	public OmnitureReportAPIManager getOmnitureReportAPIManager() {
		return omnitureReportAPIManager;
	}
    /**
     * 
     * @param omnitureReportAPIManager set to omnitureReportAPIManager
     */
	public void setOmnitureReportAPIManager(
			OmnitureReportAPIManager omnitureReportAPIManager) {
		this.omnitureReportAPIManager = omnitureReportAPIManager;
	}

	@Override
	public void doScheduledTask(final Scheduler scheduler,
			final ScheduledJob job) {

		this.doScheduledTask();
	}

	/**
	 * Fetches/queue omniture reports for all three concepts
	 */
	public void doScheduledTask() {
		this.logInfo("OmnitureReportScheduler.scheduledTask method started");
		
		
		if (this.isSchedulerEnabled()) {
		
		// emailContentListMap  to save emailStatusVo which contains detail of report during processing  
		Map<String,List<OmnitureReportEmailContentVO>> emailContentListMap=new LinkedHashMap<String, List<OmnitureReportEmailContentVO>>(10);
		
		// queueStatusList to save the respose of getQueueStatus API which will we used to exclude GET and Queue a report which is not ready
		List<OmnitureReportStatus> queueStatusList=new ArrayList<>();	
		
		try{
							
				getOmnitureReportAPIManager().checkQueue(queueStatusList, emailContentListMap);
				this.logInfo("OmnitureReportScheduler.checkQueue complete");
				
				getOmnitureReportAPIManager().consumeQueuedReport(queueStatusList, emailContentListMap);
				this.logInfo("OmnitureReportScheduler.consumeQueuedReport complete");
				
				getOmnitureReportAPIManager().queueScheduledReport(queueStatusList, emailContentListMap);
				this.logInfo("OmnitureReportScheduler.queueScheduledReport complete: ");
				
				
			}catch(Exception exception)
			{
				logError("OmnitureReportScheduler.doScheduledTask "+exception);
				List<OmnitureReportEmailContentVO> excepList= new ArrayList<OmnitureReportEmailContentVO>();
				OmnitureReportEmailContentVO emailVo= new OmnitureReportEmailContentVO();
				emailVo.setExceptionDetails("Exception In OmnitureReportScheduler :"+BBBUtility.getExceptionTrace(exception));
				excepList.add(emailVo); 
				emailContentListMap.put(BBBCoreConstants.OMNITURE_PROCESSING_EXCEPTION, excepList);
			}
			finally
			{
			  try{
				  getOmnitureReportAPIManager().sendEmail(emailContentListMap);
				  }
			     catch(Exception ex)
			     {
			    	 logError("OmnitureReportScheduler.doScheduledTask sendemail:"+ex);
			    	 if(isLoggingDebug()){
			    		 logError("OmnitureReportScheduler.doScheduledTask sendemail:Stack Trace:"+BBBUtility.getExceptionTrace(ex));
			    	 }
			     }
			}
			
			
			
		} else {
			this.logInfo("Scheduler Task is Disabled");
		}
		this.logInfo("OmnitureReportScheduler.scheduledTask method End");
	}


}
