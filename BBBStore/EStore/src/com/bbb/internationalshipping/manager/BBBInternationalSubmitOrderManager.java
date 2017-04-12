package com.bbb.internationalshipping.manager;

import static atg.commerce.states.StateDefinitions.ORDERSTATES;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.TaxPriceInfo;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.internationalshipping.pofile.BasketItem;
import com.bbb.framework.jaxb.internationalshipping.pofile.OrderDetails;
import com.bbb.framework.jaxb.internationalshipping.pofile.OrderFeed;
import com.bbb.framework.jaxb.internationalshipping.pofile.OrderId;
import com.bbb.internationalshipping.utils.BBBInternationalOrderUpdateServiceHelper;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.utils.BBBUtility;

/**
 * The Class BBBInternationalOrderUpdateService updates shipping and payment information into ATG order and submit order.
 */
public class BBBInternationalSubmitOrderManager extends BBBGenericService {

	//private static final String JVM_PROPERTY="weblogic.Name";
	/** The order manager. */
	private BBBOrderManager orderManager;

	/** The international order update service helper. */
	private BBBInternationalOrderUpdateServiceHelper internationalOrderUpdateServiceHelper;

	/** The transaction manager. */
	private TransactionManager transactionManager;

	/** The site repository. */
	private MutableRepository siteRepository;

	/**
	 * Gets the order manager.
	 *
	 * @return the order manager
	 */
	public BBBOrderManager getOrderManager() {
		return orderManager;
	}


	/**
	 * Sets the order manager.
	 *
	 * @param orderManager the new order manager
	 */
	public void setOrderManager(final BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}


	/**
	 * Gets the international order update service helper.
	 *
	 * @return the international order update service helper
	 */
	public BBBInternationalOrderUpdateServiceHelper getInternationalOrderUpdateServiceHelper() {
		return internationalOrderUpdateServiceHelper;
	}


	/**
	 * Sets the international order update service helper.
	 *
	 * @param internationalOrderUpdateServiceHelper the new international order update service helper
	 */
	public void setInternationalOrderUpdateServiceHelper(
			final BBBInternationalOrderUpdateServiceHelper internationalOrderUpdateServiceHelper) {
		this.internationalOrderUpdateServiceHelper = internationalOrderUpdateServiceHelper;
	}


	/**
	 * Gets the transaction manager.
	 *
	 * @return the transaction manager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}


	/**
	 * Sets the transaction manager.
	 *
	 * @param transactionManager the new transaction manager
	 */
	public void setTransactionManager(final TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}


	/**
	 * Gets the site repository.
	 *
	 * @return the site repository
	 */
	public MutableRepository getSiteRepository() {
		return siteRepository;
	}


	/**
	 * Sets the site repository.
	 *
	 * @param siteRepository the new site repository
	 */
	public void setSiteRepository(final MutableRepository siteRepository) {
		this.siteRepository = siteRepository;
	}
	private String dcPrefix;
	/**
	 * @return the dcPrefix
	 */
	public final String getDcPrefix() {
		return this.dcPrefix;
	}

	/**
	 * @param dcPrefix the dcPrefix to set
	 */
	public final void setDcPrefix(final String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

	/**
	 * Update international order.
	 *Process the order(update shipping ,billing and credit card details(If Order is in PESO ,then also update order prices in USD ,got from borderfree)
	 * of border free in the ATG order) if fraudstate is green or Empty,else cancel the order.
	 * @throws CommerceException the commerce exception
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	
	
	public boolean updateInternationalOrder(final OrderFeed orderFeed) throws  BBBBusinessException, BBBSystemException 
	{
		boolean isFraud=false;
	  String fraudState=orderFeed.getOrder().get(0).getFraudState() == null ? null :  orderFeed.getOrder().get(0).getFraudState();
	  if( BBBUtility.isEmpty(fraudState) || fraudState.equalsIgnoreCase(BBBInternationalShippingConstants.GREEN_PO_FILE))
	  {
		 final String orderId=((OrderId) orderFeed.getOrder().get(0).getOrderId()).getMerchantOrderRef();
		 final TransactionDemarcation td = new TransactionDemarcation();
		 boolean shouldRollback = false; 
		 try
		 {
			final BBBOrder bbbOrder= (BBBOrder) getOrderManager().loadOrder(orderId);
			if (getOrderManager().isOrderStateValidForProcessing(bbbOrder.getState())) {

				td.begin(getOrderManager().getOrderTools().getTransactionManager());
				synchronized (bbbOrder) {
					RepositoryItem   profile=getSiteSpecificProfile(bbbOrder.getSiteId());
					bbbOrder.setProfileId(profile.getRepositoryId());
					/*if(orderFeed.getOrder().get(0).getMarketing().getAddress().get(0).getCountry().equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY))//Mexico Order prices need to be updated in USD
					{*/
						OrderDetails orderdetails=orderFeed.getOrder().get(0).getDomesticBasket().getOrderDetails();
						BBBOrderPriceInfo priceinfo=(BBBOrderPriceInfo) bbbOrder.getPriceInfo();
						HardgoodShippingGroup hd =(HardgoodShippingGroup)	bbbOrder.getShippingGroups().get(0);
						//Set Order info For International Orders
						if(priceinfo.getShippingItemsSubtotalPriceInfos()!=null && priceinfo.getShippingItemsSubtotalPriceInfos().get(hd.getId())!=null){
							((OrderPriceInfo)priceinfo.getShippingItemsSubtotalPriceInfos().get(hd.getId())).setAmount(Double.valueOf(orderdetails.getTotalProductSaleValue()));
							Iterator iterator = priceinfo.getShippingItemsSubtotalPriceInfos().entrySet().iterator();
							while (iterator.hasNext()) {
								Map.Entry<String,OrderPriceInfo> mapEntry = (Map.Entry<String,OrderPriceInfo>) iterator.next();
								if(mapEntry!=null &&  mapEntry.getValue()!=null)
								{
									mapEntry.getValue().setRawSubtotal(Double.valueOf(orderdetails.getTotalProductSaleValue()));
								}
							}
						}
						priceinfo.setOnlineSubtotal(Double.valueOf(orderdetails.getTotalProductSaleValue()));
						priceinfo.setRawSubtotal(Double.valueOf(orderdetails.getTotalProductSaleValue()));
						priceinfo.setShipping(Double.valueOf(orderFeed.getOrder().get(0).getCOPShippingMethod().getDeliveryServiceType().getShippingPrice()));
						priceinfo.setAmount(Double.valueOf(orderdetails.getTotalProductSaleValue()));
						List<PricingAdjustment> adjustment=priceinfo.getAdjustments();
						if(adjustment!=null){
							for(PricingAdjustment priceAdjustment:adjustment)
							{
								if(priceAdjustment.getAdjustmentDescription().equalsIgnoreCase(BBBInternationalShippingConstants.ORDER_SUBTOTAL)){
										priceAdjustment.setTotalAdjustment(Double.valueOf(orderdetails.getTotalProductSaleValue()));
								}
							}
						}
						//set Shipping price For International Orders
						BBBShippingPriceInfo priceInfo=(BBBShippingPriceInfo)hd.getPriceInfo();
						priceInfo.setFinalShipping(Double.valueOf(orderFeed.getOrder().get(0).getCOPShippingMethod().getDeliveryServiceType().getShippingPrice()));
						priceInfo.setRawShipping(Double.valueOf(orderFeed.getOrder().get(0).getCOPShippingMethod().getDeliveryServiceType().getShippingPrice()));
						priceInfo.setAmount(Double.valueOf(orderFeed.getOrder().get(0).getCOPShippingMethod().getDeliveryServiceType().getShippingPrice()));
						//List<BBBCommerceItem> comItemRest = bbbOrder.getCommerceItems();
						CommerceItem commerceItem = null;
						//Item details For International Orders
						final List<ShippingGroupCommerceItemRelationship> commerceItemRelationships = hd.getCommerceItemRelationships();
						for(final ShippingGroupCommerceItemRelationship commerceItemRelation : commerceItemRelationships){
							commerceItem = commerceItemRelation.getCommerceItem();
							if(commerceItem instanceof BBBCommerceItem) {
								List<BasketItem> basketItem= orderFeed.getOrder().get(0).getDomesticBasket().getBasketDetails().getBasketItem();
								for(BasketItem item: basketItem)
								{
									if(item.getMerchantSKU().equalsIgnoreCase(commerceItem.getCatalogRefId())){
										if(commerceItem.getPriceInfo()!=null){
											commerceItem.getPriceInfo().setRawTotalPrice((Double.valueOf(item.getProductListPrice()))*(Integer.parseInt(item.getProductQuantity())));
											commerceItem.getPriceInfo().setListPrice(Double.valueOf(item.getProductListPrice()));
											commerceItem.getPriceInfo().setAmount((Double.valueOf(item.getProductSalePrice())*(Integer.parseInt(item.getProductQuantity()))));
											((BBBCommerceItem) commerceItem).setPrevPrice(Double.valueOf(item.getProductSalePrice()));
											List<PricingAdjustment> itemAdjustment=commerceItem.getPriceInfo().getAdjustments();
											for(PricingAdjustment priceAdjustment:itemAdjustment)
											{
												if(priceAdjustment.getAdjustmentDescription().equalsIgnoreCase(BBBInternationalShippingConstants.LIST_RPICE)){
														priceAdjustment.setTotalAdjustment(((Double.valueOf(item.getProductListPrice()))*(Integer.parseInt(item.getProductQuantity()))));
												}
												if(priceAdjustment.getAdjustmentDescription().equalsIgnoreCase(BBBInternationalShippingConstants.SALE_PRICE)){
													priceAdjustment.setTotalAdjustment((Double.valueOf(item.getProductSalePrice())*(Integer.parseInt(item.getProductQuantity())))-((Double.valueOf(item.getProductListPrice()))*(Integer.parseInt(item.getProductQuantity()))));
											}
											}
											if(commerceItem.getPriceInfo().isOnSale()){
												commerceItem.getPriceInfo().setSalePrice((Double.valueOf(item.getProductSalePrice())));
											}
											final List<DetailedItemPriceInfo> priceBeans= commerceItem.getPriceInfo().getCurrentPriceDetailsForRange(commerceItemRelation.getRange());
											for(DetailedItemPriceInfo dpi : priceBeans){
												dpi.setItemPriceInfo(commerceItem.getPriceInfo());
												dpi.setAmount(commerceItem.getPriceInfo().getAmount());
											}
									}
								 }		
								}
								
							 }
								
							}
						

				//	}
					//update shipping info
					internationalOrderUpdateServiceHelper.updateShippingInfo(bbbOrder,orderFeed);
					//update payment info
					internationalOrderUpdateServiceHelper.updatePaymentInfo(bbbOrder,orderFeed);
					//set tax price info as 0 if taxPriceInfo is null
					TaxPriceInfo taxPriceInfo=bbbOrder.getTaxPriceInfo();
					if(taxPriceInfo==null)
					{
						taxPriceInfo=new TaxPriceInfo();
						taxPriceInfo.setAmount(0);
						taxPriceInfo.setCountyTax(0);
						taxPriceInfo.setCountyTax(0);
						taxPriceInfo.setAmountIsFinal(false);
						bbbOrder.setTaxPriceInfo(taxPriceInfo);
					}
					taxPriceInfo.setCurrencyCode(BBBInternationalShippingConstants.CURRENCY_USD);
					PipelineResult result = null;
					//bbbOrder.setCreatedByOrderId(this.getDcPrefix() + "-" + System.getProperty(JVM_PROPERTY));
					getOrderManager().updateOrder(bbbOrder);
					//submitting order
					result = getOrderManager().processOrder(bbbOrder,getProcessOrderMap(getOrderManager().getDefaultLocale()));


					if (result == null || result.hasErrors()) {
						if (isLoggingError()) {
							logError("Process Pipeline Error Occurred for order ");
							for (final Object err : result.getErrors()) {
								logError(err.toString());
							}
							throw new BBBBusinessException(BBBInternationalShippingConstants.
									UPDATE_ORDER_PROCESS_ORDER_ERROR_CODE,BBBInternationalShippingConstants.
									UPDATE_ORDER_PROCESS_ORDER_MESSAGE);
						}
					} else {
						vlogDebug("Successfully Processed ---------------->"+ORDERSTATES.getStateString(bbbOrder.getState()));
						 final BBBOrderTools tools = (BBBOrderTools) this.getOrderManager().getOrderTools();
						 tools.updateOrderSubstatus(bbbOrder, BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED);
						 tools.updateInternationalOrderState(bbbOrder, BBBInternationalShippingConstants.INTL_SUBMITTED);
					}
				}
			}

		 } catch (TransactionDemarcationException exe) {
			shouldRollback = true;
			logError("TransactionDemarcationException  ..."+exe);
			throw new BBBBusinessException(BBBInternationalShippingConstants.UPDATE_ORDER_TRANSACTION_DEMARCATION_EXCEPTION_ERROR_CODE,BBBInternationalShippingConstants.UPDATE_ORDER_TRANSACTION_DEMARCATION_EXCEPTION_MESSAGE);
		 }  catch (RepositoryException e) {
			logError("RepositoryException  ..."+e);
			throw new BBBSystemException(BBBInternationalShippingConstants.UPDATE_ORDER_REPOSITORY_EXCEPTION_ERROR_CODE,BBBInternationalShippingConstants.UPDATE_ORDER_REPOSITORY_EXCEPTION_MESSAGE);
		 } catch (CommerceException e) {
			logError("CommerceException  ..."+e);
			throw new BBBBusinessException(BBBInternationalShippingConstants.UPDATE_ORDER_COMMERCE_EXCEPTION_ERROR_CODE,BBBInternationalShippingConstants.UPDATE_ORDER_COMMERCE_EXCEPTION_MESSAGE);
		 } 
		 finally
		 {
			endTransaction(td, shouldRollback);
		 }
	  }	
	  else if(fraudState.equalsIgnoreCase(BBBInternationalShippingConstants.RED_PO_FILE) || fraudState.equalsIgnoreCase(BBBInternationalShippingConstants.CANCELLED_PO_FILE))
	  {
		  final TransactionDemarcation td = new TransactionDemarcation();
		  boolean shouldRollback = false; 
		  final String orderId=((OrderId) orderFeed.getOrder().get(0).getOrderId()).getMerchantOrderRef();
			 try
			 {
				final Order bbbOrder=  getOrderManager().loadOrder(orderId);
			    // final int stateFromString = StateDefinitions.ORDERSTATES.getStateFromString(BBBInternationalShippingConstants.CANCELLED);

					td.begin(getOrderManager().getOrderTools().getTransactionManager());
					synchronized (bbbOrder) {
						final BBBOrderTools tool=(BBBOrderTools) this.getOrderManager().getOrderTools();
						this.getOrderManager().updateOrderSubstatus(bbbOrder, BBBInternationalShippingConstants.INTL_CANCELLED);
						this.getOrderManager().updateOrderState(bbbOrder, BBBInternationalShippingConstants.CANCELLED);
						tool.updateInternationalOrderState(bbbOrder, BBBInternationalShippingConstants.INTL_CANCELLED);
					}
				
			}
			 catch (TransactionDemarcationException exe) {
					shouldRollback = true;
					logError("TransactionDemarcationException  ..."+exe);
					throw new BBBBusinessException(BBBInternationalShippingConstants.UPDATE_ORDER_TRANSACTION_DEMARCATION_EXCEPTION_ERROR_CODE,BBBInternationalShippingConstants.UPDATE_ORDER_TRANSACTION_DEMARCATION_EXCEPTION_MESSAGE);
				 }  catch (CommerceException e) {
					logError("CommerceException  ..."+e);
					throw new BBBBusinessException(BBBInternationalShippingConstants.UPDATE_ORDER_COMMERCE_EXCEPTION_ERROR_CODE,BBBInternationalShippingConstants.UPDATE_ORDER_COMMERCE_EXCEPTION_MESSAGE);
				 } catch (RepositoryException e) {
					 logError("RepositoryException  ..."+e);
						throw new BBBSystemException(BBBInternationalShippingConstants.UPDATE_ORDER_REPOSITORY_EXCEPTION_ERROR_CODE,BBBInternationalShippingConstants.UPDATE_ORDER_REPOSITORY_EXCEPTION_MESSAGE);
					 } 
				 finally
				 {
					 isFraud=true;
					endTransaction(td, shouldRollback);
				 }
	  }
	  return isFraud;
	}


	/**
	 * @param td
	 * @param shouldRollback
	 * @throws BBBBusinessException
	 */
	protected void endTransaction(final TransactionDemarcation td,
			boolean shouldRollback) throws BBBBusinessException {
		try {
			td.end(shouldRollback);
		} catch (final TransactionDemarcationException tde) {
			this.logError("Transaction roll back error", tde);
			throw new BBBBusinessException(BBBInternationalShippingConstants.UPDATE_ORDER_TRANSACTION_DEMARCATION_EXCEPTION_ERROR_CODE,BBBInternationalShippingConstants.UPDATE_ORDER_TRANSACTION_DEMARCATION_EXCEPTION_MESSAGE);
		}
	}

	
	/**
	 * Gets the process order map.
	 *
	 * @param userLocale the user locale
	 * @return the process order map
	 * @throws CommerceException 
	 */
	private HashMap getProcessOrderMap(final Locale userLocale) throws CommerceException {
		HashMap pomap = null;
			pomap = getOrderManager().getProcessOrderMap(userLocale, null);
		
		return pomap;
	}
	
	/**
	 * Gets the site specific profile.
	 *
	 * @param orderSiteId the order site id
	 * @return the site specific profile
	 * @throws RepositoryException the repository exception
	 */
	public RepositoryItem getSiteSpecificProfile(final String orderSiteId) throws RepositoryException  {
		final MutableRepositoryItem profile = getOrderManager().getOrderTools().getProfileTools().getProfileRepository().createItem("user");
		RepositoryItem requestSiteInfo=null;

		requestSiteInfo = getSiteRepository().getItem(orderSiteId,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
		if (requestSiteInfo != null) {
				logDebug("ProfileSite........"+ requestSiteInfo.getRepositoryId());
				// Set the Profile Catalog property
				profile.setPropertyValue(BBBInternationalShippingConstants.PROPERTY_CATALOG, requestSiteInfo.getPropertyValue(BBBInternationalShippingConstants.PROPERTY_DEFAULT_CATALOG));

				// set the list & sale price lists on the profile.
				profile.setPropertyValue(BBBInternationalShippingConstants.PROPERTY_PRICELIST, requestSiteInfo.getPropertyValue(BBBCatalogConstants.DEFAULT_PRICE_LIST));
				profile.setPropertyValue(BBBInternationalShippingConstants.PROFILE_SALE_PRICELIST, requestSiteInfo.getPropertyValue(BBBCatalogConstants.DEFAULT_SALE_PRICE_LIST));			
		} else {
			logError("request Site info is null");
		}
		return profile;
	}
}
