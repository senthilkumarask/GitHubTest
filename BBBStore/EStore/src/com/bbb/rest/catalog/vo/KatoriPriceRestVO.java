package com.bbb.rest.catalog.vo;


/**
 * @author rsain4
 *
 */
public class KatoriPriceRestVO {

	private String katoriItemPrice;
	private String katoriPersonlizedPrice;
	private String personalisationType;
	private boolean isErrorExist;
	private String errorMsg;
	private String currencySymbol;
	private double doubleKatoriItemPrice;
	private double doubleKatoriPersonalizedPrice;
	private boolean shipMsgFlag;
	private String displayShipMsg;
	
	public boolean isShipMsgFlag() {
		return shipMsgFlag;
	}

	public void setShipMsgFlag(boolean shipMsgFlag) {
		this.shipMsgFlag = shipMsgFlag;
	}

	public String getDisplayShipMsg() {
		return displayShipMsg;
	}

	public void setDisplayShipMsg(String displayShipMsg) {
		this.displayShipMsg = displayShipMsg;
	}

	/**
	 * @return katoriPrice
	 */
	public String getKatoriItemPrice() {
		return this.katoriItemPrice;
	}

	/**
	 * @param katoriPrice
	 */
	public void setKatoriItemPrice(final String katoriItemPrice) {
		this.katoriItemPrice = katoriItemPrice;
	}

	/**
	 * @return personalisationType
	 */
	public String getPersonalisationType() {
		return this.personalisationType;
	}

	/**
	 * @param personalisationType
	 */
	public void setPersonalisationType(final String personalisationType) {
		this.personalisationType = personalisationType;
	}

	public boolean isErrorExist() {
		return isErrorExist;
	}

	public void setErrorExist(boolean isErrorExist) {
		this.isErrorExist = isErrorExist;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return the currencySymbol
	 */
	public String getCurrencySymbol() {
		return currencySymbol;
	}

	/**
	 * @param currencySymbol the currencySymbol to set
	 */
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	/**
	 * @return the katoriPersonlizedPrice
	 */
	public String getKatoriPersonlizedPrice() {
		return katoriPersonlizedPrice;
	}

	/**
	 * @param katoriPersonlizedPrice the katoriPersonlizedPrice to set
	 */
	public void setKatoriPersonlizedPrice(String katoriPersonlizedPrice) {
		this.katoriPersonlizedPrice = katoriPersonlizedPrice;
	}
	
	public double getDoubleKatoriItemPrice() {
		return doubleKatoriItemPrice;
	}

	public void setDoubleKatoriItemPrice(double formattedKatoriItemPrice) {
		this.doubleKatoriItemPrice = formattedKatoriItemPrice;
	}

	public double getDoubleKatoriPersonalizedPrice() {
		return doubleKatoriPersonalizedPrice;
	}

	public void setDoubleKatoriPersonalizedPrice(
			double formattedKatoriPersonalizedPrice) {
		this.doubleKatoriPersonalizedPrice = formattedKatoriPersonalizedPrice;
	}

}
