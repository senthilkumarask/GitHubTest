package com.bbb.kickstarters;

import java.io.Serializable;

import java.util.List;


import atg.repository.RepositoryItem;

public class PicklistVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String title;
	private String pickListDescription;
	private String customerType;
	private RepositoryItem site;
	private List<RegistryTypeVO> registryTypes;
	private List<TopSkuVO> topSkus;
	private List<RepositoryItem> channels;
	
	public String getPickListDescription() {
		return pickListDescription;
	}
	public void setPickListDescription(String pickListDescription) {
		this.pickListDescription = pickListDescription;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public RepositoryItem getSite() {
		return site;
	}
	public void setSite(RepositoryItem site) {
		this.site = site;
	}
	public List<RegistryTypeVO> getRegistryTypes() {
		return registryTypes;
	}
	public void setRegistryTypes(List<RegistryTypeVO> registryTypes) {
		this.registryTypes = registryTypes;
	}
	public List<TopSkuVO> getTopSkus() {
		return topSkus;
	}
	public void setTopSkus(List<TopSkuVO> topSkus) {
		this.topSkus = topSkus;
	}
	public List<RepositoryItem> getChannels() {
		return channels;
	}
	public void setChannels(List<RepositoryItem> channels) {
		this.channels = channels;
	}	
}