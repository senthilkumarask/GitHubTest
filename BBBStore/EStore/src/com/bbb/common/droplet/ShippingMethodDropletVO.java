package com.bbb.common.droplet;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.bbb.common.vo.ShippingInfoKey;
import com.bbb.common.vo.StatesShippingMethodPriceVO;

/**
 * ShippingMethodDropletVO class hold shipping policy details
 * @author akhaju
 *
 */
public class ShippingMethodDropletVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>> mShippingPriceTableDetail;
	private Set<String> mShippingMethods;
	/**
	 * @return the shippingPriceTableDetail
	 */
	public Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>> getShippingPriceTableDetail() {
		return mShippingPriceTableDetail;
	}
	/**
	 * @param pShippingPriceTableDetail the shippingPriceTableDetail to set
	 */
	public void setShippingPriceTableDetail(Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>> pShippingPriceTableDetail) {
		mShippingPriceTableDetail = pShippingPriceTableDetail;
	}
	/**
	 * @return the shippingMethods
	 */
	public Set<String> getShippingMethods() {
		return mShippingMethods;
	}
	/**
	 * @param pShippingMethods the shippingMethods to set
	 */
	public void setShippingMethods(Set<String> pShippingMethods) {
		mShippingMethods = pShippingMethods;
	}
}
