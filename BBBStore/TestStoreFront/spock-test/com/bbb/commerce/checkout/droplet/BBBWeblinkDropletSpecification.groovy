package com.bbb.commerce.checkout.droplet

import spock.lang.specification.BBBExtendedSpec;

class BBBWeblinkDropletSpecification extends BBBExtendedSpec {
 def  BBBWeblinkDroplet wlDropletObject 
 
 def setup(){
	 wlDropletObject = new BBBWeblinkDroplet()
 }
 def"service. TC to check order is webLinkorder when school cookie is in request object "(){
	 given:
	 requestMock.getCookieParameter("SchoolCookie") >> "coll1235"
	 
	 when:
	 wlDropletObject.service(requestMock, responseMock)
	 
	 then:
	 1*requestMock.serviceParameter("weblinkOrder", requestMock, responseMock);
	 
 }
 
 def"service. TC to check order is not webLinkorder when school cookie is not in request object "(){
	 given:
	 requestMock.getCookieParameter("SchoolCookie") >> ""
	 
	 when:
	 wlDropletObject.service(requestMock, responseMock)
	 
	 then:
	 1*requestMock.serviceParameter("notWeblinkOrder", requestMock, responseMock);
	 
 }
	
}
