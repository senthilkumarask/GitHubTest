package com.bbb.commerce.giftregistry.scheduler

import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;

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

class SendWeeklyRecommEmailSchedulerSpecification extends BBBExtendedSpec {

	SendWeeklyRecommEmailScheduler sres 
	GiftRegistryTools grTools = Mock()
	RepositoryItem rcomEmail = Mock()
	Nucleus nucluseMock = Mock()
	HeadPipelineServlet hpServlet = Mock()
	GiftRegistryRecommendationManager grRecManager = Mock()

	
	
	def setup(){
		sres = new SendWeeklyRecommEmailScheduler(giftRegistryTools : grTools,batchSize : 3,giftRegistryRecommendationManager : grRecManager, enabled : true)
		
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
		1*grTools.fetchRecomEmail(0,3,"RecommendationEmailWeekly") >> [rcomEmail]
		
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
		sres.executeDoScheduledTask()
		then:
		
		1*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		1*requestMock.setParameter("configurableType", "Weekly")
		1*requestMock.setParameter("isFromScheduler", "true")
		1*requestMock.setParameter("siteId", "usBed")
		1*grRecManager.sendRegistrantScheduledBulkEmail(_)
		1*grTools.refreshMattView(1)
		1*grTools.refreshRepoItemCache("RecommendationEmailWeekly")

	}
	
	def"doScheduledTask.TC when batch size is one"(){
		given:
		sres.setBatchSize(1)
		1*grTools.fetchRecomEmail(0,1,"RecommendationEmailWeekly") >> [rcomEmail]
		
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
		 sres.doScheduledTask(null, null)
		then:
		1*grTools.refreshRepoItemCache("RecommendationEmailWeekly")
		1*grTools.refreshMattView(1)
		1*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		1*requestMock.setParameter("configurableType", "Weekly")
		1*requestMock.setParameter("isFromScheduler", "true")
		1*requestMock.setParameter("siteId", "usBed")
		1*grRecManager.sendRegistrantScheduledBulkEmail(_)

	}
	
	def"doScheduledTask.TC for BBBSystemException"(){
		given:
		sres.setBatchSize(1)
		1*grTools.refreshRepoItemCache("RecommendationEmailWeekly")	>> {throw new BBBSystemException("exception")}
		when:
		 sres.doScheduledTask(null, null)
		then:
		
		1*grTools.refreshMattView(1)
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Weekly")

	}
	
	def"doScheduledTask.TC for BBBBusinessException"(){
		given:
		sres.setBatchSize(1)
		1*grTools.refreshRepoItemCache("RecommendationEmailWeekly")	>> {throw new BBBBusinessException("exception")}
		when:
		 sres.doScheduledTask(null, null)
		then:
		
		1*grTools.refreshMattView(1)
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Weekly")

	}
	
	def"doScheduledTask.TC for SQLException"(){
		given:
		//sdrEmailSheduler = Spy()
		sres.setBatchSize(1)
		1*grTools.refreshMattView(1)	>> {throw new SQLException("Registry not found")}
		when:
		 sres.doScheduledTask(null, null)
		then:
		//1*sdrEmailSheduler.logInfo("SendDailyRecommEmailScheduler doScheduledTask started :: Registry not found")
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Weekly")

	}
	
	def"doScheduledTask.TC for SQLException when error message is not 'Registry not found'"(){
		given:
		//sdrEmailSheduler = Spy()
		sres.setBatchSize(1)
		1*grTools.refreshMattView(1)	>> {throw new SQLException("Registr")}
		when:
		 sres.doScheduledTask(null, null)
		then:
		
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Weekly")

	}
	
	def"doScheduledTask.TC for SQLException when error message is null"(){
		given:
		//sdrEmailSheduler = Spy()
		sres.setBatchSize(1)
		1*grTools.refreshMattView(1)	>> {throw new SQLException()}
		when:
		 sres.doScheduledTask(null, null)
		then:
		
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Weekly")

	}
	
	def"doScheduledTask.TC when sheduler is not enabled"(){
		given:
		sres.setEnabled(false)
		when:
		 sres.doScheduledTask(null, null)
		then:
		0*grTools.refreshMattView(1)
		0*requestMock.setParameter("emailType", "RegistrantScheduledBulkEmail")
		0*requestMock.setParameter("configurableType", "Weekly")

	}
}
