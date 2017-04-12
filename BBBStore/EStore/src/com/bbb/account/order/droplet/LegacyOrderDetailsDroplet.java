/**
 * 
 */
package com.bbb.account.order.droplet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.account.vo.order.CartItemDetailInfo;
import com.bbb.account.vo.order.GiftCard;
import com.bbb.account.vo.order.OrderDetailInfoReturn;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.rest.account.order.LegacyOrderDetailDropletResultVO;
import com.bbb.utils.BBBUtility;

public class LegacyOrderDetailsDroplet extends BBBDynamoServlet {
	private OrderDetailsManager mOrderDetailsManager;
	private BBBCatalogTools mCatalogTools;
	private String mShippingCarriers;
	//private static final String CarrierCode = "carrierCode";
	private static final String ORDERID = "orderId";
	private static final String ORDERDETAILS = "orderDetails";
	private static final String OUTPUT = "output";
	private static final String ERROR = "error";
	private static final String EMPTY = "empty";
	private static final String CARRIERURL = "carrierURL";
	public static final String Profile_PATH = "/atg/userprofiling/Profile";
	/**
	 * Method will initiate the GetOrderHistory Web service call to get the
	 * orders based on member id.
	 * 
	 */
	@Override
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {
		/*Profile profile = (Profile) pReq.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		String profileId = profile.getRepositoryId();*/
		OrderDetailInfoReturn objOrderDetailRes = null;
		String orderId = pReq.getParameter(ORDERID);
		String email = pReq.getParameter(BBBCoreConstants.EMAIL);	
		if (!BBBUtility.isEmpty(orderId) && email !=null) {
			try {
				// Get Legacy orders
				objOrderDetailRes = getOrderDetailsManager().getLegacyOrderDetail(orderId);
				if (null != objOrderDetailRes && objOrderDetailRes.getOrderInfo() !=null 
						&& objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getEmailAddr().equalsIgnoreCase(email)) {		
					String currentSiteId = SiteContextManager.getCurrentSiteId();
					String dateFormat = BBBCoreConstants.DATE_FORMAT;

					if (currentSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
						dateFormat = BBBCoreConstants.CA_DATE_FORMAT;
					}
					String orderStatus = objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getOrderStatus();
					try{
					objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setOrderStatus(BBBUtility.getTrackingOrderStatus(orderStatus, objOrderDetailRes,dateFormat));
					} catch (ParseException parseException) {
						logError("ParseException While invoking getTrackingOrderStatus in BBBUtility "+"siteId is " + currentSiteId,parseException);
					}
				
					String orderDt = objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getOrderDt();
					String state = objOrderDetailRes.getShipping().getAddress().getState();
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
					String shippingMethod = objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getShipMethodCD();
					if (BBBUtility.isNotEmpty(shippingMethod)) {
						String shipMethodDescription = null;
						RepositoryItem shippingMethodRepsoitoryItem = mCatalogTools.getShippingMethod(shippingMethod);
						if (shippingMethodRepsoitoryItem != null && shippingMethodRepsoitoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME) != null) {
							shipMethodDescription = (String) shippingMethodRepsoitoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME);
						}
						objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setShipMethod(shipMethodDescription);
					}

					if (BBBUtility.isEmpty(objOrderDetailRes.getShipping().getDeliveryDt())) {
						String expectedDeliveryDate = mCatalogTools.getExpectedDeliveryDate(shippingMethod, state, currentSiteId, orderDate,false);
						objOrderDetailRes.getShipping().setDeliveryDt(expectedDeliveryDate);
					}

					if (currentSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || currentSiteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) {
						if (BBBUtility.isNotEmpty(orderDt)) {
							orderDt = BBBUtility.convertDateToAppFormat(orderDt);
							objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setOrderDt(orderDt);
						}
					}
					
					if (null != objOrderDetailRes.getOrderInfo().getCartDetailInfo().getCartItemDetailList()) {
						List<CartItemDetailInfo> cartItemDetailInfos = objOrderDetailRes.getOrderInfo().getCartDetailInfo().getCartItemDetailList();
						int count = 0;
						for (CartItemDetailInfo cartItemDetailInfo : cartItemDetailInfos) {
							String productId = getCatalogTools().getParentProductForSku(cartItemDetailInfo.getSKU());
							if (BBBUtility.isEmpty(productId)) {
								productId = getCatalogTools().getParentProductForSku(
												cartItemDetailInfo.getSKU(), true);
							}
							objOrderDetailRes.getOrderInfo().getCartDetailInfo()
									.getCartItemDetailList().get(count).setProductId(productId);
							count++;

						}

					}
					

					pReq.setParameter(ORDERDETAILS, objOrderDetailRes);	
					pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
					// Code to get Shipping carrier details
					Map<String, String> carrierURL = null;//new HashMap<String, String>();
					try {
						carrierURL = getCatalogTools().getConfigValueByconfigType(
								getShippingCarriers());					
						if (carrierURL != null) {
							pReq.setParameter(CARRIERURL, carrierURL);						
						}
					} catch (BBBSystemException e) {	
						logError(e);					
					} catch (BBBBusinessException e) {	
						logError(e);					
					}					
				} else {
					pReq.setParameter(EMPTY, EMPTY);
					pReq.serviceLocalParameter(EMPTY, pReq, pRes);
				}
			} catch (BBBBusinessException e) {
				logError(e);
				pReq.setParameter(ERROR, ERROR);
				pReq.serviceLocalParameter(ERROR, pReq, pRes);
				
			} catch (BBBSystemException e) {
				logError(e);
				pReq.setParameter(ERROR, ERROR);
				pReq.setParameter(ORDERDETAILS, objOrderDetailRes);
				pReq.serviceLocalParameter(ERROR, pReq, pRes);
			}			

		}	

	}	

	/**
	 * @param orderId : Id of the order whose details would be returned by this method
	 * @return LegacyOrderDetailDropletResultVO : Contains the values set by the "LegacyOrderDetailsDroplet".
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public LegacyOrderDetailDropletResultVO getLegacyOrderDetails(
			String orderId) throws BBBSystemException {
		logDebug("Inside class: OrderDetailsManager,  method :getLegacyOrderDetailsForUser");
		BBBPerformanceMonitor.start("OrderDetailsManager"
				+ " getLegacyOrderDetailsForUser");
		LegacyOrderDetailDropletResultVO orderAndCarrierData = new LegacyOrderDetailDropletResultVO();
		try{
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
			final Profile profile = (Profile) pRequest.resolveName(Profile_PATH);
			String email = (String) profile.getPropertyValue("email");
			pRequest.setParameter("email", email);
			pRequest.setParameter("orderId", orderId);
			try{
				//calls the "service" method to fetch the details of the legacy order
				service(pRequest, pResponse);
			}catch (ServletException | IOException e) {
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_Legacy_Details, "Exception occurred while fetching the legacy order details",e);
			}
			if("error".equalsIgnoreCase((String)pRequest
					.getObjectParameter("error"))){
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_GET_ORDER_DETAILS_1001, "Error occurred while fetching order details");
			}
			OrderDetailInfoReturn objOrderDetailRes = (OrderDetailInfoReturn) pRequest
					.getObjectParameter("orderDetails");
			if(objOrderDetailRes!=null && objOrderDetailRes.getPayments()!=null && objOrderDetailRes.getPayments().getGiftCards()!=null
					&& !objOrderDetailRes.getPayments().getGiftCards().isEmpty()){
				List<GiftCard> giftCardList=objOrderDetailRes.getPayments().getGiftCards();
				List<GiftCard> newGiftCardList=new ArrayList<GiftCard>();
				
				for(GiftCard giftCard:giftCardList){
					String maskedGiftCardNo=BBBUtility.maskCrediCardNumber(giftCard.getCardNum());
					String maskedGiftPin=BBBUtility.maskAllDigits(giftCard.getPin());
					giftCard.setCardNum(maskedGiftCardNo);
					giftCard.setPin(maskedGiftPin);
					newGiftCardList.add(giftCard);
					
				}
				objOrderDetailRes.getPayments().setGiftCards(newGiftCardList);
			}
			Map<String, String> carrierURL = (Map<String, String>) pRequest
					.getObjectParameter("carrierURL");
			orderAndCarrierData.setOrderDetailInfoReturn(objOrderDetailRes);
			orderAndCarrierData.setCarrierURL(carrierURL);
		}finally{
			BBBPerformanceMonitor.end("OrderDetailsManager"
					+ " getLegacyOrderDetailsForUser");
		}
		return orderAndCarrierData;
	}
	
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the shippingCarriers
	 */
	public String getShippingCarriers() {
		return mShippingCarriers;
	}

	/**
	 * @param pShippingCarriers
	 *            the shippingCarriers to set
	 */
	public void setShippingCarriers(String pShippingCarriers) {
		mShippingCarriers = pShippingCarriers;
	}

	/**
	 * @return the orderDetailsManager
	 */
	public OrderDetailsManager getOrderDetailsManager() {
		return mOrderDetailsManager;
	}

	/**
	 * @param orderDetailsManager the orderDetailsManager to set
	 */
	public void setOrderDetailsManager(OrderDetailsManager orderDetailsManager) {
		mOrderDetailsManager = orderDetailsManager;
	}

}
