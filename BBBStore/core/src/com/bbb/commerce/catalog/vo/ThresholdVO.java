package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

public class ThresholdVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long thresholdLimited;
	private long thresholdAvailable;
	/**
	 * @return the thresholdLimited
	 */
	public long getThresholdLimited() {
		return thresholdLimited;
	}
	/**
	 * @param thresholdLimited the thresholdLimited to set
	 */
	public void setThresholdLimited(final long thresholdLimited) {
		this.thresholdLimited = thresholdLimited;
	}
	/**
	 * @return the thresholdAvailable
	 */
	public long getThresholdAvailable() {
		return thresholdAvailable;
	}
	/**
	 * @param thresholdAvailable the thresholdAvailable to set
	 */
	public void setThresholdAvailable(final long thresholdAvailable) {
		this.thresholdAvailable = thresholdAvailable;
	}
	public String toString(){
		StringBuffer toString=new StringBuffer(" Threshold VO Details \n ");
		toString.append("Threshold Limited  ").append(thresholdLimited).append("\n")
		.append(" Threshold Available ").append(thresholdAvailable).append("\n");
		return toString.toString();
	}

}
