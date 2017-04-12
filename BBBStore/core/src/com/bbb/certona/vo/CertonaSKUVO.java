package com.bbb.certona.vo;

import java.util.Date;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

public class CertonaSKUVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skuId;
	private Timestamp creationDate;
	private Date skuEnableDate;
	private String skuType;
	private boolean isGiftCertsku=false;
	private String smallImageURL;
	private String mediumImageURL;
	private String largeImageURL;
	private String swatchImageURL;
	private String thumbNailImage;
	private String zoomImageURL;
	private Integer zoomIndex;
	private String jdaDept;
	private String jdaSubDept;
	private String jdaClass;
	private String color;
	private String colorGroup;
	private String size;
	private String vdcSkuMessage;
	private String vdcSkuType;
	private String upc;
	private String eComFulfillment;
	private String vendorId;
	private boolean onsale=false;
	private String collegeId;
	private boolean emailOutOfStock=false;
	private boolean giftWrapEligible=false;
	private boolean bopusExclusion=true;
	private String skuTaxStatus;
	private Prop65TypeVO prop65Flags;
	private boolean ltlFlag=false;
	private Map<String, SiteSpecificSKUAttr> siteSKUAttrMap;
	public String toString(){

		StringBuffer toString=new StringBuffer();
		toString.append("**********Details for skuId ").append(skuId).append("\n")
		.append(" Creation Date of the sku ").append(creationDate).append("::")
		.append(" type of sku" ).append(getSkuType()).append("::")
		.append(" is sku a gift cert ?" ).append(isGiftCertsku()).append("::")
		.append(" Color of the sku"  ).append(getColor()).append("::")
		.append(" Color group of the sku"  ).append(getColorGroup()).append("::")
		.append(" size of the sku"  ).append(getSize()).append("::")
		.append(" is sku on sale "  ).append(isOnsale()).append("::")
		.append(" Email out of stock mail option available for sku? "  ).append(isEmailOutOfStock()).append("::")
		.append(" is sku on bopus excluded "  ).append(isBopusExclusion()).append("::")
		.append("vdc SkuMessage of the sku"  ).append(getVdcSkuMessage()).append("::")
		.append("*******JDA  DETAILS ").append("\n")
		.append(" JDA Department ID ").append(getJdaDept()).append("::")
		.append(" JDA Sub Department ID ").append(getJdaSubDept()).append("::")
		.append(" JDA Class ").append(jdaClass).append("\n")
		.append("*******IMAGE DETAILS ").append("\n")
		.append(" URL of small Image ").append(smallImageURL).append("::")	
		.append(" URL of medium Image ").append(mediumImageURL).append("::")		
		.append(" URL of large Image ").append(largeImageURL).append("::")		
		.append(" URL of thumbNail Image ").append(thumbNailImage).append("::")
		.append(" URL of Zoom Image ").append(zoomImageURL)	.append("::")
		.append(" URL of Zoom Index ").append(zoomImageURL).append("\n");

		if(siteSKUAttrMap!=null && !siteSKUAttrMap.isEmpty()){
			toString.append("Site specific data  for sku id "+skuId ).append("\n");
			Set<String> keySet=siteSKUAttrMap.keySet();
			for(String key:keySet){
				toString.append(" ************Sku Attributes  for site Id "+key).append("\n");
		
				toString.append(siteSKUAttrMap.get(key).toString()).append("\n");
			}
		}
		else{
			toString.append("Site specific data null for sku id "+skuId ).append("\n");
		}
		return toString.toString();
	}
	public boolean isOnsale() {
		return onsale;
	}
	public void setOnsale(boolean onsale) {
		this.onsale = onsale;
	}
	public String getCollegeId() {
		return collegeId;
	}
	public void setCollegeId(String collegeId) {
		this.collegeId = collegeId;
	}
	public boolean isEmailOutOfStock() {
		return emailOutOfStock;
	}
	public void setEmailOutOfStock(boolean emailOutOfStock) {
		this.emailOutOfStock = emailOutOfStock;
	}
	public boolean isGiftWrapEligible() {
		return giftWrapEligible;
	}
	public void setGiftWrapEligible(boolean giftWrapEligible) {
		this.giftWrapEligible = giftWrapEligible;
	}
	public boolean isBopusExclusion() {
		return bopusExclusion;
	}
	public void setBopusExclusion(boolean bopusExclusion) {
		this.bopusExclusion = bopusExclusion;
	}
	public String getSkuTaxStatus() {
		return skuTaxStatus;
	}
	public void setSkuTaxStatus(String skuTaxStatus) {
		this.skuTaxStatus = skuTaxStatus;
	}

	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		return skuId;
	}
	/**
	 * @param pSkuId the skuId to set
	 */
	public void setSkuId(String pSkuId) {
		skuId = pSkuId;
	}
	/**
	 * @return the creationDate
	 */
	public Timestamp getCreationDate() {
		return creationDate;
	}
	/**
	 * @param pCreationDate the creationDate to set
	 */
	public void setCreationDate(Timestamp pCreationDate) {
		creationDate = pCreationDate;
	}
	
	
	
	public Date getSkuEnableDate() {
		return skuEnableDate;
	}
	public void setSkuEnableDate(Date skuEnableDate) {
		this.skuEnableDate = skuEnableDate;
	}
	/**
	 * @return the skuType
	 */
	public String getSkuType() {
		return skuType;
	}
	/**
	 * @param pSkuType the skuType to set
	 */
	public void setSkuType(String pSkuType) {
		skuType = pSkuType;
	}
	/**
	 * @return the isGiftCertsku
	 */
	public boolean isGiftCertsku() {
		return isGiftCertsku;
	}
	/**
	 * @param pIsGiftCertsku the isGiftCertsku to set
	 */
	public void setGiftCertsku(boolean pIsGiftCertsku) {
		isGiftCertsku = pIsGiftCertsku;
	}
	/**
	 * @return the smallImageURL
	 */
	public String getSmallImageURL() {
		return smallImageURL;
	}
	/**
	 * @param pSmallImageURL the smallImageURL to set
	 */
	public void setSmallImageURL(String pSmallImageURL) {
		smallImageURL = pSmallImageURL;
	}
	/**
	 * @return the mediumImageURL
	 */
	public String getMediumImageURL() {
		return mediumImageURL;
	}
	/**
	 * @param pMediumImageURL the mediumImageURL to set
	 */
	public void setMediumImageURL(String pMediumImageURL) {
		mediumImageURL = pMediumImageURL;
	}
	/**
	 * @return the largeImageURL
	 */
	public String getLargeImageURL() {
		return largeImageURL;
	}
	/**
	 * @param pLargeImageURL the largeImageURL to set
	 */
	public void setLargeImageURL(String pLargeImageURL) {
		largeImageURL = pLargeImageURL;
	}
	/**
	 * @return the swatchImageURL
	 */
	public String getSwatchImageURL() {
		return swatchImageURL;
	}
	/**
	 * @param pSwatchImageURL the swatchImageURL to set
	 */
	public void setSwatchImageURL(String pSwatchImageURL) {
		swatchImageURL = pSwatchImageURL;
	}
	/**
	 * @return the thumbNailImage
	 */
	public String getThumbNailImage() {
		return thumbNailImage;
	}
	/**
	 * @param pThumbNailImage the thumbNailImage to set
	 */
	public void setThumbNailImage(String pThumbNailImage) {
		thumbNailImage = pThumbNailImage;
	}
	/**
	 * @return the zoomImageURL
	 */
	public String getZoomImageURL() {
		return zoomImageURL;
	}
	/**
	 * @param pZoomImageURL the zoomImageURL to set
	 */
	public void setZoomImageURL(String pZoomImageURL) {
		zoomImageURL = pZoomImageURL;
	}
	/**
	 * @return the zoomIndex
	 */
	public Integer getZoomIndex() {
		return zoomIndex;
	}
	/**
	 * @param pZoomIndex the zoomIndex to set
	 */
	public void setZoomIndex(Integer pZoomIndex) {
		zoomIndex = pZoomIndex;
	}
	/**
	 * @return the jdaDept
	 */
	public String getJdaDept() {
		return jdaDept;
	}
	/**
	 * @param pJdaDept the jdaDept to set
	 */
	public void setJdaDept(String pJdaDept) {
		jdaDept = pJdaDept;
	}
	/**
	 * @return the jdaSubDept
	 */
	public String getJdaSubDept() {
		return jdaSubDept;
	}
	/**
	 * @param pJdaSubDept the jdaSubDept to set
	 */
	public void setJdaSubDept(String pJdaSubDept) {
		jdaSubDept = pJdaSubDept;
	}
	/**
	 * @return the jdaClass
	 */
	public String getJdaClass() {
		return jdaClass;
	}
	/**
	 * @param pJdaClass the jdaClass to set
	 */
	public void setJdaClass(String pJdaClass) {
		jdaClass = pJdaClass;
	}
	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @param pColor the color to set
	 */
	public void setColor(String pColor) {
		color = pColor;
	}
	/**
	 * @return the colorGroup
	 */
	public String getColorGroup() {
		return colorGroup;
	}
	/**
	 * @param pColorGroup the colorGroup to set
	 */
	public void setColorGroup(String pColorGroup) {
		colorGroup = pColorGroup;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param pSize the size to set
	 */
	public void setSize(String pSize) {
		size = pSize;
	}
	/**
	 * @return the vdcSkuMessage
	 */
	public String getVdcSkuMessage() {
		return vdcSkuMessage;
	}
	/**
	 * @param pVdcSkuMessage the vdcSkuMessage to set
	 */
	public void setVdcSkuMessage(String pVdcSkuMessage) {
		vdcSkuMessage = pVdcSkuMessage;
	}
	/**
	 * @return the vdcSkuType
	 */
	public String getVdcSkuType() {
		return vdcSkuType;
	}
	/**
	 * @param pVdcSkuType the vdcSkuType to set
	 */
	public void setVdcSkuType(String pVdcSkuType) {
		vdcSkuType = pVdcSkuType;
	}
	/**
	 * @return the upc
	 */
	public String getUpc() {
		return upc;
	}
	/**
	 * @param pUpc the upc to set
	 */
	public void setUpc(String pUpc) {
		upc = pUpc;
	}
	/**
	 * @return the eComFulfillment
	 */
	public String geteComFulfillment() {
		return eComFulfillment;
	}
	/**
	 * @param pEComFulfillment the eComFulfillment to set
	 */
	public void seteComFulfillment(String pEComFulfillment) {
		eComFulfillment = pEComFulfillment;
	}
	/**
	 * @return the vendorId
	 */
	public String getVendorId() {
		return vendorId;
	}
	/**
	 * @param pVendorId the vendorId to set
	 */
	public void setVendorId(String pVendorId) {
		vendorId = pVendorId;
	}
	/**
	 * @return the prop65Flags
	 */
	public Prop65TypeVO getProp65Flags() {
		return prop65Flags;
	}
	/**
	 * @param pProp65Flags the prop65Flags to set
	 */
	public void setProp65Flags(Prop65TypeVO pProp65Flags) {
		prop65Flags = pProp65Flags;
	}
	/**
	 * @return the siteSKUAttrMap
	 */
	public Map<String, SiteSpecificSKUAttr> getSiteSKUAttrMap() {
		return siteSKUAttrMap;
	}
	/**
	 * @param pSiteSKUAttrMap the siteSKUAttrMap to set
	 */
	public void setSiteSKUAttrMap(Map<String, SiteSpecificSKUAttr> pSiteSKUAttrMap) {
		siteSKUAttrMap = pSiteSKUAttrMap;
	}
	/**
	 * @return the ltlFlag
	 */
	public boolean isLtlFlag() {
		return ltlFlag;
	}
	/**
	 * @param ltlFlag the ltlFlag to set
	 */
	public void setLtlFlag(boolean ltlFlag) {
		this.ltlFlag = ltlFlag;
	}




}
