package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;
import java.util.List;

/**
 * This class gives you the information
 * about Basket Display VO.
 * @version 1.0
 */
public class BBBDomesticBasketVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to List of BBBBasketItemsVO.
	 */
	private List<BBBBasketItemsVO> bbbBasketItemsVO;
	/**
	 * This variable is used to point to bbbBasketTotalVO.
	 */
	BBBBasketTotalVO bbbBasketTotalVO;
	/**
	 * @return the bbbBasketItemsVO
	 */
	private String customData;
	/**
	 * @return the customData
	 */

	public final String getCustomData() {
		return customData;
	}
	/**
	 * @param customData the customData to set
	 */
	public final void setCustomData(String customData) {
		this.customData = customData;
	}
	public final List<BBBBasketItemsVO> getBbbBasketItemsVO() {
		return bbbBasketItemsVO;
	}
	/**
	 * @param bbbBasketItemsVO the bbbBasketItemsVO to set
	 */
	public final void setBbbBasketItemsVO(final List<BBBBasketItemsVO> bbbBasketItemsVO) {
		this.bbbBasketItemsVO = bbbBasketItemsVO;
	}
	/**
	 * @return the bbbBasketTotalVO
	 */
	public final BBBBasketTotalVO getBbbBasketTotalVO() {
		return bbbBasketTotalVO;
	}
	/**
	 * @param bbbBasketTotalVO the bbbBasketTotalVO to set
	 */
	public final void setBbbBasketTotalVO(final BBBBasketTotalVO bbbBasketTotalVO) {
		this.bbbBasketTotalVO = bbbBasketTotalVO;
	}

	

}
