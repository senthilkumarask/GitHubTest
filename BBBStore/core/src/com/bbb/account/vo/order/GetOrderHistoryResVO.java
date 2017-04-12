package com.bbb.account.vo.order;

import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

/**
 * 
 * @author jsidhu
 *
 * Used to hold the GetOrderHistory webservice response
 */
public class GetOrderHistoryResVO extends ServiceResponseBase{
	

	private static final long serialVersionUID = 4997229096349975502L;
	private List<OrderHistoryResVO> mOrderList;
	private ErrorStatus mErrorStatus;
	
	/**
	 * @return the orderList
	 */
	public List<OrderHistoryResVO> getOrderList() {
		return mOrderList;
	}
	/**
	 * @param pOrderList the orderList to set
	 */
	public void setOrderList(List<OrderHistoryResVO> pOrderList) {
		mOrderList = pOrderList;
	}
	/**
	 * @return the errorStatus
	 */
	public ErrorStatus getErrorStatus() {
		return mErrorStatus;
	}
	/**
	 * @param pErrorStatus the errorStatus to set
	 */
	public void setErrorStatus(ErrorStatus pErrorStatus) {
		mErrorStatus = pErrorStatus;
	}

	
	
}
