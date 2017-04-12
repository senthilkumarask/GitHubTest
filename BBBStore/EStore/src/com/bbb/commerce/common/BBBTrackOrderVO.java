package com.bbb.commerce.common;

import java.util.Map;
import com.bbb.account.vo.order.OrderDetailInfoReturn;


/**
 * This file is part of Response for Order Tracking, This contains the OrderVO(s) and is returned to the REST service 
 *
 * @author agu117
 *  
 */
public class BBBTrackOrderVO {

	private BBBOrderVO bbbOrderVO;
	private OrderDetailInfoReturn legacyOrderVO;
    private boolean legacyOrderFlag;
    private Map<String, Boolean> activeProdMap;
    private String bvToken;
    
    /**
	 * @return the bbbOrderVO
	 */
	public final BBBOrderVO getBbbOrderVO() {
		return this.bbbOrderVO;
	}
	/**
	 * @param bbbOrderVO the bbbOrderVO to set
	 */
	public final void setBbbOrderVO(final BBBOrderVO bbbOrderVO) {
		this.bbbOrderVO = bbbOrderVO;
	}
	/**
	 * @return the legacyOrderVO
	 */
	public final OrderDetailInfoReturn getLegacyOrderVO() {
		return this.legacyOrderVO;
	}
	/**
	 * @param legacyOrderVO the legacyOrderVO to set
	 */
	public final void setLegacyOrderVO(final OrderDetailInfoReturn legacyOrderVO) {
		this.legacyOrderVO = legacyOrderVO;
	}
	/**
	 * @return the isLegacyOrder
	 */
	public final boolean isLegacyOrderFlag() {
		return this.legacyOrderFlag;
	}
	/**
	 * @param isLegacyOrder the isLegacyOrder to set
	 */
	public final void setLegacyOrderFlag(final boolean isLegacyOrder) {
		this.legacyOrderFlag = isLegacyOrder;
	}
	/**
	 * This method provides the REST service a Map for Products in the Order with Active Flag, used to display Write a review Button
	 * @return
	 */
	public Map<String, Boolean> getActiveProdMap() {
		return this.activeProdMap;
	}
	
	/**
	 * This method sets the value in Map for Products in the Order with Active Flag, used to display Write a review Button
	 * @param activeProdMap
	 */
	public void setActiveProdMap(final Map<String, Boolean> activeProdMap) {
		this.activeProdMap = activeProdMap;
	}
	/**
	 * @return the bvToken
	 */
	public String getBvToken() {
		return this.bvToken;
	}
	/**
	 * @param bvToken the bvToken to set
	 */
	public void setBvToken(String bvToken) {
		this.bvToken = bvToken;
	}

}
