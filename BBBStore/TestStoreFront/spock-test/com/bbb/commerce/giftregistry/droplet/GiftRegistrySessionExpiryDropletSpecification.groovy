package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.constants.BBBWebServiceConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException



import atg.multisite.Site
import atg.multisite.SiteContext
import spock.lang.specification.BBBExtendedSpec

class GiftRegistrySessionExpiryDropletSpecification extends BBBExtendedSpec {

	GiftRegistrySessionExpiryDroplet droplet
	RegistrySearchVO mRegistrySearchVO
	BBBCatalogTools mCatalogTools
	SiteContext mSiteContext
	String mSearchRegistryServiceName
	
	def setup(){
		droplet = Spy()
		mCatalogTools = Mock()
		mSiteContext = Mock()
		mRegistrySearchVO = new RegistrySearchVO()
		mSearchRegistryServiceName = "serviceName"
		droplet.setCatalogTools(mCatalogTools)
		droplet.setSiteContext(mSiteContext)
		droplet.setRegistrySearchVO(mRegistrySearchVO)
		droplet.setSearchRegistryServiceName(mSearchRegistryServiceName)
	}
	
	def "service method happy path"(){
		
		given:
			droplet.getSiteId() >> "siteId"
			GiftRegSessionBean bean = Mock()
			1*requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> bean
			1*mCatalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "siteId") >> ["BBBCanada"]
			1*mCatalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			mRegistrySearchVO.setGiftGiver(true)
			mRegistrySearchVO.setFirstName("")
			mRegistrySearchVO.setLastName("")
			mRegistrySearchVO.setRegistryId("")
			mRegistrySearchVO.setSortSeq("")
			mRegistrySearchVO.setSortSeqOrder(BBBGiftRegistryConstants.DEFAULT_SORT_ORDER)
			mRegistrySearchVO.setProfileId(null)
			mRegistrySearchVO.setFilterRegistriesInProfile(false)
			mRegistrySearchVO.setSiteId("")
			mRegistrySearchVO.setUserToken("")
			mRegistrySearchVO.setServiceName("")
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logDebug(" GiftRegistrySessExpiry service - start")
			1*droplet.logDebug("GiftRegistrySessExpiry service - end")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
			mRegistrySearchVO.getSiteId().equals("BBBCanada")
	}
	
	def "service method with BBBSystemException thrown"(){
		
		given:
			droplet.setRegistrySearchVO(null)
			droplet.getSiteId() >> "siteId"
			GiftRegSessionBean bean = Mock()
			1*requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> bean
			1*mCatalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "siteId") >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*mCatalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
			1*droplet.logDebug(" GiftRegistrySessExpiry service - start")
			1*droplet.logError("Error while retrieving gift registryVO", _)
	}
	
	def "service method with BBBBusinessException thrown"(){
		
		given:
			droplet.getSiteId() >> "siteId"
			GiftRegSessionBean bean = Mock()
			1*requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> bean
			1*mCatalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "siteId") >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*mCatalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
			1*droplet.logDebug(" GiftRegistrySessExpiry service - start")
			1*droplet.logError("Error while retrieving gift registryVO", _)
	}
}
