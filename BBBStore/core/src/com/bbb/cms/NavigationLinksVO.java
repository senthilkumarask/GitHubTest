package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

public class NavigationLinksVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String navLinkId;
	public String siteId;
	private List<LinkVO> bannerList;
	
	/**
	 * @return the navLinkId
	 */
	public String getNavLinkId() {
		return navLinkId;
	}
	/**
	 * @param navLinkId the navLinkId to set
	 */
	public void setNavLinkId(String navLinkId) {
		this.navLinkId = navLinkId;
	}
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return the bannerList
	 */
	public List<LinkVO> getBannerList() {
		return bannerList;
	}
	/**
	 * @param bannerList the bannerList to set
	 */
	public void setBannerList(List<LinkVO> bannerList) {
		this.bannerList = bannerList;
	}

}
