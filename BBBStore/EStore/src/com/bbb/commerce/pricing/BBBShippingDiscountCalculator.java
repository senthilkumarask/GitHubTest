package com.bbb.commerce.pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingDiscountCalculator;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.utils.BBBUtility;


public class BBBShippingDiscountCalculator extends ShippingDiscountCalculator {

	private BBBCatalogTools mCatalogTools;
	private MutableRepository catalogRepository;
	private String couponRuleQuery;
	private MutableRepository couponRepository;

	/**
	 * @return catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return this.catalogRepository;
	}

	/**
	 * @param catalogRepository
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	
	@SuppressWarnings("unchecked")
	List<CommerceItem> getExcludedPromoItemsForSG(ShippingGroup pShippingGrp, RepositoryItem pPricingModel, Order order){
		
		List<CommerceItem> excludedItemsInSG = new ArrayList<CommerceItem>();		
		
		try{
			if( !("closenessQualifier".equalsIgnoreCase(pPricingModel.getItemDescriptor().getItemDescriptorName()))){
				String couponCode = String.valueOf(pPricingModel.getPropertyValue("bbbCoupons"));
				
				if(StringUtils.isBlank(couponCode)){
					if(isLoggingDebug()){
						logDebug("Leaving ShippingDiscountCalculator.getExcludedPromoItemsForSG() because coupon code is not valid hence items are not excluded from Free Shipping promotion");
					}
					return excludedItemsInSG;
				}
					this.logDebug("Coupon id " + couponCode + " is associated with Free Standard Shipping promotion hence checking for Exclusion & Inclusion criteria");
					if(pShippingGrp instanceof HardgoodShippingGroup){
						List<CommerceItemRelationship> sgCommItemList =  pShippingGrp.getCommerceItemRelationships();
						boolean doCheckForInclusion = true;
						for (CommerceItemRelationship commerceItemRel : sgCommItemList) {
							if ( (getCatalogTools().isGiftCardItem(order.getSiteId(),
									commerceItemRel.getCommerceItem().getCatalogRefId())) || (commerceItemRel.getCommerceItem() instanceof NonMerchandiseCommerceItem) ){
								continue;
							}
							if (getCatalogTools()
									.isSkuExcluded(commerceItemRel.getCommerceItem().getCatalogRefId(),
									couponCode, false)) {
								this.logDebug("Exclusion exists for Free Shipping promotion for coupon id " + couponCode);
								doCheckForInclusion = false;
								excludedItemsInSG.add(commerceItemRel.getCommerceItem());
							}
						}
						// If exclusion exist for F.S., then don't check for inclusion criteria as there can be only either Exclusion or Inclusion.
						if(doCheckForInclusion){
							this.logDebug("No exclusion found hence checking for Free Shipping promotion inclusion for coupon id " + couponCode);
							this.checkInclusionItems(sgCommItemList,
									couponCode, excludedItemsInSG, order);
						}
						
					}
			}
		}catch (RepositoryException re) {
		    if(isLoggingError()) {
		        logError("ShippingDiscountCalculator :  error occourred while comparing the ItemDescriptor with closenessQualifier : "+re);
		    }
		}catch (BBBSystemException bse) {
			
		    if(isLoggingError()) {
		        logError(
						"BBBSystem Exception occured while identifying a promotional item "
								, bse);
		    }
			
		} catch (BBBBusinessException bbe) {
			
		    if(isLoggingError()) {
		        logError(
						"BBBBusiness Exception occured while identifying a promotional item "
								, bbe);
		    }
			

		}
		
		return excludedItemsInSG;
	}

	/**
	 * overridden method to check for storeSG and excludedItems
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void priceShippingGroup(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShipment, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {
	    BBBPerformanceMonitor.start("BBBShippingDiscountCalculator", "priceShippingGroup");
		
		/*
		 * If current promotion is associated with any coupons then take out
		 * that coupon for adding it to pricing adjustment object.
		 */
		Map<RepositoryItem, String> shipmap = null;
		RepositoryItem coupon = null;
		if (pExtraParameters.get(BBBCoreConstants.PROMO_MAP) != null && pPricingModel != null
				&& pPricingModel.getRepositoryId() != null) {
			shipmap = (Map<RepositoryItem, String>) pExtraParameters.get(BBBCoreConstants.PROMO_MAP);
			for (RepositoryItem couponItem : shipmap.keySet()) {
				String promotionId = shipmap.get(couponItem);
				if (promotionId.equalsIgnoreCase(pPricingModel.getRepositoryId())) {
					coupon = couponItem;
					break;
				}
			}
		}

		/*
		 * Avoid applying promotion on store SG and for SG already got discount
		 * or price of SG is zero
		 */
		
	    String fromTBSPricingService = (String) pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER);
		
	    logDebug("Check for Request from TBS Pricing Service , return from calculator for TBS pricing Service Call for Flag" + fromTBSPricingService);
	    if (!(fromTBSPricingService != null && fromTBSPricingService.equalsIgnoreCase("true"))) {
		    // Avoid applying promotion on store SG and for SG already got discount
			// or price of SG is zero
			if (pShipment instanceof BBBStoreShippingGroup
					|| pPriceQuote.isDiscounted() ||
					pPriceQuote.getAmount() == 0) {
				/*
				 * If already a coupon or any global promotion is applied then
				 * remove all other coupons from PROMO MAP
				 */
				if (null != shipmap && null != coupon) {
					shipmap.remove(coupon);
				}
				return;
			}

		BBBShippingPriceInfo shippingPriceInfo = (BBBShippingPriceInfo) pPriceQuote;
		if(isLoggingDebug())
		{
			logDebug("\n Before Shipping Discount Calculation:: shippingPriceInfo.getFinalSurcharge() = "+shippingPriceInfo.getFinalSurcharge()+"\n shippingPriceInfo.getFinalShipping() = "+shippingPriceInfo.getFinalShipping()+ "\n pPriceQuote.getAmount() = "+pPriceQuote.getAmount());
		}

		double oldAmount = shippingPriceInfo.getAmount();

		pPriceQuote.setAmount(shippingPriceInfo.getFinalShipping());
		extractSuperCall(pOrder, pShipment, pPricingModel, pLocale, pProfile, pExtraParameters, shippingPriceInfo);
		shippingPriceInfo.setFinalShipping(pPriceQuote.getAmount());
		pPriceQuote.setAmount(pPriceQuote.getAmount() + shippingPriceInfo.getFinalSurcharge());
		Set<CommerceItem> excludedCommItems = null;
		/*
		 * Following portion of code is used for adding applied coupon to
		 * shippingPriceInfo's adjustment object.
		 */
		if (coupon != null && pPriceQuote.isDiscounted() && null != shippingPriceInfo.getAdjustments()
				&& shippingPriceInfo.getAdjustments().size() > 0) {
			final PricingAdjustment adjustment = (PricingAdjustment) shippingPriceInfo.getAdjustments().get(
					shippingPriceInfo.getAdjustments().size() - 1);
			/*
			 * we will have exclusion rules only when there is any coupon
			 * associated to a promotion as the rules is defined in
			 * CouponRuleRepository.
			 */
			excludedCommItems = ((BBBOrderImpl) pOrder).getExcludedPromotionMap().get(coupon.getRepositoryId());
			vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Excluded items are {0} for coupon {1}",
					excludedCommItems, coupon);
			if (null != adjustment.getPricingModel()
					&& adjustment.getPricingModel().getRepositoryId().equalsIgnoreCase(pPricingModel.getRepositoryId())) {
				adjustment.setCoupon(coupon);
				vlogDebug(
						"BBBShippingDiscountCalculator.priceShippingGroup: Coupon {0} is added to shipping adjustment of order id {1}, then it will remove from shipmap {2}",
						coupon, pOrder.getId(), shipmap);
				shipmap.remove(coupon);
				vlogDebug("BBBShippingDiscountCalculator.priceShippingGroup: Coupon {0} is removed from shipmap {1}.",
						coupon, shipmap);
			}
		}
		if(isLoggingDebug())
		{
			logDebug("\n After parent class priceShippingGroup :: shippingPriceInfo.getFinalSurcharge() = "+shippingPriceInfo.getFinalSurcharge()+"\n shippingPriceInfo.getFinalShipping() = "+shippingPriceInfo.getFinalShipping()+ "\n shippingPriceInfo.getAmount() = "+shippingPriceInfo.getAmount());
		}

		// set final shipping n final surcharge
		List<CommerceItem> excludedItemsInSG = new ArrayList<CommerceItem>();
		if(excludedCommItems != null && !excludedCommItems.isEmpty()){
			excludedItemsInSG.addAll(excludedCommItems);
		}
			if(null != pExtraParameters && excludedItemsInSG != null){
				pExtraParameters.put(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS, excludedItemsInSG);
				StringBuilder excludedCatalogRefIds = new StringBuilder();
				for (CommerceItem commerceItem : excludedItemsInSG) {
					excludedCatalogRefIds.append(commerceItem.getCatalogRefId()).append(",");
				}
				if(!StringUtils.isBlank(excludedCatalogRefIds.toString())){
					pShipment.getSpecialInstructions().put(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS, excludedCatalogRefIds.toString());
				}
				if(isLoggingDebug()){
					this.logDebug("Commerce items which are excluded from the Free shipping promotion due to Inclusion or Exclusion criteria are [ " + excludedCatalogRefIds.toString() + " ]");
				}
			}
			
		if (excludedItemsInSG != null && !excludedItemsInSG.isEmpty() ) {
	
			List<CommerceItemRelationship> exludedItemRelinSG = new ArrayList<CommerceItemRelationship>();

			double skuSurcharge = 0;
			double itemTotal = 0;
			double surchargeTotal = 0;
			double shippingTotal = 0;

			// matching excluded items in SG
			List<CommerceItemRelationship> ciRelationships = pShipment
					.getCommerceItemRelationships();
			for (CommerceItem excludedItem : excludedItemsInSG) {
				for (CommerceItemRelationship commerceItemRelationship : ciRelationships) {
					if (commerceItemRelationship.getCommerceItem().getId()
							.equalsIgnoreCase(excludedItem.getId())) {
						exludedItemRelinSG.add(commerceItemRelationship);
					}
				}
			}
			try {
					for (CommerceItemRelationship tempItemRel : exludedItemRelinSG) {
						if (!getCatalogTools()
								.isFreeShipping(
										pOrder.getSiteId(),
										tempItemRel.getCommerceItem()
												.getCatalogRefId(),
										pShipment.getShippingMethod())) {
							skuSurcharge = getCatalogTools().getSKUSurcharge(
									pOrder.getSiteId(),
									tempItemRel.getCommerceItem()
											.getCatalogRefId(),
									pShipment.getShippingMethod());
							surchargeTotal += (skuSurcharge * tempItemRel
									.getQuantity());

							itemTotal += ((BBBPricingTools) getPricingTools())
									.getShipItemRelPriceTotal(
											(ShippingGroupCommerceItemRelationship) tempItemRel,
											"amount");
						}
					}
					if (pShipment instanceof HardgoodShippingGroup
							&& ((HardgoodShippingGroup) pShipment)
									.getShippingAddress() != null && itemTotal > 0.00) {
						shippingTotal = getCatalogTools().getShippingFee(
								pOrder.getSiteId(),
								pShipment.getShippingMethod(),
								((HardgoodShippingGroup) pShipment)
										.getShippingAddress().getState(),
								itemTotal, null);
					}

			} catch (BBBSystemException bse) {
				
			    if(isLoggingError()) {
			        logError(
							"BBBSystem Exception occured while invoking catalogAPI : "
									, bse);
			    }
				
			} catch (BBBBusinessException bbe) {
				
			    if(isLoggingError()) {
			        logError(
							"BBBBusiness Exception occured while invoking catalogAPI : ", bbe);
			    }
				

			}

			double newAmount = shippingTotal + surchargeTotal;
//			shippingPriceInfo.setFinalSurcharge(getPricingTools().round(surchargeTotal));

			if (newAmount > 0) {
				shippingPriceInfo.setFinalShipping(getPricingTools().round(shippingTotal));
				shippingPriceInfo.setAmount(getPricingTools().round(newAmount));
				double adjustAmount = newAmount - oldAmount;
				((PricingAdjustment) shippingPriceInfo.getAdjustments().get(0))
						.setTotalAdjustment(getPricingTools().round(adjustAmount));
			}
		} 
		//else {
		//	shippingPriceInfo.setFinalShipping(shippingPriceInfo.getAmount());
			//shippingPriceInfo.setFinalSurcharge(0);
		//}
		if(isLoggingInfo())
		{
			logInfo(" After ShippingDiscountCalculation:: excludedItemsInSG= "+ excludedItemsInSG+", shippingPriceInfo.getFinalSurcharge() = "+shippingPriceInfo.getFinalSurcharge()+", shippingPriceInfo.getFinalShipping() = "+shippingPriceInfo.getFinalShipping()+ ", pPriceQuote.getAmount() = "+pPriceQuote.getAmount());
		}
	    }
		BBBPerformanceMonitor.end("BBBShippingDiscountCalculator", "priceShippingGroup");

	}

	protected void extractSuperCall(Order pOrder, ShippingGroup pShipment, RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters, BBBShippingPriceInfo shippingPriceInfo)
					throws PricingException {
		super.priceShippingGroup(pOrder, shippingPriceInfo, pShipment,
				pPricingModel, pLocale, pProfile, pExtraParameters);
	}
	
	/**
	 * This method checks whether the current coupon has any inclusion rule associated with or not.
	 * If not null, then the items are checked for this inclusion rule & all those items which don't
	 * satisfy this rule are excluded from this promotion. JIRA id BBBSL-22653. This inclusion functionality
	 * is for Free standard shipping promotion discount.
	 * 
	 * @param sgCommItemList
	 * @param promotionCode
	 * @param excludedPromoItems
	 * @param order 
	 * @throws BBBSystemException
	 * @throws BBBBusinessException 
	 */
	public void checkInclusionItems(List<CommerceItemRelationship> sgCommItemList, final String promotionCode, List<CommerceItem> excludedPromoItems,Order order)
			throws BBBSystemException, BBBBusinessException {

		this.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : Start");

		RepositoryItem[] couponRuleItem;
		String skuJdaDeptId = null;
		String skuJdaSubDeptId = null;
		RepositoryItem jdaDeptItem = null;
		RepositoryItem jdaSubDeptItem = null;
		String siteId = null;
		
		if(null != ServletUtil.getCurrentRequest()) {
            siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
		}

		if (BBBUtility.isEmpty(siteId)){
            siteId = extractSiteId();
		}
		final Object[] params = new Object[3];
		// Fetch inclusion Rules parameters for the Coupon Code
		params[0] = promotionCode;
		params[1] = BBBCoreConstants.INCLUSION_RULE;
		params[2] = siteId;
		
		this.logDebug("Passing parameters for sku inclusion in shipping discount calculator: promotionCode=" + promotionCode +", siteId=" + siteId);
		couponRuleItem = getCatalogTools().executeRQLQuery(this.getCouponRuleQuery(), params,
				BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, this.getCouponRepository());
		if ((couponRuleItem != null) && (couponRuleItem.length > 0)) {
			this.logDebug("Found [" + couponRuleItem.length + "] Inclusion Rule(s) for this Coupon : [" + promotionCode + "]");

			for (CommerceItemRelationship commerceItemRel : sgCommItemList) {
				if((getCatalogTools().isGiftCardItem(order.getSiteId(),
						commerceItemRel.getCommerceItem().getCatalogRefId())) || (commerceItemRel.getCommerceItem() instanceof NonMerchandiseCommerceItem)){
							continue;
						}
				try {
					String skuId = commerceItemRel.getCommerceItem().getCatalogRefId();
					if(!BBBUtility.isEmpty(skuId)){
						final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
								BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);

						if (skuRepositoryItem == null) {
							this.logDebug("SKU item is not in the Repository for sku id " + skuId);
							continue;
						}

						this.logDebug("Checking the coupon inclusion rule for coupon id : "+ promotionCode + " for the sku id" + skuId);
						final String skuVendorId = (String) skuRepositoryItem
								.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME);
						jdaDeptItem = (RepositoryItem) skuRepositoryItem
								.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
						jdaSubDeptItem = (RepositoryItem) skuRepositoryItem
								.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME);
						final String skuJdaClass = (String) skuRepositoryItem
								.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME);

						if (jdaDeptItem != null) {
							skuJdaDeptId = jdaDeptItem.getRepositoryId();
						}
						if (jdaSubDeptItem != null) {
							skuJdaSubDeptId = jdaSubDeptItem.getRepositoryId();
						}

						this.logDebug("SKU Properties to be checked for inclusion : VendorId=" + skuVendorId +", jdaDeptId=" + skuJdaDeptId 
								+ ", jdaSubDeptId=" + skuJdaSubDeptId + ", jdaClass=" + skuJdaClass);
						
						boolean isInclusionRuleSatisfied = false;
						
						for (int ruleIndex = 0; ruleIndex < couponRuleItem.length; ruleIndex++) {
							final String ruleSkuId = (String) couponRuleItem[ruleIndex]
									.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID);
							final String ruleVendorId = (String) couponRuleItem[ruleIndex]
									.getPropertyValue(BBBCatalogConstants.COUPON_RULES_VENDOR_ID);
							final String ruleDeptId = (String) couponRuleItem[ruleIndex]
									.getPropertyValue(BBBCatalogConstants.COUPON_RULES_DEPT_ID);
							final String ruleSubDeptId = (String) couponRuleItem[ruleIndex]
									.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SUB_DEPT_ID);
							final String ruleClass = (String) couponRuleItem[ruleIndex]
									.getPropertyValue(BBBCatalogConstants.COUPON_RULES_CLASS);

							this.logDebug("Inclusive Coupon Rule properties for coupon " + couponRuleItem[ruleIndex].getRepositoryId() + " are : SKU=" + ruleSkuId 
									+ ", VendorId=" + ruleVendorId
									+ ", jdaDeptId=" + ruleDeptId 
									+ ", jdaSubDeptId=" + ruleSubDeptId
									+ ", jdaClass=" + ruleClass);
							
							boolean isDeptSubDeptClassInclusive = false;

							// Check for SKU inclusion. If sku is inclusive, then leave the filtered commerce items as it is and iterate over next item.
							if (!BBBUtility.isEmpty(ruleSkuId) && !(BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(ruleSkuId))
									&& ruleSkuId.equalsIgnoreCase(skuId)) {
								isInclusionRuleSatisfied = true;
								break;
							}
							
							// Check for Dept. & Sub Dept. & Class inclusion eligibility.
							isDeptSubDeptClassInclusive = getCatalogTools().checkForDeptSubDeptClassInclusion(ruleDeptId, skuJdaDeptId, ruleSubDeptId, skuJdaSubDeptId, ruleClass, skuJdaClass);

							// Check for Vendor inclusion eligibility
							isInclusionRuleSatisfied = getCatalogTools().checkForVendorInclusion(ruleVendorId, skuVendorId, ruleDeptId, isDeptSubDeptClassInclusive);
							
							// If inclusion rule is satisfied for any one of the criteria above, break the loop as no more rule needs to be checked for the current item
							if(isInclusionRuleSatisfied)
								break;
						}
						
						// if inclusion rule applies to any of the above rule, then retain the filtered items else remove them from the list
						// so that only inclusive items are eligible for promotion discount
						if(!isInclusionRuleSatisfied) {
							this.logInfo("Inclusion Rule NOT Applicable hence adding the commerce item to the exclusion list for sku " + skuId);
							excludedPromoItems.add(commerceItemRel.getCommerceItem());
						} 
					}
				} catch (final RepositoryException rpex) {
					this.logError("BBBShippingDiscountCalculator Method Name checkInclusionItems() : RepositoryException ");
					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, rpex);
				}
			}
		}
		this.logDebug("Coupon rule item or sku is null or No Rule(s) found for the Coupon : " + promotionCode);
		this.logDebug("BBBShippingDiscountCalculator class method Name checkInclusionItems() : End");
	}

	protected String extractSiteId() {
		String siteId;
		siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}
	
	/**
	 * @return couponRuleQuery
	 */
	public String getCouponRuleQuery() {
		return this.couponRuleQuery;
	}

	/**
	 * @param couponRuleQuery
	 */
	public void setCouponRuleQuery(String couponRuleQuery) {
		this.couponRuleQuery = couponRuleQuery;
	}

	/**
	 * @return couponRepository
	 */
	public MutableRepository getCouponRepository() {
		return this.couponRepository;
	}

	/**
	 * @param couponRepository
	 */
	public void setCouponRepository(MutableRepository couponRepository) {
		this.couponRepository = couponRepository;
	}
}
