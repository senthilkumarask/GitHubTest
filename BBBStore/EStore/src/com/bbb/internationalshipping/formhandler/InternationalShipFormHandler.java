package com.bbb.internationalshipping.formhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.webservices.BBBProfileServices;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.formhandler.BBBShippingGroupFormhandler;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.priceLists.BBBPriceListProfilePropertySetter;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.manager.BBBInternationalSubmitOrderManager;
import com.bbb.internationalshipping.manager.InternationalCheckoutManager;
import com.bbb.internationalshipping.utils.InternationalShipTools;
import com.bbb.internationalshipping.vo.BBBInternationalContextVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBInternationalCheckoutInputVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBInternationalCheckoutResponseVO;
import com.bbb.internationalshipping.vo.checkoutrequest.IntlShippingErrorMessage;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSARepository;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.order.purchase.CartModifierFormHandler;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingException;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.idgen.IdGeneratorException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.BBBRequestLocale;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.CookieManager;
import atg.userprofiling.Profile;

/**
 * The Class InternationalShipFormHandler.
 */
public class InternationalShipFormHandler extends PurchaseProcessFormHandler {

	private static final String JVM_PROPERTY="weblogic.Name";
	
	private String dcPrefix;

	/** The success url. */
	private String successURL;
	/** The error url. */
	private String errorURL;
	
	private BBBProfileServices profileService;
	
	private boolean loggedIn=false;

	private String currencySymbol;
	
	private Integer currencyRoundingMethod;
	
	private List<String> urlPattern;
	
	private CookieManager cookieManager;
	
	private String exceptionString;
	
	private String exceptionCode;
	
	private Map<String,String> successUrlMap;
	
    private Map<String,String> errorUrlMap;
    
    private String fromPage; // Page Name that will be set from JSP
    
	/**
	 * @return fromPage
	 */
	public String getFromPage() {
		return fromPage;
	}

	/**
	 * @param fromPage
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

    
    public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}
	
	
	public CookieManager getCookieManager() {
		return cookieManager;
	}


	public void setCookieManager(CookieManager cookieManager) {
		this.cookieManager = cookieManager;
	}


	public String getCurrencySymbol() {
		return currencySymbol;
	}


	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}


	public Integer getCurrencyRoundingMethod() {
		return currencyRoundingMethod;
	}


	public void setCurrencyRoundingMethod(Integer currencyRoundingMethod) {
		this.currencyRoundingMethod = currencyRoundingMethod;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}


	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}



	public BBBProfileServices getProfileService() {
		return profileService;
	}


	public void setProfileService(BBBProfileServices profileService) {
		this.profileService = profileService;
	}

	/** The order. */
	Order order;
	
	/** The commerce item manager. */
	CommerceItemManager commerceItemManager;
	

	
	
	/** The restricted sku. */
	private List<String> restrictedSku;
	
	
	/** The bopus sku. */
	private List<String> bopusSku;
	
	/** The is rest. */
	private boolean isRest=false;
	
	
/** The Constant ERR_INTL_SHIP_RESPONSE_ERROR. */
public static final String ERR_INTL_SHIP_RESPONSE_ERROR="ERR_INTL_SHIP_RESPONSE_ERROR";
public static final String ERROR_INTL_SHIP_1001="error_intl_ship_1001";

/** The ship group form handler. */
private BBBShippingGroupFormhandler shipGroupFormHandler;

	/** The tools. */
	private InternationalShipTools tools;
	/** The user selected country. */
	String userSelectedCountry;
	/** The user selected currency. */
	String userSelectedCurrency;
	/** The checkout manager. */
	private BBBCheckoutManager checkoutManager;
	
	/** The lbl txt template manager. */
	private LblTxtTemplateManager lblTxtTemplateManager;

	/** The profile form handler. */
	private BBBProfileFormHandler profileFormHandler; 

	/** The intl checkout manager. */
	private InternationalCheckoutManager intlCheckoutManager;

	/** The transaction manager. */
	private TransactionManager transactionManager;

	/** The profile adapter repository. */
	private GSARepository profileAdapterRepository;
	
	/** The checkout response vo. */
	BBBInternationalCheckoutResponseVO checkoutResponseVO;

	OrderHolder shoppingCart;
	private BBBPricingTools pricingTools;
    private BBBInventoryManager inventoryManager;
	private BBBInternationalSubmitOrderManager submitOrderManager;
	
	/** ATG OOTB RequestLocale. */
	private BBBRequestLocale requestLocale;
	
	private BBBSessionBean sessionBean;
	
	private String usCountryCode;
	
	private String usCurrencyCode;	
	
	private BBBInternationalContextVO intContextVO;
	
	private BBBPriceListProfilePropertySetter priceListProfilePropertySetter;

	/**
	 * @return the priceListProfilePropertySetter
	 */
	public BBBPriceListProfilePropertySetter getPriceListProfilePropertySetter() {
		return priceListProfilePropertySetter;
	}


	/**
	 * @param priceListProfilePropertySetter the priceListProfilePropertySetter to set
	 */
	public void setPriceListProfilePropertySetter(
			BBBPriceListProfilePropertySetter priceListProfilePropertySetter) {
		this.priceListProfilePropertySetter = priceListProfilePropertySetter;
	}


	/**
	 * @return the intContextVO
	 */
	public BBBInternationalContextVO getIntContextVO() {
		return intContextVO;
	}


	/**
	 * @param intContextVO the intContextVO to set
	 */
	public void setIntContextVO(BBBInternationalContextVO intContextVO) {
		this.intContextVO = intContextVO;
	}

	/**
	 * @return the usCountryCode
	 */
	public String getUsCountryCode() {
		return usCountryCode;
	}


	/**
	 * @param usCountryCode the usCountryCode to set
	 */
	public void setUsCountryCode(String usCountryCode) {
		this.usCountryCode = usCountryCode;
	}


	/**
	 * @return the usCurrencyCode
	 */
	public String getUsCurrencyCode() {
		return usCurrencyCode;
	}


	/**
	 * @param usCurrencyCode the usCurrencyCode to set
	 */
	public void setUsCurrencyCode(String usCurrencyCode) {
		this.usCurrencyCode = usCurrencyCode;
	}


	/**
	 * @return the requestLocale
	 */
	public BBBRequestLocale getRequestLocale() {
		return requestLocale;
	}


	/**
	 * @param requestLocale the requestLocale to set
	 */
	public void setRequestLocale(BBBRequestLocale requestLocale) {
		this.requestLocale = requestLocale;
	}

	/**
	 * @return the sessionBean
	 */
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}


	/**
	 * @param sessionBean the sessionBean to set
	 */
	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}


	/**
	 * @return the submitOrderManager
	 */
	public final BBBInternationalSubmitOrderManager getSubmitOrderManager() {
		return submitOrderManager;
	}


	/**
	 * @param submitOrderManager the submitOrderManager to set
	 */
	public final void setSubmitOrderManager(
			BBBInternationalSubmitOrderManager submitOrderManager) {
		this.submitOrderManager = submitOrderManager;
	}


	/** @return the inventoryManager */
    public final BBBInventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    /** @param inventoryManager the inventoryManager to set */
    public final void setInventoryManager(final BBBInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }
    
    public OrderHolder getShoppingCart() {
		return shoppingCart;
	}


	public void setShoppingCart(OrderHolder shoppingCart) {
		this.shoppingCart = shoppingCart;
	}


	/**
	 * Checks if is rest.
	 *
	 * @return true, if is rest
	 */
	public boolean isRest() {
		return isRest;
	}


	/**
	 * Sets the rest.
	 *
	 * @param isRest the new rest
	 */
	public void setRest(final boolean isRest) {
		this.isRest = isRest;
	}




	/**
	 * Gets the ship group form handler.
	 *
	 * @return the ship group form handler
	 */
	public BBBShippingGroupFormhandler getShipGroupFormHandler() {
		return shipGroupFormHandler;
	}


	/**
	 * Sets the ship group form handler.
	 *
	 * @param shipGroupFormHandler the new ship group form handler
	 */
	public void setShipGroupFormHandler(
			final	BBBShippingGroupFormhandler shipGroupFormHandler) {
		this.shipGroupFormHandler = shipGroupFormHandler;
	}


	/**
	 * Gets the restricted sku.
	 *
	 * @return the restricted sku
	 */
	public List<String> getRestrictedSku() {
		return restrictedSku;
	}


	/**
	 * Sets the restricted sku.
	 *
	 * @param restrictedSku the new restricted sku
	 */
	public void setRestrictedSku(final List<String> restrictedSku) {
		this.restrictedSku = restrictedSku;
	}


	/**
	 * Gets the bopus sku.
	 *
	 * @return the bopus sku
	 */
	public List<String> getBopusSku() {
		return bopusSku;
	}


	/**
	 * Sets the bopus sku.
	 *
	 * @param bopusSku the new bopus sku
	 */
	public void setBopusSku(final List<String> bopusSku) {
		this.bopusSku = bopusSku;
	}


	


	/**
	 * Gets the commerce item manager.
	 *
	 * @return the commerce item manager
	 */
	public CommerceItemManager getCommerceItemManager() {
		return commerceItemManager;
	}


	/**
	 * Sets the commerce item manager.
	 *
	 * @param commerceItemManager the new commerce item manager
	 */
	public void setCommerceItemManager(final CommerceItemManager commerceItemManager) {
		this.commerceItemManager = commerceItemManager;
	}



	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}


	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 */
	public void setOrder(final Order order) {
		this.order = order;
	}


	/**
	 * Gets the checkout response vo.
	 *
	 * @return the checkout response vo
	 */
	public BBBInternationalCheckoutResponseVO getCheckoutResponseVO() {
		return checkoutResponseVO;
	}


	/**
	 * Sets the checkout response vo.
	 *
	 * @param checkoutResponseVO the new checkout response vo
	 */
	public void setCheckoutResponseVO(
			final BBBInternationalCheckoutResponseVO checkoutResponseVO) {
		this.checkoutResponseVO = checkoutResponseVO;
	}

	





	/**
	 * Gets the checkout manager.
	 *
	 * @return the checkout manager
	 */
	public BBBCheckoutManager getCheckoutManager() {
		return checkoutManager;
	}


	/**
	 * Sets the checkout manager.
	 *
	 * @param checkoutManager the new checkout manager
	 */
	public void setCheckoutManager(final BBBCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}


	/**
	 * Gets the lbl txt template manager.
	 *
	 * @return the lbl txt template manager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}


	/**
	 * Sets the lbl txt template manager.
	 *
	 * @param lblTxtTemplateManager the new lbl txt template manager
	 */
	public void setLblTxtTemplateManager(final LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
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
	 * Gets the user selected country.
	 *
	 * @return the user selected country
	 */
	public String getUserSelectedCountry() {
		return userSelectedCountry;
	}


	/**
	 * Sets the user selected country.
	 *
	 * @param userSelectedCountry the new user selected country
	 */
	public void setUserSelectedCountry(final String userSelectedCountry) {
		this.userSelectedCountry = userSelectedCountry;
	}


	/**
	 * Gets the user selected currency.
	 *
	 * @return the user selected currency
	 */
	public String getUserSelectedCurrency() {
		return userSelectedCurrency;
	}


	/**
	 * Sets the user selected currency.
	 *
	 * @param userSelectedCurrency the new user selected currency
	 */
	public void setUserSelectedCurrency(final String userSelectedCurrency) {
		this.userSelectedCurrency = userSelectedCurrency;
	}


	/**
	 * Gets the success url.
	 *
	 * @return the success url
	 */
	public String getSuccessURL() {
		return successURL;
	}


	/**
	 * Sets the success url.
	 *
	 * @param successURL the new success url
	 */
	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}


	/**
	 * Gets the error url.
	 *
	 * @return the error url
	 */
	public String getErrorURL() {
		return errorURL;
	}


	/**
	 * Sets the error url.
	 *
	 * @param errorURL the new error url
	 */
	public void setErrorURL(final String errorURL) {
		this.errorURL = errorURL;
	}


	/**
	 * Gets the profile form handler.
	 *
	 * @return the profile form handler
	 */
	public BBBProfileFormHandler getProfileFormHandler() {
		return profileFormHandler;
	}


	/**
	 * Sets the profile form handler.
	 *
	 * @param profileFormHandler the new profile form handler
	 */
	public void setProfileFormHandler(final BBBProfileFormHandler profileFormHandler) {
		this.profileFormHandler = profileFormHandler;
	}





	/**
	 * Gets the intl checkout manager.
	 *
	 * @return the intl checkout manager
	 */
	public InternationalCheckoutManager getIntlCheckoutManager() {
		return intlCheckoutManager;
	}


	/**
	 * Sets the intl checkout manager.
	 *
	 * @param intlCheckoutManager the new intl checkout manager
	 */
	public void setIntlCheckoutManager(
			final InternationalCheckoutManager intlCheckoutManager) {
		this.intlCheckoutManager = intlCheckoutManager;
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
	public void setProfileAdapterRepository(final GSARepository profileAdapterRepository) {
		this.profileAdapterRepository = profileAdapterRepository;
	}
	public String getDcPrefix() {
		return dcPrefix;
	}


	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}


	/**
	 * Handle currency country selector.the form handler sets cookie to store country and currency
	 * It validates if all the sku in the cart are eligible for international ship
	 *If all validation passes then a call to envoy is made and the response is shown n iFrame 
	 * @param pRequest the request
	 * @param pResponse the response
	 * @return true, if successful
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final boolean handleEnvoyCheckout(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
			throws ServletException,IOException {
		boolean shouldRollback = false; // NOPMD by njai13 on 6/5/14 11:42 AM
		//SAP-255 Changed code to get the source of traffic from Header field "origin_of_traffic" set in Mobile code for both Mobile Web and Mobile App.
		String channel = BBBUtility.getOriginOfTraffic();
		final TransactionDemarcation td = new TransactionDemarcation();
		String profileId=null;
		String merchantOrderId =null;
		final String userIPAddress;
		final String siteId = SiteContextManager.getCurrentSiteId();
		// BBBH-391 | Client DOM XSRF
		if (!BBBUtility.siteIsTbs(siteId)
				&& StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));
		}
		final BBBInternationalCheckoutInputVO bbbInputVO = new BBBInternationalCheckoutInputVO(); // NOPMD by njai13 on 6/5/14 11:42 AM

		if(this.getInventoryManager().checkUncachedInventory((BBBOrder) this.getOrder())){
			String successURL = pRequest.getContextPath() + "/cart/cart.jsp";
			return this.checkFormRedirect(successURL, null, pRequest, pResponse);
		}
		if(!StringUtils.isEmpty(getUserSelectedCountry()) && !StringUtils.isEmpty( getUserSelectedCurrency())){
			//add international order cookie to store country and currency selected by user
			//BBBUtility.addCookie(pResponse, this.getTools().setInternationalShipCookie(getUserSelectedCountry(), getUserSelectedCurrency(), pRequest), true);

			boolean isCommItemValid=false;

			
			if  (order != null) {
			
				List<CommerceItemVO> commerceItemVOs = null;
				
				try {
					//get all commerce items from cart
					commerceItemVOs = this.getCheckoutManager().getCartItemVOList(order);
					//check if all the commerec  items are eligible for international shipping
					isCommItemValid=this.validateCartForInternationalShipping(commerceItemVOs,pRequest,pResponse);
					
				} catch (BBBSystemException e) {
					isCommItemValid = false;
					logError(LogMessageFormatter.formatMessage(pRequest,
							"Error getting item details"), e);
					addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
							"ERR_INT_SHIP_WS_GENERIC_ERROR", pRequest.getLocale().getLanguage(),
							null, null),"ERR_INT_SHIP_WS_GENERIC_ERROR"));
				} catch (BBBBusinessException e) {
					isCommItemValid = false;
					logError(LogMessageFormatter.formatMessage(pRequest,
							"Error getting item details"), e);
					addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
							"ERR_INT_SHIP_WS_GENERIC_ERROR", pRequest.getLocale().getLanguage(),
							null, null),"ERR_INT_SHIP_WS_GENERIC_ERROR"));

				} 
				if(commerceItemVOs!=null && commerceItemVOs.size()!=0)
				{
					//if all items in cart are eligible for international ship make call to envoy
					if(!isCommItemValid){
						try {
							getIntlCheckoutManager().removeTaxInfo(order);
							getIntlCheckoutManager().removeGiftwrap(order);
						} catch (BBBBusinessException e2) {
							vlogError("Inside Exception: Exception occured while removing Giftwrap item in Order" + e2);
							addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
									ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
									null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
						} catch (RepositoryException e) {
							addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
									ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
									null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
						}
						final BBBOrder bbOrder =(BBBOrder)order;
						// Set Commerce Item Infor For REST ONLY
						
						/*if(isRest()){
							List<BBBCommerceItem> comItemRest = bbOrder.getCommerceItems();
							bbbComInfo = new BBBCommerceItemVO[comItemRest.size()];
							int indexRest = 0;
							for(int i=0;i<comItemRest.size();i++) {
								if(comItemRest.get(i) instanceof BBBCommerceItem){
									bbbComInfo[indexRest] = new BBBCommerceItemVO();
									bbbComInfo[indexRest].setCatalogRefId(comItemRest.get(i).getCatalogRefId());
									bbbComInfo[indexRest].setFreeShippingMethod(comItemRest.get(i).getFreeShippingMethod());
									bbbComInfo[indexRest].setProductId(comItemRest.get(i).getAuxiliaryData().getProductId());
									bbbComInfo[indexRest].setQuantity(comItemRest.get(i).getQuantity());
									bbbComInfo[indexRest].setSkuSurcharge(comItemRest.get(i).getSkuSurcharge());
									bbbComInfo[indexRest].setVdcInd(comItemRest.get(i).isVdcInd());
									bbbComInfo[indexRest].setRegistryId(comItemRest.get(i).getRegistryId());
									bbbComInfo[indexRest].setRegistryInfo(comItemRest.get(i).getRegistryInfo());
									
									indexRest++;
								}
							}
						}*/
						
						//Removing user explicit promotions/coupons for International shipping 
						@SuppressWarnings("unchecked")
						final Map<String, RepositoryItem> couponMap = ((BBBOrderImpl) this.getOrder()).getCouponMap();
						if(!couponMap.isEmpty())
						{
							removeUserPromotion(pRequest,pResponse,td,shouldRollback);
							
						}				

						if (order.getShippingGroupCount() > BBBCoreConstants.ONE) {
							if (isLoggingDebug()) {
								this.logDebug("Order :" + order.getId() + "has " + order.getShippingGroupCount() + " shipping groups");
							}
							this.updateShippingGroupsForInternationalCheckout(pRequest, td);
						}
						
						String e4XOrderId=null;
						try {
							if(null != order.getShippingGroups() && order.getShippingGroups().get(0) instanceof HardgoodShippingGroup) {
								HardgoodShippingGroup hd =(HardgoodShippingGroup)	order.getShippingGroups().get(0);
								//hd.getPriceInfo().getAmount();
								//hd.getShippingMethod();
								hd.setShippingMethod("3g");
								getShipGroupFormHandler().repriceOrder(pRequest,  pResponse);
								hd.getPriceInfo().getAmount();
								BBBShippingPriceInfo priceInfo=(BBBShippingPriceInfo)hd.getPriceInfo();
								bbbInputVO.setShippingAmount(priceInfo.getFinalShipping());
								//bbbInputVO.setShippingAmount(hd.getPriceInfo().getAmount());
								Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
								getIntlCheckoutManager().updateShippingPrice(order,profile,siteId);
							
							
							}
							merchantOrderId = getIntlCheckoutManager().createDSOrderId(pRequest, bbOrder,siteId);
							vlogInfo("dsOrder for intl Shipping...."+merchantOrderId);
							userIPAddress = this.getTools().getUserIpAddress(pRequest);
							bbbInputVO.setUserIPAddress(userIPAddress);
							bbbInputVO.setCountryCode(getUserSelectedCountry());
							bbbInputVO.setCurrencyCode(getUserSelectedCurrency());
							bbbInputVO.setOrder(order);
							bbbInputVO.setMerchantOrderId(merchantOrderId);
							bbbInputVO.setSiteId(siteId);
							bbbInputVO.setChannel(channel);
							if(null != pRequest.getSession()) {
								bbbInputVO.setUserSessionId(pRequest.getSession().getId());
							}
						} catch (BBBSystemException e1) {
							vlogError("Inside Exception: Exception occured while creating ds order ID" + e1);
							addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
									ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
									null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
						} catch (BBBBusinessException e1) {
							vlogError("Inside Exception: Exception occured while creating ds order ID" + e1);
							addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
									ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
									null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
						}
					
						if(this.getFormError()) {
							return this.checkFormRedirect(this.successURL, this.errorURL, pRequest,
									pResponse);
						}
					
						// Creates the Envoy checkout and sets the Checkout Response with the envoy url
						this.handleInternationalCheckout(pRequest, bbbInputVO);
						

						if(this.getFormError()) {
							return this.checkFormRedirect(this.successURL, this.errorURL, pRequest,
									pResponse);
						}
						
						//after getting success response from BF

						if (this.getCheckoutResponseVO() != null && !StringUtils.isBlank(this.getCheckoutResponseVO().getFullEnvoyUrl())) {
							pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_URL,(this.getCheckoutResponseVO().getFullEnvoyUrl()));
							e4XOrderId=this.getCheckoutResponseVO().getInternationalOrderId();
						} else {
							pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR, "true");
							addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
									ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
									null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
						
							final List<IntlShippingErrorMessage>  errorList = this.getCheckoutResponseVO().getErrorMessage();
							for(final IntlShippingErrorMessage error:errorList)
							{
								logError("ERROR:: ENVOY HTTP POST :: Message"+error.getMessage());
								logError("ERROR:: ENVOY HTTP POST :: Details"+error.getDetails());
							}
							logError("ERROR:: ENVOY HTTP POST :: Invalid Envoy Success URL");
							if(this.getFormError()) {
								return this.checkFormRedirect(this.successURL, this.errorURL, pRequest,
										pResponse);
							}
						}


						if(!getProfile().isTransient())
						{
							// Logout the International User
							shouldRollback = handleLogoutInternationalUser(
									pRequest, pResponse, shouldRollback, td,
									bbOrder);
							
							if(this.getFormError()) {
								return this.checkFormRedirect(this.successURL, this.errorURL, pRequest,
										pResponse);
							}

						}else{
							removePersonalizedItemsFromSession(pRequest);
						}
						
						//Persit ATG order xml into DB
						this.handlePersistOrderXml(pRequest, pResponse, shouldRollback, channel,
								td, bbOrder, e4XOrderId,merchantOrderId);
						if(this.getFormError()) {
							return this.checkFormRedirect(this.successURL, this.errorURL, pRequest,
									pResponse);
						}
					}

					else{
						pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
						addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
								"ERR_INT_SHIP_WS_GENERIC_ERROR", pRequest.getLocale().getLanguage(),
								null, null),"ERR_INT_SHIP_WS_GENERIC_ERROR"));
					}
				}
				else
				{
					//pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
					addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
							"", pRequest.getLocale().getLanguage(),
							null, null),""));
				}
			}
			
		}
		return this.checkFormRedirect(this.successURL, this.errorURL, pRequest,
				pResponse);

	}


	/**
	 * Method used to remove all the shipping groups from Order that originally has multiple shipping groups
	 * and add a default shipping group to order in case of international checkout.
	 * @param td 
	 * @param pRequest 
	 */
	@SuppressWarnings("unchecked")
	private void updateShippingGroupsForInternationalCheckout(DynamoHttpServletRequest pRequest, TransactionDemarcation td) {
		if (isLoggingDebug()) {
			this.logDebug("Entering - InternationalShipFormHandler Method Name [updateShippingGroupsForInternationalCheckout]");
		}
		
		boolean shouldRollback = false;
		try {
			td.begin(getOrderManager().getOrderTools().getTransactionManager());
			synchronized (order) {
				final ShippingGroupManager shippingGroupManager = this.getOrderManager().getShippingGroupManager();
				shippingGroupManager.removeAllShippingGroupsFromOrder(order);
				/* add a default shipping group to the order */
				ShippingGroup defaultShippingGroup = shippingGroupManager.createShippingGroup();
				shippingGroupManager.addShippingGroupToOrder(order, defaultShippingGroup);
	
				for (BBBCommerceItem ci : (List<BBBCommerceItem>) order.getCommerceItems()) {
					if (ci.getShippingGroupRelationshipCount() < BBBCoreConstants.ONE) {
						getOrderManager().getCommerceItemManager().addItemQuantityToShippingGroup(order, ci.getId(),
								defaultShippingGroup.getId(),ci.getQuantity());
					}
				}
//				this.getPricingTools().priceOrderTotal(order);
				getOrderManager().updateOrder(order);
				if (isLoggingDebug()) {
					this.logDebug("Order :" + order.getId() + "has " + order.getShippingGroupCount() + " shipping group after update ");
				}
			}
		} catch (CommerceException e) {
			shouldRollback = true;
			vlogError("Inside Exception: Exception occured while updating order" + e);
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
		} catch (TransactionDemarcationException e) {
			shouldRollback = true;
			vlogError("Inside Exception: Exception occured while getting Transaction" + e);
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
		}  finally {
			try {
				td.end(shouldRollback);
			} catch (TransactionDemarcationException e) {
				this.logError("Transaction roll back error", e);
			}
		}
		if (isLoggingDebug()) {
			this.logDebug("Exit - InternationalShipFormHandler Method Name [updateShippingGroupsForInternationalCheckout] :: Success : " + !shouldRollback );
		}
		
	}


	/**
	 * Method used to persist the Order XML.
	 * @param pRequest
	 * @param shouldRollback
	 * @param channel
	 * @param td
	 * @param bbOrder
	 * @param e4XOrderId
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws  
	 */
	private void handlePersistOrderXml (final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse,
			boolean shouldRollback, final String channel,
			final TransactionDemarcation td, final BBBOrder bbOrder, String e4XOrderId,String merchantOrderId) throws ServletException, IOException {
		
		//final HardgoodShippingGroup sg = (HardgoodShippingGroup)order.getShippingGroups().get(0);
	//	sg.setPropertyValue(BBBInternationalShippingConstants.CYBERSOURCE_VERIFIED, true);
		
		try {
			td.begin(getOrderManager().getOrderTools().getTransactionManager());
			
			synchronized (order) {
				final HardgoodShippingGroup sg = (HardgoodShippingGroup)order.getShippingGroups().get(0);
				sg.setPropertyValue(BBBInternationalShippingConstants.CYBERSOURCE_VERIFIED, true);
					order.setCreatedByOrderId(this.getDcPrefix() + "-" + System.getProperty(JVM_PROPERTY));
					if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel)) {
						order.setSalesChannel(BBBInternationalShippingConstants.CHANNEL_MOBILE_BFREE);
					} else if (BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel)){
						order.setSalesChannel(BBBInternationalShippingConstants.CHANNEL_MOBILE_APP_BFREE);
					}else{
						order.setSalesChannel(BBBInternationalShippingConstants.CHANNEL_DESKTOP_BFREE);
					}
				
				//set sales OS from traffic OS variable - get from request header.
				final String trafficOS = BBBUtility.getTrafficOS();
				if(trafficOS != null){
					((BBBOrder) order).setSalesOS(trafficOS);
				}
					
				this.logInfo("Order id ..."+getShoppingCart().getCurrent().getId());
				String	userIPAddress = this.getTools().getUserIpAddress(pRequest);
				((BBBOrder) order).setUserIP(userIPAddress);
				order.setState(BBBInternationalShippingConstants.INTL_INCOMPLETE_STATE);
				String deviceFingerPrint =null;
				if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel())
		                || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){
					if(BBBUtility.isNotEmpty(pRequest.getHeader(BBBCoreConstants.MOBILE_SESSION_ID))){
						deviceFingerPrint = pRequest.getHeader(BBBCoreConstants.MOBILE_SESSION_ID).split("!")[0];
					}
				}else{
					deviceFingerPrint = pRequest.getSession().getId().split("!")[0];
					
				}
				((BBBOrderImpl)order).setDeviceFingerprint(deviceFingerPrint);
				getOrderManager().updateOrder(order);
				
			}	
			
		} catch (TransactionDemarcationException exe) {
			shouldRollback = true;
			pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
			vlogError("Inside Exception: Exception occured while getting Transaction" + exe);
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
		} catch (CommerceException e) {
			shouldRollback = true;
			pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
			vlogError("Inside Exception: Exception occured while updating order" + e);
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
		}
		finally
		{
			try {
				
				td.end(shouldRollback);
			} catch (final TransactionDemarcationException tde) {
				this.logError("Transaction roll back error", tde);
			}
		}
		
		try {
			getIntlCheckoutManager().persistOrderXml(bbOrder.getId(), e4XOrderId,merchantOrderId,getUserSelectedCountry(),getUserSelectedCurrency());
			
			if(!isRest() || (isRest() && !isLoggedIn()))
			{
				this.logInfo("New order id..."+getShoppingCart().getCurrent().getId());
				order = getShoppingCart().getCurrent();
				shouldRollback = false;
				TransactionDemarcation td2 = new TransactionDemarcation();
				try {
					td2.begin(getOrderManager().getOrderTools().getTransactionManager());
					synchronized (order) {
						
						List<BBBCommerceItem> comItems = bbOrder.getCommerceItems();
						int index = 0;
						for(int i=0;i<comItems.size();i++) {
							BBBCommerceItem comItem=null;
							AddCommerceItemInfo[] atComInfo = new AddCommerceItemInfo[comItems.size()];
							if(comItems.get(i) instanceof BBBCommerceItem){
								comItem=comItems.get(i);
								atComInfo[index] = new AddCommerceItemInfo();
								atComInfo[index].setProductId(comItem.getAuxiliaryData().getProductId());
								atComInfo[index].setCatalogRefId(comItem.getCatalogRefId());
								atComInfo[index].setQuantity(comItem.getQuantity());
								BBBCommerceItem ci = (BBBCommerceItem)getOrderManager().getCommerceItemManager().createCommerceItem(
										atComInfo[index].getCatalogRefId(),
										atComInfo[index].getProductId(),
										atComInfo[index].getQuantity());
								ci.setVdcInd(comItem.isVdcInd());
								ci.setFreeShippingMethod(comItem.getFreeShippingMethod());
								ci.setSkuSurcharge(comItem.getSkuSurcharge());
								ci.setRegistryId(comItem.getRegistryId());
								ci.setRegistryInfo(comItem.getRegistryInfo());
								ci.setSeqNumber(comItem.getSeqNumber());
								getOrderManager().getCommerceItemManager().addItemToOrder(order, ci);
							}
							index++;
						}
						
						if(order.getCommerceItems() != null)
						{
							order.setOriginOfOrder(bbOrder.getOriginOfOrder());
							/* add a default payment group to the order */
							getOrderManager().getPaymentGroupManager().removeAllPaymentGroupsFromOrder(order);
							PaymentGroup defaultPaymentGroup =getOrderManager().getPaymentGroupManager().createPaymentGroup();
							getOrderManager().getPaymentGroupManager().addPaymentGroupToOrder(order, defaultPaymentGroup);

							final ShippingGroupManager shippingGroupManager = getOrderManager().getShippingGroupManager();
							shippingGroupManager.removeAllShippingGroupsFromOrder(order);
							/* add a default shipping group to the order */
							ShippingGroup defaultShippingGroup = shippingGroupManager.createShippingGroup();
							shippingGroupManager.addShippingGroupToOrder(order, defaultShippingGroup);

							for (BBBCommerceItem ci : (List<BBBCommerceItem>) order.getCommerceItems()) {
								if (ci.getShippingGroupRelationshipCount() < 1) {
									getOrderManager().getCommerceItemManager().addItemQuantityToShippingGroup(order, ci.getId(),
											defaultShippingGroup.getId(),ci.getQuantity());
								}
							}

						}
						if(!getProfile().isTransient())
						{
							String profileId= getSubmitOrderManager().getSiteSpecificProfile(order.getSiteId()).getRepositoryId();
							vlogInfo("new Profile created ..."+profileId);
							order.setProfileId(profileId);
						}
//						this.getPricingTools().priceOrderTotal(order);
						 //Repricing the order
			             this.runRepricingProcess(pRequest, pResponse, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING);
			            
			            getSubmitOrderManager().getOrderManager().initializePosListInSession(pRequest, (BBBOrder)order);
  						synchronized (order) {
			            	 getOrderManager().updateOrder(order);
			 			 }		
			             
					}
				} catch (CommerceException e) {
					shouldRollback = true;
					pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
					vlogError("Inside Exception: Exception occured while updating order" + e);
					addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
							ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
							null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
				} catch (final RunProcessException e) {
			    	shouldRollback = true;
		            this.logError(LogMessageFormatter.formatMessage(pRequest, "Exception occurred while repricing order",
	                            BBBCoreErrorConstants.CART_ERROR_1023), e);
			    }catch (TransactionDemarcationException e) {
					shouldRollback = true;
					pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
					vlogError("Inside Exception: Exception occured while getting Transaction" + e);
					addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
							ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
							null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
				} catch (RepositoryException e) {
					pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
					addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
							ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
							null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
				} finally {
					try {
						td2.end(shouldRollback);
					} catch (TransactionDemarcationException e) {
						this.logError("Transaction roll back error", e);
					}
				}
			 }
			
			} catch (BBBSystemException e) {
			pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));

		} catch (BBBBusinessException e) {
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));

		}
	}

	
	/**
	 * Method used to LogOut the User.
	 * @param pRequest
	 * @param pResponse
	 * @param shouldRollback
	 * @param td
	 * @param bbOrder
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean handleLogoutInternationalUser(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, boolean shouldRollback,
			final TransactionDemarcation td, final BBBOrder bbOrder)
			throws ServletException, IOException {
		String profileId;
		try {
			final MutableRepositoryItem  rItem=getProfileAdapterRepository().createItem("user");
			profileId= rItem.getRepositoryId();
			vlogInfo("new Profile created ..."+profileId);
			td.begin(getOrderManager().getOrderTools().getTransactionManager());
			synchronized (order) {
				order.setProfileId(profileId);
				getOrderManager().updateOrder(order);
			}	
		} catch (TransactionDemarcationException exe) {
			shouldRollback = true;
			pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
			vlogError("Inside Exception: Exception occured while getting Transaction" + exe);
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
		} catch (CommerceException e) {
			shouldRollback = true;
			pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
			vlogError("Inside Exception: Exception occured while updating order" + e);
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
		} catch (RepositoryException e) {
			pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
		}
		finally
		{
			try {
				td.end(shouldRollback);
			} catch (final TransactionDemarcationException tde) {
				this.logError("Transaction roll back error", tde);
			}
		}
		//create cartCookies
		getIntlCheckoutManager().createCartCookies(pRequest, pResponse, bbOrder);
		//logout logged in user
		getCookieManager().expireProfileCookies((Profile) this.getProfile(), pRequest, pResponse);

		if(isRest){
				this.setLoggedIn(true);
				this.getProfileService().logoutUser();
			
		}
		else{
		profileFormHandler.handleLogout(pRequest, pResponse);
		}
		removePersonalizedItemsFromSession(pRequest);
		pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_URL,(checkoutResponseVO.getFullEnvoyUrl()));
		return shouldRollback;
	}
	
	private void removePersonalizedItemsFromSession(DynamoHttpServletRequest request){
		BBBSessionBean sessionBean = (BBBSessionBean)request.resolveName(BBBCoreConstants.SESSION_BEAN);
		sessionBean.getPersonalizedSkus().clear();
	}
	

	/**
	 * This method calls the checkoutInternationalOrder method of InternationalCheckoutManager class.
	 * @param pRequest
	 * @param bbbInputVO
	 	 */
	private void handleInternationalCheckout(
			final DynamoHttpServletRequest pRequest,
			final BBBInternationalCheckoutInputVO bbbInputVO) {
		try {
			//get response from envoy
			this.setCheckoutResponseVO(this.getIntlCheckoutManager().checkoutInternationalOrder(bbbInputVO));
		} catch (BBBSystemException e1) {
			vlogError("Inside BBBSystemException: Exception occured while getting response from envoy " + e1);
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
			this.setExceptionString(e1.getMessage());
			this.setExceptionCode(ERROR_INTL_SHIP_1001);
			
		} catch (BBBBusinessException e1) {
			vlogError("Inside Exception: Exception occured Exception occured while getting response from envoy" + e1);
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
					null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
		}
	}


	/**
	 * handleCurrencyCountrySelectorRest().
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @return true, if successful
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 * @throws IdGeneratorException the id generator exception
	 */
	public final boolean handleEnvoyCheckoutRest(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
			throws ServletException,IOException, BBBSystemException, BBBBusinessException, IdGeneratorException {
		if(BBBUtility.isNotEmpty(getSuccessURL()) && getSuccessURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)
				&& BBBUtility.isNotEmpty(getErrorURL()) && getErrorURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT))
		{
			setSuccessURL("");
			setErrorURL("");
		}
		this.setRest(true);
		return this.handleEnvoyCheckout(pRequest, pResponse);
	}
	
	/**
	 * handleUpdateUserContextForRest().
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @return true, if successful
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 * @throws IdGeneratorException the id generator exception
	 */
	public final boolean handleUpdateUserContextForRest(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
			throws ServletException,IOException, BBBSystemException, BBBBusinessException, IdGeneratorException {
		if(BBBUtility.isNotEmpty(getSuccessURL()) && getSuccessURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)
				&& BBBUtility.isNotEmpty(getErrorURL()) && getErrorURL().equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)){
			setSuccessURL("");
			setErrorURL("");
		}
		this.setRest(true);
		if (!BBBUtility.isEmpty(getUserSelectedCurrency())) {
			List<Object> currencyInfoList = this.getIntlCheckoutManager().getCurrencyInfoBasedOnCurrencyCode(getUserSelectedCurrency());
			if (!BBBUtility.isListEmpty(currencyInfoList)) {
				this.setCurrencySymbol((String) currencyInfoList.get(BBBCoreConstants.ZERO));
				this.setCurrencyRoundingMethod((Integer) currencyInfoList.get(BBBCoreConstants.ONE));
			}
			
		}
		return this.handleUpdateUserContext(pRequest, pResponse);
	}


	/**
	 * Validate cart for international shipping.
	 *
	 * @param commerceItemVOs the commerce item v os
	 * @param pRequest the request
	 * @param pResponse the response
	 * @return true, if successful
	 * @throws BBBBusinessException the bBB business exception
	 * @throws BBBSystemException the bBB system exception
	 * @throws CommerceException the commerce exception
	 */
	@SuppressWarnings("unchecked")
	private boolean validateCartForInternationalShipping(final List<CommerceItemVO> commerceItemVOs,final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) throws BBBBusinessException, BBBSystemException {
		Boolean isError=false;
		/*final List<String> restrictedSkuList=new ArrayList<String>();*/
		final List<String> bopusSkuList=new ArrayList<String>();
		 BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		for(final CommerceItemVO commerceItemVO:commerceItemVOs){
			if(commerceItemVO!=null  ){
				final	String commerceItemId=commerceItemVO.getBBBCommerceItem().getId();
				/*if(commerceItemVO.getSkuDetailVO()!=null && this.getTools().getCatalogTools().isSkuRestrictedForIntShip(commerceItemVO.getSkuDetailVO())){
						final String skuId=commerceItemVO.getSkuDetailVO().getSkuId();
						this.logDebug(" Sku ID "+skuId+" is restricted for international shipping adding to list of restricted sku");
						restrictedSkuList.add(commerceItemId);
						isError=true;
						commerceItemVO.getSkuDetailVO().setRestrictedForIntShip(true);
				}*/
				if(commerceItemVO.getBBBCommerceItem()!=null && isBopusItem(commerceItemVO.getBBBCommerceItem())){
				
					this.logDebug(" commerceItem ID "+commerceItemId+" is a bopus item hence restricted for international shipping adding to list of bopus sku");
					bopusSkuList.add(commerceItemId);
				final List<ShippingGroupRelationship> shipGrpRelnList=commerceItemVO.getBBBCommerceItem().getShippingGroupRelationships();
					for(final ShippingGroupRelationship shipGrpReln:shipGrpRelnList){
						final ShippingGroupImpl shipGrp=	(ShippingGroupImpl) shipGrpReln.getShippingGroup();
						if(!(shipGrp instanceof HardgoodShippingGroup)){
						
							shipGroupFormHandler.setOldShippingId(shipGrp.getId());
							shipGroupFormHandler.setCommerceItemId(commerceItemId);
							shipGroupFormHandler.setNewQuantity(Long.toString(commerceItemVO.getBBBCommerceItem().getQuantity()));
							try {
								shipGroupFormHandler.handleChangeToShipOnline(pRequest, pResponse);
							} catch (ServletException e) {
								logError(" servlet exception for commerce id"+commerceItemId);
							} catch (IOException e) {
								logError(" IOException exception for commerce id"+commerceItemId);
							}
						}
					}
					isError=true;
					commerceItemVO.getSkuDetailVO().setRestrictedForBopusAllowed(true);
				}
			}
		}
		/*if(!restrictedSkuList.isEmpty()){
			sessionBean.getValues().put(BBBInternationalShippingConstants.RESTRICTED_SKU_LIST_IN_SESSION, restrictedSkuList);	
			this.setRestrictedSku(restrictedSkuList);
		}*/
		if(!bopusSkuList.isEmpty()){
			sessionBean.getValues().put(BBBInternationalShippingConstants.BOPUS_SKU_LIST_IN_SESSION, bopusSkuList);	
			this.setBopusSku(bopusSkuList);
		}
		return isError;
	}

	/**
	 * Checks if commerce item is bopus item.
	 *
	 * @param cItem the c item
	 * @return true, if is bopus item 
	 */
	private boolean isBopusItem(final BBBCommerceItem cItem){
		final String bopusItem =  cItem.getStoreId();
		return BBBUtility.isNotEmpty(bopusItem) ? true:false;
		
	}


	public BBBPricingTools getPricingTools() {
		return pricingTools;
	}


	public void setPricingTools(BBBPricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}
	


	/** The promotionTools. */
	private PromotionTools promotionTools;
	
	/** The promotionTools. */
	private CartModifierFormHandler cartFormHandler;
	
	/**
	 * @return the cartFormHandler
	 */
	public CartModifierFormHandler getCartFormHandler() {
		return cartFormHandler;
	}



	/**
	 * @param cartFormHandler the cartFormHandler to set
	 */
	public void setCartFormHandler(CartModifierFormHandler cartFormHandler) {
		this.cartFormHandler = cartFormHandler;
	}


	/**
	 * @return the promotionTools
	 */
	public PromotionTools getPromotionTools() {
		return promotionTools;
	}


	/**
	 * @param promotionTools the promotionTools to set
	 */
	public void setPromotionTools(PromotionTools promotionTools) {
		this.promotionTools = promotionTools;
	}

	/**
     * This method removes the promotion or coupon associated to the profile and from the order.
     * @param pRequest
	 * @param pResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	@SuppressWarnings("unchecked")
		private void removeUserPromotion(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, TransactionDemarcation td,boolean shouldRollback) throws IOException, ServletException
	    {

		this.logDebug("Entering - InternationalShipFormHandler " +
	 				"Method Name [removeUserPromotion]"); 
			
			  	try{
	    		synchronized (this.getOrder()) {
				//Removing remaining explicit promotions
	    			td.begin(getOrderManager().getOrderTools().getTransactionManager());
	    		 final MutableRepositoryItem profile = (MutableRepositoryItem) this.getProfile();

	             final List<RepositoryItem> availablePromotions = (List<RepositoryItem>) profile.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);

	             this.logDebug("Inside - InternationalShipFormHandler Method Name [removeUserPromotion] : " +
	             		"For profile ::"+profile+ "and availablePromotions ::" + availablePromotions); 
	             
	             for (final Object element : availablePromotions) {
						// remove already granted promotion
	                 final RepositoryItem promotion = (RepositoryItem) element;
	                 this.getPromotionTools().removePromotion((MutableRepositoryItem) this.getProfile(), promotion,false);
	             }

	             // Setting order object promo code null on removal of promotions.
	             availablePromotions.clear();
				
	             profile.setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
	             this.getPromotionTools().initializePricingModels(pRequest, pResponse);
	             
	             //Repricing the order
	             this.runRepricingProcess(pRequest, pResponse, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING);
				           
	             this.getOrderManager().updateOrder(this.getOrder());
	             
	             this.logDebug("Exit - InternationalShipFormHandler " +
	     				"Method Name [removeUserPromotion]"); 
	    		}
	        } catch (CommerceException e) {
	        	shouldRollback = true;
				//e.printStackTrace();
				logError(BBBCoreErrorConstants.CART_ERROR_1021
	                    + ": Commerce exception while updating Order after removing promotions", e);
			}   
		    catch (final RunProcessException e) {
		    	shouldRollback = true;
	            this.logError(LogMessageFormatter.formatMessage(pRequest, "Exception occurred while repricing coupons",
                            BBBCoreErrorConstants.CART_ERROR_1023), e);
		    }
	    	catch (TransactionDemarcationException exe) {
				shouldRollback = true;
				pRequest.getSession().setAttribute(BBBInternationalShippingConstants.ENVOY_ERROR,"true");
				vlogError("Inside Exception: Exception occured while getting Transaction" + exe);
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
						ERR_INTL_SHIP_RESPONSE_ERROR, pRequest.getLocale().getLanguage(),
						null, null),ERR_INTL_SHIP_RESPONSE_ERROR));
			}
	    	finally
			{
				try {
					td.end(shouldRollback);
				} catch (final TransactionDemarcationException tde) {
					this.logError("Transaction roll back error", tde);
				}
			}
       
	    }
	 
		 /** Repricing the Order.
	     *
	     * @param pRequest a <code>DynamoHttpServletRequest</code> value
	     * @param pResponse a <code>DynamoHttpServletResponse</code> value
	     * @param pOperationType a <code>Operation type</code> value
	     * @param object
	     * @exception RunProcessException if an error occurs
	     * @exception ServletException if an error occurs
	     * @exception IOException if an error occurs */
		 
	 	private final void runRepricingProcess(final DynamoHttpServletRequest pRequest,
				             final DynamoHttpServletResponse pResponse, final String pOperationType)
				             throws RunProcessException, ServletException, IOException {
				 if (getCartFormHandler().getRepriceOrderChainId() == null) {
				     return;
				 }
				
				 final HashMap<String, Object> params = new HashMap<String, Object>();
				 params.put(PricingConstants.PRICING_OPERATION_PARAM, pOperationType);
				 params.put(PricingConstants.ORDER_PARAM, this.getOrder());
				 //this.getOrder().getPriceInfo().getShipping();
				 params.put(PricingConstants.PRICING_MODELS_PARAM, getCartFormHandler().getUserPricingModels());
				 params.put(PricingConstants.LOCALE_PARAM, getCartFormHandler().getUserLocale());
				 params.put(PricingConstants.PROFILE_PARAM, this.getProfile());
				 params.put(PipelineConstants.ORDERMANAGER, this.getOrderManager());
				 getOrderManager().getPipelineManager().runProcess(getCartFormHandler().getRepriceOrderChainId(), params);
				 
				 //getShipGroupFormHandler().repriceOrder(pRequest,  pResponse);
			}
	 	
	 	/**
		 * Handle currency country selector.the form handler sets cookie to store country and currency
		 * @param pRequest the request
		 * @param pResponse the response
		 * @return true, if successful
		 * @throws ServletException the servlet exception
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public final boolean handleUpdateUserContext(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
				throws ServletException,IOException {
			this.logDebug("Entering - InternationalShipFormHandler " +
	 				"Method Name [handleUpdateUserContext]");
			Transaction tr = null;
			
			String redirectUrl= pRequest.getHeader(BBBCoreConstants.REFERRER);
			if (BBBUtility.isNotEmpty(redirectUrl) && !redirectUrl.contains(BBBCoreConstants.CONTEXT_STORE))
			{
				redirectUrl=pRequest.getScheme() + BBBCoreConstants.CONSTANT_SLASH +pRequest.getServerName()+BBBCoreConstants.CONTEXT_STORE;
			}
			
			this.setSuccessURL(redirectUrl);
			this.setErrorURL(redirectUrl);
			String previousCountry = (String) getSessionBean().getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
			
			
			if(!StringUtils.isEmpty(getUserSelectedCountry()) && !StringUtils.isEmpty( getUserSelectedCurrency())){
				
				try {
				BBBUtility.addCookie(pResponse, this.getTools().setInternationalShipCookie(getUserSelectedCountry(), getUserSelectedCurrency(), pRequest), false);
					BBBInternationalContextVO contextVO = getTools().getInternationalShippingBuilder().buildContextBasedOnCountryCode(getUserSelectedCountry());
					this.logDebug("Setting user profile and session with country: " + getUserSelectedCountry() + " and currency: " + getUserSelectedCurrency());
					getSessionBean().getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, getUserSelectedCountry());
					getSessionBean().getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, getUserSelectedCurrency());
					getSessionBean().setInternationalShippingContext(!getUserSelectedCountry().equals(getUsCountryCode()));
					((MutableRepositoryItem) this.getProfile()).setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, getUserSelectedCountry());
					((MutableRepositoryItem) this.getProfile()).setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, getUserSelectedCurrency());
					((MutableRepositoryItem) this.getProfile()).setPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT,
							!getUserSelectedCountry().equals(getUsCountryCode()));
					if (contextVO != null && contextVO.getShippingLocation() != null) {
						this.setIntContextVO(contextVO);
						getSessionBean().getValues().put(BBBInternationalShippingConstants.KEY_FOR_SHIPPING_ENABLED, contextVO.getShippingLocation().isShippingEnabled());
					}
					boolean updateOrderRequired = (!BBBUtility.isEmpty(previousCountry) && 
							(previousCountry.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) || getUserSelectedCountry().equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)) && !previousCountry.equalsIgnoreCase(getUserSelectedCountry()));
					tr = this.ensureTransaction();
					synchronized (this.getOrder()) {
						this.logDebug("Setting New Price List into user profile and calling Reprice Order");
						this.getPriceListProfilePropertySetter().setProperties((Profile) getProfile(), pRequest, pResponse);
						HashMap<String, Object> params = new HashMap<String, Object>();
					    params.put(BBBCheckoutConstants.REPRICE_SHOPPING_CART_ORDER, this.getOrder());
					    try {
//							pipelineResult = this.runRepricingProcess(pRequest, pResponse, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING);
					    	getPricingTools().performPricingOperation(((BBBProfileTools)getProfileFormHandler().getProfileTools()).getRepriceOrderPricingOp(), 
								this.getOrder(), getProfileFormHandler().getUserPricingModels(), 
									((BBBProfileTools)getProfileFormHandler().getProfileTools()).getUserLocale(pRequest, pResponse), getProfile(), params);
						if (updateOrderRequired) {
								this.logDebug("Updating Previous prices of Items in cart in case of new Price List in Profile");
							getPricingTools().updatePreviousPricesInCommerceItems(this.getOrder());
							}
					    } catch (PricingException pe) {
					    	if (isLoggingError()) {
								logError(pe.getMessage());
							}
					    	this.logDebug("Some error occured while Repricing of Order, removing all the commerce items from Order : " + this.getOrder().getId());
					    	this.getOrderManager().getCommerceItemManager().removeAllCommerceItemsFromOrder(this.getOrder());
						}
					    if (updateOrderRequired) {
					    	this.logDebug("Updating Previous prices of Items Wish list in case of new Price List in profile");
							getPricingTools().updatePreviousPricesInSavedItems(pRequest,this.getGiftlistManager(), this.getProfile());
					    }

                    	List<CommerceItem> commerceItemList=(List<CommerceItem>) getOrder().getCommerceItems();
                    	for(CommerceItem commerceItem:commerceItemList){
                			 BaseCommerceItemImpl cItemimpl = (BaseCommerceItemImpl) commerceItem;
                			 if(cItemimpl.isPorchService()){
                				 try {
                						cItemimpl.setPorchServiceRef(null);
                						cItemimpl.setPorchService(false);
                					
                					} catch (Exception e) {
                						if(isLoggingError()){ 
                						logDebug(" error while removing proch service ref from commerce item for international shipping"+e,e);
                						}
                					
                					}
                				
                			 }
                		 }
                    	
                    
							this.getOrderManager().updateOrder(this.getOrder());
	
					}
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(null, "BBBSystemException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
					markTransactionRollback();
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
					markTransactionRollback();
				} catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(null, "RepositoryException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
					markTransactionRollback();
				} catch (CommerceException e) {
					logError(LogMessageFormatter.formatMessage(null, "CommerceException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
					markTransactionRollback();
				} finally{
					if(isLoggingDebug()){
						logDebug("committing the transaction if no exception occured");
					}
					if (tr!= null) {
						this.commitTransaction(tr);
					}
				}
			}
			postUpdateUserContext(pRequest, pResponse, previousCountry);
			this.logDebug("Exit - InternationalShipFormHandler " + " inside method [handleUpdateUserContext]"); 
			return this.checkFormRedirect(this.successURL, this.errorURL, pRequest, pResponse);
		}
		
		
		public void repriceReffererOrder(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse,String previousCountry) throws IOException, ServletException
		{
			Transaction tr = null;
			try{
				tr = this.ensureTransaction();
				synchronized (this.getOrder()) {
					this.logDebug("Setting New Price List into user profile and calling Reprice Order");
					this.getPriceListProfilePropertySetter().setProperties((Profile) getProfile(), pRequest, pResponse);
					this.runRepricingProcess(pRequest, pResponse, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING);
					this.logDebug("Updating Previous prices of Items in cart and Wish list");
					if(BBBUtility.isNotEmpty(previousCountry) && previousCountry.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)){
						getPricingTools().updatePreviousPricesInCommerceItems(this.getOrder());
						getPricingTools().updatePreviousPricesInSavedItems(pRequest,this.getGiftlistManager(), this.getProfile());
					}
					this.getOrderManager().updateOrder(this.getOrder());
				}
			}  catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
				markTransactionRollback();
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
				markTransactionRollback();
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
				markTransactionRollback();
			} catch (RunProcessException e) {
				logError(LogMessageFormatter.formatMessage(null, "RunProcessException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
				markTransactionRollback();
			} catch (CommerceException e) {
				logError(LogMessageFormatter.formatMessage(null, "CommerceException from handleUpdateUserContext of InternationalShipFormHandler :"), e);
				markTransactionRollback();
			} finally{
				this.logInfo("committing the transaction if no exception occured");
				if (tr!= null) {
					this.commitTransaction(tr);
				}
			}
		}
		
		public final void postUpdateUserContext(final DynamoHttpServletRequest pRequest,
                final DynamoHttpServletResponse pResponse, String previousCountry) throws ServletException, IOException {
			String requestUrl = pRequest.getRequestURI();
			for(String urlChar : urlPattern) 
			{
				if(requestUrl.contains(urlChar)) {
					
					if(((previousCountry==null || 
							previousCountry.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY)) && !getUserSelectedCountry().equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY)) || 
							(!previousCountry.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY) && getUserSelectedCountry().equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY)) || 
							(previousCountry.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) && !getUserSelectedCountry().equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)) || 
							(!previousCountry.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) && getUserSelectedCountry().equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)))
		            {
						pRequest.addQueryParameter(BBBCoreConstants.CLEAR_FILTERS, BBBCoreConstants.TRUE);
					}
					break;
				}
					
			}
			if(pRequest.getHeader(BBBCoreConstants.REFERRER)!=null && pRequest.getHeader(BBBCoreConstants.REFERRER).contains(BBBInternationalShippingConstants.MCID_GOOGLE))
			{
				pRequest.addQueryParameter(BBBInternationalShippingConstants.RESET_GOOGLE_ADDFLOW, BBBCoreConstants.TRUE);
		}
			pRequest.getSession().removeAttribute(BBBInternationalShippingConstants.GOOGLE_FLOW);
		}
		
		private void markTransactionRollback() {
			if (!this.isTransactionMarkedAsRollBack()) {
				try {
					this.setTransactionToRollbackOnly();
		        } catch (final SystemException e) {
		        	this.logError(" Error in marking the transaction rollback", e);
		        }
		    }
		}
		
		public List<String> getUrlPattern() {
			return urlPattern;
		}


		public void setUrlPattern(List<String> urlPattern) {
			this.urlPattern = urlPattern;
		}
		
		@Override
	    public void logDebug(final String pMessage) {
	        if (this.isLoggingDebug()) {
	            this.logDebug(pMessage, null);
	        }
	    }
		
		public void logInfo(final String pMessage) {
	        if (this.isLoggingInfo()) {
	            this.logInfo(pMessage, null);
	        }
	    }


		public String getExceptionString() {
			return exceptionString;
		}


		public void setExceptionString(String exceptionString) {
			this.exceptionString = exceptionString;
		}


		public String getExceptionCode() {
			return exceptionCode;
		}


		public void setExceptionCode(String exceptionCode) {
			this.exceptionCode = exceptionCode;
		}

		 
}

