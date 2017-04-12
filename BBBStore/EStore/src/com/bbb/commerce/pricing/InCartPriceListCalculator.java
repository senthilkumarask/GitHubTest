package com.bbb.commerce.pricing;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.Constants;
import atg.commerce.pricing.priceLists.ItemPriceCalculator;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBItemPriceInfo;
import com.bbb.utils.BBBUtility;

public class InCartPriceListCalculator extends ItemPriceCalculator {

	private BBBCatalogTools catalogTools;
	private PriceListManager priceListManager;
	private MutableRepository priceListRepository;

	/**
	 * BBBH-2410 Create Incart Pricelist and modify Item Price PreCalculator.
	 * This method is called to set salePrice of commerceItem as incartprice if
	 * in cart flag is true for the sku.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem,
			RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {

		BBBPerformanceMonitor.start("InCartPriceListCalculator", "priceItem");
			logDebug(LogMessageFormatter.formatMessage(null,
					"Start - InCartPriceListCalculator.priceItem"));
		double totalPrice = 0.0;
		Double inCartPrice = null;
		double adjustAmount = 0.0;
		PricingAdjustment adjustment;
		final String siteId = extractSiteId();
		RepositoryItem price = null;
		String inCartPriceListId = null;
		RepositoryItem priceListItem = null;
		double rawTotalPrice = 0.0;
		boolean fromRegistryOwnerPage=false;
		Map <Object,Object> parameterMap = new HashMap<Object,Object>();
		try {
			final long qty = pItem.getQuantity();
			logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId());
			if(pItem instanceof BBBCommerceItem) {
				parameterMap.put("siteId", siteId);
				parameterMap.put("skuId", pItem.getCatalogRefId());
				parameterMap.put("calculateAboveBelowLine", false);
				parameterMap.put("fromCart", true);
				SKUDetailVO skuVO = this.getCatalogTools().getSKUDynamicPriceDetails(parameterMap);
				String country = BBBInternationalShippingConstants.DEFAULT_COUNTRY;
				if (this.getCatalogTools().returnCountryFromSession() != null) {
					country = this.getCatalogTools().returnCountryFromSession();
				}
				if (skuVO.isInCartFlag()) {
					logDebug("In cart flag is true");
				// use CanadaInCartPriceList if the site is BedBathCanada.
				if (siteId.equals(BBBCoreConstants.SITE_BAB_CA)
						|| siteId.equals(BBBCoreConstants.SITEBAB_CA_TBS)) {
					inCartPriceListId = this
							.getCatalogTools()
							.getConfigValueByconfigType(
									BBBCoreConstants.CONTENT_CATALOG_KEYS)
							.get(BBBCoreConstants.SITE_BAB_CA
									+ BBBCatalogConstants.IN_CART_PRICELIST);
					}// use MexicoInCartPriceList if the country selected is Mexico.
				else if (country
						.equalsIgnoreCase(BBBCatalogConstants.MEXICO_COUNTRY)) {
					inCartPriceListId = this
							.getCatalogTools()
							.getConfigValueByconfigType(
									BBBCoreConstants.CONTENT_CATALOG_KEYS)
							.get(BBBCatalogConstants.MEXICO_CODE
									+ BBBCatalogConstants.IN_CART_PRICELIST);
				}// use USInCartPriceList if the country selected is other than
					// Mexico.
					else {
						inCartPriceListId = this
								.getCatalogTools()
								.getConfigValueByconfigType(
										BBBCoreConstants.CONTENT_CATALOG_KEYS)
										.get(BBBCoreConstants.SITE_BAB_US
												+ BBBCatalogConstants.IN_CART_PRICELIST);
					}
					if (!BBBUtility.isEmpty(inCartPriceListId)) {
						logDebug(" Price List id is: " + inCartPriceListId);
						priceListItem = getPriceListRepository()
								.getItem(
										inCartPriceListId,
										BBBInternationalShippingConstants.PROPERTY_PRICELIST);
						if (null != priceListItem) {
							price = this.getPriceListManager().getPrice(
									(RepositoryItem) priceListItem,
									pItem.getAuxiliaryData().getProductId(),
									pItem.getCatalogRefId());
							if (price != null) {
								inCartPrice = (Double) price
										.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME);

								totalPrice = inCartPrice * qty;
								totalPrice = getPricingTools().round(totalPrice);
								logDebug("rounding item price.total price: "
										+ totalPrice);
								// set sale price, raw total price and amount for
								// item price
								// info.
								rawTotalPrice=  qty * pPriceQuote.getListPrice();
								rawTotalPrice = getPricingTools().round(rawTotalPrice);
								pPriceQuote.setRawTotalPrice(rawTotalPrice);
								pPriceQuote.setAmount(totalPrice);
								pPriceQuote.setSalePrice(inCartPrice);

								//BBBH-3558 setting the incart flag to true to display message on mini cart
								/*BBBH-6242 Story 24 : Feedback : Mini Cart Behavior for Registrant view */
								if(pExtraParameters.containsKey(BBBCoreConstants.FROM_REIGISTRY_OWNER))
								{
									fromRegistryOwnerPage=Boolean.valueOf(pExtraParameters.get(BBBCoreConstants.FROM_REIGISTRY_OWNER).toString());
									
									
								}
								//if(pItem instanceof BBBCommerceItem &&!fromRegistryOwnerPage){
								
									((BBBCommerceItem) pItem).setIncartPriceItem(true);
								//}


								List detailsList = pPriceQuote
										.getCurrentPriceDetails();
								if (!detailsList.isEmpty()) {
									detailsList.clear();
								}
								logDebug("price quote: " + pPriceQuote);
								detailsList
								.addAll(getPricingTools()
										.getDetailedItemPriceTools()
										.createInitialDetailedItemPriceInfos(
												totalPrice,
												pPriceQuote,
												pItem,
												pPricingModel,
												pLocale,
												pProfile,
												pExtraParameters,
												Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION));

								List adjustments = pPriceQuote.getAdjustments();

								adjustments.clear();

							adjustment = new PricingAdjustment(
									Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION,
									null, getPricingTools().round(rawTotalPrice-totalPrice),
									pItem.getQuantity());
							adjustments.add(adjustment);
								logDebug("adjustment: " + adjustment
										+ " adjustAmount: " + adjustAmount);
							}
						}
					}
				}
			}
		} catch (final PriceListException e) {
			this.logError(
					"InCartPriceListCalculator.priceItem PriceListException", e);
		} catch (BBBSystemException e1) {
			this.logError(
					"InCartPriceListCalculator.priceItem BBBSystemException",
					e1);
		} catch (BBBBusinessException e1) {
			this.logError(
					"InCartPriceListCalculator.priceItem BBBBusinessException",
					e1);
		} catch (RepositoryException e) {
			this.logError(
					"InCartPriceListCalculator.priceItem RepositoryException",
					e);
		}

		if (pPriceQuote instanceof BBBItemPriceInfo) {
			if (pPriceQuote.getSalePrice() > 0) {
				((BBBItemPriceInfo) pPriceQuote).setCurrentPrice(pPriceQuote
						.getSalePrice());
			} else {
				((BBBItemPriceInfo) pPriceQuote).setCurrentPrice(pPriceQuote
						.getListPrice());
			}
		}

		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End- InCartPriceListCalculator.priceItem"));
		}

		BBBPerformanceMonitor.end("InCartPriceListCalculator", "priceItem");

	}

	protected String extractSiteId() {
		final String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public void logDebug(String msg) {
		if (isLoggingDebug()) {
			super.logDebug(msg);
		}
	}

	public void logError(String msg) {
		if (isLoggingError()) {
			super.logError(msg);
		}
	}

	public PriceListManager getPriceListManager() {
		return priceListManager;
	}

	public void setPriceListManager(PriceListManager priceListManager) {
		this.priceListManager = priceListManager;
	}

	public MutableRepository getPriceListRepository() {
		return priceListRepository;
	}

	public void setPriceListRepository(MutableRepository priceListRepository) {
		this.priceListRepository = priceListRepository;
	}

}
