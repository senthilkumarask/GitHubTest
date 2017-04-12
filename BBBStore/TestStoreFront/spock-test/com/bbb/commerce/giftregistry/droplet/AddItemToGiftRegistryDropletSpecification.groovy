package com.bbb.commerce.giftregistry.droplet

import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;

import atg.repository.RepositoryException
import atg.userprofiling.Profile;
import spock.lang.specification.BBBExtendedSpec

class AddItemToGiftRegistryDropletSpecification extends BBBExtendedSpec {

	AddItemToGiftRegistryDroplet droplet = new AddItemToGiftRegistryDroplet()
	GiftRegistryManager mGiftRegistryManager = Mock()
	BBBCatalogTools mCatalogTools = Mock()
	
	def setup(){
		droplet.setCatalogTools(mCatalogTools)
		droplet.setGiftRegistryManager(mGiftRegistryManager)
	}
	
	def "service method happy path"(){
		
		given:
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			RegistrySkinnyVO vo1 = new RegistrySkinnyVO()
			RegistrySkinnyVO vo2 = new RegistrySkinnyVO()
			requestMock.getParameter("siteId") >> "BBBUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",null)
			1*mGiftRegistryManager.getAcceptableGiftRegistries(profile, "BBBUS") >> [vo1,vo2,null]
			vo1.setEventDate("date")
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*mGiftRegistryManager.recommendRegistryList(_)
			1*requestMock.setParameter("registrySkinnyVOList",_)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*sessionBean.getValues().put("registrySkinnyVOList",_)
			0*sessionBean.setRegistrySummaryVO(_)
	}
	
	def "service method with map as not null and recommendedRegistryList as not null"(){
		
		given:
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			RegistrySkinnyVO vo1 = new RegistrySkinnyVO()
			RegistrySummaryVO regSummVO = new RegistrySummaryVO()
			requestMock.getParameter("siteId") >> "BBBUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",[vo1])
			1*mGiftRegistryManager.recommendRegistryList(_) >> [regSummVO]
			vo1.setEventDate("date")
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registrySkinnyVOList",_)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1*sessionBean.setRegistrySummaryVO(_)
			0*mGiftRegistryManager.getAcceptableGiftRegistries(profile, "BBBUS")
	}
	
	def "service method with map as null and sessionBean.getRegistrySummaryVO() not null"(){
		
		given:
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			requestMock.getParameter("siteId") >> "BBBUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",null)
			1*mGiftRegistryManager.getAcceptableGiftRegistries(profile, "BBBUS") >> null
			sessionBean.getRegistrySummaryVO() >> []
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registrySkinnyVOList",_)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*sessionBean.getValues().put("registrySkinnyVOList",_)
			0*sessionBean.setRegistrySummaryVO(_)
	}
	
	def "service method with map as null and sessionBean.sessionBean.isRecommRegistriesPopulated() not true"(){
		
		given:
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			requestMock.getParameter("siteId") >> "BBBUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",null)
			1*mGiftRegistryManager.getAcceptableGiftRegistries(profile, "BBBUS") >> null
			sessionBean.isRecommRegistriesPopulated() >> true
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registrySkinnyVOList",_)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*sessionBean.getValues().put("registrySkinnyVOList",_)
			0*sessionBean.setRegistrySummaryVO(_)
	}
	
	def "service method with BBBSystemException thrown"(){
		
		given:
			droplet = Spy()
			droplet.setGiftRegistryManager(mGiftRegistryManager)
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			requestMock.getParameter("siteId") >> "BBBUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",null)
			1*mGiftRegistryManager.getAcceptableGiftRegistries(profile, "BBBUS") >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "BBBSystemException from service of AddItemToGiftRegistryDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),_);
			1*requestMock.setParameter("errorMsg","err_regsearch_sys_exception");
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock);
	}
	
	def "service method with BBBBusinessException thrown"(){
		
		given:
			droplet = Spy()
			droplet.setGiftRegistryManager(mGiftRegistryManager)
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			requestMock.getParameter("siteId") >> "BBBUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",null)
			1*mGiftRegistryManager.getAcceptableGiftRegistries(profile, "BBBUS") >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "BBBBusinessException from service of AddItemToGiftRegistryDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),_)
			1*requestMock.setParameter("errorMsg","err_regsearch_biz_exception")
			1*requestMock.serviceLocalParameter("error",requestMock,responseMock)
	}
	
	def "service method with RepositoryException thrown"(){
		
		given:
			droplet = Spy()
			droplet.setGiftRegistryManager(mGiftRegistryManager)
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			requestMock.getParameter("siteId") >> "BBBUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",null)
			1*mGiftRegistryManager.getAcceptableGiftRegistries(profile, "BBBUS") >> {throw new RepositoryException("RepositoryException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Repository Exception from service of AddItemToGiftRegistryDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001),_)
			1*requestMock.setParameter("errorMsg","err_regsearch_repo_exception")
			1*requestMock.serviceLocalParameter("error",requestMock,responseMock)
	}
	
	def "getProfileRegistryList method happy path with service method transient property true"(){
		
		given:
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			RegistrySkinnyVO vo1 = new RegistrySkinnyVO()
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",[vo1])
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter("siteId") >> "BBBUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> true
			1*requestMock.getParameter("errorMsg") >> ""
		
		when:
			List<RegistrySkinnyVO> list = droplet.getProfileRegistryList()
		
		then:
			list != null
			list.size() == 1
			1 * requestMock.setParameter('siteId', null)
	}
	
	def "getProfileRegistryList method with errorMsg not empty"(){
		
		given:
			Profile profile =  Mock()
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> true
			1*requestMock.getParameter("errorMsg") >> "err_regsearch_biz_exception"
		
		when:
			List<RegistrySkinnyVO> list = droplet.getProfileRegistryList()
		
		then:
			list == null
			BBBBusinessException excep = thrown()
			excep.getMessage().equals("giftregistry_1004:BBBBusinessException")
			1 * requestMock.setParameter('siteId', null)
	}
	
	def "getProfileRegistryList method with errorMsg not empty and repo excep thrown"(){
		
		given:
			Profile profile =  Mock()
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> true
			1*requestMock.getParameter("errorMsg") >> "err_regsearch_repo_exception"
		
		when:
			List<RegistrySkinnyVO> list = droplet.getProfileRegistryList()
		
		then:
			list == null
			RepositoryException excep = thrown()
			excep.getMessage().equals(null)
			1 * requestMock.setParameter('siteId', null)
	}
	
	def "getProfileRegistryList method with errorMsg  empty and BBBSystemException is thrown"(){
		
		given:
			Profile profile =  Mock()
			BBBSessionBean sessionBean = Mock()
			Map map = new HashMap()
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> true
			1*requestMock.getParameter("errorMsg") >> ""
			sessionBean.getValues() >> map
			map.put("registrySkinnyVOList",null)
		
		when:
			List<RegistrySkinnyVO> list = droplet.getProfileRegistryList()
		
		then:
			list == null
			BBBSystemException excep = thrown()
			excep.getMessage().equals("giftregistry_1005:BBBSystemException")
			1 * requestMock.setParameter('siteId', null)
	}
}
