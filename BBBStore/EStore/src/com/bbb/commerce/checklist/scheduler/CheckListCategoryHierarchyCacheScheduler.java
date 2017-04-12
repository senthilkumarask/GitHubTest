package com.bbb.commerce.checklist.scheduler;

import com.bbb.commerce.checklist.tools.CheckListCategoryHierarchyCacheTool;

import atg.nucleus.ServiceException;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * This Scheduler will populate hierarchy of Interactive check list with
 * dimensionId , number of product available for URL and Siblings of URL
 * with details and put in coherence Cache.
 */

public class CheckListCategoryHierarchyCacheScheduler extends SingletonSchedulableService {
	
	private boolean schedulerEnable= false;
	
	private CheckListCategoryHierarchyCacheTool checkListCategoryHierarchyCacheTool;
	
	
	public void doScheduledTask() {
			if(! isSchedulerEnable()){
				logInfo("CheckListCategoryHierarchyCacheScheduler is not enable");
				return;
			}
			else{
				boolean success=	getCheckListCategoryHierarchyCacheTool().populateCheckListHierarchyAndLoadInCache();
				logInfo("CheckListCategoryHierarchyCacheScheduler is enable: job executed: isSuccess:"+success);
			}
		}
	
	public void doStartService() throws ServiceException {
		callSuperDoStartService();
		logInfo("CheckListCategoryHierarchyCacheScheduler local doStartService  call in progress");
		doScheduledTask();
	}

	/**
	 * @throws ServiceException
	 */
	protected void callSuperDoStartService() throws ServiceException {
		super.doStartService();
	}
	
	
	
	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		
		doScheduledTask();
	}



	public boolean isSchedulerEnable() {
		return schedulerEnable;
	}
	



	public void setSchedulerEnable(boolean schedulerEnable) {
		this.schedulerEnable = schedulerEnable;
	}



	public CheckListCategoryHierarchyCacheTool getCheckListCategoryHierarchyCacheTool() {
		return checkListCategoryHierarchyCacheTool;
	}
	



	public void setCheckListCategoryHierarchyCacheTool(
			CheckListCategoryHierarchyCacheTool checkListCategoryHierarchyCacheTool) {
		this.checkListCategoryHierarchyCacheTool = checkListCategoryHierarchyCacheTool;
	}
	
	

}
