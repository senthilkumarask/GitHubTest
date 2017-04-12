/**
 * 
 */
package com.bbb.integration.csr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.OrderLookup;
import atg.commerce.order.OrderManager;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileTools;
import com.bbb.account.order.manager.OrderHistoryManager;
import com.bbb.account.vo.order.OrderHistoryResVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author akhat1
 * 
 * This droplet returns the ATG orders and legacy orders list for
 * a given profile or for a given orderId and overrides the default enable security 
 * flag of OrderLookUp class
 */
public class CSRGetOrderListDroplet extends OrderLookup {
	private OrderHistoryManager mOrderHistoryManager;
	private BBBCatalogTools mCatalogTools;
	//private static final String CarrierCode = "carrierCode";
	private static final String RESULT = "result";
	private static final String ORDERLIST = "OrderList";
	private static final String ORDEROUTPUT = "orderOutput";
	private static final String ORDEROUTPUTSTART = "orderOutputStart";
	private static final String ORDEREMPTY = "orderEmpty";
	private static final String NUMORDER = "numOrders";
	private BBBProfileTools mProfileTools;
	private static final String ORDERID = "orderId";
	private String mNumOrder;
	private OrderManager orderManager;
	private BBBOrderTools mOrderTools;
	
	/**
	 * @return the orderHistoryManager
	 */
	public OrderHistoryManager getOrderHistoryManager() {
		return this.mOrderHistoryManager;
	}

	/**
	 * @param pOrderHistoryManager
	 *            the orderHistoryManager to set
	 */
	public void setOrderHistoryManager(OrderHistoryManager pOrderHistoryManager) {
		this.mOrderHistoryManager = pOrderHistoryManager;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	* Returns the mOrderTools 
	*/
	public BBBOrderTools getOrderTools() {
		return this.mOrderTools;
	}

	/**
	* Sets the mOrderTools name.
	*/
	public void setOrderTools(BBBOrderTools pOrderTools) {
		this.mOrderTools = pOrderTools;
	}
	
	@Override
	public OrderManager getOrderManager() {
		return this.orderManager;
	}


	@Override
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}


	/**
	 * @return the mNumOrder
	 */
	public String getNumOrder() {
		return this.mNumOrder;
	}


	/**
	 * @param mNumOrder the mNumOrder to set
	 */
	public void setNumOrder(String mNumOrder) {
		this.mNumOrder = mNumOrder;
	}
	
	
	/**
	 * @return mProfileTools
	 */
	public BBBProfileTools getProfileTools() {
		return this.mProfileTools;
	}

	/** 
	 * @param pProfileTools the pProfileTools to set
	 */
	public void setProfileTools(final BBBProfileTools pProfileTools) {
		this.mProfileTools = pProfileTools;
	}
	
	/**
	 * Method will initiate the GetOrderHistory Web service call to get the
	 * orders based on member id.
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {

		RepositoryItem profile = null;
		String profileId = null;
		String emailId = null;
		String orderId = null;
		if(pReq.getParameter(BBBCoreConstants.EMAIL)!=null){
			emailId = pReq.getParameter(BBBCoreConstants.EMAIL);
			profile = this.getProfileTools().getItemFromEmail(emailId);
			profileId = profile.getRepositoryId();
		}else{
			if(pReq.getParameter(ORDERID)!=null){
				orderId = pReq.getParameter(ORDERID);
			}
		}
		String siteId = SiteContextManager.getCurrentSiteId();
		List<OrderHistoryResVO> orderList = new ArrayList<OrderHistoryResVO>();
		List<OrderHistoryResVO> legacyOrderList = null;
		String memberId = BBBCoreConstants.BLANK;
		
		// Get Profile based ATG orders
			if(profile!=null && profile.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM)!=null){
				
				if(null != ((RepositoryItem)((Map)profile.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM)).get(siteId))){
					memberId = (String) ((RepositoryItem)((Map)profile.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM)).get(siteId)).getPropertyValue("memberId");
				}
				pReq.setParameter(NUMORDER, getNumOrder());
				/** Invalidate last order*/
				
				searchByUserId(pReq, pRes, profileId);
				if (null != pReq.getObjectParameter(RESULT)) {
					List<BBBOrder> ordersATGList = (ArrayList<BBBOrder>) pReq.getObjectParameter(RESULT);
					// Update the list with Legacy + ATG orders
					orderList = getOrderHistoryManager().getProfileOrders(ordersATGList);
				}
				try {
						// Get Legacy orders
						if (memberId == null) {
							memberId = BBBCoreConstants.BLANK;
						}
						logDebug("Profile Id is : "+ profileId);
						logDebug("MemberId Id is : "+ memberId);
						legacyOrderList = getOrderHistoryManager().getLegacyOrder(profileId, memberId);
						
						// Merge ATG & Legacy Orders
						if (legacyOrderList != null && legacyOrderList.size()>0) {
							orderList.addAll(legacyOrderList);
						}
						
				} catch (BBBBusinessException e) {
					if (isLoggingError()) {
						logError("Business Exception in Service Method of CSRGetOrderListDroplet for ProfileId"+ profileId + "MemberId" + memberId + e);
					}
					if (orderList == null) {
						pReq.serviceLocalParameter(ERROR, pReq, pRes);
					}
				} catch (BBBSystemException e) {
					if (isLoggingError()) {
						logError("System Exception in Service Method of CSRGetOrderListDroplet for ProfileId"+ profileId + "MemberId" + memberId + e);
					}
					pReq.setParameter(ERROR, ERROR);
					if (orderList == null) {
						pReq.serviceLocalParameter(ERROR, pReq, pRes);
					}
				}
						
			if(orderList!=null){
				Collections.sort(orderList);
				Iterator<OrderHistoryResVO> itr = orderList.iterator();
				while(itr.hasNext()){
					OrderHistoryResVO vo = itr.next();
					if(vo.getOrderDate() == null || StringUtils.isEmpty(vo.getOrderDate().toString())){
						itr.remove();
					}
				}
			}
			if (null != orderList && orderList.size() > 0) {
				pReq.setParameter(ORDERLIST, orderList);	
				pReq.serviceLocalParameter(ORDEROUTPUTSTART, pReq, pRes);
				pReq.serviceLocalParameter(ORDEROUTPUT, pReq, pRes);
			} else {
				pReq.serviceLocalParameter(ORDEREMPTY, pReq, pRes);
			}
		}else{
			logDebug("Invoking SearchByOrderId for  OrderId " + orderId );
			SearchByOrderId(pReq, pRes, orderId);
		}
		
	  }
	
}
