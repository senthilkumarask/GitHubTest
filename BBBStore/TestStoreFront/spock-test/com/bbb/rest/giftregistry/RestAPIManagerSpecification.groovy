package com.bbb.rest.giftregistry

import atg.droplet.DropletException
import atg.droplet.GenericFormHandler
import atg.multisite.Site
import atg.multisite.SiteContext
import atg.multisite.SiteContextManager
import atg.nucleus.Nucleus;
import atg.nucleus.Nucleus.NucleusStartupOptions
import atg.nucleus.registry.NucleusRegistry
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.security.DigestPasswordHasher
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile
import atg.userprofiling.ProfileFormHandler
import atg.userprofiling.ProfileServices
import atg.userprofiling.ProfileTools
import com.bbb.account.BBBProfileFormHandler
import com.bbb.account.BBBProfileFormHandlerBean
import com.bbb.cms.PromoBoxVO
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.browse.droplet.BreadcrumbDroplet;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.checklist.manager.CheckListManager
import com.bbb.commerce.checklist.tools.CheckListTools
import com.bbb.commerce.checklist.vo.MyItemCategoryVO
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.droplet.AddItemToGiftRegistryDroplet
import com.bbb.commerce.giftregistry.droplet.DateCalculationDroplet
import com.bbb.commerce.giftregistry.droplet.GiftRegistryFlyoutDroplet
import com.bbb.commerce.giftregistry.droplet.MyRegistriesDisplayDroplet
import com.bbb.commerce.giftregistry.droplet.POBoxValidateDroplet
import com.bbb.commerce.giftregistry.droplet.RegistryInfoDisplayDroplet
import com.bbb.commerce.giftregistry.droplet.RegistryItemsDisplayDroplet
import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.commerce.giftregistry.vo.AddressVO
import com.bbb.commerce.giftregistry.vo.AppRegistryInfoDetailVO
import com.bbb.commerce.giftregistry.vo.EventVO
import com.bbb.commerce.giftregistry.vo.ProfileRegistryListVO
import com.bbb.commerce.giftregistry.vo.RegistrantVO
import com.bbb.commerce.giftregistry.vo.RegistryCategoryBucketVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO
import com.bbb.commerce.giftregistry.vo.RegistryResVO
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO
import com.bbb.commerce.giftregistry.vo.RestRegistryInfoDetailVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.commerce.giftregistry.vo.ShippingVO
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.integration.vo.ResponseErrorVO
import com.bbb.profile.BBBPropertyManager
import com.bbb.profile.session.BBBSessionBean
import com.bbb.security.PBKDF2PasswordHasher

import java.util.Dictionary;
import java.util.List
import java.util.Map;

import javax.servlet.ServletException

import spock.lang.specification.BBBExtendedSpec

class RestAPIManagerSpecification extends BBBExtendedSpec {

	 def RestAPIManager rAPIManager
	 Profile profileMock = Mock()
	 SiteContext sContext = Mock()
	 Site site = Mock()
	 def BBBCatalogTools cToolsMock = Mock()
	 
	 RegistrySummaryVO rSummeryVO = new RegistrySummaryVO()
	 RegistrySummaryVO rSummeryVO1 = new RegistrySummaryVO()
	 RegistrySummaryVO rSummeryVO2 = new RegistrySummaryVO()
	 RegistrySummaryVO rSummeryVO3 = new RegistrySummaryVO()
	 
	  MyRegistriesDisplayDroplet rDisplayDropletMock = Mock()
	 BBBSessionBean sessionBeanMock = Mock()
	 DateCalculationDroplet dcDropletMock = Mock()
	 AddItemToGiftRegistryDroplet aITRDroplet = Mock()
	 GiftRegistryManager grManagerMock = Mock()
	 RegistryInfoDisplayDroplet riDisplayDroplet = Mock()
	 POBoxValidateDroplet poBoxVDropletMock = Mock()
	 RepositoryItem item1 = Mock()
	 RepositoryItem item2 = Mock()
	 RepositoryItem item3 = Mock()
	 RepositoryItem chanleItem = Mock()
	 RepositoryItem singleSite = Mock()
	 
	 CheckListManager cheManager = Mock()
	 RegistryItemsDisplayDroplet ritemDisplayDroplet = Mock()
	 GiftRegistryTools grTools = Mock()
	 GiftRegistryRecommendationManager grRecManager = Mock()

	 BreadcrumbDroplet bCrumbDroplet = Mock()
	 
	 MutableRepository muRepository = Mock()
	 MutableRepositoryItem muRepItem = Mock()
	 CheckListTools cheLisTools = Mock()
	 GiftRegistryFlyoutDroplet grFlyoutDroplet = Mock()
	 
	 def RegistryTypes rType = Mock()
	 SiteContext siteContexMock = Mock()
	 GiftRegSessionBean grSessionBean = Mock()
	 
	 RegistrySkinnyVO rSkinnyVO = new RegistrySkinnyVO()
	 RegistrySkinnyVO rSkinnyVO1 = new RegistrySkinnyVO()
	 RegistryItemVO rItemVO = new RegistryItemVO()
	 RegistryItemVO rItemVO1 = new RegistryItemVO()
	 RegistryItemVO rItemVO2 = new RegistryItemVO()
	 RegistryItemVO rItemVO3 = new RegistryItemVO()
	 AddressVO addressVO = new AddressVO()
	 SKUDetailVO sDetaiVo = new SKUDetailVO()
	 SKUDetailVO sDetaiVo1 = new SKUDetailVO()
	 
	 RegistryVO registryVO = new RegistryVO()
	 PromoBoxVO pBoxVO = new PromoBoxVO()
	 AddressVO addressVo = new AddressVO()
	 
	 RegistryCategoryMapVO repositoryCVO = new RegistryCategoryMapVO()
	 Repository sRepository = Mock()
	 Repository chnlRepository = Mock()
	// 
	 ProfileServices profService = Mock()
	 DropletException dExceptionMock = Mock()
	 DropletException dExceptionMock1 = Mock()
	 LblTxtTemplateManager lblTexTManager = Mock()
	 RepositoryView rViewMock = Mock()
	 BBBPropertyManager propertyManager =  new BBBPropertyManager()
	 ProfileTools pToolsMock = Mock()
	 
	 
	 def setup(){
		 rAPIManager = new RestAPIManager(checkListManager:cheManager,siteContext : sContext, myRegistryDroplet : rDisplayDropletMock,
			  dateCalculationDroplet : dcDropletMock, addItemToGiftRegistryDroplet : aITRDroplet, giftRegistryManager : grManagerMock,
			  catalogTools:cToolsMock,profile : profileMock,registryInfoDroplet : riDisplayDroplet,poBoxValidateDroplet : poBoxVDropletMock,
			   giftRegistryTools : grTools,checkListRepository : muRepository, checkListTools:cheLisTools, giftRegistryFlyoutDroplet:grFlyoutDroplet,
			    breadcrumbDroplet:bCrumbDroplet, siteRepository: sRepository, registryItemDroplet : ritemDisplayDroplet, giftRegistryRecommendationManager :grRecManager)
		 requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		// NucleusStartupOptions pOptions = Mock()
		// pOptions.isRegisterChildNucleus() >> true
		 Nucleus nucluse = Mock() 
		 NucleusRegistry sNucleusRegistry = Mock()
		 Nucleus.setNucleusRegistry(sNucleusRegistry)
		 sNucleusRegistry.getKey() >> "nKey"
		 sNucleusRegistry.get("nKey") >> nucluse
		 
		 Nucleus.sUsingChildNucleii = true
		 
	 }
	 
	 def"getRegistryInfoForProfileID.Tc to get Registries information for the profile"(){
		 given:
		 	 setCommonParaForGetRegistryInfoForProfileID()
			 requestMock.getObjectParameter("errorMsg") >> null
			 requestMock.getObjectParameter("registrySummaryVO") >> [rSummeryVO,rSummeryVO1,rSummeryVO2, rSummeryVO3]
			 rSummeryVO.setEventDate("122345")
			 rSummeryVO1.setEventDate("122345")
			 rSummeryVO2.setEventDate("0")
			 rSummeryVO3.setEventDate("")
			 
			 requestMock.getParameter("daysToGo") >> "3"
			 requestMock.getParameter("check") >> false >> true
			 requestMock.getParameter("daysToNextCeleb") >> 4
			 
		 when:
		 	rAPIManager.getRegistryInfoForProfileID()
		 then:
		 	1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter("siteId", "USBed")
			1*rDisplayDropletMock.service(requestMock, responseMock)
			2*requestMock.setParameter("eventDate", "122345")
			2*dcDropletMock.service(requestMock, responseMock)
			rSummeryVO.getDaysToGo() == 3
			!rSummeryVO.isEventYetToCome()
			rSummeryVO.getDaysToNextCeleb() == 4
	 } 
	 
	 def"getRegistryInfoForProfileID.Tc for error message"(){
		 given:
		     setCommonParaForGetRegistryInfoForProfileID()
			 requestMock.getObjectParameter("errorMsg") >> "error"
		 
		 when:
			  List value = rAPIManager.getRegistryInfoForProfileID()
		 then:
		    value == null
			1*rDisplayDropletMock.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
	 }
	 
	 def"getRegistryInfoForProfileID.Tc when registry VO is null"(){
		 given:
		 	 setCommonParaForGetRegistryInfoForProfileID()
			 requestMock.getObjectParameter("errorMsg") >> null
			 requestMock.getObjectParameter("registrySummaryVO") >> null
		 
		 when:
			  List value = rAPIManager.getRegistryInfoForProfileID()
		 then:
			value == null
			1*rDisplayDropletMock.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
			
	 }
	 
	 def"getRegistryInfoForProfileID.Tc for IOException"(){
		 given:
		 	 setCommonParaForGetRegistryInfoForProfileID()
			 1*rDisplayDropletMock.service(requestMock, responseMock) >> {throw new IOException("exception") }
		 
		 when:
			  List value = rAPIManager.getRegistryInfoForProfileID()
		 then:
			value == null
			0*dcDropletMock.service(requestMock, responseMock)
			
	 }
	 
	 def"getRegistryInfoForProfileID.Tc for ServletException"(){
		 given:
			 setCommonParaForGetRegistryInfoForProfileID()			
			 1*rDisplayDropletMock.service(requestMock, responseMock) >> {throw new ServletException("exception") }
		 
		 when:
			  List value = rAPIManager.getRegistryInfoForProfileID()
		 then:
			value == null
			0*dcDropletMock.service(requestMock, responseMock)
			
	 }
	 private setCommonParaForGetRegistryInfoForProfileID(){
		 requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		 sContext.getSite() >> site
		 site.getId() >> "USBed"

	 }
	 /*********************************************** getRegistryListForProfile **************************************************/ 
	 
	 def"getRegistryListForProfile. TC to fetch the Registry list for Profile"(){
		 given:
		    Map map = new HashMap()
			map.put("registrySkinnyVOList", null)
			
		 	requestMock.getObjectParameter("registrySkinnyVOList") >>  null
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			sessionBeanMock.getValues() >> map
			requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
			1*profileMock.isTransient() >> false
			1*grManagerMock.getAcceptableGiftRegistries(profileMock, null) >> [rSkinnyVO,rSkinnyVO1]
			rSkinnyVO.setEventDate("01/12/2014")
			rSkinnyVO1.setEventDate("02/12/2014")
		 when:
		 	List value = rAPIManager.getRegistryListForProfile()
		 
		 then:
		 value == [rSkinnyVO1,rSkinnyVO]
		 1*requestMock.setParameter("siteId", _)
		 1*aITRDroplet.service(requestMock, responseMock)
		 map.get("registrySkinnyVOList") == [rSkinnyVO1,rSkinnyVO]
		 map.get("size") == 2
		 
	 }
	 
	 def"getRegistryListForProfile. TC FOR BBBBusinessException while getting registrySkinnyVOList  "(){
		 given:
			Map map = new HashMap()
			map.put("registrySkinnyVOList", null)
			
			 requestMock.getObjectParameter("registrySkinnyVOList") >>  null
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			sessionBeanMock.getValues() >> map
			requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
			1*profileMock.isTransient() >> false
			1*grManagerMock.getAcceptableGiftRegistries(profileMock, null) >> {throw new BBBBusinessException("exception")}
		 when:
			 List value = rAPIManager.getRegistryListForProfile()
		 
		 then:
			 value == null
			 1*requestMock.setParameter("siteId", _)
			 1*aITRDroplet.service(requestMock, responseMock)
			 map.get("registrySkinnyVOList") == null
		 
	 }
	 
	 def"getRegistryListForProfile. TC when profile is transient "(){
		 given:
			Map map = new HashMap()
			map.put("registrySkinnyVOList", null)
			
			 requestMock.getObjectParameter("registrySkinnyVOList") >>  null
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			sessionBeanMock.getValues() >> map
			requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
			1*profileMock.isTransient() >> true
		 when:
			 List value = rAPIManager.getRegistryListForProfile()
		 
		 then:
			 value == null
			 1*requestMock.setParameter("siteId", _)
			 1*aITRDroplet.service(requestMock, responseMock)
			 map.get("registrySkinnyVOList") == null
			 0*grManagerMock.getAcceptableGiftRegistries(profileMock, null)
		 
	 }
	 
	 def"getRegistryListForProfile. TC when registrySkinnyVOList is not null gets from sessionBean "(){
		 given:
			Map map = new HashMap()
			map.put("registrySkinnyVOList", [rSkinnyVO,rSkinnyVO1])
			
			 requestMock.getObjectParameter("registrySkinnyVOList") >>  null
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			sessionBeanMock.getValues() >> map
		 when:
			 List value = rAPIManager.getRegistryListForProfile()
		 
		 then:
			 value == [rSkinnyVO,rSkinnyVO1]
			 1*aITRDroplet.service(requestMock, responseMock)
			 0*grManagerMock.getAcceptableGiftRegistries(profileMock, null)
		 
	 }
	 
	 def"getRegistryListForProfile. TC when registrySkinnyVOList is not null gets from request object "(){
		 given:
			
			 requestMock.getObjectParameter("registrySkinnyVOList") >>  [rSkinnyVO,rSkinnyVO1]
		 when:
			 List value = rAPIManager.getRegistryListForProfile()
		 
		 then:
			 value == [rSkinnyVO,rSkinnyVO1]
			 1*aITRDroplet.service(requestMock, responseMock)
			 0*grManagerMock.getAcceptableGiftRegistries(profileMock, null)
		 
	 }
	 
	 def"getRegistryListForProfile. TC for IOException  "(){
		 given:
		 1*aITRDroplet.service(requestMock, responseMock) >> {throw new IOException("ioException")} 
			
		 when:
			 List value = rAPIManager.getRegistryListForProfile()
		 
		 then:
		 BBBSystemException ex = thrown()
		 
	 }
	 
	 def"getRegistryListForProfile. TC for ServletException  "(){
		 given:
		 1*aITRDroplet.service(requestMock, responseMock) >> {throw new ServletException("ioException")}
			
		 when:
			 List value = rAPIManager.getRegistryListForProfile()
		 
		 then:
		 BBBSystemException ex = thrown()
		 
	 }
	 
	 /***************************************** getOwnAndRecommendedRegistriesForProfile *********************************/
	 
	 def"getOwnAndRecommendedRegistriesForProfile"(){
		 given:
			Map map = new HashMap()
			map.put("registrySkinnyVOList", [rSkinnyVO,rSkinnyVO1])

		 	requestMock.getObjectParameter("registrySkinnyVOList") >>  null
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			sessionBeanMock.getValues() >> map
			//cToolsMock.getValueForConfigKey("FlagDrivenFunctions","categoryBuckets",false) >> true
			1*cToolsMock.getValueForConfigKey(*_) >> true
			rSkinnyVO.setEventDate("02/10/2014")
			rSkinnyVO1.setEventDate("07/10/2014")
			1*grManagerMock.getNotifyRegistrantMsgType("sku1","02/10/2014") >> "thresholdMessage"
			1*grManagerMock.getNotifyRegistrantMsgType("sku1","07/10/2014") >> ""
			
			profileMock.getRepositoryId() >> "pId1"
			1*grManagerMock.recommendRegistryList("pId1") >> [rSummeryVO]
			
		 when:
		 	ProfileRegistryListVO value = rAPIManager.getOwnAndRecommendedRegistriesForProfile("sku1")
		 then:
			 1*requestMock.setParameter("siteId", _)
			 1*aITRDroplet.service(requestMock, responseMock)
			 rSkinnyVO.getThresholdMsg().equals("thresholdMessage")
			 value.getRecommendedRegistryList() == [rSummeryVO]
			 value.getProfileRegistryList() == [rSkinnyVO,rSkinnyVO1]
	 }
	 
	 def"getOwnAndRecommendedRegistriesForProfile .TC when notification of registrant is fasle"(){
		 given:

			 requestMock.getObjectParameter("registrySkinnyVOList") >>  [rSkinnyVO,rSkinnyVO1]
		    1* cToolsMock.getValueForConfigKey(*_) >> false
			
			profileMock.getRepositoryId() >> "pId1"
			1*grManagerMock.recommendRegistryList("pId1") >> null
			
		 when:
			 ProfileRegistryListVO value = rAPIManager.getOwnAndRecommendedRegistriesForProfile("sku1")
		 then:
			 1*requestMock.setParameter("siteId", _)
			 1*aITRDroplet.service(requestMock, responseMock)
			 value.getRecommendedRegistryList() != null
			 value.getProfileRegistryList() == [rSkinnyVO,rSkinnyVO1]
	 }
	 
	 def"getOwnAndRecommendedRegistriesForProfile .TC when passed skuId is empty"(){
		 given:
			Map map = new HashMap()
			map.put("registrySkinnyVOList", [rSkinnyVO,rSkinnyVO1])

			 requestMock.getObjectParameter("registrySkinnyVOList") >>  null
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			sessionBeanMock.getValues() >> map
			1*cToolsMock.getValueForConfigKey(*_) >> false
			
			profileMock.getRepositoryId() >> "pId1"
			1*grManagerMock.recommendRegistryList("pId1") >> null
			
		 when:
			 ProfileRegistryListVO value = rAPIManager.getOwnAndRecommendedRegistriesForProfile("")
		 then:
			 1*requestMock.setParameter("siteId", _)
			 1*aITRDroplet.service(requestMock, responseMock)
			 value.getRecommendedRegistryList() != null
			 value.getProfileRegistryList() == [rSkinnyVO,rSkinnyVO1]
	 }
	 
	 def"getOwnAndRecommendedRegistriesForProfile .TC when registrySkinnyVOList is null gets from  sessionBean"(){
		 given:
			Map map = new HashMap()
			map.put("registrySkinnyVOList", null)

			 requestMock.getObjectParameter("registrySkinnyVOList") >>  null
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			
			sessionBeanMock.getValues() >> map
			profileMock.getRepositoryId() >> "pId1"
			1*grManagerMock.recommendRegistryList("pId1") >> null
		 when:
			 ProfileRegistryListVO value = rAPIManager.getOwnAndRecommendedRegistriesForProfile("")
		 then:
			 1*requestMock.setParameter("siteId", _)
			 1*aITRDroplet.service(requestMock, responseMock)
			 value.getRecommendedRegistryList() != null
			 0*cToolsMock.getValueForConfigKey(*_)
			 value.getProfileRegistryList() == null
	 }
	 
	 def"getOwnAndRecommendedRegistriesForProfile .TC for IOException "(){
		 given:
		    1*aITRDroplet.service(requestMock, responseMock) >> {throw new IOException("exception")}
		 when:
			 ProfileRegistryListVO value = rAPIManager.getOwnAndRecommendedRegistriesForProfile("")
		 then:
		     0*grManagerMock.recommendRegistryList("pId1")
			 BBBSystemException exe = thrown()
	 }
	 
	 def"getOwnAndRecommendedRegistriesForProfile .TC for ServletException "(){
		 given:
			1*aITRDroplet.service(requestMock, responseMock) >> {throw new ServletException("exception")}
		 when:
			 ProfileRegistryListVO value = rAPIManager.getOwnAndRecommendedRegistriesForProfile("")
		 then:
			 0*grManagerMock.recommendRegistryList("pId1")
			 BBBSystemException exe = thrown()
	 }

	 /******************************************* getOwnRegistriesForProfileCount *****************************************/
	 
	 def"getOwnRegistriesForProfileCount. TC to get the  registry list count"(){
		 given:
		 	1*grManagerMock.getUserRegistryListCount(profileMock, _) >> 2
		 when:
		 	int value = rAPIManager.getOwnRegistriesForProfileCount()
		 then:
		 value == 2
	 }
	 
	 /******************************************* checkUserStatus ********************************************************/
	 
	 def"checkUserStatus. TC to check user status"(){
		 given:
		 	profileMock.isTransient() >> true
		 when:
		 	Boolean value = rAPIManager.checkUserStatus()
		 then:
		 value
	 }
	 
	 /**************************************************setPriceInRegistryItem *****************************************/
	 
	 def"setPriceInRegistryItem.TC to set the price in item vo for registry item "(){
		 given:
		 	List itemVOList = [rItemVO, rItemVO1, rItemVO2, rItemVO3,null]
			 Map<String, List<RegistryItemVO>> map = ["id1" : itemVOList, "id2" :null]
			 map.put(null, null)
			 rItemVO.setSku(11)
			 rItemVO1.setSku(12)
			 rItemVO2.setSku(13)
			 rItemVO3.setSku(14)
			 
			 1*cToolsMock.getParentProductForSku("11", true) >> "prod1"
			 1*cToolsMock.getParentProductForSku("12", true) >> "prod2"
			 1*cToolsMock.getParentProductForSku("13", true) >> {throw new BBBBusinessException("exception")}
			 1*cToolsMock.getParentProductForSku("14", true) >> {throw new BBBSystemException("exception")}

			 			 
			 1*cToolsMock.getSalePrice("prod1", "11") >> 0
			 1*cToolsMock.getSalePrice("prod2", "12") >> 5
			 
			 1*cToolsMock.getListPrice("prod1", "11") >> 10
			 1*cToolsMock.getSkuIncartFlag("11") >> true
			 1*cToolsMock.getSkuIncartFlag("12") >> false
			 
			 1*cToolsMock.getIncartPrice("prod1", "11") >> 30
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)
			 
		 when:
		 	rAPIManager.setPriceInRegistryItem(map, false)
		 then:
			 rItemVO.getPrice() == '30.0'
			 rItemVO.getTotalPrice() == 50.0
			 rItemVO.getTotalDeliveryCharges() == 20.0
	 }
	 
	 def"setPriceInRegistryItem.TC to set the price in item vo for registry item when GiftGiver flag is true"(){
		 given:
			 List itemVOList = [rItemVO]
			 Map<String, List<RegistryItemVO>> map = ["id1" : itemVOList]
			 rItemVO.setSku(11)
			 
			 1*cToolsMock.getParentProductForSku("11", true) >> "prod1"
						  
			 1*cToolsMock.getSalePrice("prod1", "11") >> 10
			 1*cToolsMock.getSkuIncartFlag("11") >> true
			 
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)
			 
		 when:
			 rAPIManager.setPriceInRegistryItem(map, true)
		 then:
			 rItemVO.getPrice() == '10.0'
			 rItemVO.getTotalPrice() == 30.0
			 rItemVO.getTotalDeliveryCharges() == 20.0
			 0*cToolsMock.getIncartPrice("prod1", "11")
	 }
	 
	 /**********************************************convertCategoryBucketOutput *******************************/
	 
	 def"convertCategoryBucketOutput.Tc to get the RegistryCategoryBucketVO"(){
		 given:
			 SKUDetailVO sDetaiVo = new SKUDetailVO()
			 List itemVOList = [rItemVO,rItemVO1, rItemVO2,null]
			 List itemVOList1 = []
			 Map<String, List<RegistryItemVO>> map = ["id1" : itemVOList, "id2":itemVOList1]
	         
			 rItemVO.setSku(11)
			 rItemVO1.setSku(12)
			 rItemVO2.setSku(13)
			 
			 1*cToolsMock.getParentProductForSku("11", true) >> "prod1"
			 1*cToolsMock.getParentProductForSku("12", true) >> {throw new BBBBusinessException("exception")}
			 1*cToolsMock.getParentProductForSku("13", true) >> {throw new BBBSystemException("exception")}
			 
			 rItemVO.setsKUDetailVO(sDetaiVo)
		 
		 when:
		 	List<RegistryCategoryBucketVO> value = rAPIManager.convertCategoryBucketOutput(map)
		 then:
			 sDetaiVo.getParentProdId() == "prod1"
			 RegistryCategoryBucketVO fItem = value.get(0)
			 fItem.getCatgoryName() == "id1"
			 fItem.getItems() == itemVOList
	 }
	 
	 /********************************************** fetchPOBoxAddress ***********************************/
	 
	 def"fetchPOBoxAddress. TC to fetch PO BOX address"(){
		 given:
			  AddressVO addressVO = new AddressVO()
	
			 1*riDisplayDroplet.service(requestMock, responseMock)
			 requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
			 rSummeryVO.setShippingAddress(addressVO)
			 addressVO.setAddressLine1("124/sf new")
			 addressVO.setAddressLine2("delhi")
			 
			 rSummeryVO.setRegistryId("rid1")
			 
			 1*poBoxVDropletMock.service(requestMock, responseMock)
			 requestMock.getParameter("isValid") >> true
		 when:
		 	Map<String,String> value = rAPIManager.fetchPOBoxAddress("rId")
		 then:
		 	 value.get("summaryVORegId") == "rid1"
			 value.get("poBoxAddress") == "true"
			 1 * requestMock.setParameter("registryId", "rId");
			 1 * requestMock.setParameter("displayView", "guest")
             1 * requestMock.setParameter('address', '124/sf new delhi')	 }


	def"fetchPOBoxAddress. TC to fetch PO BOX address. for ServletException"(){
		given:
			 AddressVO addressVO = new AddressVO()
	
			1*riDisplayDroplet.service(requestMock, responseMock) >> {throw new ServletException("exception")}
			requestMock.getObjectParameter("registrySummaryVO") >> null
						
			1*poBoxVDropletMock.service(requestMock, responseMock) >> {throw new ServletException("exception")}
			requestMock.getParameter("isValid") >> true
		when:
			Map<String,String> value = rAPIManager.fetchPOBoxAddress("rId")
		then:
			value.get("summaryVORegId") == null
			value.get("poBoxAddress") == "true"
			1 * requestMock.setParameter("registryId", "rId");
			1 * requestMock.setParameter("displayView", "guest")
			0 * requestMock.setParameter('address', '124/sf new delhi')	
			
		}
	
	def"fetchPOBoxAddress. TC to fetch PO BOX address. for IOException"(){
		given:
			 AddressVO addressVO = new AddressVO()
	
			1*riDisplayDroplet.service(requestMock, responseMock) >> {throw new IOException("exception")}
			requestMock.getObjectParameter("registrySummaryVO") >> null
						
			1*poBoxVDropletMock.service(requestMock, responseMock) >> {throw new IOException("exception")}
			requestMock.getParameter("isValid") >> true
		when:
			Map<String,String> value = rAPIManager.fetchPOBoxAddress("rId")
		then:
			value.get("summaryVORegId") == null
			value.get("poBoxAddress") == "true"
			1 * requestMock.setParameter("registryId", "rId");
			1 * requestMock.setParameter("displayView", "guest")
			0 * requestMock.setParameter('address', '124/sf new delhi')
			
		}
	
	/********************************************************* getRegistryDetailAPI ******************************************/
	
	def"getRegistryDetailAPI. TC to get the registry detail api"(){
		given:
		rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", "true")
		inputParam.put("c1id", "cid1")
		inputParam.put("c2id", "cid2")
		inputParam.put("c3id", "cid3")
		inputParam.put("c1name", "Cname1")
		inputParam.put("c2name", "Cname2")
		inputParam.put("c3name", "Cname3")
		
		inputParam.put("displayView", "owner")
		inputParam.put("sortSeq", "2")
		inputParam.put("view", "view")
		inputParam.put("startIdx", "1")
		inputParam.put("bulkSize" , "4")
		inputParam.put("isGiftGiver", "")
		inputParam.put("isAvailForWebPurchaseFlag", "true")
		inputParam.put("fromRegistryController", "false")
		inputParam.put("fromchecklist", "fChecklist")
		inputParam.put("eph", "yes")
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		
        profileMock.getRepositoryId() >> "prId"
		sessionBeanMock.getValues() >> map
		1*grManagerMock.fetchUserRegistries(_,"prId") >> [item1]
		item1.getRepositoryId() >> "item1"
		
		riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("12/12/2014")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> "01/01/2014"
		requestMock.getParameter("daysToGo") >> 10
		requestMock.getParameter("check") >> "false"
		requestMock.getParameter("daysToNextCeleb") >> 12
		
		1*dcDropletMock.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> null
		
		rSummeryVO.setRegistryType(rType)  
		1*rType.getRegistryTypeName() >> "registryType"
		
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> true
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		1*cheManager.showCheckListButton("registryType") >> {throw new BBBBusinessException("exception")}
		
		
		1*ritemDisplayDroplet.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> "error"
		requestMock.getParameter("showStartBrowsing") >> "ssBrowsing"
		requestMock.getObjectParameter("priceRangeList") >> ["range"]
		requestMock.getParameter("skuList") >> "skuList"
		requestMock.getParameter("sortSequence") >> "2"
		requestMock.getParameter("emptyList") >> "true"
		
		requestMock.getObjectParameter("categoryBuckets") >> ["range": itemList]
		
		//Start set price
		 	List itemVOList = [rItemVO, rItemVO1, rItemVO2, rItemVO3,null]
			 Map<String, List<RegistryItemVO>> instockMap = ["range" : itemVOList, "id2" :null]
			 rItemVO.setSku(11)
			 rItemVO1.setSku(12)
			 rItemVO2.setSku(13)
			 rItemVO3.setSku(14)
			 
			 5*cToolsMock.getParentProductForSku("11", true) >> "prod1"
			 4*cToolsMock.getParentProductForSku("12", true) >> "prod2"
			 4*cToolsMock.getParentProductForSku("13", true) >> {throw new BBBBusinessException("exception")}
			 4*cToolsMock.getParentProductForSku("14", true) >> {throw new BBBSystemException("exception")}

			 			 
			 3*cToolsMock.getSalePrice("prod1", "11") >> 0
			 2*cToolsMock.getSalePrice("prod2", "12") >> 5
			 
			 3*cToolsMock.getListPrice("prod1", "11") >> 10
			 3*cToolsMock.getSkuIncartFlag("11") >> true
			 2*cToolsMock.getSkuIncartFlag("12") >> false
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)		// start convertCategoryBucketOutputForPrice
		
		
		//end
			 rItemVO.setsKUDetailVO(sDetaiVo)
			 rItemVO1.setsKUDetailVO(sDetaiVo1)
		requestMock.getObjectParameter("categoryVOMap") >> ["cVO1" : repositoryCVO ]
		
		requestMock.getObjectParameter("inStockCategoryBuckets") >> instockMap
		requestMock.getObjectParameter("notInStockCategoryBuckets") >> instockMap
		
		
		
		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> "2"
		requestMock.getParameter("emptyOutOfStockListFlag") >> "false"
		
		1*cheManager.showC1CategoryOnRlp("cid1", "rid", _) >> "notShow"
		
		profileMock.isTransient() >> false
		
		1*grManagerMock.isRegistryOwnedByProfile("prId", "rid", _) >> false
		
		1*grTools.getRecommendationCount("rid") >> [1,2]
		
		1*grRecManager.getEmailOptInValue("rid") >> 1
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
			1*requestMock.setParameter("address", "124/sf new delhi")
			2*requestMock.setParameter("registryId", "rid");
			1*requestMock.setParameter("displayView", "owner");
	        //map.get("userRegistriesList") == null
			1*requestMock.setParameter("eventDate", "12/12/2014")
			rSummeryVO.getDaysToGo() == 10
			!rSummeryVO.isEventYetToCome()
			rSummeryVO.getDaysToNextCeleb() == 12
			
			1*requestMock.setParameter("siteId", _);
			1*requestMock.setParameter("sortSeq", "2");
			1*requestMock.setParameter(BBBGiftRegistryConstants.VIEW, "view");
			1*requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, "registryType")
			1*requestMock.setParameter(BBBGiftRegistryConstants.START_INDEX, 1)
			1*requestMock.setParameter(BBBGiftRegistryConstants.BULK_SIZE, 4)
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_GIFT_GIVER, true)
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, true)
			1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, "fChecklist")
			
			1*requestMock.setParameter("eventType", "eType")
			
			1*requestMock.setParameter(BBBCoreConstants.C1_ID, "cid1")
			1*requestMock.setParameter(BBBCoreConstants.C2_ID, "cid2")
			1*requestMock.setParameter(BBBCoreConstants.C3_ID, "cid3")
			1*requestMock.setParameter(BBBCoreConstants.C1_NAME, "Cname1")
			1*requestMock.setParameter(BBBCoreConstants.C2_NAME, "Cname2")
			1*requestMock.setParameter(BBBCoreConstants.C3_NAME, "Cname3")
			//1*requestMock.setParameter(BBBCoreConstants.QTY, 2)
	
			sDetaiVo.getParentProdId() == "prod1"
		
			infoDetailVO.getPriceRangeList() == ["range"]
			infoDetailVO.getIsListEmpty() == "true"
			infoDetailVO.getSortSequence() == "2"
			infoDetailVO.getSkuList() == "skuList"
			infoDetailVO.getCount() ==2
			infoDetailVO.getPromoBox() == pBoxVO
			infoDetailVO.getShowStartBrowsing() == "ssBrowsing"

	
	}
	
	
	def"getRegistryDetailAPI. TC to get the registry detail when c2id,c2id,c1name,c2name is null"(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO() 
/*		rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
	*/	//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("userRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", "true")
		inputParam.put("c1id", "ci")
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", "owner")
		inputParam.put("sortSeq", "")
		inputParam.put("view", "view")
		inputParam.put("startIdx", "")
		inputParam.put("bulkSize" , "")
		inputParam.put("isGiftGiver", "false")
		inputParam.put("isAvailForWebPurchaseFlag", "")
		inputParam.put("fromRegistryController", "")
		inputParam.put("fromchecklist", "")
		
		//rAPIManager.getSiteId() >> "BedBathCanada1"
		
		profileMock.getRepositoryId() >> "prId"
		sessionBeanMock.getValues() >> map
		//1*grManagerMock.fetchUserRegistries(_,"prId") >> [item1]
		item1.getRepositoryId() >> "item1"
		
		riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("0")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> "01/01/2014"
		requestMock.getParameter("daysToGo") >> 10
		requestMock.getParameter("check") >> "true"
		//requestMock.getParameter("daysToNextCeleb") >> 12
		
		1*dcDropletMock.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> null
		
		rSummeryVO.setRegistryType(rType)
		1*rType.getRegistryTypeName() >> "registryType"
		
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> true
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		1*cheManager.showCheckListButton("registryType") >> false
		
		
		1*ritemDisplayDroplet.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> "error"
		requestMock.getParameter("showStartBrowsing") >> "ssBrowsing"
		requestMock.getObjectParameter("priceRangeList") >> ["range"]
		requestMock.getParameter("skuList") >> "skuList"
		requestMock.getParameter("sortSequence") >> "2"
		requestMock.getParameter("emptyList") >> "true"
		
		requestMock.getObjectParameter("categoryBuckets") >> ["range": itemList]
		
		//Start set price
			 List itemVOList = [rItemVO]
			 Map<String, List<RegistryItemVO>> instockMap = ["range" : itemVOList, "id2" :null]
			 rItemVO.setSku(11)
			 
			 4*cToolsMock.getParentProductForSku("11", true) >> "prod1"

						  
			 3*cToolsMock.getSalePrice("prod1", "11") >> 0
			 
			 3*cToolsMock.getListPrice("prod1", "11") >> 10
			 3*cToolsMock.getSkuIncartFlag("11") >> true
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)	
			 cToolsMock.getSkuIncartFlag("11") >> true
			 
			 cToolsMock.getIncartPrice("prod1", "11") >> 30
	// start convertCategoryBucketOutputForPrice
		
		
		//end
			 rItemVO.setsKUDetailVO(sDetaiVo)
			 rItemVO1.setsKUDetailVO(sDetaiVo1)
		requestMock.getObjectParameter("categoryVOMap") >> null
		
		requestMock.getObjectParameter("inStockCategoryBuckets") >> instockMap
		requestMock.getObjectParameter("notInStockCategoryBuckets") >> instockMap
		
		
		
		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> null
		requestMock.getParameter("emptyOutOfStockListFlag") >> "true"
		
		requestMock.getObjectParameter("registryItemsAll") >> [rItemVO1]
		requestMock.getObjectParameter("ephCategoryBuckets") >> [myIteCatVO]
		requestMock.getParameter("expandedCategory") >> "expCat1"
		
		requestMock.getObjectParameter("inStockEPHCategoryBuckets") >> [myIteCatVO] 
		
		
		1*cheManager.showC1CategoryOnRlp("ci", "rid", _) >> "Show"
		
		profileMock.isTransient() >> true
		
		//1*grManagerMock.isRegistryOwnedByProfile("prId", "rid", _) >> false
		
		1*grTools.getRecommendationCount("rid") >> [1,2]
		
		1*grRecManager.getEmailOptInValue("rid") >> 1
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
			1*requestMock.setParameter("address", "124/sf new delhi")
			2*requestMock.setParameter("registryId", "rid");
			1*requestMock.setParameter("displayView", "owner");
			//map.get("userRegistriesList") == null
			rSummeryVO.getDaysToGo() == 10
			
			1*requestMock.setParameter("siteId", _);
			1*requestMock.setParameter("sortSeq", "1");
			1*requestMock.setParameter(BBBGiftRegistryConstants.VIEW, "view");
			1*requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, "registryType")
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, true)
			1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, null)
			
			1*requestMock.setParameter("eventType", "eType")
			
			//1*requestMock.setParameter(BBBCoreConstants.QTY, 2)
	
			sDetaiVo.getParentProdId() == "prod1"
		
			infoDetailVO.getPriceRangeList() == ["range"]
			infoDetailVO.getIsListEmpty() == "true"
			infoDetailVO.getSortSequence() == "2"
			infoDetailVO.getSkuList() == "skuList"
			infoDetailVO.getCount() ==0
			infoDetailVO.getPromoBox() == pBoxVO
			infoDetailVO.getShowStartBrowsing() == "ssBrowsing"

	
	}
	
	def"getRegistryDetailAPI. TC to get the registry detail when c2id is null"(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
/*		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
	*/	//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", "true")
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", "owner")
		inputParam.put("sortSeq", "2")
		inputParam.put("view", "")
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", "false")
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", null)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		
		profileMock.getRepositoryId() >> "prId"
		sessionBeanMock.getValues() >> map
		1*grManagerMock.fetchUserRegistries(_,"prId") >> null
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> "0"
		requestMock.getParameter("daysToGo") >> 10
		requestMock.getParameter("check") >> "true"
		//requestMock.getParameter("daysToNextCeleb") >> 12
		
		requestMock.getObjectParameter("errorMsg") >> null
		
		rSummeryVO.setRegistryType(rType)
		1*rType.getRegistryTypeName() >> "registryType"
		
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> true
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		1*cheManager.showCheckListButton("registryType") >> false
		
		
		1*ritemDisplayDroplet.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> "error"
		requestMock.getParameter("showStartBrowsing") >> "ssBrowsing"
		requestMock.getObjectParameter("priceRangeList") >> ["range"]
		requestMock.getParameter("skuList") >> "skuList"
		requestMock.getParameter("sortSequence") >> "2"
		requestMock.getParameter("emptyList") >> "true"
		
		requestMock.getObjectParameter("categoryBuckets") >> ["range": itemList]
		
		//Start set price
			 List itemVOList = [rItemVO]
			 Map<String, List<RegistryItemVO>> instockMap = ["range" : itemVOList, "id2" :null]
			 rItemVO.setSku(11)
			 
			 2*cToolsMock.getParentProductForSku("11", true) >> "prod1"

						  
			 1*cToolsMock.getSalePrice("prod1", "11") >> 0
			 
			 1*cToolsMock.getListPrice("prod1", "11") >> 10
			 1*cToolsMock.getSkuIncartFlag("11") >> true
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)
			 cToolsMock.getSkuIncartFlag("11") >> true
			 
			 cToolsMock.getIncartPrice("prod1", "11") >> 30
	// start convertCategoryBucketOutputForPrice
		
		
		//end
			 rItemVO.setsKUDetailVO(sDetaiVo)
			 rItemVO1.setsKUDetailVO(sDetaiVo1)
		requestMock.getObjectParameter("categoryVOMap") >> null
		
		requestMock.getObjectParameter("inStockCategoryBuckets") >> null
		requestMock.getObjectParameter("notInStockCategoryBuckets") >> null
		
		
		
		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> null
		requestMock.getParameter("emptyOutOfStockListFlag") >> null
		
		requestMock.getObjectParameter("registryItemsAll") >> [rItemVO1]
		requestMock.getObjectParameter("ephCategoryBuckets") >> [myIteCatVO]
		requestMock.getParameter("expandedCategory") >> "expCat1"
		
		requestMock.getObjectParameter("inStockEPHCategoryBuckets") >> [myIteCatVO]
		
		
		1*cheManager.showC1CategoryOnRlp(_, "rid", _) >> {throw new BBBBusinessException("exception")}
		
		profileMock.isTransient() >> false
		
		1*grManagerMock.isRegistryOwnedByProfile(_, "rid", _) >> {throw new BBBSystemException("exception")}
		
		//1*grTools.getRecommendationCount("rid") >> [1,2]
		
		//1*grRecManager.getEmailOptInValue("rid") >> 1
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
			1*requestMock.setParameter("address", "124/sf new delhi")
			2*requestMock.setParameter("registryId", "rid");
			1*requestMock.setParameter("displayView", "owner");
			
			1*requestMock.setParameter("siteId", _);
			1*requestMock.setParameter("sortSeq", "2");
			1*requestMock.setParameter(BBBGiftRegistryConstants.VIEW, '1');
			1*requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, "registryType")
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, true)
			1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, null)
			
			1*requestMock.setParameter("eventType", "eType")
			
			//1*requestMock.setParameter(BBBCoreConstants.QTY, 2)
	
			sDetaiVo.getParentProdId() == "prod1"
		
			infoDetailVO.getErrorMessage() == "error_registry_detail"

	
	}
	
	def"getRegistryDetailAPI. TC to get the registry detail when load selected catagory is false"(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", "false")
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", "")
		inputParam.put("sortSeq", null)
		inputParam.put("view", null)
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", null)
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", null)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada1"
		
		profileMock.getRepositoryId() >> "prId"
		sessionBeanMock.getValues() >> map
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("12/20/2014")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> ""
		//requestMock.getParameter("daysToGo") >> 10
		//requestMock.getParameter("check") >> "true"
		//requestMock.getParameter("daysToNextCeleb") >> 12
		
		requestMock.getObjectParameter("errorMsg") >> null
		
		rSummeryVO.setRegistryType(rType)
		1*rType.getRegistryTypeName() >> "registryType"
		
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> true
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		1*cheManager.showCheckListButton("registryType") >> false
		
		
		1*ritemDisplayDroplet.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> "error"
		requestMock.getParameter("showStartBrowsing") >> "ssBrowsing"
		requestMock.getObjectParameter("priceRangeList") >> ["range"]
		requestMock.getParameter("skuList") >> "skuList"
		requestMock.getParameter("sortSequence") >> "1"
		requestMock.getParameter("emptyList") >> "true"
		
		requestMock.getObjectParameter("categoryBuckets") >> null //["range": itemList]
		
		//Start set price
			 List itemVOList = [rItemVO]
			 Map<String, List<RegistryItemVO>> instockMap = ["range" : itemVOList, "id2" :null]
			 rItemVO.setSku(11)
			 
			// 2*cToolsMock.getParentProductForSku("11", true) >> "prod1"

						  
			// 1*cToolsMock.getSalePrice("prod1", "11") >> 0
			 
			 //1*cToolsMock.getListPrice("prod1", "11") >> 10
			 //1*cToolsMock.getSkuIncartFlag("11") >> true
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)
			 cToolsMock.getSkuIncartFlag("11") >> true
			 
			 cToolsMock.getIncartPrice("prod1", "11") >> 30
	// start convertCategoryBucketOutputForPrice
		
		
		//end
			 rItemVO.setsKUDetailVO(sDetaiVo)
			 rItemVO1.setsKUDetailVO(sDetaiVo1)
		requestMock.getObjectParameter("categoryVOMap") >> null
		
		requestMock.getObjectParameter("inStockCategoryBuckets") >> null
		requestMock.getObjectParameter("notInStockCategoryBuckets") >> null
		
		
		
		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> null
		requestMock.getParameter("emptyOutOfStockListFlag") >> null
		
		requestMock.getObjectParameter("registryItemsAll") >> [rItemVO1]
		requestMock.getObjectParameter("ephCategoryBuckets") >> [myIteCatVO]
		requestMock.getParameter("expandedCategory") >> "expCat1"
		
		requestMock.getObjectParameter("inStockEPHCategoryBuckets") >> [myIteCatVO]
		requestMock.getObjectParameter("notInStockEPHCategoryBuckets") >> [myIteCatVO]
		
				
		1*cheManager.showC1CategoryOnRlp(_, "rid", _) >> {throw new BBBBusinessException("exception")}
		
		profileMock.isTransient() >> false
		
		1*grManagerMock.isRegistryOwnedByProfile(_, "rid", _) >> {throw new BBBBusinessException("exception")}
		
		1*grTools.getRecommendationCount("rid") >> [1,2]
		
		1*grRecManager.getEmailOptInValue("rid") >> 1
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
			1*requestMock.setParameter("address", "124/sf new delhi")
			2*requestMock.setParameter("registryId", "rid");
			1*requestMock.setParameter("displayView", "guest");
			
			1*requestMock.setParameter("siteId", _);
			1*requestMock.setParameter("sortSeq", "1");
			1*requestMock.setParameter(BBBGiftRegistryConstants.VIEW, '1');
			1*requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, "registryType")
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, true)
			1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, null)
			
			1*requestMock.setParameter("eventType", "eType")
			0*grManagerMock.fetchUserRegistries(_,"prId")
			//1*requestMock.setParameter(BBBCoreConstants.QTY, 2)
			infoDetailVO.getInStockEPHCategoryBuckets() ==  [myIteCatVO]
			infoDetailVO.getNotInStockEPHCategoryBuckets() == [myIteCatVO]

	
	}
	
	
	def"getRegistryDetailAPI. TC to get the registry detail when load selected catagory is null"(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
/*		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
*/		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", null)
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", "owner")
		inputParam.put("sortSeq", null)
		inputParam.put("view", null)
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", "false")
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", null)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> null
		
		profileMock.getRepositoryId() >> "prId"
		//sessionBeanMock.getValues() >> map
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> ""
		//requestMock.getParameter("daysToGo") >> 10
		//requestMock.getParameter("check") >> "true"
		//requestMock.getParameter("daysToNextCeleb") >> 12
		
		requestMock.getObjectParameter("errorMsg") >> null
		
		rSummeryVO.setRegistryType(rType)
		1*rType.getRegistryTypeName() >> "registryType"
		
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> true
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		1*cheManager.showCheckListButton("registryType") >> {throw new BBBBusinessException("exception")}
		
		
		1*ritemDisplayDroplet.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> "error"
		requestMock.getParameter("showStartBrowsing") >> "ssBrowsing"
		requestMock.getObjectParameter("priceRangeList") >> ["range"]
		requestMock.getParameter("skuList") >> "skuList"
		requestMock.getParameter("sortSequence") >> "1"
		requestMock.getParameter("emptyList") >> "true"
		
		requestMock.getObjectParameter("categoryBuckets") >> null //["range": itemList]
		
		//Start set price
			 List itemVOList = [rItemVO]
			 Map<String, List<RegistryItemVO>> instockMap = ["range" : itemVOList, "id2" :null]
			 rItemVO.setSku(11)
			 
			// 2*cToolsMock.getParentProductForSku("11", true) >> "prod1"

						  
			// 1*cToolsMock.getSalePrice("prod1", "11") >> 0
			 
			 //1*cToolsMock.getListPrice("prod1", "11") >> 10
			 //1*cToolsMock.getSkuIncartFlag("11") >> true
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)
			 cToolsMock.getSkuIncartFlag("11") >> true
			 
			 cToolsMock.getIncartPrice("prod1", "11") >> 30
	// start convertCategoryBucketOutputForPrice
		
		
		//end
			 rItemVO.setsKUDetailVO(sDetaiVo)
			 rItemVO1.setsKUDetailVO(sDetaiVo1)
		requestMock.getObjectParameter("categoryVOMap") >> null
		
		requestMock.getObjectParameter("inStockCategoryBuckets") >> null
		requestMock.getObjectParameter("notInStockCategoryBuckets") >> null
		
		
		
		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> null
		requestMock.getParameter("emptyOutOfStockListFlag") >> null
		
		requestMock.getObjectParameter("registryItemsAll") >> [rItemVO1]
		requestMock.getObjectParameter("ephCategoryBuckets") >> [myIteCatVO]
		requestMock.getParameter("expandedCategory") >> "expCat1"
		
		requestMock.getObjectParameter("inStockEPHCategoryBuckets") >> [myIteCatVO]
		requestMock.getObjectParameter("notInStockEPHCategoryBuckets") >> [myIteCatVO]
		
				
		1*cheManager.showC1CategoryOnRlp(_, "rid", _) >> {throw new BBBBusinessException("exception")}
		
		profileMock.isTransient() >> false
		
		1*grManagerMock.isRegistryOwnedByProfile(_, "rid", _) >> {throw new BBBBusinessException("exception")}
		
		1*grTools.getRecommendationCount("rid") >> [1,2]
		
		1*grRecManager.getEmailOptInValue("rid") >> 1
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
			1*requestMock.setParameter("address", "124/sf new delhi")
			2*requestMock.setParameter("registryId", "rid");
			1*requestMock.setParameter("displayView", "owner");
			
			1*requestMock.setParameter("siteId", _);
			1*requestMock.setParameter("sortSeq", "1");
			1*requestMock.setParameter(BBBGiftRegistryConstants.VIEW, '1');
			1*requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, "registryType")
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, true)
			1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, null)
			
			1*requestMock.setParameter("eventType", "eType")
			0*grManagerMock.fetchUserRegistries(_,"prId")
			//1*requestMock.setParameter(BBBCoreConstants.QTY, 2)
			infoDetailVO.getInStockEPHCategoryBuckets() ==  null
			infoDetailVO.getNotInStockEPHCategoryBuckets() == null

	
	}
	
	def"getRegistryDetailAPI. TC to get the registry detail when when isRegistryOwnedByProfile is true "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
/*		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
*/		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", null)
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", null)
		inputParam.put("sortSeq", "2")
		inputParam.put("view", null)
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", "")
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", null)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> null
		
		profileMock.getRepositoryId() >> "prId"
		//sessionBeanMock.getValues() >> map
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> ""
		//requestMock.getParameter("daysToGo") >> 10
		//requestMock.getParameter("check") >> "true"
		//requestMock.getParameter("daysToNextCeleb") >> 12
		
		requestMock.getObjectParameter("errorMsg") >> null
		
		rSummeryVO.setRegistryType(rType)
		1*rType.getRegistryTypeName() >> "registryType"
		
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> true
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		1*cheManager.showCheckListButton("registryType") >> false
		
		
		1*ritemDisplayDroplet.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> "error"
		requestMock.getParameter("showStartBrowsing") >> "ssBrowsing"
		requestMock.getObjectParameter("priceRangeList") >> ["range"]
		requestMock.getParameter("skuList") >> "skuList"
		requestMock.getParameter("sortSequence") >> "2"
		requestMock.getParameter("emptyList") >> "true"
		
		requestMock.getObjectParameter("categoryBuckets") >> null //["range": itemList]
		
		//Start set price
			 List itemVOList = [rItemVO]
			 Map<String, List<RegistryItemVO>> instockMap = ["range" : itemVOList, "id2" :null]
			 rItemVO.setSku(11)
			 
			// 2*cToolsMock.getParentProductForSku("11", true) >> "prod1"

						  
			// 1*cToolsMock.getSalePrice("prod1", "11") >> 0
			 
			 //1*cToolsMock.getListPrice("prod1", "11") >> 10
			 //1*cToolsMock.getSkuIncartFlag("11") >> true
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)
			 cToolsMock.getSkuIncartFlag("11") >> true
			 
			 cToolsMock.getIncartPrice("prod1", "11") >> 30
	// start convertCategoryBucketOutputForPrice
		
		
		//end
			 rItemVO.setsKUDetailVO(sDetaiVo)
			 rItemVO1.setsKUDetailVO(sDetaiVo1)
		requestMock.getObjectParameter("categoryVOMap") >> null
		
		requestMock.getObjectParameter("inStockCategoryBuckets") >> null
		requestMock.getObjectParameter("notInStockCategoryBuckets") >> null
		
		
		
		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> null
		requestMock.getParameter("emptyOutOfStockListFlag") >> null
		
		requestMock.getObjectParameter("registryItemsAll") >> [rItemVO1]
		requestMock.getObjectParameter("ephCategoryBuckets") >> [myIteCatVO]
		requestMock.getParameter("expandedCategory") >> "expCat1"
		
		requestMock.getObjectParameter("inStockEPHCategoryBuckets") >> [myIteCatVO]
		requestMock.getObjectParameter("notInStockEPHCategoryBuckets") >> [myIteCatVO]
		
				
		1*cheManager.showC1CategoryOnRlp(_, "rid", _) >> {throw new BBBBusinessException("exception")}
		
		profileMock.isTransient() >> false
		
		1*grManagerMock.isRegistryOwnedByProfile(_, "rid", _) >> true
		
		1*grTools.getRecommendationCount("rid") >> [1,2]
		
		1*grRecManager.getEmailOptInValue("rid") >> 1
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
			1*requestMock.setParameter("address", "124/sf new delhi")
			2*requestMock.setParameter("registryId", "rid");
			1*requestMock.setParameter("displayView", "guest");
			
			1*requestMock.setParameter("siteId", _);
			1*requestMock.setParameter(BBBGiftRegistryConstants.VIEW, '1');
			1*requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, "registryType")
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, true)
			1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, null)
			
			1*requestMock.setParameter("eventType", "eType")
			0*grManagerMock.fetchUserRegistries(_,"prId")
			//1*requestMock.setParameter(BBBCoreConstants.QTY, 2)
			infoDetailVO.getInStockEPHCategoryBuckets() ==  null
			infoDetailVO.getNotInStockEPHCategoryBuckets() == null

	
	}
	
	def"getRegistryDetailAPI. TC to get the registry detail when when GiftGiver is true and sort seq is 1 "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
	/*	rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
	*/	//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", null)
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", null)
		inputParam.put("sortSeq", "1")
		inputParam.put("view", null)
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", "")
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", null)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> null
		
		profileMock.getRepositoryId() >> "prId"
		//sessionBeanMock.getValues() >> map
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> ""
		//requestMock.getParameter("daysToGo") >> 10
		//requestMock.getParameter("check") >> "true"
		//requestMock.getParameter("daysToNextCeleb") >> 12
		
		requestMock.getObjectParameter("errorMsg") >> null
		
		rSummeryVO.setRegistryType(rType)
		1*rType.getRegistryTypeName() >> "registryType"
		
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> {throw new BBBBusinessException("exception")}
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		1*cheManager.showCheckListButton("registryType") >> false
		
		
		1*ritemDisplayDroplet.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> "error"
		requestMock.getParameter("showStartBrowsing") >> "ssBrowsing"
		requestMock.getObjectParameter("priceRangeList") >> ["range"]
		requestMock.getParameter("skuList") >> "skuList"
		requestMock.getParameter("sortSequence") >> "1"
		requestMock.getParameter("emptyList") >> "true"
		
		requestMock.getObjectParameter("categoryBuckets") >> null //["range": itemList]
		
		//Start set price
			 List itemVOList = [rItemVO]
			 Map<String, List<RegistryItemVO>> instockMap = ["range" : itemVOList, "id2" :null]
			 rItemVO.setSku(11)
			 
			 cToolsMock.getParentProductForSku("11", true) >> "prod1"

						  
			 cToolsMock.getSalePrice("prod1", "11") >> 0
			 
			 cToolsMock.getListPrice("prod1", "11") >> 10
			 cToolsMock.getSkuIncartFlag("11") >> true
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)
			 cToolsMock.getSkuIncartFlag("11") >> true
			 
			 cToolsMock.getIncartPrice("prod1", "11") >> 30
	// start convertCategoryBucketOutputForPrice
		
		
		//end
			 rItemVO.setsKUDetailVO(sDetaiVo)
			 rItemVO1.setsKUDetailVO(sDetaiVo1)
		requestMock.getObjectParameter("categoryVOMap") >> null
		
		requestMock.getObjectParameter("inStockCategoryBuckets") >> instockMap
		requestMock.getObjectParameter("notInStockCategoryBuckets") >> instockMap
		
		
		
		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> null
		requestMock.getParameter("emptyOutOfStockListFlag") >> null
		
		requestMock.getObjectParameter("registryItemsAll") >> [rItemVO1]
		requestMock.getObjectParameter("ephCategoryBuckets") >> [myIteCatVO]
		requestMock.getParameter("expandedCategory") >> "expCat1"
		
		requestMock.getObjectParameter("inStockEPHCategoryBuckets") >> [myIteCatVO]
		requestMock.getObjectParameter("notInStockEPHCategoryBuckets") >> [myIteCatVO]
		
				
		1*cheManager.showC1CategoryOnRlp(_, "rid", _) >> {throw new BBBBusinessException("exception")}
		
		profileMock.isTransient() >> false
		
		1*grManagerMock.isRegistryOwnedByProfile(_, "rid", _) >> true
		
		1*grTools.getRecommendationCount("rid") >> [1,2]
		
		1*grRecManager.getEmailOptInValue("rid") >> 1
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
			1*requestMock.setParameter("address", "124/sf new delhi")
			2*requestMock.setParameter("registryId", "rid");
			1*requestMock.setParameter("displayView", "guest");
			
			1*requestMock.setParameter("siteId", _);
			1*requestMock.setParameter(BBBGiftRegistryConstants.VIEW, '1');
			1*requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, "registryType")
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, true)
			1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, null)
			
			1*requestMock.setParameter("eventType", "eType")
			0*grManagerMock.fetchUserRegistries(_,"prId")
			//1*requestMock.setParameter(BBBCoreConstants.QTY, 2)
			infoDetailVO.getInStockEPHCategoryBuckets() ==  null
			infoDetailVO.getNotInStockEPHCategoryBuckets() == null

	
	}
	
	
	def"getRegistryDetailAPI. TC to get the registry detail when chanle is not mobile "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
/*		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
*/		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", null)
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", null)
		inputParam.put("sortSeq", "1")
		inputParam.put("view", null)
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", "")
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", null)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> null
		
		profileMock.getRepositoryId() >> "prId"
		//sessionBeanMock.getValues() >> map
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> ""
		//requestMock.getParameter("daysToGo") >> 10
		//requestMock.getParameter("check") >> "true"
		//requestMock.getParameter("daysToNextCeleb") >> 12
		
		requestMock.getObjectParameter("errorMsg") >> null
		
		rSummeryVO.setRegistryType(rType)
		1*rType.getRegistryTypeName() >> "registryType"
		
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> {throw new BBBBusinessException("exception")}
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "notMobileWeb"
		//1*cheManager.showCheckListButton("registryType") >> false
		
		
		1*ritemDisplayDroplet.service(requestMock, responseMock)
		
		requestMock.getObjectParameter("errorMsg") >> "error"
		requestMock.getParameter("showStartBrowsing") >> "ssBrowsing"
		requestMock.getObjectParameter("priceRangeList") >> ["range"]
		requestMock.getParameter("skuList") >> "skuList"
		requestMock.getParameter("sortSequence") >> "1"
		requestMock.getParameter("emptyList") >> "true"
		
		requestMock.getObjectParameter("categoryBuckets") >> null //["range": itemList]
		
		//Start set price
			 List itemVOList = [rItemVO]
			 Map<String, List<RegistryItemVO>> instockMap = ["range" : itemVOList, "id2" :null]
			 rItemVO.setSku(11)
			 
			 cToolsMock.getParentProductForSku("11", true) >> "prod1"

						  
			 cToolsMock.getSalePrice("prod1", "11") >> 0
			 
			 cToolsMock.getListPrice("prod1", "11") >> 10
			 cToolsMock.getSkuIncartFlag("11") >> true
			 rItemVO.setDeliverySurcharge(10)
			 rItemVO.setAssemblyFees(10)
			 cToolsMock.getSkuIncartFlag("11") >> true
			 
			 cToolsMock.getIncartPrice("prod1", "11") >> 30
	// start convertCategoryBucketOutputForPrice
		
		
		//end
			 rItemVO.setsKUDetailVO(sDetaiVo)
			 rItemVO1.setsKUDetailVO(sDetaiVo1)
		requestMock.getObjectParameter("categoryVOMap") >> null
		
		requestMock.getObjectParameter("inStockCategoryBuckets") >> instockMap
		requestMock.getObjectParameter("notInStockCategoryBuckets") >> instockMap
		
		
		
		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> null
		requestMock.getParameter("emptyOutOfStockListFlag") >> null
		
		requestMock.getObjectParameter("registryItemsAll") >> [rItemVO1]
	
		profileMock.isTransient() >> false
		
		1*grManagerMock.isRegistryOwnedByProfile(_, "rid", _) >> true
		
		1*grTools.getRecommendationCount("rid") >> [1,2]
		
		1*grRecManager.getEmailOptInValue("rid") >> 1
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
			1*requestMock.setParameter("address", "124/sf new delhi")
			2*requestMock.setParameter("registryId", "rid");
			1*requestMock.setParameter("displayView", "guest");
			
			1*requestMock.setParameter("siteId", _);
			1*requestMock.setParameter(BBBGiftRegistryConstants.VIEW, '1');
			1*requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, "registryType")
			1*requestMock.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, true)
			1*requestMock.setParameter("profile", profileMock)
			1*requestMock.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, null)
			
			1*requestMock.setParameter("eventType", "eType")
			0*grManagerMock.fetchUserRegistries(_,"prId")
			//1*requestMock.setParameter(BBBCoreConstants.QTY, 2)
			infoDetailVO.getInStockEPHCategoryBuckets() ==  null
			infoDetailVO.getNotInStockEPHCategoryBuckets() == null

	
	}
	
	/*************************** error message gets from RegistryItemDroplet *******/
	
	def"getRegistryDetailAPI. TC for 'err_no_reg_info' error Message  for RegistryItemDroplet "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >> null >> "err_no_reg_info"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 900
		errorVO.isErrorExists()
	
	}
	
	
	
	def"getRegistryDetailAPI. TC for 'err_invalid_reg_info_req' error Message for RegistryItemDroplet  "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >> null >> "err_invalid_reg_info_req"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 900
		errorVO.isErrorExists()
	
	}
	
	def"getRegistryDetailAPI. TC for 'err_gift_reg_fatal_error' error Message  for RegistryItemDroplet "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >> null >> "err_gift_reg_fatal_error"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 900
		errorVO.isErrorExists()
	
	}
	
	def"getRegistryDetailAPI. TC for 'err_gift_reg_siteflag_usertoken_error' error Message  for RegistryItemDroplet  "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >> null >> "err_gift_reg_siteflag_usertoken_error"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 901
		errorVO.isErrorExists()
	
	}
	
	def"getRegistryDetailAPI. TC for 'err_gift_reg_invalid_input_format' error Message for RegistryItemDroplet   "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >> null >> "err_gift_reg_invalid_input_format"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 902
		errorVO.isErrorExists()
	
	}
	
	def"getRegistryDetailAPI. TC for not matched error Message for RegistryItemDroplet  "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >> null >> "not error"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 0
		!errorVO.isErrorExists()
	
	}
	
	
	/*************************** error message gets from PoBoxValidateDroplet *******/
	
	def"getRegistryDetailAPI. TC for 'err_no_reg_info' error Message  for PoBoxValidateDroplet "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >>  "err_no_reg_info"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 900
		errorVO.isErrorExists()
	
	}
	
	
	
	def"getRegistryDetailAPI. TC for 'err_invalid_reg_info_req' error Message for PoBoxValidateDroplet  "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >>  "err_invalid_reg_info_req"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 900
		errorVO.isErrorExists()
	
	}
	
	def"getRegistryDetailAPI. TC for 'err_gift_reg_fatal_error' error Message  for PoBoxValidateDroplet "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >>  "err_gift_reg_fatal_error"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 900
		errorVO.isErrorExists()
	
	}
	
	def"getRegistryDetailAPI. TC for 'err_gift_reg_siteflag_usertoken_error' error Message  for PoBoxValidateDroplet  "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >>  "err_gift_reg_siteflag_usertoken_error"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 901
		errorVO.isErrorExists()
	
	}
	
	def"getRegistryDetailAPI. TC for 'err_gift_reg_invalid_input_format' error Message for PoBoxValidateDroplet   "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >>  "err_gift_reg_invalid_input_format"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 902
		errorVO.isErrorExists()
	
	}
	
	def"getRegistryDetailAPI. TC for not matched error Message for PoBoxValidateDroplet  "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		requestMock.getObjectParameter("errorMsg") >>  "not error"
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "registryType", null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		
		ServiceErrorVO errorVO =infoDetailVO.getRegistryResVO().getServiceErrorVO()
		errorVO.getErrorId() == 0
		!errorVO.isErrorExists()
	
	}
	
	private getRegistryDetailAPIErrorScenarioCommonCode(Map inputParam, String eventType, String frControler){
		List itemList = [rItemVO]
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", null)
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", null)
		inputParam.put("sortSeq", "1")
		inputParam.put("view", null)
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", "")
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", frControler)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> null
		
		profileMock.getRepositoryId() >> "prId"
		//sessionBeanMock.getValues() >> map
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		
		rSummeryVO.setShippingAddress(addressVO)
		addressVO.setAddressLine1("124/sf new")
		addressVO.setAddressLine2("delhi")

		1*poBoxVDropletMock.service(requestMock, responseMock)
		
		requestMock.getParameter("isValid") >> true
		rSummeryVO.setEventDate("")
		
		requestMock.getObjectParameter("registryVO") >> registryVO
		
		requestMock.getParameter("eventDate") >> ""
		
		rSummeryVO.setRegistryType(rType)
		rType.getRegistryTypeName() >>eventType //"registryType"
		
		cToolsMock.getValueForConfigKey("FlagDrivenFunctions","My_Items_Checklist_Flag",false) >> {throw new BBBBusinessException("exception")}
		rSummeryVO.setEventType("eType")
		requestMock.getHeader("X-bbb-channel") >> "notMobileWeb"
		//1*cheManager.showCheckListButton("registryType") >> false
		
		
		ritemDisplayDroplet.service(requestMock, responseMock)
		

		requestMock.getObjectParameter("promoBox") >> pBoxVO
		requestMock.getParameter("count") >> null
		requestMock.getParameter("emptyOutOfStockListFlag") >> null
		
		requestMock.getObjectParameter("registryItemsAll") >> [rItemVO1]
	
		profileMock.isTransient() >> false
		
		1*grManagerMock.isRegistryOwnedByProfile(_, "rid", _) >> true
		
		1*grTools.getRecommendationCount("rid") >> [1,2]
		
		1*grRecManager.getEmailOptInValue("rid") >> 1
		
	}
	
	def"getRegistryDetailAPI.TC for IOException"(){
		given:
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])
		Map inputParam = new HashMap()
		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", null)
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", null)
		inputParam.put("sortSeq", "1")
		inputParam.put("view", null)
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", "")
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", null)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> null
		
		profileMock.getRepositoryId() >> "prId"
		//sessionBeanMock.getValues() >> map
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock) >> {throw new IOException("exception")}
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		infoDetailVO.getErrorCode() =='8106'
		infoDetailVO.getErrorMessage() == 'error_registry_detail'
	}
	
	def"getRegistryDetailAPI.TC for ServletException"(){
		given:
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])
		Map inputParam = new HashMap()
		inputParam.put("registryId", "rid")
		inputParam.put("loadSelectedCategory", null)
		inputParam.put("c1id", null)
		inputParam.put("c2id", null)
		inputParam.put("c3id", null)
		inputParam.put("c1name", null)
		inputParam.put("c2name", null)
		inputParam.put("c3name", null)
		
		inputParam.put("displayView", null)
		inputParam.put("sortSeq", "1")
		inputParam.put("view", null)
		inputParam.put("startIdx", null)
		inputParam.put("bulkSize" , null)
		inputParam.put("isGiftGiver", "")
		inputParam.put("isAvailForWebPurchaseFlag", null)
		inputParam.put("fromRegistryController", null)
		inputParam.put("fromchecklist", null)
		
		rAPIManager.getSiteId() >> "BedBathCanada"
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> null
		
		profileMock.getRepositoryId() >> "prId"
		//sessionBeanMock.getValues() >> map
		item1.getRepositoryId() >> "item1"
		
		1*riDisplayDroplet.service(requestMock, responseMock) >> {throw new ServletException("exception")}
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		infoDetailVO.getErrorCode() =='8105'
		infoDetailVO.getErrorMessage() == 'error_registry_detail'
	}
	
	def"getRegistryDetailAPI. TC when event type is null   "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, null, null)

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		1*requestMock.setParameter("address", "124/sf new delhi")
		1*requestMock.setParameter("registryId", "rid");
		1*requestMock.setParameter("displayView", "guest");

	}
	
	def"getRegistryDetailAPI. TC when fromRegistryController is true   "(){
		given:
		MyItemCategoryVO myIteCatVO = new MyItemCategoryVO()
		//rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setRegistryInfoDroplet(riDisplayDroplet)
		rAPIManager.setPoBoxValidateDroplet(poBoxVDropletMock)
		rAPIManager.setDateCalculationDroplet(dcDropletMock)
		rAPIManager.setProfile(profileMock)
		rAPIManager.setCatalogTools(cToolsMock)
		rAPIManager.setCheckListManager(cheManager)
		rAPIManager.setRegistryItemDroplet(ritemDisplayDroplet)
		rAPIManager.setGiftRegistryTools(grTools)
		rAPIManager.setGiftRegistryRecommendationManager(grRecManager)
		//requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proForHandler
		
		Map inputParam = new HashMap()
		
		
		getRegistryDetailAPIErrorScenarioCommonCode(inputParam, "eventCode", "true")

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		then:
		1*requestMock.setParameter("address", "124/sf new delhi")
		1*requestMock.setParameter("registryId", "rid");
		1*requestMock.setParameter("displayView", "guest");

	}
	
	def"getRegistryDetailAPI. TC for RepositoryException"(){
		given:
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("loadSelectedCategory", "true")
		
		inputParam.put("displayView", "owner")
	
		rAPIManager.getSiteId() >> "BedBathCanada"
		
		profileMock.getRepositoryId() >> "prId"
		sessionBeanMock.getValues() >> map
		1*grManagerMock.fetchUserRegistries(_,"prId") >> {throw new RepositoryException("exception")}//[item1]
		item1.getRepositoryId() >> "item1"
		
		riDisplayDroplet.service(requestMock, responseMock)
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		
		then:
		infoDetailVO.getErrorCode() =='2003'
		infoDetailVO.getErrorMessage() == 'err_fetching_registry_Info'

	}
	
	def"getRegistryDetailAPI. TC for BBBSystemException"(){
		given:
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("loadSelectedCategory", "true")
		
		inputParam.put("displayView", "owner")
	
		rAPIManager.getSiteId() >> "BedBathCanada"
		
		profileMock.getRepositoryId() >> "prId"
		sessionBeanMock.getValues() >> map
		1*grManagerMock.fetchUserRegistries(_,"prId") >> {throw new BBBSystemException("exception")}//[item1]
		item1.getRepositoryId() >> "item1"
		
		riDisplayDroplet.service(requestMock, responseMock)
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		
		then:
		infoDetailVO.getErrorCode() =='8105'
		infoDetailVO.getErrorMessage() == 'err_fetching_registry_Info'

	}
	
	def"getRegistryDetailAPI. TC for BBBBusinessException"(){
		given:
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("loadSelectedCategory", "true")
		
		inputParam.put("displayView", "owner")
	
		rAPIManager.getSiteId() >> "BedBathCanada"
		
		profileMock.getRepositoryId() >> "prId"
		sessionBeanMock.getValues() >> map
		1*grManagerMock.fetchUserRegistries(_,"prId") >> {throw new BBBBusinessException("exception")}//[item1]
		item1.getRepositoryId() >> "item1"
		
		riDisplayDroplet.service(requestMock, responseMock)
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		
		then:
		infoDetailVO.getErrorCode() =='8105'
		infoDetailVO.getErrorMessage() == 'err_fetching_registry_Info'

	}
	
	def"getRegistryDetailAPI. TC when registry summery VO is null"(){
		given:
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		List itemList = [rItemVO]
		Map inputParam = new HashMap()
		
		Map map = new HashMap()
		map.put("notuserRegistriesList", [])

		inputParam.put("loadSelectedCategory", "true")
		
		inputParam.put("displayView", "owner")
	
		rAPIManager.getSiteId() >> "BedBathCanada"
		
		profileMock.getRepositoryId() >> "prId"
		sessionBeanMock.getValues() >> map
		1*grManagerMock.fetchUserRegistries(_,"prId") >> [item1]
		item1.getRepositoryId() >> "item1"
		
		riDisplayDroplet.service(requestMock, responseMock)
		requestMock.getObjectParameter("registrySummaryVO") >> null
		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(inputParam)
		
		then:
		infoDetailVO.getErrorCode() =='8105'
		infoDetailVO.getErrorMessage() == 'err_fetching_registry_Info'

	}
	
	def"getRegistryDetailAPI. TC when input map is null"(){
		given:

		when:
			RestRegistryInfoDetailVO infoDetailVO =rAPIManager.getRegistryDetailAPI(null)
		
		then:
		infoDetailVO.getErrorCode() =='8107'
		infoDetailVO.getErrorMessage() == 'err_invalid_input'

	}
	
	/********************************************************* showRecommendationForRegistry *****************************************/
	def"showRecommendationForRegistry.TC to check recommendation tab for a registry"(){
		given:
			
			requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
			requestMock.getObjectParameter("registryVO") >> registryVO
			rSummeryVO.setEventType( "eventType") 
			requestMock.getParameter("eventDate") >> ""
			
			requestMock.getParameter("check") >> "true"
			registryVO.setIsPublic("1")
			1*grTools.getRecommendationCount("rId") >> [1,2]
			1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","Invite_Friends_Key",false) >> true
			
			registryVO.setPrefStoreNum("2")
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
		    value == "false:eventType:2"
			1*requestMock.setParameter("registryId", "rId")
			!rSummeryVO.isEventYetToCome()
	}
	
	def"showRecommendationForRegistry.TC to check recommendation tab for a registry when is public is 1"(){
		given:
		//commonCodeForshowRecommendationForRegistry( "0", "true", "1")
		
			requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
			requestMock.getObjectParameter("registryVO") >> registryVO
			rSummeryVO.setEventType( "eventType")
			requestMock.getParameter("eventDate") >> "0"
			
			requestMock.getParameter("check") >> "true"
			registryVO.setIsPublic("1")
			1*grTools.getRecommendationCount("rId") >> [1,2]
			1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","Invite_Friends_Key",false) >> false
			
			registryVO.setPrefStoreNum("2")
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
			value == "true:eventType:2"
			1*requestMock.setParameter("registryId", "rId")
	}
	
	def"showRecommendationForRegistry.TC to check recommendation tab for a registry when is public is 1 and event type to come is true"(){
		given:
		commonCodeForshowRecommendationForRegistry( "12/12/2014", "true", "1")
		
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			1*dcDropletMock.service(requestMock, responseMock)
			value == "true:eventType:2"
			1*requestMock.setParameter("registryId", "rId")
			rSummeryVO.isEventYetToCome()
	}
	
	def"showRecommendationForRegistry.TC to check recommendation tab for a registry when is public is 1 and event type to come is false"(){
		given:
		commonCodeForshowRecommendationForRegistry( "12/12/2014", "false", "1")
		

		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			1*dcDropletMock.service(requestMock, responseMock)
			value == "false:eventType:2"
			1*requestMock.setParameter("registryId", "rId")
			
	}
	
	def"showRecommendationForRegistry.TC to check recommendation tab for a registry when is public is 0 "(){
		given:
		    commonCodeForshowRecommendationForRegistry( "12/12/2014", "false", "0")
		
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			1*dcDropletMock.service(requestMock, responseMock)
			value == "true:eventType:2"
			1*requestMock.setParameter("registryId", "rId")
			
	}
	
	def"showRecommendationForRegistry.TC to check recommendation tab for a registry when is public is 3 "(){
		given:
			commonCodeForshowRecommendationForRegistry( "12/12/2014", "false", "3")
		
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			1*dcDropletMock.service(requestMock, responseMock)
			value == "false:eventType:2"
			1*requestMock.setParameter("registryId", "rId")
			
	}
	
	def "showRecommendationForRegistry.TC when registryVO is null"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
			requestMock.getObjectParameter("registryVO") >> null
	
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
			value.equals("false")
	}
	
	def "showRecommendationForRegistry.TC when rSummeryVO is null"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> null
			requestMock.getObjectParameter("registryVO") >> null
	
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
			value.equals("false")
	}
	
	def "showRecommendationForRegistry.TC for IOException"(){
		given:
			riDisplayDroplet.service(requestMock, responseMock) >> {throw new IOException("exception")}
	
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			0*dcDropletMock.service(requestMock, responseMock)
			value.equals("false")
			0*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","Invite_Friends_Key",false)
	}
	
	def "showRecommendationForRegistry.TC for ServletException"(){
		given:
			riDisplayDroplet.service(requestMock, responseMock) >> {throw new ServletException("exception")}
	
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			0*dcDropletMock.service(requestMock, responseMock)
			value.equals("false")
			0*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","Invite_Friends_Key",false)
	}
	
	def "showRecommendationForRegistry.TC for BBBSystemException"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
			requestMock.getObjectParameter("registryVO") >> registryVO

			1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","Invite_Friends_Key",false) >> {throw new BBBSystemException("exception") }
	
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
			value.equals("false")
	}
	
	def "showRecommendationForRegistry.TC for BBBBusinessException"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
			requestMock.getObjectParameter("registryVO") >> registryVO

			1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","Invite_Friends_Key",false) >> {throw new BBBBusinessException("exception") }
	
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			1*riDisplayDroplet.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
			value.equals("false")
	}
	
	def "showRecommendationForRegistry.TC when request is null"(){
		given:
		    ServletUtil.setCurrentRequest(null)
	
		when:
			String value = rAPIManager.showRecommendationForRegistry("rId")
		then:
			0*riDisplayDroplet.service(requestMock, responseMock)
			0*dcDropletMock.service(requestMock, responseMock)
			value.equals("false")
	}
	
	private commonCodeForshowRecommendationForRegistry(String eDate, String evToCome, String isPublic){
		requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
		requestMock.getObjectParameter("registryVO") >> registryVO
		rSummeryVO.setEventType( "eventType")
		requestMock.getParameter("eventDate") >> eDate
		
		requestMock.getParameter("check") >> evToCome
		registryVO.setIsPublic(isPublic)
		1*grTools.getRecommendationCount("rId") >> [1,0]
		1*cToolsMock.getValueForConfigKey("FlagDrivenFunctions","Invite_Friends_Key",false) >> false
		
		registryVO.setPrefStoreNum("2")

	}
	
	/******************************************** getQty ********************************/
	
	def"getQty.TC to get the quantity of leaf node visited from checklist" (){
		given:
		    RegistryItemsListVO rItemListVo = new RegistryItemsListVO()
			RepositoryItem skuItem =  Mock()
			List itemVos = [rItemVO, rItemVO1,rItemVO2]
			1*grTools.fetchRegistryItemsFromEcomAdmin("rId", false, "1") >> rItemListVo
			rItemListVo.setRegistryItemList(itemVos)
			1*muRepository.getItem("c3Id","checkListCategory") >> muRepItem
			2*muRepItem.getPropertyValue("suggestedQuantity") >> "2"
			
			rItemVO.setSku(11)
			rItemVO1.setSku(12)
			//rItemVO2.setSku()
			
			1*cheLisTools.getSkuComputedAttributeItem("11") >> skuItem
			1*cheLisTools.getSkuComputedAttributeItem("12") >> skuItem
			
			2*cheLisTools.getCategoryListForSku(skuItem) >> ["c3Id"] >> ["cccccId"]
			
			1*cheLisTools.getPackageCountForSku(skuItem) >> 3
			rItemVO.setQtyRequested(5)
			
			
		when:
			String value = rAPIManager.getQty("rId", "c3Id","c2id")
		then:
		   value.equals("15 of 2")
	}
	
	def"getQty.TC to for BBBBusinessException while getting SkuComputedAttribute" (){
		given:
			RegistryItemsListVO rItemListVo = new RegistryItemsListVO()
			RepositoryItem skuItem =  Mock()
			List itemVos = [rItemVO,]
			1*grTools.fetchRegistryItemsFromEcomAdmin("rId", false, "1") >> rItemListVo
			rItemListVo.setRegistryItemList(itemVos)
			1*muRepository.getItem("c2id","checkListCategory") >> muRepItem
			muRepItem.getPropertyValue("suggestedQuantity") >> null
			
			rItemVO.setSku(11)
			
			cheLisTools.getSkuComputedAttributeItem("11") >> {throw new BBBBusinessException("exception")}
			
			
		when:
			String value = rAPIManager.getQty("rId", "","c2id")
		then:
		   value == null
		   0*cheLisTools.getCategoryListForSku(_)
	}
	
	def"getQty.TC to for RepositoryException while getting checkList Repository" (){
		given:
			RegistryItemsListVO rItemListVo = new RegistryItemsListVO()
			RepositoryItem skuItem =  Mock()
			List itemVos = [rItemVO]
			1*grTools.fetchRegistryItemsFromEcomAdmin("rId", false, "1") >> rItemListVo
			rItemListVo.setRegistryItemList(itemVos)
			1*muRepository.getItem("c2id","checkListCategory") >> {throw new RepositoryException("exception")}
			muRepItem.getPropertyValue("suggestedQuantity") >> null
			
			rItemVO.setSku(11)
			
			cheLisTools.getSkuComputedAttributeItem("11") >> {throw new BBBBusinessException("exception")}
			
				
			
		when:
			String value = rAPIManager.getQty("rId", "","c2id")
		then:
		   value == null
		   0*cheLisTools.getCategoryListForSku(_)
		   0*muRepItem.getPropertyValue("suggestedQuantity")
	}
	
	def"getQty.TC when RegistryItems is empty" (){
		given:
			RegistryItemsListVO rItemListVo = new RegistryItemsListVO()
			RepositoryItem skuItem =  Mock()
			List itemVos = []
			1*grTools.fetchRegistryItemsFromEcomAdmin("rId", false, "1") >> rItemListVo
			rItemListVo.setRegistryItemList(itemVos)
				
			
		when:
			String value = rAPIManager.getQty("rId", "","c2id")
		then:
		   value == "0 of 0"
		   0*cheLisTools.getCategoryListForSku(_)
		   0*muRepItem.getPropertyValue("suggestedQuantity")
		   0*cheLisTools.getSkuComputedAttributeItem("11")
	}
	
	def"getQty.TC when RegistryItemsFromEcomAdmin is null" (){
		given:
			grTools.fetchRegistryItemsFromEcomAdmin("rId", false, "1") >> null
				
			
		when:
			String value = rAPIManager.getQty("rId", "","c2id")
		then:
		   value == "0 of 0"
		   0*cheLisTools.getCategoryListForSku(_)
		   0*muRepItem.getPropertyValue("suggestedQuantity")
		   0*cheLisTools.getSkuComputedAttributeItem("11")
	}
	
	/*******************************convertCategoryBucketOutputForPrice *******************************/
	
	def"convertCategoryBucketOutputForPrice"(){
		given:
			List itemVos = []
			Map map = ["item" : itemVos]
			List rang = ["item",null]
			
		when:
		List<RegistryCategoryBucketVO> value = rAPIManager.convertCategoryBucketOutputForPrice(map, rang)
		
		then:
		RegistryCategoryBucketVO vo = value.get(0)
		vo.getCatgoryName().equals("item")
		vo.getItems() == []
		
	}
	
	/***************************************** getRegistryDashboard *******************************/
	
	def"getRegistryDashboard. TC to get registry summery VO"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> rSummeryVO
			requestMock.getObjectParameter("errorMsg") >> null
		when:
			RegistryResVO rrVO = rAPIManager.getRegistryDashboard()
		then:
		rrVO.getRegistrySummaryVO() == rSummeryVO
		1*requestMock.setParameter("profile", profileMock)
		1*grFlyoutDroplet.service(requestMock, responseMock)
		
	}
	
	def"getRegistryDashboard. TC for IOException"(){
		given:
		    1*grFlyoutDroplet.service(requestMock, responseMock) >> {throw new IOException("exception")}
		when:
			RegistryResVO rrVO = rAPIManager.getRegistryDashboard()
		then:
		BBBSystemException exc = thrown()
		1*requestMock.setParameter("profile", profileMock)
		
		
	}
	
	def"getRegistryDashboard. TC for ServletException"(){
		given:
			1*grFlyoutDroplet.service(requestMock, responseMock) >> {throw new ServletException("exception")}
		when:
			RegistryResVO rrVO = rAPIManager.getRegistryDashboard()
		then:
		BBBSystemException exc = thrown()
		1*requestMock.setParameter("profile", profileMock)
		
		
	}
	
	def"getRegistryDashboard. TC when error message is 'err_no_reg_info'"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> null
			requestMock.getObjectParameter("errorMsg") >> "err_no_reg_info"
		when:
			RegistryResVO rrVO = rAPIManager.getRegistryDashboard()
		then:
		rrVO.getRegistrySummaryVO() == null
		ServiceErrorVO errorVo = rrVO.getServiceErrorVO()
		errorVo.getErrorId() == 900
		errorVo.getErrorMessage().equals("err_no_reg_info")
		errorVo.isErrorExists()
		
		1*requestMock.setParameter("profile", profileMock)
		1*grFlyoutDroplet.service(requestMock, responseMock)
		
	}
	
	def"getRegistryDashboard. TC when error message is 'err_invalid_reg_info_req'"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> null
			requestMock.getObjectParameter("errorMsg") >> "err_invalid_reg_info_req"
		when:
			RegistryResVO rrVO = rAPIManager.getRegistryDashboard()
		then:
		rrVO.getRegistrySummaryVO() == null
		ServiceErrorVO errorVo = rrVO.getServiceErrorVO()
		errorVo.getErrorId() == 900
		errorVo.getErrorMessage().equals("err_invalid_reg_info_req")
		errorVo.isErrorExists()
		
		1*requestMock.setParameter("profile", profileMock)
		1*grFlyoutDroplet.service(requestMock, responseMock)
		
	}
	
	def"getRegistryDashboard. TC when error message is 'err_gift_reg_fatal_error'"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> null
			requestMock.getObjectParameter("errorMsg") >> "err_gift_reg_fatal_error"
		when:
			RegistryResVO rrVO = rAPIManager.getRegistryDashboard()
		then:
		rrVO.getRegistrySummaryVO() == null
		ServiceErrorVO errorVo = rrVO.getServiceErrorVO()
		errorVo.getErrorId() == 900
		errorVo.getErrorMessage().equals("err_gift_reg_fatal_error")
		errorVo.isErrorExists()
		
		1*requestMock.setParameter("profile", profileMock)
		1*grFlyoutDroplet.service(requestMock, responseMock)
		
	}
	
	def"getRegistryDashboard. TC when error message is 'err_gift_reg_siteflag_usertoken_error'"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> null
			requestMock.getObjectParameter("errorMsg") >> "err_gift_reg_siteflag_usertoken_error"
		when:
			RegistryResVO rrVO = rAPIManager.getRegistryDashboard()
		then:
		rrVO.getRegistrySummaryVO() == null
		ServiceErrorVO errorVo = rrVO.getServiceErrorVO()
		errorVo.getErrorId() == 901
		errorVo.getErrorMessage().equals("err_gift_reg_siteflag_usertoken_error")
		errorVo.isErrorExists()
		
		
	}
	
	def"getRegistryDashboard. TC when error message is 'unKnown Error'"(){
		given:
			requestMock.getObjectParameter("registrySummaryVO") >> null
			requestMock.getObjectParameter("errorMsg") >> "unKnown Error"
		when:
			RegistryResVO rrVO = rAPIManager.getRegistryDashboard()
		then:
		rrVO.getRegistrySummaryVO() == null
		ServiceErrorVO errorVo = rrVO.getServiceErrorVO()
		errorVo.getErrorId() == 0
		!errorVo.isErrorExists()
		
		
	}
	
	/****************************************** convertToJsonMessage ****************************************/
	
	def"convertToJsonMessage.TC to convert to json message"(){
		given:
			List itemVos = [rItemVO,rItemVO1]
			Map map = ["items" : itemVos, "item1s" : null ]

		when:
			Map value = rAPIManager.convertToJsonMessage(map)
		then:
		Map innerListMap = value.get("items")
		value.get("item1s") == null
		innerListMap.get("1") == rItemVO
		innerListMap.get("2") == rItemVO1
		
	}
	
	def"convertToJsonMessage.TC when categoryBuckets map is empty"(){
		given:
			Map map = [:]

		when:
			Map value = rAPIManager.convertToJsonMessage(map)
		then:
		value.isEmpty()
	}
	
	/*****************************************getActiveRegistryForRest********************************************/
	def"getActiveRegistryForRest.TC to get the active registry "(){
		given:
		
		SiteContextManager sContextManager = Mock() 
		rAPIManager = Spy()
		rAPIManager.setGiftRegistryManager(grManagerMock)
		rAPIManager.setSiteContext(sContext)
		sContext.getSiteContextManager() >> sContextManager
		sContextManager.getCurrentSiteId() >> "usBed"
		
		GiftRegSessionBean grSessionBean = Mock()
		requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grSessionBean
		1*profileMock.isTransient() >> false
		
		
		Map map = new HashMap()
		List urseRegList = ["use1"]
		List urseActRegList = []
		map.put("userRegistrysummaryVO", null)
		map.put("userRegistriesList", urseRegList)
		map.put("userActiveRegistriesList", urseActRegList)
		
		
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		sessionBeanMock.getValues() >> map
		
		1*grManagerMock.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "acStatus"
		profileMock.getRepositoryId() >> "pid"
		1*grManagerMock.fetchUserRegistries(_, "pid") >> [item1,item2]
		item1.getRepositoryId() >> "item1"
		item2.getRepositoryId() >> "item2"
		1*grManagerMock.getRegistryStatusFromRepo(_,	"item1") >> "acStatus"
		1*grManagerMock.getRegistryStatusFromRepo(_,	"item2") >> "aaacStatus"
		
		1*grManagerMock.getRegistryInfo( "item1", _) >> rSummeryVO
		
		grSessionBean.getRegistryOperation() >> "created" >> "updated" >> "updated" >> "removed" 
		
		rSummeryVO.setEventDate("12/20/2012")
		1*rAPIManager.getDateDiff(_, rSummeryVO) >> -100
		rSummeryVO.setShippingAddress(null)

		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == rSummeryVO
	}
	
	//working on this
	def"getActiveRegistryForRest.TC when userActiveRegList size is more then one "(){
		given:
		
		SiteContextManager sContextManager = Mock()
		sContext.getSiteContextManager() >> sContextManager
		sContextManager.getCurrentSiteId() >> "usBed"
		
		GiftRegSessionBean grSessionBean = Mock()
		requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grSessionBean
		1*profileMock.isTransient() >> false
		
		
		Map map = new HashMap()
		List urseRegList = ["use1"]
		List urseActRegList = []
		map.put("userRegistrysummaryVO", null)
		map.put("userRegistriesList", urseRegList)
		map.put("userActiveRegistriesList", urseActRegList)
		
		
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		sessionBeanMock.getValues() >> map
		
		1*grManagerMock.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "acStatus"
		profileMock.getRepositoryId() >> "pid"
		1*grManagerMock.fetchUserRegistries(_, "pid") >> [item1,item2]
		item1.getRepositoryId() >> "item1"
		item2.getRepositoryId() >> "item2"
		1*grManagerMock.getRegistryStatusFromRepo(_,	"item1") >> "acStatus"
		1*grManagerMock.getRegistryStatusFromRepo(_,	"item2") >> "acStatus"
		
		grManagerMock.fetchUsersSoonestOrRecent(_) >> "item1"
		1*grManagerMock.getRegistryInfo( "item1", _) >> rSummeryVO
		
		grSessionBean.getRegistryOperation() >> "created" >> "updated1" 
		
		rSummeryVO.setEventDate(null)
		//1*rAPIManager.getDateDiff(_, rSummeryVO) >> -100 
		rSummeryVO.setShippingAddress(addressVo)
		addressVo.setAddressLine1("address1")
		addressVo.setAddressLine2("address1")
		
		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == rSummeryVO
			!rSummeryVO.isActiveRegistryHasPoBoxAddress()
	}
	
	def"getActiveRegistryForRest.TC when rSummeryVO is null "(){
		given:
		
		SiteContextManager sContextManager = Mock()
		sContext.getSiteContextManager() >> sContextManager
		sContextManager.getCurrentSiteId() >> "usBed"
		
		GiftRegSessionBean grSessionBean = Mock()
		requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grSessionBean
		1*profileMock.isTransient() >> false
		
		
		Map map = new HashMap()
		List urseRegList = ["use1"]
		List urseActRegList = ["item1", "item2"]
		map.put("userRegistrysummaryVO", null)
		map.put("userRegistriesList", urseRegList)
		map.put("userActiveRegistriesList", urseActRegList)
		
		
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		sessionBeanMock.getValues() >> map
		
		1*grManagerMock.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "acStatus"
		profileMock.getRepositoryId() >> "pid"
	
		grManagerMock.fetchUsersSoonestOrRecent(_) >> null
		1*grManagerMock.getRegistryInfo( "item1", _) >> null
		
		grSessionBean.getRegistryOperation() >> "created" >> "updated" >> "updated1" 
		
		rSummeryVO.setEventDate(null)
		
		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == null
			!rSummeryVO.isActiveRegistryHasPoBoxAddress()
	}
	
	def"getActiveRegistryForRest.TC when registry operation is not removed "(){
		given:
		Map map = new HashMap()
        commonCodeForActiveRegistryForRestOperation(map)		
		grSessionBean.getRegistryOperation() >>  "created" >> "updated" >> "updated" >> "removed1" 
		
		rSummeryVO.setEventDate(null)
		
		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == rSummeryVO
			!rSummeryVO.isActiveRegistryHasPoBoxAddress()
	}
	
	def"getActiveRegistryForRest.TC when registry operation is owner "(){
		given:
		Map map = new HashMap()
		commonCodeForActiveRegistryForRestOperation(map)
		grSessionBean.getRegistryOperation() >>  "created" >> "updated" >> "updated" >> "removed" >> "owner"
		
		rSummeryVO.setEventDate(null)
		
		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == rSummeryVO
			!rSummeryVO.isActiveRegistryHasPoBoxAddress()
	}
	
	def"getActiveRegistryForRest.TC when registry operation is not created "(){
		given:
		Map map = new HashMap()
		commonCodeForActiveRegistryForRestOperation(map)
		grSessionBean.getRegistryOperation() >>  "created1"
		
		rSummeryVO.setEventDate(null)
		
		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == rSummeryVO
			!rSummeryVO.isActiveRegistryHasPoBoxAddress()
	}

	
	private commonCodeForActiveRegistryForRestOperation(Map map){
		SiteContextManager sContextManager = Mock()
		sContext.getSiteContextManager() >> sContextManager
		sContextManager.getCurrentSiteId() >> "usBed"
		
		
		requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grSessionBean
		1*profileMock.isTransient() >> false
		
		
		List urseRegList = ["use1"]
		List urseActRegList = ["item1", "item2"]
		map.put("userRegistrysummaryVO", null)
		map.put("userRegistriesList", urseRegList)
		map.put("userActiveRegistriesList", urseActRegList)
		
		
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		sessionBeanMock.getValues() >> map
		
		1*grManagerMock.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> ""
		profileMock.getRepositoryId() >> "pid"
	
		grManagerMock.fetchUsersSoonestOrRecent(_) >> null
		1*grManagerMock.getRegistryInfo( "item1", _) >> rSummeryVO

	}
	
	def"getActiveRegistryForRest.TC to get the active registry when user registry item is null "(){
		given:
		
		SiteContextManager sContextManager = Mock()
		sContext.getSiteContextManager() >> sContextManager
		sContextManager.getCurrentSiteId() >> "usBed"
		
		GiftRegSessionBean grSessionBean = Mock()
		requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grSessionBean
		1*profileMock.isTransient() >> false
		
		
		Map map = new HashMap()
		List urseRegList = ["use1"]
		List urseActRegList = []
		map.put("userActiveRegistriesList", urseActRegList)
		
		
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		sessionBeanMock.getValues() >> map
		
		1*grManagerMock.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "acStatus"
		profileMock.getRepositoryId() >> "pid"
		1*grManagerMock.fetchUserRegistries(_, "pid") >> null
		rSummeryVO.setShippingAddress(null)

		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == null
	}
	
	def"getActiveRegistryForRest.TC for RepositoryException "(){
		given:
		
		Map map = new HashMap()
		getActiveRegistryForRestCommonCodeForException(map)
		1*grManagerMock.fetchUserRegistries(_, "pid") >> {throw new RepositoryException("exception")}
		rSummeryVO.setShippingAddress(null)

		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == null
	}
	
	def"getActiveRegistryForRest.TC for BBBSystemException "(){
		given:
		
		Map map = new HashMap()
		getActiveRegistryForRestCommonCodeForException(map)
		1*grManagerMock.fetchUserRegistries(_, "pid") >> {throw new BBBSystemException("exception")}
		rSummeryVO.setShippingAddress(null)

		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == null
	}
	
	def"getActiveRegistryForRest.TC for Exception "(){
		given:
		
		Map map = new HashMap()
		getActiveRegistryForRestCommonCodeForException(map)
		1*grManagerMock.fetchUserRegistries(_, "pid") >> {throw new Exception("exception")}
		rSummeryVO.setShippingAddress(null)

		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == null
	}
	
	def"getActiveRegistryForRest.TC for BBBBusinessException "(){
		given:
		
		Map map = new HashMap()
		getActiveRegistryForRestCommonCodeForException(map)
		1*grManagerMock.fetchUserRegistries(_, "pid") >> {throw new BBBBusinessException("exception")}
		rSummeryVO.setShippingAddress(null)

		when:
			rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == null
	}
	
	private getActiveRegistryForRestCommonCodeForException(Map map){
		
		SiteContextManager sContextManager = Mock()
		sContext.getSiteContextManager() >> sContextManager
		sContextManager.getCurrentSiteId() >> "usBed"
		
		GiftRegSessionBean grSessionBean = Mock()
		requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grSessionBean
		profileMock.isTransient() >> false
		
		
		
		List urseRegList = ["use1"]
		List urseActRegList = []
		map.put("userActiveRegistriesList", urseActRegList)
		
		
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		sessionBeanMock.getValues() >> map
		
		1*grManagerMock.getGiftRegistryConfigurationByKey("AcceptableStatuses") >> "acStatus"
		profileMock.getRepositoryId() >> "pid"

	}
	
	def"getActiveRegistryForRest.summery VO is not null gets from session bean "(){
		given:
		
		SiteContextManager sContextManager = Mock()
		sContext.getSiteContextManager() >> sContextManager
		sContextManager.getCurrentSiteId() >> "usBed"		
		1*profileMock.isTransient() >> false
		
		
		Map map = new HashMap()
		List urseRegList = ["use1"]
		List urseActRegList = []
		map.put("userActiveRegistriesList", urseActRegList)
		map.put("userRegistrysummaryVO", rSummeryVO)
		
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		sessionBeanMock.getValues() >> map
		
		when:
			RegistrySummaryVO value = rAPIManager.getActiveRegistryForRest()
		then:
			map.get("userRegistrysummaryVO") == rSummeryVO
			0*grManagerMock.getGiftRegistryConfigurationByKey("AcceptableStatuses")
			value == rSummeryVO
	}
	
	def"getActiveRegistryForRest.when profile is transient "(){
		given:
		
		SiteContextManager sContextManager = Mock()
		sContext.getSiteContextManager() >> sContextManager
		sContextManager.getCurrentSiteId() >> "usBed"
		1*profileMock.isTransient() >> true
				
		when:
			RegistrySummaryVO value = rAPIManager.getActiveRegistryForRest()
		then:
			value == null
			0*grManagerMock.getGiftRegistryConfigurationByKey("AcceptableStatuses")
	}
	
	/*************************************** isBTSProduct ************************************************/
	
	def"isBTSProduct. TC to check bts product"(){
	given:
	requestMock.getObjectParameter("bts") >> true
		 
	when:
		boolean value = rAPIManager.isBTSProduct("prodId", true)
	then:
	1*requestMock.setParameter("productId", "prodId")
	1*requestMock.setParameter("forOmniture", true)
	1*requestMock.setParameter("siteId",_)
	1*bCrumbDroplet.service(requestMock, responseMock)

	}
	
	def"isBTSProduct. TC for ServletException"(){
		given:
		1*bCrumbDroplet.service(requestMock, responseMock) >> {throw new ServletException("exception")}
		
			 
		when:
			boolean value = rAPIManager.isBTSProduct("prodId", true)
		then:
		BBBSystemException exce = thrown()
		1*requestMock.setParameter("productId", "prodId")
		
	
		}
	
	def"isBTSProduct. TC for IOException"(){
		given:
		1*bCrumbDroplet.service(requestMock, responseMock) >> {throw new IOException("exception")}
		
			 
		when:
			boolean value = rAPIManager.isBTSProduct("prodId", true)
		then:
		BBBSystemException exce = thrown()
		1*requestMock.setParameter("productId", "prodId")
		
	
		}
	
	def"isBTSProduct. TC when productId is null"(){
		given:
			 
		when:
			boolean value = rAPIManager.isBTSProduct(null, true)
		then:
		BBBBusinessException exce = thrown()
		0*requestMock.setParameter("productId", null)
		0*bCrumbDroplet.service(requestMock, responseMock)
		
	
		}
	
	/******************************************* getRegistryDetailForApp ********************************/
	
	def"getRegistryDetailForApp.Tc get Registry detail"(){
		given:
			RegistryItemsListVO rItemListVo = new RegistryItemsListVO()
			requestMock.getParameter("view") >> "displayView"
			1*grManagerMock.fetchRegistryItemsFromEcomAdmin("rId", "displayView") >> rItemListVo
			
			
		when:
			AppRegistryInfoDetailVO value = rAPIManager.getRegistryDetailForApp("rId")
		then:
		value.getItems() == rItemListVo
	}
	
	def"getRegistryDetailForApp.Tc for BBBSystemException"(){
		given:
			RegistryItemsListVO rItemListVo = new RegistryItemsListVO()
			requestMock.getParameter("view") >> ""
			1*grManagerMock.fetchRegistryItemsFromEcomAdmin("rId", "1") >> {throw new BBBSystemException("exception")}
			
			
		when:
			AppRegistryInfoDetailVO value = rAPIManager.getRegistryDetailForApp("rId")
		then:
		value.getItems() == null
	}
	
	def"getRegistryDetailForApp.Tc for BBBBusinessException"(){
		given:
			RegistryItemsListVO rItemListVo = new RegistryItemsListVO()
			requestMock.getParameter("view") >> ""
			1*grManagerMock.fetchRegistryItemsFromEcomAdmin("rId", "1") >> {throw new BBBBusinessException("exception")}
			
			
		when:
			AppRegistryInfoDetailVO value = rAPIManager.getRegistryDetailForApp("rId")
		then:
		value.getItems() == null
	}
	
	
	/************************************** createRegistry2 ******************************************************/
	
	def"createRegistry2. TC to create registry"(){
		given:
		
		rAPIManager = Spy()
		rAPIManager.setSiteRepository(sRepository)	
		rAPIManager.setChannelRepository(chnlRepository)
		rAPIManager.setProfileServices(profService)
		rAPIManager.setLblTxtTemplateManager(lblTexTManager)
		GiftRegistryFormHandler grFormHandler = Mock()
		//validateSite and channel
		Set sites =[singleSite]
		Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
		RepositoryView rViewMock = Mock()
		requestMock.getHeader("X-bbb-site-id") >> "usBed"
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> [item1,item2]
		
		item1.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"
		
		item2.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"

		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		item3.getRepositoryId() >> "sing1"
		1*chnlRepository.getItem("Mobile", "channelInfo") >> chanleItem
		1*chanleItem.getRepositoryId() >> "MobileWeb"
		//End validate site and chanel
		
		requestMock.resolveName("/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler") >> grFormHandler
		
		
		requestMock.getParameter("primaryRegistrantEmail") >> "hr@gmail.com"
		requestMock.getParameter("password") >> "123415"
		1*profService.loginUser("hr@gmail.com", "123415") >> "rVo"
		
		2*grFormHandler.getErrorMap() >> ["erId":"error"]
		
		requestMock.getLocale() >> {new Locale("en_US")}
		
		// getAllFormExceptions 
		getAllFormException(grFormHandler)

		//End getAll
		
		rAPIManager.isLoggingDebug() >> true
		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		Set<String> error = value.get("result")
		error == result
		1*requestMock.setParameter("isFromThirdParty", true)
		1*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	
	def"createRegistry2. TC to create registry when chanel is desktop"(){
		given:
		
		rAPIManager = Spy()
		rAPIManager.setSiteRepository(sRepository)
		rAPIManager.setChannelRepository(chnlRepository)
		rAPIManager.setProfileServices(profService)
		rAPIManager.setLblTxtTemplateManager(lblTexTManager)
		GiftRegistryFormHandler grFormHandler = Mock()
		//validateSite and channel
		Set sites =[singleSite]
		Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
		RepositoryView rViewMock = Mock()
		requestMock.getHeader("X-bbb-site-id") >> "usBed"
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> [item1,item2]
		
		item1.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"
		
		item2.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"

		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		item3.getRepositoryId() >> "sing1"
		1*chnlRepository.getItem("Mobile", "channelInfo") >> chanleItem
		2*chanleItem.getRepositoryId() >> "DesktopWeb"
		//End validate site and chanel
		
		requestMock.resolveName("/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler") >> grFormHandler
		
		
		requestMock.getParameter("primaryRegistrantEmail") >> "hr@gmail.com"
		requestMock.getParameter("password") >> null
		//1*profService.loginUser("hr@gmail.com", "123415") >> "rVo"
		
		2*grFormHandler.getErrorMap() >> [:]
		
		requestMock.getLocale() >> {new Locale("en_US")}
		
		// getAllFormExceptions
		getAllFormException(grFormHandler)
		//End getAll
		
		rAPIManager.isLoggingDebug() >> false
		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		Set<String> error = value.get("result")
		error == result
		1*requestMock.setParameter("isFromThirdParty", true)
		1*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	def"createRegistry2. TC to create registry when primary email is is null"(){
		given:
		
		rAPIManager = Spy()
		rAPIManager.setSiteRepository(sRepository)
		rAPIManager.setChannelRepository(chnlRepository)
		rAPIManager.setProfileServices(profService)
		rAPIManager.setLblTxtTemplateManager(lblTexTManager)
		GiftRegistryFormHandler grFormHandler = Mock()
		RegistryResVO rrVO = new RegistryResVO ()
		ResponseErrorVO rErrorVO = new ResponseErrorVO()
		
		//validateSite and channel
		Set sites =[singleSite]
		Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
		RepositoryView rViewMock = Mock()
		requestMock.getHeader("X-bbb-site-id") >> "usBed"
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> [item1,item2]
		
		item1.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"
		
		item2.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"

		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		item3.getRepositoryId() >> "sing1"
		1*chnlRepository.getItem("Mobile", "channelInfo") >> {throw new RepositoryException("exception")}
		//2*chanleItem.getRepositoryId() >> "DesktopWeb"
		//End validate site and chanel
		
		requestMock.resolveName("/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler") >> grFormHandler
		
		
		requestMock.getParameter("primaryRegistrantEmail") >> null
		requestMock.getParameter("password") >> null
		//1*profService.loginUser("hr@gmail.com", "123415") >> "rVo"
		
		grFormHandler.getErrorMap() >> null
		
		requestMock.getLocale() >> {new Locale("en_US")}
		
		// getAllFormExceptions
		Vector exception = new Vector()
		grFormHandler.getFormExceptions()  >> exception
		//End getAll
		1*rAPIManager.setCreateRegistryParameters(requestMock, _) >> {}
		grFormHandler.getCreateRegistryResVO() >> rrVO
		rrVO.setWebServiceError(true) 
		rrVO.setError(rErrorVO)
		rErrorVO.setErrorMsg("error message")
		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		String error = value.get("result")
		error.equals("error message")
		1*requestMock.setParameter("isFromThirdParty", true)
		1*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	def"createRegistry2. TC to create registry when form exception is null"(){
		given:
		
		rAPIManager = Spy()
		rAPIManager.setSiteRepository(sRepository)
		rAPIManager.setChannelRepository(chnlRepository)
		rAPIManager.setProfileServices(profService)
		rAPIManager.setLblTxtTemplateManager(lblTexTManager)
		GiftRegistryFormHandler grFormHandler = Mock()
		RegistryResVO rrVO = new RegistryResVO ()
		ResponseErrorVO rErrorVO = new ResponseErrorVO()
		
		//validateSite and channel
		Set sites =[singleSite]
		Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
		RepositoryView rViewMock = Mock()
		requestMock.getHeader("X-bbb-site-id") >> "usBed"
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> [item1,item2]
		
		item1.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"
		
		item2.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"

		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		item3.getRepositoryId() >> "sing1"
		1*chnlRepository.getItem("Mobile", "channelInfo") >> chanleItem
		2*chanleItem.getRepositoryId() >> "DesktopWeb"
		//End validate site and chanel
		
		requestMock.resolveName("/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler") >> grFormHandler
		
		
		requestMock.getParameter("primaryRegistrantEmail") >> null
		requestMock.getParameter("password") >> null
		//1*profService.loginUser("hr@gmail.com", "123415") >> "rVo"
		
		grFormHandler.getErrorMap() >> null
		
		requestMock.getLocale() >> {new Locale("en_US")}
		
		// getAllFormExceptions
		grFormHandler.getFormExceptions()  >> null
		//End getAll
		1*rAPIManager.setCreateRegistryParameters(requestMock, _) >> {}
		grFormHandler.getCreateRegistryResVO() >> rrVO
		rrVO.setWebServiceError(false)
		rrVO.setRegistryId(1235)
/*		rrVO.setError(rErrorVO)
		rErrorVO.setErrorMsg("error message")
*/		
		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		!value.get("errorExists")
		String error = value.get("result")
		error=='1235'
		1*requestMock.setParameter("isFromThirdParty", true)
		1*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	def"createRegistry2. TC when site and channel is not valid"(){
		given:
		
         setSpySetter()	
		 GiftRegistryFormHandler grFormHandler = Mock()
		//validateSite and channel
		Set sites =[singleSite]
	//	Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
		requestMock.getHeader("X-bbb-site-id") >> "usBed"
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> [item1]
		
		item1.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"
		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		item3.getRepositoryId() >> "sing1"
		1*chnlRepository.getItem("Mobile", "channelInfo") >> chanleItem
		2*chanleItem.getRepositoryId() >> "notMobiloeOrDesktop"
		//End validate site and chanel
		
		requestMock.getLocale() >> {new Locale("en_US")}
		lblTexTManager.getErrMsg("err_invalid_request_headers", _, null) >> ""
		

		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		String error = value.get("result")
		error=='Please enter valid Request values.'
		0*requestMock.setParameter("isFromThirdParty", true)
		0*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	def"createRegistry2. TC when site and channel is not valid and chanel item is null"(){
		given:
		
		 setSpySetter()
		 GiftRegistryFormHandler grFormHandler = Mock()
		//validateSite and channel
		Set sites =[singleSite]
	//	Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
		requestMock.getHeader("X-bbb-site-id") >> "usBed"
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> [item1]
		
		item1.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"
		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		item3.getRepositoryId() >> "sing1"
		1*chnlRepository.getItem("Mobile", "channelInfo") >> null
		//2*chanleItem.getRepositoryId() >> "notMobiloeOrDesktop"
		//End validate site and chanel
		
		requestMock.getLocale() >> {new Locale("en_US")}
		lblTexTManager.getErrMsg("err_invalid_request_headers", _, null) >> ""
		

		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		String error = value.get("result")
		error=='Please enter valid Request values.'
		0*requestMock.setParameter("isFromThirdParty", true)
		0*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	def"createRegistry2. TC for when valide site map dosent contain site id"(){
		given:
		
		 setSpySetter()
		 GiftRegistryFormHandler grFormHandler = Mock()
		//validateSite and channel
		Set sites =[singleSite]
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> [item1]
		
		item1.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"
		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		item3.getRepositoryId() >> "sing2"
		
		
		requestMock.getLocale() >> {new Locale("en_US")}
		lblTexTManager.getErrMsg("err_invalid_request_headers", _, null) >> ""
		
		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		String error = value.get("result")
		error=='Please enter valid Request values.'
		0*requestMock.setParameter("isFromThirdParty", true)
		0*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	def"createRegistry2. TC for when siteConfiguration item is null"(){
		given:
		
		 setSpySetter()
		 GiftRegistryFormHandler grFormHandler = Mock()
		//validateSite and channel
		Set sites =[singleSite]
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> {throw new RepositoryException("exception")}
		
//		item1.getPropertyValue("sites") >> sites
//		1*singleSite.getRepositoryId() >> "sing1"
		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> null
		item3.getRepositoryId() >> "sing2"
		
		
		requestMock.getLocale() >> {new Locale("en_US")}
		lblTexTManager.getErrMsg("err_invalid_request_headers", _, null) >> ""
		
		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		String error = value.get("result")
		error=='Please enter valid Request values.'
		0*requestMock.setParameter("isFromThirdParty", true)
		0*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	def"createRegistry2. TC for when valide site map is null"(){
		given:
		
		 setSpySetter()
		 GiftRegistryFormHandler grFormHandler = Mock()
		//validateSite and channel
		Set sites =[singleSite]
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> null
		
	    //End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		
		
		requestMock.getLocale() >> {new Locale("en_US")}
		lblTexTManager.getErrMsg("err_invalid_request_headers", _, null) >> ""
		rAPIManager.isLoggingDebug() >> true
		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		String error = value.get("result")
		error=='Please enter valid Request values.'
		0*requestMock.setParameter("isFromThirdParty", true)
		0*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	def"createRegistry2. TC for RepositoryException while getting siteConfiguration "(){
		given:
		
		 setSpySetter()
		 GiftRegistryFormHandler grFormHandler = Mock()
		//validateSite and channel
		Set sites =[singleSite]
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> null
		
		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> {throw new RepositoryException("exception")}
		
		
		requestMock.getLocale() >> {new Locale("en_US")}
		lblTexTManager.getErrMsg("err_invalid_request_headers", _, null) >> ""
		rAPIManager.isLoggingDebug() >> true
		when:
		
		Map<String, Object> value =  rAPIManager.createRegistry2()
		then:
		value.get("errorExists")
		String error = value.get("result")
		error=='Please enter valid Request values.'
		0*requestMock.setParameter("isFromThirdParty", true)
		0*grFormHandler.handleCreateRegistry(requestMock, responseMock)
		
	}
	
	private getAllFormException(GenericFormHandler grFormHandler){
		rAPIManager.setCreateRegistryParameters(requestMock, _) >> {}
		Vector exception = new Vector()
		exception.add(dExceptionMock)
		exception.add(dExceptionMock1)
		grFormHandler.getFormExceptions()  >> exception
		
		1*dExceptionMock.getErrorCode() >> "err1"
		1*dExceptionMock1.getErrorCode() >> "err2"
		
		1*lblTexTManager.getErrMsg("err1", _, null) >> "updateError"
		1*lblTexTManager.getErrMsg("err2", _, null) >> ""
		
		1*dExceptionMock.getMessage() >> "123"
		1*dExceptionMock1.getMessage() >> "1234"
		1*lblTexTManager.getErrMsg("123", _, null) >> "detailUpdateMessage"
		1*lblTexTManager.getErrMsg("1234", _, null) >> ""

	}
	
	private setSpySetter(){
		rAPIManager = Spy()
		rAPIManager.setSiteRepository(sRepository)
		rAPIManager.setChannelRepository(chnlRepository)
		rAPIManager.setProfileServices(profService)
		rAPIManager.setLblTxtTemplateManager(lblTexTManager)
		rAPIManager.setProfileTools(pToolsMock)
		requestMock.getHeader("X-bbb-site-id") >> "usBed"
		
		

	}
	
	/********************************************** createUser2 ************************************/
	
	def"createUser2.TC to create  user while creating gets some error"(){
		given:
			setSpySetter()
			Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
			validateSiteAndChanleForTrueValue()
			Dictionary<String, String> value = new Hashtable()
			BBBProfileFormHandler proFormHandler = Mock()
			requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proFormHandler 
			//set loginParam
			 requestMock.getParameter("email") >> "harry@gmail.com"
			 requestMock.getParameter("password") >> "123456"
			requestMock.getParameter("confirmPassword") >> "123456"
			 requestMock.getParameter("firstName") >> "harry"
			 requestMock.getParameter("lastName") >> "harry"
			 requestMock.getParameter("mobileNumber") >> "12456789"
			 requestMock.getParameter("phoneNumber") >> "454755"
			 requestMock.getParameter("emailOptIn") >> "123"
			 requestMock.getParameter("shareCheckBoxEnabled") >> "true"
			 1*proFormHandler.getPropertyManager() >> propertyManager
			 proFormHandler.getValue() >> value
			 
			 propertyManager.setEmailAddressPropertyName("email") 
			 propertyManager.setPasswordPropertyName("password") 
			 propertyManager.setConfirmPasswordPropertyName("confirmPassword") 
			 propertyManager.setFirstNamePropertyName("fName") 
			 propertyManager.setLastNamePropertyName("lName") 
			 propertyManager.setMobileNumberPropertyName("mNumber") 
			 propertyManager.setPhoneNumberPropertyName("phoneNumber") 
	
			//
			 
			// proFormHandler.handleCreateUser(requestMock, responseMock)
			 2*proFormHandler.getErrorMap() >> ["errorId":"1234"]
			 requestMock.getLocale() >> {new Locale("en_US")}
			 getAllFormException(proFormHandler)
		when:
		Map<String, Object> output = rAPIManager.createUser2()
		then:
		output.get("errorExists")
		output.get("result") == result
		
		1*requestMock.setParameter("isFromThirdParty", true)
		1*proFormHandler.handleCreateUser(requestMock, responseMock)
		value.get("email") == "harry@gmail.com"
		value.get("password") == "123456"
		value.get("fName") == "harry"
		value.get("lName") == "harry"
		value.get("mNumber") == "12456789"
		value.get("phoneNumber") == "454755"
	}
	
	def"createUser2.TC to create  user when email , password, firstName etc is empty"(){
		given:
			setSpySetter()
			Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
			validateSiteAndChanleForTrueValue()
			Dictionary<String, String> value = new Hashtable()
			BBBProfileFormHandler proFormHandler = Mock()
			requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proFormHandler
			//set loginParam
			 requestMock.getParameter("email") >> ""
			 requestMock.getParameter("password") >> ""
			requestMock.getParameter("confirmPassword") >> ""
			 requestMock.getParameter("firstName") >> ""
			 requestMock.getParameter("lastName") >> ""
			 requestMock.getParameter("mobileNumber") >> ""
			 requestMock.getParameter("phoneNumber") >> ""
			 requestMock.getParameter("emailOptIn") >> ""
			 requestMock.getParameter("shareCheckBoxEnabled") >> ""
			 1*proFormHandler.getPropertyManager() >> propertyManager
			 proFormHandler.getValue() >> value
			 
		     2*proFormHandler.getErrorMap() >> [:]
			 requestMock.getLocale() >> {new Locale("en_US")}
			 getAllFormException(proFormHandler)
			 rAPIManager.isLoggingDebug() >> true
		when:
		Map<String, Object> output = rAPIManager.createUser2()
		then:
		output.get("errorExists")
		output.get("result") == result
		
		1*requestMock.setParameter("isFromThirdParty", true)
		1*proFormHandler.handleCreateUser(requestMock, responseMock)
		//profileFormHandler.handleCreateUser(request, response)
	}
	
	def"createUser2.TC to create  user when there is no error"(){
		given:
			setSpySetter()
			Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
			validateSiteAndChanleForTrueValue()
			Dictionary<String, String> value = new Hashtable()
			BBBProfileFormHandler proFormHandler = Mock()
			requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proFormHandler
			//set loginParam
			 requestMock.getParameter("email") >> ""
			 requestMock.getParameter("password") >> ""
			requestMock.getParameter("confirmPassword") >> ""
			 requestMock.getParameter("firstName") >> ""
			 requestMock.getParameter("lastName") >> ""
			 requestMock.getParameter("mobileNumber") >> ""
			 requestMock.getParameter("phoneNumber") >> ""
			 requestMock.getParameter("emailOptIn") >> ""
			 requestMock.getParameter("shareCheckBoxEnabled") >> ""
			 1*proFormHandler.getPropertyManager() >> propertyManager
			 proFormHandler.getValue() >> value
			 
			 2*proFormHandler.getErrorMap() >> [:]
			 requestMock.getLocale() >> {new Locale("en_US")}
			 
			 Vector exception = new Vector()
			 proFormHandler.getFormExceptions()  >> exception
			 
			 pToolsMock.getItemFromEmail(_) >> muRepItem
			 muRepItem.getRepositoryId() >> "proId"
			 
		when:
			Map<String, Object> output = rAPIManager.createUser2()
		then:
			!output.get("errorExists")
			output.get("result") == "proId"
			
			1*requestMock.setParameter("isFromThirdParty", true)
			1*proFormHandler.handleCreateUser(requestMock, responseMock)
			
		//profileFormHandler.handleCreateUser(request, response)
	}
	
	def"createUser2.TC to create  user when there is no error and profile error map is null"(){
		given:
			setSpySetter()
			Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
			validateSiteAndChanleForTrueValue()
			Dictionary<String, String> value = new Hashtable()
			BBBProfileFormHandler proFormHandler = Mock()
			requestMock.resolveName("/atg/userprofiling/ProfileFormHandler") >> proFormHandler
			//set loginParam
			 requestMock.getParameter("email") >> ""
			 requestMock.getParameter("password") >> ""
			 requestMock.getParameter("confirmPassword") >> ""
			 requestMock.getParameter("firstName") >> ""
			 requestMock.getParameter("lastName") >> ""
			 requestMock.getParameter("mobileNumber") >> ""
			 requestMock.getParameter("phoneNumber") >> ""
			 requestMock.getParameter("emailOptIn") >> ""
			 requestMock.getParameter("shareCheckBoxEnabled") >> ""
			 1*proFormHandler.getPropertyManager() >> propertyManager
			 proFormHandler.getValue() >> value
			 
			 1*proFormHandler.getErrorMap() >> null
			 //requestMock.getLocale() >> {new Locale("en_US")}
			 
			 //Vector exception = new Vector()
			 proFormHandler.getFormExceptions()  >> null
			 
			 pToolsMock.getItemFromEmail(_) >> null
			// muRepItem.getRepositoryId() >> "proId"
			 rAPIManager.isLoggingDebug() >> true
		when:
			Map<String, Object> output = rAPIManager.createUser2()
		then:
			output.get("errorExists")
			List error = output.get("result")
			error.get(0) == "A System Error Occurred. Please try again later."
			
			1*requestMock.setParameter("isFromThirdParty", true)
			1*proFormHandler.handleCreateUser(requestMock, responseMock)
			
		//profileFormHandler.handleCreateUser(request, response)
	}
	
	def "createUser2. TC when site and channel is not valid" (){
		given:
		setSpySetter()
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> {throw new RepositoryException("exception")}
		
		requestMock.getLocale() >> {new Locale("en_US")}
		lblTexTManager.getErrMsg("err_invalid_request_headers", _, null) >> "site is not valid"
		
		when:
			Map<String, Object> output = rAPIManager.createUser2()
		then:
			output.get("errorExists")
			output.get("result") == "site is not valid"
		

		
	}
	
	private validateSiteAndChanleForTrueValue(){
		
		Set sites =[singleSite]
		Set<String> result = ['1234', 'updateError', 'detailUpdateMessage']
		RepositoryView rViewMock = Mock()
		requestMock.getHeader("X-bbb-site-id") >> "usBed"
		
		// getValidSites
		sRepository.getView("siteGroup") >> rViewMock
		1*rAPIManager.executeQuery(_, rViewMock) >> [item1,item2]
		
		item1.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"
		
		item2.getPropertyValue("sites") >> sites
		1*singleSite.getRepositoryId() >> "sing1"

		//End
		
		requestMock.getHeader("X-bbb-channel") >> "Mobile"
		sRepository.getItem("usBed", "siteConfiguration") >> item3
		item3.getRepositoryId() >> "sing1"
		1*chnlRepository.getItem("Mobile", "channelInfo") >> chanleItem
		1*chanleItem.getRepositoryId() >> "MobileWeb"
	}
	
	
	/********************************************* loadChecklistItems ******************************************/
	
	def"loadChecklistItems"(){
		given:
		    MyItemCategoryVO myItemCat = new MyItemCategoryVO() 
			
			Map<String, String> inputParam =["categoryId":"cId","eventDate" : "12/12/1990"]
			1*ritemDisplayDroplet.setRegItemsSecondCall(requestMock, responseMock) >> {throw new ServletException("exception")}
			requestMock.getObjectParameter("ephCategoryBuckets") >> [myItemCat]
		when:
			List<MyItemCategoryVO> value = rAPIManager.loadChecklistItems(inputParam)
		then:
		    value == [myItemCat]
			1 * requestMock.setParameter("categoryId", "cId");
			1 * requestMock.setParameter("eventDate" , "12/12/1990");
			1 * requestMock.setParameter("siteId", _);

	}
	
	def"loadChecklistItems.TC when categoryId and eventDate is empty"(){
		given:
			MyItemCategoryVO myItemCat = new MyItemCategoryVO()
			
			Map<String, String> inputParam =["categoryId":"","eventDate" : ""]
			requestMock.getObjectParameter("ephCategoryBuckets") >> [myItemCat]
		when:
			List<MyItemCategoryVO> value = rAPIManager.loadChecklistItems(inputParam)
		then:
			value == [myItemCat]
			1 * requestMock.setParameter("categoryId", null)
			1 * requestMock.setParameter("eventDate" , null)
			1 * requestMock.setParameter("siteId", _)
			1*ritemDisplayDroplet.setRegItemsSecondCall(requestMock, responseMock)

	}
	
	def"loadChecklistItems.TC when categoryId and eventDate is null"(){
		given:
			MyItemCategoryVO myItemCat = new MyItemCategoryVO()
			
			Map<String, String> inputParam =["categoryId":null, "eventDate" : null]
			requestMock.getObjectParameter("ephCategoryBuckets") >> [myItemCat]
		when:
			List<MyItemCategoryVO> value = rAPIManager.loadChecklistItems(inputParam)
		then:
			value == [myItemCat]
			1 * requestMock.setParameter("categoryId", null)
			1 * requestMock.setParameter("eventDate" , null)
			1 * requestMock.setParameter("siteId", _)
			1*ritemDisplayDroplet.setRegItemsSecondCall(requestMock, responseMock)

	}
	
	def"loadChecklistItems.TC when input param is null"(){
		given:
			
			Map<String, String> inputParam = null
			
		when:
			List<MyItemCategoryVO> value = rAPIManager.loadChecklistItems(inputParam)
		then:
			value == null
			0 * requestMock.setParameter("categoryId", null)
			0 * requestMock.setParameter("eventDate" , null)
			0 * requestMock.setParameter("siteId", _)

	}

	
	/********************************************* setCreateRegistryParameters  **********************************************/
	
	def"setCreateRegistryParameters.TC to set registry param"(){
		given:
		
		GiftRegistryFormHandler grFormHandler = Mock()
		EventVO eventVO = new EventVO() 
		RegistrantVO registrantVO = new RegistrantVO ()
		RegistrantVO coRegistrant = new RegistrantVO ()
		ShippingVO shippingVO = new ShippingVO()
		
		
		RegistryTypes rType = Mock()
		AddressVO addressVO = new AddressVO()
		AddressVO shippingAdd = new AddressVO()
		AddressVO futureShippingAdd = new AddressVO()
		
		 setRequestParamForSetCreateRegistryParameters("male")
		 
		 grFormHandler.getRegistryVO() >> registryVO
		  registryVO.setEvent(eventVO) 
		  registryVO.setPrimaryRegistrant(registrantVO)  
		  registryVO.getRegistryType() >> rType
		  
		  registrantVO.setContactAddress(addressVO)
		  
		  registryVO.setCoRegistrant(coRegistrant)
		  
		  registryVO.setShipping(shippingVO)
		  shippingVO.setShippingAddress(shippingAdd)
		  
		  shippingVO.setFutureshippingAddress(futureShippingAdd)
		  
		when:
			rAPIManager.setCreateRegistryParameters(requestMock, grFormHandler)
			
		then:
		
		1*grFormHandler.setRegistryEventType("rEventType")
		eventVO.getEventDate() == "12/12/2010"
		eventVO.getBabyNurseryTheme() == "theme"
		eventVO.getCollege() ==  "col"
		eventVO.getGuestCount() == "2"
		registrantVO.getBabyMaidenName() == "jac"
		eventVO.getBabyGender() == "male"
		eventVO.getBabyName() == "jack"
		eventVO.getBirthDate() == "15/12/2014"
		eventVO.getShowerDate() == "01/01/2012"
		
		registrantVO.getFirstName() == "jamsss"
		registrantVO.getPrimaryPhone() == "123456789"
		registrantVO.getLastName() == "jack"
		registrantVO.getEmail() == "test@gmail.com"
		
		//1*rType.setRegistryTypeName("rtype")
		1*grFormHandler.setRegContactAddress("street1")
		
		
		registrantVO.getCellPhone() == "123456789"
		addressVO.getFirstName() == "fname"
		addressVO.getLastName() == "si"
		addressVO.getAddressLine1() == "12bord"
		addressVO.getAddressLine2() == "1st cross"
		addressVO.getCity() == "ney york"
		addressVO.getState() == "york"
		addressVO.getZip() == "10245"
		
		coRegistrant.getFirstName() == "cRFName"
		coRegistrant.getLastName() == "cRLName"
		coRegistrant.getEmail() == "rest@gmail.com"
		coRegistrant.getBabyMaidenName() == "maidi"
		
		1*grFormHandler.setCoRegEmailNotFoundPopupStatus("active")
		1*grFormHandler.setShippingAddress("45 town")
		
		shippingAdd.getFirstName() == "sFname"
		shippingAdd.getLastName() == "sLName"
		shippingAdd.getAddressLine1() == "sAdress"
		shippingAdd.getAddressLine2() == "saddress2"
		shippingAdd.getCity() == "sCity"
		shippingAdd.getZip() == "saddressZip"
		shippingAdd.getState() == "sAddressState"

		registryVO.getOptInWeddingOrBump() == "optIn"
	
		1*grFormHandler.setFutureShippingDateSelected("4/12/210")
		1*grFormHandler.setFutureShippingAddress("fSaddress")
		
		shippingVO.getFutureShippingDate() == "fShipDate"

		futureShippingAdd.getFirstName() == "fsAddressName"
		futureShippingAdd.getLastName() == "fsAddressName"
		futureShippingAdd.getAddressLine1() == "fsAddressl1"
		futureShippingAdd.getAddressLine2() == "fsAddress2"
		futureShippingAdd.getZip() ==    "fsaddreeZ"
		futureShippingAdd.getState() == "fsadState"
		futureShippingAdd.getCity() == "fsAddCity"
	
		registryVO.getRefStoreContactMethod() == "rscMethod"
		registryVO.getPrefStoreNum() == "1245"
		registryVO.getNetworkAffiliation() == "nwAffi"


	
	}
	
	def"setCreateRegistryParameters.TC to set registry param when baby gender lenght is 1"(){
		given:
		
		GiftRegistryFormHandler grFormHandler = Mock()
		EventVO eventVO = new EventVO()
		RegistrantVO registrantVO = new RegistrantVO ()
		RegistrantVO coRegistrant = new RegistrantVO ()
		ShippingVO shippingVO = new ShippingVO()
		
		
		RegistryTypes rType = Mock()
		AddressVO addressVO = new AddressVO()
		AddressVO shippingAdd = new AddressVO()
		AddressVO futureShippingAdd = new AddressVO()
		
		 setRequestParamForSetCreateRegistryParameters("m")
		 
		 grFormHandler.getRegistryVO() >> registryVO
		  registryVO.setEvent(eventVO)
		  registryVO.setPrimaryRegistrant(registrantVO)
		  registryVO.getRegistryType() >> rType
		  
		  registrantVO.setContactAddress(addressVO)
		  
		  registryVO.setCoRegistrant(coRegistrant)
		  
		  registryVO.setShipping(shippingVO)
		  shippingVO.setShippingAddress(shippingAdd)
		  
		  shippingVO.setFutureshippingAddress(futureShippingAdd)
		  
		when:
			rAPIManager.setCreateRegistryParameters(requestMock, grFormHandler)
			
		then:
		
		1*grFormHandler.setRegistryEventType("rEventType")
		eventVO.getEventDate() == "12/12/2010"
		eventVO.getBabyNurseryTheme() == "theme"
		eventVO.getCollege() ==  "col"
		eventVO.getGuestCount() == "2"
		registrantVO.getBabyMaidenName() == "jac"
		eventVO.getBabyGender() == null


		registryVO.getOptInWeddingOrBump() == "optIn"
		1*grFormHandler.addFormException(_)

	
	}
	
	def"setCreateRegistryParameters.TC to set registry param when baby gender is empty"(){
		given:
		
		GiftRegistryFormHandler grFormHandler = Mock()
		EventVO eventVO = new EventVO()
		RegistrantVO registrantVO = new RegistrantVO ()
		RegistrantVO coRegistrant = new RegistrantVO ()
		ShippingVO shippingVO = new ShippingVO()
		
		
		RegistryTypes rType = Mock()
		AddressVO addressVO = new AddressVO()
		AddressVO shippingAdd = new AddressVO()
		AddressVO futureShippingAdd = new AddressVO()
		
		 setRequestParamForSetCreateRegistryParameters("")
		 
		 grFormHandler.getRegistryVO() >> registryVO
		  registryVO.setEvent(eventVO)
		  registryVO.setPrimaryRegistrant(registrantVO)
		  registryVO.getRegistryType() >> rType
		  
		  registrantVO.setContactAddress(addressVO)
		  
		  registryVO.setCoRegistrant(coRegistrant)
		  
		  registryVO.setShipping(shippingVO)
		  shippingVO.setShippingAddress(shippingAdd)
		  
		  shippingVO.setFutureshippingAddress(futureShippingAdd)
		  
		when:
			rAPIManager.setCreateRegistryParameters(requestMock, grFormHandler)
			
		then:
		
		1*grFormHandler.setRegistryEventType("rEventType")
		eventVO.getEventDate() == "12/12/2010"
		eventVO.getBabyNurseryTheme() == "theme"
		eventVO.getCollege() ==  "col"
		eventVO.getGuestCount() == "2"
		registrantVO.getBabyMaidenName() == "jac"
		eventVO.getBabyGender() == null


		registryVO.getOptInWeddingOrBump() == "optIn"
		0*grFormHandler.addFormException(_)

	
	}

	private setRequestParamForSetCreateRegistryParameters(String babyGender) {
		
		requestMock.getParameter("registryEventType") >> "rEventType"
		requestMock.getParameter("eventDate")  >> "12/12/2010"
		requestMock.getParameter("babyNurseryTheme") >> "theme"
		requestMock.getParameter("college")  >> "col"
		requestMock.getParameter("guestCount") >> "2"
		requestMock.getParameter("primaryRegBabyMaidenName") >> "jac"
		requestMock.getParameter("babyGender") >> babyGender
		requestMock.getParameter("babyName") >> "jack"
		requestMock.getParameter("birthDate") >> "15/12/2014"
		requestMock.getParameter("showerDate") >> "01/01/2012"
		requestMock.getParameter("primaryRegistrantFirstName") >> "jamsss"
		requestMock.getParameter("primaryRegistrantPhone") >> "123456789"
		requestMock.getParameter("primaryRegistrantLastName") >> "jack"
		requestMock.getParameter("primaryRegistrantEmail") >> "test@gmail.com"
		requestMock.getParameter("registryTypeName") >> "rType"
		requestMock.getParameter("regContactAddress") >> "street1"
		requestMock.getParameter("primaryRegistrantCellPhone")  >> "123456789"
		requestMock.getParameter("primaryRegContactAddFName") >> "fname"
		requestMock.getParameter("primaryRegContactAddLName") >> "si"
		requestMock.getParameter("primaryRegContactAddLine1") >> "12bord"
		requestMock.getParameter("primaryRegContactAddLine2") >> "1st cross"
		requestMock.getParameter("primaryRegContactAddCity") >> "ney york"
		requestMock.getParameter("primaryRegContactAddState") >> "york"
		requestMock.getParameter( "primaryRegContactAddZip") >> "10245"
		requestMock.getParameter("coRegFName") >> "cRFName"
		requestMock.getParameter("coRegLName") >> "cRLName"
		requestMock.getParameter("coRegEmail") >> "rest@gmail.com"
		requestMock.getParameter("coRegBabyMaidenName") >> "maidi"
		requestMock.getParameter("coRegEmailStatus") >> "active"
		requestMock.getParameter("shippingAddress") >> "45 town"
		requestMock.getParameter("shippingAddressFName") >> "sFname"
		requestMock.getParameter("shippingAddressLName") >> "sLName"
		requestMock.getParameter("shippingAddressLine1") >> "sAdress"
		requestMock.getParameter("shippingAddressLine2") >> "saddress2"
		requestMock.getParameter( "shippingAddressCity") >> "sCity"
		requestMock.getParameter( "shippingAddressState") >> "sAddressState"
		requestMock.getParameter("shippingAddressZip") >> "saddressZip"
		requestMock.getParameter("optInWeddingOrBump") >> "optIn"
		requestMock.getParameter("futureShipDateSelected") >> "4/12/210"
		requestMock.getParameter("futureShippingAddress") >> "fSaddress"
		requestMock.getParameter("futureShippingAddFName") >> "fsAddressName"
		requestMock.getParameter("futureShippingAddLName") >> "fsAddressName"
		requestMock.getParameter("futureShippingDate") >> "fShipDate"
		requestMock.getParameter("futureShippingAddLine1") >> "fsAddressl1"
		requestMock.getParameter("futureShippingAddLine2") >> "fsAddress2"
		requestMock.getParameter("futureShippingAddZip") >> "fsaddreeZ"
		requestMock.getParameter("futureShippingAddState") >> "fsadState"
		requestMock.getParameter("futureShippingAddCity") >> "fsAddCity"
		requestMock.getParameter("refStoreContactMethod") >> "rscMethod"
		requestMock.getParameter("prefStoreNum") >> "1245"
		requestMock.getParameter("networkAffiliation") >> "nwAffi"
	}
	
	
}

