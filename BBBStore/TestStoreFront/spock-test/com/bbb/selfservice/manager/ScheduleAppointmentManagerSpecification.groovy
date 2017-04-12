package com.bbb.selfservice.manager

import java.util.List;
import java.util.Map;

import atg.multisite.SiteContextManager;
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile

import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.AppointmentVO;
import com.bbb.commerce.catalog.vo.ScheduleAppointmentVO;
import com.bbb.commerce.catalog.vo.StoreVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.repository.RepositoryItemMock;
import com.bbb.selfservice.common.StoreDetails;

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class ScheduleAppointmentManagerSpecification extends BBBExtendedSpec {

	ScheduleAppointmentManager testObj
	BBBCatalogToolsImpl bbbCatalogToolsImplMock = Mock()
	SearchStoreManager searchStoreManagerMock = Mock()
	Repository appointmentRepositoryMock = Mock()
	Repository storeRepositoryMock = Mock()
	RepositoryView repositoryViewMock = Mock()
	RqlStatement rqlStatementMock = Mock()
	RepositoryItemMock repositoryItemMock = Mock()
	Repository repositoryItemMock1 = Mock()
	Profile profileMock = Mock()
	StoreDetails storeDetailsMock = Mock()
	StoreVO storeVOMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = Spy()
		testObj.setSearchStoreManager(searchStoreManagerMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsImplMock)
		testObj.setAppointmentRepository(appointmentRepositoryMock)
		testObj.setStoreRepository(storeRepositoryMock)
		testObj.setIdAppointmentMappingQuery("id= ?0 and appointmentTypes contains ?1")
		testObj.setPreSelectedServiceRefQuery("appointmentCode= ?0")
		testObj.setAppointmentListQuery("enabled=1 and sites = ?0 order by appointmentName sort asc")
		testObj.setDefaultAppointmentType("OTH")
		testObj.setDefaultPreSelectedServiceRefValue(28)
	}
	
	////////////////////////////////////////TestCases for fetchEnabledAppointmentTypes --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public List<AppointmentVO> fetchEnabledAppointmentTypes(String pSiteId) ///////////
	
	def"fetchEnabledAppointmentTypes. This TC is the Happy flow of fetchEnabledAppointmentTypes"(){
		given:
			String siteId = "BedBathUS"
			1 * appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			testObj.rqlStatementQuery(testObj.getAppointmentListQuery()) >> rqlStatementMock
			RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
			RepositoryItemMock repositoryItem2 = new RepositoryItemMock()
			RepositoryItem[] repositoryItemList = [repositoryItem1,repositoryItem2]
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["BedBathUS"]) >> repositoryItemList
			repositoryItem1.setProperties(["appointmentCode":"code1","appointmentName":"name1"])
			repositoryItem2.setProperties(["appointmentCode":"code2","appointmentName":"name2"])
			
		when:
			List<AppointmentVO> results = testObj.fetchEnabledAppointmentTypes(siteId)
		then:
			results[0].getAppointmentCode().equals("code1")
			results[0].getAppointmentName().equals("name1")
			results[1].getAppointmentCode().equals("code2")
			results[1].getAppointmentName().equals("name2")
	}

	def"fetchEnabledAppointmentTypes. This TC is when RepositoryException thrown and repositoryItem is null"(){
		given:
			String siteId = "BedBathUS"
			1 * appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			testObj.rqlStatementQuery(testObj.getAppointmentListQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["BedBathUS"]) >> {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			List<AppointmentVO> results = testObj.fetchEnabledAppointmentTypes(siteId)
		then:
			results == []
			1 * testObj.logDebug('repositoryItems returned null')
			1 * testObj.logError('ScheduleAppointmentManager|fetchAppointmentTypes()|No data for site')
	}
	
	////////////////////////////////////////TestCases for fetchEnabledAppointmentTypes --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getScheduleAppointment --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public ScheduleAppointmentVO getScheduleAppointment(String storeId, String appointmentType, String registryId, String coregFN, String coregLN, String eventDate,String pageName) ///////////
	
	def"getScheduleAppointment. This TC is the Happy flow of getScheduleAppointment method"(){
		given:
			String storeId = "2345"
			String appointmentType = "type"
			String registryId = "23456545"
			String coregFN = "coregFN"
			String coregLN = "coregLN"
			String eventDate = "10/09/2017"
			String pageName = "pagename"
			String firstName = "john"
			String lastName = "Kennedy"
			String email = "bbbcustomer@gmail.com"
			String phoneNum = "8585858585"
			
			testObj.canScheduleAppointmentForSiteId(_) >> TRUE
			1 * bbbCatalogToolsImplMock.getAllValuesForKey("ThirdPartyURLs", "appointmentSkedgeURL") >> ["/skedge/url"]
			
			//getProfileData Private Method Coverage
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> firstName
			1 * profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> lastName
			1 * profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> email
			1 * profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> phoneNum
			1 * searchStoreManagerMock.fetchFavoriteStoreId(_, profileMock) >> "fac23456"
			
			//isApponitmentTypeValidForStore public Method Coverage
			1 * storeRepositoryMock.getView("store") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getIdAppointmentMappingQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["2345", "type"]) >> repositoryItemMock
			
			//fetchPreSelectedServiceRef Public Method Coverage
			1 * appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getPreSelectedServiceRefQuery()) >> rqlStatementMock
			RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
			RepositoryItem[] repositoryItemList = [repositoryItem1]
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["type"]) >> repositoryItemList
			repositoryItem1.setProperties(["preselectedServiceRef":2])
			
		when:
			ScheduleAppointmentVO results = testObj.getScheduleAppointment(storeId, appointmentType, registryId, coregFN, coregLN, eventDate, pageName)
		then:
			results.getPreSelectedServiceRef().equals(2)
			results.getSkedgeURL().equals("/skedge/url")
			results.isScheduleappointment().equals(TRUE)
			results.isDirectskedgeMe().equals(TRUE)
			results.isErrormodal().equals(FALSE)
			checkScheduleAppointmentVO(results, firstName, lastName, email, phoneNum, storeId, coregFN, coregLN, registryId, eventDate, appointmentType, pageName)
	}
	
	def"getScheduleAppointment. This TC is when storeId is null"(){
		given:
			String storeId = null
			String appointmentType = "OTH"
			String registryId = "23456545"
			String coregFN = "coregFN"
			String coregLN = "coregLN"
			String eventDate = "10/09/2017"
			String pageName = "pagename"
			String firstName = "john"
			String lastName = "Kennedy"
			String email = "bbbcustomer@gmail.com"
			String phoneNum = "8585858585"
			
			testObj.canScheduleAppointmentForSiteId(_) >> TRUE
			1 * bbbCatalogToolsImplMock.getAllValuesForKey("ThirdPartyURLs", "appointmentSkedgeURL") >> []
			
			//getProfileData Private Method Coverage
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> firstName
			1 * profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> lastName
			1 * profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> email
			1 * profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> phoneNum
			1 * searchStoreManagerMock.fetchFavoriteStoreId(_, profileMock) >> "fac23456"
			
			//isApponitmentTypeValidForStore public Method Coverage
			1 * storeRepositoryMock.getView("store") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getIdAppointmentMappingQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["fac23456", "OTH"]) >> repositoryItemMock
			
			//fetchPreSelectedServiceRef Public Method Coverage
			1 * appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getPreSelectedServiceRefQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["OTH"]) >> null
			
		when:
			ScheduleAppointmentVO results = testObj.getScheduleAppointment(storeId, appointmentType, registryId, coregFN, coregLN, eventDate, pageName)
		then:
			results.getPreSelectedServiceRef().equals(28)
			results.getSkedgeURL().equals(null)
			results.isScheduleappointment().equals(TRUE)
			results.isDirectskedgeMe().equals(TRUE)
			results.isErrormodal().equals(FALSE)
			checkScheduleAppointmentVO(results, firstName, lastName, email, phoneNum, storeId, coregFN, coregLN, registryId, eventDate, appointmentType, pageName)
			
	}
	
	def"getScheduleAppointment. This TC is when storeId is empty and appointmentType is null"(){
		given:
			String storeId = ""
			String appointmentType = null
			String registryId = "23456545"
			String coregFN = "coregFN"
			String coregLN = "coregLN"
			String eventDate = "10/09/2017"
			String pageName = "pagename"
			String firstName = "john"
			String lastName = "Kennedy"
			String email = "bbbcustomer@gmail.com"
			String phoneNum = "8585858585"
			testObj.canScheduleAppointmentForSiteId(_) >> TRUE
			
			//getProfileData Private Method Coverage
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> firstName
			1 * profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> lastName
			1 * profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> email
			1 * profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> phoneNum
			1 * searchStoreManagerMock.fetchFavoriteStoreId(_, profileMock) >> "fac23456"
			
		when:
			ScheduleAppointmentVO results = testObj.getScheduleAppointment(storeId, appointmentType, registryId, coregFN, coregLN, eventDate, pageName)
		then:
			results.getPreSelectedServiceRef().equals(0)
			results.getSkedgeURL().equals(null)
			results.isScheduleappointment().equals(TRUE)
			results.isDirectskedgeMe().equals(FALSE)
			results.isErrormodal().equals(TRUE)
			checkScheduleAppointmentVO(results, firstName, lastName, email, phoneNum, storeId, coregFN, coregLN, registryId, eventDate, appointmentType, pageName)
	}
	
	def"getScheduleAppointment. This TC is when acceptsAppointment returns false"(){
		given:
			String storeId = ""
			String appointmentType = null
			String registryId = "23456545"
			String coregFN = "coregFN"
			String coregLN = "coregLN"
			String eventDate = "10/09/2017"
			String pageName = "pagename"
			String firstName = "john"
			String lastName = "Kennedy"
			String email = "bbbcustomer@gmail.com"
			String phoneNum = "8585858585"
			testObj.canScheduleAppointmentForSiteId(_) >> FALSE
			
		when:
			ScheduleAppointmentVO results = testObj.getScheduleAppointment(storeId, appointmentType, registryId, coregFN, coregLN, eventDate, pageName)
		then:
			results.getPreSelectedServiceRef().equals(0)
			results.getSkedgeURL().equals(null)
			results.isScheduleappointment().equals(FALSE)
			results.isDirectskedgeMe().equals(FALSE)
			results.isErrormodal().equals(FALSE)
			checkScheduleAppointmentVO(results, firstName, lastName, email, phoneNum, storeId, coregFN, coregLN, registryId, eventDate, appointmentType, pageName)
			
	}
	
	def"getScheduleAppointment. This TC is when isTransient is false in getProfileData Private method"(){
		given:
			String storeId = null
			String appointmentType = "OTH"
			String registryId = "23456545"
			String coregFN = "coregFN"
			String coregLN = "coregLN"
			String eventDate = "10/09/2017"
			String pageName = "pagename"
			String firstName = "john"
			String lastName = "Kennedy"
			String email = "bbbcustomer@gmail.com"
			String phoneNum = "8585858585"
			
			testObj.canScheduleAppointmentForSiteId(_) >> TRUE
			
			//getProfileData Private Method Coverage
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			//fetchPreSelectedServiceRef Public Method Coverage
			1 * appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getPreSelectedServiceRefQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["OTH"]) >> null
			
		when:
			ScheduleAppointmentVO results = testObj.getScheduleAppointment(storeId, appointmentType, registryId, coregFN, coregLN, eventDate, pageName)
		then:
			results.getPreSelectedServiceRef().equals(28)
			results.getSkedgeURL().equals(null)
			results.isScheduleappointment().equals(TRUE)
			results.isDirectskedgeMe().equals(FALSE)
			results.isErrormodal().equals(TRUE)
			checkScheduleAppointmentVO(results, firstName, lastName, email, phoneNum, storeId, coregFN, coregLN, registryId, eventDate, appointmentType, pageName)
			
	}
	
	def"getScheduleAppointment. This TC is when isApponitmentTypeValidForStore returns false"(){
		given:
			String storeId = "2345"
			String appointmentType = "type"
			String registryId = "23456545"
			String coregFN = "coregFN"
			String coregLN = "coregLN"
			String eventDate = "10/09/2017"
			String pageName = "pagename"
			String firstName = "john"
			String lastName = "Kennedy"
			String email = "bbbcustomer@gmail.com"
			String phoneNum = "8585858585"
			
			testObj.canScheduleAppointmentForSiteId(_) >> TRUE
			
			//getProfileData Private Method Coverage
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> firstName
			1 * profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> lastName
			1 * profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> email
			1 * profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> phoneNum
			1 * searchStoreManagerMock.fetchFavoriteStoreId(_, profileMock) >> "fac23456"
			
			//isApponitmentTypeValidForStore public Method Coverage
			1 * storeRepositoryMock.getView("store") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getIdAppointmentMappingQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["2345", "type"]) >> null
			
			//fetchPreSelectedServiceRef Public Method Coverage
			1 * appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getPreSelectedServiceRefQuery()) >> rqlStatementMock
			RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
			RepositoryItem[] repositoryItemList = [repositoryItem1]
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["type"]) >> repositoryItemList
			repositoryItem1.setProperties(["preselectedServiceRef":2])
			
		when:
			ScheduleAppointmentVO results = testObj.getScheduleAppointment(storeId, appointmentType, registryId, coregFN, coregLN, eventDate, pageName)
		then:
			results.getPreSelectedServiceRef().equals(2)
			results.getSkedgeURL().equals(null)
			results.isScheduleappointment().equals(TRUE)
			results.isDirectskedgeMe().equals(FALSE)
			results.isErrormodal().equals(TRUE)
			checkScheduleAppointmentVO(results, firstName, lastName, email, phoneNum, storeId, coregFN, coregLN, registryId, eventDate, appointmentType, pageName)
	}

	private checkScheduleAppointmentVO(ScheduleAppointmentVO results, String firstName, String lastName, String email, String phoneNum, String storeId, String coregFN, String coregLN, String registryId, String eventDate, String appointmentType, String pageName) {
		results.getFirstname().equals(firstName)
		results.getLastname().equals(lastName)
		results.getEmail().equals(email)
		results.getPhone().equals(phoneNum)
		results.getStoreId().equals(storeId)
		results.getCoregFN().equals(coregFN)
		results.getCoregLN().equals(coregLN)
		results.getRegistryId().equals(registryId)
		results.getEventDate().equals(eventDate)
		results.getAppointmentType().equals(appointmentType)
		results.getPageName().equals(pageName)
	}
	
	////////////////////////////////////////TestCases for getScheduleAppointment --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for isApponitmentTypeValidForStore --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public boolean isApponitmentTypeValidForStore(String pFavouriteStoreId,String pAppointmentType) ///////////
	
	def"isApponitmentTypeValidForStore. This TC is when repositoryItem returns more than one item"(){
		given:
			String favStoreId = "2345"
			String appointmentType = "type"
			1 * storeRepositoryMock.getView("store") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getIdAppointmentMappingQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["2345", "type"]) >> []
		when:
			def results = testObj.isApponitmentTypeValidForStore(favStoreId, appointmentType)
		then:
			results == false
		
	}
	
	def"isApponitmentTypeValidForStore. This TC is when RepositoryException thrown"(){
		given:
			String favStoreId = "2345"
			String appointmentType = "type"
			1 * storeRepositoryMock.getView("store") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getIdAppointmentMappingQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["2345", "type"]) >> {throw new RepositoryException("Mock for RepositoryException")}
		when:
			def results = testObj.isApponitmentTypeValidForStore(favStoreId, appointmentType)
		then:
			results == null
			1 * testObj.logError('catalog_1042: ScheduleAppointmentManager|checkAppointmentAvailability()|RepositoryException', _)
			thrown BBBSystemException
		
	}
	
	////////////////////////////////////////TestCases for isApponitmentTypeValidForStore --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for checkAppointmentAvailability --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public Map<String, Boolean> checkAppointmentAvailability(List<StoreDetails> pStoreDetails, String pAppointmentType) ///////////
	
	def"checkAppointmentAvailability. This TC is the Happy flow of checkAppointmentAvailability method"(){
		given:
			String appointmentType = "type"
			StoreDetails storeDetailsMock1 = Mock()
			storeDetailsMock.getStoreId() >> "2345"
			storeDetailsMock1.getStoreId() >> "3546"
			1 * testObj.checkAppointmentAvailable(storeDetailsMock.getStoreId(), appointmentType) >> ["2345":TRUE]
			1 * testObj.checkAppointmentAvailable(storeDetailsMock1.getStoreId(), appointmentType) >> ["3546":FALSE]
		when:
			Map<String, Boolean> results = testObj.checkAppointmentAvailability([storeDetailsMock,storeDetailsMock1], appointmentType)
			
		then:
			results == ["2345":TRUE,"3546":FALSE]
			
	}
	
	////////////////////////////////////////TestCases for checkAppointmentAvailability --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for checkAppointmentAvailabilityCanada --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public Map<String, Boolean> checkAppointmentAvailabilityCanada(List<StoreVO> pStoreVo, String pAppointmentType) ///////////
	
	def"checkAppointmentAvailabilityCanada. This TC is the Happy flow of checkAppointmentAvailabilityCanada method"(){
		given:
			String appointmentType = "type"
			StoreVO storeVOMock1 = Mock()
			storeVOMock.getStoreId() >> "2345"
			storeVOMock1.getStoreId() >> "3546"
			1 * testObj.checkAppointmentAvailable(storeVOMock.getStoreId(), appointmentType) >> ["2345":TRUE]
			1 * testObj.checkAppointmentAvailable(storeVOMock1.getStoreId(), appointmentType) >> ["3546":FALSE]
		when:
			Map<String, Boolean> results = testObj.checkAppointmentAvailabilityCanada([storeVOMock,storeVOMock1], appointmentType)
			
		then:
			results == ["2345":TRUE,"3546":FALSE]
			
	}
	
	////////////////////////////////////////TestCases for checkAppointmentAvailabilityCanada --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for checkAppointmentAvailable --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public Map<String, Boolean> checkAppointmentAvailable(String pStoreId, String pAppointmentType) ///////////
	
	def"checkAppointmentAvailable. This TC is the Happy flow of checkAppointmentAvailable method"(){
		given:
			String storeId = "2345"
			String appointmentType = "type"
			1 * storeRepositoryMock.getView("store") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getIdAppointmentMappingQuery()) >> rqlStatementMock
			rqlStatementMock.executeQuery(repositoryViewMock, ["2345", "type"]) >>  repositoryItemMock
			
		when:
			Map<String, Boolean> results = testObj.checkAppointmentAvailable(storeId, appointmentType)
			
		then:
			results == ["2345":TRUE]
			
	}
	
	def"checkAppointmentAvailable. This TC is the when repositoryItem return null"(){
		given:
			String storeId = "2345"
			String appointmentType = "type"
			1 * storeRepositoryMock.getView("store") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getIdAppointmentMappingQuery()) >> rqlStatementMock
			rqlStatementMock.executeQuery(repositoryViewMock, ["2345", "type"]) >> null
			
		when:
			Map<String, Boolean> results = testObj.checkAppointmentAvailable(storeId, appointmentType)
			
		then:
			results == ["2345":FALSE]
			
	}
	
	def"checkAppointmentAvailable. This TC is the when RepositoryException thrown"(){
		given:
			String storeId = "2345"
			String appointmentType = "type"
			1 * storeRepositoryMock.getView("store") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getIdAppointmentMappingQuery()) >> rqlStatementMock
			rqlStatementMock.executeQuery(repositoryViewMock, ["2345", "type"]) >> {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			Map<String, Boolean> results = testObj.checkAppointmentAvailable(storeId, appointmentType)
			
		then:
			results == null
			thrown BBBSystemException
			1 * testObj.logError('catalog_1042: ScheduleAppointmentManager|checkAppointmentAvailability()|RepositoryException', _)
			
	}
	
	////////////////////////////////////////TestCases for checkAppointmentAvailable --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for checkAppointmentEligible --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public void checkAppointmentEligible(StoreDetails storeDetails, Map<String, Boolean> appointmentMap, boolean appointmentEligible) ///////////
	
	def"checkAppointmentEligible. This TC is the Happy flow of checkAppointmentEligible"(){
		given:
			storeDetailsMock.getStoreId() >> "2345"
			
		when:
			testObj.checkAppointmentEligible(storeDetailsMock, ["2345":TRUE], FALSE)
			
		then:
			1 * storeDetailsMock.setAppointmentAvailable(true)
			1 * storeDetailsMock.setAppointmentEligible(false)
			1 * testObj.logDebug('SearchStoreService.searchStores : store ID 2345 appointmentAvailable is true')
			1 * testObj.logDebug('SearchStoreService.searchStores : site id is null store id 2345 appointmentEligible flag false')
			
	}
	
	def"checkAppointmentEligible. This TC is when appointmentAvailable is false"(){
		given:
			storeDetailsMock.getStoreId() >> "2345"
			
		when:
			testObj.checkAppointmentEligible(storeDetailsMock, ["2345":FALSE], TRUE)
			
		then:
			1 * storeDetailsMock.setAppointmentAvailable(false)
			1 * testObj.logDebug('SearchStoreService.searchStores : store ID 2345 appointmentAvailable is false')
			0 * storeDetailsMock.setAppointmentEligible(false)
			0 * testObj.logDebug('SearchStoreService.searchStores : site id is null store id 2345 appointmentEligible flag false')
			
	}
	
	////////////////////////////////////////TestCases for checkAppointmentEligible --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for checkAppointmentEligibleCanada --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public void checkAppointmentEligibleCanada(StoreVO storeDetails,	Map<String, Boolean> appointmentMap, boolean appointmentEligible) ///////////
	
	def"checkAppointmentEligibleCanada. This TC is the Happy flow of checkAppointmentEligible"(){
		given:
			storeVOMock.getStoreId() >> "2345"
			
		when:
			testObj.checkAppointmentEligibleCanada(storeVOMock, ["2345":TRUE], FALSE)
			
		then:
			1 * testObj.logDebug('SearchStoreService.searchStores : store ID 2345 appointmentAvailable is true')
			1 * testObj.logDebug('SearchStoreService.searchStores : site id is null store id 2345 appointmentEligible flag false')
			1 * storeVOMock.setAppointmentEligible(false)
			1 * storeVOMock.setAppointmentAvailable(true)
			
	}
	
	def"checkAppointmentEligibleCanada. This TC is when appointmentAvailable is false"(){
		given:
			storeVOMock.getStoreId() >> "2345"
			
		when:
			testObj.checkAppointmentEligibleCanada(storeVOMock, ["2345":FALSE], TRUE)
			
		then:
			1 * storeVOMock.setAppointmentAvailable(false)
			1 * testObj.logDebug('SearchStoreService.searchStores : store ID 2345 appointmentAvailable is false')
			
	}
	
	////////////////////////////////////////TestCases for checkAppointmentEligibleCanada --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for canScheduleAppointmentForSiteId --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public boolean canScheduleAppointmentForSiteId(String siteId) ///////////
	
	def"canScheduleAppointmentForSiteId. This TC is the Happy flow of canScheduleAppointmentForSiteId"(){
		given:
			String siteId = "BedBathUS"
			1 * bbbCatalogToolsImplMock.getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE) >> "true"
			
		when:
			def results = testObj.canScheduleAppointmentForSiteId(siteId)
			
		then:
			results == TRUE
			1 * testObj.logDebug('ScheduleAppointmentManager.canScheduleAppointmentForSiteId() method end')
			1 * testObj.logDebug('ScheduleAppointmentManager.canScheduleAppointmentForSiteId() method started')
			
	}
	
	////////////////////////////////////////TestCases for canScheduleAppointmentForSiteId --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for fetchPreSelectedServiceRef --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public int fetchPreSelectedServiceRef(String pAppointmentCode) ///////////
	
	def"fetchPreSelectedServiceRef. This TC is the when appointmentCode is null and repository returns empty and recursive call takesplace"(){
		given:
			String appointmentCode = null
			2 * appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			2 * testObj.rqlStatementQuery(testObj.getPreSelectedServiceRefQuery()) >> rqlStatementMock
			
			RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
			RepositoryItemMock repositoryItem2 = new RepositoryItemMock()
			RepositoryItem[] repositoryItemList = [repositoryItem1,repositoryItem2]
			
			1 * rqlStatementMock.executeQuery(repositoryViewMock, [null]) >> repositoryItemList
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["OTH"]) >> null
			
		when:
			int results = testObj.fetchPreSelectedServiceRef(appointmentCode)
			
		then:
			results == 28
			
	}
	
	def"fetchPreSelectedServiceRef. This TC is the when appointmentCode is not OTH"(){
		given:
			String appointmentCode = "type"
			appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			testObj.rqlStatementQuery(testObj.getPreSelectedServiceRefQuery()) >> rqlStatementMock
			rqlStatementMock.executeQuery(repositoryViewMock, ["type"]) >> []
			
		when:
			int results = testObj.fetchPreSelectedServiceRef(appointmentCode)
			
		then:
			results == 28
			
	}
	
	def"fetchPreSelectedServiceRef. This TC is when RepositoryException thrown"(){
		given:
			String appointmentCode = "type"
			1 * appointmentRepositoryMock.getView("appointmentType") >> repositoryViewMock
			1 * testObj.rqlStatementQuery(testObj.getPreSelectedServiceRefQuery()) >> rqlStatementMock
			1 * rqlStatementMock.executeQuery(repositoryViewMock, ["type"]) >> {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			int results = testObj.fetchPreSelectedServiceRef(appointmentCode)
			
		then:
			results == 0
			thrown BBBSystemException
			1 * testObj.logError('catalog_1042: ScheduleAppointmentManager|fetchPreSelectedServiceRef()|RepositoryException', _)
			1 * testObj.logDebug('ScheduleAppointmentManager.fetchPreSelectedServiceRef() method started : appointment code : type')
			
	}
	
	////////////////////////////////////////TestCases for fetchPreSelectedServiceRef --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for isStoreAppointmentValid --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public boolean isStoreAppointmentValid(String favouriteStoreId,String appointmentType) ///////////
	
	def"isStoreAppointmentValid. This TC is when appointmentType is passed as empty"(){
		given:
			String favouriteStoreId = "2345"
			String appointmentType = ""
			
		when:
			boolean results = testObj.isStoreAppointmentValid(favouriteStoreId, appointmentType)
			
		then:
			results == FALSE
			
	}
	
	def"isStoreAppointmentValid. This TC is when favouriteStoreId is passed as empty"(){
		given:
			String favouriteStoreId = ""
			String appointmentType = "type"
			
		when:
			boolean results = testObj.isStoreAppointmentValid(favouriteStoreId, appointmentType)
			
		then:
			results == FALSE
			
	}
	
	////////////////////////////////////////TestCases for isStoreAppointmentValid --> ENDS//////////////////////////////////////////////////////////
	
}
