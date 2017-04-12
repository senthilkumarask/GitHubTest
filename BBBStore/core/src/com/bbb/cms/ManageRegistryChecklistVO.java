package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.checklist.vo.NonRegistryGuideVO;

public class ManageRegistryChecklistVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String registryType;
	private List<BannerVO> links;
	private List<NonRegistryGuideVO> nonRegistryGuideVOs;
	private boolean activateGuideInRegistryRibbon;
	private String selectedGuideType;
	public String getRegistryType() {
		return registryType;
	}
	public void setRegistryType(String registryType) {
		this.registryType = registryType;
	}
	public List<BannerVO> getLinks() {
		return links;
	}
	public void setLinks(List<BannerVO> links) {
		this.links = links;
	}
	public List<NonRegistryGuideVO> getNonRegistryGuideVOs() {
		return nonRegistryGuideVOs;
	}
	public void setNonRegistryGuideVOs(List<NonRegistryGuideVO> nonRegistryGuideVOs) {
		this.nonRegistryGuideVOs = nonRegistryGuideVOs;
	}
	public boolean isActivateGuideInRegistryRibbon() {
		return activateGuideInRegistryRibbon;
	}
	public void setActivateGuideInRegistryRibbon(
			boolean activateGuideInRegistryRibbon) {
		this.activateGuideInRegistryRibbon = activateGuideInRegistryRibbon;
	}
	public String getSelectedGuideType() {
		return selectedGuideType;
	}
	public void setSelectedGuideType(String selectedGuideType) {
		this.selectedGuideType = selectedGuideType;
	} 
}
