package com.bbb.rest.account.order;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.order.droplet.LegacyOrderDetailsDroplet;
import com.bbb.account.vo.order.OrderDetailInfoReturn;
import com.bbb.account.vo.order.OrderHistory2ResVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class BBBRestAccountOrderManagerImpl extends BBBGenericService {
	public static final String Profile_PATH = "/atg/userprofiling/Profile";
	private static final String ORDERLIST = "OrderList";
	private static final String CARRIERURL = "carrierURL";
	private static final String NUMORDERS = "numOrders";	
	private LegacyOrderDetailsDroplet legacyOrderDetailsDroplet;
	
	public LegacyOrderDetailsDroplet getLegacyOrderDetailsDroplet() {
		return legacyOrderDetailsDroplet;
	}

	public void setLegacyOrderDetailsDroplet(
			LegacyOrderDetailsDroplet legacyOrderDetailsDroplet) {
		this.legacyOrderDetailsDroplet = legacyOrderDetailsDroplet;
	}
	
	
	/**
	 * @return OrderHistoryDropletResultVO:  Contains the values set by the "OrderHistoryDroplet".
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public OrderHistoryDropletResultVO getAllOrdersForUser() throws BBBSystemException {

		logDebug("Inside class: OrderDetailsManager,  method :getAllOrdersForUser");
		BBBPerformanceMonitor.start("OrderDetailsManager"
				+ " getAllOrdersForUser");
		OrderHistoryDropletResultVO resultVO = new OrderHistoryDropletResultVO();
		List<OrderHistory2ResVO> legacyOrderList = null;
		try{
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();
			pRequest.setParameter("siteId", SiteContextManager.getCurrentSiteId());
			try{
				//calls the "orderHistoryDroplet" to fetch the ATG+Legacy orders
				getLegacyOrderDetailsDroplet().service(pRequest, pResponse);
			}catch (ServletException e) {
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SERVLET_DROPLET, "Servlet exception occurred in the called droplet");
			}catch (IOException e) {
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_IO_DROPLET,"IO exception occurred in the called droplet");
			}
			legacyOrderList =(List<OrderHistory2ResVO>) pRequest.getObjectParameter(ORDERLIST);
			Map<String, String> carrierURL = (Map<String, String>) pRequest.getObjectParameter(CARRIERURL);
			int numOrder = (Integer) pRequest.getObjectParameter(NUMORDERS);
			resultVO.setOrderList(legacyOrderList);
			resultVO.setCarrierURL(carrierURL);
			resultVO.setNumOrders(numOrder);
		}finally{ 		
			BBBPerformanceMonitor.end("OrderDetailsManager"
					+ " getAllOrdersForUser");
		}
		return resultVO;
	}
	
	

	/**
	 * @param orderId : Id of the order whose details would be returned by this method
	 * @return LegacyOrderDetailDropletResultVO : Contains the values set by the "LegacyOrderDetailsDroplet".
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public LegacyOrderDetailDropletResultVO getLegacyOrderDetailsForUser(
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
				//calls the "LegacyOrderDetailsDroplet" to fetch the details of the legacy order
				getLegacyOrderDetailsDroplet().service(pRequest, pResponse);
			}catch (ServletException e) {
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SERVLET_DROPLET, "Servlet exception occurred in the called droplet");
			}catch (IOException e) {
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_IO_DROPLET,"IO exception occurred in the called droplet");
			}
			if("error".equalsIgnoreCase((String)pRequest
					.getObjectParameter("error"))){
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_GET_ORDER_DETAILS_1001, "Error occurred while fetching order details");
			}
			OrderDetailInfoReturn objOrderDetailRes = (OrderDetailInfoReturn) pRequest
					.getObjectParameter("orderDetails");
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

}
