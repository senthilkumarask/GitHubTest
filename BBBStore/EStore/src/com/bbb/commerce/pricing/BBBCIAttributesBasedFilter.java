package com.bbb.commerce.pricing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PromotionQualifyingFilter;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * To filter item based on promotion code
 * 
 * @author msiddi
 * @story UC_Checkout_Item_Promo
 * @version 1.0
 */

public class BBBCIAttributesBasedFilter extends BBBGenericService implements PromotionQualifyingFilter {

	private BBBCatalogTools mCatalogTools;
	private MutableRepository catalogRepository;
	private String couponRuleSkuQuery;
	private String couponRuleVendorDeptClassQuery;
	private String couponRuleVendorDeptQuery;
	private String couponRuleQuery;
	
	private MutableRepository couponRepository;

	/**
	 * @return the couponRuleQuery
	 */
	public String getCouponRuleQuery() {
		return couponRuleQuery;
	}

	/**
	 * @param couponRuleQuery the couponRuleQuery to set
	 */
	public void setCouponRuleQuery(String couponRuleQuery) {
		this.couponRuleQuery = couponRuleQuery;
	}
	
	/**
	 * @return the couponRuleSkuQuery
	 */
	public String getCouponRuleSkuQuery() {
		return couponRuleSkuQuery;
	}

	/**
	 * @param couponRuleSkuQuery the couponRuleSkuQuery to set
	 */
	public void setCouponRuleSkuQuery(String couponRuleSkuQuery) {
		this.couponRuleSkuQuery = couponRuleSkuQuery;
	}

	
	/**
	 * @return the couponRuleVendorDeptClassQuery
	 */
	public String getCouponRuleVendorDeptClassQuery() {
		return couponRuleVendorDeptClassQuery;
	}

	/**
	 * @param couponRuleVendorDeptClassQuery the couponRuleVendorDeptClassQuery to set
	 */
	public void setCouponRuleVendorDeptClassQuery(
			String couponRuleVendorDeptClassQuery) {
		this.couponRuleVendorDeptClassQuery = couponRuleVendorDeptClassQuery;
	}

	/**
	 * @return the couponRuleVendorDeptQuery
	 */
	public String getCouponRuleVendorDeptQuery() {
		return couponRuleVendorDeptQuery;
	}

	/**
	 * @param couponRuleVendorDeptQuery the couponRuleVendorDeptQuery to set
	 */
	public void setCouponRuleVendorDeptQuery(String couponRuleVendorDeptQuery) {
		this.couponRuleVendorDeptQuery = couponRuleVendorDeptQuery;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void filterItems(int filterContext, PricingContext pricingContext, Map extraParametersMap,
			Map detailsPendingActingAsQualifier, Map detailsRangesToReceiveDiscount, List filteredItems)
			throws PricingException {
		logDebug("Start : BBBCIAttributesBasedFilter.filterItems");

		try {
			if (null == pricingContext || null == pricingContext.getPricingModel()) {
				logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because pricingContext is not valid : pricingContext - "
						+ pricingContext);
				return;
			}
			if (BBBCheckoutConstants.CLOSENESS_QUALIFIER.equalsIgnoreCase(pricingContext.getPricingModel()
					.getItemDescriptor().getItemDescriptorName())) {
				return;
			}
			boolean isUserCoupon = false;
			RepositoryItem couponRepositoryItem = null;
			String couponCode = null;
			Map<RepositoryItem, String> promoMap = null;
			if (extraParametersMap.get(BBBCoreConstants.PROMO_MAP) != null) {
				// my offers automation - Get Promo map from extraparameter map
				promoMap = (Map<RepositoryItem, String>) extraParametersMap.get(BBBCoreConstants.PROMO_MAP);
				vlogDebug("BBBCIAttributesBasedFilter.filterItems: Promo Map cantains {0}", promoMap);
				// Get the Coupon code for the current promotion from
				for (RepositoryItem coupon : promoMap.keySet()) {
					String promotionId = promoMap.get(coupon);
					if (promotionId.equalsIgnoreCase(pricingContext.getPricingModel().getRepositoryId())
							&& (!pricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName()
									.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT) || extraParametersMap
									.get(BBBCoreConstants.COUPON_CODE) == null)) {
						couponCode = coupon.getRepositoryId();
						couponRepositoryItem = coupon;
						if (pricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName().equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT)) {
							promoMap.remove(coupon);
						}
						isUserCoupon = true;
						break;
					}
				}
			} else {
				/*
				 * No active promotion found , so no need to proceed further to
				 * execute coupon rules
				 */
				vlogDebug("Leaving BBBCIAttributesBasedFilter.filterItems: because promotion map is null or gobal promotion has no rules to execute");
				return;
			}

			/*
			 * Added for My offers Automation | Item Promotion | For using same
			 * coupon code for the consecutive qualifier called for a promotion
			 */
			if (BBBUtility.isEmpty(couponCode)
					&& extraParametersMap.get(BBBCoreConstants.COUPON_CODE) != null
					&& pricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName()
							.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT)) {
				couponCode = (String) extraParametersMap.get(BBBCoreConstants.COUPON_CODE);
				logDebug("** Duplicate call for Qualifier for Coupon  : " + couponCode);
				extraParametersMap.remove(BBBCoreConstants.COUPON_CODE);
			}
			boolean isAppliedCoupon = false;
			Map<String, String> couponList = (Map<String, String>) extraParametersMap.get("couponListParam");
			if (null != couponList && couponList.containsValue(couponCode)) {
				isAppliedCoupon = true;
			}
			Set<CommerceItem> excludedPromoItems = null;
			if (BBBUtility.isEmpty(couponCode)) {
				logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because coupon code is not valid");
				return;

			} else if (BBBUtility.isListEmpty(filteredItems)) {
				if (getCatalogTools().isLogCouponDetails() && isAppliedCoupon) {
					logInfo("The coupon : "
							+ couponCode
							+ " cannot be applied to any commerce item. The items have already been filetered out before applying exclusion/inclusion rules.");
				}
				logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
				return;

			} else {
				logDebug("** filteredItems : " + filteredItems);
				logDebug("** couponCode : " + couponCode);
				excludedPromoItems = new HashSet<CommerceItem>();
				boolean doCheckForInclusion = true;
				for (Iterator<FilteredCommerceItem> itemIterator = filteredItems.iterator(); itemIterator.hasNext();) {
					FilteredCommerceItem filteredCommerceItem = itemIterator.next();

					// remove the item if it is excluded from promotion
					if (getCatalogTools().isSkuExcluded(filteredCommerceItem.getWrappedItem().getCatalogRefId(),
							couponCode, isAppliedCoupon)) {
						doCheckForInclusion = false;
						excludedPromoItems.add(filteredCommerceItem.getWrappedItem());
						itemIterator.remove();
					}
				}
				if (filteredItems.isEmpty() && getCatalogTools().isLogCouponDetails()) {
					logInfo("The coupon : "
							+ couponCode
							+ " cannot be applied to any commerce Item. The items has been filtered out due to exclusion rule");
				}
				/*
				 * BBBSL-2193. Inclusion functionality for promotions. If a
				 * coupon has inclusion functionality, then items are checked
				 * whether or not they satisfy any inclusion criteria.
				 */
				if (doCheckForInclusion)
					this.checkInclusionItems(filteredItems, couponCode, excludedPromoItems, isAppliedCoupon);
			}

			// create sku level map for coupons only for item promotions
			if ((pricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName()
					.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT)) && couponRepositoryItem != null) {
				createSkuLevelMapForCoupons(pricingContext, extraParametersMap, filteredItems, isUserCoupon,
						couponRepositoryItem);
			}

			RepositoryItem promotion = pricingContext.getPricingModel();
			Map<String, Set<CommerceItem>> promoExclMap = ((BBBOrderImpl) pricingContext.getOrder())
					.getExcludedPromotionMap();

			/*
			 * we will have exclusion rules only when there is any coupon
			 * associated to a promotion as the rules is defined in
			 * CouponRuleRepository.
			 */
			if (!excludedPromoItems.isEmpty() && !StringUtils.isEmpty(couponCode)) {
				promoExclMap.put(couponCode, excludedPromoItems);
			}
			if (pricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName()
					.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT)
					&& couponRepositoryItem != null) {
				extraParametersMap.put(BBBCoreConstants.COUPON_CODE, couponCode);
			}

			/*logDebug("** promotion : " + promotion.getRepositoryId());
			logDebug("** promoExclMap : " + promoExclMap);*/
			logDebug("End : BBBCIAttributesBasedFilter.filterItems");

		} catch (BBBSystemException bse) {
			logError("BBBSystem Exception occured while identifying a promotional item ", bse);

		} catch (BBBBusinessException bbe) {
			logError("BBBBusiness Exception occured while identifying a promotional item ", bbe);

		} catch (RepositoryException e) {
			logError("BBBCIAttributesBasedFilter :  error occourred while comparing the ItemDescriptor with closenessQualifier : "
					+ e);
		}
	}

	/**
	 * 
	 * @param pricingContext
	 * @param extraParametersMap
	 * @param filteredItems
	 * @param isUserCoupon
	 * @param couponRepositoryItem
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createSkuLevelMapForCoupons(PricingContext pricingContext, Map extraParametersMap, List filteredItems,
			boolean isUserCoupon, RepositoryItem couponRepositoryItem) {
		// creating map for my offers automation
		logDebug("** createSkuLevelMapForCoupons : Start");
		LinkedHashMap<RepositoryItem, String> skuMapValue = null;
		Map<String, LinkedHashMap<RepositoryItem, String>> skuMap = null;

		// Iterate over the CI and create/update the sku level map
		for (Iterator<FilteredCommerceItem> itemIterator = filteredItems.iterator(); itemIterator.hasNext();) {
			FilteredCommerceItem filteredCommerceItem = itemIterator.next();
			if (isUserCoupon) {
				/*
				 * If skuMap already present in extraparameters map, then update
				 * that map only
				 */
				if (extraParametersMap.get(BBBCoreConstants.SKU_MAP) != null) {
					skuMap = (Map<String, LinkedHashMap<RepositoryItem, String>>) extraParametersMap
							.get(BBBCoreConstants.SKU_MAP);
					/*
					 * If skuMap contains entry for the commerce item(sku), then
					 * update that map itself
					 */
					if (skuMap.get(filteredCommerceItem.getCatalogRefId()) != null) {
						skuMapValue = skuMap.get(filteredCommerceItem.getCatalogRefId());
						skuMapValue.put(couponRepositoryItem, pricingContext.getPricingModel().getRepositoryId());
					} else {
						/*
						 * If skuMap does not contain the entry for the commerce
						 * item(sku), then create a new map and populate the map
						 * with coupon and promotion
						 */
						skuMapValue = new LinkedHashMap<RepositoryItem, String>();
						skuMapValue.put(couponRepositoryItem, pricingContext.getPricingModel().getRepositoryId());
						skuMap.put(filteredCommerceItem.getCatalogRefId(), skuMapValue);
					}
				}
				/*
				 * If skuMap not already present in extraparameters map, then
				 * create a new map and put the mapping of the sku in the new
				 * map
				 */
				else {
					skuMap = new LinkedHashMap<String, LinkedHashMap<RepositoryItem, String>>();
					skuMapValue = new LinkedHashMap<RepositoryItem, String>();
					skuMapValue.put(couponRepositoryItem, pricingContext.getPricingModel().getRepositoryId());
					skuMap.put(filteredCommerceItem.getCatalogRefId(), skuMapValue);
				}
				extraParametersMap.put(BBBCoreConstants.SKU_MAP, skuMap);
			}
			logDebug("** createSkuLevelMapForCoupons : end :: Map value" + skuMap);
		}
	}

	/**
	 * This method checks whether the current coupon has any inclusion rule
	 * associated with or not. If not null, then the items are checked for this
	 * inclusion rule & all those items which don't satisfy this rule are
	 * excluded from this promotion. JIRA id BBBSL-2193. This inclusion
	 * functionality is for both order and item level coupon discount.
	 * 
	 * @param filteredItems
	 * @param promotionCode
	 * @param couponRule
	 * @param excludedPromoItems
	 * @throws BBBSystemException
	 */
	private void checkInclusionItems(List<FilteredCommerceItem> filteredItems, final String promotionCode,
			Set<CommerceItem> excludedPromoItems, boolean isAppliedCoupon) throws BBBSystemException {

		this.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");

		RepositoryItem[] couponRuleItem;
		String skuJdaDeptId = null;
		String skuJdaSubDeptId = null;
		RepositoryItem jdaDeptItem = null;
		RepositoryItem jdaSubDeptItem = null;
		String siteId = null;

		if (null != ServletUtil.getCurrentRequest()) {
			siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
		}

		if (BBBUtility.isEmpty(siteId)) {
			siteId = SiteContextManager.getCurrentSiteId();
		}

		final Object[] params = new Object[9];
		// Fetch inclusion Rules parameters for the Coupon Code
		params[0] = promotionCode;
		params[1] = BBBCoreConstants.INCLUSION_RULE;
		params[2] = siteId;

		this.logDebug("Passing parameters for sku inclusion : promotionCode=" + promotionCode + ", siteId=" + siteId);
		couponRuleItem = getCatalogTools().executeRQLQuery(this.getCouponRuleQuery(), params,
				BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, this.getCouponRepository());
		if ((couponRuleItem != null) && (couponRuleItem.length > 0)) {
			this.logDebug("Found [" + couponRuleItem.length + "] Inclusion Rule(s) for this Coupon : [" + promotionCode
					+ "]");
			for (Iterator<FilteredCommerceItem> itemIterator = filteredItems.iterator(); itemIterator.hasNext();) {
				FilteredCommerceItem filteredCommerceItem = itemIterator.next();
				try {
					String skuId = filteredCommerceItem.getWrappedItem().getCatalogRefId();
					if (!BBBUtility.isEmpty(skuId)) {
						final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,
								BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);

						if (skuRepositoryItem == null) {
							this.logDebug("SKU item is not in the Repository for sku id " + skuId);
							continue;
						}

						this.logDebug("Checking the coupon inclusion rule for coupon id : " + promotionCode
								+ " for the sku id" + skuId);
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

						this.logDebug("SKU Properties to be checked for inclusion : VendorId=" + skuVendorId
								+ ", jdaDeptId=" + skuJdaDeptId + ", jdaSubDeptId=" + skuJdaSubDeptId + ", jdaClass="
								+ skuJdaClass);

						boolean isInclusionRuleSatisfied = false;
						
						if(getCatalogTools().isLogCouponDetails() && isAppliedCoupon){
							this.logInfo("SKU Properties to be checked for inclusion : VendorId=" + skuVendorId +", jdaDeptId=" + skuJdaDeptId 
									+ ", jdaSubDeptId=" + skuJdaSubDeptId + ", jdaClass=" + skuJdaClass);
							isAppliedCoupon= false;
						}
                        
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

							this.logDebug("Inclusive Coupon Rule properties for coupon "
									+ couponRuleItem[ruleIndex].getRepositoryId() + " are : SKU=" + ruleSkuId
									+ ", VendorId=" + ruleVendorId + ", jdaDeptId=" + ruleDeptId + ", jdaSubDeptId="
									+ ruleSubDeptId + ", jdaClass=" + ruleClass);

							boolean isDeptSubDeptClassInclusive = false;

							/*
							 * Check for SKU inclusion. If sku is inclusive,
							 * then leave the filtered commerce items as it is
							 * and iterate over next item.
							 */
							if (!BBBUtility.isEmpty(ruleSkuId)
									&& !(BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(ruleSkuId))
									&& ruleSkuId.equalsIgnoreCase(skuId)) {
								isInclusionRuleSatisfied = true;
								break;
							}

							/*
							 * Check for Dept. & Sub Dept. & Class inclusion
							 * eligibility.
							 */
							isDeptSubDeptClassInclusive = getCatalogTools().checkForDeptSubDeptClassInclusion(
									ruleDeptId, skuJdaDeptId, ruleSubDeptId, skuJdaSubDeptId, ruleClass, skuJdaClass);

							// Check for Vendor inclusion eligibility
							isInclusionRuleSatisfied = getCatalogTools().checkForVendorInclusion(ruleVendorId,
									skuVendorId, ruleDeptId, isDeptSubDeptClassInclusive);

							/*
							 * If inclusion rule is satisfied for any one of the
							 * criteria above, break the loop as no more rule
							 * needs to be checked for the current item
							 */
							if (isInclusionRuleSatisfied)
								break;
						}

						/*
						 * if inclusion rule applies to any of the above rule,
						 * then retain the filtered items else remove them from
						 * the list so that only inclusive items are eligible
						 * for promotion discount
						 */
						if (!isInclusionRuleSatisfied) {
							this.logDebug("Inclusion Rule NOT Applicable hence removing the filterItem from the list for  sku "
									+ skuId);
							excludedPromoItems.add(filteredCommerceItem.getWrappedItem());
							itemIterator.remove();
						}
					}
				} catch (final RepositoryException rpex) {
					this.logError("BBBCIAttributesBasedFilter Method Name checkInclusionItems() : RepositoryException ");
					throw new BBBSystemException(
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, rpex);
				}
			}
			if (filteredItems.isEmpty() && getCatalogTools().isLogCouponDetails()) {
				logInfo("The coupon : "
						+ promotionCode
						+ " cannot be applied to any commerce Item as items has been filtered out due to inclusion rule");
			}
		}
		this.logDebug("Coupon rule item or sku is null or No Rule(s) found for the Coupon : " + promotionCode);
		this.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : End");
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
}
