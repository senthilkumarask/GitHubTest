package com.bbb.commerce.order.droplet

import atg.commerce.order.CommerceItem
import atg.repository.RepositoryItem
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.droplet.BBBPromotionTools
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec

class PromotionExclusionMsgDropletSpecification extends BBBExtendedSpec {

	def PromotionExclusionMsgDroplet pemDroplet
	def BBBOrderImpl orderMock = Mock()
	def CommerceItem item = Mock()
	def CommerceItem item1 = Mock()
	def CommerceItem item3 = Mock()
	
	def BBBPromotionTools promotionToolsMock = Mock()
	def BBBCatalogToolsImpl catalogTools = Mock()
	def RepositoryItem coupon1 = Mock()
	def RepositoryItem coupon2 = Mock()
	def RepositoryItem coupon3 = Mock()
	def RepositoryItem coupon4 = Mock()
	
	
	def RepositoryItem promotion1 = Mock()
	
	
	def setup(){
		pemDroplet = new PromotionExclusionMsgDroplet(promotionTools:promotionToolsMock)
	}
	
	def "service. Tc to check promoExclusionMap" (){
		given:
			Set<CommerceItem> s = [item,item1,item3]
			Set<CommerceItem> s1 = [item,item1]
			requestMock.getObjectParameter("order") >> orderMock
			1*orderMock.getExcludedPromotionMap()  >> ["cou1" :s , "cou3": s1 ,"cou4": s1]
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [coupon1, coupon2, coupon3, coupon4]
			coupon1.getRepositoryId() >> "cou1"
			coupon2.getRepositoryId() >> "cou2"
			coupon3.getRepositoryId() >> "cou3"
			coupon4.getRepositoryId() >> "cou4"
			
			promotionToolsMock.getCatalogTools() >> catalogTools
			1*catalogTools.getPromotions("cou1") >> [promotion1]  
			1*catalogTools.getPromotions("cou3") >> null
			1*catalogTools.getPromotions("cou4") >> []
			
			item.getId() >> "item1"
			item1.getId() >> "item1"
			item3.getId() >> "item3"
			
		when:
			pemDroplet.service(requestMock, responseMock)
		    
		then:
			1 * requestMock.setParameter("promoExclusionMap", ['item3':[promotion1] as Set,'item1':[promotion1] as Set])
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0 * catalogTools.getPromotions("cou2") 
			
	}
	
	def "service. Tc when CouponList is empty " (){
		given:
			Set<CommerceItem> s = [item,item1,item3]
			requestMock.getObjectParameter("order") >> orderMock
			orderMock.getExcludedPromotionMap()  >> ["cou1" :s ]
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> []

			
		when:
			pemDroplet.service(requestMock, responseMock)
			
		then:
			0 * requestMock.setParameter("promoExclusionMap", _)
			1 * requestMock.serviceLocalParameter("empty", requestMock, responseMock)
			0*catalogTools.getPromotions(_)
	}
	
	def "service. Tc when promotion map is empty " (){
		given:
			Set<CommerceItem> s = [item,item1,item3]
			requestMock.getObjectParameter("order") >> orderMock
			orderMock.getExcludedPromotionMap()  >> [:]

			
		when:
			pemDroplet.service(requestMock, responseMock)
			
		then:
			0 * requestMock.setParameter("promoExclusionMap", _)
			1 * requestMock.serviceLocalParameter("empty", requestMock, responseMock)
	}
	
	/*****************************************exception scenario ****************************/
	def "service. Tc for exception scenario" (){
		given:
			Set<CommerceItem> s1 = [item,item1]
			requestMock.getObjectParameter("order") >> orderMock
			orderMock.getExcludedPromotionMap()  >> ["cou1" :s1 ]
			promotionToolsMock.getCouponListFromOrder(orderMock) >> [coupon1, coupon2]
			coupon1.getRepositoryId() >> "cou1"
			coupon2.getRepositoryId() >> "cou1"
			
			promotionToolsMock.getCatalogTools() >> catalogTools
			2*catalogTools.getPromotions("cou1") >> {throw new BBBSystemException("exception")} >> {throw new BBBBusinessException("exception")}
			
		when:
			pemDroplet.service(requestMock, responseMock)
			
		then:
			1 * requestMock.serviceLocalParameter("empty", requestMock, responseMock)
			2 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
			
	}
	
}
