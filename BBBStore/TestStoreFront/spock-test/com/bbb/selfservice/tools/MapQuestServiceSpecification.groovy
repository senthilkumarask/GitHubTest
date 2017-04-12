package com.bbb.selfservice.tools

import java.io.Reader;

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import spock.lang.specification.BBBExtendedSpec

class MapQuestServiceSpecification extends BBBExtendedSpec {
	
	MapQuestService service = new MapQuestService()
	
	def "communicateMapQuest method"(){
		
		given:
			service = Spy()
			HttpResponse webResponse = Mock()
			StatusLine statusLine = Mock()
			HttpEntity entity = Mock()
			InputStream stream = Mock()
			
			1*service.executeHttpClient(_,_) >> webResponse
			1*webResponse.getStatusLine() >> statusLine
			1*statusLine.getStatusCode() >> 200
			1*webResponse.getEntity() >> entity
			1*entity.getContent() >> stream
			1*service.inputToString(_) >> "response"
			
		when:
			String commMapQuest = service.communicateMapQuest("www.google.com")
		
		then:
			commMapQuest == "response"
	}
	
	def "communicateMapQuest method with searchString contains |"(){
		
		given:
			service = Spy()
			HttpResponse webResponse = Mock()
			StatusLine statusLine = Mock()
			HttpEntity entity = Mock()
			InputStream stream = Mock()
			
			1*service.executeHttpClient(_,_) >> webResponse
			1*webResponse.getStatusLine() >> statusLine
			1*statusLine.getStatusCode() >> 200
			1*webResponse.getEntity() >> entity
			1*entity.getContent() >> stream
			1*service.inputToString(_) >> "response"
			
		when:
			String commMapQuest = service.communicateMapQuest("www.google.com|")
		
		then:
			commMapQuest == "response"
	}
	
	def "communicateMapQuest method with searchString contains space and status code not equals 200 and reader as null"(){
		
		given:
			service = Spy()
			HttpResponse webResponse = Mock()
			StatusLine statusLine = Mock()
			HttpEntity entity = Mock()
			InputStream stream = Mock()
			
			1*service.executeHttpClient(_,_) >> webResponse
			1*webResponse.getStatusLine() >> statusLine
			1*statusLine.getStatusCode() >> 20
			
		when:
			String commMapQuest = service.communicateMapQuest("www.google.com  ")
		
		then:
			commMapQuest == null
	}
	
	def "inputToString method"(){
		
		given:
			BufferedReader reader = Mock()
			1*reader.read() >> 1
			1*reader.read() >> -1
		
		when:
			String inpToString = service.inputToString(reader)
		
		then:
			inpToString == ""
		
	}
}
