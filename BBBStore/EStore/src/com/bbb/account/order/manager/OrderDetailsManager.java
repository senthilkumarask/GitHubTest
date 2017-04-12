/**
 * 
 */
package com.bbb.account.order.manager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.util.NoLockNameException;
import atg.commerce.util.TransactionLockFactory;
import atg.commerce.util.TransactionLockService;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBDesEncryptionTools;
import com.bbb.account.vo.order.OrderDetailInfoReturn;
import com.bbb.account.vo.order.OrderDetailsReqVO;
import com.bbb.commerce.cart.bean.CommerceItemDisplayVO;
import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.cart.droplet.StatusChangeMessageManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.checkout.droplet.IsProductSKUShippingDropletHelper;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.checkout.util.BBBOrderUtilty;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.common.PriceInfoDisplayVO;
import com.bbb.commerce.common.ShippingGroupDisplayVO;
import com.bbb.commerce.common.ShippingGrpCIRelationshipVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.commerce.pricing.BBBPricingManager;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PaymentGroupDisplayVO;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;

/**
 * @author jsidhu
 * 
 *         This class will interact with WSFramework and get the order details
 *         from webservice
 */
public class OrderDetailsManager extends BBBGenericService {

	/* Constants */
	public static final String SHIPPING_GRP = "shippingGroup";
	public static final String PROFILE_PATH = "/atg/userprofiling/Profile";
	public static final String PROFILE_PARAM = "Profile";
	public static final String ORDER_PRAM = "Order";
	public static final String CREDIT_CART_CON = "CreditCardContainer";
	private static final int sflCookieAge = 2592000;
	private static final String sflCookiePath = "/";
	private static final String SFL_COOKIE_NAME = "SFLCookie";
	private static final String SAVED_ITEMS_LIST = "savedItemsList";
	private static final String ORDER_ID = "orderId";
	private static final String SITE_ID = "siteId";
	private static final int orderCookieAge = 2592000;
	private static final String orderCookiePath = "/";
	private static final String cartCookie = "CartCookie";
	private static final String ITEM_LIST = "itemList";

	private String mServiceName;
	private BBBCatalogTools mCatalogTools;
	private BBBPricingTools mPricingTools;
	private StatusChangeMessageManager statusManager;
	
	/**
	 * Instance of Order Manager.
	 */
	private OrderManager mOrderManager;
	private BBBPricingManager pricingManager;
	private GiftRegistryManager registryManager;
	private IsProductSKUShippingDropletHelper productSkuShipHelper;
	private BBBPromotionTools promotionTools;
	private PorchServiceManager porchServiceManager;

	/**
	 * Instance of Shipping manager.
	 */
	private BBBShippingGroupManager mShippingGroupManager;
	
	private BBBCheckoutManager checkoutManager;
	
	private TransactionManager transactionManager;
	private BBBCommerceItemManager commerceItemManager;
	
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
	public TransactionManager getTransactionManager() {
	    return this.transactionManager;
	}

	public void setTransactionManager(final TransactionManager transactionManager) {
	    this.transactionManager = transactionManager;
	}

	public BBBCheckoutManager getCheckoutManager() {
		return this.checkoutManager;
	}

	public void setCheckoutManager(final BBBCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}
	
	private String mShippingCarriers;
		
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
	 * @return the statusManager
	 */
	public StatusChangeMessageManager getStatusManager() {
		return statusManager;
	}

	/**
	 * @param pStatusManager the statusManager to set
	 */
	public void setStatusManager(StatusChangeMessageManager pStatusManager) {
		statusManager = pStatusManager;
	}
	
	
	/**
	 * @return the shippingCarriers
	 */
	public String getShippingCarriers() {
		return this.mShippingCarriers;
	}

	/**
	 * @param pShippingCarriers
	 *            the shippingCarriers to set
	 */
	public void setShippingCarriers(final String pShippingCarriers) {
		this.mShippingCarriers = pShippingCarriers;
	}
	
	public final OrderManager getOrderManager() {
        return this.mOrderManager;
    }
	

    public final void setOrderManager(final OrderManager pOrderManager) {
        this.mOrderManager = pOrderManager;
    }
    

	/**
	 * Getter for BBBShippingGroupManager.
	 * 
	 * @return mShippingGroupManager.
	 */
	public BBBShippingGroupManager getShippingGroupManager() {
		return this.mShippingGroupManager;
	}

	/**
	 * Setter for BBBShippingGroupManager.
	 * 
	 * @param pShipingGrpMgr
	 *            BBB Shipping Group Manager
	 */
	public void setShippingGroupManager(final BBBShippingGroupManager pShipingGrpMgr) {
		this.mShippingGroupManager = pShipingGrpMgr;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
		
	/**
	 * @return the pricingManager
	 */
	public BBBPricingManager getPricingManager() {
		return pricingManager;
	}

	/**
	 * @param pricingManager the pricingManager to set
	 */
	public void setPricingManager(BBBPricingManager pricingManager) {
		this.pricingManager = pricingManager;
	}
	
	/**
	 * @return the registryManager
	 */
	public GiftRegistryManager getRegistryManager() {
		return registryManager;
	}

	/**
	 * @param registryManager the registryManager to set
	 */
	public void setRegistryManager(GiftRegistryManager registryManager) {
		this.registryManager = registryManager;
	}
	
	/**
	 * @return the productSkuShipHelper
	 */
	public IsProductSKUShippingDropletHelper getProductSkuShipHelper() {
		return productSkuShipHelper;
	}

	/**
	 * @param productSkuShipHelper the productSkuShipHelper to set
	 */
	public void setProductSkuShipHelper(
			IsProductSKUShippingDropletHelper productSkuShipHelper) {
		this.productSkuShipHelper = productSkuShipHelper;
	}
	
	/**
	 * @return the promotionTools
	 */
	public BBBPromotionTools getPromotionTools() {
		return promotionTools;
	}

	/**
	 * @param promotionTools the promotionTools to set
	 */
	public void setPromotionTools(BBBPromotionTools promotionTools) {
		this.promotionTools = promotionTools;
	}
	
	
	private BBBDesEncryptionTools bbbDesEncryptionTools;
	
	public BBBDesEncryptionTools getBbbDesEncryptionTools() {
		return bbbDesEncryptionTools;
	}

	public void setBbbDesEncryptionTools(BBBDesEncryptionTools bbbDesEncryptionTools) {
		this.bbbDesEncryptionTools = bbbDesEncryptionTools;
	}

	public OrderDetailInfoReturn getLegacyOrderDetailInfo(
			final String pOrderId) throws BBBSystemException,
			BBBBusinessException {

		OrderDetailInfoReturn objOrderDetailRes = new OrderDetailInfoReturn();
		if (!BBBUtility.isEmpty(pOrderId)) {
				// Get Legacy orders
				objOrderDetailRes = this.getLegacyOrderDetail(pOrderId);
				if(null != objOrderDetailRes && !objOrderDetailRes.getStatus().isErrorExists()){
				if (objOrderDetailRes.getOrderInfo() != null) {
					final String currentSiteId = SiteContextManager.getCurrentSiteId();
					String orderDt = objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getOrderDt();
					Date orderDate = null;

					if (BBBUtility.isNotEmpty(orderDt)) {
						DateFormat formatter;
						try {
							formatter = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
							orderDate = (Date) formatter.parse(orderDt);
						} catch (ParseException e) {
							orderDate = null;
						}
					}
					if(currentSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || currentSiteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
						if (BBBUtility.isNotEmpty(orderDt)) {
							orderDt = BBBUtility.convertDateToAppFormat(orderDt);
							objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setOrderDt(orderDt);
						}
					}
					//To Do Removing Secure Information
					objOrderDetailRes.setBilling(null);
					objOrderDetailRes.setPayments(null);
				}
			} 
		} else {
			objOrderDetailRes = null;
		}
		return objOrderDetailRes;	
	}

	/**
	 * 
	 * @param pOrderId
	 * @return OrderDetailInfoReturn
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 *             Get Legacy order detail based on the order id
	 */
	public OrderDetailInfoReturn getLegacyOrderDetail(final String pOrderId)
			throws BBBBusinessException, BBBSystemException {
		
		OrderDetailInfoReturn orderDetailInfoReturn=null;
		if (!BBBUtility.isEmpty(pOrderId)) {
			final OrderDetailsReqVO orderDetailsReq = generateRequest(pOrderId);			
			orderDetailInfoReturn = (OrderDetailInfoReturn) ServiceHandlerUtil
					.invoke(orderDetailsReq);
			if (orderDetailInfoReturn != null && orderDetailInfoReturn.getStatus()!=null && orderDetailInfoReturn.getStatus().isErrorExists()) {				
					throw new BBBBusinessException(Integer.toString(orderDetailInfoReturn.getStatus().getErrorId()),orderDetailInfoReturn.getStatus().getDisplayMessage());
			}
		}
		return orderDetailInfoReturn;
	}
	
	/**
	 * 
	 * @param pOrderId
	 * @return OrderDetailsReqVO
	 * To generate the order details webservice request
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	private OrderDetailsReqVO generateRequest(final String pOrderId) throws BBBSystemException, BBBBusinessException {
		final OrderDetailsReqVO orderDetailsReq = new OrderDetailsReqVO();

		
		final String siteId = SiteContextManager.getCurrentSiteId();
		
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).size()>BBBCoreConstants.ZERO) {
			orderDetailsReq.setSiteFlag(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1268,"Site Flag for webservices is not set for siteId: " + siteId);
			}
			
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size()>BBBCoreConstants.ZERO) {
			orderDetailsReq.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1269,"Webservices Token for webservices is not set");
			}
		orderDetailsReq.setOrderID(pOrderId);
		orderDetailsReq.setServiceName(getServiceName());
		return orderDetailsReq;
	}


	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return this.mServiceName;
	}


	/**
	 * @param pServiceName the serviceName to set
	 */
	public void setServiceName(final String pServiceName) {
		this.mServiceName = pServiceName;
	}
	/**
	 * Method to get current order details
	 * 
	 * @return details of current order
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public BBBOrderVO getATGOrderDetails(final String orderId) throws BBBSystemException, BBBBusinessException {
		BBBOrderVO orderVO = new BBBOrderVO();
		BBBOrderImpl order = null;
		
		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final Profile prof = (Profile)request.resolveName("/atg/userprofiling/Profile");
		final String profileId = prof.getRepositoryId();
		
		try {
			if(StringUtils.isBlank(orderId) || !BBBUtility.isValidOrderNumber(orderId) || !getOrderManager().orderExists(orderId)){
				orderVO.setErrorExist(true);
				orderVO.setErrorMsg("Invalid order Id.");
				logError("Exiting class: OrderDetailsManager,  method :getATGOrderDetails  Error Message = " + orderVO.getErrorMsg());
				return orderVO;
				
			}
			order = (BBBOrderImpl) getOrderManager().loadOrder(orderId);
			orderVO = getOrderDetailsVO(order, true);
			logDebug("Profile ID from REQUEST : "+profileId);
			logDebug("Profile ID from ORDER : "+orderVO.getProfileId());
			if(!profileId.equals(orderVO.getProfileId())){
				logError("The profileId on the Order does not match the profileId of the REQUEST. So may be a HACK !!");
				throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_GET_ATG_ORDER_DETAILS_1001,"Profile ID on order does not match request");
			}
			final String siteId = SiteContextManager.getCurrentSiteId();
			if(siteId!=null&&siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) && ! StringUtils.isEmpty(orderVO.getSubmittedDate()))
			{
				orderVO.setSubmittedDate(BBBUtility.convertUSDateIntoWSFormatCanada(orderVO.getSubmittedDate()));
			} 
		} catch (CommerceException e) {
			throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_GET_ATG_ORDER_DETAILS_1002,
			"Error occurred while loading order from database",e);
		}

		logDebug("End method OrderDetailsManager.getATGOrderDetails");
		return orderVO;
		
	}	
	
	/**
	 * Method to get current order details
	 * 
	 * @return details of current order
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public BBBOrderVO getATGOrderDetails(final String orderId, final String itemLevelExpDeliveryReq) throws BBBSystemException, BBBBusinessException {
		BBBOrderVO orderVO = new BBBOrderVO();
		BBBOrderImpl order = null;
		
		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final Profile prof = (Profile)request.resolveName("/atg/userprofiling/Profile");
		final String profileId = prof.getRepositoryId();
		if (itemLevelExpDeliveryReq != null) {
			request.setParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY, itemLevelExpDeliveryReq);
        }
		
		try {
			if(StringUtils.isBlank(orderId) || !BBBUtility.isValidOrderNumber(orderId) || !getOrderManager().orderExists(orderId)){
				orderVO.setErrorExist(true);
				orderVO.setErrorMsg("Invalid order Id.");
				logError("Exiting class: OrderDetailsManager,  method :getATGOrderDetails  Error Message = " + orderVO.getErrorMsg());
				return orderVO;
				
			}
			order = (BBBOrderImpl) getOrderManager().loadOrder(orderId);
			orderVO = getOrderDetailsVO(order, true);
			logDebug("Profile ID from REQUEST : "+profileId);
			logDebug("Profile ID from ORDER : "+orderVO.getProfileId());
			if(!profileId.equals(orderVO.getProfileId())){
				logError("The profileId on the Order does not match the profileId of the REQUEST. So may be a HACK !!");
				throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_GET_ATG_ORDER_DETAILS_1001,"Profile ID on order does not match request");
			}
			final String siteId = SiteContextManager.getCurrentSiteId();
			if(siteId!=null&&siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) && ! StringUtils.isEmpty(orderVO.getSubmittedDate()))
			{
				orderVO.setSubmittedDate(BBBUtility.convertUSDateIntoWSFormatCanada(orderVO.getSubmittedDate()));
			} 
			else if(siteId!=null&&siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA) && ! StringUtils.isEmpty(orderVO.getSubmittedDate()))
			{
				orderVO.setSubmittedDate(BBBUtility.convertUSDateIntoWSFormatCanada(orderVO.getSubmittedDate()));
			} 
		} catch (CommerceException e) {
			throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_GET_ATG_ORDER_DETAILS_1002,
			"Error occurred while loading order from database",e);
		}

		logDebug("End method OrderDetailsManager.getATGOrderDetails");
		return orderVO;
		
	}	

	/**
	 * Method to get current order details
	 * @param order 
	 * @param fromAtgOrderDetails 
	 * @return details of current order
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException 
	 */
	public BBBOrderVO getOrderDetailsVO(BBBOrderImpl order, boolean fromAtgOrderDetails) 
			throws BBBSystemException, BBBBusinessException, CommerceException {
		return getOrderDetailsVO(order, fromAtgOrderDetails, true);
	}
	

	
	/**
	 * Method to get current order details
	 * @param order 
	 * @param fromAtgOrderDetails 
	 * @param couponFlag 
	 * @return details of current order
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException 
	 */
	@SuppressWarnings("unchecked")
	public BBBOrderVO getOrderDetailsVO(BBBOrderImpl order, boolean fromAtgOrderDetails, boolean couponFlag) 
			throws BBBSystemException, BBBBusinessException, CommerceException {
		
		
		logDebug("Starting method OrderDetailsManager.getCurrentOrderDetails");
		
		
		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		final BBBOrderVO orderVO = new BBBOrderVO();
		 
		String isSinglePage = request.getParameter("singlePageCheckoutEnabled");
		String fromMultiShippingPage = request.getParameter("fromMultiShippingPage");
		beforeSet(request, response);
		try{
		
		BBBOrderUtilty.populateOrderDetails(order, orderVO);			 
		if(!fromAtgOrderDetails && ((isSinglePage!=null && isSinglePage.equalsIgnoreCase("true"))||(fromMultiShippingPage!=null && fromMultiShippingPage.equalsIgnoreCase("true")) )){
			orderVO.setPorchRegistrantAddressRemoved(false);
				getPorchServiceManager().getRestrictedPorchServiceAddressMobile(order,orderVO);
			 
		}
		
		
	 
		
		//R2.2 - 83 Paypal -Check multi-shipping order for mobile - Start
		orderVO.setSingleShippingOrder(this.checkDisplayPPForSingleShipping(order));
      //R2.2 - 83 END
		boolean isException = false;
		final String siteId = SiteContextManager.getCurrentSiteId();
		
		final Map<String, String> carrierURL  = getCatalogTools().getConfigValueByconfigType(getShippingCarriers());
		orderVO.setCarrierUrlMap(carrierURL);
		
//		final TransactionManager transMgr = getTransactionManager();
//		final TransactionDemarcation transDem = new TransactionDemarcation();
		
		
		try {
			Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		    	
//		    	transDem.begin(transMgr, TransactionDemarcation.REQUIRED);
		    	if(order.getPriceInfo() == null) {
		    	    if(orderVO.getOrderId() != null){
		    	    	logDebug("Repricing Order due to Price Info Null for Order Id : "+ orderVO.getOrderId());
		    	    }
		    	    repriceCartMoveItemsFromCart(order, profile);
		    	}
		    	
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			orderVO.setProfileId(order.getProfileId());
			
			// 03/10/2013 populate coupon list
			if(!fromAtgOrderDetails){
				orderVO.setAppliedCouponsVO(this.getPromotionTools().getAppliedCoupons(order, profile,couponFlag));
			}
			// populate commerce item count
			orderVO.setCartItemCount((String.valueOf(order.getBBBCommerceItemCount())));

			// populate shipping group
			populateShippingGrp(ServletUtil.getCurrentRequest(), order, orderVO, fromAtgOrderDetails);

			// populate order price info
			final PriceInfoVO orderPriceInfo = getPricingTools().getOrderPriceInfo(order);
			orderVO.setOrderPriceInfoDisplayVO(BBBOrderUtilty.populatePriceInfo(orderPriceInfo, siteId, order, null));
			
			//populate total taxes for multishipping scenario for canada site.
			if(!(StringUtils.isEmpty(siteId)) && (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || 
												  siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) ){
				BBBOrderUtilty.setCanadaMultipleShippingTax(orderVO.getShippingGroups(), orderVO.getOrderPriceInfoDisplayVO());
			}
			
			// populate commerceItemVo list
			populateCommerceItemInfoList(ServletUtil.getCurrentRequest(), order, orderVO,fromAtgOrderDetails);
		
			getRegistryManager().populateRegistryMapInOrder(pRequest, order);
			
			orderVO.setRegistryMap(order.getRegistryMap());
			boolean isFromSPC = false;
            String isSPCEnabled = pRequest.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED);
            if (!BBBUtility.isEmpty((String)isSPCEnabled)) {
                isFromSPC = BBBCoreConstants.TRUE.equalsIgnoreCase(isSPCEnabled);
            }
            if(isFromSPC && orderVO.isBopusOrderFlag()){
            	orderVO.setPackAndHoldFlag(false);
            }else{
    			orderVO.setPackAndHoldFlag(getCheckoutManager().hasAllPackNHoldItems(siteId, order, isFromSPC));
            }
			// populate payment groups
		
			logDebug("OrderDetailsManager.getOrderDetailsVO: Start - populate payment groups");
			
			
			if(null != order.getPaymentGroups() && !order.getPaymentGroups().isEmpty()){
				
				List<PaymentGroupDisplayVO> paymentGrpList = new ArrayList<PaymentGroupDisplayVO>();
				PaymentGroupDisplayVO paymentVO = null;
				
				for(Object paymentGrp: order.getPaymentGroups()){
					paymentVO = new PaymentGroupDisplayVO();
					//R2.2 Story 83-J Start -Added payment group information for preview page 
					if(paymentGrp instanceof Paypal){
						paymentVO = BBBOrderUtilty.populatePayPalPaymentGrp(paymentVO, (Paypal)paymentGrp);
					}//R2.2 Story 83-J END
					else if(paymentGrp instanceof BBBGiftCard){
						paymentVO = BBBOrderUtilty.populateGiftCardPaymentGrp(paymentVO, (BBBGiftCard)paymentGrp);
					}else if(paymentGrp instanceof BBBCreditCard){
						paymentVO =  BBBOrderUtilty.populateCreditCardPaymentGrp(paymentVO, (BBBCreditCard)paymentGrp);
						
						List<BasicBBBCreditCardInfo> creditCardInfoList = BBBPurchaseProcessHelper.getCreditCardFromOrder(order);
						if(null != creditCardInfoList && !creditCardInfoList.isEmpty()){
							BasicBBBCreditCardInfo creditCard = creditCardInfoList.get(0);
							creditCard.setCreditCardNumber(BBBUtility.maskCrediCardNumber(creditCard.getCreditCardNumber()));
							creditCard.setBillingAddress(null);
							paymentVO.setCreditCardInfo(creditCard);
						}
						
					}
					paymentGrpList.add(paymentVO);
				}
				orderVO.setPaymentGroups(paymentGrpList);
				logDebug("No of PaymentGroups in Order "+ orderVO.getOrderId() + " is " + paymentGrpList.size());
			}
			
			String description = null;
		
			// second argument is false - as no need to check for single page functionality for mobile
			//BBBSL-9914 | Checking for order submission to avoid update order call
			try {
				if(BBBUtility.isNull(order.getSubmittedDate())){
					Boolean isSingleShipping=this.getCheckoutManager().displaySingleShipping(order, false);
					description = (isSingleShipping) ? getShippingGroupManager().getSingleShippingDescription(
						pRequest, order) : getShippingGroupManager().getMultiShippingDescription(pRequest, order);
				}
			} catch (BBBSystemException e) {
				
				logError(LogMessageFormatter.formatMessage(null,
							"SystemException"), e);
				
			} catch (BBBBusinessException e) {
			
				logError(LogMessageFormatter.formatMessage(null,
							"BusinessException"), e);
				
			}
			
			orderVO.getOrderPriceInfoDisplayVO().setEstimatedShippingMethod(description);
			double orderPreTaxAmount = orderVO.getOrderPriceInfoDisplayVO().getOrderPreTaxAmount();
			double storeAmount = orderVO.getOrderPriceInfoDisplayVO().getStoreAmount();
			double storeEcoFeeTotal = orderVO.getOrderPriceInfoDisplayVO().getStoreEcoFeeTotal();
			orderVO.getOrderPriceInfoDisplayVO().setTotalPreTaxAmount(BBBOrderUtilty.convertToTwoDecimals(orderPreTaxAmount + storeAmount + storeEcoFeeTotal));
			// Set prop 65 value if available
			setProp65Value(orderVO);
			logDebug("OrderDetailsManager.getOrderDetailsVO: End - populate payment groups");
			
			
			
		} catch (ServletException e) {
		
			logDebug("Error occurred while fetching order details :" + e.getCause() );
			
			isException = true;
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_GET_ORDER_DETAILS_1001, "Error occurred while fetching order details",e);
		} catch (IOException e) {
			
		
			logDebug("Error occurred while fetching order details :" + e.getCause() );
			
			isException = true;
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_GET_ORDER_DETAILS_1002, "Error occurred while fetching order details",e);
		} 
//		catch (TransactionDemarcationException e) {
//			if(isLoggingDebug()){
//			    logDebug("Error transaction " , e.getCause());
//			}
//			isException = true;
//			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_GET_ORDER_DETAILS_1001, "Error occurred while committing transection");
//		}
//		finally {
//			this.commitTransaction(isException, order);
//		} // End Try
		
		logDebug("Exiting method OrderDetailsManager.getCurrentOrderDetails");
		

		for (ShippingGroupDisplayVO shippingGroup : orderVO.getShippingGroups()) {
			if(shippingGroup != null && !shippingGroup.getCommerceItemRelationshipCount().equalsIgnoreCase("0")){
				String shippingMethodID = shippingGroup.getShippingMethod();
				if(StringUtils.isNotBlank(shippingMethodID) && !shippingMethodID.equalsIgnoreCase("storeShippingGroup")){
					RepositoryItem shippingMethod;
					try {
						shippingMethod = getCatalogTools().getShippingMethod(shippingMethodID);
						String shippingMethodDescription = (String) shippingMethod.getPropertyValue("shipMethodDescription");
						
						if(StringUtils.isNotBlank(shippingMethodDescription)){
							shippingGroup.setShippingMethodDescription(shippingMethodDescription);
						}
					} catch (BBBBusinessException e) {
						logError("Business Error while retrieving shipping method for [" + shippingMethodID + "]", e);
					} catch (BBBSystemException e) {
						logError("System Error while retrieving shipping method for [" + shippingMethodID + "]", e);
					}
				}
			}
		}
		}finally{
			afterSet(request, response);
		}
		if(null!= orderVO && !BBBUtility.isEmpty(orderVO.getCartItemCount()) && orderVO.getCartItemCount().equals("1")){
			orderVO.setSingleItemCheckoutFlag(true);
			orderVO.setSingleItemCheckoutUrl("checkout");
			
		}
		
		return orderVO;
	}
	

	protected void commitTransaction( boolean isRollback, Order order) {

		boolean exception = false;
		Transaction pTransaction = null;

		try {
			pTransaction = getTransactionManager().getTransaction();

			if(pTransaction != null){
				
				TransactionManager tm = getTransactionManager();
				if (isRollback || this.isTransactionMarkedAsRollBack())  {
					this.logDebug("Transaction is getting rollback from getorderDetailsVO() in OrderDetailsManager");
					if (tm != null){
						tm.rollback();
					}else{
						pTransaction.rollback();  // PR65109: rollback() before invalidateOrder() prevent deadlocks due to thread synchronization in the invalidateOrder()
					}
					if (order!=null && order instanceof OrderImpl){
						this.logDebug("Invalidating order because of transaction is getting rollback from getorderDetailsVO() in OrderDetailsManager");
						((OrderImpl)order).invalidateOrder();
					}
				}
				else {
					if (tm != null){
						this.logDebug("Commiting transaction in getorderDetailsVO() in OrderDetailsManager");
						tm.commit();
					}else{
						this.logDebug("Commiting transaction in getorderDetailsVO() in OrderDetailsManager");
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
				this.logDebug("Invalidating order due to exception in getorderDetailsVO() in OrderDetailsManager");
				if (order!=null && order instanceof OrderImpl)
					((OrderImpl)order).invalidateOrder();

			} // if
		} // finally
		// if
	}
	
	/**
	 * Returns true if the transaction associated with the current thread
	 * is marked for rollback. This is useful if you do not want to perform
	 * some action (e.g. updating the order) if some other subservice already
	 * needs the transaction rolledback.
	 * @return a <code>boolean</code> value
	 */
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

	/**
	 * This method is used to set prop 65 values in to current order
	 * @param bbbOrderVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("rawtypes")
	public  void  setProp65Value(BBBOrderVO bbbOrderVO ) throws BBBBusinessException, BBBSystemException
	{
		List<CommerceItemDisplayVO> commerceItemVOList=bbbOrderVO.getCommerceItemVOList();
		Map<String, Map> skuProp65Map = new HashMap<String, Map>();
		if (null!=commerceItemVOList && !commerceItemVOList.isEmpty())
		{
		for (CommerceItemDisplayVO commerceItemDisplayVO : commerceItemVOList) {
			
			String skuId=commerceItemDisplayVO.getSkuId();
			
			if(skuId != null){
					Map<String, String> skuProdMap = new HashMap<String, String>();
					skuProdMap= getCatalogTools().getSkuPropFlagStatus(skuId);
					if (null!=skuProdMap && !skuProdMap.isEmpty()){
					skuProp65Map.put(skuId, skuProdMap);
					}
	
			}
		  }
		}	
		bbbOrderVO.setProp65DetailMap(skuProp65Map);
	}

	/**
	 * Populates Commerce Item Info List.
	 * 
	 * @param currentRequest
	 * @param order
	 * @param orderVO
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 * @throws CommerceException 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private void populateCommerceItemInfoList(final DynamoHttpServletRequest currentRequest, final BBBOrderImpl order, final BBBOrderVO orderVO, final boolean fromAtgOrderDetails)
			throws ServletException, IOException, BBBSystemException, BBBBusinessException, CommerceException {
		
	
		logDebug("Starting method OrderDetailsManager.populateCommerceItemInfoList");

		BBBSessionBean session = null;
		
		session = (BBBSessionBean) currentRequest.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME);	
		if(session ==null){
			session = (BBBSessionBean)currentRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
			currentRequest.setAttribute(BBBCoreConstants.SESSION_BEAN_NAME, session);
		}
		
		
		HashMap values = session.getValues();
		List<String> userActiveRegIds = null;
			
		if(values !=null &&values.containsKey(BBBGiftRegistryConstants.USER_REGISTRIES_LIST)) {
			userActiveRegIds=(List<String>) values.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
		}
		// BBBH-1486 story changes
		boolean checkSddLogic = false;
    	if(ServletUtil.getCurrentRequest() != null){
    		String actionReq= ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.ACTION_REQ);
    		if(!StringUtils.isBlank(actionReq) && actionReq.equals(BBBCoreConstants.SAME_DAY_DELIVERY_REQ)){
    			checkSddLogic = true;
    		}
    	}
		String siteId = SiteContextManager.getCurrentSiteId();
		
		// BBBJ-611 TBS Next - Order Details page : Avoiding the DOM calls
		if(!fromAtgOrderDetails) {
		
			//update availability map before getting commerceitems VO's
			((BBBOrderManager) this.getOrderManager()).updateAvailabilityMapInOrder(currentRequest, order);
		
		}
		
		// BBBJ-611 TBS Next - Order Details page : ENDS
		
		List<CommerceItemVO> commerceItemVOList = (List<CommerceItemVO>) this.getCheckoutManager().getCartItemVOList(order);
		// BBBH-1486 story changes
		if(checkSddLogic){
			 this.getCheckoutManager().processSddItemsOnCart(commerceItemVOList, siteId);
		}
		List<CommerceItemDisplayVO> commerceItemDisplayVOList = new ArrayList<CommerceItemDisplayVO>();
		List<CommerceItemDisplayVO> sortedCommerceItemDisplayVOList = new ArrayList<CommerceItemDisplayVO>();
		CommerceItemDisplayVO commItemDisplayVO;
		boolean isOrderContainLTLItem = false; //LTL	
		boolean isOrderContainIntlRestrictedItem = false;
		boolean isVdcSku = false;
		boolean orderHasErrorPrsnlizedItem = false;
		boolean orderHasPersonlizedItem = false;
		boolean OOSFlag = false;
		if(commerceItemVOList!=null){
			logDebug("Total number of Commerce Items in Order ::" + commerceItemVOList.size());
	
			for (Iterator<CommerceItemVO> iterator = commerceItemVOList.iterator(); iterator.hasNext();) {
			CommerceItemVO commerceItemVO = iterator.next();
			commItemDisplayVO = new CommerceItemDisplayVO();
			commItemDisplayVO.setSddAvailabilityStatus(commerceItemVO.getSddAvailabilityStatus());
			logDebug("OrderDetailsManager.populateCommerceItemInfoList: Start - PriceDisplayDroplet call" +
						"for commerce item price info");
			
			commerceItemVO.setVendorInfoVO(commerceItemVO.getVendorInfoVO());
			commItemDisplayVO.setIncartPriceItem(commerceItemVO.getBBBCommerceItem().isIncartPriceItem());
			if(!orderVO.isIncartPriceOrder()){
				orderVO.setIncartPriceOrder(commerceItemVO.getBBBCommerceItem().isIncartPriceItem());
			}
			
			// BBBJ-611 TBS Next - Order Details page : Avoiding the DOM calls
			
			if(!fromAtgOrderDetails) {
				
				//BSL-2646 Code start item level message were not coming as priceMessageVO was not set 
				commerceItemVO.setPriceMessageVO(getStatusManager().checkCartItemMessage(commerceItemVO.getBBBCommerceItem(), currentRequest, order));
				//BSL-2646 Code end
			
			}
			
			// BBBJ-611 TBS Next - Order Details page : ENDS
			
			PriceInfoVO itemPriceInfoVO  = getCommerceItemManager().getItemPriceInfo(commerceItemVO.getBBBCommerceItem(),order);
			
			
			logDebug("OrderDetailsManager.populateCommerceItemInfoList: End - PriceDisplayDroplet call" +
						"for commerce item  price info");
			
			List<CommerceItem> comItemObj = order.getCommerceItems();

			for (CommerceItem comitem : comItemObj) {
				if(comitem instanceof BBBCommerceItem && comitem.getId().equalsIgnoreCase(commerceItemVO.getBBBCommerceItem().getId())){
					itemPriceInfoVO.setPrevPrice(((BBBCommerceItem)comitem).getPrevPrice());
					break;
				}
			}
			
			// check if commerce item is eligible for pack and hold
			if(commerceItemVO.getBBBCommerceItem().getBts() && !commerceItemVO.getSkuDetailVO().isVdcSku()){
				commItemDisplayVO.setPackNHold(true);
			}
			// LTL start : Check only until this flag is false. 
			if(! isOrderContainLTLItem) {  
				if(commerceItemVO.getSkuDetailVO().isLtlItem()){
					isOrderContainLTLItem = true;
				}
			}
			
			// RestrictedSku start : Check only until this flag is false.
			if(commerceItemVO.getSkuDetailVO().isIntlRestricted()) {
				isOrderContainIntlRestrictedItem = true;
			}
			// LTL end
			String skuId = commerceItemVO.getSkuDetailVO().getSkuId();
			boolean isSkuBelowLine = commerceItemVO.getSkuDetailVO().isSkuBelowLine();
			commItemDisplayVO.setSkuBelowLine(isSkuBelowLine);
			
			commItemDisplayVO.setShippingRestricted(getCatalogTools().isShippingRestrictionsExistsForSku(skuId));
			
			// get Restricted attributes for sku TO-DO Remove once these attributes are deactivated
			List<AttributeVO> list = populateRestrictedAttributesForCommItem(currentRequest, skuId, siteId);
			
			commItemDisplayVO.setRestrictedAttributes(list);
			String registryId = commerceItemVO.getBBBCommerceItem().getRegistryId();	
			commItemDisplayVO.setRegistryId(registryId);
				
			commItemDisplayVO.setGuestView(false);
			
			if((userActiveRegIds!=null && !userActiveRegIds.isEmpty() && userActiveRegIds.contains(registryId)) || registryId==null){
				commItemDisplayVO.setGuestView(true);   	
			}
			
			commItemDisplayVO.setFreeShipMethods(BBBPurchaseProcessHelper.getFreeShippingMethodInfo(commerceItemVO.getSkuDetailVO().getFreeShipMethods()));
			commItemDisplayVO.setCommerceItemPriceInfo(BBBOrderUtilty.populatePriceInfo(itemPriceInfoVO, siteId, order, commerceItemVO.getBBBCommerceItem().getPriceInfo()));
			commItemDisplayVO.setPriceMessageVO(commerceItemVO.getPriceMessageVO());
			commItemDisplayVO.setShippingMethodAvl(getCommerceItemManager().isShippingMehthodAvlForCommerceItem(commerceItemVO.getBBBCommerceItem()));
			//BPSI-551
			commItemDisplayVO.setWhiteGloveAssembly((Boolean.valueOf(commerceItemVO.getBBBCommerceItem().getWhiteGloveAssembly())));
			commItemDisplayVO.setRegisryShipMethod(commerceItemVO.getBBBCommerceItem().getRegistrantShipMethod());
			commItemDisplayVO.setHighestshipMethod(commerceItemVO.getBBBCommerceItem().getHighestshipMethod());
			BBBOrderUtilty.populateCommerceItemDisplayVO(commerceItemVO, commItemDisplayVO);
			//LTL-1235 expectedDeliveryDateForLTLItem at Item level and Blank at shipping group level
			
			BBBShippingGroupCommerceItemRelationship relationship = (BBBShippingGroupCommerceItemRelationship) commerceItemVO.getBBBCommerceItem().getShippingGroupRelationships().get(0);
			ShippingGroup shippingGroup = relationship.getShippingGroup();
			String shipMethod = null;
			if( null != shippingGroup){
				shipMethod = shippingGroup.getShippingMethod();		
			}
			//BPSI-551
			
			//BPSI-2440  | Set the offset and VDC message
			if(shippingGroup instanceof BBBHardGoodShippingGroup && !StringUtils.isEmpty(skuId) && commerceItemVO.getSkuDetailVO().isVdcSku()){
				isVdcSku = true;
				if(BBBUtility.isEmpty(shipMethod)){
					shipMethod = BBBCoreConstants.SHIP_METHOD_STANDARD_ID;
				}
				commItemDisplayVO.setVdcOffsetMessage(getCatalogTools().getActualOffsetMessage(skuId, siteId));
				commItemDisplayVO.setVdcSKUShipMesage(getCatalogTools().getVDCShipMessage(skuId, false, shipMethod,new Date(),false));
				commItemDisplayVO.setVdcSKULearnMoreMesage(getCatalogTools().getVDCShipMessage(skuId, false, shipMethod,new Date(),true));
			}
			
			//BPSI-2712 Cart Attributes for Mobile
			String referenceNumber=commerceItemVO.getBBBCommerceItem().getReferenceNumber();
			if(!BBBUtility.isEmpty(referenceNumber)){
				//populating Mobile EximData ;
				logDebug("Calling populateMobileEximData and refnum is" +referenceNumber);
				populateMobileEximData(commItemDisplayVO, commerceItemVO,
						referenceNumber);
			}
			String enableKatoriFlag = getCheckoutManager().getKatoriAvailability();
			// SET a flag for OrderLabel message id Error exist for Personalized item
			if (!BBBUtility.isEmpty(commerceItemVO.getBBBCommerceItem()
					.getReferenceNumber()) 
					&& ("false".equalsIgnoreCase(enableKatoriFlag) || (commerceItemVO.getBBBCommerceItem()
							.isEximErrorExists()))) {
				orderHasErrorPrsnlizedItem = true;
			}
			//SET a flag for OrderLevel message if Personalized item exist
			if (!BBBUtility.isEmpty(commerceItemVO.getBBBCommerceItem()
					.getReferenceNumber())) {
				orderHasPersonlizedItem = true;
			}
			
			if (commerceItemVO.getBBBCommerceItem().isLtlItem()) {
				if(order.getSubmittedDate() != null && !BBBUtility.isEmpty(shipMethod)){
					commItemDisplayVO.setExpectedDeliveryDateForLTLItem(getCatalogTools().getExpectedDeliveryDateForLTLItem(shipMethod, siteId, skuId, order.getSubmittedDate(),false));
				}else if(!BBBUtility.isEmpty(shipMethod)){
					commItemDisplayVO.setExpectedDeliveryDateForLTLItem(getCatalogTools().getExpectedDeliveryDateForLTLItem(shipMethod, siteId, skuId, new Date(),false));
				}
				
				//BPSI-1192
				 if(!BBBUtility.isEmpty(commerceItemVO.getBBBCommerceItem().getLtlShipMethod())) {
					 	//Get the Shipping Method and description from commerce item 
					    commItemDisplayVO.setLtlShipMethod(commerceItemVO.getBBBCommerceItem().getLtlShipMethod());
						RepositoryItem shippingMethod = getCatalogTools().getShippingMethod(commerceItemVO.getBBBCommerceItem().getLtlShipMethod());
						String ltlShipMethodDesc = (String) shippingMethod.getPropertyValue("shipMethodDescription");
						commItemDisplayVO.setLtlShipMethodDesc(ltlShipMethodDesc);
				}else{
						//Get the Shipping Method and description from shipping group 
						commItemDisplayVO.setLtlShipMethod(shipMethod);
						commItemDisplayVO.setLtlShipMethodDesc(getCommerceItemManager().getShippingMethodDesc(commerceItemVO.getBBBCommerceItem()));
			}
				commItemDisplayVO.setShipMethodUnsupported(commerceItemVO.getBBBCommerceItem().isShipMethodUnsupported());
			} else {
				final String expDeliveryRequired = currentRequest.getParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY);
				logDebug("populateCommerceItemInfoList -> the expDeliveryRequired is: "+ expDeliveryRequired + " and isVdcSku: "+ commerceItemVO.getSkuDetailVO().isVdcSku());
				// Here, expDeliveryRequired is to identify the origin/context of the request,
				// If these are generated from CheckoutController then populate 'Expected Delivery' on preview, Order
				// Confirmation & Order Details page.
				if(null == shipMethod || StringUtils.isEmpty(shipMethod)){
                    shipMethod = this.getCatalogTools().getDefaultShippingMethod(siteId).getShipMethodId();
             }
				if(shippingGroup instanceof BBBHardGoodShippingGroup && expDeliveryRequired != null && expDeliveryRequired.equalsIgnoreCase(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY_REQ)){
					setItemLevelExpectedDeliveryDate(commItemDisplayVO, orderVO, commerceItemVO.getSkuDetailVO().isVdcSku(), skuId, fromAtgOrderDetails, shipMethod, shippingGroup.getShipOnDate(), shippingGroup);
				}
			}
			
			//LTL-1235 expectedDeliveryDateForLTLItem at Item level and Blank at shipping group level
			List<ShipMethodVO> shipMethodVOs = commItemDisplayVO.getEligibleShipMethods();
			if (!OOSFlag && (BBBUtility.isEmpty(registryId) && commItemDisplayVO.getPriceMessageVO() != null && !commItemDisplayVO.getPriceMessageVO().isInStock()) || (BBBUtility.isNotEmpty(registryId) && commerceItemVO.getStockAvailability() == 1)) {
				OOSFlag = true;				
			}
			//Adding shipping cost price here so that it can be displayed on jsp as per sorted on shiipingCharges.
			getShippingGroupManager().calculateShippingCost(shipMethodVOs, order);
			commItemDisplayVO.setBuyOffAssociatedItem(commerceItemVO.getBBBCommerceItem().isBuyOffAssociatedItem());
			commItemDisplayVO.setBuyOffPrimaryRegFirstName(commerceItemVO.getBBBCommerceItem().getBuyOffPrimaryRegFirstName());
			commItemDisplayVO.setBuyOffCoRegFirstName(commerceItemVO.getBBBCommerceItem().getBuyOffCoRegFirstName());
			commItemDisplayVO.setBuyOffRegistryEventType(commerceItemVO.getBBBCommerceItem().getBuyOffRegistryEventType());
			if(commItemDisplayVO.isLtlItem()){
					 String lTLHighestShipMethodId = "";
					 String commShipMethod = commItemDisplayVO.getLtlShipMethod();
					 boolean ltlItemFlag = commItemDisplayVO.isLtlItem();
					 if(ltlItemFlag && (BBBUtility.isEmpty(commerceItemVO.getBBBCommerceItem().getDeliveryItemId()) || BBBUtility.isEmpty(commShipMethod))){
						 this.logDebug("LTL item found");
						 List<ShipMethodVO> lTLEligibleShippingMethods = this.getCatalogTools().getLTLEligibleShippingMethods(commItemDisplayVO.getSkuId(), SiteContextManager.getCurrentSiteId(), 
								 currentRequest.getLocale().getLanguage());
						 if(!(lTLEligibleShippingMethods.size()==0)){
						 ShipMethodVO lTLHighestShipMethod = lTLEligibleShippingMethods.get(lTLEligibleShippingMethods.size()-1);
						 lTLHighestShipMethodId = lTLHighestShipMethod.getShipMethodId();
						 commItemDisplayVO.setHighestshipMethod(lTLHighestShipMethodId);
						 }
	 				 
				 }	
			}
			
			// getting eph (porch service family codes)data for product
			ProductVO productVO = new ProductVO();
			getPorchServiceManager().getPorchServiceFamilyCodes(commerceItemVO.getBBBCommerceItem().getAuxiliaryData().getProductId(),productVO);
			 
			commItemDisplayVO.setPorchServiceFamilyCodes(productVO.getPorchServiceFamilyCodes());
			commItemDisplayVO.setPorchServiceFamilyType(productVO.getPorchServiceFamilyType());
			 
			commItemDisplayVO.setPorchService(((BaseCommerceItemImpl)commerceItemVO.getBBBCommerceItem()).isPorchService());
			if(commItemDisplayVO.isPorchService()){
				commItemDisplayVO.setPriceEstimation(commerceItemVO.getBBBCommerceItem().getPriceEstimation());
				commItemDisplayVO.setPorchProjectId((String)commerceItemVO.getBBBCommerceItem().getPorchServiceRef().getPropertyValue("porchProjectId"));
			}

			commerceItemDisplayVOList.add(commItemDisplayVO);

		}
	}

		orderVO.setOrderContainsOOSItem(OOSFlag);
		sortedCommerceItemDisplayVOList =sortedCommerceItemDisplayList(commerceItemDisplayVOList);
		if(!fromAtgOrderDetails){
		orderVO.setCommerceItemVOList(sortedCommerceItemDisplayVOList);
		}
		else{
			orderVO.setCommerceItemVOList(commerceItemDisplayVOList);	
		}
		orderVO.setOrderContainLTLItem(isOrderContainLTLItem); //LTL
		orderVO.setOrderContainIntlRestrictedItem(isOrderContainIntlRestrictedItem);
		orderVO.setEximWebserviceFailure(order.isEximWebserviceFailure());
		orderVO.setOrderHasErrorPrsnlizedItem(orderHasErrorPrsnlizedItem);
		orderVO.setOrderHasPersonlizedItem(orderHasPersonlizedItem);
		logDebug("Exiting method OrderDetailsManager.populateCommerceItemInfoList");

	

	}
	
	/**
	 * This method populates data in CommerceItemDisplayVO for Exim Call on cart
	 * @param commItemDisplayVO
	 * @param commerceItemVO
	 * @param referenceNumber
	 */
	private void populateMobileEximData(
			CommerceItemDisplayVO commItemDisplayVO,
			CommerceItemVO commerceItemVO, String referenceNumber) {
		logDebug(" populateMobileEximData method starts");
		commItemDisplayVO.setReferenceNumber(referenceNumber);
		commItemDisplayVO.setPersonalizationDetails(commerceItemVO.getBBBCommerceItem().getPersonalizationDetails());
		commItemDisplayVO.setPersonalizationOptions(commerceItemVO.getBBBCommerceItem().getPersonalizationOptions());
		commItemDisplayVO.setPersonalizationOptionsDisplay(commerceItemVO.getBBBCommerceItem().getPersonalizationOptionsDisplay());
		commItemDisplayVO.setPersonalizePrice(commerceItemVO.getBBBCommerceItem().getPersonalizePrice());
		commItemDisplayVO.setPersonalizationType(commerceItemVO.getSkuDetailVO().getPersonalizationType());
		commItemDisplayVO.setMobileFullImagePath(commerceItemVO.getBBBCommerceItem().getMobileFullImagePath());
		commItemDisplayVO.setMobileThumbnailImagePath(commerceItemVO.getBBBCommerceItem().getMobileThumbnailImagePath());
		commItemDisplayVO.setEximErrorExists(commerceItemVO.getBBBCommerceItem().isEximErrorExists());
		commItemDisplayVO.setVendorInfoVO(commerceItemVO.getVendorInfoVO());
		logDebug(" populateMobileEximData method ends");
	}
	
	public void setItemLevelExpectedDeliveryDate(
			CommerceItemDisplayVO commItemDisplayVO, BBBOrderVO orderVO, final boolean isVdcSku, final String skuId,
			final boolean fromAtgOrderDetails, final String shippingMethod,
			final Date shipOnDate, final ShippingGroup shippingGroup)
			throws BBBBusinessException, BBBSystemException {
		String state = ((BBBHardGoodShippingGroup) shippingGroup)
				.getShippingAddress().getState();
		String siteId = SiteContextManager.getCurrentSiteId();
		String orderDate = orderVO.getSubmittedDate();
		Date orderSubmittedDate = null;
		String expDeliveryDate = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					BBBCoreConstants.DATE_FORMAT);
			if (orderDate != null) {
				orderSubmittedDate = simpleDateFormat.parse(orderDate);
			}
			if (shippingMethod != null
					&& state != null
					&& !StringUtils
							.equalsIgnoreCase(
									shippingMethod,
									BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR)) {
				logDebug("shippingMethod: " + shippingMethod);
				logDebug("state: " + state);
				logDebug("fromAtgOrderDetails: " + fromAtgOrderDetails);
				logDebug("shipOnDate: " + shipOnDate);
				if (fromAtgOrderDetails && orderSubmittedDate != null) {
					if (shipOnDate != null) {
						if (isVdcSku) {
							expDeliveryDate = getCatalogTools()
									.getExpectedDeliveryTimeVDC(shippingMethod,
											skuId, true, shipOnDate, false);
						} else {
							expDeliveryDate = getCatalogTools()
									.getExpectedDeliveryDate(shippingMethod,
											state, siteId, shipOnDate,false);
						}
					} else {
						if (isVdcSku) {
							expDeliveryDate = getCatalogTools()
									.getExpectedDeliveryTimeVDC(shippingMethod,
											skuId, true, orderSubmittedDate, false);
						} else {
							expDeliveryDate = getCatalogTools()
									.getExpectedDeliveryDate(shippingMethod,
											state, siteId, orderSubmittedDate,false);
						}
					}
				} else {
					if (shipOnDate != null) {
						if (isVdcSku && orderSubmittedDate != null) {
							expDeliveryDate = getCatalogTools()
									.getExpectedDeliveryTimeVDC(shippingMethod,
											skuId, true, shipOnDate, false);
						} else {
							expDeliveryDate = getCatalogTools()
									.getExpectedDeliveryDate(shippingMethod,
											state, siteId, shipOnDate,false);
						}
					} else {
						if (isVdcSku) {
							expDeliveryDate = getCatalogTools()
									.getExpectedDeliveryTimeVDC(shippingMethod,
											skuId, true, new Date(), false);
						} else {
							expDeliveryDate = getCatalogTools()
									.getExpectedDeliveryDate(shippingMethod,
											state, siteId, new Date(),false);
						}
					}
				}
				logDebug("expDeliveryDate: " + expDeliveryDate);
				commItemDisplayVO
						.setItemLevelExpectedDeliveryDate(expDeliveryDate);

			} else {
				logDebug("Item Level Expected Delievery date is null for orderSubmittedDate: "
						+ orderSubmittedDate
						+ ", State: "
						+ state
						+ " and shippingMethod: " + shippingMethod);
			}
		} catch (BBBBusinessException e) {
			logError(
					"Error While invoking getItemLevelExpectedDeliveryDate method shipOnDate is "
							+ shipOnDate + " and siteId is" + siteId, e);
			throw new BBBBusinessException(
					"Error While invoking getItemLevelExpectedDeliveryDate method");
		} catch (BBBSystemException e) {
			logError(
					"Error While invoking getItemLevelExpectedDeliveryDate method shipOnDate is "
							+ shipOnDate + " and siteId is " + siteId, e);
			throw new BBBSystemException(
					"Error While invoking getItemLevelExpectedDeliveryDate method");
		} catch (ParseException e) {
			logError(
					"ParseException While invoking getItemLevelExpectedDeliveryDate method shipOnDate is "
							+ shipOnDate + " and siteId is " + siteId, e);
		}
	}

	public List<CommerceItemDisplayVO>  sortedCommerceItemDisplayList(List<CommerceItemDisplayVO> commerceItemDisplayVOList ){
		List<CommerceItemDisplayVO> sortedCommerceItemDisplayVOList = new ArrayList<CommerceItemDisplayVO>();
		List<CommerceItemDisplayVO> regCommerceItemDisplayVOList = new ArrayList<CommerceItemDisplayVO>();
		List<CommerceItemDisplayVO> nonCommerceItemDisplayVOList = new ArrayList<CommerceItemDisplayVO>();
		for (Iterator<CommerceItemDisplayVO> iterator = commerceItemDisplayVOList.iterator(); iterator.hasNext();) {
			CommerceItemDisplayVO commerceItemDisplayVO = iterator.next();
			if(commerceItemDisplayVO.getRegistryId()!=null && !commerceItemDisplayVO.getRegistryId().isEmpty() && !commerceItemDisplayVO.getRegistryId().equalsIgnoreCase(BBBCoreConstants.NULL_VALUE)) {
				regCommerceItemDisplayVOList.add(commerceItemDisplayVO);
			}else{
				nonCommerceItemDisplayVOList.add(commerceItemDisplayVO);
			}
		}
		if(!BBBUtility.isListEmpty(regCommerceItemDisplayVOList)){
		sortedCommerceItemDisplayVOList.addAll(regCommerceItemDisplayVOList);
		}
		if(!BBBUtility.isListEmpty(nonCommerceItemDisplayVOList)){
			sortedCommerceItemDisplayVOList.addAll(nonCommerceItemDisplayVOList);
		}
		
		return sortedCommerceItemDisplayVOList;
	}
	/**
	 * Rest Specific - Method to populate Restricted Attributes for commerceItem
	 * 
	 * @param currentRequest
	 * @param skuId
	 * @param siteId
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private List<AttributeVO> populateRestrictedAttributesForCommItem(final DynamoHttpServletRequest currentRequest, final String skuId, final String siteId) throws ServletException, IOException
	{
		List<AttributeVO> list = new ArrayList<AttributeVO>();
		try {
			String productId = getCatalogTools()
					.getParentProductForSku(skuId, true);
			
			logDebug("OrderDetailsManager.populateRestrictedAttributesForCommItem : IsProductSkuShippingHelper call" +
						"to get restricted attributes for sku: " + skuId + "prodId: " + productId);
			
			Map<String,AttributeVO> restrictionZipCodeAttributes= new HashMap<String,AttributeVO>();
			restrictionZipCodeAttributes = this.getProductSkuShipHelper().getAttribute(siteId, skuId, productId);
			
			if( null != restrictionZipCodeAttributes){	
				Set<String> restrictedAttributes = (Set<String>) restrictionZipCodeAttributes.keySet();
				if(restrictedAttributes != null && !restrictedAttributes.isEmpty()){
					for(String attribute : restrictedAttributes){
						RepositoryItem[] items = getCatalogTools().getAttributeInfoRepositoryItems(attribute);
						if(items != null  && items.length > 0){
							for(RepositoryItem item : items){
								if(item != null){
									AttributeVO attributeVO = new AttributeVO();
									String attributeName = item.getRepositoryId();
									attributeVO.setAttributeName(attributeName);
									if(null != item.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)){
										String displayDescription = String.valueOf(item.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME));
										attributeVO.setAttributeDescrip(displayDescription);
									}
									if(null != item.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)){
										String imageURL = String.valueOf(item.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME));
										attributeVO.setImageURL(imageURL);
									}
									if(null != item.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)){
										String actionURL = String.valueOf(item.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME));
										attributeVO.setActionURL(actionURL);
									}
									if(null != item.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME)){
										String placeHolder = String.valueOf(item.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME));
										attributeVO.setPlaceHolder(placeHolder);
									}
									if(item.getPropertyValue(BBBCatalogConstants.PRIORITY_ATTRIBUTE_PROPERTY_NAME) != null){
										int priority = (Integer)item.getPropertyValue(BBBCatalogConstants.PRIORITY_ATTRIBUTE_PROPERTY_NAME);
										attributeVO.setPriority(priority);
									}
									list.add(attributeVO);
								}
							}
						}
					}
				}
			}
		}
		catch (BBBBusinessException e) {
			logDebug("populateCommerceItemInfoList :: Business Exception while fetching attribute list for sku id: " + skuId,e);
		} catch (BBBSystemException e) {
			logDebug("populateCommerceItemInfoList :: System Exception while fetching attribute list for sku id: " + skuId,e);
		}
		return list;
		
	}

	/**
	 * populate shipping group information.
	 * 
	 * @param pRequest
	 * @param order
	 * @param orderVO
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void populateShippingGrp(final DynamoHttpServletRequest pRequest, final BBBOrderImpl order, final BBBOrderVO orderVO, final boolean fromAtgOrderDetails) 
																				throws ServletException, IOException,BBBBusinessException,BBBSystemException{

		
		logDebug("Starting method OrderDetailsManager.populateShippingGrp");
		
		
		String siteId = SiteContextManager.getCurrentSiteId();
		List<ShippingGroupDisplayVO> shippingGroupsList = new ArrayList<ShippingGroupDisplayVO>();

		List<BBBShippingGroup> shipGrpList = order.getShippingGroups();

		if (null != shipGrpList && !shipGrpList.isEmpty()) {

			for (Iterator iterator = shipGrpList.iterator(); iterator.hasNext();) {

				ShippingGroupImpl shipGrp = (ShippingGroupImpl) iterator.next();
				ShippingGroupDisplayVO shipGroupVO = BBBOrderUtilty.populateShippingGrpInfo(shipGrp);				
				PriceInfoVO priceInfo = this.getPricingManager().getShippingPriceInfo(shipGrp, order);

				// populate shipping group priceInfo
				if (null != priceInfo){
					logDebug("Total saved amount is -- "+ priceInfo.getTotalSavedAmount());
				}
				shipGroupVO.setShippingPriceInfoDisplayVO(BBBOrderUtilty.populatePriceInfo(priceInfo, siteId, order, null));
				try {
				populateCIRelationshipPriceInfo(shipGrp, pRequest, shipGroupVO,order,fromAtgOrderDetails);
				}catch (BBBBusinessException e) {
					logError("Error While invoking populateCIRelationshipPriceInfo:" + e);
				}catch (BBBSystemException e) {
					logError("Error While invoking populateCIRelationshipPriceInfo method:" +e);
				}
				//LTL-1235 expectedDeliveryDateForLTLItem at Item level and Blank at shipping group level
				boolean isLTLSg = this.getShippingGroupManager().isLTLSg(shipGrp.getShippingMethod(),order.getSiteId());
				if(isLTLSg) {
					shipGroupVO.setExpectedDeliveryDate("");
				} else if (shipGrp instanceof BBBHardGoodShippingGroup){
					//set the Expected delivery date of shipping
					setExpectedDeliveryDate((BBBHardGoodShippingGroup)shipGrp, shipGroupVO, orderVO, fromAtgOrderDetails);
				}
				shippingGroupsList.add(shipGroupVO);
			}
			logDebug("No of Shipping Group in Order are :" + shippingGroupsList.size());
			orderVO.setShippingGroups(shippingGroupsList);

		}
		
	
		logDebug("Exiting method OrderDetailsManager.populateShippingGrp");

	

	}

	private void setExpectedDeliveryDate(final BBBHardGoodShippingGroup shipGrp, final ShippingGroupDisplayVO shipGrpVO, final BBBOrderVO orderVO, final boolean fromAtgOrderDetails) {
		
		String shippingMethod = shipGrp.getShippingMethod();
		String state = shipGrp.getShippingAddress().getState();
		Date shipOnDate = shipGrp.getShipOnDate();
		String siteId = SiteContextManager.getCurrentSiteId();
		String orderDate = orderVO.getSubmittedDate();
		Date orderSubmittedDate = null;
		String expDeliveryDate = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BBBCoreConstants.DATE_FORMAT);
			if(orderDate != null){
			orderSubmittedDate = simpleDateFormat.parse(orderDate);}
			if(shippingMethod != null && state != null && !StringUtils.equalsIgnoreCase(shippingMethod, BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR) ){
				logDebug("shippingMethod: " + shippingMethod + " state: " + state + "shipOnDate: " + shipOnDate);
				if(fromAtgOrderDetails && orderSubmittedDate != null){
					if(shipOnDate != null){
						expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state,siteId,shipOnDate,false );
					}else{
						expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state,siteId,orderSubmittedDate,false );
					}
				}
				else{
					if(shipOnDate != null){
						expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state, siteId, shipOnDate,false);
					}else{
						if(!BBBUtility.isEmpty(shippingMethod)){
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state, siteId, new Date(),false);
						}
					}
				}
				shipGrpVO.setExpectedDeliveryDate(expDeliveryDate);
				
			}else{
				logDebug("Expected Delievery date is null for orderSubmittedDate: " + orderSubmittedDate + "State: " + state + "shippingMethod: " + shippingMethod);
			}
		} catch (BBBBusinessException e) {
			logError("Error While invoking getExpectedDeliveryDate method" + "shipOnDate is" + shipOnDate + "siteId is" + siteId,e);
		} catch (BBBSystemException e) {
			logError("Error While invoking getExpectedDeliveryDate method" + "shipOnDate is" + shipOnDate + "siteId is " + siteId,e);
		} catch (ParseException e) {
			logError("ParseException While invoking getExpectedDeliveryDate method" + "shipOnDate is" + shipOnDate + "siteId is " + siteId,e);
		}	
	}

	/**
	 * Populates CommerceItem Relationship Price Info.
	 * 
	 * @param shippingGrpImpl
	 * @param pRequest
	 * @param shipGroupVO
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings( { "unused", "rawtypes" })
	private void populateCIRelationshipPriceInfo(final ShippingGroupImpl shippingGrpImpl, final DynamoHttpServletRequest pRequest, final ShippingGroupDisplayVO shipGroupVO, final BBBOrderImpl order, final boolean fromAtgOrderDetails)
			throws ServletException, IOException, BBBBusinessException,BBBSystemException {
	
		logDebug("Starting method OrderDetailsManager.populateCIRelationshipPriceInfo");


		Map<String, PriceInfoDisplayVO> priceInfoMap = null;

		ShippingGrpCIRelationshipVO relationshipVO = null;
		List<ShippingGrpCIRelationshipVO> commerceItemRelationshipVOList = new ArrayList<ShippingGrpCIRelationshipVO>();
		List<ShippingGrpCIRelationshipVO> regVOList = new ArrayList<ShippingGrpCIRelationshipVO>();
		List<ShippingGrpCIRelationshipVO> nonRegVOList = new ArrayList<ShippingGrpCIRelationshipVO>();

		if (null != shippingGrpImpl && null != shippingGrpImpl.getCommerceItemRelationships() && !shippingGrpImpl.getCommerceItemRelationships().isEmpty()) {
			priceInfoMap = new HashMap<String, PriceInfoDisplayVO>();
			for (Iterator iterator = shippingGrpImpl.getCommerceItemRelationships().iterator(); iterator.hasNext();) {
				
				relationshipVO = new ShippingGrpCIRelationshipVO();
				ShippingGroupCommerceItemRelationship ciRelationsShip = (ShippingGroupCommerceItemRelationship) iterator.next();				
				PriceInfoVO priceInfo = this.getPricingManager().getShippingGroupCommerceItemPriceInfo((ciRelationsShip));
				relationshipVO.setQuantity(String.valueOf(ciRelationsShip.getQuantity()));
				relationshipVO.setCommerceItemId(ciRelationsShip.getCommerceItem().getId());
				relationshipVO.setPriceBeans(priceInfo.getPriceBeans());
				relationshipVO.setUndiscountedItemsCount(priceInfo.getUndiscountedItemsCount());
				relationshipVO.setShippingGroupItemTotal(priceInfo.getShippingGroupItemTotal());

				commerceItemRelationshipVOList.add(relationshipVO);
			} 
			List<CommerceItemVO> commerceItemVOList = (List<CommerceItemVO>) this.getCheckoutManager().getCartItemVOList(order);
			for(ShippingGrpCIRelationshipVO commerceItemRelationshipVO:commerceItemRelationshipVOList){
				for(CommerceItemVO commerceItemVO:commerceItemVOList){
					if((commerceItemVO.getBBBCommerceItem().getId()).equalsIgnoreCase(commerceItemRelationshipVO.getCommerceItemId())){
						if(!StringUtils.isEmpty(commerceItemVO.getBBBCommerceItem().getRegistryId())){
							regVOList.add(commerceItemRelationshipVO);
						}else{
							nonRegVOList.add(commerceItemRelationshipVO);
						}
					}
				}
			}
			commerceItemRelationshipVOList.clear();
			if(fromAtgOrderDetails){
				commerceItemRelationshipVOList.addAll(nonRegVOList);
				commerceItemRelationshipVOList.addAll(regVOList);
			}else{
				commerceItemRelationshipVOList.addAll(regVOList);
				commerceItemRelationshipVOList.addAll(nonRegVOList);
			}
	
		}
		logDebug("Commerce Items in Shipping Group Count for Shipping Group id " + shipGroupVO.getId() + "is :" + commerceItemRelationshipVOList.size());
		shipGroupVO.setCommerceItemRelationshipVOList(commerceItemRelationshipVOList);		
	
		logDebug("Exiting method OrderDetailsManager.populateCIRelationshipPriceInfo");


	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void repriceCartMoveItemsFromCart(final Order pOrder, final Profile pProfile) throws BBBSystemException, BBBBusinessException, CommerceException {
		this.logDebug("Entering method OrderDetailsManager.repriceCartMoveItemsFromCart()");
		Map map = new HashMap();
		map.put(PipelineConstants.ORDER, pOrder);
		map.put("Profile", pProfile);
		map.put(PricingConstants.PRICING_OPERATION_PARAM,  PricingConstants.OP_REPRICE_ORDER_TOTAL);
		map.put(PipelineConstants.ORDERMANAGER, getOrderManager());
		boolean isException=false;
		try {
			synchronized (pOrder) {
				PipelineResult rs = getOrderManager().getPipelineManager().runProcess("repriceAndUpdateOrder", map);
				if (rs.hasErrors()){
					isException=true;
				}
			}
		} catch (RunProcessException e) {
			logError(LogMessageFormatter.formatMessage(null, "RunProcessException in StoreGiftlistFormHandler while repriceCartMoveItemsFromCart", BBBCoreErrorConstants.ACCOUNT_ERROR_1256 ), e);
			isException=true;
			
		} 
		if(isException){
			logError("Errors in Pipeline execution for repriceAndUpdateOrder");
		}
		this.logDebug("Exiting method OrderDetailsManager.repriceCartMoveItemsFromCart()");
	}

	// Code added from Purchase Process Form Handler
	
	private void afterSet(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) {
		// TODO Auto-generated method stub
		try
	    {


	      // Try to keep the response to this page from being cached.
	      ServletUtil.setNoCacheHeaders(response);


	    }

	    // Release the transaction lock in a finally clause in case any of the code
	    // above throws exceptions.  We don't want to end up holding the lock forever
	    // in this case.

	    finally
	    {
		      try{
		      Transaction t = getCurrentTransaction();
		   
		        if (t != null)
		          logDebug("afterSet sees currentTransaction as " + t);
		        else
		          logDebug("afterSet sees no current transaction.");
		     
	
		      if (t != null && isTransactionCreated(request, response)) {			      
			        logDebug("afterSet committing transaction " + t);
			        commitTransaction(t);
			        request.removeParameter("transactionCreated");
			      }
		      }finally{
			      try {
			        releaseTransactionLock(request);
			      }
			      catch (LockManagerException lme) {
			         logError(lme);
			      }
			  }
	    }
		
		
	}
	
	protected void releaseTransactionLock(DynamoHttpServletRequest pRequest)
			throws LockManagerException {
		try {
			TransactionLockService service = getTransactionLockService();
			if (service != null) {
				String lockName = (String) pRequest
						.getAttribute("atg.PurchaseProcessFormHandlerLock");
				if (lockName != null) {
					service.releaseTransactionLock(lockName);
					pRequest.removeAttribute("atg.PurchaseProcessFormHandlerLock");
				} else {
					service.releaseTransactionLock();
				}
			}
		} catch (LockManagerException exc) {			
			logError(exc);
		}
	}
	
	
	
	
	private TransactionLockService getTransactionLockService() {
		if (mTransactionLockService != null)
			return mTransactionLockService;
		
		TransactionLockFactory factory =  (TransactionLockFactory) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/util/TransactionLockFactory");
				

		if (factory != null)
			mTransactionLockService = factory.getServiceInstance(this);
		else if (isLoggingWarning())
			logWarning("Missing Transaction lock factory");

		// use the default setting of the TransactionLockService unless we have
		// been
		// explicitly configured
		if (mUseLocksAroundTransactionsSet)
			mTransactionLockService.setEnabled(isUseLocksAroundTransactions());

		return mTransactionLockService;
	}
	
	boolean mUseLocksAroundTransactions = true;
	
	private boolean mUseLocksAroundTransactionsSet = false;
	private TransactionLockService mTransactionLockService = null;
	public boolean isUseLocksAroundTransactions() {
	    return mUseLocksAroundTransactions;
	  }
	
	protected void commitTransaction(Transaction pTransaction)
	  {
	   boolean exception = false;
	   
	   if (pTransaction != null) {
	      try {
	        TransactionManager tm = getTransactionManager();
	        if (isTransactionMarkedAsRollBack())  {
	          if (tm != null){
	            tm.rollback();
	          }else{
	            pTransaction.rollback();  // PR65109: rollback() before invalidateOrder() prevent deadlocks due to thread synchronization in the invalidateOrder()
	          }
	          if (getOrder() instanceof OrderImpl)
	            ((OrderImpl)getOrder()).invalidateOrder();
	        }
	        else {
	          if (tm != null){
	            tm.commit();
	          }else{
	            pTransaction.commit();
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
	          if (getOrder() instanceof OrderImpl)
	            ((OrderImpl)getOrder()).invalidateOrder();

	        } // if
	      } // finally
	    } // if
	  }
	
	private OrderImpl getOrder() {
		// TODO Auto-generated method stub
		
		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			
		final OrderHolder cart = (OrderHolder) request.resolveName("/atg/commerce/ShoppingCart");
        final BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
		
		return order;
	}

	protected boolean isTransactionCreated(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {
		String txCreated = pRequest.getParameter("transactionCreated");
		if (txCreated != null && txCreated.equals("true")) {
			return true;
		} // end of if ()
		return false;
	}
	public Transaction getCurrentTransaction() {
	    try{
	      TransactionManager tm = getTransactionManager();
	      if (tm == null) {	       
	         logError("Missing Transaction Manager");
	        
	        return null;
	      }
	      Transaction t = tm.getTransaction();

	      return t;
	    }
	    catch (SystemException exc) {	      
	        logError(exc);
	    }
	    return null;

	  }

	private void beforeSet(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) {
		// TODO Auto-generated method stub
		try {
		      acquireTransactionLock(request);
		    }
		    catch (DeadlockException de) {

		      // We are going to log the exception here and then ignore it because
		      // the worst that should happen is that the user will get a concurrent
		      // update exception if two threads try to modify the same order, and we
		      // can recover from that.
		  
		        logError(de);
		    }
		
		
		Transaction t = ensureTransaction();
	    if (t != null)
	    	request.setParameter("transactionCreated", "true");
	   
	      if (t != null)
	        logDebug("beforeSet created transaction " + t);
	      else
	        logDebug("beforeSet did not create a transaction.");
	    	
	}
	
	protected void acquireTransactionLock(DynamoHttpServletRequest pRequest)
			throws DeadlockException {
		try {
			TransactionLockService service = getTransactionLockService();
			if (service != null) {
				RepositoryItem profileItem = getProfile();
				if (profileItem != null) {
					String profileId = profileItem.getRepositoryId();
					pRequest.setAttribute("atg.PurchaseProcessFormHandlerLock", profileId);
					service.acquireTransactionLock(profileId);
				} else {
					service.acquireTransactionLock();
				}
			}
		} catch (NoLockNameException exc) {
			logError(exc);
		}
	}
	private RepositoryItem getProfile() {
		// TODO Auto-generated method stub		
		Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		return profile;
	}

	boolean mEnsureTransaction = true;
	public boolean isEnsureTransaction() {
	    return mEnsureTransaction;
	 }
	
	protected Transaction ensureTransaction(){
		if (! isEnsureTransaction())
		      return null;

		    try {
		      TransactionManager tm = getTransactionManager();
		      /*
		       * If we are not configured with a transaction manager, we just
		       * have to skip this.
		       */
		      if (tm == null) {
		          logError("Missing Transaction Manager");		      
		        return null;
		      }
		      Transaction t = tm.getTransaction();
		      if (t == null) {
		        tm.begin();
		        t = tm.getTransaction();
		        return t;
		      }
		      return null;
		    }
		    catch (NotSupportedException exc) {		   
		        logError(exc);
		    }
		    catch (SystemException exc) {		     
		        logError(exc);
		    }
		    return null;
	}
	
	
	// Copying Code Ended

	/** Enables Paypal option for user only for single shipping
    *
    * @param BBBOrderImpl order
    * @return boolean either to display paypal option or not
	*/
   private  boolean checkDisplayPPForSingleShipping(final BBBOrderImpl order)
                   throws BBBSystemException, BBBBusinessException {
       if (this.getShippingGroupManager().isMultipleHardgoodShippingGroupsWithRelationships(order)
                       || (this.getShippingGroupManager().getAllHardgoodShippingGroups(order).size() > 1)) {

           this.logDebug("there are multiple hardgoodshippinggroups in order with id " + order.getId());
           return false;
       }
       return true;
   }
   
   public Cookie createCartCookie(Order order, DynamoHttpServletResponse pResponse) {
	   
		JSONObject parentJsonObject = createJSONObjectFromOrder(order, SiteContextManager.getCurrentSiteId());
		// add cookie
		final Cookie cookie = new Cookie(cartCookie, parentJsonObject.toString());
		cookie.setMaxAge(orderCookieAge);
		cookie.setPath(orderCookiePath);
		return cookie;
	}
   
   /** This method creates SFL cookie in case of Move to Cart or Move To SLF operation
   *
   * @param DynamoHttpServletRequest pRequest
   * @return Cookie
	*/

   public Cookie createSFLCookie (DynamoHttpServletRequest pRequest) {
		
		BBBSavedItemsSessionBean savedItemsListBean = (BBBSavedItemsSessionBean) pRequest.resolveName(BBBCoreConstants.SAVEDCOMP);
		Cookie sflCookie = new Cookie(SFL_COOKIE_NAME, BBBCoreConstants.BLANK);
		sflCookie.setMaxAge(0);
		sflCookie.setPath(sflCookiePath);
		JSONObject sflJSONObject = new JSONObject();
		sflJSONObject.element(BBBCoreConstants.SITE_ID, SiteContextManager.getCurrentSiteId());
		List jsonSFLObjectList = createJSONObject(savedItemsListBean.getGiftListVO(), SiteContextManager.getCurrentSiteId());

		if (isLoggingDebug()) {
			logDebug("Json object for SFL created successfully");
		}
		sflJSONObject.element(SAVED_ITEMS_LIST, jsonSFLObjectList);
		
		sflCookie = new Cookie(SFL_COOKIE_NAME, sflJSONObject.toString());
		sflCookie.setMaxAge(sflCookieAge);
		sflCookie.setPath(sflCookiePath);
		return sflCookie;
   }
   
   /* Create JSON object from the order.
	 * 
	 * @param order
	 * @return the JSON object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject createJSONObjectFromOrder(Order order, String siteId) {
		
		JSONObject jsonRootObject = new JSONObject();
		jsonRootObject.element(ORDER_ID, getBbbDesEncryptionTools().encrypt(order.getId()));
		jsonRootObject.element(SITE_ID, siteId);
		
		List jsonCIList = new ArrayList();
		JSONObject jsonCIObject;
		for (Iterator<CommerceItem> iterator = order.getCommerceItems().iterator(); iterator.hasNext();) {
			
			CommerceItem citem = iterator.next();
			if (citem instanceof BBBCommerceItem) {
				//s is skuId, p is prodId, q is qty, r is registryId, st is storeId, b is bts
				jsonCIObject = new JSONObject();
				jsonCIObject.put("s", citem.getCatalogRefId());
				jsonCIObject.put("p", ((BBBCommerceItem) citem).getAuxiliaryData().getProductId());
				jsonCIObject.put("q", citem.getQuantity());
				jsonCIObject.put("b", ((BBBCommerceItem) citem).getBts());
				jsonCIObject.put("st", ((BBBCommerceItem) citem).getStoreId());
				jsonCIObject.put("r", ((BBBCommerceItem) citem).getRegistryId());
				jsonCIObject.put("prc", ((BBBCommerceItem) citem).getPrevPrice());
				jsonCIObject.put("oos", ((BBBCommerceItem) citem).isMsgShownOOS());
				jsonCIObject.put("seqNo", ((BBBCommerceItem) citem).getSeqNumber());
				jsonCIObject.put("refNum", ((BBBCommerceItem) citem).getReferenceNumber());
				jsonCIObject.put(BBBCoreConstants.ORIGINAL_LTL_SHIP_METHOD, ((BBBCommerceItem) citem).getRegistrantShipMethod());
				if(((BBBCommerceItem)citem).getLtlShipMethod()!=null){
					if(((BBBCommerceItem)citem).getLtlShipMethod().equals(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD) && 
							((BBBCommerceItem) citem).getAssemblyItemId()!=null && !((BBBCommerceItem) citem).getAssemblyItemId().isEmpty()) {
						jsonCIObject.put("sm",BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD );
					} else {
						jsonCIObject.put("sm", ((BBBCommerceItem)citem).getLtlShipMethod());
					}
				} else {
					jsonCIObject.put("sm", "");
				}
				jsonCIList.add(jsonCIObject);
			}

		}
		if(isLoggingDebug()){
			logDebug("Json object created successfully" + jsonCIList.toString());
		}
		jsonRootObject.element(ITEM_LIST, jsonCIList);
		
		return jsonRootObject;
	}
	
	/**
	 * Create JSOn object from the Gift List.
	 * 
	 * @param list
	 * @return the JSON object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List createJSONObject(List<GiftListVO> list, String siteId) {

		List jsonCIList = new ArrayList();
		JSONObject jsonCIObject;
		if (list != null) {
			
			for (Iterator<GiftListVO> iterator = list.iterator(); iterator.hasNext();) {
				GiftListVO gitem = iterator.next();
				if (gitem instanceof GiftListVO) {
					//s is skuId, p is prodId, q is qty, r is registryId, st is storeId, b is bts, refNum personalized reference number 
					jsonCIObject = new JSONObject();
					jsonCIObject.put("p", gitem.getProdID());
					jsonCIObject.put("s", gitem.getSkuID());
					jsonCIObject.put("q", gitem.getQuantity());
					jsonCIObject.put("pp", gitem.getPrevPrice());
					jsonCIObject.put("r", gitem.getRegistryID());
					jsonCIObject.put("w", gitem.getWishListItemId());
					jsonCIObject.put("oos", gitem.isMsgShownOOS());
					jsonCIObject.put("sm", gitem.getLtlShipMethod());
					//reference number
					jsonCIObject.put("refNum", gitem.getReferenceNumber());
					jsonCIList.add(jsonCIObject);
				}
			}
		}
		if(isLoggingDebug()){
			logDebug("Json object created successfully");
		}
		return jsonCIList;
	}

	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}
   
}
