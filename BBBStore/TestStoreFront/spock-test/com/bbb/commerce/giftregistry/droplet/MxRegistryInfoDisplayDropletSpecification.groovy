package com.bbb.commerce.giftregistry.droplet

import atg.multisite.Site
import atg.multisite.SiteContext
import atg.userprofiling.Profile
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.EventVO
import com.bbb.commerce.giftregistry.vo.RegistrantVO
import com.bbb.commerce.giftregistry.vo.RegistryResVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.BBBPropertyManager
import com.bbb.profile.session.BBBSessionBean

import spock.lang.specification.BBBExtendedSpec

class MxRegistryInfoDisplayDropletSpecification extends BBBExtendedSpec {

	MxRegistryInfoDisplayDroplet mridDroplet  
	SiteContext sContextMock = Mock()
	GiftRegistryManager grManager = Mock()
	Profile profileMock = Mock()
	BBBCatalogTools cToolsMock = Mock()
	RegistryResVO rResVO = new RegistryResVO()
	ServiceErrorVO sErrorVO = new ServiceErrorVO()
	
	RegistryVO registryVO = new RegistryVO()
	RegistrySummaryVO rSummaryVO = new RegistrySummaryVO()
	RegistrySummaryVO rSummaryVO1 = new RegistrySummaryVO()
	RegistrySummaryVO rSummaryVO2 = new RegistrySummaryVO()
	
	
	EventVO eventVO = new EventVO()
	RegistrantVO registrantVO = new RegistrantVO()
	RegistrantVO registrantVO1 = new RegistrantVO()
	
	Site site = Mock()
	RegistryTypes rType = Mock()
	BBBPropertyManager pManager = Mock()
	BBBSessionBean sBean = Mock()
	
	GiftRegSessionBean grBean = Mock()
	
	def setup(){
		mridDroplet = new MxRegistryInfoDisplayDroplet(siteContext : sContextMock, giftRegistryManager:grManager,profile :profileMock, catalogTools : cToolsMock, registryInfoServiceName : "registInfo", pmgr : pManager, giftRegSessionBean : grBean,regGuestViewURL : "rURL" )
	}
	
	def"service. TC to Fetch Registry Info from registry Id and display registry summary info"(){
		given:
		 def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		 def List<RegistrySummaryVO> ls = new ArrayList()
		 def List<RegistrySummaryVO> sls = new ArrayList()
		 ls.add(registrantVO1)
		 sls.add("rId")
		 map.put("registryIdList", ls)
		 map.put("userRegistriesList", sls)
		 
		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner"
	     sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 //grManager.isUserOwnRegistry(profileMock, "rId", requestMock) >> true
		 1*cToolsMock.getAllValuesForKey("WSDLKeys",_) >> ["tocken123"]
		 
		 1*grManager.getRegistryInfo(_) >> rResVO
		 
		rResVO.setServiceErrorVO(sErrorVO)
		sErrorVO.setErrorExists(false)
		rResVO.setRegistryVO(registryVO)
		
		rResVO.setRegistrySummaryVO(rSummaryVO)
		registryVO.setEvent(eventVO)
		
		registryVO.setPrimaryRegistrant(registrantVO)
		
		registrantVO.setEmail("harry@gmail.com")
		registrantVO.setPrimaryPhone("123456")
		registryVO.setPrefStoreNum("st12")
		
		rSummaryVO.setRegistryType(rType)
		(1.._)*rType.getRegistryTypeName() >> "rtName"
		
		1*grManager.canScheuleAppointment("st12", _, "enUS") >> true
		
		profileMock.isTransient() >> false
		
		1*pManager.getLoginPropertyName() >> "loginP"
		1*profileMock.getPropertyValue("loginP") >> "hars"
		
		registryVO.setCoRegistrant(registrantVO1) 
		registrantVO1.setEmail("hars")
		
		1*cToolsMock.getRegistryTypeName("rtName","enUS") >> "crType"
		
		rSummaryVO.setEventDate("20102014")
		rSummaryVO.setFutureShippingDate("20010203")
		
		1*grManager.getRegistryStatusFromRepo("enUS", "rId") >> "rStatus"
		1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "rStatus"
		
		//saveRegistryInfoToSession
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		sBean.getValues() >> map
		
		requestMock.getRequestURI() >> "view_ret.jsp"
		1*grManager.getRegistryInfo(_, "enUS") >> rSummaryVO2
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		when:
		mridDroplet.service(requestMock, responseMock)
		then:
		1 * requestMock.setParameter("validCheck", true)
		rSummaryVO.getPrimaryRegistrantEmail() == "harry@gmail.com"
		rSummaryVO.getPrimaryRegistrantPrimaryPhoneNum() == "123456"
		rSummaryVO.getFavStoreId() == "st12" 
		rSummaryVO.isAllowedToScheduleAStoreAppointment() == true
		rSummaryVO.getEventType() == "crType"
		rSummaryVO.getEventDate() == "20/14/2010"
		1*grBean.setRegistryOperation(_)
		
		1*requestMock.setParameter("coRegFlag",true)
		1*requestMock.setParameter("registryURL","rURL")
		1*requestMock.setParameter("eventDate", "20/14/2010")
		1*requestMock.setParameter("futureShippingDate", "20010203")
		1*requestMock.setParameter("registrySummaryVO",_)
		1*requestMock.setParameter("registryVO", _)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		map.containsKey("userRegistrysummaryVO")
		
	}
	
	
	def"service. TC to Fetch Registry Info from registry Id and display registry summery info when PrefStoreNum is empty"(){
		given:
		 def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		 def List<RegistrySummaryVO> ls = new ArrayList()
		 def List<RegistrySummaryVO> sls = new ArrayList()
		 ls.add(registrantVO1)
		 sls.add("rId")
		 map.put("registryIdList", ls)
		 map.put("userRegistriesList", sls)
		 
		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner"
		 sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 1*cToolsMock.getAllValuesForKey("WSDLKeys",_) >> ["tocken123"]
		 
		 1*grManager.getRegistryInfo(_) >> rResVO
		 
		rResVO.setServiceErrorVO(null)
		rResVO.setRegistryVO(registryVO)
		
		rResVO.setRegistrySummaryVO(rSummaryVO)
		registryVO.setEvent(eventVO)
		
		registryVO.setPrimaryRegistrant(registrantVO)
		
		registrantVO.setEmail("harry@gmail.com")
		registrantVO.setPrimaryPhone("123456")
		registryVO.setPrefStoreNum("")
		
		rSummaryVO.setRegistryType(rType)
		
		//1*grManager.canScheuleAppointment("st12", _, "enUS") >> true
		
		profileMock.isTransient() >> false
		
		1*pManager.getLoginPropertyName() >> "loginP"
		1*profileMock.getPropertyValue("loginP") >> "hars"
		
		registryVO.setCoRegistrant(registrantVO1)
		registrantVO1.setEmail("harr@gmail")
		
		
		rSummaryVO.setEventDate("20102014")
		rSummaryVO.setFutureShippingDate(null)
		
		1*grManager.getRegistryStatusFromRepo("enUS", "rId") >> "rStatus"
		1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> ""
		
		//saveRegistryInfoToSession
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		sBean.getValues() >> map
		
		requestMock.getRequestURI() >> "view_registry_guest.jsp"
		1*grManager.getRegistryInfo(_, "enUS") >> rSummaryVO2
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		when:
		mridDroplet.service(requestMock, responseMock)
		then:
		1 * requestMock.setParameter("validCheck", true)
		rSummaryVO.getPrimaryRegistrantEmail() == "harry@gmail.com"
		rSummaryVO.getPrimaryRegistrantPrimaryPhoneNum() == "123456"
		rSummaryVO.getFavStoreId() == ""
		rSummaryVO.isAllowedToScheduleAStoreAppointment() == false
		rSummaryVO.getEventDate() == "20/14/2010"
		0*grBean.setRegistryOperation(_)
		
		1*requestMock.setParameter("coRegFlag",false)
		1*requestMock.setParameter("registryURL","rURL")
		1*requestMock.setParameter("eventDate", "20/14/2010")
		1*requestMock.setParameter("futureShippingDate", null)
		1*requestMock.setParameter("registrySummaryVO",_)
		1*requestMock.setParameter("registryVO", _)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		!map.containsKey("userRegistrysummaryVO") 
		0*grManager.canScheuleAppointment(_, _, _)
		0*cToolsMock.getRegistryTypeName(_,_)
		
	}
	
	def"service. TC to Fetch Registry Info from registry Id and display registry summery info when profile is transient"(){
		given:
		 def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		 def List<RegistrySummaryVO> ls = new ArrayList()
		 def List<RegistrySummaryVO> sls = new ArrayList()
		 ls.add(registrantVO1)
		 sls.add("rId")
		 map.put("registryIdList", ls)
		 map.put("userRegistriesList", sls)
		 
		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner1"
		 sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 1*cToolsMock.getAllValuesForKey("WSDLKeys",_) >> ["tocken123"]
		 
		 1*grManager.getRegistryInfo(_) >> rResVO
		 
		rResVO.setServiceErrorVO(null)
		rResVO.setRegistryVO(registryVO)
		
		rResVO.setRegistrySummaryVO(rSummaryVO)
		registryVO.setEvent(eventVO)
		
		registryVO.setPrimaryRegistrant(registrantVO)
		
		registrantVO.setEmail("harry@gmail.com")
		registrantVO.setPrimaryPhone("123456")
		registryVO.setPrefStoreNum("")
		
		rSummaryVO.setRegistryType(null)
		
		//1*grManager.canScheuleAppointment("st12", _, "enUS") >> true
		
		profileMock.isTransient() >> true
		
		
		registryVO.setCoRegistrant(registrantVO1)
		registrantVO1.setEmail("harr@gmail")
		
		
		rSummaryVO.setEventDate(null)
		rSummaryVO.setFutureShippingDate(null)
		
		grManager.getRegistryStatusFromRepo("enUS", "rId") >> "rStatus"
		grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "rStatus"
		
		//saveRegistryInfoToSession
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		sBean.getValues() >> map
		
		requestMock.getRequestURI() >> "view_registry_guest.jsp"
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		when:
		mridDroplet.service(requestMock, responseMock)
		then:
		1 * requestMock.setParameter("validCheck", true)
		rSummaryVO.getPrimaryRegistrantEmail() == "harry@gmail.com"
		rSummaryVO.getPrimaryRegistrantPrimaryPhoneNum() == "123456"
		rSummaryVO.getFavStoreId() == ""
		rSummaryVO.isAllowedToScheduleAStoreAppointment() == false
		0*grBean.setRegistryOperation(_)
		
		1*requestMock.setParameter("coRegFlag",false)
		1*requestMock.setParameter("registryURL","rURL")
		1*requestMock.setParameter("futureShippingDate", null)
		1*requestMock.setParameter("registrySummaryVO",_)
		1*requestMock.setParameter("registryVO", _)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		!map.containsKey("userRegistrysummaryVO")
		0*grManager.canScheuleAppointment(_, _, _)
		0*cToolsMock.getRegistryTypeName(_,_)
		
	}
	
	def"service. TC to Fetch Registry Info from registry Id and display registry summery info when acceptableStatusesList dosent content registryStatus "(){
		given:
		 def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		 def List<RegistrySummaryVO> ls = new ArrayList()
		 def List<RegistrySummaryVO> sls = new ArrayList()
		 ls.add(registrantVO1)
		 sls.add("rId")
		 map.put("registryIdList", ls)
		 map.put("userRegistriesList", sls)
		 
		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner"
		 sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 1*cToolsMock.getAllValuesForKey("WSDLKeys",_) >> ["tocken123"]
		 
		 1*grManager.getRegistryInfo(_) >> rResVO
		 
		rResVO.setServiceErrorVO(null)
		rResVO.setRegistryVO(registryVO)
		
		rResVO.setRegistrySummaryVO(rSummaryVO)
		registryVO.setEvent(eventVO)
		
		registryVO.setPrimaryRegistrant(registrantVO)
		
		registrantVO.setEmail("harry@gmail.com")
		registrantVO.setPrimaryPhone("123456")
		registryVO.setPrefStoreNum("")
		
		rSummaryVO.setRegistryType(rType)
		
		//1*grManager.canScheuleAppointment("st12", _, "enUS") >> true
		
		profileMock.isTransient() >> false
		
		1*pManager.getLoginPropertyName() >> "loginP"
		1*profileMock.getPropertyValue("loginP") >> "hars"
		
		registryVO.setCoRegistrant(registrantVO1)
		registrantVO1.setEmail("harr@gmail")
		
		
		rSummaryVO.setEventDate("20102014")
		rSummaryVO.setFutureShippingDate(null)
		
		1*grManager.getRegistryStatusFromRepo("enUS", "rId") >> "rStatus"
		1*grManager.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "rStatus"
		
		//saveRegistryInfoToSession
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		sBean.getValues() >> map
		
		requestMock.getRequestURI() >> "view_registry_guest.jsp"
		1*grManager.getRegistryInfo(_, "enUS") >> rSummaryVO2
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		when:
		mridDroplet.service(requestMock, responseMock)
		then:
		1 * requestMock.setParameter("validCheck", true)
		rSummaryVO.getPrimaryRegistrantEmail() == "harry@gmail.com"
		rSummaryVO.getPrimaryRegistrantPrimaryPhoneNum() == "123456"
		rSummaryVO.getFavStoreId() == ""
		rSummaryVO.isAllowedToScheduleAStoreAppointment() == false
		rSummaryVO.getEventDate() == "20/14/2010"
		0*grBean.setRegistryOperation(_)
		
		1*requestMock.setParameter("coRegFlag",false)
		1*requestMock.setParameter("registryURL","rURL")
		1*requestMock.setParameter("eventDate", "20/14/2010")
		1*requestMock.setParameter("futureShippingDate", null)
		1*requestMock.setParameter("registrySummaryVO",_)
		1*requestMock.setParameter("registryVO", _)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		!map.containsKey("userRegistrysummaryVO")
		0*grManager.canScheuleAppointment(_, _, _)
		0*cToolsMock.getRegistryTypeName(_,_)
		
	}
	
	

	
/**************************************************** for error scenario *********************************/	
	def"service. TC for  900 error  "(){
		given:
		
             setErrorCommonParameters()	 
			 rResVO.setServiceErrorVO(sErrorVO)
			 rResVO.setRegistryVO(registryVO)
			 sErrorVO.setErrorExists(true)
			 sErrorVO.setErrorDisplayMessage("error Display Message")
			 sErrorVO.setErrorMessage("errorMessage")
			 sErrorVO.setErrorId(900)
			 
			//rResVO.setRegistrySummaryVO(null)
		
		when:
			mridDroplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter( "errorMsg", "err_gift_reg_fatal_error")
			1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}
	
	def"service. TC for  900 error  when display error message is empty  "(){
		given:
		
			 setErrorCommonParameters()
			 rResVO.setServiceErrorVO(sErrorVO)
			 rResVO.setRegistryVO(registryVO)
			 sErrorVO.setErrorExists(true)
			 sErrorVO.setErrorDisplayMessage("")
			 sErrorVO.setErrorMessage("errorMessage")
			 sErrorVO.setErrorId(900)
			 
			//rResVO.setRegistrySummaryVO(null)
		
		when:
			mridDroplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter( "errorMsg", "err_gift_reg_fatal_error")
			1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}

	def"service. TC for  901 error  "(){
		given:
		
			 setErrorCommonParameters()
			 rResVO.setServiceErrorVO(sErrorVO)
			 rResVO.setRegistryVO(registryVO)
			 sErrorVO.setErrorExists(true)
			 sErrorVO.setErrorDisplayMessage("")
			 sErrorVO.setErrorMessage("errorMessage")
			 sErrorVO.setErrorId(901)
			 
			//rResVO.setRegistrySummaryVO(null)
		
		when:
			mridDroplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter( "errorMsg", "err_gift_reg_siteflag_usertoken_error")
			1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}
	
	def"service. TC for  902 error  "(){
		given:
		
			 setErrorCommonParameters()
			 rResVO.setServiceErrorVO(sErrorVO)
			 rResVO.setRegistryVO(registryVO)
			 sErrorVO.setErrorExists(true)
			 sErrorVO.setErrorDisplayMessage("")
			 sErrorVO.setErrorMessage("errorMessage")
			 sErrorVO.setErrorId(902)
			 
			//rResVO.setRegistrySummaryVO(null)
		
		when:
			mridDroplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter( "errorMsg", "err_gift_reg_invalid_input_format")
			1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}
	
	def"service. TC when error message is empty  "(){
		given:
		
			 setErrorCommonParameters()
			 rResVO.setServiceErrorVO(sErrorVO)
			 rResVO.setRegistryVO(registryVO)
			 sErrorVO.setErrorExists(true)
			 sErrorVO.setErrorDisplayMessage("")
			 sErrorVO.setErrorMessage("")
			 sErrorVO.setErrorId(902)
			 
			//rResVO.setRegistrySummaryVO(null)
		
		when:
			mridDroplet.service(requestMock, responseMock)
			
		then:
			0*requestMock.setParameter( "errorMsg", "err_gift_reg_invalid_input_format")
			1*requestMock.serviceParameter("error", requestMock, responseMock)
			0*requestMock.setParameter( "errorMsg", "err_gift_reg_siteflag_usertoken_error")
			0*requestMock.setParameter( "errorMsg", "err_gift_reg_fatal_error")
			1*requestMock.setParameter( "errorMsg", "err_no_reg_info")

		
	}
	
	def"service. TC when error code not in (900,901 and 902)  "(){
		given:
		
			 setErrorCommonParameters()
			 rResVO.setServiceErrorVO(sErrorVO)
			 rResVO.setRegistryVO(registryVO)
			 sErrorVO.setErrorExists(true)
			 sErrorVO.setErrorDisplayMessage("")
			 sErrorVO.setErrorMessage("errorMessage")
			 sErrorVO.setErrorId(904)
			 
		
		when:
			mridDroplet.service(requestMock, responseMock)
			
		then:
			0*requestMock.setParameter( "errorMsg", "err_gift_reg_invalid_input_format")
			1*requestMock.serviceParameter("error", requestMock, responseMock)
			0*requestMock.setParameter( "errorMsg", "err_gift_reg_siteflag_usertoken_error")
			0*requestMock.setParameter( "errorMsg", "err_gift_reg_fatal_error")
			1*requestMock.setParameter( "errorMsg", "err_no_reg_info")

		
	}
	
	private setErrorCommonParameters(){
		def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		def List<RegistrySummaryVO> ls = new ArrayList()
		def List<RegistrySummaryVO> sls = new ArrayList()
		ls.add(registrantVO1)
		sls.add("rId")
		map.put("registryIdList", ls)
		map.put("userRegistriesList", sls)
   

		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner"
		 sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 1*cToolsMock.getAllValuesForKey("WSDLKeys",_) >> ["tocken123"]
		 
		 1*grManager.getRegistryInfo(_) >> rResVO
		 
		 requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		 sBean.getValues() >> map

		 requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		 profileMock.isTransient() >> false
		 grManager.isUserOwnRegistry(profileMock, _, requestMock) >> true

	}
	
	def"service. TC when registry response VO is null  "(){
		given:
		
		def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		def List<RegistrySummaryVO> ls = new ArrayList()
		def List<RegistrySummaryVO> sls = new ArrayList()
		ls.add(registrantVO1)
		sls.add("rId")
		map.put("registryIdList", ls)
		map.put("userRegistriesList", sls)
   

		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner"
		 sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 1*cToolsMock.getAllValuesForKey("WSDLKeys",_) >> ["tocken123"]
		 
		 1*grManager.getRegistryInfo(_) >> null
		 
		 requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		 sBean.getValues() >> map

		 requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		 profileMock.isTransient() >> false
		 grManager.isUserOwnRegistry(profileMock, _, requestMock) >> true
			 
		
		when:
			mridDroplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter("errorMsg", "err_no_reg_info")
			1*requestMock.serviceParameter("error", requestMock, responseMock)

		
	}
	
	def"service. TC when registry is not valide  "(){
		given:
		
		def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		def List<RegistrySummaryVO> ls = new ArrayList()
		def List<RegistrySummaryVO> sls = new ArrayList()
		ls.add(registrantVO1)
		sls.add("rId")
		map.put("registryIdList", ls)
		map.put("userRegistriesList", sls)
   

		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner"
		 sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 
		// 1*grManager.getRegistryInfo(_) >> null
		 
		 requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		 sBean.getValues() >> map

		 requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		 profileMock.isTransient() >> true
			 
		
		when:
			mridDroplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter("errorMsg", "err_invalid_reg_info_req")
			1*requestMock.serviceParameter("error", requestMock, responseMock)
			0*cToolsMock.getAllValuesForKey("WSDLKeys",_)

		
	}
	
	def"service.TC for BBBBusinessException"(){
		given:
		def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		def List<RegistrySummaryVO> ls = new ArrayList()
		def List<RegistrySummaryVO> sls = new ArrayList()
		ls.add(registrantVO1)
		sls.add("rId")
		map.put("registryIdList", ls)
		map.put("userRegistriesList", sls)
   

		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner"
		 sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 1*cToolsMock.getAllValuesForKey("WSDLKeys",_) >> {throw new BBBBusinessException("exception")}
		 
		 
		 requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		 sBean.getValues() >> map

		 requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		 profileMock.isTransient() >> false
		 grManager.isUserOwnRegistry(profileMock, _, requestMock) >> true

		 when:
		 	mridDroplet.service(requestMock, responseMock)
			 
		 then:
		 1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		 
	}
	
	def"service.TC for BBBSystemException"(){
		given:
		def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
		def List<RegistrySummaryVO> ls = new ArrayList()
		def List<RegistrySummaryVO> sls = new ArrayList()
		ls.add(registrantVO1)
		sls.add("rId")
		map.put("registryIdList", ls)
		map.put("userRegistriesList", sls)
   

		 requestMock.getLocale() >> {return new Locale("en_US")}
		 requestMock.getParameter("registryId") >> "rId"
		 requestMock.getParameter("displayView") >> "owner"
		 sContextMock.getSite() >> site
		 site.getId() >> "enUS"
		 1*cToolsMock.getAllValuesForKey("WSDLKeys",_) >> {throw new BBBSystemException("exception")}
		 
		 
		 requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
		 sBean.getValues() >> map

		 requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		 profileMock.isTransient() >> false
		 grManager.isUserOwnRegistry(profileMock, _, requestMock) >> true

		 when:
			 mridDroplet.service(requestMock, responseMock)
			 
		 then:
		 1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		 1*requestMock.setParameter("errorMsg", "err_reginfo_sys_error")
	}
	
	/*************************************************** saveRegistryInfoToSession ********************************/
	
	def "saveRegistryInfoToSession. TC when usersCurrentlyViewedRegistry contains registry summery vo" (){
		given:
			def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
			def List<RegistrySummaryVO> ls = new ArrayList()
			//def List<RegistrySummaryVO> sls = new ArrayList()
			ls.add(rSummaryVO)
			//sls.add("rId")
			map.put("registryIdList", ls)
			//map.put("userRegistriesList", sls)
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			2*sBean.getValues() >> map
   
  
		when:
		 mridDroplet.saveRegistryInfoToSession(rSummaryVO, requestMock)
		then:
		map.get("registryIdList") == [rSummaryVO]
	}
	
	def "saveRegistryInfoToSession. TC when usersCurrentlyViewedRegistry list is null" (){
		given:
			def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
			def List<RegistrySummaryVO> ls = new ArrayList()	
			//ls.add(rSummaryVO)
			
			map.put("registryIdList", null)			
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			2*sBean.getValues() >> map
   
  
		when:
		 mridDroplet.saveRegistryInfoToSession(rSummaryVO, requestMock)
		then:
		map.get("registryIdList") == [rSummaryVO]
	}
	
	def "saveRegistryInfoToSession. TC when registry summery vo is null" (){
		given:
			def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			1*sBean.getValues() >> map
   
  
		expect:
		 mridDroplet.saveRegistryInfoToSession(null, requestMock)
	}
	
	def "validateRegistry. TC when registry id is null" (){
		given:
			
		when:
			boolean value = mridDroplet.validateRegistry(null, "displayView",requestMock)
		then:
		value == false
	}
	
	def "validateRegistry. TC when display view is null" (){
		given:
			def HashMap<String, List> map = new HashMap <String, List<RegistrySummaryVO>>()
			def List<RegistrySummaryVO> sls = new ArrayList()
			sls.add("rId")
			map.put("userRegistriesList", sls)
			
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sBean
			sBean.getValues() >> map
   
	  
		when:
			boolean value = mridDroplet.validateRegistry("rid", null,requestMock)
		then:
		value == true
		requestMock.setParameter("validCheck", false)
	}
}
