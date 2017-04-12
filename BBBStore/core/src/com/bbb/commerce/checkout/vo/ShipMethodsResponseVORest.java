package com.bbb.commerce.checkout.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.RegionVO;

/**
 * VO for Get ShippingMethod calls.
 * 
 * @author msiddi
 *
 */
 
public class ShipMethodsResponseVORest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ShipMethodsVORest> shipMethodsVORest;
	
	private String shippingOperation;
	
	private RegionVO regionVO;

	public RegionVO getRegionVO() {
		return regionVO;
	}

	public void setRegionVO(RegionVO regionVO) {
		this.regionVO = regionVO;
	}

	public void setShippingOperation(String shippingOperation) {
		this.shippingOperation = shippingOperation;
	}

	public String getShippingOperation() {
		return shippingOperation;
	}

	public void setShipMethodsVORest(List<ShipMethodsVORest> shipMethodsVORest) {
		this.shipMethodsVORest = shipMethodsVORest;
	}

	public List<ShipMethodsVORest> getShipMethodsVORest() {
		return shipMethodsVORest;
	}



}
