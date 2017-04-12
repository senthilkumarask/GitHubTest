/*
 * 
 */
package com.bbb.internationalshipping.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;


import atg.adapter.gsa.GSARepository;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderServices;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.manager.InternationalCheckoutManager;
import com.bbb.internationalshipping.vo.BBBInternationalCheckoutVO;
import com.bbb.internationalshipping.vo.BBBInternationalContextVO;
import com.bbb.internationalshipping.vo.BBBInternationalOrderSubmitVO;
import com.bbb.internationalshipping.vo.BBBInternationalPropertyManagerVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;



/**
 * The class is contains all methods that have business logic across all features of international shipping.
 *
 */
public class InternationaShippingCheckoutHelper extends BBBGenericService {


	/** The tools. */
	private InternationalShipTools tools;
	
	/** The default country code. */
	private	String defaultCountryCode;
	
	/** The default currency code. */
	private	String defaultCurrencyCode;
	
	/** The country code. */
	private String countryCode;
	
	/** The currency code. */
	private String currencyCode;
	
	/** The country name. */
	private String countryName;
	
	/** The session bean. */
	private BBBSessionBean sessionBean;
	
	/** The order manager. */
	private BBBOrderManager orderManager;
	
	/** The profile adapter repository. */
	private GSARepository profileAdapterRepository;
	OrderHolder shoppingCart;
	private BBBPricingTools pricingTools;
	/**
	 * The InternationalCheckoutManager
	 */
	private InternationalCheckoutManager checkoutManager;
	
	/**
	 * @return the checkoutManager
	 */
	public final InternationalCheckoutManager getCheckoutManager() {
		return checkoutManager;
	}

	/**
	 * @param checkoutManager the checkoutManager to set
	 */
	public final void setCheckoutManager(final 
			InternationalCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}

	public BBBPricingTools getPricingTools() {
		return pricingTools;
	}

	public void setPricingTools(BBBPricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}

	Order order;


	public OrderHolder getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(OrderHolder shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	/** The transaction manager. */
	private TransactionManager transactionManager;
	
	/** The international order xml repo tools. */
	private InternationalOrderXmlRepoTools internationalOrderXmlRepoTools;
	
	/** The international property manager. */
	private BBBInternationalPropertyManagerVO internationalPropertyManager;
	
	/** The profile tools. */
	private ProfileTools profileTools;
	
	/** The international order repository. */
	private Repository internationalOrderRepository;
	
	/** The helper. */
	private InternationaShippingCheckoutHelper helper;
	
	/** The order services. */
	private OrderServices orderServices;
	
	/** The currency name. */
	private String currencyName;
	
	/** The display country currency. */
	private Boolean displayCountryCurrency;
	
	/** The context list. */
	private List<BBBInternationalContextVO> contextList=new ArrayList<BBBInternationalContextVO>();
	
	/** The currency map. */
	private Map<String,String> currencyMap;

	/** The Constant DISPLAY_COUNTRY_CURRENCY. */
	public static final String  DISPLAY_COUNTRY_CURRENCY="displayCountryCurrency";
	
	/** The Constant COUNTRY_CODE. */
	public static final String COUNTRY_CODE="countryCode";
	
	/** The Constant CURRENCY_CODE. */
	public static final String CURRENCY_CODE="currencyCode";
	
	/** The Constant ALL_CONTEXT_LIST. */
	public static final String ALL_CONTEXT_LIST="allContextList";
	
	/** The Constant ALL_CURRENCY_MAP. */
	public static final String ALL_CURRENCY_MAP="allCurrencyMap";
	
	/**
	 * The Order Repository.
	 */
	private Repository orderRepository;



	/**
	 * Gets the profile adapter repository.
	 *
	 * @return the profile adapter repository
	 */
	public GSARepository getProfileAdapterRepository() {
		return profileAdapterRepository;
	}

	/**
	 * Sets the profile adapter repository.
	 *
	 * @param profileAdapterRepository the new profile adapter repository
	 */
	public void setProfileAdapterRepository(
			final 	GSARepository profileAdapterRepository) {
		this.profileAdapterRepository = profileAdapterRepository;
	}

	/**
	 * Gets the order manager.
	 *
	 * @return the order manager
	 */
	public BBBOrderManager getOrderManager() {
		return this.orderManager;
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
	 * Gets the order services.
	 *
	 * @return the order services
	 */
	/**
	 * @return the orderRepository
	 */
	public final Repository getOrderRepository() {
		return orderRepository;
	}

	/**
	 * @param orderRepository the orderRepository to set
	 */
	public final void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}
	
	public OrderServices getOrderServices() {
		return orderServices;
	}

	/**
	 * Sets the order services.
	 *
	 * @param orderServices the new order services
	 */
	public void setOrderServices(final OrderServices orderServices) {
		this.orderServices = orderServices;
	}

	/**
	 * Gets the helper.
	 *
	 * @return the helper
	 */
	public InternationaShippingCheckoutHelper getHelper() {
		return helper;
	}

	/**
	 * Sets the helper.
	 *
	 * @param helper the new helper
	 */
	public void setHelper(final InternationaShippingCheckoutHelper helper) {
		this.helper = helper;
	}

	/**
	 * Gets the international order repository.
	 *
	 * @return the international order repository
	 */
	public Repository getInternationalOrderRepository() {
		return internationalOrderRepository;
	}

	/**
	 * Sets the international order repository.
	 *
	 * @param internationalOrderRepository the new international order repository
	 */
	public void setInternationalOrderRepository(
			final Repository internationalOrderRepository) {
		this.internationalOrderRepository = internationalOrderRepository;
	}

	/**
	 * Gets the international order xml repo tools.
	 *
	 * @return the international order xml repo tools
	 */
	public InternationalOrderXmlRepoTools getInternationalOrderXmlRepoTools() {
		return internationalOrderXmlRepoTools;
	}

	/**
	 * Sets the international order xml repo tools.
	 *
	 * @param internationalOrderXmlRepoTools the new international order xml repo tools
	 */
	public void setInternationalOrderXmlRepoTools(
			final InternationalOrderXmlRepoTools internationalOrderXmlRepoTools) {
		this.internationalOrderXmlRepoTools = internationalOrderXmlRepoTools;
	}

	/**
	 * Gets the international property manager.
	 *
	 * @return the international property manager
	 */
	public BBBInternationalPropertyManagerVO getInternationalPropertyManager() {
		return internationalPropertyManager;
	}

	/**
	 * Sets the international property manager.
	 *
	 * @param internationalPropertyManager the new international property manager
	 */
	public void setInternationalPropertyManager(
			final BBBInternationalPropertyManagerVO internationalPropertyManager) {
		this.internationalPropertyManager = internationalPropertyManager;
	}

	/**
	 * Gets the profile tools.
	 *
	 * @return the profile tools
	 */
	public ProfileTools getProfileTools() {
		return profileTools;
	}

	/**
	 * Sets the profile tools.
	 *
	 * @param profileTools the new profile tools
	 */
	public void setProfileTools(final ProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	/**
	 * Gets the session bean.
	 *
	 * @return the session bean
	 */
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}




	/**
	 * Sets the session bean.
	 *
	 * @param sessionBean the new session bean
	 */
	public void setSessionBean(final BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}




	/**
	 * Gets the country name.
	 *
	 * @return the country name
	 */
	public String getCountryName() {
		return countryName;
	}




	/**
	 * Sets the country name.
	 *
	 * @param countryName the new country name
	 */
	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}




	/**
	 * Gets the currency name.
	 *
	 * @return the currency name
	 */
	public String getCurrencyName() {
		return currencyName;
	}




	/**
	 * Sets the currency name.
	 *
	 * @param currencyName the new currency name
	 */
	public void setCurrencyName(final String currencyName) {
		this.currencyName = currencyName;
	}



	/**
	 * Gets the tools.
	 *
	 * @return the tools
	 */
	public InternationalShipTools getTools() {
		return tools;
	}




	/**
	 * Sets the tools.
	 *
	 * @param tools the new tools
	 */
	public void setTools(final InternationalShipTools tools) {
		this.tools = tools;
	}




	/**
	 * Gets the default country code.
	 *
	 * @return the default country code
	 */
	public String getDefaultCountryCode() {
		return defaultCountryCode;
	}




	/**
	 * Sets the default country code.
	 *
	 * @param defaultCountryCode the new default country code
	 */
	public void setDefaultCountryCode(final String defaultCountryCode) {
		this.defaultCountryCode = defaultCountryCode;
	}




	/**
	 * Gets the default currency code.
	 *
	 * @return the default currency code
	 */
	public String getDefaultCurrencyCode() {
		return defaultCurrencyCode;
	}




	/**
	 * Sets the default currency code.
	 *
	 * @param defaultCurrencyCode the new default currency code
	 */
	public void setDefaultCurrencyCode(final String defaultCurrencyCode) {
		this.defaultCurrencyCode = defaultCurrencyCode;
	}


	/**
	 * Gets the country code.
	 *
	 * @return the country code
	 */
	public String getCountryCode() {
		return countryCode;
	}


	/**
	 * Sets the country code.
	 *
	 * @param countryCode the new country code
	 */
	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}


	/**
	 * Gets the currency code.
	 *
	 * @return the currency code
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}



	/**
	 * Sets the currency code.
	 *
	 * @param currencyCode the new currency code
	 */
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}




	/**
	 * Gets the display country currency.
	 *
	 * @return the display country currency
	 */
	public Boolean getDisplayCountryCurrency() {
		return displayCountryCurrency;
	}




	/**
	 * Sets the display country currency.
	 *
	 * @param displayCountryCurrency the new display country currency
	 */
	public void setDisplayCountryCurrency(final Boolean displayCountryCurrency) {
		this.displayCountryCurrency = displayCountryCurrency;
	}




	/**
	 * Gets the context list.
	 *
	 * @return the context list
	 */
	public List<BBBInternationalContextVO> getContextList() {
		return contextList;
	}




	/**
	 * Sets the context list.
	 *
	 * @param contextList the new context list
	 */
	public void setContextList(final List<BBBInternationalContextVO> contextList) {
		this.contextList = contextList;
	}




	/**
	 * Gets the currency map.
	 *
	 * @return the currency map
	 */
	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}




	/**
	 * Sets the currency map.
	 *
	 * @param currencyMap the currency map
	 */
	public void setCurrencyMap(final Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}



	/**
	 * This method sets the default country and currency to be shown to the user on country currency modal window.
	 *
	 * @param pRequest the new checkout param
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void setCheckoutParam(final DynamoHttpServletRequest pRequest ) throws BBBSystemException, BBBBusinessException
	{

		this.logDebug(LogMessageFormatter.formatMessage(null,
				"Inside  method of InternationaShippingCheckoutHelper : setCheckoutParam"));

		Cookie cookie=null;

		cookie=this.getTools().getIntShipCookieAvailable(pRequest);
		//Get all context information to display drop down
		this.contextList=this.tools.getInternationalShippingBuilder().buildContextAll();
		//get all cuurency info to diplay in drop down
		this.setCurrencyMap(this.tools.getInternationalShippingBuilder().buildCurrencyMap(this.contextList));

		if(cookie !=null){
			final 	List<String> intUserCookieSelection= this.getTools().getDefaultFrmCookie(cookie);
			if(intUserCookieSelection!=null && !intUserCookieSelection.isEmpty()){
				final String countryCodeFromCookie=intUserCookieSelection.get(0);
				final String currencyCodeFromCookie=intUserCookieSelection.get(1);

				final BBBInternationalContextVO contextVO=this.getTools().getContextVO(countryCodeFromCookie);
				if(contextVO!=null && contextVO.getShippingLocation().isShippingEnabled())
				{

					this.setCountryCode(countryCodeFromCookie);	
					this.setDisplayCountryCurrency(true);
					this.setCountryName(contextVO.getShippingLocation().getCountryName());
					this.setCurrencyName(contextVO.getShoppingCurrency().getCurrencyName());
					if(currencyMap.containsValue(currencyCodeFromCookie)){
						this.setCurrencyCode(currencyCodeFromCookie);
					}
					else{
						this.setCurrencyCode(this.getDefaultCurrencyCode());
					}


				}
				else {
					this.setCountryCurrencyFromIP(pRequest);
				}
			}

		}
		else{
			this.setCountryCurrencyFromIP(pRequest);
		}


	}

	/**
	 * Sets the checkout param rest.
	 *
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */

	public void setCheckoutParamREST( ) throws BBBSystemException, BBBBusinessException
	{

		final DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();

		Cookie cookie=null;

		cookie=this.getTools().getIntShipCookieAvailable(pRequest);

		this.contextList=this.tools.getInternationalShippingBuilder().buildContextAll();

		this.setCurrencyMap(this.tools.getInternationalShippingBuilder().buildCurrencyMap(this.contextList));


		if(cookie !=null){
			final List<String> intUserCookieSelection= this.getTools().getDefaultFrmCookie(cookie);
			if(intUserCookieSelection!=null && !intUserCookieSelection.isEmpty()){
				final String countryCodeFromCookie=intUserCookieSelection.get(0);
				final String currencyCodeFromCookie=intUserCookieSelection.get(1);

				final BBBInternationalContextVO contextVO=this.getTools().getContextVO(countryCodeFromCookie);
				if(contextVO!=null && contextVO.getShippingLocation().isShippingEnabled())
				{

					this.setCountryCode(countryCodeFromCookie);	
					this.setDisplayCountryCurrency(true);
					this.setCountryName(contextVO.getShippingLocation().getCountryName());
					this.setCurrencyName(contextVO.getShoppingCurrency().getCurrencyName());
					if(currencyMap.containsValue(currencyCodeFromCookie)){
						this.setCurrencyCode(currencyCodeFromCookie);
					}
					else{
						this.setCurrencyCode(this.getDefaultCurrencyCode());
					}


				}
				else {
					this.setCountryCurrencyFromIP(pRequest);
				}
			}

		}
		else{
			this.setCountryCurrencyFromIP(pRequest);
		}


	}

	/**
	 * Gets the checkout param.
	 *
	 * @return the checkout param
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	//Rest call for mobile
	public BBBInternationalCheckoutVO getCheckoutParam( ) throws BBBSystemException, BBBBusinessException
	{
		this.logDebug(LogMessageFormatter.formatMessage(null,
				"Inside  method of InternationaShippingCheckoutHelper : getCheckoutParam"));
		final BBBInternationalCheckoutVO checkoutVo=new BBBInternationalCheckoutVO();
		this.contextList=this.tools.getInternationalShippingBuilder().buildContextAll();
		checkoutVo.setContextList(this.contextList);
		checkoutVo.setCurrencyMap(this.tools.getInternationalShippingBuilder().buildCurrencyMap(this.contextList));
		this.setCheckoutParamREST();
		checkoutVo.setCountryCode(this.getCountryCode());
		checkoutVo.setCountryName(this.countryName);
		checkoutVo.setCurrencyCode(this.currencyCode);
		checkoutVo.setCurrencyName(this.currencyName);


		return checkoutVo;
	}
	
	/**
	 * This method is to set default country and currency for user  based on the IP.
	 *
	 * @param pRequest the new country currency from ip
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */

	private void setCountryCurrencyFromIP(final DynamoHttpServletRequest pRequest) throws BBBSystemException, BBBBusinessException{


		final String countryCodeFromSession=this.getCountryCodeFromSession();
		if(countryCodeFromSession.equalsIgnoreCase(this.getDefaultCountryCode())){
			this.setCountryCode(null);
			this.setCurrencyCode(null);
			this.setCountryName(null);
			this.setCurrencyName(null);
			this.setDisplayCountryCurrency(false);
		}
		else{
			final BBBInternationalContextVO contextVO=this.getTools().getContextVO(countryCodeFromSession);
			if(contextVO!=null)
			{
				this.setCountryCode(contextVO.getShippingLocation().getCountryCode());
				this.setCurrencyCode(contextVO.getShoppingCurrency().getCurrencyCode());
				this.setCountryName(contextVO.getShippingLocation().getCountryName());
				this.setCurrencyName(contextVO.getShoppingCurrency().getCurrencyName());


			}
			if(this.getDefaultCountryCode().equalsIgnoreCase(countryCodeFromSession) || this.getDefaultCountryCode().equalsIgnoreCase(countryCode))
			{
				this.setDisplayCountryCurrency(false);
			}
			else
			{
				this.setDisplayCountryCurrency(true);
			}

		}

	}
	
	/**
	 * Gets the country code from session.
	 *
	 * @return the country code from session
	 */
	public String getCountryCodeFromSession(){
		String countryCodeFromSession = null;
		@SuppressWarnings("rawtypes")
		final HashMap  sessionMap=sessionBean.getValues();
		if(sessionMap!=null&& !sessionMap.isEmpty()){
			countryCodeFromSession= (String)sessionMap.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);

		}

		if(StringUtils.isEmpty(countryCodeFromSession))
		{
			countryCodeFromSession=this.getDefaultCountryCode();
		}
		return countryCodeFromSession;
	}


	
	/**
	 * The method is used to perform following tasks for International Order Mobile  
	 * • Add the items to cart after logging out User 
	 * -
	 *
	 * @param catalogRefId the catalogRefId
	 * @param productId the productId
	 * @param quantity the quantity
	 * @param vdcInd the quantity
	 * @param freeShippingcharge the quantity
	 * @param surcharge the quantity
	 */
	public boolean restAddItemstoCart(String catalogRefId,String productId,String quantity,String vdcInd,String freeShippingcharge,String surcharge,String registryId, String registryInfo){
		  
		  boolean resultStatus = true; 
		  logInfo("New order id..."+getShoppingCart().getCurrent().getId());
		  DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
		  
		  final Profile profile = (Profile) pRequest.resolveName("/atg/userprofiling/Profile");
		  OrderHolder cart = (OrderHolder) pRequest.resolveName("/atg/commerce/ShoppingCart");
		  BBBOrder newOrder=null;

		  try {
				 newOrder = (BBBOrder) getOrderManager().createOrder(profile.getRepositoryId(), getOrderManager().getOrderTools().getDefaultOrderType());
		  } catch (CommerceException e1) {
				 vlogError("Inside Exception: Exception occured while creating order in restAddItemstoCart" + e1);
				 resultStatus = false;
		  }
		  if(resultStatus){
		  String catalogId[]=catalogRefId.split(",");
		  String productIds[]=productId.split(",");
		  String quantitys[]=quantity.split(",");
		  String vdcIND[]=vdcInd.split(",");
		  String freeShippingcharges[]=freeShippingcharge.split(",");
		  String surcharges[]=surcharge.split(",");
		  String registryIds[]=registryId.split(",");
		  String registryInfos[]=registryInfo.split(",");

		  if(newOrder != null){
			  cart.setCurrent(newOrder);
			 logDebug(LogMessageFormatter.formatMessage(pRequest, "New order created with id:" + newOrder.getId()));
		  }
		  
		  if(catalogId.length >0){
		  for(int i=0;i<catalogId.length;i++){
			 order = cart.getCurrent();
			 boolean shouldRollback = false;
			 TransactionDemarcation td2 = new TransactionDemarcation();
		  
			try {
			 td2.begin(getOrderManager().getOrderTools().getTransactionManager());
			 synchronized (order) {
				  BBBCommerceItem ci = (BBBCommerceItem)getOrderManager().getCommerceItemManager().createCommerceItem(catalogId[i],productIds[i],Integer.parseInt(quantitys[i].trim()));
				  ci.setVdcInd(Boolean.parseBoolean(vdcIND[i]));
				  ci.setFreeShippingMethod(freeShippingcharges[i]);
				  ci.setSkuSurcharge(Double.parseDouble(surcharges[i]));
				  ci.setRegistryId(registryIds[i]);
				  ci.setRegistryInfo(registryInfos[i]);
				  getOrderManager().getCommerceItemManager().addItemToOrder(order, ci);
				   
				   if(order.getCommerceItems() != null){
					  /* add a default payment group to the order */
					  getOrderManager().getPaymentGroupManager().removeAllPaymentGroupsFromOrder(order);
					  PaymentGroup defaultPaymentGroup =getOrderManager().getPaymentGroupManager().createPaymentGroup();
					  getOrderManager().getPaymentGroupManager().addPaymentGroupToOrder(order, defaultPaymentGroup);

					  final ShippingGroupManager shippingGroupManager = getOrderManager().getShippingGroupManager();
					  shippingGroupManager.removeAllShippingGroupsFromOrder(order);
					  /* add a default shipping group to the order */
					  ShippingGroup defaultShippingGroup = shippingGroupManager.createShippingGroup();
					  shippingGroupManager.addShippingGroupToOrder(order, defaultShippingGroup);


					  for (BBBCommerceItem bci : (List<BBBCommerceItem>) order.getCommerceItems()) {
						 if (bci.getShippingGroupRelationshipCount() < 1) {
								getOrderManager().getCommerceItemManager().addItemQuantityToShippingGroup(order, bci.getId(),
											  defaultShippingGroup.getId(),bci.getQuantity());
						 }
					  }
				   }
				   
				   this.getPricingTools().priceOrderTotal(order);
				   getOrderManager().updateOrder(order);
				 }
			}
			  catch (CommerceException e) {
					 shouldRollback = true;
					 pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
					 vlogError("Inside Exception: Exception occured while updating order" + e);
					 resultStatus = false;
					 
			  } catch (TransactionDemarcationException e) {
					 shouldRollback = true;
					 pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
					 vlogError("Inside Exception: Exception occured while getting Transaction" + e);
					 resultStatus = false;
					 
			  } finally {
					 try {
						   td2.end(shouldRollback);
					 } catch (TransactionDemarcationException e) {
						   this.logError("Transaction roll back error", e);
					 }
			  		}
		  		}
		  	}
		  }
		 return resultStatus;
	   }


	/**
	 * The method is used to perform following tasks for Submission of International Order
	 * • If success URL is invoked, read the order XML, convert to order object and persist it. Change state of order to INTL_SUBMITTED and remove the XML from DB. Decrement the inventory.
	 * • If pending URL is invoked, read the order XML, convert to order object and persist it. Change state of order to INTL_HOLD and remove the XML from DB. Decrement the inventory. 
	 * • If failure URL is invoked, read the order XML, convert to order object and persist it. Cancel the order and remove the XML from DB. Do not decrement the inventory. 
	 * -
	 *
	 * @param orderId the order id
	 * @param fraudState the fraud state
	 * @param countrycode2 the countrycode2
	 * @param currencyCode2 the currency code2 
	 * @param omnitureProductString 
	 * @param orderSubmitVO 
	 * @throws BBBSystemException 
	 * @throws CommerceException 
	 */

	public void persistOrderFromXml(final String orderId, final String merchantOrderId, final String fraudState, final String countrycode2, final String currencyCode2, BBBInternationalOrderSubmitVO orderSubmitVO) throws BBBSystemException  {
		final StringBuffer buffer= new StringBuffer("Inside  service of InternationaShippingCheckoutHelper : persistXml with parameters order Id  ").append(orderId).
				append("fraudState ").append(fraudState).append("country code ").append(countrycode2).append("currency Code ").append(currencyCode2);
		this.logDebug(buffer.toString());
		try {
			this.orderUpdate(orderId, merchantOrderId, fraudState, countrycode2, currencyCode2, orderSubmitVO);
		} catch (BBBSystemException e) {
			logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while creating order from xml", e);
			throw e;
		} catch (CommerceException e) {
			logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ":Error while creating order from xml", e);
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while creating order from xml", e);
		}
	}

	
	/**
	 * Order update.
	 *
	 * @param orderId the order id
	 * @param fraudState the fraud state
	 * @param omnitureProductString 
	 * @param orderSubmitVO 
	 * @param countrycode2 the countrycode2
	 * @param currencyCode2 the currency code2
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws CommerceException 
	 */
	private void orderUpdate(final String orderId, final String merchantOrderId, final String fraudState,final String countrycode,final  String currencyCode, BBBInternationalOrderSubmitVO orderSubmitVO) throws BBBSystemException, CommerceException {
		String internationalOrderId = null;
		internationalOrderId = this.getInternationalOrderId(orderId);
		this.getCheckoutManager().updateInternationalOrder(orderId, merchantOrderId, fraudState, countrycode, currencyCode, internationalOrderId, true, orderSubmitVO);
	}


	/**
	 * This method returns the E4x Order Id for the ATG Order Id
	 * from the International Order Repository.
	 * @param orderid
	 * @return
	 * @throws BBBSystemException 
	 * @throws RepositoryException
	 */
	private String getInternationalOrderId(String orderid) throws BBBSystemException {
		String internationalOrderId = null;
		try {
			final RepositoryItem orderItem = this.getInternationalOrderRepository().getItem(orderid, BBBInternationalShippingConstants.INTERNATIONAL_ORDER);
			if (null != orderItem) {
				internationalOrderId = (String) orderItem.getPropertyValue(BBBInternationalShippingConstants.INTL_EXCHANGE_OORDER_ID);
			}
		}
		catch(RepositoryException e){
			logError(e);
			throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1016, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1016);
		}
		return internationalOrderId;
	}

	
	/**
	 * Decrement inventory repository.
	 *
	 * @param pOrder the order
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	//end envoy order persist

	

	protected void commitTransaction( boolean isRollback, Order order) {

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
			logError(exc);
		}
		catch (HeuristicMixedException exc) {
			exception = true;
			logError(exc);
		}
		catch (HeuristicRollbackException exc) {
			exception = true;
			logError(exc);
		}
		catch (SystemException exc) {
			exception = true;
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
		      logError(exc);
		  }
		  return false;
		}


}
