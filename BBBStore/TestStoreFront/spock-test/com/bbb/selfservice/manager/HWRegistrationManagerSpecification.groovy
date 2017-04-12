package com.bbb.selfservice.manager

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.tibco.vo.HWRegistrationVO

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class HWRegistrationManagerSpecification extends BBBExtendedSpec {
	
	HWRegistrationManager testObj
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = Spy()
	}
	
	def"requestHealthyWomanTIBCO.This TC is the Happy flow of requestHealthyWomanTIBCO method"(){
		given:
			HWRegistrationVO HWRegistrationVOMock = new HWRegistrationVO(mType:null,mEmailAddr:"bbbcustomer@gmail.com",mFirstName:"John",
				mLastName:"Kennedy",mAddressLine1:"1211 Tremont Street",mAddressLine2:"Massachusetts Avenue",mCity:"Boston",mState:"New Jercy",
				mZipcode:"70002",mEmailOffer:TRUE)
			
			1 * testObj.sendHealthyWomenTIBCO(HWRegistrationVOMock) >> {}
			
		when:
			testObj.requestHealthyWomanTIBCO(HWRegistrationVOMock)
		then:
			1 * testObj.logDebug('Last Name :Kennedy')
			1 * testObj.logDebug('Email Offer :true')
			1 * testObj.logDebug('First Name :John')
			1 * testObj.logDebug('Type :null')
			1 * testObj.logDebug('Email Address :bbbcustomer@gmail.com')
			1 * testObj.logDebug('Zipcode :70002')
			1 * testObj.logDebug('State :New Jercy')
			1 * testObj.logDebug('Address Line2 :Massachusetts Avenue')
			1 * testObj.logDebug('Address Line1 :1211 Tremont Street')
			1 * testObj.logDebug('HWRegistrationManager.requestHealthyWomanTIBCO method ends')
			1 * testObj.logDebug('HWRegistrationManager.requestHealthyWomanTIBCO method started')
			1 * testObj.logDebug('City :Boston')
		
	}
	
	def"requestHealthyWomanTIBCO.This TC is when hwRegistrationVO parameter is passed as null"(){
		given:
			
		when:
			testObj.requestHealthyWomanTIBCO(null)
		then:
			thrown BBBBusinessException
	}
	
	def"requestHealthyWomanTIBCO.This TC is when BBBSystemException thrown"(){
		given:
			HWRegistrationVO HWRegistrationVOMock = new HWRegistrationVO(mType:null,mEmailAddr:"bbbcustomer@gmail.com",mFirstName:"John",
				mLastName:"Kennedy",mAddressLine1:"1211 Tremont Street",mAddressLine2:"Massachusetts Avenue",mCity:"Boston",mState:"New Jercy",
				mZipcode:"70002",mEmailOffer:TRUE)
			
			1 * testObj.sendHealthyWomenTIBCO(HWRegistrationVOMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.requestHealthyWomanTIBCO(HWRegistrationVOMock)
		then:
			thrown BBBBusinessException
			1 * testObj.logError('account_1221: BBBSystemException in HWRegistrationManager while requestHealthyWomanTIBCO', _)
			1 * testObj.logDebug('Last Name :Kennedy')
			1 * testObj.logDebug('Email Offer :true')
			1 * testObj.logDebug('First Name :John')
			1 * testObj.logDebug('Type :null')
			1 * testObj.logDebug('Email Address :bbbcustomer@gmail.com')
			1 * testObj.logDebug('Zipcode :70002')
			1 * testObj.logDebug('State :New Jercy')
			1 * testObj.logDebug('Address Line2 :Massachusetts Avenue')
			1 * testObj.logDebug('Address Line1 :1211 Tremont Street')
			1 * testObj.logDebug('HWRegistrationManager.requestHealthyWomanTIBCO method started')
			1 * testObj.logDebug('City :Boston')
		
	}

}
