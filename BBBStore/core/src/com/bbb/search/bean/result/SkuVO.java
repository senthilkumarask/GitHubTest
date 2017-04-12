package com.bbb.search.bean.result;

import java.io.Serializable;


public class SkuVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String skuID;
	private String skuDisplayName;
	private String skuMedImageURL;
	private String skuSwatchImageURL;
	private String color;
	private String colorGroup;
	private String skuSize;
	private String skuVerticalImageURL;
	private String skuMedImageUrlForGrid3x3;
	private String skuMedImageUrlForGrid4;
	
	public String getSkuID() {
		return skuID;
	}
	public void setSkuID(String skuID) {
		this.skuID = skuID;
	}
	public String getSkuDisplayName() {
		return skuDisplayName;
	}
	public void setSkuDisplayName(String skuDisplayName) {
		this.skuDisplayName = skuDisplayName;
	}
	public String getSkuMedImageURL() {
		return skuMedImageURL;
	}
	public void setSkuMedImageURL(String skuMedImageURL) {
		this.skuMedImageURL = skuMedImageURL;
	}
	public String getSkuSwatchImageURL() {
		return skuSwatchImageURL;
	}
	public void setSkuSwatchImageURL(String skuSwatchImageURL) {
		this.skuSwatchImageURL = skuSwatchImageURL;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getColorGroup() {
		return colorGroup;
	}
	public void setColorGroup(String colorGroup) {
		this.colorGroup = colorGroup;
	}
	public String getSkuSize() {
		return skuSize;
	}
	public void setSkuSize(String skuSize) {
		this.skuSize = skuSize;
	}
	public String getSkuVerticalImageURL() {
		return skuVerticalImageURL;
	}
	public void setSkuVerticalImageURL(String skuVerticalImageURL) {
		this.skuVerticalImageURL = skuVerticalImageURL;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSkuMedImageUrlForGrid3x3() {
		return skuMedImageUrlForGrid3x3;
	}
	public void setSkuMedImageUrlForGrid3x3(String skuMedImageUrlForGrid3x3) {
		this.skuMedImageUrlForGrid3x3 = skuMedImageUrlForGrid3x3;
	}
	public String getSkuMedImageUrlForGrid4() {
		return skuMedImageUrlForGrid4;
	}
	public void setSkuMedImageUrlForGrid4(String skuMedImageUrlForGrid4) {
		this.skuMedImageUrlForGrid4 = skuMedImageUrlForGrid4;
	}
	
}
