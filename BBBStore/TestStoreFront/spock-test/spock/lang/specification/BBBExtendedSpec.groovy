package spock.lang.specification;

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import spock.lang.Specification
import atg.commerce.states.OrderStates
import atg.commerce.states.StateDefinitions
import atg.servlet.DynamoHttpServletRequest
import atg.servlet.DynamoHttpServletResponse
import atg.servlet.ServletUtil

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.logger.PerformanceLogger
import com.bbb.framework.validation.BBBValidationRules
import com.bbb.utils.BBBUtility

public class BBBExtendedSpec extends Specification{
	
	def DynamoHttpServletRequest requestMock = Mock()
	def DynamoHttpServletResponse responseMock = Mock()
	def HttpServletResponse responseResponseMock = Mock()
	def PerformanceLogger perfMock = Mock()
	def HttpSession sessionMock = Mock()
	def numericOnlyPattern = /^[0-9]*$/
	def nonPOBoxAddressPattern = /(?i)([\\w\\s*\\W]*(P(OST)?\\.?\\s*((O(FF(ICE)?)?)?\\.?\\s*(B(IN|OX)?\\.?\\b))+))[\\w\\s*\\W]*/ 
	def alphaNumericPattern = /^[a-zA-Z0-9_]*\$/ 
	def crossSiteScriptingPattern = /^.*(<([a-zA-Z0-9]+).*>.*<\/([a-zA-Z0-9]+)>).*|(<([a-zA-Z0-9]+).*\/>)/
	def emailPattern = /^[_a-zA-Z0-9!#\$%&'\*\+\-\/=\?\^_`{\|}~\\[\\]\\(\\)]([._-]{0,1}[_a-zA-Z0-9!#\$%&'\*\+\-\/=\?\^_`{\|}~\\[\\]\\(\\)])*@[a-zA-Z0-9]([._-]{0,1}[a-zA-Z0-9])*\.[a-zA-Z]{2,}$/
	def phonePattern=/[0-9]{10,15}/
	def namePattern=/^[a-zA-Z]+[a-zA-Z\\-\\'\\ \\.]*$/
    def addressLine1Pattern=/^[^(),\"<>]+$/
	def addressLine2Pattern=/^[^(),\"]+$/
    def companyNamePattern = /.*{0,20}/
	def cityNamePattern=/^[a-zA-ZáàâäæãåèéêëîïíìôöòóøõûüùúÿñÁÀÂÄÆÃÅÈÉÊËÎÏÍÌÔÖÒÓØÕÛÜÙÚ\\.\\-\\']+[a-zA-Z0-9áàâäæãåèéêëîïíìôöòóøõûüùúÿñÁÀÂÄÆÃÅÈÉÊËÎÏÍÌÔÖÒÓØÕÛÜÙÚ\\.\\-\\'\\ ]*$/
	def zipCodePattern=/(^d{5}(-[0-9A-Za-z]{4,6})?$)|([ABCEGHJKLMNPRSTVXY]{1}\d{1}[A-Z]{1} *\d{1}[A-Z]{1}\d{1}$)/
	def giftMessagePattern=/^[1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,.:;"_''-+=\\\\$#&()@!?%^*~`<>|¡¿{}\\[\\]\\s[\\p{L}\\. \'-]]{0,}$/
	def registryNamePattern=/^[a-zA-Z0-9_\\+\\s\\/]*$/
	def statePattern=/^[A-Z]*$/
	BBBCatalogTools CatalogToolsMock = Mock()
	static {//Static block to add static initialization of OOTB classes like OrderHolder
		def orderState = new OrderStates()
		Properties properties = new Properties()
		properties.put("incomplete", "1")
		orderState.setStateValueMap(properties)
		//StateDefinitions.ORDERSTATES = orderState
	}
	BBBValidationRules bbbRules = new BBBValidationRules()
	def setup() {
		bbbRules.setCatalogTools(CatalogToolsMock)
		CatalogToolsMock.getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.COLLEGENAME_MAXLENGTH,BBBCoreConstants.TWENTY_NINE) >> "20"
		bbbRules.setCrossSiteScriptingPattern(crossSiteScriptingPattern)
		bbbRules.setEmailPattern(emailPattern)
		bbbRules.setAlphaNumericPattern(alphaNumericPattern)
		bbbRules.setNonPOBoxAddressPattern(nonPOBoxAddressPattern)
		bbbRules.setNumericOnlyPattern(numericOnlyPattern)
		bbbRules.setPhonePattern(phonePattern)
		bbbRules.setCompanyNamePattern(companyNamePattern)
		bbbRules.setCollegeNameMaxLength()
		bbbRules.setNamePattern(namePattern)
		bbbRules.setAddressLine1Pattern(addressLine1Pattern)
		bbbRules.setAddressLine2Pattern(addressLine2Pattern)
		bbbRules.setCityNamePattern(cityNamePattern)
		bbbRules.setZipCodePattern(zipCodePattern)
		bbbRules.setRegistryNamePattern(registryNamePattern)
		bbbRules.setStatePattern(statePattern)
		//need to verify having dought
		bbbRules.setGiftMessagePattern(addressLine2Pattern)
		BBBUtility.setRules(bbbRules)
		OrderStates orderStates = new OrderStates()
		Properties states = new Properties()
		states.put("incomplete","1000")
		states.put("submitted","2000")
		
		orderStates.setStateValueMap(states)
		StateDefinitions.ORDERSTATES = orderStates

		
	
		//Setting current request
		ServletUtil.setCurrentRequest(requestMock)
		ServletUtil.setCurrentResponse(responseMock)
		//Setting Mock Session
		requestMock.getSession() >> sessionMock
		//adding mock for performance logger and disabling it
		requestMock.resolveName("/com/bbb/framework/performance/logger/PerformanceLogger") >> perfMock
		perfMock.isEnableCustomComponentsMonitoring() >> false
		requestMock.getResponse() >> responseMock
	}

}
