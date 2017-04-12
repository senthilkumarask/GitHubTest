package com.bbb.commerce.browse.manager

import java.util.List
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ImageVO
import com.bbb.commerce.catalog.vo.MediaVO;
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.commerce.catalog.vo.RollupTypeVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.objectspace.jgl.predicates.InstanceOf;

import spock.lang.specification.BBBExtendedSpec

class ProductManagerSpecification extends BBBExtendedSpec {
	ProductManager prodMngr = new ProductManager()
	def BBBCatalogTools catalogTools = Mock()
	
	def setup(){
		prodMngr.setBbbCatalogTools(catalogTools)
	}
	
	def "getProductDetails(), Happy Path"(){
		
		def ProductVO vo = Mock()
		
		given:
			1*catalogTools.getProductDetails(_,_) >> 	vo
		
		when:
			vo = prodMngr.getProductDetails("siteId","prodId")
		then:
			vo != null
	}
	
	def "getMainProductDetails(), Happy Path"(){
		
		def ProductVO vo = Mock()
		
		given:
			1*catalogTools.getMainProductDetails(_,_,true, false, true) >> vo
		
		when:
			vo = prodMngr.getMainProductDetails("siteId","prodId")
			
		then:
			vo != null
	}
	
	def "getProductStatus(), Happy Path"(){
		
		boolean flag = false
		
		given:
			1*catalogTools.isEverlivingProduct(_,_) >> true
		
		when:
			flag = prodMngr.getProductStatus("siteId","prodId")
			
		then:
			flag == true
	}
	
	def "getEverLivingProductDetails(), Happy Path"(){
		
		def ProductVO vo = Mock()
		
		given:
			1*catalogTools.getEverLivingProductDetails(_,_,true, false, true) >> vo
		
		when:
			vo = prodMngr.getEverLivingProductDetails("siteId","prodId")
			
		then:
			vo != null
	}
	
	def "getEverLivingMainProductDetails(), Happy Path"(){
		
		def ProductVO vo = Mock()
		
		given:
			1*catalogTools.getEverLivingMainProductDetails(_,_,true, false, true) >> vo
		
		when:
			vo = prodMngr.getEverLivingMainProductDetails("siteId","prodId")
			
		then:
			vo != null
	}
	
	def "getMediaDetails(), Happy Path"(){
		
		def MediaVO vo = Mock()
		def List<MediaVO> list = new ArrayList<MediaVO>()
		
		given:
			1*catalogTools.getProductMedia(_,_) >> [vo]
		
		when:
			list = prodMngr.getMediaDetails("siteId","prodId")
			
		then:
			list.isEmpty() == false
			list.size() == 1
			
	}
	
	def "getEverLivingSKUId(), Happy Path"(){
		
		def Map<String,String> rollUpTypeValueMap = new HashMap<String,String>()
		def String skuId
		
		given:
			1*catalogTools.getEverLivingSKUDetails(_,_,rollUpTypeValueMap) >> "skuId"
		
		when:
			skuId = prodMngr.getEverLivingSKUId("siteId","prodId",rollUpTypeValueMap)
			
		then:
			skuId.equals("skuId") == true
			
	}
	
	def "getRollupDetails(), Happy Path"(){
		
		def RollupTypeVO vo = Mock()
		def List<RollupTypeVO> list = new ArrayList<RollupTypeVO>()
		
		given:
			1*catalogTools.getRollUpList("productId", "firstRollUpValue", "firstRollUpType", "secondRollUpType") >> [vo]
		
		when:
			list = prodMngr.getRollupDetails("productId", "firstRollUpValue", "firstRollUpType", "secondRollUpType")
			
		then:
			list != null
	}
	
	def "getEverLivingRollupDetails(), Happy Path"(){
		
		def RollupTypeVO vo = Mock()
		def List<RollupTypeVO> list = new ArrayList<RollupTypeVO>()
		
		given:
			1*catalogTools.getEverLivingRollUpList("productId", "firstRollUpValue", "firstRollUpType", "secondRollUpType") >> [vo]
		
		when:
			list = prodMngr.getEverLivingRollupDetails("productId", "firstRollUpValue", "firstRollUpType", "secondRollUpType")
			
		then:
			list != null
	}
	
	def "getSKUDetails(), Happy Path"(){
		
		def SKUDetailVO vo = Mock()
		
		given:
			1*catalogTools.getSKUDetails(_,_,_) >> vo
		
		when:
			vo = prodMngr.getSKUDetails("siteId","prodId",true)
			
		then:
			vo != null
	}
	
	def "getEverLivingSKUDetails(), Happy Path"(){
		
		def SKUDetailVO vo = Mock()
		
		given:
			1*catalogTools.getEverLivingSKUDetails(_,_,_) >> vo
		
		when:
			vo = prodMngr.getEverLivingSKUDetails("siteId","prodId",true)
			
		then:
			vo != null
	}
	
	def "getSKUId(), Happy Path"(){
		
		def Map<String,String> rollUpTypeValueMap = new HashMap<String,String>()
		def String skuId
		
		given:
			1*catalogTools.getSKUDetails(_,_,rollUpTypeValueMap) >> "skuId"
		
		when:
			skuId = prodMngr.getSKUId("siteId","prodId",rollUpTypeValueMap)
			
		then:
			skuId.equals("skuId") == true
			
	}
	
	def "getParentCategoryForProduct(), Happy Path"(){
		
		def Map<String, CategoryVO> map = new HashMap<String, CategoryVO>()
		
		given:
			1*catalogTools.getParentCategoryForProduct(_,_) >> map
		
		when:
			map = prodMngr.getParentCategoryForProduct("siteId","prodId")
			
		then:
			map!=null
			
	}
	
	def "getParentCategory(), Happy Path"(){
		
		def Map<String, CategoryVO> map = new HashMap<String, CategoryVO>()
		
		given:
			1*catalogTools.getParentCategory(_,_) >> map
		
		when:
			map = prodMngr.getParentCategory("siteId","prodId")
			
		then:
			map!=null
			
	}
	
	def "getImmediateParentCategoryForProduct(), Happy Path"(){
		
		def String category
		
		given:
			1*catalogTools.getImmediateParentCategoryForProduct(_,_) >> "parentCategory"
		
		when:
			category = prodMngr.getImmediateParentCategoryForProduct("siteId","prodId")
			
		then:
			category.equals("parentCategory") == true
			
	}
	
	def "getProductImages(), Happy Path"(){
		
		def ImageVO vo = Mock()
		
		given:
			1*catalogTools.getProductImages(_) >> vo
		
		when:
			vo = prodMngr.getProductImages("prodId")
			
		then:
			vo != null
	}
	
	def "getProductRelatedCategories(), Happy Path"(){
		
		def CategoryVO vo = Mock()
		def List<CategoryVO> list = new ArrayList<CategoryVO>()
		
		given:
			1*catalogTools.getRelatedCategories(_,_) >> [vo]
		
		when:
			list = prodMngr.getProductRelatedCategories("productId", "siteId")
			
		then:
			list != null
	}
}
