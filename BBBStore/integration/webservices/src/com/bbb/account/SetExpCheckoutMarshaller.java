/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: ssh108
 *
 * Created on: 18-Feb-2014
 * --------------------------------------------------------------------------------
 */

package com.bbb.account;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;

import paypalapi.api.ebay.RequesterCredentialsDocument;
import paypalapi.api.ebay.SetExpressCheckoutReqDocument;
import paypalapi.api.ebay.SetExpressCheckoutReqDocument.SetExpressCheckoutReq;
import paypalapi.api.ebay.SetExpressCheckoutRequestDocument;
import paypalapi.api.ebay.SetExpressCheckoutRequestType;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;
import atg.core.util.StringUtils;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.paypal.BBBPayPalCredentials;
import com.bbb.paypal.BBBSetExpressCheckoutReq;
import com.bbb.paypal.PayPalProdDescVO;

import corecomponenttypes.apis.ebay.BasicAmountType;
import eblbasecomponents.apis.ebay.AddressStatusCodeType;
import eblbasecomponents.apis.ebay.AddressType;
import eblbasecomponents.apis.ebay.CountryCodeType;
import eblbasecomponents.apis.ebay.CurrencyCodeType;
import eblbasecomponents.apis.ebay.CustomSecurityHeaderType;
import eblbasecomponents.apis.ebay.PaymentActionCodeType;
import eblbasecomponents.apis.ebay.PaymentDetailsItemType;
import eblbasecomponents.apis.ebay.PaymentDetailsType;
import eblbasecomponents.apis.ebay.SetExpressCheckoutRequestDetailsDocument;
import eblbasecomponents.apis.ebay.SetExpressCheckoutRequestDetailsType;
import eblbasecomponents.apis.ebay.UserIdPasswordType;

/**
 * The class is the marshaller class which takes the payPal requestVo and
 * creates a XML request for the Webservice to call The class will require a
 * request VO object which will contain the request parameters for the XML
 * request
 * 
 * @author ssh108
 * 
 */
public class SetExpCheckoutMarshaller extends RequestMarshaller {

	private static final String ASSEMBLY_TOTAL = "Assembly Total";
	private static final String DELIVERY_TOTAL = "Delivery Total";
	private static final String GIFT_WRAP = "Gift Wrap";
	private static final String GIFT_CARD = "Gift Card";
	private static final String TAX = "Tax";
	private static final String PST = "PST";
	private static final String GST = "GST/HST";
	private static final String SHIPPING = "Shipping";
	private static final String EHF = "EHF";
	private static final String SHIPPINGSURCHARGE = "Shipping Surcharge";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BBBPayPalCredentials cred;

	
		

	/**
	 * The method is extension of the RequestMarshaller service which will take
	 * the request Vo object and create a XML Object for the webservice
	 * 
	 * @param ServiceRequestIF
	 *            the validate payPal pRreqVO vo
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public XmlObject buildRequest(ServiceRequestIF pReqVo) throws BBBBusinessException, BBBSystemException {
		
		logDebug("Entry buildRequest of SetExpCheckoutMarshaller with ServiceRequestIF object:" + pReqVo.getServiceName());
		
		BBBPerformanceMonitor.start("SetExpCheckoutMarshaller-buildRequest");

		SetExpressCheckoutReqDocument setExpressCheckoutDoc = null;
		
		try {
			setExpressCheckoutDoc = SetExpressCheckoutReqDocument.Factory.newInstance();
			//**********************Paypal Request VO**********************
			SetExpressCheckoutReq setExpressCheckoutReq = setExpressCheckoutDoc.addNewSetExpressCheckoutReq();
			SetExpressCheckoutRequestDocument setExpressCheckoutRequestDocument = SetExpressCheckoutRequestDocument.Factory.newInstance();
			SetExpressCheckoutRequestType setExpressCheckoutRequestType = setExpressCheckoutRequestDocument.addNewSetExpressCheckoutRequest();
			SetExpressCheckoutRequestDetailsDocument setExpressCheckoutRequestDetailDocument = SetExpressCheckoutRequestDetailsDocument.Factory.newInstance();
			SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetailType = setExpressCheckoutRequestDetailDocument.addNewSetExpressCheckoutRequestDetails();

			//**********************BBB Request VO***********************
			BBBSetExpressCheckoutReq req = (BBBSetExpressCheckoutReq) pReqVo;
			String cancelURL = req.getSetExpressCheckoutRequest().getSetExpressCheckoutRequestDetails().getCancelURL();
			String sucessURL = req.getSetExpressCheckoutRequest().getSetExpressCheckoutRequestDetails().getReturnURL();

			setExpressCheckoutRequestDetailType.setCancelURL(cancelURL);
			setExpressCheckoutRequestDetailType.setReturnURL(sucessURL);
			setExpressCheckoutRequestDetailType.setReqBillingAddress(BBBCoreConstants.STRING_ONE);
			
			this.cred = req.getCred();
			BBBOrder order= req.getOrder();
			String currID = req.getSetExpressCheckoutRequest().getSetExpressCheckoutRequestDetails().getOrderTotal().getCurrencyID();
			logDebug("currencyID:" +currID);
		
			//Sale – This is a final sale for which you are requesting payment (default).
			//Authorization – This payment is a basic authorization subject to settlement with PayPal Authorization and Capture.
			//Order – This payment is an order authorization subject to settlement with PayPal Authorization and Capture
			//Optional
			setExpressCheckoutRequestDetailType.setPaymentAction(PaymentActionCodeType.ORDER);
			
			
			if(req.getLocal() != null && req.getLocal().equalsIgnoreCase("US")){
				setExpressCheckoutRequestDetailType.setLocaleCode("US");
			} else {
				setExpressCheckoutRequestDetailType.setLocaleCode("CA");
			}
			
			List<ShippingGroup> shpGrp = order.getShippingGroups();
			logDebug("Request from Cart Page " + req.isFromCart());
			String shippingGroupID = req.getShippingGroupId();
			
			if(StringUtils.isEmpty(shippingGroupID)){
				setExpressCheckoutRequestDetailType.setNoShipping(BBBCoreConstants.STRING_ONE);
			} else  {
				addShippingGroup(shpGrp, shippingGroupID,setExpressCheckoutRequestDetailType);
			}
			
			//Check whether there is shipping address or not
			if(setExpressCheckoutRequestDetailType.getAddress() != null){
				//0 – The PayPal pages should not display the shipping address.
				//1 – The PayPal pages should display the shipping address.
				//Required
				setExpressCheckoutRequestDetailType.setAddressOverride(this.cred.getAddressOverRide().toString());
				
			}
			
			//Required************Set Version****************
			setExpressCheckoutRequestType.setVersion(this.cred.getVersion());
			
			List<CommerceItem> commerceItemLists = order.getCommerceItems();
			PaymentDetailsType paymentDetailsType = calculate(req, commerceItemLists);
			
			PaymentDetailsType[] typeP = new PaymentDetailsType[1];
			typeP[0] = paymentDetailsType;
			setExpressCheckoutRequestDetailType.setPaymentDetailsArray(typeP);
			/*else {
				BasicAmountType newOrderTotal = setExpressCheckoutRequestDetailType.addNewOrderTotal();
				DecimalFormat df = new DecimalFormat("####0.00");
				if (currID != null && currID.equalsIgnoreCase(CurrencyCodeType.CAD.toString())) {
					newOrderTotal.setCurrencyID(CurrencyCodeType.CAD);
				} else {
					newOrderTotal.setCurrencyID(CurrencyCodeType.USD);
				}
				newOrderTotal.setStringValue(df.format(Double.parseDouble(req.getOrderTotal())));
				setExpressCheckoutRequestDetailType.setOrderTotal(newOrderTotal);
			}*/
			setExpressCheckoutRequestType.setSetExpressCheckoutRequestDetails(setExpressCheckoutRequestDetailType);
			setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequestType);
		} finally {
			BBBPerformanceMonitor.end("SetExpCheckoutMarshaller-buildRequest");
		}
		
		logDebug("Exit buildRequest of SetExpCheckoutMarshaller with XmlObject object:" + setExpressCheckoutDoc.getClass());
		
		return setExpressCheckoutDoc;

	}

	

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @throws BBBSystemException
	 * 
	 */
	@Override
	public XmlObject buildHeader() throws BBBSystemException, BBBBusinessException {
		
		logDebug("Entry buildHeader of PayPalServiceMarshaller:");
		
		
		if (this.cred != null) {
			BBBPerformanceMonitor.start("PayPalServiceMarshaller-buildHeader");
			RequesterCredentialsDocument credDoc = RequesterCredentialsDocument.Factory.newInstance();
			CustomSecurityHeaderType sechdr = credDoc.addNewRequesterCredentials();

			UserIdPasswordType idType = sechdr.addNewCredentials();
			idType.setUsername(this.cred.getUserName());
			idType.setPassword(this.cred.getPassword());
			idType.setSignature(this.cred.getSignature());
			idType.setSubject(this.cred.getSubject());
			sechdr.setCredentials(idType);
			credDoc.setRequesterCredentials(sechdr);
			BBBPerformanceMonitor.end("PayPalServiceMarshaller-buildHeader");
			
			logDebug("Exit buildHeader of PayPalServiceMarshaller:");
			
			return credDoc;
		}
		logError("BBBPaypalCredentials null");
		throw new BBBBusinessException(BBBCoreErrorConstants.PAYPAL_REQUEST_CRED_NULL_EXCEPTION);

	}
	
	
	/**
	 * The method is used to add shipping address if user have shipping address in his profile
	 * 
	 * @param List,SetExpressCheckoutRequestDetailsType
	 *           
	 * @return SetExpressCheckoutRequestDetailsType return SetExpressCheckoutRequestDetailsType after adding shipping address
	 * 
	 */
	private static void addShippingGroup(List<ShippingGroup> shpGrp, String pSGID, SetExpressCheckoutRequestDetailsType pSetExpRewDeType){
		//Add Shipping Address If user have in its profile.
		AddressType address = null;
		
		ShippingGroup grp= null;
		
		for(ShippingGroup fgrp:shpGrp){
			if(fgrp.getId().equalsIgnoreCase(pSGID)){
				grp = fgrp;
			}
		}
		
			if(grp instanceof BBBHardGoodShippingGroup && StringUtils.isBlank(((BBBHardGoodShippingGroup)grp).getRegistryInfo())){
				//************Set Shipping Address****************
				BBBHardGoodShippingGroup hrd = (BBBHardGoodShippingGroup)grp;
				Address addrHrd = hrd.getShippingAddress();
				if(addrHrd.getCity() != null && addrHrd.getAddress1()!=null ){
				
				address=  pSetExpRewDeType.addNewAddress();
				
				address.setAddressID(addrHrd.getOwnerId());
				address.setCityName(addrHrd.getCity());
				if(!StringUtils.isEmpty(addrHrd.getMiddleName())) {
					address.setName(addrHrd.getFirstName() + BBBCoreConstants.SPACE + addrHrd.getMiddleName() + BBBCoreConstants.SPACE + addrHrd.getLastName());
				} else {
					address.setName(addrHrd.getFirstName() + BBBCoreConstants.SPACE + addrHrd.getLastName());
				}
				address.setStreet1(addrHrd.getAddress1());
				String addr2 = addrHrd.getAddress2();
				String addr3 = addrHrd.getAddress3();
				if(!StringUtils.isEmpty(addr2)) {
					address.setStreet2(addr2);
					if(!StringUtils.isEmpty(addr3)){
							address.setStreet2(	addr2 + BBBCoreConstants.SPACE + addr3);
					}
				} else if(!StringUtils.isEmpty(addr3)) {
					address.setStreet2(addr3);
				} 
				
				address.setPostalCode(addrHrd.getPostalCode());
				address.setAddressStatus(AddressStatusCodeType.CONFIRMED);
				if(addrHrd.getCountry() != null && !addrHrd.getCountry().equalsIgnoreCase("US")){
					address.setCountry(CountryCodeType.CA);
				} else { 
					address.setCountry(CountryCodeType.US);
				}
				address.setStateOrProvince(addrHrd.getState());
				}
			}
		
	}
	
	/**
	 * The method is used to add Payment details from order
	 * 
	 * @param BBBSetExpressCheckoutReq,List<CommerceItem>
	 *          
	 * @return PaymentDetailsType
	 * 
	 */
	
	private PaymentDetailsType calculate(BBBSetExpressCheckoutReq req,List<CommerceItem> commerceItemLists) {
		
		PaymentDetailsType paymentDetailsType = PaymentDetailsType.Factory.newInstance();
		List<PaymentDetailsItemType> paymentDetailsList = new ArrayList<PaymentDetailsItemType>();
		String currID = req.getSetExpressCheckoutRequest().getSetExpressCheckoutRequestDetails().getOrderTotal().getCurrencyID();
		CurrencyCodeType.Enum currency = CurrencyCodeType.USD;
		if (currID != null && currID.equalsIgnoreCase(CurrencyCodeType.CAD.toString())) {
			currency = CurrencyCodeType.CAD;
		}
		
		Double itemtotal = 0.0;
		int giftWrapCount = 0;
		Map<String,PayPalProdDescVO> map =  req.getItem();
		//Iterate over Map containing all items and populate Paypal VO
		for (String prodItem : map.keySet()) {
				PayPalProdDescVO prodDesc = map.get(prodItem);
    			PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();

    			BigInteger itemQuantity = BigInteger.valueOf(prodDesc.getQuantity());
    			Double amountNew = prodDesc.getAmount();
    			//*************Add Item Amount***********
    			BasicAmountType amount = item.addNewAmount();
    			amount.setStringValue(amountNew.toString());
    			amount.setCurrencyID(currency);
    			item.setQuantity(itemQuantity);
    			item.setName(prodDesc.getSkuName());
    			item.setAmount(amount);
    			item.setDescription(prodDesc.getSkuDescription());
    			item.setItemURL(prodDesc.getProdURL());
    			itemtotal = itemtotal + (itemQuantity.doubleValue())*(amountNew.doubleValue());
    			paymentDetailsList.add(item);
        }
		
		for(CommerceItem commerceItem : commerceItemLists){
			if(commerceItem instanceof GiftWrapCommerceItem){
	    		giftWrapCount++;
	    	}
		}
		
		//Add Each item as separate Line item i.e. Gift wrap, EHF, PST, GST/HST irrespective of Gift card
		itemtotal = populatePaypalLineItems(giftWrapCount, req, itemtotal, paymentDetailsList, currency, paymentDetailsType);
		
		String ltlDeliveryTotal = req.getLtlDeliveryTotal();
		Double ltlDeliveryTotalAmount = Double.parseDouble(ltlDeliveryTotal);
		if (ltlDeliveryTotalAmount > 0.0) {
			PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
			BasicAmountType amount = item.addNewAmount();
			//BigInteger itemQuantity = BigInteger.valueOf(1);
			amount.setStringValue(ltlDeliveryTotalAmount.toString());
			amount.setCurrencyID(currency);
			item.setName(DELIVERY_TOTAL);
			//item.setQuantity(itemQuantity);
			item.setAmount(amount);
			itemtotal = itemtotal + ltlDeliveryTotalAmount;
			paymentDetailsList.add(item);
		}
		
		String ltlAssemblyTotal = req.getLtlAssemblyTotal();
		Double ltlAssemblyTotalAmount = Double.parseDouble(ltlAssemblyTotal);
		if (ltlAssemblyTotalAmount > 0.0) {
			PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
			BasicAmountType amount = item.addNewAmount();
			//BigInteger itemQuantity = BigInteger.valueOf(1);
			amount.setStringValue(ltlAssemblyTotalAmount.toString());
			amount.setCurrencyID(currency);
			item.setName(ASSEMBLY_TOTAL);
			//item.setQuantity(itemQuantity);
			item.setAmount(amount);
			itemtotal = itemtotal + ltlAssemblyTotalAmount;
			paymentDetailsList.add(item);
		}
		
		double giftCardPresent = Double.parseDouble(req.getGiftCardAmount());
		logDebug("Gift Card Amount" + giftCardPresent);
		//populate shipping, Tax, Gift Card as line item if gift card is present
		if(giftCardPresent > 0.0){ 
			itemtotal = populateItemsGiftCard(req, itemtotal, paymentDetailsList, currency, paymentDetailsType);
		}
				
		
		PaymentDetailsItemType[] paymentDetailsItemType = new PaymentDetailsItemType[paymentDetailsList.size()];
		int i=0;
		for (PaymentDetailsItemType paymentDetailsType2 : paymentDetailsList) {
			paymentDetailsItemType[i] = paymentDetailsType2;
			i++;
		}
		paymentDetailsType.setPaymentDetailsItemArray(paymentDetailsItemType);
		
		//*******************Add New OrderTotal*******************
		BasicAmountType newOrderTotal = paymentDetailsType.addNewOrderTotal();
		DecimalFormat df = new DecimalFormat("####0.00");
		if (currID != null && currID.equalsIgnoreCase(CurrencyCodeType.CAD.toString())) {
			newOrderTotal.setCurrencyID(CurrencyCodeType.CAD);
		} else {
			newOrderTotal.setCurrencyID(CurrencyCodeType.USD);
		}
		newOrderTotal.setStringValue(df.format(Double.parseDouble(req.getOrderTotal())));
		paymentDetailsType.setOrderTotal(newOrderTotal);
		
		//*******************Add New Item Total*******************
		BasicAmountType itemTotal = paymentDetailsType.addNewItemTotal();
		paymentDetailsType.setItemTotal(itemTotal);
		itemTotal.setCurrencyID(currency);
		
		//*************Truncate price to two decimal places******************
		itemTotal.setStringValue(df.format(itemtotal));
		
		//populate items in case there is no gift card in order
		if(giftCardPresent == 0.0){
			populateItemsNoGiftCard(req, currency, paymentDetailsType);
		}
		
		return paymentDetailsType;
	}
	
	/**
	 * This method is to populate paymentDetails in case we have gift card in order
	 * 
	 * @param req
	 * @param itemtotal
	 * @param paymentDetailsList
	 * @param curr
	 * @param paymentDetailsType
	 * @return
	 */
	private Double populateItemsGiftCard(BBBSetExpressCheckoutReq req, Double itemtotal, List<PaymentDetailsItemType> paymentDetailsList, 
								CurrencyCodeType.Enum curr, PaymentDetailsType paymentDetailsType){
		
		logDebug("populateItemsGiftCard() method: Start :: Populating items when we have gift card in order");
		//*******************Add New Shipping Total*******************
		String shippingCost = req.getOrderShippingCost();
		if(shippingCost != null){
			double shippingAmount = Double.parseDouble(shippingCost);
			if (shippingAmount > 0.0) {
				PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
				BasicAmountType amount = item.addNewAmount();
				amount.setStringValue(shippingCost);
				amount.setCurrencyID(curr);
				item.setName(SHIPPING);
				item.setAmount(amount);
				itemtotal = itemtotal + shippingAmount;
				paymentDetailsList.add(item);
			}
		}
		//*******************Add new Tax Total*******************
		String orderTax = req.getOrderTax();
		if(orderTax != null){
			double taxAmount = Double.parseDouble(orderTax);
			if (taxAmount > 0.0) {
				PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
				BasicAmountType amount = item.addNewAmount();
				amount.setStringValue(orderTax);
				amount.setCurrencyID(curr);
				item.setName(TAX);
				item.setAmount(amount);
				itemtotal = itemtotal + taxAmount;
				paymentDetailsList.add(item);
			}
		}
		
		//*******************Add Gift Card Amount*******************
		String giftCardAmount = req.getGiftCardAmount();
		double giftAmount = Double.parseDouble(giftCardAmount);
		if (giftAmount > 0.0) {
			PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
			BasicAmountType amount = item.addNewAmount();
			//BigInteger itemQuantity = BigInteger.valueOf(1);
			amount.setStringValue("-" + giftCardAmount);
			amount.setCurrencyID(curr);
			item.setName(GIFT_CARD);
			//item.setQuantity(itemQuantity);
			item.setAmount(amount);
			itemtotal = itemtotal - giftAmount;
			paymentDetailsList.add(item);
		}
		logDebug("populateItemsGiftCard() method: End");
		
		//*******************Add New Shipping Total*******************
		String shipAndHand = req.getSurcharge();
		if(shipAndHand != null){
			if(!StringUtils.isEmpty(shipAndHand)){
				double shipAndHandCost = Double.parseDouble(shipAndHand);
				if (shipAndHandCost > 0.0) {
					PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
					BasicAmountType amount = item.addNewAmount();
					amount.setStringValue(shipAndHand);
					amount.setCurrencyID(curr);
					item.setName(SHIPPINGSURCHARGE);
					item.setAmount(amount);
					itemtotal = itemtotal + shipAndHandCost;
					paymentDetailsList.add(item);
				}
			}
		}
		return itemtotal;
	}
	
	/**
	 * This method is to populate items when we don't have any gift card in order
	 * 
	 * @param req
	 * @param curr
	 * @param paymentDetailsType
	 */
	private void populateItemsNoGiftCard(BBBSetExpressCheckoutReq req, 
			CurrencyCodeType.Enum curr, PaymentDetailsType paymentDetailsType){
		
		logDebug("populateItemsNoGiftCard() method: Start");
		logDebug("Populating items when we have no gift card");
		//*******************Add New Shipping Total*******************
		BasicAmountType shpamount = paymentDetailsType.addNewShippingTotal();
		shpamount.setCurrencyID(curr);
		shpamount.setStringValue(req.getOrderShippingCost());
		paymentDetailsType.setShippingTotal(shpamount);
		
		//*******************Add new Tax Total*******************
		BasicAmountType taxAmount = paymentDetailsType.addNewTaxTotal();
		paymentDetailsType.setTaxTotal(taxAmount);
		taxAmount.setCurrencyID(curr);
		taxAmount.setStringValue(req.getOrderTax());
		
		//*******************Add new Surcharge*******************
		BasicAmountType surcharge = paymentDetailsType.addNewHandlingTotal();
		paymentDetailsType.setHandlingTotal(surcharge);
		surcharge.setCurrencyID(curr);
		surcharge.setStringValue(req.getSurcharge());
		logDebug("populateItemsNoGiftCard() method: End");
	}
	
	/**
	 * This method populates Gift wrap, EHF, PST, GST/HST as Line item
	 * to display on paypal left side
	 * 
	 */
	private Double populatePaypalLineItems(int giftWrapCount, BBBSetExpressCheckoutReq req, Double itemtotal, List<PaymentDetailsItemType> paymentDetailsList, 
			CurrencyCodeType.Enum currency, PaymentDetailsType paymentDetailsType){
		
		DecimalFormat df = new DecimalFormat("####0.00");
		logDebug("add Gift wrap as an item to display on paypal");
		//*******************Add Gift Wrap amount as a new item*******************
		String giftWrap = req.getGiftWrapTotal();
		Double giftWrapAmount = Double.parseDouble(giftWrap);
		if (giftWrapAmount > 0.0 && giftWrapCount > 0) {
			giftWrapAmount = giftWrapAmount/giftWrapCount;
			PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
			BasicAmountType amount = item.addNewAmount();
			//BigInteger itemQuantity = BigInteger.valueOf(1);
			amount.setStringValue(df.format(giftWrapAmount));
			amount.setCurrencyID(currency);
			item.setName(GIFT_WRAP);
			item.setQuantity(BigInteger.valueOf(giftWrapCount));
			item.setAmount(amount);			
			itemtotal = itemtotal + (giftWrapAmount*giftWrapCount);
			paymentDetailsList.add(item);
		}
		
		logDebug("add Ehf as an item to display on paypal");
		//*******************Add Ehf amount as a new item*******************
		String ehf = req.getEhfAmount();
		double ehfAmount = Double.parseDouble(ehf);
		if (ehfAmount > 0.0) {
			PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
			BasicAmountType amount = item.addNewAmount();
			amount.setStringValue(df.format(ehfAmount));
			amount.setCurrencyID(currency);
			item.setName(EHF);
			item.setAmount(amount);
			itemtotal = itemtotal + ehfAmount;
			paymentDetailsList.add(item);
		}
		
		logDebug("add estimated PST as an item to display on paypal");
		//*******************Add PST amount as a new item*******************
		String estimatedPST = req.getEstimatedPST();
		double estimatedPSTAmount = Double.parseDouble(estimatedPST);
		if (estimatedPSTAmount > 0.0) {
			PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
			BasicAmountType amount = item.addNewAmount();
			amount.setStringValue(df.format(estimatedPSTAmount));
			amount.setCurrencyID(currency);
			item.setName(PST);
			item.setAmount(amount);
			itemtotal = itemtotal + estimatedPSTAmount;
			paymentDetailsList.add(item);
		}
		
		logDebug("add estimated GST/HST as an item to display on paypal");
		//*******************Add GST/HST amount as a new item*******************
		String estimatedGSTHST = req.getEstimatedGSTHST();
		double estimatedGSTHSTAmount = Double.parseDouble(estimatedGSTHST);
		if (estimatedGSTHSTAmount > 0.0) {
			PaymentDetailsItemType item = paymentDetailsType.addNewPaymentDetailsItem();
			BasicAmountType amount = item.addNewAmount();
			amount.setStringValue(df.format(estimatedGSTHSTAmount));
			amount.setCurrencyID(currency);
			item.setName(GST);
			item.setAmount(amount);
			itemtotal = itemtotal + estimatedGSTHSTAmount;
			paymentDetailsList.add(item);
		}
		return itemtotal;
	}
}
