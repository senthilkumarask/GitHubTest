package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.category.IProdToutHelper;
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import atg.nucleus.ServiceMap
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class ProdToutDropletSpecification extends BBBExtendedSpec {
	
	def ServiceMap tabsToutHelperServiceMap = Mock()
	
	ProdToutDroplet droplet = new ProdToutDroplet()
	
	def setup(){
		droplet.setTabsToutHelperServiceMap(tabsToutHelperServiceMap)
	}
	
	def "service method, happy path"(){
		
		given:
			droplet = Spy()
			droplet.setTabsToutHelperServiceMap(tabsToutHelperServiceMap)
			def IProdToutHelper prodHelper = Mock()
			def IProdToutHelper prodHelper1 = Mock()
			def IProdToutHelper prodHelper2 = Mock()
			def IProdToutHelper prodHelper3 = Mock()
			def ProductVO vo = new ProductVO()
			vo.setProductId("prdId")
			requestMock.getParameter(ParameterName.getParameterName("tabList")) >> "tab1,tab2,tab3,tab4,tab5"
			requestMock.getParameter("id") >>"id"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> null
			droplet.extractCurrentSiteId() >> "siteId"
			droplet.tabsToutHelperServiceMap.get("tab1") >> prodHelper
			droplet.tabsToutHelperServiceMap.get("tab3") >> prodHelper1
			droplet.tabsToutHelperServiceMap.get("tab4") >> prodHelper2
			droplet.tabsToutHelperServiceMap.get("tab5") >> prodHelper3
			prodHelper.getProducts(_,_) >> [vo]
			prodHelper1.getProducts(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
			prodHelper2.getProducts(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			prodHelper3.getProducts(_,_) >> null
					
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			2*requestMock.setParameter("linkString",_)
			1*requestMock.serviceParameter("output", _, _)
			1 * droplet.logError("browse_1031: Business Exception from service of ProdToutDroplet ", _)
			1 * droplet.logError("browse_1030: System Exception from service of ProdToutDroplet ", _)
			
	}
	
	def "service method, site id is not null and tabsList is null"(){
		
		given:
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			0*requestMock.setParameter("linkString",_)
			1*requestMock.serviceParameter("output", _, _)
	}
}
