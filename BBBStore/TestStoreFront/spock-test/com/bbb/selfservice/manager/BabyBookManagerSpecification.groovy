package com.bbb.selfservice.manager

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException
import com.bbb.framework.integration.util.ServiceHandlerUtil
import com.bbb.selfservice.tibco.vo.BabyBookVO
import com.bbb.selfservice.tibco.vo.TellAFriendVO

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class BabyBookManagerSpecification extends BBBExtendedSpec {
	
	BabyBookManager testObj
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = Spy()
	}
	
	def"requestBabyBookTIBCO. This TC is the happy flow of requestBabyBookTIBCO method"(){
		given:
			BabyBookVO babyBookVOMock = new BabyBookVO(mType:null,mEmailAddr:"bbbgift.gmail.com",mFirstName:"john",mLastName:"kennedy",
				mAddressLine1:"1211 Tremont Street",mAddressLine2:"Massachusetts Avenue",mCity:"Boston",mState:"New Jercy",mZipcode:"70002",
				mPhoneNumber:"8989898989",mEmailOffer:TRUE)
			1 * testObj.sendBabyBook(babyBookVOMock) >> {}
			
		when:
			testObj.requestBabyBookTIBCO(babyBookVOMock)
		then:
			1 * testObj.logDebug('First Name :john')
			1 * testObj.logDebug('State :New Jercy')
			1 * testObj.logDebug('Last Name :kennedy')
			1 * testObj.logDebug('Email Offer :true')
			1 * testObj.logDebug('Zipcode :70002')
			1 * testObj.logDebug('Address Line2 :Massachusetts Avenue')
			1 * testObj.logDebug('Phone Number :8989898989')
			1 * testObj.logDebug('requestBabyBookTIBCO method started')
			1 * testObj.logDebug('Email Address :bbbgift.gmail.com')
			1 * testObj.logDebug('Address Line1 :1211 Tremont Street')
			1 * testObj.logDebug('Type :null')
			1 * testObj.logDebug('City :Boston')	
			
	}
	
	def"requestBabyBookTIBCO. This TC is when pBabyBookVO parameter passed as null"(){
		given:
		
		when:
			testObj.requestBabyBookTIBCO(null)
		then:
			thrown(BBBBusinessException)
	}
	
	def"requestBabyBookTIBCO. This TC is when BBBSystemException thrown"(){
		given:
			BabyBookVO babyBookVOMock = new BabyBookVO(mType:null,mEmailAddr:"bbbgift.gmail.com",mFirstName:"john",mLastName:"kennedy",
				mAddressLine1:"1211 Tremont Street",mAddressLine2:"Massachusetts Avenue",mCity:"Boston",mState:"New Jercy",mZipcode:"70002",
				mPhoneNumber:"8989898989",mEmailOffer:TRUE)
			1 * testObj.sendBabyBook(babyBookVOMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		when:
			testObj.requestBabyBookTIBCO(babyBookVOMock)
		then:
			thrown BBBBusinessException
			1 * testObj.logError('account_1216: BBBSystemException in Baby Book manager while requestBabyBookTIBCO', _)
			
	}
	
	def"requestTellAFriendTIBCO. This TC is the happy flow of requestTellAFriendTIBCO"(){
		given:
			TellAFriendVO tellAFriendVOMock = new TellAFriendVO(mType:null,mSenderEmailAddr:"bbbgift.gmail.com",mSenderFirstName:"john",mSenderLastName:"kennedy",
				mRecipientEmailAddr:"bbbgift.gmail.com",mRecipientFirstName:"rajan",mRecipientLastName:"sreedhar",mEmailCopy:TRUE)
			testObj.sendTellAFriend(tellAFriendVOMock) >> {}
			
		when:
			testObj.requestTellAFriendTIBCO(tellAFriendVOMock)
		then:
			1 * testObj.logDebug('Email Copy :true')
			1 * testObj.logDebug('Sender First Name :john')
			1 * testObj.logDebug('requestTellAFriendTIBCO method started')
			1 * testObj.logDebug('Sender Last Name :kennedy')
			1 * testObj.logDebug('requestTellAFriendTIBCO method ends')
			1 * testObj.logDebug('Type :null')
			1 * testObj.logDebug('Recipient First Name :rajan')
			1 * testObj.logDebug('Recipient Last Name :sreedhar')
			1 * testObj.logDebug('Sender Email Address :bbbgift.gmail.com')
			1 * testObj.logDebug('Recipient Email Address :bbbgift.gmail.com')
			
	}
	
	def"requestTellAFriendTIBCO. This TC is when pTellAFriendVO parameter passed as null"(){
		given:
		
		when:
			testObj.requestTellAFriendTIBCO(null)
		then:
			thrown(BBBBusinessException)
	}
	
	def"requestTellAFriendTIBCO. This TC is when BBBSystemException thrown"(){
		given:
			TellAFriendVO tellAFriendVOMock = new TellAFriendVO(mType:null,mSenderEmailAddr:"bbbgift.gmail.com",mSenderFirstName:"john",mSenderLastName:"kennedy",
				mRecipientEmailAddr:"bbbgift.gmail.com",mRecipientFirstName:"rajan",mRecipientLastName:"sreedhar",mEmailCopy:TRUE)
			1 * testObj.sendTellAFriend(tellAFriendVOMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.requestTellAFriendTIBCO(tellAFriendVOMock)
		then:
			thrown BBBBusinessException
			1 * testObj.logError('account_1217: BBBSystemException in Baby Book manager while requestTellAFriendTIBCO', _)
			
	}

}
