package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

public class SDDResponseVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String availableStatus;
	private String displayMessage;
	private RegionVO regionVO;

	public String getAvailableStatus() {
		return availableStatus;
	}

	public void setAvailableStatus(String availableStatus) {
		this.availableStatus = availableStatus;
	}

	public String getDisplayMessage() {
		return displayMessage;
	}

	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}

	public RegionVO getRegionVO() {
		return regionVO;
	}

	public void setRegionVO(RegionVO regionVO) {
		this.regionVO = regionVO;
	}

}