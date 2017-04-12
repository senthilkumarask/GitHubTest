package com.bbb.commerce.checkout.droplet

import atg.multisite.SiteContextManager;
import atg.userprofiling.Profile
import com.bbb.account.api.BBBCreditCardAPIImpl
import com.bbb.commerce.checkout.BBBCreditCardContainer
import com.bbb.commerce.common.BasicBBBCreditCardInfo
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCheckoutConstants
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.rest.checkout.vo.CreditCardInfoVO
import spock.lang.specification.BBBExtendedSpec
import atg.repository.MutableRepositoryItem
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.servlet.ServletUtil;


class GetCreditCardsForPaymentSpecification extends BBBExtendedSpec {
	def GetCreditCardsForPayment ccPaymentObject 
	def Profile profileMock = Mock()
	def BBBOrderImpl orderMock = Mock()
	def BBBCreditCardContainer ccContainerMock = Mock()
	def BBBPurchaseProcessHelper processHelperMock = Mock()
	def BasicBBBCreditCardInfo creditCardInfo = Mock()
	def BasicBBBCreditCardInfo scnCreditCardInfo = Mock()
	def Repository repositoryMock = Mock()
	def RepositoryView view = Mock()
	def MutableRepositoryItem addressItemMock = Mock()
	def RepositoryItem itemMock = Mock()
	
	def BBBCreditCardAPIImpl ccApiImplMock = Mock()
	def setup(){
		ccPaymentObject =  new GetCreditCardsForPayment(purchaseProcessHelper : processHelperMock, creditCardAPIImpl:ccApiImplMock)
	}
	
	def"TC to check credit catr info list added in request parameter, gets from order and profile "(){
		given:
		ccPaymentObject = Spy()
		ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
		ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)
		
		requestMock.getObjectParameter("Profile") >> profileMock
		requestMock.getObjectParameter("Order")  >>  orderMock
		requestMock.getObjectParameter("CreditCardContainer") >> ccContainerMock
		
		creditCardInfo.getPaymentId() >> "pay123"
		creditCardInfo.getExpired() >> false
		
		creditCardInfo.getCreditCardNumber() >> "123456"
		creditCardInfo.getExpirationYear() >> "2017"
		creditCardInfo.getExpirationMonth() >> "DEC"
		1*ccPaymentObject.extractGetCreditCardFromOrder(orderMock) >> [creditCardInfo]
		//credit card info geted from userProfile
		scnCreditCardInfo.getExpirationYear() >> "2018"
		scnCreditCardInfo.getExpirationMonth() >> "NOV"
		scnCreditCardInfo.getCreditCardNumber() >> "456"
		scnCreditCardInfo.isDefault() >> false
		//scnCreditCardInfo.getPaymentId() >> "456"
		1*ccApiImplMock.getUserCreditCardWallet(profileMock, null) >> [scnCreditCardInfo]
		
		when:
		ccPaymentObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("creditCardAmount", 0.0);
		1*requestMock.setParameter( "creditCardInfo",[scnCreditCardInfo]);
		1*requestMock.setParameter("selectedId", "pay123");
		1*requestMock.serviceParameter("output", requestMock, responseMock);

	}
	
	def"TC to check credit card info list added in request parameter when credit card info gets from order is expired and isDefault is false"(){
		given:
		ccPaymentObject = Spy()
		ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
		ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)
		
		requestMock.getObjectParameter("Profile") >> profileMock
		requestMock.getObjectParameter("Order")  >>  orderMock
		requestMock.getObjectParameter("CreditCardContainer") >> ccContainerMock
		
		creditCardInfo.getExpired() >> true
		
		creditCardInfo.getCreditCardNumber() >> "123456"
		creditCardInfo.getExpirationYear() >> "2017"
		creditCardInfo.getExpirationMonth() >> "DEC"
		1*ccPaymentObject.extractGetCreditCardFromOrder(orderMock) >> []
		//credit card info geted from userProfile
		scnCreditCardInfo.getExpirationYear() >> "2018"
		scnCreditCardInfo.getExpirationMonth() >> "DEC"
		scnCreditCardInfo.getCreditCardNumber() >> "123456"
		scnCreditCardInfo.isDefault() >> false
		scnCreditCardInfo.getPaymentId() >> "456"
		1*ccApiImplMock.getUserCreditCardWallet(profileMock, null) >> [scnCreditCardInfo]
		
		when:
		ccPaymentObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("creditCardAmount", 0.0);
		1*requestMock.setParameter( "creditCardInfo",[scnCreditCardInfo]);
		1*requestMock.setParameter("selectedId", null);
		1*requestMock.serviceParameter("output", requestMock, responseMock);

	}
	
	def"TC to check credit card info list added in request parameter when credit card info gets from order is empty"(){
		given:
		ccPaymentObject = Spy()
		ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
		ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)
		
		requestMock.getObjectParameter("Profile") >> profileMock
		requestMock.getObjectParameter("Order")  >>  orderMock
		requestMock.getObjectParameter("CreditCardContainer") >> ccContainerMock
		
		creditCardInfo.getExpired() >> true
		
		creditCardInfo.getCreditCardNumber() >> "12346"
		creditCardInfo.getExpirationYear() >> "2017"
		creditCardInfo.getExpirationMonth() >> "DEC"
		1*ccPaymentObject.extractGetCreditCardFromOrder(orderMock) >> []
		//credit card info geted from userProfile
		scnCreditCardInfo.getExpirationYear() >> "2018"
		scnCreditCardInfo.getExpirationMonth() >> "DEC"
		scnCreditCardInfo.getCreditCardNumber() >> "12346"
		scnCreditCardInfo.isDefault() >> true
		scnCreditCardInfo.getPaymentId() >> "456"
		1*ccApiImplMock.getUserCreditCardWallet(profileMock, null) >> [scnCreditCardInfo]
		
		when:
		ccPaymentObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("creditCardAmount", 0.0);
		1*requestMock.setParameter( "creditCardInfo",_);
		1*requestMock.setParameter("selectedId", "456");
		1*requestMock.serviceParameter("output", requestMock, responseMock);

	}
	
	def"TC to check credit card gets from order and profile is same "(){
		given:
		ccPaymentObject = Spy()
		ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
		ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)
		
		requestMock.getObjectParameter("Profile") >> profileMock
		requestMock.getObjectParameter("Order")  >>  orderMock
		requestMock.getObjectParameter("CreditCardContainer") >> ccContainerMock
		
		creditCardInfo.getExpired() >> false
		
		creditCardInfo.getCreditCardNumber() >> "12346"
		creditCardInfo.getExpirationYear() >> "2018"
		creditCardInfo.getExpirationMonth() >> "DEC"
		1*ccPaymentObject.extractGetCreditCardFromOrder(orderMock) >> [creditCardInfo]
		//credit card info geted from userProfile
		scnCreditCardInfo.getExpirationYear() >> "2018"
		scnCreditCardInfo.getExpirationMonth() >> "DEC"
		scnCreditCardInfo.getCreditCardNumber() >> "12346"
		scnCreditCardInfo.isDefault() >> true
		scnCreditCardInfo.getPaymentId() >> "456"
		1*ccApiImplMock.getUserCreditCardWallet(profileMock, null) >> [scnCreditCardInfo]
		
		when:
		ccPaymentObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("creditCardAmount", 0.0);
		1*requestMock.setParameter( "creditCardInfo",_);
		1*requestMock.setParameter("selectedId", null);
		1*requestMock.serviceParameter("output", requestMock, responseMock);

	}
	
	//////////// exception scenario ////////////////////////////
		def"TC BBBSystemException while getting credit card info from profile "(){
		given:
		ccPaymentObject = Spy()
		ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
		ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)
		
		requestMock.getObjectParameter("Profile") >> profileMock
		requestMock.getObjectParameter("Order")  >>  orderMock
		requestMock.getObjectParameter("CreditCardContainer") >> ccContainerMock
		
		creditCardInfo.getExpired() >> true
		
		1*ccPaymentObject.extractGetCreditCardFromOrder(orderMock) >> [creditCardInfo]
		//credit card info geted from userProfile
		1*ccApiImplMock.getUserCreditCardWallet(profileMock, null) >> {throw new BBBSystemException("exception") }
		
		when:
		ccPaymentObject.service(requestMock, responseMock)
		
		then:
		0*requestMock.setParameter("creditCardAmount", 0.0);
		0*requestMock.setParameter( "creditCardInfo",_);
		0*requestMock.setParameter("selectedId", _);
		0*requestMock.serviceParameter("output", requestMock, responseMock);
		1*requestMock.setParameter("errorMsg", _)
	}
	
		def"TC BBBBusinessException while getting credit card info from order "(){
			given:
			ccPaymentObject = Spy()
			ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
			ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)
			
			requestMock.getObjectParameter("Profile") >> profileMock
			requestMock.getObjectParameter("Order")  >>  orderMock
			requestMock.getObjectParameter("CreditCardContainer") >> ccContainerMock
			
			creditCardInfo.getExpired() >> true
			
			1*ccPaymentObject.extractGetCreditCardFromOrder(orderMock) >> {throw new BBBBusinessException("exception") }
			
			when:
			ccPaymentObject.service(requestMock, responseMock)
			
			then:
			0*requestMock.setParameter("creditCardAmount", 0.0);
			0*requestMock.setParameter( "creditCardInfo",_);
			0*requestMock.setParameter("selectedId", _);
			0*requestMock.serviceParameter("output", requestMock, responseMock);
			1*requestMock.setParameter("errorMsg", _)
		}
	
	/////////////////////////////////////// getAllCreditCard //////////////////////////////////////
		
		def "getAllCreditCard . TC to get the creditCard details "(){
			given:
			ccPaymentObject = Spy()
			ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
			ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)

			ServletUtil.setCurrentRequest(requestMock)
			ServletUtil.setCurrentResponse(responseMock)
			profileMock.getRepository() >> repositoryMock
			repositoryMock.getView("credit-card") >> view
			///calling the processCrediCardContainer
			setContainer()
			// two times will execute
		    2*itemMock.getPropertyValue("billingAddress") >> addressItemMock
			addressItemMock.getPropertyValue("address1") >> "address"
			addressItemMock.getPropertyValue("city") >> "NeyYork"
			addressItemMock.getPropertyValue("state") >> "US"
			addressItemMock.getPropertyValue("country") >> "unitedState"
			addressItemMock.getPropertyValue("firstName") >> "harry"
			
			RepositoryItem [] itemList= [itemMock]
			ccPaymentObject.extractExecuteQuery(view, _,  _) >>  itemList
			
			
			when:
			 CreditCardInfoVO creditCardinfo = ccPaymentObject.getAllCreditCard(orderMock,profileMock )
			then:
			creditCardinfo !=null
			creditCardinfo.getOrderBalance() == null
		//	creditCardinfo.getCreditCardInfoList().get("pay123").getBillingAddress() != null
			creditCardinfo.getCreditCardInfoList().get("pay123").getCreditCardNumber() == "123456"
			creditCardinfo.getCreditCardInfoList().get("pay123").getExpirationYear() == "2017"
			creditCardinfo.getCreditCardInfoList().get("pay123").getExpirationMonth() == "DEC"
		}
		
		def "getAllCreditCard . TC when return credit card list using executeQuery is null  "(){
			given:
			ccPaymentObject = Spy()
			ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
			ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)

			ServletUtil.setCurrentRequest(requestMock)
			ServletUtil.setCurrentResponse(responseMock)
			profileMock.getRepository() >> repositoryMock
			repositoryMock.getView("credit-card") >> view
			///calling the processCrediCardContainer
			setContainer()
			//2*itemMock.getPropertyValue("billingAddress") >> addressItemMock
			
			ccPaymentObject.extractExecuteQuery(view, _,  _) >>  null
			
			
			when:
			 CreditCardInfoVO creditCardinfo = ccPaymentObject.getAllCreditCard(orderMock,profileMock )
			then:
			creditCardinfo !=null
			creditCardinfo.getOrderBalance() == null
			creditCardinfo.getCreditCardInfoList().get("pay123").getCreditCardNumber() == "123456"
			creditCardinfo.getCreditCardInfoList().get("pay123").getExpirationYear() == "2017"
			creditCardinfo.getCreditCardInfoList().get("pay123").getExpirationMonth() == "DEC"
			0*itemMock.getPropertyValue("billingAddress")
		}
		
		def "getAllCreditCard . TC when  credit card item list ( using  executeQuery )is empty  "(){
			given:
			ccPaymentObject = Spy()
			ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
			ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)

			ServletUtil.setCurrentRequest(requestMock)
			ServletUtil.setCurrentResponse(responseMock)
			profileMock.getRepository() >> repositoryMock
			repositoryMock.getView("credit-card") >> view
			///calling the processCrediCardContainer
			setContainer()
			//2*itemMock.getPropertyValue("billingAddress") >> addressItemMock
			
			RepositoryItem [] itemList= [null]
			ccPaymentObject.extractExecuteQuery(view, _,  _) >>  itemList
			
			
			when:
			 CreditCardInfoVO creditCardinfo = ccPaymentObject.getAllCreditCard(orderMock,profileMock )
			then:
			creditCardinfo !=null
			creditCardinfo.getOrderBalance() == null
			creditCardinfo.getCreditCardInfoList().get("pay123").getCreditCardNumber() == "123456"
			creditCardinfo.getCreditCardInfoList().get("pay123").getExpirationYear() == "2017"
			creditCardinfo.getCreditCardInfoList().get("pay123").getExpirationMonth() == "DEC"
			0*itemMock.getPropertyValue("billingAddress")
		}
		
////////////////////////////////////// exception scenario ///////////////////////
		
				
		def "getAllCreditCard . TC when getview throw RepositoryException  "(){
			given:
			ccPaymentObject = Spy()
			ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
			ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)

			ServletUtil.setCurrentRequest(requestMock)
			ServletUtil.setCurrentResponse(responseMock)
			profileMock.getRepository() >> repositoryMock
			repositoryMock.getView("credit-card") >> {throw new RepositoryException("repositoryException")}
			///calling the processCrediCardContainer
			setContainer()
			
			when:
			 CreditCardInfoVO creditCardinfo = ccPaymentObject.getAllCreditCard(orderMock,profileMock )
			then:
			creditCardinfo !=null
			creditCardinfo.getOrderBalance() == null
			creditCardinfo.getCreditCardInfoList().get("pay123").getCreditCardNumber() == "123456"
			creditCardinfo.getCreditCardInfoList().get("pay123").getExpirationYear() == "2017"
			creditCardinfo.getCreditCardInfoList().get("pay123").getExpirationMonth() == "DEC"
			0*itemMock.getPropertyValue("billingAddress")
		}
		
		def "getAllCreditCard . TC torws RepositoryException while  credit card item list ( using  executeQuery )  "(){
			given:
			ccPaymentObject = Spy()
			ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
			ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)

			ServletUtil.setCurrentRequest(requestMock)
			ServletUtil.setCurrentResponse(responseMock)
			profileMock.getRepository() >> repositoryMock
			repositoryMock.getView("credit-card") >> view
			///calling the processCrediCardContainer
			setContainer()
			//2*itemMock.getPropertyValue("billingAddress") >> addressItemMock
			
			RepositoryItem [] itemList= [null]
			ccPaymentObject.extractExecuteQuery(view, _,  _) >>  {throw new RepositoryException("exception")}
			
			
			when:
			 CreditCardInfoVO creditCardinfo = ccPaymentObject.getAllCreditCard(orderMock,profileMock )
			then:
			creditCardinfo !=null
			0*itemMock.getPropertyValue("billingAddress")
		}
		
		
		def "getAllCreditCard . TC whe processCrediCardContainer method torws BBBSystemException   "(){
			given:
			ccPaymentObject = Spy()
			ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
			ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)

			ServletUtil.setCurrentRequest(requestMock)
			ServletUtil.setCurrentResponse(responseMock)
			profileMock.getRepository() >> repositoryMock
			repositoryMock.getView("credit-card") >> view
			///calling the processCrediCardContainer
			ccPaymentObject.processCrediCardContainer(requestMock,profileMock,*_) >>{throw new  BBBSystemException("exception")}
			
			when:
			 CreditCardInfoVO creditCardinfo = ccPaymentObject.getAllCreditCard(orderMock,profileMock )
			 
			then:
			BBBSystemException exception = thrown()
			0*itemMock.getPropertyValue("billingAddress")
		}
		
		def "getAllCreditCard . TC whe processCrediCardContainer method torws BBBBusinessException   "(){
			given:
			ccPaymentObject = Spy()
			ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
			ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)

			ServletUtil.setCurrentRequest(requestMock)
			ServletUtil.setCurrentResponse(responseMock)
			profileMock.getRepository() >> repositoryMock
			repositoryMock.getView("credit-card") >> view
			///calling the processCrediCardContainer
			ccPaymentObject.processCrediCardContainer(requestMock,profileMock,*_) >>{throw new  BBBBusinessException("exception")}
			
			when:
			 CreditCardInfoVO creditCardinfo = ccPaymentObject.getAllCreditCard(orderMock,profileMock )
			 
			then:
			BBBSystemException exception = thrown()
			0*itemMock.getPropertyValue("billingAddress")
		}
		
		private void setContainer(){
			//start processCrediCardContainer method
			
		    creditCardInfo.getPaymentId() >> "pay123"
			creditCardInfo.getExpired() >> false
						
			creditCardInfo.getCreditCardNumber() >> "123456"
			creditCardInfo.getExpirationYear() >> "2017"
			creditCardInfo.getExpirationMonth() >> "DEC"
			1*ccPaymentObject.extractGetCreditCardFromOrder(orderMock) >> [creditCardInfo]
						//credit card info geted from userProfile
			scnCreditCardInfo.getExpirationYear() >> "2018"
			scnCreditCardInfo.getExpirationMonth() >> "NOV"
			scnCreditCardInfo.getCreditCardNumber() >> "456"
			scnCreditCardInfo.isDefault() >> false
			scnCreditCardInfo.getPaymentId() >> "456"
			1*ccApiImplMock.getUserCreditCardWallet(profileMock, null) >> [scnCreditCardInfo]
			//end processCrediCardContainer method
				
		}
		
		
		def "processCrediCardContainer . TC whe processCrediCardContainer methodsinessException   "(){
			given:
		ccPaymentObject = Spy()
		ccPaymentObject.setPurchaseProcessHelper(processHelperMock)
		ccPaymentObject.setCreditCardAPIImpl(ccApiImplMock)
		BBBCreditCardContainer ccContainerMock = new BBBCreditCardContainer()
		//requestMock.getObjectParameter("CreditCardContainer") >> ccContainerMock
		
		creditCardInfo.getPaymentId() >> "pay123"
		creditCardInfo.getExpired() >> false
		
		creditCardInfo.getCreditCardNumber() >> "123456"
		creditCardInfo.getExpirationYear() >> "2017"
		creditCardInfo.getExpirationMonth() >> "DEC"
		1*ccPaymentObject.extractGetCreditCardFromOrder(orderMock) >> [creditCardInfo]
		//credit card info geted from userProfile
		scnCreditCardInfo.getPaymentId() >> "pay12"
		
		scnCreditCardInfo.getExpirationYear() >> "2018"
		scnCreditCardInfo.getExpirationMonth() >> "NOV"
		scnCreditCardInfo.getCreditCardNumber() >> "456"
		scnCreditCardInfo.isDefault() >> false
		//scnCreditCardInfo.getPaymentId() >> "456"
		 1*ccApiImplMock.getUserCreditCardWallet(profileMock, null) >> [scnCreditCardInfo]

			
			when:
			 ccPaymentObject.processCrediCardContainer(requestMock, profileMock, orderMock ,ccContainerMock )
			 
			then:
			ccContainerMock.getCreditCardMap().get("pay12").getExpirationYear() == "2018"
			1*requestMock.setParameter("creditCardAmount", 0.0);
			1*requestMock.setParameter( "creditCardInfo",_);
			1*requestMock.setParameter("selectedId", "pay123");

		}
			
}
