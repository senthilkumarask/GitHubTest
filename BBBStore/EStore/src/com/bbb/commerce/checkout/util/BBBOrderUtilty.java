package com.bbb.commerce.checkout.util;

import java.beans.IntrospectionException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.AmountInfo;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.UnitPriceBean;
import atg.core.util.Address;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.RepositoryItem;
import atg.userprofiling.address.AddressTools;

import com.bbb.account.vo.order.ShipmentTrackingInfoVO;
import com.bbb.cms.RecordCountPropertyDescriptor;
import com.bbb.commerce.cart.bean.CommerceItemDisplayVO;
import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.PriceBeanDisplayVO;
import com.bbb.commerce.common.PriceInfoDisplayVO;
import com.bbb.commerce.common.ShippingGroupDisplayVO;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.pricing.bean.PaymentGroupDisplayVO;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.TrackingInfo;
import com.bbb.utils.BBBUtility;

public class BBBOrderUtilty {
	
	private static ApplicationLogging mLogger =
			ClassLoggingFactory.getFactory().getLoggerForClass(RecordCountPropertyDescriptor.class);

	/**
	 * @return ApplicationLogging object for logger.
	 */
	private static ApplicationLogging getLogger()  {
		return mLogger;
	}

	/**
	 * 
	 * Populates ShippingGrp Info.
	 * 
	 * @param sg
	 * @return ShippingGroupDisplayVO
	 */
	public static ShippingGroupDisplayVO populateShippingGrpInfo(ShippingGroupImpl sg) {
		
		ShippingGroupDisplayVO shipGroupVO = new ShippingGroupDisplayVO();
		
		if(null!= sg){			
			if (sg instanceof HardgoodShippingGroup) {
				BBBHardGoodShippingGroup bbbShippingGroup = (BBBHardGoodShippingGroup) sg;
				shipGroupVO.setContainsGiftWrap(bbbShippingGroup.isContainsGiftWrap());
				
				if(null != bbbShippingGroup.getShipOnDate()){
					DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
					String pHoldDate = formatter.format(bbbShippingGroup.getShipOnDate());  
					shipGroupVO.setPackAndHoldDate(pHoldDate);
				}
				shipGroupVO.setContainsGiftWrapMessage(bbbShippingGroup.getContainsGiftWrapMessage());
				shipGroupVO.setGiftWrapMessage(bbbShippingGroup.getGiftWrapMessage());
				shipGroupVO.setEcoFeeItemMap(shipGroupVO.getEcoFeeItemMap());
				Address shippingAddress =  bbbShippingGroup.getShippingAddress();
				Address shippingAddressCopy = new BBBAddressImpl();
				if(shippingAddress != null && shippingAddress.getAddress1() != null) {
					try {
						AddressTools.copyAddress(shippingAddress, shippingAddressCopy);
					} catch (IntrospectionException e) {
						getLogger().logDebug("**********Error copying shipping address**********");
					}
				}
				shipGroupVO.setShippingAddress(shippingAddressCopy);
				shipGroupVO.setEmail(bbbShippingGroup.getShippingConfirmationEmail());
				shipGroupVO.setSourceId(bbbShippingGroup.getSourceId());
				if(shippingAddress instanceof BBBRepositoryContactInfo){
					final BBBRepositoryContactInfo contactInfo = (BBBRepositoryContactInfo) shippingAddress;
					shipGroupVO.setAlternatePhoneNumber(contactInfo.getAlternatePhoneNumber());
					shipGroupVO.setPhoneNumber(contactInfo.getPhoneNumber());
					shipGroupVO.setMobilePhoneNumber(contactInfo.getMobileNumber());
					shipGroupVO.setLtlEmail(contactInfo.getEmail());
				}
				if(null != bbbShippingGroup.getTrackingInfos() && !bbbShippingGroup.getTrackingInfos().isEmpty()){
					shipGroupVO.setTrackingInfoVOList(getTrackingInfoVOList(bbbShippingGroup.getTrackingInfos()));
				}
				
			} else {
				shipGroupVO.setStoreId(((BBBStoreShippingGroup) sg).getStoreId());
				shipGroupVO.setEcoFeeItemMap(((BBBStoreShippingGroup) sg).getEcoFeeItemMap());
			}
			
			shipGroupVO.setCommerceItemRelationshipCount(String.valueOf(sg.getCommerceItemRelationshipCount()));
			shipGroupVO.setId(sg.getId());
			shipGroupVO.setShippingGroupType(sg.getShippingGroupClassType());
			shipGroupVO.setShippingMethod(sg.getShippingMethod());
			
		}
		
		
		return shipGroupVO;
	}
	
	/*
	 * To get Shipment tracking VO.
	 */
	private static List<ShipmentTrackingInfoVO> getTrackingInfoVOList(Set<TrackingInfo> trackingInfos) {

		List<ShipmentTrackingInfoVO> shipmentList = new ArrayList<ShipmentTrackingInfoVO>();
		Iterator<TrackingInfo> iterateTrackingInfo = trackingInfos.iterator();

		while (iterateTrackingInfo.hasNext()) {
			TrackingInfo trackingInfoObj = iterateTrackingInfo.next();
			ShipmentTrackingInfoVO shipment = new ShipmentTrackingInfoVO();
			shipment.setCarrier(trackingInfoObj.getCarrierCode());
			shipment.setTrackingNumber(trackingInfoObj.getTrackingNumber());
			shipmentList.add(shipment);
		}

		return shipmentList;
	}

	/**
	 * populate price info
	 * 
	 * @param priceInfoVO
	 * @param priceInfo 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static PriceInfoDisplayVO populatePriceInfo(PriceInfoVO priceInfoVO, String siteId,BBBOrderImpl order, AmountInfo priceInfo) {
		
		PriceInfoDisplayVO priceInfoDisplayVO = new PriceInfoDisplayVO();
		
		
		if (null != priceInfoVO) {
			try{
			priceInfoDisplayVO.setShippingGroupItemsTotal(convertToTwoDecimals(priceInfoVO.getShippingGroupItemsTotal()));
			priceInfoDisplayVO.setShippingGroupItemTotal(convertToTwoDecimals(priceInfoVO.getShippingGroupItemTotal()));
			priceInfoDisplayVO.setUndiscountedItemsCount(priceInfoVO.getUndiscountedItemsCount());
			priceInfoDisplayVO.setItemCount(priceInfoVO.getItemCount());
			priceInfoDisplayVO.setTotalShippingAmount(convertToTwoDecimals(priceInfoVO.getTotalShippingAmount()));
			priceInfoDisplayVO.setEcoFeeTotal(convertToTwoDecimals(priceInfoVO.getEcoFeeTotal()));
			priceInfoDisplayVO.setTotalSavedPercentage(priceInfoVO.getTotalSavedPercentage());
			priceInfoDisplayVO.setShippingLevelTax(convertToTwoDecimals(priceInfoVO.getShippingLevelTax()));
			priceInfoDisplayVO.setStoreAmount(convertToTwoDecimals(priceInfoVO.getStoreAmount()));
			priceInfoDisplayVO.setStoreEcoFeeTotal(convertToTwoDecimals(priceInfoVO.getStoreEcoFeeTotal()));
			priceInfoDisplayVO.setOnlinePurchaseTotal(convertToTwoDecimals(priceInfoVO.getOnlinePurchaseTotal()));
			priceInfoDisplayVO.setOnlineRawTotal(convertToTwoDecimals(priceInfoVO.getOnlineRawTotal()));
			priceInfoDisplayVO.setPromoDiscountAmount(convertToTwoDecimals(priceInfoVO.getPromoDiscountAmount()));
			priceInfoDisplayVO.setOnlineEcoFeeTotal(convertToTwoDecimals(priceInfoVO.getOnlineEcoFeeTotal()));
			priceInfoDisplayVO.setGiftWrapTotal(convertToTwoDecimals(priceInfoVO.getGiftWrapTotal()));
			priceInfoDisplayVO.setRawShippingTotal(convertToTwoDecimals(priceInfoVO.getRawShippingTotal()));
			priceInfoDisplayVO.setTotalSurcharge(convertToTwoDecimals(priceInfoVO.getTotalSurcharge()));
			priceInfoDisplayVO.setTotalTax(convertToTwoDecimals(priceInfoVO.getTotalTax()));
			priceInfoDisplayVO.setOnlineTotal(convertToTwoDecimals(priceInfoVO.getOnlineTotal()));
			priceInfoDisplayVO.setOrderPreTaxAmount(convertToTwoDecimals(priceInfoVO.getOrderPreTaxAmout()));
			priceInfoDisplayVO.setTotalAmount(convertToTwoDecimals(priceInfoVO.getTotalAmount()));
			priceInfoDisplayVO.setFreeShipping(priceInfoVO.getFreeShipping());
			priceInfoDisplayVO.setHardgoodShippingGroupItemCount(priceInfoVO.getHardgoodShippingGroupItemCount());
			priceInfoDisplayVO.setStorePickupShippingGroupItemCount(priceInfoVO.getStorePickupShippingGroupItemCount());
			priceInfoDisplayVO.setTotalSavedAmount(convertToTwoDecimals(priceInfoVO.getTotalSavedAmount()));
			priceInfoDisplayVO.setFinalShippingCharge(convertToTwoDecimals(priceInfoVO.getFinalShippingCharge()));
			
			}catch(NumberFormatException nfe){
				getLogger().logError("**********Price Info**********" + priceInfoVO.toString());
				
				throw nfe;
			}
			priceInfoDisplayVO.setSurchargeSavings(convertToTwoDecimals(priceInfoVO.getSurchargeSavings()));
			priceInfoDisplayVO.setShippingSavings(convertToTwoDecimals(priceInfoVO.getShippingSavings()));
			
			//RM Defect 19688
			if(!(StringUtils.isEmpty(siteId)) && (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) ||
												  siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) ){
				double stateLeveltax = priceInfoVO.getShippingStateLevelTax();
				double countyLevelTax = priceInfoVO.getShippingCountyLevelTax();
						
				if(Double.compare(stateLeveltax, 0.0) == 0 && Double.compare(countyLevelTax, 0.0) != 0){
					priceInfoVO.setShippingStateLevelTax(countyLevelTax);
					priceInfoVO.setShippingCountyLevelTax(stateLeveltax);
				} else {
					priceInfoVO.setShippingStateLevelTax(stateLeveltax);
					priceInfoVO.setShippingCountyLevelTax(countyLevelTax);
				}
			}
			priceInfoDisplayVO.setShippingStateLevelTax(convertToTwoDecimals(priceInfoVO.getShippingStateLevelTax()));
			priceInfoDisplayVO.setShippingCountyLevelTax(convertToTwoDecimals(priceInfoVO.getShippingCountyLevelTax()));
			priceInfoDisplayVO.setPrevPrice(priceInfoVO.getPrevPrice());
			Map<String, List<PriceBeanDisplayVO>> promoDisplayVOMap = new HashMap<String ,List<PriceBeanDisplayVO>>();
			int promoCount=0;
			if (null != priceInfoVO.getPriceBeans() && !priceInfoVO.getPriceBeans().isEmpty()) {
				/*
				 * BBBH-5500|My Offer Automation |Cart Page Changes starts
				 * Following code is for displaying promotions in mobile cart
				 * page.
				 */
				if (null != priceInfo && priceInfo instanceof ItemPriceInfo) {
					List<DetailedItemPriceInfo> dpiList = ((ItemPriceInfo) priceInfo).getCurrentPriceDetails();
					List<DetailedItemPriceInfo> tempDpiList = new LinkedList<DetailedItemPriceInfo>(dpiList);
					for (DetailedItemPriceInfo dpi : tempDpiList) {
						List<PricingAdjustment> adjustmentList = dpi.getAdjustments();
						for (PricingAdjustment adjustment : adjustmentList) {
							if (null == adjustment) {
							  if(getLogger().isLoggingDebug()){
								getLogger().logDebug(
										"BBBOrderUtilty.populatePriceInfo: Adjustment is null for item priceinfo "
												+ dpi.getItemPriceInfo());
							}
								continue;
							}
							if (null != adjustment.getPricingModel()) {
								String promoCouponKey = adjustment.getPricingModel().getRepositoryId();
								if (null != adjustment.getCoupon()) {
									if(getLogger().isLoggingDebug()){
									getLogger().logDebug(
											"BBBOrderUtilty.populatePriceInfo: Coupon " + adjustment.getCoupon()
													+ " present for promotion " + adjustment.getPricingModel());
									}
									promoCouponKey += adjustment.getCoupon().getRepositoryId();
								}
								String promotionDisplayName = adjustment.getPricingModel().getItemDisplayName();
								PriceBeanDisplayVO priceBeanVO = new PriceBeanDisplayVO();
								priceBeanVO.setQuantity(dpi.getQuantity());
								priceBeanVO.setUnitPrice(dpi.getDetailedUnitPrice());
								List<String> promotions = new ArrayList<String>();
								promotions.add(promotionDisplayName);
								priceBeanVO.setPromotionDisplayNameList(promotions);
								if (!promoDisplayVOMap.containsKey(promoCouponKey)) {
									if(getLogger().isLoggingDebug()){
									getLogger()
											.logDebug(
													"BBBOrderUtilty.populatePriceInfo: Found unique promotion, Increasing promotion count and updating promoDisplayVo map");
									}
									promoCount++;
									List<PriceBeanDisplayVO> priceBeanDisplayVOList = new ArrayList<PriceBeanDisplayVO>();
									priceBeanDisplayVOList.add(priceBeanVO);
									promoDisplayVOMap.put(promoCouponKey, priceBeanDisplayVOList);
								} else {
									List<PriceBeanDisplayVO> priceBeanDisplayVOList = promoDisplayVOMap
											.get(promoCouponKey);
									if (null != priceBeanDisplayVOList) {
										priceBeanDisplayVOList.add(priceBeanVO);
									} else {
										if(getLogger().isLoggingDebug()){
										getLogger()
												.logDebug(
														"BBBOrderUtilty.populatePriceInfo: Found unique promotion, Increasing promotion count and updating promoDisplayVo map");
										}
										List<PriceBeanDisplayVO> dPriceBeanDisplayVOList = new ArrayList<PriceBeanDisplayVO>();
										dPriceBeanDisplayVOList.add(priceBeanVO);
										promoDisplayVOMap.put(promoCouponKey, dPriceBeanDisplayVOList);
									}
								}
							}
						}
					}
					if(getLogger().isLoggingDebug()){
					getLogger().logDebug(
							"BBBOrderUtilty.populatePriceInfo: promotion count is " + promoCount
									+ "updated promoDisplayVo map is " + promoDisplayVOMap);
					}
					priceInfoDisplayVO.setPromotionCount(promoCount);
					priceInfoDisplayVO.setPromoPriceBeansDisplayVO(promoDisplayVOMap);
				}
				/* BBBH-5500|My Offer Automation |Cart Page Changes Ends */
				/*
				 * This section is for order review and order confirmation page
				 * where we have different display logic. Also this is used for
				 * omniture tracking.
				 */
				List<PriceBeanDisplayVO> priceBeansList = new ArrayList<PriceBeanDisplayVO>();
				for (UnitPriceBean priceBean : priceInfoVO.getPriceBeans()) {
					List<String> promotions = new ArrayList<String>();
					PriceBeanDisplayVO priceBeanVO = new PriceBeanDisplayVO();
					priceBeanVO.setQuantity(priceBean.getQuantity());
					priceBeanVO.setUnitPrice(priceBean.getUnitPrice());
					List<RepositoryItem> pricingModels = priceBean.getPricingModels();
					if (null != pricingModels && !pricingModels.isEmpty()) {
						for (RepositoryItem pricingModel : pricingModels) {
							String itemDisplayName = pricingModel.getItemDisplayName();
							String discountType = String.valueOf(pricingModel
									.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS));
							priceBeanVO.setCouponDiscountType(discountType);
							if (StringUtils.isNotBlank(itemDisplayName)) {
								promotions.add(itemDisplayName.trim());
							}
						}
						priceBeanVO.setPromotionDisplayNameList(promotions);
						/*
						 * Calculate Omniture discount per item as per sale
						 * price or list price -Start
						 */
						if (priceInfoVO.getUnitSalePrice() > 0) {
							priceBeanVO.setOmmnitureDiscountAmount(convertToTwoDecimals(BigDecimal
									.valueOf(priceInfoVO.getUnitSalePrice() - priceBean.getUnitPrice())
									.multiply(BigDecimal.valueOf(priceBean.getQuantity())).doubleValue()));
						} else {
							priceBeanVO.setOmmnitureDiscountAmount(convertToTwoDecimals(BigDecimal
									.valueOf(priceInfoVO.getUnitListPrice() - priceBean.getUnitPrice())
									.multiply(BigDecimal.valueOf(priceBean.getQuantity())).doubleValue()));
						}
						/*
						 * Calculate Omniture discount per item as per sale
						 * price or list price -End
						 */
					}
					priceBeansList.add(priceBeanVO);
				}
				if(getLogger().isLoggingDebug()){
				getLogger().logDebug(
						"BBBOrderUtilty.populatePriceInfo: Unit price beans for price info " + priceInfo + " is "
								+ priceBeansList);
				}
				priceInfoDisplayVO.setPriceBeans(priceBeansList);
			}
			
			priceInfoDisplayVO.setItemAdjustments(priceInfoVO.getItemAdjustments());
			priceInfoDisplayVO.setShippingAdjustments(priceInfoVO.getShippingAdjustments());
			priceInfoDisplayVO.setRawAmount(convertToTwoDecimals(priceInfoVO.getRawAmount()));
			priceInfoDisplayVO.setItemPromotionVOList(priceInfoVO.getItemPromotionVOList());
			priceInfoDisplayVO.setTotalDiscountShare(convertToTwoDecimals(priceInfoVO.getTotalDiscountShare()));
			priceInfoDisplayVO.setUnitSavedPercentage(priceInfoVO.getUnitSavedPercentage());
			priceInfoDisplayVO.setUnitSavedAmount(convertToTwoDecimals(priceInfoVO.getUnitSavedAmount()));
			priceInfoDisplayVO.setUnitListPrice(convertToTwoDecimals(priceInfoVO.getUnitListPrice()));
			priceInfoDisplayVO.setUnitSalePrice(convertToTwoDecimals(priceInfoVO.getUnitSalePrice()));
			
			double totalGiftCardAmt = 0.0;
			// R 2.2 pay with paypal amount changes			
			if(null != order.getPaymentGroups() && !order.getPaymentGroups().isEmpty()){				
				
				double amountWithoutGiftCard = 0.0;
				for(Object paymentGrp: order.getPaymentGroups()){					
					if(paymentGrp instanceof BBBGiftCard){
						totalGiftCardAmt = totalGiftCardAmt + ((BBBGiftCard) paymentGrp).getAmount();						
					}					
				}
				amountWithoutGiftCard = (convertToTwoDecimals(priceInfoVO.getOnlineTotal() - totalGiftCardAmt));
				if(amountWithoutGiftCard <= 0){
					priceInfoDisplayVO.setAmountWithoutGiftCard(convertToTwoDecimals(0.0));
				}else{
				priceInfoDisplayVO.setAmountWithoutGiftCard(amountWithoutGiftCard);
				}
			}
			priceInfoDisplayVO.setTotalGiftCardAmt(totalGiftCardAmt);
			//LTL Changes Start
			
			priceInfoDisplayVO.setDeliverySurcharge(convertToTwoDecimals(priceInfoVO.getDeliverySurcharge()));
			priceInfoDisplayVO.setDeliverySurchargeProrated(convertToTwoDecimals(priceInfoVO.getDeliverySurchargeProrated()));
			priceInfoDisplayVO.setDeliverySurchargeSaving(convertToTwoDecimals(priceInfoVO.getDeliverySurchargeSaving()));
			priceInfoDisplayVO.setAssemblyFee(convertToTwoDecimals(priceInfoVO.getAssemblyFee()));
			
			priceInfoDisplayVO.setTotalAssemblyFee(convertToTwoDecimals(priceInfoVO.getTotalAssemblyFee()));
			priceInfoDisplayVO.setMaxDeliverySurchargeReached(priceInfoVO.isMaxDeliverySurchargeReached());
			priceInfoDisplayVO.setMaxDeliverySurcharge(convertToTwoDecimals(priceInfoVO.getMaxDeliverySurcharge()));
			priceInfoDisplayVO.setTotalDeliverySurcharge(convertToTwoDecimals(priceInfoVO.getTotalDeliverySurcharge()));
			
			//LTL Changes end
			
		}
		return priceInfoDisplayVO;
	}
	
	
	
	/**
	 * This method takes the priceInfoValues & reduce them to two decimals
	 * @param priceInfoValue
	 */
	
	
	public static double convertToTwoDecimals(double priceInfoValue){
		DecimalFormat twoDecimalFormat = new DecimalFormat("0.00");
		return Double.parseDouble(twoDecimalFormat.format(priceInfoValue));
		
	}
	
	/**
	 * Populates Commerce Item Display VO.
	 * 
	 * @param commerceItemVO
	 * @param commItemDisplayVO
	 */
	public static void populateCommerceItemDisplayVO(CommerceItemVO commerceItemVO, CommerceItemDisplayVO commItemDisplayVO) {
		
		if (null != commerceItemVO && null != commItemDisplayVO) {
			
			BBBCommerceItem item = commerceItemVO.getBBBCommerceItem();
			SKUDetailVO skuVO = commerceItemVO.getSkuDetailVO();
			RepositoryItem catalogRepositoryItem = (RepositoryItem) item.getAuxiliaryData().getCatalogRef();
			RepositoryItem productRepositoryItem= (RepositoryItem) item.getAuxiliaryData().getProductRef();
			commItemDisplayVO.setCommerceItemId(item.getId());
			commItemDisplayVO.setProductId(String.valueOf(item.getRepositoryItem().getPropertyValue("productId")));
			if(productRepositoryItem != null){
				commItemDisplayVO.setProductSeoUrl(String.valueOf(productRepositoryItem.getPropertyValue("seoUrl")));
			}
			commItemDisplayVO.setStoreId(item.getStoreId());
			commItemDisplayVO.setEligibleShipMethods(skuVO.getEligibleShipMethods());
			commItemDisplayVO.setGiftWrapEligible(skuVO.getGiftWrapEligible());
			commItemDisplayVO.setStockAvailability(commerceItemVO.getStockAvailability());
			commItemDisplayVO.setEligibleShipMethods(skuVO.getEligibleShipMethods());
			commItemDisplayVO.setCommaSepNonShipableStates(skuVO.getCommaSepNonShipableStates());
			commItemDisplayVO.setSkuId(skuVO.getSkuId());
			commItemDisplayVO.setShippingSurcharge(skuVO.getShippingSurcharge());
			commItemDisplayVO.setVdcSku(commerceItemVO.getBBBCommerceItem().isVdcInd());
			commItemDisplayVO.setSkuDisplayName((String) catalogRepositoryItem.getPropertyValue("displayName"));
			commItemDisplayVO.setSkuThumbnailImage((String) catalogRepositoryItem.getPropertyValue("thumbnailImage"));
			commItemDisplayVO.setSkuSmallImage((String) catalogRepositoryItem.getPropertyValue("smallImage"));
			commItemDisplayVO.setSkuDescription(skuVO.getDescription());
			//commItemDisplayVO.setSkuLongDescription(skuVO.getLongDescription());
			commItemDisplayVO.setSkuColor(skuVO.getColor());
			commItemDisplayVO.setSize(skuVO.getSize());
			commItemDisplayVO.setUpc(skuVO.getUpc());
			commItemDisplayVO.setBopusAllowed(skuVO.isBopusAllowed());
			commItemDisplayVO.setSkuHasRebate(skuVO.isHasRebate());
			commItemDisplayVO.setSkuOnSale(skuVO.isOnSale());
			commItemDisplayVO.setLastModifiedDate(commerceItemVO.getBBBCommerceItem().getLastModifiedDate());
			commItemDisplayVO.setEcoFeeEligible(skuVO.getIsEcoFeeEligible());
			commItemDisplayVO.setLtlItem(skuVO.isLtlItem());
			commItemDisplayVO.setWebOfferedFlag(skuVO.isWebOfferedFlag());
			commItemDisplayVO.setIntlRestricted(skuVO.isIntlRestricted());
			commItemDisplayVO.setReferenceNumber(item.getReferenceNumber());
			commItemDisplayVO.setFullImagePath(item.getFullImagePath());
			commItemDisplayVO.setSkuDisableFlag(skuVO.isDisableFlag());
		}
		
	}
	
	private static final String DATE_FORMAT = "MM/dd/yyyy";
	
	/**
	 * populates order properties in orderVO.
	 * 
	 * @param order
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public static BBBOrderVO populateOrderDetails(BBBOrderImpl order, BBBOrderVO orderVO) {

		if (null != orderVO && null != order) {
			orderVO.setOrderId(order.getId());
			orderVO.setTransientFlag(order.isTransient());
			orderVO.setBillingAddress(order.getBillingAddress());
			orderVO.setBopusOrderFlag(order.isBopusOrder());
			orderVO.setOrderType(order.getOnlineBopusItemsStatusInOrder());
			orderVO.setShippingGroups(order.getShippingGroups());
			
			orderVO.setBopusOrderNumber(order.getBopusOrderNumber());
			orderVO.setOnlineOrderNumber(order.getOnlineOrderNumber());
			//R2.2 - 83 Paypal -Set Paypal Flag - Start
			orderVO.setPayPalOrder(order.isPayPalOrder());
			//R2.2- END
			if(orderVO.getBillingAddress() != null && orderVO.getBillingAddress().getCountry() != null){
				if(orderVO.getBillingAddress().getCountry().equalsIgnoreCase("Canada")){
					orderVO.getBillingAddress().setCountry("CA");
				}
			}
			if(null != order.getSubmittedDate()){
				DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
				String orderDate = formatter.format(order.getSubmittedDate());  
				orderVO.setSubmittedDate(orderDate);
			}
			
			String orderStatus = order.getStateDetail();
			if (StringUtils.isNotBlank(orderStatus)) {
				orderVO.setOrderStatus(order.getStateDetail());
			} else {
				orderVO.setOrderStatus(order.getStateAsString());
			}
			orderVO.setExpressCheckOut(order.isExpressCheckOut());
			orderVO.setOrderSubStatus(order.getSubStatus());
			orderVO.setEmailSignUp(order.isEmailSignUp());
		}

		return orderVO;
	}

	/**
	 * Populate CreditCard PaymentGrp.
	 * 
	 * @param paymentVO
	 * @param paymentGrp
	 */
	public static PaymentGroupDisplayVO populateCreditCardPaymentGrp(PaymentGroupDisplayVO paymentVO, BBBCreditCard paymentGrp) {
		paymentVO.setAmountCredited(paymentGrp.getAmountCredited());
		paymentVO.setAmountAuthorized(paymentGrp.getAmountAuthorized());
		paymentVO.setAmountDebited(paymentGrp.getAmountDebited());
		paymentVO.setBalanceAmtGiftCard(paymentGrp.getAmountDebited());
		paymentVO.setCurrencyCode(paymentGrp.getCurrencyCode());
		paymentVO.setId(paymentGrp.getId());
		paymentVO.setPaymentMethodType(paymentGrp.getPaymentMethod());
		paymentVO.setState(paymentGrp.getStateAsUserResource());
		return paymentVO;
	}

	/**
	 * R2.2 - 83 J Populate Paypal PaymentGrp.
	 * 
	 * @param paymentVO
	 * @param paymentGrp
	 * @return PaymentGroupDisplayVO
	 */
	public static PaymentGroupDisplayVO populatePayPalPaymentGrp(PaymentGroupDisplayVO paymentVO, Paypal paymentGrp) {
		paymentVO.setPaymentMethodType(paymentGrp.getPaymentMethod());
		paymentVO.setPayerEmail(paymentGrp.getPayerEmail());
		return paymentVO;
	}

	/**
	 * Populate GiftCard PaymentGrp.
	 * 
	 * @param paymentVO
	 * @param paymentGrp
	 */
	public static PaymentGroupDisplayVO populateGiftCardPaymentGrp(PaymentGroupDisplayVO paymentVO, BBBGiftCard paymentGrp) {
		paymentVO.setAmountCredited(paymentGrp.getAmountCredited());
		paymentVO.setAmount(paymentGrp.getAmount());
		paymentVO.setAmountAuthorized(paymentGrp.getAmountAuthorized());
		paymentVO.setAmountDebited(paymentGrp.getAmountDebited());
		paymentVO.setBalanceAmtGiftCard(paymentGrp.getBalance());
		paymentVO.setRemainingBalance(paymentGrp.getBalance()- paymentGrp.getAmount());
		paymentVO.setCurrencyCode(paymentGrp.getCurrencyCode());
		paymentVO.setId(paymentGrp.getId());
		paymentVO.setPaymentMethodType(paymentGrp.getPaymentMethod());
		paymentVO.setState(paymentGrp.getStateAsUserResource());
		if(StringUtils.isNotBlank(paymentGrp.getCardNumber())){
			paymentVO.setGiftCardNumber(BBBUtility.maskCrediCardNumber(paymentGrp.getCardNumber()));
		}
		
		return paymentVO;
	}

	/**
	 * This method add the total taxes for state and county in case of multiple 
	 * shipping for an order for BedBathCanada site.
	 * @param shippingGroups
	 * @param orderPriceInfo
	 * @param priceInfoDisplayVO
	 */
	public static void setCanadaMultipleShippingTax(List<ShippingGroupDisplayVO> shippingGroups,
			PriceInfoDisplayVO priceInfoDisplayVO) {
		
		if(!shippingGroups.isEmpty()){
			Iterator<ShippingGroupDisplayVO> itr = shippingGroups.iterator();
			ShippingGroupDisplayVO shippingGroupDisplayVo = null;
			double totalStateTax = 0.0;
			double totalCountyTax = 0.0;
			
			while(itr.hasNext()) {
				shippingGroupDisplayVo = itr.next();
				totalStateTax = totalStateTax + shippingGroupDisplayVo.getShippingPriceInfoDisplayVO().getShippingStateLevelTax();
				totalCountyTax = totalCountyTax + shippingGroupDisplayVo.getShippingPriceInfoDisplayVO().getShippingCountyLevelTax();
			}
			priceInfoDisplayVO.setShippingStateLevelTax(totalStateTax);
			priceInfoDisplayVO.setShippingCountyLevelTax(totalCountyTax);
		}
	}
}
