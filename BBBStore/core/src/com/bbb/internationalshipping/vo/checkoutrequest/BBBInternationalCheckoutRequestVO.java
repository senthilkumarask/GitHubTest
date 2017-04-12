package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;


/**
 * This class gives you the information
 * about Checkout Request VO.
 * @version 1.0
 */

public class BBBInternationalCheckoutRequestVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to merchantId.
	 */
	private String merchantId;
	/**
	 * This variable is used to point to BBBDomesticBasketVO.
	 */
	private BBBDomesticBasketVO bbbDomesticBasketVO;
	/**
	 * This variable is used to point to BBBDomesticShippingMethodVO.
	 */
	private BBBDomesticShippingMethodVO bbbDomesticShippingMethodVO;
	/**
	 * This variable is used to point to BBBSessionDetailsVO.
	 */
	private BBBSessionDetailsVO bbbSessionDetailsVO;
	/**
	 * This variable is used to point to BBBOrderPropertiesVO.
	 */
	private BBBOrderPropertiesVO bbbOrderPropertiesVO;
	/**
	 * @return the merchantId
	 */
	public final String getMerchantId() {
		return merchantId;
	}
	/**
	 * @param merchantId the merchantId to set
	 */
	public final void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	/**
	 * @return the bbbDomesticBasletVO
	 */
	public final BBBDomesticBasketVO getBbbDomesticBasketVO() {
		return bbbDomesticBasketVO;
	}
	/**
	 * @param bbbDomesticBasletVO the bbbDomesticBasletVO to set
	 */
	public final void setBbbDomesticBasketVO(final BBBDomesticBasketVO bbbDomesticBasketVO) {
		this.bbbDomesticBasketVO = bbbDomesticBasketVO;
	}
	/**
	 * @return the bbbDomesticShippingMethodVO
	 */
	public final BBBDomesticShippingMethodVO getBbbDomesticShippingMethodVO() {
		return bbbDomesticShippingMethodVO;
	}
	/**
	 * @param bbbDomesticShippingMethodVO the bbbDomesticShippingMethodVO to set
	 */
	public final void setBbbDomesticShippingMethodVO(final 
			BBBDomesticShippingMethodVO bbbDomesticShippingMethodVO) {
		this.bbbDomesticShippingMethodVO = bbbDomesticShippingMethodVO;
	}
	/**
	 * @return the bbbSessionDetailsVO
	 */
	public final BBBSessionDetailsVO getBbbSessionDetailsVO() {
		return bbbSessionDetailsVO;
	}
	/**
	 * @param bbbSessionDetailsVO the bbbSessionDetailsVO to set
	 */
	public final void setBbbSessionDetailsVO(final BBBSessionDetailsVO bbbSessionDetailsVO) {
		this.bbbSessionDetailsVO = bbbSessionDetailsVO;
	}
	/**
	 * @return the bbbOrderPropertiesVO
	 */
	public final BBBOrderPropertiesVO getBbbOrderPropertiesVO() {
		return bbbOrderPropertiesVO;
	}
	/**
	 * @param bbbOrderPropertiesVO the bbbOrderPropertiesVO to set
	 */
	public final void setBbbOrderPropertiesVO(
			BBBOrderPropertiesVO bbbOrderPropertiesVO) {
		this.bbbOrderPropertiesVO = bbbOrderPropertiesVO;
	}
}
