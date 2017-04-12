package com.bbb.commerce.browse.droplet

import java.util.Set;
import javax.servlet.ServletException

import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.browse.vo.SddZipcodeVO
import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.RegionVO
import com.bbb.commerce.catalog.vo.SDDResponseVO;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean
import com.bbb.selfservice.manager.SearchStoreManager
import com.bbb.selfservice.tools.StoreTools

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class BBBDisplaySDDEligibilityOnPDPDropletSpecification extends BBBExtendedSpec {
	
	BBBDisplaySDDEligibilityOnPDPDroplet testObj
	SearchStoreManager searchStoreManagerMock = Mock()
	BBBInventoryManager inventoryManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	BBBCatalogToolsImpl bbbCatalogToolsImplMock = Mock()
	StoreTools storeToolsMock = Mock()
	BBBSameDayDeliveryManager sameDayDeliveryManagerMock = Mock()
	RegionVO regionVOMock = Mock()
	MutableRepository mutableRepositoryMock = Mock()
	RepositoryItem repositoryItemMock = Mock()
	
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	public final static String AVAIABLE_STATUS = "availableStatus";
	public final static String MESSAGE = "message";
	public final static String STR_ZIP_CODE = "strZipCode";
	public final static String STR_ORDER_BY = "strOrderBy";
	public final static String STR_GET_BY = "strGetBy";
	public final static String STR_LBL_SDD_AVAIL = "txt_sameday_delivery_available";
	public final static String STR_LBL_SDD_UNAVAIL = "txt_sameday_delivery_unavailable";
	public final static String STR_LBL_SDD_INELIGIBLE = "txt_sameday_delivery_ineligible";
	public final static String ZIP_CODE = "zipCode";
	public final static String SDD_AJAX = "sddFromAjax";
	public final static String SDD_ELIGIBLE = "sddEligibility";
	public final static String SDD_ATTRIBS = "sddAttribs";
	public final static String SDD_INSTOCK = "inStock";
	public final String SDD_SITE_ID = "siteId";
	public final String SDD_SITE_CAT_REF = "catalogRefId";
	public final String SDD_ZIP_CODE = "zipCode";
	public final String SDD_SKU_ID = "skuId";
	public final String SDD_IS_AJAX = "sddFromAjax";
	public final String SDD_AVAILABLE_STATUS = "availableStatus";
	public final String SDD_MESSAGE = "message";
	public final String SDD_REGION_VO = "regionVO";
	
	def setup(){
		testObj = new BBBDisplaySDDEligibilityOnPDPDroplet(inventoryManager:inventoryManagerMock,bbbCatalogTools:bbbCatalogToolsImplMock,
			searchStoreManager:searchStoreManagerMock,catalogTools:catalogToolsMock,lblTxtTemplateManager:lblTxtTemplateManagerMock,
			storeTools:storeToolsMock,sameDayDeliveryManager:sameDayDeliveryManagerMock)
		
		bbbCatalogToolsImplMock.getCatalogRepository() >> mutableRepositoryMock
		
		
		
	}
	
	/////////////////////////////////TCs for service starts////////////////////////////////////////
	//Signature : public void service(final DynamoHttpServletRequest req, DynamoHttpServletResponse res) ////
	
	def"service method.This TC is when fromAjax is true and Instock is true"(){
		given:
			testObj.setLoggingDebug(TRUE)
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getParameter(ZIP_CODE) >> "70002"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "70002",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock 
			RegionVO regionVOMock1 = Mock()
			1 * bbbCatalogToolsImplMock.getStoreIdsFromRegion("r12345") >>  regionVOMock1
			Set<String> sddStoreIdList = ["12325","22332"]
			1 * regionVOMock1.getStoreIds() >> sddStoreIdList
			1 * searchStoreManagerMock.getStoreWithInventoryByStoreId(_,requestMock, responseMock,_) >> "56552"
			1 * mutableRepositoryMock.getItem("sdd12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> TRUE
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_AVAIL, ["strZipCode":"70002","strOrderBy":"6.30PM","strGetBy":"9.30AM"]) >> "Your Product is Available"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sddZipcodeVOMock.getStoreId().equals("56552")
			1 * requestMock.setParameter(AVAIABLE_STATUS, "available")
			1 * requestMock.setParameter(MESSAGE, "Your Product is Available")
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
	}
	
	def"service method.This TC is when SDD_SKU_ID is null and channel is MobileApp"(){
		given:
			testObj.setLoggingDebug(TRUE)
			requestMock.getParameter(SDD_INSTOCK) >> null
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> null
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			1 * requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * catalogToolsMock.hasSDDAttribute(_,_, "promo12345", true) >> FALSE
			
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_INELIGIBLE, ["strZipCode":"7000"]) >> "Your Product is Ineligible"

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sddZipcodeVOMock.getStoreId().equals("")
			1 * requestMock.setParameter(AVAIABLE_STATUS, "ineligible")
			1 * requestMock.setParameter(MESSAGE, "Your Product is Ineligible")
	}
	
	def"service method.This TC is when getCurrentZipcodeVO() is null and inputZip is empty"(){
		given:
			testObj.setLoggingDebug(TRUE)
			requestMock.getParameter(SDD_INSTOCK) >> null
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> null
			requestMock.getParameter(ZIP_CODE) >> ""
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:null,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
			
	}
	
	def"service method.This TC is when sessionBean is null and fromAjax is false"(){
		given:
			testObj.setLoggingDebug(TRUE)
			requestMock.getParameter(SDD_INSTOCK) >> null
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> null
			requestMock.getParameter(ZIP_CODE) >> ""
			requestMock.getParameter(SDD_AJAX) >> "false"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:null)
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> null

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
	}
	
	def"service method.This TC is when SDD_SKU_ID is empty and channel is MobileWeb"(){
		given:
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> ""
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			RegionVO regionVOMock1 = Mock()
			1 * bbbCatalogToolsImplMock.getStoreIdsFromRegion("r12345") >>  regionVOMock1
			Set<String> sddStoreIdList = ["12325","22332"]
			1 * regionVOMock1.getStoreIds() >> sddStoreIdList
			1 * mutableRepositoryMock.getItem("SKU12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> TRUE
			
			1 * searchStoreManagerMock.getStoreWithInventoryByStoreId(_,requestMock, responseMock,_) >> ""
			
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_UNAVAIL, ["strZipCode":"7000"]) >> "Your Product is UnAvailable"

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sddZipcodeVOMock.getStoreId().equals("")
			1 * requestMock.setParameter(AVAIABLE_STATUS, "unavailable")
			1 * requestMock.setParameter(MESSAGE, "Your Product is UnAvailable")
	}
	
	
	
	def"service method.This TC is when isSddEligibility is false"(){
		given:
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> ""
			requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:FALSE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_INELIGIBLE, ["strZipCode":"7000"]) >> "Your Product is Ineligible"

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "ineligible")
			1 * requestMock.setParameter(MESSAGE, "Your Product is Ineligible")
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)		
	}
	
	def "service method.This TC is when user lands to MSWP PDP in non-pilot market"(){
		given:
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:FALSE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea("txt_product_sdd_market_ineligible", ["strZipCode":"7000"]) >> "Your market is Ineligible"

		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "product_sdd_market_ineligible")
			1 * requestMock.setParameter(MESSAGE, "Your market is Ineligible")
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
	}
	
	def "service method.This TC is when user lands to SDD eligible MSWP PDP in pilot market"(){
		given:
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter("productId") >> "product12345"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("product12345", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> TRUE
			1 * lblTxtTemplateManagerMock.getPageTextArea("txt_product_sdd_eligible", ["strZipCode":"7000"]) >> "Your MSWP product is SDD Eligible"

		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "product_sdd_eligible")
			1 * requestMock.setParameter(MESSAGE, "Your MSWP product is SDD Eligible")
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
	}
	
	def "service method.This TC is when user lands to SDD ineligible MSWP PDP in pilot market"(){
		given:
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter("productId") >> "product12345"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("product12345", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> FALSE
			1 * lblTxtTemplateManagerMock.getPageTextArea("txt_product_sdd_ineligible", ["strZipCode":"7000"]) >> "Your MSWP product is SDD Ineligible"

		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "product_sdd_ineligible")
			1 * requestMock.setParameter(MESSAGE, "Your MSWP product is SDD Ineligible")
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
	}
	
	
	def"service method.This TC is when BBBSystemException thrown in hasSDDAttribute"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> FALSE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getParameter(ZIP_CODE) >> "70002"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "70002",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("sdd12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			1 * testObj.setSddAvailabilityStatus(requestMock, BBBCoreConstants.SDD_ITEM_INELIGIBLE, "70002", "", "") >> null
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
			1 * testObj.vlogError(_,
					"BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching isHasSddAttribute from skuDetailVO ")
	}
	
	def"service method.This TC is when BBBBusinessException thrown in hasSDDAttribute"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> FALSE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getParameter(ZIP_CODE) >> "70002"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "70002",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("sdd12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			1 * testObj.setSddAvailabilityStatus(requestMock, BBBCoreConstants.SDD_ITEM_INELIGIBLE, "70002", "", "") >> null
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
			1 * testObj.vlogError(_,
					"BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching isHasSddAttribute from skuDetailVO ")
	}
	
	def"service method.This TC is when RepositoryException thrown in getItem"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> FALSE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getParameter(ZIP_CODE) >> "70002"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "70002",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("sdd12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "ineligible")
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
			1 * testObj.vlogError(_,
					"BBBDisplaySDDEligibilityOnPDPDroplet| RepositoryException occurred while fetching SKU item from SKU id.")
	}
	
	def"service method.This TC is when BBBBusinessException thrown in getStoreWithInventoryByStoreId"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> ""
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("SKU12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> TRUE
			RegionVO regionVOMock1 = Mock()
			1 * bbbCatalogToolsImplMock.getStoreIdsFromRegion("r12345") >>  regionVOMock1
			Set<String> sddStoreIdList = ["12325"]
			1 * regionVOMock1.getStoreIds() >> sddStoreIdList
			1 * searchStoreManagerMock.getStoreWithInventoryByStoreId(_,requestMock, responseMock,_) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_UNAVAIL, ["strZipCode":"7000"]) >> "Your Product is UnAvailable"
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "unavailable")
			1 * requestMock.setParameter(MESSAGE, "Your Product is UnAvailable")
			1 * testObj.logError('BBBDisplaySDDEligibilityOnPDPDroplet| BBBBusinessException or BBBSystemException occurred whilegetStoreWithInventoryByStoreId ', _)
	}
	
	def"service method.This TC is when BBBSystemException thrown in getStoreWithInventoryByStoreId"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> ""
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("SKU12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> TRUE
			RegionVO regionVOMock1 = Mock()
			1 * bbbCatalogToolsImplMock.getStoreIdsFromRegion("r12345") >>  regionVOMock1
			Set<String> sddStoreIdList = ["12325"]
			1 * regionVOMock1.getStoreIds() >> sddStoreIdList
			1 * searchStoreManagerMock.getStoreWithInventoryByStoreId(_,requestMock, responseMock,_) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_UNAVAIL, ["strZipCode":"7000"]) >> "Your Product is UnAvailable"

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "unavailable")
			1 * requestMock.setParameter(MESSAGE, "Your Product is UnAvailable")
			1 * testObj.logError('BBBDisplaySDDEligibilityOnPDPDroplet| BBBBusinessException or BBBSystemException occurred whilegetStoreWithInventoryByStoreId ', _)
	}
	
	def"service method.This TC is when RepositoryException thrown in getStoreWithInventoryByStoreId"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> ""
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("SKU12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> TRUE
			RegionVO regionVOMock1 = Mock()
			1 * bbbCatalogToolsImplMock.getStoreIdsFromRegion("r12345") >>  regionVOMock1
			Set<String> sddStoreIdList = ["12325"]
			1 * regionVOMock1.getStoreIds() >> sddStoreIdList
			1 * searchStoreManagerMock.getStoreWithInventoryByStoreId(_,requestMock, responseMock,_) >> {throw new RepositoryException("Mock for RepositoryException")}
			
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_UNAVAIL, ["strZipCode":"7000"]) >> "Your Product is UnAvailable"

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "unavailable")
			1 * requestMock.setParameter(MESSAGE, "Your Product is UnAvailable")
			1 * testObj.logError('BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching inventory from Repository ', _)
	}
	
	def"service method.This TC is when fromAjax is false and BBBBusinessException thrown in getStoreWithInventoryByStoreId"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "false"
			requestMock.getParameter(SDD_ATTRIBS) >> "true"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * searchStoreManagerMock.getStoreWithInventoryByStoreId(_,requestMock, responseMock,_) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_UNAVAIL, ["strZipCode":"70002"]) >> "Your Product is UnAvailable"
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "unavailable")
			1 * requestMock.setParameter(MESSAGE, "Your Product is UnAvailable")
			1 * testObj.logError('BBBDisplaySDDEligibilityOnPDPDroplet| BBBBusinessException or BBBSystemException occurred whilegetStoreWithInventoryByStoreId Mock for BBBBusinessException', _)
	}
	
	def"service method.This TC is when fromAjax is false and BBBSystemException thrown in getStoreWithInventoryByStoreId"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "false"
			requestMock.getParameter(SDD_ATTRIBS) >> "true"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * searchStoreManagerMock.getStoreWithInventoryByStoreId(_,requestMock, responseMock,_) >>  {throw new BBBSystemException("Mock for BBBSystemException")}
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_UNAVAIL, ["strZipCode":"70002"]) >> "Your Product is UnAvailable"
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "unavailable")
			1 * requestMock.setParameter(MESSAGE, "Your Product is UnAvailable")
			1 * testObj.logError('BBBDisplaySDDEligibilityOnPDPDroplet| BBBBusinessException or BBBSystemException occurred whilegetStoreWithInventoryByStoreId Mock for BBBSystemException', _)
	}
	
	def"service method.This TC is when fromAjax is false and RepositoryException thrown in getStoreWithInventoryByStoreId"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.setAttribute("regionVO", regionVOMock)
			requestMock.getParameter(ZIP_CODE) >> "7000-28"
			requestMock.getParameter(SDD_AJAX) >> "false"
			requestMock.getParameter(SDD_ATTRIBS) >> "true"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "7000",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * searchStoreManagerMock.getStoreWithInventoryByStoreId(_,requestMock, responseMock,_) >>  {throw new RepositoryException("Mock for RepositoryException")}
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_UNAVAIL, ["strZipCode":"70002"]) >> "Your Product is UnAvailable"
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "unavailable")
			1 * requestMock.setParameter(MESSAGE, "Your Product is UnAvailable")
			1 * testObj.logError('BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching inventory from Repository Mock for RepositoryException', _)
	}
	
	def"service method.This TC is when BBBBusinessException thrown in getStoreIdsFromRegion"(){
		given:
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getParameter(ZIP_CODE) >> "70002"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "70002",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("sdd12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> TRUE
			1 * bbbCatalogToolsImplMock.getStoreIdsFromRegion("r12345") >>  {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_INELIGIBLE, ["strZipCode":"70002"]) >> "Your Product is InEligible"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "ineligible")
			1 * requestMock.setParameter(MESSAGE, "Your Product is InEligible")
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)		
	}
	
	def"service method.This TC is when BBBSystemException thrown in getStoreIdsFromRegion"(){
		given:
			this.spyForService()
			requestMock.getParameter(SDD_INSTOCK) >> TRUE
			requestMock.getParameter(BBBCoreConstants.SKU_ID) >> "SKU12345"
			requestMock.getParameter(SDD_SKU_ID) >> "sdd12345"
			requestMock.getParameter(ZIP_CODE) >> "70002"
			requestMock.getParameter(SDD_AJAX) >> "true"
			requestMock.getParameter(SDD_ATTRIBS) >> "sddAttributes"
			
			//getZipCodeFromSession Private method Coverage
			SddZipcodeVO sddZipcodeVOMock = new SddZipcodeVO(zipcode:"70002",sddEligibility:TRUE,promoAttId:"promo12345",regionId:"r12345",displayCutoffTime:"6.30PM",displayGetByTime:"9.30AM")
			
			BBBSessionBean sessionBeanMock = new BBBSessionBean(currentZipcodeVO:sddZipcodeVOMock,mSddStoreId:"sdd")
			1 * requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			1 * sameDayDeliveryManagerMock.populateDataInVO(sessionBeanMock, requestMock, "70002",BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE,BBBCoreConstants.RETURN_FALSE) >> regionVOMock
			1 * mutableRepositoryMock.getItem("sdd12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * catalogToolsMock.hasSDDAttribute(_, repositoryItemMock, "promo12345", true) >> TRUE
			1 * bbbCatalogToolsImplMock.getStoreIdsFromRegion("r12345") >>  {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//setSddAvailabilityStatus Public Method Coverage
			1 * lblTxtTemplateManagerMock.getPageTextArea(STR_LBL_SDD_INELIGIBLE, ["strZipCode":"70002"]) >> "Your Product is InEligible"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(AVAIABLE_STATUS, "ineligible")
			1 * requestMock.setParameter(MESSAGE, "Your Product is InEligible")
			1 * requestMock.serviceParameter(AVAIABLE_STATUS, requestMock, responseMock)
			1 * requestMock.serviceParameter(MESSAGE, requestMock, responseMock)
	}

	private spyForService() {
		testObj = Spy()
		testObj.setLoggingDebug(TRUE)
		testObj.setSameDayDeliveryManager(sameDayDeliveryManagerMock)
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsImplMock)
		testObj.setSearchStoreManager(searchStoreManagerMock)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
	}
	
	/////////////////////////////////TCs for service ends////////////////////////////////////////
	
	/////////////////////////////////TCs for getSDDEligibilityForREST starts////////////////////////////////////////
	//Signature : public SDDResponseVO getSDDEligibilityForREST(String customerZip,	String skuId, String isAjax, String sddAttribs, String inStock) ////

	def"getSDDEligibilityForREST method. This TC is the Happy flow of getSDDEligibilityForREST method"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			String customerZip = "20001" 
			String skuId = "sku12345"
			String productId = "product12345"
			String isAjax = "true"
			String sddAttribs = "sddAttribs"
			String inStock = "true"
			1 * testObj.service(requestMock,responseMock) >> {}
			requestMock.getParameter(SDD_AVAILABLE_STATUS) >> "Available"
			requestMock.getParameter(SDD_MESSAGE) >> "sdd_message"
			requestMock.getAttribute(SDD_REGION_VO) >> regionVOMock
		 
		when:
			SDDResponseVO sddResponseVOResults = testObj.getSDDEligibilityForREST(customerZip, skuId, productId, isAjax, sddAttribs, inStock)
			
		then:
			sddResponseVOResults.getAvailableStatus() == "Available"
			sddResponseVOResults.getDisplayMessage() == "sdd_message"
			sddResponseVOResults.getRegionVO() == regionVOMock
			1 * requestMock.setParameter(SDD_SITE_CAT_REF, skuId)
			1 * requestMock.setParameter(SDD_ZIP_CODE, customerZip)
			1 * requestMock.setParameter(SDD_SKU_ID, skuId)
			1 * requestMock.setParameter(SDD_IS_AJAX, isAjax)
			1 * requestMock.setParameter(SDD_ATTRIBS, sddAttribs)
			1 * requestMock.setParameter(SDD_INSTOCK, inStock)
			1 * testObj.logDebug('BBBDisplaySDDEligibilityOnPDPDroplet| getSDDEligibilityForREST params 20001|skuId-sku12345-|isAjaxtrue-|sddAttribssddAttribs-|inStocktrue')
			1 * testObj.logDebug('BBBDisplaySDDEligibilityOnPDPDroplet| getSDDEligibilityForREST ends')
	}
	
	def"getSDDEligibilityForREST method. This TC is when ServletException thrown"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(FALSE)
			String customerZip = "20001"
			String skuId = "sku12345"
			String productId = "product12345"
			String isAjax = "true"
			String sddAttribs = "sddAttribs"
			String inStock = "true"
			1 * testObj.service(requestMock,responseMock) >> {throw new ServletException("Mock for ServletException")}
		 
		when:
			SDDResponseVO sddResponseVOResults = testObj.getSDDEligibilityForREST(customerZip, skuId, productId, isAjax, sddAttribs, inStock)
			
		then:
			sddResponseVOResults == null
			1 * testObj.logError('BBBDisplaySDDEligibilityOnPDPDroplet| getSDDEligibilityForREST() | Exception:Mock for ServletException')
			1 * requestMock.setParameter(SDD_SITE_CAT_REF, skuId)
			1 * requestMock.setParameter(SDD_ZIP_CODE, customerZip)
			1 * requestMock.setParameter(SDD_SKU_ID, skuId)
			1 * requestMock.setParameter(SDD_IS_AJAX, isAjax)
			1 * requestMock.setParameter(SDD_ATTRIBS, sddAttribs)
			1 * requestMock.setParameter(SDD_INSTOCK, inStock)
	}
	
	def"getSDDEligibilityForREST method. This TC is when IOException thrown"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(FALSE)
			String customerZip = "20001"
			String skuId = "sku12345"
			String productId = "product12345"
			String isAjax = "true"
			String sddAttribs = "sddAttribs"
			String inStock = "true"
			1 * testObj.service(requestMock,responseMock) >> {throw new IOException("Mock for IOException")}
		 
		when:
			SDDResponseVO sddResponseVOResults = testObj.getSDDEligibilityForREST(customerZip, skuId, productId, isAjax, sddAttribs, inStock)
			
		then:
			sddResponseVOResults == null
			1 * testObj.logError('BBBDisplaySDDEligibilityOnPDPDroplet| getSDDEligibilityForREST() | Exception:Mock for IOException')
			1 * requestMock.setParameter(SDD_SITE_CAT_REF, skuId)
			1 * requestMock.setParameter(SDD_ZIP_CODE, customerZip)
			1 * requestMock.setParameter(SDD_SKU_ID, skuId)
			1 * requestMock.setParameter(SDD_IS_AJAX, isAjax)
			1 * requestMock.setParameter(SDD_ATTRIBS, sddAttribs)
			1 * requestMock.setParameter(SDD_INSTOCK, inStock)
	}
	
	def"getSDDEligibilityForREST method. This TC is when loggingDebug is false"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(FALSE)
			String customerZip = "20001"
			String skuId = "sku12345"
			String productId = "product12345"
			String isAjax = "true"
			String sddAttribs = "sddAttribs"
			String inStock = "true"
			1 * testObj.service(requestMock,responseMock) >> {}
			requestMock.getParameter(SDD_AVAILABLE_STATUS) >> "Available"
			requestMock.getParameter(SDD_MESSAGE) >> "sdd_message"
			requestMock.getAttribute(SDD_REGION_VO) >> regionVOMock
		 
		when:
			SDDResponseVO sddResponseVOResults = testObj.getSDDEligibilityForREST(customerZip, skuId, productId, isAjax, sddAttribs, inStock)
			
		then:
			sddResponseVOResults.getAvailableStatus() == "Available"
			sddResponseVOResults.getDisplayMessage() == "sdd_message"
			sddResponseVOResults.getRegionVO() == regionVOMock
			1 * requestMock.setParameter(SDD_SITE_CAT_REF, skuId)
			1 * requestMock.setParameter(SDD_ZIP_CODE, customerZip)
			1 * requestMock.setParameter(SDD_SKU_ID, skuId)
			1 * requestMock.setParameter(SDD_IS_AJAX, isAjax)
			1 * requestMock.setParameter(SDD_ATTRIBS, sddAttribs)
			1 * requestMock.setParameter(SDD_INSTOCK, inStock)
	}
	
	/////////////////////////////////TCs for getSDDEligibilityForREST ends////////////////////////////////////////
	
}
