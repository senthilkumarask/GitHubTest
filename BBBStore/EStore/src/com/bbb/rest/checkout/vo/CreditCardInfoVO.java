package com.bbb.rest.checkout.vo;

import java.io.Serializable;
import java.util.Map;

import com.bbb.commerce.common.BasicBBBCreditCardInfo;

/**
 * VO to get all credit card information
 * @author sku134
 *
 */
public class CreditCardInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String selectedId;
	private String orderBalance;
	private Map<String,BasicBBBCreditCardInfo> creditCardInfoList;
	public String getSelectedId() {
		return selectedId;
	}
	public void setSelectedId(String selectedId) {
		this.selectedId = selectedId;
	}
	
	public String getOrderBalance() {
		return orderBalance;
	}
	public void setOrderBalance(String orderBalance) {
		this.orderBalance = orderBalance;
	}
	public Map<String, BasicBBBCreditCardInfo> getCreditCardInfoList() {
		return creditCardInfoList;
	}
	public void setCreditCardInfoList(
			Map<String, BasicBBBCreditCardInfo> creditCardInfoList) {
		this.creditCardInfoList = creditCardInfoList;
	}
 
	
}
