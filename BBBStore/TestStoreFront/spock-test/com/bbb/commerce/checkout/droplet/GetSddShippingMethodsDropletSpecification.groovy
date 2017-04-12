package com.bbb.commerce.checkout.droplet

import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.pricing.PricingTools
import atg.repository.RepositoryException
import com.bbb.commerce.browse.vo.SddZipcodeVO
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.RegionVO
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean;
import javax.servlet.http.HttpSession

import spock.lang.specification.BBBExtendedSpec;

class GetSddShippingMethodsDropletSpecification extends BBBExtendedSpec {
	def GetSddShippingMethodsDroplet gssmDropletMock = Mock()
	def BBBOrder orderMock = Mock()
	def BBBSessionBean sessionBean = Mock()
	def BBBSameDayDeliveryManager sddManagerMock = Mock()
	def BBBCatalogToolsImpl catalogToolsMock = Mock()
	def BBBShippingGroupManager sgManagerMock = Mock()
	def HardgoodShippingGroup hgdsMock = Mock()
	def HttpSession httpSessionMock = Mock()
	def PricingTools pricingToolsMock = Mock()
	
	RegionVO regionVO = new RegionVO()
	SddZipcodeVO ssdZipcodeVO = new SddZipcodeVO()
	SddZipcodeVO landingdZipcodeVO = new SddZipcodeVO()
	
	
	def setup(){
	  gssmDropletMock = new GetSddShippingMethodsDroplet(sameDayDeliveryManager : sddManagerMock, shippingGroupManager:sgManagerMock,pricingTools : pricingToolsMock)
	  //requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
   }
	
	
   def "service . TC to check the ssd shipping method list added in request object when choosesddOption is true  "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   Set storeIdsSet =  new HashSet()
	   storeIdsSet.add("storeId") 
	   
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >> true
	   requestMock.getParameter("currentZip") >> "10002"
	   
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   1* catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   
/*	   ssdZipcodeVO.setZipcode("10001")
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   
*/	   ///// to get region VO ////
	   ssdZipcodeVO.setRegionId("re456")
	   sessionBean.getShippingZipcodeVO() >> ssdZipcodeVO
	   catalogToolsMock.getStoreIdsFromRegion(ssdZipcodeVO.getRegionId()) >> regionVO
	   
	   regionVO.setStoreIds(storeIdsSet)
	   sddManagerMock.checkSBCInventoryForSdd(regionVO.getStoreIds(), orderMock, false) >> "itemEligible"
	   
	   1 * sgManagerMock.calculateShippingCost(_, orderMock, [hgdsMock],_)
	   when:
	   gssmDropletMock.service(requestMock, responseMock) 
	   then:
	   
	   1 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "itemEligible")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   
   }
   
   def "service . TC to check the ssd shipping method list added in request object when choosesddOption is true and region VO is null  "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   Set storeIdsSet =  new HashSet()
	   storeIdsSet.add("storeId")
	   
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >> true
	   //requestMock.getParameter("currentZip") >> "10002"
	   
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   1* catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	    // current zipcode VO 
        ssdZipcodeVO.setZipcode("10001")
	    sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   
       ///// to get region VO ////
	   ssdZipcodeVO.setRegionId("re456")
	   sessionBean.getShippingZipcodeVO() >> ssdZipcodeVO
	   1*catalogToolsMock.getStoreIdsFromRegion(ssdZipcodeVO.getRegionId()) >> null
	   
	   regionVO.setStoreIds(storeIdsSet)
	   
	   1 * sgManagerMock.calculateShippingCost(_, orderMock, [hgdsMock],_)
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0*sddManagerMock.checkSBCInventoryForSdd(regionVO.getStoreIds(), orderMock)
	   
   }
   
   def "service . TC to check the ssd shipping method list added in request object when currentZipCodeVO  is null  "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   Set storeIdsSet =  new HashSet()
	   storeIdsSet.add("storeId")
	   
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >> true
	   //requestMock.getParameter("currentZip") >> "10002"
	   
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   1* catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
		// current zipcode VO
		ssdZipcodeVO.setZipcode("10001")
		sessionBean.getCurrentZipcodeVO() >> null
	   
	   ///// to get region VO ////
	   ssdZipcodeVO.setRegionId("re456")
	   
	   
	   1 * sgManagerMock.calculateShippingCost(_, orderMock, [hgdsMock],_)
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0 *catalogToolsMock.getStoreIdsFromRegion(ssdZipcodeVO.getRegionId())
   }
   
   def "service . TC to check the ssd shipping method list added in request object when ShippingZipcodeVO  is null  "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   Set storeIdsSet =  new HashSet()
	   storeIdsSet.add("storeId")
	   
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >> true
	   requestMock.getParameter("currentZip") >> "10002"
	   
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   1* catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   
		// ShippingZipcodeVO
   
	   ssdZipcodeVO.setRegionId("re456")
	   sessionBean.getShippingZipcodeVO() >> null
	   1 * sgManagerMock.calculateShippingCost(_, orderMock, [hgdsMock],_)
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0 *catalogToolsMock.getStoreIdsFromRegion(ssdZipcodeVO.getRegionId())
   }
   
   ////////////// when ssdOption is false //////////
   
   def "service . TC to check the ssd shipping method list added in request object when chooseSddOption is false   "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   gssmDropletMock = Spy()
	   gssmDropletMock.setSameDayDeliveryManager(sddManagerMock)
	   gssmDropletMock.setShippingGroupManager(sgManagerMock)
	   
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >> false
	   requestMock.getParameter("currentZip") >> ""
	  requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   
	   
	   //current zipCode VO
       sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
/*	   //sessionBean.getLandingZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setZipcode("1203")
	   ssdZipcodeVO.setSddEligibility(true)
  */ 
	   1*sddManagerMock.populateDataInVO(sessionBean, requestMock, _, false, false, true) >> regionVO
	   1*sddManagerMock.checkForSDDEligibility(requestMock, orderMock, regionVO, _, _) >>  "sddEligible"
	   gssmDropletMock.isDisplaySSD(regionVO,sessionBean,true) >> true
	   
	      
	   1 * sgManagerMock.calculateShippingCost(_, orderMock, [hgdsMock],_)
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   1 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "sddEligible")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   1 * requestMock.setParameter("regionVO", regionVO)
	   1 * gssmDropletMock.logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
   }
   
   def "service . TC  to check the ssd shipping method list added in request object when chooseSddOption is false, zipCode is blanck and region VO is null    "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   gssmDropletMock = Spy()
	   gssmDropletMock.setSameDayDeliveryManager(sddManagerMock)
	   gssmDropletMock.setShippingGroupManager(sgManagerMock)

	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >> 
	   requestMock.getParameter("currentZip") >> ""
	  requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   gssmDropletMock.isDisplaySSD(regionVO,sessionBean,true) >> false
	   
	   //current zipCode VO
       sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
/*	   sessionBean.setLandingZipcodeVO(ssdZipcodeVO)
	   ssdZipcodeVO.setZipcode("1203")
	   ssdZipcodeVO.setSddEligibility(true)
*/	   
	   1*sddManagerMock.populateDataInVO(sessionBean, requestMock, _, false, false, true) >> null
	   1*sddManagerMock.checkForSDDEligibility(requestMock, orderMock, null, _, _) >>  "sssss"
	   
		  
	   1 * sgManagerMock.calculateShippingCost(_, orderMock, [hgdsMock],_)
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "sssss")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0 * gssmDropletMock.logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
	   
   }
   
/*   def "service . TC  to check the ssd shipping method list added in request object when chooseSddOption is false, zipCode is blanck , region VO is null and SddEligibility is true for landing zipcode    "(){
	   given:
   
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>
	   requestMock.getParameter("currentZip") >> ""
	  requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   //current zipCode VO
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setZipcode("1203")
	   ssdZipcodeVO.setSddEligibility(false)
	   
	   sessionBean.getLandingZipcodeVO >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   sddManagerMock.populateDataInVO(sessionBean, requestMock, "1203", false, false, true) >> null
	   sddManagerMock.checkForSDDEligibility(requestMock, orderMock, null, _, "1203") >>  "ttttt"
	   
		  
	   1 * sgManagerMock.calculateShippingCost(_, orderMock, [hgdsMock])
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "ttttt")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
   }*/
   
   
   def "service .  when chooseSddOption is false, zipCode is blanck and CurrentZipcodeVO is null  "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>
	   requestMock.getParameter("currentZip") >> ""
	  requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   //current zipCode VO
	   sessionBean.getCurrentZipcodeVO() >> null
		  
	   sgManagerMock.calculateShippingCost(_, orderMock, [hgdsMock],"07081")
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0 * sddManagerMock.populateDataInVO(sessionBean, requestMock, _ , false, false, true)
   }
   
   
   def "service . TC  when chooseSddOption is false and shippingZip is 'registry zip'   "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   gssmDropletMock = Spy()
	   gssmDropletMock.setSameDayDeliveryManager(sddManagerMock)
	   gssmDropletMock.setShippingGroupManager(sgManagerMock)

	   
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>false
	   requestMock.getParameter("currentZip") >> "registryZip"
	   requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   gssmDropletMock.isDisplaySSD(null,sessionBean,false) >> true
	   
	   //current zipCode VO
/*	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setZipcode("1203")
	   ssdZipcodeVO.setSddEligibility(true)
*/	   
	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)	
	   	  
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "addressIneligible")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   1 * gssmDropletMock.logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
	   
   }
   
   def "service . TC  when chooseSddOption is false , shippingZip is 'registry zip' and sessionBean  is null  "(){
	   given:
	   gssmDropletMock = Spy()
	   gssmDropletMock.setSameDayDeliveryManager(sddManagerMock)
	   gssmDropletMock.setShippingGroupManager(sgManagerMock)

	   
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> null
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>false
	   requestMock.getParameter("currentZip") >> "registryZip"
	   requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   gssmDropletMock.isDisplaySSD(null,sessionBean,false) >> true
	   
	   //current zipCode VO
/*	   sessionBean.getCurrentZipcodeVO() >> null
	   
	   sessionBean.getLandingZipcodeVO() >> null
*/			 
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "addressIneligible")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   0 * gssmDropletMock.logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
   }
   
   def "service . TC  when chooseSddOption is false , shippingZip is 'registry zip' session bean is not null  "(){
	   given:
	   gssmDropletMock = Spy()
	   gssmDropletMock.setSameDayDeliveryManager(sddManagerMock)
	   gssmDropletMock.setShippingGroupManager(sgManagerMock)

	   
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>false
	   requestMock.getParameter("currentZip") >> "registryZip"
	   requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   gssmDropletMock.isDisplaySSD(null,sessionBean,false) >> false
	   
	   //current zipCode VO
/*	   sessionBean.getCurrentZipcodeVO() >> null
	   
	   sessionBean.getLandingZipcodeVO() >> null
*/			 
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "addressIneligible")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   0 * gssmDropletMock.logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
 
   }
   
   def "service . TC  when chooseSddOption is false , shippingZip is not  'registry zip' "(){
	   given:
	   gssmDropletMock = Spy()
	   gssmDropletMock.setSameDayDeliveryManager(sddManagerMock)
	   gssmDropletMock.setShippingGroupManager(sgManagerMock)

	   
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>false
	   requestMock.getParameter("currentZip") >> "notregistryZip"
	   requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   1*sddManagerMock.populateDataInVO(sessionBean, requestMock, "notregistryZip", false, false, true) >> regionVO
	   1*sddManagerMock.checkForSDDEligibility(requestMock, orderMock, regionVO, _, "notregistryZip") >>  "itemEligible"
	   gssmDropletMock.isDisplaySSD(regionVO,sessionBean,true) >> true
	   

	   //current zipCode VO
/*	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setZipcode("1203")
	   ssdZipcodeVO.setSddEligibility(true)

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
*/	   
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   1 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "itemEligible")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   1 * requestMock.setParameter("regionVO", regionVO)
	   1 * gssmDropletMock.logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
   }
   
   def "service . TC  when chooseSddOption is false , shippingZip is not  'registry zip' , regionVO is null "(){
	   given:
	   gssmDropletMock = Spy()
	   gssmDropletMock.setSameDayDeliveryManager(sddManagerMock)
	   gssmDropletMock.setShippingGroupManager(sgManagerMock)

	   
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>false
	   requestMock.getParameter("currentZip") >> "notregistryZip"
	   requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {}
	   sgManagerMock.getHardgoodShippingGroups(orderMock) >> [hgdsMock]
	   1 * sddManagerMock.populateDataInVO(sessionBean, requestMock, "notregistryZip", false, false, true) >> null
	   1 * sddManagerMock.checkForSDDEligibility(requestMock, orderMock, null, _, "notregistryZip") >>  "nonitemEligible"
	   gssmDropletMock.isDisplaySSD(regionVO,sessionBean,true) >> false
	   
   
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", true)
	   1 * requestMock.setParameter("sddEligiblityStatus", "nonitemEligible")
	   1 * requestMock.setParameter("shipMethodVOList", _)
	   1 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0 * requestMock.setParameter("regionVO", regionVO)
	   0 * gssmDropletMock.logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
	   
   }
   
   /////////////////////////////////  isDisplaySSD //////////////
   def "isDisplaySSD. when includeRegionVO is true" (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(true)

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(regionVO, sessionBean, true)
	   
	   then:
	   value == true
   }
   
   
   
   def "isDisplaySSD. when includeRegionVO is true and sddEligibility of landingzipcode is false" (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(true)

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(false)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(regionVO, sessionBean, true)
	   
	   then:
	   value == true
   }
   
   def "isDisplaySSD. when includeRegionVO is true and landingZipCodeVO is null " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(true)

	   sessionBean.getLandingZipcodeVO() >> null
	   landingdZipcodeVO.setSddEligibility(false)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(regionVO, sessionBean, true)
	   
	   then:
	   value == true
   }
   
   def "isDisplaySSD. when includeRegionVO is true and sddEligibility of currentzip code is false " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(false)

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(regionVO, sessionBean, true)
	   
	   then:
	   value == true
   }
   
   def "isDisplaySSD. when includeRegionVO is true and currentzip code VO is null " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> null
	   ssdZipcodeVO.setSddEligibility(false)
	   

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(regionVO, sessionBean, true)
	   
	   then:
	   value == true
   }
   
   def "isDisplaySSD. when includeRegionVO is true and regionVO is null and SddEligibility is flase " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(false)
	   
	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(false)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, true)
	   
	   then:
	   value == false
   }
   
   def "isDisplaySSD. when includeRegionVO is true andregionVO is null  " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(true)
	   
	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, true)
	   
	   then:
	   value == true
   }
   def "isDisplaySSD. when includeRegionVO is true andr SddEligibility of currentZipcode is false " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(false)
	   
	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, true)
	   
	   then:
	   value == true
   }
   
  ///////////////////// include reasone is false 
   
   def "isDisplaySSD. when includeRegionVO is false" (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(true)

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, false)
	   
	   then:
	   value == true
   }
   
   
   
   def "isDisplaySSD. when includeRegionVO is false and sddEligibility of landingzipcode is false" (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(true)

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(false)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, false)
	   
	   then:
	   value == true
   }
   
   def "isDisplaySSD. when includeRegionVO is false and landingZipCodeVO is null " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(true)

	   sessionBean.getLandingZipcodeVO() >> null
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, false)
	   
	   then:
	   value == true
   }
   
   def "isDisplaySSD. when includeRegionVO is false and landingZipCodeVO is null and  " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(false)

	   sessionBean.getLandingZipcodeVO() >> null
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, false)
	   
	   then:
	   value == false
   }
   
   def "isDisplaySSD. when includeRegionVO is false and sddEligibility of currentzip code is false " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(false)

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, false)
	   
	   then:
	   value == true
   }
   
   def "isDisplaySSD. when includeRegionVO is false and currentzip code VO is null " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> null
	   ssdZipcodeVO.setSddEligibility(false)
	   

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(true)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, false)
	   
	   then:
	   value == true
   }
   
   def "isDisplaySSD. when includeRegionVO is false and SddEligibility is false  " (){
	   given:
	   sessionBean.getCurrentZipcodeVO() >> ssdZipcodeVO
	   ssdZipcodeVO.setSddEligibility(false)
	   

	   sessionBean.getLandingZipcodeVO() >> landingdZipcodeVO
	   landingdZipcodeVO.setSddEligibility(false)
	   
	   when:
	   boolean value = gssmDropletMock.isDisplaySSD(null, sessionBean, false)
	   
	   then:
	   value == false
   }
   

 /////////////////////////////////// exception scenario //////////////////////
   def "service . TC while geting updating list of sdd shipping method it's throws  BBBSystemException  "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>false
	   requestMock.getParameter("currentZip") >> "notregistryZip"
	   requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {throw new BBBSystemException("excveption") }
	   //current zipCode VO
	   
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", _)
	   0 * requestMock.setParameter("sddEligiblityStatus", _)
	   0 * requestMock.setParameter("shipMethodVOList", _)
	   0 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0 * requestMock.setParameter("regionVO", _)
   }
   
   def "service . TC while geting updating list of sdd shipping method it's throws  BBBBusinessException  "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>false
	   requestMock.getParameter("currentZip") >> "notregistryZip"
	   requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {throw new BBBBusinessException("excveption") }
	   //current zipCode VO
	   
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", _)
	   0 * requestMock.setParameter("sddEligiblityStatus", _)
	   0 * requestMock.setParameter("shipMethodVOList", _)
	   0 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0 * requestMock.setParameter("regionVO", _)
   }
   
   def "service . TC while geting updating list of sdd shipping method it's throws  RepositoryException  "(){
	   given:
	   requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean
	   requestMock.getObjectParameter("order") >> orderMock
	   requestMock.getParameter("chooseSddOption") >>false
	   requestMock.getParameter("currentZip") >> "notregistryZip"
	   requestMock.getSession() >> httpSessionMock
	   sddManagerMock.getBbbCatalogTools() >> catalogToolsMock
	   
	   catalogToolsMock.updateListWithSddShipMethod(_) >> {throw new RepositoryException("excveption") }
	   //current zipCode VO
	   
	   when:
	   gssmDropletMock.service(requestMock, responseMock)
	   then:
	   0 * requestMock.setParameter("sddOptionEnabled", _)
	   0 * requestMock.setParameter("sddEligiblityStatus", _)
	   0 * requestMock.setParameter("shipMethodVOList", _)
	   0 * requestMock.serviceParameter("output", requestMock, responseMock)
	   0 * requestMock.setParameter("regionVO", _)
   }
     
   
}
