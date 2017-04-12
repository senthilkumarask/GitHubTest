package com.bbb.commerce.checkout.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.ShipMethodVO;

/**
 * VO for Get ShippingMethod calls.
 * 
 * @author msiddi
 *
 */
public class ShipMethodsVORest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ShipMethodVO> shipMethodList;
	private String skuId;
	private String sddEligibilityStatus;
	private String sddOptionState;
	private String preSelectedShipMethod;

	public String getPreSelectedShipMethod() {
		return preSelectedShipMethod;
	}

	public void setPreSelectedShipMethod(String preSelectedShipMethod) {
		this.preSelectedShipMethod = preSelectedShipMethod;
	}
	public String getSddEligibilityStatus() {
		return sddEligibilityStatus;
	}

	public void setSddEligibilityStatus(String sddEligibilityStatus) {
		this.sddEligibilityStatus = sddEligibilityStatus;
	}

	public String getSddOptionState() {
		return sddOptionState;
	}

	public void setSddOptionState(String sddOptionState) {
		this.sddOptionState = sddOptionState;
	}

	public List<ShipMethodVO> getShipMethodList() {
		return shipMethodList;
	}

	public void setShipMethodList(List<ShipMethodVO> shipMethodList) {
		this.shipMethodList = shipMethodList;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}


}
