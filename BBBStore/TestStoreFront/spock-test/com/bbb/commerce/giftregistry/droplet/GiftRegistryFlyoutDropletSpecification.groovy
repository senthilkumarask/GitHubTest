package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean;

import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.userprofiling.Profile
import spock.lang.specification.BBBExtendedSpec

class GiftRegistryFlyoutDropletSpecification extends BBBExtendedSpec {

	GiftRegistryFlyoutDroplet grfDroplet
	Profile profileMock = Mock()
	BBBSessionBean sBean = Mock()
	GiftRegistryManager grManager = Mock()
	RegistrySummaryVO rSummaryVO = new RegistrySummaryVO()
	RegistrySummaryVO rSummaryVO1 = new RegistrySummaryVO()
	RepositoryItem userRegistriesRepItem = Mock()
	RepositoryItem userRegistriesRepItem1 = Mock()
	RepositoryItem userRegistriesRepItem2 = Mock()
	
	GiftRegSessionBean grSesBean = Mock()

	
	def setup(){
		grfDroplet = new GiftRegistryFlyoutDroplet(giftRegistryManager : grManager,giftRegSessionBean : grSesBean, profile :profileMock)
	}
	
	def "service.TC when  userActiveRegList  is empty  "(){
		given:
		    grfDroplet = Spy()
			grfDroplet.setGiftRegistryManager(grManager)
			grfDroplet.setGiftRegSessionBean(grSesBean)
			grfDroplet.setProfile(profileMock)
			
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			def List<String> urList = ["ur1"]
			def List<String> urAList = []
			
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			map.put("userActiveRegistriesList", null)
			map.put("userRegistriesList",  urList)
			map.put("userRegistrysummaryVO", null)
			sBean.getValues() >> map
			1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus"
			
			
			RepositoryItem[] userRegistriesRepItems = [userRegistriesRepItem]
			profileMock.getRepositoryId() >> "p1"
			1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
			userRegistriesRepItem.getRepositoryId() >> "item1"
			1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
			1*grManager.getRegistryInfo( "item1", _) >> rSummaryVO
			
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( "22122014") 
			1*grfDroplet.getDateDiff(_, rSummaryVO) >> -100
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "2")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when  userActiveRegList  is empty and registration summery VO is not null gets from value map  "(){
		given:
			
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			def List<String> urList = ["ur1"]
			def List<String> urAList = []
			
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			map.put("userActiveRegistriesList", urAList)
			map.put("userRegistriesList",  urList)
			map.put("userRegistrysummaryVO", rSummaryVO)
			sBean.getValues() >> map
			1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus"
			
			
			RepositoryItem[] userRegistriesRepItems = [userRegistriesRepItem]
			profileMock.getRepositoryId() >> "p1"
			1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
			userRegistriesRepItem.getRepositoryId() >> "item1"
			1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
			//1*grManager.getRegistryInfo( "item1", _) >> rSummaryVO
			
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( null)
			//1*grfDroplet.getDateDiff(_, rSummaryVO) >> -100
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "3")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when  userActiveRegList  is empty and registration summery VO is  null   "(){
		given:
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			def List<String> urList = ["ur1"]
			def List<String> urAList = []
			
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			map.put("userActiveRegistriesList", urAList)
			map.put("userRegistriesList",  urList)
			map.put("userRegistrysummaryVO", null)
			sBean.getValues() >> map
			1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus"
			
			
			RepositoryItem[] userRegistriesRepItems = [userRegistriesRepItem]
			profileMock.getRepositoryId() >> "p1"
			1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
			userRegistriesRepItem.getRepositoryId() >> "item1"
			1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
			1*grManager.getRegistryInfo( "item1", _) >> null
			
			grSesBean.getRegistryOperation() >> "created1"
			
			rSummaryVO.setEventDate( "22122014")
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "3")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when TC when  userActiveRegList  is empty and  registry operation  is owner view   "(){
		given:
		    def HashMap<String, Object> map = new HashMap <String, Object>()
            setCommonFieldForOpetationWithEmptyRegList(map,true)		
			
		   grSesBean.getRegistryOperation() >> "owner"
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "3")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when TC when  userActiveRegList  is empty and  registry operation  is removed   "(){
		given:
		    def HashMap<String, Object> map = new HashMap <String, Object>()
            setCommonFieldForOpetationWithEmptyRegList(map,true)		
		
			grSesBean.getRegistryOperation() >> "removed"
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "3")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when TC when  userActiveRegList  is empty and  registry operation  is updated   "(){
		given:
		    def HashMap<String, Object> map = new HashMap <String, Object>()
            setCommonFieldForOpetationWithEmptyRegList(map,true)		
		
			grSesBean.getRegistryOperation() >> "updated"
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "3")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when TC when  userActiveRegList  is empty and  registry operation  is created   "(){
		given:
		    def HashMap<String, Object> map = new HashMap <String, Object>()
            setCommonFieldForOpetationWithEmptyRegList(map,true)		
			grSesBean.getRegistryOperation() >> "created"
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "3")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	private setCommonFieldForOpetationWithEmptyRegList(HashMap<String, Object> map, boolean singleItem){
		requestMock.getObjectParameter("profile") >> profileMock
		profileMock.isTransient() >> false
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		def List<String> urList = ["ur1"]
		def List<String> urAList = []
		
		
		map.put("userActiveRegistriesList", urAList)
		map.put("userRegistriesList",  urList)
		map.put("userRegistrysummaryVO", null)
		sBean.getValues() >> map
		1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus,accStatus1"
		RepositoryItem[] userRegistriesRepItems 
		if(singleItem){
		 userRegistriesRepItems = [userRegistriesRepItem]
		userRegistriesRepItem.getRepositoryId() >> "item1"
		1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
		}else{
		 userRegistriesRepItems = [userRegistriesRepItem,userRegistriesRepItem1]
		 userRegistriesRepItem.getRepositoryId() >> "item1"
		 userRegistriesRepItem1.getRepositoryId() >> "item2"
		 1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
		 1*grManager.getRegistryStatusFromRepo(_, "item2") >> "accStatus1"
		 

		}
		profileMock.getRepositoryId() >> "p1"
		1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
		
		1*grManager.getRegistryInfo( "item1", _) >> null

	}
	
	def "service.TC when  userActiveRegList  is empty and userRegistriesRepItem1 is more then one  "(){
		given:
			grfDroplet = Spy()
			grfDroplet.setGiftRegistryManager(grManager)
			grfDroplet.setGiftRegSessionBean(grSesBean)
			grfDroplet.setProfile(profileMock)
			
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			def List<String> urList = ["ur1"]
			def List<String> urAList = []
			
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			map.put("userActiveRegistriesList", urAList)
			map.put("userRegistriesList",  urList)
			map.put("userRegistrysummaryVO", null)
			sBean.getValues() >> map
			1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus,accStatus1"
			
			
			RepositoryItem[] userRegistriesRepItems = [userRegistriesRepItem,userRegistriesRepItem1,userRegistriesRepItem2]
			profileMock.getRepositoryId() >> "p1"
			1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
			userRegistriesRepItem.getRepositoryId() >> "item1"
			userRegistriesRepItem1.getRepositoryId() >> "item2"
			userRegistriesRepItem2.getRepositoryId() >> "item3"
			1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
			1*grManager.getRegistryStatusFromRepo(_, "item2") >> "accStatus1"
			1*grManager.getRegistryStatusFromRepo(_, "item3") >> "accStatus3"
			
			grManager.fetchUsersSoonestOrRecent(_) >> "accStatus"
			
			
			1*grManager.getRegistryInfo( "accStatus", _) >> rSummaryVO
			
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( "22122014")
			1*grfDroplet.getDateDiff(_, rSummaryVO) >> -100
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "2")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when  userActiveRegList  is empty and userRegistriesRepItem1 is more then one and event date is null "(){
		given:
			
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			def List<String> urList = ["ur1"]
			def List<String> urAList = []
			
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			map.put("userActiveRegistriesList", urAList)
			map.put("userRegistriesList",  urList)
			map.put("userRegistrysummaryVO", rSummaryVO)
			sBean.getValues() >> map
			1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus,accStatus1"
			
			
			RepositoryItem[] userRegistriesRepItems = [userRegistriesRepItem,userRegistriesRepItem1]
			profileMock.getRepositoryId() >> "p1"
			1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
			userRegistriesRepItem.getRepositoryId() >> "item1"
			userRegistriesRepItem1.getRepositoryId() >> "item2"
			
			1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
			1*grManager.getRegistryStatusFromRepo(_, "item2") >> "accStatus1"
			
			grManager.fetchUsersSoonestOrRecent(_) >> null
			
			
			
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( null)
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "4")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when  userActiveRegList  is empty , userRegistriesRepItem is more then one and userRegistrysummaryVO is null "(){
		given:
			
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			def List<String> urList = ["ur1"]
			def List<String> urAList = []
			
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			map.put("userActiveRegistriesList", urAList)
			map.put("userRegistriesList",  urList)
			map.put("userRegistrysummaryVO", null)
			sBean.getValues() >> map
			1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus,accStatus1"
			
			
			RepositoryItem[] userRegistriesRepItems = [userRegistriesRepItem,userRegistriesRepItem1]
			profileMock.getRepositoryId() >> "p1"
			1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
			userRegistriesRepItem.getRepositoryId() >> "item1"
			userRegistriesRepItem1.getRepositoryId() >> "item2"
			
			1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
			1*grManager.getRegistryStatusFromRepo(_, "item2") >> "accStatus1"
			
			grManager.fetchUsersSoonestOrRecent(_) >> null
			
			
			1*grManager.getRegistryInfo( "item1", _) >> null
			
			grSesBean.getRegistryOperation() >> "created1"
		
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "4")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when TC when  userActiveRegList  is empty , userRegistriesRepItem1 is more then one and  registry operation  is owner view   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,false)
			
		   grSesBean.getRegistryOperation() >> "owner"
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "4")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when TC when  userActiveRegList  is empty , userRegistriesRepItem1 is more then one  and  registry operation  is removed   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,false)
		
			grSesBean.getRegistryOperation() >> "removed"
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "4")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when TC when  userActiveRegList  is empty , userRegistriesRepItem1 is more then one  and  registry operation  is updated   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,false)
		
			grSesBean.getRegistryOperation() >> "updated"
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "4")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "service.TC when TC when  userActiveRegList  is empty , userRegistriesRepItem1 is more then one  and  registry operation  is created   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,false)
			grSesBean.getRegistryOperation() >> "created"
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "4")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def"service .when userActiveRegList is empty"(){
		given:
		requestMock.getObjectParameter("profile") >> profileMock
		profileMock.isTransient() >> false
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		def List<String> urList = ["ur1"]
		def List<String> urAList = []
		
		def HashMap<String, Object> map = new HashMap <String, Object>()
		
		map.put("userActiveRegistriesList", urAList)
		map.put("userRegistriesList",  urList)
		map.put("userRegistrysummaryVO", rSummaryVO)
		sBean.getValues() >> map
		1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> ""

		
		RepositoryItem[] userRegistriesRepItems = [userRegistriesRepItem]
		profileMock.getRepositoryId() >> "p1"
		1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
		userRegistriesRepItem.getRepositoryId() >> "item1"
		
		1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"

		
		when:
			grfDroplet.service(requestMock, responseMock)
		
		then:
		
		1*requestMock.setParameter("userStatus", "2")
		map.get("userActiveRegistriesList") == []
		List value = (List<String>)map.get("userRegistriesList")
		value.contains("item1")
	}
	
	def"service .when userRegistriesRepItems list is null"(){
		given:
		    HashMap<String, Object> map = new HashMap <String, Object>()
			commonParameters(map)		
			1*grManager.fetchUserRegistries(_, "p1") >> null

		
		when:
			grfDroplet.service(requestMock, responseMock)
		
		then:
		
		1*requestMock.setParameter("userStatus", "2")
		List value = (List<String>)map.get("userRegistriesList")
	}
	
	def"service .for RepositoryException when userActiveRegList is empty "(){
		given:
			HashMap<String, Object> map = new HashMap <String, Object>()
			commonParameters(map)
			1*grManager.fetchUserRegistries(_, "p1") >> {throw new RepositoryException("exception")}

		
		when:
			grfDroplet.service(requestMock, responseMock)
		
		then:
		
		1*requestMock.setParameter("userStatus", "8")
		1*requestMock.setParameter("errorMsg", _)
	}
	
	def"service .for BBBSystemException when userActiveRegList is empty "(){
		given:
			HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			def List<String> urList = ["ur1"]
			def List<String> urAList = []
			
			
			map.put("userActiveRegistriesList", null)
			map.put("userRegistriesList",  urList)
			map.put("userRegistrysummaryVO", null)
			sBean.getValues() >> map
			1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus"
	
			RepositoryItem[] userRegistriesRepItems = [userRegistriesRepItem]
			
			profileMock.getRepositoryId() >> "p1"
			1*grManager.fetchUserRegistries(_, "p1") >> userRegistriesRepItems
			userRegistriesRepItem.getRepositoryId() >> "item1"
			1*grManager.getRegistryStatusFromRepo(_, "item1") >> "accStatus"
			1*grManager.getRegistryInfo( "item1", _) >> {throw new BBBSystemException("exception")}
	
		
		when:
			grfDroplet.service(requestMock, responseMock)
		
		then:
		
		1*requestMock.setParameter("userStatus", "2")
		1*requestMock.setParameter("registrySummaryVO", null)
		1*requestMock.setParameter("userActiveRegSize", 0)
	}
	
	def"service .for BBBBusinessException when userActiveRegList is empty "(){
		given:
			HashMap<String, Object> map = new HashMap <String, Object>()
			commonParameters(map)
			1*grManager.fetchUserRegistries(_, "p1") >> {throw new BBBBusinessException("exception")}

		
		when:
			grfDroplet.service(requestMock, responseMock)
		
		then:
		
		1*requestMock.setParameter("userStatus", "5")
		1*requestMock.setParameter("errorMsg", _)
	}
	
	def"service .for Exception when userActiveRegList is empty "(){
		given:
			HashMap<String, Object> map = new HashMap <String, Object>()
			commonParameters(map)
			1*grManager.fetchUserRegistries(_, "p1") >> {throw new Exception("exception")}

		
		when:
			grfDroplet.service(requestMock, responseMock)
		
		then:
		
		1*requestMock.setParameter("userStatus", "6")
		1*requestMock.setParameter("errorMsg", _)
	}
	
	
	private commonParameters(HashMap<String, Object> map) {
		
		requestMock.getObjectParameter("profile") >> profileMock
		profileMock.isTransient() >> false
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		def List<String> urList = ["ur1"]
		def List<String> urAList = []
		
		
		map.put("userActiveRegistriesList", null)
		map.put("userRegistriesList",  urList)
		map.put("userRegistrysummaryVO", rSummaryVO)
		sBean.getValues() >> map
		1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> ""

		
		profileMock.getRepositoryId() >> "p1"
	}
	
	/*********************************************** userActiveRegList is not empty ********************************/
	
	def"service.TC when when userActiveRegList is not empty"(){
		given:
		
			grfDroplet = Spy()
			grfDroplet.setGiftRegistryManager(grManager)
			grfDroplet.setGiftRegSessionBean(grSesBean)
			grfDroplet.setProfile(profileMock)
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			rSummaryVO.getRegistryId() >> "sId"
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( "22122014")
			1*grfDroplet.getDateDiff(_, rSummaryVO) >> -100

		
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "2")
		
		
	}
	
	def"service.TC when  userActiveRegList is not empty and event date is null"(){
		given:
		
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			rSummaryVO.getRegistryId() >> "sId"
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( null)
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "3")
			0*grfDroplet.getDateDiff(_, rSummaryVO)
		
		
	}
	
	def"service.TC when  userActiveRegList is not empty and RegistryOperation is owner"(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "owner"
		
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "3")
		
		
	}
	
	def"service.TC when  userActiveRegList is not empty and RegistryOperation is removed"(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "removed"
		
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "3")
		
		
	}
	
	def"service.TC when  userActiveRegList is not empty and RegistryOperation is updated"(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "updated"
		
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "3")
		
		
	}
	
	def"service.TC when  userActiveRegList is not empty and RegistryOperation is created"(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created"
		
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "3")
		
		
	}
	
	def"service.TC when  userActiveRegList is not empty and acceptableStatusesList not contain registry status"(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus11"
			grSesBean.getRegistryOperation() >> "created"
			1*grManager.fetchUsersSoonestOrRecent(_) >> "rrId"
			1*grManager.getRegistryInfo("rrId", _) >> rSummaryVO
		
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
		     map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "4")
		
		
	}
	
	def"service.TC when  userActiveRegList is not empty and registry summery VO is null"(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)

			1*grManager.getRegistryInfo( "userAl", _) >> null
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			 
			0*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "2")
		
		
	}
	
	def"service.TC for Exception when  userActiveRegList is not empty "(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)

			1*grManager.getRegistryInfo( "userAl", _) >> {throw new Exception("exception")}
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			 
			1*requestMock.setParameter("errorMsg", _)
			1*requestMock.setParameter("userStatus", "6")
		
		
	}
	
	def"service.TC for BBBSystemException when  userActiveRegList is not empty "(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)

			1*grManager.getRegistryInfo( "userAl", _) >> {throw new BBBSystemException("exception")}
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			 
			1*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "2")
		
		
	}
	
	def"service.TC for BBBBusinessException when  userActiveRegList is not empty "(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)

			1*grManager.getRegistryInfo( "userAl", _) >> {throw new BBBBusinessException("exception")}
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			 
			1*requestMock.setParameter("errorMsg", _)
			1*requestMock.setParameter("userStatus", "5")
		
		
	}
	
	def"service.TC for NumberFormatException when  userActiveRegList is not empty "(){
		given:
		
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus11"
			grSesBean.getRegistryOperation() >> "created"
			1*grManager.fetchUsersSoonestOrRecent(_) >> {throw new NumberFormatException("exception") }
			
			
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			 
			1*requestMock.setParameter("errorMsg", _)
			1*requestMock.setParameter("userStatus", "7")
		
		
	}
	
	
	private commonParametersForNonEmptyARegList(HashMap<String, Object> map, RegistrySummaryVO pRegSummaryVO){
		requestMock.getObjectParameter("profile") >> profileMock
		profileMock.isTransient() >> false
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		def List<String> urList = ["ur1"]
		def List<String> urAList = ["userAl"]
		
		
		map.put("userActiveRegistriesList", urAList)
		map.put("userRegistriesList",  urList)
		map.put("userRegistrysummaryVO", pRegSummaryVO)
		sBean.getValues() >> map
		1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus"

	}
	
	/***************************************************userActiveRegList size is greater then 1 ****************************/
		def"service.TC when when userActiveRegList size is greater then 1"(){
		given:
		
			grfDroplet = Spy()
			grfDroplet.setGiftRegistryManager(grManager)
			grfDroplet.setGiftRegSessionBean(grSesBean)
			grfDroplet.setProfile(profileMock)
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> "rrId"
			
			1*grManager.getRegistryInfo( "rrId", _) >> rSummaryVO
			
			//rSummaryVO.getRegistryId() >> "sId"
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( "22122014")
			1*grfDroplet.getDateDiff(_, rSummaryVO) >> -100
			1*grManager.fetchUsersSoonestRegistry(_, _) >> "rid"
			1*grManager.getRegistryInfo( "rid", _) >> rSummaryVO
		
		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "4")
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userActiveRegSize", 2)
		
		
	}
		def"service.TC when when userActiveRegList size is greater then 1 and RegistryId is null gets from fetchUsersSoonestRegistry"(){
			given:
			
				grfDroplet = Spy()
				grfDroplet.setGiftRegistryManager(grManager)
				grfDroplet.setGiftRegSessionBean(grSesBean)
				grfDroplet.setProfile(profileMock)
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> "rrId"
				
				1*grManager.getRegistryInfo( "rrId", _) >> rSummaryVO
				
				//rSummaryVO.getRegistryId() >> "sId"
				1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
				grSesBean.getRegistryOperation() >> "created1"
			
				rSummaryVO.setEventDate( "22122014")
				1*grfDroplet.getDateDiff(_, rSummaryVO) >> -100
				1*grManager.fetchUsersSoonestRegistry(_, _) >> null
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				1*sBean.setRegDiffDateLess(true)
				1*requestMock.setParameter("userStatus", "2")
				1*requestMock.setParameter("userActiveRegSize", 2)
			
			
		}
		
		def"service.TC when when userActiveRegList size is greater then 1 and event date is null of summery VO"(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
				
				//rSummaryVO.getRegistryId() >> "sId"
				1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
				grSesBean.getRegistryOperation() >> "created1"
			
				rSummaryVO.setEventDate( null)
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				0*sBean.setRegDiffDateLess(true)
				1*requestMock.setParameter("userStatus", "4")
				1*requestMock.setParameter("userActiveRegSize", 2)
				1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			
		}
		
		def"service.TC when when userActiveRegList size is greater then 1 and registry operation is owner"(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
				
				//rSummaryVO.getRegistryId() >> "sId"
				1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
				grSesBean.getRegistryOperation() >> "owner"
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				0*sBean.setRegDiffDateLess(true)
				1*requestMock.setParameter("userStatus", "4")
				1*requestMock.setParameter("userActiveRegSize", 2)
				1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			
		}
		
		def"service.TC when when userActiveRegList size is greater then 1 and registry operation is removed"(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
				
				1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
				grSesBean.getRegistryOperation() >> "removed"
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				0*sBean.setRegDiffDateLess(true)
				1*requestMock.setParameter("userStatus", "4")
				1*requestMock.setParameter("userActiveRegSize", 2)
				1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			
		}
		
		def"service.TC when when userActiveRegList size is greater then 1 and registry operation is updated"(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
				
				1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
				grSesBean.getRegistryOperation() >> "updated"
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				0*sBean.setRegDiffDateLess(true)
				1*requestMock.setParameter("userStatus", "4")
				1*requestMock.setParameter("userActiveRegSize", 2)
				1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			
		}
		
		def"service.TC when when userActiveRegList size is greater then 1 and registry operation is created"(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, rSummaryVO)
	/*			1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
	*/			
				1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
				grSesBean.getRegistryOperation() >> "created"
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				0*sBean.setRegDiffDateLess(true)
				1*requestMock.setParameter("userStatus", "4")
				1*requestMock.setParameter("userActiveRegSize", 2)
				1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			
		}
		
		def"service.TC when when userActiveRegList size is greater then 1 and registryStatus is not in acceptableStatusesList"(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
				
				1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus1"
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				0*sBean.setRegDiffDateLess(true)
				0*requestMock.setParameter("userStatus", "4")
				1*requestMock.setParameter("userActiveRegSize", 2)
				0*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			
		}
		
		def"service.TC when when userActiveRegList size is greater then 1 and registry summery VO is null"(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> null
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				0*sBean.setRegDiffDateLess(true)
				1*requestMock.setParameter("userStatus", "2")
				1*requestMock.setParameter("userActiveRegSize", 2)
				0*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			
		}
	
		def"service.TC for NumberFormatException when userActiveRegList size is greater then 1 and "(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> {throw new NumberFormatException("exception")}
				
				//1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				
				1*requestMock.setParameter("userStatus", "7")
				1*requestMock.setParameter("errorMsg", _)
				
			
		}
		
		def"service.TC for RepositoryException when userActiveRegList size is greater then 1 and "(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> {throw new RepositoryException("exception")}
				
				//1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				
				1*requestMock.setParameter("userStatus", "8")
				1*requestMock.setParameter("errorMsg", _)
			
		}
		
		def"service.TC for BBBBusinessException when userActiveRegList size is greater then 1 and "(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> {throw new BBBBusinessException("exception")}
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				
				1*requestMock.setParameter("userStatus", "5")
				1*requestMock.setParameter("errorMsg", _)
			
		}
		
		def"service.TC for BBBSystemException when userActiveRegList size is greater then 1 and "(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> {throw new BBBSystemException("exception")}
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				
				1*requestMock.setParameter("userStatus", "2")
				1*requestMock.setParameter("registrySummaryVO", null)
			
		}
		
		def"service.TC for Exception when userActiveRegList size is greater then 1 and "(){
			given:
			
				def HashMap<String, Object> map = new HashMap <String, Object>()
				
				commonParametersForNonEmptyARegListGreaterSize(map, null)
				1*grManager.fetchUsersSoonestOrRecent(_) >> null
				
				1*grManager.getRegistryInfo( "userAl", _) >> {throw new Exception("exception")}
			
			when:
				grfDroplet.service(requestMock, responseMock)
			then:
				
				1*requestMock.setParameter("userStatus", "6")
				1*requestMock.setParameter("errorMsg", _)
			
		}
		
	private commonParametersForNonEmptyARegListGreaterSize(HashMap<String, Object> map, RegistrySummaryVO pRegSummaryVO){
		requestMock.getObjectParameter("profile") >> profileMock
		profileMock.isTransient() >> false
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		def List<String> urList = ["ur1"]
		def List<String> urAList = ["userAl","userAl1"]
		
		
		map.put("userActiveRegistriesList", urAList)
		map.put("userRegistriesList",  urList)
		map.put("userRegistrysummaryVO", pRegSummaryVO)
		sBean.getValues() >> map
		1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "accStatus"

	}
	
	def "service. TC when profile is transient"(){
		given:
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> true

		when:
			grfDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("userStatus", "1")
	}
}
