package com.bbb.cms.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CategoryVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import spock.lang.specification.BBBExtendedSpec;

class CollegeCategoryDropletSpecification extends BBBExtendedSpec {
	
	def BBBCatalogTools catalogToolMock = Mock()
	def CollegeCategoryDroplet testObj

	def setup(){
		testObj = Spy()
		testObj.setCatalogTools(catalogToolMock)
	}
	
	def "when root categoryId is not null for site and CategoryDetail is not null"(){
		given:
				String siteId="BedBathUs"
				String rootCategoryId="rootCatId"
				CategoryVO collegeCategory= new CategoryVO()
				CategoryVO sortedCollegeCategory=new CategoryVO()
				1* testObj.getCurrentSiteId() >> siteId
				1* catalogToolMock.getRootCollegeIdFrmConfig(siteId) >> rootCategoryId
				1* catalogToolMock.getCategoryDetail(siteId,rootCategoryId,false) >> collegeCategory
				1* catalogToolMock.getSortedCollegeCategory(collegeCategory) >> sortedCollegeCategory
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("starting method CollegeCategoryDroplet")
			1* testObj.logDebug("CollegeCategory will be created : ")
			1* requestMock.setParameter("collegeCategories", sortedCollegeCategory)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* testObj.logDebug("Existing method CollegeCategoryDroplet")
		
	}
	
	def "when root categoryId is not null for site and CategoryDetail is null"(){
		given:
				String siteId="BedBathUs"
				String rootCategoryId="rootCatId"
				CategoryVO collegeCategory= null
				CategoryVO sortedCollegeCategory=new CategoryVO()
				1* testObj.getCurrentSiteId() >> siteId
				1* catalogToolMock.getRootCollegeIdFrmConfig(siteId) >> rootCategoryId
				1* catalogToolMock.getCategoryDetail(siteId,rootCategoryId,false) >> collegeCategory
				0* catalogToolMock.getSortedCollegeCategory(collegeCategory) >> sortedCollegeCategory
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("starting method CollegeCategoryDroplet")
			1* testObj.logDebug("CollegeCategory will be created : ")
			1* requestMock.setParameter("collegeCategories", null)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* testObj.logDebug("Existing method CollegeCategoryDroplet")
		
	}
	
	def "handle BBBBusinessException"(){
		given:
				String siteId="BedBathUs"
				String rootCategoryId="rootCatId"
				
				1* testObj.getCurrentSiteId() >> siteId
				1* catalogToolMock.getRootCollegeIdFrmConfig(siteId) >> {throw new BBBBusinessException("BBBBusinessException")}
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("starting method CollegeCategoryDroplet")
			0* testObj.logDebug("CollegeCategory will be created : ")
			0* requestMock.setParameter("collegeCategories", _)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "CollegeCategorydroplet|service|BBBBusinessException","catalog_1030"),_)
			1* requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
			1* testObj.logDebug("Existing method CollegeCategoryDroplet")
		
	}
	
	
	def "handle BBBSystemException"(){
		given:
				String siteId="BedBathUs"
				String rootCategoryId="rootCatId"
				
				1* testObj.getCurrentSiteId() >> siteId
				1* catalogToolMock.getRootCollegeIdFrmConfig(siteId) >> {throw new BBBSystemException("BBBSystemException")}
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("starting method CollegeCategoryDroplet")
			0* testObj.logDebug("CollegeCategory will be created : ")
			0* requestMock.setParameter("collegeCategories", _)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "CollegeCategorydroplet|service|BBBSystemException","catalog_1031"),_)
			1* requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
			1* testObj.logDebug("Existing method CollegeCategoryDroplet")
		
	}
	
}
