package com.bbb.commerce.catalog

import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException
import atg.service.email.EmailException
import atg.service.email.SMTPEmailSender

import java.sql.Timestamp;

import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO
import com.bbb.commerce.catalog.vo.BazaarVoiceVO
import com.bbb.constants.BBBCertonaConstants
import com.bbb.logging.LogMessageFormatter;

import spock.lang.specification.BBBExtendedSpec;

class BazaarVoiceManagerSpecification extends BBBExtendedSpec{

	BazaarVoiceManager man
	MutableRepository mRep =Mock()
	MutableRepository scRep =Mock()
	MutableRepositoryItem bazaarVoiceItem =Mock()
	SMTPEmailSender sender =Mock()

	def setup(){
		man = new BazaarVoiceManager()
		man.setBazaarVoiceRepository(mRep)
		man.setSender("sender")
		man.setRecipients()
		man.setSubject("errorMail")
		man.setEmailSender(sender)
		man.setScheduledRepository(scRep)
	}

	def"createUpdateProductBV , update the product review rating"(){

		given:
		man.setSendStatusReport(true)
		BazaarVoiceVO pBazaarVoiceVO = new BazaarVoiceVO()
		BazaarVoiceProductVO vo1 = new BazaarVoiceProductVO()
		BazaarVoiceProductVO vo2 = new BazaarVoiceProductVO()
		BazaarVoiceProductVO vo3 = new BazaarVoiceProductVO()
		pBazaarVoiceVO.setBazaarVoiceProduct([vo1,vo2,vo3])
		vo1.setId("Id1")
		vo1.setSiteId("tbs")
		vo1.setAverageOverallRating(50)
		vo1.setExternalId("extId")
		vo1.setTotalReviewCount(10)
		vo2.setId("Id2")
		vo2.setSiteId("Can")
		vo3.setId("Id3")
		vo3.setSiteId("US")

		1*mRep.getItemForUpdate("Id1" + ":"+ "tbs", BBBCatalogConstants.BAZAAR_VOICE) >> bazaarVoiceItem
		1*mRep.getItemForUpdate("Id2" + ":"+ "Can", BBBCatalogConstants.BAZAAR_VOICE) >> null
		1*mRep.createItem("Id2" + ":"+ "Can", BBBCatalogConstants.BAZAAR_VOICE) >> null
		1*mRep.getItemForUpdate("Id3" + ":"+ "US", BBBCatalogConstants.BAZAAR_VOICE) >> {throw new RepositoryException("")}
		1*sender.sendEmailMessage(man.getSender(), man.getRecipients(), man.getSubject(), _) >> {throw new EmailException("")}

		when:
		man.createUpdateProductBV(pBazaarVoiceVO)

		then:
		1*bazaarVoiceItem.setPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING, Float.valueOf(vo1.getAverageOverallRating()))
		1*bazaarVoiceItem.setPropertyValue(BBBCatalogConstants.EXTERNAL_ID,vo1.getExternalId());
		1*bazaarVoiceItem.setPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT,Integer.valueOf(vo1.getTotalReviewCount()))
		1*mRep.updateItem(bazaarVoiceItem)
		1*mRep.addItem(null)
		man.getFailedProductIdList() == ["Id3" :"Repository Exception while updating product id Id3"]
	}

	def"createUpdateProductBV , when BazaarVoiceProductVO list is empty"(){

		given:
		BazaarVoiceVO pBazaarVoiceVO = new BazaarVoiceVO()
		pBazaarVoiceVO.setBazaarVoiceProduct([])
		0*mRep.getItemForUpdate(_, BBBCatalogConstants.BAZAAR_VOICE)

		when:
		man.createUpdateProductBV(pBazaarVoiceVO)

		then:
		0*mRep.updateItem(_)
		0*mRep.addItem(null)
		man.getFailedProductIdList() == [:]
		0*sender.sendEmailMessage(man.getSender(), man.getRecipients(), man.getSubject(), _)
	}

	def"createUpdateProductBV , when Email is sent when repositoryException occurs"(){

		given:
		man.setSendStatusReport(true)
		BazaarVoiceVO pBazaarVoiceVO = new BazaarVoiceVO()
		BazaarVoiceProductVO vo3 = new BazaarVoiceProductVO()
		pBazaarVoiceVO.setBazaarVoiceProduct([vo3])
		vo3.setId("Id3")
		vo3.setSiteId("US")
		1*mRep.getItemForUpdate("Id3" + ":"+ "US", BBBCatalogConstants.BAZAAR_VOICE) >> {throw new RepositoryException("")}

		when:
		man.createUpdateProductBV(pBazaarVoiceVO)

		then:
		0*mRep.updateItem(_)
		0*mRep.addItem(null)
		man.getFailedProductIdList() == ["Id3" :"Repository Exception while updating product id Id3"]
		1*sender.sendEmailMessage(man.getSender(), man.getRecipients(), man.getSubject(), _)
	}

	def"updateScheduledRepository , updates the certona repository with the details of the scheduler run"(){

		given:
		Timestamp schedulerStartDate =Mock()
		MutableRepositoryItem certonaItem =Mock()
		String fullDataFeed = "dataFeed"
		String typeOfFeed ="typeFeed"
		boolean status = true
		1*scRep.createItem(BBBCertonaConstants.FEED) >> certonaItem

		when:
		man.updateScheduledRepository(schedulerStartDate,fullDataFeed,typeOfFeed,status)

		then:
		1*certonaItem.setPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE, schedulerStartDate)
		1*certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_COMPLETION_DATE, _)
		1*certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_START_DATE, schedulerStartDate)
		1*certonaItem.setPropertyValue(BBBCertonaConstants.STATUS, Boolean.valueOf(status))
		1*certonaItem.setPropertyValue(BBBCertonaConstants.TYPE_OF_FEED, typeOfFeed)
		1*certonaItem.setPropertyValue(BBBCertonaConstants.MODE, fullDataFeed)
		1*scRep.addItem(certonaItem)
	}

	def"updateScheduledRepository , when RepositoryException is thrown"(){

		given:
		man =Spy()
		man.setFailedProductIdList([:])
		man.setScheduledRepository(scRep)
		Timestamp schedulerStartDate =Mock()
		MutableRepositoryItem certonaItem =Mock()
		String fullDataFeed = "dataFeed"
		String typeOfFeed ="typeFeed"
		boolean status = true
		1*scRep.createItem(BBBCertonaConstants.FEED) >> {throw new RepositoryException("")}

		when:
		man.updateScheduledRepository(schedulerStartDate,fullDataFeed,typeOfFeed,status)

		then:
		0*certonaItem.setPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE, schedulerStartDate)
		0*certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_COMPLETION_DATE, _)
		0*certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_START_DATE, schedulerStartDate)
		0*certonaItem.setPropertyValue(BBBCertonaConstants.STATUS, Boolean.valueOf(status))
		0*certonaItem.setPropertyValue(BBBCertonaConstants.TYPE_OF_FEED, typeOfFeed)
		0*certonaItem.setPropertyValue(BBBCertonaConstants.MODE, fullDataFeed)
		0*scRep.addItem(certonaItem)
		1*man.logError("catalog_1072: BazaarVoiceManager.updateScheduledRepository() | RepositoryException ",_)
	}
}
