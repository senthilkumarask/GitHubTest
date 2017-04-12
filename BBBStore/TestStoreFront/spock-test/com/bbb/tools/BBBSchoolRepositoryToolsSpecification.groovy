package com.bbb.tools

import java.util.List;

import atg.repository.MutableRepository;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement
import atg.servlet.DynamoHttpServletRequest;
import com.bbb.exception.BBBSystemException;

import com.bbb.exception.BBBBusinessException;

import atg.servlet.ServletUtil

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.selfservice.vo.SchoolVO;
import com.bbb.framework.cache.BBBObjectCache
import com.bbb.framework.performance.logger.PerformanceLogger;
import com.bbb.repository.RepositoryItemMock;
import com.bbb.selfservice.vo.BeddingShipAddrVO;

import spock.lang.Specification

class BBBSchoolRepositoryToolsSpecification extends Specification {
	
	def BBBSchoolRepositoryTools schoolRepositoryToolsTestObj
	def MutableRepository schoolRepositoryMock = Mock()
	def MutableRepository schoolVerRepositoryMock = Mock()
	def MutableRepository siteRepositoryMock = Mock()
	def BBBObjectCache objectCacheMock = Mock()
	def RepositoryView repositoryViewMock = Mock()
	def RqlStatement rqlStatementMock = Mock()
	def DynamoHttpServletRequest requestMock = Mock()
	def PerformanceLogger perfMock = Mock()
	def RepositoryItemMock repositoryItemMock = Mock()
	def RepositoryItemMock siteRepositoryItemMock = Mock()
	
	
	def setup() {
		
		schoolRepositoryToolsTestObj = new BBBSchoolRepositoryTools(schoolRepository:schoolRepositoryMock,schoolVerRepository:schoolVerRepositoryMock,
										objectCache:objectCacheMock)
		
		ServletUtil.setCurrentRequest(requestMock)
		requestMock.getContextPath() >> "/store"
		requestMock.resolveName("/com/bbb/framework/performance/logger/PerformanceLogger") >> perfMock
		perfMock.isEnableCustomComponentsMonitoring() >> false
	
		}
	
	/* getSchoolDetailsById - Test Case START */
	
	def "getSchoolDetailsById by passing schoolId" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
			
			schoolRepositoryMock.getItem("1411",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1411"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME) >> "National-Louis University - Wheeling" 
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_SCHOOL_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_SCHOOL_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_SCHOOL_PROPERTY_NAME) >> "Port St. Lucie"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_SCHOOL_PROPERTY_NAME) >> "22102"
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.HTML_SNIPPET) >> "htmlsnippet"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH) >> "/opt/css/path"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH) >> "/opt/js/path"
			
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolId() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(repositoryViewMock,["1411"]) >> [item1]
			Date creationDate = new Date()
			Date lastModifiedDate = new Date()
			
			item1.setProperties(["smallLogoURL" : "NationalLouisUniversity" , "collegeLogo" : "AcademyPark" , "collegeTag" : "Fifth" , "creationDate" : creationDate ,
				"imageURL" : "/opt/images/smallImages" , "largeLogoURL" : "/opt/images/largeImages" , "largeWelcomeMsg" : "HaiWelcome" , "lastModifiedDate" : lastModifiedDate, 
				"pdfURL" : "/opt/pdfurl" , "prefStoreId" : "storeId" , "smallWelcomeMsg" : "HaiWelcomeAgain" , "schoolPromoId" : repositoryItemMock , "promotion" : repositoryItemMock])
			
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("1411")
			
		then:
			results.schoolName == "National-Louis University - Wheeling"
			results.city == "Port St. Lucie"
			results.collegeTag == "Fifth"
			results.smallWelcomeMsg == "HaiWelcomeAgain"
		
	}
	
	def "getSchoolDetailsById when repositoryException occurs" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
			
			schoolRepositoryMock.getItem("1411",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1411"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME) >> "National-Louis University - Wheeling"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_SCHOOL_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_SCHOOL_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_SCHOOL_PROPERTY_NAME) >> "Port St. Lucie"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_SCHOOL_PROPERTY_NAME) >> "22102"
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.HTML_SNIPPET) >> "htmlsnippet"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH) >> "/opt/css/path"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH) >> "/opt/js/path"
			
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolId() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(repositoryViewMock,["1411"]) >> {throw new RepositoryException("Mock Repository Exception")}
			
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("1411")
			
		then:
			results == null
			thrown BBBSystemException
		
	}
	
	def "getSchoolDetailsById by passing when channel is MobileWeb" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			requestMock.getHeader(*_) >> "MobileWeb"
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
			
			schoolRepositoryMock.getItem("1411",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1411"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME) >> "National-Louis University - Wheeling"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_SCHOOL_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_SCHOOL_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_SCHOOL_PROPERTY_NAME) >> "Port St. Lucie"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_SCHOOL_PROPERTY_NAME) >> "22102"
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.HTML_SNIPPET) >> "htmlsnippet"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH) >> "/opt/css/path"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH) >> "/opt/js/path"
			
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolId() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(repositoryViewMock,["1411"]) >> null
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("1411")
			
		then:
			results.schoolName == "National-Louis University - Wheeling"
			results.city == "Port St. Lucie"
			results.collegeTag == null
			results.smallWelcomeMsg == null
		
	}
	
	def "getSchoolDetailsById by passing when channel is not MobileWeb" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			requestMock.getHeader(*_) >> "Web"
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
			
			schoolRepositoryMock.getItem("1411",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1411"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME) >> "National-Louis University - Wheeling"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_SCHOOL_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_SCHOOL_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_SCHOOL_PROPERTY_NAME) >> "Port St. Lucie"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_SCHOOL_PROPERTY_NAME) >> "22102"
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.HTML_SNIPPET) >> "htmlsnippet"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH) >> "/opt/css/path"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH) >> "/opt/js/path"
			
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolId() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
		
			rqlStatementMock.executeQuery(repositoryViewMock,["1411"]) >> null
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("1411")
			
		then:
			results == null
			thrown BBBBusinessException
			
		
	}
	
	def "getSchoolDetailsById by passing schoolId as null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById(null)
			
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getSchoolDetailsById by passing request as null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
		
			ServletUtil.setCurrentRequest(null)
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById(null)
			
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getSchoolDetailsById by passing schoolId as empty" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("")
			
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getSchoolDetailsById when schoolVerRepository returns null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
			
			schoolRepositoryMock.getItem("1411",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1411"
			
			
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolId() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(*_) >> null

		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("1411")
			
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getSchoolDetailsById when schoolVerRepository item properties returns null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
			
			schoolRepositoryMock.getItem("1411",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1411"
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.HTML_SNIPPET) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH) >> null
			
			
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolId() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(repositoryViewMock,["1411"]) >> [item1]
			
			
			item1.setProperties(["smallLogoURL" : "NationalLouisUniversity" , "collegeLogo" : "AcademyPark" , "collegeTag" : "Fifth" , "creationDate" : null ,
				"imageURL" : "/opt/images/smallImages" , "largeLogoURL" : "/opt/images/largeImages" , "largeWelcomeMsg" : "HaiWelcome" , "lastModifiedDate" : null,
				"pdfURL" : "/opt/pdfurl" , "prefStoreId" : "storeId" , "smallWelcomeMsg" : "HaiWelcomeAgain" , "schoolPromoId" : repositoryItemMock , "promotion" : repositoryItemMock])
			
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("1411")
			
		then:
			results.smallLogoURL == "NationalLouisUniversity"
		
	}
	
	def "getSchoolDetailsById when schoolRepository returns null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			
			schoolRepositoryMock.getItem("1411",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1411"
			
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("1411")
			
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getSchoolDetailsById when schoolPromoSet item returns null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
			
			schoolRepositoryMock.getItem("1411",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1411"
			
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolId() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(repositoryViewMock,["1411"]) >> [item1]
			
			
			item1.setProperties(["smallLogoURL" : "NationalLouisUniversity" , "collegeLogo" : "AcademyPark" , "collegeTag" : "Fifth" , "creationDate" : null ,
				"imageURL" : "/opt/images/smallImages" , "largeLogoURL" : "/opt/images/largeImages" , "largeWelcomeMsg" : "HaiWelcome" , "lastModifiedDate" : null,
				"pdfURL" : "/opt/pdfurl" , "prefStoreId" : "storeId" , "smallWelcomeMsg" : "HaiWelcomeAgain" , "schoolPromoId" : null , "promotion" : repositoryItemMock])
			
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsById("1411")
			
		then:
			results.smallLogoURL == "NationalLouisUniversity"
		
	}
	
	/* getSchoolDetailsById - Test Cases ENDS*/ 
	
	/* getSchoolDetailsByName - Test Cases START */
	
	def "getSchoolDetailsByName by passing school seoName" () {
		
		given:
		
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
				
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolName() >> rqlStatementMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1826"
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME) >> "National-Louis University - Wheeling"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_SCHOOL_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_SCHOOL_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_SCHOOL_PROPERTY_NAME) >> "Port St. Lucie"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_SCHOOL_PROPERTY_NAME) >> "22102"
	 		
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["Florida International University"]) >> [item1]
			
			Date creationDate = new Date()
			Date lastModifiedDate = new Date()
			
			item1.setProperties(["schools" : repositoryItemMock , "smallLogoURL" : "NationalLouisUniversity" , "collegeLogo" : "AcademyPark" , "collegeTag" : "Fifth" , "creationDate" : creationDate ,
				"imageURL" : "/opt/images/smallImages" , "largeLogoURL" : "/opt/images/largeImages" , "largeWelcomeMsg" : "HaiWelcome" , "lastModifiedDate" : lastModifiedDate, 
				"pdfURL" : "/opt/pdfurl" , "prefStoreId" : "storeId" , "smallWelcomeMsg" : "HaiWelcomeAgain" , "schoolSeoName" : "Florida International University" , "promotion" : repositoryItemMock])
			
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsByName("Florida International University")
		then:
			results.smallLogoURL == "NationalLouisUniversity"
			results.addrLine1 == "1111 Academy Park Loop"
			results.schoolSeoName == "Florida International University"
	}
	
	def "getSchoolDetailsByName when Repository Exception Occurs" () {
		
		given:
		
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
				
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolName() >> rqlStatementMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1826"
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME) >> "National-Louis University - Wheeling"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_SCHOOL_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_SCHOOL_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_SCHOOL_PROPERTY_NAME) >> "Port St. Lucie"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_SCHOOL_PROPERTY_NAME) >> "22102"
			 
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["Florida International University"]) >> {throw new RepositoryException("Mock Repository Exception")} 

		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsByName("Florida International University")
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "getSchoolDetailsByName when schoolVerRepository item property returns null" () {
		
		given:
		
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolRepository() >> schoolRepositoryMock
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.getSchoolVerRepository() >> schoolVerRepositoryMock
				
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolName() >> rqlStatementMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1826"
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME) >> "National-Louis University - Wheeling"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_SCHOOL_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_SCHOOL_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_SCHOOL_PROPERTY_NAME) >> "Port St. Lucie"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_SCHOOL_PROPERTY_NAME) >> "22102"
			 
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["Florida International University"]) >> [item1]
			
			item1.setProperties(["schools" : repositoryItemMock , "smallLogoURL" : "NationalLouisUniversity" , "collegeLogo" : "AcademyPark" , "collegeTag" : "Fifth" , "creationDate" : null ,
				"imageURL" : "/opt/images/smallImages" , "largeLogoURL" : "/opt/images/largeImages" , "largeWelcomeMsg" : "HaiWelcome" , "lastModifiedDate" : null,
				"pdfURL" : "/opt/pdfurl" , "prefStoreId" : "storeId" , "smallWelcomeMsg" : "HaiWelcomeAgain" , "schoolSeoName" : "Florida International University" , "promotion" : repositoryItemMock])
			
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsByName("Florida International University")
		then:
			results.smallLogoURL == "NationalLouisUniversity"
			results.addrLine1 == "1111 Academy Park Loop"
			results.schoolSeoName == "Florida International University"
	}
	
	def "getSchoolDetailsByName by passing school seoName as blank" () {
		
		given:
		
			schoolRepositoryToolsTestObj = Spy()
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsByName("")
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getSchoolDetailsByName when schoolVerRepository returns null" () {
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			
			
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolName() >> rqlStatementMock
	 		
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["Florida International University"]) >> null
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsByName("Florida International University")
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getSchoolDetailsByName when schoolRepository returns null" () {
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
				
			schoolVerRepositoryMock.getView(BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR) >> repositoryViewMock
			schoolRepositoryToolsTestObj.getRqlForSchoolName() >> rqlStatementMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "1826"
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> null
	 		
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["Florida International University"]) >> [item1]
			
			item1.setProperties(["schools" : repositoryItemMock])
		
		when:
			def SchoolVO results = schoolRepositoryToolsTestObj.getSchoolDetailsByName("Florida International University")
		then:
			results == null
			thrown BBBBusinessException
	}
	
	/* getSchoolDetailsByName - Test Cases ENDS */
	
	/* getBeddingShipAddress - Test Cases START */
	
	def "getBeddingShipAddress by passing schoolId" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.setSiteRepository(siteRepositoryMock)
			
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLEGE_NAME) >> "Florida International University"
			
			schoolVerRepositoryMock.getItem("18261") >>  repositoryItemMock
			schoolRepositoryToolsTestObj.getCurrentSiteId() >> "BedBathUS"
			siteRepositoryMock.getItem("BedBathUS", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_BEDDING_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_BEDDING_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_BEDDING_PROPERTY_NAME) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_BEDDING_PROPERTY_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_BEDDING_PROPERTY_NAME) >> "22102"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COMPANY_NAME) >> "Florida"
			Date shippingStartDate = new Date()
			Date shippingEndDate = new Date()
			Date shippingEndDateFromSite = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_START_DATE) >> shippingStartDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_END_DATE) >> shippingEndDate
			siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> shippingEndDateFromSite
			
		
		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("1826")
			
		then:
			
			results.addrLine1 == "1111 Academy Park Loop"
			results.companyName == "Florida"
	}
	
	def "getBeddingShipAddress when RepositoryException Occurs" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}

		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("1826")
			
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "getBeddingShipAddress when item properties gives null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.setSiteRepository(siteRepositoryMock)
			
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLEGE_NAME) >> null
			
			schoolVerRepositoryMock.getItem("18261") >>  repositoryItemMock
			schoolRepositoryToolsTestObj.getCurrentSiteId() >> "BedBathUS"
			siteRepositoryMock.getItem("BedBathUS", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_BEDDING_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_BEDDING_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_BEDDING_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_BEDDING_PROPERTY_NAME) >> null
			repositoryItemMock.getRepositoryId() >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_BEDDING_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COMPANY_NAME) >> null
			Date shippingStartDate = new Date()
			Date shippingEndDate = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_START_DATE) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_END_DATE) >> null
			siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> null
			
		
		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("1826")
			
		then:
			
			results != null
	}
	
	def "getBeddingShipAddress when shippingDateFromCollege is after current date" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.setSiteRepository(siteRepositoryMock)
			
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLEGE_NAME) >> "Florida International University"
			
			schoolVerRepositoryMock.getItem("18261") >>  repositoryItemMock
			schoolRepositoryToolsTestObj.getCurrentSiteId() >> "BedBathUS"
			siteRepositoryMock.getItem("BedBathUS", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_BEDDING_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_BEDDING_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_BEDDING_PROPERTY_NAME) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_BEDDING_PROPERTY_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_BEDDING_PROPERTY_NAME) >> "22102"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COMPANY_NAME) >> "Florida"
			def shippingStartDate = new GregorianCalendar(2116, Calendar.AUGUST, 3, 1, 23, 45).time
			Date shippingEndDate = new Date()
			def shippingEndDateFromSite = new GregorianCalendar(2015, Calendar.AUGUST, 3, 1, 23, 45).time
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_START_DATE) >> shippingStartDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_END_DATE) >> shippingEndDate
			siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> shippingEndDateFromSite
			
		
		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("1826")
			
		then:
			
			results.addrLine1 == "1111 Academy Park Loop"
			results.companyName == "Florida"
	}
	
	def "getBeddingShipAddress when shippingEndDateFromSite is null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.setSiteRepository(siteRepositoryMock)
			
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLEGE_NAME) >> "Florida International University"
			
			schoolVerRepositoryMock.getItem("18261") >>  repositoryItemMock
			schoolRepositoryToolsTestObj.getCurrentSiteId() >> "BedBathUS"
			siteRepositoryMock.getItem("BedBathUS", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_BEDDING_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_BEDDING_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_BEDDING_PROPERTY_NAME) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_BEDDING_PROPERTY_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_BEDDING_PROPERTY_NAME) >> "22102"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COMPANY_NAME) >> "Florida"
			def shippingStartDate = new GregorianCalendar(2116, Calendar.AUGUST, 3, 1, 23, 45).time
			Date shippingEndDate = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_START_DATE) >> shippingStartDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_END_DATE) >> shippingEndDate
			siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> null
			
		
		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("1826")
			
		then:
			
			results.addrLine1 == "1111 Academy Park Loop"
			results.companyName == "Florida"
	}
	
	def "getBeddingShipAddress when shippingEndDateFromCollege is null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.setSiteRepository(siteRepositoryMock)
			
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLEGE_NAME) >> "Florida International University"
			
			schoolVerRepositoryMock.getItem("18261") >>  repositoryItemMock
			schoolRepositoryToolsTestObj.getCurrentSiteId() >> "BedBathUS"
			siteRepositoryMock.getItem("BedBathUS", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_BEDDING_PROPERTY_NAME) >> "1111 Academy Park Loop"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_BEDDING_PROPERTY_NAME) >> "Fifth Avenue South"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CITY_BEDDING_PROPERTY_NAME) >> "AL"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_BEDDING_PROPERTY_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "18261"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ZIP_BEDDING_PROPERTY_NAME) >> "22102"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.COMPANY_NAME) >> "Florida"
			def shippingStartDate = new GregorianCalendar(2116, Calendar.AUGUST, 3, 1, 23, 45).time
			def shippingEndDateFromSite = new GregorianCalendar(2015, Calendar.AUGUST, 3, 1, 23, 45).time
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_START_DATE) >> shippingStartDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_END_DATE) >> null
			siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> shippingEndDateFromSite 
			
		
		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("1826")
			
		then:
			
			results.addrLine1 == "1111 Academy Park Loop"
			results.companyName == "Florida"
	}
	
	def "getBeddingShipAddress by passing schoolId as empty" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
		
		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("")
			
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getBeddingShipAddress when schoolRepository returns null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> "18261"
		
		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("1826")
			
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getBeddingShipAddress when schoolVerRepository returns null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
			schoolRepositoryToolsTestObj.setSiteRepository(siteRepositoryMock)
		
			schoolRepositoryMock.getItem("1826",BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ID) >> null
			schoolVerRepositoryMock.getItem("18261") >>  null
			schoolRepositoryToolsTestObj.getCurrentSiteId() >> "BedBathUS"
			siteRepositoryMock.getItem("BedBathUS", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
		
		when:
			def BeddingShipAddrVO results = schoolRepositoryToolsTestObj.getBeddingShipAddress("1826")
			
		then:
			results == null
			thrown BBBBusinessException
	}
	
	/* getBeddingShipAddress - Test Cases ENDS */
	
	/* getCollegesByState - Test Cases START */
	
	def "getCollegesByState when passing statecode" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setObjectCache(objectCacheMock)
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
		
			List<String> cacheName = new ArrayList<String>()
			cacheName.add("123321")
			
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			
			List<CollegeVO> objectCache = new ArrayList<CollegeVO>()
			objectCache.add("someOtherValue")
			
			objectCacheMock.get(*_) >> null
			
			schoolRepositoryToolsTestObj.getRqlCollegesByState() >> rqlStatementMock
			schoolRepositoryMock.getView("schools") >> repositoryViewMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(repositoryViewMock,["true", "NJ"]) >> [item1]
			item1.setRepositoryId("3456")
			item1.setProperties(["schoolName" : "Florida"])
		
		when:
			def List<CollegeVO> results = schoolRepositoryToolsTestObj.getCollegesByState("NJ")
			
		then:
			results.size > 0
			
	}
	
	def "getCollegesByState when RepositoryException Occurs" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setObjectCache(objectCacheMock)
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
		
			List<String> cacheName = new ArrayList<String>()
			cacheName.add("123321")
			
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			
			List<CollegeVO> objectCache = new ArrayList<CollegeVO>()
			objectCache.add("someOtherValue")
			
			objectCacheMock.get(*_) >> null
			
			schoolRepositoryToolsTestObj.getRqlCollegesByState() >> rqlStatementMock
			schoolRepositoryMock.getView("schools") >> repositoryViewMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(repositoryViewMock,["true", "NJ"]) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def List<CollegeVO> results = schoolRepositoryToolsTestObj.getCollegesByState("NJ")
			
		then:
			results == null
			thrown BBBSystemException
			
	}
	
	def "getCollegesByState when passing statecode as empty" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setObjectCache(objectCacheMock)
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
		
		when:
			def List<CollegeVO> results = schoolRepositoryToolsTestObj.getCollegesByState("")
			
		then:
			results == null
			thrown BBBSystemException
			
	}
	
	def "getCollegesByState when repository item returns null" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setObjectCache(null)
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
		
			List<String> cacheName = new ArrayList<String>()
			cacheName.add("somevalue")
			
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			
			schoolRepositoryToolsTestObj.getRqlCollegesByState() >> rqlStatementMock
			schoolRepositoryMock.getView("schools") >> repositoryViewMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(*_) >> null
		
		when:
			def List<CollegeVO> results = schoolRepositoryToolsTestObj.getCollegesByState("NJ")
			
		then:
			results == null
			thrown NullPointerException
			
	}
	
	def "getCollegesByState when repository item returns empty" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setObjectCache(null)
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
		
			List<String> cacheName = new ArrayList<String>()
			cacheName.add("123321")
			
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			
			schoolRepositoryToolsTestObj.getRqlCollegesByState() >> rqlStatementMock
			schoolRepositoryMock.getView("schools") >> repositoryViewMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(*_) >> []
		
		when:
			def List<CollegeVO> results = schoolRepositoryToolsTestObj.getCollegesByState("NJ")
			
		then:
			results == null
			thrown NullPointerException
			
	}
	
	def "getCollegesByState when passing statecode with ObjectCache" (){
		given:
			schoolRepositoryToolsTestObj = Spy()
			
			schoolRepositoryToolsTestObj.setObjectCache(objectCacheMock)
			schoolRepositoryToolsTestObj.setSchoolRepository(schoolRepositoryMock)
			schoolRepositoryToolsTestObj.setSchoolVerRepository(schoolVerRepositoryMock)
		
			List<String> cacheName = new ArrayList<String>()
			cacheName.add("123321")
			
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			schoolRepositoryToolsTestObj.getAllValuesForKey(*_) >> cacheName
			
			List<CollegeVO> objectCache = new ArrayList<CollegeVO>()
			objectCache.add("someOtherValue")
			
			objectCacheMock.get(*_) >> objectCache
		
		when:
			def List<CollegeVO> results = schoolRepositoryToolsTestObj.getCollegesByState("NJ")
			
		then:
			results != null
			
	}
	
	/* getCollegesByState - Test Cases ENDS */
	
}
