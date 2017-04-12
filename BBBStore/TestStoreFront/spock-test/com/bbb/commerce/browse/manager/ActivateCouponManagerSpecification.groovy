package com.bbb.commerce.browse.manager

import com.bbb.account.validatecoupon.ActivateCouponRequestVO;
import com.bbb.account.validatecoupon.ActivateCouponResponseVO
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.constants.BBBCmsConstants
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.utils.BBBConfigRepoUtils;

import spock.lang.specification.BBBExtendedSpec

class ActivateCouponManagerSpecification extends BBBExtendedSpec {
	
	def ActivateCouponManager couponManager
	BBBCatalogToolsImpl catalogMock =Mock()
	
	def "ActivateCouponManager(), Happy path"(){
		
		given:
			 ActivateCouponRequestVO reqVO = Mock()
			 ActivateCouponResponseVO resVO = Mock()
			couponManager= Spy()
			BBBConfigRepoUtils.setBbbCatalogTools(catalogMock)
			catalogMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCmsConstants.BIG_BLUE_TOKEN) >> ["true"]
			1*couponManager.invokeServiceHandlerUtil(reqVO) >> resVO
		
		when:
			resVO = couponManager.activateBigBlue(reqVO)
			
		then:
			resVO != null
	}
	
	def "ActivateCouponManager(), reqVO passed as null and bigBlueToken as empty string"(){
		
		given:
			def ActivateCouponRequestVO reqVO = Mock()
			def ActivateCouponResponseVO resVO = Mock()
			couponManager= Spy()
		when:
			resVO = couponManager.activateBigBlue(null)
			
		then:
			resVO == null
	}

}
