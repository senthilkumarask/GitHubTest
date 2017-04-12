package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.rest.catalog.vo.CatalogItemAttributesVO;

import atg.nucleus.naming.ParameterName;
import spock.lang.specification.BBBExtendedSpec

class GetGenderKeyDropletSpecification extends BBBExtendedSpec {

	GetGenderKeyDroplet droplet = new GetGenderKeyDroplet()
	BBBCatalogTools mBBBCatalogTools = Mock()
	
	def setup(){
		droplet.setBBBCatalogTools(mBBBCatalogTools)
	}
	
	def "service method"(){
		
		given:
			Map<String,String> genderMap = new HashMap<String,String>()
			genderMap.put("genderKey","M")
			genderMap.put("key","F")
		 	requestMock.getParameter(ParameterName.getParameterName("genderKey")) >> "genderKey"
			requestMock.getParameter("inverseflag") >> "invFlag"
			1*mBBBCatalogTools.getConfigValueByconfigType("GenderMap") >> genderMap
			
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("genderCode", "M");
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
	}
	
	def "service method with inverseFlag as null"(){
		
		given:
			 requestMock.getParameter(ParameterName.getParameterName("genderKey")) >> "gender_Key"
			 requestMock.getParameter("inverseflag") >> null
			
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("genderCode", "Key");
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
	}
	
	def "service method with inverseFlag as null and genderKey has _ at start"(){
		
		given:
			 1*requestMock.getParameter(ParameterName.getParameterName("genderKey")) >> "_genderKey"
			 1*requestMock.getParameter("inverseflag") >> null
			
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("genderCode", "_genderKey");
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
	}
	
	def "service method with genderKey as null"(){
		
		given:
			 requestMock.getParameter(ParameterName.getParameterName("genderKey")) >> null
			 requestMock.getParameter("inverseflag") >> null
			
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("errorMsg", "genderKy is null");
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock);
	}
	
	def "service method with BBBSystemException thrown"(){
		
		given:
			 requestMock.getParameter(ParameterName.getParameterName("genderKey")) >> "genderKey"
			requestMock.getParameter("inverseflag") >> "invFlag"
			1*mBBBCatalogTools.getConfigValueByconfigType("GenderMap") >> {throw new BBBSystemException("BBBSystemException is thrown")}
			
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("errorMsg", "genderValue is null");
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
	}
	
	def "service method  with BBBBusinessException thrown"(){
		
		given:
			 requestMock.getParameter(ParameterName.getParameterName("genderKey")) >> "genderKey"
			requestMock.getParameter("inverseflag") >> "invFlag"
			1*mBBBCatalogTools.getConfigValueByconfigType("GenderMap") >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("genderCode", "");
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
	}
}
