package com.bbb.commerce.giftregistry.manager

import atg.core.util.Address
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile
import atg.userprofiling.email.TemplateEmailException
import atg.userprofiling.email.TemplateEmailInfoImpl

import java.util.Calendar;
import java.util.HashMap
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException

import com.bbb.account.BBBProfileTools
import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.api.BBBAddressVO
import com.bbb.account.vo.ProfileSyncRequestVO
import com.bbb.account.vo.ProfileSyncResponseVO;
import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegistryTypeVO
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.giftregistry.bean.AddItemsBean
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean
import com.bbb.commerce.giftregistry.droplet.GiftRegistryTypesDroplet
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.commerce.giftregistry.utility.BBBGiftRegistryUtils
import com.bbb.commerce.giftregistry.vo.AddressVO
import com.bbb.commerce.giftregistry.vo.BridalRegistryVO;
import com.bbb.commerce.giftregistry.vo.EventVO
import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO;
import com.bbb.commerce.giftregistry.vo.RegAddressesVO
import com.bbb.commerce.giftregistry.vo.RegInfoVO
import com.bbb.commerce.giftregistry.vo.RegNamesVO
import com.bbb.commerce.giftregistry.vo.RegSearchResVO;
import com.bbb.commerce.giftregistry.vo.RegSkuDetailsVO
import com.bbb.commerce.giftregistry.vo.RegSkuListVO
import com.bbb.commerce.giftregistry.vo.RegistrantVO
import com.bbb.commerce.giftregistry.vo.RegistryBabyVO;
import com.bbb.commerce.giftregistry.vo.RegistryHeaderVO
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.RegistryPrefStoreVO;
import com.bbb.commerce.giftregistry.vo.RegistryReqVO
import com.bbb.commerce.giftregistry.vo.RegistryResVO
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryListVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.commerce.giftregistry.vo.SetAnnouncementCardResVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO
import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO
import com.bbb.commerce.inventory.OnlineInventoryManagerImpl
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.email.BBBTemplateEmailSender
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.webservices.vo.ErrorStatus
import com.bbb.framework.webservices.vo.ValidationError
import com.bbb.profile.session.BBBSessionBean;
import java.text.ParseException;

import spock.lang.specification.BBBExtendedSpec

class GiftRegistryManagerSpecification extends BBBExtendedSpec {

	GiftRegistryManager grManager
	
	RegistryVO registryVO = new RegistryVO() 
	RegistrantVO registrantVO = new RegistrantVO()
	RegistrantVO coRegistrant = new RegistrantVO()
	
	
	BBBProfileTools cTools = Mock()
	MutableRepositoryItem profileItem = Mock()
	MutableRepositoryItem coProfileItem = Mock()
	MutableRepositoryItem giftRegistryItem = Mock()
	
	GiftRegistryTools grTools = Mock()
	Profile profileMock = Mock()
	RepositoryItem reItem = Mock()
	RepositoryItem reItem1 = Mock()
	TemplateEmailInfoImpl temlateInfoMock = Mock()
	BBBTemplateEmailSender emailSenderMock = Mock()
	BBBSessionBean sBean = Mock()
	GiftRegSessionBean grSesBean = Mock()
	RepositoryItem userRegistriesRepItem = Mock()
	RepositoryItem userRegistriesRepItem1 = Mock()
	RepositoryItem userRegistriesRepItem2 = Mock()
	BBBCatalogTools catTools = Mock()
	GiftRegistryTypesDroplet grTypeDroplet = Mock()
	
	RegistrySummaryVO rSummaryVO = new RegistrySummaryVO()
	RegistrySummaryVO rSummaryVO1 = new RegistrySummaryVO()
	RegistrySummaryVO rSummaryVO2 = new RegistrySummaryVO()
	RegistryTypeVO regTypeVo = new RegistryTypeVO()
	BBBGiftRegistryUtils grUtilsMock = Mock()
	BBBEximManager bexManagerMock = Mock()
	GiftRegistryViewBean grViewBean = Mock()
	RepositoryItem profile = Mock()
	ErrorStatus eStatus = Mock()
	RegistrySummaryVO rsVO = new RegistrySummaryVO()
	ServiceErrorVO  sErrorVO = Mock()
	BBBSessionBean sessionBean = Mock()
	MutableRepository catRepository = Mock()
	OnlineInventoryManagerImpl invManagerMock = Mock()
	BBBAddressAPI addressApi = Mock()
	RegistryTypes rType = Mock()
	
	LblTxtTemplateManager ltlManager = Mock()
	
	def setup(){
		grManager = new GiftRegistryManager(tools : cTools, giftRegistryTools : grTools, emailRegistryRecommendation :"testre@gmail.com",
			templateUrl : "/store.jsp", emailSender : emailSenderMock, bbbCatalogTools : catTools, registryInfoServiceName : "regInfo"
			,catalogTools : catTools, giftRegistryTypesDroplet :  grTypeDroplet, giftRegUtils : grUtilsMock, bbbEximPricingManager : bexManagerMock, 
			catalogRepository : catRepository, inventoryManager : invManagerMock, bbbAddressAPI : addressApi,emailCoFoundRegistryType : "coFRegType"
			,emailCoNotFoundRegistryType : "coNoFRegType", lblTxtTemplateManager : ltlManager)
	}
	
	def"linkRegistry.TC to check user is Primary Registrant or Co Registrant"(){
		given:
			registryVO.setRegistrantVO(registrantVO)
			registrantVO.setEmail("test@gmail.com")
			1*cTools.getItemFromEmail("test@gmail.com") >> profileItem
			
			//grTools..addORUpdateRegistry(registryVO, profileItem, null)
		when:
		 	grManager.linkRegistry(registryVO, true)
		then:
			1*grTools.addORUpdateRegistry(registryVO, profileItem, null)
	}
	
	def"linkRegistry.TC to check user is Primary Registrant or Co Registrant when isImportedAsReg is false"(){
		given:
			registryVO.setRegistrantVO(registrantVO)
			registrantVO.setEmail("test@gmail.com")
			1*cTools.getItemFromEmail("test@gmail.com") >> profileItem
			
			//grTools..addORUpdateRegistry(registryVO, profileItem, null)
		when:
			 grManager.linkRegistry(registryVO, false)
		then:
			1*grTools.addORUpdateRegistry(registryVO, null, profileItem)
	}
	
	
	/****************************************  giftRegistryRepoEntry **********************************************/
	
	def"giftRegistryRepoEntry. tc to to insert create registry info in repository"(){
		given:
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(coRegistrant)
			
			registrantVO.setEmail("test@gmail.com")
			coRegistrant.setEmail("test@gmail.com")
			3*cTools.getItemFromEmail("test@gmail.com") >> profileItem >> profileItem >> coProfileItem
			
			//primary reRepository Entry
			1*grTools.addORUpdateRegistry(registryVO, profileItem, null) >> giftRegistryItem
			//end
			//grTools.addORUpdateRegistry(registryVO,profileItem, coProfileItem)
		when:
			grManager.giftRegistryRepoEntry(registryVO, "true")
		then:
			1*grTools.addORUpdateRegistry(registryVO,profileItem, coProfileItem)
	}
	
	def"giftRegistryRepoEntry. tc to to insert create registry info in repository when coreEmailPopupStatus is false"(){
		given:
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(coRegistrant)
			
			registrantVO.setEmail("test@gmail.com")
			coRegistrant.setEmail("test@gmail.com")
			2*cTools.getItemFromEmail("test@gmail.com") >> profileItem >> profileItem 
			
			//primary reRepository Entry
			1*grTools.addORUpdateRegistry(registryVO, profileItem, null) >> giftRegistryItem
			//end
			//grTools.addORUpdateRegistry(registryVO,profileItem, coProfileItem)
		when:
			grManager.giftRegistryRepoEntry(registryVO, "false")
		then:
			0*grTools.addORUpdateRegistry(registryVO,profileItem, coProfileItem)
	}
	
	/************************************************** sendEmailRegistryRecommendation *********************************/
	
	def"sendEmailRegistryRecommendation.TC to send the registry email"(){
		given:
		Map values = ["pRegFirstName":"harry", "pRegLastName":"john", "configurableType":"cgType", "message":"this is it", "registryURL" : "rURL"]
		Map tokensMap = ["t1" : reItem]
		
		1*requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		1*profileMock.getRepositoryId() >> "user12356"
		
		1*reItem.getPropertyValue("inviteeEmailIid") >> "init@emil.com"
		
		when:
		boolean result = grManager.sendEmailRegistryRecommendation("usBed",  values, temlateInfoMock, tokensMap)
		then:
		result
		1*temlateInfoMock.setMessageTo("init@emil.com")
		1 * emailSenderMock.sendEmailMessage(temlateInfoMock, ['init@emil.com'], true, false)
	}
	
	def"sendEmailRegistryRecommendation.TC for TemplateEmailException"(){
		given:
		Map values = ["pRegFirstName":"harry", "pRegLastName":"john", "configurableType":"cgType", "message":"this is it", "registryURL" : "rURL"]
		Map tokensMap = ["t1" : reItem]
		
		1*requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		1*profileMock.getRepositoryId() >> "user12356"
		
		1*reItem.getPropertyValue("inviteeEmailIid") >> "init@emil.com"
		1 * emailSenderMock.sendEmailMessage(temlateInfoMock, ['init@emil.com'], true, false) >> {throw new TemplateEmailException("exception")}
		
		when:
		boolean result = grManager.sendEmailRegistryRecommendation("usBed",  values, temlateInfoMock, tokensMap)
		then:
		!result
		1*temlateInfoMock.setMessageTo("init@emil.com")
	}
	
	def"sendEmailRegistryRecommendation.TC for RepositoryException"(){
		given:
		Map values = ["pRegFirstName":"harry", "pRegLastName":"john", "configurableType":"cgType", "message":"this is it", "registryURL" : "rURL"]
		Map tokensMap = ["t1" : reItem]
		
		1*requestMock.resolveName("/atg/userprofiling/Profile") >> null
		//1*profileMock.getRepositoryId() >> "user12356"
		
		1*reItem.getPropertyValue("inviteeEmailIid") >> "init@emil.com"
		1 * emailSenderMock.sendEmailMessage(temlateInfoMock, ['init@emil.com'], true, false) >> {throw new RepositoryException("exception")}
		
		when:
		boolean result = grManager.sendEmailRegistryRecommendation("usBed",  values, temlateInfoMock, tokensMap)
		then:
		!result
		1*temlateInfoMock.setMessageTo("init@emil.com")
	}
	
	
	/*****************************************  getActiveRegistry  ********************************************
	 */
	
	def"getActiveRegistry. TC when TC when  userActiveRegList  is empty"(){
		given:
		grManager = Spy()
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
		1*grManager.getDateDiff(_, rSummaryVO) >> -100
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
		
			map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "2")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null
			1*requestMock.setParameter("userActiveRegSize", 1)

	}
	
	def"getActiveRegistry. TC when  userActiveRegList  is empty and registration summery VO is not null gets from value map"(){
		given:
		grManager = Spy()
		requestMock.getObjectParameter("profile") >> profileMock
		profileMock.isTransient() >> false
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		def List<String> urList = ["ur1"]
		def List<String> urAList = []
		
		def HashMap<String, Object> map = new HashMap <String, Object>()
		
		map.put("userActiveRegistriesList", null)
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
	
		rSummaryVO.setEventDate(null)
		//1*grManager.getDateDiff(_, rSummaryVO) >> -100
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
		
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "3")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def"getActiveRegistry. TC when  userActiveRegList  is empty and registration summery VO is  null"(){
		given:
		grManager = Spy()
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
		1*grManager.getRegistryInfo( "item1", _) >> null
		
		grSesBean.getRegistryOperation() >> "created1"
	
		rSummaryVO.setEventDate(null)
		//1*grManager.getDateDiff(_, rSummaryVO) >> -100
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
		
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "3")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC  when  userActiveRegList  is empty and  registry operation  is owner view   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,true)
			
		   grSesBean.getRegistryOperation() >> "owner"
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	
	def "getActiveRegistry.TC when TC when  userActiveRegList  is empty and  registry operation  is removed   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,true)
		
			grSesBean.getRegistryOperation() >> "removed"
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC when TC when  userActiveRegList  is empty and  registry operation  is updated   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,true)
		
			grSesBean.getRegistryOperation() >> "updated"
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC when TC when  userActiveRegList  is empty and  registry operation  is created   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,true)
			grSesBean.getRegistryOperation() >> "created"
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	
	private setCommonFieldForOpetationWithEmptyRegList(HashMap<String, Object> map, boolean singleItem){
		grManager = Spy()
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
		
		1*grManager.getRegistryInfo( "item1", _) >> rSummaryVO

	}
	
	def "getActiveRegistry.TC when  userActiveRegList  is empty and userRegistriesRepItem1 is more then one  "(){
		given:
			grManager = Spy()
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
			1*grManager.getDateDiff(_, rSummaryVO) >> -100
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "2")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC when  userActiveRegList  is empty and userRegistriesRepItem1 is more then one and event date is null "(){
		given:
			grManager = Spy()
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
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "4")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC when  userActiveRegList  is empty , userRegistriesRepItem is more then one and userRegistrysummaryVO is null "(){
		given:
			
			grManager = Spy()
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
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
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == null
			2*requestMock.setParameter("registrySummaryVO", null)
			1*requestMock.setParameter("userStatus", "4")
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC when TC when  userActiveRegList  is empty , userRegistriesRepItem1 is more then one and  registry operation  is owner view   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,false)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
		    grSesBean.getRegistryOperation() >> "owner"
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC when TC when  userActiveRegList  is empty , userRegistriesRepItem1 is more then one  and  registry operation  is removed   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,false)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			grSesBean.getRegistryOperation() >> "removed"
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC when TC when  userActiveRegList  is empty , userRegistriesRepItem1 is more then one  and  registry operation  is updated   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,false)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			grSesBean.getRegistryOperation() >> "updated"
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	def "getActiveRegistry.TC when TC when  userActiveRegList  is empty , userRegistriesRepItem1 is more then one  and  registry operation  is created   "(){
		given:
			def HashMap<String, Object> map = new HashMap <String, Object>()
			setCommonFieldForOpetationWithEmptyRegList(map,false)
			grSesBean.getRegistryOperation() >> "created"
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			2*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			map.get("userActiveRegistriesList") != null
			map.get("userRegistriesList") != null

	}
	
	
	def"getActiveRegistry .when userActiveRegList is empty after getting registry status"(){
		given:
		grManager = Spy()
		requestMock.getObjectParameter("profile") >> profileMock
		profileMock.isTransient() >> false
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
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		
		then:
		
		1*requestMock.setParameter("userStatus", "2")
		map.get("userActiveRegistriesList") == []
		List value = (List<String>)map.get("userRegistriesList")
		value.contains("item1")
	}
	
	def"getActiveRegistry .for RepositoryException when userActiveRegList is empty "(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			commonParameters(map)
			1*grManager.fetchUserRegistries(_, "p1") >> {throw new RepositoryException("exception")}

		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		
		then:
			1 * grManager.logError('giftregistry_1075: Repository Exception from service of GetRegistryFlyoutDroplet ', _)
			//1*requestMock.setParameter("errorMsg", _)
	}
	
	def"getActiveRegistry .when userRegistriesRepItems list is null"(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			commonParameters(map)
			1*grManager.fetchUserRegistries(_, "p1") >> null

		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		
		then:
		
			1*requestMock.setParameter("userStatus", "2")
			List value = (List<String>)map.get("userRegistriesList")
	}
	
	def"getActiveRegistry .for BBBSystemException when userActiveRegList is empty "(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> false
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
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		
		then:
		    1 * grManager.logError('giftregistry_1076: System Exception from service of GetRegistryFlyoutDroplet ', _)
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
	
	def"getActiveRegistry .for BBBBusinessException when userActiveRegList is empty "(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			commonParameters(map)
			1*grManager.fetchUserRegistries(_, "p1") >> {throw new BBBBusinessException("exception")}
		
		when:
		
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		
		then:
		    1 * grManager.logError('giftregistry_1005: BBBBusinessException from SERVICE of GiftRegistryFlyoutDroplet', _)
	}
	
	def"getActiveRegistry .for Exception when userActiveRegList is empty "(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			commonParameters(map)
			1*grManager.fetchUserRegistries(_, "p1") >> {throw new Exception("exception")}

		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		
		then:
		
		1 * grManager.logError('giftregistry_1077: Other Exception from service of GetRegistryFlyoutDroplet ',_)
		
			}
	
	/*********************************************** userActiveRegList is not empty ********************************/
	def"getActiveRegistry.TC when when userActiveRegList is not empty"(){
		given:
		
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			rSummaryVO.getRegistryId() >> "sId"
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( "22122014")
			1*grManager.getDateDiff(_, rSummaryVO) >> -100

		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("userStatus", "2")
		
		
	}
	
	def"getActiveRegistry.TC when  userActiveRegList is not empty and event date is null"(){
		given:
		    grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			rSummaryVO.getRegistryId() >> "sId"
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( null)
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "3")
			0*grManager.getDateDiff(_, rSummaryVO)
		
		
	}
	
	def"getActiveRegistry.TC when  userActiveRegList is not empty and RegistryOperation is owner"(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "owner"
		
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "3")
		
		
	}
	
	def"getActiveRegistry.TC when  userActiveRegList is not empty and RegistryOperation is removed"(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "removed"
		
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "3")
		
		
	}
	
	def"getActiveRegistry.TC when  userActiveRegList is not empty and RegistryOperation is updated"(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "updated"
		
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "3")
		
		
	}
	
	def"getActiveRegistry.TC when  userActiveRegList is not empty and RegistryOperation is created"(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created"
		
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "3")
		
		
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
	
	def"getActiveRegistry.TC when  userActiveRegList is not empty and acceptableStatusesList not contain registry status"(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus11"
			grSesBean.getRegistryOperation() >> "created"
			1*grManager.fetchUsersSoonestOrRecent(_) >> "rrId"
			1*grManager.getRegistryInfo("rrId", _) >> rSummaryVO
		
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			 map.get("userRegistrysummaryVO") == rSummaryVO
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "4")
		
		
	}
	
	def"getActiveRegistry.TC when  userActiveRegList is not empty and registry summery VO is null"(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)

			1*grManager.getRegistryInfo( "userAl", _) >> null
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			 
			0*requestMock.setParameter("registrySummaryVO", rSummaryVO)
			1*requestMock.setParameter("userStatus", "2")
		
		
	}
	
	def"getActiveRegistry.TC for Exception when  userActiveRegList is not empty "(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)

			1*grManager.getRegistryInfo( "userAl", _) >> {throw new Exception("exception")}
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			 
			1*requestMock.setParameter("userStatus", "6")
		
		
	}
	
	def"getActiveRegistry.TC for BBBSystemException when  userActiveRegList is not empty "(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)

			1*grManager.getRegistryInfo( "userAl", _) >> {throw new BBBSystemException("exception")}
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			 
			1*requestMock.setParameter("userStatus", "6")
		
		
	}
	
	def"getActiveRegistry.TC for BBBBusinessException when  userActiveRegList is not empty "(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, null)

			1*grManager.getRegistryInfo( "userAl", _) >> {throw new BBBBusinessException("exception")}
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			 
			1*requestMock.setParameter("userStatus", "5")
		
		
	}
	
	def"service.TC for NumberFormatException when  userActiveRegList is not empty "(){
		given:
			grManager = Spy()
			HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegList(map, rSummaryVO)

			1*grManager.getRegistryStatusFromRepo(_,_) >> "accStatus11"
			grSesBean.getRegistryOperation() >> "created"
			1*grManager.fetchUsersSoonestOrRecent(_) >> {throw new NumberFormatException("exception") }
			
			
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			 
			1*requestMock.setParameter("userStatus", "7")
		
		
	}
	
	/***************************************************userActiveRegList size is greater then 1 ****************************/
	def"getActiveRegistry.TC when when userActiveRegList size is greater then 1"(){
	given:
		grManager = Spy()
		def HashMap<String, Object> map = new HashMap <String, Object>()
		
		commonParametersForNonEmptyARegListGreaterSize(map, null)
		1*grManager.fetchUsersSoonestOrRecent(_) >> "rrId"
		
		1*grManager.getRegistryInfo( "rrId", _) >> rSummaryVO
		
		//rSummaryVO.getRegistryId() >> "sId"
		1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
		grSesBean.getRegistryOperation() >> "created1"
	
		rSummaryVO.setEventDate( "22122014")
		1*grManager.getDateDiff(_, rSummaryVO) >> -100
		1*grManager.fetchUsersSoonestRegistry(_, _) >> "rid"
		1*grManager.getRegistryInfo( "rid", _) >> rSummaryVO
	
	when:
		grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
	then:
		map.get("userRegistrysummaryVO") == rSummaryVO
		0*sBean.setRegDiffDateLess(true)
		1*requestMock.setParameter("userStatus", "4")
		1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
		1*requestMock.setParameter("userActiveRegSize", 2)
	
	
}
	
	def"getActiveRegistry.TC when when userActiveRegList size is greater then 1 and RegistryId is null gets from fetchUsersSoonestRegistry"(){
		given:
		
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> "rrId"
			
			1*grManager.getRegistryInfo( "rrId", _) >> rSummaryVO
			
			//rSummaryVO.getRegistryId() >> "sId"
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( "22122014")
			1*grManager.getDateDiff(_, rSummaryVO) >> -100
			1*grManager.fetchUsersSoonestRegistry(_, _) >> null
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("userStatus", "2")
			1*requestMock.setParameter("userActiveRegSize", 2)
		
		
	}
	
	def"getActiveRegistry.TC when when userActiveRegList size is greater then 1 and event date is null of summery VO"(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			//rSummaryVO.getRegistryId() >> "sId"
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created1"
		
			rSummaryVO.setEventDate( null)
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "4")
			1*requestMock.setParameter("userActiveRegSize", 2)
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
		
	}
	
	def"getActiveRegistry.TC when when userActiveRegList size is greater then 1 and registry operation is owner"(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			//rSummaryVO.getRegistryId() >> "sId"
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "owner"
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("userStatus", "4")
			1*requestMock.setParameter("userActiveRegSize", 2)
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
		
	}
	
	def"getActiveRegistry.TC when when userActiveRegList size is greater then 1 and registry operation is removed"(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "removed"
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "4")
			1*requestMock.setParameter("userActiveRegSize", 2)
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
		
	}
	
	def"getActiveRegistry.TC when  userActiveRegList size is greater then 1 and registry operation is updated"(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "updated"
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			0*sBean.setRegDiffDateLess(true)
			1*requestMock.setParameter("userStatus", "4")
			1*requestMock.setParameter("userActiveRegSize", 2)
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
		
	}
	
	def"getActiveRegistry.TC when when userActiveRegList size is greater then 1 and registry operation is created"(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, rSummaryVO)
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus"
			grSesBean.getRegistryOperation() >> "created"
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("userStatus", "4")
			1*requestMock.setParameter("userActiveRegSize", 2)
			1*requestMock.setParameter("registrySummaryVO", rSummaryVO)
		
	}
	
	def"getActiveRegistry.TC when userActiveRegList size is greater then 1 and registryStatus is not in acceptableStatusesList"(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
			
			1*grManager.getRegistryStatusFromRepo(_,	_) >> "accStatus1"
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			0*requestMock.setParameter("userStatus", "4")
			1*requestMock.setParameter("userActiveRegSize", 2)
			0*requestMock.setParameter("registrySummaryVO", rSummaryVO)
		
	}
	
	def"getActiveRegistry.TC when when userActiveRegList size is greater then 1 and registry summery VO is null"(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> null
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("userStatus", "2")
			1*requestMock.setParameter("userActiveRegSize", 2)
			0*requestMock.setParameter("registrySummaryVO", rSummaryVO)
		
	}
	
	def"getActiveRegistry.TC for NumberFormatException when userActiveRegList size is greater then 1 and "(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> {throw new NumberFormatException("exception")}
			
			//1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			
			1*requestMock.setParameter("userStatus", "7")
			
		
	}
	
	def"getActiveRegistry.TC for RepositoryException when userActiveRegList size is greater then 1 and "(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> {throw new RepositoryException("exception")}
			
			//1*grManager.getRegistryInfo( "userAl", _) >> rSummaryVO
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			
			1*requestMock.setParameter("userStatus", "8")
		
	}
	
	def"getActiveRegistry.TC for BBBBusinessException when userActiveRegList size is greater then 1 and "(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> {throw new BBBBusinessException("exception")}
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			
			1*requestMock.setParameter("userStatus", "5")
		
	}
	
	def"getActiveRegistry.TC for BBBSystemException when userActiveRegList size is greater then 1 and "(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> {throw new BBBSystemException("exception")}
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			
			1*requestMock.setParameter("userStatus", "6")
		
	}
	
	def"getActiveRegistry.TC for Exception when userActiveRegList size is greater then 1 and "(){
		given:
			grManager = Spy()
			def HashMap<String, Object> map = new HashMap <String, Object>()
			
			commonParametersForNonEmptyARegListGreaterSize(map, null)
			1*grManager.fetchUsersSoonestOrRecent(_) >> null
			
			1*grManager.getRegistryInfo( "userAl", _) >> {throw new Exception("exception")}
		
		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			
			1*requestMock.setParameter("userStatus", "6")
		
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
	
	def "getActiveRegistry. TC when profile is transient"(){
		given:
			requestMock.getObjectParameter("profile") >> profileMock
			profileMock.isTransient() >> true

		when:
			grManager.getActiveRegistry(sBean, requestMock, profileMock, grSesBean)
		then:
			1*requestMock.setParameter("userStatus", "1")
	}
	
	
	
	
	/************************************************* giftRegistryRepoUpdate ************************************/
	
	def"giftRegistryRepoUpdate.TC  to update gift registry repository"(){
		given:
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(coRegistrant)
			coRegistrant.setEmail("co@gmail.com")
			registrantVO.setEmail("test@gmail.com")
			
			
			registryVO.setSiteId("usBed")
	
			1*cTools.getItemFromEmail("test@gmail.com") >> profileItem
			1*cTools.getItemFromEmail("co@gmail.com") >> coProfileItem
		when:
			grManager.giftRegistryRepoUpdate(registryVO, "true", "true")
		then:
			1*grTools.addORUpdateRegistry(registryVO, profileItem,coProfileItem)
	}
	
	def"giftRegistryRepoUpdate.TC  to update gift registry repository when email id is null of CORegistrantVO"(){
		given:
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(coRegistrant)
			coRegistrant.setEmail(null)
			registrantVO.setEmail("test@gmail.com")
			
			
			registryVO.setSiteId("usBed")
	
			1*cTools.getItemFromEmail("test@gmail.com") >> profileItem
			//1*cTools.getItemFromEmail("co@gmail.com") >> coProfileItem
		when:
			grManager.giftRegistryRepoUpdate(registryVO, "true", "true")
		then:
			1*grTools.addORUpdateRegistry(registryVO, profileItem,null)
	}
	
	def"giftRegistryRepoUpdate.TC  to update gift registry repository when  CORegistrantVO is null"(){
		given:
			RegistryVO registryVO1 = Mock()
			registryVO1.getPrimaryRegistrant() >> registrantVO
			registryVO1.getCoRegistrant() >> null
			registrantVO.setEmail("test@gmail.com")
			
			
			registryVO.setSiteId("usBed")
	
			1*cTools.getItemFromEmail("test@gmail.com") >> profileItem
			//1*cTools.getItemFromEmail("co@gmail.com") >> coProfileItem
		when:
			grManager.giftRegistryRepoUpdate(registryVO1, "true", "true")
		then:
			1*grTools.addORUpdateRegistry(registryVO1, profileItem,null)
	}
	
	def"giftRegistryRepoUpdate.TC  to update gift registry repository when  coEmailPopupStatus is false"(){
		given:
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(null)
			registrantVO.setEmail("test@gmail.com")
			
			
			registryVO.setSiteId("usBed")
	
			1*cTools.getItemFromEmail("test@gmail.com") >> profileItem
			//1*cTools.getItemFromEmail("co@gmail.com") >> coProfileItem
		when:
			grManager.giftRegistryRepoUpdate(registryVO, "false", "true")
		then:
			1*grTools.addORUpdateRegistry(registryVO, profileItem,null)
	}
	
	
	/********************************************** getRegistryInfo ********************************************/
	
	def"getRegistryInfo. TC  to get the shipping address for registry items"(){
		given:
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
			RegistryVO regisVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			EventVO eVO = new EventVO()
			RegistryTypes rType = Mock()
			
			List currentViewRegistry = [rSummeryVO]
			def HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			sBean.getValues() >> map
			
			map.put("registryIdList",currentViewRegistry)
			rSummeryVO.setRegistryId("regId")
			1*catTools.getAllValuesForKey( "WSDLSiteFlags", "BedBathCanada") >> ["can"]
			
			1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
			
			// start getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["true"]
			1*grTools.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo'&& rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO
			//end 
			registryResVO.setRegistrySummaryVO(rSummeryVO1)
			registryResVO.setRegistryVO(regisVO)
			
			regisVO.setPrimaryRegistrant(registrantVO)
			registrantVO.setEmail("relgistrant@gmail.com")
			registrantVO.setCellPhone("123456")
			
			regisVO.setEvent("etype") >> eVO
			
			rSummeryVO1.setRegistryType(rType)
			rType.getRegistryTypeName() >> "rType"
			
			2*catTools.getRegistryTypeName("rType",_) >> "eventType" >> "eventType" 
			
			rSummeryVO1.setEventDate("12/20/2010")
			
			rSummeryVO1.setFutureShippingDate("01/01/2010")
		
		when:
			grManager.getRegistryInfo("rId", "BedBathCanada")
		then:
		
			rSummeryVO1.getRegistrantEmail() == "relgistrant@gmail.com"
			rSummeryVO1.getEventVO() == eVO
			rSummeryVO1.getPrimaryRegistrantMobileNum() == "123456"
			rSummeryVO1.getEventType() == "eventType"
			rType.setRegistryTypeDesc("eventType") 
			rSummeryVO1.getFutureShippingDate() == "01/01/2010"
			rSummeryVO1.getEventDate() == "12/20/2010"
	}
	
	def"getRegistryInfo. TC  to get the shipping address for registry item. when regItemsWSCallFlag List is empty"(){
		given:
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
			RegistryVO regisVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			EventVO eVO = new EventVO()
			RegistryTypes rType = Mock()
			
			List currentViewRegistry = [null]
			def HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			sBean.getValues() >> map
			
			map.put("registryIdList",currentViewRegistry)
			//rSummeryVO.setRegistryId("regId")
			1*catTools.getAllValuesForKey( "WSDLSiteFlags", "BedBathCanada") >> ["can"]
			
			1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
			
			// start getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> []
			1*grTools.getRegistryInfoFromEcomAdmin('rId', 'can') >> registryResVO
			//end
			registryResVO.setRegistrySummaryVO(rSummeryVO1)
			registryResVO.setRegistryVO(null)
			
			rSummeryVO1.setRegistryType(rType)
			rType.getRegistryTypeName() >> "rType"
			
			2*catTools.getRegistryTypeName("rType",_) >> "eventType" >> "eventType"
			
			rSummeryVO1.setEventDate("12/20/2010")
			
			rSummeryVO1.setFutureShippingDate(null)
		
		when:
			grManager.getRegistryInfo("rId", "BedBathCanada")
		then:
		
			rSummeryVO1.getEventType() == "eventType"
			rType.setRegistryTypeDesc("eventType")
			rSummeryVO1.getEventDate() == "12/20/2010"
			0*grTools.getRegistryInfo()
	}
	
	def"getRegistryInfo. TC  site is not BedBathCanada "(){
		given:
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
			RegistryVO regisVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			EventVO eVO = new EventVO()
			RegistryTypes rType = Mock()
			
			List currentViewRegistry = [null]
			def HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			sBean.getValues() >> map
			
			map.put("registryIdList",currentViewRegistry)
			//rSummeryVO.setRegistryId("regId")
			1*catTools.getAllValuesForKey( "WSDLSiteFlags", "us") >> ["us"]
			
			1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
			
			// start getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> null
			1*grTools.getRegistryInfoFromEcomAdmin('rId', 'us') >> registryResVO
			//end
			registryResVO.setRegistrySummaryVO(rSummeryVO1)
			registryResVO.setRegistryVO(null)
			
			rSummeryVO1.setRegistryType(rType)
			rType.getRegistryTypeName() >> "rType"
			
			2*catTools.getRegistryTypeName("rType",_) >> "eventType" >> "eventType"
			
			rSummeryVO1.setEventDate("12/20/2010")
			
			rSummeryVO1.setFutureShippingDate(null)
		
		when:
			grManager.getRegistryInfo("rId", "us")
		then:
		
			rSummeryVO1.getEventType() == "eventType"
			rType.setRegistryTypeDesc("eventType")
			rSummeryVO1.getEventDate() == "12/20/2010"
			0*grTools.getRegistryInfo()
	}
	
	def"getRegistryInfo. TC  when event date is null"(){
		given:
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
			RegistryVO regisVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			EventVO eVO = new EventVO()
			RegistryTypes rType = Mock()
			
			//List currentViewRegistry = [null]
			def HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			1*sBean.getValues() >> map
			
			map.put("registryIdList",null)
			//rSummeryVO.setRegistryId("regId")
			1*catTools.getAllValuesForKey( "WSDLSiteFlags", "us") >> ["us"]
			
			1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
			
			// start getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> null
			1*grTools.getRegistryInfoFromEcomAdmin('rId', 'us') >> registryResVO
			//end
			registryResVO.setRegistrySummaryVO(rSummeryVO1)
			registryResVO.setRegistryVO(null)
			
			rSummeryVO1.setRegistryType(rType)
			rType.getRegistryTypeName() >> "rType"
			
			2*catTools.getRegistryTypeName("rType",_) >> "eventType" >> "eventType"
			
			rSummeryVO1.setEventDate(null)
			
			rSummeryVO1.setFutureShippingDate(null)
		
		when:
			grManager.getRegistryInfo("rId", "us")
		then:
		
			rSummeryVO1.getEventType() == "eventType"
			rType.setRegistryTypeDesc("eventType")
			0*grTools.getRegistryInfo()
	}
	
	def"getRegistryInfo. TC  when regSummery VO is null"(){
		given:
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
			RegistryVO regisVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			EventVO eVO = new EventVO()
			RegistryTypes rType = Mock()
			
			//List currentViewRegistry = [null]
			def HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			1*sBean.getValues() >> map
			
			map.put("registryIdList",null)
			//rSummeryVO.setRegistryId("regId")
			1*catTools.getAllValuesForKey( "WSDLSiteFlags", "us") >> ["us"]
			
			1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
			
			// start getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> null
			1*grTools.getRegistryInfoFromEcomAdmin('rId', 'us') >> registryResVO
			//end
			registryResVO.setRegistrySummaryVO(null)
			registryResVO.setRegistryVO(null)
			
			
			0*catTools.getRegistryTypeName("rType",_) >> "eventType" >> "eventType"
			
		when:
			grManager.getRegistryInfo("rId", "us")
		then:
		
			rSummeryVO1.getEventType() == null
			0*grTools.getRegistryInfo()
	}
	
	def"getRegistryInfo. TC  when registryResponse  VO is null"(){
		given:
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
			RegistryVO regisVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			EventVO eVO = new EventVO()
			RegistryTypes rType = Mock()
			
			//List currentViewRegistry = [null]
			def HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			1*sBean.getValues() >> map
			
			map.put("registryIdList",null)
			//rSummeryVO.setRegistryId("regId")
			1*catTools.getAllValuesForKey( "WSDLSiteFlags", "us") >> ["us"]
			
			1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
			
			// start getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> null
			1*grTools.getRegistryInfoFromEcomAdmin('rId', 'us') >> null
			//end
			
			0*catTools.getRegistryTypeName("rType",_) >> "eventType" >> "eventType"
			
		when:
			grManager.getRegistryInfo("rId", "us")
		then:
		
			rSummeryVO1.getEventType() == null
			0*grTools.getRegistryInfo()
	}
	
	def"getRegistryInfo. TC  registrySummaryVOForCart"(){
		given:
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
			RegistryVO regisVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			EventVO eVO = new EventVO()
			RegistryTypes rType = Mock()
			
			List currentViewRegistry = [rSummeryVO]
			def HashMap<String, Object> map = new HashMap <String, Object>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			sBean.getValues() >> map
			
			map.put("registryIdList",currentViewRegistry)
			rSummeryVO.setRegistryId("rId")
		
		when:
			RegistrySummaryVO result = grManager.getRegistryInfo("rId", "us")
		then:
			result.getRegistryId() == "rId"
			rSummeryVO1.getEventType() == null
			0*grTools.getRegistryInfo()
			0*catTools.getAllValuesForKey( "WSDLSiteFlags", _)
	}
	
	/******************************************************** canScheuleAppointment ***************************/
	
	def "canScheuleAppointment. To check schedule appointment" (){
		given:
		1*grTools.canScheduleAppointmentForRegType("usBed", "rType") >> true
		1*grTools.canScheduleAppointmentForStore("sId","rType") >> false
		when:
			boolean value = grManager.canScheuleAppointment("sId", "rType", "usBed")
		then:
		!value 
	}
	
	def "canScheuleAppointment. To check schedule appointment when schedule appointment for registry type is false" (){
		given:
		1*grTools.canScheduleAppointmentForRegType("usBed", "rType") >> false
		//grTools.canScheduleAppointmentForStore("sId","rType") >> true
		when:
			boolean value = grManager.canScheuleAppointment("sId", "rType", "usBed")
		then:
		!value
		0*grTools.canScheduleAppointmentForStore("sId","rType")
	}
	
	/*************************************  getRegistryInfoFromDB ***********************************************/
	
	def"getRegistryInfoFromDB. TC to get the registry info from DB"(){
		given:
		1*grTools.getRegistryInfoFromDB("rId", "usBed") >> [reItem]
		1*reItem.getPropertyValue("registryNum") >> 3
		1*reItem.getPropertyValue("eventType") >> "eType"
		1*reItem.getPropertyValue("eventDate") >> 121022
		1*reItem.getPropertyValue("actionCD")   >> "actCd"
		1*reItem.getPropertyValue("GiftWrap") >> "gw"
		1*reItem.getPropertyValue("onlineRegFlag") >> "regFlag"
		1*reItem.getPropertyValue("processFlag") >> "pFlag"
		when:
		RegInfoVO regInfoVO = grManager.getRegistryInfoFromDB( "rId", "usBed")
		then:
		
		regInfoVO.getRegistryNum() ==3
		regInfoVO.getEventType() == "eType"
		regInfoVO.getEventDate() ==121022
		regInfoVO.getActionCD() == "actCd"
		regInfoVO.getGiftWrap() ==  "gw"
		regInfoVO.getOnlineRegFlag() == "regFlag"
		regInfoVO.getProcessFlag() ==  "pFlag"
	}
	
	
	def"getRegistryInfoFromDB. TC when repository item is null"(){
		given:
		1*grTools.getRegistryInfoFromDB("rId", "usBed") >> null
		
		when:
		RegInfoVO regInfoVO = grManager.getRegistryInfoFromDB( "rId", "usBed")
		
		then:
		0*reItem.getPropertyValue("registryNum")
		regInfoVO.getRegistryNum() == 0
		regInfoVO.getEventType() == null
	}
	
	/********************************************** getRegistryAddressesFromDB *******************************************/
	
	def"getRegistryAddressesFromDB TC to get registry address from  DB"(){
		given:
		1*grTools.getRegistryAddressesFromDB("rId") >> [reItem]
		reItem.getPropertyValue("registryNum") >> 3
		reItem.getPropertyValue("nameAddrNum") >> 2
		reItem.getPropertyValue("nameAddrType") >> "nType"
		reItem.getPropertyValue("lastName") >> "lName"
		reItem.getPropertyValue("firstName") >> "fName"
		reItem.getPropertyValue("address1") >> "address1"
		reItem.getPropertyValue("address2") >> "sddress2"
		reItem.getPropertyValue("city") >> "new"
		reItem.getPropertyValue("state") >> "new york"
		reItem.getPropertyValue("zipCode") >> "12054"
		reItem.getPropertyValue("dayPhone") >> "985641222"
		reItem.getPropertyValue("dayPhoneExt") >> "125458"
		
		when:
		RegAddressesVO regAddVO = 	grManager.getRegistryAddressesFromDB("rId")
		
		then:
		RegNamesVO regName = regAddVO.getRegAddressVO().get(0)
		//rgeName.getRegistryNum() == 3
		regName.getNameAddrNum() == 2
		regName.getNameAddrType() == "nType"
		regName.getLastName() ==  "lName"
		regName.getFirstName() == "fName"
		regName.getAddress1() == "address1"
		regName.getAddress2() == "sddress2"
		regName.getCity() == "new"
		regName.getState() == "new york"
		regName.getZipCode() == "12054"
		regName.getDayPhone() == "985641222"
		regName.getDayPhoneExt() == "125458"
			
		
	}
	
	
	/************************************************* getRegistrySkuDetailsFromDB **************************************/
	def"getRegistrySkuDetailsFromDB. TC to get registry sku details"(){
	given:
		1*grTools.getRegistrySkuDetailsFromDB("rId") >> [reItem]
		reItem.getPropertyValue("registryNum") >> 2
		reItem.getPropertyValue("skuId") >> 123
		reItem.getPropertyValue("qtyFulfilled") >> 5
		reItem.getPropertyValue("qtyRequested") >> 4
		reItem.getPropertyValue("actionCD") >> "actCD"
		
	when:
		RegSkuListVO result = grManager.getRegistrySkuDetailsFromDB("rId")
	then:
		RegSkuDetailsVO rsdVO = result.getRegAddressVO().get(0)
		rsdVO.getRegistryNum() == 2
		rsdVO.getSkuId()== 123
		rsdVO.getQtyFulfilled() == 5
		rsdVO.getQtyRequested() == 4
		rsdVO.getActionCD() == "actCD"
	
	}
	
	def"getRegistrySkuDetailsFromDB. TC to get registry sku details when sku details gets null"(){
		given:
			1*grTools.getRegistrySkuDetailsFromDB("rId") >> null
			
		when:
			RegSkuListVO result = grManager.getRegistrySkuDetailsFromDB("rId")
		then:
			0*reItem.getPropertyValue("registryNum")
			0*reItem.getPropertyValue("skuId")
		
		}
	
	
	/**************************************************** getRegistryInfoFromWebService ************************************/
	
	def "getRegistryInfoFromWebService . tc to get registry info " (){
		given:
		RegistryResVO registryResVO = new RegistryResVO()
		
		RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
		RegistryVO regisVO = new RegistryVO()
		RegistrantVO registrantVO = new RegistrantVO()
		RegistryTypes rType = Mock()

		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "BedBathCanada") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grTools.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo'&& rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO 
		
		registryResVO.setRegistrySummaryVO(rSummeryVO1)
		registryResVO.setRegistryVO(regisVO)
		
		regisVO.setPrimaryRegistrant(registrantVO)
		registrantVO.setEmail("relgistrant@gmail.com")
		
		
		rSummeryVO1.setRegistryType(rType)
		rType.getRegistryTypeName() >> "rType"
		
		1*catTools.getRegistryTypeName("rType",_) >> "eventType" >> "eventType"
		
		rSummeryVO1.setEventDate("12/20/2010")
		
		rSummeryVO1.setFutureShippingDate("01/01/2010")

		when:
			RegistrySummaryVO rsVO =grManager.getRegistryInfoFromWebService("rId", "BedBathCanada")
		then:
		rsVO != null
		rSummeryVO1.getRegistrantEmail() == "relgistrant@gmail.com"
		1*rType.setRegistryTypeDesc("eventType")
		rSummeryVO1.getFutureShippingDate() == "01/01/2010"
		rSummeryVO1.getEventDate() == "12/20/2010"

	}
	
	def "getRegistryInfoFromWebService . tc when future shipping date is null " (){
		given:
		RegistryResVO registryResVO = new RegistryResVO()
		
		RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
		RegistryVO regisVO = new RegistryVO()
		RegistrantVO registrantVO = new RegistrantVO()
		RegistryTypes rType = Mock()

		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "BedBathCanada") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grTools.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo'&& rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO
		
		registryResVO.setRegistrySummaryVO(rSummeryVO1)
		registryResVO.setRegistryVO(null)
		
		//regisVO.setPrimaryRegistrant(registrantVO)
		//registrantVO.setEmail("relgistrant@gmail.com")
		
		
		rSummeryVO1.setRegistryType(null)
		//rType.getRegistryTypeName() >> "rType"
		
		//1*catTools.getRegistryTypeName("rType",_) >> "eventType" >> "eventType"
		
		rSummeryVO1.setEventDate("12/20/2010")
		
		rSummeryVO1.setFutureShippingDate(null)

		when:
			RegistrySummaryVO rsVO =grManager.getRegistryInfoFromWebService("rId", "BedBathCanada")
		then:
		rsVO != null
		rSummeryVO1.getRegistrantEmail() == null
		0*rType.setRegistryTypeDesc("eventType")
		rSummeryVO1.getFutureShippingDate() == null
		rSummeryVO1.getEventDate() == "12/20/2010"

	}
	
	def "getRegistryInfoFromWebService . tc when site is not BedBathCanada " (){
		given:
		RegistryResVO registryResVO = new RegistryResVO()
		
		RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
		RegistryVO regisVO = new RegistryVO()
		RegistrantVO registrantVO = new RegistrantVO()
		RegistryTypes rType = Mock()

		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grTools.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo'&& rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO
		
		registryResVO.setRegistrySummaryVO(rSummeryVO1)
		registryResVO.setRegistryVO(null)
		
		
		
		rSummeryVO1.setRegistryType(null)
		
		rSummeryVO1.setEventDate("12/20/2010")
		
		rSummeryVO1.setFutureShippingDate(null)

		when:
			RegistrySummaryVO rsVO =grManager.getRegistryInfoFromWebService("rId", "usBed")
		then:
		rsVO != null
		rSummeryVO1.getRegistrantEmail() == null
		0*rType.setRegistryTypeDesc("eventType")
		rSummeryVO1.getFutureShippingDate() == null
		rSummeryVO1.getEventDate() == "12/20/2010"

	}
	
	def "getRegistryInfoFromWebService . tc when event date is null " (){
		given:
		RegistryResVO registryResVO = new RegistryResVO()
		
		RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
		RegistryVO regisVO = new RegistryVO()
		RegistrantVO registrantVO = new RegistrantVO()
		RegistryTypes rType = Mock()

		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grTools.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo'&& rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO
		
		registryResVO.setRegistrySummaryVO(rSummeryVO1)
		registryResVO.setRegistryVO(null)
		
		
		
		rSummeryVO1.setRegistryType(null)
		
		rSummeryVO1.setEventDate(null)
		
		rSummeryVO1.setFutureShippingDate(null)

		when:
			RegistrySummaryVO rsVO =grManager.getRegistryInfoFromWebService("rId", "usBed")
		then:
		rsVO != null
		rSummeryVO1.getRegistrantEmail() == null
		0*rType.setRegistryTypeDesc("eventType")
		rSummeryVO1.getFutureShippingDate() == null
		rSummeryVO1.getEventDate() == null

	}
	
	def "getRegistryInfoFromWebService . tc when RegistrySummaryVO  is null fets from response " (){
		given:
		RegistryResVO registryResVO = new RegistryResVO()
		
		RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
		RegistryVO regisVO = new RegistryVO()
		RegistrantVO registrantVO = new RegistrantVO()
		RegistryTypes rType = Mock()

		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grTools.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo'&& rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO
		
		registryResVO.setRegistrySummaryVO(null)
		
		when:
			RegistrySummaryVO rsVO =grManager.getRegistryInfoFromWebService("rId", "usBed")
		then:
		
		rsVO == null
		rSummeryVO1.getRegistrantEmail() == null
		0*rType.setRegistryTypeDesc("eventType")
		rSummeryVO1.getFutureShippingDate() == null
		rSummeryVO1.getEventDate() == null

	}
	
	/*********************************************  getRegistryDetailInfo ***********************************/
	
	def "getRegistryDetailInfo.TC to get registry detail" (){
		given:
		grManager = Spy()
		grManager.setBbbCatalogTools(catTools)
		grManager.setRegistryInfoServiceName("regInfo")
		RegistryVO regisVO = new RegistryVO()
		RegistryTypes rType = Mock()
		RegistryResVO registryResVO = new RegistryResVO()
		ServiceErrorVO sErroVO = new ServiceErrorVO()
		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grManager.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo' && rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO
		registryResVO.setServiceErrorVO(sErroVO)
		sErroVO.setErrorExists(false)
		sErroVO.setErrorMessage("")
		
		registryResVO.setRegistryVO(regisVO)
		regisVO.setRegistryType(rType)
		rType.getRegistryTypeName() >> "rType"
		
		1*catTools.getRegistryTypeName("rType",_) >> "eventType"
		
		
		when:
			RegistryVO rv = grManager.getRegistryDetailInfo("rId", "usBed")
		then:
		rv.getRegistryType() == rType
		1*rType.setRegistryTypeDesc("eventType")
	}
	
	def "getRegistryDetailInfo.TC when RegistryTypeDescRegistryTypeDesc is not empty " (){
		given:
		grManager = Spy()
		grManager.setBbbCatalogTools(catTools)
		grManager.setRegistryInfoServiceName("regInfo")
		RegistryVO regisVO = new RegistryVO()
		RegistryTypes rType = Mock()
		RegistryResVO registryResVO1 = Mock()
		ServiceErrorVO sErroVO = new ServiceErrorVO()
		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grManager.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo' && rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO1
		registryResVO1.getServiceErrorVO() >> null
		//sErroVO.setErrorExists(false)
		//sErroVO.setErrorMessage("")
		
		registryResVO1.getRegistryVO() >> regisVO
		regisVO.setRegistryType(rType)
		rType.getRegistryTypeDesc() >> "desc"
		
		
		when:
			RegistryVO rv = grManager.getRegistryDetailInfo("rId", "usBed")
		then:
		rv.getRegistryType() == rType
		0*rType.setRegistryTypeDesc("eventType")
		0*catTools.getRegistryTypeName(_,_)
	}
	
	def "getRegistryDetailInfo.TC when registry VO is null " (){
		given:
		grManager = Spy()
		grManager.setBbbCatalogTools(catTools)
		grManager.setRegistryInfoServiceName("regInfo")
		RegistryTypes rType = Mock()
		RegistryResVO registryResVO = new RegistryResVO()
		ServiceErrorVO sErroVO = new ServiceErrorVO()
		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grManager.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo' && rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO
		registryResVO.setServiceErrorVO(sErroVO)
		sErroVO.setErrorExists(false)
		sErroVO.setErrorMessage("")
		
		registryResVO.setRegistryVO(null)
		//regisVO.setRegistryType(rType)
		//rType.getRegistryTypeDesc() >> "desc"
		
		
		when:
			RegistryVO rv = grManager.getRegistryDetailInfo("rId", "usBed")
		then:
		rv == null
		0*rType.setRegistryTypeDesc("eventType")
		0*catTools.getRegistryTypeName(_,_)
	}
	
	def "getRegistryDetailInfo.TC when error vo having error message " (){
		given:
		grManager = Spy()
		grManager.setBbbCatalogTools(catTools)
		grManager.setRegistryInfoServiceName("regInfo")
		RegistryResVO registryResVO = new RegistryResVO()
		ServiceErrorVO sErroVO = new ServiceErrorVO()
		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grManager.getRegistryInfo({RegistryReqVO rvo -> rvo.registryId=='rId' && rvo.serviceName=='regInfo' && rvo.siteId=='can' && rvo.userToken=='tocken' }) >> registryResVO
		registryResVO.setServiceErrorVO(sErroVO)
		sErroVO.setErrorExists(false)
		sErroVO.setErrorMessage("error")
		
		
		when:
			RegistryVO rv = grManager.getRegistryDetailInfo("rId", "usBed")
		then:
		rv == null
		0*catTools.getRegistryTypeName(_,_)
		1 * grManager.logError('GiftRegistryManager.getRegistryDetailInfo recieved Error in service response for registryidrId Error = null')
	}
	
	def "getRegistryDetailInfo.TC when error vo having error " (){
		given:
		grManager = Spy()
		grManager.setBbbCatalogTools(catTools)
		grManager.setRegistryInfoServiceName("regInfo")
		RegistryResVO registryResVO = new RegistryResVO()
		ServiceErrorVO sErroVO = new ServiceErrorVO()
		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grManager.getRegistryInfo(_) >> registryResVO
		registryResVO.setServiceErrorVO(sErroVO)
		sErroVO.setErrorExists(true)
		//sErroVO.setErrorMessage("error")
		
		
		when:
			RegistryVO rv = grManager.getRegistryDetailInfo("rId", "usBed")
		then:
		rv == null
		0*catTools.getRegistryTypeName(_,_)
		1 * grManager.logError('GiftRegistryManager.getRegistryDetailInfo recieved Error in service response for registryidrId Error = null')
	}
	
	def "getRegistryDetailInfo.TC when registryResVO is null " (){
		given:
		grManager = Spy()
		grManager.setBbbCatalogTools(catTools)
		grManager.setRegistryInfoServiceName("regInfo")
		RegistryResVO registryResVO = new RegistryResVO()
		ServiceErrorVO sErroVO = new ServiceErrorVO()
		
		1*catTools.getAllValuesForKey( "WSDLSiteFlags", "usBed") >> ["can"]
		1*catTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken"]
		1*grManager.getRegistryInfo(_) >> null
		//registryResVO.setServiceErrorVO(sErroVO)
		//sErroVO.setErrorExists(true)
		
		
		when:
			RegistryVO rv = grManager.getRegistryDetailInfo("rId", "usBed")
		then:
		rv == null
		0*catTools.getRegistryTypeName(_,_)
	}
	
	
/*****************************************   getRegistryTypes **************************************/
	
	
	def"getRegistryTypes. TC to get the registry type for perticular site"(){
		given:
			RegistryTypeVO regTypeVo = new RegistryTypeVO()
			
			2*requestMock.getObjectParameter("registryTypes") >> [regTypeVo]
			
		when:
		List<RegistryTypeVO> value  = grManager.getRegistryTypes()
		
		then:
		    value.get(0) == regTypeVo
			1*grTypeDroplet.service(requestMock, responseMock)
			1*requestMock.setParameter("siteId",_)
	}
	
	
	def"getRegistryTypes. TC to get the registry type when regType VO is null gets from grType droplet"(){
		given:
			RegistryTypeVO regTypeVo = new RegistryTypeVO()
			
			1*requestMock.getObjectParameter("registryTypes") >> null
			
		when:
		List<RegistryTypeVO> value  = grManager.getRegistryTypes()
		
		then:
			value == null
			1*grTypeDroplet.service(requestMock, responseMock)
			1*requestMock.setParameter("siteId",_)
	}
	
	def"getRegistryTypes. TC for IOException"(){
		given:
			RegistryTypeVO regTypeVo = new RegistryTypeVO()
			1*grTypeDroplet.service(requestMock, responseMock) >> {throw new IOException("IOexception")}
			
		when:
		List<RegistryTypeVO> value  = grManager.getRegistryTypes()
		
		then:
			BBBSystemException exc = thrown()
			exc.getMessage() == "IOexception"
			1*requestMock.setParameter("siteId",_)
	}
	
	def"getRegistryTypes. TC for ServletException"(){
		given:
			RegistryTypeVO regTypeVo = new RegistryTypeVO()
			1*grTypeDroplet.service(requestMock, responseMock) >> {throw new ServletException("SerException")}
			
		when:
		List<RegistryTypeVO> value  = grManager.getRegistryTypes()
		
		then:
			BBBSystemException exc = thrown()
			exc.getMessage() == "SerException"
			1*requestMock.setParameter("siteId",_)
	}
	
	/************************************* fetchRegistryTypes  ******************************************/
	
	def"fetchRegistryTypes. TC to fetch registry type"(){
		given:
			1*catTools.getRegistryTypes("usBed") >> [regTypeVo]
			regTypeVo.setRegistryTypeId("typeID")
		when:
		
			List<RegistryTypeVO> vo = grManager.fetchRegistryTypes("usBed")
		then:
			vo.get(0).getRegistryTypeId() == "typeID"
	}
	
	/**************************************** getUserRegistryListCount ****************************/
	
	def"getUserRegistryListCount.TC to get the registry list count"(){
		given:
		profileMock.getRepositoryId() >> "user1"
		1*grTools.fetchUserRegistries("usBed","user1", true) >> [reItem]
		
		when:
			int value = grManager.getUserRegistryListCount(profileMock, "usBed")
		then:
		value == 1
	}
	
	def"getUserRegistryListCount.TC to get the registry list count when userRegistry item is null"(){
		given:
		profileMock.getRepositoryId() >> "user1"
		1*grTools.fetchUserRegistries("usBed","user1", true) >> null
		
		when:
			int value = grManager.getUserRegistryListCount(profileMock, "usBed")
		then:
		value == 0
	}
	
	
	/***************************************  getBridalRegistries *********************************/
	
	def "getBridalRegistries. TC to get user registry list for wedding and commitment registry"(){
		given:
		    grManager = Spy()
			grManager.setGiftRegistryTools(grTools)
			grManager.setBbbCatalogTools(catTools)
			
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			RegistrySkinnyVO rsVO1 = new RegistrySkinnyVO()
			
			1*grTools.getFutureRegistryList(profileMock, "BedBathCanada") >> [rsVO, rsVO1]
			
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> ["new"]
			//END
			rsVO.setEventCode("BRD")
			rsVO.setStatus("new")
			rsVO.setEventDate("10/12/2014")
			rsVO.setEventType("eType")
			
			rsVO1.setEventCode("COM")
			rsVO1.setStatus("acitve")
	
			
			1*grManager.getRegistryInfo(*_) >> rSummaryVO
			rSummaryVO.setBridalToolkitToken("tck1")
			
		when:
			 List<BridalRegistryVO> value = grManager.getBridalRegistries( profileMock, "BedBathCanada")
		
		then:
			BridalRegistryVO bridalVO = value.get(0)
			bridalVO.getEventCode() == "BRD"
			bridalVO.getEventDate() == "12/10/2014"
			bridalVO.getEventType() == "eType"
			bridalVO.getBridalToolkitToken() == "tck1"
			value.size() == 1
	}
	
	def "getBridalRegistries. TC to get user registry list for wedding and commitment registry when site is TBS_BedBathCanada "(){
		given:
			grManager = Spy()
			grManager.setGiftRegistryTools(grTools)
			grManager.setBbbCatalogTools(catTools)
			
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			RegistrySkinnyVO rsVO1 = new RegistrySkinnyVO()
			
			1*grTools.getFutureRegistryList(profileMock, "TBS_BedBathCanada") >> [rsVO, rsVO1]
			
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> ["new"]
			//END
			rsVO.setEventCode("BRD")
			rsVO.setStatus("new")
			rsVO.setEventDate("10/12/2014")
			rsVO.setEventType("eType")
			
			rsVO1.setEventCode("COMl")
			rsVO1.setStatus("acitve")
	
			
			1*grManager.getRegistryInfo(*_) >> rSummaryVO
			rSummaryVO.setBridalToolkitToken("tck1")
			
		when:
			 List<BridalRegistryVO> value = grManager.getBridalRegistries( profileMock, "TBS_BedBathCanada")
		
		then:
			BridalRegistryVO bridalVO = value.get(0)
			bridalVO.getEventCode() == "BRD"
			bridalVO.getEventDate() == "12/10/2014"
			bridalVO.getEventType() == "eType"
			bridalVO.getBridalToolkitToken() == "tck1"
			value.size() == 1
	}
	
	def "getBridalRegistries. TC to get user registry list for wedding and commitment registry when site is not TBS_BedBathCanada or BEDBathCanada "(){
		given:
			grManager = Spy()
			grManager.setGiftRegistryTools(grTools)
			grManager.setBbbCatalogTools(catTools)
			
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			
			1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
			
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> ["new"]
			//END
			rsVO.setEventCode("BRD")
			rsVO.setStatus("new")
			rsVO.setEventDate("10/12/2014")
			rsVO.setEventType("eType")
			
			
			1*grManager.getRegistryInfo(*_) >> rSummaryVO
			rSummaryVO.setBridalToolkitToken("tck1")
			
		when:
			 List<BridalRegistryVO> value = grManager.getBridalRegistries( profileMock, "site")
		
		then:
			BridalRegistryVO bridalVO = value.get(0)
			bridalVO.getEventCode() == "BRD"
			bridalVO.getEventDate() == "10/12/2014"
			bridalVO.getEventType() == "eType"
			bridalVO.getBridalToolkitToken() == "tck1"
			value.size() == 1
	}
	
	def "getBridalRegistries. TC when futrue registry list is null "(){
		given:
			grManager = Spy()
			grManager.setGiftRegistryTools(grTools)
			grManager.setBbbCatalogTools(catTools)
			
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			
			1*grTools.getFutureRegistryList(profileMock, "site") >> null
			
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> [""]
			//END
			rsVO.setEventCode("BRD")
			
		when:
			 List<BridalRegistryVO> value = grManager.getBridalRegistries( profileMock, "site")
		
		then:
			value.empty
		
	}
	
	
	/***************************************** fetchUsersBabyRegistries ***********************************/
	
	def"fetchUsersBabyRegistries. tc to getting future baby registry list"(){
		given:
		RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
		
		1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
		//getGiftRegistryConfigurationByKey
		1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> ["new"]
		//END

		rsVO.setEventCode("BA1")
		rsVO.setStatus("ACtiv")
		rsVO.setEventType("eType")

		when:
		 List<RegistrySkinnyVO> value = grManager.fetchUsersBabyRegistries(profileMock, "site")
		then:
		
		value == []
	}
	
	def"fetchUsersBabyRegistries. tc to getting future baby registry list when event code is not BA1"(){
		given:
		RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
		
		1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
		//getGiftRegistryConfigurationByKey
		1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> [""]
		//END

		rsVO.setEventCode("BA5")
		rsVO.setStatus("ACtiv")
		rsVO.setEventType("eType")

		when:
		 List<RegistrySkinnyVO> value = grManager.fetchUsersBabyRegistries(profileMock, "site")
		then:
		
		value == []
	}
	
	def"fetchUsersBabyRegistries. tc to getting future baby registry list when event type is null"(){
		given:
		RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
		rsVO.setAlternatePhone("123456789")
		
		1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
		//getGiftRegistryConfigurationByKey
		1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> ["new"]
		//END

		rsVO.setEventCode("BA5")
		rsVO.setStatus("new")
		rsVO.setEventType(null)

		when:
		 List<RegistrySkinnyVO> value = grManager.fetchUsersBabyRegistries(profileMock, "site")
		then:
		
		value.get(0).getAlternatePhone() == "123456789"
	}
	
	/****************************************  fetchUsersWeddingOrBabyRegistries ***************************/
	
	def "fetchUsersWeddingOrBabyRegistries.TC to get future wedding and baby registry list" (){
		given:
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			
			1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> ["new"]
			//END
	
			rsVO.setEventCode("COM")
			rsVO.setStatus("ACtiv")
			rsVO.setEventType("eType")

		when:
			List<RegistrySkinnyVO> value = grManager.fetchUsersWeddingOrBabyRegistries(profileMock, "site")
		
		then:
		value == []
	}
	
	def "fetchUsersWeddingOrBabyRegistries.TC to get future wedding and baby registry list when event type is baby" (){
		given:
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			
			1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> [""]
			//END
	
			rsVO.setEventCode("BA1")
			rsVO.setStatus("ACtiv")
			rsVO.setEventType("eType")

		when:
			List<RegistrySkinnyVO> value = grManager.fetchUsersWeddingOrBabyRegistries(profileMock, "site")
		
		then:
		value == []
	}
	
	def "fetchUsersWeddingOrBabyRegistries.TC to get future wedding and baby registry list when event type is wedding" (){
		given:
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			rsVO.setAlternatePhone("123456789")
			1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> ["new"]
			//END
	
			rsVO.setEventCode("BRD")
			rsVO.setStatus("new")
			rsVO.setEventType("eType")

		when:
			List<RegistrySkinnyVO> value = grManager.fetchUsersWeddingOrBabyRegistries(profileMock, "site")
		
		then:
		value.get(0).getAlternatePhone() == "123456789"
	}
	
	def "fetchUsersWeddingOrBabyRegistries.TC to get future wedding and baby registry list when event type is null" (){
		given:
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			rsVO.setAlternatePhone("123456789")
			1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> ["new"]
			//END
	
			rsVO.setEventCode("BRD")
			rsVO.setStatus("new")
			rsVO.setEventType(null)

		when:
			List<RegistrySkinnyVO> value = grManager.fetchUsersWeddingOrBabyRegistries(profileMock, "site")
		
		then:
		value.get(0).getAlternatePhone() == "123456789"
	}
	
	def "fetchUsersWeddingOrBabyRegistries.TC to get future wedding and baby registry list when event type is not in (wedding, baby and cermony)" (){
		given:
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			
			1*grTools.getFutureRegistryList(profileMock, "site") >> [rsVO]
			//getGiftRegistryConfigurationByKey
			1*catTools.getAllValuesForKey("GiftRegistryConfig", _) >> [""]
			//END
	
			rsVO.setEventCode("wrong")
			rsVO.setStatus("ACtiv")
			rsVO.setEventType("eType")

		when:
			List<RegistrySkinnyVO> value = grManager.fetchUsersWeddingOrBabyRegistries(profileMock, "site")
		
		then:
		value == []
	}
	
	/***************************************** getDefaultRegistryTypeBySite **********************/
	
	def"getDefaultRegistryTypeBySite. TC to get registry type"(){
		given:
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "BBCanada") >> ["BRD", "BA1"]
		when:
			String rType = grManager.getDefaultRegistryTypeBySite("BBCanada")
		then:
		
		rType.equalsIgnoreCase("BRD")
	}
	
	def"getDefaultRegistryTypeBySite. TC to get registry type when registry type list is empty"(){
		given:
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "BBCanada") >> []
		when:
			String rType = grManager.getDefaultRegistryTypeBySite("BBCanada")
		then:
		
		rType == null
	}
	
	def"getDefaultRegistryTypeBySite. TC to get registry type when registry type list is null"(){
		given:
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "BBCanada") >> null
		when:
			String rType = grManager.getDefaultRegistryTypeBySite("BBCanada")
		then:
		
		rType == null
	}
	
	def"getDefaultRegistryTypeBySite. TC for BBBSystemException"(){
		given:
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "BBCanada") >> {throw new BBBSystemException("exception")}
		when:
			String rType = grManager.getDefaultRegistryTypeBySite("BBCanada")
		then:
		
		rType == null
	}
	
	def"getDefaultRegistryTypeBySite. TC for BBBBusinessException"(){
		given:
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "BBCanada") >> {throw new BBBBusinessException("exception")}
		when:
			String rType = grManager.getDefaultRegistryTypeBySite("BBCanada")
		then:
		
		rType == null
	}
	
	
	/*********************************************** addItemToGiftRegistry **************************************/
	
	def"addItemToGiftRegistry. TC for error while doing adding item to the registry"(){
		given:
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("")
			grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("LWA")
			addItem.setLtlDeliveryPrices("200")
			
		    grViewBean.getSku() >> "13"
			1*catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			ServletUtil.setCurrentUserProfile(profile)
			//ServletUtil.getCurrentUserProfile()
			
			1*profile.getRepositoryId() >> "user1"
			1*profile.getPropertyValue("email") >> "test@gmail.com"
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["false"]
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> ["3"]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> ["5"]
			
			grViewBean.getQuantity() >> "1"
			grViewBean.getItemTypes() >> "type1"
			
			 grViewBean.getItemTypes() >> "itemType"
			 1*grViewBean.getRefNum()  >> "refN"
			 1*grViewBean.getAssemblySelections() >> "aSele"
			 1*grViewBean.getAssemblyPrices() >> "assePri"
			 1*grViewBean.getLtlDeliveryServices() >> "ltlDe"
			 1*grViewBean.getLtlDeliveryPrices() >> "150"
			 1*grViewBean.getPersonlizationCodes()  >> "pc1"
			 1*grViewBean.getPersonlizationCodes() >> "123"
			 1*grViewBean.getCustomizationPrices() >> "101"
			 1*grViewBean.getPersonalizationDescrips() >> "pdec"
			 1*grViewBean.getPersonalizedImageUrls() >> "//http:Image/jpg"
			 1*grViewBean.getPersonalizedImageUrlThumbs() >> "thism"
			 1*grViewBean.getPersonalizedMobImageUrls() >> "//:http/mobil.png"
			 1*grViewBean.getPersonalizedMobImageUrlThumbs() >> "url:htdata"

			 1*grUtilsMock.validateInput(_) >> eStatus
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			addItem.getRegistryId() == "123456"
			1*grViewBean.setItemTypes("LTL")
			1*grViewBean.setLtlDeliveryServices("LWA")
			1*grViewBean.setLtlDeliveryPrices("200")
			1*grViewBean.setLtlDeliveryServices("LW")
			1*grViewBean.setAssemblySelections("Y")
			1*grViewBean.setAssemblyPrices("10.0")
			
			1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorId() == 200
			
			
	}
	
	
	def"addItemToGiftRegistry. TC for error while doing adding item to the registry when max sku length is empty"(){
		given:
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("1234564")
			//grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("LW")
			addItem.setLtlDeliveryPrices("200")
			
			grViewBean.getSku() >> "13"
			//catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			ServletUtil.setCurrentUserProfile(null)
			//ServletUtil.getCurrentUserProfile()
			
		//	profile.getRepositoryId() >> "user1"
		//	profile.getPropertyValue("email") >> "test@gmail.com"
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  []
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> ["9"]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			
			grViewBean.getQuantity() >> "1"
			grViewBean.getItemTypes() >> ""
			
			 grViewBean.getItemTypes() >> "itemType"
			 grViewBean.getRefNum()  >> "refN"
			 grViewBean.getAssemblySelections() >> "aSele"
			 grViewBean.getAssemblyPrices() >> "assePri"
			 grViewBean.getLtlDeliveryServices() >> "ltlDe"
			 grViewBean.getLtlDeliveryPrices() >> "150"
			 grViewBean.getPersonlizationCodes()  >> "pc1"
			 grViewBean.getPersonlizationCodes() >> "123"
			 grViewBean.getCustomizationPrices() >> "101"
			 grViewBean.getPersonalizationDescrips() >> "pdec"
			 grViewBean.getPersonalizedImageUrls() >> "//http:Image/jpg"
			 grViewBean.getPersonalizedImageUrlThumbs() >> "thism"
			 grViewBean.getPersonalizedMobImageUrls() >> "//:http/mobil.png"
			 grViewBean.getPersonalizedMobImageUrlThumbs() >> "url:htdata"

			 1*grUtilsMock.validateInput(_) >> eStatus
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			//addItem.getRegistryId() == "123456"
			1*grViewBean.setItemTypes("LTL")
			1*grViewBean.setLtlDeliveryPrices("200")
			1*grViewBean.setLtlDeliveryServices("LW")
			0*grViewBean.setAssemblySelections("Y")
			0*grViewBean.setAssemblyPrices("10.0")
			
			//1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorId() == 200
			1*grViewBean.setItemTypes("REG")
			
	}
	
	def"addItemToGiftRegistry. TC for error while doing adding item to the registry when min sku length is  empty"(){
		given:
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("1234564")
			//grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("")
			//addItem.setLtlDeliveryPrices("200")
			
			grViewBean.getSku() >> "8"
			//catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			ServletUtil.setCurrentUserProfile(null)
			//ServletUtil.getCurrentUserProfile()
			
		//	profile.getRepositoryId() >> "user1"
		//	profile.getPropertyValue("email") >> "test@gmail.com"
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  {throw new BBBSystemException("exception")}
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			
			grViewBean.getQuantity() >> "1000000"
			grViewBean.getItemTypes() >> ""
			
			 setGRBeanValues(grViewBean)
			 1*grUtilsMock.validateInput(_) >> eStatus
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			//addItem.getRegistryId() == "123456"
			0*grViewBean.setItemTypes("LTL")
		    0*grViewBean.setAssemblyPrices("10.0")
			
			//1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorId() == 200
			1*grViewBean.setItemTypes("REG")
			
	}
	
	def"addItemToGiftRegistry. TC for error exist while validating input"(){
		given:
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("1234564")
			//grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("")
			//addItem.setLtlDeliveryPrices("200")
			
			grViewBean.getSku() >> "8"
			//catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			ServletUtil.setCurrentUserProfile(null)
			//ServletUtil.getCurrentUserProfile()
			
		//	profile.getRepositoryId() >> "user1"
		//	profile.getPropertyValue("email") >> "test@gmail.com"
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  {throw new BBBSystemException("exception")}
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			
			grViewBean.getQuantity() >> "1000000"
			grViewBean.getItemTypes() >> ""
			
			 setGRBeanValues(grViewBean)
			 grUtilsMock.validateInput(_) >> eStatus
			 eStatus.isErrorExists() >> true
			 eStatus.getErrorMessage() >> "displayMessageError"
			 eStatus.getErrorId() >> 200
			 
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			//addItem.getRegistryId() == "123456"
			0*grViewBean.setItemTypes("LTL")
			0*grViewBean.setAssemblyPrices("10.0")
			
			//1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorId() == 200
			errorVO.getErrorMessage() == "displayMessageError"
			1*grViewBean.setItemTypes("REG")
			
	}
	
	
	def"addItemToGiftRegistry. TC for error while doing adding item to the registry when quentity is less then 9999"(){
		given:
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			ValidateAddItemsResVO addItemsResVO = new ValidateAddItemsResVO()
			ServiceErrorVO sErrorVO = new ServiceErrorVO()  
			
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("1234564")
			//grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("")
			//addItem.setLtlDeliveryPrices("200")
			
			grViewBean.getSku() >> "8"
			//catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			ServletUtil.setCurrentUserProfile(null)
			//ServletUtil.getCurrentUserProfile()
			
		//	profile.getRepositoryId() >> "user1"
		//	profile.getPropertyValue("email") >> "test@gmail.com"
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  {throw new BBBBusinessException("exception")}
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			
			grViewBean.getQuantity() >> "123"
			grViewBean.getItemTypes() >> ""
			
			setGRBeanValues(grViewBean)
			
			 grUtilsMock.validateInput(_) >> eStatus
			 
			 
			 1*grTools.addItemToGiftRegistry(grViewBean) >> addItemsResVO 
			 addItemsResVO.setServiceErrorVO(sErrorVO)
			 sErrorVO.setErrorExists(false)
			 1*bexManagerMock.getKatoriAvailability() >> "true"
			 grViewBean.getRefNum() >> "ref1"
			 
			 1*bexManagerMock.invokeLockAPI(_, "refN") >> "success"
			 1*grTools.removePersonalizedSkuFromSession(_)
			 
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			//addItem.getRegistryId() == "123456"
			0*grViewBean.setItemTypes("LTL")
			0*grViewBean.setAssemblyPrices("10.0")
			
			//1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			!errorVO.isErrorExists()
			1*grViewBean.setItemTypes("REG")
			
			value.getCount() == 14
			
	}
	
	
	def"addItemToGiftRegistry. TC for Exception while adding item to gift registry "(){
		given:
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			ValidateAddItemsResVO addItemsResVO = new ValidateAddItemsResVO()
			ServiceErrorVO sErrorVO = new ServiceErrorVO()
			
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			AddItemsBean addItem1 = new AddItemsBean()
			AddItemsBean addItem2 = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem, addItem1,addItem2]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("1234564")
			
			addItem1.setProductId("prd1")
			addItem1.setSku("13")
			addItem1.setRegistryId("1234564")
			
			addItem2.setProductId("prd1")
			addItem2.setSku("14")
			addItem2.setRegistryId("1234564")


			//grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("")
			addItem1.setLtlDeliveryServices("")
			addItem2.setLtlDeliveryServices("")
			//addItem.setLtlDeliveryPrices("200")
			
			grViewBean.getSku() >> "8"
			//catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			addItem1.setQuantity("15")
			addItem2.setQuantity("1")
			ServletUtil.setCurrentUserProfile(null)
			//ServletUtil.getCurrentUserProfile()
			
		//	profile.getRepositoryId() >> "user1"
		//	profile.getPropertyValue("email") >> "test@gmail.com"
			catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  {throw new BBBBusinessException("exception")}
			
			catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			
			grViewBean.getQuantity() >> "123"
			grViewBean.getItemTypes() >> ""
			
			grViewBean.getItemTypes() >> "itemType"
			
			grViewBean.getAssemblySelections() >> "aSele"
			grViewBean.getAssemblyPrices() >> "assePri"
			grViewBean.getLtlDeliveryServices() >> "ltlDe"
			grViewBean.getLtlDeliveryPrices() >> "150"
			grViewBean.getPersonlizationCodes()  >> "pc1"
			grViewBean.getPersonlizationCodes() >> "123"
			grViewBean.getCustomizationPrices() >> "101"
			grViewBean.getPersonalizationDescrips() >> "pdec"
			grViewBean.getPersonalizedImageUrls() >> "//http:Image/jpg"
			grViewBean.getPersonalizedImageUrlThumbs() >> "thism"
			grViewBean.getPersonalizedMobImageUrls() >> "//:http/mobil.png"
			grViewBean.getPersonalizedMobImageUrlThumbs() >> "url:htdata"

			
			 grUtilsMock.validateInput(_) >> eStatus
			 
			 
			 grTools.addItemToGiftRegistry(grViewBean) >> {throw new Exception("exception")}
			 
			 grUtilsMock.logAndFormatError(*_) >> sErrorVO
			 
			 
			 //addItemsResVO.setServiceErrorVO(sErrorVO)
			 sErrorVO.setErrorExists(false)
			 3*bexManagerMock.getKatoriAvailability() >> "true" >> "true" >> "false"
			 grViewBean.getRefNum()  >> "refN"
			 
			 bexManagerMock.invokeLockAPI(_, "refN") >> "failed" >> {throw new Exception("exception")}
			 3*grTools.removePersonalizedSkuFromSession(_)
			 
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			3*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			//addItem.getRegistryId() == "123456"
			0*grViewBean.setItemTypes("LTL")
			0*grViewBean.setAssemblyPrices("10.0")
			
			//1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			!errorVO.isErrorExists()
			3*grViewBean.setItemTypes("REG")
			
			value.getCount() == 30
			
	}
	
	def"addItemToGiftRegistry. TC when ref num is null "(){
		given:
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			ValidateAddItemsResVO addItemsResVO = new ValidateAddItemsResVO()
			ServiceErrorVO sErrorVO = Mock()
			
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			AddItemsBean addItem1 = new AddItemsBean()
			AddItemsBean addItem2 = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem, addItem1,addItem2]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("1234564")
			
			addItem1.setProductId("prd1")
			addItem1.setSku("13")
			addItem1.setRegistryId("1234564")
			
			addItem2.setProductId("prd1")
			addItem2.setSku("14")
			addItem2.setRegistryId("1234564")


			
			addItem.setLtlDeliveryServices("")
			addItem1.setLtlDeliveryServices("")
			addItem2.setLtlDeliveryServices("")
			
			grViewBean.getSku() >> "8"
			
			addItem.setQuantity("14")
			addItem1.setQuantity("15")
			addItem2.setQuantity("1")
			ServletUtil.setCurrentUserProfile(null)
			3*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  {throw new BBBBusinessException("exception")}
			
			3*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			3*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			
			grViewBean.getQuantity() >> "123"
			grViewBean.getItemTypes() >> ""
			
			grViewBean.getItemTypes() >> "itemType"
			
			grViewBean.getAssemblySelections() >> "aSele"
			grViewBean.getAssemblyPrices() >> "assePri"
			grViewBean.getLtlDeliveryServices() >> "ltlDe"
			grViewBean.getLtlDeliveryPrices() >> "150"
			grViewBean.getPersonlizationCodes()  >> "pc1"
			grViewBean.getPersonlizationCodes() >> "123"
			grViewBean.getCustomizationPrices() >> "101"
			grViewBean.getPersonalizationDescrips() >> "pdec"
			grViewBean.getPersonalizedImageUrls() >> "//http:Image/jpg"
			grViewBean.getPersonalizedImageUrlThumbs() >> "thism"
			grViewBean.getPersonalizedMobImageUrls() >> "//:http/mobil.png"
			grViewBean.getPersonalizedMobImageUrlThumbs() >> "url:htdata"

			
			 grUtilsMock.validateInput(_) >> eStatus
			 
			 
			 grTools.addItemToGiftRegistry(grViewBean) >> {throw new Exception("exception")}
			 
			 grUtilsMock.logAndFormatError(*_) >> sErrorVO
			 
			 
			 3*sErrorVO.isErrorExists() >> false >> false>> true 
			 bexManagerMock.getKatoriAvailability() >> "true" 
			 6*grViewBean.getRefNum()  >> "" >> "" >> "" >> null >> null
			 
			// bexManagerMock.invokeLockAPI(_, "refN") >> "failed" >> {throw new Exception("exception")}
			 grTools.removePersonalizedSkuFromSession(_)
			 
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			3*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			//addItem.getRegistryId() == "123456"
			0*grViewBean.setItemTypes("LTL")
			0*grViewBean.setAssemblyPrices("10.0")
			
			//1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			3*grViewBean.setItemTypes("REG")
			
			value.getCount() == 29
			
	}
	
	
	def"addItemToGiftRegistry. TC when registry item WScall flag is true"(){
		given:
		    grManager = Spy()
			grManager.setCatalogTools(catTools)
			grManager.setGiftRegUtils(grUtilsMock)
			
			ServiceErrorVO sErrorVO = new ServiceErrorVO() 
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			ValidateAddItemsResVO resVO = new ValidateAddItemsResVO()
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("")
			grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("LWA")
			addItem.setLtlDeliveryPrices("200")
			
			grViewBean.getSku() >> "13"
			1*catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			ServletUtil.setCurrentUserProfile(profile)
			//ServletUtil.getCurrentUserProfile()
			
			profile.getRepositoryId() >> "user1"
			profile.getPropertyValue("email") >> "test@gmail.com"
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["true"]
			
			1*grManager.invokeServiceHandler(grViewBean) >> resVO
			resVO.setServiceErrorVO(sErrorVO)
			sErrorVO.setErrorExists(true) 
			sErrorVO.setErrorDisplayMessage("errorMessage")  
			 grUtilsMock.validateInput(_) >> eStatus
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			addItem.getRegistryId() == "123456"
			1*grViewBean.setItemTypes("LTL")
			1*grViewBean.setLtlDeliveryServices("LWA")
			1*grViewBean.setLtlDeliveryPrices("200")
			1*grViewBean.setLtlDeliveryServices("LW")
			1*grViewBean.setAssemblySelections("Y")
			1*grViewBean.setAssemblyPrices("10.0")
			
			1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorDisplayMessage() == "errorMessage"
			
			
	}
	
	def"addItemToGiftRegistry. TC when registry item WScall flag is true and error display message is null"(){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			grManager.setGiftRegUtils(grUtilsMock)
			
			ServiceErrorVO sErrorVO = new ServiceErrorVO()
			RepositoryItem profile = Mock()
			ErrorStatus eStatus = Mock()
			ValidateAddItemsResVO resVO = new ValidateAddItemsResVO()
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("")
			grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("LWA")
			addItem.setLtlDeliveryPrices("200")
			
			grViewBean.getSku() >> "13"
			1*catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			ServletUtil.setCurrentUserProfile(profile)
			//ServletUtil.getCurrentUserProfile()
			
			profile.getRepositoryId() >> "user1"
			profile.getPropertyValue("email") >> "test@gmail.com"
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["true"]
			
			1*grManager.invokeServiceHandler(grViewBean) >> resVO
			resVO.setServiceErrorVO(sErrorVO)
			sErrorVO.setErrorExists(true)
			sErrorVO.setErrorDisplayMessage("")
			 grUtilsMock.validateInput(_) >> eStatus
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			addItem.getRegistryId() == "123456"
			1*grViewBean.setItemTypes("LTL")
			1*grViewBean.setLtlDeliveryServices("LWA")
			1*grViewBean.setLtlDeliveryPrices("200")
			1*grViewBean.setLtlDeliveryServices("LW")
			1*grViewBean.setAssemblySelections("Y")
			1*grViewBean.setAssemblyPrices("10.0")
			
			1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorDisplayMessage() == ""
			
			
	}
	
	def"addItemToGiftRegistry. TC when registry item WScall flag is true and no error exist"(){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			grManager.setGiftRegUtils(grUtilsMock)
			grManager.setBbbEximPricingManager(bexManagerMock)
			grManager.setGiftRegistryTools(grTools)
			
			ServiceErrorVO sErrorVO = new ServiceErrorVO()
			RepositoryItem profile = Mock()
			//ErrorStatus eStatus = Mock()
			ValidateAddItemsResVO resVO = Mock()
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("")
			grViewBean.getRegistryId() >> "123456"
			
			addItem.setLtlDeliveryServices("LWA")
			addItem.setLtlDeliveryPrices("200")
			
			grViewBean.getSku() >> "13"
			catTools.getAssemblyCharge(_, "13") >> 10
			
			addItem.setQuantity("14")
			ServletUtil.setCurrentUserProfile(profile)
			//ServletUtil.getCurrentUserProfile()
			
			profile.getRepositoryId() >> "user1"
			profile.getPropertyValue("email") >> "test@gmail.com"
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["true"]
			
			1*grManager.invokeServiceHandler(grViewBean) >> resVO
			4*resVO.getServiceErrorVO() >> sErrorVO
			sErrorVO.setErrorExists(false)
			//sErrorVO.setErrorDisplayMessage("")
			 //grUtilsMock.validateInput(_) >> eStatus
			 
			 1*bexManagerMock.getKatoriAvailability() >> "true"
			 1*grTools.removePersonalizedSkuFromSession(_)
			 
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			addItem.getRegistryId() == "123456"
			1*grViewBean.setItemTypes("LTL")
			1*grViewBean.setLtlDeliveryServices("LWA")
			1*grViewBean.setLtlDeliveryPrices("200")
			1*grViewBean.setLtlDeliveryServices("LW")
			1*grViewBean.setAssemblySelections("Y")
			1*grViewBean.setAssemblyPrices("10.0")
			
			1*grViewBean.setRegistryId("123456")
			1*grViewBean.setQuantity("14")

			ServiceErrorVO errorVO = value.getServiceErrorVO()
			!errorVO.isErrorExists()
			
			
	}
	
	def"addItemToGiftRegistry. TC when registry id is not valid "(){
		given:
			
			ServiceErrorVO sErrorVO = new ServiceErrorVO()
			RepositoryItem profile = Mock()
			//ErrorStatus eStatus = Mock()
			ValidateAddItemsResVO resVO = Mock()
			GiftRegistryViewBean grViewBean = Mock()
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			addItem.setProductId("prd1")
			addItem.setSku("12")
			addItem.setRegistryId("")
			grViewBean.getRegistryId() >> "fdlfk%&^"
			
		 
		when:
			ValidateAddItemsResVO value = grManager.addItemToGiftRegistry(grViewBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("12")
			addItem.getRegistryId() == "fdlfk%&^"
			0*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall")
			
	}
	
	private setGRBeanValues(GiftRegistryViewBean grViewBean){
		grViewBean.getItemTypes() >> "itemType"
		grViewBean.getRefNum()  >> "refN"
		grViewBean.getAssemblySelections() >> "aSele"
		grViewBean.getAssemblyPrices() >> "assePri"
		grViewBean.getLtlDeliveryServices() >> "ltlDe"
		grViewBean.getLtlDeliveryPrices() >> "150"
		grViewBean.getPersonlizationCodes()  >> "pc1"
		grViewBean.getPersonlizationCodes() >> "123"
		grViewBean.getCustomizationPrices() >> "101"
		grViewBean.getPersonalizationDescrips() >> "pdec"
		grViewBean.getPersonalizedImageUrls() >> "//http:Image/jpg"
		grViewBean.getPersonalizedImageUrlThumbs() >> "thism"
		grViewBean.getPersonalizedMobImageUrls() >> "//:http/mobil.png"
		grViewBean.getPersonalizedMobImageUrlThumbs() >> "url:htdata"

	}
	
	
	/*********************************************** addBulkItemsToGiftRegistry *******************************************/
	
	def "addBulkItemsToGiftRegistry . TC to get error  while getting bulk item" (){
		given:
			BBBSessionBean sessionBean = new BBBSessionBean()
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("1")
			addItem.setRegistryId("")
			addItem.setQuantity("14")

			ServletUtil.setCurrentUserProfile(profile)
			
			1*profile.getRepositoryId() >> "user1"
			1*profile.getPropertyValue("email") >> "test@gmail.com"
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["false"]
			1*grUtilsMock.validateInput(_) >> eStatus
			eStatus.isErrorExists() >> false
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> ["3"]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> ["5"]

			
		when:
		 	
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			2*grViewBean.setProductId("prd1")
			2*grViewBean.setSku("1")
			2*grViewBean.setQuantity("14")
			1*grViewBean.setItemTypes("REG")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorId() == 200

	}
	
	def "addBulkItemsToGiftRegistry . TC to get error  while getting bulk item when sku in item bean is more the max value" (){
		given:
			BBBSessionBean sessionBean = new BBBSessionBean()
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("10")
			addItem.setRegistryId("")
			addItem.setQuantity("14")

			ServletUtil.setCurrentUserProfile(profile)
			
			1*profile.getRepositoryId() >> "user1"
			1*profile.getPropertyValue("email") >> "test@gmail.com"
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  {throw new BBBSystemException("exception")}
			1*grUtilsMock.validateInput(_) >> eStatus
			eStatus.isErrorExists() >> false
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> ["3"]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			grViewBean.getItemTypes() >> "itemType"
			
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			2*grViewBean.setProductId("prd1")
			2*grViewBean.setSku("10")
			2*grViewBean.setQuantity("14")
			0*grViewBean.setItemTypes("REG")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorId() == 200

	}
	
	def "addBulkItemsToGiftRegistry . TC to get error  while getting bulk item when item quantity is more then 9999" (){
		given:
			BBBSessionBean sessionBean = new BBBSessionBean()
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setRegistryId("")
			addItem.setQuantity("10000")

			ServletUtil.setCurrentUserProfile(null)
			
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  {throw new BBBBusinessException("exception")}
			1*grUtilsMock.validateInput(_) >> eStatus
			eStatus.isErrorExists() >> false
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			grViewBean.getItemTypes() >> "itemType"
			
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			2*grViewBean.setProductId("prd1")
			2*grViewBean.setSku("8")
			2*grViewBean.setQuantity("10000")
			0*grViewBean.setItemTypes("REG")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorId() == 200

	}
	
	def "addBulkItemsToGiftRegistry . TC to get error  while validating inputs" (){
		given:
			BBBSessionBean sessionBean = new BBBSessionBean()
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setRegistryId("")
			addItem.setQuantity("10000")

			ServletUtil.setCurrentUserProfile(null)
			
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  {throw new BBBBusinessException("exception")}
			1*grUtilsMock.validateInput(_) >> eStatus
			eStatus.isErrorExists() >> true
			eStatus.getErrorMessage() >> "error in display"
			
/*			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			grViewBean.getItemTypes() >> "itemType"
*/			
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("8")
			1*grViewBean.setQuantity("10000")
			0*grViewBean.setItemTypes("REG")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			//errorVO.getErrorId() == 200
			errorVO.getErrorMessage() == "error in display"
			0*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH)
	}
	
	def "addBulkItemsToGiftRegistry . TC to get item response VO for no error" (){
		given:
			ValidateAddItemsResVO addItemsResVO = new ValidateAddItemsResVO() 
			BBBSessionBean sessionBean = new BBBSessionBean()
			ServiceErrorVO sErrorVO = Mock()
			ServiceErrorVO sErrorVO1 = Mock()
			
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			AddItemsBean addItem1 = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem, addItem1]
			
			addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setRegistryId("")
			addItem.setQuantity("10")
			
			addItem1.setProductId("prd1")
			addItem1.setSku("8")
			addItem1.setRegistryId("")
			addItem1.setQuantity("10")


			ServletUtil.setCurrentUserProfile(null)
			
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  []
			1*grUtilsMock.validateInput(_) >> eStatus
			eStatus.isErrorExists() >> false
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			grViewBean.getItemTypes() >> "itemType"
			
			2*grTools.addItemToGiftRegistry(grViewBean) >> addItemsResVO >> {throw new Exception("exception")}
			addItemsResVO.getServiceErrorVO() >> sErrorVO
			sErrorVO.isErrorExists() >> false
			sessionBean.setEventType("seseType")
			
			grUtilsMock.logAndFormatError("AddItemsToGiftRegistry2",*_) >> sErrorVO
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			2*grViewBean.setProductId("prd1")
			2*grViewBean.setSku("8")
			1*grViewBean.setQuantity("10,10")
			2*grViewBean.setQuantity("10")
			0*grViewBean.setItemTypes("REG")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			!errorVO.isErrorExists()
			
			1*grViewBean.setTotQuantity(20)
			1*grViewBean.setRegistryName("seseType")
			value.getCount() == 20

	}
	
	
	def "addBulkItemsToGiftRegistry . TC when error exist while getting while adding item in gift registry" (){
		given:
			ValidateAddItemsResVO addItemsResVO = Mock()
			BBBSessionBean sessionBean = new BBBSessionBean()
			ServiceErrorVO sErrorVO = Mock()
			ServiceErrorVO sErrorVO1 = Mock()
			
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			AddItemsBean addItem1 = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setRegistryId("")
			addItem.setQuantity("10")
			


			ServletUtil.setCurrentUserProfile(null)
			
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  []
			1*grUtilsMock.validateInput(_) >> eStatus
			eStatus.isErrorExists() >> false
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MIN_SKU_LENGTH) >> [""]
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.MAX_SKU_LENGTH) >> [""]
			grViewBean.getItemTypes() >> "itemType"
			
			1*grTools.addItemToGiftRegistry(grViewBean) >> addItemsResVO 
			3*addItemsResVO.getServiceErrorVO() >> sErrorVO1 >> sErrorVO1 >> null 
			1*sErrorVO1.isErrorExists() >> true
			sessionBean.setEventType("seseType")
			
			//grUtilsMock.logAndFormatError("AddItemsToGiftRegistry2",*_) >> sErrorVO
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			2*grViewBean.setProductId("prd1")
			2*grViewBean.setSku("8")
			//1*grViewBean.setQuantity("10,10")
			2*grViewBean.setQuantity("10")
			0*grViewBean.setItemTypes("REG")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			
			0*grViewBean.setTotQuantity(20)
			0*grViewBean.setRegistryName("seseType")
			//value.getCount() == 20

	}
	

	
	
	def "addBulkItemsToGiftRegistry . TC when regItemsWSCall flag is true " (){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			ValidateAddItemsResVO addItemsResVO = Mock()
			BBBSessionBean sessionBean = new BBBSessionBean()
			ServiceErrorVO sErrorVO = Mock()
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
		    addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setQuantity("10")
			

			ServletUtil.setCurrentUserProfile(null)
			
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["true"]
			
			grManager.invokeServiceHandler(grViewBean) >> addItemsResVO
			6*addItemsResVO.getServiceErrorVO() >> sErrorVO
			sErrorVO.isErrorExists() >> true
			sErrorVO.getErrorDisplayMessage() >> "errorDisplayMsg"
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("8")
			//1*grViewBean.setQuantity("10,10")
			1*grViewBean.setQuantity("10")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorDisplayMessage() == "errorDisplayMsg"

	}
	
	def "addBulkItemsToGiftRegistry . TC when regItemsWSCall flag is true and error display message is empty " (){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			ValidateAddItemsResVO addItemsResVO = Mock()
			BBBSessionBean sessionBean = new BBBSessionBean()
			ServiceErrorVO sErrorVO = Mock()
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setQuantity("10")
			

			ServletUtil.setCurrentUserProfile(null)
			
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["true"]
			
			grManager.invokeServiceHandler(grViewBean) >> addItemsResVO
			addItemsResVO.getServiceErrorVO() >> sErrorVO
			sErrorVO.isErrorExists() >> true
			sErrorVO.getErrorDisplayMessage() >> ""
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("8")
			//1*grViewBean.setQuantity("10,10")
			1*grViewBean.setQuantity("10")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO.isErrorExists()
			errorVO.getErrorDisplayMessage() == ""

	}
	
	def "addBulkItemsToGiftRegistry . TC when regItemsWSCall flag is true and no error " (){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			ValidateAddItemsResVO addItemsResVO = Mock()
			BBBSessionBean sessionBean = new BBBSessionBean()
			ServiceErrorVO sErrorVO = Mock()
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setQuantity("10")
			

			ServletUtil.setCurrentUserProfile(null)
			
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["true"]
			
			grManager.invokeServiceHandler(grViewBean) >> addItemsResVO
			addItemsResVO.getServiceErrorVO() >> sErrorVO
			sErrorVO.isErrorExists() >> false
			sErrorVO.getErrorDisplayMessage() >> ""
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("8")
			//1*grViewBean.setQuantity("10,10")
			1*grViewBean.setQuantity("10")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			!errorVO.isErrorExists()
			

	}
	
	
	def "addBulkItemsToGiftRegistry . TC when regItemsWSCall flag is true and Service error VO is null" (){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			ValidateAddItemsResVO addItemsResVO = Mock()
			BBBSessionBean sessionBean = new BBBSessionBean()
			ServiceErrorVO sErrorVO = Mock()
			grViewBean.getRegistryId() >> "123456"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setQuantity("10")
			

			ServletUtil.setCurrentUserProfile(null)
			
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >>  ["true"]
			
			grManager.invokeServiceHandler(grViewBean) >> addItemsResVO
			addItemsResVO.getServiceErrorVO() >> null
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("8")
			//1*grViewBean.setQuantity("10,10")
			1*grViewBean.setQuantity("10")
			ServiceErrorVO errorVO = value.getServiceErrorVO()
			errorVO == null
			

	}
	
	def "addBulkItemsToGiftRegistry . TC when registry id is not valid" (){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			ValidateAddItemsResVO addItemsResVO = Mock()
			BBBSessionBean sessionBean = new BBBSessionBean()
			ServiceErrorVO sErrorVO = Mock()
			grViewBean.getRegistryId() >> "df&&&&"
			AddItemsBean addItem = new AddItemsBean()
			grViewBean.getAdditem() >> [addItem]
			
			addItem.setProductId("prd1")
			addItem.setSku("8")
			addItem.setQuantity("10")
			

			ServletUtil.setCurrentUserProfile(null)
			
			
		when:
			 
			ValidateAddItemsResVO value = grManager.addBulkItemsToGiftRegistry(grViewBean, sessionBean)
		then:
			1*grViewBean.setProductId("prd1")
			1*grViewBean.setSku("8")
			value == null
			0*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall")
			

	}
	
	
	/********************************************* createRegistry **************************************************/
	
	def"createRegistry. TC to create registry"(){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			grManager.setGiftRegUtils(grUtilsMock)
			grManager.setGiftRegistryTools(grTools)
			
			RegistryResVO createRegistryResVO = new RegistryResVO()
			ErrorStatus errorStatus = new ErrorStatus() 
			RegistryHeaderVO registryHeaderVO = new RegistryHeaderVO() 
			RegistryBabyVO registryBabyVO = new RegistryBabyVO() 
			ShippingVO shippingVO = new ShippingVO()
			AddressVO addressVO = new AddressVO()
			RegistryPrefStoreVO registryPrefStoreVO = new RegistryPrefStoreVO()
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.REGISTRY_WS_CALL) >> ["false"]
			//grTools.createRegistry(registryVO) >> createRegistryResVO
			1*grManager.validateInputForInvalidCharectars(registryVO) >> errorStatus
			
			errorStatus.isErrorExists() >> false
			1*grManager.populateRegHeaderVO(registryVO) >> registryHeaderVO
			1*grManager.populateRegBabyVO(registryVO) >> registryBabyVO
			
			1*grManager.populateRegNamesVO(registryVO, _, BBBGiftRegistryConstants.REG_SUB_TYPE, _)
			grUtilsMock.isCoRegistrantEmpty(registryVO) >> false
			1*grManager.populateRegNamesVO(registryVO, _, BBBGiftRegistryConstants.COREG_SUB_TYPE, _)
			
			registryVO.setShipping(shippingVO)
			shippingVO.setShippingAddress(addressVO)
			shippingVO.setFutureshippingAddress(addressVO)
			
			1*grUtilsMock.isFutureShippingEmpty(shippingVO) >> false
			
			1*grManager.populateShippingVO(registryVO, addressVO, BBBGiftRegistryConstants.SH, _) 
			1*grManager.populateShippingVO(registryVO, addressVO, BBBGiftRegistryConstants.FU, _) 
			
			1*grManager.populateRegPrefStoreVO(registryVO) >> registryPrefStoreVO
			
			1*grManager.validateInputForCreateOrUpdateRegistry(registryVO, registryHeaderVO,_, _, registryBabyVO, registryPrefStoreVO ) >> true
			
			1*grTools.createRegistry(*_) >> createRegistryResVO
			createRegistryResVO.setRegistryId(5461)
			
		when:
			RegistryResVO result = grManager.createRegistry(registryVO)
		then:
			result.getRegistryId() == 5461
				}
	
	
	def"createRegistry. TC to create registry , Gets error when validating the input "(){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			grManager.setGiftRegUtils(grUtilsMock)
			grManager.setGiftRegistryTools(grTools)
			
			RegistryResVO createRegistryResVO = new RegistryResVO()
			ErrorStatus errorStatus = new ErrorStatus()
			RegistryHeaderVO registryHeaderVO = new RegistryHeaderVO()
			RegistryBabyVO registryBabyVO = new RegistryBabyVO()
			ShippingVO shippingVO = new ShippingVO()
			AddressVO addressVO = new AddressVO()
			RegistryPrefStoreVO registryPrefStoreVO = new RegistryPrefStoreVO()
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.REGISTRY_WS_CALL) >> []
			1*grManager.validateInputForInvalidCharectars(registryVO) >> errorStatus
			
			errorStatus.isErrorExists() >> false
			1*grManager.populateRegHeaderVO(registryVO) >> registryHeaderVO
			1*grManager.populateRegBabyVO(registryVO) >> registryBabyVO
			
			1*grManager.populateRegNamesVO(registryVO, _, BBBGiftRegistryConstants.REG_SUB_TYPE, _)
			grUtilsMock.isCoRegistrantEmpty(registryVO) >> true
			
			registryVO.setShipping(shippingVO)
			shippingVO.setShippingAddress(null)
			shippingVO.setFutureshippingAddress(addressVO)
			
			1*grUtilsMock.isFutureShippingEmpty(shippingVO) >> true
			
				
			1*grManager.populateRegPrefStoreVO(registryVO) >> registryPrefStoreVO
			
			1*grManager.validateInputForCreateOrUpdateRegistry(registryVO, registryHeaderVO,_, _, registryBabyVO, registryPrefStoreVO ) >> false
			
			
		when:
			RegistryResVO result = grManager.createRegistry(registryVO)
		then:
			 ServiceErrorVO errorVO =  result.getServiceErrorVO()
			 errorVO.isErrorExists()
			 errorVO.getErrorId() == 200
			 
			 0*grManager.populateShippingVO(registryVO, addressVO, BBBGiftRegistryConstants.SH, _)
			 
				}
	
	def"createRegistry. TC for Exception while Getting  validating the input "(){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			grManager.setGiftRegUtils(grUtilsMock)
			grManager.setGiftRegistryTools(grTools)
			RegistrantVO primaryRegistrant = new RegistrantVO()
			ServiceErrorVO  errorVO1 = new ServiceErrorVO()
			
			//RegistryResVO createRegistryResVO = new RegistryResVO()
			ErrorStatus errorStatus = new ErrorStatus()
			RegistryHeaderVO registryHeaderVO = new RegistryHeaderVO()
			RegistryBabyVO registryBabyVO = new RegistryBabyVO()
			ShippingVO shippingVO = new ShippingVO()
			AddressVO addressVO = new AddressVO()
			RegistryPrefStoreVO registryPrefStoreVO = new RegistryPrefStoreVO()
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.REGISTRY_WS_CALL) >> [""]
			1*grManager.validateInputForInvalidCharectars(registryVO) >> errorStatus
			
			errorStatus.isErrorExists() >> false
			1*grManager.populateRegHeaderVO(registryVO) >> registryHeaderVO
			1*grManager.populateRegBabyVO(registryVO) >> registryBabyVO
			
			1*grManager.populateRegNamesVO(_, _, BBBGiftRegistryConstants.REG_SUB_TYPE, _)
			grUtilsMock.isCoRegistrantEmpty(registryVO) >> true
			registryVO.setShipping(null) >> null
			
				
			1*grManager.populateRegPrefStoreVO(registryVO) >> registryPrefStoreVO
			
			1*grManager.validateInputForCreateOrUpdateRegistry(registryVO, registryHeaderVO,_, _, registryBabyVO, registryPrefStoreVO ) >> {throw new Exception("exception")}
			
			1*grUtilsMock.logAndFormatError("CreateRegistry2",*_) >> errorVO1
			
		when:
			RegistryResVO result = grManager.createRegistry(registryVO)
		then:
			 ServiceErrorVO errorVO =  result.getServiceErrorVO()
			 errorVO != null
			 0*grManager.populateShippingVO(registryVO, addressVO, BBBGiftRegistryConstants.SH, _)
			 
				}
	
	def"createRegistry. TC for error while validating input "(){
		given:
			grManager = Spy()
			grManager.setCatalogTools(catTools)
			grManager.setGiftRegUtils(grUtilsMock)
			grManager.setGiftRegistryTools(grTools)
			RegistrantVO primaryRegistrant = new RegistrantVO()
			ServiceErrorVO  errorVO1 = new ServiceErrorVO()
			
			ErrorStatus errorStatus = Mock()
		
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.REGISTRY_WS_CALL) >> [""]
			1*grManager.validateInputForInvalidCharectars(registryVO) >> errorStatus
			
			errorStatus.isErrorExists() >> true
			errorStatus.getErrorId() >> 100
			
		when:
			RegistryResVO result = grManager.createRegistry(registryVO)
		then:
			 ServiceErrorVO errorVO =  result.getServiceErrorVO()
			 errorVO.isErrorExists()
			 errorVO.getErrorId() == 100
			
			 0*grManager.populateShippingVO(registryVO, _, BBBGiftRegistryConstants.SH, _)
			 
				}
	
	def"createRegistry. TC when regItemsWSCall flage is true "(){
		given:
			RegistryResVO createRegistryResVO = new RegistryResVO()
			
			ErrorStatus errorStatus = Mock()
		
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.REGISTRY_WS_CALL) >> ["true"]
			1*grTools.createRegistry(registryVO) >> createRegistryResVO
			createRegistryResVO.setRegistryId(1234578)
		when:
			RegistryResVO result = grManager.createRegistry(registryVO)
		then:
		result.getRegistryId()== 1234578
		
				}
	
	
	/***************************************** searchRegistries *********************************************/
	
	def"searchRegistries.Tc to Search registry for user"(){
		given:
		List summeryVos = [rsVO]
		RegistrySearchVO registrySearchVO = new RegistrySearchVO() 
		RegSearchResVO regSearchResVO = new RegSearchResVO() 
		
		1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,"RegItemsWSCall") >> ["true"]
		1*grTools.searchRegistries(registrySearchVO, "CA") >> regSearchResVO
		
		1*grTools.changedRegistryTypeName(regSearchResVO, "CA")  >> regSearchResVO
		
		regSearchResVO.setListRegistrySummaryVO(summeryVos)
		rsVO.setFavStoreId("124547")
		
		when:
		
		RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
		
		result.getListRegistrySummaryVO().get(0).getFavStoreId() == "124547"
	}
	
	def"searchRegistries.Tc to Search registry when service name is regSearch "(){
		given:
		    grManager = Spy()
			grManager.setCatalogTools(catTools)
			grManager.setGiftRegUtils(grUtilsMock)
			grManager.setGiftRegistryTools(grTools)
			
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			registrySearchVO.setGiftGiver(true) 
			registrySearchVO.setRegistryId("321")
			1*grUtilsMock.isNumeric("321") >> true
			1*grTools.getRegistryListByRegNum(registrySearchVO) >> regSearchResVO1
			
			//End
			regSearchResVO1.setServiceErrorVO(sErrorVO)
			sErrorVO.isErrorExists() >> true
		    1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,"RegItemsWSCall") >> []
			
			regSearchResVO1.setListRegistrySummaryVO(summeryVos)
			rsVO.setFavStoreId("1245")
		
		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			0*grTools.searchRegistries(registrySearchVO, "CA")
			result.getListRegistrySummaryVO().get(0).getFavStoreId() == "1245"
			0*grTools.changedRegistryTypeName(regSearchResVO, "CA")
	}
	
	def"searchRegistries.Tc to Search registry when service name is getRegistryInfoByProfileId "(){
		given:
			
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("getRegistryInfoByProfileId")
			//searchRegistriesbyProfileId
			registrySearchVO.setProfileId(profileMock)
			profileMock.getRepositoryId() >> "user1"
			1*grUtilsMock.validateInput(['user1']) >> eStatus
			eStatus.isErrorExists() >> false
			grTools.searchRegistriesByProfileId(registrySearchVO) >> regSearchResVO1
			

			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,"RegItemsWSCall") >> null
			
			1*grTools.changedRegistryTypeName(_, "CA")  >> regSearchResVO
	
			regSearchResVO.setListRegistrySummaryVO(summeryVos)
			rsVO.setFavStoreId("1245")
		
		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
		    0*grTools.searchRegistries(_, "CA")
			result.getListRegistrySummaryVO().get(0).getFavStoreId() == "1245"
	}
	
	def"searchRegistries.Tc to Search registry for user when Service name is not in (getRegistryInfoByProfileId,regSearch)"(){
		given:
		List summeryVos = [rsVO]
		RegistrySearchVO registrySearchVO = new RegistrySearchVO()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		registrySearchVO.setServiceName("wrongService")
		
		1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,"RegItemsWSCall") >> ["false"]
		
		
		when:
		
		RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
		
		result == null
		0*grUtilsMock.validateInput(_)
		0*grTools.getRegistryListByRegNum(registrySearchVO)
		0*grTools.searchRegistries(registrySearchVO, "CA")
		0*grTools.changedRegistryTypeName(regSearchResVO, "CA")
	}
	
	
	def"searchRegistries.Tc to Search registry when error exists while searching registry id using profile id "(){
		given:
			
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("getRegistryInfoByProfileId")
			//searchRegistriesbyProfileId
			registrySearchVO.setProfileId(profileMock)
			profileMock.getRepositoryId() >> "user1"
			1*grUtilsMock.validateInput(['user1']) >> eStatus
			eStatus.isErrorExists() >> true
			eStatus.getErrorMessage() >> "error in display"
			eStatus.getErrorId() >> 201
			

			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,"RegItemsWSCall") >> null
			
		
		when:
		
			RegSearchResVO result1 = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			0*grTools.searchRegistries(_, "CA")
			ServiceErrorVO error =  result1.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == "error in display"
			error.getErrorId() == 201
	}
	
	def"searchRegistries.Tc to Search registry when profile.toString() is empty while searching registry id using profile id "(){
		given:
			
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("getRegistryInfoByProfileId")
			//searchRegistriesbyProfileId
			registrySearchVO.setProfileId(profileMock)
			profileMock.getRepositoryId() >> "user1"
			1*grUtilsMock.validateInput(['user1']) >> eStatus
			eStatus.isErrorExists() >> false
			profileMock.toString() >> ""

			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,"RegItemsWSCall") >> null
			
		
		when:
		
			RegSearchResVO result1 = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			0*grTools.searchRegistries(_, "CA")
			ServiceErrorVO error =  result1.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == BBBGiftRegistryConstants.ERROR_INVALID_PROFILE_ID
			error.getErrorDisplayMessage() == BBBGiftRegistryConstants.ERROR_INVALID_PROFILE_ID
			error.getErrorId() == 200
	}
	
	//Working
	def"searchRegistries.Tc for Exception  when service name is getRegistryInfoByProfileId "(){
		given:
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO() 
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("getRegistryInfoByProfileId")
			//searchRegistriesbyProfileId
			registrySearchVO.setProfileId(profileMock)
			profileMock.getRepositoryId() >> "user1"
			1*grUtilsMock.validateInput(['user1']) >> eStatus
			eStatus.isErrorExists() >> false
			grTools.searchRegistriesByProfileId(registrySearchVO) >> {throw new Exception("exception")}
			

			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,"RegItemsWSCall") >> null
			registrySearchVO.setFirstName("harry")
			registrySearchVO.setSiteId("us")
			registrySearchVO.setLastName("kuma")
			registrySearchVO.setStartIdx(2)
			registrySearchVO.setEvent("ev")
			registrySearchVO.setState("york")
			
			1*grTools.getFilterOptions("ev", "york")
			1*grUtilsMock.logAndFormatError("getRegistryInfoByProfileId", null, BBBGiftRegistryConstants.SERVICE_ERROR_VO, _, "harry", "us", "kuma",2,*_) >> serviceErrorVO
			serviceErrorVO.setErrorMessage("error in profile id")
			serviceErrorVO.setErrorExists(true)
			
		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			0*grTools.searchRegistries(_, "CA")
			ServiceErrorVO error  = result.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == "error in profile id"
	}
	
	
	def"searchRegistries.Tc to Search registry when service name is regSearch and registry id is not numeric "(){
		given:
            setSpy()			
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			registrySearchVO.setGiftGiver(true)
			registrySearchVO.setRegistryId("kfkfk")
			1*grUtilsMock.isNumeric("kfkfk") >> false
			//1*grTools.getRegistryListByRegNum(registrySearchVO) >> regSearchResVO1
			grUtilsMock.setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,"Invalid registry number","Invalid registry number",true, 200, null) >> serviceErrorVO
			serviceErrorVO.setErrorExists(true) 
			serviceErrorVO.setErrorMessage("Invalid registry number")
			//End
		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			0*grTools.searchRegistries(registrySearchVO, "CA")
			ServiceErrorVO error  = result.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == "Invalid registry number"
	}
	
	def"searchRegistries.Tc to Search registry by email when service name is regSearch and registry id is empty "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			registrySearchVO.setGiftGiver(true)
			registrySearchVO.setRegistryId("")
			registrySearchVO.setEmail("test@gmail.com")
			
			grTools.getRegistryListByEmail(registrySearchVO) >> regSearchResVO1
			
			regSearchResVO1.setListRegistrySummaryVO(summeryVos)
			rsVO.setFavStoreId("1245")
			1*grTools.changedRegistryTypeName(regSearchResVO1, _) >> regSearchResVO1


		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			0*grUtilsMock.isNumeric(_)
			0*grTools.searchRegistries(registrySearchVO, "CA")
			result.getListRegistrySummaryVO().get(0).getFavStoreId() == "1245"	}
	
	
	def"searchRegistries.Tc to Search registry by name when service name is regSearch and registry id is empty and email  "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			registrySearchVO.setGiftGiver(true)
			registrySearchVO.setRegistryId("")
			registrySearchVO.setEmail("")
			
			grTools.getRegistryListByName(registrySearchVO) >> regSearchResVO1
			
			regSearchResVO1.setListRegistrySummaryVO(summeryVos)
			rsVO.setFavStoreId("1245")
			1*grTools.changedRegistryTypeName(regSearchResVO1, _) >> regSearchResVO1


		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			0*grUtilsMock.isNumeric(_)
			0*grTools.searchRegistries(registrySearchVO, "CA")
			result.getListRegistrySummaryVO().get(0).getFavStoreId() == "1245"	}
	
	
	def"searchRegistries.Tc to Search registry by name when service name is regSearch and is not gift giver and profile id is empty  "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			//List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			registrySearchVO.setGiftGiver(false)
			registrySearchVO.setProfileId(profileMock)
			profileMock.getRepositoryId() >> ""
			

			1*grUtilsMock.setErrorInResponse(_, "Profile Id is required when isGiftGiver = false", "Profile Id is required for registrant search", true, 200, null) >> serviceErrorVO
			serviceErrorVO.setErrorExists(true)
			serviceErrorVO.setErrorMessage("Profile Id is required for registrant search")

			1*grUtilsMock.isNumeric("12456") >> true
			registrySearchVO.setRegistryId("12456")
			grTools.regSearchByRegUsingRegNum(registrySearchVO) >> regSearchResVO

		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			ServiceErrorVO error  = result.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == "Profile Id is required for registrant search"
	}
	
	def"searchRegistries.Tc to Search registry by name when service name is regSearch and is not gift giver and registry id is not numric  "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			//List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			registrySearchVO.setGiftGiver(false)
			registrySearchVO.setProfileId(profileMock)
			profileMock.getRepositoryId() >> "user1"
			

			grUtilsMock.setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO, "Invalid registry number", "Invalid registry number", true, 200, null) >> serviceErrorVO
			serviceErrorVO.setErrorExists(true)
			serviceErrorVO.setErrorMessage("Invalid registry number")

			1*grUtilsMock.isNumeric("12456") >> false
			registrySearchVO.setRegistryId("12456")
			grTools.regSearchByRegUsingRegNum(registrySearchVO) >> regSearchResVO

		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			ServiceErrorVO error  = result.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == "Invalid registry number"
	}
	
	def"searchRegistries.Tc to Search registry by name when service name is regSearch and is not gift giver and registry id is empty  "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			registrySearchVO.setGiftGiver(false)
			registrySearchVO.setProfileId(profileMock)
			registrySearchVO.setEmail("test@gmail.com")
			profileMock.getRepositoryId() >> "user1"
			

			grTools.regSearchByRegUsingEmail(registrySearchVO) >> regSearchResVO1
			registrySearchVO.setRegistryId("")
			1*grTools.changedRegistryTypeName(regSearchResVO1, _) >> regSearchResVO1
			//grTools.regSearchByRegUsingRegNum(registrySearchVO) >> regSearchResVO
			regSearchResVO1.setListRegistrySummaryVO(summeryVos)
			rsVO.setFavStoreId("1245")

		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			result.getListRegistrySummaryVO().get(0).getFavStoreId() == "1245"	
			
			}
	
	
	def"searchRegistries.Tc to Search registry by name when service name is regSearch and is not gift giver and email id  is empty  "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			registrySearchVO.setGiftGiver(false)
			registrySearchVO.setProfileId(profileMock)
			registrySearchVO.setEmail("")
			profileMock.getRepositoryId() >> "user1"
			

			grTools.regSearchByRegUsingName(registrySearchVO) >> regSearchResVO1
			registrySearchVO.setRegistryId("")
			1*grTools.changedRegistryTypeName(regSearchResVO1, _) >> regSearchResVO1
			//grTools.regSearchByRegUsingRegNum(registrySearchVO) >> regSearchResVO
			regSearchResVO1.setListRegistrySummaryVO(summeryVos)
			rsVO.setFavStoreId("1245")

		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			result.getListRegistrySummaryVO().get(0).getFavStoreId() == "1245"
			
			}
	
	def"searchRegistries.Tc for Exception 'Registry not found' when service name is regSearch   "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> {throw new Exception("Registry not found")}
			
			registrySearchVO.setFirstName("harry")
			registrySearchVO.setSiteId("us")
			registrySearchVO.setLastName("kuma")
			registrySearchVO.setStartIdx(2)
			registrySearchVO.setEvent("ev")
			registrySearchVO.setState("york")
			
			1*grTools.getFilterOptions("ev", "york")
			1*grUtilsMock.logAndFormatError("regSearch", null, BBBGiftRegistryConstants.SERVICE_ERROR_VO, _, "harry", "us", "kuma",2,*_) >> serviceErrorVO
			serviceErrorVO.setErrorMessage("error in registry id")
			serviceErrorVO.setErrorExists(true)


		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			ServiceErrorVO error  = result.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == "error in registry id"
			
			}
	
	def"searchRegistries.Tc for Exception 'DBexception' when service name is regSearch   "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> {throw new Exception("DBexception")}
			
			registrySearchVO.setFirstName("harry")
			registrySearchVO.setSiteId("us")
			registrySearchVO.setLastName("kuma")
			registrySearchVO.setStartIdx(2)
			registrySearchVO.setEvent("ev")
			registrySearchVO.setState("york")
			
			1*grTools.getFilterOptions("ev", "york")
			1*grUtilsMock.logAndFormatError("regSearch", null, BBBGiftRegistryConstants.SERVICE_ERROR_VO, _, "harry", "us", "kuma",2,*_) >> serviceErrorVO
			serviceErrorVO.setErrorMessage("error")
			serviceErrorVO.setErrorExists(true)


		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			ServiceErrorVO error  = result.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == "error"
			
			}
	
	def"searchRegistries.Tc for Exception 'DBexception'  with null error message when service name is regSearch   "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			RegSearchResVO regSearchResVO1 = new RegSearchResVO()
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> {throw new Exception()}
			
			registrySearchVO.setFirstName("harry")
			registrySearchVO.setSiteId("us")
			registrySearchVO.setLastName("kuma")
			registrySearchVO.setStartIdx(2)
			registrySearchVO.setEvent("ev")
			registrySearchVO.setState("york")
			
			1*grTools.getFilterOptions("ev", "york")
			1*grUtilsMock.logAndFormatError("regSearch", null, BBBGiftRegistryConstants.SERVICE_ERROR_VO, _, "harry", "us", "kuma",2,*_) >> serviceErrorVO
			serviceErrorVO.setErrorMessage("error")
			serviceErrorVO.setErrorExists(true)


		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			ServiceErrorVO error  = result.getServiceErrorVO()
			error.isErrorExists()
			error.getErrorMessage() == "error"
			
			}
	
	def"searchRegistries.Tc when error exists   "(){
		given:
			setSpy()
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			//List summeryVos = [rsVO]
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			RegSearchResVO regSearchResVO = new RegSearchResVO()
			
			
			registrySearchVO.setServiceName("regSearch")
			//searchRegistriesbyProfileId
			1*grManager.validRegistrySearchVO(registrySearchVO)  >> regSearchResVO
			regSearchResVO.setServiceErrorVO(serviceErrorVO)
			serviceErrorVO.setErrorExists(true)
			


		when:
		
			RegSearchResVO result = grManager.searchRegistries(registrySearchVO, "CA")
		
		then:
			ServiceErrorVO error  = result.getServiceErrorVO()
			error.isErrorExists()
			
			}
	
	
	/************************************************* validRegistrySearchVO *********************************/
	
	def"validRegistrySearchVO. tc to perform validation"(){
		given:
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySearchVO.setRegistryId("546")
			registrySearchVO.setEmail("")
			registrySearchVO.setFirstName("a")
			registrySearchVO.setLastName("harry")
			registrySearchVO.setState("york")
			registrySearchVO.setSiteId("CA")
			1*grUtilsMock.isValidState("york", "CA") >> true
			
			registrySearchVO.setEvent("evn")
			1*grUtilsMock.hasInvalidChars("evn") >> false
			1*grUtilsMock.hasInvalidCharsForHackOnly("evn") >> false
			
			registrySearchVO.setGiftGiver(true)
			1*grUtilsMock.isNumeric("546") >> true
		
		when:
			RegSearchResVO result = grManager.validRegistrySearchVO(registrySearchVO)
		then:
			!result.getServiceErrorVO().isErrorExists()
	}
	
	def"validRegistrySearchVO. tc to perform validation when registry id is not valid"(){
		given:
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySearchVO.setRegistryId("zyx")
			registrySearchVO.setEmail("")
			registrySearchVO.setFirstName("a")
			registrySearchVO.setLastName("harry")
			registrySearchVO.setState("")
			registrySearchVO.setSiteId("CA")
			
			registrySearchVO.setEvent("")
			
			registrySearchVO.setGiftGiver(true)
			1*grUtilsMock.isNumeric("zyx") >> false
			
			1*grUtilsMock.setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
				BBBGiftRegistryConstants.ERROR_INVALID_REGISTRY, BBBGiftRegistryConstants.ERROR_INVALID_REGISTRY, true, 200, null) >> serviceErrorVO
			
			serviceErrorVO.setErrorMessage("invalidRegistry")
			serviceErrorVO.setErrorExists(true)
		when:
			RegSearchResVO result = grManager.validRegistrySearchVO(registrySearchVO)
		then:
			result.getServiceErrorVO().isErrorExists()
			result.getServiceErrorVO().getErrorMessage() == "invalidRegistry"
			0*grUtilsMock.isValidState("york", "CA")
			0*grUtilsMock.hasInvalidChars("")
	}
	
	def"validRegistrySearchVO. tc to perform validation when registry id is empty and last name length more then  2"(){
		given:
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySearchVO.setRegistryId("")
			registrySearchVO.setEmail("")
			registrySearchVO.setFirstName("a")
			registrySearchVO.setLastName("harry")
			registrySearchVO.setState("")
			registrySearchVO.setSiteId("CA")
			
			registrySearchVO.setEvent("")
			
			registrySearchVO.setGiftGiver(true)
	when:
			RegSearchResVO result = grManager.validRegistrySearchVO(registrySearchVO)
		then:
			!result.getServiceErrorVO().isErrorExists()
			
			}
	
	def"validRegistrySearchVO. tc to perform validation when it is not gift givver  and first name length more then  1"(){
		given:
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySearchVO.setRegistryId("")
			registrySearchVO.setEmail("")
			registrySearchVO.setFirstName("aaa")
			registrySearchVO.setLastName("harry")
			registrySearchVO.setState("")
			registrySearchVO.setSiteId("CA")
			
			registrySearchVO.setEvent("")
			
			registrySearchVO.setGiftGiver(false)
		when:
			RegSearchResVO result = grManager.validRegistrySearchVO(registrySearchVO)
		then:
			!result.getServiceErrorVO().isErrorExists()	
			
			}
	
	def"validRegistrySearchVO. tc to perform validation when filter is not valid  , first name is  empty"(){
		given:
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySearchVO.setRegistryId("")
			registrySearchVO.setEmail("")
			registrySearchVO.setFirstName("")
			registrySearchVO.setLastName("harry")
			registrySearchVO.setState("york")
			registrySearchVO.setSiteId("CA")
			
			registrySearchVO.setGiftGiver(false)
			
			1*grUtilsMock.isValidState("york", "CA") >> false
			
			registrySearchVO.setEvent("evn")
			1*grUtilsMock.hasInvalidChars("evn") >> true
			
			1*grUtilsMock.setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
				BBBGiftRegistryConstants.ERROR_INVALID_FILTERS, BBBGiftRegistryConstants.ERROR_INVALID_FILTERS, true, 200, null) >> serviceErrorVO
			
			serviceErrorVO.setErrorMessage("invalidFilter")
			serviceErrorVO.setErrorExists(true)

	when:
			RegSearchResVO result = grManager.validRegistrySearchVO(registrySearchVO)
		then:
			result.getServiceErrorVO().isErrorExists()
			result.getServiceErrorVO().getErrorMessage() == "invalidFilter"
			
			}
	
	def"validRegistrySearchVO. tc to perform validation when filter is not valid  , email is not   empty"(){
		given:
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySearchVO.setRegistryId("")
			registrySearchVO.setEmail("harry@gmail.com")
			registrySearchVO.setFirstName("")
			registrySearchVO.setLastName("harry")
			registrySearchVO.setState("york")
			registrySearchVO.setSiteId("CA")
			
			grUtilsMock.isValidEmail("harry@gmail.com") >> true
			
			registrySearchVO.setGiftGiver(false)
			
			1*grUtilsMock.isValidState("york", "CA") >> false
			
			registrySearchVO.setEvent("evn")
			1*grUtilsMock.hasInvalidChars("evn") >> false
			1*grUtilsMock.hasInvalidCharsForHackOnly("evn") >> true
			
			1*grUtilsMock.setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
				BBBGiftRegistryConstants.ERROR_INVALID_FILTERS, BBBGiftRegistryConstants.ERROR_INVALID_FILTERS, true, 200, null) >> serviceErrorVO
			
			serviceErrorVO.setErrorMessage("invalidFilter")
			serviceErrorVO.setErrorExists(true)

			
	when:
			RegSearchResVO result = grManager.validRegistrySearchVO(registrySearchVO)
		then:
			result.getServiceErrorVO().isErrorExists()
			result.getServiceErrorVO().getErrorMessage() == "invalidFilter"
			
			}
	
	def"validRegistrySearchVO. tc to perform   , email is not valid  "(){
		given:
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySearchVO.setRegistryId("")
			registrySearchVO.setEmail("harry,,,@gmail.com")
			registrySearchVO.setFirstName("")
			registrySearchVO.setLastName("harry")
			registrySearchVO.setState("york")
			registrySearchVO.setSiteId("CA")
			
			grUtilsMock.isValidEmail("harry,,,@gmail.com") >> false
			
		
			1*grUtilsMock.setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
				BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_CHARS, BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_CHARS, true, 200, null) >> serviceErrorVO
			
			serviceErrorVO.setErrorMessage("invalidEmail")
			serviceErrorVO.setErrorExists(true)

			
		when:
			RegSearchResVO result = grManager.validRegistrySearchVO(registrySearchVO)
		then:
			result.getServiceErrorVO().isErrorExists()
			result.getServiceErrorVO().getErrorMessage() == "invalidEmail"
			
			}
	
/*	def"validRegistrySearchVO. tc to perform   , first name is not valid  empty"(){
		given:
			ServiceErrorVO serviceErrorVO = new ServiceErrorVO()
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySearchVO.setRegistryId("")
			registrySearchVO.setEmail("")
			registrySearchVO.setFirstName("")
			registrySearchVO.setLastName("h")
			registrySearchVO.setState("york")
			registrySearchVO.setSiteId("CA")
			
			grUtilsMock.isValidEmail("harry,,,@gmail.com") >> false
			

			1*grUtilsMock.setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
				BBBGiftRegistryConstants.ERROR_FIRST_LAST_NAME, BBBGiftRegistryConstants.ERROR_FIRST_LAST_NAME, true, 200, null) >> serviceErrorVO
			
			serviceErrorVO.setErrorMessage("invalidName")
			serviceErrorVO.setErrorExists(true)

			
	when:
			RegSearchResVO result = grManager.validRegistrySearchVO(registrySearchVO)
		then:
			result.getServiceErrorVO().isErrorExists()
			result.getServiceErrorVO().getErrorMessage() == "invalidName"
			
			}*/
	
	
	/***************************************************** assignAnnouncementCardCount **********************************/
	
	def"assignAnnouncementCardCount"(){
		given:
			RegistryVO registryVO = new RegistryVO()
			SetAnnouncementCardResVO setAnnouncementCardResVO = new SetAnnouncementCardResVO()
			1*grTools.assignAnnouncementCardCount(registryVO) >> setAnnouncementCardResVO
			
			setAnnouncementCardResVO.setOperationStatus(true)
		when:
			SetAnnouncementCardResVO result = grManager.assignAnnouncementCardCount(registryVO)
		then:
			result.isOperationStatus()
		
	}
	
	/******************************** getRegistryInfo ********************************************************************/
	
	def"getRegistryInfo.TC to get registry info"(){
		given:
			RegistryReqVO regReqVO = new RegistryReqVO()
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			// getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> ["true"]
			1*grTools.getRegistryInfo(regReqVO) >> registryResVO
			//end
			regReqVO.setSiteId(BBBGiftRegistryConstants.CANADA_SITE_ID)
			regReqVO.setRegistryId("rId")
			
			registryResVO.setRegistrySummaryVO(rSummeryVO)
			rSummeryVO.setEventDate("14/12/2014")
			rSummeryVO.setFutureShippingDate("10/10/2014")
			
		when:
			RegistryResVO result = grManager.getRegistryInfo(regReqVO)
		then:
			result.getRegistrySummaryVO().getEventDate() == "14/12/2014"
			result.getRegistrySummaryVO().getFutureShippingDate() == "10/10/2014"
	}
	
	def"getRegistryInfo.TC to get registry info when future shipping date is null"(){
		given:
			RegistryReqVO regReqVO = new RegistryReqVO()
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			
			regReqVO.setSiteId(BBBGiftRegistryConstants.CANADA_SITE_ID)
			regReqVO.setRegistryId("rId")

			// getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
			1*grTools.getRegistryInfoFromEcomAdmin("rId", BBBGiftRegistryConstants.CANADA_SITE_ID) >> registryResVO
			//1*grTools.getRegistryInfo(regReqVO) >> registryResVO
			//end
			
			registryResVO.setRegistrySummaryVO(rSummeryVO)
			rSummeryVO.setEventDate("14/12/2014")
			rSummeryVO.setFutureShippingDate(null)
			
		when:
			RegistryResVO result = grManager.getRegistryInfo(regReqVO)
		then:
			result.getRegistrySummaryVO().getEventDate() == "14/12/2014"
			result.getRegistrySummaryVO().getFutureShippingDate() == null
			0*grTools.getRegistryInfo(regReqVO)
	}
	
	def"getRegistryInfo.TC to get registry info when site id is not Canada"(){
		given:
			RegistryReqVO regReqVO = new RegistryReqVO()
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			
			regReqVO.setSiteId("us")
			regReqVO.setRegistryId("rId")

			// getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
			1*grTools.getRegistryInfoFromEcomAdmin("rId", "us") >> registryResVO
			//end
			
			registryResVO.setRegistrySummaryVO(rSummeryVO)
			rSummeryVO.setEventDate("14/12/2014")
			
		when:
			RegistryResVO result = grManager.getRegistryInfo(regReqVO)
		then:
			result.getRegistrySummaryVO().getEventDate() == "14/12/2014"
			0*grTools.getRegistryInfo(regReqVO)
	}
	
	def"getRegistryInfo.TC to get registry info when event date is null"(){
		given:
			RegistryReqVO regReqVO = new RegistryReqVO()
			RegistryResVO registryResVO = new RegistryResVO()
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			
			regReqVO.setSiteId("us")
			regReqVO.setRegistryId("rId")

			// getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
			1*grTools.getRegistryInfoFromEcomAdmin("rId", "us") >> registryResVO
			//end
			
			registryResVO.setRegistrySummaryVO(rSummeryVO)
			rSummeryVO.setEventDate(null)
			
		when:
			RegistryResVO result = grManager.getRegistryInfo(regReqVO)
		then:
			result.getRegistrySummaryVO().getEventDate() == null
	}
	
	def"getRegistryInfo.TC to get registry info when RegistrySummaryVO is null"(){
		given:
			RegistryReqVO regReqVO = new RegistryReqVO()
			RegistryResVO registryResVO = new RegistryResVO()
			
			regReqVO.setSiteId("us")
			regReqVO.setRegistryId("rId")

			// getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
			1*grTools.getRegistryInfoFromEcomAdmin("rId", "us") >> registryResVO
			//end
			
			registryResVO.setRegistrySummaryVO(null)
			
		when:
			RegistryResVO result = grManager.getRegistryInfo(regReqVO)
		then:
			result.getRegistrySummaryVO() == null
	}
	
	def"getRegistryInfo.TC to get registry info when registryResponse is null"(){
		given:
			RegistryReqVO regReqVO = new RegistryReqVO()
			RegistryResVO registryResVO = new RegistryResVO()
			
			regReqVO.setSiteId("us")
			regReqVO.setRegistryId("rId")

			// getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
			1*grTools.getRegistryInfoFromEcomAdmin("rId", "us") >> null
			//end
						
		when:
			RegistryResVO result = grManager.getRegistryInfo(regReqVO)
		then:
			result == null
	}
	
	
	/*********************************************** fetchRegistryItems *****************************/
	def"fetchRegistryItems.TC to fetch refistryItem"(){
		given:
			RegistrySearchVO pRegistrySearchVO = new RegistrySearchVO()
			RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO() 
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> ["true"]
			1*grTools.fetchRegistryItems(pRegistrySearchVO) >> registryItemsListVO
			registryItemsListVO.setSortReponse("12")
		when:
			 RegistryItemsListVO result = grManager.fetchRegistryItems(pRegistrySearchVO)
		then:
			result.getSortReponse() == "12"
	}
	
	def"fetchRegistryItems.TC to fetch refistryItem when regItemsWSCall is false"(){
		given:
			RegistrySearchVO pRegistrySearchVO = new RegistrySearchVO()
			RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO()
			
			pRegistrySearchVO.setRegistryId("125")
			pRegistrySearchVO.setView(12)
			
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
			1*grTools.fetchRegistryItemsFromEcomAdmin("125", "12") >> registryItemsListVO
			registryItemsListVO.setSortReponse("12")
			
		when:
			 RegistryItemsListVO result = grManager.fetchRegistryItems(pRegistrySearchVO)
			 
		then:
			result.getSortReponse() == "12"
	}
	
	/*************************************** fetchRegistryItemsFromRepo **************************************/
	
	def"fetchRegistryItemsFromRepo"(){
		given:
			RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO() 
			1*grTools.fetchRegistryItemsFromRepo("rId", "view") >> registryItemsListVO
			registryItemsListVO.setSortReponse("13")
		when:
			RegistryItemsListVO result = grManager.fetchRegistryItemsFromRepo("rId", "view")
		then:
		result.getSortReponse() == "13"
	}
	/********************************************* fetchRegistryItemsFromEcomAdmin ************************************/
	def"fetchRegistryItemsFromEcomAdmin"(){
		given:
			RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO()
			1*grTools.fetchRegistryItemsFromEcomAdmin("rId", "view") >> registryItemsListVO
			registryItemsListVO.setSortReponse("14")
		when:
			RegistryItemsListVO result = grManager.fetchRegistryItemsFromEcomAdmin("rId", "view")
		then:
		result.getSortReponse() == "14"
	}
	
	
	/********************************************* fetchRegistryItemsFromEcomAdmin for four parameters ************************************/
	def"fetchRegistryItemsFromEcomAdmin. TC for RLV is true"(){
		given:
			RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO()
			1*grTools.fetchRegistryItemsFromEcomAdmin("rId", true, true, "regView") >> registryItemsListVO
			registryItemsListVO.setSortReponse("16")
			
		when:
			RegistryItemsListVO result = grManager.fetchRegistryItemsFromEcomAdmin("rId", true, true, "regView")
			
		then:
		result.getSortReponse() == "16"
	}
	
	def"fetchRegistryItemsFromEcomAdmin. TC for RLV is false and gift giver is true"(){
		given:
			RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO()
			1*grTools.fetchRegistryItemsFromEcomAdmin("rId", "regView") >> registryItemsListVO
			registryItemsListVO.setSortReponse("17")
			
		when:
			RegistryItemsListVO result = grManager.fetchRegistryItemsFromEcomAdmin("rId", true, false, "regView")
			
		then:
		result.getSortReponse() == "17"
	}
	
	def"fetchRegistryItemsFromEcomAdmin. TC for RLV is false and gift giver is false"(){
		given:
		
			RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO()
			1*grTools.fetchRegistryItemsFromEcomAdmin("rId", false, "regView") >> registryItemsListVO
			registryItemsListVO.setSortReponse("18")
			
		when:
		
			RegistryItemsListVO result = grManager.fetchRegistryItemsFromEcomAdmin("rId", false, false, "regView")
			
		then:
		result.getSortReponse() == "18"
	}
	
	
	/******************************************** getRegistryDate *********************/
	
	def"getRegistryDate.Tc to get registry date"(){
		given:
			RegistrySkinnyVO rsVO = new RegistrySkinnyVO()
			RegistrySkinnyVO rsVO2 = new RegistrySkinnyVO()
			List rsList = [rsVO,null,rsVO2] 
			Map value = new HashMap()
			
			value.put(BBBGiftRegistryConstants.PROP_REGISTRY_SKINNY_VO_LIST, rsList)
			1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			sessionBean.getValues() >> value
			
			rsVO.setRegistryId("rrrId")
			//rsVO1.setRegistryId(null)
			rsVO2.setRegistryId("rId")
			
			rsVO2.setEventDate("14/12/2014")
		
		when:
			String result = grManager.getRegistryDate(requestMock, "rId")
			
		then:
		
			result == "14/12/2014"
		
	}
	
	def"getRegistryDate.Tc to get registry date when RegistrySkinnyVOList is empty "(){
		given:
			List rsList = []
			Map value = new HashMap()
			
			value.put(BBBGiftRegistryConstants.PROP_REGISTRY_SKINNY_VO_LIST, rsList)
			1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			sessionBean.getValues() >> value
			
		
		when:
			String result = grManager.getRegistryDate(requestMock, "rId")
			
		then:
		
			result == null
		
	}
	
	
	/************************************************* getNotifyRegistrantMsgType **********************************/
	
	def"getNotifyRegistrantMsgType.TC to fetch fetch inventory threshold value"(){
		given:
		setSpy()
		    RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathUS"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_WEB_SKU_STATUS_CD) >> "N"
			//end
			
			//getConfiguredInvThreshold
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> associatedSubDept
			associatedSubDept.getRepositoryId() >> "ass1"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "ass1") >> ["-1"]
			
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> associatedDept
			associatedDept.getRepositoryId() >> "assd1"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "assd1") >> ["-1"]
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> ["1"]
			//end
			
			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathUS") >> 0
			//end
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> ["3"]
			
		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10/10/2060")
		then:
			value == "N"
	}
	
	def"getNotifyRegistrantMsgType.TC to fetch fetch inventory threshold value when site id is BedBathCanada "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> "D"
			//end
			
			//getConfiguredInvThreshold
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> associatedSubDept
			associatedSubDept.getRepositoryId() >> "ass1"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "ass1") >> []
			
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> associatedDept
			associatedDept.getRepositoryId() >> "assd1"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "assd1") >> []
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> ["1"]
			//end
			
			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathCanada") >> 3
			//end
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> []
			
		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10/10/2060")
		then:
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC to fetch fetch inventory threshold value when site id is BedBathCanad and  associatedDept id is empty  "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> "D"
			//end
			
			//getConfiguredInvThreshold
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> associatedSubDept
			associatedSubDept.getRepositoryId() >> ""
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "") >> []
			
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> associatedDept
			associatedDept.getRepositoryId() >> ""
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "") >> []
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> ["1"]
			//end
			
			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathCanada") >> 0
			//end
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "tbs"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> []
			
		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10/10/2014")
		then:
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC for BBBBusinessException when site id is BedBathCanad and  associatedDept id is null  "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> "D"
			//end
			
			//getConfiguredInvThreshold
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> associatedSubDept
			associatedSubDept.getRepositoryId() >> "aa"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "aa") >> {throw new BBBBusinessException("exception")}
			
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> associatedDept
			associatedDept.getRepositoryId() >> "bb"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "bb") >> {throw new BBBBusinessException("exception")}
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> []
			//end
			
/*			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathCanada") >> 0
			//end
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "tbs"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> []
*/			
		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10102014")
		then:
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC when site id is BedBathCanad and  associatedDept id is null  "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> "D"
			//end
			
			//getConfiguredInvThreshold
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> null
			//associatedSubDept.getRepositoryId() >> "aa"
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "aa") >> {throw new BBBBusinessException("exception")}
			
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> null
			//associatedDept.getRepositoryId() >> "bb"
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "bb") >> {throw new BBBBusinessException("exception")}
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> {throw new BBBBusinessException("exception")}
			//end
			
/*			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathCanada") >> 0
			//end
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "tbs"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> []
*/
		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10102014")
		then:
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC when site id is BedBathCanad and  configuredInvThreshold is 0 "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> "D"
			//end
			
			//getConfiguredInvThreshold
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> associatedSubDept
			associatedSubDept.getRepositoryId() >> "aa"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "aa") >> ["0"]
			
			//1*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> null
			//associatedDept.getRepositoryId() >> "bb"
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "bb") >> {throw new BBBBusinessException("exception")}
			
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> {throw new BBBBusinessException("exception")}
			//end
			
/*			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathCanada") >> 0
			//end
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "tbs"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> []
*/
		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10102014")
		then:
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC when site id is BedBathCanad and  sku status code is null "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> null
			//end
			

		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10102014")
			
			
		then:
			value == ""
			0*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "aa")
	}
	
	def"getNotifyRegistrantMsgType.TC when site id is BedBathUS and  sku status code is null "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BuyBuyBaby"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_WEB_SKU_STATUS_CD) >> null
			//end
			

		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10102014")
			
			
		then:
			value == ""
			0*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "aa")
	}
	
	def"getNotifyRegistrantMsgType.TC when site id is not in  (BedBathUS,BedBathCanada and Buy Buy baby )"(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "site"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			

		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "10102014")
			
			
		then:
			value == ""
			0*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "aa")
			0*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_WEB_SKU_STATUS_CD)
	}
	
	def"getNotifyRegistrantMsgType.TC to fetch fetch inventory threshold value when event date dosent contain slash "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> "D"
			//end
			
			//getConfiguredInvThreshold
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> associatedSubDept
			associatedSubDept.getRepositoryId() >> ""
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "") >> []
			
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> associatedDept
			associatedDept.getRepositoryId() >> ""
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "") >> []
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> ["1"]
			//end
			
			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathCanada") >> 0
			//end
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "tbs"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> []
			
		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "abs")
		then:
			1*grManager.logError("getNotifyRegistrantMsgType method :  Error occurred while parsing date",_)
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC for BBBBusinessException "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> "D"
			//end
			
			//getConfiguredInvThreshold
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> associatedSubDept
			associatedSubDept.getRepositoryId() >> ""
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "") >> []
			
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> associatedDept
			associatedDept.getRepositoryId() >> ""
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "") >> []
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> ["1"]
			//end
			
			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathCanada") >> 0
			//end
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "tbs"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> {throw new BBBBusinessException("exception")}
			
		when:
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "abs")
		then:
			1*grManager.logError("getNotifyRegistrantMsgType method :  Error occurred while fetching configKeys from bcc",_)
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC for BBBSystemException "(){
		given:
		setSpy()
			RepositoryItem  associatedSubDept = Mock()
			RepositoryItem  associatedDept = Mock()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> reItem
			//getSkuNotifyRegStatusCode
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) >> "D"
			//end
			
			//getConfiguredInvThreshold
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) >> associatedSubDept
			associatedSubDept.getRepositoryId() >> ""
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "") >> []
			
			2*reItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) >> associatedDept
			associatedDept.getRepositoryId() >> ""
			//1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, "") >> []
			
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY) >> ["1"]
			//end
			
			//verifyInventoryRule
			1*reItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID) >> "sku1"
			1*invManagerMock.getAfs("sku1", "BedBathCanada") >> {throw new BBBSystemException("exception") }
			//end
/*			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "tbs"
			1*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY) >> {throw new BBBBusinessException("exception")}
*/			
		when:
		
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "abs")
			
		then:
		
			1*grManager.logError("getNotifyRegistrantMsgType method :  Error occurred while setting notify registrant details",_)
			0*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY)
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC for RepositoryException "(){
		given:
		setSpy()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> {throw new RepositoryException("exception")}

		when:
		
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "abs")
			
		then:
		
			1*grManager.logError("getNotifyRegistrantMsgType method : Error occurred while getting sku item from sku id",_)
			0*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY)
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC when catalog repository item is null "(){
		given:
		setSpy()
			1*grManager.getSiteId() >> "BedBathCanada"
			1*catRepository.getItem("sku1", BBBGiftRegistryConstants.SKU) >> null

		when:
		
			String value =  grManager.getNotifyRegistrantMsgType("sku1", "abs")
			
		then:
		
			1 * grManager.logDebug('SKU item is null, sending displayMessageType as blank.')
			0*catTools.getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY)
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC when sku id is null "(){
		given:
		setSpy()
			1*grManager.getSiteId() >> "BedBathCanada"

		when:
		
			String value =  grManager.getNotifyRegistrantMsgType(null, "abs")
			
		then:
			0*catRepository.getItem(_, BBBGiftRegistryConstants.SKU)
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC when site id is TBS "(){
		given:
		setSpy()
			1*grManager.getSiteId() >> "TBS"

		when:
		
			String value =  grManager.getNotifyRegistrantMsgType(null, "abs")
			
		then:
			0*catRepository.getItem(_, BBBGiftRegistryConstants.SKU)
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC when event date is 0 "(){
		given:
		setSpy()
			1*grManager.getSiteId() >> "TBS"

		when:
		
			String value =  grManager.getNotifyRegistrantMsgType(null, "0")
			
		then:
			0*catRepository.getItem(_, BBBGiftRegistryConstants.SKU)
			value == ""
	}
	
	def"getNotifyRegistrantMsgType.TC when event date is empty "(){
		given:
		setSpy()
			1*grManager.getSiteId() >> "TBS"

		when:
		
			String value =  grManager.getNotifyRegistrantMsgType(null, "")
			
		then:
			0*catRepository.getItem(_, BBBGiftRegistryConstants.SKU)
			value == ""
	}
	

	/*******************************************************  convertRegSummaryVOToRegistryVOv ***************************************/
	
/*	def"convertRegSummaryVOToRegistryVO"(){
		given:
			RegistrySummaryVO registrySummaryVO = new RegistrySummaryVO()
			registrySummaryVO.setCoRegistrantFirstName("fName")
			registrySummaryVO.setCoRegistrantLastName("lName")
			
			registrySummaryVO.setPrimaryRegistrantFirstName("pFName")
			registrySummaryVO.setPrimaryRegistrantLastName("pLName")
			
			registrySummaryVO.setEventDate("02/02/2020")
			registrySummaryVO.setRegistryId("rId")
			
			registrySummaryVO.setEventCode("01A")
			registrySummaryVO.setRegistryType("regType")
			
		when:
		
		then:
		
		registryVO.getCoRegistrant(registrantVO)
	}	
	*/
	
	
	/***************************************************  doCoRegLinking *************************************************/
	
	def"doCoRegLinking.TC to  link co-registrant to registry against against email address "(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO() 
		RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		RegistryResVO linkCoRegToRegistries = new RegistryResVO()
		List sVos = [rSummeryVO]
		ServiceErrorVO errorVO = new ServiceErrorVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		//RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		
		2*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> ["usBed"]
		2*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true && vo.siteId== 'usBed' && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(false)
		
		regSearchResVO.setListRegistrySummaryVO(sVos)
		profileMock.getRepositoryId() >> "pId"
		
		1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> ["true"]
		
		1*grTools.linkCoRegProfileToReg({RegistryReqVO rVO -> rVO.emailId=='ha@gmail.com' && rVO.profileId=='pId' && rVO.siteId=='usBed' && rVO.userToken=='ut'}, true) >> linkCoRegToRegistries
		
		linkCoRegToRegistries.setServiceErrorVO(errorVO)
		errorVO.setErrorExists(false)
		
		// convertRegSummaryVOToRegistryVO
		rSummeryVO.setCoRegistrantFirstName("fName")
		rSummeryVO.setCoRegistrantLastName("lName")
		
		rSummeryVO.setPrimaryRegistrantFirstName("pFName")
		rSummeryVO.setPrimaryRegistrantLastName("pLName")
		
		rSummeryVO.setEventDate("02/02/2020")
		rSummeryVO.setRegistryId("rId")
		
		rSummeryVO.setEventCode("01A")
		rSummeryVO.setRegistryType(registryType)

		//end
		rSummeryVO.setCoRegistrantEmail("ha@gmail.com")
		
		1*grManager.getProfileItemFromEmail("ha@gmail.com","usBed") >> profileItem
		1*grTools.addORUpdateRegistry({RegistryVO rvo ->  rvo.registryId=='rId' }, null,profileItem)
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		!value 
	}
	
	def"doCoRegLinking.TC to  link co-registrant to registry against against email addres when profile item is null"(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		RegistryResVO linkCoRegToRegistries = new RegistryResVO()
		List sVos = [rSummeryVO]
		ServiceErrorVO errorVO = new ServiceErrorVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		//RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> null 
		2*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true  && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(true)
		
		regSearchResVO.setListRegistrySummaryVO(sVos)
		profileMock.getRepositoryId() >> "pId"
		
		1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
		
		1*grTools.linkCoRegProfileToReg(_, false) >> linkCoRegToRegistries
		
		linkCoRegToRegistries.setServiceErrorVO(errorVO)
		errorVO.setErrorExists(true)
		
		// convertRegSummaryVOToRegistryVO
		convertRegSummaryVOToRegistryVO(rSummeryVO)

		//end
		rSummeryVO.setCoRegistrantEmail("ha@gmail.com")
		
		cTools.getItemFromEmail("ha@gmail.com") >> null
		
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		!value
		0*grTools.addORUpdateRegistry(_, null,_)
	}
	
	def"doCoRegLinking.TC to  link co-registrant to registry against against email addres when profile repository id is empty"(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		RegistryResVO linkCoRegToRegistries = new RegistryResVO()
		List sVos = [rSummeryVO]
		ServiceErrorVO errorVO = Mock()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		//RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> null
		2*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true  && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(true)
		errorVO1.setErrorDisplayMessage("wrongError")
		
		regSearchResVO.setListRegistrySummaryVO(sVos)
		profileMock.getRepositoryId() >> ""
		
		1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
		
		//1*grTools.linkCoRegProfileToReg(_, false) >> linkCoRegToRegistries
		
/*		linkCoRegToRegistries.setServiceErrorVO(errorVO)
		errorVO.isErrorExists() >> true
		errorVO.getErrorDisplayMessage() >> "err"
		errorVO.getErrorId() >> 11145
	*/	
		// convertRegSummaryVOToRegistryVO
		convertRegSummaryVOToRegistryVO(rSummeryVO)

		//end
		rSummeryVO.setCoRegistrantEmail("ha@gmail123.com")
		
		//cTools.getItemFromEmail("ha@gmail.com") >> null
		
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		!value
		0*grTools.addORUpdateRegistry(_, null,_)
		0*cTools.getItemFromEmail("ha@gmail.com")
		0*grTools.linkCoRegProfileToReg(_, false)
	}
	
	
	def"doCoRegLinking.TC sql exception while getting Search response"(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> null
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true  && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(true)
		errorVO1.setErrorDisplayMessage(".SQLException")
		
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_code_db_conn:err_code_db_conn"
	}
	
	def"doCoRegLinking.TC system exception when error id is 200  while getting Search response"(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> null
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true  && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(true)
		errorVO1.setErrorDisplayMessage("ex")
		errorVO1.setErrorId(200)
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_gift_reg_input_field_error:err_gift_reg_input_field_error"
	}
	
	def"doCoRegLinking.TC system exception when error id is 900  while getting Search response"(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> null
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true  && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(true)
		errorVO1.setErrorDisplayMessage("ex")
		errorVO1.setErrorId(900)
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_gift_reg_fatal_error:err_gift_reg_fatal_error"
	}
	
	def"doCoRegLinking.TC system exception when error id is 902  while getting Search response"(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> null
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true  && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(true)
		errorVO1.setErrorDisplayMessage("ex")
		errorVO1.setErrorId(902)
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_gift_reg_invalid_input_format:err_gift_reg_invalid_input_format"
	}
	
	
	def"doCoRegLinking.TC system exception when error id is 901  while getting Search response"(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> null
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true  && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(true)
		errorVO1.setErrorDisplayMessage("ex")
		errorVO1.setErrorId(901)
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_gift_reg_siteflag_usertoken_error:err_gift_reg_siteflag_usertoken_error"
	}
	
	
	def"doCoRegLinking.TC fro BBBSystemException while getting linkCoRegProfileToReg when error id is 200"(){
		given:
		ServiceErrorVO errorVO = exceptionCodeFroLinkCoReg()
		
		// convertRegSummaryVOToRegistryVO
		errorVO.setErrorId(200)
		errorVO.setErrorDisplayMessage("ex")
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_gift_reg_input_field_error:err_gift_reg_input_field_error"
	}
	
	def"doCoRegLinking.TC fro BBBSystemException while getting linkCoRegProfileToReg when error id is 900"(){
		given:
		ServiceErrorVO errorVO = exceptionCodeFroLinkCoReg()
		
		// convertRegSummaryVOToRegistryVO
		errorVO.setErrorId(900)
		errorVO.setErrorDisplayMessage("ex")
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_gift_reg_fatal_error:err_gift_reg_fatal_error"
	}
	
	def"doCoRegLinking.TC fro BBBSystemException while getting linkCoRegProfileToReg when error id is 901"(){
		given:
		ServiceErrorVO errorVO = exceptionCodeFroLinkCoReg()
		
		// convertRegSummaryVOToRegistryVO
		errorVO.setErrorId(901)
		errorVO.setErrorDisplayMessage("ex")
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_gift_reg_siteflag_usertoken_error:err_gift_reg_siteflag_usertoken_error"
	}
	
	def"doCoRegLinking.TC fro BBBSystemException while getting linkCoRegProfileToReg when error id is 902"(){
		given:
		ServiceErrorVO errorVO = exceptionCodeFroLinkCoReg()
		
		// convertRegSummaryVOToRegistryVO
		errorVO.setErrorId(902)
		errorVO.setErrorDisplayMessage("ex")
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		BBBSystemException exception = thrown()
		exception.getMessage()=="err_gift_reg_invalid_input_format:err_gift_reg_invalid_input_format"
	}
	
	def"doCoRegLinking.TC fro BBBSystemException while getting linkCoRegProfileToReg when error id is wrong"(){
		given:
		ServiceErrorVO errorVO = exceptionCodeFroLinkCoReg()
		
		// convertRegSummaryVOToRegistryVO
		errorVO.setErrorId(90255)
		errorVO.setErrorDisplayMessage("ex")
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		!value
	}

	private ServiceErrorVO exceptionCodeFroLinkCoReg() {
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		RegistryResVO linkCoRegToRegistries = new RegistryResVO()
		List sVos = [rSummeryVO]
		ServiceErrorVO errorVO = new ServiceErrorVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()

		RegistryTypes registryType = Mock()
		//RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()

		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> null
		2*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true  && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO

		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(false)

		regSearchResVO.setListRegistrySummaryVO(sVos)
		profileMock.getRepositoryId() >> "pId"

		1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []

		1*grTools.linkCoRegProfileToReg(_, false) >> linkCoRegToRegistries

		linkCoRegToRegistries.setServiceErrorVO(errorVO)
		errorVO.setErrorExists(true)
		return errorVO
	}
	
	
	def"doCoRegLinking.TC when linkedRegistriesVOList is empty"(){
		given:
		setSpy()
		RegSearchResVO regSearchResVO = new RegSearchResVO()
		RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		RegistryResVO linkCoRegToRegistries = new RegistryResVO()
		List sVos = []
		ServiceErrorVO errorVO = new ServiceErrorVO()
		ServiceErrorVO errorVO1 = new ServiceErrorVO()
		
		RegistryTypes registryType = Mock()
		//RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
		
		2*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "CA") >> ["usBed"]
		1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
		1*grManager.searchRegistries({RegistrySearchVO vo -> vo.email=='ha@gmail.com' && vo.returnLeagacyRegistries==true && vo.siteId== 'usBed' && vo.userToken=='ut' && vo.serviceName=='serviceName'},_) >> regSearchResVO
		
		regSearchResVO.setServiceErrorVO(errorVO1)
		errorVO1.setErrorExists(false)
		
		regSearchResVO.setListRegistrySummaryVO(sVos)
		
		when:
		boolean value = grManager.doCoRegLinking("ha@gmail.com", profileMock, "CA" , requestMock)

		then:
		!value
		0*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall")
	}
	

	private convertRegSummaryVOToRegistryVO(RegistrySummaryVO rSummeryVO) {
		rSummeryVO.setCoRegistrantFirstName(null)
		rSummeryVO.setCoRegistrantLastName(null)

		rSummeryVO.setPrimaryRegistrantFirstName("pFName")
		rSummeryVO.setPrimaryRegistrantLastName("pLName")

		rSummeryVO.setEventDate("02/02/2020")
		rSummeryVO.setRegistryId("rId")

		rSummeryVO.setEventCode(null)
		rSummeryVO.setRegistryType(null)
	}
	
	
	/*********************************************************  fetchUsersSoonestRegistry ********************************************/
	
	def"fetchUsersSoonestRegistry.TC to fetch users soonest or recent"(){
		given:
			setSpy()
			RegistryResVO registryResVO = new RegistryResVO() 
			RegistryResVO registryResVO1 = new RegistryResVO()
			RegistryResVO registryResVO2 = new RegistryResVO()
			RegistryResVO registryResVO3 = new RegistryResVO()
			
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
			RegistrySummaryVO rSummeryVO2 = new RegistrySummaryVO()
			
			List rIds = ["rId1","rId2","rId3", "rId4", "rId5"]
			
			5*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["usBed"]
			5*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
	
			///getRegistryInfoFromWSorDB
			5*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> ["true"]
			1*grTools.getRegistryInfo({RegistryReqVO revo -> revo.registryId=='rId1'  && revo.siteId=='usBed' && revo.userToken=='ut'}) >> registryResVO 
			1*grTools.getRegistryInfo({RegistryReqVO revo -> revo.registryId=='rId2'  && revo.siteId=='usBed' && revo.userToken=='ut'}) >> registryResVO1
			1*grTools.getRegistryInfo({RegistryReqVO revo -> revo.registryId=='rId3'  && revo.siteId=='usBed' && revo.userToken=='ut'}) >> registryResVO2
			1*grTools.getRegistryInfo({RegistryReqVO revo -> revo.registryId=='rId4'  && revo.siteId=='usBed' && revo.userToken=='ut'}) >> registryResVO3
			1*grTools.getRegistryInfo({RegistryReqVO revo -> revo.registryId=='rId5'  && revo.siteId=='usBed' && revo.userToken=='ut'}) >> null
			//end
			registryResVO.setRegistrySummaryVO(rSummeryVO)
			registryResVO1.setRegistrySummaryVO(rSummeryVO1)
			registryResVO2.setRegistrySummaryVO(rSummeryVO2)
			registryResVO3.setRegistrySummaryVO(null)
			
			rSummeryVO.setEventDate("12/10/2014")
			rSummeryVO1.setEventDate("12/10/2014")
			rSummeryVO2.setEventDate(null)
			
			rSummeryVO.setFutureShippingDate("10/10/2015")
			rSummeryVO1.setFutureShippingDate(null)
			
			1*grManager.getDateDiff("BedBathCanada", rSummeryVO) >> 0 
			1*grManager.getDateDiff("BedBathCanada", rSummeryVO1) >> -100
			
			1*grTools.fetchUsersSoonestOrRecent(["rId1"]) >> "returnValue"
		when:
			String value = grManager.fetchUsersSoonestRegistry(rIds, "BedBathCanada")
		then:
			rSummeryVO.getEventDate() == "12/10/2014"
			rSummeryVO.getFutureShippingDate() == "10/10/2015"
			value == "returnValue"
	}
	
	def"fetchUsersSoonestRegistry.TC to fetch users soonest or recent when site is not BedBathCanada "(){
		given:
			setSpy()
			RegistryResVO registryResVO = new RegistryResVO()
			
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			
			List rIds = ["rId1"]
			
			1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "usBed") >> ["usBed"]
			1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
	
			///getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> ["true"]
			1*grTools.getRegistryInfo({RegistryReqVO revo -> revo.registryId=='rId1'  && revo.siteId=='usBed' && revo.userToken=='ut'}) >> registryResVO
			//end
			registryResVO.setRegistrySummaryVO(rSummeryVO)
			
			rSummeryVO.setEventDate("12/10/2014")
			1*grManager.getDateDiff("usBed", rSummeryVO) >> -100
			
		when:
			String value = grManager.fetchUsersSoonestRegistry(rIds, "usBed")
		then:
			rSummeryVO.getEventDate() == "12/10/2014"
			rSummeryVO.getFutureShippingDate() == null
			value == null
			0*grTools.fetchUsersSoonestOrRecent(_)
	}
	
	def"fetchUsersSoonestRegistry.TC for ParseException "(){
		given:
			setSpy()
			RegistryResVO registryResVO = new RegistryResVO()
			
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			
			List rIds = ["rId1"]
			
			1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "usBed") >> ["usBed"]
			1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["ut"]
	
			///getRegistryInfoFromWSorDB
			1*catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> ["true"]
			1*grTools.getRegistryInfo({RegistryReqVO revo -> revo.registryId=='rId1'  && revo.siteId=='usBed' && revo.userToken=='ut'}) >> registryResVO
			//end
			registryResVO.setRegistrySummaryVO(rSummeryVO)
			
			rSummeryVO.setEventDate("12/10/2014")
			1*grManager.getDateDiff("usBed", rSummeryVO) >> {throw new ParseException("exception",1)}
			
		when:
			String value = grManager.fetchUsersSoonestRegistry(rIds, "usBed")
		then:
			RepositoryException	 exe = thrown()
}
	
	def"fetchUsersSoonestRegistry.TC for BBBSystemException "(){
		given:
			setSpy()
			RegistryResVO registryResVO = new RegistryResVO()
			
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			
			List rIds = ["rId1"]
			
			1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "usBed") >> {throw new BBBSystemException("exception")}
		
		when:
			String value = grManager.fetchUsersSoonestRegistry(rIds, "usBed")
		then:
			RepositoryException	 exe = thrown()
			1*grManager.logError("Error in fetchUsersSoonestRegistry",_)
}
	
	def"fetchUsersSoonestRegistry.TC for BBBBusinessException "(){
		given:
			setSpy()
			RegistryResVO registryResVO = new RegistryResVO()
			
			RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
			
			List rIds = ["rId1"]
			
			1*catTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "usBed") >> {throw new BBBBusinessException("exception")}
		
		when:
			String value = grManager.fetchUsersSoonestRegistry(rIds, "usBed")
		then:
			RepositoryException	 exe = thrown()
			1*grManager.logError("Error in fetchUsersSoonestRegistry",_)
}
	

	/*****************************************************  updateRegistrantProfileInfo ***************************************/
	
	def "updateRegistrantProfileInfo. Tc to  update registrant porfile information"(){
		given:
		RegistryVO pRegistryVO = new RegistryVO()
		ShippingVO sAddressVO = new ShippingVO()
		AddressVO address1 = new AddressVO()
		AddressVO cAddress = new AddressVO()
		AddressVO fuSAddress = new AddressVO()
		
		RepositoryItem addressItem = Mock()
		
		RegistrantVO registrantVO = Mock()
		
		pRegistryVO.setShipping(sAddressVO)
		sAddressVO.setShippingAddress(address1)
		address1.setAddressLine1("3Street")
		
		address1.setFirstName("ar")
		address1.setLastName("john")
		address1.setAddressLine2("Ney ")
		address1.setCity("delhi")
		address1.setState("delhi")
		address1.setZip("10024")
		address1.setPoBoxAddress(true)
		address1.setQasValidated(true)
		
		3*cTools.getAllAvailableAddresses(profileMock) >> [addressItem] >> [] >> []
		//1*addressApi.addNewShippingAddress(profileMock, {Address avo -> avo.firstName=='ar' && avo.lastName=='john' && avo.address2=='Ney' && avo.city=='delhi' && avo.state=='delhi' && avo.poBoxAddress==true && avo.qasValidated==true}, _, false, false) 
		//1*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		
		pRegistryVO.setPrimaryRegistrant(registrantVO)
		registrantVO.getAddressSelected() >> "newPrimaryRegAddress"
		registrantVO.getContactAddress() >> cAddress
		
		cAddress.setAddressLine1("3Street")
		
		cAddress.setFirstName("ar1")
		cAddress.setLastName("john1")
		cAddress.setAddressLine2("Ney1")
		cAddress.setCity("delhi")
		cAddress.setState("delhi")
		cAddress.setZip("10024")
		cAddress.setPoBoxAddress(true)
		cAddress.setQasValidated(true)
	
		//1*addressApi.addNewShippingAddress(profileMock, {BBBAddressVO avo -> avo.firstName=='ar1' && avo.lastName=='john1' && avo.address2=='Ney1' && avo.city=='delhi' && avo.state=='delhi' && avo.poBoxAddress==true && avo.qasValidated==true}, _, true, true)
		//2*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		
		sAddressVO.setFutureshippingAddress(fuSAddress)
		
		fuSAddress.setAddressLine1("3Street")
		
		fuSAddress.setFirstName("ar2")
		fuSAddress.setLastName("john2")
		fuSAddress.setAddressLine2("Ney2")
		fuSAddress.setCity("delhi")
		fuSAddress.setState("delhi")
		fuSAddress.setZip("10024")
		fuSAddress.setPoBoxAddress(true)
		fuSAddress.setQasValidated(true)

		//1*addressApi.addNewShippingAddress(profileMock, {BBBAddressVO avo -> avo.firstName=='ar2' && avo.lastName=='john2' && avo.address2=='Ney2' && avo.city=='delhi' && avo.state=='delhi' && avo.poBoxAddress==true && avo.qasValidated==true}, _, true, true)
		when:
		
		grManager.updateRegistrantProfileInfo(pRegistryVO, "newShippingAddress", "newFutureShippingAddress", profileMock, "CA")
		then:
		2*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		1*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		
	}	
	
	def "updateRegistrantProfileInfo. Tc to  update registrant porfile information when primary registrant is oldRegistrantAddressFromWS"(){
		given:
		RegistryVO pRegistryVO = new RegistryVO()
		ShippingVO sAddressVO = new ShippingVO()
		AddressVO address1 = new AddressVO()
		AddressVO cAddress = new AddressVO()
		AddressVO fuSAddress = new AddressVO()
		
		RepositoryItem addressItem = Mock()
		
		RegistrantVO registrantVO = Mock()
		
		pRegistryVO.setShipping(sAddressVO)
		sAddressVO.setShippingAddress(address1)
		address1.setAddressLine1("add")
		
		address1.setFirstName("")
		address1.setLastName("")
		address1.setAddressLine2(" ")
		address1.setCity("")
		address1.setState("")
		address1.setZip("")
		address1.setPoBoxAddress(true)
		address1.setQasValidated(true)
		
		3*cTools.getAllAvailableAddresses(profileMock) >> [] >> [addressItem] >> [addressItem]
		//1*addressApi.addNewShippingAddress(profileMock, {Address avo -> avo.firstName=='ar' && avo.lastName=='john' && avo.address2=='Ney' && avo.city=='delhi' && avo.state=='delhi' && avo.poBoxAddress==true && avo.qasValidated==true}, _, false, false)
		//1*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		
		pRegistryVO.setPrimaryRegistrant(registrantVO)
		registrantVO.getAddressSelected() >> "oldRegistrantAddressFromWS"
		registrantVO.getContactAddress() >> cAddress
		
		cAddress.setAddressLine1("3Street")
		
		cAddress.setFirstName("ar1")
		cAddress.setLastName("john1")
		cAddress.setAddressLine2("Ney1")
		cAddress.setCity("delhi")
		cAddress.setState("delhi")
		cAddress.setZip("10024")
		cAddress.setPoBoxAddress(true)
		cAddress.setQasValidated(true)
	
		//1*addressApi.addNewShippingAddress(profileMock, {BBBAddressVO avo -> avo.firstName=='ar1' && avo.lastName=='john1' && avo.address2=='Ney1' && avo.city=='delhi' && avo.state=='delhi' && avo.poBoxAddress==true && avo.qasValidated==true}, _, true, true)
		//2*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		
		sAddressVO.setFutureshippingAddress(fuSAddress)
		
		fuSAddress.setAddressLine1("3Street")
		
		fuSAddress.setFirstName("ar2")
		fuSAddress.setLastName("john2")
		fuSAddress.setAddressLine2("Ney2")
		fuSAddress.setCity("delhi")
		fuSAddress.setState("delhi")
		fuSAddress.setZip("10024")
		fuSAddress.setPoBoxAddress(true)
		fuSAddress.setQasValidated(true)

		//1*addressApi.addNewShippingAddress(profileMock, {BBBAddressVO avo -> avo.firstName=='ar2' && avo.lastName=='john2' && avo.address2=='Ney2' && avo.city=='delhi' && avo.state=='delhi' && avo.poBoxAddress==true && avo.qasValidated==true}, _, true, true)
		when:
		
		grManager.updateRegistrantProfileInfo(pRegistryVO, "oldShippingAddressFromWS", "oldFutShippingAddressFromWS", profileMock, "CA")
		then:
		1*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		2*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		
	}
	
	
	def "updateRegistrantProfileInfo. Tc to  update registrant porfile information when primary registrant is oldRegistrantAddressFromW"(){
		given:
		RegistryVO pRegistryVO = new RegistryVO()
		ShippingVO sAddressVO = new ShippingVO()
		AddressVO address1 = new AddressVO()
		AddressVO cAddress = new AddressVO()
		AddressVO fuSAddress = new AddressVO()
		
		RepositoryItem addressItem = Mock()
		
		RegistrantVO registrantVO = Mock()
		
		pRegistryVO.setShipping(sAddressVO)
		sAddressVO.setShippingAddress(address1)
		address1.setAddressLine1("")
				
		1*cTools.getAllAvailableAddresses(profileMock) >> [] 
		
		pRegistryVO.setPrimaryRegistrant(registrantVO)
		registrantVO.getAddressSelected() >> "oldRegistrantAddressFromWS"
		registrantVO.getContactAddress() >> cAddress
		
		cAddress.setAddressLine1("3Street")
		
	
		
		sAddressVO.setFutureshippingAddress(fuSAddress)
		
		fuSAddress.setAddressLine1("3Street")
		
		fuSAddress.setAddressLine2("Ney2")
		fuSAddress.setCity("")

		when:
		
		grManager.updateRegistrantProfileInfo(pRegistryVO, "oldShippingAddressFromWS", "oldFutShippingAddressFromWS", profileMock, "CA")
		then:
		//1*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		1*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		
	}
	
	def "updateRegistrantProfileInfo. Tc to  update registrant porfile information when primary registrant address Item  is null"(){
		given:
		RegistryVO pRegistryVO = new RegistryVO()
		ShippingVO sAddressVO = new ShippingVO()
		AddressVO address1 = new AddressVO()
		AddressVO cAddress = new AddressVO()
		AddressVO fuSAddress = new AddressVO()
		
		RepositoryItem addressItem = Mock()
		
		RegistrantVO registrantVO = Mock()
		
		pRegistryVO.setShipping(sAddressVO)
		sAddressVO.setShippingAddress(address1)
		address1.setAddressLine1(null)
				
		1*cTools.getAllAvailableAddresses(profileMock) >> null 
		
		pRegistryVO.setPrimaryRegistrant(registrantVO)
		registrantVO.getAddressSelected() >> "oldRegistrantAddressFromWS"
		registrantVO.getContactAddress() >> cAddress
		
		cAddress.setAddressLine1("3Street")
		
	
		
		sAddressVO.setFutureshippingAddress(fuSAddress)
		
		fuSAddress.setAddressLine1("")
		

		when:
		
		grManager.updateRegistrantProfileInfo(pRegistryVO, "oldShippingAddressFromWS", "oldFutShippingAddressFromWS", profileMock, "CA")
		then:
		//1*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		1*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		
	}
	
	def "updateRegistrantProfileInfo. Tc to  update registrant porfile information when address line1 of future shipping address is null "(){
		given:
		RegistryVO pRegistryVO = new RegistryVO()
		ShippingVO sAddressVO = new ShippingVO()
		AddressVO address1 = new AddressVO()
		AddressVO cAddress = new AddressVO()
		AddressVO fuSAddress = new AddressVO()
		
		RepositoryItem addressItem = Mock()
		
		RegistrantVO registrantVO = Mock()
		
			
		
		pRegistryVO.setPrimaryRegistrant(registrantVO)
		registrantVO.getAddressSelected() >> "oldRegistrantAddressFromWS"
		registrantVO.getContactAddress() >> cAddress
		
		cAddress.setAddressLine1("")
		
	
		
		sAddressVO.setFutureshippingAddress(fuSAddress)
		
		fuSAddress.setAddressLine1(null)
		

		when:
		
		grManager.updateRegistrantProfileInfo(pRegistryVO, "wrong", "oldFutShippingAddressFromWS", profileMock, "CA")
		then:
		0*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		0*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		0*cTools.getAllAvailableAddresses(profileMock)
		
	}
	
	def "updateRegistrantProfileInfo. Tc to  update registrant porfile information when address line1 of future shipping address is not old or new address "(){
		given:
		RegistryVO pRegistryVO = new RegistryVO()
		AddressVO cAddress = new AddressVO()
		
		RepositoryItem addressItem = Mock()
		
		RegistrantVO registrantVO = Mock()
				
		
		pRegistryVO.setPrimaryRegistrant(registrantVO)
		registrantVO.getAddressSelected() >> "oldRegistrantAddressFromWS"
		registrantVO.getContactAddress() >> cAddress
		
		cAddress.setAddressLine1(null)
				

		when:
		
		grManager.updateRegistrantProfileInfo(pRegistryVO, "wrong", "weong", profileMock, "CA")
		then:
		0*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		0*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		0*cTools.getAllAvailableAddresses(profileMock)
		
	}
	
	def "updateRegistrantProfileInfo. Tc to  update registrant porfile information when selected address of primary registrant is null "(){
		given:
		RegistryVO pRegistryVO = new RegistryVO()
		AddressVO cAddress = new AddressVO()
		
		RepositoryItem addressItem = Mock()
		
		RegistrantVO registrantVO = Mock()
				
		
		pRegistryVO.setPrimaryRegistrant(registrantVO)
		registrantVO.getAddressSelected() >> null
		registrantVO.getContactAddress() >> cAddress
		
		cAddress.setAddressLine1(null)
				

		when:
		
		grManager.updateRegistrantProfileInfo(pRegistryVO, "wrong", "weong", profileMock, "CA")
		then:
		0*addressApi.addNewShippingAddress(profileMock, _, _, true, true)
		0*addressApi.addNewShippingAddress(profileMock, _, _, false, false)
		0*cTools.getAllAvailableAddresses(profileMock)
		
	}
	
	
	/********************************************* sendEmailToCoregistrant ******************************************/
	def"sendEmailToCoregistrant.Tc to send email to co Registrant"(){
		given:
			setSpy()
			RegistryVO registryVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			RegistrantVO coRegistrantVO = new RegistrantVO()
			
			grManager.getUniquekeyDate() >> 10
			
			requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
			profileMock.getRepositoryId() >> "user1"
			
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(coRegistrantVO)
			
			registrantVO.setFirstName("ar2")
			registrantVO.setLastName("john2")
			
			coRegistrantVO.setFirstName("ha")
			coRegistrantVO.setLastName("si")
			
			registryVO.setRegistryType(rType)
			rType.getRegistryTypeName() >> "regName"
			
			catTools.getRegistryTypeName("regName","usBed") >> "B"
			//catTools.getRegistryTypeName(registryVO.getRegistryType().getRegistryTypeName(),siteId)
			
			registryVO.setRegistryId("reg123")
			
			registrantVO.setEmail("test@gmail.com")
			coRegistrantVO.setEmail("test1@gmail.com")
			1 * temlateInfoMock.setTemplateParameters(['templateUrl':'/store.jspg', 'placeHolderValues':['emailType':'coFRegType', 'coRegFirstName':'ha', 'frmData_siteId':'usBed', 'registry_Id_key':'reg123', 'coRegLastName':'si','emailPersistId':'user110', 'account_login_url':'accountUrl', 'configurableType':'B', 'pRegFirstName':'ar2', 'registry_guest_url':'rUrlB', 'pRegLastName':'john2'], 'siteId':'usBed', 'subject':'infoEmail', 'recipientEmail':'test1@gmail.com', 'senderEmail':'test@gmail.com'])
			
		when:
			grManager.sendEmailToCoregistrant("rUrl", "accountUrl", "usBed", "infoEmail", registryVO, "true", "true", temlateInfoMock)
		then:
		
		1 * temlateInfoMock.setTemplateURL('/store.jspg')
		1 * temlateInfoMock.setMessageFrom('test@gmail.com')
		1 * temlateInfoMock.setSiteId('usBed')
		1 * temlateInfoMock.setMessageSubject('infoEmail')
		
		
	}
	
	def"sendEmailToCoregistrant.Tc to send email to co Registrant when CoRegEmailFoundPopupStatus is false and coEmailNotFoundPopup is true"(){
		given:
			setSpy()
			RegistryVO registryVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			RegistrantVO coRegistrantVO = new RegistrantVO()
			
			grManager.getUniquekeyDate() >> 10
			
			requestMock.resolveName("/atg/userprofiling/Profile") >> null
			profileMock.getRepositoryId() >> "user1"
			
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(coRegistrantVO)
			
			registrantVO.setFirstName("ar2")
			registrantVO.setLastName("john2")
			
			coRegistrantVO.setFirstName("ha")
			coRegistrantVO.setLastName("si")
			
			registryVO.setRegistryType(rType)
			rType.getRegistryTypeName() >> "regName"
			
			catTools.getRegistryTypeName("regName","usBed") >> "B"
			
			registryVO.setRegistryId("reg123")
			
			registrantVO.setEmail("test@gmail.com")
			coRegistrantVO.setEmail("test1@gmail.com")
			
			
		when:
			grManager.sendEmailToCoregistrant("rUrl", "accountUrl", "usBed", "infoEmail", registryVO, "false", "true", temlateInfoMock)
		then:
			//1 * temlateInfoMock.setTemplateParameters(['templateUrl':'/store.jspg', 'placeHolderValues':['emailType':'coFRegType', 'coRegFirstName':'ha', 'frmData_siteId':'usBed', 'registry_Id_key':'reg123', 'coRegLastName':'si', _, 'account_login_url':'accountUrl', 'configurableType':'B', 'pRegFirstName':'ar2', 'registry_guest_url':'rUrlB', 'pRegLastName':'john2'], 'siteId':'usBed', 'subject':'infoEmail', 'recipientEmail':'test1@gmail.com', 'senderEmail':'test@gmail.com'])
		1 * temlateInfoMock.setTemplateURL('/store.jspg')
		1 * temlateInfoMock.setMessageFrom('test@gmail.com')
		1 * temlateInfoMock.setSiteId('usBed')
		1 * temlateInfoMock.setMessageSubject('infoEmail')
		1 * temlateInfoMock.setTemplateParameters(['templateUrl':'/store.jspg', 'placeHolderValues':['emailType':null, 'coRegFirstName':'ha', 'frmData_siteId':'usBed', 'registry_Id_key':'reg123', 'coRegLastName':'si', 'emailPersistId':'null10', 'account_login_url':'accountUrl', 'configurableType':'B', 'pRegFirstName':'ar2', 'registry_guest_url':'rUrlB', 'pRegLastName':'john2'], 'siteId':'usBed', 'subject':'infoEmail', 'recipientEmail':'test1@gmail.com', 'senderEmail':'test@gmail.com'])
		
	}
	
	def"sendEmailToCoregistrant.Tc to send email to co Registrant when CoRegEmailFoundPopupStatus is false and coEmail is empty "(){
		given:
			RegistryVO registryVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			RegistrantVO coRegistrantVO = new RegistrantVO()
			
			requestMock.resolveName("/atg/userprofiling/Profile") >> null
			profileMock.getRepositoryId() >> "user1"
			
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(coRegistrantVO)
			
			registrantVO.setFirstName("ar2")
			registrantVO.setLastName("john2")
			
			coRegistrantVO.setFirstName("ha")
			coRegistrantVO.setLastName("si")
			
			registryVO.setRegistryType(rType)
			rType.getRegistryTypeName() >> "regName"
			
			catTools.getRegistryTypeName("regName","usBed") >> "B"
			
			registryVO.setRegistryId("reg123")
			
			registrantVO.setEmail("test@gmail.com")
			coRegistrantVO.setEmail("")
			
			
		when:
			grManager.sendEmailToCoregistrant("rUrl", "accountUrl", "usBed", "infoEmail", registryVO, "false", "true", temlateInfoMock)
		then:
			//1 * temlateInfoMock.setTemplateParameters(['templateUrl':'/store.jspg', 'placeHolderValues':['emailType':'coFRegType', 'coRegFirstName':'ha', 'frmData_siteId':'usBed', 'registry_Id_key':'reg123', 'coRegLastName':'si', _, 'account_login_url':'accountUrl', 'configurableType':'B', 'pRegFirstName':'ar2', 'registry_guest_url':'rUrlB', 'pRegLastName':'john2'], 'siteId':'usBed', 'subject':'infoEmail', 'recipientEmail':'test1@gmail.com', 'senderEmail':'test@gmail.com'])
		0 * temlateInfoMock.setTemplateURL('/store.jsp')
		0 * temlateInfoMock.setMessageFrom('test@gmail.com')
		0 * temlateInfoMock.setSiteId('usBed')
		0 * temlateInfoMock.setMessageSubject('infoEmail')
		
	}
	
	def"sendEmailToCoregistrant.Tc to send email to co Registrant when CoRegEmailFoundPopupStatus is false  and coEmailNotFoundPopup is false "(){
		given:
			RegistryVO registryVO = new RegistryVO()
			RegistrantVO registrantVO = new RegistrantVO()
			RegistrantVO coRegistrantVO = new RegistrantVO()
			
			requestMock.resolveName("/atg/userprofiling/Profile") >> null
			profileMock.getRepositoryId() >> "user1"
			
			registryVO.setPrimaryRegistrant(registrantVO)
			registryVO.setCoRegistrant(coRegistrantVO)
			
			registrantVO.setFirstName("ar2")
			registrantVO.setLastName("john2")
			
			coRegistrantVO.setFirstName("ha")
			coRegistrantVO.setLastName("si")
			
			registryVO.setRegistryType(rType)
			rType.getRegistryTypeName() >> "regName"
			
			catTools.getRegistryTypeName("regName","usBed") >> "B"
			
			registryVO.setRegistryId("reg123")
			
			registrantVO.setEmail("test@gmail.com")
			coRegistrantVO.setEmail("")
			
			
		when:
			grManager.sendEmailToCoregistrant("rUrl", "accountUrl", "usBed", "infoEmail", registryVO, "false", "false", temlateInfoMock)
		then:
			
		0 * temlateInfoMock.setTemplateURL('/store.jsp')
		0 * temlateInfoMock.setMessageFrom('test@gmail.com')
		0 * temlateInfoMock.setSiteId('usBed')
		0 * temlateInfoMock.setMessageSubject('infoEmail')
		
	}

	/******************************************** removeUpdateGiftRegistryItem *********************************/
	
	def"removeUpdateGiftRegistryItem"(){
		given:
			ManageRegItemsResVO mageItemsResVO = new ManageRegItemsResVO() 
			catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> ["true"]
			1*grTools.removeUpdateGiftRegistryItem(grViewBean) >> mageItemsResVO
			mageItemsResVO.setOperationStatus(true)
		when:
			ManageRegItemsResVO value = grManager.removeUpdateGiftRegistryItem(profileMock, grViewBean)
		then:
			value.isOperationStatus() 
	}
	
	def"removeUpdateGiftRegistryItem.Tc when regItemsWSCall is false"(){
		given:
			ManageRegItemsResVO mageItemsResVO = new ManageRegItemsResVO()
			catTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall") >> []
			1*grTools.removeUpdateGiftRegistryItemInEcomAdmin(grViewBean) >> mageItemsResVO
			mageItemsResVO.setOperationStatus(true)
		when:
			ManageRegItemsResVO value = grManager.removeUpdateGiftRegistryItem(profileMock, grViewBean)
		then:
			value.isOperationStatus()
	}
	
	
/************************************************ sendEmailRegistry *********************************************/
	
	def"sendEmailRegistry.TC to send email for registry"(){
		given:
		setSpy()
		Map<String, String> value = ["registryURL": "rgUrl","pRegFirstName":"ha","pRegLastName":"si","coRegFirstName":"coname","coRegLastName":"colst","user_message":"provideInfo","pRegFirstName":"regFName","pRegLastName":"regLas","eventTypeRegistry":"even","eventDate":"14/12","daysToGo":"10","configurableType":"ctype","dateLabel":"lable","message":"mregis","senderEmail":"test@g.com","recipientEmail":"rec@gmail.com","subject":"info"]                  
		
		grManager.getUniquekeyDate() >> 10
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		profileMock.getRepositoryId() >> "user1"
		temlateInfoMock.getMessageFrom() >> "harr@gmail.com"

		
		when:
		boolean result = grManager.sendEmailRegistry("usBed", value, temlateInfoMock)
		
		then:
		result
		1 * temlateInfoMock.setTemplateParameters(['templateUrl':'/store.jspg', 'placeHolderValues':['eventDate':'14/12', 'frmData_siteId':'usBed', 'emailPersistId':'user110', 'configurableType':'ctype', 'pRegFirstName':'regFName', 'all_names_key':'regFName regLas &amp; coname colst', 'registryURL':'rgUrl', 'pRegLastName':'regLas', 'user_message':'provideInfo', 'coRegFirstName':'coname', 'emailType':'emlAType', 'first_names_key':'regFName and coname', 'eventTypeRegistry':'even', 'daysToGo':'10', 'coRegLastName':'colst', 'frmData_commentsMessage':'mregis', 'dateLabel':'lable'], 'subject':'info', 'recipientEmail':'harr@gmail.com', 'senderEmail':'test@g.com'])
		1 * temlateInfoMock.setMessageTo('harr@gmail.com')
		
		}	
	
	def"sendEmailRegistry.TC to send email for registry when coregFirstname is empty"(){
		given:
		setSpy()
		Map<String, String> value = ["registryURL": "rgUrl","pRegFirstName":"ha","pRegLastName":"si","coRegFirstName":"","coRegLastName":"colst","user_message":"provideInfo","pRegFirstName":"regFName","pRegLastName":"regLas","eventTypeRegistry":"even","eventDate":"14/12","configurableType":"ctype","dateLabel":"lable","message":"mregis","senderEmail":"test@g.com","recipientEmail":""]
		value.put("daysToGo", null)
		value.put("messageCC","true")
		value.put("subject", null)
		
		
		grManager.getUniquekeyDate() >> 10
		requestMock.resolveName("/atg/userprofiling/Profile") >> null
		profileMock.getRepositoryId() >> "user1"
		temlateInfoMock.getMessageFrom() >> "harr@gmail.com"

		emailSenderMock.sendEmailMessage(_, _, true, false) >> {throw new TemplateEmailException("exce")}
		when:
		boolean result = grManager.sendEmailRegistry("usBed", value, temlateInfoMock)
		
		then:
		!result
		1 * temlateInfoMock.setTemplateParameters(['templateUrl':'/store.jspg', 'placeHolderValues':['eventDate':'14/12', 'frmData_siteId':'usBed', 'emailPersistId':'null10', 'configurableType':'ctype', 'pRegFirstName':'regFName', 'all_names_key':'regFName regLas', 'registryURL':'rgUrl', 'pRegLastName':'regLas', 'user_message':'provideInfo', 'coRegFirstName':'', 'emailType':null, 'first_names_key':'regFName', 'eventTypeRegistry':'even', 'daysToGo':null, 'coRegLastName':'colst', 'frmData_commentsMessage':'mregis', 'dateLabel':'lable'], 'recipientEmail':'harr@gmail.com', 'senderEmail':'test@g.com'])
		1 * temlateInfoMock.setMessageTo('harr@gmail.com')
		
		
		}
	
	def"sendEmailRegistry.TC for RepositoryException while sending email"(){
		given:
		setSpy()
		Map<String, String> value = ["registryURL": "rgUrl","pRegFirstName":"ha","pRegLastName":"si","coRegLastName":"colst","user_message":"provideInfo","pRegFirstName":"regFName","pRegLastName":"regLas","eventTypeRegistry":"even","eventDate":"14/12","configurableType":"ctype","dateLabel":"lable","message":"mregis","senderEmail":"test@g.com","recipientEmail":""]
		value.put("daysToGo", null)
		value.put("messageCC","true")
		value.put("subject", null)
		value.put("coRegFirstName", null)
		
		grManager.getUniquekeyDate() >> 10
		requestMock.resolveName("/atg/userprofiling/Profile") >> null
		profileMock.getRepositoryId() >> "user1"
		temlateInfoMock.getMessageFrom() >> "harr@gmail.com"

		emailSenderMock.sendEmailMessage(_, _, true, false) >> {throw new RepositoryException("exce")}
		when:
		boolean result = grManager.sendEmailRegistry("usBed", value, temlateInfoMock)
		
		then:
		!result
		1 * temlateInfoMock.setMessageTo('harr@gmail.com')
		
		}
	
	
	/**************************************************** sendEmailMxRegistry *****************************************/
	
	def"sendEmailMxRegistry.TC to send  email for registry"(){
		given:
		setSpy()
		Map<String, String> value = ["registryURL": "rgUrl","pRegFirstName":"ha","pRegLastName":"si","coRegFirstName":"coname","coRegLastName":"colst","user_message":"provideInfo","pRegFirstName":"regFName","pRegLastName":"regLas","eventDate":"14/12","daysToGo":"10","configurableType":"ctype","dateLabel":"lable","message":"mregis","senderEmail":"test@g.com","recipientEmail":"rec@gmail.com","subject":"info"]                  
		
		grManager.getUniquekeyDate() >> 10
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		profileMock.getRepositoryId() >> "user1"
		temlateInfoMock.getMessageFrom() >> "harr@gmail.com"

		
		when:
		boolean result = grManager.sendEmailMxRegistry("usBed", value, temlateInfoMock)
		
		then:
		result
		1 * temlateInfoMock.setTemplateParameters(['templateUrl':'/store.jspg', 'placeHolderValues':['eventDate':'14/12', 'frmData_siteId':'usBed', 'emailPersistId':'user110', 'configurableType':'ctype', 'pRegFirstName':'regFName', 'all_names_key':'regFName regLas &amp; coname colst', 'registryURL':'rgUrl', 'pRegLastName':'regLas', 'user_message':'provideInfo', 'coRegFirstName':'coname', 'emailType':'mxEmlAType', 'first_names_key':'regFName and coname', 'daysToGo':'10', 'coRegLastName':'colst', 'frmData_commentsMessage':'mregis', 'dateLabel':'lable'], 'subject':'info', 'recipientEmail':'harr@gmail.com', 'senderEmail':'test@g.com'])
		1 * temlateInfoMock.setMessageTo('harr@gmail.com')
		
		}
	
	def"sendEmailMxRegistry.TC to send email for registry when coregFirstname is empty"(){
		given:
		setSpy()
		Map<String, String> value = ["registryURL": "rgUrl","pRegFirstName":"ha","pRegLastName":"si","coRegFirstName":"","coRegLastName":"colst","user_message":"provideInfo","pRegFirstName":"regFName","pRegLastName":"regLas","eventDate":"14/12","configurableType":"ctype","dateLabel":"lable","message":"mregis","senderEmail":"test@g.com","recipientEmail":""]
		value.put("daysToGo", null)
		value.put("messageCC","true")
		value.put("subject", null)
		
		
		grManager.getUniquekeyDate() >> 10
		requestMock.resolveName("/atg/userprofiling/Profile") >> null
		profileMock.getRepositoryId() >> "user1"
		temlateInfoMock.getMessageFrom() >> "harr@gmail.com"

		emailSenderMock.sendEmailMessage(_, _, true, false) >> {throw new TemplateEmailException("exce")}
		when:
		boolean result = grManager.sendEmailMxRegistry("usBed", value, temlateInfoMock)
		
		then:
		!result
		1 * temlateInfoMock.setTemplateParameters(['templateUrl':'/store.jspg', 'placeHolderValues':['eventDate':'14/12', 'frmData_siteId':'usBed', 'emailPersistId':'null10', 'configurableType':'ctype', 'pRegFirstName':'regFName', 'all_names_key':'regFName regLas', 'registryURL':'rgUrl', 'pRegLastName':'regLas', 'user_message':'provideInfo', 'coRegFirstName':'', 'emailType':null, 'first_names_key':'regFName', 'daysToGo':null, 'coRegLastName':'colst', 'frmData_commentsMessage':'mregis', 'dateLabel':'lable'], 'recipientEmail':'harr@gmail.com', 'senderEmail':'test@g.com'])
		1 * temlateInfoMock.setMessageTo('harr@gmail.com')
		
		
		}
	
	def"sendEmailMxRegistry.TC for RepositoryException while sending email"(){
		given:
		setSpy()
		Map<String, String> value = ["registryURL": "rgUrl","pRegFirstName":"ha","pRegLastName":"si","coRegLastName":"colst","user_message":"provideInfo","pRegFirstName":"regFName","pRegLastName":"regLas","eventDate":"14/12","configurableType":"ctype","dateLabel":"lable","message":"mregis","senderEmail":"test@g.com","recipientEmail":""]
		value.put("daysToGo", null)
		value.put("messageCC","true")
		value.put("subject", null)
		value.put("coRegFirstName", null)
		
		grManager.getUniquekeyDate() >> 10
		requestMock.resolveName("/atg/userprofiling/Profile") >> null
		profileMock.getRepositoryId() >> "user1"
		temlateInfoMock.getMessageFrom() >> "harr@gmail.com"

		emailSenderMock.sendEmailMessage(_, _, true, false) >> {throw new RepositoryException("exce")}
		when:
		boolean result = grManager.sendEmailMxRegistry("usBed", value, temlateInfoMock)
		
		then:
		!result
		1 * temlateInfoMock.setMessageTo('harr@gmail.com')
		
		}
		
	
	
	private setSpy(){
		grManager = Spy()
		grManager.setCatalogTools(catTools)
		grManager.setGiftRegUtils(grUtilsMock)
		grManager.setGiftRegistryTools(grTools)
		grManager.setCatalogRepository(catRepository)
		grManager.setInventoryManager(invManagerMock)
		grManager.setBbbCatalogTools(catTools)
		grManager.setSearchRegistryServiceName("serviceName")
		grManager.setLinkCoRegProfileToRegServiceName("coSName")
		grManager.setTools(cTools)
		grManager.setTemplateUrl("/store.jspg")
		grManager.setEmailCoFoundRegistryType("coFRegType")
		grManager.setEmailSender(emailSenderMock)
		grManager.setEmailARegistryType("emlAType")
		grManager.setMxEmailARegistryType("mxEmlAType")
	}
}