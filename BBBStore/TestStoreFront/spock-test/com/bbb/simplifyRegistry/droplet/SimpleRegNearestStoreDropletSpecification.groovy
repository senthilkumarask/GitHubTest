package com.bbb.simplifyRegistry.droplet

import atg.multisite.Site
import atg.multisite.SiteContext
import atg.multisite.SiteContextManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.manager.SearchStoreManager
import spock.lang.specification.BBBExtendedSpec

class SimpleRegNearestStoreDropletSpecification extends BBBExtendedSpec {

	SimpleRegNearestStoreDroplet srbsDroplet
	SearchStoreManager sStoreManagerMock = Mock()
	BBBCatalogTools cToolsMock = Mock()
	
	def setup(){
		srbsDroplet = Spy()
		srbsDroplet.setSearchStoreManager(sStoreManagerMock)
		srbsDroplet.setCatalogTools(cToolsMock)
		srbsDroplet.setRadius("5")

		
	}
	
	def"service. TC for simple nearest store for  BedBathCanada"(){
		given:
			
			requestMock.getLocalParameter("origin") >> "12456"
			srbsDroplet.getSiteId() >> "BedBathCanada"
			1*sStoreManagerMock.getStoreType("BedBathCanada") >> "usStore"
			1*cToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> ["st12","st13"]
			
		when:
		
		srbsDroplet.service(requestMock, responseMock)
		
		then:
		
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE  , "usStore");
		1*sessionMock.setAttribute(BBBCoreConstants.TYPE, '2');
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "st12");
		1*sessionMock.setAttribute(BBBCoreConstants.STATUS, "location=12456");
		1*sessionMock.setAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME,"12456");
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
		1*requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,	responseMock);

	}
	
	def"service. TC for BBBSystemException for BedBathCanada  "(){
		given:
			
			requestMock.getLocalParameter("origin") >> ""
			1*srbsDroplet.getSiteId() >> "BedBathCanada"
			1*sStoreManagerMock.getStoreType("BedBathCanada") >> "usStore"
			1*cToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> {throw new BBBSystemException("exception")}
			
		when:
		
		srbsDroplet.service(requestMock, responseMock)
		
		then:
		
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE  , "usStore");
		1*sessionMock.setAttribute(BBBCoreConstants.TYPE, '3');
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "5");
		1*sessionMock.setAttribute(BBBCoreConstants.STATUS, "location=");
		1*sessionMock.setAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME,"");
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
		1*requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,	responseMock);

	}
	
	def"service. TC for simple nearest store for  BedBathUS"(){
		given:
			
			requestMock.getLocalParameter("origin") >> "12456"
			1*srbsDroplet.getSiteId() >> "BedBathUS"
			1*sStoreManagerMock.getStoreType("BedBathUS") >> "usStore"
			1*cToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_us") >> ["st12","st13"]
			
		when:
		
		srbsDroplet.service(requestMock, responseMock)
		
		then:
		
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE  , "usStore");
		1*sessionMock.setAttribute(BBBCoreConstants.TYPE, '2');
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "st12");
		1*sessionMock.setAttribute(BBBCoreConstants.STATUS, "location=12456");
		1*sessionMock.setAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME,"12456");
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
		1*requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,	responseMock);

	}
	
	def"service. TC for BBBSystemException for BedBathUS  "(){
		given:
			
			requestMock.getLocalParameter("origin") >> ""
			1*srbsDroplet.getSiteId() >> "BedBathUS"
			1*sStoreManagerMock.getStoreType("BedBathUS") >> "usStore"
			1*cToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_us") >> {throw new BBBSystemException("exception")}
			
		when:
		
		srbsDroplet.service(requestMock, responseMock)
		
		then:
		
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE  , "usStore");
		1*sessionMock.setAttribute(BBBCoreConstants.TYPE, '3');
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "5");
		1*sessionMock.setAttribute(BBBCoreConstants.STATUS, "location=");
		1*sessionMock.setAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME,"");
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
		1*requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,	responseMock);

	}
	
	def"service. TC for simple nearest store for  BuyBuyBaby"(){
		given:
			
			requestMock.getLocalParameter("origin") >> "12456"
			srbsDroplet.getSiteId() >> "BuyBuyBaby"
			1*sStoreManagerMock.getStoreType("BuyBuyBaby") >> "usStore"
			1*cToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_baby") >> ["st12","st13"]
			
		when:
		
		srbsDroplet.service(requestMock, responseMock)
		
		then:
		
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE  , "usStore");
		1*sessionMock.setAttribute(BBBCoreConstants.TYPE, '2');
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "st12");
		1*sessionMock.setAttribute(BBBCoreConstants.STATUS, "location=12456");
		1*sessionMock.setAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME,"12456");
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
		1*requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,	responseMock);

	}
	
	def"service. TC for BBBSystemException for BuyBuyBaby  "(){
		given:
			
			requestMock.getLocalParameter("origin") >> ""
			1*srbsDroplet.getSiteId() >> "BuyBuyBaby"
			1*sStoreManagerMock.getStoreType("BuyBuyBaby") >> "usStore"
			1*cToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_baby") >> {throw new BBBSystemException("exception")}
			
		when:
		
		srbsDroplet.service(requestMock, responseMock)
		
		then:
		
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE  , "usStore");
		1*sessionMock.setAttribute(BBBCoreConstants.TYPE, '3');
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "5");
		1*sessionMock.setAttribute(BBBCoreConstants.STATUS, "location=");
		1*sessionMock.setAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME,"");
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
		1*requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,	responseMock);

	}
	
	def"service. TC for BBBSystemException while gettign store type "(){
		given:
			
			requestMock.getLocalParameter("origin") >> ""
			1*srbsDroplet.getSiteId() >> "BuyBuyBaby"
			sStoreManagerMock.getStoreType("BuyBuyBaby") >> {throw new BBBSystemException("exception")}
			
		when:
		
		srbsDroplet.service(requestMock, responseMock)
		
		then:
		
		0*cToolsMock.getAllValuesForKey("MapQuestStoreType",_)
		0*requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,	responseMock)
		1*requestMock.serviceParameter("error", requestMock,	responseMock)

	}
	
	def"service. TC for BBBBusinessException while gettign store type "(){
		given:
			
			requestMock.getLocalParameter("origin") >> ""
			1*srbsDroplet.getSiteId() >> "BuyBuyBaby"
			sStoreManagerMock.getStoreType("BuyBuyBaby") >> {throw new BBBBusinessException("exception")}
			
		when:
		
		srbsDroplet.service(requestMock, responseMock)
		
		then:
		
		0*cToolsMock.getAllValuesForKey("MapQuestStoreType",_)
		0*requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,	responseMock)
		1*requestMock.serviceParameter("error", requestMock,	responseMock)

	}

}
