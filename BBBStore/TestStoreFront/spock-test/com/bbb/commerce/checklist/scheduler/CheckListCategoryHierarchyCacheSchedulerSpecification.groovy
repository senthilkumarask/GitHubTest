package com.bbb.commerce.checklist.scheduler

import atg.service.scheduler.ScheduledJob
import atg.service.scheduler.Scheduler
import com.bbb.commerce.checklist.tools.CheckListCategoryHierarchyCacheTool;

import spock.lang.specification.BBBExtendedSpec

class CheckListCategoryHierarchyCacheSchedulerSpecification extends BBBExtendedSpec {
	 CheckListCategoryHierarchyCacheScheduler testObj
	 CheckListCategoryHierarchyCacheTool checkListCategoryHierarchyCacheToolMock=Mock()
	 Scheduler schedulerMock = Mock()
	 ScheduledJob scheduledJobMock = Mock()
	def setup(){
		testObj = new CheckListCategoryHierarchyCacheScheduler()
		}

	def "doScheduledTask-we set SchedulerEnable as true"(){
		given:
		testObj= Spy()
		testObj.setCheckListCategoryHierarchyCacheTool(checkListCategoryHierarchyCacheToolMock)
		testObj.setSchedulerEnable(true)
		1*checkListCategoryHierarchyCacheToolMock.populateCheckListHierarchyAndLoadInCache() >> true
		when:
		testObj.doScheduledTask(schedulerMock, scheduledJobMock)
		then:
		1*testObj.logInfo(_)
		0*testObj.logInfo("CheckListCategoryHierarchyCacheScheduler is not enable")
	}
	def "doScheduledTask-we set SchedulerEnable as false"(){
		given:
		testObj= Spy()
		testObj.setCheckListCategoryHierarchyCacheTool(checkListCategoryHierarchyCacheToolMock)
		testObj.setSchedulerEnable(false)
		0*checkListCategoryHierarchyCacheToolMock.populateCheckListHierarchyAndLoadInCache() >> true
		when:
		testObj.doScheduledTask(schedulerMock, scheduledJobMock)
		then:
		1*testObj.logInfo("CheckListCategoryHierarchyCacheScheduler is not enable")
	}
	def "doStartService-we set SchedulerEnable as true"(){
		testObj= Spy()
		testObj.setCheckListCategoryHierarchyCacheTool(checkListCategoryHierarchyCacheToolMock)
		testObj.setSchedulerEnable(true)
		1*checkListCategoryHierarchyCacheToolMock.populateCheckListHierarchyAndLoadInCache() >> true
		when:
		testObj.doStartService()
		then:
		
		0*testObj.logInfo("CheckListCategoryHierarchyCacheScheduler is not enable")
		1*testObj.logInfo("CheckListCategoryHierarchyCacheScheduler local doStartService  call in progress")
	}
	def "doStartService-we set SchedulerEnable as false"(){
		testObj= Spy()
		testObj.setCheckListCategoryHierarchyCacheTool(checkListCategoryHierarchyCacheToolMock)
		testObj.setSchedulerEnable(false)
		0*checkListCategoryHierarchyCacheToolMock.populateCheckListHierarchyAndLoadInCache() >> true
		when:
		testObj.doStartService()
		then:
		
		1*testObj.logInfo("CheckListCategoryHierarchyCacheScheduler is not enable")
		1*testObj.logInfo("CheckListCategoryHierarchyCacheScheduler local doStartService  call in progress")
	}
}
