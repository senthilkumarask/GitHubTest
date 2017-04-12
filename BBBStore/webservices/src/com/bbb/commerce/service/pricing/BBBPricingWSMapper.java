/**
 * 
 */
package com.bbb.commerce.service.pricing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import com.bbb.common.BBBGenericService;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.GiftWrapVO;
import com.bbb.commerce.catalog.vo.LTLAssemblyFeeVO;
import com.bbb.commerce.catalog.vo.LTLDeliveryChargeVO;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.utils.BBBUtility;
import com.bedbathandbeyond.atg.Adjustment;
import com.bedbathandbeyond.atg.AdjustmentList;
import com.bedbathandbeyond.atg.Coupon;
import com.bedbathandbeyond.atg.CouponList;
import com.bedbathandbeyond.atg.Item;
import com.bedbathandbeyond.atg.ItemList;
import com.bedbathandbeyond.atg.MessageHeader;
import com.bedbathandbeyond.atg.PricingRequest;
import com.bedbathandbeyond.atg.PricingRequestInfo;
import com.bedbathandbeyond.atg.PricingResponse;
import com.bedbathandbeyond.atg.PricingResponseInfo;
import com.bedbathandbeyond.atg.ShippingList;

import atg.commerce.pricing.ShippingPriceInfo;

/**
 * @author Pradeep Reddy
 * 
 */
public class BBBPricingWSMapper extends BBBGenericService {

	private BBBCatalogToolsImpl mCatalogTools;

	private Repository mPromotionsRepository;

	private BBBOrderManager mOrderManager;

	private BBBShippingGroupManager mShippingGroupManager;

	private BBBCommerceItemManager mCommerceItemManager;

	private BBBPricingTools mPricingTools;

	private ProfileTools mProfileTools;
	
	private TransactionManager transactionManager;
	
	public final static String PWS = "PWS";
	
	public final static String INTL_ORDER_PREFIX = "E4X";
	
	public final static String WhiteGloveShipMethod= "LW";
	
    //Identify if its a tbs order
	private static final String TBS_ORDER_IDENTIFIER = "TBS";
	private static final String ORDER_TYPE = "ORDER_TYPE";
	
	private Repository siteRepository;
	
	/** The shipping method list for international order. */
	private List<String> shippingMethodList;
	
	private BBBOrderTools orderTools;

	public BBBOrder transformRequestToOrder(PricingRequest wsRequest, Profile pProfile, Map<String, Object> pParameters) throws BBBBusinessException, BBBSystemException {
		MessageHeader messageHeader = wsRequest.getHeader();
		BBBOrder bbbOrder = createOrder(messageHeader.getOrderId(), pProfile.getRepositoryId());
		return transformRequestToOrder(bbbOrder, wsRequest, pProfile, pParameters);
	}

	public BBBOrder transformRequestToOrder(BBBOrder bbbOrder, PricingRequest wsRequest, Profile pProfile, Map<String, Object> pParameters) throws BBBBusinessException, BBBSystemException {
		
		// BBBSL-2734. Added transaction & synchronization while updating the order.
		TransactionDemarcation td = new TransactionDemarcation();
		boolean rollback = false;
		try {
			td.begin(getTransactionManager());
			synchronized (bbbOrder) {
				/* Validate the webservice request */
				validatePricingRequest(wsRequest);

				MessageHeader messageHeader = wsRequest.getHeader();

				PricingRequestInfo pricingRequest = wsRequest.getRequest();
				populateOrderInfo(bbbOrder, messageHeader);
				pParameters.put(ORDER_TYPE, messageHeader.getOrderIdentifier());
				populateShippingGroups(bbbOrder, pProfile, pricingRequest, pParameters);
				pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED, isPartiallyShipped(wsRequest.getRequest()));
			}
		} catch (TransactionDemarcationException exp1) {
			this.logError("TransactionDemarcationException in transformRequestToOrder() in BBBPricingWSMapper class. ", exp1);
			rollback = true;
		}
		finally{
			try {
				td.end(rollback);
			} catch (TransactionDemarcationException exp2) {
				this.logError("TransactionDemarcationException while creating ending transaction in transformRequestToOrder() in BBBPricingWSMapper class.", exp2);
			}
		}
		return bbbOrder;
	}

	/**
	 * Execute validation rules on the incoming webservice request
	 * 
	 * @param wsRequest
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void validatePricingRequest(PricingRequest wsRequest) throws BBBBusinessException, BBBSystemException {

		ShippingList shippingList = wsRequest.getRequest().getShippingGroups();
		for (com.bedbathandbeyond.atg.ShippingGroup shippingGroup : shippingList.getShippingGroupArray()) {
			/* Validate Shipping methods. If the invocation is */
			try {
				getCatalogTools().getShippingMethod(shippingGroup.getShippingMethod());
			} catch (BBBBusinessException e) {
				throw new BBBBusinessException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1013,"The shipping method [" + shippingGroup.getShippingMethod() + "] is not valid. Please provide a valid shipping method", e);
			}
		}
	}

	private BBBOrder createOrder(String orderId, String pProfileID) throws BBBSystemException {
		BBBOrder bbbOrder = null;
		try {
			bbbOrder = (BBBOrder) mOrderManager.createOrder(pProfileID, PWS + orderId, getOrderManager().getOrderTools().getDefaultOrderType());
			
			logDebug("Created ATG order with order ID [" + bbbOrder.getId() + "]");
			
		} catch (CommerceException e) {

			String msg = "Error while creating order for profile [" + pProfileID + "]";
			logError(msg, e);
		
			throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1014,msg, e);
		}

		return bbbOrder;
	}

	private void populateOrderInfo(BBBOrder bbbOrder, MessageHeader messageHeader) {
		if (messageHeader.getSiteId() != null) {
			bbbOrder.setSiteId(messageHeader.getSiteId().toString());
		}
		RepositoryItem site = null;

		if (!StringUtils.isBlank(messageHeader.getSiteId().toString())) {
			try {
				site = getSiteRepository().getItem(messageHeader.getSiteId().toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM);
			} catch (RepositoryException repoExp) {
				logDebug("Error occured while populating OrderInfo", repoExp);
			}
		}

		OrderPriceInfo atgOrderPriceInfo = bbbOrder.getPriceInfo();
		if (atgOrderPriceInfo == null) {
			atgOrderPriceInfo = new BBBOrderPriceInfo();
		}
		if (null != site) {
			atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY));
		} else if (messageHeader.getCurrencyCode() != null) {
			atgOrderPriceInfo.setCurrencyCode(String.valueOf(messageHeader.getCurrencyCode()));
		} else {
			atgOrderPriceInfo.setCurrencyCode(BBBCheckoutConstants.SITE_CURRENCY_USD);
		}
		
		logDebug("Setting the Site Id to  [" + bbbOrder.getSiteId() + "]" + " and Currency code to : " + atgOrderPriceInfo.getCurrencyCode());
		
		bbbOrder.setPriceInfo(atgOrderPriceInfo);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void populateShippingGroups(BBBOrder bbbOrder, Profile pProfile, PricingRequestInfo pPricingRequest, Map<String, Object> pParameters) throws BBBSystemException {
		/* First remove all the shipping groups */
		bbbOrder.removeAllShippingGroups();

		BBBAddressImpl address = null;
		BBBHardGoodShippingGroup bbbShippingGroup = null;
		ShippingList shippingList = pPricingRequest.getShippingGroups();

		Map<String, String> shippingGroupRelationMap = new HashMap<String, String>();
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap<ShippingGroupCommerceItemRelationship, Item>();
		Map<String, ShippingPriceInfo> storedShippingInfo = new HashMap<String, ShippingPriceInfo>();
		if (pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION) != null) {
			shippingGroupRelationMap = (Map<String, String>) pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION);
		} else {
			pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap);
		}
		if (pParameters.get(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM) != null) {
			itemInfoMap = (Map<ShippingGroupCommerceItemRelationship, Item>) pParameters.get(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM);
		} else {
			pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap);
		}
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO, storedShippingInfo);

		try {
			for (com.bedbathandbeyond.atg.ShippingGroup shippingGroup : shippingList.getShippingGroupArray()) {

				address = populateAddressInfo(new BBBAddressImpl(), shippingGroup.getShippingAddress());

				
				logDebug("Creating ATG Shipping group with shipping method [" + shippingGroup.getShippingMethod() + "]...");
				

				bbbShippingGroup = getShippingGroupManager().createHardGoodShippingGroup(bbbOrder, address, shippingGroup.getShippingMethod());
				String fromTBS = (String)pParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER);
				if(null != fromTBS && fromTBS.equalsIgnoreCase(BBBCheckoutConstants.TRUE_STRING) && shippingGroup != null && shippingGroup.getShippingPrice() != null) {
					bbbShippingGroup = populateTBSShippingPriceInRequest(bbbOrder, shippingGroup, bbbShippingGroup,storedShippingInfo);					
				}else if (null != fromTBS && fromTBS.equalsIgnoreCase(BBBCheckoutConstants.TRUE_STRING) && shippingGroup != null){
					bbbShippingGroup = populateZeroTBSShippingPriceInRequest(bbbOrder, shippingGroup, bbbShippingGroup,storedShippingInfo);		
				}
				shippingGroupRelationMap.put(bbbShippingGroup.getId(), shippingGroup.getShippingGroupId());
				
				logDebug("Created ATG Shipping group with shipping group [" + bbbShippingGroup.getId() + "]");
				

				itemInfoMap.putAll(populateItemInfo(bbbOrder, bbbShippingGroup, shippingGroup, pParameters));
			}

			
			logDebug("Updating ATG order with order ID [" + bbbOrder.getId() + "]");
			
			getOrderManager().updateOrder(bbbOrder);
			
			logDebug("Repricing ATG order with order ID [" + bbbOrder.getId() + "] for Order Subtotal");
			
			//Start: LTL-305 pricing web service change for incorporating LTL specific business rules
			HashMap parameters = new HashMap();
			parameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, bbbOrder);
			getPricingTools().priceOrderSubtotal(bbbOrder, getPricingTools().getDefaultLocale(), pProfile, parameters);
			//End: LTL-305 pricing web service change for incorporating LTL specific business rules
		} catch (CommerceException e) {
			String msg = "Error while creating shipping group";
			logError(e.getMessage(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1014,msg, e);
		}
	}

	/**
	 * This method is for collecting all promotion requested by coupon/promotion
	 * id, and add these promotion to active promotions of profile.
	 * 
	 * @param pBbbOrder
	 * @param pricingRequest
	 * @param profile
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Collection<RepositoryItem>> populatePromotions(BBBOrder pBbbOrder,
			PricingRequest pricingRequest, Profile profile) throws BBBBusinessException, BBBSystemException {
		Map<String, Collection<RepositoryItem>> promotionMap = new HashMap<String, Collection<RepositoryItem>>();
		Collection<RepositoryItem> itemPromotions = new ArrayList<RepositoryItem>();
		Collection<RepositoryItem> shippingPromotions = new ArrayList<RepositoryItem>();
		Collection<RepositoryItem> orderPromotions = new ArrayList<RepositoryItem>();
		logDebug("Populating Promotions for Coupons : " + pricingRequest);
		boolean isPartiallyShipped = isPartiallyShipped(pricingRequest.getRequest());
		CouponList coupons = pricingRequest.getHeader().getCoupons();
		Collection<RepositoryItem> promotionStatuses = (Collection<RepositoryItem>) profile
				.getPropertyValue(getPricingTools().getPromotionTools().getActivePromotionsProperty());
		if (coupons != null) {
			RepositoryItem[] promotionArray = null;
			for (Coupon coupon : coupons.getCouponArray()) {
				try {
					promotionArray = getPricingTools().getPromotion(coupon.getIdentifier(),
							String.valueOf(coupon.getType()));
					if (promotionArray != null && promotionArray.length > 0) {

						Integer promoType = (Integer) promotionArray[0].getPropertyValue("type");
						if (promoType == 0 && !isPartiallyShipped) {
							// Item Discount
							itemPromotions.add(promotionArray[0]);
						} else if (promoType == 5) {
							// Shipping Discount
							shippingPromotions.add(promotionArray[0]);
						} else if (promoType == 9 && !isPartiallyShipped) {
							// Order Discount
							orderPromotions.add(promotionArray[0]);
						}
						if(!BBBCoreConstants.ATG_PROMOTION.equalsIgnoreCase(String.valueOf(coupon.getType()))){
							RepositoryItem couponItem =getCatalogTools().getClaimableTools().getClaimableItem(coupon.getIdentifier());
							Integer uses = (Integer) promotionArray[0].getPropertyValue(getPricingTools()
									.getPromotionTools().getUsesProperty());
							RepositoryItem promotionStatus = getPricingTools().getPromotionTools().createPromotionStatus(
									profile.getDataSource(), promotionArray[0], uses, couponItem);
							if (promotionStatuses == null) {
								vlogDebug("BBBPricingWSMapper.populatePromotions: There is no active promotions in profile so creating new promotion status list");
								promotionStatuses = new ArrayList<RepositoryItem>();
							}
							promotionStatuses.add(promotionStatus);
						}
					} else {
						logDebug("Promotion with Id : " + coupon.getIdentifier()
								+ " is not available in Promotion Repository");
						throw new BBBBusinessException("1000", "Promotion with Id : " + coupon.getIdentifier()
								+ " is not available in Promotion Repository ");
					}
				} catch (RepositoryException e) {
					vlogError("BBBPricingWSMapper.populatePromotions: Repository exception occured while creating new promotion status or getting coupon/promotion");
					throw new BBBBusinessException(
							"BBBPricingWSMapper.populatePromotions: Repository exception occured while creating new promotion status or getting coupon/promotion",
							e);
				}
			}
			profile.setPropertyValue(getPricingTools().getPromotionTools().getActivePromotionsProperty(),
					promotionStatuses);
		}
		promotionMap.put(BBBCheckoutConstants.ITEM_PROMOTIONS, itemPromotions);
		promotionMap.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, shippingPromotions);
		promotionMap.put(BBBCheckoutConstants.ORDER_PROMOTIONS, orderPromotions);
		return promotionMap;
	}

	private BBBAddressImpl populateAddressInfo(BBBAddressImpl pbbbAddress, com.bedbathandbeyond.atg.Address pAddress) {
		
		logDebug("Setting Address in Order : " + pAddress);
		
		pbbbAddress.setAddress1(pAddress.getAddressLine1());
		pbbbAddress.setAddress2(pAddress.getAddressLine2());
		pbbbAddress.setCity(pAddress.getCity());
		pbbbAddress.setState(pAddress.getState());
		pbbbAddress.setPostalCode(pAddress.getZipCode());
		pbbbAddress.setCountry(pAddress.getCountry());
		pbbbAddress.setPhoneNumber(pAddress.getDayPhone());
		pbbbAddress.setAlternatePhoneNumber(pAddress.getEvePhone());
		pbbbAddress.setEmail(pAddress.getEmail());
		return pbbbAddress;
	}

	private Map<ShippingGroupCommerceItemRelationship, Item> populateItemInfo(BBBOrder bbbOrder, BBBHardGoodShippingGroup pbbbShippingGroup, com.bedbathandbeyond.atg.ShippingGroup pShippingGroup, Map<String, Object> pParameters)
			throws BBBSystemException {
		ItemList shipmentItems = pShippingGroup.getItemList();
		boolean isGiftWrapEligible = true;//pricing web service will not check for eligibility.
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap<ShippingGroupCommerceItemRelationship, Item>();
		CommerceItem commerceItem = null;
		CommerceItem deliveryCommerceItem = null;
		CommerceItem assemblyCommerceItem = null;
		for (Item shipmentItem : shipmentItems.getItemArray()) {
			
			try {
				RepositoryItem skuItem = getCatalogTools().getCatalogRepository().getItem(shipmentItem.getSku(), BBBCheckoutConstants.PROPERTY_SKU);
				if (skuItem == null) {
					throw new BBBBusinessException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1015,"The sku [" + shipmentItem.getSku() + "] provided is not valid. Please provide a valid sku.");
				}
				/*if (getCatalogTools().isGiftWrapItem(bbbOrder.getSiteId(), skuItem.getRepositoryId())) {
					isGiftWrapEligible = true;
				}*///Pricing webservice will not validate this.
				@SuppressWarnings("unchecked")
				Set<RepositoryItem> productItems = (Set<RepositoryItem>) skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT);
				if (productItems == null || productItems.size() == 0) {
					throw new BBBBusinessException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1015,"The sku [" + shipmentItem.getSku() + "] provided is not valid. Please provide a valid sku.");
				}
				String commerceItemType = ((BBBOrderTools) getOrderManager().getOrderTools()).getDefaultCommerceItemType();
				commerceItem = getOrderManager().addAsSeparateItemToShippingGroup(bbbOrder, pbbbShippingGroup, shipmentItem.getSku(), shipmentItem.getQuantity().longValue(),
						productItems.toArray(new RepositoryItem[0])[0].getRepositoryId(), commerceItemType);
				//Start: LTL-305 pricing web service change for incorporating LTL specific business rules
				if(this.getCatalogTools().isSkuLtl(bbbOrder.getSiteId(), shipmentItem.getSku()) && BBBUtility.isNotEmpty(pShippingGroup.getShippingMethod())){
					((BBBCommerceItem)commerceItem).setLtlItem(true);
					final LTLDeliveryChargeVO ltlDeliverChargeVO = this.getCatalogTools().getLTLDeliveryChargeSkuDetails(bbbOrder.getSiteId());
					if (ltlDeliverChargeVO != null) {
						deliveryCommerceItem = getOrderManager().addAsSeparateItemToShippingGroup(bbbOrder, pbbbShippingGroup, ltlDeliverChargeVO.getLtlDeliveryChargeSkuId(), shipmentItem.getQuantity().longValue(),
								ltlDeliverChargeVO.getLtlDeliveryChargeProductId(), ((BBBOrderTools) this.getOrderManager().getOrderTools()).getLtlDeliveryChargeCommerceItemType());
						((LTLDeliveryChargeCommerceItem)deliveryCommerceItem).setLtlCommerceItemRelation(commerceItem.getId());
					}
					if(pShippingGroup.getShippingMethod().equalsIgnoreCase(WhiteGloveShipMethod) && shipmentItem.getIsAssemblyRequested()){
						final LTLAssemblyFeeVO ltlAssemblyVO = this.getCatalogTools().getLTLAssemblyFeeSkuDetails(bbbOrder.getSiteId());
						if (ltlAssemblyVO != null) {
							assemblyCommerceItem = getOrderManager().addAsSeparateItemToShippingGroup(bbbOrder, pbbbShippingGroup, ltlAssemblyVO.getLtlAssemblySkuId(), shipmentItem.getQuantity().longValue(),
									ltlAssemblyVO.getLtlAssemblyProductId(), ((BBBOrderTools) this.getOrderManager().getOrderTools()).getLtlAssemblyFeeCommerceItemType());
							((LTLAssemblyFeeCommerceItem)assemblyCommerceItem).setLtlCommerceItemRelation(commerceItem.getId());
						}
					}
					if(assemblyCommerceItem!=null){
						((BBBCommerceItem)commerceItem).setAssemblyItemId(assemblyCommerceItem.getId());
						((BBBCommerceItem)commerceItem).setDeliveryItemId(deliveryCommerceItem.getId());
					} else{
						((BBBCommerceItem)commerceItem).setDeliveryItemId(deliveryCommerceItem.getId());
					}
				}
				//End: LTL-305 pricing web service change for incorporating LTL specific business rules
				if (commerceItem == null) {
					throw new CommerceException();
				}

				ShippingGroupCommerceItemRelationship shippingGroupCommerceItemRelationship = getShippingGroupManager().getShippingGroupCommerceItemRelationship(bbbOrder, commerceItem.getId(),
						pbbbShippingGroup.getId());

				itemInfoMap.put(shippingGroupCommerceItemRelationship, shipmentItem);
				
				logDebug("Added ATG Item for sku [" + commerceItem.getCatalogRefId() + "] with id [" + commerceItem.getId() + "]");
				
			} catch (CommerceException e) {
				String msg = "Error while adding commerce item [" + shipmentItem.getSku() + "] to shipping group";
				logError(msg, e);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1014,msg, e);
			} catch (RepositoryException e) {
				String msg = "Error while retrieving product details for item [" + shipmentItem.getSku() + "]";
				logError(msg, e);		
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1012,msg, e);
			} catch (BBBBusinessException e) {
				String msg = "Error while retrieving product details for item [" + shipmentItem.getSku() + "]";
				logError(msg, e);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1018,msg, e);
			}
		}

		if (pShippingGroup.getGiftWrapRequired() && isGiftWrapEligible) {
			try {
				
				logDebug("Adding gift wrap to shipping group [" + pbbbShippingGroup.getId() + "]");
				
				final GiftWrapVO giftWrapVO = getCatalogTools().getWrapSkuDetails(bbbOrder.getSiteId());
				String skuId = giftWrapVO.getWrapSkuId();
				int quantity = 1;
				String productId = giftWrapVO.getWrapProductId();
				String commerceItemType = ((BBBOrderTools) getOrderManager().getOrderTools()).getGiftWrapCommerceItemType();
				CommerceItem item = getOrderManager().addAsSeparateItemToShippingGroup(bbbOrder, pbbbShippingGroup, skuId, quantity, productId, commerceItemType);
				if (item != null) {
					pbbbShippingGroup.setGiftWrapInd(true);
					// Set gift wrap price to value passed in
//					if( pShippingGroup.getShippingPrice() != null ) {
//						BigDecimal giftWrapFee = pShippingGroup.getShippingPrice().getGiftWrapFee();
//						if( giftWrapFee != null ) {
//							item.getPriceInfo().setAmount(giftWrapFee.doubleValue());	
//							item.getPriceInfo().setAmountIsFinal(true);				
//						}
//					}
					// Set ALL gift wrap pricing to 0 - This is a temporary fix for TBS orders DS
					if(!StringUtils.isEmpty((String)pParameters.get(ORDER_TYPE)) && ((String)pParameters.get(ORDER_TYPE)).equalsIgnoreCase(TBS_ORDER_IDENTIFIER)){
						item.getPriceInfo().setAmount(0.0);	
						item.getPriceInfo().setAmountIsFinal(true);				
					}
				}
				
			} catch (BBBBusinessException e) {
				String msg = "Error while retrieving gift wrap item for site [" + bbbOrder.getSiteId() + "]";
				logError(msg, e);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1017,msg, e);
			} catch (CommerceException e) {
				String msg = "Error while adding gift wrap to shipping group [" + pbbbShippingGroup.getId() + "]";
				logError(msg, e);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1014,msg, e);
			}
		}
		return itemInfoMap;
	}

	/**
	 * To find out if the order is partially shipped or not.
	 * 
	 * @param pricingRequest
	 * 
	 * @return boolean
	 */
	@SuppressWarnings("unused")
	public boolean isPartiallyShipped(final PricingRequestInfo pricingRequest) {
		if (null != pricingRequest.getShippingGroups()) {

			final com.bedbathandbeyond.atg.ShippingGroup[] shippingGroups = pricingRequest.getShippingGroups().getShippingGroupArray();

			final ShippingList shippingList = pricingRequest.getShippingGroups();
			ItemList itemList = null;
			Item[] items = null;
			for (com.bedbathandbeyond.atg.ShippingGroup shippingGroup : shippingList.getShippingGroupArray()) {
				itemList = shippingGroup.getItemList();
				items = itemList.getItemArray();
				for (Item item : itemList.getItemArray()) {
					if (item.getStatus().equals(Item.Status.SHIPPED)) {
						return true;
					}

				}
			}
		}

		return false;
	}

	/**
	 * Transforms the BBB Order instance to Pricing response
	 * 
	 * @param pOrder
	 * @param pWSRequest
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public PricingResponse transformOrderToResponse(BBBOrder pOrder, final PricingRequest pWSRequest, Map<String, Object> pParameters) throws BBBSystemException, BBBBusinessException {
		PricingResponse pricingResponse = PricingResponse.Factory.newInstance();

		/* Initialize response instance with header detail */
		pricingResponse.setHeader(populateResponseHeader(pOrder, pWSRequest));

		/* Populate response instance */
		pricingResponse.setResponse(populatePricingResponse(pOrder, pWSRequest.getRequest(), pParameters));

		return pricingResponse;
	}

	/**
	 * Populates message header for response
	 * 
	 * @param pOrder
	 * @param pWSRequest
	 * @return
	 */
	private MessageHeader populateResponseHeader(BBBOrder pOrder, final PricingRequest pWSRequest) {
		MessageHeader pricingHeader = MessageHeader.Factory.newInstance();
		MessageHeader requestHeader = pWSRequest.getHeader();

		/* Set header information */
		pricingHeader.setOrderId(requestHeader.getOrderId());
		
		//Adding logic to send TBS orderIdentifier in-case of TBS order
		if(!StringUtils.isEmpty(requestHeader.getOrderIdentifier()) && requestHeader.getOrderIdentifier().equalsIgnoreCase(TBS_ORDER_IDENTIFIER)){
			pricingHeader.setOrderIdentifier(requestHeader.getOrderIdentifier());
		}
		
		pricingHeader.setSiteId(requestHeader.getSiteId());
		pricingHeader.setOrderDate(requestHeader.getOrderDate());
		if (requestHeader.getCurrencyCode() != null) {
			pricingHeader.setCurrencyCode(requestHeader.getCurrencyCode());
		}
		if (requestHeader.getCallingAppCode() != null) {
			pricingHeader.setCallingAppCode(requestHeader.getCallingAppCode());
		}
		pricingHeader.setTimestamp(Calendar.getInstance());

		return pricingHeader;
	}

	/**
	 * Populates pricing response
	 * 
	 * @param pOrder
	 * @param pPricingRequestInfo
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private PricingResponseInfo populatePricingResponse(BBBOrder pOrder, PricingRequestInfo pPricingRequestInfo, Map<String, Object> pParameters) throws BBBSystemException, BBBBusinessException {
		PricingResponseInfo pricingResponseInfo = PricingResponseInfo.Factory.newInstance();
		
		BBBOrder oldOrder = null;
		String originalOrder = pOrder.getId().replaceFirst(PWS, "");
		vlogDebug("originalOrder ..."+originalOrder);
		
		try {
			oldOrder = (BBBOrder) getOrderTools().getOrderFromOnlineOrBopusOrderNumber(originalOrder);
		} catch (RepositoryException e1) {
			logError("Error while getting Repository item  "+e1);
		} catch (CommerceException e1) {
			logError("Error while loading order "+e1);
		}
		//if international order than update shipping price according BCC flags 
		if(oldOrder != null && (BBBUtility.isNotEmpty(oldOrder.getInternationalOrderId())) )
		{
			vlogDebug("International Order updating shipping prices");
			updateISShippingCharges(pOrder);
		}
		/* Populate order price in the response */
		pricingResponseInfo.setOrderPrice(populateOrderPriceInResponse(pOrder, pPricingRequestInfo));

		/* Populate shipping groups in the response */
		pricingResponseInfo.setShippingGroups(populateShippingGroupsInResponse(pOrder, pPricingRequestInfo, pParameters));

		return pricingResponseInfo;
	}

	/**
	 * Populates Order price for response
	 * 
	 * @param pOrder
	 * @param pPricingRequestInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private com.bedbathandbeyond.atg.OrderPriceInfo populateOrderPriceInResponse(BBBOrder pOrder, PricingRequestInfo pPricingRequestInfo) {
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = com.bedbathandbeyond.atg.OrderPriceInfo.Factory.newInstance();
		OrderPriceInfo bbbOrderPriceInfo = pOrder.getPriceInfo();
		String thresholdAmountString = null;

		/* Set Order Total */
		if (bbbOrderPriceInfo.getTotal() > 0.0) {
			orderPriceInfo.setOrderTotal(toBigDecimal(bbbOrderPriceInfo.getTotal()));
		}
		//Start: LTL-305 pricing web service change for incorporating LTL specific business rules
		if (((BBBOrderPriceInfo)bbbOrderPriceInfo).getDeliverySurchargeTotal() > 0.0){
			orderPriceInfo.setTotalDeliverySurcharge(toBigDecimal(((BBBOrderPriceInfo)bbbOrderPriceInfo).getDeliverySurchargeTotal()));
		}
		if(((BBBOrderPriceInfo)bbbOrderPriceInfo).getAssemblyFeeTotal() > 0.0){
			orderPriceInfo.setTotalAssemblyFee(toBigDecimal(((BBBOrderPriceInfo)bbbOrderPriceInfo).getAssemblyFeeTotal()));
		}
		try {
			thresholdAmountString = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT).get(0);
		
		double thresholdAmount = Double.parseDouble(thresholdAmountString);
		if(thresholdAmount > 0.0){
			orderPriceInfo.setDeliveryThreshold(toBigDecimal(thresholdAmount));
		}
		if(((BBBOrderPriceInfo)bbbOrderPriceInfo).getDeliverySurchargeTotal() > thresholdAmount){
			orderPriceInfo.setTotalDeliverySurchargeSaving(toBigDecimal(((BBBOrderPriceInfo)bbbOrderPriceInfo).getDeliverySurchargeTotal() - thresholdAmount));
		}
		} catch (BBBSystemException e) {
			logError("Error getting thresholdAmount from config keys", e);
		} catch (BBBBusinessException e) {
			logError("Error getting thresholdAmount from config keys", e);
		}
		
		//End: LTL-305 pricing web service change for incorporating LTL specific business rules
		double totalShipping = 0.0;
		double totalSurcharge = 0.0;
		BBBShippingPriceInfo bbbShippingPriceInfo = null;
		BBBHardGoodShippingGroup hardGoodShippingGroup = null;
		List<ShippingGroup> shippingList = (List<ShippingGroup>) pOrder.getShippingGroups();
		for (ShippingGroup shippingGroup : shippingList) {
			if (shippingGroup instanceof BBBHardGoodShippingGroup) {
				hardGoodShippingGroup = (BBBHardGoodShippingGroup) shippingGroup;
				bbbShippingPriceInfo = (BBBShippingPriceInfo) hardGoodShippingGroup.getPriceInfo();

				totalShipping += bbbShippingPriceInfo.getAmount();
				totalSurcharge += bbbShippingPriceInfo.getFinalSurcharge();
			}
		}

		/* Set Total Shipping */
		orderPriceInfo.setTotalShipping(toBigDecimal(totalShipping));

		/* Set Surcharge Total */
		if (totalSurcharge > 0.0) {
			orderPriceInfo.setTotalSurcharge(toBigDecimal(totalSurcharge));
		}

		return orderPriceInfo;
	}

	/**
	 * Populates shipping groups for response
	 * 
	 * @param pOrder
	 * @param pPricingRequestInfo
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	private ShippingList populateShippingGroupsInResponse(BBBOrder pOrder, PricingRequestInfo pPricingRequestInfo, Map<String, Object> pParameters) throws BBBSystemException, BBBBusinessException {
		int itemLineNumber = 0;
		String shippingMethod = null;
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup = null;
		pParameters.put(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER, itemLineNumber);

		Map<String, String> shippingGroupRelationMap = (Map<String, String>) pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION);
		List<com.bedbathandbeyond.atg.ShippingGroup> shippingArray = new ArrayList<com.bedbathandbeyond.atg.ShippingGroup>();
		boolean isAnyItemShipped = isPartiallyShipped(pPricingRequestInfo);
		List<ShippingGroup> bbbShippingList = (List<ShippingGroup>) pOrder.getShippingGroups();
		for (ShippingGroup bbbShippingGroup : bbbShippingList) {
			if (bbbShippingGroup instanceof BBBHardGoodShippingGroup) {
				BBBHardGoodShippingGroup bbbHardGoodShippingGroup = (BBBHardGoodShippingGroup) bbbShippingGroup;
				shippingMethod = bbbHardGoodShippingGroup.getShippingMethod();
				shippingGroup = com.bedbathandbeyond.atg.ShippingGroup.Factory.newInstance();

				shippingGroup.setShippingGroupId(shippingGroupRelationMap.get(bbbShippingGroup.getId()));
				/* Set the shipping method */
				shippingGroup.setShippingMethod(shippingMethod);
				/* Populate the shipping address in response */
				shippingGroup.setShippingAddress(populateAddressInResponse(bbbHardGoodShippingGroup.getShippingAddress()));
				/* Populate the item list in response */
				shippingGroup.setItemList(populateItemListInResponse(pOrder, bbbHardGoodShippingGroup, shippingGroup, pParameters, isAnyItemShipped));
				/* Populate the shipping price in response */
				shippingGroup.setShippingPrice(populateShippingPriceInResponse(pOrder, bbbHardGoodShippingGroup, pPricingRequestInfo));

				shippingArray.add(shippingGroup);
			}
		}

		if (shippingArray.size() > 0) {
			ShippingList shippingList = ShippingList.Factory.newInstance();
			shippingList.setShippingGroupArray(shippingArray.toArray(new com.bedbathandbeyond.atg.ShippingGroup[shippingArray.size()]));

			return shippingList;
		} else {
			return null;
		}

	}

	/**
	 * Populates shipping price for response
	 * 
	 * @param pOrder
	 * @param pBbbHardGoodShippingGroup
	 * @param pPricingRequestInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private com.bedbathandbeyond.atg.ShippingPriceInfo populateShippingPriceInResponse(BBBOrder pOrder, BBBHardGoodShippingGroup pBbbHardGoodShippingGroup, PricingRequestInfo pPricingRequestInfo) {
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = com.bedbathandbeyond.atg.ShippingPriceInfo.Factory.newInstance();
		BBBShippingPriceInfo bbbShippingPriceInfo = (BBBShippingPriceInfo) pBbbHardGoodShippingGroup.getPriceInfo();
		
		shippingPriceInfo.setShippingAmount(toBigDecimal(bbbShippingPriceInfo.getFinalShipping()));
		if (bbbShippingPriceInfo.getRawShipping() > 0.0) {
			shippingPriceInfo.setShippingRawAmount(toBigDecimal(bbbShippingPriceInfo.getRawShipping()));
		}
		if (bbbShippingPriceInfo.getFinalSurcharge() > 0.0) {
			shippingPriceInfo.setSurcharge(toBigDecimal(bbbShippingPriceInfo.getFinalSurcharge()));
		}
		GiftWrapCommerceItem giftWrapItem = pBbbHardGoodShippingGroup.getGiftWrapCommerceItem();
		if (giftWrapItem != null) {
			shippingPriceInfo.setGiftWrapFee(toBigDecimal(giftWrapItem.getPriceInfo().getAmount()));
		}
		AdjustmentList adjustmentList = populateShippingAdjustmentListInResponse(pOrder, bbbShippingPriceInfo.getAdjustments());
		if (adjustmentList != null && adjustmentList.sizeOfAdjustmentArray() > 0) {
			shippingPriceInfo.setAdjustmentList(adjustmentList);
		}

		return shippingPriceInfo;
	}

	/**
	 * Populate adjustments for response
	 * 
	 * @param pOrder
	 * @param pAdjustments
	 * @return
	 */
	private AdjustmentList populateShippingAdjustmentListInResponse(BBBOrder pOrder, List<PricingAdjustment> pAdjustments) {
		Adjustment adjustment = null;
		RepositoryItem pricingModel = null;
		List<Adjustment> adjustmentArray = new ArrayList<Adjustment>();
		for (PricingAdjustment bbbAdjustment : pAdjustments) {
			pricingModel = bbbAdjustment.getPricingModel();
			if (pricingModel != null) {
				adjustment = Adjustment.Factory.newInstance();
				adjustment.setAtgPromotionId(pricingModel.getRepositoryId());
				if (null != bbbAdjustment.getCoupon()) {
					adjustment.setCouponCode(bbbAdjustment.getCoupon().getRepositoryId());
				}
				adjustment.setDiscountAmount(toBigDecimal(Math.abs(bbbAdjustment.getAdjustment())));
				adjustment.setPromotionType(String.valueOf(pricingModel.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS)));
				adjustment.setPromotionDescription(String.valueOf(pricingModel.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME)));

				adjustmentArray.add(adjustment);
			}
		}

		if (adjustmentArray.size() > 0) {
			AdjustmentList adjustmentList = AdjustmentList.Factory.newInstance();
			adjustmentList.setAdjustmentArray(adjustmentArray.toArray(new Adjustment[adjustmentArray.size()]));

			return adjustmentList;
		} else {
			return null;
		}
	}
	
	/**
	 * Populates shipping price in Request for TBS
	 * 
	 * @param pOrder
	 * @param pBbbHardGoodShippingGroup
	 * @param storedShippingInfo 
	 * @param pPricingRequestInfo
	 * @return
	 */
	private BBBHardGoodShippingGroup populateZeroTBSShippingPriceInRequest(BBBOrder pOrder, 
			com.bedbathandbeyond.atg.ShippingGroup shippingGroup, 
			BBBHardGoodShippingGroup pBbbHardGoodShippingGroup, Map<String, ShippingPriceInfo> storedShippingInfo) {
		
		
		BBBShippingPriceInfo bbbShippingPriceInfo = (BBBShippingPriceInfo) pBbbHardGoodShippingGroup.getPriceInfo();
		
		bbbShippingPriceInfo.setFinalShipping(0.00);
		bbbShippingPriceInfo.setRawShipping(0.00);
		bbbShippingPriceInfo.setFinalSurcharge(0.00);

		GiftWrapCommerceItem giftWrapItem = pBbbHardGoodShippingGroup.getGiftWrapCommerceItem();
		
		if(giftWrapItem!=null)
		{
			giftWrapItem.getPriceInfo().setAmount(0.00);
		}
		
		storedShippingInfo.put(pBbbHardGoodShippingGroup.getId(), bbbShippingPriceInfo);
		return pBbbHardGoodShippingGroup;
	}
	
	/**
	 * Populates shipping price in Request for TBS
	 * 
	 * @param pOrder
	 * @param pBbbHardGoodShippingGroup
	 * @param storedShippingInfo 
	 * @param pPricingRequestInfo
	 * @return
	 */
	private BBBHardGoodShippingGroup populateTBSShippingPriceInRequest(BBBOrder pOrder, 
			com.bedbathandbeyond.atg.ShippingGroup shippingGroup, 
			BBBHardGoodShippingGroup pBbbHardGoodShippingGroup, Map<String, ShippingPriceInfo> storedShippingInfo) {
		
		
		BBBShippingPriceInfo bbbShippingPriceInfo = (BBBShippingPriceInfo) pBbbHardGoodShippingGroup.getPriceInfo();
		
		bbbShippingPriceInfo.setFinalShipping(shippingGroup.getShippingPrice().getShippingAmount().doubleValue());
		//shippingPriceInfo.setShippingAmount(toBigDecimal(bbbShippingPriceInfo.getFinalShipping()));
		if (shippingGroup.getShippingPrice().getShippingRawAmount().doubleValue() > 0.0) {
			//shippingPriceInfo.setShippingRawAmount(toBigDecimal(bbbShippingPriceInfo.getRawShipping()));
			bbbShippingPriceInfo.setRawShipping(shippingGroup.getShippingPrice().getShippingRawAmount().doubleValue());
		}
		if (shippingGroup.getShippingPrice().getSurcharge().doubleValue() > 0.0) {
			bbbShippingPriceInfo.setFinalSurcharge(shippingGroup.getShippingPrice().getSurcharge().doubleValue());
		}
		
		GiftWrapCommerceItem giftWrapItem = pBbbHardGoodShippingGroup.getGiftWrapCommerceItem();
		//shippingPriceInfo.setGiftWrapFee(toBigDecimal(giftWrapItem.getPriceInfo().getAmount()));
		if(giftWrapItem!=null)
		{
			giftWrapItem.getPriceInfo().setAmount(shippingGroup.getShippingPrice().getGiftWrapFee().doubleValue());
		}
		
		storedShippingInfo.put(pBbbHardGoodShippingGroup.getId(), bbbShippingPriceInfo);
		return pBbbHardGoodShippingGroup;
	}

			/**
	 * Populates items for response
	 * 
	 * @param pOrder
	 * @param pBbbHardGoodShippingGroup
	 * @param pShippingGroup
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	private ItemList populateItemListInResponse(BBBOrder pOrder, BBBHardGoodShippingGroup pBbbHardGoodShippingGroup, com.bedbathandbeyond.atg.ShippingGroup pShippingGroup,
			Map<String, Object> pParameters, boolean isAnyItemShipped) throws BBBSystemException, BBBBusinessException {
		int lineNumber = 0;
		
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = null;
		if (pParameters != null) {
			sgciRelationMap = (Map<ShippingGroupCommerceItemRelationship, Item>) pParameters.get(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM);
			if (sgciRelationMap == null) {
				sgciRelationMap = new HashMap<ShippingGroupCommerceItemRelationship, Item>();
			}

			lineNumber = (Integer) pParameters.get(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER);
		}

		Item item = null;
		Item requestItem = null;
		BBBCommerceItem bbbCommerceItem = null;
		//Start: LTL-305 pricing web service change for incorporating LTL specific business rules
		String deliveryItemId = "";
		String assemblyItemId = "";
		LTLDeliveryChargeCommerceItem deliveryCommerceItem = null;
		LTLAssemblyFeeCommerceItem assemblyCommerceItem = null;
		List<DetailedItemPriceInfo> priceBeans = null;
		//End: LTL-305 pricing web service change for incorporating LTL specific business rules
		List<Item> itemArray = new ArrayList<Item>();
		List<ShippingGroupCommerceItemRelationship> commerceItemRelationships = pBbbHardGoodShippingGroup.getCommerceItemRelationships();
		for (ShippingGroupCommerceItemRelationship commerceItemRelation : commerceItemRelationships) {
			if (commerceItemRelation.getCommerceItem() instanceof BBBCommerceItem) {
				bbbCommerceItem = (BBBCommerceItem) commerceItemRelation.getCommerceItem();
				requestItem = sgciRelationMap.get(commerceItemRelation);
				boolean lineNumberPopulated = false;

				/* Process item-level promotions */
				priceBeans = bbbCommerceItem.getPriceInfo().getCurrentPriceDetailsForRange(commerceItemRelation.getRange());
				
				double deliveryChargeForCommItem = 0.0;
				if(bbbCommerceItem.isLtlItem()){
					deliveryItemId = bbbCommerceItem.getDeliveryItemId();
					assemblyItemId = bbbCommerceItem.getAssemblyItemId();
					try {
						if(BBBUtility.isNotEmpty(assemblyItemId)){
							assemblyCommerceItem = (LTLAssemblyFeeCommerceItem) pOrder.getCommerceItem(assemblyItemId);
						}
						deliveryCommerceItem = (LTLDeliveryChargeCommerceItem) pOrder.getCommerceItem(deliveryItemId);
						deliveryChargeForCommItem = deliveryCommerceItem.getPriceInfo().getAmount();
					} catch (CommerceItemNotFoundException e) {
						throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_CITEM_NOT_FOOUND,e.getMessage(), e);
					} catch (InvalidParameterException e) {
						throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_INVALID_PARAM,e.getMessage(), e);
					}
				}
				if (priceBeans != null && priceBeans.size() > 0) {
					long shippingGroupItemsQuantity = 0;
					for (DetailedItemPriceInfo unitPriceBean : priceBeans) {
						shippingGroupItemsQuantity += unitPriceBean.getQuantity();
					}
					double itemSurcharge = getPricingTools().calculateItemSurchargeInSG(pOrder.getSiteId(), commerceItemRelation);
					List<Item> ltlItemArray = new ArrayList<Item>();
					for (DetailedItemPriceInfo promotionPriceBean : priceBeans) {
						item = Item.Factory.newInstance();
						lineNumber++;
						
						
						if (requestItem != null) {
							item.setOriginalLineNumber(requestItem.getOriginalLineNumber());
							item.setStatus(requestItem.getStatus());
							item.setIsPcInd(requestItem.getIsPcInd());
							item.setPcInfo(requestItem.getPcInfo());
							if(!lineNumberPopulated){
								item.setLineNumber(requestItem.getLineNumber());
							}else{
								item.setLineNumber("");
							}
						} else {
							item.setOriginalLineNumber(String.valueOf(lineNumber));
							if(!lineNumberPopulated){
								item.setLineNumber(String.valueOf(lineNumber)); 
							}else{
								item.setLineNumber("");
							}
						}
						
						lineNumberPopulated = true;
						
						pParameters.put(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER, lineNumber);
						item.setSku(bbbCommerceItem.getCatalogRefId());
						item.setQuantity(BigInteger.valueOf(promotionPriceBean.getQuantity()));

						item.setSurcharge(toBigDecimal(itemSurcharge));

						if (!StringUtils.isBlank(bbbCommerceItem.getRegistryId())) {
							item.setRegistryId(bbbCommerceItem.getRegistryId());
						}
						AdjustmentList adjustmentList = populateItemAdjustmentListInResponse(promotionPriceBean, bbbCommerceItem, pParameters);
						if (adjustmentList != null && adjustmentList.sizeOfAdjustmentArray() > 0) {
							item.setAdjustmentList(adjustmentList);
						}
						item.setPrice(toBigDecimal(bbbCommerceItem.getPriceInfo().getListPrice()));
						item.setFinalPrice(toBigDecimal(promotionPriceBean.getDetailedUnitPrice()));

						/* Eco-fee calculation */
						try {
							Map<String, String> ecoFeeMap = pBbbHardGoodShippingGroup.getEcoFeeItemMap();
							if (ecoFeeMap != null) {
								String ecoFeeItemId = ecoFeeMap.get(bbbCommerceItem.getId());
								if (!StringUtils.isBlank(ecoFeeItemId)) {

									CommerceItem ecoFeeCommerceItem = pOrder.getCommerceItem(ecoFeeItemId);
									if (ecoFeeCommerceItem != null && ecoFeeCommerceItem.getPriceInfo() != null) {
										double ecoFeeRatio = promotionPriceBean.getQuantity() / shippingGroupItemsQuantity;
										//item.setEcoFee(toBigDecimal(ecoFeeCommerceItem.getPriceInfo().getAmount() * ecoFeeRatio));
										item.setEcoFee(toBigDecimal(ecoFeeCommerceItem.getPriceInfo().getListPrice()));
										item.setEcoFeeSku(ecoFeeCommerceItem.getCatalogRefId());
									}
								}
							}
						} catch (CommerceItemNotFoundException e) {
							throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_CITEM_NOT_FOOUND,e.getMessage(), e);
						} catch (InvalidParameterException e) {
							throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_INVALID_PARAM,e.getMessage(), e);
						}
						//Start: LTL-305 pricing web service change for incorporating LTL specific business rules
						if(bbbCommerceItem.isLtlItem()) {
							if(isAnyItemShipped){
								if(requestItem.getDeliverySurcharge()!=null)
									item.setDeliverySurcharge(requestItem.getDeliverySurcharge());
								if(requestItem.getDeliverySurchargePerQty()!=null)
									item.setDeliverySurchargePerQty(requestItem.getDeliverySurchargePerQty());
								if(requestItem.getDeliverySurchargeSaving()!=null)
									item.setDeliverySurchargeSaving(requestItem.getDeliverySurchargeSaving());
								if(requestItem.getDeliverySurchargeSku()!=null)
									item.setDeliverySurchargeSku(requestItem.getDeliverySurchargeSku());
								if(BBBUtility.isNotEmpty(assemblyItemId)){
									if(requestItem.getAssemblyFee()!=null)
										item.setAssemblyFee(requestItem.getAssemblyFee());
									if(requestItem.getAssemblyFeePerQty()!=null)
										item.setAssemblyFeePerQty(requestItem.getAssemblyFeePerQty());
									if(requestItem.getAssemblyFeeSku()!=null)
										item.setAssemblyFeeSku(requestItem.getAssemblyFeeSku());
								}
							} else{
								populateLTLItemInfo(item, bbbCommerceItem,
										assemblyItemId, itemArray, priceBeans,
										deliveryCommerceItem, assemblyCommerceItem,
										deliveryChargeForCommItem, ltlItemArray,
										promotionPriceBean);
							}
						}
						//End: LTL-305 pricing web service change for incorporating LTL specific business rules
						itemArray.add(item);
					}
					if(!ltlItemArray.isEmpty()){
						populateLTLItemInforForPromotion(bbbCommerceItem,
								assemblyItemId, deliveryCommerceItem,
								assemblyCommerceItem,
								deliveryChargeForCommItem, ltlItemArray);
					}
				}
			}
		}
		if (itemArray.size() > 0) {
			ItemList itemList = ItemList.Factory.newInstance();
			itemList.setItemArray(itemArray.toArray(new Item[itemArray.size()]));

			return itemList;
		} else {
			return null;
		}
	}

	/**
	 * This method is used to adjust .01 dollar difference in a item with lowest quantity when size of pricebean is gt 1.
	 * @param bbbCommerceItem
	 * @param assemblyItemId
	 * @param deliveryCommerceItem
	 * @param assemblyCommerceItem
	 * @param deliveryChargeForCommItem
	 * @param ltlItemArray
	 * @param counter
	 * @param totalAmount
	 */
	private void populateLTLItemInforForPromotion(
			BBBCommerceItem bbbCommerceItem, String assemblyItemId,
			LTLDeliveryChargeCommerceItem deliveryCommerceItem,
			LTLAssemblyFeeCommerceItem assemblyCommerceItem,
			double deliveryChargeForCommItem, List<Item> ltlItemArray) {
		double totalAmount = deliveryCommerceItem.getPriceInfo().getAmount();
		int counter = 0;
		double delChargePerQty = 0L;
		double delChargeForPriceBean = 0L;
		Collections.sort(ltlItemArray,new LtlPricingWSItemQuantityComparator());
		for(Item tempItem : ltlItemArray){
			counter++;
			if(counter==ltlItemArray.size()){
				delChargePerQty = totalAmount;
				delChargeForPriceBean = totalAmount;
				tempItem.setDeliverySurchargePerQty(toBigDecimal(delChargePerQty));
				tempItem.setDeliverySurcharge(toBigDecimal(delChargeForPriceBean));
				tempItem.setDeliverySurchargeSku(deliveryCommerceItem.getCatalogRefId());
				double surchargeSavingForPriceBean = (deliveryCommerceItem.getPriceInfo().getRawTotalPrice()/bbbCommerceItem.getQuantity())* (tempItem.getQuantity().intValue())
						- delChargeForPriceBean;
				tempItem.setDeliverySurchargeSaving(toBigDecimal(surchargeSavingForPriceBean));
				if(BBBUtility.isNotEmpty(assemblyItemId)){
					tempItem.setIsAssemblyRequested(true);
					tempItem.setAssemblyFeePerQty(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice()));
					tempItem.setAssemblyFee(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice() * tempItem.getQuantity().intValue()));
					tempItem.setAssemblyFeeSku(assemblyCommerceItem.getCatalogRefId());
				}
			} else{
				delChargePerQty = getPricingTools().round(deliveryCommerceItem.getPriceInfo().getAmount() / bbbCommerceItem.getQuantity());
				delChargeForPriceBean = ((this.getPricingTools().round(deliveryChargeForCommItem / bbbCommerceItem.getQuantity()))) * (tempItem.getQuantity().intValue());
				totalAmount = totalAmount - delChargeForPriceBean;
				tempItem.setDeliverySurchargePerQty(toBigDecimal(delChargePerQty));
				tempItem.setDeliverySurcharge(toBigDecimal(delChargeForPriceBean));
				tempItem.setDeliverySurchargeSku(deliveryCommerceItem.getCatalogRefId());
				double surchargeSavingForPriceBean = (deliveryCommerceItem.getPriceInfo().getRawTotalPrice()/bbbCommerceItem.getQuantity())* (tempItem.getQuantity().intValue())
						- delChargeForPriceBean;
				tempItem.setDeliverySurchargeSaving(toBigDecimal(surchargeSavingForPriceBean));
				if(BBBUtility.isNotEmpty(assemblyItemId)){
					tempItem.setIsAssemblyRequested(true);
					tempItem.setAssemblyFeePerQty(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice()));
					tempItem.setAssemblyFee(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice() * tempItem.getQuantity().intValue()));
					tempItem.setAssemblyFeeSku(assemblyCommerceItem.getCatalogRefId());
				}
			}
		}
	}

	/**
	 * This method is used to populate LTL specific details in pricing ws response.
	 * 100/3 = 33.3  33.3*3=99.99 adjust .01 difference in a separate item.
	 * @param item
	 * @param bbbCommerceItem
	 * @param assemblyItemId
	 * @param itemArray
	 * @param priceBeans
	 * @param deliveryCommerceItem
	 * @param assemblyCommerceItem
	 * @param deliveryChargeForCommItem
	 * @param ltlItemArray
	 * @param promotionPriceBean
	 */
	private void populateLTLItemInfo(Item item,
			BBBCommerceItem bbbCommerceItem, String assemblyItemId,
			List<Item> itemArray, List<DetailedItemPriceInfo> priceBeans,
			LTLDeliveryChargeCommerceItem deliveryCommerceItem,
			LTLAssemblyFeeCommerceItem assemblyCommerceItem,
			double deliveryChargeForCommItem, List<Item> ltlItemArray,
			DetailedItemPriceInfo promotionPriceBean) {
		item.setIsLTLSku(true);
		if(priceBeans.size()>1){
			ltlItemArray.add(item);
		}
		else{
			double delChargePerQty = getPricingTools().round(deliveryCommerceItem.getPriceInfo().getAmount() / bbbCommerceItem.getQuantity());
			double delChargeForPriceBean = delChargePerQty * promotionPriceBean.getQuantity();
			if (delChargeForPriceBean != deliveryChargeForCommItem) {
				Item item2 = Item.Factory.newInstance();
				item2.setIsLTLSku(true);
				item2.setDeliverySurchargePerQty(toBigDecimal(deliveryChargeForCommItem - (delChargePerQty * (promotionPriceBean.getQuantity() - 1))));
				item2.setDeliverySurcharge(toBigDecimal(deliveryChargeForCommItem - (delChargePerQty * (promotionPriceBean.getQuantity() - 1))));
				item2.setDeliverySurchargeSku(deliveryCommerceItem.getCatalogRefId());
				double surchargeSavingItem2 = (deliveryCommerceItem.getPriceInfo().getRawTotalPrice()/bbbCommerceItem.getQuantity())
						- (deliveryChargeForCommItem - (delChargePerQty * (promotionPriceBean.getQuantity() - 1)));
				item2.setDeliverySurchargeSaving(toBigDecimal(surchargeSavingItem2));
				if(BBBUtility.isNotEmpty(assemblyItemId)){
					item2.setIsAssemblyRequested(true);
					item2.setAssemblyFeePerQty(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice()));
					item2.setAssemblyFee(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice()));
					item2.setAssemblyFeeSku(assemblyCommerceItem.getCatalogRefId());
				}
				item2.setOriginalLineNumber(item.getOriginalLineNumber());
				item2.setStatus(item.getStatus());
				item2.setLineNumber(item.getLineNumber());
				item2.setSku(item.getSku());
				item2.setQuantity(BigInteger.valueOf(1));
				item2.setPrice(item.getPrice());
				item2.setFinalPrice(item.getFinalPrice());
				itemArray.add(item2);
				item.setQuantity(item.getQuantity().subtract(BigInteger.valueOf(1)));
				item.setLineNumber("");
				item.setDeliverySurchargePerQty(toBigDecimal(delChargePerQty));
				item.setDeliverySurcharge(toBigDecimal(delChargePerQty * (promotionPriceBean.getQuantity() - 1)));
				item.setDeliverySurchargeSku(deliveryCommerceItem.getCatalogRefId());
				double surchargeSavingItem = deliveryCommerceItem.getPriceInfo().getRawTotalPrice() - deliveryCommerceItem.getPriceInfo().getAmount() - surchargeSavingItem2;
				item.setDeliverySurchargeSaving(toBigDecimal(surchargeSavingItem));
				if(BBBUtility.isNotEmpty(assemblyItemId)){
					item.setIsAssemblyRequested(true);
					item.setAssemblyFeePerQty(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice()));
					item.setAssemblyFee(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice() * (promotionPriceBean.getQuantity() - 1)));
					item.setAssemblyFeeSku(assemblyCommerceItem.getCatalogRefId());
				}
			} else {
				double surchargeSavingForPriceBean = this.getPricingTools().round((deliveryCommerceItem.getPriceInfo().getRawTotalPrice() - deliveryCommerceItem.getPriceInfo().getAmount()) / bbbCommerceItem.getQuantity()) * promotionPriceBean.getQuantity();
				item.setDeliverySurchargePerQty(toBigDecimal(delChargePerQty));
				item.setDeliverySurcharge(toBigDecimal(delChargePerQty * promotionPriceBean.getQuantity()));
				item.setDeliverySurchargeSku(deliveryCommerceItem.getCatalogRefId());
				item.setDeliverySurchargeSaving(toBigDecimal(surchargeSavingForPriceBean));
				if(BBBUtility.isNotEmpty(assemblyItemId)){
					item.setIsAssemblyRequested(true);
					item.setAssemblyFeePerQty(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice()));
					item.setAssemblyFee(toBigDecimal(assemblyCommerceItem.getPriceInfo().getListPrice() * promotionPriceBean.getQuantity()));
					item.setAssemblyFeeSku(assemblyCommerceItem.getCatalogRefId());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private AdjustmentList populateItemAdjustmentListInResponse(DetailedItemPriceInfo promotionPriceBean, CommerceItem pCommerceItem, Map<String, Object> pParameters) {
		/* OrderPriceInfo.adjustmentList.adjustment details */
		RepositoryItem promotionItem = null;
		Adjustment adjustment = null;
		List<Adjustment> adjustmentArray = new ArrayList<Adjustment>();
		boolean partiallyShipped = (Boolean) pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED);
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = (Map<ShippingGroupCommerceItemRelationship, Item>) pParameters.get(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM);
		
		boolean pricingModelFlag = isPromotionApplied(promotionPriceBean.getAdjustments());
		if (pricingModelFlag){
			
			List<PricingAdjustment> adjustments = promotionPriceBean.getAdjustments();
			for(PricingAdjustment pricingAdj:adjustments){
				if(null != pricingAdj.getPricingModel()){
					adjustment = Adjustment.Factory.newInstance();
					promotionItem = pricingAdj.getPricingModel();
					adjustment.setAtgPromotionId(promotionItem.getRepositoryId());
					if (null != pricingAdj.getCoupon()) {
						adjustment.setCouponCode(pricingAdj.getCoupon().getRepositoryId());
					}
					//ItemPriceInfo pItemPriceInfo = pCommerceItem.getPriceInfo();
					adjustment.setDiscountAmount(toBigDecimal(Math.abs(pricingAdj.getAdjustment())));
					adjustment.setPromotionType(String.valueOf(promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS)));
					adjustment.setPromotionDescription(String.valueOf(promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME)));

					adjustmentArray.add(adjustment);
				}
			}
			
		} else if (partiallyShipped && itemInfoMap != null && itemInfoMap.values() != null) {
			for (Item item : itemInfoMap.values()) {
				if (pCommerceItem.getCatalogRefId().equals(item.getSku()) && item.getAdjustmentList() != null && item.getAdjustmentList().sizeOfAdjustmentArray() > 0) {
					for (Adjustment adj : item.getAdjustmentList().getAdjustmentArray()) {
						adjustment = Adjustment.Factory.newInstance();

						if (adj.getAtgPromotionId() != null) {
							adjustment.setAtgPromotionId(adj.getAtgPromotionId());
						}
						if (adj.getCouponCode() != null) {
							adjustment.setCouponCode(adj.getCouponCode());
						}
						adjustment.setDiscountAmount(adj.getDiscountAmount());
						adjustment.setPromotionType(adj.getPromotionType());
						adjustment.setPromotionDescription(adj.getPromotionDescription());

						adjustmentArray.add(adjustment);
					}
				}
			}
		}

		if (adjustmentArray.size() > 0) {
			AdjustmentList adjustmentList = AdjustmentList.Factory.newInstance();
			adjustmentList.setAdjustmentArray(adjustmentArray.toArray(new Adjustment[adjustmentArray.size()]));

			return adjustmentList;
		} else {
			return null;
		}
	}
	
	private boolean isPromotionApplied(List<PricingAdjustment> pricingAdjustmentList) {

		if (null != pricingAdjustmentList && !pricingAdjustmentList.isEmpty()) {
			PricingAdjustment adj = null;
			for (int j = 0; j < pricingAdjustmentList.size(); j++) {
				adj = (PricingAdjustment) pricingAdjustmentList.get(j);
				if (adj.getPricingModel() != null)
					return true;
			}
		}
		return false;
	}

	/**
	 * Populates address for response
	 * 
	 * @param pAddress
	 * @return
	 */
	private com.bedbathandbeyond.atg.Address populateAddressInResponse(Address pAddress) {
		com.bedbathandbeyond.atg.Address address = com.bedbathandbeyond.atg.Address.Factory.newInstance();

		address.setAddressLine1(pAddress.getAddress1());
		if (!StringUtils.isBlank(pAddress.getAddress2())) {
			address.setAddressLine2(pAddress.getAddress2());
		}
		address.setCity(pAddress.getCity());
		address.setState(pAddress.getState());
		address.setZipCode(pAddress.getPostalCode());
		address.setCountry(pAddress.getCountry());

		return address;
	}

	/**
	 * Converts & rounds value to Double object
	 * 
	 * @param pValue
	 * @return
	 */
	/*
	 * private Double toDouble(double pValue) { return
	 * Double.valueOf(getPricingTools().round(pValue, 2)); }
	 */

	/**
	 * Converts & rounds value to BigDecimal object
	 * 
	 * @param pValue
	 * @return
	 */
	private BigDecimal toBigDecimal(double pValue) {
		return BigDecimal.valueOf(getPricingTools().round(pValue, 2));
	}

	/**
	 * @return the orderManager
	 */
	public final BBBOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager
	 *            the orderManager to set
	 */
	public final void setOrderManager(BBBOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * @return the shippingGroupManager
	 */
	public final BBBShippingGroupManager getShippingGroupManager() {
		return mShippingGroupManager;
	}

	/**
	 * @param pShippingGroupManager
	 *            the shippingGroupManager to set
	 */
	public final void setShippingGroupManager(BBBShippingGroupManager pShippingGroupManager) {
		mShippingGroupManager = pShippingGroupManager;
	}

	/**
	 * @return the pricingTools
	 */
	public final BBBPricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * @param pPricingTools
	 *            the pricingTools to set
	 */
	public final void setPricingTools(BBBPricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}

	/**
	 * @return the promotionsRepository
	 */
	public final Repository getPromotionsRepository() {
		return mPromotionsRepository;
	}

	/**
	 * @param pPromotionsRepository
	 *            the promotionsRepository to set
	 */
	public final void setPromotionRepository(Repository pPromotionsRepository) {
		mPromotionsRepository = pPromotionsRepository;
	}

	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogToolsImpl getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public final void setCatalogTools(BBBCatalogToolsImpl pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the commerceItemManager
	 */
	public final BBBCommerceItemManager getCommerceItemManager() {
		return mCommerceItemManager;
	}

	/**
	 * @param pCommerceItemManager
	 *            the commerceItemManager to set
	 */
	public final void setCommerceItemManager(BBBCommerceItemManager pCommerceItemManager) {
		mCommerceItemManager = pCommerceItemManager;
	}

	/**
	 * @return the profileTools
	 */
	public final ProfileTools getProfileTools() {
		return mProfileTools;
	}

	/**
	 * @param profileTools
	 *            the profileTools to set
	 */
	public final void setProfileTools(ProfileTools profileTools) {
		mProfileTools = profileTools;
	}

	/**
	 * @return the siteRepository
	 */
	public Repository getSiteRepository() {
		return siteRepository;
	}

	/**
	 * @param siteRepository
	 *            the siteRepository to set
	 */
	public void setSiteRepository(final Repository siteRepository) {
		this.siteRepository = siteRepository;
	}
	
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public List<String> getShippingMethodList() {
		return shippingMethodList;
	}

	public void setShippingMethodList(List<String> shippingMethodList) {
		this.shippingMethodList = shippingMethodList;
	}

	public BBBOrderTools getOrderTools() {
		return orderTools;
	}

	public void setOrderTools(BBBOrderTools orderTools) {
		this.orderTools = orderTools;
	}

/**
 * update shipping charges according intl shipping flag configure in BCC 
 * @param order
 */
	public void  updateISShippingCharges(Order order)  {
		List<String> shippingList = null;
		for(final String shippingMethod : this.getShippingMethodList()) {			
			try {
				shippingList = this.getCatalogTools().getAllValuesForKey(
						BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, shippingMethod);
			} catch (BBBSystemException e) {
				logError("Error getting INTERNATIONAL_SHIPPING from config keys", e);
			} catch (BBBBusinessException e) {
				logError("Error getting INTERNATIONAL_SHIPPING from config keys", e);
			}
			if(!BBBUtility.isListEmpty(shippingList)){
				if(shippingMethod.equals(BBBInternationalShippingConstants.CONFIG_KEY_SHIPPING_WITHOUT_PROMOTION) && shippingList.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
					logDebug(" removing all shipping promotion for user as flag set is Shipping_Charge_Without_Promotion ");
					HardgoodShippingGroup hd =(HardgoodShippingGroup)	order.getShippingGroups().get(0);
					final BBBShippingPriceInfo priceInfo = (BBBShippingPriceInfo) hd.getPriceInfo();
					List<PricingAdjustment> shipAdjustments= priceInfo.getAdjustments();
					shipAdjustments.clear();
					priceInfo.setDiscounted(false);
					priceInfo.setAmountIsFinal(true);
					try{
						double actualShipping=getPricingTools().calculateShippingCost(order.getSiteId(), hd.getShippingMethod(), hd, null);
						logDebug(" Final shipping applicable to user is "+actualShipping);
						priceInfo.setAmount(actualShipping); 
						priceInfo.setFinalShipping(actualShipping);
						BBBOrderPriceInfo pinfo =(BBBOrderPriceInfo)order.getPriceInfo();
						pinfo.setShipping(actualShipping);
					} catch (PricingException e) {
						logError("Exception While calculating Shipping Price For International Orders", e);
					}
					break;
				} else if (shippingMethod.equals(BBBInternationalShippingConstants.CONFIG_KEY_SHIPPING_WITH_PROMOTION) && shippingList.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
					logDebug(" using all shipping promotion for user as flag set is Shipping_Charge_With_Promotion ");
					break;
				} else if (shippingMethod.equals(BBBInternationalShippingConstants.CONFIG_KEY_SHIPPING_ZERO) && shippingList.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
					logDebug(" applying zero shipping charge for user as flag set is Shipping_Charge_Zero ");
					HardgoodShippingGroup hd =(HardgoodShippingGroup)	order.getShippingGroups().get(0);
					BBBShippingPriceInfo priceInfo=(BBBShippingPriceInfo)hd.getPriceInfo();
					
					List<PricingAdjustment> shipAdjustments= priceInfo.getAdjustments();
					shipAdjustments.clear();
					priceInfo.setDiscounted(false);
					priceInfo.setAmountIsFinal(true);
					
					priceInfo.setAmount(0.00);
					priceInfo.setRawShipping(0.00);
					priceInfo.setFinalShipping(0.00);
					priceInfo.setFinalSurcharge(0.00);

					BBBOrderPriceInfo pinfo =(BBBOrderPriceInfo)order.getPriceInfo();
					pinfo.setShipping(0.00);
					break;
				}
			}
		}
	}

}