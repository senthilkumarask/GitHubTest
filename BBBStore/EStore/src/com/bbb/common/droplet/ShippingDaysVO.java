package com.bbb.common.droplet;

import java.io.Serializable;

public class ShippingDaysVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String mMinDay;
	public String mMaxDay;
	public String mMinDayVDC;
	public String mMaxDayVDC;
	/**
	 * @return the minDay
	 */
	public String getMinDay() {
		return mMinDay;
	}
	/**
	 * @param pMinDay the minDay to set
	 */
	public void setMinDay(String pMinDay) {
		mMinDay = pMinDay;
	}
	/**
	 * @return the maxDay
	 */
	public String getMaxDay() {
		return mMaxDay;
	}
	/**
	 * @param pMaxDay the maxDay to set
	 */
	public void setMaxDay(String pMaxDay) {
		mMaxDay = pMaxDay;
	}
	/**
	 * 
	 * @return mMinDayVDC
	 */
	public String getMinDayVDC() {
		return mMinDayVDC;
}
	/**
	 * 
	 * @param mMinDayVDC
	 */
	public void setMinDayVDC(String mMinDayVDC) {
		this.mMinDayVDC = mMinDayVDC;
	}
	/**
	 * 
	 * @return mMaxDayVDC
	 */
	public String getMaxDayVDC() {
		return mMaxDayVDC;
	}
	/**
	 * 
	 * @param mMaxDayVDC
	 */
	public void setMaxDayVDC(String mMaxDayVDC) {
		this.mMaxDayVDC = mMaxDayVDC;
	}
}
