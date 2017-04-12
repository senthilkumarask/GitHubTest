
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

package com.bbb.commerce.order.paypal;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import org.apache.commons.lang.StringEscapeUtils;

import com.bbb.account.api.BBBAddressVO;
import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.checkout.util.BBBOrderUtilty;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.PriceInfoDisplayVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.order.manager.PromotionLookupManager;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.commerce.pricing.droplet.BBBPriceDisplayDroplet;
import com.bbb.commerce.vo.PayPalInputVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestBase;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.webservices.BBBWebservicesConfig;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.paypal.BBBBasicAmountType;
import com.bbb.paypal.BBBDoAuthorizationReq;
import com.bbb.paypal.BBBDoAuthorizationRequestType;
import com.bbb.paypal.BBBDoAuthorizationResVO;
import com.bbb.paypal.BBBDoExpressCheckoutPaymentReq;
import com.bbb.paypal.BBBDoExpressCheckoutPaymentRequest;
import com.bbb.paypal.BBBDoExpressCheckoutPaymentRequestDetailType;
import com.bbb.paypal.BBBDoExpressCheckoutPaymentResVO;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsReq;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsRequestType;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.paypal.BBBPayPalCredentials;
import com.bbb.paypal.BBBSetExpressCheckoutReq;
import com.bbb.paypal.BBBSetExpressCheckoutRequestDetailsType;
import com.bbb.paypal.BBBSetExpressCheckoutRequestType;
import com.bbb.paypal.BBBSetExpressCheckoutResVO;
import com.bbb.paypal.PayPalProdDescVO;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;


/**
 * This Manager class sets paypal credentials and get token from
 *         Paypal after making webservice call.
 * @author smalho 
 * 
 */

public class BBBPayPalServiceManager extends BBBGenericService {
	private static final String DEFAULT_LOCALE = "EN";
	private static final String LBL_FROM = "lbl_cart_registry_from_text";
	private static final String LBL_SUFIX = "lbl_cart_registry_name_suffix";
	private static final String DATE_FORMATER = "MM/dd/yyyy HH:mm:ss";
	String VALIDATION_FAILED = "VALIDATION_FAILED";
	String PAYPAL_GET_SERVICE_FAILED = "PAYPAL_GET_SERVICE_FAILED";
	
	private String cancelUrl;
	private String returnUrl;
	private BBBCatalogTools catalogTools;
	private BBBWebservicesConfig bbbWebServicesConfig;
	private String payPalCredConfig;
	private String setExpressCheckoutService;
	private String getExpressCheckoutService;
	private OrderDetailsManager orderDetailsManager;
	private BBBCheckoutManager checkoutMgr;
	private GiftRegistryManager giftRegistryManager;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBPurchaseProcessHelper shippingHelper;
	private Map<String,String> addressVerifyRedirectUrl;
	private BBBShippingGroupManager shippingManager;
	private BBBOrderManager orderManager;
	private BBBPriceDisplayDroplet priceDisplayDroplet;
	private BBBPaymentGroupManager paymentGroupManager;
	private ClientLockManager localLockManager;
	private TransactionManager transactionManager;
	private String doExpressCheckoutService;
	private String doAuthorization;
	private boolean isEnableTesting;
	private boolean isTokenExpTest;
	private ShippingGroupManager shippingGroupManager;
	private Repository siteRepository;
	private BBBPricingTools mPricingTools;
	private MutableRepository catalogRepository;
	private BBBCommerceItemManager commerceItemManager;
	private PromotionLookupManager promoManager;
	
	/**
	 * @return the promoManager
	 */
	public PromotionLookupManager getPromoManager() {
		return this.promoManager;
	}

	/**
	 * @param promoManager the promoManager to set
	 */
	public void setPromoManager(PromotionLookupManager promoManager) {
		this.promoManager = promoManager;
	}

	/**
	 * @return the commerceItemManager
	 */
	public BBBCommerceItemManager getCommerceItemManager() {
		return this.commerceItemManager;
	}

	/**
	 * @param commerceItemManager the commerceItemManager to set
	 */
	public void setCommerceItemManager(BBBCommerceItemManager commerceItemManager) {
		this.commerceItemManager = commerceItemManager;
	}

	public MutableRepository getCatalogRepository() {
		return this.catalogRepository;
	}

	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public BBBPricingTools getPricingTools() {
		return this.mPricingTools;
	}

	public void setPricingTools(BBBPricingTools mPricingTools) {
		this.mPricingTools = mPricingTools;
	}

	/**
	 * @return the siteRepository
	 */
	public Repository getSiteRepository() {
		return this.siteRepository;
	}

	/**
	 * @param siteRepository the siteRepository to set
	 */
	public void setSiteRepository(Repository siteRepository) {
		this.siteRepository = siteRepository;
	}
	
	public ShippingGroupManager getShippingGroupManager() {
		return this.shippingGroupManager;
	}

	public void setShippingGroupManager(ShippingGroupManager shippingGroupManager) {
		this.shippingGroupManager = shippingGroupManager;
	}
	/**
	 * @return the isTokenExpTest
	 */
	public boolean isTokenExpTest() {
		return this.isTokenExpTest;
	}

	/**
	 * @param isTokenExpTest the isTokenExpTest to set
	 */
	public void setTokenExpTest(boolean isTokenExpTest) {
		this.isTokenExpTest = isTokenExpTest;
	}

	/**
	 * @return the doAuthorization
	 */
	public String getDoAuthorization() {
		return this.doAuthorization;
	}

	/**
	 * @param doAuthorization the doAuthorization to set
	 */
	public void setDoAuthorization(String doAuthorization) {
		this.doAuthorization = doAuthorization;
	}

	/**
	 * @return the doExpressCheckoutService
	 */
	public String getDoExpressCheckoutService() {
		return this.doExpressCheckoutService;
	}

	/**
	 * @return the isEnableTesting
	 */
	public boolean isEnableTesting() {
		return this.isEnableTesting;
	}

	/**
	 * @param isEnableTesting the isEnableTesting to set
	 */
	public void setEnableTesting(boolean isEnableTesting) {
		this.isEnableTesting = isEnableTesting;
	}
	/**
	 * @param doExpressCheckoutService the doExpressCheckoutService to set
	 */
	public void setDoExpressCheckoutService(String doExpressCheckoutService) {
		this.doExpressCheckoutService = doExpressCheckoutService;
	}
	
	
	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return the localLockManager
	 */
	public ClientLockManager getLocalLockManager() {
		return this.localLockManager;
	}

	/**
	 * @param localLockManager the localLockManager to set
	 */
	public void setLocalLockManager(ClientLockManager localLockManager) {
		this.localLockManager = localLockManager;
	}

	/**
	 * @return the checkoutMgr
	 */
	public BBBCheckoutManager getCheckoutMgr() {
		return this.checkoutMgr;
	}

	/**
	 * @param checkoutMgr the checkoutMgr to set
	 */
	public void setCheckoutMgr(BBBCheckoutManager checkoutMgr) {
		this.checkoutMgr = checkoutMgr;
	}

	/**
	 * @return the shippingManager
	 */
	public BBBShippingGroupManager getShippingManager() {
		return this.shippingManager;
	}

	/**
	 * @param shippingManager the shippingManager to set
	 */
	public void setShippingManager(BBBShippingGroupManager shippingManager) {
		this.shippingManager = shippingManager;
	}

	/**
	 * @return the orderManager
	 */
	public BBBOrderManager getOrderManager() {
		return this.orderManager;
	}

	/**
	 * @param orderManager the orderManager to set
	 */
	public void setOrderManager(BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	/**
	 * @return the priceDisplayDroplet
	 */
	public BBBPriceDisplayDroplet getPriceDisplayDroplet() {
		return this.priceDisplayDroplet;
	}

	/**
	 * @param priceDisplayDroplet the priceDisplayDroplet to set
	 */
	public void setPriceDisplayDroplet(BBBPriceDisplayDroplet priceDisplayDroplet) {
		this.priceDisplayDroplet = priceDisplayDroplet;
	}

	/**
	 * @return the paymentGroupManager
	 */
	public BBBPaymentGroupManager getPaymentGroupManager() {
		return this.paymentGroupManager;
	}

	/**
	 * @param paymentGroupManager the paymentGroupManager to set
	 */
	public void setPaymentGroupManager(BBBPaymentGroupManager paymentGroupManager) {
		this.paymentGroupManager = paymentGroupManager;
	}

	/**
	 * @return addressVerifyRedirectUrl
	 */
	public Map<String, String> getAddressVerifyRedirectUrl() {
		return this.addressVerifyRedirectUrl;
	}

	/**
	 * @param addressVerifyRedirectUrl to set addressVerifyRedirectUrl
	 */
	public void setAddressVerifyRedirectUrl(
			Map<String, String> addressVerifyRedirectUrl) {
		this.addressVerifyRedirectUrl = addressVerifyRedirectUrl;
	}

	/**
	 * @return shippingHelper
	 */
	public BBBPurchaseProcessHelper getShippingHelper() {
		return this.shippingHelper;
	}

	/**
	 * @param shippingHelper to set shippingHelper
	 */
	public void setShippingHelper(BBBPurchaseProcessHelper shippingHelper) {
		this.shippingHelper = shippingHelper;
	}

	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}

	/**
	 * @param lblTxtTemplateManager
	 *            the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return this.giftRegistryManager;
	}

	/**
	 * @param giftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager giftRegistryManager) {
		this.giftRegistryManager = giftRegistryManager;
	}

	/**
	 * @return the orderDetailsManager
	 */
	public OrderDetailsManager getOrderDetailsManager() {
		return this.orderDetailsManager;
	}

	/**
	 * @param orderDetailsManager
	 *            the orderDetailsManager to set
	 */
	public void setOrderDetailsManager(OrderDetailsManager orderDetailsManager) {
		this.orderDetailsManager = orderDetailsManager;
	}

	/**
	 * @return the getExpressCheckoutService
	 */
	public String getGetExpressCheckoutService() {
		return this.getExpressCheckoutService;
	}

	/**
	 * @param getExpressCheckoutService
	 *            the getExpressCheckoutService to set
	 */
	public void setGetExpressCheckoutService(String getExpressCheckoutService) {
		this.getExpressCheckoutService = getExpressCheckoutService;
	}

	/**
	 * @return the setExpressCheckoutService
	 */
	public String getSetExpressCheckoutService() {
		return this.setExpressCheckoutService;
	}

	/**
	 * @param setExpressCheckoutService
	 *            the setExpressCheckoutService to set
	 */
	public void setSetExpressCheckoutService(String setExpressCheckoutService) {
		this.setExpressCheckoutService = setExpressCheckoutService;
	}

	/**
	 * @return the payPalCredConfig
	 */
	public String getPayPalCredConfig() {
		return this.payPalCredConfig;
	}

	/**
	 * @param payPalCredConfig
	 *            the payPalCredConfig to set
	 */
	public void setPayPalCredConfig(String payPalCredConfig) {
		this.payPalCredConfig = payPalCredConfig;
	}

	/**
	 * @return the bbbWebServicesConfig
	 */
	public BBBWebservicesConfig getBbbWebServicesConfig() {
		return this.bbbWebServicesConfig;
	}

	/**
	 * @param bbbWebServicesConfig
	 *            the bbbWebServicesConfig to set
	 */
	public void setBbbWebServicesConfig(BBBWebservicesConfig bbbWebServicesConfig) {
		this.bbbWebServicesConfig = bbbWebServicesConfig;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return
	 */
	public String getCancelUrl() {
		return this.cancelUrl;
	}

	/**
	 * @param cancelUrl
	 */
	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	/**
	 * @return
	 */
	public String getReturnUrl() {
		return this.returnUrl;
	}

	/**
	 * @param returnUrl
	 */
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	/**
	 * 
	 * @param order
	 * @return This method gets the token from Paypal after webservice call
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	@SuppressWarnings({ "boxing", "unchecked" })
	public BBBSetExpressCheckoutResVO doSetExpressCheckOut(Order pOrder, String pCancelURL, String pReturnURL, BBBPayPalSessionBean payPalSessionBean, Profile profile) throws BBBBusinessException, BBBSystemException{
		logDebug("Entry getTokenFromPaypal of BBBPayPalService with order:" + pOrder +  " - pCancelURL" + pCancelURL + " - pReturnURL" + pReturnURL);
		
		// *************************Initialize BBBReq VO for PayPal Service***********
		BBBSetExpressCheckoutRequestDetailsType detail = null;
		BBBSetExpressCheckoutRequestType checkoutRequest = null;
		BBBSetExpressCheckoutResVO resType = null;
		BBBSetExpressCheckoutReq setExpReq = null;

		// ******************BBBSetExpressCheckoutRequestDetailsType VO*************************
		detail = new BBBSetExpressCheckoutRequestDetailsType();

		// ******************Set Cancel and Return URL*************************
		detail.setCancelURL(pCancelURL);			
		detail.setReturnURL(pReturnURL);
		
		// ******************Set Basic Amount*************************
		BBBBasicAmountType orderTotal = new BBBBasicAmountType();
		orderTotal.setValue(((Double) pOrder.getPriceInfo().getAmount()).toString());
		orderTotal.setCurrencyID(pOrder.getPriceInfo().getCurrencyCode());
		detail.setOrderTotal(orderTotal);

		checkoutRequest = new BBBSetExpressCheckoutRequestType();
		checkoutRequest.setSetExpressCheckoutRequestDetails(detail);

		// ******************Create Main Req VO*************************
		setExpReq = new BBBSetExpressCheckoutReq();
		setExpReq.setOrder((BBBOrderImpl) pOrder);
		setExpReq.setSetExpressCheckoutRequest(checkoutRequest);
		setExpReq.setServiceName(this.setExpressCheckoutService);
			
		// *************************Set payment group id with highest tickting group*************************
		
		double maxAmount = 0.0d;
		String higTicketSGid = null;
		List<ShippingGroup> shpGrp = pOrder.getShippingGroups();
		for (ShippingGroup grp : shpGrp) {
			if (grp instanceof BBBHardGoodShippingGroup && StringUtils.isBlank(((BBBHardGoodShippingGroup) grp).getRegistryInfo())) {
				BBBHardGoodShippingGroup hrd = (BBBHardGoodShippingGroup) grp;
				List<CommerceItemRelationship> list = hrd.getCommerceItemRelationships();
				double amount = 0.0d;
				for(CommerceItemRelationship cmitem:list){
					amount = amount +  (cmitem.getQuantity() * cmitem.getCommerceItem().getPriceInfo().getAmount());						
				}
				if (amount > maxAmount) {
					higTicketSGid = hrd.getId();
					maxAmount = amount;
				}
				
			}
		}
		setExpReq.setShippingGroupId(higTicketSGid);
		logDebug("BBBPayPalService.doSetExpressCheckOut(): Highest Ticketing Shipping Group Id: " + higTicketSGid + " Max Amount: " + maxAmount);
	
		final String siteId = getSiteId();
		
		// *************************Set Order Information which will go to PayPal*************************
	
		final PriceInfoVO orderPriceInfo = getPricingTools().getOrderPriceInfo((BBBOrderImpl)pOrder);
		PriceInfoDisplayVO  priceInfoVO = populatePriceInfo(pOrder, siteId, orderPriceInfo);
		List<PaymentGroup> paymentGrp = getPaymentGroupManager().getPaymentGroups(pOrder, BBBCheckoutConstants.GIFTCARD);
		double gcPrice = getPaymentGroupManager().getAmountCoveredByGiftCard(paymentGrp, pOrder);
	
		//Paypal Story - R2.2.1 - Added for Bed Bath Canada. Show PST and GST/HST as Separate Line Items instead of Tax - Start
		double estimatedPST = 0.0;
		double estimatedGSTHST = 0.0;
		if(BBBUtility.isNotEmpty(siteId) && 
				(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || 
				 siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) ) {
			estimatedPST = priceInfoVO.getShippingCountyLevelTax();
			estimatedGSTHST = priceInfoVO.getShippingStateLevelTax();
		}
		else{
			setExpReq.setOrderTax((Double.toString(priceInfoVO.getTotalTax())));
		}
		//Paypal Story - R2.2.1 - Added for Bed Bath Canada. Show PST and GST/HST as Separate Line Items instead of Tax - End
		
		//Start: Fix for RM 24794
		setExpReq.setOrderShippingCost(Double.toString(BBBOrderUtilty.convertToTwoDecimals(priceInfoVO.getRawShippingTotal()-priceInfoVO.getShippingSavings())));
		//End: Fix for RM 24794
		double orderTotalTemp = priceInfoVO.getTotalAmount() - gcPrice;
		setExpReq.setOrderTotal((Double.toString(orderTotalTemp)));
		setExpReq.setGiftWrapTotal((Double.toString(priceInfoVO.getGiftWrapTotal())));
		setExpReq.setSurcharge(Double.toString(BBBOrderUtilty.convertToTwoDecimals(priceInfoVO.getTotalSurcharge()-priceInfoVO.getSurchargeSavings())));
		setExpReq.setShippingDiscount(Double.toString(priceInfoVO.getTotalSavedAmount()));
		setExpReq.setGiftCardAmount(String.valueOf(gcPrice));
		setExpReq.setEhfAmount(Double.toString(priceInfoVO.getOnlineEcoFeeTotal()));
		setExpReq.setEstimatedPST(Double.toString(estimatedPST));
		setExpReq.setEstimatedGSTHST(Double.toString(estimatedGSTHST));
		// LTL Start
		setExpReq.setLtlAssemblyTotal(Double.toString(priceInfoVO.getTotalAssemblyFee()));
		if(priceInfoVO.isMaxDeliverySurchargeReached()) {
			setExpReq.setLtlDeliveryTotal(Double.toString(priceInfoVO.getMaxDeliverySurcharge()));
		} else {
			setExpReq.setLtlDeliveryTotal(Double.toString(priceInfoVO.getTotalDeliverySurcharge()));
		}
		// LTL End
		
		/*List<CommerceItem> commerceItemLists = pOrder.getCommerceItems();
		for (CommerceItem commerceItem : commerceItemLists) {
			if(!(commerceItem instanceof BBBCommerceItem)){
				continue;
			}
			BBBCommerceItem comItem = (BBBCommerceItem) commerceItem;
			PriceInfoVO priceInfo = getCommerceItemManager().getItemPriceInfo(comItem);
			if (commerceItem instanceof BBBCommerceItem) {
				if(priceInfo.getPriceBeans() != null && priceInfo.getPriceBeans().size() > 1){
					setExpReq.setShowItems(false);
					this.logDebug("Item is splitted in 2 variables(Promotions applied), so items will not get displayed on paypal site, isShowItems(): " + setExpReq.isShowItems());
					break;
				}
			}
		}*/
		LinkedHashMap<String, PayPalProdDescVO> map = null;
		this.logDebug("Populate items to be displayed on paypal site");
		map = populateOrderItemDetails((BBBOrderImpl)pOrder);

		setExpReq.setLocal(getCatalogTools().getSiteDetailFromSiteId(getSiteId()).getCountryCode());
		setExpReq.setItem(map);

		// *************************Get SetExpress Checkout Service WSDL*************************
		final String servicewsdl = (String) (getBbbWebServicesConfig().getServiceToWsdlMap()).get(this.setExpressCheckoutService);
		logDebug("Service WSDL = " + servicewsdl);
		BBBPayPalCredentials paypalCredentialsVO = getPayPalCred(servicewsdl);
		setExpReq.setCred(paypalCredentialsVO);
		
		payPalSessionBean.setGetExpCheckoutResponse(null);
		payPalSessionBean.setValidateOrderAddress(false);
		long start = System.currentTimeMillis();
		resType = (BBBSetExpressCheckoutResVO) invokeServiceMethod(setExpReq);
		long end = System.currentTimeMillis();
		
		logInfo("Total time taken to execute the Paypal service doSetExpressCheckOut =" + (end - start));
		if (resType != null) {
			profile.setPropertyValue(BBBCoreConstants.TOKEN, resType.getToken());
			logDebug("Ending  getTokenFromPaypal of BBBPayPalService with order with response :" + resType.getDetailVO());
		}
		return resType;

	}
	/**
	 * This method is used to create price info VO object 
	 * based on details in order
	 * @param pOrder
	 * @param siteId
	 * @param orderPriceInfo
	 * @return
	 */
	protected PriceInfoDisplayVO populatePriceInfo(Order pOrder, final String siteId,
			final PriceInfoVO orderPriceInfo) {
		return BBBOrderUtilty.populatePriceInfo(orderPriceInfo, siteId, (BBBOrderImpl)pOrder, null);
	}
	/**
	 * This method is used to
	 * return site Id from context manager
	 * 
	 * @return
	 */
	protected String getSiteId(){
		String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}
	/**
	 * This method gives the PayPal credential in form of VO
	 * 
	 * @param pServicewsdl
	 * @return BBBPayPalCredentials
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * 
	 */
	private BBBPayPalCredentials getPayPalCred(String pServicewsdl) throws BBBSystemException, BBBBusinessException {
		logDebug("Start  getPayPalCred of BBBPayPalService with  WSDL_service:" + pServicewsdl);
		BBBPayPalCredentials paypalCredentialsVO = null;
		if (pServicewsdl != null) {
			paypalCredentialsVO = new BBBPayPalCredentials();
			paypalCredentialsVO.setUserName(getCatalogTools().getAllValuesForKey(pServicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_LOGIN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL).get(BBBWebServiceConstants.ZERO));
			paypalCredentialsVO.setPassword(getCatalogTools().getAllValuesForKey(pServicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_PASSWORD + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL).get(BBBWebServiceConstants.ZERO));
			paypalCredentialsVO.setSignature(getCatalogTools().getAllValuesForKey(pServicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_SIGNATURE + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL).get(BBBWebServiceConstants.ZERO));
			paypalCredentialsVO.setVersion(getCatalogTools().getAllValuesForKey(pServicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_VERSION + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL).get(BBBWebServiceConstants.ZERO));
			paypalCredentialsVO.setAddressOverRide(Integer.valueOf((getCatalogTools().getAllValuesForKey(pServicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_ADDR_OR + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL).get(BBBWebServiceConstants.ZERO))));
			paypalCredentialsVO.setPaymentAction(getCatalogTools().getAllValuesForKey(pServicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_PAY_ACT + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL).get(BBBWebServiceConstants.ZERO));
			paypalCredentialsVO.setExpTime(Integer.valueOf(getCatalogTools().getAllValuesForKey(pServicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_WSTOKEN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_EXP_MINUTES).get(BBBWebServiceConstants.ZERO)));
			paypalCredentialsVO.setSubject(getCatalogTools().getAllValuesForKey(pServicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_SUBJECT + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL).get(BBBWebServiceConstants.ZERO));
			logDebug("************paypalCredentialsVO**************" + paypalCredentialsVO.getDetailVO());
		} else {
			// ********************Remove*****************
			logError("pServicewsdl is null");

		}

		logDebug("End  getPayPalCred of BBBPayPalService returns  paypalCredentialsVO :" + paypalCredentialsVO);
		return paypalCredentialsVO;
	}

	/**
	 * This getExpCheckoutDetails method fetch user info from PayPal based on token ID
	 * @param token
	 * @return voResp
	 * @throws CommerceException 
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public BBBGetExpressCheckoutDetailsResVO getExpCheckoutDetails(String token,Order pOrder, Profile profile, BBBAddressContainer addressContainer) throws CommerceException, BBBSystemException, BBBBusinessException{
		logDebug("Start  getExpCheckoutDetails of BBBPayPalService with token:" + token);
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		// *********************************Remove Exiting Credit Card Payment Group****************************
		// begin Transaction & getting lock on profile
		ClientLockManager lockManager = getLocalLockManager();
		boolean acquireLock = false;
		boolean shouldRollback = false;
		TransactionDemarcation td = new TransactionDemarcation();
		// *****************************************************************************************************

		BBBGetExpressCheckoutDetailsRequestType detail = new BBBGetExpressCheckoutDetailsRequestType();
		BBBGetExpressCheckoutDetailsResVO voResp = null;

		detail.setToken(token);
		BBBGetExpressCheckoutDetailsReq req = new BBBGetExpressCheckoutDetailsReq();
		req.setGetGetExpressCheckoutDetailsRequest(detail);
		req.setServiceName(getGetExpressCheckoutService());
		try {
			acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
			if (acquireLock) {
				lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
			}

			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

			synchronized (pOrder) {
				
				final String servicewsdl = (String) (getBbbWebServicesConfig().getServiceToWsdlMap()).get(getGetExpressCheckoutService());
				BBBPayPalCredentials paypalCredentialsVO = getPayPalCred(servicewsdl);
				req.setCred(paypalCredentialsVO);
				voResp = (BBBGetExpressCheckoutDetailsResVO) invokeServiceMethod(req);
				long start = System.currentTimeMillis();
				voResp = (BBBGetExpressCheckoutDetailsResVO) invokeServiceMethod(req);
				long end = System.currentTimeMillis();
				
				logInfo("Total time taken to execute the Paypal service getExpCheckoutDetails =" + (end - start));

				if(voResp != null && !voResp.getErrorStatus().isErrorExists()) {
					validateDetails(voResp);
					//Added RM Defect : 24732 Start -Pass State/Province Code instead of Name
					substituteStateCode(voResp.getShippingAddress());
					substituteStateCode(voResp.getBillingAddress());
					//RM Defect: 24732 END
					if((null != voResp.getShippingAddress() && null == voResp.getShippingAddress().getState()) 
							|| (null != voResp.getBillingAddress() && null == voResp.getBillingAddress().getState())) {
						logInfo("Inside getExpCheckoutDetails : Shipping or Billing Address State is null : Checkout Details Response for Order : " +pOrder.getId() +
								" : " +voResp.toString());
					}
					updateTokenInOrder(token, ((BBBOrderImpl)pOrder), voResp.getPayerInfo().getPayerID(), profile);
					//update response info in order
					createPaymentGroup(voResp, ((BBBOrderImpl) pOrder), profile);
					saveCartInPaypalBean();
					logDebug("Setting Billing Address in order recieved from paypal");
					this.getPaymentGroupManager().setPayPalBillingAddress(voResp, (BBBOrderImpl) pOrder, addressContainer);
					logDebug("End  getExpCheckoutDetails of BBBPayPalService returns  voResp :" + voResp.toString());
				}
				else{
					throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_GET_SERVICE_FAIL, PAYPAL_GET_SERVICE_FAILED);
				}
			}
		} catch (DeadlockException e) {
			shouldRollback = true;
			this.logError("DeadlockException while getExpCheckoutDetails", e);
			throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null));
		} catch (TransactionDemarcationException e) {
			shouldRollback = true;
			this.logError("TransactionDemarcationException while getExpCheckoutDetails", e);
			throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null));
		} catch (CommerceException e){
			shouldRollback = true;
			logError("BBBPayPalServiceManager.getExpCheckoutDetails():: Commerce Exception while calling webservice GetExpressCheckoutDetails: " + e);
			throw new CommerceException(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG);
		} catch (BBBSystemException e) {
			shouldRollback = true;
			logError("BBBPayPalServiceManager.getExpCheckoutDetails():: System Exception while calling webservice GetExpressCheckoutDetails: " + e);
			if(e.getMessage() != null && e.getMessage().contains(PAYPAL_GET_SERVICE_FAILED)){
				throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_GET_SERVICE_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_GET_SERVICE_FAIL, "EN", null));
			}
			else{
				throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null));
			}
		} catch (BBBBusinessException e) {
			shouldRollback = true;
			logError("BBBPayPalServiceManager.getExpCheckoutDetails():: Business Exception while calling webservice GetExpressCheckoutDetails: " + e);
			if(e.getMessage() != null && e.getMessage().contains(VALIDATION_FAILED)){
				throw new BBBBusinessException(e.getErrorCode(), e.getErrorCode());
			}
			else{
				throw new BBBBusinessException(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null));
			}
		}
		finally {
			logDebug("Ends getExpCheckoutDetails of BBBPayPalService:");
			try {
				td.end(shouldRollback);
			} catch (TransactionDemarcationException e) {
				this.logError("TransactionDemarcationException while creating Rollback transaction", e);

			} finally {
				if (acquireLock)
					try {
						lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
					} catch (LockManagerException e) {
						this.logError("TransactionDemarcationException releasing lock on profile", e);

					}
			}
		}
		
		return voResp;
	}
	/**
	 * This method will return PayPal URL to be redirect from BBB 
	 * @return redirectURL
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getPayPalRedirectURL() throws BBBSystemException, BBBBusinessException {
		final String servicewsdl = (String) (getBbbWebServicesConfig().getServiceToWsdlMap()).get(this.setExpressCheckoutService);
		String redirectURL = getCatalogTools().getAllValuesForKey(servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_REDIRECT_URL + BBBWebServiceConstants.TXT_UNDERSCORE + this.setExpressCheckoutService).get(BBBWebServiceConstants.ZERO);
		logDebug("BBBPayPalServiceManager.getPayPalRedirectURL() returns redirectURL = " + redirectURL);
		return redirectURL;
	}
	
	
	/**
	 * Below Code will call PayPalservice manger to populate shipping address in shipping group
	 * @param pRequest
	 * @param pResponse
	 * @param voResp
	 * @param order
	 * @throws ServletException
	 * @throws IOException
	 */
	public void createShippingGroupPP(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrderImpl order, PayPalInputVO paypalInput) throws ServletException, IOException {
		logDebug("PayPalDroplet.createShippingGroupPP():: Start");
		boolean shipBillStatus;
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    	DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		try {
			shipBillStatus = createShippingGroup(voResp, order, paypalInput);
			request.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, voResp.getShippingAddress());
			if (shipBillStatus)
				request.serviceParameter(BBBCoreConstants.OUTPUT, request, response);
			else
				request.serviceParameter(BBBCoreConstants.ERROR, request, response);

		} catch (BBBBusinessException e) {
			logError("createShippingGroupPP() :: Business Exception while the calling createShippingGroupPP" + e);

		} catch (BBBSystemException e) {
			logError("createShippingGroupPP() :: System Exception while the calling createShippingGroupPP" + e);
		} 
		logDebug("PayPalDroplet.createBillingShipping():: Ends");
	}

	/**
	 * This method is called to create shipping group
	 * @param voResp
	 * @param pOrder
	 * @param pReq
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @return boolean
	 * 
	 */
	public boolean createShippingGroup(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrder pOrder, PayPalInputVO paypalInput) throws BBBSystemException, BBBBusinessException {
		logDebug("Starts createbillingShippingGroup of BBBPayPalService:");
		
		boolean shippingGroupSuccess = true;
		// begin Transaction & getting lock on profile
		ClientLockManager lockManager = getLocalLockManager();
		boolean acquireLock = false;
		boolean shouldRollback = false;
		TransactionDemarcation td = new TransactionDemarcation();
		Profile profile = paypalInput.getProfile();
		try {
			acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
			if (acquireLock) {
				lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
			}

			td.begin(getTransactionManager());

			synchronized (pOrder) {
				// Create shipping Group
				shippingGroupSuccess = populateShippingAddressPP(voResp, pOrder, paypalInput);
				
				return shippingGroupSuccess;
			}
		} catch (DeadlockException e) {
			shouldRollback = true;
			this.logError("DeadlockException while creating shipping and payment group", e);
			return false;
		} catch (TransactionDemarcationException e) {
			shouldRollback = true;
			this.logError("TransactionDemarcationException while creating shipping and payment group", e);
			return false;
		} catch (CommerceException e) {
			shouldRollback = true;
			this.logError("CommerceException while creating shipping and payment group", e);
			return false;
		} catch (RunProcessException e) {
			shouldRollback = true;
			this.logError("RunProcessException while creating shipping and payment group", e);
			return false;
		} finally {
			logDebug("Ends createbillingShippingGroup of BBBPayPalService:");
			try {
				td.end(shouldRollback);
			} catch (TransactionDemarcationException e) {
				this.logError("TransactionDemarcationException while creating Rollback transaction", e);
				return false;
			} finally {
				if (acquireLock)
					try {
						lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
					} catch (LockManagerException e) {
						this.logError("TransactionDemarcationException releasing lock on profile", e);
						return false;
					}
			}
		}
	}
	
	/**
	 * This method is called to create PayPal group
	 * @param voResp
	 * @param pOrder
	 * @param pReq
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @return boolean
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean createPaymentGroup(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrder pOrder, Profile profile) throws BBBSystemException, BBBBusinessException {
		boolean billingGroupSuccess = false;
		
		// begin Transaction & getting lock on profile
		final List<PaymentGroup> paymentList = pOrder.getPaymentGroups();
		for (final PaymentGroup paymentGroup : paymentList) {
			if (paymentGroup instanceof Paypal) {
				logError("PayPal Payment Gorup Already Exit");
				return true;
			}
		}
		billingGroupSuccess = getPaymentGroupManager().createPayPalPaymentGroup(voResp, pOrder, profile);

		if (billingGroupSuccess) {
			return true;
		} 
		return false;
		
	}


	/**
	 * This method creates shipping group if not present
	 * @param voResp
	 * @param pOrder
	 * @return boolean
	 * @throws CommerceException 
	 * @throws RunProcessException 
	 */
	@SuppressWarnings("unchecked")
	public boolean populateShippingAddressPP(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrder pOrder, PayPalInputVO paypalInput) throws CommerceException, RunProcessException {
		logDebug("BBBPayPalServiceManager.createShippingGroupPP() :: Start");
		BBBAddressPPVO addr = voResp.getShippingAddress();
		BBBHardGoodShippingGroup hgSG = null;
		boolean doPricing = false;
		Profile profile = paypalInput.getProfile();
		synchronized (pOrder) {
			try {
				List<ShippingGroup> shpGrp = pOrder.getShippingGroups();
				for (ShippingGroup fgrp : shpGrp) {
					hgSG = (BBBHardGoodShippingGroup) fgrp;
					
					// Creating shipping group if shipping group is empty
					if (hgSG.getShippingAddress().getCity() == null && hgSG.getShippingAddress().getAddress1() == null && hgSG.getShippingAddress().getCountry() == null) {
						
						logDebug("BBBPayPalServiceManager.createShippingGroup() :: shipping group id: " + hgSG.getId());
						hgSG.setShippingMethod(BBBCoreConstants.SHIP_METHOD_STANDARD_ID);
						BBBAddressContainer multiShipCont = (BBBAddressContainer)paypalInput.getMultiShipAddContainer();
						BBBAddressContainer shippingAddContainer = (BBBAddressContainer)paypalInput.getAddressCoontainer();
						//hgSG.getPriceInfo().setCurrencyCode(pOrder.getPriceInfo().getCurrencyCode());
						multiShipCont.addNewAddressToContainer(hgSG.getId(), addr);
						multiShipCont.setNewAddressKey(hgSG.getId());
						shippingAddContainer.addNewAddressToContainer(hgSG.getId(), addr);
						shippingAddContainer.setNewAddressKey(hgSG.getId());
						hgSG.setShippingAddress(addr);
						hgSG.setIsFromPaypal(true);
						hgSG.setAddressStatus(addr.getAddressStatus());
						
						List<CommerceItem> item = pOrder.getCommerceItems();
						if(!profile.isTransient()) {
							try {
								BBBAddressVO addrVO = new BBBAddressVO();
								BBBAddress addrBBB = copyPayPalAddress(addrVO, addr, pOrder.getSiteId(), profile);
								hgSG.setSourceId(addrBBB.getIdentifier());
							} catch (BBBSystemException e) {
								logError("BBBPayPalServiceManager.createShippingGroupPP() : BBBSystemException while saving address to user profile" + e);
							} catch (BBBBusinessException e) {
								logError("BBBPayPalServiceManager.createShippingGroupPP() : BBBBusinessException while saving address to user profile" + e);
							}
						} else {
							hgSG.setSourceId(hgSG.getId());
						}
						for (CommerceItem itemtemp : item) {
							if (itemtemp instanceof BBBCommerceItem) {
								// fetching shipping group relationship from Commerce Item
								BBBShippingGroupCommerceItemRelationship sgrl = (BBBShippingGroupCommerceItemRelationship) itemtemp.getShippingGroupRelationships().get(0);
								// Comparing item quantity to shipping group relationship quantity
								logDebug("BBBPayPalServiceManager.createShippingGroup() :: shipping group quantity: " + sgrl.getQuantity());
								if (itemtemp.getQuantity() != sgrl.getQuantity()) {
									sgrl.setQuantity(itemtemp.getQuantity());
									logDebug("BBBPayPalServiceManager.createShippingGroup() :: update shipping group quantity with item quantity: " + sgrl.getQuantity());
								}
							}
						}
						
						doPricing = true;
						// Checking only first occurrence of shipping group
						break;
					}
					logDebug("BBBPayPalServiceManager.createShippingGroupPP() :: Shipping group already exist");
					return true;
				}
				
				if (doPricing) {
					logDebug("BBBPayPalServiceManager.createShippingGroupPP() :: do pricing");
					getOrderManager().getCommerceItemManager().generateRangesForOrder(pOrder);
					TaxPriceInfo tpi = new TaxPriceInfo();
					tpi.setAmount(0.0);
					tpi.setCurrencyCode(getPricingCurrencyCode());
					pOrder.setTaxPriceInfo(tpi);
					this.orderManager.updateOrder(pOrder);
					// run engine
					getPaymentGroupManager().runPricingEngine(profile, pOrder, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL);					
				}
				
			} catch (final RunProcessException e) {
				this.logError(LogMessageFormatter.formatMessage(null, "RunProcessException while creating paypal shipping group", BBBCoreErrorConstants.PAYPAL_SHIPPING_GROUP_EXCEPTION), e);
				throw new RunProcessException(e);				
			} catch (CommerceException e1) {
				logDebug("error in creating shipping group");
				this.logError(BBBCoreErrorConstants.PAYPAL_SHIPPING_GROUP_EXCEPTION + ": commerceException", e1);
				throw new CommerceException(e1);
			}
		}
		logDebug("BBBPayPalServiceManager.createShippingGroup() :: End");
		return true;
	}

	
	/**
	 * This method validated Paypal details - Email, Phone, Payer Id
	 * 
	 * @param voResp
	 * @throws BBBBusinessException 
	 * @return void
	 */
	public void validateDetails(BBBGetExpressCheckoutDetailsResVO voResp) throws BBBBusinessException {
		logDebug("BBBPayPalServiceManager.validateDetails() :: Start");
		
		// Validation Phone number
		if (!BBBUtility.isEmpty(voResp.getContactPhone())){
			if(isInternationalBilling(voResp.getBillingAddress().getCountry())){
				if(!BBBUtility.isValidInternationalPhoneNumber(voResp.getContactPhone())){
					logError("validateDetails():: Phone Number is not valid: " + voResp.getContactPhone());
					throw new BBBBusinessException(BBBPayPalConstants.ERR_PAYPAL_PHONE_NOT_VALID, VALIDATION_FAILED);
				}
			}
			else if(!BBBUtility.isValidPhoneNumber(voResp.getContactPhone())) {
				logError("validateDetails():: Phone Number is not valid: " + voResp.getContactPhone());
				throw new BBBBusinessException(BBBPayPalConstants.ERR_PAYPAL_PHONE_NOT_VALID, VALIDATION_FAILED);
			}
		}
		if(voResp.getPayerInfo() != null){
			// Validation email Address
			if (!BBBUtility.isValidEmail(voResp.getPayerInfo().getPayerEmail())) {
				logError("validateDetails():: Email Address is not valid: " + voResp.getPayerInfo().getPayerEmail());
				throw new BBBBusinessException(BBBPayPalConstants.ERR_PAYPAL_EMAIL_NOT_VALID, VALIDATION_FAILED);
			}
			// Validating payer ID
			if (voResp.getPayerInfo().getPayerID() == null) {
				logError("validateDetails():: Payer Id is not valid: Null ");
				throw new BBBBusinessException(BBBPayPalConstants.ERR_PAYPAL_PAYER_ID_NOT_VALID, VALIDATION_FAILED);
			}
		}
		logDebug("BBBPayPalServiceManager.validateDetails() :: End");
	}
	
	/** 
	 * This method validates shipping method for item in the cart and returns key if expedite or express is changed
	 * @param pOrder
	 * @return boolean
	 * @throws BBBSystemException 
	 * @throws CommerceException 
	 */
	@SuppressWarnings("unchecked")
	public String validateShippingMethod(BBBOrder pOrder, Profile profile) throws BBBSystemException {
		ClientLockManager lockManager = getLocalLockManager();
		boolean acquireLock = false;
		boolean shouldRollback = false;
		TransactionDemarcation td = new TransactionDemarcation();
		try {
			acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
			if (acquireLock) {
				lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
			}

			td.begin(getTransactionManager());

			synchronized (pOrder) {
				boolean doPricing = false;
				BBBHardGoodShippingGroup hgSG = null;
				//List<CommerceItem> items = pOrder.getCommerceItems();
				List<ShippingGroup> shpGrp = pOrder.getShippingGroups();
				String shippingMethod = null;
				String shippingMethodChanged = null;
				
				String sddEligibleOn = getStringValue();
				
				for (ShippingGroup fgrp : shpGrp) {
					hgSG = (BBBHardGoodShippingGroup) fgrp;
					
					shippingMethod = hgSG.getShippingMethod();
					List<CommerceItemRelationship> cisg = hgSG.getCommerceItemRelationships();
					List<CommerceItem> ci = new ArrayList<CommerceItem>();
					for(CommerceItemRelationship item : cisg){
						if(item.getCommerceItem() instanceof BBBCommerceItem){
							ci.add(item.getCommerceItem());
						}
					}
					boolean flag = getCheckoutMgr().canItemShipByMethod(pOrder.getSiteId(), ci, shippingMethod);
					boolean sameDayDeliveryFlag = false;
			 		
			 		if(null != sddEligibleOn){
			 			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
			 		}
					if (!flag) {
						for (CommerceItem commerceItem : ci) {
							List<ShipMethodVO> skuMethodVO;

							skuMethodVO = this.getCatalogTools().getShippingMethodsForSku(pOrder.getSiteId(), commerceItem.getCatalogRefId(), sameDayDeliveryFlag);

							if (skuMethodVO == null) {
								this.logDebug("No skumethodvo not founds so this order is restricted");
								continue;
							}
							// Check if item available shipping method is equal
							// to
							// the shipping method of default HRDSHIP GRP
							boolean containShippingMethod = false;
							for (ShipMethodVO sMet : skuMethodVO) {
								if (sMet.getShipMethodId().equalsIgnoreCase(shippingMethod)) {
									containShippingMethod = true;
								}
							}
							if (!containShippingMethod) {
								fgrp.setShippingMethod(BBBCoreConstants.SHIP_METHOD_STANDARD_ID);
								doPricing = true;
								shippingMethodChanged = shippingMethod;
								logInfo("Shipping method changed for shipping group id - " + hgSG.getId() +  " Shipping Method - " + shippingMethodChanged);
								break;
							}
						}
					} 
					
				}

				if (doPricing) {
					getOrderManager().getCommerceItemManager().generateRangesForOrder(pOrder);
					TaxPriceInfo tpi = new TaxPriceInfo();
					tpi.setAmount(0.0);
					tpi.setCurrencyCode(getPricingCurrencyCode());
					pOrder.setTaxPriceInfo(tpi);
					this.orderManager.updateOrder(pOrder);
					getPaymentGroupManager().runPricingEngine(profile, pOrder, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL);
					return shippingMethodChanged;
				}
			}

		} catch (BBBSystemException e) {
			this.logError("BBBSystem Exception occured while validating shipping method ", e);
			shouldRollback = true;
			throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null));
		} catch (BBBBusinessException e) {
			this.logError("BBBBusinessException Exception occured while validating shipping method ", e);
			shouldRollback = true;
			throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null));
		} catch (CommerceException e1) {
			this.logError("CommerceException occured while validating shipping method ", e1);
			shouldRollback = true;
			throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null));
		} catch (final RunProcessException e) {
				shouldRollback = true;
				this.logError(LogMessageFormatter.formatMessage(null, "RunProcessException occured while validating shipping method", BBBCoreErrorConstants.PAYPAL_SHIPPING_GROUP_EXCEPTION), e);
				throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null));
		} catch (DeadlockException e) {
			shouldRollback = true;
			this.logError("DeadlockException  occured while validating shipping method", e);
			throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null));
		} catch (TransactionDemarcationException e) {
			shouldRollback = true;
			this.logError("TransactionDemarcationException  occured while validating shipping method", e);
			throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null));
		} finally {
			try {
				td.end(shouldRollback);
			} catch (TransactionDemarcationException e) {
				this.logError("TransactionDemarcationException  occured while validating shipping method", e);

			} finally {
				if (acquireLock)
					try {
						lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
					} catch (LockManagerException e) {
						this.logError("TransactionDemarcationException releasing lock on profile", e);

					}
			}
		}
		return null;
	}
	/**
	 * This method is used to return same day delivery flag value
	 * @return
	 */
	protected String getStringValue() {
		return BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
	}
	
	/**
	 * This method check if address exist in order or not
	 * 
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean addressInOrder(BBBOrderImpl order){
		
		this.logDebug("addressInOrder() :: Checking if address exists in order: Start" );
		boolean addressExist = false;
		if(order.getShippingGroupCount() > 1)
			addressExist = true;
		else{
			List<ShippingGroup> shpGrpList = order.getShippingGroups();
			for(ShippingGroup grp : shpGrpList){
				if(grp instanceof BBBHardGoodShippingGroup){
					BBBHardGoodShippingGroup hrd = (BBBHardGoodShippingGroup)grp;
					Address addrHrd = hrd.getShippingAddress();
					if((addrHrd.getCity() != null && addrHrd.getAddress1() !=null) || !StringUtils.isBlank(hrd.getRegistryInfo())){
						addressExist = true;
						this.logDebug("addressInOrder() :::  Address exist in order" );
						break;
					}
				}
			}
		}
		this.logDebug("addressInOrder() :::  End" );
		return addressExist;
	}
	
	/**
	 * This method check if address exist for commerce item or not
	 * 
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean ShippingCommerceRelationshipExist(BBBOrderImpl order){
		
		this.logDebug("ShippingCommerceRelationshipExist() :: Checking if address exists for commerce item: Start" );
		boolean relationExist = true;
		boolean isRegistryEqual = true;
		List<ShippingGroup> shpGrpList = order.getShippingGroups();
		final List<BBBAddress> allRegistryAddresses = getCheckoutMgr().getRegistryShippingAddress(order.getSiteId(), order);
		for(ShippingGroup grp : shpGrpList){
			if(grp instanceof BBBHardGoodShippingGroup){
				BBBHardGoodShippingGroup hrd = (BBBHardGoodShippingGroup)grp;
				Address addrHrd = hrd.getShippingAddress();
				if(hrd.getCommerceItemRelationshipCount() > 0 && ((addrHrd.getAddress1() == null) && addrHrd.getCity() == null && StringUtils.isBlank(hrd.getRegistryInfo()))){
					relationExist = false;
					this.logDebug("ShippingCommerceRelationshipExist() :::  Address does not exist for commerce item");
					break;
				}
				//check if registry in shipping group is available in order
				else {
					for(BBBAddress regAdd : allRegistryAddresses){
						if(!(regAdd.getId().equalsIgnoreCase(hrd.getRegistryId())) && !StringUtils.isEmpty(hrd.getRegistryId())){
							isRegistryEqual = false;
						}
						else{
							isRegistryEqual = true;
							break;
						}
					}
					if(!isRegistryEqual){
						relationExist=false;
					}
					
				}
			}
		}
		this.logDebug("ShippingCommerceRelationshipExist() :::  End" );
		return relationExist;
	}
	
	/**
	 * This doExpressCheckout method makes service call to PayPal
	 * @param pOrder
	 * @return doExpRes 
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("unchecked")
	public BBBDoExpressCheckoutPaymentResVO doExpressCheckout(BBBOrderImpl pOrder) throws BBBSystemException, BBBBusinessException{

		BBBDoExpressCheckoutPaymentReq doExpressReq = new BBBDoExpressCheckoutPaymentReq();
		BBBDoExpressCheckoutPaymentRequest doExpressRequest = new BBBDoExpressCheckoutPaymentRequest();
		BBBDoExpressCheckoutPaymentRequestDetailType doExpressRequestDetail = new BBBDoExpressCheckoutPaymentRequestDetailType();
		BBBDoExpressCheckoutPaymentResVO doExpRes = new BBBDoExpressCheckoutPaymentResVO();

		doExpressRequestDetail.setToken(pOrder.getToken());
		doExpressRequestDetail.setPayerId(pOrder.getPayerId());

		// ******************Set Basic Amount*************************
		BBBBasicAmountType orderTotal = new BBBBasicAmountType();
		orderTotal.setValue((Double.toString(pOrder.getPriceInfo().getAmount())));
		orderTotal.setCurrencyID(pOrder.getPriceInfo().getCurrencyCode());
		doExpressRequestDetail.setOrderTotal(orderTotal);

		doExpressRequest.setDoExpressCheckoutPaymentRequestDetailsType(doExpressRequestDetail);

		// *************************Set payment group id with highest tickting group*************************

				double maxAmount = 0.0d;
				String higTicketSGid = null;
				List<ShippingGroup> shpGrp = pOrder.getShippingGroups();
				for (ShippingGroup grp : shpGrp) {
					if (grp instanceof BBBHardGoodShippingGroup && StringUtils.isBlank(((BBBHardGoodShippingGroup) grp).getRegistryInfo())) {
						BBBHardGoodShippingGroup hrd = (BBBHardGoodShippingGroup) grp;
						List<CommerceItemRelationship> list = hrd.getCommerceItemRelationships();
						double amount = 0.0d;
						for (CommerceItemRelationship cmitem : list) {
							amount = amount + (cmitem.getQuantity() * cmitem.getCommerceItem().getPriceInfo().getAmount());
						}
						if (amount > maxAmount) {
							higTicketSGid = hrd.getId();
							maxAmount = amount;
						}

					}
				}
				doExpressReq.setShippingGroupId(higTicketSGid);
		// ******************Create Main Req VO*************************

		doExpressReq.setOrder(pOrder);
		doExpressReq.setDoExpressCheckoutPaymentRequest(doExpressRequest);
		doExpressReq.setServiceName(getDoExpressCheckoutService());

		final String siteId = getSiteId();
		
		// *************************Set Order Information which will go to PayPal*************************

		final PriceInfoVO orderPriceInfo = getPricingTools().getOrderPriceInfo(pOrder);
		PriceInfoDisplayVO  priceInfoVO = populatePriceInfo(pOrder, siteId, orderPriceInfo);
		List<PaymentGroup> paymentGrp = getPaymentGroupManager().getPaymentGroups(pOrder, BBBCheckoutConstants.GIFTCARD);
		double gcPrice = getPaymentGroupManager().getAmountCoveredByGiftCard(paymentGrp, pOrder);
		//Paypal Story - R2.2.1 - Added for Bed Bath Canada. Show PST and GST/HST as Separate Line Items instead of Tax - Start
		double estimatedPST = 0.0;
		double estimatedGSTHST = 0.0;
		if(BBBUtility.isNotEmpty(siteId) && 
				(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || 
				 siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) ) {
			estimatedPST = priceInfoVO.getShippingCountyLevelTax();
			estimatedGSTHST = priceInfoVO.getShippingStateLevelTax();
		}
		else{
			doExpressReq.setOrderTax((Double.toString(priceInfoVO.getTotalTax())));
		}
		//Paypal Story - R2.2.1 - Added for Bed Bath Canada. Show PST and GST/HST as Separate Line Items instead of Tax - End
		//Start: Fix for RM 24794
		doExpressReq.setOrderShippingCost(Double.toString(BBBOrderUtilty.convertToTwoDecimals(priceInfoVO.getRawShippingTotal()-priceInfoVO.getShippingSavings())));
		//End: Fix for RM 24794
		double orderTotalTemp = priceInfoVO.getTotalAmount() - gcPrice;
		doExpressReq.setOrderTotal((Double.toString(orderTotalTemp)));
		doExpressReq.setGiftWrapTotal((Double.toString(priceInfoVO.getGiftWrapTotal())));
		doExpressReq.setSurcharge(Double.toString(BBBOrderUtilty.convertToTwoDecimals(priceInfoVO.getTotalSurcharge()-priceInfoVO.getSurchargeSavings())));
		//Double orderShippingDiscount = Double.valueOf(priceInfoVO.getTotalSavedAmount());
		doExpressReq.setShippingDiscount(Double.toString(priceInfoVO.getTotalSavedAmount()));
		doExpressReq.setGiftCardAmount(String.valueOf(gcPrice));
		doExpressReq.setEhfAmount(Double.toString(priceInfoVO.getOnlineEcoFeeTotal()));
		doExpressReq.setEstimatedPST(Double.toString(estimatedPST));
		doExpressReq.setEstimatedGSTHST(Double.toString(estimatedGSTHST));
		doExpressReq.setLtlAssemblyTotal(Double.toString(priceInfoVO.getTotalAssemblyFee()));
		if(priceInfoVO.isMaxDeliverySurchargeReached()) {
			doExpressReq.setLtlDeliveryTotal(Double.toString(priceInfoVO.getMaxDeliverySurcharge()));
		} else {
			doExpressReq.setLtlDeliveryTotal(Double.toString(priceInfoVO.getTotalDeliverySurcharge()));
		}
		
		/*List<CommerceItem> commerceItemLists = pOrder.getCommerceItems();
		for (CommerceItem commerceItem : commerceItemLists) {
			if(!(commerceItem instanceof BBBCommerceItem)){
				continue;
			}
			BBBCommerceItem comItem = (BBBCommerceItem) commerceItem;
			PriceInfoVO priceInfo = getCommerceItemManager().getItemPriceInfo(comItem);
			if (commerceItem instanceof BBBCommerceItem) {
				if(priceInfo.getPriceBeans() != null && priceInfo.getPriceBeans().size() > 1){
					doExpressReq.setShowItems(false);
					this.logDebug("Item is splitted in 2 variables(Promotions applied), so items will not get displayed on paypal site, isShowItems(): " + doExpressReq.isShowItems());
					break;
				}
			}
		}*/
		Map<String, PayPalProdDescVO> map = null;
		this.logDebug("items to be displayed on paypal site");
		map = populateOrderItemDetails(pOrder);
		doExpressReq.setItem(map);

		final String servicewsdl = (String) (getBbbWebServicesConfig().getServiceToWsdlMap()).get(getDoExpressCheckoutService());
		BBBPayPalCredentials paypalCredentialsVO = getPayPalCred(servicewsdl);
		doExpressReq.setCred(paypalCredentialsVO);
		long start = System.currentTimeMillis();
		doExpRes = (BBBDoExpressCheckoutPaymentResVO) invokeServiceMethod(doExpressReq);
		long end = System.currentTimeMillis();
		
		logInfo("Total time taken to execute the Paypal service doExpressChecout =" + (end - start));
		
		logDebug("End  getExpCheckoutDetails of BBBPayPalService");
		return doExpRes;
	}
	/**
	 * This doAuthorization method makes service call to PayPal
	 * @param transactionID
	 * @param pOrder
	 * @return voResp
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	@SuppressWarnings("unchecked")
	public BBBDoAuthorizationResVO doAuthorization(String transactionID, BBBOrderImpl pOrder) throws BBBBusinessException, BBBSystemException{
		logDebug("Start  doAuthorization of BBBPayPalService with transactionID:" + transactionID);
		if (StringUtils.isEmpty(transactionID)) {
			return null;
		}

		BBBDoAuthorizationReq doAuthReq = new BBBDoAuthorizationReq();
		BBBDoAuthorizationRequestType doAuthReqType = new BBBDoAuthorizationRequestType();
		BBBDoAuthorizationResVO voResp = null;

		doAuthReqType.setTransactionId(transactionID);

		// set Amount
		BBBBasicAmountType orderTotal = new BBBBasicAmountType();
		orderTotal.setValue((Double.toString(pOrder.getPriceInfo().getAmount())));
		orderTotal.setCurrencyID(pOrder.getPriceInfo().getCurrencyCode());
		doAuthReqType.setAmount(orderTotal);
		doAuthReq.setDoAuthorizationRequestType(doAuthReqType);
		doAuthReq.setOrder(pOrder);

		// *************************Set payment group id with highest tickting group*************************

		double maxAmount = 0.0d;
		String higTicketSGid = null;
		List<ShippingGroup> shpGrp = pOrder.getShippingGroups();
		for (ShippingGroup grp : shpGrp) {
			if (grp instanceof BBBHardGoodShippingGroup && StringUtils.isBlank(((BBBHardGoodShippingGroup) grp).getRegistryInfo())) {
				BBBHardGoodShippingGroup hrd = (BBBHardGoodShippingGroup) grp;
				List<CommerceItemRelationship> list = hrd.getCommerceItemRelationships();
				double amount = 0.0d;
				for (CommerceItemRelationship cmitem : list) {
					amount = amount + (cmitem.getQuantity() * cmitem.getCommerceItem().getPriceInfo().getAmount());
				}
				if (amount > maxAmount) {
					higTicketSGid = hrd.getId();
					maxAmount = amount;
				}

			}
		}
		doAuthReq.setShippingGroupId(higTicketSGid);

		final String siteId = getSiteId();
		
		// *************************Set Order Information which will go to PayPal*************************

		final PriceInfoVO orderPriceInfo = getPricingTools().getOrderPriceInfo(pOrder);
		PriceInfoDisplayVO  priceInfoVO = BBBOrderUtilty.populatePriceInfo(orderPriceInfo, siteId, pOrder, null);
		List<PaymentGroup> paymentGrp = getPaymentGroupManager().getPaymentGroups(pOrder, BBBCheckoutConstants.GIFTCARD);
		double gcPrice = getPaymentGroupManager().getAmountCoveredByGiftCard(paymentGrp, pOrder);
		//Paypal Story - R2.2.1 - Added for Bed Bath Canada. Show PST and GST/HST as Separate Line Items instead of Tax - Start
				double estimatedPST = 0.0;
				double estimatedGSTHST = 0.0;
				if(BBBUtility.isNotEmpty(siteId) && 
						(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || 
						 siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) ) {
					estimatedPST = priceInfoVO.getShippingCountyLevelTax();
					estimatedGSTHST = priceInfoVO.getShippingStateLevelTax();
				}
				else{
					doAuthReq.setOrderTax((Double.toString(priceInfoVO.getTotalTax())));
				}
				//Paypal Story - R2.2.1 - Added for Bed Bath Canada. Show PST and GST/HST as Separate Line Items instead of Tax - End
				
		doAuthReq.setOrderShippingCost((Double.toString(priceInfoVO.getRawShippingTotal())));
		double orderTotalTemp = priceInfoVO.getTotalAmount() - gcPrice;
		doAuthReq.setOrderTotal((Double.toString(orderTotalTemp)));
		doAuthReq.setGiftWrapTotal((Double.toString(priceInfoVO.getGiftWrapTotal())));
		doAuthReq.setSurcharge((Double.toString(priceInfoVO.getTotalSurcharge()-priceInfoVO.getSurchargeSavings())));
		doAuthReq.setShippingDiscount(Double.toString(priceInfoVO.getTotalSavedAmount()));
		doAuthReq.setGiftCardAmount(String.valueOf(gcPrice));
		doAuthReq.setEstimatedPST(Double.toString(estimatedPST));
		doAuthReq.setEstimatedGSTHST(Double.toString(estimatedGSTHST));

		Map<String, PayPalProdDescVO> map = populateOrderItemDetails(pOrder);
		doAuthReq.setItem(map);

		doAuthReq.setServiceName(getDoAuthorization());
		
		final String servicewsdl = (String) (getBbbWebServicesConfig().getServiceToWsdlMap()).get(getDoAuthorization());
		BBBPayPalCredentials paypalCredentialsVO = getPayPalCred(servicewsdl);
		doAuthReq.setCred(paypalCredentialsVO);
		long start = System.currentTimeMillis();
		voResp = (BBBDoAuthorizationResVO) invokeServiceMethod(doAuthReq);
		long end = System.currentTimeMillis();
		logInfo("Total time taken to execute the Paypal service doAuthorization =" + (end - start));

		
		if (voResp != null) {
			logDebug("End  getExpCheckoutDetails of BBBPayPalService returns  voResp :" + voResp.toString());
		}
		
		return voResp;
	}
	
	
	/**
	 * Below method will validate token expiration time
	 * @param pOrder
	 * @return boolean
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public boolean isTokenExpired(BBBPayPalSessionBean payPalSessionBean, BBBOrderImpl pOrder) throws BBBSystemException, BBBBusinessException {
		logDebug("BBBPapPaylServiceManager.isTokenExpired() - starts");
		// Below code check Is Response VO Null
		/*if (getPayPalSessionBean().getGetExpCheckoutResponse() == null) {
			logError("BBBPapPaylServiceManager.isTokenExpired() Ends| received getGetExpCheckoutResponse = null");
			return true;
		}*/
		//For testing purpose set the below isEnableTesting to true
		if(isEnableTesting()){
			logDebug("BBBPapPaylServiceManager.isTokenExpired() - Testing is enabled| This is dummy response");
			return isTokenExpTest();
		}
		if(StringUtils.isEmpty(pOrder.getToken())){
			logDebug("********************************************Token Not Available**************************************");
			return true;
		}
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMATER);
		Date date = pOrder.getTimeStamp();
		final String servicewsdl = (String) (getBbbWebServicesConfig().getServiceToWsdlMap()).get(this.setExpressCheckoutService);
		logDebug("Service WSDL = " + servicewsdl);
		Integer expTime = Integer.valueOf(getCatalogTools().getAllValuesForKey(servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PP_WSTOKEN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_EXP_MINUTES).get(BBBWebServiceConstants.ZERO));

		logDebug("BBBPapPaylServiceManager.isTokenExpired() -Expiration Time - " + expTime);

		if (date == null) {
			logError("BBBPapPaylServiceManager.isTokenExpired() Ends recieved date = null");
			return true;
		}
		logDebug("BBBPapPaylServiceManager.isTokenExpired() order Id = " + pOrder.getId() + " | token + " + pOrder.getToken() + " | Date " + date.toString() + " Order TimeStamp: " + pOrder.getTimeStamp());
		String dateStart = format.format(pOrder.getTimeStamp());
		String currTime = format.format(cal.getTime());

		try {
			Date d1 = format.parse(dateStart);
			Date d2 = format.parse(currTime);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();
			
			long diffMinutes = diff / (60 * 1000);
			if (diffMinutes < expTime) {
				logDebug("BBBPapPaylServiceManager.isTokenExpired() - ends | Returns- false");
				return false;
			}
		} catch (ParseException e) {
			this.logError("ParseException occured while validating token ", e);
		}
		logDebug("BBBPapPaylServiceManager.isTokenExpired() - ends | Returns- true");
		payPalSessionBean.setGetExpCheckoutResponse(null);
		payPalSessionBean.setValidateOrderAddress(false);
		return true;
	}
	
	/*public String getShippingMethodChange(Profile profile) throws BBBSystemException {
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		
        final String ATG_COMMERCE_SHOPPING_CART = "/atg/commerce/ShoppingCart";
        final OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_COMMERCE_SHOPPING_CART);
        final BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();

        return this.validateShippingMethod(order, profile);
	}*/
	
	
	/**
	 * Below code will update order with the new token received from papal and remove the exiting PayPal PaymentGroup
	 * @param resType
	 * @param pOrder
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 */
	protected void updateTokenInOrder(String pToken, BBBOrderImpl pOrder, String pPayerId, Profile profile) throws CommerceException, BBBSystemException {
		logDebug("updateTokenInOrder() starts with pToken" + pToken + " | Order |" + pOrder + "| pPayerId |" + pPayerId);
		if (!StringUtils.isEmpty(pToken) && pOrder != null) {
			ClientLockManager lockManager = getLocalLockManager();
			boolean acquireLock = false;
			boolean shouldRollback = false;
			TransactionDemarcation td = new TransactionDemarcation();
			try {
				acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
				if (acquireLock) {
					lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
				}

				td.begin(getTransactionManager());

				synchronized (pOrder) {
					Calendar cal = Calendar.getInstance();
					pOrder.setTimeStamp(cal.getTime());
					pOrder.setToken(pToken);
					pOrder.setPayerId(pPayerId);
					getOrderManager().updateOrder(pOrder);
					profile.setPropertyValue(BBBCoreConstants.TOKEN, null);
				}
			} catch (DeadlockException e) {
				shouldRollback = true;
				this.logError("DeadlockException updating order repository", e);
				throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_TOKEN_UPDATE_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_TOKEN_UPDATE_FAIL, "EN", null));
			} catch (TransactionDemarcationException e) {
				shouldRollback = true;
				this.logError("TransactionDemarcationException while updating order repository", e);
				throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_TOKEN_UPDATE_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_TOKEN_UPDATE_FAIL, "EN", null));
			} finally {
				
				try {
					td.end(shouldRollback);
				} catch (TransactionDemarcationException e) {
					this.logError("TransactionDemarcationException while updating order repository", e);

				} finally {
					if (acquireLock)
						try {
							lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
						} catch (LockManagerException e) {
							this.logError("TransactionDemarcationException releasing lock on profile", e);

						}
				}
				
				logDebug("Ends updateTokenInOrder of BBBPayPalService:");
			}
		}
		logDebug("updateTokenInOrder() ends");
	}
	


	/**
	 * This Method validates if coupons are available for user or not.
	 * 
	 * 
	 * @param order
	 * @param addressVerifyVO
	 * @return couponExist true if exist otherwise false
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public boolean validateCoupons(BBBOrderImpl order, Profile profile) throws ServletException, IOException{
		
		logDebug("PayPalDroplet.validateCoupons():: User is Transient & Invoke Coupon webservice ");
		boolean couponExist = false;
		 String phoneNumber="";
		 String mobileNumber="";
		 if(order !=null && order.getBillingAddress() !=null)
		{
		 phoneNumber=order.getBillingAddress().getPhoneNumber();
		 mobileNumber=order.getBillingAddress().getMobileNumber();
		 //Resetting phone number and mobile number to dummy value for International Billing | pay Pal Carry Over |23752
	     if(isInternationalBilling(order.getBillingAddress().getCountry())){
		        order.getBillingAddress().setPhoneNumber(BBBPayPalConstants.DUMMY_PHONE_NUMBER);
		        order.getBillingAddress().setMobileNumber(BBBPayPalConstants.DUMMY_PHONE_NUMBER);
		        }
		}
			 
		//BBBGetCouponsManager couponManager = (BBBGetCouponsManager) ServletUtil.getCurrentRequest().resolveName(BBBPayPalConstants.COUPON_MANAGER);
		Map<String, RepositoryItem> promotions = getPromoManager().getCouponMap(order, profile);
		if((promotions != null && promotions.size() > 0) || getPromoManager().isSchoolPromotion(order)){
			logDebug("PayPalDroplet.service():: Coupons Exists");
			couponExist = true;
		}
		else{
			logDebug("PayPalDroplet.validateCoupons():: No coupon exists for user");
		}
		 //Setting phone number and mobile number back to actual Value | pay Pal Carry Over |23752
		if(order !=null && order.getBillingAddress() !=null)
		{
        	order.getBillingAddress().setPhoneNumber(phoneNumber);
        	order.getBillingAddress().setMobileNumber(mobileNumber);
		}
		return couponExist;
	}
	
	//Checking for Orders with International Billing
	private boolean isInternationalBilling(String billingCountry)
 {
		logDebug("isInternationalBilling [Start] Billing address country name :" + billingCountry);
		boolean isInternationalBilling = true;
		String country = billingCountry;
		String siteId = getSiteId();
		logDebug("siteId " + siteId + " country " + country);
		if (!StringUtils.isBlank(siteId) && !StringUtils.isBlank(country)) {
			if (((siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US) || siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB) ||
					siteId.equalsIgnoreCase("TBS_BedBathUS") || siteId.equalsIgnoreCase("TBS_BuyBuyBaby")) && 
					country.equalsIgnoreCase(BBBPayPalConstants.US_COUNTRY_CODE))
					|| ((siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase("TBS_BedBathCanada")) && country.equalsIgnoreCase(BBBPayPalConstants.CA_COUNTRY_CODE)))
			{
				isInternationalBilling = false;
			}
		}
		logDebug("isInternationalBilling [END] isInternationalBilling :"
					+ isInternationalBilling);
		return isInternationalBilling;
	}
	
	/**
	 * Below Code will fetch Currency Code from Repository
	 * @return String 
	 */
	protected String getPricingCurrencyCode() {
		String currency = null;
		logDebug("BBBPayPalServiceManager.getPricingCurrencyCode: Start");
		try {

			String siteId = getSiteId();
			if (!StringUtils.isBlank(siteId)) {
				RepositoryItem site = getSiteRepository().getItem(siteId, BBBCheckoutConstants.SITE_CONFIGURATION_ITEM);
				if (site != null) {
					currency = (String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY);
				}
			}

			if (StringUtils.isBlank(currency)) {
				currency = BBBCheckoutConstants.SITE_CURRENCY_USD;
			}
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError(e);
			}
			return BBBCheckoutConstants.SITE_CURRENCY_USD;
		}
		logDebug("BBBPayPalServiceManager.getPricingCurrencyCode: End");
		return currency;
	}
	
	/**
	 * This method populates item details in request VO
	 * 
	 * @param pOrder
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, PayPalProdDescVO> populateOrderItemDetails(BBBOrderImpl pOrder){
		
		logDebug("BBBPayPalServiceManager.populateOrderItemDetails: Start");
		List<CommerceItem> commerceItemLists = pOrder.getCommerceItems();
		BBBCommerceItem bbbItem = null;
		LinkedHashMap<String, PayPalProdDescVO> map = new LinkedHashMap<String, PayPalProdDescVO>();
		
			// *************************Set Commerce Item Information which will go to PayPal*************************
		for (CommerceItem commerceItem : commerceItemLists) {
			if (commerceItem instanceof BBBCommerceItem) {
				
				bbbItem = (BBBCommerceItem) commerceItem;
				logDebug("BBBPayPalServiceManager.populateOrderItemDetails: " + " sku id: " + bbbItem.getCatalogRefId() + "product id" + bbbItem.getAuxiliaryData().getProductId());
				PriceInfoVO priceInfo = getCommerceItemManager().getItemPriceInfo(bbbItem);
				
				// *************************Set Commerce Item Registry Information which will go to PayPal*************************
				String registantName = "";
				if (!StringUtils.isEmpty(bbbItem.getRegistryId())) {
					try {
						logDebug("BBBPayPalServiceManager.populateOrderItemDetails: Registry Id: " + bbbItem.getRegistryId() + " sku id: " + bbbItem.getCatalogRefId());
						RegistryVO regVO = getGiftRegistryManager().getRegistryDetailInfo(bbbItem.getRegistryId(),getSiteId());
						String prefix = getLblTxtTemplateManager().getPageLabel(LBL_FROM, DEFAULT_LOCALE, null, null);
						String sufix = getLblTxtTemplateManager().getPageLabel(LBL_SUFIX, DEFAULT_LOCALE, null, null);
						String registyLable = getLblTxtTemplateManager().getPageLabel("lbl_cart_registry_text", DEFAULT_LOCALE, null, null);
						
						// *************************Get Registry Type**********************
						String registryType = BBBCoreConstants.BLANK;
						Map<String, RegistrySummaryVO> registryMap = pOrder.getRegistryMap();
						RegistrySummaryVO regSummVO = registryMap.get(bbbItem.getRegistryId());
						if (regSummVO != null && regSummVO.getRegistryType() != null) {
							registryType = regSummVO.getRegistryType().getRegistryTypeDesc();
						}
						registantName = prefix + BBBCoreConstants.SPACE + regVO.getPrimaryRegistrant().getFirstName() + sufix + BBBCoreConstants.SPACE + registryType + BBBCoreConstants.SPACE + registyLable + BBBCoreConstants.SPACE + registyLable;
						if (!BBBUtility.isEmpty(regVO.getCoRegistrant().getFirstName())) {

							registantName = prefix + BBBCoreConstants.SPACE + regVO.getPrimaryRegistrant().getFirstName() + BBBCoreConstants.SPACE + BBBCoreConstants.AMPERSAND + BBBCoreConstants.SPACE + regVO.getCoRegistrant().getFirstName() + sufix + BBBCoreConstants.SPACE + registryType + BBBCoreConstants.SPACE + registyLable;
						}
						
					} catch (Exception e) {
						logError("BBBPayPalServiceManager.populateOrderItemDetails: Error occured while fetching registry details for registry ID " + bbbItem.getRegistryId());
						logError("Error Message :" + e);
					}
				}
				RepositoryItem productRepoItem = productRepoItem(bbbItem.getAuxiliaryData().getProductId());
				RepositoryItem skuRepoItem = skuRepoItem(bbbItem.getCatalogRefId());
				
				//If coupons are prorated on items quantity then add as different line item for each sku.
				// E.g. If Sku S1 has X quantity with Unit Prize Y and A quantity with Unit Price B then add each as different entry in Map.
				if (priceInfo != null && priceInfo.getPriceBeans() != null){
					if(priceInfo.getPriceBeans().size() > 1) {
						int priceBeanCount = 1;
						String priceBeanKey = null;
						//Item is  divided into 2 groups i.e. It has X quantity with Unit Prize Y and A quantity with Unit Price B
						for(UnitPriceBean priceBean : priceInfo.getPriceBeans()){
						if(priceBean != null){
							logInfo("BBB Commerce Item: ProductId: " + bbbItem.getAuxiliaryData().getProductId() + " Sku ID: " + bbbItem.getCatalogRefId() + 
									" Quanity: " + priceBean.getQuantity() + " Unit Price: " + priceBean.getUnitPrice());
							PayPalProdDescVO desVO = new PayPalProdDescVO();
							populateProductDetails(bbbItem, desVO, registantName, registantName, productRepoItem, skuRepoItem);
							desVO.setQuantity(priceBean.getQuantity());
							desVO.setAmount(priceBean.getUnitPrice());
							//BBBSL-4292 Creating a unique key to put in map
							priceBeanKey = bbbItem.getId()  + priceBean.getUnitPrice() + priceBeanCount;
							logDebug("BBBPayPalServiceManager.populateOrderItemDetails: Key for Map created by Price Beans of Commerce Item is : " + priceBeanKey );
							map.put(priceBeanKey, desVO);
							priceBeanCount++;
						}
						}
					}
					else if(priceInfo.getPriceBeans().get(0) != null){
						//Item have not any separation i.e. it has X quantity with price Y
						UnitPriceBean priceBean = priceInfo.getPriceBeans().get(0);
						logInfo("BBB Commerce Item: ProductId: " + bbbItem.getAuxiliaryData().getProductId() + " Sku ID: " + bbbItem.getCatalogRefId() + 
								" Quanity: " + priceBean.getQuantity() + " Unit Price: " + priceBean.getUnitPrice());
						PayPalProdDescVO desVO = new PayPalProdDescVO();
						populateProductDetails(bbbItem, desVO, registantName, registantName, productRepoItem, skuRepoItem);
						desVO.setAmount(priceBean.getUnitPrice());
						desVO.setQuantity(priceBean.getQuantity());
						logDebug("BBBPayPalServiceManager.populateOrderItemDetails: Key for Map created by Price Beans of Commerce Item is : " + bbbItem.getId() );
						map.put(bbbItem.getId(), desVO);
					}
				}
					
				}
			}
			logDebug("BBBPayPalServiceManager.populateOrderItemDetails: End");
			return map;
		}
	
		/**
		 * This method retrieve product details from catalog
		 * 
		 * @param productId
		 * @param desVO
		 * @param hostUrl
		 */
		private void populateProductDetails(BBBCommerceItem bbbItem, PayPalProdDescVO desVO, String hostUrl, String registantName, RepositoryItem productRepoItem, RepositoryItem skuRepoItem){
			
			desVO.setProdID(bbbItem.getAuxiliaryData().getProductId());
			desVO.setSkuId(bbbItem.getCatalogRefId());
			desVO.setSkuDescription(registantName);
			if(productRepoItem != null){
				if( productRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)!=null)
	 			{
					desVO.setProdName((String) productRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
	 			}
				if( productRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME)!=null){
					desVO.setProdURL(hostUrl + (String) productRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME));
	 			}
			}
			if(skuRepoItem != null){
				if( skuRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_SKU_PROPERTY_NAME)!=null)
	 			{
					desVO.setSkuName(StringEscapeUtils.unescapeHtml((String) skuRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_SKU_PROPERTY_NAME)));
	 			}
			}
		}
		
		/**
		 * This method retrieve product details from catalog
		 * 
		 * @param productId
		 * @param desVO
		 * @param hostUrl
		 */
		private RepositoryItem productRepoItem(String productId){
			
			RepositoryItem productRepositoryItem = null;
			try {
				if(productId != null)
				{
					logDebug("Fetching product details: product Id: " + productId);
					productRepositoryItem = this.getCatalogRepository().getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
				}
				else{
					logError("BBBPayPalServiceManager.productRepoItem: product id is null");
				}
				
			} catch (RepositoryException e) {
				logError("BBBPayPalServiceManager.productRepoItem: Error occured while fetching product details for product ID " + productId);
			}
			return productRepositoryItem;
		}
		
		/**
		 * This method retrieve sku details from catalog
		 * 
		 * @param skuId
		 * @param desVO
		 */
		private RepositoryItem skuRepoItem(String skuId){
			
			RepositoryItem skuRepositoryItem = null;
			try {
				if(skuId != null){
					logDebug("Fetching Sku details: sku Id: " + skuId);
					skuRepositoryItem = this.getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				}
				else{
					logError("BBBPayPalServiceManager.skuRepoItem: sku id is null");
				}
			} catch (RepositoryException e) {
				logError("BBBPayPalServiceManager.skuRepoItem: Error occured while fetching sku details for sku ID " + skuId);
			}
			
			return skuRepositoryItem;
		}
		/**
		 * Checks for state's Name from paypal and substitute state's code.
		 * This is required when paypal sends state's name instead of state's code
		 * @param bbbAddressPPVO
		 * @throws BBBSystemException
		 * @throws BBBBusinessException
		 */
		private void substituteStateCode(BBBAddressPPVO bbbAddressPPVO)
				throws BBBSystemException, BBBBusinessException {
			if(null != bbbAddressPPVO){
				List<StateVO> stateList=getCatalogTools().getStateList();
				for(StateVO stateVO: stateList){
					if(stateVO.getStateCode().equalsIgnoreCase(bbbAddressPPVO.getState())){
						break;
					}else if(stateVO.getStateName().equalsIgnoreCase(bbbAddressPPVO.getState())){
						bbbAddressPPVO.setState(stateVO.getStateCode());
						break;
					}
				}
			}
		}
		
		/**
		 * This method removes PayPal Details from Order if getExpressCheckout Service Fails.
		 * 
		 * @param order
		 * @throws CommerceException
		 * @throws BBBSystemException
		 *//*
		@SuppressWarnings("unchecked")
		public void removePayPalDetails(BBBOrderImpl order) throws CommerceException, BBBSystemException{
			
			logDebug("removePayPalDetails() starts");
			if (order != null) {
				ClientLockManager lockManager = getLocalLockManager();
				Profile profile = getProfile();
				boolean acquireLock = false;
				boolean shouldRollback = false;
				TransactionDemarcation td = new TransactionDemarcation();
				try {
					acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
					if (acquireLock) {
						lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
					}

					td.begin(getTransactionManager());

					synchronized (order) {
						logInfo("removePayPalDetails(): Removing Token from Order");
						Calendar cal = Calendar.getInstance();
						order.setTimeStamp(cal.getTime());
						order.setToken(null);
						order.setPayerId(null);
						
						//updateTokenInOrder(null, order, null);
						List<PaymentGroup> payPg = order.getPaymentGroups();
						if (payPg != null) {
							for (PaymentGroup pg : payPg) {
								if (pg instanceof Paypal) {
									logInfo("removePayPalDetails(): PayPal Payment Group Exist in Order and removing the same : PG Id: " + pg.getId());
									this.getPaymentGroupManager().removePaymentGroupFromOrder(order, pg.getId());
									break;
								}
							}
						}
						getOrderManager().updateOrder(order);
						getProfile().setPropertyValue(BBBCoreConstants.TOKEN, null);
					}
				} catch (DeadlockException e) {
					shouldRollback = true;
					this.logError("DeadlockException updating order repository", e);
					throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, "EN", null));

				} catch (TransactionDemarcationException e) {
					shouldRollback = true;
					this.logError("TransactionDemarcationException while updating order repository", e);
					throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, "EN", null));
				}
				finally {
					
					try {
						td.end(shouldRollback);
					} catch (TransactionDemarcationException e) {
						this.logError("TransactionDemarcationException while updating order repository", e);

					} finally {
						if (acquireLock)
							try {
								lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
							} catch (LockManagerException e) {
								this.logError("TransactionDemarcationException releasing lock on profile", e);
							}
					}
					logDebug("Ends removePayPalDetails of BBBPayPalServiceManager:");
				}
			}
			logDebug("removePayPalDetails() ends");
			
		}
		*/

	
	/**
	 * Below Code will remove PayPal Payment Group, paypal billing address and token from Order
	 * @param pOrder
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("unchecked")
	public void removePayPalPaymentGroup(BBBOrderImpl pOrder, Profile profile) throws BBBSystemException {
		logDebug("BBBPayPalserviceManger.removePayPalPaymentGroup() starts");
		ClientLockManager lockManager = getLocalLockManager();
		boolean acquireLock = false;
		boolean shouldRollback = false;
		TransactionDemarcation td = new TransactionDemarcation();
		try {
			acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
			if (acquireLock) {
				lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
			}

			td.begin(getTransactionManager());

			synchronized (pOrder) {
				final List<PaymentGroup> paymentList = pOrder.getPaymentGroups();
				Paypal pp = null;
				for (final PaymentGroup paymentGroup : paymentList) {
					if (paymentGroup instanceof Paypal) {
						pp = (Paypal) paymentGroup;
						break;
					}
				}
				if(null != pOrder.getBillingAddress() && pOrder.getBillingAddress().getIsFromPaypal()){
					pOrder.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null);
				}
				pOrder.setToken(null);
				if (pp != null) {					
					((BBBPaymentGroupManager) getOrderManager().getPaymentGroupManager()).removePaymentGroupFromOrder(pOrder, pp.getId());
				}
				this.getOrderManager().updateOrder(pOrder);
			}
		} catch (DeadlockException e) {
			shouldRollback = true;
			this.logError("DeadlockException while removing payment group in removePayPalPaymentGroup()", e);
			throwPaypalDetailsRemoveFailError();

		} catch (TransactionDemarcationException e) {
			shouldRollback = true;
			this.logError("TransactionDemarcationExceptionwhile removing payment group in removePayPalPaymentGroup()", e);
			throwPaypalDetailsRemoveFailError();
		} catch (CommerceException e) {
			shouldRollback = true;
			this.logError("CommerceException while removing payment group in removePayPalPaymentGroup()", e);
			throwPaypalDetailsRemoveFailError();
		} finally {
			logDebug("Ends removePayPalPaymentGroup of BBBPayPalService:");
			try {
				td.end(shouldRollback);
			} catch (TransactionDemarcationException e) {
				this.logError("TransactionDemarcationException while creating Rollback transaction", e);
				throwPaypalDetailsRemoveFailError();
			} finally {
				if (acquireLock)
					try {
						lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
					} catch (LockManagerException e) {
						this.logError("TransactionDemarcationException releasing lock on profile", e);
						throwPaypalDetailsRemoveFailError();
					}
				logDebug("BBBPayPalserviceManger.removePayPalPaymentGroup() starts");
			}
		}

	}

	/**
	 * @throws BBBSystemException
	 */
	protected void throwPaypalDetailsRemoveFailError() throws BBBSystemException {
		throw new BBBSystemException(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, "EN", null));
	}
	
	
	/**
	 * Below Code will save paypal shipping address in user profile
	 * @param pAddrVO
	 * @param pAddrPP
	 * @param pSiteId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public BBBAddress copyPayPalAddress(BBBAddressVO pAddrVO, BBBAddressPPVO pAddrPP,String pSiteId, Profile profile) throws BBBSystemException, BBBBusinessException{
		return this.getCheckoutMgr().getProfileAddressTool().addNewPayPalShippingAddress(profile, pAddrPP, pSiteId,false, false);
	}
	
	public final boolean compareOldAndNewCart(boolean saveOrder) throws ServletException, IOException, BBBSystemException, BBBBusinessException {
    	if(isLoggingDebug()){
    		logDebug("compareOldAndNewCart Method Starts.");
    	}
    	boolean result = true;
    	DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    	BBBPayPalSessionBean paypalSessionBean = (BBBPayPalSessionBean)request.resolveName(BBBPayPalConstants.PAYPAL_SESSION_BEAN_PATH);
    	OrderHolder cart = (OrderHolder)(request.resolveName(BBBCoreConstants.SHOPPING_CART_PATH));
    	BBBOrderImpl order = (BBBOrderImpl)cart.getCurrent();
    	synchronized (order) {
			List<CommerceItem> commerceItems = order.getCommerceItems();
			if(!BBBUtility.isListEmpty(commerceItems)){
				for(CommerceItem commerceItem : commerceItems){
					if(paypalSessionBean.getOldCartMap().containsKey(commerceItem.getId())){
						Long oldQuantity = paypalSessionBean.getOldCartMap().get(commerceItem.getId());
						if(oldQuantity.longValue() != commerceItem.getQuantity()){
							result = false;
							break;
						}
					}else{
						result = false;
						break;
					}
				}
				if(result && !paypalSessionBean.getOldCartMap().isEmpty() 
						&& paypalSessionBean.getOldCartMap().size() != commerceItems.size()){
					result = false;
				}
			}
			if(isLoggingDebug()){
				logDebug("Comparing Old Cart when paypal API was initially called and Current Cart match is " + result);
	    		logDebug("compareOldAndNewCart Method Ends.");
	    	}
			if(!result){
				logDebug("Result is false, so setting token to null in paypalSessionBean.");
				paypalSessionBean.getSessionBeanNull();
				if(saveOrder){
					logDebug("Result is false, saveOrder is true, setting token in order and removingPaypalPaymentGroup");
					Profile profile = (Profile) request.resolveName(BBBCoreConstants.ATG_PROFILE);
					removePayPalPaymentGroup(order, profile);
				}
			}
			return result;
    	}
    }
	
	public final boolean saveCartInPaypalBean() {
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		OrderHolder cart = (OrderHolder)(request.resolveName(BBBCoreConstants.SHOPPING_CART_PATH));
    	BBBOrderImpl order = (BBBOrderImpl)cart.getCurrent();
    	synchronized (order) {
			List<CommerceItem> commerceItems = order.getCommerceItems();
			BBBPayPalSessionBean paypalSessionBean = (BBBPayPalSessionBean)request.resolveName(BBBPayPalConstants.PAYPAL_SESSION_BEAN_PATH);
			for(CommerceItem commerceItem : commerceItems){				
				paypalSessionBean.getOldCartMap().put(commerceItem.getId(), new Long(commerceItem.getQuantity()));
			}
			if(isLoggingDebug()){
				logDebug("Saving Current Cart in PaypalSessionBean.");
			}
		}
    	return true;
    }
	/**
	 * The method to invoke pay pal service to get Response
	 * @param setExpReq
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected ServiceResponseIF invokeServiceMethod(ServiceRequestBase req) throws BBBBusinessException, BBBSystemException{
		return ServiceHandlerUtil.invoke(req);
	}
}