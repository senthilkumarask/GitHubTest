package com.bbb.commerce.browse.droplet

import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.nucleus.naming.ParameterName;
import atg.servlet.ServletUtil;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CollegeVO
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException
import com.bbb.rest.catalog.vo.CollegeMerchandizeVO
import com.bbb.search.integration.SearchManager

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class CollegeSearchDropletSpecification extends BBBExtendedSpec {
	
	CollegeSearchDroplet testObj
	SearchManager searchManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	
	public final static ParameterName PARAMETER_STATE_ID = ParameterName.getParameterName("stateId")
	
	def setup(){
		testObj = new CollegeSearchDroplet(mSearchManager:searchManagerMock,mCatalogTools:catalogToolsMock)
	}
	
	def"service method.This TC is the Happy flow of service method"(){
		given:
			1 * requestMock.getParameter(PARAMETER_STATE_ID) >> "NJ"
			CollegeVO collegeVOMock = new CollegeVO(mCollegeName:"Princeton")
			CollegeVO collegeVOMock1 = new CollegeVO(mCollegeName:"Pale")
			1 * searchManagerMock.getAllColleges("NJ") >> [collegeVOMock,collegeVOMock1]
			
			//getCollegeMaxCount Public Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "collegeMaxCount") >> ["1"]
			
			//getCollegeGroupList Public Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "CollegeGroupList") >> ["Harvard,Princeton"]
			
			//getListCollegeNamePatern Public Method Coverage
			5 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "listCollgeNamePattern") >> ["ton1,ton"]
			
			TreeSet<String> setCollegeName = ['Harvard','Princeton']
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter('CollegeNameGrp', setCollegeName)
			1 * requestMock.setParameter("collegesCount", 2)
			1 * requestMock.setParameter("collegesMap", _)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"getCollegeGroups.This TC is the Happy flow of getCollegeGroups"(){
		given:
			1 * requestMock.resolveName("/com/bbb/commerce/browse/droplet/CollegeLookup") >> testObj
			StateVO stateVOMock = new StateVO(stateCode:"AS")
			StateVO stateVOMock1 = new StateVO(stateCode:"NJ",stateName:"New Jercy")
			1 * catalogToolsMock.getStateList() >> [stateVOMock,stateVOMock1]
			1 * searchManagerMock.getCatalogId(BBBSearchBrowseConstants.SCHOOL_STATE, "New Jercy") >> "12"
			CollegeVO collegeVOMock = new CollegeVO(mCollegeName:"Princeton",mCollegeId:"c12345")
			CollegeVO collegeVOMock1 = new CollegeVO(mCollegeName:"Pale",mCollegeId:"c78945")
			1 * requestMock.getObjectParameter("collegesMap") >> ["N":[collegeVOMock],"P":[collegeVOMock1]]
			TreeSet<String> setCollegeName = ['Harvard','Princeton']
			1 * requestMock.getObjectParameter("CollegeNameGrp") >> setCollegeName
			
			//service Method Coverage
			1 * searchManagerMock.getAllColleges(_) >> null
			
		when:
			CollegeMerchandizeVO results = testObj.getCollegeGroups("NJ")
			
		then:
			results != null
			results.getCollegeBucket() == setCollegeName
			results.getAlphabetCollegeListMap().size() == 2
			1 * requestMock.setParameter("stateId","12")
			1 * requestMock.setParameter("noResult", "No Result Found")
			1 * requestMock.serviceLocalParameter("empty", requestMock, responseMock)
	}
	
	def"getCollegeGroups.This TC is when pStateCode parameter passed as empty"(){
		given:
			1 * requestMock.resolveName("/com/bbb/commerce/browse/droplet/CollegeLookup") >> testObj
			CollegeVO collegeVOMock = new CollegeVO(mCollegeName:"Princeton",mCollegeId:"c12345")
			CollegeVO collegeVOMock1 = new CollegeVO(mCollegeName:"Pale",mCollegeId:"c78945")
			1 * requestMock.getObjectParameter("collegesMap") >> ["N":[collegeVOMock],"P":[collegeVOMock1]]
			TreeSet<String> setCollegeName = ['Harvard','Princeton']
			1 * requestMock.getObjectParameter("CollegeNameGrp") >> setCollegeName
			
			//service Method Coverage
			1 * searchManagerMock.getAllColleges(_) >> [collegeVOMock,collegeVOMock1]
			
			//getCollegeMaxCount Public Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "collegeMaxCount") >> ["3"]
			
			//getListCollegeNamePatern Public Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "listCollgeNamePattern") >> ["Prince,Pale"]
			
		when:
			CollegeMerchandizeVO results = testObj.getCollegeGroups("")
			
		then:
			results != null
			1 * requestMock.setParameter("stateId","0")
			1 * requestMock.setParameter("collegesCount", 2)
			1 * requestMock.setParameter("collegesMap", _)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			results.getCollegeBucket() == setCollegeName
			results.getAlphabetCollegeListMap().size() == 2
	}
	
	def"getCollegeGroups.This TC is when BBBBusinessException thrown"(){
		given:
			1 * requestMock.resolveName("/com/bbb/commerce/browse/droplet/CollegeLookup") >> testObj
			StateVO stateVOMock = new StateVO(stateCode:"NJ",stateName:null)
			1 * catalogToolsMock.getStateList() >> [stateVOMock]
			
		when:
			CollegeMerchandizeVO results = testObj.getCollegeGroups("NJ")
			
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def"getCollegeGroups.This TC is when alphabetCollegeListMap is null and BBBBusinessException thrown in service method"(){
		given:
			1 * requestMock.resolveName("/com/bbb/commerce/browse/droplet/CollegeLookup") >> testObj
			StateVO stateVOMock = new StateVO(stateCode:"AS")
			StateVO stateVOMock1 = new StateVO(stateCode:"NJ",stateName:"New Jercy")
			1 * catalogToolsMock.getStateList() >> [stateVOMock,stateVOMock1]
			1 * searchManagerMock.getCatalogId(BBBSearchBrowseConstants.SCHOOL_STATE, "New Jercy") >> "12"
			CollegeVO collegeVOMock = new CollegeVO(mCollegeName:"Princeton",mCollegeId:"c12345")
			CollegeVO collegeVOMock1 = new CollegeVO(mCollegeName:"Pale",mCollegeId:"c78945")
			1 * requestMock.getObjectParameter("collegesMap") >> null
			TreeSet<String> setCollegeName = ['Harvard','Princeton']
			1 * requestMock.getObjectParameter("CollegeNameGrp") >> setCollegeName
			
			//service Method Coverage
			1 * searchManagerMock.getAllColleges(_) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			CollegeMerchandizeVO results = testObj.getCollegeGroups("NJ")
			
		then:
			results != null
			results.getCollegeBucket() == setCollegeName
			results.getAlphabetCollegeListMap() == [:]
	}
	
	def"getCollegeGroups.This TC is when alphabetCollegeListMap is null and BBBSystemException thrown in service method"(){
		given:
			1 * requestMock.resolveName("/com/bbb/commerce/browse/droplet/CollegeLookup") >> testObj
			StateVO stateVOMock = new StateVO(stateCode:"AS")
			StateVO stateVOMock1 = new StateVO(stateCode:"NJ",stateName:"New Jercy")
			1 * catalogToolsMock.getStateList() >> [stateVOMock,stateVOMock1]
			1 * searchManagerMock.getCatalogId(BBBSearchBrowseConstants.SCHOOL_STATE, "New Jercy") >> "12"
			CollegeVO collegeVOMock = new CollegeVO(mCollegeName:"Princeton",mCollegeId:"c12345")
			CollegeVO collegeVOMock1 = new CollegeVO(mCollegeName:"Pale",mCollegeId:"c78945")
			1 * requestMock.getObjectParameter("collegesMap") >> [:]
			TreeSet<String> setCollegeName = ['Harvard','Princeton']
			1 * requestMock.getObjectParameter("CollegeNameGrp") >> setCollegeName
			
			//service Method Coverage
			1 * searchManagerMock.getAllColleges(_) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			CollegeMerchandizeVO results = testObj.getCollegeGroups("NJ")
			
		then:
			results != null
			results.getCollegeBucket() == setCollegeName
			results.getAlphabetCollegeListMap() == [:]
	}
	
	def"service method.This TC is when prefix is empty in prefixGroup private method"(){
		given:
			1 * requestMock.getParameter(PARAMETER_STATE_ID) >> "NJ"
			CollegeVO collegeVOMock = new CollegeVO(mCollegeName:"Princeton George")
			CollegeVO collegeVOMock1 = new CollegeVO(mCollegeName:"Pale")
			1 * searchManagerMock.getAllColleges("NJ") >> [collegeVOMock,collegeVOMock1]
			
			//getCollegeMaxCount Public Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "collegeMaxCount") >> ["1"]
			
			//getCollegeGroupList Public Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "CollegeGroupList") >> ["Harvard,Princeton"]
			
			//getListCollegeNamePatern Public Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "listCollgeNamePattern") >> ["Princeton,ton"]
			
			TreeSet<String> setCollegeName = ['Harvard']
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter('CollegeNameGrp', setCollegeName)
			1 * requestMock.setParameter("collegesCount", 2)
			1 * requestMock.setParameter("collegesMap", _)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	
	def"getCollegeMaxCount. This TC is when collegeMaxList is null"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "collegeMaxCount") >> []
			
		when:
			testObj.getCollegeMaxCount()
		then:
			thrown NumberFormatException
	}
	
	def"getCollegeMaxCount. This TC is when BBBBusinessException thrown"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "collegeMaxCount") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.getCollegeMaxCount()
		then:
			thrown NumberFormatException
	}
	
	def"getCollegeMaxCount. This TC is when BBBSystemException thrown"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "collegeMaxCount") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.getCollegeMaxCount()
		then:
			thrown NumberFormatException
	}
	
	def"getListCollegeNamePatern. This TC is when listCollegeNamePatern is empty"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "listCollgeNamePattern") >> []
		when:
			List<String> results = testObj.getListCollegeNamePatern()
		then:
			results == []
	}
	
	def"getListCollegeNamePatern. This TC is when BBBBusinessException thrown"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "listCollgeNamePattern") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		when:
			List<String> results = testObj.getListCollegeNamePatern()
		then:
			results == []
	}
	
	def"getListCollegeNamePatern. This TC is when BBBSystemException thrown"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "listCollgeNamePattern") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		when:
			List<String> results = testObj.getListCollegeNamePatern()
		then:
			results == []
	}
	
	def"getCollegeGroupList. This TC is when collegeGroupList is empty"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "CollegeGroupList") >> []
		when:
			List<String> results = testObj.getCollegeGroupList()
		then:
			results == null
	}
	
	def"getCollegeGroupList. This TC is when BBBBusinessException thrown"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "CollegeGroupList") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		when:
			List<String> results = testObj.getCollegeGroupList()
		then:
			results == null
	}
	
	def"getCollegeGroupList. This TC is when BBBSystemException thrown"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "CollegeGroupList") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		when:
			List<String> results = testObj.getCollegeGroupList()
		then:
			results == null
	}


}
