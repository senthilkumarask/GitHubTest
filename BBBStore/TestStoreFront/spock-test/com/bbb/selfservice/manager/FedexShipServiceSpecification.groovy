package com.bbb.selfservice.manager

import java.util.Map;

import atg.multisite.SiteContext;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.fedex.CreatePendingShipmentReqBuilder;
import com.bbb.fedex.ProcessShipmentReqBuilder
import com.bbb.fedex.vo.CreatePendingShipmentResVo
import com.bbb.fedex.vo.ProcessShipmentResVo
import com.fedex.ws.openship.v7.CreateOpenShipmentReply
import com.fedex.ws.openship.v7.CreateOpenShipmentRequest
import com.fedex.ws.openship.v7.PendingShipmentAccessDetail;
import com.fedex.ws.openship.v7.PendingShipmentAccessorDetail;
import com.fedex.ws.openship.v7.PendingShipmentDetail;
import com.fedex.ws.ship.v15.CompletedPackageDetail
import com.fedex.ws.ship.v15.CompletedShipmentDetail
import com.fedex.ws.ship.v15.Notification
import com.fedex.ws.ship.v15.NotificationSeverityType;
import com.fedex.ws.ship.v15.ProcessShipmentReply
import com.fedex.ws.ship.v15.ProcessShipmentRequest
import com.fedex.ws.ship.v15.ShippingDocument1
import com.fedex.ws.ship.v15.ShippingDocumentPart
import com.fedex.ws.ship.v15.TrackingId

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class FedexShipServiceSpecification extends BBBExtendedSpec {
	
	FedexShipService testObj
	BBBCatalogTools bbbCatalogToolsMock = Mock()
	SiteContext siteContextMock = Mock()
	ProcessShipmentRequest processShipmentRequestMock = Mock()
	ProcessShipmentResVo processShipmentResVoMock = Mock()
	ProcessShipmentReqBuilder processShipmentReqBuilderMock = Mock()
	ProcessShipmentReply processShipmentReplyMock = Mock()
	CompletedShipmentDetail completedShipmentDetailMock = Mock()
	CompletedPackageDetail completedPackageDetailMock = Mock()
	ShippingDocument1 shippingDocument1Mock = Mock()
	ShippingDocumentPart shippingDocumentPartMock = Mock()
	Notification notificationMock = Mock()
	CreatePendingShipmentReqBuilder createPendingShipmentReqBuilderMock = Mock()
	CreateOpenShipmentRequest createOpenShipmentRequestMock = Mock()
	CreatePendingShipmentResVo createPendingShipmentResVoMock = Mock()
	PendingShipmentAccessorDetail pendingShipmentAccessorDetailMock = Mock()
	PendingShipmentAccessDetail pendingShipmentAccessDetailMock = Mock()
	CreateOpenShipmentReply createOpenShipmentReplyMock = Mock()
	com.fedex.ws.openship.v7.CompletedShipmentDetail completedShipmentDetailV7Mock = Mock()
	com.fedex.ws.openship.v7.Notification Notificationv7Mock =Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	Map<String,String> mapValues = ["US":"BedBathUS","Canada":"BedBathCanada"]
	
	def setup(){
		testObj = Spy()
	}
	
	////////////////////////////////////////TestCases for getLabel --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public Map<String , Object>  getLabel(Map<String , String> pMap , BBBCatalogTools pBBBCatalogTools ,SiteContext pSiteContext) ///////////
	
	def"getLabel. This TC is the Happy flow of getLabel method"(){
		given:
			testObj.buildRequestGetLabel(*_) >> processShipmentRequestMock
			testObj.invokeServiceHandler(_) >> processShipmentResVoMock
			processShipmentResVoMock.getProcessShipmentReply() >> processShipmentReplyMock
			processShipmentReplyMock.getHighestSeverity() >> NotificationSeverityType.WARNING
			processShipmentReplyMock.getCompletedShipmentDetail() >> completedShipmentDetailMock
			CompletedPackageDetail completedPackageDetailMock1 = Mock()
			completedShipmentDetailMock.getCompletedPackageDetailsArray() >> [completedPackageDetailMock,completedPackageDetailMock1]
			completedPackageDetailMock.getLabel() >> shippingDocument1Mock
			ShippingDocument1 shippingDocument1Mock1 = Mock()
			completedPackageDetailMock1.getLabel() >> shippingDocument1Mock1
			ShippingDocumentPart shippingDocumentPartMock1 = Mock()
			ShippingDocumentPart shippingDocumentPartMock2 = Mock()
			shippingDocument1Mock.getPartsArray() >> [shippingDocumentPartMock]
			shippingDocument1Mock1.getPartsArray() >> [shippingDocumentPartMock1,shippingDocumentPartMock2]
			shippingDocumentPartMock.getImage() >> null
			shippingDocumentPartMock1.getImage() >> null
			shippingDocumentPartMock2.getImage() >> [2]
			TrackingId trackingIdMock = Mock()
			completedPackageDetailMock1.getTrackingIdsArray(0) >> trackingIdMock
			1 * trackingIdMock.getTrackingNumber() >> "12345"
			
		when:
			Map<String,Object> results = testObj.getLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == ["trackingNumber":"FedEx12345.pdf","success": [2]]
	}
	
	def"getLabel. This TC is the when getHighestSeverity is false"(){
		given:
			testObj.buildRequestGetLabel(*_) >> processShipmentRequestMock
			testObj.invokeServiceHandler(_) >> processShipmentResVoMock
			processShipmentResVoMock.getProcessShipmentReply() >> processShipmentReplyMock
			processShipmentReplyMock.getHighestSeverity() >> null
			processShipmentReplyMock.getNotificationsArray() >> [notificationMock]
		
		when:
			Map<String,Object> results = testObj.getLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == ["failure":[notificationMock]]
	}
	
	def"getLabel. This TC is the when getHighestSeverity is false and getNotificationsArray returns null"(){
		given:
			testObj.buildRequestGetLabel(*_) >> processShipmentRequestMock
			testObj.invokeServiceHandler(_) >> processShipmentResVoMock
			processShipmentResVoMock.getProcessShipmentReply() >> processShipmentReplyMock
			processShipmentReplyMock.getHighestSeverity() >> null
			processShipmentReplyMock.getNotificationsArray() >> null
		
		when:
			Map<String,Object> results = testObj.getLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == null
	}
	
	def"getLabel. This TC is the when getCompletedPackageDetailsArray is empty"(){
		given:
			testObj.buildRequestGetLabel(*_) >> processShipmentRequestMock
			testObj.invokeServiceHandler(_) >> processShipmentResVoMock
			processShipmentResVoMock.getProcessShipmentReply() >> processShipmentReplyMock
			processShipmentReplyMock.getHighestSeverity() >> NotificationSeverityType.WARNING
			processShipmentReplyMock.getCompletedShipmentDetail() >> completedShipmentDetailMock
			completedShipmentDetailMock.getCompletedPackageDetailsArray() >> []
		
		when:
			Map<String,Object> results = testObj.getLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == null
	}
	
	def"getLabel. This TC is the when Exception thrown"(){
		given:
			testObj.buildRequestGetLabel(*_) >> processShipmentRequestMock
			testObj.invokeServiceHandler(_) >> {throw new Exception("Mock for Exception")}

		when:
			Map<String,Object> results = testObj.getLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == null
			1 * testObj.logError('Mock for Exception', _)
			
	}
	
	////////////////////////////////////////TestCases for getLabel --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for sendEmailLabel --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public Map<String , Object> sendEmailLabel(Map<String , String> pMap , BBBCatalogTools pBBBCatalogTools ,SiteContext pSiteContext ) ///////////
	
	def"sendEmailLabel.This TC is the Happy flow of sendEmailLabel method"(){
		given:
			testObj.buildRequestPendingShipment(*_) >> createOpenShipmentRequestMock
			testObj.createPendingService(_) >> createPendingShipmentResVoMock
			createPendingShipmentResVoMock.getCreateOpenShipmentReply() >> createOpenShipmentReplyMock
			createOpenShipmentReplyMock.getHighestSeverity() >> com.fedex.ws.openship.v7.NotificationSeverityType.WARNING
			createOpenShipmentReplyMock.getCompletedShipmentDetail() >> completedShipmentDetailV7Mock
			completedShipmentDetailV7Mock.getAccessDetail() >> pendingShipmentAccessDetailMock
			PendingShipmentAccessorDetail pendingShipmentAccessorDetailMock1 = Mock()
			pendingShipmentAccessDetailMock.getAccessorDetailsArray() >> [pendingShipmentAccessorDetailMock,pendingShipmentAccessorDetailMock1]
			pendingShipmentAccessorDetailMock.getEmailLabelUrl() >> "Customer"
			pendingShipmentAccessorDetailMock1.getEmailLabelUrl() >> "BBB"
			
		when:
			Map<String , Object> results = testObj.sendEmailLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == ["Url":"Customer","Url": "BBB"]
	}
	
	def"sendEmailLabel.This TC is when getHighestSeverity is false"(){
		given:
			testObj.buildRequestPendingShipment(*_) >> createOpenShipmentRequestMock
			testObj.createPendingService(_) >> createPendingShipmentResVoMock
			createPendingShipmentResVoMock.getCreateOpenShipmentReply() >> createOpenShipmentReplyMock
			createOpenShipmentReplyMock.getHighestSeverity() >> null
			createOpenShipmentReplyMock.getNotificationsArray() >> [Notificationv7Mock]
			
		when:
			Map<String , Object> results = testObj.sendEmailLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == ["failure":[Notificationv7Mock]]
			results.size() == 1
			results.get("failure").size() == 1
	}
	
	def"sendEmailLabel.This TC is when getHighestSeverity is false and getNotificationsArray returns null"(){
		given:
			testObj.buildRequestPendingShipment(*_) >> createOpenShipmentRequestMock
			testObj.createPendingService(_) >> createPendingShipmentResVoMock
			createPendingShipmentResVoMock.getCreateOpenShipmentReply() >> createOpenShipmentReplyMock
			createOpenShipmentReplyMock.getHighestSeverity() >> null
			createOpenShipmentReplyMock.getNotificationsArray() >> null
			
		when:
			Map<String , Object> results = testObj.sendEmailLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == null
	}
	
	def"sendEmailLabel.This TC is when BBBBusinessException thrown"(){
		given:
			testObj.buildRequestPendingShipment(*_) >> createOpenShipmentRequestMock		
			testObj.createPendingService(_) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			Map<String , Object> results = testObj.sendEmailLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == null
			1 * testObj.logError('Mock for BBBBusinessException', _)
	}
	
	def"sendEmailLabel.This TC is when BBBSystemException thrown"(){
		given:
			testObj.buildRequestPendingShipment(*_) >> createOpenShipmentRequestMock
			testObj.createPendingService(_) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			Map<String , Object> results = testObj.sendEmailLabel(mapValues, bbbCatalogToolsMock, siteContextMock)
		then:
			results == null
				1 * testObj.logError('Mock for BBBSystemException', _)
	}
	
	////////////////////////////////////////TestCases for sendEmailLabel --> ENDS//////////////////////////////////////////////////////////

}
