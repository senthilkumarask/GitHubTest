package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.Range;
import atg.nucleus.naming.ComponentName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.catalog.vo.SiteVO;
import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.commerce.order.vo.OrderOmnitureVO;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.utils.BBBUtility;

/**
 * @author hkapo1
 * @version $Revision: #1 $
 */
public class BBBOrderOmnitureDroplet extends BBBDynamoServlet {
	private static final String COMMA = ",";
	private static final String SEMICOLON = ";";
	private static final String PIPE = "|";
	private static final String BBBCOUPONS = "bbbCoupons";
	DecimalFormat forma = new DecimalFormat("0.00");
	private BBBCatalogTools catalogTools;
	private BBBPricingTools mPricingTools;
    private BBBEximManager bbbEximPricingManager;
    private final String REGISTERED_USER="registered user";
    
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the pricingTools
	 */
	public final BBBPricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public final void setPricingTools(BBBPricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}

	/**
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("START: BBBOrderOmnitureDroplet.service");

		Object order = pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		Profile profile = (Profile) pRequest.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		
		/* Code change for JIRA Ticket #TBXPS-1470 starts */
		
		boolean isLoggedInUser = pRequest.getObjectParameter(BBBCoreConstants.IS_LOGGEDIN_USER) == null 
								? false : Boolean.parseBoolean(pRequest.getObjectParameter(BBBCoreConstants.IS_LOGGEDIN_USER).toString());
		
		/* Code change for JIRA Ticket #TBXPS-1470 ends */
		
		OrderOmnitureVO omnitureVO = new OrderOmnitureVO();
		try {
			if (order != null) {

				StringBuilder products = new StringBuilder();
				StringBuilder events = new StringBuilder();
				StringBuilder event58and59 = new StringBuilder();

				if (((BBBOrder) order).getCommerceItems().size() > 0) {
					events.append("purchase");
				}

				appendProdAndEvents(order, products, events, event58and59);


				// Total Shipping Amount
				appendTotalShipping(order, products, events);

				// Total Tax Amount
				appendTaxAmt(order, products, events);
				if (((BBBOrder) order).getSiteId() != null && ((BBBOrder) order).getSiteId().contains("TBS")) {
					appendTotalOverriddenAmt(order, products, events,omnitureVO);
				}

				// Order Discounts
				//appendOrderPromos(order, products, events);

				String genOrderCode = ((BBBOrder) order).getOnlineOrderNumber();
				if (genOrderCode == null) {
					genOrderCode = ((BBBOrder) order).getBopusOrderNumber();
				}

				omnitureVO.setPurchaseID(genOrderCode);

				if(((BBBOrder) order).getBillingAddress() != null){
					omnitureVO.setState(((BBBOrder) order).getBillingAddress().getState());
					omnitureVO.setZip(((BBBOrder) order).getBillingAddress().getPostalCode());
				}

				List<String> promoStr = createPromoStr(order, profile);
				omnitureVO.setProp17(promoStr.get(0)+promoStr.get(1));

				// Add evars to VO
				updateOmnitureVO(order, profile, omnitureVO);
				
				
				//Code change for JIRA Ticket #TBXPS-1470 starts  
				if (isLoggedInUser){
					omnitureVO.setEvar16(REGISTERED_USER); 
				}
				//Code change for JIRA Ticket #TBXPS-1470 ends
				
				String orderDiscountCoupon = promoStr.get(1);
				if(promoStr.get(1).startsWith(COMMA)){
					orderDiscountCoupon = promoStr.get(1).replaceFirst(COMMA, "");
				}
				appendProdAndEvent15(order, products, events, orderDiscountCoupon, omnitureVO);

				//LTL Changes : START
				events.append(event58and59);
				//LTL Changes : END

				omnitureVO.setProducts(products.toString());
				omnitureVO.setEvents(events.toString());

			}
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {
				logError("BBBBusinessException occourred from Catalog API call");
			}
			pRequest.setParameter(BBBCoreConstants.ERROR,
					"Error occourred in finding order details");
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
					pResponse);
		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError("BBBBusinessException occourred from Catalog API call");
			}
			pRequest.setParameter(BBBCoreConstants.ERROR,
					"Error occourred in finding order details");
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
					pResponse);
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("RepositoryException occourred from Repository API call");
			}
			pRequest.setParameter(BBBCoreConstants.ERROR,
					"Error occourred in finding order details");
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
					pResponse);
		} catch (CommerceItemNotFoundException e) {
			if (isLoggingError()) {
				logError("CommerceItemNotFoundException occourred from Repository API call");
		}
			pRequest.setParameter(BBBCoreConstants.ERROR,
					"Error occourred in finding commerecitem details");
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
					pResponse);
		} catch (InvalidParameterException e) {
			if (isLoggingError()) {
				logError("InvalidParameterException occourred from Repository API call");
			}
			pRequest.setParameter(BBBCoreConstants.ERROR,
					"Error occourred in finding commerecitem details");
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
					pResponse);
		}

		pRequest.setParameter(BBBCoreConstants.OMNITURE_VO, omnitureVO);
		pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
		logDebug("END: BBBOrderOmnitureDroplet.service");
		logDebug("output : " + omnitureVO.toString());

	}

	/**
	 * @param order
	 * @param profile
	 * @param omnitureVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	private void updateOmnitureVO(final Object order, final Profile profile,
			final OrderOmnitureVO omnitureVO) throws BBBBusinessException,
			BBBSystemException {
		StringBuilder evar12 = new StringBuilder();
		int indx = 1;
		for (PaymentGroup paymentGroup : (List<PaymentGroup>) ((BBBOrder) order)
				.getPaymentGroups()) {
			if (paymentGroup.getOrderRelationshipCount() > 0) {
				if(paymentGroup instanceof CreditCard){
					evar12.append(((CreditCard)paymentGroup).getCreditCardType());
				}else{
					evar12.append(paymentGroup.getPaymentMethod());
				}
				if (indx < ((BBBOrder) order).getPaymentGroupCount()) {
					evar12.append(COMMA);
				}
			}
			indx ++;
		}
		if (evar12.length() > 0) {
			omnitureVO.setEvar12(evar12.toString());
		}

		if (profile.isTransient()) {
			omnitureVO.setEvar16("Non registered User");
		} else {
			omnitureVO.setEvar16("registered user");
		}

		if (((BBBOrderImpl) order).isExpressCheckOut()) {
			omnitureVO.setEvar14("express");
		} else {
			omnitureVO.setEvar14("non express");
		}

		if (((BBBOrder) order).getSiteId() != null) {
			SiteVO siteVO = getCatalogTools().getSiteDetailFromSiteId(
					((BBBOrder) order).getSiteId());

			logDebug("SiteName From Catalog API Call : "
						+ siteVO.getSiteName());
			logDebug("CountryCode From Catalog API Call : "
						+ siteVO.getCountryCode());

			omnitureVO.setEvar19(siteVO.getSiteName());
			omnitureVO.setEvar20(siteVO.getCountryCode());
			omnitureVO.setEvar21("Web");
		}
	}

	/**
	 * Extract all the promotions applied to the order
	 *
	 * @param order
	 * @param profile
	 * @return
	 */
	
	private List<String> createPromoStr(final Object order, final Profile profile) {
		List<String> promotionStringList = new ArrayList<String>();
		final StringBuilder itemPromotionString = new StringBuilder();
		final StringBuilder orderPromotionString = new StringBuilder();
		/*BBBH-3026 Code Change for fetching promotions from claimable repository not from BBBCoupons Property*/
		List<RepositoryItem>  couponRepositoryItem = this.getPromotionTools().getCouponListFromOrder((BBBOrderImpl)order);
		boolean firstItem = true;
		boolean firstOrderItem = true;

		try {
		for (RepositoryItem repositoryItem : couponRepositoryItem) {
			RepositoryItem []	currentPromos =  this.getCatalogTools().getPromotions(repositoryItem.getRepositoryId());//(RepositoryItem) ((Set) repositoryItem.getPropertyValue("promotions")).iterator().next();
			RepositoryItem currentPromo = null;
			if(currentPromos != null && currentPromos.length >0){
				currentPromo= currentPromos[0];
			}
			if(currentPromo!= null)
				{
					
						logDebug("Current Promotions:" + currentPromo);
						if(currentPromo.getItemDescriptor().getItemDescriptorName()
						.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT)){
							if(!firstItem){
								itemPromotionString.append(COMMA);
							}
							itemPromotionString.append(String.valueOf(currentPromo
									.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME)));
							firstItem = false;
						}
						else if(currentPromo.getItemDescriptor().getItemDescriptorName()
						.equalsIgnoreCase(BBBCoreConstants.ORDER_DISCOUNT)){
							if(!firstOrderItem){
								orderPromotionString.append(COMMA);
							}
							orderPromotionString.append(String.valueOf(currentPromo
									.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME)));
							firstOrderItem = false;
						}
						
					}
				}
		}catch (RepositoryException e) {
						
						logError("Error occured while fetching coupons in omniture",e);
					}
					catch (BBBSystemException e) {
						
						logError("BBBSystem Exception occured while fetching coupons in omniture",e);
					} catch (BBBBusinessException e) {
						
						logError("BBBBusiness Exception occured while fetching coupons in omniture",e);
					}
				
			
			if(!firstItem && orderPromotionString.length()>0){
				 orderPromotionString.insert(0,COMMA);
			}
		
		promotionStringList.add(itemPromotionString.toString());
		promotionStringList.add(orderPromotionString.toString());
		return promotionStringList;
	}

	private BBBPromotionTools mPromotionTools;

	public BBBPromotionTools getPromotionTools() {
		return mPromotionTools;
	}

	public void setPromotionTools(BBBPromotionTools pPromotionTools) {
		this.mPromotionTools = pPromotionTools;
	}

	private void appendTaxAmt(final Object order, final StringBuilder products,
			final StringBuilder events) {
		if (((BBBOrder) order).getTaxPriceInfo() != null) {
			final double taxAmt = ((BBBOrder) order).getTaxPriceInfo()
					.getAmount();
			events.append(COMMA);
			events.append("event14=");
			events.append(forma.format(getPricingTools().round(taxAmt, 2)));

			/*
			products.append(COMMA);
			products.append(";Tax;;;");
			products.append("event14=");
			//products.append(Double.toString(taxAmt));
			products.append(forma.format(getPricingTools().round(taxAmt, 2)));
			*/
		}
	}

	@SuppressWarnings("unchecked")
	private void appendTotalShipping(final Object order,
			final StringBuilder products, final StringBuilder events) {
		double totalShippingAmt = 0;

		PriceInfoVO orderPriceInfo = getPricingTools().getOrderPriceInfo((OrderImpl)order);
		double shippingSavingsAmount = orderPriceInfo.getShippingSavings();
		totalShippingAmt = orderPriceInfo.getRawShippingTotal();
//		for (ShippingGroup shippingGroup : (List<ShippingGroup>) ((BBBOrder) order)
//				.getShippingGroups()) {
//			if (shippingGroup instanceof BBBHardGoodShippingGroup
//					&& shippingGroup.getPriceInfo() != null) {
//				totalShippingAmt += shippingGroup.getPriceInfo().getAmount();
//			}
//		}
		double totalShippingAmtAfterDiscount = totalShippingAmt - shippingSavingsAmount;

	//	if (totalShippingAmtAfterDiscount > 0) {
			//Update as per BBBSL-3200
			events.append(COMMA);
			events.append("event13=");
			events.append(forma.format(getPricingTools().round(totalShippingAmtAfterDiscount, 2)));

			/*
			products.append(COMMA);
			products.append(";Shipping;;;");
			products.append("event13=");
			//products.append(Double.toString(totalShippingAmt));
			products.append(forma.format(getPricingTools().round(totalShippingAmtAfterDiscount, 2)));
			*/
	//	}
	}

	@SuppressWarnings("unchecked")
	private void appendProdAndEvents(final Object order,
			final StringBuilder products, final StringBuilder events, final StringBuilder event58and59)
			throws RepositoryException, CommerceItemNotFoundException, InvalidParameterException {
		String productId = null;
		String skuId = null;
		long quantity = 0;
		double price = 0.00;

		boolean promoApplied = false;
		boolean firstItem = true;

		boolean setEvent58Flag = false;
		boolean setEvent59Flag = false;
		HashMap<String,String> eximMap = getBbbEximPricingManager().getEximValueMap();
		for (ShippingGroup sg : (List<ShippingGroup>) ((BBBOrder) order)
				.getShippingGroups()) {

			for (ShippingGroupCommerceItemRelationship cisiRel : (List<ShippingGroupCommerceItemRelationship>) sg
					.getCommerceItemRelationships()) {

				CommerceItem commerceItem = cisiRel.getCommerceItem();
				if(!(commerceItem instanceof BBBCommerceItem)) {
                    continue;
                }
				productId = commerceItem.getAuxiliaryData().getProductId();
				skuId = commerceItem.getCatalogRefId();
				BBBCommerceItem bbbCommItem = (BBBCommerceItem)commerceItem;
				quantity = cisiRel.getQuantity();
				if (cisiRel.getCommerceItem().getPriceInfo() != null) {
					double salePrice = cisiRel.getCommerceItem().getPriceInfo().getSalePrice();
					double listPrice = cisiRel.getCommerceItem().getPriceInfo().getListPrice();
					if(salePrice>0){
						price= salePrice;
					}else{
						price=listPrice;
					}
				} else {
					logError("BBBOrderOmnitureDroplet.appendProdAndEvents(). Null pointer exception occured while getting price info.");
				}
				double shippingGroupItemsTotal = 0;
				shippingGroupItemsTotal += BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity)).doubleValue();
				
				DecimalFormat twoDForm = new DecimalFormat("#.00");
				String sgItemsTotal = twoDForm.format(shippingGroupItemsTotal);
				
				// Find discounts applied to Item PriceInfo
				int index = 0;
				double itemAdjustment = 0;
				double totalItemAdjustment = 0;
				StringBuffer evar35 = new StringBuffer();

				for (PricingAdjustment itemPriceAdj : (List<PricingAdjustment>) commerceItem
						.getPriceInfo().getAdjustments()) {
					index ++;
					if (itemPriceAdj.getPricingModel() != null) {
						promoApplied = true;

						itemAdjustment = itemPriceAdj.getTotalAdjustment();
						itemAdjustment = Math.abs(itemAdjustment);
						totalItemAdjustment += itemAdjustment;

						evar35.append(itemPriceAdj.getPricingModel().getPropertyValue("displayName"));
						if(index < commerceItem.getPriceInfo().getAdjustments().size()){
							evar35.append(BBBCoreConstants.PLUS);
						}
					}
				}

				if(!firstItem){
					products.append(COMMA);
				}

				products.append(SEMICOLON);
				products.append(productId);
				products.append(SEMICOLON);
				products.append(quantity);
				products.append(SEMICOLON);
				products.append(sgItemsTotal);
				products.append(SEMICOLON);
				//LTL Changes : START
		        if(((BBBCommerceItem)commerceItem).isLtlItem()){
		        	String deliveryItemId = ((BBBCommerceItem)commerceItem).getDeliveryItemId();
		        	String assemblyItemId = ((BBBCommerceItem)commerceItem).getAssemblyItemId();
		        	LTLDeliveryChargeCommerceItem deliveryCommerceItem = (LTLDeliveryChargeCommerceItem) ((BBBOrder)order).getCommerceItem(deliveryItemId);
		        	//LTL-1568 || assemblyItemId should be checked for both blank and null
		        	if(BBBUtility.isNotEmpty(assemblyItemId)){
			        	LTLAssemblyFeeCommerceItem assemblyFeeCommerceItem = (LTLAssemblyFeeCommerceItem) ((BBBOrder)order).getCommerceItem(assemblyItemId);
			        	if(assemblyFeeCommerceItem != null){
			        		Double assemblyFee= assemblyFeeCommerceItem.getPriceInfo().getAmount();
			        		if(assemblyFee!=null && assemblyFee > 0){
			        			setEvent58Flag = true;
			        			products.append("event58=");
			        			products.append(forma.format(getPricingTools().round(assemblyFee, 2)));
			        			products.append(PIPE);
			        		}
			        	}

		        	}

		        	products.append("event59=");
		        	setEvent59Flag = true;
	    			double deliverySurcharge=deliveryCommerceItem.getPriceInfo().getAmount();
	                products.append(forma.format(getPricingTools().round(deliverySurcharge, 2)));

		        }
				//LTL Changes : END

		        if(((BBBCommerceItem)commerceItem).isLtlItem() && evar35.length() > 0){
		        	products.append(PIPE);
		        }

				if(evar35.length() > 0){
					products.append("event16=");
					//products.append(Double.toString(totalItemAdjustment));
					products.append(forma.format(getPricingTools().round(totalItemAdjustment, 2)));
					products.append(SEMICOLON);
					products.append("eVar35=");
					products.append(evar35.toString());
					products.append(PIPE);
				}else{
					products.append(SEMICOLON);
				}


				products.append("eVar30=");
				products.append(skuId);

				if (cisiRel.getShippingGroup() instanceof HardgoodShippingGroup) {
					HardgoodShippingGroup hgsg = (HardgoodShippingGroup) cisiRel
							.getShippingGroup();
					if (hgsg.getShippingAddress() != null) {
						products.append(PIPE);
						products.append("eVar13=");

						if(StringUtils.isNotBlank(hgsg.getShippingMethod())){
							RepositoryItem shippingMethod;
							try {
								shippingMethod = getCatalogTools().getShippingMethod(hgsg.getShippingMethod());
								String shippingMethodDescription = (String) shippingMethod.getPropertyValue("shipMethodDescription");

								if(StringUtils.isNotBlank(shippingMethodDescription)){
									products.append(shippingMethodDescription);
								}

							} catch (BBBBusinessException e) {
								logError("Business Error while re trieving shipping method for [" + hgsg.getShippingMethod() + "]", e);
							} catch (BBBSystemException e) {
								logError("System Error while retrieving shipping method for [" + hgsg.getShippingMethod() + "]", e);
							}
						}


						products.append(PIPE);
						products.append("eVar17=");
						products.append(hgsg.getShippingAddress()
								.getState());
						products.append(PIPE);
						products.append("eVar18=");
						products.append(hgsg.getShippingAddress()
								.getPostalCode());
						if(null !=bbbCommItem.getReferenceNumber() && !StringUtils.isEmpty(bbbCommItem.getReferenceNumber())){
		        			   products.append(PIPE);
		        			   products.append("eVar54=");
		        			   products.append(eximMap.get(bbbCommItem.getPersonalizationOptions()));
						}

					}
				} else if (cisiRel.getShippingGroup() instanceof BBBStoreShippingGroup) {
					products.append(PIPE);
					products.append("eVar13=");
					products.append("Pick up in Store");
				}

				if(bbbCommItem.isBuyOffAssociatedItem()) {
     			   products.append(PIPE);
     			   products.append("eVar55=");
     			   products.append(BBBCoreConstants.OMNI_BUY_OFF_REG_MSG);
				}

				firstItem = false;
				//products.append(COMMA);


			}
		}

		//LTL Changes : START
		if(setEvent58Flag){
       	 	event58and59.append(COMMA);
       	 	event58and59.append("event58");
		}if(setEvent59Flag){
			event58and59.append(COMMA);
			event58and59.append("event59");
		}
		//LTL Changes : END

		if(promoApplied) {
			events.append(COMMA);
			events.append("event16");
		}

		}




	@SuppressWarnings("unchecked")
	private void appendProdAndEvent15(final Object order,
			final StringBuilder products, final StringBuilder events, final String promos, OrderOmnitureVO omnitureVO)
			throws RepositoryException {
		long quantity = 0;
		double price = 0.00;
		double shippingGroupItemsTotal = 0;
		double orderUnitPriceTotal = 0;
		double totalItemAdjustment = 0;
		PriceInfoVO orderPriceInfo = getPricingTools().getOrderPriceInfo((OrderImpl)order);
		for (ShippingGroup sg : (List<ShippingGroup>) ((BBBOrder) order)
				.getShippingGroups()) {

			for (ShippingGroupCommerceItemRelationship cisiRel : (List<ShippingGroupCommerceItemRelationship>) sg
					.getCommerceItemRelationships()) {

				CommerceItem commerceItem = cisiRel.getCommerceItem();
				if(!(commerceItem instanceof BBBCommerceItem)) {
                    continue;
                }
				int index = 0;
				double itemAdjustment = 0;
				for (PricingAdjustment itemPriceAdj : (List<PricingAdjustment>) commerceItem
						.getPriceInfo().getAdjustments()) {
					index ++;
					if (itemPriceAdj.getPricingModel() != null) {

						itemAdjustment = itemPriceAdj.getTotalAdjustment();
						itemAdjustment = Math.abs(itemAdjustment);
						totalItemAdjustment += itemAdjustment;
					}
				}
				Range relationshipRange = cisiRel.getRange();
				quantity = cisiRel.getQuantity();
				List<UnitPriceBean> priceBeans= getPricingTools().generatePriceBeans(cisiRel.
						getCommerceItem().getPriceInfo().getCurrentPriceDetailsForRange(relationshipRange));

					for(UnitPriceBean unitPriceBean : priceBeans) {
						orderUnitPriceTotal += BigDecimal.valueOf(unitPriceBean.getUnitPrice()).multiply(BigDecimal.valueOf(unitPriceBean.getQuantity())).doubleValue();
					}

				double salePrice = cisiRel.getCommerceItem().getPriceInfo().getSalePrice();
				double listPrice = cisiRel.getCommerceItem().getPriceInfo().getListPrice();
				if(salePrice>0){
					price= salePrice;
				}else{
					price=listPrice;
				}

				shippingGroupItemsTotal += BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity)).doubleValue();
			}
		}
		double orderLevelDiscount = shippingGroupItemsTotal - (orderUnitPriceTotal+totalItemAdjustment);
		if(StringUtils.isNotBlank(promos) && null != orderPriceInfo){

			/*
			 * BBBSL-3200 removing them from product and moving to events
			 */

			//Updating events as per BBBSL-3200
			events.append(COMMA);
			events.append("event15=");
			events.append(forma.format(getPricingTools().round(orderLevelDiscount, 2)));
			omnitureVO.setEvar31(promos);

			if(getPricingTools().round(orderPriceInfo.getShippingSavings())>0){
				events.append(COMMA);
				events.append("event43=");
				events.append(forma.format(getPricingTools().round(orderPriceInfo.getShippingSavings(), 2)));
			}
		}else if(orderPriceInfo != null && getPricingTools().round(orderPriceInfo.getShippingSavings())>0){
			events.append(COMMA);
			events.append("event43=");
			events.append(forma.format(getPricingTools().round(orderPriceInfo.getShippingSavings(), 2)));
		}

	}
	
	@SuppressWarnings("unchecked")
	private void appendTotalOverriddenAmt(final Object order,
			final StringBuilder products, final StringBuilder events, OrderOmnitureVO omnitureVO) {
		double totalOverRiddenAmt = 0;
		StringBuilder reasonCodes = new StringBuilder();
		if (((BBBOrder) order).getCommerceItems().size() > 0) {
			List<CommerceItem> CommerceItms = ((BBBOrder) order).getCommerceItems();
			for (CommerceItem commerceItem : CommerceItms) {
				if (commerceItem instanceof TBSCommerceItem && null != ((TBSCommerceItem) commerceItem).getTBSItemInfo() && 
						((TBSCommerceItem) commerceItem).getTBSItemInfo().isPriceOveride()) {
					reasonCodes.append(((TBSCommerceItem) commerceItem).getTBSItemInfo().getOverideReason());
					reasonCodes.append(COMMA);
					List<PricingAdjustment> adjustments = ((TBSCommerceItem) commerceItem).getPriceInfo().getAdjustments();
					for (PricingAdjustment pricingAdjustment : adjustments) {
						if(pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase(TBSConstants.PRICE_OVERRIDE_DESC)) {
							totalOverRiddenAmt += Math.abs(pricingAdjustment.getTotalAdjustment());
							break;
						}
					}
				}
				if (commerceItem instanceof LTLAssemblyFeeCommerceItem && null != ((LTLAssemblyFeeCommerceItem) commerceItem).getTBSItemInfo() && 
					((LTLAssemblyFeeCommerceItem) commerceItem).getTBSItemInfo().isPriceOveride()) {
					reasonCodes.append(((LTLAssemblyFeeCommerceItem) commerceItem).getTBSItemInfo().getOverideReason());
					reasonCodes.append(COMMA);
					List<PricingAdjustment> adjustments = ((LTLAssemblyFeeCommerceItem) commerceItem).getPriceInfo().getAdjustments();
					for (PricingAdjustment pricingAdjustment : adjustments) {
						if(pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase(TBSConstants.PRICE_OVERRIDE_DESC)) {
							totalOverRiddenAmt += Math.abs(pricingAdjustment.getTotalAdjustment());
							break;
						}
					}
				}
				if (commerceItem instanceof LTLDeliveryChargeCommerceItem && null != ((LTLDeliveryChargeCommerceItem) commerceItem).getTBSItemInfo() && 
					((LTLDeliveryChargeCommerceItem) commerceItem).getTBSItemInfo().isPriceOveride()) {
					reasonCodes.append(((LTLDeliveryChargeCommerceItem) commerceItem).getTBSItemInfo().getOverideReason());
					reasonCodes.append(COMMA);
					List<PricingAdjustment> adjustments = ((LTLDeliveryChargeCommerceItem) commerceItem).getPriceInfo().getAdjustments();
					for (PricingAdjustment pricingAdjustment : adjustments) {
						if(pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase(TBSConstants.PRICE_OVERRIDE_DESC)) {
							totalOverRiddenAmt += Math.abs(pricingAdjustment.getTotalAdjustment());
							break;
						}
					}
				}
				if (commerceItem instanceof GiftWrapCommerceItem && (null != ((GiftWrapCommerceItem) commerceItem).getTBSItemInfo()) && 
						((GiftWrapCommerceItem) commerceItem).getTBSItemInfo().isPriceOveride()) {
					reasonCodes.append(((GiftWrapCommerceItem) commerceItem).getTBSItemInfo().getOverideReason());
					reasonCodes.append(COMMA);
					List<PricingAdjustment> adjustments = ((GiftWrapCommerceItem) commerceItem).getPriceInfo().getAdjustments();
					for (PricingAdjustment pricingAdjustment : adjustments) {
						if(pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase(TBSConstants.PRICE_OVERRIDE_DESC)) {
							totalOverRiddenAmt += Math.abs(pricingAdjustment.getTotalAdjustment());
							break;
						}
					}
				}
			}
		}
		//shiping and tax overridden amt and reason codes
		if (!((BBBOrder) order).getShippingGroups().isEmpty()) {
				for (ShippingGroup shippingGroup : (List<ShippingGroup>) ((BBBOrder) order).getShippingGroups()) {
					BBBShippingPriceInfo shippingPriceInfo = (BBBShippingPriceInfo) shippingGroup.getPriceInfo();
					TBSShippingInfo shipInfo = null;
					if (shippingPriceInfo != null) {
						if(shippingGroup instanceof BBBHardGoodShippingGroup){
							shipInfo = ((BBBHardGoodShippingGroup)shippingGroup).getTbsShipInfo();
						} else if(shippingGroup instanceof BBBStoreShippingGroup){
							shipInfo = ((BBBStoreShippingGroup)shippingGroup).getTbsShipInfo();
						}
						if(shipInfo != null && shipInfo.isShipPriceOverride()){
							reasonCodes.append(shipInfo.getShipPriceReason());
							reasonCodes.append(COMMA);
							List<PricingAdjustment> adjustments = shippingGroup.getPriceInfo().getAdjustments();
							for (PricingAdjustment pricingAdjustment : adjustments) {
								if(pricingAdjustment.getPricingModel() == null && pricingAdjustment.getAdjustmentDescription().equals("Shipping Override")){
									totalOverRiddenAmt += Math.abs(pricingAdjustment.getAdjustment());
								}
							}
						}
						if(shipInfo != null && shipInfo.isTaxOverride()){
							reasonCodes.append(shipInfo.getTaxReason());
							reasonCodes.append(COMMA);
							Map<String, TaxPriceInfo> taxInfos = ((BBBOrder) order).getTaxPriceInfo().getShippingItemsTaxPriceInfos();
							TaxPriceInfo taxPriceInfo = taxInfos.get(shippingGroup.getId());
							if(taxPriceInfo != null){
								totalOverRiddenAmt += taxPriceInfo.getCityTax();
								totalOverRiddenAmt += taxPriceInfo.getCountryTax();
								totalOverRiddenAmt += taxPriceInfo.getCountyTax();
								totalOverRiddenAmt += taxPriceInfo.getStateTax();
							}
						}
						if(shipInfo != null && shipInfo.isSurchargeOverride()){
							reasonCodes.append(shipInfo.getSurchargeReason());
							reasonCodes.append(COMMA);
							List<PricingAdjustment> adjustments = shippingGroup.getPriceInfo().getAdjustments();
							for (PricingAdjustment pricingAdjustment : adjustments) {
								if(pricingAdjustment.getPricingModel() == null && pricingAdjustment.getAdjustmentDescription().equals("Surcharge Override")){
									totalOverRiddenAmt += Math.abs(pricingAdjustment.getAdjustment());
								}
							}
						}
					}
				}
			}
		events.append(COMMA);
		events.append("event70=");
		events.append(forma.format(getPricingTools().round(totalOverRiddenAmt)));
		omnitureVO.seteVar54(reasonCodes.toString());

	}

	public OrderOmnitureVO getOmnitureVO() throws BBBSystemException, BBBBusinessException{
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();

		OrderOmnitureVO omnitureVO = new OrderOmnitureVO();

		final OrderHolder cart = (OrderHolder) request.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
        final BBBOrderImpl order = (BBBOrderImpl) cart.getLast();
        request.setParameter(BBBCoreConstants.ORDER, order);

		try {
			this.service(request, response);
			omnitureVO = (OrderOmnitureVO)request.getObjectParameter(BBBCoreConstants.OMNITURE_VO);

		} catch (ServletException e) {
			logError("Method: BBBOrderOmnitureDroplet.BBBOrderOmnitureDroplet, Servlet Exception while getting order Omniture VO " + e);
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_OMNITURE, "Some error occurred while getting order omniture vo");
		} catch (IOException e) {
			logError("Method: BBBOrderOmnitureDroplet.BBBOrderOmnitureDroplet, IO Exception while getting order Omniture VO " + e);
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_OMNITURE, "Some error occurred while getting order omniture vo");
		}

		return omnitureVO;
	}

	public BBBEximManager getBbbEximPricingManager() {
		return bbbEximPricingManager;
	}

	public void setBbbEximPricingManager(BBBEximManager bbbEximPricingManager) {
		this.bbbEximPricingManager = bbbEximPricingManager;
	}

}