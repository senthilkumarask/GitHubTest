package com.bbb.commerce.pricing;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.vo.BBBInternationalCurrencyDetailsVO;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.output.BBBCustomTagComponent;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.ShippingPricingEngine;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import atg.commerce.promotion.PromotionException;
import atg.commerce.states.OrderStates;
import atg.commerce.states.StateDefinitions;
import atg.core.util.Range;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;


/**
 * @author sdandr
 *
 */
public class BBBPricingTools extends PricingTools {

    /**
	 * Constant for BBBCatalogTools
	 */
	private BBBCatalogToolsImpl mCatalogUtil;

	private BBBPromotionTools mPromotionTools;

	private ShippingPricingEngine shippingPricingEngine;

	private String tempShippingMethod;
	
	private final String SHIPPING_SURCHARGE_CAP = "shippingSurchargeCap";
	private BBBCustomTagComponent bbbcustomcomponent;
	private HashMap mTbsToStoreSiteIds;
	
	public HashMap getTbsToStoreSiteIds() {
		return mTbsToStoreSiteIds;
	}

	public void setTbsToStoreSiteIds(HashMap pTbsToStoreSiteIds) {
		this.mTbsToStoreSiteIds = pTbsToStoreSiteIds;
	}

	public BBBCustomTagComponent getBbbcustomcomponent() {
		return bbbcustomcomponent;
	}

	public void setBbbcustomcomponent(BBBCustomTagComponent bbbcustomcomponent) {
		this.bbbcustomcomponent = bbbcustomcomponent;
	}

	/**
	 * @return the tempShippingMethod
	 */
	public String getTempShippingMethod() {
		return this.tempShippingMethod;
	}

	/**
	 * @param tempShippingMethod the tempShippingMethod to set
	 */
	public void setTempShippingMethod(final String tempShippingMethod) {
		this.tempShippingMethod = tempShippingMethod;
	}

	/**
	 * @return the shippingPricingEngine
	 */
	@Override
    public ShippingPricingEngine getShippingPricingEngine() {
		return this.shippingPricingEngine;
	}

	/**
	 * @param shippingPricingEngine the shippingPricingEngine to set
	 */
	@Override
    public void setShippingPricingEngine(final ShippingPricingEngine shippingPricingEngine) {
		this.shippingPricingEngine = shippingPricingEngine;
	}

	public BBBPromotionTools getPromotionTools() {
		return this.mPromotionTools;
	}

	public void setPromotionTools(final BBBPromotionTools pPromotionTools) {
		this.mPromotionTools = pPromotionTools;
	}


	/**
	 * @param siteId
	 *            , shippingMethod, hardgoodShippingGroup
	 * @param sddShippingZip 
	 * @return double: Shipping Cost
	 *
	 * @description This method loops through each commerce item in the shipping
	 *              group and sums the shippingGroupSubtotal. But it skips items
	 *              which are of giftCard type and items which are free
	 *              shippable. After this it invokes the CatalogUtil to get the
	 *              shippingCost for the given shipping group.
	 *
	 */
	@SuppressWarnings("unchecked")
    public double calculateShippingCost(final String siteId, final String shippingMethod,
			final HardgoodShippingGroup hardgoodShippingGroup, String sddShippingZip)
			throws PricingException {
		
		String webSiteID = siteId;
		
		if (this.isLoggingDebug()) {
			this.logDebug("Inside calculateShippingCost" + webSiteID + shippingMethod
					+ hardgoodShippingGroup);
		}

		double shippingAmount = 0.0;
		double gcShippingAmount = 0.0;
		double shippingGroupSubtotal = 0.0;
		String state = null;

		int nonGiftCardCommerceItemCountForShipping = 0;
		
		if(hardgoodShippingGroup!=null){
			/*return shippingGroupSubtotal;*/
		
		
		final List<CommerceItemRelationship> commerceItemRelationshipList = hardgoodShippingGroup
				.getCommerceItemRelationships();

		// if no commerce item is present return cost as 0.0
		if ((null == commerceItemRelationshipList)
				|| commerceItemRelationshipList.isEmpty()) {
			return shippingGroupSubtotal;
		}

		
		CommerceItem commerceItem;
		for (final CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {

			commerceItem = commerceItemRelationship
					.getCommerceItem();

			try {
				state = hardgoodShippingGroup.getShippingAddress().getState();
				if (this.getCatalogUtil().isGiftCardItem(webSiteID,
						commerceItem.getCatalogRefId())) {
					if (this.isLoggingDebug()) {
						this.logDebug("GiftCart Item Found");
					}
				} else if (!(commerceItem instanceof NonMerchandiseCommerceItem)
						&& !this.getCatalogUtil().isFreeShipping(webSiteID,commerceItem.getCatalogRefId(), shippingMethod)) {
					nonGiftCardCommerceItemCountForShipping++;
					if (this.isLoggingDebug()) {
						this.logDebug("Item is not free shipping");
					}
					shippingGroupSubtotal = shippingGroupSubtotal
							+ this.getShipItemRelPriceTotal(
									(ShippingGroupCommerceItemRelationship) commerceItemRelationship,
									"amount");
				}

			} catch (final BBBSystemException e) {
				throw new PricingException(e);
			} catch (final BBBBusinessException e) {
				throw new PricingException(e);
			}

		}
		// Set state as null, as it is being initialised to INITIAL value, which
		// is wrong.
		if (((null == hardgoodShippingGroup.getShippingAddress()) || StringUtils
						.isBlank(hardgoodShippingGroup.getShippingAddress()
								.getAddress1()))) {
			state = null;
		}
		}

		if (nonGiftCardCommerceItemCountForShipping > 0) {

			if (this.isLoggingDebug()) {
				this.logDebug("Cart contain items other than GiftCard");
			}

			try {
				if (shippingGroupSubtotal > 0) {
					//Mexico shipping price calculation 
					shippingGroupSubtotal = mexicoOrderShippingCalculation(shippingGroupSubtotal);
					
					String shipMethod = hardgoodShippingGroup.getShippingMethod();
					String regionId = null;
					if(!StringUtils.isBlank(shippingMethod) && shippingMethod.equals("SDD")){
						
						String postalCode = null;
						if (StringUtils.isNotBlank(sddShippingZip)) {
							vlogDebug("BBBPricingTools.calculateShippingCost: Select SDD shipping method flow, selected zipcode is : {0}", sddShippingZip);
							postalCode = sddShippingZip;
						} else {
							postalCode = hardgoodShippingGroup.getShippingAddress().getPostalCode();
						}
						//Get the region from the postal code.
						
						if(!StringUtils.isBlank(postalCode)){
							RegionVO regionVO = getCatalogUtil().getRegionDataFromZip(postalCode);
							
							if(null!=regionVO){
								regionId = regionVO.getRegionId();		
							}
							
						}
						
						
						
					}
					
					shippingAmount = shippingAmount
							+ this.getCatalogUtil().getShippingFee(webSiteID,
									shippingMethod, state,
									shippingGroupSubtotal, regionId);
				}
			} catch (final BBBSystemException e) {
				if (this.isLoggingError()) {
					this.logError("Error getting shipping fee", e);
				}
			} catch (final BBBBusinessException e) {
				if (this.isLoggingError()) {
				    this.logError("Error getting shipping fee", e);
				}
			}
		}else{
			if (this.isLoggingDebug()) {
				this.logDebug("There are only GiftCart Items in SG");
			}
			try{
				gcShippingAmount = this.getCatalogUtil().shippingCostForGiftCard(webSiteID,
							shippingMethod);
			}catch (final BBBBusinessException e) {
				if(BBBCatalogErrorCodes.NO_GIFT_CARD_FOR_SHIPPING_ID_IN_REPOSITORY.equalsIgnoreCase(e.getMessage()) ){
					gcShippingAmount = 0;
				}
				if (this.isLoggingError()) {
				    this.logError("Error getting shipping fee", e);
				}
			}catch (final BBBSystemException e) {
				if (this.isLoggingError()) {
				    this.logError("Error getting shipping fee", e);
				}
			}
			shippingAmount = gcShippingAmount;
		}
		return shippingAmount;
	}
	/**
	 * @param shippingGroupSubtotal
	 * @return double: shippingGroupSubtotal
	 *
	 * @description This method is to calculate shipping charge for mexico orders. It converts
	 *              total amount in peso to usd
	 *              and then we calculate the shipping charge
	 */
	public double mexicoOrderShippingCalculation(double shippingGroupSubtotal) {
		if (this.isLoggingDebug()) {
			this.logDebug("Entering method mexicoOrderShippingCalculation");
		}
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		String country = null;
		String currency = null;
		if (pRequest != null) {
			BBBSessionBean sessionBeanFromReq = (BBBSessionBean) pRequest
					.getAttribute(BBBInternationalShippingConstants.SESSIONBEAN);
			if (sessionBeanFromReq == null) {
				sessionBeanFromReq = (BBBSessionBean) pRequest
						.resolveName(BBBCoreConstants.SESSION_BEAN);
				pRequest.setAttribute(
						BBBInternationalShippingConstants.SESSIONBEAN,
						sessionBeanFromReq);
			}
			country = (String) sessionBeanFromReq
					.getValues()
					.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
			currency = (String) sessionBeanFromReq
					.getValues()
					.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY);
			if (BBBUtility.isNotEmpty(country)
					&& BBBUtility.isNotEmpty(currency)
					&& country
							.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)
					&& currency
							.equalsIgnoreCase(BBBInternationalShippingConstants.CURRENCY_MEXICO)) {
				BBBInternationalCurrencyDetailsVO currenyDetailVO = this
						.getBbbcustomcomponent()
						.getCurrencyDetailsVO(
								BBBInternationalShippingConstants.CURRENCY_MEXICO,
								country);
				Double fxRate = currenyDetailVO.getFxRate();
				
				if (fxRate != 0) {
					BigDecimal convertedValue = BigDecimal.valueOf(shippingGroupSubtotal / fxRate);
					convertedValue = convertedValue.divide(BigDecimal.ONE, 2,
							BigDecimal.ROUND_HALF_UP);
					shippingGroupSubtotal = convertedValue.doubleValue();
				}
				
				if (this.isLoggingDebug()) {
					this.logDebug("Exiting method method mexicoOrderShippingCalculation: shippingGroupSubtotal after conersion in USD ="
							+ shippingGroupSubtotal);
				}

			}
		}
		return shippingGroupSubtotal;
	}

	/**
	 * @param siteId
	 *            , shippingMethod, hardgoodShippingGroup
	 * @return double: shipping surcharge
	 *
	 * @description This method is to calculate shipping surcharge. It loops
	 *              through each commerce item in the shipping group and sums
	 *              the finalSurcharge and finally sets into the
	 *              shippingGroupPriceInfo.finalSurcharge property.
	 */
	@SuppressWarnings("unchecked")
    public double calculateSurcharge(final String siteId,
			final HardgoodShippingGroup hardgoodShippingGroup) throws PricingException {

		if (this.isLoggingDebug()){
			this.logDebug("Inside calculateSurcharge" + siteId + hardgoodShippingGroup);
		}
		final List<CommerceItemRelationship> commerceItemRelationshipList = hardgoodShippingGroup
				.getCommerceItemRelationships();
		double finalSurcharge = 0.0;
		CommerceItem commerceItem;
		for (final CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {

			commerceItem = commerceItemRelationship
					.getCommerceItem();

			//String state = null;

			//state = hardgoodShippingGroup.getShippingAddress().getState();

			if ((commerceItem != null) && !(commerceItem instanceof NonMerchandiseCommerceItem) ) {
				try {
//					if (!this.getCatalogUtil().isFreeShipping(
//							siteId, commerceItem.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod())) {

						final double surchargeAmount = this.getCatalogUtil().getSKUSurcharge(
							siteId, commerceItem.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod());

						finalSurcharge = finalSurcharge
							+ (surchargeAmount * commerceItemRelationship.getQuantity());
//					}
				} catch (final BBBSystemException e) {
					throw new PricingException(e);
				} catch (final BBBBusinessException e) {
					throw new PricingException(e);
				}
			}
		}

		/*BBBShippingPriceInfo bbbShippingPriceInfo = (BBBShippingPriceInfo) hardgoodShippingGroup.getPriceInfo();
		if( bbbShippingPriceInfo != null){
			//bbbShippingPriceInfo.setFinalSurcharge(finalSurcharge);

			return bbbShippingPriceInfo
					.getFinalSurcharge();

		}else{
			throw new PricingException("bbb");
		}*/
		
		
		int shippingSurchargeCap = 0;
		 List<String> maxSurcahrgeCapList = null;

     try {
         maxSurcahrgeCapList = this.getCatalogUtil().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,
                         this.SHIPPING_SURCHARGE_CAP);
     } catch (final BBBSystemException e) {
         this.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : "
                         + this.SHIPPING_SURCHARGE_CAP);
     } catch (final BBBBusinessException e) {
         this.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : "
                         + this.SHIPPING_SURCHARGE_CAP);
     }

      if((null != maxSurcahrgeCapList) && (maxSurcahrgeCapList.size() > 0)) {
          if(BBBUtility.isNumericOnly(maxSurcahrgeCapList.get(0).toString())) {
              shippingSurchargeCap = Integer.parseInt(maxSurcahrgeCapList.get(0).toString());
          }
      }
		
      if((shippingSurchargeCap > 0) && (finalSurcharge > shippingSurchargeCap)){
    	  finalSurcharge = shippingSurchargeCap;
      }

		return finalSurcharge;

	}

	/**
	 * @return BBBCatalogTools
	 */
	public BBBCatalogToolsImpl getCatalogUtil() {
		return this.mCatalogUtil;
	}

	/**
	 * @param catalogUtil
	 */
	public void setCatalogUtil(final BBBCatalogToolsImpl catalogUtil) {
		this.mCatalogUtil = catalogUtil;
	}

	/**
	 * Generates price beans for a relationship specified. This method takes all
	 * commerce item's price infos with range located within relationship's
	 * range. For each price info it creates a price bean.
	 *
	 * @param pRelationship
	 *            - specifies a shipping-group-commerce-item relationsip to
	 *            build price beans from.
	 * @return list of price beans for the relationship specified.
	 */
	@SuppressWarnings("unchecked")
    public List<UnitPriceBean> generatePriceBeansForRelationship(final ShippingGroupCommerceItemRelationship pRelationship) {
		List<UnitPriceBean> unitPrices = new ArrayList<UnitPriceBean>();

		final Range relationshipRange = pRelationship.getRange();

		final ItemPriceInfo priceInfo = pRelationship.getCommerceItem().getPriceInfo();
		if (priceInfo != null) {
			// Only price infos with proper ranges should be used
			final List<DetailedItemPriceInfo> currentPriceDetails = priceInfo.getCurrentPriceDetailsForRange(relationshipRange);
			unitPrices = this.generatePriceBeans(currentPriceDetails);
		}
		return unitPrices;
	}



	@SuppressWarnings("unused")
	public double calculateItemSurchargeInSG(final String pSiteId, final ShippingGroupCommerceItemRelationship pCommerceItemRelationship) throws BBBBusinessException, BBBSystemException {
		if (this.isLoggingDebug()) {
			this.logDebug("Inside calculateItemSurchargeInSG : input - siteId " + pSiteId + ", ShippingGroupCommerceItemRelationship " + pCommerceItemRelationship);
		}
		double itemSurcharge = 0.0;
		final ShippingGroup sg = pCommerceItemRelationship.getShippingGroup();

		if ((sg instanceof BBBHardGoodShippingGroup) && (pCommerceItemRelationship.getCommerceItem() instanceof BBBCommerceItem)) {

			final BBBCommerceItem tempItem = (BBBCommerceItem) pCommerceItemRelationship.getCommerceItem();
			final SKUDetailVO skuVO = this.getCatalogUtil().getSKUDetails(pSiteId, tempItem.getCatalogRefId(), false, true, true);
			final BBBShippingPriceInfo shippingPriceInfo = (BBBShippingPriceInfo) sg.getPriceInfo();

			final BBBHardGoodShippingGroup hgsg = (BBBHardGoodShippingGroup) sg;

			final String excludedItems = (String) hgsg.getSpecialInstructions().get(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS);

			if (((skuVO.getShippingSurcharge() == 0) || (shippingPriceInfo.getFinalSurcharge() == 0) || this.getCatalogUtil().isFreeShipping(pSiteId, tempItem.getCatalogRefId(), hgsg.getShippingMethod()))
					&& !this.isItemInExcludedItemList(excludedItems, tempItem.getCatalogRefId())) {
				return 0;
			} else {
				itemSurcharge = this.getCatalogUtil().getSKUSurcharge(pSiteId, tempItem.getCatalogRefId(), hgsg.getShippingMethod());
				return itemSurcharge;// * pCommerceItemRelationship.getQuantity();
			}
		}

		return itemSurcharge;
	}

	private boolean isItemInExcludedItemList(final String excludedItems, final String catalogRefId){
		if(!StringUtils.isBlank(excludedItems)) {
			final StringTokenizer commaTokenizer = new StringTokenizer(excludedItems, ",");
			while(commaTokenizer.hasMoreElements()){
				if(commaTokenizer.nextToken().equalsIgnoreCase(catalogRefId)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get a new adjustment.
	 *
	 * @param pPricingModel
	 * 			pricing model.
	 * @param discountAmount
	 * 			discount amount.
	 * @return PricingAdjustment
	 * @throws RepositoryException
	 */
	public PricingAdjustment getPromotionAdjustment(final RepositoryItem pPricingModel, final double discountAmount, final long quantityAdjusted) {
		String descriptorName = "";
		PricingAdjustment adjustment = null;
		try {
			descriptorName = pPricingModel.getItemDescriptor().getItemDescriptorName();
		} catch (final RepositoryException e) {
			this.logError(e);
		}
		adjustment = new PricingAdjustment(descriptorName, pPricingModel, this.round(-discountAmount * quantityAdjusted), quantityAdjusted);
		return adjustment;
	}

	/**
	 * This method is used to get promotion by promotion/coupon id.
	 * 
	 * @param identifier
	 * @param type
	 * @return
	 * @throws RepositoryException
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getPromotion(final String identifier, final String type) throws RepositoryException,
			BBBSystemException, BBBBusinessException {
		vlogDebug("BBBPricingTools.getPromotion: Starts, Requested promotions for given {0} and id {1} ", type,
				identifier);
		RepositoryItem[] promotions = null;
		if (BBBCoreConstants.ATG_PROMOTION.equalsIgnoreCase(type)) {
			/*
			 * for ATG_PROMOTION type we are getting promotion from Promotion
			 * Repository by promotion id.
			 */
			final RqlStatement statement = RqlStatement.parseRqlStatement(BBBCoreConstants.GET_PROMOTION_QUERY);
			final Object[] params = new Object[1];
			params[0] = identifier;
			final RepositoryView view = this.getCatalogUtil().getCatalogRepository()
					.getView(getCatalogUtil().getClaimableTools().getPromotionPropertyName());
			promotions = extractDbCall(statement, params, view);
			vlogDebug("BBBPricingTools.getPromotion: Items returned {0} after executing query {1} with params {2} ",
					promotions, BBBCoreConstants.GET_PROMOTION_QUERY, params);
		} else {
			/*
			 * In case of COUPON type 1st we are querying Claimable repository
			 * to get coupons then we are extracting promotions form that
			 * coupon.
			 */
			promotions = getCatalogUtil().getPromotions(identifier);
			vlogDebug("BBBPricingTools.getPromotion: Promotions returned {0} by querying with coupon id {1}",
					promotions, identifier);
		}
		vlogDebug("BBBPricingTools.getPromotion: Ends, Found promotions {0} for given {1} id {2} ", promotions, type,
				identifier);
		return promotions;
	}

	protected RepositoryItem[] extractDbCall(final RqlStatement statement, final Object[] params,
			final RepositoryView view) throws RepositoryException {
		RepositoryItem[] promotions;
		promotions = statement.executeQuery(view, params);
		return promotions;
	}




	/**
	 * This method takes the Order object and get its priceInfo object and sets
	 * into the PriceInfoVO.
	 *
	 * @param Order
	 * @param pReq
	 * @return PriceInfoVO
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public PriceInfoVO getOrderPriceInfo(final OrderImpl pOrder) {

		final PriceInfoVO priceInfoVO = new PriceInfoVO();
		if ((pOrder != null) && (pOrder.getPriceInfo() != null)) {
		    final BBBOrderPriceInfo orderPriceInfo = (BBBOrderPriceInfo) pOrder.getPriceInfo();
            priceInfoVO.setOrderPriceInfo(orderPriceInfo);

			int onlinePurchasedItemCount = 0;
			int storePruchasedItemCount =0;
			double onlineTotalRawAmount = 0.0;
			double onlineOrderPreTaxTotal = 0.0;
			double totalSavedAmount  = 0.0;

			double storeTotalRawAmount  =0.0;//A1
			double storeTotalActualAmount  =0.0;//A1
			double onlineOrderTotal = 0.0;//I
			double orderTotal = 0.0;//J
			double onlineDiscountedAmount = 0.0;//C


			double totalShippingAmount = 0.0;//F
			double shippingSavedAmount = 0.0;//F1
			double totalShippingSurcharge = 0.0;//G
            double surchargeSavings = 0.0;
			double shippingSavings = 0.0;

			double onlineEcoFeeTotal = 0.0;//D
			double storeEcoFeeTotal = 0.0;//A2
			double giftWrapTotal = 0.0;//E
			boolean isFreeShipping = false;
			
			double deliverySurchargeTotal = 0.0;
		    double assemblyFeeTotal = 0.0;
		    double deliverySurchargeRawTotal = 0.0;
		    double assemblyFeeRawTotal = 0.0;
		    boolean isLtlOrder = false;
		    
			final List commerceItems = pOrder.getCommerceItems();
			CommerceItem item;
			if(commerceItems != null) {
    			for (final Iterator iterator = commerceItems.iterator(); iterator
                        .hasNext();) {
                    item = (CommerceItem) iterator.next();
                    if (item != null) {
                        if(item instanceof BBBCommerceItem){
                            if(StringUtils.isEmpty(((BBBCommerceItem) item).getStoreId())) {
                                onlinePurchasedItemCount += item.getQuantity();
                                onlineTotalRawAmount += item.getPriceInfo().getRawTotalPrice();
                                onlineDiscountedAmount += item.getPriceInfo().getAmount();
                            } else {
                                storePruchasedItemCount += item.getQuantity();
                                storeTotalActualAmount += item.getPriceInfo().getRawTotalPrice();
                                storeTotalRawAmount += item.getPriceInfo().getAmount();
                            }
                        } else if(item instanceof EcoFeeCommerceItem) {
                            if(StringUtils.isEmpty(((EcoFeeCommerceItem) item).getStoreId())) {
                                onlineEcoFeeTotal += item.getPriceInfo().getAmount();
                            } else {
                                storeEcoFeeTotal += item.getPriceInfo().getAmount();
                            }
                        } else if (item instanceof GiftWrapCommerceItem){
                        	giftWrapTotal = giftWrapTotal + item.getPriceInfo().getAmount();
                        }else if (item instanceof LTLDeliveryChargeCommerceItem) {
                        	isLtlOrder = true;
                        	deliverySurchargeTotal += item.getPriceInfo().getAmount();
                        	deliverySurchargeRawTotal += item.getPriceInfo().getRawTotalPrice();
            			}else if (item instanceof LTLAssemblyFeeCommerceItem) {
            				assemblyFeeTotal += item.getPriceInfo().getAmount();
            				assemblyFeeRawTotal += item.getPriceInfo().getRawTotalPrice();
                        }
                    }
                }
			}
			 int shippingSurchargeCap = 0;
			 List<String> maxSurcahrgeCapList = null;

            try {
                maxSurcahrgeCapList = this.getCatalogUtil().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,
                                this.SHIPPING_SURCHARGE_CAP);
            } catch (final BBBSystemException e) {
                this.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : "
                                + this.SHIPPING_SURCHARGE_CAP);
            } catch (final BBBBusinessException e) {
                this.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : "
                                + this.SHIPPING_SURCHARGE_CAP);
            }

             if((null != maxSurcahrgeCapList) && (maxSurcahrgeCapList.size() > 0)) {
                 if(BBBUtility.isNumericOnly(maxSurcahrgeCapList.get(0).toString())) {
                     shippingSurchargeCap = Integer.parseInt(maxSurcahrgeCapList.get(0).toString());
                 }
             }

			// This iterates through all the shipping groups and sums totalShippingAmount and totalSurcharge
			BBBShippingPriceInfo priceInfo;
			for (final ShippingGroupImpl shippingGroup : (List<ShippingGroupImpl>)pOrder.getShippingGroups()){

				priceInfo = (BBBShippingPriceInfo) shippingGroup.getPriceInfo();

                if(priceInfo != null){
                    totalShippingAmount += priceInfo.getRawShipping();
                    totalShippingSurcharge += priceInfo.getSurcharge();
            		if(shippingGroup instanceof BBBHardGoodShippingGroup){
            			this.fillAdjustments(priceInfoVO, shippingGroup);
                    	shippingSavedAmount += priceInfo.getRawShipping() - priceInfo.getFinalShipping();
        /*            	
                    	if(((shippingSurchargeCap > 0) && (priceInfo.getSurcharge() > shippingSurchargeCap)) || totalShippingSurcharge > shippingSurchargeCap) {
//                        	shippingSavedAmount += priceInfo.getSurcharge() - priceInfo.getFinalSurcharge();
//                        	surchargeSavings += priceInfo.getSurcharge() - shippingSurchargeCap;
                        	totalShippingSurcharge = shippingSurchargeCap;
                    	}
                    	*/
                    	
                    	shippingSavedAmount += priceInfo.getSurcharge() - priceInfo.getFinalSurcharge();
                    	surchargeSavings += priceInfo.getSurcharge() - priceInfo.getFinalSurcharge();
                    	
                    	shippingSavings += priceInfo.getRawShipping() - priceInfo.getFinalShipping();
            		}
                }
			}
			// If order does not have ltl item, then make isShipMethodAvlForAllLTLItem false for non ltl order
			if((Double.compare(totalShippingAmount,0.0) == 0) && (onlinePurchasedItemCount > 0)) {
				isFreeShipping = true;
			}
			totalSavedAmount = getTotalSavings(onlineTotalRawAmount,onlineDiscountedAmount,shippingSavedAmount,deliverySurchargeRawTotal,deliverySurchargeTotal,storeTotalActualAmount,storeTotalRawAmount);
			priceInfoVO.setStoreAmount(storeTotalRawAmount);
			priceInfoVO.setStoreEcoFeeTotal(storeEcoFeeTotal);
			priceInfoVO.setOnlinePurchaseTotal(onlineDiscountedAmount);
			priceInfoVO.setOnlineEcoFeeTotal(onlineEcoFeeTotal);
			priceInfoVO.setGiftWrapTotal(giftWrapTotal);
			priceInfoVO.setRawShippingTotal(totalShippingAmount);
			priceInfoVO.setTotalSurcharge(totalShippingSurcharge);
			priceInfoVO.setPromoDiscountAmount(totalSavedAmount);
			priceInfoVO.setOnlineRawTotal(onlineDiscountedAmount + totalSavedAmount);
			final TaxPriceInfo taxPriceInfo = pOrder.getTaxPriceInfo();
            double taxAmount = 0.0;
            if(taxPriceInfo != null) {
                taxAmount = taxPriceInfo.getAmount();//H
                priceInfoVO.setTotalTax(taxAmount);

            }
            onlineOrderPreTaxTotal = (onlineDiscountedAmount + onlineEcoFeeTotal + giftWrapTotal + totalShippingAmount + totalShippingSurcharge + surchargeSavings + assemblyFeeTotal + deliverySurchargeTotal) - shippingSavedAmount;
            onlineOrderTotal = onlineOrderPreTaxTotal + taxAmount;
            orderTotal = onlineOrderTotal + storeEcoFeeTotal + storeTotalRawAmount;
            if(taxPriceInfo != null)
            {
            	priceInfoVO.setShippingStateLevelTax(taxPriceInfo.getStateTax());
                priceInfoVO.setShippingCountyLevelTax(taxPriceInfo.getCountyTax());
            }
            priceInfoVO.setOnlineTotal(onlineOrderTotal);
            priceInfoVO.setOrderPreTaxAmout(onlineOrderPreTaxTotal);
            priceInfoVO.setTotalAmount(orderTotal);
            priceInfoVO.setFreeShipping(isFreeShipping);
            priceInfoVO.setHardgoodShippingGroupItemCount(onlinePurchasedItemCount);
            priceInfoVO.setStorePickupShippingGroupItemCount(storePruchasedItemCount);
            priceInfoVO.setTotalSavedAmount(totalSavedAmount);
            priceInfoVO.setSurchargeSavings(surchargeSavings);
            priceInfoVO.setShippingSavings(shippingSavings);
            
            priceInfoVO.setFinalShippingCharge(priceInfoVO.getRawShippingTotal() - priceInfoVO.getShippingSavings());
            
            //LTL start
            String thresholdAmountString;
			try {
				thresholdAmountString = this.getCatalogUtil().getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount").get(0);
				double thresholdAmount = Double.parseDouble(thresholdAmountString);
	            if(deliverySurchargeRawTotal>thresholdAmount){
	            	priceInfoVO.setMaxDeliverySurchargeReached(true);
	            	priceInfoVO.setMaxDeliverySurcharge(thresholdAmount);
	            	priceInfoVO.setDeliverySurchargeSaving(this.round(deliverySurchargeRawTotal - thresholdAmount));
	            }
/*	            if(deliverySurchargeRawTotal ==0.0 && isLtlOrder){
	            	isFreeShipping=false;
	            	priceInfoVO.setFreeShipping(isFreeShipping);
	            }
*/	            
	            priceInfoVO.setTotalDeliverySurcharge(deliverySurchargeRawTotal);
				priceInfoVO.setTotalAssemblyFee(assemblyFeeRawTotal);
			} catch (BBBSystemException e) {
				logError("Error getting thresholdAmount from config keys", e);
			} catch (BBBBusinessException e) {
				logError("Error getting thresholdAmount from config keys", e);
			}
            
            //LTL end
            
            //Append all used and not used promotions
            this.fillAllAdjustments(priceInfoVO, pOrder);


            for(final CommerceItem pCommItem: (List<CommerceItem>)pOrder.getCommerceItems()){
            	this.fillItemAdjustments(priceInfoVO, pCommItem);
            }

		}
		return priceInfoVO;
	}

	public double getTotalSavings(double onlineTotalRawAmount,
			double onlineDiscountedAmount, double shippingSavedAmount,
			double deliverySurchargeRawTotal, double deliverySurchargeTotal,
			double storeTotalActualAmount, double storeTotalRawAmount) {
		// TODO Auto-generated method stub
		/***
		 * BBBSL-11422 Update the formula for Total Savings to not include
		 * Shipping Discount On Cart, SPC, MPC, Order Preview and Order
		 * Confirmation page
		 * 
		 * Note: Removed shippingSavedAmount
		 * 
		 */
		return (onlineTotalRawAmount - onlineDiscountedAmount) + (deliverySurchargeRawTotal - deliverySurchargeTotal);
		
	}

	/**
	 * This method takes the PriceInfoVO & shippingGroup , iterates through the
	 * PricingAdjustments and populate the PriceInfoVO adjustments.
	 *
	 * @param PriceInfoVO
	 * @param shippingGroup
	 */
	public void fillAdjustments(final PriceInfoVO priceInfoVO,
			final ShippingGroup shippingGroup) {
		final BBBShippingPriceInfo priceInfo = (BBBShippingPriceInfo) shippingGroup.getPriceInfo();
		final Map<RepositoryItem, Double> shippingAdjustments = priceInfoVO.getShippingAdjustments();
		PricingAdjustment spa;
		if((priceInfo != null) && priceInfo.isDiscounted()) {
	  		  for (final Object spaObj : priceInfo.getAdjustments()) {
	  			  spa = (PricingAdjustment) spaObj;
	  			  if (spa.getPricingModel() != null){
	  				  if (shippingAdjustments.get(spa.getPricingModel()) == null) {
	  					  shippingAdjustments.put(spa.getPricingModel(), Math.abs(spa.getTotalAdjustment()));
	  				  } else {
	  					  Double adjAmount = shippingAdjustments.get(spa.getPricingModel());
	  					  adjAmount += Math.abs(spa.getTotalAdjustment());
	  					  shippingAdjustments.put(spa.getPricingModel(), adjAmount);
	  				  }

	  			  }
	  		  }
		}
		priceInfoVO.setShippingAdjustments(shippingAdjustments);
	}

	/**
	 * This method takes the PriceInfoVO & order , iterates through the
	 * PricingAdjustments of items and populate the PriceInfoVO adjustments.
	 *
	 * @param PriceInfoVO
	 * @param order
	 */
	@SuppressWarnings("rawtypes")
    public void fillItemAdjustments(final PriceInfoVO priceInfoVO,
			final CommerceItem pItem) {
		final List priceInfoList = pItem.getPriceInfo().getCurrentPriceDetails();
		Map<RepositoryItem, Double> itemAdjustments = priceInfoVO.getItemAdjustments();
		if(itemAdjustments == null) {
		    itemAdjustments = new HashMap<RepositoryItem, Double>();
		}
		DetailedItemPriceInfo dpi;
		PricingAdjustment spa;
		for (final Iterator iterator = priceInfoList.iterator(); iterator.hasNext();) {
            dpi = (DetailedItemPriceInfo) iterator.next();
            for (final Object spaObj : dpi.getAdjustments()) {
                spa = (PricingAdjustment) spaObj;
                if (spa.getPricingModel() != null){
                    if (itemAdjustments.get(spa.getPricingModel()) == null) {
                      itemAdjustments.put(spa.getPricingModel(), Math.abs(spa.getTotalAdjustment()));
                    } else {
                        Double adjAmount = itemAdjustments.get(spa.getPricingModel());
                        adjAmount += Math.abs(spa.getTotalAdjustment());
                        itemAdjustments.put(spa.getPricingModel(), adjAmount);
                    }

                }
            }
        }

		priceInfoVO.setItemAdjustments(itemAdjustments);
	}


	/**
	 * This method takes the PriceInfoVO & Order , iterates through the
	 * Promotions and populate the PriceInfoVO adjustments.
	 *
	 * @param PriceInfoVO
	 * @param pOrder
	 */

	@SuppressWarnings("unchecked")
	public void fillAllAdjustments(final PriceInfoVO priceInfoVO,
			final Order pOrder) {
		List<RepositoryItem> allPromotions = new ArrayList<RepositoryItem>();
		final List<RepositoryItem> appliedPromotions = new ArrayList<RepositoryItem>();
		List<RepositoryItem> unAppliedPromotions = new ArrayList<RepositoryItem>();



		if(pOrder.getProfileId() != null){
			try{
				allPromotions = (List<RepositoryItem>) this.getPromotionTools().getPromotions(pOrder.getProfileId());

				final List<RepositoryItem> pOrderPromotions = new ArrayList<RepositoryItem>();
				final List<RepositoryItem> pTaxPromotions = new ArrayList<RepositoryItem>();
				final List<RepositoryItem> pItemPromotions = new ArrayList<RepositoryItem>();
				final List<RepositoryItem> pShippingPromotions = new ArrayList<RepositoryItem>();

				this.getPromotionTools().getOrderPromotions(pOrder, pOrderPromotions, pTaxPromotions, pItemPromotions, pShippingPromotions, false);
				
				if(allPromotions != null){
					appliedPromotions.addAll(pOrderPromotions);
					appliedPromotions.addAll(pTaxPromotions);
					appliedPromotions.addAll(pItemPromotions);
					appliedPromotions.addAll(pShippingPromotions);

					unAppliedPromotions = allPromotions;
					unAppliedPromotions.removeAll(appliedPromotions);
				}

				if(appliedPromotions.size() > 0){
					priceInfoVO.setAppliedAdjustmentsList(appliedPromotions);
				}

				if(unAppliedPromotions.size() > 0){
					priceInfoVO.setUnAppliedAdjustmentsList(unAppliedPromotions);
				}

			}catch (final PromotionException e) {
				if(this.isLoggingError()){
					this.logError("PromotionException occorred while listing all promotions atached to user's profile", e);
				}
			}catch (final Exception e) {
				if(this.isLoggingError()){
					this.logError("Exception occorred while listing all AdjustmentsList in priceInfoVO");
				}
			}
		}else{
			if(this.isLoggingError()){
				this.logError("Invalid Profile from Order. The order is not linked to any profile.");
			}
		}
	}

	/**
	 * This method takes the shippingGroup , Order and shipping Method id and
	 * iterates through the PricingAdjustments and populate the discounted shipping cosat for the .
	 * Free Standard Shipping case.
	 * @param shippingGroup
	 * @param order
	 * @param shipMethodId
	 */
	@SuppressWarnings("unchecked")
	public double fillAdjustmentsForShipMethod(final ShippingGroup shippingGroup, final BBBOrder order,
					final String shipMethodId) {

				if (this.isLoggingDebug()) {
					this.logDebug("Inside fillAdjustmentsForShipMethod :: Start ");
				}

				final BBBShippingPriceInfo priceInfo = (BBBShippingPriceInfo) shippingGroup
						.getPriceInfo();
				final Map<String, ShippingPriceInfo> newShipMethodInfo = new HashMap<String, ShippingPriceInfo>();
				double discountedShipping = 0.0;
				double shippingWithSurcharge = 0.0;
				if (priceInfo != null) {

					try {
						final Order simpleOrder = order;
						final RepositoryItem profileItem = this.getProfile(order.getProfileId());
						final Collection pricingModel = this.getShippingPricingEngine()
								.getPricingModels(profileItem);
						double adjAmount = 0.0;

						if (!shipMethodId.isEmpty()
								&& shipMethodId
										.equals(shippingGroup.getShippingMethod())) {
							if (priceInfo != null) {
								PricingAdjustment spa;
								for (final Object spaObj : priceInfo.getAdjustments()) {
									spa = (PricingAdjustment) spaObj;
									if (spa.getPricingModel() != null) {
										adjAmount += Math.abs(spa.getTotalAdjustment());
									}
								}
								 // change for rest shipping method
								final DecimalFormat newFormat = new DecimalFormat("#.##");
								shippingWithSurcharge = (priceInfo.getRawShipping()+ priceInfo.getSurcharge())- adjAmount ;
								final double shippingWithSurchargeFmt =  Double.valueOf(newFormat.format(shippingWithSurcharge));

								if (Double.compare(shippingWithSurchargeFmt,0.0) == 0){
								    discountedShipping=shippingWithSurchargeFmt;
								}else if ((priceInfo.getRawShipping()+ priceInfo.getSurcharge())>=adjAmount){
								    discountedShipping=priceInfo.getRawShipping() - adjAmount;
								}else{
								    discountedShipping=adjAmount-priceInfo.getRawShipping();
								}

							}
						}else if(!shipMethodId.isEmpty()
								&& shipMethodId
								.equals(BBBCoreConstants.SHIP_METHOD_STANDARD_ID)){

							newShipMethodInfo.put(BBBCoreConstants.SHIP_METHOD_TEMP,shippingGroup.getPriceInfo());

							this.setTempShippingMethod(shippingGroup.getShippingMethod());
							shippingGroup
									.setShippingMethod(BBBCoreConstants.SHIP_METHOD_STANDARD_ID);
							RepositoryItem[] coupons = null;
							Map<RepositoryItem, String> promoMap = new LinkedHashMap<RepositoryItem, String>();
							
							/*(BBBH-5142) Desktop | standard shipping price displayed as $0 on selecting express*/
							RepositoryItem pricingModelItem = null;
							if (pricingModel!=null) {
								for(Object pricingModelObj : pricingModel){
									pricingModelItem = (RepositoryItem) pricingModelObj; 
									coupons = getPromotionTools().getCoupons(pricingModelItem);
									if(coupons!=null){ 
										promoMap.put(coupons[0], pricingModelItem.getRepositoryId());
										if (this.isLoggingDebug()) {
											logDebug("Putting coupon "+ coupons[0].getRepositoryId() + " in the promo map");
										}
									}
								}
							}
							
							Map pExtraParameters = new HashMap();
							pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap);
							final BBBShippingPriceInfo info = (BBBShippingPriceInfo) this.getShippingPricingEngine()
									.priceShippingGroup(simpleOrder, shippingGroup,
											pricingModel, this.getDefaultLocale(),
											profileItem, pExtraParameters);

							if (info != null) {
								PricingAdjustment spa;
								for (final Object spaObj : info.getAdjustments()) {
									spa = (PricingAdjustment) spaObj;
									if (spa.getPricingModel() != null) {
										adjAmount += Math.abs(spa.getTotalAdjustment());
									}

								}
								 // change for rest shipping method
								final DecimalFormat newFormat = new DecimalFormat("#.##");
								shippingWithSurcharge = (info.getRawShipping()+ info.getSurcharge())- adjAmount ;
								final double shippingWithSurchargeFmt =  Double.valueOf(newFormat.format(shippingWithSurcharge));

								if (Double.compare(shippingWithSurchargeFmt,0.0) == 0){
								    discountedShipping=shippingWithSurchargeFmt;
								}else if ((info.getRawShipping()+ info.getSurcharge())>=adjAmount){
								    discountedShipping=info.getRawShipping() - adjAmount;
								}else{
								    discountedShipping=adjAmount - info.getSurcharge();
								}
							}
							shippingGroup.setShippingMethod(this.getTempShippingMethod());
							shippingGroup.setPriceInfo(newShipMethodInfo
									.get(BBBCoreConstants.SHIP_METHOD_TEMP));
						}

					} catch (final RepositoryException e) {
						if (this.isLoggingError()) {
							this.logError("fillAdjustmentsForShipMethod : Repository Exception occorred while updating the Shipping Method prices for Free Standard Shipping");
						}
					} catch (final PricingException e) {
						if (this.isLoggingError()) {
							this.logError("fillAdjustmentsForShipMethod : Pricing Exception occorred while updating the Shipping Method prices for Free Standard Shipping");
						}
					}

				}

				if (this.isLoggingDebug()) {
					this.logDebug("Inside fillAdjustmentsForShipMethod :: End ");
				}

				return discountedShipping;
			}
	
	
	/**
	* This method takes the Current Order and
	* iterates through the commerce items and set the previous price of each commerce item with list/sale (current) price
	* This method is invoked only when country is changed from Mexico to US and vice versa
	* @param order
	*/
		
	@SuppressWarnings("unchecked")
	public void updatePreviousPricesInCommerceItems(Order order) {
		if (this.isLoggingDebug()) {
			logDebug("Entering - BBBPricingTools Method Name [updatePreviousPricesInCommerceItems]");
		}
			
		List<CommerceItem> commerceItemsList = null;
		double currentPrice = BBBCoreConstants.DOUBLE_ZERO;
		if (order != null && order instanceof OrderImpl) {
			OrderImpl orderImpl = (OrderImpl) order;
			commerceItemsList = (List<CommerceItem>)orderImpl.getCommerceItems();
		}
		if (!BBBUtility.isListEmpty(commerceItemsList)) {
			BBBCommerceItem bbbCommerceItem;
			for (CommerceItem commerceItem : commerceItemsList) {
				if (commerceItem instanceof BBBCommerceItem) {
					bbbCommerceItem = (BBBCommerceItem) commerceItem;
					currentPrice = bbbCommerceItem.getPriceInfo().getSalePrice() != BBBCoreConstants.DOUBLE_ZERO ? bbbCommerceItem.getPriceInfo().getSalePrice() : bbbCommerceItem.getPriceInfo().getListPrice();
					if (this.isLoggingDebug()) {
						logDebug("Previous Price is: " + bbbCommerceItem.getPrevPrice() + " Now Setting it to : " + currentPrice);
					}
					bbbCommerceItem.setPrevPrice(currentPrice);
				}
			}
		}
		if (this.isLoggingDebug()) {
			logDebug("Exit - BBBPricingTools Method Name [updatePreviousPriceInCommerceItems]");
		}
	}
		
	/**
	* This method retrieves the Saved items from Session bean and
	* iterates through each item and set the previous price of each item with list/sale (current) price
	* This method is invoked only when country is changed from Mexico to US and vice versa
	* @param pRequest
	* @param giftListManager
	* @param profile
	*/
		
		
	public void updatePreviousPricesInSavedItems (DynamoHttpServletRequest pRequest, GiftlistManager giftListManager, RepositoryItem profile) throws BBBSystemException, BBBBusinessException, CommerceException, RepositoryException {
			
		if (this.isLoggingDebug()) {
			logDebug("Entering - BBBPricingTools Method Name [updatePreviousPricesInSavedItems]");
		}
		List <GiftListVO>giftListVOs = null;
		BBBSavedItemsSessionBean savedItemsSessionBean = (BBBSavedItemsSessionBean) pRequest.resolveName(BBBCoreConstants.SAVEDCOMP);
		if (savedItemsSessionBean != null) {
			giftListVOs = savedItemsSessionBean.getItems();
		}
		
		if (!BBBUtility.isListEmpty(giftListVOs)) {
			double currentPrice;
			String productId;
			String skuId;
			Double listPrice;
			Double salePrice;
			boolean onSale;
			for (GiftListVO giftListVO : giftListVOs) {
				currentPrice = BBBCoreConstants.DOUBLE_ZERO;
				productId = giftListVO.getProdID();
				skuId = giftListVO.getSkuID();
				listPrice = getCatalogUtil().getListPrice(productId, skuId);
				salePrice = getCatalogUtil().getSalePrice(productId, skuId);
				onSale = getCatalogUtil().isSkuOnSale(productId, skuId);
				currentPrice = checkCurrentPrice(onSale, listPrice, salePrice);
				if (this.isLoggingDebug()) {
					logDebug("Previous Price is: " + giftListVO.getPrevPrice() + " Now Setting it to : " + currentPrice);
				}
				giftListVO.setPrevPrice(currentPrice);
				if (!profile.isTransient()) {
					MutableRepositoryItem item = (MutableRepositoryItem) giftListManager.getGiftitem(giftListManager.getGiftlistItemId(giftListManager.getWishlistId(profile.getRepositoryId()), skuId, productId, extractSiteId()));
					item.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, currentPrice);
					((MutableRepository) giftListManager.getGiftlistTools().getGiftlistRepository()).updateItem(item);
				}
			}
		savedItemsSessionBean.setGiftListVO(giftListVOs);
		}
		
		if (this.isLoggingDebug()) {
			logDebug("Exit - BBBPricingTools Method Name [updatePreviousPricesInSavedItems]");
		}
	}

	protected String extractSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
		
		
	public Double checkCurrentPrice(boolean onSale, Double listPrice, Double salePrice) {
		Double currentPrice;
		if (onSale) {
			if ((null != salePrice)) {
				currentPrice = salePrice;
			} else {
				currentPrice = listPrice;
			}
		} else {
			currentPrice = listPrice;
		}
		return currentPrice;
	}
	
	/**
	   * Checks to see if an order is restricted for pricing. This implementation checks if the order is in the
	   * QUOTED state or if the order is in the submitted state and has a non null quoteInfo present (which
	   * indicates that we are dealing with a quoted order).
	   *
	   * @param pOrder The order object
	   * @return boolean true if the order is restricted for pricing, false otherwise
	   */
	  public boolean isOrderRestrictedForPricing(Order pOrder) {
	    OrderStates orderStates = extractOrderStates();
	    if(pOrder.getState() == orderStates.getStateValue(OrderStates.QUOTED) ||
	            (pOrder.getState() == orderStates.getStateValue(OrderStates.SUBMITTED)))
	    {
	    	logDebug("Repricing called for submitted order with order id: ["+pOrder.getId()+"] from : "+Thread.currentThread().getStackTrace());
	    	return true;
	    }
	    return false;
	  }

	protected OrderStates extractOrderStates() {
		OrderStates orderStates = StateDefinitions.ORDERSTATES;
		return orderStates;
	}
}
