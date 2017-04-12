package com.bbb.commerce.pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.Constants;
import atg.commerce.pricing.priceLists.ItemPriceCalculator;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.order.bean.BBBItemPriceInfo;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.utils.BBBUtility;

public class CustomPriceListCalculator extends ItemPriceCalculator {

	public static final String COMMERCE_ITEM = "commerceItem";
	public static final String DELIVERY_ITEM_ID = "deliveryItemId";
	public static final String ASSEMBLY_ITEM_ID = "assemblyItemId";
	public static final String SITE_ID = "siteId";
	public static final String LOCALE = "locale";
	private BBBCatalogTools catalogTools;

	/**
	 * This method is called to set priceinfo for LTLDeliveryChargeCommerceItem and LTLAssemblyFeeCommerceItem.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
	throws PricingException {

		BBBPerformanceMonitor.start("CustomPriceListCalculator", "priceItem");
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start - CustomPriceListCalculator.priceItem"));
		}

        double totalPrice = 0.0;
        List detailsList;
        List newDetails;
        List adjustments;
        double adjustAmount = 0.0;
        Double deliveryCharge = 0.0;
        String shipMethod = null;
        PricingAdjustment adjustment;
        String siteId = extractSiteID();
        String locale = ServletUtil.getCurrentRequest().getLocale().toString();
        //check if instance of commerce item is LTLDeliveryChargeCommerceItem then execute below code
		if(pItem instanceof LTLDeliveryChargeCommerceItem){
			if(isLoggingDebug()){
				logDebug("Item is of instance LTL Delivery charge commerce item");
			}
			CommerceItem ltlCommerceItemForSurcharge = getCommerceItemForCustomLTLCommerceItem(pItem, DELIVERY_ITEM_ID, pExtraParameters);

			if(ltlCommerceItemForSurcharge != null){
				List<ShipMethodVO> ltlShipMethodVoList = new ArrayList<ShipMethodVO>();
				try {
					//get all the eligible shipping methods vo for current commerce item to get the price according to the shipping method selected.
					ltlShipMethodVoList = this.getCatalogTools().getLTLEligibleShippingMethods(ltlCommerceItemForSurcharge.getCatalogRefId(), siteId, locale);
				} catch (BBBSystemException e) {
					this.logError("Error occurred in getting list of LTLEligibleShippingMethods", e);
				} catch (BBBBusinessException e) {
					this.logError("Error occurred in getting list of LTLEligibleShippingMethods", e);
				}
				//get the shipping group of current commerce item to get the shipping method
				BBBHardGoodShippingGroup sg = (BBBHardGoodShippingGroup) ((BBBShippingGroupCommerceItemRelationship)pItem.getShippingGroupRelationships().iterator().next()).getShippingGroup();
				shipMethod = sg.getShippingMethod();
				double qty = (double)pItem.getQuantity();
				//iterate on ltlShipMethodVo to get the delivery charge for current shipping method selected.
				for(ShipMethodVO ltlShipMethodVo : ltlShipMethodVoList){
					if (ltlShipMethodVo.getShipMethodId().equalsIgnoreCase(shipMethod)){
						deliveryCharge = ltlShipMethodVo.getDeliverySurcharge();
					}
				}
				if(isLoggingDebug()){
					logDebug("shipMethod: " + shipMethod + " quantity: " + qty + " delivery charge: " + deliveryCharge);
				}
				totalPrice = deliveryCharge * qty;
				totalPrice = getPricingTools().round(totalPrice);
				if(isLoggingDebug()){
					logDebug("rounding item price.total price: " + totalPrice);
				}
				//set list price, raw total price and amount for item price info.
				pPriceQuote.setRawTotalPrice(totalPrice);
				pPriceQuote.setListPrice(deliveryCharge);
				pPriceQuote.setAmount(totalPrice);

				detailsList = pPriceQuote.getCurrentPriceDetails();
				if(detailsList.size() > 0){
					detailsList.clear();
				}
				if(isLoggingDebug()){
					logDebug("price quote: " + pPriceQuote);
				}
				newDetails = getPricingTools().getDetailedItemPriceTools().createInitialDetailedItemPriceInfos(totalPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION);
				detailsList.addAll(newDetails);

				adjustments = pPriceQuote.getAdjustments();
				if(adjustments.size() > 0){
					adjustments.clear();
				}

				adjustAmount = totalPrice;
				adjustment = new PricingAdjustment(Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION, null, getPricingTools().round(adjustAmount), pItem.getQuantity());
				adjustments.add(adjustment);
				if(isLoggingDebug()){
					logDebug("adjustment: " + adjustment + " adjustAmount: " + adjustAmount);
				}
			}



		}//check if instance of commerce item is LTLAssemblyFeeCommerceItem then execute below code
		else if(pItem instanceof LTLAssemblyFeeCommerceItem){
			double assemblyFee = 0.0;
			CommerceItem ltlCommerceItemForAssembly = getCommerceItemForCustomLTLCommerceItem(pItem, ASSEMBLY_ITEM_ID, pExtraParameters);

			if(ltlCommerceItemForAssembly != null){
				try {
					//get assembly fee for BBBCommerce selected
					assemblyFee = this.getCatalogTools().getAssemblyCharge(siteId, ltlCommerceItemForAssembly.getCatalogRefId());
				} catch (BBBBusinessException e) {
					this.logError("Error occurred in getting LTLAssemblyCharge", e);
				} catch (BBBSystemException e) {
					this.logError("Error occurred processing LTLAssemblyCharge", e);
				}
				totalPrice = assemblyFee * (double)pItem.getQuantity();

				if(isLoggingDebug()){
					logDebug("rounding item price.");
				}
				totalPrice = getPricingTools().round(totalPrice);
				//set list price, raw total price and amount for item price info.
				pPriceQuote.setRawTotalPrice(totalPrice);
				pPriceQuote.setListPrice(assemblyFee);
				pPriceQuote.setAmount(totalPrice);
				if(isLoggingDebug()){
					logDebug("price quote: " + pPriceQuote);
				}
				detailsList = pPriceQuote.getCurrentPriceDetails();
				if(detailsList.size() > 0){
					detailsList.clear();
				}

				newDetails = getPricingTools().getDetailedItemPriceTools().createInitialDetailedItemPriceInfos(totalPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION);
				detailsList.addAll(newDetails);

				adjustments = pPriceQuote.getAdjustments();
				if(adjustments.size() > 0){
					adjustments.clear();
				}

				adjustAmount = totalPrice;
				adjustment = new PricingAdjustment(Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION, null, getPricingTools().round(adjustAmount), pItem.getQuantity());
				adjustments.add(adjustment);
				if(isLoggingDebug()){
					logDebug("adjustment: " + adjustment + " adjustAmount: " + adjustAmount);
				}
			}

		}

		if (pPriceQuote instanceof BBBItemPriceInfo) {
			((BBBItemPriceInfo)pPriceQuote).setCurrentPrice(pPriceQuote.getListPrice());
			if (pPriceQuote.getSalePrice() > 0)
				((BBBItemPriceInfo)pPriceQuote).setCurrentPrice(pPriceQuote.getSalePrice());
		}

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End- CustomPriceListCalculator.priceItem"));
		}

		BBBPerformanceMonitor.end("CustomPriceListCalculator", "priceItem");

	}



	protected String extractSiteID() {
		String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}


	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	private CommerceItem getCommerceItemForCustomLTLCommerceItem(CommerceItem customLTLCommerceItem, String deliveryOrAssembly, Map pExtraParameters){

		if(isLoggingDebug()){
			logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		}
		String ltlCommerceItemForDeliveryItem = "";
		if(deliveryOrAssembly.equalsIgnoreCase(DELIVERY_ITEM_ID)){
			if(isLoggingDebug()){
				logDebug("delivery item is present");
			}
			ltlCommerceItemForDeliveryItem = ((LTLDeliveryChargeCommerceItem)customLTLCommerceItem).getLtlCommerceItemRelation();
		}
		else if(deliveryOrAssembly.equalsIgnoreCase(ASSEMBLY_ITEM_ID)){
			if(isLoggingDebug()){
				logDebug("assembly item is present");
			}
			ltlCommerceItemForDeliveryItem = ((LTLAssemblyFeeCommerceItem)customLTLCommerceItem).getLtlCommerceItemRelation();
		}
		CommerceItem ltlCommerceItem = null;

		if(!BBBUtility.isEmpty(ltlCommerceItemForDeliveryItem)){
			logDebug("ltlCommerceItemForDeliveryItem: " + ltlCommerceItemForDeliveryItem);
			OrderHolder cart = (OrderHolder) ServletUtil.getCurrentRequest().resolveName("/atg/commerce/ShoppingCart");
			BBBOrderImpl order = null;
			if(pExtraParameters.get(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER) != null){
				order = (BBBOrderImpl) pExtraParameters.get(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER);
			} else if (pExtraParameters.get(BBBCheckoutConstants.REPRICE_SHOPPING_CART_ORDER) != null){
				order = (BBBOrderImpl) pExtraParameters.get(BBBCheckoutConstants.REPRICE_SHOPPING_CART_ORDER);
			}
			else{
				order = (BBBOrderImpl) cart.getCurrent();
			}
			logDebug("Order id: " + order.getId());
			try {
				ltlCommerceItem = order.getCommerceItem(ltlCommerceItemForDeliveryItem);
			} catch (CommerceItemNotFoundException e) {
				this.logError("No Commerce Item found for given ID", e);
			} catch (InvalidParameterException e) {
				this.logError("No Commerce Item found for given ID", e);
			}
			logDebug("ltlCommerceItem: " + ltlCommerceItem);
		}
		logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		return ltlCommerceItem;

	}

	public void logDebug(String msg){
		if (isLoggingDebug()) {
			super.logDebug(msg);
		}
	}

}
