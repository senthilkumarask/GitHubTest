package com.bbb.commerce.order.droplet

import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem
import atg.commerce.order.ShippingGroup
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.OrderPriceInfo
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import atg.userprofiling.Profile

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.droplet.BBBPromotionTools
import com.bbb.commerce.catalog.vo.CategoryVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import spock.lang.specification.BBBExtendedSpec

class BBBOrderRKGInfoSpecification extends BBBExtendedSpec {
	def BBBOrderRKGInfo orderRKGInfo
	def Profile profileMock = Mock()
	def BBBOrderImpl orderMock =  Mock()
	def ShippingGroupCommerceItemRelationship sgciRelationship1 =  Mock()
	def ShippingGroupCommerceItemRelationship sgciRelationship2 =  Mock()
	def ShippingGroupCommerceItemRelationship sgciRelationship3 =  Mock()
	def ShippingGroupCommerceItemRelationship sgciRelationship4 =  Mock()
	def ShippingGroupCommerceItemRelationship sgciRelationship5 =  Mock()
	def ShippingGroupCommerceItemRelationship sgciRelationship6 =  Mock()
	def ShippingGroupCommerceItemRelationship sgciRelationship7 =  Mock()

	
	def ShippingGroup sg1 = Mock()
	def ShippingGroup sg2 = Mock()
	
	def BBBCommerceItem commerceItem1 = Mock()
	def BBBCommerceItem commerceItem2 = Mock()
	def CommerceItem commerceItem3 = Mock()
	def BBBCommerceItem commerceItem4 = Mock()
	def BBBCommerceItem commerceItem5 = Mock()
	def BBBCommerceItem commerceItem6 = Mock()
	def BBBCommerceItem commerceItem7 = Mock()

	def AuxiliaryData auxilarydata = Mock()
	def AuxiliaryData auxilarydata2 = Mock()
	def AuxiliaryData auxilarydata3 = Mock()
	def AuxiliaryData auxilarydata4 = Mock()
	def AuxiliaryData auxilarydata5 = Mock()
	def AuxiliaryData auxilarydata6 = Mock()
	def AuxiliaryData auxilarydata7 = Mock()

	
	def RepositoryItem sku = Mock()
	def BBBPromotionTools promotionToolsMock = Mock()
	def BBBCatalogToolsImpl cToolsMock = Mock()
	
	def RepositoryItem couponItem1 = Mock()
	def RepositoryItem couponItem2 = Mock()
	def RepositoryItem couponItem3 = Mock()
	def RepositoryItem couponItem4 = Mock()
	def RepositoryItem couponItem5 = Mock()
	def RepositoryItem couponItem6 = Mock()
	def RepositoryItem couponItem7 = Mock()
	def RepositoryItem couponItem8 = Mock()
	def RepositoryItem couponItem9 = Mock()
	def RepositoryItem couponItem10 = Mock()
	def RepositoryItem couponItem11 = Mock()
	def RepositoryItem couponItem12 = Mock()
	

	def RepositoryItem promos = Mock() 
	def RepositoryItem promos1 = Mock()
	def RepositoryItem promos2 = Mock()
	def RepositoryItem promos3 = Mock()
	def RepositoryItem promos4 = Mock()
	
	def RepositoryItem productRepositoryItem = Mock() 
	
	def RepositoryItem parentCategory1 = Mock()
	def RepositoryItem parentCategory2 = Mock()
	
	def RepositoryItemDescriptor itemDescriptor = Mock()
	def RepositoryItemDescriptor itemDescriptor1 = Mock()
	def RepositoryItemDescriptor itemDescriptor2 = Mock()
	def RepositoryItemDescriptor itemDescriptor3 = Mock()
	
	
	def BBBPromotionTools promotionTools = Mock()
	
	def MutableRepository catalogRepository = Mock()
	def OrderPriceInfo orderPriInfo = Mock()
	
	CategoryVO categoryVO1 = new CategoryVO()
	CategoryVO categoryVO2 = new CategoryVO()
	CategoryVO categoryVO3 = new CategoryVO()
	CategoryVO categoryVO4 = new CategoryVO()
	CategoryVO categoryVO5 = new CategoryVO()
	CategoryVO categoryVO6 = new CategoryVO()

	
	def setup(){
		orderRKGInfo = new BBBOrderRKGInfo(catalogTools : cToolsMock ,profile :  profileMock , promotionTools : promotionTools)
	}
	def "service" (){
		given:
		
		def Set  parentCategorySet = [parentCategory1, parentCategory2] 
		String siteId = "usBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getSiteId() >> siteId
		orderMock.getCommerceItemCount() >> 2
		
		//  createPromoStr method
		
		1*promotionTools.getCouponListFromOrder(orderMock) >> [couponItem1, couponItem2, couponItem3, couponItem4, couponItem5,couponItem6, couponItem7, couponItem8, couponItem9, couponItem10, couponItem11,couponItem12]
		couponItem1.getRepositoryId() >> "c1"
		couponItem2.getRepositoryId() >> "c2"
		couponItem3.getRepositoryId() >> "c3"
		couponItem4.getRepositoryId() >> "c4"
		couponItem5.getRepositoryId() >> "c5"
		couponItem6.getRepositoryId() >> "c6"
		couponItem7.getRepositoryId() >> "c7"
		couponItem8.getRepositoryId() >> "c8"
		couponItem9.getRepositoryId() >> "c9"
		couponItem10.getRepositoryId() >> "c10"
		couponItem11.getRepositoryId() >> "c11"
		couponItem12.getRepositoryId() >> "c12"
		
		
		1*cToolsMock.getPromotions( "c1") >> [promos] 
		1*cToolsMock.getPromotions( "c2") >> [promos]
		1*cToolsMock.getPromotions( "c3") >> [promos1]
		1*cToolsMock.getPromotions( "c4") >> [promos1]
		1*cToolsMock.getPromotions( "c5") >> [promos2]
		1*cToolsMock.getPromotions( "c6") >> [promos2]
		1*cToolsMock.getPromotions( "c7") >> [promos3]
		1*cToolsMock.getPromotions( "c8") >> null
		1*cToolsMock.getPromotions( "c9") >> []
		1*cToolsMock.getPromotions( "c10") >> {throw new BBBSystemException("exception")}
		1*cToolsMock.getPromotions( "c11") >> {throw new BBBBusinessException("exception")}
		1*cToolsMock.getPromotions( "c12") >> [promos4]
		
		promos.getItemDescriptor() >> itemDescriptor
		2*itemDescriptor.getItemDescriptorName() >> "Item Discount"
		promos.getPropertyValue("displayName") >> "item"
		
		promos1.getItemDescriptor() >> itemDescriptor1
		(1.._)*itemDescriptor1.getItemDescriptorName() >> "Order Discount"
		promos1.getPropertyValue("displayName") >> "order"
		
		promos2.getItemDescriptor() >> itemDescriptor2
		(1.._)*itemDescriptor2.getItemDescriptorName() >> "Ship Discount"
		promos2.getPropertyValue("displayName") >> "shipping"
		
		promos3.getItemDescriptor() >> itemDescriptor3
		(1.._)*itemDescriptor3.getItemDescriptorName() >> "Discount"

		1*promos4.getItemDescriptor() >> {throw new RepositoryException("exception")} 
		//itemDescriptor.getItemDescriptorName() >> "Item Discount"



		// end 
		
		1*orderMock.getShippingGroups() >> [sg1]
		1*sg1.getCommerceItemRelationships() >> [sgciRelationship1,sgciRelationship2, sgciRelationship3, sgciRelationship4]
		
		1*sgciRelationship1.getCommerceItem() >> commerceItem1
		1*sgciRelationship2.getCommerceItem() >> commerceItem3
		1*sgciRelationship3.getCommerceItem() >> commerceItem2
		1*sgciRelationship4.getCommerceItem() >> commerceItem4
		
		commerceItem1.getAuxiliaryData() >> auxilarydata
		commerceItem2.getAuxiliaryData() >> auxilarydata2
		commerceItem4.getAuxiliaryData() >> auxilarydata4
		
		1*auxilarydata.getCatalogRef() >> sku
		1*auxilarydata2.getCatalogRef() >> sku
		1*auxilarydata4.getCatalogRef() >> sku
		
		sku.getPropertyValue("displayName") >> "sku1"
		
		auxilarydata.getProductId() >> "p1"
		auxilarydata2.getProductId() >> "p2"
		auxilarydata4.getProductId() >> null
		
		commerceItem1.getQuantity() >> 2
		(1.._)*cToolsMock.getCatalogRepository() >> catalogRepository
		
		(1.._)*catalogRepository.getItem("p1", "product") >> productRepositoryItem
		(1.._)*catalogRepository.getItem("p2", "product") >> productRepositoryItem
		
		(1.._)*productRepositoryItem.getPropertyValue("parentCategories") >> parentCategorySet
		1*cToolsMock.getParentCategoryForProduct("p1", "usBed") >> ["cat1" : categoryVO1, "cat2":categoryVO2, "cat3" : categoryVO3 ]
		1*cToolsMock.getParentCategoryForProduct("p2", "usBed") >> ["cat" : categoryVO4,]
		
		categoryVO1.setCategoryId("catId")
		categoryVO1.setCategoryName("catName")

		categoryVO2.setCategoryId("catId2")
		categoryVO2.setCategoryName("catName2")
		
		categoryVO3.setCategoryId("catId2")
		categoryVO3.setCategoryName("catName2")

		(1.._)*orderMock.getPriceInfo() >> orderPriInfo
		orderPriInfo.getRawSubtotal() >> 100
		when:
		orderRKGInfo.service(requestMock, responseMock)
		then:
		
		1*requestMock.setParameter("RKG_PRETAX_TOTAL", '100.00' )
		1*requestMock.setParameter(BBBCoreConstants.RKG_PRODUCT_NAMES, "sku1,sku1sku1");
		1*requestMock.setParameter(BBBCoreConstants.RKG_PRODUCT_IDS, "p1,p2null");
		1*requestMock.setParameter(BBBCoreConstants.RKG_PRODUCT_COUNT, 2);
		1*requestMock.setParameter(BBBCoreConstants.RKG_PROMOTIONS, "item,item,,order,order,shippingshipping");
		1*requestMock.setParameter(BBBCoreConstants.RKG_PROD_CATEGORYID_L1, "catId,null");
		1*requestMock.setParameter(BBBCoreConstants.RKG_PROD_CATEGORY_NAME_L1, "catName,null");
		1*requestMock.setParameter(BBBCoreConstants.RKG_PROD_CATEGORY_NAME_L2, "catName2,null");
		1*requestMock.setParameter(BBBCoreConstants.RKG_PROD_CATEGORY_NAME_L3, "catName2,null");
		1*requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock);

		
	}
	
	def "serviceme . tc when promotion type is item discount type" (){
		given:
		def Set  parentCategorySet = [parentCategory1, parentCategory2]
		def Set  parentCategorySet1 = []
		
		String siteId = "usBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getSiteId() >> siteId
		orderMock.getCommerceItemCount() >> 2
		
		promotionTools.getCouponListFromOrder(orderMock) >> [couponItem1]
		couponItem1.getRepositoryId() >> "c1"
		couponItem2.getRepositoryId() >> "c2"
	
		
		cToolsMock.getPromotions( "c1") >> [promos]
		
		promos.getItemDescriptor() >> itemDescriptor
		itemDescriptor.getItemDescriptorName() >> "Item Discount"
		promos.getPropertyValue("displayName") >> "item"
		
	
		orderMock.getShippingGroups() >> [sg1]
		sg1.getCommerceItemRelationships() >> [sgciRelationship1,sgciRelationship3, sgciRelationship4, sgciRelationship5,sgciRelationship6]
		
		sgciRelationship1.getCommerceItem() >> commerceItem1
		sgciRelationship3.getCommerceItem() >> commerceItem2
		sgciRelationship4.getCommerceItem() >> commerceItem4
		sgciRelationship5.getCommerceItem() >> commerceItem5
		sgciRelationship6.getCommerceItem() >> commerceItem6
		
		
		commerceItem1.getAuxiliaryData() >> auxilarydata
		commerceItem2.getAuxiliaryData() >> auxilarydata2
		commerceItem4.getAuxiliaryData() >> auxilarydata4
		commerceItem5.getAuxiliaryData() >> auxilarydata5
		commerceItem6.getAuxiliaryData() >> auxilarydata6
		
		
		auxilarydata.getCatalogRef() >> sku
		auxilarydata2.getCatalogRef() >> sku
		auxilarydata4.getCatalogRef() >> sku
		auxilarydata5.getCatalogRef() >> sku
		auxilarydata6.getCatalogRef() >> sku
		
		
		sku.getPropertyValue("displayName") >> "sku1"
		
		auxilarydata.getProductId() >> "p1"
		auxilarydata2.getProductId() >> "p2"
		auxilarydata4.getProductId() >> "p2"
		auxilarydata5.getProductId() >> "p3"
		auxilarydata6.getProductId() >> "p4"
		
		
		commerceItem1.getQuantity() >> 2
		cToolsMock.getCatalogRepository() >> catalogRepository
		
		catalogRepository.getItem("p1", "product") >> productRepositoryItem
		catalogRepository.getItem("p2", "product") >> productRepositoryItem
		catalogRepository.getItem("p3", "product") >> null
		catalogRepository.getItem("p4", "product") >> {throw new RepositoryException("exception")}
		
		productRepositoryItem.getPropertyValue("parentCategories") >> parentCategorySet >> parentCategorySet1 >> null
		cToolsMock.getParentCategoryForProduct("p1", "usBed") >> [:]
		
		categoryVO1.setCategoryId("catId")
		categoryVO1.setCategoryName("catName")

		when:
		orderRKGInfo.service(requestMock, responseMock)
		
		then:
		1 * requestMock.setParameter('rkgProdCatNameL3', 'nullnullnullnull')
		1 * requestMock.setParameter('rkgProdCatNameL2', 'nullnullnullnull')
		1 * requestMock.setParameter('rkgProductNames', 'sku1,sku1sku1sku1sku1')
		1 * requestMock.setParameter('rkgPromotions', 'item')
		1 * requestMock.setParameter('rkgProductIds', 'p1,p2p2p3p4')
		1 * requestMock.setParameter('rkgProdCatNameL1', 'nullnullnullnull')
		1 * requestMock.setParameter('rkgProdCatIdL1', 'nullnullnullnull')
		
			}
	
	def "serviceme . tc when promotion type is order discount type" (){
		given:
		def Set  parentCategorySet = [parentCategory1, parentCategory2]
		
		String siteId = "usBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getSiteId() >> siteId
		orderMock.getCommerceItemCount() >> 2
		
		promotionTools.getCouponListFromOrder(orderMock) >> [couponItem1]
		couponItem1.getRepositoryId() >> "c1"
		couponItem2.getRepositoryId() >> "c2"
	
		
		cToolsMock.getPromotions( "c1") >> [promos]
		
		promos.getItemDescriptor() >> itemDescriptor
		itemDescriptor.getItemDescriptorName() >> "Order Discount"
		promos.getPropertyValue("displayName") >> "item"
		

		orderMock.getShippingGroups() >> [sg1]
		sg1.getCommerceItemRelationships() >> [sgciRelationship1,sgciRelationship3]
		
		sgciRelationship1.getCommerceItem() >> commerceItem1
		sgciRelationship3.getCommerceItem() >> commerceItem2
		
		
		commerceItem1.getAuxiliaryData() >> auxilarydata
		commerceItem2.getAuxiliaryData() >> auxilarydata2
		
		
		auxilarydata.getCatalogRef() >> sku
		auxilarydata2.getCatalogRef() >> sku
		
		
		sku.getPropertyValue("displayName") >> "sku1"
		
		auxilarydata.getProductId() >> "p1"
		auxilarydata2.getProductId() >> "p2"
	
		commerceItem1.getQuantity() >> 2
		cToolsMock.getCatalogRepository() >> catalogRepository
		
		catalogRepository.getItem("p1", "product") >> productRepositoryItem
		catalogRepository.getItem("p2", "product") >> productRepositoryItem
		
		productRepositoryItem.getPropertyValue("parentCategories") >> parentCategorySet >> parentCategorySet 
		cToolsMock.getParentCategoryForProduct("p1", "usBed") >> {throw new BBBSystemException("exception")}
		cToolsMock.getParentCategoryForProduct("p2", "usBed") >> {throw new BBBBusinessException("exception")}
		
		categoryVO1.setCategoryId("catId")
		categoryVO1.setCategoryName("catName")

		when:
		orderRKGInfo.service(requestMock, responseMock)
		
		then:
		1 * requestMock.setParameter('rkgProdCatIdL1', '')
		1 * requestMock.setParameter('rkgProdCatNameL3', '')
		1 * requestMock.setParameter('rkgProductCount', 2)
		1 * requestMock.setParameter('rkgProdCatNameL1', '')
		1 * requestMock.setParameter('rkgProdCatNameL2', '')
		1 * requestMock.setParameter('rkgProductNames', 'sku1,sku1')	
		
		}
	
	def "serviceme . tc when order is null" (){
		given:
		requestMock.getObjectParameter("order") >> null
		when:
		orderRKGInfo.service(requestMock, responseMock)
		
		then:
		0*requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
}
