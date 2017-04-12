package com.bbb.commerce.giftregistry.scheduler

import atg.nucleus.Nucleus
import atg.nucleus.registry.NucleusRegistry
import atg.repository.RepositoryItem
import atg.servlet.pipeline.HeadPipelineServlet

import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import java.sql.SQLException

import spock.lang.specification.BBBExtendedSpec

class SendDailyRecommEmailSchedulerSpecification extends BBBExtendedSpec {

	SendDailyRecommEmailScheduler sdrEmailSheduler
	GiftRegistryTools grTools = Mock()
	RepositoryItem rcomEmail = Mock()
	Nucleus nucluseMock = Mock()
	HeadPipelineServlet hpServlet = Mock()
	GiftRegistryRecommendationManager grRecManager = Mock()
	
	def setup() {
	sdrEmailSheduler = new SendDailyRecommEmailScheduler(giftRegistryTools : grTools,batchSize : 3,giftRegistryRecommendationManager : grRecManager, enabled : true)
	
	
	NucleusRegistry sNucleusRegistry = Mock()
	Nucleus.setNucleusRegistry(sNucleusRegistry)
	sNucleusRegistry.getKey() >> "nKey"
	sNucleusRegistry.get("nKey") >> nucluseMock
	
	Nucleus.sUsingChildNucleii = true

	}
	
	def"doScheduledTask"(){
		given:
		//grTools.refreshMattView(0) >> {}
		//grTools.refreshRepoItemCache("RecommendationEmailDaily") >> {}
		1*grTools.fetchRecomEmail(0,3,"RecommendationEmailDaily") >> [rcomEmail]
		
		1*rcomEmail.getPropertyValue("registryId") >> "r123"
		1*rcomEmail.getPropertyValue("userId") >> "u123"
		1*rcomEmail.getPropertyValue("firstName") >> "harry"
		1*rcomEmail.getPropertyValue("lastName") >> "jock"
		1*rcomEmail.getPropertyValue("recommendation_count") >> 2
		1*rcomEmail.getPropertyValue("email") >> "test@gmail.com"
		1*rcomEmail.getPropertyValue("eventType") >> "eType"
		1*rcomEmail.getPropertyValue("siteId") >> "usBed"
		
		nucluseMock.resolveName("/atg/dynamo/servlet/dafpipeline/DynamoHandler") >> hpServlet
		hpServlet.getRequest(null) >> requestMock
		requestMock.getResponse() >> responseMock
		
		//grRecManager.sendRegistrantScheduledBulkEmail(_)
		
		grTools.logBatchJobStatus(0,_) >> {}
		
		when:
		 //sdrEmailSheduler.doScheduledTask(null, null)
		sdrEmailSheduler.executeDoScheduledTask()
		then:
		
		1*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		1*requestMock.setParameter("configurableType", "Daily")
		1*requestMock.setParameter("isFromScheduler", "true")
		1*requestMock.setParameter("siteId", "usBed")
		1*grRecManager.sendRegistrantScheduledBulkEmail(_)
		1*grTools.refreshMattView(0)
		1*grTools.refreshRepoItemCache("RecommendationEmailDaily")

	}
	
	def"doScheduledTask.TC when batch size is one"(){
		given:
		sdrEmailSheduler.setBatchSize(1)
		1*grTools.fetchRecomEmail(0,1,"RecommendationEmailDaily") >> [rcomEmail]
		
		1*rcomEmail.getPropertyValue("registryId") >> "r123"
		1*rcomEmail.getPropertyValue("userId") >> "u123"
		1*rcomEmail.getPropertyValue("firstName") >> "harry"
		1*rcomEmail.getPropertyValue("lastName") >> "jock"
		1*rcomEmail.getPropertyValue("recommendation_count") >> 2
		1*rcomEmail.getPropertyValue("email") >> "test@gmail.com"
		1*rcomEmail.getPropertyValue("eventType") >> "eType"
		1*rcomEmail.getPropertyValue("siteId") >> "usBed"
		
		nucluseMock.resolveName("/atg/dynamo/servlet/dafpipeline/DynamoHandler") >> hpServlet
		hpServlet.getRequest(null) >> requestMock
		requestMock.getResponse() >> responseMock
		
		//grRecManager.sendRegistrantScheduledBulkEmail(_)
		
		grTools.logBatchJobStatus(0,_) >> {}
		
		when:
		 sdrEmailSheduler.doScheduledTask(null, null)
		then:
		1*grTools.refreshRepoItemCache("RecommendationEmailDaily")
		1*grTools.refreshMattView(0)
		1*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		1*requestMock.setParameter("configurableType", "Daily")
		1*requestMock.setParameter("isFromScheduler", "true")
		1*requestMock.setParameter("siteId", "usBed")
		1*grRecManager.sendRegistrantScheduledBulkEmail(_)

	}
	
	def"doScheduledTask.TC for BBBSystemException"(){
		given:
		sdrEmailSheduler.setBatchSize(1)
        1*grTools.refreshRepoItemCache("RecommendationEmailDaily")	>> {throw new BBBSystemException("exception")}	
		when:
		 sdrEmailSheduler.doScheduledTask(null, null)
		then:
		
		1*grTools.refreshMattView(0)
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Daily")

	}
	
	def"doScheduledTask.TC for BBBBusinessException"(){
		given:
		sdrEmailSheduler.setBatchSize(1)
		1*grTools.refreshRepoItemCache("RecommendationEmailDaily")	>> {throw new BBBBusinessException("exception")}
		when:
		 sdrEmailSheduler.doScheduledTask(null, null)
		then:
		
		1*grTools.refreshMattView(0)
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Daily")

	}
	
	def"doScheduledTask.TC for SQLException"(){
		given:
		//sdrEmailSheduler = Spy()
		sdrEmailSheduler.setBatchSize(1)
		1*grTools.refreshMattView(0)	>> {throw new SQLException("Registry not found")}
		when:
		 sdrEmailSheduler.doScheduledTask(null, null)
		then:
		//1*sdrEmailSheduler.logInfo("SendDailyRecommEmailScheduler doScheduledTask started :: Registry not found")
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Daily")

	}
	
	def"doScheduledTask.TC for SQLException when error message is not 'Registry not found'"(){
		given:
		//sdrEmailSheduler = Spy()
		sdrEmailSheduler.setBatchSize(1)
		1*grTools.refreshMattView(0)	>> {throw new SQLException("Registr")}
		when:
		 sdrEmailSheduler.doScheduledTask(null, null)
		then:
		
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Daily")

	}
	
	def"doScheduledTask.TC for SQLException when error message is null"(){
		given:
		//sdrEmailSheduler = Spy()
		sdrEmailSheduler.setBatchSize(1)
		1*grTools.refreshMattView(0)	>> {throw new SQLException()}
		when:
		 sdrEmailSheduler.doScheduledTask(null, null)
		then:
		
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Daily")

	}
	
	def"doScheduledTask.TC when sheduler is not enabled"(){
		given:
		sdrEmailSheduler.setEnabled(false)
		when:
		 sdrEmailSheduler.doScheduledTask(null, null)
		then:
		0*grTools.refreshMattView(0)
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Daily")

	}
}
