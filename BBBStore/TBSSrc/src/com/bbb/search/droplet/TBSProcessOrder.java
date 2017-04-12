package com.bbb.search.droplet;

import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.order.TBSOrderManager;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.commerce.vo.OrderVO;
import com.bbb.search.bean.TBSAdvancedOrderSearchResultsCacheBean;

/**
 * This droplet is used to construct OrderVO's and add the same to the list which is cached.
 *
 */
public class TBSProcessOrder extends DynamoServlet {
	
	private TBSOrderManager mOrderManager;
	
	private TBSAdvancedOrderSearchResultsCacheBean advancedOrderSearchCache;
	
	private final static String ITEM_AND_VO ="itemAndVO";
	private final static String PER_PAGE ="perPage";
	private final static String SEARCH_KEY ="searchKey";
	
	/**
	 * @return the orderManager
	 */
	public TBSOrderManager getOrderManager() {
		return mOrderManager;
	}
	/**
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(TBSOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}
	
	/**
	 * This method fetches List<OrderVO> from cache and constructs the OrderVO and adds the same to the List which is cached.  
	 * 
	 * @param pRequest
	 * @param pResponse
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		LinkedHashMap searchKeyOrderItemAndVO = (LinkedHashMap) pRequest.getObjectParameter(ITEM_AND_VO);
		int perPage = Integer.parseInt(pRequest.getParameter(PER_PAGE));
		String searchKey = pRequest.getParameter(SEARCH_KEY);
		HashMap orderItemAndVO = (HashMap) searchKeyOrderItemAndVO.get(searchKey);
		if(orderItemAndVO!=null){
			List<RepositoryItem> ordersList = (List<RepositoryItem>) orderItemAndVO.keySet().iterator().next();
			List<OrderVO> orderVOList = (List<OrderVO>) orderItemAndVO.get(ordersList);
			try {
				if(orderVOList.size() != perPage) {
					List<OrderVO> deltaOrderVOList = ((TBSOrderTools)getOrderManager().getOrderTools()).partialProcessOrders(ordersList,orderVOList,perPage);
					for(OrderVO orderVO:deltaOrderVOList){
						orderVOList.add(orderVO);
					}
				}
			} catch (CommerceException e) {
				e.printStackTrace();
			}
		
		}else{
			pRequest.serviceLocalParameter("empty", pRequest, pResponse);
		}
	}
	
	public TBSAdvancedOrderSearchResultsCacheBean getAdvancedOrderSearchCache() {
		return advancedOrderSearchCache;
	}
	public void setAdvancedOrderSearchCache(TBSAdvancedOrderSearchResultsCacheBean advancedOrderSearchCache) {
		this.advancedOrderSearchCache = advancedOrderSearchCache;
	}

}
