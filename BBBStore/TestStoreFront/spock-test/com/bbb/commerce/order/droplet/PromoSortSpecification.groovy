package com.bbb.commerce.order.droplet

import com.bbb.constants.BBBCoreConstants;
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Formatter.DateTime

import atg.repository.RepositoryItem
import spock.lang.specification.BBBExtendedSpec;


class PromoSortSpecification extends BBBExtendedSpec  {

	def PromoSort psObject
	def RepositoryItem item1 = Mock()
	def RepositoryItem item2 = Mock()
	def setup(){
		psObject = new PromoSort()
	}
	
	def"compare. TC to compare date "(){
		given:
	
		Timestamp timestamp1 = new Timestamp(new Date().getTime());
		java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2005-04-06 09:01:10")
		
		
		item1.getPropertyValue("endUsable")  >> timestamp1
		item2.getPropertyValue("endUsable") >> ts2
		
		when:
		int value =psObject.compare(item1, item2)
		then:
		value == -1
		
	}
	
	def"compare. TC to compare date  "(){
		given:
	
		Timestamp timestamp1 = new Timestamp(new Date().getTime());
		java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2005-04-06 09:01:10")
		
		
		item1.getPropertyValue("endUsable")  >> ts2
		item2.getPropertyValue("endUsable") >> timestamp1
		
		when:
		int value =psObject.compare(item1, item2)
		then:
		value == 1
		
	}
	
	def"compare. TC to end usable date is equals  "(){
		given:
	
		Timestamp timestamp1 = new Timestamp(new Date().getTime());
		
		item1.getPropertyValue("endUsable")  >> timestamp1
		item2.getPropertyValue("endUsable") >> timestamp1
		
		when:
		int value =psObject.compare(item1, item2)
		then:
		value == 0
		
	}
}
