/**
 * 
 */
package com.bbb.account.order.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;


import atg.multisite.SiteContextManager;

import com.bbb.account.vo.order.GetOrderHistory2ResVO;
import com.bbb.account.vo.order.GetOrderHistoryResVO;
import com.bbb.account.vo.order.OrderHistory2ReqVO;
import com.bbb.account.vo.order.OrderHistory2ResVO;
import com.bbb.account.vo.order.OrderHistoryReqVO;
import com.bbb.account.vo.order.OrderHistoryResVO;
import com.bbb.account.vo.order.ShipmentTrackingInfoVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.TrackingInfo;
import com.bbb.utils.BBBUtility;


/**
 * @author jsidhu 
 * 
 * This class will interact with WSFramework and get the order
 *         history details from webservice
 */
public class OrderHistoryManager extends BBBGenericService {
	private String mServiceName;
	private BBBCatalogTools mCatalogTools;
	private String mServiceNameOrderHistory2;
	
	public String getServiceNameOrderHistory2() {
		return mServiceNameOrderHistory2;
	}

	public void setServiceNameOrderHistory2(String mServiceNameOrderHistory2) {
		this.mServiceNameOrderHistory2 = mServiceNameOrderHistory2;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	
	/**
	 * 
	 * @param pMemberId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 *             Get Legacy order list based on the member id
	 */
	public List<OrderHistoryResVO> getLegacyOrder(String profileId, String pMemberId)
			throws BBBBusinessException, BBBSystemException {
		List<OrderHistoryResVO> orderList = null;
		if (profileId!=null && !BBBUtility.isEmpty(profileId)) {
			OrderHistoryReqVO orderHistoryReq = generateRequest(profileId, pMemberId);
			GetOrderHistoryResVO getOrderHistoryResVO;
			getOrderHistoryResVO = (GetOrderHistoryResVO) ServiceHandlerUtil
					.invoke(orderHistoryReq);
			if (getOrderHistoryResVO != null) {
				orderList = getOrderHistoryResVO.getOrderList();
				if ((getOrderHistoryResVO.getErrorStatus()==null || (getOrderHistoryResVO.getErrorStatus()!=null && getOrderHistoryResVO.getErrorStatus().isErrorExists()))
						&& orderList == null) {
					throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1310,
							"Legacy WebService returns an error.");
				}
			}
		}
		return orderList;
	}
	
	public List<OrderHistory2ResVO> getFilteredLegacyOrders(String profileId, String pMemberId, String pOrderType)
	throws BBBBusinessException, BBBSystemException {
		List<OrderHistory2ResVO> orderList = null;
		if (profileId!=null && !BBBUtility.isEmpty(profileId)) {
			OrderHistory2ReqVO orderHistory2Req = generateOrderHistory2Request(profileId, pMemberId, pOrderType);
			GetOrderHistory2ResVO getOrderHistory2ResVO;
			getOrderHistory2ResVO = (GetOrderHistory2ResVO) ServiceHandlerUtil
					.invoke(orderHistory2Req);
			if (getOrderHistory2ResVO != null) {
				if((!getOrderHistory2ResVO.webServiceError) && getOrderHistory2ResVO.getErrorStatus()!=null && !getOrderHistory2ResVO.getErrorStatus().isErrorExists())
				orderList = getOrderHistory2ResVO.getOrderList();
			}
		}
		return orderList;
		}
	

	/**
	 * 
	 * @param pOrdersATGList
	 * @return List<OrderHistoryResVO>
	 * 
	 * Populate the ATG orders into Custom VO to access it directly from JSP
	 */
	public List<OrderHistoryResVO> getProfileOrders(List<BBBOrder> pOrdersATGList) {
		OrderHistoryResVO orderhistory = null;
		
		List<OrderHistoryResVO> orderList = new ArrayList<OrderHistoryResVO>();
		ArrayList<ShipmentTrackingInfoVO> shipmentList = null;
		if (pOrdersATGList != null && pOrdersATGList.size() > 0) {
			for (BBBOrder orders : pOrdersATGList) {
				if(orders.getState()!=0)//0 = Incomplete order
				{
					orderhistory = new OrderHistoryResVO();
					shipmentList = new ArrayList<ShipmentTrackingInfoVO>();
					
					orderhistory.setOrderNumber(orders.getId());
					orderhistory.setOnlineOrderNumber(orders.getOnlineOrderNumber());
					orderhistory.setBopusOrderNumber(orders.getBopusOrderNumber());
					orderhistory.setOrderDate(orders.getSubmittedDate());
					
					String orderStatus = ((BBBOrderImpl)orders).getStateDetail();
					if (orderStatus != null && StringUtils.isNotEmpty(orderStatus)) {
						orderhistory.setOrderStatus(((BBBOrderImpl)orders).getStateDetail());
					} else {
						orderhistory.setOrderStatus(((BBBOrderImpl)orders).getStateAsString());
					}
					
					//Order type flag kept to order details page
					orderhistory.setOrderType(BBBAccountConstants.ORDER_TYPE_ATG);
					// TODO: Add tracking info details from Order object
				    //Fetch List of TrackingInfo from BBBOrder
	
					List<TrackingInfo> trackingInfoList = null;
					trackingInfoList = orders.getTrackingInfos();
					if (trackingInfoList != null) {
						Iterator<TrackingInfo> iterateTrackingInfo = trackingInfoList.iterator();
						while (iterateTrackingInfo.hasNext()) {
							TrackingInfo trackingInfoObj = (TrackingInfo) iterateTrackingInfo.next();
							ShipmentTrackingInfoVO shipment = new ShipmentTrackingInfoVO();
							shipment.setCarrier(trackingInfoObj.getCarrierCode());
							shipment.setTrackingNumber(trackingInfoObj.getTrackingNumber());
							shipmentList.add(shipment);
						}
						orderhistory.setShipment(shipmentList);
					}
					// orderhistory.setShipment(orders.getShippingGroup()OrderHistoryManager.getShipment())
					// Shipment will be a list in order object so directly it can be
					// set in SetShipment()
					orderList.add(orderhistory);
				}
			}
		}
		return orderList;
	}
	

	/**
	 * 
	 * @param pMemberId
	 * @return OrderHistoryReqVO
	 * To generate the webservice request
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	private OrderHistoryReqVO generateRequest(String pProfileId, String pMemberId) throws BBBSystemException, BBBBusinessException {
		OrderHistoryReqVO orderHistoryReq = new OrderHistoryReqVO();
		String siteId = SiteContextManager.getCurrentSiteId();
		
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).size()>BBBCoreConstants.ZERO) {
			orderHistoryReq.setSiteFlag(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1268,"Site Flag for webservices is not set for siteId: " + siteId);
			}
			
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size()>BBBCoreConstants.ZERO) {
			orderHistoryReq.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1269,"Webservices Token for webservices is not set");
			}

		orderHistoryReq.setMbrNum(pMemberId);
		orderHistoryReq.setMemberID(pMemberId);
		orderHistoryReq.setProfileId(pProfileId);
		orderHistoryReq.setServiceName(getServiceName());
		return orderHistoryReq;
	}
	
	
	private OrderHistory2ReqVO generateOrderHistory2Request(String pProfileId, String pMemberId, String pOrderType) throws BBBSystemException, BBBBusinessException {
		OrderHistory2ReqVO orderHistory2Req = new OrderHistory2ReqVO();
		String siteId = SiteContextManager.getCurrentSiteId();
		
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).size()>BBBCoreConstants.ZERO) {
			orderHistory2Req.setSiteFlag(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1268,"Site Flag for webservices is not set for siteId: " + siteId);
			}
			
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)!=null 
				&& getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size()>BBBCoreConstants.ZERO) {
			orderHistory2Req.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1269,"Webservices Token for webservices is not set");
			}

		orderHistory2Req.setMbrNum(pMemberId);
		orderHistory2Req.setMemberID(pMemberId);
		orderHistory2Req.setProfileId(pProfileId);
		orderHistory2Req.setServiceName(getServiceNameOrderHistory2());
		orderHistory2Req.setOrderType(pOrderType);
		return orderHistory2Req;
	}



	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return mServiceName;
	}


	/**
	 * @param pServiceName the serviceName to set
	 */
	public void setServiceName(String pServiceName) {
		mServiceName = pServiceName;
	}
	
	/**
	 * 
	 * @param pOrdersATGList
	 * @return List<OrderHistory2ResVO>
	 * 
	 * Populate the ATG orders into Custom VO to access it directly from JSP
	 */
	public List<OrderHistory2ResVO> getProfileOrdersWithOrdTotal(List<BBBOrder> pOrdersATGList, String pOrderType) {
		OrderHistory2ResVO orderhistory = null;
		
		List<OrderHistory2ResVO> orderList = new ArrayList<OrderHistory2ResVO>();
		if (pOrdersATGList != null && pOrdersATGList.size() > 0) {
			for (BBBOrder orders : pOrdersATGList) {
				if(orders.getState()!=0 )//&& pOrderType.equals("TBS") && !StringUtils.isBlank(orders.getTBSAssociateID()))//0 = Incomplete order
				{
					orderhistory = new OrderHistory2ResVO();
					ArrayList<ShipmentTrackingInfoVO> shipmentList = new ArrayList<ShipmentTrackingInfoVO>();
					double totalAmount =0.00;
					try {
					orderhistory.setOrderNumber(orders.getId());
					orderhistory.setOnlineOrderNumber(orders.getOnlineOrderNumber());
					orderhistory.setBopusOrderNumber(orders.getBopusOrderNumber());
					orderhistory.setOrderDate(orders.getSubmittedDate());
					orderhistory.setWsOrder(false);
					if(orders.getPriceInfo()!=null){
							BBBOrderPriceInfo orderPriceInfo = (BBBOrderPriceInfo) orders.getPriceInfo();
							orderhistory.setTotalAmt(orderPriceInfo.getOnlineStoreTotal());
						}
					else{
						orderhistory.setTotalAmt(totalAmount);	
					}
					String orderStatus = ((BBBOrderImpl)orders).getStateDetail();
					if (orderStatus != null && !StringUtils.isBlank(orderStatus)) {
						orderhistory.setOrderStatus(((BBBOrderImpl)orders).getStateDetail());
					} else {
						orderhistory.setOrderStatus(((BBBOrderImpl)orders).getStateAsString());
					}
					
					//Order type flag kept to order details page
					//orderhistory.setOrderType(BBBAccountConstants.ORDER_TYPE_ATG);
	
					List<TrackingInfo> trackingInfoList = null;
					trackingInfoList = orders.getTrackingInfos();
					if (trackingInfoList != null) {
						Iterator<TrackingInfo> iterateTrackingInfo = trackingInfoList.iterator();
						while (iterateTrackingInfo.hasNext()) {
							TrackingInfo trackingInfoObj = (TrackingInfo) iterateTrackingInfo.next();
							ShipmentTrackingInfoVO shipment = new ShipmentTrackingInfoVO();
							shipment.setCarrier(trackingInfoObj.getCarrierCode());
							shipment.setTrackingNumber(trackingInfoObj.getTrackingNumber());
							shipmentList.add(shipment);
						}
						if(!shipmentList.isEmpty()){
							orderhistory.setShipment(shipmentList);
						}
					}
					orderList.add(orderhistory);
					} catch (Exception e) {
						logError("OrderHistoryManager.getProfileOrdersWithOrdTotal : Error while fetching order details for order no - " + orders.getId(),e);
					}
					shipmentList = null;
				}
				/*if(orders.getState()!=0 && pOrderType.equals("ONL") && StringUtils.isBlank(orders.getTBSAssociateID()))//0 = Incomplete order
				{
					orderhistory = new OrderHistory2ResVO();
					ArrayList<ShipmentTrackingInfoVO> shipmentList = new ArrayList<ShipmentTrackingInfoVO>();
					double totalAmount =0.00;
					
					orderhistory.setOrderNumber(orders.getId());
					orderhistory.setOnlineOrderNumber(orders.getOnlineOrderNumber());
					orderhistory.setBopusOrderNumber(orders.getBopusOrderNumber());
					orderhistory.setOrderDate(orders.getSubmittedDate());
					orderhistory.setWsOrder(false);
					if(orders.getPriceInfo()!=null){
							BBBOrderPriceInfo orderPriceInfo = (BBBOrderPriceInfo) orders.getPriceInfo();
							orderhistory.setTotalAmt(orderPriceInfo.getOnlineStoreTotal());
						}
					else
					orderhistory.setTotalAmt(totalAmount);	
					String orderStatus = ((BBBOrderImpl)orders).getStateDetail();
					if (orderStatus != null && !StringUtils.isBlank(orderStatus)) {
						orderhistory.setOrderStatus(((BBBOrderImpl)orders).getStateDetail());
					} else {
						orderhistory.setOrderStatus(((BBBOrderImpl)orders).getStateAsString());
					}
					
					//Order type flag kept to order details page
					orderhistory.setOrderType(BBBAccountConstants.ORDER_TYPE_ATG);
	
					List<TrackingInfo> trackingInfoList = null;
					trackingInfoList = orders.getTrackingInfos();
					if (trackingInfoList != null) {
						Iterator<TrackingInfo> iterateTrackingInfo = trackingInfoList.iterator();
						while (iterateTrackingInfo.hasNext()) {
							TrackingInfo trackingInfoObj = (TrackingInfo) iterateTrackingInfo.next();
							ShipmentTrackingInfoVO shipment = new ShipmentTrackingInfoVO();
							shipment.setCarrier(trackingInfoObj.getCarrierCode());
							shipment.setTrackingNumber(trackingInfoObj.getTrackingNumber());
							shipmentList.add(shipment);
						}
						if(!shipmentList.isEmpty()){
							orderhistory.setShipment(shipmentList);
						}
					}
					orderList.add(orderhistory);
					shipmentList = null;
				}*/
			}
		}
		return orderList;
	}
}
