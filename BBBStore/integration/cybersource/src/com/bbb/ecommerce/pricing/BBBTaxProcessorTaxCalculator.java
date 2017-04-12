/**
 * 
 */
package com.bbb.ecommerce.pricing;

import java.beans.IntrospectionException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBDetailedItemPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.utils.BBBUtility;
import com.cybersource.stub.TaxReply;
import com.cybersource.stub.TaxReplyItem;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.processor.OrderRepositoryUtils;
import atg.commerce.pricing.AmountInfo;
import atg.commerce.pricing.Constants;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.integrations.cybersourcesoap.CyberSourceException;
import atg.integrations.cybersourcesoap.CyberSourceStatus;
import atg.integrations.cybersourcesoap.CyberSourceUtils;
import atg.integrations.cybersourcesoap.MessageConstant;
import atg.integrations.cybersourcesoap.pricing.BaseTaxProcessorTaxCalculator;
import atg.payment.tax.ShippingDestination;
import atg.payment.tax.ShippingDestinationImpl;
import atg.payment.tax.TaxRequestInfo;
import atg.payment.tax.TaxRequestInfoImpl;
import atg.payment.tax.TaxStatus;
import atg.payment.tax.TaxableItem;
import atg.payment.tax.TaxableItemImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.ServletUtil;
/**
 * @author Pradeep Reddy
 *
 */
public class BBBTaxProcessorTaxCalculator extends BaseTaxProcessorTaxCalculator {
	private MutableRepository orderRepository;
	/**
	 * 
	 * @return orderRepository
	 */
	public MutableRepository getOrderRepository() {
		return orderRepository;
	}
	/**
	 * 
	 * @param orderRepository
	 */
	public void setOrderRepository(MutableRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	/**
	 * This method is overridden to assign the TaxPriceInfo objects to the
	 * StoreItemPriceInfo objects. This method is used if
	 * <code>calculateTaxByShipping</code> is true.
	 * 
	 * @param pShippingGroup
	 *            The shipping group whose details will be updated
	 * @param pTaxPriceInfo
	 *            The TaxPriceInfo for the shipping group
	 * @param pOrderPrice
	 *            The TaxPriceInfo for the entire order
	 * @param pShippingSubtotal
	 *            The price info containing the subtotal information for the
	 *            shipping group
	 * @param pStatus
	 *            This is the tax status that was returned from the
	 *            TaxProcessor.
	 * @throws PricingException
	 *             if error occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void assignItemTaxAmounts(ShippingGroup pShippingGroup, TaxPriceInfo pTaxPriceInfo,
			OrderPriceInfo pOrderPrice, OrderPriceInfo pShippingSubtotal, TaxStatus pStatus) throws PricingException {
		vlogDebug("BBBTaxProcessorTaxCalculator.assignItemTaxAmounts: Starts");
		super.assignItemTaxAmounts(pShippingGroup, pTaxPriceInfo, pOrderPrice, pShippingSubtotal, pStatus);
		TaxReplyItem taxReplyItem = null;
		if (pShippingGroup == null) {
			vlogDebug("BBBTaxProcessorTaxCalculator.assignItemTaxAmounts: Couldn't assign item tax amounts.  The shipping group was null.");
			return;
		}
		if (pTaxPriceInfo == null) {
			vlogDebug("BBBTaxProcessorTaxCalculator.assignItemTaxAmounts: Couldn't assign item tax amounts.  The tax price info was null.");
			return;
		}
		List relationships = getRelationshipsToAssignTaxTo(pShippingGroup);

		CyberSourceStatus taxStatus = (CyberSourceStatus) pStatus;

		Map<String, TaxPriceInfo> itemTaxMap = new HashMap();

		/*
		 * If the relationships are null or they are empty then there is nothing
		 * to distribute and as such we should just return
		 */

		if (relationships == null || relationships.size() == 0) {
			vlogDebug("relationships is null or size is zero.");
			return;
		}
		vlogDebug("shippingGroup - {0}, relationships - {1}", pShippingGroup.getId(), relationships.size());
		int taxReplyIndex = 0;
		OrderHolder shoppingCart = (OrderHolder) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_SHOPPING_CART);
		Order currentOrder = shoppingCart.getCurrent();
		TaxReply taxReply = taxStatus.getReply().getTaxReply();
		for (int r = 0; r < relationships.size(); r++) {
			ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) relationships.get(r);
			String commerceItemId = sgcir.getCommerceItem().getId();
			vlogDebug("relationshipIndex - {0}, commerceItem - {1}, commerceItemType - {2}", r, commerceItemId, sgcir.getCommerceItem().getCommerceItemClassType());
			if (sgcir.getCommerceItem() instanceof BBBCommerceItem && sgcir.getRange()!= null) {
				vlogDebug("Assigning Tax Amounts to BBBCommerceItem. Range is {0} - {1}", sgcir.getRange().getLowBound(), sgcir.getRange().getHighBound());
				BBBCommerceItem commerceItem = (BBBCommerceItem) sgcir.getCommerceItem();
				try {
					final List<BBBDetailedItemPriceInfo> priceBeans = commerceItem.getPriceInfo().getCurrentPriceDetailsForRange(sgcir.getRange());
					Collections.sort(priceBeans, Collections.reverseOrder(new TaxCalculatorComparator()));
					boolean isSplitReq = false;
					NonMerchandiseCommerceItem nonMerchCommerceItem = null;//DeliveryItem
					String deliveryItemId = null;
					deliveryItemId = commerceItem.getDeliveryItemId();
					if (BBBUtility.isNotEmpty(deliveryItemId)) {
						nonMerchCommerceItem = (NonMerchandiseCommerceItem) currentOrder.getCommerceItem(deliveryItemId);
						double deliveryItemTotalAmount = nonMerchCommerceItem.getPriceInfo().getAmount();
						double deliveryItemQuantity = commerceItem.getQuantity();
						isSplitReq = this.isSplitRequired(deliveryItemTotalAmount, deliveryItemQuantity);
					}
					vlogDebug("deliveryItemId - {0}, isSplitReq - {1}, priceBeans Size - {2}", deliveryItemId, isSplitReq, priceBeans.size());					
					for (int loopCounter = 0; loopCounter < priceBeans.size(); loopCounter++) {
						BBBDetailedItemPriceInfo priceBean = priceBeans.get(loopCounter);
						long quantity = priceBean.getQuantity();
						boolean lastItem = priceBeans.size() - 1 == loopCounter ;
						boolean splitLastItem = (quantity > 1 && lastItem && isSplitReq);//Indicates whether the current PriceBean has to be split because of delivery surcharge capping.
						vlogDebug("LoopCounter - {0}, lastItem - {1}, splitLastItem - {2}, quantity - {3}", loopCounter, lastItem, splitLastItem, quantity);
						List<RepositoryItem> dpiTaxItemList = priceBean.getDsLineItemTaxInfos();
						taxReplyIndex = populateLineItemTaxDetails(itemTaxMap, dpiTaxItemList, sgcir,
								taxReply, taxReplyIndex, pShippingGroup, false, priceBean);
						if(splitLastItem){
							vlogDebug("splitLastItem is {0}. Assigning tax amounts to item with quantity 1", splitLastItem);
							taxReplyIndex = populateLineItemTaxDetails(itemTaxMap, dpiTaxItemList, sgcir,
									taxReply, taxReplyIndex, pShippingGroup, splitLastItem, priceBean);
						}
						vlogDebug("Assigning Tax Amounts to dpi with index - {0} ends with taxReplyIndex - {1}", loopCounter, taxReplyIndex);
					}
					vlogDebug("Assigning Tax Amounts to priceBeans of commerceItem - {0} is completed. taxReplyIndex - {1}", commerceItem.getId(), taxReplyIndex);
				} catch (RepositoryException e) {
					vlogError(
							"BBBTaxProcessorTaxCalculator.assignItemTaxAmounts: Repository Exception occured while populating tax info to DPI {0}",
							e);
				} catch (CommerceItemNotFoundException e) {
					vlogError(
							"BBBTaxProcessorTaxCalculator.assignItemTaxAmounts: Repository Exception occured while getting nonMerchcommerceItem {0}",
							e);
				} catch (InvalidParameterException e) {
					vlogError(
							"BBBTaxProcessorTaxCalculator.assignItemTaxAmounts: Repository Exception occured while getting nonMerchcommerceItem {0}",
							e);
				}
			} else if (!(sgcir.getCommerceItem() instanceof EcoFeeCommerceItem)
					&& !(sgcir.getCommerceItem() instanceof LTLDeliveryChargeCommerceItem)
					&& !(sgcir.getCommerceItem() instanceof LTLAssemblyFeeCommerceItem)) {
				vlogDebug("Assigning Tax Amounts to commerceItem of type - {0}, taxReplyIndex - {1}, commerceItemId - {2}", sgcir.getCommerceItem().getCommerceItemClassType(), taxReplyIndex, commerceItemId);
				taxReplyItem = taxReply.getItem(taxReplyIndex);
				TaxPriceInfo taxPriceInfo = this.retrieveTaxPriceInfo(itemTaxMap, commerceItemId);
				copyCyberSourceTaxReplyItemDetails(taxReplyItem, taxPriceInfo);
				taxReplyIndex++;
				itemTaxMap.put(commerceItemId, taxPriceInfo);
				vlogDebug("Assigned Tax Amounts to commerceItem with id - {0}, taxReplyIndex - {1}", commerceItemId, taxReplyIndex);
			}
		}
		vlogDebug("Assigning total tax amounts to shippingGroup.");
		// adding shipping tax details
		taxReplyItem = taxReply.getItem(taxReply.getItem().length - 1);
		TaxPriceInfo shippingTaxPriceInfo = this.retrieveTaxPriceInfo(itemTaxMap, BBBCoreConstants.SHIPPING_TAX_PRICE_INFO_KEY);
		copyCyberSourceTaxReplyItemDetails(taxReplyItem, shippingTaxPriceInfo);
		itemTaxMap.put(BBBCoreConstants.SHIPPING_TAX_PRICE_INFO_KEY, shippingTaxPriceInfo);
		pTaxPriceInfo.setShippingItemsTaxPriceInfos(itemTaxMap);
		vlogDebug("BBBTaxProcessorTaxCalculator.assignItemTaxAmounts: Ends.");
	}
	
	/**
	 * Retrieves taxPriceInfo for the current commerceItem in the map[shippingGroup taxMap]. If not found, creates new TaxPriceInfo object.
	 * 
	 * @param itemTaxMap
	 * @param itemId
	 * @return
	 */
	private TaxPriceInfo retrieveTaxPriceInfo(Map<String, TaxPriceInfo> itemTaxMap, String itemId){
		TaxPriceInfo result = null;
		if(itemTaxMap!=null && !itemTaxMap.isEmpty() && itemTaxMap.containsKey(itemId))
			result = (TaxPriceInfo) itemTaxMap.get(itemId);
		else
			result = new TaxPriceInfo();
		return result;
	}

	/**
	 * Stores the retrieved tax information from cyberSource into the DSLineItem's and TaxPriceInfo's.
	 * 
	 * @param itemTaxMap
	 * @param dpiTaxItemList
	 * @param sgcir
	 * @param taxReply
	 * @param taxReplyIndex
	 * @param pShippingGroup
	 * @param splitLastItem
	 * @param priceBean
	 * @return
	 * @throws RepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int populateLineItemTaxDetails(Map<String, TaxPriceInfo> itemTaxMap, List<RepositoryItem> dpiTaxItemList,
			ShippingGroupCommerceItemRelationship sgcir, TaxReply taxReply,
			int taxReplyIndex, ShippingGroup pShippingGroup, boolean splitLastItem, DetailedItemPriceInfo priceBean) throws RepositoryException {
		vlogDebug("BBBTaxProcessorTaxCalculator.populateLineItemTaxDetails starts. splitLastItem :- {0}, taxReplyIndex :- {1}, shippingGroup - {2}, "
				+ "commerceItem - {3}, priceBeanRange - {4}", splitLastItem, taxReplyIndex, sgcir.getShippingGroup().getId(), 
				sgcir.getCommerceItem().getId(), priceBean.getRange().getLowBound() + "-" + priceBean.getRange().getHighBound());
		//Retrieve DeliveryItemId, AssemblyItemId, EcoFeeItemId if present.
		BBBCommerceItem commerceItem = (BBBCommerceItem) sgcir.getCommerceItem();
		BBBShippingGroup shippingGroup = (BBBShippingGroup) sgcir.getShippingGroup();
		String ecoFeeItemId = shippingGroup.getEcoFeeItemMap().get(commerceItem.getId());
		String deliveryFeeItemId = commerceItem.getDeliveryItemId();
		String assemblyFeeItemId = commerceItem.getAssemblyItemId();
		MutableRepositoryItem dpiTaxItem = null;
		vlogDebug("Assigning Tax Amounts to CommerceItem.");
		//Assigning Tax Amount to main commerceItem.
		TaxPriceInfo taxPriceInfo = retrieveTaxPriceInfo(itemTaxMap, commerceItem.getId());
		//If splitLastItem is true[in case when surchargeCapping is applied and the last DPI is split.], use DSLineItemType as DPI_ITEM_SPLIT, otherwise use DPI_ITEM. There will only be one DSLineItemTaxInfo with item type DPI_ITEM_SPLIT.
		dpiTaxItem = copyTaxInfoToDPI(taxReply.getItem(taxReplyIndex), splitLastItem ? BBBCoreConstants.DPI_ITEM_SPLIT : BBBCoreConstants.DPI_ITEM);
		dpiTaxItemList.add(dpiTaxItem);
		copyCyberSourceTaxReplyItemDetails(taxReply.getItem(taxReplyIndex), taxPriceInfo);
		priceBean.setTax(this.getPricingTools().round(Double.valueOf(taxReply.getItem(taxReplyIndex).getTotalTaxAmount())));
		taxReplyIndex++;
		vlogDebug("Assigned Tax Amounts to CommerceItem. Created DSLineItem of type - {0}, taxReplyIndex - {1}", 
				splitLastItem ? BBBCoreConstants.DPI_ITEM_SPLIT : BBBCoreConstants.DPI_ITEM, taxReplyIndex);
		itemTaxMap.put(sgcir.getCommerceItem().getId(), taxPriceInfo);
		//Tax Amount Assignment to main CommerceItem is finished.
		//Assign Tax Amounts to commerceItems associated with the main commerceItem such as LTLDeliveryChargeCommerceItem/LTLAssemblyCommerceItem/EcoFeeCommmerceItem.
		//Assigning Tax amounts to LTLDeliveryChargeCommerceItem.
		if (BBBUtility.isNotEmpty(deliveryFeeItemId)) {
			vlogDebug("Assigning Tax Amounts to deliveryCommerceItem with id - {0} for commerceItem - {1}", deliveryFeeItemId, commerceItem.getId());
			TaxPriceInfo deliveryTaxPriceInfo = this.retrieveTaxPriceInfo(itemTaxMap, deliveryFeeItemId);
			dpiTaxItem = copyTaxInfoToDPI(taxReply.getItem(taxReplyIndex), splitLastItem ? BBBCoreConstants.DPI_DELIVERY_SPLIT : BBBCoreConstants.DPI_DELIVERY);
			dpiTaxItemList.add(dpiTaxItem);
			copyCyberSourceTaxReplyItemDetails(taxReply.getItem(taxReplyIndex), deliveryTaxPriceInfo);
			taxReplyIndex++;
			itemTaxMap.put(deliveryFeeItemId, deliveryTaxPriceInfo);
			vlogDebug("Assigned Tax Amounts to DeliveryCommerceItem. Created DSLineItem of type - {0}, taxReplyIndex - {1}", 
					splitLastItem ? BBBCoreConstants.DPI_DELIVERY_SPLIT : BBBCoreConstants.DPI_DELIVERY, taxReplyIndex);
		}
		//Assignment of tax amounts to LTL DeliveryCharge CommerceItem ends.
		//Assigning Tax amounts to LTLAssemblyFeeCommerceItem.
		if (BBBUtility.isNotEmpty(assemblyFeeItemId)) {
			vlogDebug("Assigning Tax Amounts to assemblyItem with id - {0} for commerceItem - {1}", assemblyFeeItemId, commerceItem.getId());
			TaxPriceInfo assemblyTaxPriceInfo = this.retrieveTaxPriceInfo(itemTaxMap, assemblyFeeItemId);
			dpiTaxItem = copyTaxInfoToDPI(taxReply.getItem(taxReplyIndex), splitLastItem ? BBBCoreConstants.DPI_ASSEMBLY_SPLIT : BBBCoreConstants.DPI_ASSEMBLY);
			dpiTaxItemList.add(dpiTaxItem);
			copyCyberSourceTaxReplyItemDetails(taxReply.getItem(taxReplyIndex), assemblyTaxPriceInfo);
			itemTaxMap.put(assemblyFeeItemId, assemblyTaxPriceInfo);
			taxReplyIndex++;
			vlogDebug("Assigned Tax Amounts to AssemblyCommerceItem. Created DSLineItem of type - {0}, taxReplyIndex - {1}", 
					splitLastItem ? BBBCoreConstants.DPI_ASSEMBLY_SPLIT : BBBCoreConstants.DPI_ASSEMBLY, taxReplyIndex);
		}
		//Assignment of tax amounts to LTLAssemblyCommerceItem ends.
		//Assigning tax amounts to EcoFeeCommerceItem.
		if (BBBUtility.isNotEmpty(ecoFeeItemId)) {
			vlogDebug("Assigning Tax Amounts to ecoFeeItem with id - {0} for commerceItem - {1}", ecoFeeItemId, commerceItem.getId());
			TaxPriceInfo ecoFeeTaxInfo = this.retrieveTaxPriceInfo(itemTaxMap, ecoFeeItemId);
			dpiTaxItem = copyTaxInfoToDPI(taxReply.getItem(taxReplyIndex), splitLastItem ? BBBCoreConstants.DPI_ECOFEE_SPLIT : BBBCoreConstants.DPI_ECOFEE);
			dpiTaxItemList.add(dpiTaxItem);
			copyCyberSourceTaxReplyItemDetails(taxReply.getItem(taxReplyIndex), ecoFeeTaxInfo);
			taxReplyIndex++;
			itemTaxMap.put(ecoFeeItemId, ecoFeeTaxInfo);
			vlogDebug("Assigned Tax Amounts to EcoFeeCommerceItem. Created DSLineItem of type - {0}, taxReplyIndex - {1}", 
					splitLastItem ? BBBCoreConstants.DPI_ECOFEE_SPLIT : BBBCoreConstants.DPI_ECOFEE, taxReplyIndex);
		}
		//Assigning tax amounts to ecoFeeCommerceItem ends.
		vlogDebug("BBBTaxProcessorTaxCalculator.populateLineItemTaxDetails ends. taxReplyIndex :- {0}", taxReplyIndex);
		return taxReplyIndex;
	}

	/**
	 * 
	 * Copies the tax information from the taxReply to DSLineItem.
	 * 
	 * @param taxReplyItem
	 * @param dpiItemType
	 * @return
	 * @throws RepositoryException
	 */
	private MutableRepositoryItem copyTaxInfoToDPI(TaxReplyItem taxReplyItem,
			String dpiItemType) throws RepositoryException {
		vlogDebug("BBBTaxProcessorTaxCalculator.copyTaxInfoToDPI: Starts");
		MutableRepositoryItem dpiTaxItem = this.getOrderRepository().createItem(BBBCoreConstants.DS_LINEITEM_TAXINFO);
		dpiTaxItem.setPropertyValue("cityTax", Double.valueOf(taxReplyItem.getCityTaxAmount()));
		dpiTaxItem.setPropertyValue("countyTax", Double.valueOf(taxReplyItem.getCountyTaxAmount()));
		dpiTaxItem.setPropertyValue("districtTax", Double.valueOf(taxReplyItem.getDistrictTaxAmount()));
		dpiTaxItem.setPropertyValue("stateTax", Double.valueOf(taxReplyItem.getStateTaxAmount()));
		dpiTaxItem.setPropertyValue("itemType", dpiItemType);
		vlogDebug("BBBTaxProcessorTaxCalculator.copyTaxInfoToDPI: Ends");
		return dpiTaxItem;
	}
	
	/**
	 * Determines whether the current shippingGroup has taxOverride set and it is zero.
	 * The TaxValue should be zero for the functionality to work.
	 *  
	 * @param shippingGroup
	 * @return
	 */
	private boolean isTaxOverride(ShippingGroup shippingGroup){
		boolean isTaxOverride = false;
		if(shippingGroup!=null){
			if (shippingGroup instanceof BBBHardGoodShippingGroup) {
				BBBHardGoodShippingGroup bbbgroup = (BBBHardGoodShippingGroup) shippingGroup;
				TBSShippingInfo tbsInfo = bbbgroup.getTbsShipInfo();
				if (tbsInfo != null && tbsInfo.isTaxOverride()) {
					if (tbsInfo.getTaxValue() == 0.0) {
						isTaxOverride = true;
					}else{
						logError("TaxOverride Information obtained for the shippingGroup " + shippingGroup.getId() + " is " + tbsInfo.getTaxValue());
					}
				}
			} else if (shippingGroup instanceof BBBStoreShippingGroup) {
				BBBStoreShippingGroup bbbstoregroup = (BBBStoreShippingGroup) shippingGroup;
				TBSShippingInfo tbsInfo = bbbstoregroup.getTbsShipInfo();
				if (tbsInfo != null && tbsInfo.isTaxOverride()) {
					if (tbsInfo.getTaxValue() == 0.0) {
						isTaxOverride = true;
					}else{
						logError("TaxOverride Information obtained for the shippingGroup " + shippingGroup.getId() + " is " + tbsInfo.getTaxValue());
					}
				}
			}
		}
		return isTaxOverride;
	}

	/**
	 * Copies the CyberSourceTax Reply Item Details into TaxPriceInfo.
	 * 
	 * @param taxReplyItem
	 * @param dpiTaxItem
	 * @param taxPriceInfo
	 */
	protected void copyCyberSourceTaxReplyItemDetails(TaxReplyItem taxReplyItem, TaxPriceInfo taxPriceInfo) {
		vlogDebug("BBBTaxProcessorTaxCalculator.copyCyberSourceTaxReplyItemDetails: Starts");
		taxPriceInfo.setCityTax(this.getPricingTools().round(taxPriceInfo.getCityTax() + Double.valueOf(taxReplyItem.getCityTaxAmount())));
		taxPriceInfo.setCountyTax(this.getPricingTools().round(taxPriceInfo.getCountyTax() + Double.valueOf(taxReplyItem.getCountyTaxAmount())));
		taxPriceInfo.setDistrictTax(this.getPricingTools().round(taxPriceInfo.getDistrictTax() + Double.valueOf(taxReplyItem.getDistrictTaxAmount())));
		taxPriceInfo.setStateTax(this.getPricingTools().round(taxPriceInfo.getStateTax() + Double.valueOf(taxReplyItem.getStateTaxAmount()).doubleValue()));
		taxPriceInfo.setAmount(this.getPricingTools().round(taxPriceInfo.getAmount() + Double.valueOf(taxReplyItem.getTotalTaxAmount())));
		vlogDebug("BBBTaxProcessorTaxCalculator.copyCyberSourceTaxReplyItemDetails: Ends");
	}

	/**
	 * verifies that any one of the shipping address is not null, and that no crucial
	 * properties of any shipping address are null. The crucial properties are
	 * defined in the RequiredShippingAddressProperties property
	 *
	 * @param pTRI
	 *            the TaxRequestInfo containing the shipping address to verify
	 * @return true if the address is OK, false if the address has problems
	 * @exception PricingException
	 *                if there was a problem verifying the shipping address
	 */
	@Override
	protected boolean verifyShippingAddress(TaxRequestInfo pTRI) throws PricingException {
		logDebug("BBBTaxProcessorTaxCalculator.verifyShippingAddress: Starts");
		boolean cybersourceCallRequired = false;
		/*
		 * Below code decides whether we need call to cybersource in case of
		 * multiple shipping/HYBRID order. If any of the shipping group has home
		 * delivery we should call cybersource irrespective of their position.
		 */
		if (null != pTRI.getShippingDestinations() && pTRI.getShippingDestinations().length > 1) {
			for (int index = 0; index < pTRI.getShippingDestinations().length; index++) {
				if (null != pTRI.getShippingDestinations()[index]
						&& null != pTRI.getShippingDestinations()[index].getShippingAddress()) {
					cybersourceCallRequired = true;
					break;
				}
			}
			vlogDebug(
					"BBBTaxProcessorTaxCalculator.verifyShippingAddress: Ends, Multiple shipping destination present.");
			return cybersourceCallRequired;
		}
		vlogDebug("BBBTaxProcessorTaxCalculator.verifyShippingAddress: Ends");
		return super.verifyShippingAddress(pTRI);
	}

	/**
	 * This method is overwritten in order to recalculate taxes based on an override in TBS.
	 * The Super method is called first and if no overrides have been applied, no changes will take place. 
	 */
	protected void calculateTaxByShipping (TaxRequestInfo pTRI,
			TaxPriceInfo pPriceQuote,
			Order pOrder,
			RepositoryItem pPricingModel,
			Locale pLocale,
			RepositoryItem pProfile,
			Map pExtraParameters) throws PricingException {
		vlogDebug("BBBTaxProcessorTaxCalculator.calculateTaxByShipping: Starts");
		// call SUPER first
		super.calculateTaxByShipping(pTRI, pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
		
		// If no tax, or total is 0 already, continue...
		if (pPriceQuote == null || pPriceQuote.getAmount() == 0.0) {
			return;
		}

		// sum the status amounts
		double totalAmount = 0.0;

		List shippingGroupList = pOrder.getShippingGroups();
		Iterator shippingGroupIterator = shippingGroupList.iterator();
		Map shippingItemsTaxPriceInfos = pPriceQuote.getShippingItemsTaxPriceInfos();

		// Check for tax override settings on each shipping group 
		while (shippingGroupIterator.hasNext())
		{
			ShippingGroup group = (ShippingGroup) shippingGroupIterator.next();
			if( group instanceof BBBHardGoodShippingGroup ) {
				BBBHardGoodShippingGroup bbbgroup = (BBBHardGoodShippingGroup)group;
				TBSShippingInfo tbsInfo = bbbgroup.getTbsShipInfo();

				// Check TAX OVERRIDE flag
				TaxPriceInfo tpi =	(TaxPriceInfo)shippingItemsTaxPriceInfos.get(group.getId());
				if( tbsInfo != null && tbsInfo.isTaxOverride() ) {
					if( tbsInfo.getTaxOverrideType() == TBSConstants.ZERO ) {
						// Type = percentage
						calculateItemTaxByPercentage( bbbgroup, tpi, tbsInfo.getTaxValue() );
					}
					else {
						// Type = amount
						calculateItemTaxByAmount( bbbgroup, tpi, tbsInfo.getTaxValue() );
					}
					TaxPriceInfo taxinfo = new TaxPriceInfo();
					taxinfo.setAmount(tpi.getAmount());
					taxinfo.setCityTax(tpi.getCityTax());
					taxinfo.setCountyTax(tpi.getCountyTax());
					taxinfo.setStateTax(tpi.getStateTax());
					taxinfo.setDistrictTax(tpi.getDistrictTax());
					taxinfo.setCountryTax(tpi.getCountryTax());
					shippingItemsTaxPriceInfos.put(group.getId(), taxinfo);
				}
				totalAmount += tpi.getAmount();
			} else if( group instanceof BBBStoreShippingGroup ) {
				TaxPriceInfo tpi =	(TaxPriceInfo)shippingItemsTaxPriceInfos.get(group.getId());
				totalAmount += tpi.getAmount();
			}
		}

		// register the Order tax in the taxPriceInfo
		if (isLoggingDebug()) {
			logDebug("Total tax amount before rounding: " + totalAmount);
		}

		double roundedAmount = getPricingTools().round(totalAmount);

		if (isLoggingDebug()) {
			logDebug("Total tax amount rounded to : " + roundedAmount);
		}

		pPriceQuote.setAmount(roundedAmount);
		vlogDebug("BBBTaxProcessorTaxCalculator.calculateTaxByShipping: Ends");
	}
	
	/**
	 * This method calculates tax based on the flat tax rate passed in.  The tax is only applied to those items for which 
	 * Cybersource already decided are taxed. 
	 * 
	 * @param sg
	 * @param pTaxPriceInfo
	 * @param taxRate
	 */
	private void calculateItemTaxByPercentage( BBBHardGoodShippingGroup sg, TaxPriceInfo pTaxPriceInfo, double taxRate ) {
		double totalSGTax = 0.0;
		
		List relationships = getRelationshipsToAssignTaxTo(sg);
		Map itemsTaxPriceInfos = pTaxPriceInfo.getShippingItemsTaxPriceInfos();
	    
		if (relationships == null || relationships.size() == 0) {
			return;
		}
		    
		// Calculate tax per item using rate passed in
		//
		for (int r = 0; r < relationships.size(); r++) {
			ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship)relationships.get(r);
			CommerceItem ci = sgcir.getCommerceItem();
			TaxPriceInfo itemTaxPrice = (TaxPriceInfo)itemsTaxPriceInfos.get(ci.getId());

			if( itemTaxPrice != null && itemTaxPrice.getAmount() > 0.0 ) {
	    	
				double itemPrice = ci.getPriceInfo().getAmount();   
				double itemTax = getPricingTools().round(itemPrice * taxRate);
				totalSGTax += itemTax;
		   
				itemTaxPrice.setAmount(itemTax);
				if( isLoggingDebug() ) {
					logDebug("Setting ITEM level tax = " + itemTax);
				}
			}
		}
	    
		// Add shipping tax 
		TaxPriceInfo shipTaxPrice = (TaxPriceInfo)itemsTaxPriceInfos.get(BBBCoreConstants.SHIPPING_TAX_PRICE_INFO_KEY);
		double shipTax = getPricingTools().round( sg.getPriceInfo().getAmount() * taxRate );
		if( isLoggingDebug() ) {
			logDebug("Shipping tax = " + shipTax);
		}
		shipTaxPrice.setAmount(shipTax);
		totalSGTax += shipTax;
	    
		// Set SG level totals
		pTaxPriceInfo.setAmount(totalSGTax);
		pTaxPriceInfo.setCountyTax(totalSGTax);
		pTaxPriceInfo.setStateTax(totalSGTax);
		if( isLoggingDebug() ) {
			logDebug("Setting SG level tax = " + totalSGTax);
		}
	}

	/**
	 * This method calculates tax based on a total tax amount passed in.  The tax is distributed among the items only if those 
	 * items were already taxed by Cybersource. 
	 * 
	 * @param sg
	 * @param pTaxPriceInfo
	 * @param taxAmount
	 */
	private void calculateItemTaxByAmount( BBBHardGoodShippingGroup sg, TaxPriceInfo pTaxPriceInfo, double taxAmount ) {
		double runningTotal = 0.0;
		double shipCost = sg.getPriceInfo().getAmount();
		
		List relationships = getRelationshipsToAssignTaxTo(sg);
		Map itemsTaxPriceInfos = pTaxPriceInfo.getShippingItemsTaxPriceInfos();
	    
		if (relationships == null || relationships.size() == 0) {
			return;
		}
	    
		// First, subtotal the items in the SG that are taxable...
		double sgSubTotal = 0.0;
		for (int r = 0; r < relationships.size(); r++) {
			ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship)relationships.get(r);
			CommerceItem ci = sgcir.getCommerceItem();
			TaxPriceInfo itemTaxPrice = (TaxPriceInfo)itemsTaxPriceInfos.get(ci.getId());
			if( itemTaxPrice != null && itemTaxPrice.getAmount() > 0.0 ) {
				sgSubTotal += ci.getPriceInfo().getAmount();
			}
		}
		sgSubTotal += shipCost;
		if( isLoggingDebug() ) {
			logDebug("SG SubTotal = " + sgSubTotal);
		}

		// Find the pro-rated amount of the total tax value to assign to each item
		double proRateMult;
		for (int r = 0; r < relationships.size(); r++) {
			ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship)relationships.get(r);
			CommerceItem ci = sgcir.getCommerceItem();
			TaxPriceInfo itemTaxPrice = (TaxPriceInfo)itemsTaxPriceInfos.get(ci.getId());

			if( itemTaxPrice != null && itemTaxPrice.getAmount() > 0.0 ) {
				if( taxAmount == 0.0 ) {
					itemTaxPrice.setAmount(0.0);
				}
				else {
					double itemPrice = ci.getPriceInfo().getAmount();
					proRateMult = itemPrice / sgSubTotal;
		    
					double itemTax = getPricingTools().round(taxAmount * proRateMult);	   
					runningTotal += itemTax;
					itemTaxPrice.setAmount(itemTax);
					if( isLoggingDebug() ) {
						logDebug("Setting ITEM level tax = " + itemTax);
					}
				}
			}
		}
	    
		// Add shipping tax pro-rated
		TaxPriceInfo shipTaxPrice = (TaxPriceInfo)itemsTaxPriceInfos.get(BBBCoreConstants.SHIPPING_TAX_PRICE_INFO_KEY);
		proRateMult =  shipCost / sgSubTotal;	    
		double shipTax = getPricingTools().round( taxAmount * proRateMult );
		if( isLoggingDebug() ) {
			logDebug("Shipping tax = " + shipTax);
		}
		shipTaxPrice.setAmount(shipTax);
		runningTotal += shipTax;

		// Make sure we account for rounding...
		if( runningTotal != taxAmount ) {
			// Put remaining amount on first commerce item
			ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship)relationships.get(0);
			CommerceItem ci = sgcir.getCommerceItem();
			TaxPriceInfo itemTaxPrice = (TaxPriceInfo)itemsTaxPriceInfos.get(ci.getId());
			double diff = taxAmount - runningTotal;
			if( isLoggingDebug() ) {
				logDebug("Adding diff = " + diff);
			}
			itemTaxPrice.setAmount(itemTaxPrice.getAmount() + diff);
		}
	    
		// Assign the final amount;
		pTaxPriceInfo.setAmount(taxAmount);
		if( isLoggingDebug() ) {
			logDebug("Setting SG level tax = " + taxAmount);
		}
	}
	
	/**
	 * 
	 * Called to inititate tax calculation operation.
	 * 
	 * @param paramTaxPriceInfo
	 * @param paramOrder
	 * @param paramRepositoryItem1
	 * @param paramLocale
	 * @param paramRepositoryItem2
	 * @param paramMap
	 */
	public void priceTax(TaxPriceInfo paramTaxPriceInfo, Order paramOrder, RepositoryItem paramRepositoryItem1, Locale paramLocale, RepositoryItem paramRepositoryItem2, Map paramMap)
			throws PricingException
	{
		String str1 = "priceTax";
		PerformanceMonitor.startOperation("TaxProcessorTaxCalculator", str1);
		int i = 0;
		BBBOrder bbbOrder = null;
		try
		{
			if (paramOrder == null)
				throw new PricingException(MessageFormat.format(Constants.PARAMETER_NOT_SET, new Object[] { "pOrder", "priceTax" }));
			if (paramTaxPriceInfo == null)
				throw new PricingException(MessageFormat.format(Constants.PARAMETER_NOT_SET, new Object[] { "pPriceQuote", "priceTax" }));
			if ((paramOrder.getPriceInfo() == null) || (paramOrder.getPriceInfo().getAmount() == 0.0D))
			{
				clearTaxPriceInfo(paramTaxPriceInfo);
				return;
			}
			String str2 = paramOrder.getSiteId();
			if(paramOrder instanceof BBBOrder){
				bbbOrder = (BBBOrder) paramOrder;
				bbbOrder.getTaxOverrideMap().clear();
			}
			TaxRequestInfoImpl localTaxRequestInfoImpl = new TaxRequestInfoImpl();
			localTaxRequestInfoImpl.setOrderId(paramOrder.getId());
			localTaxRequestInfoImpl.setOrder(paramOrder);
			List<PaymentGroup> paymentGroups  = paramOrder.getPaymentGroups();
			if (paymentGroups != null)
			{
				Iterator<PaymentGroup> paymentGroupIterator  = ((List)paymentGroups).iterator();
				while (((Iterator)paymentGroupIterator).hasNext())
				{
					PaymentGroup paymentGroup = (PaymentGroup)((Iterator)paymentGroupIterator).next();
					try
					{
						if (DynamicBeans.getBeanInfo(paymentGroup).hasProperty(getBillingAddressPropertyName()))
						{
							Address localAddress1 = (Address)DynamicBeans.getPropertyValue(paymentGroup, getBillingAddressPropertyName());
							if (localAddress1 != null)
							{
								localTaxRequestInfoImpl.setBillingAddress(localAddress1);
								break; 
							}
						}
					}
					catch (IntrospectionException introspectionException)
					{
						logError("IntrospectionException Exception " + introspectionException);
					}
					catch (PropertyNotFoundException localPropertyNotFoundException)
					{
						logError("PropertyNotFoundException Exception " + localPropertyNotFoundException);
					}
				}
			}
			
			if (localTaxRequestInfoImpl.getOrder() != null) {
				localTaxRequestInfoImpl.setBillingAddress(((BBBOrderImpl) localTaxRequestInfoImpl.getOrder()).getBillingAddress());
			}
			
			List<ShippingDestination> shippingDestinations  = new LinkedList<ShippingDestination>();
			List<ShippingGroup>shippingGroups = paramOrder.getShippingGroups();
			Iterator<ShippingGroup>  localIterator = (shippingGroups).iterator();
			HashMap localHashMap1 = new HashMap();
			HashMap localHashMap2 = new HashMap();
			int shippingGroupIndex = 0;//Loop Counter
			while (localIterator.hasNext())
			{
				ShippingGroup shippingGroup = (ShippingGroup)localIterator.next();
				ShippingDestinationImpl localShippingDestinationImpl = new ShippingDestinationImpl();
				localShippingDestinationImpl.setCurrencyCode(paramOrder.getPriceInfo().getCurrencyCode());
				localShippingDestinationImpl.setShippingAmount(((ShippingGroup)shippingGroup).getPriceInfo().getAmount());
				Address localAddress2 = determineShippingAddress((ShippingGroup)shippingGroup);
				localShippingDestinationImpl.setShippingAddress(localAddress2);
				LinkedList<TaxableItem> localLinkedList = new LinkedList<TaxableItem>();
				double d2;
				if ((shippingGroup.getCommerceItemRelationships() == null) || ((shippingGroup.getCommerceItemRelationships().isEmpty())))
				{
					List<CommerceItem> commerceItems = paramOrder.getCommerceItems();
					Iterator<CommerceItem> commerceItemIterator = commerceItems.iterator();
					while (commerceItemIterator.hasNext())
					{
						CommerceItem commerceItem = commerceItemIterator.next();
						TaxableItemImpl localTaxableItemImpl = new TaxableItemImpl();
						if (isLoggingDebug())
							logDebug("rounding item: " + commerceItem.getId() + " amount for taxing: " + commerceItem.getPriceInfo().getAmount());
						d2 = getPricingTools().calculateTaxableAmount(commerceItem, paramOrder, paramLocale, paramRepositoryItem2, paramMap);
						double d3 = getPricingTools().round(d2);
						if (isLoggingDebug())
							logDebug("rounded item price for taxing to : " + d3);
						localTaxableItemImpl.setAmount(d3);
						localTaxableItemImpl.setCatalogRefId(commerceItem.getCatalogRefId());
						localTaxableItemImpl.setProductId(commerceItem.getAuxiliaryData().getProductId());
						localTaxableItemImpl.setQuantity(commerceItem.getQuantity());
						if (getTaxStatusProperty() != null)
						{
							Object localObject8 = commerceItem.getAuxiliaryData().getCatalogRef();
							if (localObject8 != null)
								try
							{
									Object localObject9 = DynamicBeans.getPropertyValue(localObject8, getTaxStatusProperty());
									if (localObject9 instanceof String)
									{
										if (isLoggingDebug())
											logDebug("Setting taxStatus to " + ((String)localObject9));
										localTaxableItemImpl.setTaxStatus((String)localObject9);
									}
									else
									{
										if (isLoggingDebug())
											logDebug("Setting taxStatus to " + String.valueOf(localObject9));
										localTaxableItemImpl.setTaxStatus(String.valueOf(localObject9));
									}
							}
							catch (PropertyNotFoundException localPropertyNotFoundException2)
							{
								if (isLoggingDebug())
									logDebug("Could not find taxStatus property for " + localObject8, localPropertyNotFoundException2);
							}
						}
						localLinkedList.add(localTaxableItemImpl);
						localHashMap1.put(commerceItem, Long.valueOf(((CommerceItem)commerceItem).getQuantity()));
						localHashMap2.put(commerceItem, Double.valueOf(d3));
					}
				}
				else
				{
					TaxableItemImpl  taxableItem =null;	
					Iterator<ShippingGroupCommerceItemRelationship> sgci = shippingGroup.getCommerceItemRelationships().iterator();
					while (sgci.hasNext())
					{
						ShippingGroupCommerceItemRelationship shipComRel = (ShippingGroupCommerceItemRelationship)sgci.next();
						int j = 0;
						d2 = 0.0D;
						if ((getPricingTools().isShippingSubtotalUsesAverageItemPrice()) || (((ShippingGroupCommerceItemRelationship)shipComRel).getRange() == null)){
							j = 1;}
						if (j != 0){
							taxableItem = (TaxableItemImpl)createTaxableItemForRelationshipByAverage((CommerceItemRelationship)shipComRel, localTaxRequestInfoImpl, paramOrder, paramLocale, paramRepositoryItem2, paramMap);
							if (taxableItem == null){
								continue;}
							long l = 0L;
							d2 += ((TaxableItemImpl)taxableItem).getAmount();
							l = checkTaxableItemByAverage((TaxableItemImpl)taxableItem, (ShippingGroupCommerceItemRelationship)shipComRel, localHashMap1, localHashMap2, paramOrder, paramLocale, paramRepositoryItem2, paramMap);
							if (l > 0L)
							{
								localLinkedList.add(taxableItem);
								localHashMap1.put(((ShippingGroupCommerceItemRelationship)shipComRel).getCommerceItem(), Long.valueOf(l));
								Double localObject8 = (Double)localHashMap2.get(((ShippingGroupCommerceItemRelationship)shipComRel).getCommerceItem());
								double d4 = 0.0D;
								if (localObject8 != null){
									d4 = ((Double)localObject8).doubleValue();}
								localHashMap2.put(((ShippingGroupCommerceItemRelationship)shipComRel).getCommerceItem(), Double.valueOf(d4 + ((TaxableItemImpl)taxableItem).getAmount()));
							}
						}else{
							CommerceItem commerceItem=shipComRel.getCommerceItem();//Penny Issue|SAP-2040|BBBH-6447 :: Code to create line items for each DPI to be sent to CyberSource
							List priceBeans = shipComRel.getCommerceItem().getPriceInfo().getCurrentPriceDetailsForRange(shipComRel.getRange());//Retrieve DPI's[priceBeans] for the current CommerceItem within this shippingGroup indicating using Range.
							Collections.sort(priceBeans, Collections.reverseOrder(new TaxCalculatorComparator()));//Sort the priceBeans in descending order of quantity.
							Map<String, String> ecoFeeItemMap = ((BBBShippingGroup)shippingGroup).getEcoFeeItemMap();//Retrieve EcoFeeItemMap for the shippingGroup which is applicable only on BedBathCanada.
							//Determine whether tax calculation should be skipped for the current commerceItem. For LTLDeliveryCharge, LTLAssemblyFee and EcoFeeCommerceItem, taxableItems are created in the context of the main commerceItem.
							boolean skipCreatingTaxableItem = (commerceItem instanceof LTLDeliveryChargeCommerceItem || commerceItem instanceof LTLAssemblyFeeCommerceItem || commerceItem instanceof EcoFeeCommerceItem);
							vlogDebug("skipCreatingTaxableItem is {0}, commerceItem is {1}, shippingGroup is {2}, taxableItemList Size is {3}, commerceItemType - {4}", skipCreatingTaxableItem, 
									commerceItem.getId(), shippingGroup.getId(), localLinkedList.size(), commerceItem.getCommerceItemClassType());
							if(!skipCreatingTaxableItem){
								//For Delivery Surcharge Capping, we have to split the item with Quantity[qty] to 2 items with Quantity[qty-1] and Quantity[1]. isSplitReq specified whether this action is to be done or not.
								boolean isSplitReq = false;
								NonMerchandiseCommerceItem nonMerchCommerceItem = null;//LTLDeliverySurchargeCommerceItem will be set to nonMerchCommerceItem.
								DetailedItemPriceInfo dpi = null;//CurrentDPI in the iteration of priceBeans.
								BBBCommerceItem bbbCommerceItem = null;//bbbCommerceItem used to determine nonMerchCommerceItem and whether split is required[isSplitReq].
								String ecoFeeItemId = "";
								if(!BBBUtility.isMapNullOrEmpty(ecoFeeItemMap))//Retrieve ecoFeeItemId if present.
									ecoFeeItemId = ecoFeeItemMap.get(commerceItem.getId());
								//Determine whether Split of DPI is required when LTL Delivery Surcharge Capping is applied on items with multiple quantities.
								//Applicable only for LTL Items. Split is done only when Delivery Surcharge Capping is applied. LTL Assembly charges are not capped.
								if(commerceItem instanceof BBBCommerceItem){
									bbbCommerceItem = (BBBCommerceItem) shipComRel.getCommerceItem();
									if(BBBUtility.isNotEmpty(bbbCommerceItem.getDeliveryItemId())){
										nonMerchCommerceItem = (NonMerchandiseCommerceItem) paramOrder.getCommerceItem(bbbCommerceItem.getDeliveryItemId());//Retrieve LTL DeliveryCommerceItem.
										vlogDebug("DeliveryItemId for the commerceItem is {0}", nonMerchCommerceItem.getId());
										double nonMerchCommerceItemTotal = nonMerchCommerceItem.getPriceInfo().getAmount();//As LTL Items do not span across shippingGroups, the entire nonMerchCommerceItem priceInfo's amount is put in this shippingGroup.
										double nonMerchCommerceItemQuantity = bbbCommerceItem.getQuantity();//As LTL Items do not span across shippingGroups, we use the commerceItem's quantity for determining whether split is required or not.
										//Calculates whether a penny difference might occur when average is taken and used to assign delivery item amounts.
										isSplitReq = this.isSplitRequired(nonMerchCommerceItemTotal, nonMerchCommerceItemQuantity);
									}
								}
								vlogDebug("isSplitReq - {0}, ecoFeeItemId - {1}", isSplitReq, ecoFeeItemId);
								TaxableItem taxableItemForDPI = null;
								//Iterate over DPI's[priceBeans] to create taxableItem's.
								for(int loopCounter=0;loopCounter<priceBeans.size();loopCounter++){
									/**
									 * 
									 * The last DPI in the iteration is the DPI which is split[if multiple quantity], or penny adjusted[if single quantity].
									 * Two cases are possible.
									 * 
									 * 1. Item is LTL[nonMerchCommerceItem is null].
									 * 	  a. Creates taxableItem's for all the DPI's except the last one.
									 * 	  b. Checks whether item is lastItem and split is required.
									 *    c. If split is required and is lastItem, it depends on quantity.
									 *    d. If the dpi is of multiple quantity, the item is split into two items with quantity[qty-1] and quantity[1].
									 *    e. If the dpi is single quantity, the penny difference is added into the dpi's amount.
									 *    f. In each iteration, we create different taxableItem's for Delivery, Assembly and EcoFee.
									 * 2. Item is not LTL[nonMerchCommerceItem is null].
									 * 	  a. Creates taxableItem's based on quantity and price.
									 * 	  b. Add them to the list.
									 *    c. Check if item is ecoFeeCommerceItem. If it is, then create taxableItem for EcoFee and add it to the list. 
									 * 
									 */
									dpi = (DetailedItemPriceInfo) priceBeans.get(loopCounter);
									if(nonMerchCommerceItem!=null){
										long quantity = dpi.getQuantity();
										boolean lastItem = priceBeans.size() - 1 == loopCounter ;//Indicates lastItem of the priceBeans in the relationship.
										boolean splitLastItem = (quantity > 1 && lastItem && isSplitReq);//Indicates whether the current priceBean with quantity > 1 has to be split because of delivery surcharge capping.
										boolean adjustInLastItem = (quantity == 1 && lastItem && isSplitReq);//Indicates whether the current priceBean with quantity 1 needs to be priceAdjusted or not.
										//Either splitLastItem will be true/adjustInLastItem will be true.
										quantity = splitLastItem ? quantity - 1 : quantity;//Decrease Quantity if it is lastItem and item needs to be split.
										vlogDebug("Relationship Quantity is {0}, splitLastItem is {1}, adjustInLastItem is {2}, Adjusted Quantity is {3}", dpi.getQuantity(), splitLastItem, adjustInLastItem, quantity);
										taxableItemForDPI = createTaxableItemForDPI(commerceItem, quantity, dpi);
										localLinkedList.add(taxableItemForDPI);
										//To Maintain backward compatability, update the amount taxed so far and quantity in localHashMap2 and localHashMap1.
										this.updateAmountAndRelationshipValuesInMap(localHashMap1, localHashMap2, dpi.getAmount(), quantity, commerceItem);
										//Create TaxableItem's for LTLDelivery, LTLAssembly items.
										this.createItemDPISubTaxableInfo(paramOrder, localLinkedList, dpi, commerceItem, 
												adjustInLastItem, quantity, localHashMap1, localHashMap2);
										//Create TaxableItem if ecoFeeItemId is not empty.
										if(BBBUtility.isNotEmpty(ecoFeeItemId))
											this.createItemDPIEcoFeeTaxableInfo(paramOrder, localLinkedList, dpi, commerceItem, 
													adjustInLastItem, ecoFeeItemId, localHashMap1, localHashMap2);
										if(splitLastItem){
											//If item is last and needs to be split, taxableItem with quantity 1 and priceAdjusted needs to be created. Price Adjustment is done for LTLDeliveryChargeCommerceItem.
											quantity = 1;
											vlogDebug("Creating TaxableItem for the lastItem of the DPI with quantity - {0} as splitLastItem is true.", quantity);
											taxableItemForDPI = createTaxableItemForDPI(commerceItem, quantity, dpi);
											localLinkedList.add(taxableItemForDPI);
											this.updateAmountAndRelationshipValuesInMap(localHashMap1, localHashMap2, dpi.getAmount(), quantity, commerceItem);
											//Create TaxableItem's for LTLDelivery, LTL Assembly CommerceItem's. Penny Adjustment is done here.
											this.createItemDPISubTaxableInfo(paramOrder, localLinkedList, dpi, commerceItem, 
													splitLastItem , quantity, localHashMap1, localHashMap2);
											//Create EcoFee TaxableItem if present.
											if(BBBUtility.isNotEmpty(ecoFeeItemId))
												this.createItemDPIEcoFeeTaxableInfo(paramOrder, localLinkedList, dpi, commerceItem, 
														splitLastItem, ecoFeeItemId, localHashMap1, localHashMap2);
											vlogDebug("Created TaxableItem for the penny adjusted item.");
										}
									}else{
										//Create taxableItem for Non-LTL Items/GiftWrapCommerceItem's.
										vlogDebug("Creating TaxableItem for Non-LTL CommerceItem with quantity - {0}", dpi.getQuantity());
										taxableItemForDPI = createTaxableItemForDPI(commerceItem, dpi.getQuantity(), dpi);
										localLinkedList.add(taxableItemForDPI);
										this.updateAmountAndRelationshipValuesInMap(localHashMap1, localHashMap2, dpi.getAmount(), dpi.getQuantity(), commerceItem);
										//Create TaxableItem for EcoFeeCommerceItem if ecoFeeItemId is non-empty.
										if(BBBUtility.isNotEmpty(ecoFeeItemId))
											this.createItemDPIEcoFeeTaxableInfo(paramOrder, localLinkedList, dpi, commerceItem, 
													false, ecoFeeItemId, localHashMap1, localHashMap2);
										vlogDebug("Created TaxableItem for Non-LTL CommerceItem.");
									}
									vlogDebug("Size of taxableItemLinkedList is {0}", localLinkedList.size());
								}
								vlogDebug("Size of taxableItemLinkedList is {0}", localLinkedList.size());
							}
						}
					}
				}
				double d1 = 0.0D;
				if (shippingGroup.getId() == null)
				{
					if (isLoggingDebug())
						logDebug("DANGER: group ID is null");
				}
				else
				{
					OrderPriceInfo localObject7 = (OrderPriceInfo)paramOrder.getPriceInfo().getTaxableShippingItemsSubtotalPriceInfos().get(((ShippingGroup)shippingGroup).getId());
					if (localObject7 == null){
						if (isLoggingDebug())
							logDebug("WARNING: no entry in order's group taxable subtotal map found for group ID: " + ((ShippingGroup)shippingGroup).getId());
					}  else
						d1 = ((OrderPriceInfo)localObject7).getAmount();
				}
				if (isLoggingDebug())
					logDebug("shipping group subtotal for taxing: " + d1);
				localShippingDestinationImpl.setTaxableItemAmount(d1);
				TaxableItem[] localObject7 = new TaxableItem[localLinkedList.size()];
				localObject7 = (TaxableItem[])(TaxableItem[])localLinkedList.toArray(localObject7);
				localShippingDestinationImpl.setTaxableItems(localObject7);
				if (localShippingDestinationImpl.getTaxableItemAmount() > 0.0D){
					((List)shippingDestinations).add(localShippingDestinationImpl);
					if(bbbOrder!=null){
						//Used in CyberSourceTax during Tax Calculation. This transient property is used to determine whether a TaxRequest has to be made when the shippingGroup's Tax is overriden to zero.
						bbbOrder.getTaxOverrideMap().put(shippingGroupIndex, isTaxOverride(shippingGroup));
						//Counter used to determine the index in shippingDestinations.
						vlogDebug("ShippingGroup TaxOverride Flag in order - {0}, shippingGroupIndex - {1}", bbbOrder.getTaxOverrideMap().get(shippingGroupIndex), shippingGroupIndex);
						shippingGroupIndex++;
					}
				}
			}
			ShippingDestination[]  localObject4 = new ShippingDestination[((List)shippingDestinations).size()];
			localObject4 = (ShippingDestination[])(ShippingDestination[])((List)shippingDestinations).toArray(localObject4);
			localTaxRequestInfoImpl.setShippingDestinations(localObject4);
			if (((List)shippingDestinations).size() > 0){
				if (getSiteConfiguration().getCalculateTaxByShipping(str2)){
					calculateTaxByShipping(localTaxRequestInfoImpl, paramTaxPriceInfo, paramOrder, paramRepositoryItem1, paramLocale, paramRepositoryItem2, paramMap);
				}else{
					calculateTax(localTaxRequestInfoImpl, paramTaxPriceInfo, paramOrder, paramRepositoryItem1, paramLocale, paramRepositoryItem2, paramMap);
				}
			}
			saveDSLineItemCommerceItemRel(paramOrder);
		}
		catch (CyberSourceException localPerfStackMismatchException3)
		{
			if (isLoggingError())
			{
				String  localObject1 = localPerfStackMismatchException3.getMessage();
				if (isLoggingError())
					logError(CyberSourceUtils.format(MessageConstant.CYBER_SOURCE_NOT_PROPERLY_CONFIGURED, new Object[] { localObject1 }));
			}
		} catch (CommerceItemNotFoundException e) {
			logError("CommerceItemNotFoundException Exception "+ e);
			if (isLoggingDebug())
			logDebug("CommerceItemNotFoundException Exception ", e);
		} catch (InvalidParameterException e) {
			logError("Invalid Parameter Exception " , e);
		}
		finally
		{
			//Remove the TaxOverride specialInstruction form the order which if persisted will cause SQLException.
			//It is not needed as it is only used a temporary map to determine whether tax calculation has to be made or skipped by creating zero tax reply.
			//paramOrder.getSpecialInstructions().remove(BBBCoreConstants.TAX_OVERRIDE_SPECIAL_INSTRUCTION);
			if(bbbOrder!=null){
				bbbOrder.setTaxOverrideMap(null);
			}
			try
			{
				if (i == 0)
				{
					PerformanceMonitor.endOperation("TaxProcessorTaxCalculator", str1);
					i = 1;
				}
			}
			catch (PerfStackMismatchException localPerfStackMismatchException4)
			{
				if (isLoggingWarning())
					logWarning(localPerfStackMismatchException4);
			}
		}
	}
	
	/**
	 * Determines whether split is required by calculating Average Amount and then calculating total from the quantity to check whether penny difference might occur.
	 * 
	 * @param totalAmount
	 * @param quantity
	 * @return
	 */
	private boolean isSplitRequired(double totalAmount, double quantity){
		double averagePrice = this.getPricingTools().round(totalAmount/quantity);
		double averageTotal = this.getPricingTools().round(averagePrice * quantity);
		return totalAmount!=averageTotal ? true : false;
	}
	
	/**
	 * Updates the amount taxedSoFar and quantity in localHashMap2 and localHashMap1 respectively with commerceItem as the key in the map.
	 * 
	 * @param localHashMap1
	 * @param localHashMap2
	 * @param amount
	 * @param quantity
	 * @param commerceItem
	 */
	private void updateAmountAndRelationshipValuesInMap(Map localHashMap1, Map localHashMap2, double amount, long quantity, Object commerceItem){
		long existingQuantity = localHashMap1.containsKey(commerceItem) ? ((Long)localHashMap1.get(commerceItem)).longValue() : 0;
		double existingAmount = localHashMap2.containsKey(commerceItem) ? ((Double)localHashMap2.get(commerceItem)).doubleValue() : 0.0;
		localHashMap1.put(commerceItem, Long.valueOf(existingQuantity + quantity));
		localHashMap2.put(commerceItem, Double.valueOf(amount + existingAmount));
	}
	
	/**
	 * Saves the newly created DSLineItem's in commerceItem. Deletes all the dsLineItem's which were created and previously associated with the commerceItem
	 * for all the DPI's. 
	 * 
	 * 1. Clears the commerceItem of all DSLineItemTaxInfo relations.
	 * 2. Re-Associates the commerceItem-DSLineItem relations by iterating over all DPI's of the commerceItem.
	 * 
	 * @param order
	 */
	@SuppressWarnings({"rawtypes","unchecked"})
	private void saveDSLineItemCommerceItemRel(Order order){
		vlogDebug("Saving DSLineItem Relations to commerceItem's. saveDSLineItemCommerceItemRel() Method Starts.");
		List<CommerceItem> commerceItems = order.getCommerceItems();
		BBBCommerceItem currentCI = null;
		MutableRepositoryItem currentCIRepItem = null;
		for(CommerceItem commerceItem : commerceItems){
			if(commerceItem instanceof BBBCommerceItem){
				currentCI = (BBBCommerceItem) commerceItem;
				currentCIRepItem = currentCI.getRepositoryItem();
				Set dsLineItemRel = (Set) currentCIRepItem.getPropertyValue(BBBCoreConstants.DS_LINE_ITEM_REL);
				dsLineItemRel = dsLineItemRel == null ? new HashSet<>() : dsLineItemRel; 
				vlogDebug("commerceItemId - {0}, commerceItemType - {1} dsLineItemRel Size before - {2}", commerceItem.getId(), commerceItem.getCommerceItemClassType(), dsLineItemRel.size());
				ItemPriceInfo ciPriceInfo = currentCI.getPriceInfo();
				List<AmountInfo> priceBeans = ciPriceInfo.getCurrentPriceDetails();
				Class dpiClass = null;
				try {
					dpiClass = Class.forName(this.getOrderManager().getOrderTools().getItemDescriptorToBeanNameMap().getProperty(BBBCoreConstants.DETAILED_ITEM_PRICE_INFO));
				} catch (ClassNotFoundException e) {
					logError("Invalid class configured for detailedItemPriceInfo", e);
				}
				if(dpiClass!=null){
					dsLineItemRel.clear();
					List tempDSLineItemRel = new ArrayList<>();
					for(AmountInfo priceBean : priceBeans){
						Object customDPI = dpiClass.cast(priceBean);
						List dsLineItems = null;
						try {
							dsLineItems = (List) DynamicBeans.getPropertyValue(customDPI, BBBCoreConstants.DPI_DSLINEITEM_PROPERTY);
							OrderRepositoryUtils.getRepositoryItems(this.getOrderRepository(), dsLineItems, tempDSLineItemRel, this.getOrderManager().getOrderTools());
						} catch (PropertyNotFoundException e) {
							logError("PropertyNotFoundException occured while saving DSLineItems", e);
						} catch (RepositoryException e) {
							logError("RepositoryException occured while saving DSLineItems", e);
						} catch (IntrospectionException e) {
							logError("IntrospectionException occured while saving DSLineItems", e);
						}
						dsLineItemRel.addAll(tempDSLineItemRel);
					}
				}
				currentCIRepItem.setPropertyValue(BBBCoreConstants.DS_LINE_ITEM_REL, dsLineItemRel);
				vlogDebug("commerceItemId - {0}, commerceItemType - {1}, dsLineItemRel Size - {2}", commerceItem.getId(), commerceItem.getCommerceItemClassType(), dsLineItemRel.size());
			}
		}
		vlogDebug("saveDSLineItemCommerceItemRel() Method Ends.");
	}
	
	/**
	 * 
	 * Create taxableItem for EcoFeeCommerceItem.
	 * 
	 * 1. Retrieves the ecoFeeCommerceItem.
	 * 2. Determines the unitPrice by determining whether the commerceItem is onSale or not. If it is onSale, use SalePrice, otherwise use ListPrice.
	 * 3. Creates TaxableItem using the above calculated ecoFeeUnitPrice.
	 * 
	 * @param paramOrder
	 * @param localLinkedList
	 * @param unitPriceBean
	 * @param commerceItem
	 * @param useSplit
	 * @param ecoFeeItemId
	 * @param localHashMap1
	 * @param localHashMap2
	 */
	private void createItemDPIEcoFeeTaxableInfo(Order paramOrder, LinkedList<TaxableItem> localLinkedList, DetailedItemPriceInfo unitPriceBean, CommerceItem commerceItem
			, boolean useSplit, String ecoFeeItemId,Map localHashMap1, Map localHashMap2) {
		try {
			vlogDebug("Creating TaxableItem for ecoFeeCommerceItem - {0} of commerceItem - {1}", ecoFeeItemId, commerceItem.getId());
			EcoFeeCommerceItem ecoFeeCommerceItem = null;
			if(BBBUtility.isNotEmpty(ecoFeeItemId)){
				ecoFeeCommerceItem = (EcoFeeCommerceItem) paramOrder.getCommerceItem(ecoFeeItemId);
			}
			ItemPriceInfo ecoFeeItemPriceInfo = ecoFeeCommerceItem.getPriceInfo();
			double ecoFeeUnitPrice = this.checkCurrentPrice(ecoFeeItemPriceInfo.isOnSale(), ecoFeeItemPriceInfo.getListPrice(), ecoFeeItemPriceInfo.getSalePrice());
			vlogDebug("ecoFeeUnitPrice - {0}, ecoFeeItem onSale() - {1}", ecoFeeUnitPrice, ecoFeeItemPriceInfo.isOnSale());
			TaxableItemImpl ti = (TaxableItemImpl) this.poupulateTaxableItem(ecoFeeCommerceItem, ecoFeeUnitPrice, unitPriceBean.getQuantity());
			localLinkedList.add(ti);
			this.updateAmountAndRelationshipValuesInMap(localHashMap1, localHashMap2, ecoFeeItemPriceInfo.getAmount(), unitPriceBean.getQuantity(), ecoFeeCommerceItem);
			vlogDebug("Created TaxableItem for ecoFeeCommerceItem - {0} of commerceItem - {1}", ecoFeeItemId, commerceItem.getId());
		} catch (CommerceItemNotFoundException e) {
			logError("CommerceItemNotFoundException occurred for commerceItem with " + ecoFeeItemId + e);
		} catch (InvalidParameterException e) {
			logError("InvalidParameterException occurred for commerceItem with " + ecoFeeItemId + e);
		}
	}
	
	/**
	 * Creates taxableItem's for LTLDeliveryCharge and LTLAssemblyCharge CommerceItem's. If adjust is true[which might be true for last DPI], pennyDifference is adjusted.
	 * 
	 * 1. Determines whether the deliveryItem is present for the current commerceItem.
	 * 2. If adjust is passed as true, adds the penny difference into the amount.
	 * 3. Creates taxableItem for DeliveryItem.
	 * 4. Determines whether AssemblyItem is present for the current commerceItem.
	 * 5. Checks whether the assemblyCommerceItem is onSale. If it is onSale, use SalePrice, otherwise use Listprice.
	 * 6. Creates taxableItem for AssemblyCommerceItem.
	 * 
	 * @param paramOrder
	 * @param localLinkedList
	 * @param unitPriceBean
	 * @param commerceItem
	 * @param adjust
	 * @param quantity
	 * @param localHashMap1
	 * @param localHashMap2
	 */
	private void createItemDPISubTaxableInfo(Order paramOrder,
			LinkedList<TaxableItem> localLinkedList, DetailedItemPriceInfo unitPriceBean, CommerceItem commerceItem,
			boolean adjust, long quantity, Map localHashMap1, Map localHashMap2) {
		try {
			BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) commerceItem;
			String deliveryItemId = bbbCommerceItem.getDeliveryItemId();
			String assemblyItemId = bbbCommerceItem.getAssemblyItemId();
			vlogDebug("createItemDPISubTaxableInfo Method starts. DeliveryItemId :- {0}, AssemblyItemId - {1}, adjust :- {2}, quantity :- {3}, commerceItem :- {4}", deliveryItemId, assemblyItemId, adjust, quantity, commerceItem.getId());
			if(BBBUtility.isNotEmpty(deliveryItemId)){
				LTLDeliveryChargeCommerceItem deliveryCommerceItem = (LTLDeliveryChargeCommerceItem) paramOrder.getCommerceItem(deliveryItemId);
				double shipSurchargeUnitPrice = this.getPricingTools().round(deliveryCommerceItem.getPriceInfo().getAmount()/bbbCommerceItem.getQuantity());
				if(adjust){
					//Adjust should be true for the last item with quantity 1.
					double pennyDifference = this.getPricingTools().round(deliveryCommerceItem.getPriceInfo().getAmount() - (shipSurchargeUnitPrice * bbbCommerceItem.getQuantity()));
					vlogDebug("Adjust is {0}. Average Price - {1}, pennyDifference - {2}", adjust, shipSurchargeUnitPrice, pennyDifference);
					shipSurchargeUnitPrice += pennyDifference;
				} 
				TaxableItemImpl ti=(TaxableItemImpl) poupulateTaxableItem(deliveryCommerceItem,shipSurchargeUnitPrice,quantity);
				localLinkedList.add(ti);
				this.updateAmountAndRelationshipValuesInMap(localHashMap1, localHashMap2, deliveryCommerceItem.getPriceInfo().getAmount(), quantity, deliveryCommerceItem);
			}
			if(BBBUtility.isNotEmpty(assemblyItemId)){
				vlogDebug("Creating TaxableItem for AssemblyItem with Id :- {0}", assemblyItemId);
				LTLAssemblyFeeCommerceItem assemblyCommerceItem = (LTLAssemblyFeeCommerceItem) paramOrder.getCommerceItem(assemblyItemId);
				double shipSurchargeUnitPrice = this.getPricingTools().round(assemblyCommerceItem.getPriceInfo().getAmount()/bbbCommerceItem.getQuantity());
				TaxableItemImpl ti=(TaxableItemImpl) poupulateTaxableItem(assemblyCommerceItem,shipSurchargeUnitPrice,quantity);
				localLinkedList.add(ti); 
				this.updateAmountAndRelationshipValuesInMap(localHashMap1, localHashMap2, assemblyCommerceItem.getPriceInfo().getAmount(), quantity, assemblyCommerceItem);
				vlogDebug("Created TaxableItem for AssemblyItem with Id :- {0}", assemblyItemId);
			}
		} catch (CommerceItemNotFoundException e) {
			logError("CommerceItem NotFound Exception " + e);
		} catch (InvalidParameterException e) {
			logError("Invalid Parameter Exception " + e);
		}
		vlogDebug("createItemDPISubTaxableInfo ends.");
	}
	
	/**
	 * Checks whether Item is onSale and salePrice > 0 and returns salePrice/listPrice.
	 * 
	 * @param onSale
	 * @param listPrice
	 * @param salePrice
	 * @return
	 */
	private double checkCurrentPrice(boolean onSale, double listPrice, double salePrice){
		if(onSale){
			return salePrice > 0.0 ? salePrice : listPrice;
		}else{
			return listPrice;
		}
	}
	
	/**
	 * Creates taxableItem for the NonMerchandiseCommerceItem with given quantity and unitPrice.
	 * 
	 * @param nonMerchcommerceItem
	 * @param unitPrice
	 * @param quantity
	 * @return
	 */
	private TaxableItem poupulateTaxableItem(NonMerchandiseCommerceItem nonMerchcommerceItem,double unitPrice,long quantity) {
		TaxableItemImpl ti = new TaxableItemImpl();
		ti.setQuantity(quantity);
		if (isLoggingDebug())
		logDebug("Populating Line items Taxable items " + nonMerchcommerceItem.getCatalogRefId());
		double roundedAmount = getPricingTools().round(unitPrice);
		ti.setAmount(roundedAmount);
		String catRef=nonMerchcommerceItem.getCatalogRefId();
		ti.setCatalogRefId(catRef);
		ti.setProductId(nonMerchcommerceItem.getAuxiliaryData().getProductId());
		if (getTaxStatusProperty() != null) {
			Object catalogRef = nonMerchcommerceItem.getAuxiliaryData().getCatalogRef();
			if (catalogRef != null) {
				try {
					Object taxStatus = DynamicBeans.getPropertyValue(catalogRef, getTaxStatusProperty());
					if (taxStatus instanceof String){
						ti.setTaxStatus((String)taxStatus);
					}
				}
				catch (PropertyNotFoundException exc) {
					if (isLoggingError())
						logError("Could not find taxStatus property for " + catalogRef, exc);
				}
			}
		}
		return ti;
	}
	/**
	 * 
	 * @param pValue
	 * @return
	 */
	private BigDecimal toBigDecimal(double pValue) {
		return BigDecimal.valueOf(getPricingTools().round(pValue, 2));
	}
	/**
	 * 
	 * @param paramTaxPriceInfo
	 */
	private void clearTaxPriceInfo(TaxPriceInfo paramTaxPriceInfo) {
		paramTaxPriceInfo.setAmount(0.0D);
		paramTaxPriceInfo.setCityTax(0.0D);
		paramTaxPriceInfo.setCountyTax(0.0D);
		paramTaxPriceInfo.setStateTax(0.0D);
		paramTaxPriceInfo.setDistrictTax(0.0D);
		paramTaxPriceInfo.setCountryTax(0.0D);
	}
	/**
	 * 
	 * @param pTaxableItem
	 * @param pRelationship
	 * @param pTaxedItems
	 * @param pTaxedItemAmounts
	 * @param pOrder
	 * @param pLocale
	 * @param pProfile
	 * @param pExtraParameters
	 * @return
	 * @throws PricingException
	 */
	private long checkTaxableItemByAverage(TaxableItemImpl pTaxableItem, 
			ShippingGroupCommerceItemRelationship pRelationship, 
			Map pTaxedItems,
			Map pTaxedItemAmounts,
			Order pOrder,
			Locale pLocale,
			RepositoryItem pProfile,
			Map pExtraParameters)
					throws PricingException
	{
		if (pTaxableItem != null) {
			CommerceItem item = pRelationship.getCommerceItem();
			
			long relationshipQuantity = 0;
			if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
				relationshipQuantity = pRelationship.getQuantity();
			} else if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING) {
				relationshipQuantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(item);
			} else {
				throw new PricingException(MessageFormat.format(Constants.BAD_SHIPPING_GROUP_TYPE,
						new Object [] {
								Integer.valueOf(pRelationship.getRelationshipType())
				}));
			}
			
			// if there's no quantity in this rel, skip it
			if (relationshipQuantity == 0) {
				if (isLoggingDebug()) {
					logDebug("no quantity of the item: " + item.getId() + " in this relationship to tax, continuing.");
				}
				return 0;
			}
			
			if (isLoggingDebug()) {
				logDebug("rounding the price of " + relationshipQuantity + " units of the item: " + item.getId() + " : " + pTaxableItem.getAmount());
			}
			// round the taxable item's price.
			// If these are the last units being priced, round and add the leftovers.
			double roundedAmount = getPricingTools().roundDown(pTaxableItem.getAmount());
			if (isLoggingDebug()) {
				logDebug("rounded the price of " + relationshipQuantity + " units of the item to: " + roundedAmount);
			}
			pTaxableItem.setAmount(roundedAmount);
			long quantityAlreadyTaxed = 0;
			Long quantityTaxed = (Long) pTaxedItems.get(item);
			if (quantityTaxed != null) {
				quantityAlreadyTaxed = quantityTaxed.longValue();
			}
			
			if (isLoggingDebug()) {
				logDebug("quantity of the item: " + item.getId() + " already taxed: " + quantityAlreadyTaxed);
			}
			
			if (isLoggingDebug()) {
				logDebug("quantity in this relationship: " + relationshipQuantity);
			}
			
			if (isLoggingDebug()) {
				logDebug("total quantity of the item: " + item.getQuantity());
			}
			// if we're about to tax the last of an item, add the leftovers to its amount
			if (quantityAlreadyTaxed + relationshipQuantity == item.getQuantity()) {
				if (isLoggingDebug()) {
					logDebug("calculating leftovers from rounding unit prices down");
				}
				// the leftovers are the remainders from rounding every unit of the CommerceItem
				double averageUnitPrice =
						item.getPriceInfo().getAmount() / item.getQuantity();
				// if we divided by zero... use zero instead
				if(Double.isNaN(averageUnitPrice) || Double.isInfinite(averageUnitPrice)) {
					if(isLoggingDebug()) {
						logDebug(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "checkTaxableItemByAverage",
								Double.toString(item.getPriceInfo().getAmount()), 
								Double.toString(item.getQuantity()) ));
					}
					averageUnitPrice = 0.0;
				}
				if (isLoggingDebug()) {
					logDebug("average unit price: " + averageUnitPrice);
				}
				double roundedAveragePrice = getPricingTools().roundDown(averageUnitPrice);
				if (isLoggingDebug()) {
					logDebug("rounded average price: " + roundedAveragePrice);
				}
				if (isLoggingDebug()) {
					logDebug("total item amount: " + item.getPriceInfo().getAmount());
				}
				if (isLoggingDebug()) {
					logDebug("rounded unit price times quantity of: " + item.getQuantity() + " : " + ( roundedAveragePrice * item.getQuantity()));
				}
				double leftovers = item.getPriceInfo().getAmount() - (roundedAveragePrice * item.getQuantity());
				if (isLoggingDebug()) {
					logDebug("adding leftovers: " + leftovers);
				}
				double withLeftovers = pTaxableItem.getAmount() + leftovers;
				if (isLoggingDebug()) {
					logDebug("rounding taxable item amount with leftovers");
				}
				double rounded = getPricingTools().roundDown(withLeftovers);
				if (isLoggingDebug()) {
					logDebug("rounded with leftovers to: " + rounded);
				}
				pTaxableItem.setAmount(rounded);
			} // end if we need to add leftovers
			return quantityAlreadyTaxed + relationshipQuantity;
		} // end if taxableItem is not null
		else {
			return 0;
		}
}
	/**
	 * 
	 * @param pTaxableItem
	 * @param pRelationship
	 * @param pTaxedItems
	 * @param pTaxedItemAmounts
	 * @param pTotalTaxAmount
	 * @param pOrder
	 * @param pLocale
	 * @param pProfile
	 * @param pExtraParameters
	 * @return
	 * @throws PricingException
	 */
	private long checkTaxableItem(TaxableItemImpl pTaxableItem, 
			ShippingGroupCommerceItemRelationship pRelationship, 
			Map pTaxedItems,
			Map pTaxedItemAmounts,
			double pTotalTaxAmount,
			Order pOrder,
			Locale pLocale,
			RepositoryItem pProfile,
			Map pExtraParameters)
					throws PricingException
	{
		if (pTaxableItem != null) {
			// if this relationship doesn't have a range, go ahead and use the old method
			// since we could't get a reliable subtotal for this relationship without it
			if(pRelationship.getRange() == null)
				return checkTaxableItemByAverage(pTaxableItem, pRelationship, pTaxedItems, pTaxedItemAmounts,
						pOrder, pLocale, pProfile, pExtraParameters);
			
			CommerceItem item = pRelationship.getCommerceItem();
			long relationshipQuantity = 0;
			if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
				relationshipQuantity = pRelationship.getQuantity();
			} else if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING) {
				relationshipQuantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(item);
			} else {
				throw new PricingException(MessageFormat.format(Constants.BAD_SHIPPING_GROUP_TYPE,
						new Object [] {
								Integer.valueOf(pRelationship.getRelationshipType())
				}));
			}
			// if there's no quantity in this rel, skip it
			if (relationshipQuantity == 0) {
				if (isLoggingDebug()) {
					logDebug("no quantity of the item: " + item.getId() + " in this relationship to tax, continuing.");
				}
				return 0;
			}
			// when checking by average we reset the price to a rounded amount.  Now
			// we just sum the details so this isn't necessary anymore.
			long quantityAlreadyTaxed = 0;
			Long quantityTaxed = (Long) pTaxedItems.get(item);
			if (quantityTaxed != null) {
				quantityAlreadyTaxed = quantityTaxed.longValue();
			}
		
			if (isLoggingDebug()) {
				logDebug("quantity of the item: " + item.getId() + " already taxed: " + quantityAlreadyTaxed);
				logDebug("quantity in this relationship: " + relationshipQuantity);
				logDebug("total quantity of the item: " + item.getQuantity());
			}
			
				// the average method had to worry about leftovers.  Now we just sum the details.
				// if we're about to tax the last of an item, add the leftovers to its amount
				if (quantityAlreadyTaxed + relationshipQuantity == item.getQuantity()) {
					ItemPriceInfo info = item.getPriceInfo();
					double totalTax = getPricingTools().calculateTaxableAmount(item, pOrder, pLocale,
							pProfile, pExtraParameters);
					Double amountTaxableSoFar = (Double) pTaxedItemAmounts.get(item);
					double taxableSoFar = 0.0;
					if(amountTaxableSoFar != null)
						taxableSoFar = amountTaxableSoFar.doubleValue();
					double roundedTotal = getPricingTools().round(totalTax);
	
					double leftovers = roundedTotal - pTotalTaxAmount - taxableSoFar;
	
					if(isLoggingDebug())
						logDebug("Calculate " + leftovers + " for the taxable amount.");
					double withLeftovers = pTaxableItem.getAmount() + leftovers;
					pTaxableItem.setAmount(getPricingTools().round(withLeftovers));
	
					if (isLoggingDebug()) {
						logDebug("calculating leftovers from rounding unit prices down");
					}
				}
		return quantityAlreadyTaxed + relationshipQuantity;
		} // end if taxableItem is not null
		else {
			return 0;
		}
	}
	
	/**
	 * Creates the CyberSourceTaxRequest taxableItem for the given commerceItem, priceBean and quantity.
	 * @param commerceItem
	 * @param quantity
	 * @param unitPriceBean
	 * @return
	 * @throws PricingException
	 */
	protected TaxableItem createTaxableItemForDPI(CommerceItem commerceItem,
			long quantity,DetailedItemPriceInfo unitPriceBean)
					throws PricingException
	{
		long relationshipQuantity = unitPriceBean.getQuantity();
		
		if (isLoggingDebug()) {
			logDebug("total quantity (from this relationship) to tax: " + relationshipQuantity);
		}
		if (relationshipQuantity == 0) {
			if (isLoggingDebug()) {
				logDebug("no quantity of this relationship to tax, continuing.");
			}
			return null;
		}
		try {
			double amount = (Double) DynamicBeans.getPropertyValue(unitPriceBean, PricingTools.DETAILED_ITEM_PRICE_AMOUNT_PROPERTY);
			double orderDiscountShare = (Double) DynamicBeans.getPropertyValue(unitPriceBean,
					PricingTools.DETAILED_ITEM_PRICE_ORDER_DISCOUNT_SHARE_PROPERTY);
			double orderManualAdjustmentShare = (Double) DynamicBeans.getPropertyValue(unitPriceBean,
					PricingTools.DETAILED_ITEM_PRICE_ORDER_MANUAL_ADJUSTMENT_SHARE_PROPERTY);
			amount -= orderDiscountShare;
			amount += orderManualAdjustmentShare;
		
			if(isLoggingDebug())
				logDebug("Taxable amount of DPI: " + amount);

			TaxableItemImpl ti = new TaxableItemImpl();
			ti.setQuantity(quantity);
			double roundedAmount = getPricingTools().round(amount/relationshipQuantity);
			ti.setAmount(roundedAmount);
			vlogDebug("Rounded Amount of TaxableItem is {0}", ti.getAmount());
			String catRef=commerceItem.getCatalogRefId();
			ti.setCatalogRefId(catRef);
			ti.setProductId(commerceItem.getAuxiliaryData().getProductId());
			if (getTaxStatusProperty() != null) {
				Object catalogRef = commerceItem.getAuxiliaryData().getCatalogRef();
				if (catalogRef != null) {
					try {
						Object taxStatus = DynamicBeans.getPropertyValue(catalogRef, getTaxStatusProperty());
						if (taxStatus instanceof String)
							ti.setTaxStatus((String)taxStatus);
					}
					catch (PropertyNotFoundException exc) {
						if (isLoggingDebug())
							logDebug("Could not find taxStatus property for " + catalogRef, exc);
					}
				}
			}
			return ti; 
		}catch(PropertyNotFoundException pnf) {
			throw new PricingException(pnf);
		}
	   
	}
	
	private BigDecimal round(final double pValue){
		return BigDecimal.valueOf(this.getPricingTools().round(pValue, 2));
	}
	
	/**
	 * Determines a shipping address based on a ShippingGroup
	 * This is to override for the TBS perpose
	 */
	protected Address determineShippingAddress(ShippingGroup pGroup) {
		if (pGroup instanceof HardgoodShippingGroup) {
			return ((HardgoodShippingGroup) pGroup).getShippingAddress();
		} else {
			return null;
		}
	} // end determineShippingAddress
}