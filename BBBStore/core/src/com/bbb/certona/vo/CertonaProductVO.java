package com.bbb.certona.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.ImageVO;

public class CertonaProductVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productId;
	private String productType;
	private ImageVO productImages;
	private BrandVO productBrand;
	private boolean collection=false;
	private List<CollectionChildProductAttr> childProductAttr;
	private String productKeyWrds;
	private Map<String,SiteSpecificProductAttr> sitePrdtAttrMap;
	private Timestamp creationDate;
	private Date enableDate;
	private boolean isGiftCert=false;
	private List<String> childSKUs;
	private boolean swatchFlag=false;
	private String productRollupType;
	private String collegeId;
	private boolean ltlFlag=false;
	private String productUrl;
	

	public String toString(){
		StringBuffer toString=new StringBuffer();
		toString.append("**********Details for product ").append(productId).append("\n")
		.append(" Product Type ").append(productType).append("\n")
		.append(" Is product a collection ").append(collection).append("\n")
		.append(" productKeyWrds ").append(productKeyWrds ).append("\n")
		.append(" productRollupType ").append(productRollupType ).append("\n")
		.append("Site Specific properties of Product Id ").append(productId).append("\n");
		if(sitePrdtAttrMap!=null && !sitePrdtAttrMap.isEmpty()){
			Set<String> keySet=sitePrdtAttrMap.keySet();
			for(String key:keySet){
				toString.append("Site specific Details for site ").append(key).append("\n")
				.append(sitePrdtAttrMap.get(key).toString());
			}
		}
		else{
			toString.append("Site specific Details for product is null or empty ").append(productId).append("\n");
		}
		if(this.childSKUs!=null  && !childSKUs.isEmpty()){
			toString.append("List of Child Sku available for productId ").append(this.productId).append("\n");
			for(String skuId:childSKUs){
				toString.append(skuId).append(" :: ");
			}
		}
		else{
			toString.append(" No Child Sku available for ").append(this.productId).append("\n");
		}
		if(collection && childProductAttr!=null && !childProductAttr.isEmpty()){
			for(CollectionChildProductAttr collChildPrdt:childProductAttr){
			toString.append(collChildPrdt.toString()).append("\n");
			}
		}
		return toString.toString();

	}
	/**
	 * @return the childProductAttr
	 */
	public List<CollectionChildProductAttr> getChildProductAttr() {
		return childProductAttr;
	}
	/**
	 * @param childProductAttr the childProductAttr to set
	 */
	public void setChildProductAttr(final 
			List<CollectionChildProductAttr> childProductAttr) {
		this.childProductAttr = childProductAttr;
	}
	/**
	 * @return the productKeyWrds
	 */
	public String getProductKeyWrds() {
		return productKeyWrds;
	}
	/**
	 * @param productKeyWrds the productKeyWrds to set
	 */
	public void setProductKeyWrds(final String productKeyWrds) {
		this.productKeyWrds = productKeyWrds;
	}



	/**
	 * @return the sitePrdtAttrMap
	 */
	public Map<String, SiteSpecificProductAttr> getSitePrdtAttrMap() {
		return sitePrdtAttrMap;
	}
	/**
	 * @param sitePrdtAttrMap the sitePrdtAttrMap to set
	 */
	public void setSitePrdtAttrMap(final 
			Map<String, SiteSpecificProductAttr> sitePrdtAttrMap) {
		this.sitePrdtAttrMap = sitePrdtAttrMap;
	}
	/**
	 * @return the creationDate
	 */
	public Timestamp getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(final Timestamp creationDate) {
		this.creationDate = creationDate;
	}
	
	

	public Date getEnableDate() {
		return enableDate;
	}
	public void setEnableDate(Date enableDate) {
		this.enableDate = enableDate;
	}
	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(final String productId) {
		this.productId = productId;
	}
	/**
	 * @return the productType
	 */
	public String getProductType() {
		return productType;
	}
	/**
	 * @param productType the productType to set
	 */
	public void setProductType(final String productType) {
		this.productType = productType;
	}
	/**
	 * @return the productImages
	 */
	public ImageVO getProductImages() {
		return productImages;
	}
	/**
	 * @param productImages the productImages to set
	 */
	public void setProductImages(final ImageVO productImages) {
		this.productImages = productImages;
	}
	/**
	 * @return the productBrand
	 */
	public BrandVO getProductBrand() {
		return productBrand;
	}
	/**
	 * @param productBrand the productBrand to set
	 */
	public void setProductBrand(final BrandVO productBrand) {
		this.productBrand = productBrand;
	}
	/**
	 * @return the collection
	 */
	public boolean isCollection() {
		return collection;
	}
	/**
	 * @param collection the collection to set
	 */
	public void setCollection(final boolean collection) {
		this.collection = collection;
	}
	/**
	 * @return the isGiftCert
	 */
	public boolean getIsGiftCert() {
		return isGiftCert;
	}
	/**
	 * @param isGiftCert the isGiftCert to set
	 */
	public void setGiftCert(final boolean isGiftCert) {
		this.isGiftCert = isGiftCert;
	}
	/**
	 * @return the childSKUs
	 */
	public List<String> getChildSKUs() {
		return childSKUs;
	}
	/**
	 * @param childSKUs the childSKUs to set
	 */
	public void setChildSKUs(final List<String> childSKUs) {
		this.childSKUs = childSKUs;
	}
	/**
	 * @return the swatchFlag
	 */
	public boolean isSwatchFlag() {
		return swatchFlag;
	}
	/**
	 * @param swatchFlag the swatchFlag to set
	 */
	public void setSwatchFlag(final boolean swatchFlag) {
		this.swatchFlag = swatchFlag;
	}
	/**
	 * @return the productRollupType
	 */
	public String getProductRollupType() {
		return productRollupType;
	}
	/**
	 * @param productRollupType the productRollupType to set
	 */
	public void setProductRollupType(final String productRollupType) {
		this.productRollupType = productRollupType;
	}
	/**
	 * @return the collegeId
	 */
	public String getCollegeId() {
		return collegeId;
	}
	/**
	 * @param collegeId the collegeId to set
	 */
	public void setCollegeId(final String collegeId) {
		this.collegeId = collegeId;
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
	/**
	 * @return the productUrl
	 */
	public final String getProductUrl() {
		return this.productUrl;
	}
	/**
	 * @param productUrl the productUrl to set
	 */
	public final void setProductUrl(final String productUrl) {
		this.productUrl = productUrl;
	}
	
}
