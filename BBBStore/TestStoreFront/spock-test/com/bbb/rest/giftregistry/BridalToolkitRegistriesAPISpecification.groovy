package com.bbb.rest.giftregistry

import com.bbb.commerce.giftregistry.droplet.BridalToolkitLinkDroplet;
import com.bbb.commerce.giftregistry.vo.BridalRegistryVO
import com.bbb.exception.BBBSystemException
import javax.servlet.ServletException
import spock.lang.specification.BBBExtendedSpec;

class BridalToolkitRegistriesAPISpecification extends BBBExtendedSpec{

	BridalToolkitRegistriesAPI toolApi
	BridalToolkitLinkDroplet bridalToolkitLinkDroplet =Mock()

	def setup(){
		toolApi = Spy()
		toolApi.setBridalToolkitLinkDroplet(bridalToolkitLinkDroplet)
	}

	def"getBridalToolkitRegistries, returns the bridalRegistryVOList"(){

		given:
		BridalRegistryVO vo1 =new BridalRegistryVO()
		1*bridalToolkitLinkDroplet.service(requestMock, responseMock)
		1*requestMock.getObjectParameter("bridalRegistryVOList") >> [vo1]

		when:
		List<BridalRegistryVO> list = toolApi.getBridalToolkitRegistries()

		then:
		list == [vo1]
		1*requestMock.setParameter("siteId",_)
	}

	def"getBridalToolkitRegistries, throws IOException"(){

		given:
		1*bridalToolkitLinkDroplet.service(requestMock, responseMock) >> {throw new IOException("Error")}
		0*requestMock.getObjectParameter("bridalRegistryVOList") 

		when:
		List<BridalRegistryVO> list = toolApi.getBridalToolkitRegistries()

		then:
		list == null
		1*requestMock.setParameter("siteId", _)
		BBBSystemException e = thrown()
	}

	def"getBridalToolkitRegistries, throws ServletException"(){

		given:
		1*bridalToolkitLinkDroplet.service(requestMock, responseMock) >> {throw new ServletException("Error")}
		0*requestMock.getObjectParameter("bridalRegistryVOList")

		when:
		List<BridalRegistryVO> list = toolApi.getBridalToolkitRegistries()

		then:
		list == null
		1*requestMock.setParameter("siteId", _)
		BBBSystemException e = thrown()
	}
}
