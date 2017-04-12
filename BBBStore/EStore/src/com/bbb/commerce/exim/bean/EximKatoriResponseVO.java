package com.bbb.commerce.exim.bean;

import com.bbb.rest.catalog.vo.KatoriPriceRestVO;

public class EximKatoriResponseVO {
	
	private double cost_adder;
	private String customization_service;
	private String customization_status;
	private String description;
	private String imageURL;
	private String imageURL_thumb;
	private String mobileURL;
	private String mobileURL_thumb;
	private String namedrop;
	private double price_adder;
	private long quantity;
	private String refnum;
	private String sku;
	private boolean personalizationComplete;
	private String personalizedSingleCode;
	private String personalizedSingleCodeDescription;
	private boolean altImages;
	private KatoriPriceRestVO katoriPriceVO;
	public boolean isPersonalizationComplete() {
		return personalizationComplete;
	}
	
	public void setPersonalizationComplete(boolean personalizationComplete) {
		this.personalizationComplete = personalizationComplete;
	}
	
	public String getPersonalizedSingleCode() {
		return personalizedSingleCode;
	}
	
	public void setPersonalizedSingleCode(String personalizedSingleCode) {
		this.personalizedSingleCode = personalizedSingleCode;
	}
	
	public boolean isAltImages() {
		return altImages;
	}
	
	public void setAltImages(boolean altImages) {
		this.altImages = altImages;
	}
	
	public double getCost_adder() {
		return cost_adder;
	}
	
	public void setCost_adder(double cost_adder) {
		this.cost_adder = cost_adder;
	}
	
	public String getCustomization_service() {
		return customization_service;
	}
	
	public void setCustomization_service(String customization_service) {
		this.customization_service = customization_service;
	}
	
	public String getCustomization_status() {
		return customization_status;
	}
	
	public void setCustomization_status(String customization_status) {
		this.customization_status = customization_status;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	public String getImageURL_thumb() {
		return imageURL_thumb;
	}
	
	public void setImageURL_thumb(String imageURL_thumb) {
		this.imageURL_thumb = imageURL_thumb;
	}
	
	public String getMobileURL() {
		return mobileURL;
	}
	
	public void setMobileURL(String mobileURL) {
		this.mobileURL = mobileURL;
	}
	
	public String getMobileURL_thumb() {
		return mobileURL_thumb;
	}
	
	public void setMobileURL_thumb(String mobileURL_thumb) {
		this.mobileURL_thumb = mobileURL_thumb;
	}
	
	public String getNamedrop() {
		return namedrop;
	}
	
	public void setNamedrop(String namedrop) {
		this.namedrop = namedrop;
	}
	
	public double getPrice_adder() {
		return price_adder;
	}
	
	public void setPrice_adder(double price_adder) {
		this.price_adder = price_adder;
	}
	
	public long getQuantity() {
		return quantity;
	}
	
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public String getRefnum() {
		return refnum;
	}
	
	public void setRefnum(String refnum) {
		this.refnum = refnum;
	}
	
	public String getSku() {
		return sku;
	}
	
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public KatoriPriceRestVO getKatoriPriceVO() {
		return katoriPriceVO;
	}
	
	public void setKatoriPriceVO(KatoriPriceRestVO katoriPriceVO) {
		this.katoriPriceVO = katoriPriceVO;
	}
	
	public String getPersonalizedSingleCodeDescription() {
		return personalizedSingleCodeDescription;
	}

	public void setPersonalizedSingleCodeDescription(
			String personalizedSingleCodeDescription) {
		this.personalizedSingleCodeDescription = personalizedSingleCodeDescription;
	}

}
