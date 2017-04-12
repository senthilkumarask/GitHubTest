package com.bbb.tools

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.specification.BBBExtendedSpec
import atg.adapter.gsa.GSARepository
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement
import atg.servlet.DynamoHttpServletRequest
import atg.servlet.ServletUtil
import atg.userprofiling.Profile
import net.sf.json.JSONObject

import java.util.List;

import com.bbb.account.profile.vo.ProfileEDWInfoVO
import com.bbb.constants.BBBCmsConstants
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBWebServiceConstants
import com.bbb.edwData.EDWProfileDataVO;
import com.bbb.framework.integration.util.ServiceHandlerUtil
import com.bbb.framework.messaging.handlers.MessageServiceHandler
import com.bbb.framework.performance.logger.PerformanceLogger
import com.bbb.framework.webservices.handlers.ServiceHandler;
import com.bbb.repository.RepositoryItemMock
import com.bbb.repositorywrapper.IRepositoryWrapper
import com.bbb.repositorywrapper.RepositoryWrapperImpl

/**
 *
 * @author Velmurugan Moorthy
 *
 * This class is created for unit testing using Spock
 * The class tested using this Specification class is BBBEdwRepositoryTools
 *
 */

class BBBEdwRepositoryToolsSpecification extends BBBExtendedSpec{

	public static final String EDW_SITE_SPECT_DATA = "EDWSiteSpectData"
	def BBBEdwRepositoryTools edwRepositoryToolsTestObj	/** The edw site spect data repository. */	def GSARepository edwSiteSpectDataRepositoryMock = Mock()	/** The profile adapter repository. */	def GSARepository edwProfileDataRepositoryMock = Mock()	/** The sites. */	def List<String> sites	/** The m user profile repository. */	def MutableRepository userProfileRepositoryMock = Mock()
	def Profile profileObj = Mock()
		/** The edw ttl. */	def String edwTTL	/** The data center map. */	def Map<String, String> dataCenterMap	/** The dc prefix. */	def String dcPrefix	def DynamoHttpServletRequest requestMock = Mock()
	def PerformanceLogger perfMock = Mock()
		
	def ProfileEDWInfoVO profileEDWInfoVOMock = Mock()
	
	def GSARepository GSARepositoryMock = Mock()
	
	def IRepositoryWrapper iRepositoryWrapper
	
	def RepositoryItem  edwSiteSpectDataRepositoryItemMock = Mock()
	
	def MutableRepositoryItem edwProfileMock = Mock()
	
	def MutableRepositoryItem profileRepositoryItemMock = Mock()
	
    def ServiceHandlerUtil serviceHandlerUtilMock = new ServiceHandlerUtil()
	
	def edwSiteSpectDataRepositoryItemArray = new RepositoryItem[100]
	
		
	def setup(){
		iRepositoryWrapper = new RepositoryWrapperImpl(GSARepositoryMock);
		edwRepositoryToolsTestObj = Spy()
		
		ServletUtil.setCurrentRequest(requestMock)
		requestMock.getContextPath() >> "/store"
		requestMock.resolveName(BBBSpockTestConstants.PERFORMANCE_LOGGER_PATH) >> perfMock	 	
		perfMock.isEnableCustomComponentsMonitoring() >> false
		profileEDWInfoVOMock.isSessionTIBCOcall() >> false 
		profileEDWInfoVOMock.isEdwDataStale() >> true //- removing this assignment logic only makes the code work - should find why?
		profileEDWInfoVOMock.getUserToken() >> "1"
		edwRepositoryToolsTestObj.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["1"]
		//edwRepositoryToolsTestObj = new BBBEdwRepositoryTools(edwSiteSpectDataRepository : edwSiteSpectDataRepositoryMock, dcPrefix : "atg/dynamo/service/IdGenerator.dcPrefix" , edwTTL : 7, sites : ["BEDBATHUS", "BUYBUYBABY", "BEDBATHCA"],dataCenterMap : ["DC1":"AN","DC":"SD","DC3":"UN"],edwProfileDataRepository : edwProfileDataRepositoryMock);		
		edwRepositoryToolsTestObj.getEdwProfileDataRepository() >> edwSiteSpectDataRepositoryMock
		edwRepositoryToolsTestObj.getDcPrefix() >> "atg/dynamo/service/IdGenerator.dcPrefix"
		edwRepositoryToolsTestObj.getEdwTTL() >> "7"
		edwRepositoryToolsTestObj.getSites() >> ["BEDBATHUS", "BUYBUYBABY", "BEDBATHCA"]
		edwRepositoryToolsTestObj.getDataCenterMap() >> ["DC1":"AN","DC":"SD","DC3":"UN"]
		edwRepositoryToolsTestObj.getEdwProfileDataRepository() >> edwProfileDataRepositoryMock
		edwRepositoryToolsTestObj.getUserProfileRepository() >> userProfileRepositoryMock
		MessageServiceHandler messageServiceHandlerMock = Mock()
		ServiceHandlerUtil.setMsgservicehandlerif(messageServiceHandlerMock)
														  
	}
	
	/*
	 * populateEDWProfileData() - test cases - starts
	 */
	
	def "populateEDWProfileData - happy flow" () {
		
			given :
					def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
					def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()					
					def edwDataKey = "somedata"
					def tempProfileId = "3423434"
					def testEmailId = "email@gmail.com"
					def profileAttributeTableCover = "Table covers:German covers"
					def profileAttributeAge = "age:50"
					def profileAttributeValues = profileAttributeTableCover + "|" + profileAttributeAge
					def ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
					def RepositoryView profileEDWViewMock = Mock()
					def RqlStatement rqlStatementMock = Mock()					
					def edwDataRql = "profileId = ?0"
					
					iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
					edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
					
					List<String> innerKeyList  = new ArrayList<String>();
					innerKeyList.add("Table covers");
					innerKeyList.add("Furniture covers");
					innerKeyList.add("Bench covers");
					edwKeys.put("sendToSiteSpect", innerKeyList)
					
					profileEDWInfoVOMock.getEmail() >> testEmailId
					profileEDWInfoVOMock.getATGProfileID() >> tempProfileId
					profileEDWInfoVOMock.isSessionTIBCOcall() >> false
					profileEDWInfoVOMock.isEdwDataStale() >> true
					
					edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
					edwRepositoryToolsTestObj.getEdwProfileDataRepository().getView(BBBCoreConstants.EDW_ITEM_DESCRIPTOR) >> profileEDWViewMock
					edwRepositoryToolsTestObj.getEdwDataRqlStmt() >> rqlStatementMock
					rqlStatementMock.executeQuery(profileEDWViewMock,["profileEdw01"]) >> [edwProfileMock]
					rqlStatementMock.executeQuery(*_) >>  [edwProfileMock]
					def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
					def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
										
					userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
					
					edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
					edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
					edwRepositoryToolsTestObj.getEDWAttributes() >> edwKeys
					edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
					edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["false"]
					
			when :
			        
				   def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, profileEDWInfoVOMock)
				   edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId)
				   
			then :
					profileEDWInfoVO.getATGProfileID() == tempProfileId
					profileEDWInfoVO.getEmail() == testEmailId
					profileEDWInfoVO.isEdwDataStale() == true
					profileEDWInfoVO.isSessionTIBCOcall() == false
					profileEDWInfoVO.getUserToken() == "1"
					
		}
	
	
	def "populateEDWProfileData - isSessionTIBCOcall is enabled (true)" () {
		
			given :
					def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
					def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
					def edwDataKey = "somedata"
					def tempProfileId = "3423434"
					def testEmailId = "email@gmail.com"
					def profileAttributeTableCover = "Table covers:German covers"
					def profileAttributeAge = "age:50"
					def profileAttributeValues = profileAttributeTableCover + "|" + profileAttributeAge
					def ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
					def RepositoryView profileEDWViewMock = Mock()
					def RqlStatement rqlStatementMock = Mock()
					def edwDataRql = "profileId = ?0"
					
					edwRepositoryToolsTestObj = Spy()
					
					edwRepositoryToolsTestObj.getEdwProfileDataRepository() >> edwSiteSpectDataRepositoryMock
					edwRepositoryToolsTestObj.getDcPrefix() >> "atg/dynamo/service/IdGenerator.dcPrefix"
					edwRepositoryToolsTestObj.getEdwTTL() >> "7"
					edwRepositoryToolsTestObj.getSites() >> ["BEDBATHUS", "BUYBUYBABY", "BEDBATHCA"]
					edwRepositoryToolsTestObj.getDataCenterMap() >> ["DC1":"AN","DC":"SD","DC3":"UN"]
					edwRepositoryToolsTestObj.getEdwProfileDataRepository() >> edwProfileDataRepositoryMock
					edwRepositoryToolsTestObj.getUserProfileRepository() >> userProfileRepositoryMock
					
					def ProfileEDWInfoVO tempProfileEDWInfoVOMock = Mock()
					
					iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
					edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
					
					List<String> innerKeyList  = new ArrayList<String>();
					innerKeyList.add("Table covers");
					innerKeyList.add("Furniture covers");
					innerKeyList.add("Bench covers");
					edwKeys.put("sendToSiteSpect", innerKeyList)
					
					tempProfileEDWInfoVOMock.getEmail() >> testEmailId
					tempProfileEDWInfoVOMock.getATGProfileID() >> tempProfileId
					tempProfileEDWInfoVOMock.isSessionTIBCOcall() >> true
					tempProfileEDWInfoVOMock.isEdwDataStale() >> true
					
					edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
					edwRepositoryToolsTestObj.getEdwProfileDataRepository().getView(BBBCoreConstants.EDW_ITEM_DESCRIPTOR) >> profileEDWViewMock
					edwRepositoryToolsTestObj.getEdwDataRqlStmt() >> rqlStatementMock
					rqlStatementMock.executeQuery(profileEDWViewMock,["profileEdw01"]) >> [edwProfileMock]
					rqlStatementMock.executeQuery(*_) >>  [edwProfileMock]
					def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
					def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
										
					userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
					
					edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
					edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
					edwRepositoryToolsTestObj.getEDWAttributes() >> edwKeys
					edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
					edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["false"]
					
			when :
					
				   def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, tempProfileEDWInfoVOMock)
				   edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId)
				   
			then :
					profileEDWInfoVO.getATGProfileID() == tempProfileId
					profileEDWInfoVO.getEmail() == testEmailId
					profileEDWInfoVO.isEdwDataStale() == true
					profileEDWInfoVO.isSessionTIBCOcall() == true
					profileEDWInfoVO.getUserToken() == null
					0 * tempProfileEDWInfoVOMock.setEdwProfileAttributes(edwKeys.get(BBBCoreConstants.IS_TIBCODATA));
					0 * tempProfileEDWInfoVOMock.setSessionTIBCOcall(true);
					0 * edwRepositoryToolsTestObj.submitEDWProfileDataMesssage(*_)
		}
	
	def "populateEDWProfileData - isEdwDataStale is disabled (false)" () {
		
			given :
					def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
					def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
					def edwDataKey = "somedata"
					def tempProfileId = "3423434"
					def testEmailId = "email@gmail.com"
					def profileAttributeTableCover = "Table covers:German covers"
					def profileAttributeAge = "age:50"
					def profileAttributeValues = profileAttributeTableCover + "|" + profileAttributeAge
					def ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
					def RepositoryView profileEDWViewMock = Mock()
					def RqlStatement rqlStatementMock = Mock()
					def edwDataRql = "profileId = ?0"
					
					def ProfileEDWInfoVO tempProfileEDWInfoVOMock = Mock()
					
					iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
					edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
					
					List<String> innerKeyList  = new ArrayList<String>();
					innerKeyList.add("Table covers");
					innerKeyList.add("Furniture covers");
					innerKeyList.add("Bench covers");
					edwKeys.put("sendToSiteSpect", innerKeyList)
					
					tempProfileEDWInfoVOMock.getEmail() >> testEmailId
					tempProfileEDWInfoVOMock.getATGProfileID() >> tempProfileId
					tempProfileEDWInfoVOMock.isSessionTIBCOcall() >> true
					tempProfileEDWInfoVOMock.isEdwDataStale() >> false
					
					edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
					edwRepositoryToolsTestObj.getEdwProfileDataRepository().getView(BBBCoreConstants.EDW_ITEM_DESCRIPTOR) >> profileEDWViewMock
					edwRepositoryToolsTestObj.getEdwDataRqlStmt() >> rqlStatementMock
					rqlStatementMock.executeQuery(profileEDWViewMock,["profileEdw01"]) >> [edwProfileMock]
					rqlStatementMock.executeQuery(*_) >>  [edwProfileMock]
					def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
					def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
										
					userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
					
					edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
					edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
					edwRepositoryToolsTestObj.getEDWAttributes() >> edwKeys
					edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
					edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["false"]
					
			when :
					
				   def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, tempProfileEDWInfoVOMock)
				   edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId)
				   
			then :
					profileEDWInfoVO.getATGProfileID() == tempProfileId
					profileEDWInfoVO.getEmail() == testEmailId
					profileEDWInfoVO.isEdwDataStale() == false
					profileEDWInfoVO.isSessionTIBCOcall() == true
					profileEDWInfoVO.getUserToken() == null
					
					0 * tempProfileEDWInfoVOMock.setEdwProfileAttributes(edwKeys.get(BBBCoreConstants.IS_TIBCODATA));
					0 * tempProfileEDWInfoVOMock.setSessionTIBCOcall(true);
					0 * edwRepositoryToolsTestObj.submitEDWProfileDataMesssage(*_)
					
		}
	
	def "populateEDWProfileData - profileHeaderItem is not set (null)" () {
		
			given :
					def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
					def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
					def edwDataKey = "somedata"
					def tempProfileId = "3423434"
					def testEmailId = "test@yopmail.com"
					def profileAttributeName = "name:some"
					def profileAttributeAge = "age:50"
					def RepositoryItemMock edwSiteItemRepoMock = new RepositoryItemMock()
					def RepositoryItem[] edwSiteItemRepoMockList = [edwSiteItemRepoMock]

				   List<String> innerKeyList  = new ArrayList<String>();
				   innerKeyList.add("Table covers");
				   innerKeyList.add("Furniture covers");
				   innerKeyList.add("Bench covers");
				   edwKeys.put("sendToSiteSpect", innerKeyList)
				   
					def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
					def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
					def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
					def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
					
					profileEDWInfoVOMock.getEmail() >> testEmailId
					profileEDWInfoVOMock.getATGProfileID() >> tempProfileId
					profileEDWInfoVOMock.isSessionTIBCOcall() >> false
					profileEDWInfoVOMock.isEdwDataStale() >> true
					
					edwRepositoryToolsTestObj.getUserProfileRepository().getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
					edwRepositoryToolsTestObj.getEDWAttributes() >> edwKeys
					edwRepositoryToolsTestObj.getEdwRepoItems(*_) >> edwSiteItemRepoMockList
					edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> null 
					edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["true"]
					
			when :
				   def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, profileEDWInfoVOMock)
				   
			then : 
				   profileEDWInfoVO.email == testEmailId
				   profileEDWInfoVO.getATGProfileID() == tempProfileId
				   profileEDWInfoVO.getEmail() == testEmailId
				   profileEDWInfoVO.isEdwDataStale() == true
				   profileEDWInfoVO.isSessionTIBCOcall() == false
		}
	
	def "populateEDWProfileData - config key values for  EDW_COFIG_TTL key is not set (null)" () {
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				List<String> innerKeyList  = new ArrayList<String>();
				innerKeyList.add("Table covers");
				innerKeyList.add("Furniture covers");
				innerKeyList.add("Bench covers");
				edwKeys.put("sendToSiteSpect", innerKeyList)
				
				profileEDWInfoVOMock.getEmail() >> testEmailId
				profileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				profileEDWInfoVOMock.isSessionTIBCOcall() >> false
				profileEDWInfoVOMock.isEdwDataStale() >> true
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> [edwProfileMock]
				
				def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				edwRepositoryToolsTestObj.getEDWAttributes() >> edwKeys
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
		
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["false"]
		
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL) >> null
				
		when : 
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, profileEDWInfoVOMock)
				
		then : 

				0 * edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL).get(0)
				profileEDWInfoVO.email == testEmailId
				profileEDWInfoVO.getATGProfileID() == tempProfileId
				profileEDWInfoVO.getEmail() == testEmailId
				profileEDWInfoVO.isEdwDataStale() == true
				profileEDWInfoVO.isSessionTIBCOcall() == false
		
	}
	
	
	def "populateEDWProfileData - edwAttributes (edwKeys) is empty" () {
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				profileEDWInfoVOMock.getEmail() >> testEmailId
				profileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				profileEDWInfoVOMock.isSessionTIBCOcall() >> false
				profileEDWInfoVOMock.isEdwDataStale() >> true
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> [edwProfileMock]
				
				def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				edwRepositoryToolsTestObj.getEDWAttributes() >> edwKeys
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["false"]
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL) >> null
				
		when :
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, profileEDWInfoVOMock)
				
		then :
				
				0 * edwKeys.get(BBBCoreConstants.IS_SITESPECTDATA)
				0 * profileEDWInfoVOMock.setEdwProfileAttributes(edwKeys.get(BBBCoreConstants.IS_TIBCODATA));
				0 * profileEDWInfoVOMock.setSessionTIBCOcall(true);
				0 * edwRepositoryToolsTestObj.submitEDWProfileDataMesssage(*_)
				
	}
	
	
	def "populateEDWProfileData - edw attributes(edwKeys) is not set and isEdwDataStale disabled (false)"(){
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				List<String> innerKeyList  = new ArrayList<String>();
				innerKeyList.add("Table covers");
				innerKeyList.add("Furniture covers");
				innerKeyList.add("Bench covers");
				edwKeys.put("sendToSiteSpect", innerKeyList)
				
				profileEDWInfoVOMock.getEmail() >> testEmailId
				profileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				profileEDWInfoVOMock.isSessionTIBCOcall() >> false
				profileEDWInfoVOMock.isEdwDataStale() >> false
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> [edwProfileMock]
				
				def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["false"]
				
		when :
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, profileEDWInfoVOMock)
				
		then :
					profileEDWInfoVO.getATGProfileID() == tempProfileId
					profileEDWInfoVO.getEmail() == testEmailId					
					0 * profileEDWInfoVOMock.setEdwProfileAttributes(edwKeys.get(BBBCoreConstants.IS_TIBCODATA));
					0 * profileEDWInfoVOMock.setSessionTIBCOcall(true);
					0 * edwRepositoryToolsTestObj.submitEDWProfileDataMesssage(*_)
	}

	def "populateEDWProfileData - isSessionTIBCOcall disabled(false), isEdwDataStale enabled(true), profileHeaderRepositoryItem is not set(null) and isTibcoEnable false"(){
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				List<String> innerKeyList  = new ArrayList<String>();
				innerKeyList.add("Table covers");
				innerKeyList.add("Furniture covers");
				innerKeyList.add("Bench covers");
				edwKeys.put("sendToSiteSpect", innerKeyList)
				
				profileEDWInfoVOMock.getEmail() >> testEmailId
				profileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				profileEDWInfoVOMock.isSessionTIBCOcall() >> false
				profileEDWInfoVOMock.isEdwDataStale() >> true
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> null
				
				def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["false"]
				
		when :
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, profileEDWInfoVOMock)
				
		then :
					profileEDWInfoVO.getATGProfileID() == tempProfileId
					profileEDWInfoVO.getEmail() == testEmailId					
					0 * profileEDWInfoVOMock.setEdwProfileAttributes(edwKeys.get(BBBCoreConstants.IS_TIBCODATA));
					0 * profileEDWInfoVOMock.setSessionTIBCOcall(true);
					0 * edwRepositoryToolsTestObj.submitEDWProfileDataMesssage(*_)
	}

	def "populateEDWProfileData - isSessionTIBCOcall, isEdwDataStale, and isTibcoEnable are disabed(false) | profileHeaderRepositoryItem has valid data"(){
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				List<String> innerKeyList  = new ArrayList<String>();
				innerKeyList.add("Table covers");
				innerKeyList.add("Furniture covers");
				innerKeyList.add("Bench covers");
				edwKeys.put("sendToSiteSpect", innerKeyList)
				def ProfileEDWInfoVO tempProfileEDWInfoVOMock = Mock() 
				
				tempProfileEDWInfoVOMock.getEmail() >> testEmailId
				tempProfileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				tempProfileEDWInfoVOMock.isSessionTIBCOcall() >> false
				tempProfileEDWInfoVOMock.isEdwDataStale() >> false
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> [edwProfileMock]
				
				def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["false"]
				
		when :
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, tempProfileEDWInfoVOMock)
				
		then :
				profileEDWInfoVO.getATGProfileID() == tempProfileId
				profileEDWInfoVO.getEmail() == testEmailId					
	}
	
	
	def "populateEDWProfileData - isSessionTIBCOcall enabled (true), isEdwDataStale disabled(false) profileHeaderRepositoryItem false_isNotNull isTibcoEnable true" () {
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				List<String> innerKeyList  = new ArrayList<String>();
				innerKeyList.add("Table covers");
				innerKeyList.add("Furniture covers");
				innerKeyList.add("Bench covers");
				edwKeys.put("sendToSiteSpect", innerKeyList)
				def ProfileEDWInfoVO tempProfileEDWInfoVOMock = Mock()
				
				tempProfileEDWInfoVOMock.getEmail() >> testEmailId
				tempProfileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				tempProfileEDWInfoVOMock.isSessionTIBCOcall() >> true
				tempProfileEDWInfoVOMock.isEdwDataStale() >> false
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> [edwProfileMock]
				
				def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["true"]
				
		when :
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, tempProfileEDWInfoVOMock)
				
		then :
				profileEDWInfoVO.getATGProfileID() == tempProfileId
				profileEDWInfoVO.getEmail() == testEmailId					

	}
	
	def "populateEDWProfileData - isSessionTIBCOcall is enabled(true), isEdwDataStale is enabled(true), isTibcoEnable is enabled(true) and profileHeaderRepositoryItem has valid data "(){
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				List<String> innerKeyList  = new ArrayList<String>();
				innerKeyList.add("Table covers");
				innerKeyList.add("Furniture covers");
				innerKeyList.add("Bench covers");
				edwKeys.put("sendToSiteSpect", innerKeyList)
				def ProfileEDWInfoVO tempProfileEDWInfoVOMock = Mock()
				
				tempProfileEDWInfoVOMock.getEmail() >> testEmailId
				tempProfileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				tempProfileEDWInfoVOMock.isSessionTIBCOcall() >> true
				tempProfileEDWInfoVOMock.isEdwDataStale() >> true
				
				profileEDWInfoVOMock.getEmail() >> testEmailId
				profileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				profileEDWInfoVOMock.isSessionTIBCOcall() >> false
				profileEDWInfoVOMock.isEdwDataStale() >> false
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> [edwProfileMock]
				
				def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["true"]
				
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL) >> null
				
		when :
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, tempProfileEDWInfoVOMock)
				
		then :
				profileEDWInfoVO.getATGProfileID() == tempProfileId
				profileEDWInfoVO.getEmail() == testEmailId					
	
	}
	
	def "populateEDWProfileData isSessionTIBCOcall and isEdwDataStale are disabled(false), profileHeaderRepositoryItem is null and isTibcoEnable is enabled(true)"(){
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				List<String> innerKeyList  = new ArrayList<String>();
				innerKeyList.add("Table covers");
				innerKeyList.add("Furniture covers");
				innerKeyList.add("Bench covers");
				edwKeys.put("sendToSiteSpect", innerKeyList)
				def ProfileEDWInfoVO tempProfileEDWInfoVOMock = Mock()
				
				tempProfileEDWInfoVOMock.getEmail() >> testEmailId
				tempProfileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				tempProfileEDWInfoVOMock.isSessionTIBCOcall() >> false
				tempProfileEDWInfoVOMock.isEdwDataStale() >> false
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> [edwProfileMock]
				
				def RepositoryItemMock profileHeaderRepositoryItem = null
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["true"]
				
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL) >> null
				
		when :
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, tempProfileEDWInfoVOMock)
				
		then :
				profileEDWInfoVO.getATGProfileID() == tempProfileId
				profileEDWInfoVO.getEmail() == testEmailId					
	
	}
	
	def "populateEDWProfileData - isSessionTIBCOcall and isEdwDataStale are disabled(false) | profileHeaderRepositoryItem is not set(null), isTibcoEnable is enabled"(){
		
		given :
				
				def Map<String, List<String>> edwKeys = new HashMap<String, List<String>>()
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def edwDataKey = "somedata"
				def tempProfileId = "3423434"
				def testEmailId = "email@gmail.com"
				def profileAttributeName = "name:some"
				def profileAttributeAge = "age:50"
				def edwTTL = "7"
				def profileAttributeValues = profileAttributeName + "|" + profileAttributeAge
				def  ProfileEDWInfoVO profileEdwInfoObj = new ProfileEDWInfoVO()
				
				iRepositoryWrapperMock.queryRepositoryItems(EDW_SITE_SPECT_DATA, "ALL", null,true) >> [edwSiteSpectDataRepositoryItemMock]
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				
				List<String> innerKeyList  = new ArrayList<String>();
				innerKeyList.add("Table covers");
				innerKeyList.add("Furniture covers");
				innerKeyList.add("Bench covers");
				edwKeys.put("sendToSiteSpect", innerKeyList)
				def ProfileEDWInfoVO tempProfileEDWInfoVOMock = Mock()
				
				tempProfileEDWInfoVOMock.getEmail() >> testEmailId
				tempProfileEDWInfoVOMock.getATGProfileID() >> tempProfileId
				tempProfileEDWInfoVOMock.isSessionTIBCOcall() >> false
				tempProfileEDWInfoVOMock.isEdwDataStale() >> false
				
				edwSiteSpectDataRepositoryItemMock.getPropertyValue(BBBCoreConstants.EDW_Data_Key) >> edwDataKey
				edwRepositoryToolsTestObj.getProfileHeaderItem(tempProfileId) >> [edwProfileMock]
				
				def RepositoryItemMock profileHeaderRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] profileHeaderRepositoryItemList = [profileHeaderRepositoryItem]
									
				userProfileRepositoryMock.getItem(tempProfileId, BBBCoreConstants.USER) >> profileRepositoryItemMock
				edwProfileMock.getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE) >> new Date()
				edwProfileMock.getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES) >> profileAttributeValues
				edwRepositoryToolsTestObj.getEdwTTL() >> edwTTL
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL)  >> ["1"]
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY) >> ["true"]
				
				edwRepositoryToolsTestObj.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL) >> null
				
		when :
				def profileEDWInfoVO = edwRepositoryToolsTestObj.populateEDWProfileData(tempProfileId, tempProfileEDWInfoVOMock)
				
		then :
				profileEDWInfoVO.getATGProfileID() == tempProfileId
				profileEDWInfoVO.getEmail() == testEmailId					
	
	}
		
	/*
	 * populateEDWProfileData() - test cases - ENDS
	 */
	
		
	/*
	 * submitEDWProfileDataMessage() - test cases - STARTS
	 */
	
	def "submitEDWProfileDataMesssage - happy flow" () {
		
		given :
				EDWProfileDataVO edwDataRequest = Mock()
		        edwRepositoryToolsTestObj.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)  >> ["1"] 
				profileEDWInfoVOMock.getDataCentre() >> "DC1" 
				profileEDWInfoVOMock.getUserToken() >>  "1"
				edwDataRequest.getProfileEDWData() >> profileEDWInfoVOMock
				MessageServiceHandler messageServiceHandlerMock = Mock()
				ServiceHandlerUtil.sendTextMessage(edwDataRequest)
			
		when  : 
				def userToken = edwRepositoryToolsTestObj.submitEDWProfileDataMesssage(profileEDWInfoVOMock)
		
	  	then : 
		      (1.._) * profileEDWInfoVOMock.setDataCentre(_)
		      (1.._) * profileEDWInfoVOMock.setUserToken(_)
			   
	}
	
	/*
	 * submitEDWProfileDataMessage() - test cases - ENDS
	 */
	
	/**
	 *  getEdwAttributes - test cases - STARTS 
	 */
	
	def "getEdwAttributes - happy flow" () {
		
		given :
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def RepositoryItemMock edwSiteItemRepoMock = new RepositoryItemMock()
				def RepositoryItem[] edwSiteItemRepoMockList = [edwSiteItemRepoMock]
				def RepositoryItem tempRepoItem = Mock()
				def edwSiteDataMap = new HashMap<>()
				def edwSites = new ArrayList<>()
				def siteID = "BBB";
				def profileAttrKey = "Sample_Edw_data_key_BBB"
				
				edwSites.add(siteID)				
				edwSiteDataMap.put(BBBCoreConstants.EDW_Data_Key, profileAttrKey)
				edwSiteDataMap.put(BBBCoreConstants.IS_SITESPECTDATA, true)
				edwSiteDataMap.put(BBBCoreConstants.IS_TIBCODATA, true)
				
				edwRepositoryToolsTestObj.getSiteId() >> siteID
				
				edwRepositoryToolsTestObj.getSites() >> edwSites				
				edwSiteItemRepoMock.setProperties(edwSiteDataMap)
				
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				edwRepositoryToolsTestObj.getEdwRepoItems(*_) >> edwSiteItemRepoMockList
		
		when : 
				def edwAttributes = edwRepositoryToolsTestObj.getEDWAttributes()
				def edwSiteSpectKeys =  edwAttributes.get(BBBCoreConstants.IS_SITESPECTDATA)
				def edwTibcoKeys =	edwAttributes.get(BBBCoreConstants.IS_TIBCODATA)
			
		then :
		
			edwSiteSpectKeys.contains(profileAttrKey)
			edwTibcoKeys.contains(profileAttrKey)
	}
	
	def "getEdwAttributes - Canada site happy flow" () {
		
		given :
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def RepositoryItemMock edwSiteItemRepoMock = new RepositoryItemMock()
				def RepositoryItem[] edwSiteItemRepoMockList = [edwSiteItemRepoMock]
				def RepositoryItem tempRepoItem = Mock()
				def edwSiteDataMap = new HashMap<>()
				def edwSites = new ArrayList<>()
				def siteID = "BedBathCanada";
				def profileAttrKey = "Sample_Edw_data_key_BedBathCanada"
				
				edwSites.add(siteID)
				edwSiteDataMap.put(BBBCoreConstants.EDW_Data_Key, profileAttrKey)
				edwSiteDataMap.put(BBBCoreConstants.IS_SITESPECTDATA, true)
				edwSiteDataMap.put(BBBCoreConstants.IS_TIBCODATA, true)
				
				edwRepositoryToolsTestObj.getSiteId() >> siteID
				
				edwRepositoryToolsTestObj.getSites() >> edwSites
				edwSiteItemRepoMock.setProperties(edwSiteDataMap)
				
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				edwRepositoryToolsTestObj.getEdwRepoItems(*_) >> edwSiteItemRepoMockList
		
		when :
				def edwAttributes = edwRepositoryToolsTestObj.getEDWAttributes()
				def edwSiteSpectKeys =  edwAttributes.get(BBBCoreConstants.IS_SITESPECTDATA)
				def edwTibcoKeys =	edwAttributes.get(BBBCoreConstants.IS_TIBCODATA)
			
		then :
		
			edwSiteSpectKeys.contains(profileAttrKey)
			edwSiteSpectKeys.contains(profileAttrKey)
			
	}

	
	def "getEdwAttributes - RQL query returned no EDW repoistory items(null)" () {

		given :
		
		def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
		def RepositoryItemMock edwSiteItemRepoMock = new RepositoryItemMock()
		def RepositoryItem[] edwSiteItemRepoMockList = [edwSiteItemRepoMock]
		def RepositoryItem tempRepoItem = Mock()
		def edwSiteDataMap = new HashMap<>()
		def edwSites = new ArrayList<>()
		def siteID = "BBB";
		def profileAttrKey = "Sample_Edw_data_key_BBB"

		edwSites.add(siteID)
		edwSiteDataMap.put(BBBCoreConstants.EDW_Data_Key, profileAttrKey)
		edwSiteDataMap.put(BBBCoreConstants.IS_SITESPECTDATA, true)
		edwSiteDataMap.put(BBBCoreConstants.IS_TIBCODATA, true)

		edwRepositoryToolsTestObj.getSiteId() >> siteID

		edwRepositoryToolsTestObj.getSites() >> edwSites
		edwSiteItemRepoMock.setProperties(edwSiteDataMap)

		edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
		edwRepositoryToolsTestObj.getEdwRepoItems(*_) >> null


		when :
		def edwAttributes = edwRepositoryToolsTestObj.getEDWAttributes()
		def edwSiteSpectKeys =  edwAttributes.get(BBBCoreConstants.IS_SITESPECTDATA)
		def edwTibcoKeys =	edwAttributes.get(BBBCoreConstants.IS_TIBCODATA)

		then :
		
		edwAttributes.isEmpty() == true
	}

	def "getEdwAttributes - RQL query returns empty EDW repoistory item" () {
		
		given :
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def RepositoryItemMock edwSiteItemRepoMock = new RepositoryItemMock()
				def RepositoryItem[] edwSiteItemRepoMockList = []
				def RepositoryItem tempRepoItem = Mock()
				def edwSiteDataMap = new HashMap<>()
				def edwSites = new ArrayList<>()
				def siteID = "BBB";
				def profileAttrKey = "Sample_Edw_data_key_BBB"
				
				edwSites.add(siteID)
				edwSiteDataMap.put(BBBCoreConstants.EDW_Data_Key, profileAttrKey)
				edwSiteDataMap.put(BBBCoreConstants.IS_SITESPECTDATA, true)
				edwSiteDataMap.put(BBBCoreConstants.IS_TIBCODATA, true)
				
				edwRepositoryToolsTestObj.getSiteId() >> siteID
				
				edwRepositoryToolsTestObj.getSites() >> edwSites
				edwSiteItemRepoMock.setProperties(edwSiteDataMap)
				
				//edwSiteItemRepoMock.setP
				
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				edwRepositoryToolsTestObj.getEdwRepoItems(*_) >> edwSiteItemRepoMockList
				
		
		when :
				def edwAttributes = edwRepositoryToolsTestObj.getEDWAttributes()
				def edwSiteSpectKeys =  edwAttributes.get(BBBCoreConstants.IS_SITESPECTDATA)
				def edwTibcoKeys =	edwAttributes.get(BBBCoreConstants.IS_TIBCODATA)
			
		then :
		
			edwAttributes.isEmpty() == true
			
	}
	
	def "getEdwAttributes - siteSpect is disabled (false)" () {
		
		given :
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def RepositoryItemMock edwSiteItemRepoMock = new RepositoryItemMock()
				def RepositoryItem[] edwSiteItemRepoMockList = [edwSiteItemRepoMock]
				def RepositoryItem tempRepoItem = Mock()
				def edwSiteDataMap = new HashMap<>()
				def edwSites = new ArrayList<>()
				def siteID = "BBB";
				def profileAttrKey = "Sample_Edw_data_key_BBB"
				
				List<String> edwSiteSpectKeysMock = Mock()
				
				edwSites.add(siteID)
				edwSiteDataMap.put(BBBCoreConstants.EDW_Data_Key, profileAttrKey)
				edwSiteDataMap.put(BBBCoreConstants.IS_SITESPECTDATA, false)
				edwSiteDataMap.put(BBBCoreConstants.IS_TIBCODATA, false)
				
				edwRepositoryToolsTestObj.getSiteId() >> siteID
				
				edwRepositoryToolsTestObj.getSites() >> edwSites
				edwSiteItemRepoMock.setProperties(edwSiteDataMap)
				
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				edwRepositoryToolsTestObj.getEdwRepoItems(*_) >> edwSiteItemRepoMockList
		
		when :
				def edwAttributes = edwRepositoryToolsTestObj.getEDWAttributes()
				def edwSiteSpectKeys =  edwAttributes.get(BBBCoreConstants.IS_SITESPECTDATA)
				def edwTibcoKeys =	edwAttributes.get(BBBCoreConstants.IS_TIBCODATA)
			
		then :
		
			edwSiteDataMap.get(BBBCoreConstants.IS_SITESPECTDATA) == false
			edwSiteDataMap.get(BBBCoreConstants.IS_TIBCODATA) == false
			0 * edwSiteSpectKeysMock.add(profileAttrKey)
	}

	def "getEdwAttributes - siteID and siteContext are different (mismatching)" () {
		
		given :
				def RepositoryWrapperImpl iRepositoryWrapperMock = Mock()
				def RepositoryItemMock edwSiteItemRepoMock = new RepositoryItemMock()
				def RepositoryItem[] edwSiteItemRepoMockList = [edwSiteItemRepoMock]
				def RepositoryItem tempRepoItem = Mock()
				def edwSiteDataMap = new HashMap<>()
				def edwSites = new ArrayList<>()
				def siteID = "BUYBUYBABY";
				def profileAttrKey = "Sample_Edw_data_key_BEDBATHUS"
				
				List<String> edwSiteSpectKeysMock = Mock ()
				
				edwSites.add(siteID)
				edwSiteDataMap.put(BBBCoreConstants.EDW_Data_Key, profileAttrKey)
				edwSiteDataMap.put(BBBCoreConstants.IS_SITESPECTDATA, true)
				edwSiteDataMap.put(BBBCoreConstants.IS_TIBCODATA, true)
				
				edwRepositoryToolsTestObj.getSiteId() >> siteID
				
				edwRepositoryToolsTestObj.getSites() >> edwSites
				edwSiteItemRepoMock.setProperties(edwSiteDataMap)
				
				edwRepositoryToolsTestObj.getRepositoryWrapper() >> iRepositoryWrapperMock
				edwRepositoryToolsTestObj.getEdwRepoItems(*_) >> edwSiteItemRepoMockList
		
		when :
				def edwAttributes = edwRepositoryToolsTestObj.getEDWAttributes()
				def List<String> edwSiteSpectKeys =  edwAttributes.get(BBBCoreConstants.IS_SITESPECTDATA)
				def edwTibcoKeys =	edwAttributes.get(BBBCoreConstants.IS_TIBCODATA)
			
		then :
		
			edwSiteSpectKeys.isEmpty() == true
			0 * edwSiteSpectKeysMock.add(profileAttrKey)
			
	}
	/**
	 *  getEdwAttributes - test cases - ENDS
	 */
	
}