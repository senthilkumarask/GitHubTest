package com.bbb.store.catalog.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.rest.catalog.vo.ProductRestVO;

public class ProductGSVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProductRestVO productRestVO;
	private Map<String,String> productImageMap;
	private List<ProductGSVO> siblingProducts;
	private List<String> goodToKnow;
	private String productLowPrice;
	private String productHighPrice;
	private String seoUrl;
	private String primaryCategory;
	private String productLowSalePrice;
	private String productHighSalePrice;
	private List<GSMediaVO> otherMedia;
	
	/**
	 * @return
	 */
	public List<GSMediaVO> getOtherMedia() {
		return this.otherMedia;
	}

	/**
	 * @param otherMedia
	 */
	public void setOtherMedia(List<GSMediaVO> otherMedia) {
		this.otherMedia = otherMedia;
	}

	/**
	 * @return the primaryCategory
	 */
	public String getPrimaryCategory() {
		return this.primaryCategory;
	}

	/**
	 * @param primaryCategory the primaryCategory to set
	 */
	public void setPrimaryCategory(String primaryCategory) {
		this.primaryCategory = primaryCategory;
	}

	/**
	 * @return the productLowPrice
	 */
	public String getProductLowPrice() {
		return this.productLowPrice;
	}

	/**
	 * @param productLowPrice the productLowPrice to set
	 */
	public void setProductLowPrice(String productLowPrice) {
		this.productLowPrice = productLowPrice;
	}

	/**
	 * @return the productHighPrice
	 */
	public String getProductHighPrice() {
		return this.productHighPrice;
	}

	/**
	 * @param productHighPrice the productHighPrice to set
	 */
	public void setProductHighPrice(String productHighPrice) {
		this.productHighPrice = productHighPrice;
	}

	/**
	 * @return the siblingProducts
	 */
	public List<ProductGSVO> getSiblingProducts() {
		return this.siblingProducts;
	}

	/**
	 * @param siblingProducts the siblingProducts to set
	 */
	public void setSiblingProducts(List<ProductGSVO> siblingProducts) {
		this.siblingProducts = siblingProducts;
	}


	/**
	 * @return the productImageMap
	 */
	public Map<String, String> getProductImageMap() {
		return this.productImageMap;
	}

	/**
	 * @param productImageMap the productImageMap to set
	 */
	public void setProductImageMap(Map<String, String> productImageMap) {
		this.productImageMap = productImageMap;
	}

	/**
	 * @return the productRestVO
	 */
	public ProductRestVO getProductRestVO() {
		return this.productRestVO;
	}

	/**
	 * @param productRestVO the productRestVO to set
	 */
	public void setProductRestVO(ProductRestVO productRestVO) {
		this.productRestVO = productRestVO;
	}

	/**
	 * @return the seoUrl
	 */
	public String getSeoUrl() {
		return this.seoUrl;
	}

	/**
	 * @param seoUrl the seoUrl to set
	 */
	public void setSeoUrl(String seoUrl) {
		this.seoUrl = seoUrl;
	}

	/**
	 * @return the productLowSalePrice
	 */
	public String getProductLowSalePrice() {
		return this.productLowSalePrice;
	}

	/**
	 * @param productLowSalePrice the productLowSalePrice to set
	 */
	public void setProductLowSalePrice(String productLowSalePrice) {
		this.productLowSalePrice = productLowSalePrice;
	}

	/**
	 * @return the productHighSalePrice
	 */
	public String getProductHighSalePrice() {
		return this.productHighSalePrice;
	}

	/**
	 * @param productHighSalePrice the productHighSalePrice to set
	 */
	public void setProductHighSalePrice(String productHighSalePrice) {
		this.productHighSalePrice = productHighSalePrice;
	}

	/**
	 * @return
	 */
	public List<String> getGoodToKnow() {
		return this.goodToKnow;
	}

	/**
	 * @param goodToKnow
	 */
	public void setGoodToKnow(List<String> goodToKnow) {
		this.goodToKnow = goodToKnow;
	}
	
	
}
