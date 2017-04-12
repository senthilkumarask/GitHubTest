package com.bbb.internationalshipping.integration.checkoutrequest;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.Address;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.Basket;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.Basket.BasketItems;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.BasketItem;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.BasketTotal;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.BuyerPreferences;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.BuyerSession;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.BuyerSessionDetails;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.CheckoutUrls;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.DomesticSession;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.DomesticShippingOptionV2;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.ItemPricing;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.Message;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.MessageHeader;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.ObjectFactory;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.OrderProperties;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.PayPalPaymentUrl;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.Payload;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.PaymentUrls;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.PriceBookType;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.ProductAttributes;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.SetCheckoutSessionRequest;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBBasketItemsVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBBasketTotalVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBDomesticShippingMethodVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBInternationalCheckoutRequestVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBOrderPropertiesVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBSessionDetailsVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;


/**
 * The class is the marshaller class which takes the International Checkout requestVo and
 * creates a XML request for the Https POST International Checkout API call. The class will require a
 * request VO object which will contain the request parameters for the XML
 * request.
 * 
 * 
 */
public class BBBInternationalCheckoutMarshaller extends BBBGenericService {
	
	// JAXBContext instance
	private static JAXBContext context;
	
	/**
	 * This method will prepare the XML by mapping the requestVo with jaxbVO
	 * with the help of JAXB Classes.
	 * @param checkoutRequestVO
	 * @return
	 * @throws BBBBusinessException
	 */
	public String prepareInternationalOrderRequest(
			final BBBInternationalCheckoutRequestVO checkoutRequestVO,String mexicoOrderSwitch) throws BBBBusinessException {
		logDebug("Entering class: BBBInternationalCheckoutMarshaller,  "
				+ "method : prepareInternationalOrderRequest");
		
		final ObjectFactory factory = new ObjectFactory();
		
		final MessageHeader messageHeader = factory.createMessageHeader();
		final Payload payload = this.createPayloadDetails(factory, checkoutRequestVO,mexicoOrderSwitch);
		final Message message = factory.createMessage();
		message.setPayload(payload);
		message.setHeader(messageHeader);
		
		final JAXBElement<Message> messageElement = factory.createMessage(message);
		final StringWriter stringWriter = new StringWriter();
		String requestXml = null;
		try {
			if(null == context){
				context= JAXBContext.newInstance(Message.class);
			}
			final Marshaller marshaller = getMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(messageElement, stringWriter);
			requestXml = stringWriter.getBuffer().toString();
		} catch (JAXBException e) {
				logError(LogMessageFormatter.formatMessage(null,
						"BBBInternationalCheckoutMarshaller.prepareInternationalOrderRequest() | BBBBusinessException "), e);
			throw new BBBBusinessException(BBBCatalogErrorCodes.JAXB_EXCEPTION,BBBCatalogErrorCodes.JAXB_EXCEPTION, e);
		}
		
		
		logDebug("International Checkout Request XML generated: " +requestXml);
		logDebug("Exiting class: BBBInternationalCheckoutMarshaller,  "
				+ "method : prepareInternationalOrderRequest");
		
		return requestXml;
	}

	protected Marshaller getMarshaller() throws JAXBException {
		return context.createMarshaller();
	}

	/**
	 * This method will prepare the PayLoadDetails.
	 * @param factory
	 * @param checkoutRequestVO
	 * @return
	 */
	private Payload createPayloadDetails(final ObjectFactory factory,
			final BBBInternationalCheckoutRequestVO checkoutRequestVO,String mexicoOrderSwitch) {
		
		logDebug("Entering class: BBBInternationalCheckoutMarshaller,  "
				+ "method : prepareInternationalOrderRequest");
		
		final Payload payload = factory.createPayload();
		final SetCheckoutSessionRequest sessionRequest = factory.createSetCheckoutSessionRequest();
		
		final DomesticSession domesticSession = factory.createDomesticSession();
		final BuyerSession buyerSession = factory.createBuyerSession();
		
		final Basket basket = this.createBasketDetails(factory, checkoutRequestVO,mexicoOrderSwitch);
		
		final DomesticShippingOptionV2 shippingOptionV2 = this.createDomesticShippingOption(factory, checkoutRequestVO);
		
		final OrderProperties orderProperties = this.createOrderProperties(factory, checkoutRequestVO);
		
		final BBBSessionDetailsVO sessionDetailsVO = checkoutRequestVO.getBbbSessionDetailsVO();
		final BuyerSessionDetails sessionDetails = this.createBuyerSessionDetails(factory, sessionDetailsVO);
		
		domesticSession.setMerchantId(checkoutRequestVO.getMerchantId());
		domesticSession.setDomesticShippingMethod(shippingOptionV2);
		domesticSession.setDomesticBasket(basket);
		domesticSession.setOrderProperties(orderProperties);
		domesticSession.setSessionDetails(sessionDetails);
		
		final Address address = factory.createAddress();
		address.setIsPoBox(false);
		address.setIsReadOnly(false);
		address.setCountryCode(sessionDetailsVO.getBuyerCountry());
		
		final BuyerPreferences buyerPreferences = factory.createBuyerPreferences();
		buyerPreferences.setLanguage("");
		buyerPreferences.setBuyerCurrency(sessionDetailsVO.getBuyerCurrency());
		
		/* removed shipping id as per comments from borderfree*/
		//buyerPreferences.setShippingMethodId(BBBInternationalShippingConstants.SHIPPING_METHOD_ID);
		
		buyerSession.setShipToAddress(address);
		buyerSession.setBuyerPreferences(buyerPreferences);
		sessionRequest.setDomesticSession(domesticSession);
		sessionRequest.setBuyerSession(buyerSession);
		
		payload.setSetCheckoutSessionRequest(sessionRequest);
		
		logDebug("Exiting class: BBBInternationalCheckoutMarshaller,  "
				+ "method : prepareInternationalOrderRequest");
		
		return payload;
	}
	
	/**
	 * This method will prepare the Basket Details.
	 * @param factory
	 * @param checkoutRequestVO
	 * @return
	 */
	private Basket createBasketDetails(final ObjectFactory factory,
			final BBBInternationalCheckoutRequestVO checkoutRequestVO,String mexicoOrderSwitch) {
		
		logDebug("Entering class: BBBInternationalCheckoutMarshaller,  "
				+ "method : createBasketDetails");
		final BBBSessionDetailsVO sessionDetailsVO = checkoutRequestVO.getBbbSessionDetailsVO();
		String currency = sessionDetailsVO.getBuyerCurrency();
		final Basket basket = factory.createBasket();
		final BasketItems basketItems = factory.createBasketBasketItems();
		final List<BBBBasketItemsVO> basketItemsList = checkoutRequestVO.getBbbDomesticBasketVO().getBbbBasketItemsVO();
		final Iterator<BBBBasketItemsVO> itemIterator = basketItemsList.iterator();
		while (itemIterator.hasNext()) {
			final BBBBasketItemsVO basketItemsVO = itemIterator.next();
			final ProductAttributes productAttributes = factory.createProductAttributes();
			final ItemPricing itemPricing = factory.createItemPricing(); 
			final BasketItem basketItem = factory.createBasketItem();
			basketItem.setSku(basketItemsVO.getSkuId());
			productAttributes.setColor(basketItemsVO.getBbbDisplayVO().getColor());
			productAttributes.setDescription(basketItemsVO.getBbbDisplayVO().getDescription());
			productAttributes.setName(basketItemsVO.getBbbDisplayVO().getName());
			productAttributes.setProductUrl(basketItemsVO.getBbbDisplayVO().getProductUrl());
			productAttributes.setImageUrl(basketItemsVO.getBbbDisplayVO().getImageUrl());
			productAttributes.setSize(basketItemsVO.getBbbDisplayVO().getSize());
			itemPricing.setItemDiscount(BigDecimal.valueOf(basketItemsVO.getBbbPricingVO().getItemDiscount()));
			itemPricing.setListPrice(BigDecimal.valueOf(basketItemsVO.getBbbPricingVO().getListPrice()));
			itemPricing.setProductExtraHandling(BigDecimal.valueOf(basketItemsVO.getBbbPricingVO().getProductExtraHandling()));
			itemPricing.setProductExtraShipping(BigDecimal.valueOf(basketItemsVO.getBbbPricingVO().getProductExtraShipping()));
			itemPricing.setSalePrice(BigDecimal.valueOf(basketItemsVO.getBbbPricingVO().getSalePrice()));
			basketItem.setPricing(itemPricing);
			basketItem.setDisplay(productAttributes);
			basketItem.setQuantity((int) basketItemsVO.getQuantity());
			basketItems.getBasketItem().add(basketItem);
		}
		
		final BasketTotal basketTotal = factory.createBasketTotal();
		final BBBBasketTotalVO basketTotalVO = checkoutRequestVO.getBbbDomesticBasketVO().getBbbBasketTotalVO();
		
		basketTotal.setOrderDiscount(BigDecimal.valueOf(basketTotalVO.getOrderDiscount()));
		basketTotal.setTotalPrice(BigDecimal.valueOf(basketTotalVO.getTotalPrice()));
		basketTotal.setTotalProductExtraShipping(BigDecimal.valueOf(basketTotalVO.getTotalProductExtraShipping()));
		basketTotal.setTotalProductExtraHandling(BigDecimal.valueOf(basketTotalVO.getTotalProductExtraHandling()));
		basketTotal.setTotalSalePrice(BigDecimal.valueOf(basketTotalVO.getTotalSalePrice()));
		if(!BBBUtility.isEmpty(currency) && currency.equalsIgnoreCase(BBBInternationalShippingConstants.CURRENCY_MEXICO)){
			basket.setCurrency(currency);
			if(mexicoOrderSwitch.equalsIgnoreCase(BBBInternationalShippingConstants.DUTIES)){
				basket.setIncludes(PriceBookType.DUTIES);}
			else if(mexicoOrderSwitch.equalsIgnoreCase(BBBInternationalShippingConstants.TAXES)){
					basket.setIncludes(PriceBookType.TAXES);
				}
			else if(mexicoOrderSwitch.equalsIgnoreCase(BBBInternationalShippingConstants.DUTIES_AND_TAXES)){
				basket.setIncludes(PriceBookType.DUTIES_AND_TAXES);
			
			}
		}
		basket.setBasketItems(basketItems);
		basket.setBasketTotal(basketTotal);
		basket.setCustomData(checkoutRequestVO.getBbbDomesticBasketVO().getCustomData());
		logDebug("Exiting class: BBBInternationalCheckoutMarshaller,  "
				+ "method : createBasketDetails");
		
		return basket;
	}
	
	/**
	 * This method is used to create the Domestic Shipping details.
	 * @param factory
	 * @param checkoutRequestVO
	 * @return
	 */
	private DomesticShippingOptionV2 createDomesticShippingOption(final ObjectFactory factory,
			final BBBInternationalCheckoutRequestVO checkoutRequestVO) {
		
		logDebug("Entering class: BBBInternationalCheckoutMarshaller,  "
				+ "method : createDomesticShippingOption");
	
		final DomesticShippingOptionV2 shippingOptionV2 = factory.createDomesticShippingOptionV2();
		final BBBDomesticShippingMethodVO methodVO = checkoutRequestVO.getBbbDomesticShippingMethodVO();
		
		shippingOptionV2.setDeliveryPromiseMaximum(methodVO.getDeliveryPromiseMaximum());
		shippingOptionV2.setDeliveryPromiseMinimum(methodVO.getDeliveryPromiseMinimum());
		shippingOptionV2.setDomesticHandlingPrice(BigDecimal.valueOf(methodVO.getDomesticHandlingPrice()));
		shippingOptionV2.setDomesticShippingPrice(BigDecimal.valueOf(methodVO.getDomesticShippingPrice()));
		shippingOptionV2.setExtraInsurancePrice(BigDecimal.valueOf(methodVO.getExtraInsurancePrice()));
		
		logDebug("Exiting class: BBBInternationalCheckoutMarshaller,  "
				+ "method : createDomesticShippingOption");
		
		return shippingOptionV2;
	}
	
	/**
	 * This method is used to create the Order Property details.
	 * @param factory
	 * @param checkoutRequestVO
	 * @return
	 */
	private OrderProperties createOrderProperties(final ObjectFactory factory,
			final BBBInternationalCheckoutRequestVO checkoutRequestVO) {
		
		logDebug("Entering class: BBBInternationalCheckoutMarshaller,  "
				+ "method : createOrderProperties");
		
		final OrderProperties orderProperties = factory.createOrderProperties();
		final BBBOrderPropertiesVO orderPropertiesVO = checkoutRequestVO.getBbbOrderPropertiesVO();
		orderProperties.setCurrencyQuoteId((int) orderPropertiesVO.getCurrencyQuoteId());
		
		if (null != orderPropertiesVO.getLcpRuleId()) {
			orderProperties.setLcpRuleId(Integer.valueOf(orderPropertiesVO.getLcpRuleId()));
		}
		orderProperties.setMerchantOrderId(orderPropertiesVO.getMerchantOrderId());
		orderProperties.setMerchantOrderRef(orderPropertiesVO.getMerchantOrderRef());
		
		logDebug("Exiting class: BBBInternationalCheckoutMarshaller,  "
				+ "method : createOrderProperties");
		
		return orderProperties;
	}
	
	/**
	 * This method is used to create the Buyer Session Details.
	 * @param factory
	 * @param checkoutRequestVO
	 * @param sessionDetailsVO
	 * @return
	 */
	private BuyerSessionDetails createBuyerSessionDetails(final ObjectFactory factory, final BBBSessionDetailsVO sessionDetailsVO) {
		
		logDebug("Entering class: BBBInternationalCheckoutMarshaller,  "
				+ "method : createBuyerSessionDetails");
	
		final BuyerSessionDetails details = factory.createBuyerSessionDetails();
						
		final CheckoutUrls checkoutUrls = factory.createCheckoutUrls();
		checkoutUrls.setBasketUrl(sessionDetailsVO.getCheckoutBasketUrl());
		checkoutUrls.setFailureUrl(sessionDetailsVO.getCheckoutFailureUrl());
		checkoutUrls.setPendingUrl(sessionDetailsVO.getCheckoutPendingUrl());
		checkoutUrls.setUsCartStartPageUrl(sessionDetailsVO.getCheckoutUSCartStartPageUrl());
		checkoutUrls.setSuccessUrl(sessionDetailsVO.getCheckoutSuccessUrl());
		
		final PaymentUrls paymentUrls = factory.createPaymentUrls();
		final PayPalPaymentUrl payPalPaymentUrl = factory.createPayPalPaymentUrl();
		payPalPaymentUrl.setReturnUrl(sessionDetailsVO.getPayPalReturnUrl());
		payPalPaymentUrl.setHeaderLogoUrl(sessionDetailsVO.getPayPalHeaderLogoUrl());
		payPalPaymentUrl.setCancelUrl(sessionDetailsVO.getPayPalCancelUrl());
		
		paymentUrls.setPayPalUrls(payPalPaymentUrl);
		
		checkoutUrls.setPaymentUrls(paymentUrls);
		
		details.setBuyerIpAddress(sessionDetailsVO.getBuyerIpAddress());
		details.setBuyerSessionId(sessionDetailsVO.getBuyerSessionId());
		details.setCheckoutUrls(checkoutUrls);
		
		logDebug("Exiting class: BBBInternationalCheckoutMarshaller,  "
				+ "method : createBuyerSessionDetails");
		
		return details;
	}
}
