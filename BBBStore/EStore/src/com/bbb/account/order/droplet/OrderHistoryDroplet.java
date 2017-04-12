/**
 * 
 */
package com.bbb.account.order.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderLookup;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ComponentName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.order.manager.OrderHistoryManager;
import com.bbb.account.vo.order.OrderHistory2ResVO;
import com.bbb.account.vo.order.ShipmentTrackingInfoVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBTagConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.rest.account.order.OrderHistoryDropletResultVO;
import com.bbb.utils.BBBUtility;

public class OrderHistoryDroplet extends OrderLookup {
	private OrderHistoryManager mOrderHistoryManager;
	private BBBCatalogTools mCatalogTools;
	private String mShippingCarriers;
	private String mNumOrder;
	//private static final String CarrierCode = "carrierCode";
	private static final String RESULT = "result";
	private static final String ORDERLIST = "OrderList";
	private static final String ORDEROUTPUT = "orderOutput";
	private static final String ORDEROUTPUTSTART = "orderOutputStart";
	private static final String ORDEREMPTY = "orderEmpty";
	private static final String CARRIERURL = "carrierURL";
	private static final String IMAGE = "_Image";
	private static final String TRACK = "_Track";
	
	/**
	 * Method will initiate the GetOrderHistory Web service call to get the
	 * orders based on member id.
	 * 
	 */
	@Override
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {

		Profile profile = (Profile) pReq.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		String profileId = profile.getRepositoryId();
		String siteId= pReq.getParameter(BBBCoreConstants.SITE_ID);	
		String orderType= pReq.getParameter(BBBCoreConstants.ORDER_TYPE);
		List<OrderHistory2ResVO> orderList = new ArrayList<OrderHistory2ResVO>();
		List<OrderHistory2ResVO> legacyOrderList = null;
		String memberId = "";
		
		String sisterSiteId = null;
		if(siteId.startsWith("TBS") ) {
			sisterSiteId = siteId.substring(4);
		}
		else {
			sisterSiteId = "TBS_" + siteId;
		}
		
		// Get Profile based ATG orders
		if (profileId != null && !profile.isTransient()) {

			RepositoryItem siteItem = (RepositoryItem) profile.getDataSource();
			if(siteItem!=null && siteItem.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM)!=null)
			{
				memberId = (String) ((RepositoryItem)((Map)siteItem.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM)).get(siteId)).getPropertyValue("memberId");
				pReq.setParameter("numOrders", Integer.valueOf(getNumOrder()));
			
				/** Invalidate last order*/
				invalidateLastOder(siteId, pReq);
				
				ArrayList<String> siteIDs = new ArrayList<String>();
				siteIDs.add(siteId);
				siteIDs.add(sisterSiteId);
				pReq.setParameter("siteIds", siteIDs);
				
				searchByUserId(pReq, pRes, profileId);
				try {
					if ((orderType!=null && orderType.equalsIgnoreCase("ONL")) && null != pReq.getObjectParameter(RESULT)) {
						List<BBBOrder> ordersATGList = (ArrayList<BBBOrder>) pReq.getObjectParameter(RESULT);
						// Update the list with Legacy + ATG orders
						orderList = getOrderHistoryManager().getProfileOrdersWithOrdTotal(ordersATGList, orderType);
					}
				
					String orderHistroyOn = getCatalogTools().getThirdPartyTagStatus(siteId, getCatalogTools(), BBBTagConstants.OORDER_HISTORY_TAG);
					if(BBBUtility.isNotEmpty(orderHistroyOn) && orderHistroyOn.equalsIgnoreCase(BBBCoreConstants.TRUE) ){//&& !orderType.startsWith("TBS")){
						// Get Legacy orders
						if(profileId!=null && !BBBUtility.isEmpty(profileId))
						{
							if (memberId == null) {
								memberId = "";
							}
							logDebug("Profile Id is : "+ profileId);
							logDebug("MemberId Id is : "+ memberId);
							legacyOrderList = getOrderHistoryManager().getFilteredLegacyOrders(profileId, memberId,orderType);
						}
						
						// Merge ATG & Legacy Orders
						if (legacyOrderList != null && legacyOrderList.size()>0) {
							orderList.addAll(legacyOrderList);
						}
						
					}
					
				} catch (BBBBusinessException e) {
					
						logError(e);
					
					if (orderList == null) {
						pReq.serviceLocalParameter(ERROR, pReq, pRes);
					}
				} catch (Exception e) {
					
						logError(e);
					
						pReq.setParameter(ERROR, ERROR); //Added for Mobile project (REST API)
						if (orderList.size()==0) {
							pReq.serviceLocalParameter(ERROR, pReq, pRes);
							return;
						}
			}
			
			// Sort List
			Collections.sort(orderList);
			if(orderList!=null && !orderList.isEmpty()){
				Iterator<OrderHistory2ResVO> itr = orderList.iterator();
				while(itr.hasNext()){
					OrderHistory2ResVO vo = itr.next();
					if(vo.getOrderDate() == null || StringUtils.isEmpty(vo.getOrderDate().toString())){
						itr.remove();
					}
				}
			}
			if (null != orderList && orderList.size() > 0) {
				
				pReq.setParameter(ORDERLIST, orderList);	
				pReq.serviceLocalParameter(ORDEROUTPUTSTART, pReq, pRes);
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
				pReq.serviceLocalParameter(ORDEROUTPUT, pReq, pRes);
			} else {
				pReq.serviceLocalParameter(ORDEREMPTY, pReq, pRes);
			}
			}
		}	
	}
	
	
	/**
	 * Get all( atg + legacy) orders for logged in user.
	 * 
	 * @return OrderHistoryDropletResultVO
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public OrderHistoryDropletResultVO getAllOrders()throws BBBSystemException{

		OrderHistoryDropletResultVO resultVO = new OrderHistoryDropletResultVO();
		resultVO =getFilteredOrder("ONL");
		return resultVO;
	}
	
	public OrderHistoryDropletResultVO getAllTBSOrders()throws BBBSystemException{

		OrderHistoryDropletResultVO resultVO = new OrderHistoryDropletResultVO();
		resultVO =getFilteredOrder("TBS");
		return resultVO;
	}

	public OrderHistoryDropletResultVO getFilteredOrder(String orderType) throws BBBSystemException {
		logDebug("Inside class: OrderHistoryDroplet,  method :getFilteredOrder");
		BBBPerformanceMonitor.start("OrderHistoryDroplet"
				+ " getFilteredOrder");
		OrderHistoryDropletResultVO resultVO = new OrderHistoryDropletResultVO();
		List<OrderHistory2ResVO> legacyOrderList = null;
		try{
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();
			pRequest.setParameter("siteId", SiteContextManager.getCurrentSiteId());  
			pRequest.setParameter(BBBCoreConstants.ORDER_TYPE, orderType);
			try {
				// calls the "orderHistoryDroplet" to fetch the ATG+Legacy
				// orders
				service(pRequest, pResponse);
			} catch (ServletException e) {
				throw new BBBSystemException(
						BBBCoreErrorConstants.ERROR_SERVLET_DROPLET,
						"Exception occurred while fetching data",e);
			} catch (IOException e) {
				throw new BBBSystemException(
						BBBCoreErrorConstants.ERROR_IO_DROPLET,
						"Exception occurred while fetching data",e);
			}

			legacyOrderList = (List<OrderHistory2ResVO>) pRequest
					.getObjectParameter(ORDERLIST);
			Map<String, String> carrierURL = (Map<String, String>) pRequest
					.getObjectParameter(CARRIERURL);
			int numOrder = (Integer) pRequest.getObjectParameter(NUMORDERS);
			
			if(legacyOrderList != null){
				for(OrderHistory2ResVO order : legacyOrderList){
				if(order != null){
					List<ShipmentTrackingInfoVO> shipmentTrackingInfoList = order.getShipment();
					if(shipmentTrackingInfoList != null){
						for(ShipmentTrackingInfoVO shipmentTrackingInfo : shipmentTrackingInfoList)
						{
							if(shipmentTrackingInfo != null){
								String carrierImage = shipmentTrackingInfo.getCarrier() + IMAGE;
								String carrierTrack = shipmentTrackingInfo.getCarrier() + TRACK;
								if(carrierURL != null){
									String ImageURL = carrierURL.get(carrierImage);
									String TrackUrl = carrierURL.get(carrierTrack);
									shipmentTrackingInfo.setVendorTrackingURL(TrackUrl + shipmentTrackingInfo.getTrackingNumber());
									shipmentTrackingInfo.setVendorImageURL(ImageURL);
								}
								else{
									logDebug("Vendor Tracking URL is null as carrierURL is Empty");}
							}
						}
					}
					else{
						logDebug("Shipment Tracking List is Empty for order: " + order.getOnlineOrderNumber());}
				}}
				}
				else{
					logDebug("Order List is empty");}
			resultVO.setOrderList(legacyOrderList);
			resultVO.setCarrierURL(carrierURL);
			resultVO.setNumOrders(numOrder);
		}finally{ 		
			BBBPerformanceMonitor.end("OrderHistoryDroplet:"
					+ " getFilteredOrder");
		}
		return resultVO;
	}
	/**
	 * @return the orderHistoryManager
	 */
	public OrderHistoryManager getOrderHistoryManager() {
		return mOrderHistoryManager;
	}

	/**
	 * @param pOrderHistoryManager
	 *            the orderHistoryManager to set
	 */
	public void setOrderHistoryManager(OrderHistoryManager pOrderHistoryManager) {
		mOrderHistoryManager = pOrderHistoryManager;
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
	 * @return the mNumOrder
	 */
	public String getNumOrder() {
		return mNumOrder;
	}


	/**
	 * @param mNumOrder the mNumOrder to set
	 */
	public void setNumOrder(String mNumOrder) {
		this.mNumOrder = mNumOrder;
	}


	//-------------------------------------
	// property: orderTools
	//-------------------------------------
	private BBBOrderTools mOrderTools = null;

	/**
	* Returns the mOrderTools 
	*/
	public BBBOrderTools getOrderTools() {
		return mOrderTools;
	}

	/**
	* Sets the mOrderTools name.
	*/
	public void setOrderTools(BBBOrderTools pOrderTools) {
		mOrderTools = pOrderTools;
	}
	
	/**
	 * This method invalidates the last order if any 
	 * 
	 * @param siteId
	 */
	protected void invalidateLastOder(String siteId, DynamoHttpServletRequest pReq){
		
		String invalidateLastOrderTagOn = BBBCoreConstants.FALSE;
		String tagValue = null;
		try {
			tagValue = getCatalogTools().getThirdPartyTagStatus(siteId, getCatalogTools(), BBBTagConstants.INVALIDATE_LAST_ORDER_TAG);
		} catch (BBBBusinessException e1) {
			logError("MSG=[Business exception while fetching tag fron config keys ]"+BBBTagConstants.INVALIDATE_LAST_ORDER_TAG,e1);
		} catch (BBBSystemException e1) {
			logError("MSG=[System exception while fetching tag fron config keys ]"+BBBTagConstants.INVALIDATE_LAST_ORDER_TAG,e1);
		}
		
		if (!StringUtils.isBlank(tagValue)) {
			invalidateLastOrderTagOn = tagValue;
		}
		if ( null != invalidateLastOrderTagOn && invalidateLastOrderTagOn.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
			OrderHolder orderholder = (OrderHolder) pReq.resolveName(BBBCoreConstants.ATG_SHOPPING_CART);
			if(orderholder !=null ){
				Order lastOrder = orderholder.getLast();
				if(lastOrder != null){
					logDebug("MSG=[Invalidating cache for order:]"+ lastOrder.getId());
					getOrderTools().invalidateOrderCache(lastOrder);
				}
			}
		}
		
	}
	
    @Override
    public final void logDebug(final String message) {
        if (this.isLoggingDebug()) {
            this.logDebug(message, null);
        }
    }

}
