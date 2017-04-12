package com.bbb.certona.vo;

import java.io.Serializable;
import java.util.List;

/**
 * VO for populating guide information
 */
public class CertonaGuideVO implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<String> productIds;
	private String guideId;
	private String guideUrl;
	private String siteId;
	/**
	 * @return the productIds
	 */
	public List<String> getProductIds() {
		return productIds;
	}
	/**
	 * @param productIds
	 *          the productIds to set
	 */
	public void setProductIds(List<String> productIds) {
		this.productIds = productIds;
	}
	/**
	 * @return the guideId
	 */
	public String getGuideId() {
		return guideId;
	}
	/**
	 * @param guideId
	 *          the guideId to set
	 */
	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}
	/**
	 * @return the guideUrl
	 */
	public String getGuideUrl() {
		return guideUrl;
	}
	/**
	 * @param guideUrl
	 *          the guideUrl to set
	 */
	public void setGuideUrl(String guideUrl) {
		this.guideUrl = guideUrl;
	}
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId
	 *          the siteId to set
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	
	
}
