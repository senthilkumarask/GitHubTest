package com.bbb.cms;

import java.io.Serializable;

public class BannerVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bannerID;
	private String bannerText;
	private String bannerLink;
	private String bannerBackground;
	private String bannerForeground;
	private String bannerFontSize;
	private String bannerFontWeight;
	/**
	 * @return the bannerID
	 */
	public String getBannerID() {
		return bannerID;
	}
	/**
	 * @param bannerID the bannerID to set
	 */
	public void setBannerID(String bannerID) {
		this.bannerID = bannerID;
	}
	/**
	 * @return the bannerText
	 */
	public String getBannerText() {
		return bannerText;
	}
	/**
	 * @param bannerText the bannerText to set
	 */
	public void setBannerText(String bannerText) {
		this.bannerText = bannerText;
	}
	/**
	 * @return the bannerLink
	 */
	public String getBannerLink() {
		return bannerLink;
	}
	/**
	 * @param bannerLink the bannerLink to set
	 */
	public void setBannerLink(String bannerLink) {
		this.bannerLink = bannerLink;
	}
	/**
	 * @return the bannerBackground
	 */
	public String getBannerBackground() {
		return bannerBackground;
	}
	/**
	 * @param bannerBackground the bannerBackground to set
	 */
	public void setBannerBackground(String bannerBackground) {
		this.bannerBackground = bannerBackground;
	}
	/**
	 * @return the bannerForeground
	 */
	public String getBannerForeground() {
		return bannerForeground;
	}
	/**
	 * @param bannerForeground the bannerForeground to set
	 */
	public void setBannerForeground(String bannerForeground) {
		this.bannerForeground = bannerForeground;
	}
	/**
	 * @return the bannerFontSize
	 */
	public String getBannerFontSize() {
		return bannerFontSize;
	}
	/**
	 * @param bannerFontSize the bannerFontSize to set
	 */
	public void setBannerFontSize(String bannerFontSize) {
		this.bannerFontSize = bannerFontSize;
	}
	/**
	 * @return the bannerFontWeight
	 */
	public String getBannerFontWeight() {
		return bannerFontWeight;
	}
	/**
	 * @param bannerFontWeight the bannerFontWeight to set
	 */
	public void setBannerFontWeight(String bannerFontWeight) {
		this.bannerFontWeight = bannerFontWeight;
	}
	
}
