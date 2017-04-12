package com.bbb.commerce.pricing

import java.util.Locale;
import java.util.Map
import atg.core.util.Range
import com.bbb.account.BBBProfileTools.CItemInfo;
import com.bbb.commerce.pricing.calculator.BBBOrderSubtotalCalculator
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBDetailedItemPriceInfo
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.FilteredCommerceItem
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.PricingTools
import atg.commerce.pricing.TaxPriceInfo;
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;

class BBBPostOrderDpiMergerCalculatorSpecification extends BBBExtendedSpec{

	BBBPostOrderDpiMergerCalculator dpiCalculator
	BBBCommerceItem bbbCommerceItems = Mock()
	PricingTools pTools = new PricingTools()
	OrderPriceInfo pPriceQuote =Mock()
	RepositoryItem pPricingModel = Mock()
	RepositoryItem pProfile = Mock()
	TaxPriceInfo taxInfo=Mock()
	CommerceItem cItem =Mock()
	ItemPriceInfo priceInfo =Mock()
	EcoFeeCommerceItem ecoFeeItems =Mock()
	GiftWrapCommerceItem giftWrapItem = Mock()
	LTLDeliveryChargeCommerceItem ltlItem =Mock()
	LTLAssemblyFeeCommerceItem ltlAssembelyItem = Mock()
	Order pOrder =Mock()
	Map<String,Object> pExtraParameters = new HashMap()
	DetailedItemPriceInfo dpItem = Mock()
	DetailedItemPriceInfo dpItem2 = Mock()
	List<DetailedItemPriceInfo> dpiList
	Range range =Mock()
	Range range1 =Mock()

	Locale pLocale

	def setup(){
		dpiCalculator =new BBBPostOrderDpiMergerCalculator()
		dpiCalculator.setPricingTools(pTools)
		dpiList = new ArrayList()
		dpiList.add(dpItem)
		dpiList.add(dpItem)
		dpiList.add(dpItem2)
	}

	/**
	 * sets common parameters
	 * @return
	 */
	private setParameters(){
		dpiCalculator.setLoggingDebug(true)
		FilteredCommerceItem fitem =Mock()
		1*fitem.getWrappedItem() >> cItem
		pExtraParameters.put("FilteredWrappedItems",[fitem])
	}


	def"priceOrder, when filteredItemsList is null"(){

		given:
		dpiCalculator = Spy()
		dpiCalculator.setLoggingDebug(true)
		FilteredCommerceItem fitem =Mock()
		pExtraParameters.put("FilteredWrappedItems",null)

		0*fitem.getWrappedItem() >> cItem

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*dpiCalculator.logDebug(LogMessageFormatter.formatMessage(null,"Start - BBBPostOrderDpiMergerCalculator.priceOrder"))
		1*dpiCalculator.logDebug(LogMessageFormatter.formatMessage(null,"End- BBBPostOrderDpiMergerCalculator.priceOrder"))
	}

	def"priceOrder, when dpiList is null"(){

		given:
		dpiCalculator = Spy()
		dpiCalculator.setLoggingDebug(true)
		FilteredCommerceItem fitem =Mock()
		1*fitem.getWrappedItem() >> cItem
		pExtraParameters.put("FilteredWrappedItems",[fitem])
		pExtraParameters.put(cItem.getId(),null)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		0*cItem.getPriceInfo()>>priceInfo

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*dpiCalculator.logDebug(LogMessageFormatter.formatMessage(null,"Corresponding DPI list for CommerceItem - "+ cItem.getCatalogRefId() + "is null or empty"))
		1*dpiCalculator.logDebug(LogMessageFormatter.formatMessage(null,"End- BBBPostOrderDpiMergerCalculator.priceOrder"))
	}

	def"priceOrder, when rangeBounds are not equal(updateExistingDPI) and sourceAdjusmentCoupon and compareAdjustmentCoupon are not null, repositoryID's are not equal(removeDuplicatePricingAdjustments)"(){

		given:
		setParameters()

		// for mergeDPIListForCommerceItem
		pExtraParameters.put("id",dpiList)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		cItem.getPriceInfo() >> priceInfo

		//for mergeSameDPI in mergeDPIListForCommerceItem
		BBBDetailedItemPriceInfo existingcommItemDPI = Mock()
		dpItem.getRange() >>  range
		dpItem2.getRange() >> range1

		priceInfo.getCurrentPriceDetailsForRange(range) >> [existingcommItemDPI]
		priceInfo.getCurrentPriceDetailsForRange(range1) >> [existingcommItemDPI]
		existingcommItemDPI.getRange() >> range
		range.getHighBound() >> 10
		range.getSize() >> 50

		//for updateExistingDpi
		PricingAdjustment adj =Mock()
		PricingAdjustment adj1 =Mock()
		priceInfo.getAmount() >> 500.0
		dpItem.getAmount() >> 100.0
		dpItem.getQuantity() >> 2
		range.getLowBound() >> 5
		existingcommItemDPI.getAmount() >> 700.0
		existingcommItemDPI.getQuantity() >> 1

		dpItem2.getAmount() >> 20      //for 2nd iteration
		dpItem2.getQuantity() >> 5
		range1.getHighBound() >> 5
		range1.getLowBound() >> 5
		priceInfo.getCurrentPriceDetails() >>[]
		dpItem2.getAdjustments() >>[]

		existingcommItemDPI.getAdjustments() >> []
		dpItem.getAdjustments() >> [adj,adj1]

		//removeDuplicatePricingAdjustments
		RepositoryItem rep =Mock()
		RepositoryItem rep1 =Mock()
		RepositoryItem coupon =Mock()
		RepositoryItem coupon1 =Mock()

		adj.getPricingModel() >>rep
		adj1.getPricingModel() >>rep1
		rep.getRepositoryId() >>"repID"
		rep1.getRepositoryId() >>"repID"
		adj.getCoupon() >>coupon
		adj1.getCoupon() >>coupon1
		coupon.getRepositoryId() >> "cId"
		coupon1.getRepositoryId() >> "cId1"
		adj.getTotalAdjustment() >> 50
		adj1.getTotalAdjustment() >> 150

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*range.setHighBound(11)
		1*dpItem.setQuantity(50);
		1*priceInfo.setAmount(300.0)
		1*existingcommItemDPI.setAmount(600.0)
		2*existingcommItemDPI.setDiscounted(true)

		1*priceInfo.setAmount(400.0) // for 2nd iteration
		1*existingcommItemDPI.setAmount(3500)
		1*existingcommItemDPI.setQuantity(5)
		1*range.setHighBound(9)
		BBBDetailedItemPriceInfo newDpi =priceInfo.getCurrentPriceDetails().get(0)
		newDpi.getRange() != null
		newDpi.getRange().getHighBound() ==10
		newDpi.getRange().getLowBound() ==10
		newDpi.getQuantity() ==1
		newDpi.getAmount() == -2800
		1*existingcommItemDPI.setAmount(680.0)

		//removeDuplicatePricingAdjustments
		0*adj.setTotalAdjustment(200)
		existingcommItemDPI.getAdjustments() == [adj,adj1]
	}

	def"priceOrder, when rangeBounds are not equal(updateExistingDPI) , compareAdjustment.getPricingModel is null, repositoryID's are not equal(removeDuplicatePricingAdjustments)"(){

		given:
		setParameters()

		// for mergeDPIListForCommerceItem
		pExtraParameters.put("id",dpiList)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		cItem.getPriceInfo() >> priceInfo

		//for mergeSameDPI in mergeDPIListForCommerceItem
		BBBDetailedItemPriceInfo existingcommItemDPI = Mock()
		dpItem.getRange() >>  range
		dpItem2.getRange() >> range1

		priceInfo.getCurrentPriceDetailsForRange(range) >> [existingcommItemDPI]
		priceInfo.getCurrentPriceDetailsForRange(range1) >> [existingcommItemDPI]
		existingcommItemDPI.getRange() >> range
		range.getHighBound() >> 10
		range.getSize() >> 50

		//for updateExistingDpi
		PricingAdjustment adj =Mock()
		PricingAdjustment adj1 =Mock()
		priceInfo.getAmount() >> 500.0
		dpItem.getAmount() >> 100.0
		dpItem.getQuantity() >> 2
		range.getLowBound() >> 5
		existingcommItemDPI.getAmount() >> 700.0
		existingcommItemDPI.getQuantity() >> 1

		dpItem2.getAmount() >> 20      //for 2nd iteration
		dpItem2.getQuantity() >> 5
		range1.getHighBound() >> 5
		range1.getLowBound() >> 5
		priceInfo.getCurrentPriceDetails() >>[]
		dpItem2.getAdjustments() >>[]

		existingcommItemDPI.getAdjustments() >> []
		dpItem.getAdjustments() >> [adj,adj1]

		//removeDuplicatePricingAdjustments
		RepositoryItem rep =Mock()
		2*adj.getPricingModel() >>rep
		2*adj1.getPricingModel() >>null
		0*adj.getTotalAdjustment() >> 50
		0*adj1.getTotalAdjustment() >> 150

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*range.setHighBound(11)
		1*dpItem.setQuantity(50);
		1*priceInfo.setAmount(300.0)
		1*existingcommItemDPI.setAmount(600.0)
		2*existingcommItemDPI.setDiscounted(true)

		1*priceInfo.setAmount(400.0) // for 2nd iteration
		1*existingcommItemDPI.setAmount(3500)
		1*existingcommItemDPI.setQuantity(5)
		1*range.setHighBound(9)
		BBBDetailedItemPriceInfo newDpi =priceInfo.getCurrentPriceDetails().get(0)
		newDpi.getRange() != null
		newDpi.getRange().getHighBound() ==10
		newDpi.getRange().getLowBound() ==10
		newDpi.getQuantity() ==1
		newDpi.getAmount() == -2800
		1*existingcommItemDPI.setAmount(680.0)

		//removeDuplicatePricingAdjustments
		0*adj.setTotalAdjustment(_)
		existingcommItemDPI.getAdjustments() == [adj,adj1]
	}

	def"priceOrder, when rangeBounds are not equal(updateExistingDPI) , sourceAdjustment.getPricingModel is null, repositoryID's are not equal(removeDuplicatePricingAdjustments)"(){

		given:
		setParameters()

		// for mergeDPIListForCommerceItem
		pExtraParameters.put("id",dpiList)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		cItem.getPriceInfo() >> priceInfo

		//for mergeSameDPI in mergeDPIListForCommerceItem
		Range range =Mock()
		Range range1 =Mock()
		BBBDetailedItemPriceInfo existingcommItemDPI = Mock()
		dpItem.getRange() >>  range
		dpItem2.getRange() >> range1

		priceInfo.getCurrentPriceDetailsForRange(range) >> [existingcommItemDPI]
		priceInfo.getCurrentPriceDetailsForRange(range1) >> [existingcommItemDPI]
		existingcommItemDPI.getRange() >> range
		range.getHighBound() >> 10
		range.getSize() >> 50

		//for updateExistingDpi
		PricingAdjustment adj =Mock()
		PricingAdjustment adj1 =Mock()
		priceInfo.getAmount() >> 500.0
		dpItem.getAmount() >> 100.0
		dpItem.getQuantity() >> 2
		range.getLowBound() >> 5
		existingcommItemDPI.getAmount() >> 700.0
		existingcommItemDPI.getQuantity() >> 1

		dpItem2.getAmount() >> 20      //for 2nd iteration
		dpItem2.getQuantity() >> 5
		range1.getHighBound() >> 5
		range1.getLowBound() >> 5
		priceInfo.getCurrentPriceDetails() >>[]
		dpItem2.getAdjustments() >>[]

		existingcommItemDPI.getAdjustments() >> []
		dpItem.getAdjustments() >> [adj,adj1]

		//removeDuplicatePricingAdjustments
		RepositoryItem rep =Mock()
		2*adj.getPricingModel() >>null
		0*adj.getTotalAdjustment() >> 50
		0*adj1.getTotalAdjustment() >> 150

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*range.setHighBound(11)
		1*dpItem.setQuantity(50);
		1*priceInfo.setAmount(300.0)
		1*existingcommItemDPI.setAmount(600.0)
		2*existingcommItemDPI.setDiscounted(true)

		1*priceInfo.setAmount(400.0) // for 2nd iteration
		1*existingcommItemDPI.setAmount(3500)
		1*existingcommItemDPI.setQuantity(5)
		1*range.setHighBound(9)
		BBBDetailedItemPriceInfo newDpi =priceInfo.getCurrentPriceDetails().get(0)
		newDpi.getRange() != null
		newDpi.getRange().getHighBound() ==10
		newDpi.getRange().getLowBound() ==10
		newDpi.getQuantity() ==1
		newDpi.getAmount() == -2800
		1*existingcommItemDPI.setAmount(680.0)

		//removeDuplicatePricingAdjustments
		0*adj.setTotalAdjustment(_)
		existingcommItemDPI.getAdjustments() == [adj,adj1]
	}

	def"priceOrder, when rangeBounds are not equal(updateExistingDPI) , PricingModels are not null, repositoryID's are not equal(removeDuplicatePricingAdjustments)"(){

		given:
		setParameters()

		// for mergeDPIListForCommerceItem
		pExtraParameters.put("id",dpiList)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		cItem.getPriceInfo() >> priceInfo

		//for mergeSameDPI in mergeDPIListForCommerceItem
		BBBDetailedItemPriceInfo existingcommItemDPI = Mock()
		dpItem.getRange() >>  range
		dpItem2.getRange() >> range1

		priceInfo.getCurrentPriceDetailsForRange(range) >> [existingcommItemDPI]
		priceInfo.getCurrentPriceDetailsForRange(range1) >> [existingcommItemDPI]
		existingcommItemDPI.getRange() >> range
		range.getHighBound() >> 10
		range.getSize() >> 50

		//for updateExistingDpi
		PricingAdjustment adj =Mock()
		PricingAdjustment adj1 =Mock()
		priceInfo.getAmount() >> 500.0
		dpItem.getAmount() >> 100.0
		dpItem.getQuantity() >> 2
		range.getLowBound() >> 5
		existingcommItemDPI.getAmount() >> 700.0
		existingcommItemDPI.getQuantity() >> 1

		dpItem2.getAmount() >> 20      //for 2nd iteration
		dpItem2.getQuantity() >> 5
		range1.getHighBound() >> 5
		range1.getLowBound() >> 5
		priceInfo.getCurrentPriceDetails() >>[]
		dpItem2.getAdjustments() >>[]

		existingcommItemDPI.getAdjustments() >> []
		dpItem.getAdjustments() >> [adj,adj1]

		//removeDuplicatePricingAdjustments
		RepositoryItem rep =Mock()
		RepositoryItem rep1 =Mock()
		4*adj.getPricingModel() >>rep
		4*adj1.getPricingModel() >>rep1
		rep.getRepositoryId() >> "rep"
		rep1.getRepositoryId() >> "rep1"
		0*adj.getTotalAdjustment() >> 50
		0*adj1.getTotalAdjustment() >> 150

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*range.setHighBound(11)
		1*dpItem.setQuantity(50);
		1*priceInfo.setAmount(300.0)
		1*existingcommItemDPI.setAmount(600.0)
		2*existingcommItemDPI.setDiscounted(true)

		1*priceInfo.setAmount(400.0) // for 2nd iteration
		1*existingcommItemDPI.setAmount(3500)
		1*existingcommItemDPI.setQuantity(5)
		1*range.setHighBound(9)
		BBBDetailedItemPriceInfo newDpi =priceInfo.getCurrentPriceDetails().get(0)
		newDpi.getRange() != null
		newDpi.getRange().getHighBound() ==10
		newDpi.getRange().getLowBound() ==10
		newDpi.getQuantity() ==1
		newDpi.getAmount() == -2800
		1*existingcommItemDPI.setAmount(680.0)

		//removeDuplicatePricingAdjustments
		0*adj.setTotalAdjustment(_)
		existingcommItemDPI.getAdjustments() == [adj,adj1]
	}

	def"priceOrder, when rangeBounds are not equal(updateExistingDPI) and sourceAdjusmentCoupon is null,compareAdjustmentCoupon is not null, repositoryID's are not equal(removeDuplicatePricingAdjustments) "(){

		given:
		setParameters()

		// for mergeDPIListForCommerceItem
		pExtraParameters.put("id",dpiList)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		cItem.getPriceInfo() >> priceInfo

		//for mergeSameDPI in mergeDPIListForCommerceItem
		BBBDetailedItemPriceInfo existingcommItemDPI = Mock()
		dpItem.getRange() >>  range
		dpItem2.getRange() >> range1

		priceInfo.getCurrentPriceDetailsForRange(range) >> [existingcommItemDPI]
		priceInfo.getCurrentPriceDetailsForRange(range1) >> [existingcommItemDPI]
		existingcommItemDPI.getRange() >> range
		range.getHighBound() >> 10
		range.getSize() >> 50

		//for updateExistingDpi
		PricingAdjustment adj =Mock()
		PricingAdjustment adj1 =Mock()
		priceInfo.getAmount() >> 500.0
		dpItem.getAmount() >> 100.0
		dpItem.getQuantity() >> 2
		range.getLowBound() >> 5
		existingcommItemDPI.getAmount() >> 700.0
		existingcommItemDPI.getQuantity() >> 1

		dpItem2.getAmount() >> 20      //for 2nd iteration
		dpItem2.getQuantity() >> 5
		range1.getHighBound() >> 5
		range1.getLowBound() >> 5
		priceInfo.getCurrentPriceDetails() >>[]
		dpItem2.getAdjustments() >>[]

		existingcommItemDPI.getAdjustments() >> []
		dpItem.getAdjustments() >> [adj,adj1]

		//removeDuplicatePricingAdjustments
		RepositoryItem rep =Mock()
		RepositoryItem rep1 =Mock()
		RepositoryItem coupon =Mock()
		RepositoryItem coupon1 =Mock()

		adj.getPricingModel() >>rep
		adj1.getPricingModel() >>rep1
		rep.getRepositoryId() >>"repID"
		rep1.getRepositoryId() >>"repID"
		adj.getCoupon() >>null
		adj.getTotalAdjustment() >> 50
		adj1.getTotalAdjustment() >> 150

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*range.setHighBound(11)
		1*dpItem.setQuantity(50);
		1*priceInfo.setAmount(300.0)
		1*existingcommItemDPI.setAmount(600.0)
		2*existingcommItemDPI.setDiscounted(true)

		1*priceInfo.setAmount(400.0) // for 2nd iteration
		1*existingcommItemDPI.setAmount(3500)
		1*existingcommItemDPI.setQuantity(5)
		1*range.setHighBound(9)
		BBBDetailedItemPriceInfo newDpi =priceInfo.getCurrentPriceDetails().get(0)
		newDpi.getRange() != null
		newDpi.getRange().getHighBound() ==10
		newDpi.getRange().getLowBound() ==10
		newDpi.getQuantity() ==1
		newDpi.getAmount() == -2800
		1*existingcommItemDPI.setAmount(680.0)

		//removeDuplicatePricingAdjustments
		1*adj.setTotalAdjustment(200)
		existingcommItemDPI.getAdjustments() == [adj]
	}

	def"priceOrder, when rangeBounds are not equal(updateExistingDPI) and sourceAdjusmentCoupon is not null,compareAdjustmentCoupon is null, repositoryID's are not equal(removeDuplicatePricingAdjustments) "(){

		given:
		setParameters()

		// for mergeDPIListForCommerceItem
		pExtraParameters.put("id",dpiList)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		cItem.getPriceInfo() >> priceInfo

		//for mergeSameDPI in mergeDPIListForCommerceItem
		BBBDetailedItemPriceInfo existingcommItemDPI = Mock()
		dpItem.getRange() >>  range
		dpItem2.getRange() >> range1

		priceInfo.getCurrentPriceDetailsForRange(range) >> [existingcommItemDPI]
		priceInfo.getCurrentPriceDetailsForRange(range1) >> [existingcommItemDPI]
		existingcommItemDPI.getRange() >> range
		range.getHighBound() >> 10
		range.getSize() >> 50

		//for updateExistingDpi
		PricingAdjustment adj =Mock()
		PricingAdjustment adj1 =Mock()
		priceInfo.getAmount() >> 500.0
		dpItem.getAmount() >> 100.0
		dpItem.getQuantity() >> 2
		range.getLowBound() >> 5
		existingcommItemDPI.getAmount() >> 700.0
		existingcommItemDPI.getQuantity() >> 1

		dpItem2.getAmount() >> 20      //for 2nd iteration
		dpItem2.getQuantity() >> 5
		range1.getHighBound() >> 5
		range1.getLowBound() >> 5
		priceInfo.getCurrentPriceDetails() >>[]
		dpItem2.getAdjustments() >>[]

		existingcommItemDPI.getAdjustments() >> []
		dpItem.getAdjustments() >> [adj,adj1]

		//removeDuplicatePricingAdjustments
		RepositoryItem rep =Mock()
		RepositoryItem rep1 =Mock()
		RepositoryItem coupon =Mock()

		adj.getPricingModel() >>rep
		adj1.getPricingModel() >>rep1
		rep.getRepositoryId() >>"repID"
		rep1.getRepositoryId() >>"repID"
		adj.getCoupon() >>coupon
		adj.getCoupon() >>null
		adj.getTotalAdjustment() >> 50
		adj1.getTotalAdjustment() >> 150

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*range.setHighBound(11)
		1*dpItem.setQuantity(50);
		1*priceInfo.setAmount(300.0)
		1*existingcommItemDPI.setAmount(600.0)
		2*existingcommItemDPI.setDiscounted(true)

		1*priceInfo.setAmount(400.0) // for 2nd iteration
		1*existingcommItemDPI.setAmount(3500)
		1*existingcommItemDPI.setQuantity(5)
		1*range.setHighBound(9)
		BBBDetailedItemPriceInfo newDpi =priceInfo.getCurrentPriceDetails().get(0)
		newDpi.getRange() != null
		newDpi.getRange().getHighBound() ==10
		newDpi.getRange().getLowBound() ==10
		newDpi.getQuantity() ==1
		newDpi.getAmount() == -2800
		1*existingcommItemDPI.setAmount(680.0)

		//removeDuplicatePricingAdjustments
		1*adj.setTotalAdjustment(200)
		existingcommItemDPI.getAdjustments() == [adj]
	}

	def"priceOrder, when rangeBounds are equal(updateExistingDPI) and sourceAdjusmentCoupon and compareAdjustmentCoupon are not null, repositoryID's are equal(removeDuplicatePricingAdjustments) "(){

		given:
		setParameters()

		// for mergeDPIListForCommerceItem
		pExtraParameters.put("id",dpiList)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		cItem.getPriceInfo() >> priceInfo

		//for mergeSameDPI in mergeDPIListForCommerceItem
		BBBDetailedItemPriceInfo existingcommItemDPI = Mock()
		dpItem.getRange() >>  range
		dpItem2.getRange() >> range1

		priceInfo.getCurrentPriceDetailsForRange(range) >> [existingcommItemDPI]
		priceInfo.getCurrentPriceDetailsForRange(range1) >> [existingcommItemDPI]
		existingcommItemDPI.getRange() >> range
		range.getHighBound() >> 10
		range.getSize() >> 50

		//for updateExistingDpi
		PricingAdjustment adj =Mock()
		PricingAdjustment adj1 =Mock()
		priceInfo.getAmount() >> 500.0
		dpItem.getAmount() >> 100.0
		dpItem.getQuantity() >> 2
		range.getLowBound() >> 5
		existingcommItemDPI.getAmount() >> 700.0
		existingcommItemDPI.getQuantity() >> 1

		dpItem2.getAmount() >> 20      //for 2nd iteration
		dpItem2.getQuantity() >> 5
		range1.getHighBound() >> 10
		range1.getLowBound() >> 20
		priceInfo.getCurrentPriceDetails() >>[]
		dpItem2.getAdjustments() >>[]

		existingcommItemDPI.getAdjustments() >> []
		dpItem.getAdjustments() >> [adj,adj1]

		//removeDuplicatePricingAdjustments
		RepositoryItem rep =Mock()
		RepositoryItem rep1 =Mock()
		RepositoryItem coupon =Mock()

		adj.getPricingModel() >>rep
		adj1.getPricingModel() >>rep1
		rep.getRepositoryId() >>"repID"
		rep1.getRepositoryId() >>"repID"
		adj.getCoupon() >>coupon
		adj1.getCoupon() >>coupon
		coupon.getRepositoryId() >> "cId"
		adj.getTotalAdjustment() >> 50
		adj1.getTotalAdjustment() >> 150


		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*range.setHighBound(11)
		1*dpItem.setQuantity(50);
		1*priceInfo.setAmount(300.0)
		1*existingcommItemDPI.setAmount(600.0)
		2*existingcommItemDPI.setDiscounted(true)

		// for 2nd iteration when else if is executed in updateExistingDPI
		1*priceInfo.setAmount(400.0)
		1*existingcommItemDPI.setAmount(3500)
		1*existingcommItemDPI.setQuantity(5)
		1*range.setLowBound(6)
		BBBDetailedItemPriceInfo newDpi =priceInfo.getCurrentPriceDetails().get(0)
		newDpi.getRange() != null
		newDpi.getRange().getHighBound() ==5
		newDpi.getRange().getLowBound() ==5
		newDpi.getQuantity() ==1
		newDpi.getAmount() == -2800
		1*existingcommItemDPI.setAmount(680.0)

		//removeDuplicatePricingAdjustments
		1*adj.setTotalAdjustment(200)
		existingcommItemDPI.getAdjustments() == [adj]
	}

	def"priceOrder, when rangeBounds are equal(updateExistingDPI) ,updateExistingDPIForInBetweenRange is called, coupons are not null, repositoryID's are equal(removeDuplicatePricingAdjustments) "(){

		given:
		setParameters()

		// for mergeDPIListForCommerceItem
		pExtraParameters.put("id",dpiList)
		3*cItem.getCatalogRefId() >>"refid"
		1*cItem.getId() >> "id"
		cItem.getPriceInfo() >> priceInfo

		//for mergeSameDPI in mergeDPIListForCommerceItem
		BBBDetailedItemPriceInfo existingcommItemDPI = Mock()
		dpItem.getRange() >>  range
		dpItem2.getRange() >> range1

		priceInfo.getCurrentPriceDetailsForRange(range) >> [existingcommItemDPI]
		priceInfo.getCurrentPriceDetailsForRange(range1) >> [existingcommItemDPI]
		existingcommItemDPI.getRange() >> range
		range.getHighBound() >> 10
		range.getSize() >> 50

		//for updateExistingDpi
		PricingAdjustment adj =Mock()
		PricingAdjustment adj1 =Mock()
		priceInfo.getAmount() >> 500.0
		dpItem.getAmount() >> 100.0
		dpItem.getQuantity() >> 2
		range.getLowBound() >> 5
		existingcommItemDPI.getAmount() >> 700.0
		existingcommItemDPI.getQuantity() >> 1

		dpItem2.getAmount() >> 20      //for 2nd iteration else block and updateExistingDPIForInBetweenRange is called
		dpItem2.getQuantity() >> 5
		range1.getHighBound() >> 2
		range1.getLowBound() >> 20
		priceInfo.getCurrentPriceDetails() >>[]
		dpItem2.getAdjustments() >>[]

		existingcommItemDPI.getAdjustments() >> []
		dpItem.getAdjustments() >> [adj,adj1]

		//removeDuplicatePricingAdjustments
		RepositoryItem rep =Mock()
		RepositoryItem rep1 =Mock()
		RepositoryItem coupon =Mock()

		adj.getPricingModel() >>rep
		adj1.getPricingModel() >>rep1
		rep.getRepositoryId() >>"repID"
		rep1.getRepositoryId() >>"repID"
		adj.getCoupon() >>null
		adj1.getCoupon() >>null
		adj.getTotalAdjustment() >> 50
		adj1.getTotalAdjustment() >> 150

		when:
		dpiCalculator.priceOrder(pPriceQuote,pOrder, pPricingModel,pLocale,pProfile, pExtraParameters)

		then:
		1*range.setHighBound(11)
		1*dpItem.setQuantity(50);
		1*priceInfo.setAmount(300.0)
		1*existingcommItemDPI.setAmount(600.0)
		2*existingcommItemDPI.setDiscounted(true)

		// for 2nd iteration , for method updateExistingDPIForInBetweenRange
		1*priceInfo.setAmount(400.0)
		1*existingcommItemDPI.setAmount(3500)
		1*existingcommItemDPI.setQuantity(5)
		1*existingcommItemDPI.setRange(range1)

		BBBDetailedItemPriceInfo newDpi =priceInfo.getCurrentPriceDetails().get(0)
		newDpi.getRange() != null
		newDpi.getRange().getHighBound() == 19
		newDpi.getRange().getLowBound() == 5
		newDpi.getQuantity() ==15
		newDpi.getAmount() == 10500
		BBBDetailedItemPriceInfo upperRangeNewDPI =priceInfo.getCurrentPriceDetails().get(1)
		upperRangeNewDPI.getRange() != null
		upperRangeNewDPI.getRange().getHighBound() == 10
		upperRangeNewDPI.getRange().getLowBound() == 3
		upperRangeNewDPI.getQuantity() ==8
		upperRangeNewDPI.getAmount() == -13300
		1*existingcommItemDPI.setAmount(680.0)

		//removeDuplicatePricingAdjustments
		1*adj.setTotalAdjustment(200)
		existingcommItemDPI.getAdjustments() == [adj]
	}
}
