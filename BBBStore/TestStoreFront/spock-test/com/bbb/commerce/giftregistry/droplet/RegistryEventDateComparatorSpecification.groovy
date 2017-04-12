package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class RegistryEventDateComparatorSpecification extends BBBExtendedSpec {
	
	RegistryEventDateComparator testObj
	RegistrySkinnyVO registrySkinnyVO1Mock = new RegistrySkinnyVO()
	RegistrySkinnyVO registrySkinnyVO2Mock = new RegistrySkinnyVO()
	
	def setup(){
		testObj = new RegistryEventDateComparator()
	}
		
	def"compare. This TC is when case1 with geteventDate values"(){
		given:
			testObj.sortOrder = 1
			registrySkinnyVO1Mock.setEventDate("12/02/2016")
			registrySkinnyVO2Mock.setEventDate("12/02/2017")
		when:
			int results = testObj.compare(registrySkinnyVO1Mock, registrySkinnyVO2Mock)
		then:
			results == -1
			registrySkinnyVO1Mock.getEventDate().equals("12/02/2016")
			registrySkinnyVO2Mock.getEventDate().equals("12/02/2017")
	}
	
	
	def"compare. This TC is when case1 with getEventDate is null"(){
		given:
			testObj.sortOrder = 1
			registrySkinnyVO1Mock.setEventDate(null)
			registrySkinnyVO2Mock.setEventDate(null)
		when:
			int results = testObj.compare(registrySkinnyVO1Mock, registrySkinnyVO2Mock)
		then:
			results == 0
			registrySkinnyVO1Mock.getEventDate().equals(null)
			registrySkinnyVO2Mock.getEventDate().equals(null)
	}
	
	def"compare. This TC is when case2 with geteventDate values"(){
		given:
			testObj.sortOrder = 2
			registrySkinnyVO1Mock.setEventDate("12/02/2016")
			registrySkinnyVO2Mock.setEventDate("12/02/2017")
		when:
			int results = testObj.compare(registrySkinnyVO1Mock, registrySkinnyVO2Mock)
		then:
			results == 1
			registrySkinnyVO1Mock.getEventDate().equals("12/02/2016")
			registrySkinnyVO2Mock.getEventDate().equals("12/02/2017")
	}
	
	def"compare. This TC is when case2 with getEventDate is null"(){
		given:
			testObj.sortOrder = 2
			registrySkinnyVO1Mock.setEventDate(null)
			registrySkinnyVO2Mock.setEventDate(null)
		when:
			int results = testObj.compare(registrySkinnyVO1Mock, registrySkinnyVO2Mock)
		then:
			results == 0
			registrySkinnyVO1Mock.getEventDate().equals(null)
			registrySkinnyVO2Mock.getEventDate().equals(null)
	}
	
	def"compare. This TC is when sortOrder is 0"(){
		given:
			testObj.sortOrder = 0
		when:
			int results = testObj.compare(registrySkinnyVO1Mock, registrySkinnyVO2Mock)
		then:
			results == 0
	}

}
