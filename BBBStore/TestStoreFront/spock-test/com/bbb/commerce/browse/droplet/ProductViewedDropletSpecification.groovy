package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.BBBSearchBrowseConstants
import com.bbb.profile.session.BBBSessionBean;

import spock.lang.specification.BBBExtendedSpec

class ProductViewedDropletSpecification extends BBBExtendedSpec {

	ProductViewedDroplet droplet = new ProductViewedDroplet()
	
	def setup(){
		droplet.setLimitNoOfLV(2)
	}
	
	def "service method, Happy path"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			Map<String,List<String>> map = new HashMap<String,List<String>>()
			map.put("productIdList",["prdId","prdId1","prdId2","prdId3"])
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			sessionBean.getValues() >> map
		
		when:
			droplet.service(requestMock, responseMock)
			
		then:
			sessionBean.getValues()!=null
			sessionBean.getValues().get("productIdList")!=null
			List<String> list = sessionBean.getValues().get("productIdList")
			list.size() == 2
	}
	
	def "service method, limit set as 10"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			Map<String,List<String>> map = new HashMap<String,List<String>>()
			map.put("productIdList",["prdId1","prdId2","prdId3"])
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			sessionBean.getValues() >> map
			droplet.setLimitNoOfLV(10)
		
		when:
			droplet.service(requestMock, responseMock)
			
		then:
			sessionBean.getValues()!=null
			sessionBean.getValues().get("productIdList")!=null
			List<String> list = sessionBean.getValues().get("productIdList")
			list.size() == 4
	}
	
	def "service method, sessionMap is passed as empty"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			Map<String,List<String>> map = new HashMap<String,List<String>>()
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			sessionBean.getValues() >> map
		
		when:
			droplet.service(requestMock, responseMock)
			
		then:
			sessionBean.getValues()!=null
			sessionBean.getValues().get("productIdList")!=null
			List<String> list = sessionBean.getValues().get("productIdList")
			list.size() == 1
	}
	
	def "service method, productId is null"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			Map<String,List<String>> map = new HashMap<String,List<String>>()
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			sessionBean.getValues() >> map
		
		when:
			droplet.service(requestMock, responseMock)
			
		then:
			sessionBean.getValues() !=null
			sessionBean.getValues().get("productIdList") ==null
	}
}
