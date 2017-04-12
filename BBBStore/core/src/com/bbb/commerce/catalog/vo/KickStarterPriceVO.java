package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.Properties;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.search.bean.result.BBBDynamicPriceVO;
import com.bbb.utils.BBBUtility;



/**
 * @author
 *
 */
public class KickStarterPriceVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String wasHighPrice;
	private String wasLowPrice;
	private String highPrice;
	private String lowPrice;
	private String wasHighPriceMX;
	private String wasLowPriceMX;
	private String highPriceMX;
	private String lowPriceMX;
	
	private String kickStrtPriceRangeDescrip;
	private String kickStrtPriceRangeDescripMX;
	private String kickStrtWasPriceRangeDescrip;
	private String KickStrtWasPriceRangeDescripMX;
	private String priceRangeToken;
	private boolean shipMsgFlag;
	private String displayShipMsg;
	
	/**
	 * 
	 */
	public KickStarterPriceVO() {
		super();
	}
	

	public String getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(String highPrice) {
		this.highPrice = highPrice;
	}
	
	public String getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(String lowPrice) {
		this.lowPrice = lowPrice;
	}
	

	public String getDisplayShipMsg() {
		return displayShipMsg;
	}
	public void setDisplayShipMsg(String displayShipMsg) {
		this.displayShipMsg = displayShipMsg;
	}
	
	public boolean isShipMsgFlag() {
		return shipMsgFlag;
	}
	public void setShipMsgFlag(boolean shipMsgFlag) {
		this.shipMsgFlag = shipMsgFlag;
	}

	
	public String getKickStrtPriceRangeDescripMX() {
		String oldPrice=this.kickStrtPriceRangeDescripMX, priceRange = this.priceRangeToken;
			String highPrice=this.getHighPriceMX(),lowPrice=this.getLowPriceMX();
			if (!BBBUtility.isEmpty(oldPrice) && !BBBUtility.isEmpty(priceRange) && !BBBUtility.isEmpty(lowPrice)) {
				Properties prop = new Properties();
				oldPrice=BBBUtility.convertToInternationalPriceByToken(priceRange,lowPrice,highPrice,prop);
			}
		
		return oldPrice;
		
	}
	public void setKickStrtPriceRangeDescripMX(String kickStrtPriceRangeDescripMX) {
		this.kickStrtPriceRangeDescripMX = kickStrtPriceRangeDescripMX;
	}
	
	public String getKickStrtPriceRangeDescrip() {
		String oldPrice=this.kickStrtPriceRangeDescrip, priceRange = this.priceRangeToken;
		String highPrice=this.getHighPrice(),lowPrice=this.getLowPrice();
		
		if (!BBBUtility.isEmpty(oldPrice) && !BBBUtility.isEmpty(priceRange) && !BBBUtility.isEmpty(lowPrice)) {
			Properties prop = new Properties();
			oldPrice=BBBUtility.convertToInternationalPriceByToken(priceRange,lowPrice,highPrice,prop);
		}
        return oldPrice;
	}
	public void setKickStrtPriceRangeDescrip(String kickStrtPriceRangeDescrip) {
		this.kickStrtPriceRangeDescrip = kickStrtPriceRangeDescrip;
	}
	public String getKickStrtWasPriceRangeDescrip() {
		String oldPrice=this.kickStrtWasPriceRangeDescrip, priceRange = this.priceRangeToken;
		String highPrice=this.getWasHighPrice(),lowPrice=this.getWasLowPrice();
		if (!BBBUtility.isEmpty(oldPrice) && !BBBUtility.isEmpty(priceRange) && !BBBUtility.isEmpty(lowPrice)) {
			Properties prop = new Properties();
			oldPrice=BBBUtility.convertToInternationalPriceByToken(priceRange,lowPrice,highPrice,prop);
		}
        return oldPrice;
	}
	public void setKickStrtWasPriceRangeDescrip(String kickStrtWasPriceRangeDescrip) {
		this.kickStrtWasPriceRangeDescrip = kickStrtWasPriceRangeDescrip;
	}
	public String getKickStrtWasPriceRangeDescripMX() {
		String oldPrice=this.KickStrtWasPriceRangeDescripMX , priceRange = this.priceRangeToken; 
		String highPrice=this.getWasHighPriceMX(),lowPrice=this.getWasLowPriceMX();
		if (!BBBUtility.isEmpty(oldPrice) && !BBBUtility.isEmpty(priceRange) && !BBBUtility.isEmpty(lowPrice)) {
			Properties prop = new Properties();
			oldPrice=BBBUtility.convertToInternationalPriceByToken(priceRange,lowPrice,highPrice,prop);
		}
		return oldPrice;
		
	}
	public void setKickStrtWasPriceRangeDescripMX(
			String KickStrtWasPriceRangeDescripMX) {
		this.KickStrtWasPriceRangeDescripMX = KickStrtWasPriceRangeDescripMX;
	}
	
	public String getWasHighPrice() {
		return wasHighPrice;
	}
	public void setWasHighPrice(String wasHighPrice) {
		this.wasHighPrice = wasHighPrice;
	}
	
	public String getWasLowPrice() {
		return wasLowPrice;
	}
	public void setWasLowPrice(String wasLowPrice) {
		this.wasLowPrice = wasLowPrice;
	}


	
	public String getHighPriceMX() {
		return highPriceMX;
	}


	public void setHighPriceMX(String highPriceMX) {
		this.highPriceMX = highPriceMX;
	}


	public String getLowPriceMX() {
		return lowPriceMX;
	}


	public void setLowPriceMX(String lowPriceMX) {
		this.lowPriceMX = lowPriceMX;
	}


	public String getWasHighPriceMX() {
		return wasHighPriceMX;
	}


	public void setWasHighPriceMX(String wasHighPriceMX) {
		this.wasHighPriceMX = wasHighPriceMX;
	}


	public String getWasLowPriceMX() {
		return wasLowPriceMX;
	}


	public void setWasLowPriceMX(String wasLowPriceMX) {
		this.wasLowPriceMX = wasLowPriceMX;
	}


	public String getPriceRangeToken() {
		return priceRangeToken;
	}


	public void setPriceRangeToken(String priceRangeToken) {
		this.priceRangeToken = priceRangeToken;
	}

}


