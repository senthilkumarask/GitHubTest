package com.bbb.commerce.checkout.tibco;


import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.order.PaypalStatus;
import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.crypto.AESEncryptorComponent;
import com.bbb.framework.crypto.EncryptorException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.submitorder.AddressType;
import com.bbb.framework.jaxb.submitorder.AdjustmentListType;
import com.bbb.framework.jaxb.submitorder.AdjustmentType;
import com.bbb.framework.jaxb.submitorder.AssemblyFeeType;
import com.bbb.framework.jaxb.submitorder.CC3DSecureInfoType;
import com.bbb.framework.jaxb.submitorder.CCAuthErrorType;
import com.bbb.framework.jaxb.submitorder.CCAuthInfoType;
import com.bbb.framework.jaxb.submitorder.CreditCardInfoType;
import com.bbb.framework.jaxb.submitorder.EcoFeeType;
import com.bbb.framework.jaxb.submitorder.GCAuthInfoType;
import com.bbb.framework.jaxb.submitorder.GiftCardInfoType;
import com.bbb.framework.jaxb.submitorder.GiftInfoType;
import com.bbb.framework.jaxb.submitorder.ItemListType;
import com.bbb.framework.jaxb.submitorder.ItemPriceInfoType;
import com.bbb.framework.jaxb.submitorder.ItemType;
import com.bbb.framework.jaxb.submitorder.ObjectFactory;
import com.bbb.framework.jaxb.submitorder.OrderInfoAttrType;
import com.bbb.framework.jaxb.submitorder.OrderInfoType;
import com.bbb.framework.jaxb.submitorder.OrderPriceInfoType;
import com.bbb.framework.jaxb.submitorder.OrderType;
import com.bbb.framework.jaxb.submitorder.OrderTypes;
import com.bbb.framework.jaxb.submitorder.Orders;
import com.bbb.framework.jaxb.submitorder.PayPalInfoType;
import com.bbb.framework.jaxb.submitorder.PaymentInfoType;
import com.bbb.framework.jaxb.submitorder.PaymentListType;
import com.bbb.framework.jaxb.submitorder.PaymentTypes;
import com.bbb.framework.jaxb.submitorder.PcInfoType;
import com.bbb.framework.jaxb.submitorder.PosInfoType;
import com.bbb.framework.jaxb.submitorder.SchoolPromoType;
import com.bbb.framework.jaxb.submitorder.ShipSurchargeType;
import com.bbb.framework.jaxb.submitorder.ShipmentInfoType;
import com.bbb.framework.jaxb.submitorder.ShipmentListType;
import com.bbb.framework.jaxb.submitorder.ShippingInfoType;
import com.bbb.framework.jaxb.submitorder.ShippingPriceInfoType;
import com.bbb.framework.jaxb.submitorder.StoreAddressType;
import com.bbb.framework.jaxb.submitorder.StoreInfoType;
import com.bbb.framework.jaxb.submitorder.TaxInfoType;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.integration.cybersource.creditcard.CreditCardStatus;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBDetailedItemPriceInfo;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;
import com.bbb.order.bean.TrackingInfo;
import com.bbb.payment.giftcard.GiftCardStatus;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.CommonConfiguration;


public class SubmitOrderMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = -891587287147446506L;

	private JAXBContext context;

	private Marshaller marshaller;

	private BBBPricingTools mPricingTools = null;

	private BBBCatalogTools mCatalogTools = null;

	private BBBOrderManager mOrderManager = null;

	private CommonConfiguration mCommonConfiguration = null;

	private AESEncryptorComponent mEncryptorTools = null;

	private static enum OrderProcess{ONLINE, BOPUS};

	public static enum TAX_TYPE {CITY, COUNTY, DISTRICT, STATE, AMOUNT};

	private SearchStoreManager mStoreManager;

	private static final String UK_STATE_CODE_ISO = "UK";
	private static final String PUERTO_RICO_STATE_CODE_ISO = "PR";
	private static final String MEXICO_STATE_CODE_ISO = "GR";
	private static final String ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD = "Error While invoking getExpectedDeliveryDate method";
	
	private String territories;
	
	private Map<String, String> mTBSSiteIdMap;

	public String getTerritories() {
		return territories;
	}

	public void setTerritories(String territories) {
		this.territories = territories;
	}
	
	public SubmitOrderMarshaller() throws BBBSystemException {
	    try {
            this.context = JAXBContext.newInstance(Orders.class);
            this.marshaller = this.context.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (final PropertyException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1058,e.getMessage(), e);
        } catch (final JAXBException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1058,e.getMessage(), e);
        }
    }

	/**
	 * @return the pricingTools
	 */
	public final BBBPricingTools getPricingTools() {
		return this.mPricingTools;
	}

	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public final void setPricingTools(final BBBPricingTools pPricingTools) {
		this.mPricingTools = pPricingTools;
	}

	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @return the orderManager
	 */
	public final BBBOrderManager getOrderManager() {
		return this.mOrderManager;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public final void setOrderManager(final BBBOrderManager pOrderManager) {
		this.mOrderManager = pOrderManager;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public final void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the commonConfiguration
	 */
	public final CommonConfiguration getCommonConfiguration() {
		return this.mCommonConfiguration;
	}

	/**
	 * @param pCommonConf the commonConfiguration to set
	 */
	public final void setCommonConfiguration(final CommonConfiguration pCommonConf) {
		this.mCommonConfiguration = pCommonConf;
	}

	/**
	 * @return the encryptorTools
	 */
	public final AESEncryptorComponent getEncryptorTools() {
		return this.mEncryptorTools;
	}

	/**
	 * @param pEncryptorTools the encryptorTools to set
	 */
	public final void setEncryptorTools(final AESEncryptorComponent pEncryptorTools) {
		this.mEncryptorTools = pEncryptorTools;
	}

	public SearchStoreManager getStoreManager() {
		return this.mStoreManager;
	}

	public void setStoreManager(final SearchStoreManager storeManager) {
		this.mStoreManager = storeManager;
	}


    @Override
	public String marshall(final ServiceRequestIF pReqVO) throws BBBSystemException, BBBBusinessException {
    	String submitOrderXml = null;
		boolean rollback = false;
		TransactionDemarcation tranDemac = null;

		if(this.getOrderManager().isLoggingDebug()){
			this.getOrderManager().logDebug("START: Submit Order Marshaller");
		}
		
		if((pReqVO != null) && ((SubmitOrderVO) pReqVO).getOrder() != null) {

			final BBBOrder bbbOrder = ((SubmitOrderVO) pReqVO).getOrder();

			if(this.getOrderManager().isLoggingDebug()){
				this.getOrderManager().logDebug("Marshalling order [" + bbbOrder.getId() + "]");
			}

			try{
				submitOrderXml = this.getOrderAsXML(bbbOrder);
				/*Only update the order XML when CommonConfiguration.persistOrderXML=true (i.e. for DEBUGGING purpose in local env)*/
				if(!StringUtils.isEmpty(submitOrderXml) && this.getCommonConfiguration().isPersistOrderXML()) {

		            tranDemac = new TransactionDemarcation();
	                synchronized (bbbOrder) {
	                    /* Start the transaction */
	                    tranDemac.begin(this.getOrderManager().getOrderTools().getTransactionManager());

                        this.getOrderManager().updateOrderXML(bbbOrder, submitOrderXml);
                        this.getOrderManager().updateOrder(bbbOrder);
	                }
                }

                if(this.getOrderManager().isLoggingDebug()){
					this.getOrderManager().logDebug("Marshalling complete for order [" + bbbOrder.getId() + "]");
				}

			} catch (final TransactionDemarcationException e) {
               this.getOrderManager().logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +": Transaction demarcation failure while p updating Order XML", e);
            } catch (final CommerceException e) {
                this.getOrderManager().logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 +": Commerce exception while updating Order XML", e);
                rollback = true;
            } finally {
                try {
                    if (rollback) {
                        this.getOrderManager().logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +": Transaction failure while  updating Order XML. Rolling back the transaction. Will retry again");
                    }
                    /* Complete the transaction */
                    if(tranDemac != null) {
                    	tranDemac.end(rollback);
                    }
                } catch (final TransactionDemarcationException tde) {
                    this.getOrderManager().logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +": Transaction failure while  updating Order XML", tde);
                }
			}

		}

		if(this.getOrderManager().isLoggingDebug()){
			this.getOrderManager().logDebug("END: Submit Order Marshaller");
		}

        return submitOrderXml;
	}

	/**
	 * @param bbbOrder
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getOrderAsXML(final BBBOrder bbbOrder) throws BBBSystemException, BBBBusinessException {
		String orderXML = null;
		
		try{
		    final Orders orders = this.transformOrder(bbbOrder);
			orderXML = this.marshallOrders(orders);
		} catch (final JAXBException jaxbException) {
			this.logError("Error marshaling order object for order "+bbbOrder.getOnlineOrderNumber()+jaxbException);
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1059,jaxbException.getMessage(), jaxbException);
		} catch (final SAXException saxException) {
			this.logError("Error parsing the XML against schema for order "+bbbOrder.getOnlineOrderNumber()+saxException);
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1059,saxException.getMessage(), saxException);
		}
		return orderXML;
	}
	
	protected String marshallOrders(Orders orders) throws SAXException, JAXBException{
		Schema schema = null;
		String orderXML = null;
		SchemaFactory sf = null;
		StringWriter stringWriter = null;
		
		try{
			sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = sf.newSchema(SubmitOrderMarshaller.class.getResource("/BBBSubmitOrder.xsd"));
			this.marshaller.setSchema(schema);
			stringWriter = new StringWriter();
			this.marshaller.marshal(orders, stringWriter);
			orderXML = stringWriter.getBuffer().toString();
		}finally{
			if(stringWriter != null) {
				try{
					stringWriter.close();
				}catch (final IOException e) {
					this.getOrderManager().logError(e);
				}finally{
					stringWriter = null;
				}
			}
		}
		return orderXML;
	}

	
	/**
	 * @param bbbOrder
	 * @param profile
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Orders transformOrder(final BBBOrder bbbOrder) throws BBBSystemException, BBBBusinessException {
		/* Identify if it is ONLINE/BOPUS/HYBRID order */
		final BBBOrder.OrderType orderType = bbbOrder.getOrderType();
		final ObjectFactory factory = new ObjectFactory();
		final Orders orders = factory.createOrders();
		final List<OrderType> orderList = orders.getOrder();

		if(BBBOrder.OrderType.ONLINE.equals(orderType)){
			final OrderType onlineOrder = this.createOrder(bbbOrder, OrderProcess.ONLINE, factory);
			orderList.add(onlineOrder);
		} else if(BBBOrder.OrderType.BOPUS.equals(orderType)){
			final OrderType bopusOrder = this.createOrder(bbbOrder, OrderProcess.BOPUS, factory);
			orderList.add(bopusOrder);
		} else {
			final OrderType onlineOrder = this.createOrder(bbbOrder, OrderProcess.ONLINE, factory);
			orderList.add(onlineOrder);
			final OrderType bopusOrder = this.createOrder(bbbOrder, OrderProcess.BOPUS, factory);
			orderList.add(bopusOrder);
		}
		return orders;
	}

	private OrderType createOrder(final BBBOrder pOrder, final OrderProcess pOrderProcess, final ObjectFactory factory) throws BBBSystemException, BBBBusinessException{
		final OrderType order = factory.createOrderType();

		/*Set Order Info details*/
		order.setOrderInfo(this.populateOrderInfo(pOrder, pOrderProcess, factory));

		/*Set OrderPriceInfo details*/
		if(pOrder.getPriceInfo() != null) {
			order.setOrderPriceInfo(this.populatePriceInfo(pOrder, pOrderProcess, factory));
		}
		/*Set OrderTaxInfo details*/
		if(OrderProcess.ONLINE.equals(pOrderProcess) && (pOrder.getTaxPriceInfo() != null)) {
			order.setOrderTaxInfo(this.populateTaxInfo(pOrder.getTaxPriceInfo(), 1.0, false, new HashMap<TAX_TYPE, BigDecimal>(), factory));
		}

		/*Set BillingAddress details*/
		if(pOrder.getBillingAddress() != null) {
			order.setBillingAddress(this.populateAddress(pOrder.getBillingAddress(), pOrderProcess, factory));
		}

		/*Set Shipment List details*/
		if((pOrder.getShippingGroups() != null) && (pOrder.getShippingGroupCount() > 0)){
			order.setShipmentList(this.populateShipping(pOrder, pOrderProcess, factory));
		}

		/*Set Payment List details*/
		if((pOrder.getPaymentGroupRelationships() != null) && (pOrder.getPaymentGroupRelationshipCount() > 0)){
			final PaymentListType paymentListType = this.populatePaymentList(pOrder, pOrderProcess, factory);
			if (!paymentListType.getPaymentInfo().isEmpty()) {
				order.setPaymentList(this.populatePaymentList(pOrder, pOrderProcess, factory));
			}
		}

		return order;
	}

	private OrderInfoType populateOrderInfo(final BBBOrder pOrder, final OrderProcess pOrderProcess, final ObjectFactory factory) throws BBBSystemException, BBBBusinessException{

		RepositoryItem pProfile;
		try {
			pProfile = this.getOrderManager().getOrderTools().getProfileTools().getProfileForOrder(pOrder);
		} catch (final RepositoryException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1065,"Error while retrieving profile from order" , e);
		}
		final OrderInfoType orderInfo = factory.createOrderInfoType();
		orderInfo.setAtgOrderId(pOrder.getId());
		if(OrderProcess.ONLINE.equals(pOrderProcess)) {
			orderInfo.setOrderNumber(pOrder.getOnlineOrderNumber());
			orderInfo.setOrderType(OrderTypes.DELIVERY);
		} else if(OrderProcess.BOPUS.equals(pOrderProcess)) {
			orderInfo.setOrderNumber(pOrder.getBopusOrderNumber());
			orderInfo.setOrderType(OrderTypes.BOPUS);
		}
		//Change for TBSNext Orders
		orderInfo.setTbsStoreNo(pOrder.getTbsStoreNo());
		
		boolean gsCart = false;
		
		List<CommerceItem> items = pOrder.getCommerceItems();
		for (CommerceItem cItem : items) {
			if (cItem instanceof TBSCommerceItem) {
				TBSCommerceItem tbsItem = (TBSCommerceItem) cItem;
				if(!StringUtils.isBlank(tbsItem.getGsOrderId())){
					gsCart = true;
					break;
				}
			}
		}
		
		if(gsCart){
			orderInfo.setCartCreate("GSOrder");
			orderInfo.setCartFinal("GSOrder");
		} else {
			orderInfo.setCartCreate(pOrder.getOriginOfOrder());
			orderInfo.setCartFinal(pOrder.getSalesChannel());
			orderInfo.setSalesOS(pOrder.getSalesOS());
		}
		
		orderInfo.setAtgProfileId(pOrder.getProfileId());
		orderInfo.setIpAddress(pOrder.getUserIP());
		// setting mom365 affiliate
		orderInfo.setAffiliate(pOrder.getAffiliate());
		String memberID = null;
		if(pProfile != null) {
		    memberID = this.getMemberID(pOrder, pProfile);
		}
		if(!StringUtils.isBlank(memberID)){
			orderInfo.setMbrNum(memberID);
		}
		
		String siteId = getTBSSiteIdMap().get(pOrder.getSiteId());
		if(StringUtils.isBlank(siteId)){
			siteId = pOrder.getSiteId();
		}
		
		orderInfo.setSiteId(siteId);
		final XMLGregorianCalendar xmlGregCal = this.getXMLCalendar(pOrder.getSubmittedDate());
		orderInfo.setOrderDate(xmlGregCal);
		if(pOrder.getPriceInfo() != null) {
			orderInfo.setCurrencyCode(pOrder.getPriceInfo().getCurrencyCode());
		}

		orderInfo.setSchoolPromo(this.populateSchoolPromoType(pOrder, pOrderProcess, factory));
		//Start: 83-U - PayPal - Order XML changes
		OrderInfoAttrType orderInfoAttr=factory.createOrderInfoAttrType();
		orderInfoAttr.setDeviceFingerprint(((BBBOrderImpl)pOrder).getDeviceFingerprint());
		//Change for TBSNext Orders
		orderInfoAttr.setApproverId(pOrder.getTBSApproverID());
		orderInfoAttr.setStoreAssociateId(pOrder.getTBSAssociateID());
		String lSiteId = pOrder.getSiteId();
		if (TBSConstants.SITE_TBS_BAB_US.equals(lSiteId) || TBSConstants.SITE_TBS_BBB.equals(lSiteId)
				|| TBSConstants.SITE_TBS_BAB_CA.equals(lSiteId)) {
			orderInfoAttr.setOrderIdentifier("TBS");
		}
		orderInfo.setOrderInfoAttr(orderInfoAttr);
		//End: 83-U - PayPal - Order XML changes
		//Change for international shipping
		if(isInternationalOrder(pOrder.getSalesChannel()))
		{
			orderInfo.setTaxExemptId(getTaxExemptID());
		}
		
		List<ShippingGroup> shippingGroups = pOrder.getShippingGroups();
		TBSShippingInfo tbsShippingInfo = null;
		BBBStoreShippingGroup ssg = null;
		BBBHardGoodShippingGroup hsg = null;
		
		for (ShippingGroup shippingGroup : shippingGroups) {
			if (shippingGroup instanceof BBBHardGoodShippingGroup) {
				hsg = (BBBHardGoodShippingGroup) shippingGroup;
			} else if (shippingGroup instanceof BBBStoreShippingGroup) {
				ssg = (BBBStoreShippingGroup) shippingGroup;
			}
			if(hsg != null){
				tbsShippingInfo = hsg.getTbsShipInfo();
			} else if(ssg != null){
				tbsShippingInfo = ssg.getTbsShipInfo();
			}
			if(tbsShippingInfo != null && tbsShippingInfo.isTaxOverride()){
				orderInfo.setTaxExemptId(tbsShippingInfo.getTaxExemptId());
				break;
			}
		}
		
		//Fix for BBBSL-2662 - Start
		orderInfo.setEmailSignUp(((BBBOrderImpl) pOrder).isEmailSignUp());
		//Fix for BBBSL-2662 - End
		return orderInfo;
	}


	private SchoolPromoType populateSchoolPromoType(final BBBOrder pOrder, final OrderProcess pOrderProcess, final ObjectFactory factory) throws BBBSystemException, BBBBusinessException{
		final SchoolPromoType schoolPromo = factory.createSchoolPromoType();

		schoolPromo.setSchoolID(pOrder.getSchoolId());
		if(OrderProcess.ONLINE.equals(pOrderProcess)) {
			schoolPromo.setPromoCode(pOrder.getSchoolCoupon());
		}

		if(StringUtils.isBlank(schoolPromo.getPromoCode()) && StringUtils.isBlank(schoolPromo.getSchoolID())){
			return null;
		}

		return schoolPromo;
	}

	@SuppressWarnings("unchecked")
    private OrderPriceInfoType populatePriceInfo(final BBBOrder pOrder, final OrderProcess pOrderProcess, final ObjectFactory pFactory) throws BBBSystemException, BBBBusinessException{
		final OrderPriceInfoType orderPriceInfo = pFactory.createOrderPriceInfoType();

		final OrderPriceInfo pPriceInfo = pOrder.getPriceInfo();
		if(pPriceInfo instanceof BBBOrderPriceInfo) {
			final BBBOrderPriceInfo bbbPriceInfo = (BBBOrderPriceInfo)pPriceInfo;

			if(OrderProcess.ONLINE.equals(pOrderProcess)) {
				orderPriceInfo.setOrderTotal(this.round(bbbPriceInfo.getTotal()));

				double totalShipping = 0.0;
				double totalSurcharge = 0.0;
				double totalGiftWrapCost = 0.0;
				// LTL Add Delivery Surcharge 
				double totalDeliveryCharge  = 0.0; 
			    
				GiftWrapCommerceItem giftWrapItem = null;
				BBBShippingPriceInfo bbbShippingPriceInfo = null;
				BBBHardGoodShippingGroup hardGoodShippingGroup = null;
				final List<ShippingGroup> shippingList = pOrder.getShippingGroups();
				for(final ShippingGroup shippingGroup : shippingList){
					if(shippingGroup instanceof BBBHardGoodShippingGroup){
						hardGoodShippingGroup = (BBBHardGoodShippingGroup)shippingGroup;
						bbbShippingPriceInfo = (BBBShippingPriceInfo) hardGoodShippingGroup.getPriceInfo();

						totalShipping += bbbShippingPriceInfo.getAmount();
						//check for international order surcharge on
						if(!isInternationalOrder(pOrder.getSalesChannel()) || (isInternationalOrder(pOrder.getSalesChannel()) && isInternationalSurchargeOn()) )
						{
							totalSurcharge += bbbShippingPriceInfo.getFinalSurcharge();
						}
						giftWrapItem = hardGoodShippingGroup.getGiftWrapCommerceItem();
						if(giftWrapItem != null) {
							totalGiftWrapCost += giftWrapItem.getPriceInfo().getAmount();
						}
					}
				}
				// To calculate Delivery Surcharge at order level 
				final List commerceItems = pOrder.getCommerceItems();
				if(commerceItems != null) {
	    			for (final Iterator iterator = commerceItems.iterator(); iterator
	                        .hasNext();) {
	                    final CommerceItem item = (CommerceItem) iterator.next();
	                    if (item != null) {
	                        if (item instanceof LTLDeliveryChargeCommerceItem) {
	                        	totalDeliveryCharge += item.getPriceInfo().getAmount();
	            			}
	                    }
	                }
				}

				if(totalShipping > 0.0){
					orderPriceInfo.setTotalShipping(this.round(totalShipping));
				}
				if(totalSurcharge > 0.0){
					orderPriceInfo.setTotalSurcharge(this.round(totalSurcharge));
				}
				if(totalGiftWrapCost > 0.0){
					orderPriceInfo.setTotalGiftWrapCost(this.round(totalGiftWrapCost));
				}
				//Delivery Surcharge at order level
				if(totalDeliveryCharge > 0.0){
					logDebug("Delivery Surcharge for LTL order "+pOrder.getOnlineOrderNumber()+ " :"+totalDeliveryCharge);
					orderPriceInfo.setTotalDeliveryCharge(this.round(totalDeliveryCharge));
				}
				
			} else if(OrderProcess.BOPUS.equals(pOrderProcess) && (bbbPriceInfo.getStoreSubtotal() > 0.0)){
					orderPriceInfo.setOrderTotal(this.round(bbbPriceInfo.getStoreSubtotal()));
			}
		}

		return orderPriceInfo;
	}

	private TaxInfoType populateTaxInfo(final TaxPriceInfo pTaxPriceInfo, double pRatio, boolean pIsLast, Map<TAX_TYPE, BigDecimal> pTaxMap, final ObjectFactory pFactory) throws BBBSystemException, BBBBusinessException{
		TaxInfoType taxInfo = null;

		if(pTaxMap == null) {
			pRatio = 1.0;
			pIsLast = false;
			pTaxMap = new HashMap<TAX_TYPE, BigDecimal>();
		}

		BigDecimal remainingTax = null;
		if(pTaxPriceInfo != null) {
			taxInfo = pFactory.createTaxInfoType();

			if(pTaxPriceInfo.getCityTax() > 0.0){
				remainingTax = pTaxMap.get(TAX_TYPE.CITY);
				if(pIsLast) {
					taxInfo.setCityTax(this.round(remainingTax.doubleValue()));
				} else {
					taxInfo.setCityTax(this.round(pTaxPriceInfo.getCityTax() * pRatio));

					if(remainingTax != null){
						remainingTax = BigDecimal.valueOf(remainingTax.doubleValue() - taxInfo.getCityTax().doubleValue());
					}
					pTaxMap.put(TAX_TYPE.CITY, remainingTax);
				}
			}
			if(pTaxPriceInfo.getCountyTax() > 0.0){
				remainingTax = pTaxMap.get(TAX_TYPE.COUNTY);
				if(pIsLast){
					taxInfo.setCountyTax(this.round(remainingTax.doubleValue()));
				} else {
					taxInfo.setCountyTax(this.round(pTaxPriceInfo.getCountyTax() * pRatio));

					if(remainingTax != null){
						remainingTax = BigDecimal.valueOf(remainingTax.doubleValue()- taxInfo.getCountyTax().doubleValue());
					}
					pTaxMap.put(TAX_TYPE.COUNTY, remainingTax);
				}
			}
			if(pTaxPriceInfo.getDistrictTax() > 0.0){
				remainingTax = pTaxMap.get(TAX_TYPE.DISTRICT);
				if(pIsLast){
					taxInfo.setDistrictTax(this.round(remainingTax.doubleValue()));
				} else {
					taxInfo.setDistrictTax(this.round(pTaxPriceInfo.getDistrictTax() * pRatio));

					if(remainingTax != null){
						remainingTax = BigDecimal.valueOf(remainingTax.doubleValue() - taxInfo.getDistrictTax().doubleValue());
					}
					pTaxMap.put(TAX_TYPE.DISTRICT, remainingTax);
				}
			}
			if(pTaxPriceInfo.getStateTax() > 0.0){
				remainingTax = pTaxMap.get(TAX_TYPE.STATE);
				if(pIsLast){
					taxInfo.setStateTax(this.round(remainingTax.doubleValue()));
				} else {
					taxInfo.setStateTax(this.round(pTaxPriceInfo.getStateTax() * pRatio));

					if(remainingTax != null){
						remainingTax = BigDecimal.valueOf(remainingTax.doubleValue() - taxInfo.getStateTax().doubleValue());
					}
					pTaxMap.put(TAX_TYPE.STATE, remainingTax);
				}
			}
			if(pTaxPriceInfo.getAmount() > 0.0){
				remainingTax = pTaxMap.get(TAX_TYPE.AMOUNT);
				if(pIsLast){
					taxInfo.setTotalTax(this.round(remainingTax.doubleValue()));
				} else {
					taxInfo.setTotalTax(this.round(pTaxPriceInfo.getAmount() * pRatio));

					if(remainingTax != null){
						remainingTax = BigDecimal.valueOf(remainingTax.doubleValue() - taxInfo.getTotalTax().doubleValue());
					}
					pTaxMap.put(TAX_TYPE.AMOUNT, remainingTax);
				}
			}
		}
		if((taxInfo != null) && (taxInfo.getTotalTax() != null) && (taxInfo.getTotalTax().doubleValue() > 0.0)) {
			return taxInfo;
		} else {
			return null;
		}

	}
	
	/**
	 * This method is used to populate taxinfo's 
	 * without proration.
	 * @param repositoryItem
	 * @param pFactory
	 * @return TaxInfoType
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private TaxInfoType populateTaxInfoFromRepository (final RepositoryItem repositoryItem, final ObjectFactory pFactory
			) throws BBBSystemException, BBBBusinessException{
		TaxInfoType taxInfo = null;
		if (repositoryItem == null) {
			return null;
		}
		Double totalAmount = 0.0;
		taxInfo = pFactory.createTaxInfoType();
		if(taxInfo != null){
			if((Double)(repositoryItem.getPropertyValue(BBBCoreConstants.CITY_TAX)) != null
					&& (Double)(repositoryItem.getPropertyValue(BBBCoreConstants.CITY_TAX)) > 0.0){
				taxInfo.setCityTax(BigDecimal.valueOf((Double)(repositoryItem.getPropertyValue(BBBCoreConstants.CITY_TAX))));
				totalAmount += (Double)(repositoryItem.getPropertyValue(BBBCoreConstants.CITY_TAX));
			}
			if((Double)(repositoryItem.getPropertyValue(BBBCoreConstants.COUNTY_TAX)) != null
					&& (Double)(repositoryItem.getPropertyValue(BBBCoreConstants.COUNTY_TAX)) > 0.0){
				taxInfo.setCountyTax(BigDecimal.valueOf((Double)(repositoryItem.getPropertyValue(BBBCoreConstants.COUNTY_TAX))));
				totalAmount += (Double)(repositoryItem.getPropertyValue(BBBCoreConstants.COUNTY_TAX));
			}
			if((Double)(repositoryItem.getPropertyValue(BBBCoreConstants.DISTRICT_TAX)) != null
					&& (Double)(repositoryItem.getPropertyValue(BBBCoreConstants.DISTRICT_TAX)) > 0.0){
				taxInfo.setDistrictTax(BigDecimal.valueOf((Double)(repositoryItem.getPropertyValue(BBBCoreConstants.DISTRICT_TAX))));
				totalAmount += (Double)(repositoryItem.getPropertyValue(BBBCoreConstants.DISTRICT_TAX));
			}
			if((Double)(repositoryItem.getPropertyValue(BBBCoreConstants.STATE_TAX)) != null
					&& (Double)(repositoryItem.getPropertyValue(BBBCoreConstants.STATE_TAX)) > 0.0){
				taxInfo.setStateTax(BigDecimal.valueOf((Double)(repositoryItem.getPropertyValue(BBBCoreConstants.STATE_TAX))));
				totalAmount += (Double)(repositoryItem.getPropertyValue(BBBCoreConstants.STATE_TAX));
			}
			taxInfo.setTotalTax(round(totalAmount));
		} else {
			return null;
		}
		if((taxInfo.getTotalTax() != null) && (taxInfo.getTotalTax().doubleValue() > 0.0)) {
			return taxInfo;
		} else {
			return null;
		}
	}
	
	private AddressType populateAddress(final Address pAddress, final OrderProcess pOrderProcess, final ObjectFactory pFactory) throws BBBSystemException, BBBBusinessException{
		final AddressType address = pFactory.createAddressType();

		if(!StringUtils.isBlank(pAddress.getFirstName())){
			address.setFirstName(pAddress.getFirstName());
		}
		if(!StringUtils.isBlank(pAddress.getMiddleName())){
			address.setMiddleName(pAddress.getMiddleName());
		}
		if(!StringUtils.isBlank(pAddress.getLastName())){
			address.setLastName(pAddress.getLastName());
		}
		if(!StringUtils.isBlank(pAddress.getAddress1())){
			address.setAddressLine1(pAddress.getAddress1());
		}
		if(!StringUtils.isBlank(pAddress.getAddress2())){
			address.setAddressLine2(pAddress.getAddress2());
		}
		if(!StringUtils.isBlank(pAddress.getCity())){
			address.setCity(pAddress.getCity());
		}

		if(!StringUtils.isBlank(pAddress.getPostalCode())){
			address.setZipCode(pAddress.getPostalCode());
		}

		if(!StringUtils.isBlank(pAddress.getCountry())){
			List<String> territoriesList = new ArrayList<String>(Arrays.asList(getTerritories().split(",")));
			if(territoriesList.contains(pAddress.getState())){
				address.setCountryCode(pAddress.getState());
			}else{
				address.setCountryCode(pAddress.getCountry());
			}
		}

		if(!StringUtils.isBlank(pAddress.getState())){
		    	if(!StringUtils.isBlank(pAddress.getCountry())){
		    	    if(pAddress.getCountry().equals("GB")){
		    		address.setState(UK_STATE_CODE_ISO);
		    	    }else if (pAddress.getCountry().equals("MX")){
		    		address.setState(MEXICO_STATE_CODE_ISO);
		    	    }else if (pAddress.getCountry().equals("PR")){
		    		address.setState(PUERTO_RICO_STATE_CODE_ISO);
		    	    }else{
		    		address.setState(pAddress.getState());
		    	    }
		    	}
		}

			if(pAddress instanceof BBBRepositoryContactInfo) {
				final BBBRepositoryContactInfo contactInfo = (BBBRepositoryContactInfo) pAddress;
				if(!StringUtils.isBlank(contactInfo.getCompanyName())){
					address.setCompanyName(contactInfo.getCompanyName().trim());
				}
				if(!StringUtils.isBlank(contactInfo.getEmail())){
					address.setEmailAddress(contactInfo.getEmail());
				}
				if(!StringUtils.isBlank(contactInfo.getPhoneNumber())){
					address.setHomePhoneNumber(contactInfo.getPhoneNumber());
				} else if(!StringUtils.isBlank(contactInfo.getMobileNumber())){
					address.setHomePhoneNumber(contactInfo.getMobileNumber());
				}
				// Alternate Phone number in case of LTL item else null
				if(!StringUtils.isBlank(contactInfo.getAlternatePhoneNumber())){
					address.setWorkPhoneNumber(contactInfo.getAlternatePhoneNumber());
				}
		}

		return address;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private ShipmentListType populateShipping(final BBBOrder pOrder, final OrderProcess pOrderProcess, final ObjectFactory pFactory) throws BBBSystemException, BBBBusinessException{
		final List<ShippingGroup> shippingList = pOrder.getShippingGroups();
		final BBBOrderPriceInfo orderPriceInfo = (BBBOrderPriceInfo) pOrder.getPriceInfo();
		final Map subtotalPriceInfos = orderPriceInfo.getShippingItemsSubtotalPriceInfos();
		final boolean taxFailure = pOrder.isTaxCalculationFailure();
		BBBShippingPriceInfo priceInfo = null;
		OrderPriceInfo subtotalPriceInfo = null;
		BBBHardGoodShippingGroup hardGoodShipping = null;
		final ShipmentListType shipmentList = pFactory.createShipmentListType();
		final List<ShipmentInfoType> shipmentInfoList = shipmentList.getShipmentInfo();
		//tbs changes start
		List<CommerceItem> citems = pOrder.getCommerceItems();
		boolean cmoflag = false;
		if(OrderProcess.ONLINE.equals(pOrderProcess)){
			for (CommerceItem cItem : citems) {
				if(cItem instanceof TBSCommerceItem){
					if(((TBSCommerceItem)cItem).isCMO()){
						cmoflag = true;
						break;
					}
				}
			}
		}
		//tbs changes end
		for(int index = 0; index < shippingList.size(); index++){
			final ShippingGroup shippingGroup = shippingList.get(index);
			double subtotalAmount = 0.0;
			Double shipDeliverySurcharge = 0.0;
			//tbs changes start
			if(cmoflag && shippingGroup instanceof BBBStoreShippingGroup){
				//populateStoreShipInfo(pOrder, pOrderProcess, pFactory, subtotalPriceInfos, shipmentInfoList, shippingGroup, subtotalAmount);
				populateCMOShipInfo(pOrder, pOrderProcess, pFactory, subtotalPriceInfos, shipmentInfoList, shippingGroup, subtotalAmount);
				//tbs changes end
			} else if(OrderProcess.ONLINE.equals(pOrderProcess) && (shippingGroup instanceof BBBHardGoodShippingGroup)){
				hardGoodShipping = (BBBHardGoodShippingGroup)shippingGroup;
				priceInfo = (BBBShippingPriceInfo)hardGoodShipping.getPriceInfo();

				final ShipmentInfoType shipmentInfo = pFactory.createShipmentInfoType();
				final ShippingInfoType shippingInfo = pFactory.createShippingInfoType();
				shipmentInfo.setAtgShippingGroupId(shippingGroup.getId());

				shippingInfo.setShippingMethod(shippingGroup.getShippingMethod());
				if(hardGoodShipping.getShipOnDate() != null) {
					shippingInfo.setShipOnDate(this.getXMLCalendarShipOnDate(hardGoodShipping.getShipOnDate()));
				}
				if(!isInternationalOrder(pOrder.getSalesChannel()) && !StringUtils.isBlank(hardGoodShipping.getRegistryId())){
					shippingInfo.setRegistryId(hardGoodShipping.getRegistryId());
				}
				shippingInfo.setShippingAddress(this.populateAddress(hardGoodShipping.getShippingAddress(), pOrderProcess, pFactory));
				/*Set the email address for shipment notification in case of Registry opt-in*/
				if(hardGoodShipping.isSendShippingConfirmation() && !StringUtils.isBlank(hardGoodShipping.getShippingConfirmationEmail())){
					shippingInfo.getShippingAddress().setEmailAddress(hardGoodShipping.getShippingConfirmationEmail());
				}
				//LTL Commented following code because it was setting null for all case.
				/*
				if(shippingInfo.getShippingAddress() != null) {
					shippingInfo.getShippingAddress().setHomePhoneNumber(null);
				}
				*/
				final ShippingPriceInfoType shippingPriceInfo = pFactory.createShippingPriceInfoType();

				shippingPriceInfo.setShippingRawAmount(this.round(priceInfo.getRawShipping()));
				shippingPriceInfo.setShippingAmount(this.round(priceInfo.getFinalShipping()));
				subtotalAmount += shippingPriceInfo.getShippingAmount().doubleValue();
				//check for international order surcharge on
				if(!isInternationalOrder(pOrder.getSalesChannel()) || (isInternationalOrder(pOrder.getSalesChannel()) && isInternationalSurchargeOn()) )
				{
					shippingPriceInfo.setSurcharge(this.round(priceInfo.getSurcharge()));
					shippingPriceInfo.setFinalSurcharge(this.round(priceInfo.getFinalSurcharge()));
				}
				else
				{
					shippingPriceInfo.setSurcharge(BigDecimal.valueOf(0.0));
					shippingPriceInfo.setFinalSurcharge(BigDecimal.valueOf(0.0));
				}
				subtotalAmount += shippingPriceInfo.getFinalSurcharge().doubleValue();

				if(priceInfo.getAdjustments().size() > 0) {
					final List<PricingAdjustment> adjustments = priceInfo.getAdjustments();
					//Changes for TBSNext Orders
					shippingPriceInfo.setAdjustmentList(this.populateAdjustmentList(adjustments, pFactory, pOrder, hardGoodShipping));
				}
				
				TBSShippingInfo lTbsShipInfo = hardGoodShipping.getTbsShipInfo();
				
				if(lTbsShipInfo != null && lTbsShipInfo.isShipPriceOverride()) {
					AdjustmentListType lAdjustmentList = shippingPriceInfo.getAdjustmentList();
					
					if(lAdjustmentList == null){
						lAdjustmentList = pFactory.createAdjustmentListType();
					}
					
					List<AdjustmentType> priceAdjustments = lAdjustmentList.getAdjustment();
					
					AdjustmentType orderPriceAdjustment = pFactory.createAdjustmentType();
					
					orderPriceAdjustment.setDiscountAmount(this.round(subtotalAmount - Math.abs(lTbsShipInfo.getShipPriceValue())));
					orderPriceAdjustment.setAtgPromotionId("PUBpromo999999");
					orderPriceAdjustment.setAdjustmentDesc("Override");
					orderPriceAdjustment.setAdjustmentType("Override");
					
					orderPriceAdjustment.setShipFeeOverride(BigDecimal.valueOf(lTbsShipInfo.getShipPriceValue()));
					orderPriceAdjustment.setShippingFeeOverrideReasonCode(lTbsShipInfo.getShipPriceReason());
					
					priceAdjustments.add(orderPriceAdjustment);
					shippingPriceInfo.setAdjustmentList(lAdjustmentList);
				}
				
				
				
				// LTL Prorated Delivery Surcharge at shipping level
				List<BBBShippingGroupCommerceItemRelationship> sgciRelList = hardGoodShipping.getCommerceItemRelationships();
				for(BBBShippingGroupCommerceItemRelationship sgciRel : sgciRelList){
					if(sgciRel.getCommerceItem() instanceof BBBCommerceItem){
						if(((BBBCommerceItem)sgciRel.getCommerceItem()).isLtlItem()){
							PriceInfoVO ciPriceInfoVo = new PriceInfoVO();
							String commerceItem = sgciRel.getCommerceItem().getId();
							ciPriceInfoVo = ((BBBCommerceItemManager) getOrderManager()
									.getCommerceItemManager()).getLTLItemPriceInfo(
									commerceItem, ciPriceInfoVo, pOrder);
							shipDeliverySurcharge+=ciPriceInfoVo.getDeliverySurchargeProrated();
						}else{
							break;
						}
					}
				}
				if(shipDeliverySurcharge > 0.0) {
					logDebug("Delivery Surcharge for LTL shippingmethod "+shippingGroup.getShippingMethod()+ " :"+shipDeliverySurcharge);
					shippingPriceInfo.setShipDeliverySurcharge(round(shipDeliverySurcharge));
				}
				if(taxFailure) {
					shippingInfo.setTaxCalculationFailure(true);
				} else {
					final Map<String, TaxPriceInfo> itemTaxPriceInfos = this.getShippingGroupLevelItemTaxPriceInfos(pOrder, hardGoodShipping);
					shippingPriceInfo.setShippingTaxInfo(this.populateTaxInfo(itemTaxPriceInfos.get(BBBCoreConstants.SHIPPING_TAX_PRICE_INFO_KEY), 1.0, false, new HashMap<TAX_TYPE, BigDecimal>(), pFactory));
				}
				shippingInfo.setShippingPriceInfo(shippingPriceInfo);
				if(pOrder.getTaxPriceInfo() != null) {
					final TaxPriceInfo shippingTax =  (TaxPriceInfo) pOrder.getTaxPriceInfo().getShippingItemsTaxPriceInfos().get(shippingGroup.getId());
					if(shippingTax != null) {
						shippingInfo.setTaxInfo(this.populateTaxInfo(shippingTax, 1.0, false, new HashMap<TAX_TYPE, BigDecimal>(), pFactory));
						if((shippingInfo.getTaxInfo() != null) && (shippingInfo.getTaxInfo().getTotalTax() != null)) {
							subtotalAmount += shippingInfo.getTaxInfo().getTotalTax().doubleValue();
						}
					}
				}

				/*Set the gift wrap item*/
				shippingInfo.setGiftInfo(this.populateGiftInfo( pOrder, hardGoodShipping, pOrderProcess, pFactory));
				shipmentInfo.setShippingInfo(shippingInfo);
				//BBBH-2899: order XML change
				if(shippingGroup.getShippingMethod().equalsIgnoreCase(BBBCoreConstants.SDD) && hardGoodShipping.getSddStoreId()!=null){
					StoreInfoType storeInfoType = populateStoreInfoType(pOrder, pFactory, hardGoodShipping.getSddStoreId());
					shipmentInfo.setStoreInfo(storeInfoType);
				
				}
				//BBBH-2899: order XML change ends				
				subtotalPriceInfo = (OrderPriceInfo)subtotalPriceInfos.get(shippingGroup.getId());
				if(subtotalPriceInfo!=null)
				{
					subtotalAmount += subtotalPriceInfo.getAmount();
				}
				shipmentInfo.setItemList(this.populateItems(pOrder, shippingGroup, pOrderProcess, pFactory));
				shipmentInfo.setSubTotal(this.round(subtotalAmount));
				//BBBH-2899: order XML change
				if(hardGoodShipping.getShippingMethod().equalsIgnoreCase(BBBCoreConstants.SDD)){
				Set<TrackingInfo> trackingInfos=null;
				trackingInfos = hardGoodShipping.getTrackingInfos();
				if(null !=trackingInfos){
				Iterator iterator = trackingInfos.iterator(); 
				   while (iterator.hasNext()){
					   TrackingInfo  trackingInfo =   (TrackingInfo) iterator.next();
					   if(trackingInfo!=null && trackingInfo.getTrackingNumber()!=null){
						   shipmentInfo.setTrackingNumber(trackingInfo.getTrackingNumber());
					   break;
				   }
				   }
				}
				}
				//BBBH-2899: order XML change ends				
				shipmentInfoList.add(shipmentInfo);
			} else if(OrderProcess.BOPUS.equals(pOrderProcess) && (shippingGroup instanceof BBBStoreShippingGroup) ){
				populateStoreShipInfo(pOrder, pOrderProcess, pFactory, subtotalPriceInfos, shipmentInfoList, shippingGroup, subtotalAmount);
			}
		}

		return shipmentList;  
	}
	//BBBH-2899: order XML change

	/**
	 * Populate store info type.
	 *
	 * @param pOrder the order
	 * @param pFactory the factory
	 * @param storeId the store id
	 * @return the store info type
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private StoreInfoType populateStoreInfoType(BBBOrder pOrder, ObjectFactory pFactory,String storeId) throws BBBSystemException, BBBBusinessException {
	logDebug("SubmitOrderMarshaller:populateStoreInfoType starts");
	final StoreInfoType storeInfo = pFactory.createStoreInfoType();
	storeInfo.setStoreId(storeId);
	StoreDetails storeVO = null;
	final StoreAddressType storeAddress = pFactory.createStoreAddressType();
	try {
		String storeType = getStoreManager().getStoreType(pOrder.getSiteId());
		logDebug("storeType is: "+ storeType);
		storeVO = this.getStoreManager().searchStoreById(storeId,pOrder.getSiteId(), storeType);
	} catch (final BBBBusinessException bbbbusinessException) {
		this.logError("BBBBusinessException in MapQuest call to fetch store data for store id = " + storeId);
	} catch (final BBBSystemException bbbSystemException) {
		this.logError("BBBSystemException in MapQuest call to fetch store data for store id = " + storeId);
	}

	if (storeVO != null) {

		if (!StringUtils.isBlank(storeVO.getStoreDescription())) {
			storeAddress.setStoreDescription(storeVO.getStoreDescription());
		}

		if (!StringUtils.isBlank(storeVO.getStoreName())) {
			storeAddress.setStoreName(storeVO.getStoreName());
		}

		storeAddress.setAddress(storeVO.getAddress());
		storeAddress.setCity(storeVO.getCity());
		storeAddress.setState(storeVO.getState());
		storeAddress.setZipCode(storeVO.getPostalCode());
		storeAddress.setCountry(storeVO.getCountry());

		if(!StringUtils.isBlank(storeVO.getSatStoreTimings())){
			storeAddress.setSatStoreTimings(storeVO.getSatStoreTimings().trim());
		}
		if(!StringUtils.isBlank(storeVO.getSunStoreTimings())){
			storeAddress.setSunStoreTimings(storeVO.getSunStoreTimings().trim());
		}
		if(!StringUtils.isBlank(storeVO.getWeekdaysStoreTimings())){
			storeAddress.setWeekdaysStoreTimings(storeVO.getWeekdaysStoreTimings().trim());
		}
		storeAddress.setStorePhoneNumber(storeVO.getStorePhone());
		storeInfo.setStoreAddress(storeAddress);
	} else {
		storeAddress.setStoreDescription(BBBCheckoutConstants.ERROR_MAPQUEST_FAILED_DESCRIPTION);
		storeAddress.setStoreName(BBBCheckoutConstants.NOT_APPLICABLE);
		storeAddress.setAddress(BBBCheckoutConstants.NOT_APPLICABLE);
		storeAddress.setCity(BBBCheckoutConstants.NOT_APPLICABLE);
		storeAddress.setState(BBBCheckoutConstants.NOT_APPLICABLE);
		storeAddress.setZipCode(BBBCheckoutConstants.NOT_APPLICABLE);
		storeAddress.setCountry(BBBCheckoutConstants.NOT_APPLICABLE);
		storeInfo.setStoreAddress(storeAddress);
		
	}
	logDebug("SubmitOrderMarshaller:populateStoreInfoType ends");
	return storeInfo;
	}
	
	//BBBH-2899: order XML change ends

	private void populateCMOShipInfo(BBBOrder pOrder, OrderProcess pOrderProcess, ObjectFactory pFactory, Map subtotalPriceInfos, List<ShipmentInfoType> shipmentInfoList,
	ShippingGroup shippingGroup, double subtotalAmount) throws BBBSystemException, BBBBusinessException {
		
		BBBStoreShippingGroup shipGroup = (BBBStoreShippingGroup)shippingGroup;
		BBBShippingPriceInfo priceInfo = (BBBShippingPriceInfo)shipGroup.getPriceInfo();
		OrderPriceInfo subtotalPriceInfo = null;

		ShipmentInfoType shipmentInfo = pFactory.createShipmentInfoType();
		ShippingInfoType shippingInfo = pFactory.createShippingInfoType();
		shipmentInfo.setAtgShippingGroupId(shipGroup.getId());
		
		RepositoryItem shipMethod = getShipMethod();
		shippingInfo.setShippingMethod(shipMethod.getRepositoryId());
		if(shipGroup.getShipOnDate() != null) {
			shippingInfo.setShipOnDate(this.getXMLCalendarShipOnDate(shipGroup.getShipOnDate()));
		}
		StoreDetails storeVO = null;
		AddressType storeAddress = pFactory.createAddressType();
		
		String siteId = pOrder.getSiteId();
		if (BBBUtility.isNotEmpty(siteId)) {
			if(siteId.equals(TBSConstants.SITE_TBS_BAB_US)) {
				siteId = BBBCoreConstants.SITE_BAB_US;
			} else if(siteId.equals(TBSConstants.SITE_TBS_BBB)) {
				siteId = BBBCoreConstants.SITE_BBB;			
			} else if(siteId.equals(TBSConstants.SITE_TBS_BAB_CA)) {
				siteId = BBBCoreConstants.SITE_BAB_CA;;			
			}
		}
		
		try {
			storeVO = getStoreManager().searchStoreById(shipGroup.getStoreId(), siteId, siteId);
		} catch (BBBBusinessException bbbbusinessException) {
			vlogError("BBBBusinessException in MapQuest call to fetch store data for store id = " + shipGroup.getStoreId());
		} catch (BBBSystemException bbbSystemException) {
			vlogError("BBBSystemException in MapQuest call to fetch store data for store id = " + shipGroup.getStoreId());
		}

		if (storeVO != null) {
			if (!StringUtils.isBlank(storeVO.getStoreName())) {
				storeAddress.setFirstName(storeVO.getStoreName());
				storeAddress.setLastName(storeVO.getStoreName());
			}
			storeAddress.setAddressLine1(storeVO.getAddress());
			storeAddress.setCity(storeVO.getCity());
			storeAddress.setState(storeVO.getState());
			storeAddress.setZipCode(storeVO.getPostalCode());
			storeAddress.setCountryCode(storeVO.getCountry().substring(0, 2));
			shippingInfo.setShippingAddress(storeAddress);
		}
		ShippingPriceInfoType shippingPriceInfo = pFactory.createShippingPriceInfoType();

		shippingPriceInfo.setShippingRawAmount(round(priceInfo.getRawShipping()));
		shippingPriceInfo.setShippingAmount(round(priceInfo.getFinalShipping()));
		subtotalAmount += shippingPriceInfo.getShippingAmount().doubleValue();

		shippingPriceInfo.setSurcharge(BigDecimal.valueOf(0.0));
		shippingPriceInfo.setFinalSurcharge(BigDecimal.valueOf(0.0));
		subtotalAmount += shippingPriceInfo.getFinalSurcharge().doubleValue();

		boolean taxFailure = pOrder.isTaxCalculationFailure();
		if(taxFailure) {
			shippingInfo.setTaxCalculationFailure(true);
		} else {
			Map<String, TaxPriceInfo> itemTaxPriceInfos = getShippingGroupLevelItemTaxPriceInfos(pOrder, shipGroup);
			shippingPriceInfo.setShippingTaxInfo(populateTaxInfo(itemTaxPriceInfos.get(BBBCoreConstants.SHIPPING_TAX_PRICE_INFO_KEY), 1.0, false, new HashMap<TAX_TYPE, BigDecimal>(), pFactory));
		}
		shippingInfo.setShippingPriceInfo(shippingPriceInfo);
		if(pOrder.getTaxPriceInfo() != null) {
			TaxPriceInfo shippingTax =  (TaxPriceInfo) pOrder.getTaxPriceInfo().getShippingItemsTaxPriceInfos().get(shipGroup.getId());
			if(shippingTax != null) {
				shippingInfo.setTaxInfo(populateTaxInfo(shippingTax, 1.0, false, new HashMap<TAX_TYPE, BigDecimal>(), pFactory));
				if((shippingInfo.getTaxInfo() != null) && (shippingInfo.getTaxInfo().getTotalTax() != null)) {
					subtotalAmount += shippingInfo.getTaxInfo().getTotalTax().doubleValue();
				}
			}
		}
		
		shipmentInfo.setShippingInfo(shippingInfo);
		subtotalPriceInfo = (OrderPriceInfo)subtotalPriceInfos.get(shipGroup.getId());
		if(subtotalPriceInfo!=null)
		{
			subtotalAmount += subtotalPriceInfo.getAmount();
		}
		shipmentInfo.setItemList(this.populateItems(pOrder, shipGroup, pOrderProcess, pFactory));
		shipmentInfo.setSubTotal(this.round(subtotalAmount));
		shipmentInfoList.add(shipmentInfo);
		
	}

	/**
	 * @return
	 */
	protected RepositoryItem getShipMethod() {
		return (RepositoryItem) SiteContextManager.getCurrentSite().getPropertyValue("defaultShipMethod");
	}

	/**
	 * This method is used to populate the StoreShipment details to the orderxml
	 * @param pOrder
	 * @param pOrderProcess
	 * @param pFactory
	 * @param subtotalPriceInfos
	 * @param shipmentInfoList
	 * @param shippingGroup
	 * @param subtotalAmount
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void populateStoreShipInfo(BBBOrder pOrder, OrderProcess pOrderProcess, ObjectFactory pFactory, Map subtotalPriceInfos,
			List<ShipmentInfoType> shipmentInfoList, ShippingGroup shippingGroup, double subtotalAmount) throws BBBSystemException, BBBBusinessException {
		
		OrderPriceInfo subtotalPriceInfo;
		final BBBStoreShippingGroup storeShipping = (BBBStoreShippingGroup) shippingGroup;
		final ShipmentInfoType shipmentInfo = pFactory.createShipmentInfoType();
		StoreInfoType storeInfo = populateStoreInfoType (pOrder,pFactory,storeShipping.getStoreId());		
		shipmentInfo.setAtgShippingGroupId(shippingGroup.getId());
		shipmentInfo.setStoreInfo(storeInfo);
		subtotalPriceInfo = (OrderPriceInfo)subtotalPriceInfos.get(shippingGroup.getId());
		subtotalAmount += subtotalPriceInfo.getAmount();
		shipmentInfo.setItemList(this.populateItems(pOrder, shippingGroup, pOrderProcess, pFactory));
		shipmentInfo.setSubTotal(this.round(subtotalAmount));
		shipmentInfoList.add(shipmentInfo);
	}

	@SuppressWarnings("unchecked")
	private PaymentListType populatePaymentList(final BBBOrder pOrder, final OrderProcess pOrderProcess, final ObjectFactory pFactory) throws BBBSystemException, BBBBusinessException{
		final PaymentListType paymentList = pFactory.createPaymentListType();
		for(final PaymentGroup paymentGroup : (List<PaymentGroup>)pOrder.getPaymentGroups()){
			final PaymentInfoType paymentInfo = pFactory.createPaymentInfoType();
			paymentInfo.setPaymentId(paymentGroup.getId());
			paymentInfo.setAmount(this.round(paymentGroup.getAmount()));
			//Changes for TBSNext Orders
			String lPaymentMethod = paymentGroup.getPaymentMethod();
			
			if (paymentGroup instanceof CreditCard) {
				paymentInfo.setPaymentType(PaymentTypes.CREDIT_CARD);
				paymentInfo.setCreditCardInfo(this.populateCreditCardPayment((BBBCreditCard)paymentGroup, pFactory, pOrder));
				paymentList.getPaymentInfo().add(paymentInfo);
			} else if((paymentGroup instanceof BBBGiftCard) && OrderProcess.ONLINE.equals(pOrderProcess)){
				paymentInfo.setPaymentType(PaymentTypes.GIFT_CARD);
				paymentInfo.setGiftCardInfo(this.populateGiftCardPayment((BBBGiftCard) paymentGroup, pFactory));
				paymentList.getPaymentInfo().add(paymentInfo);
			}
			//Start: 83-U - PayPal - Order XML changes
			else if((paymentGroup instanceof Paypal) && OrderProcess.ONLINE.equals(pOrderProcess)){
				paymentInfo.setPaymentType(PaymentTypes.PAYPAL);
				paymentInfo.setPayPalInfo(this.populatePaypalPayment(pOrder,(Paypal) paymentGroup, pFactory));
				paymentList.getPaymentInfo().add(paymentInfo);
			}
			//End: 83-U - PayPal - Order XML changes
			
			//Changes for TBSNext Orders
			else if((lPaymentMethod.equalsIgnoreCase("payAtRegister"))){
				paymentInfo.setPaymentType(PaymentTypes.POS);
				paymentInfo.setPosInfo(this.populatePOSPayment(pOrder, paymentGroup, pFactory));
				paymentList.getPaymentInfo().add(paymentInfo);
			}
		}

		return paymentList;
	}
	
	//Changes for TBSNext Orders
	private AdjustmentListType populateAdjustmentList(final List<PricingAdjustment> pAdjustments, final ObjectFactory pFactory, final BBBOrder pOrder, BBBHardGoodShippingGroup pHardGoodShipping){
		final AdjustmentListType orderPriceAdjustmentList = pFactory.createAdjustmentListType();
		final List<AdjustmentType> priceAdjustmentList = orderPriceAdjustmentList.getAdjustment();

		RepositoryItem pricingModel = null;
		/*OrderPriceInfo.adjustmentList.adjustment details*/
		AdjustmentType orderPriceAdjustment = null;
		for(final PricingAdjustment adjustment : pAdjustments) {
			if(adjustment.getPricingModel() != null) {
				orderPriceAdjustment = pFactory.createAdjustmentType();
				pricingModel = adjustment.getPricingModel();
				orderPriceAdjustment.setAtgPromotionId(pricingModel.getRepositoryId());
				this.fillCouponDetails(adjustment, orderPriceAdjustment);
				orderPriceAdjustment.setDiscountAmount(this.round(Math.abs(adjustment.getAdjustment())));
				orderPriceAdjustment.setAdjustmentType(String.valueOf(pricingModel.getPropertyValue("typeDetail")));
				orderPriceAdjustment.setAdjustmentDesc(String.valueOf(pricingModel.getPropertyValue("displayName")));
				//START : Changes for TBSNext Orders
				
				priceAdjustmentList.add(orderPriceAdjustment);
			} else if(adjustment.getAdjustmentDescription().equalsIgnoreCase("TBS Price Override")) {
				orderPriceAdjustment = pFactory.createAdjustmentType();
				orderPriceAdjustment.setDiscountAmount(this.round(Math.abs(adjustment.getAdjustment())));
				orderPriceAdjustment.setAtgPromotionId("PUBpromo999999");
				orderPriceAdjustment.setAdjustmentDesc("Override");
				orderPriceAdjustment.setAdjustmentType("Override");
				
				TBSShippingInfo lTbsShipInfo = pHardGoodShipping.getTbsShipInfo();
				
				if(lTbsShipInfo != null) {
					orderPriceAdjustment.setShipFeeOverride(BigDecimal.valueOf(lTbsShipInfo.getShipPriceValue()));
					orderPriceAdjustment.setShippingFeeOverrideReasonCode(lTbsShipInfo.getShipPriceReason());
					orderPriceAdjustment.setItemSurchargeOverride(BigDecimal.valueOf(lTbsShipInfo.getSurchargeValue()));
					orderPriceAdjustment.setItemSurchargeOverrideReasonCode(lTbsShipInfo.getSurchargeReason());
				}
				priceAdjustmentList.add(orderPriceAdjustment);
			}
			//END : Changes for TBSNext Orders
		}

		if(priceAdjustmentList.size() > 0) {
			return orderPriceAdjustmentList;
		} else {
			return null;
		}
	}

	private void fillCouponDetails(final PricingAdjustment pricingAdj,
			final AdjustmentType orderPriceAdjustment) {
		/*BBBH-3027 Code Change for fetching couponcode from claimable repository not from BBBCoupons Property*/
		if(pricingAdj.getCoupon() != null) {
			orderPriceAdjustment.setCouponCode(pricingAdj.getCoupon().getRepositoryId());
		}
	}

	@SuppressWarnings("unchecked")
	private AdjustmentListType populatePromotionList(final BBBDetailedItemPriceInfo promotionPriceBean, final CommerceItem pCommerceItem, final ObjectFactory pFactory){
		final AdjustmentListType orderPriceAdjustmentList = pFactory.createAdjustmentListType();
		final List<AdjustmentType> priceAdjustmentList = orderPriceAdjustmentList.getAdjustment();

		/*OrderPriceInfo.adjustmentList.adjustment details*/
		RepositoryItem promotionItem = null;
		AdjustmentType orderPriceAdjustment = null;
		
		if ((promotionPriceBean.getAdjustments() != null) && (promotionPriceBean.getAdjustments().size() > 0)) {
			final List<PricingAdjustment> adjustments = promotionPriceBean.getAdjustments();
			for (final PricingAdjustment pricingAdj : adjustments) {
				
				if (null != pricingAdj.getPricingModel()) {
					orderPriceAdjustment = pFactory.createAdjustmentType();
					promotionItem = pricingAdj.getPricingModel();
					orderPriceAdjustment.setAtgPromotionId(promotionItem.getRepositoryId());
					this.fillCouponDetails(pricingAdj, orderPriceAdjustment);
					orderPriceAdjustment.setDiscountAmount(this.round(Math.abs(pricingAdj.getAdjustment())));
					orderPriceAdjustment.setAdjustmentType(String.valueOf(promotionItem.getPropertyValue("typeDetail")));
					orderPriceAdjustment.setAdjustmentDesc(String.valueOf(promotionItem.getPropertyValue("displayName")));
					//Changes for TBSNext Orders
					priceAdjustmentList.add(orderPriceAdjustment);
				} else if(pricingAdj.getAdjustmentDescription().equalsIgnoreCase("TBS Price Override")) {
					orderPriceAdjustment = pFactory.createAdjustmentType();
					orderPriceAdjustment.setDiscountAmount(this.round(Math.abs(pricingAdj.getAdjustment())));
					orderPriceAdjustment.setAtgPromotionId("PUBpromo999999");
					orderPriceAdjustment.setAdjustmentDesc("Override");
					orderPriceAdjustment.setAdjustmentType("Override");
					if (pCommerceItem instanceof TBSCommerceItem) {
						TBSCommerceItem tbsCItem = (TBSCommerceItem) pCommerceItem;
						
						TBSItemInfo lTbsItemInfo = tbsCItem.getTBSItemInfo();
						
						if(null != lTbsItemInfo){
							orderPriceAdjustment.setItemPriceOverride(BigDecimal.valueOf(lTbsItemInfo.getOverridePrice()));
							orderPriceAdjustment.setItemPriceOverrideReasonCode(lTbsItemInfo.getOverideReason());
							orderPriceAdjustment.setItemPriceOverrideCompetitor(lTbsItemInfo.getCompetitor());
						}
					}
					priceAdjustmentList.add(orderPriceAdjustment);
				}
			}

		}
		if(!BBBUtility.isListEmpty(priceAdjustmentList)) {
			return orderPriceAdjustmentList;
		} else {
			return null;
		}
	}
	
	private boolean isSplitRequired(double amount, double quantity){
		double averagePrice = this.getPricingTools().round(amount/quantity);
		double averageTotal = this.getPricingTools().round(averagePrice * quantity);
		return Double.compare(amount, averageTotal) != 0;
	}

	@SuppressWarnings("unchecked")
    private ItemListType populateItems(final BBBOrder pOrder, final ShippingGroup pShippingGroup, final OrderProcess pOrderProcess, final ObjectFactory pFactory) throws BBBSystemException, BBBBusinessException {
		final ItemListType itemList = pFactory.createItemListType();

		CommerceItem commerceItem = null;
		SKUDetailVO skuDetail = null;
		final Map<TAX_TYPE, BigDecimal> taxMap = new HashMap<TAX_TYPE, BigDecimal>();
		final Map<TAX_TYPE, BigDecimal> ecoFeeTaxMap = new HashMap<TAX_TYPE, BigDecimal>();
		final Map<String, TaxPriceInfo> itemTaxPriceInfos = this.getShippingGroupLevelItemTaxPriceInfos(pOrder, pShippingGroup);
		
		Map<TAX_TYPE, BigDecimal> assemblyFeeTaxMap = new HashMap<TAX_TYPE, BigDecimal>();
		Map<TAX_TYPE, BigDecimal> shipSurchargeTaxMap = new HashMap<TAX_TYPE, BigDecimal>();
		Map<TAX_TYPE, BigDecimal> shipSurchargeProratedTaxMap = new HashMap<TAX_TYPE, BigDecimal>();
		final List<ShippingGroupCommerceItemRelationship> commerceItemRelationships = pShippingGroup.getCommerceItemRelationships();
		for(final ShippingGroupCommerceItemRelationship relationship : commerceItemRelationships){
			commerceItem = relationship.getCommerceItem();
			if(commerceItem instanceof BBBCommerceItem) {
				BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) commerceItem;
				final List<BBBDetailedItemPriceInfo> priceBeans= bbbCommerceItem.getPriceInfo().getCurrentPriceDetailsForRange(relationship.getRange());
				final TaxPriceInfo taxPrice = itemTaxPriceInfos.get(bbbCommerceItem.getId());
				if(taxPrice != null) {
					taxMap.put(TAX_TYPE.CITY, BigDecimal.valueOf(taxPrice.getCityTax()));
					taxMap.put(TAX_TYPE.COUNTY, BigDecimal.valueOf(taxPrice.getCountyTax()));
					taxMap.put(TAX_TYPE.DISTRICT, BigDecimal.valueOf(taxPrice.getDistrictTax()));
					taxMap.put(TAX_TYPE.STATE, BigDecimal.valueOf(taxPrice.getStateTax()));
					taxMap.put(TAX_TYPE.AMOUNT, BigDecimal.valueOf(taxPrice.getAmount()));
				}
				final double itemSurcharge = this.getPricingTools().calculateItemSurchargeInSG(pOrder.getSiteId(), relationship);
				
				if((priceBeans != null) && (priceBeans.size() > 0)) {
					double deliveryItemTotal = 0.0 ;
					double shippingGroupItemsTotal = 0.0;
					long shippingGroupItemsQuantity = 0;
					for(DetailedItemPriceInfo priceBean : priceBeans){
						shippingGroupItemsQuantity += priceBean.getQuantity();
						shippingGroupItemsTotal += priceBean.getAmount();
					}
					shippingGroupItemsTotal = this.getPricingTools().round(shippingGroupItemsTotal);
					ItemType item = null;
					NonMerchandiseCommerceItem nonMerchCommerceItem = null;
					String deliveryItemId = bbbCommerceItem.getDeliveryItemId();
					String assemblyItemId = bbbCommerceItem.getAssemblyItemId();
					boolean isSplitRequired = false;
					
					skuDetail = this.getCatalogTools().getSKUDetails(pOrder.getSiteId(), bbbCommerceItem.getCatalogRefId(), false, true, true);

					if(BBBUtility.isNotEmpty(deliveryItemId)){
						try {
							nonMerchCommerceItem = (NonMerchandiseCommerceItem) pOrder.getCommerceItem(deliveryItemId);
						} catch (CommerceItemNotFoundException e) {
							logError("CommerceItem with Id :- " + deliveryItemId + " is not found in container.",e);
						} catch (InvalidParameterException e) {
							logError("Error occurred while retreiving LTLDeliveryCommerceItem", e);
						}
						if(nonMerchCommerceItem!=null){
							deliveryItemTotal = nonMerchCommerceItem.getPriceInfo().getAmount();
							shipSurchargeTaxMap = createTaxPriceInfo(shipSurchargeTaxMap, itemTaxPriceInfos, deliveryItemId);
							shipSurchargeProratedTaxMap = createTaxPriceInfo(shipSurchargeProratedTaxMap, itemTaxPriceInfos, deliveryItemId);
							isSplitRequired = this.isSplitRequired(deliveryItemTotal, bbbCommerceItem.getQuantity());				
							if (BBBUtility.isNotEmpty(assemblyItemId)) {
								assemblyFeeTaxMap = createTaxPriceInfo(assemblyFeeTaxMap, itemTaxPriceInfos, assemblyItemId);
							}
						}
					}
					
					Collections.sort(priceBeans, Collections.reverseOrder(new SubmitOrderMarshalerComparator()));
					
					for(int loopCounter=0; loopCounter < priceBeans.size(); loopCounter++){
						BBBDetailedItemPriceInfo dpi = priceBeans.get(loopCounter);
						long quantity = dpi.getQuantity();
						boolean isLastItem = loopCounter == (priceBeans.size() - 1);
						boolean splitLastItem = dpi.getQuantity() > 1 && isLastItem && isSplitRequired;
						item  = createItemInfo(pFactory, commerceItem, dpi, pOrder, pShippingGroup, skuDetail, shippingGroupItemsTotal,
								shippingGroupItemsQuantity, itemSurcharge, itemTaxPriceInfos, relationship, taxMap,
								ecoFeeTaxMap, splitLastItem ? quantity - 1: quantity, splitLastItem ? false : isLastItem, assemblyFeeTaxMap, 
								shipSurchargeTaxMap, shipSurchargeProratedTaxMap, false);
						this.setTBSProperties(item, bbbCommerceItem);
						itemList.getItem().add(item);
						if(splitLastItem){
							item  = createItemInfo(pFactory, commerceItem, dpi, pOrder, pShippingGroup, skuDetail, shippingGroupItemsTotal,
									shippingGroupItemsQuantity, itemSurcharge, itemTaxPriceInfos, relationship, taxMap, 
									ecoFeeTaxMap, 1, isLastItem, assemblyFeeTaxMap, shipSurchargeTaxMap, shipSurchargeProratedTaxMap, true);
							this.setTBSProperties(item, bbbCommerceItem);
							itemList.getItem().add(item);
						}
					}
					
				}
			}
		}
		return itemList;
	}
	
	private void setTBSProperties(ItemType item, CommerceItem commerceItem){
		//Changes for TBSNext Orders
		if(commerceItem instanceof TBSCommerceItem){
			if(!StringUtils.isBlank(((TBSCommerceItem) commerceItem).getShipTime()) && item != null){
				item.setPresentedLeadTime(((TBSCommerceItem) commerceItem).getShipTime());
			}
			TBSItemInfo lTbsItemInfo = ((TBSCommerceItem) commerceItem).getTBSItemInfo();
			if(lTbsItemInfo != null && item != null){
				item.setConfigId(lTbsItemInfo.getConfigId());
			}

			Set<RepositoryItem> skuAttrRelation = null;
			RepositoryItem skuAttribute = null;
			String skuAttrId = null;

			RepositoryItem skuItem = (RepositoryItem) commerceItem.getAuxiliaryData().getCatalogRef();
			if(skuItem != null){
				skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
			}
			if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
				vlogDebug("skuAttrRelation :: "+skuAttrRelation );
				for (RepositoryItem skuAttrReln : skuAttrRelation) {
					skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
					if(skuAttribute != null){
						skuAttrId = skuAttribute.getRepositoryId();
					}
					if(!StringUtils.isBlank(skuAttrId) && skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE) && item != null){
						item.setExternalCart("KIRSCH");
						break;
					} else if(!StringUtils.isBlank(skuAttrId) && skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE) && item != null){
						item.setExternalCart("CMO");
						break;
					}
				}
			}
		}
	}

	private BigDecimal toBigDecimal(double pValue) {
		return BigDecimal.valueOf(getPricingTools().round(pValue, 2));
	}
	
	/**
	 * @param pFactory
	 * @param commerceItem
	 * @return PcInfoType
	 */
	public PcInfoType createPcInfoType(final ObjectFactory pFactory, BBBCommerceItem commerceItem ) {
		
		final PcInfoType pcInfoTypeItem = pFactory.createPcInfoType();
		String fullImagePath = null;
		String thumbNailImagePath = null;
		final String personalizationType = String.valueOf(((RepositoryItem) commerceItem.getAuxiliaryData()
				.getCatalogRef()).getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE));
		final String vendorId = (String)((RepositoryItem) commerceItem.getAuxiliaryData().getProductRef()).getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME);
		String vendorName = null;
		try {
			vendorName = this.getCatalogTools().getVendorInfo(vendorId).getVendorName();
		} catch (BBBSystemException e) {
			logError("SubmitOrderMarshaller.createPcInfoType :: BBBSystemException occured while getting vendor name for vendor Id -- " + vendorId);
		} catch (BBBBusinessException e) {
			logError("SubmitOrderMarshaller.createPcInfoType :: BBBBusinessException occured while getting vendor name for vendor Id -- " + vendorId);
		}
		if(commerceItem.getFullImagePath().contains(BBBCoreConstants.QUESTION_MARK)) {
			fullImagePath = commerceItem.getFullImagePath().split(BBBCoreConstants.QUESTION_MARK_2)[0];
			thumbNailImagePath = commerceItem.getThumbnailImagePath().split(BBBCoreConstants.QUESTION_MARK_2)[0];
		}
		
		pcInfoTypeItem.setReferenceId(commerceItem.getReferenceNumber());
		pcInfoTypeItem.setPcType(personalizationType);
		pcInfoTypeItem.setVendorConfiguratorName(vendorName);
		pcInfoTypeItem.setImageUrlLarge(fullImagePath);
		pcInfoTypeItem.setImageUrlSmall(thumbNailImagePath);
		pcInfoTypeItem.setPcDescription(commerceItem.getPersonalizationDetails());
		pcInfoTypeItem.setServiceType(commerceItem.getPersonalizationOptions());
		pcInfoTypeItem.setMetaDataFlag(Boolean.TRUE);
		pcInfoTypeItem.setMetaDataUrl(commerceItem.getMetaDataUrl());
		pcInfoTypeItem.setModerationFlag(Boolean.TRUE);
		pcInfoTypeItem.setModerationUrl(commerceItem.getModerationUrl());
		return pcInfoTypeItem;
	}
	
	@SuppressWarnings("unchecked")
	public ItemType createItemInfo(final ObjectFactory pFactory, CommerceItem commerceItem, BBBDetailedItemPriceInfo promotionPriceBean, final BBBOrder pOrder,
			final ShippingGroup pShippingGroup, SKUDetailVO skuDetail, double shippingGroupItemsTotal, long shippingGroupItemsQuantity, double itemSurcharge,
			Map<String, TaxPriceInfo> itemTaxPriceInfos, ShippingGroupCommerceItemRelationship commerceItemRelation, 
			final Map<TAX_TYPE, BigDecimal> taxMap, final Map<TAX_TYPE, BigDecimal> ecoFeeTaxMap, long itemQuantity, boolean isLastItem,
			final Map<TAX_TYPE, BigDecimal> assemblyFeeTaxMap, final Map<TAX_TYPE, BigDecimal> shipSurchargeTaxMap,
			final Map<TAX_TYPE, BigDecimal> shipSurchargeProratedTaxMap, boolean useSplit) throws BBBSystemException, BBBBusinessException {
		
		final ItemType item = pFactory.createItemType();
		
		item.setAtgCommerceItemId(commerceItem.getId());
		item.setSku(commerceItem.getCatalogRefId());
		item.setQuantity(BigInteger.valueOf(itemQuantity));

		// Set the isGiftCard & jdaDeptNum properties of the item

		item.setIsGiftCard(this.getCatalogTools().isGiftCardItem(pOrder.getSiteId(), commerceItem.getCatalogRefId()));

		final String jdaDeptId = this.getCatalogTools().getJDADeptForSku(pOrder.getSiteId(), commerceItem.getCatalogRefId());

		if (commerceItem instanceof BBBCommerceItem) {
			final boolean vdcFlag = ((BBBCommerceItem) commerceItem).isVdcInd();
			item.setIsVdcInd(vdcFlag);
			if(!BBBUtility.isEmpty(((BBBCommerceItem) commerceItem).getReferenceNumber()) && ! ((BBBCoreConstants.MINUS_ONE).equals(((BBBCommerceItem) commerceItem).getReferenceNumber()))){
				item.setIsPcInd(Boolean.TRUE);
				item.setPcInfo(this.createPcInfoType(pFactory, (BBBCommerceItem)commerceItem));
			}
		}

		if (jdaDeptId != null) {
			item.setJdaDeptNum(jdaDeptId);
		}

		if((skuDetail != null) && (pShippingGroup instanceof BBBHardGoodShippingGroup)){
			if(!StringUtils.isBlank(skuDetail.getTaxStatus())){
				item.setTaxCD(skuDetail.getTaxStatus());
			}

			if((skuDetail.getFreeShipMethods() != null) && (skuDetail.getFreeShipMethods().size() > 0)) {
				item.setFreeShipping(BBBCoreConstants.YES_CHAR);
			} else {
				item.setFreeShipping(BBBCoreConstants.NO_CHAR);
			}
			//check for international order surcharge on
			if(!isInternationalOrder(pOrder.getSalesChannel()) || (isInternationalOrder(pOrder.getSalesChannel()) && isInternationalSurchargeOn()) ){
				item.setSurchargeAmount(this.round(itemSurcharge * itemQuantity));
			}else{
				item.setSurchargeAmount(BigDecimal.valueOf(0.0));
			}
		}
		boolean cmoFlag = false;
		boolean kirschFlag = false;
		//Changes for TBSNext Orders
		if(commerceItem instanceof TBSCommerceItem){
			cmoFlag = ((TBSCommerceItem)commerceItem).isCMO();
			// changes for CMO orders
			if(cmoFlag && (skuDetail != null) && (pShippingGroup instanceof BBBStoreShippingGroup)){
				if(!StringUtils.isBlank(skuDetail.getTaxStatus())){
					item.setTaxCD(skuDetail.getTaxStatus());
				}

				if((skuDetail.getFreeShipMethods() != null) && (skuDetail.getFreeShipMethods().size() > 0)) {
					item.setFreeShipping(BBBCoreConstants.YES_CHAR);
				} else {
					item.setFreeShipping(BBBCoreConstants.NO_CHAR);
				}
				//check for international order surcharge on
				if(!isInternationalOrder(pOrder.getSalesChannel()) || (isInternationalOrder(pOrder.getSalesChannel()) && isInternationalSurchargeOn()) ){
					item.setSurchargeAmount(this.round(itemSurcharge * itemQuantity));
				}else{
					item.setSurchargeAmount(BigDecimal.valueOf(0.0));
				}
				item.setIsVdcInd(true);
			}
			kirschFlag = ((TBSCommerceItem)commerceItem).isKirsch();
			if(kirschFlag){
				item.setIsVdcInd(true);
			}
			item.setPresentedLeadTime(((TBSCommerceItem) commerceItem).getShipTime());
			TBSItemInfo lTbsItemInfo = ((TBSCommerceItem) commerceItem).getTBSItemInfo();
			vlogDebug("lTbsItemInfo :: "+ lTbsItemInfo );
			if(lTbsItemInfo != null){
				vlogDebug("Setting config id for item : " + item);
				item.setConfigId(lTbsItemInfo.getConfigId());
			}
			
			Set<RepositoryItem> skuAttrRelation = null;
			RepositoryItem skuAttribute = null;
			String skuAttrId = null;

			RepositoryItem skuItem = (RepositoryItem) commerceItem.getAuxiliaryData().getCatalogRef();
			if(skuItem != null){
				skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
			}
			if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
				vlogDebug("skuAttrRelation :: "+ skuAttrRelation );
				for (RepositoryItem skuAttrReln : skuAttrRelation) {
					skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
					if(skuAttribute != null){
						skuAttrId = skuAttribute.getRepositoryId();
					}
					if(!StringUtils.isBlank(skuAttrId) && skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE)){
						item.setExternalCart("KIRSCH");
						break;
					} else if(!StringUtils.isBlank(skuAttrId) && skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE)){
						item.setExternalCart("CMO");
						break;
					}
				}
			}
		}
		
		final ItemPriceInfoType itemPriceInfo = pFactory.createItemPriceInfoType();
		if(pShippingGroup instanceof BBBHardGoodShippingGroup || cmoFlag ){
			final double ratio = promotionPriceBean.getAmount()/shippingGroupItemsTotal;
			 RepositoryItem dsLineItemTaxInfo = null;
			 if(useSplit){
				 dsLineItemTaxInfo = ((BBBOrderTools)getOrderManager().getOrderTools()).
					 fetchTaxInfoFromDPI(promotionPriceBean, BBBCoreConstants.DPI_ITEM_SPLIT);
			 } else{
				 dsLineItemTaxInfo = ((BBBOrderTools)getOrderManager().getOrderTools()).fetchTaxInfoFromDPI(
						 promotionPriceBean, BBBCoreConstants.DPI_ITEM);
			 }
			
			
			logDebug("Tax info from repository: " + dsLineItemTaxInfo);
			//If order is old (i.e. before proration flow was removed) then traverse through proration flow otherwise search order tax info in repository.
			if (((BBBOrderImpl)pOrder).isOldOrder() || ((BBBOrderImpl)pOrder).getInternationalOrderId()!=null ) {
				itemPriceInfo.setItemTaxInfo(this.populateTaxInfo(itemTaxPriceInfos.get(commerceItem.getId()), ratio, isLastItem, taxMap, pFactory));
			} else {
				itemPriceInfo.setItemTaxInfo((null !=dsLineItemTaxInfo)?this.populateTaxInfoFromRepository(dsLineItemTaxInfo, pFactory):null);
			}
			
			itemPriceInfo.setAdjustmentList(this.populatePromotionList(promotionPriceBean, commerceItem, pFactory));

		}
		
		// LTL | Set ltl indicator at item level and create new child AssemblyFeeType and ShipSurchargeType at item level | START
		String state = "";
		if(pShippingGroup instanceof BBBHardGoodShippingGroup){
			Address pAddress = ((BBBHardGoodShippingGroup)pShippingGroup).getShippingAddress();
			if(null != pAddress && !StringUtils.isBlank(pAddress.getState())){
				state = pAddress.getState();
			}
		}
		if(!isInternationalOrder(pOrder.getSalesChannel())){
			Date submitDate = pShippingGroup.getShipOnDate();
			if(null == submitDate) {
				submitDate = pOrder.getSubmittedDate();
			}
			if(null != skuDetail && skuDetail.isLtlItem()) {
				item.setIsLtlInd(true);
				item.setIsVdcInd(true);
				this.setEstimatedDeliveryDates(true, false, item,pOrder,pShippingGroup.getShippingMethod(),state,submitDate);
			} else if(null != skuDetail && skuDetail.isVdcSku()){
				item.setIsLtlInd(false);
				this.setEstimatedDeliveryDates(false, true, item,pOrder,pShippingGroup.getShippingMethod(),state,submitDate);
			}else {
				item.setIsLtlInd(false);
				if(!(pShippingGroup instanceof BBBStoreShippingGroup)) {
					this.setEstimatedDeliveryDates(false, false, item,pOrder,pShippingGroup.getShippingMethod(),state,submitDate);
				}
			}
		}
		AssemblyFeeType assemblyFee = null;
		ShipSurchargeType shipSurcharge = null;
		ShipSurchargeType discountedShipSurcharge = null;
		
		
		
		if(pShippingGroup instanceof BBBHardGoodShippingGroup){
			try {
				assemblyFee = populateAssemblyFeeInfo(commerceItem,pFactory,itemTaxPriceInfos,(BBBHardGoodShippingGroup)pShippingGroup,pOrder,
						commerceItemRelation,promotionPriceBean,item,itemQuantity, isLastItem, assemblyFeeTaxMap,useSplit, shippingGroupItemsQuantity);
				shipSurcharge = populateShipSurchargeInfo(commerceItem,pFactory,itemTaxPriceInfos,(BBBHardGoodShippingGroup)pShippingGroup,pOrder,
						commerceItemRelation,promotionPriceBean, itemQuantity, isLastItem, shipSurchargeTaxMap, shippingGroupItemsQuantity);
				discountedShipSurcharge = populateDiscountedShipSurchargeInfo(commerceItem,pFactory,itemTaxPriceInfos,(BBBHardGoodShippingGroup)pShippingGroup,
						pOrder,commerceItemRelation,promotionPriceBean, itemQuantity, isLastItem, shipSurchargeProratedTaxMap, useSplit, shippingGroupItemsQuantity);
			} catch (CommerceItemNotFoundException e) {
				logError("Exception occured while fetching Commerce Item from order" ,e);
			} catch (InvalidParameterException e) {
				logError("Invalid parameter is passed while fecthing Commerce Item from order" ,e);
			}
		}
		if(null != assemblyFee) {
			item.setAssemblyFee(assemblyFee);
		}
		if(null != shipSurcharge) {
			item.setDeliverySurcharge(shipSurcharge);
		}
		if(null != discountedShipSurcharge) {
			item.setProdRatedDeliverySurcharge(discountedShipSurcharge);
		}
		// LTL | Set ltl indicator at item level and create new child AssemblyFeeType and ShipSurchargeType at item level | END
		if(!StringUtils.isBlank(((BBBCommerceItem)commerceItem).getRegistryId()))
		{
			item.setRegistryId(((BBBCommerceItem)commerceItem).getRegistryId());
		}
		double unitPrice = commerceItem.getPriceInfo().getListPrice();
		double salePricediffer = 0.0;
		List<PricingAdjustment> adjustments = commerceItem.getPriceInfo().getAdjustments();
		for (PricingAdjustment pricingAdjustment : adjustments) {
			 if (pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase("Sale price")){
				 salePricediffer = Math.abs(pricingAdjustment.getAdjustment());
				 break;
			 }
		}
		if(salePricediffer > 0){
			unitPrice = unitPrice - salePricediffer;
		}
		itemPriceInfo.setUnitPrice(this.round(unitPrice));
		itemPriceInfo.setTotalAmount(this.round(promotionPriceBean.getDetailedUnitPrice()*itemQuantity));
		String costDefault = (String)((RepositoryItem) commerceItem.getAuxiliaryData()
				.getCatalogRef()).getPropertyValue(BBBCatalogConstants.COST_DEFAULT);
		if(StringUtils.isBlank(costDefault))
		{
			costDefault="0";
		}
		String personalizationType = (String) ((RepositoryItem) ((BBBCommerceItem)commerceItem).getAuxiliaryData().getCatalogRef()).getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE);
		if (BBBCoreConstants.PERSONALIZATION_CODE_CR.equalsIgnoreCase(personalizationType)) {
			itemPriceInfo.setVendorCost(this.round(((BBBCommerceItem)commerceItem).getPersonalizeCost()));
		} else if (BBBCoreConstants.PERSONALIZATION_CODE_PY.equalsIgnoreCase(personalizationType)) {
			itemPriceInfo.setVendorCost(this.round(Double.parseDouble(costDefault) + ((BBBCommerceItem)commerceItem).getPersonalizeCost()));
		} else if (BBBCoreConstants.PERSONALIZATION_CODE_PB.equalsIgnoreCase(personalizationType)){
			itemPriceInfo.setVendorCost(this.round(Double.parseDouble(costDefault)));
		} else if(commerceItem instanceof TBSCommerceItem){ //Changes for TBSNext Orders
			TBSItemInfo lTbsItemInfo = ((TBSCommerceItem) commerceItem).getTBSItemInfo();
			if(lTbsItemInfo != null && lTbsItemInfo.getCost() > 0){
				itemPriceInfo.setVendorCost(BigDecimal.valueOf(lTbsItemInfo.getCost()));
			}else {
				itemPriceInfo.setVendorCost(this.round(Double.parseDouble(costDefault)));
			} 
		}else{
			itemPriceInfo.setVendorCost(this.round(Double.parseDouble(costDefault)));
		}
		if(null != ((BBBCommerceItem)commerceItem).getReferenceNumber()) {
			
		itemPriceInfo.setPcUnitPrice(this.round(((BBBCommerceItem)commerceItem).getPersonalizePrice()));
		itemPriceInfo.setPcUnitCost(this.round(((BBBCommerceItem)commerceItem).getPersonalizeCost()));
		
		}
		
		item.setItemPriceInfo(itemPriceInfo);

		//Eco Fee changes
		EcoFeeType ecoFeeType = this.populateEcoFeeItemInfo(commerceItem, pShippingGroup, pFactory, pOrder, ecoFeeTaxMap, 
				shippingGroupItemsQuantity, itemQuantity, useSplit, promotionPriceBean, isLastItem, itemTaxPriceInfos);
		if(ecoFeeType!=null)
			item.setEcoFee(ecoFeeType);
		//Eco Fee changes ends
		return item;
	}
	
	private double checkCurrentPrice(boolean onSale, double listPrice, double salePrice){
		if(onSale){
			return salePrice > 0.0 ? salePrice : listPrice;
		}else{
			return listPrice;
		}
	}
	
	private EcoFeeType populateEcoFeeItemInfo(CommerceItem commerceItem, ShippingGroup shippingGroup, ObjectFactory factory, Order order, 
			Map<TAX_TYPE, BigDecimal> ecoFeeTaxMap, long shippingGroupItemsQuantity, long itemQuantity, boolean useSplit, BBBDetailedItemPriceInfo priceBean,
			boolean isLastItem, Map<String, TaxPriceInfo> itemTaxPriceInfos) throws BBBSystemException, BBBBusinessException{
		EcoFeeType ecoFeeItem = null;
		try {
			Map<String, String> ecoFeeMap = null;

			if (shippingGroup instanceof BBBHardGoodShippingGroup) {
				ecoFeeMap = ((BBBHardGoodShippingGroup) shippingGroup).getEcoFeeItemMap();
			} else if (shippingGroup instanceof BBBStoreShippingGroup) {
				ecoFeeMap = ((BBBStoreShippingGroup) shippingGroup).getEcoFeeItemMap();
			}

			if (!BBBUtility.isMapNullOrEmpty(ecoFeeMap)) {
				final String ecoFeeItemId = ecoFeeMap.get(commerceItem.getId());

				if(!StringUtils.isBlank(ecoFeeItemId)) {
					ecoFeeItem = factory.createEcoFeeType();
					final ItemPriceInfoType ecoItemPriceInfo = factory.createItemPriceInfoType();
					final CommerceItem ecoFeeCommerceItem = order.getCommerceItem(ecoFeeItemId);
					final TaxPriceInfo ecoFeeTaxPriceInfo = itemTaxPriceInfos.get(ecoFeeItemId);

					if(ecoFeeTaxPriceInfo != null) {
						ecoFeeTaxMap.put(TAX_TYPE.CITY, BigDecimal.valueOf(ecoFeeTaxPriceInfo.getCityTax()));
						ecoFeeTaxMap.put(TAX_TYPE.COUNTY, BigDecimal.valueOf(ecoFeeTaxPriceInfo.getCountyTax()));
						ecoFeeTaxMap.put(TAX_TYPE.DISTRICT, BigDecimal.valueOf(ecoFeeTaxPriceInfo.getDistrictTax()));
						ecoFeeTaxMap.put(TAX_TYPE.STATE, BigDecimal.valueOf(ecoFeeTaxPriceInfo.getStateTax()));
						ecoFeeTaxMap.put(TAX_TYPE.AMOUNT, BigDecimal.valueOf(ecoFeeTaxPriceInfo.getAmount()));
					}

					if ((ecoFeeCommerceItem != null) && (ecoFeeCommerceItem.getPriceInfo() != null)) {
						final double ecoFeeRatio = ((double)itemQuantity)/((double)shippingGroupItemsQuantity);
						ecoFeeItem.setSku(ecoFeeCommerceItem.getCatalogRefId());
						double ecoFeeUnitPrice = this.checkCurrentPrice(ecoFeeCommerceItem.getPriceInfo().isOnSale(), ecoFeeCommerceItem.getPriceInfo().getListPrice(), ecoFeeCommerceItem.getPriceInfo().getSalePrice());
						ecoItemPriceInfo.setUnitPrice(this.round(ecoFeeUnitPrice));
						ecoItemPriceInfo.setTotalAmount(this.round(ecoFeeUnitPrice * itemQuantity));

						if(shippingGroup instanceof BBBHardGoodShippingGroup){
							RepositoryItem dsLineItemTaxInfo=null;
                            dsLineItemTaxInfo = ((BBBOrderTools)getOrderManager().getOrderTools()).fetchTaxInfoFromDPI(priceBean, useSplit ? BBBCoreConstants.DPI_ECOFEE_SPLIT : BBBCoreConstants.DPI_ECOFEE);
							logDebug("Tax info from repository: " + dsLineItemTaxInfo);
							//If order is old (i.e. before proration flow was removed) then traverse through proration flow otherwise search order tax info in repository.                                                                                    
                            if (((BBBOrderImpl)order).isOldOrder()) {
                            	ecoItemPriceInfo.setItemTaxInfo(this.populateTaxInfo(ecoFeeTaxPriceInfo, ecoFeeRatio, isLastItem, ecoFeeTaxMap, factory));
                            } else {
                            	ecoItemPriceInfo.setItemTaxInfo(this.populateTaxInfoFromRepository(dsLineItemTaxInfo, factory));
                            }
						}
					}
					ecoFeeItem.setItemPriceInfo(ecoItemPriceInfo);
				}
			}
		} catch (final CommerceItemNotFoundException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1060,e.getMessage(), e);
		} catch (final InvalidParameterException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1061,e.getMessage(), e);
		}
		return ecoFeeItem;
	}
	
	public Map<TAX_TYPE, BigDecimal> createTaxPriceInfo(final Map<TAX_TYPE, BigDecimal> taxMap, final Map<String, TaxPriceInfo> itemTaxPriceInfos, String commerceItemId){
		final TaxPriceInfo taxPriceInfo = itemTaxPriceInfos.get(commerceItemId);
		if(taxPriceInfo != null) {
			taxMap.put(TAX_TYPE.CITY, BigDecimal.valueOf(taxPriceInfo.getCityTax()));
			taxMap.put(TAX_TYPE.COUNTY, BigDecimal.valueOf(taxPriceInfo.getCountyTax()));
			taxMap.put(TAX_TYPE.DISTRICT, BigDecimal.valueOf(taxPriceInfo.getDistrictTax()));
			taxMap.put(TAX_TYPE.STATE, BigDecimal.valueOf(taxPriceInfo.getStateTax()));
			taxMap.put(TAX_TYPE.AMOUNT, BigDecimal.valueOf(taxPriceInfo.getAmount()));
		}
		return taxMap;
	}
	
	@SuppressWarnings("unchecked")
    private String getMemberID(final BBBOrder pOrder, final RepositoryItem pProfile){
		String memberID = null;
		final Map<String, RepositoryItem> userSiteItems = (Map<String, RepositoryItem>) pProfile.getPropertyValue("userSiteItems");
		if((userSiteItems != null) && !userSiteItems.isEmpty()){
			final RepositoryItem userSiteItem = userSiteItems.get(pOrder.getSiteId());
			if(userSiteItem != null) {
				memberID = (String) userSiteItem.getPropertyValue("memberId");
			}
		}

		return memberID;
	}

	@SuppressWarnings("rawtypes")
    private CreditCardInfoType populateCreditCardPayment(final BBBCreditCard pCreditCard, final ObjectFactory pFactory, final BBBOrder pOrder) throws BBBSystemException{
		final CreditCardInfoType creditCardInfo = pFactory.createCreditCardInfoType();

		try {
			creditCardInfo.setCreditCardNumber(this.getEncryptorTools().encryptString(pCreditCard.getCreditCardNumber()));
		} catch (final EncryptorException e) {
			final String msg = "Error while encrypting Credit card details";
			this.getEncryptorTools().logError(msg, e);
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1062,msg, e);
		}
		if(!StringUtils.isBlank(pCreditCard.getNameOnCard())){
			creditCardInfo.setNameOnCreditCard(pCreditCard.getNameOnCard());
		}
		creditCardInfo.setCreditCardType(pCreditCard.getCreditCardType());

		final XMLGregorianCalendar xmlGregorianCalendar = this.getXMLCalendar(null);
		xmlGregorianCalendar.setMonth(Integer.valueOf(pCreditCard.getExpirationMonth()));
		xmlGregorianCalendar.setYear(Integer.valueOf(pCreditCard.getExpirationYear()));
		creditCardInfo.setExpiration(xmlGregorianCalendar);

		final List authStatusList = pCreditCard.getAuthorizationStatus();
		if((authStatusList != null) && (authStatusList.size() > 0)) {
			final CreditCardStatus paymentStatus = (CreditCardStatus)authStatusList.get(authStatusList.size()-1);
			if(!StringUtils.isBlank(paymentStatus.getAuthorizationCode())) {
				final CCAuthInfoType ccAuthInfo = pFactory.createCCAuthInfoType();
				ccAuthInfo.setAuthAmount(this.getPricingTools().round(pCreditCard.getAmountAuthorized()));
				ccAuthInfo.setRequestId(paymentStatus.getTransactionId());
				ccAuthInfo.setAvsCode(paymentStatus.getAvsCode());
				ccAuthInfo.setAuthTime(this.getXMLCalendar(paymentStatus.getTransactionTimestamp()));
				ccAuthInfo.setAuthCode(paymentStatus.getAuthorizationCode());
				ccAuthInfo.setAuthResponseRecord(paymentStatus.getAuthResponseRecord());

				creditCardInfo.setCcAuthInfo(ccAuthInfo);
			} else {
				final CCAuthErrorType ccAuthError = pFactory.createCCAuthErrorType();
				ccAuthError.setRequestId(paymentStatus.getTransactionId());
				ccAuthError.setAuthTime(this.getXMLCalendar(paymentStatus.getTransactionTimestamp()));
				ccAuthError.setAuthError(paymentStatus.getAuthResponseRecord());

				creditCardInfo.setCcAuthError(ccAuthError);
			}
		}
		
		//Start: 258-3 - Verified by Visa - Order XML changes
		final CC3DSecureInfoType cc3DSecureInfo = pFactory.createCC3DSecureInfoType();
		cc3DSecureInfo.setXid(pOrder.getXid());
		cc3DSecureInfo.setEciFlag(pOrder.getEci());
		cc3DSecureInfo.setCavvUCAF(pOrder.getCavv());
		if(BBBUtility.isNotEmpty(pOrder.getXid()) 
				&& BBBUtility.isNotEmpty(pOrder.getCavv())){
			cc3DSecureInfo.setEcomIndicator(pOrder.getCommerceIndicator());
		}
		//Added as part of TBSNext Changes
		
			cc3DSecureInfo.setEnrollmentStatus(pOrder.getEnrolled());
			cc3DSecureInfo.setAuthenticationStatus(pOrder.getPAResStatus());
			cc3DSecureInfo.setSignatureStatus(pOrder.getSignatureVerification());
		creditCardInfo.setCc3DSecureInfo(cc3DSecureInfo);
		//End: 258-3 - Verified by Visa - Order XML changes
		
		return creditCardInfo;
	}

	@SuppressWarnings("unchecked")
    private GiftCardInfoType populateGiftCardPayment(final BBBGiftCard pGiftCard, final ObjectFactory pFactory) throws BBBSystemException{
		final GiftCardInfoType giftCardInfo = pFactory.createGiftCardInfoType();
		try {
			giftCardInfo.setGiftCardNumber(this.getEncryptorTools().encryptString(pGiftCard.getCardNumber()));
			giftCardInfo.setPin(this.getEncryptorTools().encryptString(pGiftCard.getPin()));
		} catch (final EncryptorException e) {
			final String msg = "Error while encrypting Gift card details";
			this.getEncryptorTools().logError(msg, e);
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1063,msg, e);
		}

		final List<GiftCardStatus> authStatusList = pGiftCard.getAuthorizationStatus();
		if((authStatusList != null) && (authStatusList.size() > 0)) {
			GiftCardStatus paymentStatus = null;
			for(final GiftCardStatus paymentStat : authStatusList){
				if(paymentStat.getTransactionSuccess()){
					paymentStatus= paymentStat;
					break;
				}

			}
			if(null != paymentStatus){
			final GCAuthInfoType gcAuthInfo = pFactory.createGCAuthInfoType();
			gcAuthInfo.setAuthTime(this.getXMLCalendar(paymentStatus.getTransactionTimestamp()));
			if(!StringUtils.isBlank(paymentStatus.getTraceNumber())){
				gcAuthInfo.setTraceNumber(new BigInteger(paymentStatus.getTraceNumber()));
			}
			gcAuthInfo.setAuthCode(paymentStatus.getAuthCode());
			if(pGiftCard.getBalance() != null){
				final double previousBalance = new BigDecimal(pGiftCard.getBalance()).doubleValue();
				gcAuthInfo.setPreviousBalance(this.round(previousBalance));
			}
			gcAuthInfo.setResponseCode(paymentStatus.getAuthRespCode());

			giftCardInfo.setGcAuthInfo(gcAuthInfo);
			}
		}

		return giftCardInfo;
	}
	
	/**
	 * This method is used to insert paypal details in orderXml
	 * @param pOrder
	 * @param pPaypal
	 * @param pFactory
	 * @return
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	private PayPalInfoType populatePaypalPayment(final BBBOrder pOrder, final Paypal pPaypal, final ObjectFactory pFactory) throws BBBSystemException {
		final PayPalInfoType paypalInfo = pFactory.createPayPalInfoType();
		BBBHardGoodShippingGroup hardGoodShipping = null;
		final List<PaypalStatus> authStatusList = pPaypal.getAuthorizationStatus();
		if ((authStatusList != null) && (authStatusList.size() > 0)) {
			PaypalStatus paymentStatus = null;
			for (final PaypalStatus paymentStat : authStatusList) {
				if (paymentStat.getTransactionSuccess()) {
					paymentStatus = paymentStat;
					break;
				}

			}
			if (null != paymentStatus) {
					paypalInfo.setOrderTimeStamp(paymentStatus.getOrderTimestamp().toString());
					paypalInfo.setAuthTimeStamp(paymentStatus.getAuthTimeStamp().toString());
				paypalInfo.setPayPalOrderId(paymentStatus.getPaypalOrder());
				paypalInfo.setPayPalTransactionId(paymentStatus.getTransId());
				if(paymentStatus.getProtectionEligibility().equalsIgnoreCase(BBBPayPalConstants.PARTIALLY_ELIGIBLE)){
					paypalInfo.setPayPalSellerProtection(BBBCoreConstants.STRING_TWO);
				}
				else if(paymentStatus.getProtectionEligibility().equalsIgnoreCase(BBBPayPalConstants.ELIGIBLE)){
					paypalInfo.setPayPalSellerProtection(BBBCoreConstants.STRING_ONE);
				}
				else{
					paypalInfo.setPayPalSellerProtection(BBBCoreConstants.STRING_ZERO);
				}
				paypalInfo.setPayPalCorelationId(paymentStatus.getCorrelationId());
			}
		}
		paypalInfo.setPayPalPayerId(pPaypal.getPayerId());
		paypalInfo.setPayPalPayerEmail(pPaypal.getPayerEmail());
		if(pPaypal.getPayerStatus().equalsIgnoreCase(BBBPayPalConstants.UNVERIFIED)){
			paypalInfo.setPayPalUserVerified(BBBCoreConstants.STRING_TWO);
		}
		else if(pPaypal.getPayerStatus().equalsIgnoreCase(BBBPayPalConstants.VERIFIED)){
			paypalInfo.setPayPalUserVerified(BBBCoreConstants.STRING_ONE);
		}
		else {
			paypalInfo.setPayPalUserVerified(BBBCoreConstants.STRING_ZERO);
		}
		
		final List<ShippingGroup> shippingList = pOrder.getShippingGroups();
		for (final ShippingGroup shippingGroup : shippingList) {
			if (shippingGroup instanceof BBBHardGoodShippingGroup) {
				hardGoodShipping = (BBBHardGoodShippingGroup) shippingGroup;
				if (hardGoodShipping.isFromPaypal()) {
					if(null != hardGoodShipping.getAddressStatus() && hardGoodShipping.getAddressStatus().equalsIgnoreCase(BBBPayPalConstants.UNCONFIRMED)){
						paypalInfo.setPayPalShippingVerified(BBBCoreConstants.STRING_TWO);
					}
					else if(null != hardGoodShipping.getAddressStatus() && hardGoodShipping.getAddressStatus().equalsIgnoreCase(BBBPayPalConstants.CONFIRMED)){
						paypalInfo.setPayPalShippingVerified(BBBCoreConstants.STRING_ONE);
					}
					else{
						paypalInfo.setPayPalShippingVerified(BBBCoreConstants.STRING_ZERO);
					}
				}
			}
		}
		
		Address address = pOrder.getBillingAddress();
		if (null != address) {
			if (address instanceof BBBRepositoryContactInfo) {
				final BBBRepositoryContactInfo contactInfo = (BBBRepositoryContactInfo) address;
				if (contactInfo.isFromPaypal()) {
					paypalInfo.setPayPalBillingAddressUsed(contactInfo.isFromPaypal());
				}
				else{
					paypalInfo.setPayPalBillingAddressUsed(false);
				}
			}
		}
			paypalInfo.setPayPalInvoiceId(pOrder.getOnlineOrderNumber());
		return paypalInfo;
	}
	
	private PosInfoType populatePOSPayment(BBBOrder pOrder, PaymentGroup pPaymentGroup, ObjectFactory pFactory) {
		PosInfoType posInfoType = pFactory.createPosInfoType();
		posInfoType.setPosPayment("Y");
		return posInfoType;
	}
	
	
	private GiftInfoType populateGiftInfo(final BBBOrder pOrder, final BBBHardGoodShippingGroup hardGoodShipping, final OrderProcess pOrderProcess, final ObjectFactory pFactory) throws BBBSystemException, BBBBusinessException{
		if ((hardGoodShipping.containsGiftWrap() || !StringUtils.isBlank(hardGoodShipping.getGiftWrapMessage()))) {
			final GiftInfoType giftInfo = pFactory.createGiftInfoType();

			if (!StringUtils.isEmpty(hardGoodShipping.getGiftWrapMessage()) ) {
				giftInfo.setMessage(hardGoodShipping.getGiftWrapMessage());
			}

			final GiftWrapCommerceItem giftWrapItem = hardGoodShipping.getGiftWrapCommerceItem();
			if(giftWrapItem != null) {
				giftInfo.setCharge(this.round(giftWrapItem.getPriceInfo().getAmount()));
				giftInfo.setSkuId(giftWrapItem.getCatalogRefId());

				final Map<String, TaxPriceInfo> itemTaxPriceInfos = this.getShippingGroupLevelItemTaxPriceInfos(pOrder, hardGoodShipping);
				giftInfo.setGiftTaxInfo(this.populateTaxInfo(itemTaxPriceInfos.get(giftWrapItem.getId()), 1.0, false, new HashMap<TAX_TYPE, BigDecimal>(), pFactory));
			}
			return giftInfo;
		}
		if(isInternationalOrder(pOrder.getSalesChannel()))
		{
			final GiftInfoType giftInfo = pFactory.createGiftInfoType();
			giftInfo.setMessage(getGiftMessage());
			return giftInfo;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
    private Map<String, TaxPriceInfo> getShippingGroupLevelItemTaxPriceInfos(final BBBOrder pOrder, final ShippingGroup pShippingGroup){
		Map<String, TaxPriceInfo> itemLevelTaxPrices = new HashMap<String, TaxPriceInfo>();
		final TaxPriceInfo orderTaxPrice = pOrder.getTaxPriceInfo();
		if((orderTaxPrice != null) && (orderTaxPrice.getShippingItemsTaxPriceInfos() != null)){
			final TaxPriceInfo shippingGroupTaxPrice = (TaxPriceInfo)orderTaxPrice.getShippingItemsTaxPriceInfos().get(pShippingGroup.getId());
			if(shippingGroupTaxPrice != null){
				itemLevelTaxPrices = shippingGroupTaxPrice.getShippingItemsTaxPriceInfos();
			}
		}

		return itemLevelTaxPrices;
	}

	private XMLGregorianCalendar getXMLCalendar(final Date pDate) throws BBBSystemException{
		XMLGregorianCalendar xmlGregorianCalendar = null;
		final GregorianCalendar gCalendar = new GregorianCalendar();
		if(pDate != null) {
			gCalendar.setTime(pDate);
		}

		try {
			xmlGregorianCalendar = newXMLCalender(gCalendar);
		} catch (final DatatypeConfigurationException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1064,"Error while creating XML calendar instance using [" + gCalendar + "]", e);
		}

		return xmlGregorianCalendar;
	}

	/**
	 * @param gCalendar
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	protected XMLGregorianCalendar newXMLCalender(
			final GregorianCalendar gCalendar)
			throws DatatypeConfigurationException {
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
	}

	private XMLGregorianCalendar getXMLCalendarShipOnDate(final Date pDate)
			throws BBBSystemException {
		XMLGregorianCalendar xmlGregorianCalendar = null;
		final GregorianCalendar gCalendar = new GregorianCalendar();
		if (pDate != null) {
			gCalendar.setTime(pDate);
		}
		try {
			xmlGregorianCalendar = newXMLCalenderShipOnDate(gCalendar);
		} catch (final DatatypeConfigurationException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1064,
					"Error while creating XML calendar instance using ["
							+ gCalendar + "]", e);
		}

		return xmlGregorianCalendar;
	}

	/**
	 * @param gCalendar
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	protected XMLGregorianCalendar newXMLCalenderShipOnDate(
			final GregorianCalendar gCalendar)
			throws DatatypeConfigurationException {
		return DatatypeFactory.newInstance()
				.newXMLGregorianCalendarDate(gCalendar.get(Calendar.YEAR),
						gCalendar.get(Calendar.MONTH) + 1,
						gCalendar.get(Calendar.DATE),
						DatatypeConstants.FIELD_UNDEFINED );
	}

	private BigDecimal round(final double pValue){
		return BigDecimal.valueOf(this.getPricingTools().round(pValue, 2));
	}
	
	public boolean isInternationalSurchargeOn() {
		List<String> intShipSurchargeValue=null;
		Boolean isIntShipSurchargeOn=false;
		try {
			intShipSurchargeValue = this.getCatalogTools().getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_SURCHARGE_SWITCH);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of InternationalShipTools : checkIntSurchargeSwitch"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of InternationalShipTools : checkIntSurchargeSwitch"), e);

		}

		if(intShipSurchargeValue!=null && !intShipSurchargeValue.isEmpty()){
			isIntShipSurchargeOn=Boolean.valueOf(intShipSurchargeValue.get(0));
		}
		logDebug(" is international shipping Surcharge on ? "+isIntShipSurchargeOn);
		return isIntShipSurchargeOn;
	}
	
	
	
	
	public boolean isInternationalOrder(String saleChannel) {
		boolean isInternationalOrder = false;
		if (BBBInternationalShippingConstants.CHANNEL_DESKTOP_BFREE
				.equalsIgnoreCase(saleChannel)
				|| BBBInternationalShippingConstants.CHANNEL_MOBILE_APP_BFREE
						.equalsIgnoreCase(saleChannel)
				|| BBBInternationalShippingConstants.CHANNEL_MOBILE_BFREE
						.equalsIgnoreCase(saleChannel)) {
			isInternationalOrder = true;
		}
		return isInternationalOrder;
	}
	private String getTaxExemptID() throws BBBSystemException, BBBBusinessException
	{
		List<String> taxExemptIDList=null;
		String taxExemptID=null;
			taxExemptIDList =this.getCatalogTools().getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID);
			
			if(taxExemptIDList!=null && !taxExemptIDList.isEmpty()){
				taxExemptID=taxExemptIDList.get(0);
			}
			return taxExemptID;
	}
	private String getGiftMessage() throws BBBSystemException, BBBBusinessException
	{
		List<String> giftMessageList=null;
		String message=null;
		giftMessageList =this.getCatalogTools().getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_GIFT_MESSAGE);
			
			if(!BBBUtility.isListEmpty(giftMessageList)){
				message=giftMessageList.get(0);
			}
			return message;
	}

	/**
	 * Populate Assembly Fee info and assembly indicator if applicable for LTL item in ItemType item
	 * @param commerceItem
	 * @param pFactory
	 * @param itemTaxPriceInfos
	 * @param pShippingGroup
	 * @param order
	 * @param commerceItemRelation
	 * @param priceBean
	 * @param loopCounter
	 * @param useSplit 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceItemNotFoundException
	 * @throws InvalidParameterException
	 */
	private AssemblyFeeType populateAssemblyFeeInfo(CommerceItem commerceItem, final ObjectFactory pFactory, final Map<String, 
			TaxPriceInfo> itemTaxPriceInfos, BBBHardGoodShippingGroup pShippingGroup,BBBOrder order,final ShippingGroupCommerceItemRelationship commerceItemRelation,
			BBBDetailedItemPriceInfo priceBean,ItemType item, long itemQuantity, boolean isLastItem,
			final Map<TAX_TYPE, BigDecimal> assemblyFeeTaxMap, boolean useSplit, long totalQuantity) throws BBBSystemException, BBBBusinessException, CommerceItemNotFoundException, InvalidParameterException{
		logDebug("Method populateAssemblyFeeInfo() Start");
		item.setIsAssemblyInd(false);
		String assemblyCommerceId = ((BBBCommerceItem)commerceItem).getAssemblyItemId();
		logDebug("CommerceItem:"+commerceItem.getId()+" AssemblyCommerceId:"+assemblyCommerceId);
		if (BBBUtility.isNotEmpty(assemblyCommerceId)) {
			CommerceItem assemblyCommerceItem = order.getCommerceItem(assemblyCommerceId);
			if(null != assemblyCommerceItem) {
				AssemblyFeeType assemblyFee = pFactory.createAssemblyFeeType();
				assemblyFee.setSku(assemblyCommerceItem.getCatalogRefId());
				item.setIsAssemblyInd(true);
				final ItemPriceInfoType assemblyItemPriceInfo = pFactory.createItemPriceInfoType();
				if (assemblyCommerceItem.getPriceInfo() != null) {
					final double assemblyFeeRatio = (double) itemQuantity/totalQuantity;
					final double assmeblyItemUnitPrice = this.getPricingTools().round(assemblyCommerceItem.getPriceInfo().getAmount()/totalQuantity);
					assemblyItemPriceInfo.setUnitPrice(toBigDecimal(assmeblyItemUnitPrice));
					assemblyItemPriceInfo.setTotalAmount(toBigDecimal(this.getPricingTools().round(assmeblyItemUnitPrice * itemQuantity)));

						RepositoryItem dsLineItemTaxInfo=null;
						dsLineItemTaxInfo = ((BBBOrderTools)getOrderManager().getOrderTools()).fetchTaxInfoFromDPI(priceBean, useSplit ? BBBCoreConstants.DPI_ASSEMBLY_SPLIT : BBBCoreConstants.DPI_ASSEMBLY);
						logDebug("Tax info from repository: " + dsLineItemTaxInfo);
						//If order is old (i.e. before proration flow was removed) then traverse through proration flow otherwise search order tax info in repository.
						if (((BBBOrderImpl)order).isOldOrder()) {
							assemblyItemPriceInfo.setItemTaxInfo(this.populateTaxInfo(itemTaxPriceInfos.get(assemblyCommerceItem.getId()),
									assemblyFeeRatio, isLastItem, assemblyFeeTaxMap, pFactory));
						} else {
							assemblyItemPriceInfo.setItemTaxInfo(this.populateTaxInfoFromRepository(dsLineItemTaxInfo, pFactory));
						}
				}
				assemblyItemPriceInfo.setAdjustmentList(populateAssemblyItemOverrideAdjustment(assemblyCommerceItem, pFactory));
				assemblyFee.setItemPriceInfo(assemblyItemPriceInfo);
				logDebug("Method populateAssemblyFeeInfo() End");
				return assemblyFee;
			}
		}
		logDebug("Method populateAssemblyFeeInfo() ends with null AssemblyFeeType");
		return null;
	}
	
	/**
	 * Populate Delivery Fee info if applicable for LTL item in ItemType item
	 * @param commerceItem
	 * @param pFactory
	 * @param itemTaxPriceInfos
	 * @param pShippingGroup
	 * @param order
	 * @param commerceItemRelation
	 * @param promotionPriceBean
	 * @param loopCounter
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceItemNotFoundException
	 * @throws InvalidParameterException
	 */
	private ShipSurchargeType populateShipSurchargeInfo(CommerceItem commerceItem, final ObjectFactory pFactory, final Map<String, 
			TaxPriceInfo> itemTaxPriceInfos, BBBHardGoodShippingGroup pShippingGroup,BBBOrder order,final ShippingGroupCommerceItemRelationship commerceItemRelation,
			BBBDetailedItemPriceInfo promotionPriceBean, long itemQuantity, boolean isLastItem,
			final Map<TAX_TYPE, BigDecimal> shipSurchargeTaxMap, long totalQuantity) throws BBBSystemException, BBBBusinessException, CommerceItemNotFoundException, InvalidParameterException{
		logDebug("Method populateShipSurchargeInfo() Start");
		ItemPriceInfoType deliveryItemPriceInfo = pFactory.createItemPriceInfoType();
		String deliveryCommerceId = ((BBBCommerceItem)commerceItem).getDeliveryItemId();
		logDebug("CommerceItem:"+commerceItem.getId()+" DeliveryCommerceId:"+deliveryCommerceId);
		if (BBBUtility.isNotEmpty(deliveryCommerceId)) {
			CommerceItem deliveryCommerceItem = order.getCommerceItem(deliveryCommerceId);
			if(null != deliveryCommerceItem) {
				ShipSurchargeType shipSurcharge = pFactory.createShipSurchargeType();
				shipSurcharge.setSku(deliveryCommerceItem.getCatalogRefId());
				if (deliveryCommerceItem.getPriceInfo() != null) {
					final double shipSurchargeRatio = (double) itemQuantity/totalQuantity;
					
					deliveryItemPriceInfo.setUnitPrice(round(deliveryCommerceItem.getPriceInfo().getRawTotalPrice() / totalQuantity));
					deliveryItemPriceInfo.setTotalAmount((deliveryItemPriceInfo.getUnitPrice()).multiply(new BigDecimal(itemQuantity)));
					//deliveryItemPriceInfo.setTotalAmount(toBigDecimal(deliveryCommerceItem.getPriceInfo().getAmount()));
					boolean isProRatedSurcharge = (deliveryCommerceItem.getPriceInfo().getRawTotalPrice() - deliveryCommerceItem.getPriceInfo().getAmount()) > 0.00;
					if(!(isProRatedSurcharge)){
						RepositoryItem dsLineItemTaxInfo = ((BBBOrderTools)getOrderManager().getOrderTools()).
								fetchTaxInfoFromDPI(promotionPriceBean, BBBCoreConstants.DPI_DELIVERY);
						logDebug("Tax info from repository: " + dsLineItemTaxInfo);
						//If order is old (i.e. before proration flow was removed) then traverse through proration flow otherwise search order tax info in repository.
						if (((BBBOrderImpl)order).isOldOrder()) {
							deliveryItemPriceInfo.setItemTaxInfo(this.populateTaxInfo(itemTaxPriceInfos.get(deliveryCommerceId), shipSurchargeRatio, isLastItem, shipSurchargeTaxMap, pFactory));
						} else {
							deliveryItemPriceInfo.setItemTaxInfo(this.populateTaxInfoFromRepository(dsLineItemTaxInfo, pFactory));
						}
					}
					deliveryItemPriceInfo.setAdjustmentList(populateDeliveryItemOverrideAdjustment(deliveryCommerceItem, pFactory));
					
				}
				shipSurcharge.setItemPriceInfo(deliveryItemPriceInfo);
				return shipSurcharge;
			}
		}
		logDebug("Method populateAssemblyFeeInfo() ends with null ShipSurchargeType");
		return null;
	}
	
	
	
	/**
	 * Populate delivery item override adjustment.
	 *
	 * @param deliveryCommerceItem the delivery commerce item
	 * @param pFactory the factory
	 * @return the adjustment list type
	 */
	protected AdjustmentListType populateDeliveryItemOverrideAdjustment(CommerceItem deliveryCommerceItem, ObjectFactory pFactory){
		
		List<PricingAdjustment> adjustments =  deliveryCommerceItem.getPriceInfo().getAdjustments();
		AdjustmentType orderPriceAdjustment = null;
		for (final PricingAdjustment pricingAdj : adjustments) {
			if(pricingAdj.getAdjustmentDescription().equalsIgnoreCase("TBS Price Override")) {
				AdjustmentListType orderPriceAdjustmentList = pFactory.createAdjustmentListType();
				List<AdjustmentType> priceAdjustmentList = orderPriceAdjustmentList.getAdjustment();
				orderPriceAdjustment = pFactory.createAdjustmentType();
				orderPriceAdjustment.setDiscountAmount(this.round(Math.abs(pricingAdj.getAdjustment())));
				orderPriceAdjustment.setAtgPromotionId("PUBpromo999999");
				orderPriceAdjustment.setAdjustmentDesc("Override");
				orderPriceAdjustment.setAdjustmentType("Override");
				if (deliveryCommerceItem instanceof LTLDeliveryChargeCommerceItem) {
					LTLDeliveryChargeCommerceItem deliveryItm = (LTLDeliveryChargeCommerceItem) deliveryCommerceItem;
					
					TBSItemInfo lTbsItemInfo = deliveryItm.getTBSItemInfo();
					
					if(null != lTbsItemInfo){
						orderPriceAdjustment.setItemSurchargeOverride(BigDecimal.valueOf(lTbsItemInfo.getOverridePrice()));
						orderPriceAdjustment.setItemSurchargeOverrideReasonCode(lTbsItemInfo.getOverideReason());
						orderPriceAdjustment.setItemSurchargeOverrideCompetitor(lTbsItemInfo.getCompetitor());
					}
				}
				priceAdjustmentList.add(orderPriceAdjustment);
				return orderPriceAdjustmentList;
			}
		}
		return null;
	}
	
	
	/**
	 * Populate assembly item override adjustment.
	 *
	 * @param assemblyFeeCommerceItem the assembly fee commerce item
	 * @param pFactory the factory
	 * @return the adjustment list type
	 */
	protected AdjustmentListType populateAssemblyItemOverrideAdjustment(CommerceItem assemblyFeeCommerceItem, ObjectFactory pFactory){
		
		List<PricingAdjustment> adjustments =  assemblyFeeCommerceItem.getPriceInfo().getAdjustments();
		AdjustmentType orderPriceAdjustment = null;
		for (final PricingAdjustment pricingAdj : adjustments) {
			if(pricingAdj.getAdjustmentDescription().equalsIgnoreCase("TBS Price Override")) {
				AdjustmentListType orderPriceAdjustmentList = pFactory.createAdjustmentListType();
				List<AdjustmentType> priceAdjustmentList = orderPriceAdjustmentList.getAdjustment();
				orderPriceAdjustment = pFactory.createAdjustmentType();
				orderPriceAdjustment.setDiscountAmount(this.round(Math.abs(pricingAdj.getAdjustment())));
				orderPriceAdjustment.setAtgPromotionId("PUBpromo999999");
				orderPriceAdjustment.setAdjustmentDesc("Override");
				orderPriceAdjustment.setAdjustmentType("Override");
				if (assemblyFeeCommerceItem instanceof LTLAssemblyFeeCommerceItem) {
					LTLAssemblyFeeCommerceItem deliveryItm = (LTLAssemblyFeeCommerceItem) assemblyFeeCommerceItem;
					
					TBSItemInfo lTbsItemInfo = deliveryItm.getTBSItemInfo();
					
					if(null != lTbsItemInfo){
						orderPriceAdjustment.setAssemblyFeeOverride(BigDecimal.valueOf(lTbsItemInfo.getOverridePrice()));
						orderPriceAdjustment.setAssemblyFeeOverrideReasonCode(lTbsItemInfo.getOverideReason());
						orderPriceAdjustment.setAssemblyFeeOverrideCompetitor(lTbsItemInfo.getCompetitor());
					}
				}
				priceAdjustmentList.add(orderPriceAdjustment);
				return orderPriceAdjustmentList;
			}
		}
		return null;
	}
	
	
	/**
	 * Populate Pro Rated Delivery Fee info if applicable for LTL item in ItemType item
	 * @param commerceItem
	 * @param pFactory
	 * @param itemTaxPriceInfos
	 * @param pShippingGroup
	 * @param order
	 * @param commerceItemRelation
	 * @param promotionPriceBean
	 * @param loopCounter
	 * @param useSplit 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceItemNotFoundException
	 * @throws InvalidParameterException
	 */
	private ShipSurchargeType populateDiscountedShipSurchargeInfo(CommerceItem commerceItem, final ObjectFactory pFactory, final Map<String, 
			TaxPriceInfo> itemTaxPriceInfos, BBBHardGoodShippingGroup pShippingGroup,BBBOrder order,final ShippingGroupCommerceItemRelationship commerceItemRelation,
			BBBDetailedItemPriceInfo promotionPriceBean, long itemQuantity, boolean isLastItem,
			 final Map<TAX_TYPE, BigDecimal> shipSurchargeProratedTaxMap, boolean useSplit, long totalQuantity) 
					throws BBBSystemException, BBBBusinessException, CommerceItemNotFoundException, InvalidParameterException{
		logDebug("Method populateDiscountedShipSurchargeInfo() Start");
		ItemPriceInfoType deliveryItemPriceInfo = pFactory.createItemPriceInfoType();
		String deliveryCommerceId = ((BBBCommerceItem)commerceItem).getDeliveryItemId();
		logDebug("CommerceItem:"+commerceItem.getId()+" ProRatedDeliveryCommerceId:"+deliveryCommerceId);
		if (BBBUtility.isNotEmpty(deliveryCommerceId)) {
			CommerceItem deliveryCommerceItem = order.getCommerceItem(deliveryCommerceId);
			if(null != deliveryCommerceItem) {
				
				ShipSurchargeType shipSurcharge = pFactory.createShipSurchargeType();
				shipSurcharge.setSku(deliveryCommerceItem.getCatalogRefId());
				final TaxPriceInfo shipSurchargeTaxPriceInfo = itemTaxPriceInfos.get(deliveryCommerceId);
				if (deliveryCommerceItem.getPriceInfo() != null) {
					final double shipSurchargeRatio = (double) itemQuantity/totalQuantity;
					
					double deliveryItemUnitPrice = this.getPricingTools().round(deliveryCommerceItem.getPriceInfo().getAmount()/totalQuantity);
					double pennyDifference = this.getPricingTools().round(deliveryCommerceItem.getPriceInfo().getAmount() - deliveryItemUnitPrice * totalQuantity);
					deliveryItemUnitPrice = this.getPricingTools().round(isLastItem ? pennyDifference + deliveryItemUnitPrice : deliveryItemUnitPrice);
					
					deliveryItemPriceInfo.setUnitPrice(toBigDecimal(deliveryItemUnitPrice));
					deliveryItemPriceInfo.setTotalAmount(this.toBigDecimal(itemQuantity * deliveryItemUnitPrice));

						RepositoryItem dsLineItemTaxInfo=null;
						dsLineItemTaxInfo = ((BBBOrderTools)getOrderManager().getOrderTools()).fetchTaxInfoFromDPI(promotionPriceBean, useSplit ? BBBCoreConstants.DPI_DELIVERY_SPLIT : BBBCoreConstants.DPI_DELIVERY);
						logDebug("Tax info from repository: " + dsLineItemTaxInfo);
						//If order is old (i.e. before proration flow was removed) then traverse through proration flow otherwise search order tax info in repository.
						if (((BBBOrderImpl)order).isOldOrder()) {
							deliveryItemPriceInfo.setItemTaxInfo(this.populateTaxInfo(shipSurchargeTaxPriceInfo, shipSurchargeRatio, isLastItem, shipSurchargeProratedTaxMap, pFactory));
						} else {
							deliveryItemPriceInfo.setItemTaxInfo(this.populateTaxInfoFromRepository(dsLineItemTaxInfo, pFactory));
						}
				}
				shipSurcharge.setItemPriceInfo(deliveryItemPriceInfo);
				return shipSurcharge;
			}
		}
		logDebug("Method populateAssemblyFeeInfo() ends with null ProRatedShipSurchargeType");
		return null;
	}
	
	/**
	 *  LTL change Populate Delivery Dates in ItemType item 
	 * @param ltlSku
	 * @param item
	 * @param order
	 * @param shippingMethod
	 * @param state
	 * @param orderDate
	 */
	private void setEstimatedDeliveryDates(boolean ltlSku, boolean vdcSku, ItemType item,BBBOrder order,String shippingMethod,String state,Date orderDate) {
		String deliveryDate = "";
		try {
			if(!ltlSku) {
				if(vdcSku){
					deliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, item.getSku(), true, null, true);
				}else{
					deliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state , order.getSiteId(), orderDate, true);
				}
				logDebug("Delivery Date for shippingMethod "+shippingMethod+": "+deliveryDate);
			} else {
				deliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, order.getSiteId(), item.getSku(), orderDate, true);
				logDebug("Delivery Date for sku "+item.getSku()+": "+deliveryDate);
			}
		} catch (BBBBusinessException e1) {
			logError(ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD,e1);
		} catch (BBBSystemException e1) {
			logError(ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD,e1);
		}
		if(!BBBUtility.isEmpty(deliveryDate)) {
			String deliveryDates[] = deliveryDate.split("-");
			if(deliveryDates.length > 0 && !StringUtils.isBlank(deliveryDates[0])) {
				item.setEstimatedFromDeliveryDate(deliveryDates[0].trim());
			}
			if(deliveryDates.length > 1 && !StringUtils.isBlank(deliveryDates[1])) {
				item.setEstimatedToDeliveryDate(deliveryDates[1].trim());
			}
		}
	}

	/**
	 * @return the tBSSiteIdMap
	 */
	public Map<String, String> getTBSSiteIdMap() {
		return mTBSSiteIdMap;
	}

	/**
	 * @param pTBSSiteIdMap the tBSSiteIdMap to set
	 */
	public void setTBSSiteIdMap(Map<String, String> pTBSSiteIdMap) {
		mTBSSiteIdMap = pTBSSiteIdMap;
	}
}
