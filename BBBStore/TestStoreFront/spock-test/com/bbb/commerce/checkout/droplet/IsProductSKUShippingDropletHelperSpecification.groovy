package com.bbb.commerce.checkout.droplet

import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.AttributeVO
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec

class IsProductSKUShippingDropletHelperSpecification extends BBBExtendedSpec {
    def IsProductSKUShippingDropletHelper pssdHelperObject
	def BBBCatalogTools catalogToolsMock = Mock()
	SKUDetailVO skuVOMock = new SKUDetailVO()
	AttributeVO skuAttributeVO = new AttributeVO()
	AttributeVO prodAttributeVO = new AttributeVO()
	
	ProductVO  productVo =  new ProductVO()
	def setup(){
	 pssdHelperObject = new IsProductSKUShippingDropletHelper(catalogTools : catalogToolsMock, attributeSKUEnabled : true)
 }
	def"getAttribute. TC to get restrictionZipCodeAttributes when isAttributeSKUEnabled flag is true  "(){
		given:
		
		String siteId = "siteId"
		String skuId = "skuId"
		String prodId = "productId"
		skuAttributeVO.setAttributeName("Restrictionattribute")
		// attrubuteVo list
		List<AttributeVO> atlist = new ArrayList<AttributeVO>()
		atlist.add(skuAttributeVO)
		
		//attrubute map
		Map<String,List<AttributeVO> > pAttributesMap = new HashMap<String,List<AttributeVO>>()
		pAttributesMap.put("at1", atlist)
		//setting arrribut list to skuvo and productVo
		skuVOMock.setSkuAttributes(pAttributesMap)
		
		catalogToolsMock.getSKUDetails(siteId, skuId, true) >> skuVOMock
		catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >> ["Restrictionattribute"]
		
	
		when:
	    Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	
		then:
		restrictionZipCodeAttributes.get("Restrictionattribute") == skuAttributeVO
		(restrictionZipCodeAttributes.get("Restrictionattribute")).getAttributeName() == "Restrictionattribute"
	}
	
 	def"getAttribute. TC to getg restrictionZipCodeAttributes isAttributeProductEnabled is true and productId is not empty "(){
		given:
		// spy for setting is attribute product enable
		pssdHelperObject = Spy()
		pssdHelperObject.setAttributeProductEnabled(true)
		pssdHelperObject.setCatalogTools(catalogToolsMock)
		String siteId = "siteId"
		String skuId = "skuId"
		String prodId = "productId"
	
		catalogToolsMock.getProductDetails(siteId, prodId) >> productVo
		
		catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >> ["Rescode"]
		         
		prodAttributeVO.setAttributeName("Rescode")
		
		//arrtibute list for product id
		List<AttributeVO> ProductIdAttributelist = new ArrayList<AttributeVO>()
		ProductIdAttributelist.add(prodAttributeVO)

		
		//attribute map
		Map<String,List<AttributeVO> > pAttributesMapForProductId = new HashMap<String,List<AttributeVO>>()
		pAttributesMapForProductId.put("at1", ProductIdAttributelist)
		
		productVo.setAttributesList(pAttributesMapForProductId)
		
		when:
	    Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	
		then:
		restrictionZipCodeAttributes.get("Rescode") == prodAttributeVO
		(restrictionZipCodeAttributes.get("Rescode")).getAttributeName() == "Rescode"
	} 
	
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when listOfRestrictedAttributes is null   "(){
		 given:
		 
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 skuAttributeVO.setAttributeName("Restrictionattribute")
		 // attrubuteVo list
		 List<AttributeVO> atlist = new ArrayList<AttributeVO>()
		 atlist.add(skuAttributeVO)
		 
		 //attrubute map
		 Map<String,List<AttributeVO> > pAttributesMap = new HashMap<String,List<AttributeVO>>()
		 pAttributesMap.put("at1", atlist)
		 //setting arrribut list to skuvo and productVo
		 skuVOMock.setSkuAttributes(pAttributesMap)
		 
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> skuVOMock
		 catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >> null
		 
	 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
	 }
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when listOfRestrictedAttributes is empty and SkudetailsVO is null   "(){
		 given:
		 
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 skuAttributeVO.setAttributeName("Restrictionattribute")
		 // attrubuteVo list
		 List<AttributeVO> atlist = new ArrayList<AttributeVO>()
		 atlist.add(skuAttributeVO)
		 
		 //attrubute map
		 Map<String,List<AttributeVO> > pAttributesMap = new HashMap<String,List<AttributeVO>>()
		 pAttributesMap.put("at1", atlist)
		 //setting arrribut list to skuvo and productVo
		 skuVOMock.setSkuAttributes(pAttributesMap)
		 // sku vo is null
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> null
		 //// listOfRestrictedAttributes is null
		 catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >> []
		 
	 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
	 }
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when skuAttributeVO is null   "(){
		 given:
		 
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 skuAttributeVO.setAttributeName("Restrictionattribute")
		 // attrubuteVo list
		 List<AttributeVO> atlist = new ArrayList<AttributeVO>()
		//// attribute VO is null ---- 
		 atlist.add(null)
		 
		 //attrubute map
		 Map<String,List<AttributeVO> > pAttributesMap = new HashMap<String,List<AttributeVO>>()
		 pAttributesMap.put("at1", atlist)
		 //setting arrribut list to skuvo and productVo
		 skuVOMock.setSkuAttributes(pAttributesMap)
		 
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> skuVOMock
		 catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >> null
		 
	 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
	 }
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when pAttributesMap is empty   "(){
		 given:
		 
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 skuAttributeVO.setAttributeName("Restrictionattribute")
		 
		 //attrubute map
		 Map<String,List<AttributeVO> > pAttributesMap = new HashMap<String,List<AttributeVO>>()
		 //setting arrribut list to skuvo and productVo
		 skuVOMock.setSkuAttributes(pAttributesMap)
		 
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> skuVOMock
		 catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >> null
		 
	 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
	 }
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when skuAttributeVO is not null but attribute name is null    "(){
		 given:
		 
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 // attribute name is null----
		 skuAttributeVO.setAttributeName(null)
		 // attrubuteVo list
		 List<AttributeVO> atlist = new ArrayList<AttributeVO>()
		 atlist.add(skuAttributeVO)
		 
		 //attrubute map
		 Map<String,List<AttributeVO> > pAttributesMap = new HashMap<String,List<AttributeVO>>()
		 pAttributesMap.put("at1", atlist)
		 //setting arrribut list to skuvo and productVo
		 skuVOMock.setSkuAttributes(pAttributesMap)
		 
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> skuVOMock
		 catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >> null
		 
	 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
	 }
	 
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when sku id  and catalogTools  is null "(){
		 given:
		 pssdHelperObject = Spy()
		 
		 String siteId = null
		 String skuId = null
		 String prodId = "productId"
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
	 }
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when sku id  is null "(){
		 given:
		 String siteId = null
		 String skuId = null
		 String prodId = "productId"
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
		 0*catalogToolsMock.getSKUDetails(_, _,true)
		 
	 }
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when catalogTools  is null "(){
		 given:
		 pssdHelperObject = Spy()
		 
		 String siteId =  "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
		 0*catalogToolsMock.getSKUDetails(_, _,true)
	 }
	 
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when isAttributeProductEnabled()  is false and prodId is null  "(){
		 given:
		 pssdHelperObject = Spy()
		 pssdHelperObject.setCatalogTools(catalogToolsMock)
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = null
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
		 0*catalogToolsMock.getProductDetails("siteId", _)
	 }
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes when isAttributeProductEnabled()  is true and prodId is null  "(){
		 given:
		 pssdHelperObject = Spy()
		 pssdHelperObject.setCatalogTools(catalogToolsMock)
		 pssdHelperObject.setAttributeProductEnabled(true)
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = null
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
		 0*catalogToolsMock.getProductDetails("siteId", _)
	 }
	 
	////////////////////////////Exception scenarion //////////////////////// 
	 
	 def"getAttribute. TC to get null restrictionZipCodeAttributes for BBBBusinessException while geting skuDetails  "(){
		 given:
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> {throw new BBBBusinessException("exception") }
		 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes == null
	 }
	 
	 def"getAttribute. TC to get null restrictionZipCodeAttributes for BBBSystemException while geting skuDetails  "(){
		 given:
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> {throw new BBBSystemException("exception") }
		 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes == null
	 }
	
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes for BBBSystemException while geting skuDetails  "(){
		 given:
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 skuAttributeVO.setAttributeName("Restrictionattribute")
		 // attrubuteVo list
		 List<AttributeVO> atlist = new ArrayList<AttributeVO>()
		 atlist.add(skuAttributeVO)
		 
		 //attrubute map
		 Map<String,List<AttributeVO> > pAttributesMap = new HashMap<String,List<AttributeVO>>()
		 pAttributesMap.put("at1", atlist)
		 //setting arrribut list to skuvo and productVo
		 skuVOMock.setSkuAttributes(pAttributesMap)
		 
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> skuVOMock
		 catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >>  {throw new BBBSystemException("exception") }
		 
	 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
	 }
	 
	 def"getAttribute. TC to get empty restrictionZipCodeAttributes for BBBBusinessException while geting skuDetails  "(){
		 given:
		 String siteId = "siteId"
		 String skuId = "skuId"
		 String prodId = "productId"
		 skuAttributeVO.setAttributeName("Restrictionattribute")
		 // attrubuteVo list
		 List<AttributeVO> atlist = new ArrayList<AttributeVO>()
		 atlist.add(skuAttributeVO)
		 
		 //attrubute map
		 Map<String,List<AttributeVO> > pAttributesMap = new HashMap<String,List<AttributeVO>>()
		 pAttributesMap.put("at1", atlist)
		 //setting arrribut list to skuvo and productVo
		 skuVOMock.setSkuAttributes(pAttributesMap)
		 
		 catalogToolsMock.getSKUDetails(siteId, skuId, true) >> skuVOMock
		 catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "listOfRestrictedAttributes") >>  {throw new BBBBusinessException("exception") }
		 
	 
		 when:
		 Map<String,AttributeVO> restrictionZipCodeAttributes = pssdHelperObject.getAttribute(siteId, skuId, prodId)
	 
		 then:
		 restrictionZipCodeAttributes.isEmpty()
	 }
}
