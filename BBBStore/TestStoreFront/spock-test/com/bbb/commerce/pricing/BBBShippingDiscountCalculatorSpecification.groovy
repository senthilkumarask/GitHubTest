package com.bbb.commerce.pricing

import java.util.ArrayList;
import java.util.List
import java.util.Locale;
import java.util.Map
import java.util.Set;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.ShippingPriceInfo;
import atg.core.util.Address
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor
import spock.lang.specification.BBBExtendedSpec
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBShippingPriceInfo;

class BBBShippingDiscountCalculatorSpecification extends BBBExtendedSpec{

	def ShippingGroup pShippingGrp =Mock()
	def BBBShippingDiscountCalculator shipCalc
	def RepositoryItem pPricingModel =Mock()
	def BBBCatalogTools cTools =Mock()
	def Order order = Mock()
	
	def setup(){
		shipCalc= Spy()
		shipCalc.setCatalogTools(cTools)
	}
	
	def"getExcludedPromoItemsForSG, checks if exclusion critirea is present"(){
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItemRelationship cRelation1=Mock()
		def CommerceItem cItem1 =Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cRelation1.getCommerceItem() >> cItem1
		cItem.getCatalogRefId() >>"refId"
		cItem1.getCatalogRefId() >>"refId1"
		
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		1*pShippingGrp.getCommerceItemRelationships() >> [cRelation,cRelation1]
		
		1*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		1*cTools.isGiftCardItem(order.getSiteId(), cItem1.getCatalogRefId()) >> false
		1*cTools.isSkuExcluded(cItem.getCatalogRefId(),pPricingModel.getPropertyValue("bbbCoupons"), false) >>true
		1*cTools.isSkuExcluded(cItem1.getCatalogRefId(),pPricingModel.getPropertyValue("bbbCoupons"), false) >>true
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order) 
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		2*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		list.contains(cItem) == true
		list.contains(cItem1) == true	
	}
	
	def"checks if ItemDescriptorName is equal to closenessQualifier"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		
		shipCalc.setLoggingDebug(true)
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closenessQualifier"
		pPricingModel.getPropertyValue("bbbCoupons") >>"couponCode"
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		list.isEmpty()==true
		0*shipCalc.logDebug("Leaving ShippingDiscountCalculator.getExcludedPromoItemsForSG() because coupon code is not valid hence items are not excluded from Free Shipping promotion")	
		
	}
	
	def"checks if shippingGroup is not an instance of HardGoodShippingGroup"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		
		shipCalc.setLoggingDebug(true)
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >>"couponCode"
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		list.isEmpty()==true
		0*shipCalc.logDebug("Leaving ShippingDiscountCalculator.getExcludedPromoItemsForSG() because coupon code is not valid hence items are not excluded from Free Shipping promotion")
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria");
		
		
	}
	
	def"checks if couponCode is empty"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		
		pPricingModel.getPropertyValue("bbbCoupons") >> ""
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		shipCalc.setLoggingDebug(true)
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		list.isEmpty()==true
		1*shipCalc.logDebug("Leaving ShippingDiscountCalculator.getExcludedPromoItemsForSG() because coupon code is not valid hence items are not excluded from Free Shipping promotion")
		
	}
	
	def"getExcludedPromoItemsForSG, checks if inclusion critirea is presentc,ommerce Item is an instance of NonMerchandisingCommerceItem"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def NonMerchandiseCommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		1*pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		2*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion
		def MutableRepository mRep =Mock()
		def RepositoryItem rItem =Mock()
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		list.contains(cItem) == false
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for check inclusion
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		1*shipCalc.logDebug("Coupon rule item or sku is null or No Rule(s) found for the Coupon : " + pPricingModel.getPropertyValue("bbbCoupons"));
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
		
	}
	
	def"getExcludedPromoItemsForSG, checks if inclusion critirea is present,commerce Item is not an instance of NonMerchandisingCommerceItem"(){
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		2*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> true
		
		//for inclusion
		def MutableRepository mRep =Mock()
		def RepositoryItem rItem =Mock()
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		list.contains(cItem) == false
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for check inclusion
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		1*shipCalc.logDebug("Coupon rule item or sku is null or No Rule(s) found for the Coupon : " + pPricingModel.getPropertyValue("bbbCoupons"));
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
	}
	
	def"getExcludedPromoItemsForSG, checks if inclusion critirea is present"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def RepositoryItem rItem =Mock()
		def RepositoryItem jdaDeptItem =Mock()
		def RepositoryItem jdaSubDeptItem =Mock()
		def RepositoryItem skuRepositoryItem =Mock()
		def MutableRepository mRep =Mock()
		def MutableRepository cRep1 =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		shipCalc.setCatalogRepository(cRep1)
		cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		1*cRep1.getItem(cItem.getCatalogRefId(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>skuRepositoryItem
		
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendor"
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaDeptItem
		2*jdaDeptItem.getRepositoryId() >>"123"
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubDeptItem
		2*jdaSubDeptItem.getRepositoryId() >>"456"
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "skuJdaClass"
		
		2*rItem.getRepositoryId() >>"50"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >>"refId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_VENDOR_ID) >> "ruleVendorId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_DEPT_ID) >> "ruleDeptId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SUB_DEPT_ID) >> "ruleSubDeptId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_CLASS) >> "ruleClass"
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		list.contains(cItem) == false
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		1*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId());
		1*shipCalc.logDebug("SKU Properties to be checked for inclusion : VendorId=" + "vendor" +", jdaDeptId=" + jdaDeptItem.getRepositoryId()
			+ ", jdaSubDeptId=" + jdaSubDeptItem.getRepositoryId() + ", jdaClass=" + "skuJdaClass")
		1*shipCalc.logDebug("Inclusive Coupon Rule properties for coupon " + rItem.getRepositoryId() + " are : SKU=" + "refId"
									+ ", VendorId=" + "ruleVendorId"
									+ ", jdaDeptId=" + "ruleDeptId"
									+ ", jdaSubDeptId=" + "ruleSubDeptId"
									+ ", jdaClass=" + "ruleClass")
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End")
	}
	
	def"getExcludedPromoItemsForSG, checks if checkInclusionItems is called and ruleSkuId is empty"(){ 
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def RepositoryItem rItem =Mock()
		def RepositoryItem jdaDeptItem =Mock()
		def RepositoryItem jdaSubDeptItem =Mock()
		def RepositoryItem skuRepositoryItem =Mock()
		def MutableRepository mRep =Mock()
		def MutableRepository cRep1 =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
		shipCalc.extractSiteId() >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		shipCalc.setCatalogRepository(cRep1)
		cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		cRep1.getItem(cItem.getCatalogRefId(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>skuRepositoryItem
		
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendor"
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> null
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "skuJdaClass"
		
		2*rItem.getRepositoryId() >>"50"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >>""
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_VENDOR_ID) >> "ruleVendorId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_DEPT_ID) >> "ruleDeptId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SUB_DEPT_ID) >> "ruleSubDeptId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_CLASS) >> "ruleClass"
		
		1*cTools.checkForDeptSubDeptClassInclusion("ruleDeptId", _, "ruleSubDeptId", _, "ruleClass", "skuJdaClass") >> true
		1*cTools.checkForVendorInclusion("ruleVendorId", "vendor", "ruleDeptId", true) >> false
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start")
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		1*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId())
		1*shipCalc.logDebug("SKU Properties to be checked for inclusion : VendorId=" + "vendor" +", jdaDeptId=" + null
			+ ", jdaSubDeptId=" + null + ", jdaClass=" + "skuJdaClass")
		1*shipCalc.logDebug("Inclusive Coupon Rule properties for coupon " + rItem.getRepositoryId() + " are : SKU=" + ""
									+ ", VendorId=" + "ruleVendorId"
									+ ", jdaDeptId=" + "ruleDeptId"
									+ ", jdaSubDeptId=" + "ruleSubDeptId"
									+ ", jdaClass=" + "ruleClass")
		list.contains(cItem) == true
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End")
	}
	
	def"getExcludedPromoItemsForSG, checks if checkInclusionItems is called and ruleSkuId is equal to zero"(){ 
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def RepositoryItem rItem =Mock()
		def RepositoryItem jdaDeptItem =Mock()
		def RepositoryItem jdaSubDeptItem =Mock()
		def RepositoryItem skuRepositoryItem =Mock()
		def MutableRepository mRep =Mock()
		def MutableRepository cRep1 =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
		shipCalc.extractSiteId() >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		shipCalc.setCatalogRepository(cRep1)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		1*cRep1.getItem(cItem.getCatalogRefId(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>skuRepositoryItem
		
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendor"
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> null
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "skuJdaClass"
		
		2*rItem.getRepositoryId() >>"50"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >>"0"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_VENDOR_ID) >> "ruleVendorId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_DEPT_ID) >> "ruleDeptId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SUB_DEPT_ID) >> "ruleSubDeptId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_CLASS) >> "ruleClass"
		
		1*cTools.checkForDeptSubDeptClassInclusion("ruleDeptId", _, "ruleSubDeptId", _, "ruleClass", "skuJdaClass") >> true
		1*cTools.checkForVendorInclusion("ruleVendorId", "vendor", "ruleDeptId", true) >> true
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		1*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId());
		1*shipCalc.logDebug("SKU Properties to be checked for inclusion : VendorId=" + "vendor" +", jdaDeptId=" + null
			+ ", jdaSubDeptId=" + null + ", jdaClass=" + "skuJdaClass")
		1*shipCalc.logDebug("Inclusive Coupon Rule properties for coupon " + rItem.getRepositoryId() + " are : SKU=" + "0"
									+ ", VendorId=" + "ruleVendorId"
									+ ", jdaDeptId=" + "ruleDeptId"
									+ ", jdaSubDeptId=" + "ruleSubDeptId"
									+ ", jdaClass=" + "ruleClass")
		list.contains(cItem) == false
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
	}
	
	def"getExcludedPromoItemsForSG, checks if checkInclusionItems is called and ruleSkuId is not equal to skuId"(){
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def RepositoryItem rItem =Mock()
		def RepositoryItem jdaDeptItem =Mock()
		def RepositoryItem jdaSubDeptItem =Mock()
		def RepositoryItem skuRepositoryItem =Mock()
		def MutableRepository mRep =Mock()
		def MutableRepository cRep1 =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
		shipCalc.extractSiteId() >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		shipCalc.setCatalogRepository(cRep1)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		1*cRep1.getItem(cItem.getCatalogRefId(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>skuRepositoryItem
		
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendor"
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> null
		1*skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "skuJdaClass"
		
		2*rItem.getRepositoryId() >>"50"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >>"123"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_VENDOR_ID) >> "ruleVendorId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_DEPT_ID) >> "ruleDeptId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SUB_DEPT_ID) >> "ruleSubDeptId"
		1*rItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_CLASS) >> "ruleClass"
		
		1*cTools.checkForDeptSubDeptClassInclusion("ruleDeptId", _, "ruleSubDeptId", _, "ruleClass", "skuJdaClass") >> true
		1*cTools.checkForVendorInclusion("ruleVendorId", "vendor", "ruleDeptId", true) >> true
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		1*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId());
		1*shipCalc.logDebug("SKU Properties to be checked for inclusion : VendorId=" + "vendor" +", jdaDeptId=" + null
			+ ", jdaSubDeptId=" + null + ", jdaClass=" + "skuJdaClass")
		1*shipCalc.logDebug("Inclusive Coupon Rule properties for coupon " + rItem.getRepositoryId() + " are : SKU=" + "123"
									+ ", VendorId=" + "ruleVendorId"
									+ ", jdaDeptId=" + "ruleDeptId"
									+ ", jdaSubDeptId=" + "ruleSubDeptId"
									+ ", jdaClass=" + "ruleClass")
		list.contains(cItem) == false
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
	}
	
	def"getExcludedPromoItemsForSG, checks if checkInclusionItems is called when skuRepositoryItem is null"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		2*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def RepositoryItem rItem =Mock()
		def MutableRepository mRep =Mock()
		def MutableRepository cRep1 =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
		shipCalc.extractSiteId() >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		shipCalc.setCatalogRepository(cRep1)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		1*cRep1.getItem(cItem.getCatalogRefId(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>null
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		1*shipCalc.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		0*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId());
		list.contains(cItem) == false
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
	}
	
	def"getExcludedPromoItemsForSG, checks if checkInclusionItems is called when couponRuleItem List is null"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		1*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def MutableRepository mRep =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
		shipCalc.extractSiteId() >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> null
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		0*shipCalc.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		0*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		0*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId());
		list.contains(cItem) == false
		1*shipCalc.logDebug("Coupon rule item or sku is null or No Rule(s) found for the Coupon : " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
	}
	
	def"getExcludedPromoItemsForSG, checks if checkInclusionItems is called when couponRuleItem List is empty"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		1*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def MutableRepository mRep =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
		shipCalc.extractSiteId() >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> []
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		0*shipCalc.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		0*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		0*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId());
		list.contains(cItem) == false
		1*shipCalc.logDebug("Coupon rule item or sku is null or No Rule(s) found for the Coupon : " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
	}
	
	def"getExcludedPromoItemsForSG, checks if checkInclusionItems is called when skuId is empty"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>""
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		2*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def MutableRepository mRep =Mock()
		def RepositoryItem rItem =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
		shipCalc.extractSiteId() >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		0*shipCalc.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		0*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId());
		list.contains(cItem) == false
		1*shipCalc.logDebug("Coupon rule item or sku is null or No Rule(s) found for the Coupon : " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
	}
	
	def"getExcludedPromoItemsForSG, checks if checkInclusionItems is called and RepositoryException is thrown"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"123"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		2*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		//for inclusion method
		def MutableRepository mRep =Mock()
		def MutableRepository mRep1 =Mock()
		def RepositoryItem rItem =Mock()
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
		shipCalc.extractSiteId() >> "BBB"
		shipCalc.setCouponRuleQuery("ruleQuery")
		shipCalc.setCouponRepository(mRep)
		shipCalc.setCatalogRepository(mRep1)
		1*cTools.executeRQLQuery(shipCalc.getCouponRuleQuery(), _,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR,shipCalc.getCouponRepository()) >> [rItem]
		mRep1.getItem(cItem.getCatalogRefId(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("")}
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		1*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + pPricingModel.getPropertyValue("bbbCoupons"))
		
		//for inclusion method
		1*shipCalc.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");
		0*shipCalc.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1*shipCalc.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pPricingModel.getPropertyValue("bbbCoupons") + "]")
		0*shipCalc.logDebug("Checking the coupon inclusion rule for coupon id : "+ pPricingModel.getPropertyValue("bbbCoupons") + " for the sku id" + cItem.getCatalogRefId());
		1*shipCalc.logError("BBBShippingDiscountCalculator Method Name checkInclusionItems() : RepositoryException ");
		list.contains(cItem) == false
	}
	
	
	
	def"getExcludedPromoItemsForSG, checks if RepositoryException is thrown"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getItemDescriptor() >> {throw new RepositoryException("")}
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		0*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + _)
		0*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + _)
		list.isEmpty()==true
	}
	
	def"getExcludedPromoItemsForSG, checks if BBBSystemException is thrown"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		1*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> {throw new BBBSystemException("")}
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + _)
		0*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + _)
		//1*shipCalc.logError("BBBSystem Exception occured while identifying a promotional item : atg.repository.RepositoryException: , null")
		list.isEmpty()==true
	}
	
	def"getExcludedPromoItemsForSG, checks if BBBBusinessException is thrown"(){
		
		given:
		def RepositoryItemDescriptor descriptor =Mock()
		def HardgoodShippingGroup pShippingGrp =Mock()
		def CommerceItemRelationship cRelation=Mock()
		def CommerceItem cItem =Mock()
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		order.getSiteId() >>"tbs"
		cRelation.getCommerceItem() >> cItem
		cItem.getCatalogRefId() >>"refId"
		
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "closeness"
		pPricingModel.getPropertyValue("bbbCoupons") >> "couponCode"
		pShippingGrp.getCommerceItemRelationships() >> [cRelation]
		1*cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		1*cTools.isSkuExcluded(cItem.getCatalogRefId(),pPricingModel.getPropertyValue("bbbCoupons"), false) >> {throw new BBBBusinessException("")}
		
		when:
		List<CommerceItem> list = shipCalc.getExcludedPromoItemsForSG(pShippingGrp, pPricingModel, order)
		
		then:
		1*shipCalc.logDebug("Coupon id " + pPricingModel.getPropertyValue("bbbCoupons") + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria")
		0*shipCalc.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + _)
		0*shipCalc.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + _)
		//1*shipCalc.logError("BBBBusiness Exception occured while identifying a promotional item : atg.repository.RepositoryException: , null")
		list.isEmpty()==true
	}
	
	def"priceShippingGroup, happy path"(){
		
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def Address add= Mock()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale 
		
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem) 
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		
		Set<CommerceItem> excludedCommItems = new HashSet()
		excludedCommItems.add(c1)
		
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		
		pOrder.getExcludedPromotionMap() >> excludedMap
		
		Map<String,String> m = new HashMap()
		pShipment.getSpecialInstructions()>> m
		pShipment.getShippingMethod() >> "express"
		
		pShipment.getCommerceItemRelationships() >>[cRelation1,cRelation2]
		cRelation2.getCommerceItem() >>cItem1
		cRelation1.getCommerceItem() >> cItem
		cRelation1.getQuantity() >>5
		cItem.getId()>>"1"
		cItem1.getId()>>"2"
		cItem.getCatalogRefId() >>"Id1"
		cItem1.getCatalogRefId() >>"Id2"
	
		c1.getId() >>"1"
		pOrder.getSiteId() >>"tbs"
		
		1*cTools.isFreeShipping(pOrder.getSiteId(),cItem.getCatalogRefId(), pShipment.getShippingMethod()) >>false
		2*cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())>> 500
		
		shipCalc.setPricingTools(pTools)
		pTools.getShipItemRelPriceTotal(cRelation1,"amount") >>400
		
		pShipment.getShippingAddress() >> add
		add.getState() >>"state"
		
		cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), add.getState(), 400, null) >> 50
		pTools.round(cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), add.getState(), 400, null)) >>50
		pTools.round(cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())*cRelation1.getQuantity() +cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), add.getState(), 400, null)) >>2550
		pTools.round(_) >> 2450.0
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
	    1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == ship1
		shipmap.containsKey(ship1)==false
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		Map<String, String> map = pShipment.getSpecialInstructions()
		map.containsKey(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS) == true
		map.containsValue("c1,") == true
		1*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 2450.0
	}
	
	def"priceShippingGroup,when new amount is zero"(){
		
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def Address add= Mock()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem)
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		
		Set<CommerceItem> excludedCommItems = new HashSet()
		excludedCommItems.add(c1)
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		pOrder.getExcludedPromotionMap() >> excludedMap
		Map<String,String> m = new HashMap()
		pShipment.getSpecialInstructions()>> m
		pShipment.getShippingMethod() >> "express"
		
		pShipment.getCommerceItemRelationships() >>[cRelation1,cRelation2]
		cRelation2.getCommerceItem() >>cItem1
		cRelation1.getCommerceItem() >> cItem
		cRelation1.getQuantity() >>5
		cItem.getId()>>"1"
		cItem1.getId()>>"2"
		cItem.getCatalogRefId() >>"Id1"
		cItem1.getCatalogRefId() >>"Id2"
		c1.getId() >>"1"
		pOrder.getSiteId() >>"tbs"
		
		1*cTools.isFreeShipping(pOrder.getSiteId(),cItem.getCatalogRefId(), pShipment.getShippingMethod()) >>false
		2*cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())>> 0.0
		shipCalc.setPricingTools(pTools)
		pTools.getShipItemRelPriceTotal(cRelation1,"amount") >>400
		pShipment.getShippingAddress() >> add
		add.getState() >>"state"
		
		cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), add.getState(), 400, null) >> 0.0
		pTools.round(cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), add.getState(), 400, null)) >>0.0
		pTools.round(cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())*cRelation1.getQuantity() +cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), add.getState(), 400, null)) >>0.0
		pTools.round(_) >> 2450.0
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == ship1
		shipmap.containsKey(ship1)==false
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		Map<String, String> map = pShipment.getSpecialInstructions()
		map.containsKey(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS) == true
		map.containsValue("c1,") == true
		1*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
	}
	
	def"priceShippingGroup, when shipingmethod is null"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem)
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		
		Set<CommerceItem> excludedCommItems = new HashSet()
		excludedCommItems.add(c1)
		
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		
		pOrder.getExcludedPromotionMap() >> excludedMap
		
		Map<String,String> m = new HashMap()
		pShipment.getSpecialInstructions()>> m
		pShipment.getShippingMethod() >> "express"
		
		pShipment.getCommerceItemRelationships() >>[cRelation1,cRelation2]
		cRelation2.getCommerceItem() >>cItem1
		cRelation1.getCommerceItem() >> cItem
		cRelation1.getQuantity() >>5
		cItem.getId()>>"1"
		cItem1.getId()>>"2"
		cItem.getCatalogRefId() >>"Id1"
		cItem1.getCatalogRefId() >>"Id2"
	
		c1.getId() >>"1"
		pOrder.getSiteId() >>"tbs"
		
		1*cTools.isFreeShipping(pOrder.getSiteId(),cItem.getCatalogRefId(), pShipment.getShippingMethod()) >>false
		2*cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())>> 500
		
		shipCalc.setPricingTools(pTools)
		pTools.getShipItemRelPriceTotal(cRelation1,"amount") >>400
		
		pShipment.getShippingAddress() >> null
		pTools.round(cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())*cRelation1.getQuantity() +cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), null, 400, null)) >>2500

		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == ship1
		shipmap.containsKey(ship1)==false
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		Map<String, String> map = pShipment.getSpecialInstructions()
		map.containsKey(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS) == true
		map.containsValue("c1,") == true
		1*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 2500.0
	}
	
	def"priceShippingGroup, when pShipping is not an instance of hardgood shipping group"(){
		
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def ShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale

		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem)
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		
		Set<CommerceItem> excludedCommItems = new HashSet()
		excludedCommItems.add(c1)
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		pOrder.getExcludedPromotionMap() >> excludedMap
		Map<String,String> m = new HashMap()
		pShipment.getSpecialInstructions()>> m
		pShipment.getShippingMethod() >> "express"
		
		pShipment.getCommerceItemRelationships() >>[cRelation1,cRelation2]
		cRelation2.getCommerceItem() >>cItem1
		cRelation1.getCommerceItem() >> cItem
		cRelation1.getQuantity() >>5
		cItem.getId()>>"1"
		cItem1.getId()>>"2"
		cItem.getCatalogRefId() >>"Id1"
		cItem1.getCatalogRefId() >>"Id2"
		c1.getId() >>"1"
		pOrder.getSiteId() >>"tbs"
		
		1*cTools.isFreeShipping(pOrder.getSiteId(),cItem.getCatalogRefId(), pShipment.getShippingMethod()) >>false
		2*cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())>> 500
		shipCalc.setPricingTools(pTools)
		pTools.getShipItemRelPriceTotal(cRelation1,"amount") >>400
		pTools.round(cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())*cRelation1.getQuantity() +cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), null, 400, null)) >>2500
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == ship1
		shipmap.containsKey(ship1)==false
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		Map<String, String> map = pShipment.getSpecialInstructions()
		map.containsKey(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS) == true
		map.containsValue("c1,") == true
		1*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		//adj.getTotalAdjustment() == 2450.0
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 2500.0
	}
	
	def"priceShippingGroup, when item total is zero"(){
		
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def Address add= Mock()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)

		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem)
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		
		Set<CommerceItem> excludedCommItems = new HashSet()
		excludedCommItems.add(c1)
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		pOrder.getExcludedPromotionMap() >> excludedMap
		Map<String,String> m = new HashMap()
		pShipment.getSpecialInstructions()>> m
		pShipment.getShippingMethod() >> "express"
		
		pShipment.getCommerceItemRelationships() >>[cRelation1,cRelation2]
		cRelation2.getCommerceItem() >>cItem1
		cRelation1.getCommerceItem() >> cItem
		cRelation1.getQuantity() >>5
		cItem.getId()>>"1"
		cItem1.getId()>>"2"
		cItem.getCatalogRefId() >>"Id1"
		cItem1.getCatalogRefId() >>"Id2"
	
		c1.getId() >>"1"
		pOrder.getSiteId() >>"tbs"
		
		1*cTools.isFreeShipping(pOrder.getSiteId(),cItem.getCatalogRefId(), pShipment.getShippingMethod()) >>false
		2*cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())>> 500
		shipCalc.setPricingTools(pTools)
		pTools.getShipItemRelPriceTotal(cRelation1,"amount") >>0.0
		
		pShipment.getShippingAddress() >> add
		add.getState() >>"state"
		
		pTools.round(cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())*cRelation1.getQuantity() +cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), null, 400, null)) >>2500
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == ship1
		shipmap.containsKey(ship1)==false
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		Map<String, String> map = pShipment.getSpecialInstructions()
		map.containsKey(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS) == true
		map.containsValue("c1,") == true
		1*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 2500.0
	}
	
	def"priceShippingGroup, when BBBSystemException is thrown"(){
		
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def Address add= Mock()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem)
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		
		Set<CommerceItem> excludedCommItems = new HashSet()
		excludedCommItems.add(c1)
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		pOrder.getExcludedPromotionMap() >> excludedMap
		Map<String,String> m = new HashMap()
		pShipment.getSpecialInstructions()>> m
		pShipment.getShippingMethod() >> "express"
		
		pShipment.getCommerceItemRelationships() >>[cRelation1,cRelation2]
		cRelation2.getCommerceItem() >>cItem1
		cRelation1.getCommerceItem() >> cItem
		cRelation1.getQuantity() >>5
		cItem.getId()>>"1"
		cItem1.getId()>>"2"
		cItem.getCatalogRefId() >>"Id1"
		cItem1.getCatalogRefId() >>"Id2"
	
		c1.getId() >>"1"
		pOrder.getSiteId() >>"tbs"
		cTools.isFreeShipping(pOrder.getSiteId(),cItem.getCatalogRefId(), pShipment.getShippingMethod()) >>{throw new BBBSystemException("")}
		pShipment.getShippingAddress() >> add
		add.getState() >>"state"
		pTools.round(cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())*cRelation1.getQuantity() +cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), null, 400, null)) >>2500
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == ship1
		shipmap.containsKey(ship1)==false
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		Map<String, String> map = pShipment.getSpecialInstructions()
		map.containsKey(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS) == true
		map.containsValue("c1,") == true
		1*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() ==0.0
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
		1*shipCalc.logError("BBBSystem Exception occured while invoking catalogAPI : "	, _)
	}
	
	def"priceShippingGroup, when BBBBusinessException is thrown"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def Address add= Mock()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
	
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem)
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		
		Set<CommerceItem> excludedCommItems = new HashSet()
		excludedCommItems.add(c1)
		
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		pOrder.getExcludedPromotionMap() >> excludedMap
		Map<String,String> m = new HashMap()
		pShipment.getSpecialInstructions()>> m
		pShipment.getShippingMethod() >> "express"
		
		pShipment.getCommerceItemRelationships() >>[cRelation1,cRelation2]
		cRelation2.getCommerceItem() >>cItem1
		cRelation1.getCommerceItem() >> cItem
		cRelation1.getQuantity() >>5
		cItem.getId()>>"1"
		cItem1.getId()>>"2"
		cItem.getCatalogRefId() >>"Id1"
		cItem1.getCatalogRefId() >>"Id2"
	
		c1.getId() >>"1"
		pOrder.getSiteId() >>"tbs"
		cTools.isFreeShipping(pOrder.getSiteId(),cItem.getCatalogRefId(), pShipment.getShippingMethod()) >>{throw new BBBBusinessException("")}
		pShipment.getShippingAddress() >> add
		add.getState() >>"state"
		pTools.round(cTools.getSKUSurcharge(pOrder.getSiteId(), cItem.getCatalogRefId(), pShipment.getShippingMethod())*cRelation1.getQuantity() +cTools.getShippingFee(pOrder.getSiteId(), pShipment.getShippingMethod(), null, 400, null)) >>2500
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == ship1
		shipmap.containsKey(ship1)==false
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		Map<String, String> map = pShipment.getSpecialInstructions()
		map.containsKey(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS) == true
		map.containsValue("c1,") == true
		1*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() ==0.0
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
		1*shipCalc.logError("BBBBusiness Exception occured while invoking catalogAPI : ", _)
	}
	
	def"priceShippingGroup, when is free shipping flag is true"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem)
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		
		Set<CommerceItem> excludedCommItems = new HashSet()
		excludedCommItems.add(c1)
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		
		pOrder.getExcludedPromotionMap() >> excludedMap
		
		Map<String,String> m = new HashMap()
		pShipment.getSpecialInstructions()>> m
		pShipment.getShippingMethod() >> "express"
		
		pShipment.getCommerceItemRelationships() >>[cRelation1,cRelation2]
		cRelation2.getCommerceItem() >>cItem1
		cRelation1.getCommerceItem() >> cItem
		cRelation1.getQuantity() >>5
		cItem.getId()>>"1"
		cItem1.getId()>>"2"
		cItem.getCatalogRefId() >>"Id1"
		cItem1.getCatalogRefId() >>"Id2"
	
		c1.getId() >>"1"
		pOrder.getSiteId() >>"tbs"
		
		cTools.isFreeShipping(pOrder.getSiteId(),cItem.getCatalogRefId(), pShipment.getShippingMethod()) >>true
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == ship1
		shipmap.containsKey(ship1)==false
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		Map<String, String> map = pShipment.getSpecialInstructions()
		map.containsKey(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS) == true
		map.containsValue("c1,") == true
		1*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
	}
	
	def"priceShippingGroup, when excluded commerce Item list is null"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(null)
		rItem.getRepositoryId() >>"123"
		c1.getCatalogRefId()>>"c1"
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),null)
		pOrder.getExcludedPromotionMap() >> excludedMap
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",null, _);
		adj.getCoupon() == null
		shipmap.containsKey(ship1)==true
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
	}
	
	def"priceShippingGroup, when excluded commerce Item list is empty"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		adj.setPricingModel(rItem)
		rItem.getRepositoryId() >>"234"
		c1.getCatalogRefId()>>"c1"
		Set<CommerceItem> excludedCommItems = new HashSet()
		Map<String, Set<CommerceItem>> excludedMap = new HashMap()
		excludedMap.put(ship1.getRepositoryId(),excludedCommItems)
		pOrder.getExcludedPromotionMap() >> excludedMap
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		1*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		shipmap.containsKey(ship1)==true
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
	}
	
	def"priceShippingGroup, when  coupon is null"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		pPriceQuote.getAdjustments().add(adj)
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
	}
	
	def"priceShippingGroup, when item discount flag is false"(){
		
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()
		def CommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def ShippingGroupCommerceItemRelationship cRelation1 =Mock()
		def CommerceItemRelationship cRelation2 =Mock()
		def RepositoryItem rItem =Mock()
		def CommerceItem c1 =Mock()
		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(false); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		pPriceQuote.getAdjustments().add(adj)
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		shipmap.containsKey(ship1)==true
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
	}
	
	def"priceShippingGroup, when adjustment list is empty"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def HardgoodShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()
		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		
		1*shipCalc.extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, pPriceQuote) >> {pPriceQuote.setDiscounted(true); pPriceQuote.setAmount(100); pPriceQuote.setFinalSurcharge(100) }
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		1*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		shipmap.containsKey(ship1)==true
		1*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 100.0
		pPriceQuote.getAmount() == 200.0
	}
	
	def"priceShippingGroup, when shipment is an instance of BBBStoreShippingGroup"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def BBBStoreShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		def BBBPricingTools pTools = Mock()

		Locale pLocale
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "123")
		shipmap.put(ship2, "2")
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		0*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		shipmap.containsKey(ship1)==false
		0*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 100.0
	}
	
	def"priceShippingGroup, when discount flag is true before the super call"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def ShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def RepositoryItem ship2 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		
		Locale pLocale
		Map<RepositoryItem,String> shipmap = new HashMap()
		shipmap.put(ship1, "22")
		shipmap.put(ship2, "2")
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		ship1.getRepositoryId() >>"repId"
		pPriceQuote.setDiscounted(true)
		pPriceQuote.setAmount(100)
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		0*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		shipmap.containsKey(ship1)==true
		0*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 100.0
	}
	
	def"priceShippingGroup, when shipmap is null"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def BBBStoreShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		Locale pLocale
		
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, null)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		0*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		0*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 100.0
	}
	
	def"priceShippingGroup, when pricing model is null"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def BBBStoreShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		Locale pLocale
		
		Map<String,String> shipmap = new HashMap()
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, null)
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(100)
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, null, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		0*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 100.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		0*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 100.0
	}
	
	def"priceShippingGroup, when amount retrieved form priceQoute is zero"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def ShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def RepositoryItem ship1 =Mock()
		def PricingAdjustment adj =new PricingAdjustment()
		Locale pLocale
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, null)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> "123"
		pPriceQuote.setDiscounted(false)
		pPriceQuote.setAmount(0)
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		0*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 0.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		0*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 0.0
	}
	
	def"priceShippingGroup, when TBS_pricingWebserviceOrder value is true"(){
		given:
		def BBBShippingPriceInfo pPriceQuote = new BBBShippingPriceInfo()
		def BBBStoreShippingGroup pShipment =Mock()
		def RepositoryItem pProfile =Mock()
		def BBBOrderImpl pOrder =Mock()
		def PricingAdjustment adj =new PricingAdjustment()

		Locale pLocale
		
		Map<RepositoryItem,String> shipmap = new HashMap()
		
		Map<String, Object> pExtraParameters = new HashMap<String,Object>()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, shipmap)
		pExtraParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "true")
		
		shipCalc.setLoggingDebug(true)
		shipCalc.setLoggingError(true)
		
		pPricingModel.getRepositoryId() >> null
		
		when:
		shipCalc.priceShippingGroup(pOrder, pPriceQuote,pShipment, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*shipCalc.logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER))
		0*shipCalc.logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = 0.0\n shippingPriceInfo.getFinalShipping() = 0.0\n pPriceQuote.getAmount() = 0.0")
		0*shipCalc.vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",_, _);
		adj.getCoupon() == null
		0*shipCalc.logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = 100.0\n shippingPriceInfo.getFinalShipping() = 100.0\n shippingPriceInfo.getAmount() = 200.0")
		0*shipCalc.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ c1, ]")
		adj.getTotalAdjustment() == 0.0
		pPriceQuote.getFinalShipping() == 0.0
		pPriceQuote.getAmount() == 0.0
	}
	
}
