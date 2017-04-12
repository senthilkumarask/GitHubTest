package com.bbb.selfservice.droplet

import com.bbb.account.validatecoupon.ValidateCouponResponseVO
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.droplet.BBBPromotionTools
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.webservices.vo.ErrorStatus

import spock.lang.specification.BBBExtendedSpec

class ProcessCouponDropletSpecification extends BBBExtendedSpec {
	
	def ProcessCouponDroplet pcDrolet 
	def BBBCatalogToolsImpl cTools = Mock() 
	def ErrorStatus errorStatus = Mock()
	def BBBPromotionTools pTools = Mock()
	ValidateCouponResponseVO couponResponseVO = new ValidateCouponResponseVO()
	 
	def setup() {
		pcDrolet = new ProcessCouponDroplet(catalogTools : cTools, promTools : pTools)
	}
	
	def "service. TC when request type is status" () {
		given:
		    pcDrolet = Spy()
		    pcDrolet.setCatalogTools(cTools)
			pcDrolet.setPromTools(pTools)
			
			requestMock.getParameter("requestType") >> "status"
			requestMock.getParameter("siteId") >> "usBed"
			
			//getCouponRequestVO
			3*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> ["true"]
			3*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["true"]
			requestMock.getParameter("serviceName") >> "serName"
			requestMock.getParameter("entryCd") >> "entryCd"
			requestMock.getParameter("emailAddr") >> "harry@gmial.com"
			requestMock.getParameter("Mobile Number") >> "9350001442"
			
			//
			
			1*pcDrolet.invokeService(_) >> couponResponseVO
			couponResponseVO.setCouponStatus("Activated")
			couponResponseVO.setStatus(errorStatus)
			
			errorStatus.isErrorExists() >> false
			
			//getCouponDetailsFromCatalog
			sessionMock.getAttribute("couponStatus") >> "Not Activated"
			1*cTools.getPromotionId("entryCd") >> "proId"
			requestMock.getLocale() >> {return new Locale("en_US")}
			1*pTools.getPromotionCouponKey("proId","activatedText", _, _, false) >> "cmsContent"
			
			requestMock.getParameter("cmsContent") >> "content"
		
		when:
			pcDrolet.service(requestMock, responseMock)
		then:
		 1*requestMock.setParameter("cmsContent", "cmsContent");
		 1*requestMock.setParameter("couponStatus", "Pre Activated");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)

	}
	
	
	def "service. TC when request type is activate" () {
		given:
			
			requestMock.getParameter("requestType") >> "activate"
			requestMock.getParameter("siteId") >> "usBed"
			
			//getCouponRequestVO
			3*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> ["true"]
			2*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> []
			requestMock.getParameter("serviceName") >> "serName"
			requestMock.getParameter("entryCd") >> "entryCd"
			requestMock.getParameter("emailAddr") >> "harry@gmial.com"
			requestMock.getParameter("Mobile Number") >> "9350001442"
			
		when:
			pcDrolet.service(requestMock, responseMock)
		then:
				1*requestMock.setParameter("cmsError","error");
				1*requestMock.setParameter("couponStatus","error");
				1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
				0*pcDrolet.invokeService(_)

	}
	
	def "service. TC when request type in not status and activate" () {
		given:
		
			requestMock.getParameter("requestType") >> "wrongStatus"
			
		when:
			pcDrolet.service(requestMock, responseMock)
		then:
		    0*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed")
			0*requestMock.setParameter("cmsError", "CouponUnknownError")
			0*requestMock.setParameter("couponStatus","error");
			0*requestMock.serviceLocalParameter("error", requestMock, responseMock)

	}
	
	def "service. TC when request type is null" () {
		given:
		
			requestMock.getParameter("requestType") >> null
			
		when:
			pcDrolet.service(requestMock, responseMock)
		then:
			0*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed")
			0*requestMock.setParameter("cmsError", "CouponUnknownError")
			0*requestMock.setParameter("couponStatus","error");
			0*requestMock.serviceLocalParameter("error", requestMock, responseMock)

	}
	
	
	def "service. TC for BBBBusinessException" () {
		given:
			pcDrolet = Spy()
		
			requestMock.getParameter("requestType") >> "activate"
			requestMock.getParameter("siteId") >> "usBed"
			
			//getCouponRequestVO
            pcDrolet.validateCoupon(requestMock, responseMock, "activate")	>> {throw new BBBBusinessException("exception")}		
		when:
			pcDrolet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("cmsError", "CouponUnknownError")
			1*requestMock.setParameter("couponStatus","error");
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)

	}
	
	def "service. TC for BBBSystemException" () {
		given:
			pcDrolet = Spy()
		
			requestMock.getParameter("requestType") >> "activate"
			requestMock.getParameter("siteId") >> "usBed"
			
			//getCouponRequestVO
			pcDrolet.validateCoupon(requestMock, responseMock, "activate")	>> {throw new BBBSystemException("exception")}
		when:
			pcDrolet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("cmsError", "CouponUnknownError")
			1*requestMock.setParameter("couponStatus","error");
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)

	}
	
	/************************************************* validateCoupon ************************************************/
	
	def "validateCoupon. tc when WSDLSiteFlags flag is empty" (){
		given:
		
		requestMock.getParameter("requestType") >> "status"
		requestMock.getParameter("siteId") >> "usBed"
		
		//getCouponRequestVO
		2*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> []
		1*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> null
		requestMock.getParameter("serviceName") >> "serName"
		requestMock.getParameter("entryCd") >> "entryCd"
/*		requestMock.getParameter("emailAddr") >> "harry@gmial.com"
		requestMock.getParameter("Mobile Number") >> "9350001442"
		
		//
		
		1*pcDrolet.invokeService(_) >> couponResponseVO
		couponResponseVO.setCouponStatus("Activated")
		couponResponseVO.setStatus(errorStatus)
		
		errorStatus.isErrorExists() >> false
		
		//getCouponDetailsFromCatalog
		sessionMock.getAttribute("couponStatus") >> "Not Activated"
		1*cTools.getPromotionId("entryCd") >> "proId"
		requestMock.getLocale() >> {return new Locale("en_US")}
		1*pTools.getPromotionCouponKey("proId","activatedText", _, _, false) >> "cmsContent"
		
*/		requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		1*requestMock.setParameter("cmsError","error");
		1*requestMock.setParameter("couponStatus","error");
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		0*cTools.getPromotionId("entryCd")

	}
	
	def "validateCoupon. tc when entryCd is null " (){
		given:
		
		requestMock.getParameter("requestType") >> "status"
		requestMock.getParameter("siteId") >> "usBed"
		
		3*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> ["true"]
		1*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> null
		requestMock.getParameter("serviceName") >> "serName"
		requestMock.getParameter("entryCd") >> null
		when:
		
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		 
		then:
		1*requestMock.setParameter("cmsError","error");
		1*requestMock.setParameter("couponStatus","error");
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		0*cTools.getPromotionId(_)
	}
	
	def "validateCoupon. tc when WSDLSiteFlags is null " (){
		given:
		
		requestMock.getParameter("requestType") >> "status"
		requestMock.getParameter("siteId") >> "usBed"
		
		1*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> null
		1*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> null
		requestMock.getParameter("serviceName") >> "serName"
		requestMock.getParameter("entryCd") >> null
		when:
		
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		 
		then:
		1*requestMock.setParameter("cmsError","error");
		1*requestMock.setParameter("couponStatus","error");
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		0*cTools.getPromotionId(_)
	}
	

	
	
	
	def "validateCoupon. tc when coupon status is not activated" (){
		given:
        setSpy()
		
		requestMock.getParameter("requestType") >> "status"
		requestMock.getParameter("siteId") >> "usBed"
		setcommonMock()
		1*cTools.getPromotionId("entryCd") >> "proId"
		couponResponseVO.setCouponStatus("Not Activated")
		couponResponseVO.setStatus(errorStatus)
		
		errorStatus.isErrorExists() >> false
		
		//getCouponDetailsFromCatalog
		sessionMock.getAttribute("couponStatus") >> "Not Activated"
		1*pTools.getPromotionCouponKey("proId","activationText", _, _, false) >> "cmsContent"
		
      	requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 1*requestMock.setParameter("cmsContent", "cmsContent");
		 1*requestMock.setParameter("couponStatus", "Not Activated");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
         1*sessionMock.setAttribute("couponStatus", "Not Activated")
	}
	
	def "validateCoupon. tc when request type  is not activate and status" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "wrong"
		requestMock.getParameter("siteId") >> "usBed"
		setcommonMock()
		1*cTools.getPromotionId("entryCd") >> "proId"
		couponResponseVO.setCouponStatus("Activated")
		couponResponseVO.setStatus(errorStatus)
		
		errorStatus.isErrorExists() >> false
		
		//getCouponDetailsFromCatalog
		sessionMock.getAttribute("couponStatus") >> "Not Activated"
		1*pTools.getPromotionCouponKey("proId","activatedText", _, _, false) >> "cmsContent"
		
		  requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 1*requestMock.setParameter("cmsContent", "cmsContent");
		 1*requestMock.setParameter("couponStatus", "Activated");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", "Activated")
	}
	
	def "validateCoupon. tc when request type  is  activated and coupon status is also activated" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		setcommonMock()
		1*cTools.getPromotionId("entryCd") >> "proId"
		couponResponseVO.setCouponStatus("Activated")
		couponResponseVO.setStatus(errorStatus)
		
		errorStatus.isErrorExists() >> false
		
		//getCouponDetailsFromCatalog
		sessionMock.getAttribute("couponStatus") >> "Not Activa"
		1*pTools.getPromotionCouponKey("proId","activatedText", _, _, false) >> "cmsContent"
		
		  requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 1*requestMock.setParameter("cmsContent", "cmsContent");
		 1*requestMock.setParameter("couponStatus", "Already Activated");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", "Activated")
	}
	
	def "validateCoupon. tc when coupon status   is  redemed" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		setcommonMock()
		couponResponseVO.setCouponStatus("Redeemed")
		couponResponseVO.setStatus(errorStatus)
		
		errorStatus.isErrorExists() >> false
		
		//getCouponDetailsFromCatalog
		sessionMock.getAttribute("couponStatus") >> "Not Activa"
		
		  requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 1*requestMock.setParameter("cmsContent", "Redeemed")
		 1*requestMock.setParameter("couponStatus", "Redeemed");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", "Activated")
		 0*cTools.getPromotionId("entryCd")
		 0*pTools.getPromotionCouponKey(_,_, _, _, _)
	}
	
	def "validateCoupon. tc when coupon session status is  not active" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		couponResponseVO.setCouponStatus("Expired")
		couponResponseVO.setStatus(errorStatus)
		setcommonMock()
		errorStatus.isErrorExists() >> false
		
		//getCouponDetailsFromCatalog
		sessionMock.getAttribute("couponStatus") >> "Not Activated"
		
		1*cTools.getPromotionId("entryCd") >> "proId"
		1*pTools.getPromotionCouponKey("proId","activatedText", _, _, true) >> "cmsContent"
		
		  requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 1*requestMock.setParameter("cmsContent", "cmsContent")
		 1*requestMock.setParameter("couponStatus", "Expired");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 1*sessionMock.setAttribute("couponStatus", "Activated")
	}
	
	def "validateCoupon. tc when coupon session status is   activated" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		couponResponseVO.setCouponStatus("nnExpired")
		couponResponseVO.setStatus(errorStatus)
		setcommonMock()
		errorStatus.isErrorExists() >> false
		
		//getCouponDetailsFromCatalog
		sessionMock.getAttribute("couponStatus") >> "Activated"
		
		1*cTools.getPromotionId("entryCd") >> "proId"
		1*pTools.getPromotionCouponKey("proId","activatedText", _, _, false) >> "cmsContent"
		
		  requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 1*requestMock.setParameter("cmsContent", "cmsContent")
		 1*requestMock.setParameter("couponStatus", "Already Activated");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", _)
	}
	
	def "validateCoupon. tc when coupon session status is   null" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		couponResponseVO.setCouponStatus("nnExpired")
		couponResponseVO.setStatus(errorStatus)
		setcommonMock()
		errorStatus.isErrorExists() >> false
		
		sessionMock.getAttribute("couponStatus") >> null
 	    requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", _)
	}
	
	def "validateCoupon. tc when request type is   null" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> null
		requestMock.getParameter("siteId") >> "usBed"
		couponResponseVO.setCouponStatus("nnExpired")
		couponResponseVO.setStatus(errorStatus)
		setcommonMock()
		errorStatus.isErrorExists() >> false
		
		sessionMock.getAttribute("couponStatus") >> null
		 requestMock.getParameter("cmsContent") >> "content"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
		 1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", _)
	}
	
	def "validateCoupon. tc when coupon status of response VO is   null" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		couponResponseVO.setCouponStatus(null)
		couponResponseVO.setStatus(errorStatus)
		setcommonMock()
		errorStatus.isErrorExists() >> false
		errorStatus.getErrorId() >> 1
		
		 requestMock.getParameter("cmsContent") >> null
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
		 0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", _)
	}
	
	def "validateCoupon. tc when error exists is true" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		couponResponseVO.setCouponStatus(null)
		couponResponseVO.setStatus(errorStatus)
		setcommonMock()
		errorStatus.isErrorExists() >> true
		errorStatus.getErrorId() >> 1
		
		 requestMock.getParameter("cmsContent") >> null
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
		 0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", _)
	}
	
	def "validateCoupon. tc when  response VO is   null" (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		couponResponseVO.setCouponStatus(null)
		couponResponseVO.setStatus(errorStatus)
		3*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> ["true"]
		3*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["true"]
		requestMock.getParameter("serviceName") >> "serName"
		requestMock.getParameter("entryCd") >> "entryCd"
		1*pcDrolet.invokeService(_) >> null
		requestMock.getLocale() >> {return new Locale("en_US")}

		
		errorStatus.isErrorExists() >> false
		errorStatus.getErrorId() >> 1
		
		 requestMock.getParameter("cmsContent") >> null
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "status")
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
		 0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		 1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		 0*sessionMock.setAttribute("couponStatus", _)
	}
	
	private setcommonMock(){
		3*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> ["true"]
		3*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["true"]
		requestMock.getParameter("serviceName") >> "serName"
		requestMock.getParameter("entryCd") >> "entryCd"
		1*pcDrolet.invokeService(_) >> couponResponseVO
		requestMock.getLocale() >> {return new Locale("en_US")}
		
		

	}
	
	private setSpy(){
		pcDrolet = Spy()
		pcDrolet.setCatalogTools(cTools)
		pcDrolet.setPromTools(pTools)

	}
	
	def "validateCoupon. tc when  request type is not status and acitvate " (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, "notstatus")
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
	}
	
	def "validateCoupon. tc when  request type is null " (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		when:
		 pcDrolet.validateCoupon(requestMock, responseMock, null)
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
	}
	
	def "validateCoupon. tc when  response object  is null " (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> "usBed"
		when:
		 pcDrolet.validateCoupon(requestMock, null, null)
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
	}
	
	def "validateCoupon. tc when  site id  is null " (){
		given:
		setSpy()
		
		requestMock.getParameter("requestType") >> "activate"
		requestMock.getParameter("siteId") >> null
		when:
		 pcDrolet.validateCoupon(requestMock, null, null)
		then:
		 0*requestMock.setParameter("cmsContent", "cmsContent")
		 0*requestMock.setParameter("couponStatus", "Already Activated");
	}
	
	
	/************************************************* addErrorToReqObj ********************************************/
	
	def"addErrorToReqObj. tc for 401 error"(){
		given:
		   
		when:
			pcDrolet.addErrorToReqObj(401, requestMock)
		then:
		1*requestMock.setParameter("cmsError", "EmailError")
		0*requestMock.setParameter("cmsError", "CouponActivateError")
		0*requestMock.setParameter("couponStatus", "error")
		0*requestMock.setParameter("cmsError", "CouponUnknownError")
	}
	
	def"addErrorToReqObj. tc for 402 error"(){
		given:
		   
		when:
			pcDrolet.addErrorToReqObj(402, requestMock)
		then:
		0*requestMock.setParameter("cmsError", "EmailError")
		1*requestMock.setParameter("cmsError", "CouponActivateError")
		1*requestMock.setParameter("couponStatus", "error")
		0*requestMock.setParameter("cmsError", "CouponUnknownError")
	}
	
	def"addErrorToReqObj. tc for rest error"(){
		given:
		   
		when:
			pcDrolet.addErrorToReqObj(400000, requestMock)
		then:
		0*requestMock.setParameter("cmsError", "EmailError")
		0*requestMock.setParameter("cmsError", "CouponActivateError")
		1*requestMock.setParameter("couponStatus", "error")
		1*requestMock.setParameter("cmsError", "CouponUnknownError")
	}
}
