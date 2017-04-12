package com.bbb.rest.catalog.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.ImageVO;

public class ProductPLPVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String productName;
	private String productShortDescription;
	private String productLongDescription;
	private String productPriceRangeDescription;
	private String productSalePriceRangeDescription;
	private Map<String, List<AttributeVO>> productAttributesList; 
	private String productSkuLowPrice;
	private String productSkuHighPrice;
	private ImageVO productImages;
	private BazaarVoiceProductVO productBVReviews;
	
	private CatalogItemAttributesVO productAllAttributesVO;
	
    public CatalogItemAttributesVO getProductAllAttributesVO() {
		return productAllAttributesVO;
	}

	public void setProductAllAttributesVO(CatalogItemAttributesVO allAttributeVOs) {
		this.productAllAttributesVO = allAttributeVOs;
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
	public void setProductImages(ImageVO productImages) {
		this.productImages = productImages;
	}

	/**
	 * @return the productSalePriceRangeDescription
	 */
	public String getProductSalePriceRangeDescription() {
		return productSalePriceRangeDescription;
	}

	/**
	 * @param productSalePriceRangeDescription the productSalePriceRangeDescription to set
	 */
	public void setProductSalePriceRangeDescription(
			String productSalePriceRangeDescription) {
		this.productSalePriceRangeDescription = productSalePriceRangeDescription;
	}

	/**
	 * @return the productAttributesList
	 */
	public Map<String, List<AttributeVO>> getProductAttributesList() {
		return productAttributesList;
	}

	/**
	 * @param productAttributesList the productAttributesList to set
	 */
	public void setProductAttributesList(
			Map<String, List<AttributeVO>> productAttributesList) {
		this.productAttributesList = productAttributesList;
	}

	/**
	 * @return the productBVReviews
	 */
	public BazaarVoiceProductVO getProductBVReviews() {
		return productBVReviews;
	}

	/**
	 * @param productBVReviews the productBVReviews to set
	 */
	public void setProductBVReviews(BazaarVoiceProductVO productBVReviews) {
		this.productBVReviews = productBVReviews;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductShortDescription() {
		return productShortDescription;
	}

	public void setProductShortDescription(String productShortDescription) {
		this.productShortDescription = productShortDescription;
	}

	public String getProductLongDescription() {
		return productLongDescription;
	}

	public void setProductLongDescription(String productLongDescription) {
		this.productLongDescription = productLongDescription;
	}

	public String getProductPriceRangeDescription() {
		return productPriceRangeDescription;
	}

	public void setProductPriceRangeDescription(String productPriceRangeDescription) {
		this.productPriceRangeDescription = productPriceRangeDescription;
	}

	public String getProductSkuLowPrice() {
		return productSkuLowPrice;
	}

	public void setProductSkuLowPrice(String productSkuLowPrice) {
		this.productSkuLowPrice = productSkuLowPrice;
	}

	public String getProductSkuHighPrice() {
		return productSkuHighPrice;
	}

	public void setProductSkuHighPrice(String productSkuHighPrice) {
		this.productSkuHighPrice = productSkuHighPrice;
	}

	public String getId() {
		return id;
	}

	public void setId(String productId) {
		this.id = productId;
	}
	
}
