package com.bbb.utils

import static com.bbb.constants.BBBAccountConstants.OPARAM_OUTPUT;

import spock.lang.specification.BBBExtendedSpec

class BBBLogBuildNumberSpecification extends BBBExtendedSpec {
	BBBLogBuildNumber logBuildNumer
	Properties properties = Mock()
	 def setup() {
		 logBuildNumer = Spy()
		 logBuildNumer.getBuildProperties() >> properties
	 }
	 def"This is method used to doservice method"(){
		 given:
		 logBuildNumer.setEnabled(true)
		 logBuildNumer.getResourceAsStream(_)>> {}
		 properties.load(_) >> {}
		 properties.getProperty("revision.number") >> "revision.number"
		 properties.getProperty("build.from") >> "build.from"
		 properties.getProperty("build.created.on") >> "build.created.on"
		 when:
		 logBuildNumer.doStartService()
		 then:
		 logBuildNumer.getVersionNumber().equals("revision.number")
		 logBuildNumer.getBuildFrom().equals("build.from")
		 logBuildNumer.getBuildDate().equals("build.created.on")
	 }
	 def"This is method used to doservice method.service is not enabled"(){
		 given:
		 logBuildNumer.setEnabled(false)
		 when:
		 logBuildNumer.doStartService()
		 then:
		 logBuildNumer.getVersionNumber().equals(null)
		 logBuildNumer.getBuildFrom().equals(null)
		 logBuildNumer.getBuildDate().equals(null)
	 }
	 def"This is method used to doservice method throw IOException"(){
		 given:
		 logBuildNumer.setEnabled(true)
		 logBuildNumer.getResourceAsStream(_)>> {}
		 properties.load(_) >> {throw new IOException()}
		 properties.getProperty("revision.number") >> "revision.number"
		 properties.getProperty("build.from") >> "build.from"
		 properties.getProperty("build.created.on") >> "build.created.on"
		 expect:
		 logBuildNumer.doStartService()
	 }
	 def"This is method used to doservice method throw Exception"(){
		 given:
		 logBuildNumer.setEnabled(true)
		 logBuildNumer.getResourceAsStream(_)>> {}
		 properties.load(_) >> {throw new Exception()}
		 properties.getProperty("revision.number") >> "revision.number"
		 properties.getProperty("build.from") >> "build.from"
		 properties.getProperty("build.created.on") >> "build.created.on"
		 expect:
		 logBuildNumer.doStartService()
	 }
	 def"This methos is used to call serice method"(){
		 given:
		 logBuildNumer.setEnabled(true)
		 logBuildNumer.setBuildFrom("buildfrom")
		 logBuildNumer.setDcPrefix("DC")
		 requestMock.getParameter("BUILD_TAG_NUM") >> "buildfromDC"
		 when:
		String value =  logBuildNumer.getBuildNumber()
		 then:
		1*requestMock.setParameter("BUILD_NUM",_)	 
		1*requestMock.setParameter("BUILD_FROM",_)
		1*requestMock.setParameter("TAG_ID",_);
		1*requestMock.setParameter("BUILD_DATE",_)
		1*requestMock.setParameter("BUILD_TAG_NUM","buildfromDC")
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		value.equals("buildfromDC")
	 }
	 def"This methos is used to call serice method,service is diable"(){
		 given:
		 logBuildNumer.setEnabled(false)
		 when:
		 logBuildNumer.service(requestMock, responseMock)
		 then:
		 0*requestMock.setParameter("BUILD_NUM",_)	 
		0*requestMock.setParameter("BUILD_FROM",_)
		0*requestMock.setParameter("TAG_ID",_);
		0*requestMock.setParameter("BUILD_DATE",_)
		0*requestMock.setParameter("BUILD_TAG_NUM",_)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock);
	 }
	 def"This methos is used to call serice method throw ioexception"(){
		 given:
		 logBuildNumer.setEnabled(true)
		 1*requestMock.setParameter("BUILD_NUM",_) >> {throw new IOException()}
		 expect:
		 logBuildNumer.service(requestMock, responseMock)
	 }
	 def"This methos is used to call serice method throw exception"(){
		 given:
		 logBuildNumer.setEnabled(true)
		 1*requestMock.setParameter("BUILD_NUM",_) >> {throw new Exception()}
		 expect:
		 logBuildNumer.service(requestMock, responseMock)
	 }
}

