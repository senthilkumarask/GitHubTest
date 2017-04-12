package com.bbb.cms;

import java.util.List;

import com.bbb.constants.BBBCmsConstants;

public class LinkVO extends BannerVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isSeparator;
	private String bannerText;
	private List<LinkVO> multiLinksList; 
	private boolean hiddenFlag;
	private String linkCode;
	
	/**
	 * @return the multiLinksList
	 */
	public List<LinkVO> getMultiLinksList() {
		return multiLinksList;
	}
	/**
	 * @param multiLinksList the multiLinksList to set
	 */
	public void setMultiLinksList(List<LinkVO> multiLinksList) {
		this.multiLinksList = multiLinksList;
	}
	/**
	 * 
	 */
	
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
		
		if(BBBCmsConstants.REST_NAVIGATION_LINKS_TEMPLATE_BANNER_SEPARATOR.equalsIgnoreCase(bannerText))
		{
			setSeparator(true);
			this.bannerText = "";
		}
		else
		{
			setSeparator(false);
			this.bannerText = bannerText;
		}
	}
	/**
	 * @return the isSeparator
	 */
	public boolean isSeparator() {
		return isSeparator;
	}
	/**
	 * @param isSeparator the isSeparator to set
	 */
	public void setSeparator(boolean isSeparator) {
		this.isSeparator = isSeparator;
	}
	/**
	 * @return the hiddenFlag
	 */
	public boolean isHiddenFlag() {
		return hiddenFlag;
	}
	/**
	 * @param hiddenFlag the hiddenFlag to set
	 */
	public void setHiddenFlag(boolean hiddenFlag) {
		this.hiddenFlag = hiddenFlag;
	}
	/**
	 * @return the linkCode
	 */
	public String getLinkCode() {
		return linkCode;
	}
	/**
	 * @param linkCode the linkCode to set
	 */
	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}
	

}
