package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.Comparator;
import com.bbb.search.bean.result.SortOptionVO;

/**
 * 
 * @author njai13
 *
 */
public class BrandVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String brandId;
	private String brandName;
	private String brandImage;
	private String brandImageAltText;
	private boolean displayFlag;
	private String brandDesc;
	private String featuredBrandURL;
	private String seoURL;
	private SortOptionVO sortOptionVO;
	private String brandContent;
	private String jsFilePath;
	private String cssFilePath;
	

	/**
	 * @return the seoURL
	 */
	public String getSeoURL() {
		return seoURL;
	}

	/**
	 * @param seoURL the seoURL to set
	 */
	public void setSeoURL(String seoURL) {
		this.seoURL = seoURL;
	}

	public BrandVO() {
		// TODO Auto-generated constructor stub
	}

	public BrandVO(	 String brandId,
			String brandName,
			String brandImage,
			String brandImageAltText) {
		this.brandId = brandId;
		this.brandName = brandName;
		this.brandImage = brandImage;
		this.brandImageAltText = brandImageAltText;
	}

	/**
	 * @return the brandId
	 */
	public String getBrandId() {
		return 	this.brandId;
	}

	/**
	 * @param brandId the brandId to set
	 */
	public void setBrandId(final String brandId) {
		this.brandId = brandId;
	}

	/**
	 * @return the brandName
	 */
	public String getBrandName() {

		return this.brandName;
	}

	/**
	 * @param brandName the brandName to set
	 */
	public void setBrandName(final String brandName) {
		this.brandName = brandName;
	}

	/**
	 * @return the brandImage
	 */
	public String getBrandImage() {
		return this.brandImage;
	}

	/**
	 * @param brandImage the brandImage to set
	 */
	public void setBrandImage(final String brandImage) {
		this.brandImage = brandImage;
	}

	/**
	 * @return the brandImageAltText
	 */
	public String getBrandImageAltText() {

		return this.brandImageAltText;

	}

	/**
	 * @param brandImageAltText the brandImageAltText to set
	 */
	public void setBrandImageAltText(final String brandImageAltText) {
		this.brandImageAltText = brandImageAltText;
	}

	/**
	 * @return the displayFlag
	 */
	public boolean isDisplayFlag() {
		return displayFlag;
	}

	/**
	 * @param displayFlag the displayFlag to set
	 */
	public void setDisplayFlag(boolean displayFlag) {
		this.displayFlag = displayFlag;
	}

	/**
	 * @return the brandDesc
	 */
	public String getBrandDesc() {
		return brandDesc;
	}

	/**
	 * @param brandDesc the brandDesc to set
	 */
	public void setBrandDesc(String brandDesc) {
		this.brandDesc = brandDesc;
	}

	public String getFeaturedBrandURL() {
		return featuredBrandURL;
	}

	public void setFeaturedBrandURL(String featuredBrandURL) {
		this.featuredBrandURL = featuredBrandURL;
	}

	public String toString(){
		StringBuffer toString=new StringBuffer(" Brand VO Details \n ");
		toString.append("Brand Id ").append(this.getBrandId()!=null?this.getBrandId():" NULL " ).append("\n")
		.append("Brand Name ").append(this.getBrandName()!=null?this.getBrandName():" NULL " ).append("\n")
		.append("Brand Image ").append(this.getBrandImage()!=null?this.getBrandImage():" NULL " ).append("\n")
		.append("Brand Image Alt Text ").append(this.brandImageAltText!=null?this.brandImageAltText:" NULL " ).append("\n")
		.append("Brand Display Flag ").append(this.displayFlag ).append("\n")
		.append("Brand Description ").append(this.getBrandDesc()!=null?this.getBrandDesc():" NULL " ).append("\n")
		.append("Brand Featured Brands URL ").append(this.getFeaturedBrandURL()!=null?this.getFeaturedBrandURL():" NULL " ).append("\n");
		return toString.toString();
	}

	/**
	 * @return the cssFilePath
	 */
	public String getCssFilePath() {
		return cssFilePath;
	}

	/**
	 * @param cssFilePath the cssFilePath to set
	 */
	public void setCssFilePath(String cssFilePath) {
		this.cssFilePath = cssFilePath;
	}

	/**
	 * @return the jsFilePath
	 */
	public String getJsFilePath() {
		return jsFilePath;
	}

	/**
	 * @param jsFilePath the jsFilePath to set
	 */
	public void setJsFilePath(String jsFilePath) {
		this.jsFilePath = jsFilePath;
	}

	/**
	 * @return the brandContent
	 */
	public String getBrandContent() {
		return brandContent;
	}

	/**
	 * @param brandContent the brandContent to set
	 */
	public void setBrandContent(String brandContent) {
		this.brandContent = brandContent;
	}

	/**
	 * @return the sortOptionVO
	 */
	public SortOptionVO getSortOptionVO() {
		return sortOptionVO;
	}

	/**
	 * @param sortOptionVO the sortOptionVO to set
	 */
	public void setSortOptionVO(SortOptionVO sortOptionVO) {
		this.sortOptionVO = sortOptionVO;
	}
	
	
	/*Comparator for sorting the list by Brand Name*/
    public static Comparator<BrandVO> BrandNameComparator = new Comparator<BrandVO>() {

	public int compare(BrandVO brand1, BrandVO brand2) {
	   String brandName1 = brand1.getBrandName().toUpperCase();
	   String brandName2 = brand2.getBrandName().toUpperCase();

	   //ascending order
	   return brandName1.compareTo(brandName2);

	   //descending order
	   //return brandName2.compareTo(brandName1);
    }};
}
