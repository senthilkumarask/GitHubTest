package com.bbb.cms;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;

import java.util.List;

public class GSEmailVO{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String productId;
	
	private String skuId;
	
	private int quantity;
	
	private double priceRangeDescription;
	
	private double subtotal;
	
	private String reviewRating;
	
	private String registryID;
	
	private String tableName;
	
	private String productAvailabilityFlag;
	
	private SKUDetailVO skuDetailVO;
	
	private ProductVO productVo;
	
	private String upc;
	
	private String productLowPrice;
	
	private List siblingProducts;
	
	private List<String> goodToKnow; 
	
	private String productHighPrice;
	
	private String name;
	
	private String smallImage;
	
	private String thumbnailImage;
	
	private String categoryName;
	
	private String isPresent;
	
	private String longDescription;
	
	private String isActive;
	private String productLowDecimalPrice;
	private String productHighDecimalPrice;
	private String subTotalDecimalPrice;
	private String subTotalPrice;
	private String largeImage;
	private String productHighSalePrice;
	private String productHighDecimalSalePrice;
	private String productLowSalePrice;
	private String productLowDecimalSalePrice;
	private String webStockAvailable;
	
	/**
	 * @return this.the webStockAvailable
	 */
	public final String getWebStockAvailable() {
		return this.webStockAvailable;
	}

	/**
	 * @param webStockAvailable the webStockAvailable to set
	 */
	public final void setWebStockAvailable(final String webStockAvailable) {
		this.webStockAvailable = webStockAvailable;
	}

	/**
	 * @return this.the productLowDecimalPrice productLowDecimalPrice
	 */
	public final String getProductLowDecimalPrice() {
		return this.productLowDecimalPrice;
	}

	/**
	 * @param productLowDecimalPrice productLowDecimalPrice
	 */
	public final void setProductLowDecimalPrice(final String productLowDecimalPrice) {
		this.productLowDecimalPrice = productLowDecimalPrice;
	}

	/**
	 * @return this.productHighDecimalPrice
	 */
	public final String getProductHighDecimalPrice() {
		return this.productHighDecimalPrice;
	}

	/**
	 * @param productHighDecimalPrice productHighDecimalPrice
	 */
	public final void setProductHighDecimalPrice(final String productHighDecimalPrice) {
		this.productHighDecimalPrice = productHighDecimalPrice;
	}

	/**
	 * @return this.siblingProducts
	 */
	public final List getSiblingProducts() {
		return this.siblingProducts;
	}

	/**
	 * @param siblingProducts siblingProducts
	 */
	public final void setSiblingProducts(final List siblingProducts) {
		this.siblingProducts = siblingProducts;
	}

	/**
	 * @return this.productLowPrice
	 */
	public final String getProductLowPrice() {
		return this.productLowPrice;
	}

	/**
	 * @param productLowPrice productLowPrice
	 */
	public final void setProductLowPrice(final String productLowPrice) {
		this.productLowPrice = productLowPrice;
	}

	/**
	 * @return this.productHighPrice
	 */
	public final String getProductHighPrice() {
		return this.productHighPrice;
	}

	/**
	 * @param productHighPrice productHighPrice
	 */
	public final void setProductHighPrice(final String productHighPrice) {
		this.productHighPrice = productHighPrice;
	}

	/**
	 * @return this.upc
	 */
	public final String getUpc() {
		return this.upc;
	}

	/**
	 * @param upc upc
	 */
	public final void setUpc(final String upc) {
		this.upc = upc;
	}

	/**
	 * @return this.name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @param name name
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return this.smallImage
	 */
	public final String getSmallImage() {
		return this.smallImage;
	}

	/**
	 * @param smallImage smallImage
	 */
	public final void setSmallImage(final String smallImage) {
		this.smallImage = smallImage + "?$63$";
	}

	/**
	 * @return this.thumbnailImage
	 */
	public final String getThumbnailImage() {
		return this.thumbnailImage;
	}

	/**
	 * @param thumbnailImage thumbnailImage
	 */
	public final void setThumbnailImage(final String thumbnailImage) {
		this.thumbnailImage = thumbnailImage + "?$";
	}

	/**
	 * @return this.productId
	 */
	public final String getProductId() {
		return this.productId;
	}

	/**
	 * @param productId productId
	 */
	public final void setProductId(final String productId) {
		this.productId = productId;
	}

	/**
	 * @return this.skuId
	 */
	public final String getSkuId() {
		return this.skuId;
	}

	/**
	 * @param skuId skuId
	 */
	public final void setSkuId(final String skuId) {
		this.skuId = skuId;
	}


	/**
	 * @return this.reviewRating
	 */
	public final String getReviewRating() {
		return this.reviewRating;
	}

	/**
	 * @param reviewRating reviewRating
	 */
	public final void setReviewRating(final String reviewRating) {
		this.reviewRating = reviewRating;
	}

	/**
	 * @return this.registryID
	 */
	public final String getRegistryID() {
		return this.registryID;
	}

	/**
	 * @param registryID registryID
	 */
	public final void setRegistryID(final String registryID) {
		this.registryID = registryID;
	}

	/**
	 * @return this.tableName
	 */
	public final String getTableName() {
		return this.tableName;
	}

	/**
	 * @param tableName tableName
	 */
	public final void setTableName(final String tableName) {
		this.tableName = tableName;
	}


	/**
	 * @return this.productAvailabilityFlag
	 */
	public final String getProductAvailabilityFlag() {
		return this.productAvailabilityFlag;
	}

	/**
	 * @param productAvailabilityFlag productAvailabilityFlag
	 */
	public final void setProductAvailabilityFlag(final String productAvailabilityFlag) {
		this.productAvailabilityFlag = productAvailabilityFlag;
	}

	/**
	 * @return this.productVo
	 */
	public final ProductVO getProductVo() {
		return this.productVo;
	}

	/**
	 * @param productVo productVo
	 */
	public final void setProductVo(final ProductVO productVo) {
		this.productVo = productVo;
	}

	/**
	 * @return this.skuDetailVO
	 */
	public final SKUDetailVO getSkuDetailVO() {
		return this.skuDetailVO;
	}

	/**
	 * @param skuDetailVO skuDetailVO
	 */
	public final void setSkuDetailVO(final SKUDetailVO skuDetailVO) {
		this.skuDetailVO = skuDetailVO;
	}

	/**
	 * @return this.quantity
	 */
	public final int getQuantity() {
		return this.quantity;
	}

	/**
	 * @param quantity quantity
	 */
	public final void setQuantity(final int quantity) {
		this.quantity = quantity;
	}


	/**
	 * @return this.priceRangeDescription
	 */
	public final double getPriceRangeDescription() {
		return this.priceRangeDescription;
	}

	/**
	 * @param priceRangeDescription priceRangeDescription
	 */
	public final void setPriceRangeDescription(final double priceRangeDescription) {
		this.priceRangeDescription = priceRangeDescription;
	}

	/**
	 * @return this.subtotal
	 */
	public final double getSubtotal() {
		return this.subtotal;
	}

	/**
	 * @param subtotal subtotal
	 */
	public final void setSubtotal(final double subtotal) {
		this.subtotal = subtotal;
	}

	/**
	 * @return this.isPresent
	 */
	public final String getIsPresent() {
		return this.isPresent;
	}

	/**
	 * @return this.the categoryName
	 */
	public final String getCategoryName() {
		return this.categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public final void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @param isPresent isPresent
	 */
	public final void setIsPresent(final String isPresent) {
		this.isPresent = isPresent;
	}

	/**
	 * @return this.longDescription
	 */
	public final String getLongDescription() {
		return this.longDescription;
	}

	/**
	 * @param longDescription longDescription
	 */
	public final void setLongDescription(final String longDescription) {
		this.longDescription = longDescription;
	}


	/**
	 * @return this.the isActive
	 */
	public final String getIsActive() {
		return this.isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public final void setIsActive(final String isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return this.the subTotalDecimalPrice
	 */
	public final String getSubTotalDecimalPrice() {
		return this.subTotalDecimalPrice;
	}

	/**
	 * @param subTotalDecimalPrice the subTotalDecimalPrice to set
	 */
	public final void setSubTotalDecimalPrice(final String subTotalDecimalPrice) {
		this.subTotalDecimalPrice = subTotalDecimalPrice;
	}

	/**
	 * @return this.the subTotalPrice
	 */
	public final String getSubTotalPrice() {
		return this.subTotalPrice;
	}

	/**
	 * @param subTotalPrice the subTotalPrice to set
	 */
	public final void setSubTotalPrice(final String subTotalPrice) {
		this.subTotalPrice = subTotalPrice;
	}

	/**
	 * @return this.the largeImage
	 */
	public final String getLargeImage() {
		return this.largeImage;
	}

	/**
	 * @param largeImage the largeImage to set
	 */
	public final void setLargeImage(final String largeImage) {
		this.largeImage = largeImage + "?$478$";
	}

	/**
	 * @return this.the productHighSalePrice
	 */
	public final String getProductHighSalePrice() {
		return this.productHighSalePrice;
	}

	/**
	 * @param productHighSalePrice the productHighSalePrice to set
	 */
	public final void setProductHighSalePrice(final String productHighSalePrice) {
		this.productHighSalePrice = productHighSalePrice;
	}

	/**
	 * @return this.the productHighDecimalSalePrice
	 */
	public final String getProductHighDecimalSalePrice() {
		return this.productHighDecimalSalePrice;
	}

	/**
	 * @param productHighDecimalSalePrice the productHighDecimalSalePrice to set
	 */
	public final void setProductHighDecimalSalePrice(final String productHighDecimalSalePrice) {
		this.productHighDecimalSalePrice = productHighDecimalSalePrice;
	}

	/**
	 * @return this.the productLowSalePrice
	 */
	public final String getProductLowSalePrice() {
		return this.productLowSalePrice;
	}

	/**
	 * @param productLowSalePrice the productLowSalePrice to set
	 */
	public final void setProductLowSalePrice(final String productLowSalePrice) {
		this.productLowSalePrice = productLowSalePrice;
	}

	/**
	 * @return this.the productLowDecimalSalePrice
	 */
	public final String getProductLowDecimalSalePrice() {
		return this.productLowDecimalSalePrice;
	}

	/**
	 * @param productLowDecimalSalePrice the productLowDecimalSalePrice to set
	 */
	public final void setProductLowDecimalSalePrice(final String productLowDecimalSalePrice) {
		this.productLowDecimalSalePrice = productLowDecimalSalePrice;
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
