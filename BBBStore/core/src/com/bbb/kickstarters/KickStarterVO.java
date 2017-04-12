package com.bbb.kickstarters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bbb.cms.PromoBoxVO;

public class KickStarterVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String heading1;
	private String heading2;
	private String description;
	private String imageUrl;
	private String heroImageUrl;
	private String kickstarterType;
	private int priority;
	private String isActive;
	private String imageAltAttr;
	private String heroImageAltAttr;
	private List<PicklistVO> kickStarterPicklists=new ArrayList<PicklistVO>();
	private List<PromoBoxVO> promoBoxVOList;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHeading1() {
		return heading1;
	}
	public void setHeading1(String heading1) {
		this.heading1 = heading1;
	}
	public String getHeading2() {
		return heading2;
	}
	public void setHeading2(String heading2) {
		this.heading2 = heading2;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getKickstarterType() {
		return kickstarterType;
	}
	public void setKickstarterType(String kickstarterType) {
		this.kickstarterType = kickstarterType;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public List<PicklistVO> getKickStarterPicklists() {
		return kickStarterPicklists;
	}
	public void setKickStarterPicklists(List<PicklistVO> kickStarterPicklists) {
		this.kickStarterPicklists = kickStarterPicklists;
	}
	public String getHeroImageUrl() {
		return heroImageUrl;
	}
	public void setHeroImageUrl(String heroImageUrl) {
		this.heroImageUrl = heroImageUrl;
	}
	public List<PromoBoxVO> getPromoBoxVOList() {
		return promoBoxVOList;
	}
	public void setPromoBoxVOList(List<PromoBoxVO> promoBoxVOList) {
		this.promoBoxVOList = promoBoxVOList;
	}
	public String getImageAltAttr() {
		return imageAltAttr;
	}
	public void setImageAltAttr(String imageAltAttr) {
		this.imageAltAttr = imageAltAttr;
	}
	/**
	 * @return the heroImageAltAttr
	 */
	public String getHeroImageAltAttr() {
		return heroImageAltAttr;
	}
	/**
	 * @param heroImageAltAttr the heroImageAltAttr to set
	 */
	public void setHeroImageAltAttr(String heroImageAltAttr) {
		this.heroImageAltAttr = heroImageAltAttr;
	}

}