package com.bbb.cms.droplet

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants
import com.bbb.exception.BBBSystemException
import com.bbb.utils.BBBUtility

import atg.core.util.Base64;
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryItem
import atg.userprofiling.Profile;
import spock.lang.specification.BBBExtendedSpec

class ChallengeQuestionDropletSpecification extends BBBExtendedSpec {
	def BBBCatalogTools catalogToolMock = Mock()
	def BBBProfileTools profileToolMock = Mock()
	def ChallengeQuestionDroplet testObj
	def setup(){
		testObj = Spy()
		testObj.isLoggingDebug() >> true
		testObj.setCatalogTools(catalogToolMock)
		testObj.setProfileTools(profileToolMock)
	}
	
	def"fetchchallengeQuestion when forgetEmailId is not null"(){
		given:
			 
			String forgetEmail="email@bbby.com"
			String userItemId="userItemId1"
			String id=userItemId
			
			String challengeQuentionItemId="challengeQuentionItemId_1"
			String challengeQuenstion1="challengeQuenstion1"
			String challengeQuenstion2="challengeQuenstion2"
			RepositoryItem userItemFromEmail = Mock()
			userItemFromEmail.getRepositoryId() >> userItemId
			userItemFromEmail.getPropertyValue(BBBCoreConstants.ID) >>id
			
			MutableRepositoryItem challengeQuentionItem = Mock()
			challengeQuentionItem.getRepositoryId() >> userItemId		
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1) >>challengeQuenstion1
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2) >>challengeQuenstion2
			
			Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>()
			
			Map<String,String> quesMap1 =new HashMap<String,String>()
			quesMap1.put(challengeQuenstion1, challengeQuenstion1)
			
			Map<String,String> quesMap2 =new HashMap<String,String>()
			quesMap2.put(challengeQuenstion2, challengeQuenstion2)
			
			questionMap.put(BBBCoreConstants.STRING_ONE, quesMap1)
			questionMap.put(BBBCoreConstants.STRING_TWO, quesMap2)
			
			requestMock.getParameter(BBBCoreConstants.FORGET_EMAIL) >> forgetEmail
			1* profileToolMock.getItemFromEmail(_) >> userItemFromEmail
			1* profileToolMock.fetchProfileIdFromChallengeQuestionRepository(userItemFromEmail.getRepositoryId()) >> challengeQuentionItem
			2* profileToolMock.fetchChallengeQuestionsFromRepository() >> questionMap
			
		
		when:
			Map<String,Object> challengeQuestions =	testObj.fetchchallengeQuestion(forgetEmail)
			
		then: 
			
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* requestMock.getSession().setAttribute(BBBCoreConstants.FORGET_EMAIL, forgetEmail)
			2* requestMock.setParameter(BBBCoreConstants.FORGET_EMAIL, BBBUtility.toLowerCase(forgetEmail))
			1* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1,  challengeQuenstion1)
			1* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2,  challengeQuenstion2)
			1* requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			0* testObj.logDebug("ChallengeQuestionDroplet Service method : End")
		}
	
	
	def"fetchchallengeQuestion when forgetEmail is not null| challenge quenstion does not exist in challengeQuentionItem for userId"(){
		given:
			
			String forgetEmail="email@bbby.com"
			String userItemId="userItemId1"
			String id=userItemId
			
			String challengeQuentionItemId="challengeQuentionItemId_1"
			String challengeQuenstion1="challengeQuenstion1"
			String challengeQuenstion2="challengeQuenstion2"
			RepositoryItem userItemFromEmail = Mock()
			userItemFromEmail.getRepositoryId() >> userItemId
			userItemFromEmail.getPropertyValue(BBBCoreConstants.ID) >>id
			
			MutableRepositoryItem challengeQuentionItem = Mock()
			challengeQuentionItem.getRepositoryId() >> userItemId
			
			Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>()
			
			Map<String,String> quesMap1 =new HashMap<String,String>()
			quesMap1.put(challengeQuenstion1, challengeQuenstion1)
			
			Map<String,String> quesMap2 =new HashMap<String,String>()
			quesMap2.put(challengeQuenstion2, challengeQuenstion2)
			
			questionMap.put(BBBCoreConstants.STRING_ONE, quesMap1)
			questionMap.put(BBBCoreConstants.STRING_TWO, quesMap2)
			
			requestMock.getParameter(BBBCoreConstants.FORGET_EMAIL) >> forgetEmail
			1* profileToolMock.getItemFromEmail(_) >> userItemFromEmail
			1* profileToolMock.fetchProfileIdFromChallengeQuestionRepository(userItemFromEmail.getRepositoryId()) >> challengeQuentionItem
			0* profileToolMock.fetchChallengeQuestionsFromRepository() >> questionMap
		
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(forgetEmail)
			
		then:
			
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* requestMock.getSession().setAttribute(BBBCoreConstants.FORGET_EMAIL, forgetEmail)
			2* requestMock.setParameter(BBBCoreConstants.FORGET_EMAIL, BBBUtility.toLowerCase(forgetEmail))
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1,  challengeQuenstion1)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2,  challengeQuenstion2)
			1* requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			0* testObj.logDebug("ChallengeQuestionDroplet Service method : End")
		}
	
	
	
	def"fetchchallengeQuestion when forgetEmail is not null| challenge quenstion  does not exist for userId"(){
		given:
			 
			String forgetEmail="email@bbby.com"
			String userItemId="userItemId1"
			String id=userItemId
			
			String challengeQuentionItemId="challengeQuentionItemId_1"
			String challengeQuenstion1="challengeQuenstion1"
			String challengeQuenstion2="challengeQuenstion2"
			RepositoryItem userItemFromEmail = Mock()
			userItemFromEmail.getRepositoryId() >> userItemId
			userItemFromEmail.getPropertyValue(BBBCoreConstants.ID) >>id
			
			
			
			Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>()
			
			Map<String,String> quesMap1 =new HashMap<String,String>()
			quesMap1.put(challengeQuenstion1, challengeQuenstion1)
			
			Map<String,String> quesMap2 =new HashMap<String,String>()
			quesMap2.put(challengeQuenstion2, challengeQuenstion2)
			
			questionMap.put(BBBCoreConstants.STRING_ONE, quesMap1)
			questionMap.put(BBBCoreConstants.STRING_TWO, quesMap2)
			
			requestMock.getParameter(BBBCoreConstants.FORGET_EMAIL) >> forgetEmail
			1* profileToolMock.getItemFromEmail(_) >> userItemFromEmail
			1* profileToolMock.fetchProfileIdFromChallengeQuestionRepository(userItemFromEmail.getRepositoryId()) >> null
			0* profileToolMock.fetchChallengeQuestionsFromRepository() >> questionMap
		
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(forgetEmail)
			
		then:
			
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* requestMock.getSession().setAttribute(BBBCoreConstants.FORGET_EMAIL, forgetEmail)
			2* requestMock.setParameter(BBBCoreConstants.FORGET_EMAIL, BBBUtility.toLowerCase(forgetEmail))
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1,  challengeQuenstion1)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2,  challengeQuenstion2)
			0* requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			0* testObj.logDebug("ChallengeQuestionDroplet Service method : End")
		}
	
	
	
	def"fetchchallengeQuestion when forgetEmail is null"(){
		given:
			 
			String sessionforgetEmail="email@bbby.com"
			String forgetEmail=null
			String userItemId="userItemId1"
			String id=userItemId
			String loginPage=""
			
			String challengeQuentionItemId="challengeQuentionItemId_1"
			String challengeQuenstion1="challengeQuenstion1"
			String challengeQuenstion2="challengeQuenstion2"
			RepositoryItem userItemFromEmail = Mock()
			userItemFromEmail.getRepositoryId() >> userItemId
			userItemFromEmail.getPropertyValue(BBBCoreConstants.ID) >>id
			
			MutableRepositoryItem challengeQuentionItem = Mock()
			challengeQuentionItem.getRepositoryId() >> userItemId
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1) >>challengeQuenstion1
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2) >>challengeQuenstion2
			
			Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>()
			
			Map<String,String> quesMap1 =new HashMap<String,String>()
			quesMap1.put(challengeQuenstion1, challengeQuenstion1)
			
			Map<String,String> quesMap2 =new HashMap<String,String>()
			quesMap2.put(challengeQuenstion2, challengeQuenstion2)
			
			questionMap.put(BBBCoreConstants.STRING_ONE, quesMap1)
			questionMap.put(BBBCoreConstants.STRING_TWO, quesMap2)
			
			requestMock.getParameter(BBBCoreConstants.FORGET_EMAIL) >> forgetEmail
			requestMock.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL) >> sessionforgetEmail
			requestMock.getParameter(BBBCoreConstants.FROM_LOGIN_PAGE) >> loginPage
			1* profileToolMock.getItemFromEmail(_) >> userItemFromEmail
			1* profileToolMock.fetchProfileIdFromChallengeQuestionRepository(userItemFromEmail.getRepositoryId()) >> challengeQuentionItem
			2* profileToolMock.fetchChallengeQuestionsFromRepository() >> questionMap
			
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(forgetEmail)
			
		then:
			
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			0* requestMock.getSession().setAttribute(BBBCoreConstants.FORGET_EMAIL, forgetEmail)
			1* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1,  challengeQuenstion1)
			1* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2,  challengeQuenstion2)
			1* requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			0* testObj.logDebug("ChallengeQuestionDroplet Service method : End")
			
		}
	
/*--------------------------------------- UTC for else block of service method start  --------------------------------------------------------------------------------
							
									Scenario:-
												when : Forget email is null
										 		 and : Forget email is null in http session
-----------------------------------------------------------------------------------------------------------------------------------------------------*/
	def"fetchchallengeQuestion when Forget Email is null in session and request parameter"(){
		given:
			 
			String forgetEmail=null
			String userItemId="userItemId1"
			String id=userItemId
			
			String challengeQuentionItemId="challengeQuentionItemId_1"
			String challengeQuenstion1="challengeQuenstion1"
			String challengeQuenstion2="challengeQuenstion2"
			String preferredAnswer1="preferredAnswer1"
			String preferredAnswer2="preferredAnswer2"
			RepositoryItem userItemFromEmail = Mock()
			userItemFromEmail.getRepositoryId() >> userItemId
			userItemFromEmail.getPropertyValue(BBBCoreConstants.ID) >>id
			
			MutableRepositoryItem challengeQuentionItem = Mock()
			challengeQuentionItem.getRepositoryId() >> userItemId
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1) >>challengeQuenstion1
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2) >>challengeQuenstion2
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_1) >>preferredAnswer1
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_2) >>preferredAnswer2
			Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>()
			
			Map<String,String> quesMap1 =new HashMap<String,String>()
			quesMap1.put(challengeQuenstion1, challengeQuenstion1)
			
			Map<String,String> quesMap2 =new HashMap<String,String>()
			quesMap2.put(challengeQuenstion2, challengeQuenstion2)
			
			questionMap.put(BBBCoreConstants.STRING_ONE, quesMap1)
			questionMap.put(BBBCoreConstants.STRING_TWO, quesMap2)
			Profile profileMock= Mock()
		
			1* requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >>profileMock
			1* profileMock.getPropertyValue(BBBCoreConstants.ID) >> id
			0* profileToolMock.getItemFromEmail(_) >> userItemFromEmail
			1* profileToolMock.fetchProfileIdFromChallengeQuestionRepository(userItemFromEmail.getRepositoryId()) >> challengeQuentionItem
			1* profileToolMock.fetchChallengeQuestionsFromRepository() >> questionMap
			
		
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(forgetEmail)
			
		then:
			
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* requestMock.setParameter("challengQuestionExist", true)
			1* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1, challengeQuenstion1)
			1* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2, challengeQuenstion2)
			1* requestMock.setParameter(BBBCoreConstants.PREFERRED_ANSWER_1, Base64.decodeToString(preferredAnswer1))
			1* requestMock.setParameter(BBBCoreConstants.PREFERRED_ANSWER_2, Base64.decodeToString(preferredAnswer2))
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP, _)
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP1, _);
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP2, _);
			1* requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : End")
		}
	
	
	def"fetchchallengeQuestion when Forget Email is null in session and request parameter |  challenge quenstion  does not exist in challengeQuentionItem for userId"(){
		given:
			 
			String forgetEmail=null
			String userItemId="userItemId1"
			String id=userItemId
			
			String challengeQuentionItemId="challengeQuentionItemId_1"
			String challengeQuenstion1="challengeQuenstion1"
			String challengeQuenstion2="challengeQuenstion2"
			String preferredAnswer1="preferredAnswer1"
			String preferredAnswer2="preferredAnswer2"
			RepositoryItem userItemFromEmail = Mock()
			userItemFromEmail.getRepositoryId() >> userItemId
			userItemFromEmail.getPropertyValue(BBBCoreConstants.ID) >>id
			
			MutableRepositoryItem challengeQuentionItem = Mock()
			challengeQuentionItem.getRepositoryId() >> userItemId
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1) >>null
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2) >>null
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_1) >>null
			challengeQuentionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_2) >>null
			Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>()
			
			Map<String,String> quesMap1 =new HashMap<String,String>()
			quesMap1.put(challengeQuenstion1, challengeQuenstion1)
			
			Map<String,String> quesMap2 =new HashMap<String,String>()
			quesMap2.put(challengeQuenstion2, challengeQuenstion2)
			
			questionMap.put(BBBCoreConstants.STRING_ONE, quesMap1)
			questionMap.put(BBBCoreConstants.STRING_TWO, quesMap2)
			Profile profileMock= Mock()
		
			1* requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >>profileMock
			1* profileMock.getPropertyValue(BBBCoreConstants.ID) >> id
			0* profileToolMock.getItemFromEmail(_) >> userItemFromEmail
			1* profileToolMock.fetchProfileIdFromChallengeQuestionRepository(userItemFromEmail.getRepositoryId()) >> challengeQuentionItem
			1* profileToolMock.fetchChallengeQuestionsFromRepository() >> questionMap
			
		
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(forgetEmail)
			
		then:
			
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* requestMock.setParameter("challengQuestionExist", true)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1, challengeQuenstion1)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2, challengeQuenstion2)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_ANSWER_1, Base64.decodeToString(preferredAnswer1))
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_ANSWER_2, Base64.decodeToString(preferredAnswer2))
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP, _)
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP1, _);
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP2, _);
			1* requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : End")
		}
	
	
	def"fetchchallengeQuestion when Forget Email is null in session and request parameter |  challenge quenstion  Item is null for userID"(){
		given:
			String emaiId="email@bbby.com"
			String forgetEmail=null
			String userItemId="userItemId1"
			String id=userItemId
			
			String challengeQuentionItemId="challengeQuentionItemId_1"
			String challengeQuenstion1="challengeQuenstion1"
			String challengeQuenstion2="challengeQuenstion2"
			String preferredAnswer1="preferredAnswer1"
			String preferredAnswer2="preferredAnswer2"
			RepositoryItem userItemFromEmail = Mock()
			userItemFromEmail.getRepositoryId() >> userItemId
			userItemFromEmail.getPropertyValue(BBBCoreConstants.ID) >>id
			
			MutableRepositoryItem challengeQuentionItem =null
			Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>()
			
			Map<String,String> quesMap1 =new HashMap<String,String>()
			quesMap1.put(challengeQuenstion1, challengeQuenstion1)
			
			Map<String,String> quesMap2 =new HashMap<String,String>()
			quesMap2.put(challengeQuenstion2, challengeQuenstion2)
			
			questionMap.put(BBBCoreConstants.STRING_ONE, quesMap1)
			questionMap.put(BBBCoreConstants.STRING_TWO, quesMap2)
			Profile profileMock= Mock()
		
			1* requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >>profileMock
			1* profileMock.getPropertyValue(BBBCoreConstants.ID) >> id
			0* profileToolMock.getItemFromEmail(_) >> userItemFromEmail
			1* profileToolMock.fetchProfileIdFromChallengeQuestionRepository(userItemFromEmail.getRepositoryId()) >> challengeQuentionItem
			1* profileToolMock.fetchChallengeQuestionsFromRepository() >> questionMap
			
		
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(emaiId)
			
		then:
			
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* requestMock.setParameter("challengQuestionExist", false)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1, challengeQuenstion1)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2, challengeQuenstion2)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_ANSWER_1, Base64.decodeToString(preferredAnswer1))
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_ANSWER_2, Base64.decodeToString(preferredAnswer2))
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP, _)
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP1, _);
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP2, _);
			1* requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : End")
		}
	
	
	def"fetchchallengeQuestion when Forget Email is not null in session and and request in from login page |  challenge quenstion  Item is null for userID"(){
		given:
			String emaiId="email@bbby.com"
			String forgetEmail="email@bbby.com"
			String userItemId="userItemId1"
			String id=userItemId
			String sessionforgetEmail="email@bbby.com"
			String challengeQuentionItemId="challengeQuentionItemId_1"
			String challengeQuenstion1="challengeQuenstion1"
			String challengeQuenstion2="challengeQuenstion2"
			String preferredAnswer1="preferredAnswer1"
			String preferredAnswer2="preferredAnswer2"
			RepositoryItem userItemFromEmail = Mock()
			userItemFromEmail.getRepositoryId() >> userItemId
			userItemFromEmail.getPropertyValue(BBBCoreConstants.ID) >>id
			
			MutableRepositoryItem challengeQuentionItem =null
			Map<String,Map<String,String>> questionMap= new HashMap<String,Map<String,String>>()
			
			Map<String,String> quesMap1 =new HashMap<String,String>()
			quesMap1.put(challengeQuenstion1, challengeQuenstion1)
			
			Map<String,String> quesMap2 =new HashMap<String,String>()
			quesMap2.put(challengeQuenstion2, challengeQuenstion2)
			
			questionMap.put(BBBCoreConstants.STRING_ONE, quesMap1)
			questionMap.put(BBBCoreConstants.STRING_TWO, quesMap2)
			Profile profileMock= Mock()
			
			requestMock.getParameter(BBBCoreConstants.FROM_LOGIN_PAGE) >> "true"
			3* requestMock.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL) >> sessionforgetEmail
			1* requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >>profileMock
			1* profileMock.getPropertyValue(BBBCoreConstants.ID) >> id
			0* profileToolMock.getItemFromEmail(_) >> userItemFromEmail
			1* profileToolMock.fetchProfileIdFromChallengeQuestionRepository(userItemFromEmail.getRepositoryId()) >> challengeQuentionItem
			1* profileToolMock.fetchChallengeQuestionsFromRepository() >> questionMap
			
		
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(emaiId)
			
		then:
			
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* requestMock.setParameter("challengQuestionExist", false)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1, challengeQuenstion1)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2, challengeQuenstion2)
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_ANSWER_1, Base64.decodeToString(preferredAnswer1))
			0* requestMock.setParameter(BBBCoreConstants.PREFERRED_ANSWER_2, Base64.decodeToString(preferredAnswer2))
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP, _)
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP1, _);
			1* requestMock.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP2, _);
			1* requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : End")
		}
	
	def"fetchchallengeQuestion Handle ServletException "(){
		given:
			
			String forgetEmail="email@bbby.com"
		
			requestMock.getParameter(BBBCoreConstants.FORGET_EMAIL) >> {throw new ServletException("ServletException")}
			
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(forgetEmail)
			
		then:
		    BBBSystemException exception =thrown()
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* testObj.logError("Challenge question service failed", _);
			1* requestMock.serviceParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
		}
	def"fetchchallengeQuestion Handle IOException "(){
		given:
			
			String forgetEmail="email@bbby.com"
		
			requestMock.getParameter(BBBCoreConstants.FORGET_EMAIL) >> {throw new IOException("IOException")}
			
		when:
			Map<String,Object> challengeQuestions=		testObj.fetchchallengeQuestion(forgetEmail)
			
		then:
			BBBSystemException exception =thrown()
			1* testObj.logDebug("ChallengeQuestionDroplet Service method : Start")
			1* testObj.logError("Challenge question service failed", _);
			1* requestMock.serviceParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
		}
	
	
}
