package com.bbb.selfservice.manager

import java.util.List;

import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException
import com.bbb.repository.RepositoryItemMock;
import com.bbb.selfservice.tibco.vo.ContactUsVO
import com.bbb.selfservice.tools.ContactUsTools

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class ContactUsManagerSpecification extends BBBExtendedSpec {
	
	ContactUsManager testObj
	ContactUsTools contactUsToolsMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	RepositoryItemMock repositoryItemMock = Mock()
	
	def setup(){
		testObj = new ContactUsManager(contactUsTools:contactUsToolsMock,catalogTools:catalogToolsMock)
	}
	
	////////////////////////////////////////TestCases for requestInfoTIBCO --> STARTS////////////////////////////////////////////
	///////////Signature : public void requestInfoTIBCO(ContactUsVO pContactUsVO) ///////////
	
	def"requestInfoTIBCO. This TC is the Happy flow of requestInfoTIBCO method"(){
		given:
			testObj = Spy()
			ContactUsVO ContactUsVOMock = new ContactUsVO(mEmail:"bbbcustomer@gmail.com",mEmailMessage:"Reg: Queries",mFirstName:"Richard",
				mLastName:"Steward",mPhoneNumber:"2442522522",mPhoneExt:"0451",mOrderNumber:"BBB12345678",mGender:"Male",mContactType:"Direct",
				mTimeZone:"GMT",mTimeCall:"10.30AM",mSiteFlag:"yes")
			
			1 * testObj.sendRequestInfoTIBCO(ContactUsVOMock) >> {}
		
		when:
			testObj.requestInfoTIBCO(ContactUsVOMock)
		then:
			1 * testObj.logDebug('ContactUsManager.requestInfoTIBCO() method started')
			1 * testObj.logDebug('First Name :Richard')
			1 * testObj.logDebug('Time Call :10.30AM')
			1 * testObj.logDebug('Email Message :Reg: Queries')
			1 * testObj.logDebug('Time Zone :GMT')
			1 * testObj.logDebug('Last Name :Steward')
			1 * testObj.logDebug('Contact Type :Direct')
			1 * testObj.logDebug('Phone Ext :0451')
			1 * testObj.logDebug('ContactUsManager.requestInfoTIBCO() method ends')
			1 * testObj.logDebug('Email :bbbcustomer@gmail.com')
			1 * testObj.logDebug('Gender :Male')
			1 * testObj.logDebug('Site Flag :yes')
			1 * testObj.logDebug('Order number :BBB12345678')
			1 * testObj.logDebug('Phone :2442522522')
		
	}
	
	def"requestInfoTIBCO. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			ContactUsVO ContactUsVOMock = new ContactUsVO()
			1 * testObj.sendRequestInfoTIBCO(ContactUsVOMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.requestInfoTIBCO(ContactUsVOMock)
		then:
			thrown BBBBusinessException
			1 * testObj.logError('account_1220: BBBSystemException in ContactUsManager while requestInfoTIBCO', _)
			1 * testObj.logDebug('Phone :null')
			1 * testObj.logDebug('ContactUsManager.requestInfoTIBCO() method started')
			1 * testObj.logDebug('Last Name :null')
			1 * testObj.logDebug('Order number :null')
			1 * testObj.logDebug('Email :null')
			1 * testObj.logDebug('Time Call :null')
			1 * testObj.logDebug('First Name :null')
			1 * testObj.logDebug('Contact Type :null')
			1 * testObj.logDebug('Time Zone :null')
			1 * testObj.logDebug('Site Flag :null')
			1 * testObj.logDebug('Email Message :null')
			1 * testObj.logDebug('Phone Ext :null')
			1 * testObj.logDebug('Gender :null')
			
	}
	
	////////////////////////////////////////TestCases for requestInfoTIBCO --> ENDS////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getContactUsItem --> STARTS////////////////////////////////////////////
	///////////Signature : public RepositoryItem[] getContactUsItem() ///////////
	
	def"getContactUsItem. This TC is the Happy flow of getContactUsItem method"(){
		given:
			1 * contactUsToolsMock.getContactUsItem() >> [repositoryItemMock]
		when:
			def results = testObj.getContactUsItem()
		then:
			results == [repositoryItemMock]
	}
	
	////////////////////////////////////////TestCases for getContactUsItem --> ENDS////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getTimeZones --> STARTS////////////////////////////////////////////
	///////////Signature : public List<String> getTimeZones(String pSiteId) ///////////
	
	def"getTimeZones. This TC is the Happy flow of getTimeZones method"(){
		given:
			String siteId = "BedBathUS"
			1 * catalogToolsMock.getTimeZones(siteId) >> ["someValue","anotherValue"]
		when:
			List<String> results = testObj.getTimeZones(siteId)
		then:
			results == ["someValue","anotherValue"]
	}
	
	////////////////////////////////////////TestCases for getTimeZones --> ENDS////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getcustomerCareEmailAddress --> STARTS////////////////////////////////////////////
	///////////Signature :public String getcustomerCareEmailAddress(String pSiteId) ///////////
	
	def"getcustomerCareEmailAddress.This TC is the Happy flow of getcustomerCareEmailAddress method"(){
		given:
			String siteId = "BedBathUS"
			1 * catalogToolsMock.getcustomerCareEmailAddress(siteId) >> "someValue"
		when:
		 	def results = testObj.getcustomerCareEmailAddress(siteId)
		then:
			results == "someValue"
	}
	
	////////////////////////////////////////TestCases for getcustomerCareEmailAddress --> ENDS////////////////////////////////////////////

}
