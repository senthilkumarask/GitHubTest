package com.bbb.internationalshipping.manager;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.math.BigInteger;
import java.util.UUID;
import java.util.GregorianCalendar;
import javax.servlet.http.Cookie;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.xml.datatype.XMLGregorianCalendar;
import net.sf.json.JSONObject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.springframework.web.util.HtmlUtils;

import atg.adapter.gsa.GSARepository;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderServices;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.StringUtils;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverterManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.xml.AddException;
import atg.repository.xml.AddService;
import atg.repository.xml.GetException;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBDesEncryptionTools;
import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.inventory.InventoryDecrementVO;
import com.bbb.commerce.inventory.OnlineInventoryManagerImpl;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.internationalshipping.integration.checkoutrequest.BBBInternationalCartSubmissionService;
import com.bbb.internationalshipping.integration.checkoutrequest.BBBInternationalCheckoutMarshaller;
import com.bbb.internationalshipping.integration.checkoutrequest.BBBInternationalCheckoutUnMarshaller;
import com.bbb.internationalshipping.utils.BBBInternationalShippingBuilder;
import com.bbb.internationalshipping.utils.InternationalOrderXmlRepoTools;
import com.bbb.internationalshipping.vo.BBBInternationalOrderSubmitVO;
import com.bbb.internationalshipping.vo.BBBInternationalPropertyManagerVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBBasketItemsVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBBasketTotalVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBDisplayVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBDomesticBasketVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBDomesticShippingMethodVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBInternationalCheckoutInputVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBInternationalCheckoutRequestVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBInternationalCheckoutResponseVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBOrderPropertiesVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBPricingVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBSessionDetailsVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.framework.integration.util.ServiceHandlerUtil;

// 
/**
 * This is a manager class for delegating the request for preparing
 * international checkout context, preparing international cart xml,sending cart
 * xml to transporter,getting the response from transporter and sending back the
 * response object to load the international checkout page.
 * 
 * 
 * @version 1.0
 */
public class InternationalCheckoutManager extends BBBGenericService {

	private static final String E_VAR30 = "eVar30=";
	private static final String COMMA = ",";
	private static final String SEMICOLON = ";";
	private static final String PIPE = "|";
	/** The Constant maxRandomNumberExclusive. */
	private static final int MAXRANDOMNUMBEREXCLUSIVE = 10;
	//private final String SHIPPING_SURCHARGE_CAP = "shippingSurchargeCap";
	
	private static final String QTY = "qty";
	private static final String AMT = "amt";
	private static final String ITEM = "item";
	
	private static final String GEN_ORDER_CODE = "genOrderCode";
	private static final String CJ_ITEM_APPENDED_URL = "cj_item_appended_url";
	private static final String CJ_TYPE = "cj_type";
	private static final String CJ_BASE_URL = "cj_base_url";
	private static final String CJ_CID = "cj_cid";
	
	private static final String RESX_EVENT_TYPE = "resxEventType";
	private static final String PRODUCT_IDS = "productIds";
	private static final String ITEM_QTYS = "itemQtys";
	private static final String ITEM_AMOUNTS = "itemAmounts";
	private static final String GRAND_TOTAL = "grandTotal";
	
	private static final String CJ_CID_BABY = "cj_cid_baby";
	private static final String CJ_CID_US = "cj_cid_us";
	private static final String CJ_TYPE_INTL = "cj_type_order_confirmation_intl";
	
	/** The international shipping builder. */
	private BBBInternationalShippingBuilder internationalShippingBuilder;

	/** The checkout manager. */
	private BBBCheckoutManager checkoutManager;

	/** The international repository. */
	private Repository internationalRepository;

	/** The catalog repository. */
	private Repository catalogRepository;
	private HashMap<String, String> dataCenterMap;
	/** The shipping method list. */
	private List<String> shippingMethodList;

	/** The cart submission service. */
	private BBBInternationalCartSubmissionService cartSubmissionService;

	/** The shipping method id. */
	private String shippingMethodId;
	/** The id generator. */
	private IdGenerator idGenerator;

	/** The dc prefix. */
	private String dcPrefix;

	/** The order cookie name. */
	private String orderCookieName;

	/** The order cookie age. */
	private int orderCookieAge;

	/** The order cookie path. */
	private String orderCookiePath;

	/** The site context path. */
	private String siteContextPath;

	/** The m bbb des encryption tools. */
	private BBBDesEncryptionTools mBBBDesEncryptionTools;

	/** The m user profile. */
	private String mUserProfile;

	/** The online order prefix key. */
	private String onlineOrderPrefixKey;

	/** The cart and check out config type. */
	private String cartAndCheckOutConfigType;

	/** The order services. */
	private OrderServices orderServices;

	/** The intl repository. */
	private InternationalOrderXmlRepoTools intlRepository;

	private BBBPricingTools mPricingTools;

	private BBBOrderManager orderManager;
	
	private GiftRegistryManager registryManager;

	/** The inventory manager. */
	private OnlineInventoryManagerImpl inventoryManager;
	
	/**
	 * @return the inventoryManager
	 */
	public final OnlineInventoryManagerImpl getInventoryManager() {
		return inventoryManager;
	}

	/**
	 * @param inventoryManager the inventoryManager to set
	 */
	public final void setInventoryManager(
			OnlineInventoryManagerImpl inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public BBBOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	/**
	 * @param pPricingTools - pricing tools.
	 */
	public void setPricingTools(final BBBPricingTools pPricingTools) {
		this.mPricingTools = pPricingTools;
	}

	/**
	 * @return mPricingTools - pricing tools.
	 */
	public BBBPricingTools getPricingTools() {
		return this.mPricingTools;
	}

	/**
	 * Gets the id generator.
	 * 
	 * @return the id generator
	 */
	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	/**
	 * Sets the id generator.
	 * 
	 * @param idGenerator
	 *            the new id generator
	 */
	public void setIdGenerator(final IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	/**
	 * Gets the dc prefix.
	 * 
	 * @return the dc prefix
	 */
	public String getDcPrefix() {
		return dcPrefix;
	}

	/**
	 * Sets the dc prefix.
	 * 
	 * @param dcPrefix
	 *            the new dc prefix
	 */
	public void setDcPrefix(final String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

	/**
	 * Gets the order cookie name.
	 * 
	 * @return the order cookie name
	 */
	public String getOrderCookieName() {
		return orderCookieName;
	}

	/**
	 * Sets the order cookie name.
	 * 
	 * @param orderCookieName
	 *            the new order cookie name
	 */
	public void setOrderCookieName(final String orderCookieName) {
		this.orderCookieName = orderCookieName;
	}

	/**
	 * Gets the order cookie age.
	 * 
	 * @return the order cookie age
	 */
	public int getOrderCookieAge() {
		return orderCookieAge;
	}

	/**
	 * Sets the order cookie age.
	 * 
	 * @param orderCookieAge
	 *            the new order cookie age
	 */
	public void setOrderCookieAge(final int orderCookieAge) {
		this.orderCookieAge = orderCookieAge;
	}

	/**
	 * Gets the order cookie path.
	 * 
	 * @return the order cookie path
	 */
	public String getOrderCookiePath() {
		return orderCookiePath;
	}

	/**
	 * Sets the order cookie path.
	 * 
	 * @param orderCookiePath
	 *            the new order cookie path
	 */
	public void setOrderCookiePath(final String orderCookiePath) {
		this.orderCookiePath = orderCookiePath;
	}

	/**
	 * Gets the site context path.
	 * 
	 * @return the site context path
	 */
	public String getSiteContextPath() {
		return siteContextPath;
	}

	/**
	 * Sets the site context path.
	 * 
	 * @param siteContextPath
	 *            the new site context path
	 */
	public void setSiteContextPath(final String siteContextPath) {
		this.siteContextPath = siteContextPath;
	}

	/**
	 * Gets the bBB des encryption tools.
	 * 
	 * @return the bBB des encryption tools
	 */
	public BBBDesEncryptionTools getBBBDesEncryptionTools() {
		return mBBBDesEncryptionTools;
	}

	/**
	 * Sets the bBB des encryption tools.
	 * 
	 * @param mBBBDesEncryptionTools
	 *            the new bBB des encryption tools
	 */
	public void setBBBDesEncryptionTools(final 
			BBBDesEncryptionTools mBBBDesEncryptionTools) {
		this.mBBBDesEncryptionTools = mBBBDesEncryptionTools;
	}

	/**
	 * Gets the user profile.
	 * 
	 * @return the user profile
	 */
	public String getUserProfile() {
		return mUserProfile;
	}

	/**
	 * Sets the user profile.
	 * 
	 * @param mUserProfile
	 *            the new user profile
	 */
	public void setUserProfile(final String mUserProfile) {
		this.mUserProfile = mUserProfile;
	}

	/**
	 * Gets the online order prefix key.
	 * 
	 * @return the online order prefix key
	 */
	public String getOnlineOrderPrefixKey() {
		return onlineOrderPrefixKey;
	}

	/**
	 * Sets the online order prefix key.
	 * 
	 * @param onlineOrderPrefixKey
	 *            the new online order prefix key
	 */
	public void setOnlineOrderPrefixKey(final String onlineOrderPrefixKey) {
		this.onlineOrderPrefixKey = onlineOrderPrefixKey;
	}

	/**
	 * Gets the cart and check out config type.
	 * 
	 * @return the cart and check out config type
	 */
	public String getCartAndCheckOutConfigType() {
		return cartAndCheckOutConfigType;
	}

	/**
	 * Sets the cart and check out config type.
	 * 
	 * @param cartAndCheckOutConfigType
	 *            the new cart and check out config type
	 */
	public void setCartAndCheckOutConfigType(final String cartAndCheckOutConfigType) {
		this.cartAndCheckOutConfigType = cartAndCheckOutConfigType;
	}

	/**
	 * Gets the order services.
	 * 
	 * @return the order services
	 */
	public OrderServices getOrderServices() {
		return orderServices;
	}

	/**
	 * Sets the order services.
	 * 
	 * @param orderServices
	 *            the new order services
	 */
	public void setOrderServices(final OrderServices orderServices) {
		this.orderServices = orderServices;
	}

	/**
	 * Gets the intl repository.
	 * 
	 * @return the intl repository
	 */
	public InternationalOrderXmlRepoTools getIntlRepository() {
		return intlRepository;
	}

	/**
	 * Sets the intl repository.
	 * 
	 * @param intlRepository
	 *            the new intl repository
	 */
	public void setIntlRepository(final InternationalOrderXmlRepoTools intlRepository) {
		this.intlRepository = intlRepository;
	}

	/**
	 * Gets the shipping method id.
	 *
	 * @return the shippingeMethodId
	 */
	public final String getShippingMethodId() {
		return shippingMethodId;
	}

	/**
	 * Sets the shipping method id.
	 *
	 * @param shippingMethodId the new shipping method id
	 */
	public final void setShippingMethodId(final String shippingMethodId) {
		this.shippingMethodId = shippingMethodId;
	}

	/**
	 * Gets the cart submission service.
	 *
	 * @return the cartSubmissionService
	 */
	public final BBBInternationalCartSubmissionService getCartSubmissionService() {
		return cartSubmissionService;
	}

	/**
	 * Sets the cart submission service.
	 *
	 * @param cartSubmissionService the cartSubmissionService to set
	 */
	public final void setCartSubmissionService(final 
			BBBInternationalCartSubmissionService cartSubmissionService) {
		this.cartSubmissionService = cartSubmissionService;
	}

	/**
	 * Gets the shipping method list.
	 *
	 * @return the shipping method list
	 */
	public List<String> getShippingMethodList() {
		return shippingMethodList;
	}

	/**
	 * Sets the shipping method list.
	 *
	 * @param shippingMethodList the new shipping method list
	 */
	public void setShippingMethodList(final List<String> shippingMethodList) {
		this.shippingMethodList = shippingMethodList;
	}

	/**
	 * Gets the catalog repository.
	 *
	 * @return the catalogRepository
	 */
	public final Repository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * Sets the catalog repository.
	 *
	 * @param catalogRepository the catalogRepository to set
	 */
	public final void setCatalogRepository(final Repository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}


	/**
	 * This variable is used to point to internationalPropertyManager.
	 */
	private BBBInternationalPropertyManagerVO internationalPropertyManager;

	/** The catalog tools. */
	private BBBCatalogTools catalogTools;


	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * Gets the international property manager.
	 *
	 * @return the internationalPropertyManager
	 */
	public final BBBInternationalPropertyManagerVO getInternationalPropertyManager() {
		return internationalPropertyManager;
	}

	/**
	 * Sets the international property manager.
	 *
	 * @param internationalPropertyManager the internationalPropertyManager to set
	 */
	public final void setInternationalPropertyManager(final 
			BBBInternationalPropertyManagerVO internationalPropertyManager) {
		this.internationalPropertyManager = internationalPropertyManager;
	}

	/**
	 * Gets the international repository.
	 *
	 * @return the internationalRepository
	 */
	public final Repository getInternationalRepository() {
		return internationalRepository;
	}

	/**
	 * Sets the international repository.
	 *
	 * @param internationalRepository the internationalRepository to set
	 */
	public final void setInternationalRepository(final Repository internationalRepository) {
		this.internationalRepository = internationalRepository;
	}

	/**
	 * Gets the checkout manager.
	 *
	 * @return the checkoutManager
	 */
	public final BBBCheckoutManager getCheckoutManager() {
		return checkoutManager;
	}

	/**
	 * Sets the checkout manager.
	 *
	 * @param checkoutManager the checkoutManager to set
	 */
	public final void setCheckoutManager(final BBBCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}

	/**
	 * Gets the international shipping builder.
	 *
	 * @return the internationalShippingBuilder
	 */
	public final BBBInternationalShippingBuilder getInternationalShippingBuilder() {
		return internationalShippingBuilder;
	}

	/**
	 * Sets the international shipping builder.
	 *
	 * @param internationalShippingBuilder the internationalShippingBuilder to set
	 */
	public final void setInternationalShippingBuilder(final 
			BBBInternationalShippingBuilder internationalShippingBuilder) {
		this.internationalShippingBuilder = internationalShippingBuilder;
	}
	/** The transaction manager. */
	private TransactionManager transactionManager;
	private GSARepository profileAdapterRepository;
	/** The international order xml repo tools. */
	private InternationalOrderXmlRepoTools internationalOrderXmlRepoTools;
	public InternationalOrderXmlRepoTools getInternationalOrderXmlRepoTools() {
		return internationalOrderXmlRepoTools;
	}

	public void setInternationalOrderXmlRepoTools(
			InternationalOrderXmlRepoTools internationalOrderXmlRepoTools) {
		this.internationalOrderXmlRepoTools = internationalOrderXmlRepoTools;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public GSARepository getProfileAdapterRepository() {
		return profileAdapterRepository;
	}

	public void setProfileAdapterRepository(GSARepository profileAdapterRepository) {
		this.profileAdapterRepository = profileAdapterRepository;
	}

	public GiftRegistryManager getRegistryManager() {
		return registryManager;
	}

	public void setRegistryManager(GiftRegistryManager registryManager) {
		this.registryManager = registryManager;
	}

	/**
	 * This method handles the International checkout flow.
	 * First it creates the checkoutContextVO with the input checkoutInputVO
	 * Secondly it will Marshall the checkoutContextVO to the International Checkout JAXB classes and create the request XML
	 * Then it will call the International checkout Service with the request XML and get the response XML.
	 * It will UnMarshall the response to the BBBInternationalCheckoutResponseVO and send the response back to InternationalShipFormHandler
	 *
	 * @param checkoutInputVO the checkout input vo
	 * @return the bBB international checkout response vo
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException BBBInternationalCheckoutRequestVO
	 */

	public final BBBInternationalCheckoutResponseVO checkoutInternationalOrder(final BBBInternationalCheckoutInputVO checkoutInputVO) throws BBBSystemException, BBBBusinessException {
		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : checkoutInternationalOrder");
		final BBBInternationalCheckoutRequestVO checkoutContextVO = this.getCheckoutContext(checkoutInputVO);
		final BBBInternationalCheckoutMarshaller checkoutMarshaller = new BBBInternationalCheckoutMarshaller();
		
		String mexicoOrderSwitch="NONE";
		if(checkoutContextVO.getBbbSessionDetailsVO().getBuyerCountry().equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)){
			List<String> intShipSurchargeValue=null;
			try {
				 intShipSurchargeValue = this.getCatalogTools().getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
						BBBInternationalShippingConstants.CONFIG_KEY_MEXICOORDER_TAXANDDUTIES);
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null,
						"BBBSystemException from service of InternationalShipTools : checkIntSurchargeSwitch"), e);
	
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null,
						"BBBBusinessException from service of InternationalShipTools : checkIntSurchargeSwitch"), e);
	
			}
			if(null!=intShipSurchargeValue){
				mexicoOrderSwitch=intShipSurchargeValue.get(0);
			}
		}
		
		String requestXml = checkoutMarshaller.prepareInternationalOrderRequest(checkoutContextVO,mexicoOrderSwitch);
		requestXml=requestXml.replaceAll("&amp;", "&");
		requestXml=changeString(requestXml); 
		logDebug("Inside class: InternationalCheckoutManager, Request XML : " + requestXml);
		final String responseXml = this.getCartSubmissionService().submitInternationalCart(requestXml);
		logDebug("Inside class: InternationalCheckoutManager, Response XML : " + responseXml);
		final BBBInternationalCheckoutResponseVO checkoutResponseVO= new BBBInternationalCheckoutResponseVO();
		final BBBInternationalCheckoutUnMarshaller checkoutUnMarshaller = new BBBInternationalCheckoutUnMarshaller();
		checkoutUnMarshaller.handleResponse(responseXml, checkoutResponseVO);
		logDebug("Exiting class: checkoutInternationalOrder");
		return checkoutResponseVO;
	}

	private String changeString(String requestXml)
	{
		requestXml=changeXmlString(requestXml,"successUrl");
		requestXml=changeXmlString(requestXml,"pendingUrl");
		requestXml=changeXmlString(requestXml,"failureUrl");
		return requestXml;
	}
	private String changeXmlString(String requestXml,String tag)
	{
		int beginIndex=requestXml.indexOf("<"+tag+">");
		int endIndex =	requestXml.indexOf("</"+tag+">");
		beginIndex=requestXml.indexOf("?",beginIndex)+1;
		while(beginIndex!=0 && beginIndex>0)
		{
			String name=requestXml.substring(beginIndex, endIndex);			 
			String newname=name.replaceAll("&", "&amp;");
			requestXml=requestXml.replaceAll(name, newname); 
			beginIndex=requestXml.indexOf("<"+tag+">", endIndex);
			endIndex =	requestXml.indexOf("</"+tag+">",beginIndex+1);
		}
		return requestXml;
	}
	/**
	 * Gets the checkout context.
	 *
	 * @param checkoutInputVO the checkout input vo
	 * @return the checkout context
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	private BBBInternationalCheckoutRequestVO getCheckoutContext(final BBBInternationalCheckoutInputVO checkoutInputVO) throws BBBSystemException, BBBBusinessException {
		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : getCheckoutContext");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.INTERNATIONAL_CHECKOUT
				+ " getCheckoutContext");
		final BBBInternationalCheckoutRequestVO checkoutContextVO = new BBBInternationalCheckoutRequestVO();
		final String merchantId = this.getInternationalShippingBuilder().getMerchantId();
		if(BBBUtility.isNotEmpty(merchantId)) {
			checkoutInputVO.setMerchantId(merchantId);
			final BBBDomesticBasketVO domesticBasketVO = this.getDomesticBasketDetails(checkoutInputVO);
			final BBBDomesticShippingMethodVO shippingMethodVO = this.getDomesticShippingMethodDetails(checkoutInputVO);
			final BBBSessionDetailsVO sessionDetailsVO = this.getDomesticSessionDetails(checkoutInputVO);
			final BBBOrderPropertiesVO orderPropertiesVO = this.getOrderPropertiesDetails(checkoutInputVO);
			checkoutContextVO.setMerchantId(merchantId);
			checkoutContextVO.setBbbDomesticBasketVO(domesticBasketVO);
			checkoutContextVO.setBbbDomesticShippingMethodVO(shippingMethodVO);
			checkoutContextVO.setBbbSessionDetailsVO(sessionDetailsVO);
			checkoutContextVO.setBbbOrderPropertiesVO(orderPropertiesVO);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.INTERNATIONAL_CHECKOUT
					+ " getCheckoutContext");
		} else {
			logError("Inside Class : InternationalCheckoutManager : Merchant Id is Empty or Null");
			throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1006, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1006);
		}
		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : getCheckoutContext");
		return checkoutContextVO;
	}


	/**
	 * This method fills the Order Property VO.
	 *
	 * @param checkoutInputVO the checkout input vo
	 * @return the order properties details
	 * @throws BBBSystemException the bBB system exception
	 */
	private BBBOrderPropertiesVO getOrderPropertiesDetails(final BBBInternationalCheckoutInputVO checkoutInputVO) throws BBBSystemException {
		final BBBOrderPropertiesVO orderPropertiesVO = new BBBOrderPropertiesVO();
		orderPropertiesVO.setMerchantOrderId(checkoutInputVO.getMerchantOrderId());
		orderPropertiesVO.setMerchantOrderRef(checkoutInputVO.getOrder().getId());
		orderPropertiesVO.setCurrencyQuoteId(this.getCurrencyQuoteId(checkoutInputVO.getCurrencyCode()));
		orderPropertiesVO.setLcpRuleId(this.getLcpRuleId(checkoutInputVO.getCountryCode(), checkoutInputVO.getMerchantId()));
		return orderPropertiesVO;
	}

	/**
	 * This method fills the Domestic Session Details VO.
	 *
	 * @param checkoutInputVO the checkout input vo
	 * @return the domestic session details
	 * @throws BBBBusinessException the bBB business exception
	 * @throws BBBSystemException the bBB system exception
	 */
	private BBBSessionDetailsVO getDomesticSessionDetails(final BBBInternationalCheckoutInputVO checkoutInputVO) throws BBBBusinessException, BBBSystemException {
		final BBBSessionDetailsVO bbDetailsVO = new BBBSessionDetailsVO();
		final Map<String,String> hostMap = this.getHostMap();
		String hostPath = null;
		String serverName = null;
		String contextPath = null;

		if(!BBBUtility.isMapNullOrEmpty(hostMap)) {
			hostPath = hostMap.get(BBBInternationalShippingConstants.HOST_PATH);
			serverName = hostMap.get(BBBInternationalShippingConstants.SERVER_NAME);
			contextPath = hostMap.get(BBBInternationalShippingConstants.CONTEXT_PATH);
		}

		String paypalHeaderLogoUrl = null;
		Properties prop=new Properties();
		String orderTotal=null;
		try {
		 orderTotal=	TagConverterManager
			.getTagConverterByName(BBBCoreConstants.UNFORMATTED_CURRENCY)
			.convertObjectToString(
					ServletUtil.getCurrentRequest(), Double.valueOf(checkoutInputVO.getOrder().getPriceInfo().getAmount()),
					prop).toString();
		} catch (TagConversionException e) {
			logDebug("TagConversionException while converting Currency", e);
		}
		final String parameterRequired = "&orderId=" + checkoutInputVO.getOrder().getId() + "&merchantOrderId=" + checkoutInputVO.getMerchantOrderId() + "&countryCode=" + checkoutInputVO.getCountryCode()
				+ "&currencyCode=" + checkoutInputVO.getCurrencyCode() + "&orderTotal=" + orderTotal;

		final List<String> headerList = this.getCatalogTools().getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, 
				BBBInternationalShippingConstants.PAYPAL_HEADER_LOGO_KEY);

		if(!BBBUtility.isListEmpty(headerList)) {
			paypalHeaderLogoUrl = headerList.get(0);
		}

		String hostUrl = BBBCoreConstants.HTTPS + BBBCoreConstants.CONSTANT_SLASH + serverName ;

		bbDetailsVO.setBuyerIpAddress(checkoutInputVO.getUserIPAddress());
		bbDetailsVO.setBuyerSessionId(checkoutInputVO.getUserSessionId());
		bbDetailsVO.setBuyerCountry(checkoutInputVO.getCountryCode());
		bbDetailsVO.setBuyerCurrency(checkoutInputVO.getCurrencyCode());
		bbDetailsVO.setCheckoutBasketUrl(hostPath + contextPath + "/cart?envoy2cart=true");
		bbDetailsVO.setCheckoutSuccessUrl(hostUrl + contextPath + "/checkoutSuccess?" + "fraudState=success" + parameterRequired);
		bbDetailsVO.setCheckoutFailureUrl(hostUrl + contextPath + "/checkoutFailure?" +"fraudState=failure" + parameterRequired);
		bbDetailsVO.setCheckoutPendingUrl(hostUrl + contextPath + "/checkoutPending?" +"fraudState=pending" + parameterRequired);
		bbDetailsVO.setCheckoutUSCartStartPageUrl(hostPath + contextPath + "/cart");
		bbDetailsVO.setPayPalReturnUrl(hostUrl + contextPath + "/paypalReturn");
		bbDetailsVO.setPayPalHeaderLogoUrl(hostUrl + paypalHeaderLogoUrl);
		bbDetailsVO.setPayPalCancelUrl(hostUrl + contextPath + "/paypalCancel");
		return bbDetailsVO;
	}

	/**
	 * This method fills the Domestic Shipping Method Details VO.
	 *
	 * @param checkoutInputVO the checkout input vo
	 * @return the domestic shipping method details
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	private BBBDomesticShippingMethodVO getDomesticShippingMethodDetails(final BBBInternationalCheckoutInputVO checkoutInputVO) throws BBBSystemException, BBBBusinessException {
		final BBBDomesticShippingMethodVO shippingMethodVO = new BBBDomesticShippingMethodVO();
		int minDaysToShip = 0;
		int maxDaysToShip = 0;
		double shippingCost = 0.00;
		HardgoodShippingGroup hd = null;

		for(final String shippingMethod : this.getShippingMethodList()) {
			final List<String> shippingList = this.getCatalogTools().getAllValuesForKey(
					BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, shippingMethod);
			if(!BBBUtility.isListEmpty(shippingList)){
				if(shippingMethod.equals(BBBInternationalShippingConstants.CONFIG_KEY_SHIPPING_WITHOUT_PROMOTION) && shippingList.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
					if(!checkoutInputVO.getOrder().getShippingGroups().isEmpty() && (checkoutInputVO.getOrder().getShippingGroups().get(0) instanceof HardgoodShippingGroup))				
						hd =(HardgoodShippingGroup) checkoutInputVO.getOrder().getShippingGroups().get(0);

					try {
							if(null!=hd){
								shippingCost = getPricingTools().calculateShippingCost(checkoutInputVO.getSiteId(), hd.getShippingMethod(), hd, null);
							}
					} catch (PricingException e) {
						logError("Exception While calculating Shipping Price For International Orders", e);
						throw new BBBSystemException(e.getMessage());
					}

					shippingMethodVO.setDomesticShippingPrice(Double.valueOf(this.roundOff(shippingCost)));
					break;
				} else if (shippingMethod.equals(BBBInternationalShippingConstants.CONFIG_KEY_SHIPPING_WITH_PROMOTION) && shippingList.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
					shippingMethodVO.setDomesticShippingPrice(Double.valueOf(this.roundOff(checkoutInputVO.getShippingAmount())));
					break;
				} else {
					shippingMethodVO.setDomesticShippingPrice(0.00);			
				}
			}
		}

		final RepositoryItem shippingItem = this.catalogTools.getShippingDuration(this.getShippingMethodId(), checkoutInputVO.getSiteId());
		if(null != shippingItem) {

			if (shippingItem.getPropertyValue(BBBCmsConstants.MIN_DAYS_TO_SHIP) != null) {
				minDaysToShip = (Integer) shippingItem.getPropertyValue(BBBCmsConstants.MIN_DAYS_TO_SHIP);
			}
			if (shippingItem.getPropertyValue(BBBCmsConstants.MAX_DAYS_TO_SHIP) != null) {
				maxDaysToShip = (Integer) shippingItem.getPropertyValue(BBBCmsConstants.MAX_DAYS_TO_SHIP);
			}
		}

		shippingMethodVO.setDeliveryPromiseMaximum(maxDaysToShip);
		shippingMethodVO.setDeliveryPromiseMinimum(minDaysToShip);
		shippingMethodVO.setDomesticHandlingPrice(0);
		shippingMethodVO.setExtraInsurancePrice(0);
		return shippingMethodVO;
	}

	/**
	 * This method fills the Domestic Basket VO.
	 *
	 * @param checkoutInputVO the checkout input vo
	 * @return the domestic basket details
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	private BBBDomesticBasketVO getDomesticBasketDetails(final BBBInternationalCheckoutInputVO checkoutInputVO) throws BBBSystemException, BBBBusinessException {

		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : getDomesticBasketDetails");

		double totalProductExtraHandling = 0.0;
		double totalProductExtraShipping = 0.0;
		double totalSalePrice = 0.0;
		double orderDiscount = 0.0;
		final BBBDomesticBasketVO basketVO = new BBBDomesticBasketVO();
		final List<BBBBasketItemsVO> basketItemsVOs = new ArrayList <BBBBasketItemsVO>();
		final List<BBBBasketItemsVO> surchargeBasketItemsVOs = new ArrayList <BBBBasketItemsVO>();
		List<String> intShipSurchargeValue=null;
		Boolean isIntShipSurchargeOn=false;
		long surchargeItemcount=0;
		final BBBBasketTotalVO bbbBasketTotalVO = new BBBBasketTotalVO();
		String scene7Path = null;
		String imageHost = null;
		String productId;
		String seoUrl;
		String skuId;
		String pdpSeoUrl = null;
		RepositoryItem productRepositoryItem;
		List<CommerceItemVO> commerceItemVOs = null;

		final Map<String,String> hostMap = this.getHostMap();
		String hostPath = null;
		String contextPath = null;

		BBBDisplayVO displayVO = null;
		BBBPricingVO pricingVO = null;

		if(!BBBUtility.isMapNullOrEmpty(hostMap)) {
			hostPath = hostMap.get(BBBInternationalShippingConstants.HOST_PATH);
			contextPath = hostMap.get(BBBInternationalShippingConstants.CONTEXT_PATH);
		}

		final Order order = checkoutInputVO.getOrder();

		commerceItemVOs = this.getCheckoutManager()
				.getCartItemVOList(order);

		logDebug("Inside class: InternationalCheckoutManager,  "
				+ "method : getDomesticBasketDetails : OrderId : " +order.getId());

		if(null != commerceItemVOs && !commerceItemVOs.isEmpty()) {

			final List<String> scene7KeysList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.SCENE7_URL);
			if(!BBBUtility.isListEmpty(scene7KeysList)) {
				scene7Path = scene7KeysList.get(0);
			}

			final List<String> imageHostList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.IMAGE_HOST);
			if(!BBBUtility.isListEmpty(imageHostList)) {
				imageHost = imageHostList.get(0);
			}

			try {
				for (final CommerceItemVO commerceItemVO : commerceItemVOs) {
					double productExtraShipping = 0.0;
					final double productExtraHandling = 0.0;
					long quantity;

					final BBBBasketItemsVO basketItemsVO = new BBBBasketItemsVO();
					displayVO = new BBBDisplayVO();
					pricingVO = new BBBPricingVO();
					final BBBCommerceItem bbbCommerceItem = commerceItemVO.getBBBCommerceItem();
					skuId = bbbCommerceItem.getCatalogRefId();
					productId = bbbCommerceItem.getAuxiliaryData().getProductId();
					if(BBBUtility.isNotEmpty(productId)) {
						productRepositoryItem=this.getCatalogRepository().getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
						if(null != productRepositoryItem) {
							seoUrl=	(String)productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME);
							pdpSeoUrl = hostPath + contextPath + seoUrl + BBBInternationalShippingConstants.SKU_ID_IN_URL + skuId;
						}
						displayVO.setProductUrl(pdpSeoUrl);
					}
					quantity =	bbbCommerceItem.getQuantity();
					double finalSurcharge=0.0;
					double val=0.0;
					//set values for surcharge

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
					if(isIntShipSurchargeOn)
					{
						SKUVO  skuDetailVO = this.getCatalogTools().getSKUDetails(order.getSiteId(), skuId, false, true, true);
						val=skuDetailVO.getShippingSurcharge();
						finalSurcharge=val*quantity;

						productExtraShipping=val;
					}
					//end
					basketItemsVO.setSkuId(skuId);
					basketItemsVO.setQuantity(quantity);

					this.populateDisplayVO(scene7Path, imageHost, commerceItemVO,
							displayVO);

					final double salePrice = this.populatePricingVO(productExtraShipping,
							productExtraHandling, quantity, pricingVO,
							bbbCommerceItem);

					totalProductExtraHandling = totalProductExtraHandling + productExtraHandling;
					totalProductExtraShipping = totalProductExtraShipping + finalSurcharge;
					totalSalePrice = totalSalePrice + Double.parseDouble(this.roundOff(salePrice
							* quantity));


					basketItemsVO.setBbbDisplayVO(displayVO);
					basketItemsVO.setBbbPricingVO(pricingVO);
					basketItemsVOs.add(basketItemsVO);
					if(val>0.0)
					{
						surchargeBasketItemsVOs.add(basketItemsVO);
						surchargeItemcount=surchargeItemcount+quantity;
					}
				}


				bbbBasketTotalVO.setTotalProductExtraHandling(Double.valueOf(this.roundOff(totalProductExtraHandling)));
				bbbBasketTotalVO.setTotalProductExtraShipping(Double.valueOf(this.roundOff(totalProductExtraShipping)));

				orderDiscount = Double.valueOf(this.roundOff(order.getPriceInfo().getDiscountAmount()));
				bbbBasketTotalVO.setOrderDiscount(orderDiscount);
				bbbBasketTotalVO.setTotalSalePrice(Double.valueOf(this.roundOff(totalSalePrice)));
				bbbBasketTotalVO.setTotalPrice(Double.valueOf(this.roundOff(totalSalePrice - orderDiscount + totalProductExtraHandling + totalProductExtraShipping)));

				basketVO.setBbbBasketTotalVO(bbbBasketTotalVO);
				basketVO.setBbbBasketItemsVO(basketItemsVOs);

				this.getBasketCustomData(checkoutInputVO, basketVO);

			} catch(RepositoryException e){
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException"), e);
				throw new BBBSystemException(BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION, BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
			}
		}

		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : getDomesticBasketDetails");
		return basketVO;
	}

	/**
	 * This method sets the customData attribute with International based Channel and TaxExemptID
	 * @param checkoutInputVO
	 * @param basketVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	private void getBasketCustomData(
			final BBBInternationalCheckoutInputVO checkoutInputVO,
			final BBBDomesticBasketVO basketVO) throws BBBSystemException, BBBBusinessException {
		StringBuilder customData = new StringBuilder();
		customData.append(BBBInternationalShippingConstants.CHANNEL_CUSTOM_DATA);
		if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(checkoutInputVO.getChannel())) {
			customData.append(BBBInternationalShippingConstants.CHANNEL_MOBILE_BFREE);
		} else if (BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(checkoutInputVO.getChannel())){
			customData.append(BBBInternationalShippingConstants.CHANNEL_MOBILE_APP_BFREE);
		}else{
			customData.append(BBBInternationalShippingConstants.CHANNEL_DESKTOP_BFREE);
		}
		customData.append(",");
		customData.append(BBBInternationalShippingConstants.TAX_EXEMPTID_CUSTOM_DATA);
		customData.append(getTaxExemptID());
		
		//Added code for Datacenter
		customData.append(",");
		String createdbyorder = this.getDcPrefix() + "-" + System.getProperty(BBBInternationalShippingConstants.JVM_PROPERTY);
		customData.append(BBBInternationalShippingConstants.DATACENTER+":");
		customData.append(createdbyorder);
		basketVO.setCustomData(customData.toString());
	}

	/**
	 * This method is used to populate the Pricing VO.
	 *
	 * @param productExtraShipping the product extra shipping
	 * @param productExtraHandling the product extra handling
	 * @param quantity the quantity
	 * @param pricingVO the pricing vo
	 * @param bbbCommerceItem the bbb commerce item
	 * @return the double
	 */
	private double populatePricingVO(final double productExtraShipping,
			final double productExtraHandling, final long quantity,
			final BBBPricingVO pricingVO, final BBBCommerceItem bbbCommerceItem) {

		double discountAmount;
		discountAmount = bbbCommerceItem.getPriceInfo().getRawTotalPrice()
				- bbbCommerceItem.getPriceInfo().getAmount();

		if (discountAmount > 0 && quantity > 1) {
			discountAmount = discountAmount
					/ quantity;
		}
		final double salePrice = Double.valueOf(this.roundOff(bbbCommerceItem.getPriceInfo().getListPrice() - discountAmount));

		pricingVO.setListPrice(Double.valueOf(this.roundOff(bbbCommerceItem.getPriceInfo().getListPrice())));
		pricingVO.setSalePrice(salePrice);
		pricingVO.setProductExtraShipping(Double.valueOf(this.roundOff(productExtraShipping)));
		pricingVO.setProductExtraHandling(Double.valueOf(this.roundOff(productExtraHandling)));
		pricingVO.setItemDiscount(Double.valueOf(this.roundOff(discountAmount)));
		return salePrice;
	}

	/**
	 * This method is used to populate the Display VO.
	 *
	 * @param scene7Path the scene7 path
	 * @param imageHost the image host
	 * @param commerceItemVO the commerce item vo
	 * @param displayVO the display vo
	 */
	private void populateDisplayVO(final String scene7Path, final String imageHost,
			final CommerceItemVO commerceItemVO, final BBBDisplayVO displayVO) {
		String basicImage; 
		String name=commerceItemVO.getSkuDetailVO().getDisplayName();
		String desc=commerceItemVO.getSkuDetailVO().getDescription();
		if(name!=null && name.contains(BBBInternationalShippingConstants.TRADEMARK))
		{
			name=name.replaceAll(BBBInternationalShippingConstants.TRADEMARK,BBBInternationalShippingConstants.TRADEMARKCODE);
		}
		if(desc!=null && desc.contains(BBBInternationalShippingConstants.TRADEMARK))
		{
			desc=desc.replaceAll(BBBInternationalShippingConstants.TRADEMARK,BBBInternationalShippingConstants.TRADEMARKCODE);
		}
		displayVO.setName(HtmlUtils.htmlEscapeDecimal(HtmlUtils.htmlUnescape(name)));
		displayVO.setColor(commerceItemVO.getSkuDetailVO().getColor());
		displayVO.setDescription(HtmlUtils.htmlEscapeDecimal(HtmlUtils.htmlUnescape(desc)));
		displayVO.setSize(commerceItemVO.getSkuDetailVO().getSize());
		basicImage = commerceItemVO.getSkuDetailVO().getSkuImages().getBasicImage();

		if(BBBUtility.isNotEmpty(basicImage)) {
			displayVO.setImageUrl(BBBCoreConstants.HTTPS + BBBCoreConstants.COLON 
					+ scene7Path + BBBCoreConstants.SLASH + basicImage 
					+ BBBInternationalShippingConstants.IMAGE_SIZE);
		} else {
			displayVO.setImageUrl(BBBCoreConstants.HTTPS + BBBCoreConstants.COLON + imageHost + BBBInternationalShippingConstants.NO_IMG_PATH);
		}
	}

	/**
	 * This method returns the Currency Quote Id.
	 *
	 * @param currencyCode the currency code
	 * @return the currency quote id
	 * @throws BBBSystemException the bBB system exception
	 */
	private long getCurrencyQuoteId(final String currencyCode) throws BBBSystemException {

		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : getCurrencyQuoteId");

		RepositoryItem exchangeRatesRepoItem;
		long currencyQuoteId = 0;
		try {
			exchangeRatesRepoItem = this.getInternationalRepository().getItem(currencyCode,
					BBBInternationalShippingConstants.EXCHANGE_RATE_ITEMDESCRIPTOR);
			if (null != exchangeRatesRepoItem) {
				currencyQuoteId = (Long) exchangeRatesRepoItem.getPropertyValue(this.getInternationalPropertyManager().getQuoteIdPropertyName());
			}
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(null, "RepositoryException"), e);
			throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1007, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1007);
		}

		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : getCurrencyQuoteId : currencyQuoteId : " + currencyQuoteId);

		return currencyQuoteId;
	}

	/**
	 * This method returns the LCP Rule Id which corresponds to Promotion property of country List Item Descriptor.
	 *
	 * @param countryCode the country code
	 * @param merchantId the merchant id
	 * @return the lcp rule id
	 * @throws BBBSystemException the bBB system exception
	 */
	private String getLcpRuleId(final String countryCode, final String merchantId) throws BBBSystemException {

		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : getLcpRuleId");

		RepositoryItem countryRepoItem;
		String promotionId = null;
		try {
			countryRepoItem = getInternationalRepository().getItem(
					countryCode + BBBCoreConstants.COLON
					+ merchantId,
					BBBInternationalShippingConstants.COUNTRY_LIST_ITEMDESCRIPTOR);
			if(null != countryRepoItem) {
				promotionId = (String) countryRepoItem.getPropertyValue(this.getInternationalPropertyManager().getPromotionIdPropertyName());
			}
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(null, "RepositoryException"), e);
			throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1003, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1003);
		}
		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : getLcpRuleId : promotionId : " +promotionId);
		return promotionId;
	}

	/**
	 * This method prepares host path.
	 *
	 * @return the host map
	 * @throws BBBBusinessException the bBB business exception
	 * @throws BBBSystemException the bBB system exception
	 */
	private Map<String,String> getHostMap() throws BBBBusinessException, BBBSystemException {

		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : getHostMap");

		String hostpath = null;
		String hostStr = null;
		final Map<String,String> hostMap = new HashMap<String,String>();

		final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();

		if (null != pRequest) {
			final String url = pRequest.getRequestURL().toString();
			final String contextPath = pRequest.getContextPath();
			final String serverPort = pRequest.getServerPort() + BBBCoreConstants.BLANK;
			String serverName = pRequest.getServerName();
			final String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);

			if (BBBUtility.isNotEmpty(channel)
					&& (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel
							.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
				try {
					final List<String> configValue = getCatalogTools()
							.getAllValuesForKey(
									BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
									BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);

					if (!BBBUtility.isListEmpty(configValue)) {
						hostpath = BBBCoreConstants.HTTPS
								+ BBBCoreConstants.CONSTANT_SLASH
								+ configValue.get(0);
						serverName = configValue.get(0);
					}

					hostMap.put("hostPath", hostpath);
					hostMap.put("serverName", serverName);
					hostMap.put("contextPath", "/m");

				} catch (BBBSystemException e) {
					logError("InternationalCheckoutManager.getHost :: System Exception occured while fetching config value for config key "
							+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE
							+ "config type "
							+ BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
				} catch (BBBBusinessException e) {
					logError("InternationalCheckoutManager.getHost :: Business Exception occured while fetching config value for config key "
							+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE
							+ "config type "
							+ BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
				}
			} else {
				hostStr = url.split(contextPath)[0];

				logDebug("url: " + url + "contextPath:" + contextPath
						+ "hostpath after context path split: " + hostStr
						+ "serverPort: " + serverPort + "serverName: "
						+ serverName);

				hostMap.put("hostPath", hostStr);
				hostMap.put("serverName", serverName);
				hostMap.put("contextPath", contextPath);

			}
		} else {
			logWarning("Request object is null from ServletUtil.getCurrentRequest()");
		}

		logDebug("hostMap: " + hostMap);

		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : getHostMap : hostMap: [" + hostMap + "]");

		return hostMap;
	}

	/**
	 * Round off the price to 2 decimal places.
	 *
	 * @param pPrice the price
	 * @return the string
	 */
	private String roundOff(final double pPrice) {
		String price = null;
		double calPrice;
		if (2 >= 0) {
			calPrice = Math.round(pPrice * Math.pow(10, 2))
					/ Math.pow(10, 2);
			final DecimalFormat df = new DecimalFormat("#.00");
			price = df.format(calPrice).toString();
		}
		return price;
	}


	/**
	 * This method is used to create the DS Order Id.
	 *
	 * @param pRequest the request
	 * @param bbbOrder the bbb order
	 * @param siteId the site id
	 * @return the string
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	public String createDSOrderId(final DynamoHttpServletRequest pRequest,final BBBOrder bbbOrder, final String siteId) 
			throws BBBSystemException, BBBBusinessException {

		final String orderId = bbbOrder.getId();
		String dsOrderId = null;
		final String orderType = bbbOrder.getOnlineBopusItemsStatusInOrder();
		try {
			if (!StringUtils.isBlank(orderId)) {
				logInfo("Generate New Order Numbers for DS for International Shipping");

				final Map<String, String> configMap = this.getCatalogTools()
						.getConfigValueByconfigType(getCartAndCheckOutConfigType());
				final Random randomNo = new Random();

				// Generate a random number

				final int randomNumber = randomNo.nextInt(MAXRANDOMNUMBEREXCLUSIVE);
				logDebug("Next int value for Random OrderId generation is : "
							+ randomNumber);
			
				for (int i = 0; i <= randomNumber; i++) {

					dsOrderId = getIdGenerator().generateStringId(
							BBBInternationalShippingConstants.ORDER_DS_SEED_NAME);

				}
				if (BBBUtility.isEmpty(dsOrderId)) {
					logError("DS OrderId is generated Blank");
				}
				if (orderType.equalsIgnoreCase(BBBCheckoutConstants.ONLINE_ONLY)
						|| orderType.equalsIgnoreCase(BBBCheckoutConstants.PAYPAL)) {
					String onlineOrderPrefix = "XXX";
					if (!StringUtils.isBlank(siteId) && configMap != null
							&& !configMap.isEmpty()) {
						onlineOrderPrefix = configMap.get(getOnlineOrderPrefixKey()
								.concat("_").concat(siteId));
					}
					dsOrderId = onlineOrderPrefix.concat(dsOrderId);

				}
				logInfo("New ONLINE Order Id generated For DS in internatinal shipping : "
						+ dsOrderId + " for OrderID : " + orderId);
			}
		} catch (IdGeneratorException e) {
			logError("Inside Exception: Exception occured while creating Ids for DS Order ID" + e);
			throw new BBBBusinessException("Inside Exception: Exception occured while creating Ids for DS Order ID", e);
		}

		return dsOrderId;
	}

	/**
	 * Creates the cart cookie and store all cart information in cookie.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @param order
	 *            the order
	 */
	public void createCartCookies(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final BBBOrder order) {
		final Profile profile = (Profile) pRequest.resolveName(getUserProfile());
		if (!profile.isTransient()) {
			final SiteContext siteContext = (SiteContext) pRequest.resolveName(getSiteContextPath());

			// creates json object from the order
			if (null != order && null != siteContext && null != siteContext.getSite()) {
				final JSONObject parentJsonObject = createJSONObject(order,siteContext);

				// add cookie
				final Cookie cookie = new Cookie(getOrderCookieName(),parentJsonObject.toString());
				cookie.setMaxAge(getOrderCookieAge());
				cookie.setPath(getOrderCookiePath());
				BBBUtility.addCookie(pResponse, cookie, true);
				// pResponse.addCookie(cookie);
				logDebug("Cookie added for order-" + cookie);
			
			} else {
				logDebug("Cookie not added for order:" + order
							+ " siteContext:" + siteContext);
			}
		}
	}

	/**
	 * Creates the json object.
	 * 
	 * @param order
	 *            the order
	 * @param siteContext
	 *            the site context
	 * @return the jSON object
	 */
	private JSONObject createJSONObject(final BBBOrder order, final SiteContext siteContext) {

		final JSONObject jsonRootObject = new JSONObject();
		jsonRootObject.element(BBBInternationalShippingConstants.ORDER_ID,
				getBBBDesEncryptionTools().encrypt(order.getId()));
		jsonRootObject.element(BBBInternationalShippingConstants.SITE_ID, siteContext.getSite().getId());

		final List jsonCIList = new ArrayList();
		JSONObject jsonCIObject;
		for (final Iterator<CommerceItem> iterator = order.getCommerceItems()
				.iterator(); iterator.hasNext();) {

			final CommerceItem citem = iterator.next();
			if (citem instanceof BBBCommerceItem) {
				// s is skuId, p is prodId, q is qty, r is registryId, st is
				// storeId, b is bts
				jsonCIObject = new JSONObject();
				jsonCIObject.put("s", citem.getCatalogRefId());
				jsonCIObject.put("p", ((BBBCommerceItem) citem)
						.getAuxiliaryData().getProductId());
				jsonCIObject.put("q", citem.getQuantity());
				jsonCIObject.put("b", ((BBBCommerceItem) citem).getBts());
				jsonCIObject.put("st", ((BBBCommerceItem) citem).getStoreId());
				jsonCIObject.put("r", ((BBBCommerceItem) citem).getRegistryId());
				/*jsonCIObject
				.put("r", ((BBBCommerceItem) citem).get,
				());*/
				jsonCIObject.put("prc",
						((BBBCommerceItem) citem).getPrevPrice());
				jsonCIObject.put("oos",
						((BBBCommerceItem) citem).isMsgShownOOS());
				jsonCIList.add(jsonCIObject);
				jsonCIObject.put("seqNo", ((BBBCommerceItem) citem).getSeqNumber());
			}

		}
		logDebug("Json object created successfully");
		jsonRootObject.element(BBBInternationalShippingConstants.ITEM_LIST, jsonCIList);
		return jsonRootObject;
	}

	/**
	 * this method store ATG order as a xml.
	 *
	 * @param orderId the order id
	 * @param e4XOrderId the e4 x order id
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	public void persistOrderXml(final String orderId, final String e4XOrderId,String merchantOrderId,final String countrycode ,final String currencyCode) throws BBBSystemException, BBBBusinessException  {
		String orderXML;
		try {
			orderXML = getOrderServices().getOrderAsXML(orderId).replaceAll("<order:creditCard.encryptedCreditCardNumber xsi:nil=\"true\"/>", "");
		} catch (CommerceException e1) {
			throw new BBBBusinessException(BBBCatalogErrorCodes.COMMERCE_EXCEPTION,BBBCatalogErrorCodes.COMMERCE_EXCEPTION, e1);
		} catch (GetException e1) {
			throw new BBBBusinessException("errors while generating XML Instance documents from Repository Items", e1);
		}
		vlogDebug("orderXML..............\n" +orderXML );
		getIntlRepository().addOrderXml(orderId, e4XOrderId, orderXML,merchantOrderId, countrycode ,currencyCode);
	}

	/**
	 * After submission order clearCartCookies will remove cart cookies
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 */
	public void clearCartCookies(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		final Cookie emptyCookie = new Cookie(getOrderCookieName(), "");
		emptyCookie.setMaxAge(0);
		emptyCookie.setPath(getOrderCookiePath());
		BBBUtility.addCookie(pResponse, emptyCookie, true);
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

	public void removeGiftwrap(Order order) throws BBBBusinessException {
		final List commerceItems = order.getCommerceItems();
		boolean shouldRollback=false;
		String giftWrapItemId=null;
		List<String> giftWrapItemIdList = new ArrayList<String>();

		final TransactionDemarcation td = new TransactionDemarcation();
		try {
			td.begin(getOrderManager().getOrderTools().getTransactionManager());
			if(commerceItems != null) {
				for (final Iterator iterator = commerceItems.iterator(); iterator.hasNext();) {
					final CommerceItem item = (CommerceItem) iterator.next();
					if (item != null) {
						if (item instanceof GiftWrapCommerceItem){
							giftWrapItemId=item.getId();
							giftWrapItemIdList.add(giftWrapItemId);
						}
					}
				}
			}
			if(!BBBUtility.isListEmpty(giftWrapItemIdList)) {
				synchronized (order) {
					for (String itemId : giftWrapItemIdList) {
						getOrderManager().getCommerceItemManager().removeItemFromOrder(order, itemId);
					}
					getOrderManager().updateOrder(order);
				}
			}
		} catch (TransactionDemarcationException e) {
			throw new BBBBusinessException("Transaction failure while removing Gift wrap item in Order","Error occurred while committing transection", e);
		} catch (CommerceException e) {
			throw new BBBBusinessException(BBBCatalogErrorCodes.COMMERCE_EXCEPTION,BBBCatalogErrorCodes.COMMERCE_EXCEPTION, e);

		}finally {
			try {
				td.end(shouldRollback);
			} catch (final TransactionDemarcationException tde) {
				this.logError("Transaction roll back error", tde);
			}
		}

	}

	public void removeTaxInfo(Order order) throws BBBBusinessException, RepositoryException {
		if(order.getShippingGroups()!=null && order.getShippingGroups().get(0) instanceof HardgoodShippingGroup ) {
			HardgoodShippingGroup hd =(HardgoodShippingGroup)order.getShippingGroups().get(0);
			hd.getId();
			boolean shouldRollback=false;
			final TransactionDemarcation td = new TransactionDemarcation();
			try {
				td.begin(getOrderManager().getOrderTools().getTransactionManager());

				synchronized (order) {
					TaxPriceInfo paramTaxPriceInfo = null;
					order.setTaxPriceInfo(paramTaxPriceInfo);
					final BBBRepositoryContactInfo atgShippingAddress = (BBBRepositoryContactInfo) hd.getShippingAddress();
					hd.setShippingAddress(atgShippingAddress);
					BBBRepositoryContactInfo address = new BBBRepositoryContactInfo();
					((BBBOrder) order).setBillingAddress(address);
					getOrderManager().updateOrder(order);
				}
			} catch (TransactionDemarcationException e) {
				throw new BBBBusinessException("Transaction failure while removing Tax Price Info from Order :: InternationalCheckoutManager:removeTaxInfo","Error occurred while committing transection", e);
			} catch (CommerceException e) {
				throw new BBBBusinessException(BBBCatalogErrorCodes.COMMERCE_EXCEPTION,BBBCatalogErrorCodes.COMMERCE_EXCEPTION, e);

			}finally {
				try {
					td.end(shouldRollback);
				} catch (final TransactionDemarcationException tde) {
					this.logError("Transaction roll back error", tde);
				}
			}
		}

	}
	/**
	 * Method to Persist the Order from OrderXML
	 * Update the International Order Properties.
	 * @param orderId
	 * @param merchantOrderId
	 * @param fraudState
	 * @param countrycode2
	 * @param currencyCode2
	 * @param internationalOrderId
	 * @param createOrderOOB
	 * @return
	 * @throws BBBSystemException
	 * @throws CommerceException
	 */
	public BBBOrder orderPersist(final String  orderId, final String merchantOrderId, final String fraudState, final String countrycode2, 
			final String currencyCode2,final String internationalOrderId, final boolean createOrderOOB)
					throws BBBSystemException
					{
		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : orderPersist");
		final TransactionDemarcation td1 = new TransactionDemarcation();
		boolean rollback=false;
		BBBOrder order = null;
		try
		{
			td1.begin(getTransactionManager());	
			String xmlorder;
			try {
				xmlorder = this.getInternationalOrderXmlRepoTools().getOrderXml(orderId);

				/*final 	MutableRepositoryItem  rItem=getProfileAdapterRepository().createItem(BBBInternationalShippingConstants.USER);

				final String profileId= rItem.getRepositoryId();*/
				if (!BBBUtility.isEmpty(xmlorder)) {
					String orderid = BBBCoreConstants.BLANK;
					if(!createOrderOOB){
						orderid = this.createOrderFromXmlForUnProcessedOrder(xmlorder);
						/*order = (BBBOrder) this.getOrderManager()
								.loadOrder(orderid);*/
					}
					order = (BBBOrder) this.getOrderManager().loadOrder(orderId);

					synchronized (order) {
						if (order != null)
						{
							if(order.isTransient()){
								getOrderManager().addOrder(order);
							}
							final BBBOrderTools tool = (BBBOrderTools) this
									.getOrderManager().getOrderTools();
							if (fraudState
									.equals(BBBInternationalShippingConstants.SUCCESS_FRAUD_STATE) || fraudState
									.equals(BBBInternationalShippingConstants.GREEN_PO_FILE)) {

								tool.addInternationalOrder(
										order,
										internationalOrderId,
										BBBInternationalShippingConstants.INTL_SUBMITTED,
										currencyCode2, countrycode2);
							} else if (fraudState
									.equals(BBBInternationalShippingConstants.PENDING_FRAUD_STATE)) {

								tool.addInternationalOrder(
										order,
										internationalOrderId,
										BBBInternationalShippingConstants.INTL_HOLD,
										currencyCode2, countrycode2);
							} else if (fraudState
									.equals(BBBInternationalShippingConstants.RED_PO_FILE)) {
								tool.addInternationalOrder(
										order,
										internationalOrderId,
										BBBInternationalShippingConstants.INTL_CANCELLED,
										currencyCode2, countrycode2);
								// for failed order update the status in
								// BBBOrder also
								this.getOrderManager()
								.updateOrderSubstatus(
										order,
										BBBInternationalShippingConstants.INTL_CANCELLED);
							}
							if (fraudState
									.equals(BBBInternationalShippingConstants.SUCCESS_FRAUD_STATE)
									|| fraudState.equals(BBBInternationalShippingConstants.PENDING_FRAUD_STATE) 
									|| fraudState.equals(BBBInternationalShippingConstants.FAILED_FRAUD_STATE) 
									|| fraudState.equals(BBBInternationalShippingConstants.GREEN_PO_FILE)) {
								this.getOrderManager()
								.updateOrderSubstatus(
										order,
										BBBInternationalShippingConstants.INTL_INCOMPLETE);
								order.setOnlineOrderNumber(merchantOrderId);
								/*DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
								if (request!= null) {
									request.setParameter("orderForRKG", order);
								}*/
								
							}
						}
					}
				}
				else
				{
					logError(LogMessageFormatter.formatMessage(null,
							"No record found for Order id  : " +orderId));
				}
			}
			catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null,
						"RepositoryException from service of InternationalOrderPersist : orderPersist"), e);
				throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1016, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1016);
			} catch (CommerceException e) {
				logError(LogMessageFormatter.formatMessage(null,
						"CommerceException from service of InternationalOrderPersist : orderPersist"), e);
				throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1017, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1017);
			}

		} catch (TransactionDemarcationException e) {
			rollback = true;
			logError(e);
		} finally {
			this.commitTransaction(rollback, order);
		}
		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : orderPersist");
		return order;
					}
	protected void commitTransaction( boolean isRollback, Order order) {

		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : commitTransaction");

		boolean exception = false;
		Transaction pTransaction = null;

		try {
			pTransaction = getTransactionManager().getTransaction();

			if(pTransaction != null){

				TransactionManager tm = getTransactionManager();
				if (isRollback || this.isTransactionMarkedAsRollBack())  {
					this.logInfo("Transaction is getting rollback from orderPersist() in InternationaShippingCheckoutHelper");
					if (tm != null){
						tm.rollback();
					}else{
						pTransaction.rollback(); 
					}
					if (order!=null && order instanceof OrderImpl){
						this.logInfo("Invalidating order because of transaction is getting rollback from orderPersist() in InternationaShippingCheckoutHelper");
						((OrderImpl)order).invalidateOrder();
					}
				}
				else {
					if (tm != null){
						this.logInfo("Commiting transaction in orderPersist() in InternationaShippingCheckoutHelper");
						tm.commit();
					}else{
						this.logInfo("Commiting transaction in orderPersist() in InternationaShippingCheckoutHelper");
						pTransaction.commit();
					}
				}
			}
		}
		catch (RollbackException exc) {
			exception = true;
			if (isLoggingError())
				logError(exc);
		}
		catch (HeuristicMixedException exc) {
			exception = true;
			if (isLoggingError())
				logError(exc);
		}
		catch (HeuristicRollbackException exc) {
			exception = true;
			if (isLoggingError())
				logError(exc);
		}
		catch (SystemException exc) {
			exception = true;
			if (isLoggingError())
				logError(exc);
		}
		finally {
			if (exception) {
				this.logInfo("Invalidating order due to exception in getorderDetailsVO() in OrderDetailsManager");
				if (order!=null && order instanceof OrderImpl)
					((OrderImpl)order).invalidateOrder();

			} // if
		} // finally
		// if
		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : commitTransaction");
	}
	protected boolean isTransactionMarkedAsRollBack() {
		try {
			TransactionManager tm = getTransactionManager();
			if (tm != null) {
				int transactionStatus = tm.getStatus();
				if (transactionStatus == javax.transaction.Status.STATUS_MARKED_ROLLBACK)
					return true;
			}
		}
		catch (SystemException exc) {
			if (isLoggingError())
				logError(exc);
		}
		return false;
	}

	/**
	 * This method is used to set the Order details
	 * with the OrderXML as input parameter.
	 * @param pOrderAsXML
	 * @return
	 * @throws BBBSystemException 
	 */
	private String createOrderFromXmlForUnProcessedOrder(String pOrderAsXML) throws BBBSystemException {

		logDebug((new StringBuilder()).append("Create a new order from the given XML document: ").append(pOrderAsXML).toString());
		String orderId = null;
		AddService adder = getOrderServices().getAddService();
		MutableRepositoryItem orderItem;
		try {
			orderItem = (MutableRepositoryItem)adder.addItem(pOrderAsXML, getOrderServices().isValidateOrderXMLOnCreate(), adder.isEmptyTagIsNull(), adder.isMissingTagIsNull(), adder.isPersistItem(), adder.isEmptyTagIsEmptyString());
			orderId = orderItem.getRepositoryId();
		} catch (AddException e) {
			logError(e);
			throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1017, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1017);
		}
		return orderId;

	}

	/**
	 * This method is used to Decrement the Inventory.
	 * @param pOrder
	 * @throws BBBSystemException
	 */
	private void decrementInventoryRepository(final Order pOrder) throws BBBSystemException {
		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : decrementInventoryRepository : Order  Id : " +pOrder.getId());
		final InventoryDecrementVO inventoryRequest = createInventoryRequest(pOrder);
		if(inventoryRequest.getListOfInventoryVos() != null && inventoryRequest.getListOfInventoryVos().size() > 0){
			final InventoryVO[] inventoryVOs = new InventoryVO[inventoryRequest.getListOfInventoryVos().size()];
			inventoryRequest.getListOfInventoryVos().toArray(inventoryVOs);
			if (getInventoryManager() != null) {
				getInventoryManager().decrementInventoryStock(inventoryVOs);
			}
		}
		String jmsInvDecEnabled = BBBCoreConstants.FALSE;
		List<String> config;
		try {
			config = this.getCatalogTools().getContentCatalogConfigration(BBBCoreConstants.JMS_INVENTORY_DEC);
			if (config.size() > 0) {
				jmsInvDecEnabled = config.get(0);
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while fetching flag JMSInventoryDec for generating inventory decrement message", e);
			}
		}		
		try {
			if(null != jmsInvDecEnabled && Boolean.parseBoolean(jmsInvDecEnabled)){
				inventoryRequest.setServiceName(BBBCoreConstants.INVENTORY_DECREMENT_SERVICE);
				ServiceHandlerUtil.sendTextMessage(inventoryRequest);
			}
		} catch (Exception e) {
			logError(LogMessageFormatter.formatMessage(null,
					"Exception from decrementInventoryRepository : decrementInventoryRepository"), e);
		}
		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : decrementInventoryRepository");
	}

	/**
	 * Creates the inventory request.
	 *
	 * @param pOrder the order
	 * @return the inventory decrement vo
	 */
	@SuppressWarnings("unchecked")
	private InventoryDecrementVO createInventoryRequest(final Order pOrder) {

		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : createInventoryRequest");
		final 	InventoryDecrementVO inventoryRequest = new InventoryDecrementVO();
		final List<InventoryVO> inventories = new ArrayList<InventoryVO>();
		final List<CommerceItem> items = (List<CommerceItem>)pOrder.getCommerceItems();
		String updateAllInventory = BBBCoreConstants.FALSE;
		List<String> config = null;
		try {
			config = this.getCatalogTools().getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG);
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1014 + ": Error while retrieving configure keys value for [" + BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG + "]", e);
			}
		}
		if(config != null && config.size() > 0) {
			updateAllInventory = config.get(0);
		}

		InventoryVO inventory = null;
		BBBCommerceItem bbbItem = null;
		for (final CommerceItem item : items) {
			if(item instanceof BBBCommerceItem) {
				bbbItem = (BBBCommerceItem) item;				
				/* Check if the commerce item is NON-BOPUS */
				if (StringUtils.isBlank(bbbItem.getStoreId())) {

					/*If this is non-vdc item, and the flag is turned off - indicating not to update inventory for non-vdc item, skip the inventory update*/
					if(BBBCoreConstants.FALSE.equalsIgnoreCase(updateAllInventory) && !bbbItem.isVdcInd()){
						continue;
					}

					inventory = new InventoryVO();
					inventory.setSkuID(item.getCatalogRefId());
					inventory.setSiteID(pOrder.getSiteId());
					inventory.setOrderedQuantity(item.getQuantity());
					inventory.setDeliveryType(BBBCoreConstants.SHIPPING_DT);
					inventories.add(inventory);
				}
			}
		}
		
		String uniqueId = UUID.randomUUID().toString();		
		inventoryRequest.setConsumer("TIBCO_SUB1");
		inventoryRequest.setProducer("ATG");
		inventoryRequest.setPayLoad("InventoryDecrement");
		inventoryRequest.setMessageFormat("XML");
		inventoryRequest.setMessagePriority(BigInteger.valueOf(new Long(1)));
		inventoryRequest.setMessageCreateTS(getXMLGregorianCalendar());
		inventoryRequest.setMessageId(uniqueId);
		inventoryRequest.setConsumer("TIBCO_SUB1");
		inventoryRequest.setCoRelationId(((BBBOrder)pOrder).getOnlineOrderNumber());
		inventoryRequest.setConversationId("InventoryDecrement|"+uniqueId);
		inventoryRequest.setListOfInventoryVos(inventories);
		inventoryRequest.setDataCenter(this.getDataCenterMap().get(this.getDcPrefix()));
		inventoryRequest.setOrderSubmissionDate(((BBBOrder) pOrder).getInternationalOrderDate());
		inventoryRequest.setSite(pOrder.getSiteId());
		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : createInventoryRequest");
		return inventoryRequest;
	}
	
	/**
	 * This method return XMLGregorian Calendar time format
	 * @return
	 */
	private XMLGregorianCalendar getXMLGregorianCalendar() {
		GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
		
		XMLGregorianCalendar xmlGregorianCalendar = null;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			logError("Error in fetch date in xmlGregorianCalender format",e);
		}
		return xmlGregorianCalendar;
	}
	
	/**
	 * This Method will Persist the Order.
	 * Remove the Order XML from BBB_INTL_ORDER table
	 * and will decrement the Inventory.
	 * @param orderId
	 * @param merchantOrderId
	 * @param fraudState
	 * @param countrycode
	 * @param currencyCode
	 * @param internationalOrderId
	 * @throws BBBSystemException
	 * @throws CommerceException
	 */
	public void updateInternationalOrder(final String orderId, final String merchantOrderId, final String fraudState,
			final String countrycode, final String currencyCode, final String internationalOrderId, final boolean createOrderOOB) throws BBBSystemException {
		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : updateInternationalOrder");
		BBBOrder order=null;
		if (!fraudState.equals(BBBInternationalShippingConstants.FAILED_FRAUD_STATE)) {
			order=this.orderPersist(orderId, merchantOrderId, fraudState, countrycode, currencyCode, internationalOrderId, createOrderOOB);
			/*if(!countrycode.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)){
				this.getInternationalOrderXmlRepoTools().removeOrderXml(orderId);
			}*/
		}
		if(order!=null && (!(fraudState.equalsIgnoreCase(BBBInternationalShippingConstants.FAILED_FRAUD_STATE) 
				|| fraudState.equalsIgnoreCase(BBBInternationalShippingConstants.RED_PO_FILE)))) {
			this.decrementInventoryRepository(order);
		}
		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : updateInternationalOrder");
	}

	/**
	 * This Method will Persist the Order.
	 * Remove the Order XML from BBB_INTL_ORDER table
	 * and will decrement the Inventory.
	 * @param orderId
	 * @param merchantOrderId
	 * @param fraudState
	 * @param countrycode
	 * @param currencyCode
	 * @param internationalOrderId
     * @param omnitureProductString 
	 * @param orderSubmitVO 
	 * @throws BBBSystemException
	 * @throws CommerceException
	 */
	public void updateInternationalOrder(final String orderId, final String merchantOrderId, final String fraudState,
			final String countrycode, final String currencyCode, final String internationalOrderId, final boolean createOrderOOB, BBBInternationalOrderSubmitVO orderSubmitVO) throws BBBSystemException {
		logDebug("Entering class: InternationalCheckoutManager,  "
				+ "method : updateInternationalOrder");
		BBBOrder order=null;
		if (!fraudState.equals(BBBInternationalShippingConstants.FAILED_FRAUD_STATE)) {
			order=this.orderPersist(orderId, merchantOrderId, fraudState, countrycode, currencyCode, internationalOrderId, createOrderOOB);
			//BBBSL-3216 new omniture tagging string for international shipping orders.
			buildOmnitureTaggingString(order, orderSubmitVO);
			if (!BBBUtility.isEmpty(countrycode) && !countrycode.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)) {
				populateThirdPartyVO(order, orderSubmitVO);
			}
			
			this.getInternationalOrderXmlRepoTools().removeOrderXml(orderId);
		}
		if(order!=null && (!(fraudState.equalsIgnoreCase(BBBInternationalShippingConstants.FAILED_FRAUD_STATE) 
				|| fraudState.equalsIgnoreCase(BBBInternationalShippingConstants.RED_PO_FILE)))) {
			this.decrementInventoryRepository(order);
		}
		logDebug("Exiting class: InternationalCheckoutManager,  "
				+ "method : updateInternationalOrder");
	}

	public void populateThirdPartyVO(final Object order, BBBInternationalOrderSubmitVO orderSubmitVO) {
		
		if (isLoggingDebug()) {
			logDebug("Entering class: InternationalCheckoutManager,  "
					+ "method : populateThirdPartyVO");
		}
		
		final StringBuilder productIds = new StringBuilder();
		final StringBuilder itemIds = new StringBuilder();
		final StringBuilder itemQtys = new StringBuilder();
		final StringBuilder itemprices = new StringBuilder();
		final StringBuilder itemAmounts = new StringBuilder();
		
		final StringBuilder itemNames = new StringBuilder();
		final StringBuilder registryIds = new StringBuilder();
		double grandTotal = BBBCoreConstants.DOUBLE_ZERO; 
		DecimalFormat df = new DecimalFormat("#.##");
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		
		String resxEventType = BBBCoreConstants.BLANK;
				
		if (order != null) {
 
			final String siteId = BBBUtility.getCurrentSiteId(pRequest);
			final String rkgMerchantId = getRkgMerchantId(siteId);
			String orderId = null;
			
			String onlineOrderNumber = ((BBBOrder) order).getOnlineOrderNumber();
			
			if(null != onlineOrderNumber){
				orderId = onlineOrderNumber;
			}
		
			final String genOrderCode = ((BBBOrder) order).getOnlineOrderNumber() != null ? ((BBBOrder) order).getOnlineOrderNumber() : ((BBBOrder) order).getBopusOrderNumber();
			final Date orderDate = ((BBBOrder) order).getSubmittedDate();

			final int listSize = ((BBBOrder) order).getCommerceItemCount();
			int merchandizibleCommerceItemCount = BBBCoreConstants.ZERO ;
			int count = BBBCoreConstants.ONE;
			if (isLoggingDebug()) {
			logDebug("CLS=[InternationalCheckoutManager]/MSG=[orderId= "+ orderId +" orderDate= "+ orderDate + " commerceItemsSize = " + listSize);
			}
			this.getRegistryManager().populateRegistryMapInOrder(pRequest, (BBBOrder) order);
			
			for (CommerceItem commerceItem : (List<CommerceItem>) ((BBBOrder) order).getCommerceItems()) {

				//If EcoFee or GiftWrap items then skip
				if( !(commerceItem instanceof NonMerchandiseCommerceItem)){
				
					final String itemName = (String) ((RepositoryItem) commerceItem
							.getAuxiliaryData().getCatalogRef())
							.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
					itemNames.append(itemName);
					itemIds.append(commerceItem.getCatalogRefId());
					productIds.append(commerceItem.getAuxiliaryData().getProductId());
					itemQtys.append(commerceItem.getQuantity());
					
					if (count < listSize) {
						itemIds.append(BBBCoreConstants.SEMICOLON);
						productIds.append(BBBCoreConstants.SEMICOLON);
						itemNames.append(BBBCoreConstants.DOUBLE_COLON_SYMBOL);
						itemQtys.append(BBBCoreConstants.SEMICOLON);
					}
					
					if (commerceItem.getPriceInfo() != null && commerceItem.getPriceInfo().getSalePrice() > 0.0) {
						itemAmounts.append(df.format(commerceItem.getPriceInfo().getSalePrice()*commerceItem.getQuantity()));
					} else if (commerceItem.getPriceInfo() != null){
						itemAmounts.append(df.format(commerceItem.getPriceInfo().getListPrice()*commerceItem.getQuantity()));
					}
					
					itemprices.append(df.format(commerceItem.getPriceInfo().getAmount()/commerceItem.getQuantity()));
					if (count < listSize) {
						itemprices.append(BBBCoreConstants.SEMICOLON);
						itemAmounts.append(BBBCoreConstants.SEMICOLON);
					}
					
					//registryIds
					String registryId = ((BBBCommerceItem)commerceItem).getRegistryId();

					if(!StringUtils.isEmpty(resxEventType)){
						resxEventType += BBBCoreConstants.SEMICOLON;
					}
					
					if(registryId !=null){
					
						Map<String, RegistrySummaryVO> registryMap = ((BBBOrder)order).getRegistryMap();
						RegistrySummaryVO regSummVO = registryMap.get(registryId);
						if (regSummVO != null && regSummVO.getRegistryType() != null) {
							String registryType = regSummVO.getRegistryType()
									.getRegistryTypeDesc();
							resxEventType += BBBCoreConstants.PURCHASE_CONFIRMATION + BBBCoreConstants.PLUS + registryType;
						}
						
					} else{
						resxEventType += BBBCoreConstants.PURCHASE_CONFIRMATION;
					}
					
					registryIds.append(registryId);	
					if (count < listSize) {
						registryIds.append(BBBCoreConstants.SEMICOLON);
					}
					
					merchandizibleCommerceItemCount++;
					grandTotal += commerceItem.getPriceInfo().getAmount();
				}
				count++;
			}
			
			if (isLoggingDebug()) {		
			logDebug("CLS=[InternationalCheckoutManager]/MSG=[itemIds="+itemIds +" skuQty="+itemQtys +" skuPrice ="+itemprices
					+" skuName="+itemNames +" skuAmounts="+itemAmounts);			
			}
			
			final String[] skuIds = itemIds.toString().split(BBBCoreConstants.SEMICOLON);
			final String[] skuQty = itemQtys.toString().split(BBBCoreConstants.SEMICOLON);
			final String[] skuPrice = itemprices.toString().split(BBBCoreConstants.SEMICOLON);
			final String[] skuName = itemNames.toString().split(BBBCoreConstants.DOUBLE_COLON_SYMBOL);
			final String[] regIds = registryIds.toString().split(BBBCoreConstants.SEMICOLON);
						
			final List<String> rkgItemUrlList = new ArrayList<String>();
			Map<String, Object> certonaMap = new HashMap<String, Object>();
			Map<String, String> cjParamMap = new HashMap<String, String>();
			StringBuilder cjItemAppendedUrl = new StringBuilder();;

			for (int index = 0; index < merchandizibleCommerceItemCount; index++) {
				String reg =null;
				String registryIdRKG = BBBCoreConstants.BLANK;
				if(regIds != null && index < regIds.length) {
					reg = regIds[index];
					if(regIds[index] != null){
					    registryIdRKG = regIds[index];
					}
				}
				if (isLoggingDebug()) {
					logDebug("skuIds " + index +" "+ skuIds[index] + " skuPrice " + index +" "+ skuPrice[index] + " skuQty " + index +" "
									+ skuQty[index] + " skuName " + index +" "+ skuName[index]);
				}
					
				final String cjItemUrl = createCJURL(index, skuIds[index], skuQty[index], skuPrice[index]);
				cjItemAppendedUrl.append(cjItemUrl);
				if (index != merchandizibleCommerceItemCount-1) {
					cjItemAppendedUrl.append(BBBCoreConstants.AMPERSAND);
				}
				
				final String rkgItemUrl = createRKGURL(rkgMerchantId, genOrderCode, index, registryIdRKG, skuIds[index], orderDate, 
						skuPrice[index], skuQty[index], skuName[index], pRequest.getLocale());
				rkgItemUrlList.add(rkgItemUrl);
				
			}
			
			orderSubmitVO.setRkgItemURLList(rkgItemUrlList);
			certonaMap = populateCertonaMap(resxEventType, productIds, itemQtys, itemAmounts, df.format(grandTotal), genOrderCode );
			orderSubmitVO.setCertonaMap(certonaMap);
			cjParamMap = populateCJMap(cjItemAppendedUrl.toString(), pRequest, genOrderCode);
			orderSubmitVO.setCjParamMap(cjParamMap);
			
		}
		if (isLoggingDebug()) {
			logDebug("Exiting class: InternationalCheckoutManager,  "
					+ "method : populateThirdPartyVO");
		}

	}
	
	
	private Map<String, String> populateCJMap(String cjItemAppendedUrl, DynamoHttpServletRequest pRequest, String genOrderCode) {
		
		if (isLoggingDebug()) {
			logDebug("Inside the method populateCJMap() method in InternationalCheckoutManager [START]");
		}
		
		HashMap<String, String> cjParamMap = new HashMap<String, String>();
		
		if (!BBBUtility.isEmpty(cjItemAppendedUrl)) {
			String cj_cid = null;
			String cj_type = null;
			String siteId = BBBUtility.getCurrentSiteId(pRequest);
			try {
			
				if (!BBBUtility.isEmpty(siteId) && siteId.equals(BBBCoreConstants.SITE_BBB)) {
					cj_cid = getCatalogTools().getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, CJ_CID_BABY).get(BBBCoreConstants.ZERO);
				} else if (!BBBUtility.isEmpty(siteId) && siteId.equals(BBBCoreConstants.SITE_BAB_US)) {
					cj_cid = getCatalogTools().getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, CJ_CID_US).get(BBBCoreConstants.ZERO);
				}
				cj_type = getCatalogTools().getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, CJ_TYPE_INTL).get(BBBCoreConstants.ZERO);
				String cjSaleString = getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, 
						BBBCoreConstants.CJ_REF_SALE_URL).get(BBBCoreConstants.ZERO);
				if (isLoggingDebug()) {
					logDebug("Obtained cj_cid :: " + cj_cid + "cj_type :: " + cj_type + "cjSaleString :: " + cjSaleString + " cjItemAppendedUrl :: " + cjItemAppendedUrl.toString());
				}
				if (cjSaleString != null) {
					cjParamMap.put(CJ_BASE_URL, cjSaleString);
				}
				if (cj_cid != null) {
					cjParamMap.put(CJ_CID, cj_cid);
				}
				if (cj_type != null) {
					cjParamMap.put(CJ_TYPE, cj_type);
				}
				cjParamMap.put(CJ_ITEM_APPENDED_URL, cjItemAppendedUrl.toString());
				if (genOrderCode != null) {
					cjParamMap.put(GEN_ORDER_CODE, genOrderCode);
				}		
				
			} catch (BBBSystemException e) {
				logError("Error in getting commission junction config key values ", e);
			} catch (BBBBusinessException e) {
				logError("Error in getting commission junction config key values ", e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("Inside the method populateCJMap() method in InternationalCheckoutManager [END]");
		}
		
		return cjParamMap;
		
	}

	private String createCJURL(int index, String skuId, String skuQty, String skuPrice) {
		
		if (isLoggingDebug()) {
			logDebug("Inside the method createCJURL() method in InternationalCheckoutManager [START] with values || index: " + index + "skuId: " + skuId
				 + "skuQty: " + skuQty + "skuPrice: " + skuPrice);
		}
		
		StringBuilder cjItemUrl = new StringBuilder();
		
		cjItemUrl.append(ITEM + (index+1));
		cjItemUrl.append(BBBCoreConstants.EQUAL);
		cjItemUrl.append(skuId);
		cjItemUrl.append(BBBCoreConstants.AMPERSAND);
		cjItemUrl.append(AMT + (index+1));
		cjItemUrl.append(BBBCoreConstants.EQUAL);
		cjItemUrl.append(skuPrice);
		cjItemUrl.append(BBBCoreConstants.AMPERSAND);
		cjItemUrl.append(QTY + (index+1));
		cjItemUrl.append(BBBCoreConstants.EQUAL);
		cjItemUrl.append(skuQty);
		
		if (isLoggingDebug()) {
			logDebug("Inside the method createCJURL() method in InternationalCheckoutManager :: Returned  CJ Item Url :: " + cjItemUrl.toString());
			logDebug("Inside the method createCJURL() method in InternationalCheckoutManager [END]");
		}
		return cjItemUrl.toString();
		
	}

	private Map<String, Object> populateCertonaMap(String resxEventType, StringBuilder productIds, StringBuilder itemQtys, 
			StringBuilder itemAmounts, String grandTotal, String genOrderCode) {
		
		if (isLoggingDebug()) {
		logDebug("Inside the method populateCertonaMap() method in InternationalCheckoutManager [START] with values || resxEventType: " + resxEventType + "productIds: " + productIds.toString()
				 + "itemQtys: " + itemQtys.toString() + "grandTotal: " + grandTotal + "genOrderCode: " + genOrderCode );
		}
		
		Map<String, Object> certonaMap = new HashMap<String, Object>();
		
		certonaMap.put(RESX_EVENT_TYPE, resxEventType);
		certonaMap.put(PRODUCT_IDS, productIds.toString());
		certonaMap.put(ITEM_QTYS, itemQtys.toString());
		certonaMap.put(ITEM_AMOUNTS, itemAmounts.toString());
		certonaMap.put(GRAND_TOTAL, grandTotal);
		if (genOrderCode != null) {
			certonaMap.put(GEN_ORDER_CODE, genOrderCode);
		}
		if (isLoggingDebug()) {
			logDebug("Inside the method populateCertonaMap() method in InternationalCheckoutManager [END]");
		}
		return certonaMap;
		
	}

	/**
	 * This method return Site Specific RKG Merchant Id
	 * 
	 * @param siteId
	 *            - Site Id
	 * @return rkgMerchantId
	 */
	private String getRkgMerchantId(final String siteId) {

		String rkgMerchantId = "";
		List<String> rkgMerchantIds = null;
		try {
			rkgMerchantIds = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteId);
			if (isLoggingDebug()) {
			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[rkgMerchantIds=" + rkgMerchantIds);
			}
			
		} catch (BBBSystemException e) {
			logError("Error in getting merchant Id ", e);
		} catch (BBBBusinessException e) {
			logError("Error in getting merchant Id ", e);
		}

		if (!BBBUtility.isListEmpty(rkgMerchantIds)) {
			rkgMerchantId = rkgMerchantIds.get(BBBCoreConstants.ZERO);
		}
		if (rkgMerchantId == null) {
			rkgMerchantId = "";
		}
		return rkgMerchantId;

	}
	
	/**
	 * Create RKG formated URL
	 * 
	 * @param merchantId
	 *            - Site Specific Merchant Id (Required)
	 * @param orderId
	 *            - Order Id (Required)
	 * @param lineNumber
	 *            - line number of item on the Invoice (Required)
	 * @param customInfo
	 *            - Custom Information (Optional)
	 * @param skuId
	 *            - SkuId of Product (Required)
	 * @param timeStamp
	 *            - order placement time in YYYYMMDDHHMMSS format (Optional)
	 * @param price
	 *            - price of the SKU in cents.(Required)
	 * @param quantity
	 *            - SKU quantity (Required)
	 * @param skuName
	 *            - URI encoded SKU name (Required)
	 * @return URL
	 */
	private String createRKGURL(final String merchantId, final String orderId, final int lineNumber, final String customInfo, final String skuId,
			final Date timeStamp, final String price, final String quantity, final String skuName, final Locale pLocale) {

		if (isLoggingDebug()) {
			
			logDebug("Inside the method createRKGURL() method in InternationalCheckoutManager [START] with values || merchantId: " + merchantId + "orderId: " + orderId
				 + "lineNumber: " + lineNumber + "customInfo: " + customInfo + "skuId: " + skuId + "timeStamp: " + timeStamp + "price: " + price + "quantity: " + quantity
				+ "skuName: " + skuName);
		}
		
		final StringBuilder url = new StringBuilder();
		url.append(BBBCoreConstants.RKG_URL_PARAM_MID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(merchantId);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(BBBCoreConstants.RKG_URL_PARAM_OID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(orderId);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(BBBCoreConstants.RKG_URL_PARAM_LID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(lineNumber + 1);

		url.append(BBBCoreConstants.AMPERSAND);

		url.append(BBBCoreConstants.RKG_URL_PARAM_CID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(customInfo);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(BBBCoreConstants.RKG_URL_PARAM_IID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(skuId);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(BBBCoreConstants.RKG_URL_PARAM_TS);
		url.append(BBBCoreConstants.EQUAL);
		url.append(BBBUtility.getRkgFormattedDate(timeStamp, pLocale));
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(BBBCoreConstants.RKG_URL_PARAM_ICENT);
		url.append(BBBCoreConstants.EQUAL);
		url.append(BBBUtility.getRkgFormattedPrice(price));
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(BBBCoreConstants.RKG_URL_PARAM_IQTY);
		url.append(BBBCoreConstants.EQUAL);
		url.append(quantity);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(BBBCoreConstants.RKG_URL_PARAM_INAME);
		url.append(BBBCoreConstants.EQUAL);
		url.append(BBBUtility.getEncodedString(skuName));
		
		if (isLoggingDebug()) {
			logDebug("Inside the method createRKGURL() method in InternationalCheckoutManager :: Returned Url " + url.toString());
			logDebug("Inside the method createRKGURL() method in InternationalCheckoutManager [END]");
		}
		
		return url.toString();
	}

	/**
	 * BBBSL-3216 enhancement. This method generates omniture tagging products
	 * event string for the internationally shipping orders for new omniture 
	 * tagging.
	 * 
	 * @param order
	 * @param omnitureProductString
	 */
	private void buildOmnitureTaggingString(final Object order, final BBBInternationalOrderSubmitVO orderSubmitVO) {
		
		logDebug("Inside the method buildOmnitureTaggingString() method in InternationalCheckoutManager [START]");
		String productId = null;	
		String skuId = null;
		long quantity = 0;
		double price = 0.00;
		boolean firstItem = true;
		StringBuilder omnitureProductString = new StringBuilder();
		if(null != order){
			for(CommerceItem commerceItem : (List<CommerceItem>)((BBBOrder)order).getCommerceItems()){

				if(!(commerceItem instanceof BBBCommerceItem)) {
					continue;
				}
				productId = commerceItem.getAuxiliaryData().getProductId();
				skuId = commerceItem.getCatalogRefId();
				quantity = commerceItem.getQuantity();
				double salePrice = commerceItem.getPriceInfo().getSalePrice();
				double listPrice = commerceItem.getPriceInfo().getListPrice();
				if(salePrice>0){
					price= salePrice;
				}else{
					price=listPrice;
				}
				double itemTotal = 0;
				String formattedItemTotal=BBBCoreConstants.BLANK;
				itemTotal += BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity)).doubleValue();						
				try {
					Properties prop=new Properties();
					formattedItemTotal=	TagConverterManager
						.getTagConverterByName(BBBCoreConstants.UNFORMATTED_CURRENCY)
						.convertObjectToString(
								ServletUtil.getCurrentRequest(), Double.valueOf(itemTotal),
								prop).toString();
					
					} catch (TagConversionException e) {
						logDebug("TagConversionException while converting Currency", e);
					}
				
				if(!firstItem){
					omnitureProductString.append(COMMA);
				}
				omnitureProductString.append(SEMICOLON);
				omnitureProductString.append(productId);
				omnitureProductString.append(SEMICOLON);
				omnitureProductString.append(quantity);
				omnitureProductString.append(SEMICOLON);
				omnitureProductString.append(formattedItemTotal);
				omnitureProductString.append(SEMICOLON);
				omnitureProductString.append(SEMICOLON);

				omnitureProductString.append(E_VAR30);
				omnitureProductString.append(skuId);

				firstItem = false;
			}
			logDebug("Final Omniture tagging string formed for s.products : [" + omnitureProductString + "]");
		}
		orderSubmitVO.setOmnitureProductString(omnitureProductString.toString());
		logDebug("Inside the method buildOmnitureTaggingString() method in InternationalCheckoutManager [END]");
	}
	
	public void  updateShippingPrice(Order order,Profile pProfile,String siteId) throws BBBSystemException, BBBBusinessException {
		for(final String shippingMethod : this.getShippingMethodList()) {
			final List<String> shippingList = this.getCatalogTools().getAllValuesForKey(
					BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, shippingMethod);
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
						double actualShipping=getPricingTools().calculateShippingCost(siteId, hd.getShippingMethod(), hd, null);
						logDebug(" Final shipping applicable to user is "+actualShipping);
						priceInfo.setAmount(actualShipping); 
						priceInfo.setFinalShipping(actualShipping);
						BBBOrderPriceInfo pinfo =(BBBOrderPriceInfo)order.getPriceInfo();
						pinfo.setShipping(actualShipping);
					} catch (PricingException e) {
						logError("Exception While calculating Shipping Price For International Orders", e);
						throw new BBBSystemException(e.getMessage());
					}
					break;
				} else if (shippingMethod.equals(BBBInternationalShippingConstants.CONFIG_KEY_SHIPPING_WITH_PROMOTION) && shippingList.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
					logDebug(" using all shipping promotion for user as flag set is Shipping_Charge_With_Promotion ");
					break;
				} else if (shippingMethod.equals(BBBInternationalShippingConstants.CONFIG_KEY_SHIPPING_ZERO) && shippingList.get(0).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
					logDebug(" applying zero shipping charge for user as flag set is Shipping_Charge_Zero ");
					HardgoodShippingGroup hd =(HardgoodShippingGroup)	order.getShippingGroups().get(0);
					BBBShippingPriceInfo priceInfo=(BBBShippingPriceInfo)hd.getPriceInfo();
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
	
	public List<Object> getCurrencySymbol() {
		BBBSessionBean sessionBean = (BBBSessionBean)ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		String currency=(String) sessionBean.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY);
		String currencySymbol = BBBCoreConstants.BLANK;
		Integer currencyRoundingMethod = BBBCoreConstants.TWO;
		List<Object> currencyInfoList = null;
		try	{
			RepositoryItem	exchangeRates = this.getInternationalRepository().getItem(currency, BBBCatalogConstants.EXCHANGE_RATES);
			if (exchangeRates != null) {
				currencyInfoList = new ArrayList<Object>();
				currencySymbol = (String) exchangeRates.getPropertyValue(BBBInternationalShippingConstants.SHOPPERCURRENCY);
				currencyInfoList.add(currencySymbol);
				currencyRoundingMethod = (Integer) exchangeRates.getPropertyValue(BBBInternationalShippingConstants.ROUNDMETHOD);
				currencyInfoList.add(currencyRoundingMethod);
			}
		} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException"), e);
				logDebug("Error in getting exchangeRates from Repository "+ BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1002);
		}
		return currencyInfoList;
	}
	
	/**
	 * This method fetches currency Info based on currency code.
	 * @param currencyCode
	 * @return currencySymbol
	 */
	
	public List<Object> getCurrencyInfoBasedOnCurrencyCode(String currencyCode){
		String currencySymbol = BBBCoreConstants.BLANK;
		Integer currencyRoundingMethod = BBBCoreConstants.TWO;
		List<Object> currencyInfoList = null;
		try
		{
			RepositoryItem	exchangeRates = this.getInternationalRepository().getItem(currencyCode, BBBCatalogConstants.EXCHANGE_RATES);
			if (exchangeRates != null) {
				currencyInfoList = new ArrayList<Object>();
				currencySymbol = (String) exchangeRates.getPropertyValue(BBBInternationalShippingConstants.SHOPPERCURRENCY);
				currencyInfoList.add(currencySymbol);
				currencyRoundingMethod = (Integer) exchangeRates.getPropertyValue(BBBInternationalShippingConstants.ROUNDMETHOD);
				currencyInfoList.add(currencyRoundingMethod);
			}
			
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(null, "RepositoryException"), e);
			logDebug("Error in getting exchangeRates from Repository ");
		}
		return currencyInfoList;
	}

	public HashMap<String, String> getDataCenterMap() {
		return dataCenterMap;
	}

	public void setDataCenterMap(HashMap<String, String> dataCenterMap) {
		this.dataCenterMap = dataCenterMap;
	}
}
