package com.bbb.common.vo;

import java.io.Serializable;

import atg.core.util.StringUtils;

import com.bbb.constants.BBBCmsConstants;

/**
 * Used as a key in pricing details map for shipping info.
 * 
 * @author skuma7
 *
 */
public class AppShipMethodPriceVO implements Comparable<AppShipMethodPriceVO>,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mAppShipMethodName;
	private String price;	
	
	public AppShipMethodPriceVO() {
		super();	
	}
	
	public AppShipMethodPriceVO(final String mAppShipMethodName, final String price) {
		super();
		this.mAppShipMethodName = mAppShipMethodName;
		this.price = price;
	}

	public String getmAppShipMethodName() {
		return mAppShipMethodName;
	}
	public void setmAppShipMethodName(final String mAppShipMethodName) {
		this.mAppShipMethodName = mAppShipMethodName;
	}
	public String getPrice() {
		
		if (StringUtils.isEmpty(price)) {
			price = BBBCmsConstants.FREE;
		} else {
			price = BBBCmsConstants.DOLLOR + price;
		}
		
		return price;
	}
	public void setPrice(final String price) {
		this.price = price;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("AppShipMethodPriceVO [mAppShipMethodName=");
		builder.append(mAppShipMethodName);
		builder.append(", price=");
		builder.append(price);
		builder.append("]");
		return builder.toString();
	}

	@Override
	/**
	 * decending sort
	 */
	public int compareTo(final AppShipMethodPriceVO pAppShipMethodPriceVO) {
		final int compareTo = this.getmAppShipMethodName().compareTo(pAppShipMethodPriceVO.getmAppShipMethodName());
		if(compareTo == 0){
			return 0;
		}else{
			return -1 * compareTo;
		}
	}
}
